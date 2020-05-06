package com.miui.gamebooster.m;

import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;
import b.b.o.g.e;
import com.miui.securitycenter.Application;
import java.util.List;

/* renamed from: com.miui.gamebooster.m.n  reason: case insensitive filesystem */
public class C0383n {
    public static void a(Context context, Uri uri, int i) {
        Intent intent = new Intent();
        if (uri != null) {
            intent.setData(uri);
        }
        intent.addFlags(268435456);
        try {
            ActivityOptions activityOptions = (ActivityOptions) e.a(Class.forName("android.util.MiuiMultiWindowUtils"), ActivityOptions.class, "getActivityOptions", (Class<?>[]) new Class[]{Context.class, String.class}, context, null);
            if (activityOptions != null) {
                context.startActivity(intent, activityOptions.toBundle());
            }
        } catch (Exception e) {
            Toast.makeText(context, context.getString(i), 0).show();
            Log.e("GameBoosterReflectUtils", e.toString());
        }
    }

    public static void a(Context context, String str, String str2, int i) {
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(str, str2);
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.addFlags(268435456);
        intent.setComponent(componentName);
        try {
            ActivityOptions activityOptions = (ActivityOptions) e.a(Class.forName("android.util.MiuiMultiWindowUtils"), ActivityOptions.class, "getActivityOptions", (Class<?>[]) new Class[]{Context.class, String.class}, context, str);
            if (activityOptions != null) {
                context.startActivity(intent, activityOptions.toBundle());
            }
        } catch (Exception e) {
            Toast.makeText(context, context.getString(i), 0).show();
            Log.e("GameBoosterReflectUtils", e.toString());
        }
    }

    public static void a(Context context, String str, String str2, int i, boolean z) {
        Object a2;
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(str, str2);
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.addFlags(268435456);
        intent.setComponent(componentName);
        if (z) {
            try {
                a2 = e.a(Class.forName("android.util.MiuiMultiWindowUtils"), ActivityOptions.class, "getActivityOptions", (Class<?>[]) new Class[]{Context.class, String.class, Boolean.TYPE}, context, str, true);
            } catch (Exception e) {
                Toast.makeText(context, context.getString(i), 0).show();
                Log.e("GameBoosterReflectUtils", e.toString());
                return;
            }
        } else {
            a2 = e.a(Class.forName("android.util.MiuiMultiWindowUtils"), ActivityOptions.class, "getActivityOptions", (Class<?>[]) new Class[]{Context.class, String.class}, context, str);
        }
        ActivityOptions activityOptions = (ActivityOptions) a2;
        if (activityOptions != null) {
            context.startActivity(intent, activityOptions.toBundle());
        }
    }

    public static boolean a() {
        if (Build.VERSION.SDK_INT >= 28) {
            try {
                for (Object next : (List) e.a(e.a(Class.forName("android.app.ActivityManager"), "getService", (Class<?>[]) null, new Object[0]), "getAllStackInfos", (Class<?>[]) null, new Object[0])) {
                    ComponentName componentName = (ComponentName) e.a(next, "topActivity");
                    if (((Integer) e.a(e.a(e.a(next, "configuration"), "windowConfiguration"), "getWindowingMode", (Class<?>[]) null, new Object[0])).intValue() == ((Integer) e.a(Class.forName("android.app.WindowConfiguration"), "WINDOWING_MODE_FREEFORM")).intValue() && componentName != null) {
                        return true;
                    }
                }
            } catch (Exception e) {
                Log.e("GameBoosterReflectUtils", e.toString());
            }
        }
        return false;
    }

    public static boolean a(String str) {
        try {
            Object obj = null;
            if (Build.VERSION.SDK_INT >= 28) {
                boolean z = false;
                boolean z2 = false;
                for (Object next : (List) e.a(e.a(Class.forName("android.app.ActivityManager"), "getService", (Class<?>[]) null, new Object[0]), "getAllStackInfos", (Class<?>[]) null, new Object[0])) {
                    ComponentName componentName = (ComponentName) e.a(next, "topActivity");
                    int intValue = ((Integer) e.a(e.a(e.a(next, "configuration"), "windowConfiguration"), "getWindowingMode", (Class<?>[]) null, new Object[0])).intValue();
                    int intValue2 = ((Integer) e.a(Class.forName("android.app.WindowConfiguration"), "WINDOWING_MODE_FREEFORM")).intValue();
                    boolean booleanValue = ((Boolean) e.a(next, "visible")).booleanValue();
                    if (intValue == intValue2 && booleanValue) {
                        z2 = true;
                    } else if (componentName != null && componentName.getPackageName().equals(str) && booleanValue) {
                        z = true;
                    }
                    if (z2 && z) {
                        return true;
                    }
                }
            } else if (Build.VERSION.SDK_INT < 24) {
                return false;
            } else {
                List list = (List) e.a(e.a(Class.forName("android.app.ActivityManagerNative"), "getDefault", (Class<?>[]) null, new Object[0]), "getAllStackInfos", (Class<?>[]) null, new Object[0]);
                if (list.size() > 0) {
                    obj = list.get(0);
                }
                if (obj != null) {
                    ComponentName componentName2 = (ComponentName) e.a(obj, "topActivity");
                    int[] iArr = (int[]) e.a(obj, "taskIds");
                    int intValue3 = ((Integer) e.a(obj, "stackId")).intValue();
                    if (!(componentName2 == null || iArr == null)) {
                        String packageName = componentName2.getPackageName();
                        return (C0393y.a(packageName, (Context) Application.d()) && intValue3 == 2) || "com.lbe.security.miui".equals(packageName);
                    }
                }
            }
        } catch (Exception e) {
            Log.i("GameBoosterReflectUtils", e.toString());
        }
    }
}
