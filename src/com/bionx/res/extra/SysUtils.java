package com.bionx.res.extra;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.bionx.res.R;
import com.bionx.res.activity.DashMainActivity;

public class SysUtils {
	
	private final static String cpufreq_sys_dir = "/sys/devices/system/cpu/cpu0/cpufreq/";
	private final static String scaling_min_freq = cpufreq_sys_dir
			+ "scaling_min_freq";
	private final static String cpuinfo_min_freq = cpufreq_sys_dir
			+ "cpuinfo_min_freq";
	private final static String scaling_max_freq = cpufreq_sys_dir
			+ "scaling_max_freq";
	private final static String cpuinfo_max_freq = cpufreq_sys_dir
			+ "cpuinfo_max_freq";
	private final static String scaling_cur_freq = cpufreq_sys_dir
			+ "scaling_cur_freq";
	private final static String cpuinfo_cur_freq = cpufreq_sys_dir
			+ "cpuinfo_cur_freq";
	private final static String scaling_governor = cpufreq_sys_dir
			+ "scaling_governor";
	private final static String scaling_available_freq = cpufreq_sys_dir
			+ "scaling_available_frequencies";
	private final static String scaling_available_governors = cpufreq_sys_dir
			+ "scaling_available_governors";
	private final static String scaling_stats_time_in_state = cpufreq_sys_dir
			+ "stats/time_in_state";

	private final static String ioscheduler = "/sys/block/mmcblk0/queue/scheduler";
	private final static String RE_FAKE_BLKDEV = "(loop|zram|dm-)[0-9]+";

	private final static int BUFFER_SIZE = 2048;

	private static String catContents(File dir) {
		File[] files = dir.listFiles();
		String s = "+ dir: " + dir.getAbsolutePath() + " [ ";
		for (File file : files) {
			s += file.getName() + " ";
		}
		s += "]\r\n";
		/*
		 * File contents:
		 */
		for (File file : files) {
			if (file.isFile()) {
				s += "- file: " + file.getAbsolutePath() + ":\r\n";
				Reader r = null;
				try {
					if (file.canRead()) {
						r = new FileReader(file);
					} else {
						/*
						 * Try reading as root
						 */
						Log.w(Constants.APP_TAG,
								"read-only file, trying w/ root: " + file);
						/*
						 * Try reading as root.
						 */
						String[] commands = {
								"cat " + file.getAbsolutePath() + "\n",
								"exit\n" };
						Process p = Runtime.getRuntime()
								.exec(getSUbinaryPath());
						DataOutputStream dos = new DataOutputStream(
								p.getOutputStream());
						for (String command : commands) {
							dos.writeBytes(command);
							dos.flush();
						}
						InputStream is = null;
						if (p.waitFor() == 0) {
							is = p.getInputStream();
						} else {
							is = p.getErrorStream();
						}
						r = new InputStreamReader(is);
					} // end-if: f.canRead()
					BufferedReader br = new BufferedReader(r, BUFFER_SIZE);
					String line = null;
					while ((line = br.readLine()) != null) {
						s += line + "\r\n";
					}
					br.close();
				} catch (Exception ex) {
					s += "FAILURE READING: " + ex.getMessage() + "\r\n";
				}
				s += "\r\n";
			}
		}
		/*
		 * Directories:
		 */
		for (File directory : files) {
			if (directory.isDirectory()) {
				s += catContents(directory);
				s += "\r\n";
			}
		}
		return s;
	}

	private static String readOutput(String command) {
		try {
			Process p = Runtime.getRuntime().exec(command);
			InputStream is = null;
			if (p.waitFor() == 0) {
				is = p.getInputStream();
			} else {
				is = p.getErrorStream();
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(is),
					BUFFER_SIZE);
			String line = br.readLine();
			br.close();
			return line;
		} catch (Exception ex) {
			return "ERROR: " + ex.getMessage();
		}
	}

