package com.android.server.wm;

import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.AppGlobals;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.ParceledListSlice;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.BoostFramework;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.MotionEvent;
import android.view.WindowManagerPolicyConstants;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.server.pm.DumpState;
import com.android.server.wm.RecentTasks;
import com.google.android.collect.Sets;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

class RecentTasks {
    private static final int DEFAULT_INITIAL_CAPACITY = 5;
    private static final long FREEZE_TASK_LIST_TIMEOUT_MS = TimeUnit.SECONDS.toMillis(5);
    private static final ActivityInfo NO_ACTIVITY_INFO_TOKEN = new ActivityInfo();
    private static final ApplicationInfo NO_APPLICATION_INFO_TOKEN = new ApplicationInfo();
    private static final String TAG = "ActivityTaskManager";
    private static final String TAG_RECENTS = "ActivityTaskManager";
    private static final String TAG_TASKS = "ActivityTaskManager";
    private static final Comparator<TaskRecord> TASK_ID_COMPARATOR = $$Lambda$RecentTasks$KPkDUQ9KJvmXlmV8HHAucQJJdQ.INSTANCE;
    private long mActiveTasksSessionDurationMs;
    private final ArrayList<Callbacks> mCallbacks = new ArrayList<>();
    /* access modifiers changed from: private */
    public boolean mFreezeTaskListReordering;
    private long mFreezeTaskListTimeoutMs = FREEZE_TASK_LIST_TIMEOUT_MS;
    private int mGlobalMaxNumTasks;
    private boolean mHasVisibleRecentTasks;
    private final WindowManagerPolicyConstants.PointerEventListener mListener = new WindowManagerPolicyConstants.PointerEventListener() {
        public void onPointerEvent(MotionEvent ev) {
            if (RecentTasks.this.mFreezeTaskListReordering && ev.getAction() == 0) {
                RecentTasks.this.mService.mH.post(PooledLambda.obtainRunnable(new Consumer(ev.getDisplayId(), (int) ev.getX(), (int) ev.getY()) {
                    private final /* synthetic */ int f$1;
                    private final /* synthetic */ int f$2;
                    private final /* synthetic */ int f$3;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                    }

                    public final void accept(Object obj) {
                        RecentTasks.AnonymousClass1.this.lambda$onPointerEvent$0$RecentTasks$1(this.f$1, this.f$2, this.f$3, obj);
                    }
                }, (Object) null).recycleOnUse());
            }
        }

