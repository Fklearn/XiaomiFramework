package com.android.server.pm;

import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.ActivityOptions;
import android.app.AppOpsManager;
import android.app.IApplicationThread;
import android.app.admin.DevicePolicyEventLogger;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ICrossProfileApps;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManagerInternal;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.Preconditions;
import com.android.server.LocalServices;
import com.android.server.display.color.DisplayTransformManager;
import com.android.server.wm.ActivityTaskManagerInternal;
import java.util.ArrayList;
import java.util.List;

public class CrossProfileAppsServiceImpl extends ICrossProfileApps.Stub {
    private static final String TAG = "CrossProfileAppsService";
    private Context mContext;
    private Injector mInjector;

    @VisibleForTesting
    public interface Injector {
        long clearCallingIdentity();

        ActivityManagerInternal getActivityManagerInternal();

        ActivityTaskManagerInternal getActivityTaskManagerInternal();

        AppOpsManager getAppOpsManager();

        int getCallingUid();

        UserHandle getCallingUserHandle();

        int getCallingUserId();

        PackageManager getPackageManager();

        PackageManagerInternal getPackageManagerInternal();

        UserManager getUserManager();

        void restoreCallingIdentity(long j);
    }

    public CrossProfileAppsServiceImpl(Context context) {
        this(context, new InjectorImpl(context));
    }

    @VisibleForTesting
    CrossProfileAppsServiceImpl(Context context, Injector injector) {
        this.mContext = context;
        this.mInjector = injector;
    }

    public List<UserHandle> getTargetUserProfiles(String callingPackage) {
        Preconditions.checkNotNull(callingPackage);
        verifyCallingPackage(callingPackage);
        DevicePolicyEventLogger.createEvent(DisplayTransformManager.LEVEL_COLOR_MATRIX_DISPLAY_WHITE_BALANCE).setStrings(new String[]{callingPackage}).write();
        return getTargetUserProfilesUnchecked(callingPackage, this.mInjector.getCallingUserId());
    }

    public void startActivityAsUser(IApplicationThread caller, String callingPackage, ComponentName component, int userId, boolean launchMainActivity) throws RemoteException {
        Bundle bundle;
        String str = callingPackage;
        ComponentName componentName = component;
        int i = userId;
        Preconditions.checkNotNull(callingPackage);
        Preconditions.checkNotNull(component);
        verifyCallingPackage(str);
        DevicePolicyEventLogger.createEvent(126).setStrings(new String[]{str}).write();
        int callerUserId = this.mInjector.getCallingUserId();
        int callingUid = this.mInjector.getCallingUid();
        if (!getTargetUserProfilesUnchecked(str, callerUserId).contains(UserHandle.of(userId))) {
            throw new SecurityException(str + " cannot access unrelated user " + i);
        } else if (str.equals(component.getPackageName())) {
            Intent launchIntent = new Intent();
            if (launchMainActivity) {
                launchIntent.setAction("android.intent.action.MAIN");
                launchIntent.addCategory("android.intent.category.LAUNCHER");
                launchIntent.addFlags(270532608);
                launchIntent.setPackage(component.getPackageName());
            } else if (callerUserId == i || (ActivityManager.checkComponentPermission("android.permission.INTERACT_ACROSS_PROFILES", callingUid, -1, true) == 0 && isSameProfileGroup(callerUserId, i))) {
                launchIntent.setComponent(componentName);
            } else {
                throw new SecurityException("Attempt to launch activity without required android.permission.INTERACT_ACROSS_PROFILES permission or target user is not in the same profile group.");
            }
            verifyActivityCanHandleIntentAndExported(launchIntent, componentName, callingUid, i);
            launchIntent.setPackage((String) null);
            launchIntent.setComponent(componentName);
            ActivityTaskManagerInternal activityTaskManagerInternal = this.mInjector.getActivityTaskManagerInternal();
            if (launchMainActivity) {
                bundle = ActivityOptions.makeOpenCrossProfileAppsAnimation().toBundle();
            } else {
                bundle = null;
            }
            activityTaskManagerInternal.startActivityAsUser(caller, callingPackage, launchIntent, bundle, userId);
        } else {
            throw new SecurityException(str + " attempts to start an activity in other package - " + component.getPackageName());
        }
    }

