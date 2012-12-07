package com.michaelkrauklis.android.microlife;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.michaelkrauklis.android.microlife.preferences.Help;
import com.michaelkrauklis.android.microlife.preferences.Preferences;
import com.michaelkrauklis.android.microlife.preferences.PreferencesManagement;
import com.michaelkrauklis.android.microlife.run.MicroLifeRunThread;
import com.michaelkrauklis.android.microlife.run.MicroLifeRunThread.DrawType;
import com.michaelkrauklis.android.view.ClickableGLSurfaceView;

public class MicroLife extends Activity {
	private static final int CLEAR = 0;
	private static final int PREFERENCES = 1;
	private static final int MANAGE_PREFERENCES = 2;
	private static final int DRAW_BACTERIA = 3;
	private static final int DRAW_PHAGE = 4;
	private static final int DRAW_FOOD = 5;
	private static final int DRAW_QUARANTINE = 6;
	private static final int HELP = 7;

	private GLSurfaceView view;
	private MicroLifeRunThread thread;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		setContentView(R.layout.landing_page);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		if (thread == null) {
			menu.add(Menu.NONE, CLEAR, 0, "New Game");
			menu.add(Menu.NONE, PREFERENCES, 1, "Preferences");
			menu.add(Menu.NONE, MANAGE_PREFERENCES, 2, "Manage Preferences");
			menu.add(Menu.NONE, HELP, 3, "Help");
		} else {
			menu.add(Menu.NONE, CLEAR, 7, "Reset");
			menu.add(Menu.NONE, PREFERENCES, 1, "Preferences");
			menu.add(Menu.NONE, MANAGE_PREFERENCES, 8, "Manage Preferences");
			menu.add(Menu.NONE, DRAW_BACTERIA, 3, "Draw Microbe");
			menu.add(Menu.NONE, DRAW_PHAGE, 4, "Draw Phage");
			menu.add(Menu.NONE, DRAW_FOOD, 5, "Draw Food");
			menu.add(Menu.NONE, DRAW_QUARANTINE, 6, "Draw Quarantine");
			menu.add(Menu.NONE, HELP, 7, "Help");
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case CLEAR:
			if (view == null) {
				thread = new MicroLifeRunThread(this);
				view = new ClickableGLSurfaceView(this, thread);
				view.setRenderer(thread);
				view.setKeepScreenOn(true);
				setContentView(view);

				thread.initialize();
				thread.unPause();
			} else {
				thread.reinit();
			}
			break;
		case PREFERENCES:
			Intent preferencesActivity = new Intent(getBaseContext(),
					Preferences.class);
			startActivity(preferencesActivity);
			break;
		case MANAGE_PREFERENCES:
			Intent managePreferencesActivity = new Intent(getBaseContext(),
					PreferencesManagement.class);
			startActivity(managePreferencesActivity);
			break;
		case HELP:
			Intent helpActivity = new Intent(getBaseContext(), Help.class);
			startActivity(helpActivity);
			break;
		case DRAW_BACTERIA:
			thread.setDrawType(DrawType.BACTERIA);
			break;
		case DRAW_PHAGE:
			thread.setDrawType(DrawType.PHAGE);
			break;
		case DRAW_FOOD:
			thread.setDrawType(DrawType.FOOD);
			break;
		case DRAW_QUARANTINE:
			thread.setDrawType(DrawType.QUARANTINE);
			break;
		}
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (view != null) {
			view.onResume();
			thread.unPause();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (view != null) {
			view.onPause();
			thread.pause();
		}
	}

	/**
	 * Standard window-focus override. Notice focus lost so we can pause on
	 * focus lost. e.g. user switches to take a call.
	 */
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		if (thread != null && !thread.isInitial()) {
			if (hasWindowFocus) {
				onResume();
			} else {
				onPause();
			}
		}
	}
}