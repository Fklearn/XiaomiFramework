package com.android.server.pm;

import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageParser;
import android.miui.AppOpsUtils;
import android.os.Binder;
import android.os.SystemProperties;
import android.server.am.SplitScreenReporter;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import com.android.server.pm.permission.BasePermission;
import com.miui.server.AccessController;
import com.miui.server.GreenGuardManagerService;
import com.miui.server.SplashScreenServiceDelegate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import miui.os.Build;

public class DefaultPermissionGrantPolicyInjector {
    private static final int DEFAULT_PACKAGE_INFO_QUERY_FLAGS = 536916096;
    private static String INCALL_UI = "com.android.incallui";
    private static final String[] MIUI_APPS = {"com.android.thememanager", "com.miui.barcodescanner", "com.miui.dmregservice", "com.wdstechnology.android.kryten", "com.miui.notes", "com.miui.weather2", "com.xiaomi.gamecenter", "com.miui.fmradio", "com.android.email", "com.miui.video", "com.miui.player", "com.xiaomi.market", "com.xiaomi.jr", "com.xiaomi.vip", "com.mi.vtalk", "com.xiaomi.gamecenter.sdk.service", "com.mipay.wallet", "com.miui.tsmclient", "org.simalliance.openmobileapi.service", "com.xiaomi.channel", "com.miui.yellowpage", "com.xiaomi.o2o", "com.miui.miuibbs", "com.xiaomi.pass", "com.xiaomi.mircs", "com.android.vending", "com.android.calculator2", "com.xiaomi.scanner", "com.milink.service", "com.miui.sysbase", "com.miui.calculator", "com.miui.milivetalk", "com.miui.smsextra", "com.xiaomi.oga", "com.miui.contentextension", "com.miui.personalassistant", "com.android.storagemonitor", "com.xiaomi.gamecenter.pad", "com.miui.voicetrigger", "com.xiaomi.vipaccount", "com.google.android.gms", GreenGuardManagerService.GREEN_KID_AGENT_PKG_NAME, "com.mobiletools.systemhelper", "com.miui.smarttravel"};
    public static final String[] MIUI_APPS_GLOBAL = {"com.android.bips", "com.miui.home", "com.mi.android.globallauncher", "com.miui.zman"};
    private static final String[] MIUI_SYSTEM_APPS = {"com.miui.core", "com.android.bips", "com.android.soundrecorder", "com.android.fileexplorer", "com.android.calendar", "com.android.deskclock", "com.android.browser", AccessController.PACKAGE_CAMERA, "com.android.mms", "com.xiaomi.xmsf", "com.android.quicksearchbox", "com.miui.home", "com.miui.securityadd", "com.miui.guardprovider", "com.android.providers.downloads", "com.android.providers.downloads.ui", "com.miui.cloudservice", "com.cleanmaster.sdk", "com.android.incallui", "com.trafficctr.miui", "com.opera.max.oem.xiaomi", "com.xiaomi.account", "com.android.contacts", "com.android.bluetooth", "com.miui.cloudbackup", "com.miui.voip", "com.xiaomi.finddevice", "com.xiaomi.payment", "com.miui.virtualsim", AccessController.PACKAGE_GALLERY, "com.miui.compass", "com.miui.bugreport", "com.miui.mipub", "com.miui.backup", "com.xiaomi.midrop", "com.miui.analytics", SplashScreenServiceDelegate.SPLASHSCREEN_PACKAGE, SplashScreenServiceDelegate.SPLASHSCREEN_GLOBAL_PACKAGE, "com.xiaomi.metoknlp", "com.android.htmlviewer", "com.xiaomi.simactivate.service", "com.miui.extraphoto", "com.miui.packageinstaller", "com.google.android.packageinstaller", "com.miui.hybrid", "com.xiaomi.camera.parallelservice", "com.miui.voiceassist", "com.miui.mishare.connectivity", "com.miui.zman"};
    private static final String REQUIRED_PERMISSIONS = "required_permissions";
    private static final Set<String> RUNTIME_PERMISSIONS = new ArraySet();
    private static final String RUNTIME_PERMSSION_PROPTERY = "persist.sys.runtime_perm";
    private static final int STATE_DEF = -1;
    private static final int STATE_GRANT = 0;
    private static final int STATE_REVOKE = 1;
    private static ArrayMap<String, ArrayList<String>> sTempPermissions = new ArrayMap<>();

