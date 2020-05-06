package com.miui.superpower;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.service.notification.StatusBarNotification;
import com.miui.superpower.a.d;
import java.util.List;
import java.util.Set;

public class a {
    public static ResolveInfo a(Context context) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        return context.getPackageManager().resolveActivity(intent, 0);
    }

    public static void a(Context context, SharedPreferences sharedPreferences, List<d> list) {
    }

    public static void a(Set<String> set) {
    }

    public static boolean a() {
        return true;
    }

    public static boolean a(StatusBarNotification statusBarNotification) {
        return false;
    }

    public static void b(Set<String> set) {
    }

    public static boolean b(Context context) {
        return true;
    }
}
