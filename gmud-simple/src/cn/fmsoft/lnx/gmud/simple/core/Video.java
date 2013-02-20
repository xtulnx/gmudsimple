/**
 * Copyright (C) 2011, FMSoft.GMUD.
 * 
 * Video, is used for clear, draw and fill (line, rectangle, arc, image, text).
 * 
 * @author nxliao
 */
package cn.fmsoft.lnx.gmud.simple.core;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;

/**
 * 输出绘制控制
 * 
 * @author nxliao
 * 
 */
class Video {

	/** 默认背景色 */
	static final int COLOR_BG = 0xff8FAF50;

	/** 缩放进制，用于保证无小数化 */
	static final int SCALE_RATE = Gmud.WQX_ORG_WIDTH * Gmud.WQX_ORG_HEIGHT;

	static final int LARGE_FONT_SIZE = 16;
	static final int SMALL_FONT_SIZE = 12;

	private static final Object LOCK = new Object();

	static int largeFnt = LARGE_FONT_SIZE;
	static int smallFnt = SMALL_FONT_SIZE;
	static int largeOff = largeFnt;
	static int smallOff = smallFnt;

	static float sScaleX, sScaleY;

	static Bitmap lpmemimg;
	static Canvas lpmem;

	static Paint blackBrush, greenBrush;
	static Paint blackPen;

	static Bitmap pnum;

	static Paint sPaint;

	static Matrix sMatrix = new Matrix();

	static Gmud.IVideoCallback sCallback;

	public static int sScale = 1;

	/** 输出区域，不一定与[160x80]成比例 */
	static Rect sDirtyRect;
	static int sWidth, sHeight;

	public static void SetCallback(Gmud.IVideoCallback callback) {
		sCallback = callback;
	}

	/** 重置输出区域、纵横缩放比例、字体、framebuff */
	public static void ResetLayout(Rect rect) {
		if (sDirtyRect.equals(rect)) {
			return;
		}

		Bitmap tmp_lpmemiｍg;
		Canvas tmp_lpmem;

		synchronized (LOCK) {
			tmp_lpmemiｍg = lpmemimg;
			lpmemimg = null;

			tmp_lpmem = lpmem;
//			tmp_lpmem.setBitmap(tmp_lpmemiｍg);

			sDirtyRect.set(rect);

			final int w = rect.width();
			final int h = rect.height();
			final int scale;
			if (w * Gmud.WQX_ORG_HEIGHT > h * Gmud.WQX_ORG_WIDTH) {
				scale = h * Gmud.WQX_ORG_WIDTH;
			} else {
				scale = w * Gmud.WQX_ORG_HEIGHT;
			}

			sWidth = w;
			sHeight = h;
			sScaleX = (float) w / Gmud.WQX_ORG_WIDTH;
			sScaleY = (float) h / Gmud.WQX_ORG_HEIGHT;

			Bitmap bmBack = null;
			if (tmp_lpmemiｍg != null && !tmp_lpmemiｍg.isRecycled()) {
				bmBack = tmp_lpmemiｍg;
				tmp_lpmemiｍg = Bitmap.createScaledBitmap(bmBack, sWidth,
						sHeight, true);
			} else {
				tmp_lpmemiｍg = Bitmap.createBitmap(sWidth, sHeight,
						Bitmap.Config.ARGB_8888);
				tmp_lpmemiｍg.eraseColor(COLOR_BG);
			}

			Matrix m = new Matrix();
			m.setScale(sScaleX, sScaleY);

			lpmemimg = tmp_lpmemiｍg;
			lpmem.setBitmap(lpmemimg);
			lpmem.setMatrix(m);
			if (bmBack != null) {
				bmBack.recycle();
				bmBack = null;
			}
			sScaleX = 1;
			sScaleY = 1;
			sScale = 1;

			VideoUpdate();
		}
	}

	// public static synchronized void Bind(Show show) {
	//
	// // if (sBinderShow != show) {
	// // sBinderShow = show;
	// // GmudMain.Resume();
	// // }
	// }
	//
	// public static synchronized void UnBind(Show show) {
	// sBinderShow = null;
	// }

