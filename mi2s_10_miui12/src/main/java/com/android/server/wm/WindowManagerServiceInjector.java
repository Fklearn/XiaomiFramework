package com.android.server.wm;

import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.database.ContentObserver;
import android.os.Binder;
import android.os.Build;
import android.os.Debug;
import android.os.Handler;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManagerInternal;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.server.am.SplitScreenReporter;
import android.text.TextUtils;
import android.util.MiuiDisplayMetrics;
import android.util.Slog;
import android.view.WindowManager;
import com.android.server.LocalServices;
import com.android.server.pm.DumpState;
import com.android.server.pm.PackageManagerService;
import java.util.ArrayList;

class WindowManagerServiceInjector {
    private static String CUR_DEVICE = Build.DEVICE;
    private static String[] FORCE_ORI_DEVICES_LIST = {"lithium", "chiron", "polaris"};
    private static String[] FORCE_ORI_LIST = {"com.tencent.mm/com.tencent.mm.plugin.voip.ui.VideoActivity", "com.tencent.mm/com.tencent.mm.plugin.multitalk.ui.MultiTalkMainUI"};
    public static String GOOGLE = "com.google.android.dialer";
    public static String GOOGLE_FLOATING = "com.google.android.dialer/.FloatingWindow";
    public static boolean IS_CTS_MODE = false;
    public static final int LAUNCHER_STATE_NORMAL = 1;
    public static final int LAUNCHER_STATE_OVERVIEW = 2;
    private static final String MIUI_RESOLUTION = "persist.sys.miui_resolution";
    public static String MM = "com.tencent.mm";
    public static String MM_FLOATING = "com.tencent.mm/.FloatingWindow";
    public static String QQ = "com.tencent.mobileqq";
    public static String QQ_FLOATING = "com.tencent.mobileqq/.FloatingWindow";
    public static String SECURITY = ActivityStackSupervisorInjector.MIUI_APP_LOCK_PACKAGE_NAME;
    public static String SECURITY_FLOATING = "com.miui.securitycenter/.FloatingWindow";
    public static final String SETTINGS_LAUNCHER_STATE = "launcher_state";
    private static final String TAG = "WindowManagerService";
    private static boolean mIsInScreenProjection;
    private static ArrayList<String> mProjectionBlackList = new ArrayList<>();
    static int sMiuiDisplayDensity;
    static int sMiuiDisplayHeight;
    static int sMiuiDisplayWidth;

    WindowManagerServiceInjector() {
    }

    public static void adjustWindowParams(Context context, AppOpsManager appOps, WindowManager.LayoutParams attrs, String packageName, int uid) {
        if (attrs != null) {
            if (!(((attrs.flags & DumpState.DUMP_FROZEN) == 0 && (attrs.flags & DumpState.DUMP_CHANGES) == 0) || appOps.noteOpNoThrow(10020, uid, packageName) == 0)) {
                attrs.flags &= -524289;
                attrs.flags &= -4194305;
                Slog.i(TAG, "MIUILOG- Show when locked PermissionDenied pkg : " + packageName + " uid : " + uid);
            }
            adjustFindDeviceAttrs(context, uid, attrs, packageName);
        }
    }

    public static boolean isAllowedDisableKeyguard(AppOpsManager appOps, int uid) {
        String[] packages = null;
        try {
            packages = AppGlobals.getPackageManager().getPackagesForUid(uid);
        } catch (RemoteException e) {
        }
        if (packages == null || packages.length == 0 || appOps.checkOpNoThrow(10020, uid, packages[0]) == 0) {
            return true;
        }
        Slog.i(TAG, "MIUILOG- DisableKeyguard PermissionDenied uid : " + uid);
        return false;
    }

    public static int getForceOrientation(AppWindowToken atoken, int lastOrientation) {
        WindowState win;
        String[] strArr;
        if (!(!needForceOrientation() || (win = atoken.findMainWindow()) == null || (strArr = FORCE_ORI_LIST) == null)) {
            for (String name : strArr) {
                if (name.equals(win.getAttrs().getTitle())) {
                    return 7;
                }
            }
        }
        return lastOrientation;
    }

    private static boolean needForceOrientation() {
        for (String device : FORCE_ORI_DEVICES_LIST) {
            if (device.equals(CUR_DEVICE)) {
                return true;
            }
        }
        return false;
    }

    private static void adjustFindDeviceAttrs(Context context, int uid, WindowManager.LayoutParams attrs, String packageName) {
        addShowOnFindDeviceKeyguardAttrsIfNecessary(context, attrs, packageName);
        removeFindDeviceKeyguardFlagsIfNecessary(uid, attrs, packageName);
    }

    private static void addShowOnFindDeviceKeyguardAttrsIfNecessary(Context context, WindowManager.LayoutParams attrs, String packageName) {
        if (context != null && TextUtils.equals("com.google.android.dialer", packageName) && Settings.Global.getInt(context.getContentResolver(), "com.xiaomi.system.devicelock.locked", 0) != 0) {
            attrs.format = -1;
            attrs.layoutInDisplayCutoutMode = 0;
            attrs.extraFlags |= 4096;
        }
    }

