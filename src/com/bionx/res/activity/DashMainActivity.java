package com.bionx.res.activity;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.bionx.res.R;
import com.bionx.res.about.InfoCenter;
import com.bionx.res.ui.BionxTabs;

public class DashMainActivity extends Activity {
	
	TextView txtFreeInt, txtAvaInt, txtTotalInt, txtFreeExt, txtAvaExt,
			txtTotalExt;
	Process process = null;
	// URL Buttons
	private Button mBtnURL1;
	private Button mBtnURL2;
	private Button mBtnURL3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			process = Runtime.getRuntime().exec("su");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		setContentView(R.layout.main);
		TextView textView = (TextView) findViewById(R.id.device);
		StringBuilder string = new StringBuilder("");

		string.append("Model : " + Build.MODEL + "\n");
		string.append("Android Version : " + Build.VERSION.RELEASE + "\n");
		string.append("Rom : " + Build.DISPLAY + "\n");
		string.append("Host : " + Build.HOST + "\n");
		string.append("Radio : " + Build.getRadioVersion() + "\n");
		string.append("CPUs : " + Build.CPU_ABI + "\n");
		string.append("Bootloader : " + Build.BOOTLOADER + "\n");

		textView.setText(string);

		txtAvaInt = (TextView) findViewById(R.id.txtAvaInt);
		txtAvaInt.setText("" + availableInternalStorage() + " mb");
		txtTotalInt = (TextView) findViewById(R.id.txtTotalInt);
		txtTotalInt.setText("" + totalInternalStorage() + " mb");
		txtAvaExt = (TextView) findViewById(R.id.txtAvaExt);
		txtAvaExt.setText("" + availableExternalStorage() + " mb");
		txtTotalExt = (TextView) findViewById(R.id.txtTotalExt);
		txtTotalExt.setText("" + totalExternalStorage() + " mb");
		// Web URL Buttons
		mBtnURL1 = (Button) findViewById(R.id.url1);
		mBtnURL2 = (Button) findViewById(R.id.url2);
		mBtnURL3 = (Button) findViewById(R.id.url3);
		// Launch URLs via Intents
		mBtnURL1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent url1 = new Intent("android.intent.action.VIEW", Uri
						.parse("http://www.xda-developers.com/"));
				startActivity(url1);
			}
		});
		mBtnURL2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent url2 = new Intent("android.intent.action.VIEW", Uri
						.parse("http://phandroid.com/"));
				startActivity(url2);
			}
		});
		mBtnURL3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent webview = new Intent(getBaseContext(),
						BionxWebView.class);
				startActivity(webview);
			}
		});

		ActivityManager am = (ActivityManager) this
				.getSystemService(ACTIVITY_SERVICE);
		@SuppressWarnings("rawtypes")
		List rs = am.getRunningAppProcesses();
		for (int i = 0; i > rs.size(); i++) {
			@SuppressWarnings("unused")
			ActivityManager.RunningServiceInfo rsi = (RunningServiceInfo) rs
					.get(i);
			@SuppressWarnings("unused")
			ActivityManager.MemoryInfo mi = (MemoryInfo) rs.get(i);
		}
	}

	private long availableInternalStorage() {
		StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
		long bytesAvailable = (long) stat.getAvailableBlocks()
				* (long) stat.getBlockSize();
		return bytesAvailable / 1048576;
	}

	@SuppressWarnings("unused")
	private long freeInternalStorage() {
		StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
		long bytesAvailable = (long) stat.getFreeBlocks()
				* (long) stat.getBlockSize();
		return bytesAvailable / 1048576;
	}

	private long totalInternalStorage() {
		StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
		long bytesAvailable = (long) stat.getBlockCount()
				* (long) stat.getBlockSize();
		return bytesAvailable / 1048576;
	}

	private long totalExternalStorage() {
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
				.getPath());
		long bytesAvailable = (long) stat.getBlockCount()
				* (long) stat.getBlockSize();
		return bytesAvailable / 1048576;
	}

	@SuppressWarnings("unused")
	private long freeExternalStorage() {
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
				.getPath());
		long bytesAvailable = (long) stat.getFreeBlocks()
				* (long) stat.getBlockSize();
		return bytesAvailable / 1048576;
	}

	private long availableExternalStorage() {
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
				.getPath());
		long bytesAvailable = (long) stat.getAvailableBlocks()
				* (long) stat.getBlockSize();
		return bytesAvailable / 1048576;
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