package cn.fmsoft.lnx.gmud.simple;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import cn.fmsoft.lnx.gmud.simple.ColorPickerDialog.OnColorChangedListener;
import cn.fmsoft.lnx.gmud.simple.core.Gmud;
import cn.fmsoft.lnx.gmud.simple.core.Input;

/**
 * 管理各部件位置区域及显示，响应按键点击
 * 
 * @author nxliao
 * 
 */
public final class Configure {
	final static boolean DEBUG = false;
	final static String DBG_TAG = Configure.class.getName();

	public static final int KEY_Exit = 0;
	public static final int KEY_Ent = 1;
	public static final int KEY_Down = 2;
	public static final int KEY_Left = 3;
	public static final int KEY_Up = 4;
	public static final int KEY_Right = 5;
	public static final int KEY_PgUp = 6;
	public static final int KEY_PgDn = 7;
	public static final int KEY_Fly = 8;
	public static final int KEY_HELP = 9;
	public static final int _KEY_MAX_ = 9;

	public static final int _KEY_MASK_CLEAR_ = -1 << _KEY_MAX_;
	public static final int _KEY_MASK_ = ~_KEY_MASK_CLEAR_;

	/** 视频输出区域 */
	protected static final Rect sRcVideo = new Rect();
	/** 各按键区 */
	protected static final Rect sRcKeys[] = new Rect[_KEY_MAX_ + 1];
	/** 剪切区，每次绘制前重新获取 */
	private static final Rect sＴmpRcClip = new Rect();
	/** 按下状态 */
	private static int sPressMask = 0;
	/** 按下或弹起的变化 */
	private static int sPressDirty = 0;
	private static Drawable BG_NORMAL;
	private static Drawable BG_FOCUS;
	private static Paint TITLE_PAINT;
	private static String TITLE[];
	public static float CUR_DENSITY = 1.0f;

	private static ConfigInfo sDefConfigLand, sDefConfigPort;
	private static ConfigInfo sCurConfigInfo;
	private static int sWidth, sHeight;
	private static Rect sBound = new Rect();
	/** 背景色 */
	private static int sBackground;
	private static boolean sIsLandscape;

	private static Context sContext;

	/** 初始化 */
	protected static void init(Context ctx) {
		sContext = ctx;

		Resources res = ctx.getResources();
		final DisplayMetrics dm = res.getDisplayMetrics();
		CUR_DENSITY = dm.density;

		BG_NORMAL = res.getDrawable(R.drawable.bg_normal);
		BG_FOCUS = res.getDrawable(R.drawable.bg_focus);
		TITLE = res.getStringArray(R.array.soft_key);

		TITLE_PAINT = new Paint();
		TITLE_PAINT.setAntiAlias(true);
		TITLE_PAINT.setFilterBitmap(true);
		TITLE_PAINT.setFakeBoldText(true);
		TITLE_PAINT.setTextSize(ConfigInfo.DEF_BT_TITLE_SIZE * CUR_DENSITY);
		TITLE_PAINT.setTextAlign(Paint.Align.LEFT);

		for (int i = 0, c = _KEY_MAX_; i < c; i++) {
			sRcKeys[i] = new Rect();
		}
		sRcKeys[_KEY_MAX_] = sRcVideo;
	}

	/** 绘制单个软键 */
	private static void _draw_key(Canvas canvas, int id, Rect rc) {
		final int flag = (1 << id);
		final Drawable d;
		if ((sPressMask & flag) == 0) {
			// no press
			d = BG_NORMAL;
			TITLE_PAINT.setColor(Color.GREEN);
		} else {
			d = BG_FOCUS;
			TITLE_PAINT.setColor(Color.YELLOW);
		}
		d.setBounds(rc);
		d.draw(canvas);

		String title = TITLE[id];
		Rect bounds = new Rect();
		TITLE_PAINT.getTextBounds(title, 0, title.length(), bounds);
		canvas.drawText(TITLE[id], rc.centerX() - bounds.centerX(),
				rc.centerY() - bounds.centerY(), TITLE_PAINT);

		if ((sPressDirty & flag) != 0)
			sPressDirty &= ~(flag);
	}

	private static String getConfigName(boolean isLandscape) {
		return isLandscape ? "land" : "port";
	}

