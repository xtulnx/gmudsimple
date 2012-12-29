/**
 * Copyright (C) 2011, FMSoft.GMUD.
 * 
 * @author nxliao
 */
package cn.fmsoft.lnx.gmud.simple;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import cn.fmsoft.lnx.gmud.simple.core.Gmud;

public class Show extends SurfaceView implements SurfaceHolder.Callback,
		Gmud.IVideoCallback {

	private final static int DELAY_AUTO_UPDATE_KEYPAD = 50;
	
	private final static int MSG_UPDATE = 0x10000;
	private final static int MSG_UPDATE_KEYPAD = 0x10001;
	private final Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_UPDATE:
				onUpdate();
				break;

			default:
				break;
			}
		}
	};

	public Show(Context context, AttributeSet attrs) {
		super(context, attrs);

		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
//		setFocusable(false);
//		setFocusableInTouchMode(false);
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
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

		Configure.reset(width, height);
		Canvas c = getHolder().lockCanvas();
		if (c != null) {
			c.drawColor(Color.LTGRAY);
			Configure.onDraw(c);
			Gmud.DrawVideo(c);
			getHolder().unlockCanvasAndPost(c);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Gmud.SetVideoCallback(null);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		final int action = event.getAction();
		final int count = event.getPointerCount();

		if ((action & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
			Configure.onKeyUp(0);
			onUpdate();
			return true;
		} else {
			int flag = 0;
			for (int i = 0, c = count; i < c; i++) {
				final int x = (int) event.getX(i);
				final int y = (int) event.getY(i);
				flag |= Configure.HitTestFlag(x, y);
			}
			Configure.onKeySet(flag);
			onUpdate();
			return true;
		}
//		return super.onTouchEvent(event);
	}
	

	@Override
	public void VideoPostUpdate() {
		// if (mHandler.hasMessages(MSG_UPDATE)) {
		// Message msg = Message.obtain(mHandler, MSG_UPDATE);
		// mHandler.sendMessage(msg);
		// }
		Canvas c = getHolder().lockCanvas(Configure.sRcVideo);
		if (c != null) {
			c.drawColor(Color.LTGRAY);
			Configure.onDraw(c);
			Gmud.DrawVideo(c);
			getHolder().unlockCanvasAndPost(c);
		}
	}

	protected void onUpdate() {
		Canvas c = getHolder().lockCanvas();
		if (c != null) {
			Configure.onDraw(c);
			Gmud.DrawVideo(c);
			getHolder().unlockCanvasAndPost(c);
		}
	}
}