    static {
        RUNTIME_PERMISSIONS.add("android.permission.READ_PHONE_STATE");
        RUNTIME_PERMISSIONS.add("android.permission.CALL_PHONE");
        RUNTIME_PERMISSIONS.add("android.permission.READ_CALL_LOG");
        RUNTIME_PERMISSIONS.add("android.permission.WRITE_CALL_LOG");
        RUNTIME_PERMISSIONS.add("com.android.voicemail.permission.ADD_VOICEMAIL");
        RUNTIME_PERMISSIONS.add("android.permission.USE_SIP");
        RUNTIME_PERMISSIONS.add("android.permission.PROCESS_OUTGOING_CALLS");
        RUNTIME_PERMISSIONS.add("android.permission.READ_CONTACTS");
        RUNTIME_PERMISSIONS.add("android.permission.WRITE_CONTACTS");
        RUNTIME_PERMISSIONS.add("android.permission.GET_ACCOUNTS");
        RUNTIME_PERMISSIONS.add("android.permission.ACCESS_FINE_LOCATION");
        RUNTIME_PERMISSIONS.add("android.permission.ACCESS_COARSE_LOCATION");
        RUNTIME_PERMISSIONS.add("android.permission.READ_CALENDAR");
        RUNTIME_PERMISSIONS.add("android.permission.WRITE_CALENDAR");
        RUNTIME_PERMISSIONS.add("android.permission.SEND_SMS");
        RUNTIME_PERMISSIONS.add("android.permission.RECEIVE_SMS");
        RUNTIME_PERMISSIONS.add("android.permission.READ_SMS");
        RUNTIME_PERMISSIONS.add("android.permission.RECEIVE_WAP_PUSH");
        RUNTIME_PERMISSIONS.add("android.permission.RECEIVE_MMS");
        RUNTIME_PERMISSIONS.add("android.permission.READ_CELL_BROADCASTS");
        RUNTIME_PERMISSIONS.add("android.permission.RECORD_AUDIO");
        RUNTIME_PERMISSIONS.add("android.permission.CAMERA");
        RUNTIME_PERMISSIONS.add("android.permission.BODY_SENSORS");
        RUNTIME_PERMISSIONS.add("android.permission.READ_EXTERNAL_STORAGE");
        RUNTIME_PERMISSIONS.add("android.permission.WRITE_EXTERNAL_STORAGE");
        RUNTIME_PERMISSIONS.add("android.permission.ACCESS_MEDIA_LOCATION");
        RUNTIME_PERMISSIONS.add("android.permission.ACCESS_BACKGROUND_LOCATION");
        RUNTIME_PERMISSIONS.add("android.permission.ACTIVITY_RECOGNITION");
    }

    static void grantDefaultPermissions(PackageManagerService service, int userId) {
        if (!AppOpsUtils.isXOptMode()) {
            if (Build.IS_INTERNATIONAL_BUILD) {
                int i = 0;
                while (true) {
                    String[] strArr = MIUI_APPS_GLOBAL;
                    if (i < strArr.length) {
                        grantRuntimePermissionsLPw(service, strArr[i], false, true, false, userId);
                        i++;
                    } else {
                        return;
                    }
                }
            } else {
                realGrantDefaultPermissions(service, userId);
            }
        }
    }

    private static void realGrantDefaultPermissions(PackageManagerService service, int userId) {
        int i = 0;
        while (true) {
            String[] strArr = MIUI_SYSTEM_APPS;
            if (i >= strArr.length) {
                break;
            }
            grantRuntimePermissionsLPw(service, strArr[i], false, false, true, userId);
            i++;
        }
        int i2 = 0;
        while (true) {
            String[] strArr2 = MIUI_APPS;
            if (i2 < strArr2.length) {
                grantRuntimePermissionsLPw(service, strArr2[i2], false, true, false, userId);
                i2++;
            } else {
                return;
            }
        }
    }

