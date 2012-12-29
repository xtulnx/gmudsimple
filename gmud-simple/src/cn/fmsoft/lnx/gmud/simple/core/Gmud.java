/**
 * Copyright (C) 2011, FMSoft.GMUD.
 * 
 * Gmud: base server, initialize static data, start input thread, provide archive.
 * 
 * @author nxliao
 */
package cn.fmsoft.lnx.gmud.simple.core;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * 对外接口封装
 * 
 * @author nxliao
 * 
 */
public class Gmud {

	final static boolean DEBUG = true;

	private final static Object SYNC = new Object();

	protected final static String END_TAG = "gmud.end";

	public final static String SAVE_PATH = "gmud_save";

	public final static int WQX_ORG_WIDTH = 160;
	public final static int WQX_ORG_HEIGHT = 80;
	static final int FRAME_TIME = 1000 / 60;

	static Map sMap;
	static Player sPlayer;

	// public static boolean Running = false;

	public final static int RS_UNINITIALIZED = 0;
	public final static int RS_RUNNING = 1;
	public final static int RS_PAUSE = 2;
	public final static int RS_STOP = 3;

	private static int sRunStatus = RS_UNINITIALIZED;

	static Context sContext;

	static Activity sActivity = null;

	static boolean sbConfig_MinScale = true;

	private static ICallback sCallback;

	/**
	 * 清除数据，用于自杀
	 * 
	 * @return
	 */
	static boolean CleanSave() {
		return sPlayer.Clean(sContext);
	}

	/**
	 * 存档
	 * 
	 * @return
	 */
	static boolean WriteSave() {
		return sPlayer.Save(sContext);
	}

	/**
	 * 读档
	 * 
	 * @return
	 */
	static boolean LoadSave() {
		return sPlayer.load(sContext);
	}
	
	static void tryWait() throws InterruptedException {
		SYNC.wait();
	}
	
	static void tryNotify() {
		try {
			SYNC.notifyAll();	
		} catch (IllegalMonitorStateException e) {
			e.printStackTrace();
		}
	}

	/**
	 * sleep 方式延时
	 * 
	 * @param millis
	 */
	static void GmudDelay(int millis) {
		while (true) {
			final int status;
			synchronized (SYNC) {
				status = sRunStatus;
				if (status == RS_RUNNING) {
					break;
				}

				if (status == RS_STOP) {
					throw new RuntimeException(END_TAG);
				}

				try {
					tryWait();
				} catch (InterruptedException e) {
					throw new RuntimeException(END_TAG);
				}
			}
		}

		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new RuntimeException(END_TAG);
		}
	}

	// 初始化一些静态数据
	static void prepare(Context ctx) {
		sMap = Map.getInstance();
		sPlayer = Player.getInstance();
	}

	public static void exit() {
		Input.Stop();

		if (sActivity != null) {
			sActivity.finish();
		} else {
			System.exit(0);
		}
	}

	// public static void setMinScale(boolean bMin) {
	// sbConfig_MinScale = bMin;
	// }

	// 计算初始缩放比例
	// public static void checkScale() {
	// try {
	// final Display display = sActivity.getWindowManager()
	// .getDefaultDisplay();
	//
	// // 按竖屏的模式计算宽高比
	// int w = display.getWidth();
	// int h = display.getHeight();
	// if (sbConfig_MinScale && w > h) {
	// int tmp = w;
	// w = h;
	// h = tmp;
	// }
	//
	// final float scale;
	// float scale_w, scale_h;
	// scale_w = 1.0f * w / Gmud.WQX_ORG_WIDTH;
	// scale_h = 1.0f * h / Gmud.WQX_ORG_HEIGHT;
	// if (scale_w < scale_h) {
	// scale = scale_w;
	// } else {
	// scale = scale_h;
	// }
	// int s = (int) scale;
	// Video.resetScale(s);
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	// public static void bind(Activity activity) {
	// sActivity = activity;
	//
	// checkScale();
	// }
	//
	// public static void unbind(Activity activity) {
	// sActivity = null;
	// }

	public Gmud(Context context) {
		sContext = context;

		sRunStatus = RS_UNINITIALIZED;

		Res.init(context);
		Video.VideoInit();
		new GmudMain(context).start();
	}

	public static interface ICallback {
		/** 弹出框输入名字，不可为空，完成后需调用 {@link Gmud#SetNewName(String)}， 0玩家名 1武器名 */
		public void EnterNewName(int type);
	}

	public static void SetCallback(ICallback callback) {
		sCallback = callback;
	}

	public static interface IVideoCallback {
		/** 通知有更新，由外部调用 {@link Video#onDraw(Canvas)} 完成刷新 */
		public void VideoPostUpdate();
	}

	public static void SetVideoCallback(IVideoCallback callback) {
		Video.SetCallback(callback);
	}
	
	public static void ResetVideoLayout(Rect rect) {
		Video.ResetLayout(rect);
	}
	
	public static void DrawVideo(Canvas canvas) {
		Video.onDraw(canvas);
	}
	
	public static boolean IsRunning() {
		synchronized (SYNC) {
			return sRunStatus == RS_RUNNING;
		}
	}

	public static void Start() {
		synchronized (SYNC) {
			if (sRunStatus == RS_UNINITIALIZED) {
				sRunStatus = RS_RUNNING;
				tryNotify();
			}
		}
	}

	/** 暂停游戏线程 */
	public static void Pause() {
		synchronized (SYNC) {
			if (sRunStatus == RS_RUNNING)
				sRunStatus = RS_PAUSE;
		}
	}

	/** 恢复游戏线程 */
	public static void Resume() {
		synchronized (SYNC) {
			if (sRunStatus == RS_PAUSE) {
				sRunStatus = RS_RUNNING;
				tryNotify();
			}
		}
	}

	/** 中止游戏线程 */
	public static boolean Exit() {
		final int status;
		synchronized (SYNC) {
			status = sRunStatus;
			sRunStatus = RS_STOP;
			tryNotify();
		}
		return (status != RS_UNINITIALIZED && status != RS_STOP);
	}

	// ////////////////////////////////////////////////////////////////////////
	private static String s_tmp_new_name = null;

	public static void SetNewName(String name) {
		s_tmp_new_name = name;
		Resume();
	}

	/** 等待输入名字， 0玩家名 1武器名 */
	protected static String WaitForNewName(final int type) {
		if (sCallback != null) {
			sCallback.EnterNewName(type);
			Pause();
			GmudDelay(1);
		}
		return s_tmp_new_name;
	}
}