	static boolean VideoInit() {

		sDirtyRect = new Rect();

		sPaint = new Paint();
		sPaint.setAntiAlias(true);

		lpmem = new Canvas();

		pnum = Res.loadimage(248);

		blackBrush = new Paint();
		blackBrush.setAntiAlias(true);
		blackBrush.setARGB(255, 0, 0, 0);
		blackBrush.setStyle(Style.FILL);
		greenBrush = new Paint();
		greenBrush.setAntiAlias(true);
		greenBrush.setARGB(255, 143, 175, 80);
		greenBrush.setStyle(Style.FILL);
		blackPen = new Paint();
		blackPen.setAntiAlias(true);
		blackPen.setColor(0xff000000);
		blackPen.setStyle(Style.STROKE);
		blackPen.setStrokeWidth(1.0f);

		// // 得到系统默认字体属�?
		// Paint paint = blackBrush;
		// FontMetrics fm;
		// paint.setTextSize(largeFnt);
		// fm = paint.getFontMetrics();
		// largeOff = (int) (fm.descent - fm.ascent);
		// paint.setTextSize(smallFnt);
		// fm = paint.getFontMetrics();
		// smallOff = (int) (fm.descent - fm.ascent);

		// resetScale(scale);
		ResetLayout(new Rect(0, 0, Gmud.WQX_ORG_WIDTH, Gmud.WQX_ORG_HEIGHT));

		VideoClear();
		VideoUpdate();

		return true;
	}

	static void VideoShutdown() {
		// DeleteObject(pnum);
		// DeleteObject(largeFnt);
		// DeleteObject(smallFnt);
		// DeleteObject(blackBrush);
		// DeleteObject(greenBrush);
		// DeleteObject(blackPen);
		//
		// DeleteObject(lpmemimg);
		// DeleteObject(lpmem);
		// DeleteObject(lpwnd);
		//
		// GdiplusShutdown(m_pGdiToken);
		// ReleaseDC(hw,m_hdc);
		// VideoExited = 1;
		synchronized (LOCK) {
			lpmemimg.recycle();
			lpmemimg = null;
			lpmem = null;
		}
	}

	static void exit(int code) {
		throw new RuntimeException("Video exit(" + code + ").");
	}

	static void VideoDrawLine(int x1, int y1, int x2, int y2) {
		lpmem.drawLine(x1, y1, x2, y2, blackPen);
	}

	/**
	 * 绘制一个箭头
	 * 
	 * @param x
	 * @param y
	 * @param w
	 *            宽度
	 * @param h
	 *            高度，如果小于0，表示向上
	 * @param type
	 *            类型，Bit0:左右方向 Bit1:实心 Bit2:用背景色(只用于实心)
	 */
	static void VideoDrawArrow(int x, int y, int w, int h, int type) {
		final Path path = new Path();
		x *= sScaleX;
		y *= sScaleY;
		w *= sScaleX;
		h *= sScaleY;
		path.moveTo(x, y);
		if ((type & 1) == 0) {
			path.lineTo(x + w, y);
			path.lineTo(x + w / 2, y + h);
		} else {
			path.lineTo(x, y + h);
			path.lineTo(x + w, y + h / 2);
		}
		path.close();

		if ((type & 2) == 0) {
			// clear
			lpmem.drawPath(path, greenBrush);
			lpmem.drawPath(path, blackPen);
		} else {
			if ((type & 4) == 0) {
				lpmem.drawPath(path, blackBrush);
			} else {
				lpmem.drawPath(path, greenBrush);
			}
		}
	}

	static void VideoClear() {
		synchronized (LOCK) {
			lpmemimg.eraseColor(COLOR_BG);
		}
	}

	static void VideoClearRect(int x, int y, int width, int height) {
		lpmem.drawRect(x * sScaleX, y * sScaleY, (x + width) * sScaleX,
				(y + height) * sScaleY, greenBrush);
	}

	static void VideoDrawRectangle(int x, int y, int width, int height) {
		lpmem.drawRect(x * sScale, y * sScale, (x + width) * sScale,
				(y + height) * sScale, blackPen);
	}

	static void VideoFillRectangle(int x, int y, int width, int height) {
		VideoFillRectangle(x, y, width, height, 0);
	}

	static void VideoFillRectangle(int x, int y, int width, int height, int type) {
		if (type != 0) {
			lpmem.drawRect(x * sScale, y * sScale, (x + width) * sScale,
					(y + height) * sScale, greenBrush);
		} else {
			lpmem.drawRect(x * sScale, y * sScale, (x + width) * sScale,
					(y + height) * sScale, blackBrush);
		}

	}

	static void VideoDrawArc(int x, int y, int r) {
		// lpmem->DrawArc(blackPen, x - r, y - r, 2 * r, 2 * r, 0, 360);
		lpmem.drawCircle(x * sScale, y * sScale, r * sScale, blackPen);
	}

	static void VideoFillArc(int x, int y, int r) {
		// lpmem->FillEllipse(blackBrush->Clone(), x - r, y - r, 2 * r, 2 * r);
		lpmem.drawCircle(x * sScale, y * sScale, r * sScale, blackBrush);
	}

	static void VideoDrawImage(/* Image* */Bitmap pI, int x, int y) {
		// lpmem->DrawImage(pI, x, y);
		// lpmem.drawBitmap(pI, x*sScale, y*sScale, null);

		sMatrix.setScale(sScale, sScale);
		sMatrix.postTranslate(x * sScale, y * sScale);
		lpmem.drawBitmap(pI, sMatrix, sPaint);
	}