    private static boolean doesPackageSupportRuntimePermissions(PackageInfo pkg) {
        return pkg.applicationInfo.targetSdkVersion > 22;
    }

    private static void grantRuntimePermissionsLPw(PackageManagerService service, String packageName, boolean systemFixed, boolean userFixed, boolean overrideUserChoice, int userId) {
        List<String> requiredPermissions;
        Set<String> grantablePermissions;
        List<String> requestedPermissions;
        List<String> allRequestedPermissions;
        int i;
        ArrayList<String> grantPermissions;
        String op;
        PackageSetting sysPs;
        PackageManagerService packageManagerService = service;
        int i2 = userId;
        PackageInfo pkg = packageManagerService.getPackageInfo(packageName, DEFAULT_PACKAGE_INFO_QUERY_FLAGS, i2);
        if (pkg != null && doesPackageSupportRuntimePermissions(pkg) && pkg.requestedPermissions != null && pkg.requestedPermissions.length != 0) {
            List<String> requestedPermissions2 = Arrays.asList(pkg.requestedPermissions);
            List<String> requiredPermissions2 = null;
            List<String> allRequestedPermissions2 = requestedPermissions2;
            if (pkg.applicationInfo.metaData != null) {
                String declareStr = pkg.applicationInfo.metaData.getString(REQUIRED_PERMISSIONS);
                if (!TextUtils.isEmpty(declareStr)) {
                    requiredPermissions2 = Arrays.asList(declareStr.split(";"));
                }
            }
            if (!pkg.applicationInfo.isUpdatedSystemApp() || (sysPs = packageManagerService.mSettings.getDisabledSystemPkgLPr(pkg.packageName)) == null || sysPs.pkg == null) {
                requestedPermissions = requestedPermissions2;
                requiredPermissions = requiredPermissions2;
                grantablePermissions = null;
            } else if (!sysPs.pkg.requestedPermissions.isEmpty()) {
                List<String> disablePermissionsList = sysPs.pkg.requestedPermissions;
                List<String> requiredPermissions3 = null;
                if (sysPs.pkg.mAppMetaData != null) {
                    String disableStr = sysPs.pkg.mAppMetaData.getString(REQUIRED_PERMISSIONS);
                    if (!TextUtils.isEmpty(disableStr)) {
                        requiredPermissions3 = Arrays.asList(disableStr.split(";"));
                    }
                }
                if (!requestedPermissions2.equals(disablePermissionsList)) {
                    requestedPermissions = disablePermissionsList;
                    requiredPermissions = requiredPermissions3;
                    grantablePermissions = new ArraySet<>(requestedPermissions2);
                } else {
                    requestedPermissions = requestedPermissions2;
                    requiredPermissions = requiredPermissions3;
                    grantablePermissions = null;
                }
            } else {
                return;
            }
            int grantablePermissionCount = requestedPermissions.size();
            ArrayList<String> grantPermissions2 = new ArrayList<>();
            int i3 = 0;
            while (i3 < grantablePermissionCount) {
                String permission = requestedPermissions.get(i3);
                if (grantablePermissions != null && !grantablePermissions.contains(permission)) {
                    i = i3;
                    allRequestedPermissions = allRequestedPermissions2;
                    grantPermissions = grantPermissions2;
                } else if (!RUNTIME_PERMISSIONS.contains(permission) || !allRequestedPermissions2.contains(permission)) {
                    i = i3;
                    allRequestedPermissions = allRequestedPermissions2;
                    grantPermissions = grantPermissions2;
                } else {
                    int flags = packageManagerService.getPermissionFlags(permission, pkg.packageName, i2);
                    if (requiredPermissions == null || requiredPermissions.contains(permission)) {
                        int flags2 = flags;
                        String permission2 = permission;
                        i = i3;
                        allRequestedPermissions = allRequestedPermissions2;
                        grantPermissions = grantPermissions2;
                        if (!isUserChanged(flags2) || overrideUserChoice || isOTAUpdated(flags2)) {
                            int flags3 = flags2;
                            if ((flags3 & 20) == 0) {
                                if ((i2 == 0 && packageManagerService.checkPermission(permission2, pkg.packageName, i2) == -1) || "android.permission.ACCESS_COARSE_LOCATION".equals(permission2)) {
                                    grantPermissions.add(permission2);
                                }
                                int newFlags = 32 | (flags3 & 14336);
                                if (packageManagerService.getPermissionInfo(permission2, packageManagerService.mContext.getOpPackageName(), 0).isRestricted()) {
                                    int i4 = flags3;
                                    service.updatePermissionFlags(permission2, pkg.packageName, 4096, 4096, true, userId);
                                }
                                packageManagerService.grantRuntimePermission(pkg.packageName, permission2, i2);
                                if (systemFixed) {
                                    newFlags |= 16;
                                } else if (userFixed) {
                                    newFlags |= 2;
                                }
                                service.updatePermissionFlags(permission2, pkg.packageName, newFlags, newFlags, true, userId);
                                if ("android.permission.ACCESS_BACKGROUND_LOCATION".equals(permission2) && (op = AppOpsManager.permissionToOp(permission2)) != null) {
                                    ((AppOpsManager) packageManagerService.mContext.getSystemService(AppOpsManager.class)).setUidMode(op, pkg.applicationInfo.uid, 0);
                                }
                            }
                        }
                    } else if ((flags & 32) != 0) {
                        int i5 = flags;
                        String str = permission;
                        i = i3;
                        allRequestedPermissions = allRequestedPermissions2;
                        grantPermissions = grantPermissions2;
                        service.updatePermissionFlags(permission, pkg.packageName, 4146, 0, true, userId);
                    } else {
                        String str2 = permission;
                        i = i3;
                        allRequestedPermissions = allRequestedPermissions2;
                        grantPermissions = grantPermissions2;
                    }
                }
                i3 = i + 1;
                String str3 = packageName;
                grantPermissions2 = grantPermissions;
                allRequestedPermissions2 = allRequestedPermissions;
            }
            int i6 = i3;
            List<String> list = allRequestedPermissions2;
            ArrayList<String> grantPermissions3 = grantPermissions2;
            if (i2 == 0 && !grantPermissions3.isEmpty()) {
                if (SplitScreenReporter.ACTION_ENTER_SPLIT.equals(SystemProperties.get("ro.miui.has_gmscore")) || Build.IS_INTERNATIONAL_BUILD) {
                    sTempPermissions.put(pkg.packageName, grantPermissions3);
                }
            }
        }
    }

