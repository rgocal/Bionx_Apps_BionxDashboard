package com.bionx.res.helpers;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

public class ApplicationHelper {

	private final PackageManager pm;

	public PackageManager getPm() {
		return pm;
	}

	public ApplicationHelper(PackageManager pm) {
		super();
		this.pm = pm;
	}

	public ApplicationInfo getApplicationInfo(String packageName) {
		try {
			return this.pm.getApplicationInfo(packageName,
					PackageManager.GET_UNINSTALLED_PACKAGES);
		} catch (Throwable e) {
			return null;
		}
	}

}
