package com.bionx.res.activity;

import java.text.NumberFormat;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bionx.res.R;
import com.bionx.res.extra.Constants;
import com.bionx.res.extra.Frequency;
import com.bionx.res.extra.Stats;
import com.bionx.res.extra.Stats.SortMethod;
import com.bionx.res.extra.SysUtils;

public class StatsActivity extends ListActivity {

	protected NumberFormat nf;
	protected ProgressDialog progressDialog;
	protected StatsAdapter adapter = new StatsAdapter();
	protected SharedPreferences preferences;
	//
	protected ImageView refreshButton;
	protected TextView tvCurrentFrequency;
	//
	protected TextView tvHeaderFrequency;
	protected TextView tvHeaderPerc;
	protected TextView tvHeaderPerc5;
	protected SortMethod sortMethod = SortMethod.Frequency;

	protected View.OnTouchListener sortListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (v == tvHeaderFrequency) {
				/* Default for changing sort method is ascending order */
				if (sortMethod != SortMethod.Frequency) {
					sortMethod = SortMethod.Frequency;
				} else {
					sortMethod = SortMethod.FrequencyDesc;
				}
			} else if (v == tvHeaderPerc) {
				if (sortMethod != SortMethod.Percentage) {
					sortMethod = SortMethod.Percentage;
				} else {
					sortMethod = SortMethod.PercentageDesc;
				}
			} else if (v == tvHeaderPerc5) {
				if (sortMethod != SortMethod.PartialPercentage) {
					sortMethod = SortMethod.PartialPercentage;
				} else {
					sortMethod = SortMethod.PartialPercentageDesc;
				}
			}
			updateSortingHeader();

			Editor editor = preferences.edit();
			editor.putString(Constants.PREF_SORT_METHOD, sortMethod.name());
			editor.commit();

