/**
 * Copyright (C) 2011, FMSoft.GMUD.
 * 
 * @author nxliao
 */
package cn.fmsoft.lnx.gmud.simple;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import cn.fmsoft.lnx.gmud.simple.core.Gmud;

/**
 * 展示. 定期刷新！
 * 
 * @author nxliao
 * 
 */
public class Show extends SurfaceView implements SurfaceHolder.Callback,
		Gmud.IVideoCallback {

	private final Object LOCK = new Object();

	private final static int DELAY_AUTO_UPDATE_KEYPAD = 50;

	private static int mUpdateStatus = -1;

	/** 更新游戏区 */
	private static final int UPDATE_VIDEO = 1 << 0;
	/** 更新按键 */
	private static final int UPDATE_KEYPAD = 1 << 1;
	/** 停止绘制 */
	private static final int UPDATE_CANCEL = 1 << 31;

	private Bitmap mBmShadow;
	private Canvas mCanvas;

	private class MyThread extends Thread {
		public MyThread() {
			super("auto-draw");
		}

		@Override
		public void run() {
			while (Show.this.tryDraw()) {
				try {
					sleep(DELAY_AUTO_UPDATE_KEYPAD);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public Show(Context context, AttributeSet attrs) {
		super(context, attrs);

		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		// setFocusable(false);
		// setFocusableInTouchMode(false);

	}

	public boolean tryDraw() {
		synchronized (LOCK) {
			final int status = mUpdateStatus;
			if ((status & UPDATE_CANCEL) != 0)
				return false;

			final boolean update_video = (status & UPDATE_VIDEO) != 0;
			final boolean update_keypad = (status & UPDATE_KEYPAD) != 0;

			Rect dirty = new Rect();
			if (update_video) {
				dirty.set(Configure.sRcVideo);
			}
			if (update_keypad) {
				Configure.UnionInvalidateRect(dirty);
			}

			final Canvas canvas;
			final SurfaceHolder holder = getHolder();
			if (dirty.isEmpty()) {
				return true;
			} else {
				canvas = holder.lockCanvas(dirty);
			}

			if (canvas != null) {
				canvas.drawColor(Color.LTGRAY);
				if (update_keypad)
					Configure.drawKeypad(canvas);
				if (update_video) {
					Configure.drawVideo(canvas, mBmShadow);
					mUpdateStatus &= ~(UPDATE_VIDEO);
				}
			}
			getHolder().unlockCanvasAndPost(canvas);
			return true;
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		if (widthMode == MeasureSpec.EXACTLY) {
			// Parent has told us how big to be. So be it.
		}

		setMeasuredDimension(widthSize, heightSize);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Gmud.SetVideoCallback(this);

		synchronized (LOCK) {
			mUpdateStatus = UPDATE_KEYPAD | UPDATE_VIDEO;
		}

		new MyThread().start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		synchronized (LOCK) {
			Configure.reset(width, height);
			mUpdateStatus |= UPDATE_KEYPAD | UPDATE_VIDEO;
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Gmud.SetVideoCallback(null);
		synchronized (LOCK) {
			mUpdateStatus |= UPDATE_CANCEL;
		}
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void VideoPostUpdate(Bitmap video) {
		synchronized (LOCK) {
			if ((mUpdateStatus & UPDATE_CANCEL) != 0)
				return;

			if (video == null || video.isRecycled())
				return;

			if (mCanvas == null) {
				mCanvas = new Canvas();
			}

			if (mBmShadow == null) {
				mBmShadow = Bitmap.createBitmap(video);
				mCanvas.setBitmap(mBmShadow);
			} else if (mBmShadow.getWidth() != video.getWidth()
					|| mBmShadow.getHeight() != video.getHeight()) {
				final Bitmap bm = Bitmap.createBitmap(video);
				mCanvas.setBitmap(bm);
				mBmShadow.recycle();
				mBmShadow = bm;
			}

			mCanvas.drawBitmap(video, 0, 0, null);
			mUpdateStatus |= UPDATE_VIDEO;
		}
	}

	public void KeyPostUpdate() {
		synchronized (LOCK) {
			mUpdateStatus |= UPDATE_KEYPAD;
		}
	}
}
