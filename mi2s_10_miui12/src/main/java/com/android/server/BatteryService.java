package com.android.server;

import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.hardware.health.V1_0.HealthInfo;
import android.hardware.health.V2_0.IHealth;
import android.hardware.health.V2_0.IHealthInfoCallback;
import android.hardware.health.V2_0.Result;
import android.hidl.manager.V1_0.IServiceManager;
import android.hidl.manager.V1_0.IServiceNotification;
import android.metrics.LogMaker;
import android.os.BatteryManagerInternal;
import android.os.BatteryProperty;
import android.os.Binder;
import android.os.Bundle;
import android.os.DropBoxManager;
import android.os.FileUtils;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBatteryPropertiesRegistrar;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ServiceManager;
import android.os.ShellCallback;
import android.os.ShellCommand;
import android.os.SystemClock;
import android.os.Trace;
import android.os.UEventObserver;
import android.os.UserHandle;
import android.provider.Settings;
import android.server.am.SplitScreenReporter;
import android.util.EventLog;
import android.util.MutableInt;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.IBatteryStats;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.util.DumpUtils;
import com.android.server.BatteryService;
import com.android.server.am.BatteryStatsService;
import com.android.server.am.BroadcastQueueInjector;
import com.android.server.lights.Light;
import com.android.server.lights.LightsManager;
import com.android.server.pm.DumpState;
import com.android.server.storage.DeviceStorageMonitorService;
import com.android.server.utils.PriorityDump;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public final class BatteryService extends SystemService {
    private static final long BATTERY_LEVEL_CHANGE_THROTTLE_MS = 60000;
    private static final int BATTERY_PLUGGED_NONE = 0;
    private static final int BATTERY_SCALE = 100;
    private static final boolean DEBUG = false;
    private static final String[] DUMPSYS_ARGS = {"--checkin", "--unplugged"};
    private static final String DUMPSYS_DATA_PATH = "/data/system/";
    private static final long HEALTH_HAL_WAIT_MS = 1000;
    private static final String HVDCP3_TYPE_EVENT = "POWER_SUPPLY_HVDCP3_TYPE";
    private static final int MAX_BATTERY_LEVELS_QUEUE_SIZE = 100;
    static final int OPTION_FORCE_UPDATE = 1;
    private static final String QUICK_CHARGE_TYPE_EVENT = "POWER_SUPPLY_QUICK_CHARGE_TYPE";
    /* access modifiers changed from: private */
    public static final String TAG = BatteryService.class.getSimpleName();
    /* access modifiers changed from: private */
    public ActivityManagerInternal mActivityManagerInternal;
    private boolean mBatteryLevelCritical;
    /* access modifiers changed from: private */
    public boolean mBatteryLevelLow;
    private ArrayDeque<Bundle> mBatteryLevelsEventQueue;
    private BatteryPropertiesRegistrar mBatteryPropertiesRegistrar;
    private final IBatteryStats mBatteryStats;
    BinderService mBinderService;
    private int mChargeStartLevel;
    private long mChargeStartTime;
    /* access modifiers changed from: private */
    public final Context mContext;
    private int mCriticalBatteryLevel;
    private int mDischargeStartLevel;
    private long mDischargeStartTime;
    private final Handler mHandler;
    private HealthHalCallback mHealthHalCallback;
    /* access modifiers changed from: private */
    public HealthInfo mHealthInfo;
    /* access modifiers changed from: private */
    public HealthServiceWrapper mHealthServiceWrapper;
    /* access modifiers changed from: private */
    public int mInvalidCharger;
    private int mLastBatteryHealth;
    private int mLastBatteryLevel;
    private long mLastBatteryLevelChangedSentMs;
    private boolean mLastBatteryLevelCritical;
    private boolean mLastBatteryPresent;
    private int mLastBatteryStatus;
    private int mLastBatteryTemperature;
    private int mLastBatteryVoltage;
    private int mLastChargeCounter;
    private final HealthInfo mLastHealthInfo = new HealthInfo();
    /* access modifiers changed from: private */
    public int mLastHvdcpType;
    private int mLastInvalidCharger;
    private int mLastMaxChargingCurrent;
    private int mLastMaxChargingVoltage;
    private int mLastPlugType = -1;
    /* access modifiers changed from: private */
    public int mLastQuickChargeType;
    private Led mLed;
    /* access modifiers changed from: private */
    public final Object mLock = new Object();
    private int mLowBatteryCloseWarningLevel;
    /* access modifiers changed from: private */
    public int mLowBatteryWarningLevel;
    private MetricsLogger mMetricsLogger;
    /* access modifiers changed from: private */
    public int mPlugType;
    private boolean mSentLowBatteryBroadcast = false;
    private int mSequence = 1;
    private int mShutdownBatteryTemperature;
    private final UEventObserver mUEventObserver;
    private boolean mUpdatesStopped;

    /* access modifiers changed from: private */
    public void updateHvdcpType(int hvdcpType) {
        final Intent hvdcpTypeIntent = new Intent("miui.intent.action.ACTION_HVDCP_TYPE");
        hvdcpTypeIntent.addFlags(822083584);
        hvdcpTypeIntent.putExtra("miui.intent.extra.hvdcp_type", hvdcpType);
        this.mLastHvdcpType = hvdcpType;
        this.mHandler.post(new Runnable() {
            public void run() {
                BatteryService.this.mContext.sendStickyBroadcastAsUser(hvdcpTypeIntent, UserHandle.ALL);
            }
        });
    }

    /* access modifiers changed from: private */
    public void updateQuickChargeType(int quickChargeType) {
        final Intent quickChargeTypeIntent = new Intent("miui.intent.action.ACTION_QUICK_CHARGE_TYPE");
        quickChargeTypeIntent.addFlags(822083584);
        quickChargeTypeIntent.putExtra("miui.intent.extra.quick_charge_type", quickChargeType);
        this.mLastQuickChargeType = quickChargeType;
        this.mHandler.post(new Runnable() {
            public void run() {
                BatteryService.this.mContext.sendStickyBroadcastAsUser(quickChargeTypeIntent, UserHandle.ALL);
            }
        });
    }

    private final class BatteryUEventObserver extends UEventObserver {
        private BatteryUEventObserver() {
        }

        public void onUEvent(UEventObserver.UEvent event) {
            int quickChargeType;
            int hvdcpType;
            if (!(event.get(BatteryService.HVDCP3_TYPE_EVENT) == null || (hvdcpType = Integer.parseInt(event.get(BatteryService.HVDCP3_TYPE_EVENT))) == BatteryService.this.mLastHvdcpType)) {
                String access$200 = BatteryService.TAG;
                Slog.d(access$200, "HVDCP type = " + hvdcpType + " Last HVDCP type = " + BatteryService.this.mLastHvdcpType);
                BatteryService.this.updateHvdcpType(hvdcpType);
            }
            if (event.get(BatteryService.QUICK_CHARGE_TYPE_EVENT) != null && (quickChargeType = Integer.parseInt(event.get(BatteryService.QUICK_CHARGE_TYPE_EVENT))) != BatteryService.this.mLastQuickChargeType) {
                String access$2002 = BatteryService.TAG;
                Slog.i(access$2002, "Quick Charge type = " + quickChargeType + " Last Quick Charge type = " + BatteryService.this.mLastQuickChargeType);
                BatteryService.this.updateQuickChargeType(quickChargeType);
            }
        }
    }

    public BatteryService(Context context) {
        super(context);
        this.mContext = context;
        this.mHandler = new Handler(true);
        this.mLed = new Led(context, (LightsManager) getLocalService(LightsManager.class));
        this.mBatteryStats = BatteryStatsService.getService();
        this.mActivityManagerInternal = (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
        this.mCriticalBatteryLevel = this.mContext.getResources().getInteger(17694763);
        this.mLowBatteryWarningLevel = this.mContext.getResources().getInteger(17694828);
        this.mLowBatteryCloseWarningLevel = this.mLowBatteryWarningLevel + this.mContext.getResources().getInteger(17694826);
        this.mShutdownBatteryTemperature = this.mContext.getResources().getInteger(17694896);
        this.mBatteryLevelsEventQueue = new ArrayDeque<>();
        this.mMetricsLogger = new MetricsLogger();
        if (new File("/sys/devices/virtual/switch/invalid_charger/state").exists()) {
            new UEventObserver() {
                public void onUEvent(UEventObserver.UEvent event) {
                    int invalidCharger = SplitScreenReporter.ACTION_ENTER_SPLIT.equals(event.get("SWITCH_STATE"));
                    synchronized (BatteryService.this.mLock) {
                        if (BatteryService.this.mInvalidCharger != invalidCharger) {
                            int unused = BatteryService.this.mInvalidCharger = (int) invalidCharger;
                        }
                    }
                }
            }.startObserving("DEVPATH=/devices/virtual/switch/invalid_charger");
        }
        this.mUEventObserver = new BatteryUEventObserver();
        this.mUEventObserver.startObserving(QUICK_CHARGE_TYPE_EVENT);
        this.mUEventObserver.startObserving(HVDCP3_TYPE_EVENT);
    }

    /* JADX WARNING: type inference failed for: r0v3, types: [com.android.server.BatteryService$BatteryPropertiesRegistrar, android.os.IBinder] */
    public void onStart() {
        registerHealthCallback();
        this.mBinderService = new BinderService();
        publishBinderService("battery", this.mBinderService);
        this.mBatteryPropertiesRegistrar = new BatteryPropertiesRegistrar();
        publishBinderService("batteryproperties", this.mBatteryPropertiesRegistrar);
        publishLocalService(BatteryManagerInternal.class, new LocalService());
    }

    public void onBootPhase(int phase) {
        if (phase == 550) {
            synchronized (this.mLock) {
                this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("low_power_trigger_level"), false, new ContentObserver(this.mHandler) {
                    public void onChange(boolean selfChange) {
                        synchronized (BatteryService.this.mLock) {
                            BatteryService.this.updateBatteryWarningLevelLocked();
                        }
                    }
                }, -1);
                updateBatteryWarningLevelLocked();
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    private void registerHealthCallback() {
        traceBegin("HealthInitWrapper");
        this.mHealthServiceWrapper = new HealthServiceWrapper();
        this.mHealthHalCallback = new HealthHalCallback();
        try {
            this.mHealthServiceWrapper.init(this.mHealthHalCallback, new HealthServiceWrapper.IServiceManagerSupplier() {
            }, new HealthServiceWrapper.IHealthSupplier() {
            });
            traceEnd();
            traceBegin("HealthInitWaitUpdate");
            long beforeWait = SystemClock.uptimeMillis();
            synchronized (this.mLock) {
                while (this.mHealthInfo == null) {
                    String str = TAG;
                    Slog.i(str, "health: Waited " + (SystemClock.uptimeMillis() - beforeWait) + "ms for callbacks. Waiting another " + 1000 + " ms...");
                    try {
                        this.mLock.wait(1000);
                    } catch (InterruptedException e) {
                        Slog.i(TAG, "health: InterruptedException when waiting for update.  Continuing...");
                    }
                }
            }
            String str2 = TAG;
            Slog.i(str2, "health: Waited " + (SystemClock.uptimeMillis() - beforeWait) + "ms and received the update.");
            traceEnd();
        } catch (RemoteException ex) {
            Slog.e(TAG, "health: cannot register callback. (RemoteException)");
            throw ex.rethrowFromSystemServer();
        } catch (NoSuchElementException ex2) {
            Slog.e(TAG, "health: cannot register callback. (no supported health HAL service)");
            throw ex2;
        } catch (Throwable th) {
            traceEnd();
            throw th;
        }
    }

    /* access modifiers changed from: private */
    public void updateBatteryWarningLevelLocked() {
        ContentResolver resolver = this.mContext.getContentResolver();
        int defWarnLevel = this.mContext.getResources().getInteger(17694828);
        this.mLowBatteryWarningLevel = Settings.Global.getInt(resolver, "low_power_trigger_level", defWarnLevel);
        if (this.mLowBatteryWarningLevel == 0) {
            this.mLowBatteryWarningLevel = defWarnLevel;
        }
        int i = this.mLowBatteryWarningLevel;
        int i2 = this.mCriticalBatteryLevel;
        if (i < i2) {
            this.mLowBatteryWarningLevel = i2;
        }
        this.mLowBatteryCloseWarningLevel = this.mLowBatteryWarningLevel + this.mContext.getResources().getInteger(17694826);
        processValuesLocked(true);
    }

    /* access modifiers changed from: private */
    public boolean isPoweredLocked(int plugTypeSet) {
        if (this.mHealthInfo.batteryStatus == 1) {
            return true;
        }
        if ((plugTypeSet & 1) != 0 && this.mHealthInfo.chargerAcOnline) {
            return true;
        }
        if ((plugTypeSet & 2) != 0 && this.mHealthInfo.chargerUsbOnline) {
            return true;
        }
        if ((plugTypeSet & 4) == 0 || !this.mHealthInfo.chargerWirelessOnline) {
            return false;
        }
        return true;
    }

    private boolean shouldSendBatteryLowLocked() {
        int i;
        boolean plugged = this.mPlugType != 0;
        boolean oldPlugged = this.mLastPlugType != 0;
        if (plugged || this.mHealthInfo.batteryStatus == 1 || this.mHealthInfo.batteryLevel > (i = this.mLowBatteryWarningLevel)) {
            return false;
        }
        if (oldPlugged || this.mLastBatteryLevel > i) {
            return true;
        }
        return false;
    }

    private boolean shouldShutdownLocked() {
        if (this.mHealthInfo.batteryLevel <= 0 && this.mHealthInfo.batteryPresent && this.mHealthInfo.batteryStatus != 2) {
            return true;
        }
        return false;
    }

    private void shutdownIfNoPowerLocked() {
        if (shouldShutdownLocked()) {
            this.mHandler.post(new Runnable() {
                public void run() {
                    if (BatteryService.this.mActivityManagerInternal.isSystemReady()) {
                        Intent intent = new Intent("com.android.internal.intent.action.REQUEST_SHUTDOWN");
                        intent.putExtra("android.intent.extra.KEY_CONFIRM", false);
                        intent.putExtra("android.intent.extra.REASON", "battery");
                        intent.setFlags(268435456);
                        BatteryService.this.mContext.startActivityAsUser(intent, UserHandle.CURRENT);
                    }
                }
            });
        }
    }

    private void shutdownIfOverTempLocked() {
        if (this.mHealthInfo.batteryTemperature > this.mShutdownBatteryTemperature) {
            this.mHandler.post(new Runnable() {
                public void run() {
                    if (BatteryService.this.mActivityManagerInternal.isSystemReady()) {
                        Intent intent = new Intent("com.android.internal.intent.action.REQUEST_SHUTDOWN");
                        intent.putExtra("android.intent.extra.KEY_CONFIRM", false);
                        intent.putExtra("android.intent.extra.REASON", "thermal,battery");
                        intent.setFlags(268435456);
                        BatteryService.this.mContext.startActivityAsUser(intent, UserHandle.CURRENT);
                    }
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void update(android.hardware.health.V2_0.HealthInfo info) {
        traceBegin("HealthInfoUpdate");
        Trace.traceCounter(131072, "BatteryChargeCounter", info.legacy.batteryChargeCounter);
        Trace.traceCounter(131072, "BatteryCurrent", info.legacy.batteryCurrent);
        synchronized (this.mLock) {
            if (!this.mUpdatesStopped) {
                this.mHealthInfo = info.legacy;
                processValuesLocked(false);
                this.mLock.notifyAll();
            } else {
                copy(this.mLastHealthInfo, info.legacy);
            }
        }
        traceEnd();
    }

    private static void copy(HealthInfo dst, HealthInfo src) {
        dst.chargerAcOnline = src.chargerAcOnline;
        dst.chargerUsbOnline = src.chargerUsbOnline;
        dst.chargerWirelessOnline = src.chargerWirelessOnline;
        dst.maxChargingCurrent = src.maxChargingCurrent;
        dst.maxChargingVoltage = src.maxChargingVoltage;
        dst.batteryStatus = src.batteryStatus;
        dst.batteryHealth = src.batteryHealth;
        dst.batteryPresent = src.batteryPresent;
        dst.batteryLevel = src.batteryLevel;
        dst.batteryVoltage = src.batteryVoltage;
        dst.batteryTemperature = src.batteryTemperature;
        dst.batteryCurrent = src.batteryCurrent;
        dst.batteryCycleCount = src.batteryCycleCount;
        dst.batteryFullCharge = src.batteryFullCharge;
        dst.batteryChargeCounter = src.batteryChargeCounter;
        dst.batteryTechnology = src.batteryTechnology;
    }

    private void processValuesLocked(boolean force) {
        boolean logOutlier = false;
        long dischargeDuration = 0;
        this.mBatteryLevelCritical = this.mHealthInfo.batteryStatus != 1 && this.mHealthInfo.batteryLevel <= this.mCriticalBatteryLevel;
        if (this.mHealthInfo.chargerAcOnline) {
            this.mPlugType = 1;
        } else if (this.mHealthInfo.chargerUsbOnline) {
            this.mPlugType = 2;
        } else if (this.mHealthInfo.chargerWirelessOnline) {
            this.mPlugType = 4;
        } else {
            this.mPlugType = 0;
        }
        try {
            this.mBatteryStats.setBatteryState(this.mHealthInfo.batteryStatus, this.mHealthInfo.batteryHealth, this.mPlugType, this.mHealthInfo.batteryLevel, this.mHealthInfo.batteryTemperature, this.mHealthInfo.batteryVoltage, this.mHealthInfo.batteryChargeCounter, this.mHealthInfo.batteryFullCharge);
        } catch (RemoteException e) {
        }
        shutdownIfNoPowerLocked();
        shutdownIfOverTempLocked();
        if (force || this.mHealthInfo.batteryStatus != this.mLastBatteryStatus || this.mHealthInfo.batteryHealth != this.mLastBatteryHealth || this.mHealthInfo.batteryPresent != this.mLastBatteryPresent || this.mHealthInfo.batteryLevel != this.mLastBatteryLevel || this.mPlugType != this.mLastPlugType || this.mHealthInfo.batteryVoltage != this.mLastBatteryVoltage || this.mHealthInfo.batteryTemperature != this.mLastBatteryTemperature || this.mHealthInfo.maxChargingCurrent != this.mLastMaxChargingCurrent || this.mHealthInfo.maxChargingVoltage != this.mLastMaxChargingVoltage || this.mHealthInfo.batteryChargeCounter != this.mLastChargeCounter || this.mInvalidCharger != this.mLastInvalidCharger) {
            int i = this.mPlugType;
            int i2 = this.mLastPlugType;
            if (i != i2) {
                if (i2 == 0) {
                    this.mChargeStartLevel = this.mHealthInfo.batteryLevel;
                    this.mChargeStartTime = SystemClock.elapsedRealtime();
                    LogMaker builder = new LogMaker(1417);
                    builder.setType(4);
                    builder.addTaggedData(1421, Integer.valueOf(this.mPlugType));
                    builder.addTaggedData(1418, Integer.valueOf(this.mHealthInfo.batteryLevel));
                    this.mMetricsLogger.write(builder);
                    if (!(this.mDischargeStartTime == 0 || this.mDischargeStartLevel == this.mHealthInfo.batteryLevel)) {
                        dischargeDuration = SystemClock.elapsedRealtime() - this.mDischargeStartTime;
                        logOutlier = true;
                        EventLog.writeEvent(EventLogTags.BATTERY_DISCHARGE, new Object[]{Long.valueOf(dischargeDuration), Integer.valueOf(this.mDischargeStartLevel), Integer.valueOf(this.mHealthInfo.batteryLevel)});
                        this.mDischargeStartTime = 0;
                    }
                } else if (i == 0) {
                    this.mDischargeStartTime = SystemClock.elapsedRealtime();
                    this.mDischargeStartLevel = this.mHealthInfo.batteryLevel;
                    long elapsedRealtime = SystemClock.elapsedRealtime();
                    long j = this.mChargeStartTime;
                    long chargeDuration = elapsedRealtime - j;
                    if (!(j == 0 || chargeDuration == 0)) {
                        LogMaker builder2 = new LogMaker(1417);
                        builder2.setType(5);
                        builder2.addTaggedData(1421, Integer.valueOf(this.mLastPlugType));
                        builder2.addTaggedData(1420, Long.valueOf(chargeDuration));
                        builder2.addTaggedData(1418, Integer.valueOf(this.mChargeStartLevel));
                        builder2.addTaggedData(1419, Integer.valueOf(this.mHealthInfo.batteryLevel));
                        this.mMetricsLogger.write(builder2);
                    }
                    this.mChargeStartTime = 0;
                }
            }
            if (!(this.mHealthInfo.batteryStatus == this.mLastBatteryStatus && this.mHealthInfo.batteryHealth == this.mLastBatteryHealth && this.mHealthInfo.batteryPresent == this.mLastBatteryPresent && this.mPlugType == this.mLastPlugType)) {
                EventLog.writeEvent(EventLogTags.BATTERY_STATUS, new Object[]{Integer.valueOf(this.mHealthInfo.batteryStatus), Integer.valueOf(this.mHealthInfo.batteryHealth), Integer.valueOf(this.mHealthInfo.batteryPresent ? 1 : 0), Integer.valueOf(this.mPlugType), this.mHealthInfo.batteryTechnology});
            }
            if (this.mHealthInfo.batteryLevel != this.mLastBatteryLevel) {
                EventLog.writeEvent(EventLogTags.BATTERY_LEVEL, new Object[]{Integer.valueOf(this.mHealthInfo.batteryLevel), Integer.valueOf(this.mHealthInfo.batteryVoltage), Integer.valueOf(this.mHealthInfo.batteryTemperature)});
            }
            if (this.mBatteryLevelCritical && !this.mLastBatteryLevelCritical && this.mPlugType == 0) {
                logOutlier = true;
                dischargeDuration = SystemClock.elapsedRealtime() - this.mDischargeStartTime;
            }
            if (!this.mBatteryLevelLow) {
                if (this.mPlugType == 0 && this.mHealthInfo.batteryStatus != 1 && this.mHealthInfo.batteryLevel <= this.mLowBatteryWarningLevel) {
                    this.mBatteryLevelLow = true;
                }
            } else if (this.mPlugType != 0) {
                this.mBatteryLevelLow = false;
            } else if (this.mHealthInfo.batteryLevel >= this.mLowBatteryCloseWarningLevel) {
                this.mBatteryLevelLow = false;
            } else if (force && this.mHealthInfo.batteryLevel >= this.mLowBatteryWarningLevel) {
                this.mBatteryLevelLow = false;
            }
            this.mSequence++;
            if (this.mPlugType != 0 && this.mLastPlugType == 0) {
                final Intent statusIntent = new Intent("android.intent.action.ACTION_POWER_CONNECTED");
                statusIntent.setFlags(BroadcastQueueInjector.FLAG_IMMUTABLE);
                statusIntent.putExtra(DeviceStorageMonitorService.EXTRA_SEQUENCE, this.mSequence);
                this.mHandler.post(new Runnable() {
                    public void run() {
                        BatteryService.this.mContext.sendBroadcastAsUser(statusIntent, UserHandle.ALL);
                    }
                });
            } else if (this.mPlugType == 0 && this.mLastPlugType != 0) {
                final Intent statusIntent2 = new Intent("android.intent.action.ACTION_POWER_DISCONNECTED");
                statusIntent2.setFlags(BroadcastQueueInjector.FLAG_IMMUTABLE);
                statusIntent2.putExtra(DeviceStorageMonitorService.EXTRA_SEQUENCE, this.mSequence);
                this.mHandler.post(new Runnable() {
                    public void run() {
                        BatteryService.this.mContext.sendBroadcastAsUser(statusIntent2, UserHandle.ALL);
                    }
                });
            }
            if (shouldSendBatteryLowLocked()) {
                this.mSentLowBatteryBroadcast = true;
                final Intent statusIntent3 = new Intent("android.intent.action.BATTERY_LOW");
                statusIntent3.setFlags(BroadcastQueueInjector.FLAG_IMMUTABLE);
                statusIntent3.putExtra(DeviceStorageMonitorService.EXTRA_SEQUENCE, this.mSequence);
                this.mHandler.post(new Runnable() {
                    public void run() {
                        BatteryService.this.mContext.sendBroadcastAsUser(statusIntent3, UserHandle.ALL);
                    }
                });
            } else if (this.mSentLowBatteryBroadcast && this.mHealthInfo.batteryLevel >= this.mLowBatteryCloseWarningLevel) {
                this.mSentLowBatteryBroadcast = false;
                final Intent statusIntent4 = new Intent("android.intent.action.BATTERY_OKAY");
                statusIntent4.setFlags(BroadcastQueueInjector.FLAG_IMMUTABLE);
                statusIntent4.putExtra(DeviceStorageMonitorService.EXTRA_SEQUENCE, this.mSequence);
                this.mHandler.post(new Runnable() {
                    public void run() {
                        BatteryService.this.mContext.sendBroadcastAsUser(statusIntent4, UserHandle.ALL);
                    }
                });
            }
            sendBatteryChangedIntentLocked();
            if (!(this.mLastBatteryLevel == this.mHealthInfo.batteryLevel && this.mLastPlugType == this.mPlugType)) {
                sendBatteryLevelChangedIntentLocked();
            }
            this.mLed.updateLightsLocked();
            if (logOutlier && dischargeDuration != 0) {
                logOutlierLocked(dischargeDuration);
            }
            this.mLastBatteryStatus = this.mHealthInfo.batteryStatus;
            this.mLastBatteryHealth = this.mHealthInfo.batteryHealth;
            this.mLastBatteryPresent = this.mHealthInfo.batteryPresent;
            this.mLastBatteryLevel = this.mHealthInfo.batteryLevel;
            this.mLastPlugType = this.mPlugType;
            this.mLastBatteryVoltage = this.mHealthInfo.batteryVoltage;
            this.mLastBatteryTemperature = this.mHealthInfo.batteryTemperature;
            this.mLastMaxChargingCurrent = this.mHealthInfo.maxChargingCurrent;
            this.mLastMaxChargingVoltage = this.mHealthInfo.maxChargingVoltage;
            this.mLastChargeCounter = this.mHealthInfo.batteryChargeCounter;
            this.mLastBatteryLevelCritical = this.mBatteryLevelCritical;
            this.mLastInvalidCharger = this.mInvalidCharger;
        }
    }

    private void sendBatteryChangedIntentLocked() {
        Intent intent = new Intent("android.intent.action.BATTERY_CHANGED");
        intent.addFlags(1610612736);
        int icon = getIconLocked(this.mHealthInfo.batteryLevel);
        intent.putExtra(DeviceStorageMonitorService.EXTRA_SEQUENCE, this.mSequence);
        intent.putExtra("status", this.mHealthInfo.batteryStatus);
        intent.putExtra("health", this.mHealthInfo.batteryHealth);
        intent.putExtra("present", this.mHealthInfo.batteryPresent);
        intent.putExtra("level", this.mHealthInfo.batteryLevel);
        intent.putExtra("battery_low", this.mSentLowBatteryBroadcast);
        intent.putExtra("scale", 100);
        intent.putExtra("icon-small", icon);
        intent.putExtra("plugged", this.mPlugType);
        intent.putExtra("voltage", this.mHealthInfo.batteryVoltage);
        intent.putExtra("temperature", this.mHealthInfo.batteryTemperature);
        intent.putExtra("technology", this.mHealthInfo.batteryTechnology);
        intent.putExtra("invalid_charger", this.mInvalidCharger);
        intent.putExtra("max_charging_current", this.mHealthInfo.maxChargingCurrent);
        intent.putExtra("max_charging_voltage", this.mHealthInfo.maxChargingVoltage);
        intent.putExtra("charge_counter", this.mHealthInfo.batteryChargeCounter);
        this.mHandler.post(new Runnable(intent) {
            private final /* synthetic */ Intent f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                ActivityManager.broadcastStickyIntent(this.f$0, -1);
            }
        });
    }

    private void sendBatteryLevelChangedIntentLocked() {
        Bundle event = new Bundle();
        long now = SystemClock.elapsedRealtime();
        event.putInt(DeviceStorageMonitorService.EXTRA_SEQUENCE, this.mSequence);
        event.putInt("status", this.mHealthInfo.batteryStatus);
        event.putInt("health", this.mHealthInfo.batteryHealth);
        event.putBoolean("present", this.mHealthInfo.batteryPresent);
        event.putInt("level", this.mHealthInfo.batteryLevel);
        event.putBoolean("battery_low", this.mSentLowBatteryBroadcast);
        event.putInt("scale", 100);
        event.putInt("plugged", this.mPlugType);
        event.putInt("voltage", this.mHealthInfo.batteryVoltage);
        event.putLong("android.os.extra.EVENT_TIMESTAMP", now);
        boolean queueWasEmpty = this.mBatteryLevelsEventQueue.isEmpty();
        this.mBatteryLevelsEventQueue.add(event);
        if (this.mBatteryLevelsEventQueue.size() > 100) {
            this.mBatteryLevelsEventQueue.removeFirst();
        }
        if (queueWasEmpty) {
            long j = this.mLastBatteryLevelChangedSentMs;
            this.mHandler.postDelayed(new Runnable() {
                public final void run() {
                    BatteryService.this.sendEnqueuedBatteryLevelChangedEvents();
                }
            }, now - j > 60000 ? 0 : (j + 60000) - now);
        }
    }

    /* access modifiers changed from: private */
    public void sendEnqueuedBatteryLevelChangedEvents() {
        ArrayList<Bundle> events;
        synchronized (this.mLock) {
            events = new ArrayList<>(this.mBatteryLevelsEventQueue);
            this.mBatteryLevelsEventQueue.clear();
        }
        Intent intent = new Intent("android.intent.action.BATTERY_LEVEL_CHANGED");
        intent.addFlags(DumpState.DUMP_SERVICE_PERMISSIONS);
        intent.putParcelableArrayListExtra("android.os.extra.EVENTS", events);
        this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL, "android.permission.BATTERY_STATS");
        this.mLastBatteryLevelChangedSentMs = SystemClock.elapsedRealtime();
    }

    private void logBatteryStatsLocked() {
        DropBoxManager db;
        String str;
        StringBuilder sb;
        IBinder batteryInfoService = ServiceManager.getService("batterystats");
        if (batteryInfoService != null && (db = (DropBoxManager) this.mContext.getSystemService("dropbox")) != null && db.isTagEnabled("BATTERY_DISCHARGE_INFO")) {
            File dumpFile = null;
            FileOutputStream dumpStream = null;
            try {
                dumpFile = new File("/data/system/batterystats.dump");
                dumpStream = new FileOutputStream(dumpFile);
                batteryInfoService.dump(dumpStream.getFD(), DUMPSYS_ARGS);
                FileUtils.sync(dumpStream);
                db.addFile("BATTERY_DISCHARGE_INFO", dumpFile, 2);
                try {
                    dumpStream.close();
                } catch (IOException e) {
                    Slog.e(TAG, "failed to close dumpsys output stream");
                }
                if (!dumpFile.delete()) {
                    str = TAG;
                    sb = new StringBuilder();
                    sb.append("failed to delete temporary dumpsys file: ");
                    sb.append(dumpFile.getAbsolutePath());
                    Slog.e(str, sb.toString());
                }
            } catch (RemoteException e2) {
                Slog.e(TAG, "failed to dump battery service", e2);
                if (dumpStream != null) {
                    try {
                        dumpStream.close();
                    } catch (IOException e3) {
                        Slog.e(TAG, "failed to close dumpsys output stream");
                    }
                }
                if (dumpFile != null && !dumpFile.delete()) {
                    str = TAG;
                    sb = new StringBuilder();
                }
            } catch (IOException e4) {
                Slog.e(TAG, "failed to write dumpsys file", e4);
                if (dumpStream != null) {
                    try {
                        dumpStream.close();
                    } catch (IOException e5) {
                        Slog.e(TAG, "failed to close dumpsys output stream");
                    }
                }
                if (dumpFile != null && !dumpFile.delete()) {
                    str = TAG;
                    sb = new StringBuilder();
                }
            } catch (Throwable th) {
                if (dumpStream != null) {
                    try {
                        dumpStream.close();
                    } catch (IOException e6) {
                        Slog.e(TAG, "failed to close dumpsys output stream");
                    }
                }
                if (dumpFile != null && !dumpFile.delete()) {
                    String str2 = TAG;
                    Slog.e(str2, "failed to delete temporary dumpsys file: " + dumpFile.getAbsolutePath());
                }
                throw th;
            }
        }
    }

    private void logOutlierLocked(long duration) {
        ContentResolver cr = this.mContext.getContentResolver();
        String dischargeThresholdString = Settings.Global.getString(cr, "battery_discharge_threshold");
        String durationThresholdString = Settings.Global.getString(cr, "battery_discharge_duration_threshold");
        if (dischargeThresholdString != null && durationThresholdString != null) {
            try {
                long durationThreshold = Long.parseLong(durationThresholdString);
                int dischargeThreshold = Integer.parseInt(dischargeThresholdString);
                if (duration <= durationThreshold && this.mDischargeStartLevel - this.mHealthInfo.batteryLevel >= dischargeThreshold) {
                    logBatteryStatsLocked();
                }
            } catch (NumberFormatException e) {
                String str = TAG;
                Slog.e(str, "Invalid DischargeThresholds GService string: " + durationThresholdString + " or " + dischargeThresholdString);
            }
        }
    }

    private int getIconLocked(int level) {
        if (this.mHealthInfo.batteryStatus == 2) {
            return 17303585;
        }
        if (this.mHealthInfo.batteryStatus == 3) {
            return 17303571;
        }
        if (this.mHealthInfo.batteryStatus != 4 && this.mHealthInfo.batteryStatus != 5) {
            return 17303599;
        }
        if (!isPoweredLocked(7) || this.mHealthInfo.batteryLevel < 100) {
            return 17303571;
        }
        return 17303585;
    }

    class Shell extends ShellCommand {
        Shell() {
        }

        public int onCommand(String cmd) {
            return BatteryService.this.onShellCommand(this, cmd);
        }

        public void onHelp() {
            BatteryService.dumpHelp(getOutPrintWriter());
        }
    }

    static void dumpHelp(PrintWriter pw) {
        pw.println("Battery service (battery) commands:");
        pw.println("  help");
        pw.println("    Print this help text.");
        pw.println("  set [-f] [ac|usb|wireless|status|level|temp|present|invalid] <value>");
        pw.println("    Force a battery property value, freezing battery state.");
        pw.println("    -f: force a battery change broadcast be sent, prints new sequence.");
        pw.println("  unplug [-f]");
        pw.println("    Force battery unplugged, freezing battery state.");
        pw.println("    -f: force a battery change broadcast be sent, prints new sequence.");
        pw.println("  reset [-f]");
        pw.println("    Unfreeze battery state, returning to current hardware values.");
        pw.println("    -f: force a battery change broadcast be sent, prints new sequence.");
    }

    /* access modifiers changed from: package-private */
    public int parseOptions(Shell shell) {
        int opts = 0;
        while (true) {
            String nextOption = shell.getNextOption();
            String opt = nextOption;
            if (nextOption == null) {
                return opts;
            }
            if ("-f".equals(opt)) {
                opts |= 1;
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 11 */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x01c2  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x004a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int onShellCommand(com.android.server.BatteryService.Shell r12, java.lang.String r13) {
        /*
            r11 = this;
            if (r13 != 0) goto L_0x0007
            int r0 = r12.handleDefaultCommands(r13)
            return r0
        L_0x0007:
            java.io.PrintWriter r0 = r12.getOutPrintWriter()
            int r1 = r13.hashCode()
            r2 = -840325209(0xffffffffcde9a7a7, float:-4.90009824E8)
            r3 = 2
            r4 = -1
            r5 = 1
            r6 = 0
            if (r1 == r2) goto L_0x0039
            r2 = 113762(0x1bc62, float:1.59415E-40)
            if (r1 == r2) goto L_0x002e
            r2 = 108404047(0x6761d4f, float:4.628899E-35)
            if (r1 == r2) goto L_0x0023
        L_0x0022:
            goto L_0x0044
        L_0x0023:
            java.lang.String r1 = "reset"
            boolean r1 = r13.equals(r1)
            if (r1 == 0) goto L_0x0022
            r1 = r3
            goto L_0x0045
        L_0x002e:
            java.lang.String r1 = "set"
            boolean r1 = r13.equals(r1)
            if (r1 == 0) goto L_0x0022
            r1 = r5
            goto L_0x0045
        L_0x0039:
            java.lang.String r1 = "unplug"
            boolean r1 = r13.equals(r1)
            if (r1 == 0) goto L_0x0022
            r1 = r6
            goto L_0x0045
        L_0x0044:
            r1 = r4
        L_0x0045:
            r2 = 0
            java.lang.String r7 = "android.permission.DEVICE_POWER"
            if (r1 == 0) goto L_0x01c2
            if (r1 == r5) goto L_0x007d
            if (r1 == r3) goto L_0x0053
            int r1 = r12.handleDefaultCommands(r13)
            return r1
        L_0x0053:
            int r1 = r11.parseOptions(r12)
            android.content.Context r3 = r11.getContext()
            r3.enforceCallingOrSelfPermission(r7, r2)
            long r2 = android.os.Binder.clearCallingIdentity()
            boolean r4 = r11.mUpdatesStopped     // Catch:{ all -> 0x0078 }
            if (r4 == 0) goto L_0x0072
            r11.mUpdatesStopped = r6     // Catch:{ all -> 0x0078 }
            android.hardware.health.V1_0.HealthInfo r4 = r11.mHealthInfo     // Catch:{ all -> 0x0078 }
            android.hardware.health.V1_0.HealthInfo r5 = r11.mLastHealthInfo     // Catch:{ all -> 0x0078 }
            copy(r4, r5)     // Catch:{ all -> 0x0078 }
            r11.processValuesFromShellLocked(r0, r1)     // Catch:{ all -> 0x0078 }
        L_0x0072:
            android.os.Binder.restoreCallingIdentity(r2)
            goto L_0x01ee
        L_0x0078:
            r4 = move-exception
            android.os.Binder.restoreCallingIdentity(r2)
            throw r4
        L_0x007d:
            int r1 = r11.parseOptions(r12)
            android.content.Context r8 = r11.getContext()
            r8.enforceCallingOrSelfPermission(r7, r2)
            java.lang.String r2 = r12.getNextArg()
            if (r2 != 0) goto L_0x0094
            java.lang.String r3 = "No property specified"
            r0.println(r3)
            return r4
        L_0x0094:
            java.lang.String r7 = r12.getNextArg()
            if (r7 != 0) goto L_0x00a0
            java.lang.String r3 = "No value specified"
            r0.println(r3)
            return r4
        L_0x00a0:
            boolean r8 = r11.mUpdatesStopped     // Catch:{ NumberFormatException -> 0x01ac }
            if (r8 != 0) goto L_0x00ab
            android.hardware.health.V1_0.HealthInfo r8 = r11.mLastHealthInfo     // Catch:{ NumberFormatException -> 0x01ac }
            android.hardware.health.V1_0.HealthInfo r9 = r11.mHealthInfo     // Catch:{ NumberFormatException -> 0x01ac }
            copy(r8, r9)     // Catch:{ NumberFormatException -> 0x01ac }
        L_0x00ab:
            r8 = 1
            int r9 = r2.hashCode()     // Catch:{ NumberFormatException -> 0x01ac }
            switch(r9) {
                case -1000044642: goto L_0x010a;
                case -892481550: goto L_0x00ff;
                case -318277445: goto L_0x00f4;
                case 3106: goto L_0x00ea;
                case 116100: goto L_0x00e0;
                case 3556308: goto L_0x00d5;
                case 102865796: goto L_0x00ca;
                case 957830652: goto L_0x00c0;
                case 1959784951: goto L_0x00b4;
                default: goto L_0x00b3;
            }     // Catch:{ NumberFormatException -> 0x01ac }
        L_0x00b3:
            goto L_0x0115
        L_0x00b4:
            java.lang.String r3 = "invalid"
            boolean r3 = r2.equals(r3)     // Catch:{ NumberFormatException -> 0x01ac }
            if (r3 == 0) goto L_0x00b3
            r3 = 8
            goto L_0x0116
        L_0x00c0:
            java.lang.String r3 = "counter"
            boolean r3 = r2.equals(r3)     // Catch:{ NumberFormatException -> 0x01ac }
            if (r3 == 0) goto L_0x00b3
            r3 = 6
            goto L_0x0116
        L_0x00ca:
            java.lang.String r3 = "level"
            boolean r3 = r2.equals(r3)     // Catch:{ NumberFormatException -> 0x01ac }
            if (r3 == 0) goto L_0x00b3
            r3 = 5
            goto L_0x0116
        L_0x00d5:
            java.lang.String r3 = "temp"
            boolean r3 = r2.equals(r3)     // Catch:{ NumberFormatException -> 0x01ac }
            if (r3 == 0) goto L_0x00b3
            r3 = 7
            goto L_0x0116
        L_0x00e0:
            java.lang.String r9 = "usb"
            boolean r9 = r2.equals(r9)     // Catch:{ NumberFormatException -> 0x01ac }
            if (r9 == 0) goto L_0x00b3
            goto L_0x0116
        L_0x00ea:
            java.lang.String r3 = "ac"
            boolean r3 = r2.equals(r3)     // Catch:{ NumberFormatException -> 0x01ac }
            if (r3 == 0) goto L_0x00b3
            r3 = r5
            goto L_0x0116
        L_0x00f4:
            java.lang.String r3 = "present"
            boolean r3 = r2.equals(r3)     // Catch:{ NumberFormatException -> 0x01ac }
            if (r3 == 0) goto L_0x00b3
            r3 = r6
            goto L_0x0116
        L_0x00ff:
            java.lang.String r3 = "status"
            boolean r3 = r2.equals(r3)     // Catch:{ NumberFormatException -> 0x01ac }
            if (r3 == 0) goto L_0x00b3
            r3 = 4
            goto L_0x0116
        L_0x010a:
            java.lang.String r3 = "wireless"
            boolean r3 = r2.equals(r3)     // Catch:{ NumberFormatException -> 0x01ac }
            if (r3 == 0) goto L_0x00b3
            r3 = 3
            goto L_0x0116
        L_0x0115:
            r3 = r4
        L_0x0116:
            switch(r3) {
                case 0: goto L_0x0175;
                case 1: goto L_0x0167;
                case 2: goto L_0x0159;
                case 3: goto L_0x014b;
                case 4: goto L_0x0142;
                case 5: goto L_0x0139;
                case 6: goto L_0x012f;
                case 7: goto L_0x0125;
                case 8: goto L_0x011d;
                default: goto L_0x0119;
            }     // Catch:{ NumberFormatException -> 0x01ac }
        L_0x0119:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ NumberFormatException -> 0x01ac }
            goto L_0x0183
        L_0x011d:
            int r3 = java.lang.Integer.parseInt(r7)     // Catch:{ NumberFormatException -> 0x01ac }
            r11.mInvalidCharger = r3     // Catch:{ NumberFormatException -> 0x01ac }
            goto L_0x0196
        L_0x0125:
            android.hardware.health.V1_0.HealthInfo r3 = r11.mHealthInfo     // Catch:{ NumberFormatException -> 0x01ac }
            int r9 = java.lang.Integer.parseInt(r7)     // Catch:{ NumberFormatException -> 0x01ac }
            r3.batteryTemperature = r9     // Catch:{ NumberFormatException -> 0x01ac }
            goto L_0x0196
        L_0x012f:
            android.hardware.health.V1_0.HealthInfo r3 = r11.mHealthInfo     // Catch:{ NumberFormatException -> 0x01ac }
            int r9 = java.lang.Integer.parseInt(r7)     // Catch:{ NumberFormatException -> 0x01ac }
            r3.batteryChargeCounter = r9     // Catch:{ NumberFormatException -> 0x01ac }
            goto L_0x0196
        L_0x0139:
            android.hardware.health.V1_0.HealthInfo r3 = r11.mHealthInfo     // Catch:{ NumberFormatException -> 0x01ac }
            int r9 = java.lang.Integer.parseInt(r7)     // Catch:{ NumberFormatException -> 0x01ac }
            r3.batteryLevel = r9     // Catch:{ NumberFormatException -> 0x01ac }
            goto L_0x0196
        L_0x0142:
            android.hardware.health.V1_0.HealthInfo r3 = r11.mHealthInfo     // Catch:{ NumberFormatException -> 0x01ac }
            int r9 = java.lang.Integer.parseInt(r7)     // Catch:{ NumberFormatException -> 0x01ac }
            r3.batteryStatus = r9     // Catch:{ NumberFormatException -> 0x01ac }
            goto L_0x0196
        L_0x014b:
            android.hardware.health.V1_0.HealthInfo r3 = r11.mHealthInfo     // Catch:{ NumberFormatException -> 0x01ac }
            int r9 = java.lang.Integer.parseInt(r7)     // Catch:{ NumberFormatException -> 0x01ac }
            if (r9 == 0) goto L_0x0155
            r9 = r5
            goto L_0x0156
        L_0x0155:
            r9 = r6
        L_0x0156:
            r3.chargerWirelessOnline = r9     // Catch:{ NumberFormatException -> 0x01ac }
            goto L_0x0196
        L_0x0159:
            android.hardware.health.V1_0.HealthInfo r3 = r11.mHealthInfo     // Catch:{ NumberFormatException -> 0x01ac }
            int r9 = java.lang.Integer.parseInt(r7)     // Catch:{ NumberFormatException -> 0x01ac }
            if (r9 == 0) goto L_0x0163
            r9 = r5
            goto L_0x0164
        L_0x0163:
            r9 = r6
        L_0x0164:
            r3.chargerUsbOnline = r9     // Catch:{ NumberFormatException -> 0x01ac }
            goto L_0x0196
        L_0x0167:
            android.hardware.health.V1_0.HealthInfo r3 = r11.mHealthInfo     // Catch:{ NumberFormatException -> 0x01ac }
            int r9 = java.lang.Integer.parseInt(r7)     // Catch:{ NumberFormatException -> 0x01ac }
            if (r9 == 0) goto L_0x0171
            r9 = r5
            goto L_0x0172
        L_0x0171:
            r9 = r6
        L_0x0172:
            r3.chargerAcOnline = r9     // Catch:{ NumberFormatException -> 0x01ac }
            goto L_0x0196
        L_0x0175:
            android.hardware.health.V1_0.HealthInfo r3 = r11.mHealthInfo     // Catch:{ NumberFormatException -> 0x01ac }
            int r9 = java.lang.Integer.parseInt(r7)     // Catch:{ NumberFormatException -> 0x01ac }
            if (r9 == 0) goto L_0x017f
            r9 = r5
            goto L_0x0180
        L_0x017f:
            r9 = r6
        L_0x0180:
            r3.batteryPresent = r9     // Catch:{ NumberFormatException -> 0x01ac }
            goto L_0x0196
        L_0x0183:
            r3.<init>()     // Catch:{ NumberFormatException -> 0x01ac }
            java.lang.String r9 = "Unknown set option: "
            r3.append(r9)     // Catch:{ NumberFormatException -> 0x01ac }
            r3.append(r2)     // Catch:{ NumberFormatException -> 0x01ac }
            java.lang.String r3 = r3.toString()     // Catch:{ NumberFormatException -> 0x01ac }
            r0.println(r3)     // Catch:{ NumberFormatException -> 0x01ac }
            r8 = 0
        L_0x0196:
            if (r8 == 0) goto L_0x01aa
            long r9 = android.os.Binder.clearCallingIdentity()     // Catch:{ NumberFormatException -> 0x01ac }
            r11.mUpdatesStopped = r5     // Catch:{ all -> 0x01a5 }
            r11.processValuesFromShellLocked(r0, r1)     // Catch:{ all -> 0x01a5 }
            android.os.Binder.restoreCallingIdentity(r9)     // Catch:{ NumberFormatException -> 0x01ac }
            goto L_0x01aa
        L_0x01a5:
            r3 = move-exception
            android.os.Binder.restoreCallingIdentity(r9)     // Catch:{ NumberFormatException -> 0x01ac }
            throw r3     // Catch:{ NumberFormatException -> 0x01ac }
        L_0x01aa:
            goto L_0x01ee
        L_0x01ac:
            r3 = move-exception
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "Bad value: "
            r5.append(r6)
            r5.append(r7)
            java.lang.String r5 = r5.toString()
            r0.println(r5)
            return r4
        L_0x01c2:
            int r1 = r11.parseOptions(r12)
            android.content.Context r3 = r11.getContext()
            r3.enforceCallingOrSelfPermission(r7, r2)
            boolean r2 = r11.mUpdatesStopped
            if (r2 != 0) goto L_0x01d8
            android.hardware.health.V1_0.HealthInfo r2 = r11.mLastHealthInfo
            android.hardware.health.V1_0.HealthInfo r3 = r11.mHealthInfo
            copy(r2, r3)
        L_0x01d8:
            android.hardware.health.V1_0.HealthInfo r2 = r11.mHealthInfo
            r2.chargerAcOnline = r6
            r2.chargerUsbOnline = r6
            r2.chargerWirelessOnline = r6
            long r2 = android.os.Binder.clearCallingIdentity()
            r11.mUpdatesStopped = r5     // Catch:{ all -> 0x01ef }
            r11.processValuesFromShellLocked(r0, r1)     // Catch:{ all -> 0x01ef }
            android.os.Binder.restoreCallingIdentity(r2)
        L_0x01ee:
            return r6
        L_0x01ef:
            r4 = move-exception
            android.os.Binder.restoreCallingIdentity(r2)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.BatteryService.onShellCommand(com.android.server.BatteryService$Shell, java.lang.String):int");
    }

    private void processValuesFromShellLocked(PrintWriter pw, int opts) {
        processValuesLocked((opts & 1) != 0);
        if ((opts & 1) != 0) {
            pw.println(this.mSequence);
        }
    }

    /* access modifiers changed from: private */
    public void dumpInternal(FileDescriptor fd, PrintWriter pw, String[] args) {
        synchronized (this.mLock) {
            if (args != null) {
                if (args.length != 0) {
                    if (!"-a".equals(args[0])) {
                        new Shell().exec(this.mBinderService, (FileDescriptor) null, fd, (FileDescriptor) null, args, (ShellCallback) null, new ResultReceiver((Handler) null));
                    }
                }
            }
            pw.println("Current Battery Service state:");
            if (this.mUpdatesStopped) {
                pw.println("  (UPDATES STOPPED -- use 'reset' to restart)");
            }
            pw.println("  AC powered: " + this.mHealthInfo.chargerAcOnline);
            pw.println("  USB powered: " + this.mHealthInfo.chargerUsbOnline);
            pw.println("  Wireless powered: " + this.mHealthInfo.chargerWirelessOnline);
            pw.println("  Max charging current: " + this.mHealthInfo.maxChargingCurrent);
            pw.println("  Max charging voltage: " + this.mHealthInfo.maxChargingVoltage);
            pw.println("  Charge counter: " + this.mHealthInfo.batteryChargeCounter);
            pw.println("  status: " + this.mHealthInfo.batteryStatus);
            pw.println("  health: " + this.mHealthInfo.batteryHealth);
            pw.println("  present: " + this.mHealthInfo.batteryPresent);
            pw.println("  level: " + this.mHealthInfo.batteryLevel);
            pw.println("  scale: 100");
            pw.println("  voltage: " + this.mHealthInfo.batteryVoltage);
            pw.println("  temperature: " + this.mHealthInfo.batteryTemperature);
            pw.println("  technology: " + this.mHealthInfo.batteryTechnology);
        }
    }

    /* access modifiers changed from: private */
    public void dumpProto(FileDescriptor fd) {
        ProtoOutputStream proto = new ProtoOutputStream(fd);
        synchronized (this.mLock) {
            proto.write(1133871366145L, this.mUpdatesStopped);
            int batteryPluggedValue = 0;
            if (this.mHealthInfo.chargerAcOnline) {
                batteryPluggedValue = 1;
            } else if (this.mHealthInfo.chargerUsbOnline) {
                batteryPluggedValue = 2;
            } else if (this.mHealthInfo.chargerWirelessOnline) {
                batteryPluggedValue = 4;
            }
            proto.write(1159641169922L, batteryPluggedValue);
            proto.write(1120986464259L, this.mHealthInfo.maxChargingCurrent);
            proto.write(1120986464260L, this.mHealthInfo.maxChargingVoltage);
            proto.write(1120986464261L, this.mHealthInfo.batteryChargeCounter);
            proto.write(1159641169926L, this.mHealthInfo.batteryStatus);
            proto.write(1159641169927L, this.mHealthInfo.batteryHealth);
            proto.write(1133871366152L, this.mHealthInfo.batteryPresent);
            proto.write(1120986464265L, this.mHealthInfo.batteryLevel);
            proto.write(1120986464266L, 100);
            proto.write(1120986464267L, this.mHealthInfo.batteryVoltage);
            proto.write(1120986464268L, this.mHealthInfo.batteryTemperature);
            proto.write(1138166333453L, this.mHealthInfo.batteryTechnology);
        }
        proto.flush();
    }

    /* access modifiers changed from: private */
    public static void traceBegin(String name) {
        Trace.traceBegin(524288, name);
    }

    /* access modifiers changed from: private */
    public static void traceEnd() {
        Trace.traceEnd(524288);
    }

    private final class Led {
        private final int mBatteryFullARGB;
        private final int mBatteryLedOff;
        private final int mBatteryLedOn;
        private final Light mBatteryLight;
        private final int mBatteryLowARGB;
        private final int mBatteryMediumARGB;

        public Led(Context context, LightsManager lights) {
            this.mBatteryLight = lights.getLight(3);
            this.mBatteryLowARGB = context.getResources().getInteger(17694867);
            this.mBatteryMediumARGB = context.getResources().getInteger(17694868);
            this.mBatteryFullARGB = context.getResources().getInteger(17694864);
            this.mBatteryLedOn = context.getResources().getInteger(17694866);
            this.mBatteryLedOff = context.getResources().getInteger(17694865);
        }

        public void updateLightsLocked() {
            int level = BatteryService.this.mHealthInfo.batteryLevel;
            int status = BatteryService.this.mHealthInfo.batteryStatus;
            if (level < BatteryService.this.mLowBatteryWarningLevel) {
                if (status == 2) {
                    this.mBatteryLight.setColor(this.mBatteryLowARGB);
                } else {
                    this.mBatteryLight.setFlashing(this.mBatteryLowARGB, 1, this.mBatteryLedOn, this.mBatteryLedOff);
                }
            } else if (status != 2 && status != 5) {
                this.mBatteryLight.turnOff();
            } else if (status == 5 || level >= 90) {
                this.mBatteryLight.setColor(this.mBatteryFullARGB);
            } else {
                this.mBatteryLight.setColor(this.mBatteryMediumARGB);
            }
        }
    }

    private final class HealthHalCallback extends IHealthInfoCallback.Stub implements HealthServiceWrapper.Callback {
        private HealthHalCallback() {
        }

        public void healthInfoChanged(android.hardware.health.V2_0.HealthInfo props) {
            BatteryService.this.update(props);
        }

        public void onRegistration(IHealth oldService, IHealth newService, String instance) {
            if (newService != null) {
                BatteryService.traceBegin("HealthUnregisterCallback");
                if (oldService != null) {
                    try {
                        int r = oldService.unregisterCallback(this);
                        if (r != 0) {
                            String access$200 = BatteryService.TAG;
                            Slog.w(access$200, "health: cannot unregister previous callback: " + Result.toString(r));
                        }
                    } catch (RemoteException ex) {
                        String access$2002 = BatteryService.TAG;
                        Slog.w(access$2002, "health: cannot unregister previous callback (transaction error): " + ex.getMessage());
                    } catch (Throwable th) {
                        BatteryService.traceEnd();
                        throw th;
                    }
                }
                BatteryService.traceEnd();
                BatteryService.traceBegin("HealthRegisterCallback");
                try {
                    int r2 = newService.registerCallback(this);
                    if (r2 != 0) {
                        String access$2003 = BatteryService.TAG;
                        Slog.w(access$2003, "health: cannot register callback: " + Result.toString(r2));
                        BatteryService.traceEnd();
                        return;
                    }
                    newService.update();
                    BatteryService.traceEnd();
                } catch (RemoteException ex2) {
                    String access$2004 = BatteryService.TAG;
                    Slog.e(access$2004, "health: cannot register callback (transaction error): " + ex2.getMessage());
                } catch (Throwable th2) {
                    BatteryService.traceEnd();
                    throw th2;
                }
            }
        }
    }

    private final class BinderService extends Binder {
        private BinderService() {
        }

        /* access modifiers changed from: protected */
        public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
            if (DumpUtils.checkDumpPermission(BatteryService.this.mContext, BatteryService.TAG, pw)) {
                if (args.length <= 0 || !PriorityDump.PROTO_ARG.equals(args[0])) {
                    BatteryService.this.dumpInternal(fd, pw, args);
                } else {
                    BatteryService.this.dumpProto(fd);
                }
            }
        }

        public void onShellCommand(FileDescriptor in, FileDescriptor out, FileDescriptor err, String[] args, ShellCallback callback, ResultReceiver resultReceiver) {
            new Shell().exec(this, in, out, err, args, callback, resultReceiver);
        }
    }

    private final class BatteryPropertiesRegistrar extends IBatteryPropertiesRegistrar.Stub {
        private BatteryPropertiesRegistrar() {
        }

        /* Debug info: failed to restart local var, previous not found, register: 3 */
        public int getProperty(int id, BatteryProperty prop) throws RemoteException {
            BatteryService.traceBegin("HealthGetProperty");
            try {
                IHealth service = BatteryService.this.mHealthServiceWrapper.getLastService();
                if (service != null) {
                    MutableInt outResult = new MutableInt(1);
                    switch (id) {
                        case 1:
                            service.getChargeCounter(new IHealth.getChargeCounterCallback(outResult, prop) {
                                private final /* synthetic */ MutableInt f$0;
                                private final /* synthetic */ BatteryProperty f$1;

                                {
                                    this.f$0 = r1;
                                    this.f$1 = r2;
                                }

                                public final void onValues(int i, int i2) {
                                    BatteryService.BatteryPropertiesRegistrar.lambda$getProperty$0(this.f$0, this.f$1, i, i2);
                                }
                            });
                            break;
                        case 2:
                            service.getCurrentNow(new IHealth.getCurrentNowCallback(outResult, prop) {
                                private final /* synthetic */ MutableInt f$0;
                                private final /* synthetic */ BatteryProperty f$1;

                                {
                                    this.f$0 = r1;
                                    this.f$1 = r2;
                                }

                                public final void onValues(int i, int i2) {
                                    BatteryService.BatteryPropertiesRegistrar.lambda$getProperty$1(this.f$0, this.f$1, i, i2);
                                }
                            });
                            break;
                        case 3:
                            service.getCurrentAverage(new IHealth.getCurrentAverageCallback(outResult, prop) {
                                private final /* synthetic */ MutableInt f$0;
                                private final /* synthetic */ BatteryProperty f$1;

                                {
                                    this.f$0 = r1;
                                    this.f$1 = r2;
                                }

                                public final void onValues(int i, int i2) {
                                    BatteryService.BatteryPropertiesRegistrar.lambda$getProperty$2(this.f$0, this.f$1, i, i2);
                                }
                            });
                            break;
                        case 4:
                            service.getCapacity(new IHealth.getCapacityCallback(outResult, prop) {
                                private final /* synthetic */ MutableInt f$0;
                                private final /* synthetic */ BatteryProperty f$1;

                                {
                                    this.f$0 = r1;
                                    this.f$1 = r2;
                                }

                                public final void onValues(int i, int i2) {
                                    BatteryService.BatteryPropertiesRegistrar.lambda$getProperty$3(this.f$0, this.f$1, i, i2);
                                }
                            });
                            break;
                        case 5:
                            service.getEnergyCounter(new IHealth.getEnergyCounterCallback(outResult, prop) {
                                private final /* synthetic */ MutableInt f$0;
                                private final /* synthetic */ BatteryProperty f$1;

                                {
                                    this.f$0 = r1;
                                    this.f$1 = r2;
                                }

                                public final void onValues(int i, long j) {
                                    BatteryService.BatteryPropertiesRegistrar.lambda$getProperty$5(this.f$0, this.f$1, i, j);
                                }
                            });
                            break;
                        case 6:
                            service.getChargeStatus(new IHealth.getChargeStatusCallback(outResult, prop) {
                                private final /* synthetic */ MutableInt f$0;
                                private final /* synthetic */ BatteryProperty f$1;

                                {
                                    this.f$0 = r1;
                                    this.f$1 = r2;
                                }

                                public final void onValues(int i, int i2) {
                                    BatteryService.BatteryPropertiesRegistrar.lambda$getProperty$4(this.f$0, this.f$1, i, i2);
                                }
                            });
                            break;
                    }
                    return outResult.value;
                }
                throw new RemoteException("no health service");
            } finally {
                BatteryService.traceEnd();
            }
        }

        static /* synthetic */ void lambda$getProperty$0(MutableInt outResult, BatteryProperty prop, int result, int value) {
            outResult.value = result;
            if (result == 0) {
                prop.setLong((long) value);
            }
        }

        static /* synthetic */ void lambda$getProperty$1(MutableInt outResult, BatteryProperty prop, int result, int value) {
            outResult.value = result;
            if (result == 0) {
                prop.setLong((long) value);
            }
        }

        static /* synthetic */ void lambda$getProperty$2(MutableInt outResult, BatteryProperty prop, int result, int value) {
            outResult.value = result;
            if (result == 0) {
                prop.setLong((long) value);
            }
        }

        static /* synthetic */ void lambda$getProperty$3(MutableInt outResult, BatteryProperty prop, int result, int value) {
            outResult.value = result;
            if (result == 0) {
                prop.setLong((long) value);
            }
        }

        static /* synthetic */ void lambda$getProperty$4(MutableInt outResult, BatteryProperty prop, int result, int value) {
            outResult.value = result;
            if (result == 0) {
                prop.setLong((long) value);
            }
        }

        static /* synthetic */ void lambda$getProperty$5(MutableInt outResult, BatteryProperty prop, int result, long value) {
            outResult.value = result;
            if (result == 0) {
                prop.setLong(value);
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 3 */
        public void scheduleUpdate() throws RemoteException {
            BatteryService.traceBegin("HealthScheduleUpdate");
            try {
                IHealth service = BatteryService.this.mHealthServiceWrapper.getLastService();
                if (service != null) {
                    service.update();
                    return;
                }
                throw new RemoteException("no health service");
            } finally {
                BatteryService.traceEnd();
            }
        }
    }

    private final class LocalService extends BatteryManagerInternal {
        private LocalService() {
        }

        public boolean isPowered(int plugTypeSet) {
            boolean access$2300;
            synchronized (BatteryService.this.mLock) {
                access$2300 = BatteryService.this.isPoweredLocked(plugTypeSet);
            }
            return access$2300;
        }

        public int getPlugType() {
            int access$2400;
            synchronized (BatteryService.this.mLock) {
                access$2400 = BatteryService.this.mPlugType;
            }
            return access$2400;
        }

        public int getBatteryLevel() {
            int i;
            synchronized (BatteryService.this.mLock) {
                i = BatteryService.this.mHealthInfo.batteryLevel;
            }
            return i;
        }

        public int getBatteryChargeCounter() {
            int i;
            synchronized (BatteryService.this.mLock) {
                i = BatteryService.this.mHealthInfo.batteryChargeCounter;
            }
            return i;
        }

        public int getBatteryFullCharge() {
            int i;
            synchronized (BatteryService.this.mLock) {
                i = BatteryService.this.mHealthInfo.batteryFullCharge;
            }
            return i;
        }

        public boolean getBatteryLevelLow() {
            boolean access$2500;
            synchronized (BatteryService.this.mLock) {
                access$2500 = BatteryService.this.mBatteryLevelLow;
            }
            return access$2500;
        }

        public int getInvalidCharger() {
            int access$700;
            synchronized (BatteryService.this.mLock) {
                access$700 = BatteryService.this.mInvalidCharger;
            }
            return access$700;
        }
    }

    @VisibleForTesting
    static final class HealthServiceWrapper {
        public static final String INSTANCE_HEALTHD = "backup";
        public static final String INSTANCE_VENDOR = "default";
        private static final String TAG = "HealthServiceWrapper";
        private static final List<String> sAllInstances = Arrays.asList(new String[]{INSTANCE_VENDOR, INSTANCE_HEALTHD});
        /* access modifiers changed from: private */
        public Callback mCallback;
        /* access modifiers changed from: private */
        public final HandlerThread mHandlerThread = new HandlerThread("HealthServiceRefresh");
        /* access modifiers changed from: private */
        public IHealthSupplier mHealthSupplier;
        /* access modifiers changed from: private */
        public String mInstanceName;
        /* access modifiers changed from: private */
        public final AtomicReference<IHealth> mLastService = new AtomicReference<>();
        private final IServiceNotification mNotification = new Notification();

        interface Callback {
            void onRegistration(IHealth iHealth, IHealth iHealth2, String str);
        }

        HealthServiceWrapper() {
        }

        /* access modifiers changed from: package-private */
        public IHealth getLastService() {
            return this.mLastService.get();
        }

        /* JADX INFO: finally extract failed */
        /* access modifiers changed from: package-private */
        public void init(Callback callback, IServiceManagerSupplier managerSupplier, IHealthSupplier healthSupplier) throws RemoteException, NoSuchElementException, NullPointerException {
            if (callback == null || managerSupplier == null || healthSupplier == null) {
                throw new NullPointerException();
            }
            this.mCallback = callback;
            this.mHealthSupplier = healthSupplier;
            IHealth newService = null;
            Iterator<String> it = sAllInstances.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                String name = it.next();
                BatteryService.traceBegin("HealthInitGetService_" + name);
                try {
                    newService = healthSupplier.get(name);
                } catch (NoSuchElementException e) {
                } catch (Throwable th) {
                    BatteryService.traceEnd();
                    throw th;
                }
                BatteryService.traceEnd();
                if (newService != null) {
                    this.mInstanceName = name;
                    this.mLastService.set(newService);
                    break;
                }
            }
            String str = this.mInstanceName;
            if (str == null || newService == null) {
                throw new NoSuchElementException(String.format("No IHealth service instance among %s is available. Perhaps no permission?", new Object[]{sAllInstances.toString()}));
            }
            this.mCallback.onRegistration((IHealth) null, newService, str);
            BatteryService.traceBegin("HealthInitRegisterNotification");
            this.mHandlerThread.start();
            try {
                managerSupplier.get().registerForNotifications(IHealth.kInterfaceName, this.mInstanceName, this.mNotification);
                BatteryService.traceEnd();
                Slog.i(TAG, "health: HealthServiceWrapper listening to instance " + this.mInstanceName);
            } catch (Throwable th2) {
                BatteryService.traceEnd();
                throw th2;
            }
        }

        /* access modifiers changed from: package-private */
        @VisibleForTesting
        public HandlerThread getHandlerThread() {
            return this.mHandlerThread;
        }

        interface IServiceManagerSupplier {
            IServiceManager get() throws NoSuchElementException, RemoteException {
                return IServiceManager.getService();
            }
        }

        interface IHealthSupplier {
            IHealth get(String name) throws NoSuchElementException, RemoteException {
                return IHealth.getService(name, true);
            }
        }

        private class Notification extends IServiceNotification.Stub {
            private Notification() {
            }

            public final void onRegistration(String interfaceName, String instanceName, boolean preexisting) {
                if (IHealth.kInterfaceName.equals(interfaceName) && HealthServiceWrapper.this.mInstanceName.equals(instanceName)) {
                    HealthServiceWrapper.this.mHandlerThread.getThreadHandler().post(new Runnable() {
                        public void run() {
                            try {
                                IHealth newService = HealthServiceWrapper.this.mHealthSupplier.get(HealthServiceWrapper.this.mInstanceName);
                                IHealth oldService = (IHealth) HealthServiceWrapper.this.mLastService.getAndSet(newService);
                                if (!Objects.equals(newService, oldService)) {
                                    Slog.i(HealthServiceWrapper.TAG, "health: new instance registered " + HealthServiceWrapper.this.mInstanceName);
                                    HealthServiceWrapper.this.mCallback.onRegistration(oldService, newService, HealthServiceWrapper.this.mInstanceName);
                                }
                            } catch (RemoteException | NoSuchElementException ex) {
                                Slog.e(HealthServiceWrapper.TAG, "health: Cannot get instance '" + HealthServiceWrapper.this.mInstanceName + "': " + ex.getMessage() + ". Perhaps no permission?");
                            }
                        }
                    });
                }
            }
        }
    }
}
