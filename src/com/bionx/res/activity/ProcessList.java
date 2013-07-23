package com.bionx.res.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.app.ActivityManager;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bionx.res.R;
import com.bionx.res.helpers.ApplicationHelper;
import com.bionx.res.helpers.HistoryHelper;
import com.bionx.res.helpers.ProcessHelper;

public class ProcessList extends ListActivity {
	private ProcessHelper processHelper;
	private ApplicationHelper appHelper;
	private HistoryHelper historyHelper;
	private ProgressDialog progressDialog;
	private static final int MENU_QUIT = 	0x04;

	private TextView infoLabel;

	public static final String CLASSTAG = ProcessList.class.getSimpleName();
	private SimpleAdapter listAdapter;

	private List<Map<String, Object>> processInfos;

	private static final int LOAD_PROCESS_INFOS = 1;

	private List<String> killedApps = Collections.emptyList();

	private List<String> deadKilledApps = Collections.emptyList();

	public static final String LOG_TAG = "UniqTask";

	private final Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == LOAD_PROCESS_INFOS) {
				if (progressDialog != null)
					progressDialog.dismiss();

				progressDialog = null;
				if ((processInfos == null) || processInfos.isEmpty()) {
					setListAdapter(null);
					listItemSelected = null;
					return;
				} else {
					Log.w(LOG_TAG, "There are " + processInfos.size()
							+ " apps running now.");
					listItemSelected = new boolean[processInfos.size()];
					List<String> deadThisTime = new ArrayList<String>(
							killedApps);

					for (int i = 0; i < processInfos.size(); i++) {
						String pkgName = (String) processInfos.get(i).get(
								ProcessHelper.PKG_NAME);
						if (killedApps.contains(pkgName)) {
							listItemSelected[i] = true;
							deadThisTime.remove(pkgName);
						}
					}
					deadKilledApps = deadThisTime;

					listAdapter = new SimpleAdapter(ProcessList.this,
							processInfos, R.layout.process, new String[] {
									ProcessHelper.APP_ICON,
									ProcessHelper.APP_NAME }, new int[] {
									R.id.appIcon, R.id.appName }) {

						@Override
						public View getView(int position, View convertView,
								ViewGroup parent) {
							View v = super.getView(position, convertView,
									parent);
							if (listItemSelected[position]) {
								v.setSelected(true);
								v.setPressed(true);
								v.setBackgroundColor(Color.GRAY);
							} else {
								v.setSelected(false);
								v.setPressed(false);
								v.setBackgroundColor(Color.TRANSPARENT);
							}
							return v;
						}

					};

					listAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {

						@Override
						public boolean setViewValue(View view, Object data,
								String text) {
							if (view instanceof ImageView
									&& data instanceof Drawable) {
								ImageView imageView = (ImageView) view;
								Drawable drawable = (Drawable) data;
								imageView.setImageDrawable(drawable);
								return true;
							} else
								return false;
						}
					});
					refreshProcessMemInfo();
					setListAdapter(listAdapter);
				}
			}
		}

	};

	private void refreshProcessMemInfo() {
		ActivityManager.MemoryInfo minfo = new ActivityManager.MemoryInfo();
		processHelper.getActivityManager().getMemoryInfo(minfo);
		String processInfo = getResources().getString(R.string.process) + ":"
				+ processInfos.size();
		String memInfo = getResources().getString(R.string.available_mem) + ":"
				+ Formatter.formatFileSize(getBaseContext(), minfo.availMem);
		infoLabel.setText(processInfo + "  " + memInfo);

	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.process_menu, menu);
      return true;
    } 
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
      case R.id.kill:
    	  onKilled();
			return true;
case R.id.refresh:
	loadItems(getApplicationContext());
		return true;
case R.id.quit:
case MENU_QUIT:
		finish();
		return true;
	}
	return false;
}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.process_list);
		getListView().setOnCreateContextMenuListener(this);
		this.appHelper = new ApplicationHelper(getPackageManager());
		this.processHelper = new ProcessHelper(
				(ActivityManager) getSystemService(ACTIVITY_SERVICE), appHelper);
		this.historyHelper = new HistoryHelper();
		this.infoLabel = (TextView) findViewById(R.id.infoLabel);
			}
	
	private void onKilled() {
		if (listItemSelected != null) {
			List<String> newKilledApps = new ArrayList<String>();
			int pos = 0;
			for (Boolean selected : listItemSelected) {
				if (selected) {
					Map<String, Object> processInfo = processInfos.get(pos);
					if (processInfo != null) {
						String packgeName = (String) processInfo
								.get(ProcessHelper.PKG_NAME);
						processHelper.killApp(packgeName);
						newKilledApps.add(packgeName);
					}
				}
				pos++;
			}
			newKilledApps.addAll(this.deadKilledApps);
			Collections.sort(newKilledApps);
			if (!newKilledApps.isEmpty() && !newKilledApps.equals(killedApps)) {
				this.historyHelper.saveKilledPackageNames(
						getApplicationContext(), newKilledApps);
				killedApps = newKilledApps;
			}
			loadItems(getApplicationContext());
		}
	}

	private boolean[] listItemSelected;

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if (listItemSelected[position]) {
			listItemSelected[position] = false;
			v.setBackgroundColor(Color.TRANSPARENT);
		} else {
			listItemSelected[position] = true;
			v.setBackgroundColor(Color.GRAY);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		loadKilledApps();
		loadItems(getApplicationContext());
	}

	private void loadKilledApps() {
		this.killedApps = this.historyHelper
				.loadKilledPackageNames(getApplicationContext());
		Log.w(LOG_TAG, "Loaded kill history:" + this.killedApps);
	}

	private void loadItems(final Context context) {
		progressDialog = ProgressDialog.show(this,
				getResources().getString(R.string.progress_title),
				getResources().getString(R.string.progress_message));
		new Thread() {
			@Override
			public void run() {
				processInfos = processHelper.getProcessInfos(context);
				handler.sendEmptyMessage(LOAD_PROCESS_INFOS);
			}
		}.start();
	}
}
