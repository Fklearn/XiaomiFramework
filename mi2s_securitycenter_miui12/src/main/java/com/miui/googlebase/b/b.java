package com.miui.googlebase.b;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import b.b.o.g.d;
import java.util.ArrayList;
import java.util.List;

public class b {

    /* renamed from: a  reason: collision with root package name */
    private static final List<String> f5443a = new ArrayList();

    /* renamed from: b  reason: collision with root package name */
    private static final String[] f5444b = {"com.google.android.gm", "com.google.android.apps.magazines", "com.google.android.apps.books", "com.google.android.apps.plus", "com.google.android.videos", "com.google.android.apps.docs", "com.google.android.youtube", "com.google.android.play.games", "com.google.android.apps.photos", "com.google.android.talk", "com.google.android.music", "com.google.android.apps.maps", "com.google.android.apps.cloudconsole"};

    /* renamed from: c  reason: collision with root package name */
    private static final List<String> f5445c = new ArrayList();

    static {
        f5443a.add("com.android.vending");
        f5443a.add("com.google.android.gms");
        f5443a.add("com.google.android.gsf");
        f5445c.add("com.google.android.syncadapters.contacts");
        f5445c.add("com.google.android.backuptransport");
        f5445c.add("com.google.android.onetimeinitializer");
        f5445c.add("com.google.android.partnersetup");
        f5445c.add("com.google.android.configupdater");
        f5445c.add("com.google.android.ext.services");
        f5445c.add("com.google.android.ext.shared");
        f5445c.add("com.google.android.printservice.recommendation");
    }

    public static int a(Context context) {
        PackageManager packageManager = context.getPackageManager();
        if (packageManager == null) {
            Log.e("GmsCoreUtils", "PackageManager is null!");
            return -2;
        }
        try {
            for (String applicationEnabledSetting : f5443a) {
                if (2 == packageManager.getApplicationEnabledSetting(applicationEnabledSetting)) {
                    return 0;
                }
            }
            return 1;
        } catch (IllegalArgumentException e) {
            Log.e("GmsCoreUtils", "googleApps don't exist! ", e);
            return -1;
        }
    }

    public static void a(Context context, int i) {
        try {
            Object a2 = d.a("GmsCoreUtils", Class.forName("android.app.AppGlobals"), "getPackageManager", (Class<?>[]) new Class[0], new Object[0]);
            if (a2 == null) {
                Log.e("GmsCoreUtils", "PackageManager is null!");
                return;
            }
            for (String next : f5443a) {
                if (((Boolean) d.a("GmsCoreUtils", a2, "isPackageAvailable", (Class<?>[]) new Class[]{String.class, Integer.TYPE}, next, 0)).booleanValue()) {
                    d.a("GmsCoreUtils", a2, "setApplicationEnabledSetting", (Class<?>[]) new Class[]{String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, String.class}, next, Integer.valueOf(i), 1, 0, context.getPackageName());
                }
                if (((Boolean) d.a("GmsCoreUtils", a2, "isPackageAvailable", (Class<?>[]) new Class[]{String.class, Integer.TYPE}, next, 999)).booleanValue()) {
                    d.a("GmsCoreUtils", a2, "setApplicationEnabledSetting", (Class<?>[]) new Class[]{String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, String.class}, next, Integer.valueOf(i), 1, 999, context.getPackageName());
                }
            }
            for (String next2 : f5445c) {
                if (((Boolean) d.a("GmsCoreUtils", a2, "isPackageAvailable", (Class<?>[]) new Class[]{String.class, Integer.TYPE}, next2, 0)).booleanValue()) {
                    d.a("GmsCoreUtils", a2, "setApplicationEnabledSetting", (Class<?>[]) new Class[]{String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, String.class}, next2, Integer.valueOf(i), 1, 0, context.getPackageName());
                }
                if (((Boolean) d.a("GmsCoreUtils", a2, "isPackageAvailable", (Class<?>[]) new Class[]{String.class, Integer.TYPE}, next2, 999)).booleanValue()) {
                    d.a("GmsCoreUtils", a2, "setApplicationEnabledSetting", (Class<?>[]) new Class[]{String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, String.class}, next2, Integer.valueOf(i), 1, 999, context.getPackageName());
                }
            }
            if (2 == i) {
                a(context, a2);
            }
        } catch (Exception e) {
            Log.e("GmsCoreUtils", "Set GmsCore State Exception! ", e);
        }
    }

    private static void a(Context context, Object obj) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
        for (String next : f5443a) {
            if (((Boolean) d.a("GmsCoreUtils", obj, "isPackageAvailable", (Class<?>[]) new Class[]{String.class, Integer.TYPE}, next, 0)).booleanValue()) {
                d.a("GmsCoreUtils", (Object) activityManager, "forceStopPackageAsUser", (Class<?>[]) new Class[]{String.class, Integer.TYPE}, next, 0);
            }
            if (((Boolean) d.a("GmsCoreUtils", obj, "isPackageAvailable", (Class<?>[]) new Class[]{String.class, Integer.TYPE}, next, 999)).booleanValue()) {
                d.a("GmsCoreUtils", (Object) activityManager, "forceStopPackageAsUser", (Class<?>[]) new Class[]{String.class, Integer.TYPE}, next, 999);
            }
        }
        for (String next2 : f5445c) {
            if (((Boolean) d.a("GmsCoreUtils", obj, "isPackageAvailable", (Class<?>[]) new Class[]{String.class, Integer.TYPE}, next2, 0)).booleanValue()) {
                d.a("GmsCoreUtils", (Object) activityManager, "forceStopPackageAsUser", (Class<?>[]) new Class[]{String.class, Integer.TYPE}, next2, 0);
            }
            if (((Boolean) d.a("GmsCoreUtils", obj, "isPackageAvailable", (Class<?>[]) new Class[]{String.class, Integer.TYPE}, next2, 999)).booleanValue()) {
                d.a("GmsCoreUtils", (Object) activityManager, "forceStopPackageAsUser", (Class<?>[]) new Class[]{String.class, Integer.TYPE}, next2, 999);
            }
        }
    }

    public static boolean b(Context context) {
        List<PackageInfo> installedPackages = context.getPackageManager().getInstalledPackages(0);
        ArrayList arrayList = new ArrayList();
        if (installedPackages != null && !installedPackages.isEmpty()) {
            for (PackageInfo next : installedPackages) {
                if (next != null) {
                    arrayList.add(next.packageName);
                }
            }
        }
        if (context.getPackageManager().getLaunchIntentForPackage("com.android.vending") != null) {
            return true;
        }
        for (String contains : f5444b) {
            if (arrayList.contains(contains)) {
                return true;
            }
        }
        return false;
    }
}
