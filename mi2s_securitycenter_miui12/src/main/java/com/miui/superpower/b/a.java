package com.miui.superpower.b;

import android.os.Build;
import android.util.Log;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class a {

    /* renamed from: a  reason: collision with root package name */
    private static final List<String> f8069a = new ArrayList();

    static {
        f8069a.add("com.android.systemui.tv.pip.PipOnboardingActivity");
        f8069a.add("com.android.systemui.tv.pip.PipMenuActivity");
        f8069a.add("com.android.systemui.recents.RecentsActivity");
    }

    public static void a() {
        Object invoke;
        Method method;
        Object[] objArr;
        try {
            if (Build.VERSION.SDK_INT >= 29) {
                invoke = Class.forName("android.app.ActivityTaskManager").getMethod("getService", new Class[0]).invoke((Object) null, new Object[0]);
                method = invoke.getClass().getMethod("dismissSplitScreenMode", new Class[]{Boolean.TYPE});
                objArr = new Object[]{true};
            } else {
                invoke = Class.forName("android.app.ActivityManagerNative").getMethod("getDefault", new Class[0]).invoke((Object) null, new Object[0]);
                if (Build.VERSION.SDK_INT >= 28) {
                    method = invoke.getClass().getMethod("dismissSplitScreenMode", new Class[]{Boolean.TYPE});
                    objArr = new Object[]{false};
                } else {
                    method = invoke.getClass().getMethod("moveTasksToFullscreenStack", new Class[]{Integer.TYPE, Boolean.TYPE});
                    objArr = new Object[]{3, false};
                }
            }
            method.invoke(invoke, objArr);
        } catch (Exception e) {
            Log.e("SuperPowerSaveManager", "dismissSplitScreenMode exception : " + e);
        }
    }

    public static Object b() {
        int i = Build.VERSION.SDK_INT;
        if (i < 24) {
            return null;
        }
        if (i >= 29) {
            try {
                Object invoke = Class.forName("android.app.ActivityTaskManager").getMethod("getService", new Class[0]).invoke((Object) null, new Object[0]);
                return invoke.getClass().getMethod("getStackInfo", new Class[]{Integer.TYPE, Integer.TYPE}).invoke(invoke, new Object[]{3, 0});
            } catch (Exception e) {
                Log.e("SuperPowerSaveManager", "getStackInfo exception : " + e);
                return null;
            }
        } else {
            Object invoke2 = Class.forName("android.app.ActivityManagerNative").getMethod("getDefault", new Class[0]).invoke((Object) null, new Object[0]);
            if (Build.VERSION.SDK_INT >= 28) {
                return invoke2.getClass().getMethod("getStackInfo", new Class[]{Integer.TYPE, Integer.TYPE}).invoke(invoke2, new Object[]{3, 0});
            }
            return invoke2.getClass().getMethod("getStackInfo", new Class[]{Integer.TYPE}).invoke(invoke2, new Object[]{3});
        }
    }
}
