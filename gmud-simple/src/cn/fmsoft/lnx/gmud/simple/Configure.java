package cn.fmsoft.lnx.gmud.simple;

import android.content.Context;
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
import cn.fmsoft.lnx.gmud.simple.Configure.ConfigInfo;
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
	private static float CUR_DENSITY = 1.0f;

	private static ConfigInfo sDefConfigLand, sDefConfigPort;
	private static ConfigInfo sCurConfigInfo;

	/** 布局配置 */
	protected static class ConfigInfo {
		/** 默认：背景色 */
		public final static int DEF_BACKGROUND_COLOR = Color.LTGRAY;

		/** 横屏时左右留白，避免游戏区占满屏导致按钮不可见 */
		final static int MARGIN_H_LAND = 80;
		final static int MARGIN_V_LAND = 80;
		final static int MARGIN_H_PORT = 0;
		final static int MARGIN_V_PORT = 160;

		final static int DEF_BT_WIDTH = 96;
		final static int DEF_BT_HEIGHT = 32;
		final static int DEF_BT_COLUMN_PORT = 3;
		final static int DEF_BT_ROW_PORT = 4;
		final static int DEF_BT_COLUMN_LAND = 2;
		final static int DEF_BT_ROW_LAND = 5;
		final static int DEF_BT_TITLE_SIZE = 10;

		/** 默认：竖屏：软键配置 */
		private final static int[][] DEF_SOFTKEY_PORT = new int[][] { { 0, 0 },
				{ 2, 0 }, { 1, 3 }, { 0, 3 }, { 1, 2 }, { 2, 3 }, { 0, 1 },
				{ 2, 1 }, { 1, 1 } };
		/** 默认：横屏：软键配置 */
		private final static int[][] DEF_SOFTKEY_LAND = new int[][] { { 0, 0 },
				{ 1, 0 }, { 1, 4 }, { 1, 2 }, { 1, 1 }, { 1, 3 }, { 0, 2 },
				{ 0, 3 }, { 0, 1 } };

		/** 各按键区 */
		protected final Rect mRcKeys[] = new Rect[_KEY_MAX_ + 1];
		/** 该配置应用的宽高 */
		protected int mWidth, mHeight;

		/** 保持比例按整数倍数拉伸铺满 */
		private static int _get_video_fill_scale(int w, int h) {
			final float scale;
			float scale_w, scale_h;
			scale_w = 1.0f * w / Gmud.WQX_ORG_WIDTH;
			scale_h = 1.0f * h / Gmud.WQX_ORG_HEIGHT;
			if (scale_w < scale_h) {
				scale = scale_w;
			} else {
				scale = scale_h;
			}
			int s = (int) scale;
			return s;
		}

		/**
		 * 默认配置（横屏）
		 * 
		 * @param w
		 * @param h
		 */
		private static final ConfigInfo generateDefaultLand(int w, int h) {
			final ConfigInfo info = new ConfigInfo(w, h);

			// 计算最小整数倍
			final int scale = _get_video_fill_scale(w - MARGIN_H_LAND * 2, h
					- MARGIN_V_LAND);
			final int vw = Gmud.WQX_ORG_WIDTH * scale;
			final int vh = Gmud.WQX_ORG_HEIGHT * scale;

			final int bw = (int) (CUR_DENSITY * DEF_BT_WIDTH);
			final int bh = (int) (CUR_DENSITY * DEF_BT_HEIGHT);

			int bph = (w - vw - 8 * 2) / DEF_BT_COLUMN_LAND - bw;
			if (bph > 0) {
				bph = 0;
			}
			final int bpv = (h - bh * DEF_BT_ROW_LAND) / (DEF_BT_ROW_LAND + 1);

			final int top = 0 + bpv;
			final Rect rcKeys[] = info.mRcKeys;
			final int softkey[][] = DEF_SOFTKEY_LAND;
			for (int i = 0, c = _KEY_MAX_; i < c; i++) {
				final int x = softkey[i][0] == 0 ? bph : (w - bw - bph);
				final int y = top + (bh + bpv) * softkey[i][1];
				rcKeys[i] = new Rect(x, y, x + bw, y + bh);
			}
			rcKeys[_KEY_MAX_] = new Rect((w - vw) / 2, (h - vh) / 2,
					(vw + w) / 2, (h + vh) / 2);

			return info;
		}

		/**
		 * 竖屏
		 * 
		 * @param w
		 * @param h
		 */
		private static final ConfigInfo generateDefaultPort(int w, int h) {
			final ConfigInfo info = new ConfigInfo(w, h);
			// 计算最小整数倍
			final int scale = _get_video_fill_scale(w - MARGIN_H_PORT * 2, h
					- MARGIN_V_PORT);
			final int vw = Gmud.WQX_ORG_WIDTH * scale;
			final int vh = Gmud.WQX_ORG_HEIGHT * scale;

			final int bw = (int) (CUR_DENSITY * DEF_BT_WIDTH);
			final int bh = (int) (CUR_DENSITY * DEF_BT_HEIGHT);

			final int bph = (w - bw * DEF_BT_COLUMN_PORT)
					/ (DEF_BT_COLUMN_PORT + 1);
			final int bpv = (h - vh - bh * DEF_BT_ROW_PORT)
					/ (DEF_BT_ROW_PORT + 1);

			final int top = vh + bpv;
			final Rect rcKeys[] = info.mRcKeys;
			final int softkey[][] = DEF_SOFTKEY_PORT;
			for (int i = 0, c = _KEY_MAX_; i < c; i++) {
				final int x = bph + (bw + bph) * softkey[i][0];
				final int y = top + (bh + bpv) * softkey[i][1];
				rcKeys[i] = new Rect(x, y, x + bw, y + bh);
			}
			rcKeys[_KEY_MAX_] = new Rect((w - vw) / 2, 0, (vw + w) / 2, vh);

			return info;
		}

		private ConfigInfo(int w, int h) {
			mWidth = w;
			mHeight = h;
		}

		protected ConfigInfo(ConfigInfo info) {
			mWidth = info.mWidth;
			mHeight = info.mHeight;
			for (int i = 0, c = _KEY_MAX_ + 1; i < c; i++) {
				mRcKeys[i] = new Rect(info.mRcKeys[i]);
			}
		}
	}

	/** 初始化 */
	protected static void init(Context ctx) {
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
		ConfigInfo info = new ConfigInfo(defInfo);
		return info;
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
		ConfigInfo defInfo = isLandscape ? sDefConfigLand : sDefConfigPort;
		if (defInfo == null || defInfo.mWidth != w || defInfo.mHeight != h) {
			if (isLandscape) {
				defInfo = ConfigInfo.generateDefaultLand(w, h);
				sDefConfigLand = defInfo;
			} else {
				defInfo = ConfigInfo.generateDefaultPort(w, h);
				sDefConfigPort = defInfo;
			}
		}

		// try load ...
		final ConfigInfo info = tryLoadConfig(isLandscape, defInfo);

		applyConfig(info == null ? defInfo : info);

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

	protected static void Draw(Canvas canvas) {

	}

	/** 绘制软键 */
	public static void drawKeypad(Canvas canvas) {
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
	public static void drawVideo(Canvas canvas, Bitmap video) {
		canvas.drawBitmap(video, null, sRcVideo, TITLE_PAINT);
	}

	/** 响应单击按钮，如无变化返回false，否则返回true */
	protected static boolean HitTest(MotionEvent event) {
		final int action = event.getAction();
		final int count = event.getPointerCount();

		if (DEBUG)
			Log.d("lnx", "action=" + action);

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

	/**
	 * 自定义界面
	 * 
	 * @author nxliao
	 * @version 0.1.20130219.1454
	 */
	public static class Design extends GestureDetector.SimpleOnGestureListener {
		private float mLastX;
		private float mLastY;
		private float mCurX;
		private float mCurY;
		private Show mShow;

		/** 当前元素ID */
		private int mHitId = -1;

		private boolean mDirty = false;

		private Rect mRcBak = new Rect();

		/** 目标位置（即 ACTION_UP 后元素应放下的位置） */
		private Rect mRcTarget;

		Configure.ConfigInfo mConfigInfo;

		public Design(Show show) {
			mShow = show;
		}

		public void SetConfigInfo(Configure.ConfigInfo info) {
			mConfigInfo = info;
		}

		protected boolean updateInvalidate() {
			return false;
		}

		protected void UnionInvalidateRect(Rect bound) {
			if (mHitId >= 0 && mDirty) {
				bound.union(mRcTarget);
				mRcTarget.offset((int) mCurX, (int) mCurY);
				bound.union(mRcTarget);
				mCurX = 0;
				mCurY = 0;
				mDirty = false;
			}
		}

		protected void Draw(Canvas canvas, Bitmap video, int tick) {
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

		//
		// ////////////////////////////////////////////////////////////////////////
		//
		// - 手势事件 -
		//
		//

		// 用户（轻触触摸屏后）松开，由一个1个MotionEvent ACTION_UP触发
		public boolean onSingleTapUp(MotionEvent e) {
			if (DEBUG)
				Log.d("lnx", "onSingleTapUp");
			final int x = (int) e.getX();
			final int y = (int) e.getY();

			return true;
		}

		// 用户长按触摸屏，由多个MotionEvent ACTION_DOWN触发
		public void onLongPress(MotionEvent e) {
			if (DEBUG)
				Log.d("lnx", "onLongPress");
		}

		// 用户按下触摸屏，并拖动，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE触发
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			if (DEBUG)
				Log.d("lnx", "onScroll dx=" + distanceX + " dy=" + distanceY
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
				Log.d("lnx", "onFling");
			return false;
		}

		// 用户轻触触摸屏，尚未松开或拖动，由一个1个MotionEvent ACTION_DOWN触发
		// 注意和onDown()的区别，强调的是没有松开或者拖动的状态
		public void onShowPress(MotionEvent e) {
			final int x = (int) e.getX();
			final int y = (int) e.getY();
			if (DEBUG)
				Log.d("lnx", "onShowPress");
			// final int flag = HitTestFlag(x, y);
			// onKeyDown(flag);
		}

		// 用户轻触触摸屏，由1个MotionEvent ACTION_DOWN触发Java代码
		public boolean onDown(MotionEvent e) {
			mLastX = e.getX();
			mLastY = e.getY();
			mCurX = 0;
			mCurY = 0;
			mHitId = _hitTestId((int) mLastX, (int) mLastY);
			if (DEBUG)
				Log.d("lnx", "onDown at=" + mHitId);
			if (mHitId >= 0) {
				mDirty = true;
				mRcTarget = sRcKeys[mHitId];
				return true;
			}
			mDirty = false;
			return false;
		}

		public boolean onDoubleTap(MotionEvent e) {
			if (DEBUG)
				Log.d("lnx", "onDoubleTap action=" + e.getAction());
			return true;
		}

		public boolean onDoubleTapEvent(MotionEvent e) {
			if (DEBUG)
				Log.d("lnx", "onDoubleTapEvent action=" + e.getAction());
			return true;
		}

		public boolean onSingleTapConfirmed(MotionEvent e) {
			if (DEBUG)
				Log.d("lnx", "onSingleTapConfirmed");
			return true;
		}
	}

}