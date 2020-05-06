package com.android.server;

import android.app.ActivityThread;
import android.app.usage.UsageStatsManagerInternal;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteCompatibilityWalFlags;
import android.database.sqlite.SQLiteGlobal;
import android.hardware.display.DisplayManagerInternal;
import android.os.BaseBundle;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.FactoryTest;
import android.os.FileUtils;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.Process;
import android.os.StrictMode;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.DeviceConfig;
import android.sysprop.VoldProperties;
import android.text.TextUtils;
import android.util.EventLog;
import android.util.Slog;
import android.util.TimingsTraceLog;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.os.BinderInternal;
import com.android.internal.os.ZygoteInit;
import com.android.internal.util.ConcurrentUtils;
import com.android.server.BatteryService;
import com.android.server.BinderCallsStatsService;
import com.android.server.LooperStatsService;
import com.android.server.am.ActivityManagerService;
import com.android.server.attention.AttentionManagerService;
import com.android.server.contentcapture.ContentCaptureManagerInternal;
import com.android.server.display.DisplayManagerService;
import com.android.server.gpu.GpuService;
import com.android.server.lights.MiuiLightsService;
import com.android.server.om.OverlayManagerService;
import com.android.server.os.BugreportManagerService;
import com.android.server.os.DeviceIdentifiersPolicyService;
import com.android.server.pm.Installer;
import com.android.server.pm.OtaDexoptService;
import com.android.server.pm.PackageManagerService;
import com.android.server.pm.UserManagerService;
import com.android.server.power.PowerManagerService;
import com.android.server.power.ShutdownThread;
import com.android.server.power.ThermalManagerService;
import com.android.server.rollback.RollbackManagerService;
import com.android.server.uri.UriGrantsManagerService;
import com.android.server.usage.UsageStatsService;
import com.android.server.webkit.WebViewUpdateService;
import com.android.server.wm.ActivityTaskManagerService;
import com.android.server.wm.WindowManagerGlobalLock;
import com.android.server.wm.WindowManagerService;
import com.miui.server.AccessController;
import dalvik.system.VMRuntime;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Timer;
import java.util.concurrent.Future;

public final class SystemServer {
    private static final String ACCESSIBILITY_MANAGER_SERVICE_CLASS = "com.android.server.accessibility.AccessibilityManagerService$Lifecycle";
    private static final String ACCOUNT_SERVICE_CLASS = "com.android.server.accounts.AccountManagerService$Lifecycle";
    private static final String ADB_SERVICE_CLASS = "com.android.server.adb.AdbService$Lifecycle";
    private static final String APPWIDGET_SERVICE_CLASS = "com.android.server.appwidget.AppWidgetService";
    private static final String APP_PREDICTION_MANAGER_SERVICE_CLASS = "com.android.server.appprediction.AppPredictionManagerService";
    private static final String AUTO_FILL_MANAGER_SERVICE_CLASS = "com.android.server.autofill.AutofillManagerService";
    private static final String BACKUP_MANAGER_SERVICE_CLASS = "com.android.server.backup.BackupManagerService$Lifecycle";
    private static final String BLOCK_MAP_FILE = "/cache/recovery/block.map";
    private static final TimingsTraceLog BOOT_TIMINGS_TRACE_LOG = new TimingsTraceLog(SYSTEM_SERVER_TIMING_TAG, 524288);
    private static final String CAR_SERVICE_HELPER_SERVICE_CLASS = "com.android.internal.car.CarServiceHelperService";
    private static final String COMPANION_DEVICE_MANAGER_SERVICE_CLASS = "com.android.server.companion.CompanionDeviceManagerService";
    private static final String CONTENT_CAPTURE_MANAGER_SERVICE_CLASS = "com.android.server.contentcapture.ContentCaptureManagerService";
    private static final String CONTENT_SERVICE_CLASS = "com.android.server.content.ContentService$Lifecycle";
    private static final String CONTENT_SUGGESTIONS_SERVICE_CLASS = "com.android.server.contentsuggestions.ContentSuggestionsManagerService";
    private static final int DEFAULT_SYSTEM_THEME = 16974847;
    private static final long EARLIEST_SUPPORTED_TIME = 86400000;
    private static final String ENCRYPTED_STATE = "1";
    private static final String ENCRYPTING_STATE = "trigger_restart_min_framework";
    private static final String ETHERNET_SERVICE_CLASS = "com.android.server.ethernet.EthernetService";
    private static final String GSI_RUNNING_PROP = "ro.gsid.image_running";
    private static final String IOT_SERVICE_CLASS = "com.android.things.server.IoTSystemService";
    private static final String JOB_SCHEDULER_SERVICE_CLASS = "com.android.server.job.JobSchedulerService";
    private static final String LOCK_SETTINGS_SERVICE_CLASS = "com.android.server.locksettings.LockSettingsService$Lifecycle";
    private static final String LOWPAN_SERVICE_CLASS = "com.android.server.lowpan.LowpanService";
    private static final String MIDI_SERVICE_CLASS = "com.android.server.midi.MidiService$Lifecycle";
    private static final String PERSISTENT_DATA_BLOCK_PROP = "ro.frp.pst";
    private static final String PRINT_MANAGER_SERVICE_CLASS = "com.android.server.print.PrintManagerService";
    private static final String SEARCH_MANAGER_SERVICE_CLASS = "com.android.server.search.SearchManagerService$Lifecycle";
    private static final String SLICE_MANAGER_SERVICE_CLASS = "com.android.server.slice.SliceManagerService$Lifecycle";
    private static final long SLOW_DELIVERY_THRESHOLD_MS = 200;
    private static final long SLOW_DISPATCH_THRESHOLD_MS = 100;
    private static final long SNAPSHOT_INTERVAL = 3600000;
    private static final String START_HIDL_SERVICES = "StartHidlServices";
    private static final String START_SENSOR_SERVICE = "StartSensorService";
    private static final String STORAGE_MANAGER_SERVICE_CLASS = "com.android.server.StorageManagerService$Lifecycle";
    private static final String STORAGE_STATS_SERVICE_CLASS = "com.android.server.usage.StorageStatsService$Lifecycle";
    private static final String SYSPROP_START_COUNT = "sys.system_server.start_count";
    private static final String SYSPROP_START_ELAPSED = "sys.system_server.start_elapsed";
    private static final String SYSPROP_START_UPTIME = "sys.system_server.start_uptime";
    private static final String SYSTEM_CAPTIONS_MANAGER_SERVICE_CLASS = "com.android.server.systemcaptions.SystemCaptionsManagerService";
    private static final String SYSTEM_SERVER_TIMING_ASYNC_TAG = "SystemServerTimingAsync";
    private static final String SYSTEM_SERVER_TIMING_TAG = "SystemServerTiming";
    private static final String TAG = "SystemServer";
    private static final String THERMAL_OBSERVER_CLASS = "com.google.android.clockwork.ThermalObserver";
    private static final String TIME_DETECTOR_SERVICE_CLASS = "com.android.server.timedetector.TimeDetectorService$Lifecycle";
    private static final String TIME_ZONE_RULES_MANAGER_SERVICE_CLASS = "com.android.server.timezone.RulesManagerService$Lifecycle";
    private static final String UNCRYPT_PACKAGE_FILE = "/cache/recovery/uncrypt_file";
    private static final String USB_SERVICE_CLASS = "com.android.server.usb.UsbService$Lifecycle";
    private static final String VOICE_RECOGNITION_MANAGER_SERVICE_CLASS = "com.android.server.voiceinteraction.VoiceInteractionManagerService";
    private static final String WALLPAPER_SERVICE_CLASS = "com.android.server.wallpaper.WallpaperManagerService$Lifecycle";
    private static final String WEAR_CONNECTIVITY_SERVICE_CLASS = "com.android.clockwork.connectivity.WearConnectivityService";
    private static final String WEAR_DISPLAY_SERVICE_CLASS = "com.google.android.clockwork.display.WearDisplayService";
    private static final String WEAR_GLOBAL_ACTIONS_SERVICE_CLASS = "com.android.clockwork.globalactions.GlobalActionsService";
    private static final String WEAR_LEFTY_SERVICE_CLASS = "com.google.android.clockwork.lefty.WearLeftyService";
    private static final String WEAR_POWER_SERVICE_CLASS = "com.android.clockwork.power.WearPowerService";
    private static final String WEAR_SIDEKICK_SERVICE_CLASS = "com.google.android.clockwork.sidekick.SidekickService";
    private static final String WEAR_TIME_SERVICE_CLASS = "com.google.android.clockwork.time.WearTimeService";
    private static final String WIFI_AWARE_SERVICE_CLASS = "com.android.server.wifi.aware.WifiAwareService";
    private static final String WIFI_P2P_SERVICE_CLASS = "com.android.server.wifi.p2p.WifiP2pService";
    private static final String WIFI_SERVICE_CLASS = "com.android.server.wifi.WifiService";
    private static final int sMaxBinderThreads = 31;
    private ActivityManagerService mActivityManagerService;
    private ContentResolver mContentResolver;
    private DisplayManagerService mDisplayManagerService;
    private EntropyMixer mEntropyMixer;
    private final int mFactoryTestMode = FactoryTest.getMode();
    private boolean mFirstBoot;
    private boolean mOnlyCore;
    private PackageManager mPackageManager;
    private PackageManagerService mPackageManagerService;
    private PowerManagerService mPowerManagerService;
    private Timer mProfilerSnapshotTimer;
    private final boolean mRuntimeRestart = "1".equals(SystemProperties.get("sys.boot_completed"));
    private final long mRuntimeStartElapsedTime = SystemClock.elapsedRealtime();
    private final long mRuntimeStartUptime = SystemClock.uptimeMillis();
    private Future<?> mSensorServiceStart;
    private final int mStartCount = (SystemProperties.getInt(SYSPROP_START_COUNT, 0) + 1);
    private Context mSystemContext;
    private SystemServiceManager mSystemServiceManager;
    private WebViewUpdateService mWebViewUpdateService;
    private WindowManagerGlobalLock mWindowManagerGlobalLock;
    private Future<?> mZygotePreload;

    private static native void initZygoteChildHeapProfiling();

    private static native void startHidlServices();

    private static native void startSensorService();

    public static void main(String[] args) {
        SystemServerInjector.markSystemRun(ZygoteInit.BOOT_START_TIME);
        new SystemServer().run();
    }

    /* Debug info: failed to restart local var, previous not found, register: 13 */
    /* JADX INFO: finally extract failed */
    private void run() {
        try {
            traceBeginAndSlog("InitBeforeStartServices");
            SystemProperties.set(SYSPROP_START_COUNT, String.valueOf(this.mStartCount));
            SystemProperties.set(SYSPROP_START_ELAPSED, String.valueOf(this.mRuntimeStartElapsedTime));
            SystemProperties.set(SYSPROP_START_UPTIME, String.valueOf(this.mRuntimeStartUptime));
            EventLog.writeEvent(EventLogTags.SYSTEM_SERVER_START, new Object[]{Integer.valueOf(this.mStartCount), Long.valueOf(this.mRuntimeStartUptime), Long.valueOf(this.mRuntimeStartElapsedTime)});
            if (System.currentTimeMillis() < 86400000) {
                Slog.w(TAG, "System clock is before 1970; setting to 1970.");
                SystemClock.setCurrentTimeMillis(86400000);
            }
            String timezoneProperty = SystemProperties.get("persist.sys.timezone");
            if (timezoneProperty == null || timezoneProperty.isEmpty()) {
                Slog.w(TAG, "Timezone not set; setting to GMT.");
                SystemProperties.set("persist.sys.timezone", "GMT");
            }
            if (!SystemProperties.get("persist.sys.language").isEmpty()) {
                SystemProperties.set("persist.sys.locale", Locale.getDefault().toLanguageTag());
                SystemProperties.set("persist.sys.language", "");
                SystemProperties.set("persist.sys.country", "");
                SystemProperties.set("persist.sys.localevar", "");
            }
            Binder.setWarnOnBlocking(true);
            PackageItemInfo.forceSafeLabels();
            SQLiteGlobal.sDefaultSyncMode = "FULL";
            SQLiteCompatibilityWalFlags.init((String) null);
            Slog.i(TAG, "Entered the Android system server!");
            int uptimeMillis = (int) SystemClock.elapsedRealtime();
            EventLog.writeEvent(EventLogTags.BOOT_PROGRESS_SYSTEM_RUN, uptimeMillis);
            if (!this.mRuntimeRestart) {
                MetricsLogger.histogram((Context) null, "boot_system_server_init", uptimeMillis);
            }
            SystemProperties.set("persist.sys.dalvik.vm.lib.2", VMRuntime.getRuntime().vmLibrary());
            VMRuntime.getRuntime().clearGrowthLimit();
            VMRuntime.getRuntime().setTargetHeapUtilization(0.8f);
            Build.ensureFingerprintProperty();
            Environment.setUserRequired(true);
            BaseBundle.setShouldDefuse(true);
            Parcel.setStackTraceParceling(true);
            BinderInternal.disableBackgroundScheduling(true);
            BinderInternal.setMaxThreads(31);
            Process.setThreadPriority(-2);
            Process.setCanSelfBackground(false);
            Looper.prepareMainLooper();
            Looper.getMainLooper().setSlowLogThresholdMs(SLOW_DISPATCH_THRESHOLD_MS, SLOW_DELIVERY_THRESHOLD_MS);
            System.loadLibrary("android_servers");
            if (Build.IS_DEBUGGABLE) {
                initZygoteChildHeapProfiling();
            }
            performPendingShutdown();
            createSystemContext();
            this.mSystemServiceManager = new SystemServiceManager(this.mSystemContext);
            this.mSystemServiceManager.setStartInfo(this.mRuntimeRestart, this.mRuntimeStartElapsedTime, this.mRuntimeStartUptime);
            LocalServices.addService(SystemServiceManager.class, this.mSystemServiceManager);
            SystemServerInitThreadPool.get();
            traceEnd();
            try {
                traceBeginAndSlog("StartServices");
                startBootstrapServices();
                startCoreServices();
                startOtherServices();
                SystemServerInitThreadPool.shutdown();
                traceEnd();
                StrictMode.initVmDefaults((ApplicationInfo) null);
                if (!this.mRuntimeRestart && !isFirstBootOrUpgrade()) {
                    int uptimeMillis2 = (int) SystemClock.elapsedRealtime();
                    MetricsLogger.histogram((Context) null, "boot_system_server_ready", uptimeMillis2);
                    if (uptimeMillis2 > 60000) {
                        Slog.wtf(SYSTEM_SERVER_TIMING_TAG, "SystemServer init took too long. uptimeMillis=" + uptimeMillis2);
                    }
                }
                if (VMRuntime.hasBootImageSpaces() == 0) {
                    Slog.wtf(TAG, "Runtime is not running with a boot image!");
                }
                Slog.i(TAG, "Entered the Android system server main thread loop.");
                Looper.loop();
                throw new RuntimeException("Main thread loop unexpectedly exited");
            } catch (Throwable th) {
                traceEnd();
                throw th;
            }
        } catch (Throwable th2) {
            traceEnd();
            throw th2;
        }
    }

    private boolean isFirstBootOrUpgrade() {
        return this.mPackageManagerService.isFirstBoot() || this.mPackageManagerService.isDeviceUpgrading();
    }

    private void reportWtf(String msg, Throwable e) {
        Slog.w(TAG, "***********************************************");
        Slog.wtf(TAG, "BOOT FAILURE " + msg, e);
    }

    private void performPendingShutdown() {
        final String reason;
        String shutdownAction = SystemProperties.get(ShutdownThread.SHUTDOWN_ACTION_PROPERTY, "");
        if (shutdownAction != null && shutdownAction.length() > 0) {
            final boolean reboot = shutdownAction.charAt(0) == '1';
            if (shutdownAction.length() > 1) {
                reason = shutdownAction.substring(1, shutdownAction.length());
            } else {
                reason = null;
            }
            if (reason != null && reason.startsWith("recovery-update")) {
                File packageFile = new File(UNCRYPT_PACKAGE_FILE);
                if (packageFile.exists()) {
                    String filename = null;
                    try {
                        filename = FileUtils.readTextFile(packageFile, 0, (String) null);
                    } catch (IOException e) {
                        Slog.e(TAG, "Error reading uncrypt package file", e);
                    }
                    if (filename != null && filename.startsWith("/data") && !new File(BLOCK_MAP_FILE).exists()) {
                        Slog.e(TAG, "Can't find block map file, uncrypt failed or unexpected runtime restart?");
                        return;
                    }
                }
            }
            Message msg = Message.obtain(UiThread.getHandler(), new Runnable() {
                public void run() {
                    synchronized (this) {
                        ShutdownThread.rebootOrShutdown((Context) null, reboot, reason);
                    }
                }
            });
            msg.setAsynchronous(true);
            UiThread.getHandler().sendMessage(msg);
        }
    }

    private void createSystemContext() {
        ActivityThread activityThread = ActivityThread.systemMain();
        this.mSystemContext = activityThread.getSystemContext();
        this.mSystemContext.setTheme(DEFAULT_SYSTEM_THEME);
        activityThread.getSystemUiContext().setTheme(DEFAULT_SYSTEM_THEME);
    }

