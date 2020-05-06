package com.android.server.vr;

import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.AppOpsManager;
import android.app.INotificationManager;
import android.app.NotificationManager;
import android.app.Vr2dDisplayProperties;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.hardware.display.DisplayManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.service.vr.IPersistentVrStateCallbacks;
import android.service.vr.IVrListener;
import android.service.vr.IVrManager;
import android.service.vr.IVrStateCallbacks;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.util.DumpUtils;
import com.android.server.FgThread;
import com.android.server.LocalServices;
import com.android.server.SystemConfig;
import com.android.server.SystemService;
import com.android.server.utils.ManagedApplicationService;
import com.android.server.vr.EnabledComponentsObserver;
import com.android.server.wm.ActivityTaskManagerInternal;
import com.android.server.wm.WindowManagerInternal;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Objects;

public class VrManagerService extends SystemService implements EnabledComponentsObserver.EnabledComponentChangeListener, ActivityTaskManagerInternal.ScreenObserver {
    static final boolean DBG = false;
    private static final int EVENT_LOG_SIZE = 64;
    private static final int FLAG_ALL = 7;
    private static final int FLAG_AWAKE = 1;
    private static final int FLAG_KEYGUARD_UNLOCKED = 4;
    private static final int FLAG_NONE = 0;
    private static final int FLAG_SCREEN_ON = 2;
    private static final int INVALID_APPOPS_MODE = -1;
    private static final int MSG_PENDING_VR_STATE_CHANGE = 1;
    private static final int MSG_PERSISTENT_VR_MODE_STATE_CHANGE = 2;
    private static final int MSG_VR_STATE_CHANGE = 0;
    private static final int PENDING_STATE_DELAY_MS = 300;
    public static final String TAG = "VrManagerService";
    private static final ManagedApplicationService.BinderChecker sBinderChecker = new ManagedApplicationService.BinderChecker() {
        public IInterface asInterface(IBinder binder) {
            return IVrListener.Stub.asInterface(binder);
        }

        public boolean checkType(IInterface service) {
            return service instanceof IVrListener;
        }
    };
    /* access modifiers changed from: private */
    public boolean mBootsToVr;
    /* access modifiers changed from: private */
    public EnabledComponentsObserver mComponentObserver;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public ManagedApplicationService mCurrentVrCompositorService;
    private ComponentName mCurrentVrModeComponent;
    /* access modifiers changed from: private */
    public int mCurrentVrModeUser;
    /* access modifiers changed from: private */
    public ManagedApplicationService mCurrentVrService;
    private ComponentName mDefaultVrService;
    private final ManagedApplicationService.EventCallback mEventCallback = new ManagedApplicationService.EventCallback() {
        public void onServiceEvent(ManagedApplicationService.LogEvent event) {
            ComponentName component;
            VrManagerService.this.logEvent(event);
            synchronized (VrManagerService.this.mLock) {
                component = VrManagerService.this.mCurrentVrService == null ? null : VrManagerService.this.mCurrentVrService.getComponent();
                if (component != null && component.equals(event.component) && (event.event == 2 || event.event == 3)) {
                    VrManagerService.this.callFocusedActivityChangedLocked();
                }
            }
            if (!VrManagerService.this.mBootsToVr && event.event == 4) {
                if (component == null || component.equals(event.component)) {
                    Slog.e(VrManagerService.TAG, "VrListenerSevice has died permanently, leaving system VR mode.");
                    VrManagerService.this.setPersistentVrModeEnabled(false);
                }
            }
        }
    };
    private boolean mGuard;
    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            int i = msg.what;
            boolean z = false;
            if (i == 0) {
                if (msg.arg1 == 1) {
                    z = true;
                }
                boolean state = z;
                int i2 = VrManagerService.this.mVrStateRemoteCallbacks.beginBroadcast();
                while (i2 > 0) {
                    i2--;
                    try {
                        VrManagerService.this.mVrStateRemoteCallbacks.getBroadcastItem(i2).onVrStateChanged(state);
                    } catch (RemoteException e) {
                    }
                }
                VrManagerService.this.mVrStateRemoteCallbacks.finishBroadcast();
            } else if (i == 1) {
                synchronized (VrManagerService.this.mLock) {
                    if (VrManagerService.this.mVrModeAllowed) {
                        VrManagerService.this.consumeAndApplyPendingStateLocked();
                    }
                }
            } else if (i == 2) {
                if (msg.arg1 == 1) {
                    z = true;
                }
                boolean state2 = z;
                int i3 = VrManagerService.this.mPersistentVrStateRemoteCallbacks.beginBroadcast();
                while (i3 > 0) {
                    i3--;
                    try {
                        VrManagerService.this.mPersistentVrStateRemoteCallbacks.getBroadcastItem(i3).onPersistentVrStateChanged(state2);
                    } catch (RemoteException e2) {
                    }
                }
                VrManagerService.this.mPersistentVrStateRemoteCallbacks.finishBroadcast();
            } else {
                throw new IllegalStateException("Unknown message type: " + msg.what);
            }
        }
    };
    /* access modifiers changed from: private */
    public final Object mLock = new Object();
    private boolean mLogLimitHit;
    private final ArrayDeque<ManagedApplicationService.LogFormattable> mLoggingDeque = new ArrayDeque<>(64);
    private final NotificationAccessManager mNotifAccessManager = new NotificationAccessManager();
    private INotificationManager mNotificationManager;
    private final IBinder mOverlayToken = new Binder();
    private VrState mPendingState;
    /* access modifiers changed from: private */
    public boolean mPersistentVrModeEnabled;
    /* access modifiers changed from: private */
    public final RemoteCallbackList<IPersistentVrStateCallbacks> mPersistentVrStateRemoteCallbacks = new RemoteCallbackList<>();
    private int mPreviousCoarseLocationMode = -1;
    private int mPreviousManageOverlayMode = -1;
    private boolean mRunning2dInVr;
    private boolean mStandby;
    private int mSystemSleepFlags = 5;
    private boolean mUseStandbyToExitVrMode;
    private boolean mUserUnlocked;
    private Vr2dDisplay mVr2dDisplay;
    private int mVrAppProcessId;
    private final IVrManager mVrManager = new IVrManager.Stub() {
        public void registerListener(IVrStateCallbacks cb) {
            VrManagerService.this.enforceCallerPermissionAnyOf("android.permission.ACCESS_VR_MANAGER", "android.permission.ACCESS_VR_STATE");
            if (cb != null) {
                VrManagerService.this.addStateCallback(cb);
                return;
            }
            throw new IllegalArgumentException("Callback binder object is null.");
        }

        public void unregisterListener(IVrStateCallbacks cb) {
            VrManagerService.this.enforceCallerPermissionAnyOf("android.permission.ACCESS_VR_MANAGER", "android.permission.ACCESS_VR_STATE");
            if (cb != null) {
                VrManagerService.this.removeStateCallback(cb);
                return;
            }
            throw new IllegalArgumentException("Callback binder object is null.");
        }

        public void registerPersistentVrStateListener(IPersistentVrStateCallbacks cb) {
            VrManagerService.this.enforceCallerPermissionAnyOf("android.permission.ACCESS_VR_MANAGER", "android.permission.ACCESS_VR_STATE");
            if (cb != null) {
                VrManagerService.this.addPersistentStateCallback(cb);
                return;
            }
            throw new IllegalArgumentException("Callback binder object is null.");
        }

        public void unregisterPersistentVrStateListener(IPersistentVrStateCallbacks cb) {
            VrManagerService.this.enforceCallerPermissionAnyOf("android.permission.ACCESS_VR_MANAGER", "android.permission.ACCESS_VR_STATE");
            if (cb != null) {
                VrManagerService.this.removePersistentStateCallback(cb);
                return;
            }
            throw new IllegalArgumentException("Callback binder object is null.");
        }

        public boolean getVrModeState() {
            VrManagerService.this.enforceCallerPermissionAnyOf("android.permission.ACCESS_VR_MANAGER", "android.permission.ACCESS_VR_STATE");
            return VrManagerService.this.getVrMode();
        }

        public boolean getPersistentVrModeEnabled() {
            VrManagerService.this.enforceCallerPermissionAnyOf("android.permission.ACCESS_VR_MANAGER", "android.permission.ACCESS_VR_STATE");
            return VrManagerService.this.getPersistentVrMode();
        }

        public void setPersistentVrModeEnabled(boolean enabled) {
            VrManagerService.this.enforceCallerPermissionAnyOf("android.permission.RESTRICTED_VR_ACCESS");
            VrManagerService.this.setPersistentVrModeEnabled(enabled);
        }

        public void setVr2dDisplayProperties(Vr2dDisplayProperties vr2dDisplayProp) {
            VrManagerService.this.enforceCallerPermissionAnyOf("android.permission.RESTRICTED_VR_ACCESS");
            VrManagerService.this.setVr2dDisplayProperties(vr2dDisplayProp);
        }

        public int getVr2dDisplayId() {
            return VrManagerService.this.getVr2dDisplayId();
        }

        public void setAndBindCompositor(String componentName) {
            VrManagerService.this.enforceCallerPermissionAnyOf("android.permission.RESTRICTED_VR_ACCESS");
            VrManagerService.this.setAndBindCompositor(componentName == null ? null : ComponentName.unflattenFromString(componentName));
        }

        public void setStandbyEnabled(boolean standby) {
            VrManagerService.this.enforceCallerPermissionAnyOf("android.permission.ACCESS_VR_MANAGER");
            VrManagerService.this.setStandbyEnabled(standby);
        }

        /* access modifiers changed from: protected */
        public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
            String str;
            String str2;
            if (DumpUtils.checkDumpPermission(VrManagerService.this.mContext, VrManagerService.TAG, pw)) {
                pw.println("********* Dump of VrManagerService *********");
                StringBuilder sb = new StringBuilder();
                sb.append("VR mode is currently: ");
                sb.append(VrManagerService.this.mVrModeAllowed ? "allowed" : "disallowed");
                pw.println(sb.toString());
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Persistent VR mode is currently: ");
                sb2.append(VrManagerService.this.mPersistentVrModeEnabled ? "enabled" : "disabled");
                pw.println(sb2.toString());
                StringBuilder sb3 = new StringBuilder();
                sb3.append("Currently bound VR listener service: ");
                if (VrManagerService.this.mCurrentVrService == null) {
                    str = "None";
                } else {
                    str = VrManagerService.this.mCurrentVrService.getComponent().flattenToString();
                }
                sb3.append(str);
                pw.println(sb3.toString());
                StringBuilder sb4 = new StringBuilder();
                sb4.append("Currently bound VR compositor service: ");
                if (VrManagerService.this.mCurrentVrCompositorService == null) {
                    str2 = "None";
                } else {
                    str2 = VrManagerService.this.mCurrentVrCompositorService.getComponent().flattenToString();
                }
                sb4.append(str2);
                pw.println(sb4.toString());
                pw.println("Previous state transitions:\n");
                VrManagerService.this.dumpStateTransitions(pw);
                pw.println("\n\nRemote Callbacks:");
                int i = VrManagerService.this.mVrStateRemoteCallbacks.beginBroadcast();
                while (true) {
                    int i2 = i - 1;
                    if (i <= 0) {
                        break;
                    }
                    pw.print("  ");
                    pw.print(VrManagerService.this.mVrStateRemoteCallbacks.getBroadcastItem(i2));
                    if (i2 > 0) {
                        pw.println(",");
                    }
                    i = i2;
                }
                VrManagerService.this.mVrStateRemoteCallbacks.finishBroadcast();
                pw.println("\n\nPersistent Vr State Remote Callbacks:");
                int i3 = VrManagerService.this.mPersistentVrStateRemoteCallbacks.beginBroadcast();
                while (true) {
                    int i4 = i3 - 1;
                    if (i3 <= 0) {
                        break;
                    }
                    pw.print("  ");
                    pw.print(VrManagerService.this.mPersistentVrStateRemoteCallbacks.getBroadcastItem(i4));
                    if (i4 > 0) {
                        pw.println(",");
                    }
                    i3 = i4;
                }
                VrManagerService.this.mPersistentVrStateRemoteCallbacks.finishBroadcast();
                pw.println("\n");
                pw.println("Installed VrListenerService components:");
                int userId = VrManagerService.this.mCurrentVrModeUser;
                ArraySet<ComponentName> installed = VrManagerService.this.mComponentObserver.getInstalled(userId);
                if (installed == null || installed.size() == 0) {
                    pw.println("None");
                } else {
                    Iterator<ComponentName> it = installed.iterator();
                    while (it.hasNext()) {
                        pw.print("  ");
                        pw.println(it.next().flattenToString());
                    }
                }
                pw.println("Enabled VrListenerService components:");
                ArraySet<ComponentName> enabled = VrManagerService.this.mComponentObserver.getEnabled(userId);
                if (enabled == null || enabled.size() == 0) {
                    pw.println("None");
                } else {
                    Iterator<ComponentName> it2 = enabled.iterator();
                    while (it2.hasNext()) {
                        pw.print("  ");
                        pw.println(it2.next().flattenToString());
                    }
                }
                pw.println("\n");
                pw.println("********* End of VrManagerService Dump *********");
            }
        }
    };
    /* access modifiers changed from: private */
    public boolean mVrModeAllowed;
    private boolean mVrModeEnabled;
    /* access modifiers changed from: private */
    public final RemoteCallbackList<IVrStateCallbacks> mVrStateRemoteCallbacks = new RemoteCallbackList<>();
    private boolean mWasDefaultGranted;

    private static native void initializeNative();

    private static native void setVrModeNative(boolean z);

    private void updateVrModeAllowedLocked() {
        VrState vrState;
        ManagedApplicationService managedApplicationService;
        boolean allowed = (this.mSystemSleepFlags == 7 || (this.mBootsToVr && this.mUseStandbyToExitVrMode)) && this.mUserUnlocked && !(this.mStandby && this.mUseStandbyToExitVrMode);
        if (this.mVrModeAllowed != allowed) {
            this.mVrModeAllowed = allowed;
            if (this.mVrModeAllowed) {
                if (this.mBootsToVr) {
                    setPersistentVrModeEnabled(true);
                }
                if (this.mBootsToVr && !this.mVrModeEnabled) {
                    setVrMode(true, this.mDefaultVrService, 0, -1, (ComponentName) null);
                    return;
                }
                return;
            }
            setPersistentModeAndNotifyListenersLocked(false);
            boolean z = this.mVrModeEnabled;
            if (!z || (managedApplicationService = this.mCurrentVrService) == null) {
                vrState = null;
            } else {
                vrState = new VrState(z, this.mRunning2dInVr, managedApplicationService.getComponent(), this.mCurrentVrService.getUserId(), this.mVrAppProcessId, this.mCurrentVrModeComponent);
            }
            this.mPendingState = vrState;
            updateCurrentVrServiceLocked(false, false, (ComponentName) null, 0, -1, (ComponentName) null);
        }
    }

    /* access modifiers changed from: private */
    public void setScreenOn(boolean isScreenOn) {
        setSystemState(2, isScreenOn);
    }

    public void onAwakeStateChanged(boolean isAwake) {
        setSystemState(1, isAwake);
    }

    public void onKeyguardStateChanged(boolean isShowing) {
        setSystemState(4, !isShowing);
    }

    private void setSystemState(int flags, boolean isOn) {
        synchronized (this.mLock) {
            int oldState = this.mSystemSleepFlags;
            if (isOn) {
                this.mSystemSleepFlags |= flags;
            } else {
                this.mSystemSleepFlags &= ~flags;
            }
            if (oldState != this.mSystemSleepFlags) {
                updateVrModeAllowedLocked();
            }
        }
    }

    private String getStateAsString() {
        StringBuilder sb = new StringBuilder();
        String str = "";
        sb.append((this.mSystemSleepFlags & 1) != 0 ? "awake, " : str);
        sb.append((this.mSystemSleepFlags & 2) != 0 ? "screen_on, " : str);
        if ((this.mSystemSleepFlags & 4) != 0) {
            str = "keyguard_off";
        }
        sb.append(str);
        return sb.toString();
    }

    /* access modifiers changed from: private */
    public void setUserUnlocked() {
        synchronized (this.mLock) {
            this.mUserUnlocked = true;
            updateVrModeAllowedLocked();
        }
    }

    /* access modifiers changed from: private */
    public void setStandbyEnabled(boolean standby) {
        synchronized (this.mLock) {
            if (!this.mBootsToVr) {
                Slog.e(TAG, "Attempting to set standby mode on a non-standalone device");
                return;
            }
            this.mStandby = standby;
            updateVrModeAllowedLocked();
        }
    }

    private static class SettingEvent implements ManagedApplicationService.LogFormattable {
        public final long timestamp = System.currentTimeMillis();
        public final String what;

        SettingEvent(String what2) {
            this.what = what2;
        }

        public String toLogString(SimpleDateFormat dateFormat) {
            return dateFormat.format(new Date(this.timestamp)) + "   " + this.what;
        }
    }

    private static class VrState implements ManagedApplicationService.LogFormattable {
        final ComponentName callingPackage;
        final boolean defaultPermissionsGranted;
        final boolean enabled;
        final int processId;
        final boolean running2dInVr;
        final ComponentName targetPackageName;
        final long timestamp;
        final int userId;

        VrState(boolean enabled2, boolean running2dInVr2, ComponentName targetPackageName2, int userId2, int processId2, ComponentName callingPackage2) {
            this.enabled = enabled2;
            this.running2dInVr = running2dInVr2;
            this.userId = userId2;
            this.processId = processId2;
            this.targetPackageName = targetPackageName2;
            this.callingPackage = callingPackage2;
            this.defaultPermissionsGranted = false;
            this.timestamp = System.currentTimeMillis();
        }

        VrState(boolean enabled2, boolean running2dInVr2, ComponentName targetPackageName2, int userId2, int processId2, ComponentName callingPackage2, boolean defaultPermissionsGranted2) {
            this.enabled = enabled2;
            this.running2dInVr = running2dInVr2;
            this.userId = userId2;
            this.processId = processId2;
            this.targetPackageName = targetPackageName2;
            this.callingPackage = callingPackage2;
            this.defaultPermissionsGranted = defaultPermissionsGranted2;
            this.timestamp = System.currentTimeMillis();
        }

        public String toLogString(SimpleDateFormat dateFormat) {
            StringBuilder sb = new StringBuilder(dateFormat.format(new Date(this.timestamp)));
            sb.append("  ");
            sb.append("State changed to:");
            sb.append("  ");
            sb.append(this.enabled ? "ENABLED" : "DISABLED");
            sb.append("\n");
            if (this.enabled) {
                sb.append("  ");
                sb.append("User=");
                sb.append(this.userId);
                sb.append("\n");
                sb.append("  ");
                sb.append("Current VR Activity=");
                ComponentName componentName = this.callingPackage;
                String str = "None";
                sb.append(componentName == null ? str : componentName.flattenToString());
                sb.append("\n");
                sb.append("  ");
                sb.append("Bound VrListenerService=");
                ComponentName componentName2 = this.targetPackageName;
                if (componentName2 != null) {
                    str = componentName2.flattenToString();
                }
                sb.append(str);
                sb.append("\n");
                if (this.defaultPermissionsGranted) {
                    sb.append("  ");
                    sb.append("Default permissions granted to the bound VrListenerService.");
                    sb.append("\n");
                }
            }
            return sb.toString();
        }
    }

    private final class NotificationAccessManager {
        private final SparseArray<ArraySet<String>> mAllowedPackages;
        private final ArrayMap<String, Integer> mNotificationAccessPackageToUserId;

        private NotificationAccessManager() {
            this.mAllowedPackages = new SparseArray<>();
            this.mNotificationAccessPackageToUserId = new ArrayMap<>();
        }

        public void update(Collection<String> packageNames) {
            int currentUserId = ActivityManager.getCurrentUser();
            ArraySet<String> allowed = this.mAllowedPackages.get(currentUserId);
            if (allowed == null) {
                allowed = new ArraySet<>();
            }
            for (int i = this.mNotificationAccessPackageToUserId.size() - 1; i >= 0; i--) {
                int grantUserId = this.mNotificationAccessPackageToUserId.valueAt(i).intValue();
                if (grantUserId != currentUserId) {
                    String packageName = this.mNotificationAccessPackageToUserId.keyAt(i);
                    VrManagerService.this.revokeNotificationListenerAccess(packageName, grantUserId);
                    VrManagerService.this.revokeNotificationPolicyAccess(packageName);
                    VrManagerService.this.revokeCoarseLocationPermissionIfNeeded(packageName, grantUserId);
                    this.mNotificationAccessPackageToUserId.removeAt(i);
                }
            }
            Iterator<String> it = allowed.iterator();
            while (it.hasNext()) {
                String pkg = it.next();
                if (!packageNames.contains(pkg)) {
                    VrManagerService.this.revokeNotificationListenerAccess(pkg, currentUserId);
                    VrManagerService.this.revokeNotificationPolicyAccess(pkg);
                    VrManagerService.this.revokeCoarseLocationPermissionIfNeeded(pkg, currentUserId);
                    this.mNotificationAccessPackageToUserId.remove(pkg);
                }
            }
            for (String pkg2 : packageNames) {
                if (!allowed.contains(pkg2)) {
                    VrManagerService.this.grantNotificationPolicyAccess(pkg2);
                    VrManagerService.this.grantNotificationListenerAccess(pkg2, currentUserId);
                    VrManagerService.this.grantCoarseLocationPermissionIfNeeded(pkg2, currentUserId);
                    this.mNotificationAccessPackageToUserId.put(pkg2, Integer.valueOf(currentUserId));
                }
            }
            allowed.clear();
            allowed.addAll(packageNames);
            this.mAllowedPackages.put(currentUserId, allowed);
        }
    }

    public void onEnabledComponentChanged() {
        synchronized (this.mLock) {
            ArraySet<ComponentName> enabledListeners = this.mComponentObserver.getEnabled(ActivityManager.getCurrentUser());
            ArraySet<String> enabledPackages = new ArraySet<>();
            Iterator<ComponentName> it = enabledListeners.iterator();
            while (it.hasNext()) {
                ComponentName n = it.next();
                if (isDefaultAllowed(n.getPackageName())) {
                    enabledPackages.add(n.getPackageName());
                }
            }
            this.mNotifAccessManager.update(enabledPackages);
            if (this.mVrModeAllowed) {
                consumeAndApplyPendingStateLocked(false);
                if (this.mCurrentVrService != null) {
                    updateCurrentVrServiceLocked(this.mVrModeEnabled, this.mRunning2dInVr, this.mCurrentVrService.getComponent(), this.mCurrentVrService.getUserId(), this.mVrAppProcessId, this.mCurrentVrModeComponent);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void enforceCallerPermissionAnyOf(String... permissions) {
        int length = permissions.length;
        int i = 0;
        while (i < length) {
            if (this.mContext.checkCallingOrSelfPermission(permissions[i]) != 0) {
                i++;
            } else {
                return;
            }
        }
        throw new SecurityException("Caller does not hold at least one of the permissions: " + Arrays.toString(permissions));
    }

    private final class LocalService extends VrManagerInternal {
        private LocalService() {
        }

        public void setVrMode(boolean enabled, ComponentName packageName, int userId, int processId, ComponentName callingPackage) {
            VrManagerService.this.setVrMode(enabled, packageName, userId, processId, callingPackage);
        }

        public void onScreenStateChanged(boolean isScreenOn) {
            VrManagerService.this.setScreenOn(isScreenOn);
        }

        public boolean isCurrentVrListener(String packageName, int userId) {
            return VrManagerService.this.isCurrentVrListener(packageName, userId);
        }

        public int hasVrPackage(ComponentName packageName, int userId) {
            return VrManagerService.this.hasVrPackage(packageName, userId);
        }

        public void setPersistentVrModeEnabled(boolean enabled) {
            VrManagerService.this.setPersistentVrModeEnabled(enabled);
        }

        public void setVr2dDisplayProperties(Vr2dDisplayProperties compatDisplayProp) {
            VrManagerService.this.setVr2dDisplayProperties(compatDisplayProp);
        }

        public int getVr2dDisplayId() {
            return VrManagerService.this.getVr2dDisplayId();
        }

        public void addPersistentVrModeStateListener(IPersistentVrStateCallbacks listener) {
            VrManagerService.this.addPersistentStateCallback(listener);
        }
    }

    public VrManagerService(Context context) {
        super(context);
    }

    public void onStart() {
        synchronized (this.mLock) {
            initializeNative();
            this.mContext = getContext();
        }
        boolean z = false;
        this.mBootsToVr = SystemProperties.getBoolean("ro.boot.vr", false);
        if (this.mBootsToVr && SystemProperties.getBoolean("persist.vr.use_standby_to_exit_vr_mode", true)) {
            z = true;
        }
        this.mUseStandbyToExitVrMode = z;
        publishLocalService(VrManagerInternal.class, new LocalService());
        publishBinderService("vrmanager", this.mVrManager.asBinder());
    }

    public void onBootPhase(int phase) {
        if (phase == 500) {
            ((ActivityTaskManagerInternal) LocalServices.getService(ActivityTaskManagerInternal.class)).registerScreenObserver(this);
            this.mNotificationManager = INotificationManager.Stub.asInterface(ServiceManager.getService("notification"));
            synchronized (this.mLock) {
                Looper looper = Looper.getMainLooper();
                Handler handler = new Handler(looper);
                ArrayList arrayList = new ArrayList();
                arrayList.add(this);
                this.mComponentObserver = EnabledComponentsObserver.build(this.mContext, handler, "enabled_vr_listeners", looper, "android.permission.BIND_VR_LISTENER_SERVICE", "android.service.vr.VrListenerService", this.mLock, arrayList);
                this.mComponentObserver.rebuildAll();
            }
            ArraySet<ComponentName> defaultVrComponents = SystemConfig.getInstance().getDefaultVrComponents();
            if (defaultVrComponents.size() > 0) {
                this.mDefaultVrService = defaultVrComponents.valueAt(0);
            } else {
                Slog.i(TAG, "No default vr listener service found.");
            }
            this.mVr2dDisplay = new Vr2dDisplay((DisplayManager) getContext().getSystemService("display"), (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class), (WindowManagerInternal) LocalServices.getService(WindowManagerInternal.class), this.mVrManager);
            this.mVr2dDisplay.init(getContext(), this.mBootsToVr);
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.USER_UNLOCKED");
            getContext().registerReceiver(new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    if ("android.intent.action.USER_UNLOCKED".equals(intent.getAction())) {
                        VrManagerService.this.setUserUnlocked();
                    }
                }
            }, intentFilter);
        }
    }

    public void onStartUser(int userHandle) {
        synchronized (this.mLock) {
            this.mComponentObserver.onUsersChanged();
        }
    }

    public void onSwitchUser(int userHandle) {
        FgThread.getHandler().post(new Runnable() {
            public final void run() {
                VrManagerService.this.lambda$onSwitchUser$0$VrManagerService();
            }
        });
    }

    public /* synthetic */ void lambda$onSwitchUser$0$VrManagerService() {
        synchronized (this.mLock) {
            this.mComponentObserver.onUsersChanged();
        }
    }

    public void onStopUser(int userHandle) {
        synchronized (this.mLock) {
            this.mComponentObserver.onUsersChanged();
        }
    }

    public void onCleanupUser(int userHandle) {
        synchronized (this.mLock) {
            this.mComponentObserver.onUsersChanged();
        }
    }

    private void updateOverlayStateLocked(String exemptedPackage, int newUserId, int oldUserId) {
        String[] exemptions;
        AppOpsManager appOpsManager = (AppOpsManager) getContext().getSystemService(AppOpsManager.class);
        if (oldUserId != newUserId) {
            appOpsManager.setUserRestrictionForUser(24, false, this.mOverlayToken, (String[]) null, oldUserId);
        }
        if (exemptedPackage == null) {
            exemptions = new String[0];
        } else {
            exemptions = new String[]{exemptedPackage};
        }
        appOpsManager.setUserRestrictionForUser(24, this.mVrModeEnabled, this.mOverlayToken, exemptions, newUserId);
    }

    private void updateDependentAppOpsLocked(String newVrServicePackage, int newUserId, String oldVrServicePackage, int oldUserId) {
        if (!Objects.equals(newVrServicePackage, oldVrServicePackage)) {
            long identity = Binder.clearCallingIdentity();
            try {
                updateOverlayStateLocked(newVrServicePackage, newUserId, oldUserId);
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:49:0x00ee, code lost:
        if (r1.mPersistentVrModeEnabled != false) goto L_0x00f0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x00f6, code lost:
        if (java.util.Objects.equals(r5, r1.mCurrentVrModeComponent) != false) goto L_0x00f8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x00fa, code lost:
        if (r1.mRunning2dInVr == r2) goto L_0x00fd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x00fc, code lost:
        r6 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x00fd, code lost:
        r1.mCurrentVrModeComponent = r5;
        r1.mRunning2dInVr = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:?, code lost:
        r1.mVrAppProcessId = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x0107, code lost:
        if (r1.mCurrentVrModeUser == r4) goto L_0x010c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x0109, code lost:
        r1.mCurrentVrModeUser = r4;
        r6 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x010e, code lost:
        if (r1.mCurrentVrService == null) goto L_0x011c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x0110, code lost:
        r11 = r1.mCurrentVrService.getComponent().getPackageName();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x011c, code lost:
        updateDependentAppOpsLocked(r11, r1.mCurrentVrModeUser, r10, r12);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x0123, code lost:
        if (r1.mCurrentVrService == null) goto L_0x012a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x0125, code lost:
        if (r6 == false) goto L_0x012a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x0127, code lost:
        callFocusedActivityChangedLocked();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x012a, code lost:
        if (r13 != false) goto L_0x012f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x012c, code lost:
        logStateLocked();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x012f, code lost:
        android.os.Binder.restoreCallingIdentity(r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:0x0133, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x0134, code lost:
        r0 = th;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean updateCurrentVrServiceLocked(boolean r18, boolean r19, android.content.ComponentName r20, int r21, int r22, android.content.ComponentName r23) {
        /*
            r17 = this;
            r1 = r17
            r2 = r19
            r3 = r20
            r4 = r21
            r5 = r23
            r6 = 0
            long r7 = android.os.Binder.clearCallingIdentity()
            com.android.server.vr.EnabledComponentsObserver r0 = r1.mComponentObserver     // Catch:{ all -> 0x013b }
            int r0 = r0.isValid(r3, r4)     // Catch:{ all -> 0x013b }
            r9 = 1
            r10 = 0
            if (r0 != 0) goto L_0x001b
            r0 = r9
            goto L_0x001c
        L_0x001b:
            r0 = r10
        L_0x001c:
            if (r0 == 0) goto L_0x0021
            if (r18 == 0) goto L_0x0021
            goto L_0x0022
        L_0x0021:
            r9 = r10
        L_0x0022:
            boolean r10 = r1.mVrModeEnabled     // Catch:{ all -> 0x013b }
            if (r10 != 0) goto L_0x002d
            if (r9 != 0) goto L_0x002d
            android.os.Binder.restoreCallingIdentity(r7)
            return r0
        L_0x002d:
            com.android.server.utils.ManagedApplicationService r10 = r1.mCurrentVrService     // Catch:{ all -> 0x013b }
            if (r10 == 0) goto L_0x0043
            com.android.server.utils.ManagedApplicationService r10 = r1.mCurrentVrService     // Catch:{ all -> 0x003c }
            android.content.ComponentName r10 = r10.getComponent()     // Catch:{ all -> 0x003c }
            java.lang.String r10 = r10.getPackageName()     // Catch:{ all -> 0x003c }
            goto L_0x0044
        L_0x003c:
            r0 = move-exception
            r14 = r22
        L_0x003f:
            r16 = r6
            goto L_0x0140
        L_0x0043:
            r10 = 0
        L_0x0044:
            int r12 = r1.mCurrentVrModeUser     // Catch:{ all -> 0x013b }
            r1.changeVrModeLocked(r9)     // Catch:{ all -> 0x013b }
            r13 = 0
            java.lang.String r15 = " for user "
            java.lang.String r11 = "VrManagerService"
            if (r9 != 0) goto L_0x0099
            com.android.server.utils.ManagedApplicationService r14 = r1.mCurrentVrService     // Catch:{ all -> 0x0092 }
            if (r14 == 0) goto L_0x008d
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ all -> 0x0092 }
            r14.<init>()     // Catch:{ all -> 0x0092 }
            r16 = r6
            java.lang.String r6 = "Leaving VR mode, disconnecting "
            r14.append(r6)     // Catch:{ all -> 0x0137 }
            com.android.server.utils.ManagedApplicationService r6 = r1.mCurrentVrService     // Catch:{ all -> 0x0137 }
            android.content.ComponentName r6 = r6.getComponent()     // Catch:{ all -> 0x0137 }
            r14.append(r6)     // Catch:{ all -> 0x0137 }
            r14.append(r15)     // Catch:{ all -> 0x0137 }
            com.android.server.utils.ManagedApplicationService r6 = r1.mCurrentVrService     // Catch:{ all -> 0x0137 }
            int r6 = r6.getUserId()     // Catch:{ all -> 0x0137 }
            r14.append(r6)     // Catch:{ all -> 0x0137 }
            java.lang.String r6 = r14.toString()     // Catch:{ all -> 0x0137 }
            android.util.Slog.i(r11, r6)     // Catch:{ all -> 0x0137 }
            com.android.server.utils.ManagedApplicationService r6 = r1.mCurrentVrService     // Catch:{ all -> 0x0137 }
            r6.disconnect()     // Catch:{ all -> 0x0137 }
            r6 = -10000(0xffffffffffffd8f0, float:NaN)
            r11 = 0
            r1.updateCompositorServiceLocked(r6, r11)     // Catch:{ all -> 0x0137 }
            r1.mCurrentVrService = r11     // Catch:{ all -> 0x0137 }
            r6 = r16
            r11 = 0
            goto L_0x00ea
        L_0x008d:
            r16 = r6
            r13 = 1
            r11 = 0
            goto L_0x00ea
        L_0x0092:
            r0 = move-exception
            r16 = r6
            r14 = r22
            goto L_0x0140
        L_0x0099:
            r16 = r6
            com.android.server.utils.ManagedApplicationService r6 = r1.mCurrentVrService     // Catch:{ all -> 0x0137 }
            if (r6 == 0) goto L_0x00e5
            com.android.server.utils.ManagedApplicationService r6 = r1.mCurrentVrService     // Catch:{ all -> 0x0137 }
            boolean r6 = r6.disconnectIfNotMatching(r3, r4)     // Catch:{ all -> 0x0137 }
            if (r6 == 0) goto L_0x00e0
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x0137 }
            r6.<init>()     // Catch:{ all -> 0x0137 }
            java.lang.String r14 = "VR mode component changed to "
            r6.append(r14)     // Catch:{ all -> 0x0137 }
            r6.append(r3)     // Catch:{ all -> 0x0137 }
            java.lang.String r14 = ", disconnecting "
            r6.append(r14)     // Catch:{ all -> 0x0137 }
            com.android.server.utils.ManagedApplicationService r14 = r1.mCurrentVrService     // Catch:{ all -> 0x0137 }
            android.content.ComponentName r14 = r14.getComponent()     // Catch:{ all -> 0x0137 }
            r6.append(r14)     // Catch:{ all -> 0x0137 }
            r6.append(r15)     // Catch:{ all -> 0x0137 }
            com.android.server.utils.ManagedApplicationService r14 = r1.mCurrentVrService     // Catch:{ all -> 0x0137 }
            int r14 = r14.getUserId()     // Catch:{ all -> 0x0137 }
            r6.append(r14)     // Catch:{ all -> 0x0137 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x0137 }
            android.util.Slog.i(r11, r6)     // Catch:{ all -> 0x0137 }
            r6 = -10000(0xffffffffffffd8f0, float:NaN)
            r11 = 0
            r1.updateCompositorServiceLocked(r6, r11)     // Catch:{ all -> 0x0137 }
            r1.createAndConnectService(r3, r4)     // Catch:{ all -> 0x0137 }
            r6 = 1
            goto L_0x00ea
        L_0x00e0:
            r11 = 0
            r13 = 1
            r6 = r16
            goto L_0x00ea
        L_0x00e5:
            r11 = 0
            r1.createAndConnectService(r3, r4)     // Catch:{ all -> 0x0137 }
            r6 = 1
        L_0x00ea:
            if (r5 != 0) goto L_0x00f0
            boolean r14 = r1.mPersistentVrModeEnabled     // Catch:{ all -> 0x003c }
            if (r14 == 0) goto L_0x00f8
        L_0x00f0:
            android.content.ComponentName r14 = r1.mCurrentVrModeComponent     // Catch:{ all -> 0x003c }
            boolean r14 = java.util.Objects.equals(r5, r14)     // Catch:{ all -> 0x003c }
            if (r14 == 0) goto L_0x00fc
        L_0x00f8:
            boolean r14 = r1.mRunning2dInVr     // Catch:{ all -> 0x003c }
            if (r14 == r2) goto L_0x00fd
        L_0x00fc:
            r6 = 1
        L_0x00fd:
            r1.mCurrentVrModeComponent = r5     // Catch:{ all -> 0x003c }
            r1.mRunning2dInVr = r2     // Catch:{ all -> 0x003c }
            r14 = r22
            r1.mVrAppProcessId = r14     // Catch:{ all -> 0x0134 }
            int r15 = r1.mCurrentVrModeUser     // Catch:{ all -> 0x0134 }
            if (r15 == r4) goto L_0x010c
            r1.mCurrentVrModeUser = r4     // Catch:{ all -> 0x0134 }
            r6 = 1
        L_0x010c:
            com.android.server.utils.ManagedApplicationService r15 = r1.mCurrentVrService     // Catch:{ all -> 0x0134 }
            if (r15 == 0) goto L_0x011b
            com.android.server.utils.ManagedApplicationService r11 = r1.mCurrentVrService     // Catch:{ all -> 0x0134 }
            android.content.ComponentName r11 = r11.getComponent()     // Catch:{ all -> 0x0134 }
            java.lang.String r11 = r11.getPackageName()     // Catch:{ all -> 0x0134 }
            goto L_0x011c
        L_0x011b:
        L_0x011c:
            int r15 = r1.mCurrentVrModeUser     // Catch:{ all -> 0x0134 }
            r1.updateDependentAppOpsLocked(r11, r15, r10, r12)     // Catch:{ all -> 0x0134 }
            com.android.server.utils.ManagedApplicationService r2 = r1.mCurrentVrService     // Catch:{ all -> 0x0134 }
            if (r2 == 0) goto L_0x012a
            if (r6 == 0) goto L_0x012a
            r17.callFocusedActivityChangedLocked()     // Catch:{ all -> 0x0134 }
        L_0x012a:
            if (r13 != 0) goto L_0x012f
            r17.logStateLocked()     // Catch:{ all -> 0x0134 }
        L_0x012f:
            android.os.Binder.restoreCallingIdentity(r7)
            return r0
        L_0x0134:
            r0 = move-exception
            goto L_0x003f
        L_0x0137:
            r0 = move-exception
            r14 = r22
            goto L_0x0140
        L_0x013b:
            r0 = move-exception
            r14 = r22
            r16 = r6
        L_0x0140:
            android.os.Binder.restoreCallingIdentity(r7)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.vr.VrManagerService.updateCurrentVrServiceLocked(boolean, boolean, android.content.ComponentName, int, int, android.content.ComponentName):boolean");
    }

    /* access modifiers changed from: private */
    public void callFocusedActivityChangedLocked() {
        final ComponentName c = this.mCurrentVrModeComponent;
        final boolean b = this.mRunning2dInVr;
        final int pid = this.mVrAppProcessId;
        this.mCurrentVrService.sendEvent(new ManagedApplicationService.PendingEvent() {
            public void runEvent(IInterface service) throws RemoteException {
                ((IVrListener) service).focusedActivityChanged(c, b, pid);
            }
        });
    }

    private boolean isDefaultAllowed(String packageName) {
        ApplicationInfo info = null;
        try {
            info = this.mContext.getPackageManager().getApplicationInfo(packageName, 128);
        } catch (PackageManager.NameNotFoundException e) {
        }
        if (info == null) {
            return false;
        }
        if (info.isSystemApp() || info.isUpdatedSystemApp()) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void grantNotificationPolicyAccess(String pkg) {
        ((NotificationManager) this.mContext.getSystemService(NotificationManager.class)).setNotificationPolicyAccessGranted(pkg, true);
    }

    /* access modifiers changed from: private */
    public void revokeNotificationPolicyAccess(String pkg) {
        NotificationManager nm = (NotificationManager) this.mContext.getSystemService(NotificationManager.class);
        nm.removeAutomaticZenRules(pkg);
        nm.setNotificationPolicyAccessGranted(pkg, false);
    }

    /* access modifiers changed from: private */
    public void grantNotificationListenerAccess(String pkg, int userId) {
        NotificationManager nm = (NotificationManager) this.mContext.getSystemService(NotificationManager.class);
        Iterator<ComponentName> it = EnabledComponentsObserver.loadComponentNames(this.mContext.getPackageManager(), userId, "android.service.notification.NotificationListenerService", "android.permission.BIND_NOTIFICATION_LISTENER_SERVICE").iterator();
        while (it.hasNext()) {
            ComponentName c = it.next();
            if (Objects.equals(c.getPackageName(), pkg)) {
                nm.setNotificationListenerAccessGrantedForUser(c, userId, true);
            }
        }
    }

    /* access modifiers changed from: private */
    public void revokeNotificationListenerAccess(String pkg, int userId) {
        NotificationManager nm = (NotificationManager) this.mContext.getSystemService(NotificationManager.class);
        for (ComponentName component : nm.getEnabledNotificationListeners(userId)) {
            if (component != null && component.getPackageName().equals(pkg)) {
                nm.setNotificationListenerAccessGrantedForUser(component, userId, false);
            }
        }
    }

    /* access modifiers changed from: private */
    public void grantCoarseLocationPermissionIfNeeded(String pkg, int userId) {
        if (!isPermissionUserUpdated("android.permission.ACCESS_COARSE_LOCATION", pkg, userId)) {
            try {
                this.mContext.getPackageManager().grantRuntimePermission(pkg, "android.permission.ACCESS_COARSE_LOCATION", new UserHandle(userId));
            } catch (IllegalArgumentException e) {
                Slog.w(TAG, "Could not grant coarse location permission, package " + pkg + " was removed.");
            }
        }
    }

    /* access modifiers changed from: private */
    public void revokeCoarseLocationPermissionIfNeeded(String pkg, int userId) {
        if (!isPermissionUserUpdated("android.permission.ACCESS_COARSE_LOCATION", pkg, userId)) {
            try {
                this.mContext.getPackageManager().revokeRuntimePermission(pkg, "android.permission.ACCESS_COARSE_LOCATION", new UserHandle(userId));
            } catch (IllegalArgumentException e) {
                Slog.w(TAG, "Could not revoke coarse location permission, package " + pkg + " was removed.");
            }
        }
    }

    private boolean isPermissionUserUpdated(String permission, String pkg, int userId) {
        return (this.mContext.getPackageManager().getPermissionFlags(permission, pkg, new UserHandle(userId)) & 3) != 0;
    }

    private ArraySet<String> getNotificationListeners(ContentResolver resolver, int userId) {
        String flat = Settings.Secure.getStringForUser(resolver, "enabled_notification_listeners", userId);
        ArraySet<String> current = new ArraySet<>();
        if (flat != null) {
            for (String s : flat.split(":")) {
                if (!TextUtils.isEmpty(s)) {
                    current.add(s);
                }
            }
        }
        return current;
    }

    private static String formatSettings(Collection<String> c) {
        if (c == null || c.isEmpty()) {
            return "";
        }
        StringBuilder b = new StringBuilder();
        boolean start = true;
        for (String s : c) {
            if (!"".equals(s)) {
                if (!start) {
                    b.append(':');
                }
                b.append(s);
                start = false;
            }
        }
        return b.toString();
    }

    private void createAndConnectService(ComponentName component, int userId) {
        this.mCurrentVrService = createVrListenerService(component, userId);
        this.mCurrentVrService.connect();
        Slog.i(TAG, "Connecting " + component + " for user " + userId);
    }

    private void changeVrModeLocked(boolean enabled) {
        if (this.mVrModeEnabled != enabled) {
            this.mVrModeEnabled = enabled;
            StringBuilder sb = new StringBuilder();
            sb.append("VR mode ");
            sb.append(this.mVrModeEnabled ? "enabled" : "disabled");
            Slog.i(TAG, sb.toString());
            setVrModeNative(this.mVrModeEnabled);
            onVrModeChangedLocked();
        }
    }

    private void onVrModeChangedLocked() {
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(0, this.mVrModeEnabled ? 1 : 0, 0));
    }

    private ManagedApplicationService createVrListenerService(ComponentName component, int userId) {
        int retryType;
        if (this.mBootsToVr) {
            retryType = 1;
        } else {
            retryType = 2;
        }
        return ManagedApplicationService.build(this.mContext, component, userId, 17041337, "android.settings.VR_LISTENER_SETTINGS", sBinderChecker, true, retryType, this.mHandler, this.mEventCallback);
    }

    private ManagedApplicationService createVrCompositorService(ComponentName component, int userId) {
        int retryType;
        if (this.mBootsToVr) {
            retryType = 1;
        } else {
            retryType = 3;
        }
        return ManagedApplicationService.build(this.mContext, component, userId, 0, (String) null, (ManagedApplicationService.BinderChecker) null, true, retryType, this.mHandler, this.mEventCallback);
    }

    /* access modifiers changed from: private */
    public void consumeAndApplyPendingStateLocked() {
        consumeAndApplyPendingStateLocked(true);
    }

    private void consumeAndApplyPendingStateLocked(boolean disconnectIfNoPendingState) {
        VrState vrState = this.mPendingState;
        if (vrState != null) {
            updateCurrentVrServiceLocked(vrState.enabled, this.mPendingState.running2dInVr, this.mPendingState.targetPackageName, this.mPendingState.userId, this.mPendingState.processId, this.mPendingState.callingPackage);
            this.mPendingState = null;
        } else if (disconnectIfNoPendingState) {
            updateCurrentVrServiceLocked(false, false, (ComponentName) null, 0, -1, (ComponentName) null);
        }
    }

    private void logStateLocked() {
        ComponentName currentBoundService;
        ManagedApplicationService managedApplicationService = this.mCurrentVrService;
        if (managedApplicationService == null) {
            currentBoundService = null;
        } else {
            currentBoundService = managedApplicationService.getComponent();
        }
        logEvent(new VrState(this.mVrModeEnabled, this.mRunning2dInVr, currentBoundService, this.mCurrentVrModeUser, this.mVrAppProcessId, this.mCurrentVrModeComponent, this.mWasDefaultGranted));
    }

    /* access modifiers changed from: private */
    public void logEvent(ManagedApplicationService.LogFormattable event) {
        synchronized (this.mLoggingDeque) {
            if (this.mLoggingDeque.size() == 64) {
                this.mLoggingDeque.removeFirst();
                this.mLogLimitHit = true;
            }
            this.mLoggingDeque.add(event);
        }
    }

    /* access modifiers changed from: private */
    public void dumpStateTransitions(PrintWriter pw) {
        SimpleDateFormat d = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
        synchronized (this.mLoggingDeque) {
            if (this.mLoggingDeque.size() == 0) {
                pw.print("  ");
                pw.println("None");
            }
            if (this.mLogLimitHit) {
                pw.println("...");
            }
            Iterator<ManagedApplicationService.LogFormattable> it = this.mLoggingDeque.iterator();
            while (it.hasNext()) {
                pw.println(it.next().toLogString(d));
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x001f A[Catch:{ all -> 0x0010 }] */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0024 A[Catch:{ all -> 0x0010 }] */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x003e A[Catch:{ all -> 0x0010 }] */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0042 A[Catch:{ all -> 0x0010 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setVrMode(boolean r20, android.content.ComponentName r21, int r22, int r23, android.content.ComponentName r24) {
        /*
            r19 = this;
            r8 = r19
            java.lang.Object r9 = r8.mLock
            monitor-enter(r9)
            r0 = 0
            r1 = 1
            if (r20 != 0) goto L_0x0013
            boolean r2 = r8.mPersistentVrModeEnabled     // Catch:{ all -> 0x0010 }
            if (r2 == 0) goto L_0x000e
            goto L_0x0013
        L_0x000e:
            r2 = r0
            goto L_0x0014
        L_0x0010:
            r0 = move-exception
            goto L_0x0072
        L_0x0013:
            r2 = r1
        L_0x0014:
            r17 = r2
            if (r20 != 0) goto L_0x001d
            boolean r2 = r8.mPersistentVrModeEnabled     // Catch:{ all -> 0x0010 }
            if (r2 == 0) goto L_0x001d
            r0 = r1
        L_0x001d:
            if (r0 == 0) goto L_0x0024
            android.content.ComponentName r2 = r8.mDefaultVrService     // Catch:{ all -> 0x0010 }
            r18 = r2
            goto L_0x0028
        L_0x0024:
            r2 = r21
            r18 = r2
        L_0x0028:
            com.android.server.vr.VrManagerService$VrState r2 = new com.android.server.vr.VrManagerService$VrState     // Catch:{ all -> 0x0010 }
            r10 = r2
            r11 = r17
            r12 = r0
            r13 = r18
            r14 = r22
            r15 = r23
            r16 = r24
            r10.<init>(r11, r12, r13, r14, r15, r16)     // Catch:{ all -> 0x0010 }
            r10 = r2
            boolean r2 = r8.mVrModeAllowed     // Catch:{ all -> 0x0010 }
            if (r2 != 0) goto L_0x0042
            r8.mPendingState = r10     // Catch:{ all -> 0x0010 }
            monitor-exit(r9)     // Catch:{ all -> 0x0010 }
            return
        L_0x0042:
            if (r17 != 0) goto L_0x0057
            com.android.server.utils.ManagedApplicationService r2 = r8.mCurrentVrService     // Catch:{ all -> 0x0010 }
            if (r2 == 0) goto L_0x0057
            com.android.server.vr.VrManagerService$VrState r2 = r8.mPendingState     // Catch:{ all -> 0x0010 }
            if (r2 != 0) goto L_0x0053
            android.os.Handler r2 = r8.mHandler     // Catch:{ all -> 0x0010 }
            r3 = 300(0x12c, double:1.48E-321)
            r2.sendEmptyMessageDelayed(r1, r3)     // Catch:{ all -> 0x0010 }
        L_0x0053:
            r8.mPendingState = r10     // Catch:{ all -> 0x0010 }
            monitor-exit(r9)     // Catch:{ all -> 0x0010 }
            return
        L_0x0057:
            android.os.Handler r2 = r8.mHandler     // Catch:{ all -> 0x0010 }
            r2.removeMessages(r1)     // Catch:{ all -> 0x0010 }
            r1 = 0
            r8.mPendingState = r1     // Catch:{ all -> 0x0010 }
            r1 = r19
            r2 = r17
            r3 = r0
            r4 = r18
            r5 = r22
            r6 = r23
            r7 = r24
            r1.updateCurrentVrServiceLocked(r2, r3, r4, r5, r6, r7)     // Catch:{ all -> 0x0010 }
            monitor-exit(r9)     // Catch:{ all -> 0x0010 }
            return
        L_0x0072:
            monitor-exit(r9)     // Catch:{ all -> 0x0010 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.vr.VrManagerService.setVrMode(boolean, android.content.ComponentName, int, int, android.content.ComponentName):void");
    }

    /* access modifiers changed from: private */
    public void setPersistentVrModeEnabled(boolean enabled) {
        synchronized (this.mLock) {
            setPersistentModeAndNotifyListenersLocked(enabled);
            if (!enabled) {
                setVrMode(false, (ComponentName) null, 0, -1, (ComponentName) null);
            }
        }
    }

    public void setVr2dDisplayProperties(Vr2dDisplayProperties compatDisplayProp) {
        long token = Binder.clearCallingIdentity();
        try {
            if (this.mVr2dDisplay != null) {
                this.mVr2dDisplay.setVirtualDisplayProperties(compatDisplayProp);
                return;
            }
            Binder.restoreCallingIdentity(token);
            Slog.w(TAG, "Vr2dDisplay is null!");
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    /* access modifiers changed from: private */
    public int getVr2dDisplayId() {
        Vr2dDisplay vr2dDisplay = this.mVr2dDisplay;
        if (vr2dDisplay != null) {
            return vr2dDisplay.getVirtualDisplayId();
        }
        Slog.w(TAG, "Vr2dDisplay is null!");
        return -1;
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    /* access modifiers changed from: private */
    public void setAndBindCompositor(ComponentName componentName) {
        int userId = UserHandle.getCallingUserId();
        long token = Binder.clearCallingIdentity();
        try {
            synchronized (this.mLock) {
                updateCompositorServiceLocked(userId, componentName);
            }
            Binder.restoreCallingIdentity(token);
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(token);
            throw th;
        }
    }

    private void updateCompositorServiceLocked(int userId, ComponentName componentName) {
        ManagedApplicationService managedApplicationService = this.mCurrentVrCompositorService;
        if (managedApplicationService != null && managedApplicationService.disconnectIfNotMatching(componentName, userId)) {
            Slog.i(TAG, "Disconnecting compositor service: " + this.mCurrentVrCompositorService.getComponent());
            this.mCurrentVrCompositorService = null;
        }
        if (componentName != null && this.mCurrentVrCompositorService == null) {
            Slog.i(TAG, "Connecting compositor service: " + componentName);
            this.mCurrentVrCompositorService = createVrCompositorService(componentName, userId);
            this.mCurrentVrCompositorService.connect();
        }
    }

    private void setPersistentModeAndNotifyListenersLocked(boolean enabled) {
        if (this.mPersistentVrModeEnabled != enabled) {
            StringBuilder sb = new StringBuilder();
            sb.append("Persistent VR mode ");
            sb.append(enabled ? "enabled" : "disabled");
            String eventName = sb.toString();
            Slog.i(TAG, eventName);
            logEvent(new SettingEvent(eventName));
            this.mPersistentVrModeEnabled = enabled;
            Handler handler = this.mHandler;
            handler.sendMessage(handler.obtainMessage(2, this.mPersistentVrModeEnabled ? 1 : 0, 0));
        }
    }

    /* access modifiers changed from: private */
    public int hasVrPackage(ComponentName targetPackageName, int userId) {
        int isValid;
        synchronized (this.mLock) {
            isValid = this.mComponentObserver.isValid(targetPackageName, userId);
        }
        return isValid;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0026, code lost:
        return r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isCurrentVrListener(java.lang.String r4, int r5) {
        /*
            r3 = this;
            java.lang.Object r0 = r3.mLock
            monitor-enter(r0)
            com.android.server.utils.ManagedApplicationService r1 = r3.mCurrentVrService     // Catch:{ all -> 0x0027 }
            r2 = 0
            if (r1 != 0) goto L_0x000a
            monitor-exit(r0)     // Catch:{ all -> 0x0027 }
            return r2
        L_0x000a:
            com.android.server.utils.ManagedApplicationService r1 = r3.mCurrentVrService     // Catch:{ all -> 0x0027 }
            android.content.ComponentName r1 = r1.getComponent()     // Catch:{ all -> 0x0027 }
            java.lang.String r1 = r1.getPackageName()     // Catch:{ all -> 0x0027 }
            boolean r1 = r1.equals(r4)     // Catch:{ all -> 0x0027 }
            if (r1 == 0) goto L_0x0024
            com.android.server.utils.ManagedApplicationService r1 = r3.mCurrentVrService     // Catch:{ all -> 0x0027 }
            int r1 = r1.getUserId()     // Catch:{ all -> 0x0027 }
            if (r5 != r1) goto L_0x0024
            r2 = 1
            goto L_0x0025
        L_0x0024:
        L_0x0025:
            monitor-exit(r0)     // Catch:{ all -> 0x0027 }
            return r2
        L_0x0027:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0027 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.vr.VrManagerService.isCurrentVrListener(java.lang.String, int):boolean");
    }

    /* access modifiers changed from: private */
    public void addStateCallback(IVrStateCallbacks cb) {
        this.mVrStateRemoteCallbacks.register(cb);
    }

    /* access modifiers changed from: private */
    public void removeStateCallback(IVrStateCallbacks cb) {
        this.mVrStateRemoteCallbacks.unregister(cb);
    }

    /* access modifiers changed from: private */
    public void addPersistentStateCallback(IPersistentVrStateCallbacks cb) {
        this.mPersistentVrStateRemoteCallbacks.register(cb);
    }

    /* access modifiers changed from: private */
    public void removePersistentStateCallback(IPersistentVrStateCallbacks cb) {
        this.mPersistentVrStateRemoteCallbacks.unregister(cb);
    }

    /* access modifiers changed from: private */
    public boolean getVrMode() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mVrModeEnabled;
        }
        return z;
    }

    /* access modifiers changed from: private */
    public boolean getPersistentVrMode() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mPersistentVrModeEnabled;
        }
        return z;
    }
}
