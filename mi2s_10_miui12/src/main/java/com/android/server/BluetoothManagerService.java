package com.android.server;

import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.IBluetooth;
import android.bluetooth.IBluetoothCallback;
import android.bluetooth.IBluetoothGatt;
import android.bluetooth.IBluetoothManager;
import android.bluetooth.IBluetoothManagerCallback;
import android.bluetooth.IBluetoothProfileServiceConnection;
import android.bluetooth.IBluetoothStateChangeCallback;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.UserManagerInternal;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.FeatureFlagUtils;
import android.util.Log;
import android.util.Slog;
import android.util.StatsLog;
import com.android.server.am.BroadcastQueueInjector;
import com.android.server.pm.DumpState;
import com.android.server.pm.UserRestrictionsUtils;
import com.miui.enterprise.RestrictionsHelper;
import com.miui.server.AccessController;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class BluetoothManagerService extends IBluetoothManager.Stub {
    private static final int ACTIVE_LOG_MAX_SIZE = 20;
    private static final int ADD_PROXY_DELAY_MS = 100;
    private static final String BLUETOOTH_ADMIN_PERM = "android.permission.BLUETOOTH_ADMIN";
    private static final int BLUETOOTH_OFF = 0;
    private static final int BLUETOOTH_ON_AIRPLANE = 2;
    private static final int BLUETOOTH_ON_BLUETOOTH = 1;
    private static final String BLUETOOTH_PERM = "android.permission.BLUETOOTH";
    private static final String BLUETOOTH_PRIVILEGED_PERM = "android.permission.BLUETOOTH_PRIVILEGED";
    private static final int CRASH_LOG_MAX_SIZE = 100;
    private static final boolean DBG = true;
    private static final int ERROR_RESTART_TIME_MS = 3000;
    private static final String GMS = "com.google.android.gms";
    private static final int MAX_ERROR_RESTART_RETRIES = 6;
    private static final int MESSAGE_ADD_PROXY_DELAYED = 400;
    private static final int MESSAGE_BIND_PROFILE_SERVICE = 401;
    private static final int MESSAGE_BLUETOOTH_SERVICE_CONNECTED = 40;
    private static final int MESSAGE_BLUETOOTH_SERVICE_DISCONNECTED = 41;
    private static final int MESSAGE_BLUETOOTH_STATE_CHANGE = 60;
    private static final int MESSAGE_DISABLE = 2;
    private static final int MESSAGE_ENABLE = 1;
    private static final int MESSAGE_GET_NAME_AND_ADDRESS = 200;
    private static final int MESSAGE_REGISTER_ADAPTER = 20;
    private static final int MESSAGE_REGISTER_STATE_CHANGE_CALLBACK = 30;
    private static final int MESSAGE_RESTART_BLUETOOTH_SERVICE = 42;
    private static final int MESSAGE_RESTORE_USER_SETTING = 500;
    private static final int MESSAGE_TIMEOUT_BIND = 100;
    private static final int MESSAGE_TIMEOUT_UNBIND = 101;
    private static final int MESSAGE_UNREGISTER_ADAPTER = 21;
    private static final int MESSAGE_UNREGISTER_STATE_CHANGE_CALLBACK = 31;
    private static final int MESSAGE_USER_SWITCHED = 300;
    private static final int MESSAGE_USER_UNLOCKED = 301;
    private static final int RESTORE_SETTING_TO_OFF = 0;
    private static final int RESTORE_SETTING_TO_ON = 1;
    private static final String SECURE_SETTINGS_BLUETOOTH_ADDRESS = "bluetooth_address";
    private static final String SECURE_SETTINGS_BLUETOOTH_ADDR_VALID = "bluetooth_addr_valid";
    private static final String SECURE_SETTINGS_BLUETOOTH_NAME = "bluetooth_name";
    private static final int SERVICE_IBLUETOOTH = 1;
    private static final int SERVICE_IBLUETOOTHGATT = 2;
    private static final int SERVICE_RESTART_TIME_MS = 200;
    private static final String TAG = "BluetoothManagerService";
    private static final int TIMEOUT_BIND_MS = 3000;
    private static final int USER_SWITCHED_TIME_MS = 200;
    private static boolean mIsEnableBle = false;
    private final LinkedList<ActiveLog> mActiveLogs = new LinkedList<>();
    private String mAddress;
    private final ContentObserver mAirplaneModeObserver = new ContentObserver((Handler) null) {
        /* Debug info: failed to restart local var, previous not found, register: 5 */
        /* JADX WARNING: Code restructure failed: missing block: B:26:0x00cc, code lost:
            r1 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:35:0x00f3, code lost:
            throw r1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:46:0x012f, code lost:
            r1 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:56:0x0155, code lost:
            throw r1;
         */
        /* JADX WARNING: Exception block dominator not found, dom blocks: [B:29:0x00d3, B:49:0x0136] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onChange(boolean r6) {
            /*
                r5 = this;
                monitor-enter(r5)
                com.android.server.BluetoothManagerService r0 = com.android.server.BluetoothManagerService.this     // Catch:{ all -> 0x0156 }
                boolean r0 = r0.isBluetoothPersistedStateOn()     // Catch:{ all -> 0x0156 }
                r1 = 2
                if (r0 == 0) goto L_0x001e
                com.android.server.BluetoothManagerService r0 = com.android.server.BluetoothManagerService.this     // Catch:{ all -> 0x0156 }
                boolean r0 = r0.isAirplaneModeOn()     // Catch:{ all -> 0x0156 }
                if (r0 == 0) goto L_0x0018
                com.android.server.BluetoothManagerService r0 = com.android.server.BluetoothManagerService.this     // Catch:{ all -> 0x0156 }
                r0.persistBluetoothSetting(r1)     // Catch:{ all -> 0x0156 }
                goto L_0x001e
            L_0x0018:
                com.android.server.BluetoothManagerService r0 = com.android.server.BluetoothManagerService.this     // Catch:{ all -> 0x0156 }
                r2 = 1
                r0.persistBluetoothSetting(r2)     // Catch:{ all -> 0x0156 }
            L_0x001e:
                r0 = 10
                com.android.server.BluetoothManagerService r2 = com.android.server.BluetoothManagerService.this     // Catch:{ RemoteException -> 0x0131 }
                java.util.concurrent.locks.ReentrantReadWriteLock r2 = r2.mBluetoothLock     // Catch:{ RemoteException -> 0x0131 }
                java.util.concurrent.locks.ReentrantReadWriteLock$ReadLock r2 = r2.readLock()     // Catch:{ RemoteException -> 0x0131 }
                r2.lock()     // Catch:{ RemoteException -> 0x0131 }
                com.android.server.BluetoothManagerService r2 = com.android.server.BluetoothManagerService.this     // Catch:{ RemoteException -> 0x0131 }
                android.bluetooth.IBluetooth r2 = r2.mBluetooth     // Catch:{ RemoteException -> 0x0131 }
                if (r2 == 0) goto L_0x0040
                com.android.server.BluetoothManagerService r2 = com.android.server.BluetoothManagerService.this     // Catch:{ RemoteException -> 0x0131 }
                android.bluetooth.IBluetooth r2 = r2.mBluetooth     // Catch:{ RemoteException -> 0x0131 }
                int r2 = r2.getState()     // Catch:{ RemoteException -> 0x0131 }
                r0 = r2
            L_0x0040:
                com.android.server.BluetoothManagerService r2 = com.android.server.BluetoothManagerService.this     // Catch:{ all -> 0x0156 }
                java.util.concurrent.locks.ReentrantReadWriteLock r2 = r2.mBluetoothLock     // Catch:{ all -> 0x0156 }
                java.util.concurrent.locks.ReentrantReadWriteLock$ReadLock r2 = r2.readLock()     // Catch:{ all -> 0x0156 }
                r2.unlock()     // Catch:{ all -> 0x0156 }
                java.lang.String r2 = "BluetoothManagerService"
                java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0156 }
                r3.<init>()     // Catch:{ all -> 0x0156 }
                java.lang.String r4 = "Airplane Mode change - current state:  "
                r3.append(r4)     // Catch:{ all -> 0x0156 }
                java.lang.String r4 = android.bluetooth.BluetoothAdapter.nameForState(r0)     // Catch:{ all -> 0x0156 }
                r3.append(r4)     // Catch:{ all -> 0x0156 }
                java.lang.String r4 = ", isAirplaneModeOn()="
                r3.append(r4)     // Catch:{ all -> 0x0156 }
                com.android.server.BluetoothManagerService r4 = com.android.server.BluetoothManagerService.this     // Catch:{ all -> 0x0156 }
                boolean r4 = r4.isAirplaneModeOn()     // Catch:{ all -> 0x0156 }
                r3.append(r4)     // Catch:{ all -> 0x0156 }
                java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0156 }
                android.util.Slog.d(r2, r3)     // Catch:{ all -> 0x0156 }
                com.android.server.BluetoothManagerService r2 = com.android.server.BluetoothManagerService.this     // Catch:{ all -> 0x0156 }
                boolean r2 = r2.isAirplaneModeOn()     // Catch:{ all -> 0x0156 }
                r3 = 12
                if (r2 == 0) goto L_0x0106
                com.android.server.BluetoothManagerService r2 = com.android.server.BluetoothManagerService.this     // Catch:{ all -> 0x0156 }
                r2.clearBleApps()     // Catch:{ all -> 0x0156 }
                r2 = 15
                if (r0 != r2) goto L_0x00f4
                com.android.server.BluetoothManagerService r2 = com.android.server.BluetoothManagerService.this     // Catch:{ RemoteException -> 0x00ce }
                java.util.concurrent.locks.ReentrantReadWriteLock r2 = r2.mBluetoothLock     // Catch:{ RemoteException -> 0x00ce }
                java.util.concurrent.locks.ReentrantReadWriteLock$ReadLock r2 = r2.readLock()     // Catch:{ RemoteException -> 0x00ce }
                r2.lock()     // Catch:{ RemoteException -> 0x00ce }
                com.android.server.BluetoothManagerService r2 = com.android.server.BluetoothManagerService.this     // Catch:{ RemoteException -> 0x00ce }
                android.bluetooth.IBluetooth r2 = r2.mBluetooth     // Catch:{ RemoteException -> 0x00ce }
                if (r2 == 0) goto L_0x00c1
                com.android.server.BluetoothManagerService r2 = com.android.server.BluetoothManagerService.this     // Catch:{ RemoteException -> 0x00ce }
                com.android.server.BluetoothManagerService r3 = com.android.server.BluetoothManagerService.this     // Catch:{ RemoteException -> 0x00ce }
                android.content.Context r3 = r3.mContext     // Catch:{ RemoteException -> 0x00ce }
                java.lang.String r3 = r3.getPackageName()     // Catch:{ RemoteException -> 0x00ce }
                r4 = 0
                r2.addActiveLog(r1, r3, r4)     // Catch:{ RemoteException -> 0x00ce }
                com.android.server.BluetoothManagerService r1 = com.android.server.BluetoothManagerService.this     // Catch:{ RemoteException -> 0x00ce }
                android.bluetooth.IBluetooth r1 = r1.mBluetooth     // Catch:{ RemoteException -> 0x00ce }
                r1.onBrEdrDown()     // Catch:{ RemoteException -> 0x00ce }
                com.android.server.BluetoothManagerService r1 = com.android.server.BluetoothManagerService.this     // Catch:{ RemoteException -> 0x00ce }
                boolean unused = r1.mEnable = r4     // Catch:{ RemoteException -> 0x00ce }
                com.android.server.BluetoothManagerService r1 = com.android.server.BluetoothManagerService.this     // Catch:{ RemoteException -> 0x00ce }
                boolean unused = r1.mEnableExternal = r4     // Catch:{ RemoteException -> 0x00ce }
            L_0x00c1:
                com.android.server.BluetoothManagerService r1 = com.android.server.BluetoothManagerService.this     // Catch:{ all -> 0x0156 }
                java.util.concurrent.locks.ReentrantReadWriteLock r1 = r1.mBluetoothLock     // Catch:{ all -> 0x0156 }
                java.util.concurrent.locks.ReentrantReadWriteLock$ReadLock r1 = r1.readLock()     // Catch:{ all -> 0x0156 }
                goto L_0x00e1
            L_0x00cc:
                r1 = move-exception
                goto L_0x00e6
            L_0x00ce:
                r1 = move-exception
                java.lang.String r2 = "BluetoothManagerService"
                java.lang.String r3 = "Unable to call onBrEdrDown"
                android.util.Slog.e(r2, r3, r1)     // Catch:{ all -> 0x00cc }
                com.android.server.BluetoothManagerService r1 = com.android.server.BluetoothManagerService.this     // Catch:{ all -> 0x0156 }
                java.util.concurrent.locks.ReentrantReadWriteLock r1 = r1.mBluetoothLock     // Catch:{ all -> 0x0156 }
                java.util.concurrent.locks.ReentrantReadWriteLock$ReadLock r1 = r1.readLock()     // Catch:{ all -> 0x0156 }
            L_0x00e1:
                r1.unlock()     // Catch:{ all -> 0x0156 }
                goto L_0x012d
            L_0x00e6:
                com.android.server.BluetoothManagerService r2 = com.android.server.BluetoothManagerService.this     // Catch:{ all -> 0x0156 }
                java.util.concurrent.locks.ReentrantReadWriteLock r2 = r2.mBluetoothLock     // Catch:{ all -> 0x0156 }
                java.util.concurrent.locks.ReentrantReadWriteLock$ReadLock r2 = r2.readLock()     // Catch:{ all -> 0x0156 }
                r2.unlock()     // Catch:{ all -> 0x0156 }
                throw r1     // Catch:{ all -> 0x0156 }
            L_0x00f4:
                if (r0 != r3) goto L_0x012d
                com.android.server.BluetoothManagerService r2 = com.android.server.BluetoothManagerService.this     // Catch:{ all -> 0x0156 }
                com.android.server.BluetoothManagerService r3 = com.android.server.BluetoothManagerService.this     // Catch:{ all -> 0x0156 }
                android.content.Context r3 = r3.mContext     // Catch:{ all -> 0x0156 }
                java.lang.String r3 = r3.getPackageName()     // Catch:{ all -> 0x0156 }
                r2.sendDisableMsg(r1, r3)     // Catch:{ all -> 0x0156 }
                goto L_0x012d
            L_0x0106:
                com.android.server.BluetoothManagerService r2 = com.android.server.BluetoothManagerService.this     // Catch:{ all -> 0x0156 }
                boolean r2 = r2.mEnableExternal     // Catch:{ all -> 0x0156 }
                if (r2 == 0) goto L_0x012d
                if (r0 == r3) goto L_0x012d
                com.android.server.BluetoothManagerService r2 = com.android.server.BluetoothManagerService.this     // Catch:{ all -> 0x0156 }
                boolean r2 = r2.isBluetoothPersistedStateOn()     // Catch:{ all -> 0x0156 }
                if (r2 == 0) goto L_0x012d
                com.android.server.BluetoothManagerService r2 = com.android.server.BluetoothManagerService.this     // Catch:{ all -> 0x0156 }
                com.android.server.BluetoothManagerService r3 = com.android.server.BluetoothManagerService.this     // Catch:{ all -> 0x0156 }
                boolean r3 = r3.mQuietEnableExternal     // Catch:{ all -> 0x0156 }
                com.android.server.BluetoothManagerService r4 = com.android.server.BluetoothManagerService.this     // Catch:{ all -> 0x0156 }
                android.content.Context r4 = r4.mContext     // Catch:{ all -> 0x0156 }
                java.lang.String r4 = r4.getPackageName()     // Catch:{ all -> 0x0156 }
                r2.sendEnableMsg(r3, r1, r4)     // Catch:{ all -> 0x0156 }
            L_0x012d:
                monitor-exit(r5)     // Catch:{ all -> 0x0156 }
                return
            L_0x012f:
                r1 = move-exception
                goto L_0x0148
            L_0x0131:
                r1 = move-exception
                java.lang.String r2 = "BluetoothManagerService"
                java.lang.String r3 = "Unable to call getState"
                android.util.Slog.e(r2, r3, r1)     // Catch:{ all -> 0x012f }
                com.android.server.BluetoothManagerService r2 = com.android.server.BluetoothManagerService.this     // Catch:{ all -> 0x0156 }
                java.util.concurrent.locks.ReentrantReadWriteLock r2 = r2.mBluetoothLock     // Catch:{ all -> 0x0156 }
                java.util.concurrent.locks.ReentrantReadWriteLock$ReadLock r2 = r2.readLock()     // Catch:{ all -> 0x0156 }
                r2.unlock()     // Catch:{ all -> 0x0156 }
                monitor-exit(r5)     // Catch:{ all -> 0x0156 }
                return
            L_0x0148:
                com.android.server.BluetoothManagerService r2 = com.android.server.BluetoothManagerService.this     // Catch:{ all -> 0x0156 }
                java.util.concurrent.locks.ReentrantReadWriteLock r2 = r2.mBluetoothLock     // Catch:{ all -> 0x0156 }
                java.util.concurrent.locks.ReentrantReadWriteLock$ReadLock r2 = r2.readLock()     // Catch:{ all -> 0x0156 }
                r2.unlock()     // Catch:{ all -> 0x0156 }
                throw r1     // Catch:{ all -> 0x0156 }
            L_0x0156:
                r0 = move-exception
                monitor-exit(r5)     // Catch:{ all -> 0x0156 }
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.BluetoothManagerService.AnonymousClass3.onChange(boolean):void");
        }
    };
    private AppOpsManager mAppOps;
    /* access modifiers changed from: private */
    public boolean mBinding;
    /* access modifiers changed from: private */
    public Map<IBinder, ClientDeathRecipient> mBleApps = new ConcurrentHashMap();
    /* access modifiers changed from: private */
    public IBluetooth mBluetooth;
    /* access modifiers changed from: private */
    public IBinder mBluetoothBinder;
    /* access modifiers changed from: private */
    public final IBluetoothCallback mBluetoothCallback = new IBluetoothCallback.Stub() {
        public void onBluetoothStateChange(int prevState, int newState) throws RemoteException {
            BluetoothManagerService.this.mHandler.sendMessage(BluetoothManagerService.this.mHandler.obtainMessage(60, prevState, newState));
        }
    };
    /* access modifiers changed from: private */
    public IBluetoothGatt mBluetoothGatt;
    /* access modifiers changed from: private */
    public final ReentrantReadWriteLock mBluetoothLock = new ReentrantReadWriteLock();
    /* access modifiers changed from: private */
    public final RemoteCallbackList<IBluetoothManagerCallback> mCallbacks;
    /* access modifiers changed from: private */
    public BluetoothServiceConnection mConnection = new BluetoothServiceConnection();
    private final ContentResolver mContentResolver;
    /* access modifiers changed from: private */
    public final Context mContext;
    private final LinkedList<Long> mCrashTimestamps = new LinkedList<>();
    private int mCrashes;
    /* access modifiers changed from: private */
    public boolean mEnable;
    private boolean mEnableBLE;
    /* access modifiers changed from: private */
    public boolean mEnableExternal;
    /* access modifiers changed from: private */
    public int mErrorRecoveryRetryCounter;
    /* access modifiers changed from: private */
    public final BluetoothHandler mHandler = new BluetoothHandler(IoThread.get().getLooper());
    private boolean mIsHearingAidProfileSupported;
    private long mLastEnabledTime;
    private String mName;
    /* access modifiers changed from: private */
    public final Map<Integer, ProfileServiceConnections> mProfileServices = new HashMap();
    /* access modifiers changed from: private */
    public boolean mQuietEnable = false;
    /* access modifiers changed from: private */
    public boolean mQuietEnableExternal;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            int i;
            String action = intent.getAction();
            if ("android.bluetooth.adapter.action.LOCAL_NAME_CHANGED".equals(action)) {
                String newName = intent.getStringExtra("android.bluetooth.adapter.extra.LOCAL_NAME");
                Slog.d(BluetoothManagerService.TAG, "Bluetooth Adapter name changed to " + newName);
                if (newName != null) {
                    BluetoothManagerService.this.storeNameAndAddress(newName, (String) null);
                }
            } else if ("android.bluetooth.adapter.action.BLUETOOTH_ADDRESS_CHANGED".equals(action)) {
                String newAddress = intent.getStringExtra("android.bluetooth.adapter.extra.BLUETOOTH_ADDRESS");
                if (newAddress != null) {
                    Slog.d(BluetoothManagerService.TAG, "Bluetooth Adapter address changed to " + BluetoothManagerServiceInjector.getMaskDeviceAddress(newAddress));
                    BluetoothManagerService.this.storeNameAndAddress((String) null, newAddress);
                    return;
                }
                Slog.e(BluetoothManagerService.TAG, "No Bluetooth Adapter address parameter found");
            } else if ("android.os.action.SETTING_RESTORED".equals(action) && "bluetooth_on".equals(intent.getStringExtra("setting_name"))) {
                String prevValue = intent.getStringExtra("previous_value");
                String newValue = intent.getStringExtra("new_value");
                Slog.d(BluetoothManagerService.TAG, "ACTION_SETTING_RESTORED with BLUETOOTH_ON, prevValue=" + prevValue + ", newValue=" + newValue);
                if (newValue != null && prevValue != null && !prevValue.equals(newValue)) {
                    BluetoothHandler access$200 = BluetoothManagerService.this.mHandler;
                    if (newValue.equals("0")) {
                        i = 0;
                    } else {
                        i = 1;
                    }
                    BluetoothManagerService.this.mHandler.sendMessage(access$200.obtainMessage(500, i, 0));
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public int mState;
    /* access modifiers changed from: private */
    public final RemoteCallbackList<IBluetoothStateChangeCallback> mStateChangeCallbacks;
    private final int mSystemUiUid;
    /* access modifiers changed from: private */
    public boolean mUnbinding;
    private final UserManagerInternal.UserRestrictionsListener mUserRestrictionsListener = new UserManagerInternal.UserRestrictionsListener() {
        public void onUserRestrictionsChanged(int userId, Bundle newRestrictions, Bundle prevRestrictions) {
            if (UserRestrictionsUtils.restrictionsChanged(prevRestrictions, newRestrictions, "no_bluetooth_sharing")) {
                BluetoothManagerService.this.updateOppLauncherComponentState(userId, newRestrictions.getBoolean("no_bluetooth_sharing"));
            }
            if (userId == 0 && UserRestrictionsUtils.restrictionsChanged(prevRestrictions, newRestrictions, "no_bluetooth")) {
                if (userId != 0 || !newRestrictions.getBoolean("no_bluetooth")) {
                    BluetoothManagerService.this.updateOppLauncherComponentState(userId, newRestrictions.getBoolean("no_bluetooth_sharing"));
                    return;
                }
                BluetoothManagerService.this.updateOppLauncherComponentState(userId, true);
                BluetoothManagerService bluetoothManagerService = BluetoothManagerService.this;
                bluetoothManagerService.sendDisableMsg(3, bluetoothManagerService.mContext.getPackageName());
            }
        }
    };
    private final boolean mWirelessConsentRequired;

    /* access modifiers changed from: private */
    public static CharSequence timeToLog(long timestamp) {
        return DateFormat.format("MM-dd HH:mm:ss", timestamp);
    }

    private class ActiveLog {
        private boolean mEnable;
        private String mPackageName;
        private int mReason;
        private long mTimestamp;

        ActiveLog(int reason, String packageName, boolean enable, long timestamp) {
            this.mReason = reason;
            this.mPackageName = packageName;
            this.mEnable = enable;
            this.mTimestamp = timestamp;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(BluetoothManagerService.timeToLog(this.mTimestamp));
            sb.append(this.mEnable ? "  Enabled " : " Disabled ");
            sb.append(" due to ");
            sb.append(BluetoothManagerService.getEnableDisableReasonString(this.mReason));
            sb.append(" by ");
            sb.append(this.mPackageName);
            return sb.toString();
        }
    }

    BluetoothManagerService(Context context) {
        this.mContext = context;
        this.mWirelessConsentRequired = context.getResources().getBoolean(17891614);
        this.mCrashes = 0;
        this.mBluetooth = null;
        this.mBluetoothBinder = null;
        this.mBluetoothGatt = null;
        this.mBinding = false;
        this.mUnbinding = false;
        this.mEnable = false;
        this.mEnableBLE = false;
        this.mState = 10;
        this.mQuietEnableExternal = false;
        this.mEnableExternal = false;
        this.mAddress = null;
        this.mName = null;
        this.mErrorRecoveryRetryCounter = 0;
        this.mContentResolver = context.getContentResolver();
        registerForBleScanModeChange();
        this.mCallbacks = new RemoteCallbackList<>();
        this.mStateChangeCallbacks = new RemoteCallbackList<>();
        this.mIsHearingAidProfileSupported = context.getResources().getBoolean(17891468);
        String value = SystemProperties.get("persist.sys.fflag.override.settings_bluetooth_hearing_aid");
        if (!TextUtils.isEmpty(value)) {
            boolean isHearingAidEnabled = Boolean.parseBoolean(value);
            Log.v(TAG, "set feature flag HEARING_AID_SETTINGS to " + isHearingAidEnabled);
            FeatureFlagUtils.setEnabled(context, "settings_bluetooth_hearing_aid", isHearingAidEnabled);
            if (isHearingAidEnabled && !this.mIsHearingAidProfileSupported) {
                this.mIsHearingAidProfileSupported = true;
            }
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.bluetooth.adapter.action.LOCAL_NAME_CHANGED");
        filter.addAction("android.bluetooth.adapter.action.BLUETOOTH_ADDRESS_CHANGED");
        filter.addAction("android.os.action.SETTING_RESTORED");
        filter.setPriority(1000);
        this.mContext.registerReceiver(this.mReceiver, filter);
        loadStoredNameAndAddress();
        if (isBluetoothPersistedStateOn()) {
            Slog.d(TAG, "Startup: Bluetooth persisted state is ON.");
            this.mEnableExternal = true;
        }
        String airplaneModeRadios = Settings.Global.getString(this.mContentResolver, "airplane_mode_radios");
        if (airplaneModeRadios == null || airplaneModeRadios.contains("bluetooth")) {
            this.mContentResolver.registerContentObserver(Settings.Global.getUriFor("airplane_mode_on"), true, this.mAirplaneModeObserver);
        }
        int systemUiUid = -1;
        try {
            systemUiUid = !this.mContext.getResources().getBoolean(17891490) ? this.mContext.getPackageManager().getPackageUidAsUser(AccessController.PACKAGE_SYSTEMUI, DumpState.DUMP_DEXOPT, 0) : systemUiUid;
            Slog.d(TAG, "Detected SystemUiUid: " + Integer.toString(systemUiUid));
        } catch (PackageManager.NameNotFoundException e) {
            Slog.w(TAG, "Unable to resolve SystemUI's UID.", e);
        }
        this.mSystemUiUid = systemUiUid;
    }

    /* access modifiers changed from: private */
    public boolean isAirplaneModeOn() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "airplane_mode_on", 0) == 1;
    }

    private boolean supportBluetoothPersistedState() {
        return this.mContext.getResources().getBoolean(17891536);
    }

    /* access modifiers changed from: private */
    public boolean isBluetoothPersistedStateOn() {
        if (!supportBluetoothPersistedState()) {
            return false;
        }
        int state = Settings.Global.getInt(this.mContentResolver, "bluetooth_on", -1);
        Slog.d(TAG, "Bluetooth persisted state: " + state);
        if (state != 0) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public boolean isBluetoothPersistedStateOnBluetooth() {
        if (supportBluetoothPersistedState() && Settings.Global.getInt(this.mContentResolver, "bluetooth_on", 1) == 1) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void persistBluetoothSetting(int value) {
        Slog.d(TAG, "Persisting Bluetooth Setting: " + value);
        long callingIdentity = Binder.clearCallingIdentity();
        Settings.Global.putInt(this.mContext.getContentResolver(), "bluetooth_on", value);
        Binder.restoreCallingIdentity(callingIdentity);
    }

    /* access modifiers changed from: private */
    public boolean isNameAndAddressSet() {
        String str = this.mName;
        return str != null && this.mAddress != null && str.length() > 0 && this.mAddress.length() > 0;
    }

    private void loadStoredNameAndAddress() {
        Slog.d(TAG, "Loading stored name and address");
        if (!this.mContext.getResources().getBoolean(17891374) || Settings.Secure.getInt(this.mContentResolver, SECURE_SETTINGS_BLUETOOTH_ADDR_VALID, 0) != 0) {
            this.mName = Settings.Secure.getString(this.mContentResolver, SECURE_SETTINGS_BLUETOOTH_NAME);
            this.mAddress = Settings.Secure.getString(this.mContentResolver, SECURE_SETTINGS_BLUETOOTH_ADDRESS);
            Slog.d(TAG, "Stored bluetooth Name=" + this.mName + ",Address=" + BluetoothManagerServiceInjector.getMaskDeviceAddress(this.mAddress));
            return;
        }
        Slog.d(TAG, "invalid bluetooth name and address stored");
    }

    /* access modifiers changed from: private */
    public void storeNameAndAddress(String name, String address) {
        if (name != null) {
            Settings.Secure.putString(this.mContentResolver, SECURE_SETTINGS_BLUETOOTH_NAME, name);
            this.mName = name;
            Slog.d(TAG, "Stored Bluetooth name: " + Settings.Secure.getString(this.mContentResolver, SECURE_SETTINGS_BLUETOOTH_NAME));
        }
        if (address != null) {
            Settings.Secure.putString(this.mContentResolver, SECURE_SETTINGS_BLUETOOTH_ADDRESS, address);
            this.mAddress = address;
            Slog.d(TAG, "Stored Bluetoothaddress: " + BluetoothManagerServiceInjector.getMaskDeviceAddress(Settings.Secure.getString(this.mContentResolver, SECURE_SETTINGS_BLUETOOTH_ADDRESS)));
        }
        if (name != null && address != null) {
            Settings.Secure.putInt(this.mContentResolver, SECURE_SETTINGS_BLUETOOTH_ADDR_VALID, 1);
        }
    }

    public IBluetooth registerAdapter(IBluetoothManagerCallback callback) {
        if (callback == null) {
            Slog.w(TAG, "Callback is null in registerAdapter");
            return null;
        }
        Message msg = this.mHandler.obtainMessage(20);
        msg.obj = callback;
        this.mHandler.sendMessage(msg);
        return this.mBluetooth;
    }

    public void unregisterAdapter(IBluetoothManagerCallback callback) {
        if (callback == null) {
            Slog.w(TAG, "Callback is null in unregisterAdapter");
            return;
        }
        this.mContext.enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        Message msg = this.mHandler.obtainMessage(21);
        msg.obj = callback;
        this.mHandler.sendMessage(msg);
    }

    public void registerStateChangeCallback(IBluetoothStateChangeCallback callback) {
        this.mContext.enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        if (callback == null) {
            Slog.w(TAG, "registerStateChangeCallback: Callback is null!");
            return;
        }
        Message msg = this.mHandler.obtainMessage(30);
        msg.obj = callback;
        this.mHandler.sendMessage(msg);
    }

    public void unregisterStateChangeCallback(IBluetoothStateChangeCallback callback) {
        this.mContext.enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        if (callback == null) {
            Slog.w(TAG, "unregisterStateChangeCallback: Callback is null!");
            return;
        }
        Message msg = this.mHandler.obtainMessage(31);
        msg.obj = callback;
        this.mHandler.sendMessage(msg);
    }

    public boolean isEnabled() {
        if (Binder.getCallingUid() == 1000 || checkIfCallerIsForegroundUser()) {
            try {
                this.mBluetoothLock.readLock().lock();
                if (this.mBluetooth != null) {
                    boolean isEnabled = this.mBluetooth.isEnabled();
                    this.mBluetoothLock.readLock().unlock();
                    return isEnabled;
                }
            } catch (RemoteException e) {
                Slog.e(TAG, "isEnabled()", e);
            } catch (Throwable th) {
                this.mBluetoothLock.readLock().unlock();
                throw th;
            }
            this.mBluetoothLock.readLock().unlock();
            return false;
        }
        Slog.w(TAG, "isEnabled(): not allowed for non-active and non system user");
        return false;
    }

    public int getState() {
        if (Binder.getCallingUid() == 1000 || checkIfCallerIsForegroundUser()) {
            try {
                this.mBluetoothLock.readLock().lock();
                if (this.mBluetooth != null) {
                    int state = this.mBluetooth.getState();
                    this.mBluetoothLock.readLock().unlock();
                    return state;
                }
            } catch (RemoteException e) {
                Slog.e(TAG, "getState()", e);
            } catch (Throwable th) {
                this.mBluetoothLock.readLock().unlock();
                throw th;
            }
            this.mBluetoothLock.readLock().unlock();
            return 10;
        }
        Slog.w(TAG, "getState(): report OFF for non-active and non system user");
        return 10;
    }

    class ClientDeathRecipient implements IBinder.DeathRecipient {
        private String mPackageName;

        ClientDeathRecipient(String packageName) {
            this.mPackageName = packageName;
        }

        public void binderDied() {
            Slog.d(BluetoothManagerService.TAG, "Binder is dead - unregister " + this.mPackageName);
            for (Map.Entry<IBinder, ClientDeathRecipient> entry : BluetoothManagerService.this.mBleApps.entrySet()) {
                IBinder token = entry.getKey();
                if (entry.getValue().equals(this)) {
                    BluetoothManagerService.this.updateBleAppCount(token, false, this.mPackageName);
                    return;
                }
            }
        }

        public String getPackageName() {
            return this.mPackageName;
        }
    }

    public boolean isBleScanAlwaysAvailable() {
        if (isAirplaneModeOn() && !this.mEnable) {
            return false;
        }
        try {
            if (Settings.Global.getInt(this.mContentResolver, "ble_scan_always_enabled") != 0) {
                return true;
            }
            return false;
        } catch (Settings.SettingNotFoundException e) {
            return false;
        }
    }

    public boolean isHearingAidProfileSupported() {
        return this.mIsHearingAidProfileSupported;
    }

    private void registerForBleScanModeChange() {
        this.mContentResolver.registerContentObserver(Settings.Global.getUriFor("ble_scan_always_enabled"), false, new ContentObserver((Handler) null) {
            public void onChange(boolean selfChange) {
                if (!BluetoothManagerService.this.isBleScanAlwaysAvailable()) {
                    BluetoothManagerService.this.disableBleScanMode();
                    BluetoothManagerService.this.clearBleApps();
                    try {
                        BluetoothManagerService.this.mBluetoothLock.readLock().lock();
                        if (BluetoothManagerService.this.mBluetooth != null) {
                            BluetoothManagerService.this.addActiveLog(1, BluetoothManagerService.this.mContext.getPackageName(), false);
                            BluetoothManagerService.this.mBluetooth.onBrEdrDown();
                        }
                    } catch (RemoteException e) {
                        Slog.e(BluetoothManagerService.TAG, "error when disabling bluetooth", e);
                    } catch (Throwable th) {
                        BluetoothManagerService.this.mBluetoothLock.readLock().unlock();
                        throw th;
                    }
                    BluetoothManagerService.this.mBluetoothLock.readLock().unlock();
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void disableBleScanMode() {
        try {
            this.mBluetoothLock.writeLock().lock();
            if (!(this.mBluetooth == null || this.mBluetooth.getState() == 12 || isBluetoothPersistedStateOnBluetooth())) {
                Slog.d(TAG, "Reseting the mEnable flag for clean disable");
                if (!this.mEnableExternal) {
                    this.mEnable = false;
                }
            }
        } catch (RemoteException e) {
            Slog.e(TAG, "getState()", e);
        } catch (Throwable th) {
            this.mBluetoothLock.writeLock().unlock();
            throw th;
        }
        this.mBluetoothLock.writeLock().unlock();
    }

    public int updateBleAppCount(IBinder token, boolean enable, String packageName) {
        int callingUid = Binder.getCallingUid();
        if (!(UserHandle.getAppId(callingUid) == 1000)) {
            checkPackage(callingUid, packageName);
        }
        ClientDeathRecipient r = this.mBleApps.get(token);
        int st = 10;
        if (r == null && enable) {
            ClientDeathRecipient deathRec = new ClientDeathRecipient(packageName);
            try {
                token.linkToDeath(deathRec, 0);
                this.mBleApps.put(token, deathRec);
                Slog.d(TAG, "Registered for death of " + packageName);
                if (GMS.equals(packageName)) {
                    mIsEnableBle = true;
                }
            } catch (RemoteException e) {
                throw new IllegalArgumentException("BLE app (" + packageName + ") already dead!");
            }
        } else if (!enable && r != null) {
            token.unlinkToDeath(r, 0);
            this.mBleApps.remove(token);
            Slog.d(TAG, "Unregistered for death of " + packageName);
            if (GMS.equals(packageName)) {
                mIsEnableBle = false;
            }
        }
        if (enable) {
            try {
                this.mBluetoothLock.readLock().lock();
                if (this.mBluetooth == null || !(this.mBluetooth.getState() == 15 || this.mBluetooth.getState() == 11 || this.mBluetooth.getState() == 12)) {
                    this.mEnableBLE = true;
                }
            } catch (RemoteException e2) {
                Slog.e(TAG, "Unable to call getState", e2);
            } catch (Throwable th) {
                this.mBluetoothLock.readLock().unlock();
                throw th;
            }
            this.mBluetoothLock.readLock().unlock();
        }
        int appCount = this.mBleApps.size();
        Slog.d(TAG, appCount + " registered Ble Apps");
        if (appCount == 0 && this.mEnable) {
            disableBleScanMode();
        }
        if (appCount == 0) {
            try {
                this.mBluetoothLock.readLock().lock();
                if (this.mBluetooth != null) {
                    st = this.mBluetooth.getState();
                }
                if (!this.mEnableExternal || st == 15) {
                    Slog.d(TAG, "Move to BT state OFF");
                    if (!enable) {
                        this.mEnableExternal = false;
                    }
                    sendBrEdrDownCallback();
                    this.mEnableExternal = false;
                }
            } catch (RemoteException e3) {
                Slog.e(TAG, "", e3);
            } catch (Throwable th2) {
                this.mBluetoothLock.readLock().unlock();
                throw th2;
            }
            this.mBluetoothLock.readLock().unlock();
        }
        return appCount;
    }

    /* access modifiers changed from: private */
    public void clearBleApps() {
        this.mBleApps.clear();
    }

    public boolean isBleAppPresent() {
        Slog.d(TAG, "isBleAppPresent() count: " + this.mBleApps.size());
        return this.mBleApps.size() > 0;
    }

    /* access modifiers changed from: private */
    public void continueFromBleOnState() {
        Slog.d(TAG, "continueFromBleOnState()");
        try {
            this.mBluetoothLock.readLock().lock();
            if (this.mBluetooth == null) {
                Slog.e(TAG, "onBluetoothServiceUp: mBluetooth is null!");
                this.mBluetoothLock.readLock().unlock();
                return;
            }
            int st = this.mBluetooth.getState();
            if (st != 15) {
                Slog.v(TAG, "onBluetoothServiceUp: state isn't BLE_ON: " + BluetoothAdapter.nameForState(st));
                this.mBluetoothLock.readLock().unlock();
                return;
            }
            if (isBluetoothPersistedStateOnBluetooth() || !isBleAppPresent() || this.mEnableExternal) {
                this.mBluetooth.updateQuietModeStatus(this.mQuietEnable);
                this.mBluetooth.onLeServiceUp();
                persistBluetoothSetting(1);
            }
            this.mBluetoothLock.readLock().unlock();
        } catch (RemoteException e) {
            Slog.e(TAG, "Unable to call onServiceUp", e);
        } catch (Throwable th) {
            this.mBluetoothLock.readLock().unlock();
            throw th;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0025, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:?, code lost:
        android.util.Slog.e(TAG, "Unable to disconnect all apps.", r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0048, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:?, code lost:
        r3.mBluetoothLock.readLock().unlock();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0068, code lost:
        throw r0;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:11:0x001b, B:28:0x004f] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private synchronized void sendBrEdrDownCallback() {
        /*
            r3 = this;
            monitor-enter(r3)
            java.lang.String r0 = "BluetoothManagerService"
            java.lang.String r1 = "Calling sendBrEdrDownCallback callbacks"
            android.util.Slog.d(r0, r1)     // Catch:{ all -> 0x0069 }
            android.bluetooth.IBluetooth r0 = r3.mBluetooth     // Catch:{ all -> 0x0069 }
            if (r0 != 0) goto L_0x0015
            java.lang.String r0 = "BluetoothManagerService"
            java.lang.String r1 = "Bluetooth handle is null"
            android.util.Slog.w(r0, r1)     // Catch:{ all -> 0x0069 }
            monitor-exit(r3)
            return
        L_0x0015:
            boolean r0 = r3.isBleAppPresent()     // Catch:{ all -> 0x0069 }
            if (r0 == 0) goto L_0x002f
            android.bluetooth.IBluetoothGatt r0 = r3.mBluetoothGatt     // Catch:{ RemoteException -> 0x0025 }
            if (r0 == 0) goto L_0x0024
            android.bluetooth.IBluetoothGatt r0 = r3.mBluetoothGatt     // Catch:{ RemoteException -> 0x0025 }
            r0.unregAll()     // Catch:{ RemoteException -> 0x0025 }
        L_0x0024:
            goto L_0x005d
        L_0x0025:
            r0 = move-exception
            java.lang.String r1 = "BluetoothManagerService"
            java.lang.String r2 = "Unable to disconnect all apps."
            android.util.Slog.e(r1, r2, r0)     // Catch:{ all -> 0x0069 }
            goto L_0x005d
        L_0x002f:
            java.util.concurrent.locks.ReentrantReadWriteLock r0 = r3.mBluetoothLock     // Catch:{ RemoteException -> 0x004a }
            java.util.concurrent.locks.ReentrantReadWriteLock$ReadLock r0 = r0.readLock()     // Catch:{ RemoteException -> 0x004a }
            r0.lock()     // Catch:{ RemoteException -> 0x004a }
            android.bluetooth.IBluetooth r0 = r3.mBluetooth     // Catch:{ RemoteException -> 0x004a }
            if (r0 == 0) goto L_0x0041
            android.bluetooth.IBluetooth r0 = r3.mBluetooth     // Catch:{ RemoteException -> 0x004a }
            r0.onBrEdrDown()     // Catch:{ RemoteException -> 0x004a }
        L_0x0041:
            java.util.concurrent.locks.ReentrantReadWriteLock r0 = r3.mBluetoothLock     // Catch:{ all -> 0x0069 }
            java.util.concurrent.locks.ReentrantReadWriteLock$ReadLock r0 = r0.readLock()     // Catch:{ all -> 0x0069 }
            goto L_0x0059
        L_0x0048:
            r0 = move-exception
            goto L_0x005f
        L_0x004a:
            r0 = move-exception
            java.lang.String r1 = "BluetoothManagerService"
            java.lang.String r2 = "Call to onBrEdrDown() failed."
            android.util.Slog.e(r1, r2, r0)     // Catch:{ all -> 0x0048 }
            java.util.concurrent.locks.ReentrantReadWriteLock r0 = r3.mBluetoothLock     // Catch:{ all -> 0x0069 }
            java.util.concurrent.locks.ReentrantReadWriteLock$ReadLock r0 = r0.readLock()     // Catch:{ all -> 0x0069 }
        L_0x0059:
            r0.unlock()     // Catch:{ all -> 0x0069 }
        L_0x005d:
            monitor-exit(r3)
            return
        L_0x005f:
            java.util.concurrent.locks.ReentrantReadWriteLock r1 = r3.mBluetoothLock     // Catch:{ all -> 0x0069 }
            java.util.concurrent.locks.ReentrantReadWriteLock$ReadLock r1 = r1.readLock()     // Catch:{ all -> 0x0069 }
            r1.unlock()     // Catch:{ all -> 0x0069 }
            throw r0     // Catch:{ all -> 0x0069 }
        L_0x0069:
            r0 = move-exception
            monitor-exit(r3)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.BluetoothManagerService.sendBrEdrDownCallback():void");
    }

    public boolean enableNoAutoConnect(String packageName) {
        if (isBluetoothDisallowed()) {
            Slog.d(TAG, "enableNoAutoConnect(): not enabling - bluetooth disallowed");
            return false;
        }
        int callingUid = Binder.getCallingUid();
        if (!(UserHandle.getAppId(callingUid) == 1000)) {
            checkPackage(callingUid, packageName);
        }
        this.mContext.enforceCallingOrSelfPermission(BLUETOOTH_ADMIN_PERM, "Need BLUETOOTH ADMIN permission");
        if (RestrictionsHelper.handleBluetoothChange(this.mContext, true)) {
            return false;
        }
        Slog.d(TAG, "enableNoAutoConnect():  mBluetooth =" + this.mBluetooth + " mBinding = " + this.mBinding);
        if (UserHandle.getAppId(callingUid) == 1027) {
            synchronized (this.mReceiver) {
                this.mQuietEnableExternal = true;
                this.mEnableExternal = true;
                sendEnableMsg(true, 1, packageName);
            }
            return true;
        }
        throw new SecurityException("no permission to enable Bluetooth quietly");
    }

    public boolean enable(String packageName) throws RemoteException {
        int callingUid = Binder.getCallingUid();
        boolean callerSystem = UserHandle.getAppId(callingUid) == 1000;
        if (isBluetoothDisallowed()) {
            Slog.d(TAG, "enable(): not enabling - bluetooth disallowed");
            return false;
        } else if (RestrictionsHelper.handleBluetoothChange(this.mContext, true)) {
            return false;
        } else {
            if (!callerSystem) {
                checkPackage(callingUid, packageName);
                if (!checkIfCallerIsForegroundUser()) {
                    Slog.w(TAG, "enable(): not allowed for non-active and non system user");
                    return false;
                }
                this.mContext.enforceCallingOrSelfPermission(BLUETOOTH_ADMIN_PERM, "Need BLUETOOTH ADMIN permission");
                AppOpsManager appOps = (AppOpsManager) this.mContext.getSystemService("appops");
                if (appOps != null && appOps.noteOp(10002, callingUid, packageName) != 0) {
                    return false;
                }
                if (!isEnabled() && this.mWirelessConsentRequired && startConsentUiIfNeeded(packageName, callingUid, "android.bluetooth.adapter.action.REQUEST_ENABLE")) {
                    return false;
                }
            }
            Slog.d(TAG, "enable(" + packageName + "):  mBluetooth =" + this.mBluetooth + " mBinding = " + this.mBinding + " mState = " + BluetoothAdapter.nameForState(this.mState));
            if (!GMS.equals(packageName) || !mIsEnableBle) {
                persistBluetoothSetting(1);
            } else {
                Slog.d(TAG, "GMS enable ble");
            }
            synchronized (this.mReceiver) {
                this.mQuietEnableExternal = false;
                if (!this.mEnableBLE) {
                    this.mEnableExternal = true;
                } else {
                    this.mEnableBLE = false;
                }
                sendEnableMsg(false, 1, packageName);
            }
            Slog.d(TAG, "enable returning");
            return true;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    public boolean disable(String packageName, boolean persist) throws RemoteException {
        ReentrantReadWriteLock.ReadLock readLock;
        int callingUid = Binder.getCallingUid();
        boolean callerSystem = UserHandle.getAppId(callingUid) == 1000;
        if (RestrictionsHelper.handleBluetoothChange(this.mContext, false)) {
            return false;
        }
        if (!callerSystem) {
            checkPackage(callingUid, packageName);
            if (!checkIfCallerIsForegroundUser()) {
                Slog.w(TAG, "disable(): not allowed for non-active and non system user");
                return false;
            }
            this.mContext.enforceCallingOrSelfPermission(BLUETOOTH_ADMIN_PERM, "Need BLUETOOTH ADMIN permission");
            AppOpsManager appOps = (AppOpsManager) this.mContext.getSystemService("appops");
            if (appOps != null && appOps.noteOp(10002, callingUid, packageName) != 0) {
                return false;
            }
            if (isEnabled() && this.mWirelessConsentRequired && startConsentUiIfNeeded(packageName, callingUid, "android.bluetooth.adapter.action.REQUEST_DISABLE")) {
                return false;
            }
        }
        Slog.d(TAG, "disable(): mBluetooth = " + this.mBluetooth + " mBinding = " + this.mBinding);
        synchronized (this.mReceiver) {
            if (persist) {
                persistBluetoothSetting(0);
                this.mEnableExternal = false;
                sendDisableMsg(1, packageName);
            } else {
                synchronized (this) {
                    clearBleApps();
                }
                try {
                    this.mBluetoothLock.readLock().lock();
                    this.mEnableExternal = false;
                    if (this.mBluetooth != null) {
                        if (this.mBluetooth.getState() == 15) {
                            this.mEnable = false;
                            this.mBluetooth.onBrEdrDown();
                        } else {
                            sendDisableMsg(6, packageName);
                        }
                    }
                    readLock = this.mBluetoothLock.readLock();
                } catch (RemoteException e) {
                    try {
                        Slog.e(TAG, "Unable to initiate disable", e);
                        readLock = this.mBluetoothLock.readLock();
                    } catch (Throwable th) {
                        this.mBluetoothLock.readLock().unlock();
                        throw th;
                    }
                }
                readLock.unlock();
            }
        }
        return true;
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    private boolean startConsentUiIfNeeded(String packageName, int callingUid, String intentAction) throws RemoteException {
        if (checkBluetoothPermissionWhenWirelessConsentRequired()) {
            return false;
        }
        try {
            if (this.mContext.getPackageManager().getApplicationInfoAsUser(packageName, 268435456, UserHandle.getUserId(callingUid)).uid == callingUid) {
                Intent intent = new Intent(intentAction);
                intent.putExtra("android.intent.extra.PACKAGE_NAME", packageName);
                intent.setFlags(276824064);
                try {
                    this.mContext.startActivity(intent);
                    return true;
                } catch (ActivityNotFoundException e) {
                    Slog.e(TAG, "Intent to handle action " + intentAction + " missing");
                    return false;
                }
            } else {
                throw new SecurityException("Package " + packageName + " not in uid " + callingUid);
            }
        } catch (PackageManager.NameNotFoundException e2) {
            throw new RemoteException(e2.getMessage());
        }
    }

    private void checkPackage(int uid, String packageName) {
        AppOpsManager appOpsManager = this.mAppOps;
        if (appOpsManager == null) {
            Slog.w(TAG, "checkPackage(): called before system boot up, uid " + uid + ", packageName " + packageName);
            throw new IllegalStateException("System has not boot yet");
        } else if (packageName == null) {
            Slog.w(TAG, "checkPackage(): called with null packageName from " + uid);
        } else {
            try {
                appOpsManager.checkPackage(uid, packageName);
            } catch (SecurityException e) {
                Slog.w(TAG, "checkPackage(): " + packageName + " does not belong to uid " + uid);
                throw new SecurityException(e.getMessage());
            }
        }
    }

    private boolean checkBluetoothPermissionWhenWirelessConsentRequired() {
        return this.mContext.checkCallingPermission("android.permission.MANAGE_BLUETOOTH_WHEN_WIRELESS_CONSENT_REQUIRED") == 0;
    }

    public void unbindAndFinish() {
        Slog.d(TAG, "unbindAndFinish(): " + this.mBluetooth + " mBinding = " + this.mBinding + " mUnbinding = " + this.mUnbinding);
        try {
            this.mBluetoothLock.writeLock().lock();
            if (this.mUnbinding) {
                this.mBluetoothLock.writeLock().unlock();
                return;
            }
            this.mUnbinding = true;
            this.mHandler.removeMessages(60);
            this.mHandler.removeMessages(MESSAGE_BIND_PROFILE_SERVICE);
            unbindAllBluetoothProfileServices();
            if (this.mBluetooth != null) {
                this.mBluetooth.unregisterCallback(this.mBluetoothCallback);
                this.mBluetoothBinder = null;
                this.mBluetooth = null;
                this.mContext.unbindService(this.mConnection);
                this.mUnbinding = false;
                this.mBinding = false;
            } else {
                this.mUnbinding = false;
            }
            this.mBluetoothGatt = null;
            this.mBluetoothLock.writeLock().unlock();
        } catch (RemoteException re) {
            Slog.e(TAG, "Unable to unregister BluetoothCallback", re);
        } catch (Throwable th) {
            this.mBluetoothLock.writeLock().unlock();
            throw th;
        }
    }

    public IBluetoothGatt getBluetoothGatt() {
        return this.mBluetoothGatt;
    }

    public boolean isBluetoothAvailableForBinding() {
        try {
            this.mBluetoothLock.writeLock().lock();
            if (this.mBluetooth == null || !(this.mBluetooth.getState() == 12 || this.mBluetooth.getState() == 11)) {
                this.mBluetoothLock.writeLock().unlock();
                return false;
            }
            return true;
        } catch (RemoteException e) {
            Slog.e(TAG, "getState()", e);
            return false;
        } finally {
            this.mBluetoothLock.writeLock().unlock();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:20:0x007e, code lost:
        r0 = r7.mHandler.obtainMessage(MESSAGE_ADD_PROXY_DELAYED);
        r0.arg1 = r8;
        r0.obj = r9;
        r7.mHandler.sendMessageDelayed(r0, 100);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0091, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean bindBluetoothProfileService(int r8, android.bluetooth.IBluetoothProfileServiceConnection r9) {
        /*
            r7 = this;
            boolean r0 = r7.isBluetoothAvailableForBinding()
            r1 = 0
            if (r0 != 0) goto L_0x0023
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "bindBluetoothProfileService:Trying to bind to profile: "
            r0.append(r2)
            r0.append(r8)
            java.lang.String r2 = ", while Bluetooth is disabled"
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            java.lang.String r2 = "BluetoothManagerService"
            android.util.Slog.w(r2, r0)
            return r1
        L_0x0023:
            java.util.Map<java.lang.Integer, com.android.server.BluetoothManagerService$ProfileServiceConnections> r0 = r7.mProfileServices
            monitor-enter(r0)
            java.util.Map<java.lang.Integer, com.android.server.BluetoothManagerService$ProfileServiceConnections> r2 = r7.mProfileServices     // Catch:{ all -> 0x0092 }
            java.lang.Integer r3 = new java.lang.Integer     // Catch:{ all -> 0x0092 }
            r3.<init>(r8)     // Catch:{ all -> 0x0092 }
            java.lang.Object r2 = r2.get(r3)     // Catch:{ all -> 0x0092 }
            com.android.server.BluetoothManagerService$ProfileServiceConnections r2 = (com.android.server.BluetoothManagerService.ProfileServiceConnections) r2     // Catch:{ all -> 0x0092 }
            r3 = 1
            if (r2 != 0) goto L_0x0075
            java.lang.String r4 = "BluetoothManagerService"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0092 }
            r5.<init>()     // Catch:{ all -> 0x0092 }
            java.lang.String r6 = "Creating new ProfileServiceConnections object for profile: "
            r5.append(r6)     // Catch:{ all -> 0x0092 }
            r5.append(r8)     // Catch:{ all -> 0x0092 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0092 }
            android.util.Slog.d(r4, r5)     // Catch:{ all -> 0x0092 }
            if (r8 == r3) goto L_0x0050
            monitor-exit(r0)     // Catch:{ all -> 0x0092 }
            return r1
        L_0x0050:
            android.content.Intent r4 = new android.content.Intent     // Catch:{ all -> 0x0092 }
            java.lang.Class<android.bluetooth.IBluetoothHeadset> r5 = android.bluetooth.IBluetoothHeadset.class
            java.lang.String r5 = r5.getName()     // Catch:{ all -> 0x0092 }
            r4.<init>(r5)     // Catch:{ all -> 0x0092 }
            com.android.server.BluetoothManagerService$ProfileServiceConnections r5 = new com.android.server.BluetoothManagerService$ProfileServiceConnections     // Catch:{ all -> 0x0092 }
            r5.<init>(r4)     // Catch:{ all -> 0x0092 }
            r2 = r5
            boolean r5 = r2.bindService()     // Catch:{ all -> 0x0092 }
            if (r5 != 0) goto L_0x0069
            monitor-exit(r0)     // Catch:{ all -> 0x0092 }
            return r1
        L_0x0069:
            java.util.Map<java.lang.Integer, com.android.server.BluetoothManagerService$ProfileServiceConnections> r1 = r7.mProfileServices     // Catch:{ all -> 0x0092 }
            java.lang.Integer r5 = new java.lang.Integer     // Catch:{ all -> 0x0092 }
            r5.<init>(r8)     // Catch:{ all -> 0x0092 }
            r1.put(r5, r2)     // Catch:{ all -> 0x0092 }
            goto L_0x007d
        L_0x0075:
            java.lang.String r1 = "BluetoothManagerService"
            java.lang.String r4 = "psc is not null in bindBluetoothProfileService"
            android.util.Slog.w(r1, r4)     // Catch:{ all -> 0x0092 }
        L_0x007d:
            monitor-exit(r0)     // Catch:{ all -> 0x0092 }
            com.android.server.BluetoothManagerService$BluetoothHandler r0 = r7.mHandler
            r1 = 400(0x190, float:5.6E-43)
            android.os.Message r0 = r0.obtainMessage(r1)
            r0.arg1 = r8
            r0.obj = r9
            com.android.server.BluetoothManagerService$BluetoothHandler r1 = r7.mHandler
            r4 = 100
            r1.sendMessageDelayed(r0, r4)
            return r3
        L_0x0092:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0092 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.BluetoothManagerService.bindBluetoothProfileService(int, android.bluetooth.IBluetoothProfileServiceConnection):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0073, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void unbindBluetoothProfileService(int r8, android.bluetooth.IBluetoothProfileServiceConnection r9) {
        /*
            r7 = this;
            java.util.Map<java.lang.Integer, com.android.server.BluetoothManagerService$ProfileServiceConnections> r0 = r7.mProfileServices
            monitor-enter(r0)
            java.lang.Integer r1 = new java.lang.Integer     // Catch:{ all -> 0x0074 }
            r1.<init>(r8)     // Catch:{ all -> 0x0074 }
            java.util.Map<java.lang.Integer, com.android.server.BluetoothManagerService$ProfileServiceConnections> r2 = r7.mProfileServices     // Catch:{ all -> 0x0074 }
            java.lang.Object r2 = r2.get(r1)     // Catch:{ all -> 0x0074 }
            com.android.server.BluetoothManagerService$ProfileServiceConnections r2 = (com.android.server.BluetoothManagerService.ProfileServiceConnections) r2     // Catch:{ all -> 0x0074 }
            if (r2 != 0) goto L_0x001c
            java.lang.String r3 = "BluetoothManagerService"
            java.lang.String r4 = "unbindBluetoothProfileService: psc is null, returning"
            android.util.Slog.e(r3, r4)     // Catch:{ all -> 0x0074 }
            monitor-exit(r0)     // Catch:{ all -> 0x0074 }
            return
        L_0x001c:
            java.lang.String r3 = "BluetoothManagerService"
            java.lang.String r4 = "unbindBluetoothProfileService: calling psc.removeProxy"
            android.util.Slog.w(r3, r4)     // Catch:{ all -> 0x0074 }
            r2.removeProxy(r9)     // Catch:{ all -> 0x0074 }
            boolean r3 = r2.isEmpty()     // Catch:{ all -> 0x0074 }
            if (r3 == 0) goto L_0x0072
            com.android.server.BluetoothManagerService$BluetoothHandler r3 = r7.mHandler     // Catch:{ all -> 0x0074 }
            r4 = 400(0x190, float:5.6E-43)
            boolean r3 = r3.hasMessages(r4)     // Catch:{ all -> 0x0074 }
            if (r3 != 0) goto L_0x0072
            android.content.Context r3 = r7.mContext     // Catch:{ IllegalArgumentException -> 0x003d }
            r3.unbindService(r2)     // Catch:{ IllegalArgumentException -> 0x003d }
            goto L_0x0056
        L_0x003d:
            r3 = move-exception
            java.lang.String r4 = "BluetoothManagerService"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0074 }
            r5.<init>()     // Catch:{ all -> 0x0074 }
            java.lang.String r6 = "Unable to unbind service with intent: "
            r5.append(r6)     // Catch:{ all -> 0x0074 }
            android.content.Intent r6 = r2.mIntent     // Catch:{ all -> 0x0074 }
            r5.append(r6)     // Catch:{ all -> 0x0074 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0074 }
            android.util.Slog.e(r4, r5, r3)     // Catch:{ all -> 0x0074 }
        L_0x0056:
            java.lang.String r3 = "BluetoothManagerService"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0074 }
            r4.<init>()     // Catch:{ all -> 0x0074 }
            java.lang.String r5 = "psc.isEmpty is true, removing psc entry for profile "
            r4.append(r5)     // Catch:{ all -> 0x0074 }
            r4.append(r1)     // Catch:{ all -> 0x0074 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0074 }
            android.util.Slog.w(r3, r4)     // Catch:{ all -> 0x0074 }
            java.util.Map<java.lang.Integer, com.android.server.BluetoothManagerService$ProfileServiceConnections> r3 = r7.mProfileServices     // Catch:{ all -> 0x0074 }
            r3.remove(r1)     // Catch:{ all -> 0x0074 }
        L_0x0072:
            monitor-exit(r0)     // Catch:{ all -> 0x0074 }
            return
        L_0x0074:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0074 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.BluetoothManagerService.unbindBluetoothProfileService(int, android.bluetooth.IBluetoothProfileServiceConnection):void");
    }

    /* access modifiers changed from: private */
    public void unbindAllBluetoothProfileServices() {
        synchronized (this.mProfileServices) {
            for (Integer i : this.mProfileServices.keySet()) {
                ProfileServiceConnections psc = this.mProfileServices.get(i);
                try {
                    this.mContext.unbindService(psc);
                } catch (IllegalArgumentException e) {
                    Slog.e(TAG, "Unable to unbind service with intent: " + psc.mIntent, e);
                }
                psc.removeAllProxies();
            }
            this.mProfileServices.clear();
        }
    }

    public void handleOnBootPhase() {
        Slog.d(TAG, "Bluetooth boot completed");
        this.mAppOps = (AppOpsManager) this.mContext.getSystemService(AppOpsManager.class);
        ((UserManagerInternal) LocalServices.getService(UserManagerInternal.class)).addUserRestrictionsListener(this.mUserRestrictionsListener);
        if (!isBluetoothDisallowed()) {
            if (this.mEnableExternal && isBluetoothPersistedStateOnBluetooth()) {
                Slog.d(TAG, "Auto-enabling Bluetooth.");
                sendEnableMsg(this.mQuietEnableExternal, 6, this.mContext.getPackageName());
            } else if (!isNameAndAddressSet()) {
                Slog.d(TAG, "Getting adapter name and address");
                this.mHandler.sendMessage(this.mHandler.obtainMessage(200));
            }
        }
    }

    public void handleOnSwitchUser(int userHandle) {
        Slog.d(TAG, "User " + userHandle + " switched");
        this.mHandler.obtainMessage(300, userHandle, 0).sendToTarget();
    }

    public void handleOnUnlockUser(int userHandle) {
        Slog.d(TAG, "User " + userHandle + " unlocked");
        this.mHandler.obtainMessage(MESSAGE_USER_UNLOCKED, userHandle, 0).sendToTarget();
    }

    private final class ProfileServiceConnections implements ServiceConnection, IBinder.DeathRecipient {
        ComponentName mClassName = null;
        Intent mIntent;
        boolean mInvokingProxyCallbacks = false;
        final RemoteCallbackList<IBluetoothProfileServiceConnection> mProxies = new RemoteCallbackList<>();
        IBinder mService = null;

        ProfileServiceConnections(Intent intent) {
            this.mIntent = intent;
        }

        /* access modifiers changed from: private */
        public boolean bindService() {
            int state = 10;
            try {
                BluetoothManagerService.this.mBluetoothLock.readLock().lock();
                if (BluetoothManagerService.this.mBluetooth != null) {
                    state = BluetoothManagerService.this.mBluetooth.getState();
                }
                BluetoothManagerService.this.mBluetoothLock.readLock().unlock();
                if (!BluetoothManagerService.this.mEnable || state != 12) {
                    Slog.d(BluetoothManagerService.TAG, "Unable to bindService while Bluetooth is disabled");
                    return false;
                }
                Intent intent = this.mIntent;
                if (intent == null || this.mService != null || !BluetoothManagerService.this.doBind(intent, this, 0, UserHandle.CURRENT_OR_SELF)) {
                    Slog.w(BluetoothManagerService.TAG, "Unable to bind with intent: " + this.mIntent);
                    return false;
                }
                Message msg = BluetoothManagerService.this.mHandler.obtainMessage(BluetoothManagerService.MESSAGE_BIND_PROFILE_SERVICE);
                msg.obj = this;
                BluetoothManagerService.this.mHandler.sendMessageDelayed(msg, 3000);
                return true;
            } catch (RemoteException e) {
                Slog.e(BluetoothManagerService.TAG, "Unable to call getState", e);
                BluetoothManagerService.this.mBluetoothLock.readLock().unlock();
                return false;
            } catch (Throwable th) {
                BluetoothManagerService.this.mBluetoothLock.readLock().unlock();
                throw th;
            }
        }

        /* access modifiers changed from: private */
        public void addProxy(IBluetoothProfileServiceConnection proxy) {
            this.mProxies.register(proxy);
            IBinder iBinder = this.mService;
            if (iBinder != null) {
                try {
                    proxy.onServiceConnected(this.mClassName, iBinder);
                } catch (RemoteException e) {
                    Slog.e(BluetoothManagerService.TAG, "Unable to connect to proxy", e);
                }
            } else if (!BluetoothManagerService.this.isBluetoothAvailableForBinding()) {
                Slog.w(BluetoothManagerService.TAG, "addProxy: Trying to bind to profile: " + this.mClassName + ", while Bluetooth is disabled");
                this.mProxies.unregister(proxy);
            } else if (!BluetoothManagerService.this.mHandler.hasMessages(BluetoothManagerService.MESSAGE_BIND_PROFILE_SERVICE, this)) {
                Message msg = BluetoothManagerService.this.mHandler.obtainMessage(BluetoothManagerService.MESSAGE_BIND_PROFILE_SERVICE);
                msg.obj = this;
                BluetoothManagerService.this.mHandler.sendMessage(msg);
            }
        }

        /* access modifiers changed from: private */
        public void removeProxy(IBluetoothProfileServiceConnection proxy) {
            if (proxy != null) {
                if (this.mProxies.unregister(proxy)) {
                    try {
                        proxy.onServiceDisconnected(this.mClassName);
                    } catch (RemoteException e) {
                        Slog.e(BluetoothManagerService.TAG, "Unable to disconnect proxy", e);
                    }
                }
                Slog.w(BluetoothManagerService.TAG, "removing the proxy, count is " + this.mProxies.getRegisteredCallbackCount());
                return;
            }
            Slog.w(BluetoothManagerService.TAG, "Trying to remove a null proxy");
        }

        /* access modifiers changed from: private */
        public void removeAllProxies() {
            onServiceDisconnected(this.mClassName);
            this.mProxies.kill();
        }

        /* access modifiers changed from: private */
        public boolean isEmpty() {
            RemoteCallbackList<IBluetoothProfileServiceConnection> remoteCallbackList = this.mProxies;
            return remoteCallbackList != null && remoteCallbackList.getRegisteredCallbackCount() == 0;
        }

        public void onServiceConnected(ComponentName className, IBinder service) {
            BluetoothManagerService.this.mHandler.removeMessages(BluetoothManagerService.MESSAGE_BIND_PROFILE_SERVICE, this);
            this.mService = service;
            this.mClassName = className;
            try {
                this.mService.linkToDeath(this, 0);
            } catch (RemoteException e) {
                Slog.e(BluetoothManagerService.TAG, "Unable to linkToDeath", e);
            }
            if (this.mInvokingProxyCallbacks) {
                Slog.e(BluetoothManagerService.TAG, "Proxy callbacks already in progress.");
                return;
            }
            this.mInvokingProxyCallbacks = true;
            int n = this.mProxies.beginBroadcast();
            for (int i = 0; i < n; i++) {
                try {
                    this.mProxies.getBroadcastItem(i).onServiceConnected(className, service);
                } catch (RemoteException e2) {
                    Slog.e(BluetoothManagerService.TAG, "Unable to connect to proxy", e2);
                } catch (Throwable th) {
                    this.mProxies.finishBroadcast();
                    this.mInvokingProxyCallbacks = false;
                    throw th;
                }
            }
            this.mProxies.finishBroadcast();
            this.mInvokingProxyCallbacks = false;
        }

        public void onServiceDisconnected(ComponentName className) {
            IBinder iBinder = this.mService;
            if (iBinder != null) {
                try {
                    iBinder.unlinkToDeath(this, 0);
                } catch (NoSuchElementException e) {
                    Slog.e(BluetoothManagerService.TAG, "Unable to unlinkToDeath", e);
                }
                this.mService = null;
                this.mClassName = null;
                if (this.mInvokingProxyCallbacks) {
                    Slog.e(BluetoothManagerService.TAG, "Proxy callbacks already in progress.");
                    return;
                }
                this.mInvokingProxyCallbacks = true;
                int n = this.mProxies.beginBroadcast();
                for (int i = 0; i < n; i++) {
                    try {
                        this.mProxies.getBroadcastItem(i).onServiceDisconnected(className);
                    } catch (RemoteException e2) {
                        Slog.e(BluetoothManagerService.TAG, "Unable to disconnect from proxy", e2);
                    } catch (Throwable th) {
                        this.mProxies.finishBroadcast();
                        this.mInvokingProxyCallbacks = false;
                        throw th;
                    }
                }
                this.mProxies.finishBroadcast();
                this.mInvokingProxyCallbacks = false;
            }
        }

        public void binderDied() {
            Slog.w(BluetoothManagerService.TAG, "Profile service for profile: " + this.mClassName + " died.");
            onServiceDisconnected(this.mClassName);
            if (!BluetoothManagerService.this.isBluetoothAvailableForBinding()) {
                Slog.w(BluetoothManagerService.TAG, "binderDied: Trying to bind to profile: " + this.mClassName + ", while Bluetooth is disabled");
                return;
            }
            Message msg = BluetoothManagerService.this.mHandler.obtainMessage(BluetoothManagerService.MESSAGE_BIND_PROFILE_SERVICE);
            msg.obj = this;
            BluetoothManagerService.this.mHandler.sendMessageDelayed(msg, 3000);
        }
    }

    private void sendBluetoothStateCallback(boolean isUp) {
        int i;
        try {
            int n = this.mStateChangeCallbacks.beginBroadcast();
            Slog.d(TAG, "Broadcasting onBluetoothStateChange(" + isUp + ") to " + n + " receivers.");
            i = 0;
            while (i < n) {
                this.mStateChangeCallbacks.getBroadcastItem(i).onBluetoothStateChange(isUp);
                i++;
            }
            this.mStateChangeCallbacks.finishBroadcast();
        } catch (RemoteException e) {
            Slog.e(TAG, "Unable to call onBluetoothStateChange() on callback #" + i, e);
        } catch (Throwable th) {
            this.mStateChangeCallbacks.finishBroadcast();
            throw th;
        }
    }

    /* access modifiers changed from: private */
    public void sendBluetoothServiceUpCallback() {
        int i;
        try {
            int n = this.mCallbacks.beginBroadcast();
            Slog.d(TAG, "Broadcasting onBluetoothServiceUp() to " + n + " receivers.");
            i = 0;
            while (i < n) {
                this.mCallbacks.getBroadcastItem(i).onBluetoothServiceUp(this.mBluetooth);
                i++;
            }
            this.mCallbacks.finishBroadcast();
        } catch (RemoteException e) {
            Slog.e(TAG, "Unable to call onBluetoothServiceUp() on callback #" + i, e);
        } catch (Throwable th) {
            this.mCallbacks.finishBroadcast();
            throw th;
        }
    }

    /* access modifiers changed from: private */
    public void sendBluetoothServiceDownCallback() {
        int i;
        try {
            int n = this.mCallbacks.beginBroadcast();
            Slog.d(TAG, "Broadcasting onBluetoothServiceDown() to " + n + " receivers.");
            i = 0;
            while (i < n) {
                this.mCallbacks.getBroadcastItem(i).onBluetoothServiceDown();
                i++;
            }
            this.mCallbacks.finishBroadcast();
        } catch (RemoteException e) {
            Slog.e(TAG, "Unable to call onBluetoothServiceDown() on callback #" + i, e);
        } catch (Throwable th) {
            this.mCallbacks.finishBroadcast();
            throw th;
        }
    }

    public String getAddress() {
        this.mContext.enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        if (Binder.getCallingUid() != 1000 && !checkIfCallerIsForegroundUser()) {
            Slog.w(TAG, "getAddress(): not allowed for non-active and non system user");
            return null;
        } else if (this.mContext.checkCallingOrSelfPermission("android.permission.LOCAL_MAC_ADDRESS") != 0) {
            return "02:00:00:00:00:00";
        } else {
            try {
                this.mBluetoothLock.readLock().lock();
                if (this.mBluetooth != null) {
                    String address = this.mBluetooth.getAddress();
                    this.mBluetoothLock.readLock().unlock();
                    return address;
                }
            } catch (RemoteException e) {
                Slog.e(TAG, "getAddress(): Unable to retrieve address remotely. Returning cached address", e);
            } catch (Throwable th) {
                this.mBluetoothLock.readLock().unlock();
                throw th;
            }
            this.mBluetoothLock.readLock().unlock();
            return this.mAddress;
        }
    }

    public String getName() {
        this.mContext.enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        if (Binder.getCallingUid() == 1000 || checkIfCallerIsForegroundUser()) {
            try {
                this.mBluetoothLock.readLock().lock();
                if (this.mBluetooth != null) {
                    String name = this.mBluetooth.getName();
                    this.mBluetoothLock.readLock().unlock();
                    return name;
                }
            } catch (RemoteException e) {
                Slog.e(TAG, "getName(): Unable to retrieve name remotely. Returning cached name", e);
            } catch (Throwable th) {
                this.mBluetoothLock.readLock().unlock();
                throw th;
            }
            this.mBluetoothLock.readLock().unlock();
            return this.mName;
        }
        Slog.w(TAG, "getName(): not allowed for non-active and non system user");
        return null;
    }

    public boolean factoryReset() {
        if (!(UserHandle.getAppId(Binder.getCallingUid()) == 1000)) {
            if (!checkIfCallerIsForegroundUser()) {
                Slog.w(TAG, "factoryReset(): not allowed for non-active and non system user");
                return false;
            }
            this.mContext.enforceCallingOrSelfPermission(BLUETOOTH_PRIVILEGED_PERM, "Need BLUETOOTH PRIVILEGED permission");
        }
        persistBluetoothSetting(1);
        clearBleApps();
        try {
            if (this.mBluetooth == null) {
                this.mEnable = true;
                handleEnable(this.mQuietEnable);
            } else if (this.mBluetooth != null && this.mBluetooth.getState() == 10) {
                this.mEnable = true;
                this.mBluetooth.factoryReset();
                handleEnable(this.mQuietEnable);
            } else if (this.mBluetooth != null) {
                return this.mBluetooth.factoryReset();
            }
            return true;
        } catch (RemoteException e) {
            Slog.e(TAG, "factoryReset(): Unable to do factoryReset.", e);
            return false;
        }
    }

    private class BluetoothServiceConnection implements ServiceConnection {
        private BluetoothServiceConnection() {
        }

        public void onServiceConnected(ComponentName componentName, IBinder service) {
            String name = componentName.getClassName();
            Slog.d(BluetoothManagerService.TAG, "BluetoothServiceConnection: " + name);
            Message msg = BluetoothManagerService.this.mHandler.obtainMessage(40);
            if (name.equals("com.android.bluetooth.btservice.AdapterService")) {
                msg.arg1 = 1;
                BluetoothManagerService.this.mHandler.removeMessages(100);
            } else if (name.equals("com.android.bluetooth.gatt.GattService")) {
                msg.arg1 = 2;
            } else {
                Slog.e(BluetoothManagerService.TAG, "Unknown service connected: " + name);
                return;
            }
            msg.obj = service;
            BluetoothManagerService.this.mHandler.sendMessage(msg);
        }

        public void onServiceDisconnected(ComponentName componentName) {
            String name = componentName.getClassName();
            Slog.d(BluetoothManagerService.TAG, "BluetoothServiceConnection, disconnected: " + name);
            Message msg = BluetoothManagerService.this.mHandler.obtainMessage(41);
            if (name.equals("com.android.bluetooth.btservice.AdapterService")) {
                msg.arg1 = 1;
            } else if (name.equals("com.android.bluetooth.gatt.GattService")) {
                msg.arg1 = 2;
            } else {
                Slog.e(BluetoothManagerService.TAG, "Unknown service disconnected: " + name);
                return;
            }
            BluetoothManagerService.this.mHandler.sendMessage(msg);
        }
    }

    private class BluetoothHandler extends Handler {
        boolean mGetNameAddressOnly = false;

        BluetoothHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            Message message = msg;
            int i = message.what;
            if (i == 1) {
                Slog.d(BluetoothManagerService.TAG, "MESSAGE_ENABLE(" + message.arg1 + "): mBluetooth = " + BluetoothManagerService.this.mBluetooth);
                BluetoothManagerService.this.mHandler.removeMessages(42);
                boolean unused = BluetoothManagerService.this.mEnable = true;
                boolean unused2 = BluetoothManagerService.this.mQuietEnable = message.arg1 == 1;
                try {
                    BluetoothManagerService.this.mBluetoothLock.readLock().lock();
                    if (BluetoothManagerService.this.mBluetooth != null && BluetoothManagerService.this.mBluetooth.getState() == 15) {
                        Slog.w(BluetoothManagerService.TAG, "BT Enable in BLE_ON State, going to ON");
                        BluetoothManagerService.this.mBluetooth.updateQuietModeStatus(BluetoothManagerService.this.mQuietEnable);
                        BluetoothManagerService.this.mBluetooth.onLeServiceUp();
                        long callingIdentity = Binder.clearCallingIdentity();
                        BluetoothManagerService.this.persistBluetoothSetting(1);
                        Binder.restoreCallingIdentity(callingIdentity);
                        BluetoothManagerService.this.mBluetoothLock.readLock().unlock();
                        return;
                    }
                } catch (RemoteException e) {
                    Slog.e(BluetoothManagerService.TAG, "", e);
                } catch (Throwable th) {
                    BluetoothManagerService.this.mBluetoothLock.readLock().unlock();
                    throw th;
                }
                BluetoothManagerService.this.mBluetoothLock.readLock().unlock();
                if (BluetoothManagerService.this.mBluetooth == null) {
                    BluetoothManagerService bluetoothManagerService = BluetoothManagerService.this;
                    bluetoothManagerService.handleEnable(bluetoothManagerService.mQuietEnable);
                    return;
                }
                try {
                    int state = BluetoothManagerService.this.mBluetooth.getState();
                    if (state == 13 || state == 16) {
                        boolean unused3 = BluetoothManagerService.this.waitForMonitoredOnOff(false, true);
                    }
                    BluetoothManagerService.this.mHandler.sendMessageDelayed(BluetoothManagerService.this.mHandler.obtainMessage(42), 400);
                } catch (RemoteException e2) {
                    Slog.e(BluetoothManagerService.TAG, "getState()", e2);
                }
            } else if (i == 2) {
                Slog.d(BluetoothManagerService.TAG, "MESSAGE_DISABLE: mBluetooth = " + BluetoothManagerService.this.mBluetooth);
                BluetoothManagerService.this.mHandler.removeMessages(42);
                if (!BluetoothManagerService.this.mEnable || BluetoothManagerService.this.mBluetooth == null) {
                    boolean unused4 = BluetoothManagerService.this.mEnable = false;
                    BluetoothManagerService.this.handleDisable();
                    return;
                }
                boolean unused5 = BluetoothManagerService.this.waitForMonitoredOnOff(true, false);
                try {
                    BluetoothManagerService.this.mBluetoothLock.readLock().lock();
                    if (BluetoothManagerService.this.mBluetooth.getState() == 15 && (BluetoothManagerService.this.isBluetoothPersistedStateOnBluetooth() || !BluetoothManagerService.this.isBleAppPresent())) {
                        BluetoothManagerService.this.mHandler.sendMessageDelayed(BluetoothManagerService.this.mHandler.obtainMessage(2), 100);
                        BluetoothManagerService.this.mBluetoothLock.readLock().unlock();
                        return;
                    }
                } catch (RemoteException e3) {
                    Slog.e(BluetoothManagerService.TAG, "Unable to initiate disable", e3);
                } catch (Throwable th2) {
                    BluetoothManagerService.this.mBluetoothLock.readLock().unlock();
                    throw th2;
                }
                BluetoothManagerService.this.mBluetoothLock.readLock().unlock();
                boolean unused6 = BluetoothManagerService.this.mEnable = false;
                BluetoothManagerService.this.handleDisable();
                boolean unused7 = BluetoothManagerService.this.waitForMonitoredOnOff(false, false);
            } else if (i == 20) {
                BluetoothManagerService.this.mCallbacks.register((IBluetoothManagerCallback) message.obj);
            } else if (i == 21) {
                BluetoothManagerService.this.mCallbacks.unregister((IBluetoothManagerCallback) message.obj);
            } else if (i == 30) {
                BluetoothManagerService.this.mStateChangeCallbacks.register((IBluetoothStateChangeCallback) message.obj);
            } else if (i == 31) {
                BluetoothManagerService.this.mStateChangeCallbacks.unregister((IBluetoothStateChangeCallback) message.obj);
            } else if (i == 60) {
                int prevState = message.arg1;
                int newState = message.arg2;
                Slog.d(BluetoothManagerService.TAG, "MESSAGE_BLUETOOTH_STATE_CHANGE: " + BluetoothAdapter.nameForState(prevState) + " > " + BluetoothAdapter.nameForState(newState));
                int unused8 = BluetoothManagerService.this.mState = newState;
                BluetoothManagerService.this.bluetoothStateChangeHandler(prevState, newState);
                if (prevState == 14 && newState == 10 && BluetoothManagerService.this.mBluetooth != null && BluetoothManagerService.this.mEnable) {
                    BluetoothManagerService.this.recoverBluetoothServiceFromError(false);
                }
                if (prevState == 11 && newState == 10 && BluetoothManagerService.this.mBluetooth != null && BluetoothManagerService.this.mEnable) {
                    BluetoothManagerService.this.persistBluetoothSetting(0);
                }
                if (prevState == 11 && newState == 15 && BluetoothManagerService.this.mBluetooth != null && BluetoothManagerService.this.mEnable) {
                    BluetoothManagerService.this.recoverBluetoothServiceFromError(true);
                }
                if (prevState == 16 && newState == 10 && BluetoothManagerService.this.mEnable) {
                    Slog.d(BluetoothManagerService.TAG, "Entering STATE_OFF but mEnabled is true; restarting.");
                    BluetoothManagerService.this.mHandler.removeMessages(42);
                    boolean unused9 = BluetoothManagerService.this.waitForMonitoredOnOff(false, true);
                    BluetoothManagerService.this.mHandler.sendMessageDelayed(BluetoothManagerService.this.mHandler.obtainMessage(42), 400);
                }
                if ((newState == 12 || newState == 15) && BluetoothManagerService.this.mErrorRecoveryRetryCounter != 0) {
                    Slog.w(BluetoothManagerService.TAG, "bluetooth is recovered from error");
                    int unused10 = BluetoothManagerService.this.mErrorRecoveryRetryCounter = 0;
                }
            } else if (i == 200) {
                Slog.d(BluetoothManagerService.TAG, "MESSAGE_GET_NAME_AND_ADDRESS");
                try {
                    BluetoothManagerService.this.mBluetoothLock.writeLock().lock();
                    if (BluetoothManagerService.this.mBluetooth == null && !BluetoothManagerService.this.mBinding) {
                        Slog.d(BluetoothManagerService.TAG, "Binding to service to get name and address");
                        this.mGetNameAddressOnly = true;
                        BluetoothManagerService.this.mHandler.sendMessageDelayed(BluetoothManagerService.this.mHandler.obtainMessage(100), 3000);
                        if (!BluetoothManagerService.this.doBind(new Intent(IBluetooth.class.getName()), BluetoothManagerService.this.mConnection, 65, UserHandle.CURRENT)) {
                            BluetoothManagerService.this.mHandler.removeMessages(100);
                        } else {
                            boolean unused11 = BluetoothManagerService.this.mBinding = true;
                        }
                    } else if (BluetoothManagerService.this.mBluetooth != null) {
                        BluetoothManagerService.this.storeNameAndAddress(BluetoothManagerService.this.mBluetooth.getName(), BluetoothManagerService.this.mBluetooth.getAddress());
                        if (this.mGetNameAddressOnly && !BluetoothManagerService.this.mEnable) {
                            BluetoothManagerService.this.unbindAndFinish();
                        }
                        this.mGetNameAddressOnly = false;
                    }
                } catch (RemoteException re) {
                    Slog.e(BluetoothManagerService.TAG, "Unable to grab names", re);
                } catch (Throwable th3) {
                    BluetoothManagerService.this.mBluetoothLock.writeLock().unlock();
                    throw th3;
                }
                BluetoothManagerService.this.mBluetoothLock.writeLock().unlock();
            } else if (i != 500) {
                if (i == 100) {
                    Slog.e(BluetoothManagerService.TAG, "MESSAGE_TIMEOUT_BIND");
                    BluetoothManagerService.this.mBluetoothLock.writeLock().lock();
                    boolean unused12 = BluetoothManagerService.this.mBinding = false;
                    BluetoothManagerService.this.mBluetoothLock.writeLock().unlock();
                } else if (i == 101) {
                    Slog.e(BluetoothManagerService.TAG, "MESSAGE_TIMEOUT_UNBIND");
                    BluetoothManagerService.this.mBluetoothLock.writeLock().lock();
                    boolean unused13 = BluetoothManagerService.this.mUnbinding = false;
                    BluetoothManagerService.this.mBluetoothLock.writeLock().unlock();
                } else if (i == 300) {
                    Slog.d(BluetoothManagerService.TAG, "MESSAGE_USER_SWITCHED");
                    BluetoothManagerService.this.mHandler.removeMessages(300);
                    if (BluetoothManagerService.this.mBluetooth != null && BluetoothManagerService.this.isEnabled()) {
                        try {
                            BluetoothManagerService.this.mBluetoothLock.readLock().lock();
                            if (BluetoothManagerService.this.mBluetooth != null) {
                                BluetoothManagerService.this.mBluetooth.unregisterCallback(BluetoothManagerService.this.mBluetoothCallback);
                            }
                        } catch (RemoteException re2) {
                            Slog.e(BluetoothManagerService.TAG, "Unable to unregister", re2);
                        } catch (Throwable th4) {
                            BluetoothManagerService.this.mBluetoothLock.readLock().unlock();
                            throw th4;
                        }
                        BluetoothManagerService.this.mBluetoothLock.readLock().unlock();
                        if (BluetoothManagerService.this.mState == 13) {
                            BluetoothManagerService bluetoothManagerService2 = BluetoothManagerService.this;
                            bluetoothManagerService2.bluetoothStateChangeHandler(bluetoothManagerService2.mState, 10);
                            int unused14 = BluetoothManagerService.this.mState = 10;
                        }
                        if (BluetoothManagerService.this.mState == 10) {
                            BluetoothManagerService bluetoothManagerService3 = BluetoothManagerService.this;
                            bluetoothManagerService3.bluetoothStateChangeHandler(bluetoothManagerService3.mState, 11);
                            int unused15 = BluetoothManagerService.this.mState = 11;
                        }
                        boolean unused16 = BluetoothManagerService.this.waitForMonitoredOnOff(true, false);
                        if (BluetoothManagerService.this.mState == 11) {
                            BluetoothManagerService bluetoothManagerService4 = BluetoothManagerService.this;
                            bluetoothManagerService4.bluetoothStateChangeHandler(bluetoothManagerService4.mState, 12);
                        }
                        BluetoothManagerService.this.unbindAllBluetoothProfileServices();
                        BluetoothManagerService bluetoothManagerService5 = BluetoothManagerService.this;
                        bluetoothManagerService5.addActiveLog(8, bluetoothManagerService5.mContext.getPackageName(), false);
                        BluetoothManagerService.this.clearBleApps();
                        BluetoothManagerService.this.handleDisable();
                        BluetoothManagerService.this.bluetoothStateChangeHandler(12, 13);
                        boolean didDisableTimeout = !BluetoothManagerService.this.waitForMonitoredOnOff(false, true);
                        BluetoothManagerService.this.bluetoothStateChangeHandler(13, 10);
                        BluetoothManagerService.this.sendBluetoothServiceDownCallback();
                        if (!didDisableTimeout) {
                            try {
                                BluetoothManagerService.this.mBluetoothLock.writeLock().lock();
                                if (BluetoothManagerService.this.mBluetooth != null) {
                                    IBluetooth unused17 = BluetoothManagerService.this.mBluetooth = null;
                                    BluetoothManagerService.this.mContext.unbindService(BluetoothManagerService.this.mConnection);
                                }
                                IBluetoothGatt unused18 = BluetoothManagerService.this.mBluetoothGatt = null;
                            } finally {
                                BluetoothManagerService.this.mBluetoothLock.writeLock().unlock();
                            }
                        }
                        if (didDisableTimeout) {
                            SystemClock.sleep(3000);
                            BluetoothManagerService.this.mHandler.removeMessages(41);
                        } else {
                            SystemClock.sleep(100);
                        }
                        BluetoothManagerService.this.mHandler.removeMessages(60);
                        int unused19 = BluetoothManagerService.this.mState = 10;
                        BluetoothManagerService bluetoothManagerService6 = BluetoothManagerService.this;
                        bluetoothManagerService6.addActiveLog(8, bluetoothManagerService6.mContext.getPackageName(), true);
                        boolean unused20 = BluetoothManagerService.this.mEnable = true;
                        BluetoothManagerService bluetoothManagerService7 = BluetoothManagerService.this;
                        bluetoothManagerService7.handleEnable(bluetoothManagerService7.mQuietEnable);
                    } else if (BluetoothManagerService.this.mBinding || BluetoothManagerService.this.mBluetooth != null) {
                        Message userMsg = BluetoothManagerService.this.mHandler.obtainMessage(300);
                        userMsg.arg2 = message.arg2 + 1;
                        BluetoothManagerService.this.mHandler.sendMessageDelayed(userMsg, 200);
                        Slog.d(BluetoothManagerService.TAG, "Retry MESSAGE_USER_SWITCHED " + userMsg.arg2);
                    }
                } else if (i == BluetoothManagerService.MESSAGE_USER_UNLOCKED) {
                    Slog.d(BluetoothManagerService.TAG, "MESSAGE_USER_UNLOCKED");
                    BluetoothManagerService.this.mHandler.removeMessages(300);
                    if (BluetoothManagerService.this.mEnable && !BluetoothManagerService.this.mBinding && BluetoothManagerService.this.mBluetooth == null) {
                        Slog.d(BluetoothManagerService.TAG, "Enabled but not bound; retrying after unlock");
                        BluetoothManagerService bluetoothManagerService8 = BluetoothManagerService.this;
                        bluetoothManagerService8.handleEnable(bluetoothManagerService8.mQuietEnable);
                    }
                } else if (i == BluetoothManagerService.MESSAGE_ADD_PROXY_DELAYED) {
                    ProfileServiceConnections psc = (ProfileServiceConnections) BluetoothManagerService.this.mProfileServices.get(Integer.valueOf(message.arg1));
                    if (psc != null) {
                        psc.addProxy((IBluetoothProfileServiceConnection) message.obj);
                    }
                } else if (i != BluetoothManagerService.MESSAGE_BIND_PROFILE_SERVICE) {
                    switch (i) {
                        case 40:
                            Slog.d(BluetoothManagerService.TAG, "MESSAGE_BLUETOOTH_SERVICE_CONNECTED: " + message.arg1);
                            IBinder service = (IBinder) message.obj;
                            try {
                                BluetoothManagerService.this.mBluetoothLock.writeLock().lock();
                                if (message.arg1 == 2) {
                                    IBluetoothGatt unused21 = BluetoothManagerService.this.mBluetoothGatt = IBluetoothGatt.Stub.asInterface(Binder.allowBlocking(service));
                                    BluetoothManagerService.this.continueFromBleOnState();
                                    BluetoothManagerService.this.mBluetoothLock.writeLock().unlock();
                                    return;
                                }
                                boolean unused22 = BluetoothManagerService.this.mBinding = false;
                                IBinder unused23 = BluetoothManagerService.this.mBluetoothBinder = service;
                                IBluetooth unused24 = BluetoothManagerService.this.mBluetooth = IBluetooth.Stub.asInterface(Binder.allowBlocking(service));
                                if (!BluetoothManagerService.this.isNameAndAddressSet()) {
                                    BluetoothManagerService.this.mHandler.sendMessage(BluetoothManagerService.this.mHandler.obtainMessage(200));
                                    if (this.mGetNameAddressOnly) {
                                        BluetoothManagerService.this.mBluetoothLock.writeLock().unlock();
                                        return;
                                    }
                                }
                                BluetoothManagerService.this.mBluetooth.registerCallback(BluetoothManagerService.this.mBluetoothCallback);
                                BluetoothManagerService.this.sendBluetoothServiceUpCallback();
                                try {
                                    if (!BluetoothManagerService.this.mQuietEnable) {
                                        if (!BluetoothManagerService.this.mBluetooth.enable()) {
                                            Slog.e(BluetoothManagerService.TAG, "IBluetooth.enable() returned false");
                                        }
                                    } else if (!BluetoothManagerService.this.mBluetooth.enableNoAutoConnect()) {
                                        Slog.e(BluetoothManagerService.TAG, "IBluetooth.enableNoAutoConnect() returned false");
                                    }
                                } catch (RemoteException e4) {
                                    Slog.e(BluetoothManagerService.TAG, "Unable to call enable()", e4);
                                }
                                BluetoothManagerService.this.mBluetoothLock.writeLock().unlock();
                                if (!BluetoothManagerService.this.mEnable) {
                                    boolean unused25 = BluetoothManagerService.this.waitForMonitoredOnOff(true, false);
                                    BluetoothManagerService.this.handleDisable();
                                    boolean unused26 = BluetoothManagerService.this.waitForMonitoredOnOff(false, false);
                                    return;
                                }
                                return;
                            } catch (RemoteException re3) {
                                Slog.e(BluetoothManagerService.TAG, "Unable to register BluetoothCallback", re3);
                            } catch (Throwable th5) {
                                BluetoothManagerService.this.mBluetoothLock.writeLock().unlock();
                                throw th5;
                            }
                        case 41:
                            Slog.e(BluetoothManagerService.TAG, "MESSAGE_BLUETOOTH_SERVICE_DISCONNECTED(" + message.arg1 + ")");
                            try {
                                BluetoothManagerService.this.mBluetoothLock.writeLock().lock();
                                if (message.arg1 == 1) {
                                    if (BluetoothManagerService.this.mBluetooth != null) {
                                        IBluetooth unused27 = BluetoothManagerService.this.mBluetooth = null;
                                        BluetoothManagerService.this.mBluetoothLock.writeLock().unlock();
                                        BluetoothManagerService.this.addCrashLog();
                                        BluetoothManagerService bluetoothManagerService9 = BluetoothManagerService.this;
                                        bluetoothManagerService9.addActiveLog(7, bluetoothManagerService9.mContext.getPackageName(), false);
                                        if (BluetoothManagerService.this.mEnable) {
                                            boolean unused28 = BluetoothManagerService.this.mEnable = false;
                                            BluetoothManagerService.this.mHandler.sendMessageDelayed(BluetoothManagerService.this.mHandler.obtainMessage(42), 200);
                                        }
                                        BluetoothManagerService.this.sendBluetoothServiceDownCallback();
                                        if (BluetoothManagerService.this.mState == 11 || BluetoothManagerService.this.mState == 12) {
                                            BluetoothManagerService.this.bluetoothStateChangeHandler(12, 13);
                                            int unused29 = BluetoothManagerService.this.mState = 13;
                                        }
                                        if (BluetoothManagerService.this.mState == 13) {
                                            BluetoothManagerService.this.bluetoothStateChangeHandler(13, 10);
                                        }
                                        BluetoothManagerService.this.mHandler.removeMessages(60);
                                        int unused30 = BluetoothManagerService.this.mState = 10;
                                        return;
                                    }
                                } else if (message.arg1 == 2) {
                                    IBluetoothGatt unused31 = BluetoothManagerService.this.mBluetoothGatt = null;
                                } else {
                                    Slog.e(BluetoothManagerService.TAG, "Unknown argument for service disconnect!");
                                }
                                return;
                            } finally {
                                BluetoothManagerService.this.mBluetoothLock.writeLock().unlock();
                            }
                        case 42:
                            Slog.d(BluetoothManagerService.TAG, "MESSAGE_RESTART_BLUETOOTH_SERVICE");
                            boolean unused32 = BluetoothManagerService.this.mEnable = true;
                            BluetoothManagerService bluetoothManagerService10 = BluetoothManagerService.this;
                            bluetoothManagerService10.addActiveLog(4, bluetoothManagerService10.mContext.getPackageName(), true);
                            BluetoothManagerService bluetoothManagerService11 = BluetoothManagerService.this;
                            bluetoothManagerService11.handleEnable(bluetoothManagerService11.mQuietEnable);
                            return;
                        default:
                            return;
                    }
                } else {
                    Slog.w(BluetoothManagerService.TAG, "MESSAGE_BIND_PROFILE_SERVICE");
                    ProfileServiceConnections psc2 = (ProfileServiceConnections) message.obj;
                    removeMessages(BluetoothManagerService.MESSAGE_BIND_PROFILE_SERVICE, message.obj);
                    if (psc2 == null) {
                        Slog.w(BluetoothManagerService.TAG, "psc is null, breaking");
                        return;
                    }
                    Slog.w(BluetoothManagerService.TAG, "Calling psc.bindService from MESSAGE_BIND_PROFILE_SERVICE");
                    boolean unused33 = psc2.bindService();
                }
            } else if (message.arg1 == 0 && BluetoothManagerService.this.mEnable) {
                Slog.d(BluetoothManagerService.TAG, "Restore Bluetooth state to disabled");
                BluetoothManagerService.this.persistBluetoothSetting(0);
                boolean unused34 = BluetoothManagerService.this.mEnableExternal = false;
                BluetoothManagerService bluetoothManagerService12 = BluetoothManagerService.this;
                bluetoothManagerService12.sendDisableMsg(9, bluetoothManagerService12.mContext.getPackageName());
            } else if (message.arg1 == 1 && !BluetoothManagerService.this.mEnable) {
                Slog.d(BluetoothManagerService.TAG, "Restore Bluetooth state to enabled");
                boolean unused35 = BluetoothManagerService.this.mQuietEnableExternal = false;
                boolean unused36 = BluetoothManagerService.this.mEnableExternal = true;
                BluetoothManagerService bluetoothManagerService13 = BluetoothManagerService.this;
                bluetoothManagerService13.sendEnableMsg(false, 9, bluetoothManagerService13.mContext.getPackageName());
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleEnable(boolean quietMode) {
        this.mQuietEnable = quietMode;
        try {
            this.mBluetoothLock.writeLock().lock();
            if (this.mBluetooth == null && !this.mBinding) {
                this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(100), 3000);
                if (!doBind(new Intent(IBluetooth.class.getName()), this.mConnection, 65, UserHandle.CURRENT)) {
                    this.mHandler.removeMessages(100);
                } else {
                    this.mBinding = true;
                }
            } else if (this.mBluetooth != null) {
                if (!this.mQuietEnable) {
                    if (!this.mBluetooth.enable()) {
                        Slog.e(TAG, "IBluetooth.enable() returned false");
                    }
                } else if (!this.mBluetooth.enableNoAutoConnect()) {
                    Slog.e(TAG, "IBluetooth.enableNoAutoConnect() returned false");
                }
            }
        } catch (RemoteException e) {
            Slog.e(TAG, "Unable to call enable()", e);
        } catch (Throwable th) {
            this.mBluetoothLock.writeLock().unlock();
            throw th;
        }
        this.mBluetoothLock.writeLock().unlock();
    }

    /* access modifiers changed from: package-private */
    public boolean doBind(Intent intent, ServiceConnection conn, int flags, UserHandle user) {
        ComponentName comp = intent.resolveSystemService(this.mContext.getPackageManager(), 0);
        intent.setComponent(comp);
        if (comp != null && this.mContext.bindServiceAsUser(intent, conn, flags, user)) {
            return true;
        }
        Slog.e(TAG, "Fail to bind to: " + intent);
        return false;
    }

    /* access modifiers changed from: private */
    public void handleDisable() {
        try {
            this.mBluetoothLock.readLock().lock();
            if (this.mBluetooth != null) {
                Slog.d(TAG, "Sending off request.");
                if (!this.mBluetooth.disable()) {
                    Slog.e(TAG, "IBluetooth.disable() returned false");
                }
            }
        } catch (RemoteException e) {
            Slog.e(TAG, "Unable to call disable()", e);
        } catch (Throwable th) {
            this.mBluetoothLock.readLock().unlock();
            throw th;
        }
        this.mBluetoothLock.readLock().unlock();
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x003e A[Catch:{ all -> 0x0071 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean checkIfCallerIsForegroundUser() {
        /*
            r13 = this;
            int r0 = android.os.UserHandle.getCallingUserId()
            int r1 = android.os.Binder.getCallingUid()
            long r2 = android.os.Binder.clearCallingIdentity()
            android.content.Context r4 = r13.mContext
            java.lang.String r5 = "user"
            java.lang.Object r4 = r4.getSystemService(r5)
            android.os.UserManager r4 = (android.os.UserManager) r4
            android.content.pm.UserInfo r5 = r4.getProfileParent(r0)
            if (r5 == 0) goto L_0x0020
            int r6 = r5.id
            goto L_0x0022
        L_0x0020:
            r6 = -10000(0xffffffffffffd8f0, float:NaN)
        L_0x0022:
            int r7 = android.os.UserHandle.getAppId(r1)
            r8 = 0
            int r9 = android.app.ActivityManager.getCurrentUser()     // Catch:{ all -> 0x0071 }
            if (r0 == r9) goto L_0x003a
            if (r6 == r9) goto L_0x003a
            r10 = 1027(0x403, float:1.439E-42)
            if (r7 == r10) goto L_0x003a
            int r10 = r13.mSystemUiUid     // Catch:{ all -> 0x0071 }
            if (r7 != r10) goto L_0x0038
            goto L_0x003a
        L_0x0038:
            r10 = 0
            goto L_0x003b
        L_0x003a:
            r10 = 1
        L_0x003b:
            r8 = r10
            if (r8 != 0) goto L_0x006c
            java.lang.String r10 = "BluetoothManagerService"
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ all -> 0x0071 }
            r11.<init>()     // Catch:{ all -> 0x0071 }
            java.lang.String r12 = "checkIfCallerIsForegroundUser: valid="
            r11.append(r12)     // Catch:{ all -> 0x0071 }
            r11.append(r8)     // Catch:{ all -> 0x0071 }
            java.lang.String r12 = " callingUser="
            r11.append(r12)     // Catch:{ all -> 0x0071 }
            r11.append(r0)     // Catch:{ all -> 0x0071 }
            java.lang.String r12 = " parentUser="
            r11.append(r12)     // Catch:{ all -> 0x0071 }
            r11.append(r6)     // Catch:{ all -> 0x0071 }
            java.lang.String r12 = " foregroundUser="
            r11.append(r12)     // Catch:{ all -> 0x0071 }
            r11.append(r9)     // Catch:{ all -> 0x0071 }
            java.lang.String r11 = r11.toString()     // Catch:{ all -> 0x0071 }
            android.util.Slog.d(r10, r11)     // Catch:{ all -> 0x0071 }
        L_0x006c:
            android.os.Binder.restoreCallingIdentity(r2)
            return r8
        L_0x0071:
            r9 = move-exception
            android.os.Binder.restoreCallingIdentity(r2)
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.BluetoothManagerService.checkIfCallerIsForegroundUser():boolean");
    }

    private void sendBleStateChanged(int prevState, int newState) {
        Slog.d(TAG, "Sending BLE State Change: " + BluetoothAdapter.nameForState(prevState) + " > " + BluetoothAdapter.nameForState(newState));
        Intent intent = new Intent("android.bluetooth.adapter.action.BLE_STATE_CHANGED");
        intent.putExtra("android.bluetooth.adapter.extra.PREVIOUS_STATE", prevState);
        intent.putExtra("android.bluetooth.adapter.extra.STATE", newState);
        intent.addFlags(BroadcastQueueInjector.FLAG_IMMUTABLE);
        intent.setFlags(268435456);
        this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL, BLUETOOTH_PERM);
    }

    /* access modifiers changed from: private */
    public void bluetoothStateChangeHandler(int prevState, int newState) {
        boolean isStandardBroadcast = true;
        if (prevState != newState) {
            boolean intermediate_off = true;
            if (newState == 15 || newState == 10) {
                if (!(prevState == 13 && newState == 15)) {
                    intermediate_off = false;
                }
                if (newState == 10) {
                    Slog.d(TAG, "Bluetooth is complete send Service Down");
                    sendBluetoothServiceDownCallback();
                    unbindAndFinish();
                    sendBleStateChanged(prevState, newState);
                    if (prevState != 11) {
                        isStandardBroadcast = false;
                    }
                } else if (!intermediate_off) {
                    Slog.d(TAG, "Bluetooth is in LE only mode");
                    if (this.mBluetoothGatt != null || !this.mContext.getPackageManager().hasSystemFeature("android.hardware.bluetooth_le")) {
                        continueFromBleOnState();
                    } else {
                        Slog.d(TAG, "Binding Bluetooth GATT service");
                        doBind(new Intent(IBluetoothGatt.class.getName()), this.mConnection, 65, UserHandle.CURRENT);
                    }
                    sendBleStateChanged(prevState, newState);
                    isStandardBroadcast = false;
                } else if (intermediate_off) {
                    Slog.d(TAG, "Intermediate off, back to LE only mode");
                    sendBleStateChanged(prevState, newState);
                    sendBluetoothStateCallback(false);
                    newState = 10;
                    sendBrEdrDownCallback();
                }
            } else if (newState == 12) {
                if (newState != 12) {
                    intermediate_off = false;
                }
                sendBluetoothStateCallback(intermediate_off);
                sendBleStateChanged(prevState, newState);
            } else if (newState == 14 || newState == 16) {
                sendBleStateChanged(prevState, newState);
                isStandardBroadcast = false;
            } else if (newState == 11 || newState == 13) {
                sendBleStateChanged(prevState, newState);
            }
            if (isStandardBroadcast) {
                if (prevState == 15) {
                    prevState = 10;
                }
                Intent intent = new Intent("android.bluetooth.adapter.action.STATE_CHANGED");
                intent.putExtra("android.bluetooth.adapter.extra.PREVIOUS_STATE", prevState);
                intent.putExtra("android.bluetooth.adapter.extra.STATE", newState);
                intent.addFlags(BroadcastQueueInjector.FLAG_IMMUTABLE);
                intent.addFlags(DumpState.DUMP_SERVICE_PERMISSIONS);
                this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL, BLUETOOTH_PERM);
            }
        }
    }

    private boolean waitForOnOff(boolean on, boolean off) {
        int i = 0;
        while (true) {
            if (i >= 16) {
                break;
            }
            try {
                this.mBluetoothLock.readLock().lock();
                if (this.mBluetooth == null) {
                    this.mBluetoothLock.readLock().unlock();
                    break;
                }
                if (on) {
                    if (this.mBluetooth.getState() == 12) {
                        this.mBluetoothLock.readLock().unlock();
                        return true;
                    }
                } else if (off) {
                    if (this.mBluetooth.getState() == 10) {
                        this.mBluetoothLock.readLock().unlock();
                        return true;
                    }
                } else if (this.mBluetooth.getState() != 12) {
                    this.mBluetoothLock.readLock().unlock();
                    return true;
                }
                if (on || off) {
                    SystemClock.sleep(500);
                } else {
                    SystemClock.sleep(30);
                }
                i++;
            } catch (RemoteException e) {
                Slog.e(TAG, "getState()", e);
            } finally {
                this.mBluetoothLock.readLock().unlock();
            }
        }
        Slog.e(TAG, "waitForOnOff time out");
        return false;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0028, code lost:
        bluetoothStateChangeHandler(14, 15);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x002f, code lost:
        if (r6.mBluetoothGatt == null) goto L_0x003e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0031, code lost:
        android.util.Slog.d(TAG, "GattService is connected, execute waitForOnOff");
        r1 = waitForOnOff(r7, r8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x003d, code lost:
        return r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:?, code lost:
        android.util.Slog.d(TAG, "GattService connect in progress, return to avoid timeout");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0046, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x0072, code lost:
        if (r7 != false) goto L_0x007d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x0074, code lost:
        if (r8 == false) goto L_0x0077;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x0077, code lost:
        android.os.SystemClock.sleep(50);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x007d, code lost:
        android.os.SystemClock.sleep(300);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean waitForMonitoredOnOff(boolean r7, boolean r8) {
        /*
            r6 = this;
            r0 = 0
        L_0x0001:
            r1 = 10
            if (r0 >= r1) goto L_0x0094
            com.android.server.BluetoothManagerService$BluetoothServiceConnection r2 = r6.mConnection
            monitor-enter(r2)
            android.bluetooth.IBluetooth r3 = r6.mBluetooth     // Catch:{ RemoteException -> 0x0088 }
            if (r3 != 0) goto L_0x000f
            monitor-exit(r2)     // Catch:{ all -> 0x0086 }
            goto L_0x0094
        L_0x000f:
            r3 = 12
            r4 = 15
            r5 = 1
            if (r7 == 0) goto L_0x0047
            android.bluetooth.IBluetooth r1 = r6.mBluetooth     // Catch:{ RemoteException -> 0x0088 }
            int r1 = r1.getState()     // Catch:{ RemoteException -> 0x0088 }
            if (r1 != r3) goto L_0x0020
            monitor-exit(r2)     // Catch:{ all -> 0x0086 }
            return r5
        L_0x0020:
            android.bluetooth.IBluetooth r1 = r6.mBluetooth     // Catch:{ RemoteException -> 0x0088 }
            int r1 = r1.getState()     // Catch:{ RemoteException -> 0x0088 }
            if (r1 != r4) goto L_0x0070
            r1 = 14
            r6.bluetoothStateChangeHandler(r1, r4)     // Catch:{ RemoteException -> 0x0088 }
            android.bluetooth.IBluetoothGatt r1 = r6.mBluetoothGatt     // Catch:{ RemoteException -> 0x0088 }
            if (r1 == 0) goto L_0x003e
            java.lang.String r1 = "BluetoothManagerService"
            java.lang.String r3 = "GattService is connected, execute waitForOnOff"
            android.util.Slog.d(r1, r3)     // Catch:{ RemoteException -> 0x0088 }
            boolean r1 = r6.waitForOnOff(r7, r8)     // Catch:{ RemoteException -> 0x0088 }
            monitor-exit(r2)     // Catch:{ all -> 0x0086 }
            return r1
        L_0x003e:
            java.lang.String r1 = "BluetoothManagerService"
            java.lang.String r3 = "GattService connect in progress, return to avoid timeout"
            android.util.Slog.d(r1, r3)     // Catch:{ RemoteException -> 0x0088 }
            monitor-exit(r2)     // Catch:{ all -> 0x0086 }
            return r5
        L_0x0047:
            if (r8 == 0) goto L_0x0066
            android.bluetooth.IBluetooth r3 = r6.mBluetooth     // Catch:{ RemoteException -> 0x0088 }
            int r3 = r3.getState()     // Catch:{ RemoteException -> 0x0088 }
            if (r3 != r1) goto L_0x0053
            monitor-exit(r2)     // Catch:{ all -> 0x0086 }
            return r5
        L_0x0053:
            android.bluetooth.IBluetooth r1 = r6.mBluetooth     // Catch:{ RemoteException -> 0x0088 }
            int r1 = r1.getState()     // Catch:{ RemoteException -> 0x0088 }
            if (r1 != r4) goto L_0x0070
            r1 = 13
            r6.bluetoothStateChangeHandler(r1, r4)     // Catch:{ RemoteException -> 0x0088 }
            boolean r1 = r6.waitForOnOff(r7, r8)     // Catch:{ RemoteException -> 0x0088 }
            monitor-exit(r2)     // Catch:{ all -> 0x0086 }
            return r1
        L_0x0066:
            android.bluetooth.IBluetooth r1 = r6.mBluetooth     // Catch:{ RemoteException -> 0x0088 }
            int r1 = r1.getState()     // Catch:{ RemoteException -> 0x0088 }
            if (r1 == r3) goto L_0x0070
            monitor-exit(r2)     // Catch:{ all -> 0x0086 }
            return r5
        L_0x0070:
            monitor-exit(r2)     // Catch:{ all -> 0x0086 }
            if (r7 != 0) goto L_0x007d
            if (r8 == 0) goto L_0x0077
            goto L_0x007d
        L_0x0077:
            r1 = 50
            android.os.SystemClock.sleep(r1)
            goto L_0x0082
        L_0x007d:
            r1 = 300(0x12c, double:1.48E-321)
            android.os.SystemClock.sleep(r1)
        L_0x0082:
            int r0 = r0 + 1
            goto L_0x0001
        L_0x0086:
            r1 = move-exception
            goto L_0x0092
        L_0x0088:
            r1 = move-exception
            java.lang.String r3 = "BluetoothManagerService"
            java.lang.String r4 = "getState()"
            android.util.Slog.e(r3, r4, r1)     // Catch:{ all -> 0x0086 }
            monitor-exit(r2)     // Catch:{ all -> 0x0086 }
            goto L_0x0094
        L_0x0092:
            monitor-exit(r2)     // Catch:{ all -> 0x0086 }
            throw r1
        L_0x0094:
            java.lang.String r1 = "BluetoothManagerService"
            java.lang.String r2 = "waitForMonitoredOnOff time out"
            android.util.Slog.e(r1, r2)
            r1 = 0
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.BluetoothManagerService.waitForMonitoredOnOff(boolean, boolean):boolean");
    }

    /* access modifiers changed from: private */
    public void sendDisableMsg(int reason, String packageName) {
        BluetoothHandler bluetoothHandler = this.mHandler;
        bluetoothHandler.sendMessage(bluetoothHandler.obtainMessage(2));
        addActiveLog(reason, packageName, false);
    }

    /* access modifiers changed from: private */
    public void sendEnableMsg(boolean quietMode, int reason, String packageName) {
        BluetoothHandler bluetoothHandler = this.mHandler;
        bluetoothHandler.sendMessage(bluetoothHandler.obtainMessage(1, quietMode, 0));
        addActiveLog(reason, packageName, true);
        this.mLastEnabledTime = SystemClock.elapsedRealtime();
    }

    /* access modifiers changed from: private */
    public void addActiveLog(int reason, String packageName, boolean enable) {
        int state;
        synchronized (this.mActiveLogs) {
            if (this.mActiveLogs.size() > 20) {
                this.mActiveLogs.remove();
            }
            this.mActiveLogs.add(new ActiveLog(reason, packageName, enable, System.currentTimeMillis()));
        }
        if (enable) {
            state = 1;
        } else {
            state = 2;
        }
        StatsLog.write_non_chained(67, Binder.getCallingUid(), (String) null, state, reason, packageName);
    }

    /* access modifiers changed from: private */
    public void addCrashLog() {
        synchronized (this.mCrashTimestamps) {
            if (this.mCrashTimestamps.size() == 100) {
                this.mCrashTimestamps.removeFirst();
            }
            this.mCrashTimestamps.add(Long.valueOf(System.currentTimeMillis()));
            this.mCrashes++;
        }
    }

    /* access modifiers changed from: private */
    public void recoverBluetoothServiceFromError(boolean clearBle) {
        Slog.e(TAG, "recoverBluetoothServiceFromError");
        try {
            this.mBluetoothLock.readLock().lock();
            if (this.mBluetooth != null) {
                this.mBluetooth.unregisterCallback(this.mBluetoothCallback);
            }
        } catch (RemoteException re) {
            Slog.e(TAG, "Unable to unregister", re);
        } catch (Throwable th) {
            this.mBluetoothLock.readLock().unlock();
            throw th;
        }
        this.mBluetoothLock.readLock().unlock();
        waitForMonitoredOnOff(false, true);
        sendBluetoothServiceDownCallback();
        this.mHandler.removeMessages(60);
        this.mState = 10;
        if (clearBle) {
            clearBleApps();
        }
        this.mEnable = false;
        int i = this.mErrorRecoveryRetryCounter;
        this.mErrorRecoveryRetryCounter = i + 1;
        if (i < 6) {
            this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(42), 3000);
        }
    }

    private boolean isBluetoothDisallowed() {
        long callingIdentity = Binder.clearCallingIdentity();
        try {
            return ((UserManager) this.mContext.getSystemService(UserManager.class)).hasUserRestriction("no_bluetooth", UserHandle.SYSTEM);
        } finally {
            Binder.restoreCallingIdentity(callingIdentity);
        }
    }

    /* access modifiers changed from: private */
    public void updateOppLauncherComponentState(int userId, boolean bluetoothSharingDisallowed) {
        int newState;
        ComponentName oppLauncherComponent = new ComponentName("com.android.bluetooth", "com.android.bluetooth.opp.BluetoothOppLauncherActivity");
        if (bluetoothSharingDisallowed) {
            newState = 2;
        } else {
            newState = 0;
        }
        try {
            AppGlobals.getPackageManager().setComponentEnabledSetting(oppLauncherComponent, newState, 1, userId);
        } catch (Exception e) {
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:44:0x01fa  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x01ff  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void dump(java.io.FileDescriptor r20, java.io.PrintWriter r21, java.lang.String[] r22) {
        /*
            r19 = this;
            r1 = r19
            r2 = r21
            r0 = r22
            android.content.Context r3 = r1.mContext
            java.lang.String r4 = "BluetoothManagerService"
            boolean r3 = com.android.internal.util.DumpUtils.checkDumpPermission(r3, r4, r2)
            if (r3 != 0) goto L_0x0011
            return
        L_0x0011:
            r3 = 0
            int r4 = r0.length
            r5 = 0
            r6 = 1
            if (r4 <= 0) goto L_0x0023
            r4 = r0[r5]
            java.lang.String r7 = "--proto"
            boolean r4 = r4.startsWith(r7)
            if (r4 == 0) goto L_0x0023
            r4 = r6
            goto L_0x0024
        L_0x0023:
            r4 = r5
        L_0x0024:
            if (r4 != 0) goto L_0x01f5
            java.lang.String r7 = "Bluetooth Status"
            r2.println(r7)
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "  enabled: "
            r7.append(r8)
            boolean r8 = r19.isEnabled()
            r7.append(r8)
            java.lang.String r7 = r7.toString()
            r2.println(r7)
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "  state: "
            r7.append(r8)
            int r8 = r1.mState
            java.lang.String r8 = android.bluetooth.BluetoothAdapter.nameForState(r8)
            r7.append(r8)
            java.lang.String r7 = r7.toString()
            r2.println(r7)
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "  address: "
            r7.append(r8)
            java.lang.String r8 = r1.mAddress
            java.lang.String r8 = com.android.server.BluetoothManagerServiceInjector.getMaskDeviceAddress(r8)
            r7.append(r8)
            java.lang.String r7 = r7.toString()
            r2.println(r7)
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "  name: "
            r7.append(r8)
            java.lang.String r8 = r1.mName
            r7.append(r8)
            java.lang.String r7 = r7.toString()
            r2.println(r7)
            boolean r7 = r1.mEnable
            if (r7 == 0) goto L_0x00ea
            long r7 = android.os.SystemClock.elapsedRealtime()
            long r9 = r1.mLastEnabledTime
            long r7 = r7 - r9
            java.util.Locale r9 = java.util.Locale.US
            r10 = 4
            java.lang.Object[] r10 = new java.lang.Object[r10]
            r11 = 3600000(0x36ee80, double:1.7786363E-317)
            long r11 = r7 / r11
            int r11 = (int) r11
            java.lang.Integer r11 = java.lang.Integer.valueOf(r11)
            r10[r5] = r11
            r11 = 60000(0xea60, double:2.9644E-319)
            long r11 = r7 / r11
            r13 = 60
            long r11 = r11 % r13
            int r11 = (int) r11
            java.lang.Integer r11 = java.lang.Integer.valueOf(r11)
            r10[r6] = r11
            r11 = 2
            r15 = 1000(0x3e8, double:4.94E-321)
            long r17 = r7 / r15
            long r12 = r17 % r13
            int r12 = (int) r12
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            r10[r11] = r12
            r11 = 3
            long r12 = r7 % r15
            int r12 = (int) r12
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            r10[r11] = r12
            java.lang.String r11 = "%02d:%02d:%02d.%03d"
            java.lang.String r9 = java.lang.String.format(r9, r11, r10)
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "  time since enabled: "
            r10.append(r11)
            r10.append(r9)
            java.lang.String r10 = r10.toString()
            r2.println(r10)
        L_0x00ea:
            java.util.LinkedList<com.android.server.BluetoothManagerService$ActiveLog> r7 = r1.mActiveLogs
            int r7 = r7.size()
            java.lang.String r8 = "  "
            if (r7 != 0) goto L_0x00fa
            java.lang.String r7 = "\nBluetooth never enabled!"
            r2.println(r7)
            goto L_0x0124
        L_0x00fa:
            java.lang.String r7 = "\nEnable log:"
            r2.println(r7)
            java.util.LinkedList<com.android.server.BluetoothManagerService$ActiveLog> r7 = r1.mActiveLogs
            java.util.Iterator r7 = r7.iterator()
        L_0x0105:
            boolean r9 = r7.hasNext()
            if (r9 == 0) goto L_0x0124
            java.lang.Object r9 = r7.next()
            com.android.server.BluetoothManagerService$ActiveLog r9 = (com.android.server.BluetoothManagerService.ActiveLog) r9
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            r10.append(r8)
            r10.append(r9)
            java.lang.String r10 = r10.toString()
            r2.println(r10)
            goto L_0x0105
        L_0x0124:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r9 = "\nBluetooth crashed "
            r7.append(r9)
            int r9 = r1.mCrashes
            r7.append(r9)
            java.lang.String r9 = " time"
            r7.append(r9)
            int r9 = r1.mCrashes
            java.lang.String r10 = "s"
            java.lang.String r11 = ""
            if (r9 != r6) goto L_0x0143
            r9 = r11
            goto L_0x0144
        L_0x0143:
            r9 = r10
        L_0x0144:
            r7.append(r9)
            java.lang.String r7 = r7.toString()
            r2.println(r7)
            int r7 = r1.mCrashes
            r9 = 100
            if (r7 != r9) goto L_0x0159
            java.lang.String r7 = "(last 100)"
            r2.println(r7)
        L_0x0159:
            java.util.LinkedList<java.lang.Long> r7 = r1.mCrashTimestamps
            java.util.Iterator r7 = r7.iterator()
        L_0x015f:
            boolean r9 = r7.hasNext()
            if (r9 == 0) goto L_0x0186
            java.lang.Object r9 = r7.next()
            java.lang.Long r9 = (java.lang.Long) r9
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            r12.append(r8)
            long r13 = r9.longValue()
            java.lang.CharSequence r13 = timeToLog(r13)
            r12.append(r13)
            java.lang.String r12 = r12.toString()
            r2.println(r12)
            goto L_0x015f
        L_0x0186:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r9 = "\n"
            r7.append(r9)
            java.util.Map<android.os.IBinder, com.android.server.BluetoothManagerService$ClientDeathRecipient> r9 = r1.mBleApps
            int r9 = r9.size()
            r7.append(r9)
            java.lang.String r9 = " BLE app"
            r7.append(r9)
            java.util.Map<android.os.IBinder, com.android.server.BluetoothManagerService$ClientDeathRecipient> r9 = r1.mBleApps
            int r9 = r9.size()
            if (r9 != r6) goto L_0x01a7
            r10 = r11
        L_0x01a7:
            r7.append(r10)
            java.lang.String r9 = "registered"
            r7.append(r9)
            java.lang.String r7 = r7.toString()
            r2.println(r7)
            java.util.Map<android.os.IBinder, com.android.server.BluetoothManagerService$ClientDeathRecipient> r7 = r1.mBleApps
            java.util.Collection r7 = r7.values()
            java.util.Iterator r7 = r7.iterator()
        L_0x01c1:
            boolean r9 = r7.hasNext()
            if (r9 == 0) goto L_0x01e4
            java.lang.Object r9 = r7.next()
            com.android.server.BluetoothManagerService$ClientDeathRecipient r9 = (com.android.server.BluetoothManagerService.ClientDeathRecipient) r9
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            r10.append(r8)
            java.lang.String r12 = r9.getPackageName()
            r10.append(r12)
            java.lang.String r10 = r10.toString()
            r2.println(r10)
            goto L_0x01c1
        L_0x01e4:
            r2.println(r11)
            r21.flush()
            int r7 = r0.length
            if (r7 != 0) goto L_0x01f5
            java.lang.String[] r0 = new java.lang.String[r6]
            java.lang.String r6 = "--print"
            r0[r5] = r6
            r5 = r0
            goto L_0x01f6
        L_0x01f5:
            r5 = r0
        L_0x01f6:
            android.os.IBinder r0 = r1.mBluetoothBinder
            if (r0 != 0) goto L_0x01ff
            java.lang.String r3 = "Bluetooth Service not connected"
            r6 = r20
            goto L_0x020a
        L_0x01ff:
            r6 = r20
            r0.dump(r6, r5)     // Catch:{ RemoteException -> 0x0205 }
            goto L_0x020a
        L_0x0205:
            r0 = move-exception
            r7 = r0
            r0 = r7
            java.lang.String r3 = "RemoteException while dumping Bluetooth Service"
        L_0x020a:
            if (r3 == 0) goto L_0x0212
            if (r4 == 0) goto L_0x020f
            return
        L_0x020f:
            r2.println(r3)
        L_0x0212:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.BluetoothManagerService.dump(java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[]):void");
    }

    /* access modifiers changed from: private */
    public static String getEnableDisableReasonString(int reason) {
        switch (reason) {
            case 1:
                return "APPLICATION_REQUEST";
            case 2:
                return "AIRPLANE_MODE";
            case 3:
                return "DISALLOWED";
            case 4:
                return "RESTARTED";
            case 5:
                return "START_ERROR";
            case 6:
                return "SYSTEM_BOOT";
            case 7:
                return "CRASH";
            case 8:
                return "USER_SWITCH";
            case 9:
                return "RESTORE_USER_SETTING";
            default:
                return "UNKNOWN[" + reason + "]";
        }
    }
}
