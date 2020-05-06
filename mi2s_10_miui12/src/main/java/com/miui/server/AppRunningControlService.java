package com.miui.server;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.text.TextUtils;
import android.util.Slog;
import com.android.server.pm.DumpState;
import com.android.server.wm.ActivityStackSupervisorInjector;
import java.util.ArrayList;
import java.util.List;
import miui.security.IAppRunningControlManager;

public class AppRunningControlService extends IAppRunningControlManager.Stub {
    private static final String TAG = "AppRunningControlService";
    private static ArrayList<String> sNotDisallow = new ArrayList<>();
    private List<String> mAppsDisallowRunning = new ArrayList();
    private Context mContext;
    private Intent mDisallowRunningAppIntent;
    private boolean mIsBlackListEnable;

    static {
        sNotDisallow.add("com.lbe.security.miui");
        sNotDisallow.add(ActivityStackSupervisorInjector.MIUI_APP_LOCK_PACKAGE_NAME);
        sNotDisallow.add("com.android.updater");
        sNotDisallow.add("com.xiaomi.market");
        sNotDisallow.add("com.xiaomi.finddevice");
        sNotDisallow.add("com.miui.home");
    }

    public AppRunningControlService(Context context) {
        this.mContext = context;
    }

    public void setDisallowRunningList(List<String> list, Intent intent) {
        checkPermission();
        if (intent == null) {
            Slog.w(TAG, "setDisallowRunningList intent can't be null");
            return;
        }
        this.mDisallowRunningAppIntent = intent;
        this.mAppsDisallowRunning.clear();
        if (list == null || list.isEmpty()) {
            Slog.d(TAG, "setDisallowRunningList clear list.");
            return;
        }
        for (String pkgName : list) {
            if (!sNotDisallow.contains(pkgName)) {
                this.mAppsDisallowRunning.add(pkgName);
            }
        }
    }

    public void setBlackListEnable(boolean isEnable) {
        checkPermission();
        this.mIsBlackListEnable = isEnable;
    }

    public Intent getBlockActivityIntent(String packageName, Intent intent, boolean fromActivity, int requestCode) {
        if (!this.mIsBlackListEnable) {
            return null;
        }
        if (TextUtils.isEmpty(packageName)) {
            Slog.w(TAG, "getBlockActivityIntent packageName can't be null");
            return null;
        } else if (!this.mAppsDisallowRunning.contains(packageName)) {
            return null;
        } else {
            Intent result = (Intent) this.mDisallowRunningAppIntent.clone();
            result.putExtra("packageName", packageName);
            if (intent != null) {
                if ((intent.getFlags() & DumpState.DUMP_APEX) != 0) {
                    result.addFlags(DumpState.DUMP_APEX);
                }
                result.addFlags(DumpState.DUMP_SERVICE_PERMISSIONS);
                if (!fromActivity) {
                    result.addFlags(268435456);
                } else if (requestCode >= 0) {
                    result.addFlags(DumpState.DUMP_APEX);
                }
            }
            return result;
        }
    }

    private boolean matchRuleInner(String pkgName, int wakeType) {
        if (this.mIsBlackListEnable && wakeType != 1) {
            return this.mAppsDisallowRunning.contains(pkgName);
        }
        return false;
    }

    private boolean isBlockActivityInner(Intent intent) {
        if (intent == null || this.mDisallowRunningAppIntent == null) {
            return false;
        }
        return TextUtils.equals(intent.getAction(), this.mDisallowRunningAppIntent.getAction());
    }

    private void checkPermission() {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.FORCE_STOP_PACKAGES") != 0) {
            String msg = "Permission Denial from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires " + "android.permission.FORCE_STOP_PACKAGES";
            Slog.w(TAG, msg);
            throw new SecurityException(msg);
        }
    }

    public boolean matchRule(String pkgName, int wakeType) {
        return matchRuleInner(pkgName, wakeType);
    }

    public static boolean isBlockActivity(Intent intent) {
        AppRunningControlService appRunningControlService = SecurityManagerService.getAppRunningControlService();
        if (appRunningControlService != null) {
            return appRunningControlService.isBlockActivityInner(intent);
        }
        Slog.w(TAG, "AppRunningControlService is null");
        return false;
    }

    public List<String> getNotDisallowList() {
        return sNotDisallow;
    }
}
