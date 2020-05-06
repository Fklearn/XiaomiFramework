package com.miui.server;

import android.app.AppGlobals;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageInfo;
import android.content.pm.ParceledListSlice;
import android.database.ContentObserver;
import android.os.Binder;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.server.am.SplitScreenReporter;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Slog;
import com.android.server.pm.DumpState;
import com.android.server.pm.ExtraPackageManagerService;
import com.android.server.pm.PackageManagerService;
import com.android.server.wm.ActivityStackSupervisorInjector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import miui.securityspace.CrossUserUtils;
import miui.securityspace.XSpaceConstant;
import miui.securityspace.XSpaceIntentCompat;
import miui.securityspace.XSpaceUserHandle;

public class XSpaceManagerService {
    private static final String ACTION_START_DUAL_ANIMATION = "miui.intent.action.START_DUAL_ANIMATION";
    private static final String EXTRA_XSPACE_CACHED_USERID = "android.intent.extra.xspace_cached_uid";
    private static final String EXTRA_XSPACE_RESOLVE_INTENT_AGAIN = "android.intent.extra.xspace_resolve_intent_again";
    private static final String EXTRA_XSPACE_USERID_SELECTED = "android.intent.extra.xspace_userid_selected";
    private static final ArrayList<String> HISTORY_PACKAGE = new ArrayList<>();
    private static final int MAX_COMPETE_XSPACE_NOTIFICATION_TIMES = 1;
    private static final String PACKAGE_ALIPAY = "com.eg.android.AlipayGphone";
    private static final String PACKAGE_LINKER = "@";
    private static final String PACKAGE_SECURITYADD = "com.miui.securityadd";
    private static final String PACKAGE_SETTING = "com.android.settings";
    private static final String SYSTEM_PROP_XSPACE_CREATED = "persist.sys.xspace_created";
    /* access modifiers changed from: private */
    public static final String TAG = XSpaceManagerService.class.getSimpleName();
    private static final String XIAOMI_GAMECENTER_SDK_PKGNAME = "com.xiaomi.gamecenter.sdk.service";
    private static final String XSPACE_ANIMATION_STATUS = "xspace_animation_status";
    private static final int XSPACE_ANIMATION_STATUS_DEFAULT = 2;
    private static final int XSPACE_ANIMATION_STATUS_OFF = 0;
    private static final int XSPACE_ANIMATION_STATUS_ON = 1;
    private static final int XSPACE_APP_LIST_INIT_NUMBER = sXSpaceInstalledPackagesSelfLocked.size();
    private static final String XSPACE_CLOUD_CONTROL_STATUS = "dual_animation_switch";
    private static final String XSPACE_SERVICE_COMPONENT = "com.miui.securitycore/com.miui.xspace.service.XSpaceService";
    /* access modifiers changed from: private */
    public static Context mContext;
    private static final ArrayList<String> sAddUserPackagesBlackList = new ArrayList<>();
    /* access modifiers changed from: private */
    public static boolean sAnimStatus = true;
    private static final HashMap<String, Integer> sCachedCallingRelationSelfLocked = new HashMap<>();
    private static final ArrayList<String> sCrossUserAimPackagesWhiteList = new ArrayList<>();
    private static final ArrayList<String> sCrossUserCallingPackagesWhiteList = new ArrayList<>();
    private static final ArrayList<String> sCrossUserDisableComponentActionWhiteList = new ArrayList<>();
    public static boolean sIsXSpaceActived = false;
    public static boolean sIsXSpaceCreated = false;
    private static long sLastTime;
    private static final LauncherApps.Callback sPackageCallback = new LauncherApps.Callback() {
        public void onPackageRemoved(String packageName, UserHandle user) {
            XSpaceManagerService.onPackageCallback(packageName, user, "android.intent.action.PACKAGE_REMOVED");
        }

        public void onPackageAdded(String packageName, UserHandle user) {
            XSpaceManagerService.onPackageCallback(packageName, user, "android.intent.action.PACKAGE_ADDED");
        }

        public void onPackageChanged(String packageName, UserHandle user) {
        }

        public void onPackagesAvailable(String[] packageNames, UserHandle user, boolean replacing) {
        }

        public void onPackagesUnavailable(String[] packageNames, UserHandle user, boolean replacing) {
        }
    };
    private static final ArrayList<String> sPublicActionList = new ArrayList<>();
    /* access modifiers changed from: private */
    public static ContentResolver sResolver;
    private static final ArrayList<String> sXSpaceInstalledPackagesSelfLocked = new ArrayList<>();
    private static final ArrayList<String> sXspaceAnimWhiteList = new ArrayList<>();

