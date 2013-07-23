package com.bionx.res.ui;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.bionx.res.R;
import com.bionx.res.activity.CPUMain;
import com.bionx.res.activity.EditPreferences;
import com.bionx.res.activity.StatsActivity;

// Bionx Tools Tabs
@SuppressWarnings("deprecation")
public class BionxTabs extends TabActivity {

	public static final String tab1 = "Bionx";
	public static final String tab2 = "OC_UC Table";
	public static final String tab3 = "CPU";

	// Generate Tabs
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_tab);
		TabHost th = (TabHost) findViewById(android.R.id.tabhost);
		// Lets ID the tabs
		TabSpec ts1 = th.newTabSpec("tab_id_1");
		TabSpec ts2 = th.newTabSpec("tab_id_2");
		TabSpec ts3 = th.newTabSpec("tab_id_3");
		// Launch Activities on each tab selected
		ts1.setIndicator(tab1).setContent(new Intent(this, EditPreferences.class));
		ts2.setIndicator(tab2).setContent(new Intent(this, CPUMain.class));
		ts3.setIndicator(tab3).setContent(new Intent(this, StatsActivity.class));
		// Identifiers
		th.addTab(ts1);
		th.addTab(ts2);
		th.addTab(ts3);
	}
}