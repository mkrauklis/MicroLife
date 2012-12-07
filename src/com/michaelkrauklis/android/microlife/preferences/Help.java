package com.michaelkrauklis.android.microlife.preferences;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.michaelkrauklis.android.microlife.R;

public class Help extends Activity {
	public static final String TAG = Help.class.getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.readme_page);

		TextView textView = (TextView) findViewById(R.id.helpText);
		textView.setMovementMethod(new ScrollingMovementMethod());
		textView.setText(Html
				.fromHtml("<html><body>"
						+ "<h1>Summary</h1><p>The premise of the gams is simple. You have food, microbes, and phages, all competing in a petri dish based on the preferences for life you specify. Food grows, microbes eath the food and multiply, phages attack the microbes and in turn multiply themselves.</p>"
						+ "<h1>Quick Start</h1><p>Click <b>New Game</b>. Then once the screen is green draw on the screen. You're drawing microbes! You should see them grow and multiply, so long as you have favorable preferences. Then click Menu->Draw Phage. Click on the microbes (the blue). Zinga! You've got yourself an infection. Watch the microbes and phages battle for domination!!</p>"
						+"<h1>Colors</h1><p><b>Green</b> - Food; this is what the microbes eat, and will grow at a rate you specify in your properties.</p><p><b>Blue</b> - Microbes; these little guys eat the food and get healthier as they eat (so long as they're durable). If they're healthy enough and have some friends neighboring them they'll be able to reproduce into an adjacent unoccupied spot.</p><p><b>Red</b> - Phage; a.k.a: the Dreaded Phage, dun dun dunnnn. These baddies infect the microbes and, if the infection gets strong enough it kills its host microbe and spreads to all adjacent spots. Even if the phage can't kill its host microbe, it weakens it. Phages, however, can't last long without a host. It all depends on how durable they are.</p><p><b>White</b> - Quarantine; simply think of this as an impenetrable wall that neither microbs nor phages can get through (unless it's got holes). These can be used for all sorts of purposes, such as creating mazes or creating barriers to help microbes escape phages.</p>"
						+"<h1></h1><p></p>"
						+ "<h1>Preferences</h1><h2>Managing Preferences<h2><p>The manage preferences page allows you to save and load your preferences to a string. You can then save these preferences elsewhere for easy restoration of your favorite configurations.</p><p><b>Apply Preferences</b> - Applies the preferences from the editor to the game.</p><p><b>Copy to Clipboard</b> - Coppies from the editor to the clipboard. You can then paste into another application to save your configuration.</p><p><b>Paste from Clipboard</b> - Pastes from the clipboard to the editor. You can then edit and/or apply this configuration.</p></ul>"
						+ "</body></html>"));
	}
}