	public static String discoverCPUData() {
		String s = "MODEL: " + Build.MODEL + "\r\n";
		s += "KERNEL: " + readOutput("uname -a") + "\r\n";
		String[] props = { "ro.build.id", "ro.build.display.id",
				"ro.build.version.release" };
		for (String prop : props) {
			s += prop + ": " + readOutput("/system/bin/getprop " + prop)
					+ "\r\n";
		}
		s += "\r\n";
		s += "QUICK CHECK: F:" + (getAvailableFrequencies() != null);
		s += "/m:" + (getMinFreq() != null) + "/M:" + (getMaxFreq() != null);
		s += "/c:" + (getCurFreq() != null) + "/G:"
				+ (getAvailableGovernors() != null);
		s += "/g:" + (getGovernor() != null) + "/s:"
				+ (getFrequencyStats(false) != null);
		s += "\r\n\r\n";
		s += catContents(new File("/sys/devices/system/cpu/"));
		return s;
	}

	public static String[] getAvailableFrequencies() {
		String[] frequencies = readStringArray(scaling_available_freq);
		if (frequencies == null) {
			Log.e(Constants.APP_TAG,
					"scaling_available_frequencies DOES NOT EXIST, trying different route: stats");
			Stats stats = getFrequencyStats(false);
			if (stats != null) {
				frequencies = new String[stats.getFrequencies().size()];
				for (int i = 0; i < stats.getFrequencies().size(); i++) {
					frequencies[i] = stats.getFrequencies().get(i).getValue();
				}
			} else {
				Log.e(Constants.APP_TAG,
						"stats/time_in_state DOES NOT EXIST, trying different route: cpuinfo_*_freq");
				frequencies = new String[2];
				frequencies[0] = readString(cpuinfo_min_freq);
				frequencies[1] = readString(cpuinfo_max_freq);
				if (frequencies[0] == null || frequencies[1] == null) {
					Log.e(Constants.APP_TAG,
							"cpuinfo_*_freq DO NOT EXIST! returning null");
					frequencies = null;
				}
			}
		}
		if (frequencies != null) {
			Comparator<String> frequencyComparator = new Comparator<String>() {
				@Override
				public int compare(String lhs, String rhs) {
					Integer lhi = Integer.parseInt(lhs);
					Integer rhi = Integer.parseInt(rhs);
					return lhi.compareTo(rhi);
				}
			};
			Arrays.sort(frequencies, frequencyComparator);
		}
		return frequencies;
	}

	public static String[] getAvailableGovernors() {
		return readStringArray(scaling_available_governors);
	}

	public static String[] getAvailableIOSchedulers() {
		String[] schedulers = null;
		String[] aux = readStringArray(ioscheduler);
		if (aux != null) {
			schedulers = new String[aux.length];
			for (int i = 0; i < aux.length; i++) {
				if (aux[i].charAt(0) == '[') {
					schedulers[i] = aux[i].substring(1, aux[i].length() - 1);
				} else {
					schedulers[i] = aux[i];
				}
			}
		}
		return schedulers;
	}

	private static String[] readStringArray(String filename) {
		String line = readString(filename);
		if (line != null) {
			return line.split(" ");
		}
		return null;
	}

	public static Frequency getMinFreq() {
		String[] files = { scaling_min_freq, cpuinfo_min_freq };
		return getXXXFreq(files);
	}

	public static Frequency getMaxFreq() {
		String[] files = { scaling_max_freq, cpuinfo_max_freq };
		return getXXXFreq(files);
	}

	public static Frequency getCurFreq() {
		String[] files = { scaling_cur_freq, cpuinfo_cur_freq };
		return getXXXFreq(files);
	}

	private static Frequency getXXXFreq(String[] possibleFiles) {
		for (String file : possibleFiles) {
			String s = readString(file);
			if (s != null) {
				return new Frequency(s);
			}
		}
		return null;
	}

	public static String getGovernor() {
		return readString(scaling_governor);
	}

	public static String getIOScheduler() {
		String scheduler = null;
		String[] schedulers = readStringArray(ioscheduler);
		if (schedulers != null) {
			for (String s : schedulers) {
				if (s.charAt(0) == '[') {
					scheduler = s.substring(1, s.length() - 1);
					break;
				}
			}
		}
		return scheduler;
	}