	private static void drawMultiText(String str, int x, int y,
			int restrictWidth, Paint paint) {

		FontMetrics fm = paint.getFontMetrics();// 得到系统默认字体属性
		final int fontHeight = (int) (Math.ceil(fm.descent - fm.ascent) - 2);// 获得字体高度

		// paint.getTextWidths(str, widths);

		int line = 0;
		int linestart = 0;
		int k = str.length();
		for (int i = 1; i < k; i++) {
			float w = blackBrush.measureText(str, linestart, i);
			if (w + x >= restrictWidth * sScale) {
				lpmem.drawText(str.substring(linestart, i - 1), x * sScale, y
						* sScale + fontHeight * line, blackBrush);
				linestart = i - 1;
				line++;
			}
		}
		if (linestart < k) {
			lpmem.drawText(str.substring(linestart, k), x * sScale, y * sScale
					+ fontHeight * line, blackBrush);
			return;
		}
	}

	static void VideoDrawString(String str, int x, int y) {
		VideoDrawString(str, x, y, 0);
	}

	static void VideoDrawString(String str, int x, int y, int type) {
		// 大字体y坐标:每行+16 //小字体y坐标:每行+13
		if (type != 0) {
			blackBrush.setTextSize(largeFnt);
			lpmem.drawText(str, x * sScale, y * sScale + largeOff, blackBrush);
		} else {
			blackBrush.setTextSize(smallFnt);
			drawMultiText(str, x, y + smallOff / sScale, Gmud.WQX_ORG_WIDTH,
					blackBrush);
		}
	}

	// static void VideoDrawString(const wchar_t*, int, int, int type = 0);
	static void VideoDrawStringSingleLine(final String str, int x, int y) {
		VideoDrawStringSingleLine(str, x, y, 0);
	}

	static void VideoDrawStringSingleLine(final String str, int x, int y,
			int type) {
		// PointF origin(x, y);
		// 大字体y坐标:每行+16
		// 小字体y坐标:每行+13
		switch (type) {
		case 1:
			// lpmem->DrawString(str, -1, largeFnt, PointF(x, y), blackBrush);
			blackBrush.setTextSize(largeFnt);
			lpmem.drawText(str, x * sScale, y * sScale + largeOff, blackBrush);
			break;
		case 2:
			greenBrush.setTextSize(smallFnt);
			lpmem.drawText(str, x * sScale, y * sScale + smallOff, greenBrush);
			break;
		default:
			blackBrush.setTextSize(smallFnt);
			lpmem.drawText(str, x * sScale, y * sScale + smallOff, blackBrush);
		}
	}

	static void VideoDrawNumberData(String data, int x, int y) {
		Rect rectSrc = new Rect(0, 0, 4, 5);
		Rect rectDst = new Rect(x * sScale, y * sScale, (x + 4) * sScale,
				(y + 5) * sScale);

		for (int i1 = 0; i1 < data.length(); i1++) {
			char c = data.charAt(i1);

			if ('0' <= c && c <= '9') {
				rectSrc.left = 4 * (c - '0');
				rectSrc.right = 4 * (c - '0') + 4;
			} else {
				rectSrc.left = 4 * 10;
				rectSrc.right = 4 * 10 + 4;
			}
			lpmem.drawBitmap(pnum, rectSrc, rectDst, sPaint);

			rectDst.left += 4 * sScale;
			rectDst.right += 4 * sScale;
		}
	}

	public static void VideoUpdate() {
		if (sCallback != null) {
			sCallback.VideoPostUpdate(lpmemimg);
		}
	}

	static ArrayList<String> SplitString(String str, int width) {

		final Paint paint = blackBrush;
		paint.setTextSize(smallFnt);

		ArrayList<String> sv = new ArrayList<String>();

		int linestart = 0;
		// RectF r;
		int k = str.length();

		for (int k1 = 0; k1 < k; k1++) {
			if (str.charAt(k1) == '\n') {
				if (k1 != 0) {
					sv.add(str.substring(linestart, k1));
					linestart = k1 + 1;
				}
				continue;
			}

			// if (k1 == k - 1) {
			// sv.add(str.substring(linestart, k1 + 1));
			// break;
			// }

			float w = blackBrush.measureText(str, linestart, k1);
			if (w >= width * sScale) {
				sv.add(str.substring(linestart, k1 - 1));
				linestart = k1 - 1;
			}

			// lpmem->MeasureString(str->substr(linestart, length).c_str(),
			// length, smallFnt, origin, &r);
			// if (r.GetRight() > width)
			// {
			// length = k1 - linestart;
			// sv.push_back(str->substr(linestart, length));
			// linestart = k1;
			// }
		}
		// if (linestart < k) {
		sv.add(str.substring(linestart, k));
		// }
		return sv;
	}
}
