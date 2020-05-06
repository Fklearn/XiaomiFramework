package com.android.server.wm;

import android.app.ActivityThread;
import android.app.IActivityController;
import android.app.IApplicationThread;
import android.app.ProfilerInfo;
import android.app.servertransaction.ConfigurationChangeItem;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.ArraySet;
import android.util.Log;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.server.Watchdog;
import com.android.server.am.ActivityManagerService;
import com.android.server.job.controllers.JobStatus;
import com.android.server.pm.PackageManagerService;
import com.android.server.wm.ActivityStack;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class WindowProcessController extends ConfigurationContainer<ConfigurationContainer> implements ConfigurationContainerListener {
    private static final String TAG = "ActivityTaskManager";
    private static final String TAG_CONFIGURATION = "ActivityTaskManager";
    private static final String TAG_RELEASE = "ActivityTaskManager";
    private final ArrayList<ActivityRecord> mActivities = new ArrayList<>();
    private volatile boolean mAllowBackgroundActivityStarts;
    private final ActivityTaskManagerService mAtm;
    private volatile ArraySet<Integer> mBoundClientUids = new ArraySet<>();
    private volatile boolean mCrashing;
    private volatile int mCurProcState = 21;
    private volatile int mCurSchedGroup;
    private volatile boolean mDebugging;
    private int mDisplayId;
    private volatile long mFgInteractionTime;
    private volatile boolean mHasClientActivities;
    private volatile boolean mHasForegroundActivities;
    private volatile boolean mHasForegroundServices;
    private volatile boolean mHasOverlayUi;
    private volatile boolean mHasTopUi;
    final ApplicationInfo mInfo;
    private volatile boolean mInstrumenting;
    private volatile boolean mInstrumentingWithBackgroundActivityStartPrivileges;
    private volatile long mInteractionEventTime;
    private long mLastActivityFinishTime;
    private long mLastActivityLaunchTime;
    private final Configuration mLastReportedConfiguration;
    private final WindowProcessListener mListener;
    final String mName;
    private volatile boolean mNotResponding;
    public final Object mOwner;
    private volatile boolean mPendingUiClean;
    private volatile boolean mPerceptible;
    private volatile boolean mPersistent;
    private volatile int mPid;
    final ArraySet<String> mPkgList = new ArraySet<>();
    private ActivityRecord mPreQTopResumedActivity = null;
    private final ArrayList<TaskRecord> mRecentTasks = new ArrayList<>();
    private volatile int mRepProcState = 21;
    private volatile String mRequiredAbi;
    private IApplicationThread mThread;
    final int mUid;
    final int mUserId;
    private volatile boolean mUsingWrapper;
    int mVrThreadTid;
    private volatile long mWhenUnimportant;

    public interface ComputeOomAdjCallback {
        void onOtherActivity();

        void onPausedActivity();

        void onStoppingActivity(boolean z);

        void onVisibleActivity();
    }

    public WindowProcessController(ActivityTaskManagerService atm, ApplicationInfo info, String name, int uid, int userId, Object owner, WindowProcessListener listener) {
        this.mInfo = info;
        this.mName = name;
        this.mUid = uid;
        this.mUserId = userId;
        this.mOwner = owner;
        this.mListener = listener;
        this.mAtm = atm;
        this.mLastReportedConfiguration = new Configuration();
        this.mDisplayId = -1;
        if (atm != null) {
            onConfigurationChanged(atm.getGlobalConfiguration());
        }
    }

    public void setPid(int pid) {
        this.mPid = pid;
    }

    public int getPid() {
        return this.mPid;
    }

    public void setThread(IApplicationThread thread) {
        synchronized (this.mAtm.mGlobalLockWithoutBoost) {
            this.mThread = thread;
        }
    }

    /* access modifiers changed from: package-private */
    public IApplicationThread getThread() {
        return this.mThread;
    }

    /* access modifiers changed from: package-private */
    public boolean hasThread() {
        return this.mThread != null;
    }

    public void setCurrentSchedulingGroup(int curSchedGroup) {
        this.mCurSchedGroup = curSchedGroup;
    }

    /* access modifiers changed from: package-private */
    public int getCurrentSchedulingGroup() {
        return this.mCurSchedGroup;
    }

    public void setCurrentProcState(int curProcState) {
        this.mCurProcState = curProcState;
    }

    /* access modifiers changed from: package-private */
    public int getCurrentProcState() {
        return this.mCurProcState;
    }

    public void setReportedProcState(int repProcState) {
        this.mRepProcState = repProcState;
    }

    /* access modifiers changed from: package-private */
    public int getReportedProcState() {
        return this.mRepProcState;
    }

    public void setCrashing(boolean crashing) {
        this.mCrashing = crashing;
    }

    /* access modifiers changed from: package-private */
    public boolean isCrashing() {
        return this.mCrashing;
    }

    public void setNotResponding(boolean notResponding) {
        this.mNotResponding = notResponding;
    }

    /* access modifiers changed from: package-private */
    public boolean isNotResponding() {
        return this.mNotResponding;
    }

    public void setPersistent(boolean persistent) {
        this.mPersistent = persistent;
    }

    /* access modifiers changed from: package-private */
    public boolean isPersistent() {
        return this.mPersistent;
    }

    public void setHasForegroundServices(boolean hasForegroundServices) {
        this.mHasForegroundServices = hasForegroundServices;
    }

    /* access modifiers changed from: package-private */
    public boolean hasForegroundServices() {
        return this.mHasForegroundServices;
    }

    public void setHasForegroundActivities(boolean hasForegroundActivities) {
        this.mHasForegroundActivities = hasForegroundActivities;
    }

    /* access modifiers changed from: package-private */
    public boolean hasForegroundActivities() {
        return this.mHasForegroundActivities;
    }

    public void setHasClientActivities(boolean hasClientActivities) {
        this.mHasClientActivities = hasClientActivities;
    }

    /* access modifiers changed from: package-private */
    public boolean hasClientActivities() {
        return this.mHasClientActivities;
    }

    public void setHasTopUi(boolean hasTopUi) {
        this.mHasTopUi = hasTopUi;
    }

    /* access modifiers changed from: package-private */
    public boolean hasTopUi() {
        return this.mHasTopUi;
    }

    public void setHasOverlayUi(boolean hasOverlayUi) {
        this.mHasOverlayUi = hasOverlayUi;
    }

    /* access modifiers changed from: package-private */
    public boolean hasOverlayUi() {
        return this.mHasOverlayUi;
    }

    public void setPendingUiClean(boolean hasPendingUiClean) {
        this.mPendingUiClean = hasPendingUiClean;
    }

    /* access modifiers changed from: package-private */
    public boolean hasPendingUiClean() {
        return this.mPendingUiClean;
    }

    /* access modifiers changed from: package-private */
    public boolean registeredForDisplayConfigChanges() {
        return this.mDisplayId != -1;
    }

    /* access modifiers changed from: package-private */
    public void postPendingUiCleanMsg(boolean pendingUiClean) {
        WindowProcessListener windowProcessListener = this.mListener;
        if (windowProcessListener != null) {
            this.mAtm.mH.sendMessage(PooledLambda.obtainMessage($$Lambda$zP5AObb0vZzwrv8NXOg4Yt1c.INSTANCE, windowProcessListener, Boolean.valueOf(pendingUiClean)));
        }
    }

    public void setInteractionEventTime(long interactionEventTime) {
        this.mInteractionEventTime = interactionEventTime;
    }

    /* access modifiers changed from: package-private */
    public long getInteractionEventTime() {
        return this.mInteractionEventTime;
    }

    public void setFgInteractionTime(long fgInteractionTime) {
        this.mFgInteractionTime = fgInteractionTime;
    }

    /* access modifiers changed from: package-private */
    public long getFgInteractionTime() {
        return this.mFgInteractionTime;
    }

    public void setWhenUnimportant(long whenUnimportant) {
        this.mWhenUnimportant = whenUnimportant;
    }

    /* access modifiers changed from: package-private */
    public long getWhenUnimportant() {
        return this.mWhenUnimportant;
    }

    public void setRequiredAbi(String requiredAbi) {
        this.mRequiredAbi = requiredAbi;
    }

    /* access modifiers changed from: package-private */
    public String getRequiredAbi() {
        return this.mRequiredAbi;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public int getDisplayId() {
        return this.mDisplayId;
    }

    public void setDebugging(boolean debugging) {
        this.mDebugging = debugging;
    }

    /* access modifiers changed from: package-private */
    public boolean isDebugging() {
        return this.mDebugging;
    }

    public void setUsingWrapper(boolean usingWrapper) {
        this.mUsingWrapper = usingWrapper;
    }

    /* access modifiers changed from: package-private */
    public boolean isUsingWrapper() {
        return this.mUsingWrapper;
    }

    /* access modifiers changed from: package-private */
    public void setLastActivityLaunchTime(long launchTime) {
        if (launchTime > this.mLastActivityLaunchTime) {
            this.mLastActivityLaunchTime = launchTime;
        }
    }

    /* access modifiers changed from: package-private */
    public void setLastActivityFinishTimeIfNeeded(long finishTime) {
        if (finishTime > this.mLastActivityFinishTime && hasActivityInVisibleTask()) {
            this.mLastActivityFinishTime = finishTime;
        }
    }

    public void setAllowBackgroundActivityStarts(boolean allowBackgroundActivityStarts) {
        this.mAllowBackgroundActivityStarts = allowBackgroundActivityStarts;
    }

    /* access modifiers changed from: package-private */
    public boolean areBackgroundActivityStartsAllowed() {
        if (this.mAllowBackgroundActivityStarts) {
            return true;
        }
        long now = SystemClock.uptimeMillis();
        if (((now - this.mLastActivityLaunchTime < JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY || now - this.mLastActivityFinishTime < JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY) && (this.mLastActivityLaunchTime > this.mAtm.getLastStopAppSwitchesTime() || this.mLastActivityFinishTime > this.mAtm.getLastStopAppSwitchesTime())) || this.mInstrumentingWithBackgroundActivityStartPrivileges || hasActivityInVisibleTask() || isBoundByForegroundUid()) {
            return true;
        }
        return false;
    }

    private boolean isBoundByForegroundUid() {
        for (int i = this.mBoundClientUids.size() - 1; i >= 0; i--) {
            if (this.mAtm.isUidForeground(this.mBoundClientUids.valueAt(i).intValue())) {
                return true;
            }
        }
        return false;
    }

    public void setBoundClientUids(ArraySet<Integer> boundClientUids) {
        this.mBoundClientUids = boundClientUids;
    }

    public void setInstrumenting(boolean instrumenting, boolean hasBackgroundActivityStartPrivileges) {
        this.mInstrumenting = instrumenting;
        this.mInstrumentingWithBackgroundActivityStartPrivileges = hasBackgroundActivityStartPrivileges;
    }

    /* access modifiers changed from: package-private */
    public boolean isInstrumenting() {
        return this.mInstrumenting;
    }

    public void setPerceptible(boolean perceptible) {
        this.mPerceptible = perceptible;
    }

    /* access modifiers changed from: package-private */
    public boolean isPerceptible() {
        return this.mPerceptible;
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
        return null;
    }

    public void addPackage(String packageName) {
        synchronized (this.mAtm.mGlobalLockWithoutBoost) {
            this.mPkgList.add(packageName);
        }
    }

    public void clearPackageList() {
        synchronized (this.mAtm.mGlobalLockWithoutBoost) {
            this.mPkgList.clear();
        }
    }

    /* access modifiers changed from: package-private */
    public ArrayList<ActivityRecord> getAllActivities() {
        return new ArrayList<>(this.mActivities);
    }

    /* access modifiers changed from: package-private */
    public void addActivityIfNeeded(ActivityRecord r) {
        setLastActivityLaunchTime(r.lastLaunchTime);
        if (!this.mActivities.contains(r)) {
            this.mActivities.add(r);
        }
    }

    /* access modifiers changed from: package-private */
    public void removeActivity(ActivityRecord r) {
        this.mActivities.remove(r);
    }

    /* access modifiers changed from: package-private */
    public void makeFinishingForProcessRemoved() {
        for (int i = this.mActivities.size() - 1; i >= 0; i--) {
            this.mActivities.get(i).makeFinishingLocked();
        }
    }

    /* access modifiers changed from: package-private */
    public void clearActivities() {
        this.mActivities.clear();
    }

    public boolean hasActivities() {
        boolean z;
        synchronized (this.mAtm.mGlobalLockWithoutBoost) {
            z = !this.mActivities.isEmpty();
        }
        return z;
    }

    public boolean hasVisibleActivities() {
        synchronized (this.mAtm.mGlobalLockWithoutBoost) {
            for (int i = this.mActivities.size() - 1; i >= 0; i--) {
                if (this.mActivities.get(i).visible) {
                    return true;
                }
            }
            return false;
        }
    }

    public boolean hasActivitiesOrRecentTasks() {
        boolean z;
        synchronized (this.mAtm.mGlobalLockWithoutBoost) {
            if (this.mActivities.isEmpty()) {
                if (this.mRecentTasks.isEmpty()) {
                    z = false;
                }
            }
            z = true;
        }
        return z;
    }

    private boolean hasActivityInVisibleTask() {
        ActivityRecord topActivity;
        for (int i = this.mActivities.size() - 1; i >= 0; i--) {
            TaskRecord task = this.mActivities.get(i).getTaskRecord();
            if (task != null && (topActivity = task.getTopActivity()) != null && topActivity.visible) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean updateTopResumingActivityInProcessIfNeeded(ActivityRecord activity) {
        ActivityStack stack;
        if (this.mInfo.targetSdkVersion >= 29 || this.mPreQTopResumedActivity == activity) {
            return true;
        }
        ActivityDisplay display = activity.getDisplay();
        if (display == null) {
            return false;
        }
        boolean canUpdate = false;
        ActivityRecord activityRecord = this.mPreQTopResumedActivity;
        ActivityDisplay topDisplay = activityRecord != null ? activityRecord.getDisplay() : null;
        if (topDisplay == null || !this.mPreQTopResumedActivity.visible || !this.mPreQTopResumedActivity.isFocusable()) {
            canUpdate = true;
        }
        if (!canUpdate && topDisplay.mDisplayContent.compareTo((WindowContainer) display.mDisplayContent) < 0) {
            canUpdate = true;
        }
        if (display == topDisplay && this.mPreQTopResumedActivity.getActivityStack().mTaskStack.compareTo(activity.getActivityStack().mTaskStack) <= 0) {
            canUpdate = true;
        }
        if (canUpdate) {
            ActivityRecord activityRecord2 = this.mPreQTopResumedActivity;
            if (!(activityRecord2 == null || !activityRecord2.isState(ActivityStack.ActivityState.RESUMED) || (stack = this.mPreQTopResumedActivity.getActivityStack()) == null)) {
                stack.startPausingLocked(false, false, (ActivityRecord) null, false);
            }
            this.mPreQTopResumedActivity = activity;
        }
        return canUpdate;
    }

    public void stopFreezingActivities() {
        synchronized (this.mAtm.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                int i = this.mActivities.size();
                while (i > 0) {
                    i--;
                    this.mActivities.get(i).stopFreezingScreenLocked(true);
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

    /* access modifiers changed from: package-private */
    public void finishActivities() {
        ArrayList<ActivityRecord> activities = new ArrayList<>(this.mActivities);
        for (int i = 0; i < activities.size(); i++) {
            ActivityRecord r = activities.get(i);
            if (!r.finishing && r.isInStackLocked()) {
                r.getActivityStack().finishActivityLocked(r, 0, (Intent) null, "finish-heavy", true);
            }
        }
    }

    public boolean isInterestingToUser() {
        synchronized (this.mAtm.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                int size = this.mActivities.size();
                for (int i = 0; i < size; i++) {
                    if (this.mActivities.get(i).isInterestingToUserLocked()) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return true;
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

    public boolean hasRunningActivity(String packageName) {
        synchronized (this.mAtm.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                for (int i = this.mActivities.size() - 1; i >= 0; i--) {
                    if (packageName.equals(this.mActivities.get(i).packageName)) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return true;
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

    public void clearPackagePreferredForHomeActivities() {
        synchronized (this.mAtm.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                for (int i = this.mActivities.size() - 1; i >= 0; i--) {
                    ActivityRecord r = this.mActivities.get(i);
                    if (r.isActivityTypeHome()) {
                        Log.i("ActivityTaskManager", "Clearing package preferred activities from " + r.packageName);
                        try {
                            ActivityThread.getPackageManager().clearPackagePreferredActivities(r.packageName);
                        } catch (RemoteException e) {
                        }
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

    /* access modifiers changed from: package-private */
    public boolean hasStartedActivity(ActivityRecord launchedActivity) {
        for (int i = this.mActivities.size() - 1; i >= 0; i--) {
            ActivityRecord activity = this.mActivities.get(i);
            if (launchedActivity != activity && !activity.stopped) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void updateIntentForHeavyWeightActivity(Intent intent) {
        if (!this.mActivities.isEmpty()) {
            ActivityRecord hist = this.mActivities.get(0);
            intent.putExtra("cur_app", hist.packageName);
            intent.putExtra("cur_task", hist.getTaskRecord().taskId);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean shouldKillProcessForRemovedTask(TaskRecord tr) {
        for (int k = 0; k < this.mActivities.size(); k++) {
            ActivityRecord activity = this.mActivities.get(k);
            if (!activity.stopped) {
                return false;
            }
            TaskRecord otherTask = activity.getTaskRecord();
            if (tr.taskId != otherTask.taskId && otherTask.inRecents) {
                return false;
            }
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public ArraySet<TaskRecord> getReleaseSomeActivitiesTasks() {
        TaskRecord task;
        TaskRecord firstTask = null;
        ArraySet<TaskRecord> tasks = null;
        for (int i = 0; i < this.mActivities.size(); i++) {
            ActivityRecord r = this.mActivities.get(i);
            if (r.finishing || r.isState(ActivityStack.ActivityState.DESTROYING, ActivityStack.ActivityState.DESTROYED)) {
                return null;
            }
            if (!r.visible && r.stopped && r.haveState && !r.isState(ActivityStack.ActivityState.RESUMED, ActivityStack.ActivityState.PAUSING, ActivityStack.ActivityState.PAUSED, ActivityStack.ActivityState.STOPPING) && (task = r.getTaskRecord()) != null) {
                if (firstTask == null) {
                    firstTask = task;
                } else if (firstTask != task) {
                    if (tasks == null) {
                        tasks = new ArraySet<>();
                        tasks.add(firstTask);
                    }
                    tasks.add(task);
                }
            }
        }
        return tasks;
    }

    public int computeOomAdjFromActivities(int minTaskLayer, ComputeOomAdjCallback callback) {
        synchronized (this.mAtm.mGlobalLockWithoutBoost) {
            int activitiesSize = this.mActivities.size();
            int j = 0;
            while (true) {
                if (j >= activitiesSize) {
                    break;
                }
                ActivityRecord r = this.mActivities.get(j);
                if (r.app != this) {
                    Log.e("ActivityTaskManager", "Found activity " + r + " in proc activity list using " + r.app + " instead of expected " + this);
                    if (r.app == null || r.app.mUid == this.mUid) {
                        r.setProcess(this);
                    } else {
                        j++;
                    }
                }
                if (r.visible) {
                    callback.onVisibleActivity();
                    TaskRecord task = r.getTaskRecord();
                    if (task != null && minTaskLayer > 0) {
                        int layer = task.mLayerRank;
                        if (layer >= 0 && minTaskLayer > layer) {
                            minTaskLayer = layer;
                        }
                    }
                } else {
                    if (r.isState(ActivityStack.ActivityState.PAUSING, ActivityStack.ActivityState.PAUSED)) {
                        callback.onPausedActivity();
                    } else if (r.isState(ActivityStack.ActivityState.STOPPING)) {
                        callback.onStoppingActivity(r.finishing);
                    } else {
                        callback.onOtherActivity();
                    }
                    j++;
                }
            }
        }
        return minTaskLayer;
    }

    public int computeRelaunchReason() {
        synchronized (this.mAtm.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                for (int i = this.mActivities.size() - 1; i >= 0; i--) {
                    ActivityRecord r = this.mActivities.get(i);
                    if (r.mRelaunchReason != 0) {
                        int i2 = r.mRelaunchReason;
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return i2;
                    }
                }
                WindowManagerService.resetPriorityAfterLockedSection();
                return 0;
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    public long getInputDispatchingTimeout() {
        long j;
        synchronized (this.mAtm.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                if (!isInstrumenting()) {
                    if (!isUsingWrapper()) {
                        j = 8000;
                    }
                }
                j = 60000;
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return j;
    }

    /* access modifiers changed from: package-private */
    public void clearProfilerIfNeeded() {
        if (this.mListener != null) {
            this.mAtm.mH.sendMessage(PooledLambda.obtainMessage($$Lambda$9Kj79sYFqaGRhFHazfExnbZExw.INSTANCE, this.mListener));
        }
    }

    /* access modifiers changed from: package-private */
    public void updateProcessInfo(boolean updateServiceConnectionActivities, boolean activityChange, boolean updateOomAdj) {
        WindowProcessListener windowProcessListener = this.mListener;
        if (windowProcessListener != null) {
            this.mAtm.mH.sendMessage(PooledLambda.obtainMessage($$Lambda$BEx3OWenCvYAaV5h_J2ZkZXhEcY.INSTANCE, windowProcessListener, Boolean.valueOf(updateServiceConnectionActivities), Boolean.valueOf(activityChange), Boolean.valueOf(updateOomAdj)));
        }
    }

    /* access modifiers changed from: package-private */
    public void updateServiceConnectionActivities() {
        if (this.mListener != null) {
            this.mAtm.mH.sendMessage(PooledLambda.obtainMessage($$Lambda$HLz_SQuxQoIiuaK5SB5xJ6FnoxY.INSTANCE, this.mListener));
        }
    }

    /* access modifiers changed from: package-private */
    public void setPendingUiCleanAndForceProcessStateUpTo(int newState) {
        WindowProcessListener windowProcessListener = this.mListener;
        if (windowProcessListener != null) {
            this.mAtm.mH.sendMessage(PooledLambda.obtainMessage($$Lambda$LI60v4Y5Me6khV12IZzEQtSx7A.INSTANCE, windowProcessListener, Integer.valueOf(newState)));
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isRemoved() {
        WindowProcessListener windowProcessListener = this.mListener;
        if (windowProcessListener == null) {
            return false;
        }
        return windowProcessListener.isRemoved();
    }

    private boolean shouldSetProfileProc() {
        return this.mAtm.mProfileApp != null && this.mAtm.mProfileApp.equals(this.mName) && (this.mAtm.mProfileProc == null || this.mAtm.mProfileProc == this);
    }

    /* access modifiers changed from: package-private */
    public ProfilerInfo createProfilerInfoIfNeeded() {
        ProfilerInfo currentProfilerInfo = this.mAtm.mProfilerInfo;
        if (currentProfilerInfo == null || currentProfilerInfo.profileFile == null || !shouldSetProfileProc()) {
            return null;
        }
        if (currentProfilerInfo.profileFd != null) {
            try {
                currentProfilerInfo.profileFd = currentProfilerInfo.profileFd.dup();
            } catch (IOException e) {
                currentProfilerInfo.closeFd();
            }
        }
        return new ProfilerInfo(currentProfilerInfo);
    }

    /* access modifiers changed from: package-private */
    public void onStartActivity(int topProcessState, ActivityInfo info) {
        if (this.mListener != null) {
            String packageName = null;
            if ((info.flags & 1) == 0 || !PackageManagerService.PLATFORM_PACKAGE_NAME.equals(info.packageName)) {
                packageName = info.packageName;
            }
            this.mAtm.mH.sendMessageAtFrontOfQueue(PooledLambda.obtainMessage($$Lambda$VY87MmFWaCLMkNa2qHGaPrThyrI.INSTANCE, this.mListener, Integer.valueOf(topProcessState), Boolean.valueOf(shouldSetProfileProc()), packageName, Long.valueOf(info.applicationInfo.longVersionCode)));
        }
    }

    public void appDied() {
        WindowProcessListener windowProcessListener = this.mListener;
        if (windowProcessListener != null) {
            this.mAtm.mH.sendMessage(PooledLambda.obtainMessage($$Lambda$MGgYXq0deCsjjGP28PM6ahiI2U.INSTANCE, windowProcessListener));
        }
    }

    /* access modifiers changed from: package-private */
    public void registerDisplayConfigurationListenerLocked(ActivityDisplay activityDisplay) {
        if (activityDisplay != null) {
            unregisterDisplayConfigurationListenerLocked();
            this.mDisplayId = activityDisplay.mDisplayId;
            activityDisplay.registerConfigurationChangeListener(this);
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void unregisterDisplayConfigurationListenerLocked() {
        if (this.mDisplayId != -1) {
            ActivityDisplay activityDisplay = this.mAtm.mRootActivityContainer.getActivityDisplay(this.mDisplayId);
            if (activityDisplay != null) {
                activityDisplay.unregisterConfigurationChangeListener(this);
            }
            this.mDisplayId = -1;
        }
    }

    public void onConfigurationChanged(Configuration newGlobalConfig) {
        super.onConfigurationChanged(newGlobalConfig);
        updateConfiguration();
    }

    public void onRequestedOverrideConfigurationChanged(Configuration newOverrideConfig) {
        super.onRequestedOverrideConfigurationChanged(newOverrideConfig);
        updateConfiguration();
    }

    private void updateConfiguration() {
        Configuration config = getConfiguration();
        if (this.mLastReportedConfiguration.diff(config) != 0) {
            try {
                if (this.mThread != null) {
                    config.seq = this.mAtm.increaseConfigurationSeqLocked();
                    this.mAtm.getLifecycleManager().scheduleTransaction(this.mThread, ConfigurationChangeItem.obtain(config));
                    setLastReportedConfiguration(config);
                }
            } catch (Exception e) {
                Slog.e("ActivityTaskManager", "Failed to schedule configuration change", e);
            }
        }
    }

    private void setLastReportedConfiguration(Configuration config) {
        this.mLastReportedConfiguration.setTo(config);
    }

    /* access modifiers changed from: package-private */
    public Configuration getLastReportedConfiguration() {
        return this.mLastReportedConfiguration;
    }

    public long getCpuTime() {
        WindowProcessListener windowProcessListener = this.mListener;
        if (windowProcessListener != null) {
            return windowProcessListener.getCpuTime();
        }
        return 0;
    }

    /* access modifiers changed from: package-private */
    public void addRecentTask(TaskRecord task) {
        this.mRecentTasks.add(task);
    }

    /* access modifiers changed from: package-private */
    public void removeRecentTask(TaskRecord task) {
        this.mRecentTasks.remove(task);
    }

    public boolean hasRecentTasks() {
        boolean z;
        synchronized (this.mAtm.mGlobalLockWithoutBoost) {
            z = !this.mRecentTasks.isEmpty();
        }
        return z;
    }

    /* access modifiers changed from: package-private */
    public void clearRecentTasks() {
        for (int i = this.mRecentTasks.size() - 1; i >= 0; i--) {
            this.mRecentTasks.get(i).clearRootProcess();
        }
        this.mRecentTasks.clear();
    }

    public void appEarlyNotResponding(String annotation, Runnable killAppCallback) {
        synchronized (this.mAtm.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                if (this.mAtm.mController == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                } else if (this.mAtm.mController.appEarlyNotResponding(this.mName, this.mPid, annotation) < 0 && this.mPid != ActivityManagerService.MY_PID) {
                    killAppCallback.run();
                }
            } catch (RemoteException e) {
                this.mAtm.mController = null;
                Watchdog.getInstance().setActivityController((IActivityController) null);
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0030, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0033, code lost:
        if (r0 == null) goto L_0x003a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0035, code lost:
        r0.run();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0039, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x003a, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean appNotResponding(java.lang.String r7, java.lang.Runnable r8, java.lang.Runnable r9) {
        /*
            r6 = this;
            r0 = 0
            com.android.server.wm.ActivityTaskManagerService r1 = r6.mAtm
            com.android.server.wm.WindowManagerGlobalLock r1 = r1.mGlobalLock
            monitor-enter(r1)
            com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x004d }
            com.android.server.wm.ActivityTaskManagerService r2 = r6.mAtm     // Catch:{ all -> 0x004d }
            android.app.IActivityController r2 = r2.mController     // Catch:{ all -> 0x004d }
            r3 = 0
            if (r2 != 0) goto L_0x0015
            monitor-exit(r1)     // Catch:{ all -> 0x004d }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return r3
        L_0x0015:
            com.android.server.wm.ActivityTaskManagerService r2 = r6.mAtm     // Catch:{ RemoteException -> 0x003b }
            android.app.IActivityController r2 = r2.mController     // Catch:{ RemoteException -> 0x003b }
            java.lang.String r4 = r6.mName     // Catch:{ RemoteException -> 0x003b }
            int r5 = r6.mPid     // Catch:{ RemoteException -> 0x003b }
            int r2 = r2.appNotResponding(r4, r5, r7)     // Catch:{ RemoteException -> 0x003b }
            if (r2 == 0) goto L_0x002e
            if (r2 >= 0) goto L_0x002d
            int r4 = r6.mPid     // Catch:{ RemoteException -> 0x003b }
            int r5 = com.android.server.am.ActivityManagerService.MY_PID     // Catch:{ RemoteException -> 0x003b }
            if (r4 == r5) goto L_0x002d
            r0 = r8
            goto L_0x002e
        L_0x002d:
            r0 = r9
        L_0x002e:
            monitor-exit(r1)     // Catch:{ all -> 0x004d }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            if (r0 == 0) goto L_0x003a
            r0.run()
            r1 = 1
            return r1
        L_0x003a:
            return r3
        L_0x003b:
            r2 = move-exception
            com.android.server.wm.ActivityTaskManagerService r4 = r6.mAtm     // Catch:{ all -> 0x004d }
            r5 = 0
            r4.mController = r5     // Catch:{ all -> 0x004d }
            com.android.server.Watchdog r4 = com.android.server.Watchdog.getInstance()     // Catch:{ all -> 0x004d }
            r4.setActivityController(r5)     // Catch:{ all -> 0x004d }
            monitor-exit(r1)     // Catch:{ all -> 0x004d }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return r3
        L_0x004d:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x004d }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowProcessController.appNotResponding(java.lang.String, java.lang.Runnable, java.lang.Runnable):boolean");
    }

    public void onTopProcChanged() {
        synchronized (this.mAtm.mGlobalLockWithoutBoost) {
            this.mAtm.mVrController.onTopProcChangedLocked(this);
        }
    }

    public boolean isHomeProcess() {
        boolean z;
        synchronized (this.mAtm.mGlobalLockWithoutBoost) {
            z = this == this.mAtm.mHomeProcess;
        }
        return z;
    }

    public boolean isPreviousProcess() {
        boolean z;
        synchronized (this.mAtm.mGlobalLockWithoutBoost) {
            z = this == this.mAtm.mPreviousProcess;
        }
        return z;
    }

    public String toString() {
        Object obj = this.mOwner;
        if (obj != null) {
            return obj.toString();
        }
        return null;
    }

    public void dump(PrintWriter pw, String prefix) {
        synchronized (this.mAtm.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                if (this.mActivities.size() > 0) {
                    pw.print(prefix);
                    pw.println("Activities:");
                    for (int i = 0; i < this.mActivities.size(); i++) {
                        pw.print(prefix);
                        pw.print("  - ");
                        pw.println(this.mActivities.get(i));
                    }
                }
                if (this.mRecentTasks.size() > 0) {
                    pw.println(prefix + "Recent Tasks:");
                    for (int i2 = 0; i2 < this.mRecentTasks.size(); i2++) {
                        pw.println(prefix + "  - " + this.mRecentTasks.get(i2));
                    }
                }
                if (this.mVrThreadTid != 0) {
                    pw.print(prefix);
                    pw.print("mVrThreadTid=");
                    pw.println(this.mVrThreadTid);
                }
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        pw.println(prefix + " Configuration=" + getConfiguration());
        pw.println(prefix + " OverrideConfiguration=" + getRequestedOverrideConfiguration());
        pw.println(prefix + " mLastReportedConfiguration=" + this.mLastReportedConfiguration);
    }

    /* access modifiers changed from: package-private */
    public void writeToProto(ProtoOutputStream proto, long fieldId) {
        WindowProcessListener windowProcessListener = this.mListener;
        if (windowProcessListener != null) {
            windowProcessListener.writeToProto(proto, fieldId);
        }
    }
}
