package com.android.server.notification;

import android.app.Notification;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Binder;
import android.os.UserHandle;
import android.text.TextUtils;

public class NotificationManagerServiceInjectorBase {
    private static final String EXTRA_TARGET_PKG = "xmsf_target_package";
    private static final int INVALID_UID = -1;
    private static final String SPLIT_CHAR = "\\|";
    private static final int SYSTEM_APP_MASK = 129;
    private static final String XMSF_FAKE_CONDITION_PROVIDER_PATH = "xmsf_fake_condition_provider_path";
    private static final String XMSF_PACKAGE_NAME = "com.xiaomi.xmsf";

    private static boolean isXmsf(String pkg) {
        return XMSF_PACKAGE_NAME.equals(pkg);
    }

    private static int getUidByPkg(Context context, String pkg, int userId) {
        try {
            if (!TextUtils.isEmpty(pkg)) {
                return context.getPackageManager().getPackageUidAsUser(pkg, userId);
            }
            return -1;
        } catch (Exception e) {
            return -1;
        }
    }

    private static String getAppPkgFromChannel(String id) {
        if (TextUtils.isEmpty(id)) {
            return null;
        }
        String[] array = id.split(SPLIT_CHAR);
        if (array.length >= 2) {
            return array[1];
        }
        return null;
    }

    private static String getAppPkgFromNotification(Notification n) {
        if (n == null || n.extras == null) {
            return null;
        }
        return n.extras.getString(EXTRA_TARGET_PKG);
    }

    private static InjectInfo getInjectInfoFromChannel(Context context, String id) {
        int appUid;
        if (TextUtils.isEmpty(id)) {
            return null;
        }
        String appPkg = getAppPkgFromChannel(id);
        if (isXmsf(appPkg) || (appUid = getUidByPkg(context, appPkg, UserHandle.getCallingUserId())) == -1) {
            return null;
        }
        return new InjectInfo(appPkg, appUid);
    }

    static InjectInfo injectDeleteNotificationChannel(Context context, String pkg, String channelId) {
        if (isXmsf(pkg)) {
            return getInjectInfoFromChannel(context, channelId);
        }
        return null;
    }

    static InjectInfo injectDeleteNotificationChannelGroup(Context context, String pkg, String groupId) {
        if (isXmsf(pkg)) {
            return getInjectInfoFromChannel(context, groupId);
        }
        return null;
    }

    static String getEnqueueNotificationWithTagPkg(String pkg, String opPkg, Notification n) {
        if (isXmsf(pkg) && isXmsf(opPkg)) {
            String appPkg = getAppPkgFromNotification(n);
            if (!TextUtils.isEmpty(appPkg)) {
                return appPkg;
            }
        }
        return pkg;
    }

    static String getCallerPkg(String pkg, String opPkg, String defaultPkg) {
        if (!isXmsf(opPkg) || isXmsf(pkg)) {
            return defaultPkg;
        }
        return opPkg;
    }

    static int getResolveNotificationUid(Context context, String pkg, String opPkg, int userId, int defaultUid) {
        int appUid;
        if (!isXmsf(opPkg) || isXmsf(pkg) || (appUid = getUidByPkg(context, pkg, userId)) == -1) {
            return defaultUid;
        }
        return appUid;
    }

    static String getPlayVibrationPkg(String pkg, String opPkg, String defaultPkg) {
        if (!isXmsf(opPkg) || isXmsf(pkg)) {
            return defaultPkg;
        }
        return pkg;
    }

    static boolean checkCallerIsXmsf(Context context, String callingPkg, String targetPkg, int callingUid, int userId) {
        if (!isXmsf(callingPkg) || isXmsf(targetPkg)) {
            return false;
        }
        return checkCallerIsXmsfInternal(context, callingUid, userId);
    }

    static boolean checkCallerIsXmsf(Context context) {
        return checkCallerIsXmsfInternal(context, Binder.getCallingUid(), UserHandle.getCallingUserId());
    }

    private static boolean checkCallerIsXmsfInternal(Context context, int callingUid, int userId) {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfoAsUser(XMSF_PACKAGE_NAME, 0, userId);
            if (ai == null || !UserHandle.isSameApp(ai.uid, callingUid) || (ai.flags & 129) == 0) {
                return false;
            }
            return true;
        } catch (Exception e) {
        }
    }

    static boolean checkIsXmsfFakeConditionProviderEnabled(Context context, String path) {
        return XMSF_FAKE_CONDITION_PROVIDER_PATH.equals(path) && checkCallerIsXmsf(context);
    }

    static class InjectInfo {
        public String appPkg;
        public int appUid;

        public InjectInfo(String pkg, int uid) {
            this.appPkg = pkg;
            this.appUid = uid;
        }
    }
}
