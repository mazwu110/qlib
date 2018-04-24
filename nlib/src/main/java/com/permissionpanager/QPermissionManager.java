package com.permissionpanager;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mzw on 2017/12/9.
 */

// 权限请求
public class QPermissionManager {
	public final static int PERMISSIONS_REQUEST_CAMERA = 666;
	private static Context mContext;

	//获取所有需要请求的权限
	public static boolean getAllPermission(Context context, String[] pList) {
		mContext = context;
		String[] newPlist = getNoPassPermission(pList); // 过滤已经申请过的权限
		if (newPlist.length > 0) {
			ActivityCompat.requestPermissions((Activity) mContext, newPlist, PERMISSIONS_REQUEST_CAMERA); // 申请权限
			return false; // 要申请权限
		} else {
			return true; // 已申请过权限
		}
	}

	// 获取未允许通过的权限
	private static String[] getNoPassPermission(String[] pList) {
		List<String> noPassPm = new ArrayList<>();
		for (String data : pList) {
			if (ContextCompat.checkSelfPermission(mContext, data) != PackageManager.PERMISSION_GRANTED) {
				noPassPm.add(data); // 记住需要申请的权限
			}
		}

		String[] backData = new String[noPassPm.size()];
		for (int i = 0; i < noPassPm.size(); i++) {
			backData[i] = noPassPm.get(i);
		}
		return backData;
	}

	// 核对权限是否已经都开启了
	public static boolean ReCheckAllPermission(String[] pList) {
		for (String data : pList) {
			if (ContextCompat.checkSelfPermission(mContext, data) != PackageManager.PERMISSION_GRANTED) {
				return false;
			}
		}

		return true;
	}
}
