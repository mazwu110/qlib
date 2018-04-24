package com.qlib.qutils;

import java.util.List;

import android.app.ActivityManager;
import android.content.Context;

public class AppUtils {
	// 当前应用是否处于前台
	public static boolean isForeground(Context context) {
		if (context != null) {
			ActivityManager activityManager = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			List<ActivityManager.RunningAppProcessInfo> processes = activityManager
					.getRunningAppProcesses();
			for (ActivityManager.RunningAppProcessInfo processInfo : processes) {
				if (processInfo.processName.equals(context.getPackageName())) {
					if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
