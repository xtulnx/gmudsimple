/**
 * Copyright (C) 2011, FMSoft.GMUD.
 * 
 * @author nxliao
 */
package cn.fmsoft.lnx.gmud.simple;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;
import cn.fmsoft.lnx.gmud.simple.Configure.Design;
import cn.fmsoft.lnx.gmud.simple.core.Gmud;
import cn.fmsoft.lnx.gmud.simple.core.Input;
import cn.fmsoft.lnx.gmud.simple.core.Player;

public class GmudActivity extends Activity implements Gmud.ICallback {
	private int mRequestOritation = Configuration.ORIENTATION_SQUARE;
	private boolean bLockScreen = false;
	private boolean bHideSoftKey = false;
	private Design mDesign;
	private GestureDetector mDetector;

	private static PendingIntent sMainIntent;

	private Show mShow;

	private final int _MSG_USER_ = 0x1000;
	private final int MSG_UPDATE_PLAY_TIME = _MSG_USER_ + 1;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_UPDATE_PLAY_TIME:
				show_time();
				break;

			default:
				super.handleMessage(msg);
				break;
			}
		}
	};
	protected boolean mDesignIng;

	private boolean bShowTime;
	private long mMinutes = 0;
	private int mSeconds = 0;
	private String BASE_TITLE;
	private String SHOW_TIME_FORMART = "%s   %.2f'%4d:%d";

	protected static PendingIntent getPendingIntent(Context ctx) {
		if (sMainIntent != null)
			return sMainIntent;

		Intent intent = new Intent(ctx, GmudActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return PendingIntent.getActivity(ctx, 0, intent, 0);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.main);

		BASE_TITLE = getTitle().toString();

		sMainIntent = PendingIntent.getActivity(getBaseContext(), 0,
				new Intent(getIntent()), getIntent().getFlags());

		mRequestOritation = getRequestedOrientation();
		Log.i("lnx", "Orientation = " + mRequestOritation);

		Configure.init(getBaseContext());

		mShow = (Show) findViewById(R.id.show);

		mDesign = new Design(mShow);
		mDetector = new GestureDetector(mDesign);
		mDetector.setIsLongpressEnabled(true);
		mShow.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (mDesignIng) {
					return mDetector.onTouchEvent(event);
				} else {
					if (!bHideSoftKey && Configure.HitTest(event)) {
						((Show) v).KeyPostUpdate();
					}
					return true;
				}
			}
		});

		// new Gmud(this);
		Gmud.SetCallback(this);
		Gmud.Start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		Gmud.SetCallback(null);

		// Gmud.Exit();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}

		// System.exit(0);

		// �����������ַ�ʽ
		// android.os.Process.killProcess(android.os.Process.myPid());
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Video.VideoUpdate();

		loadConfig();
	}

	private void loadConfig() {
		SharedPreferences p = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		boolean lockScreen = p.getBoolean(getString(R.string.key_lock_screen),
				bLockScreen);
		if (lockScreen != bLockScreen) {
			bLockScreen = lockScreen;
			lock_screen();
		}
		boolean hideSoftKey = p.getBoolean(
				getString(R.string.key_hide_softkey), bHideSoftKey);
		if (hideSoftKey != bHideSoftKey) {
			bHideSoftKey = hideSoftKey;
			hide_softkey();
		}

		boolean showTime = p.getBoolean(getString(R.string.key_show_time),
				bShowTime);
		if (showTime != bShowTime) {
			bShowTime = showTime;
			if (showTime)
				show_time();
			else
				setTitle(BASE_TITLE);
		}
	}

	private void show_time() {
		if (mMinutes > 0 || mSeconds > 0) {
			setTitle(String.format(SHOW_TIME_FORMART, BASE_TITLE, 14
					+ (float) mMinutes / Player.AGE_TIME, mMinutes
					% Player.AGE_TIME, mSeconds));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
		return true;
		//
		// menu.add(0, MENU_HOOKGAME, 0, "Hook");
		// menu.add(0, MENU_EXITAPPLICATION, 0, "Exit Game");
		// menu.add(0, MENU_ABOUT, 0, "About");
		// return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// case MENU_EXITAPPLICATION:
		case R.id.item_exit:
			Gmud.exit();
			break;

		// case R.id.item_lockscreen:
		// bLockScreen = !item.isChecked();
		// item.setChecked(bLockScreen);
		// lock_screen();
		// break;
		//
		// case R.id.item_hidekey:
		// bHideSoftKey = !item.isChecked();
		// item.setChecked(bHideSoftKey);
		// hide_softkey();
		// break;

		// case R.id.item_hook:
		// break;
		case R.id.item_design: {
			tryDesign();
			Toast.makeText(getBaseContext(), "可通过双击目标设计更多内容", Toast.LENGTH_LONG)
					.show();
		}
			break;

		case R.id.item_design_apply: {
			applyDesign(true);
		}
			break;
		case R.id.item_design_cancel: {
			applyDesign(false);
		}
			break;
		case R.id.item_design_reset: {
			if (mDesignIng)
				mShow.ResetDesign();
		}
			break;

		case R.id.item_setting:
			Intent intent = new Intent(getBaseContext(), SettingActivity.class);
			startActivity(intent);
			break;

		case R.id.item_about:
			Dialog dialog = new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.btn_star)
					.setTitle(R.string.title).setMessage(R.string.about)
					.setPositiveButton("OK", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					}).create();
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(true);
			dialog.show();
			break;

		// case R.id.item_backup:
		// backup();
		// break;
		//
		// case R.id.item_restore:
		// restore();
		// break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.setGroupVisible(R.id.group_main, !mDesignIng);
		menu.setGroupVisible(R.id.group_design, mDesignIng);
		return true;
	}

	private void hide_softkey() {
		mShow.hideSoftKey(bHideSoftKey);
	}

	private void lock_screen() {
		// 如果还没有横竖屏的设置,按宽高比计算
		if (bLockScreen
				&& (mRequestOritation == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)) {
			final Display display = getWindowManager().getDefaultDisplay();
			if (display.getWidth() > display.getHeight()) {
				mRequestOritation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
			} else {
				mRequestOritation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
			}
		}

		setRequestedOrientation(bLockScreen ? mRequestOritation
				: ActivityInfo.SCREEN_ORIENTATION_USER);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	@Override
	public void onBackPressed() {
		if (mDesignIng) {
			applyDesign(false);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (Input.Running) {
			if (KeyEvent.KEYCODE_ENTER == keyCode
					|| (KeyEvent.KEYCODE_0 <= keyCode && keyCode <= KeyEvent.KEYCODE_Z)) {
				Input.onKey(keyCode, event);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {

		super.onConfigurationChanged(newConfig);

		if (!bLockScreen) {
			if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
				mRequestOritation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
			} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
				mRequestOritation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
			}

			if (mDesignIng) {
				applyDesign(false);
			}
		}

		// if (this.getResources().getConfiguration().orientation ==
		// Configuration.ORIENTATION_LANDSCAPE) {
		// } else if (this.getResources().getConfiguration().orientation ==
		// Configuration.ORIENTATION_PORTRAIT) {
		// }
	}

	@Override
	public void EnterNewName(final int type) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				Context ctx = GmudActivity.this;
				AlertDialog.Builder alert = new AlertDialog.Builder(ctx);

				alert.setTitle("名字：");
				// alert.setMessage("");

				// Set an EditText view to get user input
				final EditText input = new EditText(ctx);
				input.setHint("[无名]");
				input.setSingleLine(true);
				input.setOnKeyListener(new View.OnKeyListener() {
					@Override
					public boolean onKey(View v, int keyCode, KeyEvent event) {
						if (keyCode == KeyEvent.KEYCODE_ENTER
								&& event.getAction() == KeyEvent.ACTION_UP) {
							setNewName(input.getText().toString());
						}
						return false;
					}
				});
				alert.setView(input);

				alert.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								setNewName(input.getText().toString());
							}
						});
				alert.show();
			}
		});
	}

	private void setNewName(String name) {
		if (name.length() == 0)
			name = "[无名]";
		Gmud.SetNewName(name);

	}

	private void tryDesign() {
		if (!mDesignIng) {
			mDesignIng = true;
			mShow.StartDesign(mDesign);
		}
	}

	private void applyDesign(boolean isApply) {
		if (mDesignIng) {
			mDesignIng = false;
			mShow.ApplyDesign(isApply);
		}
	}

	@Override
	public void UpdateTime(long minutes, int seconds) {
		mMinutes = minutes;
		mSeconds = seconds;
		if (bShowTime) {
			mHandler.removeMessages(MSG_UPDATE_PLAY_TIME);
			mHandler.sendEmptyMessage(MSG_UPDATE_PLAY_TIME);
		}
	}
}
