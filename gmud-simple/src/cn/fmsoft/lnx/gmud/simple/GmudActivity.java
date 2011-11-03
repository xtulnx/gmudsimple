/**
 * Copyright (C) 2011, FMSoft.GMUD.
 * 
 * @author nxliao
 */
package cn.fmsoft.lnx.gmud.simple;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import cn.fmsoft.lnx.gmud.simple.core.Gmud;
import cn.fmsoft.lnx.gmud.simple.core.Input;

public class GmudActivity extends Activity {
//	final static int MENU_HOOKGAME = 0;
//	final static int MENU_EXITAPPLICATION = 1;
//	final static int MENU_ABOUT = 2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.main);

//		new Gmud(this);
		Gmud.bind(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		Gmud.unbind(this);

		if (!Gmud.Running) {
			System.exit(0);
		}
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
		return true;
//		
//		menu.add(0, MENU_HOOKGAME, 0, "Hook");
//		menu.add(0, MENU_EXITAPPLICATION, 0, "Exit Game");
//		menu.add(0, MENU_ABOUT, 0, "About");
//		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// case MENU_EXITAPPLICATION:
		case R.id.item_exit:
			Gmud.exit();
			break;

		case R.id.item_hook:
			break;

		case R.id.item_about:
			Dialog dialog = new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.btn_star)
					.setTitle(R.string.title)
					.setMessage(R.string.about)
					.setPositiveButton("OK", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					}).create();
			dialog.show();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
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

//	@Override
//	public void onConfigurationChanged(Configuration newConfig) {
//
//		super.onConfigurationChanged(newConfig);
//
//		// if (this.getResources().getConfiguration().orientation ==
//		// Configuration.ORIENTATION_LANDSCAPE) {
//		// // �������Ҫ����Ĵ���
//		// } else if (this.getResources().getConfiguration().orientation ==
//		// Configuration.ORIENTATION_PORTRAIT) {
//		// // ��������Ҫ����Ĵ���
//		// }
//	}
}
