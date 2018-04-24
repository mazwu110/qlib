package com.qlib.qutils;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

public class FileDirHandler {
	static final String TAG = FileDirHandler.class.getSimpleName();

	private FileDirHandler() {
	}

	public static File getCacheDir(Context context, String uniqueName) {
		final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState()) ? getExternalCacheDir(context)
				.getPath() : context.getCacheDir().getPath();

		return new File(cachePath + File.separator + uniqueName);
	}

	@SuppressLint("NewApi")
	public static boolean isExternalStorageRemovable() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			return Environment.isExternalStorageRemovable();
		}
		return true;
	}

	@SuppressLint("NewApi")
	public static File getExternalCacheDir(Context context) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			return context.getExternalCacheDir();
		}
		final String cacheDir = "/Android/data/" + context.getPackageName()
				+ "/cache/";
		return new File(Environment.getExternalStorageDirectory().getPath()
				+ cacheDir);
	}

	public static File getExternalDotCacheDir(Context context, String uniqueName) {
		try {
			if (Environment.MEDIA_MOUNTED.equals(Environment
					.getExternalStorageState())) {
				// 浣跨敤椤圭洰鍖呭悕鍒涘缓.鏂囦欢澶�
				String dirName = context.getPackageName();
				return new File(Environment.getExternalStorageDirectory()
						.getPath() + "/" + "." + dirName + "/" + uniqueName);
			}
		} catch (Exception e) {
		}
		return null;
	}

	public static File getExternalDotCacheDir(Context context, String dotName,
			String uniqueName) {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			return new File(Environment.getExternalStorageDirectory().getPath()
					+ "/" + "." + dotName + "/" + uniqueName);
		}
		return null;
	}

	public static long getUsableSpace(File path) {
		try {
			final StatFs stats = new StatFs(path.getPath());
			return (long) stats.getBlockSize()
					* (long) stats.getAvailableBlocks();
		} catch (Exception e) {
			return -1;
		}
	}
}