			if (adapter.stats != null) {
				adapter.stats.sort(sortMethod);
			}
			adapter.notifyDataSetChanged();
			return true;
		}
	};

	protected Runnable statsRunnable = new Runnable() {
		@Override
		public void run() {
			if (adapter.stats != null) {
				adapter.stats.clear();
			}
			boolean withDeepSleep = preferences.getBoolean(
					Constants.PREF_INCLUDE_DEEP_SLEEP,
					Constants.PREF_DEFAULT_INCLUDE_DEEP_SLEEP);
			Stats previousStats = SysUtils.getFrequencyStats(withDeepSleep);
			// No need to sort these.
			try {
				Thread.sleep(5000);
			} catch (InterruptedException iex) {
				iex.printStackTrace();
			}
			adapter.stats = SysUtils.getFrequencyStats(withDeepSleep);
			if (adapter.stats != null) {
				adapter.stats.setPreviousStats(previousStats);
				adapter.stats.sort(sortMethod);
			}
			handler.sendEmptyMessage(0);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cpu_stats);
		setListAdapter(adapter);

		preferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		refreshButton = (ImageView) findViewById(R.id.btn_refresh_stats);
		refreshButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				progressDialog.show();
				(new Thread(statsRunnable)).start();
			}
		});

		tvCurrentFrequency = (TextView) findViewById(R.id.tv_stats_current);
		Frequency curFreq = SysUtils.getCurFreq();
		String s = curFreq != null ? curFreq.toString()
				: getString(R.string.stats_current_unavailable);
		tvCurrentFrequency.setText(getString(R.string.stats_current, s));

		/* (Sort-) Header */
		LinearLayout grpHeader = (LinearLayout) findViewById(R.id.grp_stats_header);
		tvHeaderFrequency = (TextView) grpHeader
				.findViewById(R.id.tv_stats_row_frequency);
		tvHeaderPerc = (TextView) grpHeader
				.findViewById(R.id.tv_stats_row_perc);
		tvHeaderPerc5 = (TextView) grpHeader
				.findViewById(R.id.tv_stats_row_perc5);
		tvHeaderFrequency.setOnTouchListener(sortListener);
		tvHeaderPerc.setOnTouchListener(sortListener);
		tvHeaderPerc5.setOnTouchListener(sortListener);
		/* Defult sorting method */
		sortMethod = SortMethod
				.valueOf(preferences.getString(Constants.PREF_SORT_METHOD,
						Constants.PREF_DEFAULT_SORT_METHOD));
		updateSortingHeader();

		nf = NumberFormat.getPercentInstance();
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		progressDialog = ProgressDialog.show(this, null,
				getString(R.string.spinner_stats_text), true);

		(new Thread(statsRunnable)).start();
	};

	private void updateSortingHeader() {
		/* Clear compound drawables for every view */
		tvHeaderFrequency.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		tvHeaderPerc.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		tvHeaderPerc5.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		switch (sortMethod) {
		case Frequency:
			tvHeaderFrequency.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.ic_sort_asc, 0, 0, 0);
			break;
		case FrequencyDesc:
			tvHeaderFrequency.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.ic_sort_desc, 0, 0, 0);
			break;
		case Percentage:
			tvHeaderPerc.setCompoundDrawablesWithIntrinsicBounds(0, 0,
					R.drawable.ic_sort_asc, 0);
			break;
		case PercentageDesc:
			tvHeaderPerc.setCompoundDrawablesWithIntrinsicBounds(0, 0,
					R.drawable.ic_sort_desc, 0);
			break;
		case PartialPercentage:
			tvHeaderPerc5.setCompoundDrawablesWithIntrinsicBounds(0, 0,
					R.drawable.ic_sort_asc, 0);
			break;
		case PartialPercentageDesc:
			tvHeaderPerc5.setCompoundDrawablesWithIntrinsicBounds(0, 0,
					R.drawable.ic_sort_desc, 0);
			break;
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (progressDialog != null) {
				adapter.notifyDataSetChanged();
				progressDialog.dismiss();
				//
				Frequency curFreq = SysUtils.getCurFreq();
				String s = curFreq != null ? curFreq.toString()
						: getString(R.string.stats_current_unavailable);
				String s2 = getString(R.string.stats_current, s);
				tvCurrentFrequency.setText(s2);
			}
		};
	};

	private class StatsAdapter extends BaseAdapter {
		LayoutInflater li = null;
		Paint p = null;

		private final class ViewHolder {
			TextView tvFrequency;
			TextView tvPerc;
			TextView tvPerc5;
		}

		protected Stats stats;

		@Override
		public int getCount() {
			if (stats != null) {
				return stats.getFrequencies().size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		@SuppressWarnings("deprecation")
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				if (li == null) {
					li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
				}
				if (p == null) {
					p = new Paint();
					p.setAntiAlias(true);
					p.setStyle(Style.FILL);
					p.setStrokeWidth(2);
				}
				convertView = li.inflate(R.layout.cpu_stats_row, null);
			}
			ViewHolder holder = (ViewHolder) convertView.getTag();
			if (holder == null) {
				holder = new ViewHolder();
				holder.tvFrequency = (TextView) convertView
						.findViewById(R.id.tv_stats_row_frequency);
				holder.tvPerc = (TextView) convertView
						.findViewById(R.id.tv_stats_row_perc);
				holder.tvPerc5 = (TextView) convertView
						.findViewById(R.id.tv_stats_row_perc5);
				convertView.setTag(holder);
			}

			if (stats != null) {
				Frequency freq = stats.getFrequencies().get(position);
				if (freq != null) {
					if ("-1".equals(freq.getValue())) {
						holder.tvFrequency
								.setText(getString(R.string.stats_deep_sleep));
					} else {
						holder.tvFrequency.setText(freq.toString());
					}
					holder.tvPerc.setText(nf.format(stats.getPercentage(freq)));
					holder.tvPerc5.setText(nf.format(stats
							.getPartialPercentage(freq)));
					/* Chart */
					p.setColor(Stats.colorFromPercentage((float) (stats
							.getFrequencyIndex(freq))
							/ stats.getFrequencies().size()));

					Bitmap bmp = Bitmap.createBitmap(101, 5, Config.ARGB_8888);
					Canvas c = new Canvas(bmp);
					c.drawRect((int) (100 * (1 - stats.getPercentage(freq))),
							4, 101, 5, p);

					holder.tvPerc
							.setBackgroundDrawable(new BitmapDrawable(bmp));

					bmp = Bitmap.createBitmap(101, 5, Config.ARGB_8888);
					c = new Canvas(bmp);
					c.drawRect(
							(int) (100 * (1 - stats.getPartialPercentage(freq))),
							4, 101, 5, p);
					holder.tvPerc5
							.setBackgroundDrawable(new BitmapDrawable(bmp));

				}
			}
			return convertView;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		preferences = null;
		/*
		 * Proper handling of progressDialog
		 */
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		refreshButton = null;
		tvCurrentFrequency = null;
	}
}
