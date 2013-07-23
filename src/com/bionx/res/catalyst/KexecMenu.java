package com.bionx.res.catalyst;

import com.bionx.res.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class KexecMenu extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.kexec);
		}
	}