    static {
        sCrossUserCallingPackagesWhiteList.add(PackageManagerService.PLATFORM_PACKAGE_NAME);
        sCrossUserCallingPackagesWhiteList.add(PACKAGE_SETTING);
        sCrossUserCallingPackagesWhiteList.add(AccessController.PACKAGE_SYSTEMUI);
        sCrossUserCallingPackagesWhiteList.add(ActivityStackSupervisorInjector.MIUI_APP_LOCK_PACKAGE_NAME);
        sCrossUserCallingPackagesWhiteList.add("com.miui.home");
        sCrossUserCallingPackagesWhiteList.add("com.mi.android.globallauncher");
        sCrossUserCallingPackagesWhiteList.add("com.lbe.security.miui");
        sCrossUserDisableComponentActionWhiteList.add("android.nfc.action.TECH_DISCOVERED");
        sPublicActionList.add("android.intent.action.SEND");
        sPublicActionList.add("android.intent.action.SEND_MULTIPLE");
        sPublicActionList.add("android.intent.action.SENDTO");
        sCrossUserAimPackagesWhiteList.add("com.xiaomi.xmsf");
        sCrossUserAimPackagesWhiteList.addAll(XSpaceConstant.REQUIRED_APPS);
        sCrossUserAimPackagesWhiteList.remove(XIAOMI_GAMECENTER_SDK_PKGNAME);
        sCrossUserAimPackagesWhiteList.add("com.google.android.gsf");
        sCrossUserAimPackagesWhiteList.add("com.google.android.gsf.login");
        sCrossUserAimPackagesWhiteList.add("com.google.android.gms");
        sCrossUserAimPackagesWhiteList.add("com.google.android.play.games");
        sXspaceAnimWhiteList.add("com.miui.home");
        sXspaceAnimWhiteList.add("com.mi.android.globallauncher");
        sAddUserPackagesBlackList.add("com.android.contacts");
        sXSpaceInstalledPackagesSelfLocked.add(XIAOMI_GAMECENTER_SDK_PKGNAME);
    }

