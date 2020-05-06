package com.android.server.net;

import android.app.ActivityManagerInternal;
import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.app.IActivityManager;
import android.app.IUidObserver;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.usage.UsageStatsManagerInternal;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.IConnectivityManager;
import android.net.INetworkManagementEventObserver;
import android.net.INetworkPolicyListener;
import android.net.INetworkPolicyManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkIdentity;
import android.net.NetworkPolicy;
import android.net.NetworkPolicyManager;
import android.net.NetworkQuotaInfo;
import android.net.NetworkRequest;
import android.net.NetworkState;
import android.net.NetworkStats;
import android.net.NetworkTemplate;
import android.net.StringNetworkSpecifier;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.BestClock;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IDeviceIdleController;
import android.os.INetworkManagementService;
import android.os.Message;
import android.os.MessageQueue;
import android.os.PersistableBundle;
import android.os.PowerManagerInternal;
import android.os.PowerSaveState;
import android.os.Process;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.SubscriptionPlan;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.AtomicFile;
import android.util.DataUnit;
import android.util.Log;
import android.util.Pair;
import android.util.Range;
import android.util.RecurrenceRule;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.SparseLongArray;
import android.util.Xml;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.notification.SystemNotificationChannels;
import com.android.internal.os.RoSystemProperties;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.ConcurrentUtils;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.Preconditions;
import com.android.internal.util.StatLogger;
import com.android.internal.util.XmlUtils;
import com.android.server.LocalServices;
import com.android.server.NetworkManagementService;
import com.android.server.ServiceThread;
import com.android.server.SystemConfig;
import com.android.server.job.controllers.JobStatus;
import com.android.server.pm.DumpState;
import com.android.server.pm.PackageManagerService;
import com.android.server.pm.Settings;
import com.android.server.slice.SliceClientPermissions;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import libcore.io.IoUtils;
import libcore.util.EmptyArray;
import org.xmlpull.v1.XmlPullParser;

public class NetworkPolicyManagerService extends INetworkPolicyManager.Stub {
    private static final String ACTION_ALLOW_BACKGROUND = "com.android.server.net.action.ALLOW_BACKGROUND";
    private static final String ACTION_SNOOZE_RAPID = "com.android.server.net.action.SNOOZE_RAPID";
    private static final String ACTION_SNOOZE_WARNING = "com.android.server.net.action.SNOOZE_WARNING";
    private static final String ATTR_APP_ID = "appId";
    @Deprecated
    private static final String ATTR_CYCLE_DAY = "cycleDay";
    private static final String ATTR_CYCLE_END = "cycleEnd";
    private static final String ATTR_CYCLE_PERIOD = "cyclePeriod";
    private static final String ATTR_CYCLE_START = "cycleStart";
    @Deprecated
    private static final String ATTR_CYCLE_TIMEZONE = "cycleTimezone";
    private static final String ATTR_INFERRED = "inferred";
    private static final String ATTR_LAST_LIMIT_SNOOZE = "lastLimitSnooze";
    private static final String ATTR_LAST_SNOOZE = "lastSnooze";
    private static final String ATTR_LAST_WARNING_SNOOZE = "lastWarningSnooze";
    private static final String ATTR_LIMIT_BEHAVIOR = "limitBehavior";
    private static final String ATTR_LIMIT_BYTES = "limitBytes";
    private static final String ATTR_METERED = "metered";
    private static final String ATTR_NETWORK_ID = "networkId";
    private static final String ATTR_NETWORK_TEMPLATE = "networkTemplate";
    private static final String ATTR_OWNER_PACKAGE = "ownerPackage";
    private static final String ATTR_POLICY = "policy";
    private static final String ATTR_RESTRICT_BACKGROUND = "restrictBackground";
    private static final String ATTR_SUBSCRIBER_ID = "subscriberId";
    private static final String ATTR_SUB_ID = "subId";
    private static final String ATTR_SUMMARY = "summary";
    private static final String ATTR_TITLE = "title";
    private static final String ATTR_UID = "uid";
    private static final String ATTR_USAGE_BYTES = "usageBytes";
    private static final String ATTR_USAGE_TIME = "usageTime";
    private static final String ATTR_VERSION = "version";
    private static final String ATTR_WARNING_BYTES = "warningBytes";
    private static final int CHAIN_TOGGLE_DISABLE = 2;
    private static final int CHAIN_TOGGLE_ENABLE = 1;
    private static final int CHAIN_TOGGLE_NONE = 0;
    /* access modifiers changed from: private */
    public static final boolean LOGD = NetworkPolicyLogger.LOGD;
    /* access modifiers changed from: private */
    public static final boolean LOGV = NetworkPolicyLogger.LOGV;
    private static final int MSG_ADVISE_PERSIST_THRESHOLD = 7;
    private static final int MSG_LIMIT_REACHED = 5;
    private static final int MSG_METERED_IFACES_CHANGED = 2;
    private static final int MSG_METERED_RESTRICTED_PACKAGES_CHANGED = 17;
    private static final int MSG_POLICIES_CHANGED = 13;
    private static final int MSG_REMOVE_INTERFACE_QUOTA = 11;
    private static final int MSG_RESET_FIREWALL_RULES_BY_UID = 15;
    private static final int MSG_RESTRICT_BACKGROUND_CHANGED = 6;
    private static final int MSG_RULES_CHANGED = 1;
    private static final int MSG_SET_NETWORK_TEMPLATE_ENABLED = 18;
    private static final int MSG_SUBSCRIPTION_OVERRIDE = 16;
    private static final int MSG_UPDATE_INTERFACE_QUOTA = 10;
    public static final int OPPORTUNISTIC_QUOTA_UNKNOWN = -1;
    private static final String PROP_SUB_PLAN_OWNER = "persist.sys.sub_plan_owner";
    private static final float QUOTA_FRAC_JOBS_DEFAULT = 0.5f;
    private static final float QUOTA_FRAC_MULTIPATH_DEFAULT = 0.5f;
    private static final float QUOTA_LIMITED_DEFAULT = 0.1f;
    private static final long QUOTA_UNLIMITED_DEFAULT = DataUnit.MEBIBYTES.toBytes(20);
    static final String TAG = "NetworkPolicy";
    private static final String TAG_APP_POLICY = "app-policy";
    private static final String TAG_NETWORK_POLICY = "network-policy";
    private static final String TAG_POLICY_LIST = "policy-list";
    private static final String TAG_RESTRICT_BACKGROUND = "restrict-background";
    private static final String TAG_REVOKED_RESTRICT_BACKGROUND = "revoked-restrict-background";
    private static final String TAG_SUBSCRIPTION_PLAN = "subscription-plan";
    private static final String TAG_UID_POLICY = "uid-policy";
    private static final String TAG_WHITELIST = "whitelist";
    @VisibleForTesting
    public static final int TYPE_LIMIT = 35;
    @VisibleForTesting
    public static final int TYPE_LIMIT_SNOOZED = 36;
    @VisibleForTesting
    public static final int TYPE_RAPID = 45;
    private static final int TYPE_RESTRICT_BACKGROUND = 1;
    private static final int TYPE_RESTRICT_POWER = 2;
    @VisibleForTesting
    public static final int TYPE_WARNING = 34;
    private static final int UID_MSG_GONE = 101;
    private static final int UID_MSG_STATE_CHANGED = 100;
    private static final int VERSION_ADDED_CYCLE = 11;
    private static final int VERSION_ADDED_INFERRED = 7;
    private static final int VERSION_ADDED_METERED = 4;
    private static final int VERSION_ADDED_NETWORK_ID = 9;
    private static final int VERSION_ADDED_RESTRICT_BACKGROUND = 3;
    private static final int VERSION_ADDED_SNOOZE = 2;
    private static final int VERSION_ADDED_TIMEZONE = 6;
    private static final int VERSION_INIT = 1;
    private static final int VERSION_LATEST = 11;
    private static final int VERSION_SPLIT_SNOOZE = 5;
    private static final int VERSION_SWITCH_APP_ID = 8;
    private static final int VERSION_SWITCH_UID = 10;
    private static final long WAIT_FOR_ADMIN_DATA_TIMEOUT_MS = 10000;
    @GuardedBy({"mNetworkPoliciesSecondLock"})
    private final ArraySet<NotificationId> mActiveNotifs;
    private final IActivityManager mActivityManager;
    private ActivityManagerInternal mActivityManagerInternal;
    /* access modifiers changed from: private */
    public final CountDownLatch mAdminDataAvailableLatch;
    private final INetworkManagementEventObserver mAlertObserver;
    private final BroadcastReceiver mAllowReceiver;
    @GuardedBy({"mUidRulesFirstLock"})
    private final SparseBooleanArray mAppIdleTempWhitelistAppIds;
    private final AppOpsManager mAppOps;
    private final CarrierConfigManager mCarrierConfigManager;
    private BroadcastReceiver mCarrierConfigReceiver;
    private final Clock mClock;
    private IConnectivityManager mConnManager;
    private BroadcastReceiver mConnReceiver;
    /* access modifiers changed from: private */
    public final Context mContext;
    @GuardedBy({"mUidRulesFirstLock"})
    private final SparseBooleanArray mDefaultRestrictBackgroundWhitelistUids;
    private IDeviceIdleController mDeviceIdleController;
    @GuardedBy({"mUidRulesFirstLock"})
    volatile boolean mDeviceIdleMode;
    @GuardedBy({"mUidRulesFirstLock"})
    final SparseBooleanArray mFirewallChainStates;
    final Handler mHandler;
    private final Handler.Callback mHandlerCallback;
    private final IPackageManager mIPm;
    /* access modifiers changed from: private */
    public final RemoteCallbackList<INetworkPolicyListener> mListeners;
    private boolean mLoadedRestrictBackground;
    /* access modifiers changed from: private */
    public final NetworkPolicyLogger mLogger;
    @GuardedBy({"mNetworkPoliciesSecondLock"})
    private String[] mMergedSubscriberIds;
    /* access modifiers changed from: private */
    @GuardedBy({"mNetworkPoliciesSecondLock"})
    public ArraySet<String> mMeteredIfaces;
    /* access modifiers changed from: private */
    @GuardedBy({"mUidRulesFirstLock"})
    public final SparseArray<Set<Integer>> mMeteredRestrictedUids;
    MiuiNetworkPolicyManagerService mMiuiNetPolicyManager;
    @GuardedBy({"mNetworkPoliciesSecondLock"})
    private final SparseIntArray mNetIdToSubId;
    private final ConnectivityManager.NetworkCallback mNetworkCallback;
    private final INetworkManagementService mNetworkManager;
    private volatile boolean mNetworkManagerReady;
    /* access modifiers changed from: private */
    @GuardedBy({"mNetworkPoliciesSecondLock"})
    public final SparseBooleanArray mNetworkMetered;
    final Object mNetworkPoliciesSecondLock;
    @GuardedBy({"mNetworkPoliciesSecondLock"})
    final ArrayMap<NetworkTemplate, NetworkPolicy> mNetworkPolicy;
    /* access modifiers changed from: private */
    @GuardedBy({"mNetworkPoliciesSecondLock"})
    public final SparseBooleanArray mNetworkRoaming;
    /* access modifiers changed from: private */
    public NetworkStatsManagerInternal mNetworkStats;
    @GuardedBy({"mNetworkPoliciesSecondLock"})
    private final ArraySet<NetworkTemplate> mOverLimitNotified;
    private final BroadcastReceiver mPackageReceiver;
    @GuardedBy({"mUidRulesFirstLock", "mNetworkPoliciesSecondLock"})
    private final AtomicFile mPolicyFile;
    private PowerManagerInternal mPowerManagerInternal;
    /* access modifiers changed from: private */
    @GuardedBy({"mUidRulesFirstLock"})
    public final SparseBooleanArray mPowerSaveTempWhitelistAppIds;
    @GuardedBy({"mUidRulesFirstLock"})
    private final SparseBooleanArray mPowerSaveWhitelistAppIds;
    @GuardedBy({"mUidRulesFirstLock"})
    private final SparseBooleanArray mPowerSaveWhitelistExceptIdleAppIds;
    private final BroadcastReceiver mPowerSaveWhitelistReceiver;
    @GuardedBy({"mUidRulesFirstLock"})
    volatile boolean mRestrictBackground;
    private boolean mRestrictBackgroundBeforeBsm;
    @GuardedBy({"mUidRulesFirstLock"})
    volatile boolean mRestrictBackgroundChangedInBsm;
    @GuardedBy({"mUidRulesFirstLock"})
    private PowerSaveState mRestrictBackgroundPowerState;
    @GuardedBy({"mUidRulesFirstLock"})
    private final SparseBooleanArray mRestrictBackgroundWhitelistRevokedUids;
    @GuardedBy({"mUidRulesFirstLock"})
    volatile boolean mRestrictPower;
    private final BroadcastReceiver mSnoozeReceiver;
    public final StatLogger mStatLogger;
    private final BroadcastReceiver mStatsReceiver;
    /* access modifiers changed from: private */
    @GuardedBy({"mNetworkPoliciesSecondLock"})
    public final SparseArray<String> mSubIdToSubscriberId;
    @GuardedBy({"mNetworkPoliciesSecondLock"})
    final SparseLongArray mSubscriptionOpportunisticQuota;
    @GuardedBy({"mNetworkPoliciesSecondLock"})
    final SparseArray<SubscriptionPlan[]> mSubscriptionPlans;
    @GuardedBy({"mNetworkPoliciesSecondLock"})
    final SparseArray<String> mSubscriptionPlansOwner;
    private final boolean mSuppressDefaultPolicy;
    @GuardedBy({"mUidRulesFirstLock", "mNetworkPoliciesSecondLock"})
    volatile boolean mSystemReady;
    @VisibleForTesting
    final Handler mUidEventHandler;
    private final Handler.Callback mUidEventHandlerCallback;
    private final ServiceThread mUidEventThread;
    @GuardedBy({"mUidRulesFirstLock"})
    final SparseIntArray mUidFirewallDozableRules;
    @GuardedBy({"mUidRulesFirstLock"})
    final SparseIntArray mUidFirewallPowerSaveRules;
    @GuardedBy({"mUidRulesFirstLock"})
    final SparseIntArray mUidFirewallStandbyRules;
    private final IUidObserver mUidObserver;
    @GuardedBy({"mUidRulesFirstLock"})
    final SparseIntArray mUidPolicy;
    private final BroadcastReceiver mUidRemovedReceiver;
    @GuardedBy({"mUidRulesFirstLock"})
    final SparseIntArray mUidRules;
    final Object mUidRulesFirstLock;
    @GuardedBy({"mUidRulesFirstLock"})
    final SparseIntArray mUidState;
    private UsageStatsManagerInternal mUsageStats;
    private final UserManager mUserManager;
    private final BroadcastReceiver mUserReceiver;
    private final BroadcastReceiver mWifiReceiver;

    @Retention(RetentionPolicy.SOURCE)
    public @interface ChainToggleType {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface RestrictType {
    }

    interface Stats {
        public static final int COUNT = 2;
        public static final int IS_UID_NETWORKING_BLOCKED = 1;
        public static final int UPDATE_NETWORK_ENABLED = 0;
    }

    public NetworkPolicyManagerService(Context context, IActivityManager activityManager, INetworkManagementService networkManagement) {
        this(context, activityManager, networkManagement, AppGlobals.getPackageManager(), getDefaultClock(), getDefaultSystemDir(), false);
    }

    private static File getDefaultSystemDir() {
        return new File(Environment.getDataDirectory(), "system");
    }

    private static Clock getDefaultClock() {
        return new BestClock(ZoneOffset.UTC, new Clock[]{SystemClock.currentNetworkTimeClock(), Clock.systemUTC()});
    }