    /* JADX INFO: finally extract failed */
    private void startBootstrapServices() {
        traceBeginAndSlog("StartWatchdog");
        Watchdog watchdog = Watchdog.getInstance();
        watchdog.start();
        traceEnd();
        Slog.i(TAG, "Reading configuration...");
        traceBeginAndSlog("ReadingSystemConfig");
        SystemServerInitThreadPool.get().submit($$Lambda$YWiwiKm_Qgqb55C6tTuq_n2JzdY.INSTANCE, "ReadingSystemConfig");
        traceEnd();
        traceBeginAndSlog("StartInstaller");
        Installer installer = (Installer) this.mSystemServiceManager.startService(Installer.class);
        traceEnd();
        traceBeginAndSlog("DeviceIdentifiersPolicyService");
        this.mSystemServiceManager.startService(DeviceIdentifiersPolicyService.class);
        traceEnd();
        traceBeginAndSlog("UriGrantsManagerService");
        this.mSystemServiceManager.startService(UriGrantsManagerService.Lifecycle.class);
        traceEnd();
        traceBeginAndSlog("StartActivityManager");
        ActivityTaskManagerService atm = ((ActivityTaskManagerService.Lifecycle) this.mSystemServiceManager.startService(ActivityTaskManagerService.Lifecycle.class)).getService();
        this.mActivityManagerService = ActivityManagerService.Lifecycle.startService(this.mSystemServiceManager, atm);
        this.mActivityManagerService.setSystemServiceManager(this.mSystemServiceManager);
        this.mActivityManagerService.setInstaller(installer);
        this.mWindowManagerGlobalLock = atm.getGlobalLock();
        traceEnd();
        traceBeginAndSlog("StartPowerManager");
        this.mPowerManagerService = (PowerManagerService) this.mSystemServiceManager.startService(PowerManagerService.class);
        traceEnd();
        traceBeginAndSlog("StartThermalManager");
        this.mSystemServiceManager.startService(ThermalManagerService.class);
        traceEnd();
        traceBeginAndSlog("InitPowerManagement");
        this.mActivityManagerService.initPowerManagement();
        traceEnd();
        traceBeginAndSlog("StartRecoverySystemService");
        this.mSystemServiceManager.startService(RecoverySystemService.class);
        traceEnd();
        RescueParty.noteBoot(this.mSystemContext);
        traceBeginAndSlog("StartLightsService");
        this.mSystemServiceManager.startService(MiuiLightsService.class);
        traceEnd();
        traceBeginAndSlog("StartSidekickService");
        if (SystemProperties.getBoolean("config.enable_sidekick_graphics", false)) {
            this.mSystemServiceManager.startService(WEAR_SIDEKICK_SERVICE_CLASS);
        }
        traceEnd();
        traceBeginAndSlog("StartDisplayManager");
        this.mDisplayManagerService = (DisplayManagerService) this.mSystemServiceManager.startService(DisplayManagerService.class);
        traceEnd();
        traceBeginAndSlog("WaitForDisplay");
        this.mSystemServiceManager.startBootPhase(100);
        traceEnd();
        String cryptState = (String) VoldProperties.decrypt().orElse("");
        boolean z = true;
        if (ENCRYPTING_STATE.equals(cryptState)) {
            Slog.w(TAG, "Detected encryption in progress - only parsing core apps");
            this.mOnlyCore = true;
        } else if ("1".equals(cryptState)) {
            Slog.w(TAG, "Device encrypted - only parsing core apps");
            this.mOnlyCore = true;
        }
        if (!this.mRuntimeRestart) {
            MetricsLogger.histogram((Context) null, "boot_package_manager_init_start", (int) SystemClock.elapsedRealtime());
        }
        traceBeginAndSlog("StartPackageManagerService");
        long pmsStartTime = SystemClock.uptimeMillis();
        try {
            Watchdog.getInstance().pauseWatchingCurrentThread("packagemanagermain");
            Context context = this.mSystemContext;
            if (this.mFactoryTestMode == 0) {
                z = false;
            }
            this.mPackageManagerService = PackageManagerService.main(context, installer, z, this.mOnlyCore);
            Watchdog.getInstance().resumeWatchingCurrentThread("packagemanagermain");
            SystemServerInjector.markPmsScan(pmsStartTime, SystemClock.uptimeMillis());
            this.mFirstBoot = this.mPackageManagerService.isFirstBoot();
            this.mPackageManager = this.mSystemContext.getPackageManager();
            traceEnd();
            if (!this.mRuntimeRestart && !isFirstBootOrUpgrade()) {
                MetricsLogger.histogram((Context) null, "boot_package_manager_init_ready", (int) SystemClock.elapsedRealtime());
            }
            if (!this.mOnlyCore && !SystemProperties.getBoolean("config.disable_otadexopt", false)) {
                traceBeginAndSlog("StartOtaDexOptService");
                try {
                    Watchdog.getInstance().pauseWatchingCurrentThread("moveab");
                    OtaDexoptService.main(this.mSystemContext, this.mPackageManagerService);
                } catch (Throwable th) {
                    Watchdog.getInstance().resumeWatchingCurrentThread("moveab");
                    traceEnd();
                    throw th;
                }
                Watchdog.getInstance().resumeWatchingCurrentThread("moveab");
                traceEnd();
            }
            traceBeginAndSlog("StartUserManagerService");
            this.mSystemServiceManager.startService(UserManagerService.LifeCycle.class);
            traceEnd();
            traceBeginAndSlog("InitAttributerCache");
            AttributeCache.init(this.mSystemContext);
            traceEnd();
            traceBeginAndSlog("SetSystemProcess");
            this.mActivityManagerService.setSystemProcess();
            traceEnd();
            traceBeginAndSlog("InitWatchdog");
            watchdog.init(this.mSystemContext, this.mActivityManagerService);
            traceEnd();
            this.mDisplayManagerService.setupSchedulerPolicies();
            traceBeginAndSlog("StartOverlayManagerService");
            this.mSystemServiceManager.startService((SystemService) new OverlayManagerService(this.mSystemContext, installer));
            traceEnd();
            traceBeginAndSlog("StartSensorPrivacyService");
            this.mSystemServiceManager.startService((SystemService) new SensorPrivacyService(this.mSystemContext));
            traceEnd();
            if (SystemProperties.getInt("persist.sys.displayinset.top", 0) > 0) {
                this.mActivityManagerService.updateSystemUiContext();
                ((DisplayManagerInternal) LocalServices.getService(DisplayManagerInternal.class)).onOverlayChanged();
            }
            this.mSensorServiceStart = SystemServerInitThreadPool.get().submit($$Lambda$SystemServer$UyrPns7R814gZEylCbDKhe8It4.INSTANCE, START_SENSOR_SERVICE);
        } catch (Throwable th2) {
            Watchdog.getInstance().resumeWatchingCurrentThread("packagemanagermain");
            throw th2;
        }
    }

    static /* synthetic */ void lambda$startBootstrapServices$0() {
        TimingsTraceLog traceLog = new TimingsTraceLog(SYSTEM_SERVER_TIMING_ASYNC_TAG, 524288);
        traceLog.traceBegin(START_SENSOR_SERVICE);
        startSensorService();
        traceLog.traceEnd();
    }

