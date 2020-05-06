package com.android.server;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.PackageManagerInternal;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.hardware.location.ActivityRecognitionHardware;
import android.location.Address;
import android.location.Criteria;
import android.location.GeocoderParams;
import android.location.Geofence;
import android.location.GnssMeasurementCorrections;
import android.location.IBatchedLocationCallback;
import android.location.IFusedGeofenceHardware;
import android.location.IGnssMeasurementsListener;
import android.location.IGnssNavigationMessageListener;
import android.location.IGnssStatusListener;
import android.location.IGpsGeofenceHardware;
import android.location.ILocationListener;
import android.location.ILocationManager;
import android.location.INetInitiatedListener;
import android.location.Location;
import android.location.LocationRequest;
import android.location.LocationTime;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.PowerManager;
import android.os.PowerManagerInternal;
import android.os.PowerSaveState;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.WorkSource;
import android.provider.Settings;
import android.server.am.SplitScreenReporter;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.EventLog;
import android.util.Log;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.content.PackageMonitor;
import com.android.internal.location.ProviderProperties;
import com.android.internal.location.ProviderRequest;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.Preconditions;
import com.android.server.LocationManagerService;
import com.android.server.location.AbstractLocationProvider;
import com.android.server.location.ActivityRecognitionProxy;
import com.android.server.location.CallerIdentity;
import com.android.server.location.GeocoderProxy;
import com.android.server.location.GeofenceManager;
import com.android.server.location.GeofenceProxy;
import com.android.server.location.GnssBatchingProvider;
import com.android.server.location.GnssCapabilitiesProvider;
import com.android.server.location.GnssLocalLog;
import com.android.server.location.GnssLocationProvider;
import com.android.server.location.GnssMeasurementCorrectionsProvider;
import com.android.server.location.GnssMeasurementsProvider;
import com.android.server.location.GnssNavigationMessageProvider;
import com.android.server.location.GnssStatusListenerHelper;
import com.android.server.location.LocationBlacklist;
import com.android.server.location.LocationFudger;
import com.android.server.location.LocationProviderProxy;
import com.android.server.location.LocationRequestStatistics;
import com.android.server.location.MockProvider;
import com.android.server.location.PassiveProvider;
import com.android.server.location.RemoteListenerHelper;
import com.android.server.wm.MultiWindowTransition;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;

public class LocationManagerService extends ILocationManager.Stub {
    private static final String ACCESS_LOCATION_EXTRA_COMMANDS = "android.permission.ACCESS_LOCATION_EXTRA_COMMANDS";
    public static final boolean D = Log.isLoggable(TAG, 3);
    private static final long DEFAULT_BACKGROUND_THROTTLE_INTERVAL_MS = 1800000;
    private static final long DEFAULT_LAST_LOCATION_MAX_AGE_MS = 1200000;
    private static final LocationRequest DEFAULT_LOCATION_REQUEST = new LocationRequest();
    private static final int FOREGROUND_IMPORTANCE_CUTOFF = 125;
    private static final String FUSED_LOCATION_SERVICE_ACTION = "com.android.location.service.FusedLocationProvider";
    private static final long HIGH_POWER_INTERVAL_MS = 300000;
    private static final int MAX_PROVIDER_SCHEDULING_JITTER_MS = 100;
    private static final long NANOS_PER_MILLI = 1000000;
    private static final String NETWORK_LOCATION_SERVICE_ACTION = "com.android.location.service.v3.NetworkLocationProvider";
    private static final int RESOLUTION_LEVEL_COARSE = 1;
    private static final int RESOLUTION_LEVEL_FINE = 2;
    private static final int RESOLUTION_LEVEL_NONE = 0;
    private static final String TAG = "LocationManagerService";
    private static final String WAKELOCK_KEY = "*location*";
    /* access modifiers changed from: private */
    public ActivityManager mActivityManager;
    /* access modifiers changed from: private */
    public AppOpsManager mAppOps;
    private final ArraySet<String> mBackgroundThrottlePackageWhitelist = new ArraySet<>();
    @GuardedBy({"mLock"})
    private int mBatterySaverMode;
    private LocationBlacklist mBlacklist;
    private String mComboNlpPackageName;
    private String mComboNlpReadyMarker;
    private String mComboNlpScreenMarker;
    /* access modifiers changed from: private */
    public final Context mContext;
    /* access modifiers changed from: private */
    public int mCurrentUserId = 0;
    private int[] mCurrentUserProfiles = {0};
    private final GnssLocalLog mDumpStatus = new GnssLocalLog(1000);
    @GuardedBy({"mLock"})
    private String mExtraLocationControllerPackage;
    private boolean mExtraLocationControllerPackageEnabled;
    private GeocoderProxy mGeocodeProvider;
    private GeofenceManager mGeofenceManager;
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public IBatchedLocationCallback mGnssBatchingCallback;
    @GuardedBy({"mLock"})
    private LinkedListener<IBatchedLocationCallback> mGnssBatchingDeathCallback;
    @GuardedBy({"mLock"})
    private boolean mGnssBatchingInProgress = false;
    private GnssBatchingProvider mGnssBatchingProvider;
    private GnssCapabilitiesProvider mGnssCapabilitiesProvider;
    private GnssMeasurementCorrectionsProvider mGnssMeasurementCorrectionsProvider;
    @GuardedBy({"mLock"})
    private final ArrayMap<IBinder, LinkedListener<IGnssMeasurementsListener>> mGnssMeasurementsListeners = new ArrayMap<>();
    private GnssMeasurementsProvider mGnssMeasurementsProvider;
    private GnssLocationProvider.GnssMetricsProvider mGnssMetricsProvider;
    @GuardedBy({"mLock"})
    private final ArrayMap<IBinder, LinkedListener<IGnssNavigationMessageListener>> mGnssNavigationMessageListeners = new ArrayMap<>();
    private GnssNavigationMessageProvider mGnssNavigationMessageProvider;
    @GuardedBy({"mLock"})
    private final ArrayMap<IBinder, LinkedListener<IGnssStatusListener>> mGnssStatusListeners = new ArrayMap<>();
    private GnssStatusListenerHelper mGnssStatusProvider;
    private GnssLocationProvider.GnssSystemInfoProvider mGnssSystemInfoProvider;
    private IGpsGeofenceHardware mGpsGeofenceProxy;
    /* access modifiers changed from: private */
    public final Handler mHandler;
    private final ArraySet<String> mIgnoreSettingsPackageWhitelist = new ArraySet<>();
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public final HashMap<String, Location> mLastLocation = new HashMap<>();
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public final HashMap<String, Location> mLastLocationCoarseInterval = new HashMap<>();
    private LocationFudger mLocationFudger;
    private final LocationPolicyManagerService mLocationPolicyService;
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public final LocationUsageLogger mLocationUsageLogger;
    /* access modifiers changed from: private */
    public final Object mLock = new Object();
    private INetInitiatedListener mNetInitiatedListener;
    private PackageManager mPackageManager;
    /* access modifiers changed from: private */
    public PassiveProvider mPassiveProvider;
    /* access modifiers changed from: private */
    public PowerManager mPowerManager;
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public final ArrayList<LocationProvider> mProviders = new ArrayList<>();
    @GuardedBy({"mLock"})
    private final ArrayList<LocationProvider> mRealProviders = new ArrayList<>();
    @GuardedBy({"mLock"})
    private final HashMap<Object, Receiver> mReceivers = new HashMap<>();
    /* access modifiers changed from: private */
    public final HashMap<String, ArrayList<UpdateRecord>> mRecordsByProvider = new HashMap<>();
    /* access modifiers changed from: private */
    public final LocationRequestStatistics mRequestStatistics = new LocationRequestStatistics();
    private UserManager mUserManager;

    public LocationManagerService(Context context) {
        this.mContext = context;
        this.mHandler = FgThread.getHandler();
        this.mLocationUsageLogger = new LocationUsageLogger();
        PackageManagerInternal packageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        packageManagerInternal.setLocationPackagesProvider(new PackageManagerInternal.PackagesProvider() {
            public final String[] getPackages(int i) {
                return LocationManagerService.this.lambda$new$0$LocationManagerService(i);
            }
        });
        packageManagerInternal.setLocationExtraPackagesProvider(new PackageManagerInternal.PackagesProvider() {
            public final String[] getPackages(int i) {
                return LocationManagerService.this.lambda$new$1$LocationManagerService(i);
            }
        });
        LocationManagerServiceInjector.init(this, this.mLock, this.mContext, this.mRealProviders, this.mReceivers);
        this.mLocationPolicyService = LocationPolicyManagerService.newDefaultService(context, this);
    }

    public /* synthetic */ String[] lambda$new$0$LocationManagerService(int userId) {
        return this.mContext.getResources().getStringArray(17236036);
    }

    public /* synthetic */ String[] lambda$new$1$LocationManagerService(int userId) {
        return this.mContext.getResources().getStringArray(17236035);
    }

    /* access modifiers changed from: package-private */
    public boolean callIsCurrentProfile(int userId) {
        boolean ret;
        synchronized (this.mLock) {
            ret = isCurrentProfileLocked(userId);
        }
        return ret;
    }

    public void systemRunning() {
        synchronized (this.mLock) {
            initializeLocked();
        }
    }