	@SuppressWarnings("resource")
	private static String readString(String filename) {
		try {
			File f = new File(filename);
			if (f.exists()) {
				InputStream is = null;
				if (f.canRead()) {
					is = new FileInputStream(f);
				} else {
					Log.w(Constants.APP_TAG, "read-only file, trying w/ root: "
							+ filename);
					String[] commands = { "cat " + filename + "\n", "exit\n" };
					Process p = Runtime.getRuntime().exec(getSUbinaryPath());
					DataOutputStream dos = new DataOutputStream(
							p.getOutputStream());
					for (String command : commands) {
						dos.writeBytes(command);
						dos.flush();
					}
					if (p.waitFor() == 0) {
						is = p.getInputStream();
					} else {
						// is = p.getErrorStream();
						return null;
					}
				} // end-if: f.canRead()
				BufferedReader br = new BufferedReader(
						new InputStreamReader(is), BUFFER_SIZE);
				String line = br.readLine();
				br.close();
				return line;
			} else {
				/*
				 * File does not exist.
				 */
				Log.e(Constants.APP_TAG, "file does not exist: " + filename);
				return null;
			}
		} catch (InterruptedException iex) {
			Log.e(Constants.APP_TAG, iex.getMessage(), iex);
			return null;
		} catch (IOException ioex) {
			Log.e(Constants.APP_TAG, ioex.getMessage(), ioex);
			return null;
		}
	}

	public static String getSUbinaryPath() {
		String s = "/system/bin/su";
		File f = new File(s);
		if (f.exists()) {
			return s;
		}
		s = "/system/xbin/su";
		f = new File(s);
		if (f.exists()) {
			return s;
		}
		return null;
	}

	public static boolean setFrequenciesAndGovernor(String min_freq,
			String max_freq, String governor, String ioscheduler, Context ctx,
			int resOkMsg, int resFailedMsg) {
		try {
			List<String> commands = new ArrayList<String>();

			if (governor != null) {
				commands.add("echo " + governor + " > "
						+ SysUtils.scaling_governor + "\n");
			}

			if (min_freq != null && max_freq != null) {
				String[] available_frequencies = getAvailableFrequencies();
				String lowest_min_freq = available_frequencies[0];

				/*
				 * The first two command assures that the following two commands
				 * will be successful (if max > min).
				 */
				commands.add("echo " + lowest_min_freq + " > "
						+ SysUtils.scaling_min_freq + "\n");
				commands.add("echo " + max_freq + " > "
						+ SysUtils.scaling_max_freq + "\n");
				commands.add("echo " + min_freq + " > "
						+ SysUtils.scaling_min_freq + "\n");
			}

			if (ioscheduler != null) {
				/* Add a line for each applicable block device */
				File sysBlock = new File("/sys/block");
				File[] blockDevices = sysBlock.listFiles();
				for (File blockDevice : blockDevices) {
					if (blockDevice.isDirectory()
							&& !blockDevice.getName().matches(RE_FAKE_BLKDEV)) {
						File queueScheduler = new File(blockDevice,
								"queue/scheduler");
						if (queueScheduler.exists()) {
							commands.add("echo " + ioscheduler + " > "
									+ queueScheduler.getAbsolutePath() + "\n");
						}
					}
				}
			}

			commands.add("exit\n");

			Process p = Runtime.getRuntime().exec(getSUbinaryPath());
			DataOutputStream dos = new DataOutputStream(p.getOutputStream());
			for (String command : commands) {
				dos.writeBytes(command);
				dos.flush();
			}
			int res = p.waitFor();
			if (res == 0) {
				String mex = ctx.getString(resOkMsg, getMinFreq(),
						getMaxFreq(), getGovernor(), getIOScheduler());
				Toast.makeText(ctx, mex, Toast.LENGTH_LONG).show();
				return true;
			} else {
				Toast.makeText(ctx, ctx.getString(resFailedMsg),
						Toast.LENGTH_LONG).show();
				return false;
			}
		} catch (Exception ex) {
			Toast.makeText(ctx, "Error: " + ex.getMessage(), Toast.LENGTH_LONG)
					.show();
			return false;
		}
	}