    public static Intent checkXSpaceControl(Context context, ActivityInfo aInfo, Intent intent, boolean fromActivity, int requestCode, int userId, String callingPackage) {
        String aimPkg = getAimPkg(intent);
        String action = intent.getAction();
        int callingUserId = Binder.getCallingUserHandle().getIdentifier();
        String str = TAG;
        Slog.w(str, "checkXSpaceControl, from:" + callingPackage + ", to:" + aimPkg + ", with act:" + action + ", callingUserId:" + callingUserId + ", toUserId:" + userId);
        if (callingUserId != 0 && callingUserId != 999) {
            return intent;
        }
        if ((callingUserId == 0 && userId == 0 && !sXSpaceInstalledPackagesSelfLocked.contains(aimPkg)) || (intent.getFlags() & DumpState.DUMP_DEXOPT) != 0) {
            return intent;
        }
        if (checkDualAnimationEnable(aimPkg, userId, callingPackage)) {
            return creatDualAnimIntent(intent);
        }
        if ((sCrossUserCallingPackagesWhiteList.contains(callingPackage) && !sPublicActionList.contains(action)) || sCrossUserAimPackagesWhiteList.contains(aimPkg)) {
            return intent;
        }
        if (sCrossUserDisableComponentActionWhiteList.contains(action) && !isComponentEnabled(aInfo, 999)) {
            return intent;
        }
        if (intent.hasExtra(EXTRA_XSPACE_USERID_SELECTED)) {
            Slog.w(TAG, "from XSpace ResolverActivity");
            intent.removeExtra(EXTRA_XSPACE_RESOLVE_INTENT_AGAIN);
            XSpaceIntentCompat.prepareToLeaveUser(intent, callingUserId);
            putCachedCallingRelation(aimPkg, callingPackage);
        } else {
            int cachedToUserId = getToUserIdFromCachedCallingRelation(aimPkg, callingPackage);
            if (cachedToUserId != -10000) {
                Slog.w(TAG, "using cached calling relation");
                intent.putExtra(EXTRA_XSPACE_CACHED_USERID, cachedToUserId);
            } else {
                synchronized (sXSpaceInstalledPackagesSelfLocked) {
                    if (sXSpaceInstalledPackagesSelfLocked.contains(aimPkg)) {
                        if ((!intent.hasExtra("android.intent.extra.auth_to_call_xspace") || !checkCallXSpacePermission(callingPackage)) && !PACKAGE_ALIPAY.equals(aimPkg)) {
                            Slog.w(TAG, "pop up ResolverActivity");
                            intent = getResolverActivity(intent, aimPkg, requestCode, callingPackage);
                        } else {
                            Slog.i(TAG, "call XSpace directly");
                            return intent;
                        }
                    } else if (sXSpaceInstalledPackagesSelfLocked.contains(callingPackage)) {
                        Slog.w(TAG, "XSpace installed App to normal App");
                        if (!sAddUserPackagesBlackList.contains(aimPkg)) {
                            XSpaceIntentCompat.prepareToLeaveUser(intent, callingUserId);
                        }
                        intent.putExtra(EXTRA_XSPACE_CACHED_USERID, 0);
                        intent.putExtra("userId", userId);
                        intent.putExtra("calling_relation", true);
                    }
                }
            }
        }
        return intent;
    }

    private static boolean checkDualAnimationEnable(String packageName, int userId, String callingPackage) {
        if (userId != 999 || !sXspaceAnimWhiteList.contains(callingPackage)) {
            return false;
        }
        if (!DateUtils.isToday(sLastTime)) {
            sLastTime = System.currentTimeMillis();
            HISTORY_PACKAGE.clear();
        }
        if (packageName == null || packageName.equals(PACKAGE_SETTING) || HISTORY_PACKAGE.contains(packageName) || !sAnimStatus) {
            return false;
        }
        HISTORY_PACKAGE.add(packageName);
        return true;
    }

    private static Intent creatDualAnimIntent(Intent intent) {
        Intent result = new Intent(ACTION_START_DUAL_ANIMATION);
        result.setPackage(PACKAGE_SECURITYADD);
        result.addFlags(DumpState.DUMP_VOLUMES);
        result.putExtra(EXTRA_XSPACE_CACHED_USERID, 0);
        intent.putExtra("android.intent.extra.auth_to_call_xspace", true);
        result.putExtra("android.intent.extra.INTENT", intent);
        return result;
    }

    public static boolean shouldResolveAgain(Intent intent, String callingPackage) {
        String aimPkg = getAimPkg(intent);
        Intent newIntent = new Intent(intent);
        try {
            if (TextUtils.equals(aimPkg, callingPackage) || !newIntent.hasExtra(EXTRA_XSPACE_RESOLVE_INTENT_AGAIN)) {
                return false;
            }
            intent.removeExtra(EXTRA_XSPACE_RESOLVE_INTENT_AGAIN);
            return true;
        } catch (Exception e) {
            Slog.w(TAG, "Private intent: ", e);
            return false;
        }
    }

    public static int getCachedUserId(Intent intent, String callingPackage) {
        if (TextUtils.equals(getAimPkg(intent), callingPackage) || !intent.hasExtra(EXTRA_XSPACE_CACHED_USERID)) {
            return ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION;
        }
        int cachedUserId = intent.getIntExtra(EXTRA_XSPACE_CACHED_USERID, ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION);
        intent.removeExtra(EXTRA_XSPACE_CACHED_USERID);
        return cachedUserId;
    }