    /* JADX WARNING: type inference failed for: r1v7, types: [android.app.AppOpsManager$OnOpChangedListener, com.android.server.LocationManagerService$1] */
    @GuardedBy({"mLock"})
    private void initializeLocked() {
        this.mAppOps = (AppOpsManager) this.mContext.getSystemService("appops");
        this.mPackageManager = this.mContext.getPackageManager();
        this.mPowerManager = (PowerManager) this.mContext.getSystemService("power");
        this.mActivityManager = (ActivityManager) this.mContext.getSystemService("activity");
        this.mUserManager = (UserManager) this.mContext.getSystemService("user");
        this.mLocationFudger = new LocationFudger(this.mContext, this.mHandler);
        this.mBlacklist = new LocationBlacklist(this.mContext, this.mHandler);
        this.mBlacklist.init();
        this.mGeofenceManager = new GeofenceManager(this.mContext, this.mBlacklist);
        initializeProvidersLocked();
        LocationManagerServiceInjector.updateGpsStatusProvider(this.mGnssStatusProvider);
        this.mAppOps.startWatchingMode(0, (String) null, 1, new AppOpsManager.OnOpChangedInternalListener() {
            public void onOpChanged(int op, String packageName) {
                LocationManagerService.this.mHandler.post(new Runnable() {
                    public final void run() {
                        LocationManagerService.AnonymousClass1.this.lambda$onOpChanged$0$LocationManagerService$1();
                    }
                });
            }

            public /* synthetic */ void lambda$onOpChanged$0$LocationManagerService$1() {
                synchronized (LocationManagerService.this.mLock) {
                    LocationManagerService.this.onAppOpChangedLocked();
                }
            }
        });
        this.mPackageManager.addOnPermissionsChangeListener(new PackageManager.OnPermissionsChangedListener() {
            public final void onPermissionsChanged(int i) {
                LocationManagerService.this.lambda$initializeLocked$3$LocationManagerService(i);
            }
        });
        this.mActivityManager.addOnUidImportanceListener(new ActivityManager.OnUidImportanceListener() {
            public final void onUidImportance(int i, int i2) {
                LocationManagerService.this.lambda$initializeLocked$5$LocationManagerService(i, i2);
            }
        }, 125);
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("location_mode"), true, new ContentObserver(this.mHandler) {
            public void onChange(boolean selfChange) {
                synchronized (LocationManagerService.this.mLock) {
                    LocationManagerService.this.onLocationModeChangedLocked(true);
                }
            }
        }, -1);
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("location_providers_allowed"), true, new ContentObserver(this.mHandler) {
            public void onChange(boolean selfChange) {
                synchronized (LocationManagerService.this.mLock) {
                    LocationManagerService.this.onProviderAllowedChangedLocked();
                }
            }
        }, -1);
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("location_background_throttle_interval_ms"), true, new ContentObserver(this.mHandler) {
            public void onChange(boolean selfChange) {
                synchronized (LocationManagerService.this.mLock) {
                    LocationManagerService.this.onBackgroundThrottleIntervalChangedLocked();
                }
            }
        }, -1);
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("location_background_throttle_package_whitelist"), true, new ContentObserver(this.mHandler) {
            public void onChange(boolean selfChange) {
                synchronized (LocationManagerService.this.mLock) {
                    LocationManagerService.this.onBackgroundThrottleWhitelistChangedLocked();
                }
            }
        }, -1);
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("location_ignore_settings_package_whitelist"), true, new ContentObserver(this.mHandler) {
            public void onChange(boolean selfChange) {
                synchronized (LocationManagerService.this.mLock) {
                    LocationManagerService.this.onIgnoreSettingsWhitelistChangedLocked();
                }
            }
        }, -1);
        ((PowerManagerInternal) LocalServices.getService(PowerManagerInternal.class)).registerLowPowerModeObserver(1, new Consumer() {
            public final void accept(Object obj) {
                LocationManagerService.this.lambda$initializeLocked$7$LocationManagerService((PowerSaveState) obj);
            }
        });
        new PackageMonitor() {
            public void onPackageDisappeared(String packageName, int reason) {
                synchronized (LocationManagerService.this.mLock) {
                    LocationManagerService.this.onPackageDisappearedLocked(packageName);
                }
            }
        }.register(this.mContext, this.mHandler.getLooper(), true);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.USER_SWITCHED");
        intentFilter.addAction("android.intent.action.MANAGED_PROFILE_ADDED");
        intentFilter.addAction("android.intent.action.MANAGED_PROFILE_REMOVED");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        this.mContext.registerReceiverAsUser(new BroadcastReceiver() {
            /* JADX WARNING: Can't fix incorrect switch cases order */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onReceive(android.content.Context r10, android.content.Intent r11) {
                /*
                    r9 = this;
                    java.lang.String r0 = r11.getAction()
                    if (r0 != 0) goto L_0x0007
                    return
                L_0x0007:
                    com.android.server.LocationManagerService r1 = com.android.server.LocationManagerService.this
                    java.lang.Object r1 = r1.mLock
                    monitor-enter(r1)
                    r2 = -1
                    int r3 = r0.hashCode()     // Catch:{ all -> 0x0072 }
                    r4 = 0
                    r5 = 4
                    r6 = 3
                    r7 = 2
                    r8 = 1
                    switch(r3) {
                        case -2128145023: goto L_0x0044;
                        case -1454123155: goto L_0x003a;
                        case -385593787: goto L_0x0030;
                        case 959232034: goto L_0x0026;
                        case 1051477093: goto L_0x001c;
                        default: goto L_0x001b;
                    }     // Catch:{ all -> 0x0072 }
                L_0x001b:
                    goto L_0x004d
                L_0x001c:
                    java.lang.String r3 = "android.intent.action.MANAGED_PROFILE_REMOVED"
                    boolean r3 = r0.equals(r3)     // Catch:{ all -> 0x0072 }
                    if (r3 == 0) goto L_0x001b
                    r2 = r7
                    goto L_0x004d
                L_0x0026:
                    java.lang.String r3 = "android.intent.action.USER_SWITCHED"
                    boolean r3 = r0.equals(r3)     // Catch:{ all -> 0x0072 }
                    if (r3 == 0) goto L_0x001b
                    r2 = r4
                    goto L_0x004d
                L_0x0030:
                    java.lang.String r3 = "android.intent.action.MANAGED_PROFILE_ADDED"
                    boolean r3 = r0.equals(r3)     // Catch:{ all -> 0x0072 }
                    if (r3 == 0) goto L_0x001b
                    r2 = r8
                    goto L_0x004d
                L_0x003a:
                    java.lang.String r3 = "android.intent.action.SCREEN_ON"
                    boolean r3 = r0.equals(r3)     // Catch:{ all -> 0x0072 }
                    if (r3 == 0) goto L_0x001b
                    r2 = r6
                    goto L_0x004d
                L_0x0044:
                    java.lang.String r3 = "android.intent.action.SCREEN_OFF"
                    boolean r3 = r0.equals(r3)     // Catch:{ all -> 0x0072 }
                    if (r3 == 0) goto L_0x001b
                    r2 = r5
                L_0x004d:
                    if (r2 == 0) goto L_0x0064
                    if (r2 == r8) goto L_0x005e
                    if (r2 == r7) goto L_0x005e
                    if (r2 == r6) goto L_0x0058
                    if (r2 == r5) goto L_0x0058
                    goto L_0x0070
                L_0x0058:
                    com.android.server.LocationManagerService r2 = com.android.server.LocationManagerService.this     // Catch:{ all -> 0x0072 }
                    r2.onScreenStateChangedLocked()     // Catch:{ all -> 0x0072 }
                    goto L_0x0070
                L_0x005e:
                    com.android.server.LocationManagerService r2 = com.android.server.LocationManagerService.this     // Catch:{ all -> 0x0072 }
                    r2.onUserProfilesChangedLocked()     // Catch:{ all -> 0x0072 }
                    goto L_0x0070
                L_0x0064:
                    com.android.server.LocationManagerService r2 = com.android.server.LocationManagerService.this     // Catch:{ all -> 0x0072 }
                    java.lang.String r3 = "android.intent.extra.user_handle"
                    int r3 = r11.getIntExtra(r3, r4)     // Catch:{ all -> 0x0072 }
                    r2.onUserChangedLocked(r3)     // Catch:{ all -> 0x0072 }
                L_0x0070:
                    monitor-exit(r1)     // Catch:{ all -> 0x0072 }
                    return
                L_0x0072:
                    r2 = move-exception
                    monitor-exit(r1)     // Catch:{ all -> 0x0072 }
                    throw r2
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.server.LocationManagerService.AnonymousClass8.onReceive(android.content.Context, android.content.Intent):void");
            }
        }, UserHandle.ALL, intentFilter, (String) null, this.mHandler);
        this.mCurrentUserId = ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION;
        onUserChangedLocked(0);
        onBackgroundThrottleWhitelistChangedLocked();
        onIgnoreSettingsWhitelistChangedLocked();
        onBatterySaverModeChangedLocked(this.mPowerManager.getLocationPowerSaveMode());
        this.mLocationPolicyService.systemRunning();
        LocationManagerServiceInjector.bindLocationPolicyService(this.mLocationPolicyService);
        LocationManagerServiceInjector.updateGpsStatusProvider(this.mGnssStatusProvider);
    }

    public /* synthetic */ void lambda$initializeLocked$3$LocationManagerService(int uid) {
        this.mHandler.post(new Runnable() {
            public final void run() {
                LocationManagerService.this.lambda$initializeLocked$2$LocationManagerService();
            }
        });
    }

    public /* synthetic */ void lambda$initializeLocked$2$LocationManagerService() {
        synchronized (this.mLock) {
            onPermissionsChangedLocked();
        }
    }

    public /* synthetic */ void lambda$initializeLocked$5$LocationManagerService(int uid, int importance) {
        this.mHandler.post(new Runnable(uid, importance) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                LocationManagerService.this.lambda$initializeLocked$4$LocationManagerService(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$initializeLocked$4$LocationManagerService(int uid, int importance) {
        synchronized (this.mLock) {
            onUidImportanceChangedLocked(uid, importance);
        }
    }

    public /* synthetic */ void lambda$initializeLocked$7$LocationManagerService(PowerSaveState state) {
        this.mHandler.post(new Runnable(state) {
            private final /* synthetic */ PowerSaveState f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                LocationManagerService.this.lambda$initializeLocked$6$LocationManagerService(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$initializeLocked$6$LocationManagerService(PowerSaveState state) {
        synchronized (this.mLock) {
            onBatterySaverModeChangedLocked(state.locationMode);
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public void onAppOpChangedLocked() {
        for (Receiver receiver : this.mReceivers.values()) {
            receiver.updateMonitoring(true);
        }
        Iterator<LocationProvider> it = this.mProviders.iterator();
        while (it.hasNext()) {
            applyRequirementsLocked(it.next());
        }
    }

    @GuardedBy({"mLock"})
    private void onPermissionsChangedLocked() {
        Iterator<LocationProvider> it = this.mProviders.iterator();
        while (it.hasNext()) {
            applyRequirementsLocked(it.next());
        }
    }

    @GuardedBy({"mLock"})
    private void onBatterySaverModeChangedLocked(int newLocationMode) {
        if (D) {
            Slog.d(TAG, "Battery Saver location mode changed from " + PowerManager.locationPowerSaveModeToString(this.mBatterySaverMode) + " to " + PowerManager.locationPowerSaveModeToString(newLocationMode));
        }
        if (this.mBatterySaverMode != newLocationMode) {
            this.mBatterySaverMode = newLocationMode;
            Iterator<LocationProvider> it = this.mProviders.iterator();
            while (it.hasNext()) {
                applyRequirementsLocked(it.next());
            }
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public void onScreenStateChangedLocked() {
        if (this.mBatterySaverMode == 4) {
            Iterator<LocationProvider> it = this.mProviders.iterator();
            while (it.hasNext()) {
                applyRequirementsLocked(it.next());
            }
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public void onLocationModeChangedLocked(boolean broadcast) {
        if (D) {
            Log.d(TAG, "location enabled is now " + isLocationEnabled());
        }
        Iterator<LocationProvider> it = this.mProviders.iterator();
        while (it.hasNext()) {
            it.next().onLocationModeChangedLocked();
        }
        if (broadcast) {
            this.mContext.sendBroadcastAsUser(new Intent("android.location.MODE_CHANGED"), UserHandle.ALL);
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public void onProviderAllowedChangedLocked() {
        Iterator<LocationProvider> it = this.mProviders.iterator();
        while (it.hasNext()) {
            it.next().onAllowedChangedLocked();
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public void onPackageDisappearedLocked(String packageName) {
        ArrayList<Receiver> deadReceivers = null;
        for (Receiver receiver : this.mReceivers.values()) {
            if (receiver.mCallerIdentity.mPackageName.equals(packageName)) {
                if (deadReceivers == null) {
                    deadReceivers = new ArrayList<>();
                }
                deadReceivers.add(receiver);
            }
        }
        if (deadReceivers != null) {
            Iterator<Receiver> it = deadReceivers.iterator();
            while (it.hasNext()) {
                removeUpdatesLocked(it.next());
            }
        }
    }

    @GuardedBy({"mLock"})
    private void onUidImportanceChangedLocked(int uid, int importance) {
        boolean foreground = isImportanceForeground(importance);
        HashSet hashSet = new HashSet(this.mRecordsByProvider.size());
        for (Map.Entry<String, ArrayList<UpdateRecord>> entry : this.mRecordsByProvider.entrySet()) {
            String provider = entry.getKey();
            Iterator it = entry.getValue().iterator();
            while (it.hasNext()) {
                UpdateRecord record = (UpdateRecord) it.next();
                if (record.mReceiver.mCallerIdentity.mUid == uid && record.mIsForegroundUid != foreground) {
                    if (D) {
                        Log.d(TAG, "request from uid " + uid + " is now " + foregroundAsString(foreground));
                    }
                    StringBuilder sb = new StringBuilder();
                    sb.append("request from uid ");
                    sb.append(uid);
                    sb.append(" ");
                    sb.append(record.mReceiver.mCallerIdentity.mPackageName);
                    sb.append(" is now ");
                    sb.append(foreground ? "foreground" : "background)");
                    addToBugreport(sb.toString());
                    record.updateForeground(foreground);
                    if (!isThrottlingExemptLocked(record.mReceiver.mCallerIdentity)) {
                        hashSet.add(provider);
                    }
                }
            }
        }
        Iterator it2 = hashSet.iterator();
        while (it2.hasNext()) {
            applyRequirementsLocked((String) it2.next());
        }
        int i = uid;
        boolean z = foreground;
        updateGnssDataProviderOnUidImportanceChangedLocked(this.mGnssMeasurementsListeners, this.mGnssMeasurementsProvider, $$Lambda$qoNbXUvSu3yuTPVXPUfZW_HDrTQ.INSTANCE, i, z);
        updateGnssDataProviderOnUidImportanceChangedLocked(this.mGnssNavigationMessageListeners, this.mGnssNavigationMessageProvider, $$Lambda$HALkbmbB2IPr_wdFkPjiIWCzJsY.INSTANCE, i, z);
        updateGnssDataProviderOnUidImportanceChangedLocked(this.mGnssStatusListeners, this.mGnssStatusProvider, $$Lambda$hu4394T6QBT8QyZnspMtXqICWs.INSTANCE, i, z);
    }

    @GuardedBy({"mLock"})
    private <TListener extends IInterface> void updateGnssDataProviderOnUidImportanceChangedLocked(ArrayMap<IBinder, ? extends LinkedListenerBase> gnssDataListeners, RemoteListenerHelper<TListener> gnssDataProvider, Function<IBinder, TListener> mapBinderToListener, int uid, boolean foreground) {
        for (Map.Entry<IBinder, ? extends LinkedListenerBase> entry : gnssDataListeners.entrySet()) {
            LinkedListenerBase linkedListener = (LinkedListenerBase) entry.getValue();
            CallerIdentity callerIdentity = linkedListener.mCallerIdentity;
            if (callerIdentity.mUid == uid) {
                if (D) {
                    Log.d(TAG, linkedListener.mListenerName + " from uid " + uid + " is now " + foregroundAsString(foreground));
                }
                TListener listener = (IInterface) mapBinderToListener.apply(entry.getKey());
                if (foreground || isThrottlingExemptLocked(callerIdentity)) {
                    gnssDataProvider.addListener(listener, callerIdentity);
                } else {
                    gnssDataProvider.removeListener(listener);
                }
            }
        }
    }

    private static String foregroundAsString(boolean foreground) {
        return foreground ? "foreground" : "background";
    }

    /* access modifiers changed from: private */
    public static boolean isImportanceForeground(int importance) {
        return importance <= 125;
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public void onBackgroundThrottleIntervalChangedLocked() {
        Iterator<LocationProvider> it = this.mProviders.iterator();
        while (it.hasNext()) {
            applyRequirementsLocked(it.next());
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public void onBackgroundThrottleWhitelistChangedLocked() {
        this.mBackgroundThrottlePackageWhitelist.clear();
        this.mBackgroundThrottlePackageWhitelist.addAll(SystemConfig.getInstance().getAllowUnthrottledLocation());
        String setting = Settings.Global.getString(this.mContext.getContentResolver(), "location_background_throttle_package_whitelist");
        if (!TextUtils.isEmpty(setting)) {
            this.mBackgroundThrottlePackageWhitelist.addAll(Arrays.asList(setting.split(",")));
        }
        Iterator<LocationProvider> it = this.mProviders.iterator();
        while (it.hasNext()) {
            applyRequirementsLocked(it.next());
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"lock"})
    public void onIgnoreSettingsWhitelistChangedLocked() {
        this.mIgnoreSettingsPackageWhitelist.clear();
        this.mIgnoreSettingsPackageWhitelist.addAll(SystemConfig.getInstance().getAllowIgnoreLocationSettings());
        String setting = Settings.Global.getString(this.mContext.getContentResolver(), "location_ignore_settings_package_whitelist");
        if (!TextUtils.isEmpty(setting)) {
            this.mIgnoreSettingsPackageWhitelist.addAll(Arrays.asList(setting.split(",")));
        }
        Iterator<LocationProvider> it = this.mProviders.iterator();
        while (it.hasNext()) {
            applyRequirementsLocked(it.next());
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public void onUserProfilesChangedLocked() {
        this.mCurrentUserProfiles = this.mUserManager.getProfileIdsWithDisabled(this.mCurrentUserId);
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public boolean isCurrentProfileLocked(int userId) {
        return ArrayUtils.contains(this.mCurrentUserProfiles, userId);
    }

    @GuardedBy({"mLock"})
    private void ensureFallbackFusedProviderPresentLocked(String[] pkgs) {
        PackageManager pm = this.mContext.getPackageManager();
        String systemPackageName = this.mContext.getPackageName();
        ArrayList<HashSet<Signature>> sigSets = ServiceWatcher.getSignatureSets(this.mContext, pkgs);
        for (ResolveInfo rInfo : pm.queryIntentServicesAsUser(new Intent(FUSED_LOCATION_SERVICE_ACTION), 128, this.mCurrentUserId)) {
            String packageName = rInfo.serviceInfo.packageName;
            try {
                if (!ServiceWatcher.isSignatureMatch(pm.getPackageInfo(packageName, 64).signatures, sigSets)) {
                    Log.w(TAG, packageName + " resolves service " + FUSED_LOCATION_SERVICE_ACTION + ", but has wrong signature, ignoring");
                } else if (rInfo.serviceInfo.metaData == null) {
                    Log.w(TAG, "Found fused provider without metadata: " + packageName);
                } else if (rInfo.serviceInfo.metaData.getInt("serviceVersion", -1) == 0) {
                    if ((rInfo.serviceInfo.applicationInfo.flags & 1) == 0) {
                        if (D) {
                            Log.d(TAG, "Fallback candidate not in /system: " + packageName);
                        }
                    } else if (pm.checkSignatures(systemPackageName, packageName) != 0) {
                        if (D) {
                            Log.d(TAG, "Fallback candidate not signed the same as system: " + packageName);
                        }
                    } else if (D) {
                        Log.d(TAG, "Found fallback provider: " + packageName);
                        return;
                    } else {
                        return;
                    }
                } else if (D) {
                    Log.d(TAG, "Fallback candidate not version 0: " + packageName);
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "missing package: " + packageName);
            }
        }
        throw new IllegalStateException("Unable to find a fused location provider that is in the system partition with version 0 and signed with the platform certificate. Such a package is needed to provide a default fused location provider in the event that no other fused location provider has been installed or is currently available. For example, coreOnly boot mode when decrypting the data partition. The fallback must also be marked coreApp=\"true\" in the manifest");
    }

    @GuardedBy({"mLock"})
    private void initializeProvidersLocked() {
        int defaultFusedProviderName;
        int defaultGeocoderProviderName;
        LocationManagerService locationManagerService = this;
        LocationProvider passiveProviderManager = new LocationProvider("passive");
        locationManagerService.addProviderLocked(passiveProviderManager);
        locationManagerService.mPassiveProvider = new PassiveProvider(locationManagerService.mContext, passiveProviderManager);
        passiveProviderManager.attachLocked(locationManagerService.mPassiveProvider);
        if (GnssLocationProvider.isSupported()) {
            LocationProvider gnssProviderManager = new LocationProvider("gps", true);
            locationManagerService.mRealProviders.add(gnssProviderManager);
            locationManagerService.addProviderLocked(gnssProviderManager);
            GnssLocationProvider gnssProvider = new GnssLocationProvider(locationManagerService.mContext, gnssProviderManager, locationManagerService.mHandler.getLooper());
            gnssProviderManager.attachLocked(gnssProvider);
            locationManagerService.mGnssSystemInfoProvider = gnssProvider.getGnssSystemInfoProvider();
            locationManagerService.mGnssBatchingProvider = gnssProvider.getGnssBatchingProvider();
            locationManagerService.mGnssMetricsProvider = gnssProvider.getGnssMetricsProvider();
            locationManagerService.mGnssCapabilitiesProvider = gnssProvider.getGnssCapabilitiesProvider();
            locationManagerService.mGnssStatusProvider = gnssProvider.getGnssStatusProvider();
            locationManagerService.mNetInitiatedListener = gnssProvider.getNetInitiatedListener();
            locationManagerService.mGnssMeasurementsProvider = gnssProvider.getGnssMeasurementsProvider();
            locationManagerService.mGnssMeasurementCorrectionsProvider = gnssProvider.getGnssMeasurementCorrectionsProvider();
            locationManagerService.mGnssNavigationMessageProvider = gnssProvider.getGnssNavigationMessageProvider();
            locationManagerService.mGpsGeofenceProxy = gnssProvider.getGpsGeofenceProxy();
        }
        Resources resources = locationManagerService.mContext.getResources();
        String[] pkgs = resources.getStringArray(17236036);
        if (D) {
            Log.d(TAG, "certificates for location providers pulled from: " + Arrays.toString(pkgs));
        }
        locationManagerService.ensureFallbackFusedProviderPresentLocked(pkgs);
        LocationProvider networkProviderManager = new LocationProvider("network", true);
        int defaultNetworkProviderName = 17039783;
        if (!isXOptMode() || "CN".equalsIgnoreCase(SystemProperties.get("ro.miui.region"))) {
            defaultGeocoderProviderName = 17039758;
            defaultFusedProviderName = 17039757;
        } else {
            defaultNetworkProviderName = 17039784;
            defaultGeocoderProviderName = 17039784;
            defaultFusedProviderName = 17039784;
        }
        int defaultGeocoderProviderName2 = defaultGeocoderProviderName;
        LocationProviderProxy networkProvider = LocationProviderProxy.createAndBind(locationManagerService.mContext, networkProviderManager, NETWORK_LOCATION_SERVICE_ACTION, 17891447, defaultNetworkProviderName, 17236036);
        if (networkProvider != null) {
            locationManagerService.mRealProviders.add(networkProviderManager);
            locationManagerService.addProviderLocked(networkProviderManager);
            networkProviderManager.attachLocked(networkProvider);
        } else {
            Slog.w(TAG, "no network location provider found");
        }
        LocationProvider fusedProviderManager = new LocationProvider("fused");
        LocationProvider fusedProviderManager2 = fusedProviderManager;
        LocationProviderProxy locationProviderProxy = networkProvider;
        LocationProviderProxy fusedProvider = LocationProviderProxy.createAndBind(locationManagerService.mContext, fusedProviderManager, FUSED_LOCATION_SERVICE_ACTION, 17891439, defaultFusedProviderName, 17236036);
        if (fusedProvider != null) {
            locationManagerService.mRealProviders.add(fusedProviderManager2);
            locationManagerService.addProviderLocked(fusedProviderManager2);
            fusedProviderManager2.attachLocked(fusedProvider);
        } else {
            Slog.e(TAG, "no fused location provider found", new IllegalStateException("Location service needs a fused location provider"));
        }
        locationManagerService.mGeocodeProvider = GeocoderProxy.createAndBind(locationManagerService.mContext, 17891440, defaultGeocoderProviderName2, 17236036);
        if (locationManagerService.mGeocodeProvider == null) {
            Slog.e(TAG, "no geocoder provider found");
        }
        if (GeofenceProxy.createAndBind(locationManagerService.mContext, 17891441, 17039759, 17236036, locationManagerService.mGpsGeofenceProxy, (IFusedGeofenceHardware) null) == null) {
            Slog.d(TAG, "Unable to bind FLP Geofence proxy.");
        }
        locationManagerService.mComboNlpPackageName = resources.getString(17039711);
        if (locationManagerService.mComboNlpPackageName != null) {
            locationManagerService.mComboNlpReadyMarker = locationManagerService.mComboNlpPackageName + ".nlp:ready";
            locationManagerService.mComboNlpScreenMarker = locationManagerService.mComboNlpPackageName + ".nlp:screen";
        }
        boolean activityRecognitionHardwareIsSupported = ActivityRecognitionHardware.isSupported();
        ActivityRecognitionHardware activityRecognitionHardware = null;
        if (activityRecognitionHardwareIsSupported) {
            activityRecognitionHardware = ActivityRecognitionHardware.getInstance(locationManagerService.mContext);
        } else {
            Slog.d(TAG, "Hardware Activity-Recognition not supported.");
        }
        if (ActivityRecognitionProxy.createAndBind(locationManagerService.mContext, activityRecognitionHardwareIsSupported, activityRecognitionHardware, 17891432, 17039693, 17236036) == null) {
            Slog.d(TAG, "Unable to bind ActivityRecognitionProxy.");
        }
        String[] testProviderStrings = resources.getStringArray(17236072);
        int length = testProviderStrings.length;
        int i = 0;
        while (i < length) {
            LocationProvider passiveProviderManager2 = passiveProviderManager;
            String testProviderString = testProviderStrings[i];
            LocationProvider fusedProviderManager3 = fusedProviderManager2;
            String[] fragments = testProviderString.split(",");
            String str = testProviderString;
            String name = fragments[0].trim();
            ProviderProperties properties = new ProviderProperties(Boolean.parseBoolean(fragments[1]), Boolean.parseBoolean(fragments[2]), Boolean.parseBoolean(fragments[3]), Boolean.parseBoolean(fragments[4]), Boolean.parseBoolean(fragments[5]), Boolean.parseBoolean(fragments[6]), Boolean.parseBoolean(fragments[7]), Integer.parseInt(fragments[8]), Integer.parseInt(fragments[9]));
            String[] strArr = fragments;
            Resources resources2 = resources;
            LocationProvider testProviderManager = new LocationProvider(name);
            locationManagerService.addProviderLocked(testProviderManager);
            String str2 = name;
            new MockProvider(locationManagerService.mContext, testProviderManager, properties);
            i++;
            locationManagerService = this;
            passiveProviderManager = passiveProviderManager2;
            fusedProviderManager2 = fusedProviderManager3;
            resources = resources2;
        }
    }

    public static boolean isXOptMode() {
        return !SystemProperties.getBoolean("persist.sys.miui_optimization", !SplitScreenReporter.ACTION_ENTER_SPLIT.equals(SystemProperties.get("ro.miui.cts")));
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public void onUserChangedLocked(int userId) {
        if (this.mCurrentUserId != userId) {
            if (D) {
                Log.d(TAG, "foreground user is changing to " + userId);
            }
            Iterator<LocationProvider> it = this.mProviders.iterator();
            while (it.hasNext()) {
                it.next().onUserChangingLocked();
            }
            this.mCurrentUserId = userId;
            onUserProfilesChangedLocked();
            this.mBlacklist.switchUser(userId);
            onLocationModeChangedLocked(false);
            onProviderAllowedChangedLocked();
            Iterator<LocationProvider> it2 = this.mProviders.iterator();
            while (it2.hasNext()) {
                it2.next().onUseableChangedLocked(false);
            }
        }
    }

    private class LocationProvider implements AbstractLocationProvider.LocationProviderManager {
        @GuardedBy({"mLock"})
        private boolean mAllowed;
        @GuardedBy({"mLock"})
        private boolean mEnabled;
        private final boolean mIsManagedBySettings;
        private final String mName;
        @GuardedBy({"mLock"})
        private ProviderProperties mProperties;
        @GuardedBy({"mLock"})
        protected AbstractLocationProvider mProvider;
        @GuardedBy({"mLock"})
        private boolean mUseable;

        private LocationProvider(LocationManagerService locationManagerService, String name) {
            this(name, false);
        }

        private LocationProvider(String name, boolean isManagedBySettings) {
            this.mName = name;
            this.mIsManagedBySettings = isManagedBySettings;
            this.mProvider = null;
            this.mUseable = false;
            boolean z = this.mIsManagedBySettings;
            this.mAllowed = !z;
            this.mEnabled = false;
            this.mProperties = null;
            if (z) {
                ContentResolver contentResolver = LocationManagerService.this.mContext.getContentResolver();
                Settings.Secure.putStringForUser(contentResolver, "location_providers_allowed", "-" + this.mName, LocationManagerService.this.mCurrentUserId);
            }
        }

        @GuardedBy({"mLock"})
        public void attachLocked(AbstractLocationProvider provider) {
            Preconditions.checkNotNull(provider);
            Preconditions.checkState(this.mProvider == null);
            if (LocationManagerService.D) {
                Log.d(LocationManagerService.TAG, this.mName + " provider attached");
            }
            this.mProvider = provider;
            onUseableChangedLocked(false);
        }

        public String getName() {
            return this.mName;
        }

        @GuardedBy({"mLock"})
        public List<String> getPackagesLocked() {
            AbstractLocationProvider abstractLocationProvider = this.mProvider;
            if (abstractLocationProvider == null) {
                return Collections.emptyList();
            }
            return abstractLocationProvider.getProviderPackages();
        }

        public boolean isMock() {
            return false;
        }

        @GuardedBy({"mLock"})
        public boolean isPassiveLocked() {
            return this.mProvider == LocationManagerService.this.mPassiveProvider;
        }

        @GuardedBy({"mLock"})
        public ProviderProperties getPropertiesLocked() {
            return this.mProperties;
        }

        @GuardedBy({"mLock"})
        public void setRequestLocked(ProviderRequest request, WorkSource workSource) {
            if (this.mProvider != null) {
                long identity = Binder.clearCallingIdentity();
                try {
                    this.mProvider.setRequest(request, workSource);
                } finally {
                    Binder.restoreCallingIdentity(identity);
                }
            }
        }

        @GuardedBy({"mLock"})
        public void dumpLocked(FileDescriptor fd, PrintWriter pw, String[] args) {
            pw.print("  " + this.mName + " provider");
            if (isMock()) {
                pw.print(" [mock]");
            }
            pw.println(":");
            pw.println("    useable=" + this.mUseable);
            if (!this.mUseable) {
                StringBuilder sb = new StringBuilder();
                sb.append("    attached=");
                sb.append(this.mProvider != null);
                pw.println(sb.toString());
                if (this.mIsManagedBySettings) {
                    pw.println("    allowed=" + this.mAllowed);
                }
                pw.println("    enabled=" + this.mEnabled);
            }
            pw.println("    properties=" + this.mProperties);
            if (this.mProvider != null) {
                long identity = Binder.clearCallingIdentity();
                try {
                    this.mProvider.dump(fd, pw, args);
                } finally {
                    Binder.restoreCallingIdentity(identity);
                }
            }
        }

        @GuardedBy({"mLock"})
        public long getStatusUpdateTimeLocked() {
            if (this.mProvider == null) {
                return 0;
            }
            long identity = Binder.clearCallingIdentity();
            try {
                return this.mProvider.getStatusUpdateTime();
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }

        @GuardedBy({"mLock"})
        public int getStatusLocked(Bundle extras) {
            if (this.mProvider == null) {
                return 2;
            }
            long identity = Binder.clearCallingIdentity();
            try {
                return this.mProvider.getStatus(extras);
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }

        @GuardedBy({"mLock"})
        public void sendExtraCommandLocked(String command, Bundle extras) {
            if (this.mProvider != null) {
                long identity = Binder.clearCallingIdentity();
                try {
                    this.mProvider.sendExtraCommand(command, extras);
                } finally {
                    Binder.restoreCallingIdentity(identity);
                }
            }
        }

        public void onReportLocation(Location location) {
            LocationManagerService.this.mHandler.post(new Runnable(location) {
                private final /* synthetic */ Location f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    LocationManagerService.LocationProvider.this.lambda$onReportLocation$0$LocationManagerService$LocationProvider(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$onReportLocation$0$LocationManagerService$LocationProvider(Location location) {
            synchronized (LocationManagerService.this.mLock) {
                LocationManagerService.this.handleLocationChangedLocked(location, this);
            }
        }

        public void onReportLocation(List<Location> locations) {
            LocationManagerService.this.mHandler.post(new Runnable(locations) {
                private final /* synthetic */ List f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    LocationManagerService.LocationProvider.this.lambda$onReportLocation$1$LocationManagerService$LocationProvider(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$onReportLocation$1$LocationManagerService$LocationProvider(List locations) {
            synchronized (LocationManagerService.this.mLock) {
                LocationProvider gpsProvider = LocationManagerService.this.getLocationProviderLocked("gps");
                if (gpsProvider != null) {
                    if (gpsProvider.isUseableLocked()) {
                        if (LocationManagerService.this.mGnssBatchingCallback == null) {
                            Slog.e(LocationManagerService.TAG, "reportLocationBatch() called without active Callback");
                            return;
                        }
                        try {
                            LocationManagerService.this.mGnssBatchingCallback.onLocationBatch(locations);
                        } catch (RemoteException e) {
                            Slog.e(LocationManagerService.TAG, "mGnssBatchingCallback.onLocationBatch failed", e);
                        }
                    }
                }
                Slog.w(LocationManagerService.TAG, "reportLocationBatch() called without user permission");
            }
        }

        public void onSetEnabled(boolean enabled) {
            LocationManagerService.this.mHandler.post(new Runnable(enabled) {
                private final /* synthetic */ boolean f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    LocationManagerService.LocationProvider.this.lambda$onSetEnabled$2$LocationManagerService$LocationProvider(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$onSetEnabled$2$LocationManagerService$LocationProvider(boolean enabled) {
            synchronized (LocationManagerService.this.mLock) {
                if (enabled != this.mEnabled) {
                    if (LocationManagerService.D) {
                        Log.d(LocationManagerService.TAG, this.mName + " provider enabled is now " + this.mEnabled);
                    }
                    this.mEnabled = enabled;
                    onUseableChangedLocked(false);
                }
            }
        }

        public void onSetProperties(ProviderProperties properties) {
            synchronized (LocationManagerService.this.mLock) {
                this.mProperties = properties;
            }
        }

        @GuardedBy({"mLock"})
        public void onLocationModeChangedLocked() {
            onUseableChangedLocked(false);
        }

        @GuardedBy({"mLock"})
        public void onAllowedChangedLocked() {
            boolean allowed;
            if (this.mIsManagedBySettings && (allowed = TextUtils.delimitedStringContains(Settings.Secure.getStringForUser(LocationManagerService.this.mContext.getContentResolver(), "location_providers_allowed", LocationManagerService.this.mCurrentUserId), ',', this.mName)) != this.mAllowed) {
                if (LocationManagerService.D) {
                    Log.d(LocationManagerService.TAG, this.mName + " provider allowed is now " + this.mAllowed);
                }
                this.mAllowed = allowed;
                onUseableChangedLocked(true);
            }
        }

        @GuardedBy({"mLock"})
        public boolean isUseableLocked() {
            return isUseableForUserLocked(LocationManagerService.this.mCurrentUserId);
        }

        @GuardedBy({"mLock"})
        public boolean isUseableForUserLocked(int userId) {
            return LocationManagerService.this.isCurrentProfileLocked(userId) && this.mUseable;
        }

        @GuardedBy({"mLock"})
        private boolean isUseableIgnoringAllowedLocked() {
            return this.mProvider != null && LocationManagerService.this.mProviders.contains(this) && LocationManagerService.this.isLocationEnabled() && this.mEnabled;
        }

        @GuardedBy({"mLock"})
        public void onUseableChangedLocked(boolean isAllowedChanged) {
            boolean useableIgnoringAllowed = isUseableIgnoringAllowedLocked();
            boolean useable = useableIgnoringAllowed && this.mAllowed;
            if (this.mIsManagedBySettings) {
                if (useableIgnoringAllowed && !isAllowedChanged) {
                    ContentResolver contentResolver = LocationManagerService.this.mContext.getContentResolver();
                    Settings.Secure.putStringForUser(contentResolver, "location_providers_allowed", "+" + this.mName, LocationManagerService.this.mCurrentUserId);
                } else if (!useableIgnoringAllowed) {
                    ContentResolver contentResolver2 = LocationManagerService.this.mContext.getContentResolver();
                    Settings.Secure.putStringForUser(contentResolver2, "location_providers_allowed", "-" + this.mName, LocationManagerService.this.mCurrentUserId);
                }
                Intent intent = new Intent("android.location.PROVIDERS_CHANGED");
                intent.putExtra("android.location.extra.PROVIDER_NAME", this.mName);
                LocationManagerService.this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
            }
            if (useable != this.mUseable) {
                this.mUseable = useable;
                if (LocationManagerService.D) {
                    Log.d(LocationManagerService.TAG, this.mName + " provider useable is now " + this.mUseable);
                }
                if (!this.mUseable) {
                    LocationManagerService.this.mLastLocation.clear();
                    LocationManagerService.this.mLastLocationCoarseInterval.clear();
                }
                LocationManagerService.this.updateProviderUseableLocked(this);
            }
        }

        @GuardedBy({"mLock"})
        public void onUserChangingLocked() {
            this.mUseable = false;
            LocationManagerService.this.updateProviderUseableLocked(this);
        }
    }

    private class MockLocationProvider extends LocationProvider {
        /* access modifiers changed from: private */
        public ProviderRequest mCurrentRequest;

        private MockLocationProvider(String name) {
            super(name);
        }

        public void attachLocked(AbstractLocationProvider provider) {
            Preconditions.checkState(provider instanceof MockProvider);
            super.attachLocked(provider);
        }

        public boolean isMock() {
            return true;
        }

        @GuardedBy({"mLock"})
        public void setEnabledLocked(boolean enabled) {
            if (this.mProvider != null) {
                long identity = Binder.clearCallingIdentity();
                try {
                    ((MockProvider) this.mProvider).setEnabled(enabled);
                } finally {
                    Binder.restoreCallingIdentity(identity);
                }
            }
        }

        @GuardedBy({"mLock"})
        public void setLocationLocked(Location location) {
            if (this.mProvider != null) {
                long identity = Binder.clearCallingIdentity();
                try {
                    ((MockProvider) this.mProvider).setLocation(location);
                } finally {
                    Binder.restoreCallingIdentity(identity);
                }
            }
        }

        @GuardedBy({"mLock"})
        public void setRequestLocked(ProviderRequest request, WorkSource workSource) {
            super.setRequestLocked(request, workSource);
            this.mCurrentRequest = request;
        }

        @GuardedBy({"mLock"})
        public void setStatusLocked(int status, Bundle extras, long updateTime) {
            if (this.mProvider != null) {
                long identity = Binder.clearCallingIdentity();
                try {
                    ((MockProvider) this.mProvider).setStatus(status, extras, updateTime);
                } finally {
                    Binder.restoreCallingIdentity(identity);
                }
            }
        }
    }

    class Receiver extends LinkedListenerBase implements PendingIntent.OnFinished {
        private static final long WAKELOCK_TIMEOUT_MILLIS = 60000;
        final int mAllowedResolutionLevel;
        private final boolean mHideFromAppOps;
        /* access modifiers changed from: private */
        public final Object mKey;
        final ILocationListener mListener;
        private boolean mOpHighPowerMonitoring;
        private boolean mOpMonitoring;
        private int mPendingBroadcasts;
        final PendingIntent mPendingIntent;
        final HashMap<String, UpdateRecord> mUpdateRecords;
        PowerManager.WakeLock mWakeLock;
        final WorkSource mWorkSource;

        private Receiver(ILocationListener listener, PendingIntent intent, int pid, int uid, String packageName, WorkSource workSource, boolean hideFromAppOps) {
            super(new CallerIdentity(uid, pid, packageName), "LocationListener");
            this.mUpdateRecords = new HashMap<>();
            this.mListener = listener;
            this.mPendingIntent = intent;
            if (listener != null) {
                this.mKey = listener.asBinder();
            } else {
                this.mKey = intent;
            }
            this.mAllowedResolutionLevel = LocationManagerService.this.getAllowedResolutionLevel(pid, uid);
            if (workSource != null && workSource.isEmpty()) {
                workSource = null;
            }
            this.mWorkSource = workSource;
            this.mHideFromAppOps = hideFromAppOps;
            updateMonitoring(true);
            this.mWakeLock = LocationManagerService.this.mPowerManager.newWakeLock(1, LocationManagerService.WAKELOCK_KEY);
            this.mWakeLock.setWorkSource(workSource == null ? new WorkSource(this.mCallerIdentity.mUid, this.mCallerIdentity.mPackageName) : workSource);
            this.mWakeLock.setReferenceCounted(false);
        }

        public boolean equals(Object otherObj) {
            return (otherObj instanceof Receiver) && this.mKey.equals(((Receiver) otherObj).mKey);
        }

        public int hashCode() {
            return this.mKey.hashCode();
        }

        public String toString() {
            StringBuilder s = new StringBuilder();
            s.append("Reciever[");
            s.append(Integer.toHexString(System.identityHashCode(this)));
            if (this.mListener != null) {
                s.append(" listener");
            } else {
                s.append(" intent");
            }
            for (String p : this.mUpdateRecords.keySet()) {
                s.append(" ");
                s.append(this.mUpdateRecords.get(p).toString());
            }
            s.append(" monitoring location: ");
            s.append(this.mOpMonitoring);
            s.append("]");
            return s.toString();
        }

        public void updateMonitoring(boolean allow) {
            if (!this.mHideFromAppOps) {
                boolean requestingLocation = false;
                boolean requestingHighPowerLocation = false;
                if (allow) {
                    Iterator<UpdateRecord> it = this.mUpdateRecords.values().iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        }
                        UpdateRecord updateRecord = it.next();
                        LocationProvider provider = LocationManagerService.this.getLocationProviderLocked(updateRecord.mProvider);
                        if (provider != null && (provider.isUseableLocked() || LocationManagerService.this.isSettingsExemptLocked(updateRecord))) {
                            requestingLocation = true;
                            ProviderProperties properties = provider.getPropertiesLocked();
                            if (properties != null && properties.mPowerRequirement == 3 && updateRecord.mRequest.getInterval() < 300000) {
                                requestingHighPowerLocation = true;
                                break;
                            }
                        }
                    }
                }
                this.mOpMonitoring = updateMonitoring(requestingLocation, this.mOpMonitoring, 41);
                boolean wasHighPowerMonitoring = this.mOpHighPowerMonitoring;
                this.mOpHighPowerMonitoring = updateMonitoring(requestingHighPowerLocation, this.mOpHighPowerMonitoring, 42);
                if (this.mOpHighPowerMonitoring != wasHighPowerMonitoring) {
                    LocationManagerService.this.mContext.sendBroadcastAsUser(new Intent("android.location.HIGH_POWER_REQUEST_CHANGE"), UserHandle.ALL);
                }
            }
        }

        private boolean updateMonitoring(boolean allowMonitoring, boolean currentlyMonitoring, int op) {
            if (!currentlyMonitoring) {
                if (allowMonitoring) {
                    if (LocationManagerService.this.mAppOps.startOpNoThrow(op, this.mCallerIdentity.mUid, this.mCallerIdentity.mPackageName) == 0) {
                        return true;
                    }
                    return false;
                }
            } else if (!allowMonitoring || LocationManagerService.this.mAppOps.checkOpNoThrow(op, this.mCallerIdentity.mUid, this.mCallerIdentity.mPackageName) != 0) {
                LocationManagerService.this.mAppOps.finishOp(op, this.mCallerIdentity.mUid, this.mCallerIdentity.mPackageName);
                return false;
            }
            return currentlyMonitoring;
        }

        public boolean isListener() {
            return this.mListener != null;
        }

        public boolean isPendingIntent() {
            return this.mPendingIntent != null;
        }

        public ILocationListener getListener() {
            ILocationListener iLocationListener = this.mListener;
            if (iLocationListener != null) {
                return iLocationListener;
            }
            throw new IllegalStateException("Request for non-existent listener");
        }

        public boolean callStatusChangedLocked(String provider, int status, Bundle extras) {
            ILocationListener iLocationListener = this.mListener;
            if (iLocationListener != null) {
                try {
                    iLocationListener.onStatusChanged(provider, status, extras);
                    incrementPendingBroadcastsLocked();
                    return true;
                } catch (RemoteException e) {
                    return false;
                }
            } else {
                Intent statusChanged = new Intent();
                statusChanged.putExtras(new Bundle(extras));
                statusChanged.putExtra("status", status);
                try {
                    this.mPendingIntent.send(LocationManagerService.this.mContext, 0, statusChanged, this, LocationManagerService.this.mHandler, LocationManagerService.this.getResolutionPermission(this.mAllowedResolutionLevel), PendingIntentUtils.createDontSendToRestrictedAppsBundle((Bundle) null));
                    incrementPendingBroadcastsLocked();
                    return true;
                } catch (PendingIntent.CanceledException e2) {
                    return false;
                }
            }
        }

        public boolean callLocationChangedLocked(Location location) {
            ILocationListener iLocationListener = this.mListener;
            if (iLocationListener != null) {
                try {
                    iLocationListener.onLocationChanged(new Location(location));
                    incrementPendingBroadcastsLocked();
                    return true;
                } catch (RemoteException e) {
                    return false;
                }
            } else {
                Intent locationChanged = new Intent();
                locationChanged.putExtra("location", new Location(location));
                try {
                    this.mPendingIntent.send(LocationManagerService.this.mContext, 0, locationChanged, this, LocationManagerService.this.mHandler, LocationManagerService.this.getResolutionPermission(this.mAllowedResolutionLevel), PendingIntentUtils.createDontSendToRestrictedAppsBundle((Bundle) null));
                    incrementPendingBroadcastsLocked();
                    return true;
                } catch (PendingIntent.CanceledException e2) {
                    return false;
                }
            }
        }

        /* access modifiers changed from: private */
        public boolean callProviderEnabledLocked(String provider, boolean enabled) {
            updateMonitoring(true);
            ILocationListener iLocationListener = this.mListener;
            if (iLocationListener != null) {
                if (enabled) {
                    try {
                        iLocationListener.onProviderEnabled(provider);
                    } catch (RemoteException e) {
                        return false;
                    }
                } else {
                    iLocationListener.onProviderDisabled(provider);
                }
                incrementPendingBroadcastsLocked();
            } else {
                Intent providerIntent = new Intent();
                providerIntent.putExtra("providerEnabled", enabled);
                try {
                    this.mPendingIntent.send(LocationManagerService.this.mContext, 0, providerIntent, this, LocationManagerService.this.mHandler, LocationManagerService.this.getResolutionPermission(this.mAllowedResolutionLevel), PendingIntentUtils.createDontSendToRestrictedAppsBundle((Bundle) null));
                    incrementPendingBroadcastsLocked();
                } catch (PendingIntent.CanceledException e2) {
                    return false;
                }
            }
            return true;
        }

        public void binderDied() {
            if (LocationManagerService.D) {
                Log.d(LocationManagerService.TAG, "Remote " + this.mListenerName + " died.");
            }
            synchronized (LocationManagerService.this.mLock) {
                LocationManagerService.this.removeUpdatesLocked(this);
                clearPendingBroadcastsLocked();
            }
        }

        public void onSendFinished(PendingIntent pendingIntent, Intent intent, int resultCode, String resultData, Bundle resultExtras) {
            synchronized (LocationManagerService.this.mLock) {
                decrementPendingBroadcastsLocked();
            }
        }

        private void incrementPendingBroadcastsLocked() {
            this.mPendingBroadcasts++;
            long identity = Binder.clearCallingIdentity();
            try {
                this.mWakeLock.acquire(60000);
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }

        /* access modifiers changed from: private */
        public void decrementPendingBroadcastsLocked() {
            int i = this.mPendingBroadcasts - 1;
            this.mPendingBroadcasts = i;
            if (i == 0) {
                long identity = Binder.clearCallingIdentity();
                try {
                    if (this.mWakeLock.isHeld()) {
                        this.mWakeLock.release();
                    }
                } finally {
                    Binder.restoreCallingIdentity(identity);
                }
            }
        }

        public void clearPendingBroadcastsLocked() {
            if (this.mPendingBroadcasts > 0) {
                this.mPendingBroadcasts = 0;
                long identity = Binder.clearCallingIdentity();
                try {
                    if (this.mWakeLock.isHeld()) {
                        this.mWakeLock.release();
                    }
                } finally {
                    Binder.restoreCallingIdentity(identity);
                }
            }
        }
    }

    public void locationCallbackFinished(ILocationListener listener) {
        synchronized (this.mLock) {
            Receiver receiver = this.mReceivers.get(listener.asBinder());
            if (receiver != null) {
                receiver.decrementPendingBroadcastsLocked();
            }
        }
    }

    public int getGnssYearOfHardware() {
        GnssLocationProvider.GnssSystemInfoProvider gnssSystemInfoProvider = this.mGnssSystemInfoProvider;
        if (gnssSystemInfoProvider != null) {
            return gnssSystemInfoProvider.getGnssYearOfHardware();
        }
        return 0;
    }

    public String getGnssHardwareModelName() {
        GnssLocationProvider.GnssSystemInfoProvider gnssSystemInfoProvider = this.mGnssSystemInfoProvider;
        if (gnssSystemInfoProvider != null) {
            return gnssSystemInfoProvider.getGnssHardwareModelName();
        }
        return null;
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    private boolean hasGnssPermissions(String packageName) {
        boolean checkLocationAccess;
        synchronized (this.mLock) {
            int allowedResolutionLevel = getCallerAllowedResolutionLevel();
            checkResolutionLevelIsSufficientForProviderUseLocked(allowedResolutionLevel, "gps");
            int pid = Binder.getCallingPid();
            int uid = Binder.getCallingUid();
            long identity = Binder.clearCallingIdentity();
            try {
                checkLocationAccess = checkLocationAccess(pid, uid, packageName, allowedResolutionLevel);
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }
        return checkLocationAccess;
    }

    public int getGnssBatchSize(String packageName) {
        GnssBatchingProvider gnssBatchingProvider;
        this.mContext.enforceCallingPermission("android.permission.LOCATION_HARDWARE", "Location Hardware permission not granted to access hardware batching");
        if (!hasGnssPermissions(packageName) || (gnssBatchingProvider = this.mGnssBatchingProvider) == null) {
            return 0;
        }
        return gnssBatchingProvider.getBatchSize();
    }

    public boolean addGnssBatchingCallback(IBatchedLocationCallback callback, String packageName) {
        this.mContext.enforceCallingPermission("android.permission.LOCATION_HARDWARE", "Location Hardware permission not granted to access hardware batching");
        if (!hasGnssPermissions(packageName) || this.mGnssBatchingProvider == null) {
            return false;
        }
        CallerIdentity callerIdentity = new CallerIdentity(Binder.getCallingUid(), Binder.getCallingPid(), packageName);
        synchronized (this.mLock) {
            this.mGnssBatchingCallback = callback;
            this.mGnssBatchingDeathCallback = new LinkedListener(callback, "BatchedLocationCallback", callerIdentity, new Consumer() {
                public final void accept(Object obj) {
                    LocationManagerService.this.lambda$addGnssBatchingCallback$8$LocationManagerService((IBatchedLocationCallback) obj);
                }
            });
            if (!linkToListenerDeathNotificationLocked(callback.asBinder(), this.mGnssBatchingDeathCallback)) {
                return false;
            }
            return true;
        }
    }

    public /* synthetic */ void lambda$addGnssBatchingCallback$8$LocationManagerService(IBatchedLocationCallback listener) {
        stopGnssBatch();
        removeGnssBatchingCallback();
    }

    public void removeGnssBatchingCallback() {
        synchronized (this.mLock) {
            unlinkFromListenerDeathNotificationLocked(this.mGnssBatchingCallback.asBinder(), this.mGnssBatchingDeathCallback);
            this.mGnssBatchingCallback = null;
            this.mGnssBatchingDeathCallback = null;
        }
    }

    public boolean startGnssBatch(long periodNanos, boolean wakeOnFifoFull, String packageName) {
        boolean start;
        this.mContext.enforceCallingPermission("android.permission.LOCATION_HARDWARE", "Location Hardware permission not granted to access hardware batching");
        if (!hasGnssPermissions(packageName) || this.mGnssBatchingProvider == null) {
            return false;
        }
        synchronized (this.mLock) {
            if (this.mGnssBatchingInProgress) {
                Log.e(TAG, "startGnssBatch unexpectedly called w/o stopping prior batch");
                stopGnssBatch();
            }
            this.mGnssBatchingInProgress = true;
            start = this.mGnssBatchingProvider.start(periodNanos, wakeOnFifoFull);
        }
        return start;
    }

    public void flushGnssBatch(String packageName) {
        this.mContext.enforceCallingPermission("android.permission.LOCATION_HARDWARE", "Location Hardware permission not granted to access hardware batching");
        if (!hasGnssPermissions(packageName)) {
            Log.e(TAG, "flushGnssBatch called without GNSS permissions");
            return;
        }
        synchronized (this.mLock) {
            if (!this.mGnssBatchingInProgress) {
                Log.w(TAG, "flushGnssBatch called with no batch in progress");
            }
            if (this.mGnssBatchingProvider != null) {
                this.mGnssBatchingProvider.flush();
            }
        }
    }

    public boolean stopGnssBatch() {
        this.mContext.enforceCallingPermission("android.permission.LOCATION_HARDWARE", "Location Hardware permission not granted to access hardware batching");
        synchronized (this.mLock) {
            if (this.mGnssBatchingProvider == null) {
                return false;
            }
            this.mGnssBatchingInProgress = false;
            boolean stop = this.mGnssBatchingProvider.stop();
            return stop;
        }
    }

    @GuardedBy({"mLock"})
    private void addProviderLocked(LocationProvider provider) {
        Preconditions.checkState(getLocationProviderLocked(provider.getName()) == null);
        this.mProviders.add(provider);
        provider.onAllowedChangedLocked();
        provider.onUseableChangedLocked(false);
    }

    @GuardedBy({"mLock"})
    private void removeProviderLocked(LocationProvider provider) {
        if (this.mProviders.remove(provider)) {
            provider.onUseableChangedLocked(false);
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public LocationProvider getLocationProviderLocked(String providerName) {
        Iterator<LocationProvider> it = this.mProviders.iterator();
        while (it.hasNext()) {
            LocationProvider provider = it.next();
            if (providerName.equals(provider.getName())) {
                return provider;
            }
        }
        return null;
    }

    /* access modifiers changed from: private */
    public String getResolutionPermission(int resolutionLevel) {
        if (resolutionLevel == 1) {
            return "android.permission.ACCESS_COARSE_LOCATION";
        }
        if (resolutionLevel != 2) {
            return null;
        }
        return "android.permission.ACCESS_FINE_LOCATION";
    }

    /* access modifiers changed from: private */
    public int getAllowedResolutionLevel(int pid, int uid) {
        if (this.mContext.checkPermission("android.permission.ACCESS_FINE_LOCATION", pid, uid) == 0) {
            return 2;
        }
        if (this.mContext.checkPermission("android.permission.ACCESS_COARSE_LOCATION", pid, uid) == 0) {
            return 1;
        }
        return 0;
    }

    private int getCallerAllowedResolutionLevel() {
        return getAllowedResolutionLevel(Binder.getCallingPid(), Binder.getCallingUid());
    }

    private void checkResolutionLevelIsSufficientForGeofenceUse(int allowedResolutionLevel) {
        if (allowedResolutionLevel < 2) {
            throw new SecurityException("Geofence usage requires ACCESS_FINE_LOCATION permission");
        }
    }

    @GuardedBy({"mLock"})
    private int getMinimumResolutionLevelForProviderUseLocked(String provider) {
        ProviderProperties properties;
        if ("gps".equals(provider) || "passive".equals(provider)) {
            return 2;
        }
        if ("network".equals(provider) || "fused".equals(provider)) {
            return 1;
        }
        Iterator<LocationProvider> it = this.mProviders.iterator();
        while (it.hasNext()) {
            LocationProvider lp = it.next();
            if (lp.getName().equals(provider) && (properties = lp.getPropertiesLocked()) != null) {
                if (properties.mRequiresSatellite) {
                    return 2;
                }
                if (properties.mRequiresNetwork || properties.mRequiresCell) {
                    return 1;
                }
            }
        }
        return 2;
    }

    @GuardedBy({"mLock"})
    private void checkResolutionLevelIsSufficientForProviderUseLocked(int allowedResolutionLevel, String providerName) {
        int requiredResolutionLevel = getMinimumResolutionLevelForProviderUseLocked(providerName);
        if (allowedResolutionLevel >= requiredResolutionLevel) {
            return;
        }
        if (requiredResolutionLevel == 1) {
            throw new SecurityException("\"" + providerName + "\" location provider requires ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION permission.");
        } else if (requiredResolutionLevel != 2) {
            throw new SecurityException("Insufficient permission for \"" + providerName + "\" location provider.");
        } else {
            throw new SecurityException("\"" + providerName + "\" location provider requires ACCESS_FINE_LOCATION permission.");
        }
    }

    public static int resolutionLevelToOp(int allowedResolutionLevel) {
        if (allowedResolutionLevel == 0) {
            return -1;
        }
        if (allowedResolutionLevel == 1) {
            return 0;
        }
        return 1;
    }

    private static String resolutionLevelToOpStr(int allowedResolutionLevel) {
        if (allowedResolutionLevel == 0) {
            return "android:fine_location";
        }
        if (allowedResolutionLevel != 1) {
            return allowedResolutionLevel != 2 ? "android:fine_location" : "android:fine_location";
        }
        return "android:coarse_location";
    }

    private boolean reportLocationAccessNoThrow(int pid, int uid, String packageName, int allowedResolutionLevel) {
        int op = resolutionLevelToOp(allowedResolutionLevel);
        if ((op < 0 || this.mAppOps.noteOpNoThrow(op, uid, packageName) == 0) && getAllowedResolutionLevel(pid, uid) >= allowedResolutionLevel) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean checkLocationAccess(int pid, int uid, String packageName, int allowedResolutionLevel) {
        int op = resolutionLevelToOp(allowedResolutionLevel);
        if ((op < 0 || this.mAppOps.noteOpNotRecord(op, uid, packageName) == 0) && getAllowedResolutionLevel(pid, uid) >= allowedResolutionLevel) {
            return true;
        }
        return false;
    }

    public List<String> getAllProviders() {
        ArrayList<String> providers;
        synchronized (this.mLock) {
            providers = new ArrayList<>(this.mProviders.size());
            Iterator<LocationProvider> it = this.mProviders.iterator();
            while (it.hasNext()) {
                String name = it.next().getName();
                if (!"fused".equals(name)) {
                    providers.add(name);
                }
            }
        }
        return providers;
    }

    public List<String> getProviders(Criteria criteria, boolean enabledOnly) {
        ArrayList<String> providers;
        int allowedResolutionLevel = getCallerAllowedResolutionLevel();
        synchronized (this.mLock) {
            providers = new ArrayList<>(this.mProviders.size());
            Iterator<LocationProvider> it = this.mProviders.iterator();
            while (it.hasNext()) {
                LocationProvider provider = it.next();
                String name = provider.getName();
                if (!"fused".equals(name)) {
                    if (allowedResolutionLevel >= getMinimumResolutionLevelForProviderUseLocked(name)) {
                        if (!enabledOnly || provider.isUseableLocked()) {
                            if (criteria == null || android.location.LocationProvider.propertiesMeetCriteria(name, provider.getPropertiesLocked(), criteria)) {
                                providers.add(name);
                            }
                        }
                    }
                }
            }
        }
        return providers;
    }

    public String getBestProvider(Criteria criteria, boolean enabledOnly) {
        List<String> providers = getProviders(criteria, enabledOnly);
        if (providers.isEmpty()) {
            providers = getProviders((Criteria) null, enabledOnly);
        }
        if (providers.isEmpty()) {
            return null;
        }
        if (providers.contains("gps")) {
            return "gps";
        }
        if (providers.contains("network")) {
            return "network";
        }
        return providers.get(0);
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public void updateProviderUseableLocked(LocationProvider provider) {
        boolean useable = provider.isUseableLocked();
        ArrayList<Receiver> deadReceivers = null;
        ArrayList<UpdateRecord> records = this.mRecordsByProvider.get(provider.getName());
        if (records != null) {
            Iterator<UpdateRecord> it = records.iterator();
            while (it.hasNext()) {
                UpdateRecord record = it.next();
                if (isCurrentProfileLocked(UserHandle.getUserId(record.mReceiver.mCallerIdentity.mUid)) && !isSettingsExemptLocked(record) && !record.mReceiver.callProviderEnabledLocked(provider.getName(), useable)) {
                    if (deadReceivers == null) {
                        deadReceivers = new ArrayList<>();
                    }
                    deadReceivers.add(record.mReceiver);
                }
            }
        }
        if (deadReceivers != null) {
            for (int i = deadReceivers.size() - 1; i >= 0; i--) {
                removeUpdatesLocked(deadReceivers.get(i));
            }
        }
        applyRequirementsLocked(provider);
    }

    @GuardedBy({"mLock"})
    private void applyRequirementsLocked(String providerName) {
        LocationProvider provider = getLocationProviderLocked(providerName);
        if (provider != null) {
            applyRequirementsLocked(provider);
        }
    }

    @GuardedBy({"mLock"})
    private void applyRequirementsLocked(LocationProvider provider) {
        long thresholdInterval;
        boolean z;
        LocationManagerService locationManagerService = this;
        LocationProvider locationProvider = provider;
        ArrayList<UpdateRecord> records = LocationManagerServiceInjector.takeOverLP(provider.getName(), locationManagerService.mRecordsByProvider.get(provider.getName()));
        WorkSource worksource = new WorkSource();
        ProviderRequest providerRequest = new ProviderRequest();
        if (!locationManagerService.mProviders.contains(locationProvider) || records == null || records.isEmpty()) {
            LocationManagerService locationManagerService2 = locationManagerService;
        } else {
            long identity = Binder.clearCallingIdentity();
            try {
                long backgroundThrottleInterval = Settings.Global.getLong(locationManagerService.mContext.getContentResolver(), "location_background_throttle_interval_ms", 1800000);
                Binder.restoreCallingIdentity(identity);
                boolean isForegroundOnlyMode = locationManagerService.mBatterySaverMode == 3;
                boolean shouldThrottleRequests = locationManagerService.mBatterySaverMode == 4 && !locationManagerService.mPowerManager.isInteractive();
                providerRequest.lowPowerMode = true;
                Iterator<UpdateRecord> it = records.iterator();
                while (it.hasNext()) {
                    UpdateRecord record = it.next();
                    if (locationManagerService.isCurrentProfileLocked(UserHandle.getUserId(record.mReceiver.mCallerIdentity.mUid))) {
                        Iterator<UpdateRecord> it2 = it;
                        if (!locationManagerService.checkLocationAccess(record.mReceiver.mCallerIdentity.mPid, record.mReceiver.mCallerIdentity.mUid, record.mReceiver.mCallerIdentity.mPackageName, record.mReceiver.mAllowedResolutionLevel)) {
                            it = it2;
                        } else {
                            boolean isBatterySaverDisablingLocation = shouldThrottleRequests || (isForegroundOnlyMode && !record.mIsForegroundUid);
                            if (!provider.isUseableLocked() || isBatterySaverDisablingLocation) {
                                if (locationManagerService.isSettingsExemptLocked(record)) {
                                    providerRequest.locationSettingsIgnored = true;
                                    providerRequest.lowPowerMode = false;
                                } else {
                                    boolean z2 = shouldThrottleRequests;
                                    boolean z3 = isBatterySaverDisablingLocation;
                                    locationManagerService = this;
                                    it = it2;
                                    isForegroundOnlyMode = isForegroundOnlyMode;
                                }
                            }
                            LocationRequest locationRequest = record.mRealRequest;
                            boolean shouldThrottleRequests2 = shouldThrottleRequests;
                            boolean z4 = isBatterySaverDisablingLocation;
                            long interval = locationRequest.getInterval();
                            boolean isForegroundOnlyMode2 = isForegroundOnlyMode;
                            if (!providerRequest.locationSettingsIgnored && !locationManagerService.isThrottlingExemptLocked(record.mReceiver.mCallerIdentity)) {
                                if (!record.mIsForegroundUid) {
                                    interval = Math.max(interval, backgroundThrottleInterval);
                                }
                                if (interval != locationRequest.getInterval()) {
                                    locationRequest = new LocationRequest(locationRequest);
                                    locationRequest.setInterval(interval);
                                }
                            }
                            record.mRequest = locationRequest;
                            providerRequest.locationRequests.add(locationRequest);
                            if (!locationRequest.isLowPowerMode()) {
                                providerRequest.lowPowerMode = false;
                            }
                            if (interval < providerRequest.interval) {
                                z = true;
                                providerRequest.reportLocation = true;
                                providerRequest.interval = interval;
                            } else {
                                z = true;
                            }
                            locationManagerService = this;
                            boolean z5 = z;
                            shouldThrottleRequests = shouldThrottleRequests2;
                            it = it2;
                            isForegroundOnlyMode = isForegroundOnlyMode2;
                        }
                    }
                }
                boolean z6 = shouldThrottleRequests;
                if (providerRequest.reportLocation) {
                    long thresholdInterval2 = ((providerRequest.interval + 1000) * 3) / 2;
                    Iterator<UpdateRecord> it3 = records.iterator();
                    while (it3.hasNext()) {
                        UpdateRecord record2 = it3.next();
                        if (isCurrentProfileLocked(UserHandle.getUserId(record2.mReceiver.mCallerIdentity.mUid))) {
                            LocationRequest locationRequest2 = record2.mRequest;
                            if (providerRequest.locationRequests.contains(locationRequest2)) {
                                if (locationRequest2.getInterval() > thresholdInterval2) {
                                    thresholdInterval = thresholdInterval2;
                                } else if (record2.mReceiver.mWorkSource == null || !isValidWorkSource(record2.mReceiver.mWorkSource)) {
                                    thresholdInterval = thresholdInterval2;
                                    worksource.add(record2.mReceiver.mCallerIdentity.mUid, record2.mReceiver.mCallerIdentity.mPackageName);
                                } else {
                                    worksource.add(record2.mReceiver.mWorkSource);
                                    thresholdInterval = thresholdInterval2;
                                }
                            }
                        } else {
                            thresholdInterval = thresholdInterval2;
                        }
                        thresholdInterval2 = thresholdInterval;
                    }
                    long j = thresholdInterval2;
                }
            } catch (Throwable th) {
                LocationManagerService locationManagerService3 = locationManagerService;
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }
        locationProvider.setRequestLocked(providerRequest, worksource);
    }

    private static boolean isValidWorkSource(WorkSource workSource) {
        if (workSource.size() <= 0) {
            ArrayList<WorkSource.WorkChain> workChains = workSource.getWorkChains();
            if (workChains == null || workChains.isEmpty() || workChains.get(0).getAttributionTag() == null) {
                return false;
            }
            return true;
        } else if (workSource.getName(0) != null) {
            return true;
        } else {
            return false;
        }
    }

    public String[] getBackgroundThrottlingWhitelist() {
        String[] strArr;
        synchronized (this.mLock) {
            strArr = (String[]) this.mBackgroundThrottlePackageWhitelist.toArray(new String[0]);
        }
        return strArr;
    }

    public String[] getIgnoreSettingsWhitelist() {
        String[] strArr;
        synchronized (this.mLock) {
            strArr = (String[]) this.mIgnoreSettingsPackageWhitelist.toArray(new String[0]);
        }
        return strArr;
    }

    @GuardedBy({"mLock"})
    private boolean isThrottlingExemptLocked(CallerIdentity callerIdentity) {
        if (callerIdentity.mUid != 1000 && !this.mBackgroundThrottlePackageWhitelist.contains(callerIdentity.mPackageName)) {
            return isProviderPackage(callerIdentity.mPackageName);
        }
        return true;
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public boolean isSettingsExemptLocked(UpdateRecord record) {
        if (!record.mRealRequest.isLocationSettingsIgnored()) {
            return false;
        }
        if (this.mIgnoreSettingsPackageWhitelist.contains(record.mReceiver.mCallerIdentity.mPackageName)) {
            return true;
        }
        return isProviderPackage(record.mReceiver.mCallerIdentity.mPackageName);
    }

    class UpdateRecord {
        /* access modifiers changed from: private */
        public boolean mIsForegroundUid;
        /* access modifiers changed from: private */
        public Location mLastFixBroadcast;
        /* access modifiers changed from: private */
        public long mLastStatusBroadcast;
        final String mProvider;
        final LocationRequest mRealRequest;
        final Receiver mReceiver;
        LocationRequest mRequest;
        private Throwable mStackTrace;

        private UpdateRecord(String provider, LocationRequest request, Receiver receiver) {
            this.mProvider = provider;
            this.mRealRequest = request;
            this.mRequest = request;
            this.mReceiver = receiver;
            this.mIsForegroundUid = LocationManagerService.isImportanceForeground(LocationManagerService.this.mActivityManager.getPackageImportance(this.mReceiver.mCallerIdentity.mPackageName));
            if (LocationManagerService.D && receiver.mCallerIdentity.mPid == Process.myPid()) {
                this.mStackTrace = new Throwable();
            }
            ArrayList<UpdateRecord> records = (ArrayList) LocationManagerService.this.mRecordsByProvider.get(provider);
            if (records == null) {
                records = new ArrayList<>();
                LocationManagerService.this.mRecordsByProvider.put(provider, records);
            }
            if (!records.contains(this)) {
                records.add(this);
            }
            LocationManagerService.this.mRequestStatistics.startRequesting(this.mReceiver.mCallerIdentity.mPackageName, provider, request.getInterval(), this.mIsForegroundUid);
        }

        /* access modifiers changed from: private */
        public void updateForeground(boolean isForeground) {
            this.mIsForegroundUid = isForeground;
            LocationManagerService.this.mRequestStatistics.updateForeground(this.mReceiver.mCallerIdentity.mPackageName, this.mProvider, isForeground);
        }

        /* access modifiers changed from: private */
        public void disposeLocked(boolean removeReceiver) {
            String packageName = this.mReceiver.mCallerIdentity.mPackageName;
            LocationManagerService.this.mRequestStatistics.stopRequesting(packageName, this.mProvider);
            LocationManagerService.this.mLocationUsageLogger.logLocationApiUsage(1, 1, packageName, this.mRealRequest, this.mReceiver.isListener(), this.mReceiver.isPendingIntent(), (Geofence) null, LocationManagerService.this.mActivityManager.getPackageImportance(packageName));
            ArrayList<UpdateRecord> globalRecords = (ArrayList) LocationManagerService.this.mRecordsByProvider.get(this.mProvider);
            if (globalRecords != null) {
                globalRecords.remove(this);
            }
            if (removeReceiver) {
                HashMap<String, UpdateRecord> receiverRecords = this.mReceiver.mUpdateRecords;
                receiverRecords.remove(this.mProvider);
                if (receiverRecords.size() == 0) {
                    LocationManagerService.this.removeUpdatesLocked(this.mReceiver);
                }
            }
        }

        public String toString() {
            StringBuilder b = new StringBuilder("UpdateRecord[");
            b.append(this.mProvider);
            b.append(" ");
            b.append(this.mReceiver.mCallerIdentity.mPackageName);
            b.append("(");
            b.append(this.mReceiver.mCallerIdentity.mUid);
            if (this.mIsForegroundUid) {
                b.append(" foreground");
            } else {
                b.append(" background");
            }
            b.append(") ");
            b.append(this.mRealRequest);
            b.append(" ");
            b.append(this.mReceiver.mWorkSource);
            if (this.mStackTrace != null) {
                ByteArrayOutputStream tmp = new ByteArrayOutputStream();
                this.mStackTrace.printStackTrace(new PrintStream(tmp));
                b.append("\n\n");
                b.append(tmp.toString());
                b.append("\n");
            }
            b.append("]");
            return b.toString();
        }
    }

    @GuardedBy({"mLock"})
    private Receiver getReceiverLocked(ILocationListener listener, int pid, int uid, String packageName, WorkSource workSource, boolean hideFromAppOps) {
        IBinder binder = listener.asBinder();
        Receiver receiver = this.mReceivers.get(binder);
        if (receiver == null) {
            receiver = new Receiver(listener, (PendingIntent) null, pid, uid, packageName, workSource, hideFromAppOps);
            if (!linkToListenerDeathNotificationLocked(receiver.getListener().asBinder(), receiver)) {
                return null;
            }
            this.mReceivers.put(binder, receiver);
        }
        return receiver;
    }

    @GuardedBy({"mLock"})
    private Receiver getReceiverLocked(PendingIntent intent, int pid, int uid, String packageName, WorkSource workSource, boolean hideFromAppOps) {
        PendingIntent pendingIntent = intent;
        Receiver receiver = this.mReceivers.get(intent);
        if (receiver != null) {
            return receiver;
        }
        Receiver receiver2 = new Receiver((ILocationListener) null, intent, pid, uid, packageName, workSource, hideFromAppOps);
        this.mReceivers.put(intent, receiver2);
        return receiver2;
    }

    private LocationRequest createSanitizedRequest(LocationRequest request, int resolutionLevel, boolean callerHasLocationHardwarePermission) {
        LocationRequest sanitizedRequest = new LocationRequest(request);
        if (!callerHasLocationHardwarePermission) {
            sanitizedRequest.setLowPowerMode(false);
        }
        if (resolutionLevel < 2) {
            int quality = sanitizedRequest.getQuality();
            if (quality == 100) {
                sanitizedRequest.setQuality(102);
            } else if (quality == 203) {
                sanitizedRequest.setQuality(MultiWindowTransition.TRANSIT_MULTI_WINDOW_ENTER_FROM_TOP);
            }
            if (sanitizedRequest.getInterval() < 600000) {
                sanitizedRequest.setInterval(600000);
            }
            if (sanitizedRequest.getFastestInterval() < 600000) {
                sanitizedRequest.setFastestInterval(600000);
            }
        }
        if (sanitizedRequest.getFastestInterval() > sanitizedRequest.getInterval()) {
            sanitizedRequest.setFastestInterval(request.getInterval());
        }
        return sanitizedRequest;
    }

    private void checkPackageName(String packageName) {
        if (packageName != null) {
            int uid = Binder.getCallingUid();
            String[] packages = this.mPackageManager.getPackagesForUid(uid);
            if (packages != null) {
                int length = packages.length;
                int i = 0;
                while (i < length) {
                    if (!packageName.equals(packages[i])) {
                        i++;
                    } else {
                        return;
                    }
                }
                throw new SecurityException("invalid package name: " + packageName);
            }
            throw new SecurityException("invalid UID " + uid);
        }
        throw new SecurityException("invalid package name: " + null);
    }

    /* Debug info: failed to restart local var, previous not found, register: 24 */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v0, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v0, resolved type: android.location.LocationRequest} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v1, resolved type: android.location.LocationRequest} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v2, resolved type: android.location.LocationRequest} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v9, resolved type: android.location.LocationRequest} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v10, resolved type: android.location.LocationRequest} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v11, resolved type: android.location.LocationRequest} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v14, resolved type: android.location.LocationRequest} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v15, resolved type: android.location.LocationRequest} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v16, resolved type: android.location.LocationRequest} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v17, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v18, resolved type: android.location.LocationRequest} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v19, resolved type: android.location.LocationRequest} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v20, resolved type: android.location.LocationRequest} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v21, resolved type: android.location.LocationRequest} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v22, resolved type: android.location.LocationRequest} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void requestLocationUpdates(android.location.LocationRequest r25, android.location.ILocationListener r26, android.app.PendingIntent r27, java.lang.String r28) {
        /*
            r24 = this;
            r8 = r24
            r7 = r28
            java.lang.Object r6 = r8.mLock
            monitor-enter(r6)
            if (r25 != 0) goto L_0x0014
            android.location.LocationRequest r0 = DEFAULT_LOCATION_REQUEST     // Catch:{ all -> 0x000d }
            r5 = r0
            goto L_0x0016
        L_0x000d:
            r0 = move-exception
            r12 = r25
            r13 = r6
            r14 = r7
            goto L_0x0155
        L_0x0014:
            r5 = r25
        L_0x0016:
            r8.checkPackageName(r7)     // Catch:{ all -> 0x0151 }
            int r0 = r24.getCallerAllowedResolutionLevel()     // Catch:{ all -> 0x0151 }
            r4 = r0
            java.lang.String r0 = r5.getProvider()     // Catch:{ all -> 0x0151 }
            r8.checkResolutionLevelIsSufficientForProviderUseLocked(r4, r0)     // Catch:{ all -> 0x0151 }
            android.os.WorkSource r0 = r5.getWorkSource()     // Catch:{ all -> 0x0151 }
            r18 = r0
            r0 = 0
            if (r18 == 0) goto L_0x0043
            boolean r1 = r18.isEmpty()     // Catch:{ all -> 0x003d }
            if (r1 != 0) goto L_0x0043
            android.content.Context r1 = r8.mContext     // Catch:{ all -> 0x003d }
            java.lang.String r2 = "android.permission.UPDATE_DEVICE_STATS"
            r1.enforceCallingOrSelfPermission(r2, r0)     // Catch:{ all -> 0x003d }
            goto L_0x0043
        L_0x003d:
            r0 = move-exception
            r12 = r5
            r13 = r6
            r14 = r7
            goto L_0x0155
        L_0x0043:
            boolean r1 = r5.getHideFromAppOps()     // Catch:{ all -> 0x0151 }
            r19 = r1
            if (r19 == 0) goto L_0x0052
            android.content.Context r1 = r8.mContext     // Catch:{ all -> 0x003d }
            java.lang.String r2 = "android.permission.UPDATE_APP_OPS_STATS"
            r1.enforceCallingOrSelfPermission(r2, r0)     // Catch:{ all -> 0x003d }
        L_0x0052:
            boolean r1 = r5.isLocationSettingsIgnored()     // Catch:{ all -> 0x0151 }
            if (r1 == 0) goto L_0x005f
            android.content.Context r1 = r8.mContext     // Catch:{ all -> 0x003d }
            java.lang.String r2 = "android.permission.WRITE_SECURE_SETTINGS"
            r1.enforceCallingOrSelfPermission(r2, r0)     // Catch:{ all -> 0x003d }
        L_0x005f:
            android.content.Context r0 = r8.mContext     // Catch:{ all -> 0x0151 }
            java.lang.String r1 = "android.permission.LOCATION_HARDWARE"
            int r0 = r0.checkCallingPermission(r1)     // Catch:{ all -> 0x0151 }
            r1 = 1
            r2 = 0
            if (r0 != 0) goto L_0x006d
            r0 = r1
            goto L_0x006e
        L_0x006d:
            r0 = r2
        L_0x006e:
            r3 = r0
            android.location.LocationRequest r0 = r8.createSanitizedRequest(r5, r4, r3)     // Catch:{ all -> 0x0151 }
            r15 = r0
            int r0 = android.os.Binder.getCallingPid()     // Catch:{ all -> 0x0151 }
            r14 = r0
            int r0 = android.os.Binder.getCallingUid()     // Catch:{ all -> 0x0151 }
            r13 = r0
            long r9 = android.os.Binder.clearCallingIdentity()     // Catch:{ all -> 0x0151 }
            r20 = r9
            boolean r0 = r8.checkLocationAccess(r14, r13, r7, r4)     // Catch:{ all -> 0x0143 }
            if (r0 == 0) goto L_0x0135
            boolean r0 = com.android.server.LocationManagerServiceInjector.checkIfRequestBlockedByPolicy((int) r13, (java.lang.String) r7, (android.location.LocationRequest) r15)     // Catch:{ all -> 0x0143 }
            if (r0 == 0) goto L_0x009b
            r10 = r3
            r11 = r4
            r12 = r5
            r9 = r13
            r22 = r14
            r1 = r15
            r13 = r6
            r14 = r7
            goto L_0x013e
        L_0x009b:
            if (r27 != 0) goto L_0x00b5
            if (r26 == 0) goto L_0x00a0
            goto L_0x00b5
        L_0x00a0:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException     // Catch:{ all -> 0x00a9 }
            java.lang.String r1 = "need either listener or intent"
            r0.<init>(r1)     // Catch:{ all -> 0x00a9 }
            throw r0     // Catch:{ all -> 0x00a9 }
        L_0x00a9:
            r0 = move-exception
            r10 = r3
            r11 = r4
            r12 = r5
            r9 = r13
            r22 = r14
            r1 = r15
            r13 = r6
            r14 = r7
            goto L_0x014d
        L_0x00b5:
            if (r27 == 0) goto L_0x00c2
            if (r26 != 0) goto L_0x00ba
            goto L_0x00c2
        L_0x00ba:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException     // Catch:{ all -> 0x00a9 }
            java.lang.String r1 = "cannot register both listener and intent"
            r0.<init>(r1)     // Catch:{ all -> 0x00a9 }
            throw r0     // Catch:{ all -> 0x00a9 }
        L_0x00c2:
            com.android.server.LocationUsageLogger r9 = r8.mLocationUsageLogger     // Catch:{ all -> 0x0143 }
            r10 = 0
            r11 = 1
            if (r26 == 0) goto L_0x00ca
            r0 = r1
            goto L_0x00cb
        L_0x00ca:
            r0 = r2
        L_0x00cb:
            if (r27 == 0) goto L_0x00ce
            goto L_0x00cf
        L_0x00ce:
            r1 = r2
        L_0x00cf:
            r16 = 0
            android.app.ActivityManager r2 = r8.mActivityManager     // Catch:{ all -> 0x0143 }
            int r17 = r2.getPackageImportance(r7)     // Catch:{ all -> 0x0143 }
            r12 = r28
            r2 = r13
            r13 = r5
            r22 = r14
            r14 = r0
            r23 = r15
            r15 = r1
            r9.logLocationApiUsage(r10, r11, r12, r13, r14, r15, r16, r17)     // Catch:{ all -> 0x012b }
            if (r27 == 0) goto L_0x0103
            r1 = r24
            r9 = r2
            r2 = r27
            r10 = r3
            r3 = r22
            r11 = r4
            r4 = r9
            r12 = r5
            r5 = r28
            r13 = r6
            r6 = r18
            r14 = r7
            r7 = r19
            com.android.server.LocationManagerService$Receiver r0 = r1.getReceiverLocked((android.app.PendingIntent) r2, (int) r3, (int) r4, (java.lang.String) r5, (android.os.WorkSource) r6, (boolean) r7)     // Catch:{ all -> 0x00fe }
            goto L_0x011a
        L_0x00fe:
            r0 = move-exception
            r1 = r23
            goto L_0x014d
        L_0x0103:
            r9 = r2
            r10 = r3
            r11 = r4
            r12 = r5
            r13 = r6
            r14 = r7
            r1 = r24
            r2 = r26
            r3 = r22
            r4 = r9
            r5 = r28
            r6 = r18
            r7 = r19
            com.android.server.LocationManagerService$Receiver r0 = r1.getReceiverLocked((android.location.ILocationListener) r2, (int) r3, (int) r4, (java.lang.String) r5, (android.os.WorkSource) r6, (boolean) r7)     // Catch:{ all -> 0x0127 }
        L_0x011a:
            r1 = r23
            r8.requestLocationUpdatesLocked(r1, r0, r9, r14)     // Catch:{ all -> 0x0125 }
            android.os.Binder.restoreCallingIdentity(r20)     // Catch:{ all -> 0x0157 }
            monitor-exit(r13)     // Catch:{ all -> 0x0157 }
            return
        L_0x0125:
            r0 = move-exception
            goto L_0x014d
        L_0x0127:
            r0 = move-exception
            r1 = r23
            goto L_0x014d
        L_0x012b:
            r0 = move-exception
            r9 = r2
            r10 = r3
            r11 = r4
            r12 = r5
            r13 = r6
            r14 = r7
            r1 = r23
            goto L_0x014d
        L_0x0135:
            r10 = r3
            r11 = r4
            r12 = r5
            r9 = r13
            r22 = r14
            r1 = r15
            r13 = r6
            r14 = r7
        L_0x013e:
            android.os.Binder.restoreCallingIdentity(r20)     // Catch:{ all -> 0x0157 }
            monitor-exit(r13)     // Catch:{ all -> 0x0157 }
            return
        L_0x0143:
            r0 = move-exception
            r10 = r3
            r11 = r4
            r12 = r5
            r9 = r13
            r22 = r14
            r1 = r15
            r13 = r6
            r14 = r7
        L_0x014d:
            android.os.Binder.restoreCallingIdentity(r20)     // Catch:{ all -> 0x0157 }
            throw r0     // Catch:{ all -> 0x0157 }
        L_0x0151:
            r0 = move-exception
            r12 = r5
            r13 = r6
            r14 = r7
        L_0x0155:
            monitor-exit(r13)     // Catch:{ all -> 0x0157 }
            throw r0
        L_0x0157:
            r0 = move-exception
            goto L_0x0155
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.LocationManagerService.requestLocationUpdates(android.location.LocationRequest, android.location.ILocationListener, android.app.PendingIntent, java.lang.String):void");
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public void requestLocationUpdatesLocked(LocationRequest request, Receiver receiver, int uid, String packageName) {
        Receiver receiver2 = receiver;
        int i = uid;
        String str = packageName;
        LocationRequest request2 = request == null ? DEFAULT_LOCATION_REQUEST : request;
        String name = request2.getProvider();
        if (name != null) {
            addToBugreport("request " + Integer.toHexString(System.identityHashCode(receiver)) + " " + name + " " + request2 + " from " + str + "(" + i + ")");
            LocationProvider provider = getLocationProviderLocked(name);
            if (provider != null) {
                String str2 = ")";
                UpdateRecord record = new UpdateRecord(name, request2, receiver);
                if (D) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("request ");
                    sb.append(Integer.toHexString(System.identityHashCode(receiver)));
                    sb.append(" ");
                    sb.append(name);
                    sb.append(" ");
                    sb.append(request2);
                    sb.append(" from ");
                    sb.append(str);
                    sb.append("(");
                    sb.append(i);
                    sb.append(" ");
                    sb.append(record.mIsForegroundUid ? "foreground" : "background");
                    sb.append(isThrottlingExemptLocked(receiver2.mCallerIdentity) ? " [whitelisted]" : "");
                    sb.append(str2);
                    Log.d(TAG, sb.toString());
                }
                UpdateRecord oldRecord = receiver2.mUpdateRecords.put(name, record);
                if (oldRecord != null) {
                    oldRecord.disposeLocked(false);
                }
                if (!provider.isUseableLocked() && !isSettingsExemptLocked(record)) {
                    boolean unused = receiver2.callProviderEnabledLocked(name, false);
                }
                applyRequirementsLocked(name);
                receiver2.updateMonitoring(true);
                return;
            }
            throw new IllegalArgumentException("provider doesn't exist: " + name);
        }
        throw new IllegalArgumentException("provider name must not be null");
    }

    /* Debug info: failed to restart local var, previous not found, register: 10 */
    public void removeUpdates(ILocationListener listener, PendingIntent intent, String packageName) {
        Receiver receiver;
        checkPackageName(packageName);
        int pid = Binder.getCallingPid();
        int uid = Binder.getCallingUid();
        if (intent == null && listener == null) {
            throw new IllegalArgumentException("need either listener or intent");
        } else if (intent == null || listener == null) {
            synchronized (this.mLock) {
                if (intent != null) {
                    receiver = getReceiverLocked(intent, pid, uid, packageName, (WorkSource) null, false);
                } else {
                    receiver = getReceiverLocked(listener, pid, uid, packageName, (WorkSource) null, false);
                }
                if (!LocationManagerServiceInjector.checkWhenRemoveLocationRequestLocked(listener, intent)) {
                    long identity = Binder.clearCallingIdentity();
                    try {
                        removeUpdatesLocked(receiver);
                    } finally {
                        Binder.restoreCallingIdentity(identity);
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("cannot register both listener and intent");
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public void removeUpdatesLocked(Receiver receiver) {
        if (D) {
            Log.i(TAG, "remove " + Integer.toHexString(System.identityHashCode(receiver)));
        }
        addToBugreport("remove " + Integer.toHexString(System.identityHashCode(receiver)) + " from " + receiver.mCallerIdentity.mPackageName);
        if (this.mReceivers.remove(receiver.mKey) != null && receiver.isListener()) {
            unlinkFromListenerDeathNotificationLocked(receiver.getListener().asBinder(), receiver);
            receiver.clearPendingBroadcastsLocked();
        }
        receiver.updateMonitoring(false);
        HashSet<String> providers = new HashSet<>();
        HashMap<String, UpdateRecord> oldRecords = receiver.mUpdateRecords;
        if (oldRecords != null) {
            for (UpdateRecord record : oldRecords.values()) {
                record.disposeLocked(false);
            }
            providers.addAll(oldRecords.keySet());
        }
        Iterator<String> it = providers.iterator();
        while (it.hasNext()) {
            applyRequirementsLocked(it.next());
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 21 */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:15:0x0052=Splitter:B:15:0x0052, B:94:0x013b=Splitter:B:94:0x013b} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.location.Location getLastLocation(android.location.LocationRequest r22, java.lang.String r23) {
        /*
            r21 = this;
            r1 = r21
            r2 = r23
            java.lang.Object r3 = r1.mLock
            monitor-enter(r3)
            if (r22 == 0) goto L_0x000c
            r0 = r22
            goto L_0x000e
        L_0x000c:
            android.location.LocationRequest r0 = DEFAULT_LOCATION_REQUEST     // Catch:{ all -> 0x014d }
        L_0x000e:
            r4 = r0
            int r0 = r21.getCallerAllowedResolutionLevel()     // Catch:{ all -> 0x014d }
            r5 = r0
            r1.checkPackageName(r2)     // Catch:{ all -> 0x014d }
            java.lang.String r0 = r4.getProvider()     // Catch:{ all -> 0x014d }
            r1.checkResolutionLevelIsSufficientForProviderUseLocked(r5, r0)     // Catch:{ all -> 0x014d }
            int r0 = android.os.Binder.getCallingPid()     // Catch:{ all -> 0x014d }
            r6 = r0
            int r0 = android.os.Binder.getCallingUid()     // Catch:{ all -> 0x014d }
            r7 = r0
            long r8 = android.os.Binder.clearCallingIdentity()     // Catch:{ all -> 0x014d }
            com.android.server.location.LocationBlacklist r0 = r1.mBlacklist     // Catch:{ all -> 0x0146 }
            boolean r0 = r0.isBlacklisted(r2)     // Catch:{ all -> 0x0146 }
            r10 = 0
            if (r0 == 0) goto L_0x005c
            boolean r0 = D     // Catch:{ all -> 0x0057 }
            if (r0 == 0) goto L_0x0051
            java.lang.String r0 = "LocationManagerService"
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ all -> 0x0057 }
            r11.<init>()     // Catch:{ all -> 0x0057 }
            java.lang.String r12 = "not returning last loc for blacklisted app: "
            r11.append(r12)     // Catch:{ all -> 0x0057 }
            r11.append(r2)     // Catch:{ all -> 0x0057 }
            java.lang.String r11 = r11.toString()     // Catch:{ all -> 0x0057 }
            android.util.Log.d(r0, r11)     // Catch:{ all -> 0x0057 }
        L_0x0051:
            android.os.Binder.restoreCallingIdentity(r8)     // Catch:{ all -> 0x014d }
            monitor-exit(r3)     // Catch:{ all -> 0x014d }
            return r10
        L_0x0057:
            r0 = move-exception
            r19 = r4
            goto L_0x0149
        L_0x005c:
            java.lang.String r0 = r4.getProvider()     // Catch:{ all -> 0x0146 }
            if (r0 != 0) goto L_0x0065
            java.lang.String r11 = "fused"
            r0 = r11
        L_0x0065:
            com.android.server.LocationManagerService$LocationProvider r11 = r1.getLocationProviderLocked(r0)     // Catch:{ all -> 0x0146 }
            if (r11 != 0) goto L_0x0070
            android.os.Binder.restoreCallingIdentity(r8)     // Catch:{ all -> 0x014d }
            monitor-exit(r3)     // Catch:{ all -> 0x014d }
            return r10
        L_0x0070:
            int r12 = android.os.UserHandle.getUserId(r7)     // Catch:{ all -> 0x0146 }
            boolean r12 = r1.isCurrentProfileLocked(r12)     // Catch:{ all -> 0x0146 }
            if (r12 != 0) goto L_0x0086
            boolean r12 = r1.isProviderPackage(r2)     // Catch:{ all -> 0x0057 }
            if (r12 != 0) goto L_0x0086
            android.os.Binder.restoreCallingIdentity(r8)     // Catch:{ all -> 0x014d }
            monitor-exit(r3)     // Catch:{ all -> 0x014d }
            return r10
        L_0x0086:
            boolean r12 = r11.isUseableLocked()     // Catch:{ all -> 0x0146 }
            if (r12 != 0) goto L_0x0092
            android.os.Binder.restoreCallingIdentity(r8)     // Catch:{ all -> 0x014d }
            monitor-exit(r3)     // Catch:{ all -> 0x014d }
            return r10
        L_0x0092:
            r12 = 2
            if (r5 >= r12) goto L_0x009e
            java.util.HashMap<java.lang.String, android.location.Location> r13 = r1.mLastLocationCoarseInterval     // Catch:{ all -> 0x0057 }
            java.lang.Object r13 = r13.get(r0)     // Catch:{ all -> 0x0057 }
            android.location.Location r13 = (android.location.Location) r13     // Catch:{ all -> 0x0057 }
            goto L_0x00a6
        L_0x009e:
            java.util.HashMap<java.lang.String, android.location.Location> r13 = r1.mLastLocation     // Catch:{ all -> 0x0146 }
            java.lang.Object r13 = r13.get(r0)     // Catch:{ all -> 0x0146 }
            android.location.Location r13 = (android.location.Location) r13     // Catch:{ all -> 0x0146 }
        L_0x00a6:
            if (r13 != 0) goto L_0x00ae
            android.os.Binder.restoreCallingIdentity(r8)     // Catch:{ all -> 0x014d }
            monitor-exit(r3)     // Catch:{ all -> 0x014d }
            return r10
        L_0x00ae:
            java.lang.String r14 = resolutionLevelToOpStr(r5)     // Catch:{ all -> 0x0146 }
            long r15 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x0146 }
            long r17 = r13.getElapsedRealtimeNanos()     // Catch:{ all -> 0x0146 }
            r19 = 1000000(0xf4240, double:4.940656E-318)
            long r17 = r17 / r19
            long r15 = r15 - r17
            android.content.Context r12 = r1.mContext     // Catch:{ all -> 0x0146 }
            android.content.ContentResolver r12 = r12.getContentResolver()     // Catch:{ all -> 0x0146 }
            java.lang.String r10 = "location_last_location_max_age_millis"
            r19 = r4
            r20 = r5
            r4 = 1200000(0x124f80, double:5.92879E-318)
            long r4 = android.provider.Settings.Global.getLong(r12, r10, r4)     // Catch:{ all -> 0x0142 }
            int r4 = (r15 > r4 ? 1 : (r15 == r4 ? 0 : -1))
            if (r4 <= 0) goto L_0x00ed
            android.app.AppOpsManager r4 = r1.mAppOps     // Catch:{ all -> 0x00e9 }
            int r4 = r4.unsafeCheckOp(r14, r7, r2)     // Catch:{ all -> 0x00e9 }
            r5 = 4
            if (r4 != r5) goto L_0x00ed
            android.os.Binder.restoreCallingIdentity(r8)     // Catch:{ all -> 0x014d }
            monitor-exit(r3)     // Catch:{ all -> 0x014d }
            r3 = 0
            return r3
        L_0x00e9:
            r0 = move-exception
            r5 = r20
            goto L_0x0149
        L_0x00ed:
            r4 = 0
            r5 = r20
            r10 = 2
            if (r5 >= r10) goto L_0x010e
            java.lang.String r10 = "noGPSLocation"
            android.location.Location r10 = r13.getExtraLocation(r10)     // Catch:{ all -> 0x0140 }
            if (r10 == 0) goto L_0x010b
            android.location.Location r12 = new android.location.Location     // Catch:{ all -> 0x0140 }
            r17 = r0
            com.android.server.location.LocationFudger r0 = r1.mLocationFudger     // Catch:{ all -> 0x0140 }
            android.location.Location r0 = r0.getOrCreate(r10)     // Catch:{ all -> 0x0140 }
            r12.<init>(r0)     // Catch:{ all -> 0x0140 }
            r4 = r12
            goto L_0x010d
        L_0x010b:
            r17 = r0
        L_0x010d:
            goto L_0x0116
        L_0x010e:
            r17 = r0
            android.location.Location r0 = new android.location.Location     // Catch:{ all -> 0x0140 }
            r0.<init>(r13)     // Catch:{ all -> 0x0140 }
            r4 = r0
        L_0x0116:
            if (r4 == 0) goto L_0x013a
            boolean r0 = r1.reportLocationAccessNoThrow(r6, r7, r2, r5)     // Catch:{ all -> 0x0140 }
            if (r0 != 0) goto L_0x013a
            boolean r0 = D     // Catch:{ all -> 0x0140 }
            if (r0 == 0) goto L_0x0139
            java.lang.String r0 = "LocationManagerService"
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x0140 }
            r10.<init>()     // Catch:{ all -> 0x0140 }
            java.lang.String r12 = "not returning last loc for no op app: "
            r10.append(r12)     // Catch:{ all -> 0x0140 }
            r10.append(r2)     // Catch:{ all -> 0x0140 }
            java.lang.String r10 = r10.toString()     // Catch:{ all -> 0x0140 }
            android.util.Log.d(r0, r10)     // Catch:{ all -> 0x0140 }
        L_0x0139:
            r4 = 0
        L_0x013a:
            android.os.Binder.restoreCallingIdentity(r8)     // Catch:{ all -> 0x014d }
            monitor-exit(r3)     // Catch:{ all -> 0x014d }
            return r4
        L_0x0140:
            r0 = move-exception
            goto L_0x0149
        L_0x0142:
            r0 = move-exception
            r5 = r20
            goto L_0x0149
        L_0x0146:
            r0 = move-exception
            r19 = r4
        L_0x0149:
            android.os.Binder.restoreCallingIdentity(r8)     // Catch:{ all -> 0x014d }
            throw r0     // Catch:{ all -> 0x014d }
        L_0x014d:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x014d }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.LocationManagerService.getLastLocation(android.location.LocationRequest, java.lang.String):android.location.Location");
    }

    public LocationTime getGnssTimeMillis() {
        synchronized (this.mLock) {
            Location location = this.mLastLocation.get("gps");
            if (location == null) {
                return null;
            }
            long currentNanos = SystemClock.elapsedRealtimeNanos();
            LocationTime locationTime = new LocationTime(location.getTime() + ((currentNanos - location.getElapsedRealtimeNanos()) / NANOS_PER_MILLI), currentNanos);
            return locationTime;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:22:0x004f, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean injectLocation(android.location.Location r6) {
        /*
            r5 = this;
            android.content.Context r0 = r5.mContext
            java.lang.String r1 = "android.permission.LOCATION_HARDWARE"
            java.lang.String r2 = "Location Hardware permission not granted to inject location"
            r0.enforceCallingPermission(r1, r2)
            android.content.Context r0 = r5.mContext
            java.lang.String r1 = "android.permission.ACCESS_FINE_LOCATION"
            java.lang.String r2 = "Access Fine Location permission not granted to inject Location"
            r0.enforceCallingPermission(r1, r2)
            r0 = 0
            if (r6 != 0) goto L_0x0022
            boolean r1 = D
            if (r1 == 0) goto L_0x0021
            java.lang.String r1 = "LocationManagerService"
            java.lang.String r2 = "injectLocation(): called with null location"
            android.util.Log.d(r1, r2)
        L_0x0021:
            return r0
        L_0x0022:
            java.lang.Object r1 = r5.mLock
            monitor-enter(r1)
            java.lang.String r2 = r6.getProvider()     // Catch:{ all -> 0x0050 }
            com.android.server.LocationManagerService$LocationProvider r2 = r5.getLocationProviderLocked(r2)     // Catch:{ all -> 0x0050 }
            if (r2 == 0) goto L_0x004e
            boolean r3 = r2.isUseableLocked()     // Catch:{ all -> 0x0050 }
            if (r3 != 0) goto L_0x0036
            goto L_0x004e
        L_0x0036:
            java.util.HashMap<java.lang.String, android.location.Location> r3 = r5.mLastLocation     // Catch:{ all -> 0x0050 }
            java.lang.String r4 = r2.getName()     // Catch:{ all -> 0x0050 }
            java.lang.Object r3 = r3.get(r4)     // Catch:{ all -> 0x0050 }
            if (r3 == 0) goto L_0x0044
            monitor-exit(r1)     // Catch:{ all -> 0x0050 }
            return r0
        L_0x0044:
            java.lang.String r0 = r2.getName()     // Catch:{ all -> 0x0050 }
            r5.updateLastLocationLocked(r6, r0)     // Catch:{ all -> 0x0050 }
            r0 = 1
            monitor-exit(r1)     // Catch:{ all -> 0x0050 }
            return r0
        L_0x004e:
            monitor-exit(r1)     // Catch:{ all -> 0x0050 }
            return r0
        L_0x0050:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0050 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.LocationManagerService.injectLocation(android.location.Location):boolean");
    }

    /* Debug info: failed to restart local var, previous not found, register: 22 */
    /* JADX INFO: finally extract failed */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x00b8, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x00c8, code lost:
        r0 = th;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void requestGeofence(android.location.LocationRequest r23, android.location.Geofence r24, android.app.PendingIntent r25, java.lang.String r26) {
        /*
            r22 = this;
            r1 = r22
            r9 = r25
            r8 = r26
            if (r23 != 0) goto L_0x000c
            android.location.LocationRequest r0 = DEFAULT_LOCATION_REQUEST
            r7 = r0
            goto L_0x000e
        L_0x000c:
            r7 = r23
        L_0x000e:
            int r6 = r22.getCallerAllowedResolutionLevel()
            r1.checkResolutionLevelIsSufficientForGeofenceUse(r6)
            if (r9 == 0) goto L_0x00ca
            r1.checkPackageName(r8)
            java.lang.Object r2 = r1.mLock
            monitor-enter(r2)
            java.lang.String r0 = r7.getProvider()     // Catch:{ all -> 0x00c3 }
            r1.checkResolutionLevelIsSufficientForProviderUseLocked(r6, r0)     // Catch:{ all -> 0x00c3 }
            monitor-exit(r2)     // Catch:{ all -> 0x00c3 }
            android.content.Context r0 = r1.mContext
            java.lang.String r2 = "android.permission.LOCATION_HARDWARE"
            int r0 = r0.checkCallingPermission(r2)
            if (r0 != 0) goto L_0x0032
            r0 = 1
            goto L_0x0033
        L_0x0032:
            r0 = 0
        L_0x0033:
            r5 = r0
            android.location.LocationRequest r4 = r1.createSanitizedRequest(r7, r6, r5)
            boolean r0 = D
            if (r0 == 0) goto L_0x0066
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "requestGeofence: "
            r0.append(r2)
            r0.append(r4)
            java.lang.String r2 = " "
            r0.append(r2)
            r3 = r24
            r0.append(r3)
            java.lang.String r2 = " "
            r0.append(r2)
            r0.append(r9)
            java.lang.String r0 = r0.toString()
            java.lang.String r2 = "LocationManagerService"
            android.util.Log.d(r2, r0)
            goto L_0x0068
        L_0x0066:
            r3 = r24
        L_0x0068:
            int r19 = android.os.Binder.getCallingUid()
            int r0 = android.os.UserHandle.getUserId(r19)
            if (r0 == 0) goto L_0x007b
            java.lang.String r0 = "LocationManagerService"
            java.lang.String r2 = "proximity alerts are currently available only to the primary user"
            android.util.Log.w(r0, r2)
            return
        L_0x007b:
            long r20 = android.os.Binder.clearCallingIdentity()
            java.lang.Object r2 = r1.mLock     // Catch:{ all -> 0x00ba }
            monitor-enter(r2)     // Catch:{ all -> 0x00ba }
            com.android.server.LocationUsageLogger r10 = r1.mLocationUsageLogger     // Catch:{ all -> 0x00af }
            r11 = 0
            r12 = 4
            r15 = 0
            r16 = 1
            android.app.ActivityManager r0 = r1.mActivityManager     // Catch:{ all -> 0x00af }
            int r18 = r0.getPackageImportance(r8)     // Catch:{ all -> 0x00af }
            r13 = r26
            r14 = r7
            r17 = r24
            r10.logLocationApiUsage(r11, r12, r13, r14, r15, r16, r17, r18)     // Catch:{ all -> 0x00af }
            monitor-exit(r2)     // Catch:{ all -> 0x00af }
            com.android.server.location.GeofenceManager r2 = r1.mGeofenceManager     // Catch:{ all -> 0x00ba }
            r3 = r4
            r10 = r4
            r4 = r24
            r11 = r5
            r5 = r25
            r12 = r6
            r13 = r7
            r7 = r19
            r8 = r26
            r2.addFence(r3, r4, r5, r6, r7, r8)     // Catch:{ all -> 0x00b6 }
            android.os.Binder.restoreCallingIdentity(r20)
            return
        L_0x00af:
            r0 = move-exception
            r10 = r4
            r11 = r5
            r12 = r6
            r13 = r7
        L_0x00b4:
            monitor-exit(r2)     // Catch:{ all -> 0x00b8 }
            throw r0     // Catch:{ all -> 0x00b6 }
        L_0x00b6:
            r0 = move-exception
            goto L_0x00bf
        L_0x00b8:
            r0 = move-exception
            goto L_0x00b4
        L_0x00ba:
            r0 = move-exception
            r10 = r4
            r11 = r5
            r12 = r6
            r13 = r7
        L_0x00bf:
            android.os.Binder.restoreCallingIdentity(r20)
            throw r0
        L_0x00c3:
            r0 = move-exception
            r12 = r6
            r13 = r7
        L_0x00c6:
            monitor-exit(r2)     // Catch:{ all -> 0x00c8 }
            throw r0
        L_0x00c8:
            r0 = move-exception
            goto L_0x00c6
        L_0x00ca:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "invalid pending intent: "
            r2.append(r3)
            r3 = 0
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r0.<init>(r2)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.LocationManagerService.requestGeofence(android.location.LocationRequest, android.location.Geofence, android.app.PendingIntent, java.lang.String):void");
    }

    /* Debug info: failed to restart local var, previous not found, register: 12 */
    public void removeGeofence(Geofence geofence, PendingIntent intent, String packageName) {
        if (intent != null) {
            checkPackageName(packageName);
            if (D) {
                Log.d(TAG, "removeGeofence: " + geofence + " " + intent);
            }
            long identity = Binder.clearCallingIdentity();
            try {
                synchronized (this.mLock) {
                    this.mLocationUsageLogger.logLocationApiUsage(1, 4, packageName, (LocationRequest) null, false, true, geofence, this.mActivityManager.getPackageImportance(packageName));
                }
                this.mGeofenceManager.removeFence(geofence, intent);
                Binder.restoreCallingIdentity(identity);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        } else {
            throw new IllegalArgumentException("invalid pending intent: " + null);
        }
    }

    public boolean registerGnssStatusCallback(IGnssStatusListener listener, String packageName) {
        if (LocationManagerServiceInjector.checkIfRequestBlockedByPolicy(Binder.getCallingUid(), packageName, listener)) {
            return false;
        }
        return addGnssDataListener(listener, packageName, "GnssStatusListener", this.mGnssStatusProvider, this.mGnssStatusListeners, new Consumer() {
            public final void accept(Object obj) {
                LocationManagerService.this.unregisterGnssStatusCallback((IGnssStatusListener) obj);
            }
        });
    }

    public void unregisterGnssStatusCallback(IGnssStatusListener listener) {
        synchronized (this.mLock) {
            if (!LocationManagerServiceInjector.checkWhenRemoveListenerLocked(Binder.getCallingUid(), listener)) {
                removeGnssDataListener(listener, this.mGnssStatusProvider, this.mGnssStatusListeners);
            }
        }
    }

    public boolean addGnssMeasurementsListener(IGnssMeasurementsListener listener, String packageName) {
        return addGnssDataListener(listener, packageName, "GnssMeasurementsListener", this.mGnssMeasurementsProvider, this.mGnssMeasurementsListeners, new Consumer() {
            public final void accept(Object obj) {
                LocationManagerService.this.removeGnssMeasurementsListener((IGnssMeasurementsListener) obj);
            }
        });
    }

    public void removeGnssMeasurementsListener(IGnssMeasurementsListener listener) {
        removeGnssDataListener(listener, this.mGnssMeasurementsProvider, this.mGnssMeasurementsListeners);
    }

    private static abstract class LinkedListenerBase implements IBinder.DeathRecipient {
        protected final CallerIdentity mCallerIdentity;
        protected final String mListenerName;

        private LinkedListenerBase(CallerIdentity callerIdentity, String listenerName) {
            this.mCallerIdentity = callerIdentity;
            this.mListenerName = listenerName;
        }
    }

    private static class LinkedListener<TListener> extends LinkedListenerBase {
        private final Consumer<TListener> mBinderDeathCallback;
        private final TListener mListener;

        private LinkedListener(TListener listener, String listenerName, CallerIdentity callerIdentity, Consumer<TListener> binderDeathCallback) {
            super(callerIdentity, listenerName);
            this.mListener = listener;
            this.mBinderDeathCallback = binderDeathCallback;
        }

        public void binderDied() {
            if (LocationManagerService.D) {
                Log.d(LocationManagerService.TAG, "Remote " + this.mListenerName + " died.");
            }
            this.mBinderDeathCallback.accept(this.mListener);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 20 */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0089  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x008c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private <TListener extends android.os.IInterface> boolean addGnssDataListener(TListener r21, java.lang.String r22, java.lang.String r23, com.android.server.location.RemoteListenerHelper<TListener> r24, android.util.ArrayMap<android.os.IBinder, com.android.server.LocationManagerService.LinkedListener<TListener>> r25, java.util.function.Consumer<TListener> r26) {
        /*
            r20 = this;
            r1 = r20
            r11 = r22
            r12 = r24
            boolean r0 = r1.hasGnssPermissions(r11)
            r2 = 0
            if (r0 == 0) goto L_0x00af
            if (r12 != 0) goto L_0x0013
            r3 = r21
            goto L_0x00b1
        L_0x0013:
            com.android.server.location.CallerIdentity r0 = new com.android.server.location.CallerIdentity
            int r3 = android.os.Binder.getCallingUid()
            int r4 = android.os.Binder.getCallingPid()
            r0.<init>(r3, r4, r11)
            r13 = r0
            com.android.server.LocationManagerService$LinkedListener r0 = new com.android.server.LocationManagerService$LinkedListener
            r10 = 0
            r5 = r0
            r6 = r21
            r7 = r23
            r8 = r13
            r9 = r26
            r5.<init>(r6, r7, r8, r9)
            r14 = r0
            android.os.IBinder r15 = r21.asBinder()
            java.lang.Object r10 = r1.mLock
            monitor-enter(r10)
            boolean r0 = r1.linkToListenerDeathNotificationLocked(r15, r14)     // Catch:{ all -> 0x00a6 }
            if (r0 != 0) goto L_0x003f
            monitor-exit(r10)     // Catch:{ all -> 0x00a6 }
            return r2
        L_0x003f:
            r9 = r25
            r9.put(r15, r14)     // Catch:{ all -> 0x00a6 }
            long r2 = android.os.Binder.clearCallingIdentity()     // Catch:{ all -> 0x00a6 }
            r16 = r2
            com.android.server.location.GnssMeasurementsProvider r0 = r1.mGnssMeasurementsProvider     // Catch:{ all -> 0x009d }
            if (r12 == r0) goto L_0x0056
            com.android.server.location.GnssStatusListenerHelper r0 = r1.mGnssStatusProvider     // Catch:{ all -> 0x009d }
            if (r12 != r0) goto L_0x0053
            goto L_0x0056
        L_0x0053:
            r19 = r10
            goto L_0x0076
        L_0x0056:
            com.android.server.LocationUsageLogger r2 = r1.mLocationUsageLogger     // Catch:{ all -> 0x009d }
            r3 = 0
            com.android.server.location.GnssMeasurementsProvider r0 = r1.mGnssMeasurementsProvider     // Catch:{ all -> 0x009d }
            if (r12 != r0) goto L_0x0060
            r0 = 2
            r4 = r0
            goto L_0x0062
        L_0x0060:
            r0 = 3
            r4 = r0
        L_0x0062:
            r6 = 0
            r7 = 1
            r8 = 0
            r0 = 0
            android.app.ActivityManager r5 = r1.mActivityManager     // Catch:{ all -> 0x009d }
            int r18 = r5.getPackageImportance(r11)     // Catch:{ all -> 0x009d }
            r5 = r22
            r9 = r0
            r19 = r10
            r10 = r18
            r2.logLocationApiUsage(r3, r4, r5, r6, r7, r8, r9, r10)     // Catch:{ all -> 0x0099 }
        L_0x0076:
            boolean r0 = r1.isThrottlingExemptLocked(r13)     // Catch:{ all -> 0x0099 }
            if (r0 != 0) goto L_0x008c
            android.app.ActivityManager r0 = r1.mActivityManager     // Catch:{ all -> 0x0099 }
            int r0 = r0.getPackageImportance(r11)     // Catch:{ all -> 0x0099 }
            boolean r0 = isImportanceForeground(r0)     // Catch:{ all -> 0x0099 }
            if (r0 == 0) goto L_0x0089
            goto L_0x008c
        L_0x0089:
            r3 = r21
            goto L_0x0091
        L_0x008c:
            r3 = r21
            r12.addListener(r3, r13)     // Catch:{ all -> 0x0097 }
        L_0x0091:
            r0 = 1
            android.os.Binder.restoreCallingIdentity(r16)     // Catch:{ all -> 0x00ad }
            monitor-exit(r19)     // Catch:{ all -> 0x00ad }
            return r0
        L_0x0097:
            r0 = move-exception
            goto L_0x00a2
        L_0x0099:
            r0 = move-exception
            r3 = r21
            goto L_0x00a2
        L_0x009d:
            r0 = move-exception
            r3 = r21
            r19 = r10
        L_0x00a2:
            android.os.Binder.restoreCallingIdentity(r16)     // Catch:{ all -> 0x00ad }
            throw r0     // Catch:{ all -> 0x00ad }
        L_0x00a6:
            r0 = move-exception
            r3 = r21
            r19 = r10
        L_0x00ab:
            monitor-exit(r19)     // Catch:{ all -> 0x00ad }
            throw r0
        L_0x00ad:
            r0 = move-exception
            goto L_0x00ab
        L_0x00af:
            r3 = r21
        L_0x00b1:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.LocationManagerService.addGnssDataListener(android.os.IInterface, java.lang.String, java.lang.String, com.android.server.location.RemoteListenerHelper, android.util.ArrayMap, java.util.function.Consumer):boolean");
    }

    /* Debug info: failed to restart local var, previous not found, register: 18 */
    private <TListener extends IInterface> void removeGnssDataListener(TListener listener, RemoteListenerHelper<TListener> gnssDataProvider, ArrayMap<IBinder, LinkedListener<TListener>> gnssDataListeners) {
        int i;
        RemoteListenerHelper<TListener> remoteListenerHelper = gnssDataProvider;
        if (remoteListenerHelper != null) {
            IBinder binder = listener.asBinder();
            synchronized (this.mLock) {
                try {
                    LinkedListener<TListener> linkedListener = gnssDataListeners.remove(binder);
                    if (linkedListener != null) {
                        long identity = Binder.clearCallingIdentity();
                        try {
                            if (remoteListenerHelper == this.mGnssMeasurementsProvider || remoteListenerHelper == this.mGnssStatusProvider) {
                                LocationUsageLogger locationUsageLogger = this.mLocationUsageLogger;
                                if (remoteListenerHelper == this.mGnssMeasurementsProvider) {
                                    i = 2;
                                } else {
                                    i = 3;
                                }
                                locationUsageLogger.logLocationApiUsage(1, i, linkedListener.mCallerIdentity.mPackageName, (LocationRequest) null, true, false, (Geofence) null, this.mActivityManager.getPackageImportance(linkedListener.mCallerIdentity.mPackageName));
                            }
                            Binder.restoreCallingIdentity(identity);
                            unlinkFromListenerDeathNotificationLocked(binder, linkedListener);
                            remoteListenerHelper.removeListener(listener);
                        } catch (Throwable th) {
                            th = th;
                            throw th;
                        }
                    }
                } catch (Throwable th2) {
                    th = th2;
                    TListener tlistener = listener;
                    throw th;
                }
            }
        }
    }

    private boolean linkToListenerDeathNotificationLocked(IBinder binder, LinkedListenerBase linkedListener) {
        try {
            binder.linkToDeath(linkedListener, 0);
            return true;
        } catch (RemoteException e) {
            Log.w(TAG, "Could not link " + linkedListener.mListenerName + " death callback.", e);
            return false;
        }
    }

    private boolean unlinkFromListenerDeathNotificationLocked(IBinder binder, LinkedListenerBase linkedListener) {
        try {
            binder.unlinkToDeath(linkedListener, 0);
            return true;
        } catch (NoSuchElementException e) {
            Log.w(TAG, "Could not unlink " + linkedListener.mListenerName + " death callback.", e);
            return false;
        }
    }

    public void injectGnssMeasurementCorrections(GnssMeasurementCorrections measurementCorrections, String packageName) {
        this.mContext.enforceCallingPermission("android.permission.LOCATION_HARDWARE", "Location Hardware permission not granted to inject GNSS measurement corrections.");
        if (!hasGnssPermissions(packageName)) {
            Slog.e(TAG, "Can not inject GNSS corrections due to no permission.");
            return;
        }
        GnssMeasurementCorrectionsProvider gnssMeasurementCorrectionsProvider = this.mGnssMeasurementCorrectionsProvider;
        if (gnssMeasurementCorrectionsProvider == null) {
            Slog.e(TAG, "Can not inject GNSS corrections. GNSS measurement corrections provider not available.");
        } else {
            gnssMeasurementCorrectionsProvider.injectGnssMeasurementCorrections(measurementCorrections);
        }
    }

    public long getGnssCapabilities(String packageName) {
        GnssCapabilitiesProvider gnssCapabilitiesProvider;
        this.mContext.enforceCallingPermission("android.permission.LOCATION_HARDWARE", "Location Hardware permission not granted to obtain GNSS chipset capabilities.");
        if (!hasGnssPermissions(packageName) || (gnssCapabilitiesProvider = this.mGnssCapabilitiesProvider) == null) {
            return -1;
        }
        return gnssCapabilitiesProvider.getGnssCapabilities();
    }

    public boolean addGnssNavigationMessageListener(IGnssNavigationMessageListener listener, String packageName) {
        return addGnssDataListener(listener, packageName, "GnssNavigationMessageListener", this.mGnssNavigationMessageProvider, this.mGnssNavigationMessageListeners, new Consumer() {
            public final void accept(Object obj) {
                LocationManagerService.this.removeGnssNavigationMessageListener((IGnssNavigationMessageListener) obj);
            }
        });
    }

    public void removeGnssNavigationMessageListener(IGnssNavigationMessageListener listener) {
        removeGnssDataListener(listener, this.mGnssNavigationMessageProvider, this.mGnssNavigationMessageListeners);
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    public boolean sendExtraCommand(String providerName, String command, Bundle extras) {
        if (providerName != null) {
            synchronized (this.mLock) {
                checkResolutionLevelIsSufficientForProviderUseLocked(getCallerAllowedResolutionLevel(), providerName);
                this.mLocationUsageLogger.logLocationApiUsage(0, 5, providerName);
                if (this.mContext.checkCallingOrSelfPermission(ACCESS_LOCATION_EXTRA_COMMANDS) == 0) {
                    LocationProvider provider = getLocationProviderLocked(providerName);
                    if (provider != null) {
                        provider.sendExtraCommandLocked(command, extras);
                    }
                    this.mLocationUsageLogger.logLocationApiUsage(1, 5, providerName);
                } else {
                    throw new SecurityException("Requires ACCESS_LOCATION_EXTRA_COMMANDS permission");
                }
            }
            return true;
        }
        throw new NullPointerException();
    }

    public boolean sendNiResponse(int notifId, int userResponse) {
        if (Binder.getCallingUid() == Process.myUid()) {
            try {
                return this.mNetInitiatedListener.sendNiResponse(notifId, userResponse);
            } catch (RemoteException e) {
                Slog.e(TAG, "RemoteException in LocationManagerService.sendNiResponse");
                return false;
            }
        } else {
            throw new SecurityException("calling sendNiResponse from outside of the system is not allowed");
        }
    }

    public ProviderProperties getProviderProperties(String providerName) {
        synchronized (this.mLock) {
            checkResolutionLevelIsSufficientForProviderUseLocked(getCallerAllowedResolutionLevel(), providerName);
            LocationProvider provider = getLocationProviderLocked(providerName);
            if (provider == null) {
                return null;
            }
            ProviderProperties propertiesLocked = provider.getPropertiesLocked();
            return propertiesLocked;
        }
    }

    public boolean isProviderPackage(String packageName) {
        synchronized (this.mLock) {
            Iterator<LocationProvider> it = this.mProviders.iterator();
            while (it.hasNext()) {
                if (it.next().getPackagesLocked().contains(packageName)) {
                    return true;
                }
            }
            return false;
        }
    }

    public void setExtraLocationControllerPackage(String packageName) {
        this.mContext.enforceCallingPermission("android.permission.LOCATION_HARDWARE", "android.permission.LOCATION_HARDWARE permission required");
        synchronized (this.mLock) {
            this.mExtraLocationControllerPackage = packageName;
        }
    }

    public String getExtraLocationControllerPackage() {
        String str;
        synchronized (this.mLock) {
            str = this.mExtraLocationControllerPackage;
        }
        return str;
    }

    public void setExtraLocationControllerPackageEnabled(boolean enabled) {
        this.mContext.enforceCallingPermission("android.permission.LOCATION_HARDWARE", "android.permission.LOCATION_HARDWARE permission required");
        synchronized (this.mLock) {
            this.mExtraLocationControllerPackageEnabled = enabled;
        }
    }

    public boolean isExtraLocationControllerPackageEnabled() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mExtraLocationControllerPackageEnabled && this.mExtraLocationControllerPackage != null;
        }
        return z;
    }

    /* access modifiers changed from: private */
    public boolean isLocationEnabled() {
        return isLocationEnabledForUser(this.mCurrentUserId);
    }

    public boolean isLocationEnabledForUser(int userId) {
        if (UserHandle.getCallingUserId() != userId) {
            this.mContext.enforceCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS", "Requires INTERACT_ACROSS_USERS permission");
        }
        long identity = Binder.clearCallingIdentity();
        try {
            boolean z = false;
            if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "location_mode", 0, userId) != 0) {
                z = true;
            }
            return z;
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    public boolean isProviderEnabledForUser(String providerName, int userId) {
        if (UserHandle.getCallingUserId() != userId) {
            this.mContext.enforceCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS", "Requires INTERACT_ACROSS_USERS permission");
        }
        boolean z = false;
        if ("fused".equals(providerName)) {
            return false;
        }
        synchronized (this.mLock) {
            LocationProvider provider = getLocationProviderLocked(providerName);
            if (provider != null && provider.isUseableForUserLocked(userId)) {
                z = true;
            }
        }
        return z;
    }

    @GuardedBy({"mLock"})
    private static boolean shouldBroadcastSafeLocked(Location loc, Location lastLoc, UpdateRecord record, long now) {
        if (lastLoc == null) {
            return true;
        }
        if ((loc.getElapsedRealtimeNanos() - lastLoc.getElapsedRealtimeNanos()) / NANOS_PER_MILLI < record.mRealRequest.getFastestInterval() - 100) {
            return false;
        }
        double minDistance = (double) record.mRealRequest.getSmallestDisplacement();
        if ((minDistance > 0.0d && ((double) loc.distanceTo(lastLoc)) <= minDistance) || record.mRealRequest.getNumUpdates() <= 0) {
            return false;
        }
        if (record.mRealRequest.getExpireAt() >= now) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:88:0x025a, code lost:
        if (r10 != 2) goto L_0x025f;
     */
    @com.android.internal.annotations.GuardedBy({"mLock"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void handleLocationChangedLocked(android.location.Location r31, com.android.server.LocationManagerService.LocationProvider r32) {
        /*
            r30 = this;
            r0 = r30
            r1 = r31
            r2 = r32
            java.util.ArrayList<com.android.server.LocationManagerService$LocationProvider> r3 = r0.mProviders
            boolean r3 = r3.contains(r2)
            if (r3 != 0) goto L_0x000f
            return
        L_0x000f:
            boolean r3 = r31.isComplete()
            java.lang.String r4 = "LocationManagerService"
            if (r3 != 0) goto L_0x002c
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r5 = "Dropping incomplete location: "
            r3.append(r5)
            r3.append(r1)
            java.lang.String r3 = r3.toString()
            android.util.Log.w(r4, r3)
            return
        L_0x002c:
            boolean r3 = r32.isUseableLocked()
            if (r3 == 0) goto L_0x003d
            boolean r3 = r32.isPassiveLocked()
            if (r3 != 0) goto L_0x003d
            com.android.server.location.PassiveProvider r3 = r0.mPassiveProvider
            r3.updateLocation(r1)
        L_0x003d:
            boolean r3 = D
            if (r3 == 0) goto L_0x0056
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r5 = "incoming location: "
            r3.append(r5)
            r3.append(r1)
            java.lang.String r3 = r3.toString()
            android.util.Log.d(r4, r3)
        L_0x0056:
            long r5 = android.os.SystemClock.elapsedRealtime()
            boolean r3 = r32.isUseableLocked()
            if (r3 == 0) goto L_0x0067
            java.lang.String r3 = r32.getName()
            r0.updateLastLocationLocked(r1, r3)
        L_0x0067:
            java.util.HashMap<java.lang.String, android.location.Location> r3 = r0.mLastLocationCoarseInterval
            java.lang.String r7 = r32.getName()
            java.lang.Object r3 = r3.get(r7)
            android.location.Location r3 = (android.location.Location) r3
            if (r3 != 0) goto L_0x008a
            android.location.Location r7 = new android.location.Location
            r7.<init>(r1)
            r3 = r7
            boolean r7 = r32.isUseableLocked()
            if (r7 == 0) goto L_0x008a
            java.util.HashMap<java.lang.String, android.location.Location> r7 = r0.mLastLocationCoarseInterval
            java.lang.String r8 = r32.getName()
            r7.put(r8, r3)
        L_0x008a:
            long r7 = r31.getElapsedRealtimeNanos()
            long r9 = r3.getElapsedRealtimeNanos()
            long r7 = r7 - r9
            r9 = 600000000000(0x8bb2c97000, double:2.964393875047E-312)
            int r9 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r9 <= 0) goto L_0x009f
            r3.set(r1)
        L_0x009f:
            java.lang.String r9 = "noGPSLocation"
            android.location.Location r9 = r3.getExtraLocation(r9)
            java.util.HashMap<java.lang.String, java.util.ArrayList<com.android.server.LocationManagerService$UpdateRecord>> r10 = r0.mRecordsByProvider
            java.lang.String r11 = r32.getName()
            java.lang.Object r10 = r10.get(r11)
            java.util.ArrayList r10 = (java.util.ArrayList) r10
            if (r10 == 0) goto L_0x0304
            int r11 = r10.size()
            if (r11 != 0) goto L_0x00c5
            r18 = r3
            r19 = r7
            r24 = r9
            r25 = r10
            goto L_0x030c
        L_0x00c5:
            r11 = 0
            if (r9 == 0) goto L_0x00ce
            com.android.server.location.LocationFudger r12 = r0.mLocationFudger
            android.location.Location r11 = r12.getOrCreate(r9)
        L_0x00ce:
            r12 = 0
            r13 = 0
            java.util.Iterator r14 = r10.iterator()
        L_0x00d4:
            boolean r15 = r14.hasNext()
            if (r15 == 0) goto L_0x02cb
            java.lang.Object r15 = r14.next()
            com.android.server.LocationManagerService$UpdateRecord r15 = (com.android.server.LocationManagerService.UpdateRecord) r15
            com.android.server.LocationManagerService$Receiver r1 = r15.mReceiver
            r17 = 0
            boolean r18 = r32.isUseableLocked()
            if (r18 != 0) goto L_0x00fa
            boolean r18 = r0.isSettingsExemptLocked(r15)
            if (r18 != 0) goto L_0x00fa
            r18 = r3
            r19 = r7
            r24 = r9
            r25 = r10
            goto L_0x01f7
        L_0x00fa:
            r18 = r3
            com.android.server.location.CallerIdentity r3 = r1.mCallerIdentity
            int r3 = r3.mUid
            int r3 = android.os.UserHandle.getUserId(r3)
            boolean r19 = r0.isCurrentProfileLocked(r3)
            if (r19 != 0) goto L_0x0156
            r19 = r7
            com.android.server.location.CallerIdentity r7 = r1.mCallerIdentity
            java.lang.String r7 = r7.mPackageName
            boolean r7 = r0.isProviderPackage(r7)
            if (r7 != 0) goto L_0x0158
            boolean r7 = D
            if (r7 == 0) goto L_0x0150
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "skipping loc update for background user "
            r7.append(r8)
            r7.append(r3)
            java.lang.String r8 = " (current user: "
            r7.append(r8)
            int r8 = r0.mCurrentUserId
            r7.append(r8)
            java.lang.String r8 = ", app: "
            r7.append(r8)
            com.android.server.location.CallerIdentity r8 = r1.mCallerIdentity
            java.lang.String r8 = r8.mPackageName
            r7.append(r8)
            java.lang.String r8 = ")"
            r7.append(r8)
            java.lang.String r7 = r7.toString()
            android.util.Log.d(r4, r7)
            r24 = r9
            r25 = r10
            goto L_0x01f7
        L_0x0150:
            r24 = r9
            r25 = r10
            goto L_0x01f7
        L_0x0156:
            r19 = r7
        L_0x0158:
            com.android.server.location.LocationBlacklist r7 = r0.mBlacklist
            com.android.server.location.CallerIdentity r8 = r1.mCallerIdentity
            java.lang.String r8 = r8.mPackageName
            boolean r7 = r7.isBlacklisted(r8)
            if (r7 == 0) goto L_0x018d
            boolean r7 = D
            if (r7 == 0) goto L_0x0187
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "skipping loc update for blacklisted app: "
            r7.append(r8)
            com.android.server.location.CallerIdentity r8 = r1.mCallerIdentity
            java.lang.String r8 = r8.mPackageName
            r7.append(r8)
            java.lang.String r7 = r7.toString()
            android.util.Log.d(r4, r7)
            r24 = r9
            r25 = r10
            goto L_0x01f7
        L_0x0187:
            r24 = r9
            r25 = r10
            goto L_0x01f7
        L_0x018d:
            int r7 = r1.mAllowedResolutionLevel
            r8 = 2
            if (r7 >= r8) goto L_0x0194
            r7 = r11
            goto L_0x0196
        L_0x0194:
            r7 = r31
        L_0x0196:
            if (r7 == 0) goto L_0x0226
            android.location.Location r8 = r15.mLastFixBroadcast
            if (r8 == 0) goto L_0x01ad
            boolean r22 = shouldBroadcastSafeLocked(r7, r8, r15, r5)
            if (r22 == 0) goto L_0x01a5
            goto L_0x01ad
        L_0x01a5:
            r22 = r3
            r24 = r9
            r25 = r10
            goto L_0x022c
        L_0x01ad:
            if (r8 != 0) goto L_0x01bb
            r22 = r3
            android.location.Location r3 = new android.location.Location
            r3.<init>(r7)
            r8 = r3
            android.location.Location unused = r15.mLastFixBroadcast = r8
            goto L_0x01c0
        L_0x01bb:
            r22 = r3
            r8.set(r7)
        L_0x01c0:
            com.android.server.location.CallerIdentity r3 = r1.mCallerIdentity
            int r3 = r3.mPid
            r23 = r8
            com.android.server.location.CallerIdentity r8 = r1.mCallerIdentity
            int r8 = r8.mUid
            r24 = r9
            com.android.server.location.CallerIdentity r9 = r1.mCallerIdentity
            java.lang.String r9 = r9.mPackageName
            r25 = r10
            int r10 = r1.mAllowedResolutionLevel
            boolean r3 = r0.reportLocationAccessNoThrow(r3, r8, r9, r10)
            if (r3 != 0) goto L_0x0203
            boolean r3 = D
            if (r3 == 0) goto L_0x01f7
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r8 = "skipping loc update for no op app: "
            r3.append(r8)
            com.android.server.location.CallerIdentity r8 = r1.mCallerIdentity
            java.lang.String r8 = r8.mPackageName
            r3.append(r8)
            java.lang.String r3 = r3.toString()
            android.util.Log.d(r4, r3)
        L_0x01f7:
            r1 = r31
            r3 = r18
            r7 = r19
            r9 = r24
            r10 = r25
            goto L_0x00d4
        L_0x0203:
            boolean r3 = r1.callLocationChangedLocked(r7)
            if (r3 != 0) goto L_0x0220
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r8 = "RemoteException calling onLocationChanged on "
            r3.append(r8)
            r3.append(r1)
            java.lang.String r3 = r3.toString()
            android.util.Slog.w(r4, r3)
            r3 = 1
            r17 = r3
        L_0x0220:
            android.location.LocationRequest r3 = r15.mRealRequest
            r3.decrementNumUpdates()
            goto L_0x022c
        L_0x0226:
            r22 = r3
            r24 = r9
            r25 = r10
        L_0x022c:
            android.content.Context r3 = r0.mContext
            android.content.ContentResolver r3 = r3.getContentResolver()
            java.lang.String r8 = "location_disable_status_callbacks"
            r9 = 1
            int r3 = android.provider.Settings.Global.getInt(r3, r8, r9)
            if (r3 != 0) goto L_0x028d
            long r8 = r32.getStatusUpdateTimeLocked()
            android.os.Bundle r3 = new android.os.Bundle
            r3.<init>()
            int r10 = r2.getStatusLocked(r3)
            long r26 = r15.mLastStatusBroadcast
            int r16 = (r8 > r26 ? 1 : (r8 == r26 ? 0 : -1))
            if (r16 <= 0) goto L_0x0288
            r28 = 0
            int r16 = (r26 > r28 ? 1 : (r26 == r28 ? 0 : -1))
            if (r16 != 0) goto L_0x025d
            r16 = r7
            r7 = 2
            if (r10 == r7) goto L_0x028f
            goto L_0x025f
        L_0x025d:
            r16 = r7
        L_0x025f:
            long unused = r15.mLastStatusBroadcast = r8
            java.lang.String r7 = r32.getName()
            boolean r7 = r1.callStatusChangedLocked(r7, r10, r3)
            if (r7 != 0) goto L_0x0285
            r17 = 1
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r21 = r3
            java.lang.String r3 = "RemoteException calling onStatusChanged on "
            r7.append(r3)
            r7.append(r1)
            java.lang.String r3 = r7.toString()
            android.util.Slog.w(r4, r3)
            goto L_0x028f
        L_0x0285:
            r21 = r3
            goto L_0x028f
        L_0x0288:
            r21 = r3
            r16 = r7
            goto L_0x028f
        L_0x028d:
            r16 = r7
        L_0x028f:
            android.location.LocationRequest r3 = r15.mRealRequest
            int r3 = r3.getNumUpdates()
            if (r3 <= 0) goto L_0x02a1
            android.location.LocationRequest r3 = r15.mRealRequest
            long r7 = r3.getExpireAt()
            int r3 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
            if (r3 >= 0) goto L_0x02ac
        L_0x02a1:
            if (r13 != 0) goto L_0x02a9
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r13 = r3
        L_0x02a9:
            r13.add(r15)
        L_0x02ac:
            if (r17 == 0) goto L_0x02bf
            if (r12 != 0) goto L_0x02b6
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r12 = r3
        L_0x02b6:
            boolean r3 = r12.contains(r1)
            if (r3 != 0) goto L_0x02bf
            r12.add(r1)
        L_0x02bf:
            r1 = r31
            r3 = r18
            r7 = r19
            r9 = r24
            r10 = r25
            goto L_0x00d4
        L_0x02cb:
            r18 = r3
            r19 = r7
            r24 = r9
            r25 = r10
            if (r12 == 0) goto L_0x02e9
            java.util.Iterator r1 = r12.iterator()
        L_0x02d9:
            boolean r3 = r1.hasNext()
            if (r3 == 0) goto L_0x02e9
            java.lang.Object r3 = r1.next()
            com.android.server.LocationManagerService$Receiver r3 = (com.android.server.LocationManagerService.Receiver) r3
            r0.removeUpdatesLocked(r3)
            goto L_0x02d9
        L_0x02e9:
            if (r13 == 0) goto L_0x0303
            java.util.Iterator r1 = r13.iterator()
        L_0x02ef:
            boolean r3 = r1.hasNext()
            if (r3 == 0) goto L_0x0300
            java.lang.Object r3 = r1.next()
            com.android.server.LocationManagerService$UpdateRecord r3 = (com.android.server.LocationManagerService.UpdateRecord) r3
            r4 = 1
            r3.disposeLocked(r4)
            goto L_0x02ef
        L_0x0300:
            r0.applyRequirementsLocked((com.android.server.LocationManagerService.LocationProvider) r2)
        L_0x0303:
            return
        L_0x0304:
            r18 = r3
            r19 = r7
            r24 = r9
            r25 = r10
        L_0x030c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.LocationManagerService.handleLocationChangedLocked(android.location.Location, com.android.server.LocationManagerService$LocationProvider):void");
    }

    @GuardedBy({"mLock"})
    private void updateLastLocationLocked(Location location, String provider) {
        Location noGPSLocation = location.getExtraLocation("noGPSLocation");
        Location lastLocation = this.mLastLocation.get(provider);
        if (lastLocation == null) {
            lastLocation = new Location(provider);
            this.mLastLocation.put(provider, lastLocation);
        } else {
            Location lastNoGPSLocation = lastLocation.getExtraLocation("noGPSLocation");
            if (noGPSLocation == null && lastNoGPSLocation != null) {
                location.setExtraLocation("noGPSLocation", lastNoGPSLocation);
            }
        }
        lastLocation.set(location);
    }

    public boolean geocoderIsPresent() {
        return this.mGeocodeProvider != null;
    }

    public String getFromLocation(double latitude, double longitude, int maxResults, GeocoderParams params, List<Address> addrs) {
        GeocoderProxy geocoderProxy = this.mGeocodeProvider;
        if (geocoderProxy != null) {
            return geocoderProxy.getFromLocation(latitude, longitude, maxResults, params, addrs);
        }
        return null;
    }

    public String getFromLocationName(String locationName, double lowerLeftLatitude, double lowerLeftLongitude, double upperRightLatitude, double upperRightLongitude, int maxResults, GeocoderParams params, List<Address> addrs) {
        GeocoderProxy geocoderProxy = this.mGeocodeProvider;
        if (geocoderProxy != null) {
            return geocoderProxy.getFromLocationName(locationName, lowerLeftLatitude, lowerLeftLongitude, upperRightLatitude, upperRightLongitude, maxResults, params, addrs);
        }
        return null;
    }

    private boolean canCallerAccessMockLocation(String opPackageName) {
        return this.mAppOps.checkOp(58, Binder.getCallingUid(), opPackageName) == 0;
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    public void addTestProvider(String name, ProviderProperties properties, String opPackageName) {
        if (canCallerAccessMockLocation(opPackageName)) {
            if (!"passive".equals(name)) {
                synchronized (this.mLock) {
                    long identity = Binder.clearCallingIdentity();
                    try {
                        LocationProvider oldProvider = getLocationProviderLocked(name);
                        if (oldProvider != null) {
                            if (!oldProvider.isMock()) {
                                removeProviderLocked(oldProvider);
                            } else {
                                throw new IllegalArgumentException("Provider \"" + name + "\" already exists");
                            }
                        }
                        MockLocationProvider mockProviderManager = new MockLocationProvider(name);
                        addProviderLocked(mockProviderManager);
                        mockProviderManager.attachLocked(new MockProvider(this.mContext, mockProviderManager, properties));
                    } finally {
                        Binder.restoreCallingIdentity(identity);
                    }
                }
                return;
            }
            throw new IllegalArgumentException("Cannot mock the passive location provider");
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    public void removeTestProvider(String name, String opPackageName) {
        if (canCallerAccessMockLocation(opPackageName)) {
            synchronized (this.mLock) {
                long identity = Binder.clearCallingIdentity();
                try {
                    LocationProvider testProvider = getLocationProviderLocked(name);
                    if (testProvider == null || !testProvider.isMock()) {
                        throw new IllegalArgumentException("Provider \"" + name + "\" unknown");
                    }
                    removeProviderLocked(testProvider);
                    LocationProvider realProvider = null;
                    Iterator<LocationProvider> it = this.mRealProviders.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        }
                        LocationProvider provider = it.next();
                        if (name.equals(provider.getName())) {
                            realProvider = provider;
                            break;
                        }
                    }
                    if (realProvider != null) {
                        addProviderLocked(realProvider);
                    }
                } finally {
                    Binder.restoreCallingIdentity(identity);
                }
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    public void setTestProviderLocation(String providerName, Location location, String opPackageName) {
        if (canCallerAccessMockLocation(opPackageName)) {
            synchronized (this.mLock) {
                LocationProvider testProvider = getLocationProviderLocked(providerName);
                if (testProvider == null || !testProvider.isMock()) {
                    throw new IllegalArgumentException("Provider \"" + providerName + "\" unknown");
                }
                String locationProvider = location.getProvider();
                if (!TextUtils.isEmpty(locationProvider) && !providerName.equals(locationProvider)) {
                    EventLog.writeEvent(1397638484, new Object[]{"33091107", Integer.valueOf(Binder.getCallingUid()), providerName + "!=" + location.getProvider()});
                }
                ((MockLocationProvider) testProvider).setLocationLocked(location);
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    public void setTestProviderEnabled(String providerName, boolean enabled, String opPackageName) {
        if (canCallerAccessMockLocation(opPackageName)) {
            synchronized (this.mLock) {
                LocationProvider testProvider = getLocationProviderLocked(providerName);
                if (testProvider == null || !testProvider.isMock()) {
                    throw new IllegalArgumentException("Provider \"" + providerName + "\" unknown");
                }
                ((MockLocationProvider) testProvider).setEnabledLocked(enabled);
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    public void setTestProviderStatus(String providerName, int status, Bundle extras, long updateTime, String opPackageName) {
        if (canCallerAccessMockLocation(opPackageName)) {
            synchronized (this.mLock) {
                LocationProvider testProvider = getLocationProviderLocked(providerName);
                if (testProvider == null || !testProvider.isMock()) {
                    throw new IllegalArgumentException("Provider \"" + providerName + "\" unknown");
                }
                ((MockLocationProvider) testProvider).setStatusLocked(status, extras, updateTime);
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    public List<LocationRequest> getTestProviderCurrentRequests(String providerName, String opPackageName) {
        if (!canCallerAccessMockLocation(opPackageName)) {
            return Collections.emptyList();
        }
        synchronized (this.mLock) {
            LocationProvider testProvider = getLocationProviderLocked(providerName);
            if (testProvider == null || !testProvider.isMock()) {
                throw new IllegalArgumentException("Provider \"" + providerName + "\" unknown");
            }
            MockLocationProvider provider = (MockLocationProvider) testProvider;
            if (provider.mCurrentRequest == null) {
                List<LocationRequest> emptyList = Collections.emptyList();
                return emptyList;
            }
            List<LocationRequest> requests = new ArrayList<>();
            for (LocationRequest request : provider.mCurrentRequest.locationRequests) {
                requests.add(new LocationRequest(request));
            }
            return requests;
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x002a, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:80:0x0344, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void dump(java.io.FileDescriptor r9, java.io.PrintWriter r10, java.lang.String[] r11) {
        /*
            r8 = this;
            android.content.Context r0 = r8.mContext
            java.lang.String r1 = "LocationManagerService"
            boolean r0 = com.android.internal.util.DumpUtils.checkDumpPermission(r0, r1, r10)
            if (r0 != 0) goto L_0x000b
            return
        L_0x000b:
            java.lang.Object r0 = r8.mLock
            monitor-enter(r0)
            int r1 = r11.length     // Catch:{ all -> 0x0345 }
            r2 = 0
            if (r1 <= 0) goto L_0x002b
            r1 = r11[r2]     // Catch:{ all -> 0x0345 }
            java.lang.String r3 = "--gnssmetrics"
            boolean r1 = r1.equals(r3)     // Catch:{ all -> 0x0345 }
            if (r1 == 0) goto L_0x002b
            com.android.server.location.GnssLocationProvider$GnssMetricsProvider r1 = r8.mGnssMetricsProvider     // Catch:{ all -> 0x0345 }
            if (r1 == 0) goto L_0x0029
            com.android.server.location.GnssLocationProvider$GnssMetricsProvider r1 = r8.mGnssMetricsProvider     // Catch:{ all -> 0x0345 }
            java.lang.String r1 = r1.getGnssMetricsAsProtoString()     // Catch:{ all -> 0x0345 }
            r10.append(r1)     // Catch:{ all -> 0x0345 }
        L_0x0029:
            monitor-exit(r0)     // Catch:{ all -> 0x0345 }
            return
        L_0x002b:
            java.lang.String r1 = "Current Location Manager state:"
            r10.println(r1)     // Catch:{ all -> 0x0345 }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0345 }
            r1.<init>()     // Catch:{ all -> 0x0345 }
            java.lang.String r3 = "  Current System Time: "
            r1.append(r3)     // Catch:{ all -> 0x0345 }
            long r3 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x0345 }
            java.lang.String r3 = android.util.TimeUtils.logTimeOfDay(r3)     // Catch:{ all -> 0x0345 }
            r1.append(r3)     // Catch:{ all -> 0x0345 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0345 }
            r10.print(r1)     // Catch:{ all -> 0x0345 }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0345 }
            r1.<init>()     // Catch:{ all -> 0x0345 }
            java.lang.String r3 = ", Current Elapsed Time: "
            r1.append(r3)     // Catch:{ all -> 0x0345 }
            long r3 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x0345 }
            java.lang.String r3 = android.util.TimeUtils.formatDuration(r3)     // Catch:{ all -> 0x0345 }
            r1.append(r3)     // Catch:{ all -> 0x0345 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0345 }
            r10.println(r1)     // Catch:{ all -> 0x0345 }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0345 }
            r1.<init>()     // Catch:{ all -> 0x0345 }
            java.lang.String r3 = "  Current user: "
            r1.append(r3)     // Catch:{ all -> 0x0345 }
            int r3 = r8.mCurrentUserId     // Catch:{ all -> 0x0345 }
            r1.append(r3)     // Catch:{ all -> 0x0345 }
            java.lang.String r3 = " "
            r1.append(r3)     // Catch:{ all -> 0x0345 }
            int[] r3 = r8.mCurrentUserProfiles     // Catch:{ all -> 0x0345 }
            java.lang.String r3 = java.util.Arrays.toString(r3)     // Catch:{ all -> 0x0345 }
            r1.append(r3)     // Catch:{ all -> 0x0345 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0345 }
            r10.println(r1)     // Catch:{ all -> 0x0345 }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0345 }
            r1.<init>()     // Catch:{ all -> 0x0345 }
            java.lang.String r3 = "  Location mode: "
            r1.append(r3)     // Catch:{ all -> 0x0345 }
            boolean r3 = r8.isLocationEnabled()     // Catch:{ all -> 0x0345 }
            r1.append(r3)     // Catch:{ all -> 0x0345 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0345 }
            r10.println(r1)     // Catch:{ all -> 0x0345 }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0345 }
            r1.<init>()     // Catch:{ all -> 0x0345 }
            java.lang.String r3 = "  Battery Saver Location Mode: "
            r1.append(r3)     // Catch:{ all -> 0x0345 }
            int r3 = r8.mBatterySaverMode     // Catch:{ all -> 0x0345 }
            java.lang.String r3 = android.os.PowerManager.locationPowerSaveModeToString(r3)     // Catch:{ all -> 0x0345 }
            r1.append(r3)     // Catch:{ all -> 0x0345 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0345 }
            r10.println(r1)     // Catch:{ all -> 0x0345 }
            java.lang.String r1 = "  Location Listeners:"
            r10.println(r1)     // Catch:{ all -> 0x0345 }
            java.util.HashMap<java.lang.Object, com.android.server.LocationManagerService$Receiver> r1 = r8.mReceivers     // Catch:{ all -> 0x0345 }
            java.util.Collection r1 = r1.values()     // Catch:{ all -> 0x0345 }
            java.util.Iterator r1 = r1.iterator()     // Catch:{ all -> 0x0345 }
        L_0x00cd:
            boolean r3 = r1.hasNext()     // Catch:{ all -> 0x0345 }
            if (r3 == 0) goto L_0x00ee
            java.lang.Object r3 = r1.next()     // Catch:{ all -> 0x0345 }
            com.android.server.LocationManagerService$Receiver r3 = (com.android.server.LocationManagerService.Receiver) r3     // Catch:{ all -> 0x0345 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0345 }
            r4.<init>()     // Catch:{ all -> 0x0345 }
            java.lang.String r5 = "    "
            r4.append(r5)     // Catch:{ all -> 0x0345 }
            r4.append(r3)     // Catch:{ all -> 0x0345 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0345 }
            r10.println(r4)     // Catch:{ all -> 0x0345 }
            goto L_0x00cd
        L_0x00ee:
            java.lang.String r1 = "  Active Records by Provider:"
            r10.println(r1)     // Catch:{ all -> 0x0345 }
            java.util.HashMap<java.lang.String, java.util.ArrayList<com.android.server.LocationManagerService$UpdateRecord>> r1 = r8.mRecordsByProvider     // Catch:{ all -> 0x0345 }
            java.util.Set r1 = r1.entrySet()     // Catch:{ all -> 0x0345 }
            java.util.Iterator r1 = r1.iterator()     // Catch:{ all -> 0x0345 }
        L_0x00fd:
            boolean r3 = r1.hasNext()     // Catch:{ all -> 0x0345 }
            if (r3 == 0) goto L_0x0154
            java.lang.Object r3 = r1.next()     // Catch:{ all -> 0x0345 }
            java.util.Map$Entry r3 = (java.util.Map.Entry) r3     // Catch:{ all -> 0x0345 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0345 }
            r4.<init>()     // Catch:{ all -> 0x0345 }
            java.lang.String r5 = "    "
            r4.append(r5)     // Catch:{ all -> 0x0345 }
            java.lang.Object r5 = r3.getKey()     // Catch:{ all -> 0x0345 }
            java.lang.String r5 = (java.lang.String) r5     // Catch:{ all -> 0x0345 }
            r4.append(r5)     // Catch:{ all -> 0x0345 }
            java.lang.String r5 = ":"
            r4.append(r5)     // Catch:{ all -> 0x0345 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0345 }
            r10.println(r4)     // Catch:{ all -> 0x0345 }
            java.lang.Object r4 = r3.getValue()     // Catch:{ all -> 0x0345 }
            java.util.ArrayList r4 = (java.util.ArrayList) r4     // Catch:{ all -> 0x0345 }
            java.util.Iterator r4 = r4.iterator()     // Catch:{ all -> 0x0345 }
        L_0x0132:
            boolean r5 = r4.hasNext()     // Catch:{ all -> 0x0345 }
            if (r5 == 0) goto L_0x0153
            java.lang.Object r5 = r4.next()     // Catch:{ all -> 0x0345 }
            com.android.server.LocationManagerService$UpdateRecord r5 = (com.android.server.LocationManagerService.UpdateRecord) r5     // Catch:{ all -> 0x0345 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x0345 }
            r6.<init>()     // Catch:{ all -> 0x0345 }
            java.lang.String r7 = "      "
            r6.append(r7)     // Catch:{ all -> 0x0345 }
            r6.append(r5)     // Catch:{ all -> 0x0345 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x0345 }
            r10.println(r6)     // Catch:{ all -> 0x0345 }
            goto L_0x0132
        L_0x0153:
            goto L_0x00fd
        L_0x0154:
            java.lang.String r1 = "  Active GnssMeasurement Listeners:"
            r10.println(r1)     // Catch:{ all -> 0x0345 }
            android.util.ArrayMap<android.os.IBinder, com.android.server.LocationManagerService$LinkedListener<android.location.IGnssMeasurementsListener>> r1 = r8.mGnssMeasurementsListeners     // Catch:{ all -> 0x0345 }
            r8.dumpGnssDataListenersLocked(r10, r1)     // Catch:{ all -> 0x0345 }
            java.lang.String r1 = "  Active GnssNavigationMessage Listeners:"
            r10.println(r1)     // Catch:{ all -> 0x0345 }
            android.util.ArrayMap<android.os.IBinder, com.android.server.LocationManagerService$LinkedListener<android.location.IGnssNavigationMessageListener>> r1 = r8.mGnssNavigationMessageListeners     // Catch:{ all -> 0x0345 }
            r8.dumpGnssDataListenersLocked(r10, r1)     // Catch:{ all -> 0x0345 }
            java.lang.String r1 = "  Active GnssStatus Listeners:"
            r10.println(r1)     // Catch:{ all -> 0x0345 }
            android.util.ArrayMap<android.os.IBinder, com.android.server.LocationManagerService$LinkedListener<android.location.IGnssStatusListener>> r1 = r8.mGnssStatusListeners     // Catch:{ all -> 0x0345 }
            r8.dumpGnssDataListenersLocked(r10, r1)     // Catch:{ all -> 0x0345 }
            java.lang.String r1 = "  Historical Records by Provider:"
            r10.println(r1)     // Catch:{ all -> 0x0345 }
            com.android.server.location.LocationRequestStatistics r1 = r8.mRequestStatistics     // Catch:{ all -> 0x0345 }
            java.util.HashMap<com.android.server.location.LocationRequestStatistics$PackageProviderKey, com.android.server.location.LocationRequestStatistics$PackageStatistics> r1 = r1.statistics     // Catch:{ all -> 0x0345 }
            java.util.Set r1 = r1.entrySet()     // Catch:{ all -> 0x0345 }
            java.util.Iterator r1 = r1.iterator()     // Catch:{ all -> 0x0345 }
        L_0x0183:
            boolean r3 = r1.hasNext()     // Catch:{ all -> 0x0345 }
            if (r3 == 0) goto L_0x01c4
            java.lang.Object r3 = r1.next()     // Catch:{ all -> 0x0345 }
            java.util.Map$Entry r3 = (java.util.Map.Entry) r3     // Catch:{ all -> 0x0345 }
            java.lang.Object r4 = r3.getKey()     // Catch:{ all -> 0x0345 }
            com.android.server.location.LocationRequestStatistics$PackageProviderKey r4 = (com.android.server.location.LocationRequestStatistics.PackageProviderKey) r4     // Catch:{ all -> 0x0345 }
            java.lang.Object r5 = r3.getValue()     // Catch:{ all -> 0x0345 }
            com.android.server.location.LocationRequestStatistics$PackageStatistics r5 = (com.android.server.location.LocationRequestStatistics.PackageStatistics) r5     // Catch:{ all -> 0x0345 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x0345 }
            r6.<init>()     // Catch:{ all -> 0x0345 }
            java.lang.String r7 = "    "
            r6.append(r7)     // Catch:{ all -> 0x0345 }
            java.lang.String r7 = r4.packageName     // Catch:{ all -> 0x0345 }
            r6.append(r7)     // Catch:{ all -> 0x0345 }
            java.lang.String r7 = ": "
            r6.append(r7)     // Catch:{ all -> 0x0345 }
            java.lang.String r7 = r4.providerName     // Catch:{ all -> 0x0345 }
            r6.append(r7)     // Catch:{ all -> 0x0345 }
            java.lang.String r7 = ": "
            r6.append(r7)     // Catch:{ all -> 0x0345 }
            r6.append(r5)     // Catch:{ all -> 0x0345 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x0345 }
            r10.println(r6)     // Catch:{ all -> 0x0345 }
            goto L_0x0183
        L_0x01c4:
            java.lang.String r1 = "  Last Known Locations:"
            r10.println(r1)     // Catch:{ all -> 0x0345 }
            java.util.HashMap<java.lang.String, android.location.Location> r1 = r8.mLastLocation     // Catch:{ all -> 0x0345 }
            java.util.Set r1 = r1.entrySet()     // Catch:{ all -> 0x0345 }
            java.util.Iterator r1 = r1.iterator()     // Catch:{ all -> 0x0345 }
        L_0x01d3:
            boolean r3 = r1.hasNext()     // Catch:{ all -> 0x0345 }
            if (r3 == 0) goto L_0x0208
            java.lang.Object r3 = r1.next()     // Catch:{ all -> 0x0345 }
            java.util.Map$Entry r3 = (java.util.Map.Entry) r3     // Catch:{ all -> 0x0345 }
            java.lang.Object r4 = r3.getKey()     // Catch:{ all -> 0x0345 }
            java.lang.String r4 = (java.lang.String) r4     // Catch:{ all -> 0x0345 }
            java.lang.Object r5 = r3.getValue()     // Catch:{ all -> 0x0345 }
            android.location.Location r5 = (android.location.Location) r5     // Catch:{ all -> 0x0345 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x0345 }
            r6.<init>()     // Catch:{ all -> 0x0345 }
            java.lang.String r7 = "    "
            r6.append(r7)     // Catch:{ all -> 0x0345 }
            r6.append(r4)     // Catch:{ all -> 0x0345 }
            java.lang.String r7 = ": "
            r6.append(r7)     // Catch:{ all -> 0x0345 }
            r6.append(r5)     // Catch:{ all -> 0x0345 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x0345 }
            r10.println(r6)     // Catch:{ all -> 0x0345 }
            goto L_0x01d3
        L_0x0208:
            java.lang.String r1 = "  Last Known Locations Coarse Intervals:"
            r10.println(r1)     // Catch:{ all -> 0x0345 }
            java.util.HashMap<java.lang.String, android.location.Location> r1 = r8.mLastLocationCoarseInterval     // Catch:{ all -> 0x0345 }
            java.util.Set r1 = r1.entrySet()     // Catch:{ all -> 0x0345 }
            java.util.Iterator r1 = r1.iterator()     // Catch:{ all -> 0x0345 }
        L_0x0217:
            boolean r3 = r1.hasNext()     // Catch:{ all -> 0x0345 }
            if (r3 == 0) goto L_0x024c
            java.lang.Object r3 = r1.next()     // Catch:{ all -> 0x0345 }
            java.util.Map$Entry r3 = (java.util.Map.Entry) r3     // Catch:{ all -> 0x0345 }
            java.lang.Object r4 = r3.getKey()     // Catch:{ all -> 0x0345 }
            java.lang.String r4 = (java.lang.String) r4     // Catch:{ all -> 0x0345 }
            java.lang.Object r5 = r3.getValue()     // Catch:{ all -> 0x0345 }
            android.location.Location r5 = (android.location.Location) r5     // Catch:{ all -> 0x0345 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x0345 }
            r6.<init>()     // Catch:{ all -> 0x0345 }
            java.lang.String r7 = "    "
            r6.append(r7)     // Catch:{ all -> 0x0345 }
            r6.append(r4)     // Catch:{ all -> 0x0345 }
            java.lang.String r7 = ": "
            r6.append(r7)     // Catch:{ all -> 0x0345 }
            r6.append(r5)     // Catch:{ all -> 0x0345 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x0345 }
            r10.println(r6)     // Catch:{ all -> 0x0345 }
            goto L_0x0217
        L_0x024c:
            com.android.server.location.GeofenceManager r1 = r8.mGeofenceManager     // Catch:{ all -> 0x0345 }
            if (r1 == 0) goto L_0x0256
            com.android.server.location.GeofenceManager r1 = r8.mGeofenceManager     // Catch:{ all -> 0x0345 }
            r1.dump(r10)     // Catch:{ all -> 0x0345 }
            goto L_0x025b
        L_0x0256:
            java.lang.String r1 = "  Geofences: null"
            r10.println(r1)     // Catch:{ all -> 0x0345 }
        L_0x025b:
            com.android.server.location.LocationBlacklist r1 = r8.mBlacklist     // Catch:{ all -> 0x0345 }
            if (r1 == 0) goto L_0x026a
            java.lang.String r1 = "  "
            r10.append(r1)     // Catch:{ all -> 0x0345 }
            com.android.server.location.LocationBlacklist r1 = r8.mBlacklist     // Catch:{ all -> 0x0345 }
            r1.dump(r10)     // Catch:{ all -> 0x0345 }
            goto L_0x026f
        L_0x026a:
            java.lang.String r1 = "  mBlacklist=null"
            r10.println(r1)     // Catch:{ all -> 0x0345 }
        L_0x026f:
            java.lang.String r1 = r8.mExtraLocationControllerPackage     // Catch:{ all -> 0x0345 }
            if (r1 == 0) goto L_0x0293
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0345 }
            r1.<init>()     // Catch:{ all -> 0x0345 }
            java.lang.String r3 = " Location controller extra package: "
            r1.append(r3)     // Catch:{ all -> 0x0345 }
            java.lang.String r3 = r8.mExtraLocationControllerPackage     // Catch:{ all -> 0x0345 }
            r1.append(r3)     // Catch:{ all -> 0x0345 }
            java.lang.String r3 = " enabled: "
            r1.append(r3)     // Catch:{ all -> 0x0345 }
            boolean r3 = r8.mExtraLocationControllerPackageEnabled     // Catch:{ all -> 0x0345 }
            r1.append(r3)     // Catch:{ all -> 0x0345 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0345 }
            r10.println(r1)     // Catch:{ all -> 0x0345 }
        L_0x0293:
            android.util.ArraySet<java.lang.String> r1 = r8.mBackgroundThrottlePackageWhitelist     // Catch:{ all -> 0x0345 }
            boolean r1 = r1.isEmpty()     // Catch:{ all -> 0x0345 }
            if (r1 != 0) goto L_0x02c7
            java.lang.String r1 = "  Throttling Whitelisted Packages:"
            r10.println(r1)     // Catch:{ all -> 0x0345 }
            android.util.ArraySet<java.lang.String> r1 = r8.mBackgroundThrottlePackageWhitelist     // Catch:{ all -> 0x0345 }
            java.util.Iterator r1 = r1.iterator()     // Catch:{ all -> 0x0345 }
        L_0x02a6:
            boolean r3 = r1.hasNext()     // Catch:{ all -> 0x0345 }
            if (r3 == 0) goto L_0x02c7
            java.lang.Object r3 = r1.next()     // Catch:{ all -> 0x0345 }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ all -> 0x0345 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0345 }
            r4.<init>()     // Catch:{ all -> 0x0345 }
            java.lang.String r5 = "    "
            r4.append(r5)     // Catch:{ all -> 0x0345 }
            r4.append(r3)     // Catch:{ all -> 0x0345 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0345 }
            r10.println(r4)     // Catch:{ all -> 0x0345 }
            goto L_0x02a6
        L_0x02c7:
            android.util.ArraySet<java.lang.String> r1 = r8.mIgnoreSettingsPackageWhitelist     // Catch:{ all -> 0x0345 }
            boolean r1 = r1.isEmpty()     // Catch:{ all -> 0x0345 }
            if (r1 != 0) goto L_0x02fb
            java.lang.String r1 = "  Bypass Whitelisted Packages:"
            r10.println(r1)     // Catch:{ all -> 0x0345 }
            android.util.ArraySet<java.lang.String> r1 = r8.mIgnoreSettingsPackageWhitelist     // Catch:{ all -> 0x0345 }
            java.util.Iterator r1 = r1.iterator()     // Catch:{ all -> 0x0345 }
        L_0x02da:
            boolean r3 = r1.hasNext()     // Catch:{ all -> 0x0345 }
            if (r3 == 0) goto L_0x02fb
            java.lang.Object r3 = r1.next()     // Catch:{ all -> 0x0345 }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ all -> 0x0345 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0345 }
            r4.<init>()     // Catch:{ all -> 0x0345 }
            java.lang.String r5 = "    "
            r4.append(r5)     // Catch:{ all -> 0x0345 }
            r4.append(r3)     // Catch:{ all -> 0x0345 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0345 }
            r10.println(r4)     // Catch:{ all -> 0x0345 }
            goto L_0x02da
        L_0x02fb:
            com.android.server.location.LocationFudger r1 = r8.mLocationFudger     // Catch:{ all -> 0x0345 }
            if (r1 == 0) goto L_0x030a
            java.lang.String r1 = "  fudger: "
            r10.append(r1)     // Catch:{ all -> 0x0345 }
            com.android.server.location.LocationFudger r1 = r8.mLocationFudger     // Catch:{ all -> 0x0345 }
            r1.dump(r9, r10, r11)     // Catch:{ all -> 0x0345 }
            goto L_0x030f
        L_0x030a:
            java.lang.String r1 = "  fudger: null"
            r10.println(r1)     // Catch:{ all -> 0x0345 }
        L_0x030f:
            int r1 = r11.length     // Catch:{ all -> 0x0345 }
            if (r1 <= 0) goto L_0x031f
            java.lang.String r1 = "short"
            r2 = r11[r2]     // Catch:{ all -> 0x0345 }
            boolean r1 = r1.equals(r2)     // Catch:{ all -> 0x0345 }
            if (r1 == 0) goto L_0x031f
            monitor-exit(r0)     // Catch:{ all -> 0x0345 }
            return
        L_0x031f:
            com.android.server.location.GnssLocalLog r1 = r8.mDumpStatus     // Catch:{ all -> 0x0345 }
            r1.dump(r10)     // Catch:{ all -> 0x0345 }
            java.util.ArrayList<com.android.server.LocationManagerService$LocationProvider> r1 = r8.mProviders     // Catch:{ all -> 0x0345 }
            java.util.Iterator r1 = r1.iterator()     // Catch:{ all -> 0x0345 }
        L_0x032a:
            boolean r2 = r1.hasNext()     // Catch:{ all -> 0x0345 }
            if (r2 == 0) goto L_0x033a
            java.lang.Object r2 = r1.next()     // Catch:{ all -> 0x0345 }
            com.android.server.LocationManagerService$LocationProvider r2 = (com.android.server.LocationManagerService.LocationProvider) r2     // Catch:{ all -> 0x0345 }
            r2.dumpLocked(r9, r10, r11)     // Catch:{ all -> 0x0345 }
            goto L_0x032a
        L_0x033a:
            boolean r1 = r8.mGnssBatchingInProgress     // Catch:{ all -> 0x0345 }
            if (r1 == 0) goto L_0x0343
            java.lang.String r1 = "  GNSS batching in progress"
            r10.println(r1)     // Catch:{ all -> 0x0345 }
        L_0x0343:
            monitor-exit(r0)     // Catch:{ all -> 0x0345 }
            return
        L_0x0345:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0345 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.LocationManagerService.dump(java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[]):void");
    }

    @GuardedBy({"mLock"})
    private void dumpGnssDataListenersLocked(PrintWriter pw, ArrayMap<IBinder, ? extends LinkedListenerBase> gnssDataListeners) {
        for (LinkedListenerBase listener : gnssDataListeners.values()) {
            CallerIdentity callerIdentity = listener.mCallerIdentity;
            pw.println("    " + callerIdentity.mPid + " " + callerIdentity.mUid + " " + callerIdentity.mPackageName + ": " + isThrottlingExemptLocked(callerIdentity));
        }
    }

    private void addToBugreport(String log) {
        GnssLocalLog gnssLocalLog = this.mDumpStatus;
        gnssLocalLog.log("=MI LMS= " + log);
    }
}