    private static void removeFindDeviceKeyguardFlagsIfNecessary(int uid, WindowManager.LayoutParams attrs, String packageName) {
        if ((attrs.extraFlags & 2048) != 0 || (attrs.extraFlags & 4096) != 0) {
            if (!isFindDeviceFlagUsePermitted(uid, packageName) || ((attrs.extraFlags & 2048) != 0 && !"com.xiaomi.finddevice".equals(packageName))) {
                attrs.extraFlags &= -2049;
                attrs.extraFlags &= -4097;
            }
        }
    }

    private static boolean isFindDeviceFlagUsePermitted(int uid, String packageName) {
        IPackageManager pm;
        if (TextUtils.isEmpty(packageName) || (pm = AppGlobals.getPackageManager()) == null) {
            return false;
        }
        try {
            if (pm.checkSignatures(PackageManagerService.PLATFORM_PACKAGE_NAME, packageName) == 0) {
                return true;
            }
            ApplicationInfo ai = pm.getApplicationInfo(packageName, 0, UserHandle.getUserId(uid));
            if (ai == null || (ai.flags & 1) == 0) {
                return false;
            }
            return true;
        } catch (RemoteException e) {
        }
    }

    static boolean onTransact(WindowManagerService service, int code, Parcel data, Parcel reply, int flags) {
        if (code == 255) {
            return switchResolution(service, data, reply, flags);
        }
        return false;
    }

    static boolean switchResolution(WindowManagerService service, Parcel data, Parcel reply, int flags) {
        data.enforceInterface("android.view.IWindowManager");
        switchResolutionIntenal(service, data.readInt(), data.readInt(), data.readInt(), data.readInt());
        reply.writeNoException();
        return true;
    }

    private static void switchResolutionIntenal(WindowManagerService service, int displayId, int width, int height, int density) {
        if (UserHandle.getAppId(Binder.getCallingUid()) != 1000) {
            throw new SecurityException("Only system uid can switch resolution");
        } else if (displayId == 0) {
            long ident = Binder.clearCallingIdentity();
            try {
                int[] userIds = ((UserManagerInternal) LocalServices.getService(UserManagerInternal.class)).getUserIds();
                synchronized (service.mWindowMap) {
                    DisplayContent displayContent = service.mRoot.getDisplayContent(displayId);
                    if (displayContent != null) {
                        int width2 = Math.min(Math.max(width, 200), displayContent.mInitialDisplayWidth * 2);
                        int height2 = Math.min(Math.max(height, 200), displayContent.mInitialDisplayHeight * 2);
                        displayContent.mBaseDisplayWidth = width2;
                        sMiuiDisplayWidth = width2;
                        displayContent.mBaseDisplayHeight = height2;
                        sMiuiDisplayHeight = height2;
                        displayContent.mBaseDisplayDensity = density;
                        sMiuiDisplayDensity = density;
                        service.reconfigureDisplayLocked(displayContent);
                    }
                }
                SystemProperties.set(MIUI_RESOLUTION, sMiuiDisplayWidth + "," + sMiuiDisplayHeight + "," + sMiuiDisplayDensity);
                Settings.Global.putString(service.mContext.getContentResolver(), "display_size_forced", "");
                for (int userId : userIds) {
                    Settings.Secure.putStringForUser(service.mContext.getContentResolver(), "display_density_forced", "", userId);
                }
                Binder.restoreCallingIdentity(ident);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(ident);
                throw th;
            }
        } else {
            throw new IllegalArgumentException("Can only set the default display");
        }
    }

    static void initializeMiuiResolutionLocked(DisplayContent displayContent) {
        String resolution = SystemProperties.get(MIUI_RESOLUTION, (String) null);
        if (resolution != null) {
            String[] values = resolution.split(",");
            if (values.length == 3) {
                try {
                    int parseInt = Integer.parseInt(values[0]);
                    sMiuiDisplayWidth = parseInt;
                    displayContent.mBaseDisplayWidth = parseInt;
                    int parseInt2 = Integer.parseInt(values[1]);
                    sMiuiDisplayHeight = parseInt2;
                    displayContent.mBaseDisplayHeight = parseInt2;
                    int parseInt3 = Integer.parseInt(values[2]);
                    sMiuiDisplayDensity = parseInt3;
                    displayContent.mBaseDisplayDensity = parseInt3;
                    return;
                } catch (NumberFormatException e) {
                }
            }
        }
        int i = displayContent.mInitialDisplayWidth;
        sMiuiDisplayWidth = i;
        displayContent.mBaseDisplayWidth = i;
        int i2 = displayContent.mInitialDisplayHeight;
        sMiuiDisplayHeight = i2;
        displayContent.mBaseDisplayHeight = i2;
        int i3 = MiuiDisplayMetrics.DENSITY_DEVICE;
        sMiuiDisplayDensity = i3;
        displayContent.mBaseDisplayDensity = i3;
    }

