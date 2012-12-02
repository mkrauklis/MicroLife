package com.michaelkrauklis.android.microlife.preferences;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

import com.michaelkrauklis.android.microlife.MicroLife;
import com.michaelkrauklis.android.microlife.R;
import com.robobunny.SeekBarPreference;

public class Preferences extends PreferenceActivity {
	public static final String MICROBE_SIZE = "microbeSize";// 1-100
	public static final String TERA_DELTA = "teraDelta";// 0-1
	public static final String MICROBE_VORACITY = "microbeVoracity";// 0-1
	public static final String MATING_THRESHOLD = "matingThreshold";// 0-4
	public static final String COST_OF_LIFE = "costOfLife";// 0-1
	public static final String PHAGE_VORACITY = "phageVoracity";// 0-1
	public static final String INFECTION_DEATH_THRESHOLD = "infectionDeathThreshold";// 0-1
	public static final String PHAGE_DURABILITY = "phageDurability"; // 0-1
	public static final String NEW_INFECTION_STRENGTH = "newInfectionStrength"; // 0-1

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		// Get the custom preference
		SeekBarPreference microbeSize = (SeekBarPreference) findPreference(MICROBE_SIZE);
		microbeSize
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						SharedPreferences customSharedPreference = getSharedPreferences(
								MicroLife.class.getName(),
								Activity.MODE_PRIVATE);
						SharedPreferences.Editor editor = customSharedPreference
								.edit();
						editor.putInt(MICROBE_SIZE, (Integer) newValue);
						editor.commit();
						return true;
					}

				});
		addPercentageListener(TERA_DELTA);
		addPercentageListener(MICROBE_VORACITY);
		addPercentageListener(MATING_THRESHOLD);
		addPercentageListener(COST_OF_LIFE);
		addPercentageListener(PHAGE_VORACITY);
		addPercentageListener(INFECTION_DEATH_THRESHOLD);
		addPercentageListener(PHAGE_DURABILITY);
		addPercentageListener(NEW_INFECTION_STRENGTH);
	}

	private void addPercentageListener(final String name) {
		((SeekBarPreference) findPreference(name))
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						SharedPreferences customSharedPreference = getSharedPreferences(
								MicroLife.class.getName(),
								Activity.MODE_PRIVATE);
						SharedPreferences.Editor editor = customSharedPreference
								.edit();
						editor.putFloat(name, (Integer) newValue / 100f);
						editor.commit();
						return true;
					}
				});
	}
}