    private static boolean isUserChanged(int flags) {
        return (flags & 3) != 0;
    }

    private static boolean isOTAUpdated(int flags) {
        return ((flags & 2) == 0 || (flags & 32) == 0) ? false : true;
    }

    public static void setCoreRuntimePermissionEnabled(boolean grant, int flags, int userId) {
        if (userId == 0) {
            PackageManagerService service = AppGlobals.getPackageManager();
            if (grant) {
                realGrantDefaultPermissions(service, userId);
                SystemProperties.set(RUNTIME_PERMSSION_PROPTERY, String.valueOf(0));
                return;
            }
            SystemProperties.set(RUNTIME_PERMSSION_PROPTERY, String.valueOf(1));
        }
    }

    public static void grantRuntimePermission(String packageName, int userId) {
        PackageManagerService service = AppGlobals.getPackageManager();
        if (INCALL_UI.equals(packageName)) {
            grantIncallUiPermission(service, userId);
        }
    }

    private static void grantIncallUiPermission(PackageManagerService service, int userId) {
        ArrayList<String> perms = new ArrayList<>();
        perms.add("android.permission.RECORD_AUDIO");
        perms.add("android.permission.WRITE_EXTERNAL_STORAGE");
        perms.add("android.permission.READ_EXTERNAL_STORAGE");
        perms.add("android.permission.READ_CONTACTS");
        Iterator<String> it = perms.iterator();
        while (it.hasNext()) {
            String p = it.next();
            if (service.checkPermission(p, INCALL_UI, userId) == -1) {
                service.grantRuntimePermission(INCALL_UI, p, userId);
            }
        }
    }