    public static boolean isPublicIntent(Intent intent, String callingPackage) {
        if (TextUtils.isEmpty(callingPackage) || TextUtils.equals(getAimPkg(intent), callingPackage)) {
            return false;
        }
        Intent newIntent = new Intent(intent);
        try {
            XSpaceManagerServiceCompat.setBundleDefusable(false);
            newIntent.hasExtra("");
            return true;
        } catch (Throwable e) {
            Slog.w(TAG, "Private intent", e);
            return false;
        } finally {
            XSpaceManagerServiceCompat.setBundleDefusable(true);
        }
    }

    public static void putCachedCallingRelation(String aimPkg, String callingPackage) {
        synchronized (sCachedCallingRelationSelfLocked) {
            String callingRelationKey = aimPkg + PACKAGE_LINKER + callingPackage;
            int cachedUserId = UserHandle.getCallingUserId();
            sCachedCallingRelationSelfLocked.put(callingRelationKey, Integer.valueOf(cachedUserId));
            Slog.w(TAG, "putCachedCallingRelationm, callingRelationKey:" + callingRelationKey + ", cachedUserId:" + cachedUserId);
        }
    }

    private static int getToUserIdFromCachedCallingRelation(String aimPkg, String callingPackage) {
        int cachedToUserId = ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION;
        synchronized (sCachedCallingRelationSelfLocked) {
            String callingRelationKey = callingPackage + PACKAGE_LINKER + aimPkg;
            Integer toUserIdObj = sCachedCallingRelationSelfLocked.get(callingRelationKey);
            if (toUserIdObj != null) {
                cachedToUserId = toUserIdObj.intValue();
                sCachedCallingRelationSelfLocked.remove(callingRelationKey);
                Slog.w(TAG, "got callingRelationKey :" + callingRelationKey + ", cachedToUserId:" + cachedToUserId);
            }
        }
        return cachedToUserId;
    }

    private static String getAimPkg(Intent intent) {
        ComponentName componentName;
        String aimPkg = intent.getPackage();
        if (aimPkg != null || (componentName = intent.getComponent()) == null) {
            return aimPkg;
        }
        return componentName.getPackageName();
    }

    private static Intent getResolverActivity(Intent intent, String aimPkg, int requestCode, String callingPackage) {
        Intent resolverActivityIntent = new Intent("miui.intent.action.ACTION_XSPACE_RESOLVER_ACTIVITY");
        if (requestCode >= 0) {
            intent.addFlags(DumpState.DUMP_APEX);
        } else if ((intent.getFlags() & DumpState.DUMP_APEX) != 0) {
            resolverActivityIntent.addFlags(DumpState.DUMP_APEX);
        } else {
            resolverActivityIntent.addFlags(268435456);
        }
        intent.putExtra("miui.intent.extra.xspace_resolver_activity_calling_package", callingPackage);
        intent.putExtra(EXTRA_XSPACE_USERID_SELECTED, true);
        resolverActivityIntent.putExtra("android.intent.extra.xspace_resolver_activity_original_intent", intent);
        resolverActivityIntent.putExtra("android.intent.extra.xspace_resolver_activity_aim_package", aimPkg);
        resolverActivityIntent.setClassName(PackageManagerService.PLATFORM_PACKAGE_NAME, "com.android.internal.app.ResolverActivity");
        resolverActivityIntent.putExtra(EXTRA_XSPACE_RESOLVE_INTENT_AGAIN, true);
        return resolverActivityIntent;
    }

    private static boolean checkCallXSpacePermission(String callingPkg) {
        try {
            ApplicationInfo appInfo = AppGlobals.getPackageManager().getApplicationInfo(callingPkg, 0, 0);
            if ((appInfo.flags & 1) > 0 || appInfo.uid <= 1000) {
                return true;
            }
            return false;
        } catch (RemoteException e) {
            String str = TAG;
            Slog.e(str, "Failed to read package info of: " + callingPkg, e);
        }
    }

