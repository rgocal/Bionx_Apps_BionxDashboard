package com.bionx.res.ui;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.bionx.res.R;
import com.bionx.res.about.InfoCenter;
import com.bionx.res.activity.Catalyst;
import com.bionx.res.activity.DashMainActivity;
import com.bionx.res.activity.QuickSettings;
import com.sbstrm.appirater.Appirater;

// Bionx Tools Tabs
@SuppressWarnings("deprecation")
public class MainDashboard extends TabActivity {

	public static final String tab1 = "Dashboard";
	public static final String tab2 = "Settings";
	public static final String tab3 = "Catalyst";

	// Generate Tabs
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Initialize Appirater
		Appirater.appLaunched(this);
	       //Dashboard Layout onCreate
		setContentView(R.layout.main_dashboard_tabs);
		TabHost th = (TabHost) findViewById(android.R.id.tabhost);
		// Lets ID the tabs
		TabSpec ts1 = th.newTabSpec("tab_id_1");
		TabSpec ts2 = th.newTabSpec("tab_id_2");
		TabSpec ts3 = th.newTabSpec("tab_id_3");
		// Launch Activities on each tab selected
		ts1.setIndicator(tab1)
		.setContent(new Intent(this, DashMainActivity.class));
		ts2.setIndicator(tab2)
		.setContent(new Intent(this, QuickSettings.class));
		ts3.setIndicator(tab3)
		.setContent(new Intent(this, Catalyst.class));
		// Identifiers
		th.addTab(ts1);
		th.addTab(ts2);
		th.addTab(ts3);
	}
	
	//Actionbar Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.dash_menu, menu);
      return true;
    } 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
      case R.id.options_menu:
    	  final Context context = this;
    	  Intent options_menu = new Intent(context, BionxTabs.class);
			startActivity(options_menu);
			return true;
      case R.id.about:
    	  final Context context1 = this;
    	  Intent info = new Intent(context1, InfoCenter.class);
			startActivity(info);
			return true;
      }
      return false;
    }
}