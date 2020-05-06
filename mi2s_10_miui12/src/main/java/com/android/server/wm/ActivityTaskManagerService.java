package com.android.server.wm;

import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.ActivityOptions;
import android.app.ActivityThread;
import android.app.AppGlobals;
import android.app.Dialog;
import android.app.IActivityController;
import android.app.IActivityTaskManager;
import android.app.IApplicationThread;
import android.app.IAssistDataReceiver;
import android.app.INotificationManager;
import android.app.IRequestFinishCallback;
import android.app.ITaskStackListener;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.PictureInPictureParams;
import android.app.ProfilerInfo;
import android.app.RemoteAction;
import android.app.WaitResult;
import android.app.WindowConfiguration;
import android.app.admin.DevicePolicyCache;
import android.app.assist.AssistContent;
import android.app.assist.AssistStructure;
import android.app.usage.UsageStatsManagerInternal;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.IIntentReceiver;
import android.content.IIntentSender;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ConfigurationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.PackageManagerInternal;
import android.content.pm.ParceledListSlice;
import android.content.pm.ResolveInfo;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.GraphicBuffer;
import android.graphics.Point;
import android.graphics.Rect;
import android.metrics.LogMaker;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.FactoryTest;
import android.os.Handler;
import android.os.IBinder;
import android.os.IRemoteCallback;
import android.os.IUserManager;
import android.os.LocaleList;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.os.PowerManagerInternal;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.os.UpdateLock;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.WorkSource;
import android.os.storage.IStorageManager;
import android.provider.Settings;
import android.service.voice.IVoiceInteractionSession;
import android.service.voice.VoiceInteractionManagerInternal;
import android.telecom.TelecomManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.EventLog;
import android.util.Log;
import android.util.MiuiMultiWindowAdapter;
import android.util.MiuiMultiWindowUtils;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.StatsLog;
import android.util.proto.ProtoOutputStream;
import android.view.IRecentsAnimationRunner;
import android.view.RemoteAnimationAdapter;
import android.view.RemoteAnimationDefinition;
import android.view.inputmethod.InputMethodSystemProperty;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.AssistUtils;
import com.android.internal.app.IAppOpsService;
import com.android.internal.app.IVoiceInteractionSessionShowCallback;
import com.android.internal.app.IVoiceInteractor;
import com.android.internal.app.ProcessMap;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.notification.SystemNotificationChannels;
import com.android.internal.os.TransferPipe;
import com.android.internal.os.logging.MetricsLoggerWrapper;
import com.android.internal.policy.IKeyguardDismissCallback;
import com.android.internal.util.FastPrintWriter;
import com.android.internal.util.Preconditions;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.server.AttributeCache;
import com.android.server.DeviceIdleController;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.SystemServiceManager;
import com.android.server.UiThread;
import com.android.server.Watchdog;
import com.android.server.am.ActivityManagerService;
import com.android.server.am.AppTimeTracker;
import com.android.server.am.BaseErrorDialog;
import com.android.server.am.EventLogTags;
import com.android.server.am.PendingIntentController;
import com.android.server.am.PendingIntentRecord;
import com.android.server.am.UserState;
import com.android.server.appop.AppOpsService;
import com.android.server.firewall.IntentFirewall;
import com.android.server.pm.DumpState;
import com.android.server.pm.PackageManagerService;
import com.android.server.pm.UserManagerService;
import com.android.server.policy.PermissionPolicyInternal;
import com.android.server.uri.UriGrantsManagerInternal;
import com.android.server.vr.VrManagerInternal;
import com.android.server.wm.ActivityStack;
import com.android.server.wm.ActivityTaskManagerInternal;
import com.android.server.wm.ActivityTaskManagerService;
import com.miui.internal.transition.IMiuiAppTransitionAnimationHelper;
import com.miui.internal.transition.MiuiAppTransitionAnimationSpec;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ActivityTaskManagerService extends IActivityTaskManager.Stub {
    private static final int ACTION_DONE_POLL_WAIT_MS = 100;
    static final long ACTIVITY_BG_START_GRACE_PERIOD_MS = 10000;
    static final boolean ANIMATE = true;
    private static final long APP_SWITCH_DELAY_TIME = 5000;
    private static final int CAST_MODE_ENTER = 1;
    private static final int CAST_MODE_EXIT = 0;
    public static final String DUMP_ACTIVITIES_CMD = "activities";
    public static final String DUMP_ACTIVITIES_SHORT_CMD = "a";
    public static final String DUMP_CONTAINERS_CMD = "containers";
    public static final String DUMP_LASTANR_CMD = "lastanr";
    public static final String DUMP_LASTANR_TRACES_CMD = "lastanr-traces";
    public static final String DUMP_RECENTS_CMD = "recents";
    public static final String DUMP_RECENTS_SHORT_CMD = "r";
    public static final String DUMP_STARTER_CMD = "starter";
    static final int INSTRUMENTATION_KEY_DISPATCHING_TIMEOUT_MS = 60000;
    public static final int KEY_DISPATCHING_TIMEOUT_MS = 8000;
    private static final int MAX_RESUME_TIME = 2000;
    private static final int PENDING_ASSIST_EXTRAS_LONG_TIMEOUT = 2000;
    private static final int PENDING_ASSIST_EXTRAS_TIMEOUT = 500;
    private static final int PENDING_AUTOFILL_ASSIST_STRUCTURE_TIMEOUT = 2000;
    public static final int RELAUNCH_REASON_FREE_RESIZE = 2;
    public static final int RELAUNCH_REASON_NONE = 0;
    public static final int RELAUNCH_REASON_WINDOWING_MODE_RESIZE = 1;
    private static final int SERVICE_LAUNCH_IDLE_WHITELIST_DURATION_MS = 5000;
    private static final long START_AS_CALLER_TOKEN_EXPIRED_TIMEOUT = 1802000;
    private static final long START_AS_CALLER_TOKEN_TIMEOUT = 600000;
    private static final long START_AS_CALLER_TOKEN_TIMEOUT_IMPL = 602000;
    private static final String TAG = "ActivityTaskManager";
    private static final String TAG_CONFIGURATION = "ActivityTaskManager";
    private static final String TAG_FOCUS = "ActivityTaskManager";
    private static final String TAG_IMMERSIVE = "ActivityTaskManager";
    private static final String TAG_LOCKTASK = "ActivityTaskManager";
    private static final String TAG_STACK = "ActivityTaskManager";
    private static final String TAG_SWITCH = "ActivityTaskManager";
    private static final String TAG_VISIBILITY = "ActivityTaskManager";
    final int GL_ES_VERSION;
    private final Object mActionDoneSync = new Object();
    /* access modifiers changed from: private */
    public final MirrorActiveUids mActiveUids = new MirrorActiveUids();
    ComponentName mActiveVoiceInteractionServiceComponent;
    private ActivityStartController mActivityStartController;
    final SparseArray<ArrayMap<String, Integer>> mAllowAppSwitchUids = new SparseArray<>();
    ActivityManagerInternal mAmInternal;
    public AnimationHelperDeathRecipient mAnimationHelperDeathRecipient;
    private AppOpsService mAppOpsService;
    private long mAppSwitchesAllowedTime;
    /* access modifiers changed from: private */
    public AppWarnings mAppWarnings;
    private AssistUtils mAssistUtils;
    public ActivityRecord mCastActivity;
    private boolean mCastRotationChanged;
    /* access modifiers changed from: private */
    public final Map<Integer, Set<Integer>> mCompanionAppUidsMap = new ArrayMap();
    CompatModePackages mCompatModePackages;
    private int mConfigurationSeq;
    Context mContext;
    IActivityController mController = null;
    boolean mControllerIsAMonkey = false;
    AppTimeTracker mCurAppTimeTracker;
    private final ExecutorService mDefaultExecutor = Executors.newSingleThreadExecutor();
    private int mDeviceOwnerUid = -1;
    private boolean mDidAppSwitch;
    final ArrayList<IBinder> mExpiredStartAsCallerTokens = new ArrayList<>();
    final int mFactoryTest;
    private FontScaleSettingObserver mFontScaleSettingObserver;
    boolean mForceResizableActivities;
    private float mFullscreenThumbnailScale;
    @VisibleForTesting
    MiuiGestureController mGestureController;
    final WindowManagerGlobalLock mGlobalLock = new WindowManagerGlobalLock();
    final Object mGlobalLockWithoutBoost = this.mGlobalLock;
    H mH;
    boolean mHasHeavyWeightFeature;
    WindowProcessController mHeavyWeightProcess = null;
    WindowProcessController mHomeProcess;
    IntentFirewall mIntentFirewall;
    @VisibleForTesting
    final ActivityTaskManagerInternal mInternal;
    KeyguardController mKeyguardController;
    private boolean mKeyguardShown = false;
    String mLastANRState;
    ActivityRecord mLastResumedActivity;
    private long mLastStopAppSwitchesTime;
    private final ClientLifecycleManager mLifecycleManager;
    private LockTaskController mLockTaskController;
    /* access modifiers changed from: private */
    public IMiuiAppTransitionAnimationHelper mMiuiAppTransitionAnimationHelper;
    private final ArrayList<PendingAssistExtras> mPendingAssistExtras = new ArrayList<>();
    PendingIntentController mPendingIntentController;
    /* access modifiers changed from: private */
    public final SparseArray<String> mPendingTempWhitelist = new SparseArray<>();
    private PermissionPolicyInternal mPermissionPolicyInternal;
    private PackageManagerInternal mPmInternal;
    PowerManagerInternal mPowerManagerInternal;
    WindowProcessController mPreviousProcess;
    long mPreviousProcessVisibleTime;
    final WindowProcessControllerMap mProcessMap = new WindowProcessControllerMap();
    final ProcessMap<WindowProcessController> mProcessNames = new ProcessMap<>();
    String mProfileApp = null;
    WindowProcessController mProfileProc = null;
    ProfilerInfo mProfilerInfo = null;
    /* access modifiers changed from: private */
    public RecentTasks mRecentTasks;
    RootActivityContainer mRootActivityContainer;
    IVoiceInteractionSession mRunningVoice;
    final List<ActivityTaskManagerInternal.ScreenObserver> mScreenObservers = new ArrayList();
    /* access modifiers changed from: private */
    public boolean mShowDialogs = true;
    boolean mShuttingDown = false;
    /* access modifiers changed from: private */
    public boolean mSleeping = false;
    public ActivityStackSupervisor mStackSupervisor;
    final HashMap<IBinder, IBinder> mStartActivitySources = new HashMap<>();
    final StringBuilder mStringBuilder = new StringBuilder(256);
    private String[] mSupportedSystemLocales = null;
    boolean mSupportsFreeformWindowManagement;
    boolean mSupportsMultiDisplay;
    boolean mSupportsMultiWindow;
    boolean mSupportsPictureInPicture;
    boolean mSupportsSplitScreenMultiWindow;
    boolean mSuppressResizeConfigChanges;
    final ActivityThread mSystemThread;
    private TaskChangeNotificationController mTaskChangeNotificationController;
    private Configuration mTempConfig = new Configuration();
    private int mThumbnailHeight;
    private int mThumbnailWidth;
    private final UpdateConfigurationResult mTmpUpdateConfigurationResult = new UpdateConfigurationResult();
    String mTopAction = "android.intent.action.MAIN";
    ComponentName mTopComponent;
    String mTopData;
    int mTopProcessState = 2;
    private ActivityRecord mTracedResumedActivity;
    UriGrantsManagerInternal mUgmInternal;
    final Context mUiContext;
    UiHandler mUiHandler;
    private final UpdateLock mUpdateLock = new UpdateLock("immersive");
    private UsageStatsManagerInternal mUsageStatsInternal;
    private UserManagerService mUserManager;
    private int mViSessionId = 1000;
    PowerManager.WakeLock mVoiceWakeLock;
    int mVr2dDisplayId = -1;
    VrController mVrController;
    WindowManagerService mWindowManager;

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.SOURCE)
    @interface HotPath {
        public static final int LRU_UPDATE = 2;
        public static final int NONE = 0;
        public static final int OOM_ADJUSTMENT = 1;
        public static final int PROCESS_CHANGE = 3;

        int caller() default 0;
    }

    static final class UpdateConfigurationResult {
        boolean activityRelaunched;
        int changes;

        UpdateConfigurationResult() {
        }

        /* access modifiers changed from: package-private */
        public void reset() {
            this.changes = 0;
            this.activityRelaunched = false;
        }
    }

    private final class FontScaleSettingObserver extends ContentObserver {
        private final Uri mFontScaleUri = Settings.System.getUriFor("font_scale");
        private final Uri mHideErrorDialogsUri = Settings.Global.getUriFor("hide_error_dialogs");

        public FontScaleSettingObserver() {
            super(ActivityTaskManagerService.this.mH);
            ContentResolver resolver = ActivityTaskManagerService.this.mContext.getContentResolver();
            resolver.registerContentObserver(this.mFontScaleUri, false, this, -1);
            resolver.registerContentObserver(this.mHideErrorDialogsUri, false, this, -1);
        }

        public void onChange(boolean selfChange, Uri uri, int userId) {
            if (this.mFontScaleUri.equals(uri)) {
                ActivityTaskManagerService.this.updateFontScaleIfNeeded(userId);
            } else if (this.mHideErrorDialogsUri.equals(uri)) {
                synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                    try {
                        WindowManagerService.boostPriorityForLockedSection();
                        ActivityTaskManagerService.this.updateShouldShowDialogsLocked(ActivityTaskManagerService.this.getGlobalConfiguration());
                    } catch (Throwable th) {
                        while (true) {
                            WindowManagerService.resetPriorityAfterLockedSection();
                            throw th;
                        }
                    }
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            }
        }
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    public ActivityTaskManagerService(Context context) {
        this.mContext = context;
        this.mFactoryTest = FactoryTest.getMode();
        this.mSystemThread = ActivityThread.currentActivityThread();
        this.mUiContext = this.mSystemThread.getSystemUiContext();
        this.mLifecycleManager = new ClientLifecycleManager();
        this.mInternal = new LocalService();
        this.GL_ES_VERSION = SystemProperties.getInt("ro.opengles.version", 0);
    }

    public void onSystemReady() {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mHasHeavyWeightFeature = this.mContext.getPackageManager().hasSystemFeature("android.software.cant_save_state");
                this.mAssistUtils = new AssistUtils(this.mContext);
                this.mVrController.onSystemReady();
                this.mRecentTasks.onSystemReadyLocked();
                this.mStackSupervisor.onSystemReady();
                ActivityTaskManagerServiceInjector.onSystemReady(this.mContext);
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void onInitPowerManagement() {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mStackSupervisor.initPowerManagement();
                this.mPowerManagerInternal = (PowerManagerInternal) LocalServices.getService(PowerManagerInternal.class);
                this.mVoiceWakeLock = ((PowerManager) this.mContext.getSystemService("power")).newWakeLock(1, "*voice*");
                this.mVoiceWakeLock.setReferenceCounted(false);
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void installSystemProviders() {
        this.mFontScaleSettingObserver = new FontScaleSettingObserver();
    }

    /* JADX WARNING: Removed duplicated region for block: B:41:0x0100 A[Catch:{ all -> 0x011e }] */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x010f A[Catch:{ all -> 0x011e }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void retrieveSettings(android.content.ContentResolver r18) {
        /*
            r17 = this;
            r1 = r17
            r2 = r18
            android.content.Context r0 = r1.mContext
            android.content.pm.PackageManager r0 = r0.getPackageManager()
            java.lang.String r3 = "android.software.freeform_window_management"
            boolean r0 = r0.hasSystemFeature(r3)
            r3 = 1
            r4 = 0
            if (r0 != 0) goto L_0x001f
            java.lang.String r0 = "enable_freeform_support"
            int r0 = android.provider.Settings.Global.getInt(r2, r0, r4)
            if (r0 == 0) goto L_0x001d
            goto L_0x001f
        L_0x001d:
            r0 = r4
            goto L_0x0020
        L_0x001f:
            r0 = r3
        L_0x0020:
            r5 = r0
            android.content.Context r0 = r1.mContext
            boolean r6 = android.app.ActivityTaskManager.supportsMultiWindow(r0)
            if (r6 == 0) goto L_0x0039
            android.content.Context r0 = r1.mContext
            android.content.pm.PackageManager r0 = r0.getPackageManager()
            java.lang.String r7 = "android.software.picture_in_picture"
            boolean r0 = r0.hasSystemFeature(r7)
            if (r0 == 0) goto L_0x0039
            r0 = r3
            goto L_0x003a
        L_0x0039:
            r0 = r4
        L_0x003a:
            r7 = r0
            android.content.Context r0 = r1.mContext
            boolean r8 = android.app.ActivityTaskManager.supportsSplitScreenMultiWindow(r0)
            android.content.Context r0 = r1.mContext
            android.content.pm.PackageManager r0 = r0.getPackageManager()
            java.lang.String r9 = "android.software.activities_on_secondary_displays"
            boolean r9 = r0.hasSystemFeature(r9)
            java.lang.String r0 = "debug.force_rtl"
            int r0 = android.provider.Settings.Global.getInt(r2, r0, r4)
            if (r0 == 0) goto L_0x0057
            r0 = r3
            goto L_0x0058
        L_0x0057:
            r0 = r4
        L_0x0058:
            r10 = r0
            java.lang.String r0 = "force_resizable_activities"
            int r0 = android.provider.Settings.Global.getInt(r2, r0, r4)
            if (r0 == 0) goto L_0x0063
            r0 = r3
            goto L_0x0064
        L_0x0063:
            r0 = r4
        L_0x0064:
            r11 = r0
            android.content.Context r0 = r1.mContext
            android.content.pm.PackageManager r0 = r0.getPackageManager()
            java.lang.String r12 = "android.hardware.type.pc"
            boolean r12 = r0.hasSystemFeature(r12)
            java.lang.Boolean r0 = java.lang.Boolean.valueOf(r10)
            android.sysprop.DisplayProperties.debug_force_rtl(r0)
            android.content.res.Configuration r0 = new android.content.res.Configuration
            r0.<init>()
            r13 = r0
            android.provider.Settings.System.getConfiguration(r2, r13)
            if (r10 == 0) goto L_0x0088
            java.util.Locale r0 = r13.locale
            r13.setLayoutDirection(r0)
        L_0x0088:
            com.android.server.wm.WindowManagerGlobalLock r14 = r1.mGlobalLock
            monitor-enter(r14)
            com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x011e }
            r1.mForceResizableActivities = r11     // Catch:{ all -> 0x011e }
            if (r5 != 0) goto L_0x009b
            if (r8 != 0) goto L_0x009b
            if (r7 != 0) goto L_0x009b
            if (r9 == 0) goto L_0x0099
            goto L_0x009b
        L_0x0099:
            r0 = r4
            goto L_0x009c
        L_0x009b:
            r0 = r3
        L_0x009c:
            if (r6 != 0) goto L_0x00a0
            if (r11 == 0) goto L_0x00ad
        L_0x00a0:
            if (r0 == 0) goto L_0x00ad
            r1.mSupportsMultiWindow = r3     // Catch:{ all -> 0x011e }
            r1.mSupportsFreeformWindowManagement = r5     // Catch:{ all -> 0x011e }
            r1.mSupportsSplitScreenMultiWindow = r8     // Catch:{ all -> 0x011e }
            r1.mSupportsPictureInPicture = r7     // Catch:{ all -> 0x011e }
            r1.mSupportsMultiDisplay = r9     // Catch:{ all -> 0x011e }
            goto L_0x00b7
        L_0x00ad:
            r1.mSupportsMultiWindow = r4     // Catch:{ all -> 0x011e }
            r1.mSupportsFreeformWindowManagement = r4     // Catch:{ all -> 0x011e }
            r1.mSupportsSplitScreenMultiWindow = r4     // Catch:{ all -> 0x011e }
            r1.mSupportsPictureInPicture = r4     // Catch:{ all -> 0x011e }
            r1.mSupportsMultiDisplay = r4     // Catch:{ all -> 0x011e }
        L_0x00b7:
            com.android.server.wm.WindowManagerService r4 = r1.mWindowManager     // Catch:{ all -> 0x011e }
            boolean r15 = r1.mForceResizableActivities     // Catch:{ all -> 0x011e }
            r4.setForceResizableTasks(r15)     // Catch:{ all -> 0x011e }
            com.android.server.wm.WindowManagerService r4 = r1.mWindowManager     // Catch:{ all -> 0x011e }
            boolean r15 = r1.mSupportsPictureInPicture     // Catch:{ all -> 0x011e }
            r4.setSupportsPictureInPicture(r15)     // Catch:{ all -> 0x011e }
            com.android.server.wm.WindowManagerService r4 = r1.mWindowManager     // Catch:{ all -> 0x011e }
            boolean r15 = r1.mSupportsFreeformWindowManagement     // Catch:{ all -> 0x011e }
            r4.setSupportsFreeformWindowManagement(r15)     // Catch:{ all -> 0x011e }
            com.android.server.wm.WindowManagerService r4 = r1.mWindowManager     // Catch:{ all -> 0x011e }
            r4.setIsPc(r12)     // Catch:{ all -> 0x011e }
            com.android.server.wm.WindowManagerService r4 = r1.mWindowManager     // Catch:{ all -> 0x011e }
            com.android.server.wm.RootWindowContainer r4 = r4.mRoot     // Catch:{ all -> 0x011e }
            r4.onSettingsRetrieved()     // Catch:{ all -> 0x011e }
            r4 = 0
            r1.updateConfigurationLocked(r13, r4, r3)     // Catch:{ all -> 0x011e }
            android.content.res.Configuration r4 = r17.getGlobalConfiguration()     // Catch:{ all -> 0x011e }
            android.content.Context r15 = r1.mContext     // Catch:{ all -> 0x011e }
            android.content.res.Resources r15 = r15.getResources()     // Catch:{ all -> 0x011e }
            r3 = 17104898(0x1050002, float:2.4428248E-38)
            int r3 = r15.getDimensionPixelSize(r3)     // Catch:{ all -> 0x011e }
            r1.mThumbnailWidth = r3     // Catch:{ all -> 0x011e }
            r3 = 17104897(0x1050001, float:2.4428245E-38)
            int r3 = r15.getDimensionPixelSize(r3)     // Catch:{ all -> 0x011e }
            r1.mThumbnailHeight = r3     // Catch:{ all -> 0x011e }
            int r3 = r4.uiMode     // Catch:{ all -> 0x011e }
            r16 = r0
            r0 = 4
            r3 = r3 & r0
            if (r3 != r0) goto L_0x010f
            r0 = 17695006(0x10e011e, float:2.6082082E-38)
            int r0 = r15.getInteger(r0)     // Catch:{ all -> 0x011e }
            float r0 = (float) r0     // Catch:{ all -> 0x011e }
            int r3 = r4.screenWidthDp     // Catch:{ all -> 0x011e }
            float r3 = (float) r3     // Catch:{ all -> 0x011e }
            float r0 = r0 / r3
            r1.mFullscreenThumbnailScale = r0     // Catch:{ all -> 0x011e }
            goto L_0x0119
        L_0x010f:
            r0 = 18022414(0x113000e, float:2.6999675E-38)
            r3 = 1
            float r0 = r15.getFraction(r0, r3, r3)     // Catch:{ all -> 0x011e }
            r1.mFullscreenThumbnailScale = r0     // Catch:{ all -> 0x011e }
        L_0x0119:
            monitor-exit(r14)     // Catch:{ all -> 0x011e }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return
        L_0x011e:
            r0 = move-exception
            monitor-exit(r14)     // Catch:{ all -> 0x011e }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityTaskManagerService.retrieveSettings(android.content.ContentResolver):void");
    }

    public WindowManagerGlobalLock getGlobalLock() {
        return this.mGlobalLock;
    }

    @VisibleForTesting
    public ActivityTaskManagerInternal getAtmInternal() {
        return this.mInternal;
    }

    public void initialize(IntentFirewall intentFirewall, PendingIntentController intentController, Looper looper) {
        this.mH = new H(looper);
        this.mUiHandler = new UiHandler();
        this.mIntentFirewall = intentFirewall;
        File systemDir = SystemServiceManager.ensureSystemDir();
        this.mAppWarnings = new AppWarnings(this, this.mUiContext, this.mH, this.mUiHandler, systemDir);
        this.mCompatModePackages = new CompatModePackages(this, systemDir, this.mH);
        this.mPendingIntentController = intentController;
        this.mTempConfig.setToDefaults();
        this.mTempConfig.setLocales(LocaleList.getDefault());
        this.mTempConfig.seq = 1;
        this.mConfigurationSeq = 1;
        this.mStackSupervisor = createStackSupervisor();
        this.mRootActivityContainer = new RootActivityContainer(this);
        this.mRootActivityContainer.onConfigurationChanged(this.mTempConfig);
        this.mTaskChangeNotificationController = new TaskChangeNotificationController(this.mGlobalLock, this.mStackSupervisor, this.mH);
        this.mLockTaskController = new LockTaskController(this.mContext, this.mStackSupervisor, this.mH);
        this.mActivityStartController = new ActivityStartController(this);
        this.mRecentTasks = createRecentTasks();
        this.mStackSupervisor.setRecentTasks(this.mRecentTasks);
        this.mVrController = new VrController(this.mGlobalLock);
        this.mKeyguardController = this.mStackSupervisor.getKeyguardController();
    }

    public void onActivityManagerInternalAdded() {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mAmInternal = (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
                this.mUgmInternal = (UriGrantsManagerInternal) LocalServices.getService(UriGrantsManagerInternal.class);
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    /* access modifiers changed from: package-private */
    public int increaseConfigurationSeqLocked() {
        int i = this.mConfigurationSeq + 1;
        this.mConfigurationSeq = i;
        this.mConfigurationSeq = Math.max(i, 1);
        return this.mConfigurationSeq;
    }

    /* access modifiers changed from: protected */
    public ActivityStackSupervisor createStackSupervisor() {
        ActivityStackSupervisor supervisor = new ActivityStackSupervisor(this, this.mH.getLooper());
        supervisor.initialize();
        return supervisor;
    }

    public void setWindowManager(WindowManagerService wm) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mWindowManager = wm;
                this.mLockTaskController.setWindowManager(wm);
                this.mStackSupervisor.setWindowManager(wm);
                this.mRootActivityContainer.setWindowManager(wm);
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void setUsageStatsManager(UsageStatsManagerInternal usageStatsManager) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mUsageStatsInternal = usageStatsManager;
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    /* access modifiers changed from: package-private */
    public UserManagerService getUserManager() {
        if (this.mUserManager == null) {
            this.mUserManager = IUserManager.Stub.asInterface(ServiceManager.getService("user"));
        }
        return this.mUserManager;
    }

    /* access modifiers changed from: package-private */
    public AppOpsService getAppOpsService() {
        if (this.mAppOpsService == null) {
            this.mAppOpsService = IAppOpsService.Stub.asInterface(ServiceManager.getService("appops"));
        }
        return this.mAppOpsService;
    }

    /* access modifiers changed from: package-private */
    public boolean hasUserRestriction(String restriction, int userId) {
        return getUserManager().hasUserRestriction(restriction, userId);
    }

    /* access modifiers changed from: package-private */
    public boolean hasSystemAlertWindowPermission(int callingUid, int callingPid, String callingPackage) {
        int mode = getAppOpsService().noteOperation(24, callingUid, callingPackage);
        if (mode == 3) {
            if (checkPermission("android.permission.SYSTEM_ALERT_WINDOW", callingPid, callingUid) == 0) {
                return true;
            }
            return false;
        } else if (mode == 0) {
            return true;
        } else {
            return false;
        }
    }

    /* access modifiers changed from: protected */
    public RecentTasks createRecentTasks() {
        return new RecentTasks(this, this.mStackSupervisor);
    }

    /* access modifiers changed from: package-private */
    public RecentTasks getRecentTasks() {
        return this.mRecentTasks;
    }

    /* access modifiers changed from: package-private */
    public ClientLifecycleManager getLifecycleManager() {
        return this.mLifecycleManager;
    }

    /* access modifiers changed from: package-private */
    public ActivityStartController getActivityStartController() {
        return this.mActivityStartController;
    }

    /* access modifiers changed from: package-private */
    public TaskChangeNotificationController getTaskChangeNotificationController() {
        return this.mTaskChangeNotificationController;
    }

    /* access modifiers changed from: package-private */
    public LockTaskController getLockTaskController() {
        return this.mLockTaskController;
    }

    /* access modifiers changed from: package-private */
    public Configuration getGlobalConfigurationForCallingPid() {
        return getGlobalConfigurationForPid(Binder.getCallingPid());
    }

    /* access modifiers changed from: package-private */
    public Configuration getGlobalConfigurationForPid(int pid) {
        Configuration configuration;
        if (pid == ActivityManagerService.MY_PID || pid < 0) {
            return getGlobalConfiguration();
        }
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                WindowProcessController app = this.mProcessMap.getProcess(pid);
                configuration = app != null ? app.getConfiguration() : getGlobalConfiguration();
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return configuration;
    }

    public ConfigurationInfo getDeviceConfigurationInfo() {
        ConfigurationInfo config = new ConfigurationInfo();
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                Configuration globalConfig = getGlobalConfigurationForCallingPid();
                config.reqTouchScreen = globalConfig.touchscreen;
                config.reqKeyboardType = globalConfig.keyboard;
                config.reqNavigation = globalConfig.navigation;
                if (globalConfig.navigation == 2 || globalConfig.navigation == 3) {
                    config.reqInputFeatures |= 2;
                }
                if (!(globalConfig.keyboard == 0 || globalConfig.keyboard == 1)) {
                    config.reqInputFeatures |= 1;
                }
                config.reqGlEsVersion = this.GL_ES_VERSION;
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return config;
    }

    /* access modifiers changed from: private */
    public void start() {
        LocalServices.addService(ActivityTaskManagerInternal.class, this.mInternal);
    }

    public static final class Lifecycle extends SystemService {
        private final ActivityTaskManagerService mService;

        public Lifecycle(Context context) {
            super(context);
            this.mService = new ActivityTaskManagerService(context);
        }

        /* JADX WARNING: type inference failed for: r0v0, types: [android.os.IBinder, com.android.server.wm.ActivityTaskManagerService] */
        public void onStart() {
            publishBinderService("activity_task", this.mService);
            this.mService.start();
        }

        public void onUnlockUser(int userId) {
            synchronized (this.mService.getGlobalLock()) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    this.mService.mStackSupervisor.onUserUnlocked(userId);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public void onCleanupUser(int userId) {
            synchronized (this.mService.getGlobalLock()) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    this.mService.mStackSupervisor.mLaunchParamsPersister.onCleanupUser(userId);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public ActivityTaskManagerService getService() {
            return this.mService;
        }
    }

    public final int startActivity(IApplicationThread caller, String callingPackage, Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode, int startFlags, ProfilerInfo profilerInfo, Bundle bOptions) {
        return startActivityAsUser(caller, callingPackage, intent, resolvedType, resultTo, resultWho, requestCode, startFlags, profilerInfo, bOptions, UserHandle.getCallingUserId());
    }

    public final int startActivities(IApplicationThread caller, String callingPackage, Intent[] intents, String[] resolvedTypes, IBinder resultTo, Bundle bOptions, int userId) {
        enforceNotIsolatedCaller("startActivities");
        return getActivityStartController().startActivities(caller, -1, 0, -1, callingPackage, intents, resolvedTypes, resultTo, SafeActivityOptions.fromBundle(bOptions), handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), userId, "startActivities"), "startActivities", (PendingIntentRecord) null, false);
    }

    public int startActivityAsUser(IApplicationThread caller, String callingPackage, Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode, int startFlags, ProfilerInfo profilerInfo, Bundle bOptions, int userId) {
        return startActivityAsUser(caller, callingPackage, intent, resolvedType, resultTo, resultWho, requestCode, startFlags, profilerInfo, bOptions, userId, true);
    }

    /* access modifiers changed from: package-private */
    public int startActivityAsUser(IApplicationThread caller, String callingPackage, Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode, int startFlags, ProfilerInfo profilerInfo, Bundle bOptions, int userId, boolean validateIncomingUser) {
        enforceNotIsolatedCaller("startActivityAsUser");
        IApplicationThread iApplicationThread = caller;
        String str = callingPackage;
        return getActivityStartController().obtainStarter(intent, "startActivityAsUser").setCaller(caller).setCallingPackage(callingPackage).setResolvedType(resolvedType).setResultTo(resultTo).setResultWho(resultWho).setRequestCode(requestCode).setStartFlags(startFlags).setProfilerInfo(profilerInfo).setActivityOptions(bOptions).setMayWait(getActivityStartController().checkTargetUser(userId, validateIncomingUser, Binder.getCallingPid(), Binder.getCallingUid(), "startActivityAsUser")).execute();
    }

    public int startActivityIntentSender(IApplicationThread caller, IIntentSender target, IBinder whitelistToken, Intent fillInIntent, String resolvedType, IBinder resultTo, String resultWho, int requestCode, int flagsMask, int flagsValues, Bundle bOptions) {
        IIntentSender iIntentSender = target;
        enforceNotIsolatedCaller("startActivityIntentSender");
        if (fillInIntent != null && fillInIntent.hasFileDescriptors()) {
            throw new IllegalArgumentException("File descriptors passed in Intent");
        } else if (iIntentSender instanceof PendingIntentRecord) {
            PendingIntentRecord pir = (PendingIntentRecord) iIntentSender;
            synchronized (this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityStack stack = getTopDisplayFocusedStack();
                    if (stack.mResumedActivity != null && stack.mResumedActivity.info.applicationInfo.uid == Binder.getCallingUid()) {
                        this.mAppSwitchesAllowedTime = 0;
                    }
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return pir.sendInner(0, fillInIntent, resolvedType, whitelistToken, (IIntentReceiver) null, (String) null, resultTo, resultWho, requestCode, flagsMask, flagsValues, bOptions);
        } else {
            throw new IllegalArgumentException("Bad PendingIntent object");
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:60:0x0121, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x0124, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean startNextMatchingActivity(android.os.IBinder r17, android.content.Intent r18, android.os.Bundle r19) {
        /*
            r16 = this;
            r1 = r18
            if (r1 == 0) goto L_0x0013
            boolean r0 = r18.hasFileDescriptors()
            if (r0 != 0) goto L_0x000b
            goto L_0x0013
        L_0x000b:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.String r2 = "File descriptors passed in Intent"
            r0.<init>(r2)
            throw r0
        L_0x0013:
            com.android.server.wm.SafeActivityOptions r2 = com.android.server.wm.SafeActivityOptions.fromBundle(r19)
            r3 = r16
            com.android.server.wm.WindowManagerGlobalLock r4 = r3.mGlobalLock
            monitor-enter(r4)
            com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x01bb }
            com.android.server.wm.ActivityRecord r0 = com.android.server.wm.ActivityRecord.isInStackLocked(r17)     // Catch:{ all -> 0x01bb }
            r5 = r0
            r6 = 0
            if (r5 != 0) goto L_0x002f
            com.android.server.wm.SafeActivityOptions.abort(r2)     // Catch:{ all -> 0x01bb }
            monitor-exit(r4)     // Catch:{ all -> 0x01bb }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return r6
        L_0x002f:
            boolean r0 = r5.attachedToProcess()     // Catch:{ all -> 0x01bb }
            if (r0 != 0) goto L_0x003d
            com.android.server.wm.SafeActivityOptions.abort(r2)     // Catch:{ all -> 0x01bb }
            monitor-exit(r4)     // Catch:{ all -> 0x01bb }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return r6
        L_0x003d:
            android.content.Intent r0 = new android.content.Intent     // Catch:{ all -> 0x01bb }
            r0.<init>(r1)     // Catch:{ all -> 0x01bb }
            r1 = r0
            android.content.Intent r0 = r5.intent     // Catch:{ all -> 0x01c1 }
            android.net.Uri r0 = r0.getData()     // Catch:{ all -> 0x01c1 }
            android.content.Intent r7 = r5.intent     // Catch:{ all -> 0x01c1 }
            java.lang.String r7 = r7.getType()     // Catch:{ all -> 0x01c1 }
            r1.setDataAndType(r0, r7)     // Catch:{ all -> 0x01c1 }
            r7 = 0
            r1.setComponent(r7)     // Catch:{ all -> 0x01c1 }
            int r0 = r1.getFlags()     // Catch:{ all -> 0x01c1 }
            r0 = r0 & 8
            r8 = 1
            if (r0 == 0) goto L_0x0061
            r0 = r8
            goto L_0x0062
        L_0x0061:
            r0 = r6
        L_0x0062:
            r9 = r0
            r10 = 0
            android.content.pm.IPackageManager r0 = android.app.AppGlobals.getPackageManager()     // Catch:{ RemoteException -> 0x0111 }
            java.lang.String r11 = r5.resolvedType     // Catch:{ RemoteException -> 0x0111 }
            r12 = 66560(0x10400, float:9.327E-41)
            int r13 = android.os.UserHandle.getCallingUserId()     // Catch:{ RemoteException -> 0x0111 }
            android.content.pm.ParceledListSlice r0 = r0.queryIntentActivities(r1, r11, r12, r13)     // Catch:{ RemoteException -> 0x0111 }
            java.util.List r0 = r0.getList()     // Catch:{ RemoteException -> 0x0111 }
            if (r0 == 0) goto L_0x0080
            int r11 = r0.size()     // Catch:{ RemoteException -> 0x0111 }
            goto L_0x0081
        L_0x0080:
            r11 = r6
        L_0x0081:
            r12 = 0
        L_0x0082:
            if (r12 >= r11) goto L_0x0110
            java.lang.Object r13 = r0.get(r12)     // Catch:{ RemoteException -> 0x0111 }
            android.content.pm.ResolveInfo r13 = (android.content.pm.ResolveInfo) r13     // Catch:{ RemoteException -> 0x0111 }
            android.content.pm.ActivityInfo r14 = r13.activityInfo     // Catch:{ RemoteException -> 0x0111 }
            java.lang.String r14 = r14.packageName     // Catch:{ RemoteException -> 0x0111 }
            java.lang.String r15 = r5.packageName     // Catch:{ RemoteException -> 0x0111 }
            boolean r14 = r14.equals(r15)     // Catch:{ RemoteException -> 0x0111 }
            if (r14 == 0) goto L_0x010a
            android.content.pm.ActivityInfo r14 = r13.activityInfo     // Catch:{ RemoteException -> 0x0111 }
            java.lang.String r14 = r14.name     // Catch:{ RemoteException -> 0x0111 }
            android.content.pm.ActivityInfo r15 = r5.info     // Catch:{ RemoteException -> 0x0111 }
            java.lang.String r15 = r15.name     // Catch:{ RemoteException -> 0x0111 }
            boolean r14 = r14.equals(r15)     // Catch:{ RemoteException -> 0x0111 }
            if (r14 == 0) goto L_0x010a
            int r12 = r12 + r8
            if (r12 >= r11) goto L_0x00b0
            java.lang.Object r14 = r0.get(r12)     // Catch:{ RemoteException -> 0x0111 }
            android.content.pm.ResolveInfo r14 = (android.content.pm.ResolveInfo) r14     // Catch:{ RemoteException -> 0x0111 }
            android.content.pm.ActivityInfo r14 = r14.activityInfo     // Catch:{ RemoteException -> 0x0111 }
            r10 = r14
        L_0x00b0:
            if (r9 == 0) goto L_0x0110
            java.lang.String r14 = "ActivityTaskManager"
            java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x0111 }
            r15.<init>()     // Catch:{ RemoteException -> 0x0111 }
            java.lang.String r7 = "Next matching activity: found current "
            r15.append(r7)     // Catch:{ RemoteException -> 0x0111 }
            java.lang.String r7 = r5.packageName     // Catch:{ RemoteException -> 0x0111 }
            r15.append(r7)     // Catch:{ RemoteException -> 0x0111 }
            java.lang.String r7 = "/"
            r15.append(r7)     // Catch:{ RemoteException -> 0x0111 }
            android.content.pm.ActivityInfo r7 = r5.info     // Catch:{ RemoteException -> 0x0111 }
            java.lang.String r7 = r7.name     // Catch:{ RemoteException -> 0x0111 }
            r15.append(r7)     // Catch:{ RemoteException -> 0x0111 }
            java.lang.String r7 = r15.toString()     // Catch:{ RemoteException -> 0x0111 }
            android.util.Slog.v(r14, r7)     // Catch:{ RemoteException -> 0x0111 }
            java.lang.String r7 = "ActivityTaskManager"
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x0111 }
            r14.<init>()     // Catch:{ RemoteException -> 0x0111 }
            java.lang.String r15 = "Next matching activity: next is "
            r14.append(r15)     // Catch:{ RemoteException -> 0x0111 }
            if (r10 != 0) goto L_0x00e7
            java.lang.String r15 = "null"
            goto L_0x00ff
        L_0x00e7:
            java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x0111 }
            r15.<init>()     // Catch:{ RemoteException -> 0x0111 }
            java.lang.String r8 = r10.packageName     // Catch:{ RemoteException -> 0x0111 }
            r15.append(r8)     // Catch:{ RemoteException -> 0x0111 }
            java.lang.String r8 = "/"
            r15.append(r8)     // Catch:{ RemoteException -> 0x0111 }
            java.lang.String r8 = r10.name     // Catch:{ RemoteException -> 0x0111 }
            r15.append(r8)     // Catch:{ RemoteException -> 0x0111 }
            java.lang.String r15 = r15.toString()     // Catch:{ RemoteException -> 0x0111 }
        L_0x00ff:
            r14.append(r15)     // Catch:{ RemoteException -> 0x0111 }
            java.lang.String r8 = r14.toString()     // Catch:{ RemoteException -> 0x0111 }
            android.util.Slog.v(r7, r8)     // Catch:{ RemoteException -> 0x0111 }
            goto L_0x0110
        L_0x010a:
            int r12 = r12 + 1
            r7 = 0
            r8 = 1
            goto L_0x0082
        L_0x0110:
            goto L_0x0112
        L_0x0111:
            r0 = move-exception
        L_0x0112:
            if (r10 != 0) goto L_0x0125
            com.android.server.wm.SafeActivityOptions.abort(r2)     // Catch:{ all -> 0x01c1 }
            if (r9 == 0) goto L_0x0120
            java.lang.String r0 = "ActivityTaskManager"
            java.lang.String r7 = "Next matching activity: nothing found"
            android.util.Slog.d(r0, r7)     // Catch:{ all -> 0x01c1 }
        L_0x0120:
            monitor-exit(r4)     // Catch:{ all -> 0x01c1 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return r6
        L_0x0125:
            android.content.ComponentName r0 = new android.content.ComponentName     // Catch:{ all -> 0x01c1 }
            android.content.pm.ApplicationInfo r7 = r10.applicationInfo     // Catch:{ all -> 0x01c1 }
            java.lang.String r7 = r7.packageName     // Catch:{ all -> 0x01c1 }
            java.lang.String r8 = r10.name     // Catch:{ all -> 0x01c1 }
            r0.<init>(r7, r8)     // Catch:{ all -> 0x01c1 }
            r1.setComponent(r0)     // Catch:{ all -> 0x01c1 }
            int r0 = r1.getFlags()     // Catch:{ all -> 0x01c1 }
            r7 = -503316481(0xffffffffe1ffffff, float:-5.9029578E20)
            r0 = r0 & r7
            r1.setFlags(r0)     // Catch:{ all -> 0x01c1 }
            boolean r0 = r5.finishing     // Catch:{ all -> 0x01c1 }
            r7 = 1
            r5.finishing = r7     // Catch:{ all -> 0x01c1 }
            com.android.server.wm.ActivityRecord r7 = r5.resultTo     // Catch:{ all -> 0x01c1 }
            java.lang.String r8 = r5.resultWho     // Catch:{ all -> 0x01c1 }
            int r11 = r5.requestCode     // Catch:{ all -> 0x01c1 }
            r12 = 0
            r5.resultTo = r12     // Catch:{ all -> 0x01c1 }
            if (r7 == 0) goto L_0x0151
            r7.removeResultsLocked(r5, r8, r11)     // Catch:{ all -> 0x01c1 }
        L_0x0151:
            long r13 = android.os.Binder.clearCallingIdentity()     // Catch:{ all -> 0x01c1 }
            com.android.server.wm.ActivityStartController r15 = r16.getActivityStartController()     // Catch:{ all -> 0x01c1 }
            java.lang.String r12 = "startNextMatchingActivity"
            com.android.server.wm.ActivityStarter r12 = r15.obtainStarter(r1, r12)     // Catch:{ all -> 0x01c1 }
            com.android.server.wm.WindowProcessController r15 = r5.app     // Catch:{ all -> 0x01c1 }
            android.app.IApplicationThread r15 = r15.getThread()     // Catch:{ all -> 0x01c1 }
            com.android.server.wm.ActivityStarter r12 = r12.setCaller(r15)     // Catch:{ all -> 0x01c1 }
            java.lang.String r15 = r5.resolvedType     // Catch:{ all -> 0x01c1 }
            com.android.server.wm.ActivityStarter r12 = r12.setResolvedType(r15)     // Catch:{ all -> 0x01c1 }
            com.android.server.wm.ActivityStarter r12 = r12.setActivityInfo(r10)     // Catch:{ all -> 0x01c1 }
            if (r7 == 0) goto L_0x0178
            android.view.IApplicationToken$Stub r15 = r7.appToken     // Catch:{ all -> 0x01c1 }
            goto L_0x0179
        L_0x0178:
            r15 = 0
        L_0x0179:
            com.android.server.wm.ActivityStarter r12 = r12.setResultTo(r15)     // Catch:{ all -> 0x01c1 }
            com.android.server.wm.ActivityStarter r12 = r12.setResultWho(r8)     // Catch:{ all -> 0x01c1 }
            com.android.server.wm.ActivityStarter r12 = r12.setRequestCode(r11)     // Catch:{ all -> 0x01c1 }
            r15 = -1
            com.android.server.wm.ActivityStarter r12 = r12.setCallingPid(r15)     // Catch:{ all -> 0x01c1 }
            int r6 = r5.launchedFromUid     // Catch:{ all -> 0x01c1 }
            com.android.server.wm.ActivityStarter r6 = r12.setCallingUid(r6)     // Catch:{ all -> 0x01c1 }
            java.lang.String r12 = r5.launchedFromPackage     // Catch:{ all -> 0x01c1 }
            com.android.server.wm.ActivityStarter r6 = r6.setCallingPackage(r12)     // Catch:{ all -> 0x01c1 }
            com.android.server.wm.ActivityStarter r6 = r6.setRealCallingPid(r15)     // Catch:{ all -> 0x01c1 }
            int r12 = r5.launchedFromUid     // Catch:{ all -> 0x01c1 }
            com.android.server.wm.ActivityStarter r6 = r6.setRealCallingUid(r12)     // Catch:{ all -> 0x01c1 }
            com.android.server.wm.ActivityStarter r6 = r6.setActivityOptions((com.android.server.wm.SafeActivityOptions) r2)     // Catch:{ all -> 0x01c1 }
            int r6 = r6.execute()     // Catch:{ all -> 0x01c1 }
            android.os.Binder.restoreCallingIdentity(r13)     // Catch:{ all -> 0x01c1 }
            r5.finishing = r0     // Catch:{ all -> 0x01c1 }
            if (r6 == 0) goto L_0x01b5
            monitor-exit(r4)     // Catch:{ all -> 0x01c1 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            r4 = 0
            return r4
        L_0x01b5:
            monitor-exit(r4)     // Catch:{ all -> 0x01c1 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            r4 = 1
            return r4
        L_0x01bb:
            r0 = move-exception
        L_0x01bc:
            monitor-exit(r4)     // Catch:{ all -> 0x01c1 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            throw r0
        L_0x01c1:
            r0 = move-exception
            goto L_0x01bc
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityTaskManagerService.startNextMatchingActivity(android.os.IBinder, android.content.Intent, android.os.Bundle):boolean");
    }

    public final WaitResult startActivityAndWait(IApplicationThread caller, String callingPackage, Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode, int startFlags, ProfilerInfo profilerInfo, Bundle bOptions, int userId) {
        int i;
        WaitResult res = new WaitResult();
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                enforceNotIsolatedCaller("startActivityAndWait");
                i = userId;
                try {
                } catch (Throwable th) {
                    th = th;
                    IApplicationThread iApplicationThread = caller;
                    String str = callingPackage;
                    Intent intent2 = intent;
                    String str2 = resolvedType;
                    IBinder iBinder = resultTo;
                    String str3 = resultWho;
                    int i2 = requestCode;
                    int i3 = startFlags;
                    ProfilerInfo profilerInfo2 = profilerInfo;
                    Bundle bundle = bOptions;
                    int i4 = i;
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
                try {
                    try {
                        try {
                            try {
                                try {
                                } catch (Throwable th2) {
                                    th = th2;
                                    IBinder iBinder2 = resultTo;
                                    String str4 = resultWho;
                                    int i5 = requestCode;
                                    int i6 = startFlags;
                                    ProfilerInfo profilerInfo3 = profilerInfo;
                                    Bundle bundle2 = bOptions;
                                    WindowManagerService.resetPriorityAfterLockedSection();
                                    throw th;
                                }
                            } catch (Throwable th3) {
                                th = th3;
                                String str5 = resolvedType;
                                IBinder iBinder22 = resultTo;
                                String str42 = resultWho;
                                int i52 = requestCode;
                                int i62 = startFlags;
                                ProfilerInfo profilerInfo32 = profilerInfo;
                                Bundle bundle22 = bOptions;
                                WindowManagerService.resetPriorityAfterLockedSection();
                                throw th;
                            }
                        } catch (Throwable th4) {
                            th = th4;
                            String str6 = callingPackage;
                            String str52 = resolvedType;
                            IBinder iBinder222 = resultTo;
                            String str422 = resultWho;
                            int i522 = requestCode;
                            int i622 = startFlags;
                            ProfilerInfo profilerInfo322 = profilerInfo;
                            Bundle bundle222 = bOptions;
                            WindowManagerService.resetPriorityAfterLockedSection();
                            throw th;
                        }
                    } catch (Throwable th5) {
                        th = th5;
                        IApplicationThread iApplicationThread2 = caller;
                        String str62 = callingPackage;
                        String str522 = resolvedType;
                        IBinder iBinder2222 = resultTo;
                        String str4222 = resultWho;
                        int i5222 = requestCode;
                        int i6222 = startFlags;
                        ProfilerInfo profilerInfo3222 = profilerInfo;
                        Bundle bundle2222 = bOptions;
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                } catch (Throwable th6) {
                    th = th6;
                    IApplicationThread iApplicationThread3 = caller;
                    String str7 = callingPackage;
                    Intent intent3 = intent;
                    String str5222 = resolvedType;
                    IBinder iBinder22222 = resultTo;
                    String str42222 = resultWho;
                    int i52222 = requestCode;
                    int i62222 = startFlags;
                    ProfilerInfo profilerInfo32222 = profilerInfo;
                    Bundle bundle22222 = bOptions;
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
                try {
                    try {
                        try {
                            try {
                                try {
                                } catch (Throwable th7) {
                                    th = th7;
                                    ProfilerInfo profilerInfo4 = profilerInfo;
                                    WindowManagerService.resetPriorityAfterLockedSection();
                                    throw th;
                                }
                            } catch (Throwable th8) {
                                th = th8;
                                ProfilerInfo profilerInfo322222 = profilerInfo;
                                Bundle bundle222222 = bOptions;
                                WindowManagerService.resetPriorityAfterLockedSection();
                                throw th;
                            }
                        } catch (Throwable th9) {
                            th = th9;
                            int i622222 = startFlags;
                            ProfilerInfo profilerInfo3222222 = profilerInfo;
                            Bundle bundle2222222 = bOptions;
                            WindowManagerService.resetPriorityAfterLockedSection();
                            throw th;
                        }
                    } catch (Throwable th10) {
                        th = th10;
                        int i522222 = requestCode;
                        int i6222222 = startFlags;
                        ProfilerInfo profilerInfo32222222 = profilerInfo;
                        Bundle bundle22222222 = bOptions;
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                    try {
                        getActivityStartController().obtainStarter(intent, "startActivityAndWait").setCaller(caller).setCallingPackage(callingPackage).setResolvedType(resolvedType).setResultTo(resultTo).setResultWho(resultWho).setRequestCode(requestCode).setStartFlags(startFlags).setActivityOptions(bOptions).setMayWait(handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i, "startActivityAndWait")).setProfilerInfo(profilerInfo).setWaitResult(res).execute();
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return res;
                    } catch (Throwable th11) {
                        th = th11;
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                } catch (Throwable th12) {
                    th = th12;
                    String str422222 = resultWho;
                    int i5222222 = requestCode;
                    int i62222222 = startFlags;
                    ProfilerInfo profilerInfo322222222 = profilerInfo;
                    Bundle bundle222222222 = bOptions;
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            } catch (Throwable th13) {
                th = th13;
                IApplicationThread iApplicationThread4 = caller;
                String str8 = callingPackage;
                Intent intent4 = intent;
                String str9 = resolvedType;
                IBinder iBinder3 = resultTo;
                String str10 = resultWho;
                int i7 = requestCode;
                int i8 = startFlags;
                ProfilerInfo profilerInfo5 = profilerInfo;
                Bundle bundle3 = bOptions;
                i = userId;
                int i42 = i;
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public final int startActivityWithConfig(IApplicationThread caller, String callingPackage, Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode, int startFlags, Configuration config, Bundle bOptions, int userId) {
        int execute;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                enforceNotIsolatedCaller("startActivityWithConfig");
                execute = getActivityStartController().obtainStarter(intent, "startActivityWithConfig").setCaller(caller).setCallingPackage(callingPackage).setResolvedType(resolvedType).setResultTo(resultTo).setResultWho(resultWho).setRequestCode(requestCode).setStartFlags(startFlags).setGlobalConfiguration(config).setActivityOptions(bOptions).setMayWait(handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), userId, "startActivityWithConfig")).execute();
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return execute;
    }

    public IBinder requestStartActivityPermissionToken(IBinder delegatorToken) {
        int callingUid = Binder.getCallingUid();
        if (UserHandle.getAppId(callingUid) == 1000) {
            IBinder permissionToken = new Binder();
            synchronized (this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    this.mStartActivitySources.put(permissionToken, delegatorToken);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            this.mUiHandler.sendMessageDelayed(PooledLambda.obtainMessage($$Lambda$ActivityTaskManagerService$3DTHgCAeEd5OOF7ACeXoCk8mmrQ.INSTANCE, this, permissionToken), START_AS_CALLER_TOKEN_TIMEOUT_IMPL);
            this.mUiHandler.sendMessageDelayed(PooledLambda.obtainMessage($$Lambda$ActivityTaskManagerService$7ieG0s7Zp4H2bLiWdOgB6MqhcI.INSTANCE, this, permissionToken), START_AS_CALLER_TOKEN_EXPIRED_TIMEOUT);
            return permissionToken;
        }
        throw new SecurityException("Only the system process can request a permission token, received request from uid: " + callingUid);
    }

    /* Debug info: failed to restart local var, previous not found, register: 17 */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00d7, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
        r10 = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00de, code lost:
        if (r10 != -10000) goto L_0x00e9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x00e0, code lost:
        r10 = android.os.UserHandle.getUserId(r6.app.mUid);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:?, code lost:
        r0 = getActivityStartController().obtainStarter(r20, "startActivityAsCaller").setCallingUid(r7).setCallingPackage(r8).setResolvedType(r21).setResultTo(r2).setResultWho(r23).setRequestCode(r24).setStartFlags(r25).setActivityOptions(r27).setMayWait(r10).setIgnoreTargetSecurity(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x0127, code lost:
        if (r9 == false) goto L_0x012e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x0129, code lost:
        r1 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x012e, code lost:
        r1 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x013c, code lost:
        return r0.setFilterCallingUid(r1).setAllowBackgroundActivityStart(true).execute();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x013d, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x013f, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x0141, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:0x0143, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x0145, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:0x0147, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x0149, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:75:0x014a, code lost:
        r11 = r20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:76:0x014c, code lost:
        r12 = r21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:77:0x014e, code lost:
        r13 = r23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:78:0x0150, code lost:
        r14 = r24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:79:0x0152, code lost:
        r15 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:80:0x0154, code lost:
        r5 = r27;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:81:0x0156, code lost:
        throw r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final int startActivityAsCaller(android.app.IApplicationThread r18, java.lang.String r19, android.content.Intent r20, java.lang.String r21, android.os.IBinder r22, java.lang.String r23, int r24, int r25, android.app.ProfilerInfo r26, android.os.Bundle r27, android.os.IBinder r28, boolean r29, int r30) {
        /*
            r17 = this;
            r1 = r17
            r2 = r22
            r3 = r28
            r4 = r29
            com.android.server.wm.WindowManagerGlobalLock r5 = r1.mGlobalLock
            monitor-enter(r5)
            com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x01b6 }
            if (r2 == 0) goto L_0x01a2
            if (r3 == 0) goto L_0x005b
            android.app.ActivityManagerInternal r0 = r1.mAmInternal     // Catch:{ all -> 0x01b6 }
            java.lang.String r6 = "android.permission.START_ACTIVITY_AS_CALLER"
            java.lang.String r7 = "startActivityAsCaller"
            r0.enforceCallingPermission(r6, r7)     // Catch:{ all -> 0x01b6 }
            java.util.HashMap<android.os.IBinder, android.os.IBinder> r0 = r1.mStartActivitySources     // Catch:{ all -> 0x01b6 }
            java.lang.Object r0 = r0.remove(r3)     // Catch:{ all -> 0x01b6 }
            android.os.IBinder r0 = (android.os.IBinder) r0     // Catch:{ all -> 0x01b6 }
            if (r0 != 0) goto L_0x005d
            java.util.ArrayList<android.os.IBinder> r6 = r1.mExpiredStartAsCallerTokens     // Catch:{ all -> 0x01b6 }
            boolean r6 = r6.contains(r3)     // Catch:{ all -> 0x01b6 }
            if (r6 == 0) goto L_0x0044
            java.lang.SecurityException r6 = new java.lang.SecurityException     // Catch:{ all -> 0x01b6 }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x01b6 }
            r7.<init>()     // Catch:{ all -> 0x01b6 }
            java.lang.String r8 = "Called with expired permission token: "
            r7.append(r8)     // Catch:{ all -> 0x01b6 }
            r7.append(r3)     // Catch:{ all -> 0x01b6 }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x01b6 }
            r6.<init>(r7)     // Catch:{ all -> 0x01b6 }
            throw r6     // Catch:{ all -> 0x01b6 }
        L_0x0044:
            java.lang.SecurityException r6 = new java.lang.SecurityException     // Catch:{ all -> 0x01b6 }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x01b6 }
            r7.<init>()     // Catch:{ all -> 0x01b6 }
            java.lang.String r8 = "Called with invalid permission token: "
            r7.append(r8)     // Catch:{ all -> 0x01b6 }
            r7.append(r3)     // Catch:{ all -> 0x01b6 }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x01b6 }
            r6.<init>(r7)     // Catch:{ all -> 0x01b6 }
            throw r6     // Catch:{ all -> 0x01b6 }
        L_0x005b:
            r0 = r22
        L_0x005d:
            com.android.server.wm.RootActivityContainer r6 = r1.mRootActivityContainer     // Catch:{ all -> 0x01b6 }
            com.android.server.wm.ActivityRecord r6 = r6.isInAnyStack(r0)     // Catch:{ all -> 0x01b6 }
            if (r6 == 0) goto L_0x017f
            com.android.server.wm.WindowProcessController r7 = r6.app     // Catch:{ all -> 0x01b6 }
            if (r7 == 0) goto L_0x016b
            android.content.pm.ActivityInfo r7 = r6.info     // Catch:{ all -> 0x01b6 }
            java.lang.String r7 = r7.packageName     // Catch:{ all -> 0x01b6 }
            java.lang.String r8 = "android"
            boolean r7 = r7.equals(r8)     // Catch:{ all -> 0x01b6 }
            if (r7 == 0) goto L_0x0157
            com.android.server.wm.WindowProcessController r7 = r6.app     // Catch:{ all -> 0x01b6 }
            int r7 = r7.mUid     // Catch:{ all -> 0x01b6 }
            int r7 = android.os.UserHandle.getAppId(r7)     // Catch:{ all -> 0x01b6 }
            r8 = 1000(0x3e8, float:1.401E-42)
            if (r7 == r8) goto L_0x00af
            com.android.server.wm.WindowProcessController r7 = r6.app     // Catch:{ all -> 0x01b6 }
            int r7 = r7.mUid     // Catch:{ all -> 0x01b6 }
            int r8 = r6.launchedFromUid     // Catch:{ all -> 0x01b6 }
            if (r7 != r8) goto L_0x008a
            goto L_0x00af
        L_0x008a:
            java.lang.SecurityException r7 = new java.lang.SecurityException     // Catch:{ all -> 0x01b6 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x01b6 }
            r8.<init>()     // Catch:{ all -> 0x01b6 }
            java.lang.String r9 = "Calling activity in uid "
            r8.append(r9)     // Catch:{ all -> 0x01b6 }
            com.android.server.wm.WindowProcessController r9 = r6.app     // Catch:{ all -> 0x01b6 }
            int r9 = r9.mUid     // Catch:{ all -> 0x01b6 }
            r8.append(r9)     // Catch:{ all -> 0x01b6 }
            java.lang.String r9 = " must be system uid or original calling uid "
            r8.append(r9)     // Catch:{ all -> 0x01b6 }
            int r9 = r6.launchedFromUid     // Catch:{ all -> 0x01b6 }
            r8.append(r9)     // Catch:{ all -> 0x01b6 }
            java.lang.String r8 = r8.toString()     // Catch:{ all -> 0x01b6 }
            r7.<init>(r8)     // Catch:{ all -> 0x01b6 }
            throw r7     // Catch:{ all -> 0x01b6 }
        L_0x00af:
            if (r4 == 0) goto L_0x00ce
            android.content.ComponentName r7 = r20.getComponent()     // Catch:{ all -> 0x01b6 }
            if (r7 == 0) goto L_0x00c6
            android.content.Intent r7 = r20.getSelector()     // Catch:{ all -> 0x01b6 }
            if (r7 != 0) goto L_0x00be
            goto L_0x00ce
        L_0x00be:
            java.lang.SecurityException r7 = new java.lang.SecurityException     // Catch:{ all -> 0x01b6 }
            java.lang.String r8 = "Selector not allowed with ignoreTargetSecurity"
            r7.<init>(r8)     // Catch:{ all -> 0x01b6 }
            throw r7     // Catch:{ all -> 0x01b6 }
        L_0x00c6:
            java.lang.SecurityException r7 = new java.lang.SecurityException     // Catch:{ all -> 0x01b6 }
            java.lang.String r8 = "Component must be specified with ignoreTargetSecurity"
            r7.<init>(r8)     // Catch:{ all -> 0x01b6 }
            throw r7     // Catch:{ all -> 0x01b6 }
        L_0x00ce:
            int r7 = r6.launchedFromUid     // Catch:{ all -> 0x01b6 }
            java.lang.String r8 = r6.launchedFromPackage     // Catch:{ all -> 0x01b6 }
            boolean r9 = r6.isResolverOrChildActivity()     // Catch:{ all -> 0x01b6 }
            monitor-exit(r5)     // Catch:{ all -> 0x01b6 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            r0 = -10000(0xffffffffffffd8f0, float:NaN)
            r10 = r30
            if (r10 != r0) goto L_0x00e9
            com.android.server.wm.WindowProcessController r0 = r6.app
            int r0 = r0.mUid
            int r0 = android.os.UserHandle.getUserId(r0)
            r10 = r0
        L_0x00e9:
            com.android.server.wm.ActivityStartController r0 = r17.getActivityStartController()     // Catch:{ SecurityException -> 0x0149 }
            java.lang.String r5 = "startActivityAsCaller"
            r11 = r20
            com.android.server.wm.ActivityStarter r0 = r0.obtainStarter(r11, r5)     // Catch:{ SecurityException -> 0x0147 }
            com.android.server.wm.ActivityStarter r0 = r0.setCallingUid(r7)     // Catch:{ SecurityException -> 0x0147 }
            com.android.server.wm.ActivityStarter r0 = r0.setCallingPackage(r8)     // Catch:{ SecurityException -> 0x0147 }
            r12 = r21
            com.android.server.wm.ActivityStarter r0 = r0.setResolvedType(r12)     // Catch:{ SecurityException -> 0x0145 }
            com.android.server.wm.ActivityStarter r0 = r0.setResultTo(r2)     // Catch:{ SecurityException -> 0x0145 }
            r13 = r23
            com.android.server.wm.ActivityStarter r0 = r0.setResultWho(r13)     // Catch:{ SecurityException -> 0x0143 }
            r14 = r24
            com.android.server.wm.ActivityStarter r0 = r0.setRequestCode(r14)     // Catch:{ SecurityException -> 0x0141 }
            r15 = r25
            com.android.server.wm.ActivityStarter r0 = r0.setStartFlags(r15)     // Catch:{ SecurityException -> 0x013f }
            r5 = r27
            com.android.server.wm.ActivityStarter r0 = r0.setActivityOptions((android.os.Bundle) r5)     // Catch:{ SecurityException -> 0x013d }
            com.android.server.wm.ActivityStarter r0 = r0.setMayWait(r10)     // Catch:{ SecurityException -> 0x013d }
            com.android.server.wm.ActivityStarter r0 = r0.setIgnoreTargetSecurity(r4)     // Catch:{ SecurityException -> 0x013d }
            if (r9 == 0) goto L_0x012e
            r16 = 0
            r1 = r16
            goto L_0x012f
        L_0x012e:
            r1 = r7
        L_0x012f:
            com.android.server.wm.ActivityStarter r0 = r0.setFilterCallingUid(r1)     // Catch:{ SecurityException -> 0x013d }
            r1 = 1
            com.android.server.wm.ActivityStarter r0 = r0.setAllowBackgroundActivityStart(r1)     // Catch:{ SecurityException -> 0x013d }
            int r0 = r0.execute()     // Catch:{ SecurityException -> 0x013d }
            return r0
        L_0x013d:
            r0 = move-exception
            goto L_0x0156
        L_0x013f:
            r0 = move-exception
            goto L_0x0154
        L_0x0141:
            r0 = move-exception
            goto L_0x0152
        L_0x0143:
            r0 = move-exception
            goto L_0x0150
        L_0x0145:
            r0 = move-exception
            goto L_0x014e
        L_0x0147:
            r0 = move-exception
            goto L_0x014c
        L_0x0149:
            r0 = move-exception
            r11 = r20
        L_0x014c:
            r12 = r21
        L_0x014e:
            r13 = r23
        L_0x0150:
            r14 = r24
        L_0x0152:
            r15 = r25
        L_0x0154:
            r5 = r27
        L_0x0156:
            throw r0
        L_0x0157:
            r11 = r20
            r12 = r21
            r13 = r23
            r14 = r24
            r15 = r25
            r10 = r30
            java.lang.SecurityException r1 = new java.lang.SecurityException     // Catch:{ all -> 0x01c8 }
            java.lang.String r7 = "Must be called from an activity that is declared in the android package"
            r1.<init>(r7)     // Catch:{ all -> 0x01c8 }
            throw r1     // Catch:{ all -> 0x01c8 }
        L_0x016b:
            r11 = r20
            r12 = r21
            r13 = r23
            r14 = r24
            r15 = r25
            r10 = r30
            java.lang.SecurityException r1 = new java.lang.SecurityException     // Catch:{ all -> 0x01c8 }
            java.lang.String r7 = "Called without a process attached to activity"
            r1.<init>(r7)     // Catch:{ all -> 0x01c8 }
            throw r1     // Catch:{ all -> 0x01c8 }
        L_0x017f:
            r11 = r20
            r12 = r21
            r13 = r23
            r14 = r24
            r15 = r25
            r10 = r30
            java.lang.SecurityException r1 = new java.lang.SecurityException     // Catch:{ all -> 0x01c8 }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x01c8 }
            r7.<init>()     // Catch:{ all -> 0x01c8 }
            java.lang.String r8 = "Called with bad activity token: "
            r7.append(r8)     // Catch:{ all -> 0x01c8 }
            r7.append(r0)     // Catch:{ all -> 0x01c8 }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x01c8 }
            r1.<init>(r7)     // Catch:{ all -> 0x01c8 }
            throw r1     // Catch:{ all -> 0x01c8 }
        L_0x01a2:
            r11 = r20
            r12 = r21
            r13 = r23
            r14 = r24
            r15 = r25
            r10 = r30
            java.lang.SecurityException r0 = new java.lang.SecurityException     // Catch:{ all -> 0x01c8 }
            java.lang.String r1 = "Must be called from an activity"
            r0.<init>(r1)     // Catch:{ all -> 0x01c8 }
            throw r0     // Catch:{ all -> 0x01c8 }
        L_0x01b6:
            r0 = move-exception
            r11 = r20
            r12 = r21
            r13 = r23
            r14 = r24
            r15 = r25
            r10 = r30
        L_0x01c3:
            monitor-exit(r5)     // Catch:{ all -> 0x01c8 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            throw r0
        L_0x01c8:
            r0 = move-exception
            goto L_0x01c3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityTaskManagerService.startActivityAsCaller(android.app.IApplicationThread, java.lang.String, android.content.Intent, java.lang.String, android.os.IBinder, java.lang.String, int, int, android.app.ProfilerInfo, android.os.Bundle, android.os.IBinder, boolean, int):int");
    }

    /* access modifiers changed from: package-private */
    public int handleIncomingUser(int callingPid, int callingUid, int userId, String name) {
        return this.mAmInternal.handleIncomingUser(callingPid, callingUid, userId, false, 2, name, (String) null);
    }

    public int startVoiceActivity(String callingPackage, int callingPid, int callingUid, Intent intent, String resolvedType, IVoiceInteractionSession session, IVoiceInteractor interactor, int startFlags, ProfilerInfo profilerInfo, Bundle bOptions, int userId) {
        this.mAmInternal.enforceCallingPermission("android.permission.BIND_VOICE_INTERACTION", "startVoiceActivity()");
        if (session == null || interactor == null) {
            throw new NullPointerException("null session or interactor");
        }
        return getActivityStartController().obtainStarter(intent, "startVoiceActivity").setCallingUid(callingUid).setCallingPackage(callingPackage).setResolvedType(resolvedType).setVoiceSession(session).setVoiceInteractor(interactor).setStartFlags(startFlags).setProfilerInfo(profilerInfo).setActivityOptions(bOptions).setMayWait(handleIncomingUser(callingPid, callingUid, userId, "startVoiceActivity")).setAllowBackgroundActivityStart(true).execute();
    }

    public int startAssistantActivity(String callingPackage, int callingPid, int callingUid, Intent intent, String resolvedType, Bundle bOptions, int userId) {
        this.mAmInternal.enforceCallingPermission("android.permission.BIND_VOICE_INTERACTION", "startAssistantActivity()");
        return getActivityStartController().obtainStarter(intent, "startAssistantActivity").setCallingUid(callingUid).setCallingPackage(callingPackage).setResolvedType(resolvedType).setActivityOptions(bOptions).setMayWait(handleIncomingUser(callingPid, callingUid, userId, "startAssistantActivity")).setAllowBackgroundActivityStart(true).execute();
    }

    /* Debug info: failed to restart local var, previous not found, register: 18 */
    public void startRecentsActivity(Intent intent, IAssistDataReceiver assistDataReceiver, IRecentsAnimationRunner recentsAnimationRunner) {
        enforceCallerIsRecentsOrHasPermission("android.permission.MANAGE_ACTIVITY_STACKS", "startRecentsActivity()");
        int callingPid = Binder.getCallingPid();
        long origId = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                new RecentsAnimation(this, this.mStackSupervisor, getActivityStartController(), this.mWindowManager, callingPid).startRecentsActivity(intent, recentsAnimationRunner, this.mRecentTasks.getRecentsComponent(), this.mRecentTasks.getRecentsComponentUid(), assistDataReceiver);
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            Binder.restoreCallingIdentity(origId);
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(origId);
            throw th;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    public final int startActivityFromRecents(int taskId, Bundle bOptions) {
        int startActivityFromRecents;
        enforceCallerIsRecentsOrHasPermission("android.permission.START_TASKS_FROM_RECENTS", "startActivityFromRecents()");
        int callingPid = Binder.getCallingPid();
        int callingUid = Binder.getCallingUid();
        SafeActivityOptions safeOptions = SafeActivityOptions.fromBundle(bOptions);
        long origId = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                startActivityFromRecents = this.mStackSupervisor.startActivityFromRecents(callingPid, callingUid, taskId, safeOptions);
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            Binder.restoreCallingIdentity(origId);
            return startActivityFromRecents;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(origId);
            throw th;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 11 */
    public final boolean isActivityStartAllowedOnDisplay(int displayId, Intent intent, String resolvedType, int userId) {
        boolean canPlaceEntityOnDisplay;
        int callingUid = Binder.getCallingUid();
        int callingPid = Binder.getCallingPid();
        long origId = Binder.clearCallingIdentity();
        try {
            ActivityInfo aInfo = this.mAmInternal.getActivityInfoForUser(this.mStackSupervisor.resolveActivity(intent, resolvedType, 0, (ProfilerInfo) null, userId, ActivityStarter.computeResolveFilterUid(callingUid, callingUid, ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION)), userId);
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                canPlaceEntityOnDisplay = this.mStackSupervisor.canPlaceEntityOnDisplay(displayId, callingPid, callingUid, aInfo);
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            Binder.restoreCallingIdentity(origId);
            return canPlaceEntityOnDisplay;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(origId);
            throw th;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 16 */
    public final boolean finishActivity(IBinder token, int resultCode, Intent resultData, int finishTask) {
        long origId;
        boolean res;
        ActivityRecord next;
        int i = finishTask;
        IBinder token2 = ActivityTaskManagerServiceInjector.finishActivity(this, token, resultCode, resultData);
        if (resultData == null || !resultData.hasFileDescriptors()) {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord r = ActivityRecord.isInStackLocked(token2);
                boolean z = true;
                if (r == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return true;
                }
                TaskRecord tr = r.getTaskRecord();
                ActivityRecord rootR = tr.getRootActivity();
                if (rootR == null) {
                    Slog.w("ActivityTaskManager", "Finishing task with all activities already finished");
                }
                if (getLockTaskController().activityBlockedFromFinish(r)) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return false;
                }
                if (!(this.mController == null || (next = r.getActivityStack().topRunningActivityLocked(token2, 0)) == null)) {
                    boolean resumeOK = true;
                    try {
                        resumeOK = this.mController.activityResuming(next.packageName);
                    } catch (RemoteException e) {
                        this.mController = null;
                        Watchdog.getInstance().setActivityController((IActivityController) null);
                    } catch (Throwable th) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                    if (!resumeOK) {
                        Slog.i("ActivityTaskManager", "Not finishing activity because controller resumed");
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return false;
                    }
                }
                if (r.app != null) {
                    r.app.setLastActivityFinishTimeIfNeeded(SystemClock.uptimeMillis());
                }
                origId = Binder.clearCallingIdentity();
                if (i != 1) {
                    z = false;
                }
                boolean finishWithRootActivity = z;
                if (i == 2 || (finishWithRootActivity && r == rootR)) {
                    res = this.mStackSupervisor.removeTaskByIdLocked(tr.taskId, false, finishWithRootActivity, "finish-activity");
                    if (!res) {
                        Slog.i("ActivityTaskManager", "Removing task failed to finish activity");
                    }
                    r.mRelaunchReason = 0;
                } else {
                    res = tr.getStack().requestFinishActivityLocked(token2, resultCode, resultData, "app-request", true);
                    if (!res) {
                        Slog.i("ActivityTaskManager", "Failed to finish by app-request");
                    }
                }
                Binder.restoreCallingIdentity(origId);
                WindowManagerService.resetPriorityAfterLockedSection();
                return res;
            }
        }
        throw new IllegalArgumentException("File descriptors passed in Intent");
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    public boolean finishActivityAffinity(IBinder token) {
        long origId;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                origId = Binder.clearCallingIdentity();
                ActivityRecord r = ActivityRecord.isInStackLocked(token);
                if (r == null) {
                    Binder.restoreCallingIdentity(origId);
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return false;
                }
                TaskRecord task = r.getTaskRecord();
                if (getLockTaskController().activityBlockedFromFinish(r)) {
                    Binder.restoreCallingIdentity(origId);
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return false;
                }
                boolean finishActivityAffinityLocked = task.getStack().finishActivityAffinityLocked(r);
                Binder.restoreCallingIdentity(origId);
                WindowManagerService.resetPriorityAfterLockedSection();
                return finishActivityAffinityLocked;
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0037, code lost:
        android.os.Binder.restoreCallingIdentity(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x003b, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void activityIdle(android.os.IBinder r8, android.content.res.Configuration r9, boolean r10) {
        /*
            r7 = this;
            long r0 = android.os.Binder.clearCallingIdentity()
            r2 = 0
            com.android.server.wm.WindowManagerGlobalLock r3 = r7.mGlobalLock     // Catch:{ all -> 0x0042 }
            monitor-enter(r3)     // Catch:{ all -> 0x0042 }
            com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x003c }
            com.android.server.wm.ActivityStack r4 = com.android.server.wm.ActivityRecord.getStackLocked(r8)     // Catch:{ all -> 0x003c }
            if (r4 != 0) goto L_0x0019
            monitor-exit(r3)     // Catch:{ all -> 0x003c }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            android.os.Binder.restoreCallingIdentity(r0)
            return
        L_0x0019:
            com.android.server.wm.ActivityStackSupervisor r5 = r7.mStackSupervisor     // Catch:{ all -> 0x003c }
            r6 = 0
            com.android.server.wm.ActivityRecord r5 = r5.activityIdleInternalLocked(r8, r6, r6, r9)     // Catch:{ all -> 0x003c }
            if (r5 == 0) goto L_0x002c
            com.android.server.wm.WindowProcessController r6 = r5.app     // Catch:{ all -> 0x003c }
            r2 = r6
            com.android.server.wm.ActivityTaskManagerServiceInjector$MiuiActivityController r6 = com.android.server.wm.ActivityTaskManagerServiceInjector.getMiuiActivityController()     // Catch:{ all -> 0x003c }
            r6.activityIdle(r5)     // Catch:{ all -> 0x003c }
        L_0x002c:
            if (r10 == 0) goto L_0x0033
            if (r2 == 0) goto L_0x0033
            r2.clearProfilerIfNeeded()     // Catch:{ all -> 0x003c }
        L_0x0033:
            monitor-exit(r3)     // Catch:{ all -> 0x003c }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()     // Catch:{ all -> 0x0042 }
            android.os.Binder.restoreCallingIdentity(r0)
            return
        L_0x003c:
            r4 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x003c }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()     // Catch:{ all -> 0x0042 }
            throw r4     // Catch:{ all -> 0x0042 }
        L_0x0042:
            r2 = move-exception
            android.os.Binder.restoreCallingIdentity(r0)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityTaskManagerService.activityIdle(android.os.IBinder, android.content.res.Configuration, boolean):void");
    }

    public final void activityResumed(IBinder token) {
        long origId = Binder.clearCallingIdentity();
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord.activityResumedLocked(token);
                this.mWindowManager.notifyAppResumedFinished(token);
                ActivityTaskManagerServiceInjector.getMiuiActivityController().activityResumed(ActivityRecord.forTokenLocked(token));
                setSchedFgPid(ActivityRecord.forTokenLocked(token));
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        Binder.restoreCallingIdentity(origId);
    }

    public final void activityTopResumedStateLost() {
        long origId = Binder.clearCallingIdentity();
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mStackSupervisor.handleTopResumedStateReleased(false);
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        Binder.restoreCallingIdentity(origId);
    }

    public final void activityPaused(IBinder token) {
        long origId = Binder.clearCallingIdentity();
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityStack stack = ActivityRecord.getStackLocked(token);
                if (stack != null) {
                    stack.activityPausedLocked(token, false);
                    ActivityTaskManagerServiceInjector.getMiuiActivityController().activityPaused(ActivityRecord.forTokenLocked(token));
                }
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        Binder.restoreCallingIdentity(origId);
    }

    public final void activityStopped(IBinder token, Bundle icicle, PersistableBundle persistentState, CharSequence description) {
        ActivityRecord r;
        if (icicle == null || !icicle.hasFileDescriptors()) {
            long origId = Binder.clearCallingIdentity();
            String restartingName = null;
            int restartingUid = 0;
            synchronized (this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    r = ActivityRecord.isInStackLocked(token);
                    if (r != null) {
                        if (r.attachedToProcess() && r.isState(ActivityStack.ActivityState.RESTARTING_PROCESS)) {
                            restartingName = r.app.mName;
                            restartingUid = r.app.mUid;
                        }
                        r.activityStoppedLocked(icicle, persistentState, description);
                        ActivityTaskManagerServiceInjector.getMiuiActivityController().activityStopped(r);
                    }
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            if (restartingName != null) {
                this.mStackSupervisor.removeRestartTimeouts(r);
                this.mAmInternal.killProcess(restartingName, restartingUid, "restartActivityProcess");
            }
            this.mAmInternal.trimApplications();
            Binder.restoreCallingIdentity(origId);
            return;
        }
        throw new IllegalArgumentException("File descriptors passed in Bundle");
    }

    public final void activityDestroyed(IBinder token) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityStack stack = ActivityRecord.getStackLocked(token);
                if (stack != null) {
                    stack.activityDestroyedLocked(token, "activityDestroyed");
                    ActivityTaskManagerServiceInjector.getMiuiActivityController().activityDestroyed(ActivityRecord.forTokenLocked(token));
                }
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public final void activityRelaunched(IBinder token) {
        long origId = Binder.clearCallingIdentity();
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mStackSupervisor.activityRelaunchedLocked(token);
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        Binder.restoreCallingIdentity(origId);
    }

    public final void activitySlept(IBinder token) {
        long origId = Binder.clearCallingIdentity();
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord r = ActivityRecord.isInStackLocked(token);
                if (r != null) {
                    this.mStackSupervisor.activitySleptLocked(r);
                }
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        Binder.restoreCallingIdentity(origId);
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    public void setRequestedOrientation(IBinder token, int requestedOrientation) {
        long origId;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord r = ActivityRecord.isInStackLocked(token);
                if (r == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                origId = Binder.clearCallingIdentity();
                r.setRequestedOrientation(requestedOrientation);
                Binder.restoreCallingIdentity(origId);
                WindowManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public int getRequestedOrientation(IBinder token) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord r = ActivityRecord.isInStackLocked(token);
                if (r == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return -1;
                }
                int orientation = r.getOrientation();
                WindowManagerService.resetPriorityAfterLockedSection();
                return orientation;
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 3 */
    public void setImmersive(IBinder token, boolean immersive) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord r = ActivityRecord.isInStackLocked(token);
                if (r != null) {
                    r.immersive = immersive;
                    if (r.isResumedActivityOnDisplay()) {
                        applyUpdateLockStateLocked(r);
                    }
                } else {
                    throw new IllegalArgumentException();
                }
            } finally {
                WindowManagerService.resetPriorityAfterLockedSection();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void applyUpdateLockStateLocked(ActivityRecord r) {
        this.mH.post(new Runnable(r != null && r.immersive, r) {
            private final /* synthetic */ boolean f$1;
            private final /* synthetic */ ActivityRecord f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                ActivityTaskManagerService.this.lambda$applyUpdateLockStateLocked$0$ActivityTaskManagerService(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$applyUpdateLockStateLocked$0$ActivityTaskManagerService(boolean nextState, ActivityRecord r) {
        if (this.mUpdateLock.isHeld() == nextState) {
            return;
        }
        if (nextState) {
            this.mUpdateLock.acquire();
        } else {
            this.mUpdateLock.release();
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 3 */
    public boolean isImmersive(IBinder token) {
        boolean z;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord r = ActivityRecord.isInStackLocked(token);
                if (r != null) {
                    z = r.immersive;
                } else {
                    throw new IllegalArgumentException();
                }
            } finally {
                WindowManagerService.resetPriorityAfterLockedSection();
            }
        }
        return z;
    }

    public boolean isTopActivityImmersive() {
        boolean z;
        enforceNotIsolatedCaller("isTopActivityImmersive");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord r = getTopDisplayFocusedStack().topRunningActivityLocked();
                z = r != null ? r.immersive : false;
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return z;
    }

    public void overridePendingTransition(IBinder token, String packageName, int enterAnim, int exitAnim) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord self = ActivityRecord.isInStackLocked(token);
                if (self == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                long origId = Binder.clearCallingIdentity();
                if (self.isState(ActivityStack.ActivityState.RESUMED, ActivityStack.ActivityState.PAUSING)) {
                    self.getDisplay().mDisplayContent.mAppTransition.overridePendingAppTransition(packageName, enterAnim, exitAnim, (IRemoteCallback) null);
                }
                Binder.restoreCallingIdentity(origId);
                WindowManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    public int getFrontActivityScreenCompatMode() {
        enforceNotIsolatedCaller("getFrontActivityScreenCompatMode");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord r = getTopDisplayFocusedStack().topRunningActivityLocked();
                if (r == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return -3;
                }
                int computeCompatModeLocked = this.mCompatModePackages.computeCompatModeLocked(r.info.applicationInfo);
                WindowManagerService.resetPriorityAfterLockedSection();
                return computeCompatModeLocked;
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    public void setFrontActivityScreenCompatMode(int mode) {
        this.mAmInternal.enforceCallingPermission("android.permission.SET_SCREEN_COMPATIBILITY", "setFrontActivityScreenCompatMode");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord r = getTopDisplayFocusedStack().topRunningActivityLocked();
                if (r == null) {
                    Slog.w("ActivityTaskManager", "setFrontActivityScreenCompatMode failed: no top activity");
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                this.mCompatModePackages.setPackageScreenCompatModeLocked(r.info.applicationInfo, mode);
                WindowManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    public int getLaunchedFromUid(IBinder activityToken) {
        ActivityRecord srec;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                srec = ActivityRecord.forTokenLocked(activityToken);
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        if (srec == null) {
            return -1;
        }
        return srec.launchedFromUid;
    }

    public String getLaunchedFromPackage(IBinder activityToken) {
        ActivityRecord srec;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                srec = ActivityRecord.forTokenLocked(activityToken);
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        if (srec == null) {
            return null;
        }
        return srec.launchedFromPackage;
    }

    /* Debug info: failed to restart local var, previous not found, register: 9 */
    public boolean convertFromTranslucent(IBinder token) {
        long origId = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord r = ActivityRecord.isInStackLocked(token);
                if (r == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    Binder.restoreCallingIdentity(origId);
                    return false;
                }
                boolean translucentChanged = r.changeWindowTranslucency(true);
                if (translucentChanged) {
                    this.mRootActivityContainer.ensureActivitiesVisible((ActivityRecord) null, 0, false);
                }
                this.mWindowManager.setAppFullscreen(token, true);
                WindowManagerService.resetPriorityAfterLockedSection();
                Binder.restoreCallingIdentity(origId);
                return translucentChanged;
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(origId);
            throw th;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 11 */
    public boolean convertToTranslucent(IBinder token, Bundle options) {
        SafeActivityOptions safeOptions = SafeActivityOptions.fromBundle(options);
        long origId = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord r = ActivityRecord.isInStackLocked(token);
                if (r == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    Binder.restoreCallingIdentity(origId);
                    return false;
                }
                TaskRecord task = r.getTaskRecord();
                int index = task.mActivities.lastIndexOf(r);
                if (index > 0) {
                    task.mActivities.get(index - 1).returningOptions = safeOptions != null ? safeOptions.getOptions(r) : null;
                }
                boolean translucentChanged = r.changeWindowTranslucency(false);
                if (translucentChanged) {
                    r.getActivityStack().convertActivityToTranslucent(r);
                }
                this.mRootActivityContainer.ensureActivitiesVisible((ActivityRecord) null, 0, false);
                this.mWindowManager.setAppFullscreen(token, false);
                WindowManagerService.resetPriorityAfterLockedSection();
                Binder.restoreCallingIdentity(origId);
                return translucentChanged;
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(origId);
            throw th;
        }
    }

    public void notifyActivityDrawn(IBinder token) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord r = this.mRootActivityContainer.isInAnyStack(token);
                if (r != null) {
                    r.getActivityStack().notifyActivityDrawnLocked(r);
                }
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void reportActivityFullyDrawn(IBinder token, boolean restoredFromBundle) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord r = ActivityRecord.isInStackLocked(token);
                if (r == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                r.reportFullyDrawnLocked(restoredFromBundle);
                WindowManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    public int getActivityDisplayId(IBinder activityToken) throws RemoteException {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityStack stack = ActivityRecord.getStackLocked(activityToken);
                if (stack == null || stack.mDisplayId == -1) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return 0;
                }
                int i = stack.mDisplayId;
                WindowManagerService.resetPriorityAfterLockedSection();
                return i;
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    public ActivityManager.StackInfo getFocusedStackInfo() throws RemoteException {
        enforceCallerIsRecentsOrHasPermission("android.permission.MANAGE_ACTIVITY_STACKS", "getStackInfo()");
        long ident = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityStack focusedStack = getTopDisplayFocusedStack();
                if (focusedStack != null) {
                    ActivityManager.StackInfo stackInfo = this.mRootActivityContainer.getStackInfo(focusedStack.mStackId);
                    WindowManagerService.resetPriorityAfterLockedSection();
                    Binder.restoreCallingIdentity(ident);
                    return stackInfo;
                }
                WindowManagerService.resetPriorityAfterLockedSection();
                Binder.restoreCallingIdentity(ident);
                return null;
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(ident);
            throw th;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0050, code lost:
        android.os.Binder.restoreCallingIdentity(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0054, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setFocusedStack(int r8) {
        /*
            r7 = this;
            android.app.ActivityManagerInternal r0 = r7.mAmInternal
            java.lang.String r1 = "android.permission.MANAGE_ACTIVITY_STACKS"
            java.lang.String r2 = "setFocusedStack()"
            r0.enforceCallingPermission(r1, r2)
            long r0 = android.os.Binder.clearCallingIdentity()
            com.android.server.wm.WindowManagerGlobalLock r2 = r7.mGlobalLock     // Catch:{ all -> 0x005b }
            monitor-enter(r2)     // Catch:{ all -> 0x005b }
            com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x0055 }
            com.android.server.wm.RootActivityContainer r3 = r7.mRootActivityContainer     // Catch:{ all -> 0x0055 }
            com.android.server.wm.ActivityStack r3 = r3.getStack(r8)     // Catch:{ all -> 0x0055 }
            if (r3 != 0) goto L_0x0039
            java.lang.String r4 = "ActivityTaskManager"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0055 }
            r5.<init>()     // Catch:{ all -> 0x0055 }
            java.lang.String r6 = "setFocusedStack: No stack with id="
            r5.append(r6)     // Catch:{ all -> 0x0055 }
            r5.append(r8)     // Catch:{ all -> 0x0055 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0055 }
            android.util.Slog.w(r4, r5)     // Catch:{ all -> 0x0055 }
            monitor-exit(r2)     // Catch:{ all -> 0x0055 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            android.os.Binder.restoreCallingIdentity(r0)
            return
        L_0x0039:
            com.android.server.wm.ActivityRecord r4 = r3.topRunningActivityLocked()     // Catch:{ all -> 0x0055 }
            if (r4 == 0) goto L_0x004c
            java.lang.String r5 = "setFocusedStack"
            boolean r5 = r4.moveFocusableActivityToTop(r5)     // Catch:{ all -> 0x0055 }
            if (r5 == 0) goto L_0x004c
            com.android.server.wm.RootActivityContainer r5 = r7.mRootActivityContainer     // Catch:{ all -> 0x0055 }
            r5.resumeFocusedStacksTopActivities()     // Catch:{ all -> 0x0055 }
        L_0x004c:
            monitor-exit(r2)     // Catch:{ all -> 0x0055 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()     // Catch:{ all -> 0x005b }
            android.os.Binder.restoreCallingIdentity(r0)
            return
        L_0x0055:
            r3 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0055 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()     // Catch:{ all -> 0x005b }
            throw r3     // Catch:{ all -> 0x005b }
        L_0x005b:
            r2 = move-exception
            android.os.Binder.restoreCallingIdentity(r0)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityTaskManagerService.setFocusedStack(int):void");
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    /* JADX WARNING: Code restructure failed: missing block: B:18:?, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x003b, code lost:
        android.os.Binder.restoreCallingIdentity(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x003f, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setFocusedTask(int r7) {
        /*
            r6 = this;
            android.app.ActivityManagerInternal r0 = r6.mAmInternal
            java.lang.String r1 = "android.permission.MANAGE_ACTIVITY_STACKS"
            java.lang.String r2 = "setFocusedTask()"
            r0.enforceCallingPermission(r1, r2)
            long r0 = android.os.Binder.clearCallingIdentity()
            com.android.server.wm.WindowManagerGlobalLock r2 = r6.mGlobalLock     // Catch:{ all -> 0x0046 }
            monitor-enter(r2)     // Catch:{ all -> 0x0046 }
            com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x0040 }
            com.android.server.wm.RootActivityContainer r3 = r6.mRootActivityContainer     // Catch:{ all -> 0x0040 }
            r4 = 0
            com.android.server.wm.TaskRecord r3 = r3.anyTaskForId(r7, r4)     // Catch:{ all -> 0x0040 }
            if (r3 != 0) goto L_0x0024
            monitor-exit(r2)     // Catch:{ all -> 0x0040 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            android.os.Binder.restoreCallingIdentity(r0)
            return
        L_0x0024:
            com.android.server.wm.ActivityRecord r4 = r3.topRunningActivityLocked()     // Catch:{ all -> 0x0040 }
            if (r4 == 0) goto L_0x0037
            java.lang.String r5 = "setFocusedTask"
            boolean r5 = r4.moveFocusableActivityToTop(r5)     // Catch:{ all -> 0x0040 }
            if (r5 == 0) goto L_0x0037
            com.android.server.wm.RootActivityContainer r5 = r6.mRootActivityContainer     // Catch:{ all -> 0x0040 }
            r5.resumeFocusedStacksTopActivities()     // Catch:{ all -> 0x0040 }
        L_0x0037:
            monitor-exit(r2)     // Catch:{ all -> 0x0040 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()     // Catch:{ all -> 0x0046 }
            android.os.Binder.restoreCallingIdentity(r0)
            return
        L_0x0040:
            r3 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0040 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()     // Catch:{ all -> 0x0046 }
            throw r3     // Catch:{ all -> 0x0046 }
        L_0x0046:
            r2 = move-exception
            android.os.Binder.restoreCallingIdentity(r0)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityTaskManagerService.setFocusedTask(int):void");
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    public void restartActivityProcessIfVisible(IBinder activityToken) {
        this.mAmInternal.enforceCallingPermission("android.permission.MANAGE_ACTIVITY_STACKS", "restartActivityProcess()");
        long callingId = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord r = ActivityRecord.isInStackLocked(activityToken);
                if (r == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    Binder.restoreCallingIdentity(callingId);
                    return;
                }
                r.restartProcessIfVisible();
                WindowManagerService.resetPriorityAfterLockedSection();
                Binder.restoreCallingIdentity(callingId);
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(callingId);
            throw th;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    public boolean removeTask(int taskId) {
        long ident;
        boolean removeTaskByIdLocked;
        enforceCallerIsRecentsOrHasPermission("android.permission.REMOVE_TASKS", "removeTask()");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ident = Binder.clearCallingIdentity();
                removeTaskByIdLocked = this.mStackSupervisor.removeTaskByIdLocked(taskId, true, true, "remove-task");
                Binder.restoreCallingIdentity(ident);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return removeTaskByIdLocked;
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    public void removeAllVisibleRecentTasks() {
        long ident;
        enforceCallerIsRecentsOrHasPermission("android.permission.REMOVE_TASKS", "removeAllVisibleRecentTasks()");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ident = Binder.clearCallingIdentity();
                getRecentTasks().removeAllVisibleTasks(this.mAmInternal.getCurrentUserId());
                Binder.restoreCallingIdentity(ident);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public boolean shouldUpRecreateTask(IBinder token, String destAffinity) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord srec = ActivityRecord.forTokenLocked(token);
                if (srec != null) {
                    boolean shouldUpRecreateTaskLocked = srec.getActivityStack().shouldUpRecreateTaskLocked(srec, destAffinity);
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return shouldUpRecreateTaskLocked;
                }
                WindowManagerService.resetPriorityAfterLockedSection();
                return false;
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    public boolean navigateUpTo(IBinder token, Intent destIntent, int resultCode, Intent resultData) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord r = ActivityRecord.forTokenLocked(token);
                if (r != null) {
                    boolean navigateUpToLocked = r.getActivityStack().navigateUpToLocked(r, destIntent, resultCode, resultData);
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return navigateUpToLocked;
                }
                WindowManagerService.resetPriorityAfterLockedSection();
                return false;
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 9 */
    public boolean moveActivityTaskToBack(IBinder token, boolean nonRoot) {
        long origId;
        enforceNotIsolatedCaller("moveActivityTaskToBack");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                origId = Binder.clearCallingIdentity();
                int taskId = ActivityRecord.getTaskForActivityLocked(token, !nonRoot);
                TaskRecord task = this.mRootActivityContainer.anyTaskForId(taskId);
                if (task != null && task.inFreeformWindowingMode()) {
                    this.mStackSupervisor.removeTaskByIdLocked(task.taskId, false, false, "moveActivityTaskToBack");
                    Binder.restoreCallingIdentity(origId);
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return false;
                } else if (task != null) {
                    boolean moveTaskToBackLocked = ActivityRecord.getStackLocked(token).moveTaskToBackLocked(taskId);
                    Binder.restoreCallingIdentity(origId);
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return moveTaskToBackLocked;
                } else {
                    Binder.restoreCallingIdentity(origId);
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return false;
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    /* JADX WARNING: Code restructure failed: missing block: B:23:?, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0069, code lost:
        android.os.Binder.restoreCallingIdentity(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x006d, code lost:
        return r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.graphics.Rect getTaskBounds(int r9) {
        /*
            r8 = this;
            android.app.ActivityManagerInternal r0 = r8.mAmInternal
            java.lang.String r1 = "android.permission.MANAGE_ACTIVITY_STACKS"
            java.lang.String r2 = "getTaskBounds()"
            r0.enforceCallingPermission(r1, r2)
            long r0 = android.os.Binder.clearCallingIdentity()
            android.graphics.Rect r2 = new android.graphics.Rect
            r2.<init>()
            com.android.server.wm.WindowManagerGlobalLock r3 = r8.mGlobalLock     // Catch:{ all -> 0x0074 }
            monitor-enter(r3)     // Catch:{ all -> 0x0074 }
            com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x006e }
            com.android.server.wm.RootActivityContainer r4 = r8.mRootActivityContainer     // Catch:{ all -> 0x006e }
            r5 = 1
            com.android.server.wm.TaskRecord r4 = r4.anyTaskForId(r9, r5)     // Catch:{ all -> 0x006e }
            if (r4 != 0) goto L_0x0044
            java.lang.String r5 = "ActivityTaskManager"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x006e }
            r6.<init>()     // Catch:{ all -> 0x006e }
            java.lang.String r7 = "getTaskBounds: taskId="
            r6.append(r7)     // Catch:{ all -> 0x006e }
            r6.append(r9)     // Catch:{ all -> 0x006e }
            java.lang.String r7 = " not found"
            r6.append(r7)     // Catch:{ all -> 0x006e }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x006e }
            android.util.Slog.w(r5, r6)     // Catch:{ all -> 0x006e }
            monitor-exit(r3)     // Catch:{ all -> 0x006e }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            android.os.Binder.restoreCallingIdentity(r0)
            return r2
        L_0x0044:
            com.android.server.wm.ActivityStack r5 = r4.getStack()     // Catch:{ all -> 0x006e }
            if (r5 == 0) goto L_0x004e
            r4.getWindowContainerBounds(r2)     // Catch:{ all -> 0x006e }
            goto L_0x0065
        L_0x004e:
            boolean r5 = r4.matchParentBounds()     // Catch:{ all -> 0x006e }
            if (r5 != 0) goto L_0x005c
            android.graphics.Rect r5 = r4.getBounds()     // Catch:{ all -> 0x006e }
            r2.set(r5)     // Catch:{ all -> 0x006e }
            goto L_0x0065
        L_0x005c:
            android.graphics.Rect r5 = r4.mLastNonFullscreenBounds     // Catch:{ all -> 0x006e }
            if (r5 == 0) goto L_0x0065
            android.graphics.Rect r5 = r4.mLastNonFullscreenBounds     // Catch:{ all -> 0x006e }
            r2.set(r5)     // Catch:{ all -> 0x006e }
        L_0x0065:
            monitor-exit(r3)     // Catch:{ all -> 0x006e }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()     // Catch:{ all -> 0x0074 }
            android.os.Binder.restoreCallingIdentity(r0)
            return r2
        L_0x006e:
            r4 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x006e }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()     // Catch:{ all -> 0x0074 }
            throw r4     // Catch:{ all -> 0x0074 }
        L_0x0074:
            r3 = move-exception
            android.os.Binder.restoreCallingIdentity(r0)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityTaskManagerService.getTaskBounds(int):android.graphics.Rect");
    }

    public ActivityManager.TaskDescription getTaskDescription(int id) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                enforceCallerIsRecentsOrHasPermission("android.permission.MANAGE_ACTIVITY_STACKS", "getTaskDescription()");
                TaskRecord tr = this.mRootActivityContainer.anyTaskForId(id, 1);
                if (tr != null) {
                    ActivityManager.TaskDescription taskDescription = tr.lastTaskDescription;
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return taskDescription;
                }
                WindowManagerService.resetPriorityAfterLockedSection();
                return null;
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    public void launchSmallFreeFormWindow(int taskId, int windowingMode, boolean toTop, boolean isMiniFreeformMode, Rect rect) {
        if (this.mWindowManager.mMiuiFreeFormGestureController != null) {
            this.mWindowManager.mMiuiFreeFormGestureController.mGestureListener.mFreeFormWindowMotionHelper.setOriginalBounds(rect);
            if (rect.left != 0 && windowingMode == 5) {
                this.mWindowManager.mMiuiFreeFormGestureController.showScreenSurface();
            }
        }
        MiuiMultiWindowUtils.mIsMiniFreeformMode = isMiniFreeformMode;
        setTaskWindowingMode(taskId, windowingMode, toTop);
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    public void setTaskWindowingMode(int taskId, int windowingMode, boolean toTop) {
        long ident;
        if (windowingMode == 3) {
            setTaskWindowingModeSplitScreenPrimary(taskId, 0, toTop, true, (Rect) null, true);
            return;
        }
        enforceCallerIsRecentsOrHasPermission("android.permission.MANAGE_ACTIVITY_STACKS", "setTaskWindowingMode()");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ident = Binder.clearCallingIdentity();
                TaskRecord task = this.mRootActivityContainer.anyTaskForId(taskId, 0);
                if (task == null) {
                    Slog.w("ActivityTaskManager", "setTaskWindowingMode: No task for id=" + taskId);
                    Binder.restoreCallingIdentity(ident);
                    WindowManagerService.resetPriorityAfterLockedSection();
                } else if (task.isActivityTypeStandardOrUndefined()) {
                    ActivityStack stack = task.getStack();
                    if (stack.getWindowingMode() == 5) {
                    }
                    if (ActivityTaskManagerServiceInjector.needSetWindowMode(this, task, toTop, windowingMode)) {
                        Binder.restoreCallingIdentity(ident);
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return;
                    }
                    if (toTop) {
                        stack.moveToFront("setTaskWindowingMode", task);
                    }
                    stack.setWindowingMode(windowingMode);
                    if (windowingMode == 5 && MiuiMultiWindowUtils.mIsMiniFreeformMode) {
                        this.mWindowManager.launchSmallFreeFormWindow();
                    }
                    Binder.restoreCallingIdentity(ident);
                    WindowManagerService.resetPriorityAfterLockedSection();
                } else {
                    throw new IllegalArgumentException("setTaskWindowingMode: Attempt to move non-standard task " + taskId + " to windowing mode=" + windowingMode);
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public String getCallingPackage(IBinder token) {
        String hookGetCallingPkg;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord r = getCallingRecordLocked(token);
                hookGetCallingPkg = ActivityTaskManagerServiceInjector.hookGetCallingPkg(this, token, r != null ? r.info.packageName : null);
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return hookGetCallingPkg;
    }

    public ComponentName getCallingActivity(IBinder token) {
        ComponentName component;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord r = getCallingRecordLocked(token);
                component = r != null ? r.intent.getComponent() : null;
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return component;
    }

    private ActivityRecord getCallingRecordLocked(IBinder token) {
        ActivityRecord r = ActivityRecord.isInStackLocked(token);
        if (r == null) {
            return null;
        }
        return r.resultTo;
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    public void unhandledBack() {
        long origId;
        this.mAmInternal.enforceCallingPermission("android.permission.FORCE_BACK", "unhandledBack()");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                origId = Binder.clearCallingIdentity();
                getTopDisplayFocusedStack().unhandledBackLocked();
                Binder.restoreCallingIdentity(origId);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void onBackPressedOnTaskRoot(IBinder token, IRequestFinishCallback callback) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord r = ActivityRecord.isInStackLocked(token);
                if (r == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                ActivityStack stack = r.getActivityStack();
                if (stack == null || !stack.isSingleTaskInstance()) {
                    callback.requestFinish();
                } else {
                    this.mTaskChangeNotificationController.notifyBackPressedOnTaskRoot(r.getTaskRecord().getTaskInfo());
                }
            } catch (RemoteException e) {
                Slog.e("ActivityTaskManager", "Failed to invoke request finish callback", e);
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void moveTaskToFront(IApplicationThread appThread, String callingPackage, int taskId, int flags, Bundle bOptions) {
        this.mAmInternal.enforceCallingPermission("android.permission.REORDER_TASKS", "moveTaskToFront()");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                moveTaskToFrontLocked(appThread, callingPackage, taskId, flags, SafeActivityOptions.fromBundle(bOptions), false);
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    /* access modifiers changed from: package-private */
    public void moveTaskToFrontLocked(IApplicationThread appThread, String callingPackage, int taskId, int flags, SafeActivityOptions options, boolean fromRecents) {
        WindowProcessController callerApp;
        ActivityOptions realOptions;
        int i = taskId;
        SafeActivityOptions safeActivityOptions = options;
        int callingPid = Binder.getCallingPid();
        int callingUid = Binder.getCallingUid();
        if (!isSameApp(callingUid, callingPackage)) {
            String msg = "Permission Denial: moveTaskToFrontLocked() from pid=" + Binder.getCallingPid() + " as package " + callingPackage;
            Slog.w("ActivityTaskManager", msg);
            throw new SecurityException(msg);
        } else if (!checkAppSwitchAllowedLocked(callingPid, callingUid, -1, -1, "Task to front")) {
            SafeActivityOptions.abort(options);
        } else {
            long origId = Binder.clearCallingIdentity();
            if (appThread != null) {
                callerApp = getProcessController(appThread);
            } else {
                callerApp = null;
            }
            String str = "ActivityTaskManager";
            int i2 = callingUid;
            int i3 = i;
            if (!getActivityStartController().obtainStarter((Intent) null, "moveTaskToFront").shouldAbortBackgroundActivityStart(callingUid, callingPid, callingPackage, -1, -1, callerApp, (PendingIntentRecord) null, false, (Intent) null) || isBackgroundActivityStartsEnabled()) {
                try {
                    TaskRecord task = this.mRootActivityContainer.anyTaskForId(i3);
                    if (task == null) {
                        Slog.d(str, "Could not find task for id: " + i3);
                        SafeActivityOptions.abort(options);
                        Binder.restoreCallingIdentity(origId);
                    } else if (getLockTaskController().isLockTaskModeViolation(task)) {
                        Slog.e(str, "moveTaskToFront: Attempt to violate Lock Task Mode");
                        SafeActivityOptions.abort(options);
                        Binder.restoreCallingIdentity(origId);
                    } else {
                        this.mGestureController.mLaunchRecentsFromGesture = false;
                        SafeActivityOptions safeActivityOptions2 = options;
                        if (safeActivityOptions2 != null) {
                            try {
                                realOptions = safeActivityOptions2.getOptions(this.mStackSupervisor);
                            } catch (Throwable th) {
                                th = th;
                                boolean z = fromRecents;
                                Binder.restoreCallingIdentity(origId);
                                throw th;
                            }
                        } else {
                            realOptions = null;
                        }
                        this.mStackSupervisor.findTaskToMoveToFront(task, flags, realOptions, "moveTaskToFront", false);
                        ActivityRecord topActivity = task.getTopActivity();
                        if (topActivity != null) {
                            try {
                                topActivity.showStartingWindow((ActivityRecord) null, false, true, fromRecents);
                            } catch (Throwable th2) {
                                th = th2;
                            }
                        } else {
                            boolean z2 = fromRecents;
                        }
                        Binder.restoreCallingIdentity(origId);
                    }
                } catch (Throwable th3) {
                    th = th3;
                    SafeActivityOptions safeActivityOptions3 = options;
                    boolean z3 = fromRecents;
                    Binder.restoreCallingIdentity(origId);
                    throw th;
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isSameApp(int callingUid, String packageName) {
        if (callingUid == 0 || callingUid == 1000) {
            return true;
        }
        if (packageName == null) {
            return false;
        }
        try {
            return UserHandle.isSameApp(callingUid, AppGlobals.getPackageManager().getPackageUid(packageName, 268435456, UserHandle.getUserId(callingUid)));
        } catch (RemoteException e) {
            return true;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean checkAppSwitchAllowedLocked(int sourcePid, int sourceUid, int callingPid, int callingUid, String name) {
        if (this.mAppSwitchesAllowedTime < SystemClock.uptimeMillis() || getRecentTasks().isCallerRecents(sourceUid) || checkComponentPermission("android.permission.STOP_APP_SWITCHES", sourcePid, sourceUid, -1, true) == 0 || checkAllowAppSwitchUid(sourceUid)) {
            return true;
        }
        if (callingUid != -1 && callingUid != sourceUid && (checkComponentPermission("android.permission.STOP_APP_SWITCHES", callingPid, callingUid, -1, true) == 0 || checkAllowAppSwitchUid(callingUid))) {
            return true;
        }
        Slog.w("ActivityTaskManager", name + " request from " + sourceUid + " stopped");
        return false;
    }

    private boolean checkAllowAppSwitchUid(int uid) {
        ArrayMap<String, Integer> types = this.mAllowAppSwitchUids.get(UserHandle.getUserId(uid));
        if (types == null) {
            return false;
        }
        for (int i = types.size() - 1; i >= 0; i--) {
            if (types.valueAt(i).intValue() == uid) {
                return true;
            }
        }
        return false;
    }

    public void setActivityController(IActivityController controller, boolean imAMonkey) {
        this.mAmInternal.enforceCallingPermission("android.permission.SET_ACTIVITY_WATCHER", "setActivityController()");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mController = controller;
                this.mControllerIsAMonkey = imAMonkey;
                Watchdog.getInstance().setActivityController(controller);
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public boolean isControllerAMonkey() {
        boolean z;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                z = this.mController != null && this.mControllerIsAMonkey;
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return z;
    }

    public int getTaskForActivity(IBinder token, boolean onlyRoot) {
        int taskForActivityLocked;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                taskForActivityLocked = ActivityRecord.getTaskForActivityLocked(token, onlyRoot);
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return taskForActivityLocked;
    }

    public List<ActivityManager.RunningTaskInfo> getTasks(int maxNum) {
        return getFilteredTasks(maxNum, 0, 0);
    }

    public List<ActivityManager.RunningTaskInfo> getFilteredTasks(int maxNum, @WindowConfiguration.ActivityType int ignoreActivityType, @WindowConfiguration.WindowingMode int ignoreWindowingMode) {
        int callingUid = Binder.getCallingUid();
        ArrayList<ActivityManager.RunningTaskInfo> list = new ArrayList<>();
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mRootActivityContainer.getRunningTasks(maxNum, list, ignoreActivityType, ignoreWindowingMode, callingUid, isGetTasksAllowed("getTasks", Binder.getCallingPid(), callingUid));
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return list;
    }

    public final void finishSubActivity(IBinder token, String resultWho, int requestCode) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                long origId = Binder.clearCallingIdentity();
                ActivityRecord r = ActivityRecord.isInStackLocked(token);
                if (r != null) {
                    r.getActivityStack().finishSubActivityLocked(r, resultWho, requestCode);
                }
                Binder.restoreCallingIdentity(origId);
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public boolean willActivityBeVisible(IBinder token) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityStack stack = ActivityRecord.getStackLocked(token);
                if (stack != null) {
                    boolean willActivityBeVisibleLocked = stack.willActivityBeVisibleLocked(token);
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return willActivityBeVisibleLocked;
                }
                WindowManagerService.resetPriorityAfterLockedSection();
                return false;
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 12 */
    public void moveTaskToStack(int taskId, int stackId, boolean toTop) {
        long ident;
        enforceCallerIsRecentsOrHasPermission("android.permission.MANAGE_ACTIVITY_STACKS", "moveTaskToStack()");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ident = Binder.clearCallingIdentity();
                TaskRecord task = this.mRootActivityContainer.anyTaskForId(taskId);
                if (task == null) {
                    Slog.w("ActivityTaskManager", "moveTaskToStack: No task for id=" + taskId);
                    Binder.restoreCallingIdentity(ident);
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                ActivityStack stack = this.mRootActivityContainer.getStack(stackId);
                if (stack == null) {
                    throw new IllegalStateException("moveTaskToStack: No stack for stackId=" + stackId);
                } else if (stack.isActivityTypeStandardOrUndefined()) {
                    if (stack.inSplitScreenPrimaryWindowingMode()) {
                        this.mWindowManager.setDockedStackCreateState(0, (Rect) null);
                    }
                    task.reparent(stack, toTop, 1, true, false, "moveTaskToStack");
                    Binder.restoreCallingIdentity(ident);
                    WindowManagerService.resetPriorityAfterLockedSection();
                } else {
                    throw new IllegalArgumentException("moveTaskToStack: Attempt to move task " + taskId + " to stack " + stackId);
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 16 */
    /* JADX WARNING: Code restructure failed: missing block: B:37:?, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00bc, code lost:
        android.os.Binder.restoreCallingIdentity(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00c0, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void resizeStack(int r17, android.graphics.Rect r18, boolean r19, boolean r20, boolean r21, int r22) {
        /*
            r16 = this;
            r1 = r16
            r2 = r17
            java.lang.String r0 = "android.permission.MANAGE_ACTIVITY_STACKS"
            java.lang.String r3 = "resizeStack()"
            r1.enforceCallerIsRecentsOrHasPermission(r0, r3)
            long r3 = android.os.Binder.clearCallingIdentity()
            com.android.server.wm.WindowManagerGlobalLock r5 = r1.mGlobalLock     // Catch:{ all -> 0x00c7 }
            monitor-enter(r5)     // Catch:{ all -> 0x00c7 }
            com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x00c1 }
            if (r21 == 0) goto L_0x0079
            com.android.server.wm.RootActivityContainer r0 = r1.mRootActivityContainer     // Catch:{ all -> 0x0073 }
            com.android.server.wm.ActivityStack r0 = r0.getStack(r2)     // Catch:{ all -> 0x0073 }
            if (r0 != 0) goto L_0x0042
            java.lang.String r6 = "ActivityTaskManager"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x00c1 }
            r7.<init>()     // Catch:{ all -> 0x00c1 }
            java.lang.String r8 = "resizeStack: stackId "
            r7.append(r8)     // Catch:{ all -> 0x00c1 }
            r7.append(r2)     // Catch:{ all -> 0x00c1 }
            java.lang.String r8 = " not found."
            r7.append(r8)     // Catch:{ all -> 0x00c1 }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x00c1 }
            android.util.Slog.w(r6, r7)     // Catch:{ all -> 0x00c1 }
            monitor-exit(r5)     // Catch:{ all -> 0x00c1 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            android.os.Binder.restoreCallingIdentity(r3)
            return
        L_0x0042:
            int r6 = r0.getWindowingMode()     // Catch:{ all -> 0x0073 }
            r7 = 2
            if (r6 != r7) goto L_0x0053
            r6 = 0
            r7 = 0
            r15 = r18
            r14 = r22
            r0.animateResizePinnedStack(r6, r15, r14, r7)     // Catch:{ all -> 0x00c1 }
            goto L_0x00b8
        L_0x0053:
            r15 = r18
            r14 = r22
            java.lang.IllegalArgumentException r6 = new java.lang.IllegalArgumentException     // Catch:{ all -> 0x00c1 }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x00c1 }
            r7.<init>()     // Catch:{ all -> 0x00c1 }
            java.lang.String r8 = "Stack: "
            r7.append(r8)     // Catch:{ all -> 0x00c1 }
            r7.append(r2)     // Catch:{ all -> 0x00c1 }
            java.lang.String r8 = " doesn't support animated resize."
            r7.append(r8)     // Catch:{ all -> 0x00c1 }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x00c1 }
            r6.<init>(r7)     // Catch:{ all -> 0x00c1 }
            throw r6     // Catch:{ all -> 0x00c1 }
        L_0x0073:
            r0 = move-exception
            r15 = r18
            r14 = r22
            goto L_0x00c2
        L_0x0079:
            r15 = r18
            r14 = r22
            com.android.server.wm.RootActivityContainer r0 = r1.mRootActivityContainer     // Catch:{ all -> 0x00c1 }
            com.android.server.wm.ActivityStack r0 = r0.getStack(r2)     // Catch:{ all -> 0x00c1 }
            if (r0 != 0) goto L_0x00a8
            java.lang.String r6 = "ActivityTaskManager"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x00c1 }
            r7.<init>()     // Catch:{ all -> 0x00c1 }
            java.lang.String r8 = "resizeStack: stackId "
            r7.append(r8)     // Catch:{ all -> 0x00c1 }
            r7.append(r2)     // Catch:{ all -> 0x00c1 }
            java.lang.String r8 = " not found."
            r7.append(r8)     // Catch:{ all -> 0x00c1 }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x00c1 }
            android.util.Slog.w(r6, r7)     // Catch:{ all -> 0x00c1 }
            monitor-exit(r5)     // Catch:{ all -> 0x00c1 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            android.os.Binder.restoreCallingIdentity(r3)
            return
        L_0x00a8:
            com.android.server.wm.RootActivityContainer r8 = r1.mRootActivityContainer     // Catch:{ all -> 0x00c1 }
            r11 = 0
            r12 = 0
            r6 = 0
            r9 = r0
            r10 = r18
            r13 = r20
            r14 = r19
            r15 = r6
            r8.resizeStack(r9, r10, r11, r12, r13, r14, r15)     // Catch:{ all -> 0x00c1 }
        L_0x00b8:
            monitor-exit(r5)     // Catch:{ all -> 0x00c1 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()     // Catch:{ all -> 0x00c7 }
            android.os.Binder.restoreCallingIdentity(r3)
            return
        L_0x00c1:
            r0 = move-exception
        L_0x00c2:
            monitor-exit(r5)     // Catch:{ all -> 0x00c1 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()     // Catch:{ all -> 0x00c7 }
            throw r0     // Catch:{ all -> 0x00c7 }
        L_0x00c7:
            r0 = move-exception
            android.os.Binder.restoreCallingIdentity(r3)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityTaskManagerService.resizeStack(int, android.graphics.Rect, boolean, boolean, boolean, int):void");
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    public void offsetPinnedStackBounds(int stackId, Rect compareBounds, int xOffset, int yOffset, int animationDuration) {
        enforceCallerIsRecentsOrHasPermission("android.permission.MANAGE_ACTIVITY_STACKS", "offsetPinnedStackBounds()");
        long ident = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                if (xOffset == 0 && yOffset == 0) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    Binder.restoreCallingIdentity(ident);
                    return;
                }
                ActivityStack stack = this.mRootActivityContainer.getStack(stackId);
                if (stack == null) {
                    Slog.w("ActivityTaskManager", "offsetPinnedStackBounds: stackId " + stackId + " not found.");
                    WindowManagerService.resetPriorityAfterLockedSection();
                    Binder.restoreCallingIdentity(ident);
                } else if (stack.getWindowingMode() == 2) {
                    Rect destBounds = new Rect();
                    stack.getAnimationOrCurrentBounds(destBounds);
                    if (!destBounds.isEmpty()) {
                        if (destBounds.equals(compareBounds)) {
                            destBounds.offset(xOffset, yOffset);
                            stack.animateResizePinnedStack((Rect) null, destBounds, animationDuration, false);
                            WindowManagerService.resetPriorityAfterLockedSection();
                            Binder.restoreCallingIdentity(ident);
                            return;
                        }
                    }
                    Slog.w("ActivityTaskManager", "The current stack bounds does not matched! It may be obsolete.");
                    WindowManagerService.resetPriorityAfterLockedSection();
                    Binder.restoreCallingIdentity(ident);
                } else {
                    throw new IllegalArgumentException("Stack: " + stackId + " doesn't support animated resize.");
                }
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(ident);
            throw th;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 18 */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:25:0x0070=Splitter:B:25:0x0070, B:37:0x009f=Splitter:B:37:0x009f} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean setTaskWindowingModeSplitScreenPrimary(int r19, int r20, boolean r21, boolean r22, android.graphics.Rect r23, boolean r24) {
        /*
            r18 = this;
            r1 = r18
            r2 = r19
            java.lang.String r0 = "android.permission.MANAGE_ACTIVITY_STACKS"
            java.lang.String r3 = "setTaskWindowingModeSplitScreenPrimary()"
            r1.enforceCallerIsRecentsOrHasPermission(r0, r3)
            com.android.server.wm.WindowManagerGlobalLock r3 = r1.mGlobalLock
            monitor-enter(r3)
            com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x00a3 }
            long r4 = android.os.Binder.clearCallingIdentity()     // Catch:{ all -> 0x00a3 }
            com.android.server.wm.RootActivityContainer r0 = r1.mRootActivityContainer     // Catch:{ all -> 0x009a }
            r6 = 0
            com.android.server.wm.TaskRecord r0 = r0.anyTaskForId(r2, r6)     // Catch:{ all -> 0x009a }
            if (r0 != 0) goto L_0x003d
            java.lang.String r7 = "ActivityTaskManager"
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x009a }
            r8.<init>()     // Catch:{ all -> 0x009a }
            java.lang.String r9 = "setTaskWindowingModeSplitScreenPrimary: No task for id="
            r8.append(r9)     // Catch:{ all -> 0x009a }
            r8.append(r2)     // Catch:{ all -> 0x009a }
            java.lang.String r8 = r8.toString()     // Catch:{ all -> 0x009a }
            android.util.Slog.w(r7, r8)     // Catch:{ all -> 0x009a }
            android.os.Binder.restoreCallingIdentity(r4)     // Catch:{ all -> 0x00a3 }
            monitor-exit(r3)     // Catch:{ all -> 0x00a3 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return r6
        L_0x003d:
            boolean r7 = r0.isActivityTypeStandardOrUndefined()     // Catch:{ all -> 0x009a }
            if (r7 == 0) goto L_0x0078
            com.android.server.wm.WindowManagerService r7 = r1.mWindowManager     // Catch:{ all -> 0x009a }
            r8 = r20
            r9 = r23
            r7.setDockedStackCreateState(r8, r9)     // Catch:{ all -> 0x0098 }
            int r7 = r0.getWindowingMode()     // Catch:{ all -> 0x0098 }
            com.android.server.wm.ActivityStack r10 = r0.getStack()     // Catch:{ all -> 0x0098 }
            if (r21 == 0) goto L_0x005b
            java.lang.String r11 = "setTaskWindowingModeSplitScreenPrimary"
            r10.moveToFront(r11, r0)     // Catch:{ all -> 0x0098 }
        L_0x005b:
            r12 = 3
            r15 = 0
            r16 = 0
            r17 = 0
            r11 = r10
            r13 = r22
            r14 = r24
            r11.setWindowingMode(r12, r13, r14, r15, r16, r17)     // Catch:{ all -> 0x0098 }
            int r11 = r0.getWindowingMode()     // Catch:{ all -> 0x0098 }
            if (r7 == r11) goto L_0x0070
            r6 = 1
        L_0x0070:
            android.os.Binder.restoreCallingIdentity(r4)     // Catch:{ all -> 0x00ad }
            monitor-exit(r3)     // Catch:{ all -> 0x00ad }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return r6
        L_0x0078:
            r8 = r20
            r9 = r23
            java.lang.IllegalArgumentException r6 = new java.lang.IllegalArgumentException     // Catch:{ all -> 0x0098 }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x0098 }
            r7.<init>()     // Catch:{ all -> 0x0098 }
            java.lang.String r10 = "setTaskWindowingMode: Attempt to move non-standard task "
            r7.append(r10)     // Catch:{ all -> 0x0098 }
            r7.append(r2)     // Catch:{ all -> 0x0098 }
            java.lang.String r10 = " to split-screen windowing mode"
            r7.append(r10)     // Catch:{ all -> 0x0098 }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x0098 }
            r6.<init>(r7)     // Catch:{ all -> 0x0098 }
            throw r6     // Catch:{ all -> 0x0098 }
        L_0x0098:
            r0 = move-exception
            goto L_0x009f
        L_0x009a:
            r0 = move-exception
            r8 = r20
            r9 = r23
        L_0x009f:
            android.os.Binder.restoreCallingIdentity(r4)     // Catch:{ all -> 0x00ad }
            throw r0     // Catch:{ all -> 0x00ad }
        L_0x00a3:
            r0 = move-exception
            r8 = r20
            r9 = r23
        L_0x00a8:
            monitor-exit(r3)     // Catch:{ all -> 0x00ad }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            throw r0
        L_0x00ad:
            r0 = move-exception
            goto L_0x00a8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityTaskManagerService.setTaskWindowingModeSplitScreenPrimary(int, int, boolean, boolean, android.graphics.Rect, boolean):boolean");
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    public void removeStacksInWindowingModes(int[] windowingModes) {
        long ident;
        enforceCallerIsRecentsOrHasPermission("android.permission.MANAGE_ACTIVITY_STACKS", "removeStacksInWindowingModes()");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ident = Binder.clearCallingIdentity();
                this.mRootActivityContainer.removeStacksInWindowingModes(windowingModes);
                Binder.restoreCallingIdentity(ident);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    public void removeStacksWithActivityTypes(int[] activityTypes) {
        long ident;
        enforceCallerIsRecentsOrHasPermission("android.permission.MANAGE_ACTIVITY_STACKS", "removeStacksWithActivityTypes()");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ident = Binder.clearCallingIdentity();
                this.mRootActivityContainer.removeStacksWithActivityTypes(activityTypes);
                Binder.restoreCallingIdentity(ident);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public ParceledListSlice<ActivityManager.RecentTaskInfo> getRecentTasks(int maxNum, int flags, int userId) {
        ParceledListSlice<ActivityManager.RecentTaskInfo> recentTasks;
        int callingUid = Binder.getCallingUid();
        int userId2 = handleIncomingUser(Binder.getCallingPid(), callingUid, userId, "getRecentTasks");
        boolean allowed = isGetTasksAllowed("getRecentTasks", Binder.getCallingPid(), callingUid);
        boolean detailed = checkGetTasksPermission("android.permission.GET_DETAILED_TASKS", Binder.getCallingPid(), UserHandle.getAppId(callingUid)) == 0;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                recentTasks = this.mRecentTasks.getRecentTasks(maxNum, flags, allowed, detailed, userId2, callingUid);
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return recentTasks;
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    public List<ActivityManager.StackInfo> getAllStackInfos() {
        ArrayList<ActivityManager.StackInfo> allStackInfos;
        enforceCallerIsRecentsOrHasPermission("android.permission.MANAGE_ACTIVITY_STACKS", "getAllStackInfos()");
        long ident = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                allStackInfos = this.mRootActivityContainer.getAllStackInfos();
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            Binder.restoreCallingIdentity(ident);
            return allStackInfos;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(ident);
            throw th;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    public ActivityManager.StackInfo getStackInfo(int windowingMode, int activityType) {
        ActivityManager.StackInfo stackInfo;
        enforceCallerIsRecentsOrHasPermission("android.permission.MANAGE_ACTIVITY_STACKS", "getStackInfo()");
        long ident = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                stackInfo = this.mRootActivityContainer.getStackInfo(windowingMode, activityType);
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            Binder.restoreCallingIdentity(ident);
            return stackInfo;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(ident);
            throw th;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 9 */
    public void cancelRecentsAnimation(boolean restoreHomeStackPosition) {
        int i;
        enforceCallerIsRecentsOrHasPermission("android.permission.MANAGE_ACTIVITY_STACKS", "cancelRecentsAnimation()");
        long callingUid = (long) Binder.getCallingUid();
        long origId = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                WindowManagerService windowManagerService = this.mWindowManager;
                if (restoreHomeStackPosition) {
                    i = 2;
                } else {
                    i = 0;
                }
                windowManagerService.cancelRecentsAnimationSynchronously(i, "cancelRecentsAnimation/uid=" + callingUid);
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            Binder.restoreCallingIdentity(origId);
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(origId);
            throw th;
        }
    }

    public void startLockTaskModeByToken(IBinder token) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord r = ActivityRecord.forTokenLocked(token);
                if (r == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                startLockTaskModeLocked(r.getTaskRecord(), false);
                WindowManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    public void startSystemLockTaskMode(int taskId) {
        this.mAmInternal.enforceCallingPermission("android.permission.MANAGE_ACTIVITY_STACKS", "startSystemLockTaskMode");
        long ident = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                TaskRecord task = this.mRootActivityContainer.anyTaskForId(taskId, 0);
                if (task == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    Binder.restoreCallingIdentity(ident);
                    return;
                }
                task.getStack().moveToFront("startSystemLockTaskMode");
                startLockTaskModeLocked(task, true);
                WindowManagerService.resetPriorityAfterLockedSection();
                Binder.restoreCallingIdentity(ident);
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(ident);
            throw th;
        }
    }

    public void stopLockTaskModeByToken(IBinder token) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord r = ActivityRecord.forTokenLocked(token);
                if (r == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                stopLockTaskModeInternal(r.getTaskRecord(), false);
                WindowManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    public void stopSystemLockTaskMode() throws RemoteException {
        this.mAmInternal.enforceCallingPermission("android.permission.MANAGE_ACTIVITY_STACKS", "stopSystemLockTaskMode");
        stopLockTaskModeInternal((TaskRecord) null, true);
    }

    private void startLockTaskModeLocked(TaskRecord task, boolean isSystemCaller) {
        if (task != null && task.mLockTaskAuth != 0) {
            ActivityStack stack = this.mRootActivityContainer.getTopDisplayFocusedStack();
            if (stack == null || task != stack.topTask()) {
                throw new IllegalArgumentException("Invalid task, not in foreground");
            }
            int callingUid = Binder.getCallingUid();
            long ident = Binder.clearCallingIdentity();
            try {
                this.mRootActivityContainer.removeStacksInWindowingModes(2);
                getLockTaskController().startLockTaskMode(task, isSystemCaller, callingUid);
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    private void stopLockTaskModeInternal(TaskRecord task, boolean isSystemCaller) {
        int callingUid = Binder.getCallingUid();
        long ident = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                getLockTaskController().stopLockTaskMode(task, isSystemCaller, callingUid);
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            TelecomManager tm = (TelecomManager) this.mContext.getSystemService("telecom");
            if (tm != null) {
                tm.showInCallScreen(false);
            }
            Binder.restoreCallingIdentity(ident);
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(ident);
            throw th;
        }
    }

    public void updateLockTaskPackages(int userId, String[] packages) {
        int callingUid = Binder.getCallingUid();
        if (!(callingUid == 0 || callingUid == 1000)) {
            this.mAmInternal.enforceCallingPermission("android.permission.UPDATE_LOCK_TASK_PACKAGES", "updateLockTaskPackages()");
        }
        synchronized (this) {
            getLockTaskController().updateLockTaskPackages(userId, packages);
        }
    }

    public boolean isInLockTaskMode() {
        return getLockTaskModeState() != 0;
    }

    public int getLockTaskModeState() {
        int lockTaskModeState;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                lockTaskModeState = getLockTaskController().getLockTaskModeState();
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return lockTaskModeState;
    }

    public void setTaskDescription(IBinder token, ActivityManager.TaskDescription td) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord r = ActivityRecord.isInStackLocked(token);
                if (r != null) {
                    r.setTaskDescription(td);
                    TaskRecord task = r.getTaskRecord();
                    task.updateTaskDescription();
                    this.mTaskChangeNotificationController.notifyTaskDescriptionChanged(task.getTaskInfo());
                }
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x001e, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
        android.os.Binder.restoreCallingIdentity(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0024, code lost:
        return r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.os.Bundle getActivityOptions(android.os.IBinder r7) {
        /*
            r6 = this;
            long r0 = android.os.Binder.clearCallingIdentity()
            com.android.server.wm.WindowManagerGlobalLock r2 = r6.mGlobalLock     // Catch:{ all -> 0x0033 }
            monitor-enter(r2)     // Catch:{ all -> 0x0033 }
            com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x002d }
            com.android.server.wm.ActivityRecord r3 = com.android.server.wm.ActivityRecord.isInStackLocked(r7)     // Catch:{ all -> 0x002d }
            r4 = 0
            if (r3 == 0) goto L_0x0025
            r5 = 1
            android.app.ActivityOptions r5 = r3.takeOptionsLocked(r5)     // Catch:{ all -> 0x002d }
            if (r5 != 0) goto L_0x0019
            goto L_0x001d
        L_0x0019:
            android.os.Bundle r4 = r5.toBundle()     // Catch:{ all -> 0x002d }
        L_0x001d:
            monitor-exit(r2)     // Catch:{ all -> 0x002d }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            android.os.Binder.restoreCallingIdentity(r0)
            return r4
        L_0x0025:
            monitor-exit(r2)     // Catch:{ all -> 0x002d }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            android.os.Binder.restoreCallingIdentity(r0)
            return r4
        L_0x002d:
            r3 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x002d }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()     // Catch:{ all -> 0x0033 }
            throw r3     // Catch:{ all -> 0x0033 }
        L_0x0033:
            r2 = move-exception
            android.os.Binder.restoreCallingIdentity(r0)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityTaskManagerService.getActivityOptions(android.os.IBinder):android.os.Bundle");
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    public List<IBinder> getAppTasks(String callingPackage) {
        ArrayList<IBinder> appTasksList;
        int callingUid = Binder.getCallingUid();
        long ident = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                appTasksList = this.mRecentTasks.getAppTasksList(callingUid, callingPackage);
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            Binder.restoreCallingIdentity(ident);
            return appTasksList;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(ident);
            throw th;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    public void finishVoiceTask(IVoiceInteractionSession session) {
        long origId;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                origId = Binder.clearCallingIdentity();
                this.mRootActivityContainer.finishVoiceTask(session);
                Binder.restoreCallingIdentity(origId);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public boolean isTopOfTask(IBinder token) {
        boolean z;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord r = ActivityRecord.isInStackLocked(token);
                z = r != null && r.getTaskRecord().getTopActivity() == r;
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return z;
    }

    public void notifyLaunchTaskBehindComplete(IBinder token) {
        this.mStackSupervisor.scheduleLaunchTaskBehindComplete(token);
    }

    public void notifyEnterAnimationComplete(IBinder token) {
        this.mH.post(new Runnable(token) {
            private final /* synthetic */ IBinder f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ActivityTaskManagerService.this.lambda$notifyEnterAnimationComplete$1$ActivityTaskManagerService(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$notifyEnterAnimationComplete$1$ActivityTaskManagerService(IBinder token) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord r = ActivityRecord.forTokenLocked(token);
                if (r != null && r.attachedToProcess()) {
                    try {
                        r.app.getThread().scheduleEnterAnimationComplete(r.appToken);
                    } catch (RemoteException e) {
                    }
                }
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x003d, code lost:
        r2 = null;
        r3 = r9.mGlobalLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0041, code lost:
        monitor-enter(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:?, code lost:
        com.android.server.wm.WindowManagerService.boostPriorityForLockedSection();
        buildAssistBundleLocked(r0, r11);
        r4 = r9.mPendingAssistExtras.remove(r0);
        r9.mUiHandler.removeCallbacks(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0053, code lost:
        if (r4 != false) goto L_0x005a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0055, code lost:
        monitor-exit(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0056, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0059, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:?, code lost:
        r5 = r0.receiver;
        r1 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x005d, code lost:
        if (r5 == null) goto L_0x0097;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x005f, code lost:
        r2 = new android.os.Bundle();
        r2.putInt(com.android.server.wm.ActivityTaskManagerInternal.ASSIST_TASK_ID, r0.activity.getTaskRecord().taskId);
        r2.putBinder(com.android.server.wm.ActivityTaskManagerInternal.ASSIST_ACTIVITY_ID, r0.activity.assistToken);
        r2.putBundle("data", r0.extras);
        r2.putParcelable(com.android.server.wm.ActivityTaskManagerInternal.ASSIST_KEY_STRUCTURE, r0.structure);
        r2.putParcelable(com.android.server.wm.ActivityTaskManagerInternal.ASSIST_KEY_CONTENT, r0.content);
        r2.putBundle(com.android.server.wm.ActivityTaskManagerInternal.ASSIST_KEY_RECEIVER_EXTRAS, r0.receiverExtras);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0097, code lost:
        monitor-exit(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0098, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x009b, code lost:
        if (r1 == null) goto L_0x00a3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:?, code lost:
        r1.onHandleAssistData(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00a3, code lost:
        r3 = android.os.Binder.clearCallingIdentity();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00b3, code lost:
        if (android.text.TextUtils.equals(r0.intent.getAction(), "android.service.voice.VoiceInteractionService") == false) goto L_0x00c6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00b5, code lost:
        r0.intent.putExtras(r0.extras);
        startVoiceInteractionServiceAsUser(r0.intent, r0.userHandle, "AssistContext");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00c6, code lost:
        r0.intent.replaceExtras(r0.extras);
        r0.intent.setFlags(872415232);
        r9.mInternal.closeSystemDialogs(com.android.server.policy.PhoneWindowManager.SYSTEM_DIALOG_REASON_ASSIST);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:?, code lost:
        r9.mContext.startActivityAsUser(r0.intent, new android.os.UserHandle(r0.userHandle));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00ea, code lost:
        r5 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:?, code lost:
        android.util.Slog.w("ActivityTaskManager", "No activity to handle assist action.", r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x00f7, code lost:
        r5 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x00f8, code lost:
        android.os.Binder.restoreCallingIdentity(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x00fb, code lost:
        throw r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x00fc, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x00fe, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x0101, code lost:
        throw r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void reportAssistContextExtras(android.os.IBinder r10, android.os.Bundle r11, android.app.assist.AssistStructure r12, android.app.assist.AssistContent r13, android.net.Uri r14) {
        /*
            r9 = this;
            r0 = r10
            com.android.server.wm.ActivityTaskManagerService$PendingAssistExtras r0 = (com.android.server.wm.ActivityTaskManagerService.PendingAssistExtras) r0
            monitor-enter(r0)
            r0.result = r11     // Catch:{ all -> 0x0102 }
            r0.structure = r12     // Catch:{ all -> 0x0102 }
            r0.content = r13     // Catch:{ all -> 0x0102 }
            if (r14 == 0) goto L_0x0013
            android.os.Bundle r1 = r0.extras     // Catch:{ all -> 0x0102 }
            java.lang.String r2 = "android.intent.extra.REFERRER"
            r1.putParcelable(r2, r14)     // Catch:{ all -> 0x0102 }
        L_0x0013:
            if (r12 == 0) goto L_0x002c
            com.android.server.wm.ActivityRecord r1 = r0.activity     // Catch:{ all -> 0x0102 }
            com.android.server.wm.TaskRecord r1 = r1.getTaskRecord()     // Catch:{ all -> 0x0102 }
            int r1 = r1.taskId     // Catch:{ all -> 0x0102 }
            r12.setTaskId(r1)     // Catch:{ all -> 0x0102 }
            com.android.server.wm.ActivityRecord r1 = r0.activity     // Catch:{ all -> 0x0102 }
            android.content.ComponentName r1 = r1.mActivityComponent     // Catch:{ all -> 0x0102 }
            r12.setActivityComponent(r1)     // Catch:{ all -> 0x0102 }
            boolean r1 = r0.isHome     // Catch:{ all -> 0x0102 }
            r12.setHomeActivity(r1)     // Catch:{ all -> 0x0102 }
        L_0x002c:
            r1 = 1
            r0.haveResult = r1     // Catch:{ all -> 0x0102 }
            r0.notifyAll()     // Catch:{ all -> 0x0102 }
            android.content.Intent r1 = r0.intent     // Catch:{ all -> 0x0102 }
            if (r1 != 0) goto L_0x003c
            android.app.IAssistDataReceiver r1 = r0.receiver     // Catch:{ all -> 0x0102 }
            if (r1 != 0) goto L_0x003c
            monitor-exit(r0)     // Catch:{ all -> 0x0102 }
            return
        L_0x003c:
            monitor-exit(r0)     // Catch:{ all -> 0x0102 }
            r1 = 0
            r2 = 0
            com.android.server.wm.WindowManagerGlobalLock r3 = r9.mGlobalLock
            monitor-enter(r3)
            com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x00fc }
            r9.buildAssistBundleLocked(r0, r11)     // Catch:{ all -> 0x00fc }
            java.util.ArrayList<com.android.server.wm.ActivityTaskManagerService$PendingAssistExtras> r4 = r9.mPendingAssistExtras     // Catch:{ all -> 0x00fc }
            boolean r4 = r4.remove(r0)     // Catch:{ all -> 0x00fc }
            com.android.server.wm.ActivityTaskManagerService$UiHandler r5 = r9.mUiHandler     // Catch:{ all -> 0x00fc }
            r5.removeCallbacks(r0)     // Catch:{ all -> 0x00fc }
            if (r4 != 0) goto L_0x005a
            monitor-exit(r3)     // Catch:{ all -> 0x00fc }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return
        L_0x005a:
            android.app.IAssistDataReceiver r5 = r0.receiver     // Catch:{ all -> 0x00fc }
            r1 = r5
            if (r5 == 0) goto L_0x0097
            android.os.Bundle r5 = new android.os.Bundle     // Catch:{ all -> 0x00fc }
            r5.<init>()     // Catch:{ all -> 0x00fc }
            r2 = r5
            java.lang.String r5 = "taskId"
            com.android.server.wm.ActivityRecord r6 = r0.activity     // Catch:{ all -> 0x00fc }
            com.android.server.wm.TaskRecord r6 = r6.getTaskRecord()     // Catch:{ all -> 0x00fc }
            int r6 = r6.taskId     // Catch:{ all -> 0x00fc }
            r2.putInt(r5, r6)     // Catch:{ all -> 0x00fc }
            java.lang.String r5 = "activityId"
            com.android.server.wm.ActivityRecord r6 = r0.activity     // Catch:{ all -> 0x00fc }
            android.os.Binder r6 = r6.assistToken     // Catch:{ all -> 0x00fc }
            r2.putBinder(r5, r6)     // Catch:{ all -> 0x00fc }
            java.lang.String r5 = "data"
            android.os.Bundle r6 = r0.extras     // Catch:{ all -> 0x00fc }
            r2.putBundle(r5, r6)     // Catch:{ all -> 0x00fc }
            java.lang.String r5 = "structure"
            android.app.assist.AssistStructure r6 = r0.structure     // Catch:{ all -> 0x00fc }
            r2.putParcelable(r5, r6)     // Catch:{ all -> 0x00fc }
            java.lang.String r5 = "content"
            android.app.assist.AssistContent r6 = r0.content     // Catch:{ all -> 0x00fc }
            r2.putParcelable(r5, r6)     // Catch:{ all -> 0x00fc }
            java.lang.String r5 = "receiverExtras"
            android.os.Bundle r6 = r0.receiverExtras     // Catch:{ all -> 0x00fc }
            r2.putBundle(r5, r6)     // Catch:{ all -> 0x00fc }
        L_0x0097:
            monitor-exit(r3)     // Catch:{ all -> 0x00fc }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            if (r1 == 0) goto L_0x00a3
            r1.onHandleAssistData(r2)     // Catch:{ RemoteException -> 0x00a1 }
            goto L_0x00a2
        L_0x00a1:
            r3 = move-exception
        L_0x00a2:
            return
        L_0x00a3:
            long r3 = android.os.Binder.clearCallingIdentity()
            android.content.Intent r5 = r0.intent     // Catch:{ all -> 0x00f7 }
            java.lang.String r5 = r5.getAction()     // Catch:{ all -> 0x00f7 }
            java.lang.String r6 = "android.service.voice.VoiceInteractionService"
            boolean r5 = android.text.TextUtils.equals(r5, r6)     // Catch:{ all -> 0x00f7 }
            if (r5 == 0) goto L_0x00c6
            android.content.Intent r5 = r0.intent     // Catch:{ all -> 0x00f7 }
            android.os.Bundle r6 = r0.extras     // Catch:{ all -> 0x00f7 }
            r5.putExtras(r6)     // Catch:{ all -> 0x00f7 }
            android.content.Intent r5 = r0.intent     // Catch:{ all -> 0x00f7 }
            int r6 = r0.userHandle     // Catch:{ all -> 0x00f7 }
            java.lang.String r7 = "AssistContext"
            r9.startVoiceInteractionServiceAsUser(r5, r6, r7)     // Catch:{ all -> 0x00f7 }
            goto L_0x00f2
        L_0x00c6:
            android.content.Intent r5 = r0.intent     // Catch:{ all -> 0x00f7 }
            android.os.Bundle r6 = r0.extras     // Catch:{ all -> 0x00f7 }
            r5.replaceExtras(r6)     // Catch:{ all -> 0x00f7 }
            android.content.Intent r5 = r0.intent     // Catch:{ all -> 0x00f7 }
            r6 = 872415232(0x34000000, float:1.1920929E-7)
            r5.setFlags(r6)     // Catch:{ all -> 0x00f7 }
            com.android.server.wm.ActivityTaskManagerInternal r5 = r9.mInternal     // Catch:{ all -> 0x00f7 }
            java.lang.String r6 = "assist"
            r5.closeSystemDialogs(r6)     // Catch:{ all -> 0x00f7 }
            android.content.Context r5 = r9.mContext     // Catch:{ ActivityNotFoundException -> 0x00ea }
            android.content.Intent r6 = r0.intent     // Catch:{ ActivityNotFoundException -> 0x00ea }
            android.os.UserHandle r7 = new android.os.UserHandle     // Catch:{ ActivityNotFoundException -> 0x00ea }
            int r8 = r0.userHandle     // Catch:{ ActivityNotFoundException -> 0x00ea }
            r7.<init>(r8)     // Catch:{ ActivityNotFoundException -> 0x00ea }
            r5.startActivityAsUser(r6, r7)     // Catch:{ ActivityNotFoundException -> 0x00ea }
            goto L_0x00f2
        L_0x00ea:
            r5 = move-exception
            java.lang.String r6 = "ActivityTaskManager"
            java.lang.String r7 = "No activity to handle assist action."
            android.util.Slog.w(r6, r7, r5)     // Catch:{ all -> 0x00f7 }
        L_0x00f2:
            android.os.Binder.restoreCallingIdentity(r3)
            return
        L_0x00f7:
            r5 = move-exception
            android.os.Binder.restoreCallingIdentity(r3)
            throw r5
        L_0x00fc:
            r4 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x00fc }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            throw r4
        L_0x0102:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0102 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityTaskManagerService.reportAssistContextExtras(android.os.IBinder, android.os.Bundle, android.app.assist.AssistStructure, android.app.assist.AssistContent, android.net.Uri):void");
    }

    private void startVoiceInteractionServiceAsUser(Intent intent, int userHandle, String reason) {
        ResolveInfo resolveInfo = this.mContext.getPackageManager().resolveServiceAsUser(intent, 0, userHandle);
        if (resolveInfo == null || resolveInfo.serviceInfo == null) {
            Slog.e("ActivityTaskManager", "VoiceInteractionService intent does not resolve. Not starting.");
            return;
        }
        intent.setPackage(resolveInfo.serviceInfo.packageName);
        ((DeviceIdleController.LocalService) LocalServices.getService(DeviceIdleController.LocalService.class)).addPowerSaveTempWhitelistApp(Process.myUid(), intent.getPackage(), APP_SWITCH_DELAY_TIME, userHandle, false, reason);
        try {
            this.mContext.startServiceAsUser(intent, UserHandle.of(userHandle));
        } catch (RuntimeException e) {
            Slog.e("ActivityTaskManager", "VoiceInteractionService failed to start.", e);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 18 */
    public int addAppTask(IBinder activityToken, Intent intent, ActivityManager.TaskDescription description, Bitmap thumbnail) throws RemoteException {
        Intent intent2 = intent;
        int callingUid = Binder.getCallingUid();
        long callingIdent = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityRecord r = ActivityRecord.isInStackLocked(activityToken);
                    if (r != null) {
                        ComponentName comp = intent.getComponent();
                        if (comp != null) {
                            if (thumbnail.getWidth() != this.mThumbnailWidth) {
                                ActivityManager.TaskDescription taskDescription = description;
                                ActivityRecord activityRecord = r;
                            } else if (thumbnail.getHeight() == this.mThumbnailHeight) {
                                if (intent.getSelector() != null) {
                                    intent2.setSelector((Intent) null);
                                }
                                try {
                                    if (intent.getSourceBounds() != null) {
                                        intent2.setSourceBounds((Rect) null);
                                    }
                                    if ((intent.getFlags() & DumpState.DUMP_FROZEN) != 0) {
                                        if ((intent.getFlags() & 8192) == 0) {
                                            intent2.addFlags(8192);
                                        }
                                    }
                                    ActivityInfo ainfo = AppGlobals.getPackageManager().getActivityInfo(comp, 1024, UserHandle.getUserId(callingUid));
                                    if (ainfo.applicationInfo.uid == callingUid) {
                                        ActivityStack stack = r.getActivityStack();
                                        int nextTaskIdForUserLocked = this.mStackSupervisor.getNextTaskIdForUserLocked(r.mUserId);
                                        ActivityRecord activityRecord2 = r;
                                        ActivityStack stack2 = stack;
                                        TaskRecord task = stack.createTaskRecord(nextTaskIdForUserLocked, ainfo, intent, (IVoiceInteractionSession) null, (IVoiceInteractor) null, false);
                                        if (!this.mRecentTasks.addToBottom(task)) {
                                            stack2.removeTask(task, "addAppTask", 0);
                                            WindowManagerService.resetPriorityAfterLockedSection();
                                            Binder.restoreCallingIdentity(callingIdent);
                                            return -1;
                                        }
                                        task.lastTaskDescription.copyFrom(description);
                                        int i = task.taskId;
                                        WindowManagerService.resetPriorityAfterLockedSection();
                                        Binder.restoreCallingIdentity(callingIdent);
                                        return i;
                                    }
                                    ActivityManager.TaskDescription taskDescription2 = description;
                                    ActivityRecord activityRecord3 = r;
                                    throw new SecurityException("Can't add task for another application: target uid=" + ainfo.applicationInfo.uid + ", calling uid=" + callingUid);
                                } catch (Throwable th) {
                                    th = th;
                                    IBinder iBinder = activityToken;
                                    WindowManagerService.resetPriorityAfterLockedSection();
                                    throw th;
                                }
                            } else {
                                ActivityManager.TaskDescription taskDescription3 = description;
                                ActivityRecord activityRecord4 = r;
                            }
                            throw new IllegalArgumentException("Bad thumbnail size: got " + thumbnail.getWidth() + "x" + thumbnail.getHeight() + ", require " + this.mThumbnailWidth + "x" + this.mThumbnailHeight);
                        }
                        ActivityManager.TaskDescription taskDescription4 = description;
                        ActivityRecord activityRecord5 = r;
                        throw new IllegalArgumentException("Intent " + intent2 + " must specify explicit component");
                    }
                    ActivityManager.TaskDescription taskDescription5 = description;
                    ActivityRecord activityRecord6 = r;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Activity does not exist; token=");
                    sb.append(activityToken);
                    throw new IllegalArgumentException(sb.toString());
                } catch (Throwable th2) {
                    th = th2;
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        } catch (Throwable th3) {
            th = th3;
            IBinder iBinder2 = activityToken;
            ActivityManager.TaskDescription taskDescription6 = description;
            Binder.restoreCallingIdentity(callingIdent);
            throw th;
        }
    }

    public Point getAppTaskThumbnailSize() {
        Point point;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                point = new Point(this.mThumbnailWidth, this.mThumbnailHeight);
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return point;
    }

    public void setTaskResizeable(int taskId, int resizeableMode) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                TaskRecord task = this.mRootActivityContainer.anyTaskForId(taskId, 1);
                if (task == null) {
                    Slog.w("ActivityTaskManager", "setTaskResizeable: taskId=" + taskId + " not found");
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                task.setResizeMode(resizeableMode);
                WindowManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 19 */
    public void resizeTask(int taskId, Rect bounds, int resizeMode) {
        ActivityStack stack;
        boolean preserveWindow;
        int i = taskId;
        Rect rect = bounds;
        int i2 = resizeMode;
        this.mAmInternal.enforceCallingPermission("android.permission.MANAGE_ACTIVITY_STACKS", "resizeTask()");
        long ident = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                TaskRecord task = this.mRootActivityContainer.anyTaskForId(i, 0);
                if (task == null) {
                    Slog.w("ActivityTaskManager", "resizeTask: taskId=" + i + " not found");
                    WindowManagerService.resetPriorityAfterLockedSection();
                    Binder.restoreCallingIdentity(ident);
                    return;
                }
                ActivityStack stack2 = task.getStack();
                if (task.getWindowConfiguration().canResizeTask()) {
                    boolean z = true;
                    if (rect == null && stack2.getWindowingMode() == 5) {
                        stack = stack2.getDisplay().getOrCreateStack(1, stack2.getActivityType(), true);
                    } else if (rect == null || stack2.getWindowingMode() == 5) {
                        stack = stack2;
                    } else {
                        stack = stack2.getDisplay().getOrCreateStack(5, stack2.getActivityType(), true);
                    }
                    if ((i2 & 1) == 0) {
                        z = false;
                    }
                    boolean preserveWindow2 = z;
                    if (stack != task.getStack()) {
                        ActivityStack activityStack = stack;
                        task.reparent(stack, true, 1, true, true, "resizeTask");
                        preserveWindow = false;
                    } else {
                        preserveWindow = preserveWindow2;
                    }
                    task.resize(rect, i2, preserveWindow, false);
                    WindowManagerService.resetPriorityAfterLockedSection();
                    Binder.restoreCallingIdentity(ident);
                    return;
                }
                throw new IllegalArgumentException("resizeTask not allowed on task=" + task);
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(ident);
            throw th;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    public boolean releaseActivityInstance(IBinder token) {
        long origId;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                origId = Binder.clearCallingIdentity();
                ActivityRecord r = ActivityRecord.isInStackLocked(token);
                if (r == null) {
                    Binder.restoreCallingIdentity(origId);
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return false;
                }
                boolean safelyDestroyActivityLocked = r.getActivityStack().safelyDestroyActivityLocked(r, "app-req");
                Binder.restoreCallingIdentity(origId);
                WindowManagerService.resetPriorityAfterLockedSection();
                return safelyDestroyActivityLocked;
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    public void releaseSomeActivities(IApplicationThread appInt) {
        long origId;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                origId = Binder.clearCallingIdentity();
                this.mRootActivityContainer.releaseSomeActivitiesLocked(getProcessController(appInt), "low-mem");
                Binder.restoreCallingIdentity(origId);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    public void setLockScreenShown(boolean keyguardShowing, boolean aodShowing) {
        long ident;
        if (checkCallingPermission("android.permission.DEVICE_POWER") == 0) {
            synchronized (this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ident = Binder.clearCallingIdentity();
                    if (this.mKeyguardShown != keyguardShowing) {
                        this.mKeyguardShown = keyguardShowing;
                        this.mH.sendMessage(PooledLambda.obtainMessage($$Lambda$zwLNi4Hz7werGBGptK8eYRpBWpw.INSTANCE, this.mAmInternal, Boolean.valueOf(keyguardShowing)));
                    }
                    if (!keyguardShowing) {
                        ActivityTaskManagerServiceInjector.setPackageHoldOn(this, (String) null);
                    }
                    this.mKeyguardController.setKeyguardShown(keyguardShowing, aodShowing);
                    Binder.restoreCallingIdentity(ident);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            this.mH.post(new Runnable(keyguardShowing) {
                private final /* synthetic */ boolean f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ActivityTaskManagerService.this.lambda$setLockScreenShown$2$ActivityTaskManagerService(this.f$1);
                }
            });
            return;
        }
        throw new SecurityException("Requires permission android.permission.DEVICE_POWER");
    }

    public /* synthetic */ void lambda$setLockScreenShown$2$ActivityTaskManagerService(boolean keyguardShowing) {
        for (int i = this.mScreenObservers.size() - 1; i >= 0; i--) {
            this.mScreenObservers.get(i).onKeyguardStateChanged(keyguardShowing);
        }
    }

    public void onScreenAwakeChanged(boolean isAwake) {
        this.mH.post(new Runnable(isAwake) {
            private final /* synthetic */ boolean f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ActivityTaskManagerService.this.lambda$onScreenAwakeChanged$3$ActivityTaskManagerService(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$onScreenAwakeChanged$3$ActivityTaskManagerService(boolean isAwake) {
        for (int i = this.mScreenObservers.size() - 1; i >= 0; i--) {
            this.mScreenObservers.get(i).onAwakeStateChanged(isAwake);
        }
    }

    public Bitmap getTaskDescriptionIcon(String filePath, int userId) {
        int userId2 = handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), userId, "getTaskDescriptionIcon");
        if (new File(TaskPersister.getUserImagesDir(userId2), new File(filePath).getName()).getPath().equals(filePath) && filePath.contains("_activity_icon_")) {
            return this.mRecentTasks.getTaskDescriptionIcon(filePath);
        }
        throw new IllegalArgumentException("Bad file path: " + filePath + " passed for userId " + userId2);
    }

    public void startInPlaceAnimationOnFrontMostApplication(Bundle opts) {
        ActivityOptions activityOptions;
        SafeActivityOptions safeOptions = SafeActivityOptions.fromBundle(opts);
        if (safeOptions != null) {
            activityOptions = safeOptions.getOptions(this.mStackSupervisor);
        } else {
            activityOptions = null;
        }
        if (activityOptions == null || activityOptions.getAnimationType() != 10 || activityOptions.getCustomInPlaceResId() == 0) {
            throw new IllegalArgumentException("Expected in-place ActivityOption with valid animation");
        }
        ActivityStack focusedStack = getTopDisplayFocusedStack();
        if (focusedStack != null) {
            DisplayContent dc = focusedStack.getDisplay().mDisplayContent;
            dc.prepareAppTransition(17, false);
            dc.mAppTransition.overrideInPlaceAppTransition(activityOptions.getPackageName(), activityOptions.getCustomInPlaceResId());
            dc.executeAppTransition();
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    public void removeStack(int stackId) {
        long ident;
        enforceCallerIsRecentsOrHasPermission("android.permission.MANAGE_ACTIVITY_STACKS", "removeStack()");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ident = Binder.clearCallingIdentity();
                ActivityStack stack = this.mRootActivityContainer.getStack(stackId);
                if (stack == null) {
                    Slog.w("ActivityTaskManager", "removeStack: No stack with id=" + stackId);
                    Binder.restoreCallingIdentity(ident);
                    WindowManagerService.resetPriorityAfterLockedSection();
                } else if (stack.isActivityTypeStandardOrUndefined()) {
                    this.mStackSupervisor.removeStack(stack);
                    Binder.restoreCallingIdentity(ident);
                    WindowManagerService.resetPriorityAfterLockedSection();
                } else {
                    throw new IllegalArgumentException("Removing non-standard stack is not allowed.");
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    public void moveStackToDisplay(int stackId, int displayId) {
        long ident;
        this.mAmInternal.enforceCallingPermission("android.permission.INTERNAL_SYSTEM_WINDOW", "moveStackToDisplay()");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ident = Binder.clearCallingIdentity();
                this.mRootActivityContainer.moveStackToDisplay(stackId, displayId, true);
                Binder.restoreCallingIdentity(ident);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    public void toggleFreeformWindowingMode(IBinder token) {
        long ident;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ident = Binder.clearCallingIdentity();
                ActivityRecord r = ActivityRecord.forTokenLocked(token);
                if (r != null) {
                    ActivityStack stack = r.getActivityStack();
                    if (stack != null) {
                        if (!stack.inFreeformWindowingMode()) {
                            if (stack.getWindowingMode() != 1) {
                                throw new IllegalStateException("toggleFreeformWindowingMode: You can only toggle between fullscreen and freeform.");
                            }
                        }
                        if (stack.inFreeformWindowingMode()) {
                            stack.setWindowingMode(1);
                        } else if (stack.getParent().inFreeformWindowingMode()) {
                            stack.setWindowingMode(0);
                        } else {
                            stack.setWindowingMode(5);
                        }
                        Binder.restoreCallingIdentity(ident);
                    } else {
                        throw new IllegalStateException("toggleFreeformWindowingMode: the activity doesn't have a stack");
                    }
                } else {
                    throw new IllegalArgumentException("toggleFreeformWindowingMode: No activity record matching token=" + token);
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void registerTaskStackListener(ITaskStackListener listener) {
        enforceCallerIsRecentsOrHasPermission("android.permission.MANAGE_ACTIVITY_STACKS", "registerTaskStackListener()");
        this.mTaskChangeNotificationController.registerTaskStackListener(listener);
    }

    public void unregisterTaskStackListener(ITaskStackListener listener) {
        enforceCallerIsRecentsOrHasPermission("android.permission.MANAGE_ACTIVITY_STACKS", "unregisterTaskStackListener()");
        this.mTaskChangeNotificationController.unregisterTaskStackListener(listener);
    }

    public boolean requestAssistContextExtras(int requestType, IAssistDataReceiver receiver, Bundle receiverExtras, IBinder activityToken, boolean focused, boolean newSessionId) {
        return enqueueAssistContext(requestType, (Intent) null, (String) null, receiver, receiverExtras, activityToken, focused, newSessionId, UserHandle.getCallingUserId(), (Bundle) null, 2000, 0) != null;
    }

    public boolean requestAutofillData(IAssistDataReceiver receiver, Bundle receiverExtras, IBinder activityToken, int flags) {
        return enqueueAssistContext(2, (Intent) null, (String) null, receiver, receiverExtras, activityToken, true, true, UserHandle.getCallingUserId(), (Bundle) null, 2000, flags) != null;
    }

    public boolean launchAssistIntent(Intent intent, int requestType, String hint, int userHandle, Bundle args) {
        return enqueueAssistContext(requestType, intent, hint, (IAssistDataReceiver) null, (Bundle) null, (IBinder) null, true, true, userHandle, args, 500, 0) != null;
    }

    public Bundle getAssistContextExtras(int requestType) {
        PendingAssistExtras pae = enqueueAssistContext(requestType, (Intent) null, (String) null, (IAssistDataReceiver) null, (Bundle) null, (IBinder) null, true, true, UserHandle.getCallingUserId(), (Bundle) null, 500, 0);
        if (pae == null) {
            return null;
        }
        synchronized (pae) {
            while (!pae.haveResult) {
                try {
                    pae.wait();
                } catch (InterruptedException e) {
                }
            }
        }
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                buildAssistBundleLocked(pae, pae.result);
                this.mPendingAssistExtras.remove(pae);
                this.mUiHandler.removeCallbacks(pae);
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return pae.extras;
    }

    private static int checkCallingPermission(String permission) {
        return checkPermission(permission, Binder.getCallingPid(), UserHandle.getAppId(Binder.getCallingUid()));
    }

    /* access modifiers changed from: private */
    public void enforceCallerIsRecentsOrHasPermission(String permission, String func) {
        if (!getRecentTasks().isCallerRecents(Binder.getCallingUid())) {
            this.mAmInternal.enforceCallingPermission(permission, func);
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public int checkGetTasksPermission(String permission, int pid, int uid) {
        return checkPermission(permission, pid, uid);
    }

    static int checkPermission(String permission, int pid, int uid) {
        if (permission == null) {
            return -1;
        }
        return checkComponentPermission(permission, pid, uid, -1, true);
    }

    public static int checkComponentPermission(String permission, int pid, int uid, int owningUid, boolean exported) {
        return ActivityManagerService.checkComponentPermission(permission, pid, uid, owningUid, exported);
    }

    /* access modifiers changed from: package-private */
    public boolean isGetTasksAllowed(String caller, int callingPid, int callingUid) {
        boolean z = true;
        if (getRecentTasks().isCallerRecents(callingUid)) {
            return true;
        }
        if (checkGetTasksPermission("android.permission.REAL_GET_TASKS", callingPid, callingUid) != 0) {
            z = false;
        }
        boolean allowed = z;
        if (!allowed && checkGetTasksPermission("android.permission.GET_TASKS", callingPid, callingUid) == 0) {
            try {
                if (AppGlobals.getPackageManager().isUidPrivileged(callingUid)) {
                    allowed = true;
                }
            } catch (RemoteException e) {
            }
        }
        if (allowed || !ActivityTaskManagerServiceInjector.isGetTasksOpAllowed(this, caller, callingPid, callingUid)) {
            return allowed;
        }
        return true;
    }

    private PendingAssistExtras enqueueAssistContext(int requestType, Intent intent, String hint, IAssistDataReceiver receiver, Bundle receiverExtras, IBinder activityToken, boolean focused, boolean newSessionId, int userHandle, Bundle args, long timeout, int flags) {
        ActivityRecord activity;
        IBinder iBinder = activityToken;
        Bundle bundle = args;
        this.mAmInternal.enforceCallingPermission("android.permission.GET_TOP_ACTIVITY_INFO", "enqueueAssistContext()");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord activity2 = getTopDisplayFocusedStack().getTopActivity();
                if (activity2 == null) {
                    Slog.w("ActivityTaskManager", "getAssistContextExtras failed: no top activity");
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return null;
                } else if (!activity2.attachedToProcess()) {
                    Slog.w("ActivityTaskManager", "getAssistContextExtras failed: no process for " + activity2);
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return null;
                } else {
                    if (focused) {
                        if (iBinder != null) {
                            ActivityRecord caller = ActivityRecord.forTokenLocked(activityToken);
                            if (activity2 != caller) {
                                Slog.w("ActivityTaskManager", "enqueueAssistContext failed: caller " + caller + " is not current top " + activity2);
                                WindowManagerService.resetPriorityAfterLockedSection();
                                return null;
                            }
                        }
                        activity = activity2;
                    } else {
                        ActivityRecord activity3 = ActivityRecord.forTokenLocked(activityToken);
                        if (activity3 == null) {
                            Slog.w("ActivityTaskManager", "enqueueAssistContext failed: activity for token=" + iBinder + " couldn't be found");
                            WindowManagerService.resetPriorityAfterLockedSection();
                            return null;
                        } else if (!activity3.attachedToProcess()) {
                            Slog.w("ActivityTaskManager", "enqueueAssistContext failed: no process for " + activity3);
                            WindowManagerService.resetPriorityAfterLockedSection();
                            return null;
                        } else {
                            activity = activity3;
                        }
                    }
                    Bundle extras = new Bundle();
                    if (bundle != null) {
                        extras.putAll(bundle);
                    }
                    extras.putString("android.intent.extra.ASSIST_PACKAGE", activity.packageName);
                    extras.putInt("android.intent.extra.ASSIST_UID", activity.app.mUid);
                    Bundle bundle2 = extras;
                    PendingAssistExtras pae = new PendingAssistExtras(activity, extras, intent, hint, receiver, receiverExtras, userHandle);
                    pae.isHome = activity.isActivityTypeHome();
                    if (newSessionId) {
                        this.mViSessionId++;
                    }
                    try {
                        activity.app.getThread().requestAssistContextExtras(activity.appToken, pae, requestType, this.mViSessionId, flags);
                        this.mPendingAssistExtras.add(pae);
                        try {
                            this.mUiHandler.postDelayed(pae, timeout);
                        } catch (RemoteException e) {
                        }
                    } catch (RemoteException e2) {
                        long j = timeout;
                        Slog.w("ActivityTaskManager", "getAssistContextExtras failed: crash calling " + activity);
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return null;
                    }
                    try {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return pae;
                    } catch (Throwable th) {
                        e = th;
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw e;
                    }
                }
            } catch (Throwable th2) {
                e = th2;
                long j2 = timeout;
                WindowManagerService.resetPriorityAfterLockedSection();
                throw e;
            }
        }
    }

    private void buildAssistBundleLocked(PendingAssistExtras pae, Bundle result) {
        if (result != null) {
            pae.extras.putBundle("android.intent.extra.ASSIST_CONTEXT", result);
        }
        if (pae.hint != null) {
            pae.extras.putBoolean(pae.hint, true);
        }
    }

    /* access modifiers changed from: private */
    public void pendingAssistExtrasTimedOut(PendingAssistExtras pae) {
        IAssistDataReceiver receiver;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mPendingAssistExtras.remove(pae);
                receiver = pae.receiver;
            } catch (Throwable receiver2) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw receiver2;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        if (receiver != null) {
            Bundle sendBundle = new Bundle();
            sendBundle.putBundle(ActivityTaskManagerInternal.ASSIST_KEY_RECEIVER_EXTRAS, pae.receiverExtras);
            try {
                pae.receiver.onHandleAssistData(sendBundle);
            } catch (RemoteException e) {
            }
        }
    }

    public class PendingAssistExtras extends Binder implements Runnable {
        public final ActivityRecord activity;
        public AssistContent content = null;
        public final Bundle extras;
        public boolean haveResult = false;
        public final String hint;
        public final Intent intent;
        public boolean isHome;
        public final IAssistDataReceiver receiver;
        public Bundle receiverExtras;
        public Bundle result = null;
        public AssistStructure structure = null;
        public final int userHandle;

        public PendingAssistExtras(ActivityRecord _activity, Bundle _extras, Intent _intent, String _hint, IAssistDataReceiver _receiver, Bundle _receiverExtras, int _userHandle) {
            this.activity = _activity;
            this.extras = _extras;
            this.intent = _intent;
            this.hint = _hint;
            this.receiver = _receiver;
            this.receiverExtras = _receiverExtras;
            this.userHandle = _userHandle;
        }

        public void run() {
            Slog.w("ActivityTaskManager", "getAssistContextExtras failed: timeout retrieving from " + this.activity);
            synchronized (this) {
                this.haveResult = true;
                notifyAll();
            }
            ActivityTaskManagerService.this.pendingAssistExtrasTimedOut(this);
        }
    }

    public boolean isAssistDataAllowedOnCurrentActivity() {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityStack focusedStack = getTopDisplayFocusedStack();
                if (focusedStack != null) {
                    if (!focusedStack.isActivityTypeAssistant()) {
                        ActivityRecord activity = focusedStack.getTopActivity();
                        if (activity == null) {
                            WindowManagerService.resetPriorityAfterLockedSection();
                            return false;
                        }
                        int userId = activity.mUserId;
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return !DevicePolicyCache.getInstance().getScreenCaptureDisabled(userId);
                    }
                }
                WindowManagerService.resetPriorityAfterLockedSection();
                return false;
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 9 */
    public boolean showAssistFromActivity(IBinder token, Bundle args) {
        long ident = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord caller = ActivityRecord.forTokenLocked(token);
                ActivityRecord top = getTopDisplayFocusedStack().getTopActivity();
                if (top != caller) {
                    Slog.w("ActivityTaskManager", "showAssistFromActivity failed: caller " + caller + " is not current top " + top);
                    WindowManagerService.resetPriorityAfterLockedSection();
                    Binder.restoreCallingIdentity(ident);
                    return false;
                } else if (!top.nowVisible) {
                    Slog.w("ActivityTaskManager", "showAssistFromActivity failed: caller " + caller + " is not visible");
                    WindowManagerService.resetPriorityAfterLockedSection();
                    Binder.restoreCallingIdentity(ident);
                    return false;
                } else {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    boolean showSessionForActiveService = this.mAssistUtils.showSessionForActiveService(args, 8, (IVoiceInteractionSessionShowCallback) null, token);
                    Binder.restoreCallingIdentity(ident);
                    return showSessionForActiveService;
                }
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(ident);
            throw th;
        }
    }

    public boolean isRootVoiceInteraction(IBinder token) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord r = ActivityRecord.isInStackLocked(token);
                if (r == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return false;
                }
                boolean z = r.rootVoiceInteraction;
                WindowManagerService.resetPriorityAfterLockedSection();
                return z;
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* access modifiers changed from: private */
    public void onLocalVoiceInteractionStartedLocked(IBinder activity, IVoiceInteractionSession voiceSession, IVoiceInteractor voiceInteractor) {
        long token;
        ActivityRecord activityToCallback = ActivityRecord.forTokenLocked(activity);
        if (activityToCallback != null) {
            activityToCallback.setVoiceSessionLocked(voiceSession);
            try {
                activityToCallback.app.getThread().scheduleLocalVoiceInteractionStarted(activity, voiceInteractor);
                token = Binder.clearCallingIdentity();
                startRunningVoiceLocked(voiceSession, activityToCallback.appInfo.uid);
                Binder.restoreCallingIdentity(token);
            } catch (RemoteException e) {
                activityToCallback.clearVoiceSessionLocked();
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(token);
                throw th;
            }
        }
    }

    private void startRunningVoiceLocked(IVoiceInteractionSession session, int targetUid) {
        Slog.d("ActivityTaskManager", "<<<  startRunningVoiceLocked()");
        this.mVoiceWakeLock.setWorkSource(new WorkSource(targetUid));
        IVoiceInteractionSession iVoiceInteractionSession = this.mRunningVoice;
        if (iVoiceInteractionSession == null || iVoiceInteractionSession.asBinder() != session.asBinder()) {
            boolean wasRunningVoice = this.mRunningVoice != null;
            this.mRunningVoice = session;
            if (!wasRunningVoice) {
                this.mVoiceWakeLock.acquire();
                updateSleepIfNeededLocked();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void finishRunningVoiceLocked() {
        if (this.mRunningVoice != null) {
            this.mRunningVoice = null;
            this.mVoiceWakeLock.release();
            updateSleepIfNeededLocked();
        }
    }

    public void setVoiceKeepAwake(IVoiceInteractionSession session, boolean keepAwake) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                if (this.mRunningVoice != null && this.mRunningVoice.asBinder() == session.asBinder()) {
                    if (keepAwake) {
                        this.mVoiceWakeLock.acquire();
                    } else {
                        this.mVoiceWakeLock.release();
                    }
                }
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public ComponentName getActivityClassForToken(IBinder token) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord r = ActivityRecord.isInStackLocked(token);
                if (r == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return null;
                }
                ComponentName component = r.intent.getComponent();
                WindowManagerService.resetPriorityAfterLockedSection();
                return component;
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    public String getPackageForToken(IBinder token) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord r = ActivityRecord.isInStackLocked(token);
                if (r == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return null;
                }
                String str = r.packageName;
                WindowManagerService.resetPriorityAfterLockedSection();
                return str;
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    public void showLockTaskEscapeMessage(IBinder token) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                if (ActivityRecord.forTokenLocked(token) == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                getLockTaskController().showLockTaskToast();
                WindowManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    public void keyguardGoingAway(int flags) {
        enforceNotIsolatedCaller("keyguardGoingAway");
        long token = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                this.mKeyguardController.keyguardGoingAway(flags);
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            Binder.restoreCallingIdentity(token);
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(token);
            throw th;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 12 */
    public void positionTaskInStack(int taskId, int stackId, int position) {
        long ident;
        this.mAmInternal.enforceCallingPermission("android.permission.MANAGE_ACTIVITY_STACKS", "positionTaskInStack()");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ident = Binder.clearCallingIdentity();
                TaskRecord task = this.mRootActivityContainer.anyTaskForId(taskId);
                if (task != null) {
                    ActivityStack stack = this.mRootActivityContainer.getStack(stackId);
                    if (stack == null) {
                        throw new IllegalArgumentException("positionTaskInStack: no stack for id=" + stackId);
                    } else if (stack.isActivityTypeStandardOrUndefined()) {
                        if (task.getStack() == stack) {
                            stack.positionChildAt(task, position);
                        } else {
                            task.reparent(stack, position, 2, false, false, "positionTaskInStack");
                        }
                        Binder.restoreCallingIdentity(ident);
                    } else {
                        throw new IllegalArgumentException("positionTaskInStack: Attempt to change the position of task " + taskId + " in/to non-standard stack");
                    }
                } else {
                    throw new IllegalArgumentException("positionTaskInStack: no task for id=" + taskId);
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    public void reportSizeConfigurations(IBinder token, int[] horizontalSizeConfiguration, int[] verticalSizeConfigurations, int[] smallestSizeConfigurations) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord record = ActivityRecord.isInStackLocked(token);
                if (record != null) {
                    record.setSizeConfigurations(horizontalSizeConfiguration, verticalSizeConfigurations, smallestSizeConfigurations);
                } else {
                    throw new IllegalArgumentException("reportSizeConfigurations: ActivityRecord not found for: " + token);
                }
            } finally {
                WindowManagerService.resetPriorityAfterLockedSection();
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    public void dismissSplitScreenMode(boolean toTop) {
        ActivityStack otherStack;
        enforceCallerIsRecentsOrHasPermission("android.permission.MANAGE_ACTIVITY_STACKS", "dismissSplitScreenMode()");
        long ident = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityStack stack = this.mRootActivityContainer.getDefaultDisplay().getSplitScreenPrimaryStack();
                if (stack == null) {
                    Slog.w("ActivityTaskManager", "dismissSplitScreenMode: primary split-screen stack not found.");
                    WindowManagerService.resetPriorityAfterLockedSection();
                    Binder.restoreCallingIdentity(ident);
                    return;
                }
                if (toTop) {
                    stack.moveToFront("dismissSplitScreenMode");
                } else if (this.mRootActivityContainer.isTopDisplayFocusedStack(stack) && (otherStack = stack.getDisplay().getTopStackInWindowingMode(4)) != null) {
                    otherStack.moveToFront("dismissSplitScreenMode_other");
                }
                stack.setWindowingMode(0);
                this.mStackSupervisor.scheduleIdleIfNeedLocked();
                WindowManagerService.resetPriorityAfterLockedSection();
                Binder.restoreCallingIdentity(ident);
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(ident);
            throw th;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0045, code lost:
        android.os.Binder.restoreCallingIdentity(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0049, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void dismissPip(boolean r8, int r9) {
        /*
            r7 = this;
            java.lang.String r0 = "android.permission.MANAGE_ACTIVITY_STACKS"
            java.lang.String r1 = "dismissPip()"
            r7.enforceCallerIsRecentsOrHasPermission(r0, r1)
            long r0 = android.os.Binder.clearCallingIdentity()
            com.android.server.wm.WindowManagerGlobalLock r2 = r7.mGlobalLock     // Catch:{ all -> 0x006c }
            monitor-enter(r2)     // Catch:{ all -> 0x006c }
            com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x0066 }
            com.android.server.wm.RootActivityContainer r3 = r7.mRootActivityContainer     // Catch:{ all -> 0x0066 }
            com.android.server.wm.ActivityDisplay r3 = r3.getDefaultDisplay()     // Catch:{ all -> 0x0066 }
            com.android.server.wm.ActivityStack r3 = r3.getPinnedStack()     // Catch:{ all -> 0x0066 }
            if (r3 != 0) goto L_0x002c
            java.lang.String r4 = "ActivityTaskManager"
            java.lang.String r5 = "dismissPip: pinned stack not found."
            android.util.Slog.w(r4, r5)     // Catch:{ all -> 0x0066 }
            monitor-exit(r2)     // Catch:{ all -> 0x0066 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            android.os.Binder.restoreCallingIdentity(r0)
            return
        L_0x002c:
            int r4 = r3.getWindowingMode()     // Catch:{ all -> 0x0066 }
            r5 = 2
            if (r4 != r5) goto L_0x004a
            if (r8 == 0) goto L_0x003b
            r4 = 0
            r5 = 0
            r3.animateResizePinnedStack(r5, r5, r9, r4)     // Catch:{ all -> 0x0066 }
            goto L_0x0041
        L_0x003b:
            com.android.server.wm.ActivityStackSupervisor r4 = r7.mStackSupervisor     // Catch:{ all -> 0x0066 }
            r5 = 1
            r4.moveTasksToFullscreenStackLocked(r3, r5)     // Catch:{ all -> 0x0066 }
        L_0x0041:
            monitor-exit(r2)     // Catch:{ all -> 0x0066 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()     // Catch:{ all -> 0x006c }
            android.os.Binder.restoreCallingIdentity(r0)
            return
        L_0x004a:
            java.lang.IllegalArgumentException r4 = new java.lang.IllegalArgumentException     // Catch:{ all -> 0x0066 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0066 }
            r5.<init>()     // Catch:{ all -> 0x0066 }
            java.lang.String r6 = "Stack: "
            r5.append(r6)     // Catch:{ all -> 0x0066 }
            r5.append(r3)     // Catch:{ all -> 0x0066 }
            java.lang.String r6 = " doesn't support animated resize."
            r5.append(r6)     // Catch:{ all -> 0x0066 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0066 }
            r4.<init>(r5)     // Catch:{ all -> 0x0066 }
            throw r4     // Catch:{ all -> 0x0066 }
        L_0x0066:
            r3 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0066 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()     // Catch:{ all -> 0x006c }
            throw r3     // Catch:{ all -> 0x006c }
        L_0x006c:
            r2 = move-exception
            android.os.Binder.restoreCallingIdentity(r0)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityTaskManagerService.dismissPip(boolean, int):void");
    }

    public void suppressResizeConfigChanges(boolean suppress) throws RemoteException {
        this.mAmInternal.enforceCallingPermission("android.permission.MANAGE_ACTIVITY_STACKS", "suppressResizeConfigChanges()");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mSuppressResizeConfigChanges = suppress;
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    public void moveTasksToFullscreenStack(int fromStackId, boolean onTop) {
        long origId;
        enforceCallerIsRecentsOrHasPermission("android.permission.MANAGE_ACTIVITY_STACKS", "moveTasksToFullscreenStack()");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                origId = Binder.clearCallingIdentity();
                ActivityStack stack = this.mRootActivityContainer.getStack(fromStackId);
                if (stack != null) {
                    if (stack.isActivityTypeStandardOrUndefined()) {
                        this.mStackSupervisor.moveTasksToFullscreenStackLocked(stack, onTop);
                    } else {
                        throw new IllegalArgumentException("You can't move tasks from non-standard stacks.");
                    }
                }
                Binder.restoreCallingIdentity(origId);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    public boolean moveTopActivityToPinnedStack(int stackId, Rect bounds) {
        long ident;
        boolean moveTopStackActivityToPinnedStack;
        enforceCallerIsRecentsOrHasPermission("android.permission.MANAGE_ACTIVITY_STACKS", "moveTopActivityToPinnedStack()");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                if (this.mSupportsPictureInPicture) {
                    ident = Binder.clearCallingIdentity();
                    moveTopStackActivityToPinnedStack = this.mRootActivityContainer.moveTopStackActivityToPinnedStack(stackId);
                    Binder.restoreCallingIdentity(ident);
                } else {
                    throw new IllegalStateException("moveTopActivityToPinnedStack:Device doesn't support picture-in-picture mode");
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return moveTopStackActivityToPinnedStack;
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    public boolean isInMultiWindowMode(IBinder token) {
        long origId = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord r = ActivityRecord.isInStackLocked(token);
                if (r == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    Binder.restoreCallingIdentity(origId);
                    return false;
                } else if (r.getWindowingMode() != 5 || !MiuiMultiWindowAdapter.LIST_ABOUT_NO_NEED_IN_FREEFORM.contains(r.packageName)) {
                    boolean inMultiWindowMode = r.inMultiWindowMode();
                    WindowManagerService.resetPriorityAfterLockedSection();
                    Binder.restoreCallingIdentity(origId);
                    return inMultiWindowMode;
                } else {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    Binder.restoreCallingIdentity(origId);
                    return false;
                }
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(origId);
            throw th;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    public boolean isInPictureInPictureMode(IBinder token) {
        boolean isInPictureInPictureMode;
        long origId = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                isInPictureInPictureMode = isInPictureInPictureMode(ActivityRecord.forTokenLocked(token));
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            Binder.restoreCallingIdentity(origId);
            return isInPictureInPictureMode;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(origId);
            throw th;
        }
    }

    private boolean isInPictureInPictureMode(ActivityRecord r) {
        if (r == null || r.getActivityStack() == null || !r.inPinnedWindowingMode() || r.getActivityStack().isInStackLocked(r) == null) {
            return false;
        }
        return !r.getActivityStack().getTaskStack().isAnimatingBoundsToFullscreen();
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0049, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
        android.os.Binder.restoreCallingIdentity(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x004f, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean enterPictureInPictureMode(android.os.IBinder r9, android.app.PictureInPictureParams r10) {
        /*
            r8 = this;
            long r0 = android.os.Binder.clearCallingIdentity()
            com.android.server.wm.WindowManagerGlobalLock r2 = r8.mGlobalLock     // Catch:{ all -> 0x0056 }
            monitor-enter(r2)     // Catch:{ all -> 0x0056 }
            com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x0050 }
            java.lang.String r3 = "enterPictureInPictureMode"
            com.android.server.wm.ActivityRecord r3 = r8.ensureValidPictureInPictureActivityParamsLocked(r3, r9, r10)     // Catch:{ all -> 0x0050 }
            boolean r4 = r8.isInPictureInPictureMode((com.android.server.wm.ActivityRecord) r3)     // Catch:{ all -> 0x0050 }
            r5 = 1
            if (r4 == 0) goto L_0x001f
            monitor-exit(r2)     // Catch:{ all -> 0x0050 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            android.os.Binder.restoreCallingIdentity(r0)
            return r5
        L_0x001f:
            java.lang.String r4 = "enterPictureInPictureMode"
            r6 = 0
            boolean r4 = r3.checkEnterPictureInPictureState(r4, r6)     // Catch:{ all -> 0x0050 }
            if (r4 != 0) goto L_0x0030
            monitor-exit(r2)     // Catch:{ all -> 0x0050 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            android.os.Binder.restoreCallingIdentity(r0)
            return r6
        L_0x0030:
            com.android.server.wm.-$$Lambda$ActivityTaskManagerService$js0zprxhKzo_Mx9ozR8logP_1-c r4 = new com.android.server.wm.-$$Lambda$ActivityTaskManagerService$js0zprxhKzo_Mx9ozR8logP_1-c     // Catch:{ all -> 0x0050 }
            r4.<init>(r3, r10)     // Catch:{ all -> 0x0050 }
            boolean r6 = r8.isKeyguardLocked()     // Catch:{ all -> 0x0050 }
            if (r6 == 0) goto L_0x0045
            com.android.server.wm.ActivityTaskManagerService$1 r6 = new com.android.server.wm.ActivityTaskManagerService$1     // Catch:{ all -> 0x0050 }
            r6.<init>(r4)     // Catch:{ all -> 0x0050 }
            r7 = 0
            r8.dismissKeyguard(r9, r6, r7)     // Catch:{ all -> 0x0050 }
            goto L_0x0048
        L_0x0045:
            r4.run()     // Catch:{ all -> 0x0050 }
        L_0x0048:
            monitor-exit(r2)     // Catch:{ all -> 0x0050 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            android.os.Binder.restoreCallingIdentity(r0)
            return r5
        L_0x0050:
            r3 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0050 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()     // Catch:{ all -> 0x0056 }
            throw r3     // Catch:{ all -> 0x0056 }
        L_0x0056:
            r2 = move-exception
            android.os.Binder.restoreCallingIdentity(r0)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityTaskManagerService.enterPictureInPictureMode(android.os.IBinder, android.app.PictureInPictureParams):boolean");
    }

    public /* synthetic */ void lambda$enterPictureInPictureMode$4$ActivityTaskManagerService(ActivityRecord r, PictureInPictureParams params) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                if (!r.finishing) {
                    if (r.getTaskRecord() != null) {
                        r.pictureInPictureArgs.copyOnlySet(params);
                        float aspectRatio = r.pictureInPictureArgs.getAspectRatio();
                        List<RemoteAction> actions = r.pictureInPictureArgs.getActions();
                        this.mRootActivityContainer.moveActivityToPinnedStack(r, new Rect(r.pictureInPictureArgs.getSourceRectHint()), aspectRatio, "enterPictureInPictureMode");
                        ActivityStack stack = r.getActivityStack();
                        stack.setPictureInPictureAspectRatio(aspectRatio);
                        stack.setPictureInPictureActions(actions);
                        MetricsLoggerWrapper.logPictureInPictureEnter(this.mContext, r.appInfo.uid, r.shortComponentName, r.supportsEnterPipOnTaskSwitch);
                        logPictureInPictureArgs(params);
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return;
                    }
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    public void setPictureInPictureParams(IBinder token, PictureInPictureParams params) {
        long origId = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord r = ensureValidPictureInPictureActivityParamsLocked("setPictureInPictureParams", token, params);
                r.pictureInPictureArgs.copyOnlySet(params);
                if (r.inPinnedWindowingMode()) {
                    ActivityStack stack = r.getActivityStack();
                    if (!stack.isAnimatingBoundsToFullscreen()) {
                        stack.setPictureInPictureAspectRatio(r.pictureInPictureArgs.getAspectRatio());
                        stack.setPictureInPictureActions(r.pictureInPictureArgs.getActions());
                    }
                }
                logPictureInPictureArgs(params);
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            Binder.restoreCallingIdentity(origId);
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(origId);
            throw th;
        }
    }

    public int getMaxNumPictureInPictureActions(IBinder token) {
        return 3;
    }

    private void logPictureInPictureArgs(PictureInPictureParams params) {
        if (params.hasSetActions()) {
            MetricsLogger.histogram(this.mContext, "tron_varz_picture_in_picture_actions_count", params.getActions().size());
        }
        if (params.hasSetAspectRatio()) {
            LogMaker lm = new LogMaker(824);
            lm.addTaggedData(825, Float.valueOf(params.getAspectRatio()));
            MetricsLogger.action(lm);
        }
    }

    private ActivityRecord ensureValidPictureInPictureActivityParamsLocked(String caller, IBinder token, PictureInPictureParams params) {
        if (this.mSupportsPictureInPicture) {
            ActivityRecord r = ActivityRecord.forTokenLocked(token);
            if (r == null) {
                throw new IllegalStateException(caller + ": Can't find activity for token=" + token);
            } else if (!r.supportsPictureInPicture()) {
                throw new IllegalStateException(caller + ": Current activity does not support picture-in-picture.");
            } else if (!params.hasSetAspectRatio() || this.mWindowManager.isValidPictureInPictureAspectRatio(r.getActivityStack().mDisplayId, params.getAspectRatio())) {
                params.truncateActions(getMaxNumPictureInPictureActions(token));
                return r;
            } else {
                float minAspectRatio = this.mContext.getResources().getFloat(17105070);
                float maxAspectRatio = this.mContext.getResources().getFloat(17105069);
                throw new IllegalArgumentException(String.format(caller + ": Aspect ratio is too extreme (must be between %f and %f).", new Object[]{Float.valueOf(minAspectRatio), Float.valueOf(maxAspectRatio)}));
            }
        } else {
            throw new IllegalStateException(caller + ": Device doesn't support picture-in-picture mode.");
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    public IBinder getUriPermissionOwnerForActivity(IBinder activityToken) {
        Binder externalToken;
        enforceNotIsolatedCaller("getUriPermissionOwnerForActivity");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord r = ActivityRecord.isInStackLocked(activityToken);
                if (r != null) {
                    externalToken = r.getUriPermissionsLocked().getExternalToken();
                } else {
                    throw new IllegalArgumentException("Activity does not exist; token=" + activityToken);
                }
            } finally {
                WindowManagerService.resetPriorityAfterLockedSection();
            }
        }
        return externalToken;
    }

    /* Debug info: failed to restart local var, previous not found, register: 10 */
    public void resizeDockedStack(Rect dockedBounds, Rect tempDockedTaskBounds, Rect tempDockedTaskInsetBounds, Rect tempOtherTaskBounds, Rect tempOtherTaskInsetBounds) {
        enforceCallerIsRecentsOrHasPermission("android.permission.MANAGE_ACTIVITY_STACKS", "resizeDockedStack()");
        long ident = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                this.mStackSupervisor.resizeDockedStackLocked(dockedBounds, tempDockedTaskBounds, tempDockedTaskInsetBounds, tempOtherTaskBounds, tempOtherTaskInsetBounds, true);
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            Binder.restoreCallingIdentity(ident);
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(ident);
            throw th;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    public void setSplitScreenResizing(boolean resizing) {
        enforceCallerIsRecentsOrHasPermission("android.permission.MANAGE_ACTIVITY_STACKS", "setSplitScreenResizing()");
        long ident = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                this.mStackSupervisor.setSplitScreenResizing(resizing);
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            Binder.restoreCallingIdentity(ident);
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(ident);
            throw th;
        }
    }

    public void enforceSystemHasVrFeature() {
        if (!this.mContext.getPackageManager().hasSystemFeature("android.hardware.vr.high_performance")) {
            throw new UnsupportedOperationException("VR mode not supported on this device!");
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    public int setVrMode(IBinder token, boolean enabled, ComponentName packageName) {
        ActivityRecord r;
        enforceSystemHasVrFeature();
        VrManagerInternal vrService = (VrManagerInternal) LocalServices.getService(VrManagerInternal.class);
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                r = ActivityRecord.isInStackLocked(token);
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        if (r != null) {
            int hasVrPackage = vrService.hasVrPackage(packageName, r.mUserId);
            int err = hasVrPackage;
            if (hasVrPackage != 0) {
                return err;
            }
            long callingId = Binder.clearCallingIdentity();
            try {
                synchronized (this.mGlobalLock) {
                    WindowManagerService.boostPriorityForLockedSection();
                    r.requestedVrComponent = enabled ? packageName : null;
                    if (r.isResumedActivityOnDisplay()) {
                        applyUpdateVrModeLocked(r);
                    }
                }
                WindowManagerService.resetPriorityAfterLockedSection();
                Binder.restoreCallingIdentity(callingId);
                return 0;
            } catch (Throwable th2) {
                Binder.restoreCallingIdentity(callingId);
                throw th2;
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    public void startLocalVoiceInteraction(IBinder callingActivity, Bundle options) {
        Slog.i("ActivityTaskManager", "Activity tried to startLocalVoiceInteraction");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord activity = getTopDisplayFocusedStack().getTopActivity();
                if (ActivityRecord.forTokenLocked(callingActivity) == activity) {
                    if (this.mRunningVoice == null && activity.getTaskRecord().voiceSession == null) {
                        if (activity.voiceSession == null) {
                            if (activity.pendingVoiceInteractionStart) {
                                Slog.w("ActivityTaskManager", "Pending start of voice interaction already.");
                                WindowManagerService.resetPriorityAfterLockedSection();
                                return;
                            }
                            activity.pendingVoiceInteractionStart = true;
                            WindowManagerService.resetPriorityAfterLockedSection();
                            ((VoiceInteractionManagerInternal) LocalServices.getService(VoiceInteractionManagerInternal.class)).startLocalVoiceInteraction(callingActivity, options);
                            return;
                        }
                    }
                    Slog.w("ActivityTaskManager", "Already in a voice interaction, cannot start new voice interaction");
                    return;
                }
                throw new SecurityException("Only focused activity can call startVoiceInteraction");
            } finally {
                WindowManagerService.resetPriorityAfterLockedSection();
            }
        }
    }

    public void stopLocalVoiceInteraction(IBinder callingActivity) {
        ((VoiceInteractionManagerInternal) LocalServices.getService(VoiceInteractionManagerInternal.class)).stopLocalVoiceInteraction(callingActivity);
    }

    public boolean supportsLocalVoiceInteraction() {
        return ((VoiceInteractionManagerInternal) LocalServices.getService(VoiceInteractionManagerInternal.class)).supportsLocalVoiceInteraction();
    }

    public void notifyPinnedStackAnimationStarted() {
        this.mTaskChangeNotificationController.notifyPinnedStackAnimationStarted();
    }

    public void notifyPinnedStackAnimationEnded() {
        this.mTaskChangeNotificationController.notifyPinnedStackAnimationEnded();
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    public void resizePinnedStack(Rect pinnedBounds, Rect tempPinnedTaskBounds) {
        enforceCallerIsRecentsOrHasPermission("android.permission.MANAGE_ACTIVITY_STACKS", "resizePinnedStack()");
        long ident = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                this.mStackSupervisor.resizePinnedStackLocked(pinnedBounds, tempPinnedTaskBounds);
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            Binder.restoreCallingIdentity(ident);
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(ident);
            throw th;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 11 */
    public boolean updateDisplayOverrideConfiguration(Configuration values, int displayId) {
        long origId;
        this.mAmInternal.enforceCallingPermission("android.permission.CHANGE_CONFIGURATION", "updateDisplayOverrideConfiguration()");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                boolean z = false;
                if (!this.mRootActivityContainer.isDisplayAdded(displayId)) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return false;
                }
                if (values == null) {
                    if (this.mWindowManager != null) {
                        values = this.mWindowManager.computeNewConfiguration(displayId);
                    }
                }
                if (this.mWindowManager != null) {
                    this.mH.sendMessage(PooledLambda.obtainMessage($$Lambda$ADNhW0r9Skcs9ezrOGURijIlyQ.INSTANCE, this.mAmInternal, Integer.valueOf(displayId)));
                }
                origId = Binder.clearCallingIdentity();
                if (values != null) {
                    Settings.System.clearConfiguration(values);
                }
                updateDisplayOverrideConfigurationLocked(values, (ActivityRecord) null, false, displayId, this.mTmpUpdateConfigurationResult);
                if (this.mTmpUpdateConfigurationResult.changes != 0) {
                    z = true;
                }
                Binder.restoreCallingIdentity(origId);
                WindowManagerService.resetPriorityAfterLockedSection();
                return z;
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 12 */
    public boolean updateConfiguration(Configuration values) {
        boolean z;
        long origId;
        this.mAmInternal.enforceCallingPermission("android.permission.CHANGE_CONFIGURATION", "updateConfiguration()");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                z = false;
                if (values == null && this.mWindowManager != null) {
                    values = this.mWindowManager.computeNewConfiguration(0);
                }
                if (this.mWindowManager != null) {
                    this.mH.sendMessage(PooledLambda.obtainMessage($$Lambda$ADNhW0r9Skcs9ezrOGURijIlyQ.INSTANCE, this.mAmInternal, 0));
                }
                origId = Binder.clearCallingIdentity();
                if (values != null) {
                    Settings.System.clearConfiguration(values);
                }
                updateConfigurationLocked(values, (ActivityRecord) null, false, false, ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION, false, this.mTmpUpdateConfigurationResult);
                if (this.mTmpUpdateConfigurationResult.changes != 0) {
                    z = true;
                }
                Binder.restoreCallingIdentity(origId);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return z;
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    public void dismissKeyguard(IBinder token, IKeyguardDismissCallback callback, CharSequence message) {
        if (message != null) {
            this.mAmInternal.enforceCallingPermission("android.permission.SHOW_KEYGUARD_MESSAGE", "dismissKeyguard()");
        }
        long callingId = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                this.mKeyguardController.dismissKeyguard(token, callback, message);
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            Binder.restoreCallingIdentity(callingId);
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(callingId);
            throw th;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    public void cancelTaskWindowTransition(int taskId) {
        enforceCallerIsRecentsOrHasPermission("android.permission.MANAGE_ACTIVITY_STACKS", "cancelTaskWindowTransition()");
        long ident = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                TaskRecord task = this.mRootActivityContainer.anyTaskForId(taskId, 0);
                if (task == null) {
                    Slog.w("ActivityTaskManager", "cancelTaskWindowTransition: taskId=" + taskId + " not found");
                    WindowManagerService.resetPriorityAfterLockedSection();
                    Binder.restoreCallingIdentity(ident);
                    return;
                }
                task.cancelWindowTransition();
                WindowManagerService.resetPriorityAfterLockedSection();
                Binder.restoreCallingIdentity(ident);
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(ident);
            throw th;
        }
    }

    public ActivityManager.TaskSnapshot getTaskSnapshot(int taskId, boolean reducedResolution) {
        enforceCallerIsRecentsOrHasPermission("android.permission.READ_FRAME_BUFFER", "getTaskSnapshot()");
        long ident = Binder.clearCallingIdentity();
        try {
            return getTaskSnapshot(taskId, reducedResolution, true);
        } finally {
            Binder.restoreCallingIdentity(ident);
        }
    }

    /* access modifiers changed from: private */
    public ActivityManager.TaskSnapshot getTaskSnapshot(int taskId, boolean reducedResolution, boolean restoreFromDisk) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                TaskRecord task = this.mRootActivityContainer.anyTaskForId(taskId, 1);
                if (task == null) {
                    Slog.w("ActivityTaskManager", "getTaskSnapshot: taskId=" + taskId + " not found");
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return null;
                }
                WindowManagerService.resetPriorityAfterLockedSection();
                return task.getSnapshot(reducedResolution, restoreFromDisk);
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    public void setDisablePreviewScreenshots(IBinder token, boolean disable) {
        long origId;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord r = ActivityRecord.isInStackLocked(token);
                if (r == null) {
                    Slog.w("ActivityTaskManager", "setDisablePreviewScreenshots: Unable to find activity for token=" + token);
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                origId = Binder.clearCallingIdentity();
                r.setDisablePreviewScreenshots(disable);
                Binder.restoreCallingIdentity(origId);
                WindowManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public int getLastResumedActivityUserId() {
        this.mAmInternal.enforceCallingPermission("android.permission.INTERACT_ACROSS_USERS_FULL", "getLastResumedActivityUserId()");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                if (this.mLastResumedActivity == null) {
                    int currentUserId = getCurrentUserId();
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return currentUserId;
                }
                int i = this.mLastResumedActivity.mUserId;
                WindowManagerService.resetPriorityAfterLockedSection();
                return i;
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    public void updateLockTaskFeatures(int userId, int flags) {
        int callingUid = Binder.getCallingUid();
        if (!(callingUid == 0 || callingUid == 1000)) {
            this.mAmInternal.enforceCallingPermission("android.permission.UPDATE_LOCK_TASK_PACKAGES", "updateLockTaskFeatures()");
        }
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                getLockTaskController().updateLockTaskFeatures(userId, flags);
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    public void setShowWhenLocked(IBinder token, boolean showWhenLocked) {
        long origId;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord r = ActivityRecord.isInStackLocked(token);
                if (r == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                origId = Binder.clearCallingIdentity();
                r.setShowWhenLocked(showWhenLocked);
                Binder.restoreCallingIdentity(origId);
                WindowManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    public void setInheritShowWhenLocked(IBinder token, boolean inheritShowWhenLocked) {
        long origId;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord r = ActivityRecord.isInStackLocked(token);
                if (r == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                origId = Binder.clearCallingIdentity();
                r.setInheritShowWhenLocked(inheritShowWhenLocked);
                Binder.restoreCallingIdentity(origId);
                WindowManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    public void setTurnScreenOn(IBinder token, boolean turnScreenOn) {
        long origId;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord r = ActivityRecord.isInStackLocked(token);
                if (r == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                origId = Binder.clearCallingIdentity();
                r.setTurnScreenOn(turnScreenOn);
                Binder.restoreCallingIdentity(origId);
                WindowManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    public void registerRemoteAnimations(IBinder token, RemoteAnimationDefinition definition) {
        long origId;
        this.mAmInternal.enforceCallingPermission("android.permission.CONTROL_REMOTE_APP_TRANSITION_ANIMATIONS", "registerRemoteAnimations");
        definition.setCallingPid(Binder.getCallingPid());
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord r = ActivityRecord.isInStackLocked(token);
                if (r == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                origId = Binder.clearCallingIdentity();
                r.registerRemoteAnimations(definition);
                Binder.restoreCallingIdentity(origId);
                WindowManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    public void registerRemoteAnimationForNextActivityStart(String packageName, RemoteAnimationAdapter adapter) {
        long origId;
        this.mAmInternal.enforceCallingPermission("android.permission.CONTROL_REMOTE_APP_TRANSITION_ANIMATIONS", "registerRemoteAnimationForNextActivityStart");
        adapter.setCallingPid(Binder.getCallingPid());
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                origId = Binder.clearCallingIdentity();
                getActivityStartController().registerRemoteAnimationForNextActivityStart(packageName, adapter);
                Binder.restoreCallingIdentity(origId);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    public void registerRemoteAnimationsForDisplay(int displayId, RemoteAnimationDefinition definition) {
        long origId;
        this.mAmInternal.enforceCallingPermission("android.permission.CONTROL_REMOTE_APP_TRANSITION_ANIMATIONS", "registerRemoteAnimations");
        definition.setCallingPid(Binder.getCallingPid());
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityDisplay display = this.mRootActivityContainer.getActivityDisplay(displayId);
                if (display == null) {
                    Slog.e("ActivityTaskManager", "Couldn't find display with id: " + displayId);
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                origId = Binder.clearCallingIdentity();
                display.mDisplayContent.registerRemoteAnimations(definition);
                Binder.restoreCallingIdentity(origId);
                WindowManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    public void alwaysShowUnsupportedCompileSdkWarning(ComponentName activity) {
        long origId;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                origId = Binder.clearCallingIdentity();
                this.mAppWarnings.alwaysShowUnsupportedCompileSdkWarning(activity);
                Binder.restoreCallingIdentity(origId);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void setVrThread(int tid) {
        enforceSystemHasVrFeature();
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                int pid = Binder.getCallingPid();
                this.mVrController.setVrThreadLocked(tid, pid, this.mProcessMap.getProcess(pid));
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void setPersistentVrThread(int tid) {
        if (checkCallingPermission("android.permission.RESTRICTED_VR_ACCESS") == 0) {
            enforceSystemHasVrFeature();
            synchronized (this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    int pid = Binder.getCallingPid();
                    this.mVrController.setPersistentVrThreadLocked(tid, pid, this.mProcessMap.getProcess(pid));
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return;
        }
        String msg = "Permission Denial: setPersistentVrThread() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires " + "android.permission.RESTRICTED_VR_ACCESS";
        Slog.w("ActivityTaskManager", msg);
        throw new SecurityException(msg);
    }

    public void stopAppSwitches() {
        enforceCallerIsRecentsOrHasPermission("android.permission.STOP_APP_SWITCHES", "stopAppSwitches");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mAppSwitchesAllowedTime = SystemClock.uptimeMillis() + APP_SWITCH_DELAY_TIME;
                this.mLastStopAppSwitchesTime = SystemClock.uptimeMillis();
                this.mDidAppSwitch = false;
                getActivityStartController().schedulePendingActivityLaunches(APP_SWITCH_DELAY_TIME);
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void resumeAppSwitches() {
        enforceCallerIsRecentsOrHasPermission("android.permission.STOP_APP_SWITCHES", "resumeAppSwitches");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mAppSwitchesAllowedTime = 0;
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    /* access modifiers changed from: package-private */
    public long getLastStopAppSwitchesTime() {
        return this.mLastStopAppSwitchesTime;
    }

    /* access modifiers changed from: package-private */
    public void onStartActivitySetDidAppSwitch() {
        if (this.mDidAppSwitch) {
            this.mAppSwitchesAllowedTime = 0;
        } else {
            this.mDidAppSwitch = true;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean shouldDisableNonVrUiLocked() {
        return this.mVrController.shouldDisableNonVrUiLocked();
    }

    private void applyUpdateVrModeLocked(ActivityRecord r) {
        if (!(r.requestedVrComponent == null || r.getDisplayId() == 0)) {
            Slog.i("ActivityTaskManager", "Moving " + r.shortComponentName + " from display " + r.getDisplayId() + " to main display for VR");
            this.mRootActivityContainer.moveStackToDisplay(r.getStackId(), 0, true);
        }
        this.mH.post(new Runnable(r) {
            private final /* synthetic */ ActivityRecord f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ActivityTaskManagerService.this.lambda$applyUpdateVrModeLocked$5$ActivityTaskManagerService(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$applyUpdateVrModeLocked$5$ActivityTaskManagerService(ActivityRecord r) {
        if (this.mVrController.onVrModeChanged(r)) {
            synchronized (this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    boolean disableNonVrUi = this.mVrController.shouldDisableNonVrUiLocked();
                    this.mWindowManager.disableNonVrUi(disableNonVrUi);
                    if (disableNonVrUi) {
                        this.mRootActivityContainer.removeStacksInWindowingModes(2);
                    }
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }
    }

    public int getPackageScreenCompatMode(String packageName) {
        int packageScreenCompatModeLocked;
        enforceNotIsolatedCaller("getPackageScreenCompatMode");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                packageScreenCompatModeLocked = this.mCompatModePackages.getPackageScreenCompatModeLocked(packageName);
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return packageScreenCompatModeLocked;
    }

    public void setPackageScreenCompatMode(String packageName, int mode) {
        this.mAmInternal.enforceCallingPermission("android.permission.SET_SCREEN_COMPATIBILITY", "setPackageScreenCompatMode");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mCompatModePackages.setPackageScreenCompatModeLocked(packageName, mode);
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public boolean getPackageAskScreenCompat(String packageName) {
        boolean packageAskCompatModeLocked;
        enforceNotIsolatedCaller("getPackageAskScreenCompat");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                packageAskCompatModeLocked = this.mCompatModePackages.getPackageAskCompatModeLocked(packageName);
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return packageAskCompatModeLocked;
    }

    public void setPackageAskScreenCompat(String packageName, boolean ask) {
        this.mAmInternal.enforceCallingPermission("android.permission.SET_SCREEN_COMPATIBILITY", "setPackageAskScreenCompat");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mCompatModePackages.setPackageAskCompatModeLocked(packageName, ask);
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public static String relaunchReasonToString(int relaunchReason) {
        if (relaunchReason == 1) {
            return "window_resize";
        }
        if (relaunchReason != 2) {
            return null;
        }
        return "free_resize";
    }

    /* access modifiers changed from: package-private */
    public ActivityStack getTopDisplayFocusedStack() {
        return this.mRootActivityContainer.getTopDisplayFocusedStack();
    }

    /* access modifiers changed from: package-private */
    public void notifyTaskPersisterLocked(TaskRecord task, boolean flush) {
        this.mRecentTasks.notifyTaskPersisterLocked(task, flush);
    }

    /* access modifiers changed from: package-private */
    public boolean isKeyguardLocked() {
        return this.mKeyguardController.isKeyguardLocked();
    }

    public void clearLaunchParamsForPackages(List<String> packageNames) {
        this.mAmInternal.enforceCallingPermission("android.permission.MANAGE_ACTIVITY_STACKS", "clearLaunchParamsForPackages");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                for (int i = 0; i < packageNames.size(); i++) {
                    this.mStackSupervisor.mLaunchParamsPersister.removeRecordForPackage(packageNames.get(i));
                }
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void setDisplayToSingleTaskInstance(int displayId) {
        this.mAmInternal.enforceCallingPermission("android.permission.MANAGE_ACTIVITY_STACKS", "setDisplayToSingleTaskInstance");
        long origId = Binder.clearCallingIdentity();
        try {
            ActivityDisplay display = this.mRootActivityContainer.getActivityDisplayOrCreate(displayId);
            if (display != null) {
                display.setDisplayToSingleTaskInstance();
            }
        } finally {
            Binder.restoreCallingIdentity(origId);
        }
    }

    /* access modifiers changed from: package-private */
    public void dumpLastANRLocked(PrintWriter pw) {
        pw.println("ACTIVITY MANAGER LAST ANR (dumpsys activity lastanr)");
        String str = this.mLastANRState;
        if (str == null) {
            pw.println("  <no ANR has occurred since boot>");
        } else {
            pw.println(str);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 9 */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x005f, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:?, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0068, code lost:
        throw r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void dumpLastANRTracesLocked(java.io.PrintWriter r10) {
        /*
            r9 = this;
            java.lang.String r0 = "ACTIVITY MANAGER LAST ANR TRACES (dumpsys activity lastanr-traces)"
            r10.println(r0)
            java.io.File r0 = new java.io.File
            java.lang.String r1 = "/data/anr"
            r0.<init>(r1)
            java.io.File[] r0 = r0.listFiles()
            boolean r1 = com.android.internal.util.ArrayUtils.isEmpty(r0)
            if (r1 == 0) goto L_0x001c
            java.lang.String r1 = "  <no ANR has occurred since boot>"
            r10.println(r1)
            return
        L_0x001c:
            r1 = 0
            int r2 = r0.length
            r3 = 0
        L_0x001f:
            if (r3 >= r2) goto L_0x0035
            r4 = r0[r3]
            if (r1 == 0) goto L_0x0031
            long r5 = r1.lastModified()
            long r7 = r4.lastModified()
            int r5 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r5 >= 0) goto L_0x0032
        L_0x0031:
            r1 = r4
        L_0x0032:
            int r3 = r3 + 1
            goto L_0x001f
        L_0x0035:
            java.lang.String r2 = "File: "
            r10.print(r2)
            java.lang.String r2 = r1.getName()
            r10.print(r2)
            r10.println()
            java.io.BufferedReader r2 = new java.io.BufferedReader     // Catch:{ IOException -> 0x0069 }
            java.io.FileReader r3 = new java.io.FileReader     // Catch:{ IOException -> 0x0069 }
            r3.<init>(r1)     // Catch:{ IOException -> 0x0069 }
            r2.<init>(r3)     // Catch:{ IOException -> 0x0069 }
        L_0x004e:
            java.lang.String r3 = r2.readLine()     // Catch:{ all -> 0x005d }
            r4 = r3
            if (r3 == 0) goto L_0x0059
            r10.println(r4)     // Catch:{ all -> 0x005d }
            goto L_0x004e
        L_0x0059:
            r2.close()     // Catch:{ IOException -> 0x0069 }
            goto L_0x0075
        L_0x005d:
            r3 = move-exception
            throw r3     // Catch:{ all -> 0x005f }
        L_0x005f:
            r4 = move-exception
            r2.close()     // Catch:{ all -> 0x0064 }
            goto L_0x0068
        L_0x0064:
            r5 = move-exception
            r3.addSuppressed(r5)     // Catch:{ IOException -> 0x0069 }
        L_0x0068:
            throw r4     // Catch:{ IOException -> 0x0069 }
        L_0x0069:
            r2 = move-exception
            java.lang.String r3 = "Unable to read: "
            r10.print(r3)
            r10.print(r2)
            r10.println()
        L_0x0075:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityTaskManagerService.dumpLastANRTracesLocked(java.io.PrintWriter):void");
    }

    /* access modifiers changed from: package-private */
    public void dumpActivitiesLocked(FileDescriptor fd, PrintWriter pw, String[] args, int opti, boolean dumpAll, boolean dumpClient, String dumpPackage) {
        dumpActivitiesLocked(fd, pw, args, opti, dumpAll, dumpClient, dumpPackage, "ACTIVITY MANAGER ACTIVITIES (dumpsys activity activities)");
    }

    /* access modifiers changed from: package-private */
    public void dumpActivitiesLocked(FileDescriptor fd, PrintWriter pw, String[] args, int opti, boolean dumpAll, boolean dumpClient, String dumpPackage, String header) {
        pw.println(header);
        boolean printedAnything = this.mRootActivityContainer.dumpActivities(fd, pw, dumpAll, dumpClient, dumpPackage);
        boolean needSep = printedAnything;
        if (ActivityStackSupervisor.printThisActivity(pw, this.mRootActivityContainer.getTopResumedActivity(), dumpPackage, needSep, "  ResumedActivity: ")) {
            printedAnything = true;
            needSep = false;
        }
        if (dumpPackage == null) {
            if (needSep) {
                pw.println();
            }
            printedAnything = true;
            this.mStackSupervisor.dump(pw, "  ");
        }
        if (!printedAnything) {
            pw.println("  (nothing)");
        }
    }

    /* access modifiers changed from: package-private */
    public void dumpActivityContainersLocked(PrintWriter pw) {
        pw.println("ACTIVITY MANAGER STARTER (dumpsys activity containers)");
        this.mRootActivityContainer.dumpChildrenNames(pw, " ");
        pw.println(" ");
    }

    /* access modifiers changed from: package-private */
    public void dumpActivityStarterLocked(PrintWriter pw, String dumpPackage) {
        pw.println("ACTIVITY MANAGER STARTER (dumpsys activity starter)");
        getActivityStartController().dump(pw, "", dumpPackage);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0026, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0027, code lost:
        r7 = new java.lang.String[(r10.length - r11)];
        java.lang.System.arraycopy(r10, r11, r7, 0, r10.length - r11);
        r6 = r15.size() - 1;
        r1 = null;
        r0 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0040, code lost:
        if (r6 < 0) goto L_0x00c8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0042, code lost:
        r17 = r15.get(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x004a, code lost:
        if (r0 == null) goto L_0x004f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x004c, code lost:
        r25.println();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x004f, code lost:
        r2 = r8.mGlobalLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0053, code lost:
        monitor-enter(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
        com.android.server.wm.WindowManagerService.boostPriorityForLockedSection();
        r0 = r17.getTaskRecord();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x005b, code lost:
        if (r1 == r0) goto L_0x008c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x005d, code lost:
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:?, code lost:
        r9.print("TASK ");
        r9.print(r1.affinity);
        r9.print(" id=");
        r9.print(r1.taskId);
        r9.print(" userId=");
        r9.println(r1.userId);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x007c, code lost:
        if (r29 == false) goto L_0x0083;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x007e, code lost:
        r1.dump(r9, "  ");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0083, code lost:
        r19 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0086, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0087, code lost:
        r20 = r6;
        r21 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x008c, code lost:
        r19 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:?, code lost:
        monitor-exit(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x008f, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
        dumpActivity("  ", r24, r25, r15.get(r6), r7, r29);
        r6 = r6 - 1;
        r0 = 1;
        r1 = r19;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00b4, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00b5, code lost:
        r20 = r6;
        r21 = r7;
        r1 = r19;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00bc, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00bd, code lost:
        r20 = r6;
        r21 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:?, code lost:
        monitor-exit(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00c2, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00c5, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00c6, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x00c8, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x001c, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0024, code lost:
        if (r15.size() > 0) goto L_0x0027;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean dumpActivity(java.io.FileDescriptor r24, java.io.PrintWriter r25, java.lang.String r26, java.lang.String[] r27, int r28, boolean r29, boolean r30, boolean r31) {
        /*
            r23 = this;
            r8 = r23
            r9 = r25
            r10 = r27
            r11 = r28
            com.android.server.wm.WindowManagerGlobalLock r1 = r8.mGlobalLock
            monitor-enter(r1)
            com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x00c9 }
            com.android.server.wm.RootActivityContainer r0 = r8.mRootActivityContainer     // Catch:{ all -> 0x00c9 }
            r12 = r26
            r13 = r30
            r14 = r31
            java.util.ArrayList r0 = r0.getDumpActivities(r12, r13, r14)     // Catch:{ all -> 0x00d5 }
            r15 = r0
            monitor-exit(r1)     // Catch:{ all -> 0x00d5 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            int r0 = r15.size()
            r1 = 0
            if (r0 > 0) goto L_0x0027
            return r1
        L_0x0027:
            int r0 = r10.length
            int r0 = r0 - r11
            java.lang.String[] r7 = new java.lang.String[r0]
            int r0 = r10.length
            int r0 = r0 - r11
            java.lang.System.arraycopy(r10, r11, r7, r1, r0)
            r0 = 0
            r1 = 0
            int r2 = r15.size()
            r16 = 1
            int r2 = r2 + -1
            r6 = r2
            r22 = r1
            r1 = r0
            r0 = r22
        L_0x0040:
            if (r6 < 0) goto L_0x00c8
            java.lang.Object r2 = r15.get(r6)
            r17 = r2
            com.android.server.wm.ActivityRecord r17 = (com.android.server.wm.ActivityRecord) r17
            if (r0 == 0) goto L_0x004f
            r25.println()
        L_0x004f:
            r18 = 1
            com.android.server.wm.WindowManagerGlobalLock r2 = r8.mGlobalLock
            monitor-enter(r2)
            com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x00bc }
            com.android.server.wm.TaskRecord r0 = r17.getTaskRecord()     // Catch:{ all -> 0x00bc }
            if (r1 == r0) goto L_0x008c
            r1 = r0
            java.lang.String r3 = "TASK "
            r9.print(r3)     // Catch:{ all -> 0x0086 }
            java.lang.String r3 = r1.affinity     // Catch:{ all -> 0x0086 }
            r9.print(r3)     // Catch:{ all -> 0x0086 }
            java.lang.String r3 = " id="
            r9.print(r3)     // Catch:{ all -> 0x0086 }
            int r3 = r1.taskId     // Catch:{ all -> 0x0086 }
            r9.print(r3)     // Catch:{ all -> 0x0086 }
            java.lang.String r3 = " userId="
            r9.print(r3)     // Catch:{ all -> 0x0086 }
            int r3 = r1.userId     // Catch:{ all -> 0x0086 }
            r9.println(r3)     // Catch:{ all -> 0x0086 }
            if (r29 == 0) goto L_0x0083
            java.lang.String r3 = "  "
            r1.dump(r9, r3)     // Catch:{ all -> 0x0086 }
        L_0x0083:
            r19 = r1
            goto L_0x008e
        L_0x0086:
            r0 = move-exception
            r20 = r6
            r21 = r7
            goto L_0x00c1
        L_0x008c:
            r19 = r1
        L_0x008e:
            monitor-exit(r2)     // Catch:{ all -> 0x00b4 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            java.lang.Object r0 = r15.get(r6)
            r5 = r0
            com.android.server.wm.ActivityRecord r5 = (com.android.server.wm.ActivityRecord) r5
            java.lang.String r2 = "  "
            r1 = r23
            r3 = r24
            r4 = r25
            r20 = r6
            r6 = r7
            r21 = r7
            r7 = r29
            r1.dumpActivity(r2, r3, r4, r5, r6, r7)
            int r6 = r20 + -1
            r0 = r18
            r1 = r19
            r7 = r21
            goto L_0x0040
        L_0x00b4:
            r0 = move-exception
            r20 = r6
            r21 = r7
            r1 = r19
            goto L_0x00c1
        L_0x00bc:
            r0 = move-exception
            r20 = r6
            r21 = r7
        L_0x00c1:
            monitor-exit(r2)     // Catch:{ all -> 0x00c6 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            throw r0
        L_0x00c6:
            r0 = move-exception
            goto L_0x00c1
        L_0x00c8:
            return r16
        L_0x00c9:
            r0 = move-exception
            r12 = r26
            r13 = r30
            r14 = r31
        L_0x00d0:
            monitor-exit(r1)     // Catch:{ all -> 0x00d5 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            throw r0
        L_0x00d5:
            r0 = move-exception
            goto L_0x00d0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityTaskManagerService.dumpActivity(java.io.FileDescriptor, java.io.PrintWriter, java.lang.String, java.lang.String[], int, boolean, boolean, boolean):boolean");
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    private void dumpActivity(String prefix, FileDescriptor fd, PrintWriter pw, ActivityRecord r, String[] args, boolean dumpAll) {
        TransferPipe tp;
        String innerPrefix = prefix + "  ";
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                pw.print(prefix);
                pw.print("ACTIVITY ");
                pw.print(r.shortComponentName);
                pw.print(" ");
                pw.print(Integer.toHexString(System.identityHashCode(r)));
                pw.print(" pid=");
                if (r.hasProcess()) {
                    pw.println(r.app.getPid());
                } else {
                    pw.println("(not running)");
                }
                if (dumpAll) {
                    r.dump(pw, innerPrefix);
                }
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        if (r.attachedToProcess()) {
            pw.flush();
            try {
                tp = new TransferPipe();
                r.app.getThread().dumpActivity(tp.getWriteFd(), r.appToken, innerPrefix, args);
                tp.go(fd);
                tp.kill();
            } catch (IOException e) {
                pw.println(innerPrefix + "Failure while dumping the activity: " + e);
            } catch (RemoteException e2) {
                pw.println(innerPrefix + "Got a RemoteException while dumping the activity");
            } catch (Throwable th2) {
                tp.kill();
                throw th2;
            }
        }
    }

    /* access modifiers changed from: private */
    public void writeSleepStateToProto(ProtoOutputStream proto, int wakeFullness, boolean testPssMode) {
        long sleepToken = proto.start(1146756268059L);
        proto.write(1159641169921L, PowerManagerInternal.wakefulnessToProtoEnum(wakeFullness));
        Iterator<ActivityTaskManagerInternal.SleepToken> it = this.mRootActivityContainer.mSleepTokens.iterator();
        while (it.hasNext()) {
            proto.write(2237677961218L, it.next().toString());
        }
        proto.write(1133871366147L, this.mSleeping);
        proto.write(1133871366148L, this.mShuttingDown);
        proto.write(1133871366149L, testPssMode);
        proto.end(sleepToken);
    }

    /* access modifiers changed from: package-private */
    public int getCurrentUserId() {
        return this.mAmInternal.getCurrentUserId();
    }

    /* access modifiers changed from: private */
    public void enforceNotIsolatedCaller(String caller) {
        if (UserHandle.isIsolated(Binder.getCallingUid())) {
            throw new SecurityException("Isolated process not allowed to call " + caller);
        }
    }

    public Configuration getConfiguration() {
        Configuration ci;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ci = new Configuration(getGlobalConfigurationForCallingPid());
                ci.userSetLocale = false;
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return ci;
    }

    /* access modifiers changed from: package-private */
    public Configuration getGlobalConfiguration() {
        return this.mRootActivityContainer.getConfiguration();
    }

    /* access modifiers changed from: package-private */
    public boolean updateConfigurationLocked(Configuration values, ActivityRecord starting, boolean initLocale) {
        return updateConfigurationLocked(values, starting, initLocale, false);
    }

    /* access modifiers changed from: package-private */
    public boolean updateConfigurationLocked(Configuration values, ActivityRecord starting, boolean initLocale, boolean deferResume) {
        return updateConfigurationLocked(values, starting, initLocale, false, ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION, deferResume);
    }

    /* Debug info: failed to restart local var, previous not found, register: 10 */
    public void updatePersistentConfiguration(Configuration values, int userId) {
        long origId = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                updateConfigurationLocked(values, (ActivityRecord) null, false, true, userId, false);
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            Binder.restoreCallingIdentity(origId);
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(origId);
            throw th;
        }
    }

    /* access modifiers changed from: private */
    public boolean updateConfigurationLocked(Configuration values, ActivityRecord starting, boolean initLocale, boolean persistent, int userId, boolean deferResume) {
        return updateConfigurationLocked(values, starting, initLocale, persistent, userId, deferResume, (UpdateConfigurationResult) null);
    }

    /* access modifiers changed from: package-private */
    public boolean updateConfigurationLocked(Configuration values, ActivityRecord starting, boolean initLocale, boolean persistent, int userId, boolean deferResume, UpdateConfigurationResult result) {
        int changes = 0;
        WindowManagerService windowManagerService = this.mWindowManager;
        if (windowManagerService != null) {
            windowManagerService.deferSurfaceLayout();
        }
        if (values != null) {
            try {
                changes = updateGlobalConfigurationLocked(values, initLocale, persistent, userId, deferResume);
            } catch (Throwable th) {
                WindowManagerService windowManagerService2 = this.mWindowManager;
                if (windowManagerService2 != null) {
                    windowManagerService2.continueSurfaceLayout();
                }
                throw th;
            }
        }
        boolean kept = ensureConfigAndVisibilityAfterUpdate(starting, changes);
        WindowManagerService windowManagerService3 = this.mWindowManager;
        if (windowManagerService3 != null) {
            windowManagerService3.continueSurfaceLayout();
        }
        if (result != null) {
            result.changes = changes;
            result.activityRelaunched = !kept;
        }
        return kept;
    }

    private int updateGlobalConfigurationLocked(Configuration values, boolean initLocale, boolean persistent, int userId, boolean deferResume) {
        Configuration configuration = values;
        boolean z = deferResume;
        this.mTempConfig.setTo(getGlobalConfiguration());
        int changes = this.mTempConfig.updateFrom(configuration);
        if (changes == 0) {
            performDisplayOverrideConfigUpdate(configuration, z, 0);
            return 0;
        }
        EventLog.writeEvent(EventLogTags.CONFIGURATION_CHANGED, changes);
        StatsLog.write(66, configuration.colorMode, configuration.densityDpi, configuration.fontScale, configuration.hardKeyboardHidden, configuration.keyboard, configuration.keyboardHidden, configuration.mcc, configuration.mnc, configuration.navigation, configuration.navigationHidden, configuration.orientation, configuration.screenHeightDp, configuration.screenLayout, configuration.screenWidthDp, configuration.smallestScreenWidthDp, configuration.touchscreen, configuration.uiMode);
        if (!initLocale && !values.getLocales().isEmpty() && configuration.userSetLocale) {
            LocaleList locales = values.getLocales();
            int bestLocaleIndex = 0;
            if (locales.size() > 1) {
                if (this.mSupportedSystemLocales == null) {
                    this.mSupportedSystemLocales = Resources.getSystem().getAssets().getLocales();
                }
                bestLocaleIndex = Math.max(0, locales.getFirstMatchIndex(this.mSupportedSystemLocales));
            }
            SystemProperties.set("persist.sys.locale", locales.get(bestLocaleIndex).toLanguageTag());
            LocaleList.setDefault(locales, bestLocaleIndex);
            this.mH.sendMessage(PooledLambda.obtainMessage($$Lambda$ActivityTaskManagerService$U6g1UdnOPnEF9wX1OTm9nKVXY5k.INSTANCE, this, locales.get(bestLocaleIndex)));
        }
        this.mTempConfig.seq = increaseConfigurationSeqLocked();
        this.mRootActivityContainer.onConfigurationChanged(this.mTempConfig);
        Slog.i("ActivityTaskManager", "Config changes=" + Integer.toHexString(changes) + " " + this.mTempConfig);
        this.mUsageStatsInternal.reportConfigurationChange(this.mTempConfig, this.mAmInternal.getCurrentUserId());
        updateShouldShowDialogsLocked(this.mTempConfig);
        AttributeCache ac = AttributeCache.instance();
        if (ac != null) {
            ac.updateConfiguration(this.mTempConfig);
        }
        this.mSystemThread.applyConfigurationToResources(this.mTempConfig);
        Configuration configCopy = new Configuration(this.mTempConfig);
        if (persistent && Settings.System.hasInterestingConfigurationChanges(changes)) {
            this.mH.sendMessage(PooledLambda.obtainMessage($$Lambda$ActivityTaskManagerService$yP9TbBmrgQ4lrgcxb8oL1pBAs4.INSTANCE, this, Integer.valueOf(userId), configCopy));
        }
        SparseArray<WindowProcessController> pidMap = this.mProcessMap.getPidMap();
        for (int i = pidMap.size() - 1; i >= 0; i--) {
            pidMap.get(pidMap.keyAt(i)).onConfigurationChanged(configCopy);
        }
        ActivityTaskManagerServiceInjector.handleExtraConfigurationChangesForSystem(changes, this.mTempConfig);
        this.mH.sendMessage(PooledLambda.obtainMessage($$Lambda$swA_sUfSJdP8eC8AA9Iby3SuOY.INSTANCE, this.mAmInternal, Integer.valueOf(changes), Boolean.valueOf(initLocale)));
        performDisplayOverrideConfigUpdate(this.mRootActivityContainer.getConfiguration(), z, 0);
        return changes;
    }

    /* access modifiers changed from: package-private */
    public boolean updateDisplayOverrideConfigurationLocked(Configuration values, ActivityRecord starting, boolean deferResume, int displayId) {
        return updateDisplayOverrideConfigurationLocked(values, starting, deferResume, displayId, (UpdateConfigurationResult) null);
    }

    /* access modifiers changed from: package-private */
    public boolean updateDisplayOverrideConfigurationLocked(Configuration values, ActivityRecord starting, boolean deferResume, int displayId, UpdateConfigurationResult result) {
        int changes = 0;
        WindowManagerService windowManagerService = this.mWindowManager;
        if (windowManagerService != null) {
            windowManagerService.deferSurfaceLayout();
        }
        if (values != null) {
            if (displayId == 0) {
                try {
                    changes = updateGlobalConfigurationLocked(values, false, false, ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION, deferResume);
                } catch (Throwable th) {
                    WindowManagerService windowManagerService2 = this.mWindowManager;
                    if (windowManagerService2 != null) {
                        windowManagerService2.continueSurfaceLayout();
                    }
                    throw th;
                }
            } else {
                changes = performDisplayOverrideConfigUpdate(values, deferResume, displayId);
            }
        }
        boolean kept = ensureConfigAndVisibilityAfterUpdate(starting, changes);
        WindowManagerService windowManagerService3 = this.mWindowManager;
        if (windowManagerService3 != null) {
            windowManagerService3.continueSurfaceLayout();
        }
        if (result != null) {
            result.changes = changes;
            result.activityRelaunched = !kept;
        }
        return kept;
    }

    private int performDisplayOverrideConfigUpdate(Configuration values, boolean deferResume, int displayId) {
        this.mTempConfig.setTo(this.mRootActivityContainer.getDisplayOverrideConfiguration(displayId));
        int changes = this.mTempConfig.updateFrom(values);
        if (changes != 0) {
            Slog.i("ActivityTaskManager", "Override config changes=" + Integer.toHexString(changes) + " " + this.mTempConfig + " for displayId=" + displayId);
            this.mRootActivityContainer.setDisplayOverrideConfiguration(this.mTempConfig, displayId);
            if (((changes & 4096) != 0) && displayId == 0) {
                this.mAppWarnings.onDensityChanged();
                this.mH.sendMessage(PooledLambda.obtainMessage($$Lambda$ibmQVLjaQW2x74Wk8TcE0Og2MJM.INSTANCE, this.mAmInternal, 24, 7));
            }
        }
        return changes;
    }

    /* access modifiers changed from: private */
    public void updateEventDispatchingLocked(boolean booted) {
        this.mWindowManager.setEventDispatching(booted && !this.mShuttingDown);
    }

    /* access modifiers changed from: private */
    public void sendPutConfigurationForUserMsg(int userId, Configuration config) {
        Settings.System.putConfigurationForUser(this.mContext.getContentResolver(), config, userId);
    }

    /* access modifiers changed from: private */
    public void sendLocaleToMountDaemonMsg(Locale l) {
        try {
            IStorageManager storageManager = IStorageManager.Stub.asInterface(ServiceManager.getService("mount"));
            Log.d("ActivityTaskManager", "Storing locale " + l.toLanguageTag() + " for decryption UI");
            storageManager.setField("SystemLocale", l.toLanguageTag());
        } catch (RemoteException e) {
            Log.e("ActivityTaskManager", "Error storing locale for decryption UI", e);
        }
    }

    /* access modifiers changed from: private */
    public void expireStartAsCallerTokenMsg(IBinder permissionToken) {
        this.mStartActivitySources.remove(permissionToken);
        this.mExpiredStartAsCallerTokens.add(permissionToken);
    }

    /* access modifiers changed from: private */
    public void forgetStartAsCallerTokenMsg(IBinder permissionToken) {
        this.mExpiredStartAsCallerTokens.remove(permissionToken);
    }

    /* access modifiers changed from: package-private */
    public boolean isActivityStartsLoggingEnabled() {
        return this.mAmInternal.isActivityStartsLoggingEnabled();
    }

    /* access modifiers changed from: package-private */
    public boolean isBackgroundActivityStartsEnabled() {
        return this.mAmInternal.isBackgroundActivityStartsEnabled();
    }

    /* access modifiers changed from: package-private */
    public void enableScreenAfterBoot(boolean booted) {
        EventLog.writeEvent(EventLogTags.BOOT_PROGRESS_ENABLE_SCREEN, SystemClock.uptimeMillis());
        this.mWindowManager.enableScreenAfterBoot();
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                updateEventDispatchingLocked(booted);
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    static long getInputDispatchingTimeoutLocked(ActivityRecord r) {
        if (r == null || !r.hasProcess()) {
            return 8000;
        }
        return getInputDispatchingTimeoutLocked(r.app);
    }

    private static long getInputDispatchingTimeoutLocked(WindowProcessController r) {
        if (r != null) {
            return r.getInputDispatchingTimeout();
        }
        return 8000;
    }

    /* access modifiers changed from: private */
    public void updateShouldShowDialogsLocked(Configuration config) {
        boolean z = false;
        boolean inputMethodExists = (config.keyboard == 1 && config.touchscreen == 1 && config.navigation == 1) ? false : true;
        int modeType = config.uiMode & 15;
        boolean uiModeSupportsDialogs = (modeType == 3 || (modeType == 6 && Build.IS_USER) || modeType == 4 || modeType == 7) ? false : true;
        boolean hideDialogsSet = Settings.Global.getInt(this.mContext.getContentResolver(), "hide_error_dialogs", 0) != 0;
        if (inputMethodExists && uiModeSupportsDialogs && !hideDialogsSet) {
            z = true;
        }
        this.mShowDialogs = z;
    }

    /* access modifiers changed from: private */
    public void updateFontScaleIfNeeded(int userId) {
        float scaleFactor = Settings.System.getFloatForUser(this.mContext.getContentResolver(), "font_scale", 1.0f, userId);
        synchronized (this) {
            if (getGlobalConfiguration().fontScale != scaleFactor) {
                Configuration configuration = this.mWindowManager.computeNewConfiguration(0);
                configuration.fontScale = scaleFactor;
                updatePersistentConfiguration(configuration, userId);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isSleepingOrShuttingDownLocked() {
        if (!TextUtils.isEmpty(ActivityTaskManagerServiceInjector.sPackageHoldOn)) {
            return false;
        }
        if (isSleepingLocked() || this.mShuttingDown) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean isSleepingLocked() {
        return this.mSleeping;
    }

    /* access modifiers changed from: package-private */
    public void setResumedActivityUncheckLocked(ActivityRecord r, String reason) {
        IVoiceInteractionSession session;
        TaskRecord task = r.getTaskRecord();
        if (!task.isActivityTypeStandard()) {
            r.appTimeTracker = null;
        } else if (this.mCurAppTimeTracker != r.appTimeTracker) {
            AppTimeTracker appTimeTracker = this.mCurAppTimeTracker;
            if (appTimeTracker != null) {
                appTimeTracker.stop();
                this.mH.obtainMessage(1, this.mCurAppTimeTracker).sendToTarget();
                this.mRootActivityContainer.clearOtherAppTimeTrackers(r.appTimeTracker);
                this.mCurAppTimeTracker = null;
            }
            if (r.appTimeTracker != null) {
                this.mCurAppTimeTracker = r.appTimeTracker;
                startTimeTrackingFocusedActivityLocked();
            }
        } else {
            startTimeTrackingFocusedActivityLocked();
        }
        if (task.voiceInteractor != null) {
            startRunningVoiceLocked(task.voiceSession, r.info.applicationInfo.uid);
        } else {
            finishRunningVoiceLocked();
            ActivityRecord activityRecord = this.mLastResumedActivity;
            if (activityRecord != null) {
                TaskRecord lastResumedActivityTask = activityRecord.getTaskRecord();
                if (lastResumedActivityTask == null || lastResumedActivityTask.voiceSession == null) {
                    session = this.mLastResumedActivity.voiceSession;
                } else {
                    session = lastResumedActivityTask.voiceSession;
                }
                if (session != null) {
                    finishVoiceTask(session);
                }
            }
        }
        if (!(this.mLastResumedActivity == null || r.mUserId == this.mLastResumedActivity.mUserId)) {
            this.mAmInternal.sendForegroundProfileChanged(r.mUserId);
        }
        updateResumedAppTrace(r);
        this.mLastResumedActivity = r;
        r.getDisplay().setFocusedApp(r, true);
        applyUpdateLockStateLocked(r);
        applyUpdateVrModeLocked(r);
        EventLogTags.writeAmSetResumedActivity(r.mUserId, r.shortComponentName, reason);
    }

    /* access modifiers changed from: package-private */
    public ActivityTaskManagerInternal.SleepToken acquireSleepToken(String tag, int displayId) {
        ActivityTaskManagerInternal.SleepToken token;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                token = this.mRootActivityContainer.createSleepToken(tag, displayId);
                updateSleepIfNeededLocked();
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return token;
    }

    /* access modifiers changed from: package-private */
    public void updateSleepIfNeededLocked() {
        boolean shouldSleep = !this.mRootActivityContainer.hasAwakeDisplay();
        boolean wasSleeping = this.mSleeping;
        boolean updateOomAdj = false;
        if (!shouldSleep) {
            if (wasSleeping) {
                this.mSleeping = false;
                StatsLog.write(14, 2);
                startTimeTrackingFocusedActivityLocked();
                this.mTopProcessState = 2;
                Slog.d("ActivityTaskManager", "Top Process State changed to PROCESS_STATE_TOP");
                this.mStackSupervisor.comeOutOfSleepIfNeededLocked();
            }
            this.mRootActivityContainer.applySleepTokens(true);
            if (wasSleeping) {
                updateOomAdj = true;
            }
        } else if (!this.mSleeping && shouldSleep) {
            this.mSleeping = true;
            StatsLog.write(14, 1);
            AppTimeTracker appTimeTracker = this.mCurAppTimeTracker;
            if (appTimeTracker != null) {
                appTimeTracker.stop();
            }
            this.mTopProcessState = 13;
            Slog.d("ActivityTaskManager", "Top Process State changed to PROCESS_STATE_TOP_SLEEPING");
            this.mStackSupervisor.goingToSleepLocked();
            updateResumedAppTrace((ActivityRecord) null);
            updateOomAdj = true;
        }
        if (updateOomAdj) {
            H h = this.mH;
            ActivityManagerInternal activityManagerInternal = this.mAmInternal;
            Objects.requireNonNull(activityManagerInternal);
            h.post(new Runnable(activityManagerInternal) {
                private final /* synthetic */ ActivityManagerInternal f$0;

                {
                    this.f$0 = r1;
                }

                public final void run() {
                    this.f$0.updateOomAdj();
                }
            });
        }
    }

    /* access modifiers changed from: package-private */
    public void updateOomAdj() {
        H h = this.mH;
        ActivityManagerInternal activityManagerInternal = this.mAmInternal;
        Objects.requireNonNull(activityManagerInternal);
        h.post(new Runnable(activityManagerInternal) {
            private final /* synthetic */ ActivityManagerInternal f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                this.f$0.updateOomAdj();
            }
        });
    }

    /* access modifiers changed from: package-private */
    public void updateCpuStats() {
        H h = this.mH;
        ActivityManagerInternal activityManagerInternal = this.mAmInternal;
        Objects.requireNonNull(activityManagerInternal);
        h.post(new Runnable(activityManagerInternal) {
            private final /* synthetic */ ActivityManagerInternal f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                this.f$0.updateCpuStats();
            }
        });
    }

    /* access modifiers changed from: package-private */
    public void updateBatteryStats(ActivityRecord component, boolean resumed) {
        this.mH.sendMessage(PooledLambda.obtainMessage($$Lambda$hT1kyMEAhvB1Uxr0DFAlnuU3cQ.INSTANCE, this.mAmInternal, component.mActivityComponent, Integer.valueOf(component.app.mUid), Integer.valueOf(component.mUserId), Boolean.valueOf(resumed)));
    }

    /* access modifiers changed from: package-private */
    public void updateActivityUsageStats(ActivityRecord activity, int event) {
        ActivityRecord rootActivity;
        ComponentName taskRoot = null;
        TaskRecord task = activity.getTaskRecord();
        if (!(task == null || (rootActivity = task.getRootActivity()) == null)) {
            taskRoot = rootActivity.mActivityComponent;
        }
        this.mH.sendMessage(PooledLambda.obtainMessage($$Lambda$UB90fpYUkajpKCLGR93ZDlgDhyw.INSTANCE, this.mAmInternal, activity.mActivityComponent, Integer.valueOf(activity.mUserId), Integer.valueOf(event), activity.appToken, taskRoot));
    }

    /* access modifiers changed from: package-private */
    public void setBooting(boolean booting) {
        this.mAmInternal.setBooting(booting);
    }

    /* access modifiers changed from: package-private */
    public boolean isBooting() {
        return this.mAmInternal.isBooting();
    }

    /* access modifiers changed from: package-private */
    public void setBooted(boolean booted) {
        this.mAmInternal.setBooted(booted);
    }

    /* access modifiers changed from: package-private */
    public boolean isBooted() {
        return this.mAmInternal.isBooted();
    }

    /* access modifiers changed from: package-private */
    public void postFinishBooting(boolean finishBooting, boolean enableScreen) {
        this.mH.post(new Runnable(finishBooting, enableScreen) {
            private final /* synthetic */ boolean f$1;
            private final /* synthetic */ boolean f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                ActivityTaskManagerService.this.lambda$postFinishBooting$6$ActivityTaskManagerService(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$postFinishBooting$6$ActivityTaskManagerService(boolean finishBooting, boolean enableScreen) {
        if (finishBooting) {
            this.mAmInternal.finishBooting();
        }
        if (enableScreen) {
            this.mInternal.enableScreenAfterBoot(isBooted());
        }
    }

    /* access modifiers changed from: package-private */
    public void setHeavyWeightProcess(ActivityRecord root) {
        this.mHeavyWeightProcess = root.app;
        this.mH.sendMessage(PooledLambda.obtainMessage($$Lambda$ActivityTaskManagerService$x3j1aVkumtfulORwKd6dHysJyE0.INSTANCE, this, root.app, root.intent, Integer.valueOf(root.mUserId)));
    }

    /* access modifiers changed from: package-private */
    public void clearHeavyWeightProcessIfEquals(WindowProcessController proc) {
        WindowProcessController windowProcessController = this.mHeavyWeightProcess;
        if (windowProcessController != null && windowProcessController == proc) {
            this.mHeavyWeightProcess = null;
            this.mH.sendMessage(PooledLambda.obtainMessage($$Lambda$ActivityTaskManagerService$w70cT1_hTWQQAYctmXaA0BeZuBc.INSTANCE, this, Integer.valueOf(proc.mUserId)));
        }
    }

    /* access modifiers changed from: private */
    public void cancelHeavyWeightProcessNotification(int userId) {
        INotificationManager inm = NotificationManager.getService();
        if (inm != null) {
            try {
                inm.cancelNotificationWithTag(PackageManagerService.PLATFORM_PACKAGE_NAME, (String) null, 11, userId);
            } catch (RuntimeException e) {
                Slog.w("ActivityTaskManager", "Error canceling notification for service", e);
            } catch (RemoteException e2) {
            }
        }
    }

    /* access modifiers changed from: private */
    public void postHeavyWeightProcessNotification(WindowProcessController proc, Intent intent, int userId) {
        INotificationManager inm;
        if (proc != null && (inm = NotificationManager.getService()) != null) {
            try {
                Context context = this.mContext.createPackageContext(proc.mInfo.packageName, 0);
                String text = this.mContext.getString(17040138, new Object[]{context.getApplicationInfo().loadLabel(context.getPackageManager())});
                try {
                    inm.enqueueNotificationWithTag(PackageManagerService.PLATFORM_PACKAGE_NAME, PackageManagerService.PLATFORM_PACKAGE_NAME, (String) null, 11, new Notification.Builder(context, SystemNotificationChannels.HEAVY_WEIGHT_APP).setSmallIcon(17303570).setWhen(0).setOngoing(true).setTicker(text).setColor(this.mContext.getColor(17170460)).setContentTitle(text).setContentText(this.mContext.getText(17040139)).setContentIntent(PendingIntent.getActivityAsUser(this.mContext, 0, intent, 268435456, (Bundle) null, new UserHandle(userId))).build(), userId);
                } catch (RuntimeException e) {
                    Slog.w("ActivityTaskManager", "Error showing notification for heavy-weight app", e);
                } catch (RemoteException e2) {
                }
            } catch (PackageManager.NameNotFoundException e3) {
                Slog.w("ActivityTaskManager", "Unable to create context for heavy notification", e3);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public IIntentSender getIntentSenderLocked(int type, String packageName, int callingUid, int userId, IBinder token, String resultWho, int requestCode, Intent[] intents, String[] resolvedTypes, int flags, Bundle bOptions) {
        ActivityRecord activity;
        int i = type;
        if (i == 3) {
            ActivityRecord activity2 = ActivityRecord.isInStackLocked(token);
            if (activity2 == null) {
                Slog.w("ActivityTaskManager", "Failed createPendingResult: activity " + token + " not in any stack");
                return null;
            }
            IBinder iBinder = token;
            if (activity2.finishing) {
                Slog.w("ActivityTaskManager", "Failed createPendingResult: activity " + activity2 + " is finishing");
                return null;
            }
            activity = activity2;
        } else {
            IBinder iBinder2 = token;
            activity = null;
        }
        PendingIntentRecord rec = this.mPendingIntentController.getIntentSender(type, packageName, callingUid, userId, token, resultWho, requestCode, intents, resolvedTypes, flags, bOptions);
        if (!((flags & 536870912) != 0) && i == 3) {
            if (activity.pendingResults == null) {
                activity.pendingResults = new HashSet<>();
            }
            activity.pendingResults.add(rec.ref);
        }
        return rec;
    }

    private void startTimeTrackingFocusedActivityLocked() {
        AppTimeTracker appTimeTracker;
        ActivityRecord resumedActivity = this.mRootActivityContainer.getTopResumedActivity();
        if (!this.mSleeping && (appTimeTracker = this.mCurAppTimeTracker) != null && resumedActivity != null) {
            appTimeTracker.start(resumedActivity.packageName);
        }
    }

    private void updateResumedAppTrace(ActivityRecord resumed) {
        ActivityRecord activityRecord = this.mTracedResumedActivity;
        if (activityRecord != null) {
            Trace.asyncTraceEnd(64, constructResumedTraceName(activityRecord.packageName), 0);
        }
        if (resumed != null) {
            Trace.asyncTraceBegin(64, constructResumedTraceName(resumed.packageName), 0);
        }
        this.mTracedResumedActivity = resumed;
    }

    private String constructResumedTraceName(String packageName) {
        return "focused app: " + packageName;
    }

    private boolean ensureConfigAndVisibilityAfterUpdate(ActivityRecord starting, int changes) {
        ActivityStack mainStack = this.mRootActivityContainer.getTopDisplayFocusedStack();
        if (mainStack == null) {
            return true;
        }
        if (changes != 0 && starting == null) {
            starting = mainStack.topRunningActivityLocked();
        }
        if (starting == null) {
            return true;
        }
        boolean kept = starting.ensureActivityConfiguration(changes, false);
        this.mRootActivityContainer.ensureActivitiesVisible(starting, changes, false);
        return kept;
    }

    public /* synthetic */ void lambda$scheduleAppGcsLocked$7$ActivityTaskManagerService() {
        this.mAmInternal.scheduleAppGcs();
    }

    /* access modifiers changed from: package-private */
    public void scheduleAppGcsLocked() {
        this.mH.post(new Runnable() {
            public final void run() {
                ActivityTaskManagerService.this.lambda$scheduleAppGcsLocked$7$ActivityTaskManagerService();
            }
        });
    }

    /* access modifiers changed from: package-private */
    public CompatibilityInfo compatibilityInfoForPackageLocked(ApplicationInfo ai) {
        return this.mCompatModePackages.compatibilityInfoForPackageLocked(ai);
    }

    /* access modifiers changed from: package-private */
    public IPackageManager getPackageManager() {
        return AppGlobals.getPackageManager();
    }

    /* access modifiers changed from: package-private */
    public PackageManagerInternal getPackageManagerInternalLocked() {
        if (this.mPmInternal == null) {
            this.mPmInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        }
        return this.mPmInternal;
    }

    /* access modifiers changed from: package-private */
    public PermissionPolicyInternal getPermissionPolicyInternal() {
        if (this.mPermissionPolicyInternal == null) {
            this.mPermissionPolicyInternal = (PermissionPolicyInternal) LocalServices.getService(PermissionPolicyInternal.class);
        }
        return this.mPermissionPolicyInternal;
    }

    /* access modifiers changed from: package-private */
    public AppWarnings getAppWarningsLocked() {
        return this.mAppWarnings;
    }

    /* access modifiers changed from: package-private */
    public Intent getHomeIntent() {
        String str = this.mTopAction;
        String str2 = this.mTopData;
        Intent intent = new Intent(str, str2 != null ? Uri.parse(str2) : null);
        intent.setComponent(this.mTopComponent);
        intent.addFlags(256);
        if (this.mFactoryTest != 1) {
            intent.addCategory("android.intent.category.HOME");
        }
        return intent;
    }

    /* access modifiers changed from: package-private */
    public Intent getSecondaryHomeIntent(String preferredPackage) {
        String str = this.mTopAction;
        String str2 = this.mTopData;
        Intent intent = new Intent(str, str2 != null ? Uri.parse(str2) : null);
        boolean useSystemProvidedLauncher = this.mContext.getResources().getBoolean(17891568);
        if (preferredPackage == null || useSystemProvidedLauncher) {
            intent.setComponent(ComponentName.unflattenFromString(this.mContext.getResources().getString(17039794)));
        } else {
            intent.setPackage(preferredPackage);
        }
        intent.addFlags(256);
        if (this.mFactoryTest != 1) {
            intent.addCategory("android.intent.category.SECONDARY_HOME");
        }
        return intent;
    }

    /* access modifiers changed from: package-private */
    public ApplicationInfo getAppInfoForUser(ApplicationInfo info, int userId) {
        if (info == null) {
            return null;
        }
        ApplicationInfo newInfo = new ApplicationInfo(info);
        newInfo.initForUser(userId);
        return newInfo;
    }

    /* access modifiers changed from: package-private */
    public WindowProcessController getProcessController(String processName, int uid) {
        if (uid == 1000) {
            SparseArray<WindowProcessController> procs = (SparseArray) this.mProcessNames.getMap().get(processName);
            if (procs == null) {
                return null;
            }
            int procCount = procs.size();
            for (int i = 0; i < procCount; i++) {
                int procUid = procs.keyAt(i);
                if (!UserHandle.isApp(procUid) && UserHandle.isSameUser(procUid, uid)) {
                    return procs.valueAt(i);
                }
            }
        }
        return (WindowProcessController) this.mProcessNames.get(processName, uid);
    }

    /* access modifiers changed from: package-private */
    public WindowProcessController getProcessController(IApplicationThread thread) {
        if (thread == null) {
            return null;
        }
        IBinder threadBinder = thread.asBinder();
        ArrayMap<String, SparseArray<WindowProcessController>> pmap = this.mProcessNames.getMap();
        for (int i = pmap.size() - 1; i >= 0; i--) {
            SparseArray<WindowProcessController> procs = pmap.valueAt(i);
            for (int j = procs.size() - 1; j >= 0; j--) {
                WindowProcessController proc = procs.valueAt(j);
                if (proc.hasThread() && proc.getThread().asBinder() == threadBinder) {
                    return proc;
                }
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public WindowProcessController getProcessController(int pid, int uid) {
        WindowProcessController proc = this.mProcessMap.getProcess(pid);
        if (proc != null && UserHandle.isApp(uid) && proc.mUid == uid) {
            return proc;
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public int getUidState(int uid) {
        return this.mActiveUids.getUidState(uid);
    }

    /* access modifiers changed from: package-private */
    public boolean isUidForeground(int uid) {
        return this.mWindowManager.mRoot.isAnyNonToastWindowVisibleForUid(uid);
    }

    /* access modifiers changed from: package-private */
    public boolean isDeviceOwner(int uid) {
        return uid >= 0 && this.mDeviceOwnerUid == uid;
    }

    /* access modifiers changed from: package-private */
    public void setDeviceOwnerUid(int uid) {
        this.mDeviceOwnerUid = uid;
    }

    /* access modifiers changed from: package-private */
    public String getPendingTempWhitelistTagForUidLocked(int uid) {
        return this.mPendingTempWhitelist.get(uid);
    }

    /* access modifiers changed from: package-private */
    public void logAppTooSlow(WindowProcessController app, long startTime, String msg) {
    }

    /* access modifiers changed from: package-private */
    public boolean isAssociatedCompanionApp(int userId, int uid) {
        Set<Integer> allUids = this.mCompanionAppUidsMap.get(Integer.valueOf(userId));
        if (allUids == null) {
            return false;
        }
        return allUids.contains(Integer.valueOf(uid));
    }

    final class H extends Handler {
        static final int FIRST_ACTIVITY_STACK_MSG = 100;
        static final int FIRST_SUPERVISOR_STACK_MSG = 200;
        static final int REPORT_TIME_TRACKER_MSG = 1;

        H(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                ((AppTimeTracker) msg.obj).deliverResult(ActivityTaskManagerService.this.mContext);
            }
        }
    }

    final class UiHandler extends Handler {
        static final int DISMISS_DIALOG_UI_MSG = 1;

        public UiHandler() {
            super(UiThread.get().getLooper(), (Handler.Callback) null, true);
        }

        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                ((Dialog) msg.obj).dismiss();
            }
        }
    }

    final class LocalService extends ActivityTaskManagerInternal {
        LocalService() {
        }

        public ActivityTaskManagerInternal.SleepToken acquireSleepToken(String tag, int displayId) {
            Preconditions.checkNotNull(tag);
            return ActivityTaskManagerService.this.acquireSleepToken(tag, displayId);
        }

        public ComponentName getHomeActivityForUser(int userId) {
            ComponentName componentName;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityRecord homeActivity = ActivityTaskManagerService.this.mRootActivityContainer.getDefaultDisplayHomeActivityForUser(userId);
                    componentName = homeActivity == null ? null : homeActivity.mActivityComponent;
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return componentName;
        }

        public void onLocalVoiceInteractionStarted(IBinder activity, IVoiceInteractionSession voiceSession, IVoiceInteractor voiceInteractor) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.onLocalVoiceInteractionStartedLocked(activity, voiceSession, voiceInteractor);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public void notifyAppTransitionStarting(SparseIntArray reasons, long timestamp) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mStackSupervisor.getActivityMetricsLogger().notifyTransitionStarting(reasons, timestamp);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public void notifyAppTransitionFinished() {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mStackSupervisor.notifyAppTransitionDone();
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public void notifyAppTransitionCancelled() {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mStackSupervisor.notifyAppTransitionDone();
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public List<IBinder> getTopVisibleActivities() {
            List<IBinder> topVisibleActivities;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    topVisibleActivities = ActivityTaskManagerService.this.mRootActivityContainer.getTopVisibleActivities();
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return topVisibleActivities;
        }

        public void notifyDockedStackMinimizedChanged(boolean minimized) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mRootActivityContainer.setDockedStackMinimized(minimized);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public int startActivitiesAsPackage(String packageName, int userId, Intent[] intents, Bundle bOptions) {
            int packageUid;
            Intent[] intentArr = intents;
            Preconditions.checkNotNull(intentArr, "intents");
            String[] resolvedTypes = new String[intentArr.length];
            long ident = Binder.clearCallingIdentity();
            int i = 0;
            while (i < intentArr.length) {
                try {
                    resolvedTypes[i] = intentArr[i].resolveTypeIfNeeded(ActivityTaskManagerService.this.mContext.getContentResolver());
                    i++;
                } catch (RemoteException e) {
                    String str = packageName;
                    int i2 = userId;
                    Binder.restoreCallingIdentity(ident);
                    packageUid = 0;
                    return ActivityTaskManagerService.this.getActivityStartController().startActivitiesInPackage(packageUid, packageName, intents, resolvedTypes, (IBinder) null, SafeActivityOptions.fromBundle(bOptions), userId, false, (PendingIntentRecord) null, false);
                } catch (Throwable th) {
                    th = th;
                    String str2 = packageName;
                    int i3 = userId;
                    Binder.restoreCallingIdentity(ident);
                    throw th;
                }
            }
            try {
                packageUid = AppGlobals.getPackageManager().getPackageUid(packageName, 268435456, userId);
                Binder.restoreCallingIdentity(ident);
            } catch (RemoteException e2) {
                Binder.restoreCallingIdentity(ident);
                packageUid = 0;
                return ActivityTaskManagerService.this.getActivityStartController().startActivitiesInPackage(packageUid, packageName, intents, resolvedTypes, (IBinder) null, SafeActivityOptions.fromBundle(bOptions), userId, false, (PendingIntentRecord) null, false);
            } catch (Throwable th2) {
                th = th2;
                Binder.restoreCallingIdentity(ident);
                throw th;
            }
            return ActivityTaskManagerService.this.getActivityStartController().startActivitiesInPackage(packageUid, packageName, intents, resolvedTypes, (IBinder) null, SafeActivityOptions.fromBundle(bOptions), userId, false, (PendingIntentRecord) null, false);
        }

        public int startActivitiesInPackage(int uid, int realCallingPid, int realCallingUid, String callingPackage, Intent[] intents, String[] resolvedTypes, IBinder resultTo, SafeActivityOptions options, int userId, boolean validateIncomingUser, PendingIntentRecord originatingPendingIntent, boolean allowBackgroundActivityStart) {
            int startActivitiesInPackage;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    startActivitiesInPackage = ActivityTaskManagerService.this.getActivityStartController().startActivitiesInPackage(uid, realCallingPid, realCallingUid, callingPackage, intents, resolvedTypes, resultTo, options, userId, validateIncomingUser, originatingPendingIntent, allowBackgroundActivityStart);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return startActivitiesInPackage;
        }

        public int startActivityInPackage(int uid, int realCallingPid, int realCallingUid, String callingPackage, Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode, int startFlags, SafeActivityOptions options, int userId, TaskRecord inTask, String reason, boolean validateIncomingUser, PendingIntentRecord originatingPendingIntent, boolean allowBackgroundActivityStart) {
            int startActivityInPackage;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    startActivityInPackage = ActivityTaskManagerService.this.getActivityStartController().startActivityInPackage(uid, realCallingPid, realCallingUid, callingPackage, intent, resolvedType, resultTo, resultWho, requestCode, startFlags, options, userId, inTask, reason, validateIncomingUser, originatingPendingIntent, allowBackgroundActivityStart);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return startActivityInPackage;
        }

        public int startActivityAsUser(IApplicationThread caller, String callerPacakge, Intent intent, Bundle options, int userId) {
            ActivityTaskManagerService activityTaskManagerService = ActivityTaskManagerService.this;
            return activityTaskManagerService.startActivityAsUser(caller, callerPacakge, intent, intent.resolveTypeIfNeeded(activityTaskManagerService.mContext.getContentResolver()), (IBinder) null, (String) null, 0, 268435456, (ProfilerInfo) null, options, userId, false);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:19:0x0038, code lost:
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:20:0x003b, code lost:
            if (r8 == null) goto L_?;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:21:0x003d, code lost:
            r8.run();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:31:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:32:?, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void notifyKeyguardFlagsChanged(java.lang.Runnable r8, int r9) {
            /*
                r7 = this;
                com.android.server.wm.ActivityTaskManagerService r0 = com.android.server.wm.ActivityTaskManagerService.this
                com.android.server.wm.WindowManagerGlobalLock r0 = r0.mGlobalLock
                monitor-enter(r0)
                com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x0041 }
                com.android.server.wm.ActivityTaskManagerService r1 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x0041 }
                com.android.server.wm.RootActivityContainer r1 = r1.mRootActivityContainer     // Catch:{ all -> 0x0041 }
                com.android.server.wm.ActivityDisplay r1 = r1.getActivityDisplay((int) r9)     // Catch:{ all -> 0x0041 }
                if (r1 != 0) goto L_0x0017
                monitor-exit(r0)     // Catch:{ all -> 0x0041 }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                return
            L_0x0017:
                com.android.server.wm.DisplayContent r2 = r1.mDisplayContent     // Catch:{ all -> 0x0041 }
                com.android.server.wm.AppTransition r3 = r2.mAppTransition     // Catch:{ all -> 0x0041 }
                int r3 = r3.getAppTransition()     // Catch:{ all -> 0x0041 }
                r4 = 0
                if (r3 == 0) goto L_0x0024
                r3 = 1
                goto L_0x0025
            L_0x0024:
                r3 = r4
            L_0x0025:
                if (r3 != 0) goto L_0x002a
                r2.prepareAppTransition(r4, r4)     // Catch:{ all -> 0x0041 }
            L_0x002a:
                com.android.server.wm.ActivityTaskManagerService r5 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x0041 }
                com.android.server.wm.RootActivityContainer r5 = r5.mRootActivityContainer     // Catch:{ all -> 0x0041 }
                r6 = 0
                r5.ensureActivitiesVisible(r6, r4, r4)     // Catch:{ all -> 0x0041 }
                if (r3 != 0) goto L_0x0037
                r2.executeAppTransition()     // Catch:{ all -> 0x0041 }
            L_0x0037:
                monitor-exit(r0)     // Catch:{ all -> 0x0041 }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                if (r8 == 0) goto L_0x0040
                r8.run()
            L_0x0040:
                return
            L_0x0041:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0041 }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityTaskManagerService.LocalService.notifyKeyguardFlagsChanged(java.lang.Runnable, int):void");
        }

        public void notifyKeyguardTrustedChanged() {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    if (ActivityTaskManagerService.this.mKeyguardController.isKeyguardShowing(0)) {
                        ActivityTaskManagerService.this.mRootActivityContainer.ensureActivitiesVisible((ActivityRecord) null, 0, false);
                    }
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public void setVr2dDisplayId(int vr2dDisplayId) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mVr2dDisplayId = vr2dDisplayId;
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        /* Debug info: failed to restart local var, previous not found, register: 5 */
        public void setFocusedActivity(IBinder token) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityRecord r = ActivityRecord.forTokenLocked(token);
                    if (r == null) {
                        throw new IllegalArgumentException("setFocusedActivity: No activity record matching token=" + token);
                    } else if (r.moveFocusableActivityToTop("setFocusedActivity")) {
                        ActivityTaskManagerService.this.mRootActivityContainer.resumeFocusedStacksTopActivities();
                    }
                } finally {
                    WindowManagerService.resetPriorityAfterLockedSection();
                }
            }
        }

        public void registerScreenObserver(ActivityTaskManagerInternal.ScreenObserver observer) {
            ActivityTaskManagerService.this.mScreenObservers.add(observer);
        }

        public boolean isCallerRecents(int callingUid) {
            return ActivityTaskManagerService.this.getRecentTasks().isCallerRecents(callingUid);
        }

        public boolean isRecentsComponentHomeActivity(int userId) {
            return ActivityTaskManagerService.this.getRecentTasks().isRecentsComponentHomeActivity(userId);
        }

        public void cancelRecentsAnimation(boolean restoreHomeStackPosition) {
            ActivityTaskManagerService.this.cancelRecentsAnimation(restoreHomeStackPosition);
        }

        public void enforceCallerIsRecentsOrHasPermission(String permission, String func) {
            ActivityTaskManagerService.this.enforceCallerIsRecentsOrHasPermission(permission, func);
        }

        public void notifyActiveVoiceInteractionServiceChanged(ComponentName component) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mActiveVoiceInteractionServiceComponent = component;
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        /* JADX WARNING: Code restructure failed: missing block: B:18:0x0042, code lost:
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:19:0x0045, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void setAllowAppSwitches(java.lang.String r4, int r5, int r6) {
            /*
                r3 = this;
                com.android.server.wm.ActivityTaskManagerService r0 = com.android.server.wm.ActivityTaskManagerService.this
                android.app.ActivityManagerInternal r0 = r0.mAmInternal
                r1 = 1
                boolean r0 = r0.isUserRunning(r6, r1)
                if (r0 != 0) goto L_0x000c
                return
            L_0x000c:
                com.android.server.wm.ActivityTaskManagerService r0 = com.android.server.wm.ActivityTaskManagerService.this
                com.android.server.wm.WindowManagerGlobalLock r0 = r0.mGlobalLock
                monitor-enter(r0)
                com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x0046 }
                com.android.server.wm.ActivityTaskManagerService r1 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x0046 }
                android.util.SparseArray<android.util.ArrayMap<java.lang.String, java.lang.Integer>> r1 = r1.mAllowAppSwitchUids     // Catch:{ all -> 0x0046 }
                java.lang.Object r1 = r1.get(r6)     // Catch:{ all -> 0x0046 }
                android.util.ArrayMap r1 = (android.util.ArrayMap) r1     // Catch:{ all -> 0x0046 }
                if (r1 != 0) goto L_0x0034
                if (r5 >= 0) goto L_0x0027
                monitor-exit(r0)     // Catch:{ all -> 0x0046 }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                return
            L_0x0027:
                android.util.ArrayMap r2 = new android.util.ArrayMap     // Catch:{ all -> 0x0046 }
                r2.<init>()     // Catch:{ all -> 0x0046 }
                r1 = r2
                com.android.server.wm.ActivityTaskManagerService r2 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x0046 }
                android.util.SparseArray<android.util.ArrayMap<java.lang.String, java.lang.Integer>> r2 = r2.mAllowAppSwitchUids     // Catch:{ all -> 0x0046 }
                r2.put(r6, r1)     // Catch:{ all -> 0x0046 }
            L_0x0034:
                if (r5 >= 0) goto L_0x003a
                r1.remove(r4)     // Catch:{ all -> 0x0046 }
                goto L_0x0041
            L_0x003a:
                java.lang.Integer r2 = java.lang.Integer.valueOf(r5)     // Catch:{ all -> 0x0046 }
                r1.put(r4, r2)     // Catch:{ all -> 0x0046 }
            L_0x0041:
                monitor-exit(r0)     // Catch:{ all -> 0x0046 }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                return
            L_0x0046:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0046 }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityTaskManagerService.LocalService.setAllowAppSwitches(java.lang.String, int, int):void");
        }

        public void onUserStopped(int userId) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.getRecentTasks().unloadUserDataFromMemoryLocked(userId);
                    ActivityTaskManagerService.this.mAllowAppSwitchUids.remove(userId);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public boolean isGetTasksAllowed(String caller, int callingPid, int callingUid) {
            boolean isGetTasksAllowed;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    isGetTasksAllowed = ActivityTaskManagerService.this.isGetTasksAllowed(caller, callingPid, callingUid);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return isGetTasksAllowed;
        }

        public void onProcessAdded(WindowProcessController proc) {
            synchronized (ActivityTaskManagerService.this.mGlobalLockWithoutBoost) {
                ActivityTaskManagerService.this.mProcessNames.put(proc.mName, proc.mUid, proc);
            }
        }

        public void onProcessRemoved(String name, int uid) {
            synchronized (ActivityTaskManagerService.this.mGlobalLockWithoutBoost) {
                ActivityTaskManagerService.this.mProcessNames.remove(name, uid);
            }
        }

        public void onCleanUpApplicationRecord(WindowProcessController proc) {
            synchronized (ActivityTaskManagerService.this.mGlobalLockWithoutBoost) {
                if (proc == ActivityTaskManagerService.this.mHomeProcess) {
                    ActivityTaskManagerService.this.mHomeProcess = null;
                }
                if (proc == ActivityTaskManagerService.this.mPreviousProcess) {
                    ActivityTaskManagerService.this.mPreviousProcess = null;
                }
            }
        }

        public int getTopProcessState() {
            int i;
            synchronized (ActivityTaskManagerService.this.mGlobalLockWithoutBoost) {
                i = ActivityTaskManagerService.this.mTopProcessState;
            }
            return i;
        }

        public boolean isHeavyWeightProcess(WindowProcessController proc) {
            boolean z;
            synchronized (ActivityTaskManagerService.this.mGlobalLockWithoutBoost) {
                z = proc == ActivityTaskManagerService.this.mHeavyWeightProcess;
            }
            return z;
        }

        public void clearHeavyWeightProcessIfEquals(WindowProcessController proc) {
            synchronized (ActivityTaskManagerService.this.mGlobalLockWithoutBoost) {
                ActivityTaskManagerService.this.clearHeavyWeightProcessIfEquals(proc);
            }
        }

        public void finishHeavyWeightApp() {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    if (ActivityTaskManagerService.this.mHeavyWeightProcess != null) {
                        ActivityTaskManagerService.this.mHeavyWeightProcess.finishActivities();
                    }
                    ActivityTaskManagerService.this.clearHeavyWeightProcessIfEquals(ActivityTaskManagerService.this.mHeavyWeightProcess);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public boolean isSleeping() {
            boolean isSleepingLocked;
            synchronized (ActivityTaskManagerService.this.mGlobalLockWithoutBoost) {
                isSleepingLocked = ActivityTaskManagerService.this.isSleepingLocked();
            }
            return isSleepingLocked;
        }

        public boolean isShuttingDown() {
            boolean z;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    z = ActivityTaskManagerService.this.mShuttingDown;
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return z;
        }

        public boolean shuttingDown(boolean booted, int timeout) {
            boolean shutdownLocked;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mShuttingDown = true;
                    ActivityTaskManagerService.this.mRootActivityContainer.prepareForShutdown();
                    ActivityTaskManagerService.this.updateEventDispatchingLocked(booted);
                    ActivityTaskManagerService.this.notifyTaskPersisterLocked((TaskRecord) null, true);
                    shutdownLocked = ActivityTaskManagerService.this.mStackSupervisor.shutdownLocked(timeout);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return shutdownLocked;
        }

        public void enableScreenAfterBoot(boolean booted) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    EventLog.writeEvent(EventLogTags.BOOT_PROGRESS_ENABLE_SCREEN, SystemClock.uptimeMillis());
                    ActivityTaskManagerService.this.mWindowManager.enableScreenAfterBoot();
                    ActivityTaskManagerService.this.updateEventDispatchingLocked(booted);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public boolean showStrictModeViolationDialog() {
            boolean z;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    z = ActivityTaskManagerService.this.mShowDialogs && !ActivityTaskManagerService.this.mSleeping && !ActivityTaskManagerService.this.mShuttingDown;
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return z;
        }

        public void showSystemReadyErrorDialogsIfNeeded() {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    if (AppGlobals.getPackageManager().hasSystemUidErrors()) {
                        Slog.e("ActivityTaskManager", "UIDs on the system are inconsistent, you need to wipe your data partition or your device will be unstable.");
                        ActivityTaskManagerService.this.mUiHandler.post(new Runnable() {
                            public final void run() {
                                ActivityTaskManagerService.LocalService.this.lambda$showSystemReadyErrorDialogsIfNeeded$0$ActivityTaskManagerService$LocalService();
                            }
                        });
                    }
                } catch (RemoteException e) {
                }
                try {
                    if (!Build.isBuildConsistent()) {
                        Slog.e("ActivityTaskManager", "Build fingerprint is not consistent, warning user");
                        ActivityTaskManagerService.this.mUiHandler.post(new Runnable() {
                            public final void run() {
                                ActivityTaskManagerService.LocalService.this.lambda$showSystemReadyErrorDialogsIfNeeded$1$ActivityTaskManagerService$LocalService();
                            }
                        });
                    }
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public /* synthetic */ void lambda$showSystemReadyErrorDialogsIfNeeded$0$ActivityTaskManagerService$LocalService() {
            if (ActivityTaskManagerService.this.mShowDialogs) {
                BaseErrorDialog d = new BaseErrorDialog(ActivityTaskManagerService.this.mUiContext);
                d.getWindow().setType(2010);
                d.setCancelable(false);
                d.setTitle(ActivityTaskManagerService.this.mUiContext.getText(17039504));
                d.setMessage(ActivityTaskManagerService.this.mUiContext.getText(17041217));
                d.setButton(-1, ActivityTaskManagerService.this.mUiContext.getText(17039370), ActivityTaskManagerService.this.mUiHandler.obtainMessage(1, d));
                d.show();
            }
        }

        public /* synthetic */ void lambda$showSystemReadyErrorDialogsIfNeeded$1$ActivityTaskManagerService$LocalService() {
            if (ActivityTaskManagerService.this.mShowDialogs) {
                BaseErrorDialog d = new BaseErrorDialog(ActivityTaskManagerService.this.mUiContext);
                d.getWindow().setType(2010);
                d.setCancelable(false);
                d.setTitle(ActivityTaskManagerService.this.mUiContext.getText(17039504));
                d.setMessage(ActivityTaskManagerService.this.mUiContext.getText(17041216));
                d.setButton(-1, ActivityTaskManagerService.this.mUiContext.getText(17039370), ActivityTaskManagerService.this.mUiHandler.obtainMessage(1, d));
                d.show();
            }
        }

        public void onProcessMapped(int pid, WindowProcessController proc) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mProcessMap.put(pid, proc);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public void onProcessUnMapped(int pid) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mProcessMap.remove(pid);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public void onPackageDataCleared(String name) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mCompatModePackages.handlePackageDataClearedLocked(name);
                    ActivityTaskManagerService.this.mAppWarnings.onPackageDataCleared(name);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public void onPackageUninstalled(String name) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mAppWarnings.onPackageUninstalled(name);
                    ActivityTaskManagerService.this.mCompatModePackages.handlePackageUninstalledLocked(name);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public void onPackageAdded(String name, boolean replacing) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mCompatModePackages.handlePackageAddedLocked(name, replacing);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public void onPackageReplaced(ApplicationInfo aInfo) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mRootActivityContainer.updateActivityApplicationInfo(aInfo);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public CompatibilityInfo compatibilityInfoForPackage(ApplicationInfo ai) {
            CompatibilityInfo compatibilityInfoForPackageLocked;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    compatibilityInfoForPackageLocked = ActivityTaskManagerService.this.compatibilityInfoForPackageLocked(ai);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return compatibilityInfoForPackageLocked;
        }

        public void onImeWindowSetOnDisplay(int pid, int displayId) {
            if (!InputMethodSystemProperty.MULTI_CLIENT_IME_ENABLED && pid != ActivityManagerService.MY_PID && pid >= 0) {
                synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                    try {
                        WindowManagerService.boostPriorityForLockedSection();
                        ActivityDisplay activityDisplay = ActivityTaskManagerService.this.mRootActivityContainer.getActivityDisplay(displayId);
                        if (activityDisplay == null) {
                            WindowManagerService.resetPriorityAfterLockedSection();
                            return;
                        }
                        WindowProcessController process = ActivityTaskManagerService.this.mProcessMap.getProcess(pid);
                        if (process == null) {
                            WindowManagerService.resetPriorityAfterLockedSection();
                            return;
                        }
                        process.registerDisplayConfigurationListenerLocked(activityDisplay);
                        WindowManagerService.resetPriorityAfterLockedSection();
                    } catch (Throwable th) {
                        while (true) {
                            WindowManagerService.resetPriorityAfterLockedSection();
                            throw th;
                        }
                    }
                }
            }
        }

        public void sendActivityResult(int callingUid, IBinder activityToken, String resultWho, int requestCode, int resultCode, Intent data) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityRecord r = ActivityRecord.isInStackLocked(activityToken);
                    if (!(r == null || r.getActivityStack() == null)) {
                        r.getActivityStack().sendActivityResultLocked(callingUid, r, resultWho, requestCode, resultCode, data);
                    }
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public void clearPendingResultForActivity(IBinder activityToken, WeakReference<PendingIntentRecord> pir) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityRecord r = ActivityRecord.isInStackLocked(activityToken);
                    if (!(r == null || r.pendingResults == null)) {
                        r.pendingResults.remove(pir);
                    }
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public ActivityTaskManagerInternal.ActivityTokens getTopActivityForTask(int taskId) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    TaskRecord taskRecord = ActivityTaskManagerService.this.mRootActivityContainer.anyTaskForId(taskId);
                    if (taskRecord == null) {
                        Slog.w("ActivityTaskManager", "getApplicationThreadForTopActivity failed: Requested task not found");
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return null;
                    }
                    ActivityRecord activity = taskRecord.getTopActivity();
                    if (activity == null) {
                        Slog.w("ActivityTaskManager", "getApplicationThreadForTopActivity failed: Requested activity not found");
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return null;
                    } else if (!activity.attachedToProcess()) {
                        Slog.w("ActivityTaskManager", "getApplicationThreadForTopActivity failed: No process for " + activity);
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return null;
                    } else {
                        ActivityTaskManagerInternal.ActivityTokens activityTokens = new ActivityTaskManagerInternal.ActivityTokens(activity.appToken, activity.assistToken, activity.app.getThread());
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return activityTokens;
                    }
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
        }

        public IIntentSender getIntentSender(int type, String packageName, int callingUid, int userId, IBinder token, String resultWho, int requestCode, Intent[] intents, String[] resolvedTypes, int flags, Bundle bOptions) {
            IIntentSender intentSenderLocked;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    intentSenderLocked = ActivityTaskManagerService.this.getIntentSenderLocked(type, packageName, callingUid, userId, token, resultWho, requestCode, intents, resolvedTypes, flags, bOptions);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return intentSenderLocked;
        }

        public ActivityServiceConnectionsHolder getServiceConnectionsHolder(IBinder token) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityRecord r = ActivityRecord.isInStackLocked(token);
                    if (r == null) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return null;
                    }
                    if (r.mServiceConnectionsHolder == null) {
                        r.mServiceConnectionsHolder = new ActivityServiceConnectionsHolder(ActivityTaskManagerService.this, r);
                    }
                    ActivityServiceConnectionsHolder activityServiceConnectionsHolder = r.mServiceConnectionsHolder;
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return activityServiceConnectionsHolder;
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
        }

        public ActivityRecord getFocusedStackTopRunningActivity() {
            ActivityRecord topRunning = null;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityStack focusedStack = ActivityTaskManagerService.this.mRootActivityContainer.getTopDisplayFocusedStack();
                    if (focusedStack != null) {
                        topRunning = focusedStack.topRunningActivityLocked();
                    }
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return topRunning;
        }

        public Intent getHomeIntent() {
            Intent homeIntent;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    homeIntent = ActivityTaskManagerService.this.getHomeIntent();
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return homeIntent;
        }

        public boolean startHomeActivity(int userId, String reason) {
            boolean startHomeOnDisplay;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    startHomeOnDisplay = ActivityTaskManagerService.this.mRootActivityContainer.startHomeOnDisplay(userId, reason, 0);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return startHomeOnDisplay;
        }

        public boolean startHomeOnDisplay(int userId, String reason, int displayId, boolean allowInstrumenting, boolean fromHomeKey) {
            boolean startHomeOnDisplay;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    startHomeOnDisplay = ActivityTaskManagerService.this.mRootActivityContainer.startHomeOnDisplay(userId, reason, displayId, allowInstrumenting, fromHomeKey);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return startHomeOnDisplay;
        }

        public boolean startHomeOnAllDisplays(int userId, String reason) {
            boolean startHomeOnAllDisplays;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    startHomeOnAllDisplays = ActivityTaskManagerService.this.mRootActivityContainer.startHomeOnAllDisplays(userId, reason);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return startHomeOnAllDisplays;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:21:0x003e, code lost:
            return r2;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean isFactoryTestProcess(com.android.server.wm.WindowProcessController r6) {
            /*
                r5 = this;
                com.android.server.wm.ActivityTaskManagerService r0 = com.android.server.wm.ActivityTaskManagerService.this
                java.lang.Object r0 = r0.mGlobalLockWithoutBoost
                monitor-enter(r0)
                com.android.server.wm.ActivityTaskManagerService r1 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x003f }
                int r1 = r1.mFactoryTest     // Catch:{ all -> 0x003f }
                r2 = 0
                if (r1 != 0) goto L_0x000e
                monitor-exit(r0)     // Catch:{ all -> 0x003f }
                return r2
            L_0x000e:
                com.android.server.wm.ActivityTaskManagerService r1 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x003f }
                int r1 = r1.mFactoryTest     // Catch:{ all -> 0x003f }
                r3 = 1
                if (r1 != r3) goto L_0x002d
                com.android.server.wm.ActivityTaskManagerService r1 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x003f }
                android.content.ComponentName r1 = r1.mTopComponent     // Catch:{ all -> 0x003f }
                if (r1 == 0) goto L_0x002d
                java.lang.String r1 = r6.mName     // Catch:{ all -> 0x003f }
                com.android.server.wm.ActivityTaskManagerService r4 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x003f }
                android.content.ComponentName r4 = r4.mTopComponent     // Catch:{ all -> 0x003f }
                java.lang.String r4 = r4.getPackageName()     // Catch:{ all -> 0x003f }
                boolean r1 = r1.equals(r4)     // Catch:{ all -> 0x003f }
                if (r1 == 0) goto L_0x002d
                monitor-exit(r0)     // Catch:{ all -> 0x003f }
                return r3
            L_0x002d:
                com.android.server.wm.ActivityTaskManagerService r1 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x003f }
                int r1 = r1.mFactoryTest     // Catch:{ all -> 0x003f }
                r4 = 2
                if (r1 != r4) goto L_0x003d
                android.content.pm.ApplicationInfo r1 = r6.mInfo     // Catch:{ all -> 0x003f }
                int r1 = r1.flags     // Catch:{ all -> 0x003f }
                r1 = r1 & 16
                if (r1 == 0) goto L_0x003d
                r2 = r3
            L_0x003d:
                monitor-exit(r0)     // Catch:{ all -> 0x003f }
                return r2
            L_0x003f:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x003f }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityTaskManagerService.LocalService.isFactoryTestProcess(com.android.server.wm.WindowProcessController):boolean");
        }

        public void updateTopComponentForFactoryTest() {
            CharSequence errorMsg;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    if (ActivityTaskManagerService.this.mFactoryTest != 1) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return;
                    }
                    ResolveInfo ri = ActivityTaskManagerService.this.mContext.getPackageManager().resolveActivity(new Intent("android.intent.action.FACTORY_TEST"), 1024);
                    if (ri != null) {
                        ActivityInfo ai = ri.activityInfo;
                        ApplicationInfo app = ai.applicationInfo;
                        if ((1 & app.flags) != 0) {
                            ActivityTaskManagerService.this.mTopAction = "android.intent.action.FACTORY_TEST";
                            ActivityTaskManagerService.this.mTopData = null;
                            ActivityTaskManagerService.this.mTopComponent = new ComponentName(app.packageName, ai.name);
                            errorMsg = null;
                        } else {
                            errorMsg = ActivityTaskManagerService.this.mContext.getResources().getText(17040049);
                        }
                    } else {
                        errorMsg = ActivityTaskManagerService.this.mContext.getResources().getText(17040048);
                    }
                    if (errorMsg == null) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return;
                    }
                    ActivityTaskManagerService.this.mTopAction = null;
                    ActivityTaskManagerService.this.mTopData = null;
                    ActivityTaskManagerService.this.mTopComponent = null;
                    ActivityTaskManagerService.this.mUiHandler.post(new Runnable(errorMsg) {
                        private final /* synthetic */ CharSequence f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            ActivityTaskManagerService.LocalService.this.lambda$updateTopComponentForFactoryTest$2$ActivityTaskManagerService$LocalService(this.f$1);
                        }
                    });
                    WindowManagerService.resetPriorityAfterLockedSection();
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
        }

        /* JADX WARNING: type inference failed for: r0v0, types: [com.android.server.wm.FactoryErrorDialog, android.app.Dialog] */
        public /* synthetic */ void lambda$updateTopComponentForFactoryTest$2$ActivityTaskManagerService$LocalService(CharSequence errorMsg) {
            new FactoryErrorDialog(ActivityTaskManagerService.this.mUiContext, errorMsg).show();
            ActivityTaskManagerService.this.mAmInternal.ensureBootCompleted();
        }

        /* Debug info: failed to restart local var, previous not found, register: 5 */
        /* JADX INFO: finally extract failed */
        public void handleAppDied(WindowProcessController wpc, boolean restarting, Runnable finishInstrumentationCallback) {
            synchronized (ActivityTaskManagerService.this.mGlobalLockWithoutBoost) {
                boolean hasVisibleActivities = ActivityTaskManagerService.this.mRootActivityContainer.handleAppDied(wpc);
                wpc.clearRecentTasks();
                wpc.clearActivities();
                if (wpc.isInstrumenting()) {
                    finishInstrumentationCallback.run();
                }
                if (!restarting && hasVisibleActivities) {
                    ActivityTaskManagerService.this.mWindowManager.deferSurfaceLayout();
                    try {
                        if (!ActivityTaskManagerService.this.mRootActivityContainer.resumeFocusedStacksTopActivities()) {
                            ActivityTaskManagerService.this.mRootActivityContainer.ensureActivitiesVisible((ActivityRecord) null, 0, false);
                        }
                        ActivityTaskManagerService.this.mWindowManager.continueSurfaceLayout();
                    } catch (Throwable th) {
                        ActivityTaskManagerService.this.mWindowManager.continueSurfaceLayout();
                        throw th;
                    }
                }
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 9 */
        public void closeSystemDialogs(String reason) {
            ActivityTaskManagerService.this.enforceNotIsolatedCaller("closeSystemDialogs");
            int pid = Binder.getCallingPid();
            int uid = Binder.getCallingUid();
            long origId = Binder.clearCallingIdentity();
            try {
                synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                    WindowManagerService.boostPriorityForLockedSection();
                    if (uid >= 10000) {
                        WindowProcessController proc = ActivityTaskManagerService.this.mProcessMap.getProcess(pid);
                        if (!proc.isPerceptible()) {
                            Slog.w("ActivityTaskManager", "Ignoring closeSystemDialogs " + reason + " from background process " + proc);
                            WindowManagerService.resetPriorityAfterLockedSection();
                            Binder.restoreCallingIdentity(origId);
                            return;
                        }
                    }
                    ActivityTaskManagerService.this.mWindowManager.closeSystemDialogs(reason);
                    ActivityTaskManagerService.this.mRootActivityContainer.closeSystemDialogs();
                    WindowManagerService.resetPriorityAfterLockedSection();
                    ActivityTaskManagerService.this.mAmInternal.broadcastCloseSystemDialogs(reason);
                    Binder.restoreCallingIdentity(origId);
                }
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(origId);
                throw th;
            }
        }

        public void cleanupDisabledPackageComponents(String packageName, Set<String> disabledClasses, int userId, boolean booted) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    if (ActivityTaskManagerService.this.mRootActivityContainer.finishDisabledPackageActivities(packageName, disabledClasses, true, false, userId) && booted) {
                        ActivityTaskManagerService.this.mRootActivityContainer.resumeFocusedStacksTopActivities();
                        ActivityTaskManagerService.this.mStackSupervisor.scheduleIdleLocked();
                    }
                    ActivityTaskManagerService.this.getRecentTasks().cleanupDisabledPackageTasksLocked(packageName, disabledClasses, userId);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public boolean onForceStopPackage(String packageName, boolean doit, boolean evenPersistent, int userId) {
            boolean didSomething;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    didSomething = ActivityTaskManagerService.this.getActivityStartController().clearPendingActivityLaunches(packageName) | ActivityTaskManagerService.this.mRootActivityContainer.finishDisabledPackageActivities(packageName, (Set<String>) null, doit, evenPersistent, userId);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return didSomething;
        }

        public void resumeTopActivities(boolean scheduleIdle) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mRootActivityContainer.resumeFocusedStacksTopActivities();
                    if (scheduleIdle) {
                        ActivityTaskManagerService.this.mStackSupervisor.scheduleIdleLocked();
                    }
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public void preBindApplication(WindowProcessController wpc) {
            synchronized (ActivityTaskManagerService.this.mGlobalLockWithoutBoost) {
                ActivityTaskManagerService.this.mStackSupervisor.getActivityMetricsLogger().notifyBindApplication(wpc.mInfo);
            }
        }

        public boolean attachApplication(WindowProcessController wpc) throws RemoteException {
            boolean attachApplication;
            synchronized (ActivityTaskManagerService.this.mGlobalLockWithoutBoost) {
                attachApplication = ActivityTaskManagerService.this.mRootActivityContainer.attachApplication(wpc);
            }
            return attachApplication;
        }

        /* Debug info: failed to restart local var, previous not found, register: 4 */
        public void notifyLockedProfile(int userId, int currentUserId) {
            long ident;
            try {
                if (AppGlobals.getPackageManager().isUidPrivileged(Binder.getCallingUid())) {
                    synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                        try {
                            WindowManagerService.boostPriorityForLockedSection();
                            ident = Binder.clearCallingIdentity();
                            if (ActivityTaskManagerService.this.mAmInternal.shouldConfirmCredentials(userId)) {
                                if (ActivityTaskManagerService.this.mKeyguardController.isKeyguardLocked()) {
                                    startHomeActivity(currentUserId, "notifyLockedProfile");
                                }
                                ActivityTaskManagerService.this.mRootActivityContainer.lockAllProfileTasks(userId);
                            }
                            Binder.restoreCallingIdentity(ident);
                        } catch (Throwable th) {
                            WindowManagerService.resetPriorityAfterLockedSection();
                            throw th;
                        }
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                throw new SecurityException("Only privileged app can call notifyLockedProfile");
            } catch (RemoteException ex) {
                throw new SecurityException("Fail to check is caller a privileged app", ex);
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 7 */
        public void startConfirmDeviceCredentialIntent(Intent intent, Bundle options) {
            long ident;
            ActivityTaskManagerService.this.mAmInternal.enforceCallingPermission("android.permission.MANAGE_ACTIVITY_STACKS", "startConfirmDeviceCredentialIntent");
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ident = Binder.clearCallingIdentity();
                    intent.addFlags(276824064);
                    ActivityTaskManagerService.this.mContext.startActivityAsUser(intent, (options != null ? new ActivityOptions(options) : ActivityOptions.makeBasic()).toBundle(), UserHandle.CURRENT);
                    Binder.restoreCallingIdentity(ident);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public void writeActivitiesToProto(ProtoOutputStream proto) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mRootActivityContainer.writeToProto(proto, 1146756268033L, 0);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public void saveANRState(String reason) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    StringWriter sw = new StringWriter();
                    FastPrintWriter fastPrintWriter = new FastPrintWriter(sw, false, 1024);
                    fastPrintWriter.println("  ANR time: " + DateFormat.getDateTimeInstance().format(new Date()));
                    if (reason != null) {
                        fastPrintWriter.println("  Reason: " + reason);
                    }
                    fastPrintWriter.println();
                    ActivityTaskManagerService.this.getActivityStartController().dump(fastPrintWriter, "  ", (String) null);
                    fastPrintWriter.println();
                    fastPrintWriter.println("-------------------------------------------------------------------------------");
                    ActivityTaskManagerService.this.dumpActivitiesLocked((FileDescriptor) null, fastPrintWriter, (String[]) null, 0, true, false, (String) null, "");
                    fastPrintWriter.println();
                    fastPrintWriter.close();
                    ActivityTaskManagerService.this.mLastANRState = sw.toString();
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public void clearSavedANRState() {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mLastANRState = null;
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        /* JADX WARNING: Code restructure failed: missing block: B:36:0x00a6, code lost:
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:37:0x00a9, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void dump(java.lang.String r16, java.io.FileDescriptor r17, java.io.PrintWriter r18, java.lang.String[] r19, int r20, boolean r21, boolean r22, java.lang.String r23) {
            /*
                r15 = this;
                r1 = r15
                r2 = r16
                r11 = r18
                r12 = r23
                com.android.server.wm.ActivityTaskManagerService r0 = com.android.server.wm.ActivityTaskManagerService.this
                com.android.server.wm.WindowManagerGlobalLock r13 = r0.mGlobalLock
                monitor-enter(r13)
                com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x00aa }
                java.lang.String r0 = "activities"
                boolean r0 = r0.equals(r2)     // Catch:{ all -> 0x00aa }
                if (r0 != 0) goto L_0x0090
                java.lang.String r0 = "a"
                boolean r0 = r0.equals(r2)     // Catch:{ all -> 0x00aa }
                if (r0 == 0) goto L_0x0023
                r14 = r21
                goto L_0x0092
            L_0x0023:
                java.lang.String r0 = "lastanr"
                boolean r0 = r0.equals(r2)     // Catch:{ all -> 0x00aa }
                if (r0 == 0) goto L_0x0034
                com.android.server.wm.ActivityTaskManagerService r0 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x00aa }
                r0.dumpLastANRLocked(r11)     // Catch:{ all -> 0x00aa }
                r14 = r21
                goto L_0x00a5
            L_0x0034:
                java.lang.String r0 = "lastanr-traces"
                boolean r0 = r0.equals(r2)     // Catch:{ all -> 0x00aa }
                if (r0 == 0) goto L_0x0045
                com.android.server.wm.ActivityTaskManagerService r0 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x00aa }
                r0.dumpLastANRTracesLocked(r11)     // Catch:{ all -> 0x00aa }
                r14 = r21
                goto L_0x00a5
            L_0x0045:
                java.lang.String r0 = "starter"
                boolean r0 = r0.equals(r2)     // Catch:{ all -> 0x00aa }
                if (r0 == 0) goto L_0x0055
                com.android.server.wm.ActivityTaskManagerService r0 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x00aa }
                r0.dumpActivityStarterLocked(r11, r12)     // Catch:{ all -> 0x00aa }
                r14 = r21
                goto L_0x00a5
            L_0x0055:
                java.lang.String r0 = "containers"
                boolean r0 = r0.equals(r2)     // Catch:{ all -> 0x00aa }
                if (r0 == 0) goto L_0x0065
                com.android.server.wm.ActivityTaskManagerService r0 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x00aa }
                r0.dumpActivityContainersLocked(r11)     // Catch:{ all -> 0x00aa }
                r14 = r21
                goto L_0x00a5
            L_0x0065:
                java.lang.String r0 = "recents"
                boolean r0 = r0.equals(r2)     // Catch:{ all -> 0x00aa }
                if (r0 != 0) goto L_0x0079
                java.lang.String r0 = "r"
                boolean r0 = r0.equals(r2)     // Catch:{ all -> 0x00aa }
                if (r0 == 0) goto L_0x0076
                goto L_0x0079
            L_0x0076:
                r14 = r21
                goto L_0x00a5
            L_0x0079:
                com.android.server.wm.ActivityTaskManagerService r0 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x00aa }
                com.android.server.wm.RecentTasks r0 = r0.getRecentTasks()     // Catch:{ all -> 0x00aa }
                if (r0 == 0) goto L_0x008d
                com.android.server.wm.ActivityTaskManagerService r0 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x00aa }
                com.android.server.wm.RecentTasks r0 = r0.getRecentTasks()     // Catch:{ all -> 0x00aa }
                r14 = r21
                r0.dump(r11, r14, r12)     // Catch:{ all -> 0x00b2 }
                goto L_0x00a5
            L_0x008d:
                r14 = r21
                goto L_0x00a5
            L_0x0090:
                r14 = r21
            L_0x0092:
                com.android.server.wm.ActivityTaskManagerService r3 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x00b2 }
                r4 = r17
                r5 = r18
                r6 = r19
                r7 = r20
                r8 = r21
                r9 = r22
                r10 = r23
                r3.dumpActivitiesLocked(r4, r5, r6, r7, r8, r9, r10)     // Catch:{ all -> 0x00b2 }
            L_0x00a5:
                monitor-exit(r13)     // Catch:{ all -> 0x00b2 }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                return
            L_0x00aa:
                r0 = move-exception
                r14 = r21
            L_0x00ad:
                monitor-exit(r13)     // Catch:{ all -> 0x00b2 }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                throw r0
            L_0x00b2:
                r0 = move-exception
                goto L_0x00ad
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityTaskManagerService.LocalService.dump(java.lang.String, java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[], int, boolean, boolean, java.lang.String):void");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:106:0x0353, code lost:
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:107:0x0356, code lost:
            return r5;
         */
        /* JADX WARNING: Removed duplicated region for block: B:101:0x02f4 A[Catch:{ all -> 0x036d }] */
        /* JADX WARNING: Removed duplicated region for block: B:25:0x0065 A[Catch:{ all -> 0x035b }] */
        /* JADX WARNING: Removed duplicated region for block: B:40:0x00c4 A[Catch:{ all -> 0x035b }] */
        /* JADX WARNING: Removed duplicated region for block: B:43:0x00e3 A[Catch:{ all -> 0x035b }] */
        /* JADX WARNING: Removed duplicated region for block: B:45:0x0108 A[Catch:{ all -> 0x035b }] */
        /* JADX WARNING: Removed duplicated region for block: B:61:0x0191 A[SYNTHETIC, Splitter:B:61:0x0191] */
        /* JADX WARNING: Removed duplicated region for block: B:73:0x0253 A[Catch:{ all -> 0x024e, all -> 0x0357 }] */
        /* JADX WARNING: Removed duplicated region for block: B:76:0x025b A[Catch:{ all -> 0x024e, all -> 0x0357 }] */
        /* JADX WARNING: Removed duplicated region for block: B:79:0x026f A[Catch:{ all -> 0x024e, all -> 0x0357 }] */
        /* JADX WARNING: Removed duplicated region for block: B:99:0x02f0 A[Catch:{ all -> 0x036d }] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean dumpForProcesses(java.io.FileDescriptor r14, java.io.PrintWriter r15, boolean r16, java.lang.String r17, int r18, boolean r19, boolean r20, int r21) {
            /*
                r13 = this;
                r1 = r13
                r2 = r15
                r3 = r17
                com.android.server.wm.ActivityTaskManagerService r0 = com.android.server.wm.ActivityTaskManagerService.this
                com.android.server.wm.WindowManagerGlobalLock r4 = r0.mGlobalLock
                monitor-enter(r4)
                com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x0361 }
                com.android.server.wm.ActivityTaskManagerService r0 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x0361 }
                com.android.server.wm.WindowProcessController r0 = r0.mHomeProcess     // Catch:{ all -> 0x0361 }
                if (r0 == 0) goto L_0x004d
                if (r3 == 0) goto L_0x002a
                com.android.server.wm.ActivityTaskManagerService r0 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x0021 }
                com.android.server.wm.WindowProcessController r0 = r0.mHomeProcess     // Catch:{ all -> 0x0021 }
                android.util.ArraySet<java.lang.String> r0 = r0.mPkgList     // Catch:{ all -> 0x0021 }
                boolean r0 = r0.contains(r3)     // Catch:{ all -> 0x0021 }
                if (r0 == 0) goto L_0x004d
                goto L_0x002a
            L_0x0021:
                r0 = move-exception
                r12 = r18
                r5 = r19
                r6 = r20
                goto L_0x0368
            L_0x002a:
                if (r19 == 0) goto L_0x0032
                r15.println()     // Catch:{ all -> 0x0021 }
                r0 = 0
                r5 = r0
                goto L_0x0034
            L_0x0032:
                r5 = r19
            L_0x0034:
                java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x035b }
                r0.<init>()     // Catch:{ all -> 0x035b }
                java.lang.String r6 = "  mHomeProcess: "
                r0.append(r6)     // Catch:{ all -> 0x035b }
                com.android.server.wm.ActivityTaskManagerService r6 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x035b }
                com.android.server.wm.WindowProcessController r6 = r6.mHomeProcess     // Catch:{ all -> 0x035b }
                r0.append(r6)     // Catch:{ all -> 0x035b }
                java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x035b }
                r15.println(r0)     // Catch:{ all -> 0x035b }
                goto L_0x004f
            L_0x004d:
                r5 = r19
            L_0x004f:
                com.android.server.wm.ActivityTaskManagerService r0 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x035b }
                com.android.server.wm.WindowProcessController r0 = r0.mPreviousProcess     // Catch:{ all -> 0x035b }
                if (r0 == 0) goto L_0x0082
                if (r3 == 0) goto L_0x0063
                com.android.server.wm.ActivityTaskManagerService r0 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x035b }
                com.android.server.wm.WindowProcessController r0 = r0.mPreviousProcess     // Catch:{ all -> 0x035b }
                android.util.ArraySet<java.lang.String> r0 = r0.mPkgList     // Catch:{ all -> 0x035b }
                boolean r0 = r0.contains(r3)     // Catch:{ all -> 0x035b }
                if (r0 == 0) goto L_0x0082
            L_0x0063:
                if (r5 == 0) goto L_0x006a
                r15.println()     // Catch:{ all -> 0x035b }
                r0 = 0
                r5 = r0
            L_0x006a:
                java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x035b }
                r0.<init>()     // Catch:{ all -> 0x035b }
                java.lang.String r6 = "  mPreviousProcess: "
                r0.append(r6)     // Catch:{ all -> 0x035b }
                com.android.server.wm.ActivityTaskManagerService r6 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x035b }
                com.android.server.wm.WindowProcessController r6 = r6.mPreviousProcess     // Catch:{ all -> 0x035b }
                r0.append(r6)     // Catch:{ all -> 0x035b }
                java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x035b }
                r15.println(r0)     // Catch:{ all -> 0x035b }
            L_0x0082:
                if (r16 == 0) goto L_0x00ae
                com.android.server.wm.ActivityTaskManagerService r0 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x035b }
                com.android.server.wm.WindowProcessController r0 = r0.mPreviousProcess     // Catch:{ all -> 0x035b }
                if (r0 == 0) goto L_0x0098
                if (r3 == 0) goto L_0x0098
                com.android.server.wm.ActivityTaskManagerService r0 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x035b }
                com.android.server.wm.WindowProcessController r0 = r0.mPreviousProcess     // Catch:{ all -> 0x035b }
                android.util.ArraySet<java.lang.String> r0 = r0.mPkgList     // Catch:{ all -> 0x035b }
                boolean r0 = r0.contains(r3)     // Catch:{ all -> 0x035b }
                if (r0 == 0) goto L_0x00ae
            L_0x0098:
                java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x035b }
                r6 = 128(0x80, float:1.794E-43)
                r0.<init>(r6)     // Catch:{ all -> 0x035b }
                java.lang.String r6 = "  mPreviousProcessVisibleTime: "
                r0.append(r6)     // Catch:{ all -> 0x035b }
                com.android.server.wm.ActivityTaskManagerService r6 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x035b }
                long r6 = r6.mPreviousProcessVisibleTime     // Catch:{ all -> 0x035b }
                android.util.TimeUtils.formatDuration(r6, r0)     // Catch:{ all -> 0x035b }
                r15.println(r0)     // Catch:{ all -> 0x035b }
            L_0x00ae:
                com.android.server.wm.ActivityTaskManagerService r0 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x035b }
                com.android.server.wm.WindowProcessController r0 = r0.mHeavyWeightProcess     // Catch:{ all -> 0x035b }
                if (r0 == 0) goto L_0x00e1
                if (r3 == 0) goto L_0x00c2
                com.android.server.wm.ActivityTaskManagerService r0 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x035b }
                com.android.server.wm.WindowProcessController r0 = r0.mHeavyWeightProcess     // Catch:{ all -> 0x035b }
                android.util.ArraySet<java.lang.String> r0 = r0.mPkgList     // Catch:{ all -> 0x035b }
                boolean r0 = r0.contains(r3)     // Catch:{ all -> 0x035b }
                if (r0 == 0) goto L_0x00e1
            L_0x00c2:
                if (r5 == 0) goto L_0x00c9
                r15.println()     // Catch:{ all -> 0x035b }
                r0 = 0
                r5 = r0
            L_0x00c9:
                java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x035b }
                r0.<init>()     // Catch:{ all -> 0x035b }
                java.lang.String r6 = "  mHeavyWeightProcess: "
                r0.append(r6)     // Catch:{ all -> 0x035b }
                com.android.server.wm.ActivityTaskManagerService r6 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x035b }
                com.android.server.wm.WindowProcessController r6 = r6.mHeavyWeightProcess     // Catch:{ all -> 0x035b }
                r0.append(r6)     // Catch:{ all -> 0x035b }
                java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x035b }
                r15.println(r0)     // Catch:{ all -> 0x035b }
            L_0x00e1:
                if (r3 != 0) goto L_0x0106
                java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x035b }
                r0.<init>()     // Catch:{ all -> 0x035b }
                java.lang.String r6 = "  mGlobalConfiguration: "
                r0.append(r6)     // Catch:{ all -> 0x035b }
                com.android.server.wm.ActivityTaskManagerService r6 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x035b }
                android.content.res.Configuration r6 = r6.getGlobalConfiguration()     // Catch:{ all -> 0x035b }
                r0.append(r6)     // Catch:{ all -> 0x035b }
                java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x035b }
                r15.println(r0)     // Catch:{ all -> 0x035b }
                com.android.server.wm.ActivityTaskManagerService r0 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x035b }
                com.android.server.wm.RootActivityContainer r0 = r0.mRootActivityContainer     // Catch:{ all -> 0x035b }
                java.lang.String r6 = "  "
                r0.dumpDisplayConfigs(r15, r6)     // Catch:{ all -> 0x035b }
            L_0x0106:
                if (r16 == 0) goto L_0x018f
                if (r3 != 0) goto L_0x0126
                java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x035b }
                r0.<init>()     // Catch:{ all -> 0x035b }
                java.lang.String r6 = "  mConfigWillChange: "
                r0.append(r6)     // Catch:{ all -> 0x035b }
                com.android.server.wm.ActivityTaskManagerService r6 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x035b }
                com.android.server.wm.ActivityStack r6 = r6.getTopDisplayFocusedStack()     // Catch:{ all -> 0x035b }
                boolean r6 = r6.mConfigWillChange     // Catch:{ all -> 0x035b }
                r0.append(r6)     // Catch:{ all -> 0x035b }
                java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x035b }
                r15.println(r0)     // Catch:{ all -> 0x035b }
            L_0x0126:
                com.android.server.wm.ActivityTaskManagerService r0 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x035b }
                com.android.server.wm.CompatModePackages r0 = r0.mCompatModePackages     // Catch:{ all -> 0x035b }
                java.util.HashMap r0 = r0.getPackages()     // Catch:{ all -> 0x035b }
                int r0 = r0.size()     // Catch:{ all -> 0x035b }
                if (r0 <= 0) goto L_0x018f
                r0 = 0
                com.android.server.wm.ActivityTaskManagerService r6 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x035b }
                com.android.server.wm.CompatModePackages r6 = r6.mCompatModePackages     // Catch:{ all -> 0x035b }
                java.util.HashMap r6 = r6.getPackages()     // Catch:{ all -> 0x035b }
                java.util.Set r6 = r6.entrySet()     // Catch:{ all -> 0x035b }
                java.util.Iterator r6 = r6.iterator()     // Catch:{ all -> 0x035b }
            L_0x0145:
                boolean r7 = r6.hasNext()     // Catch:{ all -> 0x035b }
                if (r7 == 0) goto L_0x018f
                java.lang.Object r7 = r6.next()     // Catch:{ all -> 0x035b }
                java.util.Map$Entry r7 = (java.util.Map.Entry) r7     // Catch:{ all -> 0x035b }
                java.lang.Object r8 = r7.getKey()     // Catch:{ all -> 0x035b }
                java.lang.String r8 = (java.lang.String) r8     // Catch:{ all -> 0x035b }
                java.lang.Object r9 = r7.getValue()     // Catch:{ all -> 0x035b }
                java.lang.Integer r9 = (java.lang.Integer) r9     // Catch:{ all -> 0x035b }
                int r9 = r9.intValue()     // Catch:{ all -> 0x035b }
                if (r3 == 0) goto L_0x016a
                boolean r10 = r3.equals(r8)     // Catch:{ all -> 0x035b }
                if (r10 != 0) goto L_0x016a
                goto L_0x0145
            L_0x016a:
                if (r0 != 0) goto L_0x0172
                java.lang.String r10 = "  mScreenCompatPackages:"
                r15.println(r10)     // Catch:{ all -> 0x035b }
                r0 = 1
            L_0x0172:
                java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x035b }
                r10.<init>()     // Catch:{ all -> 0x035b }
                java.lang.String r11 = "    "
                r10.append(r11)     // Catch:{ all -> 0x035b }
                r10.append(r8)     // Catch:{ all -> 0x035b }
                java.lang.String r11 = ": "
                r10.append(r11)     // Catch:{ all -> 0x035b }
                r10.append(r9)     // Catch:{ all -> 0x035b }
                java.lang.String r10 = r10.toString()     // Catch:{ all -> 0x035b }
                r15.println(r10)     // Catch:{ all -> 0x035b }
                goto L_0x0145
            L_0x018f:
                if (r3 != 0) goto L_0x0253
                java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x024e }
                r0.<init>()     // Catch:{ all -> 0x024e }
                java.lang.String r6 = "  mWakefulness="
                r0.append(r6)     // Catch:{ all -> 0x024e }
                java.lang.String r6 = android.os.PowerManagerInternal.wakefulnessToString(r21)     // Catch:{ all -> 0x024e }
                r0.append(r6)     // Catch:{ all -> 0x024e }
                java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x024e }
                r15.println(r0)     // Catch:{ all -> 0x024e }
                java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x024e }
                r0.<init>()     // Catch:{ all -> 0x024e }
                java.lang.String r6 = "  mSleepTokens="
                r0.append(r6)     // Catch:{ all -> 0x024e }
                com.android.server.wm.ActivityTaskManagerService r6 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x024e }
                com.android.server.wm.RootActivityContainer r6 = r6.mRootActivityContainer     // Catch:{ all -> 0x024e }
                java.util.ArrayList<com.android.server.wm.ActivityTaskManagerInternal$SleepToken> r6 = r6.mSleepTokens     // Catch:{ all -> 0x024e }
                r0.append(r6)     // Catch:{ all -> 0x024e }
                java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x024e }
                r15.println(r0)     // Catch:{ all -> 0x024e }
                com.android.server.wm.ActivityTaskManagerService r0 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x024e }
                android.service.voice.IVoiceInteractionSession r0 = r0.mRunningVoice     // Catch:{ all -> 0x024e }
                if (r0 == 0) goto L_0x01f9
                java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x035b }
                r0.<init>()     // Catch:{ all -> 0x035b }
                java.lang.String r6 = "  mRunningVoice="
                r0.append(r6)     // Catch:{ all -> 0x035b }
                com.android.server.wm.ActivityTaskManagerService r6 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x035b }
                android.service.voice.IVoiceInteractionSession r6 = r6.mRunningVoice     // Catch:{ all -> 0x035b }
                r0.append(r6)     // Catch:{ all -> 0x035b }
                java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x035b }
                r15.println(r0)     // Catch:{ all -> 0x035b }
                java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x035b }
                r0.<init>()     // Catch:{ all -> 0x035b }
                java.lang.String r6 = "  mVoiceWakeLock"
                r0.append(r6)     // Catch:{ all -> 0x035b }
                com.android.server.wm.ActivityTaskManagerService r6 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x035b }
                android.os.PowerManager$WakeLock r6 = r6.mVoiceWakeLock     // Catch:{ all -> 0x035b }
                r0.append(r6)     // Catch:{ all -> 0x035b }
                java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x035b }
                r15.println(r0)     // Catch:{ all -> 0x035b }
            L_0x01f9:
                java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x024e }
                r0.<init>()     // Catch:{ all -> 0x024e }
                java.lang.String r6 = "  mSleeping="
                r0.append(r6)     // Catch:{ all -> 0x024e }
                com.android.server.wm.ActivityTaskManagerService r6 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x024e }
                boolean r6 = r6.mSleeping     // Catch:{ all -> 0x024e }
                r0.append(r6)     // Catch:{ all -> 0x024e }
                java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x024e }
                r15.println(r0)     // Catch:{ all -> 0x024e }
                java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x024e }
                r0.<init>()     // Catch:{ all -> 0x024e }
                java.lang.String r6 = "  mShuttingDown="
                r0.append(r6)     // Catch:{ all -> 0x024e }
                com.android.server.wm.ActivityTaskManagerService r6 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x024e }
                boolean r6 = r6.mShuttingDown     // Catch:{ all -> 0x024e }
                r0.append(r6)     // Catch:{ all -> 0x024e }
                java.lang.String r6 = " mTestPssMode="
                r0.append(r6)     // Catch:{ all -> 0x024e }
                r6 = r20
                r0.append(r6)     // Catch:{ all -> 0x0357 }
                java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0357 }
                r15.println(r0)     // Catch:{ all -> 0x0357 }
                java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0357 }
                r0.<init>()     // Catch:{ all -> 0x0357 }
                java.lang.String r7 = "  mVrController="
                r0.append(r7)     // Catch:{ all -> 0x0357 }
                com.android.server.wm.ActivityTaskManagerService r7 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x0357 }
                com.android.server.wm.VrController r7 = r7.mVrController     // Catch:{ all -> 0x0357 }
                r0.append(r7)     // Catch:{ all -> 0x0357 }
                java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0357 }
                r15.println(r0)     // Catch:{ all -> 0x0357 }
                goto L_0x0255
            L_0x024e:
                r0 = move-exception
                r6 = r20
                goto L_0x0358
            L_0x0253:
                r6 = r20
            L_0x0255:
                com.android.server.wm.ActivityTaskManagerService r0 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x0357 }
                com.android.server.am.AppTimeTracker r0 = r0.mCurAppTimeTracker     // Catch:{ all -> 0x0357 }
                if (r0 == 0) goto L_0x0265
                com.android.server.wm.ActivityTaskManagerService r0 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x0357 }
                com.android.server.am.AppTimeTracker r0 = r0.mCurAppTimeTracker     // Catch:{ all -> 0x0357 }
                java.lang.String r7 = "  "
                r8 = 1
                r0.dumpWithHeader(r15, r7, r8)     // Catch:{ all -> 0x0357 }
            L_0x0265:
                com.android.server.wm.ActivityTaskManagerService r0 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x0357 }
                android.util.SparseArray<android.util.ArrayMap<java.lang.String, java.lang.Integer>> r0 = r0.mAllowAppSwitchUids     // Catch:{ all -> 0x0357 }
                int r0 = r0.size()     // Catch:{ all -> 0x0357 }
                if (r0 <= 0) goto L_0x02f0
                r0 = 0
                r7 = 0
                r8 = r7
            L_0x0272:
                com.android.server.wm.ActivityTaskManagerService r9 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x0357 }
                android.util.SparseArray<android.util.ArrayMap<java.lang.String, java.lang.Integer>> r9 = r9.mAllowAppSwitchUids     // Catch:{ all -> 0x0357 }
                int r9 = r9.size()     // Catch:{ all -> 0x0357 }
                if (r8 >= r9) goto L_0x02ed
                com.android.server.wm.ActivityTaskManagerService r9 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x0357 }
                android.util.SparseArray<android.util.ArrayMap<java.lang.String, java.lang.Integer>> r9 = r9.mAllowAppSwitchUids     // Catch:{ all -> 0x0357 }
                java.lang.Object r9 = r9.valueAt(r8)     // Catch:{ all -> 0x0357 }
                android.util.ArrayMap r9 = (android.util.ArrayMap) r9     // Catch:{ all -> 0x0357 }
                r10 = r7
            L_0x0287:
                int r11 = r9.size()     // Catch:{ all -> 0x0357 }
                if (r10 >= r11) goto L_0x02e8
                if (r3 == 0) goto L_0x02a2
                java.lang.Object r11 = r9.valueAt(r10)     // Catch:{ all -> 0x0357 }
                java.lang.Integer r11 = (java.lang.Integer) r11     // Catch:{ all -> 0x0357 }
                int r11 = r11.intValue()     // Catch:{ all -> 0x0357 }
                int r11 = android.os.UserHandle.getAppId(r11)     // Catch:{ all -> 0x0357 }
                r12 = r18
                if (r11 != r12) goto L_0x02e5
                goto L_0x02a4
            L_0x02a2:
                r12 = r18
            L_0x02a4:
                if (r5 == 0) goto L_0x02aa
                r15.println()     // Catch:{ all -> 0x036d }
                r5 = 0
            L_0x02aa:
                if (r0 != 0) goto L_0x02b2
                java.lang.String r11 = "  mAllowAppSwitchUids:"
                r15.println(r11)     // Catch:{ all -> 0x036d }
                r0 = 1
            L_0x02b2:
                java.lang.String r11 = "    User "
                r15.print(r11)     // Catch:{ all -> 0x036d }
                com.android.server.wm.ActivityTaskManagerService r11 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x036d }
                android.util.SparseArray<android.util.ArrayMap<java.lang.String, java.lang.Integer>> r11 = r11.mAllowAppSwitchUids     // Catch:{ all -> 0x036d }
                int r11 = r11.keyAt(r8)     // Catch:{ all -> 0x036d }
                r15.print(r11)     // Catch:{ all -> 0x036d }
                java.lang.String r11 = ": Type "
                r15.print(r11)     // Catch:{ all -> 0x036d }
                java.lang.Object r11 = r9.keyAt(r10)     // Catch:{ all -> 0x036d }
                java.lang.String r11 = (java.lang.String) r11     // Catch:{ all -> 0x036d }
                r15.print(r11)     // Catch:{ all -> 0x036d }
                java.lang.String r11 = " = "
                r15.print(r11)     // Catch:{ all -> 0x036d }
                java.lang.Object r11 = r9.valueAt(r10)     // Catch:{ all -> 0x036d }
                java.lang.Integer r11 = (java.lang.Integer) r11     // Catch:{ all -> 0x036d }
                int r11 = r11.intValue()     // Catch:{ all -> 0x036d }
                android.os.UserHandle.formatUid(r15, r11)     // Catch:{ all -> 0x036d }
                r15.println()     // Catch:{ all -> 0x036d }
            L_0x02e5:
                int r10 = r10 + 1
                goto L_0x0287
            L_0x02e8:
                r12 = r18
                int r8 = r8 + 1
                goto L_0x0272
            L_0x02ed:
                r12 = r18
                goto L_0x02f2
            L_0x02f0:
                r12 = r18
            L_0x02f2:
                if (r3 != 0) goto L_0x0352
                com.android.server.wm.ActivityTaskManagerService r0 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x036d }
                android.app.IActivityController r0 = r0.mController     // Catch:{ all -> 0x036d }
                if (r0 == 0) goto L_0x031e
                java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x036d }
                r0.<init>()     // Catch:{ all -> 0x036d }
                java.lang.String r7 = "  mController="
                r0.append(r7)     // Catch:{ all -> 0x036d }
                com.android.server.wm.ActivityTaskManagerService r7 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x036d }
                android.app.IActivityController r7 = r7.mController     // Catch:{ all -> 0x036d }
                r0.append(r7)     // Catch:{ all -> 0x036d }
                java.lang.String r7 = " mControllerIsAMonkey="
                r0.append(r7)     // Catch:{ all -> 0x036d }
                com.android.server.wm.ActivityTaskManagerService r7 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x036d }
                boolean r7 = r7.mControllerIsAMonkey     // Catch:{ all -> 0x036d }
                r0.append(r7)     // Catch:{ all -> 0x036d }
                java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x036d }
                r15.println(r0)     // Catch:{ all -> 0x036d }
            L_0x031e:
                java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x036d }
                r0.<init>()     // Catch:{ all -> 0x036d }
                java.lang.String r7 = "  mGoingToSleepWakeLock="
                r0.append(r7)     // Catch:{ all -> 0x036d }
                com.android.server.wm.ActivityTaskManagerService r7 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x036d }
                com.android.server.wm.ActivityStackSupervisor r7 = r7.mStackSupervisor     // Catch:{ all -> 0x036d }
                android.os.PowerManager$WakeLock r7 = r7.mGoingToSleepWakeLock     // Catch:{ all -> 0x036d }
                r0.append(r7)     // Catch:{ all -> 0x036d }
                java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x036d }
                r15.println(r0)     // Catch:{ all -> 0x036d }
                java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x036d }
                r0.<init>()     // Catch:{ all -> 0x036d }
                java.lang.String r7 = "  mLaunchingActivityWakeLock="
                r0.append(r7)     // Catch:{ all -> 0x036d }
                com.android.server.wm.ActivityTaskManagerService r7 = com.android.server.wm.ActivityTaskManagerService.this     // Catch:{ all -> 0x036d }
                com.android.server.wm.ActivityStackSupervisor r7 = r7.mStackSupervisor     // Catch:{ all -> 0x036d }
                android.os.PowerManager$WakeLock r7 = r7.mLaunchingActivityWakeLock     // Catch:{ all -> 0x036d }
                r0.append(r7)     // Catch:{ all -> 0x036d }
                java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x036d }
                r15.println(r0)     // Catch:{ all -> 0x036d }
            L_0x0352:
                monitor-exit(r4)     // Catch:{ all -> 0x036d }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                return r5
            L_0x0357:
                r0 = move-exception
            L_0x0358:
                r12 = r18
                goto L_0x0368
            L_0x035b:
                r0 = move-exception
                r12 = r18
                r6 = r20
                goto L_0x0368
            L_0x0361:
                r0 = move-exception
                r12 = r18
                r6 = r20
                r5 = r19
            L_0x0368:
                monitor-exit(r4)     // Catch:{ all -> 0x036d }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                throw r0
            L_0x036d:
                r0 = move-exception
                goto L_0x0368
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityTaskManagerService.LocalService.dumpForProcesses(java.io.FileDescriptor, java.io.PrintWriter, boolean, java.lang.String, int, boolean, boolean, int):boolean");
        }

        public void writeProcessesToProto(ProtoOutputStream proto, String dumpPackage, int wakeFullness, boolean testPssMode) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    if (dumpPackage == null) {
                        ActivityTaskManagerService.this.getGlobalConfiguration().writeToProto(proto, 1146756268051L);
                        proto.write(1133871366165L, ActivityTaskManagerService.this.getTopDisplayFocusedStack().mConfigWillChange);
                        ActivityTaskManagerService.this.writeSleepStateToProto(proto, wakeFullness, testPssMode);
                        if (ActivityTaskManagerService.this.mRunningVoice != null) {
                            long vrToken = proto.start(1146756268060L);
                            proto.write(1138166333441L, ActivityTaskManagerService.this.mRunningVoice.toString());
                            ActivityTaskManagerService.this.mVoiceWakeLock.writeToProto(proto, 1146756268034L);
                            proto.end(vrToken);
                        }
                        ActivityTaskManagerService.this.mVrController.writeToProto(proto, 1146756268061L);
                        if (ActivityTaskManagerService.this.mController != null) {
                            long token = proto.start(1146756268069L);
                            proto.write(1146756268069L, ActivityTaskManagerService.this.mController.toString());
                            proto.write(1133871366146L, ActivityTaskManagerService.this.mControllerIsAMonkey);
                            proto.end(token);
                        }
                        ActivityTaskManagerService.this.mStackSupervisor.mGoingToSleepWakeLock.writeToProto(proto, 1146756268079L);
                        ActivityTaskManagerService.this.mStackSupervisor.mLaunchingActivityWakeLock.writeToProto(proto, 1146756268080L);
                    }
                    if (ActivityTaskManagerService.this.mHomeProcess != null && (dumpPackage == null || ActivityTaskManagerService.this.mHomeProcess.mPkgList.contains(dumpPackage))) {
                        ActivityTaskManagerService.this.mHomeProcess.writeToProto(proto, 1146756268047L);
                    }
                    if (ActivityTaskManagerService.this.mPreviousProcess != null && (dumpPackage == null || ActivityTaskManagerService.this.mPreviousProcess.mPkgList.contains(dumpPackage))) {
                        ActivityTaskManagerService.this.mPreviousProcess.writeToProto(proto, 1146756268048L);
                        proto.write(1112396529681L, ActivityTaskManagerService.this.mPreviousProcessVisibleTime);
                    }
                    if (ActivityTaskManagerService.this.mHeavyWeightProcess != null && (dumpPackage == null || ActivityTaskManagerService.this.mHeavyWeightProcess.mPkgList.contains(dumpPackage))) {
                        ActivityTaskManagerService.this.mHeavyWeightProcess.writeToProto(proto, 1146756268050L);
                    }
                    for (Map.Entry<String, Integer> entry : ActivityTaskManagerService.this.mCompatModePackages.getPackages().entrySet()) {
                        String pkg = entry.getKey();
                        int mode = entry.getValue().intValue();
                        if (dumpPackage == null || dumpPackage.equals(pkg)) {
                            long compatToken = proto.start(2246267895830L);
                            proto.write(1138166333441L, pkg);
                            proto.write(1120986464258L, mode);
                            proto.end(compatToken);
                        }
                    }
                    if (ActivityTaskManagerService.this.mCurAppTimeTracker != null) {
                        ActivityTaskManagerService.this.mCurAppTimeTracker.writeToProto(proto, 1146756268063L, true);
                    }
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public boolean dumpActivity(FileDescriptor fd, PrintWriter pw, String name, String[] args, int opti, boolean dumpAll, boolean dumpVisibleStacksOnly, boolean dumpFocusedStackOnly) {
            return ActivityTaskManagerService.this.dumpActivity(fd, pw, name, args, opti, dumpAll, dumpVisibleStacksOnly, dumpFocusedStackOnly);
        }

        public void dumpForOom(PrintWriter pw) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    pw.println("  mHomeProcess: " + ActivityTaskManagerService.this.mHomeProcess);
                    pw.println("  mPreviousProcess: " + ActivityTaskManagerService.this.mPreviousProcess);
                    if (ActivityTaskManagerService.this.mHeavyWeightProcess != null) {
                        pw.println("  mHeavyWeightProcess: " + ActivityTaskManagerService.this.mHeavyWeightProcess);
                    }
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public boolean canGcNow() {
            boolean z;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    if (!isSleeping()) {
                        if (!ActivityTaskManagerService.this.mRootActivityContainer.allResumedActivitiesIdle()) {
                            z = false;
                        }
                    }
                    z = true;
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return z;
        }

        public WindowProcessController getTopApp() {
            WindowProcessController windowProcessController;
            synchronized (ActivityTaskManagerService.this.mGlobalLockWithoutBoost) {
                ActivityRecord top = ActivityTaskManagerService.this.mRootActivityContainer.getTopResumedActivity();
                windowProcessController = top != null ? top.app : null;
            }
            return windowProcessController;
        }

        public void rankTaskLayersIfNeeded() {
            synchronized (ActivityTaskManagerService.this.mGlobalLockWithoutBoost) {
                if (ActivityTaskManagerService.this.mRootActivityContainer != null) {
                    ActivityTaskManagerService.this.mRootActivityContainer.rankTaskLayersIfNeeded();
                }
            }
        }

        public void scheduleDestroyAllActivities(String reason) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mRootActivityContainer.scheduleDestroyAllActivities((WindowProcessController) null, reason);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public void removeUser(int userId) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mRootActivityContainer.removeUser(userId);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public boolean switchUser(int userId, UserState userState) {
            boolean switchUser;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    switchUser = ActivityTaskManagerService.this.mRootActivityContainer.switchUser(userId, userState);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return switchUser;
        }

        public void onHandleAppCrash(WindowProcessController wpc) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mRootActivityContainer.handleAppCrash(wpc);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public int finishTopCrashedActivities(WindowProcessController crashedApp, String reason) {
            int finishTopCrashedActivities;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    finishTopCrashedActivities = ActivityTaskManagerService.this.mRootActivityContainer.finishTopCrashedActivities(crashedApp, reason);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return finishTopCrashedActivities;
        }

        public void onUidActive(int uid, int procState) {
            ActivityTaskManagerService.this.mActiveUids.onUidActive(uid, procState);
        }

        public void onUidInactive(int uid) {
            ActivityTaskManagerService.this.mActiveUids.onUidInactive(uid);
        }

        public void onActiveUidsCleared() {
            ActivityTaskManagerService.this.mActiveUids.onActiveUidsCleared();
        }

        public void onUidProcStateChanged(int uid, int procState) {
            ActivityTaskManagerService.this.mActiveUids.onUidProcStateChanged(uid, procState);
        }

        public void onUidAddedToPendingTempWhitelist(int uid, String tag) {
            synchronized (ActivityTaskManagerService.this.mGlobalLockWithoutBoost) {
                ActivityTaskManagerService.this.mPendingTempWhitelist.put(uid, tag);
            }
        }

        public void onUidRemovedFromPendingTempWhitelist(int uid) {
            synchronized (ActivityTaskManagerService.this.mGlobalLockWithoutBoost) {
                ActivityTaskManagerService.this.mPendingTempWhitelist.remove(uid);
            }
        }

        public boolean handleAppCrashInActivityController(String processName, int pid, String shortMsg, String longMsg, long timeMillis, String stackTrace, Runnable killCrashingAppCallback) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    if (ActivityTaskManagerService.this.mController == null) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return false;
                    } else if (!ActivityTaskManagerService.this.mController.appCrashed(processName, pid, shortMsg, longMsg, timeMillis, stackTrace)) {
                        killCrashingAppCallback.run();
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return true;
                    }
                } catch (RemoteException e) {
                    ActivityTaskManagerService.this.mController = null;
                    Watchdog.getInstance().setActivityController((IActivityController) null);
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return false;
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
        }

        public void removeRecentTasksByPackageName(String packageName, int userId) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mRecentTasks.removeTasksByPackageName(packageName, userId);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public void cleanupRecentTasksForUser(int userId) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mRecentTasks.cleanupLocked(userId);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public void loadRecentTasksForUser(int userId) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mRecentTasks.loadUserRecentsLocked(userId);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public void onPackagesSuspendedChanged(String[] packages, boolean suspended, int userId) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mRecentTasks.onPackagesSuspendedChanged(packages, suspended, userId);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public void flushRecentTasks() {
            ActivityTaskManagerService.this.mRecentTasks.flush();
        }

        public WindowProcessController getHomeProcess() {
            WindowProcessController windowProcessController;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    windowProcessController = ActivityTaskManagerService.this.mHomeProcess;
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return windowProcessController;
        }

        public WindowProcessController getPreviousProcess() {
            WindowProcessController windowProcessController;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    windowProcessController = ActivityTaskManagerService.this.mPreviousProcess;
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return windowProcessController;
        }

        public void clearLockedTasks(String reason) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.getLockTaskController().clearLockedTasks(reason);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public void updateUserConfiguration() {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    Configuration configuration = new Configuration(ActivityTaskManagerService.this.getGlobalConfiguration());
                    int currentUserId = ActivityTaskManagerService.this.mAmInternal.getCurrentUserId();
                    Settings.System.adjustConfigurationForUser(ActivityTaskManagerService.this.mContext.getContentResolver(), configuration, currentUserId, Settings.System.canWrite(ActivityTaskManagerService.this.mContext));
                    boolean unused = ActivityTaskManagerService.this.updateConfigurationLocked(configuration, (ActivityRecord) null, false, false, currentUserId, false);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public boolean canShowErrorDialogs() {
            boolean z;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    z = false;
                    if (ActivityTaskManagerService.this.mShowDialogs && !ActivityTaskManagerService.this.mSleeping && !ActivityTaskManagerService.this.mShuttingDown && !ActivityTaskManagerService.this.mKeyguardController.isKeyguardOrAodShowing(0) && !ActivityTaskManagerService.this.hasUserRestriction("no_system_error_dialogs", ActivityTaskManagerService.this.mAmInternal.getCurrentUserId()) && (!UserManager.isDeviceInDemoMode(ActivityTaskManagerService.this.mContext) || !ActivityTaskManagerService.this.mAmInternal.getCurrentUser().isDemo())) {
                        z = true;
                    }
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return z;
        }

        public void setProfileApp(String profileApp) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mProfileApp = profileApp;
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public void setProfileProc(WindowProcessController wpc) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mProfileProc = wpc;
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public void setProfilerInfo(ProfilerInfo profilerInfo) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mProfilerInfo = profilerInfo;
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public ActivityMetricsLaunchObserverRegistry getLaunchObserverRegistry() {
            ActivityMetricsLaunchObserverRegistry launchObserverRegistry;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    launchObserverRegistry = ActivityTaskManagerService.this.mStackSupervisor.getActivityMetricsLogger().getLaunchObserverRegistry();
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return launchObserverRegistry;
        }

        public ActivityManager.TaskSnapshot getTaskSnapshotNoRestore(int taskId, boolean reducedResolution) {
            return ActivityTaskManagerService.this.getTaskSnapshot(taskId, reducedResolution, false);
        }

        public boolean isUidForeground(int uid) {
            boolean isUidForeground;
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    isUidForeground = ActivityTaskManagerService.this.isUidForeground(uid);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return isUidForeground;
        }

        public void setDeviceOwnerUid(int uid) {
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.setDeviceOwnerUid(uid);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public void setCompanionAppPackages(int userId, Set<String> companionAppPackages) {
            Set<Integer> result = new HashSet<>();
            for (String pkg : companionAppPackages) {
                int uid = ActivityTaskManagerService.this.getPackageManagerInternalLocked().getPackageUid(pkg, 0, userId);
                if (uid >= 0) {
                    result.add(Integer.valueOf(uid));
                }
            }
            synchronized (ActivityTaskManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityTaskManagerService.this.mCompanionAppUidsMap.put(Integer.valueOf(userId), result);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public void moveTopActivityToCastMode() {
            ActivityTaskManagerService.this.moveTopActivityToCastMode();
        }

        public void exitCastMode() {
            ActivityTaskManagerService.this.exitCastMode();
        }

        public void exitProjectionMode() {
            ActivityTaskManagerService.this.exitProjectionMode();
        }
    }

    public int handleFreeformModeRequst(IBinder token, int cmd) {
        return ActivityTaskManagerServiceInjector.handleFreeformModeRequst(token, cmd, this.mContext);
    }

    public void registerMiuiAppTransitionAnimationHelper(IMiuiAppTransitionAnimationHelper helper, int displayId) {
        if (helper != null) {
            synchronized (this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    unlinkToDeathMiuiAnimationHelper();
                    this.mAnimationHelperDeathRecipient = new AnimationHelperDeathRecipient(displayId);
                    helper.asBinder().linkToDeath(this.mAnimationHelperDeathRecipient, 0);
                    this.mMiuiAppTransitionAnimationHelper = helper;
                } catch (RemoteException e) {
                    Slog.w("ActivityTaskManager", "MiuiAppTransitionAnimationHelper linkToDeath failed.");
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                } catch (Throwable e2) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw e2;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            WindowManagerService windowManagerService = this.mWindowManager;
            if (windowManagerService != null) {
                windowManagerService.setMiuiAppTransitionAnimationHelper(this.mMiuiAppTransitionAnimationHelper, displayId);
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x001d, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
        r0 = r3.mWindowManager;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0022, code lost:
        if (r0 == null) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0024, code lost:
        r0.setMiuiAppTransitionAnimationHelper((com.miui.internal.transition.IMiuiAppTransitionAnimationHelper) null, r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void unregisterMiuiAppTransitionAnimationHelper(com.android.server.wm.ActivityTaskManagerService.AnimationHelperDeathRecipient r4, int r5) {
        /*
            r3 = this;
            com.android.server.wm.WindowManagerGlobalLock r0 = r3.mGlobalLock
            monitor-enter(r0)
            com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x0028 }
            com.android.server.wm.ActivityTaskManagerService$AnimationHelperDeathRecipient r1 = r3.mAnimationHelperDeathRecipient     // Catch:{ all -> 0x0028 }
            if (r4 == r1) goto L_0x0016
            java.lang.String r1 = "ActivityTaskManager"
            java.lang.String r2 = "The death recipient has changed, we have registered a new helper so we don't need to unregister now."
            android.util.Slog.w(r1, r2)     // Catch:{ all -> 0x0028 }
            monitor-exit(r0)     // Catch:{ all -> 0x0028 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return
        L_0x0016:
            r3.unlinkToDeathMiuiAnimationHelper()     // Catch:{ all -> 0x0028 }
            r1 = 0
            r3.mMiuiAppTransitionAnimationHelper = r1     // Catch:{ all -> 0x0028 }
            monitor-exit(r0)     // Catch:{ all -> 0x0028 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            com.android.server.wm.WindowManagerService r0 = r3.mWindowManager
            if (r0 == 0) goto L_0x0027
            r0.setMiuiAppTransitionAnimationHelper(r1, r5)
        L_0x0027:
            return
        L_0x0028:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0028 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityTaskManagerService.unregisterMiuiAppTransitionAnimationHelper(com.android.server.wm.ActivityTaskManagerService$AnimationHelperDeathRecipient, int):void");
    }

    public void setDummyTranslucent(IBinder token, boolean translucent) {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord r = ActivityRecord.isInStackLocked(token);
                if (r == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                r.changeWindowTranslucency(!translucent);
                this.mRootActivityContainer.ensureActivitiesVisible((ActivityRecord) null, 0, false);
                WindowManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    private void unlinkToDeathMiuiAnimationHelper() {
        IMiuiAppTransitionAnimationHelper iMiuiAppTransitionAnimationHelper = this.mMiuiAppTransitionAnimationHelper;
        if (iMiuiAppTransitionAnimationHelper != null && this.mAnimationHelperDeathRecipient != null) {
            iMiuiAppTransitionAnimationHelper.asBinder().unlinkToDeath(this.mAnimationHelperDeathRecipient, 0);
            this.mAnimationHelperDeathRecipient = null;
        }
    }

    /* access modifiers changed from: package-private */
    public void updateMiuiAnimationInfo(final ActivityRecord r) {
        if (r != null && !this.mWindowManager.isAppTransitionSkipped()) {
            if (!r.canShowWhenLocked() || !this.mKeyguardController.isKeyguardLocked()) {
                final String lastComponentName = r.realComponentName;
                final int userId = r.mUserId;
                this.mWindowManager.setLoadBackHomeAnimation(true, r.getDisplayId());
                this.mWindowManager.startFetchingAppTransitionSpecs(r.getDisplayId());
                this.mDefaultExecutor.execute(new Runnable() {
                    public void run() {
                        Bitmap iconThumbnail;
                        try {
                            IMiuiAppTransitionAnimationHelper helper = ActivityTaskManagerService.this.mMiuiAppTransitionAnimationHelper;
                            if (helper != null) {
                                Binder.allowBlocking(helper.asBinder());
                                MiuiAppTransitionAnimationSpec spec = helper.getSpec(lastComponentName, userId);
                                GraphicBuffer iconGraphicBuffer = null;
                                if (spec == null || (iconThumbnail = spec.mBitmap) == null) {
                                    ActivityTaskManagerService.this.mWindowManager.overrideMiuiAnimationInfo((GraphicBuffer) null, (Rect) null, r.getDisplayId());
                                    return;
                                }
                                Bitmap hwBitmap = iconThumbnail.copy(Bitmap.Config.HARDWARE, false);
                                if (hwBitmap != null) {
                                    iconGraphicBuffer = hwBitmap.createGraphicBufferHandle();
                                }
                                iconThumbnail.recycle();
                                ActivityTaskManagerService.this.mWindowManager.overrideMiuiAnimationInfo(iconGraphicBuffer, spec.mRect, r.getDisplayId());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void updateMiuiRoundedViewAnimationInfo(ActivityRecord r) {
        this.mWindowManager.setLoadRoundedViewAnimation(true, r.mLaunchedOrientation != this.mContext.getResources().getConfiguration().orientation, r.getDisplayId());
        this.mWindowManager.overrideMiuiRoundedViewAnimationInfo(r.mThumbnail, new Rect(r.mStartX, r.mStartY, r.mStartX + r.mWidth, r.mStartY + r.mHeight), r.mRadius, r.mForeGroundColor, r.mAnimationReenterStartedCallback, r.mAnimationReenterFinishedCallback, r.getDisplayId());
    }

    /* access modifiers changed from: package-private */
    public void setIsMultiWindowMode(ActivityRecord r) {
        if (r != null) {
            this.mWindowManager.setIsInMultiWindowMode(r.inMultiWindowMode());
        }
    }

    private final class AnimationHelperDeathRecipient implements IBinder.DeathRecipient {
        int mDisplayId;

        AnimationHelperDeathRecipient(int displayId) {
            this.mDisplayId = displayId;
        }

        public void binderDied() {
            ActivityTaskManagerService.this.unregisterMiuiAppTransitionAnimationHelper(this, this.mDisplayId);
        }
    }

    public Intent getTopVisibleActivity() {
        ActivityRecord r;
        Intent activity = new Intent();
        synchronized (this) {
            ActivityStack mainStack = getTopDisplayFocusedStack();
            if (!(mainStack == null || (r = mainStack.topRunningActivityLocked()) == null || !r.isState(ActivityStack.ActivityState.RESUMED))) {
                activity.setComponent(r.mActivityComponent);
            }
        }
        return activity;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x002d, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isTopActivityInFreeform(java.lang.String r6) {
        /*
            r5 = this;
            monitor-enter(r5)
            com.android.server.wm.RootActivityContainer r0 = r5.mRootActivityContainer     // Catch:{ all -> 0x002e }
            r1 = 0
            com.android.server.wm.ActivityDisplay r0 = r0.getActivityDisplay((int) r1)     // Catch:{ all -> 0x002e }
            if (r0 == 0) goto L_0x002c
            com.android.server.wm.ActivityStack r2 = r0.getTopStack()     // Catch:{ all -> 0x002e }
            if (r2 == 0) goto L_0x002c
            com.android.server.wm.ActivityStack r2 = r0.getTopStack()     // Catch:{ all -> 0x002e }
            com.android.server.wm.ActivityRecord r2 = r2.topRunningActivityLocked()     // Catch:{ all -> 0x002e }
            if (r2 == 0) goto L_0x002c
            int r3 = r2.getWindowingMode()     // Catch:{ all -> 0x002e }
            r4 = 5
            if (r3 != r4) goto L_0x002c
            java.lang.String r3 = r2.packageName     // Catch:{ all -> 0x002e }
            boolean r3 = r3.equals(r6)     // Catch:{ all -> 0x002e }
            if (r3 == 0) goto L_0x002c
            r1 = 1
            monitor-exit(r5)     // Catch:{ all -> 0x002e }
            return r1
        L_0x002c:
            monitor-exit(r5)     // Catch:{ all -> 0x002e }
            return r1
        L_0x002e:
            r0 = move-exception
            monitor-exit(r5)     // Catch:{ all -> 0x002e }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityTaskManagerService.isTopActivityInFreeform(java.lang.String):boolean");
    }

    public void moveTopActivityToCastMode() {
        long endTime = SystemClock.elapsedRealtime() + 2000;
        ActivityRecord topActivity = null;
        ActivityStack stack = null;
        synchronized (this.mActionDoneSync) {
            while (true) {
                if (!(stack == null || topActivity == null)) {
                    if (topActivity.getState() == ActivityStack.ActivityState.RESUMED) {
                        synchronized (this.mGlobalLock) {
                            try {
                                WindowManagerService.boostPriorityForLockedSection();
                                topActivity.setCastMode(true);
                                this.mCastActivity = topActivity;
                                stack.moveToBack("moveTopActivityToCastMode", stack.topTask());
                                this.mRootActivityContainer.resumeHomeActivity(topActivity, "moveTopActivityToCastMode", 0);
                                setCurrentCastModeState(topActivity.packageName, 1);
                            } catch (Throwable th) {
                                while (true) {
                                    WindowManagerService.resetPriorityAfterLockedSection();
                                    throw th;
                                }
                            }
                        }
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return;
                    }
                }
                stack = getTopDisplayFocusedStack();
                topActivity = stack.getTopActivity();
                long delay = endTime - SystemClock.elapsedRealtime();
                if (delay <= 0) {
                    Slog.e("ActivityTaskManager", "moveTopActivityToCastMode timeout!");
                    return;
                }
                try {
                    this.mActionDoneSync.wait(Math.min(delay, 100));
                } catch (InterruptedException e) {
                }
            }
        }
    }

    public void exitCastMode() {
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                if (!(this.mCastActivity == null || this.mCastActivity.getTaskRecord() == null)) {
                    TaskRecord task = this.mCastActivity.getTaskRecord();
                    task.getStack().moveToFront("exitCastMode", task);
                    resumeCastActivity();
                }
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void exitProjectionMode() {
        ActivityRecord activityRecord = this.mCastActivity;
        if (activityRecord != null) {
            String packageName = activityRecord.packageName;
            synchronized (this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    if (this.mCastActivity != null) {
                        this.mCastActivity.setCastMode(false);
                        setCurrentCastModeState(packageName, 0);
                        this.mCastActivity = null;
                    }
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            this.mAmInternal.forceStopPackage(packageName, 0, "exitProjectionMode");
        }
    }

    public void castRotationChanged(boolean changed) {
        this.mCastRotationChanged = changed;
    }

    public boolean getCastRotationChanged() {
        return this.mCastRotationChanged;
    }

    public void resumeCastActivity() {
        if (this.mCastActivity != null) {
            this.mRootActivityContainer.getActivityDisplay(0).pauseBackStacks(true, this.mCastActivity, true);
            this.mRootActivityContainer.ensureVisibilityAndConfig(this.mCastActivity, 0, true, false);
            this.mCastActivity.setCastMode(false);
            setCurrentCastModeState(this.mCastActivity.packageName, 0);
            ActivityTaskManagerServiceInjector.getMiuiActivityController().activityResumed(this.mCastActivity);
            ActivityTaskManagerServiceInjector.onForegroundActivityChangedLocked(this.mCastActivity);
            this.mCastActivity = null;
        }
    }

    public int getCastModeStackId() {
        ActivityRecord activityRecord = this.mCastActivity;
        if (activityRecord == null || activityRecord.getTaskRecord() == null) {
            return -1;
        }
        return this.mCastActivity.getTaskRecord().getStack().mStackId;
    }

    public float getFreeformScale() {
        return MiuiMultiWindowUtils.sScale;
    }

    public void setCurrentCastModeState(String packageName, int enter) {
        this.mAmInternal.setCastPid(enter, packageName);
        ContentResolver res = this.mContext.getContentResolver();
        Settings.Secure.putInt(res, "cast_mode", enter);
        Settings.Secure.putString(res, "cast_mode_package", packageName);
    }

    /* access modifiers changed from: package-private */
    public void setSchedFgPid(ActivityRecord r) {
        if (r != null && r.app != null) {
            ActivityTaskManagerServiceInjector.setSchedFgPid(r.app.getPid());
        }
    }

    /* access modifiers changed from: package-private */
    public void setSchedFgPid(int pid) {
        if (pid > 0) {
            ActivityTaskManagerServiceInjector.setSchedFgPid(pid);
        }
    }
}
