package com.qlib.qremote;

import android.app.Activity;
import android.app.Application;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.qlib.R;
import com.qlib.qutils.FileDirHandler;
import com.qlib.qutils.PicassoUtil;

import java.io.File;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class QApplication extends Application {
    public static int systemBarStyle = R.color.blue; // 沉浸式状态栏样式颜色
    public static boolean DEBUG = true; // true 打印日志
    public static int badgeCount = 0; // 桌面角标
    public static Context mLiveActivity;
    private static PackageInfo pkg;
    public static QApplication instance;
    private static ConnectivityManager mCM;
    private List<Activity> activities = new ArrayList<Activity>();
    public static PicassoUtil mImageLoader;
    private static String mobileIP = "";
    public static int maxSizeImg = 9; // 最大图片数量

    // 图片缓存目录
    public static File imageCache;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            pkg = getPackageManager().getPackageInfo(this.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        instance = this;

        imageCache = FileDirHandler.getCacheDir(this, "QlibmapCache/");
        mCM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // 设置图片工具配置
        setBitmapConfig();
        setHostIp();
    }

    /**
     * 获取手机IP
     */
    public static void setHostIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> ipAddr = intf.getInetAddresses(); ipAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = ipAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        mobileIP = inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
        } catch (Exception e) {
        }
    }

    /**
     * 设置图片工具配置
     */
    private void setBitmapConfig() {
        mImageLoader = PicassoUtil.getInstance(getApplicationContext());
    }

    public synchronized static Context getInstance() {
        return instance;
    }

    public static Resources getAppResources() {
        return instance.getResources();
    }

    public static PackageInfo getPKG() {
        return pkg;
    }

    private static int currVolume;

    // 打开扬声器
    @SuppressWarnings("deprecation")
    public static void OpenSpeaker() {
        try {
            AudioManager audioManager = (AudioManager) getInstance().getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMode(AudioManager.ROUTE_SPEAKER);
            currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);

            if (!audioManager.isSpeakerphoneOn()) {
                audioManager.setSpeakerphoneOn(true);

                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                        audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                        AudioManager.STREAM_VOICE_CALL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 关闭扬声器
    public static void CloseSpeaker() {
        try {
            AudioManager audioManager = (AudioManager) getInstance().getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                if (audioManager.isSpeakerphoneOn()) {
                    audioManager.setSpeakerphoneOn(false);
                    audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, currVolume,
                            AudioManager.STREAM_VOICE_CALL);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 强制帮用户打开GPS * @param context
     */
    public static final void openGPS(Context context) {
        Intent gpsIntent = new Intent();
        gpsIntent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
        gpsIntent.addCategory("android.intent.category.ALTERNATIVE");
        gpsIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, gpsIntent, 0).send();
        } catch (CanceledException e) {
            e.printStackTrace();
        }
    }

    public static boolean getGPSRunState(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    // 打开或关闭GPRS
    public static boolean gprsEnable(boolean bEnable) {
        boolean isOpen = gprsIsOpenMethod("getMobileDataEnabled");
        if (isOpen == !bEnable) {
            setGprsEnable("setMobileDataEnabled", bEnable);
        }

        return isOpen;
    }

    // 检测GPRS是否打开
    @SuppressWarnings("unchecked")
    public static boolean gprsIsOpenMethod(String methodName) {
        Class cmClass = mCM.getClass();
        Class[] argClasses = null;
        Object[] argObject = null;

        Boolean isOpen = false;
        try {
            Method method = cmClass.getMethod(methodName, argClasses);

            isOpen = (Boolean) method.invoke(mCM, argObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isOpen;
    }

    // 开启/关闭GPRS
    @SuppressWarnings("unchecked")
    public static void setGprsEnable(String methodName, boolean isEnable) {
        Class cmClass = mCM.getClass();
        Class[] argClasses = new Class[1];
        argClasses[0] = boolean.class;

        try {
            Method method = cmClass.getMethod(methodName, argClasses);
            method.invoke(mCM, isEnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addActivity(Activity activity) {
        activities.add(activity);
    }

    public void removeActivity(Activity activity) {
        if (activities.contains(activity))
            activities.remove(activity);
    }


    public void onTerminate(boolean isExit) {
        super.onTerminate();

        for (Activity activity : activities) {
            try {
                activity.finish();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
        }
        if (isExit)
            System.exit(0);
    }

    public static String getAppName() {
        return QApplication.getPKG().applicationInfo.packageName
                .substring(QApplication.getPKG().applicationInfo.packageName.lastIndexOf(".") + 1);
    }

    // 获取系统版本机型等信息
    public static String getHandSetInfo(Context context) {
        String handSetInfo = "手机型号:" + android.os.Build.MODEL + ",SDK版本:" + android.os.Build.VERSION.SDK + ",系统版本:"
                + android.os.Build.VERSION.RELEASE + ",App软件版本:" + getAppVersionName(context);
        return handSetInfo;

    }

    // 获取当前版本号
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo("cn.testgethandsetinfo", 0);
            versionName = packageInfo.versionName;
            if (TextUtils.isEmpty(versionName)) {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    // 获取App外部存储器路径
    public static String getAppSDPath(String fileFolderName) {
        String SDPATH = "";
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            SDPATH = Environment.getExternalStorageDirectory() + "/";
        else
            SDPATH = getInstance().getApplicationContext().getFilesDir().getAbsolutePath() + "/";

        if (!fileFolderName.equals("")) {
            SDPATH = SDPATH + fileFolderName + "/"; // 顺便创建文件夹
        }

        return SDPATH;
    }

    public static void showToast(Context mContext, String message) {

    }
}
