package com.android.server.am;

import android.app.AppGlobals;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.IPackageManager;
import android.content.pm.ParceledListSlice;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.ArraySet;
import com.android.internal.os.BackgroundThread;
import com.android.server.am.PendingIntentRecord;
import com.miui.server.AccessController;
import java.util.List;
import miui.security.WakePathChecker;
import miui.util.Log;

public class PendingIntentRecordInjector {
    private static final int MSG_CLEAR = 1;
    private static final String TAG = "PendingIntentRecordInjector";
    private static ArraySet<String> sIgnorePackages = new ArraySet<>(2);
    /* access modifiers changed from: private */
    public static Object sLock = new Object();
    private static Handler sMessageHanlder = new Handler(BackgroundThread.get().getLooper()) {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                synchronized (PendingIntentRecordInjector.sLock) {
                    PendingIntentRecordInjector.sPendingPackages.clear();
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public static ArraySet<String> sPendingPackages = new ArraySet<>();

    static {
        sIgnorePackages.add(AccessController.PACKAGE_SYSTEMUI);
        sIgnorePackages.add("com.miui.notification");
        sIgnorePackages.add("com.android.keyguard");
        sIgnorePackages.add("com.miui.home");
        sIgnorePackages.add("com.mi.android.globallauncher");
        sIgnorePackages.add("com.xiaomi.xmsf");
        sIgnorePackages.add("com.google.android.wearable.app.cn");
    }

    public static void preSendInner(PendingIntentRecord.Key key, Intent intent) {
        if (intent != null && key != null) {
            try {
                if (sIgnorePackages.contains(ExtraActivityManagerService.getPackageNameByPid(Binder.getCallingPid()))) {
                    int wakeType = 8;
                    int i = key.type;
                    if (i == 1) {
                        wakeType = 2;
                    } else if (i == 2) {
                        intent.addMiuiFlags(2);
                        if (!TextUtils.isEmpty(getTargetPkg(key, intent))) {
                            WakePathChecker.getInstance().recordWakePathCall("notification", getTargetPkg(key, intent), 1, UserHandle.myUserId(), key.userId, true);
                            return;
                        }
                        return;
                    } else if (i != 4) {
                        return;
                    }
                    String targetPkg = getTargetPkg(key, intent);
                    if (!TextUtils.isEmpty(targetPkg)) {
                        synchronized (sLock) {
                            sPendingPackages.add(targetPkg);
                        }
                        sMessageHanlder.removeMessages(1);
                        sMessageHanlder.sendMessageDelayed(sMessageHanlder.obtainMessage(1), 5000);
                        WakePathChecker.getInstance().recordWakePathCall("notification", targetPkg, wakeType, UserHandle.myUserId(), key.userId, true);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "preSendInner error", e);
            }
        }
    }

    public static boolean containsPendingIntent(String packageName) {
        boolean contains;
        synchronized (sLock) {
            contains = sPendingPackages.contains(packageName);
        }
        return contains;
    }

    private static String getTargetPkg(PendingIntentRecord.Key key, Intent intent) throws Exception {
        List<ResolveInfo> receivers;
        ComponentName component;
        String targetPkg = intent.getPackage();
        if (targetPkg == null && (component = intent.getComponent()) != null) {
            targetPkg = component.getPackageName();
        }
        if (targetPkg == null) {
            IPackageManager pm = AppGlobals.getPackageManager();
            int userId = UserHandle.getCallingUserId();
            if (key.type == 4) {
                ResolveInfo resolveIntent = pm.resolveService(intent, (String) null, 1024, userId);
                if (!(resolveIntent == null || resolveIntent.serviceInfo == null)) {
                    targetPkg = resolveIntent.serviceInfo.packageName;
                }
            } else {
                ParceledListSlice<ResolveInfo> qeury = pm.queryIntentReceivers(intent, (String) null, 1024, userId);
                if (qeury == null) {
                    return null;
                }
                if (Build.VERSION.SDK_INT > 23) {
                    receivers = ((ParceledListSlice) qeury).getList();
                } else {
                    receivers = (List) qeury;
                }
                if (receivers != null && receivers.size() == 1) {
                    ResolveInfo resolveInfo = receivers.get(0);
                    if (resolveInfo.activityInfo != null) {
                        targetPkg = resolveInfo.activityInfo.packageName;
                    }
                }
            }
        }
        if (targetPkg == null) {
            return key.packageName;
        }
        return targetPkg;
    }
}
