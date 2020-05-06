package com.android.server.wm;

import android.app.ActivityOptions;
import android.app.IActivityController;
import android.app.IApplicationThread;
import android.app.RemoteAction;
import android.app.ResultInfo;
import android.app.WindowConfiguration;
import android.app.servertransaction.ActivityLifecycleItem;
import android.app.servertransaction.ActivityResultItem;
import android.app.servertransaction.ClientTransactionItem;
import android.app.servertransaction.DestroyActivityItem;
import android.app.servertransaction.PauseActivityItem;
import android.app.servertransaction.StopActivityItem;
import android.app.servertransaction.WindowVisibilityItem;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.service.voice.IVoiceInteractionSession;
import android.util.ArraySet;
import android.util.BoostFramework;
import android.util.EventLog;
import android.util.IntArray;
import android.util.Log;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import android.view.IApplicationToken;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.ActivityTrigger;
import com.android.internal.app.IVoiceInteractor;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.server.Watchdog;
import com.android.server.am.ActivityManagerService;
import com.android.server.am.AppTimeTracker;
import com.android.server.am.EventLogTags;
import com.android.server.am.PendingIntentRecord;
import com.android.server.job.controllers.JobStatus;
import com.android.server.pm.DumpState;
import com.android.server.wm.RootActivityContainer;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ActivityStack extends ConfigurationContainer {
    private static final int CAST_MODE_EXIT = 0;
    static final int DESTROY_ACTIVITIES_MSG = 105;
    private static final int DESTROY_TIMEOUT = 10000;
    static final int DESTROY_TIMEOUT_MSG = 102;
    static final int FINISH_AFTER_PAUSE = 1;
    static final int FINISH_AFTER_VISIBLE = 2;
    static final int FINISH_IMMEDIATELY = 0;
    static final int LAUNCH_TICK = 500;
    static final int LAUNCH_TICK_MSG = 103;
    private static final int MAX_STOPPING_TO_FORCE = 3;
    private static final int PAUSE_TIMEOUT = 500;
    static final int PAUSE_TIMEOUT_MSG = 101;
    @VisibleForTesting
    protected static final int REMOVE_TASK_MODE_DESTROYING = 0;
    static final int REMOVE_TASK_MODE_MOVING = 1;
    static final int REMOVE_TASK_MODE_MOVING_TO_TOP = 2;
    private static final boolean SHOW_APP_STARTING_PREVIEW = true;
    static final int STACK_VISIBILITY_INVISIBLE = 2;
    static final int STACK_VISIBILITY_VISIBLE = 0;
    static final int STACK_VISIBILITY_VISIBLE_BEHIND_TRANSLUCENT = 1;
    private static final int STOP_TIMEOUT = 11000;
    static final int STOP_TIMEOUT_MSG = 104;
    private static final String TAG = "ActivityTaskManager";
    private static final String TAG_ADD_REMOVE = "ActivityTaskManager";
    private static final String TAG_APP = "ActivityTaskManager";
    private static final String TAG_CLEANUP = "ActivityTaskManager";
    private static final String TAG_CONTAINERS = "ActivityTaskManager";
    private static final String TAG_PAUSE = "ActivityTaskManager";
    private static final String TAG_RELEASE = "ActivityTaskManager";
    private static final String TAG_RESULTS = "ActivityTaskManager";
    private static final String TAG_SAVED_STATE = "ActivityTaskManager";
    private static final String TAG_STACK = "ActivityTaskManager";
    private static final String TAG_STATES = "ActivityTaskManager";
    private static final String TAG_SWITCH = "ActivityTaskManager";
    private static final String TAG_TASKS = "ActivityTaskManager";
    private static final String TAG_TRANSITION = "ActivityTaskManager";
    private static final String TAG_USER_LEAVING = "ActivityTaskManager";
    private static final String TAG_VISIBILITY = "ActivityTaskManager";
    private static final long TRANSLUCENT_CONVERSION_TIMEOUT = 2000;
    static final int TRANSLUCENT_TIMEOUT_MSG = 106;
    private static final ActivityPluginDelegate mActivityPluginDelegate = new ActivityPluginDelegate();
    static final ActivityTrigger mActivityTrigger = new ActivityTrigger();
    boolean mConfigWillChange;
    int mCurrentUser;
    private final Rect mDeferredBounds = new Rect();
    private final Rect mDeferredDisplayedBounds = new Rect();
    int mDisplayId;
    boolean mForceHidden = false;
    final Handler mHandler;
    boolean mInResumeTopActivity = false;
    private final ArrayList<ActivityRecord> mLRUActivities = new ArrayList<>();
    ActivityRecord mLastNoHistoryActivity = null;
    ActivityRecord mLastPausedActivity = null;
    ActivityRecord mPausingActivity = null;
    public BoostFramework mPerf = null;
    private int mRestoreOverrideWindowingMode = 0;
    ActivityRecord mResumedActivity = null;
    protected final RootActivityContainer mRootActivityContainer;
    final ActivityTaskManagerService mService;
    final int mStackId;
    protected final ActivityStackSupervisor mStackSupervisor;
    private final ArrayList<TaskRecord> mTaskHistory = new ArrayList<>();
    TaskStack mTaskStack;
    private final ArrayList<ActivityRecord> mTmpActivities = new ArrayList<>();
    private final ActivityOptions mTmpOptions = ActivityOptions.makeBasic();
    private final Rect mTmpRect = new Rect();
    private final Rect mTmpRect2 = new Rect();
    private boolean mTopActivityOccludesKeyguard;
    private ActivityRecord mTopDismissingKeyguardActivity;
    ActivityRecord mTranslucentActivityWaiting = null;
    ArrayList<ActivityRecord> mUndrawnActivitiesBelowTopTranslucent = new ArrayList<>();
    private boolean mUpdateBoundsDeferred;
    private boolean mUpdateBoundsDeferredCalled;
    private boolean mUpdateDisplayedBoundsDeferredCalled;
    final WindowManagerService mWindowManager;

    public enum ActivityState {
        INITIALIZING,
        RESUMED,
        PAUSING,
        PAUSED,
        STOPPING,
        STOPPED,
        FINISHING,
        DESTROYING,
        DESTROYED,
        RESTARTING_PROCESS
    }

    @interface StackVisibility {
    }

    /* access modifiers changed from: protected */
    public int getChildCount() {
        return this.mTaskHistory.size();
    }

    /* access modifiers changed from: protected */
    public TaskRecord getChildAt(int index) {
        return this.mTaskHistory.get(index);
    }

    /* access modifiers changed from: protected */
    public ActivityDisplay getParent() {
        return getDisplay();
    }

    /* access modifiers changed from: package-private */
    public void setParent(ActivityDisplay parent) {
        if (getParent() != parent) {
            this.mDisplayId = parent.mDisplayId;
            onParentChanged();
        }
    }

    /* access modifiers changed from: protected */
    public void onParentChanged() {
        ActivityDisplay display = getParent();
        if (display != null) {
            getConfiguration().windowConfiguration.setRotation(display.getWindowConfiguration().getRotation());
        }
        super.onParentChanged();
        if (display != null && inSplitScreenPrimaryWindowingMode()) {
            getStackDockedModeBounds((Rect) null, (Rect) null, this.mTmpRect, this.mTmpRect2);
            this.mStackSupervisor.resizeDockedStackLocked(getRequestedOverrideBounds(), this.mTmpRect, this.mTmpRect2, (Rect) null, (Rect) null, true);
        }
        this.mRootActivityContainer.updateUIDsPresentOnDisplay();
    }

    private static class ScheduleDestroyArgs {
        final WindowProcessController mOwner;
        final String mReason;

        ScheduleDestroyArgs(WindowProcessController owner, String reason) {
            this.mOwner = owner;
            this.mReason = reason;
        }
    }

    private class ActivityStackHandler extends Handler {
        ActivityStackHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            IApplicationToken.Stub stub = null;
            switch (msg.what) {
                case 101:
                    ActivityRecord r = (ActivityRecord) msg.obj;
                    Slog.w("ActivityTaskManager", "Activity pause timeout for " + r);
                    synchronized (ActivityStack.this.mService.mGlobalLock) {
                        try {
                            WindowManagerService.boostPriorityForLockedSection();
                            if (r.hasProcess()) {
                                ActivityTaskManagerService activityTaskManagerService = ActivityStack.this.mService;
                                WindowProcessController windowProcessController = r.app;
                                long j = r.pauseTime;
                                activityTaskManagerService.logAppTooSlow(windowProcessController, j, "pausing " + r);
                            }
                            ActivityStack.this.activityPausedLocked(r.appToken, true);
                        } catch (Throwable th) {
                            while (true) {
                                WindowManagerService.resetPriorityAfterLockedSection();
                                throw th;
                                break;
                            }
                        }
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                case 102:
                    ActivityRecord r2 = (ActivityRecord) msg.obj;
                    Slog.w("ActivityTaskManager", "Activity destroy timeout for " + r2);
                    synchronized (ActivityStack.this.mService.mGlobalLock) {
                        try {
                            WindowManagerService.boostPriorityForLockedSection();
                            ActivityStack activityStack = ActivityStack.this;
                            if (r2 != null) {
                                stub = r2.appToken;
                            }
                            activityStack.activityDestroyedLocked((IBinder) stub, "destroyTimeout");
                        } catch (Throwable th2) {
                            while (true) {
                                WindowManagerService.resetPriorityAfterLockedSection();
                                throw th2;
                                break;
                            }
                        }
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                case 103:
                    ActivityRecord r3 = (ActivityRecord) msg.obj;
                    synchronized (ActivityStack.this.mService.mGlobalLock) {
                        try {
                            WindowManagerService.boostPriorityForLockedSection();
                            if (r3.continueLaunchTickingLocked()) {
                                ActivityTaskManagerService activityTaskManagerService2 = ActivityStack.this.mService;
                                WindowProcessController windowProcessController2 = r3.app;
                                long j2 = r3.launchTickTime;
                                activityTaskManagerService2.logAppTooSlow(windowProcessController2, j2, "launching " + r3);
                            }
                        } catch (Throwable th3) {
                            while (true) {
                                WindowManagerService.resetPriorityAfterLockedSection();
                                throw th3;
                                break;
                            }
                        }
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                case 104:
                    ActivityRecord r4 = (ActivityRecord) msg.obj;
                    Slog.w("ActivityTaskManager", "Activity stop timeout for " + r4);
                    synchronized (ActivityStack.this.mService.mGlobalLock) {
                        try {
                            WindowManagerService.boostPriorityForLockedSection();
                            if (r4.isInHistory()) {
                                r4.activityStoppedLocked((Bundle) null, (PersistableBundle) null, (CharSequence) null);
                            }
                        } catch (Throwable th4) {
                            while (true) {
                                WindowManagerService.resetPriorityAfterLockedSection();
                                throw th4;
                                break;
                            }
                        }
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                case 105:
                    ScheduleDestroyArgs args = (ScheduleDestroyArgs) msg.obj;
                    synchronized (ActivityStack.this.mService.mGlobalLock) {
                        try {
                            WindowManagerService.boostPriorityForLockedSection();
                            ActivityStack.this.destroyActivitiesLocked(args.mOwner, args.mReason);
                        } catch (Throwable th5) {
                            while (true) {
                                WindowManagerService.resetPriorityAfterLockedSection();
                                throw th5;
                                break;
                            }
                        }
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                case 106:
                    synchronized (ActivityStack.this.mService.mGlobalLock) {
                        try {
                            WindowManagerService.boostPriorityForLockedSection();
                            ActivityStack.this.notifyActivityDrawnLocked((ActivityRecord) null);
                        } catch (Throwable th6) {
                            while (true) {
                                WindowManagerService.resetPriorityAfterLockedSection();
                                throw th6;
                                break;
                            }
                        }
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                default:
                    return;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public int numActivities() {
        int count = 0;
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            count += this.mTaskHistory.get(taskNdx).mActivities.size();
        }
        return count;
    }

    ActivityStack(ActivityDisplay display, int stackId, ActivityStackSupervisor supervisor, int windowingMode, int activityType, boolean onTop) {
        this.mStackSupervisor = supervisor;
        this.mService = supervisor.mService;
        this.mRootActivityContainer = this.mService.mRootActivityContainer;
        this.mHandler = new ActivityStackHandler(supervisor.mLooper);
        this.mWindowManager = this.mService.mWindowManager;
        this.mStackId = stackId;
        this.mCurrentUser = this.mService.mAmInternal.getCurrentUserId();
        this.mDisplayId = display.mDisplayId;
        setActivityType(activityType);
        createTaskStack(display.mDisplayId, onTop, this.mTmpRect2);
        setWindowingMode(windowingMode, false, false, false, false, true);
        if (this.mService.mGestureController != null && this.mService.mGestureController.isRecentsStackLaunchBehind(this)) {
            onTop = false;
        }
        display.addChild(this, onTop ? Integer.MAX_VALUE : Integer.MIN_VALUE);
    }

    /* access modifiers changed from: package-private */
    public void createTaskStack(int displayId, boolean onTop, Rect outBounds) {
        DisplayContent dc = this.mWindowManager.mRoot.getDisplayContent(displayId);
        if (dc != null) {
            this.mTaskStack = new TaskStack(this.mWindowManager, this.mStackId, this);
            dc.setStackOnDisplay(this.mStackId, onTop, this.mTaskStack);
            if (this.mTaskStack.matchParentBounds()) {
                outBounds.setEmpty();
            } else {
                this.mTaskStack.getRawBounds(outBounds);
            }
        } else {
            throw new IllegalArgumentException("Trying to add stackId=" + this.mStackId + " to unknown displayId=" + displayId);
        }
    }

    /* access modifiers changed from: package-private */
    public TaskStack getTaskStack() {
        return this.mTaskStack;
    }

    /* access modifiers changed from: package-private */
    public void onActivityStateChanged(ActivityRecord record, ActivityState state, String reason) {
        if (record == this.mResumedActivity && state != ActivityState.RESUMED) {
            setResumedActivity((ActivityRecord) null, reason + " - onActivityStateChanged");
        }
        if (state == ActivityState.RESUMED) {
            setResumedActivity(record, reason + " - onActivityStateChanged");
            if (record == this.mRootActivityContainer.getTopResumedActivity()) {
                this.mService.setResumedActivityUncheckLocked(record, reason);
            }
            this.mStackSupervisor.mRecentTasks.add(record.getTaskRecord());
        }
    }

    public void onConfigurationChanged(Configuration newParentConfig) {
        boolean hasNewOverrideBounds;
        ActivityDisplay display;
        TaskRecord topTask;
        Configuration configuration = newParentConfig;
        if (!this.mService.getCastRotationChanged() || this.mService.getCastModeStackId() != this.mStackId) {
            int prevWindowingMode = getWindowingMode();
            boolean prevIsAlwaysOnTop = isAlwaysOnTop();
            int prevRotation = getWindowConfiguration().getRotation();
            int prevDensity = getConfiguration().densityDpi;
            int prevScreenW = getConfiguration().screenWidthDp;
            int prevScreenH = getConfiguration().screenHeightDp;
            Rect newBounds = this.mTmpRect;
            getBounds(newBounds);
            super.onConfigurationChanged(newParentConfig);
            ActivityDisplay display2 = getDisplay();
            if (display2 == null) {
                Rect rect = newBounds;
            } else if (getTaskStack() == null) {
                ActivityDisplay activityDisplay = display2;
                Rect rect2 = newBounds;
            } else {
                boolean windowingModeChanged = prevWindowingMode != getWindowingMode();
                int overrideWindowingMode = getRequestedOverrideWindowingMode();
                boolean hasNewOverrideBounds2 = false;
                if (overrideWindowingMode == 2) {
                    hasNewOverrideBounds2 = getTaskStack().calculatePinnedBoundsForConfigChange(newBounds);
                } else if (!matchParentBounds()) {
                    int newRotation = getWindowConfiguration().getRotation();
                    boolean rotationChanged = prevRotation != newRotation;
                    if (rotationChanged) {
                        display2.mDisplayContent.rotateBounds(configuration.windowConfiguration.getBounds(), prevRotation, newRotation, newBounds);
                        hasNewOverrideBounds2 = true;
                    }
                    if ((overrideWindowingMode == 3 || overrideWindowingMode == 4) && !(!rotationChanged && !windowingModeChanged && prevDensity == getConfiguration().densityDpi && prevScreenW == getConfiguration().screenWidthDp && prevScreenH == getConfiguration().screenHeightDp)) {
                        getTaskStack().calculateDockedBoundsForConfigChange(configuration, newBounds);
                        hasNewOverrideBounds2 = true;
                    }
                }
                if (windowingModeChanged) {
                    if (overrideWindowingMode == 3) {
                        getStackDockedModeBounds((Rect) null, (Rect) null, newBounds, this.mTmpRect2);
                        setTaskDisplayedBounds((Rect) null);
                        setTaskBounds(newBounds);
                        setBounds(newBounds);
                        newBounds.set(newBounds);
                    } else if (overrideWindowingMode == 4) {
                        Rect dockedBounds = display2.getSplitScreenPrimaryStack().getBounds();
                        if (display2.mDisplayContent.getDockedDividerController().isMinimizedDock() && (topTask = display2.getSplitScreenPrimaryStack().topTask()) != null) {
                            dockedBounds = topTask.getBounds();
                        }
                        getStackDockedModeBounds(dockedBounds, (Rect) null, newBounds, this.mTmpRect2);
                        hasNewOverrideBounds2 = true;
                    }
                    display2.onStackWindowingModeChanged(this);
                    hasNewOverrideBounds = hasNewOverrideBounds2;
                } else {
                    hasNewOverrideBounds = hasNewOverrideBounds2;
                }
                if (hasNewOverrideBounds) {
                    int i = overrideWindowingMode;
                    display = display2;
                    Rect rect3 = newBounds;
                    this.mRootActivityContainer.resizeStack(this, new Rect(newBounds), (Rect) null, (Rect) null, true, true, true);
                } else {
                    display = display2;
                    Rect rect4 = newBounds;
                }
                if (prevIsAlwaysOnTop != isAlwaysOnTop()) {
                    display.positionChildAtTop(this, false);
                }
            }
        }
    }

    public void setWindowingMode(int windowingMode) {
        setWindowingMode(windowingMode, false, false, false, false, false);
    }

    private static boolean isTransientWindowingMode(int windowingMode) {
        return windowingMode == 3 || windowingMode == 4;
    }

    /* access modifiers changed from: package-private */
    public void setWindowingMode(int preferredWindowingMode, boolean animate, boolean showRecents, boolean enteringSplitScreenMode, boolean deferEnsuringVisibility, boolean creating) {
        this.mWindowManager.inSurfaceTransaction(new Runnable(preferredWindowingMode, animate, showRecents, enteringSplitScreenMode, deferEnsuringVisibility, creating) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ boolean f$2;
            private final /* synthetic */ boolean f$3;
            private final /* synthetic */ boolean f$4;
            private final /* synthetic */ boolean f$5;
            private final /* synthetic */ boolean f$6;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
            }

            public final void run() {
                ActivityStack.this.lambda$setWindowingMode$0$ActivityStack(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
            }
        });
    }

    /* Debug info: failed to restart local var, previous not found, register: 24 */
    /* access modifiers changed from: private */
    /* renamed from: setWindowingModeInSurfaceTransaction */
    public void lambda$setWindowingMode$0$ActivityStack(int preferredWindowingMode, boolean animate, boolean showRecents, boolean enteringSplitScreenMode, boolean deferEnsuringVisibility, boolean creating) {
        int windowingMode;
        int likelyResolvedMode;
        int likelyResolvedMode2;
        int i = preferredWindowingMode;
        int currentMode = getWindowingMode();
        int currentOverrideMode = getRequestedOverrideWindowingMode();
        ActivityDisplay display = getDisplay();
        TaskRecord topTask = topTask();
        ActivityStack splitScreenStack = display.getSplitScreenPrimaryStack();
        int windowingMode2 = preferredWindowingMode;
        if (i == 0 && isTransientWindowingMode(currentMode)) {
            windowingMode2 = this.mRestoreOverrideWindowingMode;
        }
        this.mTmpOptions.setLaunchWindowingMode(windowingMode2);
        if (!creating) {
            windowingMode2 = display.validateWindowingMode(windowingMode2, (ActivityRecord) null, topTask, getActivityType());
        }
        if (splitScreenStack == this && windowingMode2 == 4) {
            windowingMode = this.mRestoreOverrideWindowingMode;
        } else {
            windowingMode = windowingMode2;
        }
        boolean alreadyInSplitScreenMode = display.hasSplitScreenPrimaryStack();
        boolean sendNonResizeableNotification = !enteringSplitScreenMode && windowingMode != 5;
        if (alreadyInSplitScreenMode && windowingMode == 1 && sendNonResizeableNotification && isActivityTypeStandardOrUndefined()) {
            if ((i == 3 || i == 4) || creating) {
                this.mService.getTaskChangeNotificationController().notifyActivityDismissingDockedStack();
                ActivityStack primarySplitStack = display.getSplitScreenPrimaryStack();
                primarySplitStack.lambda$setWindowingMode$0$ActivityStack(0, false, false, false, true, primarySplitStack == this ? creating : false);
            }
        }
        if (currentMode == windowingMode) {
            getRequestedOverrideConfiguration().windowConfiguration.setWindowingMode(windowingMode);
            return;
        }
        WindowManagerService wm = this.mService.mWindowManager;
        ActivityRecord topActivity = getTopActivity();
        int likelyResolvedMode3 = windowingMode;
        if (windowingMode == 0) {
            ConfigurationContainer parent = getParent();
            if (parent != null) {
                likelyResolvedMode2 = parent.getWindowingMode();
            } else {
                likelyResolvedMode2 = 1;
            }
            likelyResolvedMode = likelyResolvedMode2;
        } else {
            likelyResolvedMode = likelyResolvedMode3;
        }
        if (sendNonResizeableNotification && likelyResolvedMode != 1 && topActivity != null && topActivity.isNonResizableOrForcedResizable() && !topActivity.noDisplay) {
            this.mService.getTaskChangeNotificationController().notifyActivityForcedResizable(topTask.taskId, 1, topActivity.appInfo.packageName);
        }
        wm.deferSurfaceLayout();
        if (!animate && topActivity != null) {
            try {
                this.mStackSupervisor.mNoAnimActivities.add(topActivity);
            } catch (Throwable th) {
                th = th;
                ActivityRecord activityRecord = topActivity;
            }
        }
        try {
            super.setWindowingMode(windowingMode);
            windowingMode = getWindowingMode();
            if (creating) {
                if (windowingMode == 5) {
                    this.mTaskStack.setMiuiConfigFlag(2, true);
                }
                if (!showRecents || alreadyInSplitScreenMode || this.mDisplayId != 0 || windowingMode != 3) {
                } else {
                    ActivityRecord activityRecord2 = topActivity;
                    display.getOrCreateStack(4, 3, true).moveToFront("setWindowingMode");
                    this.mService.mWindowManager.showRecentApps();
                }
                wm.continueSurfaceLayout();
                return;
            }
            if (windowingMode == 2 || currentMode == 2) {
                throw new IllegalArgumentException("Changing pinned windowing mode not currently supported");
            } else if (windowingMode != 3 || splitScreenStack == null) {
                if (isTransientWindowingMode(windowingMode) && !isTransientWindowingMode(currentMode)) {
                    this.mRestoreOverrideWindowingMode = currentOverrideMode;
                }
                this.mTmpRect2.setEmpty();
                if (windowingMode != 1) {
                    if (this.mTaskStack.matchParentBounds()) {
                        this.mTmpRect2.setEmpty();
                    } else {
                        this.mTaskStack.getRawBounds(this.mTmpRect2);
                    }
                }
                if (!Objects.equals(getRequestedOverrideBounds(), this.mTmpRect2)) {
                    resize(this.mTmpRect2, (Rect) null, (Rect) null);
                }
                if (windowingMode == 1 && topTask != null && !topTask.getBounds().isEmpty()) {
                    topTask.resize((Rect) null, 0, false, false);
                    ActivityStackInjector.onWindowingModeChanged(this, currentMode, windowingMode);
                }
                if (showRecents && !alreadyInSplitScreenMode && this.mDisplayId == 0 && windowingMode == 3) {
                    display.getOrCreateStack(4, 3, true).moveToFront("setWindowingMode");
                    this.mService.mWindowManager.showRecentApps();
                }
                wm.continueSurfaceLayout();
                if (!deferEnsuringVisibility) {
                    this.mRootActivityContainer.ensureActivitiesVisible((ActivityRecord) null, 0, true);
                    this.mRootActivityContainer.resumeFocusedStacksTopActivities();
                }
            } else {
                try {
                    throw new IllegalArgumentException("Setting primary split-screen windowing mode while there is already one isn't currently supported");
                } catch (Throwable th2) {
                    th = th2;
                    if (showRecents && !alreadyInSplitScreenMode && this.mDisplayId == 0 && windowingMode == 3) {
                        display.getOrCreateStack(4, 3, true).moveToFront("setWindowingMode");
                        this.mService.mWindowManager.showRecentApps();
                    }
                    wm.continueSurfaceLayout();
                    throw th;
                }
            }
        } catch (Throwable th3) {
            th = th3;
            ActivityRecord activityRecord3 = topActivity;
            display.getOrCreateStack(4, 3, true).moveToFront("setWindowingMode");
            this.mService.mWindowManager.showRecentApps();
            wm.continueSurfaceLayout();
            throw th;
        }
    }

    public boolean isCompatible(int windowingMode, int activityType) {
        if (activityType == 0) {
            activityType = 1;
        }
        return super.isCompatible(windowingMode, activityType);
    }

    /* access modifiers changed from: package-private */
    public void reparent(ActivityDisplay activityDisplay, boolean onTop, boolean displayRemoved) {
        removeFromDisplay();
        this.mTmpRect2.setEmpty();
        TaskStack taskStack = this.mTaskStack;
        if (taskStack == null) {
            Log.w("ActivityTaskManager", "Task stack is not valid when reparenting.");
        } else {
            taskStack.reparent(activityDisplay.mDisplayId, this.mTmpRect2, onTop);
        }
        setBounds(this.mTmpRect2.isEmpty() ? null : this.mTmpRect2);
        activityDisplay.addChild(this, onTop ? Integer.MAX_VALUE : Integer.MIN_VALUE);
        if (!displayRemoved) {
            postReparent();
        }
    }

    /* access modifiers changed from: package-private */
    public void postReparent() {
        adjustFocusToNextFocusableStack("reparent", true);
        this.mRootActivityContainer.resumeFocusedStacksTopActivities();
        this.mRootActivityContainer.ensureActivitiesVisible((ActivityRecord) null, 0, false);
    }

    private void removeFromDisplay() {
        ActivityDisplay display = getDisplay();
        if (display != null) {
            display.removeChild(this);
        }
        this.mDisplayId = -1;
    }

    /* access modifiers changed from: package-private */
    public void remove() {
        removeFromDisplay();
        TaskStack taskStack = this.mTaskStack;
        if (taskStack != null) {
            taskStack.removeIfPossible();
            this.mTaskStack = null;
        }
        onParentChanged();
    }

    /* access modifiers changed from: package-private */
    public ActivityDisplay getDisplay() {
        return this.mRootActivityContainer.getActivityDisplay(this.mDisplayId);
    }

    /* access modifiers changed from: package-private */
    public void getStackDockedModeBounds(Rect dockedBounds, Rect currentTempTaskBounds, Rect outStackBounds, Rect outTempTaskBounds) {
        TaskStack taskStack = this.mTaskStack;
        if (taskStack != null) {
            taskStack.getStackDockedModeBoundsLocked(getParent().getConfiguration(), dockedBounds, currentTempTaskBounds, outStackBounds, outTempTaskBounds);
            return;
        }
        outStackBounds.setEmpty();
        outTempTaskBounds.setEmpty();
    }

    /* access modifiers changed from: package-private */
    public void prepareFreezingTaskBounds() {
        TaskStack taskStack = this.mTaskStack;
        if (taskStack != null) {
            taskStack.prepareFreezingTaskBounds();
        }
    }

    /* access modifiers changed from: package-private */
    public void getWindowContainerBounds(Rect outBounds) {
        TaskStack taskStack = this.mTaskStack;
        if (taskStack != null) {
            taskStack.getBounds(outBounds);
        } else {
            outBounds.setEmpty();
        }
    }

    /* access modifiers changed from: package-private */
    public void positionChildWindowContainerAtTop(TaskRecord child) {
        TaskStack taskStack = this.mTaskStack;
        if (taskStack != null) {
            taskStack.positionChildAtTop(child.getTask(), !this.mService.mGestureController.isRecentsStackLaunchBehind(this));
        }
    }

    /* access modifiers changed from: package-private */
    public void positionChildWindowContainerAtBottom(TaskRecord child) {
        boolean z = true;
        ActivityStack nextFocusableStack = getDisplay().getNextFocusableStack(child.getStack(), true);
        TaskStack taskStack = this.mTaskStack;
        if (taskStack != null) {
            Task task = child.getTask();
            if (nextFocusableStack != null) {
                z = false;
            }
            taskStack.positionChildAtBottom(task, z);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean deferScheduleMultiWindowModeChanged() {
        if (!inPinnedWindowingMode() || getTaskStack() == null) {
            return false;
        }
        return getTaskStack().deferScheduleMultiWindowModeChanged();
    }

    /* access modifiers changed from: package-private */
    public void deferUpdateBounds() {
        if (!this.mUpdateBoundsDeferred) {
            this.mUpdateBoundsDeferred = true;
            this.mUpdateBoundsDeferredCalled = false;
        }
    }

    /* access modifiers changed from: package-private */
    public void continueUpdateBounds() {
        if (this.mUpdateBoundsDeferred) {
            this.mUpdateBoundsDeferred = false;
            if (this.mUpdateBoundsDeferredCalled) {
                setTaskBounds(this.mDeferredBounds);
                setBounds(this.mDeferredBounds);
            }
            if (this.mUpdateDisplayedBoundsDeferredCalled) {
                setTaskDisplayedBounds(this.mDeferredDisplayedBounds);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean updateBoundsAllowed(Rect bounds) {
        if (!this.mUpdateBoundsDeferred) {
            return true;
        }
        if (bounds != null) {
            this.mDeferredBounds.set(bounds);
        } else {
            this.mDeferredBounds.setEmpty();
        }
        this.mUpdateBoundsDeferredCalled = true;
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean updateDisplayedBoundsAllowed(Rect bounds) {
        if (!this.mUpdateBoundsDeferred) {
            return true;
        }
        if (bounds != null) {
            this.mDeferredDisplayedBounds.set(bounds);
        } else {
            this.mDeferredDisplayedBounds.setEmpty();
        }
        this.mUpdateDisplayedBoundsDeferredCalled = true;
        return false;
    }

    public int setBounds(Rect bounds) {
        return super.setBounds(!inMultiWindowMode() ? null : bounds);
    }

    public ActivityRecord topRunningActivityLocked() {
        return topRunningActivityLocked(false);
    }

    /* access modifiers changed from: package-private */
    public void getAllRunningVisibleActivitiesLocked(ArrayList<ActivityRecord> outActivities) {
        outActivities.clear();
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            this.mTaskHistory.get(taskNdx).getAllRunningVisibleActivitiesLocked(outActivities);
        }
    }

    /* access modifiers changed from: package-private */
    public ActivityRecord topRunningActivityLocked(boolean focusableOnly) {
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            ActivityRecord r = this.mTaskHistory.get(taskNdx).topRunningActivityLocked();
            if (r != null && (!focusableOnly || r.isFocusable())) {
                return r;
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public ActivityRecord topRunningNonOverlayTaskActivity() {
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            ArrayList<ActivityRecord> activities = this.mTaskHistory.get(taskNdx).mActivities;
            for (int activityNdx = activities.size() - 1; activityNdx >= 0; activityNdx--) {
                ActivityRecord r = activities.get(activityNdx);
                if (!r.finishing && !r.mTaskOverlay) {
                    return r;
                }
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public ActivityRecord topRunningNonDelayedActivityLocked(ActivityRecord notTop) {
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            ArrayList<ActivityRecord> activities = this.mTaskHistory.get(taskNdx).mActivities;
            for (int activityNdx = activities.size() - 1; activityNdx >= 0; activityNdx--) {
                ActivityRecord r = activities.get(activityNdx);
                if (!r.finishing && !r.delayedResume && r != notTop && r.okToShowLocked()) {
                    return r;
                }
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public final ActivityRecord topRunningActivityLocked(IBinder token, int taskId) {
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            TaskRecord task = this.mTaskHistory.get(taskNdx);
            if (task.taskId != taskId) {
                ArrayList<ActivityRecord> activities = task.mActivities;
                for (int i = activities.size() - 1; i >= 0; i--) {
                    ActivityRecord r = activities.get(i);
                    if (!r.finishing && token != r.appToken && r.okToShowLocked()) {
                        return r;
                    }
                }
                continue;
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public ActivityRecord getTopActivity() {
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            ActivityRecord r = this.mTaskHistory.get(taskNdx).getTopActivity();
            if (r != null) {
                return r;
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public final TaskRecord topTask() {
        int size = this.mTaskHistory.size();
        if (size > 0) {
            return this.mTaskHistory.get(size - 1);
        }
        return null;
    }

    private TaskRecord bottomTask() {
        if (this.mTaskHistory.isEmpty()) {
            return null;
        }
        return this.mTaskHistory.get(0);
    }

    /* access modifiers changed from: package-private */
    public TaskRecord taskForIdLocked(int id) {
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            TaskRecord task = this.mTaskHistory.get(taskNdx);
            if (task.taskId == id) {
                return task;
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public ActivityRecord isInStackLocked(IBinder token) {
        return isInStackLocked(ActivityRecord.forTokenLocked(token));
    }

    /* access modifiers changed from: package-private */
    public ActivityRecord isInStackLocked(ActivityRecord r) {
        if (r == null) {
            return null;
        }
        TaskRecord task = r.getTaskRecord();
        ActivityStack stack = r.getActivityStack();
        if (stack == null || !task.mActivities.contains(r) || !this.mTaskHistory.contains(task)) {
            return null;
        }
        if (stack != this) {
            Slog.w("ActivityTaskManager", "Illegal state! task does not point to stack it is in.");
        }
        return r;
    }

    /* access modifiers changed from: package-private */
    public boolean isInStackLocked(TaskRecord task) {
        return this.mTaskHistory.contains(task);
    }

    /* access modifiers changed from: package-private */
    public boolean isUidPresent(int uid) {
        Iterator<TaskRecord> it = this.mTaskHistory.iterator();
        while (it.hasNext()) {
            Iterator<ActivityRecord> it2 = it.next().mActivities.iterator();
            while (true) {
                if (it2.hasNext()) {
                    if (it2.next().getUid() == uid) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void getPresentUIDs(IntArray presentUIDs) {
        Iterator<TaskRecord> it = this.mTaskHistory.iterator();
        while (it.hasNext()) {
            Iterator<ActivityRecord> it2 = it.next().mActivities.iterator();
            while (it2.hasNext()) {
                presentUIDs.add(it2.next().getUid());
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isSingleTaskInstance() {
        ActivityDisplay display = getDisplay();
        return display != null && display.isSingleTaskInstance();
    }

    /* access modifiers changed from: package-private */
    public final void removeActivitiesFromLRUListLocked(TaskRecord task) {
        Iterator<ActivityRecord> it = task.mActivities.iterator();
        while (it.hasNext()) {
            this.mLRUActivities.remove(it.next());
        }
    }

    /* access modifiers changed from: package-private */
    public final boolean updateLRUListLocked(ActivityRecord r) {
        boolean hadit = this.mLRUActivities.remove(r);
        this.mLRUActivities.add(r);
        return hadit;
    }

    /* access modifiers changed from: package-private */
    public final boolean isHomeOrRecentsStack() {
        return isActivityTypeHome() || isActivityTypeRecents();
    }

    /* access modifiers changed from: package-private */
    public final boolean isOnHomeDisplay() {
        return this.mDisplayId == 0;
    }

    private boolean returnsToHomeStack() {
        if (inMultiWindowMode() || this.mTaskHistory.isEmpty() || !this.mTaskHistory.get(0).returnsToHomeStack()) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public void moveToFront(String reason) {
        moveToFront(reason, (TaskRecord) null);
    }

    /* access modifiers changed from: package-private */
    public void moveToFront(String reason, TaskRecord task) {
        ActivityStack topFullScreenStack;
        if (isAttached() && !this.mService.mGestureController.isRecentsStackLaunchBehind(this)) {
            ActivityDisplay display = getDisplay();
            if (inSplitScreenSecondaryWindowingMode() && (topFullScreenStack = display.getTopStackInWindowingMode(1)) != null) {
                ActivityStack primarySplitScreenStack = display.getSplitScreenPrimaryStack();
                if (display.getIndexOf(topFullScreenStack) > display.getIndexOf(primarySplitScreenStack)) {
                    primarySplitScreenStack.moveToFront(reason + " splitScreenToTop");
                }
            }
            if (!isActivityTypeHome() && returnsToHomeStack()) {
                display.moveHomeStackToFront(reason + " returnToHome");
            }
            boolean z = false;
            boolean movingTask = task != null;
            if (!movingTask) {
                z = true;
            }
            display.positionChildAtTop(this, z, reason);
            if (movingTask) {
                insertTaskAtTop(task, (ActivityRecord) null);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void moveToBack(String reason, TaskRecord task) {
        if (isAttached()) {
            if (getWindowingMode() == 3) {
                setWindowingMode(0);
            }
            if (getWindowingMode() == 4) {
                getDisplay().moveHomeStackToFront(reason);
                return;
            }
            getDisplay().positionChildAtBottom(this, reason);
            if (task != null) {
                insertTaskAtBottom(task);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isFocusable() {
        ActivityRecord r = topRunningActivityLocked();
        return this.mRootActivityContainer.isFocusable(this, r != null && r.isFocusable());
    }

    /* access modifiers changed from: package-private */
    public boolean isFocusableAndVisible() {
        return isFocusable() && shouldBeVisible((ActivityRecord) null);
    }

    /* access modifiers changed from: package-private */
    public final boolean isAttached() {
        ActivityDisplay display = getDisplay();
        return display != null && !display.isRemoved();
    }

    /* access modifiers changed from: package-private */
    public void findTaskLocked(ActivityRecord target, RootActivityContainer.FindTaskResult result) {
        ActivityInfo info;
        boolean z;
        boolean taskIsDocument;
        Uri taskDocumentData;
        ActivityRecord activityRecord = target;
        RootActivityContainer.FindTaskResult findTaskResult = result;
        Intent intent = activityRecord.intent;
        ActivityInfo info2 = activityRecord.info;
        ComponentName cls = intent.getComponent();
        if (info2.targetActivity != null) {
            cls = new ComponentName(info2.packageName, info2.targetActivity);
        }
        int userId = UserHandle.getUserId(info2.applicationInfo.uid);
        boolean z2 = true;
        boolean isDocument = intent.isDocument() & true;
        Uri documentData = isDocument ? intent.getData() : null;
        int taskNdx = this.mTaskHistory.size() - 1;
        while (taskNdx >= 0) {
            TaskRecord task = this.mTaskHistory.get(taskNdx);
            if (task.voiceSession != null) {
                info = info2;
                z = z2;
            } else if (task.userId != userId) {
                info = info2;
                z = z2;
            } else {
                ActivityRecord r = task.getTopActivity(false);
                if (r == null || r.finishing) {
                    info = info2;
                    z = z2;
                } else if (r.mUserId != userId && !ActivityStackInjector.isAllowCross(r.mUserId, userId)) {
                    info = info2;
                    z = z2;
                } else if (r.launchMode == 3) {
                    info = info2;
                    z = z2;
                } else if (!r.hasCompatibleActivityType(activityRecord)) {
                    info = info2;
                    z = z2;
                } else {
                    Intent taskIntent = task.intent;
                    Intent affinityIntent = task.affinityIntent;
                    if (taskIntent != null && taskIntent.isDocument()) {
                        taskIsDocument = true;
                        taskDocumentData = taskIntent.getData();
                    } else if (affinityIntent == null || !affinityIntent.isDocument()) {
                        taskIsDocument = false;
                        taskDocumentData = null;
                    } else {
                        taskIsDocument = true;
                        taskDocumentData = affinityIntent.getData();
                    }
                    if (task.realActivity != null && task.realActivity.compareTo(cls) == 0 && Objects.equals(documentData, taskDocumentData)) {
                        findTaskResult.mRecord = r;
                        findTaskResult.mIdealMatch = true;
                        ActivityInfo activityInfo = info2;
                        return;
                    } else if (affinityIntent != null && affinityIntent.getComponent() != null && affinityIntent.getComponent().compareTo(cls) == 0 && Objects.equals(documentData, taskDocumentData)) {
                        findTaskResult.mRecord = r;
                        findTaskResult.mIdealMatch = true;
                        ActivityInfo activityInfo2 = info2;
                        return;
                    } else if (isDocument || taskIsDocument || findTaskResult.mRecord != null || task.rootAffinity == null) {
                        info = info2;
                        if (ActivityStackInjector.findMatchTask(activityRecord, task, this.mTaskHistory)) {
                            Slog.d("ActivityTaskManager", "MIUI found matching task for " + intent + " bringing to top: " + r.intent);
                            findTaskResult.mRecord = r;
                            findTaskResult.mIdealMatch = true;
                            return;
                        }
                        z = true;
                    } else {
                        info = info2;
                        if (task.rootAffinity.equals(activityRecord.taskAffinity)) {
                            findTaskResult.mRecord = r;
                            findTaskResult.mIdealMatch = false;
                            z = true;
                        } else {
                            z = true;
                        }
                    }
                }
            }
            taskNdx--;
            z2 = z;
            info2 = info;
        }
    }

    /* access modifiers changed from: package-private */
    public ActivityRecord findActivityLocked(Intent intent, ActivityInfo info, boolean compareIntentFilters) {
        ComponentName cls = intent.getComponent();
        if (info.targetActivity != null) {
            cls = new ComponentName(info.packageName, info.targetActivity);
        }
        int userId = UserHandle.getUserId(info.applicationInfo.uid);
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            ArrayList<ActivityRecord> activities = this.mTaskHistory.get(taskNdx).mActivities;
            for (int activityNdx = activities.size() - 1; activityNdx >= 0; activityNdx--) {
                ActivityRecord r = activities.get(activityNdx);
                if (r.okToShowLocked() && !r.finishing && r.mUserId == userId) {
                    if (compareIntentFilters) {
                        if (r.intent.filterEquals(intent)) {
                            return r;
                        }
                    } else if (r.intent.getComponent().equals(cls)) {
                        return r;
                    }
                }
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public final void switchUserLocked(int userId) {
        if (this.mCurrentUser != userId) {
            this.mCurrentUser = userId;
            int index = this.mTaskHistory.size();
            int i = 0;
            while (i < index) {
                TaskRecord task = this.mTaskHistory.get(i);
                if (task.okToShowLocked()) {
                    this.mTaskHistory.remove(i);
                    this.mTaskHistory.add(task);
                    index--;
                } else {
                    i++;
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void minimalResumeActivityLocked(ActivityRecord r) {
        r.setState(ActivityState.RESUMED, "minimalResumeActivityLocked");
        r.completeResumeLocked();
    }

    private void clearLaunchTime(ActivityRecord r) {
        if (!this.mStackSupervisor.mWaitingActivityLaunched.isEmpty()) {
            this.mStackSupervisor.removeTimeoutsForActivityLocked(r);
            this.mStackSupervisor.scheduleIdleTimeoutLocked(r);
        }
    }

    /* access modifiers changed from: package-private */
    public void awakeFromSleepingLocked() {
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            ArrayList<ActivityRecord> activities = this.mTaskHistory.get(taskNdx).mActivities;
            for (int activityNdx = activities.size() - 1; activityNdx >= 0; activityNdx--) {
                activities.get(activityNdx).setSleeping(false);
            }
        }
        if (this.mPausingActivity != null) {
            Slog.d("ActivityTaskManager", "awakeFromSleepingLocked: previously pausing activity didn't pause");
            activityPausedLocked(this.mPausingActivity.appToken, true);
        }
    }

    /* access modifiers changed from: package-private */
    public void updateActivityApplicationInfoLocked(ApplicationInfo aInfo) {
        String packageName = aInfo.packageName;
        int userId = UserHandle.getUserId(aInfo.uid);
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            List<ActivityRecord> activities = this.mTaskHistory.get(taskNdx).mActivities;
            for (int activityNdx = activities.size() - 1; activityNdx >= 0; activityNdx--) {
                ActivityRecord ar = activities.get(activityNdx);
                if (userId == ar.mUserId && packageName.equals(ar.packageName)) {
                    ar.updateApplicationInfo(aInfo);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void checkReadyForSleep() {
        if (shouldSleepActivities() && goToSleepIfPossible(false)) {
            this.mStackSupervisor.checkReadyForSleepLocked(true);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean goToSleepIfPossible(boolean shuttingDown) {
        boolean shouldSleep = true;
        if (this.mResumedActivity != null) {
            startPausingLocked(false, true, (ActivityRecord) null, false);
            shouldSleep = false;
        } else if (this.mPausingActivity != null) {
            shouldSleep = false;
        }
        if (!shuttingDown) {
            if (containsActivityFromStack(this.mStackSupervisor.mStoppingActivities)) {
                this.mStackSupervisor.scheduleIdleLocked();
                shouldSleep = false;
            }
            if (containsActivityFromStack(this.mStackSupervisor.mGoingToSleepActivities)) {
                shouldSleep = false;
            }
        }
        if (shouldSleep) {
            goToSleep();
        }
        return shouldSleep;
    }

    /* access modifiers changed from: package-private */
    public void goToSleep() {
        ensureActivitiesVisibleLocked((ActivityRecord) null, 0, false);
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            ArrayList<ActivityRecord> activities = this.mTaskHistory.get(taskNdx).mActivities;
            for (int activityNdx = activities.size() - 1; activityNdx >= 0; activityNdx--) {
                ActivityRecord r = activities.get(activityNdx);
                if (r.isState(ActivityState.STOPPING, ActivityState.STOPPED, ActivityState.PAUSED, ActivityState.PAUSING)) {
                    r.setSleeping(true);
                }
            }
        }
    }

    private boolean containsActivityFromStack(List<ActivityRecord> rs) {
        for (ActivityRecord r : rs) {
            if (r.getActivityStack() == this) {
                return true;
            }
        }
        return false;
    }

    private void schedulePauseTimeout(ActivityRecord r) {
        Message msg = this.mHandler.obtainMessage(101);
        msg.obj = r;
        r.pauseTime = SystemClock.uptimeMillis();
        this.mHandler.sendMessageDelayed(msg, 500);
    }

    /* access modifiers changed from: package-private */
    public final boolean startPausingLocked(boolean userLeaving, boolean uiSleeping, ActivityRecord resuming, boolean pauseImmediately) {
        if (this.mPausingActivity != null) {
            Slog.wtf("ActivityTaskManager", "Going to pause when pause is already pending for " + this.mPausingActivity + " state=" + this.mPausingActivity.getState());
            if (!shouldSleepActivities()) {
                completePauseLocked(false, resuming);
            }
        }
        ActivityRecord prev = this.mResumedActivity;
        if (prev == null) {
            if (resuming == null) {
                Slog.wtf("ActivityTaskManager", "Trying to pause when nothing is resumed");
                this.mRootActivityContainer.resumeFocusedStacksTopActivities();
            }
            return false;
        } else if (prev == resuming) {
            Slog.wtf("ActivityTaskManager", "Trying to pause activity that is in process of being resumed");
            return false;
        } else if (prev.mIsCastMode) {
            Slog.i("ActivityTaskManager", "current activity should not stop, activity:" + prev.shortComponentName);
            return false;
        } else {
            ActivityTrigger activityTrigger = mActivityTrigger;
            if (activityTrigger != null) {
                activityTrigger.activityPauseTrigger(prev.intent, prev.info, prev.appInfo);
            }
            if (!(mActivityPluginDelegate == null || getWindowingMode() == 0)) {
                ActivityPluginDelegate activityPluginDelegate = mActivityPluginDelegate;
                ActivityPluginDelegate.activitySuspendNotification(prev.appInfo.packageName, getWindowingMode() == 1, true);
            }
            this.mPausingActivity = prev;
            this.mLastPausedActivity = prev;
            this.mLastNoHistoryActivity = ((prev.intent.getFlags() & 1073741824) == 0 && (prev.info.flags & 128) == 0) ? null : prev;
            prev.setState(ActivityState.PAUSING, "startPausingLocked");
            prev.getTaskRecord().touchActiveTime();
            clearLaunchTime(prev);
            this.mService.updateCpuStats();
            if (prev.attachedToProcess()) {
                try {
                    int pid = prev.app != null ? prev.app.getPid() : prev.mUserId;
                    int identityHashCode = System.identityHashCode(prev);
                    String str = prev.shortComponentName;
                    EventLogTags.writeAmPauseActivity(pid, identityHashCode, str, "userLeaving=" + userLeaving);
                    this.mService.getLifecycleManager().scheduleTransaction(prev.app.getThread(), (IBinder) prev.appToken, (ActivityLifecycleItem) PauseActivityItem.obtain(prev.finishing, userLeaving, prev.configChangeFlags, pauseImmediately));
                } catch (Exception e) {
                    Slog.w("ActivityTaskManager", "Exception thrown during pause", e);
                    this.mPausingActivity = null;
                    this.mLastPausedActivity = null;
                    this.mLastNoHistoryActivity = null;
                }
            } else {
                this.mPausingActivity = null;
                this.mLastPausedActivity = null;
                this.mLastNoHistoryActivity = null;
            }
            if (!uiSleeping && !this.mService.isSleepingOrShuttingDownLocked()) {
                this.mStackSupervisor.acquireLaunchWakelock();
            }
            if (this.mPausingActivity != null) {
                if (!uiSleeping) {
                    prev.pauseKeyDispatchingLocked();
                }
                if (pauseImmediately) {
                    completePauseLocked(false, resuming);
                    return false;
                }
                schedulePauseTimeout(prev);
                return true;
            }
            if (resuming == null) {
                this.mRootActivityContainer.resumeFocusedStacksTopActivities();
            }
            return false;
        }
    }

    /* access modifiers changed from: package-private */
    public final void activityPausedLocked(IBinder token, boolean timeout) {
        String str;
        ActivityRecord r = isInStackLocked(token);
        if (r != null) {
            this.mHandler.removeMessages(101, r);
            if (this.mPausingActivity == r) {
                this.mService.mWindowManager.deferSurfaceLayout();
                try {
                    completePauseLocked(true, (ActivityRecord) null);
                    return;
                } finally {
                    this.mService.mWindowManager.continueSurfaceLayout();
                }
            } else {
                Object[] objArr = new Object[4];
                objArr[0] = Integer.valueOf(r.app != null ? r.app.getPid() : 0);
                objArr[1] = Integer.valueOf(System.identityHashCode(r));
                objArr[2] = r.shortComponentName;
                ActivityRecord activityRecord = this.mPausingActivity;
                if (activityRecord != null) {
                    str = activityRecord.shortComponentName;
                } else {
                    str = "(none)";
                }
                objArr[3] = str;
                EventLog.writeEvent(EventLogTags.AM_FAILED_TO_PAUSE, objArr);
                if (r.isState(ActivityState.PAUSING)) {
                    r.setState(ActivityState.PAUSED, "activityPausedLocked");
                    if (r.finishing) {
                        finishCurrentActivityLocked(r, 2, false, "activityPausedLocked");
                    }
                }
            }
        }
        this.mRootActivityContainer.ensureActivitiesVisible((ActivityRecord) null, 0, false);
    }

    private void completePauseLocked(boolean resumeNext, ActivityRecord resuming) {
        ActivityRecord prev = this.mPausingActivity;
        if (prev != null) {
            prev.setWillCloseOrEnterPip(false);
            boolean wasStopping = prev.isState(ActivityState.STOPPING);
            prev.setState(ActivityState.PAUSED, "completePausedLocked");
            if (prev.finishing) {
                prev = finishCurrentActivityLocked(prev, 2, false, "completePausedLocked");
            } else if (!prev.hasProcess()) {
                prev = null;
            } else if (prev.deferRelaunchUntilPaused) {
                prev.relaunchActivityLocked(false, prev.preserveWindowOnDeferredRelaunch);
            } else if (wasStopping) {
                prev.setState(ActivityState.STOPPING, "completePausedLocked");
            } else if (!prev.visible || shouldSleepOrShutDownActivities()) {
                prev.setDeferHidingClient(false);
                addToStopping(prev, true, false, "completePauseLocked");
            }
            if (prev != null) {
                prev.stopFreezingScreenLocked(true);
            }
            this.mPausingActivity = null;
        }
        if (resumeNext) {
            ActivityStack topStack = this.mRootActivityContainer.getTopDisplayFocusedStack();
            if (!topStack.shouldSleepOrShutDownActivities()) {
                this.mRootActivityContainer.resumeFocusedStacksTopActivities(topStack, prev, (ActivityOptions) null);
            } else {
                checkReadyForSleep();
                ActivityRecord top = topStack.topRunningActivityLocked();
                if (top == null || !(prev == null || top == prev)) {
                    this.mRootActivityContainer.resumeFocusedStacksTopActivities();
                }
            }
        }
        if (prev != null) {
            prev.resumeKeyDispatchingLocked();
            if (prev.hasProcess() && prev.cpuTimeAtResume > 0) {
                long diff = prev.app.getCpuTime() - prev.cpuTimeAtResume;
                if (diff > 0) {
                    this.mService.mH.post(PooledLambda.obtainRunnable($$Lambda$1636dquQO0UvkFayOGf_gceB4iw.INSTANCE, this.mService.mAmInternal, prev.info.packageName, Integer.valueOf(prev.info.applicationInfo.uid), Long.valueOf(diff)));
                }
            }
            prev.cpuTimeAtResume = 0;
        }
        if (this.mStackSupervisor.mAppVisibilitiesChangedSinceLastPause || (getDisplay() != null && getDisplay().hasPinnedStack())) {
            this.mService.getTaskChangeNotificationController().notifyTaskStackChanged();
            this.mStackSupervisor.mAppVisibilitiesChangedSinceLastPause = false;
        }
        this.mRootActivityContainer.ensureActivitiesVisible(resuming, 0, false);
    }

    private void addToStopping(ActivityRecord r, boolean scheduleIdle, boolean idleDelayed, String reason) {
        boolean z = false;
        if (!this.mStackSupervisor.mStoppingActivities.contains(r)) {
            EventLog.writeEvent(EventLogTags.AM_ADD_TO_STOPPING, new Object[]{Integer.valueOf(r.mUserId), Integer.valueOf(System.identityHashCode(r)), r.shortComponentName, reason});
            this.mStackSupervisor.mStoppingActivities.add(r);
        }
        if (this.mStackSupervisor.mStoppingActivities.size() > 3 || (r.frontOfTask && this.mTaskHistory.size() <= 1)) {
            z = true;
        }
        boolean forceIdle = z;
        if (!scheduleIdle && !forceIdle) {
            checkReadyForSleep();
        } else if (!idleDelayed) {
            this.mStackSupervisor.scheduleIdleLocked();
        } else {
            this.mStackSupervisor.scheduleIdleTimeoutLocked(r);
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean isStackTranslucent(ActivityRecord starting) {
        if (!isAttached() || this.mForceHidden) {
            return true;
        }
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            ArrayList<ActivityRecord> activities = this.mTaskHistory.get(taskNdx).mActivities;
            for (int activityNdx = activities.size() - 1; activityNdx >= 0; activityNdx--) {
                ActivityRecord r = activities.get(activityNdx);
                if (!r.finishing && (r.visibleIgnoringKeyguard || r == starting)) {
                    if (r.fullscreen) {
                        return false;
                    }
                    if (r.hasWallpaper && !ActivityStackInjector.ignoreWallpaper(r)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean isTopStackOnDisplay() {
        ActivityDisplay display = getDisplay();
        return display != null && display.isTopStack(this);
    }

    /* access modifiers changed from: package-private */
    public boolean isFocusedStackOnDisplay() {
        ActivityDisplay display = getDisplay();
        return display != null && this == display.getFocusedStack();
    }

    /* access modifiers changed from: package-private */
    public boolean isTopActivityVisible() {
        ActivityRecord topActivity = getTopActivity();
        return topActivity != null && topActivity.visible;
    }

    /* access modifiers changed from: package-private */
    public boolean shouldBeVisible(ActivityRecord starting) {
        return getVisibility(starting) != 2;
    }

    /* access modifiers changed from: package-private */
    @StackVisibility
    public int getVisibility(ActivityRecord starting) {
        ActivityDisplay display;
        ActivityRecord activityRecord = starting;
        if (!isAttached() || this.mForceHidden) {
            return 2;
        }
        ActivityDisplay display2 = getDisplay();
        boolean gotSplitScreenStack = false;
        boolean gotOpaqueSplitScreenPrimary = false;
        boolean gotOpaqueSplitScreenSecondary = false;
        boolean gotTranslucentFullscreen = false;
        boolean gotTranslucentSplitScreenPrimary = false;
        boolean gotTranslucentSplitScreenSecondary = false;
        boolean shouldBeVisible = true;
        int windowingMode = getWindowingMode();
        boolean isAssistantType = isActivityTypeAssistant();
        boolean z = true;
        int i = display2.getChildCount() - 1;
        while (true) {
            if (i < 0) {
                break;
            }
            ActivityStack other = display2.getChildAt(i);
            boolean hasRunningActivities = other.topRunningActivityLocked() != null ? z : false;
            if (other == this) {
                shouldBeVisible = (hasRunningActivities || isInStackLocked(starting) != null || isActivityTypeHome()) ? z : false;
                ActivityDisplay activityDisplay = display2;
            } else {
                if (!hasRunningActivities) {
                    display = display2;
                } else {
                    int otherWindowingMode = other.getWindowingMode();
                    if (otherWindowingMode == z) {
                        int activityType = other.getActivityType();
                        display = display2;
                        if (windowingMode == 3 && (activityType == 2 || (activityType == 4 && this.mWindowManager.getRecentsAnimationController() != null))) {
                            break;
                        } else if (!other.isStackTranslucent(activityRecord)) {
                            return 2;
                        } else {
                            gotTranslucentFullscreen = true;
                        }
                    } else {
                        display = display2;
                        if (otherWindowingMode == 3 && !gotOpaqueSplitScreenPrimary) {
                            gotSplitScreenStack = true;
                            gotTranslucentSplitScreenPrimary = other.isStackTranslucent(activityRecord);
                            gotOpaqueSplitScreenPrimary = !gotTranslucentSplitScreenPrimary;
                            if (windowingMode == 3 && gotOpaqueSplitScreenPrimary) {
                                return 2;
                            }
                        } else if (otherWindowingMode == 4 && !gotOpaqueSplitScreenSecondary) {
                            gotSplitScreenStack = true;
                            gotTranslucentSplitScreenSecondary = other.isStackTranslucent(activityRecord);
                            gotOpaqueSplitScreenSecondary = !gotTranslucentSplitScreenSecondary;
                            if (windowingMode == 4 && gotOpaqueSplitScreenSecondary) {
                                return 2;
                            }
                        }
                        if (gotOpaqueSplitScreenPrimary && gotOpaqueSplitScreenSecondary) {
                            return 2;
                        }
                        if (isAssistantType && gotSplitScreenStack) {
                            return 2;
                        }
                    }
                }
                i--;
                display2 = display;
                z = true;
            }
        }
        if (!shouldBeVisible) {
            return 2;
        }
        if (windowingMode != 1) {
            if (windowingMode != 3) {
                if (windowingMode == 4 && gotTranslucentSplitScreenSecondary) {
                    return 1;
                }
            } else if (gotTranslucentSplitScreenPrimary) {
                return 1;
            }
        } else if (gotTranslucentSplitScreenPrimary || gotTranslucentSplitScreenSecondary) {
            return 1;
        }
        if (gotTranslucentFullscreen) {
            return 1;
        }
        return 0;
    }

    /* access modifiers changed from: package-private */
    public final int rankTaskLayers(int baseLayer) {
        int layer = 0;
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            TaskRecord task = this.mTaskHistory.get(taskNdx);
            ActivityRecord r = task.topRunningActivityLocked();
            if (r == null || r.finishing || !r.visible) {
                task.mLayerRank = -1;
            } else {
                task.mLayerRank = layer + baseLayer;
                layer++;
            }
        }
        return layer;
    }

    /* access modifiers changed from: package-private */
    public final void ensureActivitiesVisibleLocked(ActivityRecord starting, int configChanges, boolean preserveWindows) {
        ensureActivitiesVisibleLocked(starting, configChanges, preserveWindows, true);
    }

    /* access modifiers changed from: package-private */
    public final void ensureActivitiesVisibleLocked(ActivityRecord starting, int configChanges, boolean preserveWindows, boolean notifyClients) {
        int activityNdx;
        ArrayList<ActivityRecord> activities;
        boolean behindFullscreenActivity;
        boolean forceVisible;
        ActivityRecord r;
        int activityNdx2;
        ActivityRecord activityRecord = starting;
        boolean z = notifyClients;
        boolean z2 = false;
        this.mTopActivityOccludesKeyguard = false;
        this.mTopDismissingKeyguardActivity = null;
        this.mStackSupervisor.getKeyguardController().beginActivityVisibilityUpdate();
        try {
            ActivityRecord top = topRunningActivityLocked();
            if (top != null) {
                checkTranslucentActivityWaiting(top);
            }
            boolean aboveTop = top != null;
            boolean stackShouldBeVisible = shouldBeVisible(starting);
            boolean behindFullscreenActivity2 = !stackShouldBeVisible;
            boolean resumeNextActivity = isFocusable() && isInStackLocked(starting) == null;
            int taskNdx = this.mTaskHistory.size() - 1;
            int configChanges2 = configChanges;
            while (taskNdx >= 0) {
                try {
                    ArrayList<ActivityRecord> activities2 = this.mTaskHistory.get(taskNdx).mActivities;
                    int activityNdx3 = activities2.size() - 1;
                    boolean resumeNextActivity2 = resumeNextActivity;
                    int configChanges3 = configChanges2;
                    while (activityNdx >= 0) {
                        try {
                            ActivityRecord r2 = activities2.get(activityNdx);
                            if (!r2.finishing) {
                                boolean isTop = r2 == top ? true : z2;
                                if (!aboveTop || isTop) {
                                    boolean visibleIgnoringKeyguard = r2.shouldBeVisibleIgnoringKeyguard(behindFullscreenActivity2);
                                    boolean reallyVisible = r2.shouldBeVisible(behindFullscreenActivity2);
                                    if (visibleIgnoringKeyguard) {
                                        behindFullscreenActivity = updateBehindFullscreen(!stackShouldBeVisible ? true : z2, behindFullscreenActivity2, r2);
                                    } else {
                                        behindFullscreenActivity = behindFullscreenActivity2;
                                    }
                                    if (this.mService.isSleepingLocked()) {
                                        r2.mDisableDummyVisible = true;
                                    } else if (r2.mDisableDummyVisible && reallyVisible) {
                                        r2.mDisableDummyVisible = z2;
                                    }
                                    if (!this.mWindowManager.isGestureOpen() || !ActivityStackInjector.isMiuiHome(r2)) {
                                        if (r2.mIsDummyVisible && !this.mWindowManager.isGestureOpen()) {
                                            r2.setDummyVisible(z2, reallyVisible);
                                        }
                                        forceVisible = false;
                                    } else {
                                        boolean forceVisible2 = (reallyVisible || r2.mDisableDummyVisible || !ActivityStackInjector.isCurrentUser(r2.mUserId)) ? z2 : true;
                                        ActivityStack topFocusedStack = this.mRootActivityContainer.getTopDisplayFocusedStack();
                                        if (topFocusedStack == null || topFocusedStack.isActivityTypeHome() || forceVisible2) {
                                            r2.setDummyVisible(forceVisible2, reallyVisible);
                                        }
                                        forceVisible = forceVisible2;
                                    }
                                    if (!reallyVisible) {
                                        if (!forceVisible) {
                                            makeInvisible(r2);
                                            activities = activities2;
                                            aboveTop = false;
                                            behindFullscreenActivity2 = behindFullscreenActivity;
                                            activityNdx3 = activityNdx - 1;
                                            activities2 = activities;
                                            z2 = false;
                                        }
                                    }
                                    if (r2 == activityRecord || !z) {
                                        boolean z3 = preserveWindows;
                                    } else {
                                        r2.ensureActivityConfiguration(z2 ? 1 : 0, preserveWindows, true);
                                    }
                                    if (!r2.attachedToProcess()) {
                                        boolean z4 = reallyVisible;
                                        ActivityRecord r3 = r2;
                                        activityNdx2 = activityNdx;
                                        activities = activities2;
                                        if (makeVisibleAndRestartIfNeeded(starting, configChanges3, isTop, resumeNextActivity2, r3)) {
                                            if (activityNdx2 >= activities.size()) {
                                                activityNdx = activities.size() - 1;
                                                r = r3;
                                            } else {
                                                activityNdx = activityNdx2;
                                                resumeNextActivity2 = false;
                                                r = r3;
                                            }
                                            configChanges3 |= r.configChangeFlags;
                                            aboveTop = false;
                                            behindFullscreenActivity2 = behindFullscreenActivity;
                                            activityNdx3 = activityNdx - 1;
                                            activities2 = activities;
                                            z2 = false;
                                        } else {
                                            r = r3;
                                        }
                                    } else {
                                        activityNdx2 = activityNdx;
                                        activities = activities2;
                                        r = r2;
                                        if (r.visible) {
                                            if (r.mClientVisibilityDeferred && z) {
                                                r.makeClientVisible();
                                            }
                                            if (r.handleAlreadyVisible()) {
                                                resumeNextActivity2 = false;
                                            }
                                            if (z) {
                                                r.makeActiveIfNeeded(activityRecord);
                                            }
                                            activityNdx = activityNdx2;
                                            configChanges3 |= r.configChangeFlags;
                                            aboveTop = false;
                                            behindFullscreenActivity2 = behindFullscreenActivity;
                                            activityNdx3 = activityNdx - 1;
                                            activities2 = activities;
                                            z2 = false;
                                        } else {
                                            r.makeVisibleIfNeeded(activityRecord, z);
                                        }
                                    }
                                    activityNdx = activityNdx2;
                                    configChanges3 |= r.configChangeFlags;
                                    aboveTop = false;
                                    behindFullscreenActivity2 = behindFullscreenActivity;
                                    activityNdx3 = activityNdx - 1;
                                    activities2 = activities;
                                    z2 = false;
                                }
                            }
                            activities = activities2;
                            activityNdx3 = activityNdx - 1;
                            activities2 = activities;
                            z2 = false;
                        } catch (Throwable th) {
                            th = th;
                            this.mStackSupervisor.getKeyguardController().endActivityVisibilityUpdate();
                            throw th;
                        }
                    }
                    int i = activityNdx;
                    ArrayList<ActivityRecord> arrayList = activities2;
                    if (getWindowingMode() == 5) {
                        behindFullscreenActivity2 = !stackShouldBeVisible;
                    } else if (isActivityTypeHome()) {
                        behindFullscreenActivity2 = true;
                    }
                    taskNdx--;
                    configChanges2 = configChanges3;
                    resumeNextActivity = resumeNextActivity2;
                    z2 = false;
                } catch (Throwable th2) {
                    th = th2;
                    int i2 = configChanges2;
                    this.mStackSupervisor.getKeyguardController().endActivityVisibilityUpdate();
                    throw th;
                }
            }
            if (this.mTranslucentActivityWaiting != null && this.mUndrawnActivitiesBelowTopTranslucent.isEmpty()) {
                notifyActivityDrawnLocked((ActivityRecord) null);
            }
            this.mStackSupervisor.getKeyguardController().endActivityVisibilityUpdate();
        } catch (Throwable th3) {
            th = th3;
            int i3 = configChanges;
            this.mStackSupervisor.getKeyguardController().endActivityVisibilityUpdate();
            throw th;
        }
    }

    /* access modifiers changed from: package-private */
    public void addStartingWindowsForVisibleActivities(boolean taskSwitch) {
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            this.mTaskHistory.get(taskNdx).addStartingWindowsForVisibleActivities(taskSwitch);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean topActivityOccludesKeyguard() {
        return this.mTopActivityOccludesKeyguard;
    }

    /* access modifiers changed from: package-private */
    public boolean resizeStackWithLaunchBounds() {
        return inPinnedWindowingMode();
    }

    public boolean supportsSplitScreenWindowingMode() {
        TaskRecord topTask = topTask();
        return super.supportsSplitScreenWindowingMode() && (topTask == null || topTask.supportsSplitScreenWindowingMode());
    }

    /* access modifiers changed from: package-private */
    public boolean affectedBySplitScreenResize() {
        int windowingMode;
        if (!supportsSplitScreenWindowingMode() || (windowingMode = getWindowingMode()) == 5 || windowingMode == 2) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public ActivityRecord getTopDismissingKeyguardActivity() {
        return this.mTopDismissingKeyguardActivity;
    }

    /* access modifiers changed from: package-private */
    public boolean checkKeyguardVisibility(ActivityRecord r, boolean shouldBeVisible, boolean isTop) {
        int displayId = this.mDisplayId;
        if (displayId == -1) {
            displayId = 0;
        }
        boolean keyguardOrAodShowing = this.mStackSupervisor.getKeyguardController().isKeyguardOrAodShowing(displayId);
        boolean keyguardLocked = this.mStackSupervisor.getKeyguardController().isKeyguardLocked();
        boolean showWhenLocked = r.canShowWhenLocked();
        boolean dismissKeyguard = r.mAppWindowToken != null && r.mAppWindowToken.containsDismissKeyguardWindow();
        if (shouldBeVisible) {
            if (dismissKeyguard && this.mTopDismissingKeyguardActivity == null) {
                this.mTopDismissingKeyguardActivity = r;
            }
            if (isTop) {
                this.mTopActivityOccludesKeyguard |= showWhenLocked;
            }
            if (canShowWithInsecureKeyguard() && this.mStackSupervisor.getKeyguardController().canDismissKeyguard()) {
                return true;
            }
        }
        if (keyguardOrAodShowing) {
            if (!shouldBeVisible || !this.mStackSupervisor.getKeyguardController().canShowActivityWhileKeyguardShowing(r, dismissKeyguard)) {
                return false;
            }
            return true;
        } else if (!keyguardLocked) {
            return shouldBeVisible;
        } else {
            if (!shouldBeVisible || !this.mStackSupervisor.getKeyguardController().canShowWhileOccluded(dismissKeyguard, showWhenLocked)) {
                return false;
            }
            return true;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean canShowWithInsecureKeyguard() {
        ActivityDisplay activityDisplay = getDisplay();
        if (activityDisplay != null) {
            return (activityDisplay.mDisplay.getFlags() & 32) != 0;
        }
        throw new IllegalStateException("Stack is not attached to any display, stackId=" + this.mStackId);
    }

    private void checkTranslucentActivityWaiting(ActivityRecord top) {
        if (this.mTranslucentActivityWaiting != top) {
            this.mUndrawnActivitiesBelowTopTranslucent.clear();
            if (this.mTranslucentActivityWaiting != null) {
                notifyActivityDrawnLocked((ActivityRecord) null);
                this.mTranslucentActivityWaiting = null;
            }
            this.mHandler.removeMessages(106);
        }
    }

    private boolean makeVisibleAndRestartIfNeeded(ActivityRecord starting, int configChanges, boolean isTop, boolean andResume, ActivityRecord r) {
        boolean z = false;
        if (isTop || !r.visible) {
            if (r != starting) {
                r.startFreezingScreenLocked(r.app, configChanges);
            }
            if (!r.visible || r.mLaunchTaskBehind) {
                r.setVisible(true);
            }
            if (r != starting) {
                ActivityStackSupervisor activityStackSupervisor = this.mStackSupervisor;
                if (andResume && !r.mLaunchTaskBehind) {
                    z = true;
                }
                activityStackSupervisor.startSpecificActivityLocked(r, z, true);
                return true;
            }
        }
        return false;
    }

    private void makeInvisible(ActivityRecord r) {
        if (r.getState() == ActivityState.RESUMED && r.appInfo.packageName.equals(ActivityTaskManagerServiceInjector.sPackageHoldOn)) {
            return;
        }
        if ((r.getState() != ActivityState.RESUMED || !r.mIsCastMode) && r.visible) {
            try {
                boolean canEnterPictureInPicture = r.checkEnterPictureInPictureState("makeInvisible", true);
                r.setDeferHidingClient(canEnterPictureInPicture && !r.isState(ActivityState.STOPPING, ActivityState.STOPPED, ActivityState.PAUSED));
                r.setVisible(false);
                switch (r.getState()) {
                    case STOPPING:
                    case STOPPED:
                        if (r.attachedToProcess()) {
                            this.mService.getLifecycleManager().scheduleTransaction(r.app.getThread(), (IBinder) r.appToken, (ClientTransactionItem) WindowVisibilityItem.obtain(false));
                        }
                        r.supportsEnterPipOnTaskSwitch = false;
                        return;
                    case INITIALIZING:
                    case RESUMED:
                    case PAUSING:
                    case PAUSED:
                        addToStopping(r, true, canEnterPictureInPicture, "makeInvisible");
                        return;
                    default:
                        return;
                }
            } catch (Exception e) {
                Slog.w("ActivityTaskManager", "Exception thrown making hidden: " + r.intent.getComponent(), e);
            }
        }
    }

    private boolean updateBehindFullscreen(boolean stackInvisible, boolean behindFullscreenActivity, ActivityRecord r) {
        if (r.fullscreen) {
            return true;
        }
        return behindFullscreenActivity;
    }

    /* access modifiers changed from: package-private */
    public void convertActivityToTranslucent(ActivityRecord r) {
        this.mTranslucentActivityWaiting = r;
        this.mUndrawnActivitiesBelowTopTranslucent.clear();
        this.mHandler.sendEmptyMessageDelayed(106, TRANSLUCENT_CONVERSION_TIMEOUT);
    }

    /* access modifiers changed from: package-private */
    public void clearOtherAppTimeTrackers(AppTimeTracker except) {
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            ArrayList<ActivityRecord> activities = this.mTaskHistory.get(taskNdx).mActivities;
            for (int activityNdx = activities.size() - 1; activityNdx >= 0; activityNdx--) {
                ActivityRecord r = activities.get(activityNdx);
                if (r.appTimeTracker != except) {
                    r.appTimeTracker = null;
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void notifyActivityDrawnLocked(ActivityRecord r) {
        if (r == null || (this.mUndrawnActivitiesBelowTopTranslucent.remove(r) && this.mUndrawnActivitiesBelowTopTranslucent.isEmpty())) {
            ActivityRecord waitingActivity = this.mTranslucentActivityWaiting;
            this.mTranslucentActivityWaiting = null;
            this.mUndrawnActivitiesBelowTopTranslucent.clear();
            this.mHandler.removeMessages(106);
            if (waitingActivity != null) {
                boolean z = false;
                this.mWindowManager.setWindowOpaque(waitingActivity.appToken, false);
                if (waitingActivity.attachedToProcess()) {
                    try {
                        IApplicationThread thread = waitingActivity.app.getThread();
                        IApplicationToken.Stub stub = waitingActivity.appToken;
                        if (r != null) {
                            z = true;
                        }
                        thread.scheduleTranslucentConversionComplete(stub, z);
                    } catch (RemoteException e) {
                    }
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void cancelInitializingActivities() {
        boolean z;
        ActivityRecord topActivity = topRunningActivityLocked();
        boolean aboveTop = true;
        boolean behindFullscreenActivity = false;
        if (!shouldBeVisible((ActivityRecord) null)) {
            aboveTop = false;
            behindFullscreenActivity = true;
        }
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            ArrayList<ActivityRecord> activities = this.mTaskHistory.get(taskNdx).mActivities;
            for (int activityNdx = activities.size() - 1; activityNdx >= 0; activityNdx--) {
                ActivityRecord r = activities.get(activityNdx);
                if (aboveTop) {
                    if (r == topActivity) {
                        aboveTop = false;
                    }
                    z = r.fullscreen;
                } else {
                    r.removeOrphanedStartingWindow(behindFullscreenActivity);
                    z = r.fullscreen;
                }
                behindFullscreenActivity |= z;
            }
        }
    }

    /* JADX INFO: finally extract failed */
    /* access modifiers changed from: package-private */
    @GuardedBy({"mService"})
    public boolean resumeTopActivityUncheckedLocked(ActivityRecord prev, ActivityOptions options) {
        if (this.mInResumeTopActivity) {
            return false;
        }
        try {
            this.mInResumeTopActivity = true;
            boolean result = resumeTopActivityInnerLocked(prev, options);
            ActivityRecord next = topRunningActivityLocked(true);
            if (next == null || !next.canTurnScreenOn()) {
                checkReadyForSleep();
            }
            this.mInResumeTopActivity = false;
            return result;
        } catch (Throwable th) {
            this.mInResumeTopActivity = false;
            throw th;
        }
    }

    /* access modifiers changed from: protected */
    public ActivityRecord getResumedActivity() {
        return this.mResumedActivity;
    }

    private void setResumedActivity(ActivityRecord r, String reason) {
        if (this.mResumedActivity != r) {
            this.mResumedActivity = r;
            if (this.mService.mGestureController.isRecentsStackLaunchBehind(this)) {
                this.mResumedActivity = null;
                this.mService.mGestureController.mHasResumeRecentsBehind = true;
                r.setState(ActivityState.PAUSED, "gesture");
            }
            this.mStackSupervisor.updateTopResumedActivityIfNeeded();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:276:0x0484, code lost:
        if (r7.mService.mGestureController.isRecentsStackLaunchBehind(r12.getActivityStack()) == false) goto L_0x0486;
     */
    /* JADX WARNING: Removed duplicated region for block: B:209:0x0353  */
    /* JADX WARNING: Removed duplicated region for block: B:210:0x0357  */
    /* JADX WARNING: Removed duplicated region for block: B:213:0x0367  */
    /* JADX WARNING: Removed duplicated region for block: B:290:0x0506 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x0508  */
    /* JADX WARNING: Removed duplicated region for block: B:305:0x054b  */
    @com.android.internal.annotations.GuardedBy({"mService"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean resumeTopActivityInnerLocked(com.android.server.wm.ActivityRecord r32, android.app.ActivityOptions r33) {
        /*
            r31 = this;
            r7 = r31
            r8 = r32
            r9 = r33
            com.android.server.wm.ActivityTaskManagerService r0 = r7.mService
            boolean r0 = r0.isBooting()
            r10 = 0
            if (r0 != 0) goto L_0x0018
            com.android.server.wm.ActivityTaskManagerService r0 = r7.mService
            boolean r0 = r0.isBooted()
            if (r0 != 0) goto L_0x0018
            return r10
        L_0x0018:
            r11 = 1
            com.android.server.wm.ActivityRecord r12 = r7.topRunningActivityLocked(r11)
            if (r12 == 0) goto L_0x0021
            r0 = r11
            goto L_0x0022
        L_0x0021:
            r0 = r10
        L_0x0022:
            r13 = r0
            if (r13 == 0) goto L_0x002c
            boolean r0 = r31.isAttached()
            if (r0 != 0) goto L_0x002c
            return r10
        L_0x002c:
            com.android.server.wm.RootActivityContainer r0 = r7.mRootActivityContainer
            r0.cancelInitializingActivities()
            com.android.server.wm.ActivityStackSupervisor r0 = r7.mStackSupervisor
            boolean r0 = r0.mUserLeaving
            com.android.server.wm.ActivityStackSupervisor r1 = r7.mStackSupervisor
            r1.mUserLeaving = r10
            if (r13 != 0) goto L_0x0040
            boolean r1 = r31.resumeNextFocusableActivityWhenStackIsEmpty(r32, r33)
            return r1
        L_0x0040:
            r12.delayedResume = r10
            com.android.server.wm.ActivityDisplay r14 = r31.getDisplay()
            boolean r1 = r12.mIsCastMode
            if (r1 == 0) goto L_0x0053
            r7.startPausingLocked(r0, r10, r12, r10)
            com.android.server.wm.ActivityTaskManagerService r1 = r7.mService
            r1.resumeCastActivity()
            return r11
        L_0x0053:
            com.android.server.wm.ActivityRecord r1 = r7.mResumedActivity
            if (r1 != r12) goto L_0x0069
            com.android.server.wm.ActivityStack$ActivityState r1 = com.android.server.wm.ActivityStack.ActivityState.RESUMED
            boolean r1 = r12.isState(r1)
            if (r1 == 0) goto L_0x0069
            boolean r1 = r14.allResumedActivitiesComplete()
            if (r1 == 0) goto L_0x0069
            r7.executeAppTransition(r9)
            return r10
        L_0x0069:
            boolean r1 = r12.canResumeByCompat()
            if (r1 != 0) goto L_0x0070
            return r10
        L_0x0070:
            boolean r1 = r31.shouldSleepOrShutDownActivities()
            r15 = 0
            if (r1 == 0) goto L_0x00bb
            com.android.server.wm.ActivityRecord r1 = r7.mLastPausedActivity
            if (r1 != r12) goto L_0x00bb
            com.android.server.wm.RootActivityContainer r1 = r7.mRootActivityContainer
            boolean r1 = r1.allPausedActivitiesComplete()
            if (r1 == 0) goto L_0x00bb
            r1 = 1
            com.android.server.wm.ActivityTaskManagerService r2 = r7.mService
            boolean r2 = r2.mShuttingDown
            if (r2 != 0) goto L_0x00b5
            boolean r2 = r7.mTopActivityOccludesKeyguard
            if (r2 != 0) goto L_0x0096
            boolean r2 = r12.canShowWhenLocked()
            if (r2 == 0) goto L_0x0096
            r2 = r11
            goto L_0x0097
        L_0x0096:
            r2 = r10
        L_0x0097:
            com.android.server.wm.ActivityRecord r3 = r7.mTopDismissingKeyguardActivity
            if (r3 == r12) goto L_0x00a9
            com.android.server.wm.AppWindowToken r3 = r12.mAppWindowToken
            if (r3 == 0) goto L_0x00a9
            com.android.server.wm.AppWindowToken r3 = r12.mAppWindowToken
            boolean r3 = r3.containsDismissKeyguardWindow()
            if (r3 == 0) goto L_0x00a9
            r3 = r11
            goto L_0x00aa
        L_0x00a9:
            r3 = r10
        L_0x00aa:
            if (r2 != 0) goto L_0x00ae
            if (r3 == 0) goto L_0x00b5
        L_0x00ae:
            r7.ensureActivitiesVisibleLocked(r15, r10, r10)
            boolean r1 = r31.shouldSleepActivities()
        L_0x00b5:
            if (r1 == 0) goto L_0x00bb
            r7.executeAppTransition(r9)
            return r10
        L_0x00bb:
            com.android.server.wm.ActivityTaskManagerService r1 = r7.mService
            android.app.ActivityManagerInternal r1 = r1.mAmInternal
            int r2 = r12.mUserId
            boolean r1 = r1.hasStartedUserState(r2)
            java.lang.String r6 = "ActivityTaskManager"
            if (r1 != 0) goto L_0x00ed
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Skipping resume of top activity "
            r1.append(r2)
            r1.append(r12)
            java.lang.String r2 = ": user "
            r1.append(r2)
            int r2 = r12.mUserId
            r1.append(r2)
            java.lang.String r2 = " is stopped"
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            android.util.Slog.w(r6, r1)
            return r10
        L_0x00ed:
            com.android.server.wm.ActivityStackSupervisor r1 = r7.mStackSupervisor
            java.util.ArrayList<com.android.server.wm.ActivityRecord> r1 = r1.mStoppingActivities
            r1.remove(r12)
            com.android.server.wm.ActivityStackSupervisor r1 = r7.mStackSupervisor
            java.util.ArrayList<com.android.server.wm.ActivityRecord> r1 = r1.mGoingToSleepActivities
            r1.remove(r12)
            r12.sleeping = r10
            r12.launching = r11
            com.android.internal.app.ActivityTrigger r1 = mActivityTrigger
            if (r1 == 0) goto L_0x010e
            android.content.Intent r2 = r12.intent
            android.content.pm.ActivityInfo r3 = r12.info
            android.content.pm.ApplicationInfo r4 = r12.appInfo
            boolean r5 = r12.fullscreen
            r1.activityResumeTrigger(r2, r3, r4, r5)
        L_0x010e:
            com.android.server.wm.ActivityPluginDelegate r1 = mActivityPluginDelegate
            if (r1 == 0) goto L_0x012a
            int r1 = r31.getWindowingMode()
            if (r1 == 0) goto L_0x012a
            com.android.server.wm.ActivityPluginDelegate r1 = mActivityPluginDelegate
            android.content.pm.ApplicationInfo r1 = r12.appInfo
            java.lang.String r1 = r1.packageName
            int r2 = r31.getWindowingMode()
            if (r2 != r11) goto L_0x0126
            r2 = r11
            goto L_0x0127
        L_0x0126:
            r2 = r10
        L_0x0127:
            com.android.server.wm.ActivityPluginDelegate.activityInvokeNotification(r1, r2)
        L_0x012a:
            com.android.server.wm.RootActivityContainer r1 = r7.mRootActivityContainer
            boolean r1 = r1.allPausedActivitiesComplete()
            if (r1 != 0) goto L_0x0133
            return r10
        L_0x0133:
            com.android.server.wm.ActivityStackSupervisor r1 = r7.mStackSupervisor
            android.content.pm.ActivityInfo r2 = r12.info
            android.content.pm.ApplicationInfo r2 = r2.applicationInfo
            int r2 = r2.uid
            android.content.pm.ActivityInfo r3 = r12.info
            java.lang.String r3 = r3.packageName
            r1.setLaunchSource(r2, r3)
            android.util.BoostFramework r1 = r7.mPerf
            if (r1 != 0) goto L_0x014d
            android.util.BoostFramework r1 = new android.util.BoostFramework
            r1.<init>()
            r7.mPerf = r1
        L_0x014d:
            com.android.server.wm.ActivityTaskManagerService r1 = r7.mService
            java.lang.String r2 = r12.processName
            android.content.pm.ActivityInfo r3 = r12.info
            android.content.pm.ApplicationInfo r3 = r3.applicationInfo
            int r3 = r3.uid
            com.android.server.wm.WindowProcessController r16 = r1.getProcessController((java.lang.String) r2, (int) r3)
            if (r16 == 0) goto L_0x016d
            android.app.IApplicationThread r1 = r16.getThread()
            if (r1 == 0) goto L_0x016d
            android.util.BoostFramework r1 = r7.mPerf
            r2 = 4226(0x1082, float:5.922E-42)
            java.lang.String r3 = r12.packageName
            r4 = -1
            r1.perfHint(r2, r3, r4, r11)
        L_0x016d:
            r1 = 0
            r2 = 0
            com.android.server.wm.ActivityStack r5 = r14.getLastFocusedStack()
            if (r5 == 0) goto L_0x019b
            if (r5 == r7) goto L_0x019b
            com.android.server.wm.ActivityRecord r2 = r5.mResumedActivity
            if (r0 == 0) goto L_0x0188
            boolean r3 = r31.inMultiWindowMode()
            if (r3 == 0) goto L_0x0188
            boolean r3 = r5.shouldBeVisible(r12)
            if (r3 == 0) goto L_0x0188
            r0 = 0
        L_0x0188:
            if (r2 == 0) goto L_0x0194
            java.lang.String r3 = "resumeTopActivity"
            boolean r3 = r2.checkEnterPictureInPictureState(r3, r0)
            if (r3 == 0) goto L_0x0194
            r3 = r11
            goto L_0x0195
        L_0x0194:
            r3 = r10
        L_0x0195:
            r1 = r3
            r4 = r0
            r17 = r1
            r3 = r2
            goto L_0x019f
        L_0x019b:
            r4 = r0
            r17 = r1
            r3 = r2
        L_0x019f:
            android.content.pm.ActivityInfo r0 = r12.info
            int r0 = r0.flags
            r0 = r0 & 16384(0x4000, float:2.2959E-41)
            if (r0 == 0) goto L_0x01ab
            if (r17 != 0) goto L_0x01ab
            r0 = r11
            goto L_0x01ac
        L_0x01ab:
            r0 = r10
        L_0x01ac:
            r18 = r0
            com.android.server.wm.ActivityDisplay r0 = r31.getDisplay()
            boolean r0 = r0.pauseBackStacks(r4, r12, r10)
            com.android.server.wm.ActivityRecord r1 = r7.mResumedActivity
            if (r1 == 0) goto L_0x01c2
            boolean r1 = r7.startPausingLocked(r4, r10, r12, r10)
            r0 = r0 | r1
            r19 = r0
            goto L_0x01c4
        L_0x01c2:
            r19 = r0
        L_0x01c4:
            if (r19 == 0) goto L_0x01d9
            if (r18 != 0) goto L_0x01d9
            boolean r0 = r12.attachedToProcess()
            if (r0 == 0) goto L_0x01d3
            com.android.server.wm.WindowProcessController r0 = r12.app
            r0.updateProcessInfo(r10, r11, r10)
        L_0x01d3:
            if (r3 == 0) goto L_0x01d8
            r3.setWillCloseOrEnterPip(r11)
        L_0x01d8:
            return r11
        L_0x01d9:
            com.android.server.wm.ActivityRecord r0 = r7.mResumedActivity
            if (r0 != r12) goto L_0x01ef
            com.android.server.wm.ActivityStack$ActivityState r0 = com.android.server.wm.ActivityStack.ActivityState.RESUMED
            boolean r0 = r12.isState(r0)
            if (r0 == 0) goto L_0x01ef
            boolean r0 = r14.allResumedActivitiesComplete()
            if (r0 == 0) goto L_0x01ef
            r7.executeAppTransition(r9)
            return r11
        L_0x01ef:
            boolean r0 = r31.shouldSleepActivities()
            if (r0 == 0) goto L_0x021e
            com.android.server.wm.ActivityRecord r0 = r7.mLastNoHistoryActivity
            if (r0 == 0) goto L_0x021e
            boolean r0 = r0.finishing
            if (r0 != 0) goto L_0x021e
            com.android.server.wm.ActivityRecord r0 = r7.mLastNoHistoryActivity
            android.view.IApplicationToken$Stub r2 = r0.appToken
            r0 = 0
            r20 = 0
            r21 = 0
            java.lang.String r22 = "resume-no-history"
            r1 = r31
            r23 = r3
            r3 = r0
            r24 = r4
            r4 = r20
            r11 = r5
            r5 = r22
            r25 = r6
            r6 = r21
            r1.requestFinishActivityLocked(r2, r3, r4, r5, r6)
            r7.mLastNoHistoryActivity = r15
            goto L_0x0225
        L_0x021e:
            r23 = r3
            r24 = r4
            r11 = r5
            r25 = r6
        L_0x0225:
            if (r8 == 0) goto L_0x0238
            if (r8 == r12) goto L_0x0238
            boolean r0 = r12.nowVisible
            if (r0 == 0) goto L_0x0238
            boolean r0 = r8.finishing
            if (r0 == 0) goto L_0x0238
            com.android.server.wm.WindowProcessController r0 = r8.app
            if (r0 == 0) goto L_0x0238
            r8.setVisibility(r10)
        L_0x0238:
            android.content.pm.IPackageManager r0 = android.app.AppGlobals.getPackageManager()     // Catch:{ RemoteException -> 0x0268, IllegalArgumentException -> 0x0246 }
            java.lang.String r1 = r12.packageName     // Catch:{ RemoteException -> 0x0268, IllegalArgumentException -> 0x0246 }
            int r2 = r12.mUserId     // Catch:{ RemoteException -> 0x0268, IllegalArgumentException -> 0x0246 }
            r0.setPackageStoppedState(r1, r10, r2)     // Catch:{ RemoteException -> 0x0268, IllegalArgumentException -> 0x0246 }
            r2 = r25
            goto L_0x026c
        L_0x0246:
            r0 = move-exception
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Failed trying to unstop package "
            r1.append(r2)
            java.lang.String r2 = r12.packageName
            r1.append(r2)
            java.lang.String r2 = ": "
            r1.append(r2)
            r1.append(r0)
            java.lang.String r1 = r1.toString()
            r2 = r25
            android.util.Slog.w(r2, r1)
            goto L_0x026c
        L_0x0268:
            r0 = move-exception
            r2 = r25
        L_0x026c:
            r0 = 1
            com.android.server.wm.ActivityDisplay r1 = r31.getDisplay()
            com.android.server.wm.DisplayContent r6 = r1.mDisplayContent
            android.util.BoostFramework r1 = r7.mPerf
            if (r1 != 0) goto L_0x027e
            android.util.BoostFramework r1 = new android.util.BoostFramework
            r1.<init>()
            r7.mPerf = r1
        L_0x027e:
            r1 = 6
            if (r8 == 0) goto L_0x033d
            boolean r3 = r8.finishing
            r4 = 4227(0x1083, float:5.923E-42)
            if (r3 == 0) goto L_0x02e0
            com.android.server.wm.WindowProcessController r3 = r8.app
            if (r3 == 0) goto L_0x02e0
            com.android.server.wm.ActivityStackSupervisor r1 = r7.mStackSupervisor
            java.util.ArrayList<com.android.server.wm.ActivityRecord> r1 = r1.mNoAnimActivities
            boolean r1 = r1.contains(r8)
            if (r1 == 0) goto L_0x029a
            r0 = 0
            r6.prepareAppTransition(r10, r10)
            goto L_0x02da
        L_0x029a:
            com.android.server.wm.WindowManagerService r1 = r7.mWindowManager
            com.android.server.wm.TaskRecord r3 = r32.getTaskRecord()
            com.android.server.wm.TaskRecord r5 = r12.getTaskRecord()
            r21 = 7
            r22 = 9
            if (r3 != r5) goto L_0x02ad
            r3 = r21
            goto L_0x02af
        L_0x02ad:
            r3 = r22
        L_0x02af:
            r1.prepareAppTransition(r3, r10)
            com.android.server.wm.TaskRecord r1 = r32.getTaskRecord()
            com.android.server.wm.TaskRecord r3 = r12.getTaskRecord()
            if (r1 == r3) goto L_0x02c6
            android.util.BoostFramework r1 = r7.mPerf
            if (r1 == 0) goto L_0x02c6
            java.lang.String r3 = r12.packageName
            r1.perfHint(r4, r3)
        L_0x02c6:
            com.android.server.wm.TaskRecord r1 = r32.getTaskRecord()
            com.android.server.wm.TaskRecord r3 = r12.getTaskRecord()
            if (r1 != r3) goto L_0x02d4
            r1 = r21
            goto L_0x02d6
        L_0x02d4:
            r1 = r22
        L_0x02d6:
            r6.prepareAppTransition(r1, r10)
        L_0x02da:
            r8.setVisibility(r10)
            r15 = r0
            goto L_0x0351
        L_0x02e0:
            com.android.server.wm.ActivityStackSupervisor r3 = r7.mStackSupervisor
            java.util.ArrayList<com.android.server.wm.ActivityRecord> r3 = r3.mNoAnimActivities
            boolean r3 = r3.contains(r12)
            if (r3 == 0) goto L_0x02f1
            r0 = 0
            r6.prepareAppTransition(r10, r10)
            r15 = r0
            goto L_0x0351
        L_0x02f1:
            com.android.server.wm.WindowManagerService r3 = r7.mWindowManager
            com.android.server.wm.TaskRecord r5 = r32.getTaskRecord()
            com.android.server.wm.TaskRecord r15 = r12.getTaskRecord()
            r22 = 16
            r25 = 8
            if (r5 != r15) goto L_0x0303
            r5 = r1
            goto L_0x030c
        L_0x0303:
            boolean r5 = r12.mLaunchTaskBehind
            if (r5 == 0) goto L_0x030a
            r5 = r22
            goto L_0x030c
        L_0x030a:
            r5 = r25
        L_0x030c:
            r3.prepareAppTransition(r5, r10)
            com.android.server.wm.TaskRecord r3 = r32.getTaskRecord()
            com.android.server.wm.TaskRecord r5 = r12.getTaskRecord()
            if (r3 == r5) goto L_0x0323
            android.util.BoostFramework r3 = r7.mPerf
            if (r3 == 0) goto L_0x0323
            java.lang.String r5 = r12.packageName
            r3.perfHint(r4, r5)
        L_0x0323:
            com.android.server.wm.TaskRecord r3 = r32.getTaskRecord()
            com.android.server.wm.TaskRecord r4 = r12.getTaskRecord()
            if (r3 != r4) goto L_0x032f
            goto L_0x0338
        L_0x032f:
            boolean r1 = r12.mLaunchTaskBehind
            if (r1 == 0) goto L_0x0336
            r1 = r22
            goto L_0x0338
        L_0x0336:
            r1 = r25
        L_0x0338:
            r6.prepareAppTransition(r1, r10)
            goto L_0x0350
        L_0x033d:
            com.android.server.wm.ActivityStackSupervisor r3 = r7.mStackSupervisor
            java.util.ArrayList<com.android.server.wm.ActivityRecord> r3 = r3.mNoAnimActivities
            boolean r3 = r3.contains(r12)
            if (r3 == 0) goto L_0x034d
            r0 = 0
            r6.prepareAppTransition(r10, r10)
            r15 = r0
            goto L_0x0351
        L_0x034d:
            r6.prepareAppTransition(r1, r10)
        L_0x0350:
            r15 = r0
        L_0x0351:
            if (r15 == 0) goto L_0x0357
            r12.applyOptionsLocked()
            goto L_0x035a
        L_0x0357:
            r12.clearOptionsLocked()
        L_0x035a:
            com.android.server.wm.ActivityStackSupervisor r0 = r7.mStackSupervisor
            java.util.ArrayList<com.android.server.wm.ActivityRecord> r0 = r0.mNoAnimActivities
            r0.clear()
            boolean r0 = r12.attachedToProcess()
            if (r0 == 0) goto L_0x054b
            if (r11 == 0) goto L_0x0379
            boolean r0 = r11.inMultiWindowMode()
            if (r0 != 0) goto L_0x0377
            com.android.server.wm.ActivityRecord r0 = r11.mLastPausedActivity
            if (r0 == 0) goto L_0x0379
            boolean r0 = r0.fullscreen
            if (r0 != 0) goto L_0x0379
        L_0x0377:
            r0 = 1
            goto L_0x037a
        L_0x0379:
            r0 = r10
        L_0x037a:
            r22 = r0
            boolean r0 = r12.visible
            if (r0 == 0) goto L_0x0386
            boolean r0 = r12.stopped
            if (r0 != 0) goto L_0x0386
            if (r22 == 0) goto L_0x038a
        L_0x0386:
            r1 = 1
            r12.setVisibility(r1)
        L_0x038a:
            r12.startLaunchTickingLocked()
            if (r11 != 0) goto L_0x0391
            r0 = 0
            goto L_0x0393
        L_0x0391:
            com.android.server.wm.ActivityRecord r0 = r11.mResumedActivity
        L_0x0393:
            r5 = r0
            com.android.server.wm.ActivityStack$ActivityState r4 = r12.getState()
            com.android.server.wm.ActivityTaskManagerService r0 = r7.mService
            r0.updateCpuStats()
            com.android.server.wm.ActivityStack$ActivityState r0 = com.android.server.wm.ActivityStack.ActivityState.RESUMED
            java.lang.String r1 = "resumeTopActivityInnerLocked"
            r12.setState(r0, r1)
            com.android.server.wm.WindowProcessController r0 = r12.app
            r3 = 1
            r0.updateProcessInfo(r10, r3, r3)
            r7.updateLRUListLocked(r12)
            r0 = 1
            boolean r20 = r7.shouldBeVisible(r12)
            if (r20 == 0) goto L_0x03c4
            r25 = r0
            com.android.server.wm.RootActivityContainer r0 = r7.mRootActivityContainer
            r26 = r4
            int r4 = r7.mDisplayId
            boolean r0 = r0.ensureVisibilityAndConfig(r12, r4, r3, r10)
            r0 = r0 ^ r3
            r25 = r0
            goto L_0x03c8
        L_0x03c4:
            r25 = r0
            r26 = r4
        L_0x03c8:
            if (r25 == 0) goto L_0x03ee
            boolean r0 = r31.isFocusedStackOnDisplay()
            if (r0 == 0) goto L_0x03ee
            com.android.server.wm.ActivityRecord r0 = r31.topRunningActivityLocked()
            if (r0 == r12) goto L_0x03db
            com.android.server.wm.ActivityStackSupervisor r1 = r7.mStackSupervisor
            r1.scheduleResumeTopActivities()
        L_0x03db:
            boolean r1 = r12.visible
            if (r1 == 0) goto L_0x03e6
            boolean r1 = r12.stopped
            if (r1 == 0) goto L_0x03e4
            goto L_0x03e6
        L_0x03e4:
            r1 = 1
            goto L_0x03ea
        L_0x03e6:
            r1 = 1
            r12.setVisibility(r1)
        L_0x03ea:
            r12.completeResumeLocked()
            return r1
        L_0x03ee:
            com.android.server.wm.WindowProcessController r0 = r12.app     // Catch:{ Exception -> 0x04f9 }
            android.app.IApplicationThread r0 = r0.getThread()     // Catch:{ Exception -> 0x04f9 }
            android.view.IApplicationToken$Stub r3 = r12.appToken     // Catch:{ Exception -> 0x04f9 }
            android.app.servertransaction.ClientTransaction r0 = android.app.servertransaction.ClientTransaction.obtain(r0, r3)     // Catch:{ Exception -> 0x04f9 }
            java.util.ArrayList<android.app.ResultInfo> r3 = r12.results     // Catch:{ Exception -> 0x04f9 }
            if (r3 == 0) goto L_0x0419
            int r4 = r3.size()     // Catch:{ Exception -> 0x0410 }
            boolean r10 = r12.finishing     // Catch:{ Exception -> 0x0410 }
            if (r10 != 0) goto L_0x0419
            if (r4 <= 0) goto L_0x0419
            android.app.servertransaction.ActivityResultItem r10 = android.app.servertransaction.ActivityResultItem.obtain(r3)     // Catch:{ Exception -> 0x0410 }
            r0.addCallback(r10)     // Catch:{ Exception -> 0x0410 }
            goto L_0x0419
        L_0x0410:
            r0 = move-exception
            r30 = r5
            r29 = r26
            r26 = r6
            goto L_0x0500
        L_0x0419:
            java.util.ArrayList<com.android.internal.content.ReferrerIntent> r4 = r12.newIntents     // Catch:{ Exception -> 0x04f9 }
            if (r4 == 0) goto L_0x0427
            java.util.ArrayList<com.android.internal.content.ReferrerIntent> r4 = r12.newIntents     // Catch:{ Exception -> 0x0410 }
            r10 = 1
            android.app.servertransaction.NewIntentItem r4 = android.app.servertransaction.NewIntentItem.obtain(r4, r10)     // Catch:{ Exception -> 0x0410 }
            r0.addCallback(r4)     // Catch:{ Exception -> 0x0410 }
        L_0x0427:
            boolean r4 = r12.stopped     // Catch:{ Exception -> 0x04f9 }
            r12.notifyAppResumed(r4)     // Catch:{ Exception -> 0x04f9 }
            r10 = 5
            java.lang.Object[] r10 = new java.lang.Object[r10]     // Catch:{ Exception -> 0x04f9 }
            int r4 = r12.mUserId     // Catch:{ Exception -> 0x04f9 }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ Exception -> 0x04f9 }
            r27 = 0
            r10[r27] = r4     // Catch:{ Exception -> 0x04f9 }
            int r4 = java.lang.System.identityHashCode(r12)     // Catch:{ Exception -> 0x04f9 }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ Exception -> 0x04f9 }
            r20 = 1
            r10[r20] = r4     // Catch:{ Exception -> 0x04f9 }
            com.android.server.wm.TaskRecord r4 = r12.getTaskRecord()     // Catch:{ Exception -> 0x04f9 }
            int r4 = r4.taskId     // Catch:{ Exception -> 0x04f9 }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ Exception -> 0x04f9 }
            r28 = 2
            r10[r28] = r4     // Catch:{ Exception -> 0x04f9 }
            java.lang.String r4 = r12.shortComponentName     // Catch:{ Exception -> 0x04f9 }
            r28 = 3
            r10[r28] = r4     // Catch:{ Exception -> 0x04f9 }
            com.android.server.wm.WindowProcessController r4 = r12.app     // Catch:{ Exception -> 0x04f9 }
            if (r4 == 0) goto L_0x0464
            com.android.server.wm.WindowProcessController r4 = r12.app     // Catch:{ Exception -> 0x0410 }
            int r4 = r4.getPid()     // Catch:{ Exception -> 0x0410 }
            goto L_0x0465
        L_0x0464:
            r4 = 0
        L_0x0465:
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ Exception -> 0x04f9 }
            r28 = 4
            r10[r28] = r4     // Catch:{ Exception -> 0x04f9 }
            r4 = 30007(0x7537, float:4.2049E-41)
            android.util.EventLog.writeEvent(r4, r10)     // Catch:{ Exception -> 0x04f9 }
            com.android.server.wm.ActivityStack r4 = r12.getActivityStack()     // Catch:{ Exception -> 0x04f9 }
            if (r4 == 0) goto L_0x0486
            com.android.server.wm.ActivityTaskManagerService r4 = r7.mService     // Catch:{ Exception -> 0x0410 }
            com.android.server.wm.MiuiGestureController r4 = r4.mGestureController     // Catch:{ Exception -> 0x0410 }
            com.android.server.wm.ActivityStack r10 = r12.getActivityStack()     // Catch:{ Exception -> 0x0410 }
            boolean r4 = r4.isRecentsStackLaunchBehind(r10)     // Catch:{ Exception -> 0x0410 }
            if (r4 != 0) goto L_0x0489
        L_0x0486:
            com.android.server.wm.ActivityTaskManagerServiceInjector.onForegroundActivityChangedLocked(r12)     // Catch:{ Exception -> 0x04f9 }
        L_0x0489:
            r4 = 0
            r12.sleeping = r4     // Catch:{ Exception -> 0x04f9 }
            com.android.server.wm.ActivityTaskManagerService r4 = r7.mService     // Catch:{ Exception -> 0x04f9 }
            com.android.server.wm.AppWarnings r4 = r4.getAppWarningsLocked()     // Catch:{ Exception -> 0x04f9 }
            r4.onResumeActivity(r12)     // Catch:{ Exception -> 0x04f9 }
            com.android.server.wm.WindowProcessController r4 = r12.app     // Catch:{ Exception -> 0x04f9 }
            com.android.server.wm.ActivityTaskManagerService r10 = r7.mService     // Catch:{ Exception -> 0x04f9 }
            int r10 = r10.mTopProcessState     // Catch:{ Exception -> 0x04f9 }
            r4.setPendingUiCleanAndForceProcessStateUpTo(r10)     // Catch:{ Exception -> 0x04f9 }
            r12.clearOptionsLocked()     // Catch:{ Exception -> 0x04f9 }
            com.android.server.wm.WindowProcessController r4 = r12.app     // Catch:{ Exception -> 0x04f9 }
            int r4 = r4.getReportedProcState()     // Catch:{ Exception -> 0x04f9 }
            com.android.server.wm.ActivityDisplay r10 = r31.getDisplay()     // Catch:{ Exception -> 0x04f9 }
            com.android.server.wm.DisplayContent r10 = r10.mDisplayContent     // Catch:{ Exception -> 0x04f9 }
            boolean r10 = r10.isNextTransitionForward()     // Catch:{ Exception -> 0x04f9 }
            android.app.servertransaction.ResumeActivityItem r4 = android.app.servertransaction.ResumeActivityItem.obtain(r4, r10)     // Catch:{ Exception -> 0x04f9 }
            r0.setLifecycleStateRequest(r4)     // Catch:{ Exception -> 0x04f9 }
            com.android.server.wm.ActivityTaskManagerService r4 = r7.mService     // Catch:{ Exception -> 0x04f9 }
            com.android.server.wm.ClientLifecycleManager r4 = r4.getLifecycleManager()     // Catch:{ Exception -> 0x04f9 }
            r4.scheduleTransaction(r0)     // Catch:{ Exception -> 0x04f9 }
            r12.completeResumeLocked()     // Catch:{ Exception -> 0x04cb }
            r26 = r6
            r5 = 1
            goto L_0x055f
        L_0x04cb:
            r0 = move-exception
            r1 = r0
            r0 = r1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = "Exception thrown during resume of "
            r1.append(r3)
            r1.append(r12)
            java.lang.String r1 = r1.toString()
            android.util.Slog.w(r2, r1, r0)
            android.view.IApplicationToken$Stub r2 = r12.appToken
            r3 = 0
            r4 = 0
            r10 = 1
            java.lang.String r21 = "resume-exception"
            r1 = r31
            r29 = r26
            r30 = r5
            r5 = r21
            r26 = r6
            r6 = r10
            r1.requestFinishActivityLocked(r2, r3, r4, r5, r6)
            r1 = 1
            return r1
        L_0x04f9:
            r0 = move-exception
            r30 = r5
            r29 = r26
            r26 = r6
        L_0x0500:
            com.android.server.wm.ActivityStack r3 = r12.getActivityStack()
            if (r3 != 0) goto L_0x0508
            r1 = 1
            return r1
        L_0x0508:
            r3 = r29
            r12.setState(r3, r1)
            r4 = r30
            if (r4 == 0) goto L_0x0516
            com.android.server.wm.ActivityStack$ActivityState r5 = com.android.server.wm.ActivityStack.ActivityState.RESUMED
            r4.setState(r5, r1)
        L_0x0516:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r5 = "Restarting because process died: "
            r1.append(r5)
            r1.append(r12)
            java.lang.String r1 = r1.toString()
            android.util.Slog.i(r2, r1)
            boolean r1 = r12.hasBeenLaunched
            if (r1 != 0) goto L_0x0533
            r1 = 1
            r12.hasBeenLaunched = r1
            r2 = 0
            goto L_0x0544
        L_0x0533:
            if (r11 == 0) goto L_0x0543
            boolean r1 = r11.isTopStackOnDisplay()
            if (r1 == 0) goto L_0x0541
            r1 = 0
            r2 = 0
            r12.showStartingWindow(r1, r2, r2)
            goto L_0x0544
        L_0x0541:
            r2 = 0
            goto L_0x0544
        L_0x0543:
            r2 = 0
        L_0x0544:
            com.android.server.wm.ActivityStackSupervisor r1 = r7.mStackSupervisor
            r5 = 1
            r1.startSpecificActivityLocked(r12, r5, r2)
            return r5
        L_0x054b:
            r26 = r6
            r2 = r10
            r5 = 1
            boolean r0 = r12.hasBeenLaunched
            if (r0 != 0) goto L_0x0556
            r12.hasBeenLaunched = r5
            goto L_0x055a
        L_0x0556:
            r1 = 0
            r12.showStartingWindow(r1, r2, r2)
        L_0x055a:
            com.android.server.wm.ActivityStackSupervisor r0 = r7.mStackSupervisor
            r0.startSpecificActivityLocked(r12, r5, r5)
        L_0x055f:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityStack.resumeTopActivityInnerLocked(com.android.server.wm.ActivityRecord, android.app.ActivityOptions):boolean");
    }

    private boolean resumeNextFocusableActivityWhenStackIsEmpty(ActivityRecord prev, ActivityOptions options) {
        ActivityStack nextFocusedStack;
        if (!isActivityTypeHome() && (nextFocusedStack = adjustFocusToNextFocusableStack("noMoreActivities")) != null) {
            return this.mRootActivityContainer.resumeFocusedStacksTopActivities(nextFocusedStack, prev, (ActivityOptions) null);
        }
        ActivityOptions.abort(options);
        return this.mRootActivityContainer.resumeHomeActivity(prev, "noMoreActivities", this.mDisplayId);
    }

    /* access modifiers changed from: package-private */
    public int getAdjustedPositionForTask(TaskRecord task, int suggestedPosition, ActivityRecord starting) {
        int maxPosition = this.mTaskHistory.size();
        if ((starting != null && starting.okToShowLocked()) || (starting == null && task.okToShowLocked())) {
            return Math.min(suggestedPosition, maxPosition);
        }
        while (maxPosition > 0) {
            TaskRecord tmpTask = this.mTaskHistory.get(maxPosition - 1);
            if (!this.mStackSupervisor.isCurrentProfileLocked(tmpTask.userId) || tmpTask.topRunningActivityLocked() == null) {
                break;
            }
            maxPosition--;
        }
        return Math.min(suggestedPosition, maxPosition);
    }

    private void insertTaskAtPosition(TaskRecord task, int position) {
        if (position >= this.mTaskHistory.size()) {
            insertTaskAtTop(task, (ActivityRecord) null);
        } else if (position <= 0) {
            insertTaskAtBottom(task);
        } else {
            int position2 = getAdjustedPositionForTask(task, position, (ActivityRecord) null);
            this.mTaskHistory.remove(task);
            this.mTaskHistory.add(position2, task);
            TaskStack taskStack = this.mTaskStack;
            if (taskStack != null) {
                taskStack.positionChildAt(task.getTask(), position2);
            }
            updateTaskMovement(task, true);
        }
    }

    private void insertTaskAtTop(TaskRecord task, ActivityRecord starting) {
        this.mTaskHistory.remove(task);
        this.mTaskHistory.add(getAdjustedPositionForTask(task, this.mTaskHistory.size(), starting), task);
        updateTaskMovement(task, true);
        ActivityStackInjector.moveTaskIfNeed(task, this.mTaskHistory);
        positionChildWindowContainerAtTop(task);
    }

    private void insertTaskAtBottom(TaskRecord task) {
        this.mTaskHistory.remove(task);
        this.mTaskHistory.add(getAdjustedPositionForTask(task, 0, (ActivityRecord) null), task);
        updateTaskMovement(task, true);
        positionChildWindowContainerAtBottom(task);
    }

    /* access modifiers changed from: package-private */
    public void startActivityLocked(ActivityRecord r, ActivityRecord focusedTopActivity, boolean newTask, boolean keepCurTransition, ActivityOptions options) {
        ActivityRecord activityRecord = r;
        ActivityRecord activityRecord2 = focusedTopActivity;
        boolean z = keepCurTransition;
        ActivityOptions activityOptions = options;
        TaskRecord rTask = r.getTaskRecord();
        int taskId = rTask.taskId;
        if (!activityRecord.mLaunchTaskBehind && (taskForIdLocked(taskId) == null || newTask)) {
            insertTaskAtTop(rTask, activityRecord);
        }
        TaskRecord task = null;
        if (!newTask) {
            boolean startIt = true;
            int taskNdx = this.mTaskHistory.size() - 1;
            while (true) {
                if (taskNdx < 0) {
                    break;
                }
                task = this.mTaskHistory.get(taskNdx);
                if (task.getTopActivity() != null) {
                    if (task == rTask) {
                        if (!startIt) {
                            r.createAppWindowToken();
                            ActivityOptions.abort(options);
                            return;
                        }
                    } else if (task.numFullscreen > 0) {
                        startIt = false;
                    }
                }
                taskNdx--;
            }
        }
        TaskRecord activityTask = r.getTaskRecord();
        if (task == activityTask && this.mTaskHistory.indexOf(task) != this.mTaskHistory.size() - 1) {
            this.mStackSupervisor.mUserLeaving = false;
        }
        TaskRecord task2 = activityTask;
        if (activityRecord.mAppWindowToken == null) {
            r.createAppWindowToken();
        }
        task2.setFrontOfTask();
        if (mActivityPluginDelegate != null) {
            ActivityPluginDelegate.activityInvokeNotification(activityRecord.appInfo.packageName, activityRecord.fullscreen);
        }
        if (!isHomeOrRecentsStack() || numActivities() > 0) {
            DisplayContent dc = getDisplay().mDisplayContent;
            if ((activityRecord.intent.getFlags() & 65536) != 0) {
                dc.prepareAppTransition(0, z);
                this.mStackSupervisor.mNoAnimActivities.add(activityRecord);
            } else {
                int transit = 6;
                if (newTask) {
                    if (activityRecord.mLaunchTaskBehind) {
                        transit = 16;
                    } else {
                        if (canEnterPipOnTaskSwitch(activityRecord2, (TaskRecord) null, activityRecord, activityOptions)) {
                            activityRecord2.supportsEnterPipOnTaskSwitch = true;
                        }
                        transit = 8;
                    }
                }
                dc.prepareAppTransition(transit, z);
                this.mStackSupervisor.mNoAnimActivities.remove(activityRecord);
            }
            boolean doShow = true;
            if (newTask) {
                if ((activityRecord.intent.getFlags() & DumpState.DUMP_COMPILER_STATS) != 0) {
                    resetTaskIfNeededLocked(activityRecord, activityRecord);
                    doShow = topRunningNonDelayedActivityLocked((ActivityRecord) null) == activityRecord;
                }
            } else if (activityOptions != null && options.getAnimationType() == 5) {
                doShow = false;
            }
            boolean newTask2 = newTask | ActivityStackInjector.isStartingWindowSupported(activityRecord, this.mService.mContext);
            if (activityRecord.mLaunchTaskBehind) {
                activityRecord.setVisibility(true);
                ensureActivitiesVisibleLocked((ActivityRecord) null, 0, false);
            } else if (doShow) {
                TaskRecord prevTask = r.getTaskRecord();
                ActivityRecord prev = prevTask.topRunningActivityWithStartingWindowLocked();
                if (prev != null) {
                    if (prev.getTaskRecord() != prevTask) {
                        prev = null;
                    } else if (prev.nowVisible) {
                        prev = null;
                    }
                }
                activityRecord.showStartingWindow(prev, newTask2, isTaskSwitch(r, focusedTopActivity));
            }
        } else {
            ActivityOptions.abort(options);
            boolean z2 = newTask;
        }
    }

    private boolean canEnterPipOnTaskSwitch(ActivityRecord pipCandidate, TaskRecord toFrontTask, ActivityRecord toFrontActivity, ActivityOptions opts) {
        if ((opts != null && opts.disallowEnterPictureInPictureWhileLaunching()) || pipCandidate == null || pipCandidate.inPinnedWindowingMode()) {
            return false;
        }
        ActivityStack targetStack = toFrontTask != null ? toFrontTask.getStack() : toFrontActivity.getActivityStack();
        if (targetStack == null || !targetStack.isActivityTypeAssistant()) {
            return true;
        }
        return false;
    }

    private boolean isTaskSwitch(ActivityRecord r, ActivityRecord topFocusedActivity) {
        return (topFocusedActivity == null || r.getTaskRecord() == topFocusedActivity.getTaskRecord()) ? false : true;
    }

    private ActivityOptions resetTargetTaskIfNeededLocked(TaskRecord task, boolean forceReset) {
        boolean z;
        int end;
        boolean noOptions;
        ActivityOptions topOptions;
        TaskRecord targetTask;
        int i;
        TaskRecord taskRecord = task;
        ArrayList<ActivityRecord> activities = taskRecord.mActivities;
        int numActivities = activities.size();
        int rootActivityNdx = task.findEffectiveRootIndex();
        ActivityOptions topOptions2 = null;
        int replyChainEnd = -1;
        boolean canMoveOptions = true;
        int i2 = numActivities - 1;
        while (i2 > rootActivityNdx) {
            ActivityRecord target = activities.get(i2);
            if (target.frontOfTask) {
                break;
            }
            int flags = target.info.flags;
            boolean finishOnTaskLaunch = (flags & 2) != 0;
            boolean allowTaskReparenting = (flags & 64) != 0;
            boolean clearWhenTaskReset = (target.intent.getFlags() & DumpState.DUMP_FROZEN) != 0;
            if (finishOnTaskLaunch || clearWhenTaskReset || target.resultTo == null) {
                if (finishOnTaskLaunch || clearWhenTaskReset || !allowTaskReparenting || target.taskAffinity == null) {
                    ActivityRecord activityRecord = target;
                    z = false;
                } else if (!target.taskAffinity.equals(taskRecord.affinity)) {
                    ActivityRecord bottom = (this.mTaskHistory.isEmpty() || this.mTaskHistory.get(0).mActivities.isEmpty()) ? null : this.mTaskHistory.get(0).mActivities.get(0);
                    if (bottom == null || target.taskAffinity == null || !target.taskAffinity.equals(bottom.getTaskRecord().affinity)) {
                        int i3 = flags;
                        targetTask = createTaskRecord(this.mStackSupervisor.getNextTaskIdForUserLocked(target.mUserId), target.info, (Intent) null, (IVoiceInteractionSession) null, (IVoiceInteractor) null, false);
                        targetTask.affinityIntent = target.intent;
                    } else {
                        targetTask = bottom.getTaskRecord();
                        int i4 = flags;
                        ActivityRecord activityRecord2 = target;
                    }
                    boolean noOptions2 = canMoveOptions;
                    for (int srcPos = replyChainEnd < 0 ? i2 : replyChainEnd; srcPos >= i2; srcPos--) {
                        ActivityRecord p = activities.get(srcPos);
                        if (!p.finishing) {
                            if (!noOptions2 || topOptions2 != null) {
                                i = 0;
                            } else {
                                i = 0;
                                topOptions2 = p.takeOptionsLocked(false);
                                if (topOptions2 != null) {
                                    noOptions2 = false;
                                }
                            }
                            p.reparent(targetTask, i, "resetTargetTaskIfNeeded");
                            canMoveOptions = false;
                        }
                    }
                    positionChildWindowContainerAtBottom(targetTask);
                    replyChainEnd = -1;
                } else {
                    ActivityRecord activityRecord3 = target;
                    z = false;
                }
                if (forceReset || finishOnTaskLaunch || clearWhenTaskReset) {
                    if (clearWhenTaskReset) {
                        end = activities.size() - 1;
                    } else if (replyChainEnd < 0) {
                        end = i2;
                    } else {
                        end = replyChainEnd;
                    }
                    boolean noOptions3 = canMoveOptions;
                    boolean z2 = canMoveOptions;
                    int end2 = end;
                    boolean canMoveOptions2 = z2;
                    ActivityOptions activityOptions = topOptions2;
                    int srcPos2 = i2;
                    ActivityOptions topOptions3 = activityOptions;
                    while (srcPos2 <= end2) {
                        ActivityRecord p2 = activities.get(srcPos2);
                        if (!p2.finishing) {
                            if (!noOptions3 || topOptions3 != null) {
                                noOptions = noOptions3;
                                topOptions = topOptions3;
                            } else {
                                ActivityOptions topOptions4 = p2.takeOptionsLocked(z);
                                if (topOptions4 != null) {
                                    noOptions = false;
                                    topOptions = topOptions4;
                                } else {
                                    noOptions = noOptions3;
                                    topOptions = topOptions4;
                                }
                            }
                            ActivityRecord activityRecord4 = p2;
                            if (finishActivityLocked(p2, 0, (Intent) null, "reset-task", false)) {
                                end2--;
                                srcPos2--;
                                canMoveOptions2 = false;
                                topOptions3 = topOptions;
                                noOptions3 = noOptions;
                            } else {
                                canMoveOptions2 = false;
                                topOptions3 = topOptions;
                                noOptions3 = noOptions;
                            }
                        }
                        srcPos2++;
                    }
                    canMoveOptions = canMoveOptions2;
                    replyChainEnd = -1;
                    topOptions2 = topOptions3;
                } else {
                    replyChainEnd = -1;
                }
            } else if (replyChainEnd < 0) {
                replyChainEnd = i2;
            }
            i2--;
            taskRecord = task;
        }
        return topOptions2;
    }

    private int resetAffinityTaskIfNeededLocked(TaskRecord affinityTask, TaskRecord task, boolean topTaskIsHigher, boolean forceReset, int taskInsertionPoint) {
        int taskId;
        String taskAffinity;
        int taskInsertionPoint2;
        TaskRecord taskRecord = task;
        int taskId2 = taskRecord.taskId;
        String taskAffinity2 = taskRecord.affinity;
        ArrayList<ActivityRecord> activities = affinityTask.mActivities;
        int numActivities = activities.size();
        int rootActivityNdx = affinityTask.findEffectiveRootIndex();
        int i = numActivities - 1;
        int replyChainEnd = -1;
        int taskInsertionPoint3 = taskInsertionPoint;
        while (true) {
            if (i <= rootActivityNdx) {
                String str = taskAffinity2;
                break;
            }
            ActivityRecord target = activities.get(i);
            if (target.frontOfTask) {
                int i2 = taskId2;
                String str2 = taskAffinity2;
                break;
            }
            int flags = target.info.flags;
            boolean allowTaskReparenting = false;
            boolean finishOnTaskLaunch = (flags & 2) != 0;
            if ((flags & 64) != 0) {
                allowTaskReparenting = true;
            }
            if (target.resultTo != null) {
                if (replyChainEnd < 0) {
                    replyChainEnd = i;
                    taskId = taskId2;
                    taskAffinity = taskAffinity2;
                } else {
                    taskId = taskId2;
                    taskAffinity = taskAffinity2;
                }
            } else if (!topTaskIsHigher || !allowTaskReparenting || taskAffinity2 == null) {
                taskId = taskId2;
                taskAffinity = taskAffinity2;
            } else if (taskAffinity2.equals(target.taskAffinity)) {
                if (forceReset) {
                    taskId = taskId2;
                    taskAffinity = taskAffinity2;
                } else if (finishOnTaskLaunch) {
                    taskId = taskId2;
                    taskAffinity = taskAffinity2;
                } else {
                    if (taskInsertionPoint3 < 0) {
                        taskInsertionPoint3 = taskRecord.mActivities.size();
                    }
                    int srcPos = replyChainEnd >= 0 ? replyChainEnd : i;
                    while (srcPos >= i) {
                        activities.get(srcPos).reparent(taskRecord, taskInsertionPoint3, "resetAffinityTaskIfNeededLocked");
                        srcPos--;
                        taskAffinity2 = taskAffinity2;
                        taskId2 = taskId2;
                    }
                    taskId = taskId2;
                    taskAffinity = taskAffinity2;
                    positionChildWindowContainerAtTop(taskRecord);
                    if (target.info.launchMode == 1) {
                        ArrayList<ActivityRecord> taskActivities = taskRecord.mActivities;
                        int targetNdx = taskActivities.indexOf(target);
                        if (targetNdx > 0) {
                            ActivityRecord p = taskActivities.get(targetNdx - 1);
                            taskInsertionPoint2 = taskInsertionPoint3;
                            if (p.intent.getComponent().equals(target.intent.getComponent())) {
                                finishActivityLocked(p, 0, (Intent) null, "replace", false);
                            }
                        } else {
                            taskInsertionPoint2 = taskInsertionPoint3;
                        }
                    } else {
                        taskInsertionPoint2 = taskInsertionPoint3;
                    }
                    taskInsertionPoint3 = taskInsertionPoint2;
                    replyChainEnd = -1;
                }
                for (int srcPos2 = replyChainEnd >= 0 ? replyChainEnd : i; srcPos2 >= i; srcPos2--) {
                    ActivityRecord p2 = activities.get(srcPos2);
                    if (!p2.finishing) {
                        finishActivityLocked(p2, 0, (Intent) null, "move-affinity", false);
                    }
                }
                replyChainEnd = -1;
            } else {
                taskId = taskId2;
                taskAffinity = taskAffinity2;
            }
            i--;
            taskRecord = task;
            taskAffinity2 = taskAffinity;
            taskId2 = taskId;
        }
        return taskInsertionPoint3;
    }

    /* access modifiers changed from: package-private */
    public final ActivityRecord resetTaskIfNeededLocked(ActivityRecord taskTop, ActivityRecord newActivity) {
        int taskNdx;
        boolean forceReset = (newActivity.info.flags & 4) != 0;
        TaskRecord task = taskTop.getTaskRecord();
        boolean taskFound = false;
        ActivityOptions topOptions = null;
        int reparentInsertionPoint = -1;
        for (int i = this.mTaskHistory.size() - 1; i >= 0; i--) {
            TaskRecord targetTask = this.mTaskHistory.get(i);
            if (targetTask == task) {
                taskFound = true;
                topOptions = resetTargetTaskIfNeededLocked(task, forceReset);
            } else {
                reparentInsertionPoint = resetAffinityTaskIfNeededLocked(targetTask, task, taskFound, forceReset, reparentInsertionPoint);
            }
        }
        int taskNdx2 = this.mTaskHistory.indexOf(task);
        if (taskNdx2 >= 0) {
            while (true) {
                taskNdx = taskNdx2 - 1;
                taskTop = this.mTaskHistory.get(taskNdx2).getTopActivity();
                if (taskTop != null || taskNdx < 0) {
                    int i2 = taskNdx;
                } else {
                    taskNdx2 = taskNdx;
                }
            }
            int i22 = taskNdx;
        }
        if (topOptions != null) {
            if (taskTop != null) {
                taskTop.updateOptionsLocked(topOptions);
            } else {
                topOptions.abort();
            }
        }
        return taskTop;
    }

    /* access modifiers changed from: package-private */
    public void sendActivityResultLocked(int callingUid, ActivityRecord r, String resultWho, int requestCode, int resultCode, Intent data) {
        if (callingUid > 0) {
            this.mService.mUgmInternal.grantUriPermissionFromIntent(callingUid, r.packageName, data, r.getUriPermissionsLocked(), r.mUserId);
        }
        if (this.mResumedActivity == r && r.attachedToProcess()) {
            try {
                ArrayList<ResultInfo> list = new ArrayList<>();
                list.add(new ResultInfo(resultWho, requestCode, resultCode, data));
                this.mService.getLifecycleManager().scheduleTransaction(r.app.getThread(), (IBinder) r.appToken, (ClientTransactionItem) ActivityResultItem.obtain(list));
                return;
            } catch (Exception e) {
                Slog.w("ActivityTaskManager", "Exception thrown sending result to " + r, e);
            }
        }
        r.addResultLocked((ActivityRecord) null, resultWho, requestCode, resultCode, data);
    }

    private boolean isATopFinishingTask(TaskRecord task) {
        for (int i = this.mTaskHistory.size() - 1; i >= 0; i--) {
            TaskRecord current = this.mTaskHistory.get(i);
            if (current.topRunningActivityLocked() != null) {
                return false;
            }
            if (current == task) {
                return true;
            }
        }
        return false;
    }

    private void adjustFocusedActivityStack(ActivityRecord r, String reason) {
        if (this.mRootActivityContainer.isTopDisplayFocusedStack(this)) {
            ActivityRecord activityRecord = this.mResumedActivity;
            if (activityRecord == r || activityRecord == null) {
                ActivityRecord next = topRunningActivityLocked();
                String myReason = reason + " adjustFocus";
                if (next == r) {
                    ActivityRecord top = this.mRootActivityContainer.topRunningActivity();
                    if (top != null) {
                        top.moveFocusableActivityToTop(myReason);
                    }
                } else if (next == null || !isFocusable()) {
                    if (r.getTaskRecord() != null) {
                        ActivityStack stack = this.mRootActivityContainer.getNextFocusableStack(this, true);
                        if (stack != null && stack.isActivityTypeHome()) {
                            this.mService.updateMiuiAnimationInfo(r);
                            this.mService.setIsMultiWindowMode(r);
                        }
                        ActivityStack nextFocusableStack = adjustFocusToNextFocusableStack(myReason);
                        if (nextFocusableStack != null) {
                            ActivityRecord top2 = nextFocusableStack.topRunningActivityLocked();
                            if (top2 != null && top2 == this.mRootActivityContainer.getTopResumedActivity()) {
                                this.mService.setResumedActivityUncheckLocked(top2, reason);
                                return;
                            }
                            return;
                        }
                        getDisplay().moveHomeActivityToTop(myReason);
                        return;
                    }
                    throw new IllegalStateException("activity no longer associated with task:" + r);
                } else if (r.mLunchedFromRoundedView) {
                    this.mService.updateMiuiRoundedViewAnimationInfo(r);
                    this.mService.setIsMultiWindowMode(r);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public ActivityStack adjustFocusToNextFocusableStack(String reason) {
        return adjustFocusToNextFocusableStack(reason, false);
    }

    private ActivityStack adjustFocusToNextFocusableStack(String reason, boolean allowFocusSelf) {
        ActivityStack stack = this.mRootActivityContainer.getNextFocusableStack(this, !allowFocusSelf);
        String myReason = reason + " adjustFocusToNextFocusableStack";
        if (stack == null) {
            return null;
        }
        ActivityRecord top = stack.topRunningActivityLocked();
        if (!stack.isActivityTypeHome() || (top != null && top.visible)) {
            stack.moveToFront(myReason);
            return stack;
        }
        stack.getDisplay().moveHomeActivityToTop(reason);
        return stack;
    }

    /* access modifiers changed from: package-private */
    public final void stopActivityLocked(ActivityRecord r) {
        r.launching = false;
        if (!((r.intent.getFlags() & 1073741824) == 0 && (r.info.flags & 128) == 0) && !r.finishing && !shouldSleepActivities()) {
            if (requestFinishActivityLocked(r.appToken, 0, (Intent) null, "stop-no-history", false)) {
                r.resumeKeyDispatchingLocked();
                return;
            }
        }
        if (r.attachedToProcess()) {
            adjustFocusedActivityStack(r, "stopActivity");
            r.resumeKeyDispatchingLocked();
            try {
                r.stopped = false;
                if (r.mIsLastFrame) {
                    r.setLastFrame(false);
                }
                r.setState(ActivityState.STOPPING, "stopActivityLocked");
                if (!(mActivityPluginDelegate == null || getWindowingMode() == 0)) {
                    ActivityPluginDelegate activityPluginDelegate = mActivityPluginDelegate;
                    ActivityPluginDelegate.activitySuspendNotification(r.appInfo.packageName, getWindowingMode() == 1, false);
                }
                if (mActivityTrigger != null) {
                    mActivityTrigger.activityStopTrigger(r.intent, r.info, r.appInfo);
                }
                if (!r.visible) {
                    r.setVisible(false);
                }
                EventLogTags.writeAmStopActivity(r.mUserId, System.identityHashCode(r), r.shortComponentName);
                this.mService.getLifecycleManager().scheduleTransaction(r.app.getThread(), (IBinder) r.appToken, (ActivityLifecycleItem) StopActivityItem.obtain(r.visible, r.configChangeFlags));
                if (shouldSleepOrShutDownActivities()) {
                    r.setSleeping(true);
                }
                this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(104, r), 11000);
            } catch (Exception e) {
                Slog.w("ActivityTaskManager", "Exception thrown during pause", e);
                r.stopped = true;
                r.setState(ActivityState.STOPPED, "stopActivityLocked");
                if (r.deferRelaunchUntilPaused) {
                    destroyActivityLocked(r, true, "stop-except");
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public final boolean requestFinishActivityLocked(IBinder token, int resultCode, Intent resultData, String reason, boolean oomAdj) {
        ActivityRecord r = isInStackLocked(token);
        if (r == null) {
            return false;
        }
        finishActivityLocked(r, resultCode, resultData, reason, oomAdj);
        return true;
    }

    /* access modifiers changed from: package-private */
    public final void finishSubActivityLocked(ActivityRecord self, String resultWho, int requestCode) {
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            ArrayList<ActivityRecord> activities = this.mTaskHistory.get(taskNdx).mActivities;
            for (int activityNdx = activities.size() - 1; activityNdx >= 0; activityNdx--) {
                ActivityRecord r = activities.get(activityNdx);
                if (r.resultTo == self && r.requestCode == requestCode && ((r.resultWho == null && resultWho == null) || (r.resultWho != null && r.resultWho.equals(resultWho)))) {
                    finishActivityLocked(r, 0, (Intent) null, "request-sub", false);
                }
            }
        }
        this.mService.updateOomAdj();
    }

    /* access modifiers changed from: package-private */
    public final TaskRecord finishTopCrashedActivityLocked(WindowProcessController app, String reason) {
        ActivityRecord r = topRunningActivityLocked();
        if (r == null) {
            WindowProcessController windowProcessController = app;
            return null;
        } else if (r.app != app) {
            return null;
        } else {
            Slog.w("ActivityTaskManager", "  Force finishing activity " + r.intent.getComponent().flattenToShortString());
            TaskRecord finishedTask = r.getTaskRecord();
            int taskNdx = this.mTaskHistory.indexOf(finishedTask);
            int activityNdx = finishedTask.mActivities.indexOf(r);
            boolean needFinishTwice = false;
            getDisplay().mDisplayContent.prepareAppTransition(26, false);
            finishActivityLocked(r, 0, (Intent) null, reason, false);
            int activityNdx2 = activityNdx - 1;
            int newTaskNdx = this.mTaskHistory.indexOf(r.getTaskRecord());
            if (newTaskNdx == taskNdx || newTaskNdx < 0) {
                needFinishTwice = true;
            }
            if ((newTaskNdx == taskNdx && activityNdx2 < 0) || newTaskNdx < 0) {
                int taskNdx2 = newTaskNdx < 0 ? this.mTaskHistory.size() : taskNdx;
                while (true) {
                    taskNdx2--;
                    if (taskNdx2 >= 0) {
                        activityNdx2 = this.mTaskHistory.get(taskNdx2).mActivities.size() - 1;
                        if (activityNdx2 >= 0) {
                            taskNdx = taskNdx2;
                            break;
                        }
                    } else {
                        taskNdx = taskNdx2;
                        break;
                    }
                }
            }
            if (activityNdx2 < 0 || taskNdx < 0 || !needFinishTwice) {
            } else {
                ActivityRecord r2 = this.mTaskHistory.get(taskNdx).mActivities.get(activityNdx2);
                if (!r2.isState(ActivityState.RESUMED, ActivityState.PAUSING, ActivityState.PAUSED)) {
                } else if (!r2.isActivityTypeHome() || this.mService.mHomeProcess != r2.app) {
                    Slog.w("ActivityTaskManager", "  Force finishing activity " + r2.intent.getComponent().flattenToShortString());
                    int i = newTaskNdx;
                    finishActivityLocked(r2, 0, (Intent) null, reason, false);
                } else {
                    int i2 = newTaskNdx;
                }
            }
            return finishedTask;
        }
    }

    /* access modifiers changed from: package-private */
    public final void finishVoiceTask(IVoiceInteractionSession session) {
        IBinder sessionBinder = session.asBinder();
        boolean didOne = false;
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            TaskRecord tr = this.mTaskHistory.get(taskNdx);
            if (tr.voiceSession == null || tr.voiceSession.asBinder() != sessionBinder) {
                int activityNdx = tr.mActivities.size() - 1;
                while (true) {
                    if (activityNdx < 0) {
                        break;
                    }
                    ActivityRecord r = tr.mActivities.get(activityNdx);
                    if (r.voiceSession != null && r.voiceSession.asBinder() == sessionBinder) {
                        r.clearVoiceSessionLocked();
                        try {
                            r.app.getThread().scheduleLocalVoiceInteractionStarted(r.appToken, (IVoiceInteractor) null);
                        } catch (RemoteException e) {
                        }
                        this.mService.finishRunningVoiceLocked();
                        break;
                    }
                    activityNdx--;
                }
            } else {
                for (int activityNdx2 = tr.mActivities.size() - 1; activityNdx2 >= 0; activityNdx2--) {
                    ActivityRecord r2 = tr.mActivities.get(activityNdx2);
                    if (!r2.finishing) {
                        finishActivityLocked(r2, 0, (Intent) null, "finish-voice", false);
                        didOne = true;
                    }
                }
            }
        }
        if (didOne) {
            this.mService.updateOomAdj();
        }
    }

    /* access modifiers changed from: package-private */
    public final boolean finishActivityAffinityLocked(ActivityRecord r) {
        ArrayList<ActivityRecord> activities = r.getTaskRecord().mActivities;
        for (int index = activities.indexOf(r); index >= 0; index--) {
            ActivityRecord cur = activities.get(index);
            if (!Objects.equals(cur.taskAffinity, r.taskAffinity)) {
                return true;
            }
            finishActivityLocked(cur, 0, (Intent) null, "request-affinity", true);
        }
        return true;
    }

    private void finishActivityResultsLocked(ActivityRecord r, int resultCode, Intent resultData) {
        ActivityRecord resultTo = r.resultTo;
        if (resultTo != null) {
            if (!(resultTo.mUserId == r.mUserId || resultData == null)) {
                resultData.prepareToLeaveUser(r.mUserId);
            }
            if (r.info.applicationInfo.uid > 0) {
                this.mService.mUgmInternal.grantUriPermissionFromIntent(r.info.applicationInfo.uid, resultTo.packageName, resultData, resultTo.getUriPermissionsLocked(), resultTo.mUserId);
            }
            resultTo.addResultLocked(r, r.resultWho, r.requestCode, resultCode, resultData);
            r.resultTo = null;
        }
        r.results = null;
        r.pendingResults = null;
        r.newIntents = null;
        r.icicle = null;
    }

    /* access modifiers changed from: package-private */
    public final boolean finishActivityLocked(ActivityRecord r, int resultCode, Intent resultData, String reason, boolean oomAdj) {
        return finishActivityLocked(r, resultCode, resultData, reason, oomAdj, false);
    }

    /* access modifiers changed from: package-private */
    public final boolean finishActivityLocked(ActivityRecord r, int resultCode, Intent resultData, String reason, boolean oomAdj, boolean pauseImmediately) {
        ActivityRecord activityRecord = r;
        boolean removedActivity = false;
        if (activityRecord.finishing) {
            Slog.w("ActivityTaskManager", "Duplicate finish request for " + r);
            return false;
        }
        if (activityRecord.mIsCastMode) {
            r.setCastMode(false);
            this.mService.setCurrentCastModeState(activityRecord.packageName, 0);
            this.mService.mCastActivity = null;
        }
        if (activityRecord.mIsLastFrame) {
            r.setLastFrame(false);
        }
        this.mWindowManager.deferSurfaceLayout();
        try {
            r.makeFinishingLocked();
            TaskRecord task = r.getTaskRecord();
            Object[] objArr = new Object[5];
            objArr[0] = Integer.valueOf(activityRecord.app != null ? activityRecord.app.getPid() : 0);
            objArr[1] = Integer.valueOf(System.identityHashCode(r));
            int finishMode = 2;
            objArr[2] = Integer.valueOf(task.taskId);
            objArr[3] = activityRecord.shortComponentName;
            objArr[4] = reason;
            EventLog.writeEvent(EventLogTags.AM_FINISH_ACTIVITY, objArr);
            ArrayList<ActivityRecord> activities = task.mActivities;
            int index = activities.indexOf(r);
            if (index < activities.size() - 1) {
                task.setFrontOfTask();
                if ((activityRecord.intent.getFlags() & DumpState.DUMP_FROZEN) != 0) {
                    activities.get(index + 1).intent.addFlags(DumpState.DUMP_FROZEN);
                }
            }
            r.pauseKeyDispatchingLocked();
            adjustFocusedActivityStack(r, "finishActivity");
            finishActivityResultsLocked(r, resultCode, resultData);
            boolean endTask = index <= 0 && !task.isClearingToReuseTask();
            int transit = endTask ? 9 : 7;
            if (this.mResumedActivity == activityRecord) {
                if (endTask) {
                    this.mService.getTaskChangeNotificationController().notifyTaskRemovalStarted(task.getTaskInfo());
                }
                try {
                    getDisplay().mDisplayContent.prepareAppTransition(transit, false);
                    r.setVisibility(false);
                    if (this.mPausingActivity == null) {
                        startPausingLocked(false, false, (ActivityRecord) null, pauseImmediately);
                    } else {
                        boolean z = pauseImmediately;
                    }
                    if (endTask) {
                        this.mService.getLockTaskController().clearLockedTask(task);
                        boolean z2 = oomAdj;
                    } else {
                        boolean z3 = oomAdj;
                    }
                } catch (Throwable th) {
                    th = th;
                    boolean z4 = oomAdj;
                    this.mWindowManager.continueSurfaceLayout();
                    throw th;
                }
            } else {
                boolean z5 = pauseImmediately;
                if (!r.isState(ActivityState.PAUSING)) {
                    if (activityRecord.visible) {
                        prepareActivityHideTransitionAnimation(r, transit);
                    }
                    if (!activityRecord.visible) {
                        if (!activityRecord.nowVisible) {
                            finishMode = 1;
                        }
                    }
                    try {
                        if (finishCurrentActivityLocked(r, finishMode, oomAdj, "finishActivityLocked") == null) {
                            removedActivity = true;
                        }
                        if (task.onlyHasTaskOverlayActivities(true)) {
                            Iterator<ActivityRecord> it = task.mActivities.iterator();
                            while (it.hasNext()) {
                                ActivityRecord taskOverlay = it.next();
                                if (taskOverlay.mTaskOverlay) {
                                    prepareActivityHideTransitionAnimation(taskOverlay, transit);
                                }
                            }
                        }
                        this.mWindowManager.continueSurfaceLayout();
                        return removedActivity;
                    } catch (Throwable th2) {
                        th = th2;
                        this.mWindowManager.continueSurfaceLayout();
                        throw th;
                    }
                } else {
                    boolean z6 = oomAdj;
                }
            }
            this.mWindowManager.continueSurfaceLayout();
            return false;
        } catch (Throwable th3) {
            th = th3;
            boolean z7 = oomAdj;
            boolean z8 = pauseImmediately;
            this.mWindowManager.continueSurfaceLayout();
            throw th;
        }
    }

    private void prepareActivityHideTransitionAnimation(ActivityRecord r, int transit) {
        DisplayContent dc = getDisplay().mDisplayContent;
        dc.prepareAppTransition(transit, false);
        r.setVisibility(false);
        dc.executeAppTransition();
    }

    /* access modifiers changed from: package-private */
    public final ActivityRecord finishCurrentActivityLocked(ActivityRecord r, int mode, boolean oomAdj, String reason) {
        ActivityRecord activityRecord = r;
        int i = mode;
        ActivityDisplay display = getDisplay();
        boolean z = true;
        ActivityRecord next = display.topRunningActivity(true);
        boolean isFloating = r.getConfiguration().windowConfiguration.tasksAreFloating();
        if (i != 2 || ((!activityRecord.visible && !activityRecord.nowVisible) || next == null || ((next.nowVisible && !next.mIsDummyVisible) || isFloating))) {
            this.mStackSupervisor.mStoppingActivities.remove(activityRecord);
            this.mStackSupervisor.mGoingToSleepActivities.remove(activityRecord);
            ActivityState prevState = r.getState();
            if (!(mActivityPluginDelegate == null || getWindowingMode() == 0)) {
                ActivityPluginDelegate activityPluginDelegate = mActivityPluginDelegate;
                ActivityPluginDelegate.activitySuspendNotification(activityRecord.appInfo.packageName, getWindowingMode() == 1, false);
            }
            activityRecord.setState(ActivityState.FINISHING, "finishCurrentActivityLocked");
            boolean finishingInNonFocusedStackOrNoRunning = i == 2 && prevState == ActivityState.PAUSED && ((r.getActivityStack() != display.getFocusedStack()) || (next == null && display.topRunningActivity() == null && display.getHomeStack() == null));
            if (i == 0 || ((prevState == ActivityState.PAUSED && (i == 1 || inPinnedWindowingMode())) || finishingInNonFocusedStackOrNoRunning || prevState == ActivityState.STOPPING || prevState == ActivityState.STOPPED || prevState == ActivityState.INITIALIZING)) {
                r.makeFinishingLocked();
                boolean activityRemoved = destroyActivityLocked(activityRecord, true, "finish-imm:" + reason);
                if (finishingInNonFocusedStackOrNoRunning) {
                    this.mRootActivityContainer.ensureVisibilityAndConfig(next, this.mDisplayId, false, true);
                }
                if (activityRemoved) {
                    this.mRootActivityContainer.resumeFocusedStacksTopActivities();
                }
                if (activityRemoved) {
                    return null;
                }
                return activityRecord;
            }
            this.mStackSupervisor.mFinishingActivities.add(activityRecord);
            r.resumeKeyDispatchingLocked();
            this.mRootActivityContainer.resumeFocusedStacksTopActivities();
            if (activityRecord.isState(ActivityState.RESUMED) && this.mPausingActivity != null) {
                startPausingLocked(false, false, next, false);
            }
            return activityRecord;
        }
        if (!this.mStackSupervisor.mStoppingActivities.contains(activityRecord)) {
            addToStopping(activityRecord, false, false, "finishCurrentActivityLocked");
        }
        if (!(mActivityPluginDelegate == null || getWindowingMode() == 0)) {
            ActivityPluginDelegate activityPluginDelegate2 = mActivityPluginDelegate;
            String str = activityRecord.appInfo.packageName;
            if (getWindowingMode() != 1) {
                z = false;
            }
            ActivityPluginDelegate.activitySuspendNotification(str, z, false);
        }
        activityRecord.setState(ActivityState.STOPPING, "finishCurrentActivityLocked");
        if (oomAdj) {
            this.mService.updateOomAdj();
        }
        return activityRecord;
    }

    /* access modifiers changed from: package-private */
    public void finishAllActivitiesLocked(boolean immediately) {
        boolean noActivitiesInStack = true;
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            ArrayList<ActivityRecord> activities = this.mTaskHistory.get(taskNdx).mActivities;
            for (int activityNdx = activities.size() - 1; activityNdx >= 0; activityNdx--) {
                ActivityRecord r = activities.get(activityNdx);
                noActivitiesInStack = false;
                if (!r.finishing || immediately) {
                    Slog.d("ActivityTaskManager", "finishAllActivitiesLocked: finishing " + r + " immediately");
                    finishCurrentActivityLocked(r, 0, false, "finishAllActivitiesLocked");
                }
            }
        }
        if (noActivitiesInStack) {
            remove();
        }
    }

    /* access modifiers changed from: package-private */
    public boolean inFrontOfStandardStack() {
        int index;
        ActivityDisplay display = getDisplay();
        if (display == null || (index = display.getIndexOf(this)) == 0) {
            return false;
        }
        return display.getChildAt(index - 1).isActivityTypeStandard();
    }

    /* access modifiers changed from: package-private */
    public boolean shouldUpRecreateTaskLocked(ActivityRecord srec, String destAffinity) {
        if (srec == null || srec.getTaskRecord().affinity == null || !srec.getTaskRecord().affinity.equals(destAffinity)) {
            return true;
        }
        TaskRecord task = srec.getTaskRecord();
        if (srec.frontOfTask && task.getBaseIntent() != null && task.getBaseIntent().isDocument()) {
            if (!inFrontOfStandardStack()) {
                return true;
            }
            int taskIdx = this.mTaskHistory.indexOf(task);
            if (taskIdx <= 0) {
                Slog.w("ActivityTaskManager", "shouldUpRecreateTask: task not in history for " + srec);
                return false;
            } else if (!task.affinity.equals(this.mTaskHistory.get(taskIdx).affinity)) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x008c A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0098 A[LOOP:1: B:32:0x0096->B:33:0x0098, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00dc  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x0154  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final boolean navigateUpToLocked(com.android.server.wm.ActivityRecord r26, android.content.Intent r27, int r28, android.content.Intent r29) {
        /*
            r25 = this;
            r7 = r25
            r8 = r26
            r9 = r27
            com.android.server.wm.TaskRecord r10 = r26.getTaskRecord()
            java.util.ArrayList<com.android.server.wm.ActivityRecord> r11 = r10.mActivities
            int r12 = r11.indexOf(r8)
            java.util.ArrayList<com.android.server.wm.TaskRecord> r0 = r7.mTaskHistory
            boolean r0 = r0.contains(r10)
            r13 = 0
            if (r0 == 0) goto L_0x0164
            if (r12 >= 0) goto L_0x001d
            goto L_0x0164
        L_0x001d:
            int r0 = r12 + -1
            r1 = 0
            if (r0 >= 0) goto L_0x0024
            r2 = r1
            goto L_0x002a
        L_0x0024:
            java.lang.Object r2 = r11.get(r0)
            com.android.server.wm.ActivityRecord r2 = (com.android.server.wm.ActivityRecord) r2
        L_0x002a:
            r3 = 0
            android.content.ComponentName r14 = r27.getComponent()
            if (r12 <= 0) goto L_0x0063
            if (r14 == 0) goto L_0x0063
            r4 = r0
        L_0x0034:
            if (r4 < 0) goto L_0x0063
            java.lang.Object r5 = r11.get(r4)
            com.android.server.wm.ActivityRecord r5 = (com.android.server.wm.ActivityRecord) r5
            android.content.pm.ActivityInfo r6 = r5.info
            java.lang.String r6 = r6.packageName
            java.lang.String r15 = r14.getPackageName()
            boolean r6 = r6.equals(r15)
            if (r6 == 0) goto L_0x0060
            android.content.pm.ActivityInfo r6 = r5.info
            java.lang.String r6 = r6.name
            java.lang.String r15 = r14.getClassName()
            boolean r6 = r6.equals(r15)
            if (r6 == 0) goto L_0x0060
            r0 = r4
            r2 = r5
            r3 = 1
            r6 = r0
            r15 = r2
            r16 = r3
            goto L_0x0067
        L_0x0060:
            int r4 = r4 + -1
            goto L_0x0034
        L_0x0063:
            r6 = r0
            r15 = r2
            r16 = r3
        L_0x0067:
            com.android.server.wm.ActivityTaskManagerService r0 = r7.mService
            android.app.IActivityController r5 = r0.mController
            if (r5 == 0) goto L_0x008d
            android.view.IApplicationToken$Stub r0 = r8.appToken
            com.android.server.wm.ActivityRecord r2 = r7.topRunningActivityLocked(r0, r13)
            if (r2 == 0) goto L_0x008d
            r3 = 1
            java.lang.String r0 = r2.packageName     // Catch:{ RemoteException -> 0x007e }
            boolean r0 = r5.activityResuming(r0)     // Catch:{ RemoteException -> 0x007e }
            r3 = r0
            goto L_0x008a
        L_0x007e:
            r0 = move-exception
            com.android.server.wm.ActivityTaskManagerService r4 = r7.mService
            r4.mController = r1
            com.android.server.Watchdog r4 = com.android.server.Watchdog.getInstance()
            r4.setActivityController(r1)
        L_0x008a:
            if (r3 != 0) goto L_0x008d
            return r13
        L_0x008d:
            long r17 = android.os.Binder.clearCallingIdentity()
            r0 = r12
            r19 = r28
            r20 = r29
        L_0x0096:
            if (r0 <= r6) goto L_0x00c3
            java.lang.Object r1 = r11.get(r0)
            r4 = r1
            com.android.server.wm.ActivityRecord r4 = (com.android.server.wm.ActivityRecord) r4
            android.view.IApplicationToken$Stub r2 = r4.appToken
            r21 = 1
            java.lang.String r22 = "navigate-up"
            r1 = r25
            r3 = r19
            r23 = r4
            r4 = r20
            r24 = r5
            r5 = r22
            r22 = r6
            r6 = r21
            r1.requestFinishActivityLocked(r2, r3, r4, r5, r6)
            r19 = 0
            r20 = 0
            int r0 = r0 + -1
            r6 = r22
            r5 = r24
            goto L_0x0096
        L_0x00c3:
            r24 = r5
            r22 = r6
            if (r15 == 0) goto L_0x0160
            if (r16 == 0) goto L_0x0160
            android.content.pm.ActivityInfo r0 = r15.info
            int r6 = r0.launchMode
            int r21 = r27.getFlags()
            r0 = 3
            if (r6 == r0) goto L_0x0154
            r0 = 2
            if (r6 == r0) goto L_0x0154
            r0 = 1
            if (r6 == r0) goto L_0x0154
            r1 = 67108864(0x4000000, float:1.5046328E-36)
            r1 = r21 & r1
            if (r1 == 0) goto L_0x00e5
            r13 = r6
            goto L_0x0155
        L_0x00e5:
            android.content.pm.IPackageManager r1 = android.app.AppGlobals.getPackageManager()     // Catch:{ RemoteException -> 0x013f }
            android.content.ComponentName r2 = r27.getComponent()     // Catch:{ RemoteException -> 0x013f }
            r3 = 1024(0x400, float:1.435E-42)
            int r4 = r8.mUserId     // Catch:{ RemoteException -> 0x013f }
            android.content.pm.ActivityInfo r1 = r1.getActivityInfo(r2, r3, r4)     // Catch:{ RemoteException -> 0x013f }
            com.android.server.wm.ActivityTaskManagerService r2 = r7.mService     // Catch:{ RemoteException -> 0x013f }
            com.android.server.wm.ActivityStartController r2 = r2.getActivityStartController()     // Catch:{ RemoteException -> 0x013f }
            java.lang.String r3 = "navigateUpTo"
            com.android.server.wm.ActivityStarter r2 = r2.obtainStarter(r9, r3)     // Catch:{ RemoteException -> 0x013f }
            com.android.server.wm.WindowProcessController r3 = r8.app     // Catch:{ RemoteException -> 0x013f }
            android.app.IApplicationThread r3 = r3.getThread()     // Catch:{ RemoteException -> 0x013f }
            com.android.server.wm.ActivityStarter r2 = r2.setCaller(r3)     // Catch:{ RemoteException -> 0x013f }
            com.android.server.wm.ActivityStarter r2 = r2.setActivityInfo(r1)     // Catch:{ RemoteException -> 0x013f }
            android.view.IApplicationToken$Stub r3 = r15.appToken     // Catch:{ RemoteException -> 0x013f }
            com.android.server.wm.ActivityStarter r2 = r2.setResultTo(r3)     // Catch:{ RemoteException -> 0x013f }
            r3 = -1
            com.android.server.wm.ActivityStarter r2 = r2.setCallingPid(r3)     // Catch:{ RemoteException -> 0x013f }
            int r4 = r15.launchedFromUid     // Catch:{ RemoteException -> 0x013f }
            com.android.server.wm.ActivityStarter r2 = r2.setCallingUid(r4)     // Catch:{ RemoteException -> 0x013f }
            java.lang.String r4 = r15.launchedFromPackage     // Catch:{ RemoteException -> 0x013f }
            com.android.server.wm.ActivityStarter r2 = r2.setCallingPackage(r4)     // Catch:{ RemoteException -> 0x013f }
            com.android.server.wm.ActivityStarter r2 = r2.setRealCallingPid(r3)     // Catch:{ RemoteException -> 0x013f }
            int r3 = r15.launchedFromUid     // Catch:{ RemoteException -> 0x013f }
            com.android.server.wm.ActivityStarter r2 = r2.setRealCallingUid(r3)     // Catch:{ RemoteException -> 0x013f }
            com.android.server.wm.ActivityStarter r2 = r2.setComponentSpecified(r0)     // Catch:{ RemoteException -> 0x013f }
            int r2 = r2.execute()     // Catch:{ RemoteException -> 0x013f }
            if (r2 != 0) goto L_0x013b
            r13 = r0
        L_0x013b:
            r0 = r13
            r16 = r0
            goto L_0x0143
        L_0x013f:
            r0 = move-exception
            r1 = 0
            r16 = r1
        L_0x0143:
            android.view.IApplicationToken$Stub r2 = r15.appToken
            r0 = 1
            java.lang.String r5 = "navigate-top"
            r1 = r25
            r3 = r19
            r4 = r20
            r13 = r6
            r6 = r0
            r1.requestFinishActivityLocked(r2, r3, r4, r5, r6)
            goto L_0x0160
        L_0x0154:
            r13 = r6
        L_0x0155:
            android.content.pm.ActivityInfo r0 = r8.info
            android.content.pm.ApplicationInfo r0 = r0.applicationInfo
            int r0 = r0.uid
            java.lang.String r1 = r8.packageName
            r15.deliverNewIntentLocked(r0, r9, r1)
        L_0x0160:
            android.os.Binder.restoreCallingIdentity(r17)
            return r16
        L_0x0164:
            return r13
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityStack.navigateUpToLocked(com.android.server.wm.ActivityRecord, android.content.Intent, int, android.content.Intent):boolean");
    }

    /* access modifiers changed from: package-private */
    public void onActivityRemovedFromStack(ActivityRecord r) {
        removeTimeoutsForActivityLocked(r);
        ActivityRecord activityRecord = this.mResumedActivity;
        if (activityRecord != null && activityRecord == r) {
            setResumedActivity((ActivityRecord) null, "onActivityRemovedFromStack");
        }
        ActivityRecord activityRecord2 = this.mPausingActivity;
        if (activityRecord2 != null && activityRecord2 == r) {
            this.mPausingActivity = null;
        }
    }

    /* access modifiers changed from: package-private */
    public void onActivityAddedToStack(ActivityRecord r) {
        if (r.getState() == ActivityState.RESUMED) {
            setResumedActivity(r, "onActivityAddedToStack");
        }
    }

    private void cleanUpActivityLocked(ActivityRecord r, boolean cleanServices, boolean setState) {
        onActivityRemovedFromStack(r);
        r.deferRelaunchUntilPaused = false;
        r.frozenBeforeDestroy = false;
        if (setState) {
            r.setState(ActivityState.DESTROYED, "cleanupActivityLocked");
            r.app = null;
        }
        this.mStackSupervisor.cleanupActivity(r);
        if (r.finishing && r.pendingResults != null) {
            Iterator<WeakReference<PendingIntentRecord>> it = r.pendingResults.iterator();
            while (it.hasNext()) {
                PendingIntentRecord rec = (PendingIntentRecord) it.next().get();
                if (rec != null) {
                    this.mService.mPendingIntentController.cancelIntentSender(rec, false);
                }
            }
            r.pendingResults = null;
        }
        if (cleanServices) {
            cleanUpActivityServicesLocked(r);
        }
        removeTimeoutsForActivityLocked(r);
        this.mWindowManager.notifyAppRelaunchesCleared(r.appToken);
    }

    private void removeTimeoutsForActivityLocked(ActivityRecord r) {
        this.mStackSupervisor.removeTimeoutsForActivityLocked(r);
        this.mHandler.removeMessages(101, r);
        this.mHandler.removeMessages(104, r);
        this.mHandler.removeMessages(102, r);
        r.finishLaunchTickingLocked();
    }

    private void removeActivityFromHistoryLocked(ActivityRecord r, String reason) {
        finishActivityResultsLocked(r, 0, (Intent) null);
        r.makeFinishingLocked();
        r.takeFromHistory();
        removeTimeoutsForActivityLocked(r);
        r.setState(ActivityState.DESTROYED, "removeActivityFromHistoryLocked");
        r.app = null;
        r.removeWindowContainer();
        TaskRecord task = r.getTaskRecord();
        boolean lastActivity = task != null ? task.removeActivity(r) : false;
        boolean onlyHasTaskOverlays = task != null ? task.onlyHasTaskOverlayActivities(false) : false;
        if (lastActivity || onlyHasTaskOverlays) {
            if (onlyHasTaskOverlays) {
                this.mStackSupervisor.removeTaskByIdLocked(task.taskId, false, false, true, reason);
            }
            if (lastActivity) {
                removeTask(task, reason, 0);
            }
        }
        cleanUpActivityServicesLocked(r);
        r.removeUriPermissionsLocked();
    }

    private void cleanUpActivityServicesLocked(ActivityRecord r) {
        if (r.mServiceConnectionsHolder != null) {
            r.mServiceConnectionsHolder.disconnectActivityFromServices();
        }
    }

    /* access modifiers changed from: package-private */
    public final void scheduleDestroyActivities(WindowProcessController owner, String reason) {
        Message msg = this.mHandler.obtainMessage(105);
        msg.obj = new ScheduleDestroyArgs(owner, reason);
        this.mHandler.sendMessage(msg);
    }

    /* access modifiers changed from: private */
    public void destroyActivitiesLocked(WindowProcessController owner, String reason) {
        boolean lastIsOpaque = false;
        boolean activityRemoved = false;
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            ArrayList<ActivityRecord> activities = this.mTaskHistory.get(taskNdx).mActivities;
            for (int activityNdx = activities.size() - 1; activityNdx >= 0; activityNdx--) {
                ActivityRecord r = activities.get(activityNdx);
                if (!r.finishing) {
                    if (r.fullscreen) {
                        lastIsOpaque = true;
                    }
                    if ((owner == null || r.app == owner) && lastIsOpaque && r.isDestroyable() && destroyActivityLocked(r, true, reason)) {
                        activityRemoved = true;
                    }
                }
            }
        }
        if (activityRemoved) {
            this.mRootActivityContainer.resumeFocusedStacksTopActivities();
        }
    }

    /* access modifiers changed from: package-private */
    public final boolean safelyDestroyActivityLocked(ActivityRecord r, String reason) {
        if (r.isDestroyable()) {
            return destroyActivityLocked(r, true, reason);
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public final int releaseSomeActivitiesLocked(WindowProcessController app, ArraySet<TaskRecord> tasks, String reason) {
        int maxTasks = tasks.size() / 4;
        if (maxTasks < 1) {
            maxTasks = 1;
        }
        int numReleased = 0;
        int taskNdx = 0;
        while (taskNdx < this.mTaskHistory.size() && maxTasks > 0) {
            TaskRecord task = this.mTaskHistory.get(taskNdx);
            if (tasks.contains(task)) {
                int curNum = 0;
                ArrayList<ActivityRecord> activities = task.mActivities;
                int actNdx = 0;
                while (actNdx < activities.size()) {
                    ActivityRecord activity = activities.get(actNdx);
                    if (activity.app == app && activity.isDestroyable()) {
                        destroyActivityLocked(activity, true, reason);
                        if (activities.get(actNdx) != activity) {
                            actNdx--;
                        }
                        curNum++;
                    }
                    actNdx++;
                }
                if (curNum > 0) {
                    numReleased += curNum;
                    maxTasks--;
                    if (this.mTaskHistory.get(taskNdx) != task) {
                        taskNdx--;
                    }
                }
            }
            taskNdx++;
        }
        return numReleased;
    }

    /* access modifiers changed from: package-private */
    public final boolean destroyActivityLocked(ActivityRecord r, boolean removeFromApp, String reason) {
        if (r.isState(ActivityState.DESTROYING, ActivityState.DESTROYED)) {
            return false;
        }
        Object[] objArr = new Object[5];
        objArr[0] = Integer.valueOf(r.app != null ? r.app.getPid() : 0);
        objArr[1] = Integer.valueOf(System.identityHashCode(r));
        objArr[2] = Integer.valueOf(r.getTaskRecord().taskId);
        objArr[3] = r.shortComponentName;
        objArr[4] = reason;
        EventLog.writeEvent(EventLogTags.AM_DESTROY_ACTIVITY, objArr);
        boolean removedFromHistory = false;
        cleanUpActivityLocked(r, false, false);
        boolean hadApp = r.hasProcess();
        if (hadApp) {
            if (removeFromApp) {
                r.app.removeActivity(r);
                if (!r.app.hasActivities()) {
                    this.mService.clearHeavyWeightProcessIfEquals(r.app);
                }
                if (!r.app.hasActivities()) {
                    r.app.updateProcessInfo(true, false, true);
                }
            }
            boolean skipDestroy = false;
            try {
                this.mService.getLifecycleManager().scheduleTransaction(r.app.getThread(), (IBinder) r.appToken, (ActivityLifecycleItem) DestroyActivityItem.obtain(r.finishing, r.configChangeFlags));
                ActivityTaskManagerServiceInjector.destroyActivity(r.info);
            } catch (Exception e) {
                if (r.finishing) {
                    removeActivityFromHistoryLocked(r, reason + " exceptionInScheduleDestroy");
                    removedFromHistory = true;
                    skipDestroy = true;
                }
            }
            r.nowVisible = false;
            if (!r.finishing || skipDestroy) {
                r.setState(ActivityState.DESTROYED, "destroyActivityLocked. not finishing or skipping destroy");
                r.app = null;
            } else {
                r.setState(ActivityState.DESTROYING, "destroyActivityLocked. finishing and not skipping destroy");
                this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(102, r), JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY);
            }
        } else if (r.finishing) {
            removeActivityFromHistoryLocked(r, reason + " hadNoApp");
            removedFromHistory = true;
        } else {
            r.setState(ActivityState.DESTROYED, "destroyActivityLocked. not finishing and had no app");
            r.app = null;
        }
        r.configChangeFlags = 0;
        if (!this.mLRUActivities.remove(r) && hadApp) {
            Slog.w("ActivityTaskManager", "Activity " + r + " being finished, but not in LRU list");
        }
        return removedFromHistory;
    }

    /* access modifiers changed from: package-private */
    public final void activityDestroyedLocked(IBinder token, String reason) {
        long origId = Binder.clearCallingIdentity();
        try {
            activityDestroyedLocked(ActivityRecord.forTokenLocked(token), reason);
        } finally {
            Binder.restoreCallingIdentity(origId);
        }
    }

    /* access modifiers changed from: package-private */
    public final void activityDestroyedLocked(ActivityRecord record, String reason) {
        if (record != null) {
            this.mHandler.removeMessages(102, record);
        }
        if (isInStackLocked(record) != null && record.isState(ActivityState.DESTROYING, ActivityState.DESTROYED)) {
            cleanUpActivityLocked(record, true, false);
            removeActivityFromHistoryLocked(record, reason);
        }
        this.mRootActivityContainer.resumeFocusedStacksTopActivities();
    }

    private void removeHistoryRecordsForAppLocked(ArrayList<ActivityRecord> list, WindowProcessController app, String listName) {
        int i = list.size();
        while (i > 0) {
            i--;
            ActivityRecord r = list.get(i);
            if (r.app == app) {
                list.remove(i);
                removeTimeoutsForActivityLocked(r);
            }
        }
    }

    private boolean removeHistoryRecordsForAppLocked(WindowProcessController app) {
        boolean remove;
        WindowProcessController windowProcessController = app;
        removeHistoryRecordsForAppLocked(this.mLRUActivities, windowProcessController, "mLRUActivities");
        removeHistoryRecordsForAppLocked(this.mStackSupervisor.mStoppingActivities, windowProcessController, "mStoppingActivities");
        removeHistoryRecordsForAppLocked(this.mStackSupervisor.mGoingToSleepActivities, windowProcessController, "mGoingToSleepActivities");
        removeHistoryRecordsForAppLocked(this.mStackSupervisor.mFinishingActivities, windowProcessController, "mFinishingActivities");
        boolean isProcessRemoved = app.isRemoved();
        if (isProcessRemoved) {
            app.makeFinishingForProcessRemoved();
        }
        boolean hasVisibleActivities = false;
        int numActivities = numActivities();
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            ArrayList<ActivityRecord> activities = this.mTaskHistory.get(taskNdx).mActivities;
            this.mTmpActivities.clear();
            this.mTmpActivities.addAll(activities);
            while (!this.mTmpActivities.isEmpty()) {
                ActivityRecord r = this.mTmpActivities.remove(this.mTmpActivities.size() - 1);
                if (r.app == windowProcessController) {
                    if (r.visible) {
                        hasVisibleActivities = true;
                    }
                    if ((r.mRelaunchReason == 1 || r.mRelaunchReason == 2) && r.launchCount < 3 && !r.finishing) {
                        remove = false;
                    } else if ((!r.haveState && !r.stateNotNeeded && !r.isState(ActivityState.RESTARTING_PROCESS)) || r.finishing || this.mService.mShuttingDown) {
                        remove = true;
                    } else if (r.visible || r.launchCount <= 2 || r.lastLaunchTime <= SystemClock.uptimeMillis() - 60000) {
                        remove = false;
                    } else {
                        remove = true;
                    }
                    if (!remove) {
                        r.app = null;
                        r.nowVisible = r.visible;
                        if (!r.haveState) {
                            r.icicle = null;
                        }
                    } else if (!r.finishing || isProcessRemoved) {
                        Slog.w("ActivityTaskManager", "Force removing " + r + ": app died, no saved state");
                        Object[] objArr = new Object[5];
                        objArr[0] = Integer.valueOf(r.app != null ? r.app.getPid() : 0);
                        objArr[1] = Integer.valueOf(System.identityHashCode(r));
                        objArr[2] = Integer.valueOf(r.getTaskRecord().taskId);
                        objArr[3] = r.shortComponentName;
                        objArr[4] = "proc died without state saved";
                        EventLog.writeEvent(EventLogTags.AM_FINISH_ACTIVITY, objArr);
                    }
                    cleanUpActivityLocked(r, true, true);
                    if (remove) {
                        removeActivityFromHistoryLocked(r, "appDied");
                    }
                }
            }
        }
        return hasVisibleActivities;
    }

    private void updateTransitLocked(int transit, ActivityOptions options) {
        if (options != null) {
            ActivityRecord r = topRunningActivityLocked();
            if (r == null || r.isState(ActivityState.RESUMED)) {
                ActivityOptions.abort(options);
            } else {
                r.updateOptionsLocked(options);
            }
        }
        getDisplay().mDisplayContent.prepareAppTransition(transit, false);
    }

    private void updateTaskMovement(TaskRecord task, boolean toFront) {
        if (task.isPersistable) {
            task.mLastTimeMoved = System.currentTimeMillis();
            if (!toFront) {
                task.mLastTimeMoved *= -1;
            }
        }
        this.mRootActivityContainer.invalidateTaskLayers();
    }

    /* access modifiers changed from: package-private */
    public final void moveTaskToFrontLocked(TaskRecord tr, boolean noAnimation, ActivityOptions options, AppTimeTracker timeTracker, String reason) {
        TaskRecord taskRecord = tr;
        ActivityOptions activityOptions = options;
        AppTimeTracker appTimeTracker = timeTracker;
        ActivityStack topStack = getDisplay().getTopStack();
        ActivityRecord topActivity = topStack != null ? topStack.getTopActivity() : null;
        int numTasks = this.mTaskHistory.size();
        int index = this.mTaskHistory.indexOf(taskRecord);
        if (numTasks == 0) {
            String str = reason;
        } else if (index < 0) {
            String str2 = reason;
        } else {
            if (appTimeTracker != null) {
                for (int i = taskRecord.mActivities.size() - 1; i >= 0; i--) {
                    taskRecord.mActivities.get(i).appTimeTracker = appTimeTracker;
                }
            }
            try {
                getDisplay().deferUpdateImeTarget();
                insertTaskAtTop(taskRecord, (ActivityRecord) null);
                ActivityRecord top = tr.getTopActivity();
                if (top == null) {
                    String str3 = reason;
                } else if (!top.okToShowLocked()) {
                    String str4 = reason;
                } else {
                    ActivityRecord r = topRunningActivityLocked();
                    if (r != null) {
                        try {
                            r.moveFocusableActivityToTop(reason);
                        } catch (Throwable th) {
                            th = th;
                            getDisplay().continueUpdateImeTarget();
                            throw th;
                        }
                    } else {
                        String str5 = reason;
                    }
                    if (noAnimation) {
                        getDisplay().mDisplayContent.prepareAppTransition(0, false);
                        if (r != null) {
                            this.mStackSupervisor.mNoAnimActivities.add(r);
                        }
                        ActivityOptions.abort(options);
                    } else {
                        updateTransitLocked(10, activityOptions);
                    }
                    if (canEnterPipOnTaskSwitch(topActivity, taskRecord, (ActivityRecord) null, activityOptions)) {
                        topActivity.supportsEnterPipOnTaskSwitch = true;
                    }
                    this.mRootActivityContainer.resumeFocusedStacksTopActivities();
                    EventLog.writeEvent(EventLogTags.AM_TASK_TO_FRONT, new Object[]{Integer.valueOf(taskRecord.userId), Integer.valueOf(taskRecord.taskId)});
                    this.mService.getTaskChangeNotificationController().notifyTaskMovedToFront(tr.getTaskInfo());
                    getDisplay().continueUpdateImeTarget();
                    return;
                }
                if (top != null) {
                    this.mStackSupervisor.mRecentTasks.add(top.getTaskRecord());
                }
                ActivityOptions.abort(options);
                getDisplay().continueUpdateImeTarget();
                return;
            } catch (Throwable th2) {
                th = th2;
                String str6 = reason;
                getDisplay().continueUpdateImeTarget();
                throw th;
            }
        }
        if (noAnimation) {
            ActivityOptions.abort(options);
        } else {
            updateTransitLocked(10, activityOptions);
        }
    }

    /* access modifiers changed from: package-private */
    public final boolean moveTaskToBackLocked(int taskId) {
        TaskRecord tr = taskForIdLocked(taskId);
        if (tr == null) {
            Slog.i("ActivityTaskManager", "moveTaskToBack: bad taskId=" + taskId);
            return false;
        }
        Slog.i("ActivityTaskManager", "moveTaskToBack: " + tr);
        if (!this.mService.getLockTaskController().canMoveTaskToBack(tr)) {
            return false;
        }
        if (isTopStackOnDisplay() && this.mService.mController != null) {
            ActivityRecord next = topRunningActivityLocked((IBinder) null, taskId);
            if (next == null) {
                next = topRunningActivityLocked((IBinder) null, 0);
            }
            if (next != null) {
                boolean moveOK = true;
                try {
                    moveOK = this.mService.mController.activityResuming(next.packageName);
                } catch (RemoteException e) {
                    this.mService.mController = null;
                    Watchdog.getInstance().setActivityController((IActivityController) null);
                }
                if (!moveOK) {
                    return false;
                }
            }
        }
        this.mTaskHistory.remove(tr);
        this.mTaskHistory.add(0, tr);
        updateTaskMovement(tr, false);
        getDisplay().mDisplayContent.prepareAppTransition(11, false);
        moveToBack("moveTaskToBackLocked", tr);
        if (inPinnedWindowingMode()) {
            this.mStackSupervisor.removeStack(this);
            return true;
        }
        if (getDisplay().getFocusedStack() != null && getDisplay().getFocusedStack().isActivityTypeHome()) {
            ActivityRecord curTop = tr.getTopActivity();
            this.mService.updateMiuiAnimationInfo(curTop);
            this.mService.setIsMultiWindowMode(curTop);
        }
        ActivityRecord topActivity = getDisplay().topRunningActivity();
        ActivityStack topStack = topActivity.getActivityStack();
        if (!(topStack == null || topStack == this || !topActivity.isState(ActivityState.RESUMED))) {
            this.mRootActivityContainer.ensureVisibilityAndConfig((ActivityRecord) null, getDisplay().mDisplayId, false, false);
        }
        this.mRootActivityContainer.resumeFocusedStacksTopActivities();
        return true;
    }

    static void logStartActivity(int tag, ActivityRecord r, TaskRecord task) {
        Uri data = r.intent.getData();
        String strData = data != null ? data.toSafeString() : null;
        Object[] objArr = new Object[8];
        objArr[0] = Integer.valueOf(r.app != null ? r.app.getPid() : 0);
        objArr[1] = Integer.valueOf(System.identityHashCode(r));
        objArr[2] = Integer.valueOf(task.taskId);
        objArr[3] = r.shortComponentName;
        objArr[4] = r.intent.getAction();
        objArr[5] = r.intent.getType();
        objArr[6] = strData;
        objArr[7] = Integer.valueOf(r.intent.getFlags());
        EventLog.writeEvent(tag, objArr);
    }

    /* access modifiers changed from: package-private */
    public void ensureVisibleActivitiesConfigurationLocked(ActivityRecord start, boolean preserveWindow) {
        if (start != null && start.visible) {
            boolean behindFullscreen = false;
            boolean updatedConfig = false;
            for (int taskIndex = this.mTaskHistory.indexOf(start.getTaskRecord()); taskIndex >= 0; taskIndex--) {
                TaskRecord task = this.mTaskHistory.get(taskIndex);
                ArrayList<ActivityRecord> activities = task.mActivities;
                int activityIndex = start.getTaskRecord() == task ? activities.indexOf(start) : activities.size() - 1;
                while (true) {
                    if (activityIndex < 0) {
                        break;
                    }
                    ActivityRecord r = activities.get(activityIndex);
                    updatedConfig |= r.ensureActivityConfiguration(0, preserveWindow);
                    if (r.fullscreen) {
                        behindFullscreen = true;
                        break;
                    }
                    activityIndex--;
                }
                if (behindFullscreen) {
                    break;
                }
            }
            if (updatedConfig) {
                this.mRootActivityContainer.resumeFocusedStacksTopActivities();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void requestResize(Rect bounds) {
        this.mService.resizeStack(this.mStackId, bounds, true, false, false, -1);
    }

    /* access modifiers changed from: package-private */
    public void resize(Rect bounds, Rect tempTaskBounds, Rect tempTaskInsetBounds) {
        if (updateBoundsAllowed(bounds)) {
            Rect taskBounds = tempTaskBounds != null ? tempTaskBounds : bounds;
            for (int i = this.mTaskHistory.size() - 1; i >= 0; i--) {
                TaskRecord task = this.mTaskHistory.get(i);
                if (task.isResizeable()) {
                    task.updateOverrideConfiguration(taskBounds, tempTaskInsetBounds);
                }
            }
            setBounds(bounds);
        }
    }

    /* access modifiers changed from: package-private */
    public void onPipAnimationEndResize() {
        TaskStack taskStack = this.mTaskStack;
        if (taskStack != null) {
            taskStack.onPipAnimationEndResize();
        }
    }

    /* access modifiers changed from: package-private */
    public void setTaskBounds(Rect bounds) {
        if (updateBoundsAllowed(bounds)) {
            for (int i = this.mTaskHistory.size() - 1; i >= 0; i--) {
                TaskRecord task = this.mTaskHistory.get(i);
                if (task.isResizeable()) {
                    task.setBounds(bounds);
                } else {
                    task.setBounds((Rect) null);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setTaskDisplayedBounds(Rect bounds) {
        if (updateDisplayedBoundsAllowed(bounds)) {
            for (int i = this.mTaskHistory.size() - 1; i >= 0; i--) {
                TaskRecord task = this.mTaskHistory.get(i);
                if (bounds == null || bounds.isEmpty()) {
                    task.setDisplayedBounds((Rect) null);
                } else if (task.isResizeable()) {
                    task.setDisplayedBounds(bounds);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean willActivityBeVisibleLocked(IBinder token) {
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            ArrayList<ActivityRecord> activities = this.mTaskHistory.get(taskNdx).mActivities;
            for (int activityNdx = activities.size() - 1; activityNdx >= 0; activityNdx--) {
                ActivityRecord r = activities.get(activityNdx);
                if (r.appToken == token) {
                    return true;
                }
                if (r.fullscreen && !r.finishing) {
                    return false;
                }
            }
        }
        ActivityRecord r2 = ActivityRecord.forTokenLocked(token);
        if (r2 == null) {
            return false;
        }
        if (r2.finishing) {
            Slog.e("ActivityTaskManager", "willActivityBeVisibleLocked: Returning false, would have returned true for r=" + r2);
        }
        return true ^ r2.finishing;
    }

    /* access modifiers changed from: package-private */
    public void closeSystemDialogsLocked() {
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            ArrayList<ActivityRecord> activities = this.mTaskHistory.get(taskNdx).mActivities;
            for (int activityNdx = activities.size() - 1; activityNdx >= 0; activityNdx--) {
                ActivityRecord r = activities.get(activityNdx);
                if ((r.info.flags & 256) != 0) {
                    finishActivityLocked(r, 0, (Intent) null, "close-sys", true);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean finishDisabledPackageActivitiesLocked(String packageName, Set<String> filterByClasses, boolean doit, boolean evenPersistent, int userId) {
        ComponentName homeActivity;
        String str = packageName;
        Set<String> set = filterByClasses;
        int i = userId;
        boolean didSomething = false;
        TaskRecord lastTask = null;
        ComponentName homeActivity2 = null;
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            ArrayList<ActivityRecord> activities = this.mTaskHistory.get(taskNdx).mActivities;
            this.mTmpActivities.clear();
            this.mTmpActivities.addAll(activities);
            while (!this.mTmpActivities.isEmpty()) {
                boolean z = false;
                ActivityRecord r = this.mTmpActivities.remove(0);
                if ((r.packageName.equals(str) && (set == null || set.contains(r.mActivityComponent.getClassName()))) || (str == null && r.mUserId == i)) {
                    z = true;
                }
                boolean sameComponent = z;
                if ((i == -1 || r.mUserId == i) && (sameComponent || (r.getTaskRecord() == lastTask && !r.nowVisible))) {
                    if (r.app == null || evenPersistent || !r.app.isPersistent()) {
                        if (doit) {
                            if (!r.isActivityTypeHome()) {
                                homeActivity = homeActivity2;
                            } else if (homeActivity2 == null || !homeActivity2.equals(r.mActivityComponent)) {
                                homeActivity = r.mActivityComponent;
                            } else {
                                Slog.i("ActivityTaskManager", "Skip force-stop again " + r);
                            }
                            Slog.i("ActivityTaskManager", "  Force finishing activity " + r);
                            TaskRecord lastTask2 = r.getTaskRecord();
                            if (sameComponent) {
                                if (r.app != null) {
                                    r.app.removeActivity(r);
                                }
                                r.app = null;
                            }
                            finishActivityLocked(r, 0, (Intent) null, "force-stop", true);
                            homeActivity2 = homeActivity;
                            didSomething = true;
                            lastTask = lastTask2;
                        } else if (!r.finishing) {
                            return true;
                        }
                    }
                }
            }
        }
        return didSomething;
    }

    /* access modifiers changed from: package-private */
    public void getRunningTasks(List<TaskRecord> tasksOut, @WindowConfiguration.ActivityType int ignoreActivityType, @WindowConfiguration.WindowingMode int ignoreWindowingMode, int callingUid, boolean allowed) {
        boolean focusedStack = this.mRootActivityContainer.getTopDisplayFocusedStack() == this;
        boolean topTask = true;
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            TaskRecord task = this.mTaskHistory.get(taskNdx);
            if (task.getTopActivity() != null && ((allowed || task.isActivityTypeHome() || task.effectiveUid == callingUid) && ((ignoreActivityType == 0 || task.getActivityType() != ignoreActivityType) && (ignoreWindowingMode == 0 || task.getWindowingMode() != ignoreWindowingMode)))) {
                if (focusedStack && topTask) {
                    task.lastActiveTime = SystemClock.elapsedRealtime();
                    topTask = false;
                }
                tasksOut.add(task);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void unhandledBackLocked() {
        int top = this.mTaskHistory.size() - 1;
        if (top >= 0) {
            ArrayList<ActivityRecord> activities = this.mTaskHistory.get(top).mActivities;
            int activityTop = activities.size() - 1;
            if (activityTop >= 0) {
                finishActivityLocked(activities.get(activityTop), 0, (Intent) null, "unhandled-back", true);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean handleAppDiedLocked(WindowProcessController app) {
        ActivityRecord activityRecord = this.mPausingActivity;
        if (activityRecord != null && activityRecord.app == app) {
            this.mPausingActivity = null;
        }
        ActivityRecord activityRecord2 = this.mLastPausedActivity;
        if (activityRecord2 != null && activityRecord2.app == app) {
            this.mLastPausedActivity = null;
            this.mLastNoHistoryActivity = null;
        }
        return removeHistoryRecordsForAppLocked(app);
    }

    /* access modifiers changed from: package-private */
    public void handleAppCrash(WindowProcessController app) {
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            ArrayList<ActivityRecord> activities = this.mTaskHistory.get(taskNdx).mActivities;
            for (int activityNdx = activities.size() - 1; activityNdx >= 0; activityNdx--) {
                ActivityRecord r = activities.get(activityNdx);
                if (r.app == app) {
                    Slog.w("ActivityTaskManager", "  Force finishing activity " + r.intent.getComponent().flattenToShortString());
                    if (r.app != null) {
                        r.app.removeActivity(r);
                    }
                    r.app = null;
                    getDisplay().mDisplayContent.prepareAppTransition(26, false);
                    finishCurrentActivityLocked(r, 0, false, "handleAppCrashedLocked");
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean dump(FileDescriptor fd, PrintWriter pw, boolean dumpAll, boolean dumpClient, String dumpPackage, boolean needSep) {
        PrintWriter printWriter = pw;
        String str = dumpPackage;
        printWriter.println("  Stack #" + this.mStackId + ": type=" + WindowConfiguration.activityTypeToString(getActivityType()) + " mode=" + WindowConfiguration.windowingModeToString(getWindowingMode()));
        StringBuilder sb = new StringBuilder();
        sb.append("  isSleeping=");
        sb.append(shouldSleepActivities());
        printWriter.println(sb.toString());
        printWriter.println("  mBounds=" + getRequestedOverrideBounds());
        boolean printed = ActivityStackSupervisor.dumpHistoryList(fd, pw, this.mLRUActivities, "    ", "Run", false, dumpAll ^ true, false, dumpPackage, true, "    Running activities (most recent first):", (TaskRecord) null) | dumpActivitiesLocked(fd, pw, dumpAll, dumpClient, dumpPackage, needSep);
        boolean needSep2 = printed;
        if (ActivityStackSupervisor.printThisActivity(printWriter, this.mPausingActivity, str, needSep2, "    mPausingActivity: ")) {
            printed = true;
            needSep2 = false;
        }
        if (ActivityStackSupervisor.printThisActivity(printWriter, getResumedActivity(), str, needSep2, "    mResumedActivity: ")) {
            printed = true;
            needSep2 = false;
        }
        if (!dumpAll) {
            return printed;
        }
        if (ActivityStackSupervisor.printThisActivity(printWriter, this.mLastPausedActivity, str, needSep2, "    mLastPausedActivity: ")) {
            printed = true;
            needSep2 = true;
        }
        return printed | ActivityStackSupervisor.printThisActivity(printWriter, this.mLastNoHistoryActivity, str, needSep2, "    mLastNoHistoryActivity: ");
    }

    /* access modifiers changed from: package-private */
    public boolean dumpActivitiesLocked(FileDescriptor fd, PrintWriter pw, boolean dumpAll, boolean dumpClient, String dumpPackage, boolean needSep) {
        PrintWriter printWriter = pw;
        if (this.mTaskHistory.isEmpty()) {
            return false;
        }
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx += -1) {
            TaskRecord task = this.mTaskHistory.get(taskNdx);
            if (needSep) {
                printWriter.println("");
            }
            printWriter.println("    Task id #" + task.taskId);
            printWriter.println("    mBounds=" + task.getRequestedOverrideBounds());
            printWriter.println("    mMinWidth=" + task.mMinWidth);
            printWriter.println("    mMinHeight=" + task.mMinHeight);
            printWriter.println("    mLastNonFullscreenBounds=" + task.mLastNonFullscreenBounds);
            printWriter.println("    * " + task);
            task.dump(printWriter, "      ");
            ActivityStackSupervisor.dumpHistoryList(fd, pw, this.mTaskHistory.get(taskNdx).mActivities, "    ", "Hist", true, dumpAll ^ true, dumpClient, dumpPackage, false, (String) null, task);
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public ArrayList<ActivityRecord> getDumpActivitiesLocked(String name) {
        ArrayList<ActivityRecord> activities = new ArrayList<>();
        if ("all".equals(name)) {
            for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
                activities.addAll(this.mTaskHistory.get(taskNdx).mActivities);
            }
        } else if ("top".equals(name)) {
            int top = this.mTaskHistory.size() - 1;
            if (top >= 0) {
                ArrayList<ActivityRecord> list = this.mTaskHistory.get(top).mActivities;
                int listTop = list.size() - 1;
                if (listTop >= 0) {
                    activities.add(list.get(listTop));
                }
            }
        } else {
            ActivityManagerService.ItemMatcher matcher = new ActivityManagerService.ItemMatcher();
            matcher.build(name);
            for (int taskNdx2 = this.mTaskHistory.size() - 1; taskNdx2 >= 0; taskNdx2--) {
                Iterator<ActivityRecord> it = this.mTaskHistory.get(taskNdx2).mActivities.iterator();
                while (it.hasNext()) {
                    ActivityRecord r1 = it.next();
                    if (matcher.match(r1, r1.intent.getComponent())) {
                        activities.add(r1);
                    }
                }
            }
        }
        return activities;
    }

    /* access modifiers changed from: package-private */
    public ActivityRecord restartPackage(String packageName) {
        ActivityRecord starting = topRunningActivityLocked();
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            ArrayList<ActivityRecord> activities = this.mTaskHistory.get(taskNdx).mActivities;
            for (int activityNdx = activities.size() - 1; activityNdx >= 0; activityNdx--) {
                ActivityRecord a = activities.get(activityNdx);
                if (a.info.packageName.equals(packageName)) {
                    a.forceNewConfig = true;
                    if (starting != null && a == starting && a.visible) {
                        a.startFreezingScreenLocked(starting.app, 256);
                    }
                }
            }
        }
        return starting;
    }

    /* access modifiers changed from: package-private */
    public void removeTask(TaskRecord task, String reason, int mode) {
        if (this.mTaskHistory.remove(task)) {
            EventLog.writeEvent(EventLogTags.AM_REMOVE_TASK, new Object[]{Integer.valueOf(task.taskId), Integer.valueOf(getStackId())});
        }
        removeActivitiesFromLRUListLocked(task);
        updateTaskMovement(task, true);
        if (mode == 0) {
            task.cleanUpResourcesForDestroy();
        }
        if (this.mTaskHistory.isEmpty()) {
            if (mode != 2 && this.mRootActivityContainer.isTopDisplayFocusedStack(this)) {
                String myReason = reason + " leftTaskHistoryEmpty";
                if (!inMultiWindowMode() || adjustFocusToNextFocusableStack(myReason) == null) {
                    getDisplay().moveHomeStackToFront(myReason);
                }
            }
            if (isAttached()) {
                getDisplay().positionChildAtBottom(this);
            }
            if (!isActivityTypeHome() || !isAttached()) {
                remove();
            }
        }
        task.setStack((ActivityStack) null);
        if (inPinnedWindowingMode()) {
            this.mService.getTaskChangeNotificationController().notifyActivityUnpinned();
        }
    }

    /* access modifiers changed from: package-private */
    public TaskRecord createTaskRecord(int taskId, ActivityInfo info, Intent intent, IVoiceInteractionSession voiceSession, IVoiceInteractor voiceInteractor, boolean toTop) {
        return createTaskRecord(taskId, info, intent, voiceSession, voiceInteractor, toTop, (ActivityRecord) null, (ActivityRecord) null, (ActivityOptions) null);
    }

    /* access modifiers changed from: package-private */
    public TaskRecord createTaskRecord(int taskId, ActivityInfo info, Intent intent, IVoiceInteractionSession voiceSession, IVoiceInteractor voiceInteractor, boolean toTop, ActivityRecord activity, ActivityRecord source, ActivityOptions options) {
        ActivityInfo activityInfo = info;
        boolean z = toTop;
        TaskRecord task = TaskRecord.create(this.mService, taskId, info, intent, voiceSession, voiceInteractor);
        addTask(task, z, "createTaskRecord");
        int displayId = this.mDisplayId;
        boolean z2 = false;
        if (displayId == -1) {
            displayId = 0;
        }
        boolean isLockscreenShown = this.mService.mStackSupervisor.getKeyguardController().isKeyguardOrAodShowing(displayId);
        if (!this.mStackSupervisor.getLaunchParamsController().layoutTask(task, activityInfo.windowLayout, activity, source, options) && !matchParentBounds() && task.isResizeable() && !isLockscreenShown) {
            task.updateOverrideConfiguration(getRequestedOverrideBounds());
        }
        if ((activityInfo.flags & 1024) != 0) {
            z2 = true;
        }
        task.createTask(z, z2);
        return task;
    }

    /* access modifiers changed from: package-private */
    public ArrayList<TaskRecord> getAllTasks() {
        return new ArrayList<>(this.mTaskHistory);
    }

    /* access modifiers changed from: package-private */
    public void addTask(TaskRecord task, boolean toTop, String reason) {
        addTask(task, toTop ? Integer.MAX_VALUE : 0, true, reason);
        if (toTop) {
            positionChildWindowContainerAtTop(task);
        }
    }

    /* access modifiers changed from: package-private */
    public void addTask(TaskRecord task, int position, boolean schedulePictureInPictureModeChange, String reason) {
        this.mTaskHistory.remove(task);
        if (!isSingleTaskInstance() || this.mTaskHistory.isEmpty()) {
            int position2 = getAdjustedPositionForTask(task, position, (ActivityRecord) null);
            boolean toTop = position2 >= this.mTaskHistory.size();
            ActivityStack prevStack = preAddTask(task, reason, toTop);
            this.mTaskHistory.add(position2, task);
            task.setStack(this);
            updateTaskMovement(task, toTop);
            postAddTask(task, prevStack, schedulePictureInPictureModeChange);
            return;
        }
        throw new IllegalStateException("Can only have one child on stack=" + this);
    }

    /* access modifiers changed from: package-private */
    public void positionChildAt(TaskRecord task, int index) {
        if (task.getStack() == this) {
            task.updateOverrideConfigurationForStack(this);
            ActivityRecord topRunningActivity = task.topRunningActivityLocked();
            boolean wasResumed = topRunningActivity == task.getStack().mResumedActivity;
            insertTaskAtPosition(task, index);
            task.setStack(this);
            postAddTask(task, (ActivityStack) null, true);
            if (wasResumed) {
                if (this.mResumedActivity != null) {
                    Log.wtf("ActivityTaskManager", "mResumedActivity was already set when moving mResumedActivity from other stack to this stack mResumedActivity=" + this.mResumedActivity + " other mResumedActivity=" + topRunningActivity);
                }
                topRunningActivity.setState(ActivityState.RESUMED, "positionChildAt");
            }
            ensureActivitiesVisibleLocked((ActivityRecord) null, 0, false);
            this.mRootActivityContainer.resumeFocusedStacksTopActivities();
            return;
        }
        throw new IllegalArgumentException("AS.positionChildAt: task=" + task + " is not a child of stack=" + this + " current parent=" + task.getStack());
    }

    private ActivityStack preAddTask(TaskRecord task, String reason, boolean toTop) {
        ActivityStack prevStack = task.getStack();
        if (!(prevStack == null || prevStack == this)) {
            prevStack.removeTask(task, reason, toTop ? 2 : 1);
        }
        return prevStack;
    }

    private void postAddTask(TaskRecord task, ActivityStack prevStack, boolean schedulePictureInPictureModeChange) {
        if (schedulePictureInPictureModeChange && prevStack != null) {
            this.mStackSupervisor.scheduleUpdatePictureInPictureModeIfNeeded(task, prevStack);
        } else if (task.voiceSession != null) {
            try {
                task.voiceSession.taskStarted(task.intent, task.taskId);
            } catch (RemoteException e) {
            }
        }
    }

    public void setAlwaysOnTop(boolean alwaysOnTop) {
        if (isAlwaysOnTop() != alwaysOnTop) {
            super.setAlwaysOnTop(alwaysOnTop);
            getDisplay().positionChildAtTop(this, false);
        }
    }

    /* access modifiers changed from: package-private */
    public void moveToFrontAndResumeStateIfNeeded(ActivityRecord r, boolean moveToFront, boolean setResume, boolean setPause, String reason) {
        if (moveToFront) {
            ActivityState origState = r.getState();
            if (setResume) {
                r.setState(ActivityState.RESUMED, "moveToFrontAndResumeStateIfNeeded");
                updateLRUListLocked(r);
            }
            if (setPause) {
                this.mPausingActivity = r;
                schedulePauseTimeout(r);
            }
            moveToFront(reason);
            if (origState == ActivityState.RESUMED && r == this.mRootActivityContainer.getTopResumedActivity()) {
                this.mService.setResumedActivityUncheckLocked(r, reason);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public Rect getDefaultPictureInPictureBounds(float aspectRatio) {
        if (getTaskStack() == null) {
            return null;
        }
        return getTaskStack().getPictureInPictureBounds(aspectRatio, (Rect) null);
    }

    /* access modifiers changed from: package-private */
    public void animateResizePinnedStack(Rect sourceHintBounds, Rect toBounds, int animationDuration, boolean fromFullscreen) {
        if (inPinnedWindowingMode()) {
            if (skipResizeAnimation(toBounds == null)) {
                this.mService.moveTasksToFullscreenStack(this.mStackId, true);
            } else if (getTaskStack() != null) {
                getTaskStack().animateResizePinnedStack(toBounds, sourceHintBounds, animationDuration, fromFullscreen);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void getAnimationOrCurrentBounds(Rect outBounds) {
        TaskStack stack = getTaskStack();
        if (stack == null) {
            outBounds.setEmpty();
        } else {
            stack.getAnimationOrCurrentBounds(outBounds);
        }
    }

    private boolean skipResizeAnimation(boolean toFullscreen) {
        if (!toFullscreen) {
            return false;
        }
        Configuration parentConfig = getParent().getConfiguration();
        ActivityRecord top = topRunningNonOverlayTaskActivity();
        if (top == null || top.isConfigurationCompatible(parentConfig)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public void setPictureInPictureAspectRatio(float aspectRatio) {
        if (getTaskStack() != null) {
            getTaskStack().setPictureInPictureAspectRatio(aspectRatio);
        }
    }

    /* access modifiers changed from: package-private */
    public void setPictureInPictureActions(List<RemoteAction> actions) {
        if (getTaskStack() != null) {
            getTaskStack().setPictureInPictureActions(actions);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isAnimatingBoundsToFullscreen() {
        if (getTaskStack() == null) {
            return false;
        }
        return getTaskStack().isAnimatingBoundsToFullscreen();
    }

    public void updatePictureInPictureModeForPinnedStackAnimation(Rect targetStackBounds, boolean forceUpdate) {
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                if (!isAttached()) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                ArrayList<TaskRecord> tasks = getAllTasks();
                for (int i = 0; i < tasks.size(); i++) {
                    this.mStackSupervisor.updatePictureInPictureMode(tasks.get(i), targetStackBounds, forceUpdate);
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

    public int getStackId() {
        return this.mStackId;
    }

    public String toString() {
        return "ActivityStack{" + Integer.toHexString(System.identityHashCode(this)) + " stackId=" + this.mStackId + " type=" + WindowConfiguration.activityTypeToString(getActivityType()) + " mode=" + WindowConfiguration.windowingModeToString(getWindowingMode()) + " visible=" + shouldBeVisible((ActivityRecord) null) + " translucent=" + isStackTranslucent((ActivityRecord) null) + ", " + this.mTaskHistory.size() + " tasks}";
    }

    /* access modifiers changed from: package-private */
    public void onLockTaskPackagesUpdated() {
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            this.mTaskHistory.get(taskNdx).setLockTaskAuth();
        }
    }

    /* access modifiers changed from: package-private */
    public void executeAppTransition(ActivityOptions options) {
        getDisplay().mDisplayContent.executeAppTransition();
        ActivityOptions.abort(options);
    }

    /* access modifiers changed from: package-private */
    public boolean shouldSleepActivities() {
        ActivityDisplay display = getDisplay();
        if (!isFocusedStackOnDisplay() || !this.mStackSupervisor.getKeyguardController().isKeyguardGoingAway()) {
            return display != null ? display.isSleeping() : this.mService.isSleepingLocked();
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean shouldSleepOrShutDownActivities() {
        return shouldSleepActivities() || this.mService.mShuttingDown;
    }

    public void writeToProto(ProtoOutputStream proto, long fieldId, int logLevel) {
        long token = proto.start(fieldId);
        super.writeToProto(proto, 1146756268033L, logLevel);
        proto.write(1120986464258L, this.mStackId);
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            this.mTaskHistory.get(taskNdx).writeToProto(proto, 2246267895811L, logLevel);
        }
        ActivityRecord activityRecord = this.mResumedActivity;
        if (activityRecord != null) {
            activityRecord.writeIdentifierToProto(proto, 1146756268036L);
        }
        proto.write(1120986464261L, this.mDisplayId);
        if (!matchParentBounds()) {
            getRequestedOverrideBounds().writeToProto(proto, 1146756268039L);
        }
        proto.write(1133871366150L, matchParentBounds());
        proto.end(token);
    }
}
