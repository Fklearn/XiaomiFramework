package miui.external;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;
import java.io.File;

public class SdkHelper {
    private static final String LOG_TAG = "miuisdk";
    static final String MIUI_SYSTEM_APK_NAME = "miui";
    private static final String PROP_MIUI_VERSION_CODE = "ro.miui.ui.version.code";

    private SdkHelper() {
    }

    static String getApkPath(Context context, String str, String str2) {
        if (context == null) {
            return guessApkPath(str, str2);
        }
        PackageInfo packageInfo = getPackageInfo(context, str);
        if (packageInfo != null) {
            return packageInfo.applicationInfo.publicSourceDir;
        }
        return null;
    }

    static String getLibPath(Context context, String str) {
        if (context == null) {
            context = getSystemContext();
        }
        if (context == null) {
            return guessLibPath(str);
        }
        PackageInfo packageInfo = getPackageInfo(context, str);
        if (packageInfo != null) {
            return packageInfo.applicationInfo.nativeLibraryDir;
        }
        return null;
    }

    private static PackageInfo getPackageInfo(Context context, String str) {
        try {
            return context.getPackageManager().getPackageInfo(str, 128);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Context getSystemContext() {
        try {
            Class<?> cls = Class.forName("android.app.ActivityThread");
            return (Context) cls.getDeclaredMethod("getSystemContext", new Class[0]).invoke(cls.getDeclaredMethod("currentActivityThread", new Class[0]).invoke((Object) null, new Object[0]), new Object[0]);
        } catch (Exception e) {
            Log.e("miuisdk", "getSystemContext error", e);
            return null;
        }
    }

    private static String getSystemProperty(String str, String str2) {
        try {
            return (String) Class.forName("android.os.SystemProperties").getDeclaredMethod("get", new Class[]{String.class, String.class}).invoke((Object) null, new Object[]{str, str2});
        } catch (Exception e) {
            Log.e("miuisdk", "getSystemProperty error", e);
            return str2;
        }
    }

    private static String guessApkPath(String str, String str2) {
        String guessDataApkPath = guessDataApkPath(str);
        return guessDataApkPath == null ? guessSystemApkPath(str2) : guessDataApkPath;
    }

    private static String guessDataApkPath(String str) {
        return searchApkPath(new String[]{"/data/app/" + str + "-1.apk", "/data/app/" + str + "-2.apk", "/data/app/" + str + "-1/base.apk", "/data/app/" + str + "-2/base.apk"});
    }

    private static String guessLibPath(String str) {
        return "/data/data/" + str + "/lib/";
    }

    private static String guessSystemApkPath(String str) {
        return searchApkPath(new String[]{"/system/app/" + str + ".apk", "/system/priv-app/" + str + ".apk", "/system/app/" + str + "/" + str + ".apk", "/system/priv-app/" + str + "/" + str + ".apk"});
    }

    public static boolean isMiuiSystem() {
        return !TextUtils.isEmpty(getSystemProperty(PROP_MIUI_VERSION_CODE, ""));
    }

    private static String searchApkPath(String[] strArr) {
        for (String str : strArr) {
            if (new File(str).exists()) {
                return str;
            }
        }
        return null;
    }
}