    private List<UserHandle> getTargetUserProfilesUnchecked(String callingPackage, int callingUserId) {
        long ident = this.mInjector.clearCallingIdentity();
        try {
            int[] enabledProfileIds = this.mInjector.getUserManager().getEnabledProfileIds(callingUserId);
            List<UserHandle> targetProfiles = new ArrayList<>();
            for (int userId : enabledProfileIds) {
                if (userId != callingUserId) {
                    if (isPackageEnabled(callingPackage, userId)) {
                        targetProfiles.add(UserHandle.of(userId));
                    }
                }
            }
            return targetProfiles;
        } finally {
            this.mInjector.restoreCallingIdentity(ident);
        }
    }

    private boolean isPackageEnabled(String packageName, int userId) {
        int callingUid = this.mInjector.getCallingUid();
        long ident = this.mInjector.clearCallingIdentity();
        try {
            PackageInfo info = this.mInjector.getPackageManagerInternal().getPackageInfo(packageName, 786432, callingUid, userId);
            return info != null && info.applicationInfo.enabled;
        } finally {
            this.mInjector.restoreCallingIdentity(ident);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    private void verifyActivityCanHandleIntentAndExported(Intent launchIntent, ComponentName component, int callingUid, int userId) {
        long ident = this.mInjector.clearCallingIdentity();
        try {
            List<ResolveInfo> apps = this.mInjector.getPackageManagerInternal().queryIntentActivities(launchIntent, 786432, callingUid, userId);
            int size = apps.size();
            int i = 0;
            while (i < size) {
                ActivityInfo activityInfo = apps.get(i).activityInfo;
                if (!TextUtils.equals(activityInfo.packageName, component.getPackageName()) || !TextUtils.equals(activityInfo.name, component.getClassName()) || !activityInfo.exported) {
                    i++;
                } else {
                    return;
                }
            }
            throw new SecurityException("Attempt to launch activity without  category Intent.CATEGORY_LAUNCHER or activity is not exported" + component);
        } finally {
            this.mInjector.restoreCallingIdentity(ident);
        }
    }

    private boolean isSameProfileGroup(int callerUserId, int userId) {
        long ident = this.mInjector.clearCallingIdentity();
        try {
            return this.mInjector.getUserManager().isSameProfileGroup(callerUserId, userId);
        } finally {
            this.mInjector.restoreCallingIdentity(ident);
        }
    }

    private void verifyCallingPackage(String callingPackage) {
        this.mInjector.getAppOpsManager().checkPackage(this.mInjector.getCallingUid(), callingPackage);
    }

    private static class InjectorImpl implements Injector {
        private Context mContext;

        public InjectorImpl(Context context) {
            this.mContext = context;
        }

        public int getCallingUid() {
            return Binder.getCallingUid();
        }

        public int getCallingUserId() {
            return UserHandle.getCallingUserId();
        }

        public UserHandle getCallingUserHandle() {
            return Binder.getCallingUserHandle();
        }

        public long clearCallingIdentity() {
            return Binder.clearCallingIdentity();
        }

        public void restoreCallingIdentity(long token) {
            Binder.restoreCallingIdentity(token);
        }

        public UserManager getUserManager() {
            return (UserManager) this.mContext.getSystemService(UserManager.class);
        }

        public PackageManagerInternal getPackageManagerInternal() {
            return (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        }

        public PackageManager getPackageManager() {
            return this.mContext.getPackageManager();
        }

        public AppOpsManager getAppOpsManager() {
            return (AppOpsManager) this.mContext.getSystemService(AppOpsManager.class);
        }

        public ActivityManagerInternal getActivityManagerInternal() {
            return (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
        }

        public ActivityTaskManagerInternal getActivityTaskManagerInternal() {
            return (ActivityTaskManagerInternal) LocalServices.getService(ActivityTaskManagerInternal.class);
        }
    }
}
