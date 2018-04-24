package com.qlib.qutils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;

import com.qlib.R;
import com.qlib.dbdao.bin.QList;
import com.qlib.qremote.QApp;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class QUtils {

	public static boolean checkAPILevel(int mylevel) {
		int sysVersion = Integer.parseInt(VERSION.SDK);
		if (sysVersion >= mylevel) // 系统版本大于等于指定的版本，可以用新API
			return true;

		return false;
	}

	// pkgName APP 的包名
	public static void openOtherApp(Context context, String pkgName) {
		Intent intent = context.getPackageManager().getLaunchIntentForPackage(pkgName);
		context.startActivity(intent);
	}

	// 判断APP是否再前台运行
	public static boolean isRunningForeground(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		String packageName = context.getPackageName();
		List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		if (appProcesses == null)
			return false;
		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(packageName)
					&& appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				return true;
			}
		}

		return false;
	}

	public static JSONArray strArr2JsonArray(String[] src) {
		JSONArray arr = new JSONArray();

		return arr;
	}

	// 判断是否有虚拟按键
	public static boolean checkDeviceHasNavigationBar(Context context) {
		boolean hasNavigationBar = false;
		Resources rs = context.getResources();
		int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
		if (id > 0) {
			hasNavigationBar = rs.getBoolean(id);
		}
		try {
			Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
			Method m = systemPropertiesClass.getMethod("get", String.class);
			String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
			if ("1".equals(navBarOverride)) {
				hasNavigationBar = false;
			} else if ("0".equals(navBarOverride)) {
				hasNavigationBar = true;
			}
		} catch (Exception e) {
		}
		return hasNavigationBar;
	}

	/*
	 * 返回长度为【strLength】的随机数，在前面补0
	 */
	public static String getFixLenthString(int strLength) {

		Random rm = new Random();

		// 获得随机数
		double pross = (1 + rm.nextDouble()) * Math.pow(10, strLength);

		// 将获得的获得随机数转化为字符串
		String fixLenthString = String.valueOf(pross);

		// 返回固定的长度的随机数
		return fixLenthString.substring(1, strLength + 1);
	}

	// String pTime = "2012-03-12";
	public static String getWeekOfDate(String pTime, String fmtType) {
		String Week = "星期";
		SimpleDateFormat format = new SimpleDateFormat(fmtType);
		Calendar c = Calendar.getInstance();
		try {

			c.setTime(format.parse(pTime));

		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 1) {
			Week += "日";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 2) {
			Week += "一";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 3) {
			Week += "二";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 4) {
			Week += "三";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 5) {
			Week += "四";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 6) {
			Week += "五";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 7) {
			Week += "六";
		}

		return Week;
	}

	// 安装apk
	public static void installApk(Context mContext, String apkPath) {
		File apkfile = new File(apkPath);
		if (!apkfile.exists()) {
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
		mContext.startActivity(i);
		mContext.fileList();
	}

	// android系统版本号
	public static String getAndroidIOSVersion() {
		return android.os.Build.VERSION.RELEASE;
	}

	// 手机型号
	public static String getEquipMode() {
		return android.os.Build.MODEL;
	}

	// 获取手机IP
	public static String getHostIP() {
		String hostIp = null;
		try {
			Enumeration nis = NetworkInterface.getNetworkInterfaces();
			InetAddress ia = null;
			while (nis.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) nis.nextElement();
				Enumeration<InetAddress> ias = ni.getInetAddresses();
				while (ias.hasMoreElements()) {
					ia = ias.nextElement();
					if (ia instanceof Inet6Address) {
						continue;// skip ipv6
					}
					String ip = ia.getHostAddress();
					if (!"127.0.0.1".equals(ip)) {
						hostIp = ia.getHostAddress();
						break;
					}
				}
			}
		} catch (SocketException e) {
			Log.i("yao", "SocketException");
			e.printStackTrace();
		}
		return hostIp;
	}

	/*
	 * 验证号码 手机号 固话均可
	 */
	public static boolean isPhoneNumberValid(String phoneNumber) {
		boolean isValid = false;
		String expression = "((^(13|15|18)[0-9]{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9] {1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-? \\d{7,8}-(\\d{1,4})$))";
		CharSequence inputStr = phoneNumber;
		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(inputStr);
		if (matcher.matches()) {
			isValid = true;
		}

		return isValid;
	}

	// 判断输入的手机号码是否正确
	public static boolean isMobileNum(String mobiles) {
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	// 代码给控件设置左边图标
	public static void setViewDrawableLeft(Context mContext, final TextView view, int rscId) {
		Drawable drawable = mContext.getResources().getDrawable(rscId);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		view.setCompoundDrawables(drawable, null, null, null);
	}

	// 获取两位小数的数字
	public static String get2PointValue(String value) {
		DecimalFormat df = new java.text.DecimalFormat("#.##");
		return df.format(value);
	}

	public static String get2PointValue(double value) {
		DecimalFormat df = new java.text.DecimalFormat("#.##");
		return df.format(value);
	}

	// 获取状态栏高度
	public static int getStatusBarHeight(Context context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}

		return result;
	}

	public static int dp2px(Context context, float dipValue) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dp(Context context, float pxValue) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	// 拨打电话
	public static void callPhone(Context context, String phone, String id) {
		if (TextUtils.isEmpty(phone)) {
			Toast.makeText(context, "电话号码不能为空", Toast.LENGTH_LONG).show();
			return;
		}

		phone = phone.replaceAll("-", "");

		Intent intent = new Intent();
		intent.setAction("android.intent.action.CALL");
		intent.setData(Uri.parse("tel:" + phone));
		context.startActivity(intent);
	}

	public static int[] getIntArray(String value) {
		if (!value.contains(".")) {
			value += ".00";
		}

		if (value.substring(value.lastIndexOf(".") + 1).length() < 2) {
			value += "0";
		}

		int data[] = new int[3];
		data[0] = Integer.parseInt(value.substring(0, value.lastIndexOf(".")));
		data[1] = Integer.parseInt(value.substring(value.lastIndexOf(".") + 1, value.lastIndexOf(".") + 2));
		data[2] = Integer.parseInt(value.substring(value.lastIndexOf(".") + 2));

		return data;
	}

	public static String get2String(String src) {
		if (src.length() <= 2)
			return src;
		else
			return "0" + src;

	}

	// scrollView滚动到底部
	public static void scrollToBottom(final ScrollView scroll) {
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				scroll.fullScroll(ScrollView.FOCUS_DOWN);
			}
		});
	}

	// scrollView滚动到底部
	public static void scrollToTop(final ScrollView scroll) {
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				scroll.fullScroll(ScrollView.FOCUS_UP);
			}
		});
	}

	public static String setTwoPointValue(double src) {
		DecimalFormat decimalFormat = new DecimalFormat(".00"); // 构造方法的字符格式这里如果小数不足1位,会以0补足.
		String tps = addZeroToStr(decimalFormat.format(src));
		return tps;
	}

	private static String addZeroToStr(String src) {
		String tps = src;
		if (tps.indexOf(".") == 0) {
			tps = "0" + tps;
		}

		return tps;
	}

	// 检测SD卡是否可
	public static boolean checkSDIsOk() {
		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
			Log.v("TestFile", "SD card is not avaiable/writeable right now.");
			return false;
		}

		return true;
	}

	// 随机获取N位字符
	public static String getRandomChar(int length) {
		StringBuffer buffer = new StringBuffer("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
		StringBuffer sb = new StringBuffer();
		Random r = new Random();
		int range = buffer.length();

		for (int i = 0; i < length; i++) {
			sb.append(buffer.charAt(r.nextInt(range)));
		}

		return sb.toString();
	}

	// 判断所有字符串是否是数字
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	public static boolean isNetworkAvailable(Context context) {
		try {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netWorkInfo = cm.getActiveNetworkInfo();
			return (netWorkInfo != null && netWorkInfo.isAvailable());// 检测网络是否可用
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// 判断WIFI是否可用
	public static boolean isWiFiActive(Context inContext) {
		Context context = inContext.getApplicationContext();
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getTypeName().equals("WIFI") && info[i].isConnected()) {
						return true;
					}
				}
			}
		}

		return false;
	}

	// 将图片设置为圆角
	public static Bitmap toRoundView(Context context, int drawableid, int pixels) {
		Drawable drawable = context.getResources().getDrawable(drawableid);
		BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
		Bitmap bitmap = bitmapDrawable.getBitmap();

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	// 判断指定包名的进程是否运行
	public static boolean isRunning(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		String packageName = context.getPackageName();
		List<RecentTaskInfo> appTask = activityManager.getRecentTasks(Integer.MAX_VALUE, 1);

		if (appTask == null) {
			return false;
		}

		if (appTask.get(0).baseIntent.toString().contains(packageName)) {
			return true;
		}

		return false;
	}

	public static void setImageView(final ImageView imgv, String filepath) {
		if (filepath != null && (!filepath.equals(""))) {
			File file = new File(filepath);

			if (!file.exists()) {
				return;
			}

			try {
				BitmapFactory.Options opt = new BitmapFactory.Options();
				// opt.inJustDecodeBounds=true;
				opt.inDither = false; /* 不进行图片抖动处理 */
				opt.inPreferredConfig = null; /* 设置让解码器以最佳方式解码 */
				opt.inSampleSize = 5; /* 图片长宽方向缩小倍数 */

				FileInputStream fin = new FileInputStream(file);
				BufferedInputStream bin = new BufferedInputStream(fin);

				Bitmap bm = BitmapFactory.decodeStream(bin, null, opt);
				Bitmap thbm = ThumbnailUtils.extractThumbnail(bm, 100, 100);
				imgv.setImageBitmap(thbm); // 设置图片, 设置后上传图片
				bin.close();
				if (!bm.isRecycled()) {
					bm.recycle();
					System.gc();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 是否是移动网络，true是
	public static boolean isMONET(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
			return true;
		}
		return false;
	}

	// 判断网络类型
	public static String GetNetworkType(Context context) {
		String strNetworkType = "";
		ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				strNetworkType = "WIFI";
			} else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
				String _strSubTypeName = networkInfo.getSubtypeName();

				Log.e("cocos2d-x", "Network getSubtypeName : " + _strSubTypeName);

				// TD-SCDMA networkType is 17
				int networkType = networkInfo.getSubtype();
				switch (networkType) {
				case TelephonyManager.NETWORK_TYPE_GPRS:
				case TelephonyManager.NETWORK_TYPE_EDGE:
				case TelephonyManager.NETWORK_TYPE_CDMA:
				case TelephonyManager.NETWORK_TYPE_1xRTT:
				case TelephonyManager.NETWORK_TYPE_IDEN: // api<8 : replace by
															// 11
					strNetworkType = "2G";
					break;
				case TelephonyManager.NETWORK_TYPE_UMTS:
				case TelephonyManager.NETWORK_TYPE_EVDO_0:
				case TelephonyManager.NETWORK_TYPE_EVDO_A:
				case TelephonyManager.NETWORK_TYPE_HSDPA:
				case TelephonyManager.NETWORK_TYPE_HSUPA:
				case TelephonyManager.NETWORK_TYPE_HSPA:
				case TelephonyManager.NETWORK_TYPE_EVDO_B: // api<9 : replace by
															// 14
				case TelephonyManager.NETWORK_TYPE_EHRPD: // api<11 : replace by
															// 12
				case TelephonyManager.NETWORK_TYPE_HSPAP: // api<13 : replace by
															// 15
					strNetworkType = "3G";
					break;
				case TelephonyManager.NETWORK_TYPE_LTE: // api<11 : replace by
														// 13
					strNetworkType = "4G";
					break;
				default:
					// http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
					if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA")
							|| _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
						strNetworkType = "3G";
					} else {
						strNetworkType = _strSubTypeName;
					}

					break;
				}

				Log.e("cocos2d-x", "Network getSubtype : " + Integer.valueOf(networkType).toString());
			}
		}

		Log.e("cocos2d-x", "Network Type : " + strNetworkType);

		return strNetworkType;
	}

	/**
	 * 以最省内存的方式读取本地资源的图片
	 * 
	 * @param context
	 * @param resId
	 * @return
	 */
	public static Bitmap readBitMap(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

	public static Bitmap readBitMap(Context context, String filepath) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(filepath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return BitmapFactory.decodeStream(fin, null, opt);
	}

	public static String getFileLocalURL(String url) {
		return QApp.mImageLoader.getCacheFilePath(url);
	}

	public static int getScreenWitdh(Context mcontext) {
		QList qcscr = getOSDefaultDisplay(mcontext);

		return qcscr.getIntValue(0, "widthPixels");
	}

	public static int getScreenHeight(Context mcontext) {
		QList qcscr = getOSDefaultDisplay(mcontext);

		return qcscr.getIntValue(0, "heightPixels");
	}

	// 获取手机屏幕分辨率
	public static QList getOSDefaultDisplay(Context mcontext) {
		QList qclist = new QList();

		DisplayMetrics dm = new DisplayMetrics();
		((Activity) mcontext).getWindowManager().getDefaultDisplay().getMetrics(dm);

		qclist.put(0, "widthPixels", dm.widthPixels); // 宽度
		qclist.put(0, "heightPixels", dm.heightPixels); // 高度

		return qclist;
	}

	// 获取图片名称获取图片的资源id的方法
	public static int getResourceByReflect(String imageName) {
		Class drawable = R.drawable.class;
		Field field = null;
		int r_id = 0;
		try {
			field = drawable.getField(imageName);
			r_id = field.getInt(field.getName());
		} catch (Exception e) {
			Log.e("ERROR", "PICTURE NOT　FOUND！");
		}

		return r_id;
	}

	// 保存图片
	public static void saveMyBitmap(Bitmap bmp, String path) {
		File f = new File(path);
		try {
			f.createNewFile();
			FileOutputStream fOut = null;
			fOut = new FileOutputStream(f);
			bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			fOut.flush();
			fOut.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
