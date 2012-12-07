package com.michaelkrauklis.android.microlife.preferences;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.michaelkrauklis.android.microlife.MicroLife;
import com.michaelkrauklis.android.microlife.R;
import com.robobunny.SeekBarPreference;

public class Preferences extends PreferenceActivity {
	public static final String TAG = Preferences.class.getName();

	public static final String MICROBE_SIZE = "microbeSize";// 1-100
	public static final String TERA_DELTA = "teraDelta";// 0-1
	public static final String MICROBE_VORACITY = "microbeVoracity";// 0-1
	public static final String MICROBE_DURABILITY = "microbeDurability"; // 0-1
	public static final String MATING_THRESHOLD = "matingThreshold";// 0-4
	public static final String COST_OF_LIFE = "costOfLife";// 0-1
	public static final String PHAGE_VORACITY = "phageVoracity";// 0-1
	public static final String PHAGE_DURABILITY = "phageDurability"; // 0-1
	public static final String INFECTION_DEATH_THRESHOLD = "infectionDeathThreshold";// 0-1
	public static final String NEW_INFECTION_STRENGTH = "newInfectionStrength"; // 0-1
	public static final String UPDATE_SPEED = "updateSpeed"; // 0-1
	public static final String IS_MICROBE_MATING_RANDOM = "isMicrobeMatingRandom"; // true/false

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initInteger(MICROBE_SIZE, 3);
		initPercentage(TERA_DELTA, 0.11f);
		initPercentage(MICROBE_VORACITY, 0.15f);
		initPercentage(MICROBE_DURABILITY, 0.5f);
		initPercentage(MATING_THRESHOLD, 0.39f);
		initPercentage(COST_OF_LIFE, 0.15f);
		initPercentage(PHAGE_VORACITY, 0.33f);
		initPercentage(PHAGE_DURABILITY, 0.74f);
		initPercentage(INFECTION_DEATH_THRESHOLD, 0.5f);
		initPercentage(NEW_INFECTION_STRENGTH, 0.25f);
		initPercentage(UPDATE_SPEED, 0.98f);
		initBoolean(IS_MICROBE_MATING_RANDOM, true);

		addPreferencesFromResource(R.xml.preferences);

