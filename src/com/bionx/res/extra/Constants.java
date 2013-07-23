package com.bionx.res.extra;

import com.bionx.res.extra.Stats.SortMethod;

public class Constants {
	public final static String PREF_MIN_FREQ = "scaling_min_freq";
	public final static String PREF_MAX_FREQ = "scaling_max_freq";
	public final static String PREF_GOVERNOR = "scaling_governor";
	public final static String PREF_IOSCHEDULER = "ioscheduler";
	public final static String PREF_APPLY_ON_BOOT = "apply_on_boot";
	public final static String APP_TAG = "Bionx_CPU";

	public final static String CHECK_SHUTDOWN_OK = "shutdown_ok";
	/**
	 * Only to be checked if CHECK_SHUTDOWN_OK is false.
	 */
	public final static String LAST_UPTIME = "last_uptime";
	public final static String NLJ_SAFETY_CHECK_DATA = "/data/.nocpu";
	public final static String NLJ_SAFETY_CHECK_EXT = "/sd-ext/.nocpu";

	/*
	 * Preferences
	 */
	public final static String PREF_NOTIFY_ON_BOOT = "notify_on_boot";
	public final static boolean PREF_DEFAULT_NOTIFY_ON_BOOT = false;

	public final static String PREF_VIBRATE = "vibrate";
	public final static boolean PREF_DEFAULT_VIBRATE = false;

	public final static String PREF_VIBRATE_PATTERN = "vibrate_pattern";

	public final static String PREF_RINGTONE = "ringtone";
	/* Settings.System.DEFAULT_NOTIFICATION_URI.toString() */
	public final static String PREF_DEFAULT_RINGTONE = null;

	public final static String PREF_INCLUDE_DEEP_SLEEP = "include_deep_sleep";
	public final static boolean PREF_DEFAULT_INCLUDE_DEEP_SLEEP = false;

	public final static String PREF_SORT_METHOD = "sort_method";
	public final static String PREF_DEFAULT_SORT_METHOD = SortMethod.Frequency
			.name();

	public final static boolean DEBUG = false;

}