        public /* synthetic */ void lambda$onPointerEvent$0$RecentTasks$1(int displayId, int x, int y, Object nonArg) {
            synchronized (RecentTasks.this.mService.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    if (RecentTasks.this.mService.mRootActivityContainer.getActivityDisplay(displayId).mDisplayContent.pointWithinAppWindow(x, y)) {
                        ActivityStack stack = RecentTasks.this.mService.getTopDisplayFocusedStack();
                        RecentTasks.this.resetFreezeTaskListReordering(stack != null ? stack.topTask() : null);
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
    };
    private int mMaxNumVisibleTasks;
    private int mMinNumVisibleTasks;
    private final SparseArray<SparseBooleanArray> mPersistedTaskIds = new SparseArray<>(5);
    private ComponentName mRecentsComponent = null;
    private int mRecentsUid = -1;
    private final Runnable mResetFreezeTaskListOnTimeoutRunnable = new Runnable() {
        public final void run() {
            RecentTasks.this.resetFreezeTaskListReorderingOnTimeout();
        }
    };
    /* access modifiers changed from: private */
    public final ActivityTaskManagerService mService;
    private final ActivityStackSupervisor mSupervisor;
    private final TaskPersister mTaskPersister;
    private final ArrayList<TaskRecord> mTasks = new ArrayList<>();
    private final HashMap<ComponentName, ActivityInfo> mTmpAvailActCache = new HashMap<>();
    private final HashMap<String, ApplicationInfo> mTmpAvailAppCache = new HashMap<>();
    private final SparseBooleanArray mTmpQuietProfileUserIds = new SparseBooleanArray();
    private final ArrayList<TaskRecord> mTmpRecents = new ArrayList<>();
    private final SparseBooleanArray mUsersWithRecentsLoaded = new SparseBooleanArray(5);
    private final BoostFramework mUxPerf = new BoostFramework();

    interface Callbacks {
        void onRecentTaskAdded(TaskRecord taskRecord);

        void onRecentTaskRemoved(TaskRecord taskRecord, boolean z, boolean z2);
    }

    static /* synthetic */ int lambda$static$0(TaskRecord lhs, TaskRecord rhs) {
        return rhs.taskId - lhs.taskId;
    }

    @VisibleForTesting
    RecentTasks(ActivityTaskManagerService service, TaskPersister taskPersister) {
        this.mService = service;
        this.mSupervisor = this.mService.mStackSupervisor;
        this.mTaskPersister = taskPersister;
        this.mGlobalMaxNumTasks = ActivityTaskManager.getMaxRecentTasksStatic();
        this.mHasVisibleRecentTasks = true;
    }

    RecentTasks(ActivityTaskManagerService service, ActivityStackSupervisor stackSupervisor) {
        File systemDir = Environment.getDataSystemDirectory();
        Resources res = service.mContext.getResources();
        this.mService = service;
        this.mSupervisor = this.mService.mStackSupervisor;
        this.mTaskPersister = new TaskPersister(systemDir, stackSupervisor, service, this, stackSupervisor.mPersisterQueue);
        this.mGlobalMaxNumTasks = ActivityTaskManager.getMaxRecentTasksStatic();
        this.mHasVisibleRecentTasks = res.getBoolean(17891467);
        loadParametersFromResources(res);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setParameters(int minNumVisibleTasks, int maxNumVisibleTasks, long activeSessionDurationMs) {
        this.mMinNumVisibleTasks = minNumVisibleTasks;
        this.mMaxNumVisibleTasks = maxNumVisibleTasks;
        this.mActiveTasksSessionDurationMs = activeSessionDurationMs;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setGlobalMaxNumTasks(int globalMaxNumTasks) {
        this.mGlobalMaxNumTasks = globalMaxNumTasks;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setFreezeTaskListTimeout(long timeoutMs) {
        this.mFreezeTaskListTimeoutMs = timeoutMs;
    }

    /* access modifiers changed from: package-private */
    public WindowManagerPolicyConstants.PointerEventListener getInputListener() {
        return this.mListener;
    }

    /* access modifiers changed from: package-private */
    public void setFreezeTaskListReordering() {
        this.mFreezeTaskListReordering = true;
        this.mService.mH.removeCallbacks(this.mResetFreezeTaskListOnTimeoutRunnable);
        this.mService.mH.postDelayed(this.mResetFreezeTaskListOnTimeoutRunnable, this.mFreezeTaskListTimeoutMs);
    }

    /* access modifiers changed from: package-private */
    public void resetFreezeTaskListReordering(TaskRecord topTask) {
        if (this.mFreezeTaskListReordering) {
            this.mFreezeTaskListReordering = false;
            this.mService.mH.removeCallbacks(this.mResetFreezeTaskListOnTimeoutRunnable);
            if (topTask != null) {
                this.mTasks.remove(topTask);
                this.mTasks.add(0, topTask);
            }
            trimInactiveRecentTasks();
            this.mService.getTaskChangeNotificationController().notifyTaskStackChanged();
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void resetFreezeTaskListReorderingOnTimeout() {
        TaskRecord topTask;
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityStack focusedStack = this.mService.getTopDisplayFocusedStack();
                if (focusedStack != null) {
                    topTask = focusedStack.topTask();
                } else {
                    topTask = null;
                }
                resetFreezeTaskListReordering(topTask);
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
    @VisibleForTesting
    public boolean isFreezeTaskListReorderingSet() {
        return this.mFreezeTaskListReordering;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void loadParametersFromResources(Resources res) {
        long j;
        if (ActivityManager.isLowRamDeviceStatic()) {
            this.mMinNumVisibleTasks = res.getInteger(17694843);
            this.mMaxNumVisibleTasks = res.getInteger(17694834);
        } else if (SystemProperties.getBoolean("ro.recents.grid", false)) {
            this.mMinNumVisibleTasks = res.getInteger(17694842);
            this.mMaxNumVisibleTasks = res.getInteger(17694833);
        } else {
            this.mMinNumVisibleTasks = res.getInteger(17694841);
            this.mMaxNumVisibleTasks = res.getInteger(17694832);
        }
        int sessionDurationHrs = res.getInteger(17694729);
        if (sessionDurationHrs > 0) {
            j = TimeUnit.HOURS.toMillis((long) sessionDurationHrs);
        } else {
            j = -1;
        }
        this.mActiveTasksSessionDurationMs = j;
    }

    /* access modifiers changed from: package-private */
    public void loadRecentsComponent(Resources res) {
        ComponentName cn;
        String rawRecentsComponent = res.getString(17039793);
        if (!TextUtils.isEmpty(rawRecentsComponent) && (cn = ComponentName.unflattenFromString(rawRecentsComponent)) != null) {
            try {
                ApplicationInfo appInfo = AppGlobals.getPackageManager().getApplicationInfo(cn.getPackageName(), 0, this.mService.mContext.getUserId());
                if (appInfo != null) {
                    this.mRecentsUid = appInfo.uid;
                    this.mRecentsComponent = cn;
                }
            } catch (RemoteException e) {
                Slog.w("ActivityTaskManager", "Could not load application info for recents component: " + cn);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isCallerRecents(int callingUid) {
        return UserHandle.isSameApp(callingUid, this.mRecentsUid);
    }

    /* access modifiers changed from: package-private */
    public boolean isRecentsComponent(ComponentName cn, int uid) {
        return cn.equals(this.mRecentsComponent) && UserHandle.isSameApp(uid, this.mRecentsUid);
    }

    /* access modifiers changed from: package-private */
    public boolean isRecentsComponentHomeActivity(int userId) {
        ComponentName defaultHomeActivity = this.mService.getPackageManagerInternalLocked().getDefaultHomeActivity(userId);
        return (defaultHomeActivity == null || this.mRecentsComponent == null || !defaultHomeActivity.getPackageName().equals(this.mRecentsComponent.getPackageName())) ? false : true;
    }

    /* access modifiers changed from: package-private */
    public ComponentName getRecentsComponent() {
        return this.mRecentsComponent;
    }

    /* access modifiers changed from: package-private */
    public int getRecentsComponentUid() {
        return this.mRecentsUid;
    }

    /* access modifiers changed from: package-private */
    public void registerCallback(Callbacks callback) {
        this.mCallbacks.add(callback);
    }

    /* access modifiers changed from: package-private */
    public void unregisterCallback(Callbacks callback) {
        this.mCallbacks.remove(callback);
    }

    private void notifyTaskAdded(TaskRecord task) {
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            this.mCallbacks.get(i).onRecentTaskAdded(task);
        }
    }

    private void notifyTaskRemoved(TaskRecord task, boolean wasTrimmed, boolean killProcess) {
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            this.mCallbacks.get(i).onRecentTaskRemoved(task, wasTrimmed, killProcess);
        }
    }

    /* access modifiers changed from: package-private */
    public void loadUserRecentsLocked(int userId) {
        if (!this.mUsersWithRecentsLoaded.get(userId)) {
            loadPersistedTaskIdsForUserLocked(userId);
            SparseBooleanArray preaddedTasks = new SparseBooleanArray();
            Iterator<TaskRecord> it = this.mTasks.iterator();
            while (it.hasNext()) {
                TaskRecord task = it.next();
                if (task.userId == userId && shouldPersistTaskLocked(task)) {
                    preaddedTasks.put(task.taskId, true);
                }
            }
            Slog.i("ActivityTaskManager", "Loading recents for user " + userId + " into memory.");
            this.mTasks.addAll(this.mTaskPersister.restoreTasksForUserLocked(userId, preaddedTasks));
            cleanupLocked(userId);
            this.mUsersWithRecentsLoaded.put(userId, true);
            if (preaddedTasks.size() > 0) {
                syncPersistentTaskIdsLocked();
            }
        }
    }

    private void loadPersistedTaskIdsForUserLocked(int userId) {
        if (this.mPersistedTaskIds.get(userId) == null) {
            this.mPersistedTaskIds.put(userId, this.mTaskPersister.loadPersistedTaskIdsForUser(userId));
            Slog.i("ActivityTaskManager", "Loaded persisted task ids for user " + userId);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean containsTaskId(int taskId, int userId) {
        loadPersistedTaskIdsForUserLocked(userId);
        return this.mPersistedTaskIds.get(userId).get(taskId);
    }

    /* access modifiers changed from: package-private */
    public SparseBooleanArray getTaskIdsForUser(int userId) {
        loadPersistedTaskIdsForUserLocked(userId);
        return this.mPersistedTaskIds.get(userId);
    }

    /* access modifiers changed from: package-private */
    public void notifyTaskPersisterLocked(TaskRecord task, boolean flush) {
        ActivityStack stack = task != null ? task.getStack() : null;
        if (stack == null || !stack.isHomeOrRecentsStack()) {
            syncPersistentTaskIdsLocked();
            this.mTaskPersister.wakeup(task, flush);
        }
    }

    private void syncPersistentTaskIdsLocked() {
        for (int i = this.mPersistedTaskIds.size() - 1; i >= 0; i--) {
            if (this.mUsersWithRecentsLoaded.get(this.mPersistedTaskIds.keyAt(i))) {
                this.mPersistedTaskIds.valueAt(i).clear();
            }
        }
        for (int i2 = this.mTasks.size() - 1; i2 >= 0; i2--) {
            TaskRecord task = this.mTasks.get(i2);
            if (shouldPersistTaskLocked(task)) {
                if (this.mPersistedTaskIds.get(task.userId) == null) {
                    Slog.wtf("ActivityTaskManager", "No task ids found for userId " + task.userId + ". task=" + task + " mPersistedTaskIds=" + this.mPersistedTaskIds);
                    this.mPersistedTaskIds.put(task.userId, new SparseBooleanArray());
                }
                this.mPersistedTaskIds.get(task.userId).put(task.taskId, true);
            }
        }
    }

    private static boolean shouldPersistTaskLocked(TaskRecord task) {
        ActivityStack stack = task.getStack();
        return task.isPersistable && (stack == null || !stack.isHomeOrRecentsStack());
    }

    /* access modifiers changed from: package-private */
    public void onSystemReadyLocked() {
        loadRecentsComponent(this.mService.mContext.getResources());
        this.mTasks.clear();
    }

    /* access modifiers changed from: package-private */
    public Bitmap getTaskDescriptionIcon(String path) {
        return this.mTaskPersister.getTaskDescriptionIcon(path);
    }

    /* access modifiers changed from: package-private */
    public void saveImage(Bitmap image, String path) {
        this.mTaskPersister.saveImage(image, path);
    }

    /* access modifiers changed from: package-private */
    public void flush() {
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                syncPersistentTaskIdsLocked();
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        this.mTaskPersister.flush();
    }

    /* access modifiers changed from: package-private */
    public int[] usersWithRecentsLoadedLocked() {
        int[] usersWithRecentsLoaded = new int[this.mUsersWithRecentsLoaded.size()];
        int len = 0;
        for (int i = 0; i < usersWithRecentsLoaded.length; i++) {
            int userId = this.mUsersWithRecentsLoaded.keyAt(i);
            if (this.mUsersWithRecentsLoaded.valueAt(i)) {
                usersWithRecentsLoaded[len] = userId;
                len++;
            }
        }
        if (len < usersWithRecentsLoaded.length) {
            return Arrays.copyOf(usersWithRecentsLoaded, len);
        }
        return usersWithRecentsLoaded;
    }

    /* access modifiers changed from: package-private */
    public void unloadUserDataFromMemoryLocked(int userId) {
        if (this.mUsersWithRecentsLoaded.get(userId)) {
            Slog.i("ActivityTaskManager", "Unloading recents for user " + userId + " from memory.");
            this.mUsersWithRecentsLoaded.delete(userId);
            removeTasksForUserLocked(userId);
        }
        this.mPersistedTaskIds.delete(userId);
        this.mTaskPersister.unloadUserDataFromMemory(userId);
    }

    private void removeTasksForUserLocked(int userId) {
        if (userId <= 0) {
            Slog.i("ActivityTaskManager", "Can't remove recent task on user " + userId);
            return;
        }
        for (int i = this.mTasks.size() - 1; i >= 0; i--) {
            TaskRecord tr = this.mTasks.get(i);
            if (tr.userId == userId) {
                remove(tr);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void onPackagesSuspendedChanged(String[] packages, boolean suspended, int userId) {
        Set<String> packageNames = Sets.newHashSet(packages);
        for (int i = this.mTasks.size() - 1; i >= 0; i--) {
            TaskRecord tr = this.mTasks.get(i);
            if (tr.realActivity != null && packageNames.contains(tr.realActivity.getPackageName()) && tr.userId == userId && tr.realActivitySuspended != suspended) {
                tr.realActivitySuspended = suspended;
                if (suspended) {
                    this.mSupervisor.removeTaskByIdLocked(tr.taskId, false, true, "suspended-package");
                }
                notifyTaskPersisterLocked(tr, false);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void onLockTaskModeStateChanged(int lockTaskModeState, int userId) {
        if (lockTaskModeState == 1) {
            for (int i = this.mTasks.size() - 1; i >= 0; i--) {
                TaskRecord tr = this.mTasks.get(i);
                if (tr.userId == userId && !this.mService.getLockTaskController().isTaskWhitelisted(tr)) {
                    remove(tr);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void removeTasksByPackageName(String packageName, int userId) {
        for (int i = this.mTasks.size() - 1; i >= 0; i--) {
            TaskRecord tr = this.mTasks.get(i);
            String taskPackageName = tr.getBaseIntent().getComponent().getPackageName();
            if (tr.userId == userId && taskPackageName.equals(packageName)) {
                this.mSupervisor.removeTaskByIdLocked(tr.taskId, true, true, "remove-package-task");
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void removeAllVisibleTasks(int userId) {
        Set<Integer> profileIds = getProfileIds(userId);
        for (int i = this.mTasks.size() - 1; i >= 0; i--) {
            TaskRecord tr = this.mTasks.get(i);
            if (profileIds.contains(Integer.valueOf(tr.userId)) && isVisibleRecentTask(tr)) {
                this.mTasks.remove(i);
                notifyTaskRemoved(tr, true, true);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void cleanupDisabledPackageTasksLocked(String packageName, Set<String> filterByClasses, int userId) {
        for (int i = this.mTasks.size() - 1; i >= 0; i--) {
            TaskRecord tr = this.mTasks.get(i);
            if (userId == -1 || tr.userId == userId) {
                ComponentName cn = tr.intent != null ? tr.intent.getComponent() : null;
                if (cn != null && cn.getPackageName().equals(packageName) && (filterByClasses == null || filterByClasses.contains(cn.getClassName()))) {
                    this.mSupervisor.removeTaskByIdLocked(tr.taskId, false, true, "disabled-package");
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void cleanupLocked(int userId) {
        int recentsCount = this.mTasks.size();
        if (recentsCount != 0) {
            this.mTmpAvailActCache.clear();
            this.mTmpAvailAppCache.clear();
            IPackageManager pm = AppGlobals.getPackageManager();
            for (int i = recentsCount - 1; i >= 0; i--) {
                TaskRecord task = this.mTasks.get(i);
                if (userId == -1 || task.userId == userId) {
                    if (task.autoRemoveRecents && task.getTopActivity() == null) {
                        remove(task);
                        Slog.w("ActivityTaskManager", "Removing auto-remove without activity: " + task);
                    } else if (task.realActivity != null) {
                        ActivityInfo ai = this.mTmpAvailActCache.get(task.realActivity);
                        if (ai == null) {
                            try {
                                ai = pm.getActivityInfo(task.realActivity, 268436480, userId);
                                if (ai == null) {
                                    ai = NO_ACTIVITY_INFO_TOKEN;
                                }
                                this.mTmpAvailActCache.put(task.realActivity, ai);
                            } catch (RemoteException e) {
                            }
                        }
                        if (ai == NO_ACTIVITY_INFO_TOKEN) {
                            ApplicationInfo app = this.mTmpAvailAppCache.get(task.realActivity.getPackageName());
                            if (app == null) {
                                try {
                                    app = pm.getApplicationInfo(task.realActivity.getPackageName(), 8192, userId);
                                    if (app == null) {
                                        app = NO_APPLICATION_INFO_TOKEN;
                                    }
                                    this.mTmpAvailAppCache.put(task.realActivity.getPackageName(), app);
                                } catch (RemoteException e2) {
                                }
                            }
                            if (app == NO_APPLICATION_INFO_TOKEN || (8388608 & app.flags) == 0) {
                                remove(task);
                                Slog.w("ActivityTaskManager", "Removing no longer valid recent: " + task);
                            } else {
                                task.isAvailable = false;
                            }
                        } else if (!ai.enabled || !ai.applicationInfo.enabled || (ai.applicationInfo.flags & DumpState.DUMP_VOLUMES) == 0) {
                            task.isAvailable = false;
                        } else {
                            task.isAvailable = true;
                        }
                    }
                }
            }
            int i2 = 0;
            int recentsCount2 = this.mTasks.size();
            while (i2 < recentsCount2) {
                i2 = processNextAffiliateChainLocked(i2);
            }
        }
    }

    private boolean canAddTaskWithoutTrim(TaskRecord task) {
        return findRemoveIndexForAddTask(task) == -1;
    }

    /* access modifiers changed from: package-private */
    public ArrayList<IBinder> getAppTasksList(int callingUid, String callingPackage) {
        Intent intent;
        ArrayList<IBinder> list = new ArrayList<>();
        int size = this.mTasks.size();
        for (int i = 0; i < size; i++) {
            TaskRecord tr = this.mTasks.get(i);
            if (tr.effectiveUid == callingUid && (intent = tr.getBaseIntent()) != null && callingPackage.equals(intent.getComponent().getPackageName())) {
                list.add(new AppTaskImpl(this.mService, tr.taskId, callingUid).asBinder());
            }
        }
        return list;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public Set<Integer> getProfileIds(int userId) {
        Set<Integer> userIds = new ArraySet<>();
        List<UserInfo> profiles = this.mService.getUserManager().getProfiles(userId, false);
        for (int i = profiles.size() - 1; i >= 0; i--) {
            userIds.add(Integer.valueOf(profiles.get(i).id));
        }
        return userIds;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public UserInfo getUserInfo(int userId) {
        return this.mService.getUserManager().getUserInfo(userId);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public int[] getCurrentProfileIds() {
        return this.mService.mAmInternal.getCurrentProfileIds();
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean isUserRunning(int userId, int flags) {
        return this.mService.mAmInternal.isUserRunning(userId, flags);
    }

    /* access modifiers changed from: package-private */
    public ParceledListSlice<ActivityManager.RecentTaskInfo> getRecentTasks(int maxNum, int flags, boolean getTasksAllowed, boolean getDetailedTasks, int userId, int callingUid) {
        return new ParceledListSlice<>(getRecentTasksImpl(maxNum, flags, getTasksAllowed, getDetailedTasks, userId, callingUid));
    }

    private ArrayList<ActivityManager.RecentTaskInfo> getRecentTasksImpl(int maxNum, int flags, boolean getTasksAllowed, boolean getDetailedTasks, int userId, int callingUid) {
        int i = userId;
        boolean withExcluded = (flags & 1) != 0;
        if (!isUserRunning(i, 4)) {
            Slog.i("ActivityTaskManager", "user " + i + " is still locked. Cannot load recents");
            return new ArrayList<>();
        }
        loadUserRecentsLocked(i);
        Set<Integer> includedUsers = getProfileIds(i);
        includedUsers.add(Integer.valueOf(userId));
        ArrayList<ActivityManager.RecentTaskInfo> res = new ArrayList<>();
        int size = this.mTasks.size();
        int numVisibleTasks = 0;
        for (int i2 = 0; i2 < size; i2++) {
            TaskRecord tr = this.mTasks.get(i2);
            if (isVisibleRecentTask(tr)) {
                numVisibleTasks++;
                if (!isInVisibleRange(tr, i2, numVisibleTasks, withExcluded)) {
                    int i3 = maxNum;
                    int i4 = callingUid;
                } else if (res.size() >= maxNum) {
                    int i5 = callingUid;
                } else if (!includedUsers.contains(Integer.valueOf(tr.userId))) {
                    int i6 = callingUid;
                } else if (tr.realActivitySuspended) {
                    int i7 = callingUid;
                } else {
                    if (getTasksAllowed) {
                        int i8 = callingUid;
                    } else if (tr.isActivityTypeHome()) {
                        int i9 = callingUid;
                    } else if (tr.effectiveUid != callingUid) {
                    }
                    if ((!tr.autoRemoveRecents || tr.getTopActivity() != null) && (((flags & 2) == 0 || tr.isAvailable) && tr.mUserSetupComplete)) {
                        ActivityManager.RecentTaskInfo rti = createRecentTaskInfo(tr);
                        if (!getDetailedTasks) {
                            rti.baseIntent.replaceExtras((Bundle) null);
                        }
                        res.add(rti);
                    }
                }
            } else {
                int i10 = maxNum;
                int i11 = callingUid;
            }
        }
        int i12 = maxNum;
        int i13 = callingUid;
        return res;
    }

    /* access modifiers changed from: package-private */
    public void getPersistableTaskIds(ArraySet<Integer> persistentTaskIds) {
        int size = this.mTasks.size();
        for (int i = 0; i < size; i++) {
            TaskRecord task = this.mTasks.get(i);
            ActivityStack stack = task.getStack();
            if ((task.isPersistable || task.inRecents) && (stack == null || !stack.isHomeOrRecentsStack())) {
                persistentTaskIds.add(Integer.valueOf(task.taskId));
            }
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public ArrayList<TaskRecord> getRawTasks() {
        return this.mTasks;
    }

    /* access modifiers changed from: package-private */
    public SparseBooleanArray getRecentTaskIds() {
        SparseBooleanArray res = new SparseBooleanArray();
        int size = this.mTasks.size();
        int numVisibleTasks = 0;
        for (int i = 0; i < size; i++) {
            TaskRecord tr = this.mTasks.get(i);
            if (isVisibleRecentTask(tr)) {
                numVisibleTasks++;
                if (isInVisibleRange(tr, i, numVisibleTasks, false)) {
                    res.put(tr.taskId, true);
                }
            }
        }
        return res;
    }

    /* access modifiers changed from: package-private */
    public TaskRecord getTask(int id) {
        int recentsCount = this.mTasks.size();
        for (int i = 0; i < recentsCount; i++) {
            TaskRecord tr = this.mTasks.get(i);
            if (tr.taskId == id) {
                return tr;
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public void add(TaskRecord task) {
        int taskIndex;
        boolean isAffiliated = (task.mAffiliatedTaskId == task.taskId && task.mNextAffiliateTaskId == -1 && task.mPrevAffiliateTaskId == -1) ? false : true;
        int recentsCount = this.mTasks.size();
        if (task.voiceSession == null) {
            if (!isAffiliated && recentsCount > 0 && this.mTasks.get(0) == task) {
                return;
            }
            if (!isAffiliated || recentsCount <= 0 || !task.inRecents || task.mAffiliatedTaskId != this.mTasks.get(0).mAffiliatedTaskId) {
                boolean needAffiliationFix = false;
                if (task.inRecents) {
                    int taskIndex2 = this.mTasks.indexOf(task);
                    if (taskIndex2 < 0) {
                        Slog.wtf("ActivityTaskManager", "Task with inRecent not in recents: " + task);
                        needAffiliationFix = true;
                    } else if (!isAffiliated) {
                        if (!this.mFreezeTaskListReordering) {
                            this.mTasks.remove(taskIndex2);
                            this.mTasks.add(0, task);
                        }
                        notifyTaskPersisterLocked(task, false);
                        return;
                    }
                }
                removeForAddTask(task);
                task.inRecents = true;
                if (!isAffiliated || needAffiliationFix) {
                    this.mTasks.add(0, task);
                    notifyTaskAdded(task);
                } else if (isAffiliated) {
                    TaskRecord other = task.mNextAffiliate;
                    if (other == null) {
                        other = task.mPrevAffiliate;
                    }
                    if (other != null) {
                        int otherIndex = this.mTasks.indexOf(other);
                        if (otherIndex >= 0) {
                            if (other == task.mNextAffiliate) {
                                taskIndex = otherIndex + 1;
                            } else {
                                taskIndex = otherIndex;
                            }
                            this.mTasks.add(taskIndex, task);
                            notifyTaskAdded(task);
                            if (!moveAffiliatedTasksToFront(task, taskIndex)) {
                                needAffiliationFix = true;
                            } else {
                                return;
                            }
                        } else {
                            needAffiliationFix = true;
                        }
                    } else {
                        needAffiliationFix = true;
                    }
                }
                if (needAffiliationFix) {
                    cleanupLocked(task.userId);
                }
                trimInactiveRecentTasks();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean addToBottom(TaskRecord task) {
        if (!canAddTaskWithoutTrim(task)) {
            return false;
        }
        add(task);
        return true;
    }

    /* access modifiers changed from: package-private */
    public void remove(TaskRecord task) {
        this.mTasks.remove(task);
        notifyTaskRemoved(task, false, false);
        if (task != null) {
            String taskPkgName = task.getBaseIntent().getComponent().getPackageName();
            BoostFramework boostFramework = this.mUxPerf;
            if (boostFramework != null) {
                boostFramework.perfUXEngine_events(4, 0, taskPkgName, 0);
            }
        }
    }

    private void trimInactiveRecentTasks() {
        if (!this.mFreezeTaskListReordering) {
            for (int recentsCount = this.mTasks.size(); recentsCount > this.mGlobalMaxNumTasks; recentsCount--) {
                notifyTaskRemoved(this.mTasks.remove(recentsCount - 1), true, false);
            }
            int[] profileUserIds = getCurrentProfileIds();
            this.mTmpQuietProfileUserIds.clear();
            for (int userId : profileUserIds) {
                UserInfo userInfo = getUserInfo(userId);
                if (userInfo != null && userInfo.isManagedProfile() && userInfo.isQuietModeEnabled()) {
                    this.mTmpQuietProfileUserIds.put(userId, true);
                }
            }
            int numVisibleTasks = 0;
            int i = 0;
            while (i < this.mTasks.size()) {
                TaskRecord task = this.mTasks.get(i);
                if (isActiveRecentTask(task, this.mTmpQuietProfileUserIds)) {
                    if (!this.mHasVisibleRecentTasks) {
                        i++;
                    } else if (!isVisibleRecentTask(task)) {
                        i++;
                    } else {
                        numVisibleTasks++;
                        if (isInVisibleRange(task, i, numVisibleTasks, false) || !isTrimmable(task)) {
                            i++;
                        }
                    }
                }
                this.mTasks.remove(task);
                notifyTaskRemoved(task, true, false);
                notifyTaskPersisterLocked(task, false);
            }
        }
    }

    private boolean isActiveRecentTask(TaskRecord task, SparseBooleanArray quietProfileUserIds) {
        TaskRecord affiliatedTask;
        if (quietProfileUserIds.get(task.userId)) {
            return false;
        }
        if (task.mAffiliatedTaskId == -1 || task.mAffiliatedTaskId == task.taskId || (affiliatedTask = getTask(task.mAffiliatedTaskId)) == null || isActiveRecentTask(affiliatedTask, quietProfileUserIds)) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean isVisibleRecentTask(TaskRecord task) {
        int windowingMode;
        ActivityDisplay display;
        ActivityStack stack;
        int activityType = task.getActivityType();
        if (activityType == 2 || activityType == 3) {
            return false;
        }
        if ((activityType == 4 && (task.getBaseIntent().getFlags() & DumpState.DUMP_VOLUMES) == 8388608) || (windowingMode = task.getWindowingMode()) == 2) {
            return false;
        }
        if (windowingMode == 3 && (stack = task.getStack()) != null && stack.topTask() == task) {
            return false;
        }
        ActivityStack stack2 = task.getStack();
        if ((stack2 == null || (display = stack2.getDisplay()) == null || !display.isSingleTaskInstance()) && task != this.mService.getLockTaskController().getRootTask()) {
            return true;
        }
        return false;
    }

    private boolean isInVisibleRange(TaskRecord task, int taskIndex, int numVisibleTasks, boolean skipExcludedCheck) {
        if (!skipExcludedCheck) {
            if ((task.getBaseIntent().getFlags() & DumpState.DUMP_VOLUMES) == 8388608) {
                if (taskIndex == 0) {
                    return true;
                }
                return false;
            }
        }
        int i = this.mMinNumVisibleTasks;
        if (i >= 0 && numVisibleTasks <= i) {
            return true;
        }
        int i2 = this.mMaxNumVisibleTasks;
        if (i2 < 0) {
            return this.mActiveTasksSessionDurationMs > 0 && task.getInactiveDuration() <= this.mActiveTasksSessionDurationMs;
        }
        if (numVisibleTasks <= i2) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean isTrimmable(TaskRecord task) {
        ActivityStack stack = task.getStack();
        if (stack == null) {
            return true;
        }
        if (stack.mDisplayId != 0) {
            return false;
        }
        ActivityDisplay display = stack.getDisplay();
        if (display.getIndexOf(stack) < display.getIndexOf(display.getHomeStack())) {
            return true;
        }
        return false;
    }

    private void removeForAddTask(TaskRecord task) {
        int removeIndex = findRemoveIndexForAddTask(task);
        if (removeIndex != -1) {
            TaskRecord removedTask = this.mTasks.remove(removeIndex);
            if (removedTask != task) {
                notifyTaskRemoved(removedTask, false, false);
            }
            notifyTaskPersisterLocked(removedTask, false);
        }
    }

    private int findRemoveIndexForAddTask(TaskRecord task) {
        TaskRecord taskRecord = task;
        if (this.mFreezeTaskListReordering) {
            return -1;
        }
        int recentsCount = this.mTasks.size();
        Intent intent = taskRecord.intent;
        boolean z = true;
        boolean document = intent != null && intent.isDocument();
        int maxRecents = taskRecord.maxRecents - 1;
        int i = 0;
        while (i < recentsCount) {
            TaskRecord tr = this.mTasks.get(i);
            if (taskRecord != tr) {
                if ((task.getStack() == null || tr.getStack() == null || hasCompatibleActivityTypeAndWindowingMode(taskRecord, tr)) && taskRecord.userId == tr.userId) {
                    Intent trIntent = tr.intent;
                    boolean sameAffinity = (taskRecord.affinity == null || !taskRecord.affinity.equals(tr.affinity)) ? false : z;
                    boolean sameIntent = (intent == null || !intent.filterEquals(trIntent)) ? false : z;
                    boolean multiTasksAllowed = false;
                    int flags = intent.getFlags();
                    if (!((flags & 268959744) == 0 || (flags & 134217728) == 0)) {
                        multiTasksAllowed = true;
                    }
                    boolean trIsDocument = (trIntent == null || !trIntent.isDocument()) ? false : z;
                    boolean bothDocuments = (!document || !trIsDocument) ? false : z;
                    if (sameAffinity || sameIntent || bothDocuments) {
                        if (bothDocuments) {
                            if (!((taskRecord.realActivity == null || tr.realActivity == null || !taskRecord.realActivity.equals(tr.realActivity)) ? false : true)) {
                                continue;
                            } else if (maxRecents > 0) {
                                maxRecents--;
                                if (sameIntent && !multiTasksAllowed) {
                                }
                            }
                        } else if (!document && !trIsDocument) {
                        }
                    }
                }
                i++;
                z = true;
            }
            return i;
        }
        return -1;
    }

    private int processNextAffiliateChainLocked(int start) {
        TaskRecord startTask = this.mTasks.get(start);
        int affiliateId = startTask.mAffiliatedTaskId;
        if (startTask.taskId == affiliateId && startTask.mPrevAffiliate == null && startTask.mNextAffiliate == null) {
            startTask.inRecents = true;
            return start + 1;
        }
        this.mTmpRecents.clear();
        for (int i = this.mTasks.size() - 1; i >= start; i--) {
            TaskRecord task = this.mTasks.get(i);
            if (task.mAffiliatedTaskId == affiliateId) {
                this.mTasks.remove(i);
                this.mTmpRecents.add(task);
            }
        }
        Collections.sort(this.mTmpRecents, TASK_ID_COMPARATOR);
        TaskRecord first = this.mTmpRecents.get(0);
        first.inRecents = true;
        if (first.mNextAffiliate != null) {
            Slog.w("ActivityTaskManager", "Link error 1 first.next=" + first.mNextAffiliate);
            first.setNextAffiliate((TaskRecord) null);
            notifyTaskPersisterLocked(first, false);
        }
        int tmpSize = this.mTmpRecents.size();
        for (int i2 = 0; i2 < tmpSize - 1; i2++) {
            TaskRecord next = this.mTmpRecents.get(i2);
            TaskRecord prev = this.mTmpRecents.get(i2 + 1);
            if (next.mPrevAffiliate != prev) {
                Slog.w("ActivityTaskManager", "Link error 2 next=" + next + " prev=" + next.mPrevAffiliate + " setting prev=" + prev);
                next.setPrevAffiliate(prev);
                notifyTaskPersisterLocked(next, false);
            }
            if (prev.mNextAffiliate != next) {
                Slog.w("ActivityTaskManager", "Link error 3 prev=" + prev + " next=" + prev.mNextAffiliate + " setting next=" + next);
                prev.setNextAffiliate(next);
                notifyTaskPersisterLocked(prev, false);
            }
            prev.inRecents = true;
        }
        TaskRecord last = this.mTmpRecents.get(tmpSize - 1);
        if (last.mPrevAffiliate != null) {
            Slog.w("ActivityTaskManager", "Link error 4 last.prev=" + last.mPrevAffiliate);
            last.setPrevAffiliate((TaskRecord) null);
            notifyTaskPersisterLocked(last, false);
        }
        this.mTasks.addAll(start, this.mTmpRecents);
        this.mTmpRecents.clear();
        return start + tmpSize;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:40:0x012d, code lost:
        android.util.Slog.wtf("ActivityTaskManager", "Bad chain @" + r7 + ": middle task " + r14 + " @" + r7 + " has bad next affiliate " + r14.mNextAffiliate + " id " + r14.mNextAffiliateTaskId + ", expected " + r10);
        r6 = false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean moveAffiliatedTasksToFront(com.android.server.wm.TaskRecord r18, int r19) {
        /*
            r17 = this;
            r0 = r17
            r1 = r18
            r2 = r19
            java.util.ArrayList<com.android.server.wm.TaskRecord> r3 = r0.mTasks
            int r3 = r3.size()
            r4 = r18
            r5 = r19
        L_0x0010:
            com.android.server.wm.TaskRecord r6 = r4.mNextAffiliate
            if (r6 == 0) goto L_0x001b
            if (r5 <= 0) goto L_0x001b
            com.android.server.wm.TaskRecord r4 = r4.mNextAffiliate
            int r5 = r5 + -1
            goto L_0x0010
        L_0x001b:
            int r6 = r4.mAffiliatedTaskId
            int r7 = r1.mAffiliatedTaskId
            if (r6 != r7) goto L_0x0023
            r6 = 1
            goto L_0x0024
        L_0x0023:
            r6 = 0
        L_0x0024:
            r7 = r5
            r10 = r4
        L_0x0026:
            java.lang.String r11 = " @"
            java.lang.String r12 = "Bad chain @"
            java.lang.String r13 = "ActivityTaskManager"
            if (r7 >= r3) goto L_0x016b
            java.util.ArrayList<com.android.server.wm.TaskRecord> r14 = r0.mTasks
            java.lang.Object r14 = r14.get(r7)
            com.android.server.wm.TaskRecord r14 = (com.android.server.wm.TaskRecord) r14
            r15 = -1
            if (r14 != r4) goto L_0x0060
            com.android.server.wm.TaskRecord r8 = r14.mNextAffiliate
            if (r8 != 0) goto L_0x0041
            int r8 = r14.mNextAffiliateTaskId
            if (r8 == r15) goto L_0x006e
        L_0x0041:
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r12)
            r8.append(r7)
            java.lang.String r15 = ": first task has next affiliate: "
            r8.append(r15)
            r8.append(r10)
            java.lang.String r8 = r8.toString()
            android.util.Slog.wtf(r13, r8)
            r6 = 0
            r16 = r4
            goto L_0x016d
        L_0x0060:
            com.android.server.wm.TaskRecord r8 = r14.mNextAffiliate
            if (r8 != r10) goto L_0x012b
            int r8 = r14.mNextAffiliateTaskId
            int r9 = r10.taskId
            if (r8 == r9) goto L_0x006e
            r16 = r4
            goto L_0x012d
        L_0x006e:
            int r8 = r14.mPrevAffiliateTaskId
            java.lang.String r9 = ": last task "
            r16 = r4
            java.lang.String r4 = " has previous affiliate "
            if (r8 != r15) goto L_0x009f
            com.android.server.wm.TaskRecord r8 = r14.mPrevAffiliate
            if (r8 == 0) goto L_0x016d
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r12)
            r8.append(r7)
            r8.append(r9)
            r8.append(r14)
            r8.append(r4)
            com.android.server.wm.TaskRecord r4 = r14.mPrevAffiliate
            r8.append(r4)
            java.lang.String r4 = r8.toString()
            android.util.Slog.wtf(r13, r4)
            r6 = 0
            goto L_0x016d
        L_0x009f:
            com.android.server.wm.TaskRecord r8 = r14.mPrevAffiliate
            java.lang.String r15 = ": task "
            if (r8 != 0) goto L_0x00d2
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r12)
            r8.append(r7)
            r8.append(r15)
            r8.append(r14)
            r8.append(r4)
            com.android.server.wm.TaskRecord r4 = r14.mPrevAffiliate
            r8.append(r4)
            java.lang.String r4 = " but should be id "
            r8.append(r4)
            com.android.server.wm.TaskRecord r4 = r14.mPrevAffiliate
            r8.append(r4)
            java.lang.String r4 = r8.toString()
            android.util.Slog.wtf(r13, r4)
            r6 = 0
            goto L_0x016d
        L_0x00d2:
            int r4 = r14.mAffiliatedTaskId
            int r8 = r1.mAffiliatedTaskId
            if (r4 == r8) goto L_0x0106
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r12)
            r4.append(r7)
            r4.append(r15)
            r4.append(r14)
            java.lang.String r8 = " has affiliated id "
            r4.append(r8)
            int r8 = r14.mAffiliatedTaskId
            r4.append(r8)
            java.lang.String r8 = " but should be "
            r4.append(r8)
            int r8 = r1.mAffiliatedTaskId
            r4.append(r8)
            java.lang.String r4 = r4.toString()
            android.util.Slog.wtf(r13, r4)
            r6 = 0
            goto L_0x016d
        L_0x0106:
            r10 = r14
            int r7 = r7 + 1
            if (r7 < r3) goto L_0x0127
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r8 = "Bad chain ran off index "
            r4.append(r8)
            r4.append(r7)
            r4.append(r9)
            r4.append(r10)
            java.lang.String r4 = r4.toString()
            android.util.Slog.wtf(r13, r4)
            r6 = 0
            goto L_0x016d
        L_0x0127:
            r4 = r16
            goto L_0x0026
        L_0x012b:
            r16 = r4
        L_0x012d:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r12)
            r4.append(r7)
            java.lang.String r8 = ": middle task "
            r4.append(r8)
            r4.append(r14)
            r4.append(r11)
            r4.append(r7)
            java.lang.String r8 = " has bad next affiliate "
            r4.append(r8)
            com.android.server.wm.TaskRecord r8 = r14.mNextAffiliate
            r4.append(r8)
            java.lang.String r8 = " id "
            r4.append(r8)
            int r8 = r14.mNextAffiliateTaskId
            r4.append(r8)
            java.lang.String r8 = ", expected "
            r4.append(r8)
            r4.append(r10)
            java.lang.String r4 = r4.toString()
            android.util.Slog.wtf(r13, r4)
            r6 = 0
            goto L_0x016d
        L_0x016b:
            r16 = r4
        L_0x016d:
            if (r6 == 0) goto L_0x0192
            if (r7 >= r2) goto L_0x0192
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r12)
            r4.append(r7)
            java.lang.String r8 = ": did not extend to task "
            r4.append(r8)
            r4.append(r1)
            r4.append(r11)
            r4.append(r2)
            java.lang.String r4 = r4.toString()
            android.util.Slog.wtf(r13, r4)
            r6 = 0
        L_0x0192:
            if (r6 == 0) goto L_0x01ab
            r4 = r5
        L_0x0195:
            if (r4 > r7) goto L_0x01a9
            java.util.ArrayList<com.android.server.wm.TaskRecord> r8 = r0.mTasks
            java.lang.Object r8 = r8.remove(r4)
            com.android.server.wm.TaskRecord r8 = (com.android.server.wm.TaskRecord) r8
            java.util.ArrayList<com.android.server.wm.TaskRecord> r9 = r0.mTasks
            int r11 = r4 - r5
            r9.add(r11, r8)
            int r4 = r4 + 1
            goto L_0x0195
        L_0x01a9:
            r4 = 1
            return r4
        L_0x01ab:
            r4 = 0
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.RecentTasks.moveAffiliatedTasksToFront(com.android.server.wm.TaskRecord, int):boolean");
    }

    /* access modifiers changed from: package-private */
    public void dump(PrintWriter pw, boolean dumpAll, String dumpPackage) {
        PrintWriter printWriter = pw;
        String str = dumpPackage;
        printWriter.println("ACTIVITY MANAGER RECENT TASKS (dumpsys activity recents)");
        printWriter.println("mRecentsUid=" + this.mRecentsUid);
        printWriter.println("mRecentsComponent=" + this.mRecentsComponent);
        printWriter.println("mFreezeTaskListReordering=" + this.mFreezeTaskListReordering);
        printWriter.println("mFreezeTaskListReorderingPendingTimeout=" + this.mService.mH.hasCallbacks(this.mResetFreezeTaskListOnTimeoutRunnable));
        if (!this.mTasks.isEmpty()) {
            boolean printedHeader = false;
            int size = this.mTasks.size();
            boolean printedAnything = false;
            for (int i = 0; i < size; i++) {
                TaskRecord tr = this.mTasks.get(i);
                if (str == null || (tr.realActivity != null && str.equals(tr.realActivity.getPackageName()))) {
                    if (!printedHeader) {
                        printWriter.println("  Recent tasks:");
                        printedHeader = true;
                        printedAnything = true;
                    }
                    printWriter.print("  * Recent #");
                    printWriter.print(i);
                    printWriter.print(": ");
                    printWriter.println(tr);
                    if (dumpAll) {
                        tr.dump(printWriter, "    ");
                    }
                }
            }
            if (this.mHasVisibleRecentTasks) {
                boolean printedHeader2 = false;
                ArrayList<ActivityManager.RecentTaskInfo> tasks = getRecentTasksImpl(Integer.MAX_VALUE, 0, true, false, this.mService.getCurrentUserId(), 1000);
                for (int i2 = 0; i2 < tasks.size(); i2++) {
                    ActivityManager.RecentTaskInfo taskInfo = tasks.get(i2);
                    if (!printedHeader2) {
                        if (printedAnything) {
                            pw.println();
                        }
                        printWriter.println("  Visible recent tasks (most recent first):");
                        printedHeader2 = true;
                        printedAnything = true;
                    }
                    printWriter.print("  * RecentTaskInfo #");
                    printWriter.print(i2);
                    printWriter.print(": ");
                    taskInfo.dump(printWriter, "    ");
                }
            }
            if (!printedAnything) {
                printWriter.println("  (nothing)");
            }
        }
    }

    /* access modifiers changed from: package-private */
    public ActivityManager.RecentTaskInfo createRecentTaskInfo(TaskRecord tr) {
        ActivityManager.RecentTaskInfo rti = new ActivityManager.RecentTaskInfo();
        tr.fillTaskInfo(rti);
        rti.id = rti.isRunning ? rti.taskId : -1;
        rti.persistentId = rti.taskId;
        return rti;
    }

    private boolean hasCompatibleActivityTypeAndWindowingMode(TaskRecord t1, TaskRecord t2) {
        int activityType = t1.getActivityType();
        int windowingMode = t1.getWindowingMode();
        boolean isUndefinedType = activityType == 0;
        boolean isUndefinedMode = windowingMode == 0;
        int otherActivityType = t2.getActivityType();
        int otherWindowingMode = t2.getWindowingMode();
        boolean isOtherUndefinedType = otherActivityType == 0;
        boolean isOtherUndefinedMode = otherWindowingMode == 0;
        boolean isCompatibleType = activityType == otherActivityType || isUndefinedType || isOtherUndefinedType;
        boolean isCompatibleMode = windowingMode == otherWindowingMode || isUndefinedMode || isOtherUndefinedMode;
        boolean isCompatibleModeWithRunningActivity = (isCompatibleMode || !isCompatibleType) ? isCompatibleMode : t2.topRunningActivityLocked() == null;
        if (!isCompatibleType || !isCompatibleModeWithRunningActivity) {
            return false;
        }
        return true;
    }
}
