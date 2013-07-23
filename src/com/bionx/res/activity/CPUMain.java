package com.bionx.res.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Utils;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bionx.res.R;
import com.bionx.res.extra.Constants;
import com.bionx.res.extra.Frequency;
import com.bionx.res.extra.SysUtils;

public class CPUMain extends Activity {

	private Button mBtnApply;
	private Button mBtnVoltage;
	private Spinner mSpnMinFreq;
	private Spinner mSpnMaxFreq;
	private Spinner mSpnGovernor;
	private Spinner mSpnIOScheduler;
	private TextView mTvCurMinFreq;
	private TextView mTvCurMaxFreq;
	private TextView mTvCurGovernor;
	private TextView mTvCurIOScheduler;
	private ToggleButton mCkbApplyOnBoot;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!SysUtils.isRooted()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.dlg_noroot_title);
			builder.setMessage(R.string.dlg_noroot_body);
			builder.setCancelable(false);
			builder.setPositiveButton(R.string.exit,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
			builder.create().show();
		} else if (!SysUtils.hasSysfs()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.dlg_nofs_title);
			builder.setMessage(R.string.dlg_nofs_body);
			builder.setCancelable(false);
			builder.setNegativeButton(R.string.exit,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
			builder.create().show();
		} else {
			setContentView(R.layout.cpu_main);
			/*
			 * Get a reference to GUI elements.
			 */
			mSpnMinFreq = (Spinner) findViewById(R.id.sp_min_freq);
			mSpnMaxFreq = (Spinner) findViewById(R.id.sp_max_freq);
			mSpnGovernor = (Spinner) findViewById(R.id.sp_governor);
			mSpnIOScheduler = (Spinner) findViewById(R.id.sp_ioscheduler);
			mTvCurMinFreq = (TextView) findViewById(R.id.tv_cur_min_freq);
			mTvCurMaxFreq = (TextView) findViewById(R.id.tv_cur_max_freq);
			mTvCurGovernor = (TextView) findViewById(R.id.tv_cur_governor);
			mTvCurIOScheduler = (TextView) findViewById(R.id.tv_cur_ioscheduler);
			mCkbApplyOnBoot = (ToggleButton) findViewById(R.id.ckb_apply_on_boot);
			mBtnApply = (Button) findViewById(R.id.btn_apply);
			mBtnVoltage = (Button) findViewById(R.id.voltage);

			final SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());
			mCkbApplyOnBoot.setChecked(prefs.getBoolean(
					Constants.PREF_APPLY_ON_BOOT, false));

			mCkbApplyOnBoot
					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							SharedPreferences.Editor editor = prefs.edit();
							editor.putBoolean(Constants.PREF_APPLY_ON_BOOT,
									isChecked);
							editor.commit();
						}
					});

			/*
			 * Read currest situation and populate GUI.
			 */
			String[] frequencies = SysUtils.getAvailableFrequencies();
			Frequency min_frequency = SysUtils.getMinFreq();
			Frequency max_frequency = SysUtils.getMaxFreq();
			if (frequencies != null && min_frequency != null
					&& max_frequency != null) {
				mTvCurMinFreq.setText(min_frequency.toString());
				mTvCurMaxFreq.setText(max_frequency.toString());

				Frequency[] freqs = new Frequency[frequencies.length];
				for (int i = 0; i < frequencies.length; i++) {
					freqs[i] = new Frequency(frequencies[i]);
				}

				Frequency requested_min_freq = new Frequency(prefs.getString(
						Constants.PREF_MIN_FREQ, min_frequency.getValue()));
				Frequency requested_max_freq = new Frequency(prefs.getString(
						Constants.PREF_MAX_FREQ, max_frequency.getValue()));

				ArrayAdapter<Frequency> minFreqAdapter = new ArrayAdapter<Frequency>(
						this, android.R.layout.simple_spinner_item, freqs);
				minFreqAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				mSpnMinFreq.setAdapter(minFreqAdapter);
				mSpnMinFreq.setSelection(minFreqAdapter
						.getPosition(requested_min_freq));

				ArrayAdapter<Frequency> maxFreqAdapter = new ArrayAdapter<Frequency>(
						this, android.R.layout.simple_spinner_item, freqs);
				maxFreqAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				mSpnMaxFreq.setAdapter(maxFreqAdapter);
				mSpnMaxFreq.setSelection(maxFreqAdapter
						.getPosition(requested_max_freq));
			} else {
				/*
				 * No available frequencies.
				 */
				mTvCurMinFreq.setText(R.string.unavailable);
				mTvCurMaxFreq.setText(R.string.unavailable);

				mSpnMinFreq.setEnabled(false);
				mSpnMaxFreq.setEnabled(false);
			}

			/*
			 * Governor
			 */
			String governor = SysUtils.getGovernor();
			String[] governors = SysUtils.getAvailableGovernors();
			if (governor != null && governors != null) {
				mTvCurGovernor.setText(governor);

				String requested_governor = prefs.getString(
						Constants.PREF_GOVERNOR, governor);

				ArrayAdapter<String> governorAdapter = new ArrayAdapter<String>(
						this, android.R.layout.simple_spinner_item, governors);
				governorAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				mSpnGovernor.setAdapter(governorAdapter);
				mSpnGovernor.setSelection(governorAdapter
						.getPosition(requested_governor));
			} else {
				mTvCurGovernor.setText(R.string.unavailable);
				mSpnGovernor.setEnabled(false);
			}

			/*
			 * I/O Scheduler
			 */
			String ioscheduler = SysUtils.getIOScheduler();
			String[] ioschedulers = SysUtils.getAvailableIOSchedulers();
			if (ioscheduler != null && ioschedulers != null) {
				mTvCurIOScheduler.setText(ioscheduler);

				String requested_ioscheduler = prefs.getString(
						Constants.PREF_IOSCHEDULER, ioscheduler);

				ArrayAdapter<String> ioschedulerAdapter = new ArrayAdapter<String>(
						this, android.R.layout.simple_spinner_item,
						ioschedulers);
				ioschedulerAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				mSpnIOScheduler.setAdapter(ioschedulerAdapter);
				mSpnIOScheduler.setSelection(ioschedulerAdapter
						.getPosition(requested_ioscheduler));
			} else {
				mTvCurIOScheduler.setText(R.string.unavailable);
				mSpnIOScheduler.setEnabled(false);
			}

			mBtnVoltage.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent voltage = new Intent(getBaseContext(),
							VoltageControl.class);
					startActivity(voltage);
				}
			});

			mBtnApply.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					v.post(new Runnable() {
						@Override
						public void run() {
							String min_freq = null;
							String max_freq = null;
							String governor = null;
							String ioscheduler = null;

							if (mSpnMinFreq.isEnabled()) {
								Frequency aux = (Frequency) mSpnMinFreq
										.getSelectedItem();
								if (aux != null) {
									min_freq = aux.getValue();
								}
							}

							if (mSpnMaxFreq.isEnabled()) {
								Frequency aux = (Frequency) mSpnMaxFreq
										.getSelectedItem();
								if (aux != null) {
									max_freq = aux.getValue();
								}
							}

							if (mSpnGovernor.isEnabled()) {
								Object aux = mSpnGovernor.getSelectedItem();
								if (aux != null) {
									governor = aux.toString();
								}
							}

							if (mSpnIOScheduler.isEnabled()) {
								ioscheduler = mSpnIOScheduler.getSelectedItem()
										.toString();
							}

							/*
							 * First thing to do: set dirty flag.
							 */
							SharedPreferences prefs = PreferenceManager
									.getDefaultSharedPreferences(getApplicationContext());
							SharedPreferences.Editor editor = prefs.edit();
							editor.putBoolean(Constants.CHECK_SHUTDOWN_OK,
									false);
							editor.putLong(Constants.LAST_UPTIME,
									SystemClock.uptimeMillis());
							editor.commit();

							SysUtils.setFrequenciesAndGovernor(min_freq,
									max_freq, governor, ioscheduler,
									CPUMain.this, R.string.ok_updated_freqs,
									R.string.err_update_failed);
							if (Constants.DEBUG) {
								if (prefs.getBoolean(
										Constants.PREF_NOTIFY_ON_BOOT,
										Constants.PREF_DEFAULT_NOTIFY_ON_BOOT)) {
									String sound = prefs.getString(
											Constants.PREF_RINGTONE,
											Constants.PREF_DEFAULT_RINGTONE);
									String pattern = null;
									if (prefs.getBoolean(
											Constants.PREF_VIBRATE,
											Constants.PREF_DEFAULT_VIBRATE)) {
										pattern = prefs.getString(
												Constants.PREF_VIBRATE_PATTERN,
												"");
									}
									Log.d(Constants.APP_TAG, "sound: " + sound
											+ ", pattern: " + pattern);
									SysUtils.showNotification(
											CPUMain.this,
											getString(R.string.notify_title_success),
											getString(R.string.notify_success,
													SysUtils.getMinFreq(),
													SysUtils.getMaxFreq(),
													SysUtils.getGovernor(),
													SysUtils.getIOScheduler()),
											sound, pattern);
								} else {
									Log.d(Constants.APP_TAG,
											"notify_on_boot is FALSE");
								}
							}
							/*
							 * Save requested values:
							 */
							editor.putString(Constants.PREF_MIN_FREQ, min_freq);
							editor.putString(Constants.PREF_MAX_FREQ, max_freq);
							editor.putString(Constants.PREF_GOVERNOR, governor);
							editor.putString(Constants.PREF_IOSCHEDULER,
									ioscheduler);
							editor.commit();

							finish();
						}
					});
				}
			});
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (SysUtils.hasSysfs() && SysUtils.isRooted()) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.cpu_main, menu);
			return true;
		}
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_prefs: {
			Intent prefsActivity = new Intent(this, PreferencesActivity.class);
			startActivity(prefsActivity);
			return true;
		}
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}