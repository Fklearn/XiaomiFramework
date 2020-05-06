package com.android.server.power.batterysaver;

import android.app.ActivityManagerInternal;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.BatterySaverPolicyConfig;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.PowerManagerInternal;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.Preconditions;
import com.android.server.LocalServices;
import com.android.server.power.batterysaver.BatterySaverPolicy;
import java.util.ArrayList;

public class BatterySaverController implements BatterySaverPolicy.BatterySaverPolicyListener {
    static final boolean DEBUG = false;
    public static final int REASON_ADAPTIVE_DYNAMIC_POWER_SAVINGS_CHANGED = 11;
    public static final int REASON_DYNAMIC_POWER_SAVINGS_AUTOMATIC_OFF = 10;
    public static final int REASON_DYNAMIC_POWER_SAVINGS_AUTOMATIC_ON = 9;
    public static final int REASON_INTERACTIVE_CHANGED = 5;
    public static final int REASON_MANUAL_OFF = 3;
    public static final int REASON_MANUAL_ON = 2;
    public static final int REASON_PERCENTAGE_AUTOMATIC_OFF = 1;
    public static final int REASON_PERCENTAGE_AUTOMATIC_ON = 0;
    public static final int REASON_PLUGGED_IN = 7;
    public static final int REASON_POLICY_CHANGED = 6;
    public static final int REASON_SETTING_CHANGED = 8;
    public static final int REASON_STICKY_RESTORE = 4;
    public static final int REASON_TIMEOUT = 12;
    static final String TAG = "BatterySaverController";
    @GuardedBy({"mLock"})
    private boolean mAdaptiveEnabled;
    private boolean mAdaptivePreviouslyEnabled;
    private final BatterySaverPolicy mBatterySaverPolicy;
    private final BatterySavingStats mBatterySavingStats;
    private final Context mContext;
    private final FileUpdater mFileUpdater;
    @GuardedBy({"mLock"})
    private boolean mFullEnabled;
    private boolean mFullPreviouslyEnabled;
    /* access modifiers changed from: private */
    public final MyHandler mHandler;
    @GuardedBy({"mLock"})
    private boolean mIsInteractive;
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public boolean mIsPluggedIn;
    @GuardedBy({"mLock"})
    private final ArrayList<PowerManagerInternal.LowPowerModeListener> mListeners = new ArrayList<>();
    /* access modifiers changed from: private */
    public final Object mLock;
    /* access modifiers changed from: private */
    public final Plugin[] mPlugins;
    private PowerManager mPowerManager;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onReceive(android.content.Context r8, android.content.Intent r9) {
            /*
                r7 = this;
                java.lang.String r0 = r9.getAction()
                int r1 = r0.hashCode()
                r2 = 4
                r3 = 3
                r4 = 2
                r5 = 1
                r6 = 0
                switch(r1) {
                    case -2128145023: goto L_0x0039;
                    case -1538406691: goto L_0x002f;
                    case -1454123155: goto L_0x0025;
                    case 498807504: goto L_0x001b;
                    case 870701415: goto L_0x0011;
                    default: goto L_0x0010;
                }
            L_0x0010:
                goto L_0x0043
            L_0x0011:
                java.lang.String r1 = "android.os.action.DEVICE_IDLE_MODE_CHANGED"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x0010
                r0 = r3
                goto L_0x0044
            L_0x001b:
                java.lang.String r1 = "android.os.action.LIGHT_DEVICE_IDLE_MODE_CHANGED"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x0010
                r0 = r2
                goto L_0x0044
            L_0x0025:
                java.lang.String r1 = "android.intent.action.SCREEN_ON"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x0010
                r0 = r6
                goto L_0x0044
            L_0x002f:
                java.lang.String r1 = "android.intent.action.BATTERY_CHANGED"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x0010
                r0 = r4
                goto L_0x0044
            L_0x0039:
                java.lang.String r1 = "android.intent.action.SCREEN_OFF"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x0010
                r0 = r5
                goto L_0x0044
            L_0x0043:
                r0 = -1
            L_0x0044:
                if (r0 == 0) goto L_0x0070
                if (r0 == r5) goto L_0x0070
                if (r0 == r4) goto L_0x004f
                if (r0 == r3) goto L_0x0067
                if (r0 == r2) goto L_0x0067
                goto L_0x0089
            L_0x004f:
                com.android.server.power.batterysaver.BatterySaverController r0 = com.android.server.power.batterysaver.BatterySaverController.this
                java.lang.Object r0 = r0.mLock
                monitor-enter(r0)
                com.android.server.power.batterysaver.BatterySaverController r1 = com.android.server.power.batterysaver.BatterySaverController.this     // Catch:{ all -> 0x006d }
                java.lang.String r2 = "plugged"
                int r2 = r9.getIntExtra(r2, r6)     // Catch:{ all -> 0x006d }
                if (r2 == 0) goto L_0x0062
                goto L_0x0063
            L_0x0062:
                r5 = r6
            L_0x0063:
                boolean unused = r1.mIsPluggedIn = r5     // Catch:{ all -> 0x006d }
                monitor-exit(r0)     // Catch:{ all -> 0x006d }
            L_0x0067:
                com.android.server.power.batterysaver.BatterySaverController r0 = com.android.server.power.batterysaver.BatterySaverController.this
                r0.updateBatterySavingStats()
                goto L_0x0089
            L_0x006d:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x006d }
                throw r1
            L_0x0070:
                com.android.server.power.batterysaver.BatterySaverController r0 = com.android.server.power.batterysaver.BatterySaverController.this
                boolean r0 = r0.isPolicyEnabled()
                if (r0 != 0) goto L_0x007e
                com.android.server.power.batterysaver.BatterySaverController r0 = com.android.server.power.batterysaver.BatterySaverController.this
                r0.updateBatterySavingStats()
                return
            L_0x007e:
                com.android.server.power.batterysaver.BatterySaverController r0 = com.android.server.power.batterysaver.BatterySaverController.this
                com.android.server.power.batterysaver.BatterySaverController$MyHandler r0 = r0.mHandler
                r1 = 5
                r0.postStateChanged(r6, r1)
            L_0x0089:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.batterysaver.BatterySaverController.AnonymousClass1.onReceive(android.content.Context, android.content.Intent):void");
        }
    };

    public interface Plugin {
        void onBatterySaverChanged(BatterySaverController batterySaverController);

        void onSystemReady(BatterySaverController batterySaverController);
    }

    static String reasonToString(int reason) {
        switch (reason) {
            case 0:
                return "Percentage Auto ON";
            case 1:
                return "Percentage Auto OFF";
            case 2:
                return "Manual ON";
            case 3:
                return "Manual OFF";
            case 4:
                return "Sticky restore";
            case 5:
                return "Interactivity changed";
            case 6:
                return "Policy changed";
            case 7:
                return "Plugged in";
            case 8:
                return "Setting changed";
            case 9:
                return "Dynamic Warning Auto ON";
            case 10:
                return "Dynamic Warning Auto OFF";
            case 11:
                return "Adaptive Power Savings changed";
            case 12:
                return "timeout";
            default:
                return "Unknown reason: " + reason;
        }
    }

    public BatterySaverController(Object lock, Context context, Looper looper, BatterySaverPolicy policy, BatterySavingStats batterySavingStats) {
        this.mLock = lock;
        this.mContext = context;
        this.mHandler = new MyHandler(looper);
        this.mBatterySaverPolicy = policy;
        this.mBatterySaverPolicy.addListener(this);
        this.mFileUpdater = new FileUpdater(context);
        this.mBatterySavingStats = batterySavingStats;
        this.mPlugins = new Plugin[]{new BatterySaverLocationPlugin(this.mContext)};
    }

    public void addListener(PowerManagerInternal.LowPowerModeListener listener) {
        synchronized (this.mLock) {
            this.mListeners.add(listener);
        }
    }

    public void systemReady() {
        IntentFilter filter = new IntentFilter("android.intent.action.SCREEN_ON");
        filter.addAction("android.intent.action.SCREEN_OFF");
        filter.addAction("android.intent.action.BATTERY_CHANGED");
        filter.addAction("android.os.action.DEVICE_IDLE_MODE_CHANGED");
        filter.addAction("android.os.action.LIGHT_DEVICE_IDLE_MODE_CHANGED");
        this.mContext.registerReceiver(this.mReceiver, filter);
        this.mFileUpdater.systemReady(((ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class)).isRuntimeRestarted());
        this.mHandler.postSystemReady();
    }

    private PowerManager getPowerManager() {
        if (this.mPowerManager == null) {
            this.mPowerManager = (PowerManager) Preconditions.checkNotNull((PowerManager) this.mContext.getSystemService(PowerManager.class));
        }
        return this.mPowerManager;
    }

    public void onBatterySaverPolicyChanged(BatterySaverPolicy policy) {
        if (isPolicyEnabled()) {
            this.mHandler.postStateChanged(true, 6);
        }
    }

    private class MyHandler extends Handler {
        private static final int ARG_DONT_SEND_BROADCAST = 0;
        private static final int ARG_SEND_BROADCAST = 1;
        private static final int MSG_STATE_CHANGED = 1;
        private static final int MSG_SYSTEM_READY = 2;

        public MyHandler(Looper looper) {
            super(looper);
        }

        /* access modifiers changed from: package-private */
        public void postStateChanged(boolean sendBroadcast, int reason) {
            obtainMessage(1, sendBroadcast ? 1 : 0, reason).sendToTarget();
        }

        public void postSystemReady() {
            obtainMessage(2, 0, 0).sendToTarget();
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v1, resolved type: boolean} */
        /* JADX WARNING: type inference failed for: r1v0 */
        /* JADX WARNING: type inference failed for: r1v2 */
        /* JADX WARNING: type inference failed for: r1v3, types: [int] */
        /* JADX WARNING: type inference failed for: r1v5 */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void dispatchMessage(android.os.Message r6) {
            /*
                r5 = this;
                int r0 = r6.what
                r1 = 0
                r2 = 1
                if (r0 == r2) goto L_0x001d
                r2 = 2
                if (r0 == r2) goto L_0x000a
                goto L_0x002a
            L_0x000a:
                com.android.server.power.batterysaver.BatterySaverController r0 = com.android.server.power.batterysaver.BatterySaverController.this
                com.android.server.power.batterysaver.BatterySaverController$Plugin[] r0 = r0.mPlugins
                int r2 = r0.length
            L_0x0011:
                if (r1 >= r2) goto L_0x002a
                r3 = r0[r1]
                com.android.server.power.batterysaver.BatterySaverController r4 = com.android.server.power.batterysaver.BatterySaverController.this
                r3.onSystemReady(r4)
                int r1 = r1 + 1
                goto L_0x0011
            L_0x001d:
                com.android.server.power.batterysaver.BatterySaverController r0 = com.android.server.power.batterysaver.BatterySaverController.this
                int r3 = r6.arg1
                if (r3 != r2) goto L_0x0024
                r1 = r2
            L_0x0024:
                int r2 = r6.arg2
                r0.handleBatterySaverStateChanged(r1, r2)
            L_0x002a:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.batterysaver.BatterySaverController.MyHandler.dispatchMessage(android.os.Message):void");
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0018, code lost:
        return;
     */
    @com.android.internal.annotations.VisibleForTesting
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void enableBatterySaver(boolean r4, int r5) {
        /*
            r3 = this;
            java.lang.Object r0 = r3.mLock
            monitor-enter(r0)
            boolean r1 = r3.mFullEnabled     // Catch:{ all -> 0x0019 }
            if (r1 != r4) goto L_0x0009
            monitor-exit(r0)     // Catch:{ all -> 0x0019 }
            return
        L_0x0009:
            r3.mFullEnabled = r4     // Catch:{ all -> 0x0019 }
            boolean r1 = r3.updatePolicyLevelLocked()     // Catch:{ all -> 0x0019 }
            if (r1 == 0) goto L_0x0017
            com.android.server.power.batterysaver.BatterySaverController$MyHandler r1 = r3.mHandler     // Catch:{ all -> 0x0019 }
            r2 = 1
            r1.postStateChanged(r2, r5)     // Catch:{ all -> 0x0019 }
        L_0x0017:
            monitor-exit(r0)     // Catch:{ all -> 0x0019 }
            return
        L_0x0019:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0019 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.batterysaver.BatterySaverController.enableBatterySaver(boolean, int):void");
    }

    private boolean updatePolicyLevelLocked() {
        if (this.mFullEnabled) {
            return this.mBatterySaverPolicy.setPolicyLevel(2);
        }
        if (this.mAdaptiveEnabled) {
            return this.mBatterySaverPolicy.setPolicyLevel(1);
        }
        return this.mBatterySaverPolicy.setPolicyLevel(0);
    }

    public boolean isEnabled() {
        boolean z;
        synchronized (this.mLock) {
            if (!this.mFullEnabled) {
                if (!this.mAdaptiveEnabled || !this.mBatterySaverPolicy.shouldAdvertiseIsEnabled()) {
                    z = false;
                }
            }
            z = true;
        }
        return z;
    }

    /* access modifiers changed from: private */
    public boolean isPolicyEnabled() {
        boolean z;
        synchronized (this.mLock) {
            if (!this.mFullEnabled) {
                if (!this.mAdaptiveEnabled) {
                    z = false;
                }
            }
            z = true;
        }
        return z;
    }

    /* access modifiers changed from: package-private */
    public boolean isFullEnabled() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mFullEnabled;
        }
        return z;
    }

    /* access modifiers changed from: package-private */
    public boolean isAdaptiveEnabled() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mAdaptiveEnabled;
        }
        return z;
    }

    /* access modifiers changed from: package-private */
    public boolean setAdaptivePolicyLocked(String settings, String deviceSpecificSettings, int reason) {
        return setAdaptivePolicyLocked(BatterySaverPolicy.Policy.fromSettings(settings, deviceSpecificSettings), reason);
    }

    /* access modifiers changed from: package-private */
    public boolean setAdaptivePolicyLocked(BatterySaverPolicyConfig config, int reason) {
        return setAdaptivePolicyLocked(BatterySaverPolicy.Policy.fromConfig(config), reason);
    }

    /* access modifiers changed from: package-private */
    public boolean setAdaptivePolicyLocked(BatterySaverPolicy.Policy policy, int reason) {
        if (!this.mBatterySaverPolicy.setAdaptivePolicyLocked(policy)) {
            return false;
        }
        this.mHandler.postStateChanged(true, reason);
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean resetAdaptivePolicyLocked(int reason) {
        if (!this.mBatterySaverPolicy.resetAdaptivePolicyLocked()) {
            return false;
        }
        this.mHandler.postStateChanged(true, reason);
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean setAdaptivePolicyEnabledLocked(boolean enabled, int reason) {
        if (this.mAdaptiveEnabled == enabled) {
            return false;
        }
        this.mAdaptiveEnabled = enabled;
        if (!updatePolicyLevelLocked()) {
            return false;
        }
        this.mHandler.postStateChanged(true, reason);
        return true;
    }

    public boolean isInteractive() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mIsInteractive;
        }
        return z;
    }

    public BatterySaverPolicy getBatterySaverPolicy() {
        return this.mBatterySaverPolicy;
    }

    public boolean isLaunchBoostDisabled() {
        return isPolicyEnabled() && this.mBatterySaverPolicy.isLaunchBoostDisabled();
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x001e  */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x0020  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x0025  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0027  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x002c  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x002e  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0034  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x003b  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x005c  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0063  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void handleBatterySaverStateChanged(boolean r12, int r13) {
        /*
            r11 = this;
            android.os.PowerManager r0 = r11.getPowerManager()
            boolean r0 = r0.isInteractive()
            java.lang.Object r8 = r11.mLock
            monitor-enter(r8)
            boolean r1 = r11.mFullEnabled     // Catch:{ all -> 0x00f6 }
            r2 = 1
            r9 = 0
            if (r1 != 0) goto L_0x0018
            boolean r1 = r11.mAdaptiveEnabled     // Catch:{ all -> 0x00f6 }
            if (r1 == 0) goto L_0x0016
            goto L_0x0018
        L_0x0016:
            r1 = r9
            goto L_0x0019
        L_0x0018:
            r1 = r2
        L_0x0019:
            r10 = r1
            boolean r1 = r11.mFullPreviouslyEnabled     // Catch:{ all -> 0x00f6 }
            if (r1 == 0) goto L_0x0020
            r1 = r2
            goto L_0x0021
        L_0x0020:
            r1 = r9
        L_0x0021:
            boolean r3 = r11.mAdaptivePreviouslyEnabled     // Catch:{ all -> 0x00f6 }
            if (r3 == 0) goto L_0x0027
            r3 = r2
            goto L_0x0028
        L_0x0027:
            r3 = r9
        L_0x0028:
            boolean r4 = r11.mFullEnabled     // Catch:{ all -> 0x00f6 }
            if (r4 == 0) goto L_0x002e
            r4 = r2
            goto L_0x002f
        L_0x002e:
            r4 = r9
        L_0x002f:
            boolean r5 = r11.mAdaptiveEnabled     // Catch:{ all -> 0x00f6 }
            if (r10 == 0) goto L_0x003b
            com.android.server.power.batterysaver.BatterySaverPolicy r2 = r11.mBatterySaverPolicy     // Catch:{ all -> 0x00f6 }
            java.lang.String r2 = r2.toEventLogString()     // Catch:{ all -> 0x00f6 }
            goto L_0x003d
        L_0x003b:
            java.lang.String r2 = ""
        L_0x003d:
            r6 = r2
            r2 = r3
            r3 = r4
            r4 = r5
            r5 = r0
            r7 = r13
            com.android.server.EventLogTags.writeBatterySaverMode(r1, r2, r3, r4, r5, r6, r7)     // Catch:{ all -> 0x00f6 }
            boolean r1 = r11.mFullEnabled     // Catch:{ all -> 0x00f6 }
            r11.mFullPreviouslyEnabled = r1     // Catch:{ all -> 0x00f6 }
            boolean r1 = r11.mAdaptiveEnabled     // Catch:{ all -> 0x00f6 }
            r11.mAdaptivePreviouslyEnabled = r1     // Catch:{ all -> 0x00f6 }
            java.util.ArrayList<android.os.PowerManagerInternal$LowPowerModeListener> r1 = r11.mListeners     // Catch:{ all -> 0x00f6 }
            android.os.PowerManagerInternal$LowPowerModeListener[] r2 = new android.os.PowerManagerInternal.LowPowerModeListener[r9]     // Catch:{ all -> 0x00f6 }
            java.lang.Object[] r1 = r1.toArray(r2)     // Catch:{ all -> 0x00f6 }
            android.os.PowerManagerInternal$LowPowerModeListener[] r1 = (android.os.PowerManagerInternal.LowPowerModeListener[]) r1     // Catch:{ all -> 0x00f6 }
            r11.mIsInteractive = r0     // Catch:{ all -> 0x00f6 }
            if (r10 == 0) goto L_0x0063
            com.android.server.power.batterysaver.BatterySaverPolicy r2 = r11.mBatterySaverPolicy     // Catch:{ all -> 0x00f6 }
            android.util.ArrayMap r2 = r2.getFileValues(r0)     // Catch:{ all -> 0x00f6 }
            goto L_0x0064
        L_0x0063:
            r2 = 0
        L_0x0064:
            monitor-exit(r8)     // Catch:{ all -> 0x00f6 }
            java.lang.Class<android.os.PowerManagerInternal> r3 = android.os.PowerManagerInternal.class
            java.lang.Object r3 = com.android.server.LocalServices.getService(r3)
            android.os.PowerManagerInternal r3 = (android.os.PowerManagerInternal) r3
            if (r3 == 0) goto L_0x0077
            r4 = 5
            boolean r5 = r11.isEnabled()
            r3.powerHint(r4, r5)
        L_0x0077:
            r11.updateBatterySavingStats()
            boolean r4 = com.android.internal.util.ArrayUtils.isEmpty(r2)
            if (r4 == 0) goto L_0x0086
            com.android.server.power.batterysaver.FileUpdater r4 = r11.mFileUpdater
            r4.restoreDefault()
            goto L_0x008b
        L_0x0086:
            com.android.server.power.batterysaver.FileUpdater r4 = r11.mFileUpdater
            r4.writeFiles(r2)
        L_0x008b:
            com.android.server.power.batterysaver.BatterySaverController$Plugin[] r4 = r11.mPlugins
            int r5 = r4.length
            r6 = r9
        L_0x008f:
            if (r6 >= r5) goto L_0x0099
            r7 = r4[r6]
            r7.onBatterySaverChanged(r11)
            int r6 = r6 + 1
            goto L_0x008f
        L_0x0099:
            if (r12 == 0) goto L_0x00f5
            android.content.Intent r4 = new android.content.Intent
            java.lang.String r5 = "android.os.action.POWER_SAVE_MODE_CHANGING"
            r4.<init>(r5)
            boolean r5 = r11.isEnabled()
            java.lang.String r6 = "mode"
            android.content.Intent r4 = r4.putExtra(r6, r5)
            r5 = 1073741824(0x40000000, float:2.0)
            android.content.Intent r4 = r4.addFlags(r5)
            android.content.Context r6 = r11.mContext
            android.os.UserHandle r7 = android.os.UserHandle.ALL
            r6.sendBroadcastAsUser(r4, r7)
            android.content.Intent r6 = new android.content.Intent
            java.lang.String r7 = "android.os.action.POWER_SAVE_MODE_CHANGED"
            r6.<init>(r7)
            r4 = r6
            r4.addFlags(r5)
            android.content.Context r6 = r11.mContext
            android.os.UserHandle r7 = android.os.UserHandle.ALL
            r6.sendBroadcastAsUser(r4, r7)
            android.content.Intent r6 = new android.content.Intent
            java.lang.String r7 = "android.os.action.POWER_SAVE_MODE_CHANGED_INTERNAL"
            r6.<init>(r7)
            r4 = r6
            r4.addFlags(r5)
            android.content.Context r5 = r11.mContext
            android.os.UserHandle r6 = android.os.UserHandle.ALL
            java.lang.String r7 = "android.permission.DEVICE_POWER"
            r5.sendBroadcastAsUser(r4, r6, r7)
            int r5 = r1.length
        L_0x00e1:
            if (r9 >= r5) goto L_0x00f5
            r6 = r1[r9]
            com.android.server.power.batterysaver.BatterySaverPolicy r7 = r11.mBatterySaverPolicy
            int r8 = r6.getServiceType()
            android.os.PowerSaveState r7 = r7.getBatterySaverPolicy(r8)
            r6.onLowPowerModeChanged(r7)
            int r9 = r9 + 1
            goto L_0x00e1
        L_0x00f5:
            return
        L_0x00f6:
            r1 = move-exception
            monitor-exit(r8)     // Catch:{ all -> 0x00f6 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.batterysaver.BatterySaverController.handleBatterySaverStateChanged(boolean, int):void");
    }

    /* access modifiers changed from: private */
    public void updateBatterySavingStats() {
        int dozeMode;
        PowerManager pm = getPowerManager();
        if (pm == null) {
            Slog.wtf(TAG, "PowerManager not initialized");
            return;
        }
        boolean isInteractive = pm.isInteractive();
        int i = 2;
        int i2 = 1;
        if (pm.isDeviceIdleMode()) {
            dozeMode = 2;
        } else if (pm.isLightDeviceIdleMode()) {
            dozeMode = 1;
        } else {
            dozeMode = 0;
        }
        synchronized (this.mLock) {
            if (this.mIsPluggedIn) {
                this.mBatterySavingStats.startCharging();
                return;
            }
            BatterySavingStats batterySavingStats = this.mBatterySavingStats;
            if (this.mFullEnabled) {
                i = 1;
            } else if (!this.mAdaptiveEnabled) {
                i = 0;
            }
            if (!isInteractive) {
                i2 = 0;
            }
            batterySavingStats.transitionState(i, i2, dozeMode);
        }
    }
}
