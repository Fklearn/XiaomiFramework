package com.android.server.devicepolicy;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.ActivityTaskManager;
import android.app.ActivityThread;
import android.app.AlarmManager;
import android.app.AppGlobals;
import android.app.BroadcastOptions;
import android.app.IActivityManager;
import android.app.IActivityTaskManager;
import android.app.IApplicationThread;
import android.app.IServiceConnection;
import android.app.IStopUserCallback;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.admin.DeviceAdminInfo;
import android.app.admin.DevicePolicyCache;
import android.app.admin.DevicePolicyEventLogger;
import android.app.admin.DevicePolicyManagerInternal;
import android.app.admin.PasswordMetrics;
import android.app.admin.SecurityLog;
import android.app.admin.SystemUpdateInfo;
import android.app.admin.SystemUpdatePolicy;
import android.app.backup.IBackupManager;
import android.app.trust.TrustManager;
import android.app.usage.UsageStatsManagerInternal;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.PermissionChecker;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManagerInternal;
import android.content.pm.ParceledListSlice;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.StringParceledListSlice;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.IAudioService;
import android.net.ConnectivityManager;
import android.net.IIpConnectivityMetrics;
import android.net.NetworkUtils;
import android.net.ProxyInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.os.PowerManagerInternal;
import android.os.Process;
import android.os.RecoverySystem;
import android.os.RemoteCallback;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.ServiceSpecificException;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.UserManagerInternal;
import android.os.storage.StorageManager;
import android.permission.PermissionControllerManager;
import android.provider.ContactsContract;
import android.provider.ContactsInternal;
import android.provider.Settings;
import android.provider.Telephony;
import android.security.IKeyChainAliasCallback;
import android.security.IKeyChainService;
import android.security.KeyChain;
import android.service.persistentdata.PersistentDataBlockManager;
import android.telephony.TelephonyManager;
import android.telephony.data.ApnSetting;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.AtomicFile;
import android.util.Log;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.view.IWindowManager;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.IAccessibilityManager;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodSystemProperty;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.notification.SystemNotificationChannels;
import com.android.internal.os.BackgroundThread;
import com.android.internal.policy.IKeyguardDismissCallback;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.telephony.SmsApplication;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.util.FunctionalUtils;
import com.android.internal.util.JournaledFile;
import com.android.internal.util.Preconditions;
import com.android.internal.util.StatLogger;
import com.android.internal.util.XmlUtils;
import com.android.internal.widget.LockPatternUtils;
import com.android.server.BatteryService;
import com.android.server.LocalServices;
import com.android.server.LockGuard;
import com.android.server.SystemServerInitThreadPool;
import com.android.server.SystemService;
import com.android.server.UiModeManagerService;
import com.android.server.devicepolicy.TransferOwnershipMetadataManager;
import com.android.server.inputmethod.InputMethodManagerInternal;
import com.android.server.job.controllers.JobStatus;
import com.android.server.net.NetworkPolicyManagerInternal;
import com.android.server.pm.DumpState;
import com.android.server.pm.UserRestrictionsUtils;
import com.android.server.uri.UriGrantsManagerInternal;
import com.android.server.wm.ActivityTaskManagerInternal;
import com.android.server.wm.ActivityTaskManagerService;
import com.google.android.collect.Sets;
import com.miui.server.AccessController;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class DevicePolicyManagerService extends BaseIDevicePolicyManager {
    private static final String AB_DEVICE_KEY = "ro.build.ab_update";
    private static final String ACTION_EXPIRED_PASSWORD_NOTIFICATION = "com.android.server.ACTION_EXPIRED_PASSWORD_NOTIFICATION";
    private static final String ATTR_ALIAS = "alias";
    private static final String ATTR_APPLICATION_RESTRICTIONS_MANAGER = "application-restrictions-manager";
    private static final String ATTR_DELEGATED_CERT_INSTALLER = "delegated-cert-installer";
    private static final String ATTR_DEVICE_PAIRED = "device-paired";
    private static final String ATTR_DEVICE_PROVISIONING_CONFIG_APPLIED = "device-provisioning-config-applied";
    private static final String ATTR_DISABLED = "disabled";
    private static final String ATTR_ID = "id";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_PERMISSION_POLICY = "permission-policy";
    private static final String ATTR_PERMISSION_PROVIDER = "permission-provider";
    private static final String ATTR_PROVISIONING_STATE = "provisioning-state";
    private static final String ATTR_SETUP_COMPLETE = "setup-complete";
    private static final String ATTR_VALUE = "value";
    private static final Set<Integer> DA_DISALLOWED_POLICIES = new ArraySet();
    private static final String[] DELEGATIONS = {"delegation-cert-install", "delegation-app-restrictions", "delegation-block-uninstall", "delegation-enable-system-app", "delegation-keep-uninstalled-packages", "delegation-package-access", "delegation-permission-grant", "delegation-install-existing-package", "delegation-keep-uninstalled-packages", "delegation-network-logging", "delegation-cert-selection"};
    private static final int DEVICE_ADMIN_DEACTIVATE_TIMEOUT = 10000;
    private static final List<String> DEVICE_OWNER_DELEGATIONS = Arrays.asList(new String[]{"delegation-network-logging"});
    private static final String DEVICE_POLICIES_XML = "device_policies.xml";
    private static final String DO_NOT_ASK_CREDENTIALS_ON_BOOT_XML = "do-not-ask-credentials-on-boot";
    private static final boolean ENABLE_LOCK_GUARD = true;
    private static final List<String> EXCLUSIVE_DELEGATIONS = Arrays.asList(new String[]{"delegation-network-logging", "delegation-cert-selection"});
    private static final long EXPIRATION_GRACE_PERIOD_MS = (MS_PER_DAY * 5);
    private static final Set<String> GLOBAL_SETTINGS_DEPRECATED = new ArraySet();
    private static final Set<String> GLOBAL_SETTINGS_WHITELIST = new ArraySet();
    protected static final String LOG_TAG = "DevicePolicyManager";
    private static final String LOG_TAG_DEVICE_OWNER = "device-owner";
    private static final String LOG_TAG_PROFILE_OWNER = "profile-owner";
    private static final long MINIMUM_STRONG_AUTH_TIMEOUT_MS = TimeUnit.HOURS.toMillis(1);
    private static final long MS_PER_DAY = TimeUnit.DAYS.toMillis(1);
    private static final int PROFILE_KEYGUARD_FEATURES = 440;
    private static final int PROFILE_KEYGUARD_FEATURES_PROFILE_ONLY = 8;
    private static final String PROPERTY_DEVICE_OWNER_PRESENT = "ro.device_owner";
    private static final int REQUEST_EXPIRE_PASSWORD = 5571;
    private static final Set<String> SECURE_SETTINGS_DEVICEOWNER_WHITELIST = new ArraySet();
    private static final Set<String> SECURE_SETTINGS_WHITELIST = new ArraySet();
    private static final int STATUS_BAR_DISABLE2_MASK = 1;
    private static final int STATUS_BAR_DISABLE_MASK = 34013184;
    private static final Set<String> SYSTEM_SETTINGS_WHITELIST = new ArraySet();
    private static final String TAG_ACCEPTED_CA_CERTIFICATES = "accepted-ca-certificate";
    private static final String TAG_ADMIN_BROADCAST_PENDING = "admin-broadcast-pending";
    private static final String TAG_AFFILIATION_ID = "affiliation-id";
    private static final String TAG_CURRENT_INPUT_METHOD_SET = "current-ime-set";
    private static final String TAG_INITIALIZATION_BUNDLE = "initialization-bundle";
    private static final String TAG_LAST_BUG_REPORT_REQUEST = "last-bug-report-request";
    private static final String TAG_LAST_NETWORK_LOG_RETRIEVAL = "last-network-log-retrieval";
    private static final String TAG_LAST_SECURITY_LOG_RETRIEVAL = "last-security-log-retrieval";
    private static final String TAG_LOCK_TASK_COMPONENTS = "lock-task-component";
    private static final String TAG_LOCK_TASK_FEATURES = "lock-task-features";
    private static final String TAG_OWNER_INSTALLED_CA_CERT = "owner-installed-ca-cert";
    private static final String TAG_PASSWORD_TOKEN_HANDLE = "password-token";
    private static final String TAG_PASSWORD_VALIDITY = "password-validity";
    private static final String TAG_STATUS_BAR = "statusbar";
    private static final String TAG_TRANSFER_OWNERSHIP_BUNDLE = "transfer-ownership-bundle";
    private static final String TRANSFER_OWNERSHIP_PARAMETERS_XML = "transfer-ownership-parameters.xml";
    private static final int UNATTENDED_MANAGED_KIOSK_MS = 30000;
    private static final boolean VERBOSE_LOG = false;
    final Handler mBackgroundHandler;
    private final CertificateMonitor mCertificateMonitor;
    /* access modifiers changed from: private */
    public DevicePolicyConstants mConstants;
    private final DevicePolicyConstantsObserver mConstantsObserver;
    final Context mContext;
    private final DeviceAdminServiceController mDeviceAdminServiceController;
    final Handler mHandler;
    final boolean mHasFeature;
    final boolean mHasTelephonyFeature;
    final IPackageManager mIPackageManager;
    final Injector mInjector;
    final boolean mIsWatch;
    final LocalService mLocalService;
    private final Object mLockDoNoUseDirectly;
    private final LockPatternUtils mLockPatternUtils;
    @GuardedBy({"getLockObject()"})
    private NetworkLogger mNetworkLogger;
    private final OverlayPackagesProvider mOverlayPackagesProvider;
    @VisibleForTesting
    final Owners mOwners;
    private final Set<Pair<String, Integer>> mPackagesToRemove;
    /* access modifiers changed from: private */
    public final DevicePolicyCacheImpl mPolicyCache;
    final BroadcastReceiver mReceiver;
    /* access modifiers changed from: private */
    public final BroadcastReceiver mRemoteBugreportConsentReceiver;
    private final BroadcastReceiver mRemoteBugreportFinishedReceiver;
    /* access modifiers changed from: private */
    public final AtomicBoolean mRemoteBugreportServiceIsActive;
    private final AtomicBoolean mRemoteBugreportSharingAccepted;
    private final Runnable mRemoteBugreportTimeoutRunnable;
    private final SecurityLogMonitor mSecurityLogMonitor;
    private final SetupContentObserver mSetupContentObserver;
    private final StatLogger mStatLogger;
    final TelephonyManager mTelephonyManager;
    private final Binder mToken;
    @VisibleForTesting
    final TransferOwnershipMetadataManager mTransferOwnershipMetadataManager;
    final UsageStatsManagerInternal mUsageStatsManagerInternal;
    @GuardedBy({"getLockObject()"})
    final SparseArray<DevicePolicyData> mUserData;
    final UserManager mUserManager;
    final UserManagerInternal mUserManagerInternal;
    @GuardedBy({"getLockObject()"})
    final SparseArray<PasswordMetrics> mUserPasswordMetrics;

    interface Stats {
        public static final int COUNT = 1;
        public static final int LOCK_GUARD_GUARD = 0;
    }

    static {
        SECURE_SETTINGS_WHITELIST.add("default_input_method");
        SECURE_SETTINGS_WHITELIST.add("skip_first_use_hints");
        SECURE_SETTINGS_WHITELIST.add("install_non_market_apps");
        SECURE_SETTINGS_DEVICEOWNER_WHITELIST.addAll(SECURE_SETTINGS_WHITELIST);
        SECURE_SETTINGS_DEVICEOWNER_WHITELIST.add("location_mode");
        GLOBAL_SETTINGS_WHITELIST.add("adb_enabled");
        GLOBAL_SETTINGS_WHITELIST.add("auto_time");
        GLOBAL_SETTINGS_WHITELIST.add("auto_time_zone");
        GLOBAL_SETTINGS_WHITELIST.add("data_roaming");
        GLOBAL_SETTINGS_WHITELIST.add("usb_mass_storage_enabled");
        GLOBAL_SETTINGS_WHITELIST.add("wifi_sleep_policy");
        GLOBAL_SETTINGS_WHITELIST.add("stay_on_while_plugged_in");
        GLOBAL_SETTINGS_WHITELIST.add("wifi_device_owner_configs_lockdown");
        GLOBAL_SETTINGS_WHITELIST.add("private_dns_mode");
        GLOBAL_SETTINGS_WHITELIST.add("private_dns_specifier");
        GLOBAL_SETTINGS_DEPRECATED.add("bluetooth_on");
        GLOBAL_SETTINGS_DEPRECATED.add("development_settings_enabled");
        GLOBAL_SETTINGS_DEPRECATED.add("mode_ringer");
        GLOBAL_SETTINGS_DEPRECATED.add("network_preference");
        GLOBAL_SETTINGS_DEPRECATED.add("wifi_on");
        SYSTEM_SETTINGS_WHITELIST.add("screen_brightness");
        SYSTEM_SETTINGS_WHITELIST.add("screen_brightness_mode");
        SYSTEM_SETTINGS_WHITELIST.add("screen_off_timeout");
        DA_DISALLOWED_POLICIES.add(8);
        DA_DISALLOWED_POLICIES.add(9);
        DA_DISALLOWED_POLICIES.add(6);
        DA_DISALLOWED_POLICIES.add(0);
    }

    /* access modifiers changed from: package-private */
    public final Object getLockObject() {
        long start = this.mStatLogger.getTime();
        LockGuard.guard(7);
        this.mStatLogger.logDurationStat(0, start);
        return this.mLockDoNoUseDirectly;
    }

    /* access modifiers changed from: package-private */
    public final void ensureLocked() {
        if (!Thread.holdsLock(this.mLockDoNoUseDirectly)) {
            Slog.wtfStack(LOG_TAG, "Not holding DPMS lock.");
        }
    }

    public static final class Lifecycle extends SystemService {
        private BaseIDevicePolicyManager mService;

        public Lifecycle(Context context) {
            super(context);
            String dpmsClassName = context.getResources().getString(17039743);
            dpmsClassName = TextUtils.isEmpty(dpmsClassName) ? DevicePolicyManagerService.class.getName() : dpmsClassName;
            try {
                this.mService = (BaseIDevicePolicyManager) Class.forName(dpmsClassName).getConstructor(new Class[]{Context.class}).newInstance(new Object[]{context});
            } catch (Exception e) {
                throw new IllegalStateException("Failed to instantiate DevicePolicyManagerService with class name: " + dpmsClassName, e);
            }
        }

        /* JADX WARNING: type inference failed for: r0v0, types: [com.android.server.devicepolicy.BaseIDevicePolicyManager, android.os.IBinder] */
        public void onStart() {
            publishBinderService("device_policy", this.mService);
        }

        public void onBootPhase(int phase) {
            this.mService.systemReady(phase);
        }

        public void onStartUser(int userHandle) {
            this.mService.handleStartUser(userHandle);
        }

        public void onUnlockUser(int userHandle) {
            this.mService.handleUnlockUser(userHandle);
        }

        public void onStopUser(int userHandle) {
            this.mService.handleStopUser(userHandle);
        }
    }

    public static class DevicePolicyData {
        boolean doNotAskCredentialsOnBoot = false;
        final ArraySet<String> mAcceptedCaCertificates = new ArraySet<>();
        boolean mAdminBroadcastPending = false;
        final ArrayList<ActiveAdmin> mAdminList = new ArrayList<>();
        final ArrayMap<ComponentName, ActiveAdmin> mAdminMap = new ArrayMap<>();
        Set<String> mAffiliationIds = new ArraySet();
        boolean mCurrentInputMethodSet = false;
        final ArrayMap<String, List<String>> mDelegationMap = new ArrayMap<>();
        boolean mDeviceProvisioningConfigApplied = false;
        int mFailedPasswordAttempts = 0;
        PersistableBundle mInitBundle = null;
        long mLastBugReportRequestTime = -1;
        long mLastMaximumTimeToLock = -1;
        long mLastNetworkLogsRetrievalTime = -1;
        long mLastSecurityLogRetrievalTime = -1;
        int mLockTaskFeatures = 16;
        List<String> mLockTaskPackages = new ArrayList();
        Set<String> mOwnerInstalledCaCerts = new ArraySet();
        boolean mPaired = false;
        int mPasswordOwner = -1;
        long mPasswordTokenHandle = 0;
        boolean mPasswordValidAtLastCheckpoint = true;
        int mPermissionPolicy;
        final ArrayList<ComponentName> mRemovingAdmins = new ArrayList<>();
        ComponentName mRestrictionsProvider;
        boolean mStatusBarDisabled = false;
        int mUserHandle;
        int mUserProvisioningState;
        boolean mUserSetupComplete = false;

        public DevicePolicyData(int userHandle) {
            this.mUserHandle = userHandle;
        }
    }

    protected static class RestrictionsListener implements UserManagerInternal.UserRestrictionsListener {
        private Context mContext;

        public RestrictionsListener(Context context) {
            this.mContext = context;
        }

        public void onUserRestrictionsChanged(int userId, Bundle newRestrictions, Bundle prevRestrictions) {
            if (newRestrictions.getBoolean("no_sharing_into_profile") != prevRestrictions.getBoolean("no_sharing_into_profile")) {
                Intent intent = new Intent("android.app.action.DATA_SHARING_RESTRICTION_CHANGED");
                intent.setPackage(DevicePolicyManagerService.getManagedProvisioningPackage(this.mContext));
                intent.putExtra("android.intent.extra.USER_ID", userId);
                intent.addFlags(268435456);
                this.mContext.sendBroadcastAsUser(intent, UserHandle.SYSTEM);
            }
        }
    }

    static class ActiveAdmin {
        private static final String ATTR_LAST_NETWORK_LOGGING_NOTIFICATION = "last-notification";
        private static final String ATTR_NUM_NETWORK_LOGGING_NOTIFICATIONS = "num-notifications";
        private static final String ATTR_VALUE = "value";
        static final int DEF_KEYGUARD_FEATURES_DISABLED = 0;
        static final int DEF_MAXIMUM_FAILED_PASSWORDS_FOR_WIPE = 0;
        static final int DEF_MAXIMUM_NETWORK_LOGGING_NOTIFICATIONS_SHOWN = 2;
        static final long DEF_MAXIMUM_TIME_TO_UNLOCK = 0;
        static final int DEF_MINIMUM_PASSWORD_LENGTH = 0;
        static final int DEF_MINIMUM_PASSWORD_LETTERS = 1;
        static final int DEF_MINIMUM_PASSWORD_LOWER_CASE = 0;
        static final int DEF_MINIMUM_PASSWORD_NON_LETTER = 0;
        static final int DEF_MINIMUM_PASSWORD_NUMERIC = 1;
        static final int DEF_MINIMUM_PASSWORD_SYMBOLS = 1;
        static final int DEF_MINIMUM_PASSWORD_UPPER_CASE = 0;
        static final int DEF_ORGANIZATION_COLOR = Color.parseColor("#00796B");
        static final long DEF_PASSWORD_EXPIRATION_DATE = 0;
        static final long DEF_PASSWORD_EXPIRATION_TIMEOUT = 0;
        static final int DEF_PASSWORD_HISTORY_LENGTH = 0;
        private static final String TAG_ACCOUNT_TYPE = "account-type";
        private static final String TAG_CROSS_PROFILE_CALENDAR_PACKAGES = "cross-profile-calendar-packages";
        private static final String TAG_CROSS_PROFILE_CALENDAR_PACKAGES_NULL = "cross-profile-calendar-packages-null";
        private static final String TAG_CROSS_PROFILE_WIDGET_PROVIDERS = "cross-profile-widget-providers";
        private static final String TAG_DEFAULT_ENABLED_USER_RESTRICTIONS = "default-enabled-user-restrictions";
        private static final String TAG_DISABLE_ACCOUNT_MANAGEMENT = "disable-account-management";
        private static final String TAG_DISABLE_BLUETOOTH_CONTACT_SHARING = "disable-bt-contacts-sharing";
        private static final String TAG_DISABLE_CALLER_ID = "disable-caller-id";
        private static final String TAG_DISABLE_CAMERA = "disable-camera";
        private static final String TAG_DISABLE_CONTACTS_SEARCH = "disable-contacts-search";
        private static final String TAG_DISABLE_KEYGUARD_FEATURES = "disable-keyguard-features";
        private static final String TAG_DISABLE_SCREEN_CAPTURE = "disable-screen-capture";
        private static final String TAG_ENCRYPTION_REQUESTED = "encryption-requested";
        private static final String TAG_END_USER_SESSION_MESSAGE = "end_user_session_message";
        private static final String TAG_FORCE_EPHEMERAL_USERS = "force_ephemeral_users";
        private static final String TAG_GLOBAL_PROXY_EXCLUSION_LIST = "global-proxy-exclusion-list";
        private static final String TAG_GLOBAL_PROXY_SPEC = "global-proxy-spec";
        private static final String TAG_IS_LOGOUT_ENABLED = "is_logout_enabled";
        private static final String TAG_IS_NETWORK_LOGGING_ENABLED = "is_network_logging_enabled";
        private static final String TAG_KEEP_UNINSTALLED_PACKAGES = "keep-uninstalled-packages";
        private static final String TAG_LONG_SUPPORT_MESSAGE = "long-support-message";
        private static final String TAG_MANAGE_TRUST_AGENT_FEATURES = "manage-trust-agent-features";
        private static final String TAG_MAX_FAILED_PASSWORD_WIPE = "max-failed-password-wipe";
        private static final String TAG_MAX_TIME_TO_UNLOCK = "max-time-to-unlock";
        private static final String TAG_METERED_DATA_DISABLED_PACKAGES = "metered_data_disabled_packages";
        private static final String TAG_MIN_PASSWORD_LENGTH = "min-password-length";
        private static final String TAG_MIN_PASSWORD_LETTERS = "min-password-letters";
        private static final String TAG_MIN_PASSWORD_LOWERCASE = "min-password-lowercase";
        private static final String TAG_MIN_PASSWORD_NONLETTER = "min-password-nonletter";
        private static final String TAG_MIN_PASSWORD_NUMERIC = "min-password-numeric";
        private static final String TAG_MIN_PASSWORD_SYMBOLS = "min-password-symbols";
        private static final String TAG_MIN_PASSWORD_UPPERCASE = "min-password-uppercase";
        private static final String TAG_ORGANIZATION_COLOR = "organization-color";
        private static final String TAG_ORGANIZATION_NAME = "organization-name";
        private static final String TAG_PACKAGE_LIST_ITEM = "item";
        private static final String TAG_PARENT_ADMIN = "parent-admin";
        private static final String TAG_PASSWORD_EXPIRATION_DATE = "password-expiration-date";
        private static final String TAG_PASSWORD_EXPIRATION_TIMEOUT = "password-expiration-timeout";
        private static final String TAG_PASSWORD_HISTORY_LENGTH = "password-history-length";
        private static final String TAG_PASSWORD_QUALITY = "password-quality";
        private static final String TAG_PERMITTED_ACCESSIBILITY_SERVICES = "permitted-accessiblity-services";
        private static final String TAG_PERMITTED_IMES = "permitted-imes";
        private static final String TAG_PERMITTED_NOTIFICATION_LISTENERS = "permitted-notification-listeners";
        private static final String TAG_POLICIES = "policies";
        private static final String TAG_PROVIDER = "provider";
        private static final String TAG_REQUIRE_AUTO_TIME = "require_auto_time";
        private static final String TAG_RESTRICTION = "restriction";
        private static final String TAG_SHORT_SUPPORT_MESSAGE = "short-support-message";
        private static final String TAG_SPECIFIES_GLOBAL_PROXY = "specifies-global-proxy";
        private static final String TAG_START_USER_SESSION_MESSAGE = "start_user_session_message";
        private static final String TAG_STRONG_AUTH_UNLOCK_TIMEOUT = "strong-auth-unlock-timeout";
        private static final String TAG_TEST_ONLY_ADMIN = "test-only-admin";
        private static final String TAG_TRUST_AGENT_COMPONENT = "component";
        private static final String TAG_TRUST_AGENT_COMPONENT_OPTIONS = "trust-agent-component-options";
        private static final String TAG_USER_RESTRICTIONS = "user-restrictions";
        final Set<String> accountTypesWithManagementDisabled = new ArraySet();
        List<String> crossProfileWidgetProviders;
        final Set<String> defaultEnabledRestrictionsAlreadySet = new ArraySet();
        boolean disableBluetoothContactSharing = true;
        boolean disableCallerId = false;
        boolean disableCamera = false;
        boolean disableContactsSearch = false;
        boolean disableScreenCapture = false;
        int disabledKeyguardFeatures = 0;
        boolean encryptionRequested = false;
        String endUserSessionMessage = null;
        boolean forceEphemeralUsers = false;
        String globalProxyExclusionList = null;
        String globalProxySpec = null;
        DeviceAdminInfo info;
        boolean isLogoutEnabled = false;
        boolean isNetworkLoggingEnabled = false;
        final boolean isParent;
        List<String> keepUninstalledPackages;
        long lastNetworkLoggingNotificationTimeMs = 0;
        CharSequence longSupportMessage = null;
        List<String> mCrossProfileCalendarPackages = Collections.emptyList();
        int maximumFailedPasswordsForWipe = 0;
        long maximumTimeToUnlock = 0;
        List<String> meteredDisabledPackages;
        PasswordMetrics minimumPasswordMetrics = new PasswordMetrics(0, 0, 1, 0, 0, 1, 1, 0);
        int numNetworkLoggingNotifications = 0;
        int organizationColor = DEF_ORGANIZATION_COLOR;
        String organizationName = null;
        ActiveAdmin parentAdmin;
        long passwordExpirationDate = 0;
        long passwordExpirationTimeout = 0;
        int passwordHistoryLength = 0;
        List<String> permittedAccessiblityServices;
        List<String> permittedInputMethods;
        List<String> permittedNotificationListeners;
        boolean requireAutoTime = false;
        CharSequence shortSupportMessage = null;
        boolean specifiesGlobalProxy = false;
        String startUserSessionMessage = null;
        long strongAuthUnlockTimeout = 0;
        boolean testOnlyAdmin = false;
        ArrayMap<String, TrustAgentInfo> trustAgentInfos = new ArrayMap<>();
        Bundle userRestrictions;

        static class TrustAgentInfo {
            public PersistableBundle options;

            TrustAgentInfo(PersistableBundle bundle) {
                this.options = bundle;
            }
        }

        ActiveAdmin(DeviceAdminInfo _info, boolean parent) {
            this.info = _info;
            this.isParent = parent;
        }

        /* access modifiers changed from: package-private */
        public ActiveAdmin getParentActiveAdmin() {
            Preconditions.checkState(!this.isParent);
            if (this.parentAdmin == null) {
                this.parentAdmin = new ActiveAdmin(this.info, true);
            }
            return this.parentAdmin;
        }

        /* access modifiers changed from: package-private */
        public boolean hasParentActiveAdmin() {
            return this.parentAdmin != null;
        }

        /* access modifiers changed from: package-private */
        public int getUid() {
            return this.info.getActivityInfo().applicationInfo.uid;
        }

        public UserHandle getUserHandle() {
            return UserHandle.of(UserHandle.getUserId(this.info.getActivityInfo().applicationInfo.uid));
        }

        /* access modifiers changed from: package-private */
        public void writeToXml(XmlSerializer out) throws IllegalArgumentException, IllegalStateException, IOException {
            out.startTag((String) null, TAG_POLICIES);
            this.info.writePoliciesToXml(out);
            out.endTag((String) null, TAG_POLICIES);
            if (this.minimumPasswordMetrics.quality != 0) {
                out.startTag((String) null, TAG_PASSWORD_QUALITY);
                out.attribute((String) null, ATTR_VALUE, Integer.toString(this.minimumPasswordMetrics.quality));
                out.endTag((String) null, TAG_PASSWORD_QUALITY);
                if (this.minimumPasswordMetrics.length != 0) {
                    out.startTag((String) null, TAG_MIN_PASSWORD_LENGTH);
                    out.attribute((String) null, ATTR_VALUE, Integer.toString(this.minimumPasswordMetrics.length));
                    out.endTag((String) null, TAG_MIN_PASSWORD_LENGTH);
                }
                if (this.passwordHistoryLength != 0) {
                    out.startTag((String) null, TAG_PASSWORD_HISTORY_LENGTH);
                    out.attribute((String) null, ATTR_VALUE, Integer.toString(this.passwordHistoryLength));
                    out.endTag((String) null, TAG_PASSWORD_HISTORY_LENGTH);
                }
                if (this.minimumPasswordMetrics.upperCase != 0) {
                    out.startTag((String) null, TAG_MIN_PASSWORD_UPPERCASE);
                    out.attribute((String) null, ATTR_VALUE, Integer.toString(this.minimumPasswordMetrics.upperCase));
                    out.endTag((String) null, TAG_MIN_PASSWORD_UPPERCASE);
                }
                if (this.minimumPasswordMetrics.lowerCase != 0) {
                    out.startTag((String) null, TAG_MIN_PASSWORD_LOWERCASE);
                    out.attribute((String) null, ATTR_VALUE, Integer.toString(this.minimumPasswordMetrics.lowerCase));
                    out.endTag((String) null, TAG_MIN_PASSWORD_LOWERCASE);
                }
                if (this.minimumPasswordMetrics.letters != 1) {
                    out.startTag((String) null, TAG_MIN_PASSWORD_LETTERS);
                    out.attribute((String) null, ATTR_VALUE, Integer.toString(this.minimumPasswordMetrics.letters));
                    out.endTag((String) null, TAG_MIN_PASSWORD_LETTERS);
                }
                if (this.minimumPasswordMetrics.numeric != 1) {
                    out.startTag((String) null, TAG_MIN_PASSWORD_NUMERIC);
                    out.attribute((String) null, ATTR_VALUE, Integer.toString(this.minimumPasswordMetrics.numeric));
                    out.endTag((String) null, TAG_MIN_PASSWORD_NUMERIC);
                }
                if (this.minimumPasswordMetrics.symbols != 1) {
                    out.startTag((String) null, TAG_MIN_PASSWORD_SYMBOLS);
                    out.attribute((String) null, ATTR_VALUE, Integer.toString(this.minimumPasswordMetrics.symbols));
                    out.endTag((String) null, TAG_MIN_PASSWORD_SYMBOLS);
                }
                if (this.minimumPasswordMetrics.nonLetter > 0) {
                    out.startTag((String) null, TAG_MIN_PASSWORD_NONLETTER);
                    out.attribute((String) null, ATTR_VALUE, Integer.toString(this.minimumPasswordMetrics.nonLetter));
                    out.endTag((String) null, TAG_MIN_PASSWORD_NONLETTER);
                }
            }
            if (this.maximumTimeToUnlock != 0) {
                out.startTag((String) null, TAG_MAX_TIME_TO_UNLOCK);
                out.attribute((String) null, ATTR_VALUE, Long.toString(this.maximumTimeToUnlock));
                out.endTag((String) null, TAG_MAX_TIME_TO_UNLOCK);
            }
            if (this.strongAuthUnlockTimeout != 259200000) {
                out.startTag((String) null, TAG_STRONG_AUTH_UNLOCK_TIMEOUT);
                out.attribute((String) null, ATTR_VALUE, Long.toString(this.strongAuthUnlockTimeout));
                out.endTag((String) null, TAG_STRONG_AUTH_UNLOCK_TIMEOUT);
            }
            if (this.maximumFailedPasswordsForWipe != 0) {
                out.startTag((String) null, TAG_MAX_FAILED_PASSWORD_WIPE);
                out.attribute((String) null, ATTR_VALUE, Integer.toString(this.maximumFailedPasswordsForWipe));
                out.endTag((String) null, TAG_MAX_FAILED_PASSWORD_WIPE);
            }
            if (this.specifiesGlobalProxy) {
                out.startTag((String) null, TAG_SPECIFIES_GLOBAL_PROXY);
                out.attribute((String) null, ATTR_VALUE, Boolean.toString(this.specifiesGlobalProxy));
                out.endTag((String) null, TAG_SPECIFIES_GLOBAL_PROXY);
                if (this.globalProxySpec != null) {
                    out.startTag((String) null, TAG_GLOBAL_PROXY_SPEC);
                    out.attribute((String) null, ATTR_VALUE, this.globalProxySpec);
                    out.endTag((String) null, TAG_GLOBAL_PROXY_SPEC);
                }
                if (this.globalProxyExclusionList != null) {
                    out.startTag((String) null, TAG_GLOBAL_PROXY_EXCLUSION_LIST);
                    out.attribute((String) null, ATTR_VALUE, this.globalProxyExclusionList);
                    out.endTag((String) null, TAG_GLOBAL_PROXY_EXCLUSION_LIST);
                }
            }
            if (this.passwordExpirationTimeout != 0) {
                out.startTag((String) null, TAG_PASSWORD_EXPIRATION_TIMEOUT);
                out.attribute((String) null, ATTR_VALUE, Long.toString(this.passwordExpirationTimeout));
                out.endTag((String) null, TAG_PASSWORD_EXPIRATION_TIMEOUT);
            }
            if (this.passwordExpirationDate != 0) {
                out.startTag((String) null, TAG_PASSWORD_EXPIRATION_DATE);
                out.attribute((String) null, ATTR_VALUE, Long.toString(this.passwordExpirationDate));
                out.endTag((String) null, TAG_PASSWORD_EXPIRATION_DATE);
            }
            if (this.encryptionRequested) {
                out.startTag((String) null, TAG_ENCRYPTION_REQUESTED);
                out.attribute((String) null, ATTR_VALUE, Boolean.toString(this.encryptionRequested));
                out.endTag((String) null, TAG_ENCRYPTION_REQUESTED);
            }
            if (this.testOnlyAdmin) {
                out.startTag((String) null, TAG_TEST_ONLY_ADMIN);
                out.attribute((String) null, ATTR_VALUE, Boolean.toString(this.testOnlyAdmin));
                out.endTag((String) null, TAG_TEST_ONLY_ADMIN);
            }
            if (this.disableCamera) {
                out.startTag((String) null, TAG_DISABLE_CAMERA);
                out.attribute((String) null, ATTR_VALUE, Boolean.toString(this.disableCamera));
                out.endTag((String) null, TAG_DISABLE_CAMERA);
            }
            if (this.disableCallerId) {
                out.startTag((String) null, TAG_DISABLE_CALLER_ID);
                out.attribute((String) null, ATTR_VALUE, Boolean.toString(this.disableCallerId));
                out.endTag((String) null, TAG_DISABLE_CALLER_ID);
            }
            if (this.disableContactsSearch) {
                out.startTag((String) null, TAG_DISABLE_CONTACTS_SEARCH);
                out.attribute((String) null, ATTR_VALUE, Boolean.toString(this.disableContactsSearch));
                out.endTag((String) null, TAG_DISABLE_CONTACTS_SEARCH);
            }
            if (!this.disableBluetoothContactSharing) {
                out.startTag((String) null, TAG_DISABLE_BLUETOOTH_CONTACT_SHARING);
                out.attribute((String) null, ATTR_VALUE, Boolean.toString(this.disableBluetoothContactSharing));
                out.endTag((String) null, TAG_DISABLE_BLUETOOTH_CONTACT_SHARING);
            }
            if (this.disableScreenCapture) {
                out.startTag((String) null, TAG_DISABLE_SCREEN_CAPTURE);
                out.attribute((String) null, ATTR_VALUE, Boolean.toString(this.disableScreenCapture));
                out.endTag((String) null, TAG_DISABLE_SCREEN_CAPTURE);
            }
            if (this.requireAutoTime) {
                out.startTag((String) null, TAG_REQUIRE_AUTO_TIME);
                out.attribute((String) null, ATTR_VALUE, Boolean.toString(this.requireAutoTime));
                out.endTag((String) null, TAG_REQUIRE_AUTO_TIME);
            }
            if (this.forceEphemeralUsers) {
                out.startTag((String) null, TAG_FORCE_EPHEMERAL_USERS);
                out.attribute((String) null, ATTR_VALUE, Boolean.toString(this.forceEphemeralUsers));
                out.endTag((String) null, TAG_FORCE_EPHEMERAL_USERS);
            }
            if (this.isNetworkLoggingEnabled) {
                out.startTag((String) null, TAG_IS_NETWORK_LOGGING_ENABLED);
                out.attribute((String) null, ATTR_VALUE, Boolean.toString(this.isNetworkLoggingEnabled));
                out.attribute((String) null, ATTR_NUM_NETWORK_LOGGING_NOTIFICATIONS, Integer.toString(this.numNetworkLoggingNotifications));
                out.attribute((String) null, ATTR_LAST_NETWORK_LOGGING_NOTIFICATION, Long.toString(this.lastNetworkLoggingNotificationTimeMs));
                out.endTag((String) null, TAG_IS_NETWORK_LOGGING_ENABLED);
            }
            if (this.disabledKeyguardFeatures != 0) {
                out.startTag((String) null, TAG_DISABLE_KEYGUARD_FEATURES);
                out.attribute((String) null, ATTR_VALUE, Integer.toString(this.disabledKeyguardFeatures));
                out.endTag((String) null, TAG_DISABLE_KEYGUARD_FEATURES);
            }
            if (!this.accountTypesWithManagementDisabled.isEmpty()) {
                out.startTag((String) null, TAG_DISABLE_ACCOUNT_MANAGEMENT);
                writeAttributeValuesToXml(out, TAG_ACCOUNT_TYPE, this.accountTypesWithManagementDisabled);
                out.endTag((String) null, TAG_DISABLE_ACCOUNT_MANAGEMENT);
            }
            if (!this.trustAgentInfos.isEmpty()) {
                Set<Map.Entry<String, TrustAgentInfo>> set = this.trustAgentInfos.entrySet();
                out.startTag((String) null, TAG_MANAGE_TRUST_AGENT_FEATURES);
                for (Map.Entry<String, TrustAgentInfo> entry : set) {
                    TrustAgentInfo trustAgentInfo = entry.getValue();
                    out.startTag((String) null, TAG_TRUST_AGENT_COMPONENT);
                    out.attribute((String) null, ATTR_VALUE, entry.getKey());
                    if (trustAgentInfo.options != null) {
                        out.startTag((String) null, TAG_TRUST_AGENT_COMPONENT_OPTIONS);
                        try {
                            trustAgentInfo.options.saveToXml(out);
                        } catch (XmlPullParserException e) {
                            Log.e(DevicePolicyManagerService.LOG_TAG, "Failed to save TrustAgent options", e);
                        }
                        out.endTag((String) null, TAG_TRUST_AGENT_COMPONENT_OPTIONS);
                    }
                    out.endTag((String) null, TAG_TRUST_AGENT_COMPONENT);
                }
                out.endTag((String) null, TAG_MANAGE_TRUST_AGENT_FEATURES);
            }
            List<String> list = this.crossProfileWidgetProviders;
            if (list != null && !list.isEmpty()) {
                out.startTag((String) null, TAG_CROSS_PROFILE_WIDGET_PROVIDERS);
                writeAttributeValuesToXml(out, TAG_PROVIDER, this.crossProfileWidgetProviders);
                out.endTag((String) null, TAG_CROSS_PROFILE_WIDGET_PROVIDERS);
            }
            writePackageListToXml(out, TAG_PERMITTED_ACCESSIBILITY_SERVICES, this.permittedAccessiblityServices);
            writePackageListToXml(out, TAG_PERMITTED_IMES, this.permittedInputMethods);
            writePackageListToXml(out, TAG_PERMITTED_NOTIFICATION_LISTENERS, this.permittedNotificationListeners);
            writePackageListToXml(out, TAG_KEEP_UNINSTALLED_PACKAGES, this.keepUninstalledPackages);
            writePackageListToXml(out, TAG_METERED_DATA_DISABLED_PACKAGES, this.meteredDisabledPackages);
            if (hasUserRestrictions()) {
                UserRestrictionsUtils.writeRestrictions(out, this.userRestrictions, TAG_USER_RESTRICTIONS);
            }
            if (!this.defaultEnabledRestrictionsAlreadySet.isEmpty()) {
                out.startTag((String) null, TAG_DEFAULT_ENABLED_USER_RESTRICTIONS);
                writeAttributeValuesToXml(out, TAG_RESTRICTION, this.defaultEnabledRestrictionsAlreadySet);
                out.endTag((String) null, TAG_DEFAULT_ENABLED_USER_RESTRICTIONS);
            }
            if (!TextUtils.isEmpty(this.shortSupportMessage)) {
                out.startTag((String) null, TAG_SHORT_SUPPORT_MESSAGE);
                out.text(this.shortSupportMessage.toString());
                out.endTag((String) null, TAG_SHORT_SUPPORT_MESSAGE);
            }
            if (!TextUtils.isEmpty(this.longSupportMessage)) {
                out.startTag((String) null, TAG_LONG_SUPPORT_MESSAGE);
                out.text(this.longSupportMessage.toString());
                out.endTag((String) null, TAG_LONG_SUPPORT_MESSAGE);
            }
            if (this.parentAdmin != null) {
                out.startTag((String) null, TAG_PARENT_ADMIN);
                this.parentAdmin.writeToXml(out);
                out.endTag((String) null, TAG_PARENT_ADMIN);
            }
            if (this.organizationColor != DEF_ORGANIZATION_COLOR) {
                out.startTag((String) null, TAG_ORGANIZATION_COLOR);
                out.attribute((String) null, ATTR_VALUE, Integer.toString(this.organizationColor));
                out.endTag((String) null, TAG_ORGANIZATION_COLOR);
            }
            if (this.organizationName != null) {
                out.startTag((String) null, TAG_ORGANIZATION_NAME);
                out.text(this.organizationName);
                out.endTag((String) null, TAG_ORGANIZATION_NAME);
            }
            if (this.isLogoutEnabled) {
                out.startTag((String) null, TAG_IS_LOGOUT_ENABLED);
                out.attribute((String) null, ATTR_VALUE, Boolean.toString(this.isLogoutEnabled));
                out.endTag((String) null, TAG_IS_LOGOUT_ENABLED);
            }
            if (this.startUserSessionMessage != null) {
                out.startTag((String) null, TAG_START_USER_SESSION_MESSAGE);
                out.text(this.startUserSessionMessage);
                out.endTag((String) null, TAG_START_USER_SESSION_MESSAGE);
            }
            if (this.endUserSessionMessage != null) {
                out.startTag((String) null, TAG_END_USER_SESSION_MESSAGE);
                out.text(this.endUserSessionMessage);
                out.endTag((String) null, TAG_END_USER_SESSION_MESSAGE);
            }
            List<String> list2 = this.mCrossProfileCalendarPackages;
            if (list2 == null) {
                out.startTag((String) null, TAG_CROSS_PROFILE_CALENDAR_PACKAGES_NULL);
                out.endTag((String) null, TAG_CROSS_PROFILE_CALENDAR_PACKAGES_NULL);
                return;
            }
            writePackageListToXml(out, TAG_CROSS_PROFILE_CALENDAR_PACKAGES, list2);
        }

        /* access modifiers changed from: package-private */
        public void writePackageListToXml(XmlSerializer out, String outerTag, List<String> packageList) throws IllegalArgumentException, IllegalStateException, IOException {
            if (packageList != null) {
                out.startTag((String) null, outerTag);
                writeAttributeValuesToXml(out, "item", packageList);
                out.endTag((String) null, outerTag);
            }
        }

        /* access modifiers changed from: package-private */
        public void writeAttributeValuesToXml(XmlSerializer out, String tag, Collection<String> values) throws IOException {
            for (String value : values) {
                out.startTag((String) null, tag);
                out.attribute((String) null, ATTR_VALUE, value);
                out.endTag((String) null, tag);
            }
        }

        /* access modifiers changed from: package-private */
        public void readFromXml(XmlPullParser parser, boolean shouldOverridePolicies) throws XmlPullParserException, IOException {
            int outerDepth = parser.getDepth();
            while (true) {
                int next = parser.next();
                int type = next;
                if (next == 1) {
                    return;
                }
                if (type == 3 && parser.getDepth() <= outerDepth) {
                    return;
                }
                if (!(type == 3 || type == 4)) {
                    String tag = parser.getName();
                    if (TAG_POLICIES.equals(tag)) {
                        if (shouldOverridePolicies) {
                            Log.d(DevicePolicyManagerService.LOG_TAG, "Overriding device admin policies from XML.");
                            this.info.readPoliciesFromXml(parser);
                        }
                    } else if (TAG_PASSWORD_QUALITY.equals(tag)) {
                        this.minimumPasswordMetrics.quality = Integer.parseInt(parser.getAttributeValue((String) null, ATTR_VALUE));
                    } else if (TAG_MIN_PASSWORD_LENGTH.equals(tag)) {
                        this.minimumPasswordMetrics.length = Integer.parseInt(parser.getAttributeValue((String) null, ATTR_VALUE));
                    } else if (TAG_PASSWORD_HISTORY_LENGTH.equals(tag)) {
                        this.passwordHistoryLength = Integer.parseInt(parser.getAttributeValue((String) null, ATTR_VALUE));
                    } else if (TAG_MIN_PASSWORD_UPPERCASE.equals(tag)) {
                        this.minimumPasswordMetrics.upperCase = Integer.parseInt(parser.getAttributeValue((String) null, ATTR_VALUE));
                    } else if (TAG_MIN_PASSWORD_LOWERCASE.equals(tag)) {
                        this.minimumPasswordMetrics.lowerCase = Integer.parseInt(parser.getAttributeValue((String) null, ATTR_VALUE));
                    } else if (TAG_MIN_PASSWORD_LETTERS.equals(tag)) {
                        this.minimumPasswordMetrics.letters = Integer.parseInt(parser.getAttributeValue((String) null, ATTR_VALUE));
                    } else if (TAG_MIN_PASSWORD_NUMERIC.equals(tag)) {
                        this.minimumPasswordMetrics.numeric = Integer.parseInt(parser.getAttributeValue((String) null, ATTR_VALUE));
                    } else if (TAG_MIN_PASSWORD_SYMBOLS.equals(tag)) {
                        this.minimumPasswordMetrics.symbols = Integer.parseInt(parser.getAttributeValue((String) null, ATTR_VALUE));
                    } else if (TAG_MIN_PASSWORD_NONLETTER.equals(tag)) {
                        this.minimumPasswordMetrics.nonLetter = Integer.parseInt(parser.getAttributeValue((String) null, ATTR_VALUE));
                    } else if (TAG_MAX_TIME_TO_UNLOCK.equals(tag)) {
                        this.maximumTimeToUnlock = Long.parseLong(parser.getAttributeValue((String) null, ATTR_VALUE));
                    } else if (TAG_STRONG_AUTH_UNLOCK_TIMEOUT.equals(tag)) {
                        this.strongAuthUnlockTimeout = Long.parseLong(parser.getAttributeValue((String) null, ATTR_VALUE));
                    } else if (TAG_MAX_FAILED_PASSWORD_WIPE.equals(tag)) {
                        this.maximumFailedPasswordsForWipe = Integer.parseInt(parser.getAttributeValue((String) null, ATTR_VALUE));
                    } else if (TAG_SPECIFIES_GLOBAL_PROXY.equals(tag)) {
                        this.specifiesGlobalProxy = Boolean.parseBoolean(parser.getAttributeValue((String) null, ATTR_VALUE));
                    } else if (TAG_GLOBAL_PROXY_SPEC.equals(tag)) {
                        this.globalProxySpec = parser.getAttributeValue((String) null, ATTR_VALUE);
                    } else if (TAG_GLOBAL_PROXY_EXCLUSION_LIST.equals(tag)) {
                        this.globalProxyExclusionList = parser.getAttributeValue((String) null, ATTR_VALUE);
                    } else if (TAG_PASSWORD_EXPIRATION_TIMEOUT.equals(tag)) {
                        this.passwordExpirationTimeout = Long.parseLong(parser.getAttributeValue((String) null, ATTR_VALUE));
                    } else if (TAG_PASSWORD_EXPIRATION_DATE.equals(tag)) {
                        this.passwordExpirationDate = Long.parseLong(parser.getAttributeValue((String) null, ATTR_VALUE));
                    } else if (TAG_ENCRYPTION_REQUESTED.equals(tag)) {
                        this.encryptionRequested = Boolean.parseBoolean(parser.getAttributeValue((String) null, ATTR_VALUE));
                    } else if (TAG_TEST_ONLY_ADMIN.equals(tag)) {
                        this.testOnlyAdmin = Boolean.parseBoolean(parser.getAttributeValue((String) null, ATTR_VALUE));
                    } else if (TAG_DISABLE_CAMERA.equals(tag)) {
                        this.disableCamera = Boolean.parseBoolean(parser.getAttributeValue((String) null, ATTR_VALUE));
                    } else if (TAG_DISABLE_CALLER_ID.equals(tag)) {
                        this.disableCallerId = Boolean.parseBoolean(parser.getAttributeValue((String) null, ATTR_VALUE));
                    } else if (TAG_DISABLE_CONTACTS_SEARCH.equals(tag)) {
                        this.disableContactsSearch = Boolean.parseBoolean(parser.getAttributeValue((String) null, ATTR_VALUE));
                    } else if (TAG_DISABLE_BLUETOOTH_CONTACT_SHARING.equals(tag)) {
                        this.disableBluetoothContactSharing = Boolean.parseBoolean(parser.getAttributeValue((String) null, ATTR_VALUE));
                    } else if (TAG_DISABLE_SCREEN_CAPTURE.equals(tag)) {
                        this.disableScreenCapture = Boolean.parseBoolean(parser.getAttributeValue((String) null, ATTR_VALUE));
                    } else if (TAG_REQUIRE_AUTO_TIME.equals(tag)) {
                        this.requireAutoTime = Boolean.parseBoolean(parser.getAttributeValue((String) null, ATTR_VALUE));
                    } else if (TAG_FORCE_EPHEMERAL_USERS.equals(tag)) {
                        this.forceEphemeralUsers = Boolean.parseBoolean(parser.getAttributeValue((String) null, ATTR_VALUE));
                    } else if (TAG_IS_NETWORK_LOGGING_ENABLED.equals(tag)) {
                        this.isNetworkLoggingEnabled = Boolean.parseBoolean(parser.getAttributeValue((String) null, ATTR_VALUE));
                        this.lastNetworkLoggingNotificationTimeMs = Long.parseLong(parser.getAttributeValue((String) null, ATTR_LAST_NETWORK_LOGGING_NOTIFICATION));
                        this.numNetworkLoggingNotifications = Integer.parseInt(parser.getAttributeValue((String) null, ATTR_NUM_NETWORK_LOGGING_NOTIFICATIONS));
                    } else if (TAG_DISABLE_KEYGUARD_FEATURES.equals(tag)) {
                        this.disabledKeyguardFeatures = Integer.parseInt(parser.getAttributeValue((String) null, ATTR_VALUE));
                    } else if (TAG_DISABLE_ACCOUNT_MANAGEMENT.equals(tag)) {
                        readAttributeValues(parser, TAG_ACCOUNT_TYPE, this.accountTypesWithManagementDisabled);
                    } else if (TAG_MANAGE_TRUST_AGENT_FEATURES.equals(tag)) {
                        this.trustAgentInfos = getAllTrustAgentInfos(parser, tag);
                    } else if (TAG_CROSS_PROFILE_WIDGET_PROVIDERS.equals(tag)) {
                        this.crossProfileWidgetProviders = new ArrayList();
                        readAttributeValues(parser, TAG_PROVIDER, this.crossProfileWidgetProviders);
                    } else if (TAG_PERMITTED_ACCESSIBILITY_SERVICES.equals(tag)) {
                        this.permittedAccessiblityServices = readPackageList(parser, tag);
                    } else if (TAG_PERMITTED_IMES.equals(tag)) {
                        this.permittedInputMethods = readPackageList(parser, tag);
                    } else if (TAG_PERMITTED_NOTIFICATION_LISTENERS.equals(tag)) {
                        this.permittedNotificationListeners = readPackageList(parser, tag);
                    } else if (TAG_KEEP_UNINSTALLED_PACKAGES.equals(tag)) {
                        this.keepUninstalledPackages = readPackageList(parser, tag);
                    } else if (TAG_METERED_DATA_DISABLED_PACKAGES.equals(tag)) {
                        this.meteredDisabledPackages = readPackageList(parser, tag);
                    } else if (TAG_USER_RESTRICTIONS.equals(tag)) {
                        this.userRestrictions = UserRestrictionsUtils.readRestrictions(parser);
                    } else if (TAG_DEFAULT_ENABLED_USER_RESTRICTIONS.equals(tag)) {
                        readAttributeValues(parser, TAG_RESTRICTION, this.defaultEnabledRestrictionsAlreadySet);
                    } else if (TAG_SHORT_SUPPORT_MESSAGE.equals(tag)) {
                        if (parser.next() == 4) {
                            this.shortSupportMessage = parser.getText();
                        } else {
                            Log.w(DevicePolicyManagerService.LOG_TAG, "Missing text when loading short support message");
                        }
                    } else if (TAG_LONG_SUPPORT_MESSAGE.equals(tag)) {
                        if (parser.next() == 4) {
                            this.longSupportMessage = parser.getText();
                        } else {
                            Log.w(DevicePolicyManagerService.LOG_TAG, "Missing text when loading long support message");
                        }
                    } else if (TAG_PARENT_ADMIN.equals(tag)) {
                        Preconditions.checkState(!this.isParent);
                        this.parentAdmin = new ActiveAdmin(this.info, true);
                        this.parentAdmin.readFromXml(parser, shouldOverridePolicies);
                    } else if (TAG_ORGANIZATION_COLOR.equals(tag)) {
                        this.organizationColor = Integer.parseInt(parser.getAttributeValue((String) null, ATTR_VALUE));
                    } else if (TAG_ORGANIZATION_NAME.equals(tag)) {
                        if (parser.next() == 4) {
                            this.organizationName = parser.getText();
                        } else {
                            Log.w(DevicePolicyManagerService.LOG_TAG, "Missing text when loading organization name");
                        }
                    } else if (TAG_IS_LOGOUT_ENABLED.equals(tag)) {
                        this.isLogoutEnabled = Boolean.parseBoolean(parser.getAttributeValue((String) null, ATTR_VALUE));
                    } else if (TAG_START_USER_SESSION_MESSAGE.equals(tag)) {
                        if (parser.next() == 4) {
                            this.startUserSessionMessage = parser.getText();
                        } else {
                            Log.w(DevicePolicyManagerService.LOG_TAG, "Missing text when loading start session message");
                        }
                    } else if (TAG_END_USER_SESSION_MESSAGE.equals(tag)) {
                        if (parser.next() == 4) {
                            this.endUserSessionMessage = parser.getText();
                        } else {
                            Log.w(DevicePolicyManagerService.LOG_TAG, "Missing text when loading end session message");
                        }
                    } else if (TAG_CROSS_PROFILE_CALENDAR_PACKAGES.equals(tag)) {
                        this.mCrossProfileCalendarPackages = readPackageList(parser, tag);
                    } else if (TAG_CROSS_PROFILE_CALENDAR_PACKAGES_NULL.equals(tag)) {
                        this.mCrossProfileCalendarPackages = null;
                    } else {
                        Slog.w(DevicePolicyManagerService.LOG_TAG, "Unknown admin tag: " + tag);
                        XmlUtils.skipCurrentTag(parser);
                    }
                }
            }
        }

        private List<String> readPackageList(XmlPullParser parser, String tag) throws XmlPullParserException, IOException {
            List<String> result = new ArrayList<>();
            int outerDepth = parser.getDepth();
            while (true) {
                int next = parser.next();
                int outerType = next;
                if (next == 1 || (outerType == 3 && parser.getDepth() <= outerDepth)) {
                    return result;
                }
                if (!(outerType == 3 || outerType == 4)) {
                    String outerTag = parser.getName();
                    if ("item".equals(outerTag)) {
                        String packageName = parser.getAttributeValue((String) null, ATTR_VALUE);
                        if (packageName != null) {
                            result.add(packageName);
                        } else {
                            Slog.w(DevicePolicyManagerService.LOG_TAG, "Package name missing under " + outerTag);
                        }
                    } else {
                        Slog.w(DevicePolicyManagerService.LOG_TAG, "Unknown tag under " + tag + ": " + outerTag);
                    }
                }
            }
            return result;
        }

        private void readAttributeValues(XmlPullParser parser, String tag, Collection<String> result) throws XmlPullParserException, IOException {
            result.clear();
            int outerDepthDAM = parser.getDepth();
            while (true) {
                int next = parser.next();
                int typeDAM = next;
                if (next == 1) {
                    return;
                }
                if (typeDAM == 3 && parser.getDepth() <= outerDepthDAM) {
                    return;
                }
                if (!(typeDAM == 3 || typeDAM == 4)) {
                    String tagDAM = parser.getName();
                    if (tag.equals(tagDAM)) {
                        result.add(parser.getAttributeValue((String) null, ATTR_VALUE));
                    } else {
                        Slog.e(DevicePolicyManagerService.LOG_TAG, "Expected tag " + tag + " but found " + tagDAM);
                    }
                }
            }
        }

        private ArrayMap<String, TrustAgentInfo> getAllTrustAgentInfos(XmlPullParser parser, String tag) throws XmlPullParserException, IOException {
            int outerDepthDAM = parser.getDepth();
            ArrayMap<String, TrustAgentInfo> result = new ArrayMap<>();
            while (true) {
                int next = parser.next();
                int typeDAM = next;
                if (next == 1 || (typeDAM == 3 && parser.getDepth() <= outerDepthDAM)) {
                    return result;
                }
                if (!(typeDAM == 3 || typeDAM == 4)) {
                    String tagDAM = parser.getName();
                    if (TAG_TRUST_AGENT_COMPONENT.equals(tagDAM)) {
                        result.put(parser.getAttributeValue((String) null, ATTR_VALUE), getTrustAgentInfo(parser, tag));
                    } else {
                        Slog.w(DevicePolicyManagerService.LOG_TAG, "Unknown tag under " + tag + ": " + tagDAM);
                    }
                }
            }
            return result;
        }

        private TrustAgentInfo getTrustAgentInfo(XmlPullParser parser, String tag) throws XmlPullParserException, IOException {
            int outerDepthDAM = parser.getDepth();
            TrustAgentInfo result = new TrustAgentInfo((PersistableBundle) null);
            while (true) {
                int next = parser.next();
                int typeDAM = next;
                if (next == 1 || (typeDAM == 3 && parser.getDepth() <= outerDepthDAM)) {
                    return result;
                }
                if (!(typeDAM == 3 || typeDAM == 4)) {
                    String tagDAM = parser.getName();
                    if (TAG_TRUST_AGENT_COMPONENT_OPTIONS.equals(tagDAM)) {
                        result.options = PersistableBundle.restoreFromXml(parser);
                    } else {
                        Slog.w(DevicePolicyManagerService.LOG_TAG, "Unknown tag under " + tag + ": " + tagDAM);
                    }
                }
            }
            return result;
        }

        /* access modifiers changed from: package-private */
        public boolean hasUserRestrictions() {
            Bundle bundle = this.userRestrictions;
            return bundle != null && bundle.size() > 0;
        }

        /* access modifiers changed from: package-private */
        public Bundle ensureUserRestrictions() {
            if (this.userRestrictions == null) {
                this.userRestrictions = new Bundle();
            }
            return this.userRestrictions;
        }

        public void transfer(DeviceAdminInfo deviceAdminInfo) {
            if (hasParentActiveAdmin()) {
                this.parentAdmin.info = deviceAdminInfo;
            }
            this.info = deviceAdminInfo;
        }

        /* access modifiers changed from: package-private */
        public void dump(String prefix, PrintWriter pw) {
            pw.print(prefix);
            pw.print("uid=");
            pw.println(getUid());
            pw.print(prefix);
            pw.print("testOnlyAdmin=");
            pw.println(this.testOnlyAdmin);
            pw.print(prefix);
            pw.println("policies:");
            ArrayList<DeviceAdminInfo.PolicyInfo> pols = this.info.getUsedPolicies();
            if (pols != null) {
                for (int i = 0; i < pols.size(); i++) {
                    pw.print(prefix);
                    pw.print("  ");
                    pw.println(pols.get(i).tag);
                }
            }
            pw.print(prefix);
            pw.print("passwordQuality=0x");
            pw.println(Integer.toHexString(this.minimumPasswordMetrics.quality));
            pw.print(prefix);
            pw.print("minimumPasswordLength=");
            pw.println(this.minimumPasswordMetrics.length);
            pw.print(prefix);
            pw.print("passwordHistoryLength=");
            pw.println(this.passwordHistoryLength);
            pw.print(prefix);
            pw.print("minimumPasswordUpperCase=");
            pw.println(this.minimumPasswordMetrics.upperCase);
            pw.print(prefix);
            pw.print("minimumPasswordLowerCase=");
            pw.println(this.minimumPasswordMetrics.lowerCase);
            pw.print(prefix);
            pw.print("minimumPasswordLetters=");
            pw.println(this.minimumPasswordMetrics.letters);
            pw.print(prefix);
            pw.print("minimumPasswordNumeric=");
            pw.println(this.minimumPasswordMetrics.numeric);
            pw.print(prefix);
            pw.print("minimumPasswordSymbols=");
            pw.println(this.minimumPasswordMetrics.symbols);
            pw.print(prefix);
            pw.print("minimumPasswordNonLetter=");
            pw.println(this.minimumPasswordMetrics.nonLetter);
            pw.print(prefix);
            pw.print("maximumTimeToUnlock=");
            pw.println(this.maximumTimeToUnlock);
            pw.print(prefix);
            pw.print("strongAuthUnlockTimeout=");
            pw.println(this.strongAuthUnlockTimeout);
            pw.print(prefix);
            pw.print("maximumFailedPasswordsForWipe=");
            pw.println(this.maximumFailedPasswordsForWipe);
            pw.print(prefix);
            pw.print("specifiesGlobalProxy=");
            pw.println(this.specifiesGlobalProxy);
            pw.print(prefix);
            pw.print("passwordExpirationTimeout=");
            pw.println(this.passwordExpirationTimeout);
            pw.print(prefix);
            pw.print("passwordExpirationDate=");
            pw.println(this.passwordExpirationDate);
            if (this.globalProxySpec != null) {
                pw.print(prefix);
                pw.print("globalProxySpec=");
                pw.println(this.globalProxySpec);
            }
            if (this.globalProxyExclusionList != null) {
                pw.print(prefix);
                pw.print("globalProxyEclusionList=");
                pw.println(this.globalProxyExclusionList);
            }
            pw.print(prefix);
            pw.print("encryptionRequested=");
            pw.println(this.encryptionRequested);
            pw.print(prefix);
            pw.print("disableCamera=");
            pw.println(this.disableCamera);
            pw.print(prefix);
            pw.print("disableCallerId=");
            pw.println(this.disableCallerId);
            pw.print(prefix);
            pw.print("disableContactsSearch=");
            pw.println(this.disableContactsSearch);
            pw.print(prefix);
            pw.print("disableBluetoothContactSharing=");
            pw.println(this.disableBluetoothContactSharing);
            pw.print(prefix);
            pw.print("disableScreenCapture=");
            pw.println(this.disableScreenCapture);
            pw.print(prefix);
            pw.print("requireAutoTime=");
            pw.println(this.requireAutoTime);
            pw.print(prefix);
            pw.print("forceEphemeralUsers=");
            pw.println(this.forceEphemeralUsers);
            pw.print(prefix);
            pw.print("isNetworkLoggingEnabled=");
            pw.println(this.isNetworkLoggingEnabled);
            pw.print(prefix);
            pw.print("disabledKeyguardFeatures=");
            pw.println(this.disabledKeyguardFeatures);
            pw.print(prefix);
            pw.print("crossProfileWidgetProviders=");
            pw.println(this.crossProfileWidgetProviders);
            if (this.permittedAccessiblityServices != null) {
                pw.print(prefix);
                pw.print("permittedAccessibilityServices=");
                pw.println(this.permittedAccessiblityServices);
            }
            if (this.permittedInputMethods != null) {
                pw.print(prefix);
                pw.print("permittedInputMethods=");
                pw.println(this.permittedInputMethods);
            }
            if (this.permittedNotificationListeners != null) {
                pw.print(prefix);
                pw.print("permittedNotificationListeners=");
                pw.println(this.permittedNotificationListeners);
            }
            if (this.keepUninstalledPackages != null) {
                pw.print(prefix);
                pw.print("keepUninstalledPackages=");
                pw.println(this.keepUninstalledPackages);
            }
            pw.print(prefix);
            pw.print("organizationColor=");
            pw.println(this.organizationColor);
            if (this.organizationName != null) {
                pw.print(prefix);
                pw.print("organizationName=");
                pw.println(this.organizationName);
            }
            pw.print(prefix);
            pw.println("userRestrictions:");
            UserRestrictionsUtils.dumpRestrictions(pw, prefix + "  ", this.userRestrictions);
            pw.print(prefix);
            pw.print("defaultEnabledRestrictionsAlreadySet=");
            pw.println(this.defaultEnabledRestrictionsAlreadySet);
            pw.print(prefix);
            pw.print("isParent=");
            pw.println(this.isParent);
            if (this.parentAdmin != null) {
                pw.print(prefix);
                pw.println("parentAdmin:");
                ActiveAdmin activeAdmin = this.parentAdmin;
                activeAdmin.dump(prefix + "  ", pw);
            }
            if (this.mCrossProfileCalendarPackages != null) {
                pw.print(prefix);
                pw.print("mCrossProfileCalendarPackages=");
                pw.println(this.mCrossProfileCalendarPackages);
            }
        }
    }

    /* access modifiers changed from: private */
    public void handlePackagesChanged(String packageName, int userHandle) {
        boolean removedAdmin = false;
        DevicePolicyData policy = getUserData(userHandle);
        synchronized (getLockObject()) {
            for (int i = policy.mAdminList.size() - 1; i >= 0; i--) {
                ActiveAdmin aa = policy.mAdminList.get(i);
                try {
                    String adminPackage = aa.info.getPackageName();
                    if ((packageName == null || packageName.equals(adminPackage)) && (this.mIPackageManager.getPackageInfo(adminPackage, 0, userHandle) == null || this.mIPackageManager.getReceiverInfo(aa.info.getComponent(), 786432, userHandle) == null)) {
                        removedAdmin = true;
                        policy.mAdminList.remove(i);
                        policy.mAdminMap.remove(aa.info.getComponent());
                        pushActiveAdminPackagesLocked(userHandle);
                        pushMeteredDisabledPackagesLocked(userHandle);
                    }
                } catch (RemoteException e) {
                }
            }
            if (removedAdmin) {
                validatePasswordOwnerLocked(policy);
            }
            boolean removedDelegate = false;
            for (int i2 = policy.mDelegationMap.size() - 1; i2 >= 0; i2--) {
                if (isRemovedPackage(packageName, policy.mDelegationMap.keyAt(i2), userHandle)) {
                    policy.mDelegationMap.removeAt(i2);
                    removedDelegate = true;
                }
            }
            ComponentName owner = getOwnerComponent(userHandle);
            if (!(packageName == null || owner == null || !owner.getPackageName().equals(packageName))) {
                startOwnerService(userHandle, "package-broadcast");
            }
            if (removedAdmin || removedDelegate) {
                saveSettingsLocked(policy.mUserHandle);
            }
        }
        if (removedAdmin) {
            pushUserRestrictions(userHandle);
        }
    }

    private boolean isRemovedPackage(String changedPackage, String targetPackage, int userHandle) {
        if (targetPackage == null) {
            return false;
        }
        if (changedPackage != null) {
            try {
                if (!changedPackage.equals(targetPackage)) {
                    return false;
                }
            } catch (RemoteException e) {
                return false;
            }
        }
        if (this.mIPackageManager.getPackageInfo(targetPackage, 0, userHandle) == null) {
            return true;
        }
        return false;
    }

    @VisibleForTesting
    static class Injector {
        public final Context mContext;

        Injector(Context context) {
            this.mContext = context;
        }

        public boolean hasFeature() {
            return getPackageManager().hasSystemFeature("android.software.device_admin");
        }

        /* access modifiers changed from: package-private */
        public Context createContextAsUser(UserHandle user) throws PackageManager.NameNotFoundException {
            return this.mContext.createPackageContextAsUser(this.mContext.getPackageName(), 0, user);
        }

        /* access modifiers changed from: package-private */
        public Resources getResources() {
            return this.mContext.getResources();
        }

        /* access modifiers changed from: package-private */
        public Owners newOwners() {
            return new Owners(getUserManager(), getUserManagerInternal(), getPackageManagerInternal(), getActivityTaskManagerInternal());
        }

        /* access modifiers changed from: package-private */
        public UserManager getUserManager() {
            return UserManager.get(this.mContext);
        }

        /* access modifiers changed from: package-private */
        public UserManagerInternal getUserManagerInternal() {
            return (UserManagerInternal) LocalServices.getService(UserManagerInternal.class);
        }

        /* access modifiers changed from: package-private */
        public PackageManagerInternal getPackageManagerInternal() {
            return (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        }

        /* access modifiers changed from: package-private */
        public ActivityTaskManagerInternal getActivityTaskManagerInternal() {
            return (ActivityTaskManagerInternal) LocalServices.getService(ActivityTaskManagerInternal.class);
        }

        /* access modifiers changed from: package-private */
        public PermissionControllerManager getPermissionControllerManager(UserHandle user) {
            if (user.equals(this.mContext.getUser())) {
                return (PermissionControllerManager) this.mContext.getSystemService(PermissionControllerManager.class);
            }
            try {
                return (PermissionControllerManager) this.mContext.createPackageContextAsUser(this.mContext.getPackageName(), 0, user).getSystemService(PermissionControllerManager.class);
            } catch (PackageManager.NameNotFoundException notPossible) {
                throw new IllegalStateException(notPossible);
            }
        }

        /* access modifiers changed from: package-private */
        public UsageStatsManagerInternal getUsageStatsManagerInternal() {
            return (UsageStatsManagerInternal) LocalServices.getService(UsageStatsManagerInternal.class);
        }

        /* access modifiers changed from: package-private */
        public NetworkPolicyManagerInternal getNetworkPolicyManagerInternal() {
            return (NetworkPolicyManagerInternal) LocalServices.getService(NetworkPolicyManagerInternal.class);
        }

        /* access modifiers changed from: package-private */
        public NotificationManager getNotificationManager() {
            return (NotificationManager) this.mContext.getSystemService(NotificationManager.class);
        }

        /* access modifiers changed from: package-private */
        public IIpConnectivityMetrics getIIpConnectivityMetrics() {
            return IIpConnectivityMetrics.Stub.asInterface(ServiceManager.getService("connmetrics"));
        }

        /* access modifiers changed from: package-private */
        public PackageManager getPackageManager() {
            return this.mContext.getPackageManager();
        }

        /* access modifiers changed from: package-private */
        public PowerManagerInternal getPowerManagerInternal() {
            return (PowerManagerInternal) LocalServices.getService(PowerManagerInternal.class);
        }

        /* access modifiers changed from: package-private */
        public TelephonyManager getTelephonyManager() {
            return TelephonyManager.from(this.mContext);
        }

        /* access modifiers changed from: package-private */
        public TrustManager getTrustManager() {
            return (TrustManager) this.mContext.getSystemService("trust");
        }

        /* access modifiers changed from: package-private */
        public AlarmManager getAlarmManager() {
            return (AlarmManager) this.mContext.getSystemService(AlarmManager.class);
        }

        /* access modifiers changed from: package-private */
        public ConnectivityManager getConnectivityManager() {
            return (ConnectivityManager) this.mContext.getSystemService(ConnectivityManager.class);
        }

        /* access modifiers changed from: package-private */
        public IWindowManager getIWindowManager() {
            return IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
        }

        /* access modifiers changed from: package-private */
        public IActivityManager getIActivityManager() {
            return ActivityManager.getService();
        }

        /* access modifiers changed from: package-private */
        public IActivityTaskManager getIActivityTaskManager() {
            return ActivityTaskManager.getService();
        }

        /* access modifiers changed from: package-private */
        public ActivityManagerInternal getActivityManagerInternal() {
            return (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
        }

        /* access modifiers changed from: package-private */
        public IPackageManager getIPackageManager() {
            return AppGlobals.getPackageManager();
        }

        /* access modifiers changed from: package-private */
        public IBackupManager getIBackupManager() {
            return IBackupManager.Stub.asInterface(ServiceManager.getService(BatteryService.HealthServiceWrapper.INSTANCE_HEALTHD));
        }

        /* access modifiers changed from: package-private */
        public IAudioService getIAudioService() {
            return IAudioService.Stub.asInterface(ServiceManager.getService("audio"));
        }

        /* access modifiers changed from: package-private */
        public boolean isBuildDebuggable() {
            return Build.IS_DEBUGGABLE;
        }

        /* access modifiers changed from: package-private */
        public LockPatternUtils newLockPatternUtils() {
            return new LockPatternUtils(this.mContext);
        }

        /* access modifiers changed from: package-private */
        public boolean storageManagerIsFileBasedEncryptionEnabled() {
            return StorageManager.isFileEncryptedNativeOnly();
        }

        /* access modifiers changed from: package-private */
        public boolean storageManagerIsNonDefaultBlockEncrypted() {
            long identity = Binder.clearCallingIdentity();
            try {
                return StorageManager.isNonDefaultBlockEncrypted();
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }

        /* access modifiers changed from: package-private */
        public boolean storageManagerIsEncrypted() {
            return StorageManager.isEncrypted();
        }

        /* access modifiers changed from: package-private */
        public boolean storageManagerIsEncryptable() {
            return StorageManager.isEncryptable();
        }

        /* access modifiers changed from: package-private */
        public Looper getMyLooper() {
            return Looper.myLooper();
        }

        /* access modifiers changed from: package-private */
        public WifiManager getWifiManager() {
            return (WifiManager) this.mContext.getSystemService(WifiManager.class);
        }

        /* access modifiers changed from: package-private */
        public long binderClearCallingIdentity() {
            return Binder.clearCallingIdentity();
        }

        /* access modifiers changed from: package-private */
        public void binderRestoreCallingIdentity(long token) {
            Binder.restoreCallingIdentity(token);
        }

        /* access modifiers changed from: package-private */
        public int binderGetCallingUid() {
            return Binder.getCallingUid();
        }

        /* access modifiers changed from: package-private */
        public int binderGetCallingPid() {
            return Binder.getCallingPid();
        }

        /* access modifiers changed from: package-private */
        public UserHandle binderGetCallingUserHandle() {
            return Binder.getCallingUserHandle();
        }

        /* access modifiers changed from: package-private */
        public boolean binderIsCallingUidMyUid() {
            return Binder.getCallingUid() == Process.myUid();
        }

        /* access modifiers changed from: package-private */
        public void binderWithCleanCallingIdentity(FunctionalUtils.ThrowingRunnable action) {
            Binder.withCleanCallingIdentity(action);
        }

        /* access modifiers changed from: package-private */
        public final int userHandleGetCallingUserId() {
            return UserHandle.getUserId(binderGetCallingUid());
        }

        /* access modifiers changed from: package-private */
        public File environmentGetUserSystemDirectory(int userId) {
            return Environment.getUserSystemDirectory(userId);
        }

        /* access modifiers changed from: package-private */
        public void powerManagerGoToSleep(long time, int reason, int flags) {
            ((PowerManager) this.mContext.getSystemService(PowerManager.class)).goToSleep(time, reason, flags);
        }

        /* access modifiers changed from: package-private */
        public void powerManagerReboot(String reason) {
            ((PowerManager) this.mContext.getSystemService(PowerManager.class)).reboot(reason);
        }

        /* access modifiers changed from: package-private */
        public void recoverySystemRebootWipeUserData(boolean shutdown, String reason, boolean force, boolean wipeEuicc) throws IOException {
            RecoverySystem.rebootWipeUserData(this.mContext, shutdown, reason, force, wipeEuicc);
        }

        /* access modifiers changed from: package-private */
        public boolean systemPropertiesGetBoolean(String key, boolean def) {
            return SystemProperties.getBoolean(key, def);
        }

        /* access modifiers changed from: package-private */
        public long systemPropertiesGetLong(String key, long def) {
            return SystemProperties.getLong(key, def);
        }

        /* access modifiers changed from: package-private */
        public String systemPropertiesGet(String key, String def) {
            return SystemProperties.get(key, def);
        }

        /* access modifiers changed from: package-private */
        public String systemPropertiesGet(String key) {
            return SystemProperties.get(key);
        }

        /* access modifiers changed from: package-private */
        public void systemPropertiesSet(String key, String value) {
            SystemProperties.set(key, value);
        }

        /* access modifiers changed from: package-private */
        public boolean userManagerIsSplitSystemUser() {
            return UserManager.isSplitSystemUser();
        }

        /* access modifiers changed from: package-private */
        public String getDevicePolicyFilePathForSystemUser() {
            return "/data/system/";
        }

        /* access modifiers changed from: package-private */
        public PendingIntent pendingIntentGetActivityAsUser(Context context, int requestCode, Intent intent, int flags, Bundle options, UserHandle user) {
            return PendingIntent.getActivityAsUser(context, requestCode, intent, flags, options, user);
        }

        /* access modifiers changed from: package-private */
        public void registerContentObserver(Uri uri, boolean notifyForDescendents, ContentObserver observer, int userHandle) {
            this.mContext.getContentResolver().registerContentObserver(uri, notifyForDescendents, observer, userHandle);
        }

        /* access modifiers changed from: package-private */
        public int settingsSecureGetIntForUser(String name, int def, int userHandle) {
            return Settings.Secure.getIntForUser(this.mContext.getContentResolver(), name, def, userHandle);
        }

        /* access modifiers changed from: package-private */
        public String settingsSecureGetStringForUser(String name, int userHandle) {
            return Settings.Secure.getStringForUser(this.mContext.getContentResolver(), name, userHandle);
        }

        /* access modifiers changed from: package-private */
        public void settingsSecurePutIntForUser(String name, int value, int userHandle) {
            Settings.Secure.putIntForUser(this.mContext.getContentResolver(), name, value, userHandle);
        }

        /* access modifiers changed from: package-private */
        public void settingsSecurePutStringForUser(String name, String value, int userHandle) {
            Settings.Secure.putStringForUser(this.mContext.getContentResolver(), name, value, userHandle);
        }

        /* access modifiers changed from: package-private */
        public void settingsGlobalPutStringForUser(String name, String value, int userHandle) {
            Settings.Global.putStringForUser(this.mContext.getContentResolver(), name, value, userHandle);
        }

        /* access modifiers changed from: package-private */
        public void settingsSecurePutInt(String name, int value) {
            Settings.Secure.putInt(this.mContext.getContentResolver(), name, value);
        }

        /* access modifiers changed from: package-private */
        public int settingsGlobalGetInt(String name, int def) {
            return Settings.Global.getInt(this.mContext.getContentResolver(), name, def);
        }

        /* access modifiers changed from: package-private */
        public String settingsGlobalGetString(String name) {
            return Settings.Global.getString(this.mContext.getContentResolver(), name);
        }

        /* access modifiers changed from: package-private */
        public void settingsGlobalPutInt(String name, int value) {
            Settings.Global.putInt(this.mContext.getContentResolver(), name, value);
        }

        /* access modifiers changed from: package-private */
        public void settingsSecurePutString(String name, String value) {
            Settings.Secure.putString(this.mContext.getContentResolver(), name, value);
        }

        /* access modifiers changed from: package-private */
        public void settingsGlobalPutString(String name, String value) {
            Settings.Global.putString(this.mContext.getContentResolver(), name, value);
        }

        /* access modifiers changed from: package-private */
        public void settingsSystemPutStringForUser(String name, String value, int userId) {
            Settings.System.putStringForUser(this.mContext.getContentResolver(), name, value, userId);
        }

        /* access modifiers changed from: package-private */
        public void securityLogSetLoggingEnabledProperty(boolean enabled) {
            SecurityLog.setLoggingEnabledProperty(enabled);
        }

        /* access modifiers changed from: package-private */
        public boolean securityLogGetLoggingEnabledProperty() {
            return SecurityLog.getLoggingEnabledProperty();
        }

        /* access modifiers changed from: package-private */
        public boolean securityLogIsLoggingEnabled() {
            return SecurityLog.isLoggingEnabled();
        }

        /* access modifiers changed from: package-private */
        public KeyChain.KeyChainConnection keyChainBindAsUser(UserHandle user) throws InterruptedException {
            return KeyChain.bindAsUser(this.mContext, user);
        }

        /* access modifiers changed from: package-private */
        public void postOnSystemServerInitThreadPool(Runnable runnable) {
            SystemServerInitThreadPool.get().submit(runnable, DevicePolicyManagerService.LOG_TAG);
        }

        public TransferOwnershipMetadataManager newTransferOwnershipMetadataManager() {
            return new TransferOwnershipMetadataManager();
        }

        public void runCryptoSelfTest() {
            CryptoTestHelper.runAndLogSelfTest();
        }
    }

    public DevicePolicyManagerService(Context context) {
        this(new Injector(context));
    }

    @VisibleForTesting
    DevicePolicyManagerService(Injector injector) {
        this.mPolicyCache = new DevicePolicyCacheImpl();
        this.mPackagesToRemove = new ArraySet();
        this.mToken = new Binder();
        this.mRemoteBugreportServiceIsActive = new AtomicBoolean();
        this.mRemoteBugreportSharingAccepted = new AtomicBoolean();
        this.mStatLogger = new StatLogger(new String[]{"LockGuard.guard()"});
        this.mLockDoNoUseDirectly = LockGuard.installNewLock(7, true);
        this.mRemoteBugreportTimeoutRunnable = new Runnable() {
            public void run() {
                if (DevicePolicyManagerService.this.mRemoteBugreportServiceIsActive.get()) {
                    DevicePolicyManagerService.this.onBugreportFailed();
                }
            }
        };
        this.mRemoteBugreportFinishedReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if ("android.intent.action.REMOTE_BUGREPORT_DISPATCH".equals(intent.getAction()) && DevicePolicyManagerService.this.mRemoteBugreportServiceIsActive.get()) {
                    DevicePolicyManagerService.this.onBugreportFinished(intent);
                }
            }
        };
        this.mRemoteBugreportConsentReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                DevicePolicyManagerService.this.mInjector.getNotificationManager().cancel(DevicePolicyManagerService.LOG_TAG, 678432343);
                if ("com.android.server.action.REMOTE_BUGREPORT_SHARING_ACCEPTED".equals(action)) {
                    DevicePolicyManagerService.this.onBugreportSharingAccepted();
                } else if ("com.android.server.action.REMOTE_BUGREPORT_SHARING_DECLINED".equals(action)) {
                    DevicePolicyManagerService.this.onBugreportSharingDeclined();
                }
                DevicePolicyManagerService.this.mContext.unregisterReceiver(DevicePolicyManagerService.this.mRemoteBugreportConsentReceiver);
            }
        };
        this.mUserData = new SparseArray<>();
        this.mUserPasswordMetrics = new SparseArray<>();
        this.mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                final int userHandle = intent.getIntExtra("android.intent.extra.user_handle", getSendingUserId());
                if ("android.intent.action.USER_STARTED".equals(action) && userHandle == DevicePolicyManagerService.this.mOwners.getDeviceOwnerUserId()) {
                    synchronized (DevicePolicyManagerService.this.getLockObject()) {
                        if (DevicePolicyManagerService.this.isNetworkLoggingEnabledInternalLocked()) {
                            DevicePolicyManagerService.this.setNetworkLoggingActiveInternal(true);
                        }
                    }
                }
                if ("android.intent.action.BOOT_COMPLETED".equals(action) && userHandle == DevicePolicyManagerService.this.mOwners.getDeviceOwnerUserId() && DevicePolicyManagerService.this.getDeviceOwnerRemoteBugreportUri() != null) {
                    IntentFilter filterConsent = new IntentFilter();
                    filterConsent.addAction("com.android.server.action.REMOTE_BUGREPORT_SHARING_DECLINED");
                    filterConsent.addAction("com.android.server.action.REMOTE_BUGREPORT_SHARING_ACCEPTED");
                    DevicePolicyManagerService.this.mContext.registerReceiver(DevicePolicyManagerService.this.mRemoteBugreportConsentReceiver, filterConsent);
                    DevicePolicyManagerService.this.mInjector.getNotificationManager().notifyAsUser(DevicePolicyManagerService.LOG_TAG, 678432343, RemoteBugreportUtils.buildNotification(DevicePolicyManagerService.this.mContext, 3), UserHandle.ALL);
                }
                if ("android.intent.action.BOOT_COMPLETED".equals(action) || DevicePolicyManagerService.ACTION_EXPIRED_PASSWORD_NOTIFICATION.equals(action)) {
                    DevicePolicyManagerService.this.mHandler.post(new Runnable() {
                        public void run() {
                            DevicePolicyManagerService.this.handlePasswordExpirationNotification(userHandle);
                        }
                    });
                }
                if ("android.intent.action.USER_ADDED".equals(action)) {
                    sendDeviceOwnerUserCommand("android.app.action.USER_ADDED", userHandle);
                    synchronized (DevicePolicyManagerService.this.getLockObject()) {
                        DevicePolicyManagerService.this.maybePauseDeviceWideLoggingLocked();
                    }
                } else if ("android.intent.action.USER_REMOVED".equals(action)) {
                    sendDeviceOwnerUserCommand("android.app.action.USER_REMOVED", userHandle);
                    synchronized (DevicePolicyManagerService.this.getLockObject()) {
                        boolean isRemovedUserAffiliated = DevicePolicyManagerService.this.isUserAffiliatedWithDeviceLocked(userHandle);
                        DevicePolicyManagerService.this.removeUserData(userHandle);
                        if (!isRemovedUserAffiliated) {
                            DevicePolicyManagerService.this.discardDeviceWideLogsLocked();
                            DevicePolicyManagerService.this.maybeResumeDeviceWideLoggingLocked();
                        }
                    }
                } else if ("android.intent.action.USER_STARTED".equals(action)) {
                    sendDeviceOwnerUserCommand("android.app.action.USER_STARTED", userHandle);
                    synchronized (DevicePolicyManagerService.this.getLockObject()) {
                        DevicePolicyManagerService.this.maybeSendAdminEnabledBroadcastLocked(userHandle);
                        DevicePolicyManagerService.this.mUserData.remove(userHandle);
                    }
                    DevicePolicyManagerService.this.handlePackagesChanged((String) null, userHandle);
                } else if ("android.intent.action.USER_STOPPED".equals(action)) {
                    sendDeviceOwnerUserCommand("android.app.action.USER_STOPPED", userHandle);
                } else if ("android.intent.action.USER_SWITCHED".equals(action)) {
                    sendDeviceOwnerUserCommand("android.app.action.USER_SWITCHED", userHandle);
                } else if ("android.intent.action.USER_UNLOCKED".equals(action)) {
                    synchronized (DevicePolicyManagerService.this.getLockObject()) {
                        DevicePolicyManagerService.this.maybeSendAdminEnabledBroadcastLocked(userHandle);
                    }
                } else if ("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE".equals(action)) {
                    DevicePolicyManagerService.this.handlePackagesChanged((String) null, userHandle);
                } else if ("android.intent.action.PACKAGE_CHANGED".equals(action) || ("android.intent.action.PACKAGE_ADDED".equals(action) && intent.getBooleanExtra("android.intent.extra.REPLACING", false))) {
                    DevicePolicyManagerService.this.handlePackagesChanged(intent.getData().getSchemeSpecificPart(), userHandle);
                } else if ("android.intent.action.PACKAGE_REMOVED".equals(action) && !intent.getBooleanExtra("android.intent.extra.REPLACING", false)) {
                    DevicePolicyManagerService.this.handlePackagesChanged(intent.getData().getSchemeSpecificPart(), userHandle);
                } else if ("android.intent.action.MANAGED_PROFILE_ADDED".equals(action)) {
                    DevicePolicyManagerService.this.clearWipeProfileNotification();
                } else if ("android.intent.action.DATE_CHANGED".equals(action) || "android.intent.action.TIME_SET".equals(action)) {
                    DevicePolicyManagerService.this.updateSystemUpdateFreezePeriodsRecord(true);
                }
            }

            private void sendDeviceOwnerUserCommand(String action, int userHandle) {
                synchronized (DevicePolicyManagerService.this.getLockObject()) {
                    ActiveAdmin deviceOwner = DevicePolicyManagerService.this.getDeviceOwnerAdminLocked();
                    if (deviceOwner != null) {
                        Bundle extras = new Bundle();
                        extras.putParcelable("android.intent.extra.USER", UserHandle.of(userHandle));
                        DevicePolicyManagerService.this.sendAdminCommandLocked(deviceOwner, action, extras, (BroadcastReceiver) null, true);
                    }
                }
            }
        };
        this.mInjector = injector;
        this.mContext = (Context) Preconditions.checkNotNull(injector.mContext);
        this.mHandler = new Handler((Looper) Preconditions.checkNotNull(injector.getMyLooper()));
        this.mConstantsObserver = new DevicePolicyConstantsObserver(this.mHandler);
        this.mConstantsObserver.register();
        this.mConstants = loadConstants();
        this.mOwners = (Owners) Preconditions.checkNotNull(injector.newOwners());
        this.mUserManager = (UserManager) Preconditions.checkNotNull(injector.getUserManager());
        this.mUserManagerInternal = (UserManagerInternal) Preconditions.checkNotNull(injector.getUserManagerInternal());
        this.mUsageStatsManagerInternal = (UsageStatsManagerInternal) Preconditions.checkNotNull(injector.getUsageStatsManagerInternal());
        this.mIPackageManager = (IPackageManager) Preconditions.checkNotNull(injector.getIPackageManager());
        this.mTelephonyManager = (TelephonyManager) Preconditions.checkNotNull(injector.getTelephonyManager());
        this.mLocalService = new LocalService();
        this.mLockPatternUtils = injector.newLockPatternUtils();
        this.mSecurityLogMonitor = new SecurityLogMonitor(this);
        this.mHasFeature = this.mInjector.hasFeature();
        this.mIsWatch = this.mInjector.getPackageManager().hasSystemFeature("android.hardware.type.watch");
        this.mHasTelephonyFeature = this.mInjector.getPackageManager().hasSystemFeature("android.hardware.telephony");
        this.mBackgroundHandler = BackgroundThread.getHandler();
        this.mCertificateMonitor = new CertificateMonitor(this, this.mInjector, this.mBackgroundHandler);
        this.mDeviceAdminServiceController = new DeviceAdminServiceController(this, this.mConstants);
        this.mOverlayPackagesProvider = new OverlayPackagesProvider(this.mContext);
        this.mTransferOwnershipMetadataManager = this.mInjector.newTransferOwnershipMetadataManager();
        if (!this.mHasFeature) {
            this.mSetupContentObserver = null;
            return;
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.BOOT_COMPLETED");
        filter.addAction(ACTION_EXPIRED_PASSWORD_NOTIFICATION);
        filter.addAction("android.intent.action.USER_ADDED");
        filter.addAction("android.intent.action.USER_REMOVED");
        filter.addAction("android.intent.action.USER_STARTED");
        filter.addAction("android.intent.action.USER_STOPPED");
        filter.addAction("android.intent.action.USER_SWITCHED");
        filter.addAction("android.intent.action.USER_UNLOCKED");
        filter.setPriority(1000);
        this.mContext.registerReceiverAsUser(this.mReceiver, UserHandle.ALL, filter, (String) null, this.mHandler);
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction("android.intent.action.PACKAGE_CHANGED");
        filter2.addAction("android.intent.action.PACKAGE_REMOVED");
        filter2.addAction("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE");
        filter2.addAction("android.intent.action.PACKAGE_ADDED");
        filter2.addDataScheme(com.android.server.pm.Settings.ATTR_PACKAGE);
        this.mContext.registerReceiverAsUser(this.mReceiver, UserHandle.ALL, filter2, (String) null, this.mHandler);
        IntentFilter filter3 = new IntentFilter();
        filter3.addAction("android.intent.action.MANAGED_PROFILE_ADDED");
        filter3.addAction("android.intent.action.TIME_SET");
        filter3.addAction("android.intent.action.DATE_CHANGED");
        this.mContext.registerReceiverAsUser(this.mReceiver, UserHandle.ALL, filter3, (String) null, this.mHandler);
        LocalServices.addService(DevicePolicyManagerInternal.class, this.mLocalService);
        this.mSetupContentObserver = new SetupContentObserver(this.mHandler);
        this.mUserManagerInternal.addUserRestrictionsListener(new RestrictionsListener(this.mContext));
    }

    /* access modifiers changed from: package-private */
    public DevicePolicyData getUserData(int userHandle) {
        DevicePolicyData policy;
        synchronized (getLockObject()) {
            policy = this.mUserData.get(userHandle);
            if (policy == null) {
                policy = new DevicePolicyData(userHandle);
                this.mUserData.append(userHandle, policy);
                loadSettingsLocked(policy, userHandle);
            }
        }
        return policy;
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"getLockObject()"})
    public PasswordMetrics getUserPasswordMetricsLocked(int userHandle) {
        return this.mUserPasswordMetrics.get(userHandle);
    }

    /* access modifiers changed from: package-private */
    public DevicePolicyData getUserDataUnchecked(int userHandle) {
        long ident = this.mInjector.binderClearCallingIdentity();
        try {
            return getUserData(userHandle);
        } finally {
            this.mInjector.binderRestoreCallingIdentity(ident);
        }
    }

    /* access modifiers changed from: package-private */
    public void removeUserData(int userHandle) {
        synchronized (getLockObject()) {
            if (userHandle == 0) {
                Slog.w(LOG_TAG, "Tried to remove device policy file for user 0! Ignoring.");
                return;
            }
            updatePasswordQualityCacheForUserGroup(userHandle);
            this.mPolicyCache.onUserRemoved(userHandle);
            this.mOwners.removeProfileOwner(userHandle);
            this.mOwners.writeProfileOwner(userHandle);
            if (this.mUserData.get(userHandle) != null) {
                this.mUserData.remove(userHandle);
            }
            if (this.mUserPasswordMetrics.get(userHandle) != null) {
                this.mUserPasswordMetrics.remove(userHandle);
            }
            File policyFile = new File(this.mInjector.environmentGetUserSystemDirectory(userHandle), DEVICE_POLICIES_XML);
            policyFile.delete();
            Slog.i(LOG_TAG, "Removed device policy file " + policyFile.getAbsolutePath());
        }
    }

    /* access modifiers changed from: package-private */
    public void loadOwners() {
        synchronized (getLockObject()) {
            this.mOwners.load();
            setDeviceOwnerSystemPropertyLocked();
            findOwnerComponentIfNecessaryLocked();
            migrateUserRestrictionsIfNecessaryLocked();
            maybeSetDefaultDeviceOwnerUserRestrictionsLocked();
            updateDeviceOwnerLocked();
        }
    }

    private void maybeSetDefaultDeviceOwnerUserRestrictionsLocked() {
        ActiveAdmin deviceOwner = getDeviceOwnerAdminLocked();
        if (deviceOwner != null) {
            maybeSetDefaultRestrictionsForAdminLocked(this.mOwners.getDeviceOwnerUserId(), deviceOwner, UserRestrictionsUtils.getDefaultEnabledForDeviceOwner());
        }
    }

    private void maybeSetDefaultProfileOwnerUserRestrictions() {
        synchronized (getLockObject()) {
            for (Integer intValue : this.mOwners.getProfileOwnerKeys()) {
                int userId = intValue.intValue();
                ActiveAdmin profileOwner = getProfileOwnerAdminLocked(userId);
                if (profileOwner != null) {
                    if (this.mUserManager.isManagedProfile(userId)) {
                        maybeSetDefaultRestrictionsForAdminLocked(userId, profileOwner, UserRestrictionsUtils.getDefaultEnabledForManagedProfiles());
                        ensureUnknownSourcesRestrictionForProfileOwnerLocked(userId, profileOwner, false);
                    }
                }
            }
        }
    }

    private void ensureUnknownSourcesRestrictionForProfileOwnerLocked(int userId, ActiveAdmin profileOwner, boolean newOwner) {
        if (newOwner || this.mInjector.settingsSecureGetIntForUser("unknown_sources_default_reversed", 0, userId) != 0) {
            profileOwner.ensureUserRestrictions().putBoolean("no_install_unknown_sources", true);
            saveUserRestrictionsLocked(userId);
            this.mInjector.settingsSecurePutIntForUser("unknown_sources_default_reversed", 0, userId);
        }
    }

    private void maybeSetDefaultRestrictionsForAdminLocked(int userId, ActiveAdmin admin, Set<String> defaultRestrictions) {
        if (!defaultRestrictions.equals(admin.defaultEnabledRestrictionsAlreadySet)) {
            Slog.i(LOG_TAG, "New user restrictions need to be set by default for user " + userId);
            Set<String> restrictionsToSet = new ArraySet<>(defaultRestrictions);
            restrictionsToSet.removeAll(admin.defaultEnabledRestrictionsAlreadySet);
            if (!restrictionsToSet.isEmpty()) {
                for (String restriction : restrictionsToSet) {
                    admin.ensureUserRestrictions().putBoolean(restriction, true);
                }
                admin.defaultEnabledRestrictionsAlreadySet.addAll(restrictionsToSet);
                Slog.i(LOG_TAG, "Enabled the following restrictions by default: " + restrictionsToSet);
                saveUserRestrictionsLocked(userId);
            }
        }
    }

    /* access modifiers changed from: private */
    public void setDeviceOwnerSystemPropertyLocked() {
        boolean z = false;
        if (this.mInjector.settingsGlobalGetInt("device_provisioned", 0) != 0) {
            z = true;
        }
        boolean deviceProvisioned = z;
        boolean hasDeviceOwner = this.mOwners.hasDeviceOwner();
        if ((!hasDeviceOwner && !deviceProvisioned) || StorageManager.inCryptKeeperBounce()) {
            return;
        }
        if (!this.mInjector.systemPropertiesGet(PROPERTY_DEVICE_OWNER_PRESENT, "").isEmpty()) {
            Slog.w(LOG_TAG, "Trying to set ro.device_owner, but it has already been set?");
            return;
        }
        String value = Boolean.toString(hasDeviceOwner);
        this.mInjector.systemPropertiesSet(PROPERTY_DEVICE_OWNER_PRESENT, value);
        Slog.i(LOG_TAG, "Set ro.device_owner property to " + value);
    }

    private void maybeStartSecurityLogMonitorOnActivityManagerReady() {
        synchronized (getLockObject()) {
            if (this.mInjector.securityLogIsLoggingEnabled()) {
                this.mSecurityLogMonitor.start();
                this.mInjector.runCryptoSelfTest();
                maybePauseDeviceWideLoggingLocked();
            }
        }
    }

    private void findOwnerComponentIfNecessaryLocked() {
        if (this.mOwners.hasDeviceOwner()) {
            ComponentName doComponentName = this.mOwners.getDeviceOwnerComponent();
            if (TextUtils.isEmpty(doComponentName.getClassName())) {
                ComponentName doComponent = findAdminComponentWithPackageLocked(doComponentName.getPackageName(), this.mOwners.getDeviceOwnerUserId());
                if (doComponent == null) {
                    Slog.e(LOG_TAG, "Device-owner isn't registered as device-admin");
                    return;
                }
                Owners owners = this.mOwners;
                owners.setDeviceOwnerWithRestrictionsMigrated(doComponent, owners.getDeviceOwnerName(), this.mOwners.getDeviceOwnerUserId(), !this.mOwners.getDeviceOwnerUserRestrictionsNeedsMigration());
                this.mOwners.writeDeviceOwner();
            }
        }
    }

    private void migrateUserRestrictionsIfNecessaryLocked() {
        if (this.mOwners.getDeviceOwnerUserRestrictionsNeedsMigration()) {
            migrateUserRestrictionsForUser(UserHandle.SYSTEM, getDeviceOwnerAdminLocked(), (Set<String>) null, true);
            pushUserRestrictions(0);
            this.mOwners.setDeviceOwnerUserRestrictionsMigrated();
        }
        Set<String> secondaryUserExceptionList = Sets.newArraySet(new String[]{"no_outgoing_calls", "no_sms"});
        for (UserInfo ui : this.mUserManager.getUsers()) {
            int userId = ui.id;
            if (this.mOwners.getProfileOwnerUserRestrictionsNeedsMigration(userId)) {
                migrateUserRestrictionsForUser(ui.getUserHandle(), getProfileOwnerAdminLocked(userId), userId == 0 ? null : secondaryUserExceptionList, false);
                pushUserRestrictions(userId);
                this.mOwners.setProfileOwnerUserRestrictionsMigrated(userId);
            }
        }
    }

    private void migrateUserRestrictionsForUser(UserHandle user, ActiveAdmin admin, Set<String> exceptionList, boolean isDeviceOwner) {
        boolean canOwnerChange;
        Bundle origRestrictions = this.mUserManagerInternal.getBaseUserRestrictions(user.getIdentifier());
        Bundle newBaseRestrictions = new Bundle();
        Bundle newOwnerRestrictions = new Bundle();
        for (String key : origRestrictions.keySet()) {
            if (origRestrictions.getBoolean(key)) {
                if (isDeviceOwner) {
                    canOwnerChange = UserRestrictionsUtils.canDeviceOwnerChange(key);
                } else {
                    canOwnerChange = UserRestrictionsUtils.canProfileOwnerChange(key, user.getIdentifier());
                }
                if (!canOwnerChange || (exceptionList != null && exceptionList.contains(key))) {
                    newBaseRestrictions.putBoolean(key, true);
                } else {
                    newOwnerRestrictions.putBoolean(key, true);
                }
            }
        }
        this.mUserManagerInternal.setBaseUserRestrictionsByDpmsForMigration(user.getIdentifier(), newBaseRestrictions);
        if (admin != null) {
            admin.ensureUserRestrictions().clear();
            admin.ensureUserRestrictions().putAll(newOwnerRestrictions);
        } else {
            Slog.w(LOG_TAG, "ActiveAdmin for DO/PO not found. user=" + user.getIdentifier());
        }
        saveSettingsLocked(user.getIdentifier());
    }

    private ComponentName findAdminComponentWithPackageLocked(String packageName, int userId) {
        DevicePolicyData policy = getUserData(userId);
        int n = policy.mAdminList.size();
        ComponentName found = null;
        int nFound = 0;
        for (int i = 0; i < n; i++) {
            ActiveAdmin admin = policy.mAdminList.get(i);
            if (packageName.equals(admin.info.getPackageName())) {
                if (nFound == 0) {
                    found = admin.info.getComponent();
                }
                nFound++;
            }
        }
        if (nFound > 1) {
            Slog.w(LOG_TAG, "Multiple DA found; assume the first one is DO.");
        }
        return found;
    }

    private void setExpirationAlarmCheckLocked(Context context, int userHandle, boolean parent) {
        long alarmInterval;
        int affectedUserHandle;
        AlarmManager am;
        int i = userHandle;
        boolean z = parent;
        long expiration = getPasswordExpirationLocked((ComponentName) null, i, z);
        long now = System.currentTimeMillis();
        long timeToExpire = expiration - now;
        if (expiration == 0) {
            alarmInterval = 0;
        } else if (timeToExpire <= 0) {
            alarmInterval = MS_PER_DAY + now;
        } else {
            long alarmInterval2 = timeToExpire % MS_PER_DAY;
            if (alarmInterval2 == 0) {
                alarmInterval2 = MS_PER_DAY;
            }
            alarmInterval = alarmInterval2 + now;
        }
        long token = this.mInjector.binderClearCallingIdentity();
        if (z) {
            try {
                affectedUserHandle = getProfileParentId(i);
            } catch (Throwable th) {
                th = th;
                Context context2 = context;
                long j = expiration;
                this.mInjector.binderRestoreCallingIdentity(token);
                throw th;
            }
        } else {
            affectedUserHandle = i;
        }
        try {
            am = this.mInjector.getAlarmManager();
            int i2 = affectedUserHandle;
            long j2 = expiration;
        } catch (Throwable th2) {
            th = th2;
            Context context3 = context;
            long j3 = expiration;
            this.mInjector.binderRestoreCallingIdentity(token);
            throw th;
        }
        try {
            PendingIntent pi = PendingIntent.getBroadcastAsUser(context, REQUEST_EXPIRE_PASSWORD, new Intent(ACTION_EXPIRED_PASSWORD_NOTIFICATION), 1207959552, UserHandle.of(affectedUserHandle));
            am.cancel(pi);
            if (alarmInterval != 0) {
                am.set(1, alarmInterval, pi);
            }
            this.mInjector.binderRestoreCallingIdentity(token);
        } catch (Throwable th3) {
            th = th3;
            this.mInjector.binderRestoreCallingIdentity(token);
            throw th;
        }
    }

    /* access modifiers changed from: package-private */
    public ActiveAdmin getActiveAdminUncheckedLocked(ComponentName who, int userHandle) {
        ensureLocked();
        ActiveAdmin admin = getUserData(userHandle).mAdminMap.get(who);
        if (admin == null || !who.getPackageName().equals(admin.info.getActivityInfo().packageName) || !who.getClassName().equals(admin.info.getActivityInfo().name)) {
            return null;
        }
        return admin;
    }

    /* access modifiers changed from: package-private */
    public ActiveAdmin getActiveAdminUncheckedLocked(ComponentName who, int userHandle, boolean parent) {
        ensureLocked();
        if (parent) {
            enforceManagedProfile(userHandle, "call APIs on the parent profile");
        }
        ActiveAdmin admin = getActiveAdminUncheckedLocked(who, userHandle);
        if (admin == null || !parent) {
            return admin;
        }
        return admin.getParentActiveAdmin();
    }

    /* access modifiers changed from: package-private */
    public ActiveAdmin getActiveAdminForCallerLocked(ComponentName who, int reqPolicy) throws SecurityException {
        return getActiveAdminOrCheckPermissionForCallerLocked(who, reqPolicy, (String) null);
    }

    /* access modifiers changed from: package-private */
    public ActiveAdmin getActiveAdminOrCheckPermissionForCallerLocked(ComponentName who, int reqPolicy, String permission) throws SecurityException {
        ensureLocked();
        int callingUid = this.mInjector.binderGetCallingUid();
        ActiveAdmin result = getActiveAdminWithPolicyForUidLocked(who, reqPolicy, callingUid);
        if (result != null) {
            return result;
        }
        if (permission != null && this.mContext.checkCallingPermission(permission) == 0) {
            return null;
        }
        if (who != null) {
            int userId = UserHandle.getUserId(callingUid);
            ActiveAdmin admin = getUserData(userId).mAdminMap.get(who);
            boolean isDeviceOwner = isDeviceOwner(admin.info.getComponent(), userId);
            boolean isProfileOwner = isProfileOwner(admin.info.getComponent(), userId);
            if (reqPolicy == -2) {
                throw new SecurityException("Admin " + admin.info.getComponent() + " does not own the device");
            } else if (reqPolicy == -1) {
                throw new SecurityException("Admin " + admin.info.getComponent() + " does not own the profile");
            } else if (!DA_DISALLOWED_POLICIES.contains(Integer.valueOf(reqPolicy)) || isDeviceOwner || isProfileOwner) {
                throw new SecurityException("Admin " + admin.info.getComponent() + " did not specify uses-policy for: " + admin.info.getTagForPolicy(reqPolicy));
            } else {
                throw new SecurityException("Admin " + admin.info.getComponent() + " is not a device owner or profile owner, so may not use policy: " + admin.info.getTagForPolicy(reqPolicy));
            }
        } else {
            throw new SecurityException("No active admin owned by uid " + callingUid + " for policy #" + reqPolicy);
        }
    }

    /* access modifiers changed from: package-private */
    public ActiveAdmin getActiveAdminForCallerLocked(ComponentName who, int reqPolicy, boolean parent) throws SecurityException {
        return getActiveAdminOrCheckPermissionForCallerLocked(who, reqPolicy, parent, (String) null);
    }

    /* access modifiers changed from: package-private */
    public ActiveAdmin getActiveAdminOrCheckPermissionForCallerLocked(ComponentName who, int reqPolicy, boolean parent, String permission) throws SecurityException {
        ensureLocked();
        if (parent) {
            enforceManagedProfile(this.mInjector.userHandleGetCallingUserId(), "call APIs on the parent profile");
        }
        ActiveAdmin admin = getActiveAdminOrCheckPermissionForCallerLocked(who, reqPolicy, permission);
        return parent ? admin.getParentActiveAdmin() : admin;
    }

    private ActiveAdmin getActiveAdminForUidLocked(ComponentName who, int uid) {
        ensureLocked();
        ActiveAdmin admin = getUserData(UserHandle.getUserId(uid)).mAdminMap.get(who);
        if (admin == null) {
            throw new SecurityException("No active admin " + who + " for UID " + uid);
        } else if (admin.getUid() == uid) {
            return admin;
        } else {
            throw new SecurityException("Admin " + who + " is not owned by uid " + uid);
        }
    }

    /* access modifiers changed from: private */
    public ActiveAdmin getActiveAdminWithPolicyForUidLocked(ComponentName who, int reqPolicy, int uid) {
        ensureLocked();
        int userId = UserHandle.getUserId(uid);
        DevicePolicyData policy = getUserData(userId);
        if (who != null) {
            ActiveAdmin admin = policy.mAdminMap.get(who);
            if (admin == null) {
                throw new SecurityException("No active admin " + who);
            } else if (admin.getUid() != uid) {
                throw new SecurityException("Admin " + who + " is not owned by uid " + uid);
            } else if (isActiveAdminWithPolicyForUserLocked(admin, reqPolicy, userId)) {
                return admin;
            } else {
                return null;
            }
        } else {
            Iterator<ActiveAdmin> it = policy.mAdminList.iterator();
            while (it.hasNext()) {
                ActiveAdmin admin2 = it.next();
                if (admin2.getUid() == uid && isActiveAdminWithPolicyForUserLocked(admin2, reqPolicy, userId)) {
                    return admin2;
                }
            }
            return null;
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean isActiveAdminWithPolicyForUserLocked(ActiveAdmin admin, int reqPolicy, int userId) {
        ensureLocked();
        boolean ownsDevice = isDeviceOwner(admin.info.getComponent(), userId);
        boolean ownsProfile = isProfileOwner(admin.info.getComponent(), userId);
        if (reqPolicy == -2) {
            return ownsDevice;
        }
        if (reqPolicy != -1) {
            if (!(ownsDevice || ownsProfile || !DA_DISALLOWED_POLICIES.contains(Integer.valueOf(reqPolicy)) || getTargetSdk(admin.info.getPackageName(), userId) < 29) || !admin.info.usesPolicy(reqPolicy)) {
                return false;
            }
            return true;
        } else if (ownsDevice || ownsProfile) {
            return true;
        } else {
            return false;
        }
    }

    /* access modifiers changed from: package-private */
    public void sendAdminCommandLocked(ActiveAdmin admin, String action) {
        sendAdminCommandLocked(admin, action, (BroadcastReceiver) null);
    }

    /* access modifiers changed from: package-private */
    public void sendAdminCommandLocked(ActiveAdmin admin, String action, BroadcastReceiver result) {
        sendAdminCommandLocked(admin, action, (Bundle) null, result);
    }

    /* access modifiers changed from: package-private */
    public void sendAdminCommandLocked(ActiveAdmin admin, String action, Bundle adminExtras, BroadcastReceiver result) {
        sendAdminCommandLocked(admin, action, adminExtras, result, false);
    }

    /* access modifiers changed from: package-private */
    public boolean sendAdminCommandLocked(ActiveAdmin admin, String action, Bundle adminExtras, BroadcastReceiver result, boolean inForeground) {
        ActiveAdmin activeAdmin = admin;
        String str = action;
        Bundle bundle = adminExtras;
        Intent intent = new Intent(str);
        intent.setComponent(activeAdmin.info.getComponent());
        if (UserManager.isDeviceInDemoMode(this.mContext)) {
            intent.addFlags(268435456);
        }
        if (str.equals("android.app.action.ACTION_PASSWORD_EXPIRING")) {
            intent.putExtra("expiration", activeAdmin.passwordExpirationDate);
        }
        if (inForeground) {
            intent.addFlags(268435456);
        }
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        if (this.mInjector.getPackageManager().queryBroadcastReceiversAsUser(intent, 268435456, admin.getUserHandle()).isEmpty()) {
            return false;
        }
        BroadcastOptions options = BroadcastOptions.makeBasic();
        options.setBackgroundActivityStartsAllowed(true);
        if (result != null) {
            BroadcastOptions broadcastOptions = options;
            this.mContext.sendOrderedBroadcastAsUser(intent, admin.getUserHandle(), (String) null, -1, options.toBundle(), result, this.mHandler, -1, (String) null, (Bundle) null);
            return true;
        }
        this.mContext.sendBroadcastAsUser(intent, admin.getUserHandle(), (String) null, options.toBundle());
        return true;
    }

    /* access modifiers changed from: package-private */
    public void sendAdminCommandLocked(String action, int reqPolicy, int userHandle, Bundle adminExtras) {
        DevicePolicyData policy = getUserData(userHandle);
        int count = policy.mAdminList.size();
        for (int i = 0; i < count; i++) {
            ActiveAdmin admin = policy.mAdminList.get(i);
            if (admin.info.usesPolicy(reqPolicy)) {
                sendAdminCommandLocked(admin, action, adminExtras, (BroadcastReceiver) null);
            }
        }
    }

    private void sendAdminCommandToSelfAndProfilesLocked(String action, int reqPolicy, int userHandle, Bundle adminExtras) {
        for (int profileId : this.mUserManager.getProfileIdsWithDisabled(userHandle)) {
            sendAdminCommandLocked(action, reqPolicy, profileId, adminExtras);
        }
    }

    private void sendAdminCommandForLockscreenPoliciesLocked(String action, int reqPolicy, int userHandle) {
        Bundle extras = new Bundle();
        extras.putParcelable("android.intent.extra.USER", UserHandle.of(userHandle));
        if (isSeparateProfileChallengeEnabled(userHandle)) {
            sendAdminCommandLocked(action, reqPolicy, userHandle, extras);
        } else {
            sendAdminCommandToSelfAndProfilesLocked(action, reqPolicy, userHandle, extras);
        }
    }

    /* access modifiers changed from: package-private */
    public void removeActiveAdminLocked(final ComponentName adminReceiver, final int userHandle) {
        ActiveAdmin admin = getActiveAdminUncheckedLocked(adminReceiver, userHandle);
        DevicePolicyData policy = getUserData(userHandle);
        if (admin != null && !policy.mRemovingAdmins.contains(adminReceiver)) {
            policy.mRemovingAdmins.add(adminReceiver);
            sendAdminCommandLocked(admin, "android.app.action.DEVICE_ADMIN_DISABLED", new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    DevicePolicyManagerService.this.removeAdminArtifacts(adminReceiver, userHandle);
                    DevicePolicyManagerService.this.removePackageIfRequired(adminReceiver.getPackageName(), userHandle);
                }
            });
        }
    }

    public DeviceAdminInfo findAdmin(ComponentName adminName, int userHandle, boolean throwForMissingPermission) {
        if (!this.mHasFeature) {
            return null;
        }
        enforceFullCrossUsersPermission(userHandle);
        ActivityInfo ai = null;
        try {
            ai = this.mIPackageManager.getReceiverInfo(adminName, 819328, userHandle);
        } catch (RemoteException e) {
        }
        if (ai != null) {
            if (!"android.permission.BIND_DEVICE_ADMIN".equals(ai.permission)) {
                String message = "DeviceAdminReceiver " + adminName + " must be protected with " + "android.permission.BIND_DEVICE_ADMIN";
                Slog.w(LOG_TAG, message);
                if (throwForMissingPermission && ai.applicationInfo.targetSdkVersion > 23) {
                    throw new IllegalArgumentException(message);
                }
            }
            try {
                return new DeviceAdminInfo(this.mContext, ai);
            } catch (IOException | XmlPullParserException e2) {
                Slog.w(LOG_TAG, "Bad device admin requested for user=" + userHandle + ": " + adminName, e2);
                return null;
            }
        } else {
            throw new IllegalArgumentException("Unknown admin: " + adminName);
        }
    }

    private File getPolicyFileDirectory(int userId) {
        if (userId == 0) {
            return new File(this.mInjector.getDevicePolicyFilePathForSystemUser());
        }
        return this.mInjector.environmentGetUserSystemDirectory(userId);
    }

    private JournaledFile makeJournaledFile(int userId) {
        String base = new File(getPolicyFileDirectory(userId), DEVICE_POLICIES_XML).getAbsolutePath();
        File file = new File(base);
        return new JournaledFile(file, new File(base + ".tmp"));
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x0332 A[SYNTHETIC, Splitter:B:132:0x0332] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void saveSettingsLocked(int r22) {
        /*
            r21 = this;
            java.lang.String r0 = "lock-task-component"
            java.lang.String r1 = "lock-task-features"
            java.lang.String r2 = "accepted-ca-certificate"
            java.lang.String r3 = "delegation"
            java.lang.String r4 = "password-validity"
            java.lang.String r5 = "admin"
            java.lang.String r6 = "failed-password-attempts"
            java.lang.String r7 = "password-owner"
            java.lang.String r8 = "policies"
            com.android.server.devicepolicy.DevicePolicyManagerService$DevicePolicyData r9 = r21.getUserData(r22)
            com.android.internal.util.JournaledFile r10 = r21.makeJournaledFile(r22)
            r11 = 0
            java.io.FileOutputStream r12 = new java.io.FileOutputStream     // Catch:{ IOException | XmlPullParserException -> 0x0323 }
            java.io.File r13 = r10.chooseForWrite()     // Catch:{ IOException | XmlPullParserException -> 0x0323 }
            r14 = 0
            r12.<init>(r13, r14)     // Catch:{ IOException | XmlPullParserException -> 0x0323 }
            r11 = r12
            com.android.internal.util.FastXmlSerializer r12 = new com.android.internal.util.FastXmlSerializer     // Catch:{ IOException | XmlPullParserException -> 0x0323 }
            r12.<init>()     // Catch:{ IOException | XmlPullParserException -> 0x0323 }
            java.nio.charset.Charset r13 = java.nio.charset.StandardCharsets.UTF_8     // Catch:{ IOException | XmlPullParserException -> 0x0323 }
            java.lang.String r13 = r13.name()     // Catch:{ IOException | XmlPullParserException -> 0x0323 }
            r12.setOutput(r11, r13)     // Catch:{ IOException | XmlPullParserException -> 0x0323 }
            r13 = 1
            java.lang.Boolean r15 = java.lang.Boolean.valueOf(r13)     // Catch:{ IOException | XmlPullParserException -> 0x0323 }
            r14 = 0
            r12.startDocument(r14, r15)     // Catch:{ IOException | XmlPullParserException -> 0x0323 }
            r12.startTag(r14, r8)     // Catch:{ IOException | XmlPullParserException -> 0x0323 }
            android.content.ComponentName r15 = r9.mRestrictionsProvider     // Catch:{ IOException | XmlPullParserException -> 0x0323 }
            if (r15 == 0) goto L_0x005d
            java.lang.String r15 = "permission-provider"
            android.content.ComponentName r13 = r9.mRestrictionsProvider     // Catch:{ IOException | XmlPullParserException -> 0x0056 }
            java.lang.String r13 = r13.flattenToString()     // Catch:{ IOException | XmlPullParserException -> 0x0056 }
            r12.attribute(r14, r15, r13)     // Catch:{ IOException | XmlPullParserException -> 0x0056 }
            goto L_0x005d
        L_0x0056:
            r0 = move-exception
            r3 = r21
            r19 = r10
            goto L_0x0328
        L_0x005d:
            boolean r13 = r9.mUserSetupComplete     // Catch:{ IOException | XmlPullParserException -> 0x0323 }
            if (r13 == 0) goto L_0x006d
            java.lang.String r13 = "setup-complete"
            r15 = 1
            java.lang.String r14 = java.lang.Boolean.toString(r15)     // Catch:{ IOException | XmlPullParserException -> 0x0056 }
            r15 = 0
            r12.attribute(r15, r13, r14)     // Catch:{ IOException | XmlPullParserException -> 0x0056 }
        L_0x006d:
            boolean r13 = r9.mPaired     // Catch:{ IOException | XmlPullParserException -> 0x0323 }
            if (r13 == 0) goto L_0x007c
            java.lang.String r13 = "device-paired"
            r14 = 1
            java.lang.String r15 = java.lang.Boolean.toString(r14)     // Catch:{ IOException | XmlPullParserException -> 0x0056 }
            r14 = 0
            r12.attribute(r14, r13, r15)     // Catch:{ IOException | XmlPullParserException -> 0x0056 }
        L_0x007c:
            boolean r13 = r9.mDeviceProvisioningConfigApplied     // Catch:{ IOException | XmlPullParserException -> 0x0323 }
            if (r13 == 0) goto L_0x008b
            java.lang.String r13 = "device-provisioning-config-applied"
            r14 = 1
            java.lang.String r14 = java.lang.Boolean.toString(r14)     // Catch:{ IOException | XmlPullParserException -> 0x0056 }
            r15 = 0
            r12.attribute(r15, r13, r14)     // Catch:{ IOException | XmlPullParserException -> 0x0056 }
        L_0x008b:
            int r13 = r9.mUserProvisioningState     // Catch:{ IOException | XmlPullParserException -> 0x0323 }
            if (r13 == 0) goto L_0x009c
            java.lang.String r13 = "provisioning-state"
            int r14 = r9.mUserProvisioningState     // Catch:{ IOException | XmlPullParserException -> 0x0056 }
            java.lang.String r14 = java.lang.Integer.toString(r14)     // Catch:{ IOException | XmlPullParserException -> 0x0056 }
            r15 = 0
            r12.attribute(r15, r13, r14)     // Catch:{ IOException | XmlPullParserException -> 0x0056 }
        L_0x009c:
            int r13 = r9.mPermissionPolicy     // Catch:{ IOException | XmlPullParserException -> 0x0323 }
            if (r13 == 0) goto L_0x00ad
            java.lang.String r13 = "permission-policy"
            int r14 = r9.mPermissionPolicy     // Catch:{ IOException | XmlPullParserException -> 0x0056 }
            java.lang.String r14 = java.lang.Integer.toString(r14)     // Catch:{ IOException | XmlPullParserException -> 0x0056 }
            r15 = 0
            r12.attribute(r15, r13, r14)     // Catch:{ IOException | XmlPullParserException -> 0x0056 }
        L_0x00ad:
            r13 = 0
            r14 = r13
        L_0x00af:
            android.util.ArrayMap<java.lang.String, java.util.List<java.lang.String>> r13 = r9.mDelegationMap     // Catch:{ IOException | XmlPullParserException -> 0x0323 }
            int r13 = r13.size()     // Catch:{ IOException | XmlPullParserException -> 0x0323 }
            if (r14 >= r13) goto L_0x010b
            android.util.ArrayMap<java.lang.String, java.util.List<java.lang.String>> r13 = r9.mDelegationMap     // Catch:{ IOException | XmlPullParserException -> 0x0104 }
            java.lang.Object r13 = r13.keyAt(r14)     // Catch:{ IOException | XmlPullParserException -> 0x0104 }
            java.lang.String r13 = (java.lang.String) r13     // Catch:{ IOException | XmlPullParserException -> 0x0104 }
            android.util.ArrayMap<java.lang.String, java.util.List<java.lang.String>> r15 = r9.mDelegationMap     // Catch:{ IOException | XmlPullParserException -> 0x0104 }
            java.lang.Object r15 = r15.valueAt(r14)     // Catch:{ IOException | XmlPullParserException -> 0x0104 }
            java.util.List r15 = (java.util.List) r15     // Catch:{ IOException | XmlPullParserException -> 0x0104 }
            java.util.Iterator r16 = r15.iterator()     // Catch:{ IOException | XmlPullParserException -> 0x0104 }
        L_0x00cb:
            boolean r17 = r16.hasNext()     // Catch:{ IOException | XmlPullParserException -> 0x0104 }
            if (r17 == 0) goto L_0x00fb
            java.lang.Object r17 = r16.next()     // Catch:{ IOException | XmlPullParserException -> 0x0104 }
            java.lang.String r17 = (java.lang.String) r17     // Catch:{ IOException | XmlPullParserException -> 0x0104 }
            r18 = r17
            r17 = r15
            r15 = 0
            r12.startTag(r15, r3)     // Catch:{ IOException | XmlPullParserException -> 0x0104 }
            java.lang.String r15 = "delegatePackage"
            r19 = r10
            r10 = 0
            r12.attribute(r10, r15, r13)     // Catch:{ IOException | XmlPullParserException -> 0x031f }
            java.lang.String r15 = "scope"
            r20 = r13
            r13 = r18
            r12.attribute(r10, r15, r13)     // Catch:{ IOException | XmlPullParserException -> 0x031f }
            r12.endTag(r10, r3)     // Catch:{ IOException | XmlPullParserException -> 0x031f }
            r15 = r17
            r10 = r19
            r13 = r20
            goto L_0x00cb
        L_0x00fb:
            r19 = r10
            r20 = r13
            r17 = r15
            int r14 = r14 + 1
            goto L_0x00af
        L_0x0104:
            r0 = move-exception
            r19 = r10
            r3 = r21
            goto L_0x0328
        L_0x010b:
            r19 = r10
            java.util.ArrayList<com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin> r3 = r9.mAdminList     // Catch:{ IOException | XmlPullParserException -> 0x031f }
            int r3 = r3.size()     // Catch:{ IOException | XmlPullParserException -> 0x031f }
            r10 = 0
        L_0x0114:
            java.lang.String r13 = "name"
            if (r10 >= r3) goto L_0x0145
            java.util.ArrayList<com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin> r14 = r9.mAdminList     // Catch:{ IOException | XmlPullParserException -> 0x031f }
            java.lang.Object r14 = r14.get(r10)     // Catch:{ IOException | XmlPullParserException -> 0x031f }
            com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin r14 = (com.android.server.devicepolicy.DevicePolicyManagerService.ActiveAdmin) r14     // Catch:{ IOException | XmlPullParserException -> 0x031f }
            if (r14 == 0) goto L_0x013e
            r15 = 0
            r12.startTag(r15, r5)     // Catch:{ IOException | XmlPullParserException -> 0x031f }
            android.app.admin.DeviceAdminInfo r15 = r14.info     // Catch:{ IOException | XmlPullParserException -> 0x031f }
            android.content.ComponentName r15 = r15.getComponent()     // Catch:{ IOException | XmlPullParserException -> 0x031f }
            java.lang.String r15 = r15.flattenToString()     // Catch:{ IOException | XmlPullParserException -> 0x031f }
            r16 = r3
            r3 = 0
            r12.attribute(r3, r13, r15)     // Catch:{ IOException | XmlPullParserException -> 0x031f }
            r14.writeToXml(r12)     // Catch:{ IOException | XmlPullParserException -> 0x031f }
            r12.endTag(r3, r5)     // Catch:{ IOException | XmlPullParserException -> 0x031f }
            goto L_0x0140
        L_0x013e:
            r16 = r3
        L_0x0140:
            int r10 = r10 + 1
            r3 = r16
            goto L_0x0114
        L_0x0145:
            r16 = r3
            int r3 = r9.mPasswordOwner     // Catch:{ IOException | XmlPullParserException -> 0x031f }
            java.lang.String r5 = "value"
            if (r3 < 0) goto L_0x015e
            r3 = 0
            r12.startTag(r3, r7)     // Catch:{ IOException | XmlPullParserException -> 0x031f }
            int r10 = r9.mPasswordOwner     // Catch:{ IOException | XmlPullParserException -> 0x031f }
            java.lang.String r10 = java.lang.Integer.toString(r10)     // Catch:{ IOException | XmlPullParserException -> 0x031f }
            r12.attribute(r3, r5, r10)     // Catch:{ IOException | XmlPullParserException -> 0x031f }
            r12.endTag(r3, r7)     // Catch:{ IOException | XmlPullParserException -> 0x031f }
        L_0x015e:
            int r3 = r9.mFailedPasswordAttempts     // Catch:{ IOException | XmlPullParserException -> 0x031f }
            if (r3 == 0) goto L_0x0172
            r3 = 0
            r12.startTag(r3, r6)     // Catch:{ IOException | XmlPullParserException -> 0x031f }
            int r7 = r9.mFailedPasswordAttempts     // Catch:{ IOException | XmlPullParserException -> 0x031f }
            java.lang.String r7 = java.lang.Integer.toString(r7)     // Catch:{ IOException | XmlPullParserException -> 0x031f }
            r12.attribute(r3, r5, r7)     // Catch:{ IOException | XmlPullParserException -> 0x031f }
            r12.endTag(r3, r6)     // Catch:{ IOException | XmlPullParserException -> 0x031f }
        L_0x0172:
            r3 = r21
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r6 = r3.mInjector     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            boolean r6 = r6.storageManagerIsFileBasedEncryptionEnabled()     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            if (r6 != 0) goto L_0x018c
            r6 = 0
            r12.startTag(r6, r4)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            boolean r7 = r9.mPasswordValidAtLastCheckpoint     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            java.lang.String r7 = java.lang.Boolean.toString(r7)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            r12.attribute(r6, r5, r7)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            r12.endTag(r6, r4)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
        L_0x018c:
            r4 = 0
            r6 = r4
        L_0x018e:
            android.util.ArraySet<java.lang.String> r4 = r9.mAcceptedCaCertificates     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            int r4 = r4.size()     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            if (r6 >= r4) goto L_0x01ac
            r4 = 0
            r12.startTag(r4, r2)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            android.util.ArraySet<java.lang.String> r4 = r9.mAcceptedCaCertificates     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            java.lang.Object r4 = r4.valueAt(r6)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            java.lang.String r4 = (java.lang.String) r4     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            r7 = 0
            r12.attribute(r7, r13, r4)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            r12.endTag(r7, r2)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            int r6 = r6 + 1
            goto L_0x018e
        L_0x01ac:
            r2 = 0
        L_0x01ad:
            java.util.List<java.lang.String> r4 = r9.mLockTaskPackages     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            int r4 = r4.size()     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            if (r2 >= r4) goto L_0x01cb
            java.util.List<java.lang.String> r4 = r9.mLockTaskPackages     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            java.lang.Object r4 = r4.get(r2)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            java.lang.String r4 = (java.lang.String) r4     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            r6 = 0
            r12.startTag(r6, r0)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            r12.attribute(r6, r13, r4)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            r12.endTag(r6, r0)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            int r2 = r2 + 1
            goto L_0x01ad
        L_0x01cb:
            int r0 = r9.mLockTaskFeatures     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            if (r0 == 0) goto L_0x01df
            r0 = 0
            r12.startTag(r0, r1)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            int r2 = r9.mLockTaskFeatures     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            java.lang.String r2 = java.lang.Integer.toString(r2)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            r12.attribute(r0, r5, r2)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            r12.endTag(r0, r1)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
        L_0x01df:
            boolean r0 = r9.mStatusBarDisabled     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            if (r0 == 0) goto L_0x01fc
            java.lang.String r0 = "statusbar"
            r1 = 0
            r12.startTag(r1, r0)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            java.lang.String r0 = "disabled"
            boolean r1 = r9.mStatusBarDisabled     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            java.lang.String r1 = java.lang.Boolean.toString(r1)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            r2 = 0
            r12.attribute(r2, r0, r1)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            java.lang.String r0 = "statusbar"
            r12.endTag(r2, r0)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
        L_0x01fc:
            boolean r0 = r9.doNotAskCredentialsOnBoot     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            if (r0 == 0) goto L_0x020b
            java.lang.String r0 = "do-not-ask-credentials-on-boot"
            r1 = 0
            r12.startTag(r1, r0)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            java.lang.String r0 = "do-not-ask-credentials-on-boot"
            r12.endTag(r1, r0)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
        L_0x020b:
            java.util.Set<java.lang.String> r0 = r9.mAffiliationIds     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            java.util.Iterator r0 = r0.iterator()     // Catch:{ IOException | XmlPullParserException -> 0x031d }
        L_0x0211:
            boolean r1 = r0.hasNext()     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            if (r1 == 0) goto L_0x0230
            java.lang.Object r1 = r0.next()     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            java.lang.String r1 = (java.lang.String) r1     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            java.lang.String r2 = "affiliation-id"
            r4 = 0
            r12.startTag(r4, r2)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            java.lang.String r2 = "id"
            r12.attribute(r4, r2, r1)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            java.lang.String r2 = "affiliation-id"
            r12.endTag(r4, r2)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            goto L_0x0211
        L_0x0230:
            long r0 = r9.mLastSecurityLogRetrievalTime     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            r6 = 0
            int r0 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r0 < 0) goto L_0x024e
            java.lang.String r0 = "last-security-log-retrieval"
            r1 = 0
            r12.startTag(r1, r0)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            long r13 = r9.mLastSecurityLogRetrievalTime     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            java.lang.String r0 = java.lang.Long.toString(r13)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            r12.attribute(r1, r5, r0)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            java.lang.String r0 = "last-security-log-retrieval"
            r12.endTag(r1, r0)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
        L_0x024e:
            long r0 = r9.mLastBugReportRequestTime     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            int r0 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r0 < 0) goto L_0x026a
            java.lang.String r0 = "last-bug-report-request"
            r1 = 0
            r12.startTag(r1, r0)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            long r13 = r9.mLastBugReportRequestTime     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            java.lang.String r0 = java.lang.Long.toString(r13)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            r12.attribute(r1, r5, r0)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            java.lang.String r0 = "last-bug-report-request"
            r12.endTag(r1, r0)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
        L_0x026a:
            long r0 = r9.mLastNetworkLogsRetrievalTime     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            int r0 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r0 < 0) goto L_0x0286
            java.lang.String r0 = "last-network-log-retrieval"
            r1 = 0
            r12.startTag(r1, r0)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            long r13 = r9.mLastNetworkLogsRetrievalTime     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            java.lang.String r0 = java.lang.Long.toString(r13)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            r12.attribute(r1, r5, r0)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            java.lang.String r0 = "last-network-log-retrieval"
            r12.endTag(r1, r0)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
        L_0x0286:
            boolean r0 = r9.mAdminBroadcastPending     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            if (r0 == 0) goto L_0x029e
            java.lang.String r0 = "admin-broadcast-pending"
            r1 = 0
            r12.startTag(r1, r0)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            boolean r0 = r9.mAdminBroadcastPending     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            java.lang.String r0 = java.lang.Boolean.toString(r0)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            r12.attribute(r1, r5, r0)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            java.lang.String r0 = "admin-broadcast-pending"
            r12.endTag(r1, r0)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
        L_0x029e:
            android.os.PersistableBundle r0 = r9.mInitBundle     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            if (r0 == 0) goto L_0x02b5
            java.lang.String r0 = "initialization-bundle"
            r1 = 0
            r12.startTag(r1, r0)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            android.os.PersistableBundle r0 = r9.mInitBundle     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            r0.saveToXml(r12)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            java.lang.String r0 = "initialization-bundle"
            r1 = 0
            r12.endTag(r1, r0)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
        L_0x02b5:
            long r0 = r9.mPasswordTokenHandle     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            int r0 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r0 == 0) goto L_0x02d1
            java.lang.String r0 = "password-token"
            r1 = 0
            r12.startTag(r1, r0)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            long r6 = r9.mPasswordTokenHandle     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            java.lang.String r0 = java.lang.Long.toString(r6)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            r12.attribute(r1, r5, r0)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            java.lang.String r0 = "password-token"
            r12.endTag(r1, r0)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
        L_0x02d1:
            boolean r0 = r9.mCurrentInputMethodSet     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            if (r0 == 0) goto L_0x02e0
            java.lang.String r0 = "current-ime-set"
            r1 = 0
            r12.startTag(r1, r0)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            java.lang.String r0 = "current-ime-set"
            r12.endTag(r1, r0)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
        L_0x02e0:
            java.util.Set<java.lang.String> r0 = r9.mOwnerInstalledCaCerts     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            java.util.Iterator r0 = r0.iterator()     // Catch:{ IOException | XmlPullParserException -> 0x031d }
        L_0x02e6:
            boolean r1 = r0.hasNext()     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            if (r1 == 0) goto L_0x0306
            java.lang.Object r1 = r0.next()     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            java.lang.String r1 = (java.lang.String) r1     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            java.lang.String r2 = "owner-installed-ca-cert"
            r4 = 0
            r12.startTag(r4, r2)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            java.lang.String r2 = "alias"
            r12.attribute(r4, r2, r1)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            java.lang.String r2 = "owner-installed-ca-cert"
            r12.endTag(r4, r2)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            goto L_0x02e6
        L_0x0306:
            r0 = 0
            r12.endTag(r0, r8)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            r12.endDocument()     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            r11.flush()     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            android.os.FileUtils.sync(r11)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            r11.close()     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            r19.commit()     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            r21.sendChangedNotification(r22)     // Catch:{ IOException | XmlPullParserException -> 0x031d }
            goto L_0x033c
        L_0x031d:
            r0 = move-exception
            goto L_0x0328
        L_0x031f:
            r0 = move-exception
            r3 = r21
            goto L_0x0328
        L_0x0323:
            r0 = move-exception
            r3 = r21
            r19 = r10
        L_0x0328:
            r1 = r0
            java.lang.String r0 = "DevicePolicyManager"
            java.lang.String r2 = "failed writing file"
            android.util.Slog.w(r0, r2, r1)
            if (r11 == 0) goto L_0x0338
            r11.close()     // Catch:{ IOException -> 0x0336 }
            goto L_0x0338
        L_0x0336:
            r0 = move-exception
            goto L_0x0339
        L_0x0338:
        L_0x0339:
            r19.rollback()
        L_0x033c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.DevicePolicyManagerService.saveSettingsLocked(int):void");
    }

    private void sendChangedNotification(int userHandle) {
        Intent intent = new Intent("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED");
        intent.setFlags(1073741824);
        long ident = this.mInjector.binderClearCallingIdentity();
        try {
            this.mContext.sendBroadcastAsUser(intent, new UserHandle(userHandle));
        } finally {
            this.mInjector.binderRestoreCallingIdentity(ident);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 25 */
    /* JADX WARNING: Code restructure failed: missing block: B:186:0x041c, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:187:0x041d, code lost:
        r4 = r0;
        r8 = r17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:189:0x0422, code lost:
        r8 = r17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:96:0x01bf, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:97:0x01c0, code lost:
        r4 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:98:0x01c2, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:99:0x01c3, code lost:
        r23 = r5;
        r4 = r0;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:186:0x041c A[ExcHandler: IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException (r0v6 'e' java.lang.Exception A[CUSTOM_DECLARE]), PHI: r10 r17 
      PHI: (r10v4 'needsRewrite' boolean) = (r10v0 'needsRewrite' boolean), (r10v7 'needsRewrite' boolean), (r10v7 'needsRewrite' boolean), (r10v7 'needsRewrite' boolean), (r10v7 'needsRewrite' boolean), (r10v7 'needsRewrite' boolean), (r10v7 'needsRewrite' boolean), (r10v7 'needsRewrite' boolean), (r10v7 'needsRewrite' boolean), (r10v0 'needsRewrite' boolean) binds: [B:22:0x0064, B:119:0x0234, B:83:0x018b, B:86:0x0191, B:103:0x01d2, B:91:0x01aa, B:92:?, B:94:0x01b5, B:95:?, B:27:0x006e] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r17v4 'stream' java.io.FileInputStream) = (r17v7 'stream' java.io.FileInputStream), (r17v7 'stream' java.io.FileInputStream), (r17v7 'stream' java.io.FileInputStream), (r17v7 'stream' java.io.FileInputStream), (r17v7 'stream' java.io.FileInputStream), (r17v7 'stream' java.io.FileInputStream), (r17v7 'stream' java.io.FileInputStream), (r17v7 'stream' java.io.FileInputStream), (r17v9 'stream' java.io.FileInputStream) binds: [B:119:0x0234, B:83:0x018b, B:86:0x0191, B:103:0x01d2, B:91:0x01aa, B:92:?, B:94:0x01b5, B:95:?, B:27:0x006e] A[DONT_GENERATE, DONT_INLINE], Splitter:B:27:0x006e] */
    /* JADX WARNING: Removed duplicated region for block: B:200:0x0451 A[SYNTHETIC, Splitter:B:200:0x0451] */
    /* JADX WARNING: Removed duplicated region for block: B:205:0x0465  */
    /* JADX WARNING: Removed duplicated region for block: B:208:0x047c  */
    /* JADX WARNING: Removed duplicated region for block: B:217:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void loadSettingsLocked(com.android.server.devicepolicy.DevicePolicyManagerService.DevicePolicyData r26, int r27) {
        /*
            r25 = this;
            r1 = r25
            r2 = r26
            r3 = r27
            java.lang.String r4 = "delegation-app-restrictions"
            java.lang.String r5 = "delegation-cert-install"
            java.lang.String r6 = "DevicePolicyManager"
            com.android.internal.util.JournaledFile r7 = r1.makeJournaledFile(r3)
            r8 = 0
            java.io.File r9 = r7.chooseForRead()
            r10 = 0
            java.io.FileInputStream r11 = new java.io.FileInputStream     // Catch:{ FileNotFoundException -> 0x044b, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x0432 }
            r11.<init>(r9)     // Catch:{ FileNotFoundException -> 0x044b, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x0432 }
            r8 = r11
            org.xmlpull.v1.XmlPullParser r11 = android.util.Xml.newPullParser()     // Catch:{ FileNotFoundException -> 0x042c, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x0425 }
            java.nio.charset.Charset r12 = java.nio.charset.StandardCharsets.UTF_8     // Catch:{ FileNotFoundException -> 0x042c, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x0425 }
            java.lang.String r12 = r12.name()     // Catch:{ FileNotFoundException -> 0x042c, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x0425 }
            r11.setInput(r8, r12)     // Catch:{ FileNotFoundException -> 0x042c, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x0425 }
        L_0x0029:
            int r12 = r11.next()     // Catch:{ FileNotFoundException -> 0x042c, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x0425 }
            r13 = r12
            r14 = 1
            if (r12 == r14) goto L_0x0035
            r12 = 2
            if (r13 == r12) goto L_0x0035
            goto L_0x0029
        L_0x0035:
            java.lang.String r12 = r11.getName()     // Catch:{ FileNotFoundException -> 0x042c, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x0425 }
            java.lang.String r15 = "policies"
            boolean r15 = r15.equals(r12)     // Catch:{ FileNotFoundException -> 0x042c, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x0425 }
            if (r15 == 0) goto L_0x0401
            java.lang.String r15 = "permission-provider"
            r14 = 0
            java.lang.String r15 = r11.getAttributeValue(r14, r15)     // Catch:{ FileNotFoundException -> 0x042c, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x0425 }
            if (r15 == 0) goto L_0x005e
            android.content.ComponentName r14 = android.content.ComponentName.unflattenFromString(r15)     // Catch:{ FileNotFoundException -> 0x0059, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x0053 }
            r2.mRestrictionsProvider = r14     // Catch:{ FileNotFoundException -> 0x0059, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x0053 }
            goto L_0x005e
        L_0x0053:
            r0 = move-exception
            r4 = r0
            r16 = r7
            goto L_0x0436
        L_0x0059:
            r0 = move-exception
            r16 = r7
            goto L_0x044e
        L_0x005e:
            java.lang.String r14 = "setup-complete"
            r16 = r7
            r7 = 0
            java.lang.String r14 = r11.getAttributeValue(r7, r14)     // Catch:{ FileNotFoundException -> 0x03fd, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x03f8 }
            r7 = r14
            if (r7 == 0) goto L_0x007b
            r17 = r8
            r14 = 1
            java.lang.String r8 = java.lang.Boolean.toString(r14)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            boolean r8 = r8.equals(r7)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            if (r8 == 0) goto L_0x007d
            r2.mUserSetupComplete = r14     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            goto L_0x007d
        L_0x007b:
            r17 = r8
        L_0x007d:
            java.lang.String r8 = "device-paired"
            r14 = 0
            java.lang.String r8 = r11.getAttributeValue(r14, r8)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            if (r8 == 0) goto L_0x0096
            r18 = r7
            r14 = 1
            java.lang.String r7 = java.lang.Boolean.toString(r14)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            boolean r7 = r7.equals(r8)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            if (r7 == 0) goto L_0x0098
            r2.mPaired = r14     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            goto L_0x0098
        L_0x0096:
            r18 = r7
        L_0x0098:
            java.lang.String r7 = "device-provisioning-config-applied"
            r14 = 0
            java.lang.String r7 = r11.getAttributeValue(r14, r7)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            if (r7 == 0) goto L_0x00b1
            r19 = r8
            r14 = 1
            java.lang.String r8 = java.lang.Boolean.toString(r14)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            boolean r8 = r8.equals(r7)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            if (r8 == 0) goto L_0x00b3
            r2.mDeviceProvisioningConfigApplied = r14     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            goto L_0x00b3
        L_0x00b1:
            r19 = r8
        L_0x00b3:
            java.lang.String r8 = "provisioning-state"
            r14 = 0
            java.lang.String r8 = r11.getAttributeValue(r14, r8)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            boolean r14 = android.text.TextUtils.isEmpty(r8)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            if (r14 != 0) goto L_0x00c7
            int r14 = java.lang.Integer.parseInt(r8)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r2.mUserProvisioningState = r14     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
        L_0x00c7:
            java.lang.String r14 = "permission-policy"
            r20 = r7
            r7 = 0
            java.lang.String r14 = r11.getAttributeValue(r7, r14)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r7 = r14
            boolean r14 = android.text.TextUtils.isEmpty(r7)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            if (r14 != 0) goto L_0x00de
            int r14 = java.lang.Integer.parseInt(r7)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r2.mPermissionPolicy = r14     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
        L_0x00de:
            java.lang.String r14 = "delegated-cert-installer"
            r21 = r7
            r7 = 0
            java.lang.String r14 = r11.getAttributeValue(r7, r14)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r7 = r14
            if (r7 == 0) goto L_0x0110
            android.util.ArrayMap<java.lang.String, java.util.List<java.lang.String>> r14 = r2.mDelegationMap     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            java.lang.Object r14 = r14.get(r7)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            java.util.List r14 = (java.util.List) r14     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            if (r14 != 0) goto L_0x0103
            java.util.ArrayList r22 = new java.util.ArrayList     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r22.<init>()     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r14 = r22
            r22 = r8
            android.util.ArrayMap<java.lang.String, java.util.List<java.lang.String>> r8 = r2.mDelegationMap     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r8.put(r7, r14)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            goto L_0x0105
        L_0x0103:
            r22 = r8
        L_0x0105:
            boolean r8 = r14.contains(r5)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            if (r8 != 0) goto L_0x0112
            r14.add(r5)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r10 = 1
            goto L_0x0112
        L_0x0110:
            r22 = r8
        L_0x0112:
            java.lang.String r5 = "application-restrictions-manager"
            r8 = 0
            java.lang.String r5 = r11.getAttributeValue(r8, r5)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            if (r5 == 0) goto L_0x013b
            android.util.ArrayMap<java.lang.String, java.util.List<java.lang.String>> r8 = r2.mDelegationMap     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            java.lang.Object r8 = r8.get(r5)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            java.util.List r8 = (java.util.List) r8     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            if (r8 != 0) goto L_0x0130
            java.util.ArrayList r14 = new java.util.ArrayList     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r14.<init>()     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r8 = r14
            android.util.ArrayMap<java.lang.String, java.util.List<java.lang.String>> r14 = r2.mDelegationMap     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r14.put(r5, r8)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
        L_0x0130:
            boolean r14 = r8.contains(r4)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            if (r14 != 0) goto L_0x013b
            r8.add(r4)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r4 = 1
            r10 = r4
        L_0x013b:
            int r4 = r11.next()     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            int r5 = r11.getDepth()     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            java.util.List<java.lang.String> r7 = r2.mLockTaskPackages     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r7.clear()     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            java.util.ArrayList<com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin> r7 = r2.mAdminList     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r7.clear()     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            android.util.ArrayMap<android.content.ComponentName, com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin> r7 = r2.mAdminMap     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r7.clear()     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            java.util.Set<java.lang.String> r7 = r2.mAffiliationIds     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r7.clear()     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            java.util.Set<java.lang.String> r7 = r2.mOwnerInstalledCaCerts     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r7.clear()     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
        L_0x015c:
            int r7 = r11.next()     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r4 = r7
            r8 = 1
            if (r7 == r8) goto L_0x03f1
            r7 = 3
            if (r4 != r7) goto L_0x016d
            int r8 = r11.getDepth()     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            if (r8 <= r5) goto L_0x03f5
        L_0x016d:
            if (r4 == r7) goto L_0x03e5
            r7 = 4
            if (r4 != r7) goto L_0x017a
            r24 = r4
            r23 = r5
            r4 = 1
            r8 = 0
            goto L_0x03eb
        L_0x017a:
            java.lang.String r7 = r11.getName()     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r12 = r7
            java.lang.String r7 = "admin"
            boolean r7 = r7.equals(r12)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            java.lang.String r8 = "name"
            if (r7 == 0) goto L_0x01ea
            r7 = 0
            java.lang.String r8 = r11.getAttributeValue(r7, r8)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r7 = r8
            android.content.ComponentName r8 = android.content.ComponentName.unflattenFromString(r7)     // Catch:{ RuntimeException -> 0x01cc, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r13 = 0
            android.app.admin.DeviceAdminInfo r8 = r1.findAdmin(r8, r3, r13)     // Catch:{ RuntimeException -> 0x01cc, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            if (r8 == 0) goto L_0x01c7
            android.content.ComponentName r14 = r8.getComponent()     // Catch:{ RuntimeException -> 0x01cc, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            boolean r14 = r1.shouldOverwritePoliciesFromXml(r14, r3)     // Catch:{ RuntimeException -> 0x01cc, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin r13 = new com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin     // Catch:{ RuntimeException -> 0x01cc, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r24 = r4
            r4 = 0
            r13.<init>(r8, r4)     // Catch:{ RuntimeException -> 0x01c2, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r4 = r13
            r4.readFromXml(r11, r14)     // Catch:{ RuntimeException -> 0x01c2, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            android.util.ArrayMap<android.content.ComponentName, com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin> r13 = r2.mAdminMap     // Catch:{ RuntimeException -> 0x01c2, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r23 = r5
            android.app.admin.DeviceAdminInfo r5 = r4.info     // Catch:{ RuntimeException -> 0x01bf, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            android.content.ComponentName r5 = r5.getComponent()     // Catch:{ RuntimeException -> 0x01bf, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r13.put(r5, r4)     // Catch:{ RuntimeException -> 0x01bf, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            goto L_0x01cb
        L_0x01bf:
            r0 = move-exception
            r4 = r0
            goto L_0x01d2
        L_0x01c2:
            r0 = move-exception
            r23 = r5
            r4 = r0
            goto L_0x01d2
        L_0x01c7:
            r24 = r4
            r23 = r5
        L_0x01cb:
            goto L_0x01e6
        L_0x01cc:
            r0 = move-exception
            r24 = r4
            r23 = r5
            r4 = r0
        L_0x01d2:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r5.<init>()     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            java.lang.String r8 = "Failed loading admin "
            r5.append(r8)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r5.append(r7)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            java.lang.String r5 = r5.toString()     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            android.util.Slog.w(r6, r5, r4)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
        L_0x01e6:
            r4 = 1
            r8 = 0
            goto L_0x03eb
        L_0x01ea:
            r24 = r4
            r23 = r5
            java.lang.String r4 = "delegation"
            boolean r4 = r4.equals(r12)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            if (r4 == 0) goto L_0x0227
            java.lang.String r4 = "delegatePackage"
            r5 = 0
            java.lang.String r4 = r11.getAttributeValue(r5, r4)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            java.lang.String r7 = "scope"
            java.lang.String r7 = r11.getAttributeValue(r5, r7)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r5 = r7
            android.util.ArrayMap<java.lang.String, java.util.List<java.lang.String>> r7 = r2.mDelegationMap     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            java.lang.Object r7 = r7.get(r4)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            java.util.List r7 = (java.util.List) r7     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            if (r7 != 0) goto L_0x021a
            java.util.ArrayList r8 = new java.util.ArrayList     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r8.<init>()     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r7 = r8
            android.util.ArrayMap<java.lang.String, java.util.List<java.lang.String>> r8 = r2.mDelegationMap     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r8.put(r4, r7)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
        L_0x021a:
            boolean r8 = r7.contains(r5)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            if (r8 != 0) goto L_0x0223
            r7.add(r5)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
        L_0x0223:
            r4 = 1
            r8 = 0
            goto L_0x03eb
        L_0x0227:
            java.lang.String r4 = "failed-password-attempts"
            boolean r4 = r4.equals(r12)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            java.lang.String r5 = "value"
            if (r4 == 0) goto L_0x0242
            r4 = 0
            java.lang.String r5 = r11.getAttributeValue(r4, r5)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            int r4 = java.lang.Integer.parseInt(r5)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r2.mFailedPasswordAttempts = r4     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r4 = 1
            r8 = 0
            goto L_0x03eb
        L_0x0242:
            java.lang.String r4 = "password-owner"
            boolean r4 = r4.equals(r12)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            if (r4 == 0) goto L_0x025b
            r4 = 0
            java.lang.String r5 = r11.getAttributeValue(r4, r5)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            int r4 = java.lang.Integer.parseInt(r5)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r2.mPasswordOwner = r4     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r4 = 1
            r8 = 0
            goto L_0x03eb
        L_0x025b:
            java.lang.String r4 = "accepted-ca-certificate"
            boolean r4 = r4.equals(r12)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            if (r4 == 0) goto L_0x0271
            android.util.ArraySet<java.lang.String> r4 = r2.mAcceptedCaCertificates     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r5 = 0
            java.lang.String r7 = r11.getAttributeValue(r5, r8)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r4.add(r7)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r4 = 1
            r8 = 0
            goto L_0x03eb
        L_0x0271:
            java.lang.String r4 = "lock-task-component"
            boolean r4 = r4.equals(r12)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            if (r4 == 0) goto L_0x0288
            java.util.List<java.lang.String> r4 = r2.mLockTaskPackages     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r5 = 0
            java.lang.String r7 = r11.getAttributeValue(r5, r8)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r4.add(r7)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r4 = 1
            r8 = 0
            goto L_0x03eb
        L_0x0288:
            java.lang.String r4 = "lock-task-features"
            boolean r4 = r4.equals(r12)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            if (r4 == 0) goto L_0x02a1
            r4 = 0
            java.lang.String r5 = r11.getAttributeValue(r4, r5)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            int r4 = java.lang.Integer.parseInt(r5)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r2.mLockTaskFeatures = r4     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r4 = 1
            r8 = 0
            goto L_0x03eb
        L_0x02a1:
            java.lang.String r4 = "statusbar"
            boolean r4 = r4.equals(r12)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            if (r4 == 0) goto L_0x02bb
            java.lang.String r4 = "disabled"
            r5 = 0
            java.lang.String r4 = r11.getAttributeValue(r5, r4)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            boolean r4 = java.lang.Boolean.parseBoolean(r4)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r2.mStatusBarDisabled = r4     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r4 = 1
            r8 = 0
            goto L_0x03eb
        L_0x02bb:
            java.lang.String r4 = "do-not-ask-credentials-on-boot"
            boolean r4 = r4.equals(r12)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            if (r4 == 0) goto L_0x02ca
            r4 = 1
            r2.doNotAskCredentialsOnBoot = r4     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r4 = 1
            r8 = 0
            goto L_0x03eb
        L_0x02ca:
            java.lang.String r4 = "affiliation-id"
            boolean r4 = r4.equals(r12)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            if (r4 == 0) goto L_0x02e3
            java.util.Set<java.lang.String> r4 = r2.mAffiliationIds     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            java.lang.String r5 = "id"
            r7 = 0
            java.lang.String r5 = r11.getAttributeValue(r7, r5)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r4.add(r5)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r4 = 1
            r8 = 0
            goto L_0x03eb
        L_0x02e3:
            java.lang.String r4 = "last-security-log-retrieval"
            boolean r4 = r4.equals(r12)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            if (r4 == 0) goto L_0x02fc
            r4 = 0
            java.lang.String r5 = r11.getAttributeValue(r4, r5)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            long r4 = java.lang.Long.parseLong(r5)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r2.mLastSecurityLogRetrievalTime = r4     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r4 = 1
            r8 = 0
            goto L_0x03eb
        L_0x02fc:
            java.lang.String r4 = "last-bug-report-request"
            boolean r4 = r4.equals(r12)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            if (r4 == 0) goto L_0x0315
            r4 = 0
            java.lang.String r5 = r11.getAttributeValue(r4, r5)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            long r4 = java.lang.Long.parseLong(r5)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r2.mLastBugReportRequestTime = r4     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r4 = 1
            r8 = 0
            goto L_0x03eb
        L_0x0315:
            java.lang.String r4 = "last-network-log-retrieval"
            boolean r4 = r4.equals(r12)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            if (r4 == 0) goto L_0x032e
            r4 = 0
            java.lang.String r5 = r11.getAttributeValue(r4, r5)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            long r4 = java.lang.Long.parseLong(r5)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r2.mLastNetworkLogsRetrievalTime = r4     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r4 = 1
            r8 = 0
            goto L_0x03eb
        L_0x032e:
            java.lang.String r4 = "admin-broadcast-pending"
            boolean r4 = r4.equals(r12)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            if (r4 == 0) goto L_0x034b
            r4 = 0
            java.lang.String r5 = r11.getAttributeValue(r4, r5)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r4 = r5
            r5 = 1
            java.lang.String r7 = java.lang.Boolean.toString(r5)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            boolean r5 = r7.equals(r4)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r2.mAdminBroadcastPending = r5     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r4 = 1
            r8 = 0
            goto L_0x03eb
        L_0x034b:
            java.lang.String r4 = "initialization-bundle"
            boolean r4 = r4.equals(r12)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            if (r4 == 0) goto L_0x035e
            android.os.PersistableBundle r4 = android.os.PersistableBundle.restoreFromXml(r11)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r2.mInitBundle = r4     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r4 = 1
            r8 = 0
            goto L_0x03eb
        L_0x035e:
            java.lang.String r4 = "active-password"
            boolean r4 = r4.equals(r12)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            if (r4 == 0) goto L_0x036d
            r10 = 1
            r5 = r23
            r4 = r24
            goto L_0x015c
        L_0x036d:
            java.lang.String r4 = "password-validity"
            boolean r4 = r4.equals(r12)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            if (r4 == 0) goto L_0x0390
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r4 = r1.mInjector     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            boolean r4 = r4.storageManagerIsFileBasedEncryptionEnabled()     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            if (r4 != 0) goto L_0x038d
            r4 = 0
            java.lang.String r5 = r11.getAttributeValue(r4, r5)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            boolean r4 = java.lang.Boolean.parseBoolean(r5)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r2.mPasswordValidAtLastCheckpoint = r4     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r4 = 1
            r8 = 0
            goto L_0x03eb
        L_0x038d:
            r4 = 1
            r8 = 0
            goto L_0x03eb
        L_0x0390:
            java.lang.String r4 = "password-token"
            boolean r4 = r4.equals(r12)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            if (r4 == 0) goto L_0x03a8
            r4 = 0
            java.lang.String r5 = r11.getAttributeValue(r4, r5)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            long r4 = java.lang.Long.parseLong(r5)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r2.mPasswordTokenHandle = r4     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r4 = 1
            r8 = 0
            goto L_0x03eb
        L_0x03a8:
            java.lang.String r4 = "current-ime-set"
            boolean r4 = r4.equals(r12)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            if (r4 == 0) goto L_0x03b5
            r4 = 1
            r2.mCurrentInputMethodSet = r4     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r8 = 0
            goto L_0x03eb
        L_0x03b5:
            r4 = 1
            java.lang.String r5 = "owner-installed-ca-cert"
            boolean r5 = r5.equals(r12)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            if (r5 == 0) goto L_0x03cc
            java.util.Set<java.lang.String> r5 = r2.mOwnerInstalledCaCerts     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            java.lang.String r7 = "alias"
            r8 = 0
            java.lang.String r7 = r11.getAttributeValue(r8, r7)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r5.add(r7)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            goto L_0x03eb
        L_0x03cc:
            r8 = 0
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r5.<init>()     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            java.lang.String r7 = "Unknown tag: "
            r5.append(r7)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r5.append(r12)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            java.lang.String r5 = r5.toString()     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            android.util.Slog.w(r6, r5)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            com.android.internal.util.XmlUtils.skipCurrentTag(r11)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            goto L_0x03eb
        L_0x03e5:
            r24 = r4
            r23 = r5
            r4 = 1
            r8 = 0
        L_0x03eb:
            r5 = r23
            r4 = r24
            goto L_0x015c
        L_0x03f1:
            r24 = r4
            r23 = r5
        L_0x03f5:
            r8 = r17
            goto L_0x044f
        L_0x03f8:
            r0 = move-exception
            r17 = r8
            r4 = r0
            goto L_0x0436
        L_0x03fd:
            r0 = move-exception
            r17 = r8
            goto L_0x044e
        L_0x0401:
            r16 = r7
            r17 = r8
            org.xmlpull.v1.XmlPullParserException r4 = new org.xmlpull.v1.XmlPullParserException     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r5.<init>()     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            java.lang.String r7 = "Settings do not start with policies tag: found "
            r5.append(r7)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r5.append(r12)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            java.lang.String r5 = r5.toString()     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            r4.<init>(r5)     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
            throw r4     // Catch:{ FileNotFoundException -> 0x0421, IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException -> 0x041c }
        L_0x041c:
            r0 = move-exception
            r4 = r0
            r8 = r17
            goto L_0x0436
        L_0x0421:
            r0 = move-exception
            r8 = r17
            goto L_0x044e
        L_0x0425:
            r0 = move-exception
            r16 = r7
            r17 = r8
            r4 = r0
            goto L_0x0436
        L_0x042c:
            r0 = move-exception
            r16 = r7
            r17 = r8
            goto L_0x044e
        L_0x0432:
            r0 = move-exception
            r16 = r7
            r4 = r0
        L_0x0436:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r7 = "failed parsing "
            r5.append(r7)
            r5.append(r9)
            java.lang.String r5 = r5.toString()
            android.util.Slog.w(r6, r5, r4)
            goto L_0x044f
        L_0x044b:
            r0 = move-exception
            r16 = r7
        L_0x044e:
        L_0x044f:
            if (r8 == 0) goto L_0x0457
            r8.close()     // Catch:{ IOException -> 0x0455 }
            goto L_0x0457
        L_0x0455:
            r0 = move-exception
            goto L_0x0458
        L_0x0457:
        L_0x0458:
            java.util.ArrayList<com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin> r4 = r2.mAdminList
            android.util.ArrayMap<android.content.ComponentName, com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin> r5 = r2.mAdminMap
            java.util.Collection r5 = r5.values()
            r4.addAll(r5)
            if (r10 == 0) goto L_0x0468
            r1.saveSettingsLocked(r3)
        L_0x0468:
            r25.validatePasswordOwnerLocked(r26)
            r1.updateMaximumTimeToLockLocked(r3)
            java.util.List<java.lang.String> r4 = r2.mLockTaskPackages
            r1.updateLockTaskPackagesLocked(r4, r3)
            int r4 = r2.mLockTaskFeatures
            r1.updateLockTaskFeaturesLocked(r4, r3)
            boolean r4 = r2.mStatusBarDisabled
            if (r4 == 0) goto L_0x0481
            boolean r4 = r2.mStatusBarDisabled
            r1.setStatusBarDisabledInternal(r4, r3)
        L_0x0481:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.DevicePolicyManagerService.loadSettingsLocked(com.android.server.devicepolicy.DevicePolicyManagerService$DevicePolicyData, int):void");
    }

    private boolean shouldOverwritePoliciesFromXml(ComponentName deviceAdminComponent, int userHandle) {
        return !isProfileOwner(deviceAdminComponent, userHandle) && !isDeviceOwner(deviceAdminComponent, userHandle);
    }

    private void updateLockTaskPackagesLocked(List<String> packages, int userId) {
        long ident = this.mInjector.binderClearCallingIdentity();
        try {
            this.mInjector.getIActivityManager().updateLockTaskPackages(userId, (String[]) packages.toArray(new String[packages.size()]));
        } catch (RemoteException e) {
        } catch (Throwable th) {
            this.mInjector.binderRestoreCallingIdentity(ident);
            throw th;
        }
        this.mInjector.binderRestoreCallingIdentity(ident);
    }

    private void updateLockTaskFeaturesLocked(int flags, int userId) {
        long ident = this.mInjector.binderClearCallingIdentity();
        try {
            this.mInjector.getIActivityTaskManager().updateLockTaskFeatures(userId, flags);
        } catch (RemoteException e) {
        } catch (Throwable th) {
            this.mInjector.binderRestoreCallingIdentity(ident);
            throw th;
        }
        this.mInjector.binderRestoreCallingIdentity(ident);
    }

    private void updateDeviceOwnerLocked() {
        long ident = this.mInjector.binderClearCallingIdentity();
        try {
            ComponentName deviceOwnerComponent = this.mOwners.getDeviceOwnerComponent();
            if (deviceOwnerComponent != null) {
                this.mInjector.getIActivityManager().updateDeviceOwner(deviceOwnerComponent.getPackageName());
            }
        } catch (RemoteException e) {
        } catch (Throwable th) {
            this.mInjector.binderRestoreCallingIdentity(ident);
            throw th;
        }
        this.mInjector.binderRestoreCallingIdentity(ident);
    }

    static void validateQualityConstant(int quality) {
        if (quality != 0 && quality != 32768 && quality != 65536 && quality != 131072 && quality != 196608 && quality != 262144 && quality != 327680 && quality != 393216 && quality != 524288) {
            throw new IllegalArgumentException("Invalid quality constant: 0x" + Integer.toHexString(quality));
        }
    }

    /* access modifiers changed from: package-private */
    public void validatePasswordOwnerLocked(DevicePolicyData policy) {
        if (policy.mPasswordOwner >= 0) {
            boolean haveOwner = false;
            int i = policy.mAdminList.size() - 1;
            while (true) {
                if (i < 0) {
                    break;
                } else if (policy.mAdminList.get(i).getUid() == policy.mPasswordOwner) {
                    haveOwner = true;
                    break;
                } else {
                    i--;
                }
            }
            if (!haveOwner) {
                Slog.w(LOG_TAG, "Previous password owner " + policy.mPasswordOwner + " no longer active; disabling");
                policy.mPasswordOwner = -1;
            }
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void systemReady(int phase) {
        if (this.mHasFeature) {
            if (phase == 480) {
                onLockSettingsReady();
                loadAdminDataAsync();
                this.mOwners.systemReady();
            } else if (phase == 550) {
                maybeStartSecurityLogMonitorOnActivityManagerReady();
            } else if (phase == 1000) {
                ensureDeviceOwnerUserStarted();
            }
        }
    }

    private void onLockSettingsReady() {
        List<String> packageList;
        getUserData(0);
        loadOwners();
        cleanUpOldUsers();
        maybeSetDefaultProfileOwnerUserRestrictions();
        handleStartUser(0);
        maybeLogStart();
        this.mSetupContentObserver.register();
        updateUserSetupCompleteAndPaired();
        synchronized (getLockObject()) {
            packageList = getKeepUninstalledPackagesLocked();
        }
        if (packageList != null) {
            this.mInjector.getPackageManagerInternal().setKeepUninstalledPackages(packageList);
        }
        synchronized (getLockObject()) {
            ActiveAdmin deviceOwner = getDeviceOwnerAdminLocked();
            if (deviceOwner != null) {
                this.mUserManagerInternal.setForceEphemeralUsers(deviceOwner.forceEphemeralUsers);
                ActivityManagerInternal activityManagerInternal = this.mInjector.getActivityManagerInternal();
                activityManagerInternal.setSwitchingFromSystemUserMessage(deviceOwner.startUserSessionMessage);
                activityManagerInternal.setSwitchingToSystemUserMessage(deviceOwner.endUserSessionMessage);
            }
            revertTransferOwnershipIfNecessaryLocked();
        }
    }

    private void revertTransferOwnershipIfNecessaryLocked() {
        if (this.mTransferOwnershipMetadataManager.metadataFileExists()) {
            Slog.e(LOG_TAG, "Owner transfer metadata file exists! Reverting transfer.");
            TransferOwnershipMetadataManager.Metadata metadata = this.mTransferOwnershipMetadataManager.loadMetadataFile();
            if (metadata.adminType.equals(LOG_TAG_PROFILE_OWNER)) {
                transferProfileOwnershipLocked(metadata.targetComponent, metadata.sourceComponent, metadata.userId);
                deleteTransferOwnershipMetadataFileLocked();
                deleteTransferOwnershipBundleLocked(metadata.userId);
            } else if (metadata.adminType.equals(LOG_TAG_DEVICE_OWNER)) {
                transferDeviceOwnershipLocked(metadata.targetComponent, metadata.sourceComponent, metadata.userId);
                deleteTransferOwnershipMetadataFileLocked();
                deleteTransferOwnershipBundleLocked(metadata.userId);
            }
            updateSystemUpdateFreezePeriodsRecord(true);
        }
    }

    private void maybeLogStart() {
        if (SecurityLog.isLoggingEnabled()) {
            SecurityLog.writeEvent(210009, new Object[]{this.mInjector.systemPropertiesGet("ro.boot.verifiedbootstate"), this.mInjector.systemPropertiesGet("ro.boot.veritymode")});
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:?, code lost:
        r4.mInjector.getIActivityManager().startUserInBackground(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0022, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0023, code lost:
        android.util.Slog.w(LOG_TAG, "Exception starting user", r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0016, code lost:
        if (r1 == 0) goto L_?;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void ensureDeviceOwnerUserStarted() {
        /*
            r4 = this;
            java.lang.Object r0 = r4.getLockObject()
            monitor-enter(r0)
            com.android.server.devicepolicy.Owners r1 = r4.mOwners     // Catch:{ all -> 0x002b }
            boolean r1 = r1.hasDeviceOwner()     // Catch:{ all -> 0x002b }
            if (r1 != 0) goto L_0x000f
            monitor-exit(r0)     // Catch:{ all -> 0x002b }
            return
        L_0x000f:
            com.android.server.devicepolicy.Owners r1 = r4.mOwners     // Catch:{ all -> 0x002b }
            int r1 = r1.getDeviceOwnerUserId()     // Catch:{ all -> 0x002b }
            monitor-exit(r0)     // Catch:{ all -> 0x002b }
            if (r1 == 0) goto L_0x002a
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r0 = r4.mInjector     // Catch:{ RemoteException -> 0x0022 }
            android.app.IActivityManager r0 = r0.getIActivityManager()     // Catch:{ RemoteException -> 0x0022 }
            r0.startUserInBackground(r1)     // Catch:{ RemoteException -> 0x0022 }
            goto L_0x002a
        L_0x0022:
            r0 = move-exception
            java.lang.String r2 = "DevicePolicyManager"
            java.lang.String r3 = "Exception starting user"
            android.util.Slog.w(r2, r3, r0)
        L_0x002a:
            return
        L_0x002b:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x002b }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.DevicePolicyManagerService.ensureDeviceOwnerUserStarted():void");
    }

    /* access modifiers changed from: package-private */
    public void handleStartUser(int userId) {
        updateScreenCaptureDisabled(userId, getScreenCaptureDisabled((ComponentName) null, userId));
        pushUserRestrictions(userId);
        updatePasswordQualityCacheForUserGroup(userId == 0 ? -1 : userId);
        startOwnerService(userId, "start-user");
    }

    /* access modifiers changed from: package-private */
    public void handleUnlockUser(int userId) {
        startOwnerService(userId, "unlock-user");
    }

    /* access modifiers changed from: package-private */
    public void handleStopUser(int userId) {
        stopOwnerService(userId, "stop-user");
    }

    private void startOwnerService(int userId, String actionForLog) {
        ComponentName owner = getOwnerComponent(userId);
        if (owner != null) {
            this.mDeviceAdminServiceController.startServiceForOwner(owner.getPackageName(), userId, actionForLog);
        }
    }

    private void stopOwnerService(int userId, String actionForLog) {
        this.mDeviceAdminServiceController.stopServiceForOwner(userId, actionForLog);
    }

    private void cleanUpOldUsers() {
        Set<Integer> usersWithProfileOwners;
        Set<Integer> usersWithData;
        synchronized (getLockObject()) {
            usersWithProfileOwners = this.mOwners.getProfileOwnerKeys();
            usersWithData = new ArraySet<>();
            for (int i = 0; i < this.mUserData.size(); i++) {
                usersWithData.add(Integer.valueOf(this.mUserData.keyAt(i)));
            }
        }
        List<UserInfo> allUsers = this.mUserManager.getUsers();
        Set<Integer> deletedUsers = new ArraySet<>();
        deletedUsers.addAll(usersWithProfileOwners);
        deletedUsers.addAll(usersWithData);
        for (UserInfo userInfo : allUsers) {
            deletedUsers.remove(Integer.valueOf(userInfo.id));
        }
        for (Integer userId : deletedUsers) {
            removeUserData(userId.intValue());
        }
    }

    /* access modifiers changed from: private */
    public void handlePasswordExpirationNotification(int userHandle) {
        int i = userHandle;
        Bundle adminExtras = new Bundle();
        adminExtras.putParcelable("android.intent.extra.USER", UserHandle.of(userHandle));
        synchronized (getLockObject()) {
            long now = System.currentTimeMillis();
            List<ActiveAdmin> admins = getActiveAdminsForLockscreenPoliciesLocked(i, false);
            int N = admins.size();
            for (int i2 = 0; i2 < N; i2++) {
                ActiveAdmin admin = admins.get(i2);
                if (admin.info.usesPolicy(6) && admin.passwordExpirationTimeout > 0 && now >= admin.passwordExpirationDate - EXPIRATION_GRACE_PERIOD_MS && admin.passwordExpirationDate > 0) {
                    sendAdminCommandLocked(admin, "android.app.action.ACTION_PASSWORD_EXPIRING", adminExtras, (BroadcastReceiver) null);
                }
            }
            setExpirationAlarmCheckLocked(this.mContext, i, false);
        }
    }

    /* access modifiers changed from: protected */
    public void onInstalledCertificatesChanged(UserHandle userHandle, Collection<String> installedCertificates) {
        if (this.mHasFeature) {
            enforceManageUsers();
            synchronized (getLockObject()) {
                DevicePolicyData policy = getUserData(userHandle.getIdentifier());
                if ((false | policy.mAcceptedCaCertificates.retainAll(installedCertificates)) || policy.mOwnerInstalledCaCerts.retainAll(installedCertificates)) {
                    saveSettingsLocked(userHandle.getIdentifier());
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public Set<String> getAcceptedCaCertificates(UserHandle userHandle) {
        ArraySet<String> arraySet;
        if (!this.mHasFeature) {
            return Collections.emptySet();
        }
        synchronized (getLockObject()) {
            arraySet = getUserData(userHandle.getIdentifier()).mAcceptedCaCertificates;
        }
        return arraySet;
    }

    public void setActiveAdmin(ComponentName adminReceiver, boolean refreshing, int userHandle) {
        if (this.mHasFeature) {
            setActiveAdmin(adminReceiver, refreshing, userHandle, (Bundle) null);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 16 */
    private void setActiveAdmin(ComponentName adminReceiver, boolean refreshing, int userHandle, Bundle onEnableData) {
        ActiveAdmin newAdmin;
        boolean z;
        ComponentName componentName = adminReceiver;
        int i = userHandle;
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_DEVICE_ADMINS", (String) null);
        enforceFullCrossUsersPermission(i);
        DevicePolicyData policy = getUserData(i);
        DeviceAdminInfo info = findAdmin(componentName, i, true);
        synchronized (getLockObject()) {
            try {
                checkActiveAdminPrecondition(componentName, info, policy);
                long ident = this.mInjector.binderClearCallingIdentity();
                try {
                    ActiveAdmin existingAdmin = getActiveAdminUncheckedLocked(componentName, i);
                    if (!refreshing) {
                        if (existingAdmin != null) {
                            throw new IllegalArgumentException("Admin is already added");
                        }
                    }
                    newAdmin = new ActiveAdmin(info, false);
                    if (existingAdmin != null) {
                        z = existingAdmin.testOnlyAdmin;
                    } else {
                        z = isPackageTestOnly(adminReceiver.getPackageName(), i);
                    }
                    newAdmin.testOnlyAdmin = z;
                    policy.mAdminMap.put(componentName, newAdmin);
                    int replaceIndex = -1;
                    int N = policy.mAdminList.size();
                    int i2 = 0;
                    while (true) {
                        if (i2 >= N) {
                            break;
                        } else if (policy.mAdminList.get(i2).info.getComponent().equals(componentName)) {
                            replaceIndex = i2;
                            break;
                        } else {
                            i2++;
                        }
                    }
                    if (replaceIndex == -1) {
                        policy.mAdminList.add(newAdmin);
                        enableIfNecessary(info.getPackageName(), i);
                        this.mUsageStatsManagerInternal.onActiveAdminAdded(adminReceiver.getPackageName(), i);
                    } else {
                        policy.mAdminList.set(replaceIndex, newAdmin);
                    }
                    saveSettingsLocked(i);
                } catch (Throwable th) {
                    th = th;
                    Bundle bundle = onEnableData;
                    this.mInjector.binderRestoreCallingIdentity(ident);
                    throw th;
                }
                try {
                    sendAdminCommandLocked(newAdmin, "android.app.action.DEVICE_ADMIN_ENABLED", onEnableData, (BroadcastReceiver) null);
                    this.mInjector.binderRestoreCallingIdentity(ident);
                } catch (Throwable th2) {
                    th = th2;
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                Bundle bundle2 = onEnableData;
                throw th;
            }
        }
    }

    private void loadAdminDataAsync() {
        this.mInjector.postOnSystemServerInitThreadPool(new Runnable() {
            public final void run() {
                DevicePolicyManagerService.this.lambda$loadAdminDataAsync$0$DevicePolicyManagerService();
            }
        });
    }

    public /* synthetic */ void lambda$loadAdminDataAsync$0$DevicePolicyManagerService() {
        pushActiveAdminPackages();
        this.mUsageStatsManagerInternal.onAdminDataAvailable();
        pushAllMeteredRestrictedPackages();
        this.mInjector.getNetworkPolicyManagerInternal().onAdminDataAvailable();
    }

    private void pushActiveAdminPackages() {
        synchronized (getLockObject()) {
            List<UserInfo> users = this.mUserManager.getUsers();
            for (int i = users.size() - 1; i >= 0; i--) {
                int userId = users.get(i).id;
                this.mUsageStatsManagerInternal.setActiveAdminApps(getActiveAdminPackagesLocked(userId), userId);
            }
        }
    }

    private void pushAllMeteredRestrictedPackages() {
        synchronized (getLockObject()) {
            List<UserInfo> users = this.mUserManager.getUsers();
            for (int i = users.size() - 1; i >= 0; i--) {
                int userId = users.get(i).id;
                this.mInjector.getNetworkPolicyManagerInternal().setMeteredRestrictedPackagesAsync(getMeteredDisabledPackagesLocked(userId), userId);
            }
        }
    }

    private void pushActiveAdminPackagesLocked(int userId) {
        this.mUsageStatsManagerInternal.setActiveAdminApps(getActiveAdminPackagesLocked(userId), userId);
    }

    private Set<String> getActiveAdminPackagesLocked(int userId) {
        DevicePolicyData policy = getUserData(userId);
        Set<String> adminPkgs = null;
        for (int i = policy.mAdminList.size() - 1; i >= 0; i--) {
            String pkgName = policy.mAdminList.get(i).info.getPackageName();
            if (adminPkgs == null) {
                adminPkgs = new ArraySet<>();
            }
            adminPkgs.add(pkgName);
        }
        return adminPkgs;
    }

    private void transferActiveAdminUncheckedLocked(ComponentName incomingReceiver, ComponentName outgoingReceiver, int userHandle) {
        DevicePolicyData policy = getUserData(userHandle);
        if (policy.mAdminMap.containsKey(outgoingReceiver) || !policy.mAdminMap.containsKey(incomingReceiver)) {
            DeviceAdminInfo incomingDeviceInfo = findAdmin(incomingReceiver, userHandle, true);
            ActiveAdmin adminToTransfer = policy.mAdminMap.get(outgoingReceiver);
            int oldAdminUid = adminToTransfer.getUid();
            adminToTransfer.transfer(incomingDeviceInfo);
            policy.mAdminMap.remove(outgoingReceiver);
            policy.mAdminMap.put(incomingReceiver, adminToTransfer);
            if (policy.mPasswordOwner == oldAdminUid) {
                policy.mPasswordOwner = adminToTransfer.getUid();
            }
            saveSettingsLocked(userHandle);
            sendAdminCommandLocked(adminToTransfer, "android.app.action.DEVICE_ADMIN_ENABLED", (Bundle) null, (BroadcastReceiver) null);
        }
    }

    private void checkActiveAdminPrecondition(ComponentName adminReceiver, DeviceAdminInfo info, DevicePolicyData policy) {
        if (info == null) {
            throw new IllegalArgumentException("Bad admin: " + adminReceiver);
        } else if (!info.getActivityInfo().applicationInfo.isInternal()) {
            throw new IllegalArgumentException("Only apps in internal storage can be active admin: " + adminReceiver);
        } else if (info.getActivityInfo().applicationInfo.isInstantApp()) {
            throw new IllegalArgumentException("Instant apps cannot be device admins: " + adminReceiver);
        } else if (policy.mRemovingAdmins.contains(adminReceiver)) {
            throw new IllegalArgumentException("Trying to set an admin which is being removed");
        }
    }

    public boolean isAdminActive(ComponentName adminReceiver, int userHandle) {
        boolean z = false;
        if (!this.mHasFeature) {
            return false;
        }
        enforceFullCrossUsersPermission(userHandle);
        synchronized (getLockObject()) {
            if (getActiveAdminUncheckedLocked(adminReceiver, userHandle) != null) {
                z = true;
            }
        }
        return z;
    }

    public boolean isRemovingAdmin(ComponentName adminReceiver, int userHandle) {
        boolean contains;
        if (!this.mHasFeature) {
            return false;
        }
        enforceFullCrossUsersPermission(userHandle);
        synchronized (getLockObject()) {
            contains = getUserData(userHandle).mRemovingAdmins.contains(adminReceiver);
        }
        return contains;
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    public boolean hasGrantedPolicy(ComponentName adminReceiver, int policyId, int userHandle) {
        boolean usesPolicy;
        if (!this.mHasFeature) {
            return false;
        }
        enforceFullCrossUsersPermission(userHandle);
        synchronized (getLockObject()) {
            ActiveAdmin administrator = getActiveAdminUncheckedLocked(adminReceiver, userHandle);
            if (administrator != null) {
                usesPolicy = administrator.info.usesPolicy(policyId);
            } else {
                throw new SecurityException("No active admin " + adminReceiver);
            }
        }
        return usesPolicy;
    }

    public List<ComponentName> getActiveAdmins(int userHandle) {
        if (!this.mHasFeature) {
            return Collections.EMPTY_LIST;
        }
        enforceFullCrossUsersPermission(userHandle);
        synchronized (getLockObject()) {
            DevicePolicyData policy = getUserData(userHandle);
            int N = policy.mAdminList.size();
            if (N <= 0) {
                return null;
            }
            ArrayList<ComponentName> res = new ArrayList<>(N);
            for (int i = 0; i < N; i++) {
                res.add(policy.mAdminList.get(i).info.getComponent());
            }
            return res;
        }
    }

    public boolean packageHasActiveAdmins(String packageName, int userHandle) {
        if (!this.mHasFeature) {
            return false;
        }
        enforceFullCrossUsersPermission(userHandle);
        synchronized (getLockObject()) {
            DevicePolicyData policy = getUserData(userHandle);
            int N = policy.mAdminList.size();
            for (int i = 0; i < N; i++) {
                if (policy.mAdminList.get(i).info.getPackageName().equals(packageName)) {
                    return true;
                }
            }
            return false;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    public void forceRemoveActiveAdmin(ComponentName adminReceiver, int userHandle) {
        if (this.mHasFeature) {
            Preconditions.checkNotNull(adminReceiver, "ComponentName is null");
            enforceShell("forceRemoveActiveAdmin");
            long ident = this.mInjector.binderClearCallingIdentity();
            try {
                synchronized (getLockObject()) {
                    if (isAdminTestOnlyLocked(adminReceiver, userHandle)) {
                        if (isDeviceOwner(adminReceiver, userHandle)) {
                            clearDeviceOwnerLocked(getDeviceOwnerAdminLocked(), userHandle);
                        }
                        if (isProfileOwner(adminReceiver, userHandle)) {
                            clearProfileOwnerLocked(getActiveAdminUncheckedLocked(adminReceiver, userHandle, false), userHandle);
                        }
                    } else {
                        throw new SecurityException("Attempt to remove non-test admin " + adminReceiver + " " + userHandle);
                    }
                }
                removeAdminArtifacts(adminReceiver, userHandle);
                Slog.i(LOG_TAG, "Admin " + adminReceiver + " removed from user " + userHandle);
                this.mInjector.binderRestoreCallingIdentity(ident);
            } catch (Throwable th) {
                this.mInjector.binderRestoreCallingIdentity(ident);
                throw th;
            }
        }
    }

    private void clearDeviceOwnerUserRestrictionLocked(UserHandle userHandle) {
        if (this.mUserManager.hasUserRestriction("no_add_user", userHandle)) {
            this.mUserManager.setUserRestriction("no_add_user", false, userHandle);
        }
    }

    private boolean isPackageTestOnly(String packageName, int userHandle) {
        try {
            ApplicationInfo ai = this.mInjector.getIPackageManager().getApplicationInfo(packageName, 786432, userHandle);
            if (ai != null) {
                return (ai.flags & 256) != 0;
            }
            throw new IllegalStateException("Couldn't find package: " + packageName + " on user " + userHandle);
        } catch (RemoteException e) {
            throw new IllegalStateException(e);
        }
    }

    private boolean isAdminTestOnlyLocked(ComponentName who, int userHandle) {
        ActiveAdmin admin = getActiveAdminUncheckedLocked(who, userHandle);
        return admin != null && admin.testOnlyAdmin;
    }

    private void enforceShell(String method) {
        int callingUid = this.mInjector.binderGetCallingUid();
        if (callingUid != 2000 && callingUid != 0) {
            throw new SecurityException("Non-shell user attempted to call " + method);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    public void removeActiveAdmin(ComponentName adminReceiver, int userHandle) {
        if (this.mHasFeature) {
            enforceFullCrossUsersPermission(userHandle);
            enforceUserUnlocked(userHandle);
            synchronized (getLockObject()) {
                ActiveAdmin admin = getActiveAdminUncheckedLocked(adminReceiver, userHandle);
                if (admin != null) {
                    if (!isDeviceOwner(adminReceiver, userHandle)) {
                        if (!isProfileOwner(adminReceiver, userHandle)) {
                            if (admin.getUid() != this.mInjector.binderGetCallingUid()) {
                                this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_DEVICE_ADMINS", (String) null);
                            }
                            long ident = this.mInjector.binderClearCallingIdentity();
                            try {
                                removeActiveAdminLocked(adminReceiver, userHandle);
                                return;
                            } finally {
                                this.mInjector.binderRestoreCallingIdentity(ident);
                            }
                        }
                    }
                    Slog.e(LOG_TAG, "Device/profile owner cannot be removed: component=" + adminReceiver);
                }
            }
        }
    }

    public boolean isSeparateProfileChallengeAllowed(int userHandle) {
        if (isCallerWithSystemUid()) {
            ComponentName profileOwner = getProfileOwner(userHandle);
            return profileOwner != null && getTargetSdk(profileOwner.getPackageName(), userHandle) > 23;
        }
        throw new SecurityException("Caller must be system");
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    public void setPasswordQuality(ComponentName who, int quality, boolean parent) {
        if (this.mHasFeature) {
            Preconditions.checkNotNull(who, "ComponentName is null");
            validateQualityConstant(quality);
            int userId = this.mInjector.userHandleGetCallingUserId();
            synchronized (getLockObject()) {
                ActiveAdmin ap = getActiveAdminForCallerLocked(who, 0, parent);
                long ident = this.mInjector.binderClearCallingIdentity();
                try {
                    PasswordMetrics metrics = ap.minimumPasswordMetrics;
                    if (metrics.quality != quality) {
                        metrics.quality = quality;
                        updatePasswordValidityCheckpointLocked(userId, parent);
                        updatePasswordQualityCacheForUserGroup(userId);
                        saveSettingsLocked(userId);
                    }
                    maybeLogPasswordComplexitySet(who, userId, parent, metrics);
                } finally {
                    this.mInjector.binderRestoreCallingIdentity(ident);
                }
            }
            DevicePolicyEventLogger.createEvent(1).setAdmin(who).setInt(quality).setBoolean(parent).write();
        }
    }

    @GuardedBy({"getLockObject()"})
    private void updatePasswordValidityCheckpointLocked(int userHandle, boolean parent) {
        int credentialOwner = getCredentialOwner(userHandle, parent);
        DevicePolicyData policy = getUserData(credentialOwner);
        PasswordMetrics metrics = getUserPasswordMetricsLocked(credentialOwner);
        if (metrics == null) {
            metrics = new PasswordMetrics();
        }
        policy.mPasswordValidAtLastCheckpoint = isPasswordSufficientForUserWithoutCheckpointLocked(metrics, userHandle, parent);
        saveSettingsLocked(credentialOwner);
    }

    /* access modifiers changed from: private */
    public void updatePasswordQualityCacheForUserGroup(int userId) {
        List<UserInfo> users;
        if (userId == -1) {
            users = this.mUserManager.getUsers();
        } else {
            users = this.mUserManager.getProfiles(userId);
        }
        for (UserInfo userInfo : users) {
            int currentUserId = userInfo.id;
            this.mPolicyCache.setPasswordQuality(currentUserId, getPasswordQuality((ComponentName) null, currentUserId, false));
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x001e, code lost:
        return r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int getPasswordQuality(android.content.ComponentName r8, int r9, boolean r10) {
        /*
            r7 = this;
            boolean r0 = r7.mHasFeature
            if (r0 != 0) goto L_0x0006
            r0 = 0
            return r0
        L_0x0006:
            r7.enforceFullCrossUsersPermission(r9)
            java.lang.Object r0 = r7.getLockObject()
            monitor-enter(r0)
            r1 = 0
            if (r8 == 0) goto L_0x001f
            com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin r2 = r7.getActiveAdminUncheckedLocked(r8, r9, r10)     // Catch:{ all -> 0x0041 }
            if (r2 == 0) goto L_0x001c
            android.app.admin.PasswordMetrics r3 = r2.minimumPasswordMetrics     // Catch:{ all -> 0x0041 }
            int r3 = r3.quality     // Catch:{ all -> 0x0041 }
            goto L_0x001d
        L_0x001c:
            r3 = r1
        L_0x001d:
            monitor-exit(r0)     // Catch:{ all -> 0x0041 }
            return r3
        L_0x001f:
            java.util.List r2 = r7.getActiveAdminsForLockscreenPoliciesLocked(r9, r10)     // Catch:{ all -> 0x0041 }
            int r3 = r2.size()     // Catch:{ all -> 0x0041 }
            r4 = 0
        L_0x0029:
            if (r4 >= r3) goto L_0x003f
            java.lang.Object r5 = r2.get(r4)     // Catch:{ all -> 0x0041 }
            com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin r5 = (com.android.server.devicepolicy.DevicePolicyManagerService.ActiveAdmin) r5     // Catch:{ all -> 0x0041 }
            android.app.admin.PasswordMetrics r6 = r5.minimumPasswordMetrics     // Catch:{ all -> 0x0041 }
            int r6 = r6.quality     // Catch:{ all -> 0x0041 }
            if (r1 >= r6) goto L_0x003c
            android.app.admin.PasswordMetrics r6 = r5.minimumPasswordMetrics     // Catch:{ all -> 0x0041 }
            int r6 = r6.quality     // Catch:{ all -> 0x0041 }
            r1 = r6
        L_0x003c:
            int r4 = r4 + 1
            goto L_0x0029
        L_0x003f:
            monitor-exit(r0)     // Catch:{ all -> 0x0041 }
            return r1
        L_0x0041:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0041 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.DevicePolicyManagerService.getPasswordQuality(android.content.ComponentName, int, boolean):int");
    }

    private List<ActiveAdmin> getActiveAdminsForLockscreenPoliciesLocked(int userHandle, boolean parent) {
        if (!parent && isSeparateProfileChallengeEnabled(userHandle)) {
            return getUserDataUnchecked(userHandle).mAdminList;
        }
        ArrayList<ActiveAdmin> admins = new ArrayList<>();
        for (UserInfo userInfo : this.mUserManager.getProfiles(userHandle)) {
            DevicePolicyData policy = getUserData(userInfo.id);
            if (!userInfo.isManagedProfile()) {
                admins.addAll(policy.mAdminList);
            } else {
                boolean hasSeparateChallenge = isSeparateProfileChallengeEnabled(userInfo.id);
                int N = policy.mAdminList.size();
                for (int i = 0; i < N; i++) {
                    ActiveAdmin admin = policy.mAdminList.get(i);
                    if (admin.hasParentActiveAdmin()) {
                        admins.add(admin.getParentActiveAdmin());
                    }
                    if (!hasSeparateChallenge) {
                        admins.add(admin);
                    }
                }
            }
        }
        return admins;
    }

    /* access modifiers changed from: private */
    public boolean isSeparateProfileChallengeEnabled(int userHandle) {
        long ident = this.mInjector.binderClearCallingIdentity();
        try {
            return this.mLockPatternUtils.isSeparateProfileChallengeEnabled(userHandle);
        } finally {
            this.mInjector.binderRestoreCallingIdentity(ident);
        }
    }

    public void setPasswordMinimumLength(ComponentName who, int length, boolean parent) {
        if (this.mHasFeature) {
            Preconditions.checkNotNull(who, "ComponentName is null");
            int userId = this.mInjector.userHandleGetCallingUserId();
            synchronized (getLockObject()) {
                PasswordMetrics metrics = getActiveAdminForCallerLocked(who, 0, parent).minimumPasswordMetrics;
                if (metrics.length != length) {
                    metrics.length = length;
                    updatePasswordValidityCheckpointLocked(userId, parent);
                    saveSettingsLocked(userId);
                }
                maybeLogPasswordComplexitySet(who, userId, parent, metrics);
            }
            DevicePolicyEventLogger.createEvent(2).setAdmin(who).setInt(length).write();
        }
    }

    public int getPasswordMinimumLength(ComponentName who, int userHandle, boolean parent) {
        return getStrictestPasswordRequirement(who, userHandle, parent, $$Lambda$DevicePolicyManagerService$NzTaj70nEECGXhr52RbDyXK_fPU.INSTANCE, 0);
    }

    public void setPasswordHistoryLength(ComponentName who, int length, boolean parent) {
        if (this.mHasFeature && this.mLockPatternUtils.hasSecureLockScreen()) {
            Preconditions.checkNotNull(who, "ComponentName is null");
            int userId = this.mInjector.userHandleGetCallingUserId();
            synchronized (getLockObject()) {
                ActiveAdmin ap = getActiveAdminForCallerLocked(who, 0, parent);
                if (ap.passwordHistoryLength != length) {
                    ap.passwordHistoryLength = length;
                    updatePasswordValidityCheckpointLocked(userId, parent);
                    saveSettingsLocked(userId);
                }
            }
            if (SecurityLog.isLoggingEnabled()) {
                SecurityLog.writeEvent(210018, new Object[]{who.getPackageName(), Integer.valueOf(userId), Integer.valueOf(parent ? getProfileParentId(userId) : userId), Integer.valueOf(length)});
            }
        }
    }

    public int getPasswordHistoryLength(ComponentName who, int userHandle, boolean parent) {
        if (!this.mLockPatternUtils.hasSecureLockScreen()) {
            return 0;
        }
        return getStrictestPasswordRequirement(who, userHandle, parent, $$Lambda$DevicePolicyManagerService$kf4uUzLBApkNlieB7zr8MNfAxbg.INSTANCE, 0);
    }

    public void setPasswordExpirationTimeout(ComponentName who, long timeout, boolean parent) {
        if (this.mHasFeature && this.mLockPatternUtils.hasSecureLockScreen()) {
            Preconditions.checkNotNull(who, "ComponentName is null");
            Preconditions.checkArgumentNonnegative(timeout, "Timeout must be >= 0 ms");
            int userHandle = this.mInjector.userHandleGetCallingUserId();
            synchronized (getLockObject()) {
                ActiveAdmin ap = getActiveAdminForCallerLocked(who, 6, parent);
                long expiration = timeout > 0 ? System.currentTimeMillis() + timeout : 0;
                ap.passwordExpirationDate = expiration;
                ap.passwordExpirationTimeout = timeout;
                if (timeout > 0) {
                    Slog.w(LOG_TAG, "setPasswordExpiration(): password will expire on " + DateFormat.getDateTimeInstance(2, 2).format(new Date(expiration)));
                }
                saveSettingsLocked(userHandle);
                setExpirationAlarmCheckLocked(this.mContext, userHandle, parent);
            }
            if (SecurityLog.isLoggingEnabled()) {
                SecurityLog.writeEvent(210016, new Object[]{who.getPackageName(), Integer.valueOf(userHandle), Integer.valueOf(parent ? getProfileParentId(userHandle) : userHandle), Long.valueOf(timeout)});
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0026, code lost:
        return r5;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public long getPasswordExpirationTimeout(android.content.ComponentName r12, int r13, boolean r14) {
        /*
            r11 = this;
            boolean r0 = r11.mHasFeature
            r1 = 0
            if (r0 == 0) goto L_0x0054
            com.android.internal.widget.LockPatternUtils r0 = r11.mLockPatternUtils
            boolean r0 = r0.hasSecureLockScreen()
            if (r0 != 0) goto L_0x000f
            goto L_0x0054
        L_0x000f:
            r11.enforceFullCrossUsersPermission(r13)
            java.lang.Object r0 = r11.getLockObject()
            monitor-enter(r0)
            r3 = 0
            if (r12 == 0) goto L_0x0027
            com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin r1 = r11.getActiveAdminUncheckedLocked(r12, r13, r14)     // Catch:{ all -> 0x0051 }
            if (r1 == 0) goto L_0x0024
            long r5 = r1.passwordExpirationTimeout     // Catch:{ all -> 0x0051 }
            goto L_0x0025
        L_0x0024:
            r5 = r3
        L_0x0025:
            monitor-exit(r0)     // Catch:{ all -> 0x0051 }
            return r5
        L_0x0027:
            java.util.List r5 = r11.getActiveAdminsForLockscreenPoliciesLocked(r13, r14)     // Catch:{ all -> 0x0051 }
            int r6 = r5.size()     // Catch:{ all -> 0x0051 }
            r7 = 0
        L_0x0031:
            if (r7 >= r6) goto L_0x004f
            java.lang.Object r8 = r5.get(r7)     // Catch:{ all -> 0x0051 }
            com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin r8 = (com.android.server.devicepolicy.DevicePolicyManagerService.ActiveAdmin) r8     // Catch:{ all -> 0x0051 }
            int r9 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r9 == 0) goto L_0x0049
            long r9 = r8.passwordExpirationTimeout     // Catch:{ all -> 0x0051 }
            int r9 = (r9 > r1 ? 1 : (r9 == r1 ? 0 : -1))
            if (r9 == 0) goto L_0x004c
            long r9 = r8.passwordExpirationTimeout     // Catch:{ all -> 0x0051 }
            int r9 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1))
            if (r9 <= 0) goto L_0x004c
        L_0x0049:
            long r9 = r8.passwordExpirationTimeout     // Catch:{ all -> 0x0051 }
            r3 = r9
        L_0x004c:
            int r7 = r7 + 1
            goto L_0x0031
        L_0x004f:
            monitor-exit(r0)     // Catch:{ all -> 0x0051 }
            return r3
        L_0x0051:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0051 }
            throw r1
        L_0x0054:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.DevicePolicyManagerService.getPasswordExpirationTimeout(android.content.ComponentName, int, boolean):long");
    }

    public boolean addCrossProfileWidgetProvider(ComponentName admin, String packageName) {
        int userId = UserHandle.getCallingUserId();
        List<String> changedProviders = null;
        synchronized (getLockObject()) {
            ActiveAdmin activeAdmin = getActiveAdminForCallerLocked(admin, -1);
            if (activeAdmin.crossProfileWidgetProviders == null) {
                activeAdmin.crossProfileWidgetProviders = new ArrayList();
            }
            List<String> providers = activeAdmin.crossProfileWidgetProviders;
            if (!providers.contains(packageName)) {
                providers.add(packageName);
                changedProviders = new ArrayList<>(providers);
                saveSettingsLocked(userId);
            }
        }
        DevicePolicyEventLogger.createEvent(49).setAdmin(admin).write();
        if (changedProviders == null) {
            return false;
        }
        this.mLocalService.notifyCrossProfileProvidersChanged(userId, changedProviders);
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x002f, code lost:
        android.app.admin.DevicePolicyEventLogger.createEvent(com.android.server.hdmi.HdmiCecKeycode.CEC_KEYCODE_F5).setAdmin(r8).write();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x003c, code lost:
        if (r1 == null) goto L_0x0045;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x003e, code lost:
        com.android.server.devicepolicy.DevicePolicyManagerService.LocalService.access$2100(r7.mLocalService, r0, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0044, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0045, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean removeCrossProfileWidgetProvider(android.content.ComponentName r8, java.lang.String r9) {
        /*
            r7 = this;
            int r0 = android.os.UserHandle.getCallingUserId()
            r1 = 0
            java.lang.Object r2 = r7.getLockObject()
            monitor-enter(r2)
            r3 = -1
            com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin r3 = r7.getActiveAdminForCallerLocked(r8, r3)     // Catch:{ all -> 0x0048 }
            java.util.List<java.lang.String> r4 = r3.crossProfileWidgetProviders     // Catch:{ all -> 0x0048 }
            r5 = 0
            if (r4 == 0) goto L_0x0046
            java.util.List<java.lang.String> r4 = r3.crossProfileWidgetProviders     // Catch:{ all -> 0x0048 }
            boolean r4 = r4.isEmpty()     // Catch:{ all -> 0x0048 }
            if (r4 == 0) goto L_0x001d
            goto L_0x0046
        L_0x001d:
            java.util.List<java.lang.String> r4 = r3.crossProfileWidgetProviders     // Catch:{ all -> 0x0048 }
            boolean r6 = r4.remove(r9)     // Catch:{ all -> 0x0048 }
            if (r6 == 0) goto L_0x002e
            java.util.ArrayList r6 = new java.util.ArrayList     // Catch:{ all -> 0x0048 }
            r6.<init>(r4)     // Catch:{ all -> 0x0048 }
            r1 = r6
            r7.saveSettingsLocked(r0)     // Catch:{ all -> 0x0048 }
        L_0x002e:
            monitor-exit(r2)     // Catch:{ all -> 0x0048 }
            r2 = 117(0x75, float:1.64E-43)
            android.app.admin.DevicePolicyEventLogger r2 = android.app.admin.DevicePolicyEventLogger.createEvent(r2)
            android.app.admin.DevicePolicyEventLogger r2 = r2.setAdmin(r8)
            r2.write()
            if (r1 == 0) goto L_0x0045
            com.android.server.devicepolicy.DevicePolicyManagerService$LocalService r2 = r7.mLocalService
            r2.notifyCrossProfileProvidersChanged(r0, r1)
            r2 = 1
            return r2
        L_0x0045:
            return r5
        L_0x0046:
            monitor-exit(r2)     // Catch:{ all -> 0x0048 }
            return r5
        L_0x0048:
            r3 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0048 }
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.DevicePolicyManagerService.removeCrossProfileWidgetProvider(android.content.ComponentName, java.lang.String):boolean");
    }

    public List<String> getCrossProfileWidgetProviders(ComponentName admin) {
        synchronized (getLockObject()) {
            ActiveAdmin activeAdmin = getActiveAdminForCallerLocked(admin, -1);
            if (activeAdmin.crossProfileWidgetProviders != null) {
                if (!activeAdmin.crossProfileWidgetProviders.isEmpty()) {
                    if (this.mInjector.binderIsCallingUidMyUid()) {
                        ArrayList arrayList = new ArrayList(activeAdmin.crossProfileWidgetProviders);
                        return arrayList;
                    }
                    List<String> list = activeAdmin.crossProfileWidgetProviders;
                    return list;
                }
            }
            return null;
        }
    }

    private long getPasswordExpirationLocked(ComponentName who, int userHandle, boolean parent) {
        long timeout = 0;
        if (who != null) {
            ActiveAdmin admin = getActiveAdminUncheckedLocked(who, userHandle, parent);
            if (admin != null) {
                return admin.passwordExpirationDate;
            }
            return 0;
        }
        List<ActiveAdmin> admins = getActiveAdminsForLockscreenPoliciesLocked(userHandle, parent);
        int N = admins.size();
        for (int i = 0; i < N; i++) {
            ActiveAdmin admin2 = admins.get(i);
            if (timeout == 0 || (admin2.passwordExpirationDate != 0 && timeout > admin2.passwordExpirationDate)) {
                timeout = admin2.passwordExpirationDate;
            }
        }
        return timeout;
    }

    public long getPasswordExpiration(ComponentName who, int userHandle, boolean parent) {
        long passwordExpirationLocked;
        if (!this.mHasFeature || !this.mLockPatternUtils.hasSecureLockScreen()) {
            return 0;
        }
        enforceFullCrossUsersPermission(userHandle);
        synchronized (getLockObject()) {
            passwordExpirationLocked = getPasswordExpirationLocked(who, userHandle, parent);
        }
        return passwordExpirationLocked;
    }

    public void setPasswordMinimumUpperCase(ComponentName who, int length, boolean parent) {
        if (this.mHasFeature) {
            Preconditions.checkNotNull(who, "ComponentName is null");
            int userId = this.mInjector.userHandleGetCallingUserId();
            synchronized (getLockObject()) {
                PasswordMetrics metrics = getActiveAdminForCallerLocked(who, 0, parent).minimumPasswordMetrics;
                if (metrics.upperCase != length) {
                    metrics.upperCase = length;
                    updatePasswordValidityCheckpointLocked(userId, parent);
                    saveSettingsLocked(userId);
                }
                maybeLogPasswordComplexitySet(who, userId, parent, metrics);
            }
            DevicePolicyEventLogger.createEvent(7).setAdmin(who).setInt(length).write();
        }
    }

    public int getPasswordMinimumUpperCase(ComponentName who, int userHandle, boolean parent) {
        return getStrictestPasswordRequirement(who, userHandle, parent, $$Lambda$DevicePolicyManagerService$GdvC4eub6BtkkX5BnHuPR5Ob0ag.INSTANCE, 393216);
    }

    public void setPasswordMinimumLowerCase(ComponentName who, int length, boolean parent) {
        Preconditions.checkNotNull(who, "ComponentName is null");
        int userId = this.mInjector.userHandleGetCallingUserId();
        synchronized (getLockObject()) {
            PasswordMetrics metrics = getActiveAdminForCallerLocked(who, 0, parent).minimumPasswordMetrics;
            if (metrics.lowerCase != length) {
                metrics.lowerCase = length;
                updatePasswordValidityCheckpointLocked(userId, parent);
                saveSettingsLocked(userId);
            }
            maybeLogPasswordComplexitySet(who, userId, parent, metrics);
        }
        DevicePolicyEventLogger.createEvent(6).setAdmin(who).setInt(length).write();
    }

    public int getPasswordMinimumLowerCase(ComponentName who, int userHandle, boolean parent) {
        return getStrictestPasswordRequirement(who, userHandle, parent, $$Lambda$DevicePolicyManagerService$O6O5T5aoG6MmH8aAAGYNwYhbtw8.INSTANCE, 393216);
    }

    public void setPasswordMinimumLetters(ComponentName who, int length, boolean parent) {
        if (this.mHasFeature) {
            Preconditions.checkNotNull(who, "ComponentName is null");
            int userId = this.mInjector.userHandleGetCallingUserId();
            synchronized (getLockObject()) {
                PasswordMetrics metrics = getActiveAdminForCallerLocked(who, 0, parent).minimumPasswordMetrics;
                if (metrics.letters != length) {
                    metrics.letters = length;
                    updatePasswordValidityCheckpointLocked(userId, parent);
                    saveSettingsLocked(userId);
                }
                maybeLogPasswordComplexitySet(who, userId, parent, metrics);
            }
            DevicePolicyEventLogger.createEvent(5).setAdmin(who).setInt(length).write();
        }
    }

    public int getPasswordMinimumLetters(ComponentName who, int userHandle, boolean parent) {
        return getStrictestPasswordRequirement(who, userHandle, parent, $$Lambda$DevicePolicyManagerService$tN28Me5AH2pjgYHvPnMAsCjK_NU.INSTANCE, 393216);
    }

    public void setPasswordMinimumNumeric(ComponentName who, int length, boolean parent) {
        if (this.mHasFeature) {
            Preconditions.checkNotNull(who, "ComponentName is null");
            int userId = this.mInjector.userHandleGetCallingUserId();
            synchronized (getLockObject()) {
                PasswordMetrics metrics = getActiveAdminForCallerLocked(who, 0, parent).minimumPasswordMetrics;
                if (metrics.numeric != length) {
                    metrics.numeric = length;
                    updatePasswordValidityCheckpointLocked(userId, parent);
                    saveSettingsLocked(userId);
                }
                maybeLogPasswordComplexitySet(who, userId, parent, metrics);
            }
            DevicePolicyEventLogger.createEvent(3).setAdmin(who).setInt(length).write();
        }
    }

    public int getPasswordMinimumNumeric(ComponentName who, int userHandle, boolean parent) {
        return getStrictestPasswordRequirement(who, userHandle, parent, $$Lambda$DevicePolicyManagerService$BYd2ftVebU2Ktj6trDFfrGE5TE.INSTANCE, 393216);
    }

    public void setPasswordMinimumSymbols(ComponentName who, int length, boolean parent) {
        if (this.mHasFeature) {
            Preconditions.checkNotNull(who, "ComponentName is null");
            int userId = this.mInjector.userHandleGetCallingUserId();
            synchronized (getLockObject()) {
                ActiveAdmin ap = getActiveAdminForCallerLocked(who, 0, parent);
                PasswordMetrics metrics = ap.minimumPasswordMetrics;
                if (metrics.symbols != length) {
                    ap.minimumPasswordMetrics.symbols = length;
                    updatePasswordValidityCheckpointLocked(userId, parent);
                    saveSettingsLocked(userId);
                }
                maybeLogPasswordComplexitySet(who, userId, parent, metrics);
            }
            DevicePolicyEventLogger.createEvent(8).setAdmin(who).setInt(length).write();
        }
    }

    public int getPasswordMinimumSymbols(ComponentName who, int userHandle, boolean parent) {
        return getStrictestPasswordRequirement(who, userHandle, parent, $$Lambda$DevicePolicyManagerService$CClEWCtZQRadOocoqGh0wiKhG4.INSTANCE, 393216);
    }

    public void setPasswordMinimumNonLetter(ComponentName who, int length, boolean parent) {
        if (this.mHasFeature) {
            Preconditions.checkNotNull(who, "ComponentName is null");
            int userId = this.mInjector.userHandleGetCallingUserId();
            synchronized (getLockObject()) {
                ActiveAdmin ap = getActiveAdminForCallerLocked(who, 0, parent);
                PasswordMetrics metrics = ap.minimumPasswordMetrics;
                if (metrics.nonLetter != length) {
                    ap.minimumPasswordMetrics.nonLetter = length;
                    updatePasswordValidityCheckpointLocked(userId, parent);
                    saveSettingsLocked(userId);
                }
                maybeLogPasswordComplexitySet(who, userId, parent, metrics);
            }
            DevicePolicyEventLogger.createEvent(4).setAdmin(who).setInt(length).write();
        }
    }

    public int getPasswordMinimumNonLetter(ComponentName who, int userHandle, boolean parent) {
        return getStrictestPasswordRequirement(who, userHandle, parent, $$Lambda$DevicePolicyManagerService$8nvbMteplUbtaSMuw4DWJMQa4g.INSTANCE, 393216);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0021, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int getStrictestPasswordRequirement(android.content.ComponentName r9, int r10, boolean r11, java.util.function.Function<com.android.server.devicepolicy.DevicePolicyManagerService.ActiveAdmin, java.lang.Integer> r12, int r13) {
        /*
            r8 = this;
            boolean r0 = r8.mHasFeature
            r1 = 0
            if (r0 != 0) goto L_0x0006
            return r1
        L_0x0006:
            r8.enforceFullCrossUsersPermission(r10)
            java.lang.Object r0 = r8.getLockObject()
            monitor-enter(r0)
            if (r9 == 0) goto L_0x0022
            com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin r2 = r8.getActiveAdminUncheckedLocked(r9, r10, r11)     // Catch:{ all -> 0x0052 }
            if (r2 == 0) goto L_0x0020
            java.lang.Object r1 = r12.apply(r2)     // Catch:{ all -> 0x0052 }
            java.lang.Integer r1 = (java.lang.Integer) r1     // Catch:{ all -> 0x0052 }
            int r1 = r1.intValue()     // Catch:{ all -> 0x0052 }
        L_0x0020:
            monitor-exit(r0)     // Catch:{ all -> 0x0052 }
            return r1
        L_0x0022:
            r1 = 0
            java.util.List r2 = r8.getActiveAdminsForLockscreenPoliciesLocked(r10, r11)     // Catch:{ all -> 0x0052 }
            int r3 = r2.size()     // Catch:{ all -> 0x0052 }
            r4 = 0
        L_0x002d:
            if (r4 >= r3) goto L_0x0050
            java.lang.Object r5 = r2.get(r4)     // Catch:{ all -> 0x0052 }
            com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin r5 = (com.android.server.devicepolicy.DevicePolicyManagerService.ActiveAdmin) r5     // Catch:{ all -> 0x0052 }
            boolean r6 = isLimitPasswordAllowed(r5, r13)     // Catch:{ all -> 0x0052 }
            if (r6 != 0) goto L_0x003c
            goto L_0x004d
        L_0x003c:
            java.lang.Object r6 = r12.apply(r5)     // Catch:{ all -> 0x0052 }
            java.lang.Integer r6 = (java.lang.Integer) r6     // Catch:{ all -> 0x0052 }
            int r7 = r6.intValue()     // Catch:{ all -> 0x0052 }
            if (r7 <= r1) goto L_0x004d
            int r7 = r6.intValue()     // Catch:{ all -> 0x0052 }
            r1 = r7
        L_0x004d:
            int r4 = r4 + 1
            goto L_0x002d
        L_0x0050:
            monitor-exit(r0)     // Catch:{ all -> 0x0052 }
            return r1
        L_0x0052:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0052 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.DevicePolicyManagerService.getStrictestPasswordRequirement(android.content.ComponentName, int, boolean, java.util.function.Function, int):int");
    }

    public boolean isActivePasswordSufficient(int userHandle, boolean parent) {
        boolean isActivePasswordSufficientForUserLocked;
        if (!this.mHasFeature) {
            return true;
        }
        enforceFullCrossUsersPermission(userHandle);
        enforceUserUnlocked(userHandle, parent);
        synchronized (getLockObject()) {
            getActiveAdminForCallerLocked((ComponentName) null, 0, parent);
            int credentialOwner = getCredentialOwner(userHandle, parent);
            DevicePolicyData policy = getUserDataUnchecked(credentialOwner);
            isActivePasswordSufficientForUserLocked = isActivePasswordSufficientForUserLocked(policy.mPasswordValidAtLastCheckpoint, getUserPasswordMetricsLocked(credentialOwner), userHandle, parent);
        }
        return isActivePasswordSufficientForUserLocked;
    }

    public boolean isUsingUnifiedPassword(ComponentName admin) {
        if (!this.mHasFeature) {
            return true;
        }
        int userId = this.mInjector.userHandleGetCallingUserId();
        enforceProfileOrDeviceOwner(admin);
        enforceManagedProfile(userId, "query unified challenge status");
        return true ^ isSeparateProfileChallengeEnabled(userId);
    }

    public boolean isProfileActivePasswordSufficientForParent(int userHandle) {
        boolean isActivePasswordSufficientForUserLocked;
        if (!this.mHasFeature) {
            return true;
        }
        enforceFullCrossUsersPermission(userHandle);
        enforceManagedProfile(userHandle, "call APIs refering to the parent profile");
        synchronized (getLockObject()) {
            int targetUser = getProfileParentId(userHandle);
            enforceUserUnlocked(targetUser, false);
            int credentialOwner = getCredentialOwner(userHandle, false);
            DevicePolicyData policy = getUserDataUnchecked(credentialOwner);
            isActivePasswordSufficientForUserLocked = isActivePasswordSufficientForUserLocked(policy.mPasswordValidAtLastCheckpoint, getUserPasswordMetricsLocked(credentialOwner), targetUser, false);
        }
        return isActivePasswordSufficientForUserLocked;
    }

    private boolean isActivePasswordSufficientForUserLocked(boolean passwordValidAtLastCheckpoint, PasswordMetrics metrics, int userHandle, boolean parent) {
        if (!this.mInjector.storageManagerIsFileBasedEncryptionEnabled() && metrics == null) {
            return passwordValidAtLastCheckpoint;
        }
        if (metrics == null) {
            metrics = new PasswordMetrics();
        }
        return isPasswordSufficientForUserWithoutCheckpointLocked(metrics, userHandle, parent);
    }

    private boolean isPasswordSufficientForUserWithoutCheckpointLocked(PasswordMetrics metrics, int userId, boolean parent) {
        int requiredQuality = getPasswordQuality((ComponentName) null, userId, parent);
        if (requiredQuality >= 131072 && metrics.length < getPasswordMinimumLength((ComponentName) null, userId, parent)) {
            return false;
        }
        if (requiredQuality == 393216) {
            if (metrics.upperCase < getPasswordMinimumUpperCase((ComponentName) null, userId, parent) || metrics.lowerCase < getPasswordMinimumLowerCase((ComponentName) null, userId, parent) || metrics.letters < getPasswordMinimumLetters((ComponentName) null, userId, parent) || metrics.numeric < getPasswordMinimumNumeric((ComponentName) null, userId, parent) || metrics.symbols < getPasswordMinimumSymbols((ComponentName) null, userId, parent) || metrics.nonLetter < getPasswordMinimumNonLetter((ComponentName) null, userId, parent)) {
                return false;
            }
            return true;
        } else if (metrics.quality >= requiredQuality) {
            return true;
        } else {
            return false;
        }
    }

    public int getPasswordComplexity() {
        int i;
        DevicePolicyEventLogger.createEvent(72).setStrings(this.mInjector.getPackageManager().getPackagesForUid(this.mInjector.binderGetCallingUid())).write();
        int callingUserId = this.mInjector.userHandleGetCallingUserId();
        enforceUserUnlocked(callingUserId);
        this.mContext.enforceCallingOrSelfPermission("android.permission.REQUEST_PASSWORD_COMPLEXITY", "Must have android.permission.REQUEST_PASSWORD_COMPLEXITY permission.");
        synchronized (getLockObject()) {
            i = 0;
            PasswordMetrics metrics = getUserPasswordMetricsLocked(getCredentialOwner(callingUserId, false));
            if (metrics != null) {
                i = metrics.determineComplexity();
            }
        }
        return i;
    }

    public int getCurrentFailedPasswordAttempts(int userHandle, boolean parent) {
        int i;
        if (!this.mLockPatternUtils.hasSecureLockScreen()) {
            return 0;
        }
        enforceFullCrossUsersPermission(userHandle);
        synchronized (getLockObject()) {
            if (!isCallerWithSystemUid() && this.mContext.checkCallingPermission("android.permission.ACCESS_KEYGUARD_SECURE_STORAGE") != 0) {
                getActiveAdminForCallerLocked((ComponentName) null, 1, parent);
            }
            i = getUserDataUnchecked(getCredentialOwner(userHandle, parent)).mFailedPasswordAttempts;
        }
        return i;
    }

    public void setMaximumFailedPasswordsForWipe(ComponentName who, int num, boolean parent) {
        if (this.mHasFeature && this.mLockPatternUtils.hasSecureLockScreen()) {
            Preconditions.checkNotNull(who, "ComponentName is null");
            int userId = this.mInjector.userHandleGetCallingUserId();
            synchronized (getLockObject()) {
                getActiveAdminForCallerLocked(who, 4, parent);
                ActiveAdmin ap = getActiveAdminForCallerLocked(who, 1, parent);
                if (ap.maximumFailedPasswordsForWipe != num) {
                    ap.maximumFailedPasswordsForWipe = num;
                    saveSettingsLocked(userId);
                }
            }
            if (SecurityLog.isLoggingEnabled()) {
                SecurityLog.writeEvent(210020, new Object[]{who.getPackageName(), Integer.valueOf(userId), Integer.valueOf(parent ? getProfileParentId(userId) : userId), Integer.valueOf(num)});
            }
        }
    }

    public int getMaximumFailedPasswordsForWipe(ComponentName who, int userHandle, boolean parent) {
        ActiveAdmin admin;
        int i = 0;
        if (!this.mHasFeature || !this.mLockPatternUtils.hasSecureLockScreen()) {
            return 0;
        }
        enforceFullCrossUsersPermission(userHandle);
        synchronized (getLockObject()) {
            if (who != null) {
                admin = getActiveAdminUncheckedLocked(who, userHandle, parent);
            } else {
                admin = getAdminWithMinimumFailedPasswordsForWipeLocked(userHandle, parent);
            }
            if (admin != null) {
                i = admin.maximumFailedPasswordsForWipe;
            }
        }
        return i;
    }

    public int getProfileWithMinimumFailedPasswordsForWipe(int userHandle, boolean parent) {
        boolean z = this.mHasFeature;
        int i = ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION;
        if (!z || !this.mLockPatternUtils.hasSecureLockScreen()) {
            return ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION;
        }
        enforceFullCrossUsersPermission(userHandle);
        synchronized (getLockObject()) {
            ActiveAdmin admin = getAdminWithMinimumFailedPasswordsForWipeLocked(userHandle, parent);
            if (admin != null) {
                i = admin.getUserHandle().getIdentifier();
            }
        }
        return i;
    }

    private ActiveAdmin getAdminWithMinimumFailedPasswordsForWipeLocked(int userHandle, boolean parent) {
        int count = 0;
        ActiveAdmin strictestAdmin = null;
        List<ActiveAdmin> admins = getActiveAdminsForLockscreenPoliciesLocked(userHandle, parent);
        int N = admins.size();
        for (int i = 0; i < N; i++) {
            ActiveAdmin admin = admins.get(i);
            if (admin.maximumFailedPasswordsForWipe != 0) {
                int userId = admin.getUserHandle().getIdentifier();
                if (count == 0 || count > admin.maximumFailedPasswordsForWipe || (count == admin.maximumFailedPasswordsForWipe && getUserInfo(userId).isPrimary())) {
                    count = admin.maximumFailedPasswordsForWipe;
                    strictestAdmin = admin;
                }
            }
        }
        return strictestAdmin;
    }

    private UserInfo getUserInfo(int userId) {
        long token = this.mInjector.binderClearCallingIdentity();
        try {
            return this.mUserManager.getUserInfo(userId);
        } finally {
            this.mInjector.binderRestoreCallingIdentity(token);
        }
    }

    private boolean canPOorDOCallResetPassword(ActiveAdmin admin, int userId) {
        return getTargetSdk(admin.info.getPackageName(), userId) < 26;
    }

    /* access modifiers changed from: private */
    public boolean canUserHaveUntrustedCredentialReset(int userId) {
        synchronized (getLockObject()) {
            Iterator<ActiveAdmin> it = getUserData(userId).mAdminList.iterator();
            while (it.hasNext()) {
                ActiveAdmin admin = it.next();
                if (isActiveAdminWithPolicyForUserLocked(admin, -1, userId)) {
                    if (canPOorDOCallResetPassword(admin, userId)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 11 */
    public boolean resetPassword(String passwordOrNull, int flags) throws RemoteException {
        boolean preN;
        if (!this.mLockPatternUtils.hasSecureLockScreen()) {
            Slog.w(LOG_TAG, "Cannot reset password when the device has no lock screen");
            return false;
        }
        int callingUid = this.mInjector.binderGetCallingUid();
        int userHandle = this.mInjector.userHandleGetCallingUserId();
        String password = passwordOrNull != null ? passwordOrNull : "";
        if (TextUtils.isEmpty(password)) {
            enforceNotManagedProfile(userHandle, "clear the active password");
        }
        synchronized (getLockObject()) {
            ActiveAdmin admin = getActiveAdminWithPolicyForUidLocked((ComponentName) null, -1, callingUid);
            boolean z = true;
            if (admin == null) {
                ActiveAdmin admin2 = getActiveAdminOrCheckPermissionForCallerLocked((ComponentName) null, 2, "android.permission.RESET_PASSWORD");
                if (admin2 == null || getTargetSdk(admin2.info.getPackageName(), userHandle) > 23) {
                    z = false;
                }
                preN = z;
                if (TextUtils.isEmpty(password)) {
                    if (preN) {
                        Slog.e(LOG_TAG, "Cannot call with null password");
                        return false;
                    }
                    throw new SecurityException("Cannot call with null password");
                } else if (isLockScreenSecureUnchecked(userHandle)) {
                    if (preN) {
                        Slog.e(LOG_TAG, "Cannot change current password");
                        return false;
                    }
                    throw new SecurityException("Cannot change current password");
                }
            } else if (canPOorDOCallResetPassword(admin, userHandle)) {
                if (getTargetSdk(admin.info.getPackageName(), userHandle) > 23) {
                    z = false;
                }
                preN = z;
            } else {
                throw new SecurityException("resetPassword() is deprecated for DPC targeting O or later");
            }
            if (!isManagedProfile(userHandle)) {
                for (UserInfo userInfo : this.mUserManager.getProfiles(userHandle)) {
                    if (userInfo.isManagedProfile()) {
                        if (preN) {
                            Slog.e(LOG_TAG, "Cannot reset password on user has managed profile");
                            return false;
                        }
                        throw new IllegalStateException("Cannot reset password on user has managed profile");
                    }
                }
            }
            if (this.mUserManager.isUserUnlocked(userHandle)) {
                return resetPasswordInternal(password, 0, (byte[]) null, flags, callingUid, userHandle);
            }
            if (preN) {
                Slog.e(LOG_TAG, "Cannot reset password when user is locked");
                return false;
            }
            throw new IllegalStateException("Cannot reset password when user is locked");
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 19 */
    /* JADX WARNING: type inference failed for: r14v0 */
    /* JADX WARNING: type inference failed for: r14v2 */
    /* JADX WARNING: type inference failed for: r14v18 */
    /* JADX WARNING: type inference failed for: r14v19 */
    /* JADX WARNING: type inference failed for: r14v23 */
    /* JADX WARNING: Code restructure failed: missing block: B:100:0x0246, code lost:
        return r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:106:0x024a, code lost:
        r0 = th;
        r14 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x0193, code lost:
        r12 = getUserData(r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x0199, code lost:
        if (r12.mPasswordOwner < 0) goto L_0x01a9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x019d, code lost:
        if (r12.mPasswordOwner == r2) goto L_0x01a9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x019f, code lost:
        android.util.Slog.w(LOG_TAG, "resetPassword: already set by another uid and not entered by user");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x01a8, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x01a9, code lost:
        r14 = 0;
        r15 = isCallerDeviceOwner(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x01b1, code lost:
        if ((r24 & 2) == 0) goto L_0x01b5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x01b3, code lost:
        r3 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x01b5, code lost:
        r3 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x01b6, code lost:
        r16 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x01b8, code lost:
        if (r15 == false) goto L_0x01bf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x01ba, code lost:
        if (r16 == false) goto L_0x01bf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x01bc, code lost:
        setDoNotAskCredentialsOnBoot();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x01bf, code lost:
        r8 = r1.mInjector.binderClearCallingIdentity();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x01c5, code lost:
        if (r23 != null) goto L_0x01f1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x01cb, code lost:
        if (android.text.TextUtils.isEmpty(r20) != false) goto L_0x01e0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x01d3, code lost:
        r18 = r15;
        r14 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:0x01dc, code lost:
        r14 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:?, code lost:
        r1.mLockPatternUtils.saveLockPassword(r20.getBytes(), (byte[]) null, r13, r26, true);
        r14 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:75:0x01e0, code lost:
        r18 = r15;
        r14 = r8;
        r1.mLockPatternUtils.clearLock((byte[]) null, r11, true);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:76:0x01e8, code lost:
        r3 = true;
        r0 = true;
        r14 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:77:0x01ec, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:78:0x01ed, code lost:
        r18 = r15;
        r14 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:79:0x01f1, code lost:
        r18 = r15;
        r14 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:80:0x01f8, code lost:
        if (android.text.TextUtils.isEmpty(r20) != false) goto L_0x020e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:81:0x01fa, code lost:
        r0 = true;
        r3 = r1.mLockPatternUtils.setLockCredentialWithToken(r20.getBytes(), 2, r13, r21, r23, r26);
        r14 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:82:0x020e, code lost:
        r0 = true;
        r3 = r1.mLockPatternUtils.setLockCredentialWithToken((byte[]) null, -1, r13, r21, r23, r26);
        r14 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:0x0220, code lost:
        if ((r24 & 1) == 0) goto L_0x0223;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:86:0x0223, code lost:
        r0 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:87:0x0224, code lost:
        r4 = r0;
        r0 = -1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:88:0x0226, code lost:
        if (r4 == false) goto L_0x022e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:89:0x0228, code lost:
        r1.mLockPatternUtils.requireStrongAuth(2, -1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:90:0x022e, code lost:
        r5 = getLockObject();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:91:0x0232, code lost:
        monitor-enter(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:92:0x0233, code lost:
        if (r4 == false) goto L_0x0236;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:93:0x0235, code lost:
        r0 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:96:0x0238, code lost:
        if (r12.mPasswordOwner == r0) goto L_0x023f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:97:0x023a, code lost:
        r12.mPasswordOwner = r0;
        saveSettingsLocked(r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:98:0x023f, code lost:
        monitor-exit(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:99:0x0240, code lost:
        r1.mInjector.binderRestoreCallingIdentity(r14);
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean resetPasswordInternal(java.lang.String r20, long r21, byte[] r23, int r24, int r25, int r26) {
        /*
            r19 = this;
            r1 = r19
            r2 = r25
            r11 = r26
            java.lang.Object r3 = r19.getLockObject()
            monitor-enter(r3)
            r0 = 0
            r12 = 0
            int r4 = r1.getPasswordQuality(r0, r11, r12)     // Catch:{ all -> 0x0251 }
            r5 = 524288(0x80000, float:7.34684E-40)
            if (r4 != r5) goto L_0x0016
            r4 = 0
        L_0x0016:
            byte[] r5 = r20.getBytes()     // Catch:{ all -> 0x0251 }
            android.app.admin.PasswordMetrics r5 = android.app.admin.PasswordMetrics.computeForPassword(r5)     // Catch:{ all -> 0x0251 }
            int r6 = r5.quality     // Catch:{ all -> 0x0251 }
            r7 = 393216(0x60000, float:5.51013E-40)
            if (r6 >= r4) goto L_0x004f
            if (r4 == r7) goto L_0x004f
            java.lang.String r0 = "DevicePolicyManager"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x0251 }
            r7.<init>()     // Catch:{ all -> 0x0251 }
            java.lang.String r8 = "resetPassword: password quality 0x"
            r7.append(r8)     // Catch:{ all -> 0x0251 }
            java.lang.String r8 = java.lang.Integer.toHexString(r6)     // Catch:{ all -> 0x0251 }
            r7.append(r8)     // Catch:{ all -> 0x0251 }
            java.lang.String r8 = " does not meet required quality 0x"
            r7.append(r8)     // Catch:{ all -> 0x0251 }
            java.lang.String r8 = java.lang.Integer.toHexString(r4)     // Catch:{ all -> 0x0251 }
            r7.append(r8)     // Catch:{ all -> 0x0251 }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x0251 }
            android.util.Slog.w(r0, r7)     // Catch:{ all -> 0x0251 }
            monitor-exit(r3)     // Catch:{ all -> 0x0251 }
            return r12
        L_0x004f:
            int r8 = java.lang.Math.max(r6, r4)     // Catch:{ all -> 0x0251 }
            r13 = r8
            int r4 = r1.getPasswordMinimumLength(r0, r11, r12)     // Catch:{ all -> 0x0251 }
            int r8 = r20.length()     // Catch:{ all -> 0x0251 }
            if (r8 >= r4) goto L_0x0083
            java.lang.String r0 = "DevicePolicyManager"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x0251 }
            r7.<init>()     // Catch:{ all -> 0x0251 }
            java.lang.String r8 = "resetPassword: password length "
            r7.append(r8)     // Catch:{ all -> 0x0251 }
            int r8 = r20.length()     // Catch:{ all -> 0x0251 }
            r7.append(r8)     // Catch:{ all -> 0x0251 }
            java.lang.String r8 = " does not meet required length "
            r7.append(r8)     // Catch:{ all -> 0x0251 }
            r7.append(r4)     // Catch:{ all -> 0x0251 }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x0251 }
            android.util.Slog.w(r0, r7)     // Catch:{ all -> 0x0251 }
            monitor-exit(r3)     // Catch:{ all -> 0x0251 }
            return r12
        L_0x0083:
            if (r13 != r7) goto L_0x0190
            int r7 = r1.getPasswordMinimumLetters(r0, r11, r12)     // Catch:{ all -> 0x0251 }
            int r8 = r5.letters     // Catch:{ all -> 0x0251 }
            if (r8 >= r7) goto L_0x00b0
            java.lang.String r0 = "DevicePolicyManager"
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x0251 }
            r8.<init>()     // Catch:{ all -> 0x0251 }
            java.lang.String r9 = "resetPassword: number of letters "
            r8.append(r9)     // Catch:{ all -> 0x0251 }
            int r9 = r5.letters     // Catch:{ all -> 0x0251 }
            r8.append(r9)     // Catch:{ all -> 0x0251 }
            java.lang.String r9 = " does not meet required number of letters "
            r8.append(r9)     // Catch:{ all -> 0x0251 }
            r8.append(r7)     // Catch:{ all -> 0x0251 }
            java.lang.String r8 = r8.toString()     // Catch:{ all -> 0x0251 }
            android.util.Slog.w(r0, r8)     // Catch:{ all -> 0x0251 }
            monitor-exit(r3)     // Catch:{ all -> 0x0251 }
            return r12
        L_0x00b0:
            int r8 = r1.getPasswordMinimumNumeric(r0, r11, r12)     // Catch:{ all -> 0x0251 }
            int r9 = r5.numeric     // Catch:{ all -> 0x0251 }
            if (r9 >= r8) goto L_0x00db
            java.lang.String r0 = "DevicePolicyManager"
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x0251 }
            r9.<init>()     // Catch:{ all -> 0x0251 }
            java.lang.String r10 = "resetPassword: number of numerical digits "
            r9.append(r10)     // Catch:{ all -> 0x0251 }
            int r10 = r5.numeric     // Catch:{ all -> 0x0251 }
            r9.append(r10)     // Catch:{ all -> 0x0251 }
            java.lang.String r10 = " does not meet required number of numerical digits "
            r9.append(r10)     // Catch:{ all -> 0x0251 }
            r9.append(r8)     // Catch:{ all -> 0x0251 }
            java.lang.String r9 = r9.toString()     // Catch:{ all -> 0x0251 }
            android.util.Slog.w(r0, r9)     // Catch:{ all -> 0x0251 }
            monitor-exit(r3)     // Catch:{ all -> 0x0251 }
            return r12
        L_0x00db:
            int r9 = r1.getPasswordMinimumLowerCase(r0, r11, r12)     // Catch:{ all -> 0x0251 }
            int r10 = r5.lowerCase     // Catch:{ all -> 0x0251 }
            if (r10 >= r9) goto L_0x0106
            java.lang.String r0 = "DevicePolicyManager"
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x0251 }
            r10.<init>()     // Catch:{ all -> 0x0251 }
            java.lang.String r14 = "resetPassword: number of lowercase letters "
            r10.append(r14)     // Catch:{ all -> 0x0251 }
            int r14 = r5.lowerCase     // Catch:{ all -> 0x0251 }
            r10.append(r14)     // Catch:{ all -> 0x0251 }
            java.lang.String r14 = " does not meet required number of lowercase letters "
            r10.append(r14)     // Catch:{ all -> 0x0251 }
            r10.append(r9)     // Catch:{ all -> 0x0251 }
            java.lang.String r10 = r10.toString()     // Catch:{ all -> 0x0251 }
            android.util.Slog.w(r0, r10)     // Catch:{ all -> 0x0251 }
            monitor-exit(r3)     // Catch:{ all -> 0x0251 }
            return r12
        L_0x0106:
            int r10 = r1.getPasswordMinimumUpperCase(r0, r11, r12)     // Catch:{ all -> 0x0251 }
            int r14 = r5.upperCase     // Catch:{ all -> 0x0251 }
            if (r14 >= r10) goto L_0x0131
            java.lang.String r0 = "DevicePolicyManager"
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ all -> 0x0251 }
            r14.<init>()     // Catch:{ all -> 0x0251 }
            java.lang.String r15 = "resetPassword: number of uppercase letters "
            r14.append(r15)     // Catch:{ all -> 0x0251 }
            int r15 = r5.upperCase     // Catch:{ all -> 0x0251 }
            r14.append(r15)     // Catch:{ all -> 0x0251 }
            java.lang.String r15 = " does not meet required number of uppercase letters "
            r14.append(r15)     // Catch:{ all -> 0x0251 }
            r14.append(r10)     // Catch:{ all -> 0x0251 }
            java.lang.String r14 = r14.toString()     // Catch:{ all -> 0x0251 }
            android.util.Slog.w(r0, r14)     // Catch:{ all -> 0x0251 }
            monitor-exit(r3)     // Catch:{ all -> 0x0251 }
            return r12
        L_0x0131:
            int r14 = r1.getPasswordMinimumSymbols(r0, r11, r12)     // Catch:{ all -> 0x0251 }
            int r15 = r5.symbols     // Catch:{ all -> 0x0251 }
            if (r15 >= r14) goto L_0x015d
            java.lang.String r0 = "DevicePolicyManager"
            java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch:{ all -> 0x0251 }
            r15.<init>()     // Catch:{ all -> 0x0251 }
            java.lang.String r12 = "resetPassword: number of special symbols "
            r15.append(r12)     // Catch:{ all -> 0x0251 }
            int r12 = r5.symbols     // Catch:{ all -> 0x0251 }
            r15.append(r12)     // Catch:{ all -> 0x0251 }
            java.lang.String r12 = " does not meet required number of special symbols "
            r15.append(r12)     // Catch:{ all -> 0x0251 }
            r15.append(r14)     // Catch:{ all -> 0x0251 }
            java.lang.String r12 = r15.toString()     // Catch:{ all -> 0x0251 }
            android.util.Slog.w(r0, r12)     // Catch:{ all -> 0x0251 }
            monitor-exit(r3)     // Catch:{ all -> 0x0251 }
            r0 = 0
            return r0
        L_0x015d:
            r12 = 0
            int r15 = r1.getPasswordMinimumNonLetter(r0, r11, r12)     // Catch:{ all -> 0x0251 }
            r12 = r15
            int r15 = r5.nonLetter     // Catch:{ all -> 0x0251 }
            if (r15 >= r12) goto L_0x018d
            java.lang.String r0 = "DevicePolicyManager"
            java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch:{ all -> 0x0251 }
            r15.<init>()     // Catch:{ all -> 0x0251 }
            r17 = r4
            java.lang.String r4 = "resetPassword: number of non-letter characters "
            r15.append(r4)     // Catch:{ all -> 0x0251 }
            int r4 = r5.nonLetter     // Catch:{ all -> 0x0251 }
            r15.append(r4)     // Catch:{ all -> 0x0251 }
            java.lang.String r4 = " does not meet required number of non-letter characters "
            r15.append(r4)     // Catch:{ all -> 0x0251 }
            r15.append(r12)     // Catch:{ all -> 0x0251 }
            java.lang.String r4 = r15.toString()     // Catch:{ all -> 0x0251 }
            android.util.Slog.w(r0, r4)     // Catch:{ all -> 0x0251 }
            monitor-exit(r3)     // Catch:{ all -> 0x0251 }
            r0 = 0
            return r0
        L_0x018d:
            r17 = r4
            goto L_0x0192
        L_0x0190:
            r17 = r4
        L_0x0192:
            monitor-exit(r3)     // Catch:{ all -> 0x0251 }
            com.android.server.devicepolicy.DevicePolicyManagerService$DevicePolicyData r12 = r1.getUserData(r11)
            int r3 = r12.mPasswordOwner
            if (r3 < 0) goto L_0x01a9
            int r3 = r12.mPasswordOwner
            if (r3 == r2) goto L_0x01a9
            java.lang.String r0 = "DevicePolicyManager"
            java.lang.String r3 = "resetPassword: already set by another uid and not entered by user"
            android.util.Slog.w(r0, r3)
            r14 = 0
            return r14
        L_0x01a9:
            r14 = 0
            boolean r15 = r1.isCallerDeviceOwner(r2)
            r3 = r24 & 2
            r10 = 1
            if (r3 == 0) goto L_0x01b5
            r3 = r10
            goto L_0x01b6
        L_0x01b5:
            r3 = r14
        L_0x01b6:
            r16 = r3
            if (r15 == 0) goto L_0x01bf
            if (r16 == 0) goto L_0x01bf
            r19.setDoNotAskCredentialsOnBoot()
        L_0x01bf:
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r3 = r1.mInjector
            long r8 = r3.binderClearCallingIdentity()
            if (r23 != 0) goto L_0x01f1
            boolean r3 = android.text.TextUtils.isEmpty(r20)     // Catch:{ all -> 0x01ec }
            if (r3 != 0) goto L_0x01e0
            com.android.internal.widget.LockPatternUtils r3 = r1.mLockPatternUtils     // Catch:{ all -> 0x01ec }
            byte[] r4 = r20.getBytes()     // Catch:{ all -> 0x01ec }
            r5 = 0
            r0 = 1
            r6 = r13
            r7 = r26
            r18 = r15
            r14 = r8
            r8 = r0
            r3.saveLockPassword(r4, r5, r6, r7, r8)     // Catch:{ all -> 0x024a }
            goto L_0x01e8
        L_0x01e0:
            r18 = r15
            r14 = r8
            com.android.internal.widget.LockPatternUtils r3 = r1.mLockPatternUtils     // Catch:{ all -> 0x024a }
            r3.clearLock(r0, r11, r10)     // Catch:{ all -> 0x024a }
        L_0x01e8:
            r0 = 1
            r3 = r0
            r0 = r10
            goto L_0x021e
        L_0x01ec:
            r0 = move-exception
            r18 = r15
            r14 = r8
            goto L_0x024b
        L_0x01f1:
            r18 = r15
            r14 = r8
            boolean r0 = android.text.TextUtils.isEmpty(r20)     // Catch:{ all -> 0x024a }
            if (r0 != 0) goto L_0x020e
            com.android.internal.widget.LockPatternUtils r3 = r1.mLockPatternUtils     // Catch:{ all -> 0x024a }
            byte[] r4 = r20.getBytes()     // Catch:{ all -> 0x024a }
            r5 = 2
            r6 = r13
            r7 = r21
            r9 = r23
            r0 = r10
            r10 = r26
            boolean r3 = r3.setLockCredentialWithToken(r4, r5, r6, r7, r9, r10)     // Catch:{ all -> 0x024a }
            goto L_0x021e
        L_0x020e:
            r0 = r10
            com.android.internal.widget.LockPatternUtils r3 = r1.mLockPatternUtils     // Catch:{ all -> 0x024a }
            r4 = 0
            r5 = -1
            r6 = r13
            r7 = r21
            r9 = r23
            r10 = r26
            boolean r3 = r3.setLockCredentialWithToken(r4, r5, r6, r7, r9, r10)     // Catch:{ all -> 0x024a }
        L_0x021e:
            r4 = r24 & 1
            if (r4 == 0) goto L_0x0223
            goto L_0x0224
        L_0x0223:
            r0 = 0
        L_0x0224:
            r4 = r0
            r0 = -1
            if (r4 == 0) goto L_0x022e
            com.android.internal.widget.LockPatternUtils r5 = r1.mLockPatternUtils     // Catch:{ all -> 0x024a }
            r6 = 2
            r5.requireStrongAuth(r6, r0)     // Catch:{ all -> 0x024a }
        L_0x022e:
            java.lang.Object r5 = r19.getLockObject()     // Catch:{ all -> 0x024a }
            monitor-enter(r5)     // Catch:{ all -> 0x024a }
            if (r4 == 0) goto L_0x0236
            r0 = r2
        L_0x0236:
            int r6 = r12.mPasswordOwner     // Catch:{ all -> 0x0247 }
            if (r6 == r0) goto L_0x023f
            r12.mPasswordOwner = r0     // Catch:{ all -> 0x0247 }
            r1.saveSettingsLocked(r11)     // Catch:{ all -> 0x0247 }
        L_0x023f:
            monitor-exit(r5)     // Catch:{ all -> 0x0247 }
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r0 = r1.mInjector
            r0.binderRestoreCallingIdentity(r14)
            return r3
        L_0x0247:
            r0 = move-exception
            monitor-exit(r5)     // Catch:{ all -> 0x0247 }
            throw r0     // Catch:{ all -> 0x024a }
        L_0x024a:
            r0 = move-exception
        L_0x024b:
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r3 = r1.mInjector
            r3.binderRestoreCallingIdentity(r14)
            throw r0
        L_0x0251:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x0251 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.DevicePolicyManagerService.resetPasswordInternal(java.lang.String, long, byte[], int, int, int):boolean");
    }

    private boolean isLockScreenSecureUnchecked(int userId) {
        long ident = this.mInjector.binderClearCallingIdentity();
        try {
            return this.mLockPatternUtils.isSecure(userId);
        } finally {
            this.mInjector.binderRestoreCallingIdentity(ident);
        }
    }

    private void setDoNotAskCredentialsOnBoot() {
        synchronized (getLockObject()) {
            DevicePolicyData policyData = getUserData(0);
            if (!policyData.doNotAskCredentialsOnBoot) {
                policyData.doNotAskCredentialsOnBoot = true;
                saveSettingsLocked(0);
            }
        }
    }

    public boolean getDoNotAskCredentialsOnBoot() {
        boolean z;
        this.mContext.enforceCallingOrSelfPermission("android.permission.QUERY_DO_NOT_ASK_CREDENTIALS_ON_BOOT", (String) null);
        synchronized (getLockObject()) {
            z = getUserData(0).doNotAskCredentialsOnBoot;
        }
        return z;
    }

    public void setMaximumTimeToLock(ComponentName who, long timeMs, boolean parent) {
        if (this.mHasFeature) {
            Preconditions.checkNotNull(who, "ComponentName is null");
            int userHandle = this.mInjector.userHandleGetCallingUserId();
            synchronized (getLockObject()) {
                ActiveAdmin ap = getActiveAdminForCallerLocked(who, 3, parent);
                if (ap.maximumTimeToUnlock != timeMs) {
                    ap.maximumTimeToUnlock = timeMs;
                    saveSettingsLocked(userHandle);
                    updateMaximumTimeToLockLocked(userHandle);
                }
            }
            if (SecurityLog.isLoggingEnabled()) {
                SecurityLog.writeEvent(210019, new Object[]{who.getPackageName(), Integer.valueOf(userHandle), Integer.valueOf(parent ? getProfileParentId(userHandle) : userHandle), Long.valueOf(timeMs)});
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateMaximumTimeToLockLocked(int userId) {
        if (isManagedProfile(userId)) {
            updateProfileLockTimeoutLocked(userId);
        }
        long ident = this.mInjector.binderClearCallingIdentity();
        try {
            int parentId = getProfileParentId(userId);
            long timeMs = getMaximumTimeToLockPolicyFromAdmins(getActiveAdminsForLockscreenPoliciesLocked(parentId, false));
            DevicePolicyData policy = getUserDataUnchecked(parentId);
            if (policy.mLastMaximumTimeToLock != timeMs) {
                policy.mLastMaximumTimeToLock = timeMs;
                if (policy.mLastMaximumTimeToLock != JobStatus.NO_LATEST_RUNTIME) {
                    this.mInjector.settingsGlobalPutInt("stay_on_while_plugged_in", 0);
                }
                getPowerManagerInternal().setMaximumScreenOffTimeoutFromDeviceAdmin(0, timeMs);
                this.mInjector.binderRestoreCallingIdentity(ident);
            }
        } finally {
            this.mInjector.binderRestoreCallingIdentity(ident);
        }
    }

    private void updateProfileLockTimeoutLocked(int userId) {
        long timeMs;
        if (isSeparateProfileChallengeEnabled(userId)) {
            timeMs = getMaximumTimeToLockPolicyFromAdmins(getActiveAdminsForLockscreenPoliciesLocked(userId, false));
        } else {
            timeMs = JobStatus.NO_LATEST_RUNTIME;
        }
        DevicePolicyData policy = getUserDataUnchecked(userId);
        if (policy.mLastMaximumTimeToLock != timeMs) {
            policy.mLastMaximumTimeToLock = timeMs;
            long ident = this.mInjector.binderClearCallingIdentity();
            try {
                getPowerManagerInternal().setMaximumScreenOffTimeoutFromDeviceAdmin(userId, policy.mLastMaximumTimeToLock);
            } finally {
                this.mInjector.binderRestoreCallingIdentity(ident);
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x001a, code lost:
        return r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x002f, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public long getMaximumTimeToLock(android.content.ComponentName r9, int r10, boolean r11) {
        /*
            r8 = this;
            boolean r0 = r8.mHasFeature
            r1 = 0
            if (r0 != 0) goto L_0x0007
            return r1
        L_0x0007:
            r8.enforceFullCrossUsersPermission(r10)
            java.lang.Object r0 = r8.getLockObject()
            monitor-enter(r0)
            if (r9 == 0) goto L_0x001b
            com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin r3 = r8.getActiveAdminUncheckedLocked(r9, r10, r11)     // Catch:{ all -> 0x0030 }
            if (r3 == 0) goto L_0x0019
            long r1 = r3.maximumTimeToUnlock     // Catch:{ all -> 0x0030 }
        L_0x0019:
            monitor-exit(r0)     // Catch:{ all -> 0x0030 }
            return r1
        L_0x001b:
            java.util.List r3 = r8.getActiveAdminsForLockscreenPoliciesLocked(r10, r11)     // Catch:{ all -> 0x0030 }
            long r4 = r8.getMaximumTimeToLockPolicyFromAdmins(r3)     // Catch:{ all -> 0x0030 }
            r6 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            int r6 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r6 != 0) goto L_0x002d
            goto L_0x002e
        L_0x002d:
            r1 = r4
        L_0x002e:
            monitor-exit(r0)     // Catch:{ all -> 0x0030 }
            return r1
        L_0x0030:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0030 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.DevicePolicyManagerService.getMaximumTimeToLock(android.content.ComponentName, int, boolean):long");
    }

    private long getMaximumTimeToLockPolicyFromAdmins(List<ActiveAdmin> admins) {
        long time = JobStatus.NO_LATEST_RUNTIME;
        for (ActiveAdmin admin : admins) {
            if (admin.maximumTimeToUnlock > 0 && admin.maximumTimeToUnlock < time) {
                time = admin.maximumTimeToUnlock;
            }
        }
        return time;
    }

    public void setRequiredStrongAuthTimeout(ComponentName who, long timeoutMs, boolean parent) {
        long timeoutMs2;
        if (this.mHasFeature && this.mLockPatternUtils.hasSecureLockScreen()) {
            Preconditions.checkNotNull(who, "ComponentName is null");
            Preconditions.checkArgument(timeoutMs >= 0, "Timeout must not be a negative number.");
            long minimumStrongAuthTimeout = getMinimumStrongAuthTimeoutMs();
            if (timeoutMs != 0 && timeoutMs < minimumStrongAuthTimeout) {
                timeoutMs = minimumStrongAuthTimeout;
            }
            if (timeoutMs > 259200000) {
                timeoutMs2 = 259200000;
            } else {
                timeoutMs2 = timeoutMs;
            }
            int userHandle = this.mInjector.userHandleGetCallingUserId();
            synchronized (getLockObject()) {
                ActiveAdmin ap = getActiveAdminForCallerLocked(who, -1, parent);
                if (ap.strongAuthUnlockTimeout != timeoutMs2) {
                    ap.strongAuthUnlockTimeout = timeoutMs2;
                    saveSettingsLocked(userHandle);
                }
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0026, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public long getRequiredStrongAuthTimeout(android.content.ComponentName r12, int r13, boolean r14) {
        /*
            r11 = this;
            boolean r0 = r11.mHasFeature
            if (r0 != 0) goto L_0x0008
            r0 = 259200000(0xf731400, double:1.280618154E-315)
            return r0
        L_0x0008:
            com.android.internal.widget.LockPatternUtils r0 = r11.mLockPatternUtils
            boolean r0 = r0.hasSecureLockScreen()
            r1 = 0
            if (r0 != 0) goto L_0x0013
            return r1
        L_0x0013:
            r11.enforceFullCrossUsersPermission(r13)
            java.lang.Object r0 = r11.getLockObject()
            monitor-enter(r0)
            if (r12 == 0) goto L_0x0027
            com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin r3 = r11.getActiveAdminUncheckedLocked(r12, r13, r14)     // Catch:{ all -> 0x0053 }
            if (r3 == 0) goto L_0x0025
            long r1 = r3.strongAuthUnlockTimeout     // Catch:{ all -> 0x0053 }
        L_0x0025:
            monitor-exit(r0)     // Catch:{ all -> 0x0053 }
            return r1
        L_0x0027:
            java.util.List r3 = r11.getActiveAdminsForLockscreenPoliciesLocked(r13, r14)     // Catch:{ all -> 0x0053 }
            r4 = 259200000(0xf731400, double:1.280618154E-315)
            r6 = 0
        L_0x002f:
            int r7 = r3.size()     // Catch:{ all -> 0x0053 }
            if (r6 >= r7) goto L_0x0049
            java.lang.Object r7 = r3.get(r6)     // Catch:{ all -> 0x0053 }
            com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin r7 = (com.android.server.devicepolicy.DevicePolicyManagerService.ActiveAdmin) r7     // Catch:{ all -> 0x0053 }
            long r7 = r7.strongAuthUnlockTimeout     // Catch:{ all -> 0x0053 }
            int r9 = (r7 > r1 ? 1 : (r7 == r1 ? 0 : -1))
            if (r9 == 0) goto L_0x0046
            long r9 = java.lang.Math.min(r7, r4)     // Catch:{ all -> 0x0053 }
            r4 = r9
        L_0x0046:
            int r6 = r6 + 1
            goto L_0x002f
        L_0x0049:
            long r1 = r11.getMinimumStrongAuthTimeoutMs()     // Catch:{ all -> 0x0053 }
            long r1 = java.lang.Math.max(r4, r1)     // Catch:{ all -> 0x0053 }
            monitor-exit(r0)     // Catch:{ all -> 0x0053 }
            return r1
        L_0x0053:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0053 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.DevicePolicyManagerService.getRequiredStrongAuthTimeout(android.content.ComponentName, int, boolean):long");
    }

    private long getMinimumStrongAuthTimeoutMs() {
        if (!this.mInjector.isBuildDebuggable()) {
            return MINIMUM_STRONG_AUTH_TIMEOUT_MS;
        }
        return Math.min(this.mInjector.systemPropertiesGetLong("persist.sys.min_str_auth_timeo", MINIMUM_STRONG_AUTH_TIMEOUT_MS), MINIMUM_STRONG_AUTH_TIMEOUT_MS);
    }

    /* Debug info: failed to restart local var, previous not found, register: 17 */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x0082 A[Catch:{ RemoteException -> 0x00d4, all -> 0x00cd }] */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x0096 A[Catch:{ RemoteException -> 0x00d4, all -> 0x00cd }] */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00a9 A[Catch:{ RemoteException -> 0x00d4, all -> 0x00cd }] */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00ae A[Catch:{ RemoteException -> 0x00d4, all -> 0x00cd }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void lockNow(int r18, boolean r19) {
        /*
            r17 = this;
            r1 = r17
            r2 = r18
            r3 = r19
            boolean r0 = r1.mHasFeature
            if (r0 != 0) goto L_0x000b
            return
        L_0x000b:
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r0 = r1.mInjector
            int r4 = r0.userHandleGetCallingUserId()
            r5 = 0
            java.lang.Object r6 = r17.getLockObject()
            monitor-enter(r6)
            java.lang.String r0 = "android.permission.LOCK_DEVICE"
            r7 = 3
            r8 = 0
            com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin r0 = r1.getActiveAdminOrCheckPermissionForCallerLocked(r8, r7, r3, r0)     // Catch:{ all -> 0x00ee }
            r9 = r0
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r0 = r1.mInjector     // Catch:{ all -> 0x00ee }
            long r10 = r0.binderClearCallingIdentity()     // Catch:{ all -> 0x00ee }
            if (r9 != 0) goto L_0x002a
            r0 = r8
            goto L_0x0030
        L_0x002a:
            android.app.admin.DeviceAdminInfo r0 = r9.info     // Catch:{ RemoteException -> 0x00d4, all -> 0x00cd }
            android.content.ComponentName r0 = r0.getComponent()     // Catch:{ RemoteException -> 0x00d4, all -> 0x00cd }
        L_0x0030:
            r5 = r0
            if (r5 == 0) goto L_0x006b
            r0 = r2 & 1
            if (r0 == 0) goto L_0x006b
            java.lang.String r0 = "set FLAG_EVICT_CREDENTIAL_ENCRYPTION_KEY"
            r1.enforceManagedProfile(r4, r0)     // Catch:{ RemoteException -> 0x00d4, all -> 0x00cd }
            boolean r0 = r1.isProfileOwner(r5, r4)     // Catch:{ RemoteException -> 0x00d4, all -> 0x00cd }
            if (r0 == 0) goto L_0x0063
            if (r3 != 0) goto L_0x005b
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r0 = r1.mInjector     // Catch:{ RemoteException -> 0x00d4, all -> 0x00cd }
            boolean r0 = r0.storageManagerIsFileBasedEncryptionEnabled()     // Catch:{ RemoteException -> 0x00d4, all -> 0x00cd }
            if (r0 == 0) goto L_0x0053
            android.os.UserManager r0 = r1.mUserManager     // Catch:{ RemoteException -> 0x00d4, all -> 0x00cd }
            r0.evictCredentialEncryptionKey(r4)     // Catch:{ RemoteException -> 0x00d4, all -> 0x00cd }
            goto L_0x006b
        L_0x0053:
            java.lang.UnsupportedOperationException r0 = new java.lang.UnsupportedOperationException     // Catch:{ RemoteException -> 0x00d4, all -> 0x00cd }
            java.lang.String r7 = "FLAG_EVICT_CREDENTIAL_ENCRYPTION_KEY only applies to FBE devices"
            r0.<init>(r7)     // Catch:{ RemoteException -> 0x00d4, all -> 0x00cd }
            throw r0     // Catch:{ RemoteException -> 0x00d4, all -> 0x00cd }
        L_0x005b:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException     // Catch:{ RemoteException -> 0x00d4, all -> 0x00cd }
            java.lang.String r7 = "Cannot set FLAG_EVICT_CREDENTIAL_ENCRYPTION_KEY for the parent"
            r0.<init>(r7)     // Catch:{ RemoteException -> 0x00d4, all -> 0x00cd }
            throw r0     // Catch:{ RemoteException -> 0x00d4, all -> 0x00cd }
        L_0x0063:
            java.lang.SecurityException r0 = new java.lang.SecurityException     // Catch:{ RemoteException -> 0x00d4, all -> 0x00cd }
            java.lang.String r7 = "Only profile owner admins can set FLAG_EVICT_CREDENTIAL_ENCRYPTION_KEY"
            r0.<init>(r7)     // Catch:{ RemoteException -> 0x00d4, all -> 0x00cd }
            throw r0     // Catch:{ RemoteException -> 0x00d4, all -> 0x00cd }
        L_0x006b:
            r0 = -1
            if (r3 != 0) goto L_0x0077
            boolean r12 = r1.isSeparateProfileChallengeEnabled(r4)     // Catch:{ RemoteException -> 0x00d4, all -> 0x00cd }
            if (r12 != 0) goto L_0x0075
            goto L_0x0077
        L_0x0075:
            r12 = r4
            goto L_0x0078
        L_0x0077:
            r12 = r0
        L_0x0078:
            com.android.internal.widget.LockPatternUtils r13 = r1.mLockPatternUtils     // Catch:{ RemoteException -> 0x00d4, all -> 0x00cd }
            r14 = 2
            r13.requireStrongAuth(r14, r12)     // Catch:{ RemoteException -> 0x00d4, all -> 0x00cd }
            r13 = 0
            r15 = 1
            if (r12 != r0) goto L_0x0096
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r0 = r1.mInjector     // Catch:{ RemoteException -> 0x00d4, all -> 0x00cd }
            long r7 = android.os.SystemClock.uptimeMillis()     // Catch:{ RemoteException -> 0x00d4, all -> 0x00cd }
            r0.powerManagerGoToSleep(r7, r15, r13)     // Catch:{ RemoteException -> 0x00d4, all -> 0x00cd }
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r0 = r1.mInjector     // Catch:{ RemoteException -> 0x00d4, all -> 0x00cd }
            android.view.IWindowManager r0 = r0.getIWindowManager()     // Catch:{ RemoteException -> 0x00d4, all -> 0x00cd }
            r7 = 0
            r0.lockNow(r7)     // Catch:{ RemoteException -> 0x00d4, all -> 0x00cd }
            goto L_0x009f
        L_0x0096:
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r0 = r1.mInjector     // Catch:{ RemoteException -> 0x00d4, all -> 0x00cd }
            android.app.trust.TrustManager r0 = r0.getTrustManager()     // Catch:{ RemoteException -> 0x00d4, all -> 0x00cd }
            r0.setDeviceLockedForUser(r12, r15)     // Catch:{ RemoteException -> 0x00d4, all -> 0x00cd }
        L_0x009f:
            boolean r0 = android.app.admin.SecurityLog.isLoggingEnabled()     // Catch:{ RemoteException -> 0x00d4, all -> 0x00cd }
            if (r0 == 0) goto L_0x00ca
            if (r5 == 0) goto L_0x00ca
            if (r3 == 0) goto L_0x00ae
            int r0 = r1.getProfileParentId(r4)     // Catch:{ RemoteException -> 0x00d4, all -> 0x00cd }
            goto L_0x00af
        L_0x00ae:
            r0 = r4
        L_0x00af:
            r7 = 210022(0x33466, float:2.94304E-40)
            r8 = 3
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ RemoteException -> 0x00d4, all -> 0x00cd }
            java.lang.String r16 = r5.getPackageName()     // Catch:{ RemoteException -> 0x00d4, all -> 0x00cd }
            r8[r13] = r16     // Catch:{ RemoteException -> 0x00d4, all -> 0x00cd }
            java.lang.Integer r13 = java.lang.Integer.valueOf(r4)     // Catch:{ RemoteException -> 0x00d4, all -> 0x00cd }
            r8[r15] = r13     // Catch:{ RemoteException -> 0x00d4, all -> 0x00cd }
            java.lang.Integer r13 = java.lang.Integer.valueOf(r0)     // Catch:{ RemoteException -> 0x00d4, all -> 0x00cd }
            r8[r14] = r13     // Catch:{ RemoteException -> 0x00d4, all -> 0x00cd }
            android.app.admin.SecurityLog.writeEvent(r7, r8)     // Catch:{ RemoteException -> 0x00d4, all -> 0x00cd }
        L_0x00ca:
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r0 = r1.mInjector     // Catch:{ all -> 0x00ee }
            goto L_0x00d7
        L_0x00cd:
            r0 = move-exception
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r7 = r1.mInjector     // Catch:{ all -> 0x00ee }
            r7.binderRestoreCallingIdentity(r10)     // Catch:{ all -> 0x00ee }
            throw r0     // Catch:{ all -> 0x00ee }
        L_0x00d4:
            r0 = move-exception
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r0 = r1.mInjector     // Catch:{ all -> 0x00ee }
        L_0x00d7:
            r0.binderRestoreCallingIdentity(r10)     // Catch:{ all -> 0x00ee }
            monitor-exit(r6)     // Catch:{ all -> 0x00ee }
            r0 = 10
            android.app.admin.DevicePolicyEventLogger r0 = android.app.admin.DevicePolicyEventLogger.createEvent(r0)
            android.app.admin.DevicePolicyEventLogger r0 = r0.setAdmin(r5)
            android.app.admin.DevicePolicyEventLogger r0 = r0.setInt(r2)
            r0.write()
            return
        L_0x00ee:
            r0 = move-exception
            monitor-exit(r6)     // Catch:{ all -> 0x00ee }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.DevicePolicyManagerService.lockNow(int, boolean):void");
    }

    public void enforceCanManageCaCerts(ComponentName who, String callerPackage) {
        if (who != null) {
            enforceProfileOrDeviceOwner(who);
        } else if (!isCallerDelegate(callerPackage, this.mInjector.binderGetCallingUid(), "delegation-cert-install")) {
            this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_CA_CERTIFICATES", (String) null);
        }
    }

    private void enforceDeviceOwner(ComponentName who) {
        synchronized (getLockObject()) {
            getActiveAdminForCallerLocked(who, -2);
        }
    }

    private void enforceProfileOrDeviceOwner(ComponentName who) {
        synchronized (getLockObject()) {
            getActiveAdminForCallerLocked(who, -1);
        }
    }

    public boolean approveCaCert(String alias, int userId, boolean approval) {
        enforceManageUsers();
        synchronized (getLockObject()) {
            Set<String> certs = getUserData(userId).mAcceptedCaCertificates;
            if (!(approval ? certs.add(alias) : certs.remove(alias))) {
                return false;
            }
            saveSettingsLocked(userId);
            this.mCertificateMonitor.onCertificateApprovalsChanged(userId);
            return true;
        }
    }

    public boolean isCaCertApproved(String alias, int userId) {
        boolean contains;
        enforceManageUsers();
        synchronized (getLockObject()) {
            contains = getUserData(userId).mAcceptedCaCertificates.contains(alias);
        }
        return contains;
    }

    private void removeCaApprovalsIfNeeded(int userId) {
        for (UserInfo userInfo : this.mUserManager.getProfiles(userId)) {
            boolean isSecure = this.mLockPatternUtils.isSecure(userInfo.id);
            if (userInfo.isManagedProfile()) {
                isSecure |= this.mLockPatternUtils.isSecure(getProfileParentId(userInfo.id));
            }
            if (!isSecure) {
                synchronized (getLockObject()) {
                    getUserData(userInfo.id).mAcceptedCaCertificates.clear();
                    saveSettingsLocked(userInfo.id);
                }
                this.mCertificateMonitor.onCertificateApprovalsChanged(userId);
            }
        }
    }

    public boolean installCaCert(ComponentName admin, String callerPackage, byte[] certBuffer) throws RemoteException {
        if (!this.mHasFeature) {
            return false;
        }
        enforceCanManageCaCerts(admin, callerPackage);
        UserHandle userHandle = this.mInjector.binderGetCallingUserHandle();
        long id = this.mInjector.binderClearCallingIdentity();
        try {
            String alias = this.mCertificateMonitor.installCaCert(userHandle, certBuffer);
            DevicePolicyEventLogger.createEvent(21).setAdmin(callerPackage).setBoolean(admin == null).write();
            if (alias == null) {
                Log.w(LOG_TAG, "Problem installing cert");
                return false;
            }
            this.mInjector.binderRestoreCallingIdentity(id);
            synchronized (getLockObject()) {
                getUserData(userHandle.getIdentifier()).mOwnerInstalledCaCerts.add(alias);
                saveSettingsLocked(userHandle.getIdentifier());
            }
            return true;
        } finally {
            this.mInjector.binderRestoreCallingIdentity(id);
        }
    }

    /* JADX INFO: finally extract failed */
    public void uninstallCaCerts(ComponentName admin, String callerPackage, String[] aliases) {
        if (this.mHasFeature) {
            enforceCanManageCaCerts(admin, callerPackage);
            int userId = this.mInjector.userHandleGetCallingUserId();
            long id = this.mInjector.binderClearCallingIdentity();
            try {
                this.mCertificateMonitor.uninstallCaCerts(UserHandle.of(userId), aliases);
                DevicePolicyEventLogger.createEvent(24).setAdmin(callerPackage).setBoolean(admin == null).write();
                this.mInjector.binderRestoreCallingIdentity(id);
                synchronized (getLockObject()) {
                    if (getUserData(userId).mOwnerInstalledCaCerts.removeAll(Arrays.asList(aliases))) {
                        saveSettingsLocked(userId);
                    }
                }
            } catch (Throwable th) {
                this.mInjector.binderRestoreCallingIdentity(id);
                throw th;
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 18 */
    public boolean installKeyPair(ComponentName who, String callerPackage, byte[] privKey, byte[] cert, byte[] chain, String alias, boolean requestAccess, boolean isUserSelectable) {
        IKeyChainService keyChain;
        ComponentName componentName = who;
        String str = callerPackage;
        String str2 = alias;
        enforceCanManageScope(componentName, str, -1, "delegation-cert-install");
        int callingUid = this.mInjector.binderGetCallingUid();
        long id = this.mInjector.binderClearCallingIdentity();
        try {
            KeyChain.KeyChainConnection keyChainConnection = KeyChain.bindAsUser(this.mContext, UserHandle.getUserHandleForUid(callingUid));
            try {
                keyChain = keyChainConnection.getService();
            } catch (RemoteException e) {
                keyChain = e;
                byte[] bArr = privKey;
                byte[] bArr2 = cert;
                byte[] bArr3 = chain;
                boolean z = isUserSelectable;
                try {
                    Log.e(LOG_TAG, "Installing certificate", keyChain);
                    keyChainConnection.close();
                    this.mInjector.binderRestoreCallingIdentity(id);
                    return false;
                } catch (Throwable th) {
                    th = th;
                }
            } catch (Throwable th2) {
                th = th2;
                byte[] bArr4 = privKey;
                byte[] bArr5 = cert;
                byte[] bArr6 = chain;
                boolean z2 = isUserSelectable;
                keyChainConnection.close();
                throw th;
            }
            try {
                if (!keyChain.installKeyPair(privKey, cert, chain, str2)) {
                    try {
                        keyChainConnection.close();
                        this.mInjector.binderRestoreCallingIdentity(id);
                        return false;
                    } catch (InterruptedException e2) {
                        e = e2;
                        boolean z3 = isUserSelectable;
                        try {
                            Log.w(LOG_TAG, "Interrupted while installing certificate", e);
                            Thread.currentThread().interrupt();
                            this.mInjector.binderRestoreCallingIdentity(id);
                            return false;
                        } catch (Throwable th3) {
                            keyChain = th3;
                            this.mInjector.binderRestoreCallingIdentity(id);
                            throw keyChain;
                        }
                    } catch (Throwable th4) {
                        keyChain = th4;
                        boolean z4 = isUserSelectable;
                        this.mInjector.binderRestoreCallingIdentity(id);
                        throw keyChain;
                    }
                } else {
                    if (requestAccess) {
                        keyChain.setGrant(callingUid, str2, true);
                    }
                    try {
                        keyChain.setUserSelectable(str2, isUserSelectable);
                        DevicePolicyEventLogger.createEvent(20).setAdmin(str).setBoolean(componentName == null).write();
                    } catch (RemoteException e3) {
                        keyChain = e3;
                        Log.e(LOG_TAG, "Installing certificate", keyChain);
                        keyChainConnection.close();
                        this.mInjector.binderRestoreCallingIdentity(id);
                        return false;
                    }
                    try {
                        keyChainConnection.close();
                        this.mInjector.binderRestoreCallingIdentity(id);
                        return true;
                    } catch (InterruptedException e4) {
                        e = e4;
                        Log.w(LOG_TAG, "Interrupted while installing certificate", e);
                        Thread.currentThread().interrupt();
                        this.mInjector.binderRestoreCallingIdentity(id);
                        return false;
                    }
                }
            } catch (RemoteException e5) {
                keyChain = e5;
                boolean z5 = isUserSelectable;
                Log.e(LOG_TAG, "Installing certificate", keyChain);
                keyChainConnection.close();
                this.mInjector.binderRestoreCallingIdentity(id);
                return false;
            } catch (Throwable th5) {
                th = th5;
                boolean z22 = isUserSelectable;
                keyChainConnection.close();
                throw th;
            }
        } catch (InterruptedException e6) {
            e = e6;
            byte[] bArr7 = privKey;
            byte[] bArr8 = cert;
            byte[] bArr9 = chain;
            boolean z32 = isUserSelectable;
            Log.w(LOG_TAG, "Interrupted while installing certificate", e);
            Thread.currentThread().interrupt();
            this.mInjector.binderRestoreCallingIdentity(id);
            return false;
        } catch (Throwable th6) {
            keyChain = th6;
            byte[] bArr10 = privKey;
            byte[] bArr11 = cert;
            byte[] bArr12 = chain;
            boolean z42 = isUserSelectable;
            this.mInjector.binderRestoreCallingIdentity(id);
            throw keyChain;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 10 */
    public boolean removeKeyPair(ComponentName who, String callerPackage, String alias) {
        KeyChain.KeyChainConnection keyChainConnection;
        enforceCanManageScope(who, callerPackage, -1, "delegation-cert-install");
        UserHandle userHandle = new UserHandle(UserHandle.getCallingUserId());
        long id = Binder.clearCallingIdentity();
        try {
            keyChainConnection = KeyChain.bindAsUser(this.mContext, userHandle);
            try {
                boolean result = keyChainConnection.getService().removeKeyPair(alias);
                DevicePolicyEventLogger.createEvent(23).setAdmin(callerPackage).setBoolean(who == null).write();
                keyChainConnection.close();
                Binder.restoreCallingIdentity(id);
                return result;
            } catch (RemoteException e) {
                Log.e(LOG_TAG, "Removing keypair", e);
                keyChainConnection.close();
                Binder.restoreCallingIdentity(id);
                return false;
            }
        } catch (InterruptedException e2) {
            try {
                Log.w(LOG_TAG, "Interrupted while removing keypair", e2);
                Thread.currentThread().interrupt();
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(id);
                throw th;
            }
        } catch (Throwable keyChain) {
            keyChainConnection.close();
            throw keyChain;
        }
    }

    @VisibleForTesting
    public void enforceCallerCanRequestDeviceIdAttestation(ComponentName who, String callerPackage, int callerUid) throws SecurityException {
        int userId = UserHandle.getUserId(callerUid);
        if (hasProfileOwner(userId)) {
            enforceCanManageScope(who, callerPackage, -1, "delegation-cert-install");
            if (!canProfileOwnerAccessDeviceIds(userId)) {
                throw new SecurityException("Profile Owner is not allowed to access Device IDs.");
            }
            return;
        }
        enforceCanManageScope(who, callerPackage, -2, "delegation-cert-install");
    }

    @VisibleForTesting
    public static int[] translateIdAttestationFlags(int idAttestationFlags) {
        Map<Integer, Integer> idTypeToAttestationFlag = new HashMap<>();
        idTypeToAttestationFlag.put(2, 1);
        idTypeToAttestationFlag.put(4, 2);
        idTypeToAttestationFlag.put(8, 3);
        int numFlagsSet = Integer.bitCount(idAttestationFlags);
        if (numFlagsSet == 0) {
            return null;
        }
        if ((idAttestationFlags & 1) != 0) {
            numFlagsSet--;
            idAttestationFlags &= -2;
        }
        int[] attestationUtilsFlags = new int[numFlagsSet];
        int i = 0;
        for (Integer idType : idTypeToAttestationFlag.keySet()) {
            if ((idType.intValue() & idAttestationFlags) != 0) {
                attestationUtilsFlags[i] = idTypeToAttestationFlag.get(idType).intValue();
                i++;
            }
        }
        return attestationUtilsFlags;
    }

    /* Debug info: failed to restart local var, previous not found, register: 23 */
    /* JADX WARNING: Code restructure failed: missing block: B:100:0x019d, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:93:0x0193, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:94:0x0194, code lost:
        r3 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:95:0x0195, code lost:
        if (r12 != null) goto L_0x0197;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:97:?, code lost:
        $closeResource(r2, r12);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:98:0x019a, code lost:
        throw r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:99:0x019b, code lost:
        r0 = e;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:81:0x0176, B:91:0x0192] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean generateKeyPair(android.content.ComponentName r24, java.lang.String r25, java.lang.String r26, android.security.keystore.ParcelableKeyGenParameterSpec r27, int r28, android.security.keymaster.KeymasterCertificateChain r29) {
        /*
            r23 = this;
            r1 = r23
            r2 = r24
            r3 = r25
            r4 = r26
            int[] r5 = translateIdAttestationFlags(r28)
            r6 = 0
            if (r5 == 0) goto L_0x0011
            r7 = 1
            goto L_0x0012
        L_0x0011:
            r7 = r6
        L_0x0012:
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r8 = r1.mInjector
            int r8 = r8.binderGetCallingUid()
            r9 = -1
            if (r7 == 0) goto L_0x0022
            int r10 = r5.length
            if (r10 <= 0) goto L_0x0022
            r1.enforceCallerCanRequestDeviceIdAttestation(r2, r3, r8)
            goto L_0x0027
        L_0x0022:
            java.lang.String r10 = "delegation-cert-install"
            r1.enforceCanManageScope(r2, r3, r9, r10)
        L_0x0027:
            android.security.keystore.KeyGenParameterSpec r10 = r27.getSpec()
            java.lang.String r11 = r10.getKeystoreAlias()
            boolean r12 = android.text.TextUtils.isEmpty(r11)
            if (r12 != 0) goto L_0x01da
            int r12 = r10.getUid()
            java.lang.String r13 = "DevicePolicyManager"
            if (r12 == r9) goto L_0x0043
            java.lang.String r0 = "Only the caller can be granted access to the generated keypair."
            android.util.Log.e(r13, r0)
            return r6
        L_0x0043:
            if (r7 == 0) goto L_0x0054
            byte[] r9 = r10.getAttestationChallenge()
            if (r9 == 0) goto L_0x004c
            goto L_0x0054
        L_0x004c:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.String r6 = "Requested Device ID attestation but challenge is empty."
            r0.<init>(r6)
            throw r0
        L_0x0054:
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r9 = r1.mInjector
            android.os.UserHandle r9 = r9.binderGetCallingUserHandle()
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r12 = r1.mInjector
            long r14 = r12.binderClearCallingIdentity()
            android.content.Context r12 = r1.mContext     // Catch:{ RemoteException -> 0x01bd, InterruptedException -> 0x01a8, all -> 0x019f }
            android.security.KeyChain$KeyChainConnection r12 = android.security.KeyChain.bindAsUser(r12, r9)     // Catch:{ RemoteException -> 0x01bd, InterruptedException -> 0x01a8, all -> 0x019f }
            android.security.IKeyChainService r16 = r12.getService()     // Catch:{ all -> 0x0189 }
            r17 = r16
            android.security.keystore.KeyGenParameterSpec$Builder r6 = new android.security.keystore.KeyGenParameterSpec$Builder     // Catch:{ all -> 0x0189 }
            r6.<init>(r10)     // Catch:{ all -> 0x0189 }
            r0 = 0
            android.security.keystore.KeyGenParameterSpec$Builder r6 = r6.setAttestationChallenge(r0)     // Catch:{ all -> 0x0189 }
            android.security.keystore.KeyGenParameterSpec r6 = r6.build()     // Catch:{ all -> 0x0189 }
            android.security.keystore.ParcelableKeyGenParameterSpec r0 = new android.security.keystore.ParcelableKeyGenParameterSpec     // Catch:{ all -> 0x0189 }
            r0.<init>(r6)     // Catch:{ all -> 0x0189 }
            r19 = r6
            r6 = r17
            int r0 = r6.generateKeyPair(r4, r0)     // Catch:{ all -> 0x0189 }
            if (r0 == 0) goto L_0x00e5
            r17 = r7
            java.lang.String r7 = "KeyChain failed to generate a keypair, error %d."
            r20 = r9
            r9 = 1
            java.lang.Object[] r4 = new java.lang.Object[r9]     // Catch:{ all -> 0x0182 }
            java.lang.Integer r9 = java.lang.Integer.valueOf(r0)     // Catch:{ all -> 0x0182 }
            r16 = 0
            r4[r16] = r9     // Catch:{ all -> 0x0182 }
            java.lang.String r4 = java.lang.String.format(r7, r4)     // Catch:{ all -> 0x0182 }
            android.util.Log.e(r13, r4)     // Catch:{ all -> 0x0182 }
            r4 = 6
            if (r0 == r4) goto L_0x00c3
            r4 = 0
            $closeResource(r4, r12)     // Catch:{ RemoteException -> 0x00bd, InterruptedException -> 0x00b7, all -> 0x00b1 }
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r4 = r1.mInjector
            r4.binderRestoreCallingIdentity(r14)
            r4 = 0
            return r4
        L_0x00b1:
            r0 = move-exception
            r4 = r29
            r9 = r3
            goto L_0x01d4
        L_0x00b7:
            r0 = move-exception
            r4 = r29
            r9 = r3
            goto L_0x01b0
        L_0x00bd:
            r0 = move-exception
            r4 = r29
            r9 = r3
            goto L_0x01c5
        L_0x00c3:
            android.os.ServiceSpecificException r4 = new android.os.ServiceSpecificException     // Catch:{ all -> 0x0182 }
            java.lang.String r7 = "KeyChain error: %d"
            r9 = 1
            java.lang.Object[] r3 = new java.lang.Object[r9]     // Catch:{ all -> 0x0182 }
            java.lang.Integer r9 = java.lang.Integer.valueOf(r0)     // Catch:{ all -> 0x0182 }
            r16 = 0
            r3[r16] = r9     // Catch:{ all -> 0x0182 }
            java.lang.String r3 = java.lang.String.format(r7, r3)     // Catch:{ all -> 0x0182 }
            r7 = 1
            r4.<init>(r7, r3)     // Catch:{ all -> 0x0182 }
            throw r4     // Catch:{ all -> 0x0182 }
        L_0x00db:
            r0 = move-exception
            r20 = r9
            r9 = r25
            r4 = r29
            r2 = r0
            goto L_0x0192
        L_0x00e5:
            r17 = r7
            r20 = r9
            r3 = 1
            r6.setGrant(r8, r11, r3)     // Catch:{ all -> 0x0182 }
            byte[] r3 = r10.getAttestationChallenge()     // Catch:{ all -> 0x0182 }
            if (r3 == 0) goto L_0x0145
            r4 = r29
            int r7 = r6.attestKey(r11, r3, r5, r4)     // Catch:{ all -> 0x0141 }
            if (r7 == 0) goto L_0x013e
            java.lang.String r9 = "Attestation for %s failed (rc=%d), deleting key."
            r21 = r0
            r0 = 2
            java.lang.Object[] r0 = new java.lang.Object[r0]     // Catch:{ all -> 0x0141 }
            r16 = 0
            r0[r16] = r11     // Catch:{ all -> 0x0141 }
            java.lang.Integer r22 = java.lang.Integer.valueOf(r7)     // Catch:{ all -> 0x0141 }
            r18 = 1
            r0[r18] = r22     // Catch:{ all -> 0x0141 }
            java.lang.String r0 = java.lang.String.format(r9, r0)     // Catch:{ all -> 0x0141 }
            android.util.Log.e(r13, r0)     // Catch:{ all -> 0x0141 }
            r6.removeKeyPair(r11)     // Catch:{ all -> 0x0141 }
            r0 = 3
            if (r7 == r0) goto L_0x0136
            r0 = 0
            $closeResource(r0, r12)     // Catch:{ RemoteException -> 0x0131, InterruptedException -> 0x012c, all -> 0x0127 }
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r0 = r1.mInjector
            r0.binderRestoreCallingIdentity(r14)
            r9 = 0
            return r9
        L_0x0127:
            r0 = move-exception
            r9 = r25
            goto L_0x01d4
        L_0x012c:
            r0 = move-exception
            r9 = r25
            goto L_0x01b0
        L_0x0131:
            r0 = move-exception
            r9 = r25
            goto L_0x01c5
        L_0x0136:
            java.lang.UnsupportedOperationException r0 = new java.lang.UnsupportedOperationException     // Catch:{ all -> 0x0141 }
            java.lang.String r9 = "Device does not support Device ID attestation."
            r0.<init>(r9)     // Catch:{ all -> 0x0141 }
            throw r0     // Catch:{ all -> 0x0141 }
        L_0x013e:
            r21 = r0
            goto L_0x0149
        L_0x0141:
            r0 = move-exception
            r9 = r25
            goto L_0x0187
        L_0x0145:
            r4 = r29
            r21 = r0
        L_0x0149:
            if (r2 != 0) goto L_0x014d
            r0 = 1
            goto L_0x014e
        L_0x014d:
            r0 = 0
        L_0x014e:
            r7 = 59
            android.app.admin.DevicePolicyEventLogger r7 = android.app.admin.DevicePolicyEventLogger.createEvent(r7)     // Catch:{ all -> 0x0141 }
            r9 = r25
            android.app.admin.DevicePolicyEventLogger r7 = r7.setAdmin(r9)     // Catch:{ all -> 0x0180 }
            android.app.admin.DevicePolicyEventLogger r7 = r7.setBoolean(r0)     // Catch:{ all -> 0x0180 }
            r2 = r28
            android.app.admin.DevicePolicyEventLogger r7 = r7.setInt(r2)     // Catch:{ all -> 0x0180 }
            r22 = r0
            r0 = 1
            java.lang.String[] r2 = new java.lang.String[r0]     // Catch:{ all -> 0x0180 }
            r16 = 0
            r2[r16] = r26     // Catch:{ all -> 0x0180 }
            android.app.admin.DevicePolicyEventLogger r0 = r7.setStrings(r2)     // Catch:{ all -> 0x0180 }
            r0.write()     // Catch:{ all -> 0x0180 }
            r0 = 0
            $closeResource(r0, r12)     // Catch:{ RemoteException -> 0x019d, InterruptedException -> 0x019b }
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r0 = r1.mInjector
            r0.binderRestoreCallingIdentity(r14)
            r0 = 1
            return r0
        L_0x0180:
            r0 = move-exception
            goto L_0x0187
        L_0x0182:
            r0 = move-exception
            r9 = r25
            r4 = r29
        L_0x0187:
            r2 = r0
            goto L_0x0192
        L_0x0189:
            r0 = move-exception
            r4 = r29
            r17 = r7
            r20 = r9
            r9 = r3
            r2 = r0
        L_0x0192:
            throw r2     // Catch:{ all -> 0x0193 }
        L_0x0193:
            r0 = move-exception
            r3 = r0
            if (r12 == 0) goto L_0x019a
            $closeResource(r2, r12)     // Catch:{ RemoteException -> 0x019d, InterruptedException -> 0x019b }
        L_0x019a:
            throw r3     // Catch:{ RemoteException -> 0x019d, InterruptedException -> 0x019b }
        L_0x019b:
            r0 = move-exception
            goto L_0x01b0
        L_0x019d:
            r0 = move-exception
            goto L_0x01c5
        L_0x019f:
            r0 = move-exception
            r4 = r29
            r17 = r7
            r20 = r9
            r9 = r3
            goto L_0x01d4
        L_0x01a8:
            r0 = move-exception
            r4 = r29
            r17 = r7
            r20 = r9
            r9 = r3
        L_0x01b0:
            java.lang.String r2 = "Interrupted while generating keypair"
            android.util.Log.w(r13, r2, r0)     // Catch:{ all -> 0x01d3 }
            java.lang.Thread r2 = java.lang.Thread.currentThread()     // Catch:{ all -> 0x01d3 }
            r2.interrupt()     // Catch:{ all -> 0x01d3 }
            goto L_0x01cb
        L_0x01bd:
            r0 = move-exception
            r4 = r29
            r17 = r7
            r20 = r9
            r9 = r3
        L_0x01c5:
            java.lang.String r2 = "KeyChain error while generating a keypair"
            android.util.Log.e(r13, r2, r0)     // Catch:{ all -> 0x01d3 }
        L_0x01cb:
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r0 = r1.mInjector
            r0.binderRestoreCallingIdentity(r14)
            r2 = 0
            return r2
        L_0x01d3:
            r0 = move-exception
        L_0x01d4:
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r2 = r1.mInjector
            r2.binderRestoreCallingIdentity(r14)
            throw r0
        L_0x01da:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.String r2 = "Empty alias provided."
            r0.<init>(r2)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.DevicePolicyManagerService.generateKeyPair(android.content.ComponentName, java.lang.String, java.lang.String, android.security.keystore.ParcelableKeyGenParameterSpec, int, android.security.keymaster.KeymasterCertificateChain):boolean");
    }

    private static /* synthetic */ void $closeResource(Throwable x0, AutoCloseable x1) {
        if (x0 != null) {
            try {
                x1.close();
            } catch (Throwable th) {
                x0.addSuppressed(th);
            }
        } else {
            x1.close();
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 18 */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0081, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0082, code lost:
        r11 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x0083, code lost:
        if (r10 != null) goto L_0x0085;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:?, code lost:
        $closeResource(r9, r10);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x0088, code lost:
        throw r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x0089, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x008b, code lost:
        r0 = e;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:24:0x006a, B:34:0x0080] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean setKeyPairCertificate(android.content.ComponentName r19, java.lang.String r20, java.lang.String r21, byte[] r22, byte[] r23, boolean r24) {
        /*
            r18 = this;
            r1 = r18
            r2 = r19
            r3 = r20
            r4 = r21
            java.lang.String r5 = "DevicePolicyManager"
            r0 = -1
            java.lang.String r6 = "delegation-cert-install"
            r1.enforceCanManageScope(r2, r3, r0, r6)
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r0 = r1.mInjector
            int r6 = r0.binderGetCallingUid()
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r0 = r1.mInjector
            long r7 = r0.binderClearCallingIdentity()
            r9 = 0
            android.content.Context r0 = r1.mContext     // Catch:{ InterruptedException -> 0x00a8, RemoteException -> 0x0095, all -> 0x008d }
            android.os.UserHandle r10 = android.os.UserHandle.getUserHandleForUid(r6)     // Catch:{ InterruptedException -> 0x00a8, RemoteException -> 0x0095, all -> 0x008d }
            android.security.KeyChain$KeyChainConnection r0 = android.security.KeyChain.bindAsUser(r0, r10)     // Catch:{ InterruptedException -> 0x00a8, RemoteException -> 0x0095, all -> 0x008d }
            r10 = r0
            r0 = 0
            android.security.IKeyChainService r11 = r10.getService()     // Catch:{ all -> 0x0078 }
            r12 = r22
            r13 = r23
            boolean r14 = r11.setKeyPairCertificate(r4, r12, r13)     // Catch:{ all -> 0x0076 }
            if (r14 != 0) goto L_0x0048
            $closeResource(r0, r10)     // Catch:{ InterruptedException -> 0x0045, RemoteException -> 0x0043, all -> 0x0041 }
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r0 = r1.mInjector
            r0.binderRestoreCallingIdentity(r7)
            return r9
        L_0x0041:
            r0 = move-exception
            goto L_0x0092
        L_0x0043:
            r0 = move-exception
            goto L_0x009a
        L_0x0045:
            r0 = move-exception
            goto L_0x00ad
        L_0x0048:
            r14 = r24
            r11.setUserSelectable(r4, r14)     // Catch:{ all -> 0x0074 }
            if (r2 != 0) goto L_0x0052
            r16 = 1
            goto L_0x0054
        L_0x0052:
            r16 = r9
        L_0x0054:
            r17 = r16
            r16 = 60
            android.app.admin.DevicePolicyEventLogger r9 = android.app.admin.DevicePolicyEventLogger.createEvent(r16)     // Catch:{ all -> 0x0074 }
            android.app.admin.DevicePolicyEventLogger r9 = r9.setAdmin(r3)     // Catch:{ all -> 0x0074 }
            r15 = r17
            android.app.admin.DevicePolicyEventLogger r9 = r9.setBoolean(r15)     // Catch:{ all -> 0x0074 }
            r9.write()     // Catch:{ all -> 0x0074 }
            $closeResource(r0, r10)     // Catch:{ InterruptedException -> 0x008b, RemoteException -> 0x0089 }
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r0 = r1.mInjector
            r0.binderRestoreCallingIdentity(r7)
            r0 = 1
            return r0
        L_0x0074:
            r0 = move-exception
            goto L_0x007f
        L_0x0076:
            r0 = move-exception
            goto L_0x007d
        L_0x0078:
            r0 = move-exception
            r12 = r22
            r13 = r23
        L_0x007d:
            r14 = r24
        L_0x007f:
            r9 = r0
            throw r9     // Catch:{ all -> 0x0081 }
        L_0x0081:
            r0 = move-exception
            r11 = r0
            if (r10 == 0) goto L_0x0088
            $closeResource(r9, r10)     // Catch:{ InterruptedException -> 0x008b, RemoteException -> 0x0089 }
        L_0x0088:
            throw r11     // Catch:{ InterruptedException -> 0x008b, RemoteException -> 0x0089 }
        L_0x0089:
            r0 = move-exception
            goto L_0x009c
        L_0x008b:
            r0 = move-exception
            goto L_0x00af
        L_0x008d:
            r0 = move-exception
            r12 = r22
            r13 = r23
        L_0x0092:
            r14 = r24
            goto L_0x00bf
        L_0x0095:
            r0 = move-exception
            r12 = r22
            r13 = r23
        L_0x009a:
            r14 = r24
        L_0x009c:
            java.lang.String r9 = "Failed setting keypair certificate"
            android.util.Log.e(r5, r9, r0)     // Catch:{ all -> 0x00be }
        L_0x00a2:
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r0 = r1.mInjector
            r0.binderRestoreCallingIdentity(r7)
            goto L_0x00bc
        L_0x00a8:
            r0 = move-exception
            r12 = r22
            r13 = r23
        L_0x00ad:
            r14 = r24
        L_0x00af:
            java.lang.String r9 = "Interrupted while setting keypair certificate"
            android.util.Log.w(r5, r9, r0)     // Catch:{ all -> 0x00be }
            java.lang.Thread r5 = java.lang.Thread.currentThread()     // Catch:{ all -> 0x00be }
            r5.interrupt()     // Catch:{ all -> 0x00be }
            goto L_0x00a2
        L_0x00bc:
            r5 = 0
            return r5
        L_0x00be:
            r0 = move-exception
        L_0x00bf:
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r5 = r1.mInjector
            r5.binderRestoreCallingIdentity(r7)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.DevicePolicyManagerService.setKeyPairCertificate(android.content.ComponentName, java.lang.String, java.lang.String, byte[], byte[], boolean):boolean");
    }

    public void choosePrivateKeyAlias(int uid, Uri uri, String alias, IBinder response) {
        ComponentName aliasChooser;
        boolean isDelegate;
        long id;
        final IBinder iBinder = response;
        if (isCallerWithSystemUid()) {
            UserHandle caller = this.mInjector.binderGetCallingUserHandle();
            ComponentName aliasChooser2 = getProfileOwner(caller.getIdentifier());
            if (aliasChooser2 != null || !caller.isSystem()) {
                aliasChooser = aliasChooser2;
            } else {
                synchronized (getLockObject()) {
                    ActiveAdmin deviceOwnerAdmin = getDeviceOwnerAdminLocked();
                    if (deviceOwnerAdmin != null) {
                        aliasChooser2 = deviceOwnerAdmin.info.getComponent();
                    }
                }
                aliasChooser = aliasChooser2;
            }
            if (aliasChooser == null) {
                sendPrivateKeyAliasResponse((String) null, iBinder);
                return;
            }
            Intent intent = new Intent("android.app.action.CHOOSE_PRIVATE_KEY_ALIAS");
            intent.putExtra("android.app.extra.CHOOSE_PRIVATE_KEY_SENDER_UID", uid);
            intent.putExtra("android.app.extra.CHOOSE_PRIVATE_KEY_URI", uri);
            intent.putExtra("android.app.extra.CHOOSE_PRIVATE_KEY_ALIAS", alias);
            intent.putExtra("android.app.extra.CHOOSE_PRIVATE_KEY_RESPONSE", iBinder);
            intent.addFlags(268435456);
            ComponentName delegateReceiver = resolveDelegateReceiver("delegation-cert-selection", "android.app.action.CHOOSE_PRIVATE_KEY_ALIAS", caller.getIdentifier());
            if (delegateReceiver != null) {
                intent.setComponent(delegateReceiver);
                isDelegate = true;
            } else {
                intent.setComponent(aliasChooser);
                isDelegate = false;
            }
            long id2 = this.mInjector.binderClearCallingIdentity();
            try {
                long id3 = id2;
                boolean isDelegate2 = isDelegate;
                ComponentName componentName = delegateReceiver;
                try {
                    this.mContext.sendOrderedBroadcastAsUser(intent, caller, (String) null, new BroadcastReceiver() {
                        public void onReceive(Context context, Intent intent) {
                            DevicePolicyManagerService.this.sendPrivateKeyAliasResponse(getResultData(), iBinder);
                        }
                    }, (Handler) null, -1, (String) null, (Bundle) null);
                } catch (Throwable th) {
                    th = th;
                    id = id3;
                    boolean z = isDelegate2;
                    this.mInjector.binderRestoreCallingIdentity(id);
                    throw th;
                }
                try {
                    DevicePolicyEventLogger.createEvent(22).setAdmin(intent.getComponent()).setBoolean(isDelegate2).write();
                    this.mInjector.binderRestoreCallingIdentity(id3);
                } catch (Throwable th2) {
                    th = th2;
                    id = id3;
                    this.mInjector.binderRestoreCallingIdentity(id);
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                id = id2;
                boolean z2 = isDelegate;
                ComponentName componentName2 = delegateReceiver;
                this.mInjector.binderRestoreCallingIdentity(id);
                throw th;
            }
        }
    }

    /* access modifiers changed from: private */
    public void sendPrivateKeyAliasResponse(String alias, IBinder responseBinder) {
        try {
            IKeyChainAliasCallback.Stub.asInterface(responseBinder).alias(alias);
        } catch (Exception e) {
            Log.e(LOG_TAG, "error while responding to callback", e);
        }
    }

    private static boolean shouldCheckIfDelegatePackageIsInstalled(String delegatePackage, int targetSdk, List<String> scopes) {
        if (targetSdk >= 24) {
            return true;
        }
        if ((scopes.size() != 1 || !scopes.get(0).equals("delegation-cert-install")) && !scopes.isEmpty()) {
            return true;
        }
        return false;
    }

    /* Debug info: failed to restart local var, previous not found, register: 10 */
    public void setDelegatedScopes(ComponentName who, String delegatePackage, List<String> scopeList) throws SecurityException {
        Preconditions.checkNotNull(who, "ComponentName is null");
        Preconditions.checkStringNotEmpty(delegatePackage, "Delegate package is null or empty");
        Preconditions.checkCollectionElementsNotNull(scopeList, "Scopes");
        ArrayList<String> scopes = new ArrayList<>(new ArraySet(scopeList));
        if (!scopes.retainAll(Arrays.asList(DELEGATIONS))) {
            boolean hasDoDelegation = !Collections.disjoint(scopes, DEVICE_OWNER_DELEGATIONS);
            int userId = this.mInjector.userHandleGetCallingUserId();
            synchronized (getLockObject()) {
                if (hasDoDelegation) {
                    getActiveAdminForCallerLocked(who, -2);
                } else {
                    getActiveAdminForCallerLocked(who, -1);
                }
                if (shouldCheckIfDelegatePackageIsInstalled(delegatePackage, getTargetSdk(who.getPackageName(), userId), scopes)) {
                    if (!isPackageInstalledForUser(delegatePackage, userId)) {
                        throw new IllegalArgumentException("Package " + delegatePackage + " is not installed on the current user");
                    }
                }
                DevicePolicyData policy = getUserData(userId);
                List<String> exclusiveScopes = null;
                if (!scopes.isEmpty()) {
                    policy.mDelegationMap.put(delegatePackage, new ArrayList(scopes));
                    exclusiveScopes = new ArrayList<>(scopes);
                    exclusiveScopes.retainAll(EXCLUSIVE_DELEGATIONS);
                } else {
                    policy.mDelegationMap.remove(delegatePackage);
                }
                sendDelegationChangedBroadcast(delegatePackage, scopes, userId);
                if (exclusiveScopes != null && !exclusiveScopes.isEmpty()) {
                    for (int i = policy.mDelegationMap.size() - 1; i >= 0; i--) {
                        String currentPackage = policy.mDelegationMap.keyAt(i);
                        List<String> currentScopes = policy.mDelegationMap.valueAt(i);
                        if (!currentPackage.equals(delegatePackage) && currentScopes.removeAll(exclusiveScopes)) {
                            if (currentScopes.isEmpty()) {
                                policy.mDelegationMap.removeAt(i);
                            }
                            sendDelegationChangedBroadcast(currentPackage, new ArrayList(currentScopes), userId);
                        }
                    }
                }
                saveSettingsLocked(userId);
            }
            return;
        }
        throw new IllegalArgumentException("Unexpected delegation scopes");
    }

    private void sendDelegationChangedBroadcast(String delegatePackage, ArrayList<String> scopes, int userId) {
        Intent intent = new Intent("android.app.action.APPLICATION_DELEGATION_SCOPES_CHANGED");
        intent.addFlags(1073741824);
        intent.setPackage(delegatePackage);
        intent.putStringArrayListExtra("android.app.extra.DELEGATION_SCOPES", scopes);
        this.mContext.sendBroadcastAsUser(intent, UserHandle.of(userId));
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    public List<String> getDelegatedScopes(ComponentName who, String delegatePackage) throws SecurityException {
        List<String> list;
        Preconditions.checkNotNull(delegatePackage, "Delegate package is null");
        int callingUid = this.mInjector.binderGetCallingUid();
        int userId = UserHandle.getUserId(callingUid);
        synchronized (getLockObject()) {
            if (who != null) {
                getActiveAdminForCallerLocked(who, -1);
            } else {
                int uid = 0;
                try {
                    uid = this.mInjector.getPackageManager().getPackageUidAsUser(delegatePackage, userId);
                } catch (PackageManager.NameNotFoundException e) {
                }
                if (uid != callingUid) {
                    throw new SecurityException("Caller with uid " + callingUid + " is not " + delegatePackage);
                }
            }
            List<String> scopes = getUserData(userId).mDelegationMap.get(delegatePackage);
            list = scopes == null ? Collections.EMPTY_LIST : scopes;
        }
        return list;
    }

    public List<String> getDelegatePackages(ComponentName who, String scope) throws SecurityException {
        List<String> delegatePackagesInternalLocked;
        Preconditions.checkNotNull(who, "ComponentName is null");
        Preconditions.checkNotNull(scope, "Scope is null");
        if (Arrays.asList(DELEGATIONS).contains(scope)) {
            int userId = this.mInjector.userHandleGetCallingUserId();
            synchronized (getLockObject()) {
                getActiveAdminForCallerLocked(who, -1);
                delegatePackagesInternalLocked = getDelegatePackagesInternalLocked(scope, userId);
            }
            return delegatePackagesInternalLocked;
        }
        throw new IllegalArgumentException("Unexpected delegation scope: " + scope);
    }

    private List<String> getDelegatePackagesInternalLocked(String scope, int userId) {
        DevicePolicyData policy = getUserData(userId);
        List<String> delegatePackagesWithScope = new ArrayList<>();
        for (int i = 0; i < policy.mDelegationMap.size(); i++) {
            if (policy.mDelegationMap.valueAt(i).contains(scope)) {
                delegatePackagesWithScope.add(policy.mDelegationMap.keyAt(i));
            }
        }
        return delegatePackagesWithScope;
    }

    private ComponentName resolveDelegateReceiver(String scope, String action, int userId) {
        List<String> delegates;
        synchronized (getLockObject()) {
            delegates = getDelegatePackagesInternalLocked(scope, userId);
        }
        if (delegates.size() == 0) {
            return null;
        }
        if (delegates.size() > 1) {
            Slog.wtf(LOG_TAG, "More than one delegate holds " + scope);
            return null;
        }
        String pkg = delegates.get(0);
        Intent intent = new Intent(action);
        intent.setPackage(pkg);
        try {
            List<ResolveInfo> receivers = this.mIPackageManager.queryIntentReceivers(intent, (String) null, 0, userId).getList();
            int count = receivers.size();
            if (count < 1) {
                return null;
            }
            if (count > 1) {
                Slog.w(LOG_TAG, pkg + " defines more than one delegate receiver for " + action);
            }
            return receivers.get(0).activityInfo.getComponentName();
        } catch (RemoteException e) {
            return null;
        }
    }

    private boolean isCallerDelegate(String callerPackage, int callerUid, String scope) {
        Preconditions.checkNotNull(callerPackage, "callerPackage is null");
        if (Arrays.asList(DELEGATIONS).contains(scope)) {
            int userId = UserHandle.getUserId(callerUid);
            synchronized (getLockObject()) {
                List<String> scopes = getUserData(userId).mDelegationMap.get(callerPackage);
                boolean z = false;
                if (scopes != null && scopes.contains(scope)) {
                    try {
                        if (this.mInjector.getPackageManager().getPackageUidAsUser(callerPackage, userId) == callerUid) {
                            z = true;
                        }
                        return z;
                    } catch (PackageManager.NameNotFoundException e) {
                        return false;
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("Unexpected delegation scope: " + scope);
        }
    }

    private void enforceCanManageScope(ComponentName who, String callerPackage, int reqPolicy, String scope) {
        enforceCanManageScopeOrCheckPermission(who, callerPackage, reqPolicy, scope, (String) null);
    }

    private void enforceCanManageScopeOrCheckPermission(ComponentName who, String callerPackage, int reqPolicy, String scope, String permission) {
        if (who != null) {
            synchronized (getLockObject()) {
                getActiveAdminForCallerLocked(who, reqPolicy);
            }
        } else if (!isCallerDelegate(callerPackage, this.mInjector.binderGetCallingUid(), scope)) {
            if (permission != null) {
                this.mContext.enforceCallingOrSelfPermission(permission, (String) null);
                return;
            }
            throw new SecurityException("Caller with uid " + this.mInjector.binderGetCallingUid() + " is not a delegate of scope " + scope + ".");
        }
    }

    private void setDelegatedScopePreO(ComponentName who, String delegatePackage, String scope) {
        Preconditions.checkNotNull(who, "ComponentName is null");
        int userId = this.mInjector.userHandleGetCallingUserId();
        synchronized (getLockObject()) {
            getActiveAdminForCallerLocked(who, -1);
            DevicePolicyData policy = getUserData(userId);
            if (delegatePackage != null) {
                List<String> scopes = policy.mDelegationMap.get(delegatePackage);
                if (scopes == null) {
                    scopes = new ArrayList<>();
                }
                if (!scopes.contains(scope)) {
                    scopes.add(scope);
                    setDelegatedScopes(who, delegatePackage, scopes);
                }
            }
            for (int i = 0; i < policy.mDelegationMap.size(); i++) {
                String currentPackage = policy.mDelegationMap.keyAt(i);
                List<String> currentScopes = policy.mDelegationMap.valueAt(i);
                if (!currentPackage.equals(delegatePackage) && currentScopes.contains(scope)) {
                    List<String> newScopes = new ArrayList<>(currentScopes);
                    newScopes.remove(scope);
                    setDelegatedScopes(who, currentPackage, newScopes);
                }
            }
        }
    }

    public void setCertInstallerPackage(ComponentName who, String installerPackage) throws SecurityException {
        setDelegatedScopePreO(who, installerPackage, "delegation-cert-install");
        DevicePolicyEventLogger.createEvent(25).setAdmin(who).setStrings(new String[]{installerPackage}).write();
    }

    public String getCertInstallerPackage(ComponentName who) throws SecurityException {
        List<String> delegatePackages = getDelegatePackages(who, "delegation-cert-install");
        if (delegatePackages.size() > 0) {
            return delegatePackages.get(0);
        }
        return null;
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    public boolean setAlwaysOnVpnPackage(ComponentName admin, String vpnPackage, boolean lockdown, List<String> lockdownWhitelist) throws SecurityException {
        enforceProfileOrDeviceOwner(admin);
        int userId = this.mInjector.userHandleGetCallingUserId();
        long token = this.mInjector.binderClearCallingIdentity();
        if (vpnPackage != null) {
            try {
                if (!isPackageInstalledForUser(vpnPackage, userId)) {
                    Slog.w(LOG_TAG, "Non-existent VPN package specified: " + vpnPackage);
                    throw new ServiceSpecificException(1, vpnPackage);
                }
            } catch (Throwable th) {
                this.mInjector.binderRestoreCallingIdentity(token);
                throw th;
            }
        }
        if (!(vpnPackage == null || !lockdown || lockdownWhitelist == null)) {
            for (String packageName : lockdownWhitelist) {
                if (!isPackageInstalledForUser(packageName, userId)) {
                    Slog.w(LOG_TAG, "Non-existent package in VPN whitelist: " + packageName);
                    throw new ServiceSpecificException(1, packageName);
                }
            }
        }
        if (this.mInjector.getConnectivityManager().setAlwaysOnVpnPackageForUser(userId, vpnPackage, lockdown, lockdownWhitelist)) {
            int i = 0;
            DevicePolicyEventLogger devicePolicyEventLogger = DevicePolicyEventLogger.createEvent(26).setAdmin(admin).setStrings(new String[]{vpnPackage}).setBoolean(lockdown);
            if (lockdownWhitelist != null) {
                i = lockdownWhitelist.size();
            }
            devicePolicyEventLogger.setInt(i).write();
            this.mInjector.binderRestoreCallingIdentity(token);
            return true;
        }
        throw new UnsupportedOperationException();
    }

    public String getAlwaysOnVpnPackage(ComponentName admin) throws SecurityException {
        enforceProfileOrDeviceOwner(admin);
        int userId = this.mInjector.userHandleGetCallingUserId();
        long token = this.mInjector.binderClearCallingIdentity();
        try {
            return this.mInjector.getConnectivityManager().getAlwaysOnVpnPackageForUser(userId);
        } finally {
            this.mInjector.binderRestoreCallingIdentity(token);
        }
    }

    public boolean isAlwaysOnVpnLockdownEnabled(ComponentName admin) throws SecurityException {
        enforceProfileOrDeviceOwner(admin);
        int userId = this.mInjector.userHandleGetCallingUserId();
        long token = this.mInjector.binderClearCallingIdentity();
        try {
            return this.mInjector.getConnectivityManager().isVpnLockdownEnabled(userId);
        } finally {
            this.mInjector.binderRestoreCallingIdentity(token);
        }
    }

    public List<String> getAlwaysOnVpnLockdownWhitelist(ComponentName admin) throws SecurityException {
        enforceProfileOrDeviceOwner(admin);
        int userId = this.mInjector.userHandleGetCallingUserId();
        long token = this.mInjector.binderClearCallingIdentity();
        try {
            return this.mInjector.getConnectivityManager().getVpnLockdownWhitelist(userId);
        } finally {
            this.mInjector.binderRestoreCallingIdentity(token);
        }
    }

    private void forceWipeDeviceNoLock(boolean wipeExtRequested, String reason, boolean wipeEuicc) {
        wtfIfInLock();
        if (wipeExtRequested) {
            try {
                ((StorageManager) this.mContext.getSystemService("storage")).wipeAdoptableDisks();
            } catch (IOException | SecurityException e) {
                Slog.w(LOG_TAG, "Failed requesting data wipe", e);
                if (0 != 0) {
                    return;
                }
            } catch (Throwable th) {
                if (0 == 0) {
                    SecurityLog.writeEvent(210023, new Object[0]);
                }
                throw th;
            }
        }
        this.mInjector.recoverySystemRebootWipeUserData(false, reason, true, wipeEuicc);
        if (1 != 0) {
            return;
        }
        SecurityLog.writeEvent(210023, new Object[0]);
    }

    private void forceWipeUser(int userId, String wipeReasonForUser, boolean wipeSilently) {
        try {
            IActivityManager am = this.mInjector.getIActivityManager();
            if (am.getCurrentUser().id == userId) {
                am.switchUser(0);
            }
            boolean success = this.mUserManagerInternal.removeUserEvenWhenDisallowed(userId);
            if (!success) {
                Slog.w(LOG_TAG, "Couldn't remove user " + userId);
            } else if (isManagedProfile(userId) && !wipeSilently) {
                sendWipeProfileNotification(wipeReasonForUser);
            }
            if (success) {
                return;
            }
        } catch (RemoteException e) {
            if (0 != 0) {
                return;
            }
        } catch (Throwable th) {
            if (0 == 0) {
                SecurityLog.writeEvent(210023, new Object[0]);
            }
            throw th;
        }
        SecurityLog.writeEvent(210023, new Object[0]);
    }

    public void wipeDataWithReason(int flags, String wipeReasonForUser) {
        ActiveAdmin admin;
        if (this.mHasFeature) {
            Preconditions.checkStringNotEmpty(wipeReasonForUser, "wipeReasonForUser is null or empty");
            enforceFullCrossUsersPermission(this.mInjector.userHandleGetCallingUserId());
            synchronized (getLockObject()) {
                admin = getActiveAdminForCallerLocked((ComponentName) null, 4);
            }
            DevicePolicyEventLogger.createEvent(11).setAdmin(admin.info.getComponent()).setInt(flags).write();
            wipeDataNoLock(admin.info.getComponent(), flags, "DevicePolicyManager.wipeDataWithReason() from " + admin.info.getComponent().flattenToShortString(), wipeReasonForUser, admin.getUserHandle().getIdentifier());
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    private void wipeDataNoLock(ComponentName admin, int flags, String internalReason, String wipeReasonForUser, int userId) {
        String restriction;
        wtfIfInLock();
        long ident = this.mInjector.binderClearCallingIdentity();
        if (userId == 0) {
            restriction = "no_factory_reset";
        } else {
            try {
                if (isManagedProfile(userId)) {
                    restriction = "no_remove_managed_profile";
                } else {
                    restriction = "no_remove_user";
                }
            } catch (Throwable th) {
                this.mInjector.binderRestoreCallingIdentity(ident);
                throw th;
            }
        }
        if (!isAdminAffectedByRestriction(admin, restriction, userId)) {
            if ((flags & 2) != 0) {
                if (isDeviceOwner(admin, userId)) {
                    PersistentDataBlockManager manager = (PersistentDataBlockManager) this.mContext.getSystemService("persistent_data_block");
                    if (manager != null) {
                        manager.wipe();
                    }
                } else {
                    throw new SecurityException("Only device owner admins can set WIPE_RESET_PROTECTION_DATA");
                }
            }
            boolean z = false;
            if (userId == 0) {
                boolean z2 = (flags & 1) != 0;
                if ((flags & 4) != 0) {
                    z = true;
                }
                forceWipeDeviceNoLock(z2, internalReason, z);
            } else {
                if ((flags & 8) != 0) {
                    z = true;
                }
                forceWipeUser(userId, wipeReasonForUser, z);
            }
            this.mInjector.binderRestoreCallingIdentity(ident);
            return;
        }
        throw new SecurityException("Cannot wipe data. " + restriction + " restriction is set for user " + userId);
    }

    private void sendWipeProfileNotification(String wipeReasonForUser) {
        this.mInjector.getNotificationManager().notify(1001, new Notification.Builder(this.mContext, SystemNotificationChannels.DEVICE_ADMIN).setSmallIcon(17301642).setContentTitle(this.mContext.getString(17041436)).setContentText(wipeReasonForUser).setColor(this.mContext.getColor(17170460)).setStyle(new Notification.BigTextStyle().bigText(wipeReasonForUser)).build());
    }

    /* access modifiers changed from: private */
    public void clearWipeProfileNotification() {
        this.mInjector.getNotificationManager().cancel(1001);
    }

    public void getRemoveWarning(ComponentName comp, RemoteCallback result, int userHandle) {
        final RemoteCallback remoteCallback = result;
        int i = userHandle;
        if (this.mHasFeature) {
            enforceFullCrossUsersPermission(i);
            this.mContext.enforceCallingOrSelfPermission("android.permission.BIND_DEVICE_ADMIN", (String) null);
            synchronized (getLockObject()) {
                ActiveAdmin admin = getActiveAdminUncheckedLocked(comp, i);
                if (admin == null) {
                    remoteCallback.sendResult((Bundle) null);
                    return;
                }
                Intent intent = new Intent("android.app.action.DEVICE_ADMIN_DISABLE_REQUESTED");
                intent.setFlags(268435456);
                intent.setComponent(admin.info.getComponent());
                this.mContext.sendOrderedBroadcastAsUser(intent, new UserHandle(i), (String) null, new BroadcastReceiver() {
                    public void onReceive(Context context, Intent intent) {
                        remoteCallback.sendResult(getResultExtras(false));
                    }
                }, (Handler) null, -1, (String) null, (Bundle) null);
            }
        }
    }

    public void setActivePasswordState(PasswordMetrics metrics, int userHandle) {
        if (this.mLockPatternUtils.hasSecureLockScreen()) {
            enforceFullCrossUsersPermission(userHandle);
            this.mContext.enforceCallingOrSelfPermission("android.permission.BIND_DEVICE_ADMIN", (String) null);
            if (isManagedProfile(userHandle) && !isSeparateProfileChallengeEnabled(userHandle)) {
                metrics = new PasswordMetrics();
            }
            validateQualityConstant(metrics.quality);
            synchronized (getLockObject()) {
                this.mUserPasswordMetrics.put(userHandle, metrics);
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    public void reportPasswordChanged(int userId) {
        if (this.mHasFeature && this.mLockPatternUtils.hasSecureLockScreen()) {
            enforceFullCrossUsersPermission(userId);
            if (!isSeparateProfileChallengeEnabled(userId)) {
                enforceNotManagedProfile(userId, "set the active password");
            }
            this.mContext.enforceCallingOrSelfPermission("android.permission.BIND_DEVICE_ADMIN", (String) null);
            DevicePolicyData policy = getUserData(userId);
            long ident = this.mInjector.binderClearCallingIdentity();
            try {
                synchronized (getLockObject()) {
                    policy.mFailedPasswordAttempts = 0;
                    updatePasswordValidityCheckpointLocked(userId, false);
                    saveSettingsLocked(userId);
                    updatePasswordExpirationsLocked(userId);
                    setExpirationAlarmCheckLocked(this.mContext, userId, false);
                    sendAdminCommandForLockscreenPoliciesLocked("android.app.action.ACTION_PASSWORD_CHANGED", 0, userId);
                }
                removeCaApprovalsIfNeeded(userId);
                this.mInjector.binderRestoreCallingIdentity(ident);
            } catch (Throwable th) {
                this.mInjector.binderRestoreCallingIdentity(ident);
                throw th;
            }
        }
    }

    private void updatePasswordExpirationsLocked(int userHandle) {
        ArraySet<Integer> affectedUserIds = new ArraySet<>();
        List<ActiveAdmin> admins = getActiveAdminsForLockscreenPoliciesLocked(userHandle, false);
        int N = admins.size();
        for (int i = 0; i < N; i++) {
            ActiveAdmin admin = admins.get(i);
            if (admin.info.usesPolicy(6)) {
                affectedUserIds.add(Integer.valueOf(admin.getUserHandle().getIdentifier()));
                long timeout = admin.passwordExpirationTimeout;
                long expiration = 0;
                if (timeout > 0) {
                    expiration = System.currentTimeMillis() + timeout;
                }
                admin.passwordExpirationDate = expiration;
            }
        }
        Iterator<Integer> it = affectedUserIds.iterator();
        while (it.hasNext()) {
            saveSettingsLocked(it.next().intValue());
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 16 */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x005d, code lost:
        if (r13 == false) goto L_0x00c7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x005f, code lost:
        if (r14 == null) goto L_0x00c7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0061, code lost:
        r15 = r14.getUserHandle().getIdentifier();
        android.util.Slog.i(LOG_TAG, "Max failed password attempts policy reached for admin: " + r14.info.getComponent().flattenToShortString() + ". Calling wipeData for user " + r15);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:?, code lost:
        wipeDataNoLock(r14.info.getComponent(), 0, "reportFailedPasswordAttempt()", r7.mContext.getString(17041440), r15);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x00ab, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x00ac, code lost:
        android.util.Slog.w(LOG_TAG, "Failed to wipe user " + r15 + " after max failed password attempts reached.", r0);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void reportFailedPasswordAttempt(int r17) {
        /*
            r16 = this;
            r7 = r16
            r8 = r17
            r16.enforceFullCrossUsersPermission(r17)
            boolean r0 = r16.isSeparateProfileChallengeEnabled(r17)
            if (r0 != 0) goto L_0x0013
            java.lang.String r0 = "report failed password attempt if separate profile challenge is not in place"
            r7.enforceNotManagedProfile(r8, r0)
        L_0x0013:
            android.content.Context r0 = r7.mContext
            r1 = 0
            java.lang.String r2 = "android.permission.BIND_DEVICE_ADMIN"
            r0.enforceCallingOrSelfPermission(r2, r1)
            r1 = 0
            r2 = 0
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r0 = r7.mInjector
            long r9 = r0.binderClearCallingIdentity()
            java.lang.Object r3 = r16.getLockObject()     // Catch:{ all -> 0x00ec }
            monitor-enter(r3)     // Catch:{ all -> 0x00ec }
            com.android.server.devicepolicy.DevicePolicyManagerService$DevicePolicyData r0 = r16.getUserData(r17)     // Catch:{ all -> 0x00e9 }
            int r4 = r0.mFailedPasswordAttempts     // Catch:{ all -> 0x00e9 }
            r11 = 1
            int r4 = r4 + r11
            r0.mFailedPasswordAttempts = r4     // Catch:{ all -> 0x00e9 }
            r16.saveSettingsLocked(r17)     // Catch:{ all -> 0x00e9 }
            boolean r4 = r7.mHasFeature     // Catch:{ all -> 0x00e9 }
            r12 = 0
            if (r4 == 0) goto L_0x0054
            com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin r4 = r7.getAdminWithMinimumFailedPasswordsForWipeLocked(r8, r12)     // Catch:{ all -> 0x00e9 }
            r2 = r4
            if (r2 == 0) goto L_0x0044
            int r4 = r2.maximumFailedPasswordsForWipe     // Catch:{ all -> 0x00e9 }
            goto L_0x0045
        L_0x0044:
            r4 = r12
        L_0x0045:
            if (r4 <= 0) goto L_0x004c
            int r5 = r0.mFailedPasswordAttempts     // Catch:{ all -> 0x00e9 }
            if (r5 < r4) goto L_0x004c
            r1 = 1
        L_0x004c:
            java.lang.String r5 = "android.app.action.ACTION_PASSWORD_FAILED"
            r7.sendAdminCommandForLockscreenPoliciesLocked(r5, r11, r8)     // Catch:{ all -> 0x00e9 }
            r13 = r1
            r14 = r2
            goto L_0x0056
        L_0x0054:
            r13 = r1
            r14 = r2
        L_0x0056:
            monitor-exit(r3)     // Catch:{ all -> 0x00e5 }
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r0 = r7.mInjector
            r0.binderRestoreCallingIdentity(r9)
            if (r13 == 0) goto L_0x00c7
            if (r14 == 0) goto L_0x00c7
            android.os.UserHandle r0 = r14.getUserHandle()
            int r15 = r0.getIdentifier()
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "Max failed password attempts policy reached for admin: "
            r0.append(r1)
            android.app.admin.DeviceAdminInfo r1 = r14.info
            android.content.ComponentName r1 = r1.getComponent()
            java.lang.String r1 = r1.flattenToShortString()
            r0.append(r1)
            java.lang.String r1 = ". Calling wipeData for user "
            r0.append(r1)
            r0.append(r15)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "DevicePolicyManager"
            android.util.Slog.i(r1, r0)
            android.content.Context r0 = r7.mContext     // Catch:{ SecurityException -> 0x00ab }
            r1 = 17041440(0x1040820, float:2.42504E-38)
            java.lang.String r5 = r0.getString(r1)     // Catch:{ SecurityException -> 0x00ab }
            android.app.admin.DeviceAdminInfo r0 = r14.info     // Catch:{ SecurityException -> 0x00ab }
            android.content.ComponentName r2 = r0.getComponent()     // Catch:{ SecurityException -> 0x00ab }
            r3 = 0
            java.lang.String r4 = "reportFailedPasswordAttempt()"
            r1 = r16
            r6 = r15
            r1.wipeDataNoLock(r2, r3, r4, r5, r6)     // Catch:{ SecurityException -> 0x00ab }
            goto L_0x00c7
        L_0x00ab:
            r0 = move-exception
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Failed to wipe user "
            r1.append(r2)
            r1.append(r15)
            java.lang.String r2 = " after max failed password attempts reached."
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "DevicePolicyManager"
            android.util.Slog.w(r2, r1, r0)
        L_0x00c7:
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r0 = r7.mInjector
            boolean r0 = r0.securityLogIsLoggingEnabled()
            if (r0 == 0) goto L_0x00e4
            r0 = 210007(0x33457, float:2.94282E-40)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.Integer r2 = java.lang.Integer.valueOf(r12)
            r1[r12] = r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r11)
            r1[r11] = r2
            android.app.admin.SecurityLog.writeEvent(r0, r1)
        L_0x00e4:
            return
        L_0x00e5:
            r0 = move-exception
            r1 = r13
            r2 = r14
            goto L_0x00ea
        L_0x00e9:
            r0 = move-exception
        L_0x00ea:
            monitor-exit(r3)     // Catch:{ all -> 0x00e9 }
            throw r0     // Catch:{ all -> 0x00ec }
        L_0x00ec:
            r0 = move-exception
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r3 = r7.mInjector
            r3.binderRestoreCallingIdentity(r9)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.DevicePolicyManagerService.reportFailedPasswordAttempt(int):void");
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    public void reportSuccessfulPasswordAttempt(int userHandle) {
        enforceFullCrossUsersPermission(userHandle);
        this.mContext.enforceCallingOrSelfPermission("android.permission.BIND_DEVICE_ADMIN", (String) null);
        synchronized (getLockObject()) {
            DevicePolicyData policy = getUserData(userHandle);
            if (policy.mFailedPasswordAttempts != 0 || policy.mPasswordOwner >= 0) {
                long ident = this.mInjector.binderClearCallingIdentity();
                try {
                    policy.mFailedPasswordAttempts = 0;
                    policy.mPasswordOwner = -1;
                    saveSettingsLocked(userHandle);
                    if (this.mHasFeature) {
                        sendAdminCommandForLockscreenPoliciesLocked("android.app.action.ACTION_PASSWORD_SUCCEEDED", 1, userHandle);
                    }
                } finally {
                    this.mInjector.binderRestoreCallingIdentity(ident);
                }
            }
        }
        if (this.mInjector.securityLogIsLoggingEnabled()) {
            SecurityLog.writeEvent(210007, new Object[]{1, 1});
        }
    }

    public void reportFailedBiometricAttempt(int userHandle) {
        enforceFullCrossUsersPermission(userHandle);
        this.mContext.enforceCallingOrSelfPermission("android.permission.BIND_DEVICE_ADMIN", (String) null);
        if (this.mInjector.securityLogIsLoggingEnabled()) {
            SecurityLog.writeEvent(210007, new Object[]{0, 0});
        }
    }

    public void reportSuccessfulBiometricAttempt(int userHandle) {
        enforceFullCrossUsersPermission(userHandle);
        this.mContext.enforceCallingOrSelfPermission("android.permission.BIND_DEVICE_ADMIN", (String) null);
        if (this.mInjector.securityLogIsLoggingEnabled()) {
            SecurityLog.writeEvent(210007, new Object[]{1, 0});
        }
    }

    public void reportKeyguardDismissed(int userHandle) {
        enforceFullCrossUsersPermission(userHandle);
        this.mContext.enforceCallingOrSelfPermission("android.permission.BIND_DEVICE_ADMIN", (String) null);
        if (this.mInjector.securityLogIsLoggingEnabled()) {
            SecurityLog.writeEvent(210006, new Object[0]);
        }
    }

    public void reportKeyguardSecured(int userHandle) {
        enforceFullCrossUsersPermission(userHandle);
        this.mContext.enforceCallingOrSelfPermission("android.permission.BIND_DEVICE_ADMIN", (String) null);
        if (this.mInjector.securityLogIsLoggingEnabled()) {
            SecurityLog.writeEvent(210008, new Object[0]);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 10 */
    public ComponentName setGlobalProxy(ComponentName who, String proxySpec, String exclusionList) {
        if (!this.mHasFeature) {
            return null;
        }
        synchronized (getLockObject()) {
            Preconditions.checkNotNull(who, "ComponentName is null");
            DevicePolicyData policy = getUserData(0);
            ActiveAdmin admin = getActiveAdminForCallerLocked(who, 5);
            for (ComponentName component : policy.mAdminMap.keySet()) {
                if (policy.mAdminMap.get(component).specifiesGlobalProxy && !component.equals(who)) {
                    return component;
                }
            }
            if (UserHandle.getCallingUserId() != 0) {
                Slog.w(LOG_TAG, "Only the owner is allowed to set the global proxy. User " + UserHandle.getCallingUserId() + " is not permitted.");
                return null;
            }
            if (proxySpec == null) {
                admin.specifiesGlobalProxy = false;
                admin.globalProxySpec = null;
                admin.globalProxyExclusionList = null;
            } else {
                admin.specifiesGlobalProxy = true;
                admin.globalProxySpec = proxySpec;
                admin.globalProxyExclusionList = exclusionList;
            }
            long origId = this.mInjector.binderClearCallingIdentity();
            try {
                resetGlobalProxyLocked(policy);
                return null;
            } finally {
                this.mInjector.binderRestoreCallingIdentity(origId);
            }
        }
    }

    public ComponentName getGlobalProxyAdmin(int userHandle) {
        if (!this.mHasFeature) {
            return null;
        }
        enforceFullCrossUsersPermission(userHandle);
        synchronized (getLockObject()) {
            DevicePolicyData policy = getUserData(0);
            int N = policy.mAdminList.size();
            for (int i = 0; i < N; i++) {
                ActiveAdmin ap = policy.mAdminList.get(i);
                if (ap.specifiesGlobalProxy) {
                    ComponentName component = ap.info.getComponent();
                    return component;
                }
            }
            return null;
        }
    }

    public void setRecommendedGlobalProxy(ComponentName who, ProxyInfo proxyInfo) {
        enforceDeviceOwner(who);
        long token = this.mInjector.binderClearCallingIdentity();
        try {
            this.mInjector.getConnectivityManager().setGlobalProxy(proxyInfo);
        } finally {
            this.mInjector.binderRestoreCallingIdentity(token);
        }
    }

    private void resetGlobalProxyLocked(DevicePolicyData policy) {
        int N = policy.mAdminList.size();
        for (int i = 0; i < N; i++) {
            ActiveAdmin ap = policy.mAdminList.get(i);
            if (ap.specifiesGlobalProxy) {
                saveGlobalProxyLocked(ap.globalProxySpec, ap.globalProxyExclusionList);
                return;
            }
        }
        saveGlobalProxyLocked((String) null, (String) null);
    }

    private void saveGlobalProxyLocked(String proxySpec, String exclusionList) {
        if (exclusionList == null) {
            exclusionList = "";
        }
        if (proxySpec == null) {
            proxySpec = "";
        }
        String[] data = proxySpec.trim().split(":");
        int proxyPort = 8080;
        if (data.length > 1) {
            try {
                proxyPort = Integer.parseInt(data[1]);
            } catch (NumberFormatException e) {
            }
        }
        String exclusionList2 = exclusionList.trim();
        ProxyInfo proxyProperties = new ProxyInfo(data[0], proxyPort, exclusionList2);
        if (!proxyProperties.isValid()) {
            Slog.e(LOG_TAG, "Invalid proxy properties, ignoring: " + proxyProperties.toString());
            return;
        }
        this.mInjector.settingsGlobalPutString("global_http_proxy_host", data[0]);
        this.mInjector.settingsGlobalPutInt("global_http_proxy_port", proxyPort);
        this.mInjector.settingsGlobalPutString("global_http_proxy_exclusion_list", exclusionList2);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0072, code lost:
        return r6;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int setStorageEncryption(android.content.ComponentName r9, boolean r10) {
        /*
            r8 = this;
            boolean r0 = r8.mHasFeature
            r1 = 0
            if (r0 != 0) goto L_0x0006
            return r1
        L_0x0006:
            java.lang.String r0 = "ComponentName is null"
            com.android.internal.util.Preconditions.checkNotNull(r9, r0)
            int r0 = android.os.UserHandle.getCallingUserId()
            java.lang.Object r2 = r8.getLockObject()
            monitor-enter(r2)
            if (r0 == 0) goto L_0x0037
            java.lang.String r3 = "DevicePolicyManager"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0073 }
            r4.<init>()     // Catch:{ all -> 0x0073 }
            java.lang.String r5 = "Only owner/system user is allowed to set storage encryption. User "
            r4.append(r5)     // Catch:{ all -> 0x0073 }
            int r5 = android.os.UserHandle.getCallingUserId()     // Catch:{ all -> 0x0073 }
            r4.append(r5)     // Catch:{ all -> 0x0073 }
            java.lang.String r5 = " is not permitted."
            r4.append(r5)     // Catch:{ all -> 0x0073 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0073 }
            android.util.Slog.w(r3, r4)     // Catch:{ all -> 0x0073 }
            monitor-exit(r2)     // Catch:{ all -> 0x0073 }
            return r1
        L_0x0037:
            r3 = 7
            com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin r3 = r8.getActiveAdminForCallerLocked(r9, r3)     // Catch:{ all -> 0x0073 }
            boolean r4 = r8.isEncryptionSupported()     // Catch:{ all -> 0x0073 }
            if (r4 != 0) goto L_0x0044
            monitor-exit(r2)     // Catch:{ all -> 0x0073 }
            return r1
        L_0x0044:
            boolean r4 = r3.encryptionRequested     // Catch:{ all -> 0x0073 }
            if (r4 == r10) goto L_0x004d
            r3.encryptionRequested = r10     // Catch:{ all -> 0x0073 }
            r8.saveSettingsLocked(r0)     // Catch:{ all -> 0x0073 }
        L_0x004d:
            com.android.server.devicepolicy.DevicePolicyManagerService$DevicePolicyData r1 = r8.getUserData(r1)     // Catch:{ all -> 0x0073 }
            r4 = 0
            java.util.ArrayList<com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin> r5 = r1.mAdminList     // Catch:{ all -> 0x0073 }
            int r5 = r5.size()     // Catch:{ all -> 0x0073 }
            r6 = 0
        L_0x0059:
            if (r6 >= r5) goto L_0x0069
            java.util.ArrayList<com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin> r7 = r1.mAdminList     // Catch:{ all -> 0x0073 }
            java.lang.Object r7 = r7.get(r6)     // Catch:{ all -> 0x0073 }
            com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin r7 = (com.android.server.devicepolicy.DevicePolicyManagerService.ActiveAdmin) r7     // Catch:{ all -> 0x0073 }
            boolean r7 = r7.encryptionRequested     // Catch:{ all -> 0x0073 }
            r4 = r4 | r7
            int r6 = r6 + 1
            goto L_0x0059
        L_0x0069:
            r8.setEncryptionRequested(r4)     // Catch:{ all -> 0x0073 }
            if (r4 == 0) goto L_0x0070
            r6 = 3
            goto L_0x0071
        L_0x0070:
            r6 = 1
        L_0x0071:
            monitor-exit(r2)     // Catch:{ all -> 0x0073 }
            return r6
        L_0x0073:
            r1 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0073 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.DevicePolicyManagerService.setStorageEncryption(android.content.ComponentName, boolean):int");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0019, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean getStorageEncryption(android.content.ComponentName r7, int r8) {
        /*
            r6 = this;
            boolean r0 = r6.mHasFeature
            r1 = 0
            if (r0 != 0) goto L_0x0006
            return r1
        L_0x0006:
            r6.enforceFullCrossUsersPermission(r8)
            java.lang.Object r0 = r6.getLockObject()
            monitor-enter(r0)
            if (r7 == 0) goto L_0x001a
            com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin r2 = r6.getActiveAdminUncheckedLocked(r7, r8)     // Catch:{ all -> 0x003b }
            if (r2 == 0) goto L_0x0018
            boolean r1 = r2.encryptionRequested     // Catch:{ all -> 0x003b }
        L_0x0018:
            monitor-exit(r0)     // Catch:{ all -> 0x003b }
            return r1
        L_0x001a:
            com.android.server.devicepolicy.DevicePolicyManagerService$DevicePolicyData r2 = r6.getUserData(r8)     // Catch:{ all -> 0x003b }
            java.util.ArrayList<com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin> r3 = r2.mAdminList     // Catch:{ all -> 0x003b }
            int r3 = r3.size()     // Catch:{ all -> 0x003b }
            r4 = 0
        L_0x0025:
            if (r4 >= r3) goto L_0x0039
            java.util.ArrayList<com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin> r5 = r2.mAdminList     // Catch:{ all -> 0x003b }
            java.lang.Object r5 = r5.get(r4)     // Catch:{ all -> 0x003b }
            com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin r5 = (com.android.server.devicepolicy.DevicePolicyManagerService.ActiveAdmin) r5     // Catch:{ all -> 0x003b }
            boolean r5 = r5.encryptionRequested     // Catch:{ all -> 0x003b }
            if (r5 == 0) goto L_0x0036
            monitor-exit(r0)     // Catch:{ all -> 0x003b }
            r0 = 1
            return r0
        L_0x0036:
            int r4 = r4 + 1
            goto L_0x0025
        L_0x0039:
            monitor-exit(r0)     // Catch:{ all -> 0x003b }
            return r1
        L_0x003b:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x003b }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.DevicePolicyManagerService.getStorageEncryption(android.content.ComponentName, int):boolean");
    }

    public int getStorageEncryptionStatus(String callerPackage, int userHandle) {
        boolean z = this.mHasFeature;
        enforceFullCrossUsersPermission(userHandle);
        ensureCallerPackage(callerPackage);
        try {
            boolean legacyApp = false;
            if (this.mIPackageManager.getApplicationInfo(callerPackage, 0, userHandle).targetSdkVersion <= 23) {
                legacyApp = true;
            }
            int rawStatus = getEncryptionStatus();
            if (rawStatus != 5 || !legacyApp) {
                return rawStatus;
            }
            return 3;
        } catch (RemoteException e) {
            throw new SecurityException(e);
        }
    }

    private boolean isEncryptionSupported() {
        return getEncryptionStatus() != 0;
    }

    private int getEncryptionStatus() {
        if (this.mInjector.storageManagerIsFileBasedEncryptionEnabled()) {
            return 5;
        }
        if (this.mInjector.storageManagerIsNonDefaultBlockEncrypted()) {
            return 3;
        }
        if (this.mInjector.storageManagerIsEncrypted()) {
            return 4;
        }
        if (this.mInjector.storageManagerIsEncryptable()) {
            return 1;
        }
        return 0;
    }

    private void setEncryptionRequested(boolean encrypt) {
    }

    public void setScreenCaptureDisabled(ComponentName who, boolean disabled) {
        if (this.mHasFeature) {
            Preconditions.checkNotNull(who, "ComponentName is null");
            int userHandle = UserHandle.getCallingUserId();
            synchronized (getLockObject()) {
                ActiveAdmin ap = getActiveAdminForCallerLocked(who, -1);
                if (ap.disableScreenCapture != disabled) {
                    ap.disableScreenCapture = disabled;
                    saveSettingsLocked(userHandle);
                    updateScreenCaptureDisabled(userHandle, disabled);
                }
            }
            DevicePolicyEventLogger.createEvent(29).setAdmin(who).setBoolean(disabled).write();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0016, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean getScreenCaptureDisabled(android.content.ComponentName r8, int r9) {
        /*
            r7 = this;
            boolean r0 = r7.mHasFeature
            r1 = 0
            if (r0 != 0) goto L_0x0006
            return r1
        L_0x0006:
            java.lang.Object r0 = r7.getLockObject()
            monitor-enter(r0)
            if (r8 == 0) goto L_0x0017
            com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin r2 = r7.getActiveAdminUncheckedLocked(r8, r9)     // Catch:{ all -> 0x0038 }
            if (r2 == 0) goto L_0x0015
            boolean r1 = r2.disableScreenCapture     // Catch:{ all -> 0x0038 }
        L_0x0015:
            monitor-exit(r0)     // Catch:{ all -> 0x0038 }
            return r1
        L_0x0017:
            com.android.server.devicepolicy.DevicePolicyManagerService$DevicePolicyData r2 = r7.getUserData(r9)     // Catch:{ all -> 0x0038 }
            java.util.ArrayList<com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin> r3 = r2.mAdminList     // Catch:{ all -> 0x0038 }
            int r3 = r3.size()     // Catch:{ all -> 0x0038 }
            r4 = 0
        L_0x0022:
            if (r4 >= r3) goto L_0x0036
            java.util.ArrayList<com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin> r5 = r2.mAdminList     // Catch:{ all -> 0x0038 }
            java.lang.Object r5 = r5.get(r4)     // Catch:{ all -> 0x0038 }
            com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin r5 = (com.android.server.devicepolicy.DevicePolicyManagerService.ActiveAdmin) r5     // Catch:{ all -> 0x0038 }
            boolean r6 = r5.disableScreenCapture     // Catch:{ all -> 0x0038 }
            if (r6 == 0) goto L_0x0033
            monitor-exit(r0)     // Catch:{ all -> 0x0038 }
            r0 = 1
            return r0
        L_0x0033:
            int r4 = r4 + 1
            goto L_0x0022
        L_0x0036:
            monitor-exit(r0)     // Catch:{ all -> 0x0038 }
            return r1
        L_0x0038:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0038 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.DevicePolicyManagerService.getScreenCaptureDisabled(android.content.ComponentName, int):boolean");
    }

    private void updateScreenCaptureDisabled(final int userHandle, boolean disabled) {
        this.mPolicyCache.setScreenCaptureDisabled(userHandle, disabled);
        this.mHandler.post(new Runnable() {
            public void run() {
                try {
                    DevicePolicyManagerService.this.mInjector.getIWindowManager().refreshScreenCaptureDisabled(userHandle);
                } catch (RemoteException e) {
                    Log.w(DevicePolicyManagerService.LOG_TAG, "Unable to notify WindowManager.", e);
                }
            }
        });
    }

    public void setAutoTimeRequired(ComponentName who, boolean required) {
        if (this.mHasFeature) {
            Preconditions.checkNotNull(who, "ComponentName is null");
            int userHandle = UserHandle.getCallingUserId();
            synchronized (getLockObject()) {
                ActiveAdmin admin = getActiveAdminForCallerLocked(who, -1);
                if (admin.requireAutoTime != required) {
                    admin.requireAutoTime = required;
                    saveSettingsLocked(userHandle);
                }
            }
            if (required) {
                long ident = this.mInjector.binderClearCallingIdentity();
                try {
                    this.mInjector.settingsGlobalPutInt("auto_time", 1);
                } finally {
                    this.mInjector.binderRestoreCallingIdentity(ident);
                }
            }
            DevicePolicyEventLogger.createEvent(36).setAdmin(who).setBoolean(required).write();
        }
    }

    public boolean getAutoTimeRequired() {
        if (!this.mHasFeature) {
            return false;
        }
        synchronized (getLockObject()) {
            ActiveAdmin deviceOwner = getDeviceOwnerAdminLocked();
            if (deviceOwner != null && deviceOwner.requireAutoTime) {
                return true;
            }
            for (Integer userId : this.mOwners.getProfileOwnerKeys()) {
                ActiveAdmin profileOwner = getProfileOwnerAdminLocked(userId.intValue());
                if (profileOwner != null && profileOwner.requireAutoTime) {
                    return true;
                }
            }
            return false;
        }
    }

    public void setForceEphemeralUsers(ComponentName who, boolean forceEphemeralUsers) {
        if (this.mHasFeature) {
            Preconditions.checkNotNull(who, "ComponentName is null");
            if (!forceEphemeralUsers || this.mInjector.userManagerIsSplitSystemUser()) {
                boolean removeAllUsers = false;
                synchronized (getLockObject()) {
                    ActiveAdmin deviceOwner = getActiveAdminForCallerLocked(who, -2);
                    if (deviceOwner.forceEphemeralUsers != forceEphemeralUsers) {
                        deviceOwner.forceEphemeralUsers = forceEphemeralUsers;
                        saveSettingsLocked(this.mInjector.userHandleGetCallingUserId());
                        this.mUserManagerInternal.setForceEphemeralUsers(forceEphemeralUsers);
                        removeAllUsers = forceEphemeralUsers;
                    }
                }
                if (removeAllUsers) {
                    long identitity = this.mInjector.binderClearCallingIdentity();
                    try {
                        this.mUserManagerInternal.removeAllUsers();
                    } finally {
                        this.mInjector.binderRestoreCallingIdentity(identitity);
                    }
                }
            } else {
                throw new UnsupportedOperationException("Cannot force ephemeral users on systems without split system user.");
            }
        }
    }

    public boolean getForceEphemeralUsers(ComponentName who) {
        boolean z;
        if (!this.mHasFeature) {
            return false;
        }
        Preconditions.checkNotNull(who, "ComponentName is null");
        synchronized (getLockObject()) {
            z = getActiveAdminForCallerLocked(who, -2).forceEphemeralUsers;
        }
        return z;
    }

    private void ensureDeviceOwnerAndAllUsersAffiliated(ComponentName who) throws SecurityException {
        synchronized (getLockObject()) {
            getActiveAdminForCallerLocked(who, -2);
        }
        ensureAllUsersAffiliated();
    }

    /* Debug info: failed to restart local var, previous not found, register: 3 */
    private void ensureAllUsersAffiliated() throws SecurityException {
        synchronized (getLockObject()) {
            if (!areAllUsersAffiliatedWithDeviceLocked()) {
                throw new SecurityException("Not all users are affiliated.");
            }
        }
    }

    public boolean requestBugreport(ComponentName who) {
        if (!this.mHasFeature) {
            return false;
        }
        Preconditions.checkNotNull(who, "ComponentName is null");
        ensureDeviceOwnerAndAllUsersAffiliated(who);
        if (this.mRemoteBugreportServiceIsActive.get() || getDeviceOwnerRemoteBugreportUri() != null) {
            Slog.d(LOG_TAG, "Remote bugreport wasn't started because there's already one running.");
            return false;
        }
        long currentTime = System.currentTimeMillis();
        synchronized (getLockObject()) {
            DevicePolicyData policyData = getUserData(0);
            if (currentTime > policyData.mLastBugReportRequestTime) {
                policyData.mLastBugReportRequestTime = currentTime;
                saveSettingsLocked(0);
            }
        }
        long callingIdentity = this.mInjector.binderClearCallingIdentity();
        try {
            this.mInjector.getIActivityManager().requestBugReport(2);
            this.mRemoteBugreportServiceIsActive.set(true);
            this.mRemoteBugreportSharingAccepted.set(false);
            registerRemoteBugreportReceivers();
            this.mInjector.getNotificationManager().notifyAsUser(LOG_TAG, 678432343, RemoteBugreportUtils.buildNotification(this.mContext, 1), UserHandle.ALL);
            this.mHandler.postDelayed(this.mRemoteBugreportTimeoutRunnable, 600000);
            DevicePolicyEventLogger.createEvent(53).setAdmin(who).write();
            return true;
        } catch (RemoteException re) {
            Slog.e(LOG_TAG, "Failed to make remote calls to start bugreportremote service", re);
            return false;
        } finally {
            this.mInjector.binderRestoreCallingIdentity(callingIdentity);
        }
    }

    /* access modifiers changed from: package-private */
    public void sendDeviceOwnerCommand(String action, Bundle extras) {
        int deviceOwnerUserId;
        synchronized (getLockObject()) {
            deviceOwnerUserId = this.mOwners.getDeviceOwnerUserId();
        }
        ComponentName receiverComponent = null;
        if (action.equals("android.app.action.NETWORK_LOGS_AVAILABLE")) {
            receiverComponent = resolveDelegateReceiver("delegation-network-logging", action, deviceOwnerUserId);
        }
        if (receiverComponent == null) {
            synchronized (getLockObject()) {
                receiverComponent = this.mOwners.getDeviceOwnerComponent();
            }
        }
        sendActiveAdminCommand(action, extras, deviceOwnerUserId, receiverComponent);
    }

    private void sendProfileOwnerCommand(String action, Bundle extras, int userHandle) {
        sendActiveAdminCommand(action, extras, userHandle, this.mOwners.getProfileOwnerComponent(userHandle));
    }

    private void sendActiveAdminCommand(String action, Bundle extras, int userHandle, ComponentName receiverComponent) {
        Intent intent = new Intent(action);
        intent.setComponent(receiverComponent);
        if (extras != null) {
            intent.putExtras(extras);
        }
        this.mContext.sendBroadcastAsUser(intent, UserHandle.of(userHandle));
    }

    private void sendOwnerChangedBroadcast(String broadcast, int userId) {
        this.mContext.sendBroadcastAsUser(new Intent(broadcast).addFlags(DumpState.DUMP_SERVICE_PERMISSIONS), UserHandle.of(userId));
    }

    /* access modifiers changed from: private */
    public String getDeviceOwnerRemoteBugreportUri() {
        String deviceOwnerRemoteBugreportUri;
        synchronized (getLockObject()) {
            deviceOwnerRemoteBugreportUri = this.mOwners.getDeviceOwnerRemoteBugreportUri();
        }
        return deviceOwnerRemoteBugreportUri;
    }

    private void setDeviceOwnerRemoteBugreportUriAndHash(String bugreportUri, String bugreportHash) {
        synchronized (getLockObject()) {
            this.mOwners.setDeviceOwnerRemoteBugreportUriAndHash(bugreportUri, bugreportHash);
        }
    }

    private void registerRemoteBugreportReceivers() {
        try {
            this.mContext.registerReceiver(this.mRemoteBugreportFinishedReceiver, new IntentFilter("android.intent.action.REMOTE_BUGREPORT_DISPATCH", "application/vnd.android.bugreport"));
        } catch (IntentFilter.MalformedMimeTypeException e) {
            Slog.w(LOG_TAG, "Failed to set type application/vnd.android.bugreport", e);
        }
        IntentFilter filterConsent = new IntentFilter();
        filterConsent.addAction("com.android.server.action.REMOTE_BUGREPORT_SHARING_DECLINED");
        filterConsent.addAction("com.android.server.action.REMOTE_BUGREPORT_SHARING_ACCEPTED");
        this.mContext.registerReceiver(this.mRemoteBugreportConsentReceiver, filterConsent);
    }

    /* access modifiers changed from: private */
    public void onBugreportFinished(Intent intent) {
        this.mHandler.removeCallbacks(this.mRemoteBugreportTimeoutRunnable);
        this.mRemoteBugreportServiceIsActive.set(false);
        Uri bugreportUri = intent.getData();
        String bugreportUriString = null;
        if (bugreportUri != null) {
            bugreportUriString = bugreportUri.toString();
        }
        String bugreportHash = intent.getStringExtra("android.intent.extra.REMOTE_BUGREPORT_HASH");
        if (this.mRemoteBugreportSharingAccepted.get()) {
            shareBugreportWithDeviceOwnerIfExists(bugreportUriString, bugreportHash);
            this.mInjector.getNotificationManager().cancel(LOG_TAG, 678432343);
        } else {
            setDeviceOwnerRemoteBugreportUriAndHash(bugreportUriString, bugreportHash);
            this.mInjector.getNotificationManager().notifyAsUser(LOG_TAG, 678432343, RemoteBugreportUtils.buildNotification(this.mContext, 3), UserHandle.ALL);
        }
        this.mContext.unregisterReceiver(this.mRemoteBugreportFinishedReceiver);
    }

    /* access modifiers changed from: private */
    public void onBugreportFailed() {
        this.mRemoteBugreportServiceIsActive.set(false);
        this.mInjector.systemPropertiesSet("ctl.stop", "bugreportremote");
        this.mRemoteBugreportSharingAccepted.set(false);
        setDeviceOwnerRemoteBugreportUriAndHash((String) null, (String) null);
        this.mInjector.getNotificationManager().cancel(LOG_TAG, 678432343);
        Bundle extras = new Bundle();
        extras.putInt("android.app.extra.BUGREPORT_FAILURE_REASON", 0);
        sendDeviceOwnerCommand("android.app.action.BUGREPORT_FAILED", extras);
        this.mContext.unregisterReceiver(this.mRemoteBugreportConsentReceiver);
        this.mContext.unregisterReceiver(this.mRemoteBugreportFinishedReceiver);
    }

    /* access modifiers changed from: private */
    public void onBugreportSharingAccepted() {
        String bugreportUriString;
        String bugreportHash;
        this.mRemoteBugreportSharingAccepted.set(true);
        synchronized (getLockObject()) {
            bugreportUriString = getDeviceOwnerRemoteBugreportUri();
            bugreportHash = this.mOwners.getDeviceOwnerRemoteBugreportHash();
        }
        if (bugreportUriString != null) {
            shareBugreportWithDeviceOwnerIfExists(bugreportUriString, bugreportHash);
        } else if (this.mRemoteBugreportServiceIsActive.get()) {
            this.mInjector.getNotificationManager().notifyAsUser(LOG_TAG, 678432343, RemoteBugreportUtils.buildNotification(this.mContext, 2), UserHandle.ALL);
        }
    }

    /* access modifiers changed from: private */
    public void onBugreportSharingDeclined() {
        if (this.mRemoteBugreportServiceIsActive.get()) {
            this.mInjector.systemPropertiesSet("ctl.stop", "bugreportremote");
            this.mRemoteBugreportServiceIsActive.set(false);
            this.mHandler.removeCallbacks(this.mRemoteBugreportTimeoutRunnable);
            this.mContext.unregisterReceiver(this.mRemoteBugreportFinishedReceiver);
        }
        this.mRemoteBugreportSharingAccepted.set(false);
        setDeviceOwnerRemoteBugreportUriAndHash((String) null, (String) null);
        sendDeviceOwnerCommand("android.app.action.BUGREPORT_SHARING_DECLINED", (Bundle) null);
    }

    /* Debug info: failed to restart local var, previous not found, register: 11 */
    private void shareBugreportWithDeviceOwnerIfExists(String bugreportUriString, String bugreportHash) {
        ParcelFileDescriptor pfd = null;
        if (bugreportUriString != null) {
            try {
                Uri bugreportUri = Uri.parse(bugreportUriString);
                pfd = this.mContext.getContentResolver().openFileDescriptor(bugreportUri, ActivityTaskManagerService.DUMP_RECENTS_SHORT_CMD);
                synchronized (getLockObject()) {
                    Intent intent = new Intent("android.app.action.BUGREPORT_SHARE");
                    intent.setComponent(this.mOwners.getDeviceOwnerComponent());
                    intent.setDataAndType(bugreportUri, "application/vnd.android.bugreport");
                    intent.putExtra("android.app.extra.BUGREPORT_HASH", bugreportHash);
                    intent.setFlags(1);
                    ((UriGrantsManagerInternal) LocalServices.getService(UriGrantsManagerInternal.class)).grantUriPermissionFromIntent(2000, this.mOwners.getDeviceOwnerComponent().getPackageName(), intent, this.mOwners.getDeviceOwnerUserId());
                    this.mContext.sendBroadcastAsUser(intent, UserHandle.of(this.mOwners.getDeviceOwnerUserId()));
                }
                if (pfd != null) {
                    try {
                        pfd.close();
                    } catch (IOException e) {
                    }
                }
            } catch (FileNotFoundException e2) {
                try {
                    Bundle extras = new Bundle();
                    extras.putInt("android.app.extra.BUGREPORT_FAILURE_REASON", 1);
                    sendDeviceOwnerCommand("android.app.action.BUGREPORT_FAILED", extras);
                    if (pfd != null) {
                        try {
                            pfd.close();
                        } catch (IOException e3) {
                        }
                    }
                } catch (Throwable th) {
                    if (pfd != null) {
                        try {
                            pfd.close();
                        } catch (IOException e4) {
                        }
                    }
                    this.mRemoteBugreportSharingAccepted.set(false);
                    setDeviceOwnerRemoteBugreportUriAndHash((String) null, (String) null);
                    throw th;
                }
            }
            this.mRemoteBugreportSharingAccepted.set(false);
            setDeviceOwnerRemoteBugreportUriAndHash((String) null, (String) null);
            return;
        }
        throw new FileNotFoundException();
    }

    public void setCameraDisabled(ComponentName who, boolean disabled) {
        if (this.mHasFeature) {
            Preconditions.checkNotNull(who, "ComponentName is null");
            int userHandle = this.mInjector.userHandleGetCallingUserId();
            synchronized (getLockObject()) {
                ActiveAdmin ap = getActiveAdminForCallerLocked(who, 8);
                if (ap.disableCamera != disabled) {
                    ap.disableCamera = disabled;
                    saveSettingsLocked(userHandle);
                }
            }
            pushUserRestrictions(userHandle);
            DevicePolicyEventLogger.createEvent(30).setAdmin(who).setBoolean(disabled).write();
        }
    }

    public boolean getCameraDisabled(ComponentName who, int userHandle) {
        return getCameraDisabled(who, userHandle, true);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0020, code lost:
        return r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean getCameraDisabled(android.content.ComponentName r9, int r10, boolean r11) {
        /*
            r8 = this;
            android.content.Context r0 = r8.mContext
            boolean r0 = com.miui.enterprise.RestrictionsHelper.isCameraRestricted(r0)
            r1 = 1
            if (r0 == 0) goto L_0x000a
            return r1
        L_0x000a:
            boolean r0 = r8.mHasFeature
            r2 = 0
            if (r0 != 0) goto L_0x0010
            return r2
        L_0x0010:
            java.lang.Object r0 = r8.getLockObject()
            monitor-enter(r0)
            if (r9 == 0) goto L_0x0023
            com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin r1 = r8.getActiveAdminUncheckedLocked(r9, r10)     // Catch:{ all -> 0x0021 }
            if (r1 == 0) goto L_0x001f
            boolean r2 = r1.disableCamera     // Catch:{ all -> 0x0021 }
        L_0x001f:
            monitor-exit(r0)     // Catch:{ all -> 0x0021 }
            return r2
        L_0x0021:
            r1 = move-exception
            goto L_0x0051
        L_0x0023:
            if (r11 == 0) goto L_0x0031
            com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin r3 = r8.getDeviceOwnerAdminLocked()     // Catch:{ all -> 0x0021 }
            if (r3 == 0) goto L_0x0031
            boolean r4 = r3.disableCamera     // Catch:{ all -> 0x0021 }
            if (r4 == 0) goto L_0x0031
            monitor-exit(r0)     // Catch:{ all -> 0x0021 }
            return r1
        L_0x0031:
            com.android.server.devicepolicy.DevicePolicyManagerService$DevicePolicyData r3 = r8.getUserData(r10)     // Catch:{ all -> 0x0021 }
            java.util.ArrayList<com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin> r4 = r3.mAdminList     // Catch:{ all -> 0x0021 }
            int r4 = r4.size()     // Catch:{ all -> 0x0021 }
            r5 = 0
        L_0x003c:
            if (r5 >= r4) goto L_0x004f
            java.util.ArrayList<com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin> r6 = r3.mAdminList     // Catch:{ all -> 0x0021 }
            java.lang.Object r6 = r6.get(r5)     // Catch:{ all -> 0x0021 }
            com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin r6 = (com.android.server.devicepolicy.DevicePolicyManagerService.ActiveAdmin) r6     // Catch:{ all -> 0x0021 }
            boolean r7 = r6.disableCamera     // Catch:{ all -> 0x0021 }
            if (r7 == 0) goto L_0x004c
            monitor-exit(r0)     // Catch:{ all -> 0x0021 }
            return r1
        L_0x004c:
            int r5 = r5 + 1
            goto L_0x003c
        L_0x004f:
            monitor-exit(r0)     // Catch:{ all -> 0x0021 }
            return r2
        L_0x0051:
            monitor-exit(r0)     // Catch:{ all -> 0x0021 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.DevicePolicyManagerService.getCameraDisabled(android.content.ComponentName, int, boolean):boolean");
    }

    public void setKeyguardDisabledFeatures(ComponentName who, int which, boolean parent) {
        if (this.mHasFeature) {
            Preconditions.checkNotNull(who, "ComponentName is null");
            int userHandle = this.mInjector.userHandleGetCallingUserId();
            if (isManagedProfile(userHandle)) {
                if (parent) {
                    which &= 432;
                } else {
                    which &= PROFILE_KEYGUARD_FEATURES;
                }
            }
            synchronized (getLockObject()) {
                ActiveAdmin ap = getActiveAdminForCallerLocked(who, 9, parent);
                if (ap.disabledKeyguardFeatures != which) {
                    ap.disabledKeyguardFeatures = which;
                    saveSettingsLocked(userHandle);
                }
            }
            if (SecurityLog.isLoggingEnabled()) {
                SecurityLog.writeEvent(210021, new Object[]{who.getPackageName(), Integer.valueOf(userHandle), Integer.valueOf(parent ? getProfileParentId(userHandle) : userHandle), Integer.valueOf(which)});
            }
            DevicePolicyEventLogger.createEvent(9).setAdmin(who).setInt(which).setBoolean(parent).write();
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 12 */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x001f, code lost:
        r12.mInjector.binderRestoreCallingIdentity(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0024, code lost:
        return r1;
     */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0042 A[Catch:{ all -> 0x0075 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int getKeyguardDisabledFeatures(android.content.ComponentName r13, int r14, boolean r15) {
        /*
            r12 = this;
            boolean r0 = r12.mHasFeature
            r1 = 0
            if (r0 != 0) goto L_0x0006
            return r1
        L_0x0006:
            r12.enforceFullCrossUsersPermission(r14)
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r0 = r12.mInjector
            long r2 = r0.binderClearCallingIdentity()
            java.lang.Object r0 = r12.getLockObject()     // Catch:{ all -> 0x0075 }
            monitor-enter(r0)     // Catch:{ all -> 0x0075 }
            if (r13 == 0) goto L_0x0027
            com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin r4 = r12.getActiveAdminUncheckedLocked(r13, r14, r15)     // Catch:{ all -> 0x0025 }
            if (r4 == 0) goto L_0x001e
            int r1 = r4.disabledKeyguardFeatures     // Catch:{ all -> 0x0025 }
        L_0x001e:
            monitor-exit(r0)     // Catch:{ all -> 0x0025 }
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r0 = r12.mInjector
            r0.binderRestoreCallingIdentity(r2)
            return r1
        L_0x0025:
            r1 = move-exception
            goto L_0x0073
        L_0x0027:
            if (r15 != 0) goto L_0x0036
            boolean r4 = r12.isManagedProfile((int) r14)     // Catch:{ all -> 0x0025 }
            if (r4 == 0) goto L_0x0036
            com.android.server.devicepolicy.DevicePolicyManagerService$DevicePolicyData r4 = r12.getUserDataUnchecked(r14)     // Catch:{ all -> 0x0025 }
            java.util.ArrayList<com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin> r4 = r4.mAdminList     // Catch:{ all -> 0x0025 }
            goto L_0x003a
        L_0x0036:
            java.util.List r4 = r12.getActiveAdminsForLockscreenPoliciesLocked(r14, r15)     // Catch:{ all -> 0x0025 }
        L_0x003a:
            r5 = 0
            int r6 = r4.size()     // Catch:{ all -> 0x0025 }
            r7 = 0
        L_0x0040:
            if (r7 >= r6) goto L_0x006c
            java.lang.Object r8 = r4.get(r7)     // Catch:{ all -> 0x0025 }
            com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin r8 = (com.android.server.devicepolicy.DevicePolicyManagerService.ActiveAdmin) r8     // Catch:{ all -> 0x0025 }
            android.os.UserHandle r9 = r8.getUserHandle()     // Catch:{ all -> 0x0025 }
            int r9 = r9.getIdentifier()     // Catch:{ all -> 0x0025 }
            if (r15 != 0) goto L_0x0056
            if (r9 != r14) goto L_0x0056
            r10 = 1
            goto L_0x0057
        L_0x0056:
            r10 = r1
        L_0x0057:
            if (r10 != 0) goto L_0x0066
            boolean r11 = r12.isManagedProfile((int) r9)     // Catch:{ all -> 0x0025 }
            if (r11 != 0) goto L_0x0060
            goto L_0x0066
        L_0x0060:
            int r11 = r8.disabledKeyguardFeatures     // Catch:{ all -> 0x0025 }
            r11 = r11 & 432(0x1b0, float:6.05E-43)
            r5 = r5 | r11
            goto L_0x0069
        L_0x0066:
            int r11 = r8.disabledKeyguardFeatures     // Catch:{ all -> 0x0025 }
            r5 = r5 | r11
        L_0x0069:
            int r7 = r7 + 1
            goto L_0x0040
        L_0x006c:
            monitor-exit(r0)     // Catch:{ all -> 0x0025 }
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r0 = r12.mInjector
            r0.binderRestoreCallingIdentity(r2)
            return r5
        L_0x0073:
            monitor-exit(r0)     // Catch:{ all -> 0x0025 }
            throw r1     // Catch:{ all -> 0x0075 }
        L_0x0075:
            r0 = move-exception
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r1 = r12.mInjector
            r1.binderRestoreCallingIdentity(r2)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.DevicePolicyManagerService.getKeyguardDisabledFeatures(android.content.ComponentName, int, boolean):int");
    }

    public void setKeepUninstalledPackages(ComponentName who, String callerPackage, List<String> packageList) {
        if (this.mHasFeature) {
            Preconditions.checkNotNull(packageList, "packageList is null");
            int userHandle = UserHandle.getCallingUserId();
            synchronized (getLockObject()) {
                enforceCanManageScope(who, callerPackage, -2, "delegation-keep-uninstalled-packages");
                getDeviceOwnerAdminLocked().keepUninstalledPackages = packageList;
                saveSettingsLocked(userHandle);
                this.mInjector.getPackageManagerInternal().setKeepUninstalledPackages(packageList);
            }
            DevicePolicyEventLogger.createEvent(61).setAdmin(callerPackage).setBoolean(who == null).setStrings((String[]) packageList.toArray(new String[0])).write();
        }
    }

    public List<String> getKeepUninstalledPackages(ComponentName who, String callerPackage) {
        List<String> keepUninstalledPackagesLocked;
        if (!this.mHasFeature) {
            return null;
        }
        synchronized (getLockObject()) {
            enforceCanManageScope(who, callerPackage, -2, "delegation-keep-uninstalled-packages");
            keepUninstalledPackagesLocked = getKeepUninstalledPackagesLocked();
        }
        return keepUninstalledPackagesLocked;
    }

    private List<String> getKeepUninstalledPackagesLocked() {
        ActiveAdmin deviceOwner = getDeviceOwnerAdminLocked();
        if (deviceOwner != null) {
            return deviceOwner.keepUninstalledPackages;
        }
        return null;
    }

    /* Debug info: failed to restart local var, previous not found, register: 10 */
    /* JADX INFO: finally extract failed */
    public boolean setDeviceOwner(ComponentName admin, String ownerName, int userId) {
        if (!this.mHasFeature) {
            return false;
        }
        if (admin == null || !isPackageInstalledForUser(admin.getPackageName(), userId)) {
            throw new IllegalArgumentException("Invalid component " + admin + " for device owner");
        }
        boolean hasIncompatibleAccountsOrNonAdb = hasIncompatibleAccountsOrNonAdbNoLock(userId, admin);
        synchronized (getLockObject()) {
            enforceCanSetDeviceOwnerLocked(admin, userId, hasIncompatibleAccountsOrNonAdb);
            ActiveAdmin activeAdmin = getActiveAdminUncheckedLocked(admin, userId);
            if (activeAdmin == null || getUserData(userId).mRemovingAdmins.contains(admin)) {
                throw new IllegalArgumentException("Not active admin: " + admin);
            }
            toggleBackupServiceActive(0, false);
            if (isAdb()) {
                MetricsLogger.action(this.mContext, 617, LOG_TAG_DEVICE_OWNER);
                DevicePolicyEventLogger.createEvent(82).setAdmin(admin).setStrings(new String[]{LOG_TAG_DEVICE_OWNER}).write();
            }
            this.mOwners.setDeviceOwner(admin, ownerName, userId);
            this.mOwners.writeDeviceOwner();
            updateDeviceOwnerLocked();
            setDeviceOwnerSystemPropertyLocked();
            Set<String> restrictions = UserRestrictionsUtils.getDefaultEnabledForDeviceOwner();
            if (!restrictions.isEmpty()) {
                for (String restriction : restrictions) {
                    activeAdmin.ensureUserRestrictions().putBoolean(restriction, true);
                }
                activeAdmin.defaultEnabledRestrictionsAlreadySet.addAll(restrictions);
                Slog.i(LOG_TAG, "Enabled the following restrictions by default: " + restrictions);
                saveUserRestrictionsLocked(userId);
            }
            long ident = this.mInjector.binderClearCallingIdentity();
            try {
                sendOwnerChangedBroadcast("android.app.action.DEVICE_OWNER_CHANGED", userId);
                this.mInjector.binderRestoreCallingIdentity(ident);
                this.mDeviceAdminServiceController.startServiceForOwner(admin.getPackageName(), userId, "set-device-owner");
                Slog.i(LOG_TAG, "Device owner set: " + admin + " on user " + userId);
            } catch (Throwable th) {
                this.mInjector.binderRestoreCallingIdentity(ident);
                throw th;
            }
        }
        return true;
    }

    public boolean hasDeviceOwner() {
        enforceDeviceOwnerOrManageUsers();
        return this.mOwners.hasDeviceOwner();
    }

    /* access modifiers changed from: package-private */
    public boolean isDeviceOwner(ActiveAdmin admin) {
        return isDeviceOwner(admin.info.getComponent(), admin.getUserHandle().getIdentifier());
    }

    public boolean isDeviceOwner(ComponentName who, int userId) {
        boolean z;
        synchronized (getLockObject()) {
            z = this.mOwners.hasDeviceOwner() && this.mOwners.getDeviceOwnerUserId() == userId && this.mOwners.getDeviceOwnerComponent().equals(who);
        }
        return z;
    }

    private boolean isDeviceOwnerPackage(String packageName, int userId) {
        boolean z;
        synchronized (getLockObject()) {
            z = this.mOwners.hasDeviceOwner() && this.mOwners.getDeviceOwnerUserId() == userId && this.mOwners.getDeviceOwnerPackageName().equals(packageName);
        }
        return z;
    }

    private boolean isProfileOwnerPackage(String packageName, int userId) {
        boolean z;
        synchronized (getLockObject()) {
            z = this.mOwners.hasProfileOwner(userId) && this.mOwners.getProfileOwnerPackage(userId).equals(packageName);
        }
        return z;
    }

    public boolean isProfileOwner(ComponentName who, int userId) {
        return who != null && who.equals(getProfileOwner(userId));
    }

    private boolean hasProfileOwner(int userId) {
        boolean hasProfileOwner;
        synchronized (getLockObject()) {
            hasProfileOwner = this.mOwners.hasProfileOwner(userId);
        }
        return hasProfileOwner;
    }

    private boolean canProfileOwnerAccessDeviceIds(int userId) {
        boolean canProfileOwnerAccessDeviceIds;
        synchronized (getLockObject()) {
            canProfileOwnerAccessDeviceIds = this.mOwners.canProfileOwnerAccessDeviceIds(userId);
        }
        return canProfileOwnerAccessDeviceIds;
    }

    public ComponentName getDeviceOwnerComponent(boolean callingUserOnly) {
        if (!this.mHasFeature) {
            return null;
        }
        if (!callingUserOnly) {
            enforceManageUsers();
        }
        synchronized (getLockObject()) {
            if (!this.mOwners.hasDeviceOwner()) {
                return null;
            }
            if (callingUserOnly && this.mInjector.userHandleGetCallingUserId() != this.mOwners.getDeviceOwnerUserId()) {
                return null;
            }
            ComponentName deviceOwnerComponent = this.mOwners.getDeviceOwnerComponent();
            return deviceOwnerComponent;
        }
    }

    public int getDeviceOwnerUserId() {
        boolean z = this.mHasFeature;
        int i = ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION;
        if (!z) {
            return ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION;
        }
        enforceManageUsers();
        synchronized (getLockObject()) {
            if (this.mOwners.hasDeviceOwner()) {
                i = this.mOwners.getDeviceOwnerUserId();
            }
        }
        return i;
    }

    public String getDeviceOwnerName() {
        if (!this.mHasFeature) {
            return null;
        }
        enforceManageUsers();
        synchronized (getLockObject()) {
            if (!this.mOwners.hasDeviceOwner()) {
                return null;
            }
            String applicationLabel = getApplicationLabel(this.mOwners.getDeviceOwnerPackageName(), 0);
            return applicationLabel;
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public ActiveAdmin getDeviceOwnerAdminLocked() {
        ensureLocked();
        ComponentName component = this.mOwners.getDeviceOwnerComponent();
        if (component == null) {
            return null;
        }
        DevicePolicyData policy = getUserData(this.mOwners.getDeviceOwnerUserId());
        int n = policy.mAdminList.size();
        for (int i = 0; i < n; i++) {
            ActiveAdmin admin = policy.mAdminList.get(i);
            if (component.equals(admin.info.getComponent())) {
                return admin;
            }
        }
        Slog.wtf(LOG_TAG, "Active admin for device owner not found. component=" + component);
        return null;
    }

    /* Debug info: failed to restart local var, previous not found, register: 10 */
    /* JADX INFO: finally extract failed */
    public void clearDeviceOwner(String packageName) {
        Preconditions.checkNotNull(packageName, "packageName is null");
        int callingUid = this.mInjector.binderGetCallingUid();
        try {
            if (this.mInjector.getPackageManager().getPackageUidAsUser(packageName, UserHandle.getUserId(callingUid)) == callingUid) {
                synchronized (getLockObject()) {
                    ComponentName deviceOwnerComponent = this.mOwners.getDeviceOwnerComponent();
                    int deviceOwnerUserId = this.mOwners.getDeviceOwnerUserId();
                    if (!this.mOwners.hasDeviceOwner() || !deviceOwnerComponent.getPackageName().equals(packageName) || deviceOwnerUserId != UserHandle.getUserId(callingUid)) {
                        throw new SecurityException("clearDeviceOwner can only be called by the device owner");
                    }
                    enforceUserUnlocked(deviceOwnerUserId);
                    ActiveAdmin admin = getDeviceOwnerAdminLocked();
                    long ident = this.mInjector.binderClearCallingIdentity();
                    try {
                        clearDeviceOwnerLocked(admin, deviceOwnerUserId);
                        removeActiveAdminLocked(deviceOwnerComponent, deviceOwnerUserId);
                        sendOwnerChangedBroadcast("android.app.action.DEVICE_OWNER_CHANGED", deviceOwnerUserId);
                        this.mInjector.binderRestoreCallingIdentity(ident);
                        Slog.i(LOG_TAG, "Device owner removed: " + deviceOwnerComponent);
                    } catch (Throwable th) {
                        this.mInjector.binderRestoreCallingIdentity(ident);
                        throw th;
                    }
                }
                return;
            }
            throw new SecurityException("Invalid packageName");
        } catch (PackageManager.NameNotFoundException e) {
            throw new SecurityException(e);
        }
    }

    private void clearOverrideApnUnchecked() {
        setOverrideApnsEnabledUnchecked(false);
        List<ApnSetting> apns = getOverrideApnsUnchecked();
        for (int i = 0; i < apns.size(); i++) {
            removeOverrideApnUnchecked(apns.get(i).getId());
        }
    }

    private void clearDeviceOwnerLocked(ActiveAdmin admin, int userId) {
        this.mDeviceAdminServiceController.stopServiceForOwner(userId, "clear-device-owner");
        if (admin != null) {
            admin.disableCamera = false;
            admin.userRestrictions = null;
            admin.defaultEnabledRestrictionsAlreadySet.clear();
            admin.forceEphemeralUsers = false;
            admin.isNetworkLoggingEnabled = false;
            this.mUserManagerInternal.setForceEphemeralUsers(admin.forceEphemeralUsers);
        }
        getUserData(userId).mCurrentInputMethodSet = false;
        saveSettingsLocked(userId);
        DevicePolicyData systemPolicyData = getUserData(0);
        systemPolicyData.mLastSecurityLogRetrievalTime = -1;
        systemPolicyData.mLastBugReportRequestTime = -1;
        systemPolicyData.mLastNetworkLogsRetrievalTime = -1;
        saveSettingsLocked(0);
        clearUserPoliciesLocked(userId);
        clearOverrideApnUnchecked();
        this.mOwners.clearDeviceOwner();
        this.mOwners.writeDeviceOwner();
        updateDeviceOwnerLocked();
        clearDeviceOwnerUserRestrictionLocked(UserHandle.of(userId));
        this.mInjector.securityLogSetLoggingEnabledProperty(false);
        this.mSecurityLogMonitor.stop();
        setNetworkLoggingActiveInternal(false);
        deleteTransferOwnershipBundleLocked(userId);
        try {
            if (this.mInjector.getIBackupManager() != null) {
                this.mInjector.getIBackupManager().setBackupServiceActive(0, true);
            }
        } catch (RemoteException e) {
            throw new IllegalStateException("Failed reactivating backup service.", e);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 9 */
    /* JADX INFO: finally extract failed */
    public boolean setProfileOwner(ComponentName who, String ownerName, int userHandle) {
        if (!this.mHasFeature) {
            return false;
        }
        if (who == null || !isPackageInstalledForUser(who.getPackageName(), userHandle)) {
            throw new IllegalArgumentException("Component " + who + " not installed for userId:" + userHandle);
        }
        boolean hasIncompatibleAccountsOrNonAdb = hasIncompatibleAccountsOrNonAdbNoLock(userHandle, who);
        synchronized (getLockObject()) {
            enforceCanSetProfileOwnerLocked(who, userHandle, hasIncompatibleAccountsOrNonAdb);
            ActiveAdmin admin = getActiveAdminUncheckedLocked(who, userHandle);
            if (admin == null || getUserData(userHandle).mRemovingAdmins.contains(who)) {
                throw new IllegalArgumentException("Not active admin: " + who);
            }
            if (isAdb()) {
                MetricsLogger.action(this.mContext, 617, LOG_TAG_PROFILE_OWNER);
                DevicePolicyEventLogger.createEvent(82).setAdmin(who).setStrings(new String[]{LOG_TAG_PROFILE_OWNER}).write();
            }
            toggleBackupServiceActive(userHandle, false);
            this.mOwners.setProfileOwner(who, ownerName, userHandle);
            this.mOwners.writeProfileOwner(userHandle);
            Slog.i(LOG_TAG, "Profile owner set: " + who + " on user " + userHandle);
            long id = this.mInjector.binderClearCallingIdentity();
            try {
                if (this.mUserManager.isManagedProfile(userHandle)) {
                    maybeSetDefaultRestrictionsForAdminLocked(userHandle, admin, UserRestrictionsUtils.getDefaultEnabledForManagedProfiles());
                    ensureUnknownSourcesRestrictionForProfileOwnerLocked(userHandle, admin, true);
                }
                sendOwnerChangedBroadcast("android.app.action.PROFILE_OWNER_CHANGED", userHandle);
                this.mInjector.binderRestoreCallingIdentity(id);
                this.mDeviceAdminServiceController.startServiceForOwner(who.getPackageName(), userHandle, "set-profile-owner");
            } catch (Throwable th) {
                this.mInjector.binderRestoreCallingIdentity(id);
                throw th;
            }
        }
        return true;
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    private void toggleBackupServiceActive(int userId, boolean makeActive) {
        long ident = this.mInjector.binderClearCallingIdentity();
        try {
            if (this.mInjector.getIBackupManager() != null) {
                this.mInjector.getIBackupManager().setBackupServiceActive(userId, makeActive);
            }
            this.mInjector.binderRestoreCallingIdentity(ident);
        } catch (RemoteException e) {
            throw new IllegalStateException("Failed deactivating backup service.", e);
        } catch (Throwable th) {
            this.mInjector.binderRestoreCallingIdentity(ident);
            throw th;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    /* JADX INFO: finally extract failed */
    public void clearProfileOwner(ComponentName who) {
        if (this.mHasFeature) {
            Preconditions.checkNotNull(who, "ComponentName is null");
            int userId = this.mInjector.userHandleGetCallingUserId();
            enforceNotManagedProfile(userId, "clear profile owner");
            enforceUserUnlocked(userId);
            synchronized (getLockObject()) {
                ActiveAdmin admin = getActiveAdminForCallerLocked(who, -1);
                long ident = this.mInjector.binderClearCallingIdentity();
                try {
                    clearProfileOwnerLocked(admin, userId);
                    removeActiveAdminLocked(who, userId);
                    sendOwnerChangedBroadcast("android.app.action.PROFILE_OWNER_CHANGED", userId);
                    this.mInjector.binderRestoreCallingIdentity(ident);
                    Slog.i(LOG_TAG, "Profile owner " + who + " removed from user " + userId);
                } catch (Throwable th) {
                    this.mInjector.binderRestoreCallingIdentity(ident);
                    throw th;
                }
            }
        }
    }

    public void clearProfileOwnerLocked(ActiveAdmin admin, int userId) {
        this.mDeviceAdminServiceController.stopServiceForOwner(userId, "clear-profile-owner");
        if (admin != null) {
            admin.disableCamera = false;
            admin.userRestrictions = null;
            admin.defaultEnabledRestrictionsAlreadySet.clear();
        }
        DevicePolicyData policyData = getUserData(userId);
        policyData.mCurrentInputMethodSet = false;
        policyData.mOwnerInstalledCaCerts.clear();
        saveSettingsLocked(userId);
        clearUserPoliciesLocked(userId);
        this.mOwners.removeProfileOwner(userId);
        this.mOwners.writeProfileOwner(userId);
        deleteTransferOwnershipBundleLocked(userId);
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    public void setDeviceOwnerLockScreenInfo(ComponentName who, CharSequence info) {
        Preconditions.checkNotNull(who, "ComponentName is null");
        if (this.mHasFeature) {
            synchronized (getLockObject()) {
                getActiveAdminForCallerLocked(who, -2);
                long token = this.mInjector.binderClearCallingIdentity();
                try {
                    this.mLockPatternUtils.setDeviceOwnerInfo(info != null ? info.toString() : null);
                } finally {
                    this.mInjector.binderRestoreCallingIdentity(token);
                }
            }
            DevicePolicyEventLogger.createEvent(42).setAdmin(who).write();
        }
    }

    public CharSequence getDeviceOwnerLockScreenInfo() {
        return this.mLockPatternUtils.getDeviceOwnerInfo();
    }

    private void clearUserPoliciesLocked(int userId) {
        DevicePolicyData policy = getUserData(userId);
        policy.mPermissionPolicy = 0;
        policy.mDelegationMap.clear();
        policy.mStatusBarDisabled = false;
        policy.mUserProvisioningState = 0;
        policy.mAffiliationIds.clear();
        policy.mLockTaskPackages.clear();
        updateLockTaskPackagesLocked(policy.mLockTaskPackages, userId);
        policy.mLockTaskFeatures = 0;
        saveSettingsLocked(userId);
        try {
            this.mIPackageManager.updatePermissionFlagsForAllApps(4, 0, userId);
            pushUserRestrictions(userId);
        } catch (RemoteException e) {
        }
    }

    public boolean hasUserSetupCompleted() {
        return hasUserSetupCompleted(UserHandle.getCallingUserId());
    }

    private boolean hasUserSetupCompleted(int userHandle) {
        if (!this.mHasFeature) {
            return true;
        }
        return getUserData(userHandle).mUserSetupComplete;
    }

    private boolean hasPaired(int userHandle) {
        if (!this.mHasFeature) {
            return true;
        }
        return getUserData(userHandle).mPaired;
    }

    public int getUserProvisioningState() {
        if (!this.mHasFeature) {
            return 0;
        }
        enforceManageUsers();
        return getUserProvisioningState(this.mInjector.userHandleGetCallingUserId());
    }

    private int getUserProvisioningState(int userHandle) {
        return getUserData(userHandle).mUserProvisioningState;
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    public void setUserProvisioningState(int newState, int userHandle) {
        if (this.mHasFeature) {
            if (userHandle == this.mOwners.getDeviceOwnerUserId() || this.mOwners.hasProfileOwner(userHandle) || getManagedUserId(userHandle) != -1) {
                synchronized (getLockObject()) {
                    boolean transitionCheckNeeded = true;
                    if (!isAdb()) {
                        enforceCanManageProfileAndDeviceOwners();
                    } else if (getUserProvisioningState(userHandle) == 0 && newState == 3) {
                        transitionCheckNeeded = false;
                    } else {
                        throw new IllegalStateException("Not allowed to change provisioning state unless current provisioning state is unmanaged, and new state is finalized.");
                    }
                    DevicePolicyData policyData = getUserData(userHandle);
                    if (transitionCheckNeeded) {
                        checkUserProvisioningStateTransition(policyData.mUserProvisioningState, newState);
                    }
                    policyData.mUserProvisioningState = newState;
                    saveSettingsLocked(userHandle);
                }
                return;
            }
            throw new IllegalStateException("Not allowed to change provisioning state unless a device or profile owner is set.");
        }
    }

    private void checkUserProvisioningStateTransition(int currentState, int newState) {
        if (currentState != 0) {
            if (currentState == 1 || currentState == 2) {
                if (newState == 3) {
                    return;
                }
            } else if (currentState == 4 && newState == 0) {
                return;
            }
        } else if (newState != 0) {
            return;
        }
        throw new IllegalStateException("Cannot move to user provisioning state [" + newState + "] from state [" + currentState + "]");
    }

    /* Debug info: failed to restart local var, previous not found, register: 10 */
    public void setProfileEnabled(ComponentName who) {
        if (this.mHasFeature) {
            Preconditions.checkNotNull(who, "ComponentName is null");
            synchronized (getLockObject()) {
                getActiveAdminForCallerLocked(who, -1);
                int userId = UserHandle.getCallingUserId();
                enforceManagedProfile(userId, "enable the profile");
                if (getUserInfo(userId).isEnabled()) {
                    Slog.e(LOG_TAG, "setProfileEnabled is called when the profile is already enabled");
                    return;
                }
                long id = this.mInjector.binderClearCallingIdentity();
                try {
                    this.mUserManager.setUserEnabled(userId);
                    UserInfo parent = this.mUserManager.getProfileParent(userId);
                    Intent intent = new Intent("android.intent.action.MANAGED_PROFILE_ADDED");
                    intent.putExtra("android.intent.extra.USER", new UserHandle(userId));
                    intent.addFlags(1342177280);
                    this.mContext.sendBroadcastAsUser(intent, new UserHandle(parent.id));
                } finally {
                    this.mInjector.binderRestoreCallingIdentity(id);
                }
            }
        }
    }

    public void setProfileName(ComponentName who, String profileName) {
        Preconditions.checkNotNull(who, "ComponentName is null");
        enforceProfileOrDeviceOwner(who);
        int userId = UserHandle.getCallingUserId();
        long id = this.mInjector.binderClearCallingIdentity();
        try {
            this.mUserManager.setUserName(userId, profileName);
            DevicePolicyEventLogger.createEvent(40).setAdmin(who).write();
        } finally {
            this.mInjector.binderRestoreCallingIdentity(id);
        }
    }

    public ComponentName getProfileOwnerAsUser(int userHandle) {
        enforceCrossUsersPermission(userHandle);
        return getProfileOwner(userHandle);
    }

    public ComponentName getProfileOwner(int userHandle) {
        ComponentName profileOwnerComponent;
        if (!this.mHasFeature) {
            return null;
        }
        synchronized (getLockObject()) {
            profileOwnerComponent = this.mOwners.getProfileOwnerComponent(userHandle);
        }
        return profileOwnerComponent;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public ActiveAdmin getProfileOwnerAdminLocked(int userHandle) {
        ComponentName profileOwner = this.mOwners.getProfileOwnerComponent(userHandle);
        if (profileOwner == null) {
            return null;
        }
        DevicePolicyData policy = getUserData(userHandle);
        int n = policy.mAdminList.size();
        for (int i = 0; i < n; i++) {
            ActiveAdmin admin = policy.mAdminList.get(i);
            if (profileOwner.equals(admin.info.getComponent())) {
                return admin;
            }
        }
        return null;
    }

    public String getProfileOwnerName(int userHandle) {
        if (!this.mHasFeature) {
            return null;
        }
        enforceManageUsers();
        ComponentName profileOwner = getProfileOwner(userHandle);
        if (profileOwner == null) {
            return null;
        }
        return getApplicationLabel(profileOwner.getPackageName(), userHandle);
    }

    /* Debug info: failed to restart local var, previous not found, register: 12 */
    public boolean checkDeviceIdentifierAccess(String packageName, int pid, int uid) {
        int callingUid = this.mInjector.binderGetCallingUid();
        int callingPid = this.mInjector.binderGetCallingPid();
        if (UserHandle.getAppId(callingUid) < 10000 || (callingUid == uid && callingPid == pid)) {
            int userId = UserHandle.getUserId(uid);
            try {
                ApplicationInfo appInfo = this.mIPackageManager.getApplicationInfo(packageName, 0, userId);
                if (appInfo == null) {
                    Log.w(LOG_TAG, String.format("appInfo could not be found for package %s", new Object[]{packageName}));
                    return false;
                } else if (uid != appInfo.uid) {
                    String message = String.format("Package %s (uid=%d) does not match provided uid %d", new Object[]{packageName, Integer.valueOf(appInfo.uid), Integer.valueOf(uid)});
                    Log.w(LOG_TAG, message);
                    throw new SecurityException(message);
                } else if (this.mContext.checkPermission("android.permission.READ_PHONE_STATE", pid, uid) != 0) {
                    return false;
                } else {
                    ComponentName deviceOwner = getDeviceOwnerComponent(true);
                    if (deviceOwner != null && (deviceOwner.getPackageName().equals(packageName) || isCallerDelegate(packageName, uid, "delegation-cert-install"))) {
                        return true;
                    }
                    ComponentName profileOwner = getProfileOwnerAsUser(userId);
                    if (profileOwner != null && (profileOwner.getPackageName().equals(packageName) || isCallerDelegate(packageName, uid, "delegation-cert-install"))) {
                        return true;
                    }
                    Log.w(LOG_TAG, String.format("Package %s (uid=%d, pid=%d) cannot access Device IDs", new Object[]{packageName, Integer.valueOf(uid), Integer.valueOf(pid)}));
                    return false;
                }
            } catch (RemoteException e) {
                Log.e(LOG_TAG, "Exception caught obtaining appInfo for package " + packageName, e);
                return false;
            }
        } else {
            String message2 = String.format("Calling uid %d, pid %d cannot check device identifier access for package %s (uid=%d, pid=%d)", new Object[]{Integer.valueOf(callingUid), Integer.valueOf(callingPid), packageName, Integer.valueOf(uid), Integer.valueOf(pid)});
            Log.w(LOG_TAG, message2);
            throw new SecurityException(message2);
        }
    }

    private String getApplicationLabel(String packageName, int userHandle) {
        long token = this.mInjector.binderClearCallingIdentity();
        String str = null;
        try {
            Context userContext = this.mContext.createPackageContextAsUser(packageName, 0, new UserHandle(userHandle));
            ApplicationInfo appInfo = userContext.getApplicationInfo();
            CharSequence result = null;
            if (appInfo != null) {
                result = appInfo.loadUnsafeLabel(userContext.getPackageManager());
            }
            if (result != null) {
                str = result.toString();
            }
            return str;
        } catch (PackageManager.NameNotFoundException nnfe) {
            Log.w(LOG_TAG, packageName + " is not installed for user " + userHandle, nnfe);
            return null;
        } finally {
            this.mInjector.binderRestoreCallingIdentity(token);
        }
    }

    private void wtfIfInLock() {
        if (Thread.holdsLock(this)) {
            Slog.wtfStack(LOG_TAG, "Shouldn't be called with DPMS lock held");
        }
    }

    private void enforceCanSetProfileOwnerLocked(ComponentName owner, int userHandle, boolean hasIncompatibleAccountsOrNonAdb) {
        UserInfo info = getUserInfo(userHandle);
        if (info == null) {
            throw new IllegalArgumentException("Attempted to set profile owner for invalid userId: " + userHandle);
        } else if (info.isGuest()) {
            throw new IllegalStateException("Cannot set a profile owner on a guest");
        } else if (this.mOwners.hasProfileOwner(userHandle)) {
            throw new IllegalStateException("Trying to set the profile owner, but profile owner is already set.");
        } else if (this.mOwners.hasDeviceOwner() && this.mOwners.getDeviceOwnerUserId() == userHandle) {
            throw new IllegalStateException("Trying to set the profile owner, but the user already has a device owner.");
        } else if (!isAdb()) {
            enforceCanManageProfileAndDeviceOwners();
            if (!this.mIsWatch && !hasUserSetupCompleted(userHandle)) {
                return;
            }
            if (!isCallerWithSystemUid()) {
                throw new IllegalStateException("Cannot set the profile owner on a user which is already set-up");
            } else if (!this.mIsWatch) {
                String supervisor = this.mContext.getResources().getString(17039733);
                if (supervisor == null) {
                    throw new IllegalStateException("Unable to set profile owner post-setup, nodefault supervisor profile owner defined");
                } else if (!owner.equals(ComponentName.unflattenFromString(supervisor))) {
                    throw new IllegalStateException("Unable to set non-default profile owner post-setup " + owner);
                }
            }
        } else if ((this.mIsWatch || hasUserSetupCompleted(userHandle)) && hasIncompatibleAccountsOrNonAdb) {
            throw new IllegalStateException("Not allowed to set the profile owner because there are already some accounts on the profile");
        }
    }

    private void enforceCanSetDeviceOwnerLocked(ComponentName owner, int userId, boolean hasIncompatibleAccountsOrNonAdb) {
        if (!isAdb()) {
            enforceCanManageProfileAndDeviceOwners();
        }
        int code = checkDeviceOwnerProvisioningPreConditionLocked(owner, userId, isAdb(), hasIncompatibleAccountsOrNonAdb);
        switch (code) {
            case 0:
                return;
            case 1:
                throw new IllegalStateException("Trying to set the device owner, but device owner is already set.");
            case 2:
                throw new IllegalStateException("Trying to set the device owner, but the user already has a profile owner.");
            case 3:
                throw new IllegalStateException("User not running: " + userId);
            case 4:
                throw new IllegalStateException("Cannot set the device owner if the device is already set-up");
            case 5:
                throw new IllegalStateException("Not allowed to set the device owner because there are already several users on the device");
            case 6:
                throw new IllegalStateException("Not allowed to set the device owner because there are already some accounts on the device");
            case 7:
                throw new IllegalStateException("User is not system user");
            case 8:
                throw new IllegalStateException("Not allowed to set the device owner because this device has already paired");
            default:
                throw new IllegalStateException("Unexpected @ProvisioningPreCondition " + code);
        }
    }

    private void enforceUserUnlocked(int userId) {
        Preconditions.checkState(this.mUserManager.isUserUnlocked(userId), "User must be running and unlocked");
    }

    private void enforceUserUnlocked(int userId, boolean parent) {
        if (parent) {
            enforceUserUnlocked(getProfileParentId(userId));
        } else {
            enforceUserUnlocked(userId);
        }
    }

    private void enforceManageUsers() {
        int callingUid = this.mInjector.binderGetCallingUid();
        if (!isCallerWithSystemUid() && callingUid != 0) {
            this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USERS", (String) null);
        }
    }

    private void enforceFullCrossUsersPermission(int userHandle) {
        enforceSystemUserOrPermissionIfCrossUser(userHandle, "android.permission.INTERACT_ACROSS_USERS_FULL");
    }

    private void enforceCrossUsersPermission(int userHandle) {
        enforceSystemUserOrPermissionIfCrossUser(userHandle, "android.permission.INTERACT_ACROSS_USERS");
    }

    private void enforceSystemUserOrPermission(String permission) {
        if (!isCallerWithSystemUid() && this.mInjector.binderGetCallingUid() != 0) {
            Context context = this.mContext;
            context.enforceCallingOrSelfPermission(permission, "Must be system or have " + permission + " permission");
        }
    }

    private void enforceSystemUserOrPermissionIfCrossUser(int userHandle, String permission) {
        if (userHandle < 0) {
            throw new IllegalArgumentException("Invalid userId " + userHandle);
        } else if (userHandle != this.mInjector.userHandleGetCallingUserId()) {
            enforceSystemUserOrPermission(permission);
        }
    }

    private void enforceManagedProfile(int userHandle, String message) {
        if (!isManagedProfile(userHandle)) {
            throw new SecurityException("You can not " + message + " outside a managed profile.");
        }
    }

    private void enforceNotManagedProfile(int userHandle, String message) {
        if (isManagedProfile(userHandle)) {
            throw new SecurityException("You can not " + message + " for a managed profile.");
        }
    }

    private void enforceDeviceOwnerOrManageUsers() {
        synchronized (getLockObject()) {
            if (getActiveAdminWithPolicyForUidLocked((ComponentName) null, -2, this.mInjector.binderGetCallingUid()) == null) {
                enforceManageUsers();
            }
        }
    }

    private void enforceProfileOwnerOrSystemUser() {
        synchronized (getLockObject()) {
            if (getActiveAdminWithPolicyForUidLocked((ComponentName) null, -1, this.mInjector.binderGetCallingUid()) == null) {
                Preconditions.checkState(isCallerWithSystemUid(), "Only profile owner, device owner and system may call this method.");
            }
        }
    }

    private void enforceProfileOwnerOrFullCrossUsersPermission(int userId) {
        if (userId == this.mInjector.userHandleGetCallingUserId()) {
            synchronized (getLockObject()) {
                if (getActiveAdminWithPolicyForUidLocked((ComponentName) null, -1, this.mInjector.binderGetCallingUid()) != null) {
                    return;
                }
            }
        }
        enforceSystemUserOrPermission("android.permission.INTERACT_ACROSS_USERS_FULL");
    }

    private boolean canUserUseLockTaskLocked(int userId) {
        if (isUserAffiliatedWithDeviceLocked(userId)) {
            return true;
        }
        if (!this.mOwners.hasDeviceOwner() && getProfileOwner(userId) != null && !isManagedProfile(userId)) {
            return true;
        }
        return false;
    }

    private void enforceCanCallLockTaskLocked(ComponentName who) {
        getActiveAdminForCallerLocked(who, -1);
        int userId = this.mInjector.userHandleGetCallingUserId();
        if (!canUserUseLockTaskLocked(userId)) {
            throw new SecurityException("User " + userId + " is not allowed to use lock task");
        }
    }

    private void ensureCallerPackage(String packageName) {
        if (packageName == null) {
            Preconditions.checkState(isCallerWithSystemUid(), "Only caller can omit package name");
            return;
        }
        try {
            boolean z = false;
            if (this.mIPackageManager.getApplicationInfo(packageName, 0, this.mInjector.userHandleGetCallingUserId()).uid == this.mInjector.binderGetCallingUid()) {
                z = true;
            }
            Preconditions.checkState(z, "Unmatching package name");
        } catch (RemoteException e) {
        }
    }

    private boolean isCallerWithSystemUid() {
        return UserHandle.isSameApp(this.mInjector.binderGetCallingUid(), 1000);
    }

    /* access modifiers changed from: protected */
    public int getProfileParentId(int userHandle) {
        long ident = this.mInjector.binderClearCallingIdentity();
        try {
            UserInfo parentUser = this.mUserManager.getProfileParent(userHandle);
            return parentUser != null ? parentUser.id : userHandle;
        } finally {
            this.mInjector.binderRestoreCallingIdentity(ident);
        }
    }

    private int getCredentialOwner(int userHandle, boolean parent) {
        long ident = this.mInjector.binderClearCallingIdentity();
        if (parent) {
            try {
                UserInfo parentProfile = this.mUserManager.getProfileParent(userHandle);
                if (parentProfile != null) {
                    userHandle = parentProfile.id;
                }
            } catch (Throwable th) {
                this.mInjector.binderRestoreCallingIdentity(ident);
                throw th;
            }
        }
        int credentialOwnerProfile = this.mUserManager.getCredentialOwnerProfile(userHandle);
        this.mInjector.binderRestoreCallingIdentity(ident);
        return credentialOwnerProfile;
    }

    private boolean isManagedProfile(int userHandle) {
        UserInfo user = getUserInfo(userHandle);
        return user != null && user.isManagedProfile();
    }

    private void enableIfNecessary(String packageName, int userId) {
        try {
            if (this.mIPackageManager.getApplicationInfo(packageName, 32768, userId).enabledSetting == 4) {
                this.mIPackageManager.setApplicationEnabledSetting(packageName, 0, 1, userId, LOG_TAG);
            }
        } catch (RemoteException e) {
        }
    }

    /* access modifiers changed from: protected */
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (DumpUtils.checkDumpPermission(this.mContext, LOG_TAG, pw)) {
            synchronized (getLockObject()) {
                pw.println("Current Device Policy Manager state:");
                this.mOwners.dump("  ", pw);
                this.mDeviceAdminServiceController.dump("  ", pw);
                int userCount = this.mUserData.size();
                for (int u = 0; u < userCount; u++) {
                    DevicePolicyData policy = getUserData(this.mUserData.keyAt(u));
                    pw.println();
                    pw.println("  Enabled Device Admins (User " + policy.mUserHandle + ", provisioningState: " + policy.mUserProvisioningState + "):");
                    int N = policy.mAdminList.size();
                    for (int i = 0; i < N; i++) {
                        ActiveAdmin ap = policy.mAdminList.get(i);
                        if (ap != null) {
                            pw.print("    ");
                            pw.print(ap.info.getComponent().flattenToShortString());
                            pw.println(":");
                            ap.dump("      ", pw);
                        }
                    }
                    if (!policy.mRemovingAdmins.isEmpty()) {
                        pw.println("    Removing Device Admins (User " + policy.mUserHandle + "): " + policy.mRemovingAdmins);
                    }
                    pw.println(" ");
                    pw.print("    mPasswordOwner=");
                    pw.println(policy.mPasswordOwner);
                }
                pw.println();
                this.mConstants.dump("  ", pw);
                pw.println();
                this.mStatLogger.dump(pw, "  ");
                pw.println();
                pw.println("  Encryption Status: " + getEncryptionStatusName(getEncryptionStatus()));
                pw.println();
                this.mPolicyCache.dump("  ", pw);
            }
        }
    }

    private String getEncryptionStatusName(int encryptionStatus) {
        if (encryptionStatus == 0) {
            return "unsupported";
        }
        if (encryptionStatus == 1) {
            return "inactive";
        }
        if (encryptionStatus == 2) {
            return "activating";
        }
        if (encryptionStatus == 3) {
            return "block";
        }
        if (encryptionStatus == 4) {
            return "block default key";
        }
        if (encryptionStatus != 5) {
            return UiModeManagerService.Shell.NIGHT_MODE_STR_UNKNOWN;
        }
        return "per-user";
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    public void addPersistentPreferredActivity(ComponentName who, IntentFilter filter, ComponentName activity) {
        Injector injector;
        Preconditions.checkNotNull(who, "ComponentName is null");
        int userHandle = UserHandle.getCallingUserId();
        synchronized (getLockObject()) {
            getActiveAdminForCallerLocked(who, -1);
            long id = this.mInjector.binderClearCallingIdentity();
            try {
                this.mIPackageManager.addPersistentPreferredActivity(filter, activity, userHandle);
                this.mIPackageManager.flushPackageRestrictionsAsUser(userHandle);
                injector = this.mInjector;
            } catch (RemoteException e) {
                injector = this.mInjector;
            } catch (Throwable th) {
                this.mInjector.binderRestoreCallingIdentity(id);
                throw th;
            }
            injector.binderRestoreCallingIdentity(id);
        }
        DevicePolicyEventLogger.createEvent(52).setAdmin(who).setStrings(activity != null ? activity.getPackageName() : null, getIntentFilterActions(filter)).write();
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    public void clearPackagePersistentPreferredActivities(ComponentName who, String packageName) {
        Injector injector;
        Preconditions.checkNotNull(who, "ComponentName is null");
        int userHandle = UserHandle.getCallingUserId();
        synchronized (getLockObject()) {
            getActiveAdminForCallerLocked(who, -1);
            long id = this.mInjector.binderClearCallingIdentity();
            try {
                this.mIPackageManager.clearPackagePersistentPreferredActivities(packageName, userHandle);
                this.mIPackageManager.flushPackageRestrictionsAsUser(userHandle);
                injector = this.mInjector;
            } catch (RemoteException e) {
                injector = this.mInjector;
            } catch (Throwable th) {
                this.mInjector.binderRestoreCallingIdentity(id);
                throw th;
            }
            injector.binderRestoreCallingIdentity(id);
        }
    }

    public void setDefaultSmsApplication(ComponentName admin, String packageName) {
        Preconditions.checkNotNull(admin, "ComponentName is null");
        enforceDeviceOwner(admin);
        this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable(packageName) {
            private final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void runOrThrow() {
                DevicePolicyManagerService.this.lambda$setDefaultSmsApplication$9$DevicePolicyManagerService(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$setDefaultSmsApplication$9$DevicePolicyManagerService(String packageName) throws Exception {
        SmsApplication.setDefaultApplication(packageName, this.mContext);
    }

    public boolean setApplicationRestrictionsManagingPackage(ComponentName admin, String packageName) {
        try {
            setDelegatedScopePreO(admin, packageName, "delegation-app-restrictions");
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public String getApplicationRestrictionsManagingPackage(ComponentName admin) {
        List<String> delegatePackages = getDelegatePackages(admin, "delegation-app-restrictions");
        if (delegatePackages.size() > 0) {
            return delegatePackages.get(0);
        }
        return null;
    }

    public boolean isCallerApplicationRestrictionsManagingPackage(String callerPackage) {
        return isCallerDelegate(callerPackage, this.mInjector.binderGetCallingUid(), "delegation-app-restrictions");
    }

    public void setApplicationRestrictions(ComponentName who, String callerPackage, String packageName, Bundle settings) {
        enforceCanManageScope(who, callerPackage, -1, "delegation-app-restrictions");
        UserHandle userHandle = this.mInjector.binderGetCallingUserHandle();
        long id = this.mInjector.binderClearCallingIdentity();
        try {
            this.mUserManager.setApplicationRestrictions(packageName, settings, userHandle);
            DevicePolicyEventLogger.createEvent(62).setAdmin(callerPackage).setBoolean(who == null).setStrings(new String[]{packageName}).write();
        } finally {
            this.mInjector.binderRestoreCallingIdentity(id);
        }
    }

    public void setTrustAgentConfiguration(ComponentName admin, ComponentName agent, PersistableBundle args, boolean parent) {
        if (this.mHasFeature && this.mLockPatternUtils.hasSecureLockScreen()) {
            Preconditions.checkNotNull(admin, "admin is null");
            Preconditions.checkNotNull(agent, "agent is null");
            int userHandle = UserHandle.getCallingUserId();
            synchronized (getLockObject()) {
                getActiveAdminForCallerLocked(admin, 9, parent).trustAgentInfos.put(agent.flattenToString(), new ActiveAdmin.TrustAgentInfo(args));
                saveSettingsLocked(userHandle);
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0052, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x00d0, code lost:
        return r16;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<android.os.PersistableBundle> getTrustAgentConfiguration(android.content.ComponentName r19, android.content.ComponentName r20, int r21, boolean r22) {
        /*
            r18 = this;
            r1 = r18
            r2 = r19
            r3 = r21
            r4 = r22
            boolean r0 = r1.mHasFeature
            r5 = 0
            if (r0 == 0) goto L_0x00d4
            com.android.internal.widget.LockPatternUtils r0 = r1.mLockPatternUtils
            boolean r0 = r0.hasSecureLockScreen()
            if (r0 != 0) goto L_0x0019
            r6 = r20
            goto L_0x00d6
        L_0x0019:
            java.lang.String r0 = "agent null"
            r6 = r20
            com.android.internal.util.Preconditions.checkNotNull(r6, r0)
            r1.enforceFullCrossUsersPermission(r3)
            java.lang.Object r7 = r18.getLockObject()
            monitor-enter(r7)
            java.lang.String r0 = r20.flattenToString()     // Catch:{ all -> 0x00d1 }
            if (r2 == 0) goto L_0x0053
            com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin r8 = r1.getActiveAdminUncheckedLocked(r2, r3, r4)     // Catch:{ all -> 0x00d1 }
            if (r8 != 0) goto L_0x0036
            monitor-exit(r7)     // Catch:{ all -> 0x00d1 }
            return r5
        L_0x0036:
            android.util.ArrayMap<java.lang.String, com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin$TrustAgentInfo> r9 = r8.trustAgentInfos     // Catch:{ all -> 0x00d1 }
            java.lang.Object r9 = r9.get(r0)     // Catch:{ all -> 0x00d1 }
            com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin$TrustAgentInfo r9 = (com.android.server.devicepolicy.DevicePolicyManagerService.ActiveAdmin.TrustAgentInfo) r9     // Catch:{ all -> 0x00d1 }
            if (r9 == 0) goto L_0x0051
            android.os.PersistableBundle r10 = r9.options     // Catch:{ all -> 0x00d1 }
            if (r10 != 0) goto L_0x0045
            goto L_0x0051
        L_0x0045:
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ all -> 0x00d1 }
            r5.<init>()     // Catch:{ all -> 0x00d1 }
            android.os.PersistableBundle r10 = r9.options     // Catch:{ all -> 0x00d1 }
            r5.add(r10)     // Catch:{ all -> 0x00d1 }
            monitor-exit(r7)     // Catch:{ all -> 0x00d1 }
            return r5
        L_0x0051:
            monitor-exit(r7)     // Catch:{ all -> 0x00d1 }
            return r5
        L_0x0053:
            r8 = 0
            java.util.List r9 = r1.getActiveAdminsForLockscreenPoliciesLocked(r3, r4)     // Catch:{ all -> 0x00d1 }
            r10 = 1
            int r11 = r9.size()     // Catch:{ all -> 0x00d1 }
            r12 = 0
        L_0x005f:
            if (r12 >= r11) goto L_0x00c6
            java.lang.Object r13 = r9.get(r12)     // Catch:{ all -> 0x00d1 }
            com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin r13 = (com.android.server.devicepolicy.DevicePolicyManagerService.ActiveAdmin) r13     // Catch:{ all -> 0x00d1 }
            int r14 = r13.disabledKeyguardFeatures     // Catch:{ all -> 0x00d1 }
            r14 = r14 & 16
            if (r14 == 0) goto L_0x006f
            r14 = 1
            goto L_0x0070
        L_0x006f:
            r14 = 0
        L_0x0070:
            android.util.ArrayMap<java.lang.String, com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin$TrustAgentInfo> r15 = r13.trustAgentInfos     // Catch:{ all -> 0x00d1 }
            java.lang.Object r15 = r15.get(r0)     // Catch:{ all -> 0x00d1 }
            com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin$TrustAgentInfo r15 = (com.android.server.devicepolicy.DevicePolicyManagerService.ActiveAdmin.TrustAgentInfo) r15     // Catch:{ all -> 0x00d1 }
            if (r15 == 0) goto L_0x00b8
            android.os.PersistableBundle r5 = r15.options     // Catch:{ all -> 0x00d1 }
            if (r5 == 0) goto L_0x00b8
            android.os.PersistableBundle r5 = r15.options     // Catch:{ all -> 0x00d1 }
            boolean r5 = r5.isEmpty()     // Catch:{ all -> 0x00d1 }
            if (r5 != 0) goto L_0x00b8
            if (r14 == 0) goto L_0x0098
            if (r8 != 0) goto L_0x0090
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ all -> 0x00d1 }
            r5.<init>()     // Catch:{ all -> 0x00d1 }
            r8 = r5
        L_0x0090:
            android.os.PersistableBundle r5 = r15.options     // Catch:{ all -> 0x00d1 }
            r8.add(r5)     // Catch:{ all -> 0x00d1 }
            r17 = r0
            goto L_0x00be
        L_0x0098:
            java.lang.String r5 = "DevicePolicyManager"
            r17 = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x00d1 }
            r0.<init>()     // Catch:{ all -> 0x00d1 }
            java.lang.String r1 = "Ignoring admin "
            r0.append(r1)     // Catch:{ all -> 0x00d1 }
            android.app.admin.DeviceAdminInfo r1 = r13.info     // Catch:{ all -> 0x00d1 }
            r0.append(r1)     // Catch:{ all -> 0x00d1 }
            java.lang.String r1 = " because it has trust options but doesn't declare KEYGUARD_DISABLE_TRUST_AGENTS"
            r0.append(r1)     // Catch:{ all -> 0x00d1 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x00d1 }
            android.util.Log.w(r5, r0)     // Catch:{ all -> 0x00d1 }
            goto L_0x00be
        L_0x00b8:
            r17 = r0
            if (r14 == 0) goto L_0x00be
            r10 = 0
            goto L_0x00c8
        L_0x00be:
            int r12 = r12 + 1
            r5 = 0
            r1 = r18
            r0 = r17
            goto L_0x005f
        L_0x00c6:
            r17 = r0
        L_0x00c8:
            if (r10 == 0) goto L_0x00cd
            r16 = r8
            goto L_0x00cf
        L_0x00cd:
            r16 = 0
        L_0x00cf:
            monitor-exit(r7)     // Catch:{ all -> 0x00d1 }
            return r16
        L_0x00d1:
            r0 = move-exception
            monitor-exit(r7)     // Catch:{ all -> 0x00d1 }
            throw r0
        L_0x00d4:
            r6 = r20
        L_0x00d6:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.DevicePolicyManagerService.getTrustAgentConfiguration(android.content.ComponentName, android.content.ComponentName, int, boolean):java.util.List");
    }

    public void setRestrictionsProvider(ComponentName who, ComponentName permissionProvider) {
        Preconditions.checkNotNull(who, "ComponentName is null");
        synchronized (getLockObject()) {
            getActiveAdminForCallerLocked(who, -1);
            int userHandle = UserHandle.getCallingUserId();
            getUserData(userHandle).mRestrictionsProvider = permissionProvider;
            saveSettingsLocked(userHandle);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 3 */
    public ComponentName getRestrictionsProvider(int userHandle) {
        ComponentName componentName;
        synchronized (getLockObject()) {
            if (isCallerWithSystemUid()) {
                DevicePolicyData userData = getUserData(userHandle);
                componentName = userData != null ? userData.mRestrictionsProvider : null;
            } else {
                throw new SecurityException("Only the system can query the permission provider");
            }
        }
        return componentName;
    }

    /* Debug info: failed to restart local var, previous not found, register: 11 */
    public void addCrossProfileIntentFilter(ComponentName who, IntentFilter filter, int flags) {
        Injector injector;
        Preconditions.checkNotNull(who, "ComponentName is null");
        int callingUserId = UserHandle.getCallingUserId();
        synchronized (getLockObject()) {
            getActiveAdminForCallerLocked(who, -1);
            long id = this.mInjector.binderClearCallingIdentity();
            try {
                UserInfo parent = this.mUserManager.getProfileParent(callingUserId);
                if (parent == null) {
                    Slog.e(LOG_TAG, "Cannot call addCrossProfileIntentFilter if there is no parent");
                    this.mInjector.binderRestoreCallingIdentity(id);
                    return;
                }
                if ((flags & 1) != 0) {
                    this.mIPackageManager.addCrossProfileIntentFilter(filter, who.getPackageName(), callingUserId, parent.id, 0);
                }
                if ((flags & 2) != 0) {
                    this.mIPackageManager.addCrossProfileIntentFilter(filter, who.getPackageName(), parent.id, callingUserId, 0);
                }
                injector = this.mInjector;
                injector.binderRestoreCallingIdentity(id);
                DevicePolicyEventLogger.createEvent(48).setAdmin(who).setStrings(getIntentFilterActions(filter)).setInt(flags).write();
            } catch (RemoteException e) {
                injector = this.mInjector;
            } catch (Throwable th) {
                this.mInjector.binderRestoreCallingIdentity(id);
                throw th;
            }
        }
    }

    private static String[] getIntentFilterActions(IntentFilter filter) {
        if (filter == null) {
            return null;
        }
        int actionsCount = filter.countActions();
        String[] actions = new String[actionsCount];
        for (int i = 0; i < actionsCount; i++) {
            actions[i] = filter.getAction(i);
        }
        return actions;
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    public void clearCrossProfileIntentFilters(ComponentName who) {
        UserInfo parent;
        Preconditions.checkNotNull(who, "ComponentName is null");
        int callingUserId = UserHandle.getCallingUserId();
        synchronized (getLockObject()) {
            getActiveAdminForCallerLocked(who, -1);
            long id = this.mInjector.binderClearCallingIdentity();
            try {
                UserInfo parent2 = this.mUserManager.getProfileParent(callingUserId);
                if (parent2 == null) {
                    Slog.e(LOG_TAG, "Cannot call clearCrossProfileIntentFilter if there is no parent");
                    this.mInjector.binderRestoreCallingIdentity(id);
                    return;
                }
                this.mIPackageManager.clearCrossProfileIntentFilters(callingUserId, who.getPackageName());
                this.mIPackageManager.clearCrossProfileIntentFilters(parent2.id, who.getPackageName());
                parent = this.mInjector;
                parent.binderRestoreCallingIdentity(id);
            } catch (RemoteException e) {
                parent = this.mInjector;
            } catch (Throwable th) {
                this.mInjector.binderRestoreCallingIdentity(id);
                throw th;
            }
        }
    }

    private boolean checkPackagesInPermittedListOrSystem(List<String> enabledPackages, List<String> permittedList, int userIdToCheck) {
        long id = this.mInjector.binderClearCallingIdentity();
        try {
            UserInfo user = getUserInfo(userIdToCheck);
            if (user.isManagedProfile()) {
                userIdToCheck = user.profileGroupId;
            }
            Iterator<String> it = enabledPackages.iterator();
            while (true) {
                boolean z = true;
                if (it.hasNext()) {
                    String enabledPackage = it.next();
                    boolean systemService = false;
                    if ((this.mIPackageManager.getApplicationInfo(enabledPackage, 8192, userIdToCheck).flags & 1) == 0) {
                        z = false;
                    }
                    systemService = z;
                    if (!systemService && !permittedList.contains(enabledPackage)) {
                        this.mInjector.binderRestoreCallingIdentity(id);
                        return false;
                    }
                } else {
                    this.mInjector.binderRestoreCallingIdentity(id);
                    return true;
                }
            }
        } catch (RemoteException e) {
            Log.i(LOG_TAG, "Can't talk to package managed", e);
        } catch (Throwable th) {
            this.mInjector.binderRestoreCallingIdentity(id);
            throw th;
        }
    }

    private AccessibilityManager getAccessibilityManagerForUser(int userId) {
        IBinder iBinder = ServiceManager.getService("accessibility");
        return new AccessibilityManager(this.mContext, iBinder == null ? null : IAccessibilityManager.Stub.asInterface(iBinder), userId);
    }

    public boolean setPermittedAccessibilityServices(ComponentName who, List packageList) {
        if (!this.mHasFeature) {
            return false;
        }
        Preconditions.checkNotNull(who, "ComponentName is null");
        if (packageList != null) {
            int userId = UserHandle.getCallingUserId();
            long id = this.mInjector.binderClearCallingIdentity();
            try {
                UserInfo user = getUserInfo(userId);
                if (user.isManagedProfile()) {
                    userId = user.profileGroupId;
                }
                List<AccessibilityServiceInfo> enabledServices = getAccessibilityManagerForUser(userId).getEnabledAccessibilityServiceList(-1);
                if (enabledServices != null) {
                    List<String> enabledPackages = new ArrayList<>();
                    for (AccessibilityServiceInfo service : enabledServices) {
                        enabledPackages.add(service.getResolveInfo().serviceInfo.packageName);
                    }
                    if (!checkPackagesInPermittedListOrSystem(enabledPackages, packageList, userId)) {
                        Slog.e(LOG_TAG, "Cannot set permitted accessibility services, because it contains already enabled accesibility services.");
                        return false;
                    }
                }
            } finally {
                this.mInjector.binderRestoreCallingIdentity(id);
            }
        }
        synchronized (getLockObject()) {
            getActiveAdminForCallerLocked(who, -1).permittedAccessiblityServices = packageList;
            saveSettingsLocked(UserHandle.getCallingUserId());
        }
        DevicePolicyEventLogger.createEvent(28).setAdmin(who).setStrings(packageList != null ? (String[]) packageList.toArray(new String[0]) : null).write();
        return true;
    }

    public List getPermittedAccessibilityServices(ComponentName who) {
        List<String> list;
        if (!this.mHasFeature) {
            return null;
        }
        Preconditions.checkNotNull(who, "ComponentName is null");
        synchronized (getLockObject()) {
            list = getActiveAdminForCallerLocked(who, -1).permittedAccessiblityServices;
        }
        return list;
    }

    /* Debug info: failed to restart local var, previous not found, register: 13 */
    /* JADX INFO: finally extract failed */
    public List getPermittedAccessibilityServicesForUser(int userId) {
        List<String> result;
        if (!this.mHasFeature) {
            return null;
        }
        enforceManageUsers();
        synchronized (getLockObject()) {
            result = null;
            for (int profileId : this.mUserManager.getProfileIdsWithDisabled(userId)) {
                DevicePolicyData policy = getUserDataUnchecked(profileId);
                int N = policy.mAdminList.size();
                for (int j = 0; j < N; j++) {
                    List<String> fromAdmin = policy.mAdminList.get(j).permittedAccessiblityServices;
                    if (fromAdmin != null) {
                        if (result == null) {
                            result = new ArrayList<>(fromAdmin);
                        } else {
                            result.retainAll(fromAdmin);
                        }
                    }
                }
            }
            if (result != null) {
                long id = this.mInjector.binderClearCallingIdentity();
                try {
                    UserInfo user = getUserInfo(userId);
                    if (user.isManagedProfile()) {
                        userId = user.profileGroupId;
                    }
                    List<AccessibilityServiceInfo> installedServices = getAccessibilityManagerForUser(userId).getInstalledAccessibilityServiceList();
                    if (installedServices != null) {
                        for (AccessibilityServiceInfo service : installedServices) {
                            ServiceInfo serviceInfo = service.getResolveInfo().serviceInfo;
                            if ((serviceInfo.applicationInfo.flags & 1) != 0) {
                                result.add(serviceInfo.packageName);
                            }
                        }
                    }
                    this.mInjector.binderRestoreCallingIdentity(id);
                } catch (Throwable th) {
                    this.mInjector.binderRestoreCallingIdentity(id);
                    throw th;
                }
            }
        }
        return result;
    }

    public boolean isAccessibilityServicePermittedByAdmin(ComponentName who, String packageName, int userHandle) {
        if (!this.mHasFeature) {
            return true;
        }
        Preconditions.checkNotNull(who, "ComponentName is null");
        Preconditions.checkStringNotEmpty(packageName, "packageName is null");
        if (isCallerWithSystemUid()) {
            synchronized (getLockObject()) {
                ActiveAdmin admin = getActiveAdminUncheckedLocked(who, userHandle);
                if (admin == null) {
                    return false;
                }
                if (admin.permittedAccessiblityServices == null) {
                    return true;
                }
                boolean checkPackagesInPermittedListOrSystem = checkPackagesInPermittedListOrSystem(Collections.singletonList(packageName), admin.permittedAccessiblityServices, userHandle);
                return checkPackagesInPermittedListOrSystem;
            }
        }
        throw new SecurityException("Only the system can query if an accessibility service is disabled by admin");
    }

    private boolean checkCallerIsCurrentUserOrProfile() {
        int callingUserId = UserHandle.getCallingUserId();
        long token = this.mInjector.binderClearCallingIdentity();
        try {
            UserInfo callingUser = getUserInfo(callingUserId);
            UserInfo currentUser = this.mInjector.getIActivityManager().getCurrentUser();
            if (callingUser.isManagedProfile() && callingUser.profileGroupId != currentUser.id) {
                Slog.e(LOG_TAG, "Cannot set permitted input methods for managed profile of a user that isn't the foreground user.");
                return false;
            } else if (callingUser.isManagedProfile() || callingUserId == currentUser.id) {
                this.mInjector.binderRestoreCallingIdentity(token);
                return true;
            } else {
                Slog.e(LOG_TAG, "Cannot set permitted input methods of a user that isn't the foreground user.");
                return false;
            }
        } catch (RemoteException e) {
            Slog.e(LOG_TAG, "Failed to talk to activity managed.", e);
            return false;
        } finally {
            this.mInjector.binderRestoreCallingIdentity(token);
        }
    }

    public boolean setPermittedInputMethods(ComponentName who, List packageList) {
        List<InputMethodInfo> enabledImes;
        if (!this.mHasFeature) {
            return false;
        }
        Preconditions.checkNotNull(who, "ComponentName is null");
        if (!InputMethodSystemProperty.PER_PROFILE_IME_ENABLED && !checkCallerIsCurrentUserOrProfile()) {
            return false;
        }
        int callingUserId = this.mInjector.userHandleGetCallingUserId();
        if (!(packageList == null || (enabledImes = InputMethodManagerInternal.get().getEnabledInputMethodListAsUser(callingUserId)) == null)) {
            List<String> enabledPackages = new ArrayList<>();
            for (InputMethodInfo ime : enabledImes) {
                enabledPackages.add(ime.getPackageName());
            }
            if (!checkPackagesInPermittedListOrSystem(enabledPackages, packageList, callingUserId)) {
                Slog.e(LOG_TAG, "Cannot set permitted input methods, because it contains already enabled input method.");
                return false;
            }
        }
        synchronized (getLockObject()) {
            getActiveAdminForCallerLocked(who, -1).permittedInputMethods = packageList;
            saveSettingsLocked(callingUserId);
        }
        DevicePolicyEventLogger.createEvent(27).setAdmin(who).setStrings(packageList != null ? (String[]) packageList.toArray(new String[0]) : null).write();
        return true;
    }

    public List getPermittedInputMethods(ComponentName who) {
        List<String> list;
        if (!this.mHasFeature) {
            return null;
        }
        Preconditions.checkNotNull(who, "ComponentName is null");
        synchronized (getLockObject()) {
            list = getActiveAdminForCallerLocked(who, -1).permittedInputMethods;
        }
        return list;
    }

    public List getPermittedInputMethodsForCurrentUser() {
        List<String> result;
        List<InputMethodInfo> imes;
        enforceManageUsers();
        int callingUserId = this.mInjector.userHandleGetCallingUserId();
        synchronized (getLockObject()) {
            result = null;
            for (int profileId : InputMethodSystemProperty.PER_PROFILE_IME_ENABLED ? new int[]{callingUserId} : this.mUserManager.getProfileIdsWithDisabled(callingUserId)) {
                DevicePolicyData policy = getUserDataUnchecked(profileId);
                int N = policy.mAdminList.size();
                for (int j = 0; j < N; j++) {
                    List<String> fromAdmin = policy.mAdminList.get(j).permittedInputMethods;
                    if (fromAdmin != null) {
                        if (result == null) {
                            result = new ArrayList<>(fromAdmin);
                        } else {
                            result.retainAll(fromAdmin);
                        }
                    }
                }
            }
            if (!(result == null || (imes = InputMethodManagerInternal.get().getInputMethodListAsUser(callingUserId)) == null)) {
                for (InputMethodInfo ime : imes) {
                    ServiceInfo serviceInfo = ime.getServiceInfo();
                    if ((serviceInfo.applicationInfo.flags & 1) != 0) {
                        result.add(serviceInfo.packageName);
                    }
                }
            }
        }
        return result;
    }

    public boolean isInputMethodPermittedByAdmin(ComponentName who, String packageName, int userHandle) {
        if (!this.mHasFeature) {
            return true;
        }
        Preconditions.checkNotNull(who, "ComponentName is null");
        Preconditions.checkStringNotEmpty(packageName, "packageName is null");
        if (isCallerWithSystemUid()) {
            synchronized (getLockObject()) {
                ActiveAdmin admin = getActiveAdminUncheckedLocked(who, userHandle);
                if (admin == null) {
                    return false;
                }
                if (admin.permittedInputMethods == null) {
                    return true;
                }
                boolean checkPackagesInPermittedListOrSystem = checkPackagesInPermittedListOrSystem(Collections.singletonList(packageName), admin.permittedInputMethods, userHandle);
                return checkPackagesInPermittedListOrSystem;
            }
        }
        throw new SecurityException("Only the system can query if an input method is disabled by admin");
    }

    public boolean setPermittedCrossProfileNotificationListeners(ComponentName who, List<String> packageList) {
        if (!this.mHasFeature) {
            return false;
        }
        Preconditions.checkNotNull(who, "ComponentName is null");
        int callingUserId = this.mInjector.userHandleGetCallingUserId();
        if (!isManagedProfile(callingUserId)) {
            return false;
        }
        synchronized (getLockObject()) {
            getActiveAdminForCallerLocked(who, -1).permittedNotificationListeners = packageList;
            saveSettingsLocked(callingUserId);
        }
        return true;
    }

    public List<String> getPermittedCrossProfileNotificationListeners(ComponentName who) {
        List<String> list;
        if (!this.mHasFeature) {
            return null;
        }
        Preconditions.checkNotNull(who, "ComponentName is null");
        synchronized (getLockObject()) {
            list = getActiveAdminForCallerLocked(who, -1).permittedNotificationListeners;
        }
        return list;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x002f, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isNotificationListenerServicePermitted(java.lang.String r5, int r6) {
        /*
            r4 = this;
            boolean r0 = r4.mHasFeature
            r1 = 1
            if (r0 != 0) goto L_0x0006
            return r1
        L_0x0006:
            java.lang.String r0 = "packageName is null or empty"
            com.android.internal.util.Preconditions.checkStringNotEmpty(r5, r0)
            boolean r0 = r4.isCallerWithSystemUid()
            if (r0 == 0) goto L_0x0033
            java.lang.Object r0 = r4.getLockObject()
            monitor-enter(r0)
            com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin r2 = r4.getProfileOwnerAdminLocked(r6)     // Catch:{ all -> 0x0030 }
            if (r2 == 0) goto L_0x002e
            java.util.List<java.lang.String> r3 = r2.permittedNotificationListeners     // Catch:{ all -> 0x0030 }
            if (r3 != 0) goto L_0x0022
            goto L_0x002e
        L_0x0022:
            java.util.List r1 = java.util.Collections.singletonList(r5)     // Catch:{ all -> 0x0030 }
            java.util.List<java.lang.String> r3 = r2.permittedNotificationListeners     // Catch:{ all -> 0x0030 }
            boolean r1 = r4.checkPackagesInPermittedListOrSystem(r1, r3, r6)     // Catch:{ all -> 0x0030 }
            monitor-exit(r0)     // Catch:{ all -> 0x0030 }
            return r1
        L_0x002e:
            monitor-exit(r0)     // Catch:{ all -> 0x0030 }
            return r1
        L_0x0030:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0030 }
            throw r1
        L_0x0033:
            java.lang.SecurityException r0 = new java.lang.SecurityException
            java.lang.String r1 = "Only the system can query if a notification listener service is permitted"
            r0.<init>(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.DevicePolicyManagerService.isNotificationListenerServicePermitted(java.lang.String, int):boolean");
    }

    /* access modifiers changed from: private */
    public void maybeSendAdminEnabledBroadcastLocked(int userHandle) {
        DevicePolicyData policyData = getUserData(userHandle);
        if (policyData.mAdminBroadcastPending) {
            ActiveAdmin admin = getProfileOwnerAdminLocked(userHandle);
            boolean clearInitBundle = true;
            if (admin != null) {
                PersistableBundle initBundle = policyData.mInitBundle;
                clearInitBundle = sendAdminCommandLocked(admin, "android.app.action.DEVICE_ADMIN_ENABLED", initBundle == null ? null : new Bundle(initBundle), (BroadcastReceiver) null, true);
            }
            if (clearInitBundle) {
                policyData.mInitBundle = null;
                policyData.mAdminBroadcastPending = false;
                saveSettingsLocked(userHandle);
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 24 */
    /* JADX WARNING: Code restructure failed: missing block: B:110:0x01af, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:111:0x01b1, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:112:0x01b2, code lost:
        r18 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:114:?, code lost:
        r1.mUserManager.removeUser(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:116:0x01bb, code lost:
        if (r14 >= 28) goto L_0x01c4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:118:0x01c3, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:121:0x01ce, code lost:
        throw new android.os.ServiceSpecificException(1, r0.getMessage());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:122:0x01cf, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:123:0x01d0, code lost:
        r1.mInjector.binderRestoreCallingIdentity(r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:124:0x01d5, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x00fb, code lost:
        if (r9 != null) goto L_0x010b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:0x00ff, code lost:
        if (r14 >= 28) goto L_0x0102;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x0101, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x010a, code lost:
        throw new android.os.ServiceSpecificException(1, "failed to create user");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:75:0x010b, code lost:
        r4 = r9.getIdentifier();
        r1.mContext.sendBroadcastAsUser(new android.content.Intent("android.app.action.MANAGED_USER_CREATED").putExtra("android.intent.extra.user_handle", r4).putExtra("android.app.extra.PROVISIONING_LEAVE_ALL_SYSTEM_APPS_ENABLED", r8).setPackage(getManagedProvisioningPackage(r1.mContext)).addFlags(268435456), android.os.UserHandle.SYSTEM);
        r11 = r1.mInjector.binderClearCallingIdentity();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:78:0x0143, code lost:
        r13 = r25.getPackageName();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:81:0x014a, code lost:
        if (r1.mIPackageManager.isPackageAvailable(r13, r4) != false) goto L_0x0163;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:82:0x014c, code lost:
        r1.mIPackageManager.installExistingPackageAsUser(r13, r4, com.android.server.pm.DumpState.DUMP_CHANGES, 1, (java.util.List) null);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:83:0x015e, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:0x015f, code lost:
        r18 = r6;
     */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x01bd A[DONT_GENERATE] */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x01c4 A[SYNTHETIC, Splitter:B:119:0x01c4] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.os.UserHandle createAndManageUser(android.content.ComponentName r25, java.lang.String r26, android.content.ComponentName r27, android.os.PersistableBundle r28, int r29) {
        /*
            r24 = this;
            r1 = r24
            r2 = r25
            r3 = r27
            java.lang.String r0 = "admin is null"
            com.android.internal.util.Preconditions.checkNotNull(r2, r0)
            java.lang.String r0 = "profileOwner is null"
            com.android.internal.util.Preconditions.checkNotNull(r3, r0)
            java.lang.String r0 = r25.getPackageName()
            java.lang.String r4 = r27.getPackageName()
            boolean r0 = r0.equals(r4)
            if (r0 == 0) goto L_0x01ee
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r0 = r1.mInjector
            android.os.UserHandle r0 = r0.binderGetCallingUserHandle()
            boolean r0 = r0.isSystem()
            if (r0 == 0) goto L_0x01e6
            r0 = r29 & 2
            if (r0 == 0) goto L_0x0031
            r0 = 1
            goto L_0x0032
        L_0x0031:
            r0 = 0
        L_0x0032:
            r6 = r0
            r0 = r29 & 4
            if (r0 == 0) goto L_0x0041
            android.content.Context r0 = r1.mContext
            boolean r0 = android.os.UserManager.isDeviceInDemoMode(r0)
            if (r0 == 0) goto L_0x0041
            r0 = 1
            goto L_0x0042
        L_0x0041:
            r0 = 0
        L_0x0042:
            r7 = r0
            r0 = r29 & 16
            if (r0 == 0) goto L_0x0049
            r0 = 1
            goto L_0x004a
        L_0x0049:
            r0 = 0
        L_0x004a:
            r8 = r0
            r9 = 0
            java.lang.Object r10 = r24.getLockObject()
            monitor-enter(r10)
            r0 = -2
            r1.getActiveAdminForCallerLocked(r2, r0)     // Catch:{ all -> 0x01df }
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r0 = r1.mInjector     // Catch:{ all -> 0x01df }
            int r0 = r0.binderGetCallingUid()     // Catch:{ all -> 0x01df }
            r11 = r0
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r0 = r1.mInjector     // Catch:{ all -> 0x01df }
            long r12 = r0.binderClearCallingIdentity()     // Catch:{ all -> 0x01df }
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r0 = r1.mInjector     // Catch:{ all -> 0x01d6 }
            android.content.pm.PackageManagerInternal r0 = r0.getPackageManagerInternal()     // Catch:{ all -> 0x01d6 }
            int r0 = r0.getUidTargetSdkVersion(r11)     // Catch:{ all -> 0x01d6 }
            r14 = r0
            java.lang.Class<com.android.server.storage.DeviceStorageMonitorInternal> r0 = com.android.server.storage.DeviceStorageMonitorInternal.class
            java.lang.Object r0 = com.android.server.LocalServices.getService(r0)     // Catch:{ all -> 0x01d6 }
            com.android.server.storage.DeviceStorageMonitorInternal r0 = (com.android.server.storage.DeviceStorageMonitorInternal) r0     // Catch:{ all -> 0x01d6 }
            boolean r15 = r0.isMemoryLow()     // Catch:{ all -> 0x01d6 }
            r16 = 0
            r5 = 28
            if (r15 == 0) goto L_0x009d
            if (r14 >= r5) goto L_0x008e
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r4 = r1.mInjector     // Catch:{ all -> 0x0089 }
            r4.binderRestoreCallingIdentity(r12)     // Catch:{ all -> 0x0089 }
            monitor-exit(r10)     // Catch:{ all -> 0x0089 }
            return r16
        L_0x0089:
            r0 = move-exception
            r18 = r6
            goto L_0x01e2
        L_0x008e:
            android.os.ServiceSpecificException r4 = new android.os.ServiceSpecificException     // Catch:{ all -> 0x0098 }
            r5 = 5
            java.lang.String r15 = "low device storage"
            r4.<init>(r5, r15)     // Catch:{ all -> 0x0098 }
            throw r4     // Catch:{ all -> 0x0098 }
        L_0x0098:
            r0 = move-exception
            r18 = r6
            goto L_0x01d9
        L_0x009d:
            android.os.UserManager r15 = r1.mUserManager     // Catch:{ all -> 0x01d6 }
            boolean r15 = r15.canAddMoreUsers()     // Catch:{ all -> 0x01d6 }
            if (r15 != 0) goto L_0x00b9
            if (r14 >= r5) goto L_0x00af
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r4 = r1.mInjector     // Catch:{ all -> 0x0089 }
            r4.binderRestoreCallingIdentity(r12)     // Catch:{ all -> 0x0089 }
            monitor-exit(r10)     // Catch:{ all -> 0x0089 }
            return r16
        L_0x00af:
            android.os.ServiceSpecificException r4 = new android.os.ServiceSpecificException     // Catch:{ all -> 0x0098 }
            r5 = 6
            java.lang.String r15 = "user limit reached"
            r4.<init>(r5, r15)     // Catch:{ all -> 0x0098 }
            throw r4     // Catch:{ all -> 0x0098 }
        L_0x00b9:
            r15 = 0
            if (r6 == 0) goto L_0x00be
            r15 = r15 | 256(0x100, float:3.59E-43)
        L_0x00be:
            if (r7 == 0) goto L_0x00c2
            r15 = r15 | 512(0x200, float:7.175E-43)
        L_0x00c2:
            r17 = 0
            if (r8 != 0) goto L_0x00e0
            com.android.server.devicepolicy.OverlayPackagesProvider r5 = r1.mOverlayPackagesProvider     // Catch:{ all -> 0x0098 }
            int r4 = android.os.UserHandle.myUserId()     // Catch:{ all -> 0x0098 }
            r19 = r0
            java.lang.String r0 = "android.app.action.PROVISION_MANAGED_USER"
            java.util.Set r0 = r5.getNonRequiredApps(r2, r4, r0)     // Catch:{ all -> 0x0098 }
            r4 = 0
            java.lang.String[] r4 = new java.lang.String[r4]     // Catch:{ all -> 0x0098 }
            java.lang.Object[] r0 = r0.toArray(r4)     // Catch:{ all -> 0x0098 }
            java.lang.String[] r0 = (java.lang.String[]) r0     // Catch:{ all -> 0x0098 }
            r17 = r0
            goto L_0x00e4
        L_0x00e0:
            r19 = r0
            r0 = r17
        L_0x00e4:
            android.os.UserManagerInternal r4 = r1.mUserManagerInternal     // Catch:{ all -> 0x01d6 }
            r5 = r26
            android.content.pm.UserInfo r4 = r4.createUserEvenWhenDisallowed(r5, r15, r0)     // Catch:{ all -> 0x01d6 }
            if (r4 == 0) goto L_0x00f4
            android.os.UserHandle r17 = r4.getUserHandle()     // Catch:{ all -> 0x0098 }
            r9 = r17
        L_0x00f4:
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r0 = r1.mInjector     // Catch:{ all -> 0x01df }
            r0.binderRestoreCallingIdentity(r12)     // Catch:{ all -> 0x01df }
            monitor-exit(r10)     // Catch:{ all -> 0x01df }
            if (r9 != 0) goto L_0x010b
            r4 = 28
            if (r14 >= r4) goto L_0x0102
            return r16
        L_0x0102:
            android.os.ServiceSpecificException r0 = new android.os.ServiceSpecificException
            java.lang.String r4 = "failed to create user"
            r10 = 1
            r0.<init>(r10, r4)
            throw r0
        L_0x010b:
            int r4 = r9.getIdentifier()
            android.content.Intent r0 = new android.content.Intent
            java.lang.String r10 = "android.app.action.MANAGED_USER_CREATED"
            r0.<init>(r10)
            java.lang.String r10 = "android.intent.extra.user_handle"
            android.content.Intent r0 = r0.putExtra(r10, r4)
            java.lang.String r10 = "android.app.extra.PROVISIONING_LEAVE_ALL_SYSTEM_APPS_ENABLED"
            android.content.Intent r0 = r0.putExtra(r10, r8)
            android.content.Context r10 = r1.mContext
            java.lang.String r10 = getManagedProvisioningPackage(r10)
            android.content.Intent r0 = r0.setPackage(r10)
            r10 = 268435456(0x10000000, float:2.5243549E-29)
            android.content.Intent r10 = r0.addFlags(r10)
            android.content.Context r0 = r1.mContext
            android.os.UserHandle r11 = android.os.UserHandle.SYSTEM
            r0.sendBroadcastAsUser(r10, r11)
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r0 = r1.mInjector
            long r11 = r0.binderClearCallingIdentity()
            java.lang.String r0 = r25.getPackageName()     // Catch:{ all -> 0x01b1 }
            r13 = r0
            android.content.pm.IPackageManager r0 = r1.mIPackageManager     // Catch:{ RemoteException -> 0x0162, all -> 0x015e }
            boolean r0 = r0.isPackageAvailable(r13, r4)     // Catch:{ RemoteException -> 0x0162, all -> 0x015e }
            if (r0 != 0) goto L_0x015d
            android.content.pm.IPackageManager r0 = r1.mIPackageManager     // Catch:{ RemoteException -> 0x0162, all -> 0x015e }
            r21 = 4194304(0x400000, float:5.877472E-39)
            r22 = 1
            r23 = 0
            r18 = r0
            r19 = r13
            r20 = r4
            r18.installExistingPackageAsUser(r19, r20, r21, r22, r23)     // Catch:{ RemoteException -> 0x0162, all -> 0x015e }
        L_0x015d:
            goto L_0x0163
        L_0x015e:
            r0 = move-exception
            r18 = r6
            goto L_0x01b4
        L_0x0162:
            r0 = move-exception
        L_0x0163:
            r15 = 1
            r1.setActiveAdmin(r3, r15, r4)     // Catch:{ all -> 0x01b1 }
            android.os.UserHandle r0 = android.os.Process.myUserHandle()     // Catch:{ all -> 0x01b1 }
            int r0 = r0.getIdentifier()     // Catch:{ all -> 0x01b1 }
            java.lang.String r0 = r1.getProfileOwnerName(r0)     // Catch:{ all -> 0x01b1 }
            r15 = r0
            r1.setProfileOwner(r3, r15, r4)     // Catch:{ all -> 0x01b1 }
            java.lang.Object r17 = r24.getLockObject()     // Catch:{ all -> 0x01b1 }
            monitor-enter(r17)     // Catch:{ all -> 0x01b1 }
            com.android.server.devicepolicy.DevicePolicyManagerService$DevicePolicyData r0 = r1.getUserData(r4)     // Catch:{ all -> 0x01a8 }
            r5 = r28
            r0.mInitBundle = r5     // Catch:{ all -> 0x01a8 }
            r5 = 1
            r0.mAdminBroadcastPending = r5     // Catch:{ all -> 0x01a8 }
            r1.saveSettingsLocked(r4)     // Catch:{ all -> 0x01a8 }
            monitor-exit(r17)     // Catch:{ all -> 0x01a8 }
            r0 = r29 & 1
            if (r0 == 0) goto L_0x019f
            android.content.Context r0 = r1.mContext     // Catch:{ all -> 0x01b1 }
            android.content.ContentResolver r0 = r0.getContentResolver()     // Catch:{ all -> 0x01b1 }
            java.lang.String r5 = "user_setup_complete"
            r18 = r6
            r6 = 1
            android.provider.Settings.Secure.putIntForUser(r0, r5, r6, r4)     // Catch:{ all -> 0x01ad }
            goto L_0x01a1
        L_0x019f:
            r18 = r6
        L_0x01a1:
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r0 = r1.mInjector
            r0.binderRestoreCallingIdentity(r11)
            return r9
        L_0x01a8:
            r0 = move-exception
            r18 = r6
        L_0x01ab:
            monitor-exit(r17)     // Catch:{ all -> 0x01af }
            throw r0     // Catch:{ all -> 0x01ad }
        L_0x01ad:
            r0 = move-exception
            goto L_0x01b4
        L_0x01af:
            r0 = move-exception
            goto L_0x01ab
        L_0x01b1:
            r0 = move-exception
            r18 = r6
        L_0x01b4:
            android.os.UserManager r5 = r1.mUserManager     // Catch:{ all -> 0x01cf }
            r5.removeUser(r4)     // Catch:{ all -> 0x01cf }
            r5 = 28
            if (r14 >= r5) goto L_0x01c4
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r5 = r1.mInjector
            r5.binderRestoreCallingIdentity(r11)
            return r16
        L_0x01c4:
            android.os.ServiceSpecificException r5 = new android.os.ServiceSpecificException     // Catch:{ all -> 0x01cf }
            java.lang.String r6 = r0.getMessage()     // Catch:{ all -> 0x01cf }
            r13 = 1
            r5.<init>(r13, r6)     // Catch:{ all -> 0x01cf }
            throw r5     // Catch:{ all -> 0x01cf }
        L_0x01cf:
            r0 = move-exception
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r5 = r1.mInjector
            r5.binderRestoreCallingIdentity(r11)
            throw r0
        L_0x01d6:
            r0 = move-exception
            r18 = r6
        L_0x01d9:
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r4 = r1.mInjector     // Catch:{ all -> 0x01e4 }
            r4.binderRestoreCallingIdentity(r12)     // Catch:{ all -> 0x01e4 }
            throw r0     // Catch:{ all -> 0x01e4 }
        L_0x01df:
            r0 = move-exception
            r18 = r6
        L_0x01e2:
            monitor-exit(r10)     // Catch:{ all -> 0x01e4 }
            throw r0
        L_0x01e4:
            r0 = move-exception
            goto L_0x01e2
        L_0x01e6:
            java.lang.SecurityException r0 = new java.lang.SecurityException
            java.lang.String r4 = "createAndManageUser was called from non-system user"
            r0.<init>(r4)
            throw r0
        L_0x01ee:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "profileOwner "
            r4.append(r5)
            r4.append(r3)
            java.lang.String r5 = " and admin "
            r4.append(r5)
            r4.append(r2)
            java.lang.String r5 = " are not in the same package"
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            r0.<init>(r4)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.DevicePolicyManagerService.createAndManageUser(android.content.ComponentName, java.lang.String, android.content.ComponentName, android.os.PersistableBundle, int):android.os.UserHandle");
    }

    public boolean removeUser(ComponentName who, UserHandle userHandle) {
        String restriction;
        Preconditions.checkNotNull(who, "ComponentName is null");
        Preconditions.checkNotNull(userHandle, "UserHandle is null");
        enforceDeviceOwner(who);
        int callingUserId = this.mInjector.userHandleGetCallingUserId();
        long id = this.mInjector.binderClearCallingIdentity();
        try {
            if (isManagedProfile(userHandle.getIdentifier())) {
                restriction = "no_remove_managed_profile";
            } else {
                restriction = "no_remove_user";
            }
            if (isAdminAffectedByRestriction(who, restriction, callingUserId)) {
                Log.w(LOG_TAG, "The device owner cannot remove a user because " + restriction + " is enabled, and was not set by the device owner");
                return false;
            }
            boolean removeUserEvenWhenDisallowed = this.mUserManagerInternal.removeUserEvenWhenDisallowed(userHandle.getIdentifier());
            this.mInjector.binderRestoreCallingIdentity(id);
            return removeUserEvenWhenDisallowed;
        } finally {
            this.mInjector.binderRestoreCallingIdentity(id);
        }
    }

    private boolean isAdminAffectedByRestriction(ComponentName admin, String userRestriction, int userId) {
        int userRestrictionSource = this.mUserManager.getUserRestrictionSource(userRestriction, UserHandle.of(userId));
        if (userRestrictionSource == 0) {
            return false;
        }
        if (userRestrictionSource == 2) {
            return !isDeviceOwner(admin, userId);
        }
        if (userRestrictionSource != 4) {
            return true;
        }
        return !isProfileOwner(admin, userId);
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    public boolean switchUser(ComponentName who, UserHandle userHandle) {
        boolean switchUser;
        Preconditions.checkNotNull(who, "ComponentName is null");
        synchronized (getLockObject()) {
            getActiveAdminForCallerLocked(who, -2);
            long id = this.mInjector.binderClearCallingIdentity();
            int userId = 0;
            if (userHandle != null) {
                try {
                    userId = userHandle.getIdentifier();
                } catch (RemoteException e) {
                    try {
                        Log.e(LOG_TAG, "Couldn't switch user", e);
                        return false;
                    } finally {
                        this.mInjector.binderRestoreCallingIdentity(id);
                    }
                }
            }
            switchUser = this.mInjector.getIActivityManager().switchUser(userId);
            this.mInjector.binderRestoreCallingIdentity(id);
        }
        return switchUser;
    }

    public int startUserInBackground(ComponentName who, UserHandle userHandle) {
        Preconditions.checkNotNull(who, "ComponentName is null");
        Preconditions.checkNotNull(userHandle, "UserHandle is null");
        enforceDeviceOwner(who);
        int userId = userHandle.getIdentifier();
        if (isManagedProfile(userId)) {
            Log.w(LOG_TAG, "Managed profile cannot be started in background");
            return 2;
        }
        long id = this.mInjector.binderClearCallingIdentity();
        try {
            if (!this.mInjector.getActivityManagerInternal().canStartMoreUsers()) {
                Log.w(LOG_TAG, "Cannot start more users in background");
                return 3;
            } else if (this.mInjector.getIActivityManager().startUserInBackground(userId)) {
                this.mInjector.binderRestoreCallingIdentity(id);
                return 0;
            } else {
                this.mInjector.binderRestoreCallingIdentity(id);
                return 1;
            }
        } catch (RemoteException e) {
            return 1;
        } finally {
            this.mInjector.binderRestoreCallingIdentity(id);
        }
    }

    public int stopUser(ComponentName who, UserHandle userHandle) {
        Preconditions.checkNotNull(who, "ComponentName is null");
        Preconditions.checkNotNull(userHandle, "UserHandle is null");
        enforceDeviceOwner(who);
        int userId = userHandle.getIdentifier();
        if (!isManagedProfile(userId)) {
            return stopUserUnchecked(userId);
        }
        Log.w(LOG_TAG, "Managed profile cannot be stopped");
        return 2;
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    public int logoutUser(ComponentName who) {
        Preconditions.checkNotNull(who, "ComponentName is null");
        int callingUserId = this.mInjector.userHandleGetCallingUserId();
        synchronized (getLockObject()) {
            getActiveAdminForCallerLocked(who, -1);
            if (!isUserAffiliatedWithDeviceLocked(callingUserId)) {
                throw new SecurityException("Admin " + who + " is neither the device owner or affiliated user's profile owner.");
            }
        }
        if (isManagedProfile(callingUserId)) {
            Log.w(LOG_TAG, "Managed profile cannot be logout");
            return 2;
        }
        long id = this.mInjector.binderClearCallingIdentity();
        try {
            if (!this.mInjector.getIActivityManager().switchUser(0)) {
                Log.w(LOG_TAG, "Failed to switch to primary user");
                return 1;
            }
            this.mInjector.binderRestoreCallingIdentity(id);
            return stopUserUnchecked(callingUserId);
        } catch (RemoteException e) {
            return 1;
        } finally {
            this.mInjector.binderRestoreCallingIdentity(id);
        }
    }

    private int stopUserUnchecked(int userId) {
        long id = this.mInjector.binderClearCallingIdentity();
        try {
            int stopUser = this.mInjector.getIActivityManager().stopUser(userId, true, (IStopUserCallback) null);
            if (stopUser == -2) {
                this.mInjector.binderRestoreCallingIdentity(id);
                return 4;
            } else if (stopUser != 0) {
                return 1;
            } else {
                this.mInjector.binderRestoreCallingIdentity(id);
                return 0;
            }
        } catch (RemoteException e) {
            return 1;
        } finally {
            this.mInjector.binderRestoreCallingIdentity(id);
        }
    }

    public List<UserHandle> getSecondaryUsers(ComponentName who) {
        Preconditions.checkNotNull(who, "ComponentName is null");
        enforceDeviceOwner(who);
        long id = this.mInjector.binderClearCallingIdentity();
        try {
            List<UserInfo> userInfos = this.mInjector.getUserManager().getUsers(true);
            List<UserHandle> userHandles = new ArrayList<>();
            for (UserInfo userInfo : userInfos) {
                UserHandle userHandle = userInfo.getUserHandle();
                if (!userHandle.isSystem() && !isManagedProfile(userHandle.getIdentifier())) {
                    userHandles.add(userInfo.getUserHandle());
                }
            }
            return userHandles;
        } finally {
            this.mInjector.binderRestoreCallingIdentity(id);
        }
    }

    public boolean isEphemeralUser(ComponentName who) {
        Preconditions.checkNotNull(who, "ComponentName is null");
        enforceProfileOrDeviceOwner(who);
        int callingUserId = this.mInjector.userHandleGetCallingUserId();
        long id = this.mInjector.binderClearCallingIdentity();
        try {
            return this.mInjector.getUserManager().isUserEphemeral(callingUserId);
        } finally {
            this.mInjector.binderRestoreCallingIdentity(id);
        }
    }

    public Bundle getApplicationRestrictions(ComponentName who, String callerPackage, String packageName) {
        enforceCanManageScope(who, callerPackage, -1, "delegation-app-restrictions");
        UserHandle userHandle = this.mInjector.binderGetCallingUserHandle();
        long id = this.mInjector.binderClearCallingIdentity();
        try {
            Bundle bundle = this.mUserManager.getApplicationRestrictions(packageName, userHandle);
            return bundle != null ? bundle : Bundle.EMPTY;
        } finally {
            this.mInjector.binderRestoreCallingIdentity(id);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 18 */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0059  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x005b  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0073 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0074 A[RETURN] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String[] setPackagesSuspended(android.content.ComponentName r19, java.lang.String r20, java.lang.String[] r21, boolean r22) {
        /*
            r18 = this;
            r1 = r18
            r2 = r19
            r3 = r20
            r12 = r21
            int r13 = android.os.UserHandle.getCallingUserId()
            r14 = 0
            java.lang.Object r15 = r18.getLockObject()
            monitor-enter(r15)
            r0 = -1
            java.lang.String r4 = "delegation-package-access"
            r1.enforceCanManageScope(r2, r3, r0, r4)     // Catch:{ all -> 0x007c }
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r0 = r1.mInjector     // Catch:{ all -> 0x007c }
            long r4 = r0.binderClearCallingIdentity()     // Catch:{ all -> 0x007c }
            r10 = r4
            android.content.pm.IPackageManager r4 = r1.mIPackageManager     // Catch:{ RemoteException -> 0x0046, all -> 0x0043 }
            r7 = 0
            r8 = 0
            r9 = 0
            java.lang.String r0 = "android"
            r5 = r21
            r6 = r22
            r16 = r10
            r10 = r0
            r11 = r13
            java.lang.String[] r0 = r4.setPackagesSuspendedAsUser(r5, r6, r7, r8, r9, r10, r11)     // Catch:{ RemoteException -> 0x003f, all -> 0x003b }
            r14 = r0
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r0 = r1.mInjector     // Catch:{ all -> 0x007c }
            r4 = r16
            r0.binderRestoreCallingIdentity(r4)     // Catch:{ all -> 0x007c }
        L_0x003a:
            goto L_0x0056
        L_0x003b:
            r0 = move-exception
            r4 = r16
            goto L_0x0076
        L_0x003f:
            r0 = move-exception
            r4 = r16
            goto L_0x0048
        L_0x0043:
            r0 = move-exception
            r4 = r10
            goto L_0x0076
        L_0x0046:
            r0 = move-exception
            r4 = r10
        L_0x0048:
            java.lang.String r6 = "DevicePolicyManager"
            java.lang.String r7 = "Failed talking to the package manager"
            android.util.Slog.e(r6, r7, r0)     // Catch:{ all -> 0x0075 }
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r0 = r1.mInjector     // Catch:{ all -> 0x007c }
            r0.binderRestoreCallingIdentity(r4)     // Catch:{ all -> 0x007c }
            goto L_0x003a
        L_0x0056:
            monitor-exit(r15)     // Catch:{ all -> 0x007c }
            if (r2 != 0) goto L_0x005b
            r0 = 1
            goto L_0x005c
        L_0x005b:
            r0 = 0
        L_0x005c:
            r4 = 68
            android.app.admin.DevicePolicyEventLogger r4 = android.app.admin.DevicePolicyEventLogger.createEvent(r4)
            android.app.admin.DevicePolicyEventLogger r4 = r4.setAdmin(r3)
            android.app.admin.DevicePolicyEventLogger r4 = r4.setBoolean(r0)
            android.app.admin.DevicePolicyEventLogger r4 = r4.setStrings(r12)
            r4.write()
            if (r14 == 0) goto L_0x0074
            return r14
        L_0x0074:
            return r12
        L_0x0075:
            r0 = move-exception
        L_0x0076:
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r6 = r1.mInjector     // Catch:{ all -> 0x007c }
            r6.binderRestoreCallingIdentity(r4)     // Catch:{ all -> 0x007c }
            throw r0     // Catch:{ all -> 0x007c }
        L_0x007c:
            r0 = move-exception
            monitor-exit(r15)     // Catch:{ all -> 0x007c }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.DevicePolicyManagerService.setPackagesSuspended(android.content.ComponentName, java.lang.String, java.lang.String[], boolean):java.lang.String[]");
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    /* JADX INFO: finally extract failed */
    public boolean isPackageSuspended(ComponentName who, String callerPackage, String packageName) {
        boolean isPackageSuspendedForUser;
        int callingUserId = UserHandle.getCallingUserId();
        synchronized (getLockObject()) {
            enforceCanManageScope(who, callerPackage, -1, "delegation-package-access");
            long id = this.mInjector.binderClearCallingIdentity();
            try {
                isPackageSuspendedForUser = this.mIPackageManager.isPackageSuspendedForUser(packageName, callingUserId);
                this.mInjector.binderRestoreCallingIdentity(id);
            } catch (RemoteException re) {
                try {
                    Slog.e(LOG_TAG, "Failed talking to the package manager", re);
                    this.mInjector.binderRestoreCallingIdentity(id);
                    return false;
                } catch (Throwable th) {
                    this.mInjector.binderRestoreCallingIdentity(id);
                    throw th;
                }
            }
        }
        return isPackageSuspendedForUser;
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    public void setUserRestriction(ComponentName who, String key, boolean enabledFromThisOwner) {
        int eventId;
        int eventTag;
        Preconditions.checkNotNull(who, "ComponentName is null");
        if (UserRestrictionsUtils.isValidRestriction(key)) {
            int userHandle = this.mInjector.userHandleGetCallingUserId();
            synchronized (getLockObject()) {
                ActiveAdmin activeAdmin = getActiveAdminForCallerLocked(who, -1);
                if (isDeviceOwner(who, userHandle)) {
                    if (!UserRestrictionsUtils.canDeviceOwnerChange(key)) {
                        throw new SecurityException("Device owner cannot set user restriction " + key);
                    }
                } else if (!UserRestrictionsUtils.canProfileOwnerChange(key, userHandle)) {
                    throw new SecurityException("Profile owner cannot set user restriction " + key);
                }
                Bundle restrictions = activeAdmin.ensureUserRestrictions();
                if (enabledFromThisOwner) {
                    restrictions.putBoolean(key, true);
                } else {
                    restrictions.remove(key);
                }
                saveUserRestrictionsLocked(userHandle);
            }
            if (enabledFromThisOwner) {
                eventId = 12;
            } else {
                eventId = 13;
            }
            DevicePolicyEventLogger.createEvent(eventId).setAdmin(who).setStrings(new String[]{key}).write();
            if (SecurityLog.isLoggingEnabled()) {
                if (enabledFromThisOwner) {
                    eventTag = 210027;
                } else {
                    eventTag = 210028;
                }
                SecurityLog.writeEvent(eventTag, new Object[]{who.getPackageName(), Integer.valueOf(userHandle), key});
            }
        }
    }

    private void saveUserRestrictionsLocked(int userId) {
        saveSettingsLocked(userId);
        pushUserRestrictions(userId);
        sendChangedNotification(userId);
    }

    private void pushUserRestrictions(int userId) {
        Bundle userRestrictions;
        synchronized (getLockObject()) {
            boolean isDeviceOwner = this.mOwners.isDeviceOwnerUserId(userId);
            boolean disallowCameraGlobally = false;
            if (isDeviceOwner) {
                ActiveAdmin deviceOwner = getDeviceOwnerAdminLocked();
                if (deviceOwner != null) {
                    userRestrictions = deviceOwner.userRestrictions;
                    disallowCameraGlobally = deviceOwner.disableCamera;
                } else {
                    return;
                }
            } else {
                ActiveAdmin profileOwner = getProfileOwnerAdminLocked(userId);
                userRestrictions = profileOwner != null ? profileOwner.userRestrictions : null;
            }
            this.mUserManagerInternal.setDevicePolicyUserRestrictions(userId, userRestrictions, isDeviceOwner, getCameraRestrictionScopeLocked(userId, disallowCameraGlobally));
        }
    }

    private int getCameraRestrictionScopeLocked(int userId, boolean disallowCameraGlobally) {
        if (disallowCameraGlobally) {
            return 2;
        }
        if (getCameraDisabled((ComponentName) null, userId, false)) {
            return 1;
        }
        return 0;
    }

    public Bundle getUserRestrictions(ComponentName who) {
        Bundle bundle;
        if (!this.mHasFeature) {
            return null;
        }
        Preconditions.checkNotNull(who, "ComponentName is null");
        synchronized (getLockObject()) {
            bundle = getActiveAdminForCallerLocked(who, -1).userRestrictions;
        }
        return bundle;
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    public boolean setApplicationHidden(ComponentName who, String callerPackage, String packageName, boolean hidden) {
        Injector injector;
        int callingUserId = UserHandle.getCallingUserId();
        boolean result = false;
        synchronized (getLockObject()) {
            enforceCanManageScope(who, callerPackage, -1, "delegation-package-access");
            long id = this.mInjector.binderClearCallingIdentity();
            try {
                result = this.mIPackageManager.setApplicationHiddenSettingAsUser(packageName, hidden, callingUserId);
                injector = this.mInjector;
            } catch (RemoteException re) {
                try {
                    Slog.e(LOG_TAG, "Failed to setApplicationHiddenSetting", re);
                    injector = this.mInjector;
                } catch (Throwable th) {
                    this.mInjector.binderRestoreCallingIdentity(id);
                    throw th;
                }
            }
            injector.binderRestoreCallingIdentity(id);
        }
        DevicePolicyEventLogger devicePolicyEventLogger = DevicePolicyEventLogger.createEvent(63).setAdmin(callerPackage).setBoolean(who == null);
        String[] strArr = new String[2];
        strArr[0] = packageName;
        strArr[1] = hidden ? "hidden" : "not_hidden";
        devicePolicyEventLogger.setStrings(strArr).write();
        return result;
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    /* JADX INFO: finally extract failed */
    public boolean isApplicationHidden(ComponentName who, String callerPackage, String packageName) {
        boolean applicationHiddenSettingAsUser;
        int callingUserId = UserHandle.getCallingUserId();
        synchronized (getLockObject()) {
            enforceCanManageScope(who, callerPackage, -1, "delegation-package-access");
            long id = this.mInjector.binderClearCallingIdentity();
            try {
                applicationHiddenSettingAsUser = this.mIPackageManager.getApplicationHiddenSettingAsUser(packageName, callingUserId);
                this.mInjector.binderRestoreCallingIdentity(id);
            } catch (RemoteException re) {
                try {
                    Slog.e(LOG_TAG, "Failed to getApplicationHiddenSettingAsUser", re);
                    this.mInjector.binderRestoreCallingIdentity(id);
                    return false;
                } catch (Throwable th) {
                    this.mInjector.binderRestoreCallingIdentity(id);
                    throw th;
                }
            }
        }
        return applicationHiddenSettingAsUser;
    }

    /* Debug info: failed to restart local var, previous not found, register: 12 */
    public void enableSystemApp(ComponentName who, String callerPackage, String packageName) {
        Injector injector;
        synchronized (getLockObject()) {
            enforceCanManageScope(who, callerPackage, -1, "delegation-enable-system-app");
            boolean isDemo = isCurrentUserDemo();
            int userId = UserHandle.getCallingUserId();
            long id = this.mInjector.binderClearCallingIdentity();
            try {
                int parentUserId = getProfileParentId(userId);
                if (!isDemo) {
                    if (!isSystemApp(this.mIPackageManager, packageName, parentUserId)) {
                        throw new IllegalArgumentException("Only system apps can be enabled this way.");
                    }
                }
                this.mIPackageManager.installExistingPackageAsUser(packageName, userId, DumpState.DUMP_CHANGES, 1, (List) null);
                if (isDemo) {
                    this.mIPackageManager.setApplicationEnabledSetting(packageName, 1, 1, userId, LOG_TAG);
                }
                injector = this.mInjector;
            } catch (RemoteException re) {
                try {
                    Slog.wtf(LOG_TAG, "Failed to install " + packageName, re);
                    injector = this.mInjector;
                } catch (Throwable th) {
                    this.mInjector.binderRestoreCallingIdentity(id);
                    throw th;
                }
            }
            injector.binderRestoreCallingIdentity(id);
        }
        DevicePolicyEventLogger.createEvent(64).setAdmin(callerPackage).setBoolean(who == null).setStrings(new String[]{packageName}).write();
    }

    /* Debug info: failed to restart local var, previous not found, register: 22 */
    public int enableSystemAppWithIntent(ComponentName who, String callerPackage, Intent intent) {
        ComponentName componentName = who;
        String str = callerPackage;
        Intent intent2 = intent;
        int numberOfAppsInstalled = 0;
        synchronized (getLockObject()) {
            enforceCanManageScope(componentName, str, -1, "delegation-enable-system-app");
            int userId = UserHandle.getCallingUserId();
            long id = this.mInjector.binderClearCallingIdentity();
            try {
                int parentUserId = getProfileParentId(userId);
                List<ResolveInfo> activitiesToEnable = this.mIPackageManager.queryIntentActivities(intent2, intent2.resolveTypeIfNeeded(this.mContext.getContentResolver()), 786432, parentUserId).getList();
                if (activitiesToEnable != null) {
                    for (ResolveInfo info : activitiesToEnable) {
                        if (info.activityInfo != null) {
                            String packageName = info.activityInfo.packageName;
                            if (isSystemApp(this.mIPackageManager, packageName, parentUserId)) {
                                numberOfAppsInstalled++;
                                String str2 = packageName;
                                ResolveInfo resolveInfo = info;
                                this.mIPackageManager.installExistingPackageAsUser(packageName, userId, DumpState.DUMP_CHANGES, 1, (List) null);
                            } else {
                                ResolveInfo resolveInfo2 = info;
                                Slog.d(LOG_TAG, "Not enabling " + packageName + " since is not a system app");
                            }
                        }
                    }
                }
                this.mInjector.binderRestoreCallingIdentity(id);
            } catch (RemoteException e) {
                try {
                    Slog.wtf(LOG_TAG, "Failed to resolve intent for: " + intent2);
                    return 0;
                } finally {
                    this.mInjector.binderRestoreCallingIdentity(id);
                }
            }
        }
        DevicePolicyEventLogger.createEvent(65).setAdmin(str).setBoolean(componentName == null).setStrings(new String[]{intent.getAction()}).write();
        return numberOfAppsInstalled;
    }

    private boolean isSystemApp(IPackageManager pm, String packageName, int userId) throws RemoteException {
        ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 8192, userId);
        if (appInfo != null) {
            return (appInfo.flags & 1) != 0;
        }
        throw new IllegalArgumentException("The application " + packageName + " is not present on this device");
    }

    /* Debug info: failed to restart local var, previous not found, register: 11 */
    public boolean installExistingPackage(ComponentName who, String callerPackage, String packageName) {
        boolean result;
        synchronized (getLockObject()) {
            enforceCanManageScope(who, callerPackage, -1, "delegation-install-existing-package");
            int callingUserId = this.mInjector.userHandleGetCallingUserId();
            if (isUserAffiliatedWithDeviceLocked(callingUserId)) {
                long id = this.mInjector.binderClearCallingIdentity();
                try {
                    result = this.mIPackageManager.installExistingPackageAsUser(packageName, callingUserId, DumpState.DUMP_CHANGES, 1, (List) null) == 1;
                    this.mInjector.binderRestoreCallingIdentity(id);
                } catch (RemoteException e) {
                    this.mInjector.binderRestoreCallingIdentity(id);
                    return false;
                } catch (Throwable th) {
                    this.mInjector.binderRestoreCallingIdentity(id);
                    throw th;
                }
            } else {
                throw new SecurityException("Admin " + who + " is neither the device owner or affiliated user's profile owner.");
            }
        }
        if (result) {
            DevicePolicyEventLogger.createEvent(66).setAdmin(callerPackage).setBoolean(who == null).setStrings(new String[]{packageName}).write();
        }
        return result;
    }

    public void setAccountManagementDisabled(ComponentName who, String accountType, boolean disabled) {
        if (this.mHasFeature) {
            Preconditions.checkNotNull(who, "ComponentName is null");
            synchronized (getLockObject()) {
                ActiveAdmin ap = getActiveAdminForCallerLocked(who, -1);
                if (disabled) {
                    ap.accountTypesWithManagementDisabled.add(accountType);
                } else {
                    ap.accountTypesWithManagementDisabled.remove(accountType);
                }
                saveSettingsLocked(UserHandle.getCallingUserId());
            }
        }
    }

    public String[] getAccountTypesWithManagementDisabled() {
        return getAccountTypesWithManagementDisabledAsUser(UserHandle.getCallingUserId());
    }

    public String[] getAccountTypesWithManagementDisabledAsUser(int userId) {
        String[] strArr;
        enforceFullCrossUsersPermission(userId);
        if (!this.mHasFeature) {
            return null;
        }
        synchronized (getLockObject()) {
            DevicePolicyData policy = getUserData(userId);
            int N = policy.mAdminList.size();
            ArraySet<String> resultSet = new ArraySet<>();
            for (int i = 0; i < N; i++) {
                resultSet.addAll(policy.mAdminList.get(i).accountTypesWithManagementDisabled);
            }
            strArr = (String[]) resultSet.toArray(new String[resultSet.size()]);
        }
        return strArr;
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    public void setUninstallBlocked(ComponentName who, String callerPackage, String packageName, boolean uninstallBlocked) {
        Injector injector;
        int userId = UserHandle.getCallingUserId();
        synchronized (getLockObject()) {
            enforceCanManageScope(who, callerPackage, -1, "delegation-block-uninstall");
            long id = this.mInjector.binderClearCallingIdentity();
            try {
                this.mIPackageManager.setBlockUninstallForUser(packageName, uninstallBlocked, userId);
                injector = this.mInjector;
            } catch (RemoteException re) {
                try {
                    Slog.e(LOG_TAG, "Failed to setBlockUninstallForUser", re);
                    injector = this.mInjector;
                } catch (Throwable th) {
                    this.mInjector.binderRestoreCallingIdentity(id);
                    throw th;
                }
            }
            injector.binderRestoreCallingIdentity(id);
        }
        DevicePolicyEventLogger.createEvent(67).setAdmin(callerPackage).setBoolean(who == null).setStrings(new String[]{packageName}).write();
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    public boolean isUninstallBlocked(ComponentName who, String packageName) {
        boolean blockUninstallForUser;
        int userId = UserHandle.getCallingUserId();
        synchronized (getLockObject()) {
            if (who != null) {
                getActiveAdminForCallerLocked(who, -1);
            }
            long id = this.mInjector.binderClearCallingIdentity();
            try {
                blockUninstallForUser = this.mIPackageManager.getBlockUninstallForUser(packageName, userId);
                this.mInjector.binderRestoreCallingIdentity(id);
            } catch (RemoteException re) {
                try {
                    Slog.e(LOG_TAG, "Failed to getBlockUninstallForUser", re);
                    return false;
                } finally {
                    this.mInjector.binderRestoreCallingIdentity(id);
                }
            }
        }
        return blockUninstallForUser;
    }

    public void setCrossProfileCallerIdDisabled(ComponentName who, boolean disabled) {
        if (this.mHasFeature) {
            Preconditions.checkNotNull(who, "ComponentName is null");
            synchronized (getLockObject()) {
                ActiveAdmin admin = getActiveAdminForCallerLocked(who, -1);
                if (admin.disableCallerId != disabled) {
                    admin.disableCallerId = disabled;
                    saveSettingsLocked(this.mInjector.userHandleGetCallingUserId());
                }
            }
            DevicePolicyEventLogger.createEvent(46).setAdmin(who).setBoolean(disabled).write();
        }
    }

    public boolean getCrossProfileCallerIdDisabled(ComponentName who) {
        boolean z;
        if (!this.mHasFeature) {
            return false;
        }
        Preconditions.checkNotNull(who, "ComponentName is null");
        synchronized (getLockObject()) {
            z = getActiveAdminForCallerLocked(who, -1).disableCallerId;
        }
        return z;
    }

    public boolean getCrossProfileCallerIdDisabledForUser(int userId) {
        boolean z;
        enforceCrossUsersPermission(userId);
        synchronized (getLockObject()) {
            ActiveAdmin admin = getProfileOwnerAdminLocked(userId);
            z = admin != null ? admin.disableCallerId : false;
        }
        return z;
    }

    public void setCrossProfileContactsSearchDisabled(ComponentName who, boolean disabled) {
        if (this.mHasFeature) {
            Preconditions.checkNotNull(who, "ComponentName is null");
            synchronized (getLockObject()) {
                ActiveAdmin admin = getActiveAdminForCallerLocked(who, -1);
                if (admin.disableContactsSearch != disabled) {
                    admin.disableContactsSearch = disabled;
                    saveSettingsLocked(this.mInjector.userHandleGetCallingUserId());
                }
            }
            DevicePolicyEventLogger.createEvent(45).setAdmin(who).setBoolean(disabled).write();
        }
    }

    public boolean getCrossProfileContactsSearchDisabled(ComponentName who) {
        boolean z;
        if (!this.mHasFeature) {
            return false;
        }
        Preconditions.checkNotNull(who, "ComponentName is null");
        synchronized (getLockObject()) {
            z = getActiveAdminForCallerLocked(who, -1).disableContactsSearch;
        }
        return z;
    }

    public boolean getCrossProfileContactsSearchDisabledForUser(int userId) {
        boolean z;
        enforceCrossUsersPermission(userId);
        synchronized (getLockObject()) {
            ActiveAdmin admin = getProfileOwnerAdminLocked(userId);
            z = admin != null ? admin.disableContactsSearch : false;
        }
        return z;
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    public void startManagedQuickContact(String actualLookupKey, long actualContactId, boolean isContactIdIgnored, long actualDirectoryId, Intent originalIntent) {
        Intent intent = ContactsContract.QuickContact.rebuildManagedQuickContactsIntent(actualLookupKey, actualContactId, isContactIdIgnored, actualDirectoryId, originalIntent);
        int callingUserId = UserHandle.getCallingUserId();
        long ident = this.mInjector.binderClearCallingIdentity();
        try {
            synchronized (getLockObject()) {
                int managedUserId = getManagedUserId(callingUserId);
                if (managedUserId < 0) {
                    this.mInjector.binderRestoreCallingIdentity(ident);
                } else if (isCrossProfileQuickContactDisabled(managedUserId)) {
                    this.mInjector.binderRestoreCallingIdentity(ident);
                } else {
                    ContactsInternal.startQuickContactWithErrorToastForUser(this.mContext, intent, new UserHandle(managedUserId));
                    this.mInjector.binderRestoreCallingIdentity(ident);
                }
            }
        } catch (Throwable th) {
            this.mInjector.binderRestoreCallingIdentity(ident);
            throw th;
        }
    }

    private boolean isCrossProfileQuickContactDisabled(int userId) {
        return getCrossProfileCallerIdDisabledForUser(userId) && getCrossProfileContactsSearchDisabledForUser(userId);
    }

    public int getManagedUserId(int callingUserId) {
        for (UserInfo ui : this.mUserManager.getProfiles(callingUserId)) {
            if (ui.id != callingUserId && ui.isManagedProfile()) {
                return ui.id;
            }
        }
        return -1;
    }

    public void setBluetoothContactSharingDisabled(ComponentName who, boolean disabled) {
        if (this.mHasFeature) {
            Preconditions.checkNotNull(who, "ComponentName is null");
            synchronized (getLockObject()) {
                ActiveAdmin admin = getActiveAdminForCallerLocked(who, -1);
                if (admin.disableBluetoothContactSharing != disabled) {
                    admin.disableBluetoothContactSharing = disabled;
                    saveSettingsLocked(UserHandle.getCallingUserId());
                }
            }
            DevicePolicyEventLogger.createEvent(47).setAdmin(who).setBoolean(disabled).write();
        }
    }

    public boolean getBluetoothContactSharingDisabled(ComponentName who) {
        boolean z;
        if (!this.mHasFeature) {
            return false;
        }
        Preconditions.checkNotNull(who, "ComponentName is null");
        synchronized (getLockObject()) {
            z = getActiveAdminForCallerLocked(who, -1).disableBluetoothContactSharing;
        }
        return z;
    }

    public boolean getBluetoothContactSharingDisabledForUser(int userId) {
        boolean z;
        synchronized (getLockObject()) {
            ActiveAdmin admin = getProfileOwnerAdminLocked(userId);
            z = admin != null ? admin.disableBluetoothContactSharing : false;
        }
        return z;
    }

    public void setLockTaskPackages(ComponentName who, String[] packages) throws SecurityException {
        Preconditions.checkNotNull(who, "ComponentName is null");
        Preconditions.checkNotNull(packages, "packages is null");
        synchronized (getLockObject()) {
            enforceCanCallLockTaskLocked(who);
            setLockTaskPackagesLocked(this.mInjector.userHandleGetCallingUserId(), new ArrayList(Arrays.asList(packages)));
        }
    }

    private void setLockTaskPackagesLocked(int userHandle, List<String> packages) {
        getUserData(userHandle).mLockTaskPackages = packages;
        saveSettingsLocked(userHandle);
        updateLockTaskPackagesLocked(packages, userHandle);
    }

    public String[] getLockTaskPackages(ComponentName who) {
        String[] strArr;
        Preconditions.checkNotNull(who, "ComponentName is null");
        int userHandle = this.mInjector.binderGetCallingUserHandle().getIdentifier();
        synchronized (getLockObject()) {
            enforceCanCallLockTaskLocked(who);
            List<String> packages = getUserData(userHandle).mLockTaskPackages;
            strArr = (String[]) packages.toArray(new String[packages.size()]);
        }
        return strArr;
    }

    public boolean isLockTaskPermitted(String pkg) {
        boolean contains;
        int userHandle = this.mInjector.userHandleGetCallingUserId();
        synchronized (getLockObject()) {
            contains = getUserData(userHandle).mLockTaskPackages.contains(pkg);
        }
        return contains;
    }

    public void setLockTaskFeatures(ComponentName who, int flags) {
        Preconditions.checkNotNull(who, "ComponentName is null");
        boolean z = true;
        boolean hasHome = (flags & 4) != 0;
        Preconditions.checkArgument(hasHome || !((flags & 8) != 0), "Cannot use LOCK_TASK_FEATURE_OVERVIEW without LOCK_TASK_FEATURE_HOME");
        boolean hasNotification = (flags & 2) != 0;
        if (!hasHome && hasNotification) {
            z = false;
        }
        Preconditions.checkArgument(z, "Cannot use LOCK_TASK_FEATURE_NOTIFICATIONS without LOCK_TASK_FEATURE_HOME");
        int userHandle = this.mInjector.userHandleGetCallingUserId();
        synchronized (getLockObject()) {
            enforceCanCallLockTaskLocked(who);
            setLockTaskFeaturesLocked(userHandle, flags);
        }
    }

    private void setLockTaskFeaturesLocked(int userHandle, int flags) {
        getUserData(userHandle).mLockTaskFeatures = flags;
        saveSettingsLocked(userHandle);
        updateLockTaskFeaturesLocked(flags, userHandle);
    }

    public int getLockTaskFeatures(ComponentName who) {
        int i;
        Preconditions.checkNotNull(who, "ComponentName is null");
        int userHandle = this.mInjector.userHandleGetCallingUserId();
        synchronized (getLockObject()) {
            enforceCanCallLockTaskLocked(who);
            i = getUserData(userHandle).mLockTaskFeatures;
        }
        return i;
    }

    private void maybeClearLockTaskPolicyLocked() {
        long ident = this.mInjector.binderClearCallingIdentity();
        try {
            List<UserInfo> userInfos = this.mUserManager.getUsers(true);
            for (int i = userInfos.size() - 1; i >= 0; i--) {
                int userId = userInfos.get(i).id;
                if (!canUserUseLockTaskLocked(userId)) {
                    if (!getUserData(userId).mLockTaskPackages.isEmpty()) {
                        Slog.d(LOG_TAG, "User id " + userId + " not affiliated. Clearing lock task packages");
                        setLockTaskPackagesLocked(userId, Collections.emptyList());
                    }
                    if (getUserData(userId).mLockTaskFeatures != 0) {
                        Slog.d(LOG_TAG, "User id " + userId + " not affiliated. Clearing lock task features");
                        setLockTaskFeaturesLocked(userId, 0);
                    }
                }
            }
        } finally {
            this.mInjector.binderRestoreCallingIdentity(ident);
        }
    }

    public void notifyLockTaskModeChanged(boolean isEnabled, String pkg, int userHandle) {
        if (isCallerWithSystemUid()) {
            synchronized (getLockObject()) {
                DevicePolicyData policy = getUserData(userHandle);
                if (policy.mStatusBarDisabled) {
                    setStatusBarDisabledInternal(!isEnabled, userHandle);
                }
                Bundle adminExtras = new Bundle();
                adminExtras.putString("android.app.extra.LOCK_TASK_PACKAGE", pkg);
                Iterator<ActiveAdmin> it = policy.mAdminList.iterator();
                while (it.hasNext()) {
                    ActiveAdmin admin = it.next();
                    boolean ownsDevice = isDeviceOwner(admin.info.getComponent(), userHandle);
                    boolean ownsProfile = isProfileOwner(admin.info.getComponent(), userHandle);
                    if (ownsDevice || ownsProfile) {
                        if (isEnabled) {
                            sendAdminCommandLocked(admin, "android.app.action.LOCK_TASK_ENTERING", adminExtras, (BroadcastReceiver) null);
                        } else {
                            sendAdminCommandLocked(admin, "android.app.action.LOCK_TASK_EXITING");
                        }
                        DevicePolicyEventLogger.createEvent(51).setAdmin(admin.info.getPackageName()).setBoolean(isEnabled).setStrings(new String[]{pkg}).write();
                    }
                }
            }
            return;
        }
        throw new SecurityException("notifyLockTaskModeChanged can only be called by system");
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    public void setGlobalSetting(ComponentName who, String setting, String value) {
        Preconditions.checkNotNull(who, "ComponentName is null");
        synchronized (getLockObject()) {
            getActiveAdminForCallerLocked(who, -2);
            if (GLOBAL_SETTINGS_DEPRECATED.contains(setting)) {
                Log.i(LOG_TAG, "Global setting no longer supported: " + setting);
                return;
            }
            if (!GLOBAL_SETTINGS_WHITELIST.contains(setting)) {
                if (!UserManager.isDeviceInDemoMode(this.mContext)) {
                    throw new SecurityException(String.format("Permission denial: device owners cannot update %1$s", new Object[]{setting}));
                }
            }
            if ("stay_on_while_plugged_in".equals(setting)) {
                long timeMs = getMaximumTimeToLock(who, this.mInjector.userHandleGetCallingUserId(), false);
                if (timeMs > 0 && timeMs < JobStatus.NO_LATEST_RUNTIME) {
                    return;
                }
            }
            long id = this.mInjector.binderClearCallingIdentity();
            try {
                this.mInjector.settingsGlobalPutString(setting, value);
            } finally {
                this.mInjector.binderRestoreCallingIdentity(id);
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    public void setSystemSetting(ComponentName who, String setting, String value) {
        Preconditions.checkNotNull(who, "ComponentName is null");
        Preconditions.checkStringNotEmpty(setting, "String setting is null or empty");
        synchronized (getLockObject()) {
            getActiveAdminForCallerLocked(who, -1);
            if (SYSTEM_SETTINGS_WHITELIST.contains(setting)) {
                this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable(setting, value, this.mInjector.userHandleGetCallingUserId()) {
                    private final /* synthetic */ String f$1;
                    private final /* synthetic */ String f$2;
                    private final /* synthetic */ int f$3;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                    }

                    public final void runOrThrow() {
                        DevicePolicyManagerService.this.lambda$setSystemSetting$10$DevicePolicyManagerService(this.f$1, this.f$2, this.f$3);
                    }
                });
            } else {
                throw new SecurityException(String.format("Permission denial: device owners cannot update %1$s", new Object[]{setting}));
            }
        }
    }

    public /* synthetic */ void lambda$setSystemSetting$10$DevicePolicyManagerService(String setting, String value, int callingUserId) throws Exception {
        this.mInjector.settingsSystemPutStringForUser(setting, value, callingUserId);
    }

    public boolean setTime(ComponentName who, long millis) {
        Preconditions.checkNotNull(who, "ComponentName is null in setTime");
        enforceDeviceOwner(who);
        if (this.mInjector.settingsGlobalGetInt("auto_time", 0) == 1) {
            return false;
        }
        this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable(millis) {
            private final /* synthetic */ long f$1;

            {
                this.f$1 = r2;
            }

            public final void runOrThrow() {
                DevicePolicyManagerService.this.lambda$setTime$11$DevicePolicyManagerService(this.f$1);
            }
        });
        return true;
    }

    public /* synthetic */ void lambda$setTime$11$DevicePolicyManagerService(long millis) throws Exception {
        this.mInjector.getAlarmManager().setTime(millis);
    }

    public boolean setTimeZone(ComponentName who, String timeZone) {
        Preconditions.checkNotNull(who, "ComponentName is null in setTimeZone");
        enforceDeviceOwner(who);
        if (this.mInjector.settingsGlobalGetInt("auto_time_zone", 0) == 1) {
            return false;
        }
        this.mInjector.binderWithCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable(timeZone) {
            private final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void runOrThrow() {
                DevicePolicyManagerService.this.lambda$setTimeZone$12$DevicePolicyManagerService(this.f$1);
            }
        });
        return true;
    }

    public /* synthetic */ void lambda$setTimeZone$12$DevicePolicyManagerService(String timeZone) throws Exception {
        this.mInjector.getAlarmManager().setTimeZone(timeZone);
    }

    /* Debug info: failed to restart local var, previous not found, register: 10 */
    /* JADX WARNING: Code restructure failed: missing block: B:37:?, code lost:
        android.util.Slog.e(LOG_TAG, "Invalid value: " + r13 + " for setting " + r12);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x0149, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:?, code lost:
        r10.mInjector.binderRestoreCallingIdentity(r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x014f, code lost:
        throw r2;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:28:0x00af, B:44:0x0104] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setSecureSetting(android.content.ComponentName r11, java.lang.String r12, java.lang.String r13) {
        /*
            r10 = this;
            java.lang.String r0 = "ComponentName is null"
            com.android.internal.util.Preconditions.checkNotNull(r11, r0)
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r0 = r10.mInjector
            int r0 = r0.userHandleGetCallingUserId()
            java.lang.Object r1 = r10.getLockObject()
            monitor-enter(r1)
            r2 = -1
            r10.getActiveAdminForCallerLocked(r11, r2)     // Catch:{ all -> 0x0150 }
            boolean r2 = r10.isDeviceOwner(r11, r0)     // Catch:{ all -> 0x0150 }
            r3 = 0
            r4 = 1
            if (r2 == 0) goto L_0x003b
            java.util.Set<java.lang.String> r2 = SECURE_SETTINGS_DEVICEOWNER_WHITELIST     // Catch:{ all -> 0x0150 }
            boolean r2 = r2.contains(r12)     // Catch:{ all -> 0x0150 }
            if (r2 != 0) goto L_0x005a
            boolean r2 = r10.isCurrentUserDemo()     // Catch:{ all -> 0x0150 }
            if (r2 == 0) goto L_0x002b
            goto L_0x005a
        L_0x002b:
            java.lang.SecurityException r2 = new java.lang.SecurityException     // Catch:{ all -> 0x0150 }
            java.lang.String r5 = "Permission denial: Device owners cannot update %1$s"
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0150 }
            r4[r3] = r12     // Catch:{ all -> 0x0150 }
            java.lang.String r3 = java.lang.String.format(r5, r4)     // Catch:{ all -> 0x0150 }
            r2.<init>(r3)     // Catch:{ all -> 0x0150 }
            throw r2     // Catch:{ all -> 0x0150 }
        L_0x003b:
            java.util.Set<java.lang.String> r2 = SECURE_SETTINGS_WHITELIST     // Catch:{ all -> 0x0150 }
            boolean r2 = r2.contains(r12)     // Catch:{ all -> 0x0150 }
            if (r2 != 0) goto L_0x005a
            boolean r2 = r10.isCurrentUserDemo()     // Catch:{ all -> 0x0150 }
            if (r2 == 0) goto L_0x004a
            goto L_0x005a
        L_0x004a:
            java.lang.SecurityException r2 = new java.lang.SecurityException     // Catch:{ all -> 0x0150 }
            java.lang.String r5 = "Permission denial: Profile owners cannot update %1$s"
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0150 }
            r4[r3] = r12     // Catch:{ all -> 0x0150 }
            java.lang.String r3 = java.lang.String.format(r5, r4)     // Catch:{ all -> 0x0150 }
            r2.<init>(r3)     // Catch:{ all -> 0x0150 }
            throw r2     // Catch:{ all -> 0x0150 }
        L_0x005a:
            java.lang.String r2 = "install_non_market_apps"
            boolean r2 = r12.equals(r2)     // Catch:{ all -> 0x0150 }
            r5 = 2
            r6 = 14
            if (r2 == 0) goto L_0x00fc
            java.lang.String r2 = r11.getPackageName()     // Catch:{ all -> 0x0150 }
            int r2 = r10.getTargetSdk(r2, r0)     // Catch:{ all -> 0x0150 }
            r7 = 26
            if (r2 >= r7) goto L_0x00f3
            android.os.UserManager r2 = r10.mUserManager     // Catch:{ all -> 0x0150 }
            boolean r2 = r2.isManagedProfile(r0)     // Catch:{ all -> 0x0150 }
            if (r2 != 0) goto L_0x00ac
            java.lang.String r2 = "DevicePolicyManager"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0150 }
            r3.<init>()     // Catch:{ all -> 0x0150 }
            java.lang.String r4 = "Ignoring setSecureSetting request for "
            r3.append(r4)     // Catch:{ all -> 0x0150 }
            r3.append(r12)     // Catch:{ all -> 0x0150 }
            java.lang.String r4 = ". User restriction "
            r3.append(r4)     // Catch:{ all -> 0x0150 }
            java.lang.String r4 = "no_install_unknown_sources"
            r3.append(r4)     // Catch:{ all -> 0x0150 }
            java.lang.String r4 = " or "
            r3.append(r4)     // Catch:{ all -> 0x0150 }
            java.lang.String r4 = "no_install_unknown_sources_globally"
            r3.append(r4)     // Catch:{ all -> 0x0150 }
            java.lang.String r4 = " should be used instead."
            r3.append(r4)     // Catch:{ all -> 0x0150 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0150 }
            android.util.Slog.e(r2, r3)     // Catch:{ all -> 0x0150 }
            goto L_0x00f1
        L_0x00ac:
            java.lang.String r2 = "no_install_unknown_sources"
            int r7 = java.lang.Integer.parseInt(r13)     // Catch:{ NumberFormatException -> 0x00d2 }
            if (r7 != 0) goto L_0x00b7
            r7 = r4
            goto L_0x00b8
        L_0x00b7:
            r7 = r3
        L_0x00b8:
            r10.setUserRestriction(r11, r2, r7)     // Catch:{ NumberFormatException -> 0x00d2 }
            android.app.admin.DevicePolicyEventLogger r2 = android.app.admin.DevicePolicyEventLogger.createEvent(r6)     // Catch:{ NumberFormatException -> 0x00d2 }
            android.app.admin.DevicePolicyEventLogger r2 = r2.setAdmin(r11)     // Catch:{ NumberFormatException -> 0x00d2 }
            java.lang.String[] r5 = new java.lang.String[r5]     // Catch:{ NumberFormatException -> 0x00d2 }
            r5[r3] = r12     // Catch:{ NumberFormatException -> 0x00d2 }
            r5[r4] = r13     // Catch:{ NumberFormatException -> 0x00d2 }
            android.app.admin.DevicePolicyEventLogger r2 = r2.setStrings(r5)     // Catch:{ NumberFormatException -> 0x00d2 }
            r2.write()     // Catch:{ NumberFormatException -> 0x00d2 }
            goto L_0x00f1
        L_0x00d2:
            r2 = move-exception
            java.lang.String r3 = "DevicePolicyManager"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0150 }
            r4.<init>()     // Catch:{ all -> 0x0150 }
            java.lang.String r5 = "Invalid value: "
            r4.append(r5)     // Catch:{ all -> 0x0150 }
            r4.append(r13)     // Catch:{ all -> 0x0150 }
            java.lang.String r5 = " for setting "
            r4.append(r5)     // Catch:{ all -> 0x0150 }
            r4.append(r12)     // Catch:{ all -> 0x0150 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0150 }
            android.util.Slog.e(r3, r4)     // Catch:{ all -> 0x0150 }
        L_0x00f1:
            monitor-exit(r1)     // Catch:{ all -> 0x0150 }
            return
        L_0x00f3:
            java.lang.UnsupportedOperationException r2 = new java.lang.UnsupportedOperationException     // Catch:{ all -> 0x0150 }
            java.lang.String r3 = "install_non_market_apps is deprecated. Please use one of the user restrictions no_install_unknown_sources or no_install_unknown_sources_globally instead."
            r2.<init>(r3)     // Catch:{ all -> 0x0150 }
            throw r2     // Catch:{ all -> 0x0150 }
        L_0x00fc:
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r2 = r10.mInjector     // Catch:{ all -> 0x0150 }
            long r7 = r2.binderClearCallingIdentity()     // Catch:{ all -> 0x0150 }
            java.lang.String r2 = "default_input_method"
            boolean r2 = r2.equals(r12)     // Catch:{ all -> 0x0149 }
            if (r2 == 0) goto L_0x0126
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r2 = r10.mInjector     // Catch:{ all -> 0x0149 }
            java.lang.String r9 = "default_input_method"
            java.lang.String r2 = r2.settingsSecureGetStringForUser(r9, r0)     // Catch:{ all -> 0x0149 }
            boolean r9 = android.text.TextUtils.equals(r2, r13)     // Catch:{ all -> 0x0149 }
            if (r9 != 0) goto L_0x011d
            com.android.server.devicepolicy.DevicePolicyManagerService$SetupContentObserver r9 = r10.mSetupContentObserver     // Catch:{ all -> 0x0149 }
            r9.addPendingChangeByOwnerLocked(r0)     // Catch:{ all -> 0x0149 }
        L_0x011d:
            com.android.server.devicepolicy.DevicePolicyManagerService$DevicePolicyData r9 = r10.getUserData(r0)     // Catch:{ all -> 0x0149 }
            r9.mCurrentInputMethodSet = r4     // Catch:{ all -> 0x0149 }
            r10.saveSettingsLocked(r0)     // Catch:{ all -> 0x0149 }
        L_0x0126:
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r2 = r10.mInjector     // Catch:{ all -> 0x0149 }
            r2.settingsSecurePutStringForUser(r12, r13, r0)     // Catch:{ all -> 0x0149 }
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r2 = r10.mInjector     // Catch:{ all -> 0x0150 }
            r2.binderRestoreCallingIdentity(r7)     // Catch:{ all -> 0x0150 }
            monitor-exit(r1)     // Catch:{ all -> 0x0150 }
            android.app.admin.DevicePolicyEventLogger r1 = android.app.admin.DevicePolicyEventLogger.createEvent(r6)
            android.app.admin.DevicePolicyEventLogger r1 = r1.setAdmin(r11)
            java.lang.String[] r2 = new java.lang.String[r5]
            r2[r3] = r12
            r2[r4] = r13
            android.app.admin.DevicePolicyEventLogger r1 = r1.setStrings(r2)
            r1.write()
            return
        L_0x0149:
            r2 = move-exception
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r3 = r10.mInjector     // Catch:{ all -> 0x0150 }
            r3.binderRestoreCallingIdentity(r7)     // Catch:{ all -> 0x0150 }
            throw r2     // Catch:{ all -> 0x0150 }
        L_0x0150:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0150 }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.DevicePolicyManagerService.setSecureSetting(android.content.ComponentName, java.lang.String, java.lang.String):void");
    }

    public void setMasterVolumeMuted(ComponentName who, boolean on) {
        Preconditions.checkNotNull(who, "ComponentName is null");
        synchronized (getLockObject()) {
            getActiveAdminForCallerLocked(who, -1);
            setUserRestriction(who, "disallow_unmute_device", on);
            DevicePolicyEventLogger.createEvent(35).setAdmin(who).setBoolean(on).write();
        }
    }

    public boolean isMasterVolumeMuted(ComponentName who) {
        boolean isMasterMute;
        Preconditions.checkNotNull(who, "ComponentName is null");
        synchronized (getLockObject()) {
            getActiveAdminForCallerLocked(who, -1);
            isMasterMute = ((AudioManager) this.mContext.getSystemService("audio")).isMasterMute();
        }
        return isMasterMute;
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    public void setUserIcon(ComponentName who, Bitmap icon) {
        synchronized (getLockObject()) {
            Preconditions.checkNotNull(who, "ComponentName is null");
            getActiveAdminForCallerLocked(who, -1);
            int userId = UserHandle.getCallingUserId();
            long id = this.mInjector.binderClearCallingIdentity();
            try {
                this.mUserManagerInternal.setUserIcon(userId, icon);
            } finally {
                this.mInjector.binderRestoreCallingIdentity(id);
            }
        }
        DevicePolicyEventLogger.createEvent(41).setAdmin(who).write();
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    public boolean setKeyguardDisabled(ComponentName who, boolean disabled) {
        Preconditions.checkNotNull(who, "ComponentName is null");
        int userId = this.mInjector.userHandleGetCallingUserId();
        synchronized (getLockObject()) {
            getActiveAdminForCallerLocked(who, -1);
            if (!isUserAffiliatedWithDeviceLocked(userId)) {
                throw new SecurityException("Admin " + who + " is neither the device owner or affiliated user's profile owner.");
            }
        }
        if (!isManagedProfile(userId)) {
            long ident = this.mInjector.binderClearCallingIdentity();
            if (disabled) {
                try {
                    if (this.mLockPatternUtils.isSecure(userId)) {
                        this.mInjector.binderRestoreCallingIdentity(ident);
                        return false;
                    }
                } catch (RemoteException e) {
                } catch (Throwable th) {
                    this.mInjector.binderRestoreCallingIdentity(ident);
                    throw th;
                }
            }
            this.mLockPatternUtils.setLockScreenDisabled(disabled, userId);
            if (disabled) {
                this.mInjector.getIWindowManager().dismissKeyguard((IKeyguardDismissCallback) null, (CharSequence) null);
            }
            DevicePolicyEventLogger.createEvent(37).setAdmin(who).setBoolean(disabled).write();
            this.mInjector.binderRestoreCallingIdentity(ident);
            return true;
        }
        throw new SecurityException("Managed profile cannot disable keyguard");
    }

    /* Debug info: failed to restart local var, previous not found, register: 9 */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x004d, code lost:
        android.app.admin.DevicePolicyEventLogger.createEvent(38).setAdmin(r10).setBoolean(r11).write();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x005e, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean setStatusBarDisabled(android.content.ComponentName r10, boolean r11) {
        /*
            r9 = this;
            int r0 = android.os.UserHandle.getCallingUserId()
            java.lang.Object r1 = r9.getLockObject()
            monitor-enter(r1)
            r2 = -1
            r9.getActiveAdminForCallerLocked(r10, r2)     // Catch:{ all -> 0x0083 }
            boolean r2 = r9.isUserAffiliatedWithDeviceLocked(r0)     // Catch:{ all -> 0x0083 }
            if (r2 == 0) goto L_0x0067
            boolean r2 = r9.isManagedProfile((int) r0)     // Catch:{ all -> 0x0083 }
            if (r2 != 0) goto L_0x005f
            com.android.server.devicepolicy.DevicePolicyManagerService$DevicePolicyData r2 = r9.getUserData(r0)     // Catch:{ all -> 0x0083 }
            boolean r3 = r2.mStatusBarDisabled     // Catch:{ all -> 0x0083 }
            r4 = 1
            if (r3 == r11) goto L_0x004c
            r3 = 0
            r5 = 0
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r6 = r9.mInjector     // Catch:{ RemoteException -> 0x0035 }
            android.app.IActivityTaskManager r6 = r6.getIActivityTaskManager()     // Catch:{ RemoteException -> 0x0035 }
            int r6 = r6.getLockTaskModeState()     // Catch:{ RemoteException -> 0x0035 }
            if (r6 == 0) goto L_0x0032
            r6 = r4
            goto L_0x0033
        L_0x0032:
            r6 = r5
        L_0x0033:
            r3 = r6
            goto L_0x003d
        L_0x0035:
            r6 = move-exception
            java.lang.String r7 = "DevicePolicyManager"
            java.lang.String r8 = "Failed to get LockTask mode"
            android.util.Slog.e(r7, r8)     // Catch:{ all -> 0x0083 }
        L_0x003d:
            if (r3 != 0) goto L_0x0047
            boolean r6 = r9.setStatusBarDisabledInternal(r11, r0)     // Catch:{ all -> 0x0083 }
            if (r6 != 0) goto L_0x0047
            monitor-exit(r1)     // Catch:{ all -> 0x0083 }
            return r5
        L_0x0047:
            r2.mStatusBarDisabled = r11     // Catch:{ all -> 0x0083 }
            r9.saveSettingsLocked(r0)     // Catch:{ all -> 0x0083 }
        L_0x004c:
            monitor-exit(r1)     // Catch:{ all -> 0x0083 }
            r1 = 38
            android.app.admin.DevicePolicyEventLogger r1 = android.app.admin.DevicePolicyEventLogger.createEvent(r1)
            android.app.admin.DevicePolicyEventLogger r1 = r1.setAdmin(r10)
            android.app.admin.DevicePolicyEventLogger r1 = r1.setBoolean(r11)
            r1.write()
            return r4
        L_0x005f:
            java.lang.SecurityException r2 = new java.lang.SecurityException     // Catch:{ all -> 0x0083 }
            java.lang.String r3 = "Managed profile cannot disable status bar"
            r2.<init>(r3)     // Catch:{ all -> 0x0083 }
            throw r2     // Catch:{ all -> 0x0083 }
        L_0x0067:
            java.lang.SecurityException r2 = new java.lang.SecurityException     // Catch:{ all -> 0x0083 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0083 }
            r3.<init>()     // Catch:{ all -> 0x0083 }
            java.lang.String r4 = "Admin "
            r3.append(r4)     // Catch:{ all -> 0x0083 }
            r3.append(r10)     // Catch:{ all -> 0x0083 }
            java.lang.String r4 = " is neither the device owner or affiliated user's profile owner."
            r3.append(r4)     // Catch:{ all -> 0x0083 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0083 }
            r2.<init>(r3)     // Catch:{ all -> 0x0083 }
            throw r2     // Catch:{ all -> 0x0083 }
        L_0x0083:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0083 }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.DevicePolicyManagerService.setStatusBarDisabled(android.content.ComponentName, boolean):boolean");
    }

    private boolean setStatusBarDisabledInternal(boolean disabled, int userId) {
        long ident = this.mInjector.binderClearCallingIdentity();
        try {
            IStatusBarService statusBarService = IStatusBarService.Stub.asInterface(ServiceManager.checkService(TAG_STATUS_BAR));
            if (statusBarService != null) {
                int flags1 = disabled ? STATUS_BAR_DISABLE_MASK : 0;
                int flags2 = disabled ? 1 : 0;
                statusBarService.disableForUser(flags1, this.mToken, this.mContext.getPackageName(), userId);
                statusBarService.disable2ForUser(flags2, this.mToken, this.mContext.getPackageName(), userId);
                this.mInjector.binderRestoreCallingIdentity(ident);
                return true;
            }
        } catch (RemoteException e) {
            Slog.e(LOG_TAG, "Failed to disable the status bar", e);
        } catch (Throwable th) {
            this.mInjector.binderRestoreCallingIdentity(ident);
            throw th;
        }
        this.mInjector.binderRestoreCallingIdentity(ident);
        return false;
    }

    /* access modifiers changed from: package-private */
    public void updateUserSetupCompleteAndPaired() {
        List<UserInfo> users = this.mUserManager.getUsers(true);
        int N = users.size();
        for (int i = 0; i < N; i++) {
            int userHandle = users.get(i).id;
            if (this.mInjector.settingsSecureGetIntForUser("user_setup_complete", 0, userHandle) != 0) {
                DevicePolicyData policy = getUserData(userHandle);
                if (!policy.mUserSetupComplete) {
                    policy.mUserSetupComplete = true;
                    synchronized (getLockObject()) {
                        saveSettingsLocked(userHandle);
                    }
                }
            }
            if (this.mIsWatch && this.mInjector.settingsSecureGetIntForUser("device_paired", 0, userHandle) != 0) {
                DevicePolicyData policy2 = getUserData(userHandle);
                if (!policy2.mPaired) {
                    policy2.mPaired = true;
                    synchronized (getLockObject()) {
                        saveSettingsLocked(userHandle);
                    }
                } else {
                    continue;
                }
            }
        }
    }

    private class SetupContentObserver extends ContentObserver {
        private final Uri mDefaultImeChanged = Settings.Secure.getUriFor("default_input_method");
        private final Uri mDeviceProvisioned = Settings.Global.getUriFor("device_provisioned");
        private final Uri mPaired = Settings.Secure.getUriFor("device_paired");
        @GuardedBy({"getLockObject()"})
        private Set<Integer> mUserIdsWithPendingChangesByOwner = new ArraySet();
        private final Uri mUserSetupComplete = Settings.Secure.getUriFor("user_setup_complete");

        public SetupContentObserver(Handler handler) {
            super(handler);
        }

        /* access modifiers changed from: package-private */
        public void register() {
            DevicePolicyManagerService.this.mInjector.registerContentObserver(this.mUserSetupComplete, false, this, -1);
            DevicePolicyManagerService.this.mInjector.registerContentObserver(this.mDeviceProvisioned, false, this, -1);
            if (DevicePolicyManagerService.this.mIsWatch) {
                DevicePolicyManagerService.this.mInjector.registerContentObserver(this.mPaired, false, this, -1);
            }
            DevicePolicyManagerService.this.mInjector.registerContentObserver(this.mDefaultImeChanged, false, this, -1);
        }

        /* access modifiers changed from: private */
        @GuardedBy({"getLockObject()"})
        public void addPendingChangeByOwnerLocked(int userId) {
            this.mUserIdsWithPendingChangesByOwner.add(Integer.valueOf(userId));
        }

        public void onChange(boolean selfChange, Uri uri, int userId) {
            if (this.mUserSetupComplete.equals(uri) || (DevicePolicyManagerService.this.mIsWatch && this.mPaired.equals(uri))) {
                DevicePolicyManagerService.this.updateUserSetupCompleteAndPaired();
            } else if (this.mDeviceProvisioned.equals(uri)) {
                synchronized (DevicePolicyManagerService.this.getLockObject()) {
                    DevicePolicyManagerService.this.setDeviceOwnerSystemPropertyLocked();
                }
            } else if (this.mDefaultImeChanged.equals(uri)) {
                synchronized (DevicePolicyManagerService.this.getLockObject()) {
                    if (this.mUserIdsWithPendingChangesByOwner.contains(Integer.valueOf(userId))) {
                        this.mUserIdsWithPendingChangesByOwner.remove(Integer.valueOf(userId));
                    } else {
                        DevicePolicyManagerService.this.getUserData(userId).mCurrentInputMethodSet = false;
                        DevicePolicyManagerService.this.saveSettingsLocked(userId);
                    }
                }
            }
        }
    }

    private class DevicePolicyConstantsObserver extends ContentObserver {
        final Uri mConstantsUri = Settings.Global.getUriFor("device_policy_constants");

        DevicePolicyConstantsObserver(Handler handler) {
            super(handler);
        }

        /* access modifiers changed from: package-private */
        public void register() {
            DevicePolicyManagerService.this.mInjector.registerContentObserver(this.mConstantsUri, false, this, -1);
        }

        public void onChange(boolean selfChange, Uri uri, int userId) {
            DevicePolicyManagerService devicePolicyManagerService = DevicePolicyManagerService.this;
            DevicePolicyConstants unused = devicePolicyManagerService.mConstants = devicePolicyManagerService.loadConstants();
        }
    }

    @VisibleForTesting
    final class LocalService extends DevicePolicyManagerInternal {
        private List<DevicePolicyManagerInternal.OnCrossProfileWidgetProvidersChangeListener> mWidgetProviderListeners;

        LocalService() {
        }

        public List<String> getCrossProfileWidgetProviders(int profileId) {
            synchronized (DevicePolicyManagerService.this.getLockObject()) {
                if (DevicePolicyManagerService.this.mOwners == null) {
                    List<String> emptyList = Collections.emptyList();
                    return emptyList;
                }
                ComponentName ownerComponent = DevicePolicyManagerService.this.mOwners.getProfileOwnerComponent(profileId);
                if (ownerComponent == null) {
                    List<String> emptyList2 = Collections.emptyList();
                    return emptyList2;
                }
                ActiveAdmin admin = DevicePolicyManagerService.this.getUserDataUnchecked(profileId).mAdminMap.get(ownerComponent);
                if (!(admin == null || admin.crossProfileWidgetProviders == null)) {
                    if (!admin.crossProfileWidgetProviders.isEmpty()) {
                        List<String> list = admin.crossProfileWidgetProviders;
                        return list;
                    }
                }
                List<String> emptyList3 = Collections.emptyList();
                return emptyList3;
            }
        }

        public void addOnCrossProfileWidgetProvidersChangeListener(DevicePolicyManagerInternal.OnCrossProfileWidgetProvidersChangeListener listener) {
            synchronized (DevicePolicyManagerService.this.getLockObject()) {
                if (this.mWidgetProviderListeners == null) {
                    this.mWidgetProviderListeners = new ArrayList();
                }
                if (!this.mWidgetProviderListeners.contains(listener)) {
                    this.mWidgetProviderListeners.add(listener);
                }
            }
        }

        public boolean isActiveAdminWithPolicy(int uid, int reqPolicy) {
            boolean z;
            synchronized (DevicePolicyManagerService.this.getLockObject()) {
                z = DevicePolicyManagerService.this.getActiveAdminWithPolicyForUidLocked((ComponentName) null, reqPolicy, uid) != null;
            }
            return z;
        }

        /* access modifiers changed from: private */
        public void notifyCrossProfileProvidersChanged(int userId, List<String> packages) {
            List<DevicePolicyManagerInternal.OnCrossProfileWidgetProvidersChangeListener> listeners;
            synchronized (DevicePolicyManagerService.this.getLockObject()) {
                listeners = new ArrayList<>(this.mWidgetProviderListeners);
            }
            int listenerCount = listeners.size();
            for (int i = 0; i < listenerCount; i++) {
                listeners.get(i).onCrossProfileWidgetProvidersChanged(userId, packages);
            }
        }

        public Intent createShowAdminSupportIntent(int userId, boolean useDefaultIfNoAdmin) {
            ComponentName profileOwner = DevicePolicyManagerService.this.mOwners.getProfileOwnerComponent(userId);
            if (profileOwner != null) {
                return DevicePolicyManagerService.this.createShowAdminSupportIntent(profileOwner, userId);
            }
            Pair<Integer, ComponentName> deviceOwner = DevicePolicyManagerService.this.mOwners.getDeviceOwnerUserIdAndComponent();
            if (deviceOwner != null && ((Integer) deviceOwner.first).intValue() == userId) {
                return DevicePolicyManagerService.this.createShowAdminSupportIntent((ComponentName) deviceOwner.second, userId);
            }
            if (useDefaultIfNoAdmin) {
                return DevicePolicyManagerService.this.createShowAdminSupportIntent((ComponentName) null, userId);
            }
            return null;
        }

        public Intent createUserRestrictionSupportIntent(int userId, String userRestriction) {
            long ident = DevicePolicyManagerService.this.mInjector.binderClearCallingIdentity();
            try {
                List<UserManager.EnforcingUser> sources = DevicePolicyManagerService.this.mUserManager.getUserRestrictionSources(userRestriction, UserHandle.of(userId));
                if (sources != null) {
                    if (!sources.isEmpty()) {
                        if (sources.size() > 1) {
                            Intent access$2900 = DevicePolicyManagerService.this.createShowAdminSupportIntent((ComponentName) null, userId);
                            DevicePolicyManagerService.this.mInjector.binderRestoreCallingIdentity(ident);
                            return access$2900;
                        }
                        UserManager.EnforcingUser enforcingUser = sources.get(0);
                        int sourceType = enforcingUser.getUserRestrictionSource();
                        int enforcingUserId = enforcingUser.getUserHandle().getIdentifier();
                        if (sourceType == 4) {
                            ComponentName profileOwner = DevicePolicyManagerService.this.mOwners.getProfileOwnerComponent(enforcingUserId);
                            if (profileOwner != null) {
                                Intent access$29002 = DevicePolicyManagerService.this.createShowAdminSupportIntent(profileOwner, enforcingUserId);
                                DevicePolicyManagerService.this.mInjector.binderRestoreCallingIdentity(ident);
                                return access$29002;
                            }
                        } else if (sourceType == 2) {
                            Pair<Integer, ComponentName> deviceOwner = DevicePolicyManagerService.this.mOwners.getDeviceOwnerUserIdAndComponent();
                            if (deviceOwner != null) {
                                Intent access$29003 = DevicePolicyManagerService.this.createShowAdminSupportIntent((ComponentName) deviceOwner.second, ((Integer) deviceOwner.first).intValue());
                                DevicePolicyManagerService.this.mInjector.binderRestoreCallingIdentity(ident);
                                return access$29003;
                            }
                        } else if (sourceType == 1) {
                            DevicePolicyManagerService.this.mInjector.binderRestoreCallingIdentity(ident);
                            return null;
                        }
                        DevicePolicyManagerService.this.mInjector.binderRestoreCallingIdentity(ident);
                        return null;
                    }
                }
                return null;
            } finally {
                DevicePolicyManagerService.this.mInjector.binderRestoreCallingIdentity(ident);
            }
        }

        public boolean isUserAffiliatedWithDevice(int userId) {
            return DevicePolicyManagerService.this.isUserAffiliatedWithDeviceLocked(userId);
        }

        public boolean canSilentlyInstallPackage(String callerPackage, int callerUid) {
            if (callerPackage != null && isUserAffiliatedWithDevice(UserHandle.getUserId(callerUid)) && isActiveAdminWithPolicy(callerUid, -1)) {
                return true;
            }
            return false;
        }

        /* Debug info: failed to restart local var, previous not found, register: 4 */
        public void reportSeparateProfileChallengeChanged(int userId) {
            long ident = DevicePolicyManagerService.this.mInjector.binderClearCallingIdentity();
            try {
                synchronized (DevicePolicyManagerService.this.getLockObject()) {
                    DevicePolicyManagerService.this.updateMaximumTimeToLockLocked(userId);
                    DevicePolicyManagerService.this.updatePasswordQualityCacheForUserGroup(userId);
                }
                DevicePolicyManagerService.this.mInjector.binderRestoreCallingIdentity(ident);
                DevicePolicyEventLogger.createEvent(110).setBoolean(DevicePolicyManagerService.this.isSeparateProfileChallengeEnabled(userId)).write();
            } catch (Throwable th) {
                DevicePolicyManagerService.this.mInjector.binderRestoreCallingIdentity(ident);
                throw th;
            }
        }

        public boolean canUserHaveUntrustedCredentialReset(int userId) {
            return DevicePolicyManagerService.this.canUserHaveUntrustedCredentialReset(userId);
        }

        public CharSequence getPrintingDisabledReasonForUser(int userId) {
            synchronized (DevicePolicyManagerService.this.getLockObject()) {
                if (!DevicePolicyManagerService.this.mUserManager.hasUserRestriction("no_printing", UserHandle.of(userId))) {
                    Log.e(DevicePolicyManagerService.LOG_TAG, "printing is enabled");
                    return null;
                }
                String ownerPackage = DevicePolicyManagerService.this.mOwners.getProfileOwnerPackage(userId);
                if (ownerPackage == null) {
                    ownerPackage = DevicePolicyManagerService.this.mOwners.getDeviceOwnerPackageName();
                }
                PackageManager pm = DevicePolicyManagerService.this.mInjector.getPackageManager();
                try {
                    PackageInfo packageInfo = pm.getPackageInfo(ownerPackage, 0);
                    if (packageInfo == null) {
                        Log.e(DevicePolicyManagerService.LOG_TAG, "packageInfo is inexplicably null");
                        return null;
                    }
                    ApplicationInfo appInfo = packageInfo.applicationInfo;
                    if (appInfo == null) {
                        Log.e(DevicePolicyManagerService.LOG_TAG, "appInfo is inexplicably null");
                        return null;
                    }
                    CharSequence appLabel = pm.getApplicationLabel(appInfo);
                    if (appLabel == null) {
                        Log.e(DevicePolicyManagerService.LOG_TAG, "appLabel is inexplicably null");
                        return null;
                    }
                    String string = ActivityThread.currentActivityThread().getSystemUiContext().getResources().getString(17040976, new Object[]{appLabel});
                    return string;
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e(DevicePolicyManagerService.LOG_TAG, "getPackageInfo error", e);
                    return null;
                }
            }
        }

        /* access modifiers changed from: protected */
        public DevicePolicyCache getDevicePolicyCache() {
            return DevicePolicyManagerService.this.mPolicyCache;
        }
    }

    /* access modifiers changed from: private */
    public Intent createShowAdminSupportIntent(ComponentName admin, int userId) {
        Intent intent = new Intent("android.settings.SHOW_ADMIN_SUPPORT_DETAILS");
        intent.putExtra("android.intent.extra.USER_ID", userId);
        intent.putExtra("android.app.extra.DEVICE_ADMIN", admin);
        intent.setFlags(268435456);
        return intent;
    }

    public Intent createAdminSupportIntent(String restriction) {
        ActiveAdmin admin;
        ActiveAdmin admin2;
        Preconditions.checkNotNull(restriction);
        int userId = UserHandle.getUserId(this.mInjector.binderGetCallingUid());
        Intent intent = null;
        if ("policy_disable_camera".equals(restriction) || "policy_disable_screen_capture".equals(restriction)) {
            synchronized (getLockObject()) {
                DevicePolicyData policy = getUserData(userId);
                int N = policy.mAdminList.size();
                int i = 0;
                while (true) {
                    if (i >= N) {
                        break;
                    }
                    admin2 = policy.mAdminList.get(i);
                    if ((!admin2.disableCamera || !"policy_disable_camera".equals(restriction)) && (!admin2.disableScreenCapture || !"policy_disable_screen_capture".equals(restriction))) {
                        i++;
                    }
                }
                intent = createShowAdminSupportIntent(admin2.info.getComponent(), userId);
                if (intent == null && "policy_disable_camera".equals(restriction) && (admin = getDeviceOwnerAdminLocked()) != null && admin.disableCamera) {
                    intent = createShowAdminSupportIntent(admin.info.getComponent(), this.mOwners.getDeviceOwnerUserId());
                }
            }
        } else {
            intent = this.mLocalService.createUserRestrictionSupportIntent(userId, restriction);
        }
        if (intent != null) {
            intent.putExtra("android.app.extra.RESTRICTION", restriction);
        }
        return intent;
    }

    private static boolean isLimitPasswordAllowed(ActiveAdmin admin, int minPasswordQuality) {
        if (admin.minimumPasswordMetrics.quality < minPasswordQuality) {
            return false;
        }
        return admin.info.usesPolicy(0);
    }

    public void setSystemUpdatePolicy(ComponentName who, SystemUpdatePolicy policy) {
        int i;
        if (policy != null) {
            policy.validateType();
            policy.validateFreezePeriods();
            Pair<LocalDate, LocalDate> record = this.mOwners.getSystemUpdateFreezePeriodRecord();
            policy.validateAgainstPreviousFreezePeriod((LocalDate) record.first, (LocalDate) record.second, LocalDate.now());
        }
        synchronized (getLockObject()) {
            getActiveAdminForCallerLocked(who, -2);
            i = 0;
            if (policy == null) {
                this.mOwners.clearSystemUpdatePolicy();
            } else {
                this.mOwners.setSystemUpdatePolicy(policy);
                updateSystemUpdateFreezePeriodsRecord(false);
            }
            this.mOwners.writeDeviceOwner();
        }
        this.mContext.sendBroadcastAsUser(new Intent("android.app.action.SYSTEM_UPDATE_POLICY_CHANGED"), UserHandle.SYSTEM);
        DevicePolicyEventLogger admin = DevicePolicyEventLogger.createEvent(50).setAdmin(who);
        if (policy != null) {
            i = policy.getPolicyType();
        }
        admin.setInt(i).write();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x001e, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.app.admin.SystemUpdatePolicy getSystemUpdatePolicy() {
        /*
            r4 = this;
            java.lang.Object r0 = r4.getLockObject()
            monitor-enter(r0)
            com.android.server.devicepolicy.Owners r1 = r4.mOwners     // Catch:{ all -> 0x001f }
            android.app.admin.SystemUpdatePolicy r1 = r1.getSystemUpdatePolicy()     // Catch:{ all -> 0x001f }
            if (r1 == 0) goto L_0x001d
            boolean r2 = r1.isValid()     // Catch:{ all -> 0x001f }
            if (r2 != 0) goto L_0x001d
            java.lang.String r2 = "DevicePolicyManager"
            java.lang.String r3 = "Stored system update policy is invalid, return null instead."
            android.util.Slog.w(r2, r3)     // Catch:{ all -> 0x001f }
            r2 = 0
            monitor-exit(r0)     // Catch:{ all -> 0x001f }
            return r2
        L_0x001d:
            monitor-exit(r0)     // Catch:{ all -> 0x001f }
            return r1
        L_0x001f:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x001f }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.DevicePolicyManagerService.getSystemUpdatePolicy():android.app.admin.SystemUpdatePolicy");
    }

    private static boolean withinRange(Pair<LocalDate, LocalDate> range, LocalDate date) {
        return !date.isBefore((ChronoLocalDate) range.first) && !date.isAfter((ChronoLocalDate) range.second);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x008c, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateSystemUpdateFreezePeriodsRecord(boolean r11) {
        /*
            r10 = this;
            java.lang.String r0 = "DevicePolicyManager"
            java.lang.String r1 = "updateSystemUpdateFreezePeriodsRecord"
            android.util.Slog.d(r0, r1)
            java.lang.Object r0 = r10.getLockObject()
            monitor-enter(r0)
            com.android.server.devicepolicy.Owners r1 = r10.mOwners     // Catch:{ all -> 0x008d }
            android.app.admin.SystemUpdatePolicy r1 = r1.getSystemUpdatePolicy()     // Catch:{ all -> 0x008d }
            if (r1 != 0) goto L_0x0017
            monitor-exit(r0)     // Catch:{ all -> 0x008d }
            return
        L_0x0017:
            java.time.LocalDate r2 = java.time.LocalDate.now()     // Catch:{ all -> 0x008d }
            android.util.Pair r3 = r1.getCurrentFreezePeriod(r2)     // Catch:{ all -> 0x008d }
            if (r3 != 0) goto L_0x0023
            monitor-exit(r0)     // Catch:{ all -> 0x008d }
            return
        L_0x0023:
            com.android.server.devicepolicy.Owners r4 = r10.mOwners     // Catch:{ all -> 0x008d }
            android.util.Pair r4 = r4.getSystemUpdateFreezePeriodRecord()     // Catch:{ all -> 0x008d }
            java.lang.Object r5 = r4.first     // Catch:{ all -> 0x008d }
            java.time.LocalDate r5 = (java.time.LocalDate) r5     // Catch:{ all -> 0x008d }
            java.lang.Object r6 = r4.second     // Catch:{ all -> 0x008d }
            java.time.LocalDate r6 = (java.time.LocalDate) r6     // Catch:{ all -> 0x008d }
            if (r6 == 0) goto L_0x007c
            if (r5 != 0) goto L_0x0036
            goto L_0x007c
        L_0x0036:
            r7 = 1
            java.time.LocalDate r9 = r6.plusDays(r7)     // Catch:{ all -> 0x008d }
            boolean r9 = r2.equals(r9)     // Catch:{ all -> 0x008d }
            if (r9 == 0) goto L_0x0049
            com.android.server.devicepolicy.Owners r7 = r10.mOwners     // Catch:{ all -> 0x008d }
            boolean r7 = r7.setSystemUpdateFreezePeriodRecord(r5, r2)     // Catch:{ all -> 0x008d }
            goto L_0x0082
        L_0x0049:
            java.time.LocalDate r7 = r6.plusDays(r7)     // Catch:{ all -> 0x008d }
            boolean r7 = r2.isAfter(r7)     // Catch:{ all -> 0x008d }
            if (r7 == 0) goto L_0x006d
            boolean r7 = withinRange(r3, r5)     // Catch:{ all -> 0x008d }
            if (r7 == 0) goto L_0x0066
            boolean r7 = withinRange(r3, r6)     // Catch:{ all -> 0x008d }
            if (r7 == 0) goto L_0x0066
            com.android.server.devicepolicy.Owners r7 = r10.mOwners     // Catch:{ all -> 0x008d }
            boolean r7 = r7.setSystemUpdateFreezePeriodRecord(r5, r2)     // Catch:{ all -> 0x008d }
            goto L_0x0082
        L_0x0066:
            com.android.server.devicepolicy.Owners r7 = r10.mOwners     // Catch:{ all -> 0x008d }
            boolean r7 = r7.setSystemUpdateFreezePeriodRecord(r2, r2)     // Catch:{ all -> 0x008d }
            goto L_0x0082
        L_0x006d:
            boolean r7 = r2.isBefore(r5)     // Catch:{ all -> 0x008d }
            if (r7 == 0) goto L_0x007a
            com.android.server.devicepolicy.Owners r7 = r10.mOwners     // Catch:{ all -> 0x008d }
            boolean r7 = r7.setSystemUpdateFreezePeriodRecord(r2, r2)     // Catch:{ all -> 0x008d }
            goto L_0x0082
        L_0x007a:
            r7 = 0
            goto L_0x0082
        L_0x007c:
            com.android.server.devicepolicy.Owners r7 = r10.mOwners     // Catch:{ all -> 0x008d }
            boolean r7 = r7.setSystemUpdateFreezePeriodRecord(r2, r2)     // Catch:{ all -> 0x008d }
        L_0x0082:
            if (r7 == 0) goto L_0x008b
            if (r11 == 0) goto L_0x008b
            com.android.server.devicepolicy.Owners r8 = r10.mOwners     // Catch:{ all -> 0x008d }
            r8.writeDeviceOwner()     // Catch:{ all -> 0x008d }
        L_0x008b:
            monitor-exit(r0)     // Catch:{ all -> 0x008d }
            return
        L_0x008d:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x008d }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.DevicePolicyManagerService.updateSystemUpdateFreezePeriodsRecord(boolean):void");
    }

    public void clearSystemUpdatePolicyFreezePeriodRecord() {
        enforceShell("clearSystemUpdatePolicyFreezePeriodRecord");
        synchronized (getLockObject()) {
            Slog.i(LOG_TAG, "Clear freeze period record: " + this.mOwners.getSystemUpdateFreezePeriodRecordAsString());
            if (this.mOwners.setSystemUpdateFreezePeriodRecord((LocalDate) null, (LocalDate) null)) {
                this.mOwners.writeDeviceOwner();
            }
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean isCallerDeviceOwner(int callerUid) {
        synchronized (getLockObject()) {
            if (!this.mOwners.hasDeviceOwner()) {
                return false;
            }
            if (UserHandle.getUserId(callerUid) != this.mOwners.getDeviceOwnerUserId()) {
                return false;
            }
            String deviceOwnerPackageName = this.mOwners.getDeviceOwnerComponent().getPackageName();
            try {
                for (String pkg : this.mInjector.getIPackageManager().getPackagesForUid(callerUid)) {
                    if (deviceOwnerPackageName.equals(pkg)) {
                        return true;
                    }
                }
                return false;
            } catch (RemoteException e) {
                return false;
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 11 */
    public void notifyPendingSystemUpdate(SystemUpdateInfo info) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.NOTIFY_PENDING_SYSTEM_UPDATE", "Only the system update service can broadcast update information");
        if (UserHandle.getCallingUserId() != 0) {
            Slog.w(LOG_TAG, "Only the system update service in the system user can broadcast update information.");
        } else if (this.mOwners.saveSystemUpdateInfo(info)) {
            Intent intent = new Intent("android.app.action.NOTIFY_PENDING_SYSTEM_UPDATE").putExtra("android.app.extra.SYSTEM_UPDATE_RECEIVED_TIME", info == null ? -1 : info.getReceivedTime());
            long ident = this.mInjector.binderClearCallingIdentity();
            try {
                synchronized (getLockObject()) {
                    if (this.mOwners.hasDeviceOwner()) {
                        UserHandle deviceOwnerUser = UserHandle.of(this.mOwners.getDeviceOwnerUserId());
                        intent.setComponent(this.mOwners.getDeviceOwnerComponent());
                        this.mContext.sendBroadcastAsUser(intent, deviceOwnerUser);
                    }
                }
                try {
                    for (int userId : this.mInjector.getIActivityManager().getRunningUserIds()) {
                        synchronized (getLockObject()) {
                            ComponentName profileOwnerPackage = this.mOwners.getProfileOwnerComponent(userId);
                            if (profileOwnerPackage != null) {
                                intent.setComponent(profileOwnerPackage);
                                this.mContext.sendBroadcastAsUser(intent, UserHandle.of(userId));
                            }
                        }
                    }
                    this.mInjector.binderRestoreCallingIdentity(ident);
                } catch (RemoteException e) {
                    Log.e(LOG_TAG, "Could not retrieve the list of running users", e);
                    this.mInjector.binderRestoreCallingIdentity(ident);
                }
            } catch (Throwable th) {
                this.mInjector.binderRestoreCallingIdentity(ident);
                throw th;
            }
        }
    }

    public SystemUpdateInfo getPendingSystemUpdate(ComponentName admin) {
        Preconditions.checkNotNull(admin, "ComponentName is null");
        enforceProfileOrDeviceOwner(admin);
        return this.mOwners.getSystemUpdateInfo();
    }

    public void setPermissionPolicy(ComponentName admin, String callerPackage, int policy) throws RemoteException {
        int userId = UserHandle.getCallingUserId();
        synchronized (getLockObject()) {
            enforceCanManageScope(admin, callerPackage, -1, "delegation-permission-grant");
            DevicePolicyData userPolicy = getUserData(userId);
            if (userPolicy.mPermissionPolicy != policy) {
                userPolicy.mPermissionPolicy = policy;
                saveSettingsLocked(userId);
            }
        }
        DevicePolicyEventLogger.createEvent(18).setAdmin(callerPackage).setInt(policy).setBoolean(admin == null).write();
    }

    public int getPermissionPolicy(ComponentName admin) throws RemoteException {
        int i;
        int userId = UserHandle.getCallingUserId();
        synchronized (getLockObject()) {
            i = getUserData(userId).mPermissionPolicy;
        }
        return i;
    }

    /* Debug info: failed to restart local var, previous not found, register: 19 */
    public void setPermissionGrantState(ComponentName admin, String callerPackage, String packageName, String permission, int grantState, RemoteCallback callback) throws RemoteException {
        long ident;
        long ident2;
        String str = callerPackage;
        String str2 = permission;
        int i = grantState;
        RemoteCallback remoteCallback = callback;
        Preconditions.checkNotNull(callback);
        UserHandle user = this.mInjector.binderGetCallingUserHandle();
        synchronized (getLockObject()) {
            try {
                enforceCanManageScope(admin, str, -1, "delegation-permission-grant");
                long ident3 = this.mInjector.binderClearCallingIdentity();
                try {
                    boolean isPostQAdmin = getTargetSdk(str, user.getIdentifier()) >= 29;
                    if (!isPostQAdmin) {
                        try {
                            if (getTargetSdk(packageName, user.getIdentifier()) < 23) {
                                remoteCallback.sendResult((Bundle) null);
                                this.mInjector.binderRestoreCallingIdentity(ident3);
                                return;
                            }
                        } catch (SecurityException e) {
                            e = e;
                            String str3 = packageName;
                            String str4 = str2;
                            try {
                                Slog.e(LOG_TAG, "Could not set permission grant state", e);
                                remoteCallback.sendResult((Bundle) null);
                                this.mInjector.binderRestoreCallingIdentity(ident3);
                            } catch (Throwable th) {
                                th = th;
                                throw th;
                            }
                        } catch (Throwable th2) {
                            th = th2;
                            String str5 = packageName;
                            String str6 = str2;
                            this.mInjector.binderRestoreCallingIdentity(ident3);
                            throw th;
                        }
                    } else {
                        String str7 = packageName;
                    }
                    try {
                        if (!isRuntimePermission(str2)) {
                            try {
                                remoteCallback.sendResult((Bundle) null);
                                this.mInjector.binderRestoreCallingIdentity(ident3);
                            } catch (PackageManager.NameNotFoundException e2) {
                                e = e2;
                                ident = ident3;
                                try {
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("Cannot check if ");
                                    ident3 = ident;
                                } catch (SecurityException e3) {
                                    e = e3;
                                    String str8 = permission;
                                    ident3 = ident;
                                    Slog.e(LOG_TAG, "Could not set permission grant state", e);
                                    remoteCallback.sendResult((Bundle) null);
                                    this.mInjector.binderRestoreCallingIdentity(ident3);
                                } catch (Throwable th3) {
                                    th = th3;
                                    String str9 = permission;
                                    ident3 = ident;
                                    this.mInjector.binderRestoreCallingIdentity(ident3);
                                    throw th;
                                }
                                try {
                                    sb.append(permission);
                                    sb.append("is a runtime permission");
                                    throw new RemoteException(sb.toString(), e, false, true);
                                } catch (SecurityException e4) {
                                    e = e4;
                                    Slog.e(LOG_TAG, "Could not set permission grant state", e);
                                    remoteCallback.sendResult((Bundle) null);
                                    this.mInjector.binderRestoreCallingIdentity(ident3);
                                }
                            }
                        } else {
                            if (i == 1 || i == 2 || i == 0) {
                                try {
                                    PermissionControllerManager permissionControllerManager = this.mInjector.getPermissionControllerManager(user);
                                    Executor mainExecutor = this.mContext.getMainExecutor();
                                    try {
                                        ident2 = ident3;
                                        try {
                                            $$Lambda$DevicePolicyManagerService$kFKlG1V9Atta0tqqg2BxiKi4I r2 = new Consumer(isPostQAdmin, callback, admin, callerPackage, permission, grantState) {
                                                private final /* synthetic */ boolean f$0;
                                                private final /* synthetic */ RemoteCallback f$1;
                                                private final /* synthetic */ ComponentName f$2;
                                                private final /* synthetic */ String f$3;
                                                private final /* synthetic */ String f$4;
                                                private final /* synthetic */ int f$5;

                                                {
                                                    this.f$0 = r1;
                                                    this.f$1 = r2;
                                                    this.f$2 = r3;
                                                    this.f$3 = r4;
                                                    this.f$4 = r5;
                                                    this.f$5 = r6;
                                                }

                                                public final void accept(Object obj) {
                                                    DevicePolicyManagerService.lambda$setPermissionGrantState$13(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, (Boolean) obj);
                                                }
                                            };
                                            permissionControllerManager.setRuntimePermissionGrantStateByDeviceAdmin(callerPackage, packageName, permission, grantState, mainExecutor, r2);
                                        } catch (SecurityException e5) {
                                            e = e5;
                                            String str10 = permission;
                                            ident3 = ident2;
                                            Slog.e(LOG_TAG, "Could not set permission grant state", e);
                                            remoteCallback.sendResult((Bundle) null);
                                            this.mInjector.binderRestoreCallingIdentity(ident3);
                                        } catch (Throwable th4) {
                                            th = th4;
                                            String str11 = permission;
                                            ident3 = ident2;
                                            this.mInjector.binderRestoreCallingIdentity(ident3);
                                            throw th;
                                        }
                                    } catch (SecurityException e6) {
                                        e = e6;
                                        ident2 = ident3;
                                        String str102 = permission;
                                        ident3 = ident2;
                                        Slog.e(LOG_TAG, "Could not set permission grant state", e);
                                        remoteCallback.sendResult((Bundle) null);
                                        this.mInjector.binderRestoreCallingIdentity(ident3);
                                    } catch (Throwable th5) {
                                        th = th5;
                                        ident2 = ident3;
                                        String str112 = permission;
                                        ident3 = ident2;
                                        this.mInjector.binderRestoreCallingIdentity(ident3);
                                        throw th;
                                    }
                                } catch (SecurityException e7) {
                                    e = e7;
                                    String str12 = permission;
                                    Slog.e(LOG_TAG, "Could not set permission grant state", e);
                                    remoteCallback.sendResult((Bundle) null);
                                    this.mInjector.binderRestoreCallingIdentity(ident3);
                                } catch (Throwable th6) {
                                    th = th6;
                                    String str13 = permission;
                                    this.mInjector.binderRestoreCallingIdentity(ident3);
                                    throw th;
                                }
                            } else {
                                ident2 = ident3;
                            }
                            try {
                                this.mInjector.binderRestoreCallingIdentity(ident2);
                                String str14 = permission;
                            } catch (Throwable th7) {
                                th = th7;
                                String str15 = permission;
                                throw th;
                            }
                        }
                    } catch (PackageManager.NameNotFoundException e8) {
                        e = e8;
                        ident = ident3;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("Cannot check if ");
                        ident3 = ident;
                        sb2.append(permission);
                        sb2.append("is a runtime permission");
                        throw new RemoteException(sb2.toString(), e, false, true);
                    }
                } catch (SecurityException e9) {
                    e = e9;
                    String str42 = str2;
                    Slog.e(LOG_TAG, "Could not set permission grant state", e);
                    remoteCallback.sendResult((Bundle) null);
                    this.mInjector.binderRestoreCallingIdentity(ident3);
                } catch (Throwable th8) {
                    th = th8;
                    String str62 = str2;
                    this.mInjector.binderRestoreCallingIdentity(ident3);
                    throw th;
                }
            } catch (Throwable th9) {
                th = th9;
                String str16 = str2;
                throw th;
            }
        }
    }

    static /* synthetic */ void lambda$setPermissionGrantState$13(boolean isPostQAdmin, RemoteCallback callback, ComponentName admin, String callerPackage, String permission, int grantState, Boolean permissionWasSet) {
        if (!isPostQAdmin || permissionWasSet.booleanValue()) {
            DevicePolicyEventLogger.createEvent(19).setAdmin(callerPackage).setStrings(new String[]{permission}).setInt(grantState).setBoolean(admin == null).write();
            callback.sendResult(Bundle.EMPTY);
            return;
        }
        callback.sendResult((Bundle) null);
    }

    /* Debug info: failed to restart local var, previous not found, register: 11 */
    public int getPermissionGrantState(ComponentName admin, String callerPackage, String packageName, String permission) throws RemoteException {
        int granted;
        PackageManager packageManager = this.mInjector.getPackageManager();
        UserHandle user = this.mInjector.binderGetCallingUserHandle();
        if (!isCallerWithSystemUid()) {
            enforceCanManageScope(admin, callerPackage, -1, "delegation-permission-grant");
        }
        synchronized (getLockObject()) {
            long ident = this.mInjector.binderClearCallingIdentity();
            try {
                int i = 1;
                if (getTargetSdk(callerPackage, user.getIdentifier()) < 29) {
                    granted = this.mIPackageManager.checkPermission(permission, packageName, user.getIdentifier());
                } else {
                    if (PermissionChecker.checkPermissionForPreflight(this.mContext, permission, -1, packageManager.getPackageUidAsUser(packageName, user.getIdentifier()), packageName) != 0) {
                        granted = -1;
                    } else {
                        granted = 0;
                    }
                }
                if ((packageManager.getPermissionFlags(permission, packageName, user) & 4) != 4) {
                    this.mInjector.binderRestoreCallingIdentity(ident);
                    return 0;
                }
                if (granted != 0) {
                    i = 2;
                }
                this.mInjector.binderRestoreCallingIdentity(ident);
                return i;
            } catch (PackageManager.NameNotFoundException e) {
                throw new RemoteException("Cannot check if " + permission + "is a runtime permission", e, false, true);
            } catch (Throwable e2) {
                this.mInjector.binderRestoreCallingIdentity(ident);
                throw e2;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isPackageInstalledForUser(String packageName, int userHandle) {
        try {
            PackageInfo pi = this.mInjector.getIPackageManager().getPackageInfo(packageName, 0, userHandle);
            if (pi == null || pi.applicationInfo.flags == 0) {
                return false;
            }
            return true;
        } catch (RemoteException re) {
            throw new RuntimeException("Package manager has died", re);
        }
    }

    public boolean isRuntimePermission(String permissionName) throws PackageManager.NameNotFoundException {
        if ((this.mInjector.getPackageManager().getPermissionInfo(permissionName, 0).protectionLevel & 15) == 1) {
            return true;
        }
        return false;
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    public boolean isProvisioningAllowed(String action, String packageName) {
        Preconditions.checkNotNull(packageName);
        int callingUid = this.mInjector.binderGetCallingUid();
        long ident = this.mInjector.binderClearCallingIdentity();
        try {
            Preconditions.checkArgument(callingUid == this.mInjector.getPackageManager().getPackageUidAsUser(packageName, UserHandle.getUserId(callingUid)), "Caller uid doesn't match the one for the provided package.");
            this.mInjector.binderRestoreCallingIdentity(ident);
            if (checkProvisioningPreConditionSkipPermission(action, packageName) == 0) {
                return true;
            }
            return false;
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalArgumentException("Invalid package provided " + packageName, e);
        } catch (Throwable th) {
            this.mInjector.binderRestoreCallingIdentity(ident);
            throw th;
        }
    }

    public int checkProvisioningPreCondition(String action, String packageName) {
        Preconditions.checkNotNull(packageName);
        enforceCanManageProfileAndDeviceOwners();
        return checkProvisioningPreConditionSkipPermission(action, packageName);
    }

    private int checkProvisioningPreConditionSkipPermission(String action, String packageName) {
        if (!this.mHasFeature) {
            return 13;
        }
        int callingUserId = this.mInjector.userHandleGetCallingUserId();
        if (action != null) {
            char c = 65535;
            switch (action.hashCode()) {
                case -920528692:
                    if (action.equals("android.app.action.PROVISION_MANAGED_DEVICE")) {
                        c = 1;
                        break;
                    }
                    break;
                case -514404415:
                    if (action.equals("android.app.action.PROVISION_MANAGED_USER")) {
                        c = 2;
                        break;
                    }
                    break;
                case -340845101:
                    if (action.equals("android.app.action.PROVISION_MANAGED_PROFILE")) {
                        c = 0;
                        break;
                    }
                    break;
                case 631897778:
                    if (action.equals("android.app.action.PROVISION_MANAGED_SHAREABLE_DEVICE")) {
                        c = 3;
                        break;
                    }
                    break;
            }
            if (c == 0) {
                return checkManagedProfileProvisioningPreCondition(packageName, callingUserId);
            }
            if (c == 1) {
                return checkDeviceOwnerProvisioningPreCondition(callingUserId);
            }
            if (c == 2) {
                return checkManagedUserProvisioningPreCondition(callingUserId);
            }
            if (c == 3) {
                return checkManagedShareableDeviceProvisioningPreCondition(callingUserId);
            }
        }
        throw new IllegalArgumentException("Unknown provisioning action " + action);
    }

    private int checkDeviceOwnerProvisioningPreConditionLocked(ComponentName owner, int deviceOwnerUserId, boolean isAdb, boolean hasIncompatibleAccountsOrNonAdb) {
        if (this.mOwners.hasDeviceOwner()) {
            return 1;
        }
        if (this.mOwners.hasProfileOwner(deviceOwnerUserId)) {
            return 2;
        }
        if (!this.mUserManager.isUserRunning(new UserHandle(deviceOwnerUserId))) {
            return 3;
        }
        if (this.mIsWatch && hasPaired(0)) {
            return 8;
        }
        if (isAdb) {
            if ((this.mIsWatch || hasUserSetupCompleted(0)) && !this.mInjector.userManagerIsSplitSystemUser()) {
                if (this.mUserManager.getUserCount() > 1) {
                    return 5;
                }
                if (hasIncompatibleAccountsOrNonAdb) {
                    return 6;
                }
            }
            return 0;
        }
        if (!this.mInjector.userManagerIsSplitSystemUser()) {
            if (deviceOwnerUserId != 0) {
                return 7;
            }
            if (hasUserSetupCompleted(0)) {
                return 4;
            }
        }
        return 0;
    }

    private int checkDeviceOwnerProvisioningPreCondition(int deviceOwnerUserId) {
        int checkDeviceOwnerProvisioningPreConditionLocked;
        synchronized (getLockObject()) {
            checkDeviceOwnerProvisioningPreConditionLocked = checkDeviceOwnerProvisioningPreConditionLocked((ComponentName) null, deviceOwnerUserId, false, true);
        }
        return checkDeviceOwnerProvisioningPreConditionLocked;
    }

    private int checkManagedProfileProvisioningPreCondition(String packageName, int callingUserId) {
        if (!hasFeatureManagedUsers()) {
            return 9;
        }
        if (callingUserId == 0 && this.mInjector.userManagerIsSplitSystemUser()) {
            return 14;
        }
        if (getProfileOwner(callingUserId) != null) {
            return 2;
        }
        long ident = this.mInjector.binderClearCallingIdentity();
        try {
            UserHandle callingUserHandle = UserHandle.of(callingUserId);
            ComponentName ownerAdmin = getOwnerComponent(packageName, callingUserId);
            if (!this.mUserManager.hasUserRestriction("no_add_managed_profile", callingUserHandle) || (ownerAdmin != null && !isAdminAffectedByRestriction(ownerAdmin, "no_add_managed_profile", callingUserId))) {
                boolean canRemoveProfile = true;
                if (this.mUserManager.hasUserRestriction("no_remove_managed_profile", callingUserHandle) && (ownerAdmin == null || isAdminAffectedByRestriction(ownerAdmin, "no_remove_managed_profile", callingUserId))) {
                    canRemoveProfile = false;
                }
                if (!this.mUserManager.canAddMoreManagedProfiles(callingUserId, canRemoveProfile)) {
                    this.mInjector.binderRestoreCallingIdentity(ident);
                    return 11;
                }
                this.mInjector.binderRestoreCallingIdentity(ident);
                return 0;
            }
            return 15;
        } finally {
            this.mInjector.binderRestoreCallingIdentity(ident);
        }
    }

    private ComponentName getOwnerComponent(String packageName, int userId) {
        if (isDeviceOwnerPackage(packageName, userId)) {
            return this.mOwners.getDeviceOwnerComponent();
        }
        if (isProfileOwnerPackage(packageName, userId)) {
            return this.mOwners.getProfileOwnerComponent(userId);
        }
        return null;
    }

    private ComponentName getOwnerComponent(int userId) {
        synchronized (getLockObject()) {
            if (this.mOwners.getDeviceOwnerUserId() == userId) {
                ComponentName deviceOwnerComponent = this.mOwners.getDeviceOwnerComponent();
                return deviceOwnerComponent;
            } else if (!this.mOwners.hasProfileOwner(userId)) {
                return null;
            } else {
                ComponentName profileOwnerComponent = this.mOwners.getProfileOwnerComponent(userId);
                return profileOwnerComponent;
            }
        }
    }

    private int checkManagedUserProvisioningPreCondition(int callingUserId) {
        if (!hasFeatureManagedUsers()) {
            return 9;
        }
        if (!this.mInjector.userManagerIsSplitSystemUser()) {
            return 12;
        }
        if (callingUserId == 0) {
            return 10;
        }
        if (hasUserSetupCompleted(callingUserId)) {
            return 4;
        }
        if (!this.mIsWatch || !hasPaired(0)) {
            return 0;
        }
        return 8;
    }

    private int checkManagedShareableDeviceProvisioningPreCondition(int callingUserId) {
        if (!this.mInjector.userManagerIsSplitSystemUser()) {
            return 12;
        }
        return checkDeviceOwnerProvisioningPreCondition(callingUserId);
    }

    private boolean hasFeatureManagedUsers() {
        try {
            return this.mIPackageManager.hasSystemFeature("android.software.managed_users", 0);
        } catch (RemoteException e) {
            return false;
        }
    }

    public String getWifiMacAddress(ComponentName admin) {
        enforceDeviceOwner(admin);
        long ident = this.mInjector.binderClearCallingIdentity();
        try {
            String[] macAddresses = this.mInjector.getWifiManager().getFactoryMacAddresses();
            String str = null;
            if (macAddresses == null) {
                return null;
            }
            DevicePolicyEventLogger.createEvent(54).setAdmin(admin).write();
            if (macAddresses.length > 0) {
                str = macAddresses[0];
            }
            this.mInjector.binderRestoreCallingIdentity(ident);
            return str;
        } finally {
            this.mInjector.binderRestoreCallingIdentity(ident);
        }
    }

    private int getTargetSdk(String packageName, int userId) {
        try {
            ApplicationInfo ai = this.mIPackageManager.getApplicationInfo(packageName, 0, userId);
            if (ai == null) {
                return 0;
            }
            return ai.targetSdkVersion;
        } catch (RemoteException e) {
            return 0;
        }
    }

    public boolean isManagedProfile(ComponentName admin) {
        enforceProfileOrDeviceOwner(admin);
        return isManagedProfile(this.mInjector.userHandleGetCallingUserId());
    }

    public boolean isSystemOnlyUser(ComponentName admin) {
        enforceDeviceOwner(admin);
        return UserManager.isSplitSystemUser() && this.mInjector.userHandleGetCallingUserId() == 0;
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    public void reboot(ComponentName admin) {
        Preconditions.checkNotNull(admin);
        enforceDeviceOwner(admin);
        long ident = this.mInjector.binderClearCallingIdentity();
        try {
            if (this.mTelephonyManager.getCallState() == 0) {
                DevicePolicyEventLogger.createEvent(34).setAdmin(admin).write();
                this.mInjector.powerManagerReboot("deviceowner");
                return;
            }
            throw new IllegalStateException("Cannot be called with ongoing call on the device");
        } finally {
            this.mInjector.binderRestoreCallingIdentity(ident);
        }
    }

    public void setShortSupportMessage(ComponentName who, CharSequence message) {
        if (this.mHasFeature) {
            Preconditions.checkNotNull(who, "ComponentName is null");
            int userHandle = this.mInjector.userHandleGetCallingUserId();
            synchronized (getLockObject()) {
                ActiveAdmin admin = getActiveAdminForUidLocked(who, this.mInjector.binderGetCallingUid());
                if (!TextUtils.equals(admin.shortSupportMessage, message)) {
                    admin.shortSupportMessage = message;
                    saveSettingsLocked(userHandle);
                }
            }
            DevicePolicyEventLogger.createEvent(43).setAdmin(who).write();
        }
    }

    public CharSequence getShortSupportMessage(ComponentName who) {
        CharSequence charSequence;
        if (!this.mHasFeature) {
            return null;
        }
        Preconditions.checkNotNull(who, "ComponentName is null");
        synchronized (getLockObject()) {
            charSequence = getActiveAdminForUidLocked(who, this.mInjector.binderGetCallingUid()).shortSupportMessage;
        }
        return charSequence;
    }

    public void setLongSupportMessage(ComponentName who, CharSequence message) {
        if (this.mHasFeature) {
            Preconditions.checkNotNull(who, "ComponentName is null");
            int userHandle = this.mInjector.userHandleGetCallingUserId();
            synchronized (getLockObject()) {
                ActiveAdmin admin = getActiveAdminForUidLocked(who, this.mInjector.binderGetCallingUid());
                if (!TextUtils.equals(admin.longSupportMessage, message)) {
                    admin.longSupportMessage = message;
                    saveSettingsLocked(userHandle);
                }
            }
            DevicePolicyEventLogger.createEvent(44).setAdmin(who).write();
        }
    }

    public CharSequence getLongSupportMessage(ComponentName who) {
        CharSequence charSequence;
        if (!this.mHasFeature) {
            return null;
        }
        Preconditions.checkNotNull(who, "ComponentName is null");
        synchronized (getLockObject()) {
            charSequence = getActiveAdminForUidLocked(who, this.mInjector.binderGetCallingUid()).longSupportMessage;
        }
        return charSequence;
    }

    public CharSequence getShortSupportMessageForUser(ComponentName who, int userHandle) {
        if (!this.mHasFeature) {
            return null;
        }
        Preconditions.checkNotNull(who, "ComponentName is null");
        if (isCallerWithSystemUid()) {
            synchronized (getLockObject()) {
                ActiveAdmin admin = getActiveAdminUncheckedLocked(who, userHandle);
                if (admin == null) {
                    return null;
                }
                CharSequence charSequence = admin.shortSupportMessage;
                return charSequence;
            }
        }
        throw new SecurityException("Only the system can query support message for user");
    }

    public CharSequence getLongSupportMessageForUser(ComponentName who, int userHandle) {
        if (!this.mHasFeature) {
            return null;
        }
        Preconditions.checkNotNull(who, "ComponentName is null");
        if (isCallerWithSystemUid()) {
            synchronized (getLockObject()) {
                ActiveAdmin admin = getActiveAdminUncheckedLocked(who, userHandle);
                if (admin == null) {
                    return null;
                }
                CharSequence charSequence = admin.longSupportMessage;
                return charSequence;
            }
        }
        throw new SecurityException("Only the system can query support message for user");
    }

    public void setOrganizationColor(ComponentName who, int color) {
        if (this.mHasFeature) {
            Preconditions.checkNotNull(who, "ComponentName is null");
            int userHandle = this.mInjector.userHandleGetCallingUserId();
            enforceManagedProfile(userHandle, "set organization color");
            synchronized (getLockObject()) {
                getActiveAdminForCallerLocked(who, -1).organizationColor = color;
                saveSettingsLocked(userHandle);
            }
            DevicePolicyEventLogger.createEvent(39).setAdmin(who).write();
        }
    }

    public void setOrganizationColorForUser(int color, int userId) {
        if (this.mHasFeature) {
            enforceFullCrossUsersPermission(userId);
            enforceManageUsers();
            enforceManagedProfile(userId, "set organization color");
            synchronized (getLockObject()) {
                getProfileOwnerAdminLocked(userId).organizationColor = color;
                saveSettingsLocked(userId);
            }
        }
    }

    public int getOrganizationColor(ComponentName who) {
        int i;
        if (!this.mHasFeature) {
            return ActiveAdmin.DEF_ORGANIZATION_COLOR;
        }
        Preconditions.checkNotNull(who, "ComponentName is null");
        enforceManagedProfile(this.mInjector.userHandleGetCallingUserId(), "get organization color");
        synchronized (getLockObject()) {
            i = getActiveAdminForCallerLocked(who, -1).organizationColor;
        }
        return i;
    }

    public int getOrganizationColorForUser(int userHandle) {
        int i;
        if (!this.mHasFeature) {
            return ActiveAdmin.DEF_ORGANIZATION_COLOR;
        }
        enforceFullCrossUsersPermission(userHandle);
        enforceManagedProfile(userHandle, "get organization color");
        synchronized (getLockObject()) {
            ActiveAdmin profileOwner = getProfileOwnerAdminLocked(userHandle);
            if (profileOwner != null) {
                i = profileOwner.organizationColor;
            } else {
                i = ActiveAdmin.DEF_ORGANIZATION_COLOR;
            }
        }
        return i;
    }

    public void setOrganizationName(ComponentName who, CharSequence text) {
        String str;
        if (this.mHasFeature) {
            Preconditions.checkNotNull(who, "ComponentName is null");
            int userHandle = this.mInjector.userHandleGetCallingUserId();
            synchronized (getLockObject()) {
                ActiveAdmin admin = getActiveAdminForCallerLocked(who, -1);
                if (!TextUtils.equals(admin.organizationName, text)) {
                    if (text != null) {
                        if (text.length() != 0) {
                            str = text.toString();
                            admin.organizationName = str;
                            saveSettingsLocked(userHandle);
                        }
                    }
                    str = null;
                    admin.organizationName = str;
                    saveSettingsLocked(userHandle);
                }
            }
        }
    }

    public CharSequence getOrganizationName(ComponentName who) {
        String str;
        if (!this.mHasFeature) {
            return null;
        }
        Preconditions.checkNotNull(who, "ComponentName is null");
        enforceManagedProfile(this.mInjector.userHandleGetCallingUserId(), "get organization name");
        synchronized (getLockObject()) {
            str = getActiveAdminForCallerLocked(who, -1).organizationName;
        }
        return str;
    }

    public CharSequence getDeviceOwnerOrganizationName() {
        String str = null;
        if (!this.mHasFeature) {
            return null;
        }
        enforceDeviceOwnerOrManageUsers();
        synchronized (getLockObject()) {
            ActiveAdmin deviceOwnerAdmin = getDeviceOwnerAdminLocked();
            if (deviceOwnerAdmin != null) {
                str = deviceOwnerAdmin.organizationName;
            }
        }
        return str;
    }

    public CharSequence getOrganizationNameForUser(int userHandle) {
        String str = null;
        if (!this.mHasFeature) {
            return null;
        }
        enforceFullCrossUsersPermission(userHandle);
        enforceManagedProfile(userHandle, "get organization name");
        synchronized (getLockObject()) {
            ActiveAdmin profileOwner = getProfileOwnerAdminLocked(userHandle);
            if (profileOwner != null) {
                str = profileOwner.organizationName;
            }
        }
        return str;
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    public List<String> setMeteredDataDisabledPackages(ComponentName who, List<String> packageNames) {
        List<String> excludedPkgs;
        Preconditions.checkNotNull(who);
        Preconditions.checkNotNull(packageNames);
        if (!this.mHasFeature) {
            return packageNames;
        }
        synchronized (getLockObject()) {
            ActiveAdmin admin = getActiveAdminForCallerLocked(who, -1);
            int callingUserId = this.mInjector.userHandleGetCallingUserId();
            long identity = this.mInjector.binderClearCallingIdentity();
            try {
                excludedPkgs = removeInvalidPkgsForMeteredDataRestriction(callingUserId, packageNames);
                admin.meteredDisabledPackages = packageNames;
                pushMeteredDisabledPackagesLocked(callingUserId);
                saveSettingsLocked(callingUserId);
            } finally {
                this.mInjector.binderRestoreCallingIdentity(identity);
            }
        }
        return excludedPkgs;
    }

    private List<String> removeInvalidPkgsForMeteredDataRestriction(int userId, List<String> pkgNames) {
        Set<String> activeAdmins = getActiveAdminPackagesLocked(userId);
        List<String> excludedPkgs = new ArrayList<>();
        for (int i = pkgNames.size() - 1; i >= 0; i--) {
            String pkgName = pkgNames.get(i);
            if (activeAdmins.contains(pkgName)) {
                excludedPkgs.add(pkgName);
            } else {
                try {
                    if (!this.mInjector.getIPackageManager().isPackageAvailable(pkgName, userId)) {
                        excludedPkgs.add(pkgName);
                    }
                } catch (RemoteException e) {
                }
            }
        }
        pkgNames.removeAll(excludedPkgs);
        return excludedPkgs;
    }

    public List<String> getMeteredDataDisabledPackages(ComponentName who) {
        List<String> arrayList;
        Preconditions.checkNotNull(who);
        if (!this.mHasFeature) {
            return new ArrayList();
        }
        synchronized (getLockObject()) {
            ActiveAdmin admin = getActiveAdminForCallerLocked(who, -1);
            arrayList = admin.meteredDisabledPackages == null ? new ArrayList<>() : admin.meteredDisabledPackages;
        }
        return arrayList;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0027, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isMeteredDataDisabledPackageForUser(android.content.ComponentName r5, java.lang.String r6, int r7) {
        /*
            r4 = this;
            com.android.internal.util.Preconditions.checkNotNull(r5)
            boolean r0 = r4.mHasFeature
            r1 = 0
            if (r0 != 0) goto L_0x0009
            return r1
        L_0x0009:
            boolean r0 = r4.isCallerWithSystemUid()
            if (r0 == 0) goto L_0x002b
            java.lang.Object r0 = r4.getLockObject()
            monitor-enter(r0)
            com.android.server.devicepolicy.DevicePolicyManagerService$ActiveAdmin r2 = r4.getActiveAdminUncheckedLocked(r5, r7)     // Catch:{ all -> 0x0028 }
            if (r2 == 0) goto L_0x0026
            java.util.List<java.lang.String> r3 = r2.meteredDisabledPackages     // Catch:{ all -> 0x0028 }
            if (r3 == 0) goto L_0x0026
            java.util.List<java.lang.String> r1 = r2.meteredDisabledPackages     // Catch:{ all -> 0x0028 }
            boolean r1 = r1.contains(r6)     // Catch:{ all -> 0x0028 }
            monitor-exit(r0)     // Catch:{ all -> 0x0028 }
            return r1
        L_0x0026:
            monitor-exit(r0)     // Catch:{ all -> 0x0028 }
            return r1
        L_0x0028:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0028 }
            throw r1
        L_0x002b:
            java.lang.SecurityException r0 = new java.lang.SecurityException
            java.lang.String r1 = "Only the system can query restricted pkgs for a specific user"
            r0.<init>(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.DevicePolicyManagerService.isMeteredDataDisabledPackageForUser(android.content.ComponentName, java.lang.String, int):boolean");
    }

    private boolean hasGrantProfileOwnerDevcieIdAccessPermission() {
        return this.mContext.checkCallingPermission("android.permission.GRANT_PROFILE_OWNER_DEVICE_IDS_ACCESS") == 0;
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    public void grantDeviceIdsAccessToProfileOwner(ComponentName who, int userId) {
        Preconditions.checkNotNull(who);
        if (this.mHasFeature) {
            if (!isCallerWithSystemUid() && !isAdb() && !hasGrantProfileOwnerDevcieIdAccessPermission()) {
                throw new SecurityException("Only the system can grant Device IDs access for a profile owner.");
            } else if (!isAdb() || !hasIncompatibleAccountsOrNonAdbNoLock(userId, who)) {
                synchronized (getLockObject()) {
                    if (isProfileOwner(who, userId)) {
                        Slog.i(LOG_TAG, String.format("Granting Device ID access to %s, for user %d", new Object[]{who.flattenToString(), Integer.valueOf(userId)}));
                        this.mOwners.setProfileOwnerCanAccessDeviceIds(userId);
                    } else {
                        throw new IllegalArgumentException(String.format("Component %s is not a Profile Owner of user %d", new Object[]{who.flattenToString(), Integer.valueOf(userId)}));
                    }
                }
            } else {
                throw new SecurityException("Can only be called from ADB if the device has no accounts.");
            }
        }
    }

    private void pushMeteredDisabledPackagesLocked(int userId) {
        this.mInjector.getNetworkPolicyManagerInternal().setMeteredRestrictedPackages(getMeteredDisabledPackagesLocked(userId), userId);
    }

    private Set<String> getMeteredDisabledPackagesLocked(int userId) {
        ActiveAdmin admin;
        ComponentName who = getOwnerComponent(userId);
        Set<String> restrictedPkgs = new ArraySet<>();
        if (!(who == null || (admin = getActiveAdminUncheckedLocked(who, userId)) == null || admin.meteredDisabledPackages == null)) {
            restrictedPkgs.addAll(admin.meteredDisabledPackages);
        }
        return restrictedPkgs;
    }

    public void setAffiliationIds(ComponentName admin, List<String> ids) {
        if (this.mHasFeature) {
            if (ids != null) {
                for (String id : ids) {
                    if (TextUtils.isEmpty(id)) {
                        throw new IllegalArgumentException("ids must not contain empty string");
                    }
                }
                Set<String> affiliationIds = new ArraySet<>(ids);
                int callingUserId = this.mInjector.userHandleGetCallingUserId();
                synchronized (getLockObject()) {
                    getActiveAdminForCallerLocked(admin, -1);
                    getUserData(callingUserId).mAffiliationIds = affiliationIds;
                    saveSettingsLocked(callingUserId);
                    if (callingUserId != 0 && isDeviceOwner(admin, callingUserId)) {
                        getUserData(0).mAffiliationIds = affiliationIds;
                        saveSettingsLocked(0);
                    }
                    maybePauseDeviceWideLoggingLocked();
                    maybeResumeDeviceWideLoggingLocked();
                    maybeClearLockTaskPolicyLocked();
                }
                return;
            }
            throw new IllegalArgumentException("ids must not be null");
        }
    }

    public List<String> getAffiliationIds(ComponentName admin) {
        ArrayList arrayList;
        if (!this.mHasFeature) {
            return Collections.emptyList();
        }
        Preconditions.checkNotNull(admin);
        synchronized (getLockObject()) {
            getActiveAdminForCallerLocked(admin, -1);
            arrayList = new ArrayList(getUserData(this.mInjector.userHandleGetCallingUserId()).mAffiliationIds);
        }
        return arrayList;
    }

    public boolean isAffiliatedUser() {
        boolean isUserAffiliatedWithDeviceLocked;
        if (!this.mHasFeature) {
            return false;
        }
        synchronized (getLockObject()) {
            isUserAffiliatedWithDeviceLocked = isUserAffiliatedWithDeviceLocked(this.mInjector.userHandleGetCallingUserId());
        }
        return isUserAffiliatedWithDeviceLocked;
    }

    /* access modifiers changed from: private */
    public boolean isUserAffiliatedWithDeviceLocked(int userId) {
        if (!this.mOwners.hasDeviceOwner()) {
            return false;
        }
        if (userId == this.mOwners.getDeviceOwnerUserId() || userId == 0) {
            return true;
        }
        if (getProfileOwner(userId) == null) {
            return false;
        }
        Set<String> userAffiliationIds = getUserData(userId).mAffiliationIds;
        Set<String> deviceAffiliationIds = getUserData(0).mAffiliationIds;
        for (String id : userAffiliationIds) {
            if (deviceAffiliationIds.contains(id)) {
                return true;
            }
        }
        return false;
    }

    private boolean areAllUsersAffiliatedWithDeviceLocked() {
        long ident = this.mInjector.binderClearCallingIdentity();
        try {
            List<UserInfo> userInfos = this.mUserManager.getUsers(true);
            for (int i = 0; i < userInfos.size(); i++) {
                int userId = userInfos.get(i).id;
                if (!isUserAffiliatedWithDeviceLocked(userId)) {
                    Slog.d(LOG_TAG, "User id " + userId + " not affiliated.");
                    return false;
                }
            }
            this.mInjector.binderRestoreCallingIdentity(ident);
            return true;
        } finally {
            this.mInjector.binderRestoreCallingIdentity(ident);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0031, code lost:
        android.app.admin.DevicePolicyEventLogger.createEvent(15).setAdmin(r3).setBoolean(r4).write();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0042, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setSecurityLoggingEnabled(android.content.ComponentName r3, boolean r4) {
        /*
            r2 = this;
            boolean r0 = r2.mHasFeature
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            com.android.internal.util.Preconditions.checkNotNull(r3)
            java.lang.Object r0 = r2.getLockObject()
            monitor-enter(r0)
            r1 = -2
            r2.getActiveAdminForCallerLocked(r3, r1)     // Catch:{ all -> 0x0043 }
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r1 = r2.mInjector     // Catch:{ all -> 0x0043 }
            boolean r1 = r1.securityLogGetLoggingEnabledProperty()     // Catch:{ all -> 0x0043 }
            if (r4 != r1) goto L_0x001b
            monitor-exit(r0)     // Catch:{ all -> 0x0043 }
            return
        L_0x001b:
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r1 = r2.mInjector     // Catch:{ all -> 0x0043 }
            r1.securityLogSetLoggingEnabledProperty(r4)     // Catch:{ all -> 0x0043 }
            if (r4 == 0) goto L_0x002b
            com.android.server.devicepolicy.SecurityLogMonitor r1 = r2.mSecurityLogMonitor     // Catch:{ all -> 0x0043 }
            r1.start()     // Catch:{ all -> 0x0043 }
            r2.maybePauseDeviceWideLoggingLocked()     // Catch:{ all -> 0x0043 }
            goto L_0x0030
        L_0x002b:
            com.android.server.devicepolicy.SecurityLogMonitor r1 = r2.mSecurityLogMonitor     // Catch:{ all -> 0x0043 }
            r1.stop()     // Catch:{ all -> 0x0043 }
        L_0x0030:
            monitor-exit(r0)     // Catch:{ all -> 0x0043 }
            r0 = 15
            android.app.admin.DevicePolicyEventLogger r0 = android.app.admin.DevicePolicyEventLogger.createEvent(r0)
            android.app.admin.DevicePolicyEventLogger r0 = r0.setAdmin(r3)
            android.app.admin.DevicePolicyEventLogger r0 = r0.setBoolean(r4)
            r0.write()
            return
        L_0x0043:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0043 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.DevicePolicyManagerService.setSecurityLoggingEnabled(android.content.ComponentName, boolean):void");
    }

    public boolean isSecurityLoggingEnabled(ComponentName admin) {
        boolean securityLogGetLoggingEnabledProperty;
        if (!this.mHasFeature) {
            return false;
        }
        synchronized (getLockObject()) {
            if (!isCallerWithSystemUid()) {
                Preconditions.checkNotNull(admin);
                getActiveAdminForCallerLocked(admin, -2);
            }
            securityLogGetLoggingEnabledProperty = this.mInjector.securityLogGetLoggingEnabledProperty();
        }
        return securityLogGetLoggingEnabledProperty;
    }

    private void recordSecurityLogRetrievalTime() {
        synchronized (getLockObject()) {
            long currentTime = System.currentTimeMillis();
            DevicePolicyData policyData = getUserData(0);
            if (currentTime > policyData.mLastSecurityLogRetrievalTime) {
                policyData.mLastSecurityLogRetrievalTime = currentTime;
                saveSettingsLocked(0);
            }
        }
    }

    public ParceledListSlice<SecurityLog.SecurityEvent> retrievePreRebootSecurityLogs(ComponentName admin) {
        if (!this.mHasFeature) {
            return null;
        }
        Preconditions.checkNotNull(admin);
        ensureDeviceOwnerAndAllUsersAffiliated(admin);
        DevicePolicyEventLogger.createEvent(17).setAdmin(admin).write();
        if (!this.mContext.getResources().getBoolean(17891540) || !this.mInjector.securityLogGetLoggingEnabledProperty()) {
            return null;
        }
        recordSecurityLogRetrievalTime();
        ArrayList<SecurityLog.SecurityEvent> output = new ArrayList<>();
        try {
            SecurityLog.readPreviousEvents(output);
            return new ParceledListSlice<>(output);
        } catch (IOException e) {
            Slog.w(LOG_TAG, "Fail to read previous events", e);
            return new ParceledListSlice<>(Collections.emptyList());
        }
    }

    public ParceledListSlice<SecurityLog.SecurityEvent> retrieveSecurityLogs(ComponentName admin) {
        if (!this.mHasFeature) {
            return null;
        }
        Preconditions.checkNotNull(admin);
        ensureDeviceOwnerAndAllUsersAffiliated(admin);
        if (!this.mInjector.securityLogGetLoggingEnabledProperty()) {
            return null;
        }
        recordSecurityLogRetrievalTime();
        List<SecurityLog.SecurityEvent> logs = this.mSecurityLogMonitor.retrieveLogs();
        DevicePolicyEventLogger.createEvent(16).setAdmin(admin).write();
        if (logs != null) {
            return new ParceledListSlice<>(logs);
        }
        return null;
    }

    public long forceSecurityLogs() {
        enforceShell("forceSecurityLogs");
        if (this.mInjector.securityLogGetLoggingEnabledProperty()) {
            return this.mSecurityLogMonitor.forceLogs();
        }
        throw new IllegalStateException("logging is not available");
    }

    private void enforceCanManageDeviceAdmin() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_DEVICE_ADMINS", (String) null);
    }

    private void enforceCanManageProfileAndDeviceOwners() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_PROFILE_AND_DEVICE_OWNERS", (String) null);
    }

    private void enforceCallerSystemUserHandle() {
        if (UserHandle.getUserId(this.mInjector.binderGetCallingUid()) != 0) {
            throw new SecurityException("Caller has to be in user 0");
        }
    }

    public boolean isUninstallInQueue(String packageName) {
        boolean contains;
        enforceCanManageDeviceAdmin();
        Pair<String, Integer> packageUserPair = new Pair<>(packageName, Integer.valueOf(this.mInjector.userHandleGetCallingUserId()));
        synchronized (getLockObject()) {
            contains = this.mPackagesToRemove.contains(packageUserPair);
        }
        return contains;
    }

    public void uninstallPackageWithActiveAdmins(final String packageName) {
        enforceCanManageDeviceAdmin();
        Preconditions.checkArgument(!TextUtils.isEmpty(packageName));
        final int userId = this.mInjector.userHandleGetCallingUserId();
        enforceUserUnlocked(userId);
        ComponentName profileOwner = getProfileOwner(userId);
        if (profileOwner == null || !packageName.equals(profileOwner.getPackageName())) {
            ComponentName deviceOwner = getDeviceOwnerComponent(false);
            if (getDeviceOwnerUserId() != userId || deviceOwner == null || !packageName.equals(deviceOwner.getPackageName())) {
                Pair<String, Integer> packageUserPair = new Pair<>(packageName, Integer.valueOf(userId));
                synchronized (getLockObject()) {
                    this.mPackagesToRemove.add(packageUserPair);
                }
                List<ComponentName> allActiveAdmins = getActiveAdmins(userId);
                final List<ComponentName> packageActiveAdmins = new ArrayList<>();
                if (allActiveAdmins != null) {
                    for (ComponentName activeAdmin : allActiveAdmins) {
                        if (packageName.equals(activeAdmin.getPackageName())) {
                            packageActiveAdmins.add(activeAdmin);
                            removeActiveAdmin(activeAdmin, userId);
                        }
                    }
                }
                if (packageActiveAdmins.size() == 0) {
                    startUninstallIntent(packageName, userId);
                } else {
                    this.mHandler.postDelayed(new Runnable() {
                        public void run() {
                            for (ComponentName activeAdmin : packageActiveAdmins) {
                                DevicePolicyManagerService.this.removeAdminArtifacts(activeAdmin, userId);
                            }
                            DevicePolicyManagerService.this.startUninstallIntent(packageName, userId);
                        }
                    }, JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY);
                }
            } else {
                throw new IllegalArgumentException("Cannot uninstall a package with a device owner");
            }
        } else {
            throw new IllegalArgumentException("Cannot uninstall a package with a profile owner");
        }
    }

    public boolean isDeviceProvisioned() {
        boolean z;
        enforceManageUsers();
        synchronized (getLockObject()) {
            z = getUserDataUnchecked(0).mUserSetupComplete;
        }
        return z;
    }

    private boolean isCurrentUserDemo() {
        if (!UserManager.isDeviceInDemoMode(this.mContext)) {
            return false;
        }
        int userId = this.mInjector.userHandleGetCallingUserId();
        long callingIdentity = this.mInjector.binderClearCallingIdentity();
        try {
            return this.mUserManager.getUserInfo(userId).isDemo();
        } finally {
            this.mInjector.binderRestoreCallingIdentity(callingIdentity);
        }
    }

    /* access modifiers changed from: private */
    public void removePackageIfRequired(String packageName, int userId) {
        if (!packageHasActiveAdmins(packageName, userId)) {
            startUninstallIntent(packageName, userId);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0029, code lost:
        if (r5.mInjector.getIPackageManager().getPackageInfo(r6, 0, r7) != null) goto L_0x0035;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x002b, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x002e, code lost:
        android.util.Log.e(LOG_TAG, "Failure talking to PackageManager while getting package info");
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void startUninstallIntent(java.lang.String r6, int r7) {
        /*
            r5 = this;
            android.util.Pair r0 = new android.util.Pair
            java.lang.Integer r1 = java.lang.Integer.valueOf(r7)
            r0.<init>(r6, r1)
            java.lang.Object r1 = r5.getLockObject()
            monitor-enter(r1)
            java.util.Set<android.util.Pair<java.lang.String, java.lang.Integer>> r2 = r5.mPackagesToRemove     // Catch:{ all -> 0x0073 }
            boolean r2 = r2.contains(r0)     // Catch:{ all -> 0x0073 }
            if (r2 != 0) goto L_0x0018
            monitor-exit(r1)     // Catch:{ all -> 0x0073 }
            return
        L_0x0018:
            java.util.Set<android.util.Pair<java.lang.String, java.lang.Integer>> r2 = r5.mPackagesToRemove     // Catch:{ all -> 0x0073 }
            r2.remove(r0)     // Catch:{ all -> 0x0073 }
            monitor-exit(r1)     // Catch:{ all -> 0x0073 }
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r1 = r5.mInjector     // Catch:{ RemoteException -> 0x002d }
            android.content.pm.IPackageManager r1 = r1.getIPackageManager()     // Catch:{ RemoteException -> 0x002d }
            r2 = 0
            android.content.pm.PackageInfo r1 = r1.getPackageInfo(r6, r2, r7)     // Catch:{ RemoteException -> 0x002d }
            if (r1 != 0) goto L_0x002c
            return
        L_0x002c:
            goto L_0x0035
        L_0x002d:
            r1 = move-exception
            java.lang.String r2 = "DevicePolicyManager"
            java.lang.String r3 = "Failure talking to PackageManager while getting package info"
            android.util.Log.e(r2, r3)
        L_0x0035:
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r1 = r5.mInjector     // Catch:{ RemoteException -> 0x003f }
            android.app.IActivityManager r1 = r1.getIActivityManager()     // Catch:{ RemoteException -> 0x003f }
            r1.forceStopPackage(r6, r7)     // Catch:{ RemoteException -> 0x003f }
            goto L_0x0047
        L_0x003f:
            r1 = move-exception
            java.lang.String r2 = "DevicePolicyManager"
            java.lang.String r3 = "Failure talking to ActivityManager while force stopping package"
            android.util.Log.e(r2, r3)
        L_0x0047:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "package:"
            r1.append(r2)
            r1.append(r6)
            java.lang.String r1 = r1.toString()
            android.net.Uri r1 = android.net.Uri.parse(r1)
            android.content.Intent r2 = new android.content.Intent
            java.lang.String r3 = "android.intent.action.UNINSTALL_PACKAGE"
            r2.<init>(r3, r1)
            r3 = 268435456(0x10000000, float:2.5243549E-29)
            r2.setFlags(r3)
            android.content.Context r3 = r5.mContext
            android.os.UserHandle r4 = android.os.UserHandle.of(r7)
            r3.startActivityAsUser(r2, r4)
            return
        L_0x0073:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0073 }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.DevicePolicyManagerService.startUninstallIntent(java.lang.String, int):void");
    }

    /* access modifiers changed from: private */
    public void removeAdminArtifacts(ComponentName adminReceiver, int userHandle) {
        synchronized (getLockObject()) {
            ActiveAdmin admin = getActiveAdminUncheckedLocked(adminReceiver, userHandle);
            if (admin != null) {
                DevicePolicyData policy = getUserData(userHandle);
                boolean doProxyCleanup = admin.info.usesPolicy(5);
                policy.mAdminList.remove(admin);
                policy.mAdminMap.remove(adminReceiver);
                validatePasswordOwnerLocked(policy);
                if (doProxyCleanup) {
                    resetGlobalProxyLocked(policy);
                }
                pushActiveAdminPackagesLocked(userHandle);
                pushMeteredDisabledPackagesLocked(userHandle);
                saveSettingsLocked(userHandle);
                updateMaximumTimeToLockLocked(userHandle);
                policy.mRemovingAdmins.remove(adminReceiver);
                Slog.i(LOG_TAG, "Device admin " + adminReceiver + " removed from user " + userHandle);
                pushUserRestrictions(userHandle);
            }
        }
    }

    public void setDeviceProvisioningConfigApplied() {
        enforceManageUsers();
        synchronized (getLockObject()) {
            getUserData(0).mDeviceProvisioningConfigApplied = true;
            saveSettingsLocked(0);
        }
    }

    public boolean isDeviceProvisioningConfigApplied() {
        boolean z;
        enforceManageUsers();
        synchronized (getLockObject()) {
            z = getUserData(0).mDeviceProvisioningConfigApplied;
        }
        return z;
    }

    public void forceUpdateUserSetupComplete() {
        enforceCanManageProfileAndDeviceOwners();
        enforceCallerSystemUserHandle();
        if (this.mInjector.isBuildDebuggable()) {
            getUserData(0).mUserSetupComplete = this.mInjector.settingsSecureGetIntForUser("user_setup_complete", 0, 0) != 0;
            synchronized (getLockObject()) {
                saveSettingsLocked(0);
            }
        }
    }

    public void setBackupServiceEnabled(ComponentName admin, boolean enabled) {
        if (this.mHasFeature) {
            Preconditions.checkNotNull(admin);
            enforceProfileOrDeviceOwner(admin);
            toggleBackupServiceActive(this.mInjector.userHandleGetCallingUserId(), enabled);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    public boolean isBackupServiceEnabled(ComponentName admin) {
        Preconditions.checkNotNull(admin);
        boolean z = true;
        if (!this.mHasFeature) {
            return true;
        }
        enforceProfileOrDeviceOwner(admin);
        synchronized (getLockObject()) {
            try {
                IBackupManager ibm = this.mInjector.getIBackupManager();
                if (ibm == null || !ibm.isBackupServiceActive(this.mInjector.userHandleGetCallingUserId())) {
                    z = false;
                }
            } catch (RemoteException e) {
                throw new IllegalStateException("Failed requesting backup service state.", e);
            } catch (Throwable th) {
                throw th;
            }
        }
        return z;
    }

    public boolean bindDeviceAdminServiceAsUser(ComponentName admin, IApplicationThread caller, IBinder activtiyToken, Intent serviceIntent, IServiceConnection connection, int flags, int targetUserId) {
        String targetPackage;
        long callingIdentity;
        Intent intent = serviceIntent;
        int i = targetUserId;
        boolean z = false;
        if (!this.mHasFeature) {
            return false;
        }
        Preconditions.checkNotNull(admin);
        Preconditions.checkNotNull(caller);
        Preconditions.checkNotNull(serviceIntent);
        boolean z2 = (serviceIntent.getComponent() == null && serviceIntent.getPackage() == null) ? false : true;
        Preconditions.checkArgument(z2, "Service intent must be explicit (with a package name or component): " + intent);
        Preconditions.checkNotNull(connection);
        Preconditions.checkArgument(this.mInjector.userHandleGetCallingUserId() != i, "target user id must be different from the calling user id");
        if (getBindDeviceAdminTargetUsers(admin).contains(UserHandle.of(targetUserId))) {
            synchronized (getLockObject()) {
                targetPackage = getOwnerPackageNameForUserLocked(i);
            }
            long callingIdentity2 = this.mInjector.binderClearCallingIdentity();
            try {
                if (createCrossUserServiceIntent(intent, targetPackage, i) == null) {
                    this.mInjector.binderRestoreCallingIdentity(callingIdentity2);
                    return false;
                }
                String str = targetPackage;
                callingIdentity = callingIdentity2;
                try {
                    if (this.mInjector.getIActivityManager().bindService(caller, activtiyToken, serviceIntent, intent.resolveTypeIfNeeded(this.mContext.getContentResolver()), connection, flags, this.mContext.getOpPackageName(), targetUserId) != 0) {
                        z = true;
                    }
                    this.mInjector.binderRestoreCallingIdentity(callingIdentity);
                    return z;
                } catch (RemoteException e) {
                    this.mInjector.binderRestoreCallingIdentity(callingIdentity);
                    return false;
                } catch (Throwable th) {
                    th = th;
                    this.mInjector.binderRestoreCallingIdentity(callingIdentity);
                    throw th;
                }
            } catch (RemoteException e2) {
                String str2 = targetPackage;
                callingIdentity = callingIdentity2;
                this.mInjector.binderRestoreCallingIdentity(callingIdentity);
                return false;
            } catch (Throwable th2) {
                th = th2;
                String str3 = targetPackage;
                callingIdentity = callingIdentity2;
                this.mInjector.binderRestoreCallingIdentity(callingIdentity);
                throw th;
            }
        } else {
            throw new SecurityException("Not allowed to bind to target user id");
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 9 */
    public List<UserHandle> getBindDeviceAdminTargetUsers(ComponentName admin) {
        ArrayList<UserHandle> targetUsers;
        if (!this.mHasFeature) {
            return Collections.emptyList();
        }
        Preconditions.checkNotNull(admin);
        synchronized (getLockObject()) {
            getActiveAdminForCallerLocked(admin, -1);
            int callingUserId = this.mInjector.userHandleGetCallingUserId();
            long callingIdentity = this.mInjector.binderClearCallingIdentity();
            try {
                targetUsers = new ArrayList<>();
                if (isDeviceOwner(admin, callingUserId)) {
                    List<UserInfo> userInfos = this.mUserManager.getUsers(true);
                    for (int i = 0; i < userInfos.size(); i++) {
                        int userId = userInfos.get(i).id;
                        if (userId != callingUserId && canUserBindToDeviceOwnerLocked(userId)) {
                            targetUsers.add(UserHandle.of(userId));
                        }
                    }
                } else if (canUserBindToDeviceOwnerLocked(callingUserId)) {
                    targetUsers.add(UserHandle.of(this.mOwners.getDeviceOwnerUserId()));
                }
            } finally {
                this.mInjector.binderRestoreCallingIdentity(callingIdentity);
            }
        }
        return targetUsers;
    }

    private boolean canUserBindToDeviceOwnerLocked(int userId) {
        if (!this.mOwners.hasDeviceOwner() || userId == this.mOwners.getDeviceOwnerUserId() || !this.mOwners.hasProfileOwner(userId) || !TextUtils.equals(this.mOwners.getDeviceOwnerPackageName(), this.mOwners.getProfileOwnerPackage(userId))) {
            return false;
        }
        return isUserAffiliatedWithDeviceLocked(userId);
    }

    /* Debug info: failed to restart local var, previous not found, register: 13 */
    /* JADX WARNING: Code restructure failed: missing block: B:21:?, code lost:
        r5 = new java.lang.String[]{"android.account.DEVICE_OR_PROFILE_OWNER_ALLOWED"};
        r7 = new java.lang.String[]{"android.account.DEVICE_OR_PROFILE_OWNER_DISALLOWED"};
        r8 = true;
        r9 = r4.length;
        r10 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0045, code lost:
        if (r10 >= r9) goto L_0x0092;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0047, code lost:
        r11 = r4[r10];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x004d, code lost:
        if (hasAccountFeatures(r0, r11, r7) == false) goto L_0x006c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x004f, code lost:
        android.util.Log.e(LOG_TAG, r11 + " has " + r7[0]);
        r8 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0070, code lost:
        if (hasAccountFeatures(r0, r11, r5) != false) goto L_0x008f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0072, code lost:
        android.util.Log.e(LOG_TAG, r11 + " doesn't have " + r5[0]);
        r8 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x008f, code lost:
        r10 = r10 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0092, code lost:
        if (r8 == false) goto L_0x009c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0094, code lost:
        android.util.Log.w(LOG_TAG, "All accounts are compatible");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x009c, code lost:
        android.util.Log.e(LOG_TAG, "Found incompatible accounts");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00a3, code lost:
        if (r8 != false) goto L_0x00a6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00a6, code lost:
        r1 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00a7, code lost:
        r13.mInjector.binderRestoreCallingIdentity(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00ac, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean hasIncompatibleAccountsOrNonAdbNoLock(int r14, android.content.ComponentName r15) {
        /*
            r13 = this;
            boolean r0 = r13.isAdb()
            r1 = 1
            if (r0 != 0) goto L_0x0008
            return r1
        L_0x0008:
            r13.wtfIfInLock()
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r0 = r13.mInjector
            long r2 = r0.binderClearCallingIdentity()
            android.content.Context r0 = r13.mContext     // Catch:{ all -> 0x00be }
            android.accounts.AccountManager r0 = android.accounts.AccountManager.get(r0)     // Catch:{ all -> 0x00be }
            android.accounts.Account[] r4 = r0.getAccountsAsUser(r14)     // Catch:{ all -> 0x00be }
            int r5 = r4.length     // Catch:{ all -> 0x00be }
            r6 = 0
            if (r5 != 0) goto L_0x0026
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r1 = r13.mInjector
            r1.binderRestoreCallingIdentity(r2)
            return r6
        L_0x0026:
            java.lang.Object r5 = r13.getLockObject()     // Catch:{ all -> 0x00be }
            monitor-enter(r5)     // Catch:{ all -> 0x00be }
            if (r15 == 0) goto L_0x00ad
            boolean r7 = r13.isAdminTestOnlyLocked(r15, r14)     // Catch:{ all -> 0x00bb }
            if (r7 != 0) goto L_0x0035
            goto L_0x00ad
        L_0x0035:
            monitor-exit(r5)     // Catch:{ all -> 0x00bb }
            java.lang.String r5 = "android.account.DEVICE_OR_PROFILE_OWNER_ALLOWED"
            java.lang.String[] r5 = new java.lang.String[]{r5}     // Catch:{ all -> 0x00be }
            java.lang.String r7 = "android.account.DEVICE_OR_PROFILE_OWNER_DISALLOWED"
            java.lang.String[] r7 = new java.lang.String[]{r7}     // Catch:{ all -> 0x00be }
            r8 = 1
            int r9 = r4.length     // Catch:{ all -> 0x00be }
            r10 = r6
        L_0x0045:
            if (r10 >= r9) goto L_0x0092
            r11 = r4[r10]     // Catch:{ all -> 0x00be }
            boolean r12 = r13.hasAccountFeatures(r0, r11, r7)     // Catch:{ all -> 0x00be }
            if (r12 == 0) goto L_0x006c
            java.lang.String r9 = "DevicePolicyManager"
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x00be }
            r10.<init>()     // Catch:{ all -> 0x00be }
            r10.append(r11)     // Catch:{ all -> 0x00be }
            java.lang.String r12 = " has "
            r10.append(r12)     // Catch:{ all -> 0x00be }
            r12 = r7[r6]     // Catch:{ all -> 0x00be }
            r10.append(r12)     // Catch:{ all -> 0x00be }
            java.lang.String r10 = r10.toString()     // Catch:{ all -> 0x00be }
            android.util.Log.e(r9, r10)     // Catch:{ all -> 0x00be }
            r8 = 0
            goto L_0x0092
        L_0x006c:
            boolean r12 = r13.hasAccountFeatures(r0, r11, r5)     // Catch:{ all -> 0x00be }
            if (r12 != 0) goto L_0x008f
            java.lang.String r9 = "DevicePolicyManager"
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x00be }
            r10.<init>()     // Catch:{ all -> 0x00be }
            r10.append(r11)     // Catch:{ all -> 0x00be }
            java.lang.String r12 = " doesn't have "
            r10.append(r12)     // Catch:{ all -> 0x00be }
            r12 = r5[r6]     // Catch:{ all -> 0x00be }
            r10.append(r12)     // Catch:{ all -> 0x00be }
            java.lang.String r10 = r10.toString()     // Catch:{ all -> 0x00be }
            android.util.Log.e(r9, r10)     // Catch:{ all -> 0x00be }
            r8 = 0
            goto L_0x0092
        L_0x008f:
            int r10 = r10 + 1
            goto L_0x0045
        L_0x0092:
            if (r8 == 0) goto L_0x009c
            java.lang.String r9 = "DevicePolicyManager"
            java.lang.String r10 = "All accounts are compatible"
            android.util.Log.w(r9, r10)     // Catch:{ all -> 0x00be }
            goto L_0x00a3
        L_0x009c:
            java.lang.String r9 = "DevicePolicyManager"
            java.lang.String r10 = "Found incompatible accounts"
            android.util.Log.e(r9, r10)     // Catch:{ all -> 0x00be }
        L_0x00a3:
            if (r8 != 0) goto L_0x00a6
            goto L_0x00a7
        L_0x00a6:
            r1 = r6
        L_0x00a7:
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r6 = r13.mInjector
            r6.binderRestoreCallingIdentity(r2)
            return r1
        L_0x00ad:
            java.lang.String r6 = "DevicePolicyManager"
            java.lang.String r7 = "Non test-only owner can't be installed with existing accounts."
            android.util.Log.w(r6, r7)     // Catch:{ all -> 0x00bb }
            monitor-exit(r5)     // Catch:{ all -> 0x00bb }
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r5 = r13.mInjector
            r5.binderRestoreCallingIdentity(r2)
            return r1
        L_0x00bb:
            r1 = move-exception
            monitor-exit(r5)     // Catch:{ all -> 0x00bb }
            throw r1     // Catch:{ all -> 0x00be }
        L_0x00be:
            r0 = move-exception
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r1 = r13.mInjector
            r1.binderRestoreCallingIdentity(r2)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.DevicePolicyManagerService.hasIncompatibleAccountsOrNonAdbNoLock(int, android.content.ComponentName):boolean");
    }

    private boolean hasAccountFeatures(AccountManager am, Account account, String[] features) {
        try {
            return am.hasFeatures(account, features, (AccountManagerCallback) null, (Handler) null).getResult().booleanValue();
        } catch (Exception e) {
            Log.w(LOG_TAG, "Failed to get account feature", e);
            return false;
        }
    }

    private boolean isAdb() {
        int callingUid = this.mInjector.binderGetCallingUid();
        return callingUid == 2000 || callingUid == 0;
    }

    public void setNetworkLoggingEnabled(ComponentName admin, String packageName, boolean enabled) {
        if (this.mHasFeature) {
            synchronized (getLockObject()) {
                enforceCanManageScope(admin, packageName, -2, "delegation-network-logging");
                if (enabled != isNetworkLoggingEnabledInternalLocked()) {
                    ActiveAdmin deviceOwner = getDeviceOwnerAdminLocked();
                    deviceOwner.isNetworkLoggingEnabled = enabled;
                    int i = 0;
                    if (!enabled) {
                        deviceOwner.numNetworkLoggingNotifications = 0;
                        deviceOwner.lastNetworkLoggingNotificationTimeMs = 0;
                    }
                    saveSettingsLocked(this.mInjector.userHandleGetCallingUserId());
                    setNetworkLoggingActiveInternal(enabled);
                    DevicePolicyEventLogger devicePolicyEventLogger = DevicePolicyEventLogger.createEvent(119).setAdmin(packageName).setBoolean(admin == null);
                    if (enabled) {
                        i = 1;
                    }
                    devicePolicyEventLogger.setInt(i).write();
                }
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    /* access modifiers changed from: private */
    public void setNetworkLoggingActiveInternal(boolean active) {
        synchronized (getLockObject()) {
            long callingIdentity = this.mInjector.binderClearCallingIdentity();
            if (active) {
                try {
                    this.mNetworkLogger = new NetworkLogger(this, this.mInjector.getPackageManagerInternal());
                    if (!this.mNetworkLogger.startNetworkLogging()) {
                        this.mNetworkLogger = null;
                        Slog.wtf(LOG_TAG, "Network logging could not be started due to the logging service not being available yet.");
                    }
                    maybePauseDeviceWideLoggingLocked();
                    sendNetworkLoggingNotificationLocked();
                } catch (Throwable th) {
                    this.mInjector.binderRestoreCallingIdentity(callingIdentity);
                    throw th;
                }
            } else {
                if (this.mNetworkLogger != null && !this.mNetworkLogger.stopNetworkLogging()) {
                    Slog.wtf(LOG_TAG, "Network logging could not be stopped due to the logging service not being available yet.");
                }
                this.mNetworkLogger = null;
                this.mInjector.getNotificationManager().cancel(1002);
            }
            this.mInjector.binderRestoreCallingIdentity(callingIdentity);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    public long forceNetworkLogs() {
        enforceShell("forceNetworkLogs");
        synchronized (getLockObject()) {
            if (!isNetworkLoggingEnabledInternalLocked()) {
                throw new IllegalStateException("logging is not available");
            } else if (this.mNetworkLogger == null) {
                return 0;
            } else {
                long ident = this.mInjector.binderClearCallingIdentity();
                try {
                    long forceBatchFinalization = this.mNetworkLogger.forceBatchFinalization();
                    return forceBatchFinalization;
                } finally {
                    this.mInjector.binderRestoreCallingIdentity(ident);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"getLockObject()"})
    public void maybePauseDeviceWideLoggingLocked() {
        if (!areAllUsersAffiliatedWithDeviceLocked()) {
            Slog.i(LOG_TAG, "There are unaffiliated users, security and network logging will be paused if enabled.");
            this.mSecurityLogMonitor.pause();
            NetworkLogger networkLogger = this.mNetworkLogger;
            if (networkLogger != null) {
                networkLogger.pause();
            }
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"getLockObject()"})
    public void maybeResumeDeviceWideLoggingLocked() {
        if (areAllUsersAffiliatedWithDeviceLocked()) {
            long ident = this.mInjector.binderClearCallingIdentity();
            try {
                this.mSecurityLogMonitor.resume();
                if (this.mNetworkLogger != null) {
                    this.mNetworkLogger.resume();
                }
            } finally {
                this.mInjector.binderRestoreCallingIdentity(ident);
            }
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"getLockObject()"})
    public void discardDeviceWideLogsLocked() {
        this.mSecurityLogMonitor.discardLogs();
        NetworkLogger networkLogger = this.mNetworkLogger;
        if (networkLogger != null) {
            networkLogger.discardLogs();
        }
    }

    public boolean isNetworkLoggingEnabled(ComponentName admin, String packageName) {
        boolean isNetworkLoggingEnabledInternalLocked;
        if (!this.mHasFeature) {
            return false;
        }
        synchronized (getLockObject()) {
            enforceCanManageScopeOrCheckPermission(admin, packageName, -2, "delegation-network-logging", "android.permission.MANAGE_USERS");
            isNetworkLoggingEnabledInternalLocked = isNetworkLoggingEnabledInternalLocked();
        }
        return isNetworkLoggingEnabledInternalLocked;
    }

    /* access modifiers changed from: private */
    public boolean isNetworkLoggingEnabledInternalLocked() {
        ActiveAdmin deviceOwner = getDeviceOwnerAdminLocked();
        return deviceOwner != null && deviceOwner.isNetworkLoggingEnabled;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0052, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<android.app.admin.NetworkEvent> retrieveNetworkLogs(android.content.ComponentName r9, java.lang.String r10, long r11) {
        /*
            r8 = this;
            boolean r0 = r8.mHasFeature
            r1 = 0
            if (r0 != 0) goto L_0x0006
            return r1
        L_0x0006:
            r0 = -2
            java.lang.String r2 = "delegation-network-logging"
            r8.enforceCanManageScope(r9, r10, r0, r2)
            r8.ensureAllUsersAffiliated()
            java.lang.Object r0 = r8.getLockObject()
            monitor-enter(r0)
            com.android.server.devicepolicy.NetworkLogger r2 = r8.mNetworkLogger     // Catch:{ all -> 0x0053 }
            if (r2 == 0) goto L_0x0051
            boolean r2 = r8.isNetworkLoggingEnabledInternalLocked()     // Catch:{ all -> 0x0053 }
            if (r2 != 0) goto L_0x001f
            goto L_0x0051
        L_0x001f:
            r1 = 0
            if (r9 != 0) goto L_0x0024
            r2 = 1
            goto L_0x0025
        L_0x0024:
            r2 = r1
        L_0x0025:
            r3 = 120(0x78, float:1.68E-43)
            android.app.admin.DevicePolicyEventLogger r3 = android.app.admin.DevicePolicyEventLogger.createEvent(r3)     // Catch:{ all -> 0x0053 }
            android.app.admin.DevicePolicyEventLogger r3 = r3.setAdmin(r10)     // Catch:{ all -> 0x0053 }
            android.app.admin.DevicePolicyEventLogger r3 = r3.setBoolean(r2)     // Catch:{ all -> 0x0053 }
            r3.write()     // Catch:{ all -> 0x0053 }
            long r3 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x0053 }
            com.android.server.devicepolicy.DevicePolicyManagerService$DevicePolicyData r5 = r8.getUserData(r1)     // Catch:{ all -> 0x0053 }
            long r6 = r5.mLastNetworkLogsRetrievalTime     // Catch:{ all -> 0x0053 }
            int r6 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1))
            if (r6 <= 0) goto L_0x0049
            r5.mLastNetworkLogsRetrievalTime = r3     // Catch:{ all -> 0x0053 }
            r8.saveSettingsLocked(r1)     // Catch:{ all -> 0x0053 }
        L_0x0049:
            com.android.server.devicepolicy.NetworkLogger r1 = r8.mNetworkLogger     // Catch:{ all -> 0x0053 }
            java.util.List r1 = r1.retrieveLogs(r11)     // Catch:{ all -> 0x0053 }
            monitor-exit(r0)     // Catch:{ all -> 0x0053 }
            return r1
        L_0x0051:
            monitor-exit(r0)     // Catch:{ all -> 0x0053 }
            return r1
        L_0x0053:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0053 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.DevicePolicyManagerService.retrieveNetworkLogs(android.content.ComponentName, java.lang.String, long):java.util.List");
    }

    private void sendNetworkLoggingNotificationLocked() {
        ActiveAdmin deviceOwner = getDeviceOwnerAdminLocked();
        if (deviceOwner != null && deviceOwner.isNetworkLoggingEnabled && deviceOwner.numNetworkLoggingNotifications < 2) {
            long now = System.currentTimeMillis();
            if (now - deviceOwner.lastNetworkLoggingNotificationTimeMs >= MS_PER_DAY) {
                deviceOwner.numNetworkLoggingNotifications++;
                if (deviceOwner.numNetworkLoggingNotifications >= 2) {
                    deviceOwner.lastNetworkLoggingNotificationTimeMs = 0;
                } else {
                    deviceOwner.lastNetworkLoggingNotificationTimeMs = now;
                }
                Intent intent = new Intent("android.app.action.SHOW_DEVICE_MONITORING_DIALOG");
                intent.setPackage(AccessController.PACKAGE_SYSTEMUI);
                this.mInjector.getNotificationManager().notify(1002, new Notification.Builder(this.mContext, SystemNotificationChannels.DEVICE_ADMIN).setSmallIcon(17302436).setContentTitle(this.mContext.getString(17040524)).setContentText(this.mContext.getString(17040523)).setTicker(this.mContext.getString(17040524)).setShowWhen(true).setContentIntent(PendingIntent.getBroadcastAsUser(this.mContext, 0, intent, 0, UserHandle.CURRENT)).setStyle(new Notification.BigTextStyle().bigText(this.mContext.getString(17040523))).build());
                saveSettingsLocked(this.mOwners.getDeviceOwnerUserId());
            }
        }
    }

    private String getOwnerPackageNameForUserLocked(int userId) {
        if (this.mOwners.getDeviceOwnerUserId() == userId) {
            return this.mOwners.getDeviceOwnerPackageName();
        }
        return this.mOwners.getProfileOwnerPackage(userId);
    }

    private Intent createCrossUserServiceIntent(Intent rawIntent, String expectedPackageName, int targetUserId) throws RemoteException, SecurityException {
        ResolveInfo info = this.mIPackageManager.resolveService(rawIntent, rawIntent.resolveTypeIfNeeded(this.mContext.getContentResolver()), 0, targetUserId);
        if (info == null || info.serviceInfo == null) {
            Log.e(LOG_TAG, "Fail to look up the service: " + rawIntent + " or user " + targetUserId + " is not running");
            return null;
        } else if (!expectedPackageName.equals(info.serviceInfo.packageName)) {
            throw new SecurityException("Only allow to bind service in " + expectedPackageName);
        } else if (!info.serviceInfo.exported || "android.permission.BIND_DEVICE_ADMIN".equals(info.serviceInfo.permission)) {
            rawIntent.setComponent(info.serviceInfo.getComponentName());
            return rawIntent;
        } else {
            throw new SecurityException("Service must be protected by BIND_DEVICE_ADMIN permission");
        }
    }

    public long getLastSecurityLogRetrievalTime() {
        enforceDeviceOwnerOrManageUsers();
        return getUserData(0).mLastSecurityLogRetrievalTime;
    }

    public long getLastBugReportRequestTime() {
        enforceDeviceOwnerOrManageUsers();
        return getUserData(0).mLastBugReportRequestTime;
    }

    public long getLastNetworkLogRetrievalTime() {
        enforceDeviceOwnerOrManageUsers();
        return getUserData(0).mLastNetworkLogsRetrievalTime;
    }

    /* Debug info: failed to restart local var, previous not found, register: 12 */
    public boolean setResetPasswordToken(ComponentName admin, byte[] token) {
        boolean z = false;
        if (!this.mHasFeature || !this.mLockPatternUtils.hasSecureLockScreen()) {
            return false;
        }
        if (token == null || token.length < 32) {
            throw new IllegalArgumentException("token must be at least 32-byte long");
        }
        synchronized (getLockObject()) {
            int userHandle = this.mInjector.userHandleGetCallingUserId();
            getActiveAdminForCallerLocked(admin, -1);
            DevicePolicyData policy = getUserData(userHandle);
            long ident = this.mInjector.binderClearCallingIdentity();
            try {
                if (policy.mPasswordTokenHandle != 0) {
                    this.mLockPatternUtils.removeEscrowToken(policy.mPasswordTokenHandle, userHandle);
                }
                policy.mPasswordTokenHandle = this.mLockPatternUtils.addEscrowToken(token, userHandle, (LockPatternUtils.EscrowTokenStateChangeCallback) null);
                saveSettingsLocked(userHandle);
                if (policy.mPasswordTokenHandle != 0) {
                    z = true;
                }
            } finally {
                this.mInjector.binderRestoreCallingIdentity(ident);
            }
        }
        return z;
    }

    /* Debug info: failed to restart local var, previous not found, register: 10 */
    public boolean clearResetPasswordToken(ComponentName admin) {
        if (!this.mHasFeature || !this.mLockPatternUtils.hasSecureLockScreen()) {
            return false;
        }
        synchronized (getLockObject()) {
            int userHandle = this.mInjector.userHandleGetCallingUserId();
            getActiveAdminForCallerLocked(admin, -1);
            DevicePolicyData policy = getUserData(userHandle);
            if (policy.mPasswordTokenHandle == 0) {
                return false;
            }
            long ident = this.mInjector.binderClearCallingIdentity();
            try {
                boolean result = this.mLockPatternUtils.removeEscrowToken(policy.mPasswordTokenHandle, userHandle);
                policy.mPasswordTokenHandle = 0;
                saveSettingsLocked(userHandle);
                return result;
            } finally {
                this.mInjector.binderRestoreCallingIdentity(ident);
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    public boolean isResetPasswordTokenActive(ComponentName admin) {
        if (!this.mHasFeature || !this.mLockPatternUtils.hasSecureLockScreen()) {
            return false;
        }
        synchronized (getLockObject()) {
            int userHandle = this.mInjector.userHandleGetCallingUserId();
            getActiveAdminForCallerLocked(admin, -1);
            DevicePolicyData policy = getUserData(userHandle);
            if (policy.mPasswordTokenHandle == 0) {
                return false;
            }
            long ident = this.mInjector.binderClearCallingIdentity();
            try {
                boolean isEscrowTokenActive = this.mLockPatternUtils.isEscrowTokenActive(policy.mPasswordTokenHandle, userHandle);
                return isEscrowTokenActive;
            } finally {
                this.mInjector.binderRestoreCallingIdentity(ident);
            }
        }
    }

    public boolean resetPasswordWithToken(ComponentName admin, String passwordOrNull, byte[] token, int flags) {
        if (!this.mHasFeature) {
            ComponentName componentName = admin;
        } else if (!this.mLockPatternUtils.hasSecureLockScreen()) {
            ComponentName componentName2 = admin;
        } else {
            Preconditions.checkNotNull(token);
            synchronized (getLockObject()) {
                try {
                    int userHandle = this.mInjector.userHandleGetCallingUserId();
                    ComponentName componentName3 = admin;
                    getActiveAdminForCallerLocked(admin, -1);
                    DevicePolicyData policy = getUserData(userHandle);
                    if (policy.mPasswordTokenHandle != 0) {
                        boolean resetPasswordInternal = resetPasswordInternal(passwordOrNull != null ? passwordOrNull : "", policy.mPasswordTokenHandle, token, flags, this.mInjector.binderGetCallingUid(), userHandle);
                        return resetPasswordInternal;
                    }
                    Slog.w(LOG_TAG, "No saved token handle");
                    return false;
                } catch (Throwable th) {
                    th = th;
                    throw th;
                }
            }
        }
        return false;
    }

    public boolean isCurrentInputMethodSetByOwner() {
        enforceProfileOwnerOrSystemUser();
        return getUserData(this.mInjector.userHandleGetCallingUserId()).mCurrentInputMethodSet;
    }

    public StringParceledListSlice getOwnerInstalledCaCerts(UserHandle user) {
        StringParceledListSlice stringParceledListSlice;
        int userId = user.getIdentifier();
        enforceProfileOwnerOrFullCrossUsersPermission(userId);
        synchronized (getLockObject()) {
            stringParceledListSlice = new StringParceledListSlice(new ArrayList(getUserData(userId).mOwnerInstalledCaCerts));
        }
        return stringParceledListSlice;
    }

    public void clearApplicationUserData(ComponentName admin, String packageName, IPackageDataObserver callback) {
        Preconditions.checkNotNull(admin, "ComponentName is null");
        Preconditions.checkNotNull(packageName, "packageName is null");
        Preconditions.checkNotNull(callback, "callback is null");
        enforceProfileOrDeviceOwner(admin);
        int userId = UserHandle.getCallingUserId();
        long ident = this.mInjector.binderClearCallingIdentity();
        try {
            ActivityManager.getService().clearApplicationUserData(packageName, false, callback, userId);
        } catch (RemoteException e) {
        } catch (SecurityException se) {
            Slog.w(LOG_TAG, "Not allowed to clear application user data for package " + packageName, se);
            try {
                callback.onRemoveCompleted(packageName, false);
            } catch (RemoteException e2) {
            }
        } catch (Throwable th) {
            this.mInjector.binderRestoreCallingIdentity(ident);
            throw th;
        }
        this.mInjector.binderRestoreCallingIdentity(ident);
    }

    public void setLogoutEnabled(ComponentName admin, boolean enabled) {
        if (this.mHasFeature) {
            Preconditions.checkNotNull(admin);
            synchronized (getLockObject()) {
                ActiveAdmin deviceOwner = getActiveAdminForCallerLocked(admin, -2);
                if (deviceOwner.isLogoutEnabled != enabled) {
                    deviceOwner.isLogoutEnabled = enabled;
                    saveSettingsLocked(this.mInjector.userHandleGetCallingUserId());
                }
            }
        }
    }

    public boolean isLogoutEnabled() {
        boolean z = false;
        if (!this.mHasFeature) {
            return false;
        }
        synchronized (getLockObject()) {
            ActiveAdmin deviceOwner = getDeviceOwnerAdminLocked();
            if (deviceOwner != null && deviceOwner.isLogoutEnabled) {
                z = true;
            }
        }
        return z;
    }

    public List<String> getDisallowedSystemApps(ComponentName admin, int userId, String provisioningAction) throws RemoteException {
        enforceCanManageProfileAndDeviceOwners();
        return new ArrayList(this.mOverlayPackagesProvider.getNonRequiredApps(admin, userId, provisioningAction));
    }

    /* Debug info: failed to restart local var, previous not found, register: 19 */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00d1, code lost:
        r7.mInjector.binderRestoreCallingIdentity(r13);
        android.app.admin.DevicePolicyEventLogger.createEvent(58).setAdmin(r8).setStrings(new java.lang.String[]{r21.getPackageName(), r16}).write();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00f4, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void transferOwnership(android.content.ComponentName r20, android.content.ComponentName r21, android.os.PersistableBundle r22) {
        /*
            r19 = this;
            r7 = r19
            r8 = r20
            r9 = r21
            boolean r0 = r7.mHasFeature
            if (r0 != 0) goto L_0x000b
            return
        L_0x000b:
            java.lang.String r0 = "Admin cannot be null."
            com.android.internal.util.Preconditions.checkNotNull(r8, r0)
            java.lang.String r0 = "Target cannot be null."
            com.android.internal.util.Preconditions.checkNotNull(r9, r0)
            r19.enforceProfileOrDeviceOwner(r20)
            boolean r0 = r20.equals(r21)
            if (r0 != 0) goto L_0x011e
            java.lang.String r0 = r20.getPackageName()
            java.lang.String r1 = r21.getPackageName()
            boolean r0 = r0.equals(r1)
            if (r0 != 0) goto L_0x0116
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r0 = r7.mInjector
            int r10 = r0.userHandleGetCallingUserId()
            com.android.server.devicepolicy.DevicePolicyManagerService$DevicePolicyData r11 = r7.getUserData(r10)
            r0 = 1
            android.app.admin.DeviceAdminInfo r12 = r7.findAdmin(r9, r10, r0)
            r7.checkActiveAdminPrecondition(r9, r12, r11)
            boolean r1 = r12.supportsTransferOwnership()
            if (r1 == 0) goto L_0x010e
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r1 = r7.mInjector
            long r13 = r1.binderClearCallingIdentity()
            r1 = 0
            java.lang.Object r15 = r19.getLockObject()     // Catch:{ all -> 0x0103 }
            monitor-enter(r15)     // Catch:{ all -> 0x0103 }
            if (r22 != 0) goto L_0x0060
            android.os.PersistableBundle r2 = new android.os.PersistableBundle     // Catch:{ all -> 0x0059 }
            r2.<init>()     // Catch:{ all -> 0x0059 }
            r6 = r2
            goto L_0x0062
        L_0x0059:
            r0 = move-exception
            r18 = r11
            r11 = r22
            goto L_0x00fd
        L_0x0060:
            r6 = r22
        L_0x0062:
            boolean r2 = r7.isProfileOwner(r8, r10)     // Catch:{ all -> 0x00f9 }
            if (r2 == 0) goto L_0x00a2
            java.lang.String r2 = "profile-owner"
            r16 = r2
            java.lang.String r17 = "profile-owner"
            r1 = r19
            r2 = r20
            r3 = r21
            r4 = r6
            r5 = r10
            r18 = r11
            r11 = r6
            r6 = r17
            r1.prepareTransfer(r2, r3, r4, r5, r6)     // Catch:{ all -> 0x00f5 }
            r7.transferProfileOwnershipLocked(r8, r9, r10)     // Catch:{ all -> 0x00f5 }
            java.lang.String r1 = "android.app.action.TRANSFER_OWNERSHIP_COMPLETE"
            android.os.Bundle r2 = r7.getTransferOwnershipAdminExtras(r11)     // Catch:{ all -> 0x00f5 }
            r7.sendProfileOwnerCommand(r1, r2, r10)     // Catch:{ all -> 0x00f5 }
            java.lang.String r1 = "android.app.action.PROFILE_OWNER_CHANGED"
            r7.postTransfer(r1, r10)     // Catch:{ all -> 0x00f5 }
            boolean r1 = r7.isUserAffiliatedWithDeviceLocked(r10)     // Catch:{ all -> 0x00f5 }
            if (r1 == 0) goto L_0x00d0
            r7.notifyAffiliatedProfileTransferOwnershipComplete(r10)     // Catch:{ all -> 0x00f5 }
            goto L_0x00d0
        L_0x009b:
            r0 = move-exception
            r18 = r11
            r11 = r6
            r1 = r16
            goto L_0x00fd
        L_0x00a2:
            r18 = r11
            r11 = r6
            boolean r2 = r7.isDeviceOwner(r8, r10)     // Catch:{ all -> 0x0101 }
            if (r2 == 0) goto L_0x00ce
            java.lang.String r2 = "device-owner"
            r16 = r2
            java.lang.String r6 = "device-owner"
            r1 = r19
            r2 = r20
            r3 = r21
            r4 = r11
            r5 = r10
            r1.prepareTransfer(r2, r3, r4, r5, r6)     // Catch:{ all -> 0x00f5 }
            r7.transferDeviceOwnershipLocked(r8, r9, r10)     // Catch:{ all -> 0x00f5 }
            java.lang.String r1 = "android.app.action.TRANSFER_OWNERSHIP_COMPLETE"
            android.os.Bundle r2 = r7.getTransferOwnershipAdminExtras(r11)     // Catch:{ all -> 0x00f5 }
            r7.sendDeviceOwnerCommand(r1, r2)     // Catch:{ all -> 0x00f5 }
            java.lang.String r1 = "android.app.action.DEVICE_OWNER_CHANGED"
            r7.postTransfer(r1, r10)     // Catch:{ all -> 0x00f5 }
            goto L_0x00d0
        L_0x00ce:
            r16 = r1
        L_0x00d0:
            monitor-exit(r15)     // Catch:{ all -> 0x00f5 }
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r1 = r7.mInjector
            r1.binderRestoreCallingIdentity(r13)
            r1 = 58
            android.app.admin.DevicePolicyEventLogger r1 = android.app.admin.DevicePolicyEventLogger.createEvent(r1)
            android.app.admin.DevicePolicyEventLogger r1 = r1.setAdmin(r8)
            r2 = 2
            java.lang.String[] r2 = new java.lang.String[r2]
            r3 = 0
            java.lang.String r4 = r21.getPackageName()
            r2[r3] = r4
            r2[r0] = r16
            android.app.admin.DevicePolicyEventLogger r0 = r1.setStrings(r2)
            r0.write()
            return
        L_0x00f5:
            r0 = move-exception
            r1 = r16
            goto L_0x00fd
        L_0x00f9:
            r0 = move-exception
            r18 = r11
            r11 = r6
        L_0x00fd:
            monitor-exit(r15)     // Catch:{ all -> 0x0101 }
            throw r0     // Catch:{ all -> 0x00ff }
        L_0x00ff:
            r0 = move-exception
            goto L_0x0108
        L_0x0101:
            r0 = move-exception
            goto L_0x00fd
        L_0x0103:
            r0 = move-exception
            r18 = r11
            r11 = r22
        L_0x0108:
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r2 = r7.mInjector
            r2.binderRestoreCallingIdentity(r13)
            throw r0
        L_0x010e:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.String r1 = "Provided target does not support ownership transfer."
            r0.<init>(r1)
            throw r0
        L_0x0116:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.String r1 = "Provided administrator and target have the same package name."
            r0.<init>(r1)
            throw r0
        L_0x011e:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.String r1 = "Provided administrator and target are the same object."
            r0.<init>(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.DevicePolicyManagerService.transferOwnership(android.content.ComponentName, android.content.ComponentName, android.os.PersistableBundle):void");
    }

    private void prepareTransfer(ComponentName admin, ComponentName target, PersistableBundle bundle, int callingUserId, String adminType) {
        saveTransferOwnershipBundleLocked(bundle, callingUserId);
        this.mTransferOwnershipMetadataManager.saveMetadataFile(new TransferOwnershipMetadataManager.Metadata(admin, target, callingUserId, adminType));
    }

    private void postTransfer(String broadcast, int callingUserId) {
        deleteTransferOwnershipMetadataFileLocked();
        sendOwnerChangedBroadcast(broadcast, callingUserId);
    }

    private void notifyAffiliatedProfileTransferOwnershipComplete(int callingUserId) {
        Bundle extras = new Bundle();
        extras.putParcelable("android.intent.extra.USER", UserHandle.of(callingUserId));
        sendDeviceOwnerCommand("android.app.action.AFFILIATED_PROFILE_TRANSFER_OWNERSHIP_COMPLETE", extras);
    }

    private void transferProfileOwnershipLocked(ComponentName admin, ComponentName target, int profileOwnerUserId) {
        transferActiveAdminUncheckedLocked(target, admin, profileOwnerUserId);
        this.mOwners.transferProfileOwner(target, profileOwnerUserId);
        Slog.i(LOG_TAG, "Profile owner set: " + target + " on user " + profileOwnerUserId);
        this.mOwners.writeProfileOwner(profileOwnerUserId);
        this.mDeviceAdminServiceController.startServiceForOwner(target.getPackageName(), profileOwnerUserId, "transfer-profile-owner");
    }

    private void transferDeviceOwnershipLocked(ComponentName admin, ComponentName target, int userId) {
        transferActiveAdminUncheckedLocked(target, admin, userId);
        this.mOwners.transferDeviceOwnership(target);
        Slog.i(LOG_TAG, "Device owner set: " + target + " on user " + userId);
        this.mOwners.writeDeviceOwner();
        this.mDeviceAdminServiceController.startServiceForOwner(target.getPackageName(), userId, "transfer-device-owner");
    }

    private Bundle getTransferOwnershipAdminExtras(PersistableBundle bundle) {
        Bundle extras = new Bundle();
        if (bundle != null) {
            extras.putParcelable("android.app.extra.TRANSFER_OWNERSHIP_ADMIN_EXTRAS_BUNDLE", bundle);
        }
        return extras;
    }

    public void setStartUserSessionMessage(ComponentName admin, CharSequence startUserSessionMessage) {
        if (this.mHasFeature) {
            Preconditions.checkNotNull(admin);
            String startUserSessionMessageString = startUserSessionMessage != null ? startUserSessionMessage.toString() : null;
            synchronized (getLockObject()) {
                ActiveAdmin deviceOwner = getActiveAdminForCallerLocked(admin, -2);
                if (!TextUtils.equals(deviceOwner.startUserSessionMessage, startUserSessionMessage)) {
                    deviceOwner.startUserSessionMessage = startUserSessionMessageString;
                    saveSettingsLocked(this.mInjector.userHandleGetCallingUserId());
                    this.mInjector.getActivityManagerInternal().setSwitchingFromSystemUserMessage(startUserSessionMessageString);
                }
            }
        }
    }

    public void setEndUserSessionMessage(ComponentName admin, CharSequence endUserSessionMessage) {
        if (this.mHasFeature) {
            Preconditions.checkNotNull(admin);
            String endUserSessionMessageString = endUserSessionMessage != null ? endUserSessionMessage.toString() : null;
            synchronized (getLockObject()) {
                ActiveAdmin deviceOwner = getActiveAdminForCallerLocked(admin, -2);
                if (!TextUtils.equals(deviceOwner.endUserSessionMessage, endUserSessionMessage)) {
                    deviceOwner.endUserSessionMessage = endUserSessionMessageString;
                    saveSettingsLocked(this.mInjector.userHandleGetCallingUserId());
                    this.mInjector.getActivityManagerInternal().setSwitchingToSystemUserMessage(endUserSessionMessageString);
                }
            }
        }
    }

    public String getStartUserSessionMessage(ComponentName admin) {
        String str;
        if (!this.mHasFeature) {
            return null;
        }
        Preconditions.checkNotNull(admin);
        synchronized (getLockObject()) {
            str = getActiveAdminForCallerLocked(admin, -2).startUserSessionMessage;
        }
        return str;
    }

    public String getEndUserSessionMessage(ComponentName admin) {
        String str;
        if (!this.mHasFeature) {
            return null;
        }
        Preconditions.checkNotNull(admin);
        synchronized (getLockObject()) {
            str = getActiveAdminForCallerLocked(admin, -2).endUserSessionMessage;
        }
        return str;
    }

    private void deleteTransferOwnershipMetadataFileLocked() {
        this.mTransferOwnershipMetadataManager.deleteMetadataFile();
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0040, code lost:
        r6 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:?, code lost:
        $closeResource(r5, r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0044, code lost:
        throw r6;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.os.PersistableBundle getTransferOwnershipBundle() {
        /*
            r8 = this;
            java.lang.Object r0 = r8.getLockObject()
            monitor-enter(r0)
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r1 = r8.mInjector     // Catch:{ all -> 0x005e }
            int r1 = r1.userHandleGetCallingUserId()     // Catch:{ all -> 0x005e }
            r2 = -1
            r3 = 0
            r8.getActiveAdminForCallerLocked(r3, r2)     // Catch:{ all -> 0x005e }
            java.io.File r2 = new java.io.File     // Catch:{ all -> 0x005e }
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r4 = r8.mInjector     // Catch:{ all -> 0x005e }
            java.io.File r4 = r4.environmentGetUserSystemDirectory(r1)     // Catch:{ all -> 0x005e }
            java.lang.String r5 = "transfer-ownership-parameters.xml"
            r2.<init>(r4, r5)     // Catch:{ all -> 0x005e }
            boolean r4 = r2.exists()     // Catch:{ all -> 0x005e }
            if (r4 != 0) goto L_0x0026
            monitor-exit(r0)     // Catch:{ all -> 0x005e }
            return r3
        L_0x0026:
            java.io.FileInputStream r4 = new java.io.FileInputStream     // Catch:{ IOException | IllegalArgumentException | XmlPullParserException -> 0x0045 }
            r4.<init>(r2)     // Catch:{ IOException | IllegalArgumentException | XmlPullParserException -> 0x0045 }
            org.xmlpull.v1.XmlPullParser r5 = android.util.Xml.newPullParser()     // Catch:{ all -> 0x003e }
            r5.setInput(r4, r3)     // Catch:{ all -> 0x003e }
            r5.next()     // Catch:{ all -> 0x003e }
            android.os.PersistableBundle r6 = android.os.PersistableBundle.restoreFromXml(r5)     // Catch:{ all -> 0x003e }
            $closeResource(r3, r4)     // Catch:{ IOException | IllegalArgumentException | XmlPullParserException -> 0x0045 }
            monitor-exit(r0)     // Catch:{ all -> 0x005e }
            return r6
        L_0x003e:
            r5 = move-exception
            throw r5     // Catch:{ all -> 0x0040 }
        L_0x0040:
            r6 = move-exception
            $closeResource(r5, r4)     // Catch:{ IOException | IllegalArgumentException | XmlPullParserException -> 0x0045 }
            throw r6     // Catch:{ IOException | IllegalArgumentException | XmlPullParserException -> 0x0045 }
        L_0x0045:
            r4 = move-exception
            java.lang.String r5 = "DevicePolicyManager"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x005e }
            r6.<init>()     // Catch:{ all -> 0x005e }
            java.lang.String r7 = "Caught exception while trying to load the owner transfer parameters from file "
            r6.append(r7)     // Catch:{ all -> 0x005e }
            r6.append(r2)     // Catch:{ all -> 0x005e }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x005e }
            android.util.Slog.e(r5, r6, r4)     // Catch:{ all -> 0x005e }
            monitor-exit(r0)     // Catch:{ all -> 0x005e }
            return r3
        L_0x005e:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x005e }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.DevicePolicyManagerService.getTransferOwnershipBundle():android.os.PersistableBundle");
    }

    public int addOverrideApn(ComponentName who, ApnSetting apnSetting) {
        if (!this.mHasFeature || !this.mHasTelephonyFeature) {
            return -1;
        }
        Preconditions.checkNotNull(who, "ComponentName is null in addOverrideApn");
        Preconditions.checkNotNull(apnSetting, "ApnSetting is null in addOverrideApn");
        enforceDeviceOwner(who);
        long id = this.mInjector.binderClearCallingIdentity();
        try {
            Uri resultUri = this.mContext.getContentResolver().insert(Telephony.Carriers.DPC_URI, apnSetting.toContentValues());
            if (resultUri == null) {
                return -1;
            }
            try {
                return Integer.parseInt(resultUri.getLastPathSegment());
            } catch (NumberFormatException e) {
                Slog.e(LOG_TAG, "Failed to parse inserted override APN id.", e);
                return -1;
            }
        } finally {
            this.mInjector.binderRestoreCallingIdentity(id);
        }
    }

    public boolean updateOverrideApn(ComponentName who, int apnId, ApnSetting apnSetting) {
        boolean z = false;
        if (!this.mHasFeature || !this.mHasTelephonyFeature) {
            return false;
        }
        Preconditions.checkNotNull(who, "ComponentName is null in updateOverrideApn");
        Preconditions.checkNotNull(apnSetting, "ApnSetting is null in updateOverrideApn");
        enforceDeviceOwner(who);
        if (apnId < 0) {
            return false;
        }
        long id = this.mInjector.binderClearCallingIdentity();
        try {
            if (this.mContext.getContentResolver().update(Uri.withAppendedPath(Telephony.Carriers.DPC_URI, Integer.toString(apnId)), apnSetting.toContentValues(), (String) null, (String[]) null) > 0) {
                z = true;
            }
            return z;
        } finally {
            this.mInjector.binderRestoreCallingIdentity(id);
        }
    }

    public boolean removeOverrideApn(ComponentName who, int apnId) {
        if (!this.mHasFeature || !this.mHasTelephonyFeature) {
            return false;
        }
        Preconditions.checkNotNull(who, "ComponentName is null in removeOverrideApn");
        enforceDeviceOwner(who);
        return removeOverrideApnUnchecked(apnId);
    }

    private boolean removeOverrideApnUnchecked(int apnId) {
        if (apnId < 0) {
            return false;
        }
        long id = this.mInjector.binderClearCallingIdentity();
        try {
            if (this.mContext.getContentResolver().delete(Uri.withAppendedPath(Telephony.Carriers.DPC_URI, Integer.toString(apnId)), (String) null, (String[]) null) > 0) {
                return true;
            }
            return false;
        } finally {
            this.mInjector.binderRestoreCallingIdentity(id);
        }
    }

    public List<ApnSetting> getOverrideApns(ComponentName who) {
        if (!this.mHasFeature || !this.mHasTelephonyFeature) {
            return Collections.emptyList();
        }
        Preconditions.checkNotNull(who, "ComponentName is null in getOverrideApns");
        enforceDeviceOwner(who);
        return getOverrideApnsUnchecked();
    }

    private List<ApnSetting> getOverrideApnsUnchecked() {
        long id = this.mInjector.binderClearCallingIdentity();
        try {
            Cursor cursor = this.mContext.getContentResolver().query(Telephony.Carriers.DPC_URI, (String[]) null, (String) null, (String[]) null, (String) null);
            if (cursor == null) {
                return Collections.emptyList();
            }
            try {
                List<ApnSetting> apnList = new ArrayList<>();
                cursor.moveToPosition(-1);
                while (cursor.moveToNext()) {
                    apnList.add(ApnSetting.makeApnSetting(cursor));
                }
                return apnList;
            } finally {
                cursor.close();
            }
        } finally {
            this.mInjector.binderRestoreCallingIdentity(id);
        }
    }

    public void setOverrideApnsEnabled(ComponentName who, boolean enabled) {
        if (this.mHasFeature && this.mHasTelephonyFeature) {
            Preconditions.checkNotNull(who, "ComponentName is null in setOverrideApnEnabled");
            enforceDeviceOwner(who);
            setOverrideApnsEnabledUnchecked(enabled);
        }
    }

    private void setOverrideApnsEnabledUnchecked(boolean enabled) {
        ContentValues value = new ContentValues();
        value.put("enforced", Boolean.valueOf(enabled));
        long id = this.mInjector.binderClearCallingIdentity();
        try {
            this.mContext.getContentResolver().update(Telephony.Carriers.ENFORCE_MANAGED_URI, value, (String) null, (String[]) null);
        } finally {
            this.mInjector.binderRestoreCallingIdentity(id);
        }
    }

    public boolean isOverrideApnEnabled(ComponentName who) {
        boolean z = false;
        if (!this.mHasFeature || !this.mHasTelephonyFeature) {
            return false;
        }
        Preconditions.checkNotNull(who, "ComponentName is null in isOverrideApnEnabled");
        enforceDeviceOwner(who);
        long id = this.mInjector.binderClearCallingIdentity();
        try {
            Cursor enforceCursor = this.mContext.getContentResolver().query(Telephony.Carriers.ENFORCE_MANAGED_URI, (String[]) null, (String) null, (String[]) null, (String) null);
            if (enforceCursor == null) {
                return false;
            }
            try {
                if (enforceCursor.moveToFirst()) {
                    if (enforceCursor.getInt(enforceCursor.getColumnIndex("enforced")) == 1) {
                        z = true;
                    }
                    enforceCursor.close();
                    return z;
                }
            } catch (IllegalArgumentException e) {
                Slog.e(LOG_TAG, "Cursor returned from ENFORCE_MANAGED_URI doesn't contain correct info.", e);
            } catch (Throwable th) {
                enforceCursor.close();
                throw th;
            }
            enforceCursor.close();
            return false;
        } finally {
            this.mInjector.binderRestoreCallingIdentity(id);
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void saveTransferOwnershipBundleLocked(PersistableBundle bundle, int userId) {
        File parametersFile = new File(this.mInjector.environmentGetUserSystemDirectory(userId), TRANSFER_OWNERSHIP_PARAMETERS_XML);
        AtomicFile atomicFile = new AtomicFile(parametersFile);
        FileOutputStream stream = null;
        try {
            stream = atomicFile.startWrite();
            XmlSerializer serializer = new FastXmlSerializer();
            serializer.setOutput(stream, StandardCharsets.UTF_8.name());
            serializer.startDocument((String) null, true);
            serializer.startTag((String) null, TAG_TRANSFER_OWNERSHIP_BUNDLE);
            bundle.saveToXml(serializer);
            serializer.endTag((String) null, TAG_TRANSFER_OWNERSHIP_BUNDLE);
            serializer.endDocument();
            atomicFile.finishWrite(stream);
        } catch (IOException | XmlPullParserException e) {
            Slog.e(LOG_TAG, "Caught exception while trying to save the owner transfer parameters to file " + parametersFile, e);
            parametersFile.delete();
            atomicFile.failWrite(stream);
        }
    }

    /* access modifiers changed from: package-private */
    public void deleteTransferOwnershipBundleLocked(int userId) {
        new File(this.mInjector.environmentGetUserSystemDirectory(userId), TRANSFER_OWNERSHIP_PARAMETERS_XML).delete();
    }

    private void maybeLogPasswordComplexitySet(ComponentName who, int userId, boolean parent, PasswordMetrics metrics) {
        if (SecurityLog.isLoggingEnabled()) {
            SecurityLog.writeEvent(210017, new Object[]{who.getPackageName(), Integer.valueOf(userId), Integer.valueOf(parent ? getProfileParentId(userId) : userId), Integer.valueOf(metrics.length), Integer.valueOf(metrics.quality), Integer.valueOf(metrics.letters), Integer.valueOf(metrics.nonLetter), Integer.valueOf(metrics.numeric), Integer.valueOf(metrics.upperCase), Integer.valueOf(metrics.lowerCase), Integer.valueOf(metrics.symbols)});
        }
    }

    /* access modifiers changed from: private */
    public static String getManagedProvisioningPackage(Context context) {
        return context.getResources().getString(17039774);
    }

    private void putPrivateDnsSettings(String mode, String host) {
        long origId = this.mInjector.binderClearCallingIdentity();
        try {
            this.mInjector.settingsGlobalPutString("private_dns_mode", mode);
            this.mInjector.settingsGlobalPutString("private_dns_specifier", host);
        } finally {
            this.mInjector.binderRestoreCallingIdentity(origId);
        }
    }

    public int setGlobalPrivateDns(ComponentName who, int mode, String privateDnsHost) {
        if (!this.mHasFeature) {
            return 2;
        }
        Preconditions.checkNotNull(who, "ComponentName is null");
        enforceDeviceOwner(who);
        if (mode != 2) {
            if (mode != 3) {
                throw new IllegalArgumentException(String.format("Provided mode, %d, is not a valid mode.", new Object[]{Integer.valueOf(mode)}));
            } else if (TextUtils.isEmpty(privateDnsHost) || !NetworkUtils.isWeaklyValidatedHostname(privateDnsHost)) {
                throw new IllegalArgumentException(String.format("Provided hostname %s is not valid", new Object[]{privateDnsHost}));
            } else {
                putPrivateDnsSettings("hostname", privateDnsHost);
                return 0;
            }
        } else if (TextUtils.isEmpty(privateDnsHost)) {
            putPrivateDnsSettings("opportunistic", (String) null);
            return 0;
        } else {
            throw new IllegalArgumentException("Host provided for opportunistic mode, but is not needed.");
        }
    }

    public int getGlobalPrivateDnsMode(ComponentName who) {
        if (!this.mHasFeature) {
            return 0;
        }
        Preconditions.checkNotNull(who, "ComponentName is null");
        enforceDeviceOwner(who);
        String currentMode = this.mInjector.settingsGlobalGetString("private_dns_mode");
        if (currentMode == null) {
            currentMode = "off";
        }
        char c = 65535;
        int hashCode = currentMode.hashCode();
        if (hashCode != -539229175) {
            if (hashCode != -299803597) {
                if (hashCode == 109935 && currentMode.equals("off")) {
                    c = 0;
                }
            } else if (currentMode.equals("hostname")) {
                c = 2;
            }
        } else if (currentMode.equals("opportunistic")) {
            c = 1;
        }
        if (c == 0) {
            return 1;
        }
        if (c == 1) {
            return 2;
        }
        if (c != 2) {
            return 0;
        }
        return 3;
    }

    public String getGlobalPrivateDnsHost(ComponentName who) {
        if (!this.mHasFeature) {
            return null;
        }
        Preconditions.checkNotNull(who, "ComponentName is null");
        enforceDeviceOwner(who);
        return this.mInjector.settingsGlobalGetString("private_dns_specifier");
    }

    /* JADX WARNING: type inference failed for: r2v2, types: [com.android.server.devicepolicy.UpdateInstaller] */
    /* JADX WARNING: type inference failed for: r3v3, types: [com.android.server.devicepolicy.NonAbUpdateInstaller] */
    /* JADX WARNING: type inference failed for: r3v4, types: [com.android.server.devicepolicy.AbUpdateInstaller] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void installUpdateFromFile(android.content.ComponentName r10, android.os.ParcelFileDescriptor r11, android.app.admin.StartInstallingUpdateCallback r12) {
        /*
            r9 = this;
            r0 = 73
            android.app.admin.DevicePolicyEventLogger r0 = android.app.admin.DevicePolicyEventLogger.createEvent(r0)
            android.app.admin.DevicePolicyEventLogger r0 = r0.setAdmin(r10)
            boolean r1 = r9.isDeviceAB()
            android.app.admin.DevicePolicyEventLogger r0 = r0.setBoolean(r1)
            r0.write()
            r9.enforceDeviceOwner(r10)
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r0 = r9.mInjector
            long r0 = r0.binderClearCallingIdentity()
            boolean r2 = r9.isDeviceAB()     // Catch:{ all -> 0x004c }
            if (r2 == 0) goto L_0x0034
            com.android.server.devicepolicy.AbUpdateInstaller r2 = new com.android.server.devicepolicy.AbUpdateInstaller     // Catch:{ all -> 0x004c }
            android.content.Context r4 = r9.mContext     // Catch:{ all -> 0x004c }
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r7 = r9.mInjector     // Catch:{ all -> 0x004c }
            com.android.server.devicepolicy.DevicePolicyConstants r8 = r9.mConstants     // Catch:{ all -> 0x004c }
            r3 = r2
            r5 = r11
            r6 = r12
            r3.<init>(r4, r5, r6, r7, r8)     // Catch:{ all -> 0x004c }
            goto L_0x0042
        L_0x0034:
            com.android.server.devicepolicy.NonAbUpdateInstaller r2 = new com.android.server.devicepolicy.NonAbUpdateInstaller     // Catch:{ all -> 0x004c }
            android.content.Context r4 = r9.mContext     // Catch:{ all -> 0x004c }
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r7 = r9.mInjector     // Catch:{ all -> 0x004c }
            com.android.server.devicepolicy.DevicePolicyConstants r8 = r9.mConstants     // Catch:{ all -> 0x004c }
            r3 = r2
            r5 = r11
            r6 = r12
            r3.<init>(r4, r5, r6, r7, r8)     // Catch:{ all -> 0x004c }
        L_0x0042:
            r2.startInstallUpdate()     // Catch:{ all -> 0x004c }
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r2 = r9.mInjector
            r2.binderRestoreCallingIdentity(r0)
            return
        L_0x004c:
            r2 = move-exception
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r3 = r9.mInjector
            r3.binderRestoreCallingIdentity(r0)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.DevicePolicyManagerService.installUpdateFromFile(android.content.ComponentName, android.os.ParcelFileDescriptor, android.app.admin.StartInstallingUpdateCallback):void");
    }

    private boolean isDeviceAB() {
        return "true".equalsIgnoreCase(SystemProperties.get(AB_DEVICE_KEY, ""));
    }

    public void setCrossProfileCalendarPackages(ComponentName who, List<String> packageNames) {
        String[] strArr;
        if (this.mHasFeature) {
            Preconditions.checkNotNull(who, "ComponentName is null");
            synchronized (getLockObject()) {
                getActiveAdminForCallerLocked(who, -1).mCrossProfileCalendarPackages = packageNames;
                saveSettingsLocked(this.mInjector.userHandleGetCallingUserId());
            }
            DevicePolicyEventLogger admin = DevicePolicyEventLogger.createEvent(70).setAdmin(who);
            if (packageNames == null) {
                strArr = null;
            } else {
                strArr = (String[]) packageNames.toArray(new String[packageNames.size()]);
            }
            admin.setStrings(strArr).write();
        }
    }

    public List<String> getCrossProfileCalendarPackages(ComponentName who) {
        List<String> list;
        if (!this.mHasFeature) {
            return Collections.emptyList();
        }
        Preconditions.checkNotNull(who, "ComponentName is null");
        synchronized (getLockObject()) {
            list = getActiveAdminForCallerLocked(who, -1).mCrossProfileCalendarPackages;
        }
        return list;
    }

    public boolean isPackageAllowedToAccessCalendarForUser(String packageName, int userHandle) {
        if (!this.mHasFeature) {
            return false;
        }
        Preconditions.checkStringNotEmpty(packageName, "Package name is null or empty");
        enforceCrossUsersPermission(userHandle);
        synchronized (getLockObject()) {
            if (this.mInjector.settingsSecureGetIntForUser("cross_profile_calendar_enabled", 0, userHandle) == 0) {
                return false;
            }
            ActiveAdmin admin = getProfileOwnerAdminLocked(userHandle);
            if (admin == null) {
                return false;
            }
            if (admin.mCrossProfileCalendarPackages == null) {
                return true;
            }
            boolean contains = admin.mCrossProfileCalendarPackages.contains(packageName);
            return contains;
        }
    }

    public List<String> getCrossProfileCalendarPackagesForUser(int userHandle) {
        if (!this.mHasFeature) {
            return Collections.emptyList();
        }
        enforceCrossUsersPermission(userHandle);
        synchronized (getLockObject()) {
            ActiveAdmin admin = getProfileOwnerAdminLocked(userHandle);
            if (admin == null) {
                return Collections.emptyList();
            }
            List<String> list = admin.mCrossProfileCalendarPackages;
            return list;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    public boolean isManagedKiosk() {
        if (!this.mHasFeature) {
            return false;
        }
        enforceManageUsers();
        long id = this.mInjector.binderClearCallingIdentity();
        try {
            boolean isManagedKioskInternal = isManagedKioskInternal();
            this.mInjector.binderRestoreCallingIdentity(id);
            return isManagedKioskInternal;
        } catch (RemoteException e) {
            throw new IllegalStateException(e);
        } catch (Throwable th) {
            this.mInjector.binderRestoreCallingIdentity(id);
            throw th;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    public boolean isUnattendedManagedKiosk() {
        boolean z = false;
        if (!this.mHasFeature) {
            return false;
        }
        enforceManageUsers();
        long id = this.mInjector.binderClearCallingIdentity();
        try {
            if (isManagedKioskInternal() && getPowerManagerInternal().wasDeviceIdleFor(30000)) {
                z = true;
            }
            this.mInjector.binderRestoreCallingIdentity(id);
            return z;
        } catch (RemoteException e) {
            throw new IllegalStateException(e);
        } catch (Throwable th) {
            this.mInjector.binderRestoreCallingIdentity(id);
            throw th;
        }
    }

    private boolean isManagedKioskInternal() throws RemoteException {
        return this.mOwners.hasDeviceOwner() && this.mInjector.getIActivityManager().getLockTaskModeState() == 1 && !isLockTaskFeatureEnabled(1) && !deviceHasKeyguard() && !inEphemeralUserSession();
    }

    private boolean isLockTaskFeatureEnabled(int lockTaskFeature) throws RemoteException {
        return (getUserData(this.mInjector.getIActivityManager().getCurrentUser().id).mLockTaskFeatures & lockTaskFeature) == lockTaskFeature;
    }

    private boolean deviceHasKeyguard() {
        for (UserInfo userInfo : this.mUserManager.getUsers()) {
            if (this.mLockPatternUtils.isSecure(userInfo.id)) {
                return true;
            }
        }
        return false;
    }

    private boolean inEphemeralUserSession() {
        for (UserInfo userInfo : this.mUserManager.getUsers()) {
            if (this.mInjector.getUserManager().isUserEphemeral(userInfo.id)) {
                return true;
            }
        }
        return false;
    }

    private PowerManagerInternal getPowerManagerInternal() {
        return this.mInjector.getPowerManagerInternal();
    }

    public boolean startViewCalendarEventInManagedProfile(String packageName, long eventId, long start, long end, boolean allDay, int flags) {
        String str = packageName;
        if (!this.mHasFeature) {
            return false;
        }
        Preconditions.checkStringNotEmpty(str, "Package name is empty");
        int callingUid = this.mInjector.binderGetCallingUid();
        int callingUserId = this.mInjector.userHandleGetCallingUserId();
        if (isCallingFromPackage(str, callingUid)) {
            long identity = this.mInjector.binderClearCallingIdentity();
            try {
                int workProfileUserId = getManagedUserId(callingUserId);
                if (workProfileUserId < 0) {
                    this.mInjector.binderRestoreCallingIdentity(identity);
                    return false;
                } else if (!isPackageAllowedToAccessCalendarForUser(str, workProfileUserId)) {
                    try {
                        Log.d(LOG_TAG, String.format("Package %s is not allowed to access cross-profilecalendar APIs", new Object[]{str}));
                        this.mInjector.binderRestoreCallingIdentity(identity);
                        return false;
                    } catch (Throwable th) {
                        e = th;
                        long j = eventId;
                        long j2 = start;
                        boolean z = allDay;
                        int i = callingUid;
                        long j3 = end;
                        this.mInjector.binderRestoreCallingIdentity(identity);
                        throw e;
                    }
                } else {
                    Intent intent = new Intent("android.provider.calendar.action.VIEW_MANAGED_PROFILE_CALENDAR_EVENT");
                    intent.setPackage(str);
                    try {
                        intent.putExtra(ATTR_ID, eventId);
                    } catch (Throwable th2) {
                        e = th2;
                        long j4 = start;
                        boolean z2 = allDay;
                        int i2 = callingUid;
                        long j5 = end;
                        this.mInjector.binderRestoreCallingIdentity(identity);
                        throw e;
                    }
                    try {
                        intent.putExtra("beginTime", start);
                        int i3 = callingUid;
                        try {
                            intent.putExtra("endTime", end);
                        } catch (Throwable th3) {
                            e = th3;
                            boolean z3 = allDay;
                            this.mInjector.binderRestoreCallingIdentity(identity);
                            throw e;
                        }
                        try {
                            intent.putExtra("allDay", allDay);
                            intent.setFlags(flags);
                            this.mContext.startActivityAsUser(intent, UserHandle.of(workProfileUserId));
                            this.mInjector.binderRestoreCallingIdentity(identity);
                            return true;
                        } catch (ActivityNotFoundException e) {
                            Log.e(LOG_TAG, "View event activity not found", e);
                            this.mInjector.binderRestoreCallingIdentity(identity);
                            return false;
                        } catch (Throwable th4) {
                            e = th4;
                            this.mInjector.binderRestoreCallingIdentity(identity);
                            throw e;
                        }
                    } catch (Throwable th5) {
                        e = th5;
                        boolean z22 = allDay;
                        int i22 = callingUid;
                        long j52 = end;
                        this.mInjector.binderRestoreCallingIdentity(identity);
                        throw e;
                    }
                }
            } catch (Throwable th6) {
                e = th6;
                long j6 = eventId;
                long j42 = start;
                boolean z222 = allDay;
                int i222 = callingUid;
                long j522 = end;
                this.mInjector.binderRestoreCallingIdentity(identity);
                throw e;
            }
        } else {
            throw new SecurityException("Input package name doesn't align with actual calling package.");
        }
    }

    private boolean isCallingFromPackage(String packageName, int callingUid) {
        try {
            if (this.mInjector.getPackageManager().getPackageUidAsUser(packageName, UserHandle.getUserId(callingUid)) == callingUid) {
                return true;
            }
            return false;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(LOG_TAG, "Calling package not found", e);
            return false;
        }
    }

    /* access modifiers changed from: private */
    public DevicePolicyConstants loadConstants() {
        return DevicePolicyConstants.loadFromString(this.mInjector.settingsGlobalGetString("device_policy_constants"));
    }
}
