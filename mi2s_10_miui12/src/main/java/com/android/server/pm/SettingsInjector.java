package com.android.server.pm;

import android.content.Intent;
import android.content.pm.PackageParser;
import android.miui.AppOpsUtils;
import android.os.Build;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import miui.securityspace.XSpaceConstant;
import miui.securityspace.XSpaceUserHandle;

public class SettingsInjector {
    private static final String ANDROID_INSTALLER = "com.android.packageinstaller";
    private static final String GOOGLE_INSTALLER = "com.google.android.packageinstaller";
    private static final String MIUI_ACTION_PACKAGE_FIRST_LAUNCH = "miui.intent.action.PACKAGE_FIRST_LAUNCH";
    private static final String MIUI_INSTALLER = "com.miui.packageinstaller";
    private static final String MIUI_PERMISSION = "miui.permission.USE_INTERNAL_GENERAL_API";
    /* access modifiers changed from: private */
    public static final String TAG = SettingsInjector.class.getSimpleName();

    public static boolean checkXSpaceApp(PackageSetting ps, int userHandle) {
        if (XSpaceUserHandle.isXSpaceUserId(userHandle)) {
            if (XSpaceConstant.REQUIRED_APPS.contains(ps.pkg.packageName)) {
                ps.setInstalled(true, userHandle);
            } else {
                ps.setInstalled(false, userHandle);
            }
            if (XSpaceConstant.SPECIAL_APPS.containsKey(ps.pkg.packageName)) {
                ArrayList<String> requiredComponent = (ArrayList) XSpaceConstant.SPECIAL_APPS.get(ps.pkg.packageName);
                ArrayList<PackageParser.Component> components = new ArrayList<>();
                components.addAll(ps.pkg.activities);
                components.addAll(ps.pkg.services);
                components.addAll(ps.pkg.receivers);
                components.addAll(ps.pkg.providers);
                Iterator<PackageParser.Component> it = components.iterator();
                while (it.hasNext()) {
                    PackageParser.Component component = it.next();
                    if (!requiredComponent.contains(component.className)) {
                        ps.addDisabledComponent(component.className, userHandle);
                    }
                }
            }
            return true;
        }
        if (Build.VERSION.SDK_INT >= 23 && !miui.os.Build.IS_INTERNATIONAL_BUILD && !miui.os.Build.IS_TABLET) {
            if (MIUI_INSTALLER.equals(ps.pkg.packageName)) {
                ps.setInstalled(!AppOpsUtils.isXOptMode(), userHandle);
                return true;
            } else if (GOOGLE_INSTALLER.equals(ps.pkg.packageName)) {
                ps.setInstalled(AppOpsUtils.isXOptMode(), userHandle);
                return true;
            } else if (ANDROID_INSTALLER.equals(ps.pkg.packageName)) {
                ps.setInstalled(AppOpsUtils.isXOptMode(), userHandle);
                return true;
            }
        }
        return false;
    }

    private static boolean isSystem(PackageSetting pkgSetting) {
        return (pkgSetting.pkgFlags & 1) != 0;
    }

    public static boolean shouldInstallInXSpace(UserHandle installUser, int userId) {
        if (installUser != null || !XSpaceUserHandle.isXSpaceUserId(userId)) {
            return true;
        }
        return false;
    }

    public static void noftifyFirstLaunch(PackageManagerService pms, final PackageSetting pkgSetting, final int userId) {
        if (pkgSetting != null && !isSystem(pkgSetting) && Build.VERSION.SDK_INT >= 19) {
            Runnable task = new Runnable() {
                public void run() {
                    Intent intent;
                    try {
                        Log.i(SettingsInjector.TAG, "notify first launch");
                        intent = new Intent(SettingsInjector.MIUI_ACTION_PACKAGE_FIRST_LAUNCH);
                        intent.putExtra(Settings.ATTR_PACKAGE, PackageSetting.this.name);
                        if (!TextUtils.isEmpty(PackageSetting.this.installerPackageName)) {
                            intent.putExtra("installer", PackageSetting.this.installerPackageName);
                        }
                        intent.putExtra("userId", userId);
                        if (Build.VERSION.SDK_INT > 25) {
                            intent.addFlags(((Integer) Intent.class.getField("FLAG_RECEIVER_INCLUDE_BACKGROUND").get((Object) null)).intValue());
                        }
                    } catch (Exception e) {
                        Log.e(SettingsInjector.TAG, "Intent flag FLAG_RECEIVER_INCLUDE_BACKGROUND not exist");
                    } catch (Throwable t) {
                        Log.e(SettingsInjector.TAG, "notify first launch exception", t);
                        return;
                    }
                    IActivityManagerCompat.sendBroadcast(intent, SettingsInjector.MIUI_PERMISSION);
                }
            };
            if (pms == null || pms.mHandler == null) {
                task.run();
            } else {
                pms.mHandler.post(task);
            }
        }
    }
}