    static void checkBoostPriorityForLockTime(long startBoostPriorityTime) {
        long endBoostPriorityTime = SystemClock.uptimeMillis();
        if (endBoostPriorityTime - startBoostPriorityTime > 3000) {
            Slog.w(TAG, "Slow operation: holding wms lock in " + Debug.getCallers(2) + " " + (endBoostPriorityTime - startBoostPriorityTime) + "ms");
        }
    }

    public static ArrayList<String> getProjectionBlackList() {
        if (mProjectionBlackList.size() == 0) {
            mProjectionBlackList.add("StatusBar");
            mProjectionBlackList.add("Splash Screen com.android.incallui");
            mProjectionBlackList.add("com.android.incallui/com.android.incallui.InCallActivity");
            mProjectionBlackList.add("FloatAssistantView");
            mProjectionBlackList.add("MiuiFreeformBorderView");
            mProjectionBlackList.add("SnapshotStartingWindow for");
            mProjectionBlackList.add("ScreenshotThumbnail");
            mProjectionBlackList.add("com.milink.ui.activity.ScreeningConsoleWindow");
            mProjectionBlackList.add("FloatNotificationPanel");
            mProjectionBlackList.add("com.tencent.mobileqq/com.tencent.av.ui.AVActivity");
            mProjectionBlackList.add("com.tencent.mobileqq/com.tencent.av.ui.AVLoadingDialogActivity");
            mProjectionBlackList.add("com.tencent.mobileqq/com.tencent.av.ui.VideoInviteActivity");
            mProjectionBlackList.add("com.tencent.mobileqq/.FloatingWindow");
            mProjectionBlackList.add("Splash Screen com.tencent.mm");
            mProjectionBlackList.add("com.tencent.mm/com.tencent.mm.plugin.voip.ui.VideoActivity");
            mProjectionBlackList.add("com.tencent.mm/.FloatingWindow");
            mProjectionBlackList.add("com.whatsapp/com.whatsapp.voipcalling.VoipActivityV2");
            mProjectionBlackList.add("com.google.android.dialer/com.android.incallui.InCallActivity");
            mProjectionBlackList.add("com.google.android.dialer/.FloatingWindow");
            mProjectionBlackList.add("com.miui.yellowpage/com.miui.yellowpage.activity.MarkNumberActivity");
            mProjectionBlackList.add("com.miui.securitycenter/.FloatingWindow");
            mProjectionBlackList.add("com.milink.service.ui.PrivateWindow");
            mProjectionBlackList.add("com.milink.ui.activity.NFCLoadingActivity");
        }
        return mProjectionBlackList;
    }

    public static boolean getLastFrame(String name) {
        if (name.contains("Splash Screen com.android.incallui") || name.contains("com.android.incallui/com.android.incallui.InCallActivity") || name.contains("com.tencent.mobileqq/com.tencent.av.ui.AVActivity") || name.contains("com.tencent.mobileqq/com.tencent.av.ui.AVLoadingDialogActivity") || name.contains("com.tencent.mobileqq/com.tencent.av.ui.VideoInviteActivity") || name.contains("Splash Screen com.tencent.mm") || name.contains("com.tencent.mm/com.tencent.mm.plugin.voip.ui.VideoActivity") || name.contains("com.google.android.dialer/com.android.incallui.InCallActivity") || name.contains("com.whatsapp/com.whatsapp.voipcalling.VoipActivityV2")) {
            return true;
        }
        return false;
    }

    public static void setAlertWindowTitle(WindowManager.LayoutParams attrs) {
        if (WindowManager.LayoutParams.isSystemAlertWindowType(attrs.type)) {
            if (QQ.equals(attrs.packageName)) {
                attrs.setTitle(QQ_FLOATING);
            }
            if (MM.equals(attrs.packageName)) {
                attrs.setTitle(MM_FLOATING);
            }
            if (GOOGLE.equals(attrs.packageName)) {
                attrs.setTitle(GOOGLE_FLOATING);
            }
            if (SECURITY.equals(attrs.packageName)) {
                attrs.setTitle(SECURITY_FLOATING);
            }
        }
    }

    public static void registerMiuiOptimizationObserver(Context context) {
        ContentObserver observer = new ContentObserver((Handler) null) {
            public void onChange(boolean selfChange) {
                WindowManagerServiceInjector.IS_CTS_MODE = !SystemProperties.getBoolean("persist.sys.miui_optimization", !SplitScreenReporter.ACTION_ENTER_SPLIT.equals(SystemProperties.get("ro.miui.cts")));
            }
        };
        context.getContentResolver().registerContentObserver(Settings.Secure.getUriFor(MiuiSettings.Secure.MIUI_OPTIMIZATION), false, observer, -2);
        observer.onChange(false);
    }
}
