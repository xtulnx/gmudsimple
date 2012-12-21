package cn.fmsoft.lnx.gmud.simple;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import cn.fmsoft.lnx.gmud.simple.core.Gmud;

public final class Configure {
	final static boolean DEBUG = true;
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

	/** 横屏时左右留白，避免游戏区占满屏导致按钮不可见 */
	private final static int MARGIN_H_LAND = 80;
	private final static int MARGIN_V_LAND = 80;
	private final static int MARGIN_H_PORT = 0;
	private final static int MARGIN_V_PORT = 160;

	private final static int DEF_BT_WIDTH = 96;
	private final static int DEF_BT_HEIGHT = 32;
	private final static int DEF_BT_COLUMN_PORT = 3;
	private final static int DEF_BT_ROW_PORT = 4;
	private final static int DEF_BT_TITLE_SIZE = 10;

	/** 视频输出区域 */
	protected static final Rect sRcVideo = new Rect();
	/** 各按键区 */
	protected static final Rect sRcKeys[] = new Rect[_KEY_MAX_];
	/** 按下状态 */
	private static int sPressMask = 0;
	/** 剪切区，每次绘制前重新获取 */
	private static Rect sRcClip;
	private static Drawable BG_NORMAL;
	private static Drawable BG_FOCUS;
	private static Paint TITLE_PAINT;
	private static String TITLE[];
	private static float CUR_DENSITY = 1.0f;

	static final int[][] sSoftKeyPort = new int[][] { { 0, 0 }, { 2, 0 },
			{ 1, 3 }, { 0, 3 }, { 1, 2 }, { 2, 3 }, { 0, 1 }, { 2, 1 },
			{ 1, 1 } };
	static final int[][] sSoftKeyLand = new int[][] { { 0, 1 }, { 0, 0 },
			{ 4, 1 }, { 3, 1 }, { 4, 0 }, { 5, 1 }, { 3, 0 }, { 5, 0 },
			{ 1, 0 } };

	protected static void init(Context ctx) {
		Resources res = ctx.getResources();
		final DisplayMetrics dm = res.getDisplayMetrics();
		CUR_DENSITY = dm.density;
		BG_NORMAL = res.getDrawable(R.drawable.bg_normal);
		BG_FOCUS = res.getDrawable(R.drawable.bg_focus);
		TITLE = res.getStringArray(R.array.soft_key);
		sRcClip = new Rect();
		TITLE_PAINT = new Paint();

		TITLE_PAINT.setAntiAlias(true);
		TITLE_PAINT.setFakeBoldText(true);
		TITLE_PAINT.setTextSize(DEF_BT_TITLE_SIZE * CUR_DENSITY);
		TITLE_PAINT.setTextAlign(Paint.Align.LEFT);
	}

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

	private static void _draw_key(Canvas canvas, int id) {
		final Rect rc = sRcKeys[id];
		if (Rect.intersects(sRcClip, rc)) {
			final Drawable d;
			if ((sPressMask & (1 << id)) == 0) {
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
		}
	}

	/**
	 * 默认配置（横屏）
	 * 
	 * @param w
	 * @param h
	 */
	private static void _reset_default_land(int w, int h) {

	}

	/**
	 * 竖屏
	 * 
	 * @param w
	 * @param h
	 */
	private static void _reset_default_port(int w, int h) {
		// 计算最小整数倍
		final int scale = _get_video_fill_scale(w - MARGIN_H_PORT * 2, h
				- MARGIN_V_PORT);
		final int vw = Gmud.WQX_ORG_WIDTH * scale;
		final int vh = Gmud.WQX_ORG_HEIGHT * scale;

		final int bw = (int) (CUR_DENSITY * DEF_BT_WIDTH);
		final int bh = (int) (CUR_DENSITY * DEF_BT_HEIGHT);

		final int bph = (w - bw * DEF_BT_COLUMN_PORT)
				/ (DEF_BT_COLUMN_PORT + 1);
		final int bpv = (h - vh - bh * DEF_BT_ROW_PORT) / (DEF_BT_ROW_PORT + 1);

		sRcVideo.set((w - vw) / 2, 0, (vw + w) / 2, vh);
		final int top = vh + bpv;
		for (int i = 0, c = _KEY_MAX_; i < c; i++) {
			final int x = bph + (bw + bph) * sSoftKeyPort[i][0];
			final int y = top + (bh + bpv) * sSoftKeyPort[i][1];
			sRcKeys[i] = new Rect(x, y, x + bw, y + bh);
		}
	}

	/**
	 * 根据总区域大小重置各区域
	 * 
	 * @param w
	 * @param h
	 */
	protected static void reset(int w, int h) {
		if (w > h) {
			_reset_default_land(w, h);
		} else {
			_reset_default_port(w, h);
		}

		// 重置缩放比例
	}
	
	/** 检查有无必要重绘 */
	protected static boolean updateInvalidate() {
		return false;
	}
	
	protected static void Draw(Canvas canvas) {
		
	}

	public static void onDraw(Canvas canvas) {
		canvas.getClipBounds(sRcClip);
		for (int i = 0, c = _KEY_MAX_; i < c; i++) {
			_draw_key(canvas, i);
		}
	}
}