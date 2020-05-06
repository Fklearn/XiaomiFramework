package com.android.server;

import android.app.AppGlobals;
import android.app.AppOpsManagerInjector;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.MiuiBinderProxy;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Slog;
import android.util.SparseArray;
import java.util.HashSet;
import miui.security.SecurityManager;
import miui.util.FeatureParser;

public class AppOpsServiceState {
    public static final boolean DEBUG = false;
    public static final int FLAG_NOT_RECORD = 32;
    public static final int OPERATION_TYPE_FINISH = 3;
    public static final int OPERATION_TYPE_NOTE = 1;
    public static final int OPERATION_TYPE_START = 2;
    public static final int OPERATION_TYPE_VIRTUAL = 4;
    private static final String POWER_SAVE_MODE_OPEN = "POWER_SAVE_MODE_OPEN";
    private static final String TAG = "AppOpsServiceState";
    private static HashSet<String> sCtsIgnore = new HashSet<>();
    /* access modifiers changed from: private */
    public Context mContext;
    private int mDefaultMode = 1;
    private boolean mGreaterThanL;
    private IPackageManager mIPackageManager;
    private int mLastNotifyOp;
    private int mLastNotifyUid;
    private boolean mPowerSaving;
    private SecurityManager mSecurityManager;
    final SparseArray<UserState> mUidStates;

    public AppOpsServiceState() {
        boolean z = true;
        this.mGreaterThanL = Build.VERSION.SDK_INT <= 22 ? false : z;
        this.mUidStates = new SparseArray<>();
    }

    static {
        sCtsIgnore.add("android.app.usage.cts");
        sCtsIgnore.add("com.android.cts.usepermission");
        sCtsIgnore.add("com.android.cts.permission");
        sCtsIgnore.add("com.android.cts.netlegacy22.permission");
        sCtsIgnore.add("android.netlegacy22.permission.cts");
        sCtsIgnore.add("android.provider.cts");
        sCtsIgnore.add("android.telephony2.cts");
        sCtsIgnore.add("android.permission.cts");
        sCtsIgnore.add("com.android.cts.writeexternalstorageapp");
        sCtsIgnore.add("com.android.cts.readexternalstorageapp");
        sCtsIgnore.add("com.android.cts.externalstorageapp");
        sCtsIgnore.add("android.server.alertwindowapp");
        sCtsIgnore.add("android.server.alertwindowappsdk25");
        sCtsIgnore.add("com.android.app2");
        sCtsIgnore.add("com.android.cts.appbinding.app");
        sCtsIgnore.add("com.android.cts.launcherapps.simplepremapp");
    }

    private static final class UserState {
        Callback mCallback;
        MiuiBinderProxy mCallbackBinder;

        private UserState() {
        }
    }

