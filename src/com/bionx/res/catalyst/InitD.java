
package com.bionx.res.catalyst;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.bionx.res.R;
import com.bionx.res.helpers.CMDProcessor;
import com.bionx.res.helpers.Helpers;

public class InitD extends PreferenceFragment implements
        OnSharedPreferenceChangeListener, OnPreferenceChangeListener{

        File cfg;

        private static final String TAG = "InitD";

        private static final String KEY_ENABLE_INITD = "enable_init_d";
        private static final String KEY_ZIPALIGN = "zipalign";
        private static final String KEY_ENABLE_SDBOOST = "enable_sd_boost";
        private static final String KEY_SDBOOST = "sd_boost";
        private static final String KEY_FIX_PERMS = "fix_permissions";
        private static final String KEY_CLEAR_CACHE = "clear_cache";
        private static final String KEY_CACHE = "cache";
        private static final String KEY_RAM_BOOT = "ram_boot";
        private static final String KEY_RGDN = "rgdn";
        private static final String KEY_SQLITE = "sqlite";
        private static final String KEY_TETHER = "tether";

        private static final String VAR_ZIPALIGN = "ZIPALIGN_AT_BOOT";
        private static final String VAR_ENABLE_SDBOOST = "SD_BOOST_AT_BOOT";
        private static final String VAR_SDBOOST = "READ_AHEAD_KB";
        private static final String VAR_FIX_PERMS = "FIX_PERMISSIONS_AT_BOOT";
        private static final String VAR_CLEAR_CACHE = "REMOVE_CACHE";
        private static final String VAR_CACHE = "CACHE_AT_BOOT";
        private static final String VAR_RAM_BOOT = "RAM_AT_BOOT";
        private static final String VAR_RGDN = "RGDN_AT_BOOT";
        private static final String VAR_SQLITE = "RUN_SQLITE_AT_BOOT";
        private static final String VAR_TETHER = "FREE_TETHER_AT_BOOT";

        /** Called when the activity is first created. */
        @Override
        public void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);

            // get su permissions faster so there's no hangup while
            // looking up all of the preferences
            new CMDProcessor().su.run("");

            PreferenceManager.getDefaultSharedPreferences(getActivity())
                    .registerOnSharedPreferenceChangeListener(this);

            addPreferencesFromResource(R.xml.init_d);

            cfg = new File("/system/etc/liberty.cfg");

            CheckBoxPreference cb = (CheckBoxPreference) findPreference(KEY_ENABLE_INITD);
            boolean checked = isTweakEnabled(KEY_ENABLE_INITD, null);
            cb.setChecked(checked);

            cb = (CheckBoxPreference) findPreference(KEY_ZIPALIGN);
            checked = isTweakEnabled(KEY_ZIPALIGN, VAR_ZIPALIGN);
            cb.setChecked(checked);

            cb = (CheckBoxPreference) findPreference(KEY_ENABLE_SDBOOST);
            checked = isTweakEnabled(KEY_ENABLE_SDBOOST, VAR_ENABLE_SDBOOST);
            cb.setChecked(checked);

            cb = (CheckBoxPreference) findPreference(KEY_FIX_PERMS);
            checked = isTweakEnabled(KEY_FIX_PERMS, VAR_FIX_PERMS);
            cb.setChecked(checked);

            cb = (CheckBoxPreference) findPreference(KEY_ENABLE_SDBOOST);
            checked = isTweakEnabled(KEY_ENABLE_SDBOOST, VAR_ENABLE_SDBOOST);
            cb.setChecked(checked);

            cb = (CheckBoxPreference) findPreference(KEY_CLEAR_CACHE);
            checked = isTweakEnabled(KEY_CLEAR_CACHE, VAR_CLEAR_CACHE);
            cb.setChecked(checked);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                String key) {
            if (key.equals(KEY_ENABLE_INITD)) {

                final boolean initDEnabled = sharedPreferences.getBoolean(KEY_ENABLE_INITD, true);
                Helpers.getMount("rw");
                if (initDEnabled && !new File("/system/etc/init_trigger.enabled").exists()) {

                    new CMDProcessor().su
                            .runWaitFor("busybox mv /system/etc/init_trigger.disabled /system/etc/init_trigger.enabled");

                } else if (!initDEnabled && !new File("/system/etc/init_trigger.disabled").exists()) {

                    new CMDProcessor().su
                            .runWaitFor("busybox mv /system/etc/init_trigger.enabled /system/etc/init_trigger.disabled");
                }
                Helpers.getMount("ro");

            } else if (key.equals(KEY_ZIPALIGN)) {

                final boolean zipalign = sharedPreferences.getBoolean(KEY_ZIPALIGN, true);
                final String value = zipalign ? "1" : "0";
                Helpers.getMount("rw");
                new CMDProcessor().su.runWaitFor("busybox sed -i 's|" + VAR_ZIPALIGN + "=.*|"
                        + VAR_ZIPALIGN + "=" + value + "|' " + cfg);
                Helpers.getMount("ro");

            } else if (key.equals(KEY_ENABLE_SDBOOST)) {

                final boolean enable_boost = sharedPreferences.getBoolean(KEY_ENABLE_SDBOOST, true);
                final String value = enable_boost ? "1" : "0";
                Helpers.getMount("rw");
                new CMDProcessor().su.runWaitFor("busybox sed -i 's|" + VAR_ENABLE_SDBOOST + "=.*|"
                        + VAR_ENABLE_SDBOOST + "=" + value + "|' " + cfg);
                Helpers.getMount("ro");

            } else if (key.equals(KEY_SDBOOST)) {

                final String value = sharedPreferences.getString(KEY_SDBOOST, "2048");
                Helpers.getMount("rw");
                new CMDProcessor().su.runWaitFor("busybox sed -i 's|" + VAR_SDBOOST + "=.*|"
                        + VAR_SDBOOST + "=" + value + "|' " + cfg);
                Helpers.getMount("ro");

            } else if (key.equals(KEY_FIX_PERMS)) {

                final boolean fix_perms = sharedPreferences.getBoolean(KEY_FIX_PERMS, true);
                final String value = fix_perms ? "1" : "0";
                Helpers.getMount("rw");
                new CMDProcessor().su.runWaitFor("busybox sed -i 's|" + VAR_FIX_PERMS + "=.*|"
                        + VAR_FIX_PERMS + "=" + value + "|' " + cfg);
                Helpers.getMount("ro");

            } else if (key.equals(KEY_CLEAR_CACHE)) {

                final boolean clear_cache = sharedPreferences.getBoolean(KEY_CLEAR_CACHE, true);
                final String value = clear_cache ? "1" : "0";
                Helpers.getMount("rw");
                new CMDProcessor().su.runWaitFor("busybox sed -i 's|" + VAR_CLEAR_CACHE + "=.*|"
                        + VAR_CLEAR_CACHE + "=" + value + "|' " + cfg);
                Helpers.getMount("ro");
                
            } else if (key.equals(KEY_CACHE)) {

                final boolean clear_cache = sharedPreferences.getBoolean(KEY_CACHE, true);
                final String value = clear_cache ? "1" : "0";
                Helpers.getMount("rw");
                new CMDProcessor().su.runWaitFor("busybox sed -i 's|" + VAR_CACHE + "=.*|"
                        + VAR_CACHE + "=" + value + "|' " + cfg);
                Helpers.getMount("ro");
                
            } else if (key.equals(KEY_RAM_BOOT)) {

                final boolean clear_cache = sharedPreferences.getBoolean(KEY_RAM_BOOT, true);
                final String value = clear_cache ? "1" : "0";
                Helpers.getMount("rw");
                new CMDProcessor().su.runWaitFor("busybox sed -i 's|" + VAR_RAM_BOOT + "=.*|"
                        + VAR_RAM_BOOT + "=" + value + "|' " + cfg);
                Helpers.getMount("ro");
                
            } else if (key.equals(KEY_RGDN)) {

                final boolean clear_cache = sharedPreferences.getBoolean(KEY_RGDN, true);
                final String value = clear_cache ? "1" : "0";
                Helpers.getMount("rw");
                new CMDProcessor().su.runWaitFor("busybox sed -i 's|" + VAR_RGDN + "=.*|"
                        + VAR_RGDN + "=" + value + "|' " + cfg);
                Helpers.getMount("ro");
                
            } else if (key.equals(KEY_SQLITE)) {

                final boolean clear_cache = sharedPreferences.getBoolean(KEY_SQLITE, true);
                final String value = clear_cache ? "1" : "0";
                Helpers.getMount("rw");
                new CMDProcessor().su.runWaitFor("busybox sed -i 's|" + VAR_SQLITE + "=.*|"
                        + VAR_SQLITE + "=" + value + "|' " + cfg);
                Helpers.getMount("ro");
                
            } else if (key.equals(KEY_TETHER)) {

                final boolean clear_cache = sharedPreferences.getBoolean(KEY_TETHER, true);
                final String value = clear_cache ? "1" : "0";
                Helpers.getMount("rw");
                new CMDProcessor().su.runWaitFor("busybox sed -i 's|" + VAR_TETHER + "=.*|"
                        + VAR_TETHER + "=" + value + "|' " + cfg);
                Helpers.getMount("ro");
            }
        }

        private String getVariableValue(final String variable) {
            String value = null;

            try {
                BufferedReader br = new BufferedReader(new FileReader(cfg), 256);
                String line = null;
                while ((line = br.readLine()) != null) {
                    if (line.startsWith(variable)) {
                        value = line.substring(line.lastIndexOf("=") + 1);
                        break;
                    }
                }
                br.close();
            } catch (FileNotFoundException e) {
                Log.d(TAG, cfg + " does not exist");
                return null;
            } catch (IOException e) {
                Log.d(TAG, "Error reading " + cfg);
            }

            if (value == null) {
                final String s = new CMDProcessor().su.runWaitFor("busybox grep " + variable + " "
                        + cfg).stdout;
                if (s != null) {
                    value = s.substring(s.lastIndexOf("=") + 1);
                }
            }
            return value;
        }

        private boolean isTweakEnabled(final String key, final String variable) {
            if (key.equals(KEY_ENABLE_INITD)) {
                return new File("/system/etc/init_trigger.enabled").exists();
            } else {
                String value = getVariableValue(variable);
                if (value == null) {
                    return false;
                }
                return (value.equals("1"));
            }
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            return false;
        }

}