    private static void grantRuntimePermissionInternal(String packageName, String name, int userId) {
        PackageManagerService service = AppGlobals.getPackageManager();
        int callingUid = Binder.getCallingUid();
        synchronized (service.mPackages) {
            PackageParser.Package pkg = service.mPackages.get(packageName);
            if (pkg != null) {
                BasePermission bp = service.mSettings.mPermissions.getPermission(name);
                if (bp == null) {
                    throw new IllegalArgumentException("Unknown permission: " + name);
                } else if (((PackageSetting) pkg.mExtras).getPermissionsState().grantRuntimePermission(bp, userId) != -1) {
                    service.mSettings.writeRuntimePermissionsForUserLPr(userId, false);
                }
            } else {
                throw new IllegalArgumentException("Unknown package: " + packageName);
            }
        }
    }

    private static void revokeRuntimePermissionInternal(String packageName, String name, int userId) {
        PackageManagerService service = AppGlobals.getPackageManager();
        synchronized (service.mPackages) {
            PackageParser.Package pkg = service.mPackages.get(packageName);
            if (pkg != null) {
                BasePermission bp = service.mSettings.mPermissions.getPermission(name);
                if (bp != null) {
                    SettingBase sb = (SettingBase) pkg.mExtras;
                    if (sb == null) {
                        throw new IllegalArgumentException("Unknown package: " + packageName);
                    } else if (sb.getPermissionsState().revokeRuntimePermission(bp, userId) != -1) {
                        service.mSettings.writeRuntimePermissionsForUserLPr(userId, true);
                    }
                } else {
                    throw new IllegalArgumentException("Unknown permission: " + name);
                }
            } else {
                throw new IllegalArgumentException("Unknown package: " + packageName);
            }
        }
    }

    public static void revokeAllPermssions(PackageManagerService service) {
        if (!AppOpsUtils.isXOptMode()) {
            return;
        }
        if (Build.IS_INTERNATIONAL_BUILD || SplitScreenReporter.ACTION_ENTER_SPLIT.equals(SystemProperties.get("ro.miui.has_gmscore"))) {
            try {
                for (String pkg : sTempPermissions.keySet()) {
                    ArrayList<String> permissions = sTempPermissions.get(pkg);
                    if (!"com.google.android.packageinstaller".equals(pkg)) {
                        Iterator<String> it = permissions.iterator();
                        while (it.hasNext()) {
                            String p = it.next();
                            if (!"com.google.android.gms".equals(pkg) || (!"android.permission.RECORD_AUDIO".equals(p) && !"android.permission.ACCESS_FINE_LOCATION".equals(p))) {
                                try {
                                    service.revokeRuntimePermission(pkg, p, 0);
                                } catch (Exception e) {
                                    Log.d("DefaultPermissionGrantPolicyInjector", "revokeAllPermssions error:" + e.toString());
                                }
                            }
                        }
                    }
                }
                Object obj = "com.miui.packageinstaller";
                List<String> permissionList = new ArrayList<>();
                permissionList.add("android.permission.READ_EXTERNAL_STORAGE");
                permissionList.add("android.permission.WRITE_EXTERNAL_STORAGE");
                for (String permItem : permissionList) {
                    try {
                        revokeRuntimePermissionInternal("com.miui.packageinstaller", permItem, 0);
                    } catch (Exception e2) {
                        Log.d("DefaultPermissionGrantPolicyInjector", "revokeRuntimePermissionInternal error:" + e2.toString());
                    }
                }
            } catch (Exception e3) {
                e3.printStackTrace();
            }
        }
    }

    public static void grantMiuiPackageInstallerPermssions(PackageManagerService service) {
        List<String> permissionList = new ArrayList<>();
        permissionList.add("android.permission.READ_EXTERNAL_STORAGE");
        permissionList.add("android.permission.WRITE_EXTERNAL_STORAGE");
        permissionList.add("android.permission.READ_PHONE_STATE");
        for (String permItem : permissionList) {
            try {
                grantRuntimePermissionInternal("com.miui.packageinstaller", permItem, 0);
            } catch (Exception e) {
                Log.d("DefaultPermissionGrantPolicyInjector", "grantMiuiPackageInstallerPermssions error:" + e.toString());
            }
        }
    }
}