	/**
	 * 加载自定义配置
	 * 
	 * @param isLandscape
	 *            是否横屏
	 * @param defInfo
	 *            默认配置
	 * @return
	 */
	private static ConfigInfo tryLoadConfig(boolean isLandscape,
			ConfigInfo defInfo) {
		SharedPreferences sp = sContext.getSharedPreferences("design",
				Context.MODE_PRIVATE);
		String design = sp.getString(getConfigName(isLandscape), null);
		if (design != null) {
			try {
				JSONObject joRoot = new JSONObject(design);
				return ConfigInfo.unflattenFromJSON(joRoot, defInfo);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return new ConfigInfo(defInfo);
	}

	/**
	 * 储存自定义配置
	 * 
	 * @param isLandscape
	 * @param info
	 * @return
	 */
	private static boolean trySaveConfig(boolean isLandscape, ConfigInfo info) {
		SharedPreferences sp = sContext.getSharedPreferences("design",
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		JSONObject joRoot = new JSONObject();
		try {
			ConfigInfo.flattenToJSON(info, joRoot);
			editor.putString(getConfigName(isLandscape), joRoot.toString());
			return editor.commit();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 应用配置
	 * 
	 * @param info
	 */
	private static void applyConfig(ConfigInfo info) {
		sCurConfigInfo = new ConfigInfo(info);
		for (int i = 0, c = _KEY_MAX_ + 1; i < c; i++) {
			sRcKeys[i].set(info.mRcKeys[i]);
		}
		sBackground = info.mBackgroundColor;
	}

	/** 将当前使用的配置，转存到 ConfigInfo 结构中 */
	private static void dumpConfig(ConfigInfo info) {
		for (int i = 0, c = _KEY_MAX_ + 1; i < c; i++) {
			info.mRcKeys[i].set(sRcKeys[i]);
		}
		info.mBackgroundColor = sBackground;
	}

	/**
	 * 构造默认配置
	 * 
	 * @param isLandscape
	 * @param w
	 * @param h
	 * @return
	 */
	private static ConfigInfo generateDefault(boolean isLandscape, int w, int h) {
		ConfigInfo defInfo = isLandscape ? sDefConfigLand : sDefConfigPort;
		if (defInfo == null || !defInfo.match(w, h)) {
			if (isLandscape) {
				defInfo = ConfigInfo.generateDefaultLand(w, h);
				sDefConfigLand = defInfo;
			} else {
				defInfo = ConfigInfo.generateDefaultPort(w, h);
				sDefConfigPort = defInfo;
			}
		}
		return defInfo;
	}

	/**
	 * 根据总区域大小重置各区域
	 * 
	 * @param isLandscape
	 *            是否横屏
	 * @param w
	 *            宽
	 * @param h
	 *            高
	 */
	protected static void reset(boolean isLandscape, int w, int h) {
		ConfigInfo defInfo = generateDefault(isLandscape, w, h);

		// try load ...
		final ConfigInfo info = tryLoadConfig(isLandscape, defInfo);

		applyConfig(info == null ? defInfo : info);
		sIsLandscape = isLandscape;
		sWidth = w;
		sHeight = h;
		sBound.set(0, 0, w, h);

		// 重置缩放比例
		Gmud.ResetVideoLayout(sRcVideo);
	}

	/** 检查有无必要重绘 */
	protected static boolean updateInvalidate() {
		return false;
	}

	/** 合并（普通）“脏”区域 */
	protected static void UnionInvalidateRect(Rect bound) {
		for (int i = 0, c = _KEY_MAX_; i < c; i++) {
			if ((sPressDirty & (1 << i)) != 0)
				bound.union(sRcKeys[i]);
		}
	}

	protected static void Draw(Canvas canvas, Bitmap video, int tick) {
		drawBackground(canvas);
		drawKeypad(canvas);
		drawVideo(canvas, video);
	}

	/** 绘制软键 */
	private static void drawKeypad(Canvas canvas) {
		/* 剪切区 */
		final Rect clip = sＴmpRcClip;
		canvas.getClipBounds(clip);
		final Rect rcKeys[] = sRcKeys;
		for (int i = 0, c = _KEY_MAX_; i < c; i++) {
			final Rect rc = rcKeys[i];
			if (Rect.intersects(clip, rc))
				_draw_key(canvas, i, rc);
		}
	}

	/** 绘制游戏区 */
	private static void drawVideo(Canvas canvas, Bitmap video) {
		canvas.drawBitmap(video, null, sRcVideo, TITLE_PAINT);
	}

	private static void drawBackground(Canvas canvas) {
		canvas.drawColor(sBackground);
	}

	/** 响应单击按钮，如无变化返回false，否则返回true */
	protected static boolean HitTest(MotionEvent event) {
		final int action = event.getAction();
		final int count = event.getPointerCount();

		if (DEBUG)
			Log.d(DBG_TAG, "action=" + action);

		final int flag;
		switch ((action & MotionEvent.ACTION_MASK)) {
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL: {
			flag = 0;
		}
			break;

		default: {
			int tmp = 0;
			for (int i = 0, c = count; i < c; i++) {
				final int x = (int) event.getX(i);
				final int y = (int) event.getY(i);
				tmp |= Configure.HitTestFlag(x, y);
			}
			flag = tmp;
		}
			break;
		}

		if (flag == sPressMask)
			return false;
		sPressDirty = (sPressMask ^ flag);
		sPressMask = (sPressMask & _KEY_MASK_CLEAR_) | flag;
		Input.GmudSetKey(sPressMask);
		return true;
	}

	/** 返回位置所在的所有元件掩码 */
	protected static int HitTestFlag(int x, int y) {
		int flag = 0;
		for (int i = 0, c = _KEY_MAX_ + 1; i < c; i++) {
			if (sRcKeys[i].contains(x, y)) {
				flag |= 1 << i;
			}
		}
		return flag;
	}

	/** 返回位置所在的最顶层元素ID */
	protected static int HitTestId(int x, int y) {
		for (int i = _KEY_MAX_; i >= 0; i--) {
			if (sRcKeys[i].contains(x, y))
				return i;
		}
		return -1;
	}

	protected static void onKeyDown(int flag) {
		sPressMask |= flag;
		Input.GmudSetKey(sPressMask);
	}

	protected static void onKeyUp(int flag) {
		if (flag == 0)
			sPressMask = 0;
		else
			sPressMask &= ~(flag);
		Input.GmudSetKey(sPressMask);
	}

	protected static void onKeySet(int flag) {
		sPressMask = flag;
		Input.GmudSetKey(sPressMask);
	}

	protected static void startDesign(Design design) {
		final ConfigInfo info = sCurConfigInfo;
		design.SetConfigInfo(info);
	}

	protected static void applyDesign(Design design) {
		final ConfigInfo info = sCurConfigInfo;
		dumpConfig(info);
		if (DEBUG) {
			Log.d(DBG_TAG, " apply design, save configure");
		}
		trySaveConfig(sIsLandscape, info);
	}

	protected static void cancelDesign(Design design) {
		final ConfigInfo info = design.GetConfigInfo();
		applyConfig(info);
	}

	protected static void resetDesign() {
		final ConfigInfo info = generateDefault(sIsLandscape, sWidth, sHeight);
		applyConfig(info);
	}

	/**
	 * 自定义界面，直接修改了 Configure 的变量
	 * 
	 * @author nxliao
	 * @version 0.1.20130219.1454
	 */
	public static class Design extends GestureDetector.SimpleOnGestureListener
			implements OnColorChangedListener {

		public static final int DESIGN_START = 0;
		public static final int DESIGN_APPLY = 1;
		public static final int DESIGN_CANCEL = 2;
		public static final int DESIGN_RESET = 3;

		private float mCurX;
		private float mCurY;
		private Show mShow;

		/** 当前元素ID */
		private int mHitId = -1;

		private boolean mRedraw = false;
		private boolean mDirty = false;

		/** 目标位置（即 ACTION_UP 后元素应放下的位置） */
		private Rect mRcTarget;

		ConfigInfo mConfigInfo;

		/** 吸附状态 */
		private int mAdsorb = 0, mAdsorbBak = 0;
		private float mAX, mAY;
		private static final int DEF_ADSORB_H = 24;
		private static final int DEF_ADSORB_V = 24;
		private final int FLAG_ADSORB_LEFT = 1 << 0;
		private final int FLAG_ADSORB_TOP = 1 << 1;
		private final int FLAG_ADSORB_RIGHT = 1 << 2;
		private final int FLAG_ADSORB_BOTTOM = 1 << 3;

		public Design(Show show) {
			mShow = show;
		}

		public void SetConfigInfo(ConfigInfo info) {
			mConfigInfo = info;
		}

		public ConfigInfo GetConfigInfo() {
			return mConfigInfo;
		}

		protected boolean updateInvalidate() {
			return false;
		}

		/**
		 * 检查水平方向上的吸附，会修改 {@link #mCurX}、 {@link #mAX}、 {@link #mAdsorb} 、
		 * {@link #mAdsorbBak}
		 * 
		 * @param x
		 * @param flag
		 * @return 是否做了吸附处理
		 */
		private boolean checkAdsorbH(int x, int flag) {
			final int t = (int) (x + mCurX + mAX);
			boolean adsorb = (t > -DEF_ADSORB_H && t < DEF_ADSORB_H);
			if ((mAdsorb & flag) != 0) {
				if (adsorb) {
					mAX += mCurX;
					mCurX = 0;
				} else {
					mCurX += mAX;
					mAX = 0;
					mAdsorb &= ~(flag);
				}
			} else {
				if (adsorb && ((mAdsorbBak & flag) == 0)) {
					mAdsorbBak |= flag;
					mAdsorb |= flag;
					mAX += mCurX + x;
					mCurX = -x;
				} else {
					// nothing
					return false;
				}
			}
			return true;
		}

		/**
		 * 检查垂直方向上的吸附，会修改 {@link #mCurY}、 {@link #mAY}、 {@link #mAdsorb} 、
		 * {@link #mAdsorbBak}
		 * 
		 * @param y
		 * @param flag
		 * @return 是否做了吸附处理
		 */
		private boolean checkAdsorbV(int y, int flag) {
			final int t = (int) (y + mCurY + mAY);
			boolean adsorb = (t > -DEF_ADSORB_V && t < DEF_ADSORB_V);
			if ((mAdsorb & flag) != 0) {
				if (adsorb) {
					mAY += mCurY;
					mCurY = 0;
				} else {
					mCurY += mAY;
					mAY = 0;
					mAdsorb &= ~(flag);
				}
			} else {
				if (adsorb && (mAdsorbBak & flag) == 0) {
					mAdsorbBak |= flag;
					mAdsorb |= flag;
					mAY += mCurY + y;
					mCurY = -y;
				} else {
					// nothing
					return false;
				}
			}
			return true;
		}

		protected void UnionInvalidateRect(Rect bound) {
			if (mRedraw) {
				bound.union(sBound);
				mRedraw = false;
			} else if (mHitId >= 0 && mDirty) {
				final Rect rc = mRcTarget;
				bound.union(rc);

				// 检查边界吸附
				if (!checkAdsorbH(rc.left - sBound.left, FLAG_ADSORB_LEFT)) {
					checkAdsorbH(rc.right - sBound.right, FLAG_ADSORB_RIGHT);
				}
				if (!checkAdsorbV(rc.top - sBound.top, FLAG_ADSORB_TOP)) {
					checkAdsorbV(rc.bottom - sBound.bottom, FLAG_ADSORB_BOTTOM);
				}
				rc.offset((int) mCurX, (int) mCurY);

				bound.union(rc);
				mCurX = 0;
				mCurY = 0;
				mDirty = false;
			}
		}

		protected void Draw(Canvas canvas, Bitmap video, int tick) {
			drawBackground(canvas);
			drawKeypad(canvas);
			drawVideo(canvas, video);
		}

		/** 返回位置所在的最顶层元素ID */
		private int _hitTestId(int x, int y) {
			final Rect[] rc = sRcKeys;
			for (int i = Configure._KEY_MAX_; i >= 0; i--) {
				if (rc[i].contains(x, y))
					return i;
			}
			return -1;
		}

		private static void resetRectSize(Rect rc, int w, int h) {
			final int x = rc.left + rc.right;
			final int y = rc.top + rc.bottom;
			rc.set((x - w) >> 1, (y - h) >> 1, (x + w) >> 1, (y + h) >> 1);
		}

		private void _tryScale(int id) {
			if (id < 0) {
				// 空白区
				new ColorPickerDialog(mShow.getContext(), this, sBackground)
						.show();
			} else if (id < _KEY_MAX_) {
				// 按钮
				final int BW = (int) (CUR_DENSITY * ConfigInfo.DEF_BT_WIDTH);
				final int BH = (int) (CUR_DENSITY * ConfigInfo.DEF_BT_HEIGHT);
				int w = sRcKeys[id].width();
				float scale = (float) w / BW;
				if (scale < 1.5f) {
					scale += 0.1f;
				} else {
					scale = 0.7f;
				}
				int nw = (int) (BW * scale);
				int nh = (int) (BH * scale);
				for (int i = 0, c = _KEY_MAX_; i < c; i++) {
					resetRectSize(sRcKeys[i], nw, nh);
				}
				mRedraw = true;
			} else if (id == _KEY_MAX_) {
				// 游戏区按标准大小的整数倍增加
				final int BW = Gmud.WQX_ORG_WIDTH, BH = Gmud.WQX_ORG_HEIGHT;
				final int MW = sWidth, MH = sHeight;
				int w = sRcVideo.width(), h = sRcVideo.height();
				int nw, nh;
				if (w >= MW || h >= MH) {
					nw = BW;
					nh = BH;
				} else {
					if (w * BH > h * BW) {
						nw = BW + BW * h / BH;
						nh = BH + h;
					} else {
						nh = BH + BH * w / BW;
						nw = BW + w;
					}
					if (nw > MW || nh > MH) {
						if (nw * MH > nh * MW) {
							nh = nh * MW / nw;
							nw = MW;
						} else {
							nw = nw * MH / nh;
							nh = MH;
						}
					}
				}
				resetRectSize(sRcVideo, nw, nh);
				if (!Rect.intersects(sBound, sRcVideo)) {
					// 如果不可见，则需要调整回来
					sRcVideo.offsetTo(sBound.left, sBound.top);
				}
				mRedraw = true;
			}
		}

		//
		// ////////////////////////////////////////////////////////////////////////
		//
		// - 手势事件 -
		//
		//

		// 用户（轻触触摸屏后）松开，由一个1个MotionEvent ACTION_UP触发
		public boolean onSingleTapUp(MotionEvent e) {
			if (DEBUG)
				Log.d(DBG_TAG, "onSingleTapUp");
			return true;
		}

		// 用户长按触摸屏，由多个MotionEvent ACTION_DOWN触发
		public void onLongPress(MotionEvent e) {
			if (DEBUG)
				Log.d(DBG_TAG, "onLongPress");
		}

		// 用户按下触摸屏，并拖动，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE触发
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			if (DEBUG)
				Log.d(DBG_TAG, "onScroll dx=" + distanceX + " dy=" + distanceY
						+ " ox=" + e2.getX() + " oy=" + e2.getY());
			if (mHitId >= 0) {
				mCurX -= distanceX;
				mCurY -= distanceY;
				mDirty = true;
			}
			return true;
		}

		// 用户按下触摸屏、快速移动后松开，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE,
		// 1个ACTION_UP触发
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (DEBUG)
				Log.d(DBG_TAG, "onFling");
			return false;
		}

		// 用户轻触触摸屏，尚未松开或拖动，由一个1个MotionEvent ACTION_DOWN触发
		// 注意和onDown()的区别，强调的是没有松开或者拖动的状态
		public void onShowPress(MotionEvent e) {
			if (DEBUG)
				Log.d(DBG_TAG, "onShowPress");
		}

		// 用户轻触触摸屏，由1个MotionEvent ACTION_DOWN触发Java代码
		public boolean onDown(MotionEvent e) {
			mCurX = 0;
			mCurY = 0;
			mHitId = _hitTestId((int) e.getX(), (int) e.getY());
			if (DEBUG)
				Log.d(DBG_TAG, "onDown at=" + mHitId);
			if (mHitId >= 0) {
				mDirty = true;
				mAdsorb = 0;
				mAdsorbBak = 0;
				mRcTarget = sRcKeys[mHitId];
			} else {
				mDirty = false;
			}
			return true;
		}

		public boolean onDoubleTap(MotionEvent e) {
			if (DEBUG)
				Log.d(DBG_TAG, "onDoubleTap action=" + e.getAction());
			return true;
		}

		public boolean onDoubleTapEvent(MotionEvent e) {
			final int action = e.getAction();
			if (DEBUG)
				Log.d(DBG_TAG, "onDoubleTapEvent action=" + action);
			if (action == MotionEvent.ACTION_UP) {
				_tryScale(mHitId);
			}
			return true;
		}

		public boolean onSingleTapConfirmed(MotionEvent e) {
			if (DEBUG)
				Log.d(DBG_TAG, "onSingleTapConfirmed");
			return true;
		}

		@Override
		public void colorChanged(int color) {
			sBackground = color;
			mRedraw = true;
		}

		private void clear() {
			mHitId = -1;
		}

		public void apply() {
			applyDesign(this);
			clear();
		}

		public void cancel() {
			cancelDesign(this);
			clear();
		}

		public void reset() {
			resetDesign();
			clear();
		}
	}
}