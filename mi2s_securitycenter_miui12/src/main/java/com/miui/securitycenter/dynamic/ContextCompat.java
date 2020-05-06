package com.miui.securitycenter.dynamic;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.IBinder;
import android.os.UserHandle;
import android.view.Display;
import b.b.o.g.e;
import dalvik.system.DexClassLoader;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import miui.util.Log;

public class ContextCompat {
    private static final String TAG = "ContextCompat";

    public static Context createApplicationContext(Context context, ApplicationInfo applicationInfo, boolean z) {
        try {
            Object createLoadedApk = createLoadedApk(context, applicationInfo, z);
            if (Build.VERSION.SDK_INT >= 28) {
                applicationInfo.nativeLibraryDir = applicationInfo.sourceDir;
            }
            Context createContext = createContext(context, createLoadedApk);
            if (Build.VERSION.SDK_INT == 19) {
                Field declaredField = createLoadedApk.getClass().getDeclaredField("mClassLoader");
                declaredField.setAccessible(true);
                File file = new File(new File(applicationInfo.sourceDir).getParent(), "code_cache");
                if (!file.exists()) {
                    file.mkdirs();
                }
                declaredField.set(createLoadedApk, new DexClassLoader(applicationInfo.sourceDir, file.getAbsolutePath(), applicationInfo.sourceDir, context.getClassLoader()));
            }
            if (createContext.getResources() != null) {
                return createContext;
            }
            return null;
        } catch (Exception e) {
            Log.e(TAG, "createApplicationContext error", e);
            return null;
        }
    }

    private static Context createContext(Context context, Object obj) {
        Constructor<?> declaredConstructor;
        Object[] objArr;
        Object currentActivityThread = currentActivityThread();
        Class<?> loadClass = context.getClassLoader().loadClass("android.app.ContextImpl");
        int i = Build.VERSION.SDK_INT;
        if (i <= 22) {
            declaredConstructor = loadClass.getDeclaredConstructor(new Class[]{loadClass, Class.forName("android.app.ActivityThread"), Class.forName("android.app.LoadedApk"), IBinder.class, UserHandle.class, Boolean.TYPE, Display.class, Configuration.class});
            declaredConstructor.setAccessible(true);
            objArr = new Object[]{context, currentActivityThread, obj, null, newUserHandle(getUserId(context)), false, null, null};
        } else if (i == 23) {
            declaredConstructor = loadClass.getDeclaredConstructor(new Class[]{loadClass, Class.forName("android.app.ActivityThread"), Class.forName("android.app.LoadedApk"), IBinder.class, UserHandle.class, Boolean.TYPE, Display.class, Configuration.class, Integer.TYPE});
            declaredConstructor.setAccessible(true);
            objArr = new Object[]{context, currentActivityThread, obj, null, newUserHandle(getUserId(context)), false, null, null, 0};
        } else if (i == 24 || i == 25) {
            Class cls = Integer.TYPE;
            declaredConstructor = loadClass.getDeclaredConstructor(new Class[]{loadClass, Class.forName("android.app.ActivityThread"), Class.forName("android.app.LoadedApk"), IBinder.class, UserHandle.class, cls, Display.class, Configuration.class, cls});
            declaredConstructor.setAccessible(true);
            objArr = new Object[]{context, currentActivityThread, obj, null, newUserHandle(getUserId(context)), 0, null, null, 0};
        } else if (i == 29) {
            declaredConstructor = loadClass.getDeclaredConstructor(new Class[]{loadClass, Class.forName("android.app.ActivityThread"), Class.forName("android.app.LoadedApk"), String.class, IBinder.class, UserHandle.class, Integer.TYPE, ClassLoader.class, String.class});
            declaredConstructor.setAccessible(true);
            objArr = new Object[]{context, currentActivityThread, obj, null, null, newUserHandle(getUserId(context)), 0, null, null};
        } else {
            declaredConstructor = loadClass.getDeclaredConstructor(new Class[]{loadClass, Class.forName("android.app.ActivityThread"), Class.forName("android.app.LoadedApk"), String.class, IBinder.class, UserHandle.class, Integer.TYPE, ClassLoader.class});
            declaredConstructor.setAccessible(true);
            objArr = new Object[]{context, currentActivityThread, obj, null, null, newUserHandle(getUserId(context)), 0, null};
        }
        return (Context) declaredConstructor.newInstance(objArr);
    }

    private static Object createLoadedApk(Context context, ApplicationInfo applicationInfo, boolean z) {
        Object[] objArr;
        Constructor<?> constructor;
        Object a2 = e.a((Class<? extends Object>) Resources.class, (Object) context.getResources(), "getCompatibilityInfo", (Class<?>[]) null, new Object[0]);
        Object currentActivityThread = currentActivityThread();
        if (a2 == null) {
            return null;
        }
        Class<?> loadClass = context.getClassLoader().loadClass("android.app.LoadedApk");
        if (Build.VERSION.SDK_INT <= 19) {
            Class cls = Boolean.TYPE;
            constructor = loadClass.getConstructor(new Class[]{Class.forName("android.app.ActivityThread"), ApplicationInfo.class, Class.forName("android.content.res.CompatibilityInfo"), ClassLoader.class, cls, cls});
            objArr = new Object[]{currentActivityThread, applicationInfo, a2, context.getClassLoader(), false, Boolean.valueOf(z)};
        } else {
            Class cls2 = Boolean.TYPE;
            constructor = loadClass.getConstructor(new Class[]{Class.forName("android.app.ActivityThread"), ApplicationInfo.class, Class.forName("android.content.res.CompatibilityInfo"), ClassLoader.class, cls2, cls2, cls2});
            objArr = new Object[]{currentActivityThread, applicationInfo, a2, context.getClassLoader(), false, Boolean.valueOf(z), true};
        }
        return constructor.newInstance(objArr);
    }

    private static Object currentActivityThread() {
        try {
            return e.a(Class.forName("android.app.ActivityThread"), "currentActivityThread", (Class<?>[]) null, new Object[0]);
        } catch (Exception e) {
            Log.e(TAG, "currentActivityThread exception: ", e);
            return null;
        }
    }

    private static int getUserId(Context context) {
        try {
            return ((Integer) e.a((Class<? extends Object>) Context.class, (Object) context, "getUserId", (Class<?>[]) null, new Object[0])).intValue();
        } catch (Exception e) {
            Log.e(TAG, "getUserId exception: ", e);
            return 0;
        }
    }

    private static UserHandle newUserHandle(int i) {
        try {
            return UserHandle.class.getConstructor(new Class[]{Integer.TYPE}).newInstance(new Object[]{Integer.valueOf(i)});
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
