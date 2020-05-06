package com.android.server.power;

import android.app.ActivityManagerInternal;
import android.app.AppOpsManager;
import android.app.trust.TrustManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.input.InputManagerInternal;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.metrics.LogMaker;
import android.miui.Manifest;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManagerInternal;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.WorkSource;
import android.provider.Settings;
import android.util.EventLog;
import android.util.Slog;
import android.util.StatsLog;
import android.view.IWindowManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.IBatteryStats;
import com.android.internal.logging.MetricsLogger;
import com.android.server.EventLogTags;
import com.android.server.LocalServices;
import com.android.server.inputmethod.InputMethodManagerInternal;
import com.android.server.policy.WindowManagerPolicy;
import com.android.server.statusbar.StatusBarManagerInternal;

@VisibleForTesting
public class Notifier {
    private static final boolean DEBUG = false;
    private static final int INTERACTIVE_STATE_ASLEEP = 2;
    private static final int INTERACTIVE_STATE_AWAKE = 1;
    private static final int INTERACTIVE_STATE_UNKNOWN = 0;
    private static final int MSG_BROADCAST = 2;
    private static final int MSG_PROFILE_TIMED_OUT = 5;
    private static final int MSG_SCREEN_BRIGHTNESS_BOOST_CHANGED = 4;
    private static final int MSG_USER_ACTIVITY = 1;
    private static final int MSG_WIRED_CHARGING_STARTED = 6;
    private static final int MSG_WIRELESS_CHARGING_STARTED = 3;
    private static final String TAG = "PowerManagerNotifier";
    private static final AudioAttributes VIBRATION_ATTRIBUTES = new AudioAttributes.Builder().setContentType(4).build();
    private static final VibrationEffect WIRELESS_CHARGING_VIBRATION_EFFECT = VibrationEffect.createWaveform(WIRELESS_VIBRATION_TIME, WIRELESS_VIBRATION_AMPLITUDE, -1);
    private static final int[] WIRELESS_VIBRATION_AMPLITUDE = {1, 4, 11, 25, 44, 67, 91, HdmiCecKeycode.CEC_KEYCODE_F2_RED, 123, 103, 79, 55, 34, 17, 7, 2};
    private static final long[] WIRELESS_VIBRATION_TIME = {40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40};
    /* access modifiers changed from: private */
    public final ActivityManagerInternal mActivityManagerInternal;
    private final AppOpsManager mAppOps;
    private final IBatteryStats mBatteryStats;
    private boolean mBeingHangUp;
    private boolean mBroadcastInProgress;
    /* access modifiers changed from: private */
    public long mBroadcastStartTime;
    private int mBroadcastedInteractiveState;
    private final Context mContext;
    private boolean mDisableRotationDueToHangUp;
    private final BroadcastReceiver mGoToSleepBroadcastDone = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            EventLog.writeEvent(EventLogTags.POWER_SCREEN_BROADCAST_DONE, new Object[]{0, Long.valueOf(SystemClock.uptimeMillis() - Notifier.this.mBroadcastStartTime), 1});
            Notifier.this.sendNextBroadcast();
        }
    };
    private final NotifierHandler mHandler;
    private final InputManagerInternal mInputManagerInternal;
    private final InputMethodManagerInternal mInputMethodManagerInternal;
    private boolean mInteractive = true;
    /* access modifiers changed from: private */
    public int mInteractiveChangeReason;
    private long mInteractiveChangeStartTime;
    private boolean mInteractiveChanging;
    private final Object mLock = new Object();
    private boolean mPendingGoToSleepBroadcast;
    private int mPendingInteractiveState;
    private boolean mPendingWakeUpBroadcast;
    /* access modifiers changed from: private */
    public final WindowManagerPolicy mPolicy;
    private final BroadcastReceiver mScreeBrightnessBoostChangedDone = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Notifier.this.mSuspendBlocker.release();
        }
    };
    private final Intent mScreenBrightnessBoostIntent;
    private final Intent mScreenOffIntent;
    private final Intent mScreenOnIntent;
    private final StatusBarManagerInternal mStatusBarManagerInternal;
    /* access modifiers changed from: private */
    public final SuspendBlocker mSuspendBlocker;
    private final boolean mSuspendWhenScreenOffDueToProximityConfig;
    private final TrustManager mTrustManager;
    private boolean mUserActivityPending;
    private final Vibrator mVibrator;
    private final BroadcastReceiver mWakeUpBroadcastDone = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            EventLog.writeEvent(EventLogTags.POWER_SCREEN_BROADCAST_DONE, new Object[]{1, Long.valueOf(SystemClock.uptimeMillis() - Notifier.this.mBroadcastStartTime), 1});
            Notifier.this.sendNextBroadcast();
        }
    };

    public Notifier(Looper looper, Context context, IBatteryStats batteryStats, SuspendBlocker suspendBlocker, WindowManagerPolicy policy) {
        this.mContext = context;
        this.mBatteryStats = batteryStats;
        this.mAppOps = (AppOpsManager) this.mContext.getSystemService(AppOpsManager.class);
        this.mSuspendBlocker = suspendBlocker;
        this.mPolicy = policy;
        this.mActivityManagerInternal = (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
        this.mInputManagerInternal = (InputManagerInternal) LocalServices.getService(InputManagerInternal.class);
        this.mInputMethodManagerInternal = (InputMethodManagerInternal) LocalServices.getService(InputMethodManagerInternal.class);
        this.mStatusBarManagerInternal = (StatusBarManagerInternal) LocalServices.getService(StatusBarManagerInternal.class);
        this.mTrustManager = (TrustManager) this.mContext.getSystemService(TrustManager.class);
        this.mVibrator = (Vibrator) this.mContext.getSystemService(Vibrator.class);
        this.mHandler = new NotifierHandler(looper);
        this.mScreenOnIntent = new Intent("android.intent.action.SCREEN_ON");
        this.mScreenOnIntent.addFlags(1344274432);
        this.mScreenOffIntent = new Intent("android.intent.action.SCREEN_OFF");
        this.mScreenOffIntent.addFlags(1344274432);
        this.mScreenBrightnessBoostIntent = new Intent("android.os.action.SCREEN_BRIGHTNESS_BOOST_CHANGED");
        this.mScreenBrightnessBoostIntent.addFlags(1342177280);
        this.mSuspendWhenScreenOffDueToProximityConfig = context.getResources().getBoolean(17891549);
        try {
            this.mBatteryStats.noteInteractive(true);
        } catch (RemoteException e) {
        }
        StatsLog.write(33, 1);
    }

    public void onWakeLockAcquired(int flags, String tag, String packageName, int ownerUid, int ownerPid, WorkSource workSource, String historyTag) {
        int i = ownerUid;
        int monitorType = getBatteryStatsWakeLockMonitorType(flags);
        if (monitorType >= 0) {
            boolean unimportantForLogging = i == 1000 && (flags & 1073741824) != 0;
            if (workSource != null) {
                try {
                    this.mBatteryStats.noteStartWakelockFromSource(workSource, ownerPid, tag, historyTag, monitorType, unimportantForLogging);
                    String str = packageName;
                } catch (RemoteException e) {
                    String str2 = packageName;
                }
            } else {
                this.mBatteryStats.noteStartWakelock(ownerUid, ownerPid, tag, historyTag, monitorType, unimportantForLogging);
                try {
                    this.mAppOps.startOpNoThrow(40, i, packageName);
                } catch (RemoteException e2) {
                }
            }
        } else {
            String str3 = packageName;
        }
    }

    public void onLongPartialWakeLockStart(String tag, int ownerUid, WorkSource workSource, String historyTag) {
        if (workSource != null) {
            try {
                this.mBatteryStats.noteLongPartialWakelockStartFromSource(tag, historyTag, workSource);
                StatsLog.write(11, workSource, tag, historyTag, 1);
            } catch (RemoteException e) {
            }
        } else {
            this.mBatteryStats.noteLongPartialWakelockStart(tag, historyTag, ownerUid);
            StatsLog.write_non_chained(11, ownerUid, (String) null, tag, historyTag, 1);
        }
    }

    public void onLongPartialWakeLockFinish(String tag, int ownerUid, WorkSource workSource, String historyTag) {
        if (workSource != null) {
            try {
                this.mBatteryStats.noteLongPartialWakelockFinishFromSource(tag, historyTag, workSource);
                StatsLog.write(11, workSource, tag, historyTag, 0);
            } catch (RemoteException e) {
            }
        } else {
            this.mBatteryStats.noteLongPartialWakelockFinish(tag, historyTag, ownerUid);
            StatsLog.write_non_chained(11, ownerUid, (String) null, tag, historyTag, 0);
        }
    }

    public void onWakeLockChanging(int flags, String tag, String packageName, int ownerUid, int ownerPid, WorkSource workSource, String historyTag, int newFlags, String newTag, String newPackageName, int newOwnerUid, int newOwnerPid, WorkSource newWorkSource, String newHistoryTag) {
        int i = newFlags;
        int monitorType = getBatteryStatsWakeLockMonitorType(flags);
        int newMonitorType = getBatteryStatsWakeLockMonitorType(i);
        if (workSource == null || newWorkSource == null || monitorType < 0 || newMonitorType < 0) {
            int i2 = newOwnerUid;
            onWakeLockReleased(flags, tag, packageName, ownerUid, ownerPid, workSource, historyTag);
            onWakeLockAcquired(newFlags, newTag, newPackageName, newOwnerUid, newOwnerPid, newWorkSource, newHistoryTag);
            return;
        }
        try {
            this.mBatteryStats.noteChangeWakelockFromSource(workSource, ownerPid, tag, historyTag, monitorType, newWorkSource, newOwnerPid, newTag, newHistoryTag, newMonitorType, newOwnerUid == 1000 && (1073741824 & i) != 0);
        } catch (RemoteException e) {
        }
    }

    public void onWakeLockReleased(int flags, String tag, String packageName, int ownerUid, int ownerPid, WorkSource workSource, String historyTag) {
        int monitorType = getBatteryStatsWakeLockMonitorType(flags);
        if (monitorType < 0) {
            return;
        }
        if (workSource != null) {
            try {
                this.mBatteryStats.noteStopWakelockFromSource(workSource, ownerPid, tag, historyTag, monitorType);
            } catch (RemoteException e) {
            }
        } else {
            this.mBatteryStats.noteStopWakelock(ownerUid, ownerPid, tag, historyTag, monitorType);
            this.mAppOps.finishOp(40, ownerUid, packageName);
        }
    }

    private int getBatteryStatsWakeLockMonitorType(int flags) {
        int i = 65535 & flags;
        if (i == 1) {
            return 0;
        }
        if (i == 6 || i == 10) {
            return 1;
        }
        if (i == 32) {
            return this.mSuspendWhenScreenOffDueToProximityConfig ? -1 : 0;
        }
        if (i == 64 || i != 128) {
            return -1;
        }
        return 18;
    }

    public void onWakefulnessChangeStarted(final int wakefulness, int reason, long eventTime) {
        int i;
        if (wakefulness != 4) {
            boolean interactive = PowerManagerInternal.isInteractive(wakefulness);
            this.mHandler.post(new Runnable() {
                public void run() {
                    Notifier.this.mActivityManagerInternal.onWakefulnessChanged(wakefulness);
                }
            });
            if (this.mInteractive != interactive) {
                if (this.mInteractiveChanging) {
                    handleLateInteractiveChange();
                }
                this.mInputManagerInternal.setInteractive(interactive);
                this.mInputMethodManagerInternal.setInteractive(interactive);
                try {
                    this.mBatteryStats.noteInteractive(interactive);
                } catch (RemoteException e) {
                }
                if (interactive) {
                    i = 1;
                } else {
                    i = 0;
                }
                StatsLog.write(33, i);
                this.mInteractive = interactive;
                this.mInteractiveChangeReason = reason;
                this.mInteractiveChangeStartTime = eventTime;
                this.mInteractiveChanging = true;
                handleEarlyInteractiveChange();
            }
        }
    }

    public void onWakefulnessChangeFinished() {
        if (this.mInteractiveChanging) {
            this.mInteractiveChanging = false;
            handleLateInteractiveChange();
        }
    }

    private void handleEarlyInteractiveChange() {
        synchronized (this.mLock) {
            if (this.mInteractive) {
                this.mHandler.post(new Runnable() {
                    public void run() {
                        Notifier.this.mPolicy.startedWakingUp(Notifier.translateOnReason(Notifier.this.mInteractiveChangeReason));
                    }
                });
                this.mPendingInteractiveState = 1;
                this.mPendingWakeUpBroadcast = true;
                updatePendingBroadcastLocked();
            } else {
                final int why = translateOffReason(this.mInteractiveChangeReason);
                this.mHandler.post(new Runnable() {
                    public void run() {
                        Notifier.this.mPolicy.startedGoingToSleep(why);
                    }
                });
            }
        }
    }

    private void handleLateInteractiveChange() {
        synchronized (this.mLock) {
            final int interactiveChangeLatency = (int) (SystemClock.uptimeMillis() - this.mInteractiveChangeStartTime);
            if (this.mInteractive) {
                final int why = translateOnReason(this.mInteractiveChangeReason);
                this.mHandler.post(new Runnable() {
                    public void run() {
                        LogMaker log = new LogMaker(198);
                        log.setType(1);
                        log.setSubtype(why);
                        log.setLatency((long) interactiveChangeLatency);
                        log.addTaggedData(1694, Integer.valueOf(Notifier.this.mInteractiveChangeReason));
                        MetricsLogger.action(log);
                        EventLogTags.writePowerScreenState(1, 0, 0, 0, interactiveChangeLatency);
                        Notifier.this.mPolicy.finishedWakingUp(why);
                    }
                });
            } else {
                if (this.mUserActivityPending) {
                    this.mUserActivityPending = false;
                    this.mHandler.removeMessages(1);
                }
                final int why2 = translateOffReason(this.mInteractiveChangeReason);
                this.mHandler.post(new Runnable() {
                    public void run() {
                        LogMaker log = new LogMaker(198);
                        log.setType(2);
                        log.setSubtype(why2);
                        log.setLatency((long) interactiveChangeLatency);
                        log.addTaggedData(1695, Integer.valueOf(Notifier.this.mInteractiveChangeReason));
                        MetricsLogger.action(log);
                        EventLogTags.writePowerScreenState(0, why2, 0, 0, interactiveChangeLatency);
                        Notifier.this.mPolicy.finishedGoingToSleep(why2);
                    }
                });
                this.mPendingInteractiveState = 2;
                this.mPendingGoToSleepBroadcast = true;
                updatePendingBroadcastLocked();
            }
        }
    }

    private static int translateOffReason(int reason) {
        if (reason == 1) {
            return 1;
        }
        if (reason != 2) {
            return 2;
        }
        return 3;
    }

    /* access modifiers changed from: private */
    public static int translateOnReason(int reason) {
        switch (reason) {
            case 1:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 9:
                return 1;
            case 2:
                return 2;
            default:
                return 3;
        }
    }

    public void onScreenBrightnessBoostChanged() {
        this.mSuspendBlocker.acquire();
        Message msg = this.mHandler.obtainMessage(4);
        msg.setAsynchronous(true);
        this.mHandler.sendMessage(msg);
    }

    public void onUserActivity(int event, int uid) {
        try {
            this.mBatteryStats.noteUserActivity(uid, event);
        } catch (RemoteException e) {
        }
        synchronized (this.mLock) {
            if (!this.mUserActivityPending) {
                this.mUserActivityPending = true;
                Message msg = this.mHandler.obtainMessage(1);
                msg.setAsynchronous(true);
                this.mHandler.sendMessage(msg);
            }
        }
    }

    public void onWakeUp(int reason, String details, int reasonUid, String opPackageName, int opUid) {
        try {
            this.mBatteryStats.noteWakeUp(details, reasonUid);
            if (opPackageName != null) {
                this.mAppOps.noteOpNoThrow(61, opUid, opPackageName);
            }
        } catch (RemoteException e) {
        }
    }

    public void onProfileTimeout(int userId) {
        Message msg = this.mHandler.obtainMessage(5);
        msg.setAsynchronous(true);
        msg.arg1 = userId;
        this.mHandler.sendMessage(msg);
    }

    public void onWirelessChargingStarted(int batteryLevel, int userId) {
        this.mSuspendBlocker.acquire();
        Message msg = this.mHandler.obtainMessage(3);
        msg.setAsynchronous(true);
        msg.arg1 = batteryLevel;
        msg.arg2 = userId;
        this.mHandler.sendMessage(msg);
    }

    public void onWiredChargingStarted(int userId) {
        this.mSuspendBlocker.acquire();
        Message msg = this.mHandler.obtainMessage(6);
        msg.setAsynchronous(true);
        msg.arg1 = userId;
        this.mHandler.sendMessage(msg);
    }

    private void updatePendingBroadcastLocked() {
        int i;
        if (!this.mBroadcastInProgress && (i = this.mPendingInteractiveState) != 0) {
            if (this.mPendingWakeUpBroadcast || this.mPendingGoToSleepBroadcast || i != this.mBroadcastedInteractiveState) {
                this.mBroadcastInProgress = true;
                this.mSuspendBlocker.acquire();
                Message msg = this.mHandler.obtainMessage(2);
                msg.setAsynchronous(true);
                this.mHandler.sendMessage(msg);
            }
        }
    }

    private void finishPendingBroadcastLocked() {
        this.mBroadcastInProgress = false;
        this.mSuspendBlocker.release();
    }

    /* access modifiers changed from: private */
    public void sendUserActivity() {
        synchronized (this.mLock) {
            if (this.mUserActivityPending) {
                this.mUserActivityPending = false;
                this.mPolicy.userActivity();
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0049, code lost:
        android.util.EventLog.writeEvent(com.android.server.EventLogTags.POWER_SCREEN_BROADCAST_SEND, 1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x004e, code lost:
        if (r1 != 1) goto L_0x0054;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0050, code lost:
        sendWakeUpBroadcast();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0054, code lost:
        sendGoToSleepBroadcast();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void sendNextBroadcast() {
        /*
            r5 = this;
            java.lang.Object r0 = r5.mLock
            monitor-enter(r0)
            int r1 = r5.mBroadcastedInteractiveState     // Catch:{ all -> 0x0058 }
            r2 = 0
            r3 = 1
            if (r1 != 0) goto L_0x000e
            r5.mPendingWakeUpBroadcast = r2     // Catch:{ all -> 0x0058 }
            r5.mBroadcastedInteractiveState = r3     // Catch:{ all -> 0x0058 }
            goto L_0x0040
        L_0x000e:
            int r1 = r5.mBroadcastedInteractiveState     // Catch:{ all -> 0x0058 }
            if (r1 != r3) goto L_0x002a
            boolean r1 = r5.mPendingWakeUpBroadcast     // Catch:{ all -> 0x0058 }
            r4 = 2
            if (r1 != 0) goto L_0x0025
            boolean r1 = r5.mPendingGoToSleepBroadcast     // Catch:{ all -> 0x0058 }
            if (r1 != 0) goto L_0x0025
            int r1 = r5.mPendingInteractiveState     // Catch:{ all -> 0x0058 }
            if (r1 != r4) goto L_0x0020
            goto L_0x0025
        L_0x0020:
            r5.finishPendingBroadcastLocked()     // Catch:{ all -> 0x0058 }
            monitor-exit(r0)     // Catch:{ all -> 0x0058 }
            return
        L_0x0025:
            r5.mPendingGoToSleepBroadcast = r2     // Catch:{ all -> 0x0058 }
            r5.mBroadcastedInteractiveState = r4     // Catch:{ all -> 0x0058 }
            goto L_0x0040
        L_0x002a:
            boolean r1 = r5.mPendingWakeUpBroadcast     // Catch:{ all -> 0x0058 }
            if (r1 != 0) goto L_0x003c
            boolean r1 = r5.mPendingGoToSleepBroadcast     // Catch:{ all -> 0x0058 }
            if (r1 != 0) goto L_0x003c
            int r1 = r5.mPendingInteractiveState     // Catch:{ all -> 0x0058 }
            if (r1 != r3) goto L_0x0037
            goto L_0x003c
        L_0x0037:
            r5.finishPendingBroadcastLocked()     // Catch:{ all -> 0x0058 }
            monitor-exit(r0)     // Catch:{ all -> 0x0058 }
            return
        L_0x003c:
            r5.mPendingWakeUpBroadcast = r2     // Catch:{ all -> 0x0058 }
            r5.mBroadcastedInteractiveState = r3     // Catch:{ all -> 0x0058 }
        L_0x0040:
            long r1 = android.os.SystemClock.uptimeMillis()     // Catch:{ all -> 0x0058 }
            r5.mBroadcastStartTime = r1     // Catch:{ all -> 0x0058 }
            int r1 = r5.mBroadcastedInteractiveState     // Catch:{ all -> 0x0058 }
            monitor-exit(r0)     // Catch:{ all -> 0x0058 }
            r0 = 2725(0xaa5, float:3.819E-42)
            android.util.EventLog.writeEvent(r0, r3)
            if (r1 != r3) goto L_0x0054
            r5.sendWakeUpBroadcast()
            goto L_0x0057
        L_0x0054:
            r5.sendGoToSleepBroadcast()
        L_0x0057:
            return
        L_0x0058:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0058 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.Notifier.sendNextBroadcast():void");
    }

    /* access modifiers changed from: private */
    public void sendBrightnessBoostChangedBroadcast() {
        this.mContext.sendOrderedBroadcastAsUser(this.mScreenBrightnessBoostIntent, UserHandle.ALL, (String) null, this.mScreeBrightnessBoostChangedDone, this.mHandler, 0, (String) null, (Bundle) null);
    }

    private void sendWakeUpBroadcast() {
        if (this.mActivityManagerInternal.isSystemReady()) {
            this.mContext.sendOrderedBroadcastAsUser(this.mScreenOnIntent, UserHandle.ALL, (String) null, this.mWakeUpBroadcastDone, this.mHandler, 0, (String) null, (Bundle) null);
            return;
        }
        EventLog.writeEvent(EventLogTags.POWER_SCREEN_BROADCAST_STOP, new Object[]{2, 1});
        sendNextBroadcast();
    }

    private void sendGoToSleepBroadcast() {
        if (this.mActivityManagerInternal.isSystemReady()) {
            this.mContext.sendOrderedBroadcastAsUser(this.mScreenOffIntent, UserHandle.ALL, (String) null, this.mGoToSleepBroadcastDone, this.mHandler, 0, (String) null, (Bundle) null);
            return;
        }
        EventLog.writeEvent(EventLogTags.POWER_SCREEN_BROADCAST_STOP, new Object[]{3, 1});
        sendNextBroadcast();
    }

    private void playChargingStartedFeedback(int userId) {
        Ringtone sfx;
        playChargingStartedVibration(userId);
        String soundPath = Settings.Global.getString(this.mContext.getContentResolver(), "wireless_charging_started_sound");
        if (isChargingFeedbackEnabled(userId) && soundPath != null) {
            Uri soundUri = Uri.parse("file://" + soundPath);
            if (soundUri != null && (sfx = RingtoneManager.getRingtone(this.mContext, soundUri)) != null) {
                sfx.setStreamType(1);
                sfx.play();
            }
        }
    }

    /* access modifiers changed from: private */
    public void showWirelessChargingStarted(int batteryLevel, int userId) {
        playChargingStartedFeedback(userId);
        StatusBarManagerInternal statusBarManagerInternal = this.mStatusBarManagerInternal;
        if (statusBarManagerInternal != null) {
            statusBarManagerInternal.showChargingAnimation(batteryLevel);
        }
        this.mSuspendBlocker.release();
    }

    /* access modifiers changed from: private */
    public void showWiredChargingStarted(int userId) {
        playChargingStartedFeedback(userId);
        this.mSuspendBlocker.release();
    }

    /* access modifiers changed from: private */
    public void lockProfile(int userId) {
        this.mTrustManager.setDeviceLockedForUser(userId, true);
    }

    private void playChargingStartedVibration(int userId) {
        boolean vibrateEnabled = true;
        if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "charging_vibration_enabled", 1, userId) == 0) {
            vibrateEnabled = false;
        }
        if (vibrateEnabled && isChargingFeedbackEnabled(userId)) {
            this.mVibrator.vibrate(WIRELESS_CHARGING_VIBRATION_EFFECT, VIBRATION_ATTRIBUTES);
        }
    }

    private boolean isChargingFeedbackEnabled(int userId) {
        boolean enabled = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "charging_sounds_enabled", 1, userId) != 0;
        boolean dndOff = Settings.Global.getInt(this.mContext.getContentResolver(), "zen_mode", 1) == 0;
        if (!enabled || !dndOff) {
            return false;
        }
        return true;
    }

    public void onWakefulnessInHangUp(boolean hangUp) {
        if (this.mBeingHangUp != hangUp) {
            this.mBeingHangUp = hangUp;
            StringBuilder sb = new StringBuilder();
            sb.append("onWakefulnessInHangUp: ");
            sb.append(this.mBeingHangUp ? "disable " : "enable ");
            sb.append("input event.");
            Slog.d(TAG, sb.toString());
            this.mInputManagerInternal.setInteractive(!this.mBeingHangUp && this.mInteractive);
            this.mPolicy.setHangUpEnable(hangUp);
            this.mHandler.post(new Runnable() {
                public void run() {
                    Notifier.this.updateRotationOffState();
                    Notifier.this.sendHangupBroadcast();
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void sendHangupBroadcast() {
        Intent intent = new Intent("miui.intent.action.HANG_UP_CHANGED");
        intent.addFlags(1342177280);
        intent.putExtra("hang_up_enable", this.mBeingHangUp);
        this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL, Manifest.permission.HANG_UP_CHANGED, (Bundle) null);
    }

    /* access modifiers changed from: private */
    public void updateRotationOffState() {
        try {
            IWindowManager wm = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
            boolean accelerometer = Settings.System.getIntForUser(this.mContext.getContentResolver(), "accelerometer_rotation", 0, -2) == 1;
            if (this.mBeingHangUp && accelerometer) {
                this.mDisableRotationDueToHangUp = true;
                wm.freezeRotation(-1);
                Slog.d(TAG, "updateRotationOffState: disable the accelerometer");
            } else if (!this.mBeingHangUp && this.mDisableRotationDueToHangUp && !accelerometer) {
                Slog.d(TAG, "updateRotationOffState: enable the accelerometer");
                this.mDisableRotationDueToHangUp = false;
                wm.thawRotation();
            }
        } catch (RemoteException e) {
        }
    }

    private final class NotifierHandler extends Handler {
        public NotifierHandler(Looper looper) {
            super(looper, (Handler.Callback) null, true);
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Notifier.this.sendUserActivity();
                    return;
                case 2:
                    Notifier.this.sendNextBroadcast();
                    return;
                case 3:
                    Notifier.this.showWirelessChargingStarted(msg.arg1, msg.arg2);
                    return;
                case 4:
                    Notifier.this.sendBrightnessBoostChangedBroadcast();
                    return;
                case 5:
                    Notifier.this.lockProfile(msg.arg1);
                    return;
                case 6:
                    Notifier.this.showWiredChargingStarted(msg.arg1);
                    return;
                default:
                    return;
            }
        }
    }
}