    private void startCoreServices() {
        traceBeginAndSlog("StartBatteryService");
        this.mSystemServiceManager.startService(BatteryService.class);
        traceEnd();
        traceBeginAndSlog("StartUsageService");
        this.mSystemServiceManager.startService(UsageStatsService.class);
        this.mActivityManagerService.setUsageStatsManager((UsageStatsManagerInternal) LocalServices.getService(UsageStatsManagerInternal.class));
        traceEnd();
        if (this.mPackageManager.hasSystemFeature("android.software.webview")) {
            traceBeginAndSlog("StartWebViewUpdateService");
            this.mWebViewUpdateService = (WebViewUpdateService) this.mSystemServiceManager.startService(WebViewUpdateService.class);
            traceEnd();
        }
        traceBeginAndSlog("StartCachedDeviceStateService");
        this.mSystemServiceManager.startService(CachedDeviceStateService.class);
        traceEnd();
        traceBeginAndSlog("StartBinderCallsStatsService");
        this.mSystemServiceManager.startService(BinderCallsStatsService.LifeCycle.class);
        traceEnd();
        traceBeginAndSlog("StartLooperStatsService");
        this.mSystemServiceManager.startService(LooperStatsService.Lifecycle.class);
        traceEnd();
        traceBeginAndSlog("StartRollbackManagerService");
        this.mSystemServiceManager.startService(RollbackManagerService.class);
        traceEnd();
        traceBeginAndSlog("StartBugreportManagerService");
        this.mSystemServiceManager.startService(BugreportManagerService.class);
        traceEnd();
        traceBeginAndSlog(GpuService.TAG);
        this.mSystemServiceManager.startService(GpuService.class);
        traceEnd();
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v1, resolved type: android.os.IBinder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v2, resolved type: com.android.server.IpSecService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v2, resolved type: android.os.IBinder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v4, resolved type: android.os.IBinder} */
    /* JADX WARNING: type inference failed for: r0v199, types: [com.android.server.GraphicsStatsService, android.os.IBinder] */
    /* JADX WARNING: type inference failed for: r7v37, types: [android.os.IBinder] */
    /* JADX WARNING: type inference failed for: r0v296, types: [com.android.server.HardwarePropertiesManagerService] */
    /* JADX WARNING: type inference failed for: r0v302, types: [com.android.server.SerialService] */
    /* JADX WARNING: type inference failed for: r10v20, types: [android.os.IBinder] */
    /* JADX WARNING: type inference failed for: r6v117, types: [android.os.IBinder] */
    /* JADX WARNING: type inference failed for: r6v119, types: [com.android.server.UpdateLockService, android.os.IBinder] */
    /* JADX WARNING: type inference failed for: r6v121, types: [com.android.server.SystemUpdateManagerService, android.os.IBinder] */
    /* JADX WARNING: type inference failed for: r0v349, types: [com.android.server.NsdService] */
    /* JADX WARNING: type inference failed for: r11v10, types: [android.os.IBinder, android.net.IConnectivityManager] */
    /* JADX WARNING: type inference failed for: r7v70, types: [android.os.IBinder] */
    /* JADX WARNING: type inference failed for: r0v396, types: [com.android.server.IpSecService] */
    /* JADX WARNING: type inference failed for: r0v408, types: [com.android.server.statusbar.StatusBarManagerService] */
    /* JADX WARNING: type inference failed for: r6v161, types: [com.android.server.security.KeyAttestationApplicationIdProviderService, android.os.IBinder] */
    /* JADX WARNING: type inference failed for: r6v163, types: [com.android.server.os.SchedulingPolicyService, android.os.IBinder] */
    /* JADX WARNING: type inference failed for: r12v10, types: [android.os.IBinder] */
    /* JADX WARNING: type inference failed for: r13v7, types: [android.os.IBinder] */
    /* JADX WARNING: type inference failed for: r0v483, types: [com.android.server.DynamicSystemService] */
    /* JADX WARNING: type inference failed for: r11v20, types: [android.os.IBinder] */
    /* JADX WARNING: type inference failed for: r12v13, types: [com.android.server.input.InputManagerService, android.os.IBinder] */
    /* JADX WARNING: type inference failed for: r6v173, types: [android.os.IBinder, com.android.server.wm.WindowManagerService] */
    /* JADX WARNING: type inference failed for: r0v533, types: [com.android.server.ConsumerIrService] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x0405  */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x0452  */
    /* JADX WARNING: Removed duplicated region for block: B:130:0x0461  */
    /* JADX WARNING: Removed duplicated region for block: B:187:0x05b4  */
    /* JADX WARNING: Removed duplicated region for block: B:215:0x0631  */
    /* JADX WARNING: Removed duplicated region for block: B:218:0x065b  */
    /* JADX WARNING: Removed duplicated region for block: B:221:0x0676  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x0691  */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x06ac  */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x06d0  */
    /* JADX WARNING: Removed duplicated region for block: B:233:0x06d3  */
    /* JADX WARNING: Removed duplicated region for block: B:246:0x0718  */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x079f  */
    /* JADX WARNING: Removed duplicated region for block: B:311:0x088d  */
    /* JADX WARNING: Removed duplicated region for block: B:319:0x08b1  */
    /* JADX WARNING: Removed duplicated region for block: B:322:0x08c7  */
    /* JADX WARNING: Removed duplicated region for block: B:323:0x08d3  */
    /* JADX WARNING: Removed duplicated region for block: B:340:0x0923  */
    /* JADX WARNING: Removed duplicated region for block: B:343:0x0943  */
    /* JADX WARNING: Removed duplicated region for block: B:351:0x0974  */
    /* JADX WARNING: Removed duplicated region for block: B:364:0x09be  */
    /* JADX WARNING: Removed duplicated region for block: B:388:0x0a57  */
    /* JADX WARNING: Removed duplicated region for block: B:399:0x0a80  */
    /* JADX WARNING: Removed duplicated region for block: B:407:0x0ae7  */
    /* JADX WARNING: Removed duplicated region for block: B:422:0x0b5a  */
    /* JADX WARNING: Removed duplicated region for block: B:423:0x0b5c  */
    /* JADX WARNING: Removed duplicated region for block: B:426:0x0b60  */
    /* JADX WARNING: Removed duplicated region for block: B:448:0x0bf8  */
    /* JADX WARNING: Removed duplicated region for block: B:451:0x0c14  */
    /* JADX WARNING: Removed duplicated region for block: B:454:0x0c2d  */
    /* JADX WARNING: Removed duplicated region for block: B:457:0x0c64  */
    /* JADX WARNING: Removed duplicated region for block: B:465:0x0ca0  */
    /* JADX WARNING: Removed duplicated region for block: B:468:0x0cb7  */
    /* JADX WARNING: Removed duplicated region for block: B:481:0x0d02  */
    /* JADX WARNING: Removed duplicated region for block: B:482:0x0d14  */
    /* JADX WARNING: Removed duplicated region for block: B:484:0x0d18  */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x0d29  */
    /* JADX WARNING: Removed duplicated region for block: B:498:0x0d67  */
    /* JADX WARNING: Removed duplicated region for block: B:505:0x0d7e  */
    /* JADX WARNING: Removed duplicated region for block: B:512:0x0de0  */
    /* JADX WARNING: Removed duplicated region for block: B:514:0x0dfa  */
    /* JADX WARNING: Removed duplicated region for block: B:516:0x0e0b  */
    /* JADX WARNING: Removed duplicated region for block: B:521:0x0e69  */
    /* JADX WARNING: Removed duplicated region for block: B:523:0x0e7a  */
    /* JADX WARNING: Removed duplicated region for block: B:526:0x0e95  */
    /* JADX WARNING: Removed duplicated region for block: B:529:0x0ec4  */
    /* JADX WARNING: Removed duplicated region for block: B:532:0x0ee7  */
    /* JADX WARNING: Removed duplicated region for block: B:545:0x0f4b A[SYNTHETIC, Splitter:B:545:0x0f4b] */
    /* JADX WARNING: Removed duplicated region for block: B:551:0x0f7b  */
    /* JADX WARNING: Removed duplicated region for block: B:563:0x0fdc  */
    /* JADX WARNING: Removed duplicated region for block: B:566:0x100c  */
    /* JADX WARNING: Removed duplicated region for block: B:579:0x1077  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x0344  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x035c  */
    /* JADX WARNING: Unknown variable types count: 8 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void startOtherServices() {
        /*
            r63 = this;
            r5 = r63
            java.lang.String r1 = "onBootPhase"
            java.lang.String r2 = "window"
            java.lang.String r0 = "SecondaryZygotePreload"
            java.lang.String r3 = "dexopt"
            java.lang.String r4 = "SystemServer"
            android.content.Context r15 = r5.mSystemContext
            r6 = 0
            r7 = 0
            r12 = 0
            r8 = 0
            r13 = 0
            r14 = 0
            r16 = 0
            r17 = 0
            r18 = 0
            r19 = 0
            r20 = 0
            r21 = 0
            r22 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r23 = 0
            r24 = 0
            r25 = 0
            r26 = 0
            r27 = r12
            r12 = 0
            r28 = r6
            java.lang.String r6 = "config.disable_systemtextclassifier"
            boolean r29 = android.os.SystemProperties.getBoolean(r6, r12)
            java.lang.String r6 = "config.disable_networktime"
            boolean r30 = android.os.SystemProperties.getBoolean(r6, r12)
            java.lang.String r6 = "config.disable_cameraservice"
            boolean r31 = android.os.SystemProperties.getBoolean(r6, r12)
            java.lang.String r6 = "config.disable_slices"
            boolean r32 = android.os.SystemProperties.getBoolean(r6, r12)
            java.lang.String r6 = "config.enable_lefty"
            boolean r33 = android.os.SystemProperties.getBoolean(r6, r12)
            java.lang.String r6 = "ro.kernel.qemu"
            java.lang.String r6 = android.os.SystemProperties.get(r6)
            java.lang.String r12 = "1"
            boolean r35 = r6.equals(r12)
            java.lang.String r6 = "persist.vendor.wigig.enable"
            r12 = 0
            boolean r36 = android.os.SystemProperties.getBoolean(r6, r12)
            android.content.pm.PackageManager r6 = r15.getPackageManager()
            java.lang.String r12 = "android.hardware.type.watch"
            boolean r37 = r6.hasSystemFeature(r12)
            android.content.pm.PackageManager r6 = r15.getPackageManager()
            java.lang.String r12 = "org.chromium.arc"
            boolean r38 = r6.hasSystemFeature(r12)
            android.content.pm.PackageManager r6 = r15.getPackageManager()
            java.lang.String r12 = "android.hardware.vr.high_performance"
            boolean r39 = r6.hasSystemFeature(r12)
            boolean r6 = android.os.Build.IS_DEBUGGABLE
            if (r6 == 0) goto L_0x009a
            java.lang.String r6 = "debug.crash_system"
            r12 = 0
            boolean r6 = android.os.SystemProperties.getBoolean(r6, r12)
            if (r6 != 0) goto L_0x0094
            goto L_0x009a
        L_0x0094:
            java.lang.RuntimeException r0 = new java.lang.RuntimeException
            r0.<init>()
            throw r0
        L_0x009a:
            r40 = r13
            r41 = r0
            com.android.server.SystemServerInitThreadPool r6 = com.android.server.SystemServerInitThreadPool.get()     // Catch:{ RuntimeException -> 0x031e }
            com.android.server.-$$Lambda$SystemServer$VBGb9VpEls6bUcVBPwYLtX7qDTs r12 = com.android.server.$$Lambda$SystemServer$VBGb9VpEls6bUcVBPwYLtX7qDTs.INSTANCE     // Catch:{ RuntimeException -> 0x031e }
            java.util.concurrent.Future r0 = r6.submit(r12, r0)     // Catch:{ RuntimeException -> 0x031e }
            r5.mZygotePreload = r0     // Catch:{ RuntimeException -> 0x031e }
            java.lang.String r0 = "StartKeyAttestationApplicationIdProviderService"
            traceBeginAndSlog(r0)     // Catch:{ RuntimeException -> 0x031e }
            java.lang.String r0 = "sec_key_att_app_id_provider"
            com.android.server.security.KeyAttestationApplicationIdProviderService r6 = new com.android.server.security.KeyAttestationApplicationIdProviderService     // Catch:{ RuntimeException -> 0x031e }
            r6.<init>(r15)     // Catch:{ RuntimeException -> 0x031e }
            android.os.ServiceManager.addService(r0, r6)     // Catch:{ RuntimeException -> 0x031e }
            traceEnd()     // Catch:{ RuntimeException -> 0x031e }
            java.lang.String r0 = "StartKeyChainSystemService"
            traceBeginAndSlog(r0)     // Catch:{ RuntimeException -> 0x031e }
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager     // Catch:{ RuntimeException -> 0x031e }
            java.lang.Class<com.android.server.security.KeyChainSystemService> r6 = com.android.server.security.KeyChainSystemService.class
            r0.startService(r6)     // Catch:{ RuntimeException -> 0x031e }
            traceEnd()     // Catch:{ RuntimeException -> 0x031e }
            java.lang.String r0 = "StartSchedulingPolicyService"
            traceBeginAndSlog(r0)     // Catch:{ RuntimeException -> 0x031e }
            java.lang.String r0 = "scheduling_policy"
            com.android.server.os.SchedulingPolicyService r6 = new com.android.server.os.SchedulingPolicyService     // Catch:{ RuntimeException -> 0x031e }
            r6.<init>()     // Catch:{ RuntimeException -> 0x031e }
            android.os.ServiceManager.addService(r0, r6)     // Catch:{ RuntimeException -> 0x031e }
            traceEnd()     // Catch:{ RuntimeException -> 0x031e }
            java.lang.String r0 = "StartTelecomLoaderService"
            traceBeginAndSlog(r0)     // Catch:{ RuntimeException -> 0x031e }
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager     // Catch:{ RuntimeException -> 0x031e }
            java.lang.Class<com.android.server.telecom.TelecomLoaderService> r6 = com.android.server.telecom.TelecomLoaderService.class
            r0.startService(r6)     // Catch:{ RuntimeException -> 0x031e }
            traceEnd()     // Catch:{ RuntimeException -> 0x031e }
            java.lang.String r0 = "StartTelephonyRegistry"
            traceBeginAndSlog(r0)     // Catch:{ RuntimeException -> 0x031e }
            com.android.server.TelephonyRegistry r0 = new com.android.server.TelephonyRegistry     // Catch:{ RuntimeException -> 0x031e }
            r0.<init>(r15)     // Catch:{ RuntimeException -> 0x031e }
            r12 = r0
            java.lang.String r0 = "telephony.registry"
            android.os.ServiceManager.addService(r0, r12)     // Catch:{ RuntimeException -> 0x0314 }
            traceEnd()     // Catch:{ RuntimeException -> 0x0314 }
            java.lang.String r0 = "StartEntropyMixer"
            traceBeginAndSlog(r0)     // Catch:{ RuntimeException -> 0x0314 }
            com.android.server.EntropyMixer r0 = new com.android.server.EntropyMixer     // Catch:{ RuntimeException -> 0x0314 }
            r0.<init>(r15)     // Catch:{ RuntimeException -> 0x0314 }
            r5.mEntropyMixer = r0     // Catch:{ RuntimeException -> 0x0314 }
            traceEnd()     // Catch:{ RuntimeException -> 0x0314 }
            android.content.ContentResolver r0 = r15.getContentResolver()     // Catch:{ RuntimeException -> 0x0314 }
            r5.mContentResolver = r0     // Catch:{ RuntimeException -> 0x0314 }
            java.lang.String r0 = "StartAccountManagerService"
            traceBeginAndSlog(r0)     // Catch:{ RuntimeException -> 0x0314 }
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager     // Catch:{ RuntimeException -> 0x0314 }
            java.lang.String r6 = "com.android.server.accounts.AccountManagerService$Lifecycle"
            r0.startService((java.lang.String) r6)     // Catch:{ RuntimeException -> 0x0314 }
            traceEnd()     // Catch:{ RuntimeException -> 0x0314 }
            java.lang.String r0 = "StartContentService"
            traceBeginAndSlog(r0)     // Catch:{ RuntimeException -> 0x0314 }
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager     // Catch:{ RuntimeException -> 0x0314 }
            java.lang.String r6 = "com.android.server.content.ContentService$Lifecycle"
            r0.startService((java.lang.String) r6)     // Catch:{ RuntimeException -> 0x0314 }
            traceEnd()     // Catch:{ RuntimeException -> 0x0314 }
            java.lang.String r0 = "InstallSystemProviders"
            traceBeginAndSlog(r0)     // Catch:{ RuntimeException -> 0x0314 }
            com.android.server.am.ActivityManagerService r0 = r5.mActivityManagerService     // Catch:{ RuntimeException -> 0x0314 }
            r0.installSystemProviders()     // Catch:{ RuntimeException -> 0x0314 }
            android.database.sqlite.SQLiteCompatibilityWalFlags.reset()     // Catch:{ RuntimeException -> 0x0314 }
            traceEnd()     // Catch:{ RuntimeException -> 0x0314 }
            java.lang.String r0 = "StartDropBoxManager"
            traceBeginAndSlog(r0)     // Catch:{ RuntimeException -> 0x0314 }
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager     // Catch:{ RuntimeException -> 0x0314 }
            java.lang.Class<com.android.server.DropBoxManagerService> r6 = com.android.server.DropBoxManagerService.class
            r0.startService(r6)     // Catch:{ RuntimeException -> 0x0314 }
            traceEnd()     // Catch:{ RuntimeException -> 0x0314 }
            java.lang.String r0 = "StartVibratorService"
            traceBeginAndSlog(r0)     // Catch:{ RuntimeException -> 0x0314 }
            com.android.server.VibratorService r0 = new com.android.server.VibratorService     // Catch:{ RuntimeException -> 0x0314 }
            r0.<init>(r15)     // Catch:{ RuntimeException -> 0x0314 }
            r13 = r0
            java.lang.String r0 = "vibrator"
            android.os.ServiceManager.addService(r0, r13)     // Catch:{ RuntimeException -> 0x030c }
            traceEnd()     // Catch:{ RuntimeException -> 0x030c }
            java.lang.String r0 = "StartDynamicSystemService"
            traceBeginAndSlog(r0)     // Catch:{ RuntimeException -> 0x030c }
            com.android.server.DynamicSystemService r0 = new com.android.server.DynamicSystemService     // Catch:{ RuntimeException -> 0x030c }
            r0.<init>(r15)     // Catch:{ RuntimeException -> 0x030c }
            r10 = r0
            java.lang.String r0 = "dynamic_system"
            android.os.ServiceManager.addService(r0, r10)     // Catch:{ RuntimeException -> 0x0303 }
            traceEnd()     // Catch:{ RuntimeException -> 0x0303 }
            if (r37 != 0) goto L_0x019b
            java.lang.String r0 = "StartConsumerIrService"
            traceBeginAndSlog(r0)     // Catch:{ RuntimeException -> 0x0193 }
            com.android.server.ConsumerIrService r0 = new com.android.server.ConsumerIrService     // Catch:{ RuntimeException -> 0x0193 }
            r0.<init>(r15)     // Catch:{ RuntimeException -> 0x0193 }
            r11 = r0
            java.lang.String r0 = "consumer_ir"
            android.os.ServiceManager.addService(r0, r11)     // Catch:{ RuntimeException -> 0x0193 }
            traceEnd()     // Catch:{ RuntimeException -> 0x0193 }
            r28 = r11
            goto L_0x019d
        L_0x0193:
            r0 = move-exception
            r28 = r11
            r46 = r12
            r11 = r8
            goto L_0x0327
        L_0x019b:
            r28 = r11
        L_0x019d:
            java.lang.String r0 = "StartBsGamePadService"
            traceBeginAndSlog(r0)     // Catch:{ RuntimeException -> 0x02fc }
            com.android.server.gamepad.BsGamePadService r0 = new com.android.server.gamepad.BsGamePadService     // Catch:{ RuntimeException -> 0x02fc }
            r0.<init>(r15)     // Catch:{ RuntimeException -> 0x02fc }
            r11 = r0
            java.lang.String r0 = "bsgamepad"
            android.os.ServiceManager.addService(r0, r11)     // Catch:{ RuntimeException -> 0x02f4 }
            r6 = 524288(0x80000, double:2.590327E-318)
            android.os.Trace.traceEnd(r6)     // Catch:{ RuntimeException -> 0x02f4 }
            java.lang.String r0 = "StartAlarmManagerService"
            traceBeginAndSlog(r0)     // Catch:{ RuntimeException -> 0x02f4 }
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager     // Catch:{ RuntimeException -> 0x02f4 }
            com.android.server.AlarmManagerService r6 = new com.android.server.AlarmManagerService     // Catch:{ RuntimeException -> 0x02f4 }
            r6.<init>(r15)     // Catch:{ RuntimeException -> 0x02f4 }
            r0.startService((com.android.server.SystemService) r6)     // Catch:{ RuntimeException -> 0x02f4 }
            traceEnd()     // Catch:{ RuntimeException -> 0x02f4 }
            java.lang.String r0 = "StartInputManagerService"
            traceBeginAndSlog(r0)     // Catch:{ RuntimeException -> 0x02f4 }
            com.android.server.input.InputManagerService r0 = new com.android.server.input.InputManagerService     // Catch:{ RuntimeException -> 0x02f4 }
            r0.<init>(r15)     // Catch:{ RuntimeException -> 0x02f4 }
            r9 = r0
            traceEnd()     // Catch:{ RuntimeException -> 0x02eb }
            java.lang.String r0 = "StartWindowManagerService"
            traceBeginAndSlog(r0)     // Catch:{ RuntimeException -> 0x02eb }
            java.util.concurrent.Future<?> r0 = r5.mSensorServiceStart     // Catch:{ RuntimeException -> 0x02eb }
            java.lang.String r6 = "StartSensorService"
            com.android.internal.util.ConcurrentUtils.waitForFutureNoInterrupt(r0, r6)     // Catch:{ RuntimeException -> 0x02eb }
            r0 = 0
            r5.mSensorServiceStart = r0     // Catch:{ RuntimeException -> 0x02eb }
            boolean r0 = r5.mFirstBoot     // Catch:{ RuntimeException -> 0x02eb }
            if (r0 != 0) goto L_0x01e8
            r8 = 1
            goto L_0x01e9
        L_0x01e8:
            r8 = 0
        L_0x01e9:
            boolean r0 = r5.mOnlyCore     // Catch:{ RuntimeException -> 0x02eb }
            com.android.server.policy.MiuiPhoneWindowManager r45 = new com.android.server.policy.MiuiPhoneWindowManager     // Catch:{ RuntimeException -> 0x02eb }
            r45.<init>()     // Catch:{ RuntimeException -> 0x02eb }
            com.android.server.am.ActivityManagerService r6 = r5.mActivityManagerService     // Catch:{ RuntimeException -> 0x02eb }
            com.android.server.wm.ActivityTaskManagerService r7 = r6.mActivityTaskManager     // Catch:{ RuntimeException -> 0x02eb }
            r46 = r12
            r12 = 1
            r6 = r15
            r42 = r7
            r7 = r9
            r12 = r9
            r9 = r0
            r48 = r10
            r10 = r45
            r45 = r11
            r11 = r42
            com.android.server.wm.WindowManagerService r0 = com.android.server.wm.WindowManagerService.main(r6, r7, r8, r9, r10, r11)     // Catch:{ RuntimeException -> 0x02e4 }
            r6 = r0
            r0 = 17
            r7 = 0
            android.os.ServiceManager.addService(r2, r6, r7, r0)     // Catch:{ RuntimeException -> 0x02db }
            java.lang.String r0 = "input"
            r8 = 1
            android.os.ServiceManager.addService(r0, r12, r7, r8)     // Catch:{ RuntimeException -> 0x02db }
            traceEnd()     // Catch:{ RuntimeException -> 0x02db }
            java.lang.String r0 = "SetWindowManagerService"
            traceBeginAndSlog(r0)     // Catch:{ RuntimeException -> 0x02db }
            com.android.server.am.ActivityManagerService r0 = r5.mActivityManagerService     // Catch:{ RuntimeException -> 0x02db }
            r0.setWindowManager(r6)     // Catch:{ RuntimeException -> 0x02db }
            traceEnd()     // Catch:{ RuntimeException -> 0x02db }
            java.lang.String r0 = "WindowManagerServiceOnInitReady"
            traceBeginAndSlog(r0)     // Catch:{ RuntimeException -> 0x02db }
            r6.onInitReady()     // Catch:{ RuntimeException -> 0x02db }
            traceEnd()     // Catch:{ RuntimeException -> 0x02db }
            com.android.server.SystemServerInitThreadPool r0 = com.android.server.SystemServerInitThreadPool.get()     // Catch:{ RuntimeException -> 0x02db }
            com.android.server.-$$Lambda$SystemServer$NlJmG18aPrQduhRqASIdcn7G0z8 r7 = com.android.server.$$Lambda$SystemServer$NlJmG18aPrQduhRqASIdcn7G0z8.INSTANCE     // Catch:{ RuntimeException -> 0x02db }
            java.lang.String r8 = "StartHidlServices"
            r0.submit(r7, r8)     // Catch:{ RuntimeException -> 0x02db }
            if (r37 != 0) goto L_0x0250
            if (r39 == 0) goto L_0x0250
            java.lang.String r0 = "StartVrManagerService"
            traceBeginAndSlog(r0)     // Catch:{ RuntimeException -> 0x02db }
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager     // Catch:{ RuntimeException -> 0x02db }
            java.lang.Class<com.android.server.vr.VrManagerService> r7 = com.android.server.vr.VrManagerService.class
            r0.startService(r7)     // Catch:{ RuntimeException -> 0x02db }
            traceEnd()     // Catch:{ RuntimeException -> 0x02db }
        L_0x0250:
            java.lang.String r0 = "StartInputManager"
            traceBeginAndSlog(r0)     // Catch:{ RuntimeException -> 0x02db }
            com.android.server.wm.InputManagerCallback r0 = r6.getInputManagerCallback()     // Catch:{ RuntimeException -> 0x02db }
            r12.setWindowManagerCallbacks(r0)     // Catch:{ RuntimeException -> 0x02db }
            r12.start()     // Catch:{ RuntimeException -> 0x02db }
            traceEnd()     // Catch:{ RuntimeException -> 0x02db }
            java.lang.String r0 = "DisplayManagerWindowManagerAndInputReady"
            traceBeginAndSlog(r0)     // Catch:{ RuntimeException -> 0x02db }
            com.android.server.display.DisplayManagerService r0 = r5.mDisplayManagerService     // Catch:{ RuntimeException -> 0x02db }
            r0.windowManagerAndInputReady()     // Catch:{ RuntimeException -> 0x02db }
            traceEnd()     // Catch:{ RuntimeException -> 0x02db }
            int r0 = r5.mFactoryTestMode     // Catch:{ RuntimeException -> 0x02db }
            r7 = 1
            if (r0 != r7) goto L_0x027a
            java.lang.String r0 = "No Bluetooth Service (factory test)"
            android.util.Slog.i(r4, r0)     // Catch:{ RuntimeException -> 0x02db }
            goto L_0x029b
        L_0x027a:
            android.content.pm.PackageManager r0 = r15.getPackageManager()     // Catch:{ RuntimeException -> 0x02db }
            java.lang.String r7 = "android.hardware.bluetooth"
            boolean r0 = r0.hasSystemFeature(r7)     // Catch:{ RuntimeException -> 0x02db }
            if (r0 != 0) goto L_0x028c
            java.lang.String r0 = "No Bluetooth Service (Bluetooth Hardware Not Present)"
            android.util.Slog.i(r4, r0)     // Catch:{ RuntimeException -> 0x02db }
            goto L_0x029b
        L_0x028c:
            java.lang.String r0 = "StartBluetoothService"
            traceBeginAndSlog(r0)     // Catch:{ RuntimeException -> 0x02db }
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager     // Catch:{ RuntimeException -> 0x02db }
            java.lang.Class<com.android.server.BluetoothService> r7 = com.android.server.BluetoothService.class
            r0.startService(r7)     // Catch:{ RuntimeException -> 0x02db }
            traceEnd()     // Catch:{ RuntimeException -> 0x02db }
        L_0x029b:
            java.lang.String r0 = "IpConnectivityMetrics"
            traceBeginAndSlog(r0)     // Catch:{ RuntimeException -> 0x02db }
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager     // Catch:{ RuntimeException -> 0x02db }
            java.lang.Class<com.android.server.connectivity.IpConnectivityMetrics> r7 = com.android.server.connectivity.IpConnectivityMetrics.class
            r0.startService(r7)     // Catch:{ RuntimeException -> 0x02db }
            traceEnd()     // Catch:{ RuntimeException -> 0x02db }
            java.lang.String r0 = "NetworkWatchlistService"
            traceBeginAndSlog(r0)     // Catch:{ RuntimeException -> 0x02db }
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager     // Catch:{ RuntimeException -> 0x02db }
            java.lang.Class<com.android.server.net.watchlist.NetworkWatchlistService$Lifecycle> r7 = com.android.server.net.watchlist.NetworkWatchlistService.Lifecycle.class
            r0.startService(r7)     // Catch:{ RuntimeException -> 0x02db }
            traceEnd()     // Catch:{ RuntimeException -> 0x02db }
            java.lang.String r0 = "PinnerService"
            traceBeginAndSlog(r0)     // Catch:{ RuntimeException -> 0x02db }
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager     // Catch:{ RuntimeException -> 0x02db }
            java.lang.Class<com.android.server.PinnerService> r7 = com.android.server.PinnerService.class
            r0.startService(r7)     // Catch:{ RuntimeException -> 0x02db }
            traceEnd()     // Catch:{ RuntimeException -> 0x02db }
            java.lang.String r0 = "SignedConfigService"
            traceBeginAndSlog(r0)     // Catch:{ RuntimeException -> 0x02db }
            android.content.Context r0 = r5.mSystemContext     // Catch:{ RuntimeException -> 0x02db }
            com.android.server.signedconfig.SignedConfigService.registerUpdateReceiver(r0)     // Catch:{ RuntimeException -> 0x02db }
            traceEnd()     // Catch:{ RuntimeException -> 0x02db }
            r9 = r6
            r20 = r13
            r13 = r12
            goto L_0x033e
        L_0x02db:
            r0 = move-exception
            r20 = r6
            r9 = r12
            r11 = r45
            r10 = r48
            goto L_0x0327
        L_0x02e4:
            r0 = move-exception
            r9 = r12
            r11 = r45
            r10 = r48
            goto L_0x0327
        L_0x02eb:
            r0 = move-exception
            r48 = r10
            r45 = r11
            r46 = r12
            r12 = r9
            goto L_0x0327
        L_0x02f4:
            r0 = move-exception
            r48 = r10
            r45 = r11
            r46 = r12
            goto L_0x0327
        L_0x02fc:
            r0 = move-exception
            r48 = r10
            r46 = r12
            r11 = r8
            goto L_0x0327
        L_0x0303:
            r0 = move-exception
            r48 = r10
            r46 = r12
            r28 = r11
            r11 = r8
            goto L_0x0327
        L_0x030c:
            r0 = move-exception
            r46 = r12
            r10 = r7
            r28 = r11
            r11 = r8
            goto L_0x0327
        L_0x0314:
            r0 = move-exception
            r46 = r12
            r10 = r7
            r13 = r28
            r28 = r11
            r11 = r8
            goto L_0x0327
        L_0x031e:
            r0 = move-exception
            r46 = r10
            r13 = r28
            r10 = r7
            r28 = r11
            r11 = r8
        L_0x0327:
            java.lang.String r6 = "System"
            java.lang.String r7 = "******************************************"
            android.util.Slog.e(r6, r7)
            java.lang.String r7 = "************ Failure starting core service"
            android.util.Slog.e(r6, r7, r0)
            r48 = r10
            r45 = r11
            r62 = r13
            r13 = r9
            r9 = r20
            r20 = r62
        L_0x033e:
            boolean r12 = r9.detectSafeMode()
            if (r12 == 0) goto L_0x034e
            android.content.ContentResolver r0 = r15.getContentResolver()
            java.lang.String r6 = "airplane_mode_on"
            r7 = 1
            android.provider.Settings.Global.putInt(r0, r6, r7)
        L_0x034e:
            r6 = 0
            r7 = 0
            r8 = 0
            r10 = 0
            r11 = 0
            r41 = 0
            int r0 = r5.mFactoryTestMode
            r42 = r6
            r6 = 1
            if (r0 == r6) goto L_0x038e
            java.lang.String r0 = "StartInputMethodManagerLifecycle"
            traceBeginAndSlog(r0)
            boolean r0 = android.view.inputmethod.InputMethodSystemProperty.MULTI_CLIENT_IME_ENABLED
            if (r0 == 0) goto L_0x036d
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.inputmethod.MultiClientInputMethodManagerService$Lifecycle> r6 = com.android.server.inputmethod.MultiClientInputMethodManagerService.Lifecycle.class
            r0.startService(r6)
            goto L_0x0374
        L_0x036d:
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.inputmethod.InputMethodManagerService$Lifecycle> r6 = com.android.server.inputmethod.InputMethodManagerService.Lifecycle.class
            r0.startService(r6)
        L_0x0374:
            traceEnd()
            java.lang.String r0 = "StartAccessibilityManagerService"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager     // Catch:{ all -> 0x0384 }
            java.lang.String r6 = "com.android.server.accessibility.AccessibilityManagerService$Lifecycle"
            r0.startService((java.lang.String) r6)     // Catch:{ all -> 0x0384 }
            goto L_0x038b
        L_0x0384:
            r0 = move-exception
            java.lang.String r6 = "starting Accessibility Manager"
            r5.reportWtf(r6, r0)
        L_0x038b:
            traceEnd()
        L_0x038e:
            java.lang.String r0 = "MakeDisplayReady"
            traceBeginAndSlog(r0)
            r9.displayReady()     // Catch:{ all -> 0x0397 }
            goto L_0x03a0
        L_0x0397:
            r0 = move-exception
            r6 = r0
            r0 = r6
            java.lang.String r6 = "making display ready"
            r5.reportWtf(r6, r0)
        L_0x03a0:
            traceEnd()
            int r0 = r5.mFactoryTestMode
            r6 = 1
            if (r0 == r6) goto L_0x03f2
            java.lang.String r0 = "system_init.startmountservice"
            java.lang.String r0 = android.os.SystemProperties.get(r0)
            java.lang.String r6 = "0"
            boolean r0 = r6.equals(r0)
            if (r0 != 0) goto L_0x03f2
            java.lang.String r0 = "StartStorageManagerService"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager     // Catch:{ all -> 0x03d1 }
            java.lang.String r6 = "com.android.server.StorageManagerService$Lifecycle"
            r0.startService((java.lang.String) r6)     // Catch:{ all -> 0x03d1 }
            java.lang.String r0 = "mount"
            android.os.IBinder r0 = android.os.ServiceManager.getService(r0)     // Catch:{ all -> 0x03d1 }
            android.os.storage.IStorageManager r0 = android.os.storage.IStorageManager.Stub.asInterface(r0)     // Catch:{ all -> 0x03d1 }
            r27 = r0
            goto L_0x03d8
        L_0x03d1:
            r0 = move-exception
            java.lang.String r6 = "starting StorageManagerService"
            r5.reportWtf(r6, r0)
        L_0x03d8:
            traceEnd()
            java.lang.String r0 = "StartStorageStatsService"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager     // Catch:{ all -> 0x03e8 }
            java.lang.String r6 = "com.android.server.usage.StorageStatsService$Lifecycle"
            r0.startService((java.lang.String) r6)     // Catch:{ all -> 0x03e8 }
            goto L_0x03ef
        L_0x03e8:
            r0 = move-exception
            java.lang.String r6 = "starting StorageStatsService"
            r5.reportWtf(r6, r0)
        L_0x03ef:
            traceEnd()
        L_0x03f2:
            java.lang.String r0 = "StartUiModeManager"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.UiModeManagerService> r6 = com.android.server.UiModeManagerService.class
            r0.startService(r6)
            traceEnd()
            boolean r0 = r5.mOnlyCore
            if (r0 != 0) goto L_0x0452
            java.lang.String r0 = "UpdatePackagesIfNeeded"
            traceBeginAndSlog(r0)
            r49 = r7
            long r6 = android.os.SystemClock.uptimeMillis()
            com.android.server.Watchdog r0 = com.android.server.Watchdog.getInstance()     // Catch:{ all -> 0x0426 }
            r0.pauseWatchingCurrentThread(r3)     // Catch:{ all -> 0x0426 }
            com.android.server.pm.PackageManagerService r0 = r5.mPackageManagerService     // Catch:{ all -> 0x0426 }
            r0.updatePackagesIfNeeded()     // Catch:{ all -> 0x0426 }
            com.android.server.Watchdog r0 = com.android.server.Watchdog.getInstance()
            r0.resumeWatchingCurrentThread(r3)
            r50 = r8
            goto L_0x0437
        L_0x0426:
            r0 = move-exception
            r50 = r8
            java.lang.String r8 = "update packages"
            r5.reportWtf(r8, r0)     // Catch:{ all -> 0x0446 }
            com.android.server.Watchdog r0 = com.android.server.Watchdog.getInstance()
            r0.resumeWatchingCurrentThread(r3)
        L_0x0437:
            r8 = r10
            r51 = r11
            long r10 = android.os.SystemClock.uptimeMillis()
            com.android.server.SystemServerInjector.markBootDexopt(r6, r10)
            traceEnd()
            goto L_0x0459
        L_0x0446:
            r0 = move-exception
            r8 = r10
            r51 = r11
            com.android.server.Watchdog r1 = com.android.server.Watchdog.getInstance()
            r1.resumeWatchingCurrentThread(r3)
            throw r0
        L_0x0452:
            r49 = r7
            r50 = r8
            r8 = r10
            r51 = r11
        L_0x0459:
            int r0 = r5.mFactoryTestMode
            java.lang.String r3 = "starting "
            r6 = 1
            if (r0 == r6) goto L_0x0de0
            java.lang.String r0 = "StartLockSettingsService"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager     // Catch:{ all -> 0x047a }
            java.lang.String r6 = "com.android.server.locksettings.LockSettingsService$Lifecycle"
            r0.startService((java.lang.String) r6)     // Catch:{ all -> 0x047a }
            java.lang.String r0 = "lock_settings"
            android.os.IBinder r0 = android.os.ServiceManager.getService(r0)     // Catch:{ all -> 0x047a }
            com.android.internal.widget.ILockSettings r0 = com.android.internal.widget.ILockSettings.Stub.asInterface(r0)     // Catch:{ all -> 0x047a }
            r11 = r0
            goto L_0x0483
        L_0x047a:
            r0 = move-exception
            java.lang.String r6 = "starting LockSettingsService service"
            r5.reportWtf(r6, r0)
            r11 = r51
        L_0x0483:
            traceEnd()
            java.lang.String r0 = "ro.frp.pst"
            java.lang.String r0 = android.os.SystemProperties.get(r0)
            java.lang.String r6 = ""
            boolean r0 = r0.equals(r6)
            r6 = 1
            r0 = r0 ^ r6
            r6 = r0
            java.lang.String r0 = "ro.gsid.image_running"
            r7 = 0
            int r0 = android.os.SystemProperties.getInt(r0, r7)
            if (r0 <= 0) goto L_0x04a2
            r0 = 1
            goto L_0x04a3
        L_0x04a2:
            r0 = 0
        L_0x04a3:
            r7 = r0
            if (r6 == 0) goto L_0x04b7
            if (r7 != 0) goto L_0x04b7
            java.lang.String r0 = "StartPersistentDataBlock"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.PersistentDataBlockService> r10 = com.android.server.PersistentDataBlockService.class
            r0.startService(r10)
            traceEnd()
        L_0x04b7:
            java.lang.String r0 = "StartTestHarnessMode"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.testharness.TestHarnessModeService> r10 = com.android.server.testharness.TestHarnessModeService.class
            r0.startService(r10)
            traceEnd()
            if (r6 != 0) goto L_0x04ce
            boolean r0 = com.android.server.oemlock.OemLockService.isHalPresent()
            if (r0 == 0) goto L_0x04dd
        L_0x04ce:
            java.lang.String r0 = "StartOemLockService"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.oemlock.OemLockService> r10 = com.android.server.oemlock.OemLockService.class
            r0.startService(r10)
            traceEnd()
        L_0x04dd:
            java.lang.String r0 = "StartDeviceIdleController"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.DeviceIdleController> r10 = com.android.server.DeviceIdleController.class
            r0.startService(r10)
            traceEnd()
            java.lang.String r0 = "StartDevicePolicyManager"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.devicepolicy.DevicePolicyManagerService$Lifecycle> r10 = com.android.server.devicepolicy.DevicePolicyManagerService.Lifecycle.class
            r0.startService(r10)
            traceEnd()
            if (r37 != 0) goto L_0x0523
            java.lang.String r0 = "StartStatusBarManagerService"
            traceBeginAndSlog(r0)
            com.android.server.statusbar.StatusBarManagerService r0 = new com.android.server.statusbar.StatusBarManagerService     // Catch:{ all -> 0x0513 }
            r0.<init>(r15, r9)     // Catch:{ all -> 0x0513 }
            r10 = r0
            java.lang.String r0 = "statusbar"
            android.os.ServiceManager.addService(r0, r10)     // Catch:{ all -> 0x0511 }
            r51 = r6
            goto L_0x051e
        L_0x0511:
            r0 = move-exception
            goto L_0x0516
        L_0x0513:
            r0 = move-exception
            r10 = r42
        L_0x0516:
            r51 = r6
            java.lang.String r6 = "starting StatusBarManagerService"
            r5.reportWtf(r6, r0)
        L_0x051e:
            traceEnd()
            r6 = r10
            goto L_0x0527
        L_0x0523:
            r51 = r6
            r6 = r42
        L_0x0527:
            r5.startContentCaptureService(r15)
            r5.startAttentionService(r15)
            r5.startSystemCaptionsManagerService(r15)
            java.lang.String r0 = "StartAppPredictionService"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.String r10 = "com.android.server.appprediction.AppPredictionManagerService"
            r0.startService((java.lang.String) r10)
            traceEnd()
            java.lang.String r0 = "StartContentSuggestionsService"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.String r10 = "com.android.server.contentsuggestions.ContentSuggestionsManagerService"
            r0.startService((java.lang.String) r10)
            traceEnd()
            java.lang.String r0 = "InitNetworkStackClient"
            traceBeginAndSlog(r0)
            android.net.NetworkStackClient r0 = android.net.NetworkStackClient.getInstance()     // Catch:{ all -> 0x055b }
            r0.init()     // Catch:{ all -> 0x055b }
            goto L_0x0562
        L_0x055b:
            r0 = move-exception
            java.lang.String r10 = "initializing NetworkStackClient"
            r5.reportWtf(r10, r0)
        L_0x0562:
            traceEnd()
            java.lang.String r0 = "StartNetworkManagementService"
            traceBeginAndSlog(r0)
            com.android.server.NetworkManagementService r0 = com.android.server.NetworkManagementService.create(r15)     // Catch:{ all -> 0x057a }
            r10 = r0
            java.lang.String r0 = "network_management"
            android.os.ServiceManager.addService(r0, r10)     // Catch:{ all -> 0x0578 }
            r42 = r6
            goto L_0x0585
        L_0x0578:
            r0 = move-exception
            goto L_0x057d
        L_0x057a:
            r0 = move-exception
            r10 = r40
        L_0x057d:
            r42 = r6
            java.lang.String r6 = "starting NetworkManagement Service"
            r5.reportWtf(r6, r0)
        L_0x0585:
            traceEnd()
            java.lang.String r0 = "StartIpSecService"
            traceBeginAndSlog(r0)
            com.android.server.IpSecService r0 = com.android.server.IpSecService.create(r15)     // Catch:{ all -> 0x0599 }
            r14 = r0
            java.lang.String r0 = "ipsec"
            android.os.ServiceManager.addService(r0, r14)     // Catch:{ all -> 0x0599 }
            goto L_0x05a0
        L_0x0599:
            r0 = move-exception
            java.lang.String r6 = "starting IpSec Service"
            r5.reportWtf(r6, r0)
        L_0x05a0:
            traceEnd()
            java.lang.String r0 = "StartTextServicesManager"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.textservices.TextServicesManagerService$Lifecycle> r6 = com.android.server.textservices.TextServicesManagerService.Lifecycle.class
            r0.startService(r6)
            traceEnd()
            if (r29 != 0) goto L_0x05c3
            java.lang.String r0 = "StartTextClassificationManagerService"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.textclassifier.TextClassificationManagerService$Lifecycle> r6 = com.android.server.textclassifier.TextClassificationManagerService.Lifecycle.class
            r0.startService(r6)
            traceEnd()
        L_0x05c3:
            java.lang.String r0 = "StartNetworkScoreService"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.NetworkScoreService$Lifecycle> r6 = com.android.server.NetworkScoreService.Lifecycle.class
            r0.startService(r6)
            traceEnd()
            java.lang.String r0 = "StartNetworkStatsService"
            traceBeginAndSlog(r0)
            com.android.server.net.NetworkStatsService r0 = com.android.server.net.NetworkStatsService.create(r15, r10)     // Catch:{ all -> 0x05e5 }
            r6 = r0
            java.lang.String r0 = "netstats"
            android.os.ServiceManager.addService(r0, r6)     // Catch:{ all -> 0x05e3 }
            goto L_0x05f2
        L_0x05e3:
            r0 = move-exception
            goto L_0x05e8
        L_0x05e5:
            r0 = move-exception
            r6 = r16
        L_0x05e8:
            r16 = r6
            java.lang.String r6 = "starting NetworkStats Service"
            r5.reportWtf(r6, r0)
            r6 = r16
        L_0x05f2:
            traceEnd()
            java.lang.String r0 = "StartNetworkPolicyManagerService"
            traceBeginAndSlog(r0)
            com.android.server.net.NetworkPolicyManagerService r0 = new com.android.server.net.NetworkPolicyManagerService     // Catch:{ all -> 0x0611 }
            r52 = r7
            com.android.server.am.ActivityManagerService r7 = r5.mActivityManagerService     // Catch:{ all -> 0x060d }
            r0.<init>(r15, r7, r10)     // Catch:{ all -> 0x060d }
            r7 = r0
            java.lang.String r0 = "netpolicy"
            android.os.ServiceManager.addService(r0, r7)     // Catch:{ all -> 0x060b }
            goto L_0x0620
        L_0x060b:
            r0 = move-exception
            goto L_0x0616
        L_0x060d:
            r0 = move-exception
            r7 = r17
            goto L_0x0616
        L_0x0611:
            r0 = move-exception
            r52 = r7
            r7 = r17
        L_0x0616:
            r16 = r7
            java.lang.String r7 = "starting NetworkPolicy Service"
            r5.reportWtf(r7, r0)
            r7 = r16
        L_0x0620:
            traceEnd()
            android.content.pm.PackageManager r0 = r15.getPackageManager()
            r53 = r8
            java.lang.String r8 = "android.hardware.wifi"
            boolean r0 = r0.hasSystemFeature(r8)
            if (r0 == 0) goto L_0x064f
            java.lang.String r0 = "StartWifi"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.String r8 = "com.android.server.wifi.WifiService"
            r0.startService((java.lang.String) r8)
            traceEnd()
            java.lang.String r0 = "StartWifiScanning"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.String r8 = "com.android.server.wifi.scanner.WifiScanningService"
            r0.startService((java.lang.String) r8)
            traceEnd()
        L_0x064f:
            android.content.pm.PackageManager r0 = r15.getPackageManager()
            java.lang.String r8 = "android.hardware.wifi.rtt"
            boolean r0 = r0.hasSystemFeature(r8)
            if (r0 == 0) goto L_0x066a
            java.lang.String r0 = "StartRttService"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.String r8 = "com.android.server.wifi.rtt.RttService"
            r0.startService((java.lang.String) r8)
            traceEnd()
        L_0x066a:
            android.content.pm.PackageManager r0 = r15.getPackageManager()
            java.lang.String r8 = "android.hardware.wifi.aware"
            boolean r0 = r0.hasSystemFeature(r8)
            if (r0 == 0) goto L_0x0685
            java.lang.String r0 = "StartWifiAware"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.String r8 = "com.android.server.wifi.aware.WifiAwareService"
            r0.startService((java.lang.String) r8)
            traceEnd()
        L_0x0685:
            android.content.pm.PackageManager r0 = r15.getPackageManager()
            java.lang.String r8 = "android.hardware.wifi.direct"
            boolean r0 = r0.hasSystemFeature(r8)
            if (r0 == 0) goto L_0x06a0
            java.lang.String r0 = "StartWifiP2P"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.String r8 = "com.android.server.wifi.p2p.WifiP2pService"
            r0.startService((java.lang.String) r8)
            traceEnd()
        L_0x06a0:
            android.content.pm.PackageManager r0 = r15.getPackageManager()
            java.lang.String r8 = "android.hardware.lowpan"
            boolean r0 = r0.hasSystemFeature(r8)
            if (r0 == 0) goto L_0x06bb
            java.lang.String r0 = "StartLowpan"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.String r8 = "com.android.server.lowpan.LowpanService"
            r0.startService((java.lang.String) r8)
            traceEnd()
        L_0x06bb:
            android.content.pm.PackageManager r0 = r5.mPackageManager
            java.lang.String r8 = "android.hardware.ethernet"
            boolean r0 = r0.hasSystemFeature(r8)
            java.lang.String r8 = "android.hardware.usb.host"
            if (r0 != 0) goto L_0x06d3
            android.content.pm.PackageManager r0 = r5.mPackageManager
            boolean r0 = r0.hasSystemFeature(r8)
            if (r0 == 0) goto L_0x06d0
            goto L_0x06d3
        L_0x06d0:
            r54 = r11
            goto L_0x06e4
        L_0x06d3:
            java.lang.String r0 = "StartEthernet"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            r54 = r11
            java.lang.String r11 = "com.android.server.ethernet.EthernetService"
            r0.startService((java.lang.String) r11)
            traceEnd()
        L_0x06e4:
            java.lang.String r0 = "StartConnectivityService"
            traceBeginAndSlog(r0)
            com.android.server.ConnectivityService r0 = new com.android.server.ConnectivityService     // Catch:{ all -> 0x0706 }
            r0.<init>(r15, r10, r6, r7)     // Catch:{ all -> 0x0706 }
            r11 = r0
            java.lang.String r0 = "connectivity"
            r16 = r6
            r6 = 6
            r40 = r10
            r10 = 0
            android.os.ServiceManager.addService(r0, r11, r10, r6)     // Catch:{ all -> 0x06fe }
            r7.bindConnectivityManager(r11)     // Catch:{ all -> 0x06fe }
            goto L_0x0713
        L_0x06fe:
            r0 = move-exception
            goto L_0x070d
        L_0x0700:
            r0 = move-exception
            r16 = r6
            r40 = r10
            goto L_0x070d
        L_0x0706:
            r0 = move-exception
            r16 = r6
            r40 = r10
            r11 = r18
        L_0x070d:
            java.lang.String r6 = "starting Connectivity Service"
            r5.reportWtf(r6, r0)
        L_0x0713:
            traceEnd()
            if (r36 == 0) goto L_0x079f
            java.lang.String r0 = "Wigig Service"
            android.util.Slog.i(r4, r0)     // Catch:{ all -> 0x0795 }
            java.lang.String r0 = "/system/framework/wigig-service.jar:/system/product/framework/vendor.qti.hardware.wigig.supptunnel-V1.0-java.jar:/system/product/framework/vendor.qti.hardware.wigig.netperftuner-V1.0-java.jar:/system/product/framework/vendor.qti.hardware.capabilityconfigstore-V1.0-java.jar"
            dalvik.system.PathClassLoader r6 = new dalvik.system.PathClassLoader     // Catch:{ all -> 0x0795 }
            java.lang.Class r10 = r63.getClass()     // Catch:{ all -> 0x0795 }
            java.lang.ClassLoader r10 = r10.getClassLoader()     // Catch:{ all -> 0x0795 }
            r6.<init>(r0, r10)     // Catch:{ all -> 0x0795 }
            java.lang.String r10 = "com.qualcomm.qti.server.wigig.p2p.WigigP2pServiceImpl"
            java.lang.Class r10 = r6.loadClass(r10)     // Catch:{ all -> 0x0795 }
            r18 = r0
            r17 = r7
            r7 = 1
            java.lang.Class[] r0 = new java.lang.Class[r7]     // Catch:{ all -> 0x0793 }
            java.lang.Class<android.content.Context> r7 = android.content.Context.class
            r34 = 0
            r0[r34] = r7     // Catch:{ all -> 0x0793 }
            java.lang.reflect.Constructor r0 = r10.getConstructor(r0)     // Catch:{ all -> 0x0793 }
            r55 = r10
            r7 = 1
            java.lang.Object[] r10 = new java.lang.Object[r7]     // Catch:{ all -> 0x0793 }
            r7 = 0
            r10[r7] = r15     // Catch:{ all -> 0x0793 }
            java.lang.Object r7 = r0.newInstance(r10)     // Catch:{ all -> 0x0793 }
            r25 = r7
            java.lang.String r7 = "Successfully loaded WigigP2pServiceImpl class"
            android.util.Slog.i(r4, r7)     // Catch:{ all -> 0x0793 }
            java.lang.String r7 = "wigigp2p"
            r10 = r25
            android.os.IBinder r10 = (android.os.IBinder) r10     // Catch:{ all -> 0x0793 }
            android.os.ServiceManager.addService(r7, r10)     // Catch:{ all -> 0x0793 }
            java.lang.String r7 = "com.qualcomm.qti.server.wigig.WigigService"
            java.lang.Class r7 = r6.loadClass(r7)     // Catch:{ all -> 0x0793 }
            r56 = r0
            r10 = 1
            java.lang.Class[] r0 = new java.lang.Class[r10]     // Catch:{ all -> 0x0793 }
            java.lang.Class<android.content.Context> r10 = android.content.Context.class
            r34 = 0
            r0[r34] = r10     // Catch:{ all -> 0x0793 }
            java.lang.reflect.Constructor r0 = r7.getConstructor(r0)     // Catch:{ all -> 0x0793 }
            r56 = r6
            r10 = 1
            java.lang.Object[] r6 = new java.lang.Object[r10]     // Catch:{ all -> 0x0793 }
            r10 = 0
            r6[r10] = r15     // Catch:{ all -> 0x0793 }
            java.lang.Object r6 = r0.newInstance(r6)     // Catch:{ all -> 0x0793 }
            r26 = r6
            java.lang.String r6 = "Successfully loaded WigigService class"
            android.util.Slog.i(r4, r6)     // Catch:{ all -> 0x0793 }
            java.lang.String r6 = "wigig"
            r10 = r26
            android.os.IBinder r10 = (android.os.IBinder) r10     // Catch:{ all -> 0x0793 }
            android.os.ServiceManager.addService(r6, r10)     // Catch:{ all -> 0x0793 }
            goto L_0x07a1
        L_0x0793:
            r0 = move-exception
            goto L_0x0798
        L_0x0795:
            r0 = move-exception
            r17 = r7
        L_0x0798:
            java.lang.String r6 = "starting WigigService"
            r5.reportWtf(r6, r0)
            goto L_0x07a1
        L_0x079f:
            r17 = r7
        L_0x07a1:
            java.lang.String r0 = "StartNsdService"
            traceBeginAndSlog(r0)
            com.android.server.NsdService r0 = com.android.server.NsdService.create(r15)     // Catch:{ all -> 0x07b6 }
            r6 = r0
            java.lang.String r0 = "servicediscovery"
            android.os.ServiceManager.addService(r0, r6)     // Catch:{ all -> 0x07b4 }
            r19 = r6
            goto L_0x07c1
        L_0x07b4:
            r0 = move-exception
            goto L_0x07b9
        L_0x07b6:
            r0 = move-exception
            r6 = r19
        L_0x07b9:
            java.lang.String r7 = "starting Service Discovery Service"
            r5.reportWtf(r7, r0)
            r19 = r6
        L_0x07c1:
            traceEnd()
            java.lang.String r0 = "StartSystemUpdateManagerService"
            traceBeginAndSlog(r0)
            java.lang.String r0 = "system_update"
            com.android.server.SystemUpdateManagerService r6 = new com.android.server.SystemUpdateManagerService     // Catch:{ all -> 0x07d5 }
            r6.<init>(r15)     // Catch:{ all -> 0x07d5 }
            android.os.ServiceManager.addService(r0, r6)     // Catch:{ all -> 0x07d5 }
            goto L_0x07dc
        L_0x07d5:
            r0 = move-exception
            java.lang.String r6 = "starting SystemUpdateManagerService"
            r5.reportWtf(r6, r0)
        L_0x07dc:
            traceEnd()
            java.lang.String r0 = "StartUpdateLockService"
            traceBeginAndSlog(r0)
            java.lang.String r0 = "updatelock"
            com.android.server.UpdateLockService r6 = new com.android.server.UpdateLockService     // Catch:{ all -> 0x07f0 }
            r6.<init>(r15)     // Catch:{ all -> 0x07f0 }
            android.os.ServiceManager.addService(r0, r6)     // Catch:{ all -> 0x07f0 }
            goto L_0x07f7
        L_0x07f0:
            r0 = move-exception
            java.lang.String r6 = "starting UpdateLockService"
            r5.reportWtf(r6, r0)
        L_0x07f7:
            traceEnd()
            java.lang.String r0 = "StartNotificationManager"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.notification.NotificationManagerService> r6 = com.android.server.notification.NotificationManagerService.class
            r0.startService(r6)
            com.android.internal.notification.SystemNotificationChannels.removeDeprecated(r15)
            com.android.internal.notification.SystemNotificationChannels.createAll(r15)
            java.lang.String r0 = "notification"
            android.os.IBinder r0 = android.os.ServiceManager.getService(r0)
            android.app.INotificationManager r7 = android.app.INotificationManager.Stub.asInterface(r0)
            traceEnd()
            java.lang.String r0 = "StartDeviceMonitor"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.storage.DeviceStorageMonitorService> r6 = com.android.server.storage.DeviceStorageMonitorService.class
            r0.startService(r6)
            traceEnd()
            java.lang.String r0 = "StartLocationManagerService"
            traceBeginAndSlog(r0)
            com.android.server.LocationManagerService r0 = new com.android.server.LocationManagerService     // Catch:{ all -> 0x083e }
            r0.<init>(r15)     // Catch:{ all -> 0x083e }
            r6 = r0
            java.lang.String r0 = "location"
            android.os.ServiceManager.addService(r0, r6)     // Catch:{ all -> 0x083c }
            goto L_0x0847
        L_0x083c:
            r0 = move-exception
            goto L_0x0841
        L_0x083e:
            r0 = move-exception
            r6 = r50
        L_0x0841:
            java.lang.String r10 = "starting Location Manager"
            r5.reportWtf(r10, r0)
        L_0x0847:
            traceEnd()
            java.lang.String r0 = "StartCountryDetectorService"
            traceBeginAndSlog(r0)
            com.android.server.CountryDetectorService r0 = new com.android.server.CountryDetectorService     // Catch:{ all -> 0x085f }
            r0.<init>(r15)     // Catch:{ all -> 0x085f }
            r10 = r0
            java.lang.String r0 = "country_detector"
            android.os.ServiceManager.addService(r0, r10)     // Catch:{ all -> 0x085d }
            r18 = r6
            goto L_0x086a
        L_0x085d:
            r0 = move-exception
            goto L_0x0862
        L_0x085f:
            r0 = move-exception
            r10 = r53
        L_0x0862:
            r18 = r6
            java.lang.String r6 = "starting Country Detector"
            r5.reportWtf(r6, r0)
        L_0x086a:
            traceEnd()
            r6 = 1
            java.lang.String r0 = "StartTimeDetectorService"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager     // Catch:{ all -> 0x087f }
            r49 = r6
            java.lang.String r6 = "com.android.server.timedetector.TimeDetectorService$Lifecycle"
            r0.startService((java.lang.String) r6)     // Catch:{ all -> 0x087d }
            goto L_0x0888
        L_0x087d:
            r0 = move-exception
            goto L_0x0882
        L_0x087f:
            r0 = move-exception
            r49 = r6
        L_0x0882:
            java.lang.String r6 = "starting StartTimeDetectorService service"
            r5.reportWtf(r6, r0)
        L_0x0888:
            traceEnd()
            if (r37 != 0) goto L_0x08a4
            java.lang.String r0 = "StartSearchManagerService"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager     // Catch:{ all -> 0x089a }
            java.lang.String r6 = "com.android.server.search.SearchManagerService$Lifecycle"
            r0.startService((java.lang.String) r6)     // Catch:{ all -> 0x089a }
            goto L_0x08a1
        L_0x089a:
            r0 = move-exception
            java.lang.String r6 = "starting Search Service"
            r5.reportWtf(r6, r0)
        L_0x08a1:
            traceEnd()
        L_0x08a4:
            android.content.res.Resources r0 = r15.getResources()
            r6 = 17891453(0x111007d, float:2.6632644E-38)
            boolean r0 = r0.getBoolean(r6)
            if (r0 == 0) goto L_0x08c0
            java.lang.String r0 = "StartWallpaperManagerService"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.String r6 = "com.android.server.wallpaper.WallpaperManagerService$Lifecycle"
            r0.startService((java.lang.String) r6)
            traceEnd()
        L_0x08c0:
            java.lang.String r0 = "StartAudioService"
            traceBeginAndSlog(r0)
            if (r38 != 0) goto L_0x08d3
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.audio.AudioService$Lifecycle> r6 = com.android.server.audio.AudioService.Lifecycle.class
            r0.startService(r6)
            r55 = r7
            r50 = r10
            goto L_0x0916
        L_0x08d3:
            android.content.res.Resources r0 = r15.getResources()
            r6 = 17039742(0x104017e, float:2.4245642E-38)
            java.lang.String r6 = r0.getString(r6)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager     // Catch:{ all -> 0x08ff }
            r55 = r7
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x08fb }
            r7.<init>()     // Catch:{ all -> 0x08fb }
            r7.append(r6)     // Catch:{ all -> 0x08fb }
            r50 = r10
            java.lang.String r10 = "$Lifecycle"
            r7.append(r10)     // Catch:{ all -> 0x08f9 }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x08f9 }
            r0.startService((java.lang.String) r7)     // Catch:{ all -> 0x08f9 }
            goto L_0x0916
        L_0x08f9:
            r0 = move-exception
            goto L_0x0904
        L_0x08fb:
            r0 = move-exception
            r50 = r10
            goto L_0x0904
        L_0x08ff:
            r0 = move-exception
            r55 = r7
            r50 = r10
        L_0x0904:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r3)
            r7.append(r6)
            java.lang.String r7 = r7.toString()
            r5.reportWtf(r7, r0)
        L_0x0916:
            traceEnd()
            android.content.pm.PackageManager r0 = r5.mPackageManager
            java.lang.String r6 = "android.hardware.broadcastradio"
            boolean r0 = r0.hasSystemFeature(r6)
            if (r0 == 0) goto L_0x0932
            java.lang.String r0 = "StartBroadcastRadioService"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.broadcastradio.BroadcastRadioService> r6 = com.android.server.broadcastradio.BroadcastRadioService.class
            r0.startService(r6)
            traceEnd()
        L_0x0932:
            java.lang.String r0 = "StartDockObserver"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.DockObserver> r6 = com.android.server.DockObserver.class
            r0.startService(r6)
            traceEnd()
            if (r37 == 0) goto L_0x0952
            java.lang.String r0 = "StartThermalObserver"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.String r6 = "com.google.android.clockwork.ThermalObserver"
            r0.startService((java.lang.String) r6)
            traceEnd()
        L_0x0952:
            java.lang.String r0 = "StartWiredAccessoryManager"
            traceBeginAndSlog(r0)
            com.android.server.WiredAccessoryManager r0 = new com.android.server.WiredAccessoryManager     // Catch:{ all -> 0x0960 }
            r0.<init>(r15, r13)     // Catch:{ all -> 0x0960 }
            r13.setWiredAccessoryCallbacks(r0)     // Catch:{ all -> 0x0960 }
            goto L_0x0967
        L_0x0960:
            r0 = move-exception
            java.lang.String r6 = "starting WiredAccessoryManager"
            r5.reportWtf(r6, r0)
        L_0x0967:
            traceEnd()
            android.content.pm.PackageManager r0 = r5.mPackageManager
            java.lang.String r6 = "android.software.midi"
            boolean r0 = r0.hasSystemFeature(r6)
            if (r0 == 0) goto L_0x0983
            java.lang.String r0 = "StartMidiManager"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.String r6 = "com.android.server.midi.MidiService$Lifecycle"
            r0.startService((java.lang.String) r6)
            traceEnd()
        L_0x0983:
            java.lang.String r0 = "StartAdbService"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager     // Catch:{ all -> 0x0990 }
            java.lang.String r6 = "com.android.server.adb.AdbService$Lifecycle"
            r0.startService((java.lang.String) r6)     // Catch:{ all -> 0x0990 }
            goto L_0x0996
        L_0x0990:
            r0 = move-exception
            java.lang.String r6 = "Failure starting AdbService"
            android.util.Slog.e(r4, r6)
        L_0x0996:
            traceEnd()
            android.content.pm.PackageManager r0 = r5.mPackageManager
            boolean r0 = r0.hasSystemFeature(r8)
            if (r0 != 0) goto L_0x09ad
            android.content.pm.PackageManager r0 = r5.mPackageManager
            java.lang.String r6 = "android.hardware.usb.accessory"
            boolean r0 = r0.hasSystemFeature(r6)
            if (r0 != 0) goto L_0x09ad
            if (r35 == 0) goto L_0x09bc
        L_0x09ad:
            java.lang.String r0 = "StartUsbService"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.String r6 = "com.android.server.usb.UsbService$Lifecycle"
            r0.startService((java.lang.String) r6)
            traceEnd()
        L_0x09bc:
            if (r37 != 0) goto L_0x09df
            java.lang.String r0 = "StartSerialService"
            traceBeginAndSlog(r0)
            com.android.server.SerialService r0 = new com.android.server.SerialService     // Catch:{ all -> 0x09d2 }
            r0.<init>(r15)     // Catch:{ all -> 0x09d2 }
            r6 = r0
            java.lang.String r0 = "serial"
            android.os.ServiceManager.addService(r0, r6)     // Catch:{ all -> 0x09d0 }
            goto L_0x09da
        L_0x09d0:
            r0 = move-exception
            goto L_0x09d5
        L_0x09d2:
            r0 = move-exception
            r6 = r21
        L_0x09d5:
            java.lang.String r7 = "Failure starting SerialService"
            android.util.Slog.e(r4, r7, r0)
        L_0x09da:
            traceEnd()
            r21 = r6
        L_0x09df:
            java.lang.String r0 = "StartHardwarePropertiesManagerService"
            traceBeginAndSlog(r0)
            com.android.server.HardwarePropertiesManagerService r0 = new com.android.server.HardwarePropertiesManagerService     // Catch:{ all -> 0x09f4 }
            r0.<init>(r15)     // Catch:{ all -> 0x09f4 }
            r6 = r0
            java.lang.String r0 = "hardware_properties"
            android.os.ServiceManager.addService(r0, r6)     // Catch:{ all -> 0x09f2 }
            r24 = r6
            goto L_0x09fe
        L_0x09f2:
            r0 = move-exception
            goto L_0x09f7
        L_0x09f4:
            r0 = move-exception
            r6 = r24
        L_0x09f7:
            java.lang.String r7 = "Failure starting HardwarePropertiesManagerService"
            android.util.Slog.e(r4, r7, r0)
            r24 = r6
        L_0x09fe:
            traceEnd()
            java.lang.String r0 = "StartTwilightService"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.twilight.TwilightService> r6 = com.android.server.twilight.TwilightService.class
            r0.startService(r6)
            traceEnd()
            java.lang.String r0 = "StartColorDisplay"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.display.color.ColorDisplayService> r6 = com.android.server.display.color.ColorDisplayService.class
            r0.startService(r6)
            traceEnd()
            java.lang.String r0 = "StartJobScheduler"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.job.JobSchedulerService> r6 = com.android.server.job.JobSchedulerService.class
            r0.startService(r6)
            traceEnd()
            java.lang.String r0 = "StartSoundTrigger"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.soundtrigger.SoundTriggerService> r6 = com.android.server.soundtrigger.SoundTriggerService.class
            r0.startService(r6)
            traceEnd()
            java.lang.String r0 = "StartTrustManager"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.trust.TrustManagerService> r6 = com.android.server.trust.TrustManagerService.class
            r0.startService(r6)
            traceEnd()
            boolean r0 = r5.mOnlyCore
            com.android.server.SystemServerInjector.addExtraServices(r15, r0)
            boolean r0 = miui.os.DeviceFeature.hasMirihiSupport()
            if (r0 == 0) goto L_0x0a6a
            java.lang.String r0 = "miui.slide.SlideManagerService"
            miui.slide.SlideManagerService r6 = new miui.slide.SlideManagerService     // Catch:{ all -> 0x0a63 }
            r6.<init>(r15)     // Catch:{ all -> 0x0a63 }
            android.os.ServiceManager.addService(r0, r6)     // Catch:{ all -> 0x0a63 }
            goto L_0x0a6a
        L_0x0a63:
            r0 = move-exception
            java.lang.String r6 = "starting SlideManagerService"
            r5.reportWtf(r6, r0)
        L_0x0a6a:
            com.miui.server.greeze.GreezeManagerService.startService(r15)     // Catch:{ Exception -> 0x0a6e }
            goto L_0x0a76
        L_0x0a6e:
            r0 = move-exception
            r6 = r0
            r0 = r6
            java.lang.String r6 = "GreezeManager start error:"
            android.util.Slog.e(r4, r6, r0)
        L_0x0a76:
            android.content.pm.PackageManager r0 = r5.mPackageManager
            java.lang.String r6 = "android.software.backup"
            boolean r0 = r0.hasSystemFeature(r6)
            if (r0 == 0) goto L_0x0a8f
            java.lang.String r0 = "StartBackupManager"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.String r6 = "com.android.server.backup.BackupManagerService$Lifecycle"
            r0.startService((java.lang.String) r6)
            traceEnd()
        L_0x0a8f:
            android.content.pm.PackageManager r0 = r5.mPackageManager
            java.lang.String r6 = "android.software.app_widgets"
            boolean r0 = r0.hasSystemFeature(r6)
            if (r0 != 0) goto L_0x0aa6
            android.content.res.Resources r0 = r15.getResources()
            r6 = 17891433(0x1110069, float:2.6632588E-38)
            boolean r0 = r0.getBoolean(r6)
            if (r0 == 0) goto L_0x0ab5
        L_0x0aa6:
            java.lang.String r0 = "StartAppWidgetService"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.String r6 = "com.android.server.appwidget.AppWidgetService"
            r0.startService((java.lang.String) r6)
            traceEnd()
        L_0x0ab5:
            java.lang.String r0 = "StartRoleManagerService"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            com.android.server.role.RoleManagerService r6 = new com.android.server.role.RoleManagerService
            android.content.Context r7 = r5.mSystemContext
            com.android.server.policy.role.LegacyRoleResolutionPolicy r8 = new com.android.server.policy.role.LegacyRoleResolutionPolicy
            r8.<init>(r7)
            r6.<init>(r7, r8)
            r0.startService((com.android.server.SystemService) r6)
            traceEnd()
            java.lang.String r0 = "StartVoiceRecognitionManager"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.String r6 = "com.android.server.voiceinteraction.VoiceInteractionManagerService"
            r0.startService((java.lang.String) r6)
            traceEnd()
            android.content.res.Resources r0 = r15.getResources()
            boolean r0 = com.android.server.GestureLauncherService.isGestureLauncherEnabled(r0)
            if (r0 == 0) goto L_0x0af6
            java.lang.String r0 = "StartGestureLauncher"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.GestureLauncherService> r6 = com.android.server.GestureLauncherService.class
            r0.startService(r6)
            traceEnd()
        L_0x0af6:
            java.lang.String r0 = "StartSensorNotification"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.SensorNotificationService> r6 = com.android.server.SensorNotificationService.class
            r0.startService(r6)
            traceEnd()
            java.lang.String r0 = "StartContextHubSystemService"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.ContextHubSystemService> r6 = com.android.server.ContextHubSystemService.class
            r0.startService(r6)
            traceEnd()
            java.lang.String r0 = "StartDiskStatsService"
            traceBeginAndSlog(r0)
            java.lang.String r0 = "diskstats"
            com.android.server.DiskStatsService r6 = new com.android.server.DiskStatsService     // Catch:{ all -> 0x0b24 }
            r6.<init>(r15)     // Catch:{ all -> 0x0b24 }
            android.os.ServiceManager.addService(r0, r6)     // Catch:{ all -> 0x0b24 }
            goto L_0x0b2b
        L_0x0b24:
            r0 = move-exception
            java.lang.String r6 = "starting DiskStats Service"
            r5.reportWtf(r6, r0)
        L_0x0b2b:
            traceEnd()
            java.lang.String r0 = "RuntimeService"
            traceBeginAndSlog(r0)
            java.lang.String r0 = "runtime"
            com.android.server.RuntimeService r6 = new com.android.server.RuntimeService     // Catch:{ all -> 0x0b3f }
            r6.<init>(r15)     // Catch:{ all -> 0x0b3f }
            android.os.ServiceManager.addService(r0, r6)     // Catch:{ all -> 0x0b3f }
            goto L_0x0b46
        L_0x0b3f:
            r0 = move-exception
            java.lang.String r6 = "starting RuntimeService"
            r5.reportWtf(r6, r0)
        L_0x0b46:
            traceEnd()
            boolean r0 = r5.mOnlyCore
            if (r0 != 0) goto L_0x0b5c
            android.content.res.Resources r0 = r15.getResources()
            r6 = 17891452(0x111007c, float:2.6632641E-38)
            boolean r0 = r0.getBoolean(r6)
            if (r0 == 0) goto L_0x0b5c
            r0 = 1
            goto L_0x0b5d
        L_0x0b5c:
            r0 = 0
        L_0x0b5d:
            r6 = r0
            if (r6 == 0) goto L_0x0b6f
            java.lang.String r0 = "StartTimeZoneRulesManagerService"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.String r7 = "com.android.server.timezone.RulesManagerService$Lifecycle"
            r0.startService((java.lang.String) r7)
            traceEnd()
        L_0x0b6f:
            if (r37 != 0) goto L_0x0baf
            if (r30 != 0) goto L_0x0baf
            java.lang.String r0 = "StartNetworkTimeUpdateService"
            traceBeginAndSlog(r0)
            com.android.server.NewNetworkTimeUpdateService r0 = new com.android.server.NewNetworkTimeUpdateService     // Catch:{ all -> 0x0ba1 }
            r0.<init>(r15)     // Catch:{ all -> 0x0ba1 }
            r7 = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0b9f }
            r0.<init>()     // Catch:{ all -> 0x0b9f }
            java.lang.String r8 = "Using networkTimeUpdater class="
            r0.append(r8)     // Catch:{ all -> 0x0b9f }
            java.lang.Class r8 = r7.getClass()     // Catch:{ all -> 0x0b9f }
            r0.append(r8)     // Catch:{ all -> 0x0b9f }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0b9f }
            android.util.Slog.d(r4, r0)     // Catch:{ all -> 0x0b9f }
            java.lang.String r0 = "network_time_update_service"
            android.os.ServiceManager.addService(r0, r7)     // Catch:{ all -> 0x0b9f }
            r22 = r7
            goto L_0x0bac
        L_0x0b9f:
            r0 = move-exception
            goto L_0x0ba4
        L_0x0ba1:
            r0 = move-exception
            r7 = r22
        L_0x0ba4:
            java.lang.String r8 = "starting NetworkTimeUpdate service"
            r5.reportWtf(r8, r0)
            r22 = r7
        L_0x0bac:
            traceEnd()
        L_0x0baf:
            java.lang.String r0 = "CertBlacklister"
            traceBeginAndSlog(r0)
            com.android.server.CertBlacklister r0 = new com.android.server.CertBlacklister     // Catch:{ all -> 0x0bba }
            r0.<init>(r15)     // Catch:{ all -> 0x0bba }
            goto L_0x0bc1
        L_0x0bba:
            r0 = move-exception
            java.lang.String r7 = "starting CertBlacklister"
            r5.reportWtf(r7, r0)
        L_0x0bc1:
            traceEnd()
            java.lang.String r0 = "StartEmergencyAffordanceService"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.emergency.EmergencyAffordanceService> r7 = com.android.server.emergency.EmergencyAffordanceService.class
            r0.startService(r7)
            traceEnd()
            java.lang.String r0 = "StartDreamManager"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.dreams.DreamManagerService> r7 = com.android.server.dreams.DreamManagerService.class
            r0.startService(r7)
            traceEnd()
            java.lang.String r0 = "AddGraphicsStatsService"
            traceBeginAndSlog(r0)
            com.android.server.GraphicsStatsService r0 = new com.android.server.GraphicsStatsService
            r0.<init>(r15)
            java.lang.String r7 = "graphicsstats"
            android.os.ServiceManager.addService(r7, r0)
            traceEnd()
            boolean r0 = com.android.server.coverage.CoverageService.ENABLED
            if (r0 == 0) goto L_0x0c0a
            java.lang.String r0 = "AddCoverageService"
            traceBeginAndSlog(r0)
            com.android.server.coverage.CoverageService r0 = new com.android.server.coverage.CoverageService
            r0.<init>()
            java.lang.String r7 = "coverage"
            android.os.ServiceManager.addService(r7, r0)
            traceEnd()
        L_0x0c0a:
            android.content.pm.PackageManager r0 = r5.mPackageManager
            java.lang.String r7 = "android.software.print"
            boolean r0 = r0.hasSystemFeature(r7)
            if (r0 == 0) goto L_0x0c23
            java.lang.String r0 = "StartPrintManager"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.String r7 = "com.android.server.print.PrintManagerService"
            r0.startService((java.lang.String) r7)
            traceEnd()
        L_0x0c23:
            android.content.pm.PackageManager r0 = r5.mPackageManager
            java.lang.String r7 = "android.software.companion_device_setup"
            boolean r0 = r0.hasSystemFeature(r7)
            if (r0 == 0) goto L_0x0c3c
            java.lang.String r0 = "StartCompanionDeviceManager"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.String r7 = "com.android.server.companion.CompanionDeviceManagerService"
            r0.startService((java.lang.String) r7)
            traceEnd()
        L_0x0c3c:
            java.lang.String r0 = "StartRestrictionManager"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.restrictions.RestrictionsManagerService> r7 = com.android.server.restrictions.RestrictionsManagerService.class
            r0.startService(r7)
            traceEnd()
            java.lang.String r0 = "StartMediaSessionService"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.media.MediaSessionService> r7 = com.android.server.media.MediaSessionService.class
            r0.startService(r7)
            traceEnd()
            android.content.pm.PackageManager r0 = r5.mPackageManager
            java.lang.String r7 = "android.hardware.hdmi.cec"
            boolean r0 = r0.hasSystemFeature(r7)
            if (r0 == 0) goto L_0x0c73
            java.lang.String r0 = "StartHdmiControlService"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.hdmi.HdmiControlService> r7 = com.android.server.hdmi.HdmiControlService.class
            r0.startService(r7)
            traceEnd()
        L_0x0c73:
            android.content.pm.PackageManager r0 = r5.mPackageManager
            java.lang.String r7 = "android.software.live_tv"
            boolean r0 = r0.hasSystemFeature(r7)
            java.lang.String r7 = "android.software.leanback"
            if (r0 != 0) goto L_0x0c87
            android.content.pm.PackageManager r0 = r5.mPackageManager
            boolean r0 = r0.hasSystemFeature(r7)
            if (r0 == 0) goto L_0x0c96
        L_0x0c87:
            java.lang.String r0 = "StartTvInputManager"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.tv.TvInputManagerService> r8 = com.android.server.tv.TvInputManagerService.class
            r0.startService(r8)
            traceEnd()
        L_0x0c96:
            android.content.pm.PackageManager r0 = r5.mPackageManager
            java.lang.String r8 = "android.software.picture_in_picture"
            boolean r0 = r0.hasSystemFeature(r8)
            if (r0 == 0) goto L_0x0caf
            java.lang.String r0 = "StartMediaResourceMonitor"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.media.MediaResourceMonitorService> r8 = com.android.server.media.MediaResourceMonitorService.class
            r0.startService(r8)
            traceEnd()
        L_0x0caf:
            android.content.pm.PackageManager r0 = r5.mPackageManager
            boolean r0 = r0.hasSystemFeature(r7)
            if (r0 == 0) goto L_0x0cc6
            java.lang.String r0 = "StartTvRemoteService"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.tv.TvRemoteService> r7 = com.android.server.tv.TvRemoteService.class
            r0.startService(r7)
            traceEnd()
        L_0x0cc6:
            java.lang.String r0 = "StartMediaRouterService"
            traceBeginAndSlog(r0)
            com.android.server.media.MediaRouterService r0 = new com.android.server.media.MediaRouterService     // Catch:{ all -> 0x0cda }
            r0.<init>(r15)     // Catch:{ all -> 0x0cda }
            r7 = r0
            java.lang.String r0 = "media_router"
            android.os.ServiceManager.addService(r0, r7)     // Catch:{ all -> 0x0cd8 }
            goto L_0x0ce3
        L_0x0cd8:
            r0 = move-exception
            goto L_0x0cdd
        L_0x0cda:
            r0 = move-exception
            r7 = r41
        L_0x0cdd:
            java.lang.String r8 = "starting MediaRouterService"
            r5.reportWtf(r8, r0)
        L_0x0ce3:
            traceEnd()
            android.content.pm.PackageManager r0 = r5.mPackageManager
            java.lang.String r8 = "android.hardware.biometrics.face"
            boolean r8 = r0.hasSystemFeature(r8)
            android.content.pm.PackageManager r0 = r5.mPackageManager
            java.lang.String r10 = "android.hardware.biometrics.iris"
            boolean r10 = r0.hasSystemFeature(r10)
            android.content.pm.PackageManager r0 = r5.mPackageManager
            r53 = r6
            java.lang.String r6 = "android.hardware.fingerprint"
            boolean r6 = r0.hasSystemFeature(r6)
            if (r8 == 0) goto L_0x0d14
            java.lang.String r0 = "StartFaceSensor"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            r41 = r7
            java.lang.Class<com.android.server.biometrics.face.FaceService> r7 = com.android.server.biometrics.face.FaceService.class
            r0.startService(r7)
            traceEnd()
            goto L_0x0d16
        L_0x0d14:
            r41 = r7
        L_0x0d16:
            if (r10 == 0) goto L_0x0d27
            java.lang.String r0 = "StartIrisSensor"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.biometrics.iris.IrisService> r7 = com.android.server.biometrics.iris.IrisService.class
            r0.startService(r7)
            traceEnd()
        L_0x0d27:
            if (r6 == 0) goto L_0x0d38
            java.lang.String r0 = "StartFingerprintSensor"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.biometrics.fingerprint.FingerprintService> r7 = com.android.server.biometrics.fingerprint.FingerprintService.class
            r0.startService(r7)
            traceEnd()
        L_0x0d38:
            if (r8 != 0) goto L_0x0d3e
            if (r10 != 0) goto L_0x0d3e
            if (r6 == 0) goto L_0x0d4d
        L_0x0d3e:
            java.lang.String r0 = "StartBiometricService"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.biometrics.BiometricService> r7 = com.android.server.biometrics.BiometricService.class
            r0.startService(r7)
            traceEnd()
        L_0x0d4d:
            java.lang.String r0 = "StartBackgroundDexOptService"
            traceBeginAndSlog(r0)
            com.android.server.pm.PackageManagerService r0 = r5.mPackageManagerService     // Catch:{ all -> 0x0d5b }
            com.android.server.pm.BackgroundDexOptService.setPackageManagerService(r0)     // Catch:{ all -> 0x0d5b }
            com.android.server.pm.BackgroundDexOptService.schedule(r15)     // Catch:{ all -> 0x0d5b }
            goto L_0x0d62
        L_0x0d5b:
            r0 = move-exception
            java.lang.String r7 = "starting StartBackgroundDexOptService"
            r5.reportWtf(r7, r0)
        L_0x0d62:
            traceEnd()
            if (r37 != 0) goto L_0x0d7c
            java.lang.String r0 = "StartDynamicCodeLoggingService"
            traceBeginAndSlog(r0)
            com.android.server.pm.DynamicCodeLoggingService.schedule(r15)     // Catch:{ all -> 0x0d70 }
            goto L_0x0d79
        L_0x0d70:
            r0 = move-exception
            r7 = r0
            r0 = r7
            java.lang.String r7 = "starting DynamicCodeLoggingService"
            r5.reportWtf(r7, r0)
        L_0x0d79:
            traceEnd()
        L_0x0d7c:
            if (r37 != 0) goto L_0x0d92
            java.lang.String r7 = "StartPruneInstantAppsJobService"
            traceBeginAndSlog(r7)
            com.android.server.PruneInstantAppsJobService.schedule(r15)     // Catch:{ all -> 0x0d87 }
            goto L_0x0d8f
        L_0x0d87:
            r0 = move-exception
            r56 = r0
            r0 = r56
            r5.reportWtf(r7, r0)
        L_0x0d8f:
            traceEnd()
        L_0x0d92:
            java.lang.String r0 = "StartShortcutServiceLifecycle"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.pm.ShortcutService$Lifecycle> r7 = com.android.server.pm.ShortcutService.Lifecycle.class
            r0.startService(r7)
            traceEnd()
            java.lang.String r0 = "StartLauncherAppsService"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.pm.LauncherAppsService> r7 = com.android.server.pm.LauncherAppsService.class
            r0.startService(r7)
            traceEnd()
            java.lang.String r0 = "StartCrossProfileAppsService"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.pm.CrossProfileAppsService> r7 = com.android.server.pm.CrossProfileAppsService.class
            r0.startService(r7)
            traceEnd()
            r52 = r41
            r53 = r50
            r51 = r54
            r49 = r55
            r50 = r18
            r41 = r24
            r18 = r14
            r24 = r19
            r14 = r25
            r19 = r16
            r25 = r21
            r21 = r17
            r62 = r22
            r22 = r11
            r11 = r26
            r26 = r62
            goto L_0x0df8
        L_0x0de0:
            r53 = r8
            r11 = r26
            r52 = r41
            r26 = r22
            r41 = r24
            r22 = r18
            r24 = r19
            r18 = r14
            r19 = r16
            r14 = r25
            r25 = r21
            r21 = r17
        L_0x0df8:
            if (r37 != 0) goto L_0x0e09
            java.lang.String r0 = "StartMediaProjectionManager"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.media.projection.MediaProjectionManagerService> r6 = com.android.server.media.projection.MediaProjectionManagerService.class
            r0.startService(r6)
            traceEnd()
        L_0x0e09:
            if (r37 == 0) goto L_0x0e67
            java.lang.String r0 = "StartWearPowerService"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.String r6 = "com.android.clockwork.power.WearPowerService"
            r0.startService((java.lang.String) r6)
            traceEnd()
            java.lang.String r0 = "StartWearConnectivityService"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.String r6 = "com.android.clockwork.connectivity.WearConnectivityService"
            r0.startService((java.lang.String) r6)
            traceEnd()
            java.lang.String r0 = "StartWearDisplayService"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.String r6 = "com.google.android.clockwork.display.WearDisplayService"
            r0.startService((java.lang.String) r6)
            traceEnd()
            java.lang.String r0 = "StartWearTimeService"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.String r6 = "com.google.android.clockwork.time.WearTimeService"
            r0.startService((java.lang.String) r6)
            traceEnd()
            if (r33 == 0) goto L_0x0e58
            java.lang.String r0 = "StartWearLeftyService"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.String r6 = "com.google.android.clockwork.lefty.WearLeftyService"
            r0.startService((java.lang.String) r6)
            traceEnd()
        L_0x0e58:
            java.lang.String r0 = "StartWearGlobalActionsService"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.String r6 = "com.android.clockwork.globalactions.GlobalActionsService"
            r0.startService((java.lang.String) r6)
            traceEnd()
        L_0x0e67:
            if (r32 != 0) goto L_0x0e78
            java.lang.String r0 = "StartSliceManagerService"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.String r6 = "com.android.server.slice.SliceManagerService$Lifecycle"
            r0.startService((java.lang.String) r6)
            traceEnd()
        L_0x0e78:
            if (r31 != 0) goto L_0x0e89
            java.lang.String r0 = "StartCameraServiceProxy"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.camera.CameraServiceProxy> r6 = com.android.server.camera.CameraServiceProxy.class
            r0.startService(r6)
            traceEnd()
        L_0x0e89:
            android.content.pm.PackageManager r0 = r15.getPackageManager()
            java.lang.String r6 = "android.hardware.type.embedded"
            boolean r0 = r0.hasSystemFeature(r6)
            if (r0 == 0) goto L_0x0ea4
            java.lang.String r0 = "StartIoTSystemService"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.String r6 = "com.android.things.server.IoTSystemService"
            r0.startService((java.lang.String) r6)
            traceEnd()
        L_0x0ea4:
            java.lang.String r0 = "StartStatsCompanionService"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.stats.StatsCompanionService$Lifecycle> r6 = com.android.server.stats.StatsCompanionService.Lifecycle.class
            r0.startService(r6)
            traceEnd()
            java.lang.String r0 = "StartIncidentCompanionService"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.incident.IncidentCompanionService> r6 = com.android.server.incident.IncidentCompanionService.class
            r0.startService(r6)
            traceEnd()
            if (r12 == 0) goto L_0x0ec9
            com.android.server.am.ActivityManagerService r0 = r5.mActivityManagerService
            r0.enterSafeMode()
        L_0x0ec9:
            java.lang.String r0 = "StartMmsService"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.MmsServiceBroker> r6 = com.android.server.MmsServiceBroker.class
            com.android.server.SystemService r0 = r0.startService(r6)
            r23 = r0
            com.android.server.MmsServiceBroker r23 = (com.android.server.MmsServiceBroker) r23
            traceEnd()
            android.content.pm.PackageManager r0 = r5.mPackageManager
            java.lang.String r6 = "android.software.autofill"
            boolean r0 = r0.hasSystemFeature(r6)
            if (r0 == 0) goto L_0x0ef6
            java.lang.String r0 = "StartAutoFillService"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.String r6 = "com.android.server.autofill.AutofillManagerService"
            r0.startService((java.lang.String) r6)
            traceEnd()
        L_0x0ef6:
            java.lang.String r0 = "StartClipboardService"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.clipboard.ClipboardService> r6 = com.android.server.clipboard.ClipboardService.class
            r0.startService(r6)
            traceEnd()
            java.lang.String r0 = "AppServiceManager"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.appbinding.AppBindingService$Lifecycle> r6 = com.android.server.appbinding.AppBindingService.Lifecycle.class
            r0.startService(r6)
            traceEnd()
            java.lang.String r0 = "MakeVibratorServiceReady"
            traceBeginAndSlog(r0)
            r20.systemReady()     // Catch:{ all -> 0x0f1d }
            goto L_0x0f26
        L_0x0f1d:
            r0 = move-exception
            r6 = r0
            r0 = r6
            java.lang.String r6 = "making Vibrator Service ready"
            r5.reportWtf(r6, r0)
        L_0x0f26:
            traceEnd()
            java.lang.String r0 = "MakeBsGamePadServiceReady"
            r6 = 524288(0x80000, double:2.590327E-318)
            android.os.Trace.traceBegin(r6, r0)
            r45.systemReady()     // Catch:{ all -> 0x0f35 }
            goto L_0x0f3e
        L_0x0f35:
            r0 = move-exception
            r6 = r0
            r0 = r6
            java.lang.String r6 = "making BsGamePad Service ready"
            r5.reportWtf(r6, r0)
        L_0x0f3e:
            r6 = 524288(0x80000, double:2.590327E-318)
            android.os.Trace.traceEnd(r6)
            java.lang.String r0 = "MakeLockSettingsServiceReady"
            traceBeginAndSlog(r0)
            if (r51 == 0) goto L_0x0f58
            r51.systemReady()     // Catch:{ all -> 0x0f4f }
            goto L_0x0f58
        L_0x0f4f:
            r0 = move-exception
            r6 = r0
            r0 = r6
            java.lang.String r6 = "making Lock Settings Service ready"
            r5.reportWtf(r6, r0)
        L_0x0f58:
            traceEnd()
            java.lang.String r0 = "StartBootPhaseLockSettingsReady"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            r6 = 480(0x1e0, float:6.73E-43)
            r0.startBootPhase(r6)
            traceEnd()
            java.lang.String r0 = "StartBootPhaseSystemServicesReady"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            r6 = 500(0x1f4, float:7.0E-43)
            r0.startBootPhase(r6)
            traceEnd()
            if (r36 == 0) goto L_0x0fc5
            java.lang.String r0 = "calling onBootPhase for Wigig Services"
            android.util.Slog.i(r4, r0)     // Catch:{ all -> 0x0fbf }
            java.lang.Class r0 = r14.getClass()     // Catch:{ all -> 0x0fbf }
            r4 = 1
            java.lang.Class[] r7 = new java.lang.Class[r4]     // Catch:{ all -> 0x0fbf }
            java.lang.Class r4 = java.lang.Integer.TYPE     // Catch:{ all -> 0x0fbf }
            r8 = 0
            r7[r8] = r4     // Catch:{ all -> 0x0fbf }
            java.lang.reflect.Method r4 = r0.getMethod(r1, r7)     // Catch:{ all -> 0x0fbf }
            r7 = 1
            java.lang.Object[] r8 = new java.lang.Object[r7]     // Catch:{ all -> 0x0fbf }
            java.lang.Integer r7 = new java.lang.Integer     // Catch:{ all -> 0x0fbf }
            r7.<init>(r6)     // Catch:{ all -> 0x0fbf }
            r10 = 0
            r8[r10] = r7     // Catch:{ all -> 0x0fbf }
            r4.invoke(r14, r8)     // Catch:{ all -> 0x0fbf }
            java.lang.Class r7 = r11.getClass()     // Catch:{ all -> 0x0fbf }
            r8 = 1
            java.lang.Class[] r10 = new java.lang.Class[r8]     // Catch:{ all -> 0x0fbf }
            java.lang.Class r8 = java.lang.Integer.TYPE     // Catch:{ all -> 0x0fbf }
            r16 = 0
            r10[r16] = r8     // Catch:{ all -> 0x0fbf }
            java.lang.reflect.Method r1 = r7.getMethod(r1, r10)     // Catch:{ all -> 0x0fbf }
            r4 = 1
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0fbf }
            java.lang.Integer r8 = new java.lang.Integer     // Catch:{ all -> 0x0fbf }
            r8.<init>(r6)     // Catch:{ all -> 0x0fbf }
            r6 = 0
            r4[r6] = r8     // Catch:{ all -> 0x0fbf }
            r1.invoke(r11, r4)     // Catch:{ all -> 0x0fbf }
            goto L_0x0fc5
        L_0x0fbf:
            r0 = move-exception
            java.lang.String r1 = "Wigig services ready"
            r5.reportWtf(r1, r0)
        L_0x0fc5:
            java.lang.String r0 = "MakeWindowManagerServiceReady"
            traceBeginAndSlog(r0)
            r9.systemReady()     // Catch:{ all -> 0x0fce }
            goto L_0x0fd7
        L_0x0fce:
            r0 = move-exception
            r1 = r0
            r0 = r1
            java.lang.String r1 = "making Window Manager Service ready"
            r5.reportWtf(r1, r0)
        L_0x0fd7:
            traceEnd()
            if (r12 == 0) goto L_0x0fe1
            com.android.server.am.ActivityManagerService r0 = r5.mActivityManagerService
            r0.showSafeModeOverlay()
        L_0x0fe1:
            r1 = 0
            android.content.res.Configuration r4 = r9.computeNewConfiguration(r1)
            android.util.DisplayMetrics r0 = new android.util.DisplayMetrics
            r0.<init>()
            r6 = r0
            java.lang.Object r0 = r15.getSystemService(r2)
            r34 = r0
            android.view.WindowManager r34 = (android.view.WindowManager) r34
            android.view.Display r0 = r34.getDefaultDisplay()
            r0.getMetrics(r6)
            android.content.res.Resources r0 = r15.getResources()
            r0.updateConfiguration(r4, r6)
            android.content.res.Resources$Theme r43 = r15.getTheme()
            int r0 = r43.getChangingConfigurations()
            if (r0 == 0) goto L_0x100f
            r43.rebase()
        L_0x100f:
            java.lang.String r0 = "MakePowerManagerServiceReady"
            traceBeginAndSlog(r0)
            com.android.server.power.PowerManagerService r0 = r5.mPowerManagerService     // Catch:{ all -> 0x1020 }
            com.android.server.am.ActivityManagerService r2 = r5.mActivityManagerService     // Catch:{ all -> 0x1020 }
            com.android.internal.app.IAppOpsService r2 = r2.getAppOpsService()     // Catch:{ all -> 0x1020 }
            r0.systemReady(r2)     // Catch:{ all -> 0x1020 }
            goto L_0x1027
        L_0x1020:
            r0 = move-exception
            java.lang.String r2 = "making Power Manager Service ready"
            r5.reportWtf(r2, r0)
        L_0x1027:
            traceEnd()
            java.lang.String r0 = "StartPermissionPolicyService"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            java.lang.Class<com.android.server.policy.PermissionPolicyService> r2 = com.android.server.policy.PermissionPolicyService.class
            r0.startService(r2)
            traceEnd()
            java.lang.String r0 = "MakePackageManagerServiceReady"
            traceBeginAndSlog(r0)
            com.android.server.pm.PackageManagerService r0 = r5.mPackageManagerService
            r0.systemReady()
            traceEnd()
            java.lang.String r0 = "MakeDisplayManagerServiceReady"
            traceBeginAndSlog(r0)
            com.android.server.display.DisplayManagerService r0 = r5.mDisplayManagerService     // Catch:{ all -> 0x1053 }
            boolean r2 = r5.mOnlyCore     // Catch:{ all -> 0x1053 }
            r0.systemReady(r12, r2)     // Catch:{ all -> 0x1053 }
            goto L_0x105a
        L_0x1053:
            r0 = move-exception
            java.lang.String r2 = "making Display Manager Service ready"
            r5.reportWtf(r2, r0)
        L_0x105a:
            traceEnd()
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            r0.setSafeMode(r12)
            java.lang.String r0 = "StartDeviceSpecificServices"
            traceBeginAndSlog(r0)
            android.content.Context r0 = r5.mSystemContext
            android.content.res.Resources r0 = r0.getResources()
            r2 = 17236009(0x1070029, float:2.47957E-38)
            java.lang.String[] r2 = r0.getStringArray(r2)
            int r7 = r2.length
        L_0x1075:
            if (r1 >= r7) goto L_0x10ac
            r8 = r2[r1]
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r10 = "StartDeviceSpecificServices "
            r0.append(r10)
            r0.append(r8)
            java.lang.String r0 = r0.toString()
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager     // Catch:{ all -> 0x1093 }
            r0.startService((java.lang.String) r8)     // Catch:{ all -> 0x1093 }
            goto L_0x10a6
        L_0x1093:
            r0 = move-exception
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            r10.append(r3)
            r10.append(r8)
            java.lang.String r10 = r10.toString()
            r5.reportWtf(r10, r0)
        L_0x10a6:
            traceEnd()
            int r1 = r1 + 1
            goto L_0x1075
        L_0x10ac:
            traceEnd()
            java.lang.String r0 = "StartBootPhaseDeviceSpecificServicesReady"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r5.mSystemServiceManager
            r1 = 520(0x208, float:7.29E-43)
            r0.startBootPhase(r1)
            traceEnd()
            r7 = r40
            r10 = r19
            r8 = r21
            r44 = r6
            r6 = r22
            r47 = r11
            r11 = r50
            r54 = r12
            r12 = r53
            r55 = r13
            r13 = r26
            r56 = r14
            r14 = r55
            r57 = r15
            r15 = r46
            r16 = r52
            r17 = r23
            r58 = r9
            r9 = r18
            r59 = r4
            r4 = r58
            com.android.server.am.ActivityManagerService r0 = r5.mActivityManagerService
            com.android.server.-$$Lambda$SystemServer$RIWNR87PnJ4Y6VHItRXU0J6ocqQ r3 = new com.android.server.-$$Lambda$SystemServer$RIWNR87PnJ4Y6VHItRXU0J6ocqQ
            r1 = r3
            r60 = r2
            r2 = r63
            r61 = r0
            r0 = r3
            r3 = r57
            r5 = r54
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17)
            android.util.TimingsTraceLog r1 = BOOT_TIMINGS_TRACE_LOG
            r2 = r61
            r2.systemReady(r0, r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.SystemServer.startOtherServices():void");
    }

    static /* synthetic */ void lambda$startOtherServices$1() {
        try {
            Slog.i(TAG, "SecondaryZygotePreload");
            TimingsTraceLog traceLog = new TimingsTraceLog(SYSTEM_SERVER_TIMING_ASYNC_TAG, 524288);
            traceLog.traceBegin("SecondaryZygotePreload");
            if (!Process.ZYGOTE_PROCESS.preloadDefault(Build.SUPPORTED_32_BIT_ABIS[0])) {
                Slog.e(TAG, "Unable to preload default resources");
            }
            traceLog.traceEnd();
        } catch (Exception ex) {
            Slog.e(TAG, "Exception preloading default resources", ex);
        }
    }