	@SuppressWarnings("resource")
	public static Stats getFrequencyStats(boolean withDeepSleep) {
		Hashtable<String, Long> times = new Hashtable<String, Long>();
		List<Frequency> frequencies = new ArrayList<Frequency>();
		Long totalTime = 0L;
		File f = new File(scaling_stats_time_in_state);
		InputStream is = null;
		if (f.exists()) {
			try {
				if (f.canRead()) {
					is = new FileInputStream(f);
				} else {
					Log.w(Constants.APP_TAG, "read-only file, trying w/ root: "
							+ f);
					/*
					 * Try reading as root
					 */
					String[] commands = { "cat " + f + "\n", "exit\n" };
					Process p = Runtime.getRuntime().exec(getSUbinaryPath());
					DataOutputStream dos = new DataOutputStream(
							p.getOutputStream());
					for (String command : commands) {
						dos.writeBytes(command);
						dos.flush();
					}
					if (p.waitFor() == 0) {
						is = p.getInputStream();
					} else {
						is = p.getErrorStream();
					}
				}

				BufferedReader br = new BufferedReader(
						new InputStreamReader(is), BUFFER_SIZE);
				String line = null;
				while ((line = br.readLine()) != null) {
					String[] aux = line.split(" ");
					Frequency freq = new Frequency(aux[0]);
					Long time_in_state = Long.parseLong(aux[1]);
					totalTime += time_in_state;
					frequencies.add(freq);
					times.put(freq.getValue(), time_in_state);
				}
				br.close();
				if (withDeepSleep) {
					/* Add deep sleep to the values */
					Frequency deepSleep = new Frequency("-1");
					long uptimeInMillis = SystemClock.uptimeMillis();
					long elapsed = SystemClock.elapsedRealtime();
					long deepSleepTime = elapsed - uptimeInMillis;
					totalTime += deepSleepTime;
					frequencies.add(deepSleep);
					times.put(deepSleep.getValue(), deepSleepTime);
					if (Constants.DEBUG) {
						Log.d(Constants.APP_TAG, "deepSleep: " + deepSleepTime
								+ ", elapsed: " + elapsed + ", uptime: "
								+ uptimeInMillis);
					}
				}
				return new Stats(frequencies, times, totalTime);
			} catch (IOException ioex) {
				Log.e(Constants.APP_TAG, ioex.getMessage(), ioex);
				return null;
			} catch (InterruptedException iex) {
				Log.e(Constants.APP_TAG, iex.getMessage(), iex);
				return null;
			}
		} else {
			Log.e(Constants.APP_TAG, "file does not exist: " + f);
			return null;
		}

	}

	public static boolean isRooted() {
		return getSUbinaryPath() != null;
	}

	public static boolean hasSysfs() {
		String[] requiredFiles = { scaling_governor, scaling_max_freq,
				scaling_min_freq };
		for (String requiredFile : requiredFiles) {
			if (!(new File(requiredFile)).exists()) {
				return false;
			}
		}
		return true;
	}

	@SuppressWarnings("deprecation")
	public static void showNotification(Context ctx, String title, String msg,
			String sound, String pattern) {
		String menuText = msg;
		String contentTitle = title;
		String contentText = msg;

		Intent notificationIntent = new Intent(ctx, DashMainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
				notificationIntent, Intent.FLAG_FROM_BACKGROUND);

		Notification notification = new Notification(
				R.drawable.ic_notification, menuText,
				System.currentTimeMillis());
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.setLatestEventInfo(ctx, contentTitle, contentText,
				contentIntent);
		if (sound != null && sound.length() > 0) {
			notification.sound = Uri.parse(sound);
		}
		if (pattern != null) {
			if (pattern.length() == 0) {
				notification.defaults |= Notification.DEFAULT_VIBRATE;
			} else {
				String[] aux = pattern.split(",");
				long[] vibrate = new long[aux.length];
				for (int i = 0; i < aux.length; i++) {
					try {
						vibrate[i] = Long.parseLong(aux[i].trim());
					} catch (NumberFormatException nfex) {
						vibrate[i] = 0;
					}
				}
				notification.vibrate = vibrate;
			}
		}

		NotificationManager mNotificationManager = (NotificationManager) ctx
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(1, notification);
	}

}
