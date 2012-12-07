package com.michaelkrauklis.android.microlife.preferences;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.michaelkrauklis.android.microlife.R;

public class PreferencesManagement extends Activity {
	public static final String TAG = PreferencesManagement.class.getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.preferences_management_page);

		final TextView messages = (TextView) findViewById(R.id.prefManMessages);

		final EditText text = (EditText) findViewById(R.id.prefManEdit);

		text.setText(Preferences
				.serializePreferences(PreferencesManagement.this));

		final Button button = (Button) findViewById(R.id.prefManLoadButton);
		button.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				messages.setText(Preferences.deserializePreferences(
						PreferencesManagement.this, text.getText().toString()));
			}
		});

		final Button copyButton = (Button) findViewById(R.id.prefManCopyButton);
		copyButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				clipboard.setText(text.getText().toString());
			}
		});

		final Button pasteButton = (Button) findViewById(R.id.prefManPasteButton);
		pasteButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				text.setText(clipboard.getText());
			}
		});
	}
}