    public static void init(final Context context) {
        mContext = context;
        sResolver = context.getContentResolver();
        initXSpaceAppList();
        sResolver.registerContentObserver(Settings.Secure.getUriFor("xspace_enabled"), false, new ContentObserver(new Handler()) {
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                XSpaceManagerService.sIsXSpaceActived = MiuiSettings.Secure.getBoolean(XSpaceManagerService.sResolver, "xspace_enabled", false);
                String access$100 = XSpaceManagerService.TAG;
                Slog.w(access$100, "update XSpace status, active:" + XSpaceManagerService.sIsXSpaceActived);
            }
        });
        ContentObserver animListener = new ContentObserver(new Handler()) {
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                int settingStatus = Settings.Global.getInt(XSpaceManagerService.mContext.getContentResolver(), XSpaceManagerService.XSPACE_ANIMATION_STATUS, 2);
                if (settingStatus == 2) {
                    boolean unused = XSpaceManagerService.sAnimStatus = MiuiSettings.SettingsCloudData.getCloudDataBoolean(context.getContentResolver(), XSpaceManagerService.XSPACE_CLOUD_CONTROL_STATUS, Locale.getDefault().toString(), true);
                } else if (settingStatus == 1) {
                    boolean unused2 = XSpaceManagerService.sAnimStatus = true;
                } else {
                    boolean unused3 = XSpaceManagerService.sAnimStatus = false;
                }
                String access$100 = XSpaceManagerService.TAG;
                Slog.d(access$100, "update XSpace Animation status, sAnimStatus = " + XSpaceManagerService.sAnimStatus);
            }
        };
        sResolver.registerContentObserver(Settings.Global.getUriFor(XSPACE_ANIMATION_STATUS), false, animListener);
        animListener.onChange(false);
        ((LauncherApps) context.getSystemService("launcherapps")).registerCallback(sPackageCallback);
        String str = TAG;
        Slog.w(str, "XSpace init, active:" + sIsXSpaceActived);
        sIsXSpaceCreated = CrossUserUtils.hasXSpaceUser(context);
        if (sIsXSpaceCreated) {
            SystemProperties.set(SYSTEM_PROP_XSPACE_CREATED, SplitScreenReporter.ACTION_ENTER_SPLIT);
            startXSpaceService(context, (String) null);
        } else if (needSetBootXSpaceGuideTaskForCompete(mContext)) {
            startXSpaceService(context, "param_intent_value_compete_boot_xspace_guide");
        }
        ExtraPackageManagerService.checkExtraRestoreconFlag(context);
    }

    private static void initXSpaceAppList() {
        ParceledListSlice<PackageInfo> slice = null;
        try {
            slice = AppGlobals.getPackageManager().getInstalledPackages(0, 999);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (slice != null) {
            List<PackageInfo> appList = slice.getList();
            synchronized (sXSpaceInstalledPackagesSelfLocked) {
                for (PackageInfo pkgInfo : appList) {
                    if (!isSystemApp(pkgInfo.applicationInfo) && !XSpaceConstant.GMS_RELATED_APPS.contains(pkgInfo.packageName)) {
                        sXSpaceInstalledPackagesSelfLocked.add(pkgInfo.packageName);
                    }
                }
            }
        }
        if (sXSpaceInstalledPackagesSelfLocked.size() > XSPACE_APP_LIST_INIT_NUMBER) {
            sIsXSpaceActived = true;
        }
        MiuiSettings.Secure.putBoolean(sResolver, "xspace_enabled", sIsXSpaceActived);
        String str = TAG;
        Slog.d(str, "initXSpaceAppList sXSpaceInstalledPackagesSelfLocked =" + sXSpaceInstalledPackagesSelfLocked.toString() + "    XSPACE_APP_LIST_INIT_NUMBER =" + XSPACE_APP_LIST_INIT_NUMBER);
        String str2 = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Reset XSpace enable, active:");
        sb.append(sIsXSpaceActived);
        Slog.w(str2, sb.toString());
    }

    private static boolean isSystemApp(ApplicationInfo appInfo) {
        return (appInfo.flags & 1) > 0 || appInfo.uid <= 1000;
    }

    private static String getPackageActionKey(String pkg, String action) {
        return pkg + ":" + action;
    }

    /* access modifiers changed from: private */
    public static void onPackageCallback(String packageName, UserHandle user, String action) {
        String str = TAG;
        Slog.w(str, "update XSpace App: packageName:" + packageName + ", user:" + user + ", action:" + action);
        if (XSpaceUserHandle.isXSpaceUser(user) && ("android.intent.action.PACKAGE_ADDED".equals(action) || "android.intent.action.PACKAGE_REMOVED".equals(action))) {
            sIsXSpaceCreated = true;
            SystemProperties.set(SYSTEM_PROP_XSPACE_CREATED, SplitScreenReporter.ACTION_ENTER_SPLIT);
            if ("android.intent.action.PACKAGE_ADDED".equals(action)) {
                boolean isSystemApp = false;
                try {
                    ApplicationInfo appInfo = AppGlobals.getPackageManager().getApplicationInfo(packageName, 0, 999);
                    if (appInfo != null) {
                        isSystemApp = isSystemApp(appInfo);
                        if (isSystemApp || XSpaceConstant.GMS_RELATED_APPS.contains(packageName)) {
                            String str2 = TAG;
                            Slog.d(str2, "XSpace ignore system or GMS package: " + packageName);
                            return;
                        }
                        synchronized (sXSpaceInstalledPackagesSelfLocked) {
                            updateXSpaceStatusLocked(true);
                            sXSpaceInstalledPackagesSelfLocked.add(packageName);
                        }
                    }
                } catch (RemoteException e) {
                    Slog.d(TAG, "PMS died", e);
                }
            } else {
                MiuiSettings.XSpace.resetDefaultSetting(mContext, packageName);
                synchronized (sXSpaceInstalledPackagesSelfLocked) {
                    sXSpaceInstalledPackagesSelfLocked.remove(packageName);
                    updateXSpaceStatusLocked(false);
                }
            }
        } else if (user.getIdentifier() == 0 && "android.intent.action.PACKAGE_ADDED".equals(action) && MiuiSettings.XSpace.sCompeteXSpaceApps.contains(packageName) && needSetInstallXSpaceGuideTaskForCompete(mContext) && !CrossUserUtils.hasXSpaceUser(mContext)) {
            startXSpaceService(mContext, "param_intent_value_compete_install_xspace_guide");
        }
    }

    private static void updateXSpaceStatusLocked(boolean isXSpaceActive) {
        if (sXSpaceInstalledPackagesSelfLocked.size() == XSPACE_APP_LIST_INIT_NUMBER) {
            String str = TAG;
            Slog.d(str, "updateXSpaceStatusLocked sXSpaceInstalledPackagesSelfLocked =" + sXSpaceInstalledPackagesSelfLocked.toString() + "    XSPACE_APP_LIST_INIT_NUMBER =" + XSPACE_APP_LIST_INIT_NUMBER);
            String str2 = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("update XSpace Enable = ");
            sb.append(isXSpaceActive);
            Slog.w(str2, sb.toString());
            MiuiSettings.Secure.putBoolean(sResolver, "xspace_enabled", isXSpaceActive);
        }
    }

    private static void startXSpaceService(Context context, String extra) {
        Intent intent = new Intent();
        intent.setComponent(ComponentName.unflattenFromString(XSPACE_SERVICE_COMPONENT));
        if (extra != null) {
            intent.putExtra("param_intent_key_has_extra", extra);
        }
        context.startService(intent);
    }

    private static boolean needSetBootXSpaceGuideTaskForCompete(Context context) {
        return MiuiSettings.XSpace.getGuideNotificationTimes(context, "key_xspace_boot_guide_times") < 1;
    }

    private static boolean needSetInstallXSpaceGuideTaskForCompete(Context context) {
        return MiuiSettings.XSpace.getGuideNotificationTimes(context, "key_xspace_compete_guide_times") < 1;
    }

    static boolean isComponentEnabled(ActivityInfo info, int userId) {
        if (info == null) {
            return true;
        }
        boolean enabled = false;
        ComponentName compname = new ComponentName(info.packageName, info.name);
        try {
            if (AppGlobals.getPackageManager().getActivityInfo(compname, 0, userId) != null) {
                enabled = true;
            }
        } catch (RemoteException e) {
            enabled = false;
        }
        if (!enabled) {
            String str = TAG;
            Slog.d(str, "Component not enabled: " + compname + "  in user " + userId);
        }
        return enabled;
    }
}