    static /* synthetic */ void lambda$startOtherServices$2() {
        TimingsTraceLog traceLog = new TimingsTraceLog(SYSTEM_SERVER_TIMING_ASYNC_TAG, 524288);
        traceLog.traceBegin(START_HIDL_SERVICES);
        startHidlServices();
        traceLog.traceEnd();
    }

    /* JADX WARNING: Removed duplicated region for block: B:100:0x01b3 A[SYNTHETIC, Splitter:B:100:0x01b3] */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x01cb A[SYNTHETIC, Splitter:B:106:0x01cb] */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x01e3 A[SYNTHETIC, Splitter:B:112:0x01e3] */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x0206 A[Catch:{ all -> 0x020a }] */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0153 A[SYNTHETIC, Splitter:B:76:0x0153] */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x016b A[SYNTHETIC, Splitter:B:82:0x016b] */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x0183 A[SYNTHETIC, Splitter:B:88:0x0183] */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x019b A[SYNTHETIC, Splitter:B:94:0x019b] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$startOtherServices$4$SystemServer(android.content.Context r10, com.android.server.wm.WindowManagerService r11, boolean r12, com.android.server.ConnectivityService r13, com.android.server.NetworkManagementService r14, com.android.server.net.NetworkPolicyManagerService r15, com.android.server.IpSecService r16, com.android.server.net.NetworkStatsService r17, com.android.server.LocationManagerService r18, com.android.server.CountryDetectorService r19, com.android.server.NetworkTimeUpdateService r20, com.android.server.input.InputManagerService r21, com.android.server.TelephonyRegistry r22, com.android.server.media.MediaRouterService r23, com.android.server.MmsServiceBroker r24) {
        /*
            r9 = this;
            r1 = r9
            r2 = r13
            r3 = r15
            java.lang.String r0 = "SystemServer"
            java.lang.String r4 = "Making services ready"
            android.util.Slog.i(r0, r4)
            java.lang.String r0 = "StartActivityManagerReadyPhase"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r1.mSystemServiceManager
            r4 = 550(0x226, float:7.71E-43)
            r0.startBootPhase(r4)
            traceEnd()
            java.lang.String r0 = "StartObservingNativeCrashes"
            traceBeginAndSlog(r0)
            com.android.server.am.ActivityManagerService r0 = r1.mActivityManagerService     // Catch:{ all -> 0x0024 }
            r0.startObservingNativeCrashes()     // Catch:{ all -> 0x0024 }
            goto L_0x002b
        L_0x0024:
            r0 = move-exception
            java.lang.String r4 = "observing native crashes"
            r9.reportWtf(r4, r0)
        L_0x002b:
            traceEnd()
            java.lang.String r4 = "WebViewFactoryPreparation"
            r0 = 0
            boolean r5 = r1.mOnlyCore
            java.lang.String r6 = "WebViewFactoryPreparation"
            if (r5 != 0) goto L_0x004a
            com.android.server.webkit.WebViewUpdateService r5 = r1.mWebViewUpdateService
            if (r5 == 0) goto L_0x004a
            com.android.server.SystemServerInitThreadPool r5 = com.android.server.SystemServerInitThreadPool.get()
            com.android.server.-$$Lambda$SystemServer$Y1gEdKr_Hb7K7cbTDAo_WOJ-SYI r7 = new com.android.server.-$$Lambda$SystemServer$Y1gEdKr_Hb7K7cbTDAo_WOJ-SYI
            r7.<init>()
            java.util.concurrent.Future r0 = r5.submit(r7, r6)
            r5 = r0
            goto L_0x004b
        L_0x004a:
            r5 = r0
        L_0x004b:
            android.content.pm.PackageManager r0 = r1.mPackageManager
            java.lang.String r7 = "android.hardware.type.automotive"
            boolean r0 = r0.hasSystemFeature(r7)
            if (r0 == 0) goto L_0x0064
            java.lang.String r0 = "StartCarServiceHelperService"
            traceBeginAndSlog(r0)
            com.android.server.SystemServiceManager r0 = r1.mSystemServiceManager
            java.lang.String r7 = "com.android.internal.car.CarServiceHelperService"
            r0.startService((java.lang.String) r7)
            traceEnd()
        L_0x0064:
            java.lang.String r0 = "StartSystemUI"
            traceBeginAndSlog(r0)
            startSystemUi(r10, r11)     // Catch:{ all -> 0x006d }
            goto L_0x0076
        L_0x006d:
            r0 = move-exception
            r7 = r0
            r0 = r7
            java.lang.String r7 = "starting System UI"
            r9.reportWtf(r7, r0)
        L_0x0076:
            traceEnd()
            if (r12 == 0) goto L_0x0090
            java.lang.String r0 = "EnableAirplaneModeInSafeMode"
            traceBeginAndSlog(r0)
            r0 = 1
            r13.setAirplaneMode(r0)     // Catch:{ all -> 0x0085 }
            goto L_0x008d
        L_0x0085:
            r0 = move-exception
            r7 = r0
            r0 = r7
            java.lang.String r7 = "enabling Airplane Mode during Safe Mode bootup"
            r9.reportWtf(r7, r0)
        L_0x008d:
            traceEnd()
        L_0x0090:
            java.lang.String r0 = "MakeNetworkManagementServiceReady"
            traceBeginAndSlog(r0)
            if (r14 == 0) goto L_0x00a5
            r14.systemReady()     // Catch:{ all -> 0x009b }
            goto L_0x00a5
        L_0x009b:
            r0 = move-exception
            r7 = r0
            r0 = r7
            java.lang.String r7 = "making Network Managment Service ready"
            r9.reportWtf(r7, r0)
            goto L_0x00a6
        L_0x00a5:
        L_0x00a6:
            r0 = 0
            if (r3 == 0) goto L_0x00b0
            java.util.concurrent.CountDownLatch r0 = r15.networkScoreAndNetworkManagementServiceReady()
            r7 = r0
            goto L_0x00b1
        L_0x00b0:
            r7 = r0
        L_0x00b1:
            traceEnd()
            java.lang.String r0 = "MakeIpSecServiceReady"
            traceBeginAndSlog(r0)
            if (r16 == 0) goto L_0x00c9
            r16.systemReady()     // Catch:{ all -> 0x00bf }
            goto L_0x00c9
        L_0x00bf:
            r0 = move-exception
            r8 = r0
            r0 = r8
            java.lang.String r8 = "making IpSec Service ready"
            r9.reportWtf(r8, r0)
            goto L_0x00ca
        L_0x00c9:
        L_0x00ca:
            traceEnd()
            java.lang.String r0 = "MakeNetworkStatsServiceReady"
            traceBeginAndSlog(r0)
            if (r17 == 0) goto L_0x00e2
            r17.systemReady()     // Catch:{ all -> 0x00d8 }
            goto L_0x00e2
        L_0x00d8:
            r0 = move-exception
            r8 = r0
            r0 = r8
            java.lang.String r8 = "making Network Stats Service ready"
            r9.reportWtf(r8, r0)
            goto L_0x00e3
        L_0x00e2:
        L_0x00e3:
            traceEnd()
            java.lang.String r0 = "MakeConnectivityServiceReady"
            traceBeginAndSlog(r0)
            if (r2 == 0) goto L_0x00fb
            r13.systemReady()     // Catch:{ all -> 0x00f1 }
            goto L_0x00fb
        L_0x00f1:
            r0 = move-exception
            r8 = r0
            r0 = r8
            java.lang.String r8 = "making Connectivity Service ready"
            r9.reportWtf(r8, r0)
            goto L_0x00fc
        L_0x00fb:
        L_0x00fc:
            traceEnd()
            java.lang.String r0 = "MakeNetworkPolicyServiceReady"
            traceBeginAndSlog(r0)
            if (r3 == 0) goto L_0x0114
            r15.systemReady(r7)     // Catch:{ all -> 0x010a }
            goto L_0x0114
        L_0x010a:
            r0 = move-exception
            r8 = r0
            r0 = r8
            java.lang.String r8 = "making Network Policy Service ready"
            r9.reportWtf(r8, r0)
            goto L_0x0115
        L_0x0114:
        L_0x0115:
            traceEnd()
            com.android.server.pm.PackageManagerService r0 = r1.mPackageManagerService
            r0.waitForAppDataPrepared()
            java.lang.String r0 = "PhaseThirdPartyAppsCanStart"
            traceBeginAndSlog(r0)
            if (r5 == 0) goto L_0x0127
            com.android.internal.util.ConcurrentUtils.waitForFutureNoInterrupt(r5, r6)
        L_0x0127:
            com.android.server.SystemServiceManager r0 = r1.mSystemServiceManager
            r6 = 600(0x258, float:8.41E-43)
            r0.startBootPhase(r6)
            traceEnd()
            java.lang.String r0 = "StartNetworkStack"
            traceBeginAndSlog(r0)
            android.net.NetworkStackClient r0 = android.net.NetworkStackClient.getInstance()     // Catch:{ all -> 0x0141 }
            r6 = r10
            r0.start(r10)     // Catch:{ all -> 0x013f }
            goto L_0x0149
        L_0x013f:
            r0 = move-exception
            goto L_0x0143
        L_0x0141:
            r0 = move-exception
            r6 = r10
        L_0x0143:
            java.lang.String r8 = "starting Network Stack"
            r9.reportWtf(r8, r0)
        L_0x0149:
            traceEnd()
            java.lang.String r0 = "MakeLocationServiceReady"
            traceBeginAndSlog(r0)
            if (r18 == 0) goto L_0x0160
            r18.systemRunning()     // Catch:{ all -> 0x0157 }
            goto L_0x0160
        L_0x0157:
            r0 = move-exception
            r8 = r0
            r0 = r8
            java.lang.String r8 = "Notifying Location Service running"
            r9.reportWtf(r8, r0)
            goto L_0x0161
        L_0x0160:
        L_0x0161:
            traceEnd()
            java.lang.String r0 = "MakeCountryDetectionServiceReady"
            traceBeginAndSlog(r0)
            if (r19 == 0) goto L_0x0178
            r19.systemRunning()     // Catch:{ all -> 0x016f }
            goto L_0x0178
        L_0x016f:
            r0 = move-exception
            r8 = r0
            r0 = r8
            java.lang.String r8 = "Notifying CountryDetectorService running"
            r9.reportWtf(r8, r0)
            goto L_0x0179
        L_0x0178:
        L_0x0179:
            traceEnd()
            java.lang.String r0 = "MakeNetworkTimeUpdateReady"
            traceBeginAndSlog(r0)
            if (r20 == 0) goto L_0x0190
            r20.systemRunning()     // Catch:{ all -> 0x0187 }
            goto L_0x0190
        L_0x0187:
            r0 = move-exception
            r8 = r0
            r0 = r8
            java.lang.String r8 = "Notifying NetworkTimeService running"
            r9.reportWtf(r8, r0)
            goto L_0x0191
        L_0x0190:
        L_0x0191:
            traceEnd()
            java.lang.String r0 = "MakeInputManagerServiceReady"
            traceBeginAndSlog(r0)
            if (r21 == 0) goto L_0x01a8
            r21.systemRunning()     // Catch:{ all -> 0x019f }
            goto L_0x01a8
        L_0x019f:
            r0 = move-exception
            r8 = r0
            r0 = r8
            java.lang.String r8 = "Notifying InputManagerService running"
            r9.reportWtf(r8, r0)
            goto L_0x01a9
        L_0x01a8:
        L_0x01a9:
            traceEnd()
            java.lang.String r0 = "MakeTelephonyRegistryReady"
            traceBeginAndSlog(r0)
            if (r22 == 0) goto L_0x01c0
            r22.systemRunning()     // Catch:{ all -> 0x01b7 }
            goto L_0x01c0
        L_0x01b7:
            r0 = move-exception
            r8 = r0
            r0 = r8
            java.lang.String r8 = "Notifying TelephonyRegistry running"
            r9.reportWtf(r8, r0)
            goto L_0x01c1
        L_0x01c0:
        L_0x01c1:
            traceEnd()
            java.lang.String r0 = "MakeMediaRouterServiceReady"
            traceBeginAndSlog(r0)
            if (r23 == 0) goto L_0x01d8
            r23.systemRunning()     // Catch:{ all -> 0x01cf }
            goto L_0x01d8
        L_0x01cf:
            r0 = move-exception
            r8 = r0
            r0 = r8
            java.lang.String r8 = "Notifying MediaRouterService running"
            r9.reportWtf(r8, r0)
            goto L_0x01d9
        L_0x01d8:
        L_0x01d9:
            traceEnd()
            java.lang.String r0 = "MakeMmsServiceReady"
            traceBeginAndSlog(r0)
            if (r24 == 0) goto L_0x01f0
            r24.systemRunning()     // Catch:{ all -> 0x01e7 }
            goto L_0x01f0
        L_0x01e7:
            r0 = move-exception
            r8 = r0
            r0 = r8
            java.lang.String r8 = "Notifying MmsService running"
            r9.reportWtf(r8, r0)
            goto L_0x01f1
        L_0x01f0:
        L_0x01f1:
            traceEnd()
            java.lang.String r0 = "IncidentDaemonReady"
            traceBeginAndSlog(r0)
            java.lang.String r0 = "incident"
            android.os.IBinder r0 = android.os.ServiceManager.getService(r0)     // Catch:{ all -> 0x020a }
            android.os.IIncidentManager r0 = android.os.IIncidentManager.Stub.asInterface(r0)     // Catch:{ all -> 0x020a }
            if (r0 == 0) goto L_0x0209
            r0.systemRunning()     // Catch:{ all -> 0x020a }
        L_0x0209:
            goto L_0x0210
        L_0x020a:
            r0 = move-exception
            java.lang.String r8 = "Notifying incident daemon running"
            r9.reportWtf(r8, r0)
        L_0x0210:
            traceEnd()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.SystemServer.lambda$startOtherServices$4$SystemServer(android.content.Context, com.android.server.wm.WindowManagerService, boolean, com.android.server.ConnectivityService, com.android.server.NetworkManagementService, com.android.server.net.NetworkPolicyManagerService, com.android.server.IpSecService, com.android.server.net.NetworkStatsService, com.android.server.LocationManagerService, com.android.server.CountryDetectorService, com.android.server.NetworkTimeUpdateService, com.android.server.input.InputManagerService, com.android.server.TelephonyRegistry, com.android.server.media.MediaRouterService, com.android.server.MmsServiceBroker):void");
    }

    public /* synthetic */ void lambda$startOtherServices$3$SystemServer() {
        Slog.i(TAG, "WebViewFactoryPreparation");
        TimingsTraceLog traceLog = new TimingsTraceLog(SYSTEM_SERVER_TIMING_ASYNC_TAG, 524288);
        traceLog.traceBegin("WebViewFactoryPreparation");
        ConcurrentUtils.waitForFutureNoInterrupt(this.mZygotePreload, "Zygote preload");
        this.mZygotePreload = null;
        this.mWebViewUpdateService.prepareWebViewInSystemServer();
        traceLog.traceEnd();
    }

    private void startSystemCaptionsManagerService(Context context) {
        if (TextUtils.isEmpty(context.getString(17039734))) {
            Slog.d(TAG, "SystemCaptionsManagerService disabled because resource is not overlaid");
            return;
        }
        traceBeginAndSlog("StartSystemCaptionsManagerService");
        this.mSystemServiceManager.startService(SYSTEM_CAPTIONS_MANAGER_SERVICE_CLASS);
        traceEnd();
    }

    private void startContentCaptureService(Context context) {
        ActivityManagerService activityManagerService;
        boolean explicitlyEnabled = false;
        String settings = DeviceConfig.getProperty("content_capture", "service_explicitly_enabled");
        if (settings != null && !settings.equalsIgnoreCase(BatteryService.HealthServiceWrapper.INSTANCE_VENDOR)) {
            explicitlyEnabled = Boolean.parseBoolean(settings);
            if (explicitlyEnabled) {
                Slog.d(TAG, "ContentCaptureService explicitly enabled by DeviceConfig");
            } else {
                Slog.d(TAG, "ContentCaptureService explicitly disabled by DeviceConfig");
                return;
            }
        }
        if (explicitlyEnabled || !TextUtils.isEmpty(context.getString(17039725))) {
            traceBeginAndSlog("StartContentCaptureService");
            this.mSystemServiceManager.startService(CONTENT_CAPTURE_MANAGER_SERVICE_CLASS);
            ContentCaptureManagerInternal ccmi = (ContentCaptureManagerInternal) LocalServices.getService(ContentCaptureManagerInternal.class);
            if (!(ccmi == null || (activityManagerService = this.mActivityManagerService) == null)) {
                activityManagerService.setContentCaptureManager(ccmi);
            }
            traceEnd();
            return;
        }
        Slog.d(TAG, "ContentCaptureService disabled because resource is not overlaid");
    }

    private void startAttentionService(Context context) {
        if (!AttentionManagerService.isServiceConfigured(context)) {
            Slog.d(TAG, "AttentionService is not configured on this device");
            return;
        }
        traceBeginAndSlog("StartAttentionManagerService");
        this.mSystemServiceManager.startService(AttentionManagerService.class);
        traceEnd();
    }

    private static void startSystemUi(Context context, WindowManagerService windowManager) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(AccessController.PACKAGE_SYSTEMUI, "com.android.systemui.SystemUIService"));
        intent.addFlags(256);
        context.startServiceAsUser(intent, UserHandle.SYSTEM);
        windowManager.onSystemUiStarted();
    }

    private static void traceBeginAndSlog(String name) {
        Slog.i(TAG, name);
        BOOT_TIMINGS_TRACE_LOG.traceBegin(name);
    }

    private static void traceEnd() {
        BOOT_TIMINGS_TRACE_LOG.traceEnd();
    }
}