		// Get the custom preference
		addIntegerListener(MICROBE_SIZE);
		addPercentageListener(TERA_DELTA);
		addPercentageListener(MICROBE_VORACITY);
		addPercentageListener(MICROBE_DURABILITY);
		addPercentageListener(MATING_THRESHOLD);
		addPercentageListener(COST_OF_LIFE);
		addPercentageListener(PHAGE_VORACITY);
		addPercentageListener(PHAGE_DURABILITY);
		addPercentageListener(INFECTION_DEATH_THRESHOLD);
		addPercentageListener(NEW_INFECTION_STRENGTH);
		addPercentageListener(UPDATE_SPEED);
		addBooleanListener(IS_MICROBE_MATING_RANDOM);

	}

	private void initBoolean(String name, boolean defaultValue) {
		SharedPreferences customSharedPreference = PreferenceManager
				.getDefaultSharedPreferences(this);

		setBooleanSharedPreference(customSharedPreference, name,
				getBooleanSharedPreference(this, name, defaultValue));
	}

	private void initPercentage(String name, Float defaultValue) {
		SharedPreferences customSharedPreference = PreferenceManager
				.getDefaultSharedPreferences(this);

		setIntegerSharedPreference(
				customSharedPreference,
				name,
				(int) (getFloatSharedPreference(this, name, defaultValue) * 100));
	}

	@SuppressWarnings("unused")
	private void initInteger(String name, Integer defaultValue) {
		SharedPreferences customSharedPreference = PreferenceManager
				.getDefaultSharedPreferences(this);

		setIntegerSharedPreference(customSharedPreference, name,
				getIntegerSharedPreference(this, name, defaultValue));
	}

	@SuppressWarnings("unused")
	private void initFloat(String name, Float defaultValue) {
		SharedPreferences customSharedPreference = PreferenceManager
				.getDefaultSharedPreferences(this);

		setFloatSharedPreference(customSharedPreference, name,
				getFloatSharedPreference(this, name, defaultValue));
	}

	private void addIntegerListener(final String name) {
		SeekBarPreference pref = (SeekBarPreference) findPreference(name);
		pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				setIntegerSharedPreference(Preferences.this, name,
						(Integer) newValue);
				return true;
			}
		});
	}

	private void addBooleanListener(final String name) {
		CheckBoxPreference pref = (CheckBoxPreference) findPreference(name);
		pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				setBooleanSharedPreference(Preferences.this, name,
						(Boolean) newValue);
				return true;
			}
		});
	}

	private void addPercentageListener(final String name) {
		SeekBarPreference pref = (SeekBarPreference) findPreference(name);
		pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				setFloatSharedPreference(Preferences.this, name,
						(Integer) newValue / 100f);
				return true;
			}
		});
	}

	public static final void setBooleanSharedPreference(ContextWrapper context,
			String name, Boolean value) {
		SharedPreferences customSharedPreference = context
				.getSharedPreferences(MicroLife.class.getName(),
						Activity.MODE_PRIVATE);
		setBooleanSharedPreference(customSharedPreference, name, value);
	}

	private static void setBooleanSharedPreference(
			SharedPreferences customSharedPreference, String name, Boolean value) {
		SharedPreferences.Editor editor = customSharedPreference.edit();
		editor.putBoolean(name, value);
		editor.commit();
	}

	public static final Boolean getBooleanSharedPreference(
			ContextWrapper context, String name, Boolean defaultValue) {
		SharedPreferences customSharedPreference = context
				.getSharedPreferences(MicroLife.class.getName(),
						Activity.MODE_PRIVATE);
		return customSharedPreference.getBoolean(name, defaultValue);
	}

	public static final void setIntegerSharedPreference(ContextWrapper context,
			String name, Integer value) {
		SharedPreferences customSharedPreference = context
				.getSharedPreferences(MicroLife.class.getName(),
						Activity.MODE_PRIVATE);
		setIntegerSharedPreference(customSharedPreference, name, value);
	}

	private static void setIntegerSharedPreference(
			SharedPreferences customSharedPreference, String name, Integer value) {
		SharedPreferences.Editor editor = customSharedPreference.edit();
		editor.putInt(name, value);
		editor.commit();
	}

	public static final void setIntegerSharedPreference(ContextWrapper context,
			String name, Integer value, Integer min, Integer max) {
		if (value < min || value > max) {
			throw new IllegalArgumentException("Value " + value
					+ " for property " + name + " is out of bounds " + min
					+ "-" + max);
		}
		setIntegerSharedPreference(context, name, value);
	}

	public static final Integer getIntegerSharedPreference(
			ContextWrapper context, String name, Integer defaultValue) {
		SharedPreferences customSharedPreference = context
				.getSharedPreferences(MicroLife.class.getName(),
						Activity.MODE_PRIVATE);
		return customSharedPreference.getInt(name, defaultValue);
	}

	public static final void setFloatSharedPreference(ContextWrapper context,
			String name, Float value, Float min, Float max) {
		if (value < min || value > max) {
			throw new IllegalArgumentException("Value " + value
					+ " for property " + name + " is out of bounds " + min
					+ "-" + max);
		}
		setFloatSharedPreference(context, name, value);
	}

	public static final void setFloatSharedPreference(ContextWrapper context,
			String name, Float value) {
		SharedPreferences customSharedPreference = context
				.getSharedPreferences(MicroLife.class.getName(),
						Activity.MODE_PRIVATE);
		setFloatSharedPreference(customSharedPreference, name, value);
	}

	public static final void setFloatSharedPreference(
			SharedPreferences customSharedPreference, String name, Float value) {
		SharedPreferences.Editor editor = customSharedPreference.edit();
		editor.putFloat(name, value);
		editor.commit();
	}

	public static final Float getFloatSharedPreference(ContextWrapper context,
			String name, Float defaultValue) {
		SharedPreferences customSharedPreference = context
				.getSharedPreferences(MicroLife.class.getName(),
						Activity.MODE_PRIVATE);
		return customSharedPreference.getFloat(name, defaultValue);
	}

	public static String serializePreferences(Context context) {
		StringBuilder retval = new StringBuilder();

		SharedPreferences sharedPreferences = context.getSharedPreferences(
				MicroLife.class.getName(), Context.MODE_PRIVATE);

		retval.append(MICROBE_SIZE);
		retval.append(":");
		retval.append(sharedPreferences.getInt(Preferences.MICROBE_SIZE, 3));
		retval.append("\n");

		retval.append(UPDATE_SPEED);
		retval.append(":");
		retval.append(sharedPreferences.getFloat(Preferences.UPDATE_SPEED,
				0.98f));
		retval.append("\n");

		retval.append(TERA_DELTA);
		retval.append(":");
		retval.append(sharedPreferences.getFloat(Preferences.TERA_DELTA, 0.03f));
		retval.append("\n");

		retval.append(MICROBE_VORACITY);
		retval.append(":");
		retval.append(sharedPreferences.getFloat(Preferences.MICROBE_VORACITY,
				0.1f));
		retval.append("\n");

		retval.append(MICROBE_DURABILITY);
		retval.append(":");
		retval.append(sharedPreferences.getFloat(
				Preferences.MICROBE_DURABILITY, 0.5f));
		retval.append("\n");

		retval.append(MATING_THRESHOLD);
		retval.append(":");
		retval.append(sharedPreferences.getFloat(Preferences.MATING_THRESHOLD,
				0.7f));
		retval.append("\n");

		retval.append(COST_OF_LIFE);
		retval.append(":");
		retval.append(sharedPreferences.getFloat(Preferences.COST_OF_LIFE,
				0.25f));
		retval.append("\n");

		retval.append(PHAGE_VORACITY);
		retval.append(":");
		retval.append(sharedPreferences.getFloat(Preferences.PHAGE_VORACITY,
				0.25f));
		retval.append("\n");

		retval.append(PHAGE_DURABILITY);
		retval.append(":");
		retval.append(sharedPreferences.getFloat(Preferences.PHAGE_DURABILITY,
				0.9f));
		retval.append("\n");

		retval.append(INFECTION_DEATH_THRESHOLD);
		retval.append(":");
		retval.append(sharedPreferences.getFloat(
				Preferences.INFECTION_DEATH_THRESHOLD, 0.5f));
		retval.append("\n");

		retval.append(NEW_INFECTION_STRENGTH);
		retval.append(":");
		retval.append(sharedPreferences.getFloat(
				Preferences.NEW_INFECTION_STRENGTH, 0.25f));
		retval.append("\n");

		retval.append(IS_MICROBE_MATING_RANDOM);
		retval.append(":");
		retval.append(sharedPreferences.getBoolean(
				Preferences.IS_MICROBE_MATING_RANDOM, true));
		retval.append("\n");

		return retval.toString();
	}

	public static String deserializePreferences(Context context, String text) {
		StringBuilder retval = new StringBuilder();

		SharedPreferences sharedPreferences = context.getSharedPreferences(
				MicroLife.class.getName(), Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();

		int intVal;
		float floatVal;
		boolean booleanVal;

		try {
			if (text != null) {
				String lines[] = text.split("\\r?\\n");
				try {
					for (String line : lines) {
						String[] tokens = line.split(":");
						if (tokens.length == 2) {
							if (MICROBE_SIZE.equals(tokens[0])) {
								intVal = Integer.parseInt(tokens[1]);
								checkValue(intVal, 3, 100);
								editor.putInt(Preferences.MICROBE_SIZE, intVal);
							} else if (UPDATE_SPEED.equals(tokens[0])) {
								floatVal = Float.parseFloat(tokens[1]);
								checkValue(floatVal, 0, 1);
								editor.putFloat(Preferences.UPDATE_SPEED,
										floatVal);
							} else if (TERA_DELTA.equals(tokens[0])) {
								floatVal = Float.parseFloat(tokens[1]);
								checkValue(floatVal, 0, 1);
								editor.putFloat(Preferences.TERA_DELTA,
										floatVal);
							} else if (MICROBE_VORACITY.equals(tokens[0])) {
								floatVal = Float.parseFloat(tokens[1]);
								checkValue(floatVal, 0, 1);
								editor.putFloat(Preferences.MICROBE_VORACITY,
										floatVal);
							} else if (MICROBE_DURABILITY.equals(tokens[0])) {
								floatVal = Float.parseFloat(tokens[1]);
								checkValue(floatVal, 0, 1);
								editor.putFloat(Preferences.MICROBE_DURABILITY,
										floatVal);
							} else if (MATING_THRESHOLD.equals(tokens[0])) {
								floatVal = Float.parseFloat(tokens[1]);
								checkValue(floatVal, 0, 4);
								editor.putFloat(Preferences.MATING_THRESHOLD,
										floatVal);
							} else if (COST_OF_LIFE.equals(tokens[0])) {
								floatVal = Float.parseFloat(tokens[1]);
								checkValue(floatVal, 0, 1);
								editor.putFloat(Preferences.COST_OF_LIFE,
										floatVal);
							} else if (PHAGE_VORACITY.equals(tokens[0])) {
								floatVal = Float.parseFloat(tokens[1]);
								checkValue(floatVal, 0, 1);
								editor.putFloat(Preferences.PHAGE_VORACITY,
										floatVal);
							} else if (PHAGE_DURABILITY.equals(tokens[0])) {
								floatVal = Float.parseFloat(tokens[1]);
								checkValue(floatVal, 0, 1);
								editor.putFloat(Preferences.PHAGE_DURABILITY,
										floatVal);
							} else if (INFECTION_DEATH_THRESHOLD
									.equals(tokens[0])) {
								floatVal = Float.parseFloat(tokens[1]);
								checkValue(floatVal, 0, 1);
								editor.putFloat(
										Preferences.INFECTION_DEATH_THRESHOLD,
										floatVal);
							} else if (NEW_INFECTION_STRENGTH.equals(tokens[0])) {
								floatVal = Float.parseFloat(tokens[1]);
								checkValue(floatVal, 0, 1);
								editor.putFloat(
										Preferences.NEW_INFECTION_STRENGTH,
										floatVal);
							} else if (IS_MICROBE_MATING_RANDOM
									.equals(tokens[0])) {
								booleanVal = Boolean.parseBoolean(tokens[1]);
								editor.putBoolean(
										Preferences.IS_MICROBE_MATING_RANDOM,
										booleanVal);
							} else {
								addMessage(retval, "Unknown key [" + tokens[0]
										+ "] for line " + line);
							}
						} else {
							addMessage(retval, "Invalid line: " + line);
						}
					}

					editor.commit();

					retval.append("Successfully Applied");
				} catch (Exception e) {
					retval.append(e.getMessage());
				}
			} else {
				retval.append("No input provided");
			}
		} catch (Exception e) {
			retval.append(e.getMessage());
		}

		return retval.toString();
	}

	private static void checkValue(float val, float min, float max) {
		if (val < min || val > max) {
			throw new IllegalArgumentException("Value " + val
					+ " outside of bounds " + min + "-" + max);
		}
	}

	private static void addMessage(StringBuilder retval, String message) {
		retval.append(message);
		retval.append("\n");
	}
}
