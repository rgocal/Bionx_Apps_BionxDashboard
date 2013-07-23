package com.bionx.res.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.InputFilter;

import com.bionx.res.R;
import com.bionx.res.extra.Constants;
import com.bionx.res.extra.PatternReplacerInputFilter;

public class PreferencesActivity extends PreferenceActivity implements
		OnPreferenceChangeListener {

	private EditTextPreference prefPattern;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.cpu_preferences);

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		prefPattern = (EditTextPreference) findPreference(Constants.PREF_VIBRATE_PATTERN);
		prefPattern.setOnPreferenceChangeListener(this);
		prefPattern.setSummary(prefs.getString(Constants.PREF_VIBRATE_PATTERN,
				""));
		prefPattern.getEditText()
				.setFilters(
						new InputFilter[] { new PatternReplacerInputFilter(
								"[^0-9,]") });
	}

	@Override
	public boolean onPreferenceChange(final Preference preference,
			final Object newValue) {
		if (preference == prefPattern) {
			preference.setSummary((String) newValue);
			return true;
		}
		return false;
	}
}
