package com.android.server.am;

import android.app.ActivityManager;
import android.bluetooth.BluetoothActivityEnergyInfo;
import android.content.Context;
import android.net.wifi.WifiActivityEnergyInfo;
import android.os.BatteryStats;
import android.os.BatteryStatsInternal;
import android.os.Binder;
import android.os.Handler;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.PowerManagerInternal;
import android.os.PowerSaveState;
import android.os.Process;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UserManagerInternal;
import android.os.WorkSource;
import android.os.connectivity.CellularBatteryStats;
import android.os.connectivity.GpsBatteryStats;
import android.os.connectivity.WifiBatteryStats;
import android.os.health.HealthStatsParceler;
import android.os.health.HealthStatsWriter;
import android.os.health.UidHealthStats;
import android.provider.Settings;
import android.telephony.ModemActivityInfo;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Slog;
import android.util.StatsLog;
import com.android.internal.app.IBatteryStats;
import com.android.internal.os.BatteryStatsImpl;
import com.android.internal.os.PowerProfile;
import com.android.internal.os.RailStats;
import com.android.internal.os.RpmStats;
import com.android.server.LocalServices;
import com.android.server.UiModeManagerService;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import miui.telephony.SubscriptionManager;

public final class BatteryStatsService extends IBatteryStats.Stub implements PowerManagerInternal.LowPowerModeListener, BatteryStatsImpl.PlatformIdleStateCallback, BatteryStatsImpl.RailEnergyDataCallback {
    static final boolean DBG = false;
    private static final int MAX_LOW_POWER_STATS_SIZE = 2048;
    static final String TAG = "BatteryStatsService";
    private static IBatteryStats sService;
    private final Context mContext;
    private CharsetDecoder mDecoderStat = StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE).replaceWith("?");
    final BatteryStatsImpl mStats;
    private final BatteryStatsImpl.UserInfoProvider mUserManagerUserInfoProvider;
    private CharBuffer mUtf16BufferStat = CharBuffer.allocate(2048);
    private ByteBuffer mUtf8BufferStat = ByteBuffer.allocateDirect(2048);
    private final BatteryExternalStatsWorker mWorker;

    private native void getLowPowerStats(RpmStats rpmStats);

    private native int getPlatformLowPowerStats(ByteBuffer byteBuffer);

    private native void getRailEnergyPowerStats(RailStats railStats);

    private native int getSubsystemLowPowerStats(ByteBuffer byteBuffer);

    /* access modifiers changed from: private */
    public static native int nativeWaitWakeup(ByteBuffer byteBuffer);

    public void fillLowPowerStats(RpmStats rpmStats) {
        getLowPowerStats(rpmStats);
    }

    public void fillRailDataStats(RailStats railStats) {
        getRailEnergyPowerStats(railStats);
    }

    public String getPlatformLowPowerStats() {
        this.mUtf8BufferStat.clear();
        this.mUtf16BufferStat.clear();
        this.mDecoderStat.reset();
        int bytesWritten = getPlatformLowPowerStats(this.mUtf8BufferStat);
        if (bytesWritten < 0) {
            return null;
        }
        if (bytesWritten == 0) {
            return "Empty";
        }
        this.mUtf8BufferStat.limit(bytesWritten);
        this.mDecoderStat.decode(this.mUtf8BufferStat, this.mUtf16BufferStat, true);
        this.mUtf16BufferStat.flip();
        return this.mUtf16BufferStat.toString();
    }

    public String getSubsystemLowPowerStats() {
        this.mUtf8BufferStat.clear();
        this.mUtf16BufferStat.clear();
        this.mDecoderStat.reset();
        int bytesWritten = getSubsystemLowPowerStats(this.mUtf8BufferStat);
        if (bytesWritten < 0) {
            return null;
        }
        if (bytesWritten == 0) {
            return "Empty";
        }
        this.mUtf8BufferStat.limit(bytesWritten);
        this.mDecoderStat.decode(this.mUtf8BufferStat, this.mUtf16BufferStat, true);
        this.mUtf16BufferStat.flip();
        return this.mUtf16BufferStat.toString();
    }

    BatteryStatsService(Context context, File systemDir, Handler handler) {
        this.mContext = context;
        this.mUserManagerUserInfoProvider = new BatteryStatsImpl.UserInfoProvider() {
            private UserManagerInternal umi;

            public int[] getUserIds() {
                if (this.umi == null) {
                    this.umi = (UserManagerInternal) LocalServices.getService(UserManagerInternal.class);
                }
                UserManagerInternal userManagerInternal = this.umi;
                if (userManagerInternal != null) {
                    return userManagerInternal.getUserIds();
                }
                return null;
            }
        };
        this.mStats = new BatteryStatsImpl(systemDir, handler, this, this, this.mUserManagerUserInfoProvider);
        this.mWorker = new BatteryExternalStatsWorker(context, this.mStats);
        this.mStats.setExternalStatsSyncLocked(this.mWorker);
        this.mStats.setRadioScanningTimeoutLocked(((long) this.mContext.getResources().getInteger(17694878)) * 1000);
        this.mStats.setPowerProfileLocked(new PowerProfile(context));
    }

    public void publish() {
        LocalServices.addService(BatteryStatsInternal.class, new LocalService());
        ServiceManager.addService("batterystats", asBinder());
    }

    public void systemServicesReady() {
        this.mStats.systemServicesReady(this.mContext);
    }

    private final class LocalService extends BatteryStatsInternal {
        private LocalService() {
        }

        public String[] getWifiIfaces() {
            return (String[]) BatteryStatsService.this.mStats.getWifiIfaces().clone();
        }

        public String[] getMobileIfaces() {
            return (String[]) BatteryStatsService.this.mStats.getMobileIfaces().clone();
        }

        public void noteJobsDeferred(int uid, int numDeferred, long sinceLast) {
            BatteryStatsService.this.noteJobsDeferred(uid, numDeferred, sinceLast);
        }
    }

    private static void awaitUninterruptibly(Future<?> future) {
        while (true) {
            try {
                future.get();
                return;
            } catch (ExecutionException e) {
                return;
            } catch (InterruptedException e2) {
            }
        }
    }

    private void syncStats(String reason, int flags) {
        awaitUninterruptibly(this.mWorker.scheduleSync(reason, flags));
    }

    public void initPowerManagement() {
        PowerManagerInternal powerMgr = (PowerManagerInternal) LocalServices.getService(PowerManagerInternal.class);
        powerMgr.registerLowPowerModeObserver(this);
        synchronized (this.mStats) {
            this.mStats.notePowerSaveModeLocked(powerMgr.getLowPowerState(9).batterySaverEnabled);
        }
        new WakeupReasonThread().start();
    }

    public void shutdown() {
        Slog.w("BatteryStats", "Writing battery stats before shutdown...");
        syncStats("shutdown", 31);
        synchronized (this.mStats) {
            this.mStats.shutdownLocked();
        }
        this.mWorker.shutdown();
    }

    public static IBatteryStats getService() {
        IBatteryStats iBatteryStats = sService;
        if (iBatteryStats != null) {
            return iBatteryStats;
        }
        sService = asInterface(ServiceManager.getService("batterystats"));
        return sService;
    }

    public int getServiceType() {
        return 9;
    }

    public void onLowPowerModeChanged(PowerSaveState result) {
        synchronized (this.mStats) {
            this.mStats.notePowerSaveModeLocked(result.batterySaverEnabled);
        }
    }

    public BatteryStatsImpl getActiveStatistics() {
        return this.mStats;
    }

    public void scheduleWriteToDisk() {
        this.mWorker.scheduleWrite();
    }

    /* access modifiers changed from: package-private */
    public void removeUid(int uid) {
        synchronized (this.mStats) {
            this.mStats.removeUidStatsLocked(uid);
        }
    }

    /* access modifiers changed from: package-private */
    public void onCleanupUser(int userId) {
        synchronized (this.mStats) {
            this.mStats.onCleanupUserLocked(userId);
        }
    }

    /* access modifiers changed from: package-private */
    public void onUserRemoved(int userId) {
        synchronized (this.mStats) {
            this.mStats.onUserRemovedLocked(userId);
        }
    }

    /* access modifiers changed from: package-private */
    public void addIsolatedUid(int isolatedUid, int appUid) {
        synchronized (this.mStats) {
            this.mStats.addIsolatedUidLocked(isolatedUid, appUid);
        }
    }

    /* access modifiers changed from: package-private */
    public void removeIsolatedUid(int isolatedUid, int appUid) {
        synchronized (this.mStats) {
            this.mStats.scheduleRemoveIsolatedUidLocked(isolatedUid, appUid);
        }
    }

    /* access modifiers changed from: package-private */
    public void noteProcessStart(String name, int uid) {
        synchronized (this.mStats) {
            this.mStats.noteProcessStartLocked(name, uid);
            StatsLog.write(28, uid, name, 1);
        }
    }

    /* access modifiers changed from: package-private */
    public void noteProcessCrash(String name, int uid) {
        synchronized (this.mStats) {
            this.mStats.noteProcessCrashLocked(name, uid);
            StatsLog.write(28, uid, name, 2);
        }
    }

    /* access modifiers changed from: package-private */
    public void noteProcessAnr(String name, int uid) {
        synchronized (this.mStats) {
            this.mStats.noteProcessAnrLocked(name, uid);
        }
    }

    /* access modifiers changed from: package-private */
    public void noteProcessFinish(String name, int uid) {
        synchronized (this.mStats) {
            this.mStats.noteProcessFinishLocked(name, uid);
            StatsLog.write(28, uid, name, 0);
        }
    }

    /* access modifiers changed from: package-private */
    public void noteUidProcessState(int uid, int state) {
        synchronized (this.mStats) {
            StatsLog.write(27, uid, ActivityManager.processStateAmToProto(state));
            this.mStats.noteUidProcessStateLocked(uid, state);
        }
    }

    public byte[] getStatistics() {
        this.mContext.enforceCallingPermission("android.permission.BATTERY_STATS", (String) null);
        Parcel out = Parcel.obtain();
        syncStats("get-stats", 31);
        synchronized (this.mStats) {
            this.mStats.writeToParcel(out, 0);
        }
        byte[] data = out.marshall();
        out.recycle();
        return data;
    }

    public ParcelFileDescriptor getStatisticsStream() {
        this.mContext.enforceCallingPermission("android.permission.BATTERY_STATS", (String) null);
        Parcel out = Parcel.obtain();
        syncStats("get-stats", 31);
        synchronized (this.mStats) {
            this.mStats.writeToParcel(out, 0);
        }
        byte[] data = out.marshall();
        out.recycle();
        try {
            return ParcelFileDescriptor.fromData(data, "battery-stats");
        } catch (IOException e) {
            Slog.w(TAG, "Unable to create shared memory", e);
            return null;
        }
    }

    public boolean isCharging() {
        boolean isCharging;
        synchronized (this.mStats) {
            isCharging = this.mStats.isCharging();
        }
        return isCharging;
    }

    public long computeBatteryTimeRemaining() {
        long j;
        synchronized (this.mStats) {
            long time = this.mStats.computeBatteryTimeRemaining(SystemClock.elapsedRealtime());
            j = time >= 0 ? time / 1000 : time;
        }
        return j;
    }

    public long computeChargeTimeRemaining() {
        long j;
        synchronized (this.mStats) {
            long time = this.mStats.computeChargeTimeRemaining(SystemClock.elapsedRealtime());
            j = time >= 0 ? time / 1000 : time;
        }
        return j;
    }

    public void noteEvent(int code, String name, int uid) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteEventLocked(code, name, uid);
        }
    }

    public void noteSyncStart(String name, int uid) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteSyncStartLocked(name, uid);
            StatsLog.write_non_chained(7, uid, (String) null, name, 1);
        }
    }

    public void noteSyncFinish(String name, int uid) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteSyncFinishLocked(name, uid);
            StatsLog.write_non_chained(7, uid, (String) null, name, 0);
        }
    }

    public void noteJobStart(String name, int uid, int standbyBucket, int jobid) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteJobStartLocked(name, uid);
            StatsLog.write_non_chained(8, uid, (String) null, name, 1, -1, standbyBucket, jobid);
        }
    }

    public void noteJobFinish(String name, int uid, int stopReason, int standbyBucket, int jobid) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteJobFinishLocked(name, uid, stopReason);
            StatsLog.write_non_chained(8, uid, (String) null, name, 0, stopReason, standbyBucket, jobid);
        }
    }

    /* access modifiers changed from: package-private */
    public void noteJobsDeferred(int uid, int numDeferred, long sinceLast) {
        synchronized (this.mStats) {
            this.mStats.noteJobsDeferredLocked(uid, numDeferred, sinceLast);
        }
    }

    public void noteWakupAlarm(String name, int uid, WorkSource workSource, String tag) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteWakupAlarmLocked(name, uid, workSource, tag);
        }
    }

    public void noteAlarmStart(String name, WorkSource workSource, int uid) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteAlarmStartLocked(name, workSource, uid);
        }
    }

    public void noteAlarmFinish(String name, WorkSource workSource, int uid) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteAlarmFinishLocked(name, workSource, uid);
        }
    }

    public void noteStartWakelock(int uid, int pid, String name, String historyName, int type, boolean unimportantForLogging) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteStartWakeLocked(uid, pid, (WorkSource.WorkChain) null, name, historyName, type, unimportantForLogging, SystemClock.elapsedRealtime(), SystemClock.uptimeMillis());
        }
    }

    public void noteStopWakelock(int uid, int pid, String name, String historyName, int type) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteStopWakeLocked(uid, pid, (WorkSource.WorkChain) null, name, historyName, type, SystemClock.elapsedRealtime(), SystemClock.uptimeMillis());
        }
    }

    public void noteStartWakelockFromSource(WorkSource ws, int pid, String name, String historyName, int type, boolean unimportantForLogging) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteStartWakeFromSourceLocked(ws, pid, name, historyName, type, unimportantForLogging);
        }
    }

    public void noteChangeWakelockFromSource(WorkSource ws, int pid, String name, String historyName, int type, WorkSource newWs, int newPid, String newName, String newHistoryName, int newType, boolean newUnimportantForLogging) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteChangeWakelockFromSourceLocked(ws, pid, name, historyName, type, newWs, newPid, newName, newHistoryName, newType, newUnimportantForLogging);
        }
    }

    public void noteStopWakelockFromSource(WorkSource ws, int pid, String name, String historyName, int type) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteStopWakeFromSourceLocked(ws, pid, name, historyName, type);
        }
    }

    public void noteLongPartialWakelockStart(String name, String historyName, int uid) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteLongPartialWakelockStart(name, historyName, uid);
        }
    }

    public void noteLongPartialWakelockStartFromSource(String name, String historyName, WorkSource workSource) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteLongPartialWakelockStartFromSource(name, historyName, workSource);
        }
    }

    public void noteLongPartialWakelockFinish(String name, String historyName, int uid) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteLongPartialWakelockFinish(name, historyName, uid);
        }
    }

    public void noteLongPartialWakelockFinishFromSource(String name, String historyName, WorkSource workSource) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteLongPartialWakelockFinishFromSource(name, historyName, workSource);
        }
    }

    public void noteStartSensor(int uid, int sensor) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteStartSensorLocked(uid, sensor);
            StatsLog.write_non_chained(5, uid, (String) null, sensor, 1);
        }
    }

    public void noteStopSensor(int uid, int sensor) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteStopSensorLocked(uid, sensor);
            StatsLog.write_non_chained(5, uid, (String) null, sensor, 0);
        }
    }

    public void noteStartSensorWithPkg(int uid, int sensor, String packageName) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteStartSensorWithPkgLocked(uid, sensor, packageName);
            StatsLog.write_non_chained(5, uid, (String) null, sensor, 1);
        }
    }

    public void noteStopSensorWithPkg(int uid, int sensor, String packageName) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteStopSensorWithPkgLocked(uid, sensor, packageName);
            StatsLog.write_non_chained(5, uid, (String) null, sensor, 0);
        }
    }

    public void noteVibratorOn(int uid, long durationMillis) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteVibratorOnLocked(uid, durationMillis);
        }
    }

    public void noteVibratorOff(int uid) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteVibratorOffLocked(uid);
        }
    }

    public void noteGpsChanged(WorkSource oldWs, WorkSource newWs) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteGpsChangedLocked(oldWs, newWs);
        }
    }

    public void noteGpsSignalQuality(int signalLevel) {
        synchronized (this.mStats) {
            this.mStats.noteGpsSignalQualityLocked(signalLevel);
        }
    }

    public void noteScreenState(int state) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            StatsLog.write(29, state);
            this.mStats.noteScreenStateLocked(state);
        }
    }

    public void noteScreenBrightness(int brightness) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            StatsLog.write(9, brightness);
            this.mStats.noteScreenBrightnessLocked(brightness);
        }
    }

    public void noteUserActivity(int uid, int event) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteUserActivityLocked(uid, event);
        }
    }

    public void noteWakeUp(String reason, int reasonUid) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteWakeUpLocked(reason, reasonUid);
        }
    }

    public void noteInteractive(boolean interactive) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteInteractiveLocked(interactive);
        }
    }

    public void noteConnectivityChanged(int type, String extra) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteConnectivityChangedLocked(type, extra);
        }
    }

    public void noteMobileRadioPowerState(int powerState, long timestampNs, int uid) {
        boolean update;
        enforceCallingPermission();
        synchronized (this.mStats) {
            update = this.mStats.noteMobileRadioPowerStateLocked(powerState, timestampNs, uid);
        }
        if (update) {
            this.mWorker.scheduleSync("modem-data", 4);
        }
    }

    public void notePhoneOn() {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.notePhoneOnLocked();
        }
    }

    public void notePhoneOff() {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.notePhoneOffLocked();
        }
    }

    public void notePhoneSignalStrength(SignalStrength signalStrength) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.notePhoneSignalStrengthLocked(signalStrength);
        }
    }

    public void notePhoneDataConnectionState(int dataType, boolean hasData) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.notePhoneDataConnectionStateLocked(dataType, hasData);
        }
    }

    public void notePhoneState(int state) {
        enforceCallingPermission();
        int simState = TelephonyManager.getDefault().getSimState(SubscriptionManager.getDefault().getDefaultDataSlotId());
        synchronized (this.mStats) {
            this.mStats.notePhoneStateLocked(state, simState);
        }
    }

    public void noteWifiOn() {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteWifiOnLocked();
        }
        StatsLog.write(HdmiCecKeycode.CEC_KEYCODE_F1_BLUE, 1);
    }

    public void noteWifiOff() {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteWifiOffLocked();
        }
        StatsLog.write(HdmiCecKeycode.CEC_KEYCODE_F1_BLUE, 0);
    }

    public void noteStartAudio(int uid) {
        enforceSelfOrCallingPermission(uid);
        synchronized (this.mStats) {
            this.mStats.noteAudioOnLocked(uid);
            StatsLog.write_non_chained(23, uid, (String) null, 1);
        }
    }

    public void noteStopAudio(int uid) {
        enforceSelfOrCallingPermission(uid);
        synchronized (this.mStats) {
            this.mStats.noteAudioOffLocked(uid);
            StatsLog.write_non_chained(23, uid, (String) null, 0);
        }
    }

    public void noteStartVideo(int uid) {
        enforceSelfOrCallingPermission(uid);
        synchronized (this.mStats) {
            this.mStats.noteVideoOnLocked(uid);
            StatsLog.write_non_chained(24, uid, (String) null, 1);
        }
    }

    public void noteStopVideo(int uid) {
        enforceSelfOrCallingPermission(uid);
        synchronized (this.mStats) {
            this.mStats.noteVideoOffLocked(uid);
            StatsLog.write_non_chained(24, uid, (String) null, 0);
        }
    }

    public void noteResetAudio() {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteResetAudioLocked();
            StatsLog.write_non_chained(23, -1, (String) null, 2);
        }
    }

    public void noteResetVideo() {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteResetVideoLocked();
            StatsLog.write_non_chained(24, -1, (String) null, 2);
        }
    }

    public void noteFlashlightOn(int uid) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteFlashlightOnLocked(uid);
            StatsLog.write_non_chained(26, uid, (String) null, 1);
        }
    }

    public void noteFlashlightOff(int uid) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteFlashlightOffLocked(uid);
            StatsLog.write_non_chained(26, uid, (String) null, 0);
        }
    }

    public void noteStartCamera(int uid) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteCameraOnLocked(uid);
            StatsLog.write_non_chained(25, uid, (String) null, 1);
        }
    }

    public void noteStopCamera(int uid) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteCameraOffLocked(uid);
            StatsLog.write_non_chained(25, uid, (String) null, 0);
        }
    }

    public void noteResetCamera() {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteResetCameraLocked();
            StatsLog.write_non_chained(25, -1, (String) null, 2);
        }
    }

    public void noteResetFlashlight() {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteResetFlashlightLocked();
            StatsLog.write_non_chained(26, -1, (String) null, 2);
        }
    }

    public void noteWifiRadioPowerState(int powerState, long tsNanos, int uid) {
        String type;
        enforceCallingPermission();
        synchronized (this.mStats) {
            if (this.mStats.isOnBattery()) {
                if (powerState != 3) {
                    if (powerState != 2) {
                        type = "inactive";
                        BatteryExternalStatsWorker batteryExternalStatsWorker = this.mWorker;
                        batteryExternalStatsWorker.scheduleSync("wifi-data: " + type, 2);
                    }
                }
                type = "active";
                BatteryExternalStatsWorker batteryExternalStatsWorker2 = this.mWorker;
                batteryExternalStatsWorker2.scheduleSync("wifi-data: " + type, 2);
            }
            this.mStats.noteWifiRadioPowerState(powerState, tsNanos, uid);
        }
    }

    public void noteWifiRunning(WorkSource ws) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteWifiRunningLocked(ws);
        }
        StatsLog.write(HdmiCecKeycode.CEC_KEYCODE_F2_RED, ws, 1);
    }

    public void noteWifiRunningChanged(WorkSource oldWs, WorkSource newWs) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteWifiRunningChangedLocked(oldWs, newWs);
        }
        StatsLog.write(HdmiCecKeycode.CEC_KEYCODE_F2_RED, newWs, 1);
        StatsLog.write(HdmiCecKeycode.CEC_KEYCODE_F2_RED, oldWs, 0);
    }

    public void noteWifiStopped(WorkSource ws) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteWifiStoppedLocked(ws);
        }
        StatsLog.write(HdmiCecKeycode.CEC_KEYCODE_F2_RED, ws, 0);
    }

    public void noteWifiState(int wifiState, String accessPoint) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteWifiStateLocked(wifiState, accessPoint);
        }
    }

    public void noteWifiSupplicantStateChanged(int supplState, boolean failedAuth) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteWifiSupplicantStateChangedLocked(supplState, failedAuth);
        }
    }

    public void noteWifiRssiChanged(int newRssi) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteWifiRssiChangedLocked(newRssi);
        }
    }

    public void noteFullWifiLockAcquired(int uid) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteFullWifiLockAcquiredLocked(uid);
        }
    }

    public void noteFullWifiLockReleased(int uid) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteFullWifiLockReleasedLocked(uid);
        }
    }

    public void noteWifiScanStarted(int uid) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteWifiScanStartedLocked(uid);
        }
    }

    public void noteWifiScanStopped(int uid) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteWifiScanStoppedLocked(uid);
        }
    }

    public void noteWifiMulticastEnabled(int uid) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteWifiMulticastEnabledLocked(uid);
        }
    }

    public void noteWifiMulticastDisabled(int uid) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteWifiMulticastDisabledLocked(uid);
        }
    }

    public void noteFullWifiLockAcquiredFromSource(WorkSource ws) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteFullWifiLockAcquiredFromSourceLocked(ws);
        }
    }

    public void noteFullWifiLockReleasedFromSource(WorkSource ws) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteFullWifiLockReleasedFromSourceLocked(ws);
        }
    }

    public void noteWifiScanStartedFromSource(WorkSource ws) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteWifiScanStartedFromSourceLocked(ws);
        }
    }

    public void noteWifiScanStoppedFromSource(WorkSource ws) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteWifiScanStoppedFromSourceLocked(ws);
        }
    }

    public void noteWifiBatchedScanStartedFromSource(WorkSource ws, int csph) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteWifiBatchedScanStartedFromSourceLocked(ws, csph);
        }
    }

    public void noteWifiBatchedScanStoppedFromSource(WorkSource ws) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteWifiBatchedScanStoppedFromSourceLocked(ws);
        }
    }

    public void noteNetworkInterfaceType(String iface, int networkType) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteNetworkInterfaceTypeLocked(iface, networkType);
        }
    }

    public void noteNetworkStatsEnabled() {
        enforceCallingPermission();
        this.mWorker.scheduleSync("network-stats-enabled", 6);
    }

    public void noteDeviceIdleMode(int mode, String activeReason, int activeUid) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteDeviceIdleModeLocked(mode, activeReason, activeUid);
        }
    }

    public void notePackageInstalled(String pkgName, long versionCode) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.notePackageInstalledLocked(pkgName, versionCode);
        }
    }

    public void notePackageUninstalled(String pkgName) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.notePackageUninstalledLocked(pkgName);
        }
    }

    public void noteBleScanStarted(WorkSource ws, boolean isUnoptimized) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteBluetoothScanStartedFromSourceLocked(ws, isUnoptimized);
        }
    }

    public void noteBleScanStopped(WorkSource ws, boolean isUnoptimized) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteBluetoothScanStoppedFromSourceLocked(ws, isUnoptimized);
        }
    }

    public void noteResetBleScan() {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteResetBluetoothScanLocked();
        }
    }

    public void noteBleScanResults(WorkSource ws, int numNewResults) {
        enforceCallingPermission();
        synchronized (this.mStats) {
            this.mStats.noteBluetoothScanResultsFromSourceLocked(ws, numNewResults);
        }
    }

    public void noteWifiControllerActivity(WifiActivityEnergyInfo info) {
        enforceCallingPermission();
        if (info == null || !info.isValid()) {
            Slog.e(TAG, "invalid wifi data given: " + info);
            return;
        }
        this.mStats.updateWifiState(info);
    }

    public void noteBluetoothControllerActivity(BluetoothActivityEnergyInfo info) {
        enforceCallingPermission();
        if (info == null || !info.isValid()) {
            Slog.e(TAG, "invalid bluetooth data given: " + info);
            return;
        }
        synchronized (this.mStats) {
            this.mStats.updateBluetoothStateLocked(info);
        }
    }

    public void noteModemControllerActivity(ModemActivityInfo info) {
        enforceCallingPermission();
        if (info == null || !info.isValid()) {
            Slog.e(TAG, "invalid modem data given: " + info);
            return;
        }
        this.mStats.updateMobileRadioState(info);
    }

    public boolean isOnBattery() {
        return this.mStats.isOnBattery();
    }

    public void setBatteryState(int status, int health, int plugType, int level, int temp, int volt, int chargeUAh, int chargeFullUAh) {
        enforceCallingPermission();
        this.mWorker.scheduleRunnable(new Runnable(plugType, status, health, level, temp, volt, chargeUAh, chargeFullUAh) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ int f$3;
            private final /* synthetic */ int f$4;
            private final /* synthetic */ int f$5;
            private final /* synthetic */ int f$6;
            private final /* synthetic */ int f$7;
            private final /* synthetic */ int f$8;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
                this.f$7 = r8;
                this.f$8 = r9;
            }

            public final void run() {
                BatteryStatsService.this.lambda$setBatteryState$1$BatteryStatsService(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
            }
        });
    }

    public /* synthetic */ void lambda$setBatteryState$1$BatteryStatsService(int plugType, int status, int health, int level, int temp, int volt, int chargeUAh, int chargeFullUAh) {
        synchronized (this.mStats) {
            if (this.mStats.isOnBattery() == BatteryStatsImpl.isOnBattery(plugType, status)) {
                this.mStats.setBatteryStateLocked(status, health, plugType, level, temp, volt, chargeUAh, chargeFullUAh);
                return;
            }
            this.mWorker.scheduleSync("battery-state", 31);
            this.mWorker.scheduleRunnable(new Runnable(status, health, plugType, level, temp, volt, chargeUAh, chargeFullUAh) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ int f$2;
                private final /* synthetic */ int f$3;
                private final /* synthetic */ int f$4;
                private final /* synthetic */ int f$5;
                private final /* synthetic */ int f$6;
                private final /* synthetic */ int f$7;
                private final /* synthetic */ int f$8;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                    this.f$6 = r7;
                    this.f$7 = r8;
                    this.f$8 = r9;
                }

                public final void run() {
                    BatteryStatsService.this.lambda$setBatteryState$0$BatteryStatsService(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
                }
            });
        }
    }

    public /* synthetic */ void lambda$setBatteryState$0$BatteryStatsService(int status, int health, int plugType, int level, int temp, int volt, int chargeUAh, int chargeFullUAh) {
        synchronized (this.mStats) {
            this.mStats.setBatteryStateLocked(status, health, plugType, level, temp, volt, chargeUAh, chargeFullUAh);
        }
    }

    public long getAwakeTimeBattery() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BATTERY_STATS", (String) null);
        return this.mStats.getAwakeTimeBattery();
    }

    public long getAwakeTimePlugged() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BATTERY_STATS", (String) null);
        return this.mStats.getAwakeTimePlugged();
    }

    public void enforceCallingPermission() {
        if (Binder.getCallingPid() != Process.myPid()) {
            this.mContext.enforcePermission("android.permission.UPDATE_DEVICE_STATS", Binder.getCallingPid(), Binder.getCallingUid(), (String) null);
        }
    }

    private void enforceSelfOrCallingPermission(int uid) {
        if (Binder.getCallingUid() != uid) {
            enforceCallingPermission();
        }
    }

    final class WakeupReasonThread extends Thread {
        private static final int MAX_REASON_SIZE = 512;
        private CharsetDecoder mDecoder;
        private CharBuffer mUtf16Buffer;
        private ByteBuffer mUtf8Buffer;

        WakeupReasonThread() {
            super("BatteryStats_wakeupReason");
        }

        /* Debug info: failed to restart local var, previous not found, register: 3 */
        public void run() {
            Process.setThreadPriority(-2);
            this.mDecoder = StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE).replaceWith("?");
            this.mUtf8Buffer = ByteBuffer.allocateDirect(512);
            this.mUtf16Buffer = CharBuffer.allocate(512);
            while (true) {
                try {
                    String waitWakeup = waitWakeup();
                    String reason = waitWakeup;
                    if (waitWakeup != null) {
                        synchronized (BatteryStatsService.this.mStats) {
                            BatteryStatsService.this.mStats.noteWakeupReasonLocked(reason);
                        }
                    } else {
                        return;
                    }
                } catch (RuntimeException e) {
                    Slog.e(BatteryStatsService.TAG, "Failure reading wakeup reasons", e);
                    return;
                }
            }
        }

        private String waitWakeup() {
            this.mUtf8Buffer.clear();
            this.mUtf16Buffer.clear();
            this.mDecoder.reset();
            int bytesWritten = BatteryStatsService.nativeWaitWakeup(this.mUtf8Buffer);
            if (bytesWritten < 0) {
                return null;
            }
            if (bytesWritten == 0) {
                return UiModeManagerService.Shell.NIGHT_MODE_STR_UNKNOWN;
            }
            this.mUtf8Buffer.limit(bytesWritten);
            this.mDecoder.decode(this.mUtf8Buffer, this.mUtf16Buffer, true);
            this.mUtf16Buffer.flip();
            return this.mUtf16Buffer.toString();
        }
    }

    private void dumpHelp(PrintWriter pw) {
        pw.println("Battery stats (batterystats) dump options:");
        pw.println("  [--checkin] [--proto] [--history] [--history-start] [--charged] [-c]");
        pw.println("  [--daily] [--reset] [--write] [--new-daily] [--read-daily] [-h] [<package.name>]");
        pw.println("  --checkin: generate output for a checkin report; will write (and clear) the");
        pw.println("             last old completed stats when they had been reset.");
        pw.println("  -c: write the current stats in checkin format.");
        pw.println("  --proto: write the current aggregate stats (without history) in proto format.");
        pw.println("  --history: show only history data.");
        pw.println("  --history-start <num>: show only history data starting at given time offset.");
        pw.println("  --history-create-events <num>: create <num> of battery history events.");
        pw.println("  --charged: only output data since last charged.");
        pw.println("  --daily: only output full daily data.");
        pw.println("  --reset: reset the stats, clearing all current data.");
        pw.println("  --write: force write current collected stats to disk.");
        pw.println("  --new-daily: immediately create and write new daily stats record.");
        pw.println("  --read-daily: read-load last written daily stats.");
        pw.println("  --settings: dump the settings key/values related to batterystats");
        pw.println("  --cpu: dump cpu stats for debugging purpose");
        pw.println("  <package.name>: optional name of package to filter output by.");
        pw.println("  -h: print this help text.");
        pw.println("Battery stats (batterystats) commands:");
        pw.println("  enable|disable <option>");
        pw.println("    Enable or disable a running option.  Option state is not saved across boots.");
        pw.println("    Options are:");
        pw.println("      full-history: include additional detailed events in battery history:");
        pw.println("          wake_lock_in, alarms and proc events");
        pw.println("      no-auto-reset: don't automatically reset stats when unplugged");
        pw.println("      pretend-screen-off: pretend the screen is off, even if screen state changes");
    }

    private void dumpSettings(PrintWriter pw) {
        synchronized (this.mStats) {
            this.mStats.dumpConstantsLocked(pw);
        }
    }

    private void dumpCpuStats(PrintWriter pw) {
        synchronized (this.mStats) {
            this.mStats.dumpCpuStatsLocked(pw);
        }
    }

    private int doEnableOrDisable(PrintWriter pw, int i, String[] args, boolean enable) {
        int i2 = i + 1;
        if (i2 >= args.length) {
            StringBuilder sb = new StringBuilder();
            sb.append("Missing option argument for ");
            sb.append(enable ? "--enable" : "--disable");
            pw.println(sb.toString());
            dumpHelp(pw);
            return -1;
        }
        if ("full-wake-history".equals(args[i2]) || "full-history".equals(args[i2])) {
            synchronized (this.mStats) {
                this.mStats.setRecordAllHistoryLocked(enable);
            }
        } else if ("no-auto-reset".equals(args[i2])) {
            synchronized (this.mStats) {
                this.mStats.setNoAutoReset(enable);
            }
        } else if ("pretend-screen-off".equals(args[i2])) {
            synchronized (this.mStats) {
                this.mStats.setPretendScreenOff(enable);
            }
        } else {
            pw.println("Unknown enable/disable option: " + args[i2]);
            dumpHelp(pw);
            return -1;
        }
        return i2;
    }

    /*  JADX ERROR: IndexOutOfBoundsException in pass: RegionMakerVisitor
        java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        	at java.util.ArrayList.rangeCheck(ArrayList.java:657)
        	at java.util.ArrayList.get(ArrayList.java:433)
        	at jadx.core.dex.nodes.InsnNode.getArg(InsnNode.java:101)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:611)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processHandlersOutBlocks(RegionMaker.java:1008)
        	at jadx.core.dex.visitors.regions.RegionMaker.processTryCatchBlocks(RegionMaker.java:978)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:52)
        */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x03f4 A[SYNTHETIC, Splitter:B:224:0x03f4] */
    protected void dump(java.io.FileDescriptor r33, java.io.PrintWriter r34, java.lang.String[] r35) {
        /*
            r32 = this;
            r1 = r32
            r9 = r34
            r10 = r35
            android.content.Context r0 = r1.mContext
            java.lang.String r2 = "BatteryStatsService"
            boolean r0 = com.android.internal.util.DumpUtils.checkDumpAndUsageStatsPermission(r0, r2, r9)
            if (r0 != 0) goto L_0x0011
            return
        L_0x0011:
            r0 = 0
            r2 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = -1
            r11 = -1
            if (r10 == 0) goto L_0x0269
            r14 = 0
            r15 = r11
            r11 = r6
            r6 = r5
            r5 = r4
            r4 = r3
            r3 = r2
            r2 = r0
        L_0x0024:
            int r0 = r10.length
            if (r14 >= r0) goto L_0x025f
            r13 = r10[r14]
            java.lang.String r0 = "--checkin"
            boolean r0 = r0.equals(r13)
            if (r0 == 0) goto L_0x0035
            r3 = 1
            r5 = 1
            goto L_0x0204
        L_0x0035:
            java.lang.String r0 = "--history"
            boolean r0 = r0.equals(r13)
            if (r0 == 0) goto L_0x0041
            r2 = r2 | 8
            goto L_0x0204
        L_0x0041:
            java.lang.String r0 = "--history-start"
            boolean r0 = r0.equals(r13)
            r18 = r13
            r12 = 0
            if (r0 == 0) goto L_0x0066
            r2 = r2 | 8
            int r14 = r14 + 1
            int r0 = r10.length
            if (r14 < r0) goto L_0x005d
            java.lang.String r0 = "Missing time argument for --history-since"
            r9.println(r0)
            r1.dumpHelp(r9)
            return
        L_0x005d:
            r0 = r10[r14]
            long r7 = com.android.internal.util.ParseUtils.parseLong(r0, r12)
            r11 = 1
            goto L_0x0204
        L_0x0066:
            java.lang.String r0 = "--history-create-events"
            r12 = r18
            boolean r0 = r0.equals(r12)
            if (r0 == 0) goto L_0x00a3
            int r13 = r14 + 1
            int r0 = r10.length
            if (r13 < r0) goto L_0x007e
            java.lang.String r0 = "Missing events argument for --history-create-events"
            r9.println(r0)
            r1.dumpHelp(r9)
            return
        L_0x007e:
            r0 = r10[r13]
            r22 = r3
            r18 = r4
            r3 = 0
            long r3 = com.android.internal.util.ParseUtils.parseLong(r0, r3)
            com.android.internal.os.BatteryStatsImpl r14 = r1.mStats
            monitor-enter(r14)
            com.android.internal.os.BatteryStatsImpl r0 = r1.mStats     // Catch:{ all -> 0x00a0 }
            r0.createFakeHistoryEvents(r3)     // Catch:{ all -> 0x00a0 }
            java.lang.String r0 = "Battery history create events started."
            r9.println(r0)     // Catch:{ all -> 0x00a0 }
            r6 = 1
            monitor-exit(r14)     // Catch:{ all -> 0x00a0 }
            r14 = r13
            r4 = r18
            r3 = r22
            goto L_0x0204
        L_0x00a0:
            r0 = move-exception
            monitor-exit(r14)     // Catch:{ all -> 0x00a0 }
            throw r0
        L_0x00a3:
            r22 = r3
            r18 = r4
            java.lang.String r0 = "-c"
            boolean r0 = r0.equals(r12)
            if (r0 == 0) goto L_0x00b6
            r3 = 1
            r2 = r2 | 16
            r4 = r18
            goto L_0x0204
        L_0x00b6:
            java.lang.String r0 = "--proto"
            boolean r0 = r0.equals(r12)
            if (r0 == 0) goto L_0x00c3
            r4 = 1
            r3 = r22
            goto L_0x0204
        L_0x00c3:
            java.lang.String r0 = "--charged"
            boolean r0 = r0.equals(r12)
            if (r0 == 0) goto L_0x00d3
            r2 = r2 | 2
            r4 = r18
            r3 = r22
            goto L_0x0204
        L_0x00d3:
            java.lang.String r0 = "--daily"
            boolean r0 = r0.equals(r12)
            if (r0 == 0) goto L_0x00e3
            r2 = r2 | 4
            r4 = r18
            r3 = r22
            goto L_0x0204
        L_0x00e3:
            java.lang.String r0 = "--reset"
            boolean r0 = r0.equals(r12)
            if (r0 == 0) goto L_0x010c
            com.android.internal.os.BatteryStatsImpl r3 = r1.mStats
            monitor-enter(r3)
            com.android.internal.os.BatteryStatsImpl r0 = r1.mStats     // Catch:{ all -> 0x0109 }
            r0.resetAllStatsCmdLocked()     // Catch:{ all -> 0x0109 }
            java.lang.String r0 = "Battery stats reset."
            r9.println(r0)     // Catch:{ all -> 0x0109 }
            r6 = 1
            monitor-exit(r3)     // Catch:{ all -> 0x0109 }
            com.android.server.am.BatteryExternalStatsWorker r0 = r1.mWorker
            java.lang.String r3 = "dump"
            r4 = 31
            r0.scheduleSync(r3, r4)
            r4 = r18
            r3 = r22
            goto L_0x0204
        L_0x0109:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x0109 }
            throw r0
        L_0x010c:
            java.lang.String r0 = "--write"
            boolean r0 = r0.equals(r12)
            if (r0 == 0) goto L_0x0133
            java.lang.String r0 = "dump"
            r3 = 31
            r1.syncStats(r0, r3)
            com.android.internal.os.BatteryStatsImpl r3 = r1.mStats
            monitor-enter(r3)
            com.android.internal.os.BatteryStatsImpl r0 = r1.mStats     // Catch:{ all -> 0x0130 }
            r0.writeSyncLocked()     // Catch:{ all -> 0x0130 }
            java.lang.String r0 = "Battery stats written."
            r9.println(r0)     // Catch:{ all -> 0x0130 }
            r6 = 1
            monitor-exit(r3)     // Catch:{ all -> 0x0130 }
            r4 = r18
            r3 = r22
            goto L_0x0204
        L_0x0130:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x0130 }
            throw r0
        L_0x0133:
            java.lang.String r0 = "--new-daily"
            boolean r0 = r0.equals(r12)
            if (r0 == 0) goto L_0x0153
            com.android.internal.os.BatteryStatsImpl r3 = r1.mStats
            monitor-enter(r3)
            com.android.internal.os.BatteryStatsImpl r0 = r1.mStats     // Catch:{ all -> 0x0150 }
            r0.recordDailyStatsLocked()     // Catch:{ all -> 0x0150 }
            java.lang.String r0 = "New daily stats written."
            r9.println(r0)     // Catch:{ all -> 0x0150 }
            r6 = 1
            monitor-exit(r3)     // Catch:{ all -> 0x0150 }
            r4 = r18
            r3 = r22
            goto L_0x0204
        L_0x0150:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x0150 }
            throw r0
        L_0x0153:
            java.lang.String r0 = "--read-daily"
            boolean r0 = r0.equals(r12)
            if (r0 == 0) goto L_0x0173
            com.android.internal.os.BatteryStatsImpl r3 = r1.mStats
            monitor-enter(r3)
            com.android.internal.os.BatteryStatsImpl r0 = r1.mStats     // Catch:{ all -> 0x0170 }
            r0.readDailyStatsLocked()     // Catch:{ all -> 0x0170 }
            java.lang.String r0 = "Last daily stats read."
            r9.println(r0)     // Catch:{ all -> 0x0170 }
            r6 = 1
            monitor-exit(r3)     // Catch:{ all -> 0x0170 }
            r4 = r18
            r3 = r22
            goto L_0x0204
        L_0x0170:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x0170 }
            throw r0
        L_0x0173:
            java.lang.String r0 = "--enable"
            boolean r0 = r0.equals(r12)
            if (r0 != 0) goto L_0x0240
            java.lang.String r0 = "enable"
            boolean r0 = r0.equals(r12)
            if (r0 == 0) goto L_0x0185
            goto L_0x0240
        L_0x0185:
            java.lang.String r0 = "--disable"
            boolean r0 = r0.equals(r12)
            if (r0 != 0) goto L_0x0221
            java.lang.String r0 = "disable"
            boolean r0 = r0.equals(r12)
            if (r0 == 0) goto L_0x0197
            goto L_0x0221
        L_0x0197:
            java.lang.String r0 = "-h"
            boolean r0 = r0.equals(r12)
            if (r0 == 0) goto L_0x01a3
            r1.dumpHelp(r9)
            return
        L_0x01a3:
            java.lang.String r0 = "--settings"
            boolean r0 = r0.equals(r12)
            if (r0 == 0) goto L_0x01af
            r1.dumpSettings(r9)
            return
        L_0x01af:
            java.lang.String r0 = "--cpu"
            boolean r0 = r0.equals(r12)
            if (r0 == 0) goto L_0x01bb
            r1.dumpCpuStats(r9)
            return
        L_0x01bb:
            java.lang.String r0 = "-a"
            boolean r0 = r0.equals(r12)
            if (r0 == 0) goto L_0x01ca
            r2 = r2 | 32
            r4 = r18
            r3 = r22
            goto L_0x0204
        L_0x01ca:
            int r0 = r12.length()
            if (r0 <= 0) goto L_0x01f1
            r0 = 0
            char r3 = r12.charAt(r0)
            r0 = 45
            if (r3 != r0) goto L_0x01f1
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r3 = "Unknown option: "
            r0.append(r3)
            r0.append(r12)
            java.lang.String r0 = r0.toString()
            r9.println(r0)
            r1.dumpHelp(r9)
            return
        L_0x01f1:
            android.content.Context r0 = r1.mContext     // Catch:{ NameNotFoundException -> 0x0208 }
            android.content.pm.PackageManager r0 = r0.getPackageManager()     // Catch:{ NameNotFoundException -> 0x0208 }
            int r3 = android.os.UserHandle.getCallingUserId()     // Catch:{ NameNotFoundException -> 0x0208 }
            int r0 = r0.getPackageUidAsUser(r12, r3)     // Catch:{ NameNotFoundException -> 0x0208 }
            r15 = r0
            r4 = r18
            r3 = r22
        L_0x0204:
            r0 = 1
            int r14 = r14 + r0
            goto L_0x0024
        L_0x0208:
            r0 = move-exception
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Unknown package: "
            r3.append(r4)
            r3.append(r12)
            java.lang.String r3 = r3.toString()
            r9.println(r3)
            r1.dumpHelp(r9)
            return
        L_0x0221:
            r0 = 0
            int r0 = r1.doEnableOrDisable(r9, r14, r10, r0)
            if (r0 >= 0) goto L_0x0229
            return
        L_0x0229:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Disabled: "
            r3.append(r4)
            r4 = r10[r0]
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r9.println(r3)
            return
        L_0x0240:
            r0 = 1
            int r0 = r1.doEnableOrDisable(r9, r14, r10, r0)
            if (r0 >= 0) goto L_0x0248
            return
        L_0x0248:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Enabled: "
            r3.append(r4)
            r4 = r10[r0]
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r9.println(r3)
            return
        L_0x025f:
            r22 = r3
            r18 = r4
            r12 = r5
            r19 = r7
            r13 = r11
            r11 = r6
            goto L_0x0274
        L_0x0269:
            r22 = r2
            r18 = r3
            r12 = r4
            r13 = r6
            r19 = r7
            r15 = r11
            r2 = r0
            r11 = r5
        L_0x0274:
            if (r11 == 0) goto L_0x0277
            return
        L_0x0277:
            long r30 = android.os.Binder.clearCallingIdentity()
            android.content.Context r0 = r1.mContext     // Catch:{ all -> 0x0436 }
            boolean r0 = com.android.internal.os.BatteryStatsHelper.checkWifiOnly(r0)     // Catch:{ all -> 0x0436 }
            if (r0 == 0) goto L_0x0285
            r2 = r2 | 64
        L_0x0285:
            java.lang.String r0 = "dump"
            r3 = 31
            r1.syncStats(r0, r3)     // Catch:{ all -> 0x0436 }
            android.os.Binder.restoreCallingIdentity(r30)
            if (r15 < 0) goto L_0x029c
            r0 = r2 & 10
            if (r0 != 0) goto L_0x029c
            r0 = r2 | 2
            r0 = r0 & -17
            r14 = r0
            goto L_0x029d
        L_0x029c:
            r14 = r2
        L_0x029d:
            r0 = 4325376(0x420000, float:6.061143E-39)
            if (r18 == 0) goto L_0x0351
            android.content.Context r2 = r1.mContext
            android.content.pm.PackageManager r2 = r2.getPackageManager()
            java.util.List r2 = r2.getInstalledApplications(r0)
            if (r12 == 0) goto L_0x032e
            com.android.internal.os.BatteryStatsImpl r0 = r1.mStats
            com.android.internal.os.AtomicFile r3 = r0.mCheckinFile
            monitor-enter(r3)
            com.android.internal.os.BatteryStatsImpl r0 = r1.mStats     // Catch:{ all -> 0x032b }
            com.android.internal.os.AtomicFile r0 = r0.mCheckinFile     // Catch:{ all -> 0x032b }
            boolean r0 = r0.exists()     // Catch:{ all -> 0x032b }
            if (r0 == 0) goto L_0x0329
            com.android.internal.os.BatteryStatsImpl r0 = r1.mStats     // Catch:{ ParcelFormatException | IOException -> 0x030a }
            com.android.internal.os.AtomicFile r0 = r0.mCheckinFile     // Catch:{ ParcelFormatException | IOException -> 0x030a }
            byte[] r0 = r0.readFully()     // Catch:{ ParcelFormatException | IOException -> 0x030a }
            if (r0 == 0) goto L_0x0309
            android.os.Parcel r4 = android.os.Parcel.obtain()     // Catch:{ ParcelFormatException | IOException -> 0x030a }
            int r5 = r0.length     // Catch:{ ParcelFormatException | IOException -> 0x030a }
            r6 = 0
            r4.unmarshall(r0, r6, r5)     // Catch:{ ParcelFormatException | IOException -> 0x030a }
            r4.setDataPosition(r6)     // Catch:{ ParcelFormatException | IOException -> 0x030a }
            com.android.internal.os.BatteryStatsImpl r5 = new com.android.internal.os.BatteryStatsImpl     // Catch:{ ParcelFormatException | IOException -> 0x030a }
            r24 = 0
            com.android.internal.os.BatteryStatsImpl r6 = r1.mStats     // Catch:{ ParcelFormatException | IOException -> 0x030a }
            android.os.Handler r6 = r6.mHandler     // Catch:{ ParcelFormatException | IOException -> 0x030a }
            r26 = 0
            r27 = 0
            com.android.internal.os.BatteryStatsImpl$UserInfoProvider r7 = r1.mUserManagerUserInfoProvider     // Catch:{ ParcelFormatException | IOException -> 0x030a }
            r23 = r5
            r25 = r6
            r28 = r7
            r23.<init>(r24, r25, r26, r27, r28)     // Catch:{ ParcelFormatException | IOException -> 0x030a }
            r5.readSummaryFromParcel(r4)     // Catch:{ ParcelFormatException | IOException -> 0x030a }
            r4.recycle()     // Catch:{ ParcelFormatException | IOException -> 0x030a }
            android.content.Context r6 = r1.mContext     // Catch:{ ParcelFormatException | IOException -> 0x030a }
            r23 = r5
            r24 = r6
            r25 = r33
            r26 = r2
            r27 = r14
            r28 = r19
            r23.dumpProtoLocked(r24, r25, r26, r27, r28)     // Catch:{ ParcelFormatException | IOException -> 0x030a }
            com.android.internal.os.BatteryStatsImpl r6 = r1.mStats     // Catch:{ ParcelFormatException | IOException -> 0x030a }
            com.android.internal.os.AtomicFile r6 = r6.mCheckinFile     // Catch:{ ParcelFormatException | IOException -> 0x030a }
            r6.delete()     // Catch:{ ParcelFormatException | IOException -> 0x030a }
            monitor-exit(r3)     // Catch:{ all -> 0x032b }
            return
        L_0x0309:
            goto L_0x0329
        L_0x030a:
            r0 = move-exception
            java.lang.String r4 = "BatteryStatsService"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x032b }
            r5.<init>()     // Catch:{ all -> 0x032b }
            java.lang.String r6 = "Failure reading checkin file "
            r5.append(r6)     // Catch:{ all -> 0x032b }
            com.android.internal.os.BatteryStatsImpl r6 = r1.mStats     // Catch:{ all -> 0x032b }
            com.android.internal.os.AtomicFile r6 = r6.mCheckinFile     // Catch:{ all -> 0x032b }
            java.io.File r6 = r6.getBaseFile()     // Catch:{ all -> 0x032b }
            r5.append(r6)     // Catch:{ all -> 0x032b }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x032b }
            android.util.Slog.w(r4, r5, r0)     // Catch:{ all -> 0x032b }
        L_0x0329:
            monitor-exit(r3)     // Catch:{ all -> 0x032b }
            goto L_0x032e
        L_0x032b:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x032b }
            throw r0
        L_0x032e:
            com.android.internal.os.BatteryStatsImpl r3 = r1.mStats
            monitor-enter(r3)
            com.android.internal.os.BatteryStatsImpl r0 = r1.mStats     // Catch:{ all -> 0x034e }
            android.content.Context r4 = r1.mContext     // Catch:{ all -> 0x034e }
            r23 = r0
            r24 = r4
            r25 = r33
            r26 = r2
            r27 = r14
            r28 = r19
            r23.dumpProtoLocked(r24, r25, r26, r27, r28)     // Catch:{ all -> 0x034e }
            if (r13 == 0) goto L_0x034b
            com.android.internal.os.BatteryStatsImpl r0 = r1.mStats     // Catch:{ all -> 0x034e }
            r0.writeAsyncLocked()     // Catch:{ all -> 0x034e }
        L_0x034b:
            monitor-exit(r3)     // Catch:{ all -> 0x034e }
            goto L_0x042e
        L_0x034e:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x034e }
            throw r0
        L_0x0351:
            if (r22 == 0) goto L_0x0414
            android.content.Context r2 = r1.mContext
            android.content.pm.PackageManager r2 = r2.getPackageManager()
            java.util.List r17 = r2.getInstalledApplications(r0)
            if (r12 == 0) goto L_0x03f1
            com.android.internal.os.BatteryStatsImpl r0 = r1.mStats
            com.android.internal.os.AtomicFile r7 = r0.mCheckinFile
            monitor-enter(r7)
            com.android.internal.os.BatteryStatsImpl r0 = r1.mStats     // Catch:{ all -> 0x03ea }
            com.android.internal.os.AtomicFile r0 = r0.mCheckinFile     // Catch:{ all -> 0x03ea }
            boolean r0 = r0.exists()     // Catch:{ all -> 0x03ea }
            if (r0 == 0) goto L_0x03e6
            com.android.internal.os.BatteryStatsImpl r0 = r1.mStats     // Catch:{ ParcelFormatException | IOException -> 0x03c4 }
            com.android.internal.os.AtomicFile r0 = r0.mCheckinFile     // Catch:{ ParcelFormatException | IOException -> 0x03c4 }
            byte[] r0 = r0.readFully()     // Catch:{ ParcelFormatException | IOException -> 0x03c4 }
            if (r0 == 0) goto L_0x03c1
            android.os.Parcel r2 = android.os.Parcel.obtain()     // Catch:{ ParcelFormatException | IOException -> 0x03c4 }
            r8 = r2
            int r2 = r0.length     // Catch:{ ParcelFormatException | IOException -> 0x03c4 }
            r3 = 0
            r8.unmarshall(r0, r3, r2)     // Catch:{ ParcelFormatException | IOException -> 0x03c4 }
            r8.setDataPosition(r3)     // Catch:{ ParcelFormatException | IOException -> 0x03c4 }
            com.android.internal.os.BatteryStatsImpl r2 = new com.android.internal.os.BatteryStatsImpl     // Catch:{ ParcelFormatException | IOException -> 0x03c4 }
            r24 = 0
            com.android.internal.os.BatteryStatsImpl r3 = r1.mStats     // Catch:{ ParcelFormatException | IOException -> 0x03c4 }
            android.os.Handler r3 = r3.mHandler     // Catch:{ ParcelFormatException | IOException -> 0x03c4 }
            r26 = 0
            r27 = 0
            com.android.internal.os.BatteryStatsImpl$UserInfoProvider r4 = r1.mUserManagerUserInfoProvider     // Catch:{ ParcelFormatException | IOException -> 0x03c4 }
            r23 = r2
            r25 = r3
            r28 = r4
            r23.<init>(r24, r25, r26, r27, r28)     // Catch:{ ParcelFormatException | IOException -> 0x03c4 }
            r6 = r2
            r6.readSummaryFromParcel(r8)     // Catch:{ ParcelFormatException | IOException -> 0x03c4 }
            r8.recycle()     // Catch:{ ParcelFormatException | IOException -> 0x03c4 }
            android.content.Context r3 = r1.mContext     // Catch:{ ParcelFormatException | IOException -> 0x03c4 }
            r2 = r6
            r4 = r34
            r5 = r17
            r16 = r6
            r6 = r14
            r21 = r7
            r23 = r8
            r7 = r19
            r2.dumpCheckinLocked(r3, r4, r5, r6, r7)     // Catch:{ ParcelFormatException | IOException -> 0x03bf }
            com.android.internal.os.BatteryStatsImpl r2 = r1.mStats     // Catch:{ ParcelFormatException | IOException -> 0x03bf }
            com.android.internal.os.AtomicFile r2 = r2.mCheckinFile     // Catch:{ ParcelFormatException | IOException -> 0x03bf }
            r2.delete()     // Catch:{ ParcelFormatException | IOException -> 0x03bf }
            monitor-exit(r21)     // Catch:{ all -> 0x03ef }
            return
        L_0x03bf:
            r0 = move-exception
            goto L_0x03c7
        L_0x03c1:
            r21 = r7
            goto L_0x03e8
        L_0x03c4:
            r0 = move-exception
            r21 = r7
        L_0x03c7:
            java.lang.String r2 = "BatteryStatsService"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x03ef }
            r3.<init>()     // Catch:{ all -> 0x03ef }
            java.lang.String r4 = "Failure reading checkin file "
            r3.append(r4)     // Catch:{ all -> 0x03ef }
            com.android.internal.os.BatteryStatsImpl r4 = r1.mStats     // Catch:{ all -> 0x03ef }
            com.android.internal.os.AtomicFile r4 = r4.mCheckinFile     // Catch:{ all -> 0x03ef }
            java.io.File r4 = r4.getBaseFile()     // Catch:{ all -> 0x03ef }
            r3.append(r4)     // Catch:{ all -> 0x03ef }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x03ef }
            android.util.Slog.w(r2, r3, r0)     // Catch:{ all -> 0x03ef }
            goto L_0x03e8
        L_0x03e6:
            r21 = r7
        L_0x03e8:
            monitor-exit(r21)     // Catch:{ all -> 0x03ef }
            goto L_0x03f1
        L_0x03ea:
            r0 = move-exception
            r21 = r7
        L_0x03ed:
            monitor-exit(r21)     // Catch:{ all -> 0x03ef }
            throw r0
        L_0x03ef:
            r0 = move-exception
            goto L_0x03ed
        L_0x03f1:
            com.android.internal.os.BatteryStatsImpl r7 = r1.mStats
            monitor-enter(r7)
            com.android.internal.os.BatteryStatsImpl r2 = r1.mStats     // Catch:{ all -> 0x040d }
            android.content.Context r3 = r1.mContext     // Catch:{ all -> 0x040d }
            r4 = r34
            r5 = r17
            r6 = r14
            r16 = r7
            r7 = r19
            r2.dumpCheckinLocked(r3, r4, r5, r6, r7)     // Catch:{ all -> 0x0412 }
            if (r13 == 0) goto L_0x040b
            com.android.internal.os.BatteryStatsImpl r0 = r1.mStats     // Catch:{ all -> 0x0412 }
            r0.writeAsyncLocked()     // Catch:{ all -> 0x0412 }
        L_0x040b:
            monitor-exit(r16)     // Catch:{ all -> 0x0412 }
            goto L_0x042e
        L_0x040d:
            r0 = move-exception
            r16 = r7
        L_0x0410:
            monitor-exit(r16)     // Catch:{ all -> 0x0412 }
            throw r0
        L_0x0412:
            r0 = move-exception
            goto L_0x0410
        L_0x0414:
            com.android.internal.os.BatteryStatsImpl r7 = r1.mStats
            monitor-enter(r7)
            com.android.internal.os.BatteryStatsImpl r2 = r1.mStats     // Catch:{ all -> 0x042f }
            android.content.Context r3 = r1.mContext     // Catch:{ all -> 0x042f }
            r4 = r34
            r5 = r14
            r6 = r15
            r16 = r7
            r7 = r19
            r2.dumpLocked(r3, r4, r5, r6, r7)     // Catch:{ all -> 0x0434 }
            if (r13 == 0) goto L_0x042d
            com.android.internal.os.BatteryStatsImpl r0 = r1.mStats     // Catch:{ all -> 0x0434 }
            r0.writeAsyncLocked()     // Catch:{ all -> 0x0434 }
        L_0x042d:
            monitor-exit(r16)     // Catch:{ all -> 0x0434 }
        L_0x042e:
            return
        L_0x042f:
            r0 = move-exception
            r16 = r7
        L_0x0432:
            monitor-exit(r16)     // Catch:{ all -> 0x0434 }
            throw r0
        L_0x0434:
            r0 = move-exception
            goto L_0x0432
        L_0x0436:
            r0 = move-exception
            android.os.Binder.restoreCallingIdentity(r30)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.BatteryStatsService.dump(java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[]):void");
    }

    public CellularBatteryStats getCellularBatteryStats() {
        CellularBatteryStats cellularBatteryStats;
        synchronized (this.mStats) {
            cellularBatteryStats = this.mStats.getCellularBatteryStats();
        }
        return cellularBatteryStats;
    }

    public WifiBatteryStats getWifiBatteryStats() {
        WifiBatteryStats wifiBatteryStats;
        synchronized (this.mStats) {
            wifiBatteryStats = this.mStats.getWifiBatteryStats();
        }
        return wifiBatteryStats;
    }

    public GpsBatteryStats getGpsBatteryStats() {
        GpsBatteryStats gpsBatteryStats;
        synchronized (this.mStats) {
            gpsBatteryStats = this.mStats.getGpsBatteryStats();
        }
        return gpsBatteryStats;
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    public HealthStatsParceler takeUidSnapshot(int requestUid) {
        HealthStatsParceler healthStatsForUidLocked;
        if (requestUid != Binder.getCallingUid()) {
            this.mContext.enforceCallingOrSelfPermission("android.permission.BATTERY_STATS", (String) null);
        }
        long ident = Binder.clearCallingIdentity();
        try {
            if (shouldCollectExternalStats()) {
                syncStats("get-health-stats-for-uids", 31);
            }
            synchronized (this.mStats) {
                healthStatsForUidLocked = getHealthStatsForUidLocked(requestUid);
            }
            Binder.restoreCallingIdentity(ident);
            return healthStatsForUidLocked;
        } catch (Exception ex) {
            try {
                Slog.w(TAG, "Crashed while writing for takeUidSnapshot(" + requestUid + ")", ex);
                throw ex;
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(ident);
                throw th;
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    public HealthStatsParceler[] takeUidSnapshots(int[] requestUids) {
        HealthStatsParceler[] results;
        if (!onlyCaller(requestUids)) {
            this.mContext.enforceCallingOrSelfPermission("android.permission.BATTERY_STATS", (String) null);
        }
        long ident = Binder.clearCallingIdentity();
        try {
            if (shouldCollectExternalStats()) {
                syncStats("get-health-stats-for-uids", 31);
            }
            synchronized (this.mStats) {
                int N = requestUids.length;
                results = new HealthStatsParceler[N];
                for (int i = 0; i < N; i++) {
                    results[i] = getHealthStatsForUidLocked(requestUids[i]);
                }
            }
            Binder.restoreCallingIdentity(ident);
            return results;
        } catch (Exception ex) {
            try {
                throw ex;
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(ident);
                throw th;
            }
        }
    }

    private boolean shouldCollectExternalStats() {
        return SystemClock.elapsedRealtime() - this.mWorker.getLastCollectionTimeStamp() > this.mStats.getExternalStatsCollectionRateLimitMs();
    }

    private static boolean onlyCaller(int[] requestUids) {
        int caller = Binder.getCallingUid();
        for (int i : requestUids) {
            if (i != caller) {
                return false;
            }
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public HealthStatsParceler getHealthStatsForUidLocked(int requestUid) {
        HealthStatsBatteryStatsWriter writer = new HealthStatsBatteryStatsWriter();
        HealthStatsWriter uidWriter = new HealthStatsWriter(UidHealthStats.CONSTANTS);
        BatteryStats.Uid uid = (BatteryStats.Uid) this.mStats.getUidStats().get(requestUid);
        if (uid != null) {
            writer.writeUid(uidWriter, this.mStats, uid);
        }
        return new HealthStatsParceler(uidWriter);
    }

    public boolean setChargingStateUpdateDelayMillis(int delayMillis) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.POWER_SAVER", (String) null);
        long ident = Binder.clearCallingIdentity();
        try {
            return Settings.Global.putLong(this.mContext.getContentResolver(), "battery_charging_state_update_delay", (long) delayMillis);
        } finally {
            Binder.restoreCallingIdentity(ident);
        }
    }
}