    public void init(Context context) {
        this.mContext = context;
        if (FeatureParser.getBoolean("is_pad", false)) {
            this.mDefaultMode = 0;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x001c, code lost:
        return r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private synchronized com.android.server.AppOpsServiceState.UserState getUidState(int r4, boolean r5) {
        /*
            r3 = this;
            monitor-enter(r3)
            android.util.SparseArray<com.android.server.AppOpsServiceState$UserState> r0 = r3.mUidStates     // Catch:{ all -> 0x001d }
            java.lang.Object r0 = r0.get(r4)     // Catch:{ all -> 0x001d }
            com.android.server.AppOpsServiceState$UserState r0 = (com.android.server.AppOpsServiceState.UserState) r0     // Catch:{ all -> 0x001d }
            if (r0 != 0) goto L_0x001b
            r1 = 0
            if (r5 != 0) goto L_0x0010
            monitor-exit(r3)
            return r1
        L_0x0010:
            com.android.server.AppOpsServiceState$UserState r2 = new com.android.server.AppOpsServiceState$UserState     // Catch:{ all -> 0x001d }
            r2.<init>()     // Catch:{ all -> 0x001d }
            r0 = r2
            android.util.SparseArray<com.android.server.AppOpsServiceState$UserState> r1 = r3.mUidStates     // Catch:{ all -> 0x001d }
            r1.put(r4, r0)     // Catch:{ all -> 0x001d }
        L_0x001b:
            monitor-exit(r3)
            return r0
        L_0x001d:
            r4 = move-exception
            monitor-exit(r3)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.AppOpsServiceState.getUidState(int, boolean):com.android.server.AppOpsServiceState$UserState");
    }

    public synchronized void removeUser(int userHandle) {
        this.mUidStates.remove(userHandle);
    }

    public void systemReady() {
        this.mSecurityManager = (SecurityManager) this.mContext.getSystemService("security");
        this.mIPackageManager = AppGlobals.getPackageManager();
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(POWER_SAVE_MODE_OPEN), true, new ContentObserver((Handler) null) {
            public void onChange(boolean selfChange, Uri uri) {
                AppOpsServiceState.this.updatePowerState();
            }
        });
        updatePowerState();
    }

    /* access modifiers changed from: private */
    public void updatePowerState() {
        boolean z = false;
        if (Settings.System.getInt(this.mContext.getContentResolver(), POWER_SAVE_MODE_OPEN, 0) == 1) {
            z = true;
        }
        this.mPowerSaving = z;
    }

    public boolean isMiuiAllowed(int code, int uid, String packageName) {
        if (inMiuiAllowedBlackList(code)) {
            return false;
        }
        if ((code != 10008 || !AppOpsManagerInjector.isAutoStartRestriction(packageName)) && code != 10025) {
            return isSystemApp(code, uid, packageName);
        }
        return false;
    }

    public int allowedToMode(int code, int uid, String packageName) {
        int userId;
        boolean checkAutoStart = this.mPowerSaving;
        if (!(checkAutoStart || (userId = UserHandle.getUserId(uid)) == 0 || userId == 999)) {
            checkAutoStart = true;
        }
        if (!checkAutoStart || code != 10008 || !AppOpsManagerInjector.isAutoStartRestriction(packageName)) {
            return 0;
        }
        return 1;
    }

    private boolean inMiuiAllowedBlackList(int code) {
        boolean inList = false;
        if (code == 3 || code == 11 || code == 28) {
            inList = true;
        }
        if (!this.mGreaterThanL || code <= 62 || code >= 1000) {
            return inList;
        }
        return true;
    }

    private boolean isSystemApp(int code, int uid, String packageName) {
        if (UserHandle.getAppId(uid) < 10000) {
            return true;
        }
        long identity = Binder.clearCallingIdentity();
        try {
            if (this.mIPackageManager == null) {
                this.mIPackageManager = AppGlobals.getPackageManager();
            }
            ApplicationInfo info = this.mIPackageManager.getApplicationInfo(packageName, 0, UserHandle.getUserId(uid));
            if (!(info == null || (info.flags & 1) == 0)) {
                Binder.restoreCallingIdentity(identity);
                return true;
            }
        } catch (Exception e) {
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(identity);
            throw th;
        }
        Binder.restoreCallingIdentity(identity);
        return false;
    }

    public boolean isAppPermissionControlOpen(int op, int uid) {
        SecurityManager securityManager;
        if (AppOpsService.sOpInControl.get(Integer.valueOf(op)) == null || op == 10008 || (securityManager = this.mSecurityManager) == null) {
            return true;
        }
        return securityManager.getAppPermissionControlOpen(UserHandle.getUserId(uid));
    }

    public int askOperationLocked(int code, int uid, String packageName) {
        int result = this.mDefaultMode;
        if (UserHandle.getUserId(uid) == 999) {
            uid = UserHandle.getUid(0, UserHandle.getAppId(uid));
        }
        UserState uidState = getUidState(UserHandle.getUserId(uid), false);
        if (uidState == null || uidState.mCallbackBinder == null) {
            return result;
        }
        return uidState.mCallbackBinder.callTransactDefault(1, new Object[]{Integer.valueOf(uid), packageName, Integer.valueOf(code)});
    }

    public int getSuggestMode(int code, int uid, String packageName) {
        return -1;
    }

    public void onAppApplyOperation(int uid, String packageName, int op, int mode, int operationType, int processState, int flags) {
        if ((flags & 32) == 0) {
            onAppApplyOperation(uid, packageName, op, mode, operationType, processState);
        }
    }

    public void onAppApplyOperation(int uid, String packageName, int op, int mode, int operationType, int processState) {
        UserState uidState;
        if (UserHandle.getAppId(uid) >= 10000 && (uidState = getUidState(UserHandle.getUserId(uid), false)) != null && uidState.mCallbackBinder != null) {
            uidState.mCallbackBinder.callOneWayTransact(4, new Object[]{Integer.valueOf(uid), packageName, Integer.valueOf(op), Integer.valueOf(mode), Integer.valueOf(operationType), Integer.valueOf(processState)});
        }
    }

    public void onAppPermFlagsModified(String permName, String packageName, int flagMask, int flagValues, int callingUid, int userId, boolean overridePolicy) {
        UserState uidState = getUidState(userId, false);
        if (uidState != null && uidState.mCallbackBinder != null) {
            uidState.mCallbackBinder.callOneWayTransact(6, new Object[]{permName, packageName, Integer.valueOf(flagMask), Integer.valueOf(flagValues), Integer.valueOf(callingUid), Integer.valueOf(userId), Boolean.valueOf(overridePolicy)});
        }
    }

    public void onAppRuntimePermStateModified(String permName, String packageName, boolean granted, int callingUid, int userId, boolean overridePolicy) {
        UserState uidState = getUidState(userId, false);
        if (uidState != null && uidState.mCallbackBinder != null) {
            uidState.mCallbackBinder.callOneWayTransact(7, new Object[]{permName, packageName, Boolean.valueOf(granted), Integer.valueOf(callingUid), Integer.valueOf(userId), Boolean.valueOf(overridePolicy)});
        }
    }

    public void updateProcessState(int uid, int procState) {
        UserState uidState;
        if (UserHandle.getAppId(uid) >= 10000 && (uidState = getUidState(UserHandle.getUserId(uid), false)) != null && uidState.mCallbackBinder != null) {
            uidState.mCallbackBinder.callOneWayTransact(5, new Object[]{Integer.valueOf(uid), Integer.valueOf(procState)});
        }
    }

    public int registerCallback(IBinder callback) {
        this.mContext.enforcePermission("android.permission.UPDATE_APP_OPS_STATS", Binder.getCallingPid(), Binder.getCallingUid(), (String) null);
        if (callback == null) {
            return -1;
        }
        int callingUserId = UserHandle.getCallingUserId();
        if (callingUserId == 0) {
            registerCallback(callback, 999);
        }
        return registerCallback(callback, callingUserId);
    }

    public int registerCallback(IBinder callback, int userId) {
        UserState uidState;
        this.mContext.enforcePermission("android.permission.UPDATE_APP_OPS_STATS", Binder.getCallingPid(), Binder.getCallingUid(), (String) null);
        if (callback == null || (uidState = getUidState(userId, true)) == null) {
            return -1;
        }
        uidState.mCallbackBinder = new MiuiBinderProxy(callback, "com.android.internal.app.IOpsCallback");
        if (uidState.mCallback != null) {
            uidState.mCallback.unlinkToDeath();
        }
        uidState.mCallback = new Callback(callback, userId);
        return 0;
    }

    public static boolean isCtsIgnore(String packageName) {
        return sCtsIgnore.contains(packageName);
    }

    /* access modifiers changed from: private */
    public void startService(final int userId) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            public void run() {
                try {
                    Intent intent = new Intent("com.miui.permission.Action.SecurityService");
                    intent.setPackage("com.lbe.security.miui");
                    AppOpsServiceState.this.mContext.startServiceAsUser(intent, new UserHandle(userId));
                } catch (Exception e) {
                    Slog.e(AppOpsServiceState.TAG, "Start Error", e);
                }
            }
        }, 1300);
    }

    public final class Callback implements IBinder.DeathRecipient {
        final IBinder mCallback;
        volatile boolean mUnLink;
        final int mUserId;

        public Callback(IBinder callback, int userId) {
            this.mCallback = callback;
            this.mUserId = userId;
            try {
                this.mCallback.linkToDeath(this, 0);
                Slog.d(AppOpsServiceState.TAG, "linkToDeath");
            } catch (RemoteException e) {
            }
        }

        public void unlinkToDeath() {
            if (!this.mUnLink) {
                try {
                    this.mUnLink = true;
                    this.mCallback.unlinkToDeath(this, 0);
                } catch (Exception e) {
                }
            }
        }

        public void binderDied() {
            unlinkToDeath();
            AppOpsServiceState.this.startService(this.mUserId);
            Slog.d(AppOpsServiceState.TAG, "binderDied mUserId : " + this.mUserId);
        }
    }
}
