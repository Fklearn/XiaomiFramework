package com.android.server.wm;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.IApplicationThread;
import android.app.PendingIntent;
import android.app.PictureInPictureParams;
import android.app.ResultInfo;
import android.app.WindowConfiguration;
import android.app.servertransaction.ActivityConfigurationChangeItem;
import android.app.servertransaction.ActivityLifecycleItem;
import android.app.servertransaction.ActivityRelaunchItem;
import android.app.servertransaction.ClientTransaction;
import android.app.servertransaction.ClientTransactionItem;
import android.app.servertransaction.MoveToDisplayItem;
import android.app.servertransaction.MultiWindowModeChangeItem;
import android.app.servertransaction.NewIntentItem;
import android.app.servertransaction.PauseActivityItem;
import android.app.servertransaction.PipModeChangeItem;
import android.app.servertransaction.ResumeActivityItem;
import android.app.servertransaction.StopActivityItem;
import android.app.servertransaction.TopResumedActivityChangeItem;
import android.app.servertransaction.WindowVisibilityItem;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.GraphicBuffer;
import android.graphics.Rect;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IRemoteCallback;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.storage.StorageManager;
import android.server.am.SplitScreenReporter;
import android.service.voice.IVoiceInteractionSession;
import android.util.BoostFramework;
import android.util.EventLog;
import android.util.MergedConfiguration;
import android.util.MiuiMultiWindowAdapter;
import android.util.Slog;
import android.util.TimeUtils;
import android.util.proto.ProtoOutputStream;
import android.view.AppTransitionAnimationSpec;
import android.view.IAppTransitionAnimationSpecsFuture;
import android.view.IApplicationToken;
import android.view.RemoteAnimationDefinition;
import com.android.internal.R;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.ResolverActivity;
import com.android.internal.content.ReferrerIntent;
import com.android.server.AttributeCache;
import com.android.server.LocalServices;
import com.android.server.am.ActivityManagerServiceInjector;
import com.android.server.am.AppTimeTracker;
import com.android.server.am.EventLogTags;
import com.android.server.am.PendingIntentRecord;
import com.android.server.pm.DumpState;
import com.android.server.pm.PackageManagerService;
import com.android.server.uri.UriPermissionOwner;
import com.android.server.usb.descriptors.UsbTerminalTypes;
import com.android.server.wm.ActivityMetricsLogger;
import com.android.server.wm.ActivityStack;
import com.android.server.wm.ActivityStackSupervisor;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import miui.os.MiuiInit;
import miui.os.SystemProperties;
import miui.process.ProcessManagerInternal;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public final class ActivityRecord extends ConfigurationContainer {
    static final String ACTIVITY_ICON_SUFFIX = "_activity_icon_";
    private static final String ATTR_COMPONENTSPECIFIED = "component_specified";
    private static final String ATTR_ID = "id";
    private static final String ATTR_LAUNCHEDFROMPACKAGE = "launched_from_package";
    private static final String ATTR_LAUNCHEDFROMUID = "launched_from_uid";
    private static final String ATTR_RESOLVEDTYPE = "resolved_type";
    private static final String ATTR_USERID = "user_id";
    private static final String LEGACY_RECENTS_PACKAGE_NAME = "com.android.systemui.recents";
    private static final boolean SHOW_ACTIVITY_START_TIME = true;
    static final int STARTING_WINDOW_NOT_SHOWN = 0;
    static final int STARTING_WINDOW_REMOVED = 2;
    static final int STARTING_WINDOW_SHOWN = 1;
    private static final String TAG = "ActivityTaskManager";
    private static final String TAG_CONFIGURATION = "ActivityTaskManager";
    private static final String TAG_FOCUS = "ActivityTaskManager";
    private static final String TAG_INTENT = "intent";
    private static final String TAG_PERSISTABLEBUNDLE = "persistable_bundle";
    private static final String TAG_SAVED_STATE = "ActivityTaskManager";
    private static final String TAG_STATES = "ActivityTaskManager";
    private static final String TAG_SWITCH = "ActivityTaskManager";
    private static final String TAG_VISIBILITY = "ActivityTaskManager";
    WindowProcessController app;
    ApplicationInfo appInfo;
    AppTimeTracker appTimeTracker;
    final IApplicationToken.Stub appToken;
    final Binder assistToken = new Binder();
    CompatibilityInfo compat;
    private final boolean componentSpecified;
    int configChangeFlags;
    long cpuTimeAtResume;
    private long createTime = System.currentTimeMillis();
    boolean deferRelaunchUntilPaused;
    boolean delayedResume;
    boolean finishing;
    boolean forceNewConfig;
    boolean frontOfTask;
    boolean frozenBeforeDestroy;
    boolean fullscreen;
    boolean hasBeenLaunched;
    final boolean hasWallpaper;
    boolean haveState;
    Bundle icicle;
    private int icon;
    boolean idle;
    boolean immersive;
    private boolean inHistory;
    final ActivityInfo info;
    final Intent intent;
    boolean isColdStart;
    private boolean keysPaused;
    private int labelRes;
    long lastLaunchTime;
    long lastVisibleTime;
    int launchCount;
    boolean launchFailed;
    int launchMode;
    long launchTickTime;
    final String launchedFromPackage;
    final int launchedFromPid;
    final int launchedFromUid;
    public boolean launching;
    int lockTaskLaunchMode;
    private int logo;
    final ComponentName mActivityComponent;
    public IRemoteCallback mAnimationReenterFinishedCallback;
    public IRemoteCallback mAnimationReenterStartedCallback;
    AppWindowToken mAppWindowToken;
    final ActivityTaskManagerService mAtmService;
    boolean mClientVisibilityDeferred;
    private CompatDisplayInsets mCompatDisplayInsets;
    private int mConfigurationSeq;
    private boolean mDeferHidingClient;
    boolean mDisableDummyVisible;
    boolean mDrawn;
    public int mForeGroundColor;
    @VisibleForTesting
    int mHandoverLaunchDisplayId = -1;
    public int mHeight;
    private int[] mHorizontalSizeConfigurations;
    private boolean mInheritShownWhenLocked;
    boolean mIsCastMode;
    boolean mIsDummyVisible;
    boolean mIsLastFrame;
    private MergedConfiguration mLastReportedConfiguration;
    private int mLastReportedDisplayId;
    private boolean mLastReportedMultiWindowMode;
    private boolean mLastReportedPictureInPictureMode;
    boolean mLaunchTaskBehind;
    public int mLaunchedOrientation;
    public boolean mLunchedFromRoundedView;
    private int mMiuiConfigFlag = 1;
    public BoostFramework mPerf = null;
    public BoostFramework mPerf_iop = null;
    public int mRadius;
    int mRelaunchReason = 0;
    final RootActivityContainer mRootActivityContainer;
    int mRotationAnimationHint = -1;
    ActivityServiceConnectionsHolder mServiceConnectionsHolder;
    private boolean mShowWhenLocked;
    private int[] mSmallestSizeConfigurations;
    final ActivityStackSupervisor mStackSupervisor;
    public int mStartX;
    public int mStartY;
    int mStartingWindowState = 0;
    private ActivityStack.ActivityState mState;
    boolean mTaskOverlay = false;
    public GraphicBuffer mThumbnail;
    private final Rect mTmpBounds = new Rect();
    private final Configuration mTmpConfig = new Configuration();
    private boolean mTurnScreenOn;
    final int mUserId;
    private int[] mVerticalSizeConfigurations;
    public int mWidth;
    ArrayList<ReferrerIntent> newIntents;
    @VisibleForTesting
    boolean noDisplay;
    private CharSequence nonLocalizedLabel;
    boolean nowVisible;
    public final String packageName;
    long pauseTime;
    ActivityOptions pendingOptions;
    HashSet<WeakReference<PendingIntentRecord>> pendingResults;
    boolean pendingVoiceInteractionStart;
    public int perfActivityBoostHandler = -1;
    PersistableBundle persistentState;
    PictureInPictureParams pictureInPictureArgs = new PictureInPictureParams.Builder().build();
    boolean preserveWindowOnDeferredRelaunch;
    final String processName;
    String realComponentName = null;
    private int realTheme;
    final int requestCode;
    ComponentName requestedVrComponent;
    final String resolvedType;
    ActivityRecord resultTo;
    final String resultWho;
    ArrayList<ResultInfo> results;
    ActivityOptions returningOptions;
    final boolean rootVoiceInteraction;
    final String shortComponentName;
    boolean sleeping;
    final boolean stateNotNeeded;
    boolean stopped;
    String stringName;
    boolean supportsEnterPipOnTaskSwitch;
    private TaskRecord task;
    final String taskAffinity;
    ActivityManager.TaskDescription taskDescription;
    private int theme;
    long topResumedStateLossTime;
    UriPermissionOwner uriPermissions;
    boolean visible;
    boolean visibleIgnoringKeyguard;
    IVoiceInteractionSession voiceSession;
    private int windowFlags;

    private static String startingWindowStateToString(int state) {
        if (state == 0) {
            return "STARTING_WINDOW_NOT_SHOWN";
        }
        if (state == 1) {
            return "STARTING_WINDOW_SHOWN";
        }
        if (state == 2) {
            return "STARTING_WINDOW_REMOVED";
        }
        return "unknown state=" + state;
    }

    /* access modifiers changed from: package-private */
    public void dump(PrintWriter pw, String prefix) {
        String str;
        long now = SystemClock.uptimeMillis();
        pw.print(prefix);
        pw.print("packageName=");
        pw.print(this.packageName);
        pw.print(" processName=");
        pw.println(this.processName);
        pw.print(prefix);
        pw.print("launchedFromUid=");
        pw.print(this.launchedFromUid);
        pw.print(" launchedFromPackage=");
        pw.print(this.launchedFromPackage);
        pw.print(" userId=");
        pw.println(this.mUserId);
        pw.print(prefix);
        pw.print("app=");
        pw.println(this.app);
        pw.print(prefix);
        pw.println(this.intent.toInsecureStringWithClip());
        pw.print(prefix);
        pw.print("frontOfTask=");
        pw.print(this.frontOfTask);
        pw.print(" task=");
        pw.println(this.task);
        pw.print(prefix);
        pw.print("taskAffinity=");
        pw.println(this.taskAffinity);
        pw.print(prefix);
        pw.print("mActivityComponent=");
        pw.println(this.mActivityComponent.flattenToShortString());
        if (this.appInfo != null) {
            pw.print(prefix);
            pw.print("baseDir=");
            pw.println(this.appInfo.sourceDir);
            if (!Objects.equals(this.appInfo.sourceDir, this.appInfo.publicSourceDir)) {
                pw.print(prefix);
                pw.print("resDir=");
                pw.println(this.appInfo.publicSourceDir);
            }
            pw.print(prefix);
            pw.print("dataDir=");
            pw.println(this.appInfo.dataDir);
            if (this.appInfo.splitSourceDirs != null) {
                pw.print(prefix);
                pw.print("splitDir=");
                pw.println(Arrays.toString(this.appInfo.splitSourceDirs));
            }
        }
        pw.print(prefix);
        pw.print("stateNotNeeded=");
        pw.print(this.stateNotNeeded);
        pw.print(" componentSpecified=");
        pw.print(this.componentSpecified);
        pw.print(" mActivityType=");
        pw.println(WindowConfiguration.activityTypeToString(getActivityType()));
        if (this.rootVoiceInteraction) {
            pw.print(prefix);
            pw.print("rootVoiceInteraction=");
            pw.println(this.rootVoiceInteraction);
        }
        pw.print(prefix);
        pw.print("compat=");
        pw.print(this.compat);
        pw.print(" labelRes=0x");
        pw.print(Integer.toHexString(this.labelRes));
        pw.print(" icon=0x");
        pw.print(Integer.toHexString(this.icon));
        pw.print(" theme=0x");
        pw.println(Integer.toHexString(this.theme));
        pw.println(prefix + "mLastReportedConfigurations:");
        this.mLastReportedConfiguration.dump(pw, prefix + " ");
        pw.print(prefix);
        pw.print("CurrentConfiguration=");
        pw.println(getConfiguration());
        if (!getRequestedOverrideConfiguration().equals(Configuration.EMPTY)) {
            pw.println(prefix + "RequestedOverrideConfiguration=" + getRequestedOverrideConfiguration());
        }
        if (!getResolvedOverrideConfiguration().equals(getRequestedOverrideConfiguration())) {
            pw.println(prefix + "ResolvedOverrideConfiguration=" + getResolvedOverrideConfiguration());
        }
        if (!matchParentBounds()) {
            pw.println(prefix + "bounds=" + getBounds());
        }
        if (!(this.resultTo == null && this.resultWho == null)) {
            pw.print(prefix);
            pw.print("resultTo=");
            pw.print(this.resultTo);
            pw.print(" resultWho=");
            pw.print(this.resultWho);
            pw.print(" resultCode=");
            pw.println(this.requestCode);
        }
        ActivityManager.TaskDescription taskDescription2 = this.taskDescription;
        if (!(taskDescription2 == null || (taskDescription2.getIconFilename() == null && this.taskDescription.getLabel() == null && this.taskDescription.getPrimaryColor() == 0))) {
            pw.print(prefix);
            pw.print("taskDescription:");
            pw.print(" label=\"");
            pw.print(this.taskDescription.getLabel());
            pw.print("\"");
            pw.print(" icon=");
            if (this.taskDescription.getInMemoryIcon() != null) {
                str = this.taskDescription.getInMemoryIcon().getByteCount() + " bytes";
            } else {
                str = "null";
            }
            pw.print(str);
            pw.print(" iconResource=");
            pw.print(this.taskDescription.getIconResource());
            pw.print(" iconFilename=");
            pw.print(this.taskDescription.getIconFilename());
            pw.print(" primaryColor=");
            pw.println(Integer.toHexString(this.taskDescription.getPrimaryColor()));
            pw.print(prefix + " backgroundColor=");
            pw.println(Integer.toHexString(this.taskDescription.getBackgroundColor()));
            pw.print(prefix + " statusBarColor=");
            pw.println(Integer.toHexString(this.taskDescription.getStatusBarColor()));
            pw.print(prefix + " navigationBarColor=");
            pw.println(Integer.toHexString(this.taskDescription.getNavigationBarColor()));
        }
        if (this.results != null) {
            pw.print(prefix);
            pw.print("results=");
            pw.println(this.results);
        }
        HashSet<WeakReference<PendingIntentRecord>> hashSet = this.pendingResults;
        if (hashSet != null && hashSet.size() > 0) {
            pw.print(prefix);
            pw.println("Pending Results:");
            Iterator<WeakReference<PendingIntentRecord>> it = this.pendingResults.iterator();
            while (it.hasNext()) {
                WeakReference<PendingIntentRecord> wpir = it.next();
                PendingIntentRecord pir = wpir != null ? (PendingIntentRecord) wpir.get() : null;
                pw.print(prefix);
                pw.print("  - ");
                if (pir == null) {
                    pw.println("null");
                } else {
                    pw.println(pir);
                    pir.dump(pw, prefix + "    ");
                }
            }
        }
        ArrayList<ReferrerIntent> arrayList = this.newIntents;
        if (arrayList != null && arrayList.size() > 0) {
            pw.print(prefix);
            pw.println("Pending New Intents:");
            for (int i = 0; i < this.newIntents.size(); i++) {
                Intent intent2 = this.newIntents.get(i);
                pw.print(prefix);
                pw.print("  - ");
                if (intent2 == null) {
                    pw.println("null");
                } else {
                    pw.println(intent2.toShortString(false, true, false, true));
                }
            }
        }
        if (this.pendingOptions != null) {
            pw.print(prefix);
            pw.print("pendingOptions=");
            pw.println(this.pendingOptions);
        }
        AppTimeTracker appTimeTracker2 = this.appTimeTracker;
        if (appTimeTracker2 != null) {
            appTimeTracker2.dumpWithHeader(pw, prefix, false);
        }
        UriPermissionOwner uriPermissionOwner = this.uriPermissions;
        if (uriPermissionOwner != null) {
            uriPermissionOwner.dump(pw, prefix);
        }
        pw.print(prefix);
        pw.print("launchFailed=");
        pw.print(this.launchFailed);
        pw.print(" launchCount=");
        pw.print(this.launchCount);
        pw.print(" lastLaunchTime=");
        long j = this.lastLaunchTime;
        if (j == 0) {
            pw.print("0");
        } else {
            TimeUtils.formatDuration(j, now, pw);
        }
        pw.println();
        pw.print(prefix);
        pw.print("haveState=");
        pw.print(this.haveState);
        pw.print(" icicle=");
        pw.println(this.icicle);
        pw.print(prefix);
        pw.print("state=");
        pw.print(this.mState);
        pw.print(" stopped=");
        pw.print(this.stopped);
        pw.print(" delayedResume=");
        pw.print(this.delayedResume);
        pw.print(" finishing=");
        pw.println(this.finishing);
        pw.print(prefix);
        pw.print("keysPaused=");
        pw.print(this.keysPaused);
        pw.print(" inHistory=");
        pw.print(this.inHistory);
        pw.print(" visible=");
        pw.print(this.visible);
        pw.print(" mIsCastMode=");
        pw.print(this.mIsCastMode);
        pw.print(" mIsLastFrame=");
        pw.print(this.mIsLastFrame);
        pw.print(" sleeping=");
        pw.print(this.sleeping);
        pw.print(" idle=");
        pw.print(this.idle);
        pw.print(" mStartingWindowState=");
        pw.println(startingWindowStateToString(this.mStartingWindowState));
        pw.print(prefix);
        pw.print("fullscreen=");
        pw.print(this.fullscreen);
        pw.print(" noDisplay=");
        pw.print(this.noDisplay);
        pw.print(" immersive=");
        pw.print(this.immersive);
        pw.print(" launchMode=");
        pw.println(this.launchMode);
        pw.print(prefix);
        pw.print("frozenBeforeDestroy=");
        pw.print(this.frozenBeforeDestroy);
        pw.print(" forceNewConfig=");
        pw.println(this.forceNewConfig);
        pw.print(prefix);
        pw.print("mActivityType=");
        pw.println(WindowConfiguration.activityTypeToString(getActivityType()));
        if (this.requestedVrComponent != null) {
            pw.print(prefix);
            pw.print("requestedVrComponent=");
            pw.println(this.requestedVrComponent);
        }
        if (this.lastVisibleTime != 0 || this.nowVisible) {
            pw.print(prefix);
            pw.print(" nowVisible=");
            pw.print(this.nowVisible);
            pw.print(" lastVisibleTime=");
            long j2 = this.lastVisibleTime;
            if (j2 == 0) {
                pw.print("0");
            } else {
                TimeUtils.formatDuration(j2, now, pw);
            }
            pw.println();
        }
        if (this.mDeferHidingClient) {
            pw.println(prefix + "mDeferHidingClient=" + this.mDeferHidingClient);
        }
        if (this.deferRelaunchUntilPaused || this.configChangeFlags != 0) {
            pw.print(prefix);
            pw.print("deferRelaunchUntilPaused=");
            pw.print(this.deferRelaunchUntilPaused);
            pw.print(" configChangeFlags=");
            pw.println(Integer.toHexString(this.configChangeFlags));
        }
        if (this.mServiceConnectionsHolder != null) {
            pw.print(prefix);
            pw.print("connections=");
            pw.println(this.mServiceConnectionsHolder);
        }
        if (this.info != null) {
            pw.println(prefix + "resizeMode=" + ActivityInfo.resizeModeToString(this.info.resizeMode));
            pw.println(prefix + "mLastReportedMultiWindowMode=" + this.mLastReportedMultiWindowMode + " mLastReportedPictureInPictureMode=" + this.mLastReportedPictureInPictureMode);
            if (this.info.supportsPictureInPicture()) {
                pw.println(prefix + "supportsPictureInPicture=" + this.info.supportsPictureInPicture());
                pw.println(prefix + "supportsEnterPipOnTaskSwitch: " + this.supportsEnterPipOnTaskSwitch);
            }
            if (this.info.maxAspectRatio != 0.0f) {
                pw.println(prefix + "maxAspectRatio=" + this.info.maxAspectRatio);
            }
            if (this.info.minAspectRatio != 0.0f) {
                pw.println(prefix + "minAspectRatio=" + this.info.minAspectRatio);
            }
        }
        pw.print(prefix);
        pw.print("realComponentName=");
        pw.println(this.realComponentName);
    }

    /* access modifiers changed from: package-private */
    public void updateApplicationInfo(ApplicationInfo aInfo) {
        this.appInfo = aInfo;
        this.info.applicationInfo = aInfo;
    }

    private boolean crossesHorizontalSizeThreshold(int firstDp, int secondDp) {
        return crossesSizeThreshold(this.mHorizontalSizeConfigurations, firstDp, secondDp);
    }

    private boolean crossesVerticalSizeThreshold(int firstDp, int secondDp) {
        return crossesSizeThreshold(this.mVerticalSizeConfigurations, firstDp, secondDp);
    }

    private boolean crossesSmallestSizeThreshold(int firstDp, int secondDp) {
        return crossesSizeThreshold(this.mSmallestSizeConfigurations, firstDp, secondDp);
    }

    private static boolean crossesSizeThreshold(int[] thresholds, int firstDp, int secondDp) {
        if (thresholds == null) {
            return false;
        }
        for (int i = thresholds.length - 1; i >= 0; i--) {
            int threshold = thresholds[i];
            if ((firstDp < threshold && secondDp >= threshold) || (firstDp >= threshold && secondDp < threshold)) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void setSizeConfigurations(int[] horizontalSizeConfiguration, int[] verticalSizeConfigurations, int[] smallestSizeConfigurations) {
        this.mHorizontalSizeConfigurations = horizontalSizeConfiguration;
        this.mVerticalSizeConfigurations = verticalSizeConfigurations;
        this.mSmallestSizeConfigurations = smallestSizeConfigurations;
    }

    private void scheduleActivityMovedToDisplay(int displayId, Configuration config) {
        if (attachedToProcess()) {
            try {
                this.mAtmService.getLifecycleManager().scheduleTransaction(this.app.getThread(), (IBinder) this.appToken, (ClientTransactionItem) MoveToDisplayItem.obtain(displayId, config));
            } catch (RemoteException e) {
            }
        }
    }

    private void scheduleConfigurationChanged(Configuration config) {
        if (attachedToProcess()) {
            try {
                this.mAtmService.getLifecycleManager().scheduleTransaction(this.app.getThread(), (IBinder) this.appToken, (ClientTransactionItem) ActivityConfigurationChangeItem.obtain(config));
            } catch (RemoteException e) {
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean scheduleTopResumedActivityChanged(boolean onTop) {
        if (!attachedToProcess()) {
            return false;
        }
        try {
            this.mAtmService.getLifecycleManager().scheduleTransaction(this.app.getThread(), (IBinder) this.appToken, (ClientTransactionItem) TopResumedActivityChangeItem.obtain(onTop));
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    /* access modifiers changed from: package-private */
    public void updateMultiWindowMode() {
        boolean inMultiWindowMode;
        TaskRecord taskRecord = this.task;
        if (taskRecord != null && taskRecord.getStack() != null && attachedToProcess() && !this.task.getStack().deferScheduleMultiWindowModeChanged() && (inMultiWindowMode = inMultiWindowMode()) != this.mLastReportedMultiWindowMode) {
            this.mLastReportedMultiWindowMode = inMultiWindowMode;
            scheduleMultiWindowModeChanged(getConfiguration());
        }
    }

    private void scheduleMultiWindowModeChanged(Configuration overrideConfig) {
        try {
            this.mAtmService.getLifecycleManager().scheduleTransaction(this.app.getThread(), (IBinder) this.appToken, (ClientTransactionItem) MultiWindowModeChangeItem.obtain(this.mLastReportedMultiWindowMode, overrideConfig));
        } catch (Exception e) {
        }
    }

    /* access modifiers changed from: package-private */
    public void updatePictureInPictureMode(Rect targetStackBounds, boolean forceUpdate) {
        TaskRecord taskRecord = this.task;
        if (taskRecord != null && taskRecord.getStack() != null && attachedToProcess()) {
            boolean inPictureInPictureMode = inPinnedWindowingMode() && targetStackBounds != null;
            if (inPictureInPictureMode != this.mLastReportedPictureInPictureMode || forceUpdate) {
                this.mLastReportedPictureInPictureMode = inPictureInPictureMode;
                this.mLastReportedMultiWindowMode = inPictureInPictureMode;
                Configuration newConfig = new Configuration();
                if (targetStackBounds != null && !targetStackBounds.isEmpty()) {
                    newConfig.setTo(this.task.getRequestedOverrideConfiguration());
                    Rect outBounds = newConfig.windowConfiguration.getBounds();
                    this.task.adjustForMinimalTaskDimensions(outBounds, outBounds);
                    TaskRecord taskRecord2 = this.task;
                    taskRecord2.computeConfigResourceOverrides(newConfig, taskRecord2.getParent().getConfiguration());
                }
                schedulePictureInPictureModeChanged(newConfig);
                scheduleMultiWindowModeChanged(newConfig);
            }
        }
    }

    private void schedulePictureInPictureModeChanged(Configuration overrideConfig) {
        try {
            this.mAtmService.getLifecycleManager().scheduleTransaction(this.app.getThread(), (IBinder) this.appToken, (ClientTransactionItem) PipModeChangeItem.obtain(this.mLastReportedPictureInPictureMode, overrideConfig));
        } catch (Exception e) {
        }
    }

    /* access modifiers changed from: protected */
    public int getChildCount() {
        return 0;
    }

    /* access modifiers changed from: protected */
    public ConfigurationContainer getChildAt(int index) {
        return null;
    }

    /* access modifiers changed from: protected */
    public ConfigurationContainer getParent() {
        return getTaskRecord();
    }

    /* access modifiers changed from: package-private */
    public TaskRecord getTaskRecord() {
        return this.task;
    }

    /* access modifiers changed from: package-private */
    public int getRealTheme() {
        return this.realTheme;
    }

    /* access modifiers changed from: package-private */
    public void setTask(TaskRecord task2) {
        setTask(task2, false);
    }

    /* access modifiers changed from: package-private */
    public void setTask(TaskRecord task2, boolean reparenting) {
        if (task2 == null || task2 != getTaskRecord()) {
            ActivityStack oldStack = getActivityStack();
            ActivityStack newStack = task2 != null ? task2.getStack() : null;
            if (oldStack != newStack) {
                if (!reparenting && oldStack != null) {
                    oldStack.onActivityRemovedFromStack(this);
                }
                if (newStack != null) {
                    newStack.onActivityAddedToStack(this);
                }
            }
            this.task = task2;
            if (!reparenting) {
                onParentChanged();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setWillCloseOrEnterPip(boolean willCloseOrEnterPip) {
        AppWindowToken appWindowToken = this.mAppWindowToken;
        if (appWindowToken != null) {
            appWindowToken.setWillCloseOrEnterPip(willCloseOrEnterPip);
        }
    }

    static class Token extends IApplicationToken.Stub {
        private final String name;
        private final WeakReference<ActivityRecord> weakActivity;

        Token(ActivityRecord activity, Intent intent) {
            this.weakActivity = new WeakReference<>(activity);
            this.name = intent.getComponent().flattenToShortString();
        }

        /* access modifiers changed from: private */
        public static ActivityRecord tokenToActivityRecordLocked(Token token) {
            ActivityRecord r;
            if (token == null || (r = (ActivityRecord) token.weakActivity.get()) == null || r.getActivityStack() == null) {
                return null;
            }
            return r;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(128);
            sb.append("Token{");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(' ');
            sb.append(this.weakActivity.get());
            sb.append('}');
            return sb.toString();
        }

        public String getName() {
            return this.name;
        }
    }

    static ActivityRecord forTokenLocked(IBinder token) {
        try {
            return Token.tokenToActivityRecordLocked((Token) token);
        } catch (ClassCastException e) {
            Slog.w("ActivityTaskManager", "Bad activity token: " + token, e);
            return null;
        }
    }

    static boolean isResolverActivity(String className) {
        return ResolverActivity.class.getName().equals(className);
    }

    /* access modifiers changed from: package-private */
    public boolean isResolverActivity() {
        return isResolverActivity(this.mActivityComponent.getClassName());
    }

    /* access modifiers changed from: package-private */
    public boolean isResolverOrChildActivity() {
        if (!PackageManagerService.PLATFORM_PACKAGE_NAME.equals(this.packageName)) {
            return false;
        }
        try {
            return ResolverActivity.class.isAssignableFrom(Object.class.getClassLoader().loadClass(this.mActivityComponent.getClassName()));
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    ActivityRecord(ActivityTaskManagerService _service, WindowProcessController _caller, int _launchedFromPid, int _launchedFromUid, String _launchedFromPackage, Intent _intent, String _resolvedType, ActivityInfo aInfo, Configuration _configuration, ActivityRecord _resultTo, String _resultWho, int _reqCode, boolean _componentSpecified, boolean _rootVoiceInteraction, ActivityStackSupervisor supervisor, ActivityOptions options, ActivityRecord sourceRecord) {
        int i;
        ActivityTaskManagerService activityTaskManagerService = _service;
        WindowProcessController windowProcessController = _caller;
        Intent intent2 = _intent;
        ActivityInfo activityInfo = aInfo;
        ActivityOptions activityOptions = options;
        this.mAtmService = activityTaskManagerService;
        this.mRootActivityContainer = activityTaskManagerService.mRootActivityContainer;
        this.appToken = new Token(this, intent2);
        this.info = activityInfo;
        this.launchedFromPid = _launchedFromPid;
        this.launchedFromUid = _launchedFromUid;
        this.launchedFromPackage = _launchedFromPackage;
        this.mUserId = UserHandle.getUserId(activityInfo.applicationInfo.uid);
        this.intent = intent2;
        this.shortComponentName = _intent.getComponent().flattenToShortString();
        this.realComponentName = this.shortComponentName;
        this.resolvedType = _resolvedType;
        this.componentSpecified = _componentSpecified;
        this.rootVoiceInteraction = _rootVoiceInteraction;
        this.mLastReportedConfiguration = new MergedConfiguration(_configuration);
        this.resultTo = _resultTo;
        this.resultWho = _resultWho;
        this.requestCode = _reqCode;
        setState(ActivityStack.ActivityState.INITIALIZING, "ActivityRecord ctor");
        this.frontOfTask = false;
        this.launchFailed = false;
        this.stopped = false;
        this.delayedResume = false;
        this.finishing = false;
        this.deferRelaunchUntilPaused = false;
        this.keysPaused = false;
        this.inHistory = false;
        this.visible = false;
        this.nowVisible = false;
        this.mDrawn = false;
        this.idle = false;
        this.hasBeenLaunched = false;
        this.mStackSupervisor = supervisor;
        this.haveState = true;
        if (activityInfo.targetActivity == null || (activityInfo.targetActivity.equals(_intent.getComponent().getClassName()) && (activityInfo.launchMode == 0 || activityInfo.launchMode == 1))) {
            this.mActivityComponent = _intent.getComponent();
        } else {
            this.mActivityComponent = new ComponentName(activityInfo.packageName, activityInfo.targetActivity);
        }
        this.taskAffinity = activityInfo.taskAffinity;
        this.stateNotNeeded = (activityInfo.flags & 16) != 0;
        this.appInfo = activityInfo.applicationInfo;
        this.nonLocalizedLabel = activityInfo.nonLocalizedLabel;
        this.labelRes = activityInfo.labelRes;
        if (this.nonLocalizedLabel == null && this.labelRes == 0) {
            ApplicationInfo app2 = activityInfo.applicationInfo;
            this.nonLocalizedLabel = app2.nonLocalizedLabel;
            this.labelRes = app2.labelRes;
        }
        this.icon = aInfo.getIconResource();
        this.logo = aInfo.getLogoResource();
        this.theme = aInfo.getThemeResource();
        this.realTheme = this.theme;
        if (this.realTheme == 0) {
            this.realTheme = activityInfo.applicationInfo.targetSdkVersion < 11 ? 16973829 : 16973931;
        }
        if ((activityInfo.flags & 512) != 0) {
            this.windowFlags |= DumpState.DUMP_SERVICE_PERMISSIONS;
        }
        if ((activityInfo.flags & 1) == 0 || windowProcessController == null || !(activityInfo.applicationInfo.uid == 1000 || activityInfo.applicationInfo.uid == windowProcessController.mInfo.uid)) {
            this.processName = activityInfo.processName;
        } else {
            this.processName = windowProcessController.mName;
        }
        if ((activityInfo.flags & 32) != 0) {
            this.intent.addFlags(DumpState.DUMP_VOLUMES);
        }
        this.packageName = activityInfo.applicationInfo.packageName;
        this.launchMode = activityInfo.launchMode;
        AttributeCache.Entry ent = AttributeCache.instance().get(this.packageName, this.realTheme, R.styleable.Window, this.mUserId);
        if (ent != null) {
            this.fullscreen = !ActivityInfo.isTranslucentOrFloating(ent.array);
            this.hasWallpaper = ent.array.getBoolean(14, false);
            this.noDisplay = ent.array.getBoolean(10, false);
        } else {
            this.hasWallpaper = false;
            this.noDisplay = false;
        }
        AttributeCache.Entry entry = ent;
        setActivityType(_componentSpecified, _launchedFromUid, _intent, options, sourceRecord);
        this.immersive = (activityInfo.flags & 2048) != 0;
        this.requestedVrComponent = activityInfo.requestedVrComponent == null ? null : ComponentName.unflattenFromString(activityInfo.requestedVrComponent);
        this.mShowWhenLocked = (activityInfo.flags & DumpState.DUMP_VOLUMES) != 0;
        this.mInheritShownWhenLocked = (activityInfo.privateFlags & 1) != 0;
        this.mTurnScreenOn = (activityInfo.flags & DumpState.DUMP_SERVICE_PERMISSIONS) != 0;
        this.mRotationAnimationHint = activityInfo.rotationAnimation;
        this.lockTaskLaunchMode = activityInfo.lockTaskLaunchMode;
        if (this.appInfo.isPrivilegedApp() && ((i = this.lockTaskLaunchMode) == 2 || i == 1)) {
            this.lockTaskLaunchMode = 0;
        }
        if (activityOptions != null) {
            this.pendingOptions = activityOptions;
            this.mLaunchTaskBehind = options.getLaunchTaskBehind();
            this.mMiuiConfigFlag = options.getMiuiConfigFlag();
            int rotationAnimation = this.pendingOptions.getRotationAnimationHint();
            if (rotationAnimation >= 0) {
                this.mRotationAnimationHint = rotationAnimation;
            }
            PendingIntent usageReport = this.pendingOptions.getUsageTimeReport();
            if (usageReport != null) {
                this.appTimeTracker = new AppTimeTracker(usageReport);
            }
            if (this.pendingOptions.getLockTaskMode() && this.lockTaskLaunchMode == 0) {
                this.lockTaskLaunchMode = 3;
            }
            this.mHandoverLaunchDisplayId = options.getLaunchDisplayId();
        }
        if (this.mPerf == null) {
            this.mPerf = new BoostFramework();
        }
        if (getActivityType() != 3 || !this.mLaunchTaskBehind) {
            this.mAtmService.mGestureController.mLaunchRecentsFromGesture = false;
        } else {
            this.mAtmService.mGestureController.mLaunchRecentsFromGesture = true;
            this.mAtmService.mGestureController.mHasResumeRecentsBehind = false;
            if (activityOptions != null) {
                this.mAtmService.mGestureController.setRecentsItemCoordinates(options.getStartX(), options.getStartY(), options.getWidth(), options.getHeight());
            }
        }
        if (activityOptions == null || !options.getLaunchActivityFromRoundedView()) {
            this.mThumbnail = null;
            this.mLunchedFromRoundedView = false;
            this.mAnimationReenterStartedCallback = null;
            this.mAnimationReenterFinishedCallback = null;
            return;
        }
        this.mStartX = options.getStartX();
        this.mStartY = options.getStartY();
        this.mWidth = options.getWidth();
        this.mHeight = options.getHeight();
        this.mRadius = options.getRadius();
        this.mForeGroundColor = options.getForeGroundColor();
        this.mThumbnail = options.getThumbnail();
        this.mAnimationReenterStartedCallback = options.getAnimationReenterStartedListener();
        this.mAnimationReenterFinishedCallback = options.getAnimationReenterFinishedListener();
        this.mLunchedFromRoundedView = true;
        this.mLaunchedOrientation = this.mLastReportedConfiguration.getGlobalConfiguration().orientation;
    }

    /* access modifiers changed from: package-private */
    public void setProcess(WindowProcessController proc) {
        this.app = proc;
        TaskRecord taskRecord = this.task;
        if ((taskRecord != null ? taskRecord.getRootActivity() : null) == this) {
            this.task.setRootProcess(proc);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean hasProcess() {
        return this.app != null;
    }

    /* access modifiers changed from: package-private */
    public boolean attachedToProcess() {
        return hasProcess() && this.app.hasThread();
    }

    /* access modifiers changed from: package-private */
    public void createAppWindowToken() {
        if (this.mAppWindowToken == null) {
            this.inHistory = true;
            this.task.updateOverrideConfigurationFromLaunchBounds();
            updateOverrideConfiguration();
            this.mAppWindowToken = this.mAtmService.mWindowManager.mRoot.getAppWindowToken(this.appToken.asBinder());
            if (this.mAppWindowToken != null) {
                Slog.w("ActivityTaskManager", "Attempted to add existing app token: " + this.appToken);
            } else {
                Task container = this.task.getTask();
                if (container != null) {
                    this.mAppWindowToken = createAppWindow(this.mAtmService.mWindowManager, this.appToken, this.task.voiceSession != null, container.getDisplayContent(), ActivityTaskManagerService.getInputDispatchingTimeoutLocked(this) * 1000000, this.fullscreen, (this.info.flags & 1024) != 0, this.appInfo.targetSdkVersion, this.info.screenOrientation, this.mRotationAnimationHint, this.mLaunchTaskBehind, isAlwaysFocusable());
                    container.addChild(this.mAppWindowToken, Integer.MAX_VALUE);
                } else {
                    throw new IllegalArgumentException("createAppWindowToken: invalid task =" + this.task);
                }
            }
            this.task.addActivityToTop(this);
            int i = this.mMiuiConfigFlag;
            if ((i & 1) == 0) {
                this.mAppWindowToken.setMiuiConfigFlag(i, true);
            }
            this.mLastReportedMultiWindowMode = inMultiWindowMode();
            this.mLastReportedPictureInPictureMode = inPinnedWindowingMode();
            return;
        }
        throw new IllegalArgumentException("App Window Token=" + this.mAppWindowToken + " already created for r=" + this);
    }

    /* access modifiers changed from: package-private */
    public boolean addStartingWindow(String pkg, int theme2, CompatibilityInfo compatInfo, CharSequence nonLocalizedLabel2, int labelRes2, int icon2, int logo2, int windowFlags2, IBinder transferFrom, boolean newTask, boolean taskSwitch, boolean processRunning, boolean allowTaskSnapshot, boolean activityCreated, boolean fromRecents) {
        AppWindowToken appWindowToken = this.mAppWindowToken;
        if (appWindowToken == null) {
            Slog.w(DisplayPolicy.TAG, "Attempted to set icon of non-existing app token: " + this.appToken);
            return false;
        } else if (appWindowToken.getTask() != null) {
            return this.mAppWindowToken.addStartingWindow(pkg, theme2, compatInfo, nonLocalizedLabel2, labelRes2, icon2, logo2, windowFlags2, transferFrom, newTask, taskSwitch, processRunning, allowTaskSnapshot, activityCreated, fromRecents);
        } else {
            Slog.w(DisplayPolicy.TAG, "Attempted to start a window to an app token not having attached to any task: " + this.appToken);
            return false;
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public AppWindowToken createAppWindow(WindowManagerService service, IApplicationToken token, boolean voiceInteraction, DisplayContent dc, long inputDispatchingTimeoutNanos, boolean fullscreen2, boolean showForAllUsers, int targetSdk, int orientation, int rotationAnimationHint, boolean launchTaskBehind, boolean alwaysFocusable) {
        return new AppWindowToken(service, token, this.mActivityComponent, voiceInteraction, dc, inputDispatchingTimeoutNanos, fullscreen2, showForAllUsers, targetSdk, orientation, rotationAnimationHint, launchTaskBehind, alwaysFocusable, this);
    }

    /* access modifiers changed from: package-private */
    public void removeWindowContainer() {
        if (this.mAtmService.mWindowManager.mRoot != null) {
            DisplayContent dc = this.mAtmService.mWindowManager.mRoot.getDisplayContent(getDisplayId());
            if (dc == null) {
                Slog.w("ActivityTaskManager", "removeWindowContainer: Attempted to remove token: " + this.appToken + " from non-existing displayId=" + getDisplayId());
                return;
            }
            resumeKeyDispatchingLocked();
            dc.removeAppToken(this.appToken.asBinder());
        }
    }

    /* access modifiers changed from: package-private */
    public void reparent(TaskRecord newTask, int position, String reason) {
        if (this.mAppWindowToken == null) {
            Slog.w("ActivityTaskManager", "reparent: Attempted to reparent non-existing app token: " + this.appToken);
            return;
        }
        TaskRecord prevTask = this.task;
        if (prevTask == newTask) {
            throw new IllegalArgumentException(reason + ": task=" + newTask + " is already the parent of r=" + this);
        } else if (prevTask == null || newTask == null || prevTask.getStack() == newTask.getStack()) {
            this.mAppWindowToken.reparent(newTask.getTask(), position);
            ActivityStack prevStack = prevTask.getStack();
            if (prevStack != newTask.getStack()) {
                prevStack.onActivityRemovedFromStack(this);
            }
            prevTask.removeActivity(this, true);
            newTask.addActivityAtIndex(position, this);
        } else {
            throw new IllegalArgumentException(reason + ": task=" + newTask + " is in a different stack (" + newTask.getStackId() + ") than the parent of r=" + this + " (" + prevTask.getStackId() + ")");
        }
    }

    private boolean isHomeIntent(Intent intent2) {
        return "android.intent.action.MAIN".equals(intent2.getAction()) && (intent2.hasCategory("android.intent.category.HOME") || intent2.hasCategory("android.intent.category.SECONDARY_HOME")) && intent2.getCategories().size() == 1 && intent2.getData() == null && intent2.getType() == null;
    }

    static boolean isMainIntent(Intent intent2) {
        if ("android.intent.action.MAIN".equals(intent2.getAction()) && intent2.hasCategory("android.intent.category.LAUNCHER") && intent2.getCategories().size() == 1 && intent2.getData() == null && intent2.getType() == null) {
            return true;
        }
        return false;
    }

    private boolean canLaunchHomeActivity(int uid, ActivityRecord sourceRecord) {
        if (uid == Process.myUid() || uid == 0) {
            return true;
        }
        RecentTasks recentTasks = this.mStackSupervisor.mService.getRecentTasks();
        if (recentTasks != null && recentTasks.isCallerRecents(uid)) {
            return true;
        }
        if (sourceRecord == null || !sourceRecord.isResolverActivity()) {
            return false;
        }
        return true;
    }

    private boolean canLaunchAssistActivity(String packageName2) {
        ComponentName assistComponent = this.mAtmService.mActiveVoiceInteractionServiceComponent;
        if (assistComponent != null) {
            return assistComponent.getPackageName().equals(packageName2);
        }
        return false;
    }

    private void setActivityType(boolean componentSpecified2, int launchedFromUid2, Intent intent2, ActivityOptions options, ActivityRecord sourceRecord) {
        int activityType = 0;
        boolean isHomeActivity = false;
        if (!(getDisplay() == null || getDisplay().getHomeActivity() == null)) {
            isHomeActivity = this.mActivityComponent.equals(getDisplay().getHomeActivity().mActivityComponent);
        }
        if (((!componentSpecified2 || canLaunchHomeActivity(launchedFromUid2, sourceRecord)) && isHomeIntent(intent2) && !isResolverActivity()) || isHomeActivity) {
            activityType = 2;
            if (this.info.resizeMode == 4 || this.info.resizeMode == 1) {
                this.info.resizeMode = 0;
            }
        } else if (this.mActivityComponent.getClassName().contains(LEGACY_RECENTS_PACKAGE_NAME) || this.mAtmService.getRecentTasks().isRecentsComponent(this.mActivityComponent, this.appInfo.uid)) {
            activityType = 3;
        } else if (options != null && options.getLaunchActivityType() == 4 && canLaunchAssistActivity(this.launchedFromPackage)) {
            activityType = 4;
        }
        setActivityType(activityType);
    }

    /* access modifiers changed from: package-private */
    public void setTaskToAffiliateWith(TaskRecord taskToAffiliateWith) {
        int i = this.launchMode;
        if (i != 3 && i != 2) {
            this.task.setTaskToAffiliateWith(taskToAffiliateWith);
        }
    }

    /* access modifiers changed from: package-private */
    public <T extends ActivityStack> T getActivityStack() {
        TaskRecord taskRecord = this.task;
        if (taskRecord != null) {
            return taskRecord.getStack();
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public int getStackId() {
        if (getActivityStack() != null) {
            return getActivityStack().mStackId;
        }
        return -1;
    }

    /* access modifiers changed from: package-private */
    public ActivityDisplay getDisplay() {
        ActivityStack stack = getActivityStack();
        if (stack != null) {
            return stack.getDisplay();
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public boolean changeWindowTranslucency(boolean toOpaque) {
        if (this.fullscreen == toOpaque) {
            return false;
        }
        this.task.numFullscreen += toOpaque ? 1 : -1;
        this.fullscreen = toOpaque;
        return true;
    }

    /* access modifiers changed from: package-private */
    public void takeFromHistory() {
        if (this.inHistory) {
            this.inHistory = false;
            if (this.task != null && !this.finishing) {
                this.task = null;
            }
            clearOptionsLocked();
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isInHistory() {
        return this.inHistory;
    }

    /* access modifiers changed from: package-private */
    public boolean isInStackLocked() {
        ActivityStack stack = getActivityStack();
        return (stack == null || stack.isInStackLocked(this) == null) ? false : true;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:4:0x000d, code lost:
        r0 = r2.intent;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isPersistable() {
        /*
            r2 = this;
            android.content.pm.ActivityInfo r0 = r2.info
            int r0 = r0.persistableMode
            if (r0 == 0) goto L_0x000d
            android.content.pm.ActivityInfo r0 = r2.info
            int r0 = r0.persistableMode
            r1 = 2
            if (r0 != r1) goto L_0x001b
        L_0x000d:
            android.content.Intent r0 = r2.intent
            if (r0 == 0) goto L_0x001d
            int r0 = r0.getFlags()
            r1 = 8388608(0x800000, float:1.17549435E-38)
            r0 = r0 & r1
            if (r0 != 0) goto L_0x001b
            goto L_0x001d
        L_0x001b:
            r0 = 0
            goto L_0x001e
        L_0x001d:
            r0 = 1
        L_0x001e:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityRecord.isPersistable():boolean");
    }

    /* access modifiers changed from: package-private */
    public boolean isFocusable() {
        return this.mRootActivityContainer.isFocusable(this, isAlwaysFocusable());
    }

    /* access modifiers changed from: package-private */
    public boolean isResizeable() {
        return ActivityInfo.isResizeableMode(this.info.resizeMode) || this.info.supportsPictureInPicture() || supportsFreeform();
    }

    /* access modifiers changed from: package-private */
    public boolean isNonResizableOrForcedResizable() {
        return (this.info.resizeMode == 2 || this.info.resizeMode == 1) ? false : true;
    }

    /* access modifiers changed from: package-private */
    public boolean supportsPictureInPicture() {
        return this.mAtmService.mSupportsPictureInPicture && isActivityTypeStandardOrUndefined() && this.info.supportsPictureInPicture();
    }

    public boolean supportsSplitScreenWindowingMode() {
        return super.supportsSplitScreenWindowingMode() && this.mAtmService.mSupportsSplitScreenMultiWindow && supportsResizeableMultiWindow();
    }

    /* access modifiers changed from: package-private */
    public boolean supportsFreeform() {
        return this.mAtmService.mSupportsFreeformWindowManagement && this.mAtmService.mSupportsMultiWindow && !isActivityTypeHome() && (this.mAtmService.mForceResizableActivities || ActivityStackSupervisorInjector.supportsFreeform());
    }

    private boolean supportsResizeableMultiWindow() {
        return this.mAtmService.mSupportsMultiWindow && !isActivityTypeHome() && (ActivityInfo.isResizeableMode(this.info.resizeMode) || this.mAtmService.mForceResizableActivities || ActivityManagerServiceInjector.inResizeWhiteList(this.packageName)) && !ActivityManagerServiceInjector.inResizeBlackList(this.packageName);
    }

    /* access modifiers changed from: package-private */
    public boolean canBeLaunchedOnDisplay(int displayId) {
        return this.mAtmService.mStackSupervisor.canPlaceEntityOnDisplay(displayId, this.launchedFromPid, this.launchedFromUid, this.info);
    }

    /* access modifiers changed from: package-private */
    public boolean checkEnterPictureInPictureState(String caller, boolean beforeStopping) {
        if (!supportsPictureInPicture() || !checkEnterPictureInPictureAppOpsState() || this.mAtmService.shouldDisableNonVrUiLocked()) {
            return false;
        }
        boolean isKeyguardLocked = this.mAtmService.isKeyguardLocked();
        boolean isCurrentAppLocked = this.mAtmService.getLockTaskModeState() != 0;
        ActivityDisplay display = getDisplay();
        boolean hasPinnedStack = display != null && display.hasPinnedStack();
        boolean isNotLockedOrOnKeyguard = !isKeyguardLocked && !isCurrentAppLocked;
        if (beforeStopping && hasPinnedStack) {
            return false;
        }
        int i = AnonymousClass1.$SwitchMap$com$android$server$wm$ActivityStack$ActivityState[this.mState.ordinal()];
        if (i != 1) {
            if (i == 2 || i == 3) {
                if (!isNotLockedOrOnKeyguard || hasPinnedStack || !this.supportsEnterPipOnTaskSwitch) {
                    return false;
                }
                return true;
            } else if (i == 4 && this.supportsEnterPipOnTaskSwitch && isNotLockedOrOnKeyguard && !hasPinnedStack) {
                return true;
            } else {
                return false;
            }
        } else if (isCurrentAppLocked) {
            return false;
        } else {
            if (this.supportsEnterPipOnTaskSwitch || !beforeStopping) {
                return true;
            }
            return false;
        }
    }

    /* renamed from: com.android.server.wm.ActivityRecord$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$server$wm$ActivityStack$ActivityState = new int[ActivityStack.ActivityState.values().length];

        static {
            try {
                $SwitchMap$com$android$server$wm$ActivityStack$ActivityState[ActivityStack.ActivityState.RESUMED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$android$server$wm$ActivityStack$ActivityState[ActivityStack.ActivityState.PAUSING.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$android$server$wm$ActivityStack$ActivityState[ActivityStack.ActivityState.PAUSED.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$android$server$wm$ActivityStack$ActivityState[ActivityStack.ActivityState.STOPPING.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    private boolean checkEnterPictureInPictureAppOpsState() {
        return this.mAtmService.getAppOpsService().checkOperation(67, this.appInfo.uid, this.packageName) == 0;
    }

    /* access modifiers changed from: package-private */
    public boolean isAlwaysFocusable() {
        return (this.info.flags & DumpState.DUMP_DOMAIN_PREFERRED) != 0;
    }

    /* access modifiers changed from: package-private */
    public boolean moveFocusableActivityToTop(String reason) {
        if (!isFocusable()) {
            return false;
        }
        TaskRecord task2 = getTaskRecord();
        ActivityStack stack = getActivityStack();
        if (stack == null) {
            Slog.w("ActivityTaskManager", "moveActivityStackToFront: invalid task or stack: activity=" + this + " task=" + task2);
            return false;
        } else if (this.mRootActivityContainer.getTopResumedActivity() == this) {
            return false;
        } else {
            stack.moveToFront(reason, task2);
            if (this.mRootActivityContainer.getTopResumedActivity() != this) {
                return true;
            }
            this.mAtmService.setResumedActivityUncheckLocked(this, reason);
            return true;
        }
    }

    /* access modifiers changed from: package-private */
    public void makeFinishingLocked() {
        if (!this.finishing) {
            this.finishing = true;
            if (this.stopped) {
                clearOptionsLocked();
            }
            ActivityTaskManagerService activityTaskManagerService = this.mAtmService;
            if (activityTaskManagerService != null) {
                activityTaskManagerService.getTaskChangeNotificationController().notifyTaskStackChanged();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public UriPermissionOwner getUriPermissionsLocked() {
        if (this.uriPermissions == null) {
            this.uriPermissions = new UriPermissionOwner(this.mAtmService.mUgmInternal, this);
        }
        return this.uriPermissions;
    }

    /* access modifiers changed from: package-private */
    public void addResultLocked(ActivityRecord from, String resultWho2, int requestCode2, int resultCode, Intent resultData) {
        ActivityResult r = new ActivityResult(from, resultWho2, requestCode2, resultCode, resultData);
        if (this.results == null) {
            this.results = new ArrayList<>();
        }
        this.results.add(r);
    }

    /* access modifiers changed from: package-private */
    public void removeResultsLocked(ActivityRecord from, String resultWho2, int requestCode2) {
        ArrayList<ResultInfo> arrayList = this.results;
        if (arrayList != null) {
            for (int i = arrayList.size() - 1; i >= 0; i--) {
                ActivityResult r = this.results.get(i);
                if (r.mFrom == from) {
                    if (r.mResultWho == null) {
                        if (resultWho2 != null) {
                        }
                    } else if (!r.mResultWho.equals(resultWho2)) {
                    }
                    if (r.mRequestCode == requestCode2) {
                        this.results.remove(i);
                    }
                }
            }
        }
    }

    private void addNewIntentLocked(ReferrerIntent intent2) {
        if (this.newIntents == null) {
            this.newIntents = new ArrayList<>();
        }
        this.newIntents.add(intent2);
    }

    /* access modifiers changed from: package-private */
    public final boolean isSleeping() {
        ActivityStack stack = getActivityStack();
        return stack != null ? stack.shouldSleepActivities() : this.mAtmService.isSleepingLocked();
    }

    /* access modifiers changed from: package-private */
    public final void deliverNewIntentLocked(int callingUid, Intent intent2, String referrer) {
        this.mAtmService.mUgmInternal.grantUriPermissionFromIntent(callingUid, this.packageName, intent2, getUriPermissionsLocked(), this.mUserId);
        ReferrerIntent rintent = new ReferrerIntent(intent2, referrer);
        boolean unsent = true;
        boolean z = false;
        boolean isTopActivityWhileSleeping = isTopRunningActivity() && isSleeping();
        if ((this.mState == ActivityStack.ActivityState.RESUMED || this.mState == ActivityStack.ActivityState.PAUSED || isTopActivityWhileSleeping) && attachedToProcess()) {
            try {
                ArrayList<ReferrerIntent> ar = new ArrayList<>(1);
                ar.add(rintent);
                ClientLifecycleManager lifecycleManager = this.mAtmService.getLifecycleManager();
                IApplicationThread thread = this.app.getThread();
                IApplicationToken.Stub stub = this.appToken;
                if (this.mState == ActivityStack.ActivityState.RESUMED) {
                    z = true;
                }
                lifecycleManager.scheduleTransaction(thread, (IBinder) stub, (ClientTransactionItem) NewIntentItem.obtain(ar, z));
                unsent = false;
            } catch (RemoteException e) {
                Slog.w("ActivityTaskManager", "Exception thrown sending new intent to " + this, e);
            } catch (NullPointerException e2) {
                Slog.w("ActivityTaskManager", "Exception thrown sending new intent to " + this, e2);
            }
        }
        if (unsent) {
            addNewIntentLocked(rintent);
        }
    }

    /* access modifiers changed from: package-private */
    public void updateOptionsLocked(ActivityOptions options) {
        if (options != null) {
            ActivityOptions activityOptions = this.pendingOptions;
            if (activityOptions != null) {
                activityOptions.abort();
            }
            this.pendingOptions = options;
        }
    }

    /* access modifiers changed from: package-private */
    public void applyOptionsLocked() {
        ActivityOptions activityOptions = this.pendingOptions;
        if (activityOptions != null && activityOptions.getAnimationType() != 5) {
            applyOptionsLocked(this.pendingOptions, this.intent);
            TaskRecord taskRecord = this.task;
            if (taskRecord == null) {
                clearOptionsLocked(false);
            } else {
                taskRecord.clearAllPendingOptions();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void applyOptionsLocked(ActivityOptions pendingOptions2, Intent intent2) {
        Intent intent3 = intent2;
        int animationType = pendingOptions2.getAnimationType();
        DisplayContent displayContent = this.mAppWindowToken.getDisplayContent();
        if (animationType != 0) {
            boolean z = true;
            if (animationType == 1) {
                displayContent.mAppTransition.overridePendingAppTransition(pendingOptions2.getPackageName(), pendingOptions2.getCustomEnterResId(), pendingOptions2.getCustomExitResId(), pendingOptions2.getOnAnimationStartListener());
            } else if (animationType == 2) {
                displayContent.mAppTransition.overridePendingAppTransitionScaleUp(pendingOptions2.getStartX(), pendingOptions2.getStartY(), pendingOptions2.getWidth(), pendingOptions2.getHeight());
                if (intent2.getSourceBounds() == null) {
                    intent3.setSourceBounds(new Rect(pendingOptions2.getStartX(), pendingOptions2.getStartY(), pendingOptions2.getStartX() + pendingOptions2.getWidth(), pendingOptions2.getStartY() + pendingOptions2.getHeight()));
                }
            } else if (animationType == 3 || animationType == 4) {
                boolean scaleUp = animationType == 3;
                GraphicBuffer buffer = pendingOptions2.getThumbnail();
                displayContent.mAppTransition.overridePendingAppTransitionThumb(buffer, pendingOptions2.getStartX(), pendingOptions2.getStartY(), pendingOptions2.getOnAnimationStartListener(), scaleUp);
                if (intent2.getSourceBounds() == null && buffer != null) {
                    intent3.setSourceBounds(new Rect(pendingOptions2.getStartX(), pendingOptions2.getStartY(), pendingOptions2.getStartX() + buffer.getWidth(), pendingOptions2.getStartY() + buffer.getHeight()));
                }
            } else if (animationType == 8 || animationType == 9) {
                AppTransitionAnimationSpec[] specs = pendingOptions2.getAnimSpecs();
                IAppTransitionAnimationSpecsFuture specsFuture = pendingOptions2.getSpecsFuture();
                if (specsFuture != null) {
                    AppTransition appTransition = displayContent.mAppTransition;
                    IRemoteCallback onAnimationStartListener = pendingOptions2.getOnAnimationStartListener();
                    if (animationType != 8) {
                        z = false;
                    }
                    appTransition.overridePendingAppTransitionMultiThumbFuture(specsFuture, onAnimationStartListener, z);
                } else if (animationType != 9 || specs == null) {
                    displayContent.mAppTransition.overridePendingAppTransitionAspectScaledThumb(pendingOptions2.getThumbnail(), pendingOptions2.getStartX(), pendingOptions2.getStartY(), pendingOptions2.getWidth(), pendingOptions2.getHeight(), pendingOptions2.getOnAnimationStartListener(), animationType == 8);
                    if (intent2.getSourceBounds() == null) {
                        intent3.setSourceBounds(new Rect(pendingOptions2.getStartX(), pendingOptions2.getStartY(), pendingOptions2.getStartX() + pendingOptions2.getWidth(), pendingOptions2.getStartY() + pendingOptions2.getHeight()));
                    }
                } else {
                    displayContent.mAppTransition.overridePendingAppTransitionMultiThumb(specs, pendingOptions2.getOnAnimationStartListener(), pendingOptions2.getAnimationFinishedListener(), false);
                }
            } else if (animationType == 100) {
                displayContent.mAppTransition.overridePendingAppTransitionLaunchFromHome(pendingOptions2.getStartX(), pendingOptions2.getStartY(), pendingOptions2.getWidth(), pendingOptions2.getHeight());
                if (intent2.getSourceBounds() == null) {
                    intent3.setSourceBounds(new Rect(pendingOptions2.getStartX(), pendingOptions2.getStartY(), pendingOptions2.getStartX() + pendingOptions2.getWidth(), pendingOptions2.getStartY() + pendingOptions2.getHeight()));
                }
            } else if (animationType != 102) {
                switch (animationType) {
                    case 11:
                        displayContent.mAppTransition.overridePendingAppTransitionClipReveal(pendingOptions2.getStartX(), pendingOptions2.getStartY(), pendingOptions2.getWidth(), pendingOptions2.getHeight());
                        if (intent2.getSourceBounds() == null) {
                            intent3.setSourceBounds(new Rect(pendingOptions2.getStartX(), pendingOptions2.getStartY(), pendingOptions2.getStartX() + pendingOptions2.getWidth(), pendingOptions2.getStartY() + pendingOptions2.getHeight()));
                            return;
                        }
                        return;
                    case 12:
                        displayContent.mAppTransition.overridePendingAppTransitionStartCrossProfileApps();
                        return;
                    case 13:
                        displayContent.mAppTransition.overridePendingAppTransitionRemote(pendingOptions2.getRemoteAnimationAdapter());
                        return;
                    default:
                        Slog.e(DisplayPolicy.TAG, "applyOptionsLocked: Unknown animationType=" + animationType);
                        return;
                }
            } else {
                displayContent.mAppTransition.overridePendingActivityTransitionFromRoundedView(pendingOptions2.getScaledStartX(), pendingOptions2.getScaledStartY(), pendingOptions2.getScaledWidth(), pendingOptions2.getScaledHeight(), pendingOptions2.getScaledRadius(), pendingOptions2.getForeGroundColor(), pendingOptions2.getScaledThumbnail(), pendingOptions2.getOnAnimationStartListener(), pendingOptions2.getAnimationFinishedListener());
                if (intent2.getSourceBounds() == null) {
                    intent3.setSourceBounds(new Rect(pendingOptions2.getStartX(), pendingOptions2.getStartY(), pendingOptions2.getStartX() + pendingOptions2.getWidth(), pendingOptions2.getStartY() + pendingOptions2.getHeight()));
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public ActivityOptions getOptionsForTargetActivityLocked() {
        ActivityOptions activityOptions = this.pendingOptions;
        if (activityOptions != null) {
            return activityOptions.forTargetActivity();
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public void clearOptionsLocked() {
        clearOptionsLocked(true);
    }

    /* access modifiers changed from: package-private */
    public void clearOptionsLocked(boolean withAbort) {
        ActivityOptions activityOptions;
        if (withAbort && (activityOptions = this.pendingOptions) != null) {
            activityOptions.abort();
        }
        this.pendingOptions = null;
    }

    /* access modifiers changed from: package-private */
    public ActivityOptions takeOptionsLocked(boolean fromClient) {
        ActivityOptions opts = this.pendingOptions;
        if (!fromClient || opts == null || opts.getRemoteAnimationAdapter() == null) {
            this.pendingOptions = null;
        }
        return opts;
    }

    /* access modifiers changed from: package-private */
    public void removeUriPermissionsLocked() {
        UriPermissionOwner uriPermissionOwner = this.uriPermissions;
        if (uriPermissionOwner != null) {
            uriPermissionOwner.removeUriPermissions();
            this.uriPermissions = null;
        }
    }

    /* access modifiers changed from: package-private */
    public void pauseKeyDispatchingLocked() {
        if (!this.keysPaused) {
            this.keysPaused = true;
            AppWindowToken appWindowToken = this.mAppWindowToken;
            if (appWindowToken != null && appWindowToken.getDisplayContent() != null) {
                this.mAppWindowToken.getDisplayContent().getInputMonitor().pauseDispatchingLw(this.mAppWindowToken);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void resumeKeyDispatchingLocked() {
        if (this.keysPaused) {
            this.keysPaused = false;
            AppWindowToken appWindowToken = this.mAppWindowToken;
            if (appWindowToken != null && appWindowToken.getDisplayContent() != null) {
                this.mAppWindowToken.getDisplayContent().getInputMonitor().resumeDispatchingLw(this.mAppWindowToken);
            }
        }
    }

    private void updateTaskDescription(CharSequence description) {
        this.task.lastDescription = description;
    }

    /* access modifiers changed from: package-private */
    public void setDeferHidingClient(boolean deferHidingClient) {
        if (this.mDeferHidingClient != deferHidingClient) {
            this.mDeferHidingClient = deferHidingClient;
            if (!this.mDeferHidingClient && !this.visible) {
                setVisibility(false);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setVisibility(boolean visible2) {
        AppWindowToken appWindowToken = this.mAppWindowToken;
        if (appWindowToken == null) {
            Slog.w(DisplayPolicy.TAG, "Attempted to set visibility of non-existing app token: " + this.appToken);
            return;
        }
        appWindowToken.setVisibility(visible2, this.mDeferHidingClient);
        this.mStackSupervisor.getActivityMetricsLogger().notifyVisibilityChanged(this);
    }

    /* access modifiers changed from: package-private */
    public void setDummyVisible(boolean dummyVisible, boolean reallyVisible) {
        if (this.mIsDummyVisible != dummyVisible) {
            this.mIsDummyVisible = dummyVisible;
            AppWindowToken appWindowToken = this.mAppWindowToken;
            if (appWindowToken != null) {
                appWindowToken.setDummyVisible(dummyVisible, reallyVisible, this.visible);
            }
        }
    }

    public void setCastMode(boolean enterCast) {
        AppWindowToken appWindowToken;
        if (this.mIsCastMode != enterCast && (appWindowToken = this.mAppWindowToken) != null) {
            this.mIsCastMode = enterCast;
            appWindowToken.setCastMode(enterCast);
        }
    }

    /* access modifiers changed from: package-private */
    public void setLastFrame(boolean isLastFrame) {
        if (this.mIsLastFrame != isLastFrame) {
            this.mIsLastFrame = isLastFrame;
            this.mAtmService.mWindowManager.setLastFrame(isLastFrame);
        }
    }

    /* access modifiers changed from: package-private */
    public void setVisible(boolean newVisible) {
        this.visible = newVisible;
        this.mDeferHidingClient = !this.visible && this.mDeferHidingClient;
        setVisibility(this.visible);
        this.mStackSupervisor.mAppVisibilitiesChangedSinceLastPause = true;
    }

    /* access modifiers changed from: package-private */
    public void setState(ActivityStack.ActivityState state, String reason) {
        if (state != this.mState) {
            this.mState = state;
            TaskRecord parent = getTaskRecord();
            if (parent != null) {
                parent.onActivityStateChanged(this, state, reason);
            }
            if (state == ActivityStack.ActivityState.STOPPING && !isSleeping()) {
                AppWindowToken appWindowToken = this.mAppWindowToken;
                if (appWindowToken == null) {
                    Slog.w(DisplayPolicy.TAG, "Attempted to notify stopping on non-existing app token: " + this.appToken);
                    return;
                }
                appWindowToken.detachChildren();
            }
            if (state == ActivityStack.ActivityState.RESUMED) {
                this.mAtmService.updateBatteryStats(this, true);
                this.mAtmService.updateActivityUsageStats(this, 1);
            } else if (state == ActivityStack.ActivityState.PAUSED) {
                this.mAtmService.updateBatteryStats(this, false);
                this.mAtmService.updateActivityUsageStats(this, 2);
            } else if (state == ActivityStack.ActivityState.STOPPED) {
                this.mAtmService.updateActivityUsageStats(this, 23);
            } else if (state == ActivityStack.ActivityState.DESTROYED) {
                this.mAtmService.updateActivityUsageStats(this, 24);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public ActivityStack.ActivityState getState() {
        return this.mState;
    }

    /* access modifiers changed from: package-private */
    public boolean isState(ActivityStack.ActivityState state) {
        return state == this.mState;
    }

    /* access modifiers changed from: package-private */
    public boolean isState(ActivityStack.ActivityState state1, ActivityStack.ActivityState state2) {
        ActivityStack.ActivityState activityState = this.mState;
        return state1 == activityState || state2 == activityState;
    }

    /* access modifiers changed from: package-private */
    public boolean isState(ActivityStack.ActivityState state1, ActivityStack.ActivityState state2, ActivityStack.ActivityState state3) {
        ActivityStack.ActivityState activityState = this.mState;
        return state1 == activityState || state2 == activityState || state3 == activityState;
    }

    /* access modifiers changed from: package-private */
    public boolean isState(ActivityStack.ActivityState state1, ActivityStack.ActivityState state2, ActivityStack.ActivityState state3, ActivityStack.ActivityState state4) {
        ActivityStack.ActivityState activityState = this.mState;
        return state1 == activityState || state2 == activityState || state3 == activityState || state4 == activityState;
    }

    /* access modifiers changed from: package-private */
    public void notifyAppResumed(boolean wasStopped) {
        AppWindowToken appWindowToken = this.mAppWindowToken;
        if (appWindowToken == null) {
            Slog.w(DisplayPolicy.TAG, "Attempted to notify resumed of non-existing app token: " + this.appToken);
            return;
        }
        appWindowToken.notifyAppResumed(wasStopped);
    }

    /* access modifiers changed from: package-private */
    public void notifyUnknownVisibilityLaunched() {
        AppWindowToken appWindowToken;
        if (!this.noDisplay && (appWindowToken = this.mAppWindowToken) != null) {
            appWindowToken.getDisplayContent().mUnknownAppVisibilityController.notifyLaunched(this.mAppWindowToken);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean shouldBeVisibleIgnoringKeyguard(boolean behindFullscreenActivity) {
        if (!okToShowLocked()) {
            return false;
        }
        if (!behindFullscreenActivity || this.mLaunchTaskBehind || (getActivityType() == 3 && this.mAtmService.mGestureController.mLaunchRecentsFromGesture && !this.mAtmService.mGestureController.mStopLaunchRecentsBehind)) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean shouldBeVisible(boolean behindFullscreenActivity) {
        this.visibleIgnoringKeyguard = shouldBeVisibleIgnoringKeyguard(behindFullscreenActivity);
        ActivityStack stack = getActivityStack();
        if (stack == null) {
            return false;
        }
        boolean isDisplaySleeping = getDisplay().isSleeping() && getDisplayId() != 0;
        if (!stack.checkKeyguardVisibility(this, this.visibleIgnoringKeyguard, (this == stack.getTopActivity()) && (stack.isAttached() && stack.getDisplay().isTopNotPinnedStack(stack))) || isDisplaySleeping) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean shouldBeVisible() {
        ActivityStack stack = getActivityStack();
        if (stack == null) {
            return false;
        }
        return shouldBeVisible(!stack.shouldBeVisible((ActivityRecord) null));
    }

    /* access modifiers changed from: package-private */
    public void makeVisibleIfNeeded(ActivityRecord starting, boolean reportToClient) {
        if (this.mIsCastMode || !(this.mState == ActivityStack.ActivityState.RESUMED || this == starting)) {
            ActivityStack stack = getActivityStack();
            try {
                if (stack.mTranslucentActivityWaiting != null) {
                    updateOptionsLocked(this.returningOptions);
                    stack.mUndrawnActivitiesBelowTopTranslucent.add(this);
                }
                setVisible(true);
                this.sleeping = false;
                this.app.postPendingUiCleanMsg(true);
                if (reportToClient) {
                    makeClientVisible();
                } else {
                    this.mClientVisibilityDeferred = true;
                }
                this.mStackSupervisor.mStoppingActivities.remove(this);
                this.mStackSupervisor.mGoingToSleepActivities.remove(this);
            } catch (Exception e) {
                Slog.w("ActivityTaskManager", "Exception thrown making visible: " + this.intent.getComponent(), e);
            }
            handleAlreadyVisible();
        }
    }

    /* access modifiers changed from: package-private */
    public void makeClientVisible() {
        this.mClientVisibilityDeferred = false;
        try {
            this.mAtmService.getLifecycleManager().scheduleTransaction(this.app.getThread(), (IBinder) this.appToken, (ClientTransactionItem) WindowVisibilityItem.obtain(true));
            makeActiveIfNeeded((ActivityRecord) null);
            if (isState(ActivityStack.ActivityState.STOPPING, ActivityStack.ActivityState.STOPPED) && isFocusable()) {
                setState(ActivityStack.ActivityState.PAUSED, "makeClientVisible");
            }
        } catch (Exception e) {
            Slog.w("ActivityTaskManager", "Exception thrown sending visibility update: " + this.intent.getComponent(), e);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean makeActiveIfNeeded(ActivityRecord activeActivity) {
        if (shouldResumeActivity(activeActivity)) {
            return getActivityStack().resumeTopActivityUncheckedLocked(activeActivity, (ActivityOptions) null);
        }
        if (shouldPauseActivity(activeActivity)) {
            setState(ActivityStack.ActivityState.PAUSING, "makeVisibleIfNeeded");
            try {
                this.mAtmService.getLifecycleManager().scheduleTransaction(this.app.getThread(), (IBinder) this.appToken, (ActivityLifecycleItem) PauseActivityItem.obtain(this.finishing, false, this.configChangeFlags, false));
            } catch (Exception e) {
                Slog.w("ActivityTaskManager", "Exception thrown sending pause: " + this.intent.getComponent(), e);
            }
        }
        return false;
    }

    private boolean shouldPauseActivity(ActivityRecord activeActivity) {
        return shouldMakeActive(activeActivity) && !isFocusable() && !isState(ActivityStack.ActivityState.PAUSING, ActivityStack.ActivityState.PAUSED);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean shouldResumeActivity(ActivityRecord activeActivity) {
        return shouldMakeActive(activeActivity) && isFocusable() && !isState(ActivityStack.ActivityState.RESUMED) && getActivityStack().getVisibility(activeActivity) == 0;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean shouldMakeActive(ActivityRecord activeActivity) {
        if (!isState(ActivityStack.ActivityState.RESUMED, ActivityStack.ActivityState.PAUSED, ActivityStack.ActivityState.STOPPED, ActivityStack.ActivityState.STOPPING) || getActivityStack().mTranslucentActivityWaiting != null || this == activeActivity || !this.mStackSupervisor.readyToResume() || this.mLaunchTaskBehind) {
            return false;
        }
        int positionInTask = this.task.mActivities.indexOf(this);
        if (positionInTask == -1) {
            throw new IllegalStateException("Activity not found in its task");
        } else if (positionInTask == this.task.mActivities.size() - 1) {
            return true;
        } else {
            if (!this.task.mActivities.get(positionInTask + 1).finishing || this.results != null) {
                return false;
            }
            return true;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean handleAlreadyVisible() {
        stopFreezingScreenLocked(false);
        try {
            if (this.returningOptions != null) {
                this.app.getThread().scheduleOnNewActivityOptions(this.appToken, this.returningOptions.toBundle());
            }
        } catch (RemoteException e) {
        }
        if (this.mState == ActivityStack.ActivityState.RESUMED) {
            return true;
        }
        return false;
    }

    static void activityResumedLocked(IBinder token) {
        ActivityRecord r = forTokenLocked(token);
        if (r != null) {
            r.icicle = null;
            r.haveState = false;
            ActivityDisplay display = r.getDisplay();
            if (display != null) {
                display.handleActivitySizeCompatModeIfNeeded(r);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void completeResumeLocked() {
        boolean wasVisible = this.visible;
        setVisible(true);
        if (!wasVisible) {
            this.mStackSupervisor.mAppVisibilitiesChangedSinceLastPause = true;
        }
        this.idle = false;
        this.results = null;
        this.newIntents = null;
        this.stopped = false;
        if (isActivityTypeHome()) {
            this.mStackSupervisor.updateHomeProcess(this.task.mActivities.get(0).app);
            try {
                ActivityStackSupervisor activityStackSupervisor = this.mStackSupervisor;
                Objects.requireNonNull(activityStackSupervisor);
                new ActivityStackSupervisor.PreferredAppsTask().execute(new Void[0]);
            } catch (Exception e) {
                Slog.v("ActivityTaskManager", "Exception: " + e);
            }
        }
        if (this.nowVisible) {
            this.mStackSupervisor.stopWaitingForActivityVisible(this);
        }
        this.mStackSupervisor.scheduleIdleTimeoutLocked(this);
        this.mStackSupervisor.reportResumedActivityLocked(this);
        resumeKeyDispatchingLocked();
        ActivityStack stack = getActivityStack();
        this.mStackSupervisor.mNoAnimActivities.clear();
        if (hasProcess()) {
            this.cpuTimeAtResume = this.app.getCpuTime();
        } else {
            this.cpuTimeAtResume = 0;
        }
        this.returningOptions = null;
        if (canTurnScreenOn()) {
            this.mStackSupervisor.wakeUp("turnScreenOnFlag");
        } else {
            stack.checkReadyForSleep();
        }
    }

    /* access modifiers changed from: package-private */
    public final void activityStoppedLocked(Bundle newIcicle, PersistableBundle newPersistentState, CharSequence description) {
        ActivityStack stack = getActivityStack();
        boolean isStopping = this.mState == ActivityStack.ActivityState.STOPPING;
        if (isStopping || this.mState == ActivityStack.ActivityState.RESTARTING_PROCESS) {
            if (newPersistentState != null) {
                this.persistentState = newPersistentState;
                this.mAtmService.notifyTaskPersisterLocked(this.task, false);
            }
            if (newIcicle != null) {
                this.icicle = newIcicle;
                this.haveState = true;
                this.launchCount = 0;
                updateTaskDescription(description);
            }
            if (!this.stopped) {
                stack.mHandler.removeMessages(HdmiCecKeycode.CEC_KEYCODE_SELECT_MEDIA_FUNCTION, this);
                this.stopped = true;
                if (isStopping) {
                    setState(ActivityStack.ActivityState.STOPPED, "activityStoppedLocked");
                }
                AppWindowToken appWindowToken = this.mAppWindowToken;
                if (appWindowToken != null) {
                    appWindowToken.notifyAppStopped();
                }
                if (this.finishing) {
                    clearOptionsLocked();
                } else if (this.deferRelaunchUntilPaused) {
                    stack.destroyActivityLocked(this, true, "stop-config");
                    this.mRootActivityContainer.resumeFocusedStacksTopActivities();
                } else {
                    this.mRootActivityContainer.updatePreviousProcess(this);
                }
            }
        } else {
            Slog.i("ActivityTaskManager", "Activity reported stop, but no longer stopping: " + this);
            stack.mHandler.removeMessages(HdmiCecKeycode.CEC_KEYCODE_SELECT_MEDIA_FUNCTION, this);
        }
    }

    /* access modifiers changed from: package-private */
    public void startLaunchTickingLocked() {
        if (!Build.IS_USER && this.launchTickTime == 0) {
            this.launchTickTime = SystemClock.uptimeMillis();
            continueLaunchTickingLocked();
        }
    }

    /* access modifiers changed from: package-private */
    public boolean continueLaunchTickingLocked() {
        ActivityStack stack;
        if (this.launchTickTime == 0 || (stack = getActivityStack()) == null) {
            return false;
        }
        Message msg = stack.mHandler.obtainMessage(103, this);
        stack.mHandler.removeMessages(103);
        stack.mHandler.sendMessageDelayed(msg, 500);
        return true;
    }

    /* access modifiers changed from: package-private */
    public void finishLaunchTickingLocked() {
        this.launchTickTime = 0;
        ActivityStack stack = getActivityStack();
        if (stack != null) {
            stack.mHandler.removeMessages(103);
        }
    }

    public boolean mayFreezeScreenLocked(WindowProcessController app2) {
        return hasProcess() && !app2.isCrashing() && !app2.isNotResponding();
    }

    public void startFreezingScreenLocked(WindowProcessController app2, int configChanges) {
        if (mayFreezeScreenLocked(app2)) {
            AppWindowToken appWindowToken = this.mAppWindowToken;
            if (appWindowToken == null) {
                Slog.w(DisplayPolicy.TAG, "Attempted to freeze screen with non-existing app token: " + this.appToken);
            } else if ((-536870913 & configChanges) != 0 || !appWindowToken.okToDisplay()) {
                this.mAppWindowToken.startFreezingScreen();
            }
        }
    }

    public void stopFreezingScreenLocked(boolean force) {
        if (force || this.frozenBeforeDestroy) {
            this.frozenBeforeDestroy = false;
            AppWindowToken appWindowToken = this.mAppWindowToken;
            if (appWindowToken != null) {
                appWindowToken.stopFreezingScreen(true, force);
            }
        }
    }

    public void reportFullyDrawnLocked(boolean restoredFromBundle) {
        ActivityMetricsLogger.WindowingModeTransitionInfoSnapshot info2 = this.mStackSupervisor.getActivityMetricsLogger().logAppTransitionReportedDrawn(this, restoredFromBundle);
        if (info2 != null) {
            this.mStackSupervisor.reportActivityLaunchedLocked(false, this, (long) info2.windowsFullyDrawnDelayMs, info2.getLaunchState());
        }
    }

    public int isAppInfoGame() {
        ApplicationInfo applicationInfo = this.appInfo;
        if (applicationInfo == null) {
            return 0;
        }
        return (applicationInfo.category == 0 || (this.appInfo.flags & DumpState.DUMP_APEX) == 33554432) ? 1 : 0;
    }

    public void onStartingWindowDrawn(long timestamp) {
        synchronized (this.mAtmService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mStackSupervisor.getActivityMetricsLogger().notifyStartingWindowDrawn(getWindowingMode(), timestamp);
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0047, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x004a, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onWindowsDrawn(boolean r11, long r12) {
        /*
            r10 = this;
            com.android.server.wm.ActivityTaskManagerService r0 = r10.mAtmService
            com.android.server.wm.WindowManagerGlobalLock r0 = r0.mGlobalLock
            monitor-enter(r0)
            com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x004b }
            r10.mDrawn = r11     // Catch:{ all -> 0x004b }
            if (r11 != 0) goto L_0x0011
            monitor-exit(r0)     // Catch:{ all -> 0x004b }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return
        L_0x0011:
            com.android.server.wm.ActivityStackSupervisor r1 = r10.mStackSupervisor     // Catch:{ all -> 0x004b }
            com.android.server.wm.ActivityMetricsLogger r1 = r1.getActivityMetricsLogger()     // Catch:{ all -> 0x004b }
            int r2 = r10.getWindowingMode()     // Catch:{ all -> 0x004b }
            com.android.server.wm.ActivityMetricsLogger$WindowingModeTransitionInfoSnapshot r1 = r1.notifyWindowsDrawn(r2, r12)     // Catch:{ all -> 0x004b }
            r2 = -1
            if (r1 == 0) goto L_0x0025
            int r3 = r1.windowsDrawnDelayMs     // Catch:{ all -> 0x004b }
            goto L_0x0026
        L_0x0025:
            r3 = r2
        L_0x0026:
            if (r1 == 0) goto L_0x002c
            int r2 = r1.getLaunchState()     // Catch:{ all -> 0x004b }
        L_0x002c:
            r9 = r2
            com.android.server.wm.ActivityStackSupervisor r4 = r10.mStackSupervisor     // Catch:{ all -> 0x004b }
            r5 = 0
            long r7 = (long) r3     // Catch:{ all -> 0x004b }
            r6 = r10
            r4.reportActivityLaunchedLocked(r5, r6, r7, r9)     // Catch:{ all -> 0x004b }
            com.android.server.wm.ActivityStackSupervisor r2 = r10.mStackSupervisor     // Catch:{ all -> 0x004b }
            r2.stopWaitingForActivityVisible(r10)     // Catch:{ all -> 0x004b }
            r10.finishLaunchTickingLocked()     // Catch:{ all -> 0x004b }
            com.android.server.wm.TaskRecord r2 = r10.task     // Catch:{ all -> 0x004b }
            if (r2 == 0) goto L_0x0046
            com.android.server.wm.TaskRecord r2 = r10.task     // Catch:{ all -> 0x004b }
            r4 = 1
            r2.hasBeenVisible = r4     // Catch:{ all -> 0x004b }
        L_0x0046:
            monitor-exit(r0)     // Catch:{ all -> 0x004b }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return
        L_0x004b:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x004b }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityRecord.onWindowsDrawn(boolean, long):void");
    }

    public void onWindowsVisible() {
        synchronized (this.mAtmService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mStackSupervisor.stopWaitingForActivityVisible(this);
                if (!this.nowVisible) {
                    this.nowVisible = true;
                    this.launching = false;
                    this.lastVisibleTime = SystemClock.uptimeMillis();
                    this.mAtmService.scheduleAppGcsLocked();
                }
                if (!(this.app == null || this.info == null)) {
                    ((ProcessManagerInternal) LocalServices.getService(ProcessManagerInternal.class)).notifyForegroundWindowChanged(this, this.mState, this.app.getPid(), this.info.applicationInfo);
                }
                Slog.i("Timeline", "Timeline: Activity_windows_visible id: " + this + " time:" + SystemClock.uptimeMillis());
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void onWindowsGone() {
        synchronized (this.mAtmService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.nowVisible = false;
                this.launching = false;
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
    public void onAnimationFinished() {
        if (!this.mRootActivityContainer.allResumedActivitiesIdle() && !this.mStackSupervisor.isStoppingNoHistoryActivity()) {
            this.mStackSupervisor.processStoppingActivitiesLocked((ActivityRecord) null, false, true);
        } else if (this.mStackSupervisor.mStoppingActivities.contains(this)) {
            this.mStackSupervisor.scheduleIdleLocked();
        }
    }

    public boolean keyDispatchingTimedOut(String reason, int windowPid) {
        ActivityRecord anrActivity;
        WindowProcessController anrApp;
        boolean windowFromSameProcessAsActivity;
        synchronized (this.mAtmService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                anrActivity = getWaitingHistoryRecordLocked();
                anrApp = this.app;
                if (hasProcess() && this.app.getPid() != windowPid) {
                    if (windowPid != -1) {
                        windowFromSameProcessAsActivity = false;
                    }
                }
                windowFromSameProcessAsActivity = true;
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        if (windowFromSameProcessAsActivity) {
            return this.mAtmService.mAmInternal.inputDispatchingTimedOut(anrApp.mOwner, anrActivity.shortComponentName, anrActivity.appInfo, this.shortComponentName, this.app, false, reason);
        }
        if (this.mAtmService.mAmInternal.inputDispatchingTimedOut(windowPid, false, reason) < 0) {
            return true;
        }
        return false;
    }

    private ActivityRecord getWaitingHistoryRecordLocked() {
        if (this.stopped) {
            ActivityStack stack = this.mRootActivityContainer.getTopDisplayFocusedStack();
            ActivityRecord r = stack.getResumedActivity();
            if (r == null) {
                r = stack.mPausingActivity;
            }
            if (r != null) {
                return r;
            }
        }
        return this;
    }

    public boolean okToShowLocked() {
        if (!StorageManager.isUserKeyUnlocked(this.mUserId) && !this.info.applicationInfo.isEncryptionAware()) {
            return false;
        }
        if ((this.info.flags & 1024) != 0 || (this.mStackSupervisor.isCurrentProfileLocked(this.mUserId) && this.mAtmService.mAmInternal.isUserRunning(this.mUserId, 0))) {
            return true;
        }
        return false;
    }

    public boolean isInterestingToUserLocked() {
        return this.visible || this.nowVisible || this.mState == ActivityStack.ActivityState.PAUSING || this.mState == ActivityStack.ActivityState.RESUMED;
    }

    /* access modifiers changed from: package-private */
    public void setSleeping(boolean _sleeping) {
        setSleeping(_sleeping, false);
    }

    /* access modifiers changed from: package-private */
    public void setSleeping(boolean _sleeping, boolean force) {
        if ((force || this.sleeping != _sleeping) && attachedToProcess()) {
            try {
                this.app.getThread().scheduleSleeping(this.appToken, _sleeping);
                if (_sleeping && !this.mStackSupervisor.mGoingToSleepActivities.contains(this)) {
                    this.mStackSupervisor.mGoingToSleepActivities.add(this);
                }
                this.sleeping = _sleeping;
            } catch (RemoteException e) {
                Slog.w("ActivityTaskManager", "Exception thrown when sleeping: " + this.intent.getComponent(), e);
            }
        }
    }

    static int getTaskForActivityLocked(IBinder token, boolean onlyRoot) {
        ActivityRecord r = forTokenLocked(token);
        if (r == null) {
            return -1;
        }
        TaskRecord task2 = r.task;
        int activityNdx = task2.mActivities.indexOf(r);
        if (activityNdx < 0 || (onlyRoot && activityNdx > task2.findEffectiveRootIndex())) {
            return -1;
        }
        return task2.taskId;
    }

    static ActivityRecord isInStackLocked(IBinder token) {
        ActivityRecord r = forTokenLocked(token);
        if (r != null) {
            return r.getActivityStack().isInStackLocked(r);
        }
        return null;
    }

    static ActivityStack getStackLocked(IBinder token) {
        ActivityRecord r = isInStackLocked(token);
        if (r != null) {
            return r.getActivityStack();
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public int getDisplayId() {
        ActivityStack stack = getActivityStack();
        if (stack == null) {
            return -1;
        }
        return stack.mDisplayId;
    }

    /* access modifiers changed from: package-private */
    public final boolean isDestroyable() {
        ActivityStack stack;
        if (this.finishing || !hasProcess() || (stack = getActivityStack()) == null || this == stack.getResumedActivity() || this == stack.mPausingActivity || !this.haveState || !this.stopped || this.visible) {
            return false;
        }
        return true;
    }

    private static String createImageFilename(long createTime2, int taskId) {
        return String.valueOf(taskId) + ACTIVITY_ICON_SUFFIX + createTime2 + ".png";
    }

    /* access modifiers changed from: package-private */
    public void setTaskDescription(ActivityManager.TaskDescription _taskDescription) {
        if (_taskDescription.getIconFilename() == null) {
            Bitmap icon2 = _taskDescription.getIcon();
            Bitmap icon3 = icon2;
            if (icon2 != null) {
                String iconFilePath = new File(TaskPersister.getUserImagesDir(this.task.userId), createImageFilename(this.createTime, this.task.taskId)).getAbsolutePath();
                this.mAtmService.getRecentTasks().saveImage(icon3, iconFilePath);
                _taskDescription.setIconFilename(iconFilePath);
            }
        }
        this.taskDescription = _taskDescription;
    }

    /* access modifiers changed from: package-private */
    public void setVoiceSessionLocked(IVoiceInteractionSession session) {
        this.voiceSession = session;
        this.pendingVoiceInteractionStart = false;
    }

    /* access modifiers changed from: package-private */
    public void clearVoiceSessionLocked() {
        this.voiceSession = null;
        this.pendingVoiceInteractionStart = false;
    }

    /* access modifiers changed from: package-private */
    public void showStartingWindow(ActivityRecord prev, boolean newTask, boolean taskSwitch) {
        showStartingWindow(prev, newTask, taskSwitch, false);
    }

    /* access modifiers changed from: package-private */
    public void showStartingWindow(ActivityRecord prev, boolean newTask, boolean taskSwitch, boolean fromRecents) {
        int optionalLabelRes;
        ActivityRecord activityRecord = prev;
        if (this.mAppWindowToken != null && !this.mTaskOverlay) {
            ActivityOptions activityOptions = this.pendingOptions;
            if (activityOptions == null || activityOptions.getAnimationType() != 5) {
                CharSequence label = ActivityStackInjector.getStartingWindowLabel(this, this.mStackSupervisor.mService.mContext);
                if (label == null) {
                    optionalLabelRes = ActivityStackInjector.getStartingWindowLabelRes(this, this.mStackSupervisor.mService.mContext);
                } else {
                    optionalLabelRes = 0;
                }
                if (addStartingWindow(this.packageName, this.theme, this.mAtmService.compatibilityInfoForPackageLocked(this.info.applicationInfo), label == null ? this.nonLocalizedLabel : label, label == null ? optionalLabelRes == 0 ? this.labelRes : optionalLabelRes : 0, this.icon, this.logo, this.windowFlags, activityRecord != null ? activityRecord.appToken : null, newTask, taskSwitch, isProcessRunning(), allowTaskSnapshot(), this.mState.ordinal() >= ActivityStack.ActivityState.RESUMED.ordinal() && this.mState.ordinal() <= ActivityStack.ActivityState.STOPPED.ordinal(), fromRecents)) {
                    this.mStartingWindowState = 1;
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void removeOrphanedStartingWindow(boolean behindFullscreenActivity) {
        if (this.mStartingWindowState == 1 && behindFullscreenActivity) {
            this.mStartingWindowState = 2;
            this.mAppWindowToken.removeStartingWindow();
        }
    }

    /* access modifiers changed from: package-private */
    public void setRequestedOrientation(int requestedOrientation) {
        if (inFreeformWindowingMode() && MiuiMultiWindowAdapter.getFreeformVideoWhiteList(this.mAtmService.mContext).contains(this.packageName) && this.mAtmService.mWindowManager.mMiuiFreeFormGestureController != null) {
            this.mAtmService.mWindowManager.mMiuiFreeFormGestureController.setRequestedOrientation(requestedOrientation, this.task);
        }
        setOrientation(requestedOrientation, mayFreezeScreenLocked(this.app));
        this.mAtmService.getTaskChangeNotificationController().notifyActivityRequestedOrientationChanged(this.task.taskId, requestedOrientation);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:5:0x001f, code lost:
        r0 = r3.appToken;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setOrientation(int r4, boolean r5) {
        /*
            r3 = this;
            com.android.server.wm.AppWindowToken r0 = r3.mAppWindowToken
            if (r0 != 0) goto L_0x001d
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "Attempted to set orientation of non-existing app token: "
            r0.append(r1)
            android.view.IApplicationToken$Stub r1 = r3.appToken
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "WindowManager"
            android.util.Slog.w(r1, r0)
            return
        L_0x001d:
            if (r5 == 0) goto L_0x0028
            android.view.IApplicationToken$Stub r0 = r3.appToken
            if (r0 == 0) goto L_0x0028
            android.os.IBinder r0 = r0.asBinder()
            goto L_0x0029
        L_0x0028:
            r0 = 0
        L_0x0029:
            com.android.server.wm.AppWindowToken r1 = r3.mAppWindowToken
            r1.setOrientation(r4, r0, r3)
            android.content.res.Configuration r1 = r3.getMergedOverrideConfiguration()
            android.util.MergedConfiguration r2 = r3.mLastReportedConfiguration
            android.content.res.Configuration r2 = r2.getMergedConfiguration()
            boolean r1 = r1.equals(r2)
            if (r1 != 0) goto L_0x0042
            r1 = 0
            r3.ensureActivityConfiguration(r1, r1)
        L_0x0042:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityRecord.setOrientation(int, boolean):void");
    }

    /* access modifiers changed from: package-private */
    public int getOrientation() {
        AppWindowToken appWindowToken = this.mAppWindowToken;
        if (appWindowToken == null) {
            return this.info.screenOrientation;
        }
        return appWindowToken.getOrientationIgnoreVisibility();
    }

    /* access modifiers changed from: package-private */
    public void setDisablePreviewScreenshots(boolean disable) {
        AppWindowToken appWindowToken = this.mAppWindowToken;
        if (appWindowToken == null) {
            Slog.w(DisplayPolicy.TAG, "Attempted to set disable screenshots of non-existing app token: " + this.appToken);
            return;
        }
        appWindowToken.setDisablePreviewScreenshots(disable);
    }

    /* access modifiers changed from: package-private */
    public void setLastReportedGlobalConfiguration(Configuration config) {
        this.mLastReportedConfiguration.setGlobalConfiguration(config);
    }

    /* access modifiers changed from: package-private */
    public void setLastReportedConfiguration(MergedConfiguration config) {
        setLastReportedConfiguration(config.getGlobalConfiguration(), config.getOverrideConfiguration());
    }

    private void setLastReportedConfiguration(Configuration global, Configuration override) {
        this.mLastReportedConfiguration.setConfiguration(global, override);
    }

    /* access modifiers changed from: package-private */
    public int getRequestedConfigurationOrientation() {
        int screenOrientation = getOrientation();
        if (screenOrientation == 5) {
            ActivityDisplay display = getDisplay();
            if (display == null || display.mDisplayContent == null) {
                return 0;
            }
            return display.mDisplayContent.getNaturalOrientation();
        } else if (screenOrientation == 14) {
            return getConfiguration().orientation;
        } else {
            if (ActivityInfo.isFixedOrientationLandscape(screenOrientation)) {
                return 2;
            }
            if (ActivityInfo.isFixedOrientationPortrait(screenOrientation)) {
                return 1;
            }
            return 0;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean inSizeCompatMode() {
        if (!shouldUseSizeCompatMode()) {
            return false;
        }
        Configuration resolvedConfig = getResolvedOverrideConfiguration();
        Rect resolvedAppBounds = resolvedConfig.windowConfiguration.getAppBounds();
        if (resolvedAppBounds == null) {
            return false;
        }
        Configuration parentConfig = getParent().getConfiguration();
        if (parentConfig.densityDpi != resolvedConfig.densityDpi) {
            return true;
        }
        Rect parentAppBounds = parentConfig.windowConfiguration.getAppBounds();
        int appWidth = resolvedAppBounds.width();
        int appHeight = resolvedAppBounds.height();
        int parentAppWidth = parentAppBounds.width();
        int parentAppHeight = parentAppBounds.height();
        if (parentAppWidth == appWidth && parentAppHeight == appHeight) {
            return false;
        }
        if ((parentAppWidth > appWidth && parentAppHeight > appHeight) || parentAppWidth < appWidth || parentAppHeight < appHeight) {
            return true;
        }
        if (this.info.maxAspectRatio > 0.0f && (((float) Math.max(appWidth, appHeight)) + 0.5f) / ((float) Math.min(appWidth, appHeight)) >= this.info.maxAspectRatio) {
            return false;
        }
        if (this.info.minAspectRatio <= 0.0f || (((float) Math.max(parentAppWidth, parentAppHeight)) + 0.5f) / ((float) Math.min(parentAppWidth, parentAppHeight)) > this.info.minAspectRatio) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean shouldUseSizeCompatMode() {
        return !isResizeable() && (this.info.isFixedOrientation() || this.info.hasFixedAspectRatio()) && isActivityTypeStandard() && !this.mAtmService.mForceResizableActivities && shouldUseSizeCompatModeByMiui();
    }

    private void updateOverrideConfiguration() {
        Configuration overrideConfig = this.mTmpConfig;
        if (!shouldUseSizeCompatMode()) {
            computeBounds(this.mTmpBounds, getParent().getWindowConfiguration().getAppBounds());
            if (!this.mTmpBounds.equals(getRequestedOverrideBounds())) {
                overrideConfig.unset();
                overrideConfig.windowConfiguration.setBounds(this.mTmpBounds);
            } else {
                return;
            }
        } else if (this.mCompatDisplayInsets == null) {
            Configuration parentConfig = getParent().getConfiguration();
            if (hasProcess() || isConfigurationCompatible(parentConfig)) {
                overrideConfig.unset();
                overrideConfig.colorMode = parentConfig.colorMode;
                overrideConfig.densityDpi = parentConfig.densityDpi;
                overrideConfig.screenLayout = parentConfig.screenLayout & 63;
                overrideConfig.smallestScreenWidthDp = parentConfig.smallestScreenWidthDp;
                ActivityDisplay display = getDisplay();
                if (!(display == null || display.mDisplayContent == null)) {
                    this.mCompatDisplayInsets = new CompatDisplayInsets(display.mDisplayContent);
                }
            } else {
                return;
            }
        } else {
            return;
        }
        onRequestedOverrideConfigurationChanged(overrideConfig);
    }

    /* access modifiers changed from: package-private */
    public void resolveOverrideConfiguration(Configuration newParentConfiguration) {
        if (this.mCompatDisplayInsets != null) {
            resolveSizeCompatModeConfiguration(newParentConfiguration);
        } else {
            super.resolveOverrideConfiguration(newParentConfiguration);
            if (!matchParentBounds()) {
                this.task.computeConfigResourceOverrides(getResolvedOverrideConfiguration(), newParentConfiguration);
            }
        }
        int i = this.mConfigurationSeq + 1;
        this.mConfigurationSeq = i;
        this.mConfigurationSeq = Math.max(i, 1);
        getResolvedOverrideConfiguration().seq = this.mConfigurationSeq;
    }

    private void resolveSizeCompatModeConfiguration(Configuration newParentConfiguration) {
        Configuration resolvedConfig = getResolvedOverrideConfiguration();
        Rect resolvedBounds = resolvedConfig.windowConfiguration.getBounds();
        int parentRotation = newParentConfiguration.windowConfiguration.getRotation();
        int parentOrientation = newParentConfiguration.orientation;
        int orientation = getConfiguration().orientation;
        if (orientation != parentOrientation && isConfigurationCompatible(newParentConfiguration)) {
            orientation = parentOrientation;
        } else if (resolvedBounds.isEmpty() || getWindowConfiguration().getRotation() != parentRotation) {
            int requestedOrientation = getRequestedConfigurationOrientation();
            if (requestedOrientation != 0) {
                orientation = requestedOrientation;
            }
        } else {
            return;
        }
        super.resolveOverrideConfiguration(newParentConfiguration);
        boolean useParentOverrideBounds = false;
        Rect displayBounds = this.mTmpBounds;
        Rect containingAppBounds = new Rect();
        if (this.task.handlesOrientationChangeFromDescendant()) {
            this.mCompatDisplayInsets.getDisplayBoundsByOrientation(displayBounds, orientation);
        } else {
            int baseOrientation = this.task.getParent().getConfiguration().orientation;
            this.mCompatDisplayInsets.getDisplayBoundsByOrientation(displayBounds, baseOrientation);
            this.task.computeFullscreenBounds(containingAppBounds, this, displayBounds, baseOrientation);
            useParentOverrideBounds = !containingAppBounds.isEmpty();
        }
        int containingOffsetX = containingAppBounds.left;
        int containingOffsetY = containingAppBounds.top;
        if (!useParentOverrideBounds) {
            containingAppBounds.set(displayBounds);
        }
        if (parentRotation != -1) {
            TaskRecord.intersectWithInsetsIfFits(containingAppBounds, displayBounds, this.mCompatDisplayInsets.mNonDecorInsets[parentRotation]);
        }
        computeBounds(resolvedBounds, containingAppBounds);
        if (resolvedBounds.isEmpty()) {
            resolvedBounds.set(useParentOverrideBounds ? containingAppBounds : displayBounds);
        } else {
            resolvedBounds.left += containingOffsetX;
            resolvedBounds.top += containingOffsetY;
        }
        this.task.computeConfigResourceOverrides(resolvedConfig, newParentConfiguration, this.mCompatDisplayInsets);
        Rect resolvedAppBounds = resolvedConfig.windowConfiguration.getAppBounds();
        if (resolvedBounds.width() < newParentConfiguration.windowConfiguration.getAppBounds().width()) {
            resolvedBounds.right -= resolvedAppBounds.left;
        }
        if (resolvedConfig.screenWidthDp == resolvedConfig.screenHeightDp) {
            resolvedConfig.orientation = newParentConfiguration.orientation;
        }
    }

    public void onConfigurationChanged(Configuration newParentConfig) {
        super.onConfigurationChanged(newParentConfig);
        if (getMergedOverrideConfiguration().seq != getResolvedOverrideConfiguration().seq) {
            onMergedOverrideConfigurationChanged();
        }
        AppWindowToken appWindowToken = this.mAppWindowToken;
        if (appWindowToken != null) {
            Configuration appWindowTokenRequestedOverrideConfig = appWindowToken.getRequestedOverrideConfiguration();
            if (appWindowTokenRequestedOverrideConfig.seq != getResolvedOverrideConfiguration().seq) {
                appWindowTokenRequestedOverrideConfig.seq = getResolvedOverrideConfiguration().seq;
                this.mAppWindowToken.onMergedOverrideConfigurationChanged();
            }
            ActivityDisplay display = getDisplay();
            if (display != null) {
                if (this.visible) {
                    display.handleActivitySizeCompatModeIfNeeded(this);
                } else if (shouldUseSizeCompatMode()) {
                    int displayChanges = display.getLastOverrideConfigurationChanges();
                    if ((hasResizeChange(displayChanges) && (displayChanges & 536872064) != 536872064) || (displayChanges & 4096) != 0) {
                        restartProcessIfVisible();
                    }
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isConfigurationCompatible(Configuration config) {
        int orientation = getOrientation();
        if (!ActivityInfo.isFixedOrientationPortrait(orientation) || config.orientation == 1) {
            return !ActivityInfo.isFixedOrientationLandscape(orientation) || config.orientation == 2;
        }
        return false;
    }

    private void computeBounds(Rect outBounds, Rect containingAppBounds) {
        Rect overrideBounds;
        boolean adjustWidth;
        Rect rect = outBounds;
        Rect rect2 = containingAppBounds;
        outBounds.setEmpty();
        float maxAspectRatio = this.info.maxAspectRatio;
        ActivityStack stack = getActivityStack();
        float minAspectRatio = this.info.minAspectRatio;
        TaskRecord taskRecord = this.task;
        if (taskRecord != null && stack != null && !taskRecord.inMultiWindowMode()) {
            if (!(maxAspectRatio == 0.0f && minAspectRatio == 0.0f) && !isInVrUiMode(getConfiguration()) && MiuiInit.isRestrictAspect(this.info.packageName)) {
                ActivityDisplay display = getDisplay();
                if (display != null) {
                    Rect displayBounds = display.getBounds();
                    if (!(!SystemProperties.getBoolean("persist.sys.miui_optimization", !SplitScreenReporter.ACTION_ENTER_SPLIT.equals(SystemProperties.get("ro.miui.cts")))) && displayBounds != null) {
                        float displayRatio = ((float) Math.max(displayBounds.width(), displayBounds.height())) / ((float) Math.min(displayBounds.width(), displayBounds.height()));
                        if (displayRatio > maxAspectRatio && maxAspectRatio != 0.0f) {
                            maxAspectRatio = displayRatio;
                        }
                    }
                }
                int containingAppWidth = containingAppBounds.width();
                int containingAppHeight = containingAppBounds.height();
                float containingRatio = ((float) Math.max(containingAppWidth, containingAppHeight)) / ((float) Math.min(containingAppWidth, containingAppHeight));
                int activityWidth = containingAppWidth;
                int activityHeight = containingAppHeight;
                if (containingRatio <= maxAspectRatio || maxAspectRatio == 0.0f) {
                    if (containingRatio < minAspectRatio) {
                        int requestedConfigurationOrientation = getRequestedConfigurationOrientation();
                        if (requestedConfigurationOrientation == 1) {
                            adjustWidth = true;
                        } else if (requestedConfigurationOrientation == 2) {
                            adjustWidth = false;
                        } else if (containingAppWidth < containingAppHeight) {
                            adjustWidth = true;
                        } else {
                            adjustWidth = false;
                        }
                        if (adjustWidth) {
                            activityWidth = (int) ((((float) activityHeight) / minAspectRatio) + 0.5f);
                        } else {
                            activityHeight = (int) ((((float) activityWidth) / minAspectRatio) + 0.5f);
                        }
                    }
                } else if (containingAppWidth < containingAppHeight) {
                    activityHeight = (int) ((((float) activityWidth) * maxAspectRatio) + 0.5f);
                } else {
                    activityWidth = (int) ((((float) activityHeight) * maxAspectRatio) + 0.5f);
                }
                if (containingAppWidth <= activityWidth && containingAppHeight <= activityHeight && (overrideBounds = getRequestedOverrideBounds()) != null) {
                    if ((overrideBounds.width() <= overrideBounds.height() ? 1 : 2) == (containingAppWidth <= containingAppHeight ? 1 : 2)) {
                        if (overrideBounds.equals(rect2)) {
                            rect.set(0, 0, 0, 0);
                            return;
                        } else {
                            rect.set(overrideBounds);
                            return;
                        }
                    }
                }
                rect.set(0, 0, rect2.left + activityWidth, rect2.top + activityHeight);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean shouldUpdateConfigForDisplayChanged() {
        return this.mLastReportedDisplayId != getDisplayId();
    }

    /* access modifiers changed from: package-private */
    public boolean ensureActivityConfiguration(int globalChanges, boolean preserveWindow) {
        return ensureActivityConfiguration(globalChanges, preserveWindow, false);
    }

    /* access modifiers changed from: package-private */
    public boolean ensureActivityConfiguration(int globalChanges, boolean preserveWindow, boolean ignoreStopState) {
        int i;
        ActivityStack stack = getActivityStack();
        if (stack.mConfigWillChange) {
            return true;
        }
        if (this.finishing) {
            stopFreezingScreenLocked(false);
            return true;
        } else if (!ignoreStopState && (this.mState == ActivityStack.ActivityState.STOPPING || this.mState == ActivityStack.ActivityState.STOPPED)) {
            return true;
        } else {
            if (!shouldBeVisible() && !this.mAtmService.mGestureController.isRecentsStackLaunchBehind(stack)) {
                return true;
            }
            int newDisplayId = getDisplayId();
            boolean displayChanged = this.mLastReportedDisplayId != newDisplayId;
            if (displayChanged) {
                this.mLastReportedDisplayId = newDisplayId;
            }
            updateOverrideConfiguration();
            this.mTmpConfig.setTo(this.mLastReportedConfiguration.getMergedConfiguration());
            if (getConfiguration().equals(this.mTmpConfig) && !this.forceNewConfig && !displayChanged) {
                return true;
            }
            int changes = getConfigurationChanges(this.mTmpConfig);
            Configuration newMergedOverrideConfig = getMergedOverrideConfiguration();
            setLastReportedConfiguration(this.mAtmService.getGlobalConfiguration(), newMergedOverrideConfig);
            if (this.mState == ActivityStack.ActivityState.INITIALIZING) {
                return true;
            }
            if (changes == 0 && !this.forceNewConfig) {
                if (displayChanged) {
                    scheduleActivityMovedToDisplay(newDisplayId, newMergedOverrideConfig);
                } else {
                    scheduleConfigurationChanged(newMergedOverrideConfig);
                }
                return true;
            } else if (!attachedToProcess()) {
                stopFreezingScreenLocked(false);
                this.forceNewConfig = false;
                return true;
            } else if (shouldRelaunchLocked(changes, this.mTmpConfig) || this.forceNewConfig) {
                this.configChangeFlags |= changes;
                startFreezingScreenLocked(this.app, globalChanges);
                this.forceNewConfig = false;
                boolean preserveWindow2 = preserveWindow & isResizeOnlyChange(changes);
                if (hasResizeChange((~this.info.getRealConfigChanged()) & changes)) {
                    if (getTaskRecord().getTask().isDragResizing()) {
                        i = 2;
                    } else {
                        i = 1;
                    }
                    this.mRelaunchReason = i;
                } else {
                    this.mRelaunchReason = 0;
                }
                if (!attachedToProcess()) {
                    stack.destroyActivityLocked(this, true, "config");
                } else if (this.mState == ActivityStack.ActivityState.PAUSING) {
                    this.deferRelaunchUntilPaused = true;
                    this.preserveWindowOnDeferredRelaunch = preserveWindow2;
                    return true;
                } else if (this.mState == ActivityStack.ActivityState.RESUMED) {
                    relaunchActivityLocked(true, preserveWindow2);
                } else {
                    relaunchActivityLocked(false, preserveWindow2);
                }
                return false;
            } else {
                if (displayChanged) {
                    scheduleActivityMovedToDisplay(newDisplayId, newMergedOverrideConfig);
                } else {
                    scheduleConfigurationChanged(newMergedOverrideConfig);
                }
                stopFreezingScreenLocked(false);
                return true;
            }
        }
    }

    private boolean shouldRelaunchLocked(int changes, Configuration changesConfig) {
        int configChanged = this.info.getRealConfigChanged();
        if (!MiuiMultiWindowAdapter.needRelunchFreeform(this.packageName, changesConfig, getConfiguration())) {
            configChanged |= UsbTerminalTypes.TERMINAL_EMBED_UNDEFINED;
        }
        boolean onlyVrUiModeChanged = onlyVrUiModeChanged(changes, changesConfig);
        if (this.appInfo.targetSdkVersion < 26 && this.requestedVrComponent != null && onlyVrUiModeChanged) {
            configChanged |= 512;
        }
        return ((~configChanged) & changes) != 0;
    }

    private boolean onlyVrUiModeChanged(int changes, Configuration lastReportedConfig) {
        return changes == 512 && isInVrUiMode(getConfiguration()) != isInVrUiMode(lastReportedConfig);
    }

    private int getConfigurationChanges(Configuration lastReportedConfig) {
        Configuration currentConfig = getConfiguration();
        int changes = lastReportedConfig.diff(currentConfig);
        if ((changes & 1024) != 0) {
            if (!(crossesHorizontalSizeThreshold(lastReportedConfig.screenWidthDp, currentConfig.screenWidthDp) || crossesVerticalSizeThreshold(lastReportedConfig.screenHeightDp, currentConfig.screenHeightDp))) {
                changes &= -1025;
            }
        }
        if ((changes != false && true) && !crossesSmallestSizeThreshold(lastReportedConfig.smallestScreenWidthDp, currentConfig.smallestScreenWidthDp)) {
            changes &= -2049;
        }
        if ((536870912 & changes) != 0) {
            return changes & -536870913;
        }
        return changes;
    }

    private static boolean isResizeOnlyChange(int change) {
        return (change & -3457) == 0;
    }

    private static boolean hasResizeChange(int change) {
        return (change & 3456) != 0;
    }

    /* access modifiers changed from: package-private */
    public void relaunchActivityLocked(boolean andResume, boolean preserveWindow) {
        int i;
        ActivityLifecycleItem lifecycleItem;
        if (!this.mAtmService.mSuppressResizeConfigChanges || !preserveWindow) {
            List<ResultInfo> pendingResults2 = null;
            List<ReferrerIntent> pendingNewIntents = null;
            if (andResume) {
                pendingResults2 = this.results;
                pendingNewIntents = this.newIntents;
            }
            if (andResume) {
                i = EventLogTags.AM_RELAUNCH_RESUME_ACTIVITY;
            } else {
                i = EventLogTags.AM_RELAUNCH_ACTIVITY;
            }
            Object[] objArr = new Object[4];
            WindowProcessController windowProcessController = this.app;
            objArr[0] = Integer.valueOf(windowProcessController != null ? windowProcessController.getPid() : 0);
            objArr[1] = Integer.valueOf(System.identityHashCode(this));
            objArr[2] = Integer.valueOf(this.task.taskId);
            objArr[3] = this.shortComponentName;
            EventLog.writeEvent(i, objArr);
            if (andResume) {
                ActivityTaskManagerServiceInjector.onForegroundActivityChangedLocked(this);
            }
            startFreezingScreenLocked(this.app, 0);
            try {
                this.forceNewConfig = false;
                this.mStackSupervisor.activityRelaunchingLocked(this);
                ClientTransactionItem callbackItem = ActivityRelaunchItem.obtain(pendingResults2, pendingNewIntents, this.configChangeFlags, new MergedConfiguration(this.mAtmService.getGlobalConfiguration(), getMergedOverrideConfiguration()), preserveWindow);
                if (andResume) {
                    lifecycleItem = ResumeActivityItem.obtain(getDisplay().mDisplayContent.isNextTransitionForward());
                } else {
                    lifecycleItem = PauseActivityItem.obtain();
                }
                ClientTransaction transaction = ClientTransaction.obtain(this.app.getThread(), this.appToken);
                transaction.addCallback(callbackItem);
                transaction.setLifecycleStateRequest(lifecycleItem);
                this.mAtmService.getLifecycleManager().scheduleTransaction(transaction);
            } catch (RemoteException e) {
            }
            if (andResume) {
                this.results = null;
                this.newIntents = null;
                this.mAtmService.getAppWarningsLocked().onResumeActivity(this);
            } else {
                ActivityStack stack = getActivityStack();
                if (stack != null) {
                    stack.mHandler.removeMessages(101, this);
                }
                setState(ActivityStack.ActivityState.PAUSED, "relaunchActivityLocked");
            }
            this.configChangeFlags = 0;
            this.deferRelaunchUntilPaused = false;
            this.preserveWindowOnDeferredRelaunch = false;
            return;
        }
        this.configChangeFlags = 0;
    }

    /* access modifiers changed from: package-private */
    public void restartProcessIfVisible() {
        Slog.i("ActivityTaskManager", "Request to restart process of " + this);
        getRequestedOverrideConfiguration().unset();
        getResolvedOverrideConfiguration().unset();
        this.mCompatDisplayInsets = null;
        if (this.visible) {
            updateOverrideConfiguration();
        }
        if (attachedToProcess()) {
            setState(ActivityStack.ActivityState.RESTARTING_PROCESS, "restartActivityProcess");
            if (!this.visible || this.haveState) {
                this.mAtmService.mH.post(new Runnable() {
                    public final void run() {
                        ActivityRecord.this.lambda$restartProcessIfVisible$0$ActivityRecord();
                    }
                });
                return;
            }
            AppWindowToken appWindowToken = this.mAppWindowToken;
            if (appWindowToken != null) {
                appWindowToken.startFreezingScreen();
            }
            try {
                this.mAtmService.getLifecycleManager().scheduleTransaction(this.app.getThread(), (IBinder) this.appToken, (ActivityLifecycleItem) StopActivityItem.obtain(false, 0));
            } catch (RemoteException e) {
                Slog.w("ActivityTaskManager", "Exception thrown during restart " + this, e);
            }
            this.mStackSupervisor.scheduleRestartTimeout(this);
        }
    }

    public /* synthetic */ void lambda$restartProcessIfVisible$0$ActivityRecord() {
        synchronized (this.mAtmService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                if (hasProcess()) {
                    if (this.app.getReportedProcState() > 7) {
                        WindowProcessController wpc = this.app;
                        WindowManagerService.resetPriorityAfterLockedSection();
                        this.mAtmService.mAmInternal.killProcess(wpc.mName, wpc.mUid, "resetConfig");
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v5, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v2, resolved type: com.android.server.wm.WindowProcessController} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean isProcessRunning() {
        /*
            r4 = this;
            com.android.server.wm.WindowProcessController r0 = r4.app
            if (r0 != 0) goto L_0x0017
            com.android.server.wm.ActivityTaskManagerService r1 = r4.mAtmService
            com.android.internal.app.ProcessMap<com.android.server.wm.WindowProcessController> r1 = r1.mProcessNames
            java.lang.String r2 = r4.processName
            android.content.pm.ActivityInfo r3 = r4.info
            android.content.pm.ApplicationInfo r3 = r3.applicationInfo
            int r3 = r3.uid
            java.lang.Object r1 = r1.get(r2, r3)
            r0 = r1
            com.android.server.wm.WindowProcessController r0 = (com.android.server.wm.WindowProcessController) r0
        L_0x0017:
            if (r0 == 0) goto L_0x0021
            boolean r1 = r0.hasThread()
            if (r1 == 0) goto L_0x0021
            r1 = 1
            goto L_0x0022
        L_0x0021:
            r1 = 0
        L_0x0022:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityRecord.isProcessRunning():boolean");
    }

    private boolean allowTaskSnapshot() {
        ArrayList<ReferrerIntent> arrayList = this.newIntents;
        if (arrayList == null) {
            return true;
        }
        for (int i = arrayList.size() - 1; i >= 0; i--) {
            Intent intent2 = this.newIntents.get(i);
            if (intent2 != null && !isMainIntent(intent2)) {
                return false;
            }
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean isNoHistory() {
        return ((this.intent.getFlags() & 1073741824) == 0 && (this.info.flags & 128) == 0) ? false : true;
    }

    /* access modifiers changed from: package-private */
    public void saveToXml(XmlSerializer out) throws IOException, XmlPullParserException {
        out.attribute((String) null, ATTR_ID, String.valueOf(this.createTime));
        out.attribute((String) null, ATTR_LAUNCHEDFROMUID, String.valueOf(this.launchedFromUid));
        String str = this.launchedFromPackage;
        if (str != null) {
            out.attribute((String) null, ATTR_LAUNCHEDFROMPACKAGE, str);
        }
        String str2 = this.resolvedType;
        if (str2 != null) {
            out.attribute((String) null, ATTR_RESOLVEDTYPE, str2);
        }
        out.attribute((String) null, ATTR_COMPONENTSPECIFIED, String.valueOf(this.componentSpecified));
        out.attribute((String) null, ATTR_USERID, String.valueOf(this.mUserId));
        ActivityManager.TaskDescription taskDescription2 = this.taskDescription;
        if (taskDescription2 != null) {
            taskDescription2.saveToXml(out);
        }
        out.startTag((String) null, TAG_INTENT);
        this.intent.saveToXml(out);
        out.endTag((String) null, TAG_INTENT);
        if (isPersistable() && this.persistentState != null) {
            out.startTag((String) null, TAG_PERSISTABLEBUNDLE);
            this.persistentState.saveToXml(out);
            out.endTag((String) null, TAG_PERSISTABLEBUNDLE);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:43:0x00fe  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x0167  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static com.android.server.wm.ActivityRecord restoreFromXml(org.xmlpull.v1.XmlPullParser r34, com.android.server.wm.ActivityStackSupervisor r35) throws java.io.IOException, org.xmlpull.v1.XmlPullParserException {
        /*
            r0 = r34
            r1 = 0
            r2 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = -1
            int r10 = r34.getDepth()
            android.app.ActivityManager$TaskDescription r11 = new android.app.ActivityManager$TaskDescription
            r11.<init>()
            int r12 = r34.getAttributeCount()
            r13 = 1
            int r12 = r12 - r13
        L_0x001a:
            java.lang.String r14 = "ActivityTaskManager"
            if (r12 < 0) goto L_0x00ae
            java.lang.String r15 = r0.getAttributeName(r12)
            java.lang.String r13 = r0.getAttributeValue(r12)
            java.lang.String r0 = "id"
            boolean r0 = r0.equals(r15)
            if (r0 == 0) goto L_0x0036
            long r8 = java.lang.Long.parseLong(r13)
            r17 = r1
            goto L_0x00a5
        L_0x0036:
            java.lang.String r0 = "launched_from_uid"
            boolean r0 = r0.equals(r15)
            if (r0 == 0) goto L_0x0047
            int r0 = java.lang.Integer.parseInt(r13)
            r3 = r0
            r17 = r1
            goto L_0x00a5
        L_0x0047:
            java.lang.String r0 = "launched_from_package"
            boolean r0 = r0.equals(r15)
            if (r0 == 0) goto L_0x0054
            r0 = r13
            r4 = r0
            r17 = r1
            goto L_0x00a5
        L_0x0054:
            java.lang.String r0 = "resolved_type"
            boolean r0 = r0.equals(r15)
            if (r0 == 0) goto L_0x0061
            r0 = r13
            r5 = r0
            r17 = r1
            goto L_0x00a5
        L_0x0061:
            java.lang.String r0 = "component_specified"
            boolean r0 = r0.equals(r15)
            if (r0 == 0) goto L_0x0071
            boolean r0 = java.lang.Boolean.parseBoolean(r13)
            r6 = r0
            r17 = r1
            goto L_0x00a5
        L_0x0071:
            java.lang.String r0 = "user_id"
            boolean r0 = r0.equals(r15)
            if (r0 == 0) goto L_0x0081
            int r0 = java.lang.Integer.parseInt(r13)
            r7 = r0
            r17 = r1
            goto L_0x00a5
        L_0x0081:
            java.lang.String r0 = "task_description_"
            boolean r0 = r15.startsWith(r0)
            if (r0 == 0) goto L_0x008f
            r11.restoreFromXml(r15, r13)
            r17 = r1
            goto L_0x00a5
        L_0x008f:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r17 = r1
            java.lang.String r1 = "Unknown ActivityRecord attribute="
            r0.append(r1)
            r0.append(r15)
            java.lang.String r0 = r0.toString()
            android.util.Log.d(r14, r0)
        L_0x00a5:
            int r12 = r12 + -1
            r13 = 1
            r0 = r34
            r1 = r17
            goto L_0x001a
        L_0x00ae:
            r17 = r1
            r0 = r17
        L_0x00b2:
            int r1 = r34.next()
            r12 = r1
            r13 = 1
            if (r1 == r13) goto L_0x00fc
            r1 = 3
            if (r12 != r1) goto L_0x00c3
            int r1 = r34.getDepth()
            if (r1 < r10) goto L_0x00fc
        L_0x00c3:
            r1 = 2
            if (r12 != r1) goto L_0x00b2
            java.lang.String r1 = r34.getName()
            java.lang.String r15 = "intent"
            boolean r15 = r15.equals(r1)
            if (r15 == 0) goto L_0x00d7
            android.content.Intent r0 = android.content.Intent.restoreFromXml(r34)
            goto L_0x00fb
        L_0x00d7:
            java.lang.String r15 = "persistable_bundle"
            boolean r15 = r15.equals(r1)
            if (r15 == 0) goto L_0x00e4
            android.os.PersistableBundle r2 = android.os.PersistableBundle.restoreFromXml(r34)
            goto L_0x00fb
        L_0x00e4:
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r15.<init>()
            java.lang.String r13 = "restoreActivity: unexpected name="
            r15.append(r13)
            r15.append(r1)
            java.lang.String r13 = r15.toString()
            android.util.Slog.w(r14, r13)
            com.android.internal.util.XmlUtils.skipCurrentTag(r34)
        L_0x00fb:
            goto L_0x00b2
        L_0x00fc:
            if (r0 == 0) goto L_0x0167
            r1 = r35
            com.android.server.wm.ActivityTaskManagerService r13 = r1.mService
            r17 = 0
            r18 = 0
            int r20 = android.os.Binder.getCallingUid()
            r14 = r35
            r15 = r0
            r16 = r5
            r19 = r7
            android.content.pm.ActivityInfo r32 = r14.resolveActivity(r15, r16, r17, r18, r19, r20)
            if (r32 == 0) goto L_0x0148
            com.android.server.wm.ActivityRecord r33 = new com.android.server.wm.ActivityRecord
            r14 = r33
            r16 = 0
            r17 = 0
            android.content.res.Configuration r23 = r13.getConfiguration()
            r24 = 0
            r25 = 0
            r26 = 0
            r28 = 0
            r30 = 0
            r31 = 0
            r15 = r13
            r18 = r3
            r19 = r4
            r20 = r0
            r21 = r5
            r22 = r32
            r27 = r6
            r29 = r35
            r14.<init>(r15, r16, r17, r18, r19, r20, r21, r22, r23, r24, r25, r26, r27, r28, r29, r30, r31)
            r14.persistentState = r2
            r14.taskDescription = r11
            r14.createTime = r8
            return r14
        L_0x0148:
            org.xmlpull.v1.XmlPullParserException r14 = new org.xmlpull.v1.XmlPullParserException
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r15.<init>()
            java.lang.String r1 = "restoreActivity resolver error. Intent="
            r15.append(r1)
            r15.append(r0)
            java.lang.String r1 = " resolvedType="
            r15.append(r1)
            r15.append(r5)
            java.lang.String r1 = r15.toString()
            r14.<init>(r1)
            throw r14
        L_0x0167:
            org.xmlpull.v1.XmlPullParserException r1 = new org.xmlpull.v1.XmlPullParserException
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r14 = "restoreActivity error intent="
            r13.append(r14)
            r13.append(r0)
            java.lang.String r13 = r13.toString()
            r1.<init>(r13)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityRecord.restoreFromXml(org.xmlpull.v1.XmlPullParser, com.android.server.wm.ActivityStackSupervisor):com.android.server.wm.ActivityRecord");
    }

    private static boolean isInVrUiMode(Configuration config) {
        return (config.uiMode & 15) == 7;
    }

    /* access modifiers changed from: package-private */
    public int getUid() {
        return this.info.applicationInfo.uid;
    }

    /* access modifiers changed from: package-private */
    public void setShowWhenLocked(boolean showWhenLocked) {
        this.mShowWhenLocked = showWhenLocked;
        this.mRootActivityContainer.ensureActivitiesVisible((ActivityRecord) null, 0, false);
    }

    /* access modifiers changed from: package-private */
    public void setInheritShowWhenLocked(boolean inheritShowWhenLocked) {
        this.mInheritShownWhenLocked = inheritShowWhenLocked;
        this.mRootActivityContainer.ensureActivitiesVisible((ActivityRecord) null, 0, false);
    }

    /* access modifiers changed from: package-private */
    public boolean canShowWhenLocked() {
        ActivityRecord r;
        AppWindowToken appWindowToken;
        AppWindowToken appWindowToken2;
        if (!inPinnedWindowingMode() && (this.mShowWhenLocked || ((appWindowToken2 = this.mAppWindowToken) != null && appWindowToken2.containsShowWhenLockedWindow()))) {
            return ActivityRecordInjector.canShowWhenLocked(this.mAtmService.getAppOpsService(), this.appInfo.uid, this.packageName);
        }
        if (!this.mInheritShownWhenLocked || (r = getActivityBelow()) == null || r.inPinnedWindowingMode() || (!r.mShowWhenLocked && ((appWindowToken = r.mAppWindowToken) == null || !appWindowToken.containsShowWhenLockedWindow()))) {
            return false;
        }
        return ActivityRecordInjector.canShowWhenLocked(this.mAtmService.getAppOpsService(), this.appInfo.uid, this.packageName);
    }

    private ActivityRecord getActivityBelow() {
        int pos = this.task.mActivities.indexOf(this);
        if (pos == -1) {
            throw new IllegalStateException("Activity not found in its task");
        } else if (pos == 0) {
            return null;
        } else {
            return this.task.getChildAt(pos - 1);
        }
    }

    /* access modifiers changed from: package-private */
    public void setTurnScreenOn(boolean turnScreenOn) {
        this.mTurnScreenOn = turnScreenOn;
    }

    /* access modifiers changed from: package-private */
    public boolean canTurnScreenOn() {
        ActivityStack stack = getActivityStack();
        if (!this.mTurnScreenOn || stack == null || !stack.checkKeyguardVisibility(this, true, true)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean canResumeByCompat() {
        WindowProcessController windowProcessController = this.app;
        return windowProcessController == null || windowProcessController.updateTopResumingActivityInProcessIfNeeded(this);
    }

    /* access modifiers changed from: package-private */
    public boolean getTurnScreenOnFlag() {
        return this.mTurnScreenOn;
    }

    /* access modifiers changed from: package-private */
    public boolean isTopRunningActivity() {
        return this.mRootActivityContainer.topRunningActivity() == this;
    }

    /* access modifiers changed from: package-private */
    public boolean isResumedActivityOnDisplay() {
        ActivityDisplay display = getDisplay();
        return display != null && this == display.getResumedActivity();
    }

    /* access modifiers changed from: package-private */
    public void registerRemoteAnimations(RemoteAnimationDefinition definition) {
        AppWindowToken appWindowToken = this.mAppWindowToken;
        if (appWindowToken == null) {
            Slog.w(DisplayPolicy.TAG, "Attempted to register remote animations with non-existing app token: " + this.appToken);
            return;
        }
        appWindowToken.registerRemoteAnimations(definition);
    }

    public String toString() {
        if (this.stringName != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(this.stringName);
            sb.append(" t");
            TaskRecord taskRecord = this.task;
            sb.append(taskRecord == null ? -1 : taskRecord.taskId);
            sb.append(this.finishing ? " f}" : "}");
            return sb.toString();
        }
        StringBuilder sb2 = new StringBuilder(128);
        sb2.append("ActivityRecord{");
        sb2.append(Integer.toHexString(System.identityHashCode(this)));
        sb2.append(" u");
        sb2.append(this.mUserId);
        sb2.append(' ');
        sb2.append(this.intent.getComponent().flattenToShortString());
        this.stringName = sb2.toString();
        return toString();
    }

    /* access modifiers changed from: package-private */
    public void writeIdentifierToProto(ProtoOutputStream proto, long fieldId) {
        long token = proto.start(fieldId);
        proto.write(1120986464257L, System.identityHashCode(this));
        proto.write(1120986464258L, this.mUserId);
        proto.write(1138166333443L, this.intent.getComponent().flattenToShortString());
        proto.end(token);
    }

    /* access modifiers changed from: package-private */
    public void writeToProto(ProtoOutputStream proto) {
        super.writeToProto(proto, 1146756268033L, 0);
        writeIdentifierToProto(proto, 1146756268034L);
        proto.write(1138166333443L, this.mState.toString());
        proto.write(1133871366148L, this.visible);
        proto.write(1133871366149L, this.frontOfTask);
        if (hasProcess()) {
            proto.write(1120986464262L, this.app.getPid());
        }
        proto.write(1133871366151L, !this.fullscreen);
    }

    public void writeToProto(ProtoOutputStream proto, long fieldId) {
        long token = proto.start(fieldId);
        writeToProto(proto);
        proto.end(token);
    }

    static class CompatDisplayInsets {
        final int mDisplayHeight;
        final int mDisplayWidth;
        final Rect[] mNonDecorInsets = new Rect[4];
        final Rect[] mStableInsets = new Rect[4];

        CompatDisplayInsets(DisplayContent display) {
            this.mDisplayWidth = display.mBaseDisplayWidth;
            this.mDisplayHeight = display.mBaseDisplayHeight;
            DisplayPolicy policy = display.getDisplayPolicy();
            for (int rotation = 0; rotation < 4; rotation++) {
                this.mNonDecorInsets[rotation] = new Rect();
                this.mStableInsets[rotation] = new Rect();
                boolean z = true;
                if (!(rotation == 1 || rotation == 3)) {
                    z = false;
                }
                boolean rotated = z;
                policy.getNonDecorInsetsLw(rotation, rotated ? this.mDisplayHeight : this.mDisplayWidth, rotated ? this.mDisplayWidth : this.mDisplayHeight, display.calculateDisplayCutoutForRotation(rotation).getDisplayCutout(), this.mNonDecorInsets[rotation]);
                this.mStableInsets[rotation].set(this.mNonDecorInsets[rotation]);
                policy.convertNonDecorInsetsToStableInsets(this.mStableInsets[rotation], rotation);
            }
        }

        /* access modifiers changed from: package-private */
        public void getDisplayBoundsByRotation(Rect outBounds, int rotation) {
            boolean rotated = true;
            if (!(rotation == 1 || rotation == 3)) {
                rotated = false;
            }
            outBounds.set(0, 0, rotated ? this.mDisplayHeight : this.mDisplayWidth, rotated ? this.mDisplayWidth : this.mDisplayHeight);
        }

        /* access modifiers changed from: package-private */
        public void getDisplayBoundsByOrientation(Rect outBounds, int orientation) {
            int longSide = Math.max(this.mDisplayWidth, this.mDisplayHeight);
            int shortSide = Math.min(this.mDisplayWidth, this.mDisplayHeight);
            boolean isLandscape = orientation == 2;
            outBounds.set(0, 0, isLandscape ? longSide : shortSide, isLandscape ? shortSide : longSide);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean shouldUseSizeCompatModeByMiui() {
        return true ^ android.os.SystemProperties.getBoolean("persist.sys.miui_optimization", true);
    }

    public void setMiuiConfigFlag(@WindowConfiguration.MiuiConfigFlag int miuiConfigFlag) {
        this.mMiuiConfigFlag = miuiConfigFlag;
    }
}