    public NetworkPolicyManagerService(Context context, IActivityManager activityManager, INetworkManagementService networkManagement, IPackageManager pm, Clock clock, File systemDir, boolean suppressDefaultPolicy) {
        this.mUidRulesFirstLock = new Object();
        this.mNetworkPoliciesSecondLock = new Object();
        this.mAdminDataAvailableLatch = new CountDownLatch(1);
        this.mNetworkPolicy = new ArrayMap<>();
        this.mSubscriptionPlans = new SparseArray<>();
        this.mSubscriptionPlansOwner = new SparseArray<>();
        this.mSubscriptionOpportunisticQuota = new SparseLongArray();
        this.mUidPolicy = new SparseIntArray();
        this.mUidRules = new SparseIntArray();
        this.mUidFirewallStandbyRules = new SparseIntArray();
        this.mUidFirewallDozableRules = new SparseIntArray();
        this.mUidFirewallPowerSaveRules = new SparseIntArray();
        this.mFirewallChainStates = new SparseBooleanArray();
        this.mPowerSaveWhitelistExceptIdleAppIds = new SparseBooleanArray();
        this.mPowerSaveWhitelistAppIds = new SparseBooleanArray();
        this.mPowerSaveTempWhitelistAppIds = new SparseBooleanArray();
        this.mAppIdleTempWhitelistAppIds = new SparseBooleanArray();
        this.mDefaultRestrictBackgroundWhitelistUids = new SparseBooleanArray();
        this.mRestrictBackgroundWhitelistRevokedUids = new SparseBooleanArray();
        this.mMeteredIfaces = new ArraySet<>();
        this.mOverLimitNotified = new ArraySet<>();
        this.mActiveNotifs = new ArraySet<>();
        this.mUidState = new SparseIntArray();
        this.mNetworkMetered = new SparseBooleanArray();
        this.mNetworkRoaming = new SparseBooleanArray();
        this.mNetIdToSubId = new SparseIntArray();
        this.mSubIdToSubscriberId = new SparseArray<>();
        this.mMergedSubscriberIds = EmptyArray.STRING;
        this.mMeteredRestrictedUids = new SparseArray<>();
        this.mListeners = new RemoteCallbackList<>();
        this.mLogger = new NetworkPolicyLogger();
        this.mStatLogger = new StatLogger(new String[]{"updateNetworkEnabledNL()", "isUidNetworkingBlocked()"});
        this.mUidObserver = new IUidObserver.Stub() {
            public void onUidStateChanged(int uid, int procState, long procStateSeq) {
                NetworkPolicyManagerService.this.mUidEventHandler.obtainMessage(100, uid, procState, Long.valueOf(procStateSeq)).sendToTarget();
            }

            public void onUidGone(int uid, boolean disabled) {
                NetworkPolicyManagerService.this.mUidEventHandler.obtainMessage(101, uid, 0).sendToTarget();
            }

            public void onUidActive(int uid) {
            }

            public void onUidIdle(int uid, boolean disabled) {
            }

            public void onUidCachedChanged(int uid, boolean cached) {
            }
        };
        this.mPowerSaveWhitelistReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                synchronized (NetworkPolicyManagerService.this.mUidRulesFirstLock) {
                    NetworkPolicyManagerService.this.updatePowerSaveWhitelistUL();
                    NetworkPolicyManagerService.this.updateRulesForRestrictPowerUL();
                    NetworkPolicyManagerService.this.updateRulesForAppIdleUL();
                }
            }
        };
        this.mPackageReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                int uid = intent.getIntExtra("android.intent.extra.UID", -1);
                if (uid != -1 && "android.intent.action.PACKAGE_ADDED".equals(action)) {
                    if (NetworkPolicyManagerService.LOGV) {
                        Slog.v(NetworkPolicyManagerService.TAG, "ACTION_PACKAGE_ADDED for uid=" + uid);
                    }
                    synchronized (NetworkPolicyManagerService.this.mUidRulesFirstLock) {
                        NetworkPolicyManagerService.this.updateRestrictionRulesForUidUL(uid);
                    }
                }
            }
        };
        this.mUidRemovedReceiver = new BroadcastReceiver() {
            /* Debug info: failed to restart local var, previous not found, register: 4 */
            public void onReceive(Context context, Intent intent) {
                int uid = intent.getIntExtra("android.intent.extra.UID", -1);
                if (uid != -1) {
                    if (NetworkPolicyManagerService.LOGV) {
                        Slog.v(NetworkPolicyManagerService.TAG, "ACTION_UID_REMOVED for uid=" + uid);
                    }
                    synchronized (NetworkPolicyManagerService.this.mUidRulesFirstLock) {
                        NetworkPolicyManagerService.this.onUidDeletedUL(uid);
                        synchronized (NetworkPolicyManagerService.this.mNetworkPoliciesSecondLock) {
                            NetworkPolicyManagerService.this.writePolicyAL();
                        }
                    }
                }
            }
        };
        this.mUserReceiver = new BroadcastReceiver() {
            /* Debug info: failed to restart local var, previous not found, register: 6 */
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                char c = 65535;
                int userId = intent.getIntExtra("android.intent.extra.user_handle", -1);
                if (userId != -1) {
                    int hashCode = action.hashCode();
                    if (hashCode != -2061058799) {
                        if (hashCode == 1121780209 && action.equals("android.intent.action.USER_ADDED")) {
                            c = 1;
                        }
                    } else if (action.equals("android.intent.action.USER_REMOVED")) {
                        c = 0;
                    }
                    if (c == 0 || c == 1) {
                        synchronized (NetworkPolicyManagerService.this.mUidRulesFirstLock) {
                            NetworkPolicyManagerService.this.removeUserStateUL(userId, true);
                            NetworkPolicyManagerService.this.mMeteredRestrictedUids.remove(userId);
                            if (action == "android.intent.action.USER_ADDED") {
                                boolean unused = NetworkPolicyManagerService.this.addDefaultRestrictBackgroundWhitelistUidsUL(userId);
                            }
                            synchronized (NetworkPolicyManagerService.this.mNetworkPoliciesSecondLock) {
                                NetworkPolicyManagerService.this.updateRulesForGlobalChangeAL(true);
                            }
                        }
                    }
                }
            }
        };
        this.mStatsReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                synchronized (NetworkPolicyManagerService.this.mNetworkPoliciesSecondLock) {
                    NetworkPolicyManagerService.this.updateNetworkEnabledNL();
                    NetworkPolicyManagerService.this.updateNotificationsNL();
                }
            }
        };
        this.mAllowReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                NetworkPolicyManagerService.this.setRestrictBackground(false);
            }
        };
        this.mSnoozeReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                NetworkTemplate template = intent.getParcelableExtra("android.net.NETWORK_TEMPLATE");
                if (NetworkPolicyManagerService.ACTION_SNOOZE_WARNING.equals(intent.getAction())) {
                    NetworkPolicyManagerService.this.performSnooze(template, 34);
                } else if (NetworkPolicyManagerService.ACTION_SNOOZE_RAPID.equals(intent.getAction())) {
                    NetworkPolicyManagerService.this.performSnooze(template, 45);
                }
            }
        };
        this.mWifiReceiver = new BroadcastReceiver() {
            /* Debug info: failed to restart local var, previous not found, register: 3 */
            public void onReceive(Context context, Intent intent) {
                synchronized (NetworkPolicyManagerService.this.mUidRulesFirstLock) {
                    synchronized (NetworkPolicyManagerService.this.mNetworkPoliciesSecondLock) {
                        NetworkPolicyManagerService.this.upgradeWifiMeteredOverrideAL();
                    }
                }
                NetworkPolicyManagerService.this.mContext.unregisterReceiver(this);
            }
        };
        this.mNetworkCallback = new ConnectivityManager.NetworkCallback() {
            public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
                if (network != null && networkCapabilities != null) {
                    synchronized (NetworkPolicyManagerService.this.mNetworkPoliciesSecondLock) {
                        boolean newRoaming = true;
                        boolean newMetered = !networkCapabilities.hasCapability(11);
                        boolean meteredChanged = NetworkPolicyManagerService.updateCapabilityChange(NetworkPolicyManagerService.this.mNetworkMetered, newMetered, network);
                        if (networkCapabilities.hasCapability(18)) {
                            newRoaming = false;
                        }
                        boolean roamingChanged = NetworkPolicyManagerService.updateCapabilityChange(NetworkPolicyManagerService.this.mNetworkRoaming, newRoaming, network);
                        if (meteredChanged || roamingChanged) {
                            NetworkPolicyManagerService.this.mLogger.meterednessChanged(network.netId, newMetered);
                            NetworkPolicyManagerService.this.updateNetworkRulesNL();
                        }
                    }
                }
            }
        };
        this.mAlertObserver = new BaseNetworkObserver() {
            public void limitReached(String limitName, String iface) {
                NetworkPolicyManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", NetworkPolicyManagerService.TAG);
                if (!NetworkManagementService.LIMIT_GLOBAL_ALERT.equals(limitName)) {
                    NetworkPolicyManagerService.this.mHandler.obtainMessage(5, iface).sendToTarget();
                }
            }
        };
        this.mConnReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                NetworkPolicyManagerService.this.updateNetworksInternal();
            }
        };
        this.mCarrierConfigReceiver = new BroadcastReceiver() {
            /* Debug info: failed to restart local var, previous not found, register: 7 */
            public void onReceive(Context context, Intent intent) {
                if (intent.hasExtra("subscription")) {
                    int subId = intent.getIntExtra("subscription", -1);
                    NetworkPolicyManagerService.this.updateSubscriptions();
                    synchronized (NetworkPolicyManagerService.this.mUidRulesFirstLock) {
                        synchronized (NetworkPolicyManagerService.this.mNetworkPoliciesSecondLock) {
                            String subscriberId = (String) NetworkPolicyManagerService.this.mSubIdToSubscriberId.get(subId, (Object) null);
                            if (subscriberId != null) {
                                boolean unused = NetworkPolicyManagerService.this.ensureActiveMobilePolicyAL(subId, subscriberId);
                                boolean unused2 = NetworkPolicyManagerService.this.maybeUpdateMobilePolicyCycleAL(subId, subscriberId);
                            } else {
                                Slog.wtf(NetworkPolicyManagerService.TAG, "Missing subscriberId for subId " + subId);
                            }
                            NetworkPolicyManagerService.this.handleNetworkPoliciesUpdateAL(true);
                        }
                    }
                }
            }
        };
        this.mHandlerCallback = new Handler.Callback() {
            public boolean handleMessage(Message msg) {
                boolean enabled = false;
                switch (msg.what) {
                    case 1:
                        int uid = msg.arg1;
                        int uidRules = msg.arg2;
                        int length = NetworkPolicyManagerService.this.mListeners.beginBroadcast();
                        for (int i = 0; i < length; i++) {
                            NetworkPolicyManagerService.this.dispatchUidRulesChanged(NetworkPolicyManagerService.this.mListeners.getBroadcastItem(i), uid, uidRules);
                        }
                        NetworkPolicyManagerService.this.mListeners.finishBroadcast();
                        return true;
                    case 2:
                        String[] meteredIfaces = (String[]) msg.obj;
                        int length2 = NetworkPolicyManagerService.this.mListeners.beginBroadcast();
                        for (int i2 = 0; i2 < length2; i2++) {
                            NetworkPolicyManagerService.this.dispatchMeteredIfacesChanged(NetworkPolicyManagerService.this.mListeners.getBroadcastItem(i2), meteredIfaces);
                        }
                        NetworkPolicyManagerService.this.mListeners.finishBroadcast();
                        return true;
                    case 5:
                        String iface = (String) msg.obj;
                        synchronized (NetworkPolicyManagerService.this.mNetworkPoliciesSecondLock) {
                            if (NetworkPolicyManagerService.this.mMeteredIfaces.contains(iface)) {
                                NetworkPolicyManagerService.this.mNetworkStats.forceUpdate();
                                NetworkPolicyManagerService.this.updateNetworkEnabledNL();
                                NetworkPolicyManagerService.this.updateNotificationsNL();
                            }
                        }
                        return true;
                    case 6:
                        if (msg.arg1 != 0) {
                            enabled = true;
                        }
                        boolean restrictBackground = enabled;
                        int length3 = NetworkPolicyManagerService.this.mListeners.beginBroadcast();
                        for (int i3 = 0; i3 < length3; i3++) {
                            NetworkPolicyManagerService.this.dispatchRestrictBackgroundChanged(NetworkPolicyManagerService.this.mListeners.getBroadcastItem(i3), restrictBackground);
                        }
                        NetworkPolicyManagerService.this.mListeners.finishBroadcast();
                        Intent intent = new Intent("android.net.conn.RESTRICT_BACKGROUND_CHANGED");
                        intent.setFlags(1073741824);
                        NetworkPolicyManagerService.this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
                        return true;
                    case 7:
                        NetworkPolicyManagerService.this.mNetworkStats.advisePersistThreshold(((Long) msg.obj).longValue() / 1000);
                        return true;
                    case 10:
                        NetworkPolicyManagerService.this.removeInterfaceQuota((String) msg.obj);
                        NetworkPolicyManagerService.this.setInterfaceQuota((String) msg.obj, (((long) msg.arg1) << 32) | (((long) msg.arg2) & 4294967295L));
                        return true;
                    case 11:
                        NetworkPolicyManagerService.this.removeInterfaceQuota((String) msg.obj);
                        return true;
                    case 13:
                        int uid2 = msg.arg1;
                        int policy = msg.arg2;
                        Boolean notifyApp = (Boolean) msg.obj;
                        int length4 = NetworkPolicyManagerService.this.mListeners.beginBroadcast();
                        for (int i4 = 0; i4 < length4; i4++) {
                            NetworkPolicyManagerService.this.dispatchUidPoliciesChanged(NetworkPolicyManagerService.this.mListeners.getBroadcastItem(i4), uid2, policy);
                        }
                        NetworkPolicyManagerService.this.mListeners.finishBroadcast();
                        if (notifyApp.booleanValue()) {
                            NetworkPolicyManagerService.this.broadcastRestrictBackgroundChanged(uid2, notifyApp);
                        }
                        return true;
                    case 15:
                        NetworkPolicyManagerService.this.resetUidFirewallRules(msg.arg1);
                        return true;
                    case 16:
                        int overrideMask = msg.arg1;
                        int overrideValue = msg.arg2;
                        int subId = ((Integer) msg.obj).intValue();
                        int length5 = NetworkPolicyManagerService.this.mListeners.beginBroadcast();
                        for (int i5 = 0; i5 < length5; i5++) {
                            NetworkPolicyManagerService.this.dispatchSubscriptionOverride(NetworkPolicyManagerService.this.mListeners.getBroadcastItem(i5), subId, overrideMask, overrideValue);
                        }
                        NetworkPolicyManagerService.this.mListeners.finishBroadcast();
                        return true;
                    case 17:
                        NetworkPolicyManagerService.this.setMeteredRestrictedPackagesInternal((Set) msg.obj, msg.arg1);
                        return true;
                    case 18:
                        NetworkTemplate template = (NetworkTemplate) msg.obj;
                        if (msg.arg1 != 0) {
                            enabled = true;
                        }
                        NetworkPolicyManagerService.this.setNetworkTemplateEnabledInner(template, enabled);
                        return true;
                    default:
                        return false;
                }
            }
        };
        this.mUidEventHandlerCallback = new Handler.Callback() {
            public boolean handleMessage(Message msg) {
                int i = msg.what;
                if (i == 100) {
                    NetworkPolicyManagerService.this.handleUidChanged(msg.arg1, msg.arg2, ((Long) msg.obj).longValue());
                    return true;
                } else if (i != 101) {
                    return false;
                } else {
                    NetworkPolicyManagerService.this.handleUidGone(msg.arg1);
                    return true;
                }
            }
        };
        this.mContext = (Context) Preconditions.checkNotNull(context, "missing context");
        this.mActivityManager = (IActivityManager) Preconditions.checkNotNull(activityManager, "missing activityManager");
        this.mNetworkManager = (INetworkManagementService) Preconditions.checkNotNull(networkManagement, "missing networkManagement");
        this.mDeviceIdleController = IDeviceIdleController.Stub.asInterface(ServiceManager.getService("deviceidle"));
        this.mClock = (Clock) Preconditions.checkNotNull(clock, "missing Clock");
        this.mUserManager = (UserManager) this.mContext.getSystemService("user");
        this.mCarrierConfigManager = (CarrierConfigManager) this.mContext.getSystemService(CarrierConfigManager.class);
        this.mIPm = pm;
        HandlerThread thread = new HandlerThread(TAG);
        thread.start();
        this.mHandler = new Handler(thread.getLooper(), this.mHandlerCallback);
        this.mUidEventThread = new ServiceThread("NetworkPolicy.uid", -2, false);
        this.mUidEventThread.start();
        this.mUidEventHandler = new Handler(this.mUidEventThread.getLooper(), this.mUidEventHandlerCallback);
        this.mSuppressDefaultPolicy = suppressDefaultPolicy;
        this.mPolicyFile = new AtomicFile(new File(systemDir, "netpolicy.xml"), "net-policy");
        this.mAppOps = (AppOpsManager) context.getSystemService(AppOpsManager.class);
        LocalServices.addService(NetworkPolicyManagerInternal.class, new NetworkPolicyManagerInternalImpl());
        this.mMiuiNetPolicyManager = MiuiNetworkPolicyManagerService.make(context);
    }

    public void bindConnectivityManager(IConnectivityManager connManager) {
        this.mConnManager = (IConnectivityManager) Preconditions.checkNotNull(connManager, "missing IConnectivityManager");
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mUidRulesFirstLock"})
    public void updatePowerSaveWhitelistUL() {
        try {
            int[] whitelist = this.mDeviceIdleController.getAppIdWhitelistExceptIdle();
            this.mPowerSaveWhitelistExceptIdleAppIds.clear();
            if (whitelist != null) {
                for (int uid : whitelist) {
                    this.mPowerSaveWhitelistExceptIdleAppIds.put(uid, true);
                }
            }
            int[] whitelist2 = this.mDeviceIdleController.getAppIdWhitelist();
            this.mPowerSaveWhitelistAppIds.clear();
            if (whitelist2 != null) {
                for (int uid2 : whitelist2) {
                    this.mPowerSaveWhitelistAppIds.put(uid2, true);
                }
            }
        } catch (RemoteException e) {
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mUidRulesFirstLock"})
    public boolean addDefaultRestrictBackgroundWhitelistUidsUL() {
        List<UserInfo> users = this.mUserManager.getUsers();
        int numberUsers = users.size();
        boolean changed = false;
        for (int i = 0; i < numberUsers; i++) {
            changed = addDefaultRestrictBackgroundWhitelistUidsUL(users.get(i).id) || changed;
        }
        return changed;
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mUidRulesFirstLock"})
    public boolean addDefaultRestrictBackgroundWhitelistUidsUL(int userId) {
        SystemConfig sysConfig = SystemConfig.getInstance();
        PackageManager pm = this.mContext.getPackageManager();
        ArraySet<String> allowDataUsage = sysConfig.getAllowInDataUsageSave();
        boolean changed = false;
        for (int i = 0; i < allowDataUsage.size(); i++) {
            String pkg = allowDataUsage.valueAt(i);
            if (LOGD) {
                Slog.d(TAG, "checking restricted background whitelisting for package " + pkg + " and user " + userId);
            }
            try {
                ApplicationInfo app = pm.getApplicationInfoAsUser(pkg, DumpState.DUMP_DEXOPT, userId);
                if (!app.isPrivilegedApp()) {
                    Slog.e(TAG, "addDefaultRestrictBackgroundWhitelistUidsUL(): skipping non-privileged app  " + pkg);
                } else {
                    int uid = UserHandle.getUid(userId, app.uid);
                    this.mDefaultRestrictBackgroundWhitelistUids.append(uid, true);
                    if (LOGD) {
                        Slog.d(TAG, "Adding uid " + uid + " (user " + userId + ") to default restricted background whitelist. Revoked status: " + this.mRestrictBackgroundWhitelistRevokedUids.get(uid));
                    }
                    if (!this.mRestrictBackgroundWhitelistRevokedUids.get(uid)) {
                        if (LOGD) {
                            Slog.d(TAG, "adding default package " + pkg + " (uid " + uid + " for user " + userId + ") to restrict background whitelist");
                        }
                        setUidPolicyUncheckedUL(uid, 4, false);
                        changed = true;
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                if (LOGD) {
                    Slog.d(TAG, "No ApplicationInfo for package " + pkg);
                }
            }
        }
        return changed;
    }

    /* Debug info: failed to restart local var, previous not found, register: 16 */
    /* access modifiers changed from: private */
    /* renamed from: initService */
    public void lambda$networkScoreAndNetworkManagementServiceReady$0$NetworkPolicyManagerService(CountDownLatch initCompleteSignal) {
        Trace.traceBegin(2097152, "systemReady");
        int oldPriority = Process.getThreadPriority(Process.myTid());
        try {
            Process.setThreadPriority(-2);
            if (!isBandwidthControlEnabled()) {
                Slog.w(TAG, "bandwidth controls disabled, unable to enforce policy");
                return;
            }
            this.mUsageStats = (UsageStatsManagerInternal) LocalServices.getService(UsageStatsManagerInternal.class);
            this.mNetworkStats = (NetworkStatsManagerInternal) LocalServices.getService(NetworkStatsManagerInternal.class);
            synchronized (this.mUidRulesFirstLock) {
                synchronized (this.mNetworkPoliciesSecondLock) {
                    updatePowerSaveWhitelistUL();
                    this.mPowerManagerInternal = (PowerManagerInternal) LocalServices.getService(PowerManagerInternal.class);
                    this.mPowerManagerInternal.registerLowPowerModeObserver(new PowerManagerInternal.LowPowerModeListener() {
                        public int getServiceType() {
                            return 6;
                        }

                        public void onLowPowerModeChanged(PowerSaveState result) {
                            boolean enabled = result.batterySaverEnabled;
                            if (NetworkPolicyManagerService.LOGD) {
                                Slog.d(NetworkPolicyManagerService.TAG, "onLowPowerModeChanged(" + enabled + ")");
                            }
                            synchronized (NetworkPolicyManagerService.this.mUidRulesFirstLock) {
                                if (NetworkPolicyManagerService.this.mRestrictPower != enabled) {
                                    NetworkPolicyManagerService.this.mRestrictPower = enabled;
                                    NetworkPolicyManagerService.this.updateRulesForRestrictPowerUL();
                                }
                            }
                        }
                    });
                    this.mRestrictPower = this.mPowerManagerInternal.getLowPowerState(6).batterySaverEnabled;
                    this.mSystemReady = true;
                    waitForAdminData();
                    readPolicyAL();
                    this.mRestrictBackgroundBeforeBsm = this.mLoadedRestrictBackground;
                    this.mRestrictBackgroundPowerState = this.mPowerManagerInternal.getLowPowerState(10);
                    if (this.mRestrictBackgroundPowerState.batterySaverEnabled && !this.mLoadedRestrictBackground) {
                        this.mLoadedRestrictBackground = true;
                    }
                    this.mPowerManagerInternal.registerLowPowerModeObserver(new PowerManagerInternal.LowPowerModeListener() {
                        public int getServiceType() {
                            return 10;
                        }

                        public void onLowPowerModeChanged(PowerSaveState result) {
                            synchronized (NetworkPolicyManagerService.this.mUidRulesFirstLock) {
                                NetworkPolicyManagerService.this.updateRestrictBackgroundByLowPowerModeUL(result);
                            }
                        }
                    });
                    if (addDefaultRestrictBackgroundWhitelistUidsUL()) {
                        writePolicyAL();
                    }
                    setRestrictBackgroundUL(this.mLoadedRestrictBackground);
                    updateRulesForGlobalChangeAL(false);
                    updateNotificationsNL();
                }
            }
            this.mActivityManagerInternal = (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
            try {
                this.mActivityManager.registerUidObserver(this.mUidObserver, 3, 6, PackageManagerService.PLATFORM_PACKAGE_NAME);
                this.mNetworkManager.registerObserver(this.mAlertObserver);
            } catch (RemoteException e) {
            }
            this.mContext.registerReceiver(this.mPowerSaveWhitelistReceiver, new IntentFilter("android.os.action.POWER_SAVE_WHITELIST_CHANGED"), (String) null, this.mHandler);
            this.mContext.registerReceiver(this.mConnReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"), "android.permission.CONNECTIVITY_INTERNAL", this.mHandler);
            IntentFilter packageFilter = new IntentFilter();
            packageFilter.addAction("android.intent.action.PACKAGE_ADDED");
            packageFilter.addDataScheme(Settings.ATTR_PACKAGE);
            this.mContext.registerReceiver(this.mPackageReceiver, packageFilter, (String) null, this.mHandler);
            this.mContext.registerReceiver(this.mUidRemovedReceiver, new IntentFilter("android.intent.action.UID_REMOVED"), (String) null, this.mHandler);
            IntentFilter userFilter = new IntentFilter();
            userFilter.addAction("android.intent.action.USER_ADDED");
            userFilter.addAction("android.intent.action.USER_REMOVED");
            this.mContext.registerReceiver(this.mUserReceiver, userFilter, (String) null, this.mHandler);
            this.mContext.registerReceiver(this.mStatsReceiver, new IntentFilter(NetworkStatsService.ACTION_NETWORK_STATS_UPDATED), "android.permission.READ_NETWORK_USAGE_HISTORY", this.mHandler);
            this.mContext.registerReceiver(this.mAllowReceiver, new IntentFilter(ACTION_ALLOW_BACKGROUND), "android.permission.MANAGE_NETWORK_POLICY", this.mHandler);
            this.mContext.registerReceiver(this.mSnoozeReceiver, new IntentFilter(ACTION_SNOOZE_WARNING), "android.permission.MANAGE_NETWORK_POLICY", this.mHandler);
            this.mContext.registerReceiver(this.mSnoozeReceiver, new IntentFilter(ACTION_SNOOZE_RAPID), "android.permission.MANAGE_NETWORK_POLICY", this.mHandler);
            this.mContext.registerReceiver(this.mWifiReceiver, new IntentFilter("android.net.wifi.CONFIGURED_NETWORKS_CHANGE"), (String) null, this.mHandler);
            this.mContext.registerReceiver(this.mCarrierConfigReceiver, new IntentFilter("android.telephony.action.CARRIER_CONFIG_CHANGED"), (String) null, this.mHandler);
            ((ConnectivityManager) this.mContext.getSystemService(ConnectivityManager.class)).registerNetworkCallback(new NetworkRequest.Builder().build(), this.mNetworkCallback);
            this.mUsageStats.addAppIdleStateChangeListener(new AppIdleStateChangeListener());
            ((SubscriptionManager) this.mContext.getSystemService(SubscriptionManager.class)).addOnSubscriptionsChangedListener(new SubscriptionManager.OnSubscriptionsChangedListener(this.mHandler.getLooper()) {
                public void onSubscriptionsChanged() {
                    NetworkPolicyManagerService.this.updateNetworksInternal();
                }
            });
            initCompleteSignal.countDown();
            Process.setThreadPriority(oldPriority);
            Trace.traceEnd(2097152);
            this.mMiuiNetPolicyManager.systemReady();
        } finally {
            Process.setThreadPriority(oldPriority);
            Trace.traceEnd(2097152);
        }
    }

    public CountDownLatch networkScoreAndNetworkManagementServiceReady() {
        this.mNetworkManagerReady = true;
        CountDownLatch initCompleteSignal = new CountDownLatch(1);
        this.mHandler.post(new Runnable(initCompleteSignal) {
            private final /* synthetic */ CountDownLatch f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                NetworkPolicyManagerService.this.lambda$networkScoreAndNetworkManagementServiceReady$0$NetworkPolicyManagerService(this.f$1);
            }
        });
        return initCompleteSignal;
    }

    /* Debug info: failed to restart local var, previous not found, register: 3 */
    public void systemReady(CountDownLatch initCompleteSignal) {
        try {
            if (!initCompleteSignal.await(30, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Service NetworkPolicy init timeout");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Service NetworkPolicy init interrupted", e);
        }
    }

    /* access modifiers changed from: private */
    public static boolean updateCapabilityChange(SparseBooleanArray lastValues, boolean newValue, Network network) {
        boolean changed = false;
        if (lastValues.get(network.netId, false) != newValue || lastValues.indexOfKey(network.netId) < 0) {
            changed = true;
        }
        if (changed) {
            lastValues.put(network.netId, newValue);
        }
        return changed;
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mNetworkPoliciesSecondLock"})
    public void updateNotificationsNL() {
        if (LOGV) {
            Slog.v(TAG, "updateNotificationsNL()");
        }
        Trace.traceBegin(2097152, "updateNotificationsNL");
        ArraySet<NotificationId> beforeNotifs = new ArraySet<>(this.mActiveNotifs);
        this.mActiveNotifs.clear();
        for (int i = beforeNotifs.size() - 1; i >= 0; i--) {
            NotificationId notificationId = beforeNotifs.valueAt(i);
            if (!this.mActiveNotifs.contains(notificationId)) {
                cancelNotification(notificationId);
            }
        }
        Trace.traceEnd(2097152);
    }

    private ApplicationInfo findRapidBlame(NetworkTemplate template, long start, long end) {
        String[] packageNames;
        long totalBytes = 0;
        long maxBytes = 0;
        NetworkStats stats = getNetworkUidBytes(template, start, end);
        NetworkStats.Entry entry = null;
        int maxUid = 0;
        for (int i = 0; i < stats.size(); i++) {
            entry = stats.getValues(i, entry);
            long bytes = entry.rxBytes + entry.txBytes;
            totalBytes += bytes;
            if (bytes > maxBytes) {
                maxBytes = bytes;
                maxUid = entry.uid;
            }
        }
        if (maxBytes <= 0 || maxBytes <= totalBytes / 2 || (packageNames = this.mContext.getPackageManager().getPackagesForUid(maxUid)) == null || packageNames.length != 1) {
            return null;
        }
        try {
            return this.mContext.getPackageManager().getApplicationInfo(packageNames[0], 4989440);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mNetworkPoliciesSecondLock"})
    public int findRelevantSubIdNL(NetworkTemplate template) {
        for (int i = 0; i < this.mSubIdToSubscriberId.size(); i++) {
            int subId = this.mSubIdToSubscriberId.keyAt(i);
            if (template.matches(new NetworkIdentity(0, 0, this.mSubIdToSubscriberId.valueAt(i), (String) null, false, true, true))) {
                return subId;
            }
        }
        return -1;
    }

    @GuardedBy({"mNetworkPoliciesSecondLock"})
    private void notifyOverLimitNL(NetworkTemplate template) {
        if (!this.mOverLimitNotified.contains(template)) {
            Context context = this.mContext;
            context.startActivity(buildNetworkOverLimitIntent(context.getResources(), template));
            this.mOverLimitNotified.add(template);
        }
    }

    @GuardedBy({"mNetworkPoliciesSecondLock"})
    private void notifyUnderLimitNL(NetworkTemplate template) {
        this.mOverLimitNotified.remove(template);
    }

    private void enqueueNotification(NetworkPolicy policy, int type, long totalBytes, ApplicationInfo rapidBlame) {
        CharSequence title;
        CharSequence body;
        CharSequence title2;
        NetworkPolicy networkPolicy = policy;
        int i = type;
        long j = totalBytes;
        ApplicationInfo applicationInfo = rapidBlame;
        NotificationId notificationId = new NotificationId(networkPolicy, i);
        Notification.Builder builder = new Notification.Builder(this.mContext, SystemNotificationChannels.NETWORK_ALERTS);
        builder.setOnlyAlertOnce(true);
        builder.setWhen(0);
        builder.setColor(this.mContext.getColor(17170460));
        Resources res = this.mContext.getResources();
        if (i != 45) {
            switch (i) {
                case 34:
                    title = res.getText(17039844);
                    body = res.getString(17039843, new Object[]{Formatter.formatFileSize(this.mContext, j, 8)});
                    builder.setSmallIcon(17301624);
                    builder.setDeleteIntent(PendingIntent.getBroadcast(this.mContext, 0, buildSnoozeWarningIntent(networkPolicy.template), 134217728));
                    Intent viewIntent = buildViewDataUsageIntent(res, networkPolicy.template);
                    if (!isHeadlessSystemUserBuild()) {
                        builder.setContentIntent(PendingIntent.getActivity(this.mContext, 0, viewIntent, 134217728));
                        break;
                    } else {
                        builder.setContentIntent(PendingIntent.getActivityAsUser(this.mContext, 0, viewIntent, 134217728, (Bundle) null, UserHandle.CURRENT));
                        break;
                    }
                case 35:
                    int matchRule = networkPolicy.template.getMatchRule();
                    if (matchRule == 1) {
                        title2 = res.getText(17039837);
                    } else if (matchRule == 4) {
                        title2 = res.getText(17039846);
                    } else {
                        return;
                    }
                    body = res.getText(17039834);
                    builder.setOngoing(true);
                    builder.setSmallIcon(17303560);
                    Intent intent = buildNetworkOverLimitIntent(res, networkPolicy.template);
                    if (!isHeadlessSystemUserBuild()) {
                        builder.setContentIntent(PendingIntent.getActivity(this.mContext, 0, intent, 134217728));
                        break;
                    } else {
                        builder.setContentIntent(PendingIntent.getActivityAsUser(this.mContext, 0, intent, 134217728, (Bundle) null, UserHandle.CURRENT));
                        break;
                    }
                case 36:
                    int matchRule2 = networkPolicy.template.getMatchRule();
                    if (matchRule2 == 1) {
                        title = res.getText(17039836);
                    } else if (matchRule2 == 4) {
                        title = res.getText(17039845);
                    } else {
                        return;
                    }
                    body = res.getString(17039835, new Object[]{Formatter.formatFileSize(this.mContext, j - networkPolicy.limitBytes, 8)});
                    builder.setOngoing(true);
                    builder.setSmallIcon(17301624);
                    builder.setChannelId(SystemNotificationChannels.NETWORK_STATUS);
                    Intent intent2 = buildViewDataUsageIntent(res, networkPolicy.template);
                    if (!isHeadlessSystemUserBuild()) {
                        builder.setContentIntent(PendingIntent.getActivity(this.mContext, 0, intent2, 134217728));
                        break;
                    } else {
                        builder.setContentIntent(PendingIntent.getActivityAsUser(this.mContext, 0, intent2, 134217728, (Bundle) null, UserHandle.CURRENT));
                        break;
                    }
                default:
                    return;
            }
        } else {
            title = res.getText(17039840);
            if (applicationInfo != null) {
                body = res.getString(17039838, new Object[]{applicationInfo.loadLabel(this.mContext.getPackageManager())});
            } else {
                body = res.getString(17039839);
            }
            builder.setSmallIcon(17301624);
            builder.setDeleteIntent(PendingIntent.getBroadcast(this.mContext, 0, buildSnoozeRapidIntent(networkPolicy.template), 134217728));
            Intent viewIntent2 = buildViewDataUsageIntent(res, networkPolicy.template);
            if (isHeadlessSystemUserBuild()) {
                builder.setContentIntent(PendingIntent.getActivityAsUser(this.mContext, 0, viewIntent2, 134217728, (Bundle) null, UserHandle.CURRENT));
            } else {
                builder.setContentIntent(PendingIntent.getActivity(this.mContext, 0, viewIntent2, 134217728));
            }
        }
        builder.setTicker(title);
        builder.setContentTitle(title);
        builder.setContentText(body);
        builder.setStyle(new Notification.BigTextStyle().bigText(body));
        ((NotificationManager) this.mContext.getSystemService(NotificationManager.class)).notifyAsUser(notificationId.getTag(), notificationId.getId(), builder.build(), UserHandle.ALL);
        this.mActiveNotifs.add(notificationId);
    }

    private void cancelNotification(NotificationId notificationId) {
        ((NotificationManager) this.mContext.getSystemService(NotificationManager.class)).cancel(notificationId.getTag(), notificationId.getId());
    }

    /* Debug info: failed to restart local var, previous not found, register: 3 */
    /* access modifiers changed from: private */
    public void updateNetworksInternal() {
        updateSubscriptions();
        synchronized (this.mUidRulesFirstLock) {
            synchronized (this.mNetworkPoliciesSecondLock) {
                ensureActiveMobilePolicyAL();
                normalizePoliciesNL();
                updateNetworkEnabledNL();
                updateNetworkRulesNL();
                updateNotificationsNL();
            }
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void updateNetworks() throws InterruptedException {
        updateNetworksInternal();
        CountDownLatch latch = new CountDownLatch(1);
        this.mHandler.post(new Runnable(latch) {
            private final /* synthetic */ CountDownLatch f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                this.f$0.countDown();
            }
        });
        latch.await(5, TimeUnit.SECONDS);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public Handler getHandlerForTesting() {
        return this.mHandler;
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mNetworkPoliciesSecondLock"})
    public boolean maybeUpdateMobilePolicyCycleAL(int subId, String subscriberId) {
        if (LOGV) {
            Slog.v(TAG, "maybeUpdateMobilePolicyCycleAL()");
        }
        boolean policyUpdated = false;
        NetworkIdentity probeIdent = new NetworkIdentity(0, 0, subscriberId, (String) null, false, true, true);
        for (int i = this.mNetworkPolicy.size() - 1; i >= 0; i--) {
            if (this.mNetworkPolicy.keyAt(i).matches(probeIdent)) {
                policyUpdated |= updateDefaultMobilePolicyAL(subId, this.mNetworkPolicy.valueAt(i));
            }
        }
        return policyUpdated;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public int getCycleDayFromCarrierConfig(PersistableBundle config, int fallbackCycleDay) {
        int cycleDay;
        if (config == null || (cycleDay = config.getInt("monthly_data_cycle_day_int")) == -1) {
            return fallbackCycleDay;
        }
        Calendar cal = Calendar.getInstance();
        if (cycleDay >= cal.getMinimum(5) && cycleDay <= cal.getMaximum(5)) {
            return cycleDay;
        }
        Slog.e(TAG, "Invalid date in CarrierConfigManager.KEY_MONTHLY_DATA_CYCLE_DAY_INT: " + cycleDay);
        return fallbackCycleDay;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public long getWarningBytesFromCarrierConfig(PersistableBundle config, long fallbackWarningBytes) {
        if (config == null) {
            return fallbackWarningBytes;
        }
        long warningBytes = config.getLong("data_warning_threshold_bytes_long");
        if (warningBytes == -2) {
            return -1;
        }
        if (warningBytes == -1) {
            return getPlatformDefaultWarningBytes();
        }
        if (warningBytes >= 0) {
            return warningBytes;
        }
        Slog.e(TAG, "Invalid value in CarrierConfigManager.KEY_DATA_WARNING_THRESHOLD_BYTES_LONG; expected a non-negative value but got: " + warningBytes);
        return fallbackWarningBytes;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public long getLimitBytesFromCarrierConfig(PersistableBundle config, long fallbackLimitBytes) {
        if (config == null) {
            return fallbackLimitBytes;
        }
        long limitBytes = config.getLong("data_limit_threshold_bytes_long");
        if (limitBytes == -2) {
            return -1;
        }
        if (limitBytes == -1) {
            return getPlatformDefaultLimitBytes();
        }
        if (limitBytes >= 0) {
            return limitBytes;
        }
        Slog.e(TAG, "Invalid value in CarrierConfigManager.KEY_DATA_LIMIT_THRESHOLD_BYTES_LONG; expected a non-negative value but got: " + limitBytes);
        return fallbackLimitBytes;
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mUidRulesFirstLock", "mNetworkPoliciesSecondLock"})
    public void handleNetworkPoliciesUpdateAL(boolean shouldNormalizePolicies) {
        if (shouldNormalizePolicies) {
            normalizePoliciesNL();
        }
        updateNetworkEnabledNL();
        updateNetworkRulesNL();
        updateNotificationsNL();
        writePolicyAL();
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mNetworkPoliciesSecondLock"})
    public void updateNetworkEnabledNL() {
        if (LOGV) {
            Slog.v(TAG, "updateNetworkEnabledNL()");
        }
        Trace.traceBegin(2097152, "updateNetworkEnabledNL");
        long startTime = this.mStatLogger.getTime();
        int i = this.mNetworkPolicy.size() - 1;
        while (true) {
            boolean networkEnabled = false;
            if (i >= 0) {
                NetworkPolicy policy = this.mNetworkPolicy.valueAt(i);
                if (policy.limitBytes == -1 || !policy.hasCycle()) {
                    setNetworkTemplateEnabled(policy.template, true);
                } else {
                    Pair<ZonedDateTime, ZonedDateTime> cycle = (Pair) NetworkPolicyManager.cycleIterator(policy).next();
                    long start = ((ZonedDateTime) cycle.first).toInstant().toEpochMilli();
                    if (!(policy.isOverLimit(getTotalBytes(policy.template, start, ((ZonedDateTime) cycle.second).toInstant().toEpochMilli())) && policy.lastLimitSnooze < start)) {
                        networkEnabled = true;
                    }
                    setNetworkTemplateEnabled(policy.template, networkEnabled);
                }
                i--;
            } else {
                this.mStatLogger.logDurationStat(0, startTime);
                Trace.traceEnd(2097152);
                return;
            }
        }
    }

    private void setNetworkTemplateEnabled(NetworkTemplate template, boolean enabled) {
        this.mHandler.obtainMessage(18, enabled, 0, template).sendToTarget();
    }

    /* access modifiers changed from: private */
    public void setNetworkTemplateEnabledInner(NetworkTemplate template, boolean enabled) {
    }

    private static void collectIfaces(ArraySet<String> ifaces, NetworkState state) {
        String baseIface = state.linkProperties.getInterfaceName();
        if (baseIface != null) {
            ifaces.add(baseIface);
        }
        for (LinkProperties stackedLink : state.linkProperties.getStackedLinks()) {
            String stackedIface = stackedLink.getInterfaceName();
            if (stackedIface != null) {
                ifaces.add(stackedIface);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void updateSubscriptions() {
        if (LOGV) {
            Slog.v(TAG, "updateSubscriptions()");
        }
        Trace.traceBegin(2097152, "updateSubscriptions");
        TelephonyManager tm = (TelephonyManager) this.mContext.getSystemService(TelephonyManager.class);
        int[] subIds = ArrayUtils.defeatNullable(((SubscriptionManager) this.mContext.getSystemService(SubscriptionManager.class)).getActiveSubscriptionIdList());
        String[] mergedSubscriberIds = ArrayUtils.defeatNullable(tm.getMergedSubscriberIds());
        SparseArray<String> subIdToSubscriberId = new SparseArray<>(subIds.length);
        for (int subId : subIds) {
            String subscriberId = tm.getSubscriberId(subId);
            if (!TextUtils.isEmpty(subscriberId)) {
                subIdToSubscriberId.put(subId, subscriberId);
            } else {
                Slog.wtf(TAG, "Missing subscriberId for subId " + subId);
            }
        }
        synchronized (this.mNetworkPoliciesSecondLock) {
            this.mSubIdToSubscriberId.clear();
            for (int i = 0; i < subIdToSubscriberId.size(); i++) {
                this.mSubIdToSubscriberId.put(subIdToSubscriberId.keyAt(i), subIdToSubscriberId.valueAt(i));
            }
            this.mMergedSubscriberIds = mergedSubscriberIds;
        }
        Trace.traceEnd(2097152);
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mNetworkPoliciesSecondLock"})
    public void updateNetworkRulesNL() {
        boolean z;
        long j;
        NetworkState[] states;
        int i;
        int i2;
        long j2;
        ContentResolver cr;
        long quotaBytes;
        int subId;
        int i3;
        NetworkPolicy policy;
        long quotaBytes2;
        if (LOGV) {
            Slog.v(TAG, "updateNetworkRulesNL()");
        }
        Trace.traceBegin(2097152, "updateNetworkRulesNL");
        try {
            NetworkState[] states2 = defeatNullable(this.mConnManager.getAllNetworkState());
            this.mNetIdToSubId.clear();
            ArrayMap arrayMap = new ArrayMap();
            int length = states2.length;
            int i4 = 0;
            while (true) {
                z = true;
                if (i4 >= length) {
                    break;
                }
                NetworkState state = states2[i4];
                if (state.network != null) {
                    this.mNetIdToSubId.put(state.network.netId, parseSubId(state));
                }
                if (state.networkInfo != null && state.networkInfo.isConnected()) {
                    arrayMap.put(state, NetworkIdentity.buildNetworkIdentity(this.mContext, state, true));
                }
                i4++;
            }
            ArraySet<String> newMeteredIfaces = new ArraySet<>();
            ArraySet arraySet = new ArraySet();
            long lowestRule = Long.MAX_VALUE;
            int i5 = this.mNetworkPolicy.size() - 1;
            while (i5 >= 0) {
                NetworkPolicy policy2 = this.mNetworkPolicy.valueAt(i5);
                arraySet.clear();
                for (int j3 = arrayMap.size() - 1; j3 >= 0; j3--) {
                    if (policy2.template.matches((NetworkIdentity) arrayMap.valueAt(j3))) {
                        collectIfaces(arraySet, (NetworkState) arrayMap.keyAt(j3));
                    }
                }
                if (LOGD != 0) {
                    Slog.d(TAG, "Applying " + policy2 + " to ifaces " + arraySet);
                }
                boolean hasWarning = policy2.warningBytes != -1;
                boolean hasLimit = policy2.limitBytes != -1;
                if (hasLimit || policy2.metered) {
                    if (!hasLimit || !policy2.hasCycle()) {
                        i3 = i5;
                        policy = policy2;
                        quotaBytes2 = JobStatus.NO_LATEST_RUNTIME;
                    } else {
                        Pair<ZonedDateTime, ZonedDateTime> cycle = (Pair) NetworkPolicyManager.cycleIterator(policy2).next();
                        long start = ((ZonedDateTime) cycle.first).toInstant().toEpochMilli();
                        Pair<ZonedDateTime, ZonedDateTime> pair = cycle;
                        i3 = i5;
                        policy = policy2;
                        long totalBytes = getTotalBytes(policy2.template, start, ((ZonedDateTime) cycle.second).toInstant().toEpochMilli());
                        if (policy.lastLimitSnooze >= start) {
                            quotaBytes2 = JobStatus.NO_LATEST_RUNTIME;
                        } else {
                            quotaBytes2 = Math.max(1, policy.limitBytes - totalBytes);
                        }
                    }
                    if (arraySet.size() > 1) {
                        Slog.w(TAG, "shared quota unsupported; generating rule for each iface");
                    }
                    for (int j4 = arraySet.size() - 1; j4 >= 0; j4--) {
                        String iface = (String) arraySet.valueAt(j4);
                        setInterfaceQuotaAsync(iface, quotaBytes2);
                        newMeteredIfaces.add(iface);
                    }
                } else {
                    i3 = i5;
                    policy = policy2;
                }
                if (hasWarning && policy.warningBytes < lowestRule) {
                    lowestRule = policy.warningBytes;
                }
                if (hasLimit && policy.limitBytes < lowestRule) {
                    lowestRule = policy.limitBytes;
                }
                i5 = i3 - 1;
            }
            int i6 = i5;
            int length2 = states2.length;
            int i7 = 0;
            while (true) {
                j = JobStatus.NO_LATEST_RUNTIME;
                if (i7 >= length2) {
                    break;
                }
                NetworkState state2 = states2[i7];
                if (state2.networkInfo != null && state2.networkInfo.isConnected() && !state2.networkCapabilities.hasCapability(11)) {
                    arraySet.clear();
                    collectIfaces(arraySet, state2);
                    for (int j5 = arraySet.size() - 1; j5 >= 0; j5--) {
                        String iface2 = (String) arraySet.valueAt(j5);
                        if (!newMeteredIfaces.contains(iface2)) {
                            setInterfaceQuotaAsync(iface2, JobStatus.NO_LATEST_RUNTIME);
                            newMeteredIfaces.add(iface2);
                        }
                    }
                }
                i7++;
            }
            for (int i8 = this.mMeteredIfaces.size() - 1; i8 >= 0; i8--) {
                String iface3 = this.mMeteredIfaces.valueAt(i8);
                if (!newMeteredIfaces.contains(iface3)) {
                    removeInterfaceQuotaAsync(iface3);
                }
            }
            this.mMeteredIfaces = newMeteredIfaces;
            ContentResolver cr2 = this.mContext.getContentResolver();
            if (Settings.Global.getInt(cr2, "netpolicy_quota_enabled", 1) == 0) {
                z = false;
            }
            boolean quotaEnabled = z;
            long quotaUnlimited = Settings.Global.getLong(cr2, "netpolicy_quota_unlimited", QUOTA_UNLIMITED_DEFAULT);
            float quotaLimited = Settings.Global.getFloat(cr2, "netpolicy_quota_limited", 0.1f);
            this.mSubscriptionOpportunisticQuota.clear();
            int length3 = states2.length;
            int i9 = 0;
            while (i9 < length3) {
                NetworkState state3 = states2[i9];
                if (!quotaEnabled) {
                    states = states2;
                    i2 = length3;
                    i = i9;
                    j2 = j;
                    cr = cr2;
                } else if (state3.network == null) {
                    states = states2;
                    i2 = length3;
                    i = i9;
                    j2 = j;
                    cr = cr2;
                } else {
                    int subId2 = getSubIdLocked(state3.network);
                    SubscriptionPlan plan = getPrimarySubscriptionPlanLocked(subId2);
                    if (plan == null) {
                        states = states2;
                        i2 = length3;
                        i = i9;
                        j2 = j;
                        cr = cr2;
                    } else {
                        long limitBytes = plan.getDataLimitBytes();
                        if (!state3.networkCapabilities.hasCapability(18)) {
                            quotaBytes = 0;
                            states = states2;
                            subId = subId2;
                            NetworkState networkState = state3;
                            i2 = length3;
                            i = i9;
                            j2 = j;
                            cr = cr2;
                        } else if (limitBytes == -1) {
                            quotaBytes = -1;
                            states = states2;
                            subId = subId2;
                            NetworkState networkState2 = state3;
                            i2 = length3;
                            i = i9;
                            j2 = j;
                            cr = cr2;
                        } else if (limitBytes == j) {
                            quotaBytes = quotaUnlimited;
                            states = states2;
                            subId = subId2;
                            NetworkState networkState3 = state3;
                            i2 = length3;
                            i = i9;
                            j2 = j;
                            cr = cr2;
                        } else {
                            Range<ZonedDateTime> cycle2 = plan.cycleIterator().next();
                            long start2 = cycle2.getLower().toInstant().toEpochMilli();
                            long end = cycle2.getUpper().toInstant().toEpochMilli();
                            Instant now = this.mClock.instant();
                            long startOfDay = ZonedDateTime.ofInstant(now, cycle2.getLower().getZone()).truncatedTo(ChronoUnit.DAYS).toInstant().toEpochMilli();
                            NetworkState networkState4 = state3;
                            i2 = length3;
                            i = i9;
                            states = states2;
                            subId = subId2;
                            j2 = JobStatus.NO_LATEST_RUNTIME;
                            long totalBytes2 = getTotalBytes(NetworkTemplate.buildTemplateMobileAll(state3.subscriberId), start2, startOfDay);
                            long remainingBytes = limitBytes - totalBytes2;
                            long j6 = totalBytes2;
                            cr = cr2;
                            long j7 = remainingBytes;
                            quotaBytes = Math.max(0, (long) (((float) (remainingBytes / ((((end - now.toEpochMilli()) - 1) / TimeUnit.DAYS.toMillis(1)) + 1))) * quotaLimited));
                        }
                        this.mSubscriptionOpportunisticQuota.put(subId, quotaBytes);
                    }
                }
                i9 = i + 1;
                cr2 = cr;
                j = j2;
                length3 = i2;
                states2 = states;
            }
            ArraySet<String> arraySet2 = this.mMeteredIfaces;
            this.mHandler.obtainMessage(2, (String[]) arraySet2.toArray(new String[arraySet2.size()])).sendToTarget();
            this.mHandler.obtainMessage(7, Long.valueOf(lowestRule)).sendToTarget();
            Trace.traceEnd(2097152);
        } catch (RemoteException e) {
        }
    }

    @GuardedBy({"mNetworkPoliciesSecondLock"})
    private void ensureActiveMobilePolicyAL() {
        if (LOGV) {
            Slog.v(TAG, "ensureActiveMobilePolicyAL()");
        }
        if (!this.mSuppressDefaultPolicy) {
            for (int i = 0; i < this.mSubIdToSubscriberId.size(); i++) {
                ensureActiveMobilePolicyAL(this.mSubIdToSubscriberId.keyAt(i), this.mSubIdToSubscriberId.valueAt(i));
            }
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mNetworkPoliciesSecondLock"})
    public boolean ensureActiveMobilePolicyAL(int subId, String subscriberId) {
        NetworkIdentity probeIdent = new NetworkIdentity(0, 0, subscriberId, (String) null, false, true, true);
        int i = this.mNetworkPolicy.size() - 1;
        while (i >= 0) {
            NetworkTemplate template = this.mNetworkPolicy.keyAt(i);
            if (!template.matches(probeIdent)) {
                i--;
            } else if (!LOGD) {
                return false;
            } else {
                Slog.d(TAG, "Found template " + template + " which matches subscriber " + NetworkIdentity.scrubSubscriberId(subscriberId));
                return false;
            }
        }
        Slog.i(TAG, "No policy for subscriber " + NetworkIdentity.scrubSubscriberId(subscriberId) + "; generating default policy");
        addNetworkPolicyAL(buildDefaultMobilePolicy(subId, subscriberId));
        return true;
    }

    private long getPlatformDefaultWarningBytes() {
        int dataWarningConfig = this.mContext.getResources().getInteger(17694856);
        if (((long) dataWarningConfig) == -1) {
            return -1;
        }
        return ((long) dataWarningConfig) * 1048576;
    }

    private long getPlatformDefaultLimitBytes() {
        return -1;
    }

    /* Debug info: failed to restart local var, previous not found, register: 17 */
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
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:598)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:49)
        */
    @com.android.internal.annotations.VisibleForTesting
    android.net.NetworkPolicy buildDefaultMobilePolicy(int r18, java.lang.String r19) {
        /*
            r17 = this;
            r1 = r17
            android.net.NetworkTemplate r15 = android.net.NetworkTemplate.buildTemplateMobileAll(r19)
            java.time.ZonedDateTime r0 = java.time.ZonedDateTime.now()
            int r0 = r0.getDayOfMonth()
            java.time.ZoneId r2 = java.time.ZoneId.systemDefault()
            android.util.RecurrenceRule r16 = android.net.NetworkPolicy.buildRule(r0, r2)
            android.net.NetworkPolicy r0 = new android.net.NetworkPolicy
            long r5 = r17.getPlatformDefaultWarningBytes()
            long r7 = r17.getPlatformDefaultLimitBytes()
            r9 = -1
            r11 = -1
            r13 = 1
            r14 = 1
            r2 = r0
            r3 = r15
            r4 = r16
            r2.<init>(r3, r4, r5, r7, r9, r11, r13, r14)
            java.lang.Object r3 = r1.mUidRulesFirstLock
            monitor-enter(r3)
            java.lang.Object r4 = r1.mNetworkPoliciesSecondLock     // Catch:{ all -> 0x003e }
            monitor-enter(r4)     // Catch:{ all -> 0x003e }
            r5 = r18
            r1.updateDefaultMobilePolicyAL(r5, r2)     // Catch:{ all -> 0x003b }
            monitor-exit(r4)     // Catch:{ all -> 0x003b }
            monitor-exit(r3)     // Catch:{ all -> 0x0043 }
            return r2
        L_0x003b:
            r0 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x003b }
            throw r0     // Catch:{ all -> 0x0043 }
        L_0x003e:
            r0 = move-exception
            r5 = r18
        L_0x0041:
            monitor-exit(r3)     // Catch:{ all -> 0x0043 }
            throw r0
        L_0x0043:
            r0 = move-exception
            goto L_0x0041
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.net.NetworkPolicyManagerService.buildDefaultMobilePolicy(int, java.lang.String):android.net.NetworkPolicy");
    }

    @GuardedBy({"mNetworkPoliciesSecondLock"})
    private boolean updateDefaultMobilePolicyAL(int subId, NetworkPolicy policy) {
        int currentCycleDay;
        int i = subId;
        NetworkPolicy networkPolicy = policy;
        if (!networkPolicy.inferred) {
            if (LOGD) {
                Slog.d(TAG, "Ignoring user-defined policy " + networkPolicy);
            }
            return false;
        }
        NetworkTemplate networkTemplate = networkPolicy.template;
        RecurrenceRule recurrenceRule = networkPolicy.cycleRule;
        long j = networkPolicy.warningBytes;
        long j2 = networkPolicy.limitBytes;
        long j3 = networkPolicy.lastWarningSnooze;
        long j4 = networkPolicy.lastLimitSnooze;
        NetworkPolicy networkPolicy2 = new NetworkPolicy(networkTemplate, recurrenceRule, j, j2, j3, j4, networkPolicy.metered, networkPolicy.inferred);
        SubscriptionPlan[] plans = this.mSubscriptionPlans.get(i);
        if (!ArrayUtils.isEmpty(plans)) {
            SubscriptionPlan plan = plans[0];
            networkPolicy.cycleRule = plan.getCycleRule();
            long planLimitBytes = plan.getDataLimitBytes();
            if (planLimitBytes == -1) {
                networkPolicy.warningBytes = getPlatformDefaultWarningBytes();
                networkPolicy.limitBytes = getPlatformDefaultLimitBytes();
            } else if (planLimitBytes == JobStatus.NO_LATEST_RUNTIME) {
                networkPolicy.warningBytes = -1;
                networkPolicy.limitBytes = -1;
            } else {
                networkPolicy.warningBytes = (9 * planLimitBytes) / 10;
                int dataLimitBehavior = plan.getDataLimitBehavior();
                if (dataLimitBehavior == 0 || dataLimitBehavior == 1) {
                    networkPolicy.limitBytes = planLimitBytes;
                } else {
                    networkPolicy.limitBytes = -1;
                }
            }
        } else {
            PersistableBundle config = this.mCarrierConfigManager.getConfigForSubId(i);
            if (networkPolicy.cycleRule.isMonthly()) {
                currentCycleDay = networkPolicy.cycleRule.start.getDayOfMonth();
            } else {
                currentCycleDay = -1;
            }
            networkPolicy.cycleRule = NetworkPolicy.buildRule(getCycleDayFromCarrierConfig(config, currentCycleDay), ZoneId.systemDefault());
            networkPolicy.warningBytes = getWarningBytesFromCarrierConfig(config, networkPolicy.warningBytes);
            networkPolicy.limitBytes = getLimitBytesFromCarrierConfig(config, networkPolicy.limitBytes);
        }
        if (networkPolicy.equals(networkPolicy2)) {
            return false;
        }
        Slog.d(TAG, "Updated " + networkPolicy2 + " to " + networkPolicy);
        return true;
    }

    @GuardedBy({"mUidRulesFirstLock", "mNetworkPoliciesSecondLock"})
    private void readPolicyAL() {
        FileInputStream fis;
        Throwable th;
        Exception e;
        int type;
        int version;
        String networkId;
        int type2;
        RecurrenceRule cycleRule;
        long lastLimitSnooze;
        boolean metered;
        long lastWarningSnooze;
        boolean inferred;
        String cycleTimezone;
        if (LOGV) {
            Slog.v(TAG, "readPolicyAL()");
        }
        this.mNetworkPolicy.clear();
        this.mSubscriptionPlans.clear();
        this.mSubscriptionPlansOwner.clear();
        this.mUidPolicy.clear();
        FileInputStream fis2 = null;
        try {
            fis2 = this.mPolicyFile.openRead();
            try {
                XmlPullParser in = Xml.newPullParser();
                in.setInput(fis2, StandardCharsets.UTF_8.name());
                SparseBooleanArray whitelistedRestrictBackground = new SparseBooleanArray();
                int version2 = 1;
                boolean z = false;
                boolean insideWhitelist = false;
                while (true) {
                    int next = in.next();
                    type = next;
                    boolean z2 = true;
                    if (next == 1) {
                        break;
                    }
                    String tag = in.getName();
                    if (type == 2) {
                        if (TAG_POLICY_LIST.equals(tag)) {
                            boolean z3 = this.mRestrictBackground;
                            version2 = XmlUtils.readIntAttribute(in, ATTR_VERSION);
                            if (version2 < 3 || !XmlUtils.readBooleanAttribute(in, ATTR_RESTRICT_BACKGROUND)) {
                                z2 = z;
                            }
                            this.mLoadedRestrictBackground = z2;
                            fis = fis2;
                            int i = type;
                        } else if (TAG_NETWORK_POLICY.equals(tag)) {
                            int networkTemplate = XmlUtils.readIntAttribute(in, ATTR_NETWORK_TEMPLATE);
                            String subscriberId = in.getAttributeValue((String) null, ATTR_SUBSCRIBER_ID);
                            if (version2 >= 9) {
                                fis = fis2;
                                try {
                                    networkId = in.getAttributeValue((String) null, ATTR_NETWORK_ID);
                                } catch (FileNotFoundException e2) {
                                    FileNotFoundException fileNotFoundException = e2;
                                    upgradeDefaultBackgroundDataUL();
                                    IoUtils.closeQuietly(fis);
                                } catch (Exception e3) {
                                    e = e3;
                                    Log.wtf(TAG, "problem reading network policy", e);
                                    IoUtils.closeQuietly(fis);
                                }
                            } else {
                                fis = fis2;
                                networkId = null;
                            }
                            if (version2 >= 11) {
                                String start = XmlUtils.readStringAttribute(in, ATTR_CYCLE_START);
                                String end = XmlUtils.readStringAttribute(in, ATTR_CYCLE_END);
                                type2 = type;
                                String str = start;
                                String str2 = end;
                                cycleRule = new RecurrenceRule(RecurrenceRule.convertZonedDateTime(start), RecurrenceRule.convertZonedDateTime(end), RecurrenceRule.convertPeriod(XmlUtils.readStringAttribute(in, ATTR_CYCLE_PERIOD)));
                            } else {
                                type2 = type;
                                int cycleDay = XmlUtils.readIntAttribute(in, ATTR_CYCLE_DAY);
                                if (version2 >= 6) {
                                    cycleTimezone = in.getAttributeValue((String) null, ATTR_CYCLE_TIMEZONE);
                                } else {
                                    cycleTimezone = "UTC";
                                }
                                cycleRule = NetworkPolicy.buildRule(cycleDay, ZoneId.of(cycleTimezone));
                            }
                            long warningBytes = XmlUtils.readLongAttribute(in, ATTR_WARNING_BYTES);
                            long limitBytes = XmlUtils.readLongAttribute(in, ATTR_LIMIT_BYTES);
                            if (version2 >= 5) {
                                lastLimitSnooze = XmlUtils.readLongAttribute(in, ATTR_LAST_LIMIT_SNOOZE);
                            } else if (version2 >= 2) {
                                lastLimitSnooze = XmlUtils.readLongAttribute(in, ATTR_LAST_SNOOZE);
                            } else {
                                lastLimitSnooze = -1;
                            }
                            if (version2 >= 4) {
                                metered = XmlUtils.readBooleanAttribute(in, ATTR_METERED);
                            } else if (networkTemplate != 1) {
                                metered = false;
                            } else {
                                metered = true;
                            }
                            if (version2 >= 5) {
                                lastWarningSnooze = XmlUtils.readLongAttribute(in, ATTR_LAST_WARNING_SNOOZE);
                            } else {
                                lastWarningSnooze = -1;
                            }
                            if (version2 >= 7) {
                                inferred = XmlUtils.readBooleanAttribute(in, ATTR_INFERRED);
                            } else {
                                inferred = false;
                            }
                            version = version2;
                            NetworkTemplate template = new NetworkTemplate(networkTemplate, subscriberId, networkId);
                            if (template.isPersistable()) {
                                String str3 = networkId;
                                int i2 = networkTemplate;
                                this.mNetworkPolicy.put(template, new NetworkPolicy(template, cycleRule, warningBytes, limitBytes, lastWarningSnooze, lastLimitSnooze, metered, inferred));
                            } else {
                                int i3 = networkTemplate;
                            }
                            int i4 = type2;
                        } else {
                            fis = fis2;
                            version = version2;
                            int type3 = type;
                            if (TAG_SUBSCRIPTION_PLAN.equals(tag)) {
                                String start2 = XmlUtils.readStringAttribute(in, ATTR_CYCLE_START);
                                String end2 = XmlUtils.readStringAttribute(in, ATTR_CYCLE_END);
                                String period = XmlUtils.readStringAttribute(in, ATTR_CYCLE_PERIOD);
                                SubscriptionPlan.Builder builder = new SubscriptionPlan.Builder(RecurrenceRule.convertZonedDateTime(start2), RecurrenceRule.convertZonedDateTime(end2), RecurrenceRule.convertPeriod(period));
                                builder.setTitle(XmlUtils.readStringAttribute(in, ATTR_TITLE));
                                builder.setSummary(XmlUtils.readStringAttribute(in, ATTR_SUMMARY));
                                long limitBytes2 = XmlUtils.readLongAttribute(in, ATTR_LIMIT_BYTES, -1);
                                int limitBehavior = XmlUtils.readIntAttribute(in, ATTR_LIMIT_BEHAVIOR, -1);
                                if (!(limitBytes2 == -1 || limitBehavior == -1)) {
                                    builder.setDataLimit(limitBytes2, limitBehavior);
                                }
                                String end3 = end2;
                                String str4 = period;
                                long usageBytes = XmlUtils.readLongAttribute(in, ATTR_USAGE_BYTES, -1);
                                long usageTime = XmlUtils.readLongAttribute(in, ATTR_USAGE_TIME, -1);
                                int i5 = limitBehavior;
                                long usageBytes2 = usageBytes;
                                if (usageBytes2 != -1) {
                                    long j = limitBytes2;
                                    String str5 = end3;
                                    long usageTime2 = usageTime;
                                    if (usageTime2 != -1) {
                                        builder.setDataUsage(usageBytes2, usageTime2);
                                    }
                                } else {
                                    String str6 = end3;
                                    long j2 = usageTime;
                                }
                                int subId = XmlUtils.readIntAttribute(in, ATTR_SUB_ID);
                                String str7 = start2;
                                SubscriptionPlan.Builder builder2 = builder;
                                long j3 = usageBytes2;
                                this.mSubscriptionPlans.put(subId, (SubscriptionPlan[]) ArrayUtils.appendElement(SubscriptionPlan.class, this.mSubscriptionPlans.get(subId), builder.build()));
                                this.mSubscriptionPlansOwner.put(subId, XmlUtils.readStringAttribute(in, ATTR_OWNER_PACKAGE));
                                int i6 = type3;
                            } else if (TAG_UID_POLICY.equals(tag)) {
                                int uid = XmlUtils.readIntAttribute(in, "uid");
                                int policy = XmlUtils.readIntAttribute(in, ATTR_POLICY);
                                if (UserHandle.isApp(uid)) {
                                    setUidPolicyUncheckedUL(uid, policy, false);
                                } else {
                                    Slog.w(TAG, "unable to apply policy to UID " + uid + "; ignoring");
                                }
                                int uid2 = type3;
                            } else if (TAG_APP_POLICY.equals(tag)) {
                                int appId = XmlUtils.readIntAttribute(in, ATTR_APP_ID);
                                int policy2 = XmlUtils.readIntAttribute(in, ATTR_POLICY);
                                int uid3 = UserHandle.getUid(0, appId);
                                if (UserHandle.isApp(uid3)) {
                                    setUidPolicyUncheckedUL(uid3, policy2, false);
                                } else {
                                    Slog.w(TAG, "unable to apply policy to UID " + uid3 + "; ignoring");
                                }
                                int appId2 = type3;
                            } else if (TAG_WHITELIST.equals(tag)) {
                                insideWhitelist = true;
                                version2 = version;
                                int i7 = type3;
                            } else {
                                if (TAG_RESTRICT_BACKGROUND.equals(tag) && insideWhitelist) {
                                    whitelistedRestrictBackground.append(XmlUtils.readIntAttribute(in, "uid"), true);
                                } else if (TAG_REVOKED_RESTRICT_BACKGROUND.equals(tag) && insideWhitelist) {
                                    this.mRestrictBackgroundWhitelistRevokedUids.put(XmlUtils.readIntAttribute(in, "uid"), true);
                                    int uid4 = type3;
                                }
                                int i8 = type3;
                            }
                        }
                        fis2 = fis;
                        z = false;
                    } else {
                        fis = fis2;
                        version = version2;
                        if (type == 3 && TAG_WHITELIST.equals(tag)) {
                            insideWhitelist = false;
                            version2 = version;
                            fis2 = fis;
                            z = false;
                        }
                    }
                    version2 = version;
                    fis2 = fis;
                    z = false;
                }
                fis = fis2;
                int i9 = version2;
                int i10 = type;
                int size = whitelistedRestrictBackground.size();
                for (int i11 = 0; i11 < size; i11++) {
                    int uid5 = whitelistedRestrictBackground.keyAt(i11);
                    int policy3 = this.mUidPolicy.get(uid5, 0);
                    if ((policy3 & 1) != 0) {
                        Slog.w(TAG, "ignoring restrict-background-whitelist for " + uid5 + " because its policy is " + NetworkPolicyManager.uidPoliciesToString(policy3));
                    } else if (UserHandle.isApp(uid5)) {
                        int newPolicy = policy3 | 4;
                        if (LOGV) {
                            Log.v(TAG, "new policy for " + uid5 + ": " + NetworkPolicyManager.uidPoliciesToString(newPolicy));
                        }
                        setUidPolicyUncheckedUL(uid5, newPolicy, false);
                    } else {
                        Slog.w(TAG, "unable to update policy on UID " + uid5);
                    }
                }
            } catch (FileNotFoundException e4) {
                fis = fis2;
                FileNotFoundException fileNotFoundException2 = e4;
                upgradeDefaultBackgroundDataUL();
                IoUtils.closeQuietly(fis);
            } catch (Exception e5) {
                fis = fis2;
                e = e5;
                Log.wtf(TAG, "problem reading network policy", e);
                IoUtils.closeQuietly(fis);
            } catch (Throwable th2) {
                fis = fis2;
                th = th2;
                IoUtils.closeQuietly(fis);
                throw th;
            }
        } catch (FileNotFoundException e6) {
            fis = fis2;
            FileNotFoundException fileNotFoundException3 = e6;
            upgradeDefaultBackgroundDataUL();
            IoUtils.closeQuietly(fis);
        } catch (Exception e7) {
            fis = fis2;
            e = e7;
            Log.wtf(TAG, "problem reading network policy", e);
            IoUtils.closeQuietly(fis);
        } catch (Throwable th3) {
            th = th3;
            IoUtils.closeQuietly(fis);
            throw th;
        }
        IoUtils.closeQuietly(fis);
    }

    private void upgradeDefaultBackgroundDataUL() {
        boolean z = false;
        if (Settings.Global.getInt(this.mContext.getContentResolver(), "default_restrict_background_data", 0) == 1) {
            z = true;
        }
        this.mLoadedRestrictBackground = z;
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mNetworkPoliciesSecondLock", "mUidRulesFirstLock"})
    public void upgradeWifiMeteredOverrideAL() {
        int i;
        boolean modified = false;
        WifiManager wm = (WifiManager) this.mContext.getSystemService(WifiManager.class);
        List<WifiConfiguration> configs = wm.getConfiguredNetworks();
        int i2 = 0;
        while (i2 < this.mNetworkPolicy.size()) {
            NetworkPolicy policy = this.mNetworkPolicy.valueAt(i2);
            if (policy.template.getMatchRule() != 4 || policy.inferred) {
                i2++;
            } else {
                this.mNetworkPolicy.removeAt(i2);
                modified = true;
                String networkId = NetworkPolicyManager.resolveNetworkId(policy.template.getNetworkId());
                for (WifiConfiguration config : configs) {
                    if (Objects.equals(NetworkPolicyManager.resolveNetworkId(config), networkId)) {
                        Slog.d(TAG, "Found network " + networkId + "; upgrading metered hint");
                        if (policy.metered) {
                            i = 1;
                        } else {
                            i = 2;
                        }
                        config.meteredOverride = i;
                        wm.updateNetwork(config);
                    }
                }
            }
        }
        if (modified) {
            writePolicyAL();
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x0262  */
    @com.android.internal.annotations.GuardedBy({"mUidRulesFirstLock", "mNetworkPoliciesSecondLock"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void writePolicyAL() {
        /*
            r23 = this;
            r1 = r23
            java.lang.String r0 = "subscription-plan"
            java.lang.String r2 = "revoked-restrict-background"
            java.lang.String r3 = "uid-policy"
            java.lang.String r4 = "whitelist"
            java.lang.String r5 = "network-policy"
            java.lang.String r6 = "policy-list"
            boolean r7 = LOGV
            if (r7 == 0) goto L_0x0020
            java.lang.String r7 = "NetworkPolicy"
            java.lang.String r8 = "writePolicyAL()"
            android.util.Slog.v(r7, r8)
        L_0x0020:
            r7 = 0
            android.util.AtomicFile r8 = r1.mPolicyFile     // Catch:{ IOException -> 0x025f }
            java.io.FileOutputStream r8 = r8.startWrite()     // Catch:{ IOException -> 0x025f }
            r7 = r8
            com.android.internal.util.FastXmlSerializer r8 = new com.android.internal.util.FastXmlSerializer     // Catch:{ IOException -> 0x025c }
            r8.<init>()     // Catch:{ IOException -> 0x025c }
            java.nio.charset.Charset r9 = java.nio.charset.StandardCharsets.UTF_8     // Catch:{ IOException -> 0x025c }
            java.lang.String r9 = r9.name()     // Catch:{ IOException -> 0x025c }
            r8.setOutput(r7, r9)     // Catch:{ IOException -> 0x025c }
            r9 = 1
            java.lang.Boolean r9 = java.lang.Boolean.valueOf(r9)     // Catch:{ IOException -> 0x025c }
            r10 = 0
            r8.startDocument(r10, r9)     // Catch:{ IOException -> 0x025c }
            r8.startTag(r10, r6)     // Catch:{ IOException -> 0x025c }
            java.lang.String r9 = "version"
            r11 = 11
            com.android.internal.util.XmlUtils.writeIntAttribute(r8, r9, r11)     // Catch:{ IOException -> 0x025c }
            java.lang.String r9 = "restrictBackground"
            boolean r11 = r1.mRestrictBackground     // Catch:{ IOException -> 0x025c }
            com.android.internal.util.XmlUtils.writeBooleanAttribute(r8, r9, r11)     // Catch:{ IOException -> 0x025c }
            r9 = 0
            r11 = r9
        L_0x0054:
            android.util.ArrayMap<android.net.NetworkTemplate, android.net.NetworkPolicy> r12 = r1.mNetworkPolicy     // Catch:{ IOException -> 0x025c }
            int r12 = r12.size()     // Catch:{ IOException -> 0x025c }
            java.lang.String r13 = "limitBytes"
            java.lang.String r14 = "cyclePeriod"
            java.lang.String r15 = "cycleEnd"
            java.lang.String r9 = "cycleStart"
            if (r11 >= r12) goto L_0x0116
            android.util.ArrayMap<android.net.NetworkTemplate, android.net.NetworkPolicy> r12 = r1.mNetworkPolicy     // Catch:{ IOException -> 0x0111 }
            java.lang.Object r12 = r12.valueAt(r11)     // Catch:{ IOException -> 0x0111 }
            android.net.NetworkPolicy r12 = (android.net.NetworkPolicy) r12     // Catch:{ IOException -> 0x0111 }
            android.net.NetworkTemplate r10 = r12.template     // Catch:{ IOException -> 0x0111 }
            boolean r16 = r10.isPersistable()     // Catch:{ IOException -> 0x0111 }
            if (r16 != 0) goto L_0x007d
            r17 = r2
            r18 = r4
            r16 = r7
            goto L_0x0105
        L_0x007d:
            r16 = r7
            r7 = 0
            r8.startTag(r7, r5)     // Catch:{ IOException -> 0x01e7 }
            java.lang.String r7 = "networkTemplate"
            r17 = r2
            int r2 = r10.getMatchRule()     // Catch:{ IOException -> 0x01e7 }
            com.android.internal.util.XmlUtils.writeIntAttribute(r8, r7, r2)     // Catch:{ IOException -> 0x01e7 }
            java.lang.String r2 = r10.getSubscriberId()     // Catch:{ IOException -> 0x01e7 }
            if (r2 == 0) goto L_0x009f
            java.lang.String r7 = "subscriberId"
            r18 = r4
            r4 = 0
            r8.attribute(r4, r7, r2)     // Catch:{ IOException -> 0x01e7 }
            goto L_0x00a1
        L_0x009f:
            r18 = r4
        L_0x00a1:
            java.lang.String r4 = r10.getNetworkId()     // Catch:{ IOException -> 0x01e7 }
            if (r4 == 0) goto L_0x00b1
            java.lang.String r7 = "networkId"
            r19 = r2
            r2 = 0
            r8.attribute(r2, r7, r4)     // Catch:{ IOException -> 0x01e7 }
            goto L_0x00b3
        L_0x00b1:
            r19 = r2
        L_0x00b3:
            android.util.RecurrenceRule r2 = r12.cycleRule     // Catch:{ IOException -> 0x01e7 }
            java.time.ZonedDateTime r2 = r2.start     // Catch:{ IOException -> 0x01e7 }
            java.lang.String r2 = android.util.RecurrenceRule.convertZonedDateTime(r2)     // Catch:{ IOException -> 0x01e7 }
            com.android.internal.util.XmlUtils.writeStringAttribute(r8, r9, r2)     // Catch:{ IOException -> 0x01e7 }
            android.util.RecurrenceRule r2 = r12.cycleRule     // Catch:{ IOException -> 0x01e7 }
            java.time.ZonedDateTime r2 = r2.end     // Catch:{ IOException -> 0x01e7 }
            java.lang.String r2 = android.util.RecurrenceRule.convertZonedDateTime(r2)     // Catch:{ IOException -> 0x01e7 }
            com.android.internal.util.XmlUtils.writeStringAttribute(r8, r15, r2)     // Catch:{ IOException -> 0x01e7 }
            android.util.RecurrenceRule r2 = r12.cycleRule     // Catch:{ IOException -> 0x01e7 }
            java.time.Period r2 = r2.period     // Catch:{ IOException -> 0x01e7 }
            java.lang.String r2 = android.util.RecurrenceRule.convertPeriod(r2)     // Catch:{ IOException -> 0x01e7 }
            com.android.internal.util.XmlUtils.writeStringAttribute(r8, r14, r2)     // Catch:{ IOException -> 0x01e7 }
            java.lang.String r2 = "warningBytes"
            long r14 = r12.warningBytes     // Catch:{ IOException -> 0x01e7 }
            com.android.internal.util.XmlUtils.writeLongAttribute(r8, r2, r14)     // Catch:{ IOException -> 0x01e7 }
            long r14 = r12.limitBytes     // Catch:{ IOException -> 0x01e7 }
            com.android.internal.util.XmlUtils.writeLongAttribute(r8, r13, r14)     // Catch:{ IOException -> 0x01e7 }
            java.lang.String r2 = "lastWarningSnooze"
            long r13 = r12.lastWarningSnooze     // Catch:{ IOException -> 0x01e7 }
            com.android.internal.util.XmlUtils.writeLongAttribute(r8, r2, r13)     // Catch:{ IOException -> 0x01e7 }
            java.lang.String r2 = "lastLimitSnooze"
            long r13 = r12.lastLimitSnooze     // Catch:{ IOException -> 0x01e7 }
            com.android.internal.util.XmlUtils.writeLongAttribute(r8, r2, r13)     // Catch:{ IOException -> 0x01e7 }
            java.lang.String r2 = "metered"
            boolean r7 = r12.metered     // Catch:{ IOException -> 0x01e7 }
            com.android.internal.util.XmlUtils.writeBooleanAttribute(r8, r2, r7)     // Catch:{ IOException -> 0x01e7 }
            java.lang.String r2 = "inferred"
            boolean r7 = r12.inferred     // Catch:{ IOException -> 0x01e7 }
            com.android.internal.util.XmlUtils.writeBooleanAttribute(r8, r2, r7)     // Catch:{ IOException -> 0x01e7 }
            r2 = 0
            r8.endTag(r2, r5)     // Catch:{ IOException -> 0x01e7 }
        L_0x0105:
            int r11 = r11 + 1
            r7 = r16
            r2 = r17
            r4 = r18
            r9 = 0
            r10 = 0
            goto L_0x0054
        L_0x0111:
            r0 = move-exception
            r16 = r7
            goto L_0x0260
        L_0x0116:
            r17 = r2
            r18 = r4
            r16 = r7
            r2 = 0
            r4 = r2
        L_0x011e:
            android.util.SparseArray<android.telephony.SubscriptionPlan[]> r2 = r1.mSubscriptionPlans     // Catch:{ IOException -> 0x0257 }
            int r2 = r2.size()     // Catch:{ IOException -> 0x0257 }
            if (r4 >= r2) goto L_0x01ec
            android.util.SparseArray<android.telephony.SubscriptionPlan[]> r2 = r1.mSubscriptionPlans     // Catch:{ IOException -> 0x01e7 }
            int r2 = r2.keyAt(r4)     // Catch:{ IOException -> 0x01e7 }
            android.util.SparseArray<java.lang.String> r5 = r1.mSubscriptionPlansOwner     // Catch:{ IOException -> 0x01e7 }
            java.lang.Object r5 = r5.get(r2)     // Catch:{ IOException -> 0x01e7 }
            java.lang.String r5 = (java.lang.String) r5     // Catch:{ IOException -> 0x01e7 }
            android.util.SparseArray<android.telephony.SubscriptionPlan[]> r7 = r1.mSubscriptionPlans     // Catch:{ IOException -> 0x01e7 }
            java.lang.Object r7 = r7.valueAt(r4)     // Catch:{ IOException -> 0x01e7 }
            android.telephony.SubscriptionPlan[] r7 = (android.telephony.SubscriptionPlan[]) r7     // Catch:{ IOException -> 0x01e7 }
            boolean r10 = com.android.internal.util.ArrayUtils.isEmpty(r7)     // Catch:{ IOException -> 0x01e7 }
            if (r10 == 0) goto L_0x0148
            r2 = r9
            r22 = r13
            r10 = r14
            goto L_0x01df
        L_0x0148:
            int r10 = r7.length     // Catch:{ IOException -> 0x01e7 }
            r11 = 0
        L_0x014a:
            if (r11 >= r10) goto L_0x01d5
            r12 = r7[r11]     // Catch:{ IOException -> 0x01e7 }
            r19 = r7
            r7 = 0
            r8.startTag(r7, r0)     // Catch:{ IOException -> 0x01e7 }
            java.lang.String r7 = "subId"
            com.android.internal.util.XmlUtils.writeIntAttribute(r8, r7, r2)     // Catch:{ IOException -> 0x01e7 }
            java.lang.String r7 = "ownerPackage"
            com.android.internal.util.XmlUtils.writeStringAttribute(r8, r7, r5)     // Catch:{ IOException -> 0x01e7 }
            android.util.RecurrenceRule r7 = r12.getCycleRule()     // Catch:{ IOException -> 0x01e7 }
            r20 = r2
            java.time.ZonedDateTime r2 = r7.start     // Catch:{ IOException -> 0x01e7 }
            java.lang.String r2 = android.util.RecurrenceRule.convertZonedDateTime(r2)     // Catch:{ IOException -> 0x01e7 }
            com.android.internal.util.XmlUtils.writeStringAttribute(r8, r9, r2)     // Catch:{ IOException -> 0x01e7 }
            java.time.ZonedDateTime r2 = r7.end     // Catch:{ IOException -> 0x01e7 }
            java.lang.String r2 = android.util.RecurrenceRule.convertZonedDateTime(r2)     // Catch:{ IOException -> 0x01e7 }
            com.android.internal.util.XmlUtils.writeStringAttribute(r8, r15, r2)     // Catch:{ IOException -> 0x01e7 }
            java.time.Period r2 = r7.period     // Catch:{ IOException -> 0x01e7 }
            java.lang.String r2 = android.util.RecurrenceRule.convertPeriod(r2)     // Catch:{ IOException -> 0x01e7 }
            com.android.internal.util.XmlUtils.writeStringAttribute(r8, r14, r2)     // Catch:{ IOException -> 0x01e7 }
            java.lang.String r2 = "title"
            r21 = r5
            java.lang.CharSequence r5 = r12.getTitle()     // Catch:{ IOException -> 0x01e7 }
            com.android.internal.util.XmlUtils.writeStringAttribute(r8, r2, r5)     // Catch:{ IOException -> 0x01e7 }
            java.lang.String r2 = "summary"
            java.lang.CharSequence r5 = r12.getSummary()     // Catch:{ IOException -> 0x01e7 }
            com.android.internal.util.XmlUtils.writeStringAttribute(r8, r2, r5)     // Catch:{ IOException -> 0x01e7 }
            r2 = r9
            r5 = r10
            long r9 = r12.getDataLimitBytes()     // Catch:{ IOException -> 0x01e7 }
            com.android.internal.util.XmlUtils.writeLongAttribute(r8, r13, r9)     // Catch:{ IOException -> 0x01e7 }
            java.lang.String r9 = "limitBehavior"
            int r10 = r12.getDataLimitBehavior()     // Catch:{ IOException -> 0x01e7 }
            com.android.internal.util.XmlUtils.writeIntAttribute(r8, r9, r10)     // Catch:{ IOException -> 0x01e7 }
            java.lang.String r9 = "usageBytes"
            r22 = r13
            r10 = r14
            long r13 = r12.getDataUsageBytes()     // Catch:{ IOException -> 0x01e7 }
            com.android.internal.util.XmlUtils.writeLongAttribute(r8, r9, r13)     // Catch:{ IOException -> 0x01e7 }
            java.lang.String r9 = "usageTime"
            long r13 = r12.getDataUsageTime()     // Catch:{ IOException -> 0x01e7 }
            com.android.internal.util.XmlUtils.writeLongAttribute(r8, r9, r13)     // Catch:{ IOException -> 0x01e7 }
            r9 = 0
            r8.endTag(r9, r0)     // Catch:{ IOException -> 0x01e7 }
            int r11 = r11 + 1
            r9 = r2
            r14 = r10
            r7 = r19
            r2 = r20
            r13 = r22
            r10 = r5
            r5 = r21
            goto L_0x014a
        L_0x01d5:
            r20 = r2
            r21 = r5
            r19 = r7
            r2 = r9
            r22 = r13
            r10 = r14
        L_0x01df:
            int r4 = r4 + 1
            r9 = r2
            r14 = r10
            r13 = r22
            goto L_0x011e
        L_0x01e7:
            r0 = move-exception
            r7 = r16
            goto L_0x0260
        L_0x01ec:
            r0 = 0
        L_0x01ed:
            android.util.SparseIntArray r2 = r1.mUidPolicy     // Catch:{ IOException -> 0x0257 }
            int r2 = r2.size()     // Catch:{ IOException -> 0x0257 }
            java.lang.String r4 = "uid"
            if (r0 >= r2) goto L_0x021b
            android.util.SparseIntArray r2 = r1.mUidPolicy     // Catch:{ IOException -> 0x01e7 }
            int r2 = r2.keyAt(r0)     // Catch:{ IOException -> 0x01e7 }
            android.util.SparseIntArray r5 = r1.mUidPolicy     // Catch:{ IOException -> 0x01e7 }
            int r5 = r5.valueAt(r0)     // Catch:{ IOException -> 0x01e7 }
            if (r5 != 0) goto L_0x0207
            goto L_0x0218
        L_0x0207:
            r7 = 0
            r8.startTag(r7, r3)     // Catch:{ IOException -> 0x01e7 }
            com.android.internal.util.XmlUtils.writeIntAttribute(r8, r4, r2)     // Catch:{ IOException -> 0x01e7 }
            java.lang.String r4 = "policy"
            com.android.internal.util.XmlUtils.writeIntAttribute(r8, r4, r5)     // Catch:{ IOException -> 0x01e7 }
            r4 = 0
            r8.endTag(r4, r3)     // Catch:{ IOException -> 0x01e7 }
        L_0x0218:
            int r0 = r0 + 1
            goto L_0x01ed
        L_0x021b:
            r0 = 0
            r8.endTag(r0, r6)     // Catch:{ IOException -> 0x0257 }
            r2 = r18
            r8.startTag(r0, r2)     // Catch:{ IOException -> 0x0257 }
            android.util.SparseBooleanArray r0 = r1.mRestrictBackgroundWhitelistRevokedUids     // Catch:{ IOException -> 0x0257 }
            int r0 = r0.size()     // Catch:{ IOException -> 0x0257 }
            r3 = 0
        L_0x022b:
            if (r3 >= r0) goto L_0x0245
            android.util.SparseBooleanArray r5 = r1.mRestrictBackgroundWhitelistRevokedUids     // Catch:{ IOException -> 0x01e7 }
            int r5 = r5.keyAt(r3)     // Catch:{ IOException -> 0x01e7 }
            r6 = r17
            r7 = 0
            r8.startTag(r7, r6)     // Catch:{ IOException -> 0x01e7 }
            com.android.internal.util.XmlUtils.writeIntAttribute(r8, r4, r5)     // Catch:{ IOException -> 0x01e7 }
            r8.endTag(r7, r6)     // Catch:{ IOException -> 0x01e7 }
            int r3 = r3 + 1
            r17 = r6
            goto L_0x022b
        L_0x0245:
            r3 = 0
            r8.endTag(r3, r2)     // Catch:{ IOException -> 0x0257 }
            r8.endDocument()     // Catch:{ IOException -> 0x0257 }
            android.util.AtomicFile r2 = r1.mPolicyFile     // Catch:{ IOException -> 0x0257 }
            r3 = r16
            r2.finishWrite(r3)     // Catch:{ IOException -> 0x0254 }
            goto L_0x0268
        L_0x0254:
            r0 = move-exception
            r7 = r3
            goto L_0x0260
        L_0x0257:
            r0 = move-exception
            r3 = r16
            r7 = r3
            goto L_0x0260
        L_0x025c:
            r0 = move-exception
            r3 = r7
            goto L_0x0260
        L_0x025f:
            r0 = move-exception
        L_0x0260:
            if (r7 == 0) goto L_0x0267
            android.util.AtomicFile r2 = r1.mPolicyFile
            r2.failWrite(r7)
        L_0x0267:
            r3 = r7
        L_0x0268:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.net.NetworkPolicyManagerService.writePolicyAL():void");
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    public void setUidPolicy(int uid, int policy) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_NETWORK_POLICY", TAG);
        if (UserHandle.isApp(uid)) {
            synchronized (this.mUidRulesFirstLock) {
                long token = Binder.clearCallingIdentity();
                try {
                    int oldPolicy = this.mUidPolicy.get(uid, 0);
                    if (oldPolicy != policy) {
                        setUidPolicyUncheckedUL(uid, oldPolicy, policy, true);
                        this.mLogger.uidPolicyChanged(uid, oldPolicy, policy);
                    }
                } finally {
                    Binder.restoreCallingIdentity(token);
                }
            }
            return;
        }
        throw new IllegalArgumentException("cannot apply policy to UID " + uid);
    }

    public void addUidPolicy(int uid, int policy) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_NETWORK_POLICY", TAG);
        if (UserHandle.isApp(uid)) {
            synchronized (this.mUidRulesFirstLock) {
                int oldPolicy = this.mUidPolicy.get(uid, 0);
                int policy2 = policy | oldPolicy;
                if (oldPolicy != policy2) {
                    setUidPolicyUncheckedUL(uid, oldPolicy, policy2, true);
                    this.mLogger.uidPolicyChanged(uid, oldPolicy, policy2);
                }
            }
            return;
        }
        throw new IllegalArgumentException("cannot apply policy to UID " + uid);
    }

    public void removeUidPolicy(int uid, int policy) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_NETWORK_POLICY", TAG);
        if (UserHandle.isApp(uid)) {
            synchronized (this.mUidRulesFirstLock) {
                int oldPolicy = this.mUidPolicy.get(uid, 0);
                int policy2 = oldPolicy & (~policy);
                if (oldPolicy != policy2) {
                    setUidPolicyUncheckedUL(uid, oldPolicy, policy2, true);
                    this.mLogger.uidPolicyChanged(uid, oldPolicy, policy2);
                }
            }
            return;
        }
        throw new IllegalArgumentException("cannot apply policy to UID " + uid);
    }

    @GuardedBy({"mUidRulesFirstLock"})
    private void setUidPolicyUncheckedUL(int uid, int oldPolicy, int policy, boolean persist) {
        boolean notifyApp = false;
        setUidPolicyUncheckedUL(uid, policy, false);
        if (!isUidValidForWhitelistRules(uid)) {
            notifyApp = false;
        } else {
            boolean wasBlacklisted = oldPolicy == 1;
            boolean isBlacklisted = policy == 1;
            boolean wasWhitelisted = oldPolicy == 4;
            boolean isWhitelisted = policy == 4;
            boolean wasBlocked = wasBlacklisted || (this.mRestrictBackground && !wasWhitelisted);
            boolean isBlocked = isBlacklisted || (this.mRestrictBackground && !isWhitelisted);
            if (wasWhitelisted && ((!isWhitelisted || isBlacklisted) && this.mDefaultRestrictBackgroundWhitelistUids.get(uid) && !this.mRestrictBackgroundWhitelistRevokedUids.get(uid))) {
                if (LOGD) {
                    Slog.d(TAG, "Adding uid " + uid + " to revoked restrict background whitelist");
                }
                this.mRestrictBackgroundWhitelistRevokedUids.append(uid, true);
            }
            if (wasBlocked != isBlocked) {
                notifyApp = true;
            }
        }
        this.mHandler.obtainMessage(13, uid, policy, Boolean.valueOf(notifyApp)).sendToTarget();
        if (persist) {
            synchronized (this.mNetworkPoliciesSecondLock) {
                writePolicyAL();
            }
        }
    }

    @GuardedBy({"mUidRulesFirstLock"})
    private void setUidPolicyUncheckedUL(int uid, int policy, boolean persist) {
        if (policy == 0) {
            this.mUidPolicy.delete(uid);
        } else {
            this.mUidPolicy.put(uid, policy);
        }
        updateRulesForDataUsageRestrictionsUL(uid);
        if (persist) {
            synchronized (this.mNetworkPoliciesSecondLock) {
                writePolicyAL();
            }
        }
    }

    public int getUidPolicy(int uid) {
        int i;
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_NETWORK_POLICY", TAG);
        synchronized (this.mUidRulesFirstLock) {
            i = this.mUidPolicy.get(uid, 0);
        }
        return i;
    }

    public int[] getUidsWithPolicy(int policy) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_NETWORK_POLICY", TAG);
        int[] uids = new int[0];
        synchronized (this.mUidRulesFirstLock) {
            for (int i = 0; i < this.mUidPolicy.size(); i++) {
                int uid = this.mUidPolicy.keyAt(i);
                int uidPolicy = this.mUidPolicy.valueAt(i);
                if ((policy == 0 && uidPolicy == 0) || (uidPolicy & policy) != 0) {
                    uids = ArrayUtils.appendInt(uids, uid);
                }
            }
        }
        return uids;
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mUidRulesFirstLock"})
    public boolean removeUserStateUL(int userId, boolean writePolicy) {
        this.mLogger.removingUserState(userId);
        boolean changed = false;
        for (int i = this.mRestrictBackgroundWhitelistRevokedUids.size() - 1; i >= 0; i--) {
            if (UserHandle.getUserId(this.mRestrictBackgroundWhitelistRevokedUids.keyAt(i)) == userId) {
                this.mRestrictBackgroundWhitelistRevokedUids.removeAt(i);
                changed = true;
            }
        }
        int[] uids = new int[0];
        for (int i2 = 0; i2 < this.mUidPolicy.size(); i2++) {
            int uid = this.mUidPolicy.keyAt(i2);
            if (UserHandle.getUserId(uid) == userId) {
                uids = ArrayUtils.appendInt(uids, uid);
            }
        }
        if (uids.length > 0) {
            for (int uid2 : uids) {
                this.mUidPolicy.delete(uid2);
            }
            changed = true;
        }
        synchronized (this.mNetworkPoliciesSecondLock) {
            updateRulesForGlobalChangeAL(true);
            if (writePolicy && changed) {
                writePolicyAL();
            }
        }
        return changed;
    }

    public void registerListener(INetworkPolicyListener listener) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        this.mListeners.register(listener);
    }

    public void unregisterListener(INetworkPolicyListener listener) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        this.mListeners.unregister(listener);
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    public void setNetworkPolicies(NetworkPolicy[] policies) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_NETWORK_POLICY", TAG);
        long token = Binder.clearCallingIdentity();
        try {
            synchronized (this.mUidRulesFirstLock) {
                synchronized (this.mNetworkPoliciesSecondLock) {
                    setNetworkPoliciesLocked(policies);
                }
            }
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    /* access modifiers changed from: package-private */
    public void setNetworkPoliciesLocked(NetworkPolicy[] policies) {
        normalizePoliciesNL(policies);
        handleNetworkPoliciesUpdateAL(false);
    }

    /* access modifiers changed from: package-private */
    public void addNetworkPolicyAL(NetworkPolicy policy) {
        setNetworkPoliciesLocked((NetworkPolicy[]) ArrayUtils.appendElement(NetworkPolicy.class, getNetworkPolicies(this.mContext.getOpPackageName()), policy));
    }

    public NetworkPolicy[] getNetworkPolicies(String callingPackage) {
        NetworkPolicy[] policies;
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_NETWORK_POLICY", TAG);
        try {
            this.mContext.enforceCallingOrSelfPermission("android.permission.READ_PRIVILEGED_PHONE_STATE", TAG);
        } catch (SecurityException e) {
            this.mContext.enforceCallingOrSelfPermission("android.permission.READ_PHONE_STATE", TAG);
            if (this.mAppOps.noteOp(51, Binder.getCallingUid(), callingPackage) != 0) {
                return new NetworkPolicy[0];
            }
        }
        synchronized (this.mNetworkPoliciesSecondLock) {
            int size = this.mNetworkPolicy.size();
            policies = new NetworkPolicy[size];
            for (int i = 0; i < size; i++) {
                policies[i] = this.mNetworkPolicy.valueAt(i);
            }
        }
        return policies;
    }

    @GuardedBy({"mNetworkPoliciesSecondLock"})
    private void normalizePoliciesNL() {
        normalizePoliciesNL(getNetworkPolicies(this.mContext.getOpPackageName()));
    }

    @GuardedBy({"mNetworkPoliciesSecondLock"})
    private void normalizePoliciesNL(NetworkPolicy[] policies) {
        this.mNetworkPolicy.clear();
        for (NetworkPolicy policy : policies) {
            if (policy != null) {
                policy.template = NetworkTemplate.normalize(policy.template, this.mMergedSubscriberIds);
                NetworkPolicy existing = this.mNetworkPolicy.get(policy.template);
                if (existing == null || existing.compareTo(policy) > 0) {
                    if (existing != null) {
                        Slog.d(TAG, "Normalization replaced " + existing + " with " + policy);
                    }
                    this.mNetworkPolicy.put(policy.template, policy);
                }
            }
        }
    }

    public void snoozeLimit(NetworkTemplate template) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_NETWORK_POLICY", TAG);
        long token = Binder.clearCallingIdentity();
        try {
            performSnooze(template, 35);
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    /* access modifiers changed from: package-private */
    public void performSnooze(NetworkTemplate template, int type) {
        long currentTime = this.mClock.millis();
        synchronized (this.mUidRulesFirstLock) {
            synchronized (this.mNetworkPoliciesSecondLock) {
                NetworkPolicy policy = this.mNetworkPolicy.get(template);
                if (policy != null) {
                    if (type == 34) {
                        policy.lastWarningSnooze = currentTime;
                    } else if (type == 35) {
                        policy.lastLimitSnooze = currentTime;
                    } else if (type == 45) {
                        policy.lastRapidSnooze = currentTime;
                    } else {
                        throw new IllegalArgumentException("unexpected type");
                    }
                    handleNetworkPoliciesUpdateAL(true);
                } else {
                    throw new IllegalArgumentException("unable to find policy for " + template);
                }
            }
        }
    }

    public void onTetheringChanged(String iface, boolean tethering) {
        synchronized (this.mUidRulesFirstLock) {
            if (this.mRestrictBackground && tethering) {
                Log.d(TAG, "Tethering on (" + iface + "); disable Data Saver");
                setRestrictBackground(false);
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    public void setRestrictBackground(boolean restrictBackground) {
        Trace.traceBegin(2097152, "setRestrictBackground");
        try {
            this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_NETWORK_POLICY", TAG);
            long token = Binder.clearCallingIdentity();
            try {
                synchronized (this.mUidRulesFirstLock) {
                    setRestrictBackgroundUL(restrictBackground);
                }
                Binder.restoreCallingIdentity(token);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(token);
                throw th;
            }
        } finally {
            Trace.traceEnd(2097152);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    @GuardedBy({"mUidRulesFirstLock"})
    private void setRestrictBackgroundUL(boolean restrictBackground) {
        Trace.traceBegin(2097152, "setRestrictBackgroundUL");
        try {
            if (restrictBackground == this.mRestrictBackground) {
                Slog.w(TAG, "setRestrictBackgroundUL: already " + restrictBackground);
                Trace.traceEnd(2097152);
                return;
            }
            Slog.d(TAG, "setRestrictBackgroundUL(): " + restrictBackground);
            boolean oldRestrictBackground = this.mRestrictBackground;
            this.mRestrictBackground = restrictBackground;
            updateRulesForRestrictBackgroundUL();
            try {
                if (!this.mNetworkManager.setDataSaverModeEnabled(this.mRestrictBackground)) {
                    Slog.e(TAG, "Could not change Data Saver Mode on NMS to " + this.mRestrictBackground);
                    this.mRestrictBackground = oldRestrictBackground;
                    Trace.traceEnd(2097152);
                    return;
                }
            } catch (RemoteException e) {
            }
            sendRestrictBackgroundChangedMsg();
            this.mLogger.restrictBackgroundChanged(oldRestrictBackground, this.mRestrictBackground);
            if (this.mRestrictBackgroundPowerState.globalBatterySaverEnabled) {
                this.mRestrictBackgroundChangedInBsm = true;
            }
            synchronized (this.mNetworkPoliciesSecondLock) {
                updateNotificationsNL();
                writePolicyAL();
            }
            Trace.traceEnd(2097152);
        } catch (Throwable th) {
            Trace.traceEnd(2097152);
            throw th;
        }
    }

    private void sendRestrictBackgroundChangedMsg() {
        this.mHandler.removeMessages(6);
        this.mHandler.obtainMessage(6, this.mRestrictBackground ? 1 : 0, 0).sendToTarget();
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    /* JADX INFO: finally extract failed */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0036, code lost:
        return r5;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int getRestrictBackgroundByCaller() {
        /*
            r8 = this;
            android.content.Context r0 = r8.mContext
            java.lang.String r1 = "android.permission.ACCESS_NETWORK_STATE"
            java.lang.String r2 = "NetworkPolicy"
            r0.enforceCallingOrSelfPermission(r1, r2)
            int r0 = android.os.Binder.getCallingUid()
            java.lang.Object r1 = r8.mUidRulesFirstLock
            monitor-enter(r1)
            long r2 = android.os.Binder.clearCallingIdentity()     // Catch:{ all -> 0x003c }
            int r4 = r8.getUidPolicy(r0)     // Catch:{ all -> 0x0037 }
            android.os.Binder.restoreCallingIdentity(r2)     // Catch:{ all -> 0x003c }
            r5 = 3
            r6 = 1
            if (r4 != r6) goto L_0x0022
            monitor-exit(r1)     // Catch:{ all -> 0x003c }
            return r5
        L_0x0022:
            boolean r7 = r8.mRestrictBackground     // Catch:{ all -> 0x003c }
            if (r7 != 0) goto L_0x0028
            monitor-exit(r1)     // Catch:{ all -> 0x003c }
            return r6
        L_0x0028:
            android.util.SparseIntArray r6 = r8.mUidPolicy     // Catch:{ all -> 0x003c }
            int r6 = r6.get(r0)     // Catch:{ all -> 0x003c }
            r6 = r6 & 4
            if (r6 == 0) goto L_0x0034
            r5 = 2
            goto L_0x0035
        L_0x0034:
        L_0x0035:
            monitor-exit(r1)     // Catch:{ all -> 0x003c }
            return r5
        L_0x0037:
            r4 = move-exception
            android.os.Binder.restoreCallingIdentity(r2)     // Catch:{ all -> 0x003c }
            throw r4     // Catch:{ all -> 0x003c }
        L_0x003c:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x003c }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.net.NetworkPolicyManagerService.getRestrictBackgroundByCaller():int");
    }

    public boolean getRestrictBackground() {
        boolean z;
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_NETWORK_POLICY", TAG);
        synchronized (this.mUidRulesFirstLock) {
            z = this.mRestrictBackground;
        }
        return z;
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x002d, code lost:
        if (r5 == false) goto L_0x0036;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:?, code lost:
        com.android.server.EventLogTags.writeDeviceIdleOnPhase("net");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0036, code lost:
        com.android.server.EventLogTags.writeDeviceIdleOffPhase("net");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x003c, code lost:
        android.os.Trace.traceEnd(2097152);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0040, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setDeviceIdleMode(boolean r5) {
        /*
            r4 = this;
            android.content.Context r0 = r4.mContext
            java.lang.String r1 = "android.permission.MANAGE_NETWORK_POLICY"
            java.lang.String r2 = "NetworkPolicy"
            r0.enforceCallingOrSelfPermission(r1, r2)
            r0 = 2097152(0x200000, double:1.0361308E-317)
            java.lang.String r2 = "setDeviceIdleMode"
            android.os.Trace.traceBegin(r0, r2)
            java.lang.Object r2 = r4.mUidRulesFirstLock     // Catch:{ all -> 0x0044 }
            monitor-enter(r2)     // Catch:{ all -> 0x0044 }
            boolean r3 = r4.mDeviceIdleMode     // Catch:{ all -> 0x0041 }
            if (r3 != r5) goto L_0x001e
            monitor-exit(r2)     // Catch:{ all -> 0x0041 }
            android.os.Trace.traceEnd(r0)
            return
        L_0x001e:
            r4.mDeviceIdleMode = r5     // Catch:{ all -> 0x0041 }
            com.android.server.net.NetworkPolicyLogger r3 = r4.mLogger     // Catch:{ all -> 0x0041 }
            r3.deviceIdleModeEnabled(r5)     // Catch:{ all -> 0x0041 }
            boolean r3 = r4.mSystemReady     // Catch:{ all -> 0x0041 }
            if (r3 == 0) goto L_0x002c
            r4.updateRulesForRestrictPowerUL()     // Catch:{ all -> 0x0041 }
        L_0x002c:
            monitor-exit(r2)     // Catch:{ all -> 0x0041 }
            if (r5 == 0) goto L_0x0036
            java.lang.String r2 = "net"
            com.android.server.EventLogTags.writeDeviceIdleOnPhase(r2)     // Catch:{ all -> 0x0044 }
            goto L_0x003c
        L_0x0036:
            java.lang.String r2 = "net"
            com.android.server.EventLogTags.writeDeviceIdleOffPhase(r2)     // Catch:{ all -> 0x0044 }
        L_0x003c:
            android.os.Trace.traceEnd(r0)
            return
        L_0x0041:
            r3 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0041 }
            throw r3     // Catch:{ all -> 0x0044 }
        L_0x0044:
            r2 = move-exception
            android.os.Trace.traceEnd(r0)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.net.NetworkPolicyManagerService.setDeviceIdleMode(boolean):void");
    }

    public void setWifiMeteredOverride(String networkId, int meteredOverride) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_NETWORK_POLICY", TAG);
        long token = Binder.clearCallingIdentity();
        try {
            WifiManager wm = (WifiManager) this.mContext.getSystemService(WifiManager.class);
            for (WifiConfiguration config : wm.getConfiguredNetworks()) {
                if (Objects.equals(NetworkPolicyManager.resolveNetworkId(config), networkId)) {
                    config.meteredOverride = meteredOverride;
                    wm.updateNetwork(config);
                }
            }
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    @Deprecated
    public NetworkQuotaInfo getNetworkQuotaInfo(NetworkState state) {
        Log.w(TAG, "Shame on UID " + Binder.getCallingUid() + " for calling the hidden API getNetworkQuotaInfo(). Shame!");
        return new NetworkQuotaInfo();
    }

    private void enforceSubscriptionPlanAccess(int subId, int callingUid, String callingPackage) {
        this.mAppOps.checkPackage(callingUid, callingPackage);
        long token = Binder.clearCallingIdentity();
        try {
            SubscriptionInfo si = ((SubscriptionManager) this.mContext.getSystemService(SubscriptionManager.class)).getActiveSubscriptionInfo(subId);
            PersistableBundle config = this.mCarrierConfigManager.getConfigForSubId(subId);
            if (si == null || !si.isEmbedded() || !si.canManageSubscription(this.mContext, callingPackage)) {
                if (config != null) {
                    String overridePackage = config.getString("config_plans_package_override_string", (String) null);
                    if (!TextUtils.isEmpty(overridePackage) && Objects.equals(overridePackage, callingPackage)) {
                        return;
                    }
                }
                String defaultPackage = this.mCarrierConfigManager.getDefaultCarrierServicePackageName();
                if (TextUtils.isEmpty(defaultPackage) || !Objects.equals(defaultPackage, callingPackage)) {
                    String testPackage = SystemProperties.get("persist.sys.sub_plan_owner." + subId, (String) null);
                    if (TextUtils.isEmpty(testPackage) || !Objects.equals(testPackage, callingPackage)) {
                        String legacyTestPackage = SystemProperties.get("fw.sub_plan_owner." + subId, (String) null);
                        if (TextUtils.isEmpty(legacyTestPackage) || !Objects.equals(legacyTestPackage, callingPackage)) {
                            this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_SUBSCRIPTION_PLANS", TAG);
                        }
                    }
                }
            }
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    public SubscriptionPlan[] getSubscriptionPlans(int subId, String callingPackage) {
        int i = subId;
        String str = callingPackage;
        enforceSubscriptionPlanAccess(i, Binder.getCallingUid(), str);
        String fake = SystemProperties.get("fw.fake_plan");
        if (!TextUtils.isEmpty(fake)) {
            List<SubscriptionPlan> plans = new ArrayList<>();
            if ("month_hard".equals(fake)) {
                plans.add(SubscriptionPlan.Builder.createRecurringMonthly(ZonedDateTime.parse("2007-03-14T00:00:00.000Z")).setTitle("G-Mobile").setDataLimit(5368709120L, 1).setDataUsage(1073741824, ZonedDateTime.now().minusHours(36).toInstant().toEpochMilli()).build());
                plans.add(SubscriptionPlan.Builder.createRecurringMonthly(ZonedDateTime.parse("2017-03-14T00:00:00.000Z")).setTitle("G-Mobile Happy").setDataLimit(JobStatus.NO_LATEST_RUNTIME, 1).setDataUsage(5368709120L, ZonedDateTime.now().minusHours(36).toInstant().toEpochMilli()).build());
                plans.add(SubscriptionPlan.Builder.createRecurringMonthly(ZonedDateTime.parse("2017-03-14T00:00:00.000Z")).setTitle("G-Mobile, Charged after limit").setDataLimit(5368709120L, 1).setDataUsage(5368709120L, ZonedDateTime.now().minusHours(36).toInstant().toEpochMilli()).build());
            } else if ("month_soft".equals(fake)) {
                plans.add(SubscriptionPlan.Builder.createRecurringMonthly(ZonedDateTime.parse("2007-03-14T00:00:00.000Z")).setTitle("G-Mobile is the carriers name who this plan belongs to").setSummary("Crazy unlimited bandwidth plan with incredibly long title that should be cut off to prevent UI from looking terrible").setDataLimit(5368709120L, 2).setDataUsage(1073741824, ZonedDateTime.now().minusHours(1).toInstant().toEpochMilli()).build());
                plans.add(SubscriptionPlan.Builder.createRecurringMonthly(ZonedDateTime.parse("2017-03-14T00:00:00.000Z")).setTitle("G-Mobile, Throttled after limit").setDataLimit(5368709120L, 2).setDataUsage(5368709120L, ZonedDateTime.now().minusHours(1).toInstant().toEpochMilli()).build());
                plans.add(SubscriptionPlan.Builder.createRecurringMonthly(ZonedDateTime.parse("2017-03-14T00:00:00.000Z")).setTitle("G-Mobile, No data connection after limit").setDataLimit(5368709120L, 0).setDataUsage(5368709120L, ZonedDateTime.now().minusHours(1).toInstant().toEpochMilli()).build());
            } else if ("month_over".equals(fake)) {
                plans.add(SubscriptionPlan.Builder.createRecurringMonthly(ZonedDateTime.parse("2007-03-14T00:00:00.000Z")).setTitle("G-Mobile is the carriers name who this plan belongs to").setDataLimit(5368709120L, 2).setDataUsage(6442450944L, ZonedDateTime.now().minusHours(1).toInstant().toEpochMilli()).build());
                plans.add(SubscriptionPlan.Builder.createRecurringMonthly(ZonedDateTime.parse("2017-03-14T00:00:00.000Z")).setTitle("G-Mobile, Throttled after limit").setDataLimit(5368709120L, 2).setDataUsage(5368709120L, ZonedDateTime.now().minusHours(1).toInstant().toEpochMilli()).build());
                plans.add(SubscriptionPlan.Builder.createRecurringMonthly(ZonedDateTime.parse("2017-03-14T00:00:00.000Z")).setTitle("G-Mobile, No data connection after limit").setDataLimit(5368709120L, 0).setDataUsage(5368709120L, ZonedDateTime.now().minusHours(1).toInstant().toEpochMilli()).build());
            } else if ("month_none".equals(fake)) {
                plans.add(SubscriptionPlan.Builder.createRecurringMonthly(ZonedDateTime.parse("2007-03-14T00:00:00.000Z")).setTitle("G-Mobile").build());
            } else if ("prepaid".equals(fake)) {
                plans.add(SubscriptionPlan.Builder.createNonrecurring(ZonedDateTime.now().minusDays(20), ZonedDateTime.now().plusDays(10)).setTitle("G-Mobile").setDataLimit(536870912, 0).setDataUsage(104857600, ZonedDateTime.now().minusHours(3).toInstant().toEpochMilli()).build());
            } else if ("prepaid_crazy".equals(fake)) {
                plans.add(SubscriptionPlan.Builder.createNonrecurring(ZonedDateTime.now().minusDays(20), ZonedDateTime.now().plusDays(10)).setTitle("G-Mobile Anytime").setDataLimit(536870912, 0).setDataUsage(104857600, ZonedDateTime.now().minusHours(3).toInstant().toEpochMilli()).build());
                plans.add(SubscriptionPlan.Builder.createNonrecurring(ZonedDateTime.now().minusDays(10), ZonedDateTime.now().plusDays(20)).setTitle("G-Mobile Nickel Nights").setSummary("5¢/GB between 1-5AM").setDataLimit(5368709120L, 2).setDataUsage(15728640, ZonedDateTime.now().minusHours(30).toInstant().toEpochMilli()).build());
                plans.add(SubscriptionPlan.Builder.createNonrecurring(ZonedDateTime.now().minusDays(10), ZonedDateTime.now().plusDays(20)).setTitle("G-Mobile Bonus 3G").setSummary("Unlimited 3G data").setDataLimit(1073741824, 2).setDataUsage(314572800, ZonedDateTime.now().minusHours(1).toInstant().toEpochMilli()).build());
            } else if ("unlimited".equals(fake)) {
                plans.add(SubscriptionPlan.Builder.createNonrecurring(ZonedDateTime.now().minusDays(20), ZonedDateTime.now().plusDays(10)).setTitle("G-Mobile Awesome").setDataLimit(JobStatus.NO_LATEST_RUNTIME, 2).setDataUsage(52428800, ZonedDateTime.now().minusHours(3).toInstant().toEpochMilli()).build());
            }
            return (SubscriptionPlan[]) plans.toArray(new SubscriptionPlan[plans.size()]);
        }
        synchronized (this.mNetworkPoliciesSecondLock) {
            String ownerPackage = this.mSubscriptionPlansOwner.get(i);
            if (!Objects.equals(ownerPackage, str)) {
                if (UserHandle.getCallingAppId() != 1000) {
                    Log.w(TAG, "Not returning plans because caller " + str + " doesn't match owner " + ownerPackage);
                    return null;
                }
            }
            SubscriptionPlan[] subscriptionPlanArr = this.mSubscriptionPlans.get(i);
            return subscriptionPlanArr;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    public void setSubscriptionPlans(int subId, SubscriptionPlan[] plans, String callingPackage) {
        enforceSubscriptionPlanAccess(subId, Binder.getCallingUid(), callingPackage);
        for (SubscriptionPlan plan : plans) {
            Preconditions.checkNotNull(plan);
        }
        long token = Binder.clearCallingIdentity();
        try {
            synchronized (this.mUidRulesFirstLock) {
                synchronized (this.mNetworkPoliciesSecondLock) {
                    this.mSubscriptionPlans.put(subId, plans);
                    this.mSubscriptionPlansOwner.put(subId, callingPackage);
                    String subscriberId = this.mSubIdToSubscriberId.get(subId, (Object) null);
                    if (subscriberId != null) {
                        ensureActiveMobilePolicyAL(subId, subscriberId);
                        maybeUpdateMobilePolicyCycleAL(subId, subscriberId);
                    } else {
                        Slog.wtf(TAG, "Missing subscriberId for subId " + subId);
                    }
                    handleNetworkPoliciesUpdateAL(true);
                }
            }
            Intent intent = new Intent("android.telephony.action.SUBSCRIPTION_PLANS_CHANGED");
            intent.addFlags(1073741824);
            intent.putExtra("android.telephony.extra.SUBSCRIPTION_INDEX", subId);
            this.mContext.sendBroadcast(intent, "android.permission.MANAGE_SUBSCRIPTION_PLANS");
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    /* access modifiers changed from: package-private */
    public void setSubscriptionPlansOwner(int subId, String packageName) {
        SystemProperties.set("persist.sys.sub_plan_owner." + subId, packageName);
    }

    public String getSubscriptionPlansOwner(int subId) {
        String str;
        if (UserHandle.getCallingAppId() == 1000) {
            synchronized (this.mNetworkPoliciesSecondLock) {
                str = this.mSubscriptionPlansOwner.get(subId);
            }
            return str;
        }
        throw new SecurityException();
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    public void setSubscriptionOverride(int subId, int overrideMask, int overrideValue, long timeoutMillis, String callingPackage) {
        enforceSubscriptionPlanAccess(subId, Binder.getCallingUid(), callingPackage);
        synchronized (this.mNetworkPoliciesSecondLock) {
            SubscriptionPlan plan = getPrimarySubscriptionPlanLocked(subId);
            if (plan == null || plan.getDataLimitBehavior() == -1) {
                throw new IllegalStateException("Must provide valid SubscriptionPlan to enable overriding");
            }
        }
        boolean overrideEnabled = true;
        if (Settings.Global.getInt(this.mContext.getContentResolver(), "netpolicy_override_enabled", 1) == 0) {
            overrideEnabled = false;
        }
        if (overrideEnabled || overrideValue == 0) {
            Handler handler = this.mHandler;
            handler.sendMessage(handler.obtainMessage(16, overrideMask, overrideValue, Integer.valueOf(subId)));
            if (timeoutMillis > 0) {
                Handler handler2 = this.mHandler;
                handler2.sendMessageDelayed(handler2.obtainMessage(16, overrideMask, 0, Integer.valueOf(subId)), timeoutMillis);
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 12 */
    /* access modifiers changed from: protected */
    public void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        if (DumpUtils.checkDumpPermission(this.mContext, TAG, writer)) {
            IndentingPrintWriter fout = new IndentingPrintWriter(writer, "  ");
            ArraySet<String> argSet = new ArraySet<>(args.length);
            for (String arg : args) {
                argSet.add(arg);
            }
            synchronized (this.mUidRulesFirstLock) {
                synchronized (this.mNetworkPoliciesSecondLock) {
                    if (argSet.contains("--unsnooze")) {
                        for (int i = this.mNetworkPolicy.size() - 1; i >= 0; i--) {
                            this.mNetworkPolicy.valueAt(i).clearSnooze();
                        }
                        handleNetworkPoliciesUpdateAL(true);
                        fout.println("Cleared snooze timestamps");
                        return;
                    }
                    fout.print("System ready: ");
                    fout.println(this.mSystemReady);
                    fout.print("Restrict background: ");
                    fout.println(this.mRestrictBackground);
                    fout.print("Restrict power: ");
                    fout.println(this.mRestrictPower);
                    fout.print("Device idle: ");
                    fout.println(this.mDeviceIdleMode);
                    fout.print("Metered ifaces: ");
                    fout.println(String.valueOf(this.mMeteredIfaces));
                    fout.println();
                    fout.println("Network policies:");
                    fout.increaseIndent();
                    for (int i2 = 0; i2 < this.mNetworkPolicy.size(); i2++) {
                        fout.println(this.mNetworkPolicy.valueAt(i2).toString());
                    }
                    fout.decreaseIndent();
                    fout.println();
                    fout.println("Subscription plans:");
                    fout.increaseIndent();
                    for (int i3 = 0; i3 < this.mSubscriptionPlans.size(); i3++) {
                        fout.println("Subscriber ID " + this.mSubscriptionPlans.keyAt(i3) + ":");
                        fout.increaseIndent();
                        SubscriptionPlan[] plans = this.mSubscriptionPlans.valueAt(i3);
                        if (!ArrayUtils.isEmpty(plans)) {
                            for (SubscriptionPlan plan : plans) {
                                fout.println(plan);
                            }
                        }
                        fout.decreaseIndent();
                    }
                    fout.decreaseIndent();
                    fout.println();
                    fout.println("Active subscriptions:");
                    fout.increaseIndent();
                    for (int i4 = 0; i4 < this.mSubIdToSubscriberId.size(); i4++) {
                        fout.println(this.mSubIdToSubscriberId.keyAt(i4) + "=" + NetworkIdentity.scrubSubscriberId(this.mSubIdToSubscriberId.valueAt(i4)));
                    }
                    fout.decreaseIndent();
                    fout.println();
                    fout.println("Merged subscriptions: " + Arrays.toString(NetworkIdentity.scrubSubscriberId(this.mMergedSubscriberIds)));
                    fout.println();
                    fout.println("Policy for UIDs:");
                    fout.increaseIndent();
                    int size = this.mUidPolicy.size();
                    for (int i5 = 0; i5 < size; i5++) {
                        int uid = this.mUidPolicy.keyAt(i5);
                        int policy = this.mUidPolicy.valueAt(i5);
                        fout.print("UID=");
                        fout.print(uid);
                        fout.print(" policy=");
                        fout.print(NetworkPolicyManager.uidPoliciesToString(policy));
                        fout.println();
                    }
                    fout.decreaseIndent();
                    int size2 = this.mPowerSaveWhitelistExceptIdleAppIds.size();
                    if (size2 > 0) {
                        fout.println("Power save whitelist (except idle) app ids:");
                        fout.increaseIndent();
                        for (int i6 = 0; i6 < size2; i6++) {
                            fout.print("UID=");
                            fout.print(this.mPowerSaveWhitelistExceptIdleAppIds.keyAt(i6));
                            fout.print(": ");
                            fout.print(this.mPowerSaveWhitelistExceptIdleAppIds.valueAt(i6));
                            fout.println();
                        }
                        fout.decreaseIndent();
                    }
                    int size3 = this.mPowerSaveWhitelistAppIds.size();
                    if (size3 > 0) {
                        fout.println("Power save whitelist app ids:");
                        fout.increaseIndent();
                        for (int i7 = 0; i7 < size3; i7++) {
                            fout.print("UID=");
                            fout.print(this.mPowerSaveWhitelistAppIds.keyAt(i7));
                            fout.print(": ");
                            fout.print(this.mPowerSaveWhitelistAppIds.valueAt(i7));
                            fout.println();
                        }
                        fout.decreaseIndent();
                    }
                    int size4 = this.mAppIdleTempWhitelistAppIds.size();
                    if (size4 > 0) {
                        fout.println("App idle whitelist app ids:");
                        fout.increaseIndent();
                        for (int i8 = 0; i8 < size4; i8++) {
                            fout.print("UID=");
                            fout.print(this.mAppIdleTempWhitelistAppIds.keyAt(i8));
                            fout.print(": ");
                            fout.print(this.mAppIdleTempWhitelistAppIds.valueAt(i8));
                            fout.println();
                        }
                        fout.decreaseIndent();
                    }
                    int size5 = this.mDefaultRestrictBackgroundWhitelistUids.size();
                    if (size5 > 0) {
                        fout.println("Default restrict background whitelist uids:");
                        fout.increaseIndent();
                        for (int i9 = 0; i9 < size5; i9++) {
                            fout.print("UID=");
                            fout.print(this.mDefaultRestrictBackgroundWhitelistUids.keyAt(i9));
                            fout.println();
                        }
                        fout.decreaseIndent();
                    }
                    int size6 = this.mRestrictBackgroundWhitelistRevokedUids.size();
                    if (size6 > 0) {
                        fout.println("Default restrict background whitelist uids revoked by users:");
                        fout.increaseIndent();
                        for (int i10 = 0; i10 < size6; i10++) {
                            fout.print("UID=");
                            fout.print(this.mRestrictBackgroundWhitelistRevokedUids.keyAt(i10));
                            fout.println();
                        }
                        fout.decreaseIndent();
                    }
                    SparseBooleanArray knownUids = new SparseBooleanArray();
                    collectKeys(this.mUidState, knownUids);
                    collectKeys(this.mUidRules, knownUids);
                    fout.println("Status for all known UIDs:");
                    fout.increaseIndent();
                    int size7 = knownUids.size();
                    for (int i11 = 0; i11 < size7; i11++) {
                        int uid2 = knownUids.keyAt(i11);
                        fout.print("UID=");
                        fout.print(uid2);
                        int state = this.mUidState.get(uid2, 20);
                        fout.print(" state=");
                        fout.print(state);
                        if (state <= 2) {
                            fout.print(" (fg)");
                        } else {
                            fout.print(state <= 6 ? " (fg svc)" : " (bg)");
                        }
                        int uidRules = this.mUidRules.get(uid2, 0);
                        fout.print(" rules=");
                        fout.print(NetworkPolicyManager.uidRulesToString(uidRules));
                        fout.println();
                    }
                    fout.decreaseIndent();
                    fout.println("Status for just UIDs with rules:");
                    fout.increaseIndent();
                    int size8 = this.mUidRules.size();
                    for (int i12 = 0; i12 < size8; i12++) {
                        int uid3 = this.mUidRules.keyAt(i12);
                        fout.print("UID=");
                        fout.print(uid3);
                        int uidRules2 = this.mUidRules.get(uid3, 0);
                        fout.print(" rules=");
                        fout.print(NetworkPolicyManager.uidRulesToString(uidRules2));
                        fout.println();
                    }
                    fout.decreaseIndent();
                    fout.println("Admin restricted uids for metered data:");
                    fout.increaseIndent();
                    int size9 = this.mMeteredRestrictedUids.size();
                    for (int i13 = 0; i13 < size9; i13++) {
                        fout.print("u" + this.mMeteredRestrictedUids.keyAt(i13) + ": ");
                        fout.println(this.mMeteredRestrictedUids.valueAt(i13));
                    }
                    fout.decreaseIndent();
                    fout.println();
                    this.mStatLogger.dump(fout);
                    this.mLogger.dumpLogs(fout);
                }
            }
        }
    }

    /* JADX WARNING: type inference failed for: r1v1, types: [android.os.Binder] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onShellCommand(java.io.FileDescriptor r9, java.io.FileDescriptor r10, java.io.FileDescriptor r11, java.lang.String[] r12, android.os.ShellCallback r13, android.os.ResultReceiver r14) {
        /*
            r8 = this;
            com.android.server.net.NetworkPolicyManagerShellCommand r0 = new com.android.server.net.NetworkPolicyManagerShellCommand
            android.content.Context r1 = r8.mContext
            r0.<init>(r1, r8)
            r1 = r8
            r2 = r9
            r3 = r10
            r4 = r11
            r5 = r12
            r6 = r13
            r7 = r14
            r0.exec(r1, r2, r3, r4, r5, r6, r7)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.net.NetworkPolicyManagerService.onShellCommand(java.io.FileDescriptor, java.io.FileDescriptor, java.io.FileDescriptor, java.lang.String[], android.os.ShellCallback, android.os.ResultReceiver):void");
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean isUidForeground(int uid) {
        boolean isUidStateForeground;
        synchronized (this.mUidRulesFirstLock) {
            isUidStateForeground = isUidStateForeground(this.mUidState.get(uid, 20));
        }
        return isUidStateForeground;
    }

    @GuardedBy({"mUidRulesFirstLock"})
    private boolean isUidForegroundOnRestrictBackgroundUL(int uid) {
        return NetworkPolicyManager.isProcStateAllowedWhileOnRestrictBackground(this.mUidState.get(uid, 20));
    }

    @GuardedBy({"mUidRulesFirstLock"})
    private boolean isUidForegroundOnRestrictPowerUL(int uid) {
        return NetworkPolicyManager.isProcStateAllowedWhileIdleOrPowerSaveMode(this.mUidState.get(uid, 20));
    }

    private boolean isUidStateForeground(int state) {
        return state <= 6;
    }

    @GuardedBy({"mUidRulesFirstLock"})
    private boolean updateUidStateUL(int uid, int uidState) {
        Trace.traceBegin(2097152, "updateUidStateUL");
        try {
            int oldUidState = this.mUidState.get(uid, 20);
            if (oldUidState != uidState) {
                this.mUidState.put(uid, uidState);
                updateRestrictBackgroundRulesOnUidStatusChangedUL(uid, oldUidState, uidState);
                if (NetworkPolicyManager.isProcStateAllowedWhileIdleOrPowerSaveMode(oldUidState) != NetworkPolicyManager.isProcStateAllowedWhileIdleOrPowerSaveMode(uidState)) {
                    updateRuleForAppIdleUL(uid);
                    if (this.mDeviceIdleMode) {
                        updateRuleForDeviceIdleUL(uid);
                    }
                    if (this.mRestrictPower) {
                        updateRuleForRestrictPowerUL(uid);
                    }
                    updateRulesForPowerRestrictionsUL(uid);
                }
                return true;
            }
            Trace.traceEnd(2097152);
            return false;
        } finally {
            Trace.traceEnd(2097152);
        }
    }

    @GuardedBy({"mUidRulesFirstLock"})
    private boolean removeUidStateUL(int uid) {
        int index = this.mUidState.indexOfKey(uid);
        if (index < 0) {
            return false;
        }
        int oldUidState = this.mUidState.valueAt(index);
        this.mUidState.removeAt(index);
        if (oldUidState == 20) {
            return false;
        }
        updateRestrictBackgroundRulesOnUidStatusChangedUL(uid, oldUidState, 20);
        if (this.mDeviceIdleMode) {
            updateRuleForDeviceIdleUL(uid);
        }
        if (this.mRestrictPower) {
            updateRuleForRestrictPowerUL(uid);
        }
        updateRulesForPowerRestrictionsUL(uid);
        return true;
    }

    private void updateNetworkStats(int uid, boolean uidForeground) {
        if (Trace.isTagEnabled(2097152)) {
            StringBuilder sb = new StringBuilder();
            sb.append("updateNetworkStats: ");
            sb.append(uid);
            sb.append(SliceClientPermissions.SliceAuthority.DELIMITER);
            sb.append(uidForeground ? "F" : "B");
            Trace.traceBegin(2097152, sb.toString());
        }
        try {
            this.mNetworkStats.setUidForeground(uid, uidForeground);
        } finally {
            Trace.traceEnd(2097152);
        }
    }

    private void updateRestrictBackgroundRulesOnUidStatusChangedUL(int uid, int oldUidState, int newUidState) {
        if (NetworkPolicyManager.isProcStateAllowedWhileOnRestrictBackground(oldUidState) != NetworkPolicyManager.isProcStateAllowedWhileOnRestrictBackground(newUidState)) {
            updateRulesForDataUsageRestrictionsUL(uid);
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mUidRulesFirstLock"})
    public void updateRulesForPowerSaveUL() {
        Trace.traceBegin(2097152, "updateRulesForPowerSaveUL");
        try {
            updateRulesForWhitelistedPowerSaveUL(this.mRestrictPower, 3, this.mUidFirewallPowerSaveRules);
        } finally {
            Trace.traceEnd(2097152);
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mUidRulesFirstLock"})
    public void updateRuleForRestrictPowerUL(int uid) {
        updateRulesForWhitelistedPowerSaveUL(uid, this.mRestrictPower, 3);
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mUidRulesFirstLock"})
    public void updateRulesForDeviceIdleUL() {
        Trace.traceBegin(2097152, "updateRulesForDeviceIdleUL");
        try {
            updateRulesForWhitelistedPowerSaveUL(this.mDeviceIdleMode, 1, this.mUidFirewallDozableRules);
        } finally {
            Trace.traceEnd(2097152);
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mUidRulesFirstLock"})
    public void updateRuleForDeviceIdleUL(int uid) {
        updateRulesForWhitelistedPowerSaveUL(uid, this.mDeviceIdleMode, 1);
    }

    @GuardedBy({"mUidRulesFirstLock"})
    private void updateRulesForWhitelistedPowerSaveUL(boolean enabled, int chain, SparseIntArray rules) {
        if (enabled) {
            SparseIntArray uidRules = rules;
            uidRules.clear();
            List<UserInfo> users = this.mUserManager.getUsers();
            for (int ui = users.size() - 1; ui >= 0; ui--) {
                UserInfo user = users.get(ui);
                updateRulesForWhitelistedAppIds(uidRules, this.mPowerSaveTempWhitelistAppIds, user.id);
                updateRulesForWhitelistedAppIds(uidRules, this.mPowerSaveWhitelistAppIds, user.id);
                if (chain == 3) {
                    updateRulesForWhitelistedAppIds(uidRules, this.mPowerSaveWhitelistExceptIdleAppIds, user.id);
                }
            }
            for (int i = this.mUidState.size() - 1; i >= 0; i--) {
                if (NetworkPolicyManager.isProcStateAllowedWhileIdleOrPowerSaveMode(this.mUidState.valueAt(i))) {
                    uidRules.put(this.mUidState.keyAt(i), 1);
                }
            }
            setUidFirewallRulesUL(chain, uidRules, 1);
            return;
        }
        setUidFirewallRulesUL(chain, (SparseIntArray) null, 2);
    }

    private void updateRulesForWhitelistedAppIds(SparseIntArray uidRules, SparseBooleanArray whitelistedAppIds, int userId) {
        for (int i = whitelistedAppIds.size() - 1; i >= 0; i--) {
            if (whitelistedAppIds.valueAt(i)) {
                uidRules.put(UserHandle.getUid(userId, whitelistedAppIds.keyAt(i)), 1);
            }
        }
    }

    @GuardedBy({"mUidRulesFirstLock"})
    private boolean isWhitelistedFromPowerSaveUL(int uid, boolean deviceIdleMode) {
        int appId = UserHandle.getAppId(uid);
        boolean isWhitelisted = false;
        boolean isWhitelisted2 = this.mPowerSaveTempWhitelistAppIds.get(appId) || this.mPowerSaveWhitelistAppIds.get(appId);
        if (deviceIdleMode) {
            return isWhitelisted2;
        }
        if (isWhitelisted2 || this.mPowerSaveWhitelistExceptIdleAppIds.get(appId)) {
            isWhitelisted = true;
        }
        return isWhitelisted;
    }

    @GuardedBy({"mUidRulesFirstLock"})
    private void updateRulesForWhitelistedPowerSaveUL(int uid, boolean enabled, int chain) {
        if (enabled) {
            if (isWhitelistedFromPowerSaveUL(uid, chain == 1) || isUidForegroundOnRestrictPowerUL(uid)) {
                setUidFirewallRule(chain, uid, 1);
            } else {
                setUidFirewallRule(chain, uid, 0);
            }
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mUidRulesFirstLock"})
    public void updateRulesForAppIdleUL() {
        Trace.traceBegin(2097152, "updateRulesForAppIdleUL");
        try {
            SparseIntArray uidRules = this.mUidFirewallStandbyRules;
            uidRules.clear();
            List<UserInfo> users = this.mUserManager.getUsers();
            for (int ui = users.size() - 1; ui >= 0; ui--) {
                for (int uid : this.mUsageStats.getIdleUidsForUser(users.get(ui).id)) {
                    if (!this.mPowerSaveTempWhitelistAppIds.get(UserHandle.getAppId(uid), false) && hasInternetPermissions(uid)) {
                        uidRules.put(uid, 2);
                    }
                }
            }
            setUidFirewallRulesUL(2, uidRules, 0);
        } finally {
            Trace.traceEnd(2097152);
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mUidRulesFirstLock"})
    public void updateRuleForAppIdleUL(int uid) {
        if (isUidValidForBlacklistRules(uid)) {
            if (Trace.isTagEnabled(2097152)) {
                Trace.traceBegin(2097152, "updateRuleForAppIdleUL: " + uid);
            }
            try {
                if (!this.mPowerSaveTempWhitelistAppIds.get(UserHandle.getAppId(uid))) {
                    if (isUidIdle(uid) && !isUidForegroundOnRestrictPowerUL(uid)) {
                        setUidFirewallRule(2, uid, 2);
                        if (LOGD) {
                            Log.d(TAG, "updateRuleForAppIdleUL DENY " + uid);
                        }
                    }
                }
                setUidFirewallRule(2, uid, 0);
                if (LOGD) {
                    Log.d(TAG, "updateRuleForAppIdleUL " + uid + " to DEFAULT");
                }
            } finally {
                Trace.traceEnd(2097152);
            }
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mUidRulesFirstLock"})
    public void updateRulesForAppIdleParoleUL() {
        boolean paroled = this.mUsageStats.isAppIdleParoleOn();
        boolean enableChain = !paroled;
        enableFirewallChainUL(2, enableChain);
        int ruleCount = this.mUidFirewallStandbyRules.size();
        for (int i = 0; i < ruleCount; i++) {
            int uid = this.mUidFirewallStandbyRules.keyAt(i);
            int oldRules = this.mUidRules.get(uid);
            if (enableChain) {
                oldRules &= 15;
            } else if ((oldRules & 240) == 0) {
            }
            int newUidRules = updateRulesForPowerRestrictionsUL(uid, oldRules, paroled);
            if (newUidRules == 0) {
                this.mUidRules.delete(uid);
            } else {
                this.mUidRules.put(uid, newUidRules);
            }
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mUidRulesFirstLock", "mNetworkPoliciesSecondLock"})
    public void updateRulesForGlobalChangeAL(boolean restrictedNetworksChanged) {
        if (Trace.isTagEnabled(2097152)) {
            StringBuilder sb = new StringBuilder();
            sb.append("updateRulesForGlobalChangeAL: ");
            sb.append(restrictedNetworksChanged ? "R" : "-");
            Trace.traceBegin(2097152, sb.toString());
        }
        try {
            updateRulesForAppIdleUL();
            updateRulesForRestrictPowerUL();
            updateRulesForRestrictBackgroundUL();
            if (restrictedNetworksChanged) {
                normalizePoliciesNL();
                updateNetworkRulesNL();
            }
        } finally {
            Trace.traceEnd(2097152);
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mUidRulesFirstLock"})
    public void updateRulesForRestrictPowerUL() {
        Trace.traceBegin(2097152, "updateRulesForRestrictPowerUL");
        try {
            updateRulesForDeviceIdleUL();
            updateRulesForPowerSaveUL();
            updateRulesForAllAppsUL(2);
        } finally {
            Trace.traceEnd(2097152);
        }
    }

    @GuardedBy({"mUidRulesFirstLock"})
    private void updateRulesForRestrictBackgroundUL() {
        Trace.traceBegin(2097152, "updateRulesForRestrictBackgroundUL");
        try {
            updateRulesForAllAppsUL(1);
        } finally {
            Trace.traceEnd(2097152);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 16 */
    @GuardedBy({"mUidRulesFirstLock"})
    private void updateRulesForAllAppsUL(int type) {
        int i = type;
        if (Trace.isTagEnabled(2097152)) {
            Trace.traceBegin(2097152, "updateRulesForRestrictPowerUL-" + i);
        }
        try {
            PackageManager pm = this.mContext.getPackageManager();
            Trace.traceBegin(2097152, "list-users");
            List users = this.mUserManager.getUsers();
            Trace.traceEnd(2097152);
            Trace.traceBegin(2097152, "list-uids");
            List<ApplicationInfo> apps = pm.getInstalledApplications(4981248);
            Trace.traceEnd(2097152);
            int usersSize = users.size();
            int appsSize = apps.size();
            for (int i2 = 0; i2 < usersSize; i2++) {
                UserInfo user = (UserInfo) users.get(i2);
                for (int j = 0; j < appsSize; j++) {
                    int uid = UserHandle.getUid(user.id, apps.get(j).uid);
                    if (i == 1) {
                        updateRulesForDataUsageRestrictionsUL(uid);
                    } else if (i != 2) {
                        Slog.w(TAG, "Invalid type for updateRulesForAllApps: " + i);
                    } else {
                        updateRulesForPowerRestrictionsUL(uid);
                    }
                }
            }
            Trace.traceEnd(2097152);
        } catch (Throwable th) {
            Trace.traceEnd(2097152);
            throw th;
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mUidRulesFirstLock"})
    public void updateRulesForTempWhitelistChangeUL(int appId) {
        List<UserInfo> users = this.mUserManager.getUsers();
        int numUsers = users.size();
        for (int i = 0; i < numUsers; i++) {
            int uid = UserHandle.getUid(users.get(i).id, appId);
            updateRuleForAppIdleUL(uid);
            updateRuleForDeviceIdleUL(uid);
            updateRuleForRestrictPowerUL(uid);
            updateRulesForPowerRestrictionsUL(uid);
        }
    }

    private boolean isUidValidForBlacklistRules(int uid) {
        if (uid == 1013 || uid == 1019) {
            return true;
        }
        if (!UserHandle.isApp(uid) || !hasInternetPermissions(uid)) {
            return false;
        }
        return true;
    }

    private boolean isUidValidForWhitelistRules(int uid) {
        return UserHandle.isApp(uid) && hasInternetPermissions(uid);
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setAppIdleWhitelist(int uid, boolean shouldWhitelist) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_NETWORK_POLICY", TAG);
        synchronized (this.mUidRulesFirstLock) {
            if (this.mAppIdleTempWhitelistAppIds.get(uid) != shouldWhitelist) {
                long token = Binder.clearCallingIdentity();
                try {
                    this.mLogger.appIdleWlChanged(uid, shouldWhitelist);
                    if (shouldWhitelist) {
                        this.mAppIdleTempWhitelistAppIds.put(uid, true);
                    } else {
                        this.mAppIdleTempWhitelistAppIds.delete(uid);
                    }
                    updateRuleForAppIdleUL(uid);
                    updateRulesForPowerRestrictionsUL(uid);
                } finally {
                    Binder.restoreCallingIdentity(token);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public int[] getAppIdleWhitelist() {
        int[] uids;
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_NETWORK_POLICY", TAG);
        synchronized (this.mUidRulesFirstLock) {
            int len = this.mAppIdleTempWhitelistAppIds.size();
            uids = new int[len];
            for (int i = 0; i < len; i++) {
                uids[i] = this.mAppIdleTempWhitelistAppIds.keyAt(i);
            }
        }
        return uids;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x001f, code lost:
        r3 = r0.length;
        r4 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0021, code lost:
        if (r4 >= r3) goto L_0x0031;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x002b, code lost:
        if (r7.mUsageStats.isAppIdle(r0[r4], r8, r1) != false) goto L_0x002e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x002d, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x002e, code lost:
        r4 = r4 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:?, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:?, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x000f, code lost:
        r0 = r7.mContext.getPackageManager().getPackagesForUid(r8);
        r1 = android.os.UserHandle.getUserId(r8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x001d, code lost:
        if (r0 == null) goto L_?;
     */
    @com.android.internal.annotations.VisibleForTesting
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isUidIdle(int r8) {
        /*
            r7 = this;
            java.lang.Object r0 = r7.mUidRulesFirstLock
            monitor-enter(r0)
            android.util.SparseBooleanArray r1 = r7.mAppIdleTempWhitelistAppIds     // Catch:{ all -> 0x0033 }
            boolean r1 = r1.get(r8)     // Catch:{ all -> 0x0033 }
            r2 = 0
            if (r1 == 0) goto L_0x000e
            monitor-exit(r0)     // Catch:{ all -> 0x0033 }
            return r2
        L_0x000e:
            monitor-exit(r0)     // Catch:{ all -> 0x0033 }
            android.content.Context r0 = r7.mContext
            android.content.pm.PackageManager r0 = r0.getPackageManager()
            java.lang.String[] r0 = r0.getPackagesForUid(r8)
            int r1 = android.os.UserHandle.getUserId(r8)
            if (r0 == 0) goto L_0x0031
            int r3 = r0.length
            r4 = r2
        L_0x0021:
            if (r4 >= r3) goto L_0x0031
            r5 = r0[r4]
            android.app.usage.UsageStatsManagerInternal r6 = r7.mUsageStats
            boolean r6 = r6.isAppIdle(r5, r8, r1)
            if (r6 != 0) goto L_0x002e
            return r2
        L_0x002e:
            int r4 = r4 + 1
            goto L_0x0021
        L_0x0031:
            r2 = 1
            return r2
        L_0x0033:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0033 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.net.NetworkPolicyManagerService.isUidIdle(int):boolean");
    }

    private boolean hasInternetPermissions(int uid) {
        try {
            if (this.mIPm.checkUidPermission("android.permission.INTERNET", uid) != 0) {
                return false;
            }
            return true;
        } catch (RemoteException e) {
            return true;
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mUidRulesFirstLock"})
    public void onUidDeletedUL(int uid) {
        this.mUidRules.delete(uid);
        this.mUidPolicy.delete(uid);
        this.mUidFirewallStandbyRules.delete(uid);
        this.mUidFirewallDozableRules.delete(uid);
        this.mUidFirewallPowerSaveRules.delete(uid);
        this.mPowerSaveWhitelistExceptIdleAppIds.delete(uid);
        this.mPowerSaveWhitelistAppIds.delete(uid);
        this.mPowerSaveTempWhitelistAppIds.delete(uid);
        this.mAppIdleTempWhitelistAppIds.delete(uid);
        this.mHandler.obtainMessage(15, uid, 0).sendToTarget();
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mUidRulesFirstLock"})
    public void updateRestrictionRulesForUidUL(int uid) {
        updateRuleForDeviceIdleUL(uid);
        updateRuleForAppIdleUL(uid);
        updateRuleForRestrictPowerUL(uid);
        updateRulesForPowerRestrictionsUL(uid);
        updateRulesForDataUsageRestrictionsUL(uid);
    }

    private void updateRulesForDataUsageRestrictionsUL(int uid) {
        if (Trace.isTagEnabled(2097152)) {
            Trace.traceBegin(2097152, "updateRulesForDataUsageRestrictionsUL: " + uid);
        }
        try {
            updateRulesForDataUsageRestrictionsULInner(uid);
        } finally {
            Trace.traceEnd(2097152);
        }
    }

    private void updateRulesForDataUsageRestrictionsULInner(int uid) {
        int i = uid;
        if (isUidValidForWhitelistRules(uid)) {
            int uidPolicy = this.mUidPolicy.get(i, 0);
            int oldUidRules = this.mUidRules.get(i, 0);
            boolean isForeground = isUidForegroundOnRestrictBackgroundUL(uid);
            boolean isRestrictedByAdmin = isRestrictedByAdminUL(uid);
            boolean isBlacklisted = (uidPolicy & 1) != 0;
            boolean isWhitelisted = (uidPolicy & 4) != 0;
            int oldRule = oldUidRules & 15;
            int newRule = 0;
            if (isRestrictedByAdmin) {
                newRule = 4;
            } else if (isForeground) {
                if (isBlacklisted || (this.mRestrictBackground && !isWhitelisted)) {
                    newRule = 2;
                } else if (isWhitelisted) {
                    newRule = 1;
                }
            } else if (isBlacklisted) {
                newRule = 4;
            } else if (this.mRestrictBackground && isWhitelisted) {
                newRule = 1;
            }
            int newUidRules = (oldUidRules & 240) | newRule;
            if (LOGV) {
                StringBuilder sb = new StringBuilder();
                int i2 = uidPolicy;
                sb.append("updateRuleForRestrictBackgroundUL(");
                sb.append(i);
                sb.append("): isForeground=");
                sb.append(isForeground);
                sb.append(", isBlacklisted=");
                sb.append(isBlacklisted);
                sb.append(", isWhitelisted=");
                sb.append(isWhitelisted);
                sb.append(", isRestrictedByAdmin=");
                sb.append(isRestrictedByAdmin);
                sb.append(", oldRule=");
                sb.append(NetworkPolicyManager.uidRulesToString(oldRule));
                sb.append(", newRule=");
                sb.append(NetworkPolicyManager.uidRulesToString(newRule));
                sb.append(", newUidRules=");
                sb.append(NetworkPolicyManager.uidRulesToString(newUidRules));
                sb.append(", oldUidRules=");
                sb.append(NetworkPolicyManager.uidRulesToString(oldUidRules));
                Log.v(TAG, sb.toString());
            }
            if (newUidRules == 0) {
                this.mUidRules.delete(i);
            } else {
                this.mUidRules.put(i, newUidRules);
            }
            if (newRule != oldRule) {
                if (hasRule(newRule, 2)) {
                    setMeteredNetworkWhitelist(i, true);
                    if (isBlacklisted) {
                        setMeteredNetworkBlacklist(i, false);
                    }
                } else {
                    boolean z = false;
                    if (hasRule(oldRule, 2)) {
                        if (!isWhitelisted) {
                            setMeteredNetworkWhitelist(i, false);
                        }
                        if (isBlacklisted || isRestrictedByAdmin) {
                            setMeteredNetworkBlacklist(i, true);
                        }
                    } else if (hasRule(newRule, 4) || hasRule(oldRule, 4)) {
                        if (isBlacklisted || isRestrictedByAdmin) {
                            z = true;
                        }
                        setMeteredNetworkBlacklist(i, z);
                        if (hasRule(oldRule, 4) && isWhitelisted) {
                            setMeteredNetworkWhitelist(i, isWhitelisted);
                        }
                    } else if (hasRule(newRule, 1) || hasRule(oldRule, 1)) {
                        setMeteredNetworkWhitelist(i, isWhitelisted);
                    } else {
                        Log.wtf(TAG, "Unexpected change of metered UID state for " + i + ": foreground=" + isForeground + ", whitelisted=" + isWhitelisted + ", blacklisted=" + isBlacklisted + ", isRestrictedByAdmin=" + isRestrictedByAdmin + ", newRule=" + NetworkPolicyManager.uidRulesToString(newUidRules) + ", oldRule=" + NetworkPolicyManager.uidRulesToString(oldUidRules));
                    }
                }
                this.mHandler.obtainMessage(1, i, newUidRules).sendToTarget();
            }
        } else if (LOGD) {
            Slog.d(TAG, "no need to update restrict data rules for uid " + i);
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mUidRulesFirstLock"})
    public void updateRulesForPowerRestrictionsUL(int uid) {
        int newUidRules = updateRulesForPowerRestrictionsUL(uid, this.mUidRules.get(uid, 0), false);
        if (newUidRules == 0) {
            this.mUidRules.delete(uid);
        } else {
            this.mUidRules.put(uid, newUidRules);
        }
    }

    private int updateRulesForPowerRestrictionsUL(int uid, int oldUidRules, boolean paroled) {
        if (Trace.isTagEnabled(2097152)) {
            StringBuilder sb = new StringBuilder();
            sb.append("updateRulesForPowerRestrictionsUL: ");
            sb.append(uid);
            sb.append(SliceClientPermissions.SliceAuthority.DELIMITER);
            sb.append(oldUidRules);
            sb.append(SliceClientPermissions.SliceAuthority.DELIMITER);
            sb.append(paroled ? "P" : "-");
            Trace.traceBegin(2097152, sb.toString());
        }
        try {
            return updateRulesForPowerRestrictionsULInner(uid, oldUidRules, paroled);
        } finally {
            Trace.traceEnd(2097152);
        }
    }

    private int updateRulesForPowerRestrictionsULInner(int uid, int oldUidRules, boolean paroled) {
        int i = uid;
        int i2 = oldUidRules;
        boolean restrictMode = false;
        if (!isUidValidForBlacklistRules(uid)) {
            if (LOGD) {
                Slog.d(TAG, "no need to update restrict power rules for uid " + i);
            }
            return 0;
        }
        boolean isIdle = !paroled && isUidIdle(uid);
        if (isIdle || this.mRestrictPower || this.mDeviceIdleMode) {
            restrictMode = true;
        }
        boolean isForeground = isUidForegroundOnRestrictPowerUL(uid);
        boolean isWhitelisted = isWhitelistedFromPowerSaveUL(i, this.mDeviceIdleMode);
        int oldRule = i2 & 240;
        int newRule = 0;
        if (isForeground) {
            if (restrictMode) {
                newRule = 32;
            }
        } else if (restrictMode) {
            newRule = isWhitelisted ? 32 : 64;
        }
        int newUidRules = (i2 & 15) | newRule;
        if (LOGV) {
            Log.v(TAG, "updateRulesForPowerRestrictionsUL(" + i + "), isIdle: " + isIdle + ", mRestrictPower: " + this.mRestrictPower + ", mDeviceIdleMode: " + this.mDeviceIdleMode + ", isForeground=" + isForeground + ", isWhitelisted=" + isWhitelisted + ", oldRule=" + NetworkPolicyManager.uidRulesToString(oldRule) + ", newRule=" + NetworkPolicyManager.uidRulesToString(newRule) + ", newUidRules=" + NetworkPolicyManager.uidRulesToString(newUidRules) + ", oldUidRules=" + NetworkPolicyManager.uidRulesToString(oldUidRules));
        }
        if (newRule != oldRule) {
            if (newRule == 0 || hasRule(newRule, 32)) {
                if (LOGV) {
                    Log.v(TAG, "Allowing non-metered access for UID " + i);
                }
            } else if (!hasRule(newRule, 64)) {
                Log.wtf(TAG, "Unexpected change of non-metered UID state for " + i + ": foreground=" + isForeground + ", whitelisted=" + isWhitelisted + ", newRule=" + NetworkPolicyManager.uidRulesToString(newUidRules) + ", oldRule=" + NetworkPolicyManager.uidRulesToString(oldUidRules));
            } else if (LOGV) {
                Log.v(TAG, "Rejecting non-metered access for UID " + i);
            }
            this.mHandler.obtainMessage(1, i, newUidRules).sendToTarget();
        }
        return newUidRules;
    }

    private class AppIdleStateChangeListener extends UsageStatsManagerInternal.AppIdleStateChangeListener {
        private AppIdleStateChangeListener() {
        }

        /* Debug info: failed to restart local var, previous not found, register: 3 */
        public void onAppIdleStateChanged(String packageName, int userId, boolean idle, int bucket, int reason) {
            try {
                int uid = NetworkPolicyManagerService.this.mContext.getPackageManager().getPackageUidAsUser(packageName, 8192, userId);
                synchronized (NetworkPolicyManagerService.this.mUidRulesFirstLock) {
                    NetworkPolicyManagerService.this.mLogger.appIdleStateChanged(uid, idle);
                    NetworkPolicyManagerService.this.updateRuleForAppIdleUL(uid);
                    NetworkPolicyManagerService.this.updateRulesForPowerRestrictionsUL(uid);
                }
            } catch (PackageManager.NameNotFoundException e) {
            }
        }

        public void onParoleStateChanged(boolean isParoleOn) {
            synchronized (NetworkPolicyManagerService.this.mUidRulesFirstLock) {
                NetworkPolicyManagerService.this.mLogger.paroleStateChanged(isParoleOn);
                NetworkPolicyManagerService.this.updateRulesForAppIdleParoleUL();
            }
        }
    }

    /* access modifiers changed from: private */
    public void dispatchUidRulesChanged(INetworkPolicyListener listener, int uid, int uidRules) {
        if (listener != null) {
            try {
                listener.onUidRulesChanged(uid, uidRules);
            } catch (RemoteException e) {
            }
        }
    }

    /* access modifiers changed from: private */
    public void dispatchMeteredIfacesChanged(INetworkPolicyListener listener, String[] meteredIfaces) {
        if (listener != null) {
            try {
                listener.onMeteredIfacesChanged(meteredIfaces);
            } catch (RemoteException e) {
            }
        }
    }

    /* access modifiers changed from: private */
    public void dispatchRestrictBackgroundChanged(INetworkPolicyListener listener, boolean restrictBackground) {
        if (listener != null) {
            try {
                listener.onRestrictBackgroundChanged(restrictBackground);
            } catch (RemoteException e) {
            }
        }
    }

    /* access modifiers changed from: private */
    public void dispatchUidPoliciesChanged(INetworkPolicyListener listener, int uid, int uidPolicies) {
        if (listener != null) {
            try {
                listener.onUidPoliciesChanged(uid, uidPolicies);
            } catch (RemoteException e) {
            }
        }
    }

    /* access modifiers changed from: private */
    public void dispatchSubscriptionOverride(INetworkPolicyListener listener, int subId, int overrideMask, int overrideValue) {
        if (listener != null) {
            try {
                listener.onSubscriptionOverride(subId, overrideMask, overrideValue);
            } catch (RemoteException e) {
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    /* access modifiers changed from: package-private */
    public void handleUidChanged(int uid, int procState, long procStateSeq) {
        boolean updated;
        Trace.traceBegin(2097152, "onUidStateChanged");
        try {
            synchronized (this.mUidRulesFirstLock) {
                this.mLogger.uidStateChanged(uid, procState, procStateSeq);
                updated = updateUidStateUL(uid, procState);
                this.mActivityManagerInternal.notifyNetworkPolicyRulesUpdated(uid, procStateSeq);
            }
            if (updated) {
                updateNetworkStats(uid, isUidStateForeground(procState));
            }
            Trace.traceEnd(2097152);
        } catch (Throwable th) {
            Trace.traceEnd(2097152);
            throw th;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* access modifiers changed from: package-private */
    public void handleUidGone(int uid) {
        boolean updated;
        Trace.traceBegin(2097152, "onUidGone");
        try {
            synchronized (this.mUidRulesFirstLock) {
                updated = removeUidStateUL(uid);
            }
            if (updated) {
                updateNetworkStats(uid, false);
            }
            Trace.traceEnd(2097152);
        } catch (Throwable th) {
            Trace.traceEnd(2097152);
            throw th;
        }
    }

    /* access modifiers changed from: private */
    public void broadcastRestrictBackgroundChanged(int uid, Boolean changed) {
        String[] packages = this.mContext.getPackageManager().getPackagesForUid(uid);
        if (packages != null) {
            int userId = UserHandle.getUserId(uid);
            for (String packageName : packages) {
                Intent intent = new Intent("android.net.conn.RESTRICT_BACKGROUND_CHANGED");
                intent.setPackage(packageName);
                intent.setFlags(1073741824);
                this.mContext.sendBroadcastAsUser(intent, UserHandle.of(userId));
            }
        }
    }

    private void setInterfaceQuotaAsync(String iface, long quotaBytes) {
        this.mHandler.obtainMessage(10, (int) (quotaBytes >> 32), (int) (-1 & quotaBytes), iface).sendToTarget();
    }

    /* access modifiers changed from: private */
    public void setInterfaceQuota(String iface, long quotaBytes) {
        try {
            this.mNetworkManager.setInterfaceQuota(iface, quotaBytes);
        } catch (IllegalStateException e) {
            Log.wtf(TAG, "problem setting interface quota", e);
        } catch (RemoteException e2) {
        }
    }

    private void removeInterfaceQuotaAsync(String iface) {
        this.mHandler.obtainMessage(11, iface).sendToTarget();
    }

    /* access modifiers changed from: private */
    public void removeInterfaceQuota(String iface) {
        try {
            this.mNetworkManager.removeInterfaceQuota(iface);
        } catch (IllegalStateException e) {
            Log.wtf(TAG, "problem removing interface quota", e);
        } catch (RemoteException e2) {
        }
    }

    private void setMeteredNetworkBlacklist(int uid, boolean enable) {
        if (LOGV) {
            Slog.v(TAG, "setMeteredNetworkBlacklist " + uid + ": " + enable);
        }
        try {
            this.mNetworkManager.setUidMeteredNetworkBlacklist(uid, enable);
        } catch (IllegalStateException e) {
            Log.wtf(TAG, "problem setting blacklist (" + enable + ") rules for " + uid, e);
        } catch (RemoteException e2) {
        }
    }

    private void setMeteredNetworkWhitelist(int uid, boolean enable) {
        if (LOGV) {
            Slog.v(TAG, "setMeteredNetworkWhitelist " + uid + ": " + enable);
        }
        try {
            this.mNetworkManager.setUidMeteredNetworkWhitelist(uid, enable);
        } catch (IllegalStateException e) {
            Log.wtf(TAG, "problem setting whitelist (" + enable + ") rules for " + uid, e);
        } catch (RemoteException e2) {
        }
    }

    @GuardedBy({"mUidRulesFirstLock"})
    private void setUidFirewallRulesUL(int chain, SparseIntArray uidRules, int toggle) {
        if (uidRules != null) {
            setUidFirewallRulesUL(chain, uidRules);
        }
        if (toggle != 0) {
            boolean z = true;
            if (toggle != 1) {
                z = false;
            }
            enableFirewallChainUL(chain, z);
        }
    }

    private void setUidFirewallRulesUL(int chain, SparseIntArray uidRules) {
        try {
            int size = uidRules.size();
            int[] uids = new int[size];
            int[] rules = new int[size];
            for (int index = size - 1; index >= 0; index--) {
                uids[index] = uidRules.keyAt(index);
                rules[index] = uidRules.valueAt(index);
            }
            this.mNetworkManager.setFirewallUidRules(chain, uids, rules);
            this.mLogger.firewallRulesChanged(chain, uids, rules);
        } catch (IllegalStateException e) {
            Log.wtf(TAG, "problem setting firewall uid rules", e);
        } catch (RemoteException e2) {
        }
    }

    private void setUidFirewallRule(int chain, int uid, int rule) {
        if (Trace.isTagEnabled(2097152)) {
            Trace.traceBegin(2097152, "setUidFirewallRule: " + chain + SliceClientPermissions.SliceAuthority.DELIMITER + uid + SliceClientPermissions.SliceAuthority.DELIMITER + rule);
        }
        if (chain == 1) {
            try {
                this.mUidFirewallDozableRules.put(uid, rule);
            } catch (IllegalStateException e) {
                Log.wtf(TAG, "problem setting firewall uid rules", e);
            } catch (RemoteException e2) {
            } catch (Throwable th) {
                Trace.traceEnd(2097152);
                throw th;
            }
        } else if (chain == 2) {
            this.mUidFirewallStandbyRules.put(uid, rule);
        } else if (chain == 3) {
            this.mUidFirewallPowerSaveRules.put(uid, rule);
        }
        this.mNetworkManager.setFirewallUidRule(chain, uid, rule);
        this.mLogger.uidFirewallRuleChanged(chain, uid, rule);
        Trace.traceEnd(2097152);
    }

    @GuardedBy({"mUidRulesFirstLock"})
    private void enableFirewallChainUL(int chain, boolean enable) {
        if (this.mFirewallChainStates.indexOfKey(chain) < 0 || this.mFirewallChainStates.get(chain) != enable) {
            this.mFirewallChainStates.put(chain, enable);
            try {
                this.mNetworkManager.setFirewallChainEnabled(chain, enable);
                this.mLogger.firewallChainEnabled(chain, enable);
            } catch (IllegalStateException e) {
                Log.wtf(TAG, "problem enable firewall chain", e);
            } catch (RemoteException e2) {
            }
        }
    }

    /* access modifiers changed from: private */
    public void resetUidFirewallRules(int uid) {
        try {
            this.mNetworkManager.setFirewallUidRule(1, uid, 0);
            this.mNetworkManager.setFirewallUidRule(2, uid, 0);
            this.mNetworkManager.setFirewallUidRule(3, uid, 0);
            this.mNetworkManager.setUidMeteredNetworkWhitelist(uid, false);
            this.mNetworkManager.setUidMeteredNetworkBlacklist(uid, false);
        } catch (IllegalStateException e) {
            Log.wtf(TAG, "problem resetting firewall uid rules for " + uid, e);
        } catch (RemoteException e2) {
        }
    }

    @Deprecated
    private long getTotalBytes(NetworkTemplate template, long start, long end) {
        return getNetworkTotalBytes(template, start, end);
    }

    private long getNetworkTotalBytes(NetworkTemplate template, long start, long end) {
        try {
            return this.mNetworkStats.getNetworkTotalBytes(template, start, end);
        } catch (RuntimeException e) {
            Slog.w(TAG, "Failed to read network stats: " + e);
            return 0;
        }
    }

    private NetworkStats getNetworkUidBytes(NetworkTemplate template, long start, long end) {
        try {
            return this.mNetworkStats.getNetworkUidBytes(template, start, end);
        } catch (RuntimeException e) {
            Slog.w(TAG, "Failed to read network stats: " + e);
            return new NetworkStats(SystemClock.elapsedRealtime(), 0);
        }
    }

    private boolean isBandwidthControlEnabled() {
        long token = Binder.clearCallingIdentity();
        try {
            return this.mNetworkManager.isBandwidthControlEnabled();
        } catch (RemoteException e) {
            return false;
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    private static Intent buildAllowBackgroundDataIntent() {
        return new Intent(ACTION_ALLOW_BACKGROUND);
    }

    private static Intent buildSnoozeWarningIntent(NetworkTemplate template) {
        Intent intent = new Intent(ACTION_SNOOZE_WARNING);
        intent.addFlags(268435456);
        intent.putExtra("android.net.NETWORK_TEMPLATE", template);
        return intent;
    }

    private static Intent buildSnoozeRapidIntent(NetworkTemplate template) {
        Intent intent = new Intent(ACTION_SNOOZE_RAPID);
        intent.addFlags(268435456);
        intent.putExtra("android.net.NETWORK_TEMPLATE", template);
        return intent;
    }

    private static Intent buildNetworkOverLimitIntent(Resources res, NetworkTemplate template) {
        Intent intent = new Intent();
        intent.setComponent(ComponentName.unflattenFromString(res.getString(17039785)));
        intent.addFlags(268435456);
        intent.putExtra("android.net.NETWORK_TEMPLATE", template);
        return intent;
    }

    private static Intent buildViewDataUsageIntent(Resources res, NetworkTemplate template) {
        Intent intent = new Intent();
        intent.setComponent(ComponentName.unflattenFromString(res.getString(17039717)));
        intent.addFlags(268435456);
        intent.putExtra("android.net.NETWORK_TEMPLATE", template);
        return intent;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void addIdleHandler(MessageQueue.IdleHandler handler) {
        this.mHandler.getLooper().getQueue().addIdleHandler(handler);
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mUidRulesFirstLock"})
    @VisibleForTesting
    public void updateRestrictBackgroundByLowPowerModeUL(PowerSaveState result) {
        boolean shouldInvokeRestrictBackground;
        this.mRestrictBackgroundPowerState = result;
        boolean restrictBackground = result.batterySaverEnabled;
        boolean localRestrictBgChangedInBsm = this.mRestrictBackgroundChangedInBsm;
        boolean z = true;
        if (result.globalBatterySaverEnabled) {
            if (this.mRestrictBackground || !result.batterySaverEnabled) {
                z = false;
            }
            shouldInvokeRestrictBackground = z;
            this.mRestrictBackgroundBeforeBsm = this.mRestrictBackground;
            localRestrictBgChangedInBsm = false;
        } else {
            shouldInvokeRestrictBackground = !this.mRestrictBackgroundChangedInBsm;
            restrictBackground = this.mRestrictBackgroundBeforeBsm;
        }
        if (shouldInvokeRestrictBackground) {
            setRestrictBackgroundUL(restrictBackground);
        }
        this.mRestrictBackgroundChangedInBsm = localRestrictBgChangedInBsm;
    }

    private static void collectKeys(SparseIntArray source, SparseBooleanArray target) {
        int size = source.size();
        for (int i = 0; i < size; i++) {
            target.put(source.keyAt(i), true);
        }
    }

    public void factoryReset(String subscriber) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        if (!this.mUserManager.hasUserRestriction("no_network_reset")) {
            NetworkPolicy[] policies = getNetworkPolicies(this.mContext.getOpPackageName());
            NetworkTemplate template = NetworkTemplate.buildTemplateMobileAll(subscriber);
            for (NetworkPolicy policy : policies) {
                if (policy.template.equals(template)) {
                    policy.limitBytes = -1;
                    policy.inferred = false;
                    policy.clearSnooze();
                }
            }
            setNetworkPolicies(policies);
            setRestrictBackground(false);
            if (!this.mUserManager.hasUserRestriction("no_control_apps")) {
                for (int uid : getUidsWithPolicy(1)) {
                    setUidPolicy(uid, 0);
                }
            }
        }
    }

    public boolean isUidNetworkingBlocked(int uid, boolean isNetworkMetered) {
        int uidRules;
        boolean isBackgroundRestricted;
        long startTime = this.mStatLogger.getTime();
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_NETWORK_POLICY", TAG);
        synchronized (this.mUidRulesFirstLock) {
            uidRules = this.mUidRules.get(uid, 0);
            isBackgroundRestricted = this.mRestrictBackground;
        }
        boolean ret = isUidNetworkingBlockedInternal(uid, uidRules, isNetworkMetered, isBackgroundRestricted, this.mLogger);
        this.mStatLogger.logDurationStat(1, startTime);
        return ret;
    }

    private static boolean isSystem(int uid) {
        return uid < 10000;
    }

    static boolean isUidNetworkingBlockedInternal(int uid, int uidRules, boolean isNetworkMetered, boolean isBackgroundRestricted, NetworkPolicyLogger logger) {
        int reason;
        boolean blocked;
        if (isSystem(uid)) {
            reason = 7;
        } else if (hasRule(uidRules, 64)) {
            reason = 0;
        } else if (!isNetworkMetered) {
            reason = 1;
        } else if (hasRule(uidRules, 4)) {
            reason = 2;
        } else if (hasRule(uidRules, 1)) {
            reason = 3;
        } else if (hasRule(uidRules, 2)) {
            reason = 4;
        } else if (isBackgroundRestricted) {
            reason = 5;
        } else {
            reason = 6;
        }
        switch (reason) {
            case 0:
            case 2:
            case 5:
                blocked = true;
                break;
            case 1:
            case 3:
            case 4:
            case 6:
            case 7:
                blocked = false;
                break;
            default:
                throw new IllegalArgumentException();
        }
        if (logger != null) {
            logger.networkBlocked(uid, reason);
        }
        return blocked;
    }

    private class NetworkPolicyManagerInternalImpl extends NetworkPolicyManagerInternal {
        private NetworkPolicyManagerInternalImpl() {
        }

        /* Debug info: failed to restart local var, previous not found, register: 4 */
        public void resetUserState(int userId) {
            synchronized (NetworkPolicyManagerService.this.mUidRulesFirstLock) {
                boolean changed = false;
                boolean changed2 = NetworkPolicyManagerService.this.removeUserStateUL(userId, false);
                if (NetworkPolicyManagerService.this.addDefaultRestrictBackgroundWhitelistUidsUL(userId) || changed2) {
                    changed = true;
                }
                if (changed) {
                    synchronized (NetworkPolicyManagerService.this.mNetworkPoliciesSecondLock) {
                        NetworkPolicyManagerService.this.writePolicyAL();
                    }
                }
            }
        }

        public boolean isUidRestrictedOnMeteredNetworks(int uid) {
            int uidRules;
            boolean isBackgroundRestricted;
            synchronized (NetworkPolicyManagerService.this.mUidRulesFirstLock) {
                uidRules = NetworkPolicyManagerService.this.mUidRules.get(uid, 32);
                isBackgroundRestricted = NetworkPolicyManagerService.this.mRestrictBackground;
            }
            if (!isBackgroundRestricted || NetworkPolicyManagerService.hasRule(uidRules, 1) || NetworkPolicyManagerService.hasRule(uidRules, 2)) {
                return false;
            }
            return true;
        }

        public boolean isUidNetworkingBlocked(int uid, String ifname) {
            int uidRules;
            boolean isBackgroundRestricted;
            boolean isNetworkMetered;
            long startTime = NetworkPolicyManagerService.this.mStatLogger.getTime();
            synchronized (NetworkPolicyManagerService.this.mUidRulesFirstLock) {
                uidRules = NetworkPolicyManagerService.this.mUidRules.get(uid, 0);
                isBackgroundRestricted = NetworkPolicyManagerService.this.mRestrictBackground;
            }
            synchronized (NetworkPolicyManagerService.this.mNetworkPoliciesSecondLock) {
                isNetworkMetered = NetworkPolicyManagerService.this.mMeteredIfaces.contains(ifname);
            }
            boolean ret = NetworkPolicyManagerService.isUidNetworkingBlockedInternal(uid, uidRules, isNetworkMetered, isBackgroundRestricted, NetworkPolicyManagerService.this.mLogger);
            NetworkPolicyManagerService.this.mStatLogger.logDurationStat(1, startTime);
            return ret;
        }

        public void onTempPowerSaveWhitelistChange(int appId, boolean added) {
            synchronized (NetworkPolicyManagerService.this.mUidRulesFirstLock) {
                NetworkPolicyManagerService.this.mLogger.tempPowerSaveWlChanged(appId, added);
                if (added) {
                    NetworkPolicyManagerService.this.mPowerSaveTempWhitelistAppIds.put(appId, true);
                } else {
                    NetworkPolicyManagerService.this.mPowerSaveTempWhitelistAppIds.delete(appId);
                }
                NetworkPolicyManagerService.this.updateRulesForTempWhitelistChangeUL(appId);
            }
        }

        public SubscriptionPlan getSubscriptionPlan(Network network) {
            SubscriptionPlan access$3900;
            synchronized (NetworkPolicyManagerService.this.mNetworkPoliciesSecondLock) {
                access$3900 = NetworkPolicyManagerService.this.getPrimarySubscriptionPlanLocked(NetworkPolicyManagerService.this.getSubIdLocked(network));
            }
            return access$3900;
        }

        public SubscriptionPlan getSubscriptionPlan(NetworkTemplate template) {
            SubscriptionPlan access$3900;
            synchronized (NetworkPolicyManagerService.this.mNetworkPoliciesSecondLock) {
                access$3900 = NetworkPolicyManagerService.this.getPrimarySubscriptionPlanLocked(NetworkPolicyManagerService.this.findRelevantSubIdNL(template));
            }
            return access$3900;
        }

        public long getSubscriptionOpportunisticQuota(Network network, int quotaType) {
            long quotaBytes;
            synchronized (NetworkPolicyManagerService.this.mNetworkPoliciesSecondLock) {
                quotaBytes = NetworkPolicyManagerService.this.mSubscriptionOpportunisticQuota.get(NetworkPolicyManagerService.this.getSubIdLocked(network), -1);
            }
            if (quotaBytes == -1) {
                return -1;
            }
            if (quotaType == 1) {
                return (long) (((float) quotaBytes) * Settings.Global.getFloat(NetworkPolicyManagerService.this.mContext.getContentResolver(), "netpolicy_quota_frac_jobs", 0.5f));
            }
            if (quotaType == 2) {
                return (long) (((float) quotaBytes) * Settings.Global.getFloat(NetworkPolicyManagerService.this.mContext.getContentResolver(), "netpolicy_quota_frac_multipath", 0.5f));
            }
            return -1;
        }

        public void onAdminDataAvailable() {
            NetworkPolicyManagerService.this.mAdminDataAvailableLatch.countDown();
        }

        public void setAppIdleWhitelist(int uid, boolean shouldWhitelist) {
            NetworkPolicyManagerService.this.setAppIdleWhitelist(uid, shouldWhitelist);
        }

        public void setMeteredRestrictedPackages(Set<String> packageNames, int userId) {
            NetworkPolicyManagerService.this.setMeteredRestrictedPackagesInternal(packageNames, userId);
        }

        public void setMeteredRestrictedPackagesAsync(Set<String> packageNames, int userId) {
            NetworkPolicyManagerService.this.mHandler.obtainMessage(17, userId, 0, packageNames).sendToTarget();
        }
    }

    /* access modifiers changed from: private */
    public void setMeteredRestrictedPackagesInternal(Set<String> packageNames, int userId) {
        synchronized (this.mUidRulesFirstLock) {
            Set<Integer> newRestrictedUids = new ArraySet<>();
            for (String packageName : packageNames) {
                int uid = getUidForPackage(packageName, userId);
                if (uid >= 0) {
                    newRestrictedUids.add(Integer.valueOf(uid));
                }
            }
            this.mMeteredRestrictedUids.put(userId, newRestrictedUids);
            handleRestrictedPackagesChangeUL(this.mMeteredRestrictedUids.get(userId), newRestrictedUids);
            this.mLogger.meteredRestrictedPkgsChanged(newRestrictedUids);
        }
    }

    private int getUidForPackage(String packageName, int userId) {
        try {
            return this.mContext.getPackageManager().getPackageUidAsUser(packageName, 4202496, userId);
        } catch (PackageManager.NameNotFoundException e) {
            return -1;
        }
    }

    private int parseSubId(NetworkState state) {
        if (state == null || state.networkCapabilities == null || !state.networkCapabilities.hasTransport(0)) {
            return -1;
        }
        StringNetworkSpecifier networkSpecifier = state.networkCapabilities.getNetworkSpecifier();
        if (!(networkSpecifier instanceof StringNetworkSpecifier)) {
            return -1;
        }
        try {
            return Integer.parseInt(networkSpecifier.specifier);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mNetworkPoliciesSecondLock"})
    public int getSubIdLocked(Network network) {
        return this.mNetIdToSubId.get(network.netId, -1);
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mNetworkPoliciesSecondLock"})
    public SubscriptionPlan getPrimarySubscriptionPlanLocked(int subId) {
        SubscriptionPlan[] plans = this.mSubscriptionPlans.get(subId);
        if (ArrayUtils.isEmpty(plans)) {
            return null;
        }
        int length = plans.length;
        for (int i = 0; i < length; i++) {
            SubscriptionPlan plan = plans[i];
            if (plan.getCycleRule().isRecurring() || plan.cycleIterator().next().contains(ZonedDateTime.now(this.mClock))) {
                return plan;
            }
        }
        return null;
    }

    private void waitForAdminData() {
        if (this.mContext.getPackageManager().hasSystemFeature("android.software.device_admin")) {
            ConcurrentUtils.waitForCountDownNoInterrupt(this.mAdminDataAvailableLatch, 10000, "Wait for admin data");
        }
    }

    private void handleRestrictedPackagesChangeUL(Set<Integer> oldRestrictedUids, Set<Integer> newRestrictedUids) {
        if (this.mNetworkManagerReady) {
            if (oldRestrictedUids == null) {
                for (Integer intValue : newRestrictedUids) {
                    updateRulesForDataUsageRestrictionsUL(intValue.intValue());
                }
                return;
            }
            for (Integer intValue2 : oldRestrictedUids) {
                int uid = intValue2.intValue();
                if (!newRestrictedUids.contains(Integer.valueOf(uid))) {
                    updateRulesForDataUsageRestrictionsUL(uid);
                }
            }
            for (Integer intValue3 : newRestrictedUids) {
                int uid2 = intValue3.intValue();
                if (!oldRestrictedUids.contains(Integer.valueOf(uid2))) {
                    updateRulesForDataUsageRestrictionsUL(uid2);
                }
            }
        }
    }

    @GuardedBy({"mUidRulesFirstLock"})
    private boolean isRestrictedByAdminUL(int uid) {
        Set<Integer> restrictedUids = this.mMeteredRestrictedUids.get(UserHandle.getUserId(uid));
        return restrictedUids != null && restrictedUids.contains(Integer.valueOf(uid));
    }

    /* access modifiers changed from: private */
    public static boolean hasRule(int uidRules, int rule) {
        return (uidRules & rule) != 0;
    }

    private static NetworkState[] defeatNullable(NetworkState[] val) {
        return val != null ? val : new NetworkState[0];
    }

    private static boolean getBooleanDefeatingNullable(PersistableBundle bundle, String key, boolean defaultValue) {
        return bundle != null ? bundle.getBoolean(key, defaultValue) : defaultValue;
    }

    private static boolean isHeadlessSystemUserBuild() {
        return RoSystemProperties.MULTIUSER_HEADLESS_SYSTEM_USER;
    }

    private class NotificationId {
        private final int mId;
        private final String mTag;

        NotificationId(NetworkPolicy policy, int type) {
            this.mTag = buildNotificationTag(policy, type);
            this.mId = type;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof NotificationId)) {
                return false;
            }
            return Objects.equals(this.mTag, ((NotificationId) o).mTag);
        }

        public int hashCode() {
            return Objects.hash(new Object[]{this.mTag});
        }

        private String buildNotificationTag(NetworkPolicy policy, int type) {
            return "NetworkPolicy:" + policy.template.hashCode() + ":" + type;
        }

        public String getTag() {
            return this.mTag;
        }

        public int getId() {
            return this.mId;
        }
    }
}
