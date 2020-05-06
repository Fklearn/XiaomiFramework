package com.miui.server;

import android.app.AppOpsManager;
import android.app.INotificationManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageHideManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManagerCompat;
import android.content.pm.ParceledListSlice;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.miui.AppOpsUtils;
import android.miui.Manifest;
import android.miui.R;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.Registrant;
import android.os.RegistrantList;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.util.MiuiMultiWindowUtils;
import android.util.Slog;
import android.util.SparseArray;
import android.util.Xml;
import com.android.internal.app.IWakePathCallback;
import com.android.internal.content.PackageMonitor;
import com.android.internal.os.AtomicFile;
import com.android.internal.util.FastXmlSerializer;
import com.android.server.LocalServices;
import com.android.server.MiuiNetworkManagementService;
import com.android.server.MiuiUiModeManagerStub;
import com.android.server.am.AutoStartManagerService;
import com.android.server.am.ExtraActivityManagerService;
import com.android.server.lights.Light;
import com.android.server.lights.LightsManager;
import com.android.server.pm.DefaultPermissionGrantPolicyInjector;
import com.android.server.pm.PackageManagerService;
import com.android.server.pm.PackageManagerServiceCompat;
import com.android.server.pm.PackageManagerServiceInjector;
import com.android.server.pm.PackageManagerServicePermissionProxy;
import com.android.server.pm.UserManagerService;
import com.android.server.wm.ActivityTaskManagerService;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import miui.content.pm.PreloadedAppPolicy;
import miui.content.res.IconCustomizer;
import miui.content.res.ThemeNativeUtils;
import miui.content.res.ThemeRuntimeManager;
import miui.os.Build;
import miui.os.FileUtils;
import miui.reflect.Field;
import miui.security.ISecurityCallback;
import miui.security.ISecurityManager;
import miui.security.SecurityManager;
import miui.security.SecurityManagerCompat;
import miui.security.WakePathChecker;
import miui.security.WakePathComponent;
import miui.securityspace.ConfigUtils;
import miui.securityspace.XSpaceUserHandle;
import miui.util.FeatureParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class SecurityManagerService extends ISecurityManager.Stub {
    private static final String CLASS_NAME = "classname";
    private static final String CLASS_NAMES = "classnames";
    private static final boolean DEBUG = false;
    private static final String DEF_BROWSER_COUNT = "miui.sec.defBrowser";
    public static final int INSTALL_FULL_APP = 16384;
    public static final int INSTALL_REASON_USER = 4;
    private static final String LEADCORE = "leadcore";
    public static final long LOCK_TIME_OUT = 60000;
    private static final int MSG_SHOW_DIALOG = 1;
    private static final String MTK = "mediatek";
    private static final String NAME = "name";
    private static final String PACKAGE_SECURITYCENTER = "com.miui.securitycenter";
    /* access modifiers changed from: private */
    public static final String PKG_BROWSER = (Build.IS_INTERNATIONAL_BUILD ? "com.mi.globalbrowser" : "com.android.browser");
    private static final int REMVOE_AC_PACKAGE = 4;
    private static final int RTC_POWEROFF_WAKEUP_MTK = 8;
    private static final int SYS_APP_CRACKED = 1;
    private static final int SYS_APP_NOT_CRACKED = 0;
    private static final int SYS_APP_UNINIT = -1;
    static final String TAG = "SecurityManagerService";
    private static final String TIME = "time";
    private static final String UPDATE_VERSION = "1.0";
    private static final String WAKEALARM_PATH_OF_LEADCORE = "/sys/comip/rtc_alarm";
    private static final String WAKEALARM_PATH_OF_QCOM = "/sys/class/rtc/rtc0/wakealarm";
    private static final int WRITE_BOOTTIME_DELAY = 1000;
    private static final int WRITE_BOOT_TIME = 3;
    private static final int WRITE_SETTINGS = 1;
    private static final int WRITE_SETTINGS_DELAY = 1000;
    private static final int WRITE_WAKE_UP_TIME = 2;
    private static AppRunningControlService mAppRunningControlService;
    private AccessController mAccessController;
    private AppOpsManager mAom;
    private IBinder mAppRunningControlBinder;
    private RegistrantList mAppsPreInstallRegistrant = new RegistrantList();
    /* access modifiers changed from: private */
    public Context mContext;
    private boolean mDialogFlag = false;
    private boolean mFingerprintNotify;
    private Handler mHandler;
    private HandlerThread mHandlerThread;
    private INotificationManager mINotificationManager;
    private ArrayList<String> mIncompatibleAppList = new ArrayList<>();
    private boolean mIsUpdated;
    private Light mLedLight;
    private final int mLightOn;
    private PackageManagerService mPackageManagerService;
    private PackageMonitor mPackageMonitor;
    private Object mRegistrantLock = new Object();
    private SecuritySmsHandler mSecuritySmsHandler;
    /* access modifiers changed from: private */
    public SecurityWriteHandler mSecurityWriteHandler;
    /* access modifiers changed from: private */
    public AtomicFile mSettingsFile;
    private SettingsObserver mSettingsObserver;
    /* access modifiers changed from: private */
    public int mSysAppCracked = -1;
    private UserManagerService mUserManager;
    /* access modifiers changed from: private */
    public final Object mUserStateLock = new Object();
    final SparseArray<UserState> mUserStates = new SparseArray<>(3);
    /* access modifiers changed from: private */
    public long mWakeTime;
    private AtomicFile mWakeUpFile;
    /* access modifiers changed from: private */
    public HashMap<String, Long> mWakeUpTime = new HashMap<>();
    /* access modifiers changed from: private */
    public ISecurityCallback sGoogleBaseService;

    /* access modifiers changed from: private */
    public native boolean nativeIsReleased();

    private native void nativeKillPackageProcesses(int i, String str);

    static {
        System.loadLibrary("miui_security");
    }

    /* access modifiers changed from: protected */
    public void dump(FileDescriptor fd, PrintWriter fout, String[] args) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.DUMP") != 0) {
            fout.println("Permission Denial: can't dump SecurityManager from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
            return;
        }
        WakePathChecker.getInstance().dump(fout);
    }

    private static final class UserState {
        GameBoosterServiceDeath gameBoosterServiceDeath;
        final ArraySet<String> mAccessControlCanceled;
        boolean mAccessControlEnabled;
        final ArrayMap<String, Long> mAccessControlLastCheck;
        boolean mAccessControlLockConvenient;
        int mAccessControlLockMode;
        final HashSet<String> mAccessControlPassPackages;
        boolean mAccessControlSettingInit;
        /* access modifiers changed from: private */
        public int mAppPermissionControlStatus;
        boolean mIsGameMode;
        String mLastResumePackage;
        final HashMap<String, PackageSetting> mPackages;
        int userHandle;

        private UserState() {
            this.mAppPermissionControlStatus = 1;
            this.mAccessControlPassPackages = new HashSet<>();
            this.mPackages = new HashMap<>();
            this.mAccessControlCanceled = new ArraySet<>();
            this.mAccessControlLastCheck = new ArrayMap<>();
            this.mAccessControlLockMode = 0;
        }
    }

    private class GameBoosterServiceDeath implements IBinder.DeathRecipient {
        /* access modifiers changed from: private */
        public IBinder mGameBoosterService;
        private UserState mUserState;

        public GameBoosterServiceDeath(UserState userState, IBinder gameBoosterService) {
            this.mUserState = userState;
            this.mGameBoosterService = gameBoosterService;
        }

        public void binderDied() {
            synchronized (SecurityManagerService.this.mUserStateLock) {
                try {
                    this.mGameBoosterService.unlinkToDeath(this, 0);
                    this.mUserState.mIsGameMode = false;
                    this.mUserState.gameBoosterServiceDeath = null;
                } catch (Exception e) {
                    Log.e(SecurityManagerService.TAG, "GameBoosterServiceDeath", e);
                }
            }
        }
    }

    private static class AppItem {
        boolean mCheckEnable;
        String mPkg;
        String mSignature;

        public AppItem(String pkg, String signature, boolean ce) {
            this.mPkg = pkg;
            this.mSignature = signature;
            this.mCheckEnable = ce;
        }
    }

    class MyPackageMonitor extends PackageMonitor {
        MyPackageMonitor() {
        }

        public void onPackageAdded(String packageName, int uid) {
            WakePathChecker.getInstance().onPackageAdded(SecurityManagerService.this.mContext);
            checkDefaultBrowser(uid);
        }

        public void onPackageRemoved(String packageName, int uid) {
            checkDefaultBrowser(uid);
        }

        public void onPackagesAvailable(String[] packages) {
        }

        public void onPackagesUnavailable(String[] packages) {
        }

        public void onPackageUpdateStarted(String packageName, int uid) {
            checkDefaultBrowser(uid);
        }

        private void checkDefaultBrowser(int uid) {
            if (!Build.IS_INTERNATIONAL_BUILD && !AppOpsUtils.isXOptMode() && Build.VERSION.SDK_INT >= 24) {
                SecurityManagerService.this.mSecurityWriteHandler.postDelayed(new Runnable() {
                    public void run() {
                        ContentResolver cr = SecurityManagerService.this.mContext.getContentResolver();
                        try {
                            SecurityManagerService.this.checkIntentFilterVerifications();
                            int defBrowserCount = Settings.Secure.getInt(cr, SecurityManagerService.DEF_BROWSER_COUNT, -1);
                            boolean allow = true;
                            if (defBrowserCount >= 10 && defBrowserCount < 100) {
                                Settings.Secure.putInt(cr, SecurityManagerService.DEF_BROWSER_COUNT, defBrowserCount + 1);
                                allow = false;
                            }
                            if (allow) {
                                SecurityManagerService.this.setDefaultBrowser();
                            }
                        } catch (Exception e) {
                            Log.e(SecurityManagerService.TAG, "checkDefaultBrowser", e);
                        }
                    }
                }, 300);
            }
        }
    }

    class SecurityWriteHandler extends Handler {
        SecurityWriteHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i == 1) {
                Process.setThreadPriority(0);
                synchronized (SecurityManagerService.this.mSettingsFile) {
                    removeMessages(1);
                    SecurityManagerService.this.writeSettings();
                }
                Process.setThreadPriority(10);
            } else if (i == 2) {
                Process.setThreadPriority(0);
                synchronized (SecurityManagerService.this.mWakeUpTime) {
                    removeMessages(2);
                    SecurityManagerService.this.writeWakeUpTime();
                }
                Process.setThreadPriority(10);
            } else if (i == 3) {
                Process.setThreadPriority(0);
                synchronized (SecurityManagerService.this.mWakeUpTime) {
                    removeMessages(3);
                    if (FeatureParser.hasFeature("vendor", 3)) {
                        SecurityManagerCompat.writeBootTime(SecurityManagerService.this.mContext, FeatureParser.getString("vendor"), SecurityManagerService.this.mWakeTime);
                        Log.d(SecurityManagerService.TAG, "Wake up time updated " + SecurityManagerService.this.mWakeTime);
                    } else {
                        Log.w(SecurityManagerService.TAG, "There is no corresponding feature!");
                    }
                }
                Process.setThreadPriority(10);
            } else if (i == 4) {
                synchronized (SecurityManagerService.this.mUserStateLock) {
                    SecurityManagerService.this.getUserStateLocked(msg.arg1).mAccessControlCanceled.remove((String) msg.obj);
                }
            }
        }
    }

    private class SettingsObserver extends ContentObserver {
        private final Uri mAccessControlLockConvenientUri = Settings.Secure.getUriFor("access_control_lock_convenient");
        private final Uri mAccessControlLockEnabledUri = Settings.Secure.getUriFor("access_control_lock_enabled");
        private final Uri mAccessControlLockModedUri = Settings.Secure.getUriFor("access_control_lock_mode");
        private final Uri mAccessMiuiOptimizationUri = Settings.Secure.getUriFor("miui_optimization");

        public SettingsObserver(Handler handler, Context context) {
            super(handler);
            ContentResolver resolver = context.getContentResolver();
            resolver.registerContentObserver(this.mAccessControlLockEnabledUri, false, this, -1);
            resolver.registerContentObserver(this.mAccessControlLockModedUri, false, this, -1);
            resolver.registerContentObserver(this.mAccessControlLockConvenientUri, false, this, -1);
            resolver.registerContentObserver(this.mAccessMiuiOptimizationUri, false, this, -1);
        }

        public void onChange(boolean selfChange, Uri uri) {
            onChange(selfChange, uri, 0);
        }

        public void onChange(boolean selfChange, Uri uri, int userId) {
            if (this.mAccessMiuiOptimizationUri.equals(uri)) {
                SecurityManagerService.this.updateAccessMiuiOptUri();
                return;
            }
            synchronized (SecurityManagerService.this.mUserStateLock) {
                UserState userState = SecurityManagerService.this.getUserStateLocked(userId);
                if (this.mAccessControlLockEnabledUri.equals(uri)) {
                    SecurityManagerService.this.updateAccessControlEnabledLocked(userState);
                } else if (this.mAccessControlLockModedUri.equals(uri)) {
                    SecurityManagerService.this.updateAccessControlLockModeLocked(userState);
                } else if (this.mAccessControlLockConvenientUri.equals(uri)) {
                    SecurityManagerService.this.updateAccessControlLockConvenientLocked(userState);
                }
            }
        }
    }

    public SecurityManagerService(Context context, boolean onlyCore) {
        this.mContext = context;
        this.mUserManager = UserManagerService.getInstance();
        this.mPackageManagerService = (PackageManagerService) ServiceManager.getService(com.android.server.pm.Settings.ATTR_PACKAGE);
        File systemDir = new File(Environment.getDataDirectory(), "system");
        this.mSettingsFile = new AtomicFile(new File(systemDir, "miui-packages.xml"));
        HandlerThread securityWriteHandlerThread = new HandlerThread("SecurityWriteHandlerThread");
        securityWriteHandlerThread.start();
        Looper looper = securityWriteHandlerThread.getLooper();
        this.mSecurityWriteHandler = new SecurityWriteHandler(looper);
        this.mPackageMonitor = new MyPackageMonitor();
        this.mPackageMonitor.register(this.mContext, this.mSecurityWriteHandler.getLooper(), false);
        readSettings();
        updateXSpaceSettings();
        initForKK();
        this.mWakeTime = 0;
        this.mWakeUpFile = new AtomicFile(new File(systemDir, "miui-wakeuptime.xml"));
        readWakeUpTime();
        checkSystemSelfProtection(onlyCore);
        this.mAccessController = new AccessController(context, looper);
        this.mSettingsObserver = new SettingsObserver(this.mSecurityWriteHandler, context);
        synchronized (this.mUserStateLock) {
            initAccessControlSettingsLocked(getUserStateLocked(0));
        }
        if ((Build.VERSION.SDK_INT == 21 || Build.VERSION.SDK_INT == 22) && ("hennessy".equals(miui.os.Build.DEVICE) || "kenzo".equals(miui.os.Build.DEVICE) || "ido".equals(miui.os.Build.DEVICE) || "aqua".equals(miui.os.Build.DEVICE))) {
            this.mFingerprintNotify = true;
        }
        mAppRunningControlService = new AppRunningControlService(this.mContext);
        this.mAppRunningControlBinder = mAppRunningControlService.asBinder();
        WakePathChecker.getInstance().init(this.mContext);
        resetDefaultBrowser(this.mContext);
        RestrictAppNetManager.init(this.mContext);
        this.mAccessController.updatePasswordTypeForPattern(UserHandle.myUserId());
        InputMethodHelper.init(this.mContext);
        LightsManager lightManager = (LightsManager) LocalServices.getService(LightsManager.class);
        if (lightManager != null) {
            this.mLedLight = lightManager.getLight(4);
        }
        this.mLightOn = this.mContext.getResources().getInteger(R.color.privacy_notification_led);
    }

    /* access modifiers changed from: private */
    public UserState getUserStateLocked(int userHandle) {
        UserState userState = this.mUserStates.get(userHandle);
        if (userState != null) {
            return userState;
        }
        UserState userState2 = new UserState();
        userState2.userHandle = userHandle;
        this.mUserStates.put(userHandle, userState2);
        return userState2;
    }

    private UserState getUserStateOrNullUnLocked(int userHandle) {
        UserState userState;
        int userHandle2 = SecurityManager.getUserHandle(userHandle);
        synchronized (this.mUserStateLock) {
            userState = this.mUserStates.get(userHandle2);
        }
        return userState;
    }

    private void initAccessControlSettingsLocked(UserState userState) {
        updateAccessControlEnabledLocked(userState);
        updateAccessControlLockModeLocked(userState);
        updateAccessControlLockConvenientLocked(userState);
        userState.mAccessControlSettingInit = true;
    }

    /* access modifiers changed from: private */
    public void updateAccessControlEnabledLocked(UserState userState) {
        boolean z = true;
        if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "access_control_lock_enabled", 0, userState.userHandle) != 1) {
            z = false;
        }
        userState.mAccessControlEnabled = z;
    }

    /* access modifiers changed from: private */
    public void updateAccessControlLockModeLocked(UserState userState) {
        userState.mAccessControlLockMode = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "access_control_lock_mode", 1, userState.userHandle);
    }

    /* access modifiers changed from: private */
    public void updateAccessControlLockConvenientLocked(UserState userState) {
        boolean z = true;
        if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "access_control_lock_convenient", 0, userState.userHandle) != 1) {
            z = false;
        }
        userState.mAccessControlLockConvenient = z;
    }

    /* access modifiers changed from: private */
    public void updateAccessMiuiOptUri() {
        if (Build.VERSION.SDK_INT > 22 && AppOpsUtils.isXOptMode()) {
            try {
                if (Build.VERSION.SDK_INT > 28) {
                    Class[] clsArr = {String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, List.class};
                    callObjectMethod(this.mPackageManagerService, "installExistingPackageAsUser", clsArr, "com.google.android.packageinstaller", 0, 16384, 4, null);
                } else if (Build.VERSION.SDK_INT > 25) {
                    Class[] clsArr2 = {String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE};
                    callObjectMethod(this.mPackageManagerService, "installExistingPackageAsUser", clsArr2, "com.google.android.packageinstaller", 0, 16384, 4);
                } else {
                    Class[] clsArr3 = {String.class, Integer.TYPE};
                    callObjectMethod(this.mPackageManagerService, "installExistingPackageAsUser", clsArr3, "com.google.android.packageinstaller", 0);
                }
            } catch (Exception e) {
                Log.e(TAG, "call installExistingPackageAsUser error :" + e.toString(), e);
            }
            if (Build.VERSION.SDK_INT >= 29) {
                try {
                    PackageManagerCompat.setDefaultBrowserPackageNameAsUser(this.mContext.getPackageManager(), "", 0);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
        PackageManagerServiceInjector.checkPkgInstallerOptMode(this.mPackageManagerService);
        PackageManagerServiceInjector.checkGTSSpecAppOptMode(this.mPackageManagerService);
        DefaultPermissionGrantPolicyInjector.revokeAllPermssions(this.mPackageManagerService);
        if (!AppOpsUtils.isXOptMode()) {
            DefaultPermissionGrantPolicyInjector.grantMiuiPackageInstallerPermssions(this.mPackageManagerService);
            setDefaultBrowser();
        }
    }

    public static Object callObjectMethod(Object target, String method, Class<?>[] parameterTypes, Object... values) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method declaredMethod = target.getClass().getDeclaredMethod(method, parameterTypes);
        declaredMethod.setAccessible(true);
        return declaredMethod.invoke(target, values);
    }

    private boolean getAccessControlEnabledLocked(UserState userState) {
        UserState transferUserState = changeUserState(userState);
        if (!transferUserState.mAccessControlSettingInit) {
            initAccessControlSettingsLocked(transferUserState);
        }
        return transferUserState.mAccessControlEnabled;
    }

    private int getAccessControlLockMode(UserState userState) {
        UserState transferUserState = changeUserState(userState);
        if (!transferUserState.mAccessControlSettingInit) {
            initAccessControlSettingsLocked(transferUserState);
        }
        return transferUserState.mAccessControlLockMode;
    }

    private boolean getAccessControlLockConvenient(UserState userState) {
        UserState transferUserState = changeUserState(userState);
        if (!transferUserState.mAccessControlSettingInit) {
            initAccessControlSettingsLocked(transferUserState);
        }
        return transferUserState.mAccessControlLockConvenient;
    }

    private static int compareSignatures(Signature[] s1, Signature[] s2) {
        if (s1 == null || s2 == null) {
            return -3;
        }
        HashSet<Signature> set1 = new HashSet<>();
        for (Signature sig : s1) {
            set1.add(sig);
        }
        HashSet<Signature> set2 = new HashSet<>();
        for (Signature sig2 : s2) {
            set2.add(sig2);
        }
        if (set1.equals(set2)) {
            return 0;
        }
        return -3;
    }

    /* access modifiers changed from: private */
    public void enforceAppSignature(Signature[] validSignatures, String pkgName, boolean checkEnabled) {
        if (!checkAppSignature(validSignatures, pkgName, checkEnabled)) {
            throw new RuntimeException("System error: connot find system app : " + pkgName);
        }
    }

    private boolean checkAppSignature(Signature[] validSignatures, String pkgName, boolean checkEnabled) {
        try {
            PackageInfo packageInfo = this.mContext.getPackageManager().getPackageInfo(pkgName, 64);
            ApplicationInfo aInfo = packageInfo.applicationInfo;
            if (checkEnabled && !aInfo.enabled) {
                Log.e(TAG, "System error: " + pkgName + "disabled");
                return false;
            } else if (compareSignatures(validSignatures, packageInfo.signatures) == 0) {
                return true;
            } else {
                Log.e(TAG, pkgName + " signature not match!");
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    private void checkSystemSelfProtection(final boolean onlyCore) {
        new Thread() {
            public void run() {
                Process.setThreadPriority(10);
                if (!miui.os.Build.IS_TABLET && !onlyCore) {
                    long currentTimeMillis = System.currentTimeMillis();
                    PackageManager pm = SecurityManagerService.this.mContext.getPackageManager();
                    try {
                        Signature[] platformSignature = pm.getPackageInfo(PackageManagerService.PLATFORM_PACKAGE_NAME, 64).signatures;
                        if (SecurityManagerService.this.nativeIsReleased()) {
                            SecurityManagerService.this.enforcePlatformSignature(platformSignature);
                        }
                        ArrayList<String> checkApps = new ArrayList<>();
                        checkApps.add("com.lbe.security.miui");
                        checkApps.add("com.android.updater");
                        checkApps.add("com.miui.securitycenter");
                        checkApps.add("com.xiaomi.finddevice");
                        checkApps.add("com.miui.home");
                        checkApps.add("com.miui.guardprovider");
                        checkApps.add(AccessController.PACKAGE_GALLERY);
                        if (!miui.os.Build.IS_INTERNATIONAL_BUILD && !miui.os.Build.IS_CM_CUSTOMIZATION && !miui.os.Build.IS_CM_CUSTOMIZATION_TEST) {
                            checkApps.add("com.miui.player");
                            checkApps.add("com.android.browser");
                            checkApps.add("com.xiaomi.market");
                        }
                        Iterator<String> it = checkApps.iterator();
                        while (it.hasNext()) {
                            SecurityManagerService.this.checkEnabled(pm, it.next());
                        }
                        SecurityManagerService.this.enforceAppSignature(platformSignature, "com.android.updater", true);
                        int i = 0;
                        SecurityManagerService.this.enforceAppSignature(platformSignature, "com.miui.securitycenter", false);
                        SecurityManagerService.this.enforceAppSignature(platformSignature, "com.xiaomi.finddevice", true);
                        if (!miui.os.Build.IS_INTERNATIONAL_BUILD && !miui.os.Build.IS_CM_CUSTOMIZATION && !miui.os.Build.IS_CM_CUSTOMIZATION_TEST) {
                            SecurityManagerService.this.enforceAppSignature(platformSignature, "com.xiaomi.market", false);
                        }
                        boolean oldmanMode = SecurityManagerService.this.isOldmanMode();
                        if (!SecurityManagerService.this.nativeIsReleased() || oldmanMode) {
                            Log.d(SecurityManagerService.TAG, "nativeIsReleased not set or " + oldmanMode);
                        } else {
                            Log.d(SecurityManagerService.TAG, "nativeIsReleased set and " + oldmanMode);
                            SecurityManagerService securityManagerService = SecurityManagerService.this;
                            if (securityManagerService.checkSysAppCrack()) {
                                i = 1;
                            }
                            int unused = securityManagerService.mSysAppCracked = i;
                        }
                        System.currentTimeMillis();
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                        throw new RuntimeException("System error: cannot find android package.");
                    }
                }
            }
        }.start();
    }

    /* access modifiers changed from: private */
    public boolean isOldmanMode() {
        return miui.os.Build.getUserMode() == 1;
    }

    /* access modifiers changed from: private */
    public boolean checkSysAppCrack() {
        ArrayList<AppItem> appsTobeChecked = new ArrayList<>();
        appsTobeChecked.add(new AppItem("com.miui.home", SignatureConstants.PLATFORM, false));
        appsTobeChecked.add(new AppItem(AccessController.PACKAGE_GALLERY, SignatureConstants.PLATFORM, false));
        if (!miui.os.Build.IS_INTERNATIONAL_BUILD && !miui.os.Build.IS_CM_CUSTOMIZATION && !miui.os.Build.IS_CM_CUSTOMIZATION_TEST) {
            appsTobeChecked.add(new AppItem("com.miui.player", SignatureConstants.PLATFORM, false));
            appsTobeChecked.add(new AppItem("com.android.browser", SignatureConstants.PLATFORM, false));
            appsTobeChecked.add(new AppItem("com.miui.video", SignatureConstants.PLATFORM, false));
        }
        Iterator<AppItem> it = appsTobeChecked.iterator();
        while (it.hasNext()) {
            AppItem appItem = it.next();
            if (!checkAppSignature(new Signature[]{new Signature(appItem.mSignature)}, appItem.mPkg, appItem.mCheckEnable)) {
                Log.e(TAG, "checkAppSignature failed at " + appItem.mPkg);
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void enforcePlatformSignature(Signature[] signatures) {
        Signature platformSig = new Signature(SignatureConstants.PLATFORM);
        int length = signatures.length;
        int i = 0;
        while (i < length) {
            if (!platformSig.equals(signatures[i])) {
                i++;
            } else {
                return;
            }
        }
        throw new RuntimeException("System error: My heart is broken");
    }

    /* access modifiers changed from: private */
    public void checkEnabled(PackageManager pm, String pkg) {
        SecurityManagerCompat.checkAppHidden(pm, pkg, UserHandle.OWNER);
        try {
            int state = pm.getApplicationEnabledSetting(pkg);
            if (state != 0 && state != 1) {
                pm.setApplicationEnabledSetting(pkg, 0, 0);
            }
        } catch (IllegalArgumentException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void initForKK() {
        if (Build.VERSION.SDK_INT >= 19) {
            this.mAom = (AppOpsManager) this.mContext.getSystemService("appops");
            this.mHandlerThread = new HandlerThread(TAG);
            this.mHandlerThread.start();
            this.mHandler = new Handler(this.mHandlerThread.getLooper());
            this.mSecuritySmsHandler = new SecuritySmsHandler(this.mContext, this.mHandler);
        }
    }

    private void resetDefaultBrowser(final Context context) {
        if (!miui.os.Build.IS_INTERNATIONAL_BUILD && !AppOpsUtils.isXOptMode() && Build.VERSION.SDK_INT >= 24) {
            this.mHandler.postDelayed(new Runnable() {
                public void run() {
                    ContentResolver cr = context.getContentResolver();
                    try {
                        SecurityManagerService.this.checkIntentFilterVerifications();
                        if (Build.VERSION.SDK_INT >= 29) {
                            SecurityManagerService.this.setDefaultBrowser();
                        } else if (Settings.Secure.getInt(cr, SecurityManagerService.DEF_BROWSER_COUNT, -1) == -1) {
                            PackageManager pm = context.getPackageManager();
                            String defaultBrowser = PackageManagerCompat.getDefaultBrowserPackageNameAsUser(pm, 0);
                            if (TextUtils.isEmpty(defaultBrowser) || SecurityManagerService.PKG_BROWSER.equals(defaultBrowser)) {
                                Settings.Secure.putInt(cr, SecurityManagerService.DEF_BROWSER_COUNT, 1);
                                return;
                            }
                            PackageManagerCompat.setDefaultBrowserPackageNameAsUser(pm, "", 0);
                            Settings.Secure.putInt(cr, SecurityManagerService.DEF_BROWSER_COUNT, 10);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 60000);
        }
    }

    /* access modifiers changed from: private */
    public void setDefaultBrowser() {
        PackageManager pm = this.mContext.getPackageManager();
        try {
            if (TextUtils.isEmpty(PackageManagerCompat.getDefaultBrowserPackageNameAsUser(pm, 0))) {
                PackageManagerCompat.setDefaultBrowserPackageNameAsUser(pm, PKG_BROWSER, 0);
            }
        } catch (Exception e) {
            Log.e(TAG, "setDefaultBrowser", e);
        }
    }

    /* access modifiers changed from: private */
    public void checkIntentFilterVerifications() {
        List<PackageInfo> applications;
        String str;
        int i;
        boolean z;
        List<PackageInfo> applications2;
        String str2 = "android.intent.action.VIEW";
        PackageManager pm = this.mContext.getPackageManager();
        try {
            List<PackageInfo> applications3 = pm.getInstalledPackages(8192);
            Intent browserIntent = new Intent().setAction(str2).addCategory("android.intent.category.BROWSABLE").setData(Uri.parse("http://"));
            Intent httpIntent = new Intent().setAction(str2).setData(Uri.parse("http://www.xiaomi.com"));
            Intent httpsIntent = new Intent().setAction(str2).setData(Uri.parse("https://www.xiaomi.com"));
            int i2 = 1;
            Set<String> browsers = queryIntentPackages(pm, browserIntent, true, 0);
            Set<String> httpPackages = queryIntentPackages(pm, httpIntent, false, 0);
            httpPackages.addAll(queryIntentPackages(pm, httpsIntent, false, 0));
            ArraySet<String> rejectPks = new ArraySet<>();
            for (PackageInfo info : applications3) {
                if ((info.applicationInfo.flags & i2) == 0) {
                    String pkg = info.applicationInfo.packageName;
                    if (!browsers.contains(pkg)) {
                        if (httpPackages.contains(pkg)) {
                            List<IntentFilter> filters = pm.getAllIntentFilters(pkg);
                            boolean add = false;
                            if (filters == null || filters.size() <= 0) {
                                str = str2;
                                applications = applications3;
                            } else {
                                for (IntentFilter filter : filters) {
                                    if (filter.hasAction(str2)) {
                                        String str3 = str2;
                                        if (filter.hasDataScheme("http") || filter.hasDataScheme("https")) {
                                            ArrayList<String> hostList = filter.getHostsList();
                                            if (hostList.size() != 0) {
                                                ArrayList<String> arrayList = hostList;
                                                if (filter.getHostsList().contains("*")) {
                                                }
                                            } else {
                                                ArrayList<String> arrayList2 = hostList;
                                            }
                                            int dataPathsCount = filter.countDataPaths();
                                            if (dataPathsCount > 0) {
                                                int i3 = 0;
                                                while (i3 < dataPathsCount) {
                                                    int dataPathsCount2 = dataPathsCount;
                                                    List<PackageInfo> applications4 = applications3;
                                                    if (".*".equals(filter.getDataPath(i3).getPath())) {
                                                        add = true;
                                                    }
                                                    i3++;
                                                    dataPathsCount = dataPathsCount2;
                                                    applications3 = applications4;
                                                }
                                                applications2 = applications3;
                                            } else {
                                                applications2 = applications3;
                                                add = true;
                                            }
                                            str2 = str3;
                                            applications3 = applications2;
                                        }
                                        applications2 = applications3;
                                        str2 = str3;
                                        applications3 = applications2;
                                    }
                                }
                                str = str2;
                                applications = applications3;
                            }
                            if (add) {
                                z = false;
                                int status = pm.getIntentVerificationStatusAsUser(pkg, 0);
                                if (status != 0) {
                                    i = 1;
                                    if (status == 1) {
                                    }
                                } else {
                                    i = 1;
                                }
                                rejectPks.add(pkg);
                            } else {
                                z = false;
                                i = 1;
                            }
                            boolean z2 = z;
                            i2 = i;
                            str2 = str;
                            applications3 = applications;
                        }
                    }
                }
            }
            Iterator<String> it = rejectPks.iterator();
            while (it.hasNext()) {
                pm.updateIntentVerificationStatusAsUser(it.next(), 3, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Set<String> queryIntentPackages(PackageManager pm, Intent intent, boolean allweb, int userId) {
        List<ResolveInfo> list = pm.queryIntentActivitiesAsUser(intent, 131072, userId);
        int count = list.size();
        Set<String> packages = new ArraySet<>();
        for (int i = 0; i < count; i++) {
            ResolveInfo info = list.get(i);
            if (info.activityInfo != null && (!allweb || info.handleAllWebDataURI)) {
                String packageName = info.activityInfo.packageName;
                if (!packages.contains(packageName)) {
                    packages.add(packageName);
                }
            }
        }
        return packages;
    }

    public void killNativePackageProcesses(int uid, String pkgName) {
        checkPermission();
        if (uid >= 10000) {
            nativeKillPackageProcesses(uid, pkgName);
        }
    }

    public String getPackageNameByPid(int pid) {
        int callingUid = Binder.getCallingUid();
        if (!(callingUid == 0 || UserHandle.getAppId(callingUid) == 1000)) {
            try {
                PackageManager pm = this.mContext.getPackageManager();
                String[] packages = pm.getPackagesForUid(callingUid);
                if (packages != null) {
                    if (packages.length != 0) {
                        if ((pm.getApplicationInfo(packages[0], 0).flags & 1) == 0) {
                            return "";
                        }
                    }
                }
                return "";
            } catch (Exception e) {
                return "";
            }
        }
        return ExtraActivityManagerService.getPackageNameByPid(pid);
    }

    public boolean checkSmsBlocked(Intent intent) {
        return this.mSecuritySmsHandler.checkSmsBlocked(intent);
    }

    public boolean startInterceptSmsBySender(String pkgName, String sender, int count) {
        return this.mSecuritySmsHandler.startInterceptSmsBySender(pkgName, sender, count);
    }

    public boolean stopInterceptSmsBySender() {
        return this.mSecuritySmsHandler.stopInterceptSmsBySender();
    }

    public void addAccessControlPass(String packageName) {
        addAccessControlPassForUser(packageName, UserHandle.getCallingUserId());
    }

    public void addAccessControlPassForUser(String packageName, int userId) {
        checkPermission();
        synchronized (this.mUserStateLock) {
            UserState userState = getUserStateLocked(userId);
            if (getAccessControlLockMode(userState) == 2) {
                userState.mAccessControlLastCheck.put(packageName, Long.valueOf(SystemClock.elapsedRealtime()));
            }
            userState.mAccessControlPassPackages.add(packageName);
        }
    }

    public void removeAccessControlPass(String packageName) {
        checkPermission();
        removeAccessControlPassAsUser(packageName, UserHandle.getCallingUserId());
    }

    public boolean checkAccessControlPass(String packageName, Intent intent) {
        return checkAccessControlPassLocked(packageName, intent, UserHandle.getCallingUserId());
    }

    public boolean checkAccessControlPassAsUser(String packageName, Intent intent, int userId) {
        return checkAccessControlPassLocked(packageName, intent, userId);
    }

    public boolean getApplicationAccessControlEnabledAsUser(String packageName, int userId) {
        return getApplicationAccessControlEnabledLocked(packageName, userId);
    }

    public boolean getApplicationMaskNotificationEnabledAsUser(String packageName, int userId) {
        return getApplicationMaskNotificationEnabledLocked(packageName, userId);
    }

    public boolean checkGameBoosterAntimsgPassAsUser(String packageName, Intent intent, int userId) {
        return !this.mAccessController.filterIntentLocked(false, packageName, intent);
    }

    public void setGameBoosterIBinder(IBinder gameBooster, int userId, boolean isGameMode) {
        checkPermission();
        synchronized (this.mUserStateLock) {
            UserState userState = getUserStateLocked(SecurityManager.getUserHandle(userId));
            try {
                if (userState.gameBoosterServiceDeath == null) {
                    userState.gameBoosterServiceDeath = new GameBoosterServiceDeath(userState, gameBooster);
                    gameBooster.linkToDeath(userState.gameBoosterServiceDeath, 0);
                } else if (gameBooster != userState.gameBoosterServiceDeath.mGameBoosterService) {
                    userState.gameBoosterServiceDeath.mGameBoosterService.unlinkToDeath(userState.gameBoosterServiceDeath, 0);
                    userState.gameBoosterServiceDeath = new GameBoosterServiceDeath(userState, gameBooster);
                    gameBooster.linkToDeath(userState.gameBoosterServiceDeath, 0);
                } else {
                    userState.mIsGameMode = isGameMode;
                }
            } catch (Exception e) {
                Log.e(TAG, "setGameBoosterIBinder", e);
            }
        }
    }

    public boolean getGameMode(int userId) {
        boolean z;
        synchronized (this.mUserStateLock) {
            z = getUserStateLocked(SecurityManager.getUserHandle(userId)).mIsGameMode;
        }
        return z;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:39:0x009c, code lost:
        return r9;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean checkAccessControlPassLocked(java.lang.String r18, android.content.Intent r19, int r20) {
        /*
            r17 = this;
            r1 = r17
            r2 = r18
            r3 = r19
            java.lang.Object r4 = r1.mUserStateLock
            monitor-enter(r4)
            r5 = r20
            com.miui.server.SecurityManagerService$UserState r0 = r1.getUserStateLocked(r5)     // Catch:{ all -> 0x009d }
            java.util.HashMap<java.lang.String, com.miui.server.SecurityManagerService$PackageSetting> r6 = r0.mPackages     // Catch:{ all -> 0x009d }
            com.miui.server.SecurityManagerService$PackageSetting r6 = r1.getPackageSetting(r6, r2)     // Catch:{ all -> 0x009d }
            boolean r7 = r6.accessControl     // Catch:{ all -> 0x009d }
            r8 = 1
            if (r7 != 0) goto L_0x001c
            monitor-exit(r4)     // Catch:{ all -> 0x009d }
            return r8
        L_0x001c:
            int r7 = r1.getAccessControlLockMode(r0)     // Catch:{ all -> 0x009d }
            java.util.HashSet<java.lang.String> r9 = r0.mAccessControlPassPackages     // Catch:{ all -> 0x009d }
            boolean r9 = r9.contains(r2)     // Catch:{ all -> 0x009d }
            if (r9 == 0) goto L_0x006c
            r10 = 2
            if (r7 != r10) goto L_0x006c
            android.util.ArrayMap<java.lang.String, java.lang.Long> r10 = r0.mAccessControlLastCheck     // Catch:{ all -> 0x009d }
            java.lang.Object r10 = r10.get(r2)     // Catch:{ all -> 0x009d }
            java.lang.Long r10 = (java.lang.Long) r10     // Catch:{ all -> 0x009d }
            if (r10 == 0) goto L_0x0047
            long r11 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x009d }
            long r13 = r10.longValue()     // Catch:{ all -> 0x009d }
            long r13 = r11 - r13
            r15 = 60000(0xea60, double:2.9644E-319)
            int r13 = (r13 > r15 ? 1 : (r13 == r15 ? 0 : -1))
            if (r13 <= 0) goto L_0x0047
            r9 = 0
        L_0x0047:
            if (r9 == 0) goto L_0x006c
            int r11 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x009d }
            r12 = 24
            if (r11 < r12) goto L_0x005f
            java.lang.String r11 = "com.android.systemui"
            int r12 = android.os.Binder.getCallingPid()     // Catch:{ all -> 0x009d }
            java.lang.String r12 = com.android.server.am.ExtraActivityManagerService.getPackageNameByPid(r12)     // Catch:{ all -> 0x009d }
            boolean r11 = r11.equals(r12)     // Catch:{ all -> 0x009d }
            if (r11 != 0) goto L_0x006c
        L_0x005f:
            android.util.ArrayMap<java.lang.String, java.lang.Long> r11 = r0.mAccessControlLastCheck     // Catch:{ all -> 0x009d }
            long r12 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x009d }
            java.lang.Long r12 = java.lang.Long.valueOf(r12)     // Catch:{ all -> 0x009d }
            r11.put(r2, r12)     // Catch:{ all -> 0x009d }
        L_0x006c:
            if (r9 != 0) goto L_0x007d
            if (r7 != r8) goto L_0x007d
            boolean r10 = r1.getAccessControlLockConvenient(r0)     // Catch:{ all -> 0x009d }
            if (r10 == 0) goto L_0x007d
            boolean r10 = r1.isPackageAccessControlPass(r0)     // Catch:{ all -> 0x009d }
            if (r10 == 0) goto L_0x007d
            r9 = 1
        L_0x007d:
            if (r9 != 0) goto L_0x0090
            com.miui.server.AccessController r10 = r1.mAccessController     // Catch:{ all -> 0x009d }
            int r11 = android.os.Binder.getCallingPid()     // Catch:{ all -> 0x009d }
            java.lang.String r11 = com.android.server.am.ExtraActivityManagerService.getPackageNameByPid(r11)     // Catch:{ all -> 0x009d }
            boolean r10 = r10.skipActivity(r3, r11)     // Catch:{ all -> 0x009d }
            if (r10 == 0) goto L_0x0090
            r9 = 1
        L_0x0090:
            if (r9 != 0) goto L_0x009b
            com.miui.server.AccessController r10 = r1.mAccessController     // Catch:{ all -> 0x009d }
            boolean r8 = r10.filterIntentLocked(r8, r2, r3)     // Catch:{ all -> 0x009d }
            if (r8 == 0) goto L_0x009b
            r9 = 1
        L_0x009b:
            monitor-exit(r4)     // Catch:{ all -> 0x009d }
            return r9
        L_0x009d:
            r0 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x009d }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.server.SecurityManagerService.checkAccessControlPassLocked(java.lang.String, android.content.Intent, int):boolean");
    }

    public boolean getApplicationAccessControlEnabled(String packageName) {
        return getApplicationAccessControlEnabledLocked(packageName, UserHandle.getCallingUserId());
    }

    private boolean getApplicationAccessControlEnabledLocked(String packageName, int userId) {
        boolean z;
        synchronized (this.mUserStateLock) {
            try {
                z = getPackageSetting(getUserStateLocked(userId).mPackages, packageName).accessControl;
            } catch (Exception e) {
                return false;
            }
        }
        return z;
    }

    private boolean getApplicationMaskNotificationEnabledLocked(String packageName, int userId) {
        boolean z;
        synchronized (this.mUserStateLock) {
            try {
                z = getPackageSetting(getUserStateLocked(userId).mPackages, packageName).maskNotification;
            } catch (Exception e) {
                return false;
            }
        }
        return z;
    }

    public void setApplicationAccessControlEnabled(String packageName, boolean enabled) {
        setApplicationAccessControlEnabledForUser(packageName, enabled, UserHandle.getCallingUserId());
    }

    public void setApplicationAccessControlEnabledForUser(String packageName, boolean enabled, int userId) {
        checkPermission();
        synchronized (this.mUserStateLock) {
            getPackageSetting(getUserStateLocked(userId).mPackages, packageName).accessControl = enabled;
            scheduleWriteSettings();
        }
    }

    public boolean getAppDarkMode(String packageName) {
        boolean appDarkModeForUser;
        int userId = UserHandle.getCallingUserId();
        synchronized (this) {
            appDarkModeForUser = getAppDarkModeForUser(packageName, userId);
        }
        return appDarkModeForUser;
    }

    public boolean getAppDarkModeForUser(String packageName, int userId) {
        try {
            return getPackageSetting(getUserStateLocked(userId).mPackages, packageName).isDarkModeChecked;
        } catch (Exception e) {
            return false;
        }
    }

    public void setAppDarkModeForUser(String packageName, boolean enabled, int userId) {
        synchronized (this) {
            getPackageSetting(getUserStateLocked(userId).mPackages, packageName).isDarkModeChecked = enabled;
            scheduleWriteSettings();
            IBinder service = ServiceManager.getService("uimode");
            if (service instanceof MiuiUiModeManagerStub) {
                ((MiuiUiModeManagerStub) service).setAppDarkModeEnable(packageName, enabled);
            }
        }
    }

    public void setApplicationMaskNotificationEnabledForUser(String packageName, boolean enabled, int userId) {
        checkPermission();
        synchronized (this.mUserStateLock) {
            getPackageSetting(getUserStateLocked(userId).mPackages, packageName).maskNotification = enabled;
            scheduleWriteSettings();
        }
    }

    public void saveIcon(String fileName, Bitmap icon) {
        saveIconInner(fileName, icon);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0078, code lost:
        if (r3 != -1) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x007a, code lost:
        if (r9 == null) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x007c, code lost:
        if (r7 != false) goto L_0x00aa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:?, code lost:
        r0 = miui.security.SecurityManager.getCheckAccessIntent(true, r4, (android.content.Intent) null, -1, true, r6.intValue(), (android.os.Bundle) null);
        r0.putExtra("miui.KEYGUARD_LOCKED", true);
        miui.security.SecurityManagerCompat.startActvityAsUser(r1.mContext, (android.app.IApplicationThread) null, r5, (java.lang.String) null, r0, r6.intValue());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x00a1, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x00a2, code lost:
        android.util.Log.e(TAG, "removeAccessControlPassAsUser startActvityAsUser error ", r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x00ac, code lost:
        if (r1.mFingerprintNotify == false) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x00b4, code lost:
        if ("com.miui.securitycenter".equals(r4) == false) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x00b6, code lost:
        r0 = new android.content.Intent("miui.intent.action.APP_LOCK_CLEAR_STATE");
        r0.setPackage("com.miui.securitycenter");
        r1.mContext.sendBroadcast(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void removeAccessControlPassAsUser(java.lang.String r19, int r20) {
        /*
            r18 = this;
            r1 = r18
            r2 = r19
            r3 = r20
            r18.checkPermission()
            r4 = 0
            r5 = 0
            r0 = 0
            java.lang.Integer r6 = java.lang.Integer.valueOf(r0)
            r7 = 0
            r0 = 0
            r8 = -1
            if (r3 != r8) goto L_0x001b
            java.util.HashMap r0 = com.android.server.am.ExtraActivityManagerService.getTopRunningActivityInfo()
            r9 = r0
            goto L_0x001c
        L_0x001b:
            r9 = r0
        L_0x001c:
            java.lang.Object r10 = r1.mUserStateLock
            monitor-enter(r10)
            if (r3 != r8) goto L_0x0070
            android.util.SparseArray<com.miui.server.SecurityManagerService$UserState> r0 = r1.mUserStates     // Catch:{ all -> 0x00c8 }
            int r0 = r0.size()     // Catch:{ all -> 0x00c8 }
            r11 = 0
        L_0x0028:
            if (r11 >= r0) goto L_0x0038
            android.util.SparseArray<com.miui.server.SecurityManagerService$UserState> r12 = r1.mUserStates     // Catch:{ all -> 0x00c8 }
            java.lang.Object r12 = r12.valueAt(r11)     // Catch:{ all -> 0x00c8 }
            com.miui.server.SecurityManagerService$UserState r12 = (com.miui.server.SecurityManagerService.UserState) r12     // Catch:{ all -> 0x00c8 }
            r1.removeAccessControlPassLocked(r12, r2)     // Catch:{ all -> 0x00c8 }
            int r11 = r11 + 1
            goto L_0x0028
        L_0x0038:
            int r11 = com.android.server.am.ExtraActivityManagerService.getCurrentUserId()     // Catch:{ all -> 0x00c8 }
            com.miui.server.SecurityManagerService$UserState r12 = r1.getUserStateLocked(r11)     // Catch:{ all -> 0x00c8 }
            boolean r13 = r1.getAccessControlEnabledLocked(r12)     // Catch:{ all -> 0x00c8 }
            if (r13 != 0) goto L_0x0048
            monitor-exit(r10)     // Catch:{ all -> 0x00c8 }
            return
        L_0x0048:
            if (r9 == 0) goto L_0x006f
            java.lang.String r14 = "packageName"
            java.lang.Object r14 = r9.get(r14)     // Catch:{ all -> 0x00c8 }
            java.lang.String r14 = (java.lang.String) r14     // Catch:{ all -> 0x00c8 }
            r4 = r14
            java.lang.String r14 = "token"
            java.lang.Object r14 = r9.get(r14)     // Catch:{ all -> 0x00c8 }
            android.os.IBinder r14 = (android.os.IBinder) r14     // Catch:{ all -> 0x00c8 }
            r5 = r14
            java.lang.String r14 = "userId"
            java.lang.Object r14 = r9.get(r14)     // Catch:{ all -> 0x00c8 }
            java.lang.Integer r14 = (java.lang.Integer) r14     // Catch:{ all -> 0x00c8 }
            r6 = r14
            r14 = 0
            int r15 = r6.intValue()     // Catch:{ all -> 0x00c8 }
            boolean r14 = r1.checkAccessControlPassLocked(r4, r14, r15)     // Catch:{ all -> 0x00c8 }
            r7 = r14
        L_0x006f:
            goto L_0x0077
        L_0x0070:
            com.miui.server.SecurityManagerService$UserState r0 = r1.getUserStateLocked(r3)     // Catch:{ all -> 0x00c8 }
            r1.removeAccessControlPassLocked(r0, r2)     // Catch:{ all -> 0x00c8 }
        L_0x0077:
            monitor-exit(r10)     // Catch:{ all -> 0x00c8 }
            if (r3 != r8) goto L_0x00c7
            if (r9 == 0) goto L_0x00c7
            if (r7 != 0) goto L_0x00aa
            r11 = 1
            r13 = 0
            r14 = -1
            r15 = 1
            int r16 = r6.intValue()     // Catch:{ Exception -> 0x00a1 }
            r17 = 0
            r12 = r4
            android.content.Intent r0 = miui.security.SecurityManager.getCheckAccessIntent(r11, r12, r13, r14, r15, r16, r17)     // Catch:{ Exception -> 0x00a1 }
            java.lang.String r8 = "miui.KEYGUARD_LOCKED"
            r10 = 1
            r0.putExtra(r8, r10)     // Catch:{ Exception -> 0x00a1 }
            android.content.Context r11 = r1.mContext     // Catch:{ Exception -> 0x00a1 }
            r12 = 0
            r14 = 0
            int r16 = r6.intValue()     // Catch:{ Exception -> 0x00a1 }
            r13 = r5
            r15 = r0
            miui.security.SecurityManagerCompat.startActvityAsUser(r11, r12, r13, r14, r15, r16)     // Catch:{ Exception -> 0x00a1 }
            goto L_0x00a9
        L_0x00a1:
            r0 = move-exception
            java.lang.String r8 = "SecurityManagerService"
            java.lang.String r10 = "removeAccessControlPassAsUser startActvityAsUser error "
            android.util.Log.e(r8, r10, r0)
        L_0x00a9:
            goto L_0x00c7
        L_0x00aa:
            boolean r0 = r1.mFingerprintNotify
            if (r0 == 0) goto L_0x00c7
            java.lang.String r0 = "com.miui.securitycenter"
            boolean r0 = r0.equals(r4)
            if (r0 == 0) goto L_0x00c7
            android.content.Intent r0 = new android.content.Intent
            java.lang.String r8 = "miui.intent.action.APP_LOCK_CLEAR_STATE"
            r0.<init>(r8)
            java.lang.String r8 = "com.miui.securitycenter"
            r0.setPackage(r8)
            android.content.Context r8 = r1.mContext
            r8.sendBroadcast(r0)
        L_0x00c7:
            return
        L_0x00c8:
            r0 = move-exception
            monitor-exit(r10)     // Catch:{ all -> 0x00c8 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.server.SecurityManagerService.removeAccessControlPassAsUser(java.lang.String, int):void");
    }

    private void removeAccessControlPassLocked(UserState userState, String packageName) {
        if ("*".equals(packageName)) {
            userState.mAccessControlPassPackages.clear();
            userState.mAccessControlLastCheck.clear();
            return;
        }
        userState.mAccessControlPassPackages.remove(packageName);
    }

    public void setAccessControlPassword(String passwordType, String password, int userId) {
        checkPermission();
        this.mAccessController.setAccessControlPassword(passwordType, password, SecurityManager.getUserHandle(userId));
    }

    public boolean checkAccessControlPassword(String passwordType, String password, int userId) {
        checkPermission();
        return this.mAccessController.checkAccessControlPassword(passwordType, password, SecurityManager.getUserHandle(userId));
    }

    public boolean haveAccessControlPassword(int userId) {
        return this.mAccessController.haveAccessControlPassword(SecurityManager.getUserHandle(userId));
    }

    public String getAccessControlPasswordType(int userId) {
        checkPermission();
        return this.mAccessController.getAccessControlPasswordType(SecurityManager.getUserHandle(userId));
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x0010, code lost:
        r1 = r0.get(r0.size() - 2);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean needFinishAccessControl(android.os.IBinder r7) throws android.os.RemoteException {
        /*
            r6 = this;
            r6.checkPermission()
            java.util.ArrayList r0 = com.android.server.am.ExtraActivityManagerService.getTaskIntentForToken(r7)
            if (r0 == 0) goto L_0x002d
            int r1 = r0.size()
            r2 = 1
            if (r1 <= r2) goto L_0x002d
            int r1 = r0.size()
            int r1 = r1 + -2
            java.lang.Object r1 = r0.get(r1)
            android.content.Intent r1 = (android.content.Intent) r1
            android.content.ComponentName r3 = r1.getComponent()
            if (r3 == 0) goto L_0x002d
            com.miui.server.AccessController r4 = r6.mAccessController
            java.lang.String r5 = r3.getPackageName()
            boolean r2 = r4.filterIntentLocked(r2, r5, r1)
            return r2
        L_0x002d:
            r1 = 0
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.server.SecurityManagerService.needFinishAccessControl(android.os.IBinder):boolean");
    }

    public void finishAccessControl(String packageName, int userId) throws RemoteException {
        checkPermission();
        if (packageName != null) {
            synchronized (this.mUserStateLock) {
                getUserStateLocked(userId).mAccessControlCanceled.add(packageName);
                Message msg = this.mSecurityWriteHandler.obtainMessage(4);
                msg.arg1 = userId;
                msg.obj = packageName;
                this.mSecurityWriteHandler.sendMessageDelayed(msg, 500);
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:31:0x006a, code lost:
        return 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x00a4, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x00d9, code lost:
        return r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int activityResume(android.content.Intent r23) {
        /*
            r22 = this;
            r1 = r22
            r2 = r23
            r0 = 0
            if (r2 != 0) goto L_0x0008
            return r0
        L_0x0008:
            android.content.ComponentName r3 = r23.getComponent()
            if (r3 != 0) goto L_0x000f
            return r0
        L_0x000f:
            java.lang.String r4 = r3.getPackageName()
            if (r4 != 0) goto L_0x0016
            return r0
        L_0x0016:
            int r5 = android.os.Binder.getCallingUid()
            int r6 = android.os.UserHandle.getUserId(r5)
            java.lang.Object r7 = r1.mUserStateLock
            monitor-enter(r7)
            com.miui.server.SecurityManagerService$UserState r8 = r1.getUserStateLocked(r6)     // Catch:{ all -> 0x00da }
            boolean r9 = r1.getAccessControlEnabledLocked(r8)     // Catch:{ all -> 0x00da }
            if (r9 != 0) goto L_0x002d
            monitor-exit(r7)     // Catch:{ all -> 0x00da }
            return r0
        L_0x002d:
            com.android.server.pm.PackageManagerService r10 = r1.mPackageManagerService     // Catch:{ all -> 0x00da }
            int r10 = com.android.server.pm.PackageManagerServiceCompat.getPackageUid(r10, r4, r6)     // Catch:{ all -> 0x00da }
            if (r5 == r10) goto L_0x0037
            monitor-exit(r7)     // Catch:{ all -> 0x00da }
            return r0
        L_0x0037:
            r0 = 1
            int r11 = r1.getAccessControlLockMode(r8)     // Catch:{ all -> 0x00da }
            java.lang.String r12 = r8.mLastResumePackage     // Catch:{ all -> 0x00da }
            r8.mLastResumePackage = r4     // Catch:{ all -> 0x00da }
            java.util.HashSet<java.lang.String> r13 = r8.mAccessControlPassPackages     // Catch:{ all -> 0x00da }
            r14 = 2
            if (r11 != r14) goto L_0x005a
            if (r12 == 0) goto L_0x005a
            boolean r15 = r13.contains(r12)     // Catch:{ all -> 0x00da }
            if (r15 == 0) goto L_0x005a
            android.util.ArrayMap<java.lang.String, java.lang.Long> r15 = r8.mAccessControlLastCheck     // Catch:{ all -> 0x00da }
            long r16 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x00da }
            java.lang.Long r14 = java.lang.Long.valueOf(r16)     // Catch:{ all -> 0x00da }
            r15.put(r12, r14)     // Catch:{ all -> 0x00da }
        L_0x005a:
            java.util.HashMap<java.lang.String, com.miui.server.SecurityManagerService$PackageSetting> r14 = r8.mPackages     // Catch:{ all -> 0x00da }
            com.miui.server.SecurityManagerService$PackageSetting r14 = r1.getPackageSetting(r14, r4)     // Catch:{ all -> 0x00da }
            boolean r15 = r14.accessControl     // Catch:{ all -> 0x00da }
            if (r15 != 0) goto L_0x006b
            if (r11 != 0) goto L_0x0069
            r1.clearPassPackages(r6)     // Catch:{ all -> 0x00da }
        L_0x0069:
            monitor-exit(r7)     // Catch:{ all -> 0x00da }
            return r0
        L_0x006b:
            r15 = 2
            r0 = r0 | r15
            boolean r16 = r13.contains(r4)     // Catch:{ all -> 0x00da }
            if (r16 == 0) goto L_0x00a5
            if (r11 != r15) goto L_0x0099
            android.util.ArrayMap<java.lang.String, java.lang.Long> r15 = r8.mAccessControlLastCheck     // Catch:{ all -> 0x00da }
            java.lang.Object r15 = r15.get(r4)     // Catch:{ all -> 0x00da }
            java.lang.Long r15 = (java.lang.Long) r15     // Catch:{ all -> 0x00da }
            if (r15 == 0) goto L_0x0094
            long r16 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x00da }
            long r18 = r15.longValue()     // Catch:{ all -> 0x00da }
            long r18 = r16 - r18
            r20 = 60000(0xea60, double:2.9644E-319)
            int r18 = (r18 > r20 ? 1 : (r18 == r20 ? 0 : -1))
            if (r18 >= 0) goto L_0x0094
            r0 = r0 | 4
            monitor-exit(r7)     // Catch:{ all -> 0x00da }
            return r0
        L_0x0094:
            r13.remove(r4)     // Catch:{ all -> 0x00da }
            goto L_0x00a5
        L_0x0099:
            r0 = r0 | 4
            if (r11 != 0) goto L_0x00a3
            r1.clearPassPackages(r6)     // Catch:{ all -> 0x00da }
            r13.add(r4)     // Catch:{ all -> 0x00da }
        L_0x00a3:
            monitor-exit(r7)     // Catch:{ all -> 0x00da }
            return r0
        L_0x00a5:
            if (r11 != 0) goto L_0x00aa
            r1.clearPassPackages(r6)     // Catch:{ all -> 0x00da }
        L_0x00aa:
            android.util.ArraySet<java.lang.String> r15 = r8.mAccessControlCanceled     // Catch:{ all -> 0x00da }
            boolean r15 = r15.contains(r4)     // Catch:{ all -> 0x00da }
            if (r15 == 0) goto L_0x00b6
            r0 = r0 | 8
            monitor-exit(r7)     // Catch:{ all -> 0x00da }
            return r0
        L_0x00b6:
            r15 = 1
            if (r11 != r15) goto L_0x00c5
            boolean r16 = r1.getAccessControlLockConvenient(r8)     // Catch:{ all -> 0x00da }
            if (r16 == 0) goto L_0x00c5
            boolean r16 = r1.isPackageAccessControlPass(r8)     // Catch:{ all -> 0x00da }
            if (r16 != 0) goto L_0x00d6
        L_0x00c5:
            com.miui.server.AccessController r15 = r1.mAccessController     // Catch:{ all -> 0x00da }
            boolean r15 = r15.skipActivity(r2, r4)     // Catch:{ all -> 0x00da }
            if (r15 != 0) goto L_0x00d6
            com.miui.server.AccessController r15 = r1.mAccessController     // Catch:{ all -> 0x00da }
            r1 = 1
            boolean r1 = r15.filterIntentLocked(r1, r4, r2)     // Catch:{ all -> 0x00da }
            if (r1 == 0) goto L_0x00d8
        L_0x00d6:
            r0 = r0 | 4
        L_0x00d8:
            monitor-exit(r7)     // Catch:{ all -> 0x00da }
            return r0
        L_0x00da:
            r0 = move-exception
            monitor-exit(r7)     // Catch:{ all -> 0x00da }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.server.SecurityManagerService.activityResume(android.content.Intent):int");
    }

    public boolean getApplicationChildrenControlEnabled(String packageName) {
        boolean z;
        int callingUserId = UserHandle.getCallingUserId();
        synchronized (this.mUserStateLock) {
            try {
                z = getPackageSetting(getUserStateLocked(callingUserId).mPackages, packageName).childrenControl;
            } catch (Exception e) {
                return false;
            } catch (Throwable th) {
                throw th;
            }
        }
        return z;
    }

    public void setApplicationChildrenControlEnabled(String packageName, boolean enabled) {
        checkPermission();
        int callingUserId = UserHandle.getCallingUserId();
        synchronized (this.mUserStateLock) {
            getPackageSetting(getUserStateLocked(callingUserId).mPackages, packageName).childrenControl = enabled;
            scheduleWriteSettings();
        }
    }

    public void setCoreRuntimePermissionEnabled(boolean grant, int flags) throws RemoteException {
        if (Binder.getCallingUid() == 1000) {
            DefaultPermissionGrantPolicyInjector.setCoreRuntimePermissionEnabled(grant, flags, UserHandle.getCallingUserId());
            return;
        }
        throw new SecurityException("setCoreRuntimePermissionEnabled Permission DENIED");
    }

    public void grantRuntimePermission(String packageName) {
        ApplicationInfo appInfo;
        int userId = UserHandle.getCallingUserId();
        int callingUid = Binder.getCallingUid();
        int packageUid = PackageManagerServiceCompat.getPackageUid(this.mPackageManagerService, packageName, userId);
        if ((callingUid != 1000 && packageUid != callingUid) || (appInfo = this.mPackageManagerService.getApplicationInfo(packageName, 0, userId)) == null) {
            return;
        }
        if (callingUid == 1000 || (appInfo.flags & 1) != 0) {
            long identity = Binder.clearCallingIdentity();
            try {
                DefaultPermissionGrantPolicyInjector.grantRuntimePermission(packageName, userId);
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        } else {
            throw new SecurityException("grantRuntimePermission Permission DENIED");
        }
    }

    class PackageSetting {
        boolean accessControl = false;
        boolean childrenControl = false;
        boolean isDarkModeChecked = true;
        boolean isPrivacyApp = false;
        boolean maskNotification = false;
        String name;

        PackageSetting(String name2) {
            this.name = name2;
        }
    }

    private PackageSetting getPackageSetting(HashMap<String, PackageSetting> packages, String packageName) {
        PackageSetting ps = packages.get(packageName);
        if (ps != null) {
            return ps;
        }
        PackageSetting ps2 = new PackageSetting(packageName);
        packages.put(packageName, ps2);
        return ps2;
    }

    private void scheduleWriteSettings() {
        if (!this.mSecurityWriteHandler.hasMessages(1)) {
            this.mSecurityWriteHandler.sendEmptyMessageDelayed(1, 1000);
        }
    }

    private void readSettings() {
        if (this.mSettingsFile.getBaseFile().exists()) {
            FileInputStream fis = null;
            try {
                fis = this.mSettingsFile.openRead();
                readPackagesSettings(fis);
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                    }
                }
            } catch (Exception e2) {
                Log.w(TAG, "Error reading package settings", e2);
                if (fis != null) {
                    fis.close();
                }
            } catch (Throwable th) {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e3) {
                    }
                }
                throw th;
            }
        }
    }

    private void readPackagesSettings(FileInputStream fis) throws Exception {
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(fis, (String) null);
        int eventType = parser.getEventType();
        while (eventType != 2 && eventType != 1) {
            eventType = parser.next();
        }
        if ("packages".equals(parser.getName())) {
            String updateVersion = parser.getAttributeValue((String) null, "updateVersion");
            if (!TextUtils.isEmpty(updateVersion) && UPDATE_VERSION.equals(updateVersion)) {
                this.mIsUpdated = true;
            }
            int eventType2 = parser.next();
            do {
                if (eventType2 == 2 && parser.getDepth() == 2 && com.android.server.pm.Settings.ATTR_PACKAGE.equals(parser.getName())) {
                    String name = parser.getAttributeValue((String) null, "name");
                    PackageSetting ps = new PackageSetting(name);
                    int userHandle = 0;
                    String userHandleStr = parser.getAttributeValue((String) null, "u");
                    if (!TextUtils.isEmpty(userHandleStr)) {
                        userHandle = Integer.parseInt(userHandleStr);
                    }
                    ps.accessControl = Boolean.parseBoolean(parser.getAttributeValue((String) null, "accessControl"));
                    ps.childrenControl = Boolean.parseBoolean(parser.getAttributeValue((String) null, "childrenControl"));
                    ps.maskNotification = Boolean.parseBoolean(parser.getAttributeValue((String) null, "maskNotification"));
                    ps.isPrivacyApp = Boolean.parseBoolean(parser.getAttributeValue((String) null, "isPrivacyApp"));
                    ps.isDarkModeChecked = Boolean.parseBoolean(parser.getAttributeValue((String) null, "isDarkModeChecked"));
                    synchronized (this.mUserStateLock) {
                        getUserStateLocked(userHandle).mPackages.put(name, ps);
                    }
                }
                eventType2 = parser.next();
            } while (eventType2 != 1);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 10 */
    /* access modifiers changed from: private */
    public void writeSettings() {
        try {
            ArrayList<UserState> userStates = new ArrayList<>();
            synchronized (this.mUserStateLock) {
                int size = this.mUserStates.size();
                for (int i = 0; i < size; i++) {
                    UserState state = this.mUserStates.valueAt(i);
                    UserState userState = new UserState();
                    userState.userHandle = state.userHandle;
                    userState.mPackages.putAll(new HashMap(state.mPackages));
                    userStates.add(userState);
                }
            }
            FileOutputStream fos = this.mSettingsFile.startWrite();
            XmlSerializer out = new FastXmlSerializer();
            out.setOutput(fos, "utf-8");
            out.startDocument((String) null, true);
            out.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            out.startTag((String) null, "packages");
            out.attribute((String) null, "updateVersion", UPDATE_VERSION);
            Iterator<UserState> it = userStates.iterator();
            while (it.hasNext()) {
                UserState userState2 = it.next();
                for (PackageSetting ps : userState2.mPackages.values()) {
                    out.startTag((String) null, com.android.server.pm.Settings.ATTR_PACKAGE);
                    out.attribute((String) null, "name", ps.name);
                    out.attribute((String) null, "accessControl", String.valueOf(ps.accessControl));
                    out.attribute((String) null, "childrenControl", String.valueOf(ps.childrenControl));
                    out.attribute((String) null, "maskNotification", String.valueOf(ps.maskNotification));
                    out.attribute((String) null, "isPrivacyApp", String.valueOf(ps.isPrivacyApp));
                    out.attribute((String) null, "isDarkModeChecked", String.valueOf(ps.isDarkModeChecked));
                    out.attribute((String) null, "u", String.valueOf(userState2.userHandle));
                    out.endTag((String) null, com.android.server.pm.Settings.ATTR_PACKAGE);
                }
            }
            out.endTag((String) null, "packages");
            out.endDocument();
            this.mSettingsFile.finishWrite(fos);
        } catch (IOException e1) {
            Log.w(TAG, "Error writing package settings file", e1);
            if (0 != 0) {
                this.mSettingsFile.failWrite((FileOutputStream) null);
            }
        }
    }

    private void removePackage(String packageName, int uid) {
        synchronized (this.mUserStateLock) {
            getUserStateLocked(UserHandle.getUserId(uid)).mPackages.remove(packageName);
            scheduleWriteSettings();
        }
    }

    private void checkPermission() {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.CHANGE_COMPONENT_ENABLED_STATE") != 0) {
            throw new SecurityException("Permission Denial: attempt to change application state from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
        }
    }

    private void sucheduleWriteWakeUpTime() {
        if (!this.mSecurityWriteHandler.hasMessages(2)) {
            this.mSecurityWriteHandler.sendEmptyMessage(2);
        }
    }

    private void sucheduleWriteBootTime() {
        if (!this.mSecurityWriteHandler.hasMessages(3)) {
            this.mSecurityWriteHandler.sendEmptyMessage(3);
        }
    }

    public void setWakeUpTime(String componentName, long timeInSeconds) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.MANAGE_BOOT_TIME, TAG);
        putBootTimeToMap(componentName, timeInSeconds);
        sucheduleWriteWakeUpTime();
        setTimeBoot();
    }

    private void minWakeUpTime(long nowtime) {
        long min = 0;
        long rightBorder = 300 + nowtime;
        for (String componentName : this.mWakeUpTime.keySet()) {
            long tmp = getBootTimeFromMap(componentName);
            if (tmp >= nowtime && (tmp < min || min == 0)) {
                min = tmp >= rightBorder ? tmp : rightBorder;
            }
        }
        this.mWakeTime = min;
    }

    private void setTimeBoot() {
        long now_time = System.currentTimeMillis() / 1000;
        synchronized (this.mWakeUpTime) {
            minWakeUpTime(now_time);
        }
        sucheduleWriteBootTime();
    }

    /* access modifiers changed from: private */
    public void writeWakeUpTime() {
        try {
            FileOutputStream fos = this.mWakeUpFile.startWrite();
            XmlSerializer out = new FastXmlSerializer();
            out.setOutput(fos, "utf-8");
            out.startDocument((String) null, true);
            out.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            out.startTag((String) null, CLASS_NAMES);
            for (String componentName : this.mWakeUpTime.keySet()) {
                if (getBootTimeFromMap(componentName) != 0) {
                    out.startTag((String) null, CLASS_NAME);
                    out.attribute((String) null, "name", componentName);
                    out.attribute((String) null, "time", String.valueOf(getBootTimeFromMap(componentName)));
                    out.endTag((String) null, CLASS_NAME);
                }
            }
            out.endTag((String) null, CLASS_NAMES);
            out.endDocument();
            this.mWakeUpFile.finishWrite(fos);
        } catch (IOException e) {
            if (0 != 0) {
                this.mWakeUpFile.failWrite((FileOutputStream) null);
            }
        }
    }

    private void readWakeUpTime() {
        this.mWakeUpTime.clear();
        if (this.mWakeUpFile.getBaseFile().exists()) {
            FileInputStream fis = null;
            try {
                fis = this.mWakeUpFile.openRead();
                readWakeUpTime(fis);
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                    }
                }
            } catch (Exception e2) {
                this.mWakeUpFile.getBaseFile().delete();
                if (fis != null) {
                    fis.close();
                }
            } catch (Throwable th) {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e3) {
                    }
                }
                throw th;
            }
        }
    }

    private void readWakeUpTime(FileInputStream fis) throws Exception {
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(fis, (String) null);
        int eventType = parser.getEventType();
        while (eventType != 2 && eventType != 1) {
            eventType = parser.next();
        }
        if (CLASS_NAMES.equals(parser.getName())) {
            int eventType2 = parser.next();
            do {
                if (eventType2 == 2 && parser.getDepth() == 2 && CLASS_NAME.equals(parser.getName())) {
                    putBootTimeToMap(parser.getAttributeValue((String) null, "name"), new Long(parser.getAttributeValue((String) null, "time")).longValue());
                }
                eventType2 = parser.next();
            } while (eventType2 != 1);
        }
    }

    public long getWakeUpTime(String componentName) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.MANAGE_BOOT_TIME, TAG);
        return getBootTimeFromMap(componentName);
    }

    private synchronized void putBootTimeToMap(String componentName, long time) {
        this.mWakeUpTime.put(componentName, Long.valueOf(time));
    }

    private synchronized long getBootTimeFromMap(String componentName) {
        return this.mWakeUpTime.containsKey(componentName) ? this.mWakeUpTime.get(componentName).longValue() : 0;
    }

    public boolean putSystemDataStringFile(String path, String value) {
        checkPermissionByUid(1000);
        File file = new File(path);
        RandomAccessFile raf = null;
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            raf = new RandomAccessFile(file, "rw");
            raf.setLength(0);
            raf.writeUTF(value);
            try {
                raf.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            return true;
        } catch (IOException e3) {
            e3.printStackTrace();
            if (raf == null) {
                return false;
            }
            try {
                raf.close();
                return false;
            } catch (IOException e4) {
                e4.printStackTrace();
                return false;
            }
        } catch (Throwable th) {
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e5) {
                    e5.printStackTrace();
                }
            }
            throw th;
        }
    }

    public String readSystemDataStringFile(String path) {
        checkPermissionByUid(1000);
        File file = new File(path);
        RandomAccessFile raf = null;
        String result = null;
        if (file.exists()) {
            try {
                RandomAccessFile raf2 = new RandomAccessFile(file, ActivityTaskManagerService.DUMP_RECENTS_SHORT_CMD);
                result = raf2.readUTF();
                try {
                    raf2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e2) {
                e2.printStackTrace();
                if (raf != null) {
                    raf.close();
                }
            } catch (Throwable th) {
                if (raf != null) {
                    try {
                        raf.close();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                }
                throw th;
            }
        }
        return result;
    }

    private void checkPermissionByUid(int uid) {
        int callingUid = Binder.getCallingUid();
        if (UserHandle.getAppId(callingUid) != uid) {
            throw new SecurityException("no permission to read file for UID:" + callingUid);
        }
    }

    private void checkWakePathPermission() {
        this.mContext.enforcePermission("android.permission.UPDATE_APP_OPS_STATS", Binder.getCallingPid(), Binder.getCallingUid(), (String) null);
    }

    public void pushWakePathData(int wakeType, ParceledListSlice wakePathRuleInfos, int userId) {
        checkWakePathPermission();
        WakePathChecker.getInstance().pushWakePathRuleInfos(wakeType, wakePathRuleInfos.getList(), userId);
    }

    public void pushWakePathWhiteList(List<String> wakePathWhiteList, int userId) {
        checkWakePathPermission();
        WakePathChecker.getInstance().pushWakePathWhiteList(wakePathWhiteList, userId);
    }

    public void pushWakePathConfirmDialogWhiteList(int type, List<String> whiteList) {
        checkWakePathPermission();
        WakePathChecker.getInstance().pushWakePathConfirmDialogWhiteList(type, whiteList);
    }

    public void removeWakePathData(int userId) {
        checkWakePathPermission();
        WakePathChecker.getInstance().removeWakePathData(userId);
    }

    public void setTrackWakePathCallListLogEnabled(boolean enabled) {
        checkWakePathPermission();
        WakePathChecker.getInstance().setTrackWakePathCallListLogEnabled(enabled);
    }

    public ParceledListSlice getWakePathCallListLog() {
        checkWakePathPermission();
        return WakePathChecker.getInstance().getWakePathCallListLog();
    }

    public void registerWakePathCallback(IWakePathCallback callback) {
        checkWakePathPermission();
        WakePathChecker.getInstance().registerWakePathCallback(callback);
    }

    public boolean checkAllowStartActivity(String callerPkgName, String calleePkgName, Intent intent, int callerUid, int calleeUid) {
        if (!this.mUserManager.exists(calleeUid)) {
            return true;
        }
        boolean ret = this.mAccessController.filterIntentLocked(true, calleePkgName, intent);
        if (PreloadedAppPolicy.isProtectedDataApp(this.mContext, callerPkgName, 0) || PreloadedAppPolicy.isProtectedDataApp(this.mContext, calleePkgName, 0)) {
            return true;
        }
        if (!ret) {
            return WakePathChecker.getInstance().checkAllowStartActivity(callerPkgName, calleePkgName, callerUid, calleeUid);
        }
        return ret;
    }

    public int getAppPermissionControlOpen(int userId) {
        UserState userState;
        if (this.mUserManager.exists(userId) && (userState = getUserStateOrNullUnLocked(userId)) != null) {
            return userState.mAppPermissionControlStatus;
        }
        return 1;
    }

    public void setAppPermissionControlOpen(int status) {
        this.mContext.enforcePermission("android.permission.UPDATE_APP_OPS_STATS", Binder.getCallingPid(), Binder.getCallingUid(), (String) null);
        int callingUserId = UserHandle.getCallingUserId();
        synchronized (this.mUserStateLock) {
            int unused = getUserStateLocked(callingUserId).mAppPermissionControlStatus = status;
        }
    }

    public int getCurrentUserId() {
        return ExtraActivityManagerService.getCurrentUserId();
    }

    public int getSysAppCracked() {
        return this.mSysAppCracked;
    }

    public void grantInstallPermission(String packageName, String name) {
        checkPermission();
        if ("android.permission.CAPTURE_AUDIO_OUTPUT".equals(name)) {
            PackageManagerServicePermissionProxy.grantInstallPermission(packageName, name, UserHandle.getCallingUserId());
            return;
        }
        throw new IllegalArgumentException("not support permssion : " + name);
    }

    private void updateXSpaceSettings() {
        synchronized (this.mUserStateLock) {
            if (ConfigUtils.isSupportXSpace() && !this.mIsUpdated) {
                UserState userState = getUserStateLocked(0);
                UserState userStateXSpace = getUserStateLocked(999);
                for (Map.Entry<String, PackageSetting> entrySet : userState.mPackages.entrySet()) {
                    String name = entrySet.getKey();
                    if (XSpaceUserHandle.isAppInXSpace(this.mContext, name)) {
                        PackageSetting value = entrySet.getValue();
                        PackageSetting psXSpace = new PackageSetting(name);
                        psXSpace.accessControl = value.accessControl;
                        psXSpace.childrenControl = value.childrenControl;
                        userStateXSpace.mPackages.put(name, psXSpace);
                    }
                }
                scheduleWriteSettings();
            }
        }
    }

    private boolean isPackageAccessControlPass(UserState userState) {
        if (ConfigUtils.isSupportXSpace() && (userState.userHandle == 999 || userState.userHandle == 0)) {
            if (getUserStateLocked(0).mAccessControlPassPackages.size() + getUserStateLocked(999).mAccessControlPassPackages.size() > 0) {
                return true;
            }
            return false;
        } else if (userState.mAccessControlPassPackages.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    private UserState changeUserState(UserState userState) {
        return getUserStateLocked(userState.userHandle == 999 ? 0 : userState.userHandle);
    }

    private void clearPassPackages(int userId) {
        if (!ConfigUtils.isSupportXSpace() || !(userId == 0 || 999 == userId)) {
            getUserStateLocked(userId).mAccessControlPassPackages.clear();
            return;
        }
        UserState userStateOwner = getUserStateLocked(0);
        UserState userStateXSpace = getUserStateLocked(999);
        HashSet<String> passPackagesOwner = userStateOwner.mAccessControlPassPackages;
        HashSet<String> passPackagesXSpace = userStateXSpace.mAccessControlPassPackages;
        passPackagesOwner.clear();
        passPackagesXSpace.clear();
    }

    public boolean isRestrictedAppNet(String packageName) {
        return RestrictAppNetManager.isRestrictedAppNet(this.mContext, packageName);
    }

    public boolean writeAppHideConfig(boolean hide) {
        checkPermission();
        return false;
    }

    private boolean saveIconInner(String fileName, Bitmap icon) {
        if (allowSaveIconCache()) {
            return moveIconInner(ThemeRuntimeManager.createTempIconFile(this.mContext, fileName, icon));
        }
        return false;
    }

    private boolean moveIconInner(String srcIconPath) {
        boolean ret = false;
        if (!TextUtils.isEmpty(srcIconPath)) {
            String destPath = IconCustomizer.CUSTOMIZED_ICON_PATH + FileUtils.getFileName(srcIconPath);
            ret = ThemeNativeUtils.copy(srcIconPath, destPath);
            if (ret) {
                ret = ThemeNativeUtils.updateFilePermissionWithThemeContext(destPath);
            }
            IconCustomizer.ensureMiuiVersionFlagExist(this.mContext);
            ThemeNativeUtils.remove(srcIconPath);
        }
        return ret;
    }

    private boolean allowSaveIconCache() {
        return isSystemApp() && (UserHandle.getAppId(Binder.getCallingUid()) == 9801 || canSaveExternalIconCache());
    }

    private boolean isSystemApp() {
        try {
            int uid = Binder.getCallingUid();
            PackageManager pm = this.mContext.getPackageManager();
            if ((pm.getApplicationInfo(pm.getPackagesForUid(uid)[0], 0).flags & 1) != 0) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean canSaveExternalIconCache() {
        StringBuilder sb = new StringBuilder();
        sb.append(IconCustomizer.CUSTOMIZED_ICON_PATH);
        sb.append("miui_version");
        return System.currentTimeMillis() - Libcore_Os_getFileLastStatusChangedTime(sb.toString()) > 60000;
    }

    public static long Libcore_Os_getFileLastStatusChangedTime(String path) {
        miui.reflect.Method method;
        try {
            Object o = Field.of("libcore.io.Libcore", "os", "Llibcore/io/Os;").get((Object) null);
            if (Build.VERSION.SDK_INT > 20) {
                method = miui.reflect.Method.of(o.getClass(), "lstat", "(Ljava/lang/String;)Landroid/system/StructStat;");
            } else {
                method = miui.reflect.Method.of(o.getClass(), "lstat", "(Ljava/lang/String;)Llibcore/io/StructStat;");
            }
            Object o2 = method.invokeObject((Class) null, o, new Object[]{path});
            return Field.of(o2.getClass(), "st_ctime", "J").getLong(o2) * 1000;
        } catch (Exception e) {
            Log.e(TAG, "getFileChangeTime fail :" + e);
            return -1;
        }
    }

    public boolean addMiuiFirewallSharedUid(int uid) {
        checkPermissionByUid(1000);
        return MiuiNetworkManagementService.getInstance().addMiuiFirewallSharedUid(uid);
    }

    public boolean setMiuiFirewallRule(String packageName, int uid, int rule, int type) {
        checkPermissionByUid(1000);
        return MiuiNetworkManagementService.getInstance().setMiuiFirewallRule(packageName, uid, rule, type);
    }

    public boolean setCurrentNetworkState(int state) {
        checkPermissionByUid(1000);
        return MiuiNetworkManagementService.getInstance().setCurrentNetworkState(state);
    }

    public void setIncompatibleAppList(List<String> list) {
        checkPermission();
        if (list != null) {
            synchronized (this.mIncompatibleAppList) {
                this.mIncompatibleAppList.clear();
                this.mIncompatibleAppList.addAll(list);
            }
            return;
        }
        throw new NullPointerException("List is null");
    }

    public List<String> getIncompatibleAppList() {
        ArrayList arrayList;
        synchronized (this.mIncompatibleAppList) {
            arrayList = new ArrayList(this.mIncompatibleAppList);
        }
        return arrayList;
    }

    public ParceledListSlice getWakePathComponents(String packageName) {
        checkWakePathPermission();
        List<WakePathComponent> ret = PackageManagerServiceCompat.getWakePathComponents(this.mPackageManagerService, packageName);
        if (ret == null) {
            return null;
        }
        return new ParceledListSlice(ret);
    }

    public void offerGoogleBaseCallBack(final ISecurityCallback cb) {
        checkPermission();
        this.sGoogleBaseService = cb;
        try {
            cb.asBinder().linkToDeath(new IBinder.DeathRecipient() {
                public void binderDied() {
                    cb.asBinder().unlinkToDeath(this, 0);
                    ISecurityCallback unused = SecurityManagerService.this.sGoogleBaseService = null;
                    Slog.d(SecurityManagerService.TAG, "securitycenter died, reset handle to null");
                }
            }, 0);
        } catch (Exception e) {
            Log.e(TAG, "offerGoogleBaseCallBack", e);
        }
    }

    public void notifyAppsPreInstalled() {
        checkPermission();
        synchronized (this.mRegistrantLock) {
            this.mAppsPreInstallRegistrant.notifyRegistrants();
            for (int i = this.mAppsPreInstallRegistrant.size() - 1; i >= 0; i--) {
                ((Registrant) this.mAppsPreInstallRegistrant.get(i)).clear();
            }
            this.mAppsPreInstallRegistrant.removeCleared();
        }
    }

    public void registerForAppsPreInstalled(Handler h, int what, Object obj) {
        synchronized (this.mRegistrantLock) {
            if (this.mAppsPreInstallRegistrant.size() == 0) {
                this.mAppsPreInstallRegistrant.add(new Registrant(h, what, obj));
            }
        }
    }

    public ISecurityCallback getGoogleBaseService() {
        return this.sGoogleBaseService;
    }

    public boolean areNotificationsEnabledForPackage(String packageName, int uid) throws RemoteException {
        checkPermission();
        if (this.mINotificationManager == null) {
            this.mINotificationManager = INotificationManager.Stub.asInterface(ServiceManager.getService("notification"));
        }
        long identity = Binder.clearCallingIdentity();
        try {
            return this.mINotificationManager.areNotificationsEnabledForPackage(packageName, uid);
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    public void setNotificationsEnabledForPackage(String packageName, int uid, boolean enabled) throws RemoteException {
        checkPermission();
        if (this.mINotificationManager == null) {
            this.mINotificationManager = INotificationManager.Stub.asInterface(ServiceManager.getService("notification"));
        }
        long identity = Binder.clearCallingIdentity();
        try {
            this.mINotificationManager.setNotificationsEnabledForPackage(packageName, uid, enabled);
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    public boolean isAppHide() {
        return PackageHideManager.getInstance(false).isAppHide();
    }

    public boolean isFunctionOpen() {
        return PackageHideManager.getInstance(false).isFunctionOpen();
    }

    public boolean setAppHide(boolean hide) {
        return PackageHideManager.getInstance(false).setHideApp(this.mContext, hide);
    }

    public boolean isValidDevice() {
        return PackageHideManager.isValidDevice();
    }

    private void checkWriteSecurePermission() {
        Context context = this.mContext;
        context.enforceCallingPermission("android.permission.WRITE_SECURE_SETTINGS", "Permission Denial: attempt to change application privacy revoke state from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
    }

    public void setAppPrivacyStatus(String packageName, boolean isOpen) {
        if (!TextUtils.isEmpty(packageName)) {
            String callingPackageName = ExtraActivityManagerService.getPackageNameByPid(Binder.getCallingPid());
            if (!"com.android.settings".equals(callingPackageName) && !packageName.equals(callingPackageName)) {
                checkWriteSecurePermission();
            }
            long identity = Binder.clearCallingIdentity();
            try {
                ContentResolver contentResolver = this.mContext.getContentResolver();
                Settings.Secure.putInt(contentResolver, "privacy_status_" + packageName, isOpen ? 1 : 0);
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        } else {
            throw new RuntimeException("packageName can not be null or empty");
        }
    }

    public boolean isAppPrivacyEnabled(String packageName) {
        if (!TextUtils.isEmpty(packageName)) {
            long identity = Binder.clearCallingIdentity();
            try {
                ContentResolver contentResolver = this.mContext.getContentResolver();
                boolean z = true;
                if (Settings.Secure.getInt(contentResolver, "privacy_status_" + packageName, 1) == 0) {
                    z = false;
                }
                return z;
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        } else {
            throw new RuntimeException("packageName can not be null or empty");
        }
    }

    public boolean isAllowStartService(Intent service, int userId) {
        checkPermission();
        return AutoStartManagerService.isAllowStartService(this.mContext, service, userId);
    }

    public IBinder getTopActivity() {
        ComponentName componentName;
        checkPermission();
        HashMap<String, Object> topActivity = ExtraActivityManagerService.getTopRunningActivityInfo();
        if (topActivity == null || (componentName = ((Intent) topActivity.get("intent")).getComponent()) == null) {
            return null;
        }
        String clsName = componentName.getClassName();
        if (!"com.google.android.packageinstaller".equals(componentName.getPackageName())) {
            return null;
        }
        if ("com.android.packageinstaller.InstallAppProgress".equals(clsName) || "com.android.packageinstaller.InstallSuccess".equals(clsName) || !"com.android.packageinstaller.PackageInstallerActivity".equals(clsName)) {
            return (IBinder) topActivity.get("token");
        }
        return null;
    }

    public IBinder getAppRunningControlIBinder() {
        return this.mAppRunningControlBinder;
    }

    public static AppRunningControlService getAppRunningControlService() {
        return mAppRunningControlService;
    }

    public void watchGreenGuardProcess() {
        GreenGuardManagerService.startWatchGreenguardProcess(this.mContext);
    }

    public int getSecondSpaceId() {
        long callingId = Binder.clearCallingIdentity();
        try {
            return Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "second_user_id", ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION, 0);
        } finally {
            Binder.restoreCallingIdentity(callingId);
        }
    }

    public int moveTaskToStack(int taskId, int stackId, boolean toTop) {
        if (toTop) {
            long callingId = Binder.clearCallingIdentity();
            try {
                Settings.Secure.putIntForUser(this.mContext.getContentResolver(), "quick_reply", 0, -2);
            } finally {
                Binder.restoreCallingIdentity(callingId);
            }
        }
        return MiuiMultiWindowUtils.moveTaskToStack(taskId, stackId, toTop);
    }

    public void setStickWindowName(String component) {
        long callingId = Binder.clearCallingIdentity();
        try {
            Settings.Secure.putString(this.mContext.getContentResolver(), "gamebox_stick", component);
        } finally {
            Binder.restoreCallingIdentity(callingId);
        }
    }

    public boolean getStickWindowName(String component) {
        return component != null && component.equals(Settings.Secure.getString(this.mContext.getContentResolver(), "gamebox_stick"));
    }

    public int resizeTask(int taskId, Rect bounds, int resizeMode) {
        return MiuiMultiWindowUtils.resizeTask(taskId, bounds, resizeMode);
    }

    public void pushUpdatePkgsData(List<String> updatePkgsList, boolean enable) {
        checkWakePathPermission();
        WakePathChecker.getInstance().pushUpdatePkgsData(updatePkgsList, enable);
    }

    public void setPrivacyApp(String packageName, int userId, boolean isPrivacy) {
        checkPermission();
        synchronized (this.mUserStateLock) {
            getPackageSetting(getUserStateLocked(userId).mPackages, packageName).isPrivacyApp = isPrivacy;
            scheduleWriteSettings();
        }
    }

    public boolean isPrivacyApp(String packageName, int userId) {
        boolean z;
        checkPermission();
        synchronized (this.mUserStateLock) {
            try {
                z = getPackageSetting(getUserStateLocked(userId).mPackages, packageName).isPrivacyApp;
            } catch (Exception e) {
                Log.e(TAG, "isPrivacyApp error", e);
                return false;
            }
        }
        return z;
    }

    public List<String> getAllPrivacyApps(int userId) {
        List<String> privacyAppsList;
        checkPermission();
        synchronized (this.mUserStateLock) {
            privacyAppsList = new ArrayList<>();
            UserState userState = getUserStateLocked(userId);
            for (String pkgName : userState.mPackages.keySet()) {
                try {
                    if (getPackageSetting(userState.mPackages, pkgName).isPrivacyApp) {
                        privacyAppsList.add(pkgName);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "getAllPrivacyApps error", e);
                }
            }
        }
        return privacyAppsList;
    }

    public void updateLauncherPackageNames() {
        WakePathChecker.getInstance().init(this.mContext);
    }

    private void checkGrantPermissionPkg() {
        String callingPackageName = ExtraActivityManagerService.getPackageNameByPid(Binder.getCallingPid());
        if (!"com.lbe.security.miui".equals(callingPackageName)) {
            throw new SecurityException("Permission Denial: attempt to grant/revoke permission from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + ", pkg=" + callingPackageName);
        }
    }

    public void grantRuntimePermissionAsUser(String packageName, String permName, int userId) {
        checkGrantPermissionPkg();
        long identity = Binder.clearCallingIdentity();
        try {
            this.mPackageManagerService.grantRuntimePermission(packageName, permName, userId);
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    public void revokeRuntimePermissionAsUser(String packageName, String permName, int userId) {
        checkGrantPermissionPkg();
        long identity = Binder.clearCallingIdentity();
        try {
            this.mPackageManagerService.revokeRuntimePermission(packageName, permName, userId);
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    public void revokeRuntimePermissionAsUserNotKill(String packageName, String permName, int userId) {
        checkGrantPermissionPkg();
        long identity = Binder.clearCallingIdentity();
        try {
            this.mPackageManagerService.revokeRuntimePermissionNotKill(packageName, permName, userId);
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    public int getPermissionFlagsAsUser(String permName, String packageName, int userId) {
        checkGrantPermissionPkg();
        long identity = Binder.clearCallingIdentity();
        try {
            return this.mPackageManagerService.getPermissionFlags(permName, packageName, userId);
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    public void updatePermissionFlagsAsUser(String permissionName, String packageName, int flagMask, int flagValues, int userId) {
        checkGrantPermissionPkg();
        long identity = Binder.clearCallingIdentity();
        try {
            if (Build.VERSION.SDK_INT <= 28) {
                callObjectMethod(this.mPackageManagerService, "updatePermissionFlags", new Class[]{String.class, String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE}, permissionName, packageName, Integer.valueOf(flagMask), Integer.valueOf(flagValues), Integer.valueOf(userId));
            } else {
                java.lang.reflect.Field pms = this.mPackageManagerService.getClass().getDeclaredField("mPermissionManager");
                java.lang.reflect.Field callback = this.mPackageManagerService.getClass().getDeclaredField("mPermissionCallback");
                pms.setAccessible(true);
                callback.setAccessible(true);
                callObjectMethod(pms.get(this.mPackageManagerService), "updatePermissionFlags", new Class[]{String.class, String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Boolean.TYPE, Class.forName("com.android.server.pm.permission.PermissionManagerServiceInternal$PermissionCallback")}, permissionName, packageName, Integer.valueOf(flagMask), Integer.valueOf(flagValues), Integer.valueOf(Binder.getCallingUid()), Integer.valueOf(userId), true, callback.get(this.mPackageManagerService));
            }
        } catch (Exception e) {
            Log.e(TAG, "updatePermissionFlagsAsUser exception!", e);
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(identity);
            throw th;
        }
        Binder.restoreCallingIdentity(identity);
    }

    public void updateLedStatus(boolean on) {
        checkPermission();
        Light light = this.mLedLight;
        if (light == null) {
            Log.i(TAG, "updateLightsLocked mLedLight cannot assess");
            return;
        }
        if (on) {
            light.setColor(this.mLightOn);
        } else {
            light.turnOff();
        }
        Log.i(TAG, "updateLightsLocked " + on + " , calling pid= " + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
    }
}
