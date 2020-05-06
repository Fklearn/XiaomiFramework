package com.android.server.wm;

import android.app.ActivityThread;
import android.provider.Settings;
import java.lang.reflect.InvocationTargetException;

public class ActivityPluginDelegate {
    private static final String FOREGROUND_ACTIVITY_TRIGGER = "foreground_activity_trigger";
    private static final boolean LOGV = false;
    private static final int MAX_CONNECT_RETRIES = 15;
    private static final String TAG = "ActivityPluginDelegate";
    private static Class activityServiceClass = null;
    private static Object activityServiceObj = null;
    private static boolean extJarAvail = true;
    static boolean isEnabled = false;
    static int mGetFeatureEnableRetryCount = 15;

    public static void activityInvokeNotification(String appName, boolean isFullScreen) {
        if (getFeatureFlag() && extJarAvail && loadActivityExtJar()) {
            try {
                activityServiceClass.getMethod("sendActivityInvokeNotification", new Class[]{String.class, Boolean.TYPE}).invoke(activityServiceObj, new Object[]{appName, Boolean.valueOf(isFullScreen)});
            } catch (Exception | NoSuchMethodException | SecurityException | InvocationTargetException e) {
            }
        }
    }

    public static void activitySuspendNotification(String appName, boolean isFullScreen, boolean isBg) {
        if (getFeatureFlag() && extJarAvail && loadActivityExtJar()) {
            try {
                activityServiceClass.getMethod("sendActivitySuspendNotification", new Class[]{String.class, Boolean.TYPE, Boolean.TYPE}).invoke(activityServiceObj, new Object[]{appName, Boolean.valueOf(isFullScreen), Boolean.valueOf(isBg)});
            } catch (Exception | NoSuchMethodException | SecurityException | InvocationTargetException e) {
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0068, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static synchronized boolean loadActivityExtJar() {
        /*
            java.lang.Class<com.android.server.wm.ActivityPluginDelegate> r0 = com.android.server.wm.ActivityPluginDelegate.class
            monitor-enter(r0)
            java.lang.String r1 = "com.qualcomm.qti.activityextension.ActivityNotifier"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0069 }
            r2.<init>()     // Catch:{ all -> 0x0069 }
            java.io.File r3 = android.os.Environment.getRootDirectory()     // Catch:{ all -> 0x0069 }
            java.lang.String r3 = r3.getAbsolutePath()     // Catch:{ all -> 0x0069 }
            r2.append(r3)     // Catch:{ all -> 0x0069 }
            java.lang.String r3 = "/framework/ActivityExt.jar"
            r2.append(r3)     // Catch:{ all -> 0x0069 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0069 }
            java.lang.Class r3 = activityServiceClass     // Catch:{ all -> 0x0069 }
            r4 = 1
            if (r3 == 0) goto L_0x0029
            java.lang.Object r3 = activityServiceObj     // Catch:{ all -> 0x0069 }
            if (r3 == 0) goto L_0x0029
            monitor-exit(r0)
            return r4
        L_0x0029:
            java.io.File r3 = new java.io.File     // Catch:{ all -> 0x0069 }
            r3.<init>(r2)     // Catch:{ all -> 0x0069 }
            boolean r3 = r3.exists()     // Catch:{ all -> 0x0069 }
            extJarAvail = r3     // Catch:{ all -> 0x0069 }
            if (r3 != 0) goto L_0x003a
            boolean r3 = extJarAvail     // Catch:{ all -> 0x0069 }
            monitor-exit(r0)
            return r3
        L_0x003a:
            java.lang.Class r3 = activityServiceClass     // Catch:{ all -> 0x0069 }
            if (r3 != 0) goto L_0x0067
            java.lang.Object r3 = activityServiceObj     // Catch:{ all -> 0x0069 }
            if (r3 != 0) goto L_0x0067
            r3 = 0
            dalvik.system.PathClassLoader r5 = new dalvik.system.PathClassLoader     // Catch:{ ClassNotFoundException | IllegalAccessException | InstantiationException -> 0x0062, Exception -> 0x005d }
            java.lang.ClassLoader r6 = java.lang.ClassLoader.getSystemClassLoader()     // Catch:{ ClassNotFoundException | IllegalAccessException | InstantiationException -> 0x0062, Exception -> 0x005d }
            r5.<init>(r2, r6)     // Catch:{ ClassNotFoundException | IllegalAccessException | InstantiationException -> 0x0062, Exception -> 0x005d }
            java.lang.String r6 = "com.qualcomm.qti.activityextension.ActivityNotifier"
            java.lang.Class r6 = r5.loadClass(r6)     // Catch:{ ClassNotFoundException | IllegalAccessException | InstantiationException -> 0x0062, Exception -> 0x005d }
            activityServiceClass = r6     // Catch:{ ClassNotFoundException | IllegalAccessException | InstantiationException -> 0x0062, Exception -> 0x005d }
            java.lang.Class r6 = activityServiceClass     // Catch:{ ClassNotFoundException | IllegalAccessException | InstantiationException -> 0x0062, Exception -> 0x005d }
            java.lang.Object r6 = r6.newInstance()     // Catch:{ ClassNotFoundException | IllegalAccessException | InstantiationException -> 0x0062, Exception -> 0x005d }
            activityServiceObj = r6     // Catch:{ ClassNotFoundException | IllegalAccessException | InstantiationException -> 0x0062, Exception -> 0x005d }
            goto L_0x0067
        L_0x005d:
            r4 = move-exception
            extJarAvail = r3     // Catch:{ all -> 0x0069 }
            monitor-exit(r0)
            return r3
        L_0x0062:
            r4 = move-exception
            extJarAvail = r3     // Catch:{ all -> 0x0069 }
            monitor-exit(r0)
            return r3
        L_0x0067:
            monitor-exit(r0)
            return r4
        L_0x0069:
            r1 = move-exception
            monitor-exit(r0)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityPluginDelegate.loadActivityExtJar():boolean");
    }

    public static synchronized boolean getFeatureFlag() {
        synchronized (ActivityPluginDelegate.class) {
            if (!isEnabled) {
                if (mGetFeatureEnableRetryCount != 0) {
                    boolean z = false;
                    if (Settings.Global.getInt(ActivityThread.currentApplication().getApplicationContext().getContentResolver(), FOREGROUND_ACTIVITY_TRIGGER, 0) == 1) {
                        z = true;
                    }
                    isEnabled = z;
                    mGetFeatureEnableRetryCount--;
                    boolean z2 = isEnabled;
                    return z2;
                }
            }
            boolean z3 = isEnabled;
            return z3;
        }
    }
}
