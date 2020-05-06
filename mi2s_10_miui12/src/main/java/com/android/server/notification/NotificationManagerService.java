package com.android.server.notification;

import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.AlarmManager;
import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.app.AutomaticZenRule;
import android.app.IActivityManager;
import android.app.INotificationManager;
import android.app.ITransientNotification;
import android.app.IUriGrantsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.app.UriGrantsManager;
import android.app.admin.DevicePolicyManagerInternal;
import android.app.backup.BackupManager;
import android.app.role.OnRoleHoldersChangedListener;
import android.app.role.RoleManager;
import android.app.usage.UsageStatsManagerInternal;
import android.companion.ICompanionDeviceManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.PackageManagerInternal;
import android.content.pm.ParceledListSlice;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.AudioManagerInternal;
import android.media.IRingtonePlayer;
import android.metrics.LogMaker;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.IDeviceIdleController;
import android.os.IInterface;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.DeviceConfig;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.service.notification.Adjustment;
import android.service.notification.Condition;
import android.service.notification.IConditionProvider;
import android.service.notification.INotificationListener;
import android.service.notification.IStatusBarNotificationHolder;
import android.service.notification.NotificationListenerService;
import android.service.notification.NotificationRankingUpdate;
import android.service.notification.NotificationStats;
import android.service.notification.SnoozeCriterion;
import android.service.notification.StatusBarNotification;
import android.service.notification.ZenModeConfig;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.AtomicFile;
import android.util.IntArray;
import android.util.Log;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.util.StatsLog;
import android.util.Xml;
import android.util.proto.ProtoOutputStream;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.notification.SystemNotificationChannels;
import com.android.internal.os.BackgroundThread;
import com.android.internal.os.SomeArgs;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.CollectionUtils;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.util.FunctionalUtils;
import com.android.internal.util.Preconditions;
import com.android.internal.util.XmlUtils;
import com.android.internal.util.function.TriPredicate;
import com.android.server.DeviceIdleController;
import com.android.server.EventLogTags;
import com.android.server.IoThread;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.am.BroadcastQueueInjector;
import com.android.server.inputmethod.MiuiSecurityInputMethodHelper;
import com.android.server.lights.Light;
import com.android.server.lights.LightsManager;
import com.android.server.notification.GroupHelper;
import com.android.server.notification.ManagedServices;
import com.android.server.notification.NotificationManagerService;
import com.android.server.notification.NotificationManagerServiceInjectorBase;
import com.android.server.notification.NotificationRecord;
import com.android.server.notification.SnoozeHelper;
import com.android.server.notification.ZenModeHelper;
import com.android.server.pm.PackageManagerService;
import com.android.server.statusbar.StatusBarManagerInternal;
import com.android.server.uri.UriGrantsManagerInternal;
import com.android.server.wm.ActivityTaskManagerInternal;
import com.android.server.wm.WindowManagerInternal;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import libcore.io.IoUtils;
import miui.util.NotificationFilterHelper;
import miui.util.QuietUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class NotificationManagerService extends SystemService {
    /* access modifiers changed from: private */
    public static final String ACTION_NOTIFICATION_TIMEOUT = (NotificationManagerService.class.getSimpleName() + ".TIMEOUT");
    private static final String ATTR_VERSION = "version";
    static final boolean DBG = Log.isLoggable(TAG, 3);
    private static final int DB_VERSION = 1;
    static final boolean DEBUG_INTERRUPTIVENESS = SystemProperties.getBoolean("debug.notification.interruptiveness", false);
    static final String[] DEFAULT_ALLOWED_ADJUSTMENTS = {"key_contextual_actions", "key_text_replies"};
    static final float DEFAULT_MAX_NOTIFICATION_ENQUEUE_RATE = 5.0f;
    static final int DEFAULT_STREAM_TYPE = 5;
    static final long[] DEFAULT_VIBRATE_PATTERN = {0, 250, 250, 250};
    private static final long DELAY_FOR_ASSISTANT_TIME = 100;
    static final boolean ENABLE_BLOCKED_TOASTS = false;
    public static final boolean ENABLE_CHILD_NOTIFICATIONS = SystemProperties.getBoolean("debug.child_notifs", true);
    private static final int EVENTLOG_ENQUEUE_STATUS_IGNORED = 2;
    private static final int EVENTLOG_ENQUEUE_STATUS_NEW = 0;
    private static final int EVENTLOG_ENQUEUE_STATUS_UPDATE = 1;
    private static final String EXTRA_KEY = "key";
    static final int FINISH_TOKEN_TIMEOUT = 11000;
    private static final String LOCKSCREEN_ALLOW_SECURE_NOTIFICATIONS_TAG = "allow-secure-notifications-on-lockscreen";
    private static final String LOCKSCREEN_ALLOW_SECURE_NOTIFICATIONS_VALUE = "value";
    static final int LONG_DELAY = 3500;
    static final int MATCHES_CALL_FILTER_CONTACTS_TIMEOUT_MS = 3000;
    static final float MATCHES_CALL_FILTER_TIMEOUT_AFFINITY = 1.0f;
    static final int MAX_PACKAGE_NOTIFICATIONS = 25;
    static final int MESSAGE_DURATION_REACHED = 2;
    static final int MESSAGE_FINISH_TOKEN_TIMEOUT = 7;
    static final int MESSAGE_LISTENER_HINTS_CHANGED = 5;
    static final int MESSAGE_LISTENER_NOTIFICATION_FILTER_CHANGED = 6;
    static final int MESSAGE_ON_PACKAGE_CHANGED = 8;
    private static final int MESSAGE_RANKING_SORT = 1001;
    private static final int MESSAGE_RECONSIDER_RANKING = 1000;
    static final int MESSAGE_SEND_RANKING_UPDATE = 4;
    private static final long MIN_PACKAGE_OVERRATE_LOG_INTERVAL = 5000;
    /* access modifiers changed from: private */
    public static final int MY_PID = Process.myPid();
    /* access modifiers changed from: private */
    public static final int MY_UID = Process.myUid();
    static final String[] NON_BLOCKABLE_DEFAULT_ROLES = {"android.app.role.DIALER", "android.app.role.EMERGENCY"};
    private static final int REQUEST_CODE_TIMEOUT = 1;
    private static final String SCHEME_TIMEOUT = "timeout";
    static final int SHORT_DELAY = 2000;
    static final long SNOOZE_UNTIL_UNSPECIFIED = -1;
    static final String TAG = "NotificationService";
    private static final String TAG_NOTIFICATION_POLICY = "notification-policy";
    static final int VIBRATE_PATTERN_MAXLEN = 17;
    private static final IBinder WHITELIST_TOKEN = new Binder();
    /* access modifiers changed from: private */
    public static WorkerHandler mHandler;
    static final Object mNotificationLock = new Object();
    private AccessibilityManager mAccessibilityManager;
    /* access modifiers changed from: private */
    public ActivityManager mActivityManager;
    private AlarmManager mAlarmManager;
    /* access modifiers changed from: private */
    public TriPredicate<String, Integer, String> mAllowedManagedServicePackages;
    /* access modifiers changed from: private */
    public IActivityManager mAm;
    /* access modifiers changed from: private */
    public AppOpsManager mAppOps;
    private UsageStatsManagerInternal mAppUsageStats;
    /* access modifiers changed from: private */
    public Archive mArchive;
    /* access modifiers changed from: private */
    public NotificationAssistants mAssistants;
    Light mAttentionLight;
    AudioManager mAudioManager;
    AudioManagerInternal mAudioManagerInternal;
    private int mAutoGroupAtCount;
    @GuardedBy({"mNotificationLock"})
    final ArrayMap<Integer, ArrayMap<String, String>> mAutobundledSummaries = new ArrayMap<>();
    /* access modifiers changed from: private */
    public Binder mCallNotificationToken = null;
    /* access modifiers changed from: private */
    public int mCallState;
    private ICompanionDeviceManager mCompanionManager;
    /* access modifiers changed from: private */
    public ConditionProviders mConditionProviders;
    private IDeviceIdleController mDeviceIdleController;
    /* access modifiers changed from: private */
    public boolean mDisableNotificationEffects;
    /* access modifiers changed from: private */
    public DevicePolicyManagerInternal mDpm;
    /* access modifiers changed from: private */
    public List<ComponentName> mEffectsSuppressors = new ArrayList();
    @GuardedBy({"mNotificationLock"})
    final ArrayList<NotificationRecord> mEnqueuedNotifications = new ArrayList<>();
    private long[] mFallbackVibrationPattern;
    final IBinder mForegroundToken = new Binder();
    /* access modifiers changed from: private */
    public GroupHelper mGroupHelper;
    boolean mHasLight = true;
    protected boolean mInCall = false;
    /* access modifiers changed from: private */
    public AudioAttributes mInCallNotificationAudioAttributes;
    /* access modifiers changed from: private */
    public Uri mInCallNotificationUri;
    /* access modifiers changed from: private */
    public float mInCallNotificationVolume;
    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Context context2 = context;
            Intent intent2 = intent;
            String action = intent.getAction();
            if (action.equals("android.intent.action.SCREEN_ON")) {
                NotificationManagerService notificationManagerService = NotificationManagerService.this;
                notificationManagerService.mScreenOn = true;
                notificationManagerService.updateNotificationPulse();
            } else if (action.equals("android.intent.action.SCREEN_OFF")) {
                NotificationManagerService notificationManagerService2 = NotificationManagerService.this;
                notificationManagerService2.mScreenOn = false;
                notificationManagerService2.updateNotificationPulse();
            } else if (action.equals("android.intent.action.PHONE_STATE")) {
                NotificationManagerService.this.mInCall = TelephonyManager.EXTRA_STATE_OFFHOOK.equals(intent2.getStringExtra("state"));
                NotificationManagerService.this.updateNotificationPulse();
            } else if (action.equals("android.intent.action.USER_STOPPED")) {
                int userHandle = intent2.getIntExtra("android.intent.extra.user_handle", -1);
                if (userHandle >= 0) {
                    NotificationManagerService.this.cancelAllNotificationsInt(NotificationManagerService.MY_UID, NotificationManagerService.MY_PID, (String) null, (String) null, 0, 0, true, userHandle, 6, (ManagedServices.ManagedServiceInfo) null);
                }
            } else if (action.equals("android.intent.action.MANAGED_PROFILE_UNAVAILABLE")) {
                int userHandle2 = intent2.getIntExtra("android.intent.extra.user_handle", -1);
                if (userHandle2 >= 0) {
                    NotificationManagerService.this.cancelAllNotificationsInt(NotificationManagerService.MY_UID, NotificationManagerService.MY_PID, (String) null, (String) null, 0, 0, true, userHandle2, 15, (ManagedServices.ManagedServiceInfo) null);
                }
            } else if (action.equals("android.intent.action.USER_PRESENT")) {
                NotificationManagerService.this.mNotificationLight.turnOff();
            } else if (action.equals("android.intent.action.USER_SWITCHED")) {
                int userId = intent2.getIntExtra("android.intent.extra.user_handle", ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION);
                NotificationManagerService.this.mUserProfiles.updateCache(context2);
                if (!NotificationManagerService.this.mUserProfiles.isManagedProfile(userId)) {
                    NotificationManagerService.this.mSettingsObserver.update((Uri) null);
                    NotificationManagerService.this.mConditionProviders.onUserSwitched(userId);
                    NotificationManagerService.this.mListeners.onUserSwitched(userId);
                    NotificationManagerService.this.mZenModeHelper.onUserSwitched(userId);
                    NotificationManagerService.this.mPreferencesHelper.onUserSwitched(userId);
                }
                NotificationManagerService.this.mAssistants.onUserSwitched(userId);
            } else if (action.equals("android.intent.action.USER_ADDED")) {
                int userId2 = intent2.getIntExtra("android.intent.extra.user_handle", ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION);
                if (userId2 != -10000) {
                    NotificationManagerService.this.mUserProfiles.updateCache(context2);
                    if (!NotificationManagerService.this.mUserProfiles.isManagedProfile(userId2)) {
                        NotificationManagerService.this.readDefaultApprovedServices(userId2);
                    }
                }
            } else if (action.equals("android.intent.action.USER_REMOVED")) {
                int userId3 = intent2.getIntExtra("android.intent.extra.user_handle", ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION);
                NotificationManagerService.this.mUserProfiles.updateCache(context2);
                NotificationManagerService.this.mZenModeHelper.onUserRemoved(userId3);
                NotificationManagerService.this.mPreferencesHelper.onUserRemoved(userId3);
                NotificationManagerService.this.mListeners.onUserRemoved(userId3);
                NotificationManagerService.this.mConditionProviders.onUserRemoved(userId3);
                NotificationManagerService.this.mAssistants.onUserRemoved(userId3);
                NotificationManagerService.this.handleSavePolicyFile();
            } else if (action.equals("android.intent.action.USER_UNLOCKED")) {
                int userId4 = intent2.getIntExtra("android.intent.extra.user_handle", ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION);
                NotificationManagerService.this.mUserProfiles.updateCache(context2);
                NotificationManagerService.this.mAssistants.onUserUnlocked(userId4);
                if (!NotificationManagerService.this.mUserProfiles.isManagedProfile(userId4)) {
                    NotificationManagerService.this.mConditionProviders.onUserUnlocked(userId4);
                    NotificationManagerService.this.mListeners.onUserUnlocked(userId4);
                    NotificationManagerService.this.mZenModeHelper.onUserUnlocked(userId4);
                    NotificationManagerService.this.mPreferencesHelper.onUserUnlocked(userId4);
                }
            }
        }
    };
    private final NotificationManagerInternal mInternalService = new NotificationManagerInternal() {
        public NotificationChannel getNotificationChannel(String pkg, int uid, String channelId) {
            return NotificationManagerService.this.mPreferencesHelper.getNotificationChannel(pkg, uid, channelId, false);
        }

        public void enqueueNotification(String pkg, String opPkg, int callingUid, int callingPid, String tag, int id, Notification notification, int userId) {
            NotificationManagerService.this.enqueueNotificationInternal(pkg, opPkg, callingUid, callingPid, tag, id, notification, userId);
        }

        public void removeForegroundServiceFlagFromNotification(String pkg, int notificationId, int userId) {
            NotificationManagerService.this.checkCallerIsSystem();
            NotificationManagerService.mHandler.post(new Runnable(pkg, notificationId, userId) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ int f$2;
                private final /* synthetic */ int f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    NotificationManagerService.AnonymousClass11.this.lambda$removeForegroundServiceFlagFromNotification$0$NotificationManagerService$11(this.f$1, this.f$2, this.f$3);
                }
            });
        }

        public /* synthetic */ void lambda$removeForegroundServiceFlagFromNotification$0$NotificationManagerService$11(String pkg, int notificationId, int userId) {
            synchronized (NotificationManagerService.mNotificationLock) {
                List<NotificationRecord> enqueued = NotificationManagerService.this.findNotificationsByListLocked(NotificationManagerService.this.mEnqueuedNotifications, pkg, (String) null, notificationId, userId);
                for (int i = 0; i < enqueued.size(); i++) {
                    removeForegroundServiceFlagLocked(enqueued.get(i));
                }
                NotificationRecord r = NotificationManagerService.this.findNotificationByListLocked(NotificationManagerService.this.mNotificationList, pkg, (String) null, notificationId, userId);
                if (r != null) {
                    removeForegroundServiceFlagLocked(r);
                    NotificationManagerService.this.mRankingHelper.sort(NotificationManagerService.this.mNotificationList);
                    NotificationManagerService.this.mListeners.notifyPostedLocked(r, r);
                }
            }
        }

        @GuardedBy({"mNotificationLock"})
        private void removeForegroundServiceFlagLocked(NotificationRecord r) {
            if (r != null) {
                r.sbn.getNotification().flags = r.mOriginalFlags & -65;
            }
        }

        public void enqueueInterceptNotifications(String pkg, int callingPid, int userId) {
        }
    };
    /* access modifiers changed from: private */
    public int mInterruptionFilter = 0;
    private boolean mIsAutomotive;
    private boolean mIsTelevision;
    private long mLastOverRateLogTime;
    boolean mLightEnabled;
    ArrayList<String> mLights = new ArrayList<>();
    /* access modifiers changed from: private */
    public int mListenerHints;
    /* access modifiers changed from: private */
    public NotificationListeners mListeners;
    private final SparseArray<ArraySet<ComponentName>> mListenersDisablingEffects = new SparseArray<>();
    protected final BroadcastReceiver mLocaleChangeReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.LOCALE_CHANGED".equals(intent.getAction())) {
                SystemNotificationChannels.createAll(context);
                NotificationManagerService.this.mZenModeHelper.updateDefaultZenRules();
                NotificationManagerService.this.mPreferencesHelper.onLocaleChanged(context, ActivityManager.getCurrentUser());
            }
        }
    };
    /* access modifiers changed from: private */
    public boolean mLockScreenAllowSecureNotifications = true;
    /* access modifiers changed from: private */
    public float mMaxPackageEnqueueRate = DEFAULT_MAX_NOTIFICATION_ENQUEUE_RATE;
    /* access modifiers changed from: private */
    public MetricsLogger mMetricsLogger;
    @VisibleForTesting
    final NotificationDelegate mNotificationDelegate = new NotificationDelegate() {
        /* Debug info: failed to restart local var, previous not found, register: 5 */
        public void onSetDisabled(int status) {
            long identity;
            synchronized (NotificationManagerService.mNotificationLock) {
                boolean unused = NotificationManagerService.this.mDisableNotificationEffects = (262144 & status) != 0;
                if (NotificationManagerService.this.disableNotificationEffects((NotificationRecord) null) != null) {
                    long identity2 = Binder.clearCallingIdentity();
                    try {
                        IRingtonePlayer player = NotificationManagerService.this.mAudioManager.getRingtonePlayer();
                        if (player != null) {
                            player.stopAsync();
                        }
                        Binder.restoreCallingIdentity(identity2);
                    } catch (RemoteException e) {
                        Binder.restoreCallingIdentity(identity2);
                    } catch (Throwable th) {
                        Binder.restoreCallingIdentity(identity2);
                        throw th;
                    }
                    identity = Binder.clearCallingIdentity();
                    NotificationManagerService.this.mVibrator.cancel();
                    Binder.restoreCallingIdentity(identity);
                }
            }
        }

        public void onClearAll(int callingUid, int callingPid, int userId) {
            synchronized (NotificationManagerService.mNotificationLock) {
                NotificationManagerService.this.cancelAllLocked(callingUid, callingPid, userId, 3, (ManagedServices.ManagedServiceInfo) null, true);
            }
        }

        public void onNotificationClick(int callingUid, int callingPid, String key, NotificationVisibility nv) {
            String str = key;
            NotificationVisibility notificationVisibility = nv;
            NotificationManagerService.this.exitIdle();
            synchronized (NotificationManagerService.mNotificationLock) {
                NotificationRecord r = NotificationManagerService.this.mNotificationsByKey.get(str);
                if (r == null) {
                    Slog.w(NotificationManagerService.TAG, "No notification with key: " + str);
                    return;
                }
                long now = System.currentTimeMillis();
                MetricsLogger.action(r.getItemLogMaker().setType(4).addTaggedData(798, Integer.valueOf(notificationVisibility.rank)).addTaggedData(1395, Integer.valueOf(notificationVisibility.count)));
                EventLogTags.writeNotificationClicked(key, r.getLifespanMs(now), r.getFreshnessMs(now), r.getExposureMs(now), notificationVisibility.rank, notificationVisibility.count);
                StatusBarNotification sbn = r.sbn;
                NotificationManagerService.this.cancelNotification(callingUid, callingPid, sbn.getPackageName(), sbn.getTag(), sbn.getId(), 16, 64, false, r.getUserId(), 1, notificationVisibility.rank, notificationVisibility.count, (ManagedServices.ManagedServiceInfo) null);
                nv.recycle();
                NotificationManagerService.this.reportUserInteraction(r);
            }
        }

        public void onNotificationActionClick(int callingUid, int callingPid, String key, int actionIndex, Notification.Action action, NotificationVisibility nv, boolean generatedByAssistant) {
            String str = key;
            int i = actionIndex;
            NotificationVisibility notificationVisibility = nv;
            boolean z = generatedByAssistant;
            NotificationManagerService.this.exitIdle();
            synchronized (NotificationManagerService.mNotificationLock) {
                try {
                    NotificationRecord r = NotificationManagerService.this.mNotificationsByKey.get(str);
                    if (r == null) {
                        Slog.w(NotificationManagerService.TAG, "No notification with key: " + str);
                        return;
                    }
                    long now = System.currentTimeMillis();
                    int i2 = 1;
                    LogMaker addTaggedData = r.getLogMaker(now).setCategory(MiuiSecurityInputMethodHelper.TEXT_PASSWORD).setType(4).setSubtype(i).addTaggedData(798, Integer.valueOf(notificationVisibility.rank)).addTaggedData(1395, Integer.valueOf(notificationVisibility.count)).addTaggedData(1601, Integer.valueOf(action.isContextual() ? 1 : 0));
                    if (!z) {
                        i2 = 0;
                    }
                    MetricsLogger.action(addTaggedData.addTaggedData(1600, Integer.valueOf(i2)).addTaggedData(1629, Integer.valueOf(notificationVisibility.location.toMetricsEventEnum())));
                    EventLogTags.writeNotificationActionClicked(key, actionIndex, r.getLifespanMs(now), r.getFreshnessMs(now), r.getExposureMs(now), notificationVisibility.rank, notificationVisibility.count);
                    nv.recycle();
                    NotificationManagerService.this.reportUserInteraction(r);
                    NotificationManagerService.this.mAssistants.notifyAssistantActionClicked(r.sbn, i, action, z);
                } catch (Throwable th) {
                    th = th;
                    throw th;
                }
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:18:0x0027, code lost:
            r1.this$0.cancelNotification(r22, r23, r24, r25, r26, 0, 66, true, r27, 2, r2.rank, r2.count, (com.android.server.notification.ManagedServices.ManagedServiceInfo) null);
            r31.recycle();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:19:0x004b, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onNotificationClear(int r22, int r23, java.lang.String r24, java.lang.String r25, int r26, int r27, java.lang.String r28, int r29, int r30, com.android.internal.statusbar.NotificationVisibility r31) {
            /*
                r21 = this;
                r1 = r21
                r2 = r31
                java.lang.Object r3 = com.android.server.notification.NotificationManagerService.mNotificationLock
                monitor-enter(r3)
                com.android.server.notification.NotificationManagerService r0 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x004e }
                android.util.ArrayMap<java.lang.String, com.android.server.notification.NotificationRecord> r0 = r0.mNotificationsByKey     // Catch:{ all -> 0x004e }
                r4 = r28
                java.lang.Object r0 = r0.get(r4)     // Catch:{ all -> 0x004c }
                com.android.server.notification.NotificationRecord r0 = (com.android.server.notification.NotificationRecord) r0     // Catch:{ all -> 0x004c }
                if (r0 == 0) goto L_0x0022
                r5 = r29
                r0.recordDismissalSurface(r5)     // Catch:{ all -> 0x0020 }
                r6 = r30
                r0.recordDismissalSentiment(r6)     // Catch:{ all -> 0x0057 }
                goto L_0x0026
            L_0x0020:
                r0 = move-exception
                goto L_0x0053
            L_0x0022:
                r5 = r29
                r6 = r30
            L_0x0026:
                monitor-exit(r3)     // Catch:{ all -> 0x0057 }
                com.android.server.notification.NotificationManagerService r7 = com.android.server.notification.NotificationManagerService.this
                r13 = 0
                r14 = 66
                r15 = 1
                r17 = 2
                int r0 = r2.rank
                int r3 = r2.count
                r20 = 0
                r8 = r22
                r9 = r23
                r10 = r24
                r11 = r25
                r12 = r26
                r16 = r27
                r18 = r0
                r19 = r3
                r7.cancelNotification(r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20)
                r31.recycle()
                return
            L_0x004c:
                r0 = move-exception
                goto L_0x0051
            L_0x004e:
                r0 = move-exception
                r4 = r28
            L_0x0051:
                r5 = r29
            L_0x0053:
                r6 = r30
            L_0x0055:
                monitor-exit(r3)     // Catch:{ all -> 0x0057 }
                throw r0
            L_0x0057:
                r0 = move-exception
                goto L_0x0055
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.notification.NotificationManagerService.AnonymousClass1.onNotificationClear(int, int, java.lang.String, java.lang.String, int, int, java.lang.String, int, int, com.android.internal.statusbar.NotificationVisibility):void");
        }

        public void onPanelRevealed(boolean clearEffects, int items) {
            MetricsLogger.visible(NotificationManagerService.this.getContext(), 127);
            MetricsLogger.histogram(NotificationManagerService.this.getContext(), "note_load", items);
            EventLogTags.writeNotificationPanelRevealed(items);
            if (clearEffects) {
                clearEffects();
            }
        }

        public void onPanelHidden() {
            MetricsLogger.hidden(NotificationManagerService.this.getContext(), 127);
            EventLogTags.writeNotificationPanelHidden();
        }

        public void clearEffects() {
            synchronized (NotificationManagerService.mNotificationLock) {
                if (NotificationManagerService.DBG) {
                    Slog.d(NotificationManagerService.TAG, "clearEffects");
                }
                NotificationManagerService.this.clearSoundLocked();
                NotificationManagerService.this.clearVibrateLocked();
                NotificationManagerService.this.clearLightsLocked();
            }
        }

        public void onNotificationError(int callingUid, int callingPid, String pkg, String tag, int id, int uid, int initialPid, String message, int userId) {
            boolean fgService;
            synchronized (NotificationManagerService.mNotificationLock) {
                NotificationRecord r = NotificationManagerService.this.findNotificationLocked(pkg, tag, id, userId);
                fgService = (r == null || (r.getNotification().flags & 64) == 0) ? false : true;
            }
            NotificationManagerService.this.cancelNotification(callingUid, callingPid, pkg, tag, id, 0, 0, false, userId, 4, (ManagedServices.ManagedServiceInfo) null);
            if (fgService) {
                Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable(uid, initialPid, pkg, tag, id, message) {
                    private final /* synthetic */ int f$1;
                    private final /* synthetic */ int f$2;
                    private final /* synthetic */ String f$3;
                    private final /* synthetic */ String f$4;
                    private final /* synthetic */ int f$5;
                    private final /* synthetic */ String f$6;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                        this.f$5 = r6;
                        this.f$6 = r7;
                    }

                    public final void runOrThrow() {
                        NotificationManagerService.AnonymousClass1.this.lambda$onNotificationError$0$NotificationManagerService$1(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$onNotificationError$0$NotificationManagerService$1(int uid, int initialPid, String pkg, String tag, int id, String message) throws Exception {
            IActivityManager access$900 = NotificationManagerService.this.mAm;
            access$900.crashApplication(uid, initialPid, pkg, -1, "Bad notification(tag=" + tag + ", id=" + id + ") posted from package " + pkg + ", crashing app(uid=" + uid + ", pid=" + initialPid + "): " + message);
        }

        public void onNotificationVisibilityChanged(NotificationVisibility[] newlyVisibleKeys, NotificationVisibility[] noLongerVisibleKeys) {
            synchronized (NotificationManagerService.mNotificationLock) {
                for (NotificationVisibility nv : newlyVisibleKeys) {
                    NotificationRecord r = NotificationManagerService.this.mNotificationsByKey.get(nv.key);
                    if (r != null) {
                        if (!r.isSeen()) {
                            if (NotificationManagerService.DBG) {
                                Slog.d(NotificationManagerService.TAG, "Marking notification as visible " + nv.key);
                            }
                            NotificationManagerService.this.reportSeen(r);
                        }
                        boolean isHun = true;
                        r.setVisibility(true, nv.rank, nv.count);
                        if (nv.location != NotificationVisibility.NotificationLocation.LOCATION_FIRST_HEADS_UP) {
                            isHun = false;
                        }
                        if (isHun || r.hasBeenVisiblyExpanded()) {
                            NotificationManagerService.this.logSmartSuggestionsVisible(r, nv.location.toMetricsEventEnum());
                        }
                        NotificationManagerService.this.maybeRecordInterruptionLocked(r);
                        nv.recycle();
                    }
                }
                for (NotificationVisibility nv2 : noLongerVisibleKeys) {
                    NotificationRecord r2 = NotificationManagerService.this.mNotificationsByKey.get(nv2.key);
                    if (r2 != null) {
                        r2.setVisibility(false, nv2.rank, nv2.count);
                        nv2.recycle();
                    }
                }
            }
        }

        public void onNotificationExpansionChanged(String key, boolean userAction, boolean expanded, int notificationLocation) {
            int i;
            synchronized (NotificationManagerService.mNotificationLock) {
                NotificationRecord r = NotificationManagerService.this.mNotificationsByKey.get(key);
                if (r != null) {
                    r.stats.onExpansionChanged(userAction, expanded);
                    if (r.hasBeenVisiblyExpanded()) {
                        NotificationManagerService.this.logSmartSuggestionsVisible(r, notificationLocation);
                    }
                    if (userAction) {
                        LogMaker itemLogMaker = r.getItemLogMaker();
                        if (expanded) {
                            i = 3;
                        } else {
                            i = 14;
                        }
                        MetricsLogger.action(itemLogMaker.setType(i));
                    }
                    if (expanded && userAction) {
                        r.recordExpanded();
                        NotificationManagerService.this.reportUserInteraction(r);
                    }
                    NotificationManagerService.this.mAssistants.notifyAssistantExpansionChangedLocked(r.sbn, userAction, expanded);
                }
            }
        }

        public void onNotificationDirectReplied(String key) {
            NotificationManagerService.this.exitIdle();
            synchronized (NotificationManagerService.mNotificationLock) {
                NotificationRecord r = NotificationManagerService.this.mNotificationsByKey.get(key);
                if (r != null) {
                    r.recordDirectReplied();
                    NotificationManagerService.this.mMetricsLogger.write(r.getLogMaker().setCategory(1590).setType(4));
                    NotificationManagerService.this.reportUserInteraction(r);
                    NotificationManagerService.this.mAssistants.notifyAssistantNotificationDirectReplyLocked(r.sbn);
                }
            }
        }

        public void onNotificationSmartSuggestionsAdded(String key, int smartReplyCount, int smartActionCount, boolean generatedByAssistant, boolean editBeforeSending) {
            synchronized (NotificationManagerService.mNotificationLock) {
                NotificationRecord r = NotificationManagerService.this.mNotificationsByKey.get(key);
                if (r != null) {
                    r.setNumSmartRepliesAdded(smartReplyCount);
                    r.setNumSmartActionsAdded(smartActionCount);
                    r.setSuggestionsGeneratedByAssistant(generatedByAssistant);
                    r.setEditChoicesBeforeSending(editBeforeSending);
                }
            }
        }

        public void onNotificationSmartReplySent(String key, int replyIndex, CharSequence reply, int notificationLocation, boolean modifiedBeforeSending) {
            synchronized (NotificationManagerService.mNotificationLock) {
                NotificationRecord r = NotificationManagerService.this.mNotificationsByKey.get(key);
                if (r != null) {
                    int i = 1;
                    LogMaker addTaggedData = r.getLogMaker().setCategory(1383).setSubtype(replyIndex).addTaggedData(1600, Integer.valueOf(r.getSuggestionsGeneratedByAssistant() ? 1 : 0)).addTaggedData(1629, Integer.valueOf(notificationLocation)).addTaggedData(1647, Integer.valueOf(r.getEditChoicesBeforeSending() ? 1 : 0));
                    if (!modifiedBeforeSending) {
                        i = 0;
                    }
                    NotificationManagerService.this.mMetricsLogger.write(addTaggedData.addTaggedData(1648, Integer.valueOf(i)));
                    NotificationManagerService.this.reportUserInteraction(r);
                    NotificationManagerService.this.mAssistants.notifyAssistantSuggestedReplySent(r.sbn, reply, r.getSuggestionsGeneratedByAssistant());
                }
            }
        }

        public void onNotificationSettingsViewed(String key) {
            synchronized (NotificationManagerService.mNotificationLock) {
                NotificationRecord r = NotificationManagerService.this.mNotificationsByKey.get(key);
                if (r != null) {
                    r.recordViewedSettings();
                }
            }
        }

        public void onNotificationBubbleChanged(String key, boolean isBubble) {
            synchronized (NotificationManagerService.mNotificationLock) {
                NotificationRecord r = NotificationManagerService.this.mNotificationsByKey.get(key);
                if (r != null) {
                    StatusBarNotification n = r.sbn;
                    int callingUid = n.getUid();
                    String pkg = n.getPackageName();
                    if (!isBubble || !NotificationManagerService.this.isNotificationAppropriateToBubble(r, pkg, callingUid, (NotificationRecord) null)) {
                        r.getNotification().flags &= -4097;
                    } else {
                        r.getNotification().flags |= 4096;
                    }
                }
            }
        }
    };
    private boolean mNotificationEffectsEnabledForAutomotive;
    /* access modifiers changed from: private */
    public Light mNotificationLight;
    @GuardedBy({"mNotificationLock"})
    final ArrayList<NotificationRecord> mNotificationList = new ArrayList<>();
    boolean mNotificationPulseEnabled;
    private final BroadcastReceiver mNotificationTimeoutReceiver = new BroadcastReceiver() {
        /* JADX WARNING: Code restructure failed: missing block: B:13:0x0026, code lost:
            if (r0 == null) goto L_?;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0028, code lost:
            r1.this$0.cancelNotification(r0.sbn.getUid(), r0.sbn.getInitialPid(), r0.sbn.getPackageName(), r0.sbn.getTag(), r0.sbn.getId(), 0, 64, true, r0.getUserId(), 19, (com.android.server.notification.ManagedServices.ManagedServiceInfo) null);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:23:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:24:?, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onReceive(android.content.Context r19, android.content.Intent r20) {
            /*
                r18 = this;
                r1 = r18
                java.lang.String r2 = r20.getAction()
                if (r2 != 0) goto L_0x0009
                return
            L_0x0009:
                java.lang.String r0 = com.android.server.notification.NotificationManagerService.ACTION_NOTIFICATION_TIMEOUT
                boolean r0 = r0.equals(r2)
                if (r0 == 0) goto L_0x005f
                java.lang.Object r3 = com.android.server.notification.NotificationManagerService.mNotificationLock
                monitor-enter(r3)
                com.android.server.notification.NotificationManagerService r0 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x0058 }
                java.lang.String r4 = "key"
                r5 = r20
                java.lang.String r4 = r5.getStringExtra(r4)     // Catch:{ all -> 0x005d }
                com.android.server.notification.NotificationRecord r0 = r0.findNotificationByKeyLocked(r4)     // Catch:{ all -> 0x005d }
                monitor-exit(r3)     // Catch:{ all -> 0x005d }
                if (r0 == 0) goto L_0x0061
                com.android.server.notification.NotificationManagerService r6 = com.android.server.notification.NotificationManagerService.this
                android.service.notification.StatusBarNotification r3 = r0.sbn
                int r7 = r3.getUid()
                android.service.notification.StatusBarNotification r3 = r0.sbn
                int r8 = r3.getInitialPid()
                android.service.notification.StatusBarNotification r3 = r0.sbn
                java.lang.String r9 = r3.getPackageName()
                android.service.notification.StatusBarNotification r3 = r0.sbn
                java.lang.String r10 = r3.getTag()
                android.service.notification.StatusBarNotification r3 = r0.sbn
                int r11 = r3.getId()
                r12 = 0
                r13 = 64
                r14 = 1
                int r15 = r0.getUserId()
                r16 = 19
                r17 = 0
                r6.cancelNotification(r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17)
                goto L_0x0061
            L_0x0058:
                r0 = move-exception
                r5 = r20
            L_0x005b:
                monitor-exit(r3)     // Catch:{ all -> 0x005d }
                throw r0
            L_0x005d:
                r0 = move-exception
                goto L_0x005b
            L_0x005f:
                r5 = r20
            L_0x0061:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.notification.NotificationManagerService.AnonymousClass4.onReceive(android.content.Context, android.content.Intent):void");
        }
    };
    @GuardedBy({"mNotificationLock"})
    final ArrayMap<String, NotificationRecord> mNotificationsByKey = new ArrayMap<>();
    private final BroadcastReceiver mPackageIntentReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            boolean packageChanged;
            boolean packageChanged2;
            boolean removingPackage;
            boolean z;
            int[] uidList;
            String[] pkgList;
            int changeUserId;
            boolean removingPackage2;
            String pkgName;
            int i;
            Intent intent2 = intent;
            String action = intent.getAction();
            if (action != null) {
                boolean queryRestart = false;
                boolean queryRemove = false;
                boolean packageChanged3 = false;
                boolean cancelNotifications = true;
                boolean hideNotifications = false;
                boolean unhideNotifications = false;
                if (!action.equals("android.intent.action.PACKAGE_ADDED")) {
                    boolean equals = action.equals("android.intent.action.PACKAGE_REMOVED");
                    queryRemove = equals;
                    if (!equals && !action.equals("android.intent.action.PACKAGE_RESTARTED")) {
                        boolean equals2 = action.equals("android.intent.action.PACKAGE_CHANGED");
                        packageChanged3 = equals2;
                        if (!equals2) {
                            boolean equals3 = action.equals("android.intent.action.QUERY_PACKAGE_RESTART");
                            queryRestart = equals3;
                            if (!equals3 && !action.equals("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE") && !action.equals("android.intent.action.PACKAGES_SUSPENDED") && !action.equals("android.intent.action.PACKAGES_UNSUSPENDED") && !action.equals("android.intent.action.DISTRACTING_PACKAGES_CHANGED")) {
                                boolean z2 = packageChanged3;
                                return;
                            }
                        } else {
                            packageChanged = packageChanged3;
                            packageChanged2 = false;
                        }
                    }
                    packageChanged = packageChanged3;
                    packageChanged2 = queryRestart;
                } else {
                    packageChanged = false;
                    packageChanged2 = false;
                }
                int changeUserId2 = intent2.getIntExtra("android.intent.extra.user_handle", -1);
                boolean removingPackage3 = queryRemove && !intent2.getBooleanExtra("android.intent.extra.REPLACING", false);
                if (NotificationManagerService.DBG) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("action=");
                    sb.append(action);
                    sb.append(" removing=");
                    removingPackage = removingPackage3;
                    sb.append(removingPackage);
                    Slog.i(NotificationManagerService.TAG, sb.toString());
                } else {
                    removingPackage = removingPackage3;
                }
                boolean queryRemove2 = queryRemove;
                if (action.equals("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE")) {
                    pkgList = intent2.getStringArrayExtra("android.intent.extra.changed_package_list");
                    uidList = intent2.getIntArrayExtra("android.intent.extra.changed_uid_list");
                    z = false;
                } else if (action.equals("android.intent.action.PACKAGES_SUSPENDED")) {
                    pkgList = intent2.getStringArrayExtra("android.intent.extra.changed_package_list");
                    uidList = intent2.getIntArrayExtra("android.intent.extra.changed_uid_list");
                    cancelNotifications = false;
                    hideNotifications = true;
                    z = false;
                } else if (action.equals("android.intent.action.PACKAGES_UNSUSPENDED")) {
                    pkgList = intent2.getStringArrayExtra("android.intent.extra.changed_package_list");
                    uidList = intent2.getIntArrayExtra("android.intent.extra.changed_uid_list");
                    cancelNotifications = false;
                    unhideNotifications = true;
                    z = false;
                } else if (action.equals("android.intent.action.DISTRACTING_PACKAGES_CHANGED")) {
                    if ((intent2.getIntExtra("android.intent.extra.distraction_restrictions", 0) & 2) != 0) {
                        cancelNotifications = false;
                        hideNotifications = true;
                        pkgList = intent2.getStringArrayExtra("android.intent.extra.changed_package_list");
                        uidList = intent2.getIntArrayExtra("android.intent.extra.changed_uid_list");
                    } else {
                        cancelNotifications = false;
                        unhideNotifications = true;
                        pkgList = intent2.getStringArrayExtra("android.intent.extra.changed_package_list");
                        uidList = intent2.getIntArrayExtra("android.intent.extra.changed_uid_list");
                    }
                    z = false;
                } else if (packageChanged2) {
                    pkgList = intent2.getStringArrayExtra("android.intent.extra.PACKAGES");
                    uidList = new int[]{intent2.getIntExtra("android.intent.extra.UID", -1)};
                    z = false;
                } else {
                    Uri uri = intent.getData();
                    if (uri != null && (pkgName = uri.getSchemeSpecificPart()) != null) {
                        if (packageChanged) {
                            try {
                                IPackageManager access$1500 = NotificationManagerService.this.mPackageManager;
                                if (changeUserId2 != -1) {
                                    i = changeUserId2;
                                } else {
                                    i = 0;
                                }
                                int enabled = access$1500.getApplicationEnabledSetting(pkgName, i);
                                if (enabled == 1 || enabled == 0) {
                                    cancelNotifications = false;
                                }
                            } catch (IllegalArgumentException e) {
                                if (NotificationManagerService.DBG) {
                                    Slog.i(NotificationManagerService.TAG, "Exception trying to look up app enabled setting", e);
                                }
                            } catch (RemoteException e2) {
                            }
                        }
                        z = false;
                        pkgList = new String[]{pkgName};
                        uidList = new int[]{intent2.getIntExtra("android.intent.extra.UID", -1)};
                    } else {
                        return;
                    }
                }
                if (pkgList == null || pkgList.length <= 0) {
                    removingPackage2 = removingPackage;
                    changeUserId = changeUserId2;
                } else if (cancelNotifications) {
                    int length = pkgList.length;
                    int i2 = z;
                    while (i2 < length) {
                        int changeUserId3 = changeUserId2;
                        NotificationManagerService.this.cancelAllNotificationsInt(NotificationManagerService.MY_UID, NotificationManagerService.MY_PID, pkgList[i2], (String) null, 0, 0, !packageChanged2 ? true : z, changeUserId3, 5, (ManagedServices.ManagedServiceInfo) null);
                        i2++;
                        z = z;
                        removingPackage = removingPackage;
                        length = length;
                        changeUserId2 = changeUserId3;
                    }
                    removingPackage2 = removingPackage;
                    changeUserId = changeUserId2;
                } else {
                    removingPackage2 = removingPackage;
                    changeUserId = changeUserId2;
                    if (hideNotifications) {
                        NotificationManagerService.this.hideNotificationsForPackages(pkgList);
                    } else if (unhideNotifications) {
                        NotificationManagerService.this.unhideNotificationsForPackages(pkgList);
                    }
                }
                NotificationManagerService.mHandler.scheduleOnPackageChanged(removingPackage2, changeUserId, pkgList, uidList);
                boolean z3 = packageChanged2;
                boolean z4 = queryRemove2;
            }
        }
    };
    /* access modifiers changed from: private */
    public IPackageManager mPackageManager;
    /* access modifiers changed from: private */
    public PackageManager mPackageManagerClient;
    private AtomicFile mPolicyFile;
    /* access modifiers changed from: private */
    public PreferencesHelper mPreferencesHelper;
    /* access modifiers changed from: private */
    public RankingHandler mRankingHandler;
    /* access modifiers changed from: private */
    public RankingHelper mRankingHelper;
    private final HandlerThread mRankingThread = new HandlerThread("ranker", 10);
    private final BroadcastReceiver mRestoreReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if ("android.os.action.SETTING_RESTORED".equals(intent.getAction())) {
                try {
                    String element = intent.getStringExtra("setting_name");
                    String newValue = intent.getStringExtra("new_value");
                    int restoredFromSdkInt = intent.getIntExtra("restored_from_sdk_int", 0);
                    NotificationManagerService.this.mListeners.onSettingRestored(element, newValue, restoredFromSdkInt, getSendingUserId());
                    NotificationManagerService.this.mConditionProviders.onSettingRestored(element, newValue, restoredFromSdkInt, getSendingUserId());
                } catch (Exception e) {
                    Slog.wtf(NotificationManagerService.TAG, "Cannot restore managed services from settings", e);
                }
            }
        }
    };
    private RoleObserver mRoleObserver;
    boolean mScreenOn = true;
    @VisibleForTesting
    final IBinder mService = new INotificationManager.Stub() {
        /* Debug info: failed to restart local var, previous not found, register: 26 */
        /* JADX INFO: finally extract failed */
        public void enqueueToast(String pkg, ITransientNotification callback, int duration, int displayId) {
            ArrayList<ToastRecord> arrayList;
            String str = pkg;
            ITransientNotification iTransientNotification = callback;
            int i = duration;
            int i2 = displayId;
            if (NotificationManagerService.DBG) {
                Slog.i(NotificationManagerService.TAG, "enqueueToast pkg=" + str + " callback=" + iTransientNotification + " duration=" + i + " displayId=" + i2);
            }
            if (str == null || iTransientNotification == null) {
                Slog.e(NotificationManagerService.TAG, "Not enqueuing toast. pkg=" + str + " callback=" + iTransientNotification);
                return;
            }
            int callingUid = Binder.getCallingUid();
            boolean isSystemToast = NotificationManagerService.this.isCallerSystemOrPhone() || PackageManagerService.PLATFORM_PACKAGE_NAME.equals(str);
            boolean access$2900 = NotificationManagerService.this.isPackageSuspendedForUser(str, callingUid);
            boolean z = !areNotificationsEnabledForPackage(str, callingUid);
            long callingIdentity = Binder.clearCallingIdentity();
            try {
                int uidImportance = NotificationManagerService.this.mActivityManager.getUidImportance(callingUid);
                Binder.restoreCallingIdentity(callingIdentity);
                ArrayList<ToastRecord> arrayList2 = NotificationManagerService.this.mToastQueue;
                synchronized (arrayList2) {
                    try {
                        int callingPid = Binder.getCallingPid();
                        long callingId = Binder.clearCallingIdentity();
                        try {
                            int index = NotificationManagerService.this.indexOfToastLocked(str, iTransientNotification);
                            if (index >= 0) {
                                try {
                                    NotificationManagerService.this.mToastQueue.get(index).update(i);
                                    int i3 = callingPid;
                                    arrayList = arrayList2;
                                } catch (Throwable th) {
                                    th = th;
                                    int i4 = callingPid;
                                    ArrayList<ToastRecord> arrayList3 = arrayList2;
                                    Binder.restoreCallingIdentity(callingId);
                                    throw th;
                                }
                            } else {
                                if (!isSystemToast) {
                                    int count = 0;
                                    int N = NotificationManagerService.this.mToastQueue.size();
                                    int i5 = 0;
                                    while (i5 < N) {
                                        if (!NotificationManagerService.this.mToastQueue.get(i5).pkg.equals(str) || (count = count + 1) < 25) {
                                            i5++;
                                            index = index;
                                        } else {
                                            StringBuilder sb = new StringBuilder();
                                            int i6 = index;
                                            sb.append("Package has already posted ");
                                            sb.append(count);
                                            sb.append(" toasts. Not showing more. Package=");
                                            sb.append(str);
                                            Slog.e(NotificationManagerService.TAG, sb.toString());
                                            Binder.restoreCallingIdentity(callingId);
                                        }
                                    }
                                }
                                Binder token = new Binder();
                                NotificationManagerService.this.mWindowManagerInternal.addWindowToken(token, 2005, i2);
                                int callingPid2 = callingPid;
                                arrayList = arrayList2;
                                try {
                                    ToastRecord record = new ToastRecord(callingPid, pkg, callback, duration, token, displayId);
                                    NotificationManagerService.this.mToastQueue.add(record);
                                    int index2 = NotificationManagerService.this.mToastQueue.size() - 1;
                                    try {
                                        NotificationManagerService.this.keepProcessAliveIfNeededLocked(callingPid2);
                                        index = index2;
                                    } catch (Throwable th2) {
                                        th = th2;
                                        Binder.restoreCallingIdentity(callingId);
                                        throw th;
                                    }
                                } catch (Throwable th3) {
                                    th = th3;
                                    throw th;
                                }
                            }
                            if (index == 0) {
                                NotificationManagerService.this.showNextToastLocked();
                            }
                            Binder.restoreCallingIdentity(callingId);
                        } catch (Throwable th4) {
                            th = th4;
                            int i7 = callingPid;
                            ArrayList<ToastRecord> arrayList4 = arrayList2;
                            Binder.restoreCallingIdentity(callingId);
                            throw th;
                        }
                    } catch (Throwable th5) {
                        th = th5;
                        arrayList = arrayList2;
                        throw th;
                    }
                }
            } catch (Throwable th6) {
                Binder.restoreCallingIdentity(callingIdentity);
                throw th6;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 7 */
        public void cancelToast(String pkg, ITransientNotification callback) {
            Slog.i(NotificationManagerService.TAG, "cancelToast pkg=" + pkg + " callback=" + callback);
            if (pkg == null || callback == null) {
                Slog.e(NotificationManagerService.TAG, "Not cancelling notification. pkg=" + pkg + " callback=" + callback);
                return;
            }
            synchronized (NotificationManagerService.this.mToastQueue) {
                long callingId = Binder.clearCallingIdentity();
                try {
                    int index = NotificationManagerService.this.indexOfToastLocked(pkg, callback);
                    if (index >= 0) {
                        NotificationManagerService.this.cancelToastLocked(index);
                    } else {
                        Slog.w(NotificationManagerService.TAG, "Toast already cancelled. pkg=" + pkg + " callback=" + callback);
                    }
                } finally {
                    Binder.restoreCallingIdentity(callingId);
                }
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 8 */
        public void finishToken(String pkg, ITransientNotification callback) {
            synchronized (NotificationManagerService.this.mToastQueue) {
                long callingId = Binder.clearCallingIdentity();
                try {
                    int index = NotificationManagerService.this.indexOfToastLocked(pkg, callback);
                    if (index >= 0) {
                        ToastRecord record = NotificationManagerService.this.mToastQueue.get(index);
                        NotificationManagerService.this.finishTokenLocked(record.token, record.displayId);
                    } else {
                        Slog.w(NotificationManagerService.TAG, "Toast already killed. pkg=" + pkg + " callback=" + callback);
                    }
                } finally {
                    Binder.restoreCallingIdentity(callingId);
                }
            }
        }

        public void enqueueNotificationWithTag(String pkg, String opPkg, String tag, int id, Notification notification, int userId) throws RemoteException {
            NotificationManagerService.this.enqueueNotificationInternal(pkg, opPkg, Binder.getCallingUid(), Binder.getCallingPid(), tag, id, notification, userId);
        }

        /* JADX WARNING: Removed duplicated region for block: B:13:0x0036  */
        /* JADX WARNING: Removed duplicated region for block: B:14:0x0039  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void cancelNotificationWithTag(java.lang.String r16, java.lang.String r17, int r18, int r19) {
            /*
                r15 = this;
                r1 = r15
                com.android.server.notification.NotificationManagerService r0 = com.android.server.notification.NotificationManagerService.this     // Catch:{ SecurityException -> 0x000b }
                r14 = r16
                r0.checkCallerIsSystemOrSameApp(r14)     // Catch:{ SecurityException -> 0x0009 }
                goto L_0x001a
            L_0x0009:
                r0 = move-exception
                goto L_0x000e
            L_0x000b:
                r0 = move-exception
                r14 = r16
            L_0x000e:
                com.android.server.notification.NotificationManagerService r2 = com.android.server.notification.NotificationManagerService.this
                android.content.Context r2 = r2.getContext()
                boolean r2 = com.android.server.notification.NotificationManagerServiceInjector.checkCallerIsXmsf(r2)
                if (r2 == 0) goto L_0x0057
            L_0x001a:
                int r2 = android.os.Binder.getCallingPid()
                int r3 = android.os.Binder.getCallingUid()
                r5 = 1
                r6 = 0
                java.lang.String r7 = "cancelNotificationWithTag"
                r4 = r19
                r8 = r16
                int r0 = android.app.ActivityManager.handleIncomingUser(r2, r3, r4, r5, r6, r7, r8)
                com.android.server.notification.NotificationManagerService r2 = com.android.server.notification.NotificationManagerService.this
                boolean r2 = r2.isCallingUidSystem()
                if (r2 == 0) goto L_0x0039
                r2 = 0
                r9 = r2
                goto L_0x003c
            L_0x0039:
                r2 = 1088(0x440, float:1.525E-42)
                r9 = r2
            L_0x003c:
                com.android.server.notification.NotificationManagerService r2 = com.android.server.notification.NotificationManagerService.this
                int r3 = android.os.Binder.getCallingUid()
                int r4 = android.os.Binder.getCallingPid()
                r8 = 0
                r10 = 0
                r12 = 8
                r13 = 0
                r5 = r16
                r6 = r17
                r7 = r18
                r11 = r0
                r2.cancelNotification(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13)
                return
            L_0x0057:
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.notification.NotificationManagerService.AnonymousClass10.cancelNotificationWithTag(java.lang.String, java.lang.String, int, int):void");
        }

        public void cancelAllNotifications(String pkg, int userId) {
            String str = pkg;
            NotificationManagerService.this.checkCallerIsSystemOrSameApp(pkg);
            NotificationManagerService.this.cancelAllNotificationsInt(Binder.getCallingUid(), Binder.getCallingPid(), pkg, (String) null, 0, 64, true, ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), userId, true, false, "cancelAllNotifications", pkg), 9, (ManagedServices.ManagedServiceInfo) null);
        }

        public void setNotificationsEnabledForPackage(String pkg, int uid, boolean enabled) {
            String str = pkg;
            boolean z = enabled;
            enforceSystemOrSystemUI("setNotificationsEnabledForPackage");
            NotificationManagerService.this.mPreferencesHelper.setEnabled(str, uid, z);
            NotificationManagerService.this.mMetricsLogger.write(new LogMaker(147).setType(4).setPackageName(str).setSubtype(z ? 1 : 0));
            if (!z) {
                NotificationManagerService.this.cancelAllNotificationsInt(NotificationManagerService.MY_UID, NotificationManagerService.MY_PID, pkg, (String) null, 0, 0, true, UserHandle.getUserId(uid), 7, (ManagedServices.ManagedServiceInfo) null);
            }
            try {
                NotificationManagerService.this.getContext().sendBroadcastAsUser(new Intent("android.app.action.APP_BLOCK_STATE_CHANGED").putExtra("android.app.extra.BLOCKED_STATE", !z).addFlags(268435456).setPackage(str), UserHandle.of(UserHandle.getUserId(uid)), (String) null);
            } catch (SecurityException e) {
                Slog.w(NotificationManagerService.TAG, "Can't notify app about app block change", e);
            }
            NotificationManagerService.this.handleSavePolicyFile();
        }

        public void setNotificationsEnabledWithImportanceLockForPackage(String pkg, int uid, boolean enabled) {
            setNotificationsEnabledForPackage(pkg, uid, enabled);
            NotificationManagerService.this.mPreferencesHelper.setAppImportanceLocked(pkg, uid);
        }

        public boolean areNotificationsEnabled(String pkg) {
            return areNotificationsEnabledForPackage(pkg, Binder.getCallingUid());
        }

        public boolean areNotificationsEnabledForPackage(String pkg, int uid) {
            enforceSystemOrSystemUIOrSamePackage(pkg, "Caller not system or systemui or same package");
            if (UserHandle.getCallingUserId() != UserHandle.getUserId(uid)) {
                Context context = NotificationManagerService.this.getContext();
                context.enforceCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS", "canNotifyAsPackage for uid " + uid);
            }
            return NotificationManagerService.this.mPreferencesHelper.getImportance(pkg, uid) != 0;
        }

        public boolean areBubblesAllowed(String pkg) {
            return areBubblesAllowedForPackage(pkg, Binder.getCallingUid());
        }

        public boolean areBubblesAllowedForPackage(String pkg, int uid) {
            enforceSystemOrSystemUIOrSamePackage(pkg, "Caller not system or systemui or same package");
            if (UserHandle.getCallingUserId() != UserHandle.getUserId(uid)) {
                Context context = NotificationManagerService.this.getContext();
                context.enforceCallingPermission("android.permission.INTERACT_ACROSS_USERS", "canNotifyAsPackage for uid " + uid);
            }
            return NotificationManagerService.this.mPreferencesHelper.areBubblesAllowed(pkg, uid);
        }

        public void setBubblesAllowed(String pkg, int uid, boolean allowed) {
            enforceSystemOrSystemUI("Caller not system or systemui");
            NotificationManagerService.this.mPreferencesHelper.setBubblesAllowed(pkg, uid, allowed);
            NotificationManagerService.this.handleSavePolicyFile();
        }

        public boolean hasUserApprovedBubblesForPackage(String pkg, int uid) {
            enforceSystemOrSystemUI("Caller not system or systemui");
            return (NotificationManagerService.this.mPreferencesHelper.getAppLockedFields(pkg, uid) & 2) != 0;
        }

        public boolean shouldHideSilentStatusIcons(String callingPkg) {
            NotificationManagerService.this.checkCallerIsSameApp(callingPkg);
            if (NotificationManagerService.this.isCallerSystemOrPhone() || NotificationManagerService.this.mListeners.isListenerPackage(callingPkg)) {
                return NotificationManagerService.this.mPreferencesHelper.shouldHideSilentStatusIcons();
            }
            throw new SecurityException("Only available for notification listeners");
        }

        public void setHideSilentStatusIcons(boolean hide) {
            NotificationManagerService.this.checkCallerIsSystem();
            NotificationManagerService.this.mPreferencesHelper.setHideSilentStatusIcons(hide);
            NotificationManagerService.this.handleSavePolicyFile();
            NotificationManagerService.this.mListeners.onStatusBarIconsBehaviorChanged(hide);
        }

        public int getPackageImportance(String pkg) {
            NotificationManagerService.this.checkCallerIsSystemOrSameApp(pkg);
            return NotificationManagerService.this.mPreferencesHelper.getImportance(pkg, Binder.getCallingUid());
        }

        public boolean canShowBadge(String pkg, int uid) {
            NotificationManagerService.this.checkCallerIsSystem();
            return NotificationManagerService.this.mPreferencesHelper.canShowBadge(pkg, uid);
        }

        public void setShowBadge(String pkg, int uid, boolean showBadge) {
            NotificationManagerService.this.checkCallerIsSystem();
            NotificationManagerService.this.mPreferencesHelper.setShowBadge(pkg, uid, showBadge);
            NotificationManagerService.this.handleSavePolicyFile();
        }

        public void setNotificationDelegate(String callingPkg, String delegate) {
            NotificationManagerService.this.checkCallerIsSameApp(callingPkg);
            int callingUid = Binder.getCallingUid();
            UserHandle user = UserHandle.getUserHandleForUid(callingUid);
            if (delegate == null) {
                NotificationManagerService.this.mPreferencesHelper.revokeNotificationDelegate(callingPkg, Binder.getCallingUid());
                NotificationManagerService.this.handleSavePolicyFile();
                return;
            }
            try {
                ApplicationInfo info = NotificationManagerService.this.mPackageManager.getApplicationInfo(delegate, 786432, user.getIdentifier());
                if (info != null) {
                    NotificationManagerService.this.mPreferencesHelper.setNotificationDelegate(callingPkg, callingUid, delegate, info.uid);
                    NotificationManagerService.this.handleSavePolicyFile();
                }
            } catch (RemoteException e) {
                e.rethrowFromSystemServer();
            }
        }

        public String getNotificationDelegate(String callingPkg) {
            NotificationManagerService.this.checkCallerIsSystemOrSameApp(callingPkg);
            return NotificationManagerService.this.mPreferencesHelper.getNotificationDelegate(callingPkg, Binder.getCallingUid());
        }

        public boolean canNotifyAsPackage(String callingPkg, String targetPkg, int userId) {
            NotificationManagerService.this.checkCallerIsSameApp(callingPkg);
            int callingUid = Binder.getCallingUid();
            if (UserHandle.getUserHandleForUid(callingUid).getIdentifier() != userId) {
                Context context = NotificationManagerService.this.getContext();
                context.enforceCallingPermission("android.permission.INTERACT_ACROSS_USERS", "canNotifyAsPackage for user " + userId);
            }
            if (callingPkg.equals(targetPkg)) {
                return true;
            }
            try {
                ApplicationInfo info = NotificationManagerService.this.mPackageManager.getApplicationInfo(targetPkg, 786432, userId);
                if (info != null) {
                    return NotificationManagerService.this.mPreferencesHelper.isDelegateAllowed(targetPkg, info.uid, callingPkg, callingUid);
                }
                return false;
            } catch (RemoteException e) {
                return false;
            }
        }

        public void updateNotificationChannelGroupForPackage(String pkg, int uid, NotificationChannelGroup group) throws RemoteException {
            enforceSystemOrSystemUI("Caller not system or systemui");
            NotificationManagerService.this.createNotificationChannelGroup(pkg, uid, group, false, false);
            NotificationManagerService.this.handleSavePolicyFile();
        }

        public void createNotificationChannelGroups(String pkg, ParceledListSlice channelGroupList) throws RemoteException {
            NotificationManagerService.this.checkCallerIsSystemOrSameApp(pkg);
            List<NotificationChannelGroup> groups = channelGroupList.getList();
            int groupSize = groups.size();
            for (int i = 0; i < groupSize; i++) {
                NotificationManagerService.this.createNotificationChannelGroup(pkg, Binder.getCallingUid(), groups.get(i), true, false);
            }
            NotificationManagerService.this.handleSavePolicyFile();
        }

        private void createNotificationChannelsImpl(String pkg, int uid, ParceledListSlice channelsList) {
            List<NotificationChannel> channels = channelsList.getList();
            int channelsSize = channels.size();
            boolean needsPolicyFileChange = false;
            for (int i = 0; i < channelsSize; i++) {
                NotificationChannel channel = channels.get(i);
                Preconditions.checkNotNull(channel, "channel in list is null");
                needsPolicyFileChange = NotificationManagerService.this.mPreferencesHelper.createNotificationChannel(pkg, uid, channel, true, NotificationManagerService.this.mConditionProviders.isPackageOrComponentAllowed(pkg, UserHandle.getUserId(uid)));
                if (needsPolicyFileChange) {
                    NotificationManagerService.this.mListeners.notifyNotificationChannelChanged(pkg, UserHandle.getUserHandleForUid(uid), NotificationManagerService.this.mPreferencesHelper.getNotificationChannel(pkg, uid, channel.getId(), false), 1);
                }
            }
            if (needsPolicyFileChange) {
                NotificationManagerService.this.handleSavePolicyFile();
            }
        }

        public void createNotificationChannels(String pkg, ParceledListSlice channelsList) throws RemoteException {
            NotificationManagerService.this.checkCallerIsSystemOrSameApp(pkg);
            createNotificationChannelsImpl(pkg, Binder.getCallingUid(), channelsList);
        }

        public void createNotificationChannelsForPackage(String pkg, int uid, ParceledListSlice channelsList) throws RemoteException {
            try {
                NotificationManagerService.this.checkCallerIsSystem();
            } catch (SecurityException e) {
                if (!NotificationManagerServiceInjector.checkCallerIsXmsf(NotificationManagerService.this.getContext())) {
                    throw e;
                }
            }
            createNotificationChannelsImpl(pkg, uid, channelsList);
        }

        public NotificationChannel getNotificationChannel(String callingPkg, int userId, String targetPkg, String channelId) {
            if (canNotifyAsPackage(callingPkg, targetPkg, userId) || NotificationManagerService.this.isCallingUidSystem()) {
                int targetUid = -1;
                try {
                    targetUid = NotificationManagerService.this.mPackageManagerClient.getPackageUidAsUser(targetPkg, userId);
                } catch (PackageManager.NameNotFoundException e) {
                }
                return NotificationManagerService.this.mPreferencesHelper.getNotificationChannel(targetPkg, targetUid, channelId, false);
            }
            throw new SecurityException("Pkg " + callingPkg + " cannot read channels for " + targetPkg + " in " + userId);
        }

        public NotificationChannel getNotificationChannelForPackage(String pkg, int uid, String channelId, boolean includeDeleted) {
            NotificationManagerService.this.checkCallerIsSystem();
            return NotificationManagerService.this.mPreferencesHelper.getNotificationChannel(pkg, uid, channelId, includeDeleted);
        }

        public void deleteNotificationChannel(String pkg, String channelId) {
            String pkg2;
            int notificationUid;
            String pkg3 = pkg;
            String str = channelId;
            NotificationManagerService.this.checkCallerIsSystemOrSameApp(pkg3);
            int callingUid = Binder.getCallingUid();
            if (!"miscellaneous".equals(str)) {
                int notificationUid2 = callingUid;
                NotificationManagerServiceInjectorBase.InjectInfo injectInfo = NotificationManagerServiceInjector.injectDeleteNotificationChannel(NotificationManagerService.this.getContext(), pkg3, str);
                if (injectInfo != null) {
                    pkg2 = injectInfo.appPkg;
                    notificationUid = injectInfo.appUid;
                } else {
                    pkg2 = pkg3;
                    notificationUid = notificationUid2;
                }
                NotificationManagerServiceInjectorBase.InjectInfo injectInfo2 = injectInfo;
                int notificationUid3 = notificationUid;
                NotificationManagerService.this.cancelAllNotificationsInt(NotificationManagerService.MY_UID, NotificationManagerService.MY_PID, pkg2, channelId, 0, 0, true, UserHandle.getUserId(callingUid), 17, (ManagedServices.ManagedServiceInfo) null);
                NotificationManagerService.this.mPreferencesHelper.deleteNotificationChannel(pkg2, notificationUid3, str);
                NotificationManagerService.this.mListeners.notifyNotificationChannelChanged(pkg2, UserHandle.getUserHandleForUid(callingUid), NotificationManagerService.this.mPreferencesHelper.getNotificationChannel(pkg2, notificationUid3, str, true), 3);
                NotificationManagerService.this.handleSavePolicyFile();
                return;
            }
            throw new IllegalArgumentException("Cannot delete default channel");
        }

        public NotificationChannelGroup getNotificationChannelGroup(String pkg, String groupId) {
            NotificationManagerService.this.checkCallerIsSystemOrSameApp(pkg);
            return NotificationManagerService.this.mPreferencesHelper.getNotificationChannelGroupWithChannels(pkg, Binder.getCallingUid(), groupId, false);
        }

        public ParceledListSlice<NotificationChannelGroup> getNotificationChannelGroups(String pkg) {
            NotificationManagerService.this.checkCallerIsSystemOrSameApp(pkg);
            return NotificationManagerService.this.mPreferencesHelper.getNotificationChannelGroups(pkg, Binder.getCallingUid(), false, false, true);
        }

        public void deleteNotificationChannelGroup(String pkg, String groupId) {
            String pkg2 = pkg;
            String str = groupId;
            NotificationManagerService.this.checkCallerIsSystemOrSameApp(pkg2);
            int callingUid = Binder.getCallingUid();
            int notificationUid = callingUid;
            NotificationManagerServiceInjectorBase.InjectInfo injectInfo = NotificationManagerServiceInjector.injectDeleteNotificationChannelGroup(NotificationManagerService.this.getContext(), pkg2, str);
            if (injectInfo != null) {
                pkg2 = injectInfo.appPkg;
                notificationUid = injectInfo.appUid;
            }
            NotificationChannelGroup groupToDelete = NotificationManagerService.this.mPreferencesHelper.getNotificationChannelGroup(str, pkg2, notificationUid);
            if (groupToDelete != null) {
                List<NotificationChannel> deletedChannels = NotificationManagerService.this.mPreferencesHelper.deleteNotificationChannelGroup(pkg2, notificationUid, str);
                int i = 0;
                while (i < deletedChannels.size()) {
                    NotificationChannel deletedChannel = deletedChannels.get(i);
                    NotificationManagerService.this.cancelAllNotificationsInt(NotificationManagerService.MY_UID, NotificationManagerService.MY_PID, pkg2, deletedChannel.getId(), 0, 0, true, UserHandle.getUserId(Binder.getCallingUid()), 17, (ManagedServices.ManagedServiceInfo) null);
                    NotificationManagerService.this.mListeners.notifyNotificationChannelChanged(pkg2, UserHandle.getUserHandleForUid(callingUid), deletedChannel, 3);
                    i++;
                    deletedChannels = deletedChannels;
                    groupToDelete = groupToDelete;
                }
                int i2 = i;
                List<NotificationChannel> list = deletedChannels;
                NotificationManagerService.this.mListeners.notifyNotificationChannelGroupChanged(pkg2, UserHandle.getUserHandleForUid(callingUid), groupToDelete, 3);
                NotificationManagerService.this.handleSavePolicyFile();
                return;
            }
        }

        public void updateNotificationChannelForPackage(String pkg, int uid, NotificationChannel channel) {
            enforceSystemOrSystemUI("Caller not system or systemui");
            Preconditions.checkNotNull(channel);
            NotificationManagerService.this.updateNotificationChannelInt(pkg, uid, channel, false);
        }

        public ParceledListSlice<NotificationChannel> getNotificationChannelsForPackage(String pkg, int uid, boolean includeDeleted) {
            enforceSystemOrSystemUI("getNotificationChannelsForPackage");
            return NotificationManagerService.this.mPreferencesHelper.getNotificationChannels(pkg, uid, includeDeleted);
        }

        public int getNumNotificationChannelsForPackage(String pkg, int uid, boolean includeDeleted) {
            enforceSystemOrSystemUI("getNumNotificationChannelsForPackage");
            return NotificationManagerService.this.mPreferencesHelper.getNotificationChannels(pkg, uid, includeDeleted).getList().size();
        }

        public boolean onlyHasDefaultChannel(String pkg, int uid) {
            enforceSystemOrSystemUI("onlyHasDefaultChannel");
            return NotificationManagerService.this.mPreferencesHelper.onlyHasDefaultChannel(pkg, uid);
        }

        public int getDeletedChannelCount(String pkg, int uid) {
            enforceSystemOrSystemUI("getDeletedChannelCount");
            return NotificationManagerService.this.mPreferencesHelper.getDeletedChannelCount(pkg, uid);
        }

        public int getBlockedChannelCount(String pkg, int uid) {
            enforceSystemOrSystemUI("getBlockedChannelCount");
            return NotificationManagerService.this.mPreferencesHelper.getBlockedChannelCount(pkg, uid);
        }

        public ParceledListSlice<NotificationChannelGroup> getNotificationChannelGroupsForPackage(String pkg, int uid, boolean includeDeleted) {
            enforceSystemOrSystemUI("getNotificationChannelGroupsForPackage");
            return NotificationManagerService.this.mPreferencesHelper.getNotificationChannelGroups(pkg, uid, includeDeleted, true, false);
        }

        public NotificationChannelGroup getPopulatedNotificationChannelGroupForPackage(String pkg, int uid, String groupId, boolean includeDeleted) {
            enforceSystemOrSystemUI("getPopulatedNotificationChannelGroupForPackage");
            return NotificationManagerService.this.mPreferencesHelper.getNotificationChannelGroupWithChannels(pkg, uid, groupId, includeDeleted);
        }

        public NotificationChannelGroup getNotificationChannelGroupForPackage(String groupId, String pkg, int uid) {
            enforceSystemOrSystemUI("getNotificationChannelGroupForPackage");
            return NotificationManagerService.this.mPreferencesHelper.getNotificationChannelGroup(groupId, pkg, uid);
        }

        public ParceledListSlice<NotificationChannel> getNotificationChannels(String callingPkg, String targetPkg, int userId) {
            if (canNotifyAsPackage(callingPkg, targetPkg, userId) || NotificationManagerService.this.isCallingUidSystem()) {
                int targetUid = -1;
                try {
                    targetUid = NotificationManagerService.this.mPackageManagerClient.getPackageUidAsUser(targetPkg, userId);
                } catch (PackageManager.NameNotFoundException e) {
                }
                return NotificationManagerService.this.mPreferencesHelper.getNotificationChannels(targetPkg, targetUid, false);
            }
            throw new SecurityException("Pkg " + callingPkg + " cannot read channels for " + targetPkg + " in " + userId);
        }

        public int getBlockedAppCount(int userId) {
            NotificationManagerService.this.checkCallerIsSystem();
            return NotificationManagerService.this.mPreferencesHelper.getBlockedAppCount(userId);
        }

        public int getAppsBypassingDndCount(int userId) {
            NotificationManagerService.this.checkCallerIsSystem();
            return NotificationManagerService.this.mPreferencesHelper.getAppsBypassingDndCount(userId);
        }

        public ParceledListSlice<NotificationChannel> getNotificationChannelsBypassingDnd(String pkg, int userId) {
            NotificationManagerService.this.checkCallerIsSystem();
            return NotificationManagerService.this.mPreferencesHelper.getNotificationChannelsBypassingDnd(pkg, userId);
        }

        public boolean areChannelsBypassingDnd() {
            return NotificationManagerService.this.mPreferencesHelper.areChannelsBypassingDnd();
        }

        public void clearData(String packageName, int uid, boolean fromApp) throws RemoteException {
            NotificationManagerService.this.checkCallerIsSystem();
            NotificationManagerService.this.cancelAllNotificationsInt(NotificationManagerService.MY_UID, NotificationManagerService.MY_PID, packageName, (String) null, 0, 0, true, UserHandle.getUserId(Binder.getCallingUid()), 17, (ManagedServices.ManagedServiceInfo) null);
            String[] packages = {packageName};
            int[] uids = {uid};
            NotificationManagerService.this.mListeners.onPackagesChanged(true, packages, uids);
            NotificationManagerService.this.mAssistants.onPackagesChanged(true, packages, uids);
            NotificationManagerService.this.mConditionProviders.onPackagesChanged(true, packages, uids);
            NotificationManagerService.this.mSnoozeHelper.clearData(UserHandle.getUserId(uid), packageName);
            if (!fromApp) {
                NotificationManagerService.this.mPreferencesHelper.clearData(packageName, uid);
            }
            NotificationManagerService.this.handleSavePolicyFile();
        }

        public List<String> getAllowedAssistantAdjustments(String pkg) {
            NotificationManagerService.this.checkCallerIsSystemOrSameApp(pkg);
            if (NotificationManagerService.this.isCallerSystemOrPhone() || NotificationManagerService.this.mAssistants.isPackageAllowed(pkg, UserHandle.getCallingUserId())) {
                return NotificationManagerService.this.mAssistants.getAllowedAssistantAdjustments();
            }
            throw new SecurityException("Not currently an assistant");
        }

        public void allowAssistantAdjustment(String adjustmentType) {
            NotificationManagerService.this.checkCallerIsSystemOrSystemUiOrShell();
            NotificationManagerService.this.mAssistants.allowAdjustmentType(adjustmentType);
            NotificationManagerService.this.handleSavePolicyFile();
        }

        public void disallowAssistantAdjustment(String adjustmentType) {
            NotificationManagerService.this.checkCallerIsSystemOrSystemUiOrShell();
            NotificationManagerService.this.mAssistants.disallowAdjustmentType(adjustmentType);
            NotificationManagerService.this.handleSavePolicyFile();
        }

        public StatusBarNotification[] getActiveNotifications(String callingPkg) {
            NotificationManagerService.this.getContext().enforceCallingOrSelfPermission("android.permission.ACCESS_NOTIFICATIONS", "NotificationManagerService.getActiveNotifications");
            StatusBarNotification[] tmp = null;
            if (NotificationManagerService.this.mAppOps.noteOpNoThrow(25, Binder.getCallingUid(), callingPkg) == 0) {
                synchronized (NotificationManagerService.mNotificationLock) {
                    tmp = new StatusBarNotification[NotificationManagerService.this.mNotificationList.size()];
                    int N = NotificationManagerService.this.mNotificationList.size();
                    for (int i = 0; i < N; i++) {
                        tmp[i] = NotificationManagerService.this.mNotificationList.get(i).sbn;
                    }
                }
            }
            return tmp;
        }

        public ParceledListSlice<StatusBarNotification> getAppActiveNotifications(String pkg, int incomingUserId) {
            ParceledListSlice<StatusBarNotification> parceledListSlice;
            try {
                NotificationManagerService.this.checkCallerIsSystemOrSameApp(pkg);
            } catch (SecurityException e) {
                if (!NotificationManagerServiceInjector.checkCallerIsXmsf(NotificationManagerService.this.getContext())) {
                    throw e;
                }
            }
            int userId = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), incomingUserId, true, false, "getAppActiveNotifications", pkg);
            synchronized (NotificationManagerService.mNotificationLock) {
                ArrayMap<String, StatusBarNotification> map = new ArrayMap<>(NotificationManagerService.this.mNotificationList.size() + NotificationManagerService.this.mEnqueuedNotifications.size());
                int N = NotificationManagerService.this.mNotificationList.size();
                for (int i = 0; i < N; i++) {
                    StatusBarNotification sbn = sanitizeSbn(pkg, userId, NotificationManagerService.this.mNotificationList.get(i).sbn);
                    if (sbn != null) {
                        map.put(sbn.getKey(), sbn);
                    }
                }
                for (NotificationRecord snoozed : NotificationManagerService.this.mSnoozeHelper.getSnoozed(userId, pkg)) {
                    StatusBarNotification sbn2 = sanitizeSbn(pkg, userId, snoozed.sbn);
                    if (sbn2 != null) {
                        map.put(sbn2.getKey(), sbn2);
                    }
                }
                int M = NotificationManagerService.this.mEnqueuedNotifications.size();
                for (int i2 = 0; i2 < M; i2++) {
                    StatusBarNotification sbn3 = sanitizeSbn(pkg, userId, NotificationManagerService.this.mEnqueuedNotifications.get(i2).sbn);
                    if (sbn3 != null) {
                        map.put(sbn3.getKey(), sbn3);
                    }
                }
                ArrayList<StatusBarNotification> list = new ArrayList<>(map.size());
                list.addAll(map.values());
                parceledListSlice = new ParceledListSlice<>(list);
            }
            return parceledListSlice;
        }

        private StatusBarNotification sanitizeSbn(String pkg, int userId, StatusBarNotification sbn) {
            String str = pkg;
            if (sbn.getUserId() != userId) {
                return null;
            }
            if (sbn.getPackageName().equals(str) || sbn.getOpPkg().equals(str)) {
                return new StatusBarNotification(sbn.getPackageName(), sbn.getOpPkg(), sbn.getId(), sbn.getTag(), sbn.getUid(), sbn.getInitialPid(), sbn.getNotification().clone(), sbn.getUser(), sbn.getOverrideGroupKey(), sbn.getPostTime());
            }
            return null;
        }

        public StatusBarNotification[] getHistoricalNotifications(String callingPkg, int count) {
            NotificationManagerService.this.getContext().enforceCallingOrSelfPermission("android.permission.ACCESS_NOTIFICATIONS", "NotificationManagerService.getHistoricalNotifications");
            StatusBarNotification[] tmp = null;
            if (NotificationManagerService.this.mAppOps.noteOpNoThrow(25, Binder.getCallingUid(), callingPkg) == 0) {
                synchronized (NotificationManagerService.this.mArchive) {
                    tmp = NotificationManagerService.this.mArchive.getArray(count);
                }
            }
            return tmp;
        }

        public void registerListener(INotificationListener listener, ComponentName component, int userid) {
            enforceSystemOrSystemUI("INotificationManager.registerListener");
            NotificationManagerService.this.mListeners.registerService(listener, component, userid);
        }

        public void unregisterListener(INotificationListener token, int userid) {
            NotificationManagerService.this.mListeners.unregisterService((IInterface) token, userid);
        }

        /* Debug info: failed to restart local var, previous not found, register: 21 */
        public void cancelNotificationsFromListener(INotificationListener token, String[] keys) {
            int N;
            int i;
            String[] strArr = keys;
            int callingUid = Binder.getCallingUid();
            int callingPid = Binder.getCallingPid();
            long identity = Binder.clearCallingIdentity();
            try {
                synchronized (NotificationManagerService.mNotificationLock) {
                    ManagedServices.ManagedServiceInfo info = NotificationManagerService.this.mListeners.checkServiceTokenLocked(token);
                    if (strArr != null) {
                        int N2 = strArr.length;
                        int i2 = 0;
                        while (i2 < N2) {
                            NotificationRecord r = NotificationManagerService.this.mNotificationsByKey.get(strArr[i2]);
                            if (r == null) {
                                i = i2;
                                N = N2;
                            } else {
                                int userId = r.sbn.getUserId();
                                if (!(userId == info.userid || userId == -1)) {
                                    if (!NotificationManagerService.this.mUserProfiles.isCurrentProfile(userId)) {
                                        throw new SecurityException("Disallowed call from listener: " + info.service);
                                    }
                                }
                                String packageName = r.sbn.getPackageName();
                                String tag = r.sbn.getTag();
                                NotificationRecord notificationRecord = r;
                                String str = packageName;
                                i = i2;
                                String str2 = tag;
                                N = N2;
                                cancelNotificationFromListenerLocked(info, callingUid, callingPid, str, str2, r.sbn.getId(), userId);
                            }
                            i2 = i + 1;
                            INotificationListener iNotificationListener = token;
                            N2 = N;
                        }
                        int i3 = i2;
                        int i4 = N2;
                    } else {
                        NotificationManagerService.this.cancelAllLocked(callingUid, callingPid, info.userid, 11, info, info.supportsProfiles());
                    }
                }
                Binder.restoreCallingIdentity(identity);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }

        public void requestBindListener(ComponentName component) {
            ManagedServices manager;
            NotificationManagerService.this.checkCallerIsSystemOrSameApp(component.getPackageName());
            long identity = Binder.clearCallingIdentity();
            try {
                if (NotificationManagerService.this.mAssistants.isComponentEnabledForCurrentProfiles(component)) {
                    manager = NotificationManagerService.this.mAssistants;
                } else {
                    manager = NotificationManagerService.this.mListeners;
                }
                manager.setComponentState(component, true);
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 7 */
        public void requestUnbindListener(INotificationListener token) {
            long identity = Binder.clearCallingIdentity();
            try {
                synchronized (NotificationManagerService.mNotificationLock) {
                    ManagedServices.ManagedServiceInfo info = NotificationManagerService.this.mListeners.checkServiceTokenLocked(token);
                    info.getOwner().setComponentState(info.component, false);
                }
                Binder.restoreCallingIdentity(identity);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 12 */
        /* JADX WARNING: Code restructure failed: missing block: B:36:0x00a8, code lost:
            android.os.Binder.restoreCallingIdentity(r0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:37:0x00ac, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void setNotificationsShownFromListener(android.service.notification.INotificationListener r13, java.lang.String[] r14) {
            /*
                r12 = this;
                long r0 = android.os.Binder.clearCallingIdentity()
                java.lang.Object r2 = com.android.server.notification.NotificationManagerService.mNotificationLock     // Catch:{ all -> 0x00b0 }
                monitor-enter(r2)     // Catch:{ all -> 0x00b0 }
                com.android.server.notification.NotificationManagerService r3 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x00ad }
                com.android.server.notification.NotificationManagerService$NotificationListeners r3 = r3.mListeners     // Catch:{ all -> 0x00ad }
                com.android.server.notification.ManagedServices$ManagedServiceInfo r3 = r3.checkServiceTokenLocked(r13)     // Catch:{ all -> 0x00ad }
                if (r14 != 0) goto L_0x0018
                monitor-exit(r2)     // Catch:{ all -> 0x00ad }
                android.os.Binder.restoreCallingIdentity(r0)
                return
            L_0x0018:
                java.util.ArrayList r4 = new java.util.ArrayList     // Catch:{ all -> 0x00ad }
                r4.<init>()     // Catch:{ all -> 0x00ad }
                int r5 = r14.length     // Catch:{ all -> 0x00ad }
                r6 = 0
            L_0x001f:
                if (r6 >= r5) goto L_0x0098
                com.android.server.notification.NotificationManagerService r7 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x00ad }
                android.util.ArrayMap<java.lang.String, com.android.server.notification.NotificationRecord> r7 = r7.mNotificationsByKey     // Catch:{ all -> 0x00ad }
                r8 = r14[r6]     // Catch:{ all -> 0x00ad }
                java.lang.Object r7 = r7.get(r8)     // Catch:{ all -> 0x00ad }
                com.android.server.notification.NotificationRecord r7 = (com.android.server.notification.NotificationRecord) r7     // Catch:{ all -> 0x00ad }
                if (r7 != 0) goto L_0x0030
                goto L_0x0095
            L_0x0030:
                android.service.notification.StatusBarNotification r8 = r7.sbn     // Catch:{ all -> 0x00ad }
                int r8 = r8.getUserId()     // Catch:{ all -> 0x00ad }
                int r9 = r3.userid     // Catch:{ all -> 0x00ad }
                if (r8 == r9) goto L_0x0063
                r9 = -1
                if (r8 == r9) goto L_0x0063
                com.android.server.notification.NotificationManagerService r9 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x00ad }
                com.android.server.notification.ManagedServices$UserProfiles r9 = r9.mUserProfiles     // Catch:{ all -> 0x00ad }
                boolean r9 = r9.isCurrentProfile(r8)     // Catch:{ all -> 0x00ad }
                if (r9 == 0) goto L_0x004a
                goto L_0x0063
            L_0x004a:
                java.lang.SecurityException r9 = new java.lang.SecurityException     // Catch:{ all -> 0x00ad }
                java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ad }
                r10.<init>()     // Catch:{ all -> 0x00ad }
                java.lang.String r11 = "Disallowed call from listener: "
                r10.append(r11)     // Catch:{ all -> 0x00ad }
                android.os.IInterface r11 = r3.service     // Catch:{ all -> 0x00ad }
                r10.append(r11)     // Catch:{ all -> 0x00ad }
                java.lang.String r10 = r10.toString()     // Catch:{ all -> 0x00ad }
                r9.<init>(r10)     // Catch:{ all -> 0x00ad }
                throw r9     // Catch:{ all -> 0x00ad }
            L_0x0063:
                r4.add(r7)     // Catch:{ all -> 0x00ad }
                boolean r9 = r7.isSeen()     // Catch:{ all -> 0x00ad }
                if (r9 != 0) goto L_0x0095
                boolean r9 = com.android.server.notification.NotificationManagerService.DBG     // Catch:{ all -> 0x00ad }
                if (r9 == 0) goto L_0x0088
                java.lang.String r9 = "NotificationService"
                java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ad }
                r10.<init>()     // Catch:{ all -> 0x00ad }
                java.lang.String r11 = "Marking notification as seen "
                r10.append(r11)     // Catch:{ all -> 0x00ad }
                r11 = r14[r6]     // Catch:{ all -> 0x00ad }
                r10.append(r11)     // Catch:{ all -> 0x00ad }
                java.lang.String r10 = r10.toString()     // Catch:{ all -> 0x00ad }
                android.util.Slog.d(r9, r10)     // Catch:{ all -> 0x00ad }
            L_0x0088:
                com.android.server.notification.NotificationManagerService r9 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x00ad }
                r9.reportSeen(r7)     // Catch:{ all -> 0x00ad }
                r7.setSeen()     // Catch:{ all -> 0x00ad }
                com.android.server.notification.NotificationManagerService r9 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x00ad }
                r9.maybeRecordInterruptionLocked(r7)     // Catch:{ all -> 0x00ad }
            L_0x0095:
                int r6 = r6 + 1
                goto L_0x001f
            L_0x0098:
                boolean r6 = r4.isEmpty()     // Catch:{ all -> 0x00ad }
                if (r6 != 0) goto L_0x00a7
                com.android.server.notification.NotificationManagerService r6 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x00ad }
                com.android.server.notification.NotificationManagerService$NotificationAssistants r6 = r6.mAssistants     // Catch:{ all -> 0x00ad }
                r6.onNotificationsSeenLocked(r4)     // Catch:{ all -> 0x00ad }
            L_0x00a7:
                monitor-exit(r2)     // Catch:{ all -> 0x00ad }
                android.os.Binder.restoreCallingIdentity(r0)
                return
            L_0x00ad:
                r3 = move-exception
                monitor-exit(r2)     // Catch:{ all -> 0x00ad }
                throw r3     // Catch:{ all -> 0x00b0 }
            L_0x00b0:
                r2 = move-exception
                android.os.Binder.restoreCallingIdentity(r0)
                throw r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.notification.NotificationManagerService.AnonymousClass10.setNotificationsShownFromListener(android.service.notification.INotificationListener, java.lang.String[]):void");
        }

        @GuardedBy({"mNotificationLock"})
        private void cancelNotificationFromListenerLocked(ManagedServices.ManagedServiceInfo info, int callingUid, int callingPid, String pkg, String tag, int id, int userId) {
            NotificationManagerService.this.cancelNotification(callingUid, callingPid, pkg, tag, id, 0, 4162, true, userId, 10, info);
        }

        /* Debug info: failed to restart local var, previous not found, register: 10 */
        public void snoozeNotificationUntilContextFromListener(INotificationListener token, String key, String snoozeCriterionId) {
            long identity = Binder.clearCallingIdentity();
            try {
                synchronized (NotificationManagerService.mNotificationLock) {
                    NotificationManagerService.this.snoozeNotificationInt(key, -1, snoozeCriterionId, NotificationManagerService.this.mListeners.checkServiceTokenLocked(token));
                }
                Binder.restoreCallingIdentity(identity);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 10 */
        public void snoozeNotificationUntilFromListener(INotificationListener token, String key, long duration) {
            long identity = Binder.clearCallingIdentity();
            try {
                synchronized (NotificationManagerService.mNotificationLock) {
                    NotificationManagerService.this.snoozeNotificationInt(key, duration, (String) null, NotificationManagerService.this.mListeners.checkServiceTokenLocked(token));
                }
                Binder.restoreCallingIdentity(identity);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 5 */
        public void unsnoozeNotificationFromAssistant(INotificationListener token, String key) {
            long identity = Binder.clearCallingIdentity();
            try {
                synchronized (NotificationManagerService.mNotificationLock) {
                    NotificationManagerService.this.unsnoozeNotificationInt(key, NotificationManagerService.this.mAssistants.checkServiceTokenLocked(token));
                }
                Binder.restoreCallingIdentity(identity);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 16 */
        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0052, code lost:
            android.os.Binder.restoreCallingIdentity(r11);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:15:0x0056, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void cancelNotificationFromListener(android.service.notification.INotificationListener r17, java.lang.String r18, java.lang.String r19, int r20) {
            /*
                r16 = this;
                int r9 = android.os.Binder.getCallingUid()
                int r10 = android.os.Binder.getCallingPid()
                long r11 = android.os.Binder.clearCallingIdentity()
                java.lang.Object r13 = com.android.server.notification.NotificationManagerService.mNotificationLock     // Catch:{ all -> 0x0060 }
                monitor-enter(r13)     // Catch:{ all -> 0x0060 }
                r14 = r16
                com.android.server.notification.NotificationManagerService r0 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x0057 }
                com.android.server.notification.NotificationManagerService$NotificationListeners r0 = r0.mListeners     // Catch:{ all -> 0x0057 }
                r15 = r17
                com.android.server.notification.ManagedServices$ManagedServiceInfo r0 = r0.checkServiceTokenLocked(r15)     // Catch:{ all -> 0x005e }
                boolean r1 = r0.supportsProfiles()     // Catch:{ all -> 0x005e }
                if (r1 == 0) goto L_0x0041
                java.lang.String r1 = "NotificationService"
                java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x005e }
                r2.<init>()     // Catch:{ all -> 0x005e }
                java.lang.String r3 = "Ignoring deprecated cancelNotification(pkg, tag, id) from "
                r2.append(r3)     // Catch:{ all -> 0x005e }
                android.content.ComponentName r3 = r0.component     // Catch:{ all -> 0x005e }
                r2.append(r3)     // Catch:{ all -> 0x005e }
                java.lang.String r3 = " use cancelNotification(key) instead."
                r2.append(r3)     // Catch:{ all -> 0x005e }
                java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x005e }
                android.util.Slog.e(r1, r2)     // Catch:{ all -> 0x005e }
                goto L_0x0051
            L_0x0041:
                int r8 = r0.userid     // Catch:{ all -> 0x005e }
                r1 = r16
                r2 = r0
                r3 = r9
                r4 = r10
                r5 = r18
                r6 = r19
                r7 = r20
                r1.cancelNotificationFromListenerLocked(r2, r3, r4, r5, r6, r7, r8)     // Catch:{ all -> 0x005e }
            L_0x0051:
                monitor-exit(r13)     // Catch:{ all -> 0x005e }
                android.os.Binder.restoreCallingIdentity(r11)
                return
            L_0x0057:
                r0 = move-exception
                r15 = r17
            L_0x005a:
                monitor-exit(r13)     // Catch:{ all -> 0x005e }
                throw r0     // Catch:{ all -> 0x005c }
            L_0x005c:
                r0 = move-exception
                goto L_0x0065
            L_0x005e:
                r0 = move-exception
                goto L_0x005a
            L_0x0060:
                r0 = move-exception
                r14 = r16
                r15 = r17
            L_0x0065:
                android.os.Binder.restoreCallingIdentity(r11)
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.notification.NotificationManagerService.AnonymousClass10.cancelNotificationFromListener(android.service.notification.INotificationListener, java.lang.String, java.lang.String, int):void");
        }

        public ParceledListSlice<StatusBarNotification> getActiveNotificationsFromListener(INotificationListener token, String[] keys, int trim) {
            ParceledListSlice<StatusBarNotification> parceledListSlice;
            NotificationRecord r;
            synchronized (NotificationManagerService.mNotificationLock) {
                ManagedServices.ManagedServiceInfo info = NotificationManagerService.this.mListeners.checkServiceTokenLocked(token);
                boolean getKeys = keys != null;
                int N = getKeys ? keys.length : NotificationManagerService.this.mNotificationList.size();
                ArrayList<StatusBarNotification> list = new ArrayList<>(N);
                for (int i = 0; i < N; i++) {
                    if (getKeys) {
                        r = NotificationManagerService.this.mNotificationsByKey.get(keys[i]);
                    } else {
                        r = NotificationManagerService.this.mNotificationList.get(i);
                    }
                    if (r != null) {
                        StatusBarNotification sbn = r.sbn;
                        if (NotificationManagerService.this.isVisibleToListener(sbn, info)) {
                            list.add(trim == 0 ? sbn : sbn.cloneLight());
                        }
                    }
                }
                parceledListSlice = new ParceledListSlice<>(list);
            }
            return parceledListSlice;
        }

        public ParceledListSlice<StatusBarNotification> getSnoozedNotificationsFromListener(INotificationListener token, int trim) {
            ParceledListSlice<StatusBarNotification> parceledListSlice;
            synchronized (NotificationManagerService.mNotificationLock) {
                ManagedServices.ManagedServiceInfo info = NotificationManagerService.this.mListeners.checkServiceTokenLocked(token);
                List<NotificationRecord> snoozedRecords = NotificationManagerService.this.mSnoozeHelper.getSnoozed();
                int N = snoozedRecords.size();
                ArrayList<StatusBarNotification> list = new ArrayList<>(N);
                for (int i = 0; i < N; i++) {
                    NotificationRecord r = snoozedRecords.get(i);
                    if (r != null) {
                        StatusBarNotification sbn = r.sbn;
                        if (NotificationManagerService.this.isVisibleToListener(sbn, info)) {
                            list.add(trim == 0 ? sbn : sbn.cloneLight());
                        }
                    }
                }
                parceledListSlice = new ParceledListSlice<>(list);
            }
            return parceledListSlice;
        }

        /* Debug info: failed to restart local var, previous not found, register: 5 */
        public void clearRequestedListenerHints(INotificationListener token) {
            long identity = Binder.clearCallingIdentity();
            try {
                synchronized (NotificationManagerService.mNotificationLock) {
                    boolean unused = NotificationManagerService.this.removeDisabledHints(NotificationManagerService.this.mListeners.checkServiceTokenLocked(token));
                    NotificationManagerService.this.updateListenerHintsLocked();
                    NotificationManagerService.this.updateEffectsSuppressorLocked();
                }
                Binder.restoreCallingIdentity(identity);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 7 */
        public void requestHintsFromListener(INotificationListener token, int hints) {
            long identity = Binder.clearCallingIdentity();
            try {
                synchronized (NotificationManagerService.mNotificationLock) {
                    ManagedServices.ManagedServiceInfo info = NotificationManagerService.this.mListeners.checkServiceTokenLocked(token);
                    if ((hints & 7) != 0) {
                        NotificationManagerService.this.addDisabledHints(info, hints);
                    } else {
                        boolean unused = NotificationManagerService.this.removeDisabledHints(info, hints);
                    }
                    NotificationManagerService.this.updateListenerHintsLocked();
                    NotificationManagerService.this.updateEffectsSuppressorLocked();
                }
                Binder.restoreCallingIdentity(identity);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }

        public int getHintsFromListener(INotificationListener token) {
            int access$4800;
            synchronized (NotificationManagerService.mNotificationLock) {
                access$4800 = NotificationManagerService.this.mListenerHints;
            }
            return access$4800;
        }

        /* Debug info: failed to restart local var, previous not found, register: 6 */
        public void requestInterruptionFilterFromListener(INotificationListener token, int interruptionFilter) throws RemoteException {
            long identity = Binder.clearCallingIdentity();
            try {
                synchronized (NotificationManagerService.mNotificationLock) {
                    NotificationManagerService.this.mZenModeHelper.requestFromListener(NotificationManagerService.this.mListeners.checkServiceTokenLocked(token).component, interruptionFilter);
                    NotificationManagerService.this.updateInterruptionFilterLocked();
                }
                Binder.restoreCallingIdentity(identity);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }

        public int getInterruptionFilterFromListener(INotificationListener token) throws RemoteException {
            int access$4900;
            synchronized (NotificationManagerService.this.mNotificationLight) {
                access$4900 = NotificationManagerService.this.mInterruptionFilter;
            }
            return access$4900;
        }

        public void setOnNotificationPostedTrimFromListener(INotificationListener token, int trim) throws RemoteException {
            synchronized (NotificationManagerService.mNotificationLock) {
                ManagedServices.ManagedServiceInfo info = NotificationManagerService.this.mListeners.checkServiceTokenLocked(token);
                if (info != null) {
                    NotificationManagerService.this.mListeners.setOnNotificationPostedTrimLocked(info, trim);
                }
            }
        }

        public int getZenMode() {
            return NotificationManagerService.this.mZenModeHelper.getZenMode();
        }

        public ZenModeConfig getZenModeConfig() {
            enforceSystemOrSystemUI("INotificationManager.getZenModeConfig");
            return NotificationManagerService.this.mZenModeHelper.getConfig();
        }

        public void setZenMode(int mode, Uri conditionId, String reason) throws RemoteException {
            enforceSystemOrSystemUI("INotificationManager.setZenMode");
            long identity = Binder.clearCallingIdentity();
            try {
                NotificationManagerService.this.mZenModeHelper.setManualZenMode(mode, conditionId, (String) null, reason);
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }

        public List<ZenModeConfig.ZenRule> getZenRules() throws RemoteException {
            enforcePolicyAccess(Binder.getCallingUid(), "getAutomaticZenRules");
            return NotificationManagerService.this.mZenModeHelper.getZenRules();
        }

        public AutomaticZenRule getAutomaticZenRule(String id) throws RemoteException {
            Preconditions.checkNotNull(id, "Id is null");
            enforcePolicyAccess(Binder.getCallingUid(), "getAutomaticZenRule");
            return NotificationManagerService.this.mZenModeHelper.getAutomaticZenRule(id);
        }

        public String addAutomaticZenRule(AutomaticZenRule automaticZenRule) {
            Preconditions.checkNotNull(automaticZenRule, "automaticZenRule is null");
            Preconditions.checkNotNull(automaticZenRule.getName(), "Name is null");
            if (automaticZenRule.getOwner() == null && automaticZenRule.getConfigurationActivity() == null) {
                throw new NullPointerException("Rule must have a conditionproviderservice and/or configuration activity");
            }
            Preconditions.checkNotNull(automaticZenRule.getConditionId(), "ConditionId is null");
            if (automaticZenRule.getZenPolicy() == null || automaticZenRule.getInterruptionFilter() == 2) {
                enforcePolicyAccess(Binder.getCallingUid(), "addAutomaticZenRule");
                return NotificationManagerService.this.mZenModeHelper.addAutomaticZenRule(automaticZenRule, "addAutomaticZenRule");
            }
            throw new IllegalArgumentException("ZenPolicy is only applicable to INTERRUPTION_FILTER_PRIORITY filters");
        }

        public boolean updateAutomaticZenRule(String id, AutomaticZenRule automaticZenRule) throws RemoteException {
            Preconditions.checkNotNull(automaticZenRule, "automaticZenRule is null");
            Preconditions.checkNotNull(automaticZenRule.getName(), "Name is null");
            if (automaticZenRule.getOwner() == null && automaticZenRule.getConfigurationActivity() == null) {
                throw new NullPointerException("Rule must have a conditionproviderservice and/or configuration activity");
            }
            Preconditions.checkNotNull(automaticZenRule.getConditionId(), "ConditionId is null");
            enforcePolicyAccess(Binder.getCallingUid(), "updateAutomaticZenRule");
            return NotificationManagerService.this.mZenModeHelper.updateAutomaticZenRule(id, automaticZenRule, "updateAutomaticZenRule");
        }

        public boolean removeAutomaticZenRule(String id) throws RemoteException {
            Preconditions.checkNotNull(id, "Id is null");
            enforcePolicyAccess(Binder.getCallingUid(), "removeAutomaticZenRule");
            return NotificationManagerService.this.mZenModeHelper.removeAutomaticZenRule(id, "removeAutomaticZenRule");
        }

        public boolean removeAutomaticZenRules(String packageName) throws RemoteException {
            Preconditions.checkNotNull(packageName, "Package name is null");
            enforceSystemOrSystemUI("removeAutomaticZenRules");
            return NotificationManagerService.this.mZenModeHelper.removeAutomaticZenRules(packageName, "removeAutomaticZenRules");
        }

        public int getRuleInstanceCount(ComponentName owner) throws RemoteException {
            Preconditions.checkNotNull(owner, "Owner is null");
            enforceSystemOrSystemUI("getRuleInstanceCount");
            return NotificationManagerService.this.mZenModeHelper.getCurrentInstanceCount(owner);
        }

        public void setAutomaticZenRuleState(String id, Condition condition) {
            Preconditions.checkNotNull(id, "id is null");
            Preconditions.checkNotNull(condition, "Condition is null");
            enforcePolicyAccess(Binder.getCallingUid(), "setAutomaticZenRuleState");
            NotificationManagerService.this.mZenModeHelper.setAutomaticZenRuleState(id, condition);
        }

        public void setInterruptionFilter(String pkg, int filter) throws RemoteException {
            enforcePolicyAccess(pkg, "setInterruptionFilter");
            int zen = NotificationManager.zenModeFromInterruptionFilter(filter, -1);
            if (zen != -1) {
                long identity = Binder.clearCallingIdentity();
                try {
                    NotificationManagerService.this.mZenModeHelper.setManualZenMode(zen, (Uri) null, pkg, "setInterruptionFilter");
                } finally {
                    Binder.restoreCallingIdentity(identity);
                }
            } else {
                throw new IllegalArgumentException("Invalid filter: " + filter);
            }
        }

        public void notifyConditions(final String pkg, IConditionProvider provider, final Condition[] conditions) {
            final ManagedServices.ManagedServiceInfo info = NotificationManagerService.this.mConditionProviders.checkServiceToken(provider);
            NotificationManagerService.this.checkCallerIsSystemOrSameApp(pkg);
            NotificationManagerService.mHandler.post(new Runnable() {
                public void run() {
                    NotificationManagerService.this.mConditionProviders.notifyConditions(pkg, info, conditions);
                }
            });
        }

        public void requestUnbindProvider(IConditionProvider provider) {
            long identity = Binder.clearCallingIdentity();
            try {
                ManagedServices.ManagedServiceInfo info = NotificationManagerService.this.mConditionProviders.checkServiceToken(provider);
                info.getOwner().setComponentState(info.component, false);
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }

        public void requestBindProvider(ComponentName component) {
            NotificationManagerService.this.checkCallerIsSystemOrSameApp(component.getPackageName());
            long identity = Binder.clearCallingIdentity();
            try {
                NotificationManagerService.this.mConditionProviders.setComponentState(component, true);
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }

        private void enforceSystemOrSystemUI(String message) {
            if (!NotificationManagerService.this.isCallerSystemOrPhone()) {
                NotificationManagerService.this.getContext().enforceCallingPermission("android.permission.STATUS_BAR_SERVICE", message);
            }
        }

        private void enforceSystemOrSystemUIOrSamePackage(String pkg, String message) {
            try {
                NotificationManagerService.this.checkCallerIsSystemOrSameApp(pkg);
            } catch (SecurityException e) {
                NotificationManagerService.this.getContext().enforceCallingPermission("android.permission.STATUS_BAR_SERVICE", message);
            }
        }

        private void enforcePolicyAccess(int uid, String method) {
            if (NotificationManagerService.this.getContext().checkCallingPermission("android.permission.MANAGE_NOTIFICATIONS") != 0) {
                boolean accessAllowed = false;
                for (String isPackageOrComponentAllowed : NotificationManagerService.this.mPackageManagerClient.getPackagesForUid(uid)) {
                    if (NotificationManagerService.this.mConditionProviders.isPackageOrComponentAllowed(isPackageOrComponentAllowed, UserHandle.getUserId(uid))) {
                        accessAllowed = true;
                    }
                }
                if (!accessAllowed) {
                    Slog.w(NotificationManagerService.TAG, "Notification policy access denied calling " + method);
                    throw new SecurityException("Notification policy access denied");
                }
            }
        }

        private void enforcePolicyAccess(String pkg, String method) {
            if (NotificationManagerService.this.getContext().checkCallingPermission("android.permission.MANAGE_NOTIFICATIONS") != 0) {
                NotificationManagerService.this.checkCallerIsSameApp(pkg);
                if (!checkPolicyAccess(pkg)) {
                    Slog.w(NotificationManagerService.TAG, "Notification policy access denied calling " + method);
                    throw new SecurityException("Notification policy access denied");
                }
            }
        }

        private boolean checkPackagePolicyAccess(String pkg) {
            return NotificationManagerService.this.mConditionProviders.isPackageOrComponentAllowed(pkg, getCallingUserHandle().getIdentifier());
        }

        private boolean checkPolicyAccess(String pkg) {
            try {
                if (ActivityManager.checkComponentPermission("android.permission.MANAGE_NOTIFICATIONS", NotificationManagerService.this.getContext().getPackageManager().getPackageUidAsUser(pkg, UserHandle.getCallingUserId()), -1, true) == 0) {
                    return true;
                }
                if (checkPackagePolicyAccess(pkg) || NotificationManagerService.this.mListeners.isComponentEnabledForPackage(pkg) || (NotificationManagerService.this.mDpm != null && NotificationManagerService.this.mDpm.isActiveAdminWithPolicy(Binder.getCallingUid(), -1))) {
                    return true;
                }
                return false;
            } catch (PackageManager.NameNotFoundException e) {
                return false;
            }
        }

        /* access modifiers changed from: protected */
        public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
            if (DumpUtils.checkDumpAndUsageStatsPermission(NotificationManagerService.this.getContext(), NotificationManagerService.TAG, pw)) {
                DumpFilter filter = DumpFilter.parseFromArguments(args);
                long token = Binder.clearCallingIdentity();
                try {
                    if (filter.stats) {
                        NotificationManagerService.this.dumpJson(pw, filter);
                    } else if (filter.proto) {
                        NotificationManagerService.this.dumpProto(fd, filter);
                    } else if (filter.criticalPriority) {
                        NotificationManagerService.this.dumpNotificationRecords(pw, filter);
                    } else {
                        NotificationManagerService.this.dumpImpl(pw, filter);
                    }
                } finally {
                    Binder.restoreCallingIdentity(token);
                }
            }
        }

        public ComponentName getEffectsSuppressor() {
            if (!NotificationManagerService.this.mEffectsSuppressors.isEmpty()) {
                return (ComponentName) NotificationManagerService.this.mEffectsSuppressors.get(0);
            }
            return null;
        }

        public boolean matchesCallFilter(Bundle extras) {
            enforceSystemOrSystemUI("INotificationManager.matchesCallFilter");
            return NotificationManagerService.this.mZenModeHelper.matchesCallFilter(Binder.getCallingUserHandle(), extras, (ValidateNotificationPeople) NotificationManagerService.this.mRankingHelper.findExtractor(ValidateNotificationPeople.class), NotificationManagerService.MATCHES_CALL_FILTER_CONTACTS_TIMEOUT_MS, 1.0f);
        }

        public boolean isSystemConditionProviderEnabled(String path) {
            enforceSystemOrSystemUI("INotificationManager.isSystemConditionProviderEnabled");
            return NotificationManagerServiceInjector.checkIsXmsfFakeConditionProviderEnabled(NotificationManagerService.this.getContext(), path) || NotificationManagerService.this.mConditionProviders.isSystemProviderEnabled(path);
        }

        public byte[] getBackupPayload(int user) {
            NotificationManagerService.this.checkCallerIsSystem();
            if (NotificationManagerService.DBG) {
                Slog.d(NotificationManagerService.TAG, "getBackupPayload u=" + user);
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                NotificationManagerService.this.writePolicyXml(baos, true, user);
                return baos.toByteArray();
            } catch (IOException e) {
                Slog.w(NotificationManagerService.TAG, "getBackupPayload: error writing payload for user " + user, e);
                return null;
            }
        }

        public void applyRestore(byte[] payload, int user) {
            NotificationManagerService.this.checkCallerIsSystem();
            if (NotificationManagerService.DBG) {
                StringBuilder sb = new StringBuilder();
                sb.append("applyRestore u=");
                sb.append(user);
                sb.append(" payload=");
                sb.append(payload != null ? new String(payload, StandardCharsets.UTF_8) : null);
                Slog.d(NotificationManagerService.TAG, sb.toString());
            }
            if (payload == null) {
                Slog.w(NotificationManagerService.TAG, "applyRestore: no payload to restore for user " + user);
                return;
            }
            try {
                NotificationManagerService.this.readPolicyXml(new ByteArrayInputStream(payload), true, user);
                NotificationManagerService.this.handleSavePolicyFile();
            } catch (IOException | NumberFormatException | XmlPullParserException e) {
                Slog.w(NotificationManagerService.TAG, "applyRestore: error reading payload", e);
            }
        }

        public boolean isNotificationPolicyAccessGranted(String pkg) {
            return checkPolicyAccess(pkg);
        }

        public boolean isNotificationPolicyAccessGrantedForPackage(String pkg) {
            enforceSystemOrSystemUIOrSamePackage(pkg, "request policy access status for another package");
            return checkPolicyAccess(pkg);
        }

        public void setNotificationPolicyAccessGranted(String pkg, boolean granted) throws RemoteException {
            setNotificationPolicyAccessGrantedForUser(pkg, getCallingUserHandle().getIdentifier(), granted);
        }

        public void setNotificationPolicyAccessGrantedForUser(String pkg, int userId, boolean granted) {
            NotificationManagerService.this.checkCallerIsSystemOrShell();
            long identity = Binder.clearCallingIdentity();
            try {
                if (NotificationManagerService.this.mAllowedManagedServicePackages.test(pkg, Integer.valueOf(userId), NotificationManagerService.this.mConditionProviders.getRequiredPermission())) {
                    NotificationManagerService.this.mConditionProviders.setPackageOrComponentEnabled(pkg, userId, true, granted);
                    NotificationManagerService.this.getContext().sendBroadcastAsUser(new Intent("android.app.action.NOTIFICATION_POLICY_ACCESS_GRANTED_CHANGED").setPackage(pkg).addFlags(BroadcastQueueInjector.FLAG_IMMUTABLE), UserHandle.of(userId), (String) null);
                    NotificationManagerService.this.handleSavePolicyFile();
                }
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }

        public NotificationManager.Policy getNotificationPolicy(String pkg) {
            long identity = Binder.clearCallingIdentity();
            try {
                return NotificationManagerService.this.mZenModeHelper.getNotificationPolicy();
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }

        public NotificationManager.Policy getConsolidatedNotificationPolicy() {
            long identity = Binder.clearCallingIdentity();
            try {
                return NotificationManagerService.this.mZenModeHelper.getConsolidatedNotificationPolicy();
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }

        public void setNotificationPolicy(String pkg, NotificationManager.Policy policy) {
            enforcePolicyAccess(pkg, "setNotificationPolicy");
            long identity = Binder.clearCallingIdentity();
            try {
                ApplicationInfo applicationInfo = NotificationManagerService.this.mPackageManager.getApplicationInfo(pkg, 0, UserHandle.getUserId(NotificationManagerService.MY_UID));
                NotificationManager.Policy currPolicy = NotificationManagerService.this.mZenModeHelper.getNotificationPolicy();
                if (applicationInfo.targetSdkVersion < 28) {
                    policy = new NotificationManager.Policy((policy.priorityCategories & -33 & -65 & -129) | (currPolicy.priorityCategories & 32) | (currPolicy.priorityCategories & 64) | (currPolicy.priorityCategories & 128), policy.priorityCallSenders, policy.priorityMessageSenders, policy.suppressedVisualEffects);
                }
                NotificationManager.Policy policy2 = new NotificationManager.Policy(policy.priorityCategories, policy.priorityCallSenders, policy.priorityMessageSenders, NotificationManagerService.this.calculateSuppressedVisualEffects(policy, currPolicy, applicationInfo.targetSdkVersion));
                ZenLog.traceSetNotificationPolicy(pkg, applicationInfo.targetSdkVersion, policy2);
                NotificationManagerService.this.mZenModeHelper.setNotificationPolicy(policy2);
            } catch (RemoteException e) {
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
            Binder.restoreCallingIdentity(identity);
        }

        public List<String> getEnabledNotificationListenerPackages() {
            NotificationManagerService.this.checkCallerIsSystem();
            return NotificationManagerService.this.mListeners.getAllowedPackages(getCallingUserHandle().getIdentifier());
        }

        public List<ComponentName> getEnabledNotificationListeners(int userId) {
            NotificationManagerService.this.checkCallerIsSystem();
            return NotificationManagerService.this.mListeners.getAllowedComponents(userId);
        }

        public ComponentName getAllowedNotificationAssistantForUser(int userId) {
            NotificationManagerService.this.checkCallerIsSystemOrSystemUiOrShell();
            List<ComponentName> allowedComponents = NotificationManagerService.this.mAssistants.getAllowedComponents(userId);
            if (allowedComponents.size() <= 1) {
                return (ComponentName) CollectionUtils.firstOrNull(allowedComponents);
            }
            throw new IllegalStateException("At most one NotificationAssistant: " + allowedComponents.size());
        }

        public ComponentName getAllowedNotificationAssistant() {
            return getAllowedNotificationAssistantForUser(getCallingUserHandle().getIdentifier());
        }

        public boolean isNotificationListenerAccessGranted(ComponentName listener) {
            Preconditions.checkNotNull(listener);
            NotificationManagerService.this.checkCallerIsSystemOrSameApp(listener.getPackageName());
            return NotificationManagerService.this.mListeners.isPackageOrComponentAllowed(listener.flattenToString(), getCallingUserHandle().getIdentifier());
        }

        public boolean isNotificationListenerAccessGrantedForUser(ComponentName listener, int userId) {
            Preconditions.checkNotNull(listener);
            NotificationManagerService.this.checkCallerIsSystem();
            return NotificationManagerService.this.mListeners.isPackageOrComponentAllowed(listener.flattenToString(), userId);
        }

        public boolean isNotificationAssistantAccessGranted(ComponentName assistant) {
            Preconditions.checkNotNull(assistant);
            NotificationManagerService.this.checkCallerIsSystemOrSameApp(assistant.getPackageName());
            return NotificationManagerService.this.mAssistants.isPackageOrComponentAllowed(assistant.flattenToString(), getCallingUserHandle().getIdentifier());
        }

        public void setNotificationListenerAccessGranted(ComponentName listener, boolean granted) throws RemoteException {
            setNotificationListenerAccessGrantedForUser(listener, getCallingUserHandle().getIdentifier(), granted);
        }

        public void setNotificationAssistantAccessGranted(ComponentName assistant, boolean granted) {
            setNotificationAssistantAccessGrantedForUser(assistant, getCallingUserHandle().getIdentifier(), granted);
        }

        public void setNotificationListenerAccessGrantedForUser(ComponentName listener, int userId, boolean granted) {
            Preconditions.checkNotNull(listener);
            NotificationManagerService.this.checkCallerIsSystemOrShell();
            long identity = Binder.clearCallingIdentity();
            try {
                if (NotificationManagerService.this.mAllowedManagedServicePackages.test(listener.getPackageName(), Integer.valueOf(userId), NotificationManagerService.this.mListeners.getRequiredPermission())) {
                    NotificationManagerService.this.mConditionProviders.setPackageOrComponentEnabled(listener.flattenToString(), userId, false, granted);
                    NotificationManagerService.this.mListeners.setPackageOrComponentEnabled(listener.flattenToString(), userId, true, granted);
                    NotificationManagerService.this.getContext().sendBroadcastAsUser(new Intent("android.app.action.NOTIFICATION_POLICY_ACCESS_GRANTED_CHANGED").setPackage(listener.getPackageName()).addFlags(1073741824), UserHandle.of(userId), (String) null);
                    NotificationManagerService.this.handleSavePolicyFile();
                }
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }

        public void setNotificationAssistantAccessGrantedForUser(ComponentName assistant, int userId, boolean granted) {
            NotificationManagerService.this.checkCallerIsSystemOrSystemUiOrShell();
            for (UserInfo ui : NotificationManagerService.this.mUm.getEnabledProfiles(userId)) {
                NotificationManagerService.this.mAssistants.setUserSet(ui.id, true);
            }
            long identity = Binder.clearCallingIdentity();
            try {
                NotificationManagerService.this.setNotificationAssistantAccessGrantedForUserInternal(assistant, userId, granted);
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 9 */
        public void applyEnqueuedAdjustmentFromAssistant(INotificationListener token, Adjustment adjustment) {
            boolean foundEnqueued = false;
            long identity = Binder.clearCallingIdentity();
            try {
                synchronized (NotificationManagerService.mNotificationLock) {
                    NotificationManagerService.this.mAssistants.checkServiceTokenLocked(token);
                    int N = NotificationManagerService.this.mEnqueuedNotifications.size();
                    int i = 0;
                    while (true) {
                        if (i >= N) {
                            break;
                        }
                        NotificationRecord r = NotificationManagerService.this.mEnqueuedNotifications.get(i);
                        if (Objects.equals(adjustment.getKey(), r.getKey()) && Objects.equals(Integer.valueOf(adjustment.getUser()), Integer.valueOf(r.getUserId())) && NotificationManagerService.this.mAssistants.isSameUser(token, r.getUserId())) {
                            NotificationManagerService.this.applyAdjustment(r, adjustment);
                            r.applyAdjustments();
                            r.calculateImportance();
                            foundEnqueued = true;
                            break;
                        }
                        i++;
                    }
                    if (!foundEnqueued) {
                        applyAdjustmentFromAssistant(token, adjustment);
                    }
                }
                Binder.restoreCallingIdentity(identity);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }

        public void applyAdjustmentFromAssistant(INotificationListener token, Adjustment adjustment) {
            List<Adjustment> adjustments = new ArrayList<>();
            adjustments.add(adjustment);
            applyAdjustmentsFromAssistant(token, adjustments);
        }

        /* Debug info: failed to restart local var, previous not found, register: 10 */
        public void applyAdjustmentsFromAssistant(INotificationListener token, List<Adjustment> adjustments) {
            boolean needsSort = false;
            long identity = Binder.clearCallingIdentity();
            try {
                synchronized (NotificationManagerService.mNotificationLock) {
                    NotificationManagerService.this.mAssistants.checkServiceTokenLocked(token);
                    for (Adjustment adjustment : adjustments) {
                        NotificationRecord r = NotificationManagerService.this.mNotificationsByKey.get(adjustment.getKey());
                        if (r != null && NotificationManagerService.this.mAssistants.isSameUser(token, r.getUserId())) {
                            NotificationManagerService.this.applyAdjustment(r, adjustment);
                            if (!adjustment.getSignals().containsKey("key_importance") || adjustment.getSignals().getInt("key_importance") != 0) {
                                needsSort = true;
                            } else {
                                cancelNotificationsFromListener(token, new String[]{r.getKey()});
                            }
                        }
                    }
                }
                if (needsSort) {
                    NotificationManagerService.this.mRankingHandler.requestSort();
                }
                Binder.restoreCallingIdentity(identity);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }

        public void updateNotificationChannelGroupFromPrivilegedListener(INotificationListener token, String pkg, UserHandle user, NotificationChannelGroup group) throws RemoteException {
            Preconditions.checkNotNull(user);
            verifyPrivilegedListener(token, user, false);
            NotificationManagerService.this.createNotificationChannelGroup(pkg, getUidForPackageAndUser(pkg, user), group, false, true);
            NotificationManagerService.this.handleSavePolicyFile();
        }

        public void updateNotificationChannelFromPrivilegedListener(INotificationListener token, String pkg, UserHandle user, NotificationChannel channel) throws RemoteException {
            Preconditions.checkNotNull(channel);
            Preconditions.checkNotNull(pkg);
            Preconditions.checkNotNull(user);
            verifyPrivilegedListener(token, user, false);
            NotificationManagerService.this.updateNotificationChannelInt(pkg, getUidForPackageAndUser(pkg, user), channel, true);
        }

        public ParceledListSlice<NotificationChannel> getNotificationChannelsFromPrivilegedListener(INotificationListener token, String pkg, UserHandle user) throws RemoteException {
            Preconditions.checkNotNull(pkg);
            Preconditions.checkNotNull(user);
            verifyPrivilegedListener(token, user, true);
            return NotificationManagerService.this.mPreferencesHelper.getNotificationChannels(pkg, getUidForPackageAndUser(pkg, user), false);
        }

        public ParceledListSlice<NotificationChannelGroup> getNotificationChannelGroupsFromPrivilegedListener(INotificationListener token, String pkg, UserHandle user) throws RemoteException {
            Preconditions.checkNotNull(pkg);
            Preconditions.checkNotNull(user);
            verifyPrivilegedListener(token, user, true);
            List<NotificationChannelGroup> groups = new ArrayList<>();
            groups.addAll(NotificationManagerService.this.mPreferencesHelper.getNotificationChannelGroups(pkg, getUidForPackageAndUser(pkg, user)));
            return new ParceledListSlice<>(groups);
        }

        public void setPrivateNotificationsAllowed(boolean allow) {
            if (NotificationManagerService.this.getContext().checkCallingPermission("android.permission.CONTROL_KEYGUARD_SECURE_NOTIFICATIONS") != 0) {
                throw new SecurityException("Requires CONTROL_KEYGUARD_SECURE_NOTIFICATIONS permission");
            } else if (allow != NotificationManagerService.this.mLockScreenAllowSecureNotifications) {
                boolean unused = NotificationManagerService.this.mLockScreenAllowSecureNotifications = allow;
                NotificationManagerService.this.handleSavePolicyFile();
            }
        }

        public void buzzBeepBlinkForNotification(String key) {
            NotificationManagerService.this.checkCallerIsSystem();
            synchronized (NotificationManagerService.mNotificationLock) {
                NotificationRecord record = NotificationManagerService.this.mNotificationsByKey.get(key);
                if (record != null) {
                    record.setStatusBarCheckedImportance(true);
                    NotificationManagerService.this.buzzBeepBlinkLocked(record);
                }
            }
        }

        public boolean getPrivateNotificationsAllowed() {
            if (NotificationManagerService.this.getContext().checkCallingPermission("android.permission.CONTROL_KEYGUARD_SECURE_NOTIFICATIONS") == 0) {
                return NotificationManagerService.this.mLockScreenAllowSecureNotifications;
            }
            throw new SecurityException("Requires CONTROL_KEYGUARD_SECURE_NOTIFICATIONS permission");
        }

        public boolean isPackagePaused(String pkg) {
            Preconditions.checkNotNull(pkg);
            NotificationManagerService.this.checkCallerIsSameApp(pkg);
            return ((((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class)).getDistractingPackageRestrictions(pkg, Binder.getCallingUserHandle().getIdentifier()) & 2) != 0) | NotificationManagerService.this.isPackageSuspendedForUser(pkg, Binder.getCallingUid());
        }

        /* Debug info: failed to restart local var, previous not found, register: 5 */
        private void verifyPrivilegedListener(INotificationListener token, UserHandle user, boolean assistantAllowed) {
            ManagedServices.ManagedServiceInfo info;
            synchronized (NotificationManagerService.mNotificationLock) {
                info = NotificationManagerService.this.mListeners.checkServiceTokenLocked(token);
            }
            if (!NotificationManagerService.this.hasCompanionDevice(info)) {
                synchronized (NotificationManagerService.mNotificationLock) {
                    if (assistantAllowed) {
                        if (NotificationManagerService.this.mAssistants.isServiceTokenValidLocked(info.service)) {
                        }
                    }
                    throw new SecurityException(info + " does not have access");
                }
            }
            if (!info.enabledAndUserMatches(user.getIdentifier())) {
                throw new SecurityException(info + " does not have access");
            }
        }

        private int getUidForPackageAndUser(String pkg, UserHandle user) throws RemoteException {
            long identity = Binder.clearCallingIdentity();
            try {
                return NotificationManagerService.this.mPackageManager.getPackageUid(pkg, 0, user.getIdentifier());
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }

        /* JADX WARNING: type inference failed for: r1v1, types: [android.os.Binder] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onShellCommand(java.io.FileDescriptor r9, java.io.FileDescriptor r10, java.io.FileDescriptor r11, java.lang.String[] r12, android.os.ShellCallback r13, android.os.ResultReceiver r14) throws android.os.RemoteException {
            /*
                r8 = this;
                com.android.server.notification.NotificationShellCmd r0 = new com.android.server.notification.NotificationShellCmd
                com.android.server.notification.NotificationManagerService r1 = com.android.server.notification.NotificationManagerService.this
                r0.<init>(r1)
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.notification.NotificationManagerService.AnonymousClass10.onShellCommand(java.io.FileDescriptor, java.io.FileDescriptor, java.io.FileDescriptor, java.lang.String[], android.os.ShellCallback, android.os.ResultReceiver):void");
        }
    };
    /* access modifiers changed from: private */
    public SettingsObserver mSettingsObserver;
    /* access modifiers changed from: private */
    public SnoozeHelper mSnoozeHelper;
    private String mSoundNotificationKey;
    StatusBarManagerInternal mStatusBar;
    final ArrayMap<String, NotificationRecord> mSummaryByGroupKey = new ArrayMap<>();
    boolean mSystemReady;
    final ArrayList<ToastRecord> mToastQueue = new ArrayList<>();
    private IUriGrantsManager mUgm;
    private UriGrantsManagerInternal mUgmInternal;
    /* access modifiers changed from: private */
    public UserManager mUm;
    /* access modifiers changed from: private */
    public NotificationUsageStats mUsageStats;
    private boolean mUseAttentionLight;
    /* access modifiers changed from: private */
    public final ManagedServices.UserProfiles mUserProfiles = new ManagedServices.UserProfiles();
    private String mVibrateNotificationKey;
    Vibrator mVibrator;
    /* access modifiers changed from: private */
    public WindowManagerInternal mWindowManagerInternal;
    protected ZenModeHelper mZenModeHelper;

    private interface FlagChecker {
        boolean apply(int i);
    }

    private static class Archive {
        final ArrayDeque<StatusBarNotification> mBuffer = new ArrayDeque<>(this.mBufferSize);
        final int mBufferSize;

        public Archive(int size) {
            this.mBufferSize = size;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            int N = this.mBuffer.size();
            sb.append("Archive (");
            sb.append(N);
            sb.append(" notification");
            sb.append(N == 1 ? ")" : "s)");
            return sb.toString();
        }

        public void record(StatusBarNotification nr) {
            if (this.mBuffer.size() == this.mBufferSize) {
                this.mBuffer.removeFirst();
            }
            this.mBuffer.addLast(nr.cloneLight());
        }

        public Iterator<StatusBarNotification> descendingIterator() {
            return this.mBuffer.descendingIterator();
        }

        public StatusBarNotification[] getArray(int count) {
            if (count == 0) {
                count = this.mBufferSize;
            }
            StatusBarNotification[] a = new StatusBarNotification[Math.min(count, this.mBuffer.size())];
            Iterator<StatusBarNotification> iter = descendingIterator();
            int i = 0;
            while (iter.hasNext() && i < count) {
                a[i] = iter.next();
                i++;
            }
            return a;
        }
    }

    /* access modifiers changed from: protected */
    public void readDefaultApprovedServices(int userId) {
        String defaultListenerAccess = getContext().getResources().getString(17039728);
        if (defaultListenerAccess != null) {
            for (String whitelisted : defaultListenerAccess.split(":")) {
                for (ComponentName cn : this.mListeners.queryPackageForServices(whitelisted, 786432, userId)) {
                    try {
                        getBinderService().setNotificationListenerAccessGrantedForUser(cn, userId, true);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        String defaultDndAccess = getContext().getResources().getString(17039727);
        if (defaultDndAccess != null) {
            for (String whitelisted2 : defaultDndAccess.split(":")) {
                try {
                    getBinderService().setNotificationPolicyAccessGranted(whitelisted2, true);
                } catch (RemoteException e2) {
                    e2.printStackTrace();
                }
            }
        }
        setDefaultAssistantForUser(userId);
    }

    /* access modifiers changed from: protected */
    public void setDefaultAssistantForUser(int userId) {
        List<ComponentName> validAssistants = new ArrayList<>(this.mAssistants.queryPackageForServices((String) null, 786432, userId));
        List<String> candidateStrs = new ArrayList<>();
        candidateStrs.add(DeviceConfig.getProperty("systemui", "nas_default_service"));
        candidateStrs.add(getContext().getResources().getString(17039721));
        for (String candidateStr : candidateStrs) {
            if (!TextUtils.isEmpty(candidateStr)) {
                ComponentName candidate = ComponentName.unflattenFromString(candidateStr);
                if (candidate == null || !validAssistants.contains(candidate)) {
                    Slog.w(TAG, "Invalid default NAS config is found: " + candidateStr);
                } else {
                    setNotificationAssistantAccessGrantedForUserInternal(candidate, userId, true);
                    Slog.d(TAG, String.format("Set default NAS to be %s in %d", new Object[]{candidateStr, Integer.valueOf(userId)}));
                    return;
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void readPolicyXml(InputStream stream, boolean forRestore, int userId) throws XmlPullParserException, NumberFormatException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(stream, StandardCharsets.UTF_8.name());
        XmlUtils.beginDocument(parser, TAG_NOTIFICATION_POLICY);
        boolean migratedManagedServices = false;
        boolean ineligibleForManagedServices = forRestore && this.mUm.isManagedProfile(userId);
        int outerDepth = parser.getDepth();
        while (XmlUtils.nextElementWithin(parser, outerDepth)) {
            if ("zen".equals(parser.getName())) {
                this.mZenModeHelper.readXml(parser, forRestore, userId);
            } else if ("ranking".equals(parser.getName())) {
                this.mPreferencesHelper.readXml(parser, forRestore, userId);
            }
            if (this.mListeners.getConfig().xmlTag.equals(parser.getName())) {
                if (!ineligibleForManagedServices) {
                    this.mListeners.readXml(parser, this.mAllowedManagedServicePackages, forRestore, userId);
                    migratedManagedServices = true;
                }
            } else if (this.mAssistants.getConfig().xmlTag.equals(parser.getName())) {
                if (!ineligibleForManagedServices) {
                    this.mAssistants.readXml(parser, this.mAllowedManagedServicePackages, forRestore, userId);
                    migratedManagedServices = true;
                }
            } else if (this.mConditionProviders.getConfig().xmlTag.equals(parser.getName())) {
                if (!ineligibleForManagedServices) {
                    this.mConditionProviders.readXml(parser, this.mAllowedManagedServicePackages, forRestore, userId);
                    migratedManagedServices = true;
                }
            }
            if (LOCKSCREEN_ALLOW_SECURE_NOTIFICATIONS_TAG.equals(parser.getName()) && (!forRestore || userId == 0)) {
                this.mLockScreenAllowSecureNotifications = safeBoolean(parser.getAttributeValue((String) null, LOCKSCREEN_ALLOW_SECURE_NOTIFICATIONS_VALUE), true);
            }
        }
        if (!migratedManagedServices) {
            this.mListeners.migrateToXml();
            this.mAssistants.migrateToXml();
            this.mConditionProviders.migrateToXml();
            handleSavePolicyFile();
        }
        this.mAssistants.resetDefaultAssistantsIfNecessary();
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void loadPolicyFile() {
        if (DBG) {
            Slog.d(TAG, "loadPolicyFile");
        }
        synchronized (this.mPolicyFile) {
            InputStream infile = null;
            try {
                infile = this.mPolicyFile.openRead();
                readPolicyXml(infile, false, -1);
                IoUtils.closeQuietly(infile);
            } catch (FileNotFoundException e) {
                readDefaultApprovedServices(0);
                IoUtils.closeQuietly(infile);
            } catch (IOException e2) {
                Log.wtf(TAG, "Unable to read notification policy", e2);
                IoUtils.closeQuietly(infile);
            } catch (NumberFormatException e3) {
                Log.wtf(TAG, "Unable to parse notification policy", e3);
                IoUtils.closeQuietly(infile);
            } catch (XmlPullParserException e4) {
                try {
                    Log.wtf(TAG, "Unable to parse notification policy", e4);
                    IoUtils.closeQuietly(infile);
                } catch (Throwable th) {
                    IoUtils.closeQuietly(infile);
                    throw th;
                }
            }
        }
        return;
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void handleSavePolicyFile() {
        IoThread.getHandler().post(new Runnable() {
            public final void run() {
                NotificationManagerService.this.lambda$handleSavePolicyFile$0$NotificationManagerService();
            }
        });
    }

    public /* synthetic */ void lambda$handleSavePolicyFile$0$NotificationManagerService() {
        if (DBG) {
            Slog.d(TAG, "handleSavePolicyFile");
        }
        synchronized (this.mPolicyFile) {
            try {
                FileOutputStream stream = this.mPolicyFile.startWrite();
                try {
                    writePolicyXml(stream, false, -1);
                    this.mPolicyFile.finishWrite(stream);
                } catch (IOException e) {
                    Slog.w(TAG, "Failed to save policy file, restoring backup", e);
                    this.mPolicyFile.failWrite(stream);
                }
            } catch (IOException e2) {
                Slog.w(TAG, "Failed to save policy file", e2);
                return;
            }
        }
        BackupManager.dataChanged(getContext().getPackageName());
    }

    /* access modifiers changed from: private */
    public void writePolicyXml(OutputStream stream, boolean forBackup, int userId) throws IOException {
        XmlSerializer out = new FastXmlSerializer();
        out.setOutput(stream, StandardCharsets.UTF_8.name());
        out.startDocument((String) null, true);
        out.startTag((String) null, TAG_NOTIFICATION_POLICY);
        out.attribute((String) null, ATTR_VERSION, Integer.toString(1));
        this.mZenModeHelper.writeXml(out, forBackup, (Integer) null, userId);
        this.mPreferencesHelper.writeXml(out, forBackup, userId);
        this.mListeners.writeXml(out, forBackup, userId);
        this.mAssistants.writeXml(out, forBackup, userId);
        this.mConditionProviders.writeXml(out, forBackup, userId);
        if (!forBackup || userId == 0) {
            writeSecureNotificationsPolicy(out);
        }
        out.endTag((String) null, TAG_NOTIFICATION_POLICY);
        out.endDocument();
    }

    private static final class ToastRecord {
        final ITransientNotification callback;
        int displayId;
        int duration;
        final int pid;
        final String pkg;
        Binder token;

        ToastRecord(int pid2, String pkg2, ITransientNotification callback2, int duration2, Binder token2, int displayId2) {
            this.pid = pid2;
            this.pkg = pkg2;
            this.callback = callback2;
            this.duration = duration2;
            this.token = token2;
            this.displayId = displayId2;
        }

        /* access modifiers changed from: package-private */
        public void update(int duration2) {
            this.duration = duration2;
        }

        /* access modifiers changed from: package-private */
        public void dump(PrintWriter pw, String prefix, DumpFilter filter) {
            if (filter == null || filter.matches(this.pkg)) {
                pw.println(prefix + this);
            }
        }

        public final String toString() {
            return "ToastRecord{" + Integer.toHexString(System.identityHashCode(this)) + " pkg=" + this.pkg + " callback=" + this.callback + " duration=" + this.duration;
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void logSmartSuggestionsVisible(NotificationRecord r, int notificationLocation) {
        if ((r.getNumSmartRepliesAdded() > 0 || r.getNumSmartActionsAdded() > 0) && !r.hasSeenSmartReplies()) {
            r.setSeenSmartReplies(true);
            this.mMetricsLogger.write(r.getLogMaker().setCategory(1382).addTaggedData(1384, Integer.valueOf(r.getNumSmartRepliesAdded())).addTaggedData(1599, Integer.valueOf(r.getNumSmartActionsAdded())).addTaggedData(1600, Integer.valueOf(r.getSuggestionsGeneratedByAssistant() ? 1 : 0)).addTaggedData(1629, Integer.valueOf(notificationLocation)).addTaggedData(1647, Integer.valueOf(r.getEditChoicesBeforeSending() ? 1 : 0)));
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mNotificationLock"})
    public void clearSoundLocked() {
        this.mSoundNotificationKey = null;
        long identity = Binder.clearCallingIdentity();
        try {
            IRingtonePlayer player = this.mAudioManager.getRingtonePlayer();
            if (player != null) {
                player.stopAsync();
            }
        } catch (RemoteException e) {
        } catch (Throwable player2) {
            Binder.restoreCallingIdentity(identity);
            throw player2;
        }
        Binder.restoreCallingIdentity(identity);
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mNotificationLock"})
    public void clearVibrateLocked() {
        this.mVibrateNotificationKey = null;
        long identity = Binder.clearCallingIdentity();
        try {
            this.mVibrator.cancel();
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mNotificationLock"})
    public void clearLightsLocked() {
        this.mLights.clear();
        updateLightsLocked();
    }

    private final class SettingsObserver extends ContentObserver {
        private final Uri NOTIFICATION_BADGING_URI = Settings.Secure.getUriFor("notification_badging");
        private final Uri NOTIFICATION_BUBBLES_URI = Settings.Secure.getUriFor("notification_bubbles");
        private final Uri NOTIFICATION_LIGHT_PULSE_URI = Settings.System.getUriFor("notification_light_pulse");
        private final Uri NOTIFICATION_RATE_LIMIT_URI = Settings.Global.getUriFor("max_notification_enqueue_rate");

        SettingsObserver(Handler handler) {
            super(handler);
        }

        /* access modifiers changed from: package-private */
        public void observe() {
            ContentResolver resolver = NotificationManagerService.this.getContext().getContentResolver();
            resolver.registerContentObserver(this.NOTIFICATION_BADGING_URI, false, this, -1);
            resolver.registerContentObserver(this.NOTIFICATION_LIGHT_PULSE_URI, false, this, -1);
            resolver.registerContentObserver(this.NOTIFICATION_RATE_LIMIT_URI, false, this, -1);
            resolver.registerContentObserver(this.NOTIFICATION_BUBBLES_URI, false, this, -1);
            update((Uri) null);
        }

        public void onChange(boolean selfChange, Uri uri) {
            update(uri);
        }

        public void update(Uri uri) {
            ContentResolver resolver = NotificationManagerService.this.getContext().getContentResolver();
            if (uri == null || this.NOTIFICATION_LIGHT_PULSE_URI.equals(uri)) {
                boolean z = false;
                if (Settings.System.getIntForUser(resolver, "notification_light_pulse", 0, -2) != 0) {
                    z = true;
                }
                boolean pulseEnabled = z;
                if (NotificationManagerService.this.mNotificationPulseEnabled != pulseEnabled) {
                    NotificationManagerService notificationManagerService = NotificationManagerService.this;
                    notificationManagerService.mNotificationPulseEnabled = pulseEnabled;
                    notificationManagerService.updateNotificationPulse();
                }
            }
            if (uri == null || this.NOTIFICATION_RATE_LIMIT_URI.equals(uri)) {
                NotificationManagerService notificationManagerService2 = NotificationManagerService.this;
                float unused = notificationManagerService2.mMaxPackageEnqueueRate = Settings.Global.getFloat(resolver, "max_notification_enqueue_rate", notificationManagerService2.mMaxPackageEnqueueRate);
            }
            if (uri == null || this.NOTIFICATION_BADGING_URI.equals(uri)) {
                NotificationManagerService.this.mPreferencesHelper.updateBadgingEnabled();
            }
            if (uri == null || this.NOTIFICATION_BUBBLES_URI.equals(uri)) {
                NotificationManagerService.this.mPreferencesHelper.updateBubblesEnabled();
            }
        }
    }

    static long[] getLongArray(Resources r, int resid, int maxlen, long[] def) {
        int[] ar = r.getIntArray(resid);
        if (ar == null) {
            return def;
        }
        int len = ar.length > maxlen ? maxlen : ar.length;
        long[] out = new long[len];
        for (int i = 0; i < len; i++) {
            out[i] = (long) ar[i];
        }
        return out;
    }

    /* JADX WARNING: type inference failed for: r0v10, types: [com.android.server.notification.NotificationManagerService$10, android.os.IBinder] */
    public NotificationManagerService(Context context) {
        super(context);
        Notification.processWhitelistToken = WHITELIST_TOKEN;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setAudioManager(AudioManager audioMananger) {
        this.mAudioManager = audioMananger;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setHints(int hints) {
        this.mListenerHints = hints;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setVibrator(Vibrator vibrator) {
        this.mVibrator = vibrator;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setLights(Light light) {
        this.mNotificationLight = light;
        this.mAttentionLight = light;
        this.mNotificationPulseEnabled = true;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setScreenOn(boolean on) {
        this.mScreenOn = on;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public int getNotificationRecordCount() {
        int count;
        synchronized (mNotificationLock) {
            count = this.mNotificationList.size() + this.mNotificationsByKey.size() + this.mSummaryByGroupKey.size() + this.mEnqueuedNotifications.size();
            Iterator<NotificationRecord> it = this.mNotificationList.iterator();
            while (it.hasNext()) {
                NotificationRecord posted = it.next();
                if (this.mNotificationsByKey.containsKey(posted.getKey())) {
                    count--;
                }
                if (posted.sbn.isGroup() && posted.getNotification().isGroupSummary()) {
                    count--;
                }
            }
        }
        return count;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void clearNotifications() {
        this.mEnqueuedNotifications.clear();
        this.mNotificationList.clear();
        this.mNotificationsByKey.clear();
        this.mSummaryByGroupKey.clear();
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void addNotification(NotificationRecord r) {
        this.mNotificationList.add(r);
        this.mNotificationsByKey.put(r.sbn.getKey(), r);
        if (r.sbn.isGroup()) {
            this.mSummaryByGroupKey.put(r.getGroupKey(), r);
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void addEnqueuedNotification(NotificationRecord r) {
        this.mEnqueuedNotifications.add(r);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public NotificationRecord getNotificationRecord(String key) {
        return this.mNotificationsByKey.get(key);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setSystemReady(boolean systemReady) {
        this.mSystemReady = systemReady;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setHandler(WorkerHandler handler) {
        mHandler = handler;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setFallbackVibrationPattern(long[] vibrationPattern) {
        this.mFallbackVibrationPattern = vibrationPattern;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setPackageManager(IPackageManager packageManager) {
        this.mPackageManager = packageManager;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setRankingHelper(RankingHelper rankingHelper) {
        this.mRankingHelper = rankingHelper;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setPreferencesHelper(PreferencesHelper prefHelper) {
        this.mPreferencesHelper = prefHelper;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setRankingHandler(RankingHandler rankingHandler) {
        this.mRankingHandler = rankingHandler;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setZenHelper(ZenModeHelper zenHelper) {
        this.mZenModeHelper = zenHelper;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setIsAutomotive(boolean isAutomotive) {
        this.mIsAutomotive = isAutomotive;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setNotificationEffectsEnabledForAutomotive(boolean isEnabled) {
        this.mNotificationEffectsEnabledForAutomotive = isEnabled;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setIsTelevision(boolean isTelevision) {
        this.mIsTelevision = isTelevision;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setUsageStats(NotificationUsageStats us) {
        this.mUsageStats = us;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setAccessibilityManager(AccessibilityManager am) {
        this.mAccessibilityManager = am;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void init(Looper looper, IPackageManager packageManager, PackageManager packageManagerClient, LightsManager lightsManager, NotificationListeners notificationListeners, NotificationAssistants notificationAssistants, ConditionProviders conditionProviders, ICompanionDeviceManager companionManager, SnoozeHelper snoozeHelper, NotificationUsageStats usageStats, AtomicFile policyFile, ActivityManager activityManager, GroupHelper groupHelper, IActivityManager am, UsageStatsManagerInternal appUsageStats, DevicePolicyManagerInternal dpm, IUriGrantsManager ugm, UriGrantsManagerInternal ugmInternal, AppOpsManager appOps, UserManager userManager) {
        String[] extractorNames;
        LightsManager lightsManager2 = lightsManager;
        Resources resources = getContext().getResources();
        this.mMaxPackageEnqueueRate = Settings.Global.getFloat(getContext().getContentResolver(), "max_notification_enqueue_rate", DEFAULT_MAX_NOTIFICATION_ENQUEUE_RATE);
        this.mAccessibilityManager = (AccessibilityManager) getContext().getSystemService("accessibility");
        this.mAm = am;
        this.mUgm = ugm;
        this.mUgmInternal = ugmInternal;
        this.mPackageManager = packageManager;
        this.mPackageManagerClient = packageManagerClient;
        this.mAppOps = appOps;
        this.mVibrator = (Vibrator) getContext().getSystemService("vibrator");
        this.mAppUsageStats = appUsageStats;
        this.mAlarmManager = (AlarmManager) getContext().getSystemService("alarm");
        this.mCompanionManager = companionManager;
        this.mActivityManager = activityManager;
        this.mDeviceIdleController = IDeviceIdleController.Stub.asInterface(ServiceManager.getService("deviceidle"));
        this.mDpm = dpm;
        this.mUm = userManager;
        mHandler = new WorkerHandler(looper);
        this.mRankingThread.start();
        try {
            extractorNames = resources.getStringArray(17236050);
        } catch (Resources.NotFoundException e) {
            Resources.NotFoundException notFoundException = e;
            extractorNames = new String[0];
        }
        this.mUsageStats = usageStats;
        this.mMetricsLogger = new MetricsLogger();
        this.mRankingHandler = new RankingHandlerWorker(this.mRankingThread.getLooper());
        this.mConditionProviders = conditionProviders;
        this.mZenModeHelper = new ZenModeHelper(getContext(), mHandler.getLooper(), this.mConditionProviders);
        this.mZenModeHelper.addCallback(new ZenModeHelper.Callback() {
            public void onConfigChanged() {
                NotificationManagerService.this.handleSavePolicyFile();
            }

            /* access modifiers changed from: package-private */
            public void onZenModeChanged() {
                NotificationManagerService.this.sendRegisteredOnlyBroadcast("android.app.action.INTERRUPTION_FILTER_CHANGED");
                NotificationManagerService.this.getContext().sendBroadcastAsUser(new Intent("android.app.action.INTERRUPTION_FILTER_CHANGED_INTERNAL").addFlags(BroadcastQueueInjector.FLAG_IMMUTABLE), UserHandle.ALL, "android.permission.MANAGE_NOTIFICATIONS");
                synchronized (NotificationManagerService.mNotificationLock) {
                    NotificationManagerService.this.updateInterruptionFilterLocked();
                }
                NotificationManagerService.this.mRankingHandler.requestSort();
            }

            /* access modifiers changed from: package-private */
            public void onPolicyChanged() {
                NotificationManagerService.this.sendRegisteredOnlyBroadcast("android.app.action.NOTIFICATION_POLICY_CHANGED");
                NotificationManagerService.this.mRankingHandler.requestSort();
            }
        });
        this.mPreferencesHelper = new PreferencesHelper(getContext(), this.mPackageManagerClient, this.mRankingHandler, this.mZenModeHelper);
        this.mRankingHelper = new RankingHelper(getContext(), this.mRankingHandler, this.mPreferencesHelper, this.mZenModeHelper, this.mUsageStats, extractorNames);
        this.mSnoozeHelper = snoozeHelper;
        this.mGroupHelper = groupHelper;
        this.mListeners = notificationListeners;
        this.mAssistants = notificationAssistants;
        this.mAllowedManagedServicePackages = new TriPredicate() {
            public final boolean test(Object obj, Object obj2, Object obj3) {
                return NotificationManagerService.this.canUseManagedServices((String) obj, (Integer) obj2, (String) obj3);
            }
        };
        this.mPolicyFile = policyFile;
        loadPolicyFile();
        String[] strArr = extractorNames;
        this.mStatusBar = (StatusBarManagerInternal) getLocalService(StatusBarManagerInternal.class);
        StatusBarManagerInternal statusBarManagerInternal = this.mStatusBar;
        if (statusBarManagerInternal != null) {
            statusBarManagerInternal.setNotificationDelegate(this.mNotificationDelegate);
        }
        this.mNotificationLight = lightsManager2.getLight(4);
        this.mAttentionLight = lightsManager2.getLight(5);
        this.mFallbackVibrationPattern = getLongArray(resources, 17236049, 17, DEFAULT_VIBRATE_PATTERN);
        this.mInCallNotificationUri = Uri.parse("file://" + resources.getString(17039767));
        this.mInCallNotificationAudioAttributes = new AudioAttributes.Builder().setContentType(4).setUsage(2).build();
        this.mInCallNotificationVolume = resources.getFloat(17105061);
        this.mUseAttentionLight = resources.getBoolean(17891561);
        this.mHasLight = resources.getBoolean(17891471);
        boolean z = true;
        if (Settings.Global.getInt(getContext().getContentResolver(), "device_provisioned", 0) == 0) {
            this.mDisableNotificationEffects = true;
        }
        this.mZenModeHelper.initZenMode();
        this.mInterruptionFilter = this.mZenModeHelper.getZenModeListenerInterruptionFilter();
        this.mUserProfiles.updateCache(getContext());
        listenForCallState();
        this.mSettingsObserver = new SettingsObserver(mHandler);
        this.mArchive = new Archive(resources.getInteger(17694863));
        if (!this.mPackageManagerClient.hasSystemFeature("android.software.leanback") && !this.mPackageManagerClient.hasSystemFeature("android.hardware.type.television")) {
            z = false;
        }
        this.mIsTelevision = z;
        this.mIsAutomotive = this.mPackageManagerClient.hasSystemFeature("android.hardware.type.automotive", 0);
        this.mNotificationEffectsEnabledForAutomotive = resources.getBoolean(17891451);
        this.mPreferencesHelper.lockChannelsForOEM(getContext().getResources().getStringArray(17236048));
        this.mZenModeHelper.setPriorityOnlyDndExemptPackages(getContext().getResources().getStringArray(17236053));
    }

    public void onStart() {
        SnoozeHelper snoozeHelper = new SnoozeHelper(getContext(), new SnoozeHelper.Callback() {
            public void repost(int userId, NotificationRecord r) {
                try {
                    if (NotificationManagerService.DBG) {
                        Slog.d(NotificationManagerService.TAG, "Reposting " + r.getKey());
                    }
                    NotificationManagerService.this.enqueueNotificationInternal(r.sbn.getPackageName(), r.sbn.getOpPkg(), r.sbn.getUid(), r.sbn.getInitialPid(), r.sbn.getTag(), r.sbn.getId(), r.sbn.getNotification(), userId);
                } catch (Exception e) {
                    Slog.e(NotificationManagerService.TAG, "Cannot un-snooze notification", e);
                }
            }
        }, this.mUserProfiles);
        File systemDir = new File(Environment.getDataDirectory(), "system");
        NotificationListeners notificationListeners = r0;
        NotificationListeners notificationListeners2 = new NotificationListeners(AppGlobals.getPackageManager());
        NotificationAssistants notificationAssistants = r0;
        NotificationAssistants notificationAssistants2 = new NotificationAssistants(getContext(), mNotificationLock, this.mUserProfiles, AppGlobals.getPackageManager());
        File systemDir2 = systemDir;
        ConditionProviders conditionProviders = r0;
        ConditionProviders conditionProviders2 = new ConditionProviders(getContext(), this.mUserProfiles, AppGlobals.getPackageManager());
        NotificationUsageStats notificationUsageStats = r0;
        NotificationUsageStats notificationUsageStats2 = new NotificationUsageStats(getContext());
        AtomicFile atomicFile = r0;
        AtomicFile atomicFile2 = new AtomicFile(new File(systemDir2, "notification_policy.xml"), TAG_NOTIFICATION_POLICY);
        init(Looper.myLooper(), AppGlobals.getPackageManager(), getContext().getPackageManager(), (LightsManager) getLocalService(LightsManager.class), notificationListeners, notificationAssistants, conditionProviders, (ICompanionDeviceManager) null, snoozeHelper, notificationUsageStats, atomicFile, (ActivityManager) getContext().getSystemService("activity"), getGroupHelper(), ActivityManager.getService(), (UsageStatsManagerInternal) LocalServices.getService(UsageStatsManagerInternal.class), (DevicePolicyManagerInternal) LocalServices.getService(DevicePolicyManagerInternal.class), UriGrantsManager.getService(), (UriGrantsManagerInternal) LocalServices.getService(UriGrantsManagerInternal.class), (AppOpsManager) getContext().getSystemService("appops"), (UserManager) getContext().getSystemService(UserManager.class));
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.SCREEN_ON");
        filter.addAction("android.intent.action.SCREEN_OFF");
        filter.addAction("android.intent.action.PHONE_STATE");
        filter.addAction("android.intent.action.USER_PRESENT");
        filter.addAction("android.intent.action.USER_STOPPED");
        filter.addAction("android.intent.action.USER_SWITCHED");
        filter.addAction("android.intent.action.USER_ADDED");
        filter.addAction("android.intent.action.USER_REMOVED");
        filter.addAction("android.intent.action.USER_UNLOCKED");
        filter.addAction("android.intent.action.MANAGED_PROFILE_UNAVAILABLE");
        getContext().registerReceiverAsUser(this.mIntentReceiver, UserHandle.ALL, filter, (String) null, (Handler) null);
        IntentFilter pkgFilter = new IntentFilter();
        pkgFilter.addAction("android.intent.action.PACKAGE_ADDED");
        pkgFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        pkgFilter.addAction("android.intent.action.PACKAGE_CHANGED");
        pkgFilter.addAction("android.intent.action.PACKAGE_RESTARTED");
        pkgFilter.addAction("android.intent.action.QUERY_PACKAGE_RESTART");
        pkgFilter.addDataScheme(com.android.server.pm.Settings.ATTR_PACKAGE);
        getContext().registerReceiverAsUser(this.mPackageIntentReceiver, UserHandle.ALL, pkgFilter, (String) null, (Handler) null);
        IntentFilter suspendedPkgFilter = new IntentFilter();
        suspendedPkgFilter.addAction("android.intent.action.PACKAGES_SUSPENDED");
        suspendedPkgFilter.addAction("android.intent.action.PACKAGES_UNSUSPENDED");
        suspendedPkgFilter.addAction("android.intent.action.DISTRACTING_PACKAGES_CHANGED");
        getContext().registerReceiverAsUser(this.mPackageIntentReceiver, UserHandle.ALL, suspendedPkgFilter, (String) null, (Handler) null);
        IntentFilter sdFilter = new IntentFilter("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE");
        getContext().registerReceiverAsUser(this.mPackageIntentReceiver, UserHandle.ALL, sdFilter, (String) null, (Handler) null);
        IntentFilter timeoutFilter = new IntentFilter(ACTION_NOTIFICATION_TIMEOUT);
        timeoutFilter.addDataScheme(SCHEME_TIMEOUT);
        getContext().registerReceiver(this.mNotificationTimeoutReceiver, timeoutFilter);
        getContext().registerReceiver(this.mRestoreReceiver, new IntentFilter("android.os.action.SETTING_RESTORED"));
        getContext().registerReceiver(this.mLocaleChangeReceiver, new IntentFilter("android.intent.action.LOCALE_CHANGED"));
        publishBinderService("notification", this.mService, false, 5);
        publishLocalService(NotificationManagerInternal.class, this.mInternalService);
    }

    private void registerDeviceConfigChange() {
        DeviceConfig.addOnPropertiesChangedListener("systemui", getContext().getMainExecutor(), new DeviceConfig.OnPropertiesChangedListener() {
            public final void onPropertiesChanged(DeviceConfig.Properties properties) {
                NotificationManagerService.this.lambda$registerDeviceConfigChange$1$NotificationManagerService(properties);
            }
        });
    }

    public /* synthetic */ void lambda$registerDeviceConfigChange$1$NotificationManagerService(DeviceConfig.Properties properties) {
        if ("systemui".equals(properties.getNamespace()) && properties.getKeyset().contains("nas_default_service")) {
            this.mAssistants.resetDefaultAssistantsIfNecessary();
        }
    }

    private GroupHelper getGroupHelper() {
        this.mAutoGroupAtCount = getContext().getResources().getInteger(17694741);
        return new GroupHelper(this.mAutoGroupAtCount, new GroupHelper.Callback() {
            public void addAutoGroup(String key) {
                synchronized (NotificationManagerService.mNotificationLock) {
                    NotificationManagerService.this.addAutogroupKeyLocked(key);
                }
            }

            public void removeAutoGroup(String key) {
                synchronized (NotificationManagerService.mNotificationLock) {
                    NotificationManagerService.this.removeAutogroupKeyLocked(key);
                }
            }

            public void addAutoGroupSummary(int userId, String pkg, String triggeringKey) {
                NotificationManagerService.this.createAutoGroupSummary(userId, pkg, triggeringKey);
            }

            public void removeAutoGroupSummary(int userId, String pkg) {
                synchronized (NotificationManagerService.mNotificationLock) {
                    NotificationManagerService.this.clearAutogroupSummaryLocked(userId, pkg);
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void sendRegisteredOnlyBroadcast(String action) {
        Intent intent = new Intent(action);
        getContext().sendBroadcastAsUser(intent.addFlags(1073741824), UserHandle.ALL, (String) null);
        intent.setFlags(0);
        for (String pkg : this.mConditionProviders.getAllowedPackages()) {
            intent.setPackage(pkg);
            getContext().sendBroadcastAsUser(intent, UserHandle.ALL);
        }
    }

    public void onBootPhase(int phase) {
        if (phase == 500) {
            this.mSystemReady = true;
            this.mAudioManager = (AudioManager) getContext().getSystemService("audio");
            this.mAudioManagerInternal = (AudioManagerInternal) getLocalService(AudioManagerInternal.class);
            this.mWindowManagerInternal = (WindowManagerInternal) LocalServices.getService(WindowManagerInternal.class);
            this.mZenModeHelper.onSystemReady();
            this.mRoleObserver = new RoleObserver((RoleManager) getContext().getSystemService(RoleManager.class), this.mPackageManager, getContext().getMainExecutor());
            this.mRoleObserver.init();
        } else if (phase == 600) {
            this.mSettingsObserver.observe();
            this.mListeners.onBootPhaseAppsCanStart();
            this.mAssistants.onBootPhaseAppsCanStart();
            this.mConditionProviders.onBootPhaseAppsCanStart();
            registerDeviceConfigChange();
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mNotificationLock"})
    public void updateListenerHintsLocked() {
        int hints = calculateHints();
        int i = this.mListenerHints;
        if (hints != i) {
            ZenLog.traceListenerHintsChanged(i, hints, this.mEffectsSuppressors.size());
            this.mListenerHints = hints;
            scheduleListenerHintsChanged(hints);
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mNotificationLock"})
    public void updateEffectsSuppressorLocked() {
        long updatedSuppressedEffects = calculateSuppressedEffects();
        if (updatedSuppressedEffects != this.mZenModeHelper.getSuppressedEffects()) {
            List<ComponentName> suppressors = getSuppressors();
            ZenLog.traceEffectsSuppressorChanged(this.mEffectsSuppressors, suppressors, updatedSuppressedEffects);
            this.mEffectsSuppressors = suppressors;
            this.mZenModeHelper.setSuppressedEffects(updatedSuppressedEffects);
            sendRegisteredOnlyBroadcast("android.os.action.ACTION_EFFECTS_SUPPRESSOR_CHANGED");
        }
    }

    /* access modifiers changed from: private */
    public void exitIdle() {
        try {
            if (this.mDeviceIdleController != null) {
                this.mDeviceIdleController.exitIdle("notification interaction");
            }
        } catch (RemoteException e) {
        }
    }

    /* access modifiers changed from: private */
    public void updateNotificationChannelInt(String pkg, int uid, NotificationChannel channel, boolean fromListener) {
        String str = pkg;
        int i = uid;
        NotificationChannel notificationChannel = channel;
        if (channel.getImportance() == 0) {
            cancelAllNotificationsInt(MY_UID, MY_PID, pkg, channel.getId(), 0, 0, true, UserHandle.getUserId(uid), 17, (ManagedServices.ManagedServiceInfo) null);
            if (isUidSystemOrPhone(i)) {
                IntArray profileIds = this.mUserProfiles.getCurrentProfileIds();
                int N = profileIds.size();
                int i2 = 0;
                while (i2 < N) {
                    cancelAllNotificationsInt(MY_UID, MY_PID, pkg, channel.getId(), 0, 0, true, profileIds.get(i2), 17, (ManagedServices.ManagedServiceInfo) null);
                    i2++;
                    N = N;
                }
                int i3 = i2;
                int i4 = N;
            }
        }
        NotificationChannel preUpdate = this.mPreferencesHelper.getNotificationChannel(str, i, channel.getId(), true);
        this.mPreferencesHelper.updateNotificationChannel(str, i, notificationChannel, true);
        maybeNotifyChannelOwner(str, i, preUpdate, notificationChannel);
        if (!fromListener) {
            this.mListeners.notifyNotificationChannelChanged(str, UserHandle.getUserHandleForUid(uid), this.mPreferencesHelper.getNotificationChannel(str, i, channel.getId(), false), 2);
        }
        handleSavePolicyFile();
    }

    private void maybeNotifyChannelOwner(String pkg, int uid, NotificationChannel preUpdate, NotificationChannel update) {
        try {
            if ((preUpdate.getImportance() == 0 && update.getImportance() != 0) || (preUpdate.getImportance() != 0 && update.getImportance() == 0)) {
                getContext().sendBroadcastAsUser(new Intent("android.app.action.NOTIFICATION_CHANNEL_BLOCK_STATE_CHANGED").putExtra("android.app.extra.NOTIFICATION_CHANNEL_ID", update.getId()).putExtra("android.app.extra.BLOCKED_STATE", update.getImportance() == 0).addFlags(268435456).setPackage(pkg), UserHandle.of(UserHandle.getUserId(uid)), (String) null);
            }
        } catch (SecurityException e) {
            Slog.w(TAG, "Can't notify app about channel change", e);
        }
    }

    /* access modifiers changed from: private */
    public void createNotificationChannelGroup(String pkg, int uid, NotificationChannelGroup group, boolean fromApp, boolean fromListener) {
        Preconditions.checkNotNull(group);
        Preconditions.checkNotNull(pkg);
        NotificationChannelGroup preUpdate = this.mPreferencesHelper.getNotificationChannelGroup(group.getId(), pkg, uid);
        this.mPreferencesHelper.createNotificationChannelGroup(pkg, uid, group, fromApp);
        if (!fromApp) {
            maybeNotifyChannelGroupOwner(pkg, uid, preUpdate, group);
        }
        if (!fromListener) {
            this.mListeners.notifyNotificationChannelGroupChanged(pkg, UserHandle.of(UserHandle.getCallingUserId()), group, 1);
        }
    }

    private void maybeNotifyChannelGroupOwner(String pkg, int uid, NotificationChannelGroup preUpdate, NotificationChannelGroup update) {
        try {
            if (preUpdate.isBlocked() != update.isBlocked()) {
                getContext().sendBroadcastAsUser(new Intent("android.app.action.NOTIFICATION_CHANNEL_GROUP_BLOCK_STATE_CHANGED").putExtra("android.app.extra.NOTIFICATION_CHANNEL_GROUP_ID", update.getId()).putExtra("android.app.extra.BLOCKED_STATE", update.isBlocked()).addFlags(268435456).setPackage(pkg), UserHandle.of(UserHandle.getUserId(uid)), (String) null);
            }
        } catch (SecurityException e) {
            Slog.w(TAG, "Can't notify app about group change", e);
        }
    }

    private ArrayList<ComponentName> getSuppressors() {
        ArrayList<ComponentName> names = new ArrayList<>();
        for (int i = this.mListenersDisablingEffects.size() - 1; i >= 0; i--) {
            Iterator<ComponentName> it = this.mListenersDisablingEffects.valueAt(i).iterator();
            while (it.hasNext()) {
                names.add(it.next());
            }
        }
        return names;
    }

    /* access modifiers changed from: private */
    public boolean removeDisabledHints(ManagedServices.ManagedServiceInfo info) {
        return removeDisabledHints(info, 0);
    }

    /* access modifiers changed from: private */
    public boolean removeDisabledHints(ManagedServices.ManagedServiceInfo info, int hints) {
        boolean removed = false;
        for (int i = this.mListenersDisablingEffects.size() - 1; i >= 0; i--) {
            int hint = this.mListenersDisablingEffects.keyAt(i);
            ArraySet<ComponentName> listeners = this.mListenersDisablingEffects.valueAt(i);
            if (hints == 0 || (hint & hints) == hint) {
                removed |= listeners.remove(info.component);
            }
        }
        return removed;
    }

    /* access modifiers changed from: private */
    public void addDisabledHints(ManagedServices.ManagedServiceInfo info, int hints) {
        if ((hints & 1) != 0) {
            addDisabledHint(info, 1);
        }
        if ((hints & 2) != 0) {
            addDisabledHint(info, 2);
        }
        if ((hints & 4) != 0) {
            addDisabledHint(info, 4);
        }
    }

    private void addDisabledHint(ManagedServices.ManagedServiceInfo info, int hint) {
        if (this.mListenersDisablingEffects.indexOfKey(hint) < 0) {
            this.mListenersDisablingEffects.put(hint, new ArraySet());
        }
        this.mListenersDisablingEffects.get(hint).add(info.component);
    }

    private int calculateHints() {
        int hints = 0;
        for (int i = this.mListenersDisablingEffects.size() - 1; i >= 0; i--) {
            int hint = this.mListenersDisablingEffects.keyAt(i);
            if (!this.mListenersDisablingEffects.valueAt(i).isEmpty()) {
                hints |= hint;
            }
        }
        return hints;
    }

    private long calculateSuppressedEffects() {
        int hints = calculateHints();
        long suppressedEffects = 0;
        if ((hints & 1) != 0) {
            suppressedEffects = 0 | 3;
        }
        if ((hints & 2) != 0) {
            suppressedEffects |= 1;
        }
        if ((hints & 4) != 0) {
            return suppressedEffects | 2;
        }
        return suppressedEffects;
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mNotificationLock"})
    public void updateInterruptionFilterLocked() {
        int interruptionFilter = this.mZenModeHelper.getZenModeListenerInterruptionFilter();
        if (interruptionFilter != this.mInterruptionFilter) {
            this.mInterruptionFilter = interruptionFilter;
            scheduleInterruptionFilterChanged(interruptionFilter);
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public INotificationManager getBinderService() {
        return INotificationManager.Stub.asInterface(this.mService);
    }

    /* access modifiers changed from: protected */
    @GuardedBy({"mNotificationLock"})
    public void reportSeen(NotificationRecord r) {
        if (!r.isProxied()) {
            this.mAppUsageStats.reportEvent(r.sbn.getPackageName(), getRealUserId(r.sbn.getUserId()), 10);
        }
    }

    /* access modifiers changed from: protected */
    public int calculateSuppressedVisualEffects(NotificationManager.Policy incomingPolicy, NotificationManager.Policy currPolicy, int targetSdkVersion) {
        if (incomingPolicy.suppressedVisualEffects == -1) {
            return incomingPolicy.suppressedVisualEffects;
        }
        int[] effectsIntroducedInP = {4, 8, 16, 32, 64, 128, 256};
        int newSuppressedVisualEffects = incomingPolicy.suppressedVisualEffects;
        if (targetSdkVersion < 28) {
            for (int i = 0; i < effectsIntroducedInP.length; i++) {
                newSuppressedVisualEffects = (newSuppressedVisualEffects & (~effectsIntroducedInP[i])) | (currPolicy.suppressedVisualEffects & effectsIntroducedInP[i]);
            }
            if ((newSuppressedVisualEffects & 1) != 0) {
                newSuppressedVisualEffects = newSuppressedVisualEffects | 8 | 4;
            }
            if ((newSuppressedVisualEffects & 2) != 0) {
                return newSuppressedVisualEffects | 16;
            }
            return newSuppressedVisualEffects;
        }
        boolean hasNewEffects = true;
        if ((newSuppressedVisualEffects - 2) - 1 <= 0) {
            hasNewEffects = false;
        }
        if (hasNewEffects) {
            int newSuppressedVisualEffects2 = newSuppressedVisualEffects & -4;
            if ((newSuppressedVisualEffects2 & 16) != 0) {
                newSuppressedVisualEffects2 |= 2;
            }
            if ((newSuppressedVisualEffects2 & 8) == 0 || (newSuppressedVisualEffects2 & 4) == 0 || (newSuppressedVisualEffects2 & 128) == 0) {
                return newSuppressedVisualEffects2;
            }
            return newSuppressedVisualEffects2 | 1;
        }
        if ((newSuppressedVisualEffects & 1) != 0) {
            newSuppressedVisualEffects = newSuppressedVisualEffects | 8 | 4 | 128;
        }
        if ((newSuppressedVisualEffects & 2) != 0) {
            return newSuppressedVisualEffects | 16;
        }
        return newSuppressedVisualEffects;
    }

    /* access modifiers changed from: protected */
    @GuardedBy({"mNotificationLock"})
    public void maybeRecordInterruptionLocked(NotificationRecord r) {
        if (r.isInterruptive() && !r.hasRecordedInterruption()) {
            this.mAppUsageStats.reportInterruptiveNotification(r.sbn.getPackageName(), r.getChannel().getId(), getRealUserId(r.sbn.getUserId()));
            r.setRecordedInterruption(true);
        }
    }

    /* access modifiers changed from: protected */
    public void reportUserInteraction(NotificationRecord r) {
        this.mAppUsageStats.reportEvent(r.sbn.getPackageName(), getRealUserId(r.sbn.getUserId()), 7);
    }

    private int getRealUserId(int userId) {
        if (userId == -1) {
            return 0;
        }
        return userId;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public NotificationManagerInternal getInternalService() {
        return this.mInternalService;
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void setNotificationAssistantAccessGrantedForUserInternal(ComponentName assistant, int baseUserId, boolean granted) {
        List<UserInfo> users = this.mUm.getEnabledProfiles(baseUserId);
        if (users != null) {
            for (UserInfo user : users) {
                int userId = user.id;
                if (assistant == null) {
                    ComponentName allowedAssistant = (ComponentName) CollectionUtils.firstOrNull(this.mAssistants.getAllowedComponents(userId));
                    if (allowedAssistant != null) {
                        setNotificationAssistantAccessGrantedForUserInternal(allowedAssistant, userId, false);
                    }
                } else if (!granted || this.mAllowedManagedServicePackages.test(assistant.getPackageName(), Integer.valueOf(userId), this.mAssistants.getRequiredPermission())) {
                    this.mConditionProviders.setPackageOrComponentEnabled(assistant.flattenToString(), userId, false, granted);
                    this.mAssistants.setPackageOrComponentEnabled(assistant.flattenToString(), userId, true, granted);
                    getContext().sendBroadcastAsUser(new Intent("android.app.action.NOTIFICATION_POLICY_ACCESS_GRANTED_CHANGED").setPackage(assistant.getPackageName()).addFlags(1073741824), UserHandle.of(userId), (String) null);
                    handleSavePolicyFile();
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void applyAdjustment(NotificationRecord r, Adjustment adjustment) {
        if (r != null && adjustment.getSignals() != null) {
            Bundle adjustments = adjustment.getSignals();
            Bundle.setDefusable(adjustments, true);
            List<String> toRemove = new ArrayList<>();
            for (String potentialKey : adjustments.keySet()) {
                if (!this.mAssistants.isAdjustmentAllowed(potentialKey)) {
                    toRemove.add(potentialKey);
                }
            }
            for (String removeKey : toRemove) {
                adjustments.remove(removeKey);
            }
            r.addAdjustment(adjustment);
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mNotificationLock"})
    public void addAutogroupKeyLocked(String key) {
        NotificationRecord r = this.mNotificationsByKey.get(key);
        if (r != null && r.sbn.getOverrideGroupKey() == null) {
            addAutoGroupAdjustment(r, "ranker_group");
            EventLogTags.writeNotificationAutogrouped(key);
            this.mRankingHandler.requestSort();
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mNotificationLock"})
    public void removeAutogroupKeyLocked(String key) {
        NotificationRecord r = this.mNotificationsByKey.get(key);
        if (r != null && r.sbn.getOverrideGroupKey() != null) {
            addAutoGroupAdjustment(r, (String) null);
            EventLogTags.writeNotificationUnautogrouped(key);
            this.mRankingHandler.requestSort();
        }
    }

    private void addAutoGroupAdjustment(NotificationRecord r, String overrideGroupKey) {
        Bundle signals = new Bundle();
        signals.putString("key_group_key", overrideGroupKey);
        r.addAdjustment(new Adjustment(r.sbn.getPackageName(), r.getKey(), signals, "", r.sbn.getUserId()));
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mNotificationLock"})
    public void clearAutogroupSummaryLocked(int userId, String pkg) {
        NotificationRecord removed;
        ArrayMap<String, String> summaries = this.mAutobundledSummaries.get(Integer.valueOf(userId));
        if (summaries != null && summaries.containsKey(pkg) && (removed = findNotificationByKeyLocked(summaries.remove(pkg))) != null) {
            cancelNotificationLocked(removed, false, 16, removeFromNotificationListsLocked(removed), (String) null);
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mNotificationLock"})
    public boolean hasAutoGroupSummaryLocked(StatusBarNotification sbn) {
        ArrayMap<String, String> summaries = this.mAutobundledSummaries.get(Integer.valueOf(sbn.getUserId()));
        return summaries != null && summaries.containsKey(sbn.getPackageName());
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x012f, code lost:
        if (r12 == null) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x0148, code lost:
        if (checkDisqualifyingFeatures(r11, MY_UID, r12.sbn.getId(), r12.sbn.getTag(), r12, true) == false) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x014a, code lost:
        mHandler.post(new com.android.server.notification.NotificationManagerService.EnqueueNotificationRunnable(r8, r11, r12));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void createAutoGroupSummary(int r28, java.lang.String r29, java.lang.String r30) {
        /*
            r27 = this;
            r8 = r27
            r9 = r29
            r1 = 0
            java.lang.Object r2 = mNotificationLock
            monitor-enter(r2)
            android.util.ArrayMap<java.lang.String, com.android.server.notification.NotificationRecord> r0 = r8.mNotificationsByKey     // Catch:{ all -> 0x0162 }
            r10 = r30
            java.lang.Object r0 = r0.get(r10)     // Catch:{ all -> 0x015c }
            com.android.server.notification.NotificationRecord r0 = (com.android.server.notification.NotificationRecord) r0     // Catch:{ all -> 0x015c }
            if (r0 != 0) goto L_0x001b
            monitor-exit(r2)     // Catch:{ all -> 0x0016 }
            return
        L_0x0016:
            r0 = move-exception
            r11 = r28
            goto L_0x0169
        L_0x001b:
            android.service.notification.StatusBarNotification r3 = r0.sbn     // Catch:{ all -> 0x015c }
            android.os.UserHandle r4 = r3.getUser()     // Catch:{ all -> 0x015c }
            int r4 = r4.getIdentifier()     // Catch:{ all -> 0x015c }
            r11 = r4
            android.util.ArrayMap<java.lang.Integer, android.util.ArrayMap<java.lang.String, java.lang.String>> r4 = r8.mAutobundledSummaries     // Catch:{ all -> 0x0158 }
            java.lang.Integer r5 = java.lang.Integer.valueOf(r11)     // Catch:{ all -> 0x0158 }
            java.lang.Object r4 = r4.get(r5)     // Catch:{ all -> 0x0158 }
            android.util.ArrayMap r4 = (android.util.ArrayMap) r4     // Catch:{ all -> 0x0158 }
            if (r4 != 0) goto L_0x003a
            android.util.ArrayMap r5 = new android.util.ArrayMap     // Catch:{ all -> 0x016b }
            r5.<init>()     // Catch:{ all -> 0x016b }
            r4 = r5
        L_0x003a:
            android.util.ArrayMap<java.lang.Integer, android.util.ArrayMap<java.lang.String, java.lang.String>> r5 = r8.mAutobundledSummaries     // Catch:{ all -> 0x0158 }
            java.lang.Integer r6 = java.lang.Integer.valueOf(r11)     // Catch:{ all -> 0x0158 }
            r5.put(r6, r4)     // Catch:{ all -> 0x0158 }
            boolean r5 = r4.containsKey(r9)     // Catch:{ all -> 0x0158 }
            if (r5 != 0) goto L_0x012a
            android.app.Notification r5 = r3.getNotification()     // Catch:{ all -> 0x0158 }
            android.os.Bundle r5 = r5.extras     // Catch:{ all -> 0x0158 }
            java.lang.String r6 = "android.appInfo"
            android.os.Parcelable r5 = r5.getParcelable(r6)     // Catch:{ all -> 0x0158 }
            android.content.pm.ApplicationInfo r5 = (android.content.pm.ApplicationInfo) r5     // Catch:{ all -> 0x0158 }
            android.os.Bundle r6 = new android.os.Bundle     // Catch:{ all -> 0x0158 }
            r6.<init>()     // Catch:{ all -> 0x0158 }
            java.lang.String r7 = "android.appInfo"
            r6.putParcelable(r7, r5)     // Catch:{ all -> 0x0158 }
            android.app.NotificationChannel r7 = r0.getChannel()     // Catch:{ all -> 0x0158 }
            java.lang.String r7 = r7.getId()     // Catch:{ all -> 0x0158 }
            android.app.Notification$Builder r12 = new android.app.Notification$Builder     // Catch:{ all -> 0x0158 }
            android.content.Context r13 = r27.getContext()     // Catch:{ all -> 0x0158 }
            r12.<init>(r13, r7)     // Catch:{ all -> 0x0158 }
            android.app.Notification r13 = r3.getNotification()     // Catch:{ all -> 0x0158 }
            android.graphics.drawable.Icon r13 = r13.getSmallIcon()     // Catch:{ all -> 0x0158 }
            android.app.Notification$Builder r12 = r12.setSmallIcon(r13)     // Catch:{ all -> 0x0158 }
            r13 = 1
            android.app.Notification$Builder r12 = r12.setGroupSummary(r13)     // Catch:{ all -> 0x0158 }
            r14 = 2
            android.app.Notification$Builder r12 = r12.setGroupAlertBehavior(r14)     // Catch:{ all -> 0x0158 }
            java.lang.String r14 = "ranker_group"
            android.app.Notification$Builder r12 = r12.setGroup(r14)     // Catch:{ all -> 0x0158 }
            r14 = 1024(0x400, float:1.435E-42)
            android.app.Notification$Builder r12 = r12.setFlag(r14, r13)     // Catch:{ all -> 0x0158 }
            r14 = 512(0x200, float:7.175E-43)
            android.app.Notification$Builder r12 = r12.setFlag(r14, r13)     // Catch:{ all -> 0x0158 }
            android.app.Notification r14 = r3.getNotification()     // Catch:{ all -> 0x0158 }
            int r14 = r14.color     // Catch:{ all -> 0x0158 }
            android.app.Notification$Builder r12 = r12.setColor(r14)     // Catch:{ all -> 0x0158 }
            android.app.Notification$Builder r12 = r12.setLocalOnly(r13)     // Catch:{ all -> 0x0158 }
            android.app.Notification r12 = r12.build()     // Catch:{ all -> 0x0158 }
            android.os.Bundle r13 = r12.extras     // Catch:{ all -> 0x0158 }
            r13.putAll(r6)     // Catch:{ all -> 0x0158 }
            android.content.Context r13 = r27.getContext()     // Catch:{ all -> 0x0158 }
            android.content.pm.PackageManager r13 = r13.getPackageManager()     // Catch:{ all -> 0x0158 }
            android.content.Intent r13 = r13.getLaunchIntentForPackage(r9)     // Catch:{ all -> 0x0158 }
            r25 = r13
            if (r25 == 0) goto L_0x00d9
            android.content.Context r14 = r27.getContext()     // Catch:{ all -> 0x016b }
            r15 = 0
            r17 = 0
            r18 = 0
            android.os.UserHandle r19 = android.os.UserHandle.of(r11)     // Catch:{ all -> 0x016b }
            r16 = r25
            android.app.PendingIntent r13 = android.app.PendingIntent.getActivityAsUser(r14, r15, r16, r17, r18, r19)     // Catch:{ all -> 0x016b }
            r12.contentIntent = r13     // Catch:{ all -> 0x016b }
        L_0x00d9:
            android.service.notification.StatusBarNotification r26 = new android.service.notification.StatusBarNotification     // Catch:{ all -> 0x0158 }
            java.lang.String r14 = r3.getPackageName()     // Catch:{ all -> 0x0158 }
            java.lang.String r15 = r3.getOpPkg()     // Catch:{ all -> 0x0158 }
            r16 = 2147483647(0x7fffffff, float:NaN)
            java.lang.String r17 = "ranker_group"
            int r18 = r3.getUid()     // Catch:{ all -> 0x0158 }
            int r19 = r3.getInitialPid()     // Catch:{ all -> 0x0158 }
            android.os.UserHandle r21 = r3.getUser()     // Catch:{ all -> 0x0158 }
            java.lang.String r22 = "ranker_group"
            long r23 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x0158 }
            r13 = r26
            r20 = r12
            r13.<init>(r14, r15, r16, r17, r18, r19, r20, r21, r22, r23)     // Catch:{ all -> 0x0158 }
            r13 = r26
            com.android.server.notification.NotificationRecord r14 = new com.android.server.notification.NotificationRecord     // Catch:{ all -> 0x0158 }
            android.content.Context r15 = r27.getContext()     // Catch:{ all -> 0x0158 }
            r16 = r1
            android.app.NotificationChannel r1 = r0.getChannel()     // Catch:{ all -> 0x0126 }
            r14.<init>(r15, r13, r1)     // Catch:{ all -> 0x0126 }
            r1 = r14
            boolean r14 = r0.getIsAppImportanceLocked()     // Catch:{ all -> 0x016b }
            r1.setIsAppImportanceLocked(r14)     // Catch:{ all -> 0x016b }
            java.lang.String r14 = r13.getKey()     // Catch:{ all -> 0x016b }
            r4.put(r9, r14)     // Catch:{ all -> 0x016b }
            r12 = r1
            goto L_0x012e
        L_0x0126:
            r0 = move-exception
            r1 = r16
            goto L_0x0169
        L_0x012a:
            r16 = r1
            r12 = r16
        L_0x012e:
            monitor-exit(r2)     // Catch:{ all -> 0x0155 }
            if (r12 == 0) goto L_0x0154
            int r3 = MY_UID
            android.service.notification.StatusBarNotification r0 = r12.sbn
            int r4 = r0.getId()
            android.service.notification.StatusBarNotification r0 = r12.sbn
            java.lang.String r5 = r0.getTag()
            r7 = 1
            r1 = r27
            r2 = r11
            r6 = r12
            boolean r0 = r1.checkDisqualifyingFeatures(r2, r3, r4, r5, r6, r7)
            if (r0 == 0) goto L_0x0154
            com.android.server.notification.NotificationManagerService$WorkerHandler r0 = mHandler
            com.android.server.notification.NotificationManagerService$EnqueueNotificationRunnable r1 = new com.android.server.notification.NotificationManagerService$EnqueueNotificationRunnable
            r1.<init>(r11, r12)
            r0.post(r1)
        L_0x0154:
            return
        L_0x0155:
            r0 = move-exception
            r1 = r12
            goto L_0x0169
        L_0x0158:
            r0 = move-exception
            r16 = r1
            goto L_0x0169
        L_0x015c:
            r0 = move-exception
            r16 = r1
            r11 = r28
            goto L_0x0169
        L_0x0162:
            r0 = move-exception
            r10 = r30
            r16 = r1
            r11 = r28
        L_0x0169:
            monitor-exit(r2)     // Catch:{ all -> 0x016b }
            throw r0
        L_0x016b:
            r0 = move-exception
            goto L_0x0169
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.notification.NotificationManagerService.createAutoGroupSummary(int, java.lang.String, java.lang.String):void");
    }

    /* access modifiers changed from: private */
    public String disableNotificationEffects(NotificationRecord record) {
        if (this.mDisableNotificationEffects) {
            return "booleanState";
        }
        if ((this.mListenerHints & 1) != 0) {
            return "listenerHints";
        }
        if (!(record == null || record.getAudioAttributes() == null)) {
            if ((this.mListenerHints & 2) != 0 && record.getAudioAttributes().getUsage() != 2) {
                return "listenerNoti";
            }
            if ((this.mListenerHints & 4) != 0 && record.getAudioAttributes().getUsage() == 2) {
                return "listenerCall";
            }
        }
        if (this.mCallState == 0 || this.mZenModeHelper.isCall(record)) {
            return null;
        }
        return "callState";
    }

    /* access modifiers changed from: private */
    public void dumpJson(PrintWriter pw, DumpFilter filter) {
        JSONObject dump = new JSONObject();
        try {
            dump.put("service", "Notification Manager");
            dump.put("bans", this.mPreferencesHelper.dumpBansJson(filter));
            dump.put("ranking", this.mPreferencesHelper.dumpJson(filter));
            dump.put("stats", this.mUsageStats.dumpJson(filter));
            dump.put("channels", this.mPreferencesHelper.dumpChannelsJson(filter));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        pw.println(dump);
    }

    /* access modifiers changed from: private */
    public void dumpProto(FileDescriptor fd, DumpFilter filter) {
        DumpFilter dumpFilter = filter;
        ProtoOutputStream proto = new ProtoOutputStream(fd);
        synchronized (mNotificationLock) {
            int N = this.mNotificationList.size();
            for (int i = 0; i < N; i++) {
                NotificationRecord nr = this.mNotificationList.get(i);
                if (!dumpFilter.filtered || dumpFilter.matches(nr.sbn)) {
                    nr.dump(proto, 2246267895809L, dumpFilter.redact, 1);
                }
            }
            int N2 = this.mEnqueuedNotifications.size();
            for (int i2 = 0; i2 < N2; i2++) {
                NotificationRecord nr2 = this.mEnqueuedNotifications.get(i2);
                if (!dumpFilter.filtered || dumpFilter.matches(nr2.sbn)) {
                    nr2.dump(proto, 2246267895809L, dumpFilter.redact, 0);
                }
            }
            List<NotificationRecord> snoozed = this.mSnoozeHelper.getSnoozed();
            int N3 = snoozed.size();
            for (int i3 = 0; i3 < N3; i3++) {
                NotificationRecord nr3 = snoozed.get(i3);
                if (!dumpFilter.filtered || dumpFilter.matches(nr3.sbn)) {
                    nr3.dump(proto, 2246267895809L, dumpFilter.redact, 2);
                }
            }
            long zenLog = proto.start(1146756268034L);
            this.mZenModeHelper.dump(proto);
            for (ComponentName suppressor : this.mEffectsSuppressors) {
                suppressor.writeToProto(proto, 2246267895812L);
            }
            proto.end(zenLog);
            long listenersToken = proto.start(1146756268035L);
            this.mListeners.dump(proto, dumpFilter);
            proto.end(listenersToken);
            proto.write(1120986464260L, this.mListenerHints);
            int i4 = 0;
            while (i4 < this.mListenersDisablingEffects.size()) {
                long effectsToken = proto.start(2246267895813L);
                int i5 = i4;
                long zenLog2 = zenLog;
                proto.write(1120986464257L, this.mListenersDisablingEffects.keyAt(i5));
                ArraySet<ComponentName> listeners = this.mListenersDisablingEffects.valueAt(i5);
                int j = 0;
                while (j < listeners.size()) {
                    listeners.valueAt(j).writeToProto(proto, 2246267895811L);
                    j++;
                }
                int i6 = j;
                proto.end(effectsToken);
                i4 = i5 + 1;
                FileDescriptor fileDescriptor = fd;
                zenLog = zenLog2;
            }
            int i7 = i4;
            long assistantsToken = proto.start(1146756268038L);
            this.mAssistants.dump(proto, dumpFilter);
            proto.end(assistantsToken);
            long conditionsToken = proto.start(1146756268039L);
            this.mConditionProviders.dump(proto, dumpFilter);
            proto.end(conditionsToken);
            long rankingToken = proto.start(1146756268040L);
            this.mRankingHelper.dump(proto, dumpFilter);
            this.mPreferencesHelper.dump(proto, dumpFilter);
            proto.end(rankingToken);
        }
        proto.flush();
    }

    /* access modifiers changed from: private */
    public void dumpNotificationRecords(PrintWriter pw, DumpFilter filter) {
        synchronized (mNotificationLock) {
            int N = this.mNotificationList.size();
            if (N > 0) {
                pw.println("  Notification List:");
                for (int i = 0; i < N; i++) {
                    NotificationRecord nr = this.mNotificationList.get(i);
                    if (!filter.filtered || filter.matches(nr.sbn)) {
                        nr.dump(pw, "    ", getContext(), filter.redact);
                    }
                }
                pw.println("  ");
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void dumpImpl(PrintWriter pw, DumpFilter filter) {
        pw.print("Current Notification Manager state");
        if (filter.filtered) {
            pw.print(" (filtered to ");
            pw.print(filter);
            pw.print(")");
        }
        pw.println(':');
        int j = 0;
        boolean zenOnly = filter.filtered && filter.zen;
        if (!zenOnly) {
            synchronized (this.mToastQueue) {
                int N = this.mToastQueue.size();
                if (N > 0) {
                    pw.println("  Toast Queue:");
                    for (int i = 0; i < N; i++) {
                        this.mToastQueue.get(i).dump(pw, "    ", filter);
                    }
                    pw.println("  ");
                }
            }
        }
        synchronized (mNotificationLock) {
            if (!zenOnly) {
                try {
                    if (!filter.normalPriority) {
                        dumpNotificationRecords(pw, filter);
                    }
                    if (!filter.filtered) {
                        int N2 = this.mLights.size();
                        if (N2 > 0) {
                            pw.println("  Lights List:");
                            for (int i2 = 0; i2 < N2; i2++) {
                                if (i2 == N2 - 1) {
                                    pw.print("  > ");
                                } else {
                                    pw.print("    ");
                                }
                                pw.println(this.mLights.get(i2));
                            }
                            pw.println("  ");
                        }
                        pw.println("  mUseAttentionLight=" + this.mUseAttentionLight);
                        pw.println("  mHasLight=" + this.mHasLight);
                        pw.println("  mNotificationPulseEnabled=" + this.mNotificationPulseEnabled);
                        pw.println("  mSoundNotificationKey=" + this.mSoundNotificationKey);
                        pw.println("  mVibrateNotificationKey=" + this.mVibrateNotificationKey);
                        pw.println("  mDisableNotificationEffects=" + this.mDisableNotificationEffects);
                        pw.println("  mCallState=" + callStateToString(this.mCallState));
                        pw.println("  mSystemReady=" + this.mSystemReady);
                        pw.println("  mMaxPackageEnqueueRate=" + this.mMaxPackageEnqueueRate);
                    }
                    pw.println("  mArchive=" + this.mArchive.toString());
                    Iterator<StatusBarNotification> iter = this.mArchive.descendingIterator();
                    while (true) {
                        if (!iter.hasNext()) {
                            break;
                        }
                        StatusBarNotification sbn = iter.next();
                        if (filter.matches(sbn)) {
                            pw.println("    " + sbn);
                            j++;
                            if (j >= 5) {
                                if (iter.hasNext()) {
                                    pw.println("    ...");
                                }
                            }
                        }
                    }
                    if (!zenOnly) {
                        int N3 = this.mEnqueuedNotifications.size();
                        if (N3 > 0) {
                            pw.println("  Enqueued Notification List:");
                            for (int i3 = 0; i3 < N3; i3++) {
                                NotificationRecord nr = this.mEnqueuedNotifications.get(i3);
                                if (!filter.filtered || filter.matches(nr.sbn)) {
                                    nr.dump(pw, "    ", getContext(), filter.redact);
                                }
                            }
                            pw.println("  ");
                        }
                        this.mSnoozeHelper.dump(pw, filter);
                    }
                } finally {
                }
            }
            if (!zenOnly) {
                pw.println("\n  Ranking Config:");
                this.mRankingHelper.dump(pw, "    ", filter);
                pw.println("\n Notification Preferences:");
                this.mPreferencesHelper.dump(pw, "    ", filter);
                pw.println("\n  Notification listeners:");
                this.mListeners.dump(pw, filter);
                pw.print("    mListenerHints: ");
                pw.println(this.mListenerHints);
                pw.print("    mListenersDisablingEffects: (");
                int N4 = this.mListenersDisablingEffects.size();
                for (int i4 = 0; i4 < N4; i4++) {
                    int hint = this.mListenersDisablingEffects.keyAt(i4);
                    if (i4 > 0) {
                        pw.print(';');
                    }
                    pw.print("hint[" + hint + "]:");
                    ArraySet<ComponentName> listeners = this.mListenersDisablingEffects.valueAt(i4);
                    int listenerSize = listeners.size();
                    for (int j2 = 0; j2 < listenerSize; j2++) {
                        if (j2 > 0) {
                            pw.print(',');
                        }
                        ComponentName listener = listeners.valueAt(j2);
                        if (listener != null) {
                            pw.print(listener);
                        }
                    }
                }
                pw.println(')');
                pw.println("\n  Notification assistant services:");
                this.mAssistants.dump(pw, filter);
            }
            if (filter.filtered == 0 || zenOnly) {
                pw.println("\n  Zen Mode:");
                pw.print("    mInterruptionFilter=");
                pw.println(this.mInterruptionFilter);
                this.mZenModeHelper.dump(pw, "    ");
                pw.println("\n  Zen Log:");
                ZenLog.dump(pw, "    ");
            }
            pw.println("\n  Condition providers:");
            this.mConditionProviders.dump(pw, filter);
            pw.println("\n  Group summaries:");
            for (Map.Entry<String, NotificationRecord> entry : this.mSummaryByGroupKey.entrySet()) {
                NotificationRecord r = entry.getValue();
                pw.println("    " + entry.getKey() + " -> " + r.getKey());
                if (this.mNotificationsByKey.get(r.getKey()) != r) {
                    pw.println("!!!!!!LEAK: Record not found in mNotificationsByKey.");
                    r.dump(pw, "      ", getContext(), filter.redact);
                }
            }
            if (!zenOnly) {
                pw.println("\n  Usage Stats:");
                this.mUsageStats.dump(pw, "    ", filter);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void enqueueNotificationInternal(String pkg, String opPkg, int callingUid, int callingPid, String tag, int id, Notification notification, int incomingUserId) {
        boolean z;
        boolean z2;
        String channelId;
        boolean z3;
        String str = pkg;
        String str2 = opPkg;
        int i = callingUid;
        int i2 = id;
        Notification notification2 = notification;
        if (DBG) {
            Slog.v(TAG, "enqueueNotificationInternal: pkg=" + str + " id=" + i2 + " notification=" + notification2);
        }
        if (str == null || notification2 == null) {
            throw new IllegalArgumentException("null not allowed: pkg=" + str + " id=" + id + " notification=" + notification2);
        }
        int userId = ActivityManager.handleIncomingUser(callingPid, callingUid, incomingUserId, true, false, "enqueueNotification", pkg);
        UserHandle user = UserHandle.of(userId);
        int notificationUid = resolveNotificationUid(str2, str, i, userId);
        checkRestrictedCategories(notification2);
        try {
            fixNotification(notification2, str, userId);
            this.mUsageStats.registerEnqueuedByApp(str);
            String channelId2 = notification.getChannelId();
            if (this.mIsTelevision && new Notification.TvExtender(notification2).getChannelId() != null) {
                channelId2 = new Notification.TvExtender(notification2).getChannelId();
            }
            NotificationChannel channel = this.mPreferencesHelper.getNotificationChannel(str, notificationUid, channelId2, false);
            if (channel == null) {
                Slog.e(TAG, "No Channel found for pkg=" + str + ", channelId=" + channelId2 + ", id=" + i2 + ", tag=" + tag + ", opPkg=" + str2 + ", callingUid=" + i + ", userId=" + userId + ", incomingUserId=" + incomingUserId + ", notificationUid=" + notificationUid + ", notification=" + notification2);
                if (!(this.mPreferencesHelper.getImportance(str, notificationUid) == 0)) {
                    doChannelWarningToast("Developer warning for package \"" + str + "\"\nFailed to post notification on channel \"" + channelId2 + "\"\nSee log for more details");
                    return;
                }
                return;
            }
            String str3 = tag;
            int i3 = incomingUserId;
            Notification notification3 = notification2;
            String str4 = str;
            NotificationRecord r = new NotificationRecord(getContext(), new StatusBarNotification(pkg, opPkg, id, tag, notificationUid, callingPid, notification, user, (String) null, System.currentTimeMillis()), channel);
            r.setIsAppImportanceLocked(this.mPreferencesHelper.getIsAppImportanceLocked(str4, i));
            if ((notification3.flags & 64) != 0) {
                boolean fgServiceShown = channel.isFgServiceShown();
                if (((channel.getUserLockedFields() & 4) == 0 || !fgServiceShown) && (r.getImportance() == 1 || r.getImportance() == 0)) {
                    if (TextUtils.isEmpty(channelId2)) {
                        z3 = true;
                        z = false;
                    } else if ("miscellaneous".equals(channelId2)) {
                        z3 = true;
                        z = false;
                    } else {
                        channel.setImportance(2);
                        r.setSystemImportance(2);
                        if (!fgServiceShown) {
                            channel.unlockFields(4);
                            z2 = true;
                            channel.setFgServiceShown(true);
                        } else {
                            z2 = true;
                        }
                        z = false;
                        this.mPreferencesHelper.updateNotificationChannel(str4, notificationUid, channel, false);
                        r.updateNotificationChannel(channel);
                    }
                    r.setSystemImportance(2);
                } else if (fgServiceShown || TextUtils.isEmpty(channelId2)) {
                    z2 = true;
                    z = false;
                } else if (!"miscellaneous".equals(channelId2)) {
                    channel.setFgServiceShown(true);
                    r.updateNotificationChannel(channel);
                    z2 = true;
                    z = false;
                } else {
                    z2 = true;
                    z = false;
                }
            } else {
                z2 = true;
                z = false;
            }
            if (r.sbn.getOverrideGroupKey() == null) {
                z2 = z;
            }
            Notification notification4 = notification3;
            String str5 = str4;
            int i4 = notificationUid;
            NotificationChannel notificationChannel = channel;
            int userId2 = userId;
            if (checkDisqualifyingFeatures(userId, notificationUid, id, tag, r, z2)) {
                if (notification4.allPendingIntents != null) {
                    int intentCount = notification4.allPendingIntents.size();
                    if (intentCount > 0) {
                        ActivityManagerInternal am = (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
                        long duration = ((DeviceIdleController.LocalService) LocalServices.getService(DeviceIdleController.LocalService.class)).getNotificationWhitelistDuration();
                        int i5 = 0;
                        while (i5 < intentCount) {
                            PendingIntent pendingIntent = (PendingIntent) notification4.allPendingIntents.valueAt(i5);
                            if (pendingIntent != null) {
                                am.setPendingIntentWhitelistDuration(pendingIntent.getTarget(), WHITELIST_TOKEN, duration);
                                channelId = channelId2;
                                am.setPendingIntentAllowBgActivityStarts(pendingIntent.getTarget(), WHITELIST_TOKEN, 7);
                            } else {
                                channelId = channelId2;
                            }
                            i5++;
                            channelId2 = channelId;
                        }
                    }
                }
                mHandler.post(new EnqueueNotificationRunnable(userId2, r));
            }
        } catch (PackageManager.NameNotFoundException e) {
            int i6 = userId;
            Notification notification5 = notification2;
            String str6 = str;
            int i7 = notificationUid;
            Slog.e(TAG, "Cannot create a context for sending app", e);
        }
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void fixNotification(Notification notification, String pkg, int userId) throws PackageManager.NameNotFoundException {
        ApplicationInfo ai = this.mPackageManagerClient.getApplicationInfoAsUser(pkg, 268435456, userId == -1 ? 0 : userId);
        Notification.addFieldsFromContext(ai, notification);
        if (this.mPackageManagerClient.checkPermission("android.permission.USE_COLORIZED_NOTIFICATIONS", pkg) == 0) {
            notification.flags |= 2048;
        } else {
            notification.flags &= -2049;
        }
        if (!(notification.fullScreenIntent == null || ai.targetSdkVersion < 29 || this.mPackageManagerClient.checkPermission("android.permission.USE_FULL_SCREEN_INTENT", pkg) == 0)) {
            notification.fullScreenIntent = null;
            Slog.w(TAG, "Package " + pkg + ": Use of fullScreenIntent requires the USE_FULL_SCREEN_INTENT permission");
        }
        NotificationManagerServiceInjector.checkFullScreenIntent(notification, this.mAppOps, ai.uid, pkg);
    }

    /* access modifiers changed from: private */
    public void flagNotificationForBubbles(NotificationRecord r, String pkg, int userId, NotificationRecord oldRecord) {
        Notification notification = r.getNotification();
        if (isNotificationAppropriateToBubble(r, pkg, userId, oldRecord)) {
            notification.flags |= 4096;
        } else {
            notification.flags &= -4097;
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0062  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0064  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0069  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0072  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0084  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0086  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00b2  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x00b4  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x00b7 A[ADDED_TO_REGION] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isNotificationAppropriateToBubble(com.android.server.notification.NotificationRecord r18, java.lang.String r19, int r20, com.android.server.notification.NotificationRecord r21) {
        /*
            r17 = this;
            r0 = r17
            r1 = r19
            android.app.Notification r2 = r18.getNotification()
            android.app.Notification$BubbleMetadata r3 = r2.getBubbleMetadata()
            r5 = 0
            if (r3 == 0) goto L_0x001f
            android.content.Context r6 = r17.getContext()
            android.app.PendingIntent r7 = r3.getIntent()
            boolean r6 = r0.canLaunchInActivityView(r6, r7, r1)
            if (r6 == 0) goto L_0x001f
            r6 = 1
            goto L_0x0020
        L_0x001f:
            r6 = r5
        L_0x0020:
            if (r6 == 0) goto L_0x0053
            com.android.server.notification.PreferencesHelper r7 = r0.mPreferencesHelper
            r8 = r20
            boolean r7 = r7.areBubblesAllowed(r1, r8)
            if (r7 == 0) goto L_0x0050
            com.android.server.notification.PreferencesHelper r7 = r0.mPreferencesHelper
            r9 = r18
            android.service.notification.StatusBarNotification r10 = r9.sbn
            android.os.UserHandle r10 = r10.getUser()
            boolean r7 = r7.bubblesEnabled(r10)
            if (r7 == 0) goto L_0x0057
            android.app.NotificationChannel r7 = r18.getChannel()
            boolean r7 = r7.canBubble()
            if (r7 == 0) goto L_0x0057
            android.app.ActivityManager r7 = r0.mActivityManager
            boolean r7 = r7.isLowRamDevice()
            if (r7 != 0) goto L_0x0057
            r7 = 1
            goto L_0x0058
        L_0x0050:
            r9 = r18
            goto L_0x0057
        L_0x0053:
            r9 = r18
            r8 = r20
        L_0x0057:
            r7 = r5
        L_0x0058:
            android.app.ActivityManager r10 = r0.mActivityManager
            int r10 = r10.getPackageImportance(r1)
            r11 = 100
            if (r10 != r11) goto L_0x0064
            r10 = 1
            goto L_0x0065
        L_0x0064:
            r10 = r5
        L_0x0065:
            android.os.Bundle r11 = r2.extras
            if (r11 == 0) goto L_0x0072
            android.os.Bundle r11 = r2.extras
            java.lang.String r12 = "android.people.list"
            java.util.ArrayList r11 = r11.getParcelableArrayList(r12)
            goto L_0x0073
        L_0x0072:
            r11 = 0
        L_0x0073:
            java.lang.String r12 = r2.category
            java.lang.String r13 = "call"
            boolean r12 = r13.equals(r12)
            if (r12 == 0) goto L_0x0086
            int r12 = r2.flags
            r12 = r12 & 64
            if (r12 == 0) goto L_0x0086
            r12 = 1
            goto L_0x0087
        L_0x0086:
            r12 = r5
        L_0x0087:
            java.lang.Class r13 = r2.getNotificationStyle()
            java.lang.Class<android.app.Notification$MessagingStyle> r14 = android.app.Notification.MessagingStyle.class
            boolean r14 = r14.equals(r13)
            if (r14 == 0) goto L_0x0099
            boolean r15 = r0.hasValidRemoteInput(r2)
            if (r15 != 0) goto L_0x00a3
        L_0x0099:
            if (r11 == 0) goto L_0x00a5
            boolean r15 = r11.isEmpty()
            if (r15 != 0) goto L_0x00a5
            if (r12 == 0) goto L_0x00a5
        L_0x00a3:
            r15 = 1
            goto L_0x00a6
        L_0x00a5:
            r15 = r5
        L_0x00a6:
            if (r21 == 0) goto L_0x00b4
            android.app.Notification r4 = r21.getNotification()
            int r4 = r4.flags
            r4 = r4 & 4096(0x1000, float:5.74E-42)
            if (r4 == 0) goto L_0x00b4
            r4 = 1
            goto L_0x00b5
        L_0x00b4:
            r4 = r5
        L_0x00b5:
            if (r7 == 0) goto L_0x00c0
            if (r15 != 0) goto L_0x00bd
            if (r10 != 0) goto L_0x00bd
            if (r4 == 0) goto L_0x00c0
        L_0x00bd:
            r16 = 1
            goto L_0x00c2
        L_0x00c0:
            r16 = r5
        L_0x00c2:
            return r16
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.notification.NotificationManagerService.isNotificationAppropriateToBubble(com.android.server.notification.NotificationRecord, java.lang.String, int, com.android.server.notification.NotificationRecord):boolean");
    }

    private boolean hasValidRemoteInput(Notification n) {
        Notification.Action[] actions = n.actions;
        if (actions == null) {
            return false;
        }
        for (Notification.Action action : actions) {
            RemoteInput[] inputs = action.getRemoteInputs();
            if (inputs != null && inputs.length > 0) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public boolean canLaunchInActivityView(Context context, PendingIntent pendingIntent, String packageName) {
        ActivityInfo info;
        if (pendingIntent == null) {
            Log.w(TAG, "Unable to create bubble -- no intent");
            return false;
        }
        long token = Binder.clearCallingIdentity();
        try {
            Intent intent = pendingIntent.getIntent();
            if (intent != null) {
                info = intent.resolveActivityInfo(context.getPackageManager(), 0);
            } else {
                info = null;
            }
            if (info == null) {
                StatsLog.write(173, packageName, 1);
                Log.w(TAG, "Unable to send as bubble -- couldn't find activity info for intent: " + intent);
                return false;
            } else if (!ActivityInfo.isResizeableMode(info.resizeMode)) {
                StatsLog.write(173, packageName, 2);
                Log.w(TAG, "Unable to send as bubble -- activity is not resizable for intent: " + intent);
                return false;
            } else if (info.documentLaunchMode != 2) {
                StatsLog.write(173, packageName, 3);
                Log.w(TAG, "Unable to send as bubble -- activity is not documentLaunchMode=always for intent: " + intent);
                return false;
            } else if ((info.flags & Integer.MIN_VALUE) != 0) {
                return true;
            } else {
                Log.w(TAG, "Unable to send as bubble -- activity is not embeddable for intent: " + intent);
                return false;
            }
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    private void doChannelWarningToast(CharSequence toastText) {
        Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable(toastText) {
            private final /* synthetic */ CharSequence f$1;

            {
                this.f$1 = r2;
            }

            public final void runOrThrow() {
                NotificationManagerService.this.lambda$doChannelWarningToast$2$NotificationManagerService(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$doChannelWarningToast$2$NotificationManagerService(CharSequence toastText) throws Exception {
        if (Settings.Global.getInt(getContext().getContentResolver(), "show_notification_channel_warnings", (int) Build.IS_DEBUGGABLE) != 0) {
            Toast.makeText(getContext(), mHandler.getLooper(), toastText, 0).show();
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public int resolveNotificationUid(String callingPkg, String targetPkg, int callingUid, int userId) {
        if (userId == -1) {
            userId = 0;
        }
        if (isCallerSameApp(targetPkg, callingUid, userId) && (TextUtils.equals(callingPkg, targetPkg) || isCallerSameApp(callingPkg, callingUid, userId))) {
            return callingUid;
        }
        int targetUid = -1;
        try {
            targetUid = this.mPackageManagerClient.getPackageUidAsUser(targetPkg, userId);
        } catch (PackageManager.NameNotFoundException e) {
        }
        if ((targetUid != -1 && (isCallerAndroid(callingPkg, callingUid) || this.mPreferencesHelper.isDelegateAllowed(targetPkg, targetUid, callingPkg, callingUid))) || NotificationManagerServiceInjector.checkCallerIsXmsf(getContext(), callingPkg, targetPkg, callingUid, userId)) {
            return targetUid;
        }
        throw new SecurityException("Caller " + callingPkg + ":" + callingUid + " cannot post for pkg " + targetPkg + " in user " + userId);
    }

    /* Debug info: failed to restart local var, previous not found, register: 18 */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x00ce, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean checkDisqualifyingFeatures(int r19, int r20, int r21, java.lang.String r22, com.android.server.notification.NotificationRecord r23, boolean r24) {
        /*
            r18 = this;
            r1 = r18
            r2 = r19
            r3 = r23
            android.service.notification.StatusBarNotification r0 = r3.sbn
            java.lang.String r4 = r0.getPackageName()
            r5 = r20
            boolean r0 = r1.isUidSystemOrPhone(r5)
            r6 = 1
            r7 = 0
            if (r0 != 0) goto L_0x0022
            java.lang.String r0 = "android"
            boolean r0 = r0.equals(r4)
            if (r0 == 0) goto L_0x0020
            goto L_0x0022
        L_0x0020:
            r0 = r7
            goto L_0x0023
        L_0x0022:
            r0 = r6
        L_0x0023:
            r8 = r0
            com.android.server.notification.NotificationManagerService$NotificationListeners r0 = r1.mListeners
            boolean r9 = r0.isListenerPackage(r4)
            if (r8 != 0) goto L_0x010b
            if (r9 != 0) goto L_0x010b
            java.lang.Object r10 = mNotificationLock
            monitor-enter(r10)
            int r0 = android.os.Binder.getCallingUid()     // Catch:{ all -> 0x0102 }
            android.util.ArrayMap<java.lang.String, com.android.server.notification.NotificationRecord> r11 = r1.mNotificationsByKey     // Catch:{ all -> 0x0102 }
            android.service.notification.StatusBarNotification r12 = r3.sbn     // Catch:{ all -> 0x0102 }
            java.lang.String r12 = r12.getKey()     // Catch:{ all -> 0x0102 }
            java.lang.Object r11 = r11.get(r12)     // Catch:{ all -> 0x0102 }
            if (r11 != 0) goto L_0x0066
            boolean r11 = r1.isCallerInstantApp(r0, r2)     // Catch:{ all -> 0x0102 }
            if (r11 != 0) goto L_0x004a
            goto L_0x0066
        L_0x004a:
            java.lang.SecurityException r6 = new java.lang.SecurityException     // Catch:{ all -> 0x0102 }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x0102 }
            r7.<init>()     // Catch:{ all -> 0x0102 }
            java.lang.String r11 = "Instant app "
            r7.append(r11)     // Catch:{ all -> 0x0102 }
            r7.append(r4)     // Catch:{ all -> 0x0102 }
            java.lang.String r11 = " cannot create notifications"
            r7.append(r11)     // Catch:{ all -> 0x0102 }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x0102 }
            r6.<init>(r7)     // Catch:{ all -> 0x0102 }
            throw r6     // Catch:{ all -> 0x0102 }
        L_0x0066:
            android.util.ArrayMap<java.lang.String, com.android.server.notification.NotificationRecord> r11 = r1.mNotificationsByKey     // Catch:{ all -> 0x0102 }
            android.service.notification.StatusBarNotification r12 = r3.sbn     // Catch:{ all -> 0x0102 }
            java.lang.String r12 = r12.getKey()     // Catch:{ all -> 0x0102 }
            java.lang.Object r11 = r11.get(r12)     // Catch:{ all -> 0x0102 }
            if (r11 == 0) goto L_0x00cf
            android.app.Notification r11 = r23.getNotification()     // Catch:{ all -> 0x0102 }
            boolean r11 = r11.hasCompletedProgress()     // Catch:{ all -> 0x0102 }
            if (r11 != 0) goto L_0x00cf
            if (r24 != 0) goto L_0x00cf
            com.android.server.notification.NotificationUsageStats r11 = r1.mUsageStats     // Catch:{ all -> 0x0102 }
            float r11 = r11.getAppEnqueueRate(r4)     // Catch:{ all -> 0x0102 }
            float r12 = r1.mMaxPackageEnqueueRate     // Catch:{ all -> 0x0102 }
            int r12 = (r11 > r12 ? 1 : (r11 == r12 ? 0 : -1))
            if (r12 <= 0) goto L_0x00cf
            com.android.server.notification.NotificationUsageStats r6 = r1.mUsageStats     // Catch:{ all -> 0x0102 }
            r6.registerOverRateQuota(r4)     // Catch:{ all -> 0x0102 }
            long r12 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x0102 }
            long r14 = r1.mLastOverRateLogTime     // Catch:{ all -> 0x0102 }
            long r14 = r12 - r14
            r16 = 5000(0x1388, double:2.4703E-320)
            int r6 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1))
            if (r6 <= 0) goto L_0x00cd
            java.lang.String r6 = "NotificationService"
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ all -> 0x0102 }
            r14.<init>()     // Catch:{ all -> 0x0102 }
            java.lang.String r15 = "Package enqueue rate is "
            r14.append(r15)     // Catch:{ all -> 0x0102 }
            r14.append(r11)     // Catch:{ all -> 0x0102 }
            java.lang.String r15 = ". Shedding "
            r14.append(r15)     // Catch:{ all -> 0x0102 }
            android.service.notification.StatusBarNotification r15 = r3.sbn     // Catch:{ all -> 0x0102 }
            java.lang.String r15 = r15.getKey()     // Catch:{ all -> 0x0102 }
            r14.append(r15)     // Catch:{ all -> 0x0102 }
            java.lang.String r15 = ". package="
            r14.append(r15)     // Catch:{ all -> 0x0102 }
            r14.append(r4)     // Catch:{ all -> 0x0102 }
            java.lang.String r14 = r14.toString()     // Catch:{ all -> 0x0102 }
            android.util.Slog.e(r6, r14)     // Catch:{ all -> 0x0102 }
            r1.mLastOverRateLogTime = r12     // Catch:{ all -> 0x0102 }
        L_0x00cd:
            monitor-exit(r10)     // Catch:{ all -> 0x0102 }
            return r7
        L_0x00cf:
            r11 = r21
            r12 = r22
            int r13 = r1.getNotificationCountLocked(r4, r2, r11, r12)     // Catch:{ all -> 0x0109 }
            r14 = 25
            if (r13 < r14) goto L_0x0100
            com.android.server.notification.NotificationUsageStats r6 = r1.mUsageStats     // Catch:{ all -> 0x0109 }
            r6.registerOverCountQuota(r4)     // Catch:{ all -> 0x0109 }
            java.lang.String r6 = "NotificationService"
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ all -> 0x0109 }
            r14.<init>()     // Catch:{ all -> 0x0109 }
            java.lang.String r15 = "Package has already posted or enqueued "
            r14.append(r15)     // Catch:{ all -> 0x0109 }
            r14.append(r13)     // Catch:{ all -> 0x0109 }
            java.lang.String r15 = " notifications.  Not showing more.  package="
            r14.append(r15)     // Catch:{ all -> 0x0109 }
            r14.append(r4)     // Catch:{ all -> 0x0109 }
            java.lang.String r14 = r14.toString()     // Catch:{ all -> 0x0109 }
            android.util.Slog.e(r6, r14)     // Catch:{ all -> 0x0109 }
            monitor-exit(r10)     // Catch:{ all -> 0x0109 }
            return r7
        L_0x0100:
            monitor-exit(r10)     // Catch:{ all -> 0x0109 }
            goto L_0x010f
        L_0x0102:
            r0 = move-exception
            r11 = r21
            r12 = r22
        L_0x0107:
            monitor-exit(r10)     // Catch:{ all -> 0x0109 }
            throw r0
        L_0x0109:
            r0 = move-exception
            goto L_0x0107
        L_0x010b:
            r11 = r21
            r12 = r22
        L_0x010f:
            com.android.server.notification.SnoozeHelper r0 = r1.mSnoozeHelper
            java.lang.String r10 = r23.getKey()
            boolean r0 = r0.isSnoozed(r2, r4, r10)
            if (r0 == 0) goto L_0x0154
            android.metrics.LogMaker r0 = r23.getLogMaker()
            r6 = 6
            android.metrics.LogMaker r0 = r0.setType(r6)
            r6 = 831(0x33f, float:1.164E-42)
            android.metrics.LogMaker r0 = r0.setCategory(r6)
            com.android.internal.logging.MetricsLogger.action(r0)
            boolean r0 = DBG
            if (r0 == 0) goto L_0x014b
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r6 = "Ignored enqueue for snoozed notification "
            r0.append(r6)
            java.lang.String r6 = r23.getKey()
            r0.append(r6)
            java.lang.String r0 = r0.toString()
            java.lang.String r6 = "NotificationService"
            android.util.Slog.d(r6, r0)
        L_0x014b:
            com.android.server.notification.SnoozeHelper r0 = r1.mSnoozeHelper
            r0.update(r2, r3)
            r18.handleSavePolicyFile()
            return r7
        L_0x0154:
            com.android.server.notification.NotificationUsageStats r0 = r1.mUsageStats
            boolean r0 = r1.isBlocked(r3, r0)
            if (r0 == 0) goto L_0x015d
            return r7
        L_0x015d:
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.notification.NotificationManagerService.checkDisqualifyingFeatures(int, int, int, java.lang.String, com.android.server.notification.NotificationRecord, boolean):boolean");
    }

    /* access modifiers changed from: protected */
    @GuardedBy({"mNotificationLock"})
    public int getNotificationCountLocked(String pkg, int userId, int excludedId, String excludedTag) {
        int count = 0;
        int N = this.mNotificationList.size();
        for (int i = 0; i < N; i++) {
            NotificationRecord existing = this.mNotificationList.get(i);
            if (existing.sbn.getPackageName().equals(pkg) && existing.sbn.getUserId() == userId && (existing.sbn.getId() != excludedId || !TextUtils.equals(existing.sbn.getTag(), excludedTag))) {
                count++;
            }
        }
        int M = this.mEnqueuedNotifications.size();
        for (int i2 = 0; i2 < M; i2++) {
            NotificationRecord existing2 = this.mEnqueuedNotifications.get(i2);
            if (existing2.sbn.getPackageName().equals(pkg) && existing2.sbn.getUserId() == userId) {
                count++;
            }
        }
        return count;
    }

    /* access modifiers changed from: protected */
    public boolean isBlocked(NotificationRecord r, NotificationUsageStats usageStats) {
        if (!isBlocked(r)) {
            return false;
        }
        Slog.e(TAG, "Suppressing notification from package by user request.");
        usageStats.registerBlocked(r);
        return true;
    }

    /* access modifiers changed from: private */
    public boolean isBlocked(NotificationRecord r) {
        String pkg = r.sbn.getPackageName();
        int callingUid = r.sbn.getUid();
        return this.mPreferencesHelper.isGroupBlocked(pkg, callingUid, r.getChannel().getGroup()) || this.mPreferencesHelper.getImportance(pkg, callingUid) == 0 || r.getImportance() == 0;
    }

    protected class SnoozeNotificationRunnable implements Runnable {
        private final long mDuration;
        private final String mKey;
        private final String mSnoozeCriterionId;

        SnoozeNotificationRunnable(String key, long duration, String snoozeCriterionId) {
            this.mKey = key;
            this.mDuration = duration;
            this.mSnoozeCriterionId = snoozeCriterionId;
        }

        public void run() {
            synchronized (NotificationManagerService.mNotificationLock) {
                NotificationRecord r = NotificationManagerService.this.findNotificationByKeyLocked(this.mKey);
                if (r != null) {
                    snoozeLocked(r);
                }
            }
        }

        /* access modifiers changed from: package-private */
        @GuardedBy({"mNotificationLock"})
        public void snoozeLocked(NotificationRecord r) {
            if (r.sbn.isGroup()) {
                List<NotificationRecord> groupNotifications = NotificationManagerService.this.findGroupNotificationsLocked(r.sbn.getPackageName(), r.sbn.getGroupKey(), r.sbn.getUserId());
                if (r.getNotification().isGroupSummary()) {
                    for (int i = 0; i < groupNotifications.size(); i++) {
                        snoozeNotificationLocked(groupNotifications.get(i));
                    }
                } else if (!NotificationManagerService.this.mSummaryByGroupKey.containsKey(r.sbn.getGroupKey())) {
                    snoozeNotificationLocked(r);
                } else if (groupNotifications.size() != 2) {
                    snoozeNotificationLocked(r);
                } else {
                    for (int i2 = 0; i2 < groupNotifications.size(); i2++) {
                        snoozeNotificationLocked(groupNotifications.get(i2));
                    }
                }
            } else {
                snoozeNotificationLocked(r);
            }
        }

        /* access modifiers changed from: package-private */
        @GuardedBy({"mNotificationLock"})
        public void snoozeNotificationLocked(NotificationRecord r) {
            MetricsLogger.action(r.getLogMaker().setCategory(831).setType(2).addTaggedData(1139, Long.valueOf(this.mDuration)).addTaggedData(832, Integer.valueOf(this.mSnoozeCriterionId == null ? 0 : 1)));
            NotificationManagerService.this.reportUserInteraction(r);
            NotificationManagerService.this.cancelNotificationLocked(r, false, 18, NotificationManagerService.this.removeFromNotificationListsLocked(r), (String) null);
            NotificationManagerService.this.updateLightsLocked();
            if (this.mSnoozeCriterionId != null) {
                NotificationManagerService.this.mAssistants.notifyAssistantSnoozedLocked(r.sbn, this.mSnoozeCriterionId);
                NotificationManagerService.this.mSnoozeHelper.snooze(r);
            } else {
                NotificationManagerService.this.mSnoozeHelper.snooze(r, this.mDuration);
            }
            r.recordSnoozed();
            NotificationManagerService.this.handleSavePolicyFile();
        }
    }

    protected class CancelNotificationRunnable implements Runnable {
        private final int mCallingPid;
        private final int mCallingUid;
        private final int mCount;
        private final int mId;
        private final ManagedServices.ManagedServiceInfo mListener;
        private final int mMustHaveFlags;
        private final int mMustNotHaveFlags;
        private final String mPkg;
        private final int mRank;
        private final int mReason;
        private final boolean mSendDelete;
        private final String mTag;
        private final int mUserId;

        CancelNotificationRunnable(int callingUid, int callingPid, String pkg, String tag, int id, int mustHaveFlags, int mustNotHaveFlags, boolean sendDelete, int userId, int reason, int rank, int count, ManagedServices.ManagedServiceInfo listener) {
            this.mCallingUid = callingUid;
            this.mCallingPid = callingPid;
            this.mPkg = pkg;
            this.mTag = tag;
            this.mId = id;
            this.mMustHaveFlags = mustHaveFlags;
            this.mMustNotHaveFlags = mustNotHaveFlags;
            this.mSendDelete = sendDelete;
            this.mUserId = userId;
            this.mReason = reason;
            this.mRank = rank;
            this.mCount = count;
            this.mListener = listener;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:35:0x00d3, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
                r19 = this;
                r1 = r19
                com.android.server.notification.ManagedServices$ManagedServiceInfo r0 = r1.mListener
                if (r0 != 0) goto L_0x0008
                r0 = 0
                goto L_0x000e
            L_0x0008:
                android.content.ComponentName r0 = r0.component
                java.lang.String r0 = r0.toShortString()
            L_0x000e:
                r11 = r0
                boolean r0 = com.android.server.notification.NotificationManagerService.DBG
                if (r0 == 0) goto L_0x0028
                int r2 = r1.mCallingUid
                int r3 = r1.mCallingPid
                java.lang.String r4 = r1.mPkg
                int r5 = r1.mId
                java.lang.String r6 = r1.mTag
                int r7 = r1.mUserId
                int r8 = r1.mMustHaveFlags
                int r9 = r1.mMustNotHaveFlags
                int r10 = r1.mReason
                com.android.server.EventLogTags.writeNotificationCancel(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            L_0x0028:
                java.lang.Object r2 = com.android.server.notification.NotificationManagerService.mNotificationLock
                monitor-enter(r2)
                com.android.server.notification.NotificationManagerService r3 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x00d4 }
                com.android.server.notification.NotificationManagerService r0 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x00d4 }
                java.util.ArrayList<com.android.server.notification.NotificationRecord> r4 = r0.mEnqueuedNotifications     // Catch:{ all -> 0x00d4 }
                java.lang.String r5 = r1.mPkg     // Catch:{ all -> 0x00d4 }
                java.lang.String r6 = r1.mTag     // Catch:{ all -> 0x00d4 }
                int r7 = r1.mId     // Catch:{ all -> 0x00d4 }
                int r8 = r1.mUserId     // Catch:{ all -> 0x00d4 }
                com.android.server.notification.NotificationRecord r0 = r3.findNotificationByListLocked(r4, r5, r6, r7, r8)     // Catch:{ all -> 0x00d4 }
                if (r0 == 0) goto L_0x0048
                com.android.server.notification.NotificationManagerService$WorkerHandler r0 = com.android.server.notification.NotificationManagerService.mHandler     // Catch:{ all -> 0x00d4 }
                r0.post(r1)     // Catch:{ all -> 0x00d4 }
                monitor-exit(r2)     // Catch:{ all -> 0x00d4 }
                return
            L_0x0048:
                com.android.server.notification.NotificationManagerService r3 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x00d4 }
                com.android.server.notification.NotificationManagerService r0 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x00d4 }
                java.util.ArrayList<com.android.server.notification.NotificationRecord> r4 = r0.mNotificationList     // Catch:{ all -> 0x00d4 }
                java.lang.String r5 = r1.mPkg     // Catch:{ all -> 0x00d4 }
                java.lang.String r6 = r1.mTag     // Catch:{ all -> 0x00d4 }
                int r7 = r1.mId     // Catch:{ all -> 0x00d4 }
                int r8 = r1.mUserId     // Catch:{ all -> 0x00d4 }
                com.android.server.notification.NotificationRecord r0 = r3.findNotificationByListLocked(r4, r5, r6, r7, r8)     // Catch:{ all -> 0x00d4 }
                if (r0 == 0) goto L_0x00b3
                int r3 = r1.mReason     // Catch:{ all -> 0x00d4 }
                r4 = 1
                if (r3 != r4) goto L_0x006a
                com.android.server.notification.NotificationManagerService r3 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x00d4 }
                com.android.server.notification.NotificationUsageStats r3 = r3.mUsageStats     // Catch:{ all -> 0x00d4 }
                r3.registerClickedByUser(r0)     // Catch:{ all -> 0x00d4 }
            L_0x006a:
                android.app.Notification r3 = r0.getNotification()     // Catch:{ all -> 0x00d4 }
                int r3 = r3.flags     // Catch:{ all -> 0x00d4 }
                int r4 = r1.mMustHaveFlags     // Catch:{ all -> 0x00d4 }
                r3 = r3 & r4
                int r4 = r1.mMustHaveFlags     // Catch:{ all -> 0x00d4 }
                if (r3 == r4) goto L_0x0079
                monitor-exit(r2)     // Catch:{ all -> 0x00d4 }
                return
            L_0x0079:
                android.app.Notification r3 = r0.getNotification()     // Catch:{ all -> 0x00d4 }
                int r3 = r3.flags     // Catch:{ all -> 0x00d4 }
                int r4 = r1.mMustNotHaveFlags     // Catch:{ all -> 0x00d4 }
                r3 = r3 & r4
                if (r3 == 0) goto L_0x0086
                monitor-exit(r2)     // Catch:{ all -> 0x00d4 }
                return
            L_0x0086:
                com.android.server.notification.NotificationManagerService r3 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x00d4 }
                boolean r9 = r3.removeFromNotificationListsLocked(r0)     // Catch:{ all -> 0x00d4 }
                com.android.server.notification.NotificationManagerService r3 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x00d4 }
                boolean r5 = r1.mSendDelete     // Catch:{ all -> 0x00d4 }
                int r6 = r1.mReason     // Catch:{ all -> 0x00d4 }
                int r7 = r1.mRank     // Catch:{ all -> 0x00d4 }
                int r8 = r1.mCount     // Catch:{ all -> 0x00d4 }
                r4 = r0
                r10 = r11
                r3.cancelNotificationLocked(r4, r5, r6, r7, r8, r9, r10)     // Catch:{ all -> 0x00d4 }
                com.android.server.notification.NotificationManagerService r12 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x00d4 }
                int r14 = r1.mCallingUid     // Catch:{ all -> 0x00d4 }
                int r15 = r1.mCallingPid     // Catch:{ all -> 0x00d4 }
                boolean r3 = r1.mSendDelete     // Catch:{ all -> 0x00d4 }
                r18 = 0
                r13 = r0
                r16 = r11
                r17 = r3
                r12.cancelGroupChildrenLocked(r13, r14, r15, r16, r17, r18)     // Catch:{ all -> 0x00d4 }
                com.android.server.notification.NotificationManagerService r3 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x00d4 }
                r3.updateLightsLocked()     // Catch:{ all -> 0x00d4 }
                goto L_0x00d2
            L_0x00b3:
                int r3 = r1.mReason     // Catch:{ all -> 0x00d4 }
                r4 = 18
                if (r3 == r4) goto L_0x00d2
                com.android.server.notification.NotificationManagerService r3 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x00d4 }
                com.android.server.notification.SnoozeHelper r3 = r3.mSnoozeHelper     // Catch:{ all -> 0x00d4 }
                int r4 = r1.mUserId     // Catch:{ all -> 0x00d4 }
                java.lang.String r5 = r1.mPkg     // Catch:{ all -> 0x00d4 }
                java.lang.String r6 = r1.mTag     // Catch:{ all -> 0x00d4 }
                int r7 = r1.mId     // Catch:{ all -> 0x00d4 }
                boolean r3 = r3.cancel(r4, r5, r6, r7)     // Catch:{ all -> 0x00d4 }
                if (r3 == 0) goto L_0x00d2
                com.android.server.notification.NotificationManagerService r4 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x00d4 }
                r4.handleSavePolicyFile()     // Catch:{ all -> 0x00d4 }
            L_0x00d2:
                monitor-exit(r2)     // Catch:{ all -> 0x00d4 }
                return
            L_0x00d4:
                r0 = move-exception
                monitor-exit(r2)     // Catch:{ all -> 0x00d4 }
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.notification.NotificationManagerService.CancelNotificationRunnable.run():void");
        }
    }

    protected class EnqueueNotificationRunnable implements Runnable {
        private final NotificationRecord r;
        private final int userId;

        EnqueueNotificationRunnable(int userId2, NotificationRecord r2) {
            this.userId = userId2;
            this.r = r2;
        }

        public void run() {
            int enqueueStatus;
            synchronized (NotificationManagerService.mNotificationLock) {
                NotificationManagerService.this.mEnqueuedNotifications.add(this.r);
                NotificationManagerService.this.scheduleTimeoutLocked(this.r);
                StatusBarNotification n = this.r.sbn;
                if (NotificationManagerService.DBG) {
                    Slog.d(NotificationManagerService.TAG, "EnqueueNotificationRunnable.run for: " + n.getKey());
                }
                NotificationRecord old = NotificationManagerService.this.mNotificationsByKey.get(n.getKey());
                if (old != null) {
                    this.r.copyRankingInformation(old);
                }
                int callingUid = n.getUid();
                int callingPid = n.getInitialPid();
                Notification notification = n.getNotification();
                String pkg = n.getPackageName();
                int id = n.getId();
                String tag = n.getTag();
                NotificationManagerService.this.flagNotificationForBubbles(this.r, pkg, callingUid, old);
                NotificationManagerService.this.handleGroupedNotificationLocked(this.r, old, callingUid, callingPid);
                if (n.isGroup() && notification.isGroupChild()) {
                    NotificationManagerService.this.mSnoozeHelper.repostGroupSummary(pkg, this.r.getUserId(), n.getGroupKey());
                }
                if (!pkg.equals("com.android.providers.downloads") || Log.isLoggable("DownloadManager", 2)) {
                    if (old != null) {
                        enqueueStatus = 1;
                    } else {
                        enqueueStatus = 0;
                    }
                    EventLogTags.writeNotificationEnqueue(callingUid, callingPid, pkg, id, tag, this.userId, notification.toString(), enqueueStatus);
                }
                if (NotificationManagerService.this.mAssistants.isEnabled()) {
                    NotificationManagerService.this.mAssistants.onNotificationEnqueuedLocked(this.r);
                    NotificationManagerService.mHandler.postDelayed(new PostNotificationRunnable(this.r.getKey()), NotificationManagerService.DELAY_FOR_ASSISTANT_TIME);
                } else {
                    NotificationManagerService.mHandler.post(new PostNotificationRunnable(this.r.getKey()));
                }
            }
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mNotificationLock"})
    public boolean isPackageSuspendedLocked(NotificationRecord r) {
        return isPackageSuspendedForUser(r.sbn.getPackageName(), r.sbn.getUid());
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mNotificationLock"})
    public boolean isPackageDistractionRestrictionLocked(NotificationRecord r) {
        return isPackageDistractionRestrictionForUser(r.sbn.getPackageName(), r.sbn.getUid());
    }

    protected class PostNotificationRunnable implements Runnable {
        private final String key;

        PostNotificationRunnable(String key2) {
            this.key = key2;
        }

        /* Debug info: failed to restart local var, previous not found, register: 13 */
        /* JADX WARNING: Code restructure failed: missing block: B:20:0x0071, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:33:0x00af, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:81:0x0235, code lost:
            return;
         */
        /* JADX WARNING: Removed duplicated region for block: B:43:0x00ca A[Catch:{ all -> 0x0236 }] */
        /* JADX WARNING: Removed duplicated region for block: B:46:0x00f2 A[Catch:{ all -> 0x0236 }] */
        /* JADX WARNING: Removed duplicated region for block: B:47:0x010c A[Catch:{ all -> 0x0236 }] */
        /* JADX WARNING: Removed duplicated region for block: B:50:0x0150 A[Catch:{ all -> 0x0236 }] */
        /* JADX WARNING: Removed duplicated region for block: B:53:0x0172 A[Catch:{ all -> 0x0236 }] */
        /* JADX WARNING: Removed duplicated region for block: B:56:0x017f A[Catch:{ all -> 0x0236 }] */
        /* JADX WARNING: Removed duplicated region for block: B:66:0x01b2 A[Catch:{ all -> 0x0236 }] */
        /* JADX WARNING: Removed duplicated region for block: B:76:0x0212 A[Catch:{ all -> 0x0236 }] */
        /* JADX WARNING: Removed duplicated region for block: B:99:0x0234 A[EDGE_INSN: B:99:0x0234->B:80:0x0234 ?: BREAK  , SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
                r13 = this;
                java.lang.Object r0 = com.android.server.notification.NotificationManagerService.mNotificationLock
                monitor-enter(r0)
                r1 = 0
                com.android.server.notification.NotificationManagerService r2 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x0236 }
                java.util.ArrayList<com.android.server.notification.NotificationRecord> r2 = r2.mEnqueuedNotifications     // Catch:{ all -> 0x0236 }
                int r2 = r2.size()     // Catch:{ all -> 0x0236 }
                r3 = 0
            L_0x000d:
                if (r3 >= r2) goto L_0x002a
                com.android.server.notification.NotificationManagerService r4 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x0236 }
                java.util.ArrayList<com.android.server.notification.NotificationRecord> r4 = r4.mEnqueuedNotifications     // Catch:{ all -> 0x0236 }
                java.lang.Object r4 = r4.get(r3)     // Catch:{ all -> 0x0236 }
                com.android.server.notification.NotificationRecord r4 = (com.android.server.notification.NotificationRecord) r4     // Catch:{ all -> 0x0236 }
                java.lang.String r5 = r13.key     // Catch:{ all -> 0x0236 }
                java.lang.String r6 = r4.getKey()     // Catch:{ all -> 0x0236 }
                boolean r5 = java.util.Objects.equals(r5, r6)     // Catch:{ all -> 0x0236 }
                if (r5 == 0) goto L_0x0027
                r1 = r4
                goto L_0x002a
            L_0x0027:
                int r3 = r3 + 1
                goto L_0x000d
            L_0x002a:
                if (r1 != 0) goto L_0x0072
                java.lang.String r3 = "NotificationService"
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0236 }
                r4.<init>()     // Catch:{ all -> 0x0236 }
                java.lang.String r5 = "Cannot find enqueued record for key: "
                r4.append(r5)     // Catch:{ all -> 0x0236 }
                java.lang.String r5 = r13.key     // Catch:{ all -> 0x0236 }
                r4.append(r5)     // Catch:{ all -> 0x0236 }
                java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0236 }
                android.util.Slog.i(r3, r4)     // Catch:{ all -> 0x0236 }
                com.android.server.notification.NotificationManagerService r3 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x0264 }
                java.util.ArrayList<com.android.server.notification.NotificationRecord> r3 = r3.mEnqueuedNotifications     // Catch:{ all -> 0x0264 }
                int r3 = r3.size()     // Catch:{ all -> 0x0264 }
                r4 = 0
            L_0x004d:
                if (r4 >= r3) goto L_0x0070
                com.android.server.notification.NotificationManagerService r5 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x0264 }
                java.util.ArrayList<com.android.server.notification.NotificationRecord> r5 = r5.mEnqueuedNotifications     // Catch:{ all -> 0x0264 }
                java.lang.Object r5 = r5.get(r4)     // Catch:{ all -> 0x0264 }
                com.android.server.notification.NotificationRecord r5 = (com.android.server.notification.NotificationRecord) r5     // Catch:{ all -> 0x0264 }
                java.lang.String r6 = r13.key     // Catch:{ all -> 0x0264 }
                java.lang.String r7 = r5.getKey()     // Catch:{ all -> 0x0264 }
                boolean r6 = java.util.Objects.equals(r6, r7)     // Catch:{ all -> 0x0264 }
                if (r6 == 0) goto L_0x006d
                com.android.server.notification.NotificationManagerService r6 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x0264 }
                java.util.ArrayList<com.android.server.notification.NotificationRecord> r6 = r6.mEnqueuedNotifications     // Catch:{ all -> 0x0264 }
                r6.remove(r4)     // Catch:{ all -> 0x0264 }
                goto L_0x0070
            L_0x006d:
                int r4 = r4 + 1
                goto L_0x004d
            L_0x0070:
                monitor-exit(r0)     // Catch:{ all -> 0x0264 }
                return
            L_0x0072:
                com.android.server.notification.NotificationManagerService r3 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x0236 }
                boolean r3 = r3.isBlocked(r1)     // Catch:{ all -> 0x0236 }
                if (r3 == 0) goto L_0x00b0
                java.lang.String r3 = "NotificationService"
                java.lang.String r4 = "notification blocked by assistant request"
                android.util.Slog.i(r3, r4)     // Catch:{ all -> 0x0236 }
                com.android.server.notification.NotificationManagerService r3 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x0264 }
                java.util.ArrayList<com.android.server.notification.NotificationRecord> r3 = r3.mEnqueuedNotifications     // Catch:{ all -> 0x0264 }
                int r3 = r3.size()     // Catch:{ all -> 0x0264 }
                r4 = 0
            L_0x008b:
                if (r4 >= r3) goto L_0x00ae
                com.android.server.notification.NotificationManagerService r5 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x0264 }
                java.util.ArrayList<com.android.server.notification.NotificationRecord> r5 = r5.mEnqueuedNotifications     // Catch:{ all -> 0x0264 }
                java.lang.Object r5 = r5.get(r4)     // Catch:{ all -> 0x0264 }
                com.android.server.notification.NotificationRecord r5 = (com.android.server.notification.NotificationRecord) r5     // Catch:{ all -> 0x0264 }
                java.lang.String r6 = r13.key     // Catch:{ all -> 0x0264 }
                java.lang.String r7 = r5.getKey()     // Catch:{ all -> 0x0264 }
                boolean r6 = java.util.Objects.equals(r6, r7)     // Catch:{ all -> 0x0264 }
                if (r6 == 0) goto L_0x00ab
                com.android.server.notification.NotificationManagerService r6 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x0264 }
                java.util.ArrayList<com.android.server.notification.NotificationRecord> r6 = r6.mEnqueuedNotifications     // Catch:{ all -> 0x0264 }
                r6.remove(r4)     // Catch:{ all -> 0x0264 }
                goto L_0x00ae
            L_0x00ab:
                int r4 = r4 + 1
                goto L_0x008b
            L_0x00ae:
                monitor-exit(r0)     // Catch:{ all -> 0x0264 }
                return
            L_0x00b0:
                com.android.server.notification.NotificationManagerService r3 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x0236 }
                boolean r3 = r3.isPackageSuspendedLocked(r1)     // Catch:{ all -> 0x0236 }
                com.android.server.notification.NotificationManagerService r4 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x0236 }
                boolean r4 = r4.isPackageDistractionRestrictionLocked(r1)     // Catch:{ all -> 0x0236 }
                r5 = 1
                if (r3 != 0) goto L_0x00c4
                if (r4 == 0) goto L_0x00c2
                goto L_0x00c4
            L_0x00c2:
                r6 = 0
                goto L_0x00c5
            L_0x00c4:
                r6 = r5
            L_0x00c5:
                r1.setHidden(r6)     // Catch:{ all -> 0x0236 }
                if (r3 == 0) goto L_0x00d3
                com.android.server.notification.NotificationManagerService r6 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x0236 }
                com.android.server.notification.NotificationUsageStats r6 = r6.mUsageStats     // Catch:{ all -> 0x0236 }
                r6.registerSuspendedByAdmin(r1)     // Catch:{ all -> 0x0236 }
            L_0x00d3:
                com.android.server.notification.NotificationManagerService r6 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x0236 }
                android.util.ArrayMap<java.lang.String, com.android.server.notification.NotificationRecord> r6 = r6.mNotificationsByKey     // Catch:{ all -> 0x0236 }
                java.lang.String r7 = r13.key     // Catch:{ all -> 0x0236 }
                java.lang.Object r6 = r6.get(r7)     // Catch:{ all -> 0x0236 }
                com.android.server.notification.NotificationRecord r6 = (com.android.server.notification.NotificationRecord) r6     // Catch:{ all -> 0x0236 }
                android.service.notification.StatusBarNotification r7 = r1.sbn     // Catch:{ all -> 0x0236 }
                android.app.Notification r8 = r7.getNotification()     // Catch:{ all -> 0x0236 }
                com.android.server.notification.NotificationManagerService r9 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x0236 }
                java.lang.String r10 = r7.getKey()     // Catch:{ all -> 0x0236 }
                int r9 = r9.indexOfNotificationLocked(r10)     // Catch:{ all -> 0x0236 }
                r10 = 0
                if (r9 >= 0) goto L_0x010c
                com.android.server.notification.NotificationManagerService r5 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x0236 }
                java.util.ArrayList<com.android.server.notification.NotificationRecord> r5 = r5.mNotificationList     // Catch:{ all -> 0x0236 }
                r5.add(r1)     // Catch:{ all -> 0x0236 }
                com.android.server.notification.NotificationManagerService r5 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x0236 }
                com.android.server.notification.NotificationUsageStats r5 = r5.mUsageStats     // Catch:{ all -> 0x0236 }
                r5.registerPostedByApp(r1)     // Catch:{ all -> 0x0236 }
                com.android.server.notification.NotificationManagerService r5 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x0236 }
                boolean r5 = r5.isVisuallyInterruptive(r10, r1)     // Catch:{ all -> 0x0236 }
                r1.setInterruptive(r5)     // Catch:{ all -> 0x0236 }
                goto L_0x013f
            L_0x010c:
                com.android.server.notification.NotificationManagerService r11 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x0236 }
                java.util.ArrayList<com.android.server.notification.NotificationRecord> r11 = r11.mNotificationList     // Catch:{ all -> 0x0236 }
                java.lang.Object r11 = r11.get(r9)     // Catch:{ all -> 0x0236 }
                com.android.server.notification.NotificationRecord r11 = (com.android.server.notification.NotificationRecord) r11     // Catch:{ all -> 0x0236 }
                r6 = r11
                com.android.server.notification.NotificationManagerService r11 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x0236 }
                java.util.ArrayList<com.android.server.notification.NotificationRecord> r11 = r11.mNotificationList     // Catch:{ all -> 0x0236 }
                r11.set(r9, r1)     // Catch:{ all -> 0x0236 }
                com.android.server.notification.NotificationManagerService r11 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x0236 }
                com.android.server.notification.NotificationUsageStats r11 = r11.mUsageStats     // Catch:{ all -> 0x0236 }
                r11.registerUpdatedByApp(r1, r6)     // Catch:{ all -> 0x0236 }
                int r11 = r8.flags     // Catch:{ all -> 0x0236 }
                android.app.Notification r12 = r6.getNotification()     // Catch:{ all -> 0x0236 }
                int r12 = r12.flags     // Catch:{ all -> 0x0236 }
                r12 = r12 & 64
                r11 = r11 | r12
                r8.flags = r11     // Catch:{ all -> 0x0236 }
                r1.isUpdate = r5     // Catch:{ all -> 0x0236 }
                com.android.server.notification.NotificationManagerService r5 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x0236 }
                boolean r5 = r5.isVisuallyInterruptive(r6, r1)     // Catch:{ all -> 0x0236 }
                r1.setTextChanged(r5)     // Catch:{ all -> 0x0236 }
            L_0x013f:
                com.android.server.notification.NotificationManagerService r5 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x0236 }
                android.util.ArrayMap<java.lang.String, com.android.server.notification.NotificationRecord> r5 = r5.mNotificationsByKey     // Catch:{ all -> 0x0236 }
                java.lang.String r11 = r7.getKey()     // Catch:{ all -> 0x0236 }
                r5.put(r11, r1)     // Catch:{ all -> 0x0236 }
                int r5 = r8.flags     // Catch:{ all -> 0x0236 }
                r5 = r5 & 64
                if (r5 == 0) goto L_0x0156
                int r5 = r8.flags     // Catch:{ all -> 0x0236 }
                r5 = r5 | 34
                r8.flags = r5     // Catch:{ all -> 0x0236 }
            L_0x0156:
                com.android.server.notification.NotificationManagerService r5 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x0236 }
                com.android.server.notification.RankingHelper r5 = r5.mRankingHelper     // Catch:{ all -> 0x0236 }
                r5.extractSignals(r1)     // Catch:{ all -> 0x0236 }
                com.android.server.notification.NotificationManagerService r5 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x0236 }
                com.android.server.notification.RankingHelper r5 = r5.mRankingHelper     // Catch:{ all -> 0x0236 }
                com.android.server.notification.NotificationManagerService r11 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x0236 }
                java.util.ArrayList<com.android.server.notification.NotificationRecord> r11 = r11.mNotificationList     // Catch:{ all -> 0x0236 }
                r5.sort(r11)     // Catch:{ all -> 0x0236 }
                boolean r5 = r1.isHidden()     // Catch:{ all -> 0x0236 }
                if (r5 != 0) goto L_0x0179
                com.android.server.notification.NotificationManagerService r5 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x0236 }
                android.media.AudioManager r5 = r5.mAudioManager     // Catch:{ all -> 0x0236 }
                com.android.server.notification.NotificationManagerServiceInjector.calculateAudiblyAlerted(r5, r1)     // Catch:{ all -> 0x0236 }
            L_0x0179:
                android.graphics.drawable.Icon r5 = r8.getSmallIcon()     // Catch:{ all -> 0x0236 }
                if (r5 == 0) goto L_0x01b2
                if (r6 == 0) goto L_0x0183
                android.service.notification.StatusBarNotification r10 = r6.sbn     // Catch:{ all -> 0x0236 }
            L_0x0183:
                r5 = r10
                com.android.server.notification.NotificationManagerService r10 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x0236 }
                com.android.server.notification.NotificationManagerService$NotificationListeners r10 = r10.mListeners     // Catch:{ all -> 0x0236 }
                r10.notifyPostedLocked(r1, r6)     // Catch:{ all -> 0x0236 }
                if (r5 == 0) goto L_0x019d
                java.lang.String r10 = r5.getGroup()     // Catch:{ all -> 0x0236 }
                java.lang.String r11 = r7.getGroup()     // Catch:{ all -> 0x0236 }
                boolean r10 = java.util.Objects.equals(r10, r11)     // Catch:{ all -> 0x0236 }
                if (r10 != 0) goto L_0x01b1
            L_0x019d:
                com.android.server.notification.NotificationManagerService r10 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x0236 }
                boolean r10 = r10.isCritical(r1)     // Catch:{ all -> 0x0236 }
                if (r10 != 0) goto L_0x01b1
                com.android.server.notification.NotificationManagerService$WorkerHandler r10 = com.android.server.notification.NotificationManagerService.mHandler     // Catch:{ all -> 0x0236 }
                com.android.server.notification.NotificationManagerService$PostNotificationRunnable$1 r11 = new com.android.server.notification.NotificationManagerService$PostNotificationRunnable$1     // Catch:{ all -> 0x0236 }
                r11.<init>(r7)     // Catch:{ all -> 0x0236 }
                r10.post(r11)     // Catch:{ all -> 0x0236 }
            L_0x01b1:
                goto L_0x0202
            L_0x01b2:
                java.lang.String r5 = "NotificationService"
                java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x0236 }
                r10.<init>()     // Catch:{ all -> 0x0236 }
                java.lang.String r11 = "Not posting notification without small icon: "
                r10.append(r11)     // Catch:{ all -> 0x0236 }
                r10.append(r8)     // Catch:{ all -> 0x0236 }
                java.lang.String r10 = r10.toString()     // Catch:{ all -> 0x0236 }
                android.util.Slog.e(r5, r10)     // Catch:{ all -> 0x0236 }
                if (r6 == 0) goto L_0x01e8
                boolean r5 = r6.isCanceled     // Catch:{ all -> 0x0236 }
                if (r5 != 0) goto L_0x01e8
                com.android.server.notification.NotificationManagerService r5 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x0236 }
                com.android.server.notification.NotificationManagerService$NotificationListeners r5 = r5.mListeners     // Catch:{ all -> 0x0236 }
                r10 = 4
                android.service.notification.NotificationStats r11 = r1.getStats()     // Catch:{ all -> 0x0236 }
                r5.notifyRemovedLocked(r1, r10, r11)     // Catch:{ all -> 0x0236 }
                com.android.server.notification.NotificationManagerService$WorkerHandler r5 = com.android.server.notification.NotificationManagerService.mHandler     // Catch:{ all -> 0x0236 }
                com.android.server.notification.NotificationManagerService$PostNotificationRunnable$2 r10 = new com.android.server.notification.NotificationManagerService$PostNotificationRunnable$2     // Catch:{ all -> 0x0236 }
                r10.<init>(r7)     // Catch:{ all -> 0x0236 }
                r5.post(r10)     // Catch:{ all -> 0x0236 }
            L_0x01e8:
                java.lang.String r5 = "NotificationService"
                java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x0236 }
                r10.<init>()     // Catch:{ all -> 0x0236 }
                java.lang.String r11 = "WARNING: In a future release this will crash the app: "
                r10.append(r11)     // Catch:{ all -> 0x0236 }
                java.lang.String r11 = r7.getPackageName()     // Catch:{ all -> 0x0236 }
                r10.append(r11)     // Catch:{ all -> 0x0236 }
                java.lang.String r10 = r10.toString()     // Catch:{ all -> 0x0236 }
                android.util.Slog.e(r5, r10)     // Catch:{ all -> 0x0236 }
            L_0x0202:
                com.android.server.notification.NotificationManagerService r5 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x0236 }
                r5.maybeRecordInterruptionLocked(r1)     // Catch:{ all -> 0x0236 }
                com.android.server.notification.NotificationManagerService r1 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x0264 }
                java.util.ArrayList<com.android.server.notification.NotificationRecord> r1 = r1.mEnqueuedNotifications     // Catch:{ all -> 0x0264 }
                int r1 = r1.size()     // Catch:{ all -> 0x0264 }
                r2 = 0
            L_0x0210:
                if (r2 >= r1) goto L_0x0233
                com.android.server.notification.NotificationManagerService r3 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x0264 }
                java.util.ArrayList<com.android.server.notification.NotificationRecord> r3 = r3.mEnqueuedNotifications     // Catch:{ all -> 0x0264 }
                java.lang.Object r3 = r3.get(r2)     // Catch:{ all -> 0x0264 }
                com.android.server.notification.NotificationRecord r3 = (com.android.server.notification.NotificationRecord) r3     // Catch:{ all -> 0x0264 }
                java.lang.String r4 = r13.key     // Catch:{ all -> 0x0264 }
                java.lang.String r5 = r3.getKey()     // Catch:{ all -> 0x0264 }
                boolean r4 = java.util.Objects.equals(r4, r5)     // Catch:{ all -> 0x0264 }
                if (r4 == 0) goto L_0x0230
                com.android.server.notification.NotificationManagerService r4 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x0264 }
                java.util.ArrayList<com.android.server.notification.NotificationRecord> r4 = r4.mEnqueuedNotifications     // Catch:{ all -> 0x0264 }
                r4.remove(r2)     // Catch:{ all -> 0x0264 }
                goto L_0x0233
            L_0x0230:
                int r2 = r2 + 1
                goto L_0x0210
            L_0x0233:
                monitor-exit(r0)     // Catch:{ all -> 0x0264 }
                return
            L_0x0236:
                r1 = move-exception
                com.android.server.notification.NotificationManagerService r2 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x0264 }
                java.util.ArrayList<com.android.server.notification.NotificationRecord> r2 = r2.mEnqueuedNotifications     // Catch:{ all -> 0x0264 }
                int r2 = r2.size()     // Catch:{ all -> 0x0264 }
                r3 = 0
            L_0x0240:
                if (r3 >= r2) goto L_0x0262
                com.android.server.notification.NotificationManagerService r4 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x0264 }
                java.util.ArrayList<com.android.server.notification.NotificationRecord> r4 = r4.mEnqueuedNotifications     // Catch:{ all -> 0x0264 }
                java.lang.Object r4 = r4.get(r3)     // Catch:{ all -> 0x0264 }
                com.android.server.notification.NotificationRecord r4 = (com.android.server.notification.NotificationRecord) r4     // Catch:{ all -> 0x0264 }
                java.lang.String r5 = r13.key     // Catch:{ all -> 0x0264 }
                java.lang.String r6 = r4.getKey()     // Catch:{ all -> 0x0264 }
                boolean r5 = java.util.Objects.equals(r5, r6)     // Catch:{ all -> 0x0264 }
                if (r5 != 0) goto L_0x025b
                int r3 = r3 + 1
                goto L_0x0240
            L_0x025b:
                com.android.server.notification.NotificationManagerService r5 = com.android.server.notification.NotificationManagerService.this     // Catch:{ all -> 0x0264 }
                java.util.ArrayList<com.android.server.notification.NotificationRecord> r5 = r5.mEnqueuedNotifications     // Catch:{ all -> 0x0264 }
                r5.remove(r3)     // Catch:{ all -> 0x0264 }
            L_0x0262:
                throw r1     // Catch:{ all -> 0x0264 }
            L_0x0264:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0264 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.notification.NotificationManagerService.PostNotificationRunnable.run():void");
        }
    }

    /* access modifiers changed from: protected */
    @GuardedBy({"mNotificationLock"})
    @VisibleForTesting
    public boolean isVisuallyInterruptive(NotificationRecord old, NotificationRecord r) {
        NotificationRecord notificationRecord = old;
        NotificationRecord notificationRecord2 = r;
        if (notificationRecord2.sbn.isGroup() && notificationRecord2.sbn.getNotification().isGroupSummary()) {
            if (DEBUG_INTERRUPTIVENESS) {
                Slog.v(TAG, "INTERRUPTIVENESS: " + r.getKey() + " is not interruptive: summary");
            }
            return false;
        } else if (notificationRecord == null) {
            if (DEBUG_INTERRUPTIVENESS) {
                Slog.v(TAG, "INTERRUPTIVENESS: " + r.getKey() + " is interruptive: new notification");
            }
            return true;
        } else {
            Notification oldN = notificationRecord.sbn.getNotification();
            Notification newN = notificationRecord2.sbn.getNotification();
            if (oldN.extras == null || newN.extras == null) {
                if (DEBUG_INTERRUPTIVENESS) {
                    Slog.v(TAG, "INTERRUPTIVENESS: " + r.getKey() + " is not interruptive: no extras");
                }
                return false;
            } else if ((notificationRecord2.sbn.getNotification().flags & 64) != 0) {
                if (DEBUG_INTERRUPTIVENESS) {
                    Slog.v(TAG, "INTERRUPTIVENESS: " + r.getKey() + " is not interruptive: foreground service");
                }
                return false;
            } else {
                String oldTitle = String.valueOf(oldN.extras.get("android.title"));
                String newTitle = String.valueOf(newN.extras.get("android.title"));
                if (!Objects.equals(oldTitle, newTitle)) {
                    if (DEBUG_INTERRUPTIVENESS) {
                        Slog.v(TAG, "INTERRUPTIVENESS: " + r.getKey() + " is interruptive: changed title");
                        StringBuilder sb = new StringBuilder();
                        sb.append("INTERRUPTIVENESS: ");
                        sb.append(String.format("   old title: %s (%s@0x%08x)", new Object[]{oldTitle, oldTitle.getClass(), Integer.valueOf(oldTitle.hashCode())}));
                        Slog.v(TAG, sb.toString());
                        Slog.v(TAG, "INTERRUPTIVENESS: " + String.format("   new title: %s (%s@0x%08x)", new Object[]{newTitle, newTitle.getClass(), Integer.valueOf(newTitle.hashCode())}));
                    }
                    return true;
                }
                String oldText = String.valueOf(oldN.extras.get("android.text"));
                String newText = String.valueOf(newN.extras.get("android.text"));
                if (!Objects.equals(oldText, newText)) {
                    if (DEBUG_INTERRUPTIVENESS) {
                        Slog.v(TAG, "INTERRUPTIVENESS: " + r.getKey() + " is interruptive: changed text");
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("INTERRUPTIVENESS: ");
                        sb2.append(String.format("   old text: %s (%s@0x%08x)", new Object[]{oldText, oldText.getClass(), Integer.valueOf(oldText.hashCode())}));
                        Slog.v(TAG, sb2.toString());
                        Slog.v(TAG, "INTERRUPTIVENESS: " + String.format("   new text: %s (%s@0x%08x)", new Object[]{newText, newText.getClass(), Integer.valueOf(newText.hashCode())}));
                    }
                    return true;
                } else if (oldN.hasCompletedProgress() != newN.hasCompletedProgress()) {
                    if (DEBUG_INTERRUPTIVENESS) {
                        Slog.v(TAG, "INTERRUPTIVENESS: " + r.getKey() + " is interruptive: completed progress");
                    }
                    return true;
                } else if (Notification.areActionsVisiblyDifferent(oldN, newN)) {
                    if (DEBUG_INTERRUPTIVENESS) {
                        Slog.v(TAG, "INTERRUPTIVENESS: " + r.getKey() + " is interruptive: changed actions");
                    }
                    return true;
                } else {
                    try {
                        Notification.Builder oldB = Notification.Builder.recoverBuilder(getContext(), oldN);
                        Notification.Builder newB = Notification.Builder.recoverBuilder(getContext(), newN);
                        if (Notification.areStyledNotificationsVisiblyDifferent(oldB, newB)) {
                            if (DEBUG_INTERRUPTIVENESS) {
                                Slog.v(TAG, "INTERRUPTIVENESS: " + r.getKey() + " is interruptive: styles differ");
                            }
                            return true;
                        }
                        if (Notification.areRemoteViewsChanged(oldB, newB)) {
                            if (DEBUG_INTERRUPTIVENESS) {
                                Slog.v(TAG, "INTERRUPTIVENESS: " + r.getKey() + " is interruptive: remoteviews differ");
                            }
                            return true;
                        }
                        return false;
                    } catch (Exception e) {
                        Slog.w(TAG, "error recovering builder", e);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean isCritical(NotificationRecord record) {
        return record.getCriticality() < 2;
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mNotificationLock"})
    public void handleGroupedNotificationLocked(NotificationRecord r, NotificationRecord old, int callingUid, int callingPid) {
        NotificationRecord removedSummary;
        NotificationRecord notificationRecord = r;
        NotificationRecord notificationRecord2 = old;
        StatusBarNotification sbn = notificationRecord.sbn;
        Notification n = sbn.getNotification();
        if (n.isGroupSummary() && !sbn.isAppGroup()) {
            n.flags &= -513;
        }
        String group = sbn.getGroupKey();
        boolean isSummary = n.isGroupSummary();
        String str = null;
        Notification oldN = notificationRecord2 != null ? notificationRecord2.sbn.getNotification() : null;
        if (notificationRecord2 != null) {
            str = notificationRecord2.sbn.getGroupKey();
        }
        String oldGroup = str;
        boolean oldIsSummary = notificationRecord2 != null && oldN.isGroupSummary();
        if (oldIsSummary && (removedSummary = this.mSummaryByGroupKey.remove(oldGroup)) != notificationRecord2) {
            Slog.w(TAG, "Removed summary didn't match old notification: old=" + old.getKey() + ", removed=" + (removedSummary != null ? removedSummary.getKey() : "<null>"));
        }
        if (isSummary) {
            this.mSummaryByGroupKey.put(group, notificationRecord);
        }
        if (!oldIsSummary) {
            return;
        }
        if (!isSummary || !oldGroup.equals(group)) {
            cancelGroupChildrenLocked(old, callingUid, callingPid, (String) null, false, (FlagChecker) null);
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mNotificationLock"})
    @VisibleForTesting
    public void scheduleTimeoutLocked(NotificationRecord record) {
        if (record.getNotification().getTimeoutAfter() > 0) {
            this.mAlarmManager.setExactAndAllowWhileIdle(2, SystemClock.elapsedRealtime() + record.getNotification().getTimeoutAfter(), PendingIntent.getBroadcast(getContext(), 1, new Intent(ACTION_NOTIFICATION_TIMEOUT).setData(new Uri.Builder().scheme(SCHEME_TIMEOUT).appendPath(record.getKey()).build()).addFlags(268435456).putExtra(EXTRA_KEY, record.getKey()), 134217728));
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x015c  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x00ca  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x00cc  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x00d0 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x00de  */
    @com.android.internal.annotations.GuardedBy({"mNotificationLock"})
    @com.android.internal.annotations.VisibleForTesting
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void buzzBeepBlinkLocked(com.android.server.notification.NotificationRecord r26) {
        /*
            r25 = this;
            r0 = r25
            r1 = r26
            boolean r2 = r0.mIsAutomotive
            if (r2 == 0) goto L_0x000d
            boolean r2 = r0.mNotificationEffectsEnabledForAutomotive
            if (r2 != 0) goto L_0x000d
            return
        L_0x000d:
            r2 = 0
            r3 = 0
            r4 = 0
            android.service.notification.StatusBarNotification r5 = r1.sbn
            android.app.Notification r5 = r5.getNotification()
            java.lang.String r6 = r26.getKey()
            boolean r7 = r0.mIsAutomotive
            r8 = 3
            r10 = 1
            if (r7 == 0) goto L_0x002a
            int r7 = r26.getImportance()
            if (r7 <= r8) goto L_0x0028
            r7 = r10
            goto L_0x0033
        L_0x0028:
            r7 = 0
            goto L_0x0033
        L_0x002a:
            int r7 = r26.getImportance()
            if (r7 < r8) goto L_0x0032
            r7 = r10
            goto L_0x0033
        L_0x0032:
            r7 = 0
        L_0x0033:
            boolean r8 = r26.isIntercepted()
            if (r8 != 0) goto L_0x0040
            boolean r8 = android.provider.MiuiSettings.SilenceMode.isSupported
            if (r8 == 0) goto L_0x0040
            r8 = r10
            goto L_0x0041
        L_0x0040:
            r8 = 0
        L_0x0041:
            if (r6 == 0) goto L_0x004d
            java.lang.String r11 = r0.mSoundNotificationKey
            boolean r11 = r6.equals(r11)
            if (r11 == 0) goto L_0x004d
            r11 = r10
            goto L_0x004e
        L_0x004d:
            r11 = 0
        L_0x004e:
            if (r6 == 0) goto L_0x005a
            java.lang.String r12 = r0.mVibrateNotificationKey
            boolean r12 = r6.equals(r12)
            if (r12 == 0) goto L_0x005a
            r12 = r10
            goto L_0x005b
        L_0x005a:
            r12 = 0
        L_0x005b:
            r13 = 0
            r14 = 0
            r15 = 0
            boolean r9 = r1.isUpdate
            if (r9 != 0) goto L_0x0072
            int r9 = r26.getImportance()
            if (r9 <= r10) goto L_0x0072
            android.service.notification.StatusBarNotification r9 = r1.sbn
            java.lang.String r9 = r9.getPackageName()
            r0.sendAccessibilityEvent(r5, r9)
            r15 = 1
        L_0x0072:
            java.lang.String r9 = "NotificationService"
            if (r7 == 0) goto L_0x0176
            boolean r17 = r25.isNotificationForCurrentUser(r26)
            if (r17 == 0) goto L_0x0176
            boolean r10 = r0.mSystemReady
            if (r10 == 0) goto L_0x016c
            android.media.AudioManager r10 = r0.mAudioManager
            if (r10 == 0) goto L_0x016c
            android.net.Uri r10 = r26.getSound()
            if (r10 == 0) goto L_0x0096
            r18 = r2
            android.net.Uri r2 = android.net.Uri.EMPTY
            boolean r2 = r2.equals(r10)
            if (r2 != 0) goto L_0x0098
            r2 = 1
            goto L_0x0099
        L_0x0096:
            r18 = r2
        L_0x0098:
            r2 = 0
        L_0x0099:
            r14 = r2
            long[] r2 = r26.getVibration()
            if (r2 != 0) goto L_0x00c2
            if (r14 == 0) goto L_0x00c2
            r19 = r2
            android.media.AudioManager r2 = r0.mAudioManager
            int r2 = r2.getRingerModeInternal()
            r20 = r3
            r3 = 1
            if (r2 != r3) goto L_0x00c6
            android.media.AudioManager r2 = r0.mAudioManager
            android.media.AudioAttributes r3 = r26.getAudioAttributes()
            int r3 = android.media.AudioAttributes.toLegacyStreamType(r3)
            int r2 = r2.getStreamVolume(r3)
            if (r2 != 0) goto L_0x00c6
            long[] r2 = r0.mFallbackVibrationPattern
            goto L_0x00c8
        L_0x00c2:
            r19 = r2
            r20 = r3
        L_0x00c6:
            r2 = r19
        L_0x00c8:
            if (r2 == 0) goto L_0x00cc
            r3 = 1
            goto L_0x00cd
        L_0x00cc:
            r3 = 0
        L_0x00cd:
            r13 = r3
            if (r14 != 0) goto L_0x00d5
            if (r13 == 0) goto L_0x00d3
            goto L_0x00d5
        L_0x00d3:
            r3 = 0
            goto L_0x00d6
        L_0x00d5:
            r3 = 1
        L_0x00d6:
            if (r3 == 0) goto L_0x015c
            boolean r19 = r25.shouldMuteNotificationLocked(r26)
            if (r19 != 0) goto L_0x015c
            if (r15 != 0) goto L_0x00ee
            r19 = r3
            android.service.notification.StatusBarNotification r3 = r1.sbn
            java.lang.String r3 = r3.getPackageName()
            r0.sendAccessibilityEvent(r5, r3)
            r3 = 1
            r15 = r3
            goto L_0x00f0
        L_0x00ee:
            r19 = r3
        L_0x00f0:
            boolean r3 = DBG
            if (r3 == 0) goto L_0x00f9
            java.lang.String r3 = "Interrupting!"
            android.util.Slog.v(r9, r3)
        L_0x00f9:
            if (r14 == 0) goto L_0x0111
            if (r8 == 0) goto L_0x0111
            r0.mSoundNotificationKey = r6
            boolean r3 = r0.mInCall
            if (r3 == 0) goto L_0x0108
            r25.playInCallNotification()
            r3 = 1
            goto L_0x010c
        L_0x0108:
            boolean r3 = r0.playSound(r1, r10)
        L_0x010c:
            if (r3 == 0) goto L_0x0113
            r0.mSoundNotificationKey = r6
            goto L_0x0113
        L_0x0111:
            r3 = r20
        L_0x0113:
            r20 = r3
            android.media.AudioManager r3 = r0.mAudioManager
            int r3 = r3.getRingerModeInternal()
            if (r3 != 0) goto L_0x011f
            r3 = 1
            goto L_0x0120
        L_0x011f:
            r3 = 0
        L_0x0120:
            r21 = r4
            android.media.AudioManager r4 = r0.mAudioManager
            int r4 = r4.getRingerMode()
            if (r4 != 0) goto L_0x012c
            r4 = 1
            goto L_0x012d
        L_0x012c:
            r4 = 0
        L_0x012d:
            r22 = r5
            boolean r5 = r0.mInCall
            if (r5 != 0) goto L_0x0156
            if (r13 == 0) goto L_0x0156
            if (r3 != 0) goto L_0x0156
            if (r4 != 0) goto L_0x0156
            android.content.Context r5 = r25.getContext()
            r23 = r3
            android.service.notification.StatusBarNotification r3 = r1.sbn
            r24 = r4
            java.lang.String r4 = "_vibrate"
            boolean r3 = miui.util.NotificationFilterHelper.isAllowed(r5, r3, r4)
            if (r3 == 0) goto L_0x015a
            boolean r3 = r0.playVibration(r1, r2, r14)
            if (r3 == 0) goto L_0x0153
            r0.mVibrateNotificationKey = r6
        L_0x0153:
            r18 = r3
            goto L_0x015a
        L_0x0156:
            r23 = r3
            r24 = r4
        L_0x015a:
            r4 = 4
            goto L_0x017f
        L_0x015c:
            r19 = r3
            r21 = r4
            r22 = r5
            int r3 = r26.getFlags()
            r4 = 4
            r3 = r3 & r4
            if (r3 == 0) goto L_0x017f
            r14 = 0
            goto L_0x017f
        L_0x016c:
            r18 = r2
            r20 = r3
            r21 = r4
            r22 = r5
            r4 = 4
            goto L_0x017f
        L_0x0176:
            r18 = r2
            r20 = r3
            r21 = r4
            r22 = r5
            r4 = 4
        L_0x017f:
            if (r11 == 0) goto L_0x0186
            if (r14 != 0) goto L_0x0186
            r25.clearSoundLocked()
        L_0x0186:
            if (r12 == 0) goto L_0x018d
            if (r13 != 0) goto L_0x018d
            r25.clearVibrateLocked()
        L_0x018d:
            java.util.ArrayList<java.lang.String> r2 = r0.mLights
            boolean r2 = r2.remove(r6)
            boolean r3 = r0.canShowLightsLocked(r1, r7)
            if (r3 == 0) goto L_0x01ac
            java.util.ArrayList<java.lang.String> r3 = r0.mLights
            r3.add(r6)
            r25.updateLightsLocked()
            boolean r3 = r0.mUseAttentionLight
            if (r3 == 0) goto L_0x01aa
            com.android.server.lights.Light r3 = r0.mAttentionLight
            r3.pulse()
        L_0x01aa:
            r3 = 1
            goto L_0x01b3
        L_0x01ac:
            if (r2 == 0) goto L_0x01b1
            r25.updateLightsLocked()
        L_0x01b1:
            r3 = r21
        L_0x01b3:
            if (r18 != 0) goto L_0x01bd
            if (r20 != 0) goto L_0x01bd
            if (r3 == 0) goto L_0x01ba
            goto L_0x01bd
        L_0x01ba:
            r5 = 1
            goto L_0x0253
        L_0x01bd:
            android.service.notification.StatusBarNotification r5 = r1.sbn
            boolean r5 = r5.isGroup()
            java.lang.String r10 = "INTERRUPTIVENESS: "
            if (r5 == 0) goto L_0x01f6
            android.service.notification.StatusBarNotification r5 = r1.sbn
            android.app.Notification r5 = r5.getNotification()
            boolean r5 = r5.isGroupSummary()
            if (r5 == 0) goto L_0x01f6
            boolean r5 = DEBUG_INTERRUPTIVENESS
            if (r5 == 0) goto L_0x01f4
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r10)
            java.lang.String r10 = r26.getKey()
            r5.append(r10)
            java.lang.String r10 = " is not interruptive: summary"
            r5.append(r10)
            java.lang.String r5 = r5.toString()
            android.util.Slog.v(r9, r5)
            r5 = 1
            goto L_0x0219
        L_0x01f4:
            r5 = 1
            goto L_0x0219
        L_0x01f6:
            boolean r5 = DEBUG_INTERRUPTIVENESS
            if (r5 == 0) goto L_0x0215
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r10)
            java.lang.String r10 = r26.getKey()
            r5.append(r10)
            java.lang.String r10 = " is interruptive: alerted"
            r5.append(r10)
            java.lang.String r5 = r5.toString()
            android.util.Slog.v(r9, r5)
        L_0x0215:
            r5 = 1
            r1.setInterruptive(r5)
        L_0x0219:
            android.metrics.LogMaker r9 = r26.getLogMaker()
            r10 = 199(0xc7, float:2.79E-43)
            android.metrics.LogMaker r9 = r9.setCategory(r10)
            android.metrics.LogMaker r9 = r9.setType(r5)
            if (r18 == 0) goto L_0x022b
            r10 = r5
            goto L_0x022c
        L_0x022b:
            r10 = 0
        L_0x022c:
            if (r20 == 0) goto L_0x0231
            r16 = 2
            goto L_0x0233
        L_0x0231:
            r16 = 0
        L_0x0233:
            r10 = r10 | r16
            if (r3 == 0) goto L_0x0238
            goto L_0x0239
        L_0x0238:
            r4 = 0
        L_0x0239:
            r4 = r4 | r10
            android.metrics.LogMaker r4 = r9.setSubtype(r4)
            com.android.internal.logging.MetricsLogger.action(r4)
            if (r18 == 0) goto L_0x0245
            r4 = r5
            goto L_0x0246
        L_0x0245:
            r4 = 0
        L_0x0246:
            if (r20 == 0) goto L_0x024a
            r9 = r5
            goto L_0x024b
        L_0x024a:
            r9 = 0
        L_0x024b:
            if (r3 == 0) goto L_0x024f
            r10 = r5
            goto L_0x0250
        L_0x024f:
            r10 = 0
        L_0x0250:
            com.android.server.EventLogTags.writeNotificationAlert(r6, r4, r9, r10)
        L_0x0253:
            if (r18 != 0) goto L_0x0259
            if (r20 == 0) goto L_0x0258
            goto L_0x0259
        L_0x0258:
            r5 = 0
        L_0x0259:
            r1.setAudiblyAlerted(r5)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.notification.NotificationManagerService.buzzBeepBlinkLocked(com.android.server.notification.NotificationRecord):void");
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mNotificationLock"})
    public boolean canShowLightsLocked(NotificationRecord record, boolean aboveThreshold) {
        if (!this.mHasLight || !this.mNotificationPulseEnabled || record.getLight() == null || !aboveThreshold || (record.getSuppressedVisualEffects() & 8) != 0) {
            return false;
        }
        Notification notification = record.getNotification();
        if (record.isUpdate && (notification.flags & 8) != 0) {
            return false;
        }
        if ((!record.sbn.isGroup() || !record.getNotification().suppressAlertingDueToGrouping()) && !this.mInCall) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mNotificationLock"})
    public boolean shouldMuteNotificationLocked(NotificationRecord record) {
        Notification notification = record.getNotification();
        if (record.isUpdate && (notification.flags & 8) != 0) {
            return true;
        }
        String disableEffects = disableNotificationEffects(record);
        if (disableEffects != null) {
            ZenLog.traceDisableEffects(record, disableEffects);
            return true;
        } else if (record.isIntercepted() && !MiuiSettings.SilenceMode.isSupported) {
            return true;
        } else {
            if (record.sbn.isGroup() && notification.suppressAlertingDueToGrouping()) {
                return true;
            }
            if (!this.mUsageStats.isAlertRateLimited(record.sbn.getPackageName())) {
                return false;
            }
            Slog.e(TAG, "Muting recently noisy " + record.getKey());
            return true;
        }
    }

    private boolean playSound(NotificationRecord record, Uri soundUri) {
        boolean looping = (record.getNotification().flags & 4) != 0;
        if (!((this.mAudioManager.getRingerModeInternal() == 1 || this.mAudioManager.getRingerModeInternal() == 0) && this.mAudioManager.getStreamVolume(AudioAttributes.toLegacyStreamType(record.getAudioAttributes())) == 0) && !QuietUtils.checkQuiet(5, 0, record.sbn.getPackageName(), record.sbn.getNotification().extraNotification.getTargetPkg()) && NotificationFilterHelper.isAllowed(getContext(), record.sbn, "_sound")) {
            long identity = Binder.clearCallingIdentity();
            try {
                IRingtonePlayer player = this.mAudioManager.getRingtonePlayer();
                if (player != null) {
                    if (DBG) {
                        Slog.v(TAG, "Playing sound " + soundUri + " with attributes " + record.getAudioAttributes());
                    }
                    player.playAsync(soundUri, record.sbn.getUser(), looping, record.getAudioAttributes());
                    Binder.restoreCallingIdentity(identity);
                    return true;
                }
            } catch (RemoteException e) {
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
            Binder.restoreCallingIdentity(identity);
        }
        return false;
    }

    private boolean playVibration(NotificationRecord record, long[] vibration, boolean delayVibForSound) {
        long identity = Binder.clearCallingIdentity();
        try {
            VibrationEffect effect = VibrationEffect.createWaveform(vibration, (record.getNotification().flags & 4) != 0 ? 0 : -1);
            if (delayVibForSound) {
                new Thread(new Runnable(record, effect) {
                    private final /* synthetic */ NotificationRecord f$1;
                    private final /* synthetic */ VibrationEffect f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        NotificationManagerService.this.lambda$playVibration$3$NotificationManagerService(this.f$1, this.f$2);
                    }
                }).start();
            } else {
                this.mVibrator.vibrate(record.sbn.getUid(), NotificationManagerServiceInjector.getPlayVibrationPkg(record.sbn.getPackageName(), record.sbn.getOpPkg(), record.sbn.getPackageName()), effect, "Notification", record.getAudioAttributes());
            }
            return true;
        } catch (IllegalArgumentException e) {
            Slog.e(TAG, "Error creating vibration waveform with pattern: " + Arrays.toString(vibration));
            return false;
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    public /* synthetic */ void lambda$playVibration$3$NotificationManagerService(NotificationRecord record, VibrationEffect effect) {
        int waitMs = this.mAudioManager.getFocusRampTimeMs(3, record.getAudioAttributes());
        if (DBG) {
            Slog.v(TAG, "Delaying vibration by " + waitMs + "ms");
        }
        try {
            Thread.sleep((long) waitMs);
        } catch (InterruptedException e) {
        }
        synchronized (mNotificationLock) {
            if (this.mNotificationsByKey.get(record.getKey()) != null) {
                this.mVibrator.vibrate(record.sbn.getUid(), NotificationManagerServiceInjector.getPlayVibrationPkg(record.sbn.getPackageName(), record.sbn.getOpPkg(), record.sbn.getOpPkg()), effect, "Notification (delayed)", record.getAudioAttributes());
            } else {
                Slog.e(TAG, "No vibration for canceled notification : " + record.getKey());
            }
        }
    }

    /* JADX INFO: finally extract failed */
    private boolean isNotificationForCurrentUser(NotificationRecord record) {
        long token = Binder.clearCallingIdentity();
        try {
            int currentUser = ActivityManager.getCurrentUser();
            Binder.restoreCallingIdentity(token);
            return record.getUserId() == -1 || record.getUserId() == currentUser || this.mUserProfiles.isCurrentProfile(record.getUserId());
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(token);
            throw th;
        }
    }

    /* access modifiers changed from: protected */
    public void playInCallNotification() {
        if (this.mAudioManager.getRingerModeInternal() == 2 && Settings.Secure.getInt(getContext().getContentResolver(), "in_call_notification_enabled", 1) != 0) {
            new Thread() {
                public void run() {
                    long identity = Binder.clearCallingIdentity();
                    try {
                        IRingtonePlayer player = NotificationManagerService.this.mAudioManager.getRingtonePlayer();
                        if (player != null) {
                            if (NotificationManagerService.this.mCallNotificationToken != null) {
                                player.stop(NotificationManagerService.this.mCallNotificationToken);
                            }
                            Binder unused = NotificationManagerService.this.mCallNotificationToken = new Binder();
                            player.play(NotificationManagerService.this.mCallNotificationToken, NotificationManagerService.this.mInCallNotificationUri, NotificationManagerService.this.mInCallNotificationAudioAttributes, NotificationManagerService.this.mInCallNotificationVolume, false);
                        }
                    } catch (RemoteException e) {
                    } catch (Throwable player2) {
                        Binder.restoreCallingIdentity(identity);
                        throw player2;
                    }
                    Binder.restoreCallingIdentity(identity);
                }
            }.start();
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mToastQueue"})
    public void showNextToastLocked() {
        ToastRecord record = this.mToastQueue.get(0);
        while (record != null) {
            if (DBG) {
                Slog.d(TAG, "Show pkg=" + record.pkg + " callback=" + record.callback);
            }
            try {
                record.callback.show(record.token);
                scheduleDurationReachedLocked(record);
                return;
            } catch (RemoteException e) {
                Slog.w(TAG, "Object died trying to show notification " + record.callback + " in package " + record.pkg);
                int index = this.mToastQueue.indexOf(record);
                if (index >= 0) {
                    this.mToastQueue.remove(index);
                }
                keepProcessAliveIfNeededLocked(record.pid);
                if (this.mToastQueue.size() > 0) {
                    record = this.mToastQueue.get(0);
                } else {
                    record = null;
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mToastQueue"})
    public void cancelToastLocked(int index) {
        ToastRecord record = this.mToastQueue.get(index);
        try {
            record.callback.hide();
        } catch (RemoteException e) {
            Slog.w(TAG, "Object died trying to hide notification " + record.callback + " in package " + record.pkg);
        }
        ToastRecord lastToast = this.mToastQueue.remove(index);
        this.mWindowManagerInternal.removeWindowToken(lastToast.token, false, lastToast.displayId);
        scheduleKillTokenTimeout(lastToast);
        keepProcessAliveIfNeededLocked(record.pid);
        if (this.mToastQueue.size() > 0) {
            showNextToastLocked();
        }
    }

    /* access modifiers changed from: package-private */
    public void finishTokenLocked(IBinder t, int displayId) {
        mHandler.removeCallbacksAndMessages(t);
        this.mWindowManagerInternal.removeWindowToken(t, true, displayId);
    }

    @GuardedBy({"mToastQueue"})
    private void scheduleDurationReachedLocked(ToastRecord r) {
        mHandler.removeCallbacksAndMessages(r);
        mHandler.sendMessageDelayed(Message.obtain(mHandler, 2, r), (long) this.mAccessibilityManager.getRecommendedTimeoutMillis(r.duration == 1 ? 3500 : SHORT_DELAY, 2));
    }

    /* access modifiers changed from: private */
    public void handleDurationReached(ToastRecord record) {
        if (DBG) {
            Slog.d(TAG, "Timeout pkg=" + record.pkg + " callback=" + record.callback);
        }
        synchronized (this.mToastQueue) {
            int index = indexOfToastLocked(record.pkg, record.callback);
            if (index >= 0) {
                cancelToastLocked(index);
            }
        }
    }

    @GuardedBy({"mToastQueue"})
    private void scheduleKillTokenTimeout(ToastRecord r) {
        mHandler.removeCallbacksAndMessages(r);
        mHandler.sendMessageDelayed(Message.obtain(mHandler, 7, r), 11000);
    }

    /* access modifiers changed from: private */
    public void handleKillTokenTimeout(ToastRecord record) {
        if (DBG) {
            Slog.d(TAG, "Kill Token Timeout token=" + record.token);
        }
        synchronized (this.mToastQueue) {
            finishTokenLocked(record.token, record.displayId);
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mToastQueue"})
    public int indexOfToastLocked(String pkg, ITransientNotification callback) {
        IBinder cbak = callback.asBinder();
        ArrayList<ToastRecord> list = this.mToastQueue;
        int len = list.size();
        for (int i = 0; i < len; i++) {
            ToastRecord r = list.get(i);
            if (r.pkg.equals(pkg) && r.callback.asBinder() == cbak) {
                return i;
            }
        }
        return -1;
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mToastQueue"})
    public void keepProcessAliveIfNeededLocked(int pid) {
        int toastCount = 0;
        ArrayList<ToastRecord> list = this.mToastQueue;
        int N = list.size();
        for (int i = 0; i < N; i++) {
            if (list.get(i).pid == pid) {
                toastCount++;
            }
        }
        try {
            this.mAm.setProcessImportant(this.mForegroundToken, pid, toastCount > 0, "toast");
        } catch (RemoteException e) {
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0068, code lost:
        if (r9 == false) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x006a, code lost:
        mHandler.scheduleSendRankingUpdate();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void handleRankingReconsideration(android.os.Message r13) {
        /*
            r12 = this;
            java.lang.Object r0 = r13.obj
            boolean r0 = r0 instanceof com.android.server.notification.RankingReconsideration
            if (r0 != 0) goto L_0x0007
            return
        L_0x0007:
            java.lang.Object r0 = r13.obj
            com.android.server.notification.RankingReconsideration r0 = (com.android.server.notification.RankingReconsideration) r0
            r0.run()
            java.lang.Object r1 = mNotificationLock
            monitor-enter(r1)
            android.util.ArrayMap<java.lang.String, com.android.server.notification.NotificationRecord> r2 = r12.mNotificationsByKey     // Catch:{ all -> 0x0070 }
            java.lang.String r3 = r0.getKey()     // Catch:{ all -> 0x0070 }
            java.lang.Object r2 = r2.get(r3)     // Catch:{ all -> 0x0070 }
            com.android.server.notification.NotificationRecord r2 = (com.android.server.notification.NotificationRecord) r2     // Catch:{ all -> 0x0070 }
            if (r2 != 0) goto L_0x0021
            monitor-exit(r1)     // Catch:{ all -> 0x0070 }
            return
        L_0x0021:
            int r3 = r12.findNotificationRecordIndexLocked(r2)     // Catch:{ all -> 0x0070 }
            boolean r4 = r2.isIntercepted()     // Catch:{ all -> 0x0070 }
            int r5 = r2.getPackageVisibilityOverride()     // Catch:{ all -> 0x0070 }
            r0.applyChangesLocked(r2)     // Catch:{ all -> 0x0070 }
            r12.applyZenModeLocked(r2)     // Catch:{ all -> 0x0070 }
            com.android.server.notification.RankingHelper r6 = r12.mRankingHelper     // Catch:{ all -> 0x0070 }
            java.util.ArrayList<com.android.server.notification.NotificationRecord> r7 = r12.mNotificationList     // Catch:{ all -> 0x0070 }
            r6.sort(r7)     // Catch:{ all -> 0x0070 }
            int r6 = r12.findNotificationRecordIndexLocked(r2)     // Catch:{ all -> 0x0070 }
            boolean r7 = r2.isIntercepted()     // Catch:{ all -> 0x0070 }
            int r8 = r2.getPackageVisibilityOverride()     // Catch:{ all -> 0x0070 }
            if (r3 != r6) goto L_0x004f
            if (r4 != r7) goto L_0x004f
            if (r5 == r8) goto L_0x004d
            goto L_0x004f
        L_0x004d:
            r9 = 0
            goto L_0x0050
        L_0x004f:
            r9 = 1
        L_0x0050:
            if (r4 == 0) goto L_0x0067
            if (r7 != 0) goto L_0x0067
            long r10 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x0070 }
            boolean r10 = r2.isNewEnoughForAlerting(r10)     // Catch:{ all -> 0x0070 }
            if (r10 == 0) goto L_0x0067
            boolean r10 = r2.getStatusBarCheckedImportance()     // Catch:{ all -> 0x0070 }
            if (r10 == 0) goto L_0x0067
            r12.buzzBeepBlinkLocked(r2)     // Catch:{ all -> 0x0070 }
        L_0x0067:
            monitor-exit(r1)     // Catch:{ all -> 0x0070 }
            if (r9 == 0) goto L_0x006f
            com.android.server.notification.NotificationManagerService$WorkerHandler r1 = mHandler
            r1.scheduleSendRankingUpdate()
        L_0x006f:
            return
        L_0x0070:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0070 }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.notification.NotificationManagerService.handleRankingReconsideration(android.os.Message):void");
    }

    /* access modifiers changed from: package-private */
    public void handleRankingSort() {
        NotificationManagerService notificationManagerService = this;
        if (notificationManagerService.mRankingHelper != null) {
            synchronized (mNotificationLock) {
                try {
                    int N = notificationManagerService.mNotificationList.size();
                    ArrayList<String> orderBefore = new ArrayList<>(N);
                    int[] visibilities = new int[N];
                    boolean[] showBadges = new boolean[N];
                    boolean[] allowBubbles = new boolean[N];
                    ArrayList<NotificationChannel> channelBefore = new ArrayList<>(N);
                    ArrayList<String> groupKeyBefore = new ArrayList<>(N);
                    ArrayList<ArrayList<String>> overridePeopleBefore = new ArrayList<>(N);
                    ArrayList<ArrayList<SnoozeCriterion>> snoozeCriteriaBefore = new ArrayList<>(N);
                    ArrayList<Integer> userSentimentBefore = new ArrayList<>(N);
                    ArrayList<Integer> suppressVisuallyBefore = new ArrayList<>(N);
                    ArrayList<ArrayList<Notification.Action>> systemSmartActionsBefore = new ArrayList<>(N);
                    ArrayList<ArrayList<CharSequence>> smartRepliesBefore = new ArrayList<>(N);
                    int[] importancesBefore = new int[N];
                    int i = 0;
                    while (i < N) {
                        int N2 = N;
                        NotificationRecord r = notificationManagerService.mNotificationList.get(i);
                        orderBefore.add(r.getKey());
                        visibilities[i] = r.getPackageVisibilityOverride();
                        showBadges[i] = r.canShowBadge();
                        allowBubbles[i] = r.canBubble();
                        channelBefore.add(r.getChannel());
                        groupKeyBefore.add(r.getGroupKey());
                        overridePeopleBefore.add(r.getPeopleOverride());
                        snoozeCriteriaBefore.add(r.getSnoozeCriteria());
                        userSentimentBefore.add(Integer.valueOf(r.getUserSentiment()));
                        suppressVisuallyBefore.add(Integer.valueOf(r.getSuppressedVisualEffects()));
                        systemSmartActionsBefore.add(r.getSystemGeneratedSmartActions());
                        smartRepliesBefore.add(r.getSmartReplies());
                        importancesBefore[i] = r.getImportance();
                        notificationManagerService = this;
                        ArrayList<ArrayList<CharSequence>> smartRepliesBefore2 = smartRepliesBefore;
                        notificationManagerService.mRankingHelper.extractSignals(r);
                        i++;
                        N = N2;
                        smartRepliesBefore = smartRepliesBefore2;
                    }
                    int N3 = N;
                    ArrayList<ArrayList<CharSequence>> smartRepliesBefore3 = smartRepliesBefore;
                    notificationManagerService.mRankingHelper.sort(notificationManagerService.mNotificationList);
                    int i2 = 0;
                    while (true) {
                        int N4 = N3;
                        if (i2 < N4) {
                            NotificationRecord r2 = notificationManagerService.mNotificationList.get(i2);
                            ArrayList<String> orderBefore2 = orderBefore;
                            if (!orderBefore.get(i2).equals(r2.getKey()) || visibilities[i2] != r2.getPackageVisibilityOverride() || showBadges[i2] != r2.canShowBadge() || allowBubbles[i2] != r2.canBubble() || !Objects.equals(channelBefore.get(i2), r2.getChannel()) || !Objects.equals(groupKeyBefore.get(i2), r2.getGroupKey()) || !Objects.equals(overridePeopleBefore.get(i2), r2.getPeopleOverride()) || !Objects.equals(snoozeCriteriaBefore.get(i2), r2.getSnoozeCriteria()) || !Objects.equals(userSentimentBefore.get(i2), Integer.valueOf(r2.getUserSentiment())) || !Objects.equals(suppressVisuallyBefore.get(i2), Integer.valueOf(r2.getSuppressedVisualEffects())) || !Objects.equals(systemSmartActionsBefore.get(i2), r2.getSystemGeneratedSmartActions())) {
                                break;
                            }
                            ArrayList<ArrayList<CharSequence>> smartRepliesBefore4 = smartRepliesBefore3;
                            smartRepliesBefore3 = smartRepliesBefore4;
                            if (!Objects.equals(smartRepliesBefore4.get(i2), r2.getSmartReplies())) {
                                break;
                            } else if (importancesBefore[i2] != r2.getImportance()) {
                                break;
                            } else {
                                i2++;
                                notificationManagerService = this;
                                orderBefore = orderBefore2;
                                N3 = N4;
                            }
                        } else {
                            return;
                        }
                    }
                    mHandler.scheduleSendRankingUpdate();
                } catch (Throwable th) {
                    th = th;
                    throw th;
                }
            }
        }
    }

    @GuardedBy({"mNotificationLock"})
    private void recordCallerLocked(NotificationRecord record) {
        if (this.mZenModeHelper.isCall(record)) {
            this.mZenModeHelper.recordCaller(record);
        }
    }

    @GuardedBy({"mNotificationLock"})
    private void applyZenModeLocked(NotificationRecord record) {
        record.setIntercepted(this.mZenModeHelper.shouldIntercept(record));
        if (record.isIntercepted()) {
            record.setSuppressedVisualEffects(this.mZenModeHelper.getConsolidatedNotificationPolicy().suppressedVisualEffects);
        } else {
            record.setSuppressedVisualEffects(0);
        }
    }

    @GuardedBy({"mNotificationLock"})
    private int findNotificationRecordIndexLocked(NotificationRecord target) {
        return this.mRankingHelper.indexOf(this.mNotificationList, target);
    }

    /* access modifiers changed from: private */
    public void handleSendRankingUpdate() {
        synchronized (mNotificationLock) {
            this.mListeners.notifyRankingUpdateLocked((List<NotificationRecord>) null);
        }
    }

    private void scheduleListenerHintsChanged(int state) {
        mHandler.removeMessages(5);
        mHandler.obtainMessage(5, state, 0).sendToTarget();
    }

    private void scheduleInterruptionFilterChanged(int listenerInterruptionFilter) {
        mHandler.removeMessages(6);
        mHandler.obtainMessage(6, listenerInterruptionFilter, 0).sendToTarget();
    }

    /* access modifiers changed from: private */
    public void handleListenerHintsChanged(int hints) {
        synchronized (mNotificationLock) {
            this.mListeners.notifyListenerHintsChangedLocked(hints);
        }
    }

    /* access modifiers changed from: private */
    public void handleListenerInterruptionFilterChanged(int interruptionFilter) {
        synchronized (mNotificationLock) {
            this.mListeners.notifyInterruptionFilterChanged(interruptionFilter);
        }
    }

    /* access modifiers changed from: private */
    public void handleOnPackageChanged(boolean removingPackage, int changeUserId, String[] pkgList, int[] uidList) {
        this.mListeners.onPackagesChanged(removingPackage, pkgList, uidList);
        this.mAssistants.onPackagesChanged(removingPackage, pkgList, uidList);
        this.mConditionProviders.onPackagesChanged(removingPackage, pkgList, uidList);
        if (removingPackage || this.mPreferencesHelper.onPackagesChanged(removingPackage, changeUserId, pkgList, uidList)) {
            handleSavePolicyFile();
        }
    }

    protected class WorkerHandler extends Handler {
        public WorkerHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 2:
                    NotificationManagerService.this.handleDurationReached((ToastRecord) msg.obj);
                    return;
                case 4:
                    NotificationManagerService.this.handleSendRankingUpdate();
                    return;
                case 5:
                    NotificationManagerService.this.handleListenerHintsChanged(msg.arg1);
                    return;
                case 6:
                    NotificationManagerService.this.handleListenerInterruptionFilterChanged(msg.arg1);
                    return;
                case 7:
                    NotificationManagerService.this.handleKillTokenTimeout((ToastRecord) msg.obj);
                    return;
                case 8:
                    SomeArgs args = (SomeArgs) msg.obj;
                    NotificationManagerService.this.handleOnPackageChanged(((Boolean) args.arg1).booleanValue(), args.argi1, (String[]) args.arg2, (int[]) args.arg3);
                    args.recycle();
                    return;
                default:
                    return;
            }
        }

        /* access modifiers changed from: protected */
        public void scheduleSendRankingUpdate() {
            if (!hasMessages(4)) {
                sendMessage(Message.obtain(this, 4));
            }
        }

        /* access modifiers changed from: protected */
        public void scheduleCancelNotification(CancelNotificationRunnable cancelRunnable) {
            if (!hasCallbacks(cancelRunnable)) {
                sendMessage(Message.obtain(this, cancelRunnable));
            }
        }

        /* access modifiers changed from: protected */
        public void scheduleOnPackageChanged(boolean removingPackage, int changeUserId, String[] pkgList, int[] uidList) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = Boolean.valueOf(removingPackage);
            args.argi1 = changeUserId;
            args.arg2 = pkgList;
            args.arg3 = uidList;
            sendMessage(Message.obtain(this, 8, args));
        }
    }

    private final class RankingHandlerWorker extends Handler implements RankingHandler {
        public RankingHandlerWorker(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i == 1000) {
                NotificationManagerService.this.handleRankingReconsideration(msg);
            } else if (i == 1001) {
                NotificationManagerService.this.handleRankingSort();
            }
        }

        public void requestSort() {
            removeMessages(1001);
            Message msg = Message.obtain();
            msg.what = 1001;
            sendMessage(msg);
        }

        public void requestReconsideration(RankingReconsideration recon) {
            sendMessageDelayed(Message.obtain(this, 1000, recon), recon.getDelay(TimeUnit.MILLISECONDS));
        }
    }

    static int clamp(int x, int low, int high) {
        if (x < low) {
            return low;
        }
        return x > high ? high : x;
    }

    /* access modifiers changed from: package-private */
    public void sendAccessibilityEvent(Notification notification, CharSequence packageName) {
        if (this.mAccessibilityManager.isEnabled()) {
            AccessibilityEvent event = AccessibilityEvent.obtain(64);
            event.setPackageName(packageName);
            event.setClassName(Notification.class.getName());
            event.setParcelableData(notification);
            CharSequence tickerText = notification.tickerText;
            if (!TextUtils.isEmpty(tickerText)) {
                event.getText().add(tickerText);
            }
            this.mAccessibilityManager.sendAccessibilityEvent(event);
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mNotificationLock"})
    public boolean removeFromNotificationListsLocked(NotificationRecord r) {
        boolean wasPosted = false;
        NotificationRecord findNotificationByListLocked = findNotificationByListLocked(this.mNotificationList, r.getKey());
        NotificationRecord recordInList = findNotificationByListLocked;
        if (findNotificationByListLocked != null) {
            this.mNotificationList.remove(recordInList);
            this.mNotificationsByKey.remove(recordInList.sbn.getKey());
            wasPosted = true;
        }
        while (true) {
            NotificationRecord findNotificationByListLocked2 = findNotificationByListLocked(this.mEnqueuedNotifications, r.getKey());
            NotificationRecord recordInList2 = findNotificationByListLocked2;
            if (findNotificationByListLocked2 == null) {
                return wasPosted;
            }
            this.mEnqueuedNotifications.remove(recordInList2);
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mNotificationLock"})
    public void cancelNotificationLocked(NotificationRecord r, boolean sendDelete, int reason, boolean wasPosted, String listenerName) {
        cancelNotificationLocked(r, sendDelete, reason, -1, -1, wasPosted, listenerName);
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mNotificationLock"})
    public void cancelNotificationLocked(NotificationRecord r, boolean sendDelete, int reason, int rank, int count, boolean wasPosted, String listenerName) {
        PendingIntent deleteIntent;
        final NotificationRecord notificationRecord = r;
        int i = reason;
        String canceledKey = r.getKey();
        recordCallerLocked(r);
        if (r.getStats().getDismissalSurface() == -1) {
            notificationRecord.recordDismissalSurface(0);
        }
        if (sendDelete && (deleteIntent = r.getNotification().deleteIntent) != null) {
            try {
                ((ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class)).clearPendingIntentAllowBgActivityStarts(deleteIntent.getTarget(), WHITELIST_TOKEN);
                deleteIntent.send();
            } catch (PendingIntent.CanceledException ex) {
                Slog.w(TAG, "canceled PendingIntent for " + notificationRecord.sbn.getPackageName(), ex);
            }
        }
        if (wasPosted) {
            if (r.getNotification().getSmallIcon() != null) {
                if (i != 18) {
                    notificationRecord.isCanceled = true;
                }
                this.mListeners.notifyRemovedLocked(notificationRecord, i, r.getStats());
                mHandler.post(new Runnable() {
                    public void run() {
                        NotificationManagerService.this.mGroupHelper.onNotificationRemoved(notificationRecord.sbn);
                    }
                });
            }
            if (canceledKey.equals(this.mSoundNotificationKey)) {
                this.mSoundNotificationKey = null;
                long identity = Binder.clearCallingIdentity();
                try {
                    IRingtonePlayer player = this.mAudioManager.getRingtonePlayer();
                    if (player != null) {
                        player.stopAsync();
                    }
                } catch (RemoteException e) {
                } catch (Throwable player2) {
                    Binder.restoreCallingIdentity(identity);
                    throw player2;
                }
                Binder.restoreCallingIdentity(identity);
            }
            if (canceledKey.equals(this.mVibrateNotificationKey)) {
                this.mVibrateNotificationKey = null;
                long identity2 = Binder.clearCallingIdentity();
                try {
                    this.mVibrator.cancel();
                } finally {
                    Binder.restoreCallingIdentity(identity2);
                }
            }
            this.mLights.remove(canceledKey);
        }
        if (!(i == 2 || i == 3)) {
            switch (i) {
                case 8:
                case 9:
                    this.mUsageStats.registerRemovedByApp(notificationRecord);
                    break;
                case 10:
                case 11:
                    break;
            }
        }
        this.mUsageStats.registerDismissedByUser(notificationRecord);
        String groupKey = r.getGroupKey();
        NotificationRecord groupSummary = this.mSummaryByGroupKey.get(groupKey);
        if (groupSummary != null && groupSummary.getKey().equals(canceledKey)) {
            this.mSummaryByGroupKey.remove(groupKey);
        }
        ArrayMap<String, String> summaries = this.mAutobundledSummaries.get(Integer.valueOf(notificationRecord.sbn.getUserId()));
        if (summaries != null && notificationRecord.sbn.getKey().equals(summaries.get(notificationRecord.sbn.getPackageName()))) {
            summaries.remove(notificationRecord.sbn.getPackageName());
        }
        this.mArchive.record(notificationRecord.sbn);
        long now = System.currentTimeMillis();
        LogMaker logMaker = r.getItemLogMaker().setType(5).setSubtype(i);
        if (rank == -1) {
            int i2 = count;
        } else if (count != -1) {
            logMaker.addTaggedData(798, Integer.valueOf(rank)).addTaggedData(1395, Integer.valueOf(count));
        }
        MetricsLogger.action(logMaker);
        long j = now;
        EventLogTags.writeNotificationCanceled(canceledKey, reason, notificationRecord.getLifespanMs(now), notificationRecord.getFreshnessMs(now), notificationRecord.getExposureMs(now), rank, count, listenerName);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void updateUriPermissions(NotificationRecord newRecord, NotificationRecord oldRecord, String targetPkg, int targetUserId) {
        IBinder permissionOwner;
        NotificationRecord notificationRecord = newRecord;
        NotificationRecord notificationRecord2 = oldRecord;
        String key = notificationRecord != null ? newRecord.getKey() : oldRecord.getKey();
        if (DBG) {
            Slog.d(TAG, key + ": updating permissions");
        }
        ArraySet<Uri> newUris = notificationRecord != null ? newRecord.getGrantableUris() : null;
        ArraySet<Uri> oldUris = notificationRecord2 != null ? oldRecord.getGrantableUris() : null;
        if (newUris != null || oldUris != null) {
            IBinder permissionOwner2 = null;
            if (notificationRecord != null && 0 == 0) {
                permissionOwner2 = notificationRecord.permissionOwner;
            }
            if (notificationRecord2 != null && permissionOwner2 == null) {
                permissionOwner2 = notificationRecord2.permissionOwner;
            }
            if (newUris != null && permissionOwner2 == null) {
                if (DBG) {
                    Slog.d(TAG, key + ": creating owner");
                }
                permissionOwner2 = this.mUgmInternal.newUriPermissionOwner("NOTIF:" + key);
            }
            if (newUris != null || permissionOwner2 == null) {
                permissionOwner = permissionOwner2;
            } else {
                long ident = Binder.clearCallingIdentity();
                try {
                    if (DBG) {
                        Slog.d(TAG, key + ": destroying owner");
                    }
                    this.mUgmInternal.revokeUriPermissionFromOwner(permissionOwner2, (Uri) null, -1, UserHandle.getUserId(oldRecord.getUid()));
                    permissionOwner = null;
                } finally {
                    Binder.restoreCallingIdentity(ident);
                }
            }
            if (!(newUris == null || permissionOwner == null)) {
                for (int i = 0; i < newUris.size(); i++) {
                    Uri uri = newUris.valueAt(i);
                    if (oldUris == null || !oldUris.contains(uri)) {
                        if (DBG) {
                            Slog.d(TAG, key + ": granting " + uri);
                        }
                        grantUriPermission(permissionOwner, uri, newRecord.getUid(), targetPkg, targetUserId);
                    }
                }
            }
            if (!(oldUris == null || permissionOwner == null)) {
                for (int i2 = 0; i2 < oldUris.size(); i2++) {
                    Uri uri2 = oldUris.valueAt(i2);
                    if (newUris == null || !newUris.contains(uri2)) {
                        if (DBG) {
                            Slog.d(TAG, key + ": revoking " + uri2);
                        }
                        revokeUriPermission(permissionOwner, uri2, oldRecord.getUid());
                    }
                }
            }
            if (notificationRecord != null) {
                notificationRecord.permissionOwner = permissionOwner;
            }
        }
    }

    private void grantUriPermission(IBinder owner, Uri uri, int sourceUid, String targetPkg, int targetUserId) {
        if (uri != null && ActivityTaskManagerInternal.ASSIST_KEY_CONTENT.equals(uri.getScheme())) {
            long ident = Binder.clearCallingIdentity();
            try {
                this.mUgm.grantUriPermissionFromOwner(owner, sourceUid, targetPkg, ContentProvider.getUriWithoutUserId(uri), 1, ContentProvider.getUserIdFromUri(uri, UserHandle.getUserId(sourceUid)), targetUserId);
            } catch (RemoteException e) {
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(ident);
                throw th;
            }
            Binder.restoreCallingIdentity(ident);
        }
    }

    private void revokeUriPermission(IBinder owner, Uri uri, int sourceUid) {
        if (uri != null && ActivityTaskManagerInternal.ASSIST_KEY_CONTENT.equals(uri.getScheme())) {
            long ident = Binder.clearCallingIdentity();
            try {
                this.mUgmInternal.revokeUriPermissionFromOwner(owner, ContentProvider.getUriWithoutUserId(uri), 1, ContentProvider.getUserIdFromUri(uri, UserHandle.getUserId(sourceUid)));
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void cancelNotification(int callingUid, int callingPid, String pkg, String tag, int id, int mustHaveFlags, int mustNotHaveFlags, boolean sendDelete, int userId, int reason, ManagedServices.ManagedServiceInfo listener) {
        cancelNotification(callingUid, callingPid, pkg, tag, id, mustHaveFlags, mustNotHaveFlags, sendDelete, userId, reason, -1, -1, listener);
    }

    /* access modifiers changed from: package-private */
    public void cancelNotification(int callingUid, int callingPid, String pkg, String tag, int id, int mustHaveFlags, int mustNotHaveFlags, boolean sendDelete, int userId, int reason, int rank, int count, ManagedServices.ManagedServiceInfo listener) {
        WorkerHandler workerHandler = mHandler;
        CancelNotificationRunnable cancelNotificationRunnable = r1;
        CancelNotificationRunnable cancelNotificationRunnable2 = new CancelNotificationRunnable(callingUid, callingPid, pkg, tag, id, mustHaveFlags, mustNotHaveFlags, sendDelete, userId, reason, rank, count, listener);
        workerHandler.scheduleCancelNotification(cancelNotificationRunnable);
    }

    private boolean notificationMatchesUserId(NotificationRecord r, int userId) {
        return userId == -1 || r.getUserId() == -1 || r.getUserId() == userId;
    }

    private boolean notificationMatchesCurrentProfiles(NotificationRecord r, int userId) {
        return notificationMatchesUserId(r, userId) || this.mUserProfiles.isCurrentProfile(r.getUserId());
    }

    /* access modifiers changed from: package-private */
    public void cancelAllNotificationsInt(int callingUid, int callingPid, String pkg, String channelId, int mustHaveFlags, int mustNotHaveFlags, boolean doit, int userId, int reason, ManagedServices.ManagedServiceInfo listener) {
        final ManagedServices.ManagedServiceInfo managedServiceInfo = listener;
        final int i = callingUid;
        final int i2 = callingPid;
        final String str = pkg;
        final int i3 = userId;
        final int i4 = mustHaveFlags;
        final int i5 = mustNotHaveFlags;
        final int i6 = reason;
        final boolean z = doit;
        final String str2 = channelId;
        mHandler.post(new Runnable() {
            public void run() {
                ManagedServices.ManagedServiceInfo managedServiceInfo = managedServiceInfo;
                String listenerName = managedServiceInfo == null ? null : managedServiceInfo.component.toShortString();
                EventLogTags.writeNotificationCancelAll(i, i2, str, i3, i4, i5, i6, listenerName);
                if (z) {
                    synchronized (NotificationManagerService.mNotificationLock) {
                        FlagChecker flagChecker = new FlagChecker(i4, i5) {
                            private final /* synthetic */ int f$0;
                            private final /* synthetic */ int f$1;

                            {
                                this.f$0 = r1;
                                this.f$1 = r2;
                            }

                            public final boolean apply(int i) {
                                return NotificationManagerService.AnonymousClass14.lambda$run$0(this.f$0, this.f$1, i);
                            }
                        };
                        NotificationManagerService.this.cancelAllNotificationsByListLocked(NotificationManagerService.this.mNotificationList, i, i2, str, true, str2, flagChecker, false, i3, false, i6, listenerName, true);
                        NotificationManagerService.this.cancelAllNotificationsByListLocked(NotificationManagerService.this.mEnqueuedNotifications, i, i2, str, true, str2, flagChecker, false, i3, false, i6, listenerName, false);
                        NotificationManagerService.this.mSnoozeHelper.cancel(i3, str);
                    }
                }
            }

            static /* synthetic */ boolean lambda$run$0(int mustHaveFlags, int mustNotHaveFlags, int flags) {
                if ((flags & mustHaveFlags) == mustHaveFlags && (flags & mustNotHaveFlags) == 0) {
                    return true;
                }
                return false;
            }
        });
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mNotificationLock"})
    public void cancelAllNotificationsByListLocked(ArrayList<NotificationRecord> notificationList, int callingUid, int callingPid, String pkg, boolean nullPkgIndicatesUserSwitch, String channelId, FlagChecker flagChecker, boolean includeCurrentProfiles, int userId, boolean sendDelete, int reason, String listenerName, boolean wasPosted) {
        ArrayList<NotificationRecord> arrayList = notificationList;
        String str = pkg;
        String str2 = channelId;
        int i = userId;
        ArrayList<NotificationRecord> canceledNotifications = null;
        int i2 = notificationList.size() - 1;
        while (i2 >= 0) {
            NotificationRecord r = arrayList.get(i2);
            if (includeCurrentProfiles) {
                if (!notificationMatchesCurrentProfiles(r, i)) {
                    FlagChecker flagChecker2 = flagChecker;
                    i2--;
                }
            } else if (!notificationMatchesUserId(r, i)) {
                FlagChecker flagChecker3 = flagChecker;
                i2--;
            }
            if (nullPkgIndicatesUserSwitch && str == null && r.getUserId() == -1) {
                FlagChecker flagChecker4 = flagChecker;
                i2--;
            } else {
                if (flagChecker.apply(r.getFlags()) && ((str == null || r.sbn.getPackageName().equals(str)) && (str2 == null || str2.equals(r.getChannel().getId())))) {
                    if (canceledNotifications == null) {
                        canceledNotifications = new ArrayList<>();
                    }
                    arrayList.remove(i2);
                    this.mNotificationsByKey.remove(r.getKey());
                    r.recordDismissalSentiment(1);
                    canceledNotifications.add(r);
                    cancelNotificationLocked(r, sendDelete, reason, wasPosted, listenerName);
                }
                i2--;
            }
        }
        FlagChecker flagChecker5 = flagChecker;
        if (canceledNotifications != null) {
            int M = canceledNotifications.size();
            for (int i3 = 0; i3 < M; i3++) {
                cancelGroupChildrenLocked(canceledNotifications.get(i3), callingUid, callingPid, listenerName, false, flagChecker);
            }
            updateLightsLocked();
        }
    }

    /* access modifiers changed from: package-private */
    public void snoozeNotificationInt(String key, long duration, String snoozeCriterionId, ManagedServices.ManagedServiceInfo listener) {
        String listenerName = listener == null ? null : listener.component.toShortString();
        if ((duration > 0 || snoozeCriterionId != null) && key != null) {
            if (DBG) {
                Slog.d(TAG, String.format("snooze event(%s, %d, %s, %s)", new Object[]{key, Long.valueOf(duration), snoozeCriterionId, listenerName}));
            }
            mHandler.post(new SnoozeNotificationRunnable(key, duration, snoozeCriterionId));
        }
    }

    /* access modifiers changed from: package-private */
    public void unsnoozeNotificationInt(String key, ManagedServices.ManagedServiceInfo listener) {
        String listenerName = listener == null ? null : listener.component.toShortString();
        if (DBG) {
            Slog.d(TAG, String.format("unsnooze event(%s, %s)", new Object[]{key, listenerName}));
        }
        this.mSnoozeHelper.repost(key);
        handleSavePolicyFile();
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mNotificationLock"})
    public void cancelAllLocked(int callingUid, int callingPid, int userId, int reason, ManagedServices.ManagedServiceInfo listener, boolean includeCurrentProfiles) {
        final ManagedServices.ManagedServiceInfo managedServiceInfo = listener;
        final int i = callingUid;
        final int i2 = callingPid;
        final int i3 = userId;
        final int i4 = reason;
        final boolean z = includeCurrentProfiles;
        mHandler.post(new Runnable() {
            public void run() {
                synchronized (NotificationManagerService.mNotificationLock) {
                    String listenerName = managedServiceInfo == null ? null : managedServiceInfo.component.toShortString();
                    EventLogTags.writeNotificationCancelAll(i, i2, (String) null, i3, 0, 0, i4, listenerName);
                    FlagChecker flagChecker = new FlagChecker(i4) {
                        private final /* synthetic */ int f$0;

                        {
                            this.f$0 = r1;
                        }

                        public final boolean apply(int i) {
                            return NotificationManagerService.AnonymousClass15.lambda$run$0(this.f$0, i);
                        }
                    };
                    NotificationManagerService.this.cancelAllNotificationsByListLocked(NotificationManagerService.this.mNotificationList, i, i2, (String) null, false, (String) null, flagChecker, z, i3, true, i4, listenerName, true);
                    NotificationManagerService.this.cancelAllNotificationsByListLocked(NotificationManagerService.this.mEnqueuedNotifications, i, i2, (String) null, false, (String) null, flagChecker, z, i3, true, i4, listenerName, false);
                    NotificationManagerService.this.mSnoozeHelper.cancel(i3, z);
                }
            }

            static /* synthetic */ boolean lambda$run$0(int reason, int flags) {
                int flagsToCheck = 34;
                if (11 == reason) {
                    flagsToCheck = 34 | 4096;
                }
                if ((flags & flagsToCheck) != 0) {
                    return false;
                }
                return true;
            }
        });
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mNotificationLock"})
    public void cancelGroupChildrenLocked(NotificationRecord r, int callingUid, int callingPid, String listenerName, boolean sendDelete, FlagChecker flagChecker) {
        if (r.getNotification().isGroupSummary()) {
            if (r.sbn.getPackageName() != null) {
                NotificationRecord notificationRecord = r;
                int i = callingUid;
                int i2 = callingPid;
                String str = listenerName;
                boolean z = sendDelete;
                FlagChecker flagChecker2 = flagChecker;
                cancelGroupChildrenByListLocked(this.mNotificationList, notificationRecord, i, i2, str, z, true, flagChecker2);
                cancelGroupChildrenByListLocked(this.mEnqueuedNotifications, notificationRecord, i, i2, str, z, false, flagChecker2);
            } else if (DBG) {
                Slog.e(TAG, "No package for group summary: " + r.getKey());
            }
        }
    }

    @GuardedBy({"mNotificationLock"})
    private void cancelGroupChildrenByListLocked(ArrayList<NotificationRecord> notificationList, NotificationRecord parentNotification, int callingUid, int callingPid, String listenerName, boolean sendDelete, boolean wasPosted, FlagChecker flagChecker) {
        int i;
        ArrayList<NotificationRecord> arrayList = notificationList;
        FlagChecker flagChecker2 = flagChecker;
        String pkg = parentNotification.sbn.getPackageName();
        int userId = parentNotification.getUserId();
        int i2 = notificationList.size() - 1;
        while (i2 >= 0) {
            NotificationRecord childR = arrayList.get(i2);
            StatusBarNotification childSbn = childR.sbn;
            if (!childSbn.isGroup() || childSbn.getNotification().isGroupSummary()) {
                StatusBarNotification statusBarNotification = childSbn;
                NotificationRecord notificationRecord = childR;
                i = i2;
            } else if (!childR.getGroupKey().equals(parentNotification.getGroupKey())) {
                StatusBarNotification statusBarNotification2 = childSbn;
                NotificationRecord notificationRecord2 = childR;
                i = i2;
            } else if ((childR.getFlags() & 64) != 0) {
                StatusBarNotification statusBarNotification3 = childSbn;
                NotificationRecord notificationRecord3 = childR;
                i = i2;
            } else if (flagChecker2 == null || flagChecker2.apply(childR.getFlags())) {
                StatusBarNotification statusBarNotification4 = childSbn;
                NotificationRecord childR2 = childR;
                i = i2;
                EventLogTags.writeNotificationCancel(callingUid, callingPid, pkg, childSbn.getId(), childSbn.getTag(), userId, 0, 0, 12, listenerName);
                arrayList.remove(i);
                this.mNotificationsByKey.remove(childR2.getKey());
                cancelNotificationLocked(childR2, sendDelete, 12, wasPosted, listenerName);
            } else {
                i = i2;
            }
            i2 = i - 1;
            flagChecker2 = flagChecker;
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mNotificationLock"})
    public void updateLightsLocked() {
        NotificationRecord ledNotification = null;
        while (ledNotification == null && !this.mLights.isEmpty()) {
            ArrayList<String> arrayList = this.mLights;
            String owner = arrayList.get(arrayList.size() - 1);
            ledNotification = this.mNotificationsByKey.get(owner);
            if (ledNotification == null) {
                Slog.wtfStack(TAG, "LED Notification does not exist: " + owner);
                this.mLights.remove(owner);
            }
        }
        if (ledNotification == null || this.mInCall || this.mScreenOn || !NotificationFilterHelper.isAllowed(getContext(), ledNotification.sbn, "_led")) {
            this.mNotificationLight.turnOff();
            return;
        }
        NotificationRecord.Light light = ledNotification.getLight();
        if (light != null && this.mNotificationPulseEnabled) {
            this.mNotificationLight.setFlashing(light.color, 1, light.onMs, light.offMs);
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mNotificationLock"})
    public List<NotificationRecord> findGroupNotificationsLocked(String pkg, String groupKey, int userId) {
        List<NotificationRecord> records = new ArrayList<>();
        records.addAll(findGroupNotificationByListLocked(this.mNotificationList, pkg, groupKey, userId));
        records.addAll(findGroupNotificationByListLocked(this.mEnqueuedNotifications, pkg, groupKey, userId));
        return records;
    }

    @GuardedBy({"mNotificationLock"})
    private List<NotificationRecord> findGroupNotificationByListLocked(ArrayList<NotificationRecord> list, String pkg, String groupKey, int userId) {
        List<NotificationRecord> records = new ArrayList<>();
        int len = list.size();
        for (int i = 0; i < len; i++) {
            NotificationRecord r = list.get(i);
            if (notificationMatchesUserId(r, userId) && r.getGroupKey().equals(groupKey) && r.sbn.getPackageName().equals(pkg)) {
                records.add(r);
            }
        }
        return records;
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mNotificationLock"})
    public NotificationRecord findNotificationByKeyLocked(String key) {
        NotificationRecord findNotificationByListLocked = findNotificationByListLocked(this.mNotificationList, key);
        NotificationRecord r = findNotificationByListLocked;
        if (findNotificationByListLocked != null) {
            return r;
        }
        NotificationRecord findNotificationByListLocked2 = findNotificationByListLocked(this.mEnqueuedNotifications, key);
        NotificationRecord r2 = findNotificationByListLocked2;
        if (findNotificationByListLocked2 != null) {
            return r2;
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mNotificationLock"})
    public NotificationRecord findNotificationLocked(String pkg, String tag, int id, int userId) {
        NotificationRecord findNotificationByListLocked = findNotificationByListLocked(this.mNotificationList, pkg, tag, id, userId);
        NotificationRecord r = findNotificationByListLocked;
        if (findNotificationByListLocked != null) {
            return r;
        }
        NotificationRecord findNotificationByListLocked2 = findNotificationByListLocked(this.mEnqueuedNotifications, pkg, tag, id, userId);
        NotificationRecord r2 = findNotificationByListLocked2;
        if (findNotificationByListLocked2 != null) {
            return r2;
        }
        return null;
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mNotificationLock"})
    public NotificationRecord findNotificationByListLocked(ArrayList<NotificationRecord> list, String pkg, String tag, int id, int userId) {
        int len = list.size();
        for (int i = 0; i < len; i++) {
            NotificationRecord r = list.get(i);
            if (notificationMatchesUserId(r, userId) && r.sbn.getId() == id && TextUtils.equals(r.sbn.getTag(), tag) && r.sbn.getPackageName().equals(pkg)) {
                return r;
            }
        }
        return null;
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mNotificationLock"})
    public List<NotificationRecord> findNotificationsByListLocked(ArrayList<NotificationRecord> list, String pkg, String tag, int id, int userId) {
        List<NotificationRecord> matching = new ArrayList<>();
        int len = list.size();
        for (int i = 0; i < len; i++) {
            NotificationRecord r = list.get(i);
            if (notificationMatchesUserId(r, userId) && r.sbn.getId() == id && TextUtils.equals(r.sbn.getTag(), tag) && r.sbn.getPackageName().equals(pkg)) {
                matching.add(r);
            }
        }
        return matching;
    }

    @GuardedBy({"mNotificationLock"})
    private NotificationRecord findNotificationByListLocked(ArrayList<NotificationRecord> list, String key) {
        int N = list.size();
        for (int i = 0; i < N; i++) {
            if (key.equals(list.get(i).getKey())) {
                return list.get(i);
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mNotificationLock"})
    public int indexOfNotificationLocked(String key) {
        int N = this.mNotificationList.size();
        for (int i = 0; i < N; i++) {
            if (key.equals(this.mNotificationList.get(i).getKey())) {
                return i;
            }
        }
        return -1;
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void hideNotificationsForPackages(String[] pkgs) {
        synchronized (mNotificationLock) {
            List<String> pkgList = Arrays.asList(pkgs);
            List<NotificationRecord> changedNotifications = new ArrayList<>();
            int numNotifications = this.mNotificationList.size();
            for (int i = 0; i < numNotifications; i++) {
                NotificationRecord rec = this.mNotificationList.get(i);
                if (pkgList.contains(rec.sbn.getPackageName())) {
                    rec.setHidden(true);
                    changedNotifications.add(rec);
                }
            }
            this.mListeners.notifyHiddenLocked(changedNotifications);
        }
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void unhideNotificationsForPackages(String[] pkgs) {
        synchronized (mNotificationLock) {
            List<String> pkgList = Arrays.asList(pkgs);
            List<NotificationRecord> changedNotifications = new ArrayList<>();
            int numNotifications = this.mNotificationList.size();
            for (int i = 0; i < numNotifications; i++) {
                NotificationRecord rec = this.mNotificationList.get(i);
                if (pkgList.contains(rec.sbn.getPackageName())) {
                    rec.setHidden(false);
                    changedNotifications.add(rec);
                }
            }
            this.mListeners.notifyUnhiddenLocked(changedNotifications);
        }
    }

    /* access modifiers changed from: private */
    public void updateNotificationPulse() {
        synchronized (mNotificationLock) {
            updateLightsLocked();
        }
    }

    /* access modifiers changed from: protected */
    public boolean isCallingUidSystem() {
        return Binder.getCallingUid() == 1000;
    }

    /* access modifiers changed from: protected */
    public boolean isUidSystemOrPhone(int uid) {
        int appid = UserHandle.getAppId(uid);
        return appid == 1000 || appid == 1001 || uid == 0;
    }

    /* access modifiers changed from: protected */
    public boolean isCallerSystemOrPhone() {
        return isUidSystemOrPhone(Binder.getCallingUid());
    }

    /* access modifiers changed from: private */
    public void checkCallerIsSystemOrShell() {
        if (Binder.getCallingUid() != SHORT_DELAY) {
            checkCallerIsSystem();
        }
    }

    /* access modifiers changed from: private */
    public void checkCallerIsSystem() {
        if (!isCallerSystemOrPhone()) {
            throw new SecurityException("Disallowed call for uid " + Binder.getCallingUid());
        }
    }

    /* access modifiers changed from: private */
    public void checkCallerIsSystemOrSystemUiOrShell() {
        if (Binder.getCallingUid() != SHORT_DELAY && !isCallerSystemOrPhone()) {
            getContext().enforceCallingPermission("android.permission.STATUS_BAR_SERVICE", (String) null);
        }
    }

    /* access modifiers changed from: private */
    public void checkCallerIsSystemOrSameApp(String pkg) {
        if (!isCallerSystemOrPhone()) {
            checkCallerIsSameApp(pkg);
        }
    }

    private boolean isCallerAndroid(String callingPkg, int uid) {
        return isUidSystemOrPhone(uid) && callingPkg != null && PackageManagerService.PLATFORM_PACKAGE_NAME.equals(callingPkg);
    }

    private void checkRestrictedCategories(Notification notification) {
        try {
            if (!this.mPackageManager.hasSystemFeature("android.hardware.type.automotive", 0)) {
                return;
            }
        } catch (RemoteException e) {
            if (DBG) {
                Slog.e(TAG, "Unable to confirm if it's safe to skip category restrictions check thus the check will be done anyway");
            }
        }
        if ("car_emergency".equals(notification.category) || "car_warning".equals(notification.category) || "car_information".equals(notification.category)) {
            checkCallerIsSystem();
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean isCallerInstantApp(int callingUid, int userId) {
        if (isUidSystemOrPhone(callingUid)) {
            return false;
        }
        if (userId == -1) {
            userId = 0;
        }
        try {
            String[] pkgs = this.mPackageManager.getPackagesForUid(callingUid);
            if (pkgs != null) {
                String pkg = pkgs[0];
                this.mAppOps.checkPackage(callingUid, pkg);
                ApplicationInfo ai = this.mPackageManager.getApplicationInfo(pkg, 0, userId);
                if (ai != null) {
                    return ai.isInstantApp();
                }
                throw new SecurityException("Unknown package " + pkg);
            }
            throw new SecurityException("Unknown uid " + callingUid);
        } catch (RemoteException re) {
            throw new SecurityException("Unknown uid " + callingUid, re);
        }
    }

    /* access modifiers changed from: private */
    public void checkCallerIsSameApp(String pkg) {
        checkCallerIsSameApp(pkg, Binder.getCallingUid(), UserHandle.getCallingUserId());
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    private void checkCallerIsSameApp(String pkg, int uid, int userId) {
        try {
            ApplicationInfo ai = this.mPackageManager.getApplicationInfo(pkg, 0, userId);
            if (ai == null) {
                throw new SecurityException("Unknown package " + pkg);
            } else if (!UserHandle.isSameApp(ai.uid, uid)) {
                throw new SecurityException("Calling uid " + uid + " gave package " + pkg + " which is owned by uid " + ai.uid);
            }
        } catch (RemoteException re) {
            throw new SecurityException("Unknown package " + pkg + "\n" + re);
        }
    }

    private boolean isCallerSameApp(String pkg) {
        try {
            checkCallerIsSameApp(pkg);
            return true;
        } catch (SecurityException e) {
            return false;
        }
    }

    private boolean isCallerSameApp(String pkg, int uid, int userId) {
        try {
            checkCallerIsSameApp(pkg, uid, userId);
            return true;
        } catch (SecurityException e) {
            return false;
        }
    }

    /* access modifiers changed from: private */
    public static String callStateToString(int state) {
        if (state == 0) {
            return "CALL_STATE_IDLE";
        }
        if (state == 1) {
            return "CALL_STATE_RINGING";
        }
        if (state == 2) {
            return "CALL_STATE_OFFHOOK";
        }
        return "CALL_STATE_UNKNOWN_" + state;
    }

    private void listenForCallState() {
        TelephonyManager.from(getContext()).listen(new PhoneStateListener() {
            public void onCallStateChanged(int state, String incomingNumber) {
                if (NotificationManagerService.this.mCallState != state) {
                    if (NotificationManagerService.DBG) {
                        Slog.d(NotificationManagerService.TAG, "Call state changed: " + NotificationManagerService.callStateToString(state));
                    }
                    int unused = NotificationManagerService.this.mCallState = state;
                }
            }
        }, 32);
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mNotificationLock"})
    public NotificationRankingUpdate makeRankingUpdateLocked(ManagedServices.ManagedServiceInfo info) {
        NotificationManagerService notificationManagerService = this;
        int N = notificationManagerService.mNotificationList.size();
        ArrayList<NotificationListenerService.Ranking> rankings = new ArrayList<>();
        int i = 0;
        while (true) {
            boolean z = false;
            if (i < N) {
                NotificationRecord record = notificationManagerService.mNotificationList.get(i);
                if (notificationManagerService.isVisibleToListener(record.sbn, info)) {
                    String key = record.sbn.getKey();
                    NotificationListenerService.Ranking ranking = new NotificationListenerService.Ranking();
                    int size = rankings.size();
                    boolean z2 = !record.isIntercepted();
                    int packageVisibilityOverride = record.getPackageVisibilityOverride();
                    int suppressedVisualEffects = record.getSuppressedVisualEffects();
                    int importance = record.getImportance();
                    CharSequence importanceExplanation = record.getImportanceExplanation();
                    String overrideGroupKey = record.sbn.getOverrideGroupKey();
                    NotificationChannel channel = record.getChannel();
                    ArrayList<String> peopleOverride = record.getPeopleOverride();
                    ArrayList<SnoozeCriterion> snoozeCriteria = record.getSnoozeCriteria();
                    boolean canShowBadge = record.canShowBadge();
                    int userSentiment = record.getUserSentiment();
                    boolean isHidden = record.isHidden();
                    long lastAudiblyAlertedMs = record.getLastAudiblyAlertedMs();
                    if (!(record.getSound() == null && record.getVibration() == null)) {
                        z = true;
                    }
                    ranking.populate(key, size, z2, packageVisibilityOverride, suppressedVisualEffects, importance, importanceExplanation, overrideGroupKey, channel, peopleOverride, snoozeCriteria, canShowBadge, userSentiment, isHidden, lastAudiblyAlertedMs, z, record.getSystemGeneratedSmartActions(), record.getSmartReplies(), record.canBubble());
                    rankings.add(ranking);
                }
                i++;
                notificationManagerService = this;
            } else {
                ManagedServices.ManagedServiceInfo managedServiceInfo = info;
                return new NotificationRankingUpdate((NotificationListenerService.Ranking[]) rankings.toArray(new NotificationListenerService.Ranking[0]));
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean hasCompanionDevice(ManagedServices.ManagedServiceInfo info) {
        if (this.mCompanionManager == null) {
            this.mCompanionManager = getCompanionManager();
        }
        if (this.mCompanionManager == null) {
            return false;
        }
        long identity = Binder.clearCallingIdentity();
        try {
            if (!ArrayUtils.isEmpty(this.mCompanionManager.getAssociations(info.component.getPackageName(), info.userid))) {
                Binder.restoreCallingIdentity(identity);
                return true;
            }
        } catch (SecurityException e) {
        } catch (RemoteException re) {
            Slog.e(TAG, "Cannot reach companion device service", re);
        } catch (Exception e2) {
            Slog.e(TAG, "Cannot verify listener " + info, e2);
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(identity);
            throw th;
        }
        Binder.restoreCallingIdentity(identity);
        return false;
    }

    /* access modifiers changed from: protected */
    public ICompanionDeviceManager getCompanionManager() {
        return ICompanionDeviceManager.Stub.asInterface(ServiceManager.getService("companiondevice"));
    }

    /* access modifiers changed from: private */
    public boolean isVisibleToListener(StatusBarNotification sbn, ManagedServices.ManagedServiceInfo listener) {
        if (!listener.enabledAndUserMatches(sbn.getUserId())) {
            return false;
        }
        return true;
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    /* access modifiers changed from: private */
    public boolean isPackageSuspendedForUser(String pkg, int uid) {
        long identity = Binder.clearCallingIdentity();
        try {
            boolean isPackageSuspendedForUser = this.mPackageManager.isPackageSuspendedForUser(pkg, UserHandle.getUserId(uid));
            Binder.restoreCallingIdentity(identity);
            return isPackageSuspendedForUser;
        } catch (RemoteException e) {
            throw new SecurityException("Could not talk to package manager service");
        } catch (IllegalArgumentException e2) {
            Binder.restoreCallingIdentity(identity);
            return false;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(identity);
            throw th;
        }
    }

    private boolean isPackageDistractionRestrictionForUser(String pkg, int uid) {
        try {
            if ((((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class)).getDistractingPackageRestrictions(pkg, UserHandle.getUserId(uid)) & 2) != 0) {
                return true;
            }
            return false;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean canUseManagedServices(String pkg, Integer userId, String requiredPermission) {
        boolean canUseManagedServices = !this.mActivityManager.isLowRamDevice() || this.mPackageManagerClient.hasSystemFeature("android.hardware.type.watch");
        for (String whitelisted : getContext().getResources().getStringArray(17235977)) {
            if (whitelisted.equals(pkg)) {
                canUseManagedServices = true;
            }
        }
        if (requiredPermission == null) {
            return canUseManagedServices;
        }
        try {
            if (this.mPackageManager.checkPermission(requiredPermission, pkg, userId.intValue()) != 0) {
                return false;
            }
            return canUseManagedServices;
        } catch (RemoteException e) {
            Slog.e(TAG, "can't talk to pm", e);
            return canUseManagedServices;
        }
    }

    private class TrimCache {
        StatusBarNotification heavy;
        StatusBarNotification sbnClone;
        StatusBarNotification sbnCloneLight;

        TrimCache(StatusBarNotification sbn) {
            this.heavy = sbn;
        }

        /* access modifiers changed from: package-private */
        public StatusBarNotification ForListener(ManagedServices.ManagedServiceInfo info) {
            if (NotificationManagerService.this.mListeners.getOnNotificationPostedTrim(info) == 1) {
                if (this.sbnCloneLight == null) {
                    this.sbnCloneLight = this.heavy.cloneLight();
                }
                return this.sbnCloneLight;
            }
            if (this.sbnClone == null) {
                this.sbnClone = this.heavy.clone();
            }
            return this.sbnClone;
        }
    }

    public class NotificationAssistants extends ManagedServices {
        private static final String ATT_TYPES = "types";
        private static final String ATT_USER_SET = "user_set";
        private static final String TAG_ALLOWED_ADJUSTMENT_TYPES = "q_allowed_adjustments";
        static final String TAG_ENABLED_NOTIFICATION_ASSISTANTS = "enabled_assistants";
        private Set<String> mAllowedAdjustments = new ArraySet();
        private final Object mLock = new Object();
        @GuardedBy({"mLock"})
        private ArrayMap<Integer, Boolean> mUserSetMap = new ArrayMap<>();

        public NotificationAssistants(Context context, Object lock, ManagedServices.UserProfiles up, IPackageManager pm) {
            super(context, lock, up, pm);
            for (String add : NotificationManagerService.DEFAULT_ALLOWED_ADJUSTMENTS) {
                this.mAllowedAdjustments.add(add);
            }
        }

        /* access modifiers changed from: protected */
        public ManagedServices.Config getConfig() {
            ManagedServices.Config c = new ManagedServices.Config();
            c.caption = "notification assistant";
            c.serviceInterface = "android.service.notification.NotificationAssistantService";
            c.xmlTag = TAG_ENABLED_NOTIFICATION_ASSISTANTS;
            c.secureSettingName = "enabled_notification_assistant";
            c.bindPermission = "android.permission.BIND_NOTIFICATION_ASSISTANT_SERVICE";
            c.settingsAction = "android.settings.MANAGE_DEFAULT_APPS_SETTINGS";
            c.clientLabel = 17040584;
            return c;
        }

        /* access modifiers changed from: protected */
        public IInterface asInterface(IBinder binder) {
            return INotificationListener.Stub.asInterface(binder);
        }

        /* access modifiers changed from: protected */
        public boolean checkType(IInterface service) {
            return service instanceof INotificationListener;
        }

        /* access modifiers changed from: protected */
        public void onServiceAdded(ManagedServices.ManagedServiceInfo info) {
            NotificationManagerService.this.mListeners.registerGuestService(info);
        }

        /* access modifiers changed from: protected */
        @GuardedBy({"mNotificationLock"})
        public void onServiceRemovedLocked(ManagedServices.ManagedServiceInfo removed) {
            NotificationManagerService.this.mListeners.unregisterService(removed.service, removed.userid);
        }

        public void onUserUnlocked(int user) {
            if (this.DEBUG) {
                String str = this.TAG;
                Slog.d(str, "onUserUnlocked u=" + user);
            }
            rebindServices(true, user);
        }

        /* access modifiers changed from: protected */
        public String getRequiredPermission() {
            return "android.permission.REQUEST_NOTIFICATION_ASSISTANT_SERVICE";
        }

        /* access modifiers changed from: protected */
        public void writeExtraXmlTags(XmlSerializer out) throws IOException {
            synchronized (this.mLock) {
                out.startTag((String) null, TAG_ALLOWED_ADJUSTMENT_TYPES);
                out.attribute((String) null, ATT_TYPES, TextUtils.join(",", this.mAllowedAdjustments));
                out.endTag((String) null, TAG_ALLOWED_ADJUSTMENT_TYPES);
            }
        }

        /* access modifiers changed from: protected */
        public void readExtraTag(String tag, XmlPullParser parser) throws IOException {
            if (TAG_ALLOWED_ADJUSTMENT_TYPES.equals(tag)) {
                String types = XmlUtils.readStringAttribute(parser, ATT_TYPES);
                synchronized (this.mLock) {
                    this.mAllowedAdjustments.clear();
                    if (!TextUtils.isEmpty(types)) {
                        this.mAllowedAdjustments.addAll(Arrays.asList(types.split(",")));
                    }
                }
            }
        }

        /* access modifiers changed from: protected */
        public void allowAdjustmentType(String type) {
            synchronized (this.mLock) {
                this.mAllowedAdjustments.add(type);
            }
            for (ManagedServices.ManagedServiceInfo info : getServices()) {
                NotificationManagerService.mHandler.post(new Runnable(info) {
                    private final /* synthetic */ ManagedServices.ManagedServiceInfo f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        NotificationManagerService.NotificationAssistants.this.lambda$allowAdjustmentType$0$NotificationManagerService$NotificationAssistants(this.f$1);
                    }
                });
            }
        }

        /* access modifiers changed from: protected */
        public void disallowAdjustmentType(String type) {
            synchronized (this.mLock) {
                this.mAllowedAdjustments.remove(type);
            }
            for (ManagedServices.ManagedServiceInfo info : getServices()) {
                NotificationManagerService.mHandler.post(new Runnable(info) {
                    private final /* synthetic */ ManagedServices.ManagedServiceInfo f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        NotificationManagerService.NotificationAssistants.this.lambda$disallowAdjustmentType$1$NotificationManagerService$NotificationAssistants(this.f$1);
                    }
                });
            }
        }

        /* access modifiers changed from: protected */
        public List<String> getAllowedAssistantAdjustments() {
            List<String> types;
            synchronized (this.mLock) {
                types = new ArrayList<>();
                types.addAll(this.mAllowedAdjustments);
            }
            return types;
        }

        /* access modifiers changed from: protected */
        public boolean isAdjustmentAllowed(String type) {
            boolean contains;
            synchronized (this.mLock) {
                contains = this.mAllowedAdjustments.contains(type);
            }
            return contains;
        }

        /* access modifiers changed from: protected */
        public void onNotificationsSeenLocked(ArrayList<NotificationRecord> records) {
            for (ManagedServices.ManagedServiceInfo info : getServices()) {
                ArrayList<String> keys = new ArrayList<>(records.size());
                Iterator<NotificationRecord> it = records.iterator();
                while (it.hasNext()) {
                    NotificationRecord r = it.next();
                    if (NotificationManagerService.this.isVisibleToListener(r.sbn, info) && info.isSameUser(r.getUserId())) {
                        keys.add(r.getKey());
                    }
                }
                if (!keys.isEmpty()) {
                    NotificationManagerService.mHandler.post(new Runnable(info, keys) {
                        private final /* synthetic */ ManagedServices.ManagedServiceInfo f$1;
                        private final /* synthetic */ ArrayList f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run() {
                            NotificationManagerService.NotificationAssistants.this.lambda$onNotificationsSeenLocked$2$NotificationManagerService$NotificationAssistants(this.f$1, this.f$2);
                        }
                    });
                }
            }
        }

        /* access modifiers changed from: package-private */
        public boolean hasUserSet(int userId) {
            boolean booleanValue;
            synchronized (this.mLock) {
                booleanValue = ((Boolean) this.mUserSetMap.getOrDefault(Integer.valueOf(userId), false)).booleanValue();
            }
            return booleanValue;
        }

        /* access modifiers changed from: package-private */
        public void setUserSet(int userId, boolean set) {
            synchronized (this.mLock) {
                this.mUserSetMap.put(Integer.valueOf(userId), Boolean.valueOf(set));
            }
        }

        /* access modifiers changed from: protected */
        public void writeExtraAttributes(XmlSerializer out, int userId) throws IOException {
            out.attribute((String) null, ATT_USER_SET, Boolean.toString(hasUserSet(userId)));
        }

        /* access modifiers changed from: protected */
        public void readExtraAttributes(String tag, XmlPullParser parser, int userId) throws IOException {
            setUserSet(userId, XmlUtils.readBooleanAttribute(parser, ATT_USER_SET, false));
        }

        /* access modifiers changed from: private */
        /* renamed from: notifyCapabilitiesChanged */
        public void lambda$disallowAdjustmentType$1$NotificationManagerService$NotificationAssistants(ManagedServices.ManagedServiceInfo info) {
            INotificationListener assistant = info.service;
            try {
                assistant.onAllowedAdjustmentsChanged();
            } catch (RemoteException ex) {
                String str = this.TAG;
                Slog.e(str, "unable to notify assistant (capabilities): " + assistant, ex);
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: notifySeen */
        public void lambda$onNotificationsSeenLocked$2$NotificationManagerService$NotificationAssistants(ManagedServices.ManagedServiceInfo info, ArrayList<String> keys) {
            INotificationListener assistant = info.service;
            try {
                assistant.onNotificationsSeen(keys);
            } catch (RemoteException ex) {
                String str = this.TAG;
                Slog.e(str, "unable to notify assistant (seen): " + assistant, ex);
            }
        }

        /* access modifiers changed from: private */
        @GuardedBy({"mNotificationLock"})
        public void onNotificationEnqueuedLocked(NotificationRecord r) {
            boolean debug = isVerboseLogEnabled();
            if (debug) {
                String str = this.TAG;
                Slog.v(str, "onNotificationEnqueuedLocked() called with: r = [" + r + "]");
            }
            notifyAssistantLocked(r.sbn, true, new BiConsumer(debug, r) {
                private final /* synthetic */ boolean f$1;
                private final /* synthetic */ NotificationRecord f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void accept(Object obj, Object obj2) {
                    NotificationManagerService.NotificationAssistants.this.lambda$onNotificationEnqueuedLocked$3$NotificationManagerService$NotificationAssistants(this.f$1, this.f$2, (INotificationListener) obj, (NotificationManagerService.StatusBarNotificationHolder) obj2);
                }
            });
        }

        public /* synthetic */ void lambda$onNotificationEnqueuedLocked$3$NotificationManagerService$NotificationAssistants(boolean debug, NotificationRecord r, INotificationListener assistant, StatusBarNotificationHolder sbnHolder) {
            if (debug) {
                try {
                    String str = this.TAG;
                    Slog.v(str, "calling onNotificationEnqueuedWithChannel " + sbnHolder);
                } catch (RemoteException ex) {
                    String str2 = this.TAG;
                    Slog.e(str2, "unable to notify assistant (enqueued): " + assistant, ex);
                    return;
                }
            }
            assistant.onNotificationEnqueuedWithChannel(sbnHolder, r.getChannel());
        }

        /* access modifiers changed from: package-private */
        @GuardedBy({"mNotificationLock"})
        public void notifyAssistantExpansionChangedLocked(StatusBarNotification sbn, boolean isUserAction, boolean isExpanded) {
            notifyAssistantLocked(sbn, false, new BiConsumer(sbn.getKey(), isUserAction, isExpanded) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ boolean f$2;
                private final /* synthetic */ boolean f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void accept(Object obj, Object obj2) {
                    NotificationManagerService.NotificationAssistants.this.lambda$notifyAssistantExpansionChangedLocked$4$NotificationManagerService$NotificationAssistants(this.f$1, this.f$2, this.f$3, (INotificationListener) obj, (NotificationManagerService.StatusBarNotificationHolder) obj2);
                }
            });
        }

        public /* synthetic */ void lambda$notifyAssistantExpansionChangedLocked$4$NotificationManagerService$NotificationAssistants(String key, boolean isUserAction, boolean isExpanded, INotificationListener assistant, StatusBarNotificationHolder sbnHolder) {
            try {
                assistant.onNotificationExpansionChanged(key, isUserAction, isExpanded);
            } catch (RemoteException ex) {
                String str = this.TAG;
                Slog.e(str, "unable to notify assistant (expanded): " + assistant, ex);
            }
        }

        /* access modifiers changed from: package-private */
        @GuardedBy({"mNotificationLock"})
        public void notifyAssistantNotificationDirectReplyLocked(StatusBarNotification sbn) {
            notifyAssistantLocked(sbn, false, new BiConsumer(sbn.getKey()) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void accept(Object obj, Object obj2) {
                    NotificationManagerService.NotificationAssistants.this.lambda$notifyAssistantNotificationDirectReplyLocked$5$NotificationManagerService$NotificationAssistants(this.f$1, (INotificationListener) obj, (NotificationManagerService.StatusBarNotificationHolder) obj2);
                }
            });
        }

        public /* synthetic */ void lambda$notifyAssistantNotificationDirectReplyLocked$5$NotificationManagerService$NotificationAssistants(String key, INotificationListener assistant, StatusBarNotificationHolder sbnHolder) {
            try {
                assistant.onNotificationDirectReply(key);
            } catch (RemoteException ex) {
                String str = this.TAG;
                Slog.e(str, "unable to notify assistant (expanded): " + assistant, ex);
            }
        }

        /* access modifiers changed from: package-private */
        @GuardedBy({"mNotificationLock"})
        public void notifyAssistantSuggestedReplySent(StatusBarNotification sbn, CharSequence reply, boolean generatedByAssistant) {
            notifyAssistantLocked(sbn, false, new BiConsumer(sbn.getKey(), reply, generatedByAssistant) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ CharSequence f$2;
                private final /* synthetic */ boolean f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void accept(Object obj, Object obj2) {
                    NotificationManagerService.NotificationAssistants.this.lambda$notifyAssistantSuggestedReplySent$6$NotificationManagerService$NotificationAssistants(this.f$1, this.f$2, this.f$3, (INotificationListener) obj, (NotificationManagerService.StatusBarNotificationHolder) obj2);
                }
            });
        }

        public /* synthetic */ void lambda$notifyAssistantSuggestedReplySent$6$NotificationManagerService$NotificationAssistants(String key, CharSequence reply, boolean generatedByAssistant, INotificationListener assistant, StatusBarNotificationHolder sbnHolder) {
            int i;
            if (generatedByAssistant) {
                i = 1;
            } else {
                i = 0;
            }
            try {
                assistant.onSuggestedReplySent(key, reply, i);
            } catch (RemoteException ex) {
                String str = this.TAG;
                Slog.e(str, "unable to notify assistant (snoozed): " + assistant, ex);
            }
        }

        /* access modifiers changed from: package-private */
        @GuardedBy({"mNotificationLock"})
        public void notifyAssistantActionClicked(StatusBarNotification sbn, int actionIndex, Notification.Action action, boolean generatedByAssistant) {
            notifyAssistantLocked(sbn, false, new BiConsumer(sbn.getKey(), action, generatedByAssistant) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ Notification.Action f$2;
                private final /* synthetic */ boolean f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void accept(Object obj, Object obj2) {
                    NotificationManagerService.NotificationAssistants.this.lambda$notifyAssistantActionClicked$7$NotificationManagerService$NotificationAssistants(this.f$1, this.f$2, this.f$3, (INotificationListener) obj, (NotificationManagerService.StatusBarNotificationHolder) obj2);
                }
            });
        }

        public /* synthetic */ void lambda$notifyAssistantActionClicked$7$NotificationManagerService$NotificationAssistants(String key, Notification.Action action, boolean generatedByAssistant, INotificationListener assistant, StatusBarNotificationHolder sbnHolder) {
            int i;
            if (generatedByAssistant) {
                i = 1;
            } else {
                i = 0;
            }
            try {
                assistant.onActionClicked(key, action, i);
            } catch (RemoteException ex) {
                String str = this.TAG;
                Slog.e(str, "unable to notify assistant (snoozed): " + assistant, ex);
            }
        }

        /* access modifiers changed from: private */
        @GuardedBy({"mNotificationLock"})
        public void notifyAssistantSnoozedLocked(StatusBarNotification sbn, String snoozeCriterionId) {
            notifyAssistantLocked(sbn, false, new BiConsumer(snoozeCriterionId) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void accept(Object obj, Object obj2) {
                    NotificationManagerService.NotificationAssistants.this.lambda$notifyAssistantSnoozedLocked$8$NotificationManagerService$NotificationAssistants(this.f$1, (INotificationListener) obj, (NotificationManagerService.StatusBarNotificationHolder) obj2);
                }
            });
        }

        public /* synthetic */ void lambda$notifyAssistantSnoozedLocked$8$NotificationManagerService$NotificationAssistants(String snoozeCriterionId, INotificationListener assistant, StatusBarNotificationHolder sbnHolder) {
            try {
                assistant.onNotificationSnoozedUntilContext(sbnHolder, snoozeCriterionId);
            } catch (RemoteException ex) {
                String str = this.TAG;
                Slog.e(str, "unable to notify assistant (snoozed): " + assistant, ex);
            }
        }

        @GuardedBy({"mNotificationLock"})
        private void notifyAssistantLocked(StatusBarNotification sbn, boolean sameUserOnly, BiConsumer<INotificationListener, StatusBarNotificationHolder> callback) {
            TrimCache trimCache = new TrimCache(sbn);
            boolean debug = isVerboseLogEnabled();
            if (debug) {
                String str = this.TAG;
                Slog.v(str, "notifyAssistantLocked() called with: sbn = [" + sbn + "], sameUserOnly = [" + sameUserOnly + "], callback = [" + callback + "]");
            }
            for (ManagedServices.ManagedServiceInfo info : getServices()) {
                boolean sbnVisible = NotificationManagerService.this.isVisibleToListener(sbn, info) && (!sameUserOnly || info.isSameUser(sbn.getUserId()));
                if (debug) {
                    String str2 = this.TAG;
                    Slog.v(str2, "notifyAssistantLocked info=" + info + " snbVisible=" + sbnVisible);
                }
                if (sbnVisible) {
                    NotificationManagerService.mHandler.post(new Runnable(callback, info.service, new StatusBarNotificationHolder(trimCache.ForListener(info))) {
                        private final /* synthetic */ BiConsumer f$0;
                        private final /* synthetic */ INotificationListener f$1;
                        private final /* synthetic */ NotificationManagerService.StatusBarNotificationHolder f$2;

                        {
                            this.f$0 = r1;
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run() {
                            this.f$0.accept(this.f$1, this.f$2);
                        }
                    });
                }
            }
        }

        public boolean isEnabled() {
            return !getServices().isEmpty();
        }

        /* access modifiers changed from: protected */
        public void resetDefaultAssistantsIfNecessary() {
            for (UserInfo userInfo : this.mUm.getUsers(true)) {
                int userId = userInfo.getUserHandle().getIdentifier();
                if (!hasUserSet(userId)) {
                    String str = this.TAG;
                    Slog.d(str, "Approving default notification assistant for user " + userId);
                    NotificationManagerService.this.setDefaultAssistantForUser(userId);
                }
            }
        }

        /* access modifiers changed from: protected */
        public void setPackageOrComponentEnabled(String pkgOrComponent, int userId, boolean isPrimary, boolean enabled) {
            if (enabled) {
                List<ComponentName> allowedComponents = getAllowedComponents(userId);
                if (!allowedComponents.isEmpty()) {
                    ComponentName currentComponent = (ComponentName) CollectionUtils.firstOrNull(allowedComponents);
                    if (!currentComponent.flattenToString().equals(pkgOrComponent)) {
                        NotificationManagerService.this.setNotificationAssistantAccessGrantedForUserInternal(currentComponent, userId, false);
                    } else {
                        return;
                    }
                }
            }
            super.setPackageOrComponentEnabled(pkgOrComponent, userId, isPrimary, enabled);
        }

        public void dump(PrintWriter pw, DumpFilter filter) {
            super.dump(pw, filter);
            pw.println("    Has user set:");
            synchronized (this.mLock) {
                for (Integer intValue : this.mUserSetMap.keySet()) {
                    int userId = intValue.intValue();
                    pw.println("      userId=" + userId + " value=" + this.mUserSetMap.get(Integer.valueOf(userId)));
                }
            }
        }

        private boolean isVerboseLogEnabled() {
            return Log.isLoggable("notification_assistant", 2);
        }
    }

    public class NotificationListeners extends ManagedServices {
        static final String TAG_ENABLED_NOTIFICATION_LISTENERS = "enabled_listeners";
        private final ArraySet<ManagedServices.ManagedServiceInfo> mLightTrimListeners = new ArraySet<>();

        public NotificationListeners(IPackageManager pm) {
            super(NotificationManagerService.this.getContext(), NotificationManagerService.mNotificationLock, NotificationManagerService.this.mUserProfiles, pm);
        }

        /* access modifiers changed from: protected */
        public int getBindFlags() {
            return 83886337;
        }

        /* access modifiers changed from: protected */
        public ManagedServices.Config getConfig() {
            ManagedServices.Config c = new ManagedServices.Config();
            c.caption = "notification listener";
            c.serviceInterface = "android.service.notification.NotificationListenerService";
            c.xmlTag = TAG_ENABLED_NOTIFICATION_LISTENERS;
            c.secureSettingName = "enabled_notification_listeners";
            c.bindPermission = "android.permission.BIND_NOTIFICATION_LISTENER_SERVICE";
            c.settingsAction = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
            c.clientLabel = 17040582;
            return c;
        }

        /* access modifiers changed from: protected */
        public IInterface asInterface(IBinder binder) {
            return INotificationListener.Stub.asInterface(binder);
        }

        /* access modifiers changed from: protected */
        public boolean checkType(IInterface service) {
            return service instanceof INotificationListener;
        }

        public void onServiceAdded(ManagedServices.ManagedServiceInfo info) {
            NotificationRankingUpdate update;
            INotificationListener listener = info.service;
            synchronized (NotificationManagerService.mNotificationLock) {
                update = NotificationManagerService.this.makeRankingUpdateLocked(info);
            }
            try {
                listener.onListenerConnected(update);
            } catch (RemoteException e) {
            }
        }

        /* access modifiers changed from: protected */
        @GuardedBy({"mNotificationLock"})
        public void onServiceRemovedLocked(ManagedServices.ManagedServiceInfo removed) {
            if (NotificationManagerService.this.removeDisabledHints(removed)) {
                NotificationManagerService.this.updateListenerHintsLocked();
                NotificationManagerService.this.updateEffectsSuppressorLocked();
            }
            this.mLightTrimListeners.remove(removed);
        }

        /* access modifiers changed from: protected */
        public String getRequiredPermission() {
            return null;
        }

        @GuardedBy({"mNotificationLock"})
        public void setOnNotificationPostedTrimLocked(ManagedServices.ManagedServiceInfo info, int trim) {
            if (trim == 1) {
                this.mLightTrimListeners.add(info);
            } else {
                this.mLightTrimListeners.remove(info);
            }
        }

        public int getOnNotificationPostedTrim(ManagedServices.ManagedServiceInfo info) {
            return this.mLightTrimListeners.contains(info) ? 1 : 0;
        }

        public void onStatusBarIconsBehaviorChanged(boolean hideSilentStatusIcons) {
            for (ManagedServices.ManagedServiceInfo info : getServices()) {
                NotificationManagerService.mHandler.post(new Runnable(info, hideSilentStatusIcons) {
                    private final /* synthetic */ ManagedServices.ManagedServiceInfo f$1;
                    private final /* synthetic */ boolean f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        NotificationManagerService.NotificationListeners.this.lambda$onStatusBarIconsBehaviorChanged$0$NotificationManagerService$NotificationListeners(this.f$1, this.f$2);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$onStatusBarIconsBehaviorChanged$0$NotificationManagerService$NotificationListeners(ManagedServices.ManagedServiceInfo info, boolean hideSilentStatusIcons) {
            INotificationListener listener = info.service;
            try {
                listener.onStatusBarIconsBehaviorChanged(hideSilentStatusIcons);
            } catch (RemoteException ex) {
                String str = this.TAG;
                Slog.e(str, "unable to notify listener (hideSilentStatusIcons): " + listener, ex);
            }
        }

        @GuardedBy({"mNotificationLock"})
        public void notifyPostedLocked(NotificationRecord r, NotificationRecord old) {
            notifyPostedLocked(r, old, true);
        }

        @GuardedBy({"mNotificationLock"})
        private void notifyPostedLocked(NotificationRecord r, NotificationRecord old, boolean notifyAllListeners) {
            StatusBarNotification sbn = r.sbn;
            StatusBarNotification oldSbn = old != null ? old.sbn : null;
            TrimCache trimCache = new TrimCache(sbn);
            for (final ManagedServices.ManagedServiceInfo info : getServices()) {
                boolean sbnVisible = NotificationManagerService.this.isVisibleToListener(sbn, info);
                int targetUserId = 0;
                boolean oldSbnVisible = oldSbn != null ? NotificationManagerService.this.isVisibleToListener(oldSbn, info) : false;
                if ((oldSbnVisible || sbnVisible) && ((!r.isHidden() || info.targetSdkVersion >= 28) && (notifyAllListeners || info.targetSdkVersion < 28))) {
                    final NotificationRankingUpdate update = NotificationManagerService.this.makeRankingUpdateLocked(info);
                    if (!oldSbnVisible || sbnVisible) {
                        if (info.userid != -1) {
                            targetUserId = info.userid;
                        }
                        NotificationManagerService.this.updateUriPermissions(r, old, info.component.getPackageName(), targetUserId);
                        final StatusBarNotification sbnToPost = trimCache.ForListener(info);
                        NotificationManagerService.mHandler.post(new Runnable() {
                            public void run() {
                                NotificationListeners.this.notifyPosted(info, sbnToPost, update);
                            }
                        });
                    } else {
                        final StatusBarNotification oldSbnLightClone = oldSbn.cloneLight();
                        NotificationManagerService.mHandler.post(new Runnable() {
                            public void run() {
                                NotificationListeners.this.notifyRemoved(info, oldSbnLightClone, update, (NotificationStats) null, 6);
                            }
                        });
                    }
                }
            }
        }

        @GuardedBy({"mNotificationLock"})
        public void notifyRemovedLocked(NotificationRecord r, int reason, NotificationStats notificationStats) {
            NotificationRecord notificationRecord = r;
            int i = reason;
            StatusBarNotification sbn = notificationRecord.sbn;
            StatusBarNotification sbnLight = sbn.cloneLight();
            for (ManagedServices.ManagedServiceInfo info : getServices()) {
                if (NotificationManagerService.this.isVisibleToListener(sbn, info) && ((!r.isHidden() || i == 14 || info.targetSdkVersion >= 28) && (i != 14 || info.targetSdkVersion < 28))) {
                    final NotificationStats stats = NotificationManagerService.this.mAssistants.isServiceTokenValidLocked(info.service) ? notificationStats : null;
                    NotificationRankingUpdate update = NotificationManagerService.this.makeRankingUpdateLocked(info);
                    WorkerHandler access$1800 = NotificationManagerService.mHandler;
                    final ManagedServices.ManagedServiceInfo managedServiceInfo = info;
                    final StatusBarNotification statusBarNotification = sbnLight;
                    final NotificationRankingUpdate notificationRankingUpdate = update;
                    AnonymousClass3 r9 = r0;
                    final int i2 = reason;
                    AnonymousClass3 r0 = new Runnable() {
                        public void run() {
                            NotificationListeners.this.notifyRemoved(managedServiceInfo, statusBarNotification, notificationRankingUpdate, stats, i2);
                        }
                    };
                    access$1800.post(r9);
                    i = reason;
                }
            }
            NotificationManagerService.mHandler.post(new Runnable(notificationRecord) {
                private final /* synthetic */ NotificationRecord f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    NotificationManagerService.NotificationListeners.this.lambda$notifyRemovedLocked$1$NotificationManagerService$NotificationListeners(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$notifyRemovedLocked$1$NotificationManagerService$NotificationListeners(NotificationRecord r) {
            NotificationManagerService.this.updateUriPermissions((NotificationRecord) null, r, (String) null, 0);
        }

        @GuardedBy({"mNotificationLock"})
        public void notifyRankingUpdateLocked(List<NotificationRecord> changedHiddenNotifications) {
            boolean isHiddenRankingUpdate = changedHiddenNotifications != null && changedHiddenNotifications.size() > 0;
            for (final ManagedServices.ManagedServiceInfo serviceInfo : getServices()) {
                if (serviceInfo.isEnabledForCurrentProfiles()) {
                    boolean notifyThisListener = false;
                    if (isHiddenRankingUpdate && serviceInfo.targetSdkVersion >= 28) {
                        Iterator<NotificationRecord> it = changedHiddenNotifications.iterator();
                        while (true) {
                            if (it.hasNext()) {
                                if (NotificationManagerService.this.isVisibleToListener(it.next().sbn, serviceInfo)) {
                                    notifyThisListener = true;
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                    }
                    if (notifyThisListener || !isHiddenRankingUpdate) {
                        final NotificationRankingUpdate update = NotificationManagerService.this.makeRankingUpdateLocked(serviceInfo);
                        NotificationManagerService.mHandler.post(new Runnable() {
                            public void run() {
                                NotificationListeners.this.notifyRankingUpdate(serviceInfo, update);
                            }
                        });
                    }
                }
            }
        }

        @GuardedBy({"mNotificationLock"})
        public void notifyListenerHintsChangedLocked(final int hints) {
            for (final ManagedServices.ManagedServiceInfo serviceInfo : getServices()) {
                if (serviceInfo.isEnabledForCurrentProfiles()) {
                    NotificationManagerService.mHandler.post(new Runnable() {
                        public void run() {
                            NotificationListeners.this.notifyListenerHintsChanged(serviceInfo, hints);
                        }
                    });
                }
            }
        }

        @GuardedBy({"mNotificationLock"})
        public void notifyHiddenLocked(List<NotificationRecord> changedNotifications) {
            if (changedNotifications != null && changedNotifications.size() != 0) {
                notifyRankingUpdateLocked(changedNotifications);
                int numChangedNotifications = changedNotifications.size();
                for (int i = 0; i < numChangedNotifications; i++) {
                    NotificationRecord rec = changedNotifications.get(i);
                    NotificationManagerService.this.mListeners.notifyRemovedLocked(rec, 14, rec.getStats());
                }
            }
        }

        @GuardedBy({"mNotificationLock"})
        public void notifyUnhiddenLocked(List<NotificationRecord> changedNotifications) {
            if (changedNotifications != null && changedNotifications.size() != 0) {
                notifyRankingUpdateLocked(changedNotifications);
                int numChangedNotifications = changedNotifications.size();
                for (int i = 0; i < numChangedNotifications; i++) {
                    NotificationRecord rec = changedNotifications.get(i);
                    NotificationManagerService.this.mListeners.notifyPostedLocked(rec, rec, false);
                }
            }
        }

        public void notifyInterruptionFilterChanged(final int interruptionFilter) {
            for (final ManagedServices.ManagedServiceInfo serviceInfo : getServices()) {
                if (serviceInfo.isEnabledForCurrentProfiles()) {
                    NotificationManagerService.mHandler.post(new Runnable() {
                        public void run() {
                            NotificationListeners.this.notifyInterruptionFilterChanged(serviceInfo, interruptionFilter);
                        }
                    });
                }
            }
        }

        /* access modifiers changed from: protected */
        public void notifyNotificationChannelChanged(String pkg, UserHandle user, NotificationChannel channel, int modificationType) {
            if (channel != null) {
                for (ManagedServices.ManagedServiceInfo serviceInfo : getServices()) {
                    if (serviceInfo.enabledAndUserMatches(UserHandle.getCallingUserId())) {
                        BackgroundThread.getHandler().post(new Runnable(serviceInfo, pkg, user, channel, modificationType) {
                            private final /* synthetic */ ManagedServices.ManagedServiceInfo f$1;
                            private final /* synthetic */ String f$2;
                            private final /* synthetic */ UserHandle f$3;
                            private final /* synthetic */ NotificationChannel f$4;
                            private final /* synthetic */ int f$5;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                                this.f$3 = r4;
                                this.f$4 = r5;
                                this.f$5 = r6;
                            }

                            public final void run() {
                                NotificationManagerService.NotificationListeners.this.lambda$notifyNotificationChannelChanged$2$NotificationManagerService$NotificationListeners(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
                            }
                        });
                    }
                }
            }
        }

        public /* synthetic */ void lambda$notifyNotificationChannelChanged$2$NotificationManagerService$NotificationListeners(ManagedServices.ManagedServiceInfo serviceInfo, String pkg, UserHandle user, NotificationChannel channel, int modificationType) {
            if (NotificationManagerService.this.hasCompanionDevice(serviceInfo)) {
                notifyNotificationChannelChanged(serviceInfo, pkg, user, channel, modificationType);
            }
        }

        /* access modifiers changed from: protected */
        public void notifyNotificationChannelGroupChanged(String pkg, UserHandle user, NotificationChannelGroup group, int modificationType) {
            if (group != null) {
                for (ManagedServices.ManagedServiceInfo serviceInfo : getServices()) {
                    if (serviceInfo.enabledAndUserMatches(UserHandle.getCallingUserId())) {
                        BackgroundThread.getHandler().post(new Runnable(serviceInfo, pkg, user, group, modificationType) {
                            private final /* synthetic */ ManagedServices.ManagedServiceInfo f$1;
                            private final /* synthetic */ String f$2;
                            private final /* synthetic */ UserHandle f$3;
                            private final /* synthetic */ NotificationChannelGroup f$4;
                            private final /* synthetic */ int f$5;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                                this.f$3 = r4;
                                this.f$4 = r5;
                                this.f$5 = r6;
                            }

                            public final void run() {
                                NotificationManagerService.NotificationListeners.this.lambda$notifyNotificationChannelGroupChanged$3$NotificationManagerService$NotificationListeners(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
                            }
                        });
                    }
                }
            }
        }

        public /* synthetic */ void lambda$notifyNotificationChannelGroupChanged$3$NotificationManagerService$NotificationListeners(ManagedServices.ManagedServiceInfo serviceInfo, String pkg, UserHandle user, NotificationChannelGroup group, int modificationType) {
            if (NotificationManagerService.this.hasCompanionDevice(serviceInfo)) {
                notifyNotificationChannelGroupChanged(serviceInfo, pkg, user, group, modificationType);
            }
        }

        /* access modifiers changed from: private */
        public void notifyPosted(ManagedServices.ManagedServiceInfo info, StatusBarNotification sbn, NotificationRankingUpdate rankingUpdate) {
            INotificationListener listener = info.service;
            try {
                listener.onNotificationPosted(new StatusBarNotificationHolder(sbn), rankingUpdate);
            } catch (RemoteException ex) {
                String str = this.TAG;
                Slog.e(str, "unable to notify listener (posted): " + listener, ex);
            }
        }

        /* access modifiers changed from: private */
        public void notifyRemoved(ManagedServices.ManagedServiceInfo info, StatusBarNotification sbn, NotificationRankingUpdate rankingUpdate, NotificationStats stats, int reason) {
            if (info.enabledAndUserMatches(sbn.getUserId())) {
                INotificationListener listener = info.service;
                try {
                    listener.onNotificationRemoved(new StatusBarNotificationHolder(sbn), rankingUpdate, stats, reason);
                } catch (RemoteException ex) {
                    String str = this.TAG;
                    Slog.e(str, "unable to notify listener (removed): " + listener, ex);
                }
            }
        }

        /* access modifiers changed from: private */
        public void notifyRankingUpdate(ManagedServices.ManagedServiceInfo info, NotificationRankingUpdate rankingUpdate) {
            INotificationListener listener = info.service;
            try {
                listener.onNotificationRankingUpdate(rankingUpdate);
            } catch (RemoteException ex) {
                String str = this.TAG;
                Slog.e(str, "unable to notify listener (ranking update): " + listener, ex);
            }
        }

        /* access modifiers changed from: private */
        public void notifyListenerHintsChanged(ManagedServices.ManagedServiceInfo info, int hints) {
            INotificationListener listener = info.service;
            try {
                listener.onListenerHintsChanged(hints);
            } catch (RemoteException ex) {
                String str = this.TAG;
                Slog.e(str, "unable to notify listener (listener hints): " + listener, ex);
            }
        }

        /* access modifiers changed from: private */
        public void notifyInterruptionFilterChanged(ManagedServices.ManagedServiceInfo info, int interruptionFilter) {
            INotificationListener listener = info.service;
            try {
                listener.onInterruptionFilterChanged(interruptionFilter);
            } catch (RemoteException ex) {
                String str = this.TAG;
                Slog.e(str, "unable to notify listener (interruption filter): " + listener, ex);
            }
        }

        /* access modifiers changed from: package-private */
        public void notifyNotificationChannelChanged(ManagedServices.ManagedServiceInfo info, String pkg, UserHandle user, NotificationChannel channel, int modificationType) {
            INotificationListener listener = info.service;
            try {
                listener.onNotificationChannelModification(pkg, user, channel, modificationType);
            } catch (RemoteException ex) {
                String str = this.TAG;
                Slog.e(str, "unable to notify listener (channel changed): " + listener, ex);
            }
        }

        private void notifyNotificationChannelGroupChanged(ManagedServices.ManagedServiceInfo info, String pkg, UserHandle user, NotificationChannelGroup group, int modificationType) {
            INotificationListener listener = info.service;
            try {
                listener.onNotificationChannelGroupModification(pkg, user, group, modificationType);
            } catch (RemoteException ex) {
                String str = this.TAG;
                Slog.e(str, "unable to notify listener (channel group changed): " + listener, ex);
            }
        }

        public boolean isListenerPackage(String packageName) {
            if (packageName == null) {
                return false;
            }
            synchronized (NotificationManagerService.mNotificationLock) {
                for (ManagedServices.ManagedServiceInfo serviceInfo : getServices()) {
                    if (packageName.equals(serviceInfo.component.getPackageName())) {
                        return true;
                    }
                }
                return false;
            }
        }
    }

    class RoleObserver implements OnRoleHoldersChangedListener {
        private final Executor mExecutor;
        private ArrayMap<String, ArrayMap<Integer, ArraySet<String>>> mNonBlockableDefaultApps;
        private final IPackageManager mPm;
        private final RoleManager mRm;

        RoleObserver(RoleManager roleManager, IPackageManager pkgMgr, Executor executor) {
            this.mRm = roleManager;
            this.mPm = pkgMgr;
            this.mExecutor = executor;
        }

        public void init() {
            List<UserInfo> users = NotificationManagerService.this.mUm.getUsers();
            this.mNonBlockableDefaultApps = new ArrayMap<>();
            for (int i = 0; i < NotificationManagerService.NON_BLOCKABLE_DEFAULT_ROLES.length; i++) {
                ArrayMap<Integer, ArraySet<String>> userToApprovedList = new ArrayMap<>();
                this.mNonBlockableDefaultApps.put(NotificationManagerService.NON_BLOCKABLE_DEFAULT_ROLES[i], userToApprovedList);
                for (int j = 0; j < users.size(); j++) {
                    Integer userId = Integer.valueOf(users.get(j).getUserHandle().getIdentifier());
                    ArraySet<String> approvedForUserId = new ArraySet<>(this.mRm.getRoleHoldersAsUser(NotificationManagerService.NON_BLOCKABLE_DEFAULT_ROLES[i], UserHandle.of(userId.intValue())));
                    ArraySet<Pair<String, Integer>> approvedAppUids = new ArraySet<>();
                    Iterator<String> it = approvedForUserId.iterator();
                    while (it.hasNext()) {
                        String pkg = it.next();
                        approvedAppUids.add(new Pair(pkg, Integer.valueOf(getUidForPackage(pkg, userId.intValue()))));
                    }
                    userToApprovedList.put(userId, approvedForUserId);
                    NotificationManagerService.this.mPreferencesHelper.updateDefaultApps(userId.intValue(), (ArraySet<String>) null, approvedAppUids);
                }
            }
            this.mRm.addOnRoleHoldersChangedListenerAsUser(this.mExecutor, this, UserHandle.ALL);
        }

        @VisibleForTesting
        public boolean isApprovedPackageForRoleForUser(String role, String pkg, int userId) {
            return ((ArraySet) this.mNonBlockableDefaultApps.get(role).get(Integer.valueOf(userId))).contains(pkg);
        }

        public void onRoleHoldersChanged(String roleName, UserHandle user) {
            boolean relevantChange = false;
            int i = 0;
            while (true) {
                if (i >= NotificationManagerService.NON_BLOCKABLE_DEFAULT_ROLES.length) {
                    break;
                } else if (NotificationManagerService.NON_BLOCKABLE_DEFAULT_ROLES[i].equals(roleName)) {
                    relevantChange = true;
                    break;
                } else {
                    i++;
                }
            }
            if (relevantChange) {
                ArraySet<String> roleHolders = new ArraySet<>(this.mRm.getRoleHoldersAsUser(roleName, user));
                ArrayMap<Integer, ArraySet<String>> prevApprovedForRole = (ArrayMap) this.mNonBlockableDefaultApps.getOrDefault(roleName, new ArrayMap());
                ArraySet<String> previouslyApproved = (ArraySet) prevApprovedForRole.getOrDefault(Integer.valueOf(user.getIdentifier()), new ArraySet());
                ArraySet<String> toRemove = new ArraySet<>();
                ArraySet<Pair<String, Integer>> toAdd = new ArraySet<>();
                Iterator<String> it = previouslyApproved.iterator();
                while (it.hasNext()) {
                    String previous = it.next();
                    if (!roleHolders.contains(previous)) {
                        toRemove.add(previous);
                    }
                }
                Iterator<String> it2 = roleHolders.iterator();
                while (it2.hasNext()) {
                    String nowApproved = it2.next();
                    if (!previouslyApproved.contains(nowApproved)) {
                        toAdd.add(new Pair(nowApproved, Integer.valueOf(getUidForPackage(nowApproved, user.getIdentifier()))));
                    }
                }
                prevApprovedForRole.put(Integer.valueOf(user.getIdentifier()), roleHolders);
                this.mNonBlockableDefaultApps.put(roleName, prevApprovedForRole);
                NotificationManagerService.this.mPreferencesHelper.updateDefaultApps(user.getIdentifier(), toRemove, toAdd);
            }
        }

        private int getUidForPackage(String pkg, int userId) {
            try {
                return this.mPm.getPackageUid(pkg, 131072, userId);
            } catch (RemoteException e) {
                Slog.e(NotificationManagerService.TAG, "role manager has bad default " + pkg + " " + userId);
                return -1;
            }
        }
    }

    public static final class DumpFilter {
        public boolean criticalPriority = false;
        public boolean filtered = false;
        public boolean normalPriority = false;
        public String pkgFilter;
        public boolean proto = false;
        public boolean redact = true;
        public long since;
        public boolean stats;
        public boolean zen;

        /* JADX WARNING: Code restructure failed: missing block: B:35:0x009f, code lost:
            if (r3.equals(com.android.server.utils.PriorityDump.PRIORITY_ARG_CRITICAL) == false) goto L_0x00ac;
         */
        /* JADX WARNING: Removed duplicated region for block: B:41:0x00af  */
        /* JADX WARNING: Removed duplicated region for block: B:43:0x00b5  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public static com.android.server.notification.NotificationManagerService.DumpFilter parseFromArguments(java.lang.String[] r9) {
            /*
                com.android.server.notification.NotificationManagerService$DumpFilter r0 = new com.android.server.notification.NotificationManagerService$DumpFilter
                r0.<init>()
                r1 = 0
            L_0x0006:
                int r2 = r9.length
                if (r1 >= r2) goto L_0x00e3
                r2 = r9[r1]
                java.lang.String r3 = "--proto"
                boolean r3 = r3.equals(r2)
                r4 = 1
                if (r3 == 0) goto L_0x0018
                r0.proto = r4
                goto L_0x00e0
            L_0x0018:
                java.lang.String r3 = "--noredact"
                boolean r3 = r3.equals(r2)
                r5 = 0
                if (r3 != 0) goto L_0x00de
                java.lang.String r3 = "--reveal"
                boolean r3 = r3.equals(r2)
                if (r3 == 0) goto L_0x002b
                goto L_0x00de
            L_0x002b:
                java.lang.String r3 = "p"
                boolean r3 = r3.equals(r2)
                if (r3 != 0) goto L_0x00bd
                java.lang.String r3 = "pkg"
                boolean r3 = r3.equals(r2)
                if (r3 != 0) goto L_0x00bd
                java.lang.String r3 = "--package"
                boolean r3 = r3.equals(r2)
                if (r3 == 0) goto L_0x0047
                goto L_0x00bd
            L_0x0047:
                java.lang.String r3 = "--zen"
                boolean r3 = r3.equals(r2)
                if (r3 != 0) goto L_0x00b8
                java.lang.String r3 = "zen"
                boolean r3 = r3.equals(r2)
                if (r3 == 0) goto L_0x0059
                goto L_0x00b8
            L_0x0059:
                java.lang.String r3 = "--stats"
                boolean r3 = r3.equals(r2)
                if (r3 == 0) goto L_0x0079
                r0.stats = r4
                int r3 = r9.length
                int r3 = r3 - r4
                if (r1 >= r3) goto L_0x0073
                int r1 = r1 + 1
                r3 = r9[r1]
                long r5 = java.lang.Long.parseLong(r3)
                r0.since = r5
                goto L_0x00e0
            L_0x0073:
                r5 = 0
                r0.since = r5
                goto L_0x00e0
            L_0x0079:
                java.lang.String r3 = "--dump-priority"
                boolean r3 = r3.equals(r2)
                if (r3 == 0) goto L_0x00e0
                int r3 = r9.length
                int r3 = r3 - r4
                if (r1 >= r3) goto L_0x00e0
                int r1 = r1 + 1
                r3 = r9[r1]
                r6 = -1
                int r7 = r3.hashCode()
                r8 = -1986416409(0xffffffff8999b0e7, float:-3.699977E-33)
                if (r7 == r8) goto L_0x00a2
                r8 = -1560189025(0xffffffffa301679f, float:-7.015047E-18)
                if (r7 == r8) goto L_0x0099
            L_0x0098:
                goto L_0x00ac
            L_0x0099:
                java.lang.String r7 = "CRITICAL"
                boolean r3 = r3.equals(r7)
                if (r3 == 0) goto L_0x0098
                goto L_0x00ad
            L_0x00a2:
                java.lang.String r5 = "NORMAL"
                boolean r3 = r3.equals(r5)
                if (r3 == 0) goto L_0x0098
                r5 = r4
                goto L_0x00ad
            L_0x00ac:
                r5 = r6
            L_0x00ad:
                if (r5 == 0) goto L_0x00b5
                if (r5 == r4) goto L_0x00b2
                goto L_0x00e0
            L_0x00b2:
                r0.normalPriority = r4
                goto L_0x00e0
            L_0x00b5:
                r0.criticalPriority = r4
                goto L_0x00e0
            L_0x00b8:
                r0.filtered = r4
                r0.zen = r4
                goto L_0x00e0
            L_0x00bd:
                int r3 = r9.length
                int r3 = r3 - r4
                if (r1 >= r3) goto L_0x00e0
                int r1 = r1 + 1
                r3 = r9[r1]
                java.lang.String r3 = r3.trim()
                java.lang.String r3 = r3.toLowerCase()
                r0.pkgFilter = r3
                java.lang.String r3 = r0.pkgFilter
                boolean r3 = r3.isEmpty()
                if (r3 == 0) goto L_0x00db
                r3 = 0
                r0.pkgFilter = r3
                goto L_0x00e0
            L_0x00db:
                r0.filtered = r4
                goto L_0x00e0
            L_0x00de:
                r0.redact = r5
            L_0x00e0:
                int r1 = r1 + r4
                goto L_0x0006
            L_0x00e3:
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.notification.NotificationManagerService.DumpFilter.parseFromArguments(java.lang.String[]):com.android.server.notification.NotificationManagerService$DumpFilter");
        }

        public boolean matches(StatusBarNotification sbn) {
            if (!this.filtered || this.zen) {
                return true;
            }
            if (sbn == null || (!matches(sbn.getPackageName()) && !matches(sbn.getOpPkg()))) {
                return false;
            }
            return true;
        }

        public boolean matches(ComponentName component) {
            if (!this.filtered || this.zen) {
                return true;
            }
            if (component == null || !matches(component.getPackageName())) {
                return false;
            }
            return true;
        }

        public boolean matches(String pkg) {
            if (!this.filtered || this.zen) {
                return true;
            }
            if (pkg == null || !pkg.toLowerCase().contains(this.pkgFilter)) {
                return false;
            }
            return true;
        }

        public String toString() {
            if (this.stats) {
                return "stats";
            }
            if (this.zen) {
                return "zen";
            }
            return '\'' + this.pkgFilter + '\'';
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void resetAssistantUserSet(int userId) {
        this.mAssistants.setUserSet(userId, false);
        handleSavePolicyFile();
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public ComponentName getApprovedAssistant(int userId) {
        return (ComponentName) CollectionUtils.firstOrNull(this.mAssistants.getAllowedComponents(userId));
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void simulatePackageSuspendBroadcast(boolean suspend, String pkg) {
        String action;
        Bundle extras = new Bundle();
        extras.putStringArray("android.intent.extra.changed_package_list", new String[]{pkg});
        if (suspend) {
            action = "android.intent.action.PACKAGES_SUSPENDED";
        } else {
            action = "android.intent.action.PACKAGES_UNSUSPENDED";
        }
        Intent intent = new Intent(action);
        intent.putExtras(extras);
        this.mPackageIntentReceiver.onReceive(getContext(), intent);
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void simulatePackageDistractionBroadcast(int flag, String[] pkgs) {
        Bundle extras = new Bundle();
        extras.putStringArray("android.intent.extra.changed_package_list", pkgs);
        extras.putInt("android.intent.extra.distraction_restrictions", flag);
        Intent intent = new Intent("android.intent.action.DISTRACTING_PACKAGES_CHANGED");
        intent.putExtras(extras);
        this.mPackageIntentReceiver.onReceive(getContext(), intent);
    }

    private static final class StatusBarNotificationHolder extends IStatusBarNotificationHolder.Stub {
        private StatusBarNotification mValue;

        public StatusBarNotificationHolder(StatusBarNotification value) {
            this.mValue = value;
        }

        public StatusBarNotification get() {
            StatusBarNotification value = this.mValue;
            this.mValue = null;
            return value;
        }
    }

    private void writeSecureNotificationsPolicy(XmlSerializer out) throws IOException {
        out.startTag((String) null, LOCKSCREEN_ALLOW_SECURE_NOTIFICATIONS_TAG);
        out.attribute((String) null, LOCKSCREEN_ALLOW_SECURE_NOTIFICATIONS_VALUE, Boolean.toString(this.mLockScreenAllowSecureNotifications));
        out.endTag((String) null, LOCKSCREEN_ALLOW_SECURE_NOTIFICATIONS_TAG);
    }

    private static boolean safeBoolean(String val, boolean defValue) {
        if (TextUtils.isEmpty(val)) {
            return defValue;
        }
        return Boolean.parseBoolean(val);
    }

    public static WorkerHandler getHandler() {
        return mHandler;
    }
}
