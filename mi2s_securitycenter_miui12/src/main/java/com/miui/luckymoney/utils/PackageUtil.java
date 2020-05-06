package com.miui.luckymoney.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.b;
import com.miui.luckymoney.config.AppConstants;
import com.miui.luckymoney.stats.MiStatUtil;
import java.util.ArrayList;
import java.util.List;

public class PackageUtil {
    private static final String TAG = "PackageUtil";
    private static ArrayList<String> sFileExplorerActivity = new ArrayList<>();
    private static String sLastPackageName = "default";
    private static ArrayList<String> sPackageWhiteMap = new ArrayList<>();
    private static ArrayList<String> sPageBlackList = new ArrayList<>();

    static {
        sFileExplorerActivity.add("com.android.fileexplorer.activity.ShakeStickerActivity");
        sFileExplorerActivity.add("com.android.fileexplorer.view.ShakeStickerActivity");
        sPackageWhiteMap.add(AppConstants.Package.PACKAGE_NAME_MM);
        sPackageWhiteMap.add(AppConstants.Package.PACKAGE_NAME_QQ);
        sPageBlackList.add("com.tencent.mm.plugin.shake.ui.ShakeReportUI");
        sPageBlackList.add("com.tencent.mm.plugin.shakelucky.ui.ShakeLuckyUI");
        sPageBlackList.add("com.tencent.mm.plugin.shakelucky.ui.ShakeLuckyReminderUI");
    }

    public static String getAppName(Context context, String str) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(str, 0);
            if (applicationInfo != null) {
                return context.getPackageManager().getApplicationLabel(applicationInfo).toString();
            }
            return null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (RuntimeException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public static int getAppVersionCode(PackageManager packageManager, String str) {
        try {
            return packageManager.getPackageInfo(str, 8768).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String getForegroundApp(Context context) {
        ComponentName componentName = ((ActivityManager) context.getSystemService("activity")).getRunningTasks(1).get(0).topActivity;
        if (componentName == null) {
            return null;
        }
        return componentName.getPackageName();
    }

    public static String getPackageNameFromIntent(Intent intent) {
        if (intent == null) {
            return null;
        }
        String str = intent.getPackage();
        if (str != null) {
            return str;
        }
        ComponentName component = intent.getComponent();
        if (component == null) {
            return null;
        }
        return component.getPackageName();
    }

    public static Intent getStickerIntent(String str, boolean z) {
        Intent intent = new Intent("miui.intent.action.SHAKE_STICKER");
        intent.addFlags(268435456);
        if (z) {
            sLastPackageName = "default";
        }
        if (!TextUtils.isEmpty(str)) {
            intent.putExtra("packageName", str);
        }
        return intent;
    }

    public static String[] getTopActivityInfo(Context context) {
        String str;
        List<ActivityManager.RunningTaskInfo> runningTasks = ((ActivityManager) context.getSystemService("activity")).getRunningTasks(1);
        if (runningTasks == null) {
            return null;
        }
        String[] strArr = new String[2];
        ComponentName componentName = runningTasks.get(0).topActivity;
        String className = componentName.getClassName();
        String packageName = componentName.getPackageName();
        if (TextUtils.isEmpty(className) || TextUtils.isEmpty(packageName)) {
            str = "not found package info!";
        } else {
            strArr[0] = packageName;
            strArr[1] = className;
            str = "className : " + className;
        }
        Log.i(TAG, str);
        return strArr;
    }

    public static Intent getWalletUriIntent(Context context, String str) {
        try {
            Uri parse = Uri.parse(str);
            Intent intent = new Intent("android.intent.action.VIEW", parse);
            intent.setPackage("com.mipay.wallet");
            if (!isIntentExist(context, intent, "com.mipay.wallet")) {
                intent = new Intent("android.intent.action.VIEW", parse);
            }
            intent.addFlags(268435456);
            return intent;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isInstalledPackage(Context context, String str) {
        try {
            return context.getPackageManager().getPackageInfo(str, 0) != null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isIntentExist(Context context, Intent intent, String str) {
        try {
            PackageManager packageManager = context.getPackageManager();
            if (!TextUtils.isEmpty(str)) {
                intent.setPackage(str);
            }
            List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, 32);
            return queryIntentActivities != null && queryIntentActivities.size() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void startStickerActivityWithVibrator(Context context) {
        String[] topActivityInfo = getTopActivityInfo(context);
        if (topActivityInfo == null) {
            return;
        }
        if (sFileExplorerActivity.contains(topActivityInfo[1]) && !sLastPackageName.equals("defalut") && sPackageWhiteMap.contains(sLastPackageName)) {
            Log.i(TAG, "isExplorerActivity");
            Intent stickerIntent = getStickerIntent(sLastPackageName, false);
            if (isIntentExist(context, stickerIntent, AppConstants.Package.PACKAGE_NAME_FILE)) {
                ((Vibrator) context.getSystemService("vibrator")).vibrate(200);
                MiStatUtil.recordShakeRandomExpression();
                context.startActivity(stickerIntent);
            }
        } else if (sPackageWhiteMap.contains(topActivityInfo[0]) && !sPageBlackList.contains(topActivityInfo[1])) {
            Log.i(TAG, "startStickerActivityWithVibrator");
            Intent stickerIntent2 = getStickerIntent(topActivityInfo[0], false);
            if (isIntentExist(context, stickerIntent2, AppConstants.Package.PACKAGE_NAME_FILE)) {
                ((Vibrator) context.getSystemService("vibrator")).vibrate(200);
                MiStatUtil.recordShakeRandomExpression();
                context.startActivity(stickerIntent2);
                sLastPackageName = topActivityInfo[0];
            }
        }
    }

    public static void startUriWithBrowser(Context context, String str) {
        if (str != null) {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(str));
            boolean z = false;
            for (String str2 : b.e) {
                intent.setPackage(str2);
                z = isIntentExist(context, intent, str2);
                if (z) {
                    break;
                }
            }
            if (!z) {
                intent.setPackage((String) null);
            }
            intent.addFlags(268435456);
            context.startActivity(intent);
        }
    }
}
