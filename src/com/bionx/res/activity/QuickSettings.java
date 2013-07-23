package com.bionx.res.activity;

import com.bionx.res.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class QuickSettings extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.quick_settings);
		}
	}