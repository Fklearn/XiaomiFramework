package com.android.server.location;

import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.hardware.location.GeofenceHardwareImpl;
import android.location.FusedBatchOptions;
import android.location.GnssMeasurementsEvent;
import android.location.GnssNavigationMessage;
import android.location.IGpsGeofenceHardware;
import android.location.INetInitiatedListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.os.PowerSaveState;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.WorkSource;
import android.provider.Settings;
import android.server.am.SplitScreenReporter;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.Log;
import android.util.StatsLog;
import android.util.TimeUtils;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.app.IBatteryStats;
import com.android.internal.location.GpsNetInitiatedHandler;
import com.android.internal.location.ProviderProperties;
import com.android.internal.location.ProviderRequest;
import com.android.internal.location.gnssmetrics.GnssMetrics;
import com.android.server.UiModeManagerService;
import com.android.server.job.controllers.JobStatus;
import com.android.server.location.AbstractLocationProvider;
import com.android.server.location.GnssLocationProvider;
import com.android.server.location.GnssNetworkConnectivityHandler;
import com.android.server.location.GnssSatelliteBlacklistHelper;
import com.android.server.location.NtpTimeHelper;
import com.android.server.wm.MultiWindowTransition;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class GnssLocationProvider extends AbstractLocationProvider implements NtpTimeHelper.InjectNtpTimeCallback, GnssSatelliteBlacklistHelper.GnssSatelliteBlacklistCallback {
    private static final int AGPS_REF_LOCATION_TYPE_GSM_CELLID = 1;
    private static final int AGPS_REF_LOCATION_TYPE_UMTS_CELLID = 2;
    private static final int AGPS_RIL_REQUEST_SETID_IMSI = 1;
    private static final int AGPS_RIL_REQUEST_SETID_MSISDN = 2;
    private static final int AGPS_SETID_TYPE_IMSI = 1;
    private static final int AGPS_SETID_TYPE_MSISDN = 2;
    private static final int AGPS_SETID_TYPE_NONE = 0;
    private static final int AGPS_SUPL_MODE_MSA = 2;
    private static final int AGPS_SUPL_MODE_MSB = 1;
    private static final String ALARM_TIMEOUT = "com.android.internal.location.ALARM_TIMEOUT";
    private static final String ALARM_WAKEUP = "com.android.internal.location.ALARM_WAKEUP";
    /* access modifiers changed from: private */
    public static final boolean DEBUG = Log.isLoggable("GnssLocationProvider", 3);
    private static final String DOWNLOAD_EXTRA_WAKELOCK_KEY = "GnssLocationProviderPsdsDownload";
    private static final int DOWNLOAD_PSDS_DATA = 6;
    private static final int DOWNLOAD_PSDS_DATA_FINISHED = 11;
    private static final long DOWNLOAD_PSDS_DATA_TIMEOUT_MS = 60000;
    private static final int ELAPSED_REALTIME_HAS_TIMESTAMP_NS = 1;
    private static final int ELAPSED_REALTIME_HAS_TIME_UNCERTAINTY_NS = 2;
    private static final int EMERGENCY_LOCATION_UPDATE_DURATION_MULTIPLIER = 3;
    public static final int GPS_CAPABILITY_GEOFENCING = 32;
    public static final int GPS_CAPABILITY_LOW_POWER_MODE = 256;
    public static final int GPS_CAPABILITY_MEASUREMENTS = 64;
    public static final int GPS_CAPABILITY_MEASUREMENT_CORRECTIONS = 1024;
    private static final int GPS_CAPABILITY_MSA = 4;
    private static final int GPS_CAPABILITY_MSB = 2;
    public static final int GPS_CAPABILITY_NAV_MESSAGES = 128;
    private static final int GPS_CAPABILITY_ON_DEMAND_TIME = 16;
    public static final int GPS_CAPABILITY_SATELLITE_BLACKLIST = 512;
    private static final int GPS_CAPABILITY_SCHEDULING = 1;
    private static final int GPS_CAPABILITY_SINGLE_SHOT = 8;
    private static final int GPS_DELETE_ALL = 65535;
    private static final int GPS_DELETE_ALMANAC = 2;
    private static final int GPS_DELETE_CELLDB_INFO = 32768;
    private static final int GPS_DELETE_EPHEMERIS = 1;
    private static final int GPS_DELETE_HEALTH = 64;
    private static final int GPS_DELETE_IONO = 16;
    private static final int GPS_DELETE_POSITION = 4;
    private static final int GPS_DELETE_RTI = 1024;
    private static final int GPS_DELETE_SADATA = 512;
    private static final int GPS_DELETE_SVDIR = 128;
    private static final int GPS_DELETE_SVSTEER = 256;
    private static final int GPS_DELETE_TIME = 8;
    private static final int GPS_DELETE_UTC = 32;
    private static final int GPS_GEOFENCE_AVAILABLE = 2;
    private static final int GPS_GEOFENCE_ERROR_GENERIC = -149;
    private static final int GPS_GEOFENCE_ERROR_ID_EXISTS = -101;
    private static final int GPS_GEOFENCE_ERROR_ID_UNKNOWN = -102;
    private static final int GPS_GEOFENCE_ERROR_INVALID_TRANSITION = -103;
    private static final int GPS_GEOFENCE_ERROR_TOO_MANY_GEOFENCES = 100;
    private static final int GPS_GEOFENCE_OPERATION_SUCCESS = 0;
    private static final int GPS_GEOFENCE_UNAVAILABLE = 1;
    private static final int GPS_POLLING_THRESHOLD_INTERVAL = 10000;
    private static final int GPS_POSITION_MODE_MS_ASSISTED = 2;
    private static final int GPS_POSITION_MODE_MS_BASED = 1;
    private static final int GPS_POSITION_MODE_STANDALONE = 0;
    private static final int GPS_POSITION_RECURRENCE_PERIODIC = 0;
    private static final int GPS_POSITION_RECURRENCE_SINGLE = 1;
    private static final int GPS_STATUS_ENGINE_OFF = 4;
    private static final int GPS_STATUS_ENGINE_ON = 3;
    private static final int GPS_STATUS_NONE = 0;
    private static final int GPS_STATUS_SESSION_BEGIN = 1;
    private static final int GPS_STATUS_SESSION_END = 2;
    private static final int INITIALIZE_HANDLER = 13;
    private static final int INJECT_NTP_TIME = 5;
    private static final float ITAR_SPEED_LIMIT_METERS_PER_SECOND = 400.0f;
    private static final int LOCATION_HAS_ALTITUDE = 2;
    private static final int LOCATION_HAS_BEARING = 8;
    private static final int LOCATION_HAS_BEARING_ACCURACY = 128;
    private static final int LOCATION_HAS_HORIZONTAL_ACCURACY = 16;
    private static final int LOCATION_HAS_LAT_LONG = 1;
    private static final int LOCATION_HAS_SPEED = 4;
    private static final int LOCATION_HAS_SPEED_ACCURACY = 64;
    private static final int LOCATION_HAS_VERTICAL_ACCURACY = 32;
    private static final int LOCATION_INVALID = 0;
    private static final long LOCATION_OFF_DELAY_THRESHOLD_ERROR_MILLIS = 15000;
    private static final long LOCATION_OFF_DELAY_THRESHOLD_WARN_MILLIS = 2000;
    private static final long LOCATION_UPDATE_DURATION_MILLIS = 10000;
    private static final long LOCATION_UPDATE_MIN_TIME_INTERVAL_MILLIS = 1000;
    private static final long MAX_RETRY_INTERVAL = 14400000;
    private static final int NO_FIX_TIMEOUT = 60000;
    private static final ProviderProperties PROPERTIES = new ProviderProperties(true, true, false, false, true, true, true, 3, 1);
    private static final long RECENT_FIX_TIMEOUT = 10000;
    private static final int REPORT_LOCATION = 17;
    private static final int REPORT_SV_STATUS = 18;
    private static final int REQUEST_LOCATION = 16;
    private static final long RETRY_INTERVAL = 300000;
    private static final int SET_REQUEST = 3;
    private static final int STATE_DOWNLOADING = 1;
    private static final int STATE_IDLE = 2;
    private static final int STATE_PENDING_NETWORK = 0;
    private static final String TAG = "GnssLocationProvider";
    private static final int TCP_MAX_PORT = 65535;
    private static final int TCP_MIN_PORT = 0;
    private static final int UPDATE_LOCATION = 7;
    private static final boolean VERBOSE = Log.isLoggable("GnssLocationProvider", 2);
    private static final String WAKELOCK_KEY = "GnssLocationProvider";
    private final AlarmManager mAlarmManager;
    private final AppOpsManager mAppOps;
    private final IBatteryStats mBatteryStats;
    /* access modifiers changed from: private */
    public final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (GnssLocationProvider.DEBUG) {
                Log.d("GnssLocationProvider", "receive broadcast intent, action: " + action);
            }
            if (action != null) {
                char c = 65535;
                switch (action.hashCode()) {
                    case -2128145023:
                        if (action.equals("android.intent.action.SCREEN_OFF")) {
                            c = 4;
                            break;
                        }
                        break;
                    case -1992416737:
                        if (action.equals(GnssLocationProvider.ALARM_TIMEOUT)) {
                            c = 1;
                            break;
                        }
                        break;
                    case -1454123155:
                        if (action.equals("android.intent.action.SCREEN_ON")) {
                            c = 5;
                            break;
                        }
                        break;
                    case -1138588223:
                        if (action.equals("android.telephony.action.CARRIER_CONFIG_CHANGED")) {
                            c = 6;
                            break;
                        }
                        break;
                    case -678568287:
                        if (action.equals(GnssLocationProvider.ALARM_WAKEUP)) {
                            c = 0;
                            break;
                        }
                        break;
                    case -25388475:
                        if (action.equals("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED")) {
                            c = 7;
                            break;
                        }
                        break;
                    case 870701415:
                        if (action.equals("android.os.action.DEVICE_IDLE_MODE_CHANGED")) {
                            c = 3;
                            break;
                        }
                        break;
                    case 1779291251:
                        if (action.equals("android.os.action.POWER_SAVE_MODE_CHANGED")) {
                            c = 2;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        GnssLocationProvider.this.startNavigating();
                        return;
                    case 1:
                        GnssLocationProvider.this.hibernate();
                        return;
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                        GnssLocationProvider.this.updateLowPowerMode();
                        return;
                    case 6:
                    case 7:
                        GnssLocationProvider.this.subscriptionOrCarrierConfigChanged(context);
                        return;
                    default:
                        return;
                }
            }
        }
    };
    private String mC2KServerHost;
    private int mC2KServerPort;
    private WorkSource mClientSource = new WorkSource();
    private boolean mDisableGpsForPowerManager = false;
    /* access modifiers changed from: private */
    public int mDownloadPsdsDataPending = 0;
    @GuardedBy({"mLock"})
    private final PowerManager.WakeLock mDownloadPsdsWakeLock;
    private int mFixInterval = 1000;
    private long mFixRequestTime = 0;
    private final LocationChangeListener mFusedLocationListener = new FusedLocationListener();
    private GeofenceHardwareImpl mGeofenceHardwareImpl;
    private final GnssBatchingProvider mGnssBatchingProvider;
    private final GnssCapabilitiesProvider mGnssCapabilitiesProvider;
    private GnssConfiguration mGnssConfiguration;
    private final GnssGeofenceProvider mGnssGeofenceProvider;
    private final GnssMeasurementCorrectionsProvider mGnssMeasurementCorrectionsProvider;
    private final GnssMeasurementsProvider mGnssMeasurementsProvider;
    private GnssMetrics mGnssMetrics;
    private final GnssNavigationMessageProvider mGnssNavigationMessageProvider;
    private final GnssStatusListenerHelper mGnssStatusListenerHelper;
    /* access modifiers changed from: private */
    public GnssVisibilityControl mGnssVisibilityControl;
    @GuardedBy({"mLock"})
    private boolean mGpsEnabled;
    private Handler mHandler;
    /* access modifiers changed from: private */
    public volatile String mHardwareModelName;
    /* access modifiers changed from: private */
    public volatile int mHardwareYear = 0;
    private volatile boolean mItarSpeedLimitExceeded = false;
    private long mLastFixTime;
    private GnssPositionMode mLastPositionMode;
    private final LocationExtras mLocationExtras = new LocationExtras();
    private final Object mLock = new Object();
    /* access modifiers changed from: private */
    public final Looper mLooper;
    private boolean mLowPowerMode = false;
    /* access modifiers changed from: private */
    public final GpsNetInitiatedHandler mNIHandler;
    private boolean mNavigating;
    private final INetInitiatedListener mNetInitiatedListener = new INetInitiatedListener.Stub() {
        public boolean sendNiResponse(int notificationId, int userResponse) {
            int i = notificationId;
            int i2 = userResponse;
            if (GnssLocationProvider.DEBUG) {
                Log.d("GnssLocationProvider", "sendNiResponse, notifId: " + i + ", response: " + i2);
            }
            GnssLocationProvider.this.native_send_ni_response(i, i2);
            StatsLog.write(124, 2, notificationId, 0, false, false, false, 0, 0, (String) null, (String) null, 0, 0, GnssLocationProvider.this.mSuplEsEnabled, GnssLocationProvider.this.isGpsEnabled(), userResponse);
            return true;
        }
    };
    /* access modifiers changed from: private */
    public final GnssNetworkConnectivityHandler mNetworkConnectivityHandler;
    private final LocationChangeListener mNetworkLocationListener = new NetworkLocationListener();
    private byte[] mNmeaBuffer = new byte[120];
    /* access modifiers changed from: private */
    public final NtpTimeHelper mNtpTimeHelper;
    private int mPositionMode;
    private final PowerManager mPowerManager;
    private ProviderRequest mProviderRequest;
    private final ExponentialBackOff mPsdsBackOff = new ExponentialBackOff(300000, 14400000);
    private boolean mRecordLoseCount = true;
    /* access modifiers changed from: private */
    public boolean mShutdown;
    private boolean mStarted;
    private long mStartedChangedElapsedRealtime;
    private int mStatus = 1;
    private long mStatusUpdateTime = SystemClock.elapsedRealtime();
    /* access modifiers changed from: private */
    public boolean mSuplEsEnabled = false;
    private String mSuplServerHost;
    private int mSuplServerPort = 0;
    private boolean mSupportsPsds;
    private int mTimeToFirstFix = 0;
    private final PendingIntent mTimeoutIntent;
    private volatile int mTopHalCapabilities;
    /* access modifiers changed from: private */
    public final PowerManager.WakeLock mWakeLock;
    private final PendingIntent mWakeupIntent;
    private WorkSource mWorkSource = null;
    private final GnssLocalLog mdumpNmea = new GnssLocalLog(20000);
    private final GnssLocalLog mdumpStatus = new GnssLocalLog(500);

    public interface GnssMetricsProvider {
        String getGnssMetricsAsProtoString();
    }

    public interface GnssSystemInfoProvider {
        String getGnssHardwareModelName();

        int getGnssYearOfHardware();
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.SOURCE)
    private @interface NativeEntryPoint {
    }

    private static native void class_init_native();

    private native void native_agps_ni_message(byte[] bArr, int i);

    private native void native_agps_set_id(int i, String str);

    private native void native_agps_set_ref_location_cellid(int i, int i2, int i3, int i4, int i5);

    private native void native_cleanup();

    private native void native_delete_aiding_data(int i);

    private native String native_get_internal_state();

    private native boolean native_init();

    private static native void native_init_once(boolean z);

    private native void native_inject_best_location(int i, double d, double d2, double d3, float f, float f2, float f3, float f4, float f5, float f6, long j, int i2, long j2, double d4);

    private native void native_inject_location(double d, double d2, float f);

    private native void native_inject_psds_data(byte[] bArr, int i);

    private native void native_inject_time(long j, long j2, int i);

    /* access modifiers changed from: private */
    public static native boolean native_is_gnss_visibility_control_supported();

    private static native boolean native_is_supported();

    private native int native_read_nmea(byte[] bArr, int i);

    /* access modifiers changed from: private */
    public native void native_send_ni_response(int i, int i2);

    private native void native_set_agps_server(int i, String str, int i2);

    private native boolean native_set_position_mode(int i, int i2, int i3, int i4, int i5, boolean z);

    private native boolean native_start();

    private native boolean native_stop();

    private native boolean native_supports_psds();

    static {
        class_init_native();
    }

    private static class GpsRequest {
        public ProviderRequest request;
        public WorkSource source;

        public GpsRequest(ProviderRequest request2, WorkSource source2) {
            this.request = request2;
            this.source = source2;
        }
    }

    private static class LocationExtras {
        private final Bundle mBundle = new Bundle();
        private int mMaxCn0;
        private int mMeanCn0;
        private int mSvCount;

        public void set(int svCount, int meanCn0, int maxCn0) {
            synchronized (this) {
                this.mSvCount = svCount;
                this.mMeanCn0 = meanCn0;
                this.mMaxCn0 = maxCn0;
            }
            setBundle(this.mBundle);
        }

        public void reset() {
            set(0, 0, 0);
        }

        public void setBundle(Bundle extras) {
            if (extras != null) {
                synchronized (this) {
                    extras.putInt("satellites", this.mSvCount);
                    extras.putInt("meanCn0", this.mMeanCn0);
                    extras.putInt("maxCn0", this.mMaxCn0);
                }
            }
        }

        public Bundle getBundle() {
            Bundle bundle;
            synchronized (this) {
                bundle = new Bundle(this.mBundle);
            }
            return bundle;
        }
    }

    public GnssStatusListenerHelper getGnssStatusProvider() {
        return this.mGnssStatusListenerHelper;
    }

    public IGpsGeofenceHardware getGpsGeofenceProxy() {
        return this.mGnssGeofenceProvider;
    }

    public GnssMeasurementsProvider getGnssMeasurementsProvider() {
        return this.mGnssMeasurementsProvider;
    }

    public GnssMeasurementCorrectionsProvider getGnssMeasurementCorrectionsProvider() {
        return this.mGnssMeasurementCorrectionsProvider;
    }

    public GnssNavigationMessageProvider getGnssNavigationMessageProvider() {
        return this.mGnssNavigationMessageProvider;
    }

    public /* synthetic */ void lambda$onUpdateSatelliteBlacklist$0$GnssLocationProvider(int[] constellations, int[] svids) {
        this.mGnssConfiguration.setSatelliteBlacklist(constellations, svids);
    }

    public void onUpdateSatelliteBlacklist(int[] constellations, int[] svids) {
        this.mHandler.post(new Runnable(constellations, svids) {
            private final /* synthetic */ int[] f$1;
            private final /* synthetic */ int[] f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                GnssLocationProvider.this.lambda$onUpdateSatelliteBlacklist$0$GnssLocationProvider(this.f$1, this.f$2);
            }
        });
        this.mGnssMetrics.resetConstellationTypes();
    }

    /* access modifiers changed from: private */
    public void subscriptionOrCarrierConfigChanged(Context context) {
        if (DEBUG) {
            Log.d("GnssLocationProvider", "received SIM related action: ");
        }
        TelephonyManager phone = (TelephonyManager) this.mContext.getSystemService("phone");
        CarrierConfigManager configManager = (CarrierConfigManager) this.mContext.getSystemService("carrier_config");
        int ddSubId = SubscriptionManager.getDefaultDataSubscriptionId();
        String mccMnc = SubscriptionManager.isValidSubscriptionId(ddSubId) ? phone.getSimOperator(ddSubId) : phone.getSimOperator();
        boolean isKeepLppProfile = false;
        if (!TextUtils.isEmpty(mccMnc)) {
            if (DEBUG) {
                Log.d("GnssLocationProvider", "SIM MCC/MNC is available: " + mccMnc);
            }
            if (configManager != null) {
                PersistableBundle b = SubscriptionManager.isValidSubscriptionId(ddSubId) ? configManager.getConfigForSubId(ddSubId) : null;
                if (b != null) {
                    isKeepLppProfile = b.getBoolean("gps.persist_lpp_mode_bool");
                }
            }
            if (isKeepLppProfile) {
                this.mGnssConfiguration.loadPropertiesFromCarrierConfig();
                String lpp_profile = this.mGnssConfiguration.getLppProfile();
                if (lpp_profile != null) {
                    SystemProperties.set("persist.sys.gps.lpp", lpp_profile);
                }
            } else {
                SystemProperties.set("persist.sys.gps.lpp", "");
            }
            reloadGpsProperties();
        } else if (DEBUG) {
            Log.d("GnssLocationProvider", "SIM MCC/MNC is still not available");
        }
    }

    /* access modifiers changed from: private */
    public void updateLowPowerMode() {
        boolean disableGpsForPowerManager = this.mPowerManager.isDeviceIdleMode();
        boolean z = true;
        PowerSaveState result = this.mPowerManager.getPowerSaveState(1);
        int i = result.locationMode;
        if (i == 1 || i == 2) {
            if (!result.batterySaverEnabled || this.mPowerManager.isInteractive()) {
                z = false;
            }
            disableGpsForPowerManager |= z;
        }
        if (disableGpsForPowerManager != this.mDisableGpsForPowerManager) {
            this.mDisableGpsForPowerManager = disableGpsForPowerManager;
            updateEnabled();
            updateRequirements();
        }
    }

    public static boolean isSupported() {
        return native_is_supported();
    }

    /* access modifiers changed from: private */
    public void reloadGpsProperties() {
        this.mGnssConfiguration.reloadGpsProperties();
        setSuplHostPort();
        this.mC2KServerHost = this.mGnssConfiguration.getC2KHost();
        boolean z = false;
        this.mC2KServerPort = this.mGnssConfiguration.getC2KPort(0);
        this.mNIHandler.setEmergencyExtensionSeconds(this.mGnssConfiguration.getEsExtensionSec());
        if (this.mGnssConfiguration.getSuplEs(0) == 1) {
            z = true;
        }
        this.mSuplEsEnabled = z;
        this.mdumpNmea.setLength(Integer.parseInt(this.mGnssConfiguration.getNMEALen()));
        this.mNIHandler.setSuplEsEnabled(this.mSuplEsEnabled);
        GnssVisibilityControl gnssVisibilityControl = this.mGnssVisibilityControl;
        if (gnssVisibilityControl != null) {
            gnssVisibilityControl.onConfigurationUpdated(this.mGnssConfiguration);
        }
    }

    public GnssLocationProvider(Context context, AbstractLocationProvider.LocationProviderManager locationProviderManager, Looper looper) {
        super(context, locationProviderManager);
        this.mLooper = looper;
        this.mPowerManager = (PowerManager) this.mContext.getSystemService("power");
        this.mWakeLock = this.mPowerManager.newWakeLock(1, "GnssLocationProvider");
        this.mWakeLock.setReferenceCounted(true);
        this.mDownloadPsdsWakeLock = this.mPowerManager.newWakeLock(1, DOWNLOAD_EXTRA_WAKELOCK_KEY);
        this.mDownloadPsdsWakeLock.setReferenceCounted(true);
        this.mAlarmManager = (AlarmManager) this.mContext.getSystemService("alarm");
        this.mWakeupIntent = PendingIntent.getBroadcast(this.mContext, 0, new Intent(ALARM_WAKEUP), 0);
        this.mTimeoutIntent = PendingIntent.getBroadcast(this.mContext, 0, new Intent(ALARM_TIMEOUT), 0);
        this.mNetworkConnectivityHandler = new GnssNetworkConnectivityHandler(context, new GnssNetworkConnectivityHandler.GnssNetworkListener() {
            public final void onNetworkAvailable() {
                GnssLocationProvider.this.onNetworkAvailable();
            }
        }, looper);
        this.mAppOps = (AppOpsManager) this.mContext.getSystemService(AppOpsManager.class);
        this.mBatteryStats = IBatteryStats.Stub.asInterface(ServiceManager.getService("batterystats"));
        this.mHandler = new ProviderHandler(looper);
        this.mGnssConfiguration = new GnssConfiguration(this.mContext);
        this.mGnssCapabilitiesProvider = new GnssCapabilitiesProvider();
        this.mNIHandler = new GpsNetInitiatedHandler(context, this.mNetInitiatedListener, this.mSuplEsEnabled);
        sendMessage(13, 0, (Object) null);
        this.mGnssStatusListenerHelper = new GnssStatusListenerHelper(this.mContext, this.mHandler) {
            /* access modifiers changed from: protected */
            public boolean isAvailableInPlatform() {
                return GnssLocationProvider.isSupported();
            }

            /* access modifiers changed from: protected */
            public boolean isGpsEnabled() {
                return GnssLocationProvider.this.isGpsEnabled();
            }
        };
        this.mGnssMeasurementsProvider = new GnssMeasurementsProvider(this.mContext, this.mHandler) {
            /* access modifiers changed from: protected */
            public boolean isGpsEnabled() {
                return GnssLocationProvider.this.isGpsEnabled();
            }
        };
        this.mGnssMeasurementCorrectionsProvider = new GnssMeasurementCorrectionsProvider(this.mHandler);
        this.mGnssNavigationMessageProvider = new GnssNavigationMessageProvider(this.mContext, this.mHandler) {
            /* access modifiers changed from: protected */
            public boolean isGpsEnabled() {
                return GnssLocationProvider.this.isGpsEnabled();
            }
        };
        this.mGnssMetrics = new GnssMetrics(this.mBatteryStats);
        this.mNtpTimeHelper = new NtpTimeHelper(this.mContext, looper, this);
        GnssSatelliteBlacklistHelper gnssSatelliteBlacklistHelper = new GnssSatelliteBlacklistHelper(this.mContext, looper, this);
        Handler handler = this.mHandler;
        Objects.requireNonNull(gnssSatelliteBlacklistHelper);
        handler.post(new Runnable() {
            public final void run() {
                GnssSatelliteBlacklistHelper.this.updateSatelliteBlacklist();
            }
        });
        this.mGnssBatchingProvider = new GnssBatchingProvider();
        this.mGnssGeofenceProvider = new GnssGeofenceProvider();
        this.mContext.registerReceiverAsUser(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (getSendingUserId() == -1) {
                    boolean unused = GnssLocationProvider.this.mShutdown = true;
                    GnssLocationProvider.this.updateEnabled();
                }
            }
        }, UserHandle.ALL, new IntentFilter("android.intent.action.ACTION_SHUTDOWN"), (String) null, this.mHandler);
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("location_mode"), true, new ContentObserver(this.mHandler) {
            public void onChange(boolean selfChange) {
                GnssLocationProvider.this.updateEnabled();
            }
        }, -1);
        setProperties(PROPERTIES);
        setEnabled(true);
    }

    public void injectTime(long time, long timeReference, int uncertainty) {
        native_inject_time(time, timeReference, uncertainty);
    }

    /* access modifiers changed from: private */
    public void onNetworkAvailable() {
        this.mNtpTimeHelper.onNetworkAvailable();
        if (this.mDownloadPsdsDataPending == 0 && this.mSupportsPsds) {
            psdsDownloadRequest();
        }
    }

    /* access modifiers changed from: private */
    public void handleRequestLocation(boolean independentFromGnss, boolean isUserEmergency) {
        LocationChangeListener locationListener;
        String provider;
        addToBugreport("request injectNtpTime");
        if (!isRequestLocationRateLimited()) {
            long durationMillis = Settings.Global.getLong(this.mContext.getContentResolver(), "gnss_hal_location_request_duration_millis", JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY);
            if (durationMillis == 0) {
                Log.i("GnssLocationProvider", "GNSS HAL location request is disabled by Settings.");
                return;
            }
            LocationManager locationManager = (LocationManager) this.mContext.getSystemService("location");
            LocationRequest locationRequest = new LocationRequest().setInterval(1000).setFastestInterval(1000);
            if (independentFromGnss) {
                provider = "network";
                locationListener = this.mNetworkLocationListener;
                locationRequest.setQuality(MultiWindowTransition.TRANSIT_MULTI_WINDOW_ENTER_FROM_TOP);
            } else {
                provider = "fused";
                locationListener = this.mFusedLocationListener;
                locationRequest.setQuality(100);
            }
            locationRequest.setProvider(provider);
            if (isUserEmergency && this.mNIHandler.getInEmergency()) {
                locationRequest.setLocationSettingsIgnored(true);
                durationMillis *= 3;
            }
            Log.i("GnssLocationProvider", String.format("GNSS HAL Requesting location updates from %s provider for %d millis.", new Object[]{provider, Long.valueOf(durationMillis)}));
            try {
                locationManager.requestLocationUpdates(locationRequest, locationListener, this.mHandler.getLooper());
                LocationChangeListener.access$1008(locationListener);
                this.mHandler.postDelayed(new Runnable(provider, locationManager) {
                    private final /* synthetic */ String f$1;
                    private final /* synthetic */ LocationManager f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        GnssLocationProvider.lambda$handleRequestLocation$1(GnssLocationProvider.LocationChangeListener.this, this.f$1, this.f$2);
                    }
                }, durationMillis);
            } catch (IllegalArgumentException e) {
                Log.w("GnssLocationProvider", "Unable to request location.", e);
            }
        } else if (DEBUG) {
            Log.d("GnssLocationProvider", "RequestLocation is denied due to too frequent requests.");
        }
    }

    static /* synthetic */ void lambda$handleRequestLocation$1(LocationChangeListener locationListener, String provider, LocationManager locationManager) {
        if (LocationChangeListener.access$1006(locationListener) == 0) {
            Log.i("GnssLocationProvider", String.format("Removing location updates from %s provider.", new Object[]{provider}));
            locationManager.removeUpdates(locationListener);
        }
    }

    /* access modifiers changed from: private */
    public void injectBestLocation(Location location) {
        if (DEBUG) {
            Log.d("GnssLocationProvider", "injectBestLocation: " + location);
        } else {
            Location location2 = location;
        }
        int i = 2;
        int gnssLocationFlags = (location.hasAltitude() ? 2 : 0) | 1 | (location.hasSpeed() ? 4 : 0) | (location.hasBearing() ? 8 : 0) | (location.hasAccuracy() ? 16 : 0) | (location.hasVerticalAccuracy() ? 32 : 0) | (location.hasSpeedAccuracy() ? 64 : 0) | (location.hasBearingAccuracy() ? 128 : 0);
        double latitudeDegrees = location.getLatitude();
        double longitudeDegrees = location.getLongitude();
        double altitudeMeters = location.getAltitude();
        float speedMetersPerSec = location.getSpeed();
        float bearingDegrees = location.getBearing();
        float horizontalAccuracyMeters = location.getAccuracy();
        float verticalAccuracyMeters = location.getVerticalAccuracyMeters();
        float speedAccuracyMetersPerSecond = location.getSpeedAccuracyMetersPerSecond();
        float bearingAccuracyDegrees = location.getBearingAccuracyDegrees();
        long timestamp = location.getTime();
        if (!location.hasElapsedRealtimeUncertaintyNanos()) {
            i = 0;
        }
        native_inject_best_location(gnssLocationFlags, latitudeDegrees, longitudeDegrees, altitudeMeters, speedMetersPerSec, bearingDegrees, horizontalAccuracyMeters, verticalAccuracyMeters, speedAccuracyMetersPerSecond, bearingAccuracyDegrees, timestamp, i | 1, location.getElapsedRealtimeNanos(), location.getElapsedRealtimeUncertaintyNanos());
    }

    private boolean isRequestLocationRateLimited() {
        return false;
    }

    /* access modifiers changed from: private */
    public void handleDownloadPsdsData() {
        if (!this.mSupportsPsds) {
            Log.d("GnssLocationProvider", "handleDownloadPsdsData() called when PSDS not supported");
        } else if (this.mDownloadPsdsDataPending != 1) {
            if (!this.mNetworkConnectivityHandler.isDataNetworkConnected()) {
                this.mDownloadPsdsDataPending = 0;
                return;
            }
            this.mDownloadPsdsDataPending = 1;
            synchronized (this.mLock) {
                this.mDownloadPsdsWakeLock.acquire(60000);
            }
            Log.i("GnssLocationProvider", "WakeLock acquired by handleDownloadPsdsData()");
            AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
                public final void run() {
                    GnssLocationProvider.this.lambda$handleDownloadPsdsData$2$GnssLocationProvider();
                }
            });
        }
    }

    public /* synthetic */ void lambda$handleDownloadPsdsData$2$GnssLocationProvider() {
        byte[] data = new GpsPsdsDownloader(this.mGnssConfiguration.getProperties()).downloadPsdsData();
        if (data != null) {
            if (DEBUG) {
                Log.d("GnssLocationProvider", "calling native_inject_psds_data");
            }
            addToBugreport("inject XTRA data");
            native_inject_psds_data(data, data.length);
            this.mPsdsBackOff.reset();
        }
        sendMessage(11, 0, (Object) null);
        if (data == null) {
            this.mHandler.sendEmptyMessageDelayed(6, this.mPsdsBackOff.nextBackoffMillis());
        }
        synchronized (this.mLock) {
            if (this.mDownloadPsdsWakeLock.isHeld()) {
                try {
                    this.mDownloadPsdsWakeLock.release();
                    if (DEBUG) {
                        Log.d("GnssLocationProvider", "WakeLock released by handleDownloadPsdsData()");
                    }
                } catch (Exception e) {
                    Log.i("GnssLocationProvider", "Wakelock timeout & release race exception in handleDownloadPsdsData()", e);
                }
            } else {
                Log.e("GnssLocationProvider", "WakeLock expired before release in handleDownloadPsdsData()");
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleUpdateLocation(Location location) {
        if (location.hasAccuracy()) {
            if (DEBUG) {
                Log.d("GnssLocationProvider", "injectLocation: " + location);
            }
            native_inject_location(location.getLatitude(), location.getLongitude(), location.getAccuracy());
        }
    }

    private void setSuplHostPort() {
        int i;
        this.mSuplServerHost = this.mGnssConfiguration.getSuplHost();
        this.mSuplServerPort = this.mGnssConfiguration.getSuplPort(0);
        String str = this.mSuplServerHost;
        if (str != null && (i = this.mSuplServerPort) > 0 && i <= 65535) {
            native_set_agps_server(1, str, i);
            addToBugreport("set supl host " + this.mSuplServerHost + " " + this.mSuplServerPort);
        }
    }

    private int getSuplMode(boolean agpsEnabled) {
        int suplMode;
        if (!agpsEnabled || (suplMode = this.mGnssConfiguration.getSuplMode(0)) == 0 || !hasCapability(2) || (suplMode & 1) == 0) {
            return 0;
        }
        return 1;
    }

    private void setGpsEnabled(boolean enabled) {
        synchronized (this.mLock) {
            this.mGpsEnabled = enabled;
        }
    }

    private void handleEnable() {
        if (DEBUG) {
            Log.d("GnssLocationProvider", "handleEnable");
        }
        boolean inited = native_init();
        addToBugreport("enable gnss ");
        if (inited) {
            setGpsEnabled(true);
            this.mSupportsPsds = native_supports_psds();
            String str = this.mSuplServerHost;
            if (str != null) {
                native_set_agps_server(1, str, this.mSuplServerPort);
            }
            String str2 = this.mC2KServerHost;
            if (str2 != null) {
                native_set_agps_server(2, str2, this.mC2KServerPort);
            }
            this.mGnssMeasurementsProvider.onGpsEnabledChanged();
            this.mGnssNavigationMessageProvider.onGpsEnabledChanged();
            this.mGnssBatchingProvider.enable();
            GnssVisibilityControl gnssVisibilityControl = this.mGnssVisibilityControl;
            if (gnssVisibilityControl != null) {
                gnssVisibilityControl.onGpsEnabledChanged(true);
                return;
            }
            return;
        }
        setGpsEnabled(false);
        Log.w("GnssLocationProvider", "Failed to enable location provider");
    }

    private void handleDisable() {
        if (DEBUG) {
            Log.d("GnssLocationProvider", "handleDisable");
        }
        setGpsEnabled(false);
        updateClientUids(new WorkSource());
        stopNavigating();
        this.mAlarmManager.cancel(this.mWakeupIntent);
        this.mAlarmManager.cancel(this.mTimeoutIntent);
        GnssVisibilityControl gnssVisibilityControl = this.mGnssVisibilityControl;
        if (gnssVisibilityControl != null) {
            gnssVisibilityControl.onGpsEnabledChanged(false);
        }
        this.mGnssBatchingProvider.disable();
        native_cleanup();
        this.mdumpStatus.clearData();
        this.mdumpNmea.clearData();
        addToBugreport("disable gnss ");
        this.mGnssMeasurementsProvider.onGpsEnabledChanged();
        this.mGnssNavigationMessageProvider.onGpsEnabledChanged();
    }

    /* access modifiers changed from: private */
    public void updateEnabled() {
        boolean enabled = ((LocationManager) this.mContext.getSystemService(LocationManager.class)).isLocationEnabledForUser(UserHandle.CURRENT) & (!this.mDisableGpsForPowerManager);
        ProviderRequest providerRequest = this.mProviderRequest;
        boolean enabled2 = (enabled | (providerRequest != null && providerRequest.reportLocation && this.mProviderRequest.locationSettingsIgnored)) & (!this.mShutdown);
        if (enabled2 != isGpsEnabled()) {
            if (enabled2) {
                handleEnable();
            } else {
                handleDisable();
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean isGpsEnabled() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mGpsEnabled;
        }
        return z;
    }

    public int getStatus(Bundle extras) {
        this.mLocationExtras.setBundle(extras);
        return this.mStatus;
    }

    private void updateStatus(int status) {
        if (status != this.mStatus) {
            this.mStatus = status;
            this.mStatusUpdateTime = SystemClock.elapsedRealtime();
        }
    }

    public long getStatusUpdateTime() {
        return this.mStatusUpdateTime;
    }

    public void setRequest(ProviderRequest request, WorkSource source) {
        sendMessage(3, 0, new GpsRequest(request, source));
    }

    /* access modifiers changed from: private */
    public void handleSetRequest(ProviderRequest request, WorkSource source) {
        this.mProviderRequest = request;
        this.mWorkSource = source;
        updateEnabled();
        updateRequirements();
    }

    private void updateRequirements() {
        if (this.mProviderRequest != null && this.mWorkSource != null) {
            if (DEBUG) {
                Log.d("GnssLocationProvider", "setRequest " + this.mProviderRequest);
            }
            if (!this.mProviderRequest.reportLocation || !isGpsEnabled()) {
                updateClientUids(new WorkSource());
                stopNavigating();
                this.mAlarmManager.cancel(this.mWakeupIntent);
                this.mAlarmManager.cancel(this.mTimeoutIntent);
                return;
            }
            updateClientUids(this.mWorkSource);
            this.mFixInterval = (int) this.mProviderRequest.interval;
            this.mLowPowerMode = this.mProviderRequest.lowPowerMode;
            if (((long) this.mFixInterval) != this.mProviderRequest.interval) {
                Log.w("GnssLocationProvider", "interval overflow: " + this.mProviderRequest.interval);
                this.mFixInterval = Integer.MAX_VALUE;
            }
            if (this.mStarted && hasCapability(1)) {
                if (!setPositionMode(this.mPositionMode, 0, this.mFixInterval, 0, 0, this.mLowPowerMode)) {
                    Log.e("GnssLocationProvider", "set_position_mode failed in updateRequirements");
                }
            } else if (!this.mStarted) {
                startNavigating();
            } else {
                this.mAlarmManager.cancel(this.mTimeoutIntent);
                if (this.mFixInterval >= NO_FIX_TIMEOUT) {
                    this.mAlarmManager.set(2, SystemClock.elapsedRealtime() + 60000, this.mTimeoutIntent);
                }
            }
        }
    }

    private boolean setPositionMode(int mode, int recurrence, int minInterval, int preferredAccuracy, int preferredTime, boolean lowPowerMode) {
        GnssPositionMode positionMode = new GnssPositionMode(mode, recurrence, minInterval, preferredAccuracy, preferredTime, lowPowerMode);
        GnssPositionMode gnssPositionMode = this.mLastPositionMode;
        if (gnssPositionMode != null && gnssPositionMode.equals(positionMode)) {
            return true;
        }
        boolean result = native_set_position_mode(mode, recurrence, minInterval, preferredAccuracy, preferredTime, lowPowerMode);
        if (result) {
            this.mLastPositionMode = positionMode;
        } else {
            this.mLastPositionMode = null;
        }
        return result;
    }

    private void updateClientUids(WorkSource source) {
        if (!source.equals(this.mClientSource)) {
            try {
                this.mBatteryStats.noteGpsChanged(this.mClientSource, source);
            } catch (RemoteException e) {
                Log.w("GnssLocationProvider", "RemoteException", e);
            }
            List<WorkSource.WorkChain>[] diffs = WorkSource.diffChains(this.mClientSource, source);
            if (diffs != null) {
                List<WorkSource.WorkChain> newChains = diffs[0];
                List<WorkSource.WorkChain> goneChains = diffs[1];
                if (newChains != null) {
                    for (WorkSource.WorkChain newChain : newChains) {
                        this.mAppOps.startOpNoThrow(2, newChain.getAttributionUid(), newChain.getAttributionTag());
                    }
                }
                if (goneChains != null) {
                    for (WorkSource.WorkChain goneChain : goneChains) {
                        this.mAppOps.finishOp(2, goneChain.getAttributionUid(), goneChain.getAttributionTag());
                    }
                }
                this.mClientSource.transferWorkChains(source);
            }
            WorkSource[] changes = this.mClientSource.setReturningDiffs(source);
            if (changes != null) {
                WorkSource newWork = changes[0];
                WorkSource goneWork = changes[1];
                if (newWork != null) {
                    for (int i = 0; i < newWork.size(); i++) {
                        this.mAppOps.startOpNoThrow(2, newWork.get(i), newWork.getName(i));
                    }
                }
                if (goneWork != null) {
                    for (int i2 = 0; i2 < goneWork.size(); i2++) {
                        this.mAppOps.finishOp(2, goneWork.get(i2), goneWork.getName(i2));
                    }
                }
            }
        }
    }

    public void sendExtraCommand(String command, Bundle extras) {
        long identity = Binder.clearCallingIdentity();
        try {
            if ("delete_aiding_data".equals(command)) {
                deleteAidingData(extras);
            } else if ("force_time_injection".equals(command)) {
                requestUtcTime();
            } else if (!"force_psds_injection".equals(command)) {
                Log.w("GnssLocationProvider", "sendExtraCommand: unknown command " + command);
            } else if (this.mSupportsPsds) {
                psdsDownloadRequest();
            }
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    private void deleteAidingData(Bundle extras) {
        int flags;
        if (extras == null) {
            flags = 65535;
        } else {
            flags = 0;
            if (extras.getBoolean("ephemeris")) {
                flags = 0 | 1;
            }
            if (extras.getBoolean("almanac")) {
                flags |= 2;
            }
            if (extras.getBoolean("position")) {
                flags |= 4;
            }
            if (extras.getBoolean(SplitScreenReporter.STR_DEAL_TIME)) {
                flags |= 8;
            }
            if (extras.getBoolean("iono")) {
                flags |= 16;
            }
            if (extras.getBoolean("utc")) {
                flags |= 32;
            }
            if (extras.getBoolean("health")) {
                flags |= 64;
            }
            if (extras.getBoolean("svdir")) {
                flags |= 128;
            }
            if (extras.getBoolean("svsteer")) {
                flags |= 256;
            }
            if (extras.getBoolean("sadata")) {
                flags |= 512;
            }
            if (extras.getBoolean("rti")) {
                flags |= 1024;
            }
            if (extras.getBoolean("celldb-info")) {
                flags |= 32768;
            }
            if (extras.getBoolean("all")) {
                flags |= 65535;
            }
        }
        if (flags != 0) {
            addToBugreport("delete aiding data " + flags);
            native_delete_aiding_data(flags);
        }
    }

    /* access modifiers changed from: private */
    public void startNavigating() {
        String mode;
        if (!this.mStarted) {
            if (DEBUG) {
                Log.d("GnssLocationProvider", "startNavigating");
            }
            this.mTimeToFirstFix = 0;
            this.mLastFixTime = 0;
            setStarted(true);
            this.mPositionMode = 0;
            if (this.mItarSpeedLimitExceeded) {
                Log.i("GnssLocationProvider", "startNavigating with ITAR limit in place. Output limited  until slow enough speed reported.");
            }
            this.mPositionMode = getSuplMode(Settings.Global.getInt(this.mContext.getContentResolver(), "assisted_gps_enabled", 1) != 0);
            if (DEBUG) {
                int i = this.mPositionMode;
                if (i == 0) {
                    mode = "standalone";
                } else if (i == 1) {
                    mode = "MS_BASED";
                } else if (i != 2) {
                    mode = UiModeManagerService.Shell.NIGHT_MODE_STR_UNKNOWN;
                } else {
                    mode = "MS_ASSISTED";
                }
                Log.d("GnssLocationProvider", "setting position_mode to " + mode);
            }
            int interval = hasCapability(1) ? this.mFixInterval : 1000;
            this.mLowPowerMode = this.mProviderRequest.lowPowerMode;
            if (!setPositionMode(this.mPositionMode, 0, interval, 0, 0, this.mLowPowerMode)) {
                setStarted(false);
                Log.e("GnssLocationProvider", "set_position_mode failed in startNavigating()");
            } else if (!native_start()) {
                setStarted(false);
                Log.e("GnssLocationProvider", "native_start failed in startNavigating()");
            } else {
                updateStatus(1);
                this.mLocationExtras.reset();
                this.mFixRequestTime = SystemClock.elapsedRealtime();
                if (!hasCapability(1) && this.mFixInterval >= NO_FIX_TIMEOUT) {
                    this.mAlarmManager.set(2, SystemClock.elapsedRealtime() + 60000, this.mTimeoutIntent);
                }
                addToBugreport("start gnss engine ");
                this.mRecordLoseCount = true;
                GnssCollectData.savePoint("START", (String) null);
            }
        }
    }

    private void stopNavigating() {
        if (DEBUG) {
            Log.d("GnssLocationProvider", "stopNavigating");
        }
        if (this.mStarted) {
            setStarted(false);
            native_stop();
            this.mLastFixTime = 0;
            this.mLastPositionMode = null;
            updateStatus(1);
            this.mLocationExtras.reset();
            addToBugreport("stop gnss engine ");
            GnssCollectData.savePoint("STOP", (String) null);
        }
    }

    private void setStarted(boolean started) {
        if (this.mStarted != started) {
            this.mStarted = started;
            this.mStartedChangedElapsedRealtime = SystemClock.elapsedRealtime();
        }
    }

    /* access modifiers changed from: private */
    public void hibernate() {
        stopNavigating();
        this.mAlarmManager.cancel(this.mTimeoutIntent);
        this.mAlarmManager.cancel(this.mWakeupIntent);
        this.mAlarmManager.set(2, ((long) this.mFixInterval) + SystemClock.elapsedRealtime(), this.mWakeupIntent);
    }

    private boolean hasCapability(int capability) {
        return (this.mTopHalCapabilities & capability) != 0;
    }

    private void reportLocation(boolean hasLatLong, Location location) {
        sendMessage(17, hasLatLong, location);
    }

    /* access modifiers changed from: private */
    public void handleReportLocation(boolean hasLatLong, Location location) {
        if (location.hasSpeed()) {
            this.mItarSpeedLimitExceeded = location.getSpeed() > ITAR_SPEED_LIMIT_METERS_PER_SECOND;
        }
        if (this.mItarSpeedLimitExceeded) {
            Log.i("GnssLocationProvider", "Hal reported a speed in excess of ITAR limit.  GPS/GNSS Navigation output blocked.");
            if (this.mStarted) {
                this.mGnssMetrics.logReceivedLocationStatus(false);
                return;
            }
            return;
        }
        if (VERBOSE) {
            Log.v("GnssLocationProvider", "reportLocation " + location.toString());
        }
        location.setExtras(this.mLocationExtras.getBundle());
        reportLocation(location);
        if (this.mStarted) {
            this.mGnssMetrics.logReceivedLocationStatus(hasLatLong);
            if (hasLatLong) {
                if (location.hasAccuracy()) {
                    this.mGnssMetrics.logPositionAccuracyMeters(location.getAccuracy());
                }
                if (this.mTimeToFirstFix > 0) {
                    this.mGnssMetrics.logMissedReports(this.mFixInterval, (int) (SystemClock.elapsedRealtime() - this.mLastFixTime));
                }
            }
            this.mRecordLoseCount = true;
        } else {
            long locationAfterStartedFalseMillis = SystemClock.elapsedRealtime() - this.mStartedChangedElapsedRealtime;
            if (locationAfterStartedFalseMillis > LOCATION_OFF_DELAY_THRESHOLD_WARN_MILLIS) {
                String logMessage = "Unexpected GNSS Location report " + TimeUtils.formatDuration(locationAfterStartedFalseMillis) + " after location turned off";
                if (locationAfterStartedFalseMillis > LOCATION_OFF_DELAY_THRESHOLD_ERROR_MILLIS) {
                    Log.e("GnssLocationProvider", logMessage);
                } else {
                    Log.w("GnssLocationProvider", logMessage);
                }
            }
        }
        this.mLastFixTime = SystemClock.elapsedRealtime();
        if (this.mTimeToFirstFix == 0 && hasLatLong && this.mStarted) {
            this.mTimeToFirstFix = (int) (this.mLastFixTime - this.mFixRequestTime);
            if (DEBUG) {
                Log.d("GnssLocationProvider", "TTFF: " + this.mTimeToFirstFix);
            }
            if (this.mStarted) {
                this.mGnssMetrics.logTimeToFirstFixMilliSecs(this.mTimeToFirstFix);
            }
            this.mGnssStatusListenerHelper.onFirstFix(this.mTimeToFirstFix);
            addToBugreport("TTFF is " + this.mTimeToFirstFix);
            addToBugreportEncrypt("the first location is " + location);
            GnssCollectData.savePoint("FIX", (String) null);
        }
        if (this.mStarted && this.mStatus != 2) {
            if (!hasCapability(1) && this.mFixInterval < NO_FIX_TIMEOUT) {
                this.mAlarmManager.cancel(this.mTimeoutIntent);
            }
            updateStatus(2);
        }
        if (!hasCapability(1) && this.mStarted && this.mFixInterval > 10000) {
            if (DEBUG) {
                Log.d("GnssLocationProvider", "got fix, hibernating");
            }
            hibernate();
        }
    }

    private void reportStatus(int status) {
        if (DEBUG) {
            Log.v("GnssLocationProvider", "reportStatus status: " + status);
        }
        boolean wasNavigating = this.mNavigating;
        if (status == 1) {
            this.mNavigating = true;
        } else if (status == 2) {
            this.mNavigating = false;
        } else if (status != 3 && status == 4) {
            this.mNavigating = false;
        }
        boolean z = this.mNavigating;
        if (wasNavigating != z) {
            this.mGnssStatusListenerHelper.onStatusChanged(z);
        }
    }

    private static class SvStatusInfo {
        /* access modifiers changed from: private */
        public float[] mCn0s;
        /* access modifiers changed from: private */
        public float[] mSvAzimuths;
        /* access modifiers changed from: private */
        public float[] mSvCarrierFreqs;
        /* access modifiers changed from: private */
        public int mSvCount;
        /* access modifiers changed from: private */
        public float[] mSvElevations;
        /* access modifiers changed from: private */
        public int[] mSvidWithFlags;

        private SvStatusInfo() {
        }
    }

    private void reportSvStatus(int svCount, int[] svidWithFlags, float[] cn0s, float[] svElevations, float[] svAzimuths, float[] svCarrierFreqs) {
        SvStatusInfo svStatusInfo = new SvStatusInfo();
        int unused = svStatusInfo.mSvCount = svCount;
        int[] unused2 = svStatusInfo.mSvidWithFlags = svidWithFlags;
        float[] unused3 = svStatusInfo.mCn0s = cn0s;
        float[] unused4 = svStatusInfo.mSvElevations = svElevations;
        float[] unused5 = svStatusInfo.mSvAzimuths = svAzimuths;
        float[] unused6 = svStatusInfo.mSvCarrierFreqs = svCarrierFreqs;
        sendMessage(18, 0, svStatusInfo);
    }

    /* access modifiers changed from: private */
    public void handleReportSvStatus(SvStatusInfo info) {
        String str;
        String str2;
        String str3;
        String str4;
        String str5;
        String str6;
        String str7;
        this.mGnssStatusListenerHelper.onSvStatusChanged(info.mSvCount, info.mSvidWithFlags, info.mCn0s, info.mSvElevations, info.mSvAzimuths, info.mSvCarrierFreqs);
        this.mGnssMetrics.logCn0(info.mCn0s, info.mSvCount);
        if (VERBOSE) {
            Log.v("GnssLocationProvider", "SV count: " + info.mSvCount);
        }
        int usedInFixCount = 0;
        int maxCn0 = 0;
        int meanCn0 = 0;
        int i = 0;
        while (true) {
            str = "U";
            str2 = " A";
            str3 = " E";
            str4 = "";
            if (i >= info.mSvCount) {
                break;
            }
            if ((info.mSvidWithFlags[i] & 4) != 0) {
                usedInFixCount++;
                if (info.mCn0s[i] > ((float) maxCn0)) {
                    maxCn0 = (int) info.mCn0s[i];
                }
                meanCn0 = (int) (((float) meanCn0) + info.mCn0s[i]);
            }
            if (VERBOSE) {
                StringBuilder sb = new StringBuilder();
                sb.append("svid: ");
                sb.append(info.mSvidWithFlags[i] >> 8);
                sb.append(" cn0: ");
                sb.append(info.mCn0s[i]);
                sb.append(" elev: ");
                sb.append(info.mSvElevations[i]);
                sb.append(" azimuth: ");
                sb.append(info.mSvAzimuths[i]);
                sb.append(" carrier frequency: ");
                sb.append(info.mSvCarrierFreqs[i]);
                if ((info.mSvidWithFlags[i] & 1) == 0) {
                    str3 = "  ";
                }
                sb.append(str3);
                if ((info.mSvidWithFlags[i] & 2) == 0) {
                    str2 = "  ";
                }
                sb.append(str2);
                if ((info.mSvidWithFlags[i] & 4) == 0) {
                    str = str4;
                }
                sb.append(str);
                if ((info.mSvidWithFlags[i] & 8) != 0) {
                    str4 = "F";
                }
                sb.append(str4);
                Log.v("GnssLocationProvider", sb.toString());
            }
            if ((info.mSvidWithFlags[i] & 4) != 0) {
                this.mGnssMetrics.logConstellationType((info.mSvidWithFlags[i] >> 4) & 15);
            }
            i++;
        }
        if (usedInFixCount > 0) {
            meanCn0 /= usedInFixCount;
        }
        this.mLocationExtras.set(usedInFixCount, meanCn0, maxCn0);
        if (this.mLastFixTime != 0) {
            int i2 = usedInFixCount;
            if (SystemClock.elapsedRealtime() - this.mLastFixTime > 3000 && this.mRecordLoseCount) {
                addToBugreportEncrypt("lose location, record the latest SV status ");
                for (int i3 = 0; i3 < info.mSvCount; i3++) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("svid: ");
                    sb2.append(info.mSvidWithFlags[i3] >> 8);
                    sb2.append(" cn0: ");
                    sb2.append(info.mCn0s[i3] / 10.0f);
                    sb2.append(" elev: ");
                    sb2.append(info.mSvElevations[i3]);
                    sb2.append(" azimuth: ");
                    sb2.append(info.mSvAzimuths[i3]);
                    if ((info.mSvidWithFlags[i3] & 1) == 0) {
                        str5 = "  ";
                    } else {
                        str5 = str3;
                    }
                    sb2.append(str5);
                    if ((info.mSvidWithFlags[i3] & 2) == 0) {
                        str6 = "  ";
                    } else {
                        str6 = str2;
                    }
                    sb2.append(str6);
                    if ((info.mSvidWithFlags[i3] & 4) == 0) {
                        str7 = str4;
                    } else {
                        str7 = str;
                    }
                    sb2.append(str7);
                    addToBugreport(sb2.toString());
                }
                this.mRecordLoseCount = false;
            }
        }
        if (this.mNavigating != 0 && this.mStatus == 2 && this.mLastFixTime > 0 && SystemClock.elapsedRealtime() - this.mLastFixTime > JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY) {
            updateStatus(1);
            GnssCollectData.savePoint("LOSE", (String) null);
        }
    }

    private void reportAGpsStatus(int agpsType, int agpsStatus, byte[] suplIpAddr) {
        this.mNetworkConnectivityHandler.onReportAGpsStatus(agpsType, agpsStatus, suplIpAddr);
    }

    private void reportNmea(long timestamp) {
        if (!this.mItarSpeedLimitExceeded) {
            byte[] bArr = this.mNmeaBuffer;
            String nmea = new String(this.mNmeaBuffer, 0, native_read_nmea(bArr, bArr.length));
            if (DEBUG) {
                Log.d("GnssLocationProvider", "nmea = " + nmea);
            }
            if (!nmea.startsWith("$P")) {
                addNmeaToBugreport(nmea);
                this.mGnssStatusListenerHelper.onNmeaReceived(timestamp, nmea);
            }
        }
    }

    private void reportMeasurementData(GnssMeasurementsEvent event) {
        if (!this.mItarSpeedLimitExceeded) {
            this.mHandler.post(new Runnable(event) {
                private final /* synthetic */ GnssMeasurementsEvent f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    GnssLocationProvider.this.lambda$reportMeasurementData$3$GnssLocationProvider(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$reportMeasurementData$3$GnssLocationProvider(GnssMeasurementsEvent event) {
        this.mGnssMeasurementsProvider.onMeasurementsAvailable(event);
    }

    private void reportNavigationMessage(GnssNavigationMessage event) {
        if (!this.mItarSpeedLimitExceeded) {
            this.mHandler.post(new Runnable(event) {
                private final /* synthetic */ GnssNavigationMessage f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    GnssLocationProvider.this.lambda$reportNavigationMessage$4$GnssLocationProvider(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$reportNavigationMessage$4$GnssLocationProvider(GnssNavigationMessage event) {
        this.mGnssNavigationMessageProvider.onNavigationMessageAvailable(event);
    }

    private void setTopHalCapabilities(int topHalCapabilities) {
        this.mHandler.post(new Runnable(topHalCapabilities) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                GnssLocationProvider.this.lambda$setTopHalCapabilities$5$GnssLocationProvider(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$setTopHalCapabilities$5$GnssLocationProvider(int topHalCapabilities) {
        this.mTopHalCapabilities = topHalCapabilities;
        if (hasCapability(16)) {
            this.mNtpTimeHelper.enablePeriodicTimeInjection();
            requestUtcTime();
        }
        this.mGnssMeasurementsProvider.onCapabilitiesUpdated(hasCapability(64));
        this.mGnssNavigationMessageProvider.onCapabilitiesUpdated(hasCapability(128));
        restartRequests();
        this.mGnssCapabilitiesProvider.setTopHalCapabilities(this.mTopHalCapabilities);
    }

    private void setSubHalMeasurementCorrectionsCapabilities(int subHalCapabilities) {
        this.mHandler.post(new Runnable(subHalCapabilities) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                GnssLocationProvider.this.lambda$setSubHalMeasurementCorrectionsCapabilities$6$GnssLocationProvider(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$setSubHalMeasurementCorrectionsCapabilities$6$GnssLocationProvider(int subHalCapabilities) {
        if (this.mGnssMeasurementCorrectionsProvider.onCapabilitiesUpdated(subHalCapabilities)) {
            this.mGnssCapabilitiesProvider.setSubHalMeasurementCorrectionsCapabilities(subHalCapabilities);
        }
    }

    private void restartRequests() {
        Log.i("GnssLocationProvider", "restartRequests");
        restartLocationRequest();
        this.mGnssMeasurementsProvider.resumeIfStarted();
        this.mGnssNavigationMessageProvider.resumeIfStarted();
        this.mGnssBatchingProvider.resumeIfStarted();
        this.mGnssGeofenceProvider.resumeIfStarted();
    }

    private void restartLocationRequest() {
        if (DEBUG) {
            Log.d("GnssLocationProvider", "restartLocationRequest");
        }
        setStarted(false);
        updateRequirements();
    }

    private void setGnssYearOfHardware(int yearOfHardware) {
        if (DEBUG) {
            Log.d("GnssLocationProvider", "setGnssYearOfHardware called with " + yearOfHardware);
        }
        this.mHardwareYear = yearOfHardware;
    }

    private void setGnssHardwareModelName(String modelName) {
        if (DEBUG) {
            Log.d("GnssLocationProvider", "setGnssModelName called with " + modelName);
        }
        this.mHardwareModelName = modelName;
    }

    private void reportGnssServiceDied() {
        if (DEBUG) {
            Log.d("GnssLocationProvider", "reportGnssServiceDied");
        }
        this.mHandler.post(new Runnable() {
            public final void run() {
                GnssLocationProvider.this.lambda$reportGnssServiceDied$7$GnssLocationProvider();
            }
        });
    }

    public /* synthetic */ void lambda$reportGnssServiceDied$7$GnssLocationProvider() {
        setupNativeGnssService(true);
        if (isGpsEnabled()) {
            setGpsEnabled(false);
            updateEnabled();
            reloadGpsProperties();
        }
    }

    public GnssSystemInfoProvider getGnssSystemInfoProvider() {
        return new GnssSystemInfoProvider() {
            public int getGnssYearOfHardware() {
                return GnssLocationProvider.this.mHardwareYear;
            }

            public String getGnssHardwareModelName() {
                return GnssLocationProvider.this.mHardwareModelName;
            }
        };
    }

    public GnssBatchingProvider getGnssBatchingProvider() {
        return this.mGnssBatchingProvider;
    }

    public GnssMetricsProvider getGnssMetricsProvider() {
        return new GnssMetricsProvider() {
            public final String getGnssMetricsAsProtoString() {
                return GnssLocationProvider.this.lambda$getGnssMetricsProvider$8$GnssLocationProvider();
            }
        };
    }

    public /* synthetic */ String lambda$getGnssMetricsProvider$8$GnssLocationProvider() {
        return this.mGnssMetrics.dumpGnssMetricsAsProtoString();
    }

    public GnssCapabilitiesProvider getGnssCapabilitiesProvider() {
        return this.mGnssCapabilitiesProvider;
    }

    private void reportLocationBatch(Location[] locationArray) {
        List<Location> locations = new ArrayList<>(Arrays.asList(locationArray));
        if (DEBUG) {
            Log.d("GnssLocationProvider", "Location batch of size " + locationArray.length + " reported");
        }
        reportLocation(locations);
    }

    private void psdsDownloadRequest() {
        if (DEBUG) {
            Log.d("GnssLocationProvider", "psdsDownloadRequest");
        }
        sendMessage(6, 0, (Object) null);
    }

    private static int getGeofenceStatus(int status) {
        if (status == GPS_GEOFENCE_ERROR_GENERIC) {
            return 5;
        }
        if (status == 0) {
            return 0;
        }
        if (status == 100) {
            return 1;
        }
        switch (status) {
            case GPS_GEOFENCE_ERROR_INVALID_TRANSITION /*-103*/:
                return 4;
            case GPS_GEOFENCE_ERROR_ID_UNKNOWN /*-102*/:
                return 3;
            case GPS_GEOFENCE_ERROR_ID_EXISTS /*-101*/:
                return 2;
            default:
                return -1;
        }
    }

    private void reportGeofenceTransition(int geofenceId, Location location, int transition, long transitionTimestamp) {
        this.mHandler.post(new Runnable(geofenceId, location, transition, transitionTimestamp) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ Location f$2;
            private final /* synthetic */ int f$3;
            private final /* synthetic */ long f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                GnssLocationProvider.this.lambda$reportGeofenceTransition$9$GnssLocationProvider(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    public /* synthetic */ void lambda$reportGeofenceTransition$9$GnssLocationProvider(int geofenceId, Location location, int transition, long transitionTimestamp) {
        if (this.mGeofenceHardwareImpl == null) {
            this.mGeofenceHardwareImpl = GeofenceHardwareImpl.getInstance(this.mContext);
        }
        this.mGeofenceHardwareImpl.reportGeofenceTransition(geofenceId, location, transition, transitionTimestamp, 0, FusedBatchOptions.SourceTechnologies.GNSS);
    }

    private void reportGeofenceStatus(int status, Location location) {
        this.mHandler.post(new Runnable(status, location) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ Location f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                GnssLocationProvider.this.lambda$reportGeofenceStatus$10$GnssLocationProvider(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$reportGeofenceStatus$10$GnssLocationProvider(int status, Location location) {
        if (this.mGeofenceHardwareImpl == null) {
            this.mGeofenceHardwareImpl = GeofenceHardwareImpl.getInstance(this.mContext);
        }
        int monitorStatus = 1;
        if (status == 2) {
            monitorStatus = 0;
        }
        this.mGeofenceHardwareImpl.reportGeofenceMonitorStatus(0, monitorStatus, location, FusedBatchOptions.SourceTechnologies.GNSS);
    }

    private void reportGeofenceAddStatus(int geofenceId, int status) {
        this.mHandler.post(new Runnable(geofenceId, status) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                GnssLocationProvider.this.lambda$reportGeofenceAddStatus$11$GnssLocationProvider(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$reportGeofenceAddStatus$11$GnssLocationProvider(int geofenceId, int status) {
        if (this.mGeofenceHardwareImpl == null) {
            this.mGeofenceHardwareImpl = GeofenceHardwareImpl.getInstance(this.mContext);
        }
        this.mGeofenceHardwareImpl.reportGeofenceAddStatus(geofenceId, getGeofenceStatus(status));
    }

    private void reportGeofenceRemoveStatus(int geofenceId, int status) {
        this.mHandler.post(new Runnable(geofenceId, status) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                GnssLocationProvider.this.lambda$reportGeofenceRemoveStatus$12$GnssLocationProvider(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$reportGeofenceRemoveStatus$12$GnssLocationProvider(int geofenceId, int status) {
        if (this.mGeofenceHardwareImpl == null) {
            this.mGeofenceHardwareImpl = GeofenceHardwareImpl.getInstance(this.mContext);
        }
        this.mGeofenceHardwareImpl.reportGeofenceRemoveStatus(geofenceId, getGeofenceStatus(status));
    }

    private void reportGeofencePauseStatus(int geofenceId, int status) {
        this.mHandler.post(new Runnable(geofenceId, status) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                GnssLocationProvider.this.lambda$reportGeofencePauseStatus$13$GnssLocationProvider(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$reportGeofencePauseStatus$13$GnssLocationProvider(int geofenceId, int status) {
        if (this.mGeofenceHardwareImpl == null) {
            this.mGeofenceHardwareImpl = GeofenceHardwareImpl.getInstance(this.mContext);
        }
        this.mGeofenceHardwareImpl.reportGeofencePauseStatus(geofenceId, getGeofenceStatus(status));
    }

    private void reportGeofenceResumeStatus(int geofenceId, int status) {
        this.mHandler.post(new Runnable(geofenceId, status) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                GnssLocationProvider.this.lambda$reportGeofenceResumeStatus$14$GnssLocationProvider(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$reportGeofenceResumeStatus$14$GnssLocationProvider(int geofenceId, int status) {
        if (this.mGeofenceHardwareImpl == null) {
            this.mGeofenceHardwareImpl = GeofenceHardwareImpl.getInstance(this.mContext);
        }
        this.mGeofenceHardwareImpl.reportGeofenceResumeStatus(geofenceId, getGeofenceStatus(status));
    }

    public INetInitiatedListener getNetInitiatedListener() {
        return this.mNetInitiatedListener;
    }

    public void reportNiNotification(int notificationId, int niType, int notifyFlags, int timeout, int defaultResponse, String requestorId, String text, int requestorIdEncoding, int textEncoding) {
        int i = notificationId;
        int i2 = niType;
        int i3 = notifyFlags;
        int i4 = timeout;
        int i5 = defaultResponse;
        String str = requestorId;
        String str2 = text;
        int i6 = requestorIdEncoding;
        int i7 = textEncoding;
        Log.i("GnssLocationProvider", "reportNiNotification: entered");
        Log.i("GnssLocationProvider", "notificationId: " + i + ", niType: " + i2 + ", notifyFlags: " + i3 + ", timeout: " + i4 + ", defaultResponse: " + i5);
        StringBuilder sb = new StringBuilder();
        sb.append("requestorId: ");
        sb.append(str);
        sb.append(", text: ");
        sb.append(str2);
        sb.append(", requestorIdEncoding: ");
        sb.append(i6);
        sb.append(", textEncoding: ");
        sb.append(i7);
        Log.i("GnssLocationProvider", sb.toString());
        GpsNetInitiatedHandler.GpsNiNotification notification = new GpsNetInitiatedHandler.GpsNiNotification();
        notification.notificationId = i;
        notification.niType = i2;
        boolean z = false;
        notification.needNotify = (i3 & 1) != 0;
        notification.needVerify = (i3 & 2) != 0;
        if ((i3 & 4) != 0) {
            z = true;
        }
        notification.privacyOverride = z;
        notification.timeout = i4;
        notification.defaultResponse = i5;
        notification.requestorId = str;
        notification.text = str2;
        notification.requestorIdEncoding = i6;
        notification.textEncoding = i7;
        this.mNIHandler.handleNiNotification(notification);
        StatsLog.write(124, 1, notification.notificationId, notification.niType, notification.needNotify, notification.needVerify, notification.privacyOverride, notification.timeout, notification.defaultResponse, notification.requestorId, notification.text, notification.requestorIdEncoding, notification.textEncoding, this.mSuplEsEnabled, isGpsEnabled(), 0);
    }

    private void requestSetID(int flags) {
        TelephonyManager phone = (TelephonyManager) this.mContext.getSystemService("phone");
        int type = 0;
        String setId = null;
        addToBugreport("requestSetID = " + flags);
        int ddSubId = SubscriptionManager.getDefaultDataSubscriptionId();
        if ((flags & 1) == 1) {
            if (SubscriptionManager.isValidSubscriptionId(ddSubId)) {
                setId = phone.getSubscriberId(ddSubId);
            }
            if (setId == null) {
                setId = phone.getSubscriberId();
            }
            if (setId != null) {
                type = 1;
            }
        } else if ((flags & 2) == 2) {
            if (SubscriptionManager.isValidSubscriptionId(ddSubId)) {
                setId = phone.getLine1Number(ddSubId);
            }
            if (setId == null) {
                setId = phone.getLine1Number();
            }
            if (setId != null) {
                type = 2;
            }
        }
        native_agps_set_id(type, setId == null ? "" : setId);
    }

    private void requestLocation(boolean independentFromGnss, boolean isUserEmergency) {
        if (DEBUG) {
            Log.d("GnssLocationProvider", "requestLocation. independentFromGnss: " + independentFromGnss + ", isUserEmergency: " + isUserEmergency);
        }
        sendMessage(16, independentFromGnss, Boolean.valueOf(isUserEmergency));
    }

    private void requestUtcTime() {
        if (DEBUG) {
            Log.d("GnssLocationProvider", "utcTimeRequest");
        }
        sendMessage(5, 0, (Object) null);
    }

    private void requestRefLocation() {
        int type;
        TelephonyManager phone = (TelephonyManager) this.mContext.getSystemService("phone");
        int phoneType = phone.getPhoneType();
        addToBugreport("requestRefLocation ");
        if (phoneType == 1) {
            GsmCellLocation gsm_cell = (GsmCellLocation) phone.getCellLocation();
            if (gsm_cell == null || phone.getNetworkOperator() == null || phone.getNetworkOperator().length() <= 3) {
                Log.e("GnssLocationProvider", "Error getting cell location info.");
                return;
            }
            int mcc = Integer.parseInt(phone.getNetworkOperator().substring(0, 3));
            int mnc = Integer.parseInt(phone.getNetworkOperator().substring(3));
            int networkType = phone.getNetworkType();
            if (networkType == 3 || networkType == 8 || networkType == 9 || networkType == 10 || networkType == 15) {
                type = 2;
            } else {
                type = 1;
            }
            native_agps_set_ref_location_cellid(type, mcc, mnc, gsm_cell.getLac(), gsm_cell.getCid());
        } else if (phoneType == 2) {
            Log.e("GnssLocationProvider", "CDMA not supported.");
        }
    }

    private void reportNfwNotification(String proxyAppPackageName, byte protocolStack, String otherProtocolStackName, byte requestor, String requestorId, byte responseType, boolean inEmergencyMode, boolean isCachedLocation) {
        GnssVisibilityControl gnssVisibilityControl = this.mGnssVisibilityControl;
        if (gnssVisibilityControl == null) {
            Log.e("GnssLocationProvider", "reportNfwNotification: mGnssVisibilityControl is not initialized.");
        } else {
            gnssVisibilityControl.reportNfwNotification(proxyAppPackageName, protocolStack, otherProtocolStackName, requestor, requestorId, responseType, inEmergencyMode, isCachedLocation);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isInEmergencySession() {
        return this.mNIHandler.getInEmergency();
    }

    private void sendMessage(int message, int arg, Object obj) {
        this.mWakeLock.acquire();
        if (DEBUG) {
            Log.d("GnssLocationProvider", "WakeLock acquired by sendMessage(" + messageIdAsString(message) + ", " + arg + ", " + obj + ")");
        }
        this.mHandler.obtainMessage(message, arg, 1, obj).sendToTarget();
    }

    private final class ProviderHandler extends Handler {
        public ProviderHandler(Looper looper) {
            super(looper, (Handler.Callback) null, true);
        }

        public void handleMessage(Message msg) {
            int message = msg.what;
            if (message == 3) {
                GpsRequest gpsRequest = (GpsRequest) msg.obj;
                GnssLocationProvider.this.handleSetRequest(gpsRequest.request, gpsRequest.source);
            } else if (message == 11) {
                int unused = GnssLocationProvider.this.mDownloadPsdsDataPending = 2;
            } else if (message == 13) {
                handleInitialize();
            } else if (message == 5) {
                GnssLocationProvider.this.mNtpTimeHelper.retrieveAndInjectNtpTime();
            } else if (message == 6) {
                GnssLocationProvider.this.handleDownloadPsdsData();
            } else if (message != 7) {
                boolean z = false;
                switch (message) {
                    case 16:
                        GnssLocationProvider gnssLocationProvider = GnssLocationProvider.this;
                        if (msg.arg1 == 1) {
                            z = true;
                        }
                        gnssLocationProvider.handleRequestLocation(z, ((Boolean) msg.obj).booleanValue());
                        break;
                    case 17:
                        GnssLocationProvider gnssLocationProvider2 = GnssLocationProvider.this;
                        if (msg.arg1 == 1) {
                            z = true;
                        }
                        gnssLocationProvider2.handleReportLocation(z, (Location) msg.obj);
                        break;
                    case 18:
                        GnssLocationProvider.this.handleReportSvStatus((SvStatusInfo) msg.obj);
                        break;
                }
            } else {
                GnssLocationProvider.this.handleUpdateLocation((Location) msg.obj);
            }
            if (msg.arg2 == 1) {
                GnssLocationProvider.this.mWakeLock.release();
                if (GnssLocationProvider.DEBUG) {
                    Log.d("GnssLocationProvider", "WakeLock released by handleMessage(" + GnssLocationProvider.this.messageIdAsString(message) + ", " + msg.arg1 + ", " + msg.obj + ")");
                }
            }
        }

        private void handleInitialize() {
            GnssLocationProvider.this.setupNativeGnssService(false);
            if (GnssLocationProvider.native_is_gnss_visibility_control_supported()) {
                GnssLocationProvider gnssLocationProvider = GnssLocationProvider.this;
                GnssVisibilityControl unused = gnssLocationProvider.mGnssVisibilityControl = new GnssVisibilityControl(gnssLocationProvider.mContext, GnssLocationProvider.this.mLooper, GnssLocationProvider.this.mNIHandler);
            }
            GnssLocationProvider.this.reloadGpsProperties();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(GnssLocationProvider.ALARM_WAKEUP);
            intentFilter.addAction(GnssLocationProvider.ALARM_TIMEOUT);
            intentFilter.addAction("android.os.action.POWER_SAVE_MODE_CHANGED");
            intentFilter.addAction("android.os.action.DEVICE_IDLE_MODE_CHANGED");
            intentFilter.addAction("android.intent.action.SCREEN_OFF");
            intentFilter.addAction("android.intent.action.SCREEN_ON");
            intentFilter.addAction("android.telephony.action.CARRIER_CONFIG_CHANGED");
            intentFilter.addAction("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED");
            GnssLocationProvider.this.mContext.registerReceiver(GnssLocationProvider.this.mBroadcastReceiver, intentFilter, (String) null, this);
            GnssLocationProvider.this.mNetworkConnectivityHandler.registerNetworkCallbacks();
            LocationRequest request = LocationRequest.createFromDeprecatedProvider("passive", 0, 0.0f, false);
            request.setHideFromAppOps(true);
            ((LocationManager) GnssLocationProvider.this.mContext.getSystemService("location")).requestLocationUpdates(request, new NetworkLocationListener(), getLooper());
            GnssLocationProvider.this.updateEnabled();
            GnssCollectData.savePoint("INIT", (String) null);
        }
    }

    private abstract class LocationChangeListener implements LocationListener {
        private int mNumLocationUpdateRequest;

        private LocationChangeListener() {
        }

        static /* synthetic */ int access$1006(LocationChangeListener x0) {
            int i = x0.mNumLocationUpdateRequest - 1;
            x0.mNumLocationUpdateRequest = i;
            return i;
        }

        static /* synthetic */ int access$1008(LocationChangeListener x0) {
            int i = x0.mNumLocationUpdateRequest;
            x0.mNumLocationUpdateRequest = i + 1;
            return i;
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    }

    private final class NetworkLocationListener extends LocationChangeListener {
        private NetworkLocationListener() {
            super();
        }

        public void onLocationChanged(Location location) {
            if ("network".equals(location.getProvider())) {
                GnssLocationProvider.this.handleUpdateLocation(location);
            }
        }
    }

    private final class FusedLocationListener extends LocationChangeListener {
        private FusedLocationListener() {
            super();
        }

        public void onLocationChanged(Location location) {
            if ("fused".equals(location.getProvider())) {
                GnssLocationProvider.this.injectBestLocation(location);
            }
        }
    }

    /* access modifiers changed from: private */
    public String messageIdAsString(int message) {
        if (message == 3) {
            return "SET_REQUEST";
        }
        if (message == 11) {
            return "DOWNLOAD_PSDS_DATA_FINISHED";
        }
        if (message == 13) {
            return "INITIALIZE_HANDLER";
        }
        if (message == 5) {
            return "INJECT_NTP_TIME";
        }
        if (message == 6) {
            return "DOWNLOAD_PSDS_DATA";
        }
        if (message == 7) {
            return "UPDATE_LOCATION";
        }
        switch (message) {
            case 16:
                return "REQUEST_LOCATION";
            case 17:
                return "REPORT_LOCATION";
            case 18:
                return "REPORT_SV_STATUS";
            default:
                return "<Unknown>";
        }
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        StringBuilder s = new StringBuilder();
        s.append("  mStarted=");
        s.append(this.mStarted);
        s.append("   (changed ");
        TimeUtils.formatDuration(SystemClock.elapsedRealtime() - this.mStartedChangedElapsedRealtime, s);
        s.append(" ago)");
        s.append(10);
        s.append("  mFixInterval=");
        s.append(this.mFixInterval);
        s.append(10);
        s.append("  mLowPowerMode=");
        s.append(this.mLowPowerMode);
        s.append(10);
        s.append("  mGnssMeasurementsProvider.isRegistered()=");
        s.append(this.mGnssMeasurementsProvider.isRegistered());
        s.append(10);
        s.append("  mGnssNavigationMessageProvider.isRegistered()=");
        s.append(this.mGnssNavigationMessageProvider.isRegistered());
        s.append(10);
        s.append("  mDisableGpsForPowerManager=");
        s.append(this.mDisableGpsForPowerManager);
        s.append(10);
        s.append("  mTopHalCapabilities=0x");
        s.append(Integer.toHexString(this.mTopHalCapabilities));
        s.append(" ( ");
        if (hasCapability(1)) {
            s.append("SCHEDULING ");
        }
        if (hasCapability(2)) {
            s.append("MSB ");
        }
        if (hasCapability(4)) {
            s.append("MSA ");
        }
        if (hasCapability(8)) {
            s.append("SINGLE_SHOT ");
        }
        if (hasCapability(16)) {
            s.append("ON_DEMAND_TIME ");
        }
        if (hasCapability(32)) {
            s.append("GEOFENCING ");
        }
        if (hasCapability(64)) {
            s.append("MEASUREMENTS ");
        }
        if (hasCapability(128)) {
            s.append("NAV_MESSAGES ");
        }
        if (hasCapability(256)) {
            s.append("LOW_POWER_MODE ");
        }
        if (hasCapability(512)) {
            s.append("SATELLITE_BLACKLIST ");
        }
        if (hasCapability(1024)) {
            s.append("MEASUREMENT_CORRECTIONS ");
        }
        s.append(")\n");
        if (hasCapability(1024)) {
            s.append("  SubHal=MEASUREMENT_CORRECTIONS[");
            s.append(this.mGnssMeasurementCorrectionsProvider.toStringCapabilities());
            s.append("]\n");
        }
        s.append(this.mGnssMetrics.dumpGnssMetricsAsText());
        s.append("  native internal state: ");
        s.append(native_get_internal_state());
        s.append("\n");
        this.mdumpStatus.dump(pw);
        this.mdumpNmea.dump(pw);
        pw.append(s);
    }

    private void addToBugreport(String log) {
        GnssLocalLog gnssLocalLog = this.mdumpStatus;
        gnssLocalLog.log("=MI GLP= " + log);
    }

    private void addToBugreportEncrypt(String log) {
        GnssLocalLog gnssLocalLog = this.mdumpStatus;
        gnssLocalLog.log("=MI GLP EN=" + log);
    }

    private void addNmeaToBugreport(String nmea) {
        GnssLocalLog gnssLocalLog = this.mdumpNmea;
        gnssLocalLog.log("=MI NMEA=" + nmea);
    }

    /* access modifiers changed from: private */
    public void setupNativeGnssService(boolean reinitializeGnssServiceHandle) {
        native_init_once(reinitializeGnssServiceHandle);
        if (!native_init()) {
            Log.w("GnssLocationProvider", "Native initialization failed.");
        } else {
            native_cleanup();
        }
    }
}
