package com.android.server.pm;

import android.app.AppGlobals;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageHideManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageParser;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.MiuiResources;
import android.database.ContentObserver;
import android.miui.AppOpsUtils;
import android.miui.Manifest;
import android.net.Uri;
import android.os.AsyncResult;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import com.android.commands.pm.PmInjector;
import com.android.internal.app.IAppOpsService;
import com.android.internal.os.BackgroundThread;
import com.android.server.LocalServices;
import com.android.server.PinnerService;
import com.android.server.am.ActivityManagerServiceInjector;
import com.android.server.am.ExtraActivityManagerService;
import com.android.server.pm.PackageManagerService;
import com.android.server.pm.dex.DexoptOptions;
import com.android.server.pm.dex.PackageDexUsage;
import com.android.server.usage.UnixCalendar;
import com.android.server.wm.ActivityStackSupervisorInjector;
import com.android.server.wm.ActivityTaskManagerInternal;
import com.miui.enterprise.ApplicationHelper;
import com.miui.enterprise.settings.EnterpriseSettings;
import com.miui.enterprise.signature.EnterpriseVerifier;
import com.miui.server.AccessController;
import com.miui.server.GreenGuardManagerService;
import com.miui.server.SecSpaceManagerService;
import com.miui.server.SecurityManagerService;
import com.miui.server.SignatureConstants;
import com.miui.server.SplashScreenServiceDelegate;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import miui.content.pm.ExtraPackageManager;
import miui.content.pm.IPackageDeleteConfirmObserver;
import miui.core.CompatibleManager;
import miui.core.ManifestParser;
import miui.mqsas.sdk.BootEventManager;
import miui.mqsas.sdk.MQSEventManagerDelegate;
import miui.mqsas.sdk.event.PackageEvent;
import miui.os.Build;
import miui.security.ISecurityCallback;
import miui.securityspace.XSpaceIntentCompat;
import miui.securityspace.XSpaceUserHandle;
import miui.telephony.TelephonyManager;
import miui.util.FeatureParser;
import miui.util.UrlResolver;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class PackageManagerServiceInjector {
    private static final String APP_LIST_FILE = "/system/etc/install_app_filter.xml";
    private static final String[][] BLOCK_APPS = {new String[]{"com.jeejen.family.miui"}, new String[]{"com.miui.home", "com.android.mms", "com.android.contacts"}};
    static boolean DEBUG_CHANGE = true;
    static final int DELETE_FAILED_FORBIDED_BY_MIUI = -1000;
    private static final String GLOBAL_SYNC_KEY_ENABLE = "1";
    private static final String GOOGLE_MARKET_PACKAGE = "com.android.vending";
    static final int INSTALL_IGNORE_PACKAGE = -1000;
    private static final String MIUI_BROWSER_PACKAGE = "com.android.browser";
    private static final String MIUI_INSTALLER_PACKAGE = "com.miui.packageinstaller";
    private static final String MIUI_MARKET_PACKAGE = "com.xiaomi.market";
    private static final int MSG_INSTALL_APP_GOON = 0;
    private static final String PACKAGE_INSTALLER_NAME = "com.google.android.packageinstaller";
    private static final String PACKAGE_MIME_TYPE = "application/vnd.android.package-archive";
    private static final String SUPPORT_OLDMAN_MODE = "support_oldman_mode";
    /* access modifiers changed from: private */
    public static String TAG = PackageManagerServiceInjector.class.getName();
    private static final String TAG_ADD_APPS = "add_apps";
    private static final String TAG_APP = "app";
    private static final String TAG_IGNORE_APPS = "ignore_apps";
    private static final String[] WIFI_ONLY_BLOCK_APPS = {"com.android.mms", "com.miui.smsextra", "com.miui.yellowpage", "com.android.incallui", "com.miui.virtualsim", "com.xiaomi.mimobile.noti", "com.miui.vsimcore"};
    private static ArrayList<String> appsMovedToDataPartition = null;
    private static ArrayList<String> fixUpAppIdWhiteList = null;
    static Handler mHandler = new Handler(new Handler.Callback() {
        public boolean handleMessage(Message msg) {
            if (msg.what != 0) {
                return true;
            }
            try {
                InstallAppParam param = (InstallAppParam) ((AsyncResult) msg.obj).userObj;
                Slog.d(PackageManagerServiceInjector.TAG, "goon the pending installation");
                Handler h = param.getHandler();
                if (h != null) {
                    h.sendMessage(param.msg);
                    return true;
                }
                Slog.e(PackageManagerServiceInjector.TAG, "handler dismissed");
                return true;
            } catch (Throwable t) {
                String access$100 = PackageManagerServiceInjector.TAG;
                Slog.w(access$100, "failed goon the pending installation due to: " + t.getStackTrace().toString());
                return true;
            }
        }
    });
    private static boolean mInstallation = false;
    private static PackageParser.Package mMiuiInstallerPackage;
    private static PackageSetting mMiuiInstallerPackageSetting;
    private static ArrayList<String> sAllowPackage = new ArrayList<>();
    private static IAppOpsService sAppOpsService;
    static ArrayList<String> sCrossXSpaceIntent = new ArrayList<>();
    private static final SparseArray<String> sDefaultHome = new SparseArray<>();
    static List<String> sEPAllowedPackage = new ArrayList();
    private static volatile Handler sFirstUseHandler;
    private static Object sFirstUseLock = new Object();
    private static HandlerThread sFirstUseThread;
    private static Set<String> sForbiddenToInstalledPackages = new HashSet();
    private static ReentrantReadWriteLock sGlobalRWLock = new ReentrantReadWriteLock();
    private static String sGlobalSyncKey = "1";
    private static String sGlobalWhiteListPackages;
    private static ArrayList<String> sGlobalWhitePackageList = new ArrayList<>();
    private static HashSet<String> sGoogleBaseApps = new HashSet<>();
    private static Set<String> sIgnoreApks = new HashSet();
    private static Set<String> sIgnorePackages = new HashSet();
    private static Boolean sIsPlatformSignature = true;
    private static List<String> sNonProfileCompiledPkgs = new ArrayList();
    private static ArrayList<String> sNotDisable = new ArrayList<>();
    private static List<String> sSecondaryDexOptBlackList = new ArrayList();
    private static List<String> sSecondaryDexOptPkgs = new ArrayList();
    static ArrayList<String> sShellCheckPermissions = new ArrayList<>();
    private static final Set<String> sSilentlyUninstallPackages = new HashSet();
    private static Set<String> sTop10ThirdPartyPks;
    static ArrayList<String> sXSpaceAddUserAuthorityBlackList = new ArrayList<>();
    static ArrayList<String> sXSpaceDataSchemeWhiteList = new ArrayList<>();
    private static ArrayList<String> sXSpaceFriendlyActionList = new ArrayList<>();

    static {
        if (!FeatureParser.getBoolean("support_fm", true)) {
            sIgnoreApks.add("/system/app/FM.apk");
            sIgnoreApks.add("/system/app/FM");
        }
        readIgnoreApks();
        sIgnorePackages.add("com.sogou.inputmethod.mi");
        if (Build.IS_INTERNATIONAL_BUILD || (!Build.IS_CM_CUSTOMIZATION && (!TelephonyManager.getDefault().isCmccCooperationDevice() || !Build.IS_CT_CUSTOMIZATION))) {
            sIgnorePackages.add("com.miui.dmregservice");
        }
        sGlobalWhitePackageList.add("com.android.thememanager");
        sGlobalWhitePackageList.add(ActivityStackSupervisorInjector.MIUI_APP_LOCK_PACKAGE_NAME);
        sGlobalWhitePackageList.add("com.miui.cleanmaster");
        sGlobalWhitePackageList.add("com.mi.android.globalFileexplorer");
        sGlobalWhitePackageList.add("com.android.providers.downloads.ui");
        sGlobalWhitePackageList.add("com.miui.home");
        sGlobalWhitePackageList.add("com.miui.player");
        sGlobalWhitePackageList.add("com.xiaomi.midrop");
        sGlobalWhitePackageList.add("com.mi.android.globalpersonalassistant");
        sGlobalWhitePackageList.add("com.android.calendar");
        sGlobalWhitePackageList.add(MIUI_BROWSER_PACKAGE);
        sGlobalWhitePackageList.add("com.mi.globalbrowser");
        sGlobalWhitePackageList.add("com.android.mms");
        sGlobalWhitePackageList.add("com.miui.android.fashiongallery");
        sGlobalWhitePackageList.add("com.miui.weather2");
        sGlobalWhitePackageList.add(SplashScreenServiceDelegate.SPLASHSCREEN_GLOBAL_PACKAGE);
        sXSpaceFriendlyActionList.add("com.sina.weibo.action.sdkidentity");
        sXSpaceFriendlyActionList.add("com.sina.weibo.remotessoservice");
        sCrossXSpaceIntent.add("android.intent.action.VIEW");
        sCrossXSpaceIntent.add("android.intent.action.SEND");
        sCrossXSpaceIntent.add("android.intent.action.DIAL");
        sCrossXSpaceIntent.add("android.intent.action.PICK");
        sCrossXSpaceIntent.add("android.intent.action.INSERT");
        sCrossXSpaceIntent.add("android.intent.action.GET_CONTENT");
        sCrossXSpaceIntent.add("android.media.action.IMAGE_CAPTURE");
        sCrossXSpaceIntent.add("android.settings.MANAGE_APPLICATIONS_SETTINGS");
        sCrossXSpaceIntent.add("android.settings.APPLICATION_DETAILS_SETTINGS");
        sCrossXSpaceIntent.add("android.provider.Telephony.ACTION_CHANGE_DEFAULT");
        sXSpaceDataSchemeWhiteList.add(ActivityTaskManagerInternal.ASSIST_KEY_CONTENT);
        sXSpaceDataSchemeWhiteList.add("http");
        sXSpaceDataSchemeWhiteList.add("https");
        sXSpaceDataSchemeWhiteList.add("file");
        sXSpaceDataSchemeWhiteList.add("ftp");
        sXSpaceDataSchemeWhiteList.add("ed2k");
        sXSpaceDataSchemeWhiteList.add("geo");
        sXSpaceAddUserAuthorityBlackList.add("com.android.contacts");
        sNotDisable.add("com.lbe.security.miui");
        sNotDisable.add(ActivityStackSupervisorInjector.MIUI_APP_LOCK_PACKAGE_NAME);
        sNotDisable.add("com.android.updater");
        sNotDisable.add(MIUI_MARKET_PACKAGE);
        sNotDisable.add("com.xiaomi.finddevice");
        sNotDisable.add("com.miui.home");
        sShellCheckPermissions.add("android.permission.SEND_SMS");
        sShellCheckPermissions.add("android.permission.CALL_PHONE");
        sShellCheckPermissions.add("android.permission.READ_CONTACTS");
        sShellCheckPermissions.add("android.permission.WRITE_CONTACTS");
        sShellCheckPermissions.add("android.permission.CLEAR_APP_USER_DATA");
        sShellCheckPermissions.add("android.permission.WRITE_SECURE_SETTINGS");
        sShellCheckPermissions.add("android.permission.WRITE_SETTINGS");
        sShellCheckPermissions.add("android.permission.MANAGE_DEVICE_ADMINS");
        sShellCheckPermissions.add("android.permission.UPDATE_APP_OPS_STATS");
        sShellCheckPermissions.add("android.permission.INJECT_EVENTS");
        sShellCheckPermissions.add("android.permission.INSTALL_GRANT_RUNTIME_PERMISSIONS");
        sShellCheckPermissions.add("android.permission.GRANT_RUNTIME_PERMISSIONS");
        sShellCheckPermissions.add("android.permission.REVOKE_RUNTIME_PERMISSIONS");
        sShellCheckPermissions.add("android.permission.SET_PREFERRED_APPLICATIONS");
        if ((SystemProperties.getInt("ro.debuggable", 0) == 1 && (SystemProperties.getInt("ro.secureboot.devicelock", 0) == 0 || "unlocked".equals(SystemProperties.get("ro.secureboot.lockstate")))) || Build.IS_TABLET) {
            sShellCheckPermissions.clear();
        }
        sAllowPackage.add(MIUI_MARKET_PACKAGE);
        sAllowPackage.add(GOOGLE_MARKET_PACKAGE);
        sAllowPackage.add("com.google.android.gms");
        sAllowPackage.add("com.android.backup");
        sAllowPackage.add("com.xiaomi.gamecenter");
        sAllowPackage.add("com.android.packageinstaller");
        sAllowPackage.add(PACKAGE_INSTALLER_NAME);
        sAllowPackage.add("com.android.managedprovisioning");
        sAllowPackage.add(ActivityStackSupervisorInjector.MIUI_APP_LOCK_PACKAGE_NAME);
        sAllowPackage.add("com.miui.cloudbackup");
        sAllowPackage.add("com.miui.analytics");
        sAllowPackage.add("com.android.provision");
        sAllowPackage.add("com.miui.powerkeeper");
        sAllowPackage.add(SplashScreenServiceDelegate.SPLASHSCREEN_PACKAGE);
        sAllowPackage.add(SplashScreenServiceDelegate.SPLASHSCREEN_GLOBAL_PACKAGE);
        sAllowPackage.add(MIUI_INSTALLER_PACKAGE);
        sAllowPackage.add("com.xiaomi.discover");
        sAllowPackage.add("com.xiaomi.mipicks");
        sAllowPackage.add("com.xiaomi.mihomemanager");
        sAllowPackage.add("com.xiaomi.glgm");
        sAllowPackage.add("com.android.settings");
        sAllowPackage.add("com.facebook.system");
        sAllowPackage.add("com.mediatek.backuprestore");
        sAllowPackage.add("com.miui.securitycore");
        sAllowPackage.add("com.orange.update");
        sAllowPackage.add("com.ironsource.appcloud.oobe.hutchison");
        sAllowPackage.add("com.miui.home");
        sForbiddenToInstalledPackages.add(AccessController.PACKAGE_CAMERA);
        sGoogleBaseApps.add("com.google.android.gsf");
        sGoogleBaseApps.add("com.google.android.gsf.login");
        sGoogleBaseApps.add("com.google.android.gms");
        sGoogleBaseApps.add("com.google.android.partnersetup");
        sSilentlyUninstallPackages.add(SystemProperties.get("ro.miui.product.home", "com.miui.home"));
        sSilentlyUninstallPackages.add(MIUI_MARKET_PACKAGE);
        sSilentlyUninstallPackages.add("com.xiaomi.mipicks");
        sSilentlyUninstallPackages.add("com.xiaomi.discover");
        sSilentlyUninstallPackages.add("com.xiaomi.gamecenter");
        sSilentlyUninstallPackages.add("com.xiaomi.gamecenter.pad");
        sSilentlyUninstallPackages.add("com.miui.global.packageinstaller");
        sSilentlyUninstallPackages.add(PACKAGE_INSTALLER_NAME);
        sSilentlyUninstallPackages.add(GreenGuardManagerService.GREEN_KID_AGENT_PKG_NAME);
        sNonProfileCompiledPkgs.add("com.tencent.mm");
        sNonProfileCompiledPkgs.add("com.tencent.mobileqq");
        sNonProfileCompiledPkgs.add("com.taobao.taobao");
        sNonProfileCompiledPkgs.add("com.eg.android.AlipayGphone");
        sNonProfileCompiledPkgs.add("com.ss.android.article.news");
        sSecondaryDexOptPkgs.add("com.tencent.mm");
        sEPAllowedPackage.add("com.android.packageinstaller");
        sEPAllowedPackage.add(MIUI_INSTALLER_PACKAGE);
    }

    private static void readIgnoreApks() {
        String custVariant = Build.getCustVariant();
        if (!TextUtils.isEmpty(custVariant)) {
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(new File(APP_LIST_FILE));
                XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
                parser.setInput(inputStream, "UTF-8");
                String appPath = null;
                boolean is_add_apps = false;
                for (int type = parser.getEventType(); 1 != type; type = parser.next()) {
                    if (type == 2) {
                        String tagName = parser.getName();
                        if (parser.getAttributeCount() > 0) {
                            appPath = parser.getAttributeValue(0);
                        }
                        if (TAG_ADD_APPS.equals(tagName)) {
                            is_add_apps = true;
                        } else if (TAG_IGNORE_APPS.equals(tagName)) {
                            is_add_apps = false;
                        } else if (TAG_APP.equals(tagName)) {
                            String[] ss = parser.nextText().split(" ");
                            boolean is_current_cust = false;
                            int i = 0;
                            while (true) {
                                if (i >= ss.length) {
                                    break;
                                } else if (ss[i].equals(custVariant)) {
                                    is_current_cust = true;
                                    break;
                                } else {
                                    i++;
                                }
                            }
                            if ((is_add_apps && !is_current_cust) || (!is_add_apps && is_current_cust)) {
                                sIgnoreApks.add(appPath);
                            } else if (is_add_apps && is_current_cust && sIgnoreApks.contains(appPath)) {
                                sIgnoreApks.remove(appPath);
                            }
                        }
                    } else if (type == 3) {
                        String end_tag_name = parser.getName();
                        if (TAG_ADD_APPS.equals(end_tag_name)) {
                            is_add_apps = false;
                        } else if (TAG_IGNORE_APPS.equals(end_tag_name)) {
                            is_add_apps = true;
                        }
                    }
                }
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            } catch (IOException e2) {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (XmlPullParserException e3) {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Throwable th) {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e4) {
                    }
                }
                throw th;
            }
        }
    }

    static void addIgnoreApk(String path) {
        if (path != null) {
            sIgnoreApks.add(path);
        }
    }

    static boolean ignoreApk(String path) {
        if (path != null) {
            return sIgnoreApks.contains(path);
        }
        return false;
    }

    static void addIgnorePackage(String packageName) {
        if (packageName != null) {
            sIgnorePackages.add(packageName);
        }
    }

    static boolean ignorePackage(String packageName) {
        if (packageName != null) {
            return sIgnorePackages.contains(packageName);
        }
        return false;
    }

    static void addMiuiSharedUids(Settings settings, boolean multipleUids) {
        int i = 10000;
        settings.addSharedUserLPw("android.uid.backup", multipleUids ? 9800 : 10000, 1, 8);
        settings.addSharedUserLPw("android.uid.theme", multipleUids ? 9801 : 10000, 1, 8);
        settings.addSharedUserLPw("android.uid.updater", multipleUids ? 9802 : 10000, 1, 8);
        if (multipleUids) {
            i = 9810;
        }
        settings.addSharedUserLPw("android.uid.finddevice", i, 1, 8);
    }

    static ResolveInfo getSystemResolveInfo(PackageManagerService pms, List<ResolveInfo> riList) {
        ResolveInfo ret = null;
        int match = riList.get(0).match;
        PackageManager pm = pms.mContext.getPackageManager();
        for (ResolveInfo ri : riList) {
            if (ri.priority < riList.get(0).priority) {
                break;
            }
            if (ri.match > match) {
                match = ri.match;
                ret = null;
            } else if (ri.match >= match) {
                if (ret != null) {
                }
            }
            if (ri.system && ExtraPackageManager.isMiuiSystemApp(pm, ri.activityInfo.packageName)) {
                ret = ri;
            }
        }
        return ret;
    }

    static ResolveInfo getBrowserResolveInfo(PackageManagerService pms, List<ResolveInfo> riList) {
        if (!Build.checkRegion("CN")) {
            return null;
        }
        ResolveInfo ret = getSystemResolveInfo(pms, riList);
        if (ret != null) {
            return ret;
        }
        for (ResolveInfo ri : riList) {
            if (MIUI_BROWSER_PACKAGE.equals(ri.activityInfo.packageName) && !ri.system) {
                return ri;
            }
        }
        return ret;
    }

    static ResolveInfo getMarketResolveInfo(List<ResolveInfo> riList) {
        for (ResolveInfo ri : riList) {
            if (MIUI_MARKET_PACKAGE.equals(ri.activityInfo.packageName) && ri.system) {
                return ri;
            }
        }
        return null;
    }

    static ResolveInfo checkMiuiIntent(PackageManagerService pms, Intent intent, String resolvedType, int flags, List<ResolveInfo> query, int userId) {
        ResolveInfo ri;
        String host;
        ResolveInfo ri2;
        String realPkgName;
        ResolveInfo ri3;
        if (intent != null && !Build.IS_INTERNATIONAL_BUILD) {
            if (intent.getCategories() == null || !intent.getCategories().contains("android.intent.category.HOME")) {
                if ("android.intent.action.ASSIST".equals(intent.getAction()) || "android.intent.action.VOICE_COMMAND".equals(intent.getAction())) {
                    return getSystemResolveInfo(pms, query);
                }
                if (("http".equals(intent.getScheme()) || "https".equals(intent.getScheme())) && "android.intent.action.VIEW".equals(intent.getAction())) {
                    ResolveInfo ri4 = UrlResolver.checkMiuiIntent(pms.mContext, pms, intent, resolvedType, flags, query, userId);
                    if (ri4 != null) {
                        return ri4;
                    }
                    if (Build.VERSION.SDK_INT < 23 && intent.getComponent() == null && intent.getType() == null && (ri3 = getBrowserResolveInfo(pms, query)) != null) {
                        return ri3;
                    }
                } else if ("mimarket".equals(intent.getScheme()) || ("market".equals(intent.getScheme()) && "android.intent.action.VIEW".equals(intent.getAction()))) {
                    Uri uri = intent.getData();
                    if (!(uri == null || (host = uri.getHost()) == null || ((!host.equals("details") && !host.equals("search")) || (ri2 = getMarketResolveInfo(query)) == null))) {
                        return ri2;
                    }
                } else if (PACKAGE_MIME_TYPE.equals(intent.getType()) && "android.intent.action.VIEW".equals(intent.getAction())) {
                    if (miui.os.Build.IS_INTERNATIONAL_BUILD || AppOpsUtils.isXOptMode() || miui.os.Build.IS_TABLET) {
                        realPkgName = PACKAGE_INSTALLER_NAME;
                        synchronized (pms.mPackages) {
                            if (pms.mSettings.getRenamedPackageLPr(PACKAGE_INSTALLER_NAME) != null) {
                                realPkgName = pms.mSettings.getRenamedPackageLPr(PACKAGE_INSTALLER_NAME);
                            }
                        }
                    } else {
                        realPkgName = MIUI_INSTALLER_PACKAGE;
                    }
                    intent.setPackage(realPkgName);
                    return pms.resolveIntent(intent, resolvedType, flags, userId);
                }
            } else if (miui.os.Build.getUserMode() == 1) {
                intent.setClassName("com.jeejen.family.miui", "com.jeejen.home.launcher.Launcher");
            } else {
                intent.setClassName("com.miui.home", "com.miui.home.launcher.Launcher");
            }
            if (intent.getComponent() != null) {
                return pms.resolveIntent(intent, resolvedType, flags, userId);
            }
        } else if (intent != null && miui.os.Build.IS_INTERNATIONAL_BUILD && !AppOpsUtils.isXOptMode()) {
            if ((PACKAGE_MIME_TYPE.equals(intent.getType()) && "android.intent.action.VIEW".equals(intent.getAction())) || "android.intent.action.INSTALL_PACKAGE".equals(intent.getAction())) {
                intent.setPackage(PACKAGE_INSTALLER_NAME);
                return pms.resolveIntent(intent, resolvedType, flags, userId);
            } else if (isOpenByGooglePlayStore(intent, pms) && (ri = getGlobalMarketResolveInfo(query)) != null) {
                return ri;
            }
        }
        return pms.mResolveInfo;
    }

    private static boolean isOpenByGooglePlayStore(Intent intent, PackageManagerService pms) {
        if (!"market".equals(intent.getScheme()) || !"android.intent.action.VIEW".equals(intent.getAction()) || intent.getData() == null || intent.getData().getHost() == null) {
            return false;
        }
        String callingApp = intent.getSender();
        if (TextUtils.isEmpty(callingApp)) {
            return false;
        }
        boolean isOpenByGP = true;
        try {
            sGlobalRWLock.readLock().lock();
            if (!"1".equals(sGlobalSyncKey) || !sGlobalWhitePackageList.contains(callingApp)) {
                isOpenByGP = false;
            }
        } catch (Exception e) {
            String str = TAG;
            Slog.e(str, "error:" + e.toString());
        } catch (Throwable th) {
            sGlobalRWLock.readLock().unlock();
            throw th;
        }
        sGlobalRWLock.readLock().unlock();
        return isOpenByGP;
    }

    private static void decodeFromServerFormat(String cloudDataString) {
        if (TextUtils.isEmpty(cloudDataString)) {
            sGlobalWhitePackageList.clear();
            return;
        }
        try {
            String[] packages = cloudDataString.split("#");
            sGlobalWhitePackageList.clear();
            for (String app : packages) {
                sGlobalWhitePackageList.add(app);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String encodeToServerFormat() {
        String defaultCloudDataString = "";
        ArrayList<String> arrayList = sGlobalWhitePackageList;
        if (arrayList == null || arrayList.isEmpty()) {
            return defaultCloudDataString;
        }
        for (int i = 0; i < sGlobalWhitePackageList.size() - 1; i++) {
            defaultCloudDataString = defaultCloudDataString + sGlobalWhitePackageList.get(i) + "#";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(defaultCloudDataString);
        ArrayList<String> arrayList2 = sGlobalWhitePackageList;
        sb.append(arrayList2.get(arrayList2.size() - 1));
        return sb.toString();
    }

    private static void registerDataObserver(final Context context) {
        context.getContentResolver().registerContentObserver(MiuiSettings.SettingsCloudData.getCloudDataNotifyUri(), true, new ContentObserver(BackgroundThread.getHandler()) {
            public void onChange(boolean selfChange) {
                PackageManagerServiceInjector.updatePackageWhiteList(context);
            }
        });
    }

    /* access modifiers changed from: private */
    public static void updatePackageWhiteList(Context context) {
        try {
            sGlobalRWLock.writeLock().lock();
            sGlobalWhiteListPackages = MiuiSettings.SettingsCloudData.getCloudDataString(context.getContentResolver(), "IntentInterceptHelper", "global_intenttogp_packagelist", encodeToServerFormat());
            decodeFromServerFormat(sGlobalWhiteListPackages);
            sGlobalSyncKey = MiuiSettings.SettingsCloudData.getCloudDataString(context.getContentResolver(), "IntentInterceptHelper", "global_intenttogp_switch", "1");
        } catch (Exception e) {
            String str = TAG;
            Log.e(str, "error:" + e.toString());
        } catch (Throwable th) {
            sGlobalRWLock.writeLock().unlock();
            throw th;
        }
        sGlobalRWLock.writeLock().unlock();
    }

    static ResolveInfo getGlobalMarketResolveInfo(List<ResolveInfo> riList) {
        for (ResolveInfo ri : riList) {
            if (GOOGLE_MARKET_PACKAGE.equals(ri.activityInfo.packageName) && ri.system) {
                return ri;
            }
        }
        return null;
    }

    static String getInstallerPackageName(String packageName, String installerPackageName) {
        if (!"adb".equals(installerPackageName) || packageName == null || !packageName.startsWith("com.android.cts.")) {
            return installerPackageName;
        }
        return null;
    }

    static void performPreinstallApp(PackageManagerService pms, Settings curPkgSettings) {
        PreinstallApp.copyPreinstallApps(pms, curPkgSettings);
        checkPackageInstallerStatus(pms, curPkgSettings);
        checkGTSSpecAppOptMode(pms);
    }

    public static void checkPackageInstallerStatus(PackageManagerService pms, Settings curPkgSettings) {
        if (Build.VERSION.SDK_INT >= 23 && !miui.os.Build.IS_INTERNATIONAL_BUILD && !miui.os.Build.IS_TABLET) {
            PackageSetting miuiInstaller = curPkgSettings.mPackages.get(MIUI_INSTALLER_PACKAGE);
            PackageSetting googleInstaller = curPkgSettings.mPackages.get(PACKAGE_INSTALLER_NAME);
            PackageSetting androidInstaller = curPkgSettings.mPackages.get("com.android.packageinstaller");
            boolean isCtsBuild = AppOpsUtils.isXOptMode();
            if (isCtsBuild) {
                if (miuiInstaller != null) {
                    miuiInstaller.setInstalled(!isCtsBuild, 0);
                }
                mMiuiInstallerPackageSetting = curPkgSettings.mPackages.get(MIUI_INSTALLER_PACKAGE);
                mMiuiInstallerPackage = pms.mPackages.get(MIUI_INSTALLER_PACKAGE);
                curPkgSettings.mPackages.remove(MIUI_INSTALLER_PACKAGE);
                if (curPkgSettings.isDisabledSystemPackageLPr(MIUI_INSTALLER_PACKAGE)) {
                    curPkgSettings.removeDisabledSystemPackageLPw(MIUI_INSTALLER_PACKAGE);
                }
                pms.mPackages.remove(MIUI_INSTALLER_PACKAGE);
            } else {
                PackageSetting packageSetting = mMiuiInstallerPackageSetting;
                if (packageSetting != null) {
                    packageSetting.setInstalled(!isCtsBuild, 0);
                    curPkgSettings.mPackages.put(MIUI_INSTALLER_PACKAGE, mMiuiInstallerPackageSetting);
                    if ((mMiuiInstallerPackageSetting.pkgFlags & 128) != 0) {
                        curPkgSettings.disableSystemPackageLPw(MIUI_INSTALLER_PACKAGE, true);
                    }
                }
                if (mMiuiInstallerPackage != null) {
                    pms.mPackages.put(MIUI_INSTALLER_PACKAGE, mMiuiInstallerPackage);
                }
            }
            if (googleInstaller != null) {
                googleInstaller.setInstalled(isCtsBuild, 0);
            }
            if (androidInstaller != null) {
                androidInstaller.setInstalled(isCtsBuild, 0);
            }
        }
    }

    static void removePackageFromSharedUser(PackageSetting ps) {
        if (ps.sharedUser != null) {
            ps.sharedUser.removePackage(ps);
        }
    }

    /* JADX WARNING: type inference failed for: r15v0, types: [android.os.IBinder] */
    /* JADX WARNING: Code restructure failed: missing block: B:75:0x014a, code lost:
        return true;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static boolean protectAppFromDeleting(com.android.server.pm.PackageManagerService r24, java.lang.String r25, java.lang.Object r26, int r27) {
        /*
            r1 = r24
            r2 = r25
            r3 = r26
            r4 = r27
            r5 = 0
            android.util.ArrayMap<java.lang.String, android.content.pm.PackageParser$Package> r6 = r1.mPackages
            monitor-enter(r6)
            android.util.ArrayMap<java.lang.String, android.content.pm.PackageParser$Package> r0 = r1.mPackages     // Catch:{ all -> 0x015d }
            java.lang.Object r0 = r0.get(r2)     // Catch:{ all -> 0x015d }
            android.content.pm.PackageParser$Package r0 = (android.content.pm.PackageParser.Package) r0     // Catch:{ all -> 0x015d }
            r5 = r0
            monitor-exit(r6)     // Catch:{ all -> 0x015d }
            r6 = 0
            if (r5 == 0) goto L_0x015c
            android.content.pm.ApplicationInfo r0 = r5.applicationInfo
            if (r0 != 0) goto L_0x001f
            goto L_0x015c
        L_0x001f:
            boolean r0 = com.miui.enterprise.settings.EnterpriseSettings.ENTERPRISE_ACTIVATED
            r7 = -1000(0xfffffffffffffc18, float:NaN)
            r8 = 1
            if (r0 == 0) goto L_0x005c
            android.content.Context r0 = r1.mContext
            int r9 = android.os.UserHandle.getUserId(r27)
            boolean r0 = com.miui.enterprise.ApplicationHelper.protectedFromDelete(r0, r2, r9)
            if (r0 == 0) goto L_0x005c
            java.lang.String r0 = TAG     // Catch:{ RemoteException -> 0x005a }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x005a }
            r6.<init>()     // Catch:{ RemoteException -> 0x005a }
            java.lang.String r9 = "MIUILOG- can't uninstall pkg : "
            r6.append(r9)     // Catch:{ RemoteException -> 0x005a }
            r6.append(r2)     // Catch:{ RemoteException -> 0x005a }
            java.lang.String r9 = " callingUid : "
            r6.append(r9)     // Catch:{ RemoteException -> 0x005a }
            r6.append(r4)     // Catch:{ RemoteException -> 0x005a }
            java.lang.String r9 = ", reason Enterprise"
            r6.append(r9)     // Catch:{ RemoteException -> 0x005a }
            java.lang.String r6 = r6.toString()     // Catch:{ RemoteException -> 0x005a }
            android.util.Slog.d(r0, r6)     // Catch:{ RemoteException -> 0x005a }
            com.android.server.pm.PackageManagerServiceInjectorProxy.returnPackageDeletedResultToObserver(r3, r2, r7)     // Catch:{ RemoteException -> 0x005a }
            goto L_0x005b
        L_0x005a:
            r0 = move-exception
        L_0x005b:
            return r8
        L_0x005c:
            android.content.pm.ApplicationInfo r0 = r5.applicationInfo
            int r0 = r0.flags
            r0 = r0 & r8
            r9 = 0
            if (r0 != 0) goto L_0x00a7
            if (r4 == 0) goto L_0x00a7
            int r0 = android.os.UserHandle.getAppId(r27)
            r10 = 1000(0x3e8, float:1.401E-42)
            if (r0 == r10) goto L_0x00a7
            android.content.Context r0 = r1.mContext
            boolean r0 = miui.content.pm.PreloadedAppPolicy.isProtectedDataApp(r0, r2, r6)
            if (r0 == 0) goto L_0x00a7
            boolean r0 = miui.os.Build.IS_TABLET
            if (r0 != 0) goto L_0x00a7
            java.lang.String r0 = TAG     // Catch:{ RemoteException -> 0x00a5 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x00a5 }
            r6.<init>()     // Catch:{ RemoteException -> 0x00a5 }
            java.lang.String r10 = "MIUILOG- can't uninstall pkg : "
            r6.append(r10)     // Catch:{ RemoteException -> 0x00a5 }
            r6.append(r2)     // Catch:{ RemoteException -> 0x00a5 }
            java.lang.String r10 = " callingUid : "
            r6.append(r10)     // Catch:{ RemoteException -> 0x00a5 }
            r6.append(r4)     // Catch:{ RemoteException -> 0x00a5 }
            java.lang.String r6 = r6.toString()     // Catch:{ RemoteException -> 0x00a5 }
            android.util.Slog.d(r0, r6)     // Catch:{ RemoteException -> 0x00a5 }
            if (r3 == 0) goto L_0x00a4
            boolean r0 = r3 instanceof android.content.pm.IPackageDeleteObserver2     // Catch:{ RemoteException -> 0x00a5 }
            if (r0 == 0) goto L_0x00a4
            r0 = r3
            android.content.pm.IPackageDeleteObserver2 r0 = (android.content.pm.IPackageDeleteObserver2) r0     // Catch:{ RemoteException -> 0x00a5 }
            r0.onPackageDeleted(r2, r7, r9)     // Catch:{ RemoteException -> 0x00a5 }
        L_0x00a4:
            goto L_0x00a6
        L_0x00a5:
            r0 = move-exception
        L_0x00a6:
            return r8
        L_0x00a7:
            android.content.pm.ApplicationInfo r0 = r5.applicationInfo
            int r0 = r0.flags
            r0 = r0 & 128(0x80, float:1.794E-43)
            if (r0 == 0) goto L_0x015b
            android.os.Bundle r0 = r5.mAppMetaData
            if (r0 == 0) goto L_0x015b
            android.os.Bundle r0 = r5.mAppMetaData
            java.lang.String r10 = "com.miui.sdk.module"
            boolean r0 = r0.getBoolean(r10)
            if (r0 == 0) goto L_0x015b
            android.content.Context r0 = r1.mContext
            java.lang.String r10 = "keyguard"
            java.lang.Object r0 = r0.getSystemService(r10)
            r10 = r0
            android.app.KeyguardManager r10 = (android.app.KeyguardManager) r10
            boolean r0 = r10.inKeyguardRestrictedInputMode()
            if (r0 == 0) goto L_0x00e3
            if (r3 == 0) goto L_0x00e1
            boolean r0 = r3 instanceof android.content.pm.IPackageDeleteObserver2     // Catch:{ RemoteException -> 0x00dc }
            if (r0 == 0) goto L_0x00e1
            r0 = r3
            android.content.pm.IPackageDeleteObserver2 r0 = (android.content.pm.IPackageDeleteObserver2) r0     // Catch:{ RemoteException -> 0x00dc }
            r0.onPackageDeleted(r2, r7, r9)     // Catch:{ RemoteException -> 0x00dc }
            goto L_0x00e1
        L_0x00dc:
            r0 = move-exception
            r0.printStackTrace()
            return r6
        L_0x00e1:
            return r8
        L_0x00e3:
            android.app.IActivityManager r22 = android.app.ActivityManagerNative.getDefault()
            com.android.server.pm.PackageManagerServiceInjector$PackageDeleteConfirmObserver r0 = new com.android.server.pm.PackageManagerServiceInjector$PackageDeleteConfirmObserver
            r0.<init>()
            r15 = r0
            android.content.Intent r0 = new android.content.Intent
            java.lang.String r11 = "com.miui.action.PACKAGE_DELETE_CONFIRM"
            r0.<init>(r11)
            r14 = r0
            java.lang.String r0 = "android.intent.category.DEFAULT"
            r14.addCategory(r0)
            java.lang.String r0 = "extra_pkgname"
            r14.putExtra(r0, r2)
            java.lang.String r0 = "observer"
            r14.putExtra(r0, r15)
            r0 = 268435456(0x10000000, float:2.5243549E-29)
            r14.addFlags(r0)
            r12 = 0
            java.lang.String r13 = "pm"
            r0 = 0
            r16 = 0
            r17 = 0
            r18 = 0
            r19 = 0
            r20 = 0
            r21 = 0
            r11 = r22
            r23 = r14
            r8 = r15
            r15 = r0
            int r0 = r11.startActivity(r12, r13, r14, r15, r16, r17, r18, r19, r20, r21)     // Catch:{ RemoteException -> 0x0151 }
            if (r0 == 0) goto L_0x0128
            return r6
        L_0x0128:
            monitor-enter(r8)     // Catch:{ RemoteException -> 0x0151 }
        L_0x0129:
            boolean r0 = r8.finished     // Catch:{ all -> 0x014e }
            if (r0 != 0) goto L_0x0139
            r8.wait()     // Catch:{ InterruptedException -> 0x0131 }
            goto L_0x0129
        L_0x0131:
            r0 = move-exception
            r7 = r0
            r0 = r7
            r0.printStackTrace()     // Catch:{ all -> 0x014e }
            monitor-exit(r8)     // Catch:{ all -> 0x014e }
            return r6
        L_0x0139:
            boolean r0 = r8.delete     // Catch:{ all -> 0x014e }
            if (r0 != 0) goto L_0x014c
            if (r3 == 0) goto L_0x0149
            boolean r0 = r3 instanceof android.content.pm.IPackageDeleteObserver2     // Catch:{ all -> 0x014e }
            if (r0 == 0) goto L_0x0149
            r0 = r3
            android.content.pm.IPackageDeleteObserver2 r0 = (android.content.pm.IPackageDeleteObserver2) r0     // Catch:{ all -> 0x014e }
            r0.onPackageDeleted(r2, r7, r9)     // Catch:{ all -> 0x014e }
        L_0x0149:
            monitor-exit(r8)     // Catch:{ all -> 0x014e }
            r6 = 1
            return r6
        L_0x014c:
            monitor-exit(r8)     // Catch:{ all -> 0x014e }
            goto L_0x015b
        L_0x014e:
            r0 = move-exception
            monitor-exit(r8)     // Catch:{ all -> 0x014e }
            throw r0     // Catch:{ RemoteException -> 0x0151 }
        L_0x0151:
            r0 = move-exception
            goto L_0x0157
        L_0x0153:
            r0 = move-exception
            r23 = r14
            r8 = r15
        L_0x0157:
            r0.printStackTrace()
            return r6
        L_0x015b:
            return r6
        L_0x015c:
            return r6
        L_0x015d:
            r0 = move-exception
            monitor-exit(r6)     // Catch:{ all -> 0x015d }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.PackageManagerServiceInjector.protectAppFromDeleting(com.android.server.pm.PackageManagerService, java.lang.String, java.lang.Object, int):boolean");
    }

    static class PackageDeleteConfirmObserver extends IPackageDeleteConfirmObserver.Stub {
        boolean delete;
        boolean finished;

        PackageDeleteConfirmObserver() {
        }

        public void onConfirm(boolean delete2) {
            synchronized (this) {
                this.finished = true;
                this.delete = delete2;
                notifyAll();
            }
        }
    }

    static boolean checkAndRunPreInstallation(Handler h, Message msg) {
        String packageNameByPid = ExtraActivityManagerService.getPackageNameByPid(Binder.getCallingPid());
        if (miui.os.Build.IS_INTERNATIONAL_BUILD || UserHandle.getCallingUserId() != 0) {
            return false;
        }
        String apkPath = getApkPathFromInstallParams((PackageManagerService.InstallParams) msg.obj);
        SecurityManagerService service = (SecurityManagerService) ServiceManager.getService("security");
        Slog.d(TAG, "check if need preinstall apps");
        try {
            ISecurityCallback ism = service.getGoogleBaseService();
            if (ism != null && ism.checkPreInstallNeeded(apkPath)) {
                String str = TAG;
                Slog.d(str, "pending installation for: " + apkPath);
                service.registerForAppsPreInstalled(mHandler, 0, new InstallAppParam(h, msg));
                ism.preInstallApps();
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "googlebase remote error！", e);
        }
        return false;
    }

    static boolean checkAndRunPreInstallation(Handler h, Message msg, int installerUid, String packageName) {
        String[] packageNames = null;
        try {
            packageNames = AppGlobals.getPackageManager().getPackagesForUid(installerUid);
        } catch (Exception e) {
            Log.e(TAG, "googlebase getPackagesForUid error！", e);
        }
        if (packageNames != null && packageNames.length > 0) {
            String callpkg = packageNames[0];
        }
        if (miui.os.Build.IS_INTERNATIONAL_BUILD || UserHandle.getCallingUserId() != 0) {
            return false;
        }
        PackageManagerService.InstallParams installParams = (PackageManagerService.InstallParams) msg.obj;
        SecurityManagerService service = (SecurityManagerService) ServiceManager.getService("security");
        Slog.d(TAG, "check if need preinstall apps");
        try {
            ISecurityCallback ism = service.getGoogleBaseService();
            if (ism != null && ism.checkPreInstallNeeded(packageName)) {
                String str = TAG;
                Slog.d(str, "pending installation for: " + packageName);
                service.registerForAppsPreInstalled(mHandler, 0, new InstallAppParam(h, msg));
                ism.preInstallApps();
                return true;
            }
        } catch (Exception e2) {
            Log.e(TAG, "googlebase remote error！", e2);
        }
        return false;
    }

    static String getApkPathFromInstallParams(PackageManagerService.InstallParams param) {
        try {
            return param.origin.resolvedPath;
        } catch (Throwable th) {
            Log.e(TAG, th.toString());
            return null;
        }
    }

    static class InstallAppParam {
        /* access modifiers changed from: private */
        public Message msg;
        private WeakReference refH;

        InstallAppParam(Handler h, Message msg2) {
            this.refH = new WeakReference(h);
            this.msg = msg2;
        }

        /* access modifiers changed from: private */
        public Handler getHandler() {
            WeakReference weakReference = this.refH;
            if (weakReference == null) {
                return null;
            }
            return (Handler) weakReference.get();
        }
    }

    static boolean checkMiuiSdk(PackageParser.Package pkg, PackageManagerService pms) throws Exception {
        int miuiManifestResId = -1;
        Bundle bundle = pkg.mAppMetaData;
        if (bundle != null) {
            miuiManifestResId = bundle.getInt("com.miui.sdk.manifest", -1);
        }
        AssetManager assmgr = new AssetManager();
        try {
            if (assmgr.addAssetPath(pkg.baseCodePath) != 0) {
                MiuiResources res = new MiuiResources(assmgr, pms.mMetrics, (Configuration) null);
                if (miuiManifestResId <= 0) {
                    miuiManifestResId = res.getIdentifier("miui_manifest", "xml", pkg.packageName);
                }
                if (miuiManifestResId > 0) {
                    if (new CompatibleManager(pms.mContext, ManifestParser.createFromXmlParser(res, res.getXml(miuiManifestResId)).parse((Map) null)).isCompatible()) {
                        assmgr.close();
                        return true;
                    }
                } else {
                    assmgr.close();
                    return true;
                }
            }
            assmgr.close();
            return false;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            throw new PackageManagerException(-52, "error occurs while parsing miui manifest.");
        } catch (Throwable th) {
            assmgr.close();
            throw th;
        }
    }

    static int checkAndResolveFlags(Intent intent, String resolvedType, int flags, int userId) {
        if (intent == null || !XSpaceUserHandle.isXSpaceUserCalling()) {
            return flags;
        }
        if (sXSpaceFriendlyActionList.contains(intent.getAction())) {
            return flags | 8192;
        }
        return flags;
    }

    static int resolveUserId(Intent intent, String resolvedType, int userId) {
        if (!XSpaceUserHandle.isXSpaceUserId(userId) || intent.getComponent() != null || !sCrossXSpaceIntent.contains(intent.getAction())) {
            return userId;
        }
        if ("android.intent.action.VIEW".equals(intent.getAction()) && intent.getData() != null && intent.getData().getScheme() != null && !sXSpaceDataSchemeWhiteList.contains(intent.getData().getScheme())) {
            return userId;
        }
        if (intent.getData() != null && !sXSpaceAddUserAuthorityBlackList.contains(intent.getData().getAuthority())) {
            XSpaceIntentCompat.prepareToLeaveUser(intent, userId);
        }
        try {
            if (new Intent(intent).hasExtra("miui.intent.extra.USER_ID")) {
                return 0;
            }
            intent.putExtra("miui.intent.extra.USER_ID", userId);
            return 0;
        } catch (Exception e) {
            Slog.w(TAG, "Private intent: ", e);
            return 0;
        }
    }

    static boolean isXSpaceCrossUser(int userId) {
        if (!XSpaceUserHandle.isXSpaceUserId(UserHandle.getCallingUserId()) || userId != 0) {
            return false;
        }
        return true;
    }

    static boolean isAllowedDisable(String packageName, int newState) {
        if (newState == 0 || newState == 1) {
            return true;
        }
        return true ^ sNotDisable.contains(packageName);
    }

    public static int preCheckUidPermission(String permName, int uid) {
        if (uid != 2000 || !sShellCheckPermissions.contains(permName) || SystemProperties.getBoolean("persist.security.adbinput", false)) {
            return -100;
        }
        String str = TAG;
        Slog.d(str, "MIUILOG- permission　denied " + permName);
        return -1;
    }

    static boolean isAllowedHideApp(PackageManagerService service, String packageName, boolean hidden, int userId) {
        ApplicationInfo info = service.getApplicationInfo(packageName, 0, userId);
        boolean isSystem = (info == null || (info.flags & 1) == 0) ? false : true;
        if (MIUI_BROWSER_PACKAGE.equals(packageName) || "com.android.chrome".equals(packageName) || "com.mi.globalbrowser".equals(packageName)) {
            isSystem = false;
        }
        if (!hidden || (!isSystem && AppOpsUtils.isXOptMode())) {
            return true;
        }
        Slog.w(TAG, "MIUILOG- Not Support");
        return false;
    }

    /* JADX WARNING: type inference failed for: r15v0, types: [android.os.IBinder] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean isAllowedInstall(android.content.Context r18, java.io.File r19, int r20, java.lang.String r21) throws com.android.server.pm.PackageManagerException {
        /*
            r1 = r18
            r2 = r20
            r3 = -111(0xffffffffffffff91, float:NaN)
            if (r19 == 0) goto L_0x0017
            boolean r0 = isApkForbiddenToInstall(r19)
            if (r0 != 0) goto L_0x000f
            goto L_0x0017
        L_0x000f:
            com.android.server.pm.PackageManagerException r0 = new com.android.server.pm.PackageManagerException
            java.lang.String r4 = "The package is forbid to install"
            r0.<init>(r3, r4)
            throw r0
        L_0x0017:
            r4 = r21
            java.lang.String r5 = r19.getAbsolutePath()
            r6 = 1
            if (r5 != 0) goto L_0x0021
            return r6
        L_0x0021:
            r7 = 2000(0x7d0, float:2.803E-42)
            java.lang.String r8 = " pkg : "
            if (r2 != r7) goto L_0x0069
            r9 = -1
            boolean r0 = isSecondUserlocked(r18)     // Catch:{ all -> 0x0037 }
            if (r0 == 0) goto L_0x0031
            r0 = 2
            r9 = r0
            goto L_0x0036
        L_0x0031:
            int r0 = com.android.commands.pm.PmInjector.installVerify(r5)     // Catch:{ all -> 0x0037 }
            r9 = r0
        L_0x0036:
            goto L_0x003f
        L_0x0037:
            r0 = move-exception
            java.lang.String r10 = TAG
            java.lang.String r11 = "Error"
            android.util.Log.e(r10, r11, r0)
        L_0x003f:
            r0 = 2
            if (r9 != r0) goto L_0x0043
            goto L_0x0069
        L_0x0043:
            java.lang.String r0 = TAG
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "MIUILOG- INSTALL_FAILED_USER_RESTRICTED  uid: "
            r6.append(r7)
            r6.append(r2)
            r6.append(r8)
            r6.append(r4)
            java.lang.String r6 = r6.toString()
            android.util.Slog.i(r0, r6)
            java.lang.String r0 = com.android.commands.pm.PmInjector.statusToString(r9)
            com.android.server.pm.PackageManagerException r6 = new com.android.server.pm.PackageManagerException
            r6.<init>(r3, r0)
            throw r6
        L_0x0069:
            if (r2 == 0) goto L_0x01ab
            boolean r0 = android.miui.AppOpsUtils.isXOptMode()
            if (r0 == 0) goto L_0x0074
            r3 = r6
            goto L_0x01ac
        L_0x0074:
            r0 = 1000(0x3e8, float:1.401E-42)
            if (r2 != r0) goto L_0x0079
            return r6
        L_0x0079:
            boolean r0 = isEnterpriseAllowedInstall(r1, r2, r4)
            r3 = -22
            if (r0 == 0) goto L_0x01a3
            android.content.pm.PackageManager r0 = r18.getPackageManager()
            java.lang.String[] r9 = r0.getPackagesForUid(r2)
            r10 = 0
            java.lang.String r0 = "com.miui.securitycenter"
            if (r9 == 0) goto L_0x00ba
            int r11 = r9.length
            if (r11 <= 0) goto L_0x00ba
            r4 = r9[r10]
            java.lang.String r11 = "com.android.vending"
            boolean r11 = r11.equals(r4)
            if (r11 != 0) goto L_0x00b9
            java.lang.String r11 = "com.xiaomi.market"
            boolean r11 = r11.equals(r4)
            if (r11 != 0) goto L_0x00b9
            java.lang.String r11 = "com.xiaomi.gamecenter"
            boolean r11 = r11.equals(r4)
            if (r11 != 0) goto L_0x00b9
            boolean r11 = r0.equals(r4)
            if (r11 != 0) goto L_0x00b9
            java.lang.String r11 = "com.amazon.venezia"
            boolean r11 = r11.equals(r4)
            if (r11 == 0) goto L_0x00ba
        L_0x00b9:
            return r6
        L_0x00ba:
            android.content.pm.PackageManager r11 = r18.getPackageManager()
            boolean r11 = isReleaseRom(r11)
            if (r11 == 0) goto L_0x016e
            android.content.Intent r11 = new android.content.Intent
            java.lang.String r12 = "com.miui.action.PACKAGE_NEEDS_VERIFICATION"
            r11.<init>(r12)
            r11.setPackage(r0)
            r0 = 268435456(0x10000000, float:2.5243549E-29)
            r11.addFlags(r0)
            java.lang.String r0 = "path"
            r11.putExtra(r0, r5)
            java.lang.String r0 = "installerPackage"
            r11.putExtra(r0, r4)
            java.util.concurrent.atomic.AtomicInteger r0 = new java.util.concurrent.atomic.AtomicInteger
            r12 = -100
            r0.<init>(r12)
            r13 = r0
            java.util.concurrent.CountDownLatch r0 = new java.util.concurrent.CountDownLatch
            r0.<init>(r6)
            r14 = r0
            com.android.server.pm.PackageManagerServiceInjector$3 r0 = new com.android.server.pm.PackageManagerServiceInjector$3
            r0.<init>(r13, r14)
            r15 = r0
            java.lang.String r0 = "observer"
            r11.putExtra(r0, r15)
            int r0 = android.os.UserHandle.getUserId(r20)
            if (r2 != r7) goto L_0x0102
            r0 = 0
            r6 = r0
            goto L_0x0103
        L_0x0102:
            r6 = r0
        L_0x0103:
            android.os.UserHandle r0 = new android.os.UserHandle
            r0.<init>(r6)
            r16 = r0
            java.lang.String r0 = TAG
            java.lang.String r7 = "Package verify start"
            android.util.Slog.d(r0, r7)
            java.lang.String r0 = "android.permission.PACKAGE_VERIFICATION_AGENT"
            r7 = r16
            r1.sendBroadcastAsUser(r11, r7, r0)
            r17 = r11
            r10 = 600(0x258, double:2.964E-321)
            java.util.concurrent.TimeUnit r0 = java.util.concurrent.TimeUnit.SECONDS     // Catch:{ InterruptedException -> 0x0122 }
            r14.await(r10, r0)     // Catch:{ InterruptedException -> 0x0122 }
            goto L_0x0123
        L_0x0122:
            r0 = move-exception
        L_0x0123:
            int r0 = r13.get()
            if (r0 != r12) goto L_0x0147
            java.lang.String r3 = TAG
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "MIUILOG- INSTALL_FAILED_VERIFICATION time out  uid: "
            r10.append(r11)
            r10.append(r2)
            r10.append(r8)
            r10.append(r4)
            java.lang.String r8 = r10.toString()
            android.util.Slog.i(r3, r8)
            r3 = 0
            return r3
        L_0x0147:
            if (r0 == 0) goto L_0x014a
            goto L_0x016e
        L_0x014a:
            java.lang.String r10 = TAG
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r12 = "MIUILOG- INSTALL_FAILED_VERIFICATION_FAILURE  uid: "
            r11.append(r12)
            r11.append(r2)
            r11.append(r8)
            r11.append(r4)
            java.lang.String r8 = r11.toString()
            android.util.Slog.i(r10, r8)
            com.android.server.pm.PackageManagerException r8 = new com.android.server.pm.PackageManagerException
            java.lang.String r10 = "FAILED_VERIFICATION_FAILURE MIUI"
            r8.<init>(r3, r10)
            throw r8
        L_0x016e:
            r3 = 2000(0x7d0, float:2.803E-42)
            if (r2 == r3) goto L_0x01a1
            java.util.ArrayList<java.lang.String> r0 = sAllowPackage
            boolean r0 = r0.contains(r4)
            if (r0 == 0) goto L_0x017b
            goto L_0x01a1
        L_0x017b:
            java.lang.String r0 = TAG
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r6 = "MIUILOG- Install Reject uid: "
            r3.append(r6)
            r3.append(r2)
            r3.append(r8)
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            android.util.Slog.i(r0, r3)
            com.android.server.pm.PackageManagerException r0 = new com.android.server.pm.PackageManagerException
            r3 = -110(0xffffffffffffff92, float:NaN)
            java.lang.String r6 = "Permission Denied"
            r0.<init>(r3, r6)
            throw r0
        L_0x01a1:
            r3 = 1
            return r3
        L_0x01a3:
            com.android.server.pm.PackageManagerException r0 = new com.android.server.pm.PackageManagerException
            java.lang.String r6 = "FAILED_VERIFICATION_FAILURE ENTERPRISE"
            r0.<init>(r3, r6)
            throw r0
        L_0x01ab:
            r3 = r6
        L_0x01ac:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.PackageManagerServiceInjector.isAllowedInstall(android.content.Context, java.io.File, int, java.lang.String):boolean");
    }

    private static boolean isApkForbiddenToInstall(File path) {
        PackageParser.ApkLite pkg = null;
        try {
            if (path.isDirectory()) {
                File[] files = path.listFiles();
                if (files != null && files.length > 0) {
                    pkg = PackageParser.parseApkLite(files[0], 0);
                }
            } else {
                pkg = PackageParser.parseApkLite(path, 0);
            }
            if (pkg == null || pkg.packageName == null || !sForbiddenToInstalledPackages.contains(pkg.packageName)) {
                return false;
            }
            String str = TAG;
            Slog.i(str, "MIUILOG- INSTALL_FAILED_USER_RESTRICTED pkg: " + pkg.packageName);
            return true;
        } catch (PackageParser.PackageParserException e) {
            return false;
        }
    }

    public static synchronized boolean isReleaseRom(PackageManager pm) {
        boolean booleanValue;
        synchronized (PackageManagerServiceInjector.class) {
            if (sIsPlatformSignature == null) {
                boolean isPlatform = false;
                try {
                    Signature[] platformSignature = pm.getPackageInfo(PackageManagerService.PLATFORM_PACKAGE_NAME, 64).signatures;
                    Signature platformSig = new Signature(SignatureConstants.PLATFORM);
                    int length = platformSignature.length;
                    int i = 0;
                    while (true) {
                        if (i >= length) {
                            break;
                        } else if (platformSig.equals(platformSignature[i])) {
                            isPlatform = true;
                            break;
                        } else {
                            i++;
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "error");
                }
                sIsPlatformSignature = Boolean.valueOf(isPlatform);
            }
            booleanValue = sIsPlatformSignature.booleanValue();
        }
        return booleanValue;
    }

    public static void beforeSystemReady(Context context) {
        SecSpaceManagerService.init(context);
        GreenGuardManagerService.init(context);
        installBlockApps(context);
        hideOrDisplayApp();
        if (miui.os.Build.IS_INTERNATIONAL_BUILD) {
            registerDataObserver(context);
        }
    }

    private static void hideOrDisplayApp() {
        if (Build.VERSION.SDK_INT >= 24 && !miui.os.Build.IS_INTERNATIONAL_BUILD && miui.os.Build.IS_CM_CUSTOMIZATION) {
            PackageManagerService pms = AppGlobals.getPackageManager();
            PackageHideManager phm = PackageHideManager.getInstance(pms.isFirstBoot());
            List<String> ignoreApkPkgNameList = phm.getIgnoreApkPkgNameList();
            if (ignoreApkPkgNameList != null) {
                boolean appHide = phm.isAppHide();
                Slog.i(TAG, "appHide: " + appHide);
                Settings settings = pms.mSettings;
                synchronized (pms.mPackages) {
                    for (String pkgName : ignoreApkPkgNameList) {
                        PackageSetting packageSetting = settings.mPackages.get(pkgName);
                        if (packageSetting != null) {
                            packageSetting.setInstalled(!appHide, 0);
                        }
                    }
                }
            } else {
                return;
            }
        }
        if ("clover".equals(android.os.Build.DEVICE) && SystemProperties.getBoolean("ro.radio.noril", false)) {
            PackageManagerService pms2 = AppGlobals.getPackageManager();
            synchronized (pms2.mPackages) {
                for (String pkgName2 : WIFI_ONLY_BLOCK_APPS) {
                    PackageSetting packageSetting2 = pms2.mSettings.mPackages.get(pkgName2);
                    if (packageSetting2 != null) {
                        packageSetting2.setInstalled(false, UserHandle.myUserId());
                    }
                }
            }
        }
    }

    private static void installBlockApps(Context context) {
        if (FeatureParser.getBoolean(SUPPORT_OLDMAN_MODE, false)) {
            Settings settings = AppGlobals.getPackageManager().mSettings;
            int mode = 0;
            while (true) {
                String[][] strArr = BLOCK_APPS;
                if (mode < strArr.length) {
                    for (String pkgName : strArr[mode]) {
                        PackageSetting packageSetting = settings.mPackages.get(pkgName);
                        if (packageSetting != null) {
                            packageSetting.setInstalled(true, UserHandle.myUserId());
                        }
                    }
                    mode++;
                } else {
                    return;
                }
            }
        }
    }

    public static void onPackageInstalled(String name, int retCode, boolean update, PackageParser.Package pkg, String installerPkgName) {
        onPackageInstalled(name, retCode, (String) null, update, pkg, installerPkgName);
    }

    public static void onPackageInstalled(String name, int retCode, String retMsg, boolean update, PackageParser.Package pkg, String installerPkgName) {
        int i = 1;
        if (retCode != 1) {
            PackageEvent event = new PackageEvent();
            event.setType(32);
            event.setTimeStamp(System.currentTimeMillis());
            if (update) {
                i = 3;
            }
            event.setAction(i);
            event.setPackageName(name);
            event.setReturnCode(retCode);
            String str = "";
            event.setReturnMsg(retMsg != null ? retMsg : str);
            event.setVersionCode(pkg != null ? pkg.mVersionCode : 0);
            event.setVersionName(pkg != null ? pkg.mVersionName : str);
            if (installerPkgName != null) {
                str = installerPkgName;
            }
            event.setInstallerPkgName(str);
            String str2 = TAG;
            Slog.i(str2, "MIUILOG-onPackageInstalled: " + event);
            MQSEventManagerDelegate.getInstance().reportPackageEvent(event);
        }
    }

    public static void setInstallFlag(boolean flag) {
        mInstallation = flag;
    }

    public static boolean getInstallFlag() {
        return mInstallation;
    }

    public static void onPackageDeleted(String name, int retCode) {
        onPackageDeleted(name, retCode, (String) null);
    }

    public static void onPackageDeleted(String name, int retCode, String retMsg) {
        if (retCode != 1) {
            PackageEvent event = new PackageEvent();
            event.setType(32);
            event.setTimeStamp(System.currentTimeMillis());
            event.setAction(2);
            event.setPackageName(name);
            event.setReturnCode(retCode);
            event.setReturnMsg(retMsg != null ? retMsg : "");
            String str = TAG;
            Slog.i(str, "MIUILOG-onPackageDeleted: " + event);
            MQSEventManagerDelegate.getInstance().reportPackageEvent(event);
        } else if (mInstallation && !miui.os.Build.IS_INTERNATIONAL_BUILD && sGoogleBaseApps.contains(name)) {
            mInstallation = false;
        }
    }

    static boolean isCallerAllowedToSilentlyUninstall(int callingUid, PackageManagerService pms) {
        PackageParser.Package pkg;
        String[] pkgs = pms.getPackagesForUid(callingUid);
        synchronized (pms.mPackages) {
            for (int i = 0; i < pkgs.length; i++) {
                if (sSilentlyUninstallPackages.contains(pkgs[i]) && (pkg = pms.mPackages.get(pkgs[i])) != null && UserHandle.getAppId(callingUid) == pkg.applicationInfo.uid) {
                    return true;
                }
            }
            return false;
        }
    }

    static boolean checkGetInstalledAppsPermission(int callingPid, int callingUid, String where) {
        if (miui.os.Build.IS_INTERNATIONAL_BUILD || UserHandle.getAppId(callingUid) < 10000) {
            return true;
        }
        String callingPackage = ExtraActivityManagerService.getPackageNameByPid(callingPid);
        if (TextUtils.isEmpty(callingPackage)) {
            return true;
        }
        try {
            if (sAppOpsService == null) {
                sAppOpsService = IAppOpsService.Stub.asInterface(ServiceManager.getService("appops"));
            }
            if (sAppOpsService.noteOperation(10022, callingUid, callingPackage) != 0) {
                String str = TAG;
                Slog.e(str, "MIUILOG- Permission Denied " + where + ". pkg : " + callingPackage + " uid : " + callingUid);
                return false;
            }
        } catch (RemoteException e) {
            Slog.e(TAG, "checkGetInstalledAppsPermission", e);
        }
        return true;
    }

    static ArrayList<ApplicationInfo> getInstalledApplicationsWithoutPermission(int callingUid, ArrayList<ApplicationInfo> infos) {
        if (infos == null || infos.size() == 0) {
            return infos;
        }
        int uid = UserHandle.getAppId(callingUid);
        for (int i = infos.size() - 1; i >= 0; i--) {
            ApplicationInfo info = infos.get(i);
            if (info.uid != uid && (info.flags & 1) == 0) {
                infos.remove(i);
            }
        }
        return infos;
    }

    static ArrayList<PackageInfo> getInstalledPackagesWithoutPermission(int callingUid, ArrayList<PackageInfo> infos) {
        if (infos == null || infos.size() == 0) {
            return infos;
        }
        int uid = UserHandle.getAppId(callingUid);
        for (int i = infos.size() - 1; i >= 0; i--) {
            ApplicationInfo info = infos.get(i).applicationInfo;
            if (info.uid != uid && (info.flags & 1) == 0) {
                infos.remove(i);
            }
        }
        return infos;
    }

    static void markPmsScanDetail(PackageManagerService pms) {
        int type;
        int thirdAppCount = 0;
        int systemAppCount = 0;
        int persistAppCount = 0;
        synchronized (pms.mPackages) {
            Iterator<PackageParser.Package> pkgit = pms.mPackages.values().iterator();
            while (true) {
                type = 1;
                if (!pkgit.hasNext()) {
                    break;
                }
                PackageParser.Package pkg = pkgit.next();
                if ((pkg.applicationInfo.flags & 1) != 0) {
                    systemAppCount++;
                    if (pms.mSettings.isDisabledSystemPackageLPr(pkg.applicationInfo.packageName)) {
                        thirdAppCount++;
                    }
                } else {
                    thirdAppCount++;
                }
                if ((pkg.applicationInfo.flags & 8) != 0 && (!pms.mSafeMode || (1 & pkg.applicationInfo.flags) != 0)) {
                    persistAppCount++;
                }
            }
        }
        BootEventManager.getInstance().setSystemAppCount(systemAppCount);
        BootEventManager.getInstance().setThirdAppCount(thirdAppCount);
        BootEventManager.getInstance().setPersistAppCount(persistAppCount);
        if (pms.isFirstBoot()) {
            type = 2;
        } else if (pms.isDeviceUpgrading()) {
            type = 3;
        }
        BootEventManager.getInstance().setBootType(type);
    }

    static void markCoreAppDexopt(long startTime, long endTime) {
        BootEventManager.getInstance().setCoreAppDexopt(endTime - startTime);
    }

    static void markPackageOptimized(PackageParser.Package pkg) {
        BootEventManager manager = BootEventManager.getInstance();
        if (!pkg.isSystem() || pkg.isUpdatedSystemApp()) {
            manager.setDexoptThirdAppCount(manager.getDexoptThirdAppCount() + 1);
        } else {
            manager.setDexoptSystemAppCount(manager.getDexoptSystemAppCount() + 1);
        }
    }

    protected static String getAdjustedFilterIfNeeded(PackageManagerService pms, String packageName, String originalFilter) {
        String compilerFilter = originalFilter;
        if (!PackageManagerServiceCompilerMapping.getCompilerFilterForReason(1).equals(originalFilter) || !isNonProfileFilterNeeded(pms, packageName)) {
            return compilerFilter;
        }
        String compilerFilter2 = PackageManagerServiceCompilerMapping.getCompilerFilterForReason(3);
        String str = TAG;
        Log.i(str, "Ota-opt adjust compiler-filter = " + originalFilter + " for package = " + packageName + " to " + compilerFilter2);
        return compilerFilter2;
    }

    private static boolean isNonProfileFilterNeeded(PackageManagerService pms, String packageName) {
        return sNonProfileCompiledPkgs.contains(packageName) || getTop10ThirdPartyPkgs(pms).contains(packageName);
    }

    private static List<UsageStats> getRecentlyWeekUsageStats(Context context) {
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService("usagestats");
        if (usm != null) {
            long tillTime = System.currentTimeMillis();
            Map<String, UsageStats> statsMap = usm.queryAndAggregateUsageStats(tillTime - UnixCalendar.WEEK_IN_MILLIS, tillTime);
            if (statsMap != null && !statsMap.isEmpty()) {
                List<UsageStats> entryList = new ArrayList<>();
                for (Map.Entry<String, UsageStats> entry : statsMap.entrySet()) {
                    entryList.add(entry.getValue());
                }
                Collections.sort(entryList, new Comparator<UsageStats>() {
                    public int compare(UsageStats left, UsageStats right) {
                        return Long.signum(right.getTotalTimeInForeground() - left.getTotalTimeInForeground());
                    }
                });
                return entryList;
            }
        }
        return Collections.emptyList();
    }

    private static Set<String> getTop10ThirdPartyPkgs(PackageManagerService pms) {
        if (sTop10ThirdPartyPks == null) {
            sTop10ThirdPartyPks = new ArraySet();
            for (UsageStats usage : getRecentlyWeekUsageStats(pms.mContext)) {
                String packageName = usage.getPackageName();
                synchronized (pms.mPackages) {
                    PackageParser.Package pkg = pms.mPackages.get(packageName);
                    if (pkg != null) {
                        if (!pkg.isSystem()) {
                            sTop10ThirdPartyPks.add(packageName);
                        }
                    }
                }
            }
        }
        return sTop10ThirdPartyPks;
    }

    public static List<String> getSecondaryDexOptPackages(PackageManagerService pms) {
        ArrayList<String> results = new ArrayList<>();
        results.addAll(sSecondaryDexOptPkgs);
        int count = 0;
        Iterator<String> it = getTop10ThirdPartyPkgs(pms).iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            String pkg = it.next();
            if (!results.contains(pkg) && !sSecondaryDexOptBlackList.contains(pkg)) {
                results.add(pkg);
                int count2 = count + 1;
                if (count >= 5) {
                    int i = count2;
                    break;
                }
                count = count2;
            }
        }
        return results;
    }

    protected static boolean performDexOptSecondary(PackageManagerService pms, String packageName) {
        String compilerFilter = PackageManagerServiceCompilerMapping.getCompilerFilterForReason(0);
        String str = TAG;
        Log.i(str, "Ota-opt performDexOptSecondary for package = " + packageName + " compiler-filter = " + compilerFilter);
        return pms.performDexOptSecondary(packageName, compilerFilter, false);
    }

    static ProviderInfo resolveContentProvider(Settings mSettings, ProviderInfo info, int flags, int userId, PackageParser.Provider provider, PackageSetting ps) {
        if (userId != 999 || !PackageManagerServiceCompat.isEnabledAndMatchLPr(mSettings, info, flags, 0)) {
            return null;
        }
        return PackageParser.generateProviderInfo(provider, flags, ps.readUserState(0), 0);
    }

    static ProviderInfo queryProvider(ComponentResolver resolver, String name, int flags, int userId) {
        if (resolver == null || userId != 999) {
            return null;
        }
        return resolver.queryProvider(name, flags, 0);
    }

    static boolean isPreinstallApp(String packageName) {
        return PreinstallApp.isPreinstallApp(packageName);
    }

    public static boolean needClearDefaultBrowserSettings(String currentDefaultPkg) {
        if (!isCTS() && !miui.os.Build.IS_INTERNATIONAL_BUILD) {
            return false;
        }
        return true;
    }

    public static String getMiuiDefaultBrowserPkg() {
        if (!miui.os.Build.IS_INTERNATIONAL_BUILD && !isCTS()) {
            return MIUI_BROWSER_PACKAGE;
        }
        return null;
    }

    public static void checkPkgInstallerOptMode(PackageManagerService pms) {
        try {
            synchronized (pms.mPackages) {
                checkPackageInstallerStatus(pms, pms.mSettings);
            }
            pms.checkPkgInstallerOptMode();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public static void checkGTSSpecAppOptMode(PackageManagerService pms) {
        if (Build.VERSION.SDK_INT >= 29 && !miui.os.Build.IS_INTERNATIONAL_BUILD) {
            synchronized (pms.mPackages) {
                Settings curPkgSettings = pms.mSettings;
                boolean isCtsBuild = AppOpsUtils.isXOptMode();
                for (String pkg : new String[]{"com.miui.cleanmaster", "com.xiaomi.drivemode", "com.xiaomi.aiasst.service"}) {
                    PackageSetting uninstallPkg = curPkgSettings.mPackages.get(pkg);
                    if (isCtsBuild && uninstallPkg != null && !uninstallPkg.isSystem()) {
                        uninstallPkg.setInstalled(false, 0);
                        curPkgSettings.mPackages.remove(pkg);
                        if (curPkgSettings.isDisabledSystemPackageLPr(pkg)) {
                            curPkgSettings.removeDisabledSystemPackageLPw(pkg);
                        }
                        pms.mPackages.remove(pkg);
                    }
                }
            }
        }
    }

    public static boolean isCTS() {
        return !SystemProperties.getBoolean("persist.sys.miui_optimization", !"1".equals(SystemProperties.get("ro.miui.cts")));
    }

    static boolean isEnterpriseAllowedInstall(Context context, int callingUid, String callerPackage) {
        if (!EnterpriseSettings.ENTERPRISE_ACTIVATED || !ApplicationHelper.isTrustedAppStoresEnabled(context, UserHandle.getUserId(callingUid)) || sEPAllowedPackage.contains(callerPackage) || ApplicationHelper.getTrustedAppStores(context, UserHandle.getUserId(callingUid)).contains(callerPackage)) {
            return true;
        }
        Slog.d("Enterprise", "Installer:" + callerPackage + " was forbid to install pkg");
        return false;
    }

    static boolean checkEnterpriseRestriction(PackageManagerService pms, PackageParser.Package pkg) {
        if (pkg.requestedPermissions.contains(Manifest.permission.ACCESS_ENTERPRISE_API) && !EnterpriseVerifier.verify(pms.mContext, pkg.baseCodePath, pkg.packageName)) {
            Slog.d("Enterprise", "Verify enterprise signature of package " + pkg.packageName + " failed");
            return true;
        } else if (!EnterpriseSettings.ENTERPRISE_ACTIVATED || !ApplicationHelper.checkEnterprisePackageRestriction(pms.mContext, pkg.packageName)) {
            return false;
        } else {
            Slog.d("Enterprise", "Installation of package " + pkg.packageName + " is restricted");
            return true;
        }
    }

    static boolean isVerificationEnabled(Context context, int userId, int installFlags, int installerUid) {
        if (Settings.Global.getInt(context.getContentResolver(), "device_provisioned", 0) != 1) {
            return false;
        }
        if ((AppOpsUtils.isXOptMode() || (miui.os.Build.IS_INTERNATIONAL_BUILD && installerUid != 1000)) && Settings.Global.getInt(context.getContentResolver(), "package_verifier_enable", 1) == 1) {
            return true;
        }
        return false;
    }

    public static String maybeAdjustCompilerFilter(PackageParser.Package pkg, int compileReason, String orgCompilerFilter, String profileName, String dexMetadataPath) {
        return orgCompilerFilter;
    }

    static void setDefaultHome(int userId, String packageName) {
        synchronized (sDefaultHome) {
            sDefaultHome.put(userId, packageName);
        }
    }

    public static String getDefaultHome(int userId) {
        String str;
        synchronized (sDefaultHome) {
            str = sDefaultHome.get(userId);
        }
        return str;
    }

    private static boolean isSecondUserlocked(Context context) {
        boolean iscts = AppOpsUtils.isXOptMode();
        int userid = PmInjector.getDefaultUserId();
        UserManager userManager = (UserManager) context.getSystemService("user");
        if (!iscts || userid == 0 || userManager.isUserUnlocked(userid)) {
            return false;
        }
        return true;
    }

    private static Handler getFirstUseHandler() {
        if (sFirstUseHandler == null) {
            synchronized (sFirstUseLock) {
                if (sFirstUseHandler == null) {
                    sFirstUseThread = new HandlerThread("first_use_thread");
                    sFirstUseThread.start();
                    sFirstUseHandler = new Handler(sFirstUseThread.getLooper());
                }
            }
        }
        return sFirstUseHandler;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:19:0x003a, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean isFirstUseCompileNeeded(com.android.server.pm.PackageManagerService r4, java.lang.String r5) {
        /*
            java.lang.String r0 = "xiaomi"
            boolean r0 = r5.contains(r0)
            r1 = 0
            if (r0 != 0) goto L_0x003e
            java.lang.String r0 = "miui"
            boolean r0 = r5.contains(r0)
            if (r0 != 0) goto L_0x003e
            java.lang.String r0 = "GSMA"
            boolean r0 = r5.contains(r0)
            if (r0 == 0) goto L_0x001c
            goto L_0x003e
        L_0x001c:
            android.util.ArrayMap<java.lang.String, android.content.pm.PackageParser$Package> r0 = r4.mPackages
            monitor-enter(r0)
            android.util.ArrayMap<java.lang.String, android.content.pm.PackageParser$Package> r2 = r4.mPackages     // Catch:{ all -> 0x003b }
            java.lang.Object r2 = r2.get(r5)     // Catch:{ all -> 0x003b }
            android.content.pm.PackageParser$Package r2 = (android.content.pm.PackageParser.Package) r2     // Catch:{ all -> 0x003b }
            if (r2 == 0) goto L_0x0039
            boolean r3 = r2.isSystem()     // Catch:{ all -> 0x003b }
            if (r3 != 0) goto L_0x0039
            boolean r3 = r2.isUpdatedSystemApp()     // Catch:{ all -> 0x003b }
            if (r3 == 0) goto L_0x0036
            goto L_0x0039
        L_0x0036:
            monitor-exit(r0)     // Catch:{ all -> 0x003b }
            r0 = 1
            return r0
        L_0x0039:
            monitor-exit(r0)     // Catch:{ all -> 0x003b }
            return r1
        L_0x003b:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x003b }
            throw r1
        L_0x003e:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.PackageManagerServiceInjector.isFirstUseCompileNeeded(com.android.server.pm.PackageManagerService, java.lang.String):boolean");
    }

    public static void processFirstUseActivity(final PackageManagerService pm, final String packageName) {
        if (isFirstUseCompileNeeded(pm, packageName)) {
            getFirstUseHandler().post(new Runnable() {
                public void run() {
                    PackageManagerServiceInjector.handleFirstUseActivity(PackageManagerService.this, packageName);
                }
            });
        }
    }

    public static void handleFirstUseActivity(PackageManagerService pm, String packageName) {
        boolean success = pm.performDexOptMode(packageName, true, "speed-profile", false, true, (String) null);
        String str = TAG;
        Slog.d(str, "FirstUseActivity packageName = " + packageName + " is compiled using filter speed-profile, success = " + success);
        PinnerService pinnerService = (PinnerService) LocalServices.getService(PinnerService.class);
        if (pinnerService != null) {
            String str2 = TAG;
            Log.i(str2, "FirstUseActivity Pinning optimized code " + packageName);
            ArraySet<String> packages = new ArraySet<>();
            packages.add(packageName);
            pinnerService.update(packages, false);
        }
    }

    public static String dexoptResultToString(int result) {
        if (result == -1) {
            return "DEX_OPT_FAILED";
        }
        if (result == 0) {
            return "DEX_OPT_SKIPPED";
        }
        if (result != 1) {
            return "DEX_OPT_FAILED";
        }
        return "DEX_OPT_PERFORMED";
    }

    public static void handleTopAppLoadSecondaryDex(PackageDexOptimizer pdo, ApplicationInfo loaderAppinfo, PackageDexUsage.DexUseInfo dexUseInfo, String dexPath) {
        handleTopAppLoadSecondaryDexReason(pdo, loaderAppinfo, dexUseInfo, dexPath, 3);
    }

    public static void handleTopAppLoadSecondaryDexReason(PackageDexOptimizer pdo, ApplicationInfo loaderAppinfo, PackageDexUsage.DexUseInfo dexUseInfo, String dexPath, int reason) {
        String topAppPackageName = ActivityManagerServiceInjector.getTopAppPackageName();
        if (topAppPackageName == null || !loaderAppinfo.packageName.equals(topAppPackageName)) {
            String str = TAG;
            Slog.d(str, "Skip dexopt on: " + dexPath + ", because " + loaderAppinfo.packageName + " is not current resumed. Current resumed is: " + topAppPackageName);
            return;
        }
        int result = pdo.dexOptSecondaryDexPath(loaderAppinfo, dexPath, dexUseInfo, new DexoptOptions(loaderAppinfo.packageName, reason, "verify", (String) null, 12));
        String str2 = TAG;
        Slog.d(str2, "Run dexopt on: " + dexPath + ", because top-app{" + loaderAppinfo.packageName + "} loading it. Dexopt result: " + dexoptResultToString(result));
    }

    public static void processTopAppLoadSecondaryDexReason(PackageDexOptimizer pdo, ApplicationInfo loaderAppinfo, PackageDexUsage.DexUseInfo dexUseInfo, String dexPath, int reason) {
        if (dexUseInfo == null) {
            String str = TAG;
            Slog.d(str, "Oops, can't get dexUseInfo with " + dexPath + ", skip dexopt on it");
            return;
        }
        final PackageDexOptimizer packageDexOptimizer = pdo;
        final ApplicationInfo applicationInfo = loaderAppinfo;
        final PackageDexUsage.DexUseInfo dexUseInfo2 = dexUseInfo;
        final String str2 = dexPath;
        final int i = reason;
        getFirstUseHandler().post(new Runnable() {
            public void run() {
                PackageManagerServiceInjector.handleTopAppLoadSecondaryDexReason(PackageDexOptimizer.this, applicationInfo, dexUseInfo2, str2, i);
            }
        });
    }

    public static void processTopAppLoadSecondaryDex(final PackageDexOptimizer pdo, final ApplicationInfo loaderAppinfo, final PackageDexUsage.DexUseInfo dexUseInfo, final String dexPath) {
        if (dexUseInfo == null) {
            String str = TAG;
            Slog.d(str, "Oops, can't get dexUseInfo with " + dexPath + ", skip dexopt on it");
            return;
        }
        getFirstUseHandler().post(new Runnable() {
            public void run() {
                PackageManagerServiceInjector.handleTopAppLoadSecondaryDex(PackageDexOptimizer.this, loaderAppinfo, dexUseInfo, dexPath);
            }
        });
    }

    public static boolean canInterceptByMiAppStore(PackageManagerService pms, List<ResolveInfo> resolveInfos) {
        if (!miui.os.Build.IS_INTERNATIONAL_BUILD || resolveInfos == null) {
            return false;
        }
        for (ResolveInfo resolveInfo : resolveInfos) {
            Bundle bundle = resolveInfo.activityInfo.metaData;
            if (bundle != null) {
                try {
                    if (bundle.getBoolean("mi_use_custom_resolver") && resolveInfo.activityInfo.enabled) {
                        return true;
                    }
                } catch (Exception e) {
                    Log.w(TAG, e.getMessage());
                }
            }
        }
        return false;
    }

    static void markUidChangedApps(PackageManagerService pkms) {
        ArrayList<String> arrayList;
        if (pkms.isDeviceUpgrading() && (arrayList = appsMovedToDataPartition) != null) {
            Iterator<String> it = arrayList.iterator();
            while (it.hasNext()) {
                PackageSetting pkgSetting = pkms.mSettings.getPackageLPr(it.next());
                if (pkgSetting != null) {
                    pkgSetting.uidError = true;
                }
            }
            purgeAppsMovedToDataPartition();
        }
    }

    static boolean isRecoverAppDataAllowed(PackageManagerService pkms, PackageSetting ps) {
        return ps != null && ps.uidError;
    }

    private static boolean isInFixUpAppIdWhiteList(String pkg) {
        ArrayList<String> arrayList = fixUpAppIdWhiteList;
        return arrayList != null && arrayList.contains(pkg);
    }

    private static void removeFromFixUpAppIdWhiteList(String pkg) {
        ArrayList<String> arrayList = fixUpAppIdWhiteList;
        if (arrayList != null) {
            arrayList.remove(pkg);
        }
    }

    private static void purgeAppsMovedToDataPartition() {
        appsMovedToDataPartition = null;
        fixUpAppIdWhiteList = null;
    }

    static void preserveAppId(PackageManagerService pkms, PackageSetting removedPs) {
        if (removedPs.appId >= 10000 && removedPs.appId <= 19999 && pkms.isDeviceUpgrading() && removedPs.isSystem()) {
            if (appsMovedToDataPartition == null) {
                appsMovedToDataPartition = new ArrayList<>();
            }
            appsMovedToDataPartition.add(removedPs.name);
            if (fixUpAppIdWhiteList == null) {
                fixUpAppIdWhiteList = new ArrayList<>();
            }
            if (SystemProperties.get("ro.miui.pm.movedtodata.apps", "").contains(removedPs.name)) {
                fixUpAppIdWhiteList.add(removedPs.name);
            }
        }
    }

    private static SettingBase getPreservedUserId(Settings settings, PackageParser.Package pkg) {
        for (int uid = 10000; uid <= 19999; uid++) {
            SettingBase preservedUserId = settings.getSettingLPr(uid);
            if (preservedUserId != null) {
                if (preservedUserId instanceof SharedUserSetting) {
                    if (pkg.mSharedUserId == null) {
                        continue;
                    } else {
                        SharedUserSetting preservedSharedUser = (SharedUserSetting) preservedUserId;
                        if (TextUtils.equals(preservedSharedUser.name, pkg.mSharedUserId)) {
                            if (!preservedSharedUser.packages.isEmpty()) {
                                return null;
                            }
                            return preservedSharedUser;
                        }
                    }
                } else if ((preservedUserId instanceof PackageSetting) && pkg.mSharedUserId == null) {
                    PackageSetting preservedPs = (PackageSetting) preservedUserId;
                    if (TextUtils.equals(pkg.packageName, preservedPs.name)) {
                        return preservedPs;
                    }
                }
            }
        }
        return null;
    }

    public static boolean checkSignatures(PackageParser.SigningDetails p1, PackageParser.SigningDetails p2) {
        return PackageManagerServiceUtils.compareSignatures(p1.signatures, p2.signatures) == 0;
    }

    static void fixUpAppIdLPr(PackageManagerService pkms, PackageSetting newPkgSetting, PackageParser.Package pkg) {
        if (pkms.isDeviceUpgrading() && !pkg.isSystem() && pkms.mSettings.getPackageLPr(pkg.packageName) == null && isInFixUpAppIdWhiteList(pkg.packageName)) {
            removeFromFixUpAppIdWhiteList(pkg.packageName);
            SettingBase preservedUserId = getPreservedUserId(pkms.mSettings, pkg);
            if (preservedUserId == null) {
                PackageManagerServiceUtils.logCriticalInfo(6, "no preserved app id for " + pkg.packageName);
            } else if (preservedUserId instanceof SharedUserSetting) {
                SharedUserSetting preservedSharedUser = (SharedUserSetting) preservedUserId;
                if (pkg.mSigningDetails == PackageParser.SigningDetails.UNKNOWN) {
                    try {
                        PackageParser.collectCertificates(pkg, false);
                    } catch (Exception e) {
                        PackageManagerServiceUtils.logCriticalInfo(6, "fixUpAppIdLPr: failed to collect certs for " + pkg + ":" + e.getMessage());
                        return;
                    }
                }
                if (!checkSignatures(preservedSharedUser.signatures.mSigningDetails, pkg.mSigningDetails)) {
                    PackageManagerServiceUtils.logCriticalInfo(6, "package " + pkg.packageName + " changed pre-install location to data partition, failed to restore app id: signatures mismatch");
                    return;
                }
                PackageManagerServiceUtils.logCriticalInfo(4, "package " + pkg.packageName + " changed pre-install location to data partition, restored app id " + preservedSharedUser.userId + " for it");
                pkms.mSettings.mSharedUsers.put(preservedSharedUser.name, preservedSharedUser);
            } else if (!(preservedUserId instanceof PackageSetting)) {
            } else {
                if (newPkgSetting == null) {
                    PackageManagerServiceUtils.logCriticalInfo(6, "fixUpAppIdLPr: null package setting for " + pkg);
                    return;
                }
                PackageSetting preservedPs = (PackageSetting) preservedUserId;
                if (!preservedPs.isSystem()) {
                    PackageManagerServiceUtils.logCriticalInfo(6, "preserved user info doesn't own system flags, bail");
                } else if (preservedPs.signatures == null || !checkSignatures(preservedPs.signatures.mSigningDetails, pkg.mSigningDetails)) {
                    PackageManagerServiceUtils.logCriticalInfo(6, "package " + pkg.packageName + " changed pre-install location to data partition, failed to restore app id: signatures mismatch");
                } else {
                    PackageManagerServiceUtils.logCriticalInfo(4, "package " + pkg.packageName + " changed pre-install location to data partition, restore app id " + preservedPs.appId + " for it");
                    newPkgSetting.appId = preservedPs.appId;
                }
            }
        }
    }

    public static void onAppPermFlagsModified(String permName, String packageName, int flagMask, int flagValues, int callingUid, int userId, boolean overridePolicy) {
        long callingIdentity;
        if (miui.os.Build.IS_INTERNATIONAL_BUILD) {
            try {
                if (sAppOpsService == null) {
                    sAppOpsService = IAppOpsService.Stub.asInterface(ServiceManager.getService("appops"));
                }
                callingIdentity = Binder.clearCallingIdentity();
                sAppOpsService.onAppPermFlagsModified(permName, packageName, flagMask, flagValues, callingUid, userId, overridePolicy);
                Binder.restoreCallingIdentity(callingIdentity);
            } catch (RemoteException e) {
                Slog.e(TAG, "onAppPermFlagsModified", e);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(callingIdentity);
                throw th;
            }
        }
    }

    public static void onAppRuntimePermStateModified(String permName, String packageName, boolean granted, int callingUid, int userId, boolean overridePolicy) {
        long callingIdentity;
        if (miui.os.Build.IS_INTERNATIONAL_BUILD) {
            try {
                if (sAppOpsService == null) {
                    sAppOpsService = IAppOpsService.Stub.asInterface(ServiceManager.getService("appops"));
                }
                callingIdentity = Binder.clearCallingIdentity();
                sAppOpsService.onAppRuntimePermStateModified(permName, packageName, granted, callingUid, userId, overridePolicy);
                Binder.restoreCallingIdentity(callingIdentity);
            } catch (RemoteException e) {
                Slog.e(TAG, "onAppRuntimePermStateModified", e);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(callingIdentity);
                throw th;
            }
        }
    }
}
