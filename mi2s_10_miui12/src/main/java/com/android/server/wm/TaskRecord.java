package com.android.server.wm;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.ActivityTaskManager;
import android.app.TaskInfo;
import android.app.WindowConfiguration;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.Trace;
import android.os.UserHandle;
import android.provider.Settings;
import android.service.voice.IVoiceInteractionSession;
import android.util.EventLog;
import android.util.MiuiMultiWindowUtils;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import android.view.DisplayInfo;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.IVoiceInteractor;
import com.android.internal.util.XmlUtils;
import com.android.server.EventLogTags;
import com.android.server.am.ActivityManagerServiceInjector;
import com.android.server.pm.DumpState;
import com.android.server.wm.ActivityRecord;
import com.android.server.wm.ActivityStack;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Objects;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class TaskRecord extends ConfigurationContainer {
    private static final String ATTR_AFFINITY = "affinity";
    private static final String ATTR_ASKEDCOMPATMODE = "asked_compat_mode";
    private static final String ATTR_AUTOREMOVERECENTS = "auto_remove_recents";
    private static final String ATTR_CALLING_PACKAGE = "calling_package";
    private static final String ATTR_CALLING_UID = "calling_uid";
    private static final String ATTR_EFFECTIVE_UID = "effective_uid";
    private static final String ATTR_IS_AVAILABLE = "is_available";
    private static final String ATTR_LASTDESCRIPTION = "last_description";
    private static final String ATTR_LASTTIMEMOVED = "last_time_moved";
    private static final String ATTR_MIN_HEIGHT = "min_height";
    private static final String ATTR_MIN_WIDTH = "min_width";
    private static final String ATTR_NEVERRELINQUISH = "never_relinquish_identity";
    private static final String ATTR_NEXT_AFFILIATION = "next_affiliation";
    private static final String ATTR_NON_FULLSCREEN_BOUNDS = "non_fullscreen_bounds";
    private static final String ATTR_ORIGACTIVITY = "orig_activity";
    private static final String ATTR_PERSIST_TASK_VERSION = "persist_task_version";
    private static final String ATTR_PREV_AFFILIATION = "prev_affiliation";
    private static final String ATTR_REALACTIVITY = "real_activity";
    private static final String ATTR_REALACTIVITY_SUSPENDED = "real_activity_suspended";
    private static final String ATTR_RESIZE_MODE = "resize_mode";
    private static final String ATTR_ROOTHASRESET = "root_has_reset";
    private static final String ATTR_ROOT_AFFINITY = "root_affinity";
    private static final String ATTR_SUPPORTS_PICTURE_IN_PICTURE = "supports_picture_in_picture";
    private static final String ATTR_TASKID = "task_id";
    @Deprecated
    private static final String ATTR_TASKTYPE = "task_type";
    private static final String ATTR_TASK_AFFILIATION = "task_affiliation";
    private static final String ATTR_TASK_AFFILIATION_COLOR = "task_affiliation_color";
    private static final String ATTR_USERID = "user_id";
    private static final String ATTR_USER_SETUP_COMPLETE = "user_setup_complete";
    private static final int INVALID_MIN_SIZE = -1;
    static final int LOCK_TASK_AUTH_DONT_LOCK = 0;
    static final int LOCK_TASK_AUTH_LAUNCHABLE = 2;
    static final int LOCK_TASK_AUTH_LAUNCHABLE_PRIV = 4;
    static final int LOCK_TASK_AUTH_PINNABLE = 1;
    static final int LOCK_TASK_AUTH_WHITELISTED = 3;
    private static final int PERSIST_TASK_VERSION = 1;
    static final int REPARENT_KEEP_STACK_AT_FRONT = 1;
    static final int REPARENT_LEAVE_STACK_IN_PLACE = 2;
    static final int REPARENT_MOVE_STACK_TO_FRONT = 0;
    private static final String TAG = "ActivityTaskManager";
    private static final String TAG_ACTIVITY = "activity";
    private static final String TAG_ADD_REMOVE = "ActivityTaskManager";
    private static final String TAG_AFFINITYINTENT = "affinity_intent";
    private static final String TAG_INTENT = "intent";
    private static final String TAG_LOCKTASK = "ActivityTaskManager";
    private static final String TAG_RECENTS = "ActivityTaskManager";
    private static final String TAG_TASKS = "ActivityTaskManager";
    private static TaskRecordFactory sTaskRecordFactory;
    String affinity;
    Intent affinityIntent;
    boolean askedCompatMode;
    boolean autoRemoveRecents;
    int effectiveUid;
    boolean hasBeenVisible;
    boolean inRecents;
    Intent intent;
    boolean isAvailable;
    boolean isPersistable = false;
    long lastActiveTime;
    CharSequence lastDescription;
    ActivityManager.TaskDescription lastTaskDescription = new ActivityManager.TaskDescription();
    final ArrayList<ActivityRecord> mActivities;
    int mAffiliatedTaskColor;
    int mAffiliatedTaskId;
    String mCallingPackage;
    int mCallingUid;
    final Rect mDisplayedBounds = new Rect();
    Rect mLastNonFullscreenBounds = null;
    long mLastTimeMoved = System.currentTimeMillis();
    int mLayerRank = -1;
    int mLockTaskAuth = 1;
    int mLockTaskUid = -1;
    int mMinHeight;
    int mMinWidth;
    private boolean mNeverRelinquishIdentity = true;
    TaskRecord mNextAffiliate;
    int mNextAffiliateTaskId = -1;
    TaskRecord mPrevAffiliate;
    int mPrevAffiliateTaskId = -1;
    int mResizeMode;
    final TaskActivitiesReport mReuseActivitiesReport = new TaskActivitiesReport();
    private boolean mReuseTask = false;
    private WindowProcessController mRootProcess;
    final ActivityTaskManagerService mService;
    private ActivityStack mStack;
    private boolean mSupportsPictureInPicture;
    Task mTask;
    private final Rect mTmpBounds = new Rect();
    private Configuration mTmpConfig = new Configuration();
    private final Rect mTmpInsets = new Rect();
    private final Rect mTmpNonDecorBounds = new Rect();
    private final Rect mTmpStableBounds = new Rect();
    boolean mUserSetupComplete;
    int maxRecents;
    int numFullscreen;
    ComponentName origActivity;
    ComponentName realActivity;
    boolean realActivitySuspended;
    String rootAffinity;
    boolean rootWasReset;
    String stringName;
    final int taskId;
    int userId;
    final IVoiceInteractor voiceInteractor;
    final IVoiceInteractionSession voiceSession;

    @Retention(RetentionPolicy.SOURCE)
    @interface ReparentMoveStackMode {
    }

    TaskRecord(ActivityTaskManagerService service, int _taskId, ActivityInfo info, Intent _intent, IVoiceInteractionSession _voiceSession, IVoiceInteractor _voiceInteractor) {
        ComponentName componentName;
        this.mService = service;
        this.userId = UserHandle.getUserId(info.applicationInfo.uid);
        this.taskId = _taskId;
        this.lastActiveTime = SystemClock.elapsedRealtime();
        this.mAffiliatedTaskId = _taskId;
        this.voiceSession = _voiceSession;
        this.voiceInteractor = _voiceInteractor;
        this.isAvailable = true;
        this.mActivities = new ArrayList<>();
        this.mCallingUid = info.applicationInfo.uid;
        this.mCallingPackage = info.packageName;
        setIntent(_intent, info);
        setMinDimensions(info);
        touchActiveTime();
        this.mService.getTaskChangeNotificationController().notifyTaskCreated(_taskId, this.realActivity);
        if (!supportsSplitScreenWindowingMode() && (componentName = this.realActivity) != null && ActivityManagerServiceInjector.inResizeWhiteList(componentName.getPackageName())) {
            this.mResizeMode = 4;
        }
    }

    TaskRecord(ActivityTaskManagerService service, int _taskId, ActivityInfo info, Intent _intent, ActivityManager.TaskDescription _taskDescription) {
        this.mService = service;
        this.userId = UserHandle.getUserId(info.applicationInfo.uid);
        this.taskId = _taskId;
        this.lastActiveTime = SystemClock.elapsedRealtime();
        this.mAffiliatedTaskId = _taskId;
        this.voiceSession = null;
        this.voiceInteractor = null;
        this.isAvailable = true;
        this.mActivities = new ArrayList<>();
        this.mCallingUid = info.applicationInfo.uid;
        this.mCallingPackage = info.packageName;
        setIntent(_intent, info);
        setMinDimensions(info);
        this.isPersistable = true;
        this.maxRecents = Math.min(Math.max(info.maxRecents, 1), ActivityTaskManager.getMaxAppRecentsLimitStatic());
        this.lastTaskDescription = _taskDescription;
        touchActiveTime();
        this.mService.getTaskChangeNotificationController().notifyTaskCreated(_taskId, this.realActivity);
    }

    TaskRecord(ActivityTaskManagerService service, int _taskId, Intent _intent, Intent _affinityIntent, String _affinity, String _rootAffinity, ComponentName _realActivity, ComponentName _origActivity, boolean _rootWasReset, boolean _autoRemoveRecents, boolean _askedCompatMode, int _userId, int _effectiveUid, String _lastDescription, ArrayList<ActivityRecord> activities, long lastTimeMoved, boolean neverRelinquishIdentity, ActivityManager.TaskDescription _lastTaskDescription, int taskAffiliation, int prevTaskId, int nextTaskId, int taskAffiliationColor, int callingUid, String callingPackage, int resizeMode, boolean supportsPictureInPicture, boolean _realActivitySuspended, boolean userSetupComplete, int minWidth, int minHeight, boolean _isAvailable) {
        int i = _taskId;
        this.mService = service;
        this.taskId = i;
        this.intent = _intent;
        this.affinityIntent = _affinityIntent;
        this.affinity = _affinity;
        this.rootAffinity = _rootAffinity;
        this.voiceSession = null;
        this.voiceInteractor = null;
        this.realActivity = _realActivity;
        this.realActivitySuspended = _realActivitySuspended;
        this.origActivity = _origActivity;
        this.rootWasReset = _rootWasReset;
        this.isAvailable = _isAvailable;
        this.autoRemoveRecents = _autoRemoveRecents;
        this.askedCompatMode = _askedCompatMode;
        this.userId = _userId;
        this.mUserSetupComplete = userSetupComplete;
        this.effectiveUid = _effectiveUid;
        this.lastActiveTime = SystemClock.elapsedRealtime();
        this.lastDescription = _lastDescription;
        this.mActivities = activities;
        this.mLastTimeMoved = lastTimeMoved;
        this.mNeverRelinquishIdentity = neverRelinquishIdentity;
        this.lastTaskDescription = _lastTaskDescription;
        this.mAffiliatedTaskId = taskAffiliation;
        this.mAffiliatedTaskColor = taskAffiliationColor;
        this.mPrevAffiliateTaskId = prevTaskId;
        this.mNextAffiliateTaskId = nextTaskId;
        this.mCallingUid = callingUid;
        this.mCallingPackage = callingPackage;
        this.mResizeMode = resizeMode;
        this.mSupportsPictureInPicture = supportsPictureInPicture;
        this.mMinWidth = minWidth;
        this.mMinHeight = minHeight;
        this.mService.getTaskChangeNotificationController().notifyTaskCreated(i, this.realActivity);
    }

    /* access modifiers changed from: package-private */
    public Task getTask() {
        return this.mTask;
    }

    /* access modifiers changed from: package-private */
    public void createTask(boolean onTop, boolean showForAllUsers) {
        if (this.mTask == null) {
            Rect updateOverrideConfigurationFromLaunchBounds = updateOverrideConfigurationFromLaunchBounds();
            TaskStack stack = getStack().getTaskStack();
            if (stack != null) {
                EventLog.writeEvent(EventLogTags.WM_TASK_CREATED, new Object[]{Integer.valueOf(this.taskId), Integer.valueOf(stack.mStackId)});
                this.mTask = new Task(this.taskId, stack, this.userId, this.mService.mWindowManager, this.mResizeMode, this.mSupportsPictureInPicture, this.lastTaskDescription, this);
                int position = onTop ? Integer.MAX_VALUE : Integer.MIN_VALUE;
                if (!this.mDisplayedBounds.isEmpty()) {
                    this.mTask.setOverrideDisplayedBounds(this.mDisplayedBounds);
                }
                stack.addTask(this.mTask, position, showForAllUsers, onTop);
                return;
            }
            throw new IllegalArgumentException("TaskRecord: invalid stack=" + this.mStack);
        }
        throw new IllegalArgumentException("mTask=" + this.mTask + " already created for task=" + this);
    }

    /* access modifiers changed from: package-private */
    public void setTask(Task task) {
        this.mTask = task;
    }

    /* access modifiers changed from: package-private */
    public void cleanUpResourcesForDestroy() {
        if (this.mActivities.isEmpty()) {
            saveLaunchingStateIfNeeded();
            boolean isVoiceSession = this.voiceSession != null;
            if (isVoiceSession) {
                try {
                    this.voiceSession.taskFinished(this.intent, this.taskId);
                } catch (RemoteException e) {
                }
            }
            if (autoRemoveFromRecents() || isVoiceSession) {
                this.mService.mStackSupervisor.mRecentTasks.remove(this);
            }
            removeWindowContainer();
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void removeWindowContainer() {
        this.mService.getLockTaskController().clearLockedTask(this);
        Task task = this.mTask;
        if (task != null) {
            task.removeIfPossible();
            this.mTask = null;
            if (!getWindowConfiguration().persistTaskBounds()) {
                updateOverrideConfiguration((Rect) null);
            }
            this.mService.getTaskChangeNotificationController().notifyTaskRemoved(this.taskId);
        }
    }

    public void onSnapshotChanged(ActivityManager.TaskSnapshot snapshot) {
        this.mService.getTaskChangeNotificationController().notifyTaskSnapshotChanged(this.taskId, snapshot);
    }

    /* access modifiers changed from: package-private */
    public void setResizeMode(int resizeMode) {
        if (this.mResizeMode != resizeMode) {
            this.mResizeMode = resizeMode;
            this.mTask.setResizeable(resizeMode);
            this.mService.mRootActivityContainer.ensureActivitiesVisible((ActivityRecord) null, 0, false);
            this.mService.mRootActivityContainer.resumeFocusedStacksTopActivities();
        }
    }

    /* access modifiers changed from: package-private */
    public void setTaskDockedResizing(boolean resizing) {
        if (this.mTask == null) {
            Slog.w(DisplayPolicy.TAG, "setTaskDockedResizing: taskId " + this.taskId + " not found.");
            return;
        }
        ActivityStack activityStack = this.mStack;
        if (activityStack != null && activityStack.inSplitScreenWindowingMode()) {
            this.mTask.setTaskDockedResizing(resizing);
        }
    }

    public void requestResize(Rect bounds, int resizeMode) {
        try {
            this.mService.resizeTask(this.taskId, bounds, resizeMode);
        } catch (IllegalArgumentException e) {
            Slog.w("ActivityTaskManager", "Note: WMS has a Freeform taskId, but AMS's taskId is different. taskId = " + this.taskId);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    /* access modifiers changed from: package-private */
    public boolean resize(Rect bounds, int resizeMode, boolean preserveWindow, boolean deferResume) {
        ActivityRecord r;
        this.mService.mWindowManager.deferSurfaceLayout();
        if (!isResizeable()) {
            Slog.w("ActivityTaskManager", "resizeTask: task " + this + " not resizeable.");
            this.mService.mWindowManager.continueSurfaceLayout();
            return true;
        }
        boolean forced = (resizeMode & 2) != 0;
        try {
            if (equivalentRequestedOverrideBounds(bounds) && !forced) {
                return true;
            }
            if (this.mTask == null) {
                updateOverrideConfiguration(bounds);
                if (!inFreeformWindowingMode()) {
                    this.mService.mStackSupervisor.restoreRecentTaskLocked(this, (ActivityOptions) null, false);
                }
                this.mService.mWindowManager.continueSurfaceLayout();
                return true;
            } else if (canResizeToBounds(bounds)) {
                Trace.traceBegin(64, "am.resizeTask_" + this.taskId);
                boolean kept = true;
                if (updateOverrideConfiguration(bounds) && (r = topRunningActivityLocked()) != null && !deferResume) {
                    kept = r.ensureActivityConfiguration(0, preserveWindow);
                    this.mService.mRootActivityContainer.ensureActivitiesVisible(r, 0, preserveWindow);
                    if (!kept) {
                        this.mService.mRootActivityContainer.resumeFocusedStacksTopActivities();
                    }
                }
                this.mTask.resize(kept, forced);
                saveLaunchingStateIfNeeded();
                Trace.traceEnd(64);
                this.mService.mWindowManager.continueSurfaceLayout();
                return kept;
            } else {
                throw new IllegalArgumentException("resizeTask: Can not resize task=" + this + " to bounds=" + bounds + " resizeMode=" + this.mResizeMode);
            }
        } finally {
            this.mService.mWindowManager.continueSurfaceLayout();
        }
    }

    /* access modifiers changed from: package-private */
    public void resizeWindowContainer() {
        this.mTask.resize(false, false);
    }

    /* access modifiers changed from: package-private */
    public void getWindowContainerBounds(Rect bounds) {
        Task task = this.mTask;
        if (task != null) {
            task.getBounds(bounds);
        } else {
            bounds.setEmpty();
        }
    }

    /* access modifiers changed from: package-private */
    public boolean reparent(ActivityStack preferredStack, boolean toTop, int moveStackMode, boolean animate, boolean deferResume, String reason) {
        return reparent(preferredStack, toTop ? Integer.MAX_VALUE : 0, moveStackMode, animate, deferResume, true, reason);
    }

    /* access modifiers changed from: package-private */
    public boolean reparent(ActivityStack preferredStack, boolean toTop, int moveStackMode, boolean animate, boolean deferResume, boolean schedulePictureInPictureModeChange, String reason) {
        return reparent(preferredStack, toTop ? Integer.MAX_VALUE : 0, moveStackMode, animate, deferResume, schedulePictureInPictureModeChange, reason);
    }

    /* access modifiers changed from: package-private */
    public boolean reparent(ActivityStack preferredStack, int position, int moveStackMode, boolean animate, boolean deferResume, String reason) {
        return reparent(preferredStack, position, moveStackMode, animate, deferResume, true, reason);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x015d A[Catch:{ all -> 0x014b, all -> 0x0167 }] */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x0163 A[Catch:{ all -> 0x014b, all -> 0x0167 }] */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x017d A[SYNTHETIC, Splitter:B:115:0x017d] */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x018a  */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x01b0 A[Catch:{ all -> 0x01a0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x01bd A[Catch:{ all -> 0x01a0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x01cc A[Catch:{ all -> 0x02a1 }] */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x01ce A[Catch:{ all -> 0x02a1 }] */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x01e2 A[SYNTHETIC, Splitter:B:139:0x01e2] */
    /* JADX WARNING: Removed duplicated region for block: B:145:0x01f6 A[Catch:{ all -> 0x01a0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x026a  */
    /* JADX WARNING: Removed duplicated region for block: B:187:0x0277  */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x027b  */
    /* JADX WARNING: Removed duplicated region for block: B:193:0x028b  */
    /* JADX WARNING: Removed duplicated region for block: B:196:0x029b A[ORIG_RETURN, RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:214:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x00a9  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00ab  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x00b2 A[SYNTHETIC, Splitter:B:51:0x00b2] */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x00cf A[SYNTHETIC, Splitter:B:60:0x00cf] */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0101  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x0105  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x010d A[Catch:{ all -> 0x02c3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x011c A[Catch:{ all -> 0x02c3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x011e A[Catch:{ all -> 0x02c3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x012d A[SYNTHETIC, Splitter:B:95:0x012d] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean reparent(com.android.server.wm.ActivityStack r28, int r29, int r30, boolean r31, boolean r32, boolean r33, java.lang.String r34) {
        /*
            r27 = this;
            r1 = r27
            r2 = r28
            r3 = r29
            r4 = r30
            r5 = r31
            r6 = r32
            r13 = r34
            com.android.server.wm.ActivityTaskManagerService r0 = r1.mService
            com.android.server.wm.ActivityStackSupervisor r14 = r0.mStackSupervisor
            com.android.server.wm.ActivityTaskManagerService r0 = r1.mService
            com.android.server.wm.RootActivityContainer r15 = r0.mRootActivityContainer
            com.android.server.wm.ActivityTaskManagerService r0 = r1.mService
            com.android.server.wm.WindowManagerService r12 = r0.mWindowManager
            com.android.server.wm.ActivityStack r9 = r27.getStack()
            r0 = 2147483647(0x7fffffff, float:NaN)
            r7 = 0
            if (r3 != r0) goto L_0x0026
            r10 = 1
            goto L_0x0027
        L_0x0026:
            r10 = r7
        L_0x0027:
            com.android.server.wm.ActivityStack r11 = r14.getReparentTargetStack(r1, r2, r10)
            if (r11 != r9) goto L_0x002e
            return r7
        L_0x002e:
            int r10 = r11.mDisplayId
            boolean r10 = r1.canBeLaunchedOnDisplay(r10)
            if (r10 != 0) goto L_0x0037
            return r7
        L_0x0037:
            if (r3 != r0) goto L_0x003b
            r0 = 1
            goto L_0x003c
        L_0x003b:
            r0 = r7
        L_0x003c:
            r16 = r0
            r10 = 0
            if (r16 == 0) goto L_0x0050
            com.android.server.wm.ActivityRecord r0 = r11.getResumedActivity()
            if (r0 == 0) goto L_0x0050
            com.android.server.wm.ActivityRecord r0 = r11.topRunningActivityLocked()
            if (r0 == 0) goto L_0x0050
            r11.startPausingLocked(r7, r7, r10, r7)
        L_0x0050:
            int r7 = r11.getWindowingMode()
            com.android.server.wm.ActivityRecord r8 = r27.getTopActivity()
            if (r8 == 0) goto L_0x0066
            int r0 = r27.getWindowingMode()
            boolean r0 = replaceWindowsOnTaskMove(r0, r7)
            if (r0 == 0) goto L_0x0066
            r0 = 1
            goto L_0x0067
        L_0x0066:
            r0 = 0
        L_0x0067:
            r18 = r0
            if (r18 == 0) goto L_0x0070
            android.view.IApplicationToken$Stub r0 = r8.appToken
            r12.setWillReplaceWindow(r0, r5)
        L_0x0070:
            r12.deferSurfaceLayout()
            r19 = 1
            com.android.server.wm.ActivityRecord r0 = r27.topRunningActivityLocked()     // Catch:{ all -> 0x02f8 }
            r20 = r0
            r2 = r20
            if (r2 == 0) goto L_0x009e
            boolean r0 = r15.isTopDisplayFocusedStack(r9)     // Catch:{ all -> 0x008d }
            if (r0 == 0) goto L_0x009e
            com.android.server.wm.ActivityRecord r0 = r27.topRunningActivityLocked()     // Catch:{ all -> 0x008d }
            if (r0 != r2) goto L_0x009e
            r0 = 1
            goto L_0x009f
        L_0x008d:
            r0 = move-exception
            r10 = r28
            r17 = r9
            r9 = r13
            r2 = r15
            r15 = r11
            r11 = r7
            r7 = r12
            r26 = r14
            r14 = r8
            r8 = r26
            goto L_0x0306
        L_0x009e:
            r0 = 0
        L_0x009f:
            r20 = r0
            if (r2 == 0) goto L_0x00ab
            com.android.server.wm.ActivityRecord r0 = r9.getResumedActivity()     // Catch:{ all -> 0x008d }
            if (r0 != r2) goto L_0x00ab
            r0 = 1
            goto L_0x00ac
        L_0x00ab:
            r0 = 0
        L_0x00ac:
            r21 = r15
            r15 = r10
            r10 = r0
            if (r2 == 0) goto L_0x00ca
            com.android.server.wm.ActivityRecord r0 = r9.mPausingActivity     // Catch:{ all -> 0x00b8 }
            if (r0 != r2) goto L_0x00ca
            r0 = 1
            goto L_0x00cb
        L_0x00b8:
            r0 = move-exception
            r10 = r28
            r17 = r9
            r15 = r11
            r9 = r13
            r2 = r21
            r11 = r7
            r7 = r12
            r26 = r14
            r14 = r8
            r8 = r26
            goto L_0x0306
        L_0x00ca:
            r0 = 0
        L_0x00cb:
            r15 = r11
            r11 = r0
            if (r2 == 0) goto L_0x00ee
            boolean r0 = r9.isTopStackOnDisplay()     // Catch:{ all -> 0x00dd }
            if (r0 == 0) goto L_0x00ee
            com.android.server.wm.ActivityRecord r0 = r9.topRunningActivityLocked()     // Catch:{ all -> 0x00dd }
            if (r0 != r2) goto L_0x00ee
            r0 = 1
            goto L_0x00ef
        L_0x00dd:
            r0 = move-exception
            r10 = r28
            r11 = r7
            r17 = r9
            r7 = r12
            r9 = r13
            r2 = r21
            r26 = r14
            r14 = r8
            r8 = r26
            goto L_0x0306
        L_0x00ee:
            r0 = 0
        L_0x00ef:
            r22 = r0
            r23 = r7
            r7 = 0
            int r0 = r15.getAdjustedPositionForTask(r1, r3, r7)     // Catch:{ all -> 0x02e7 }
            r3 = r0
            com.android.server.wm.Task r0 = r1.mTask     // Catch:{ all -> 0x02d4 }
            com.android.server.wm.TaskStack r7 = r15.getTaskStack()     // Catch:{ all -> 0x02d4 }
            if (r4 != 0) goto L_0x0105
            r24 = r8
            r8 = 1
            goto L_0x0108
        L_0x0105:
            r24 = r8
            r8 = 0
        L_0x0108:
            r0.reparent(r7, r3, r8)     // Catch:{ all -> 0x02c3 }
            if (r4 == 0) goto L_0x0117
            r7 = 1
            if (r4 != r7) goto L_0x0115
            if (r20 != 0) goto L_0x0117
            if (r22 == 0) goto L_0x0115
            goto L_0x0117
        L_0x0115:
            r0 = 0
            goto L_0x0118
        L_0x0117:
            r0 = 1
        L_0x0118:
            r25 = r0
            if (r25 == 0) goto L_0x011e
            r0 = 2
            goto L_0x011f
        L_0x011e:
            r0 = 1
        L_0x011f:
            r9.removeTask(r1, r13, r0)     // Catch:{ all -> 0x02c3 }
            r7 = 0
            r15.addTask(r1, r3, r7, r13)     // Catch:{ all -> 0x02c3 }
            int r0 = r9.getWindowingMode()     // Catch:{ all -> 0x02c3 }
            r8 = 5
            if (r0 == r8) goto L_0x015d
            int r0 = r15.getWindowingMode()     // Catch:{ all -> 0x014b }
            if (r0 != r8) goto L_0x0146
            com.android.server.wm.TaskStack r0 = r15.getTaskStack()     // Catch:{ all -> 0x014b }
            if (r0 == 0) goto L_0x0141
            r29 = r3
            r3 = 1
            r8 = 2
            r0.setMiuiConfigFlag(r8, r3)     // Catch:{ all -> 0x0167 }
            goto L_0x0161
        L_0x0141:
            r29 = r3
            r3 = 1
            r8 = 2
            goto L_0x0161
        L_0x0146:
            r29 = r3
            r3 = 1
            r8 = 2
            goto L_0x0161
        L_0x014b:
            r0 = move-exception
            r29 = r3
            r10 = r28
            r17 = r9
            r7 = r12
            r9 = r13
            r8 = r14
            r2 = r21
            r11 = r23
            r14 = r24
            goto L_0x0306
        L_0x015d:
            r29 = r3
            r3 = 1
            r8 = 2
        L_0x0161:
            if (r33 == 0) goto L_0x0179
            r14.scheduleUpdatePictureInPictureModeIfNeeded((com.android.server.wm.TaskRecord) r1, (com.android.server.wm.ActivityStack) r9)     // Catch:{ all -> 0x0167 }
            goto L_0x0179
        L_0x0167:
            r0 = move-exception
            r10 = r28
            r3 = r29
            r17 = r9
            r7 = r12
            r9 = r13
            r8 = r14
            r2 = r21
            r11 = r23
            r14 = r24
            goto L_0x0306
        L_0x0179:
            android.service.voice.IVoiceInteractionSession r0 = r1.voiceSession     // Catch:{ all -> 0x02b1 }
            if (r0 == 0) goto L_0x0188
            android.service.voice.IVoiceInteractionSession r0 = r1.voiceSession     // Catch:{ RemoteException -> 0x0187 }
            android.content.Intent r3 = r1.intent     // Catch:{ RemoteException -> 0x0187 }
            int r7 = r1.taskId     // Catch:{ RemoteException -> 0x0187 }
            r0.taskStarted(r3, r7)     // Catch:{ RemoteException -> 0x0187 }
            goto L_0x0188
        L_0x0187:
            r0 = move-exception
        L_0x0188:
            if (r2 == 0) goto L_0x01b0
            r3 = r23
            r23 = r14
            r14 = 0
            r7 = r15
            r14 = r24
            r13 = 1
            r8 = r2
            r17 = r9
            r9 = r25
            r24 = r12
            r12 = r34
            r7.moveToFrontAndResumeStateIfNeeded(r8, r9, r10, r11, r12)     // Catch:{ all -> 0x01a0 }
            goto L_0x01bb
        L_0x01a0:
            r0 = move-exception
            r10 = r28
            r9 = r34
        L_0x01a5:
            r11 = r3
            r2 = r21
            r8 = r23
            r7 = r24
            r3 = r29
            goto L_0x0306
        L_0x01b0:
            r17 = r9
            r3 = r23
            r13 = 1
            r23 = r14
            r14 = r24
            r24 = r12
        L_0x01bb:
            if (r5 != 0) goto L_0x01c6
            com.android.server.wm.ActivityTaskManagerService r0 = r1.mService     // Catch:{ all -> 0x01a0 }
            com.android.server.wm.ActivityStackSupervisor r0 = r0.mStackSupervisor     // Catch:{ all -> 0x01a0 }
            java.util.ArrayList<com.android.server.wm.ActivityRecord> r0 = r0.mNoAnimActivities     // Catch:{ all -> 0x01a0 }
            r0.add(r14)     // Catch:{ all -> 0x01a0 }
        L_0x01c6:
            r15.prepareFreezingTaskBounds()     // Catch:{ all -> 0x02a1 }
            r0 = 3
            if (r3 != r0) goto L_0x01ce
            r0 = r13
            goto L_0x01cf
        L_0x01ce:
            r0 = 0
        L_0x01cf:
            android.graphics.Rect r7 = r27.getRequestedOverrideBounds()     // Catch:{ all -> 0x02a1 }
            if (r3 == r13) goto L_0x01d8
            r8 = 4
            if (r3 != r8) goto L_0x01f6
        L_0x01d8:
            android.graphics.Rect r8 = r15.getRequestedOverrideBounds()     // Catch:{ all -> 0x02a1 }
            boolean r8 = java.util.Objects.equals(r7, r8)     // Catch:{ all -> 0x02a1 }
            if (r8 != 0) goto L_0x01f6
            android.graphics.Rect r8 = r15.getRequestedOverrideBounds()     // Catch:{ all -> 0x01a0 }
            if (r18 != 0) goto L_0x01ea
            r9 = r13
            goto L_0x01eb
        L_0x01ea:
            r9 = 0
        L_0x01eb:
            r12 = 0
            boolean r8 = r1.resize(r8, r12, r9, r6)     // Catch:{ all -> 0x01a0 }
            r19 = r8
            r9 = r34
            goto L_0x0264
        L_0x01f6:
            r8 = 5
            if (r3 != r8) goto L_0x022d
            com.android.server.wm.ActivityRecord r8 = r27.getTopActivity()     // Catch:{ all -> 0x01a0 }
            int r8 = r8.getOrientation()     // Catch:{ all -> 0x01a0 }
            boolean r8 = android.util.MiuiMultiWindowUtils.isOrientationLandscape(r8)     // Catch:{ all -> 0x01a0 }
            com.android.server.wm.ActivityTaskManagerService r9 = r1.mService     // Catch:{ all -> 0x01a0 }
            android.content.Context r9 = r9.mContext     // Catch:{ all -> 0x01a0 }
            boolean r12 = android.util.MiuiMultiWindowUtils.mIsMiniFreeformMode     // Catch:{ all -> 0x01a0 }
            r13 = 0
            android.graphics.Rect r9 = android.util.MiuiMultiWindowUtils.getFreeformRect(r9, r13, r13, r12, r8)     // Catch:{ all -> 0x01a0 }
            if (r9 != 0) goto L_0x0220
            com.android.server.wm.ActivityTaskManagerService r12 = r1.mService     // Catch:{ all -> 0x01a0 }
            com.android.server.wm.ActivityStackSupervisor r12 = r12.mStackSupervisor     // Catch:{ all -> 0x01a0 }
            com.android.server.wm.LaunchParamsController r12 = r12.getLaunchParamsController()     // Catch:{ all -> 0x01a0 }
            r13 = 0
            r12.layoutTask(r1, r13)     // Catch:{ all -> 0x01a0 }
            r9 = r7
        L_0x0220:
            if (r18 != 0) goto L_0x0224
            r12 = 1
            goto L_0x0225
        L_0x0224:
            r12 = 0
        L_0x0225:
            r13 = 2
            boolean r12 = r1.resize(r9, r13, r12, r6)     // Catch:{ all -> 0x01a0 }
            r19 = r12
            goto L_0x0233
        L_0x022d:
            r13 = 2
            if (r0 != 0) goto L_0x0236
            if (r3 != r13) goto L_0x0233
            goto L_0x0236
        L_0x0233:
            r9 = r34
            goto L_0x0264
        L_0x0236:
            if (r0 == 0) goto L_0x0251
            r13 = 1
            if (r4 != r13) goto L_0x024e
            com.android.server.wm.ActivityTaskManagerService r8 = r1.mService     // Catch:{ all -> 0x0247 }
            com.android.server.wm.ActivityStackSupervisor r8 = r8.mStackSupervisor     // Catch:{ all -> 0x0247 }
            r9 = r34
            r8.moveRecentsStackToFront(r9)     // Catch:{ all -> 0x0245 }
            goto L_0x0254
        L_0x0245:
            r0 = move-exception
            goto L_0x024a
        L_0x0247:
            r0 = move-exception
            r9 = r34
        L_0x024a:
            r10 = r28
            goto L_0x01a5
        L_0x024e:
            r9 = r34
            goto L_0x0254
        L_0x0251:
            r9 = r34
            r13 = 1
        L_0x0254:
            android.graphics.Rect r8 = r15.getRequestedOverrideBounds()     // Catch:{ all -> 0x029d }
            if (r18 != 0) goto L_0x025c
            r12 = r13
            goto L_0x025d
        L_0x025c:
            r12 = 0
        L_0x025d:
            r13 = 0
            boolean r8 = r1.resize(r8, r13, r12, r6)     // Catch:{ all -> 0x029d }
            r19 = r8
        L_0x0264:
            r24.continueSurfaceLayout()
            if (r18 == 0) goto L_0x0277
            android.view.IApplicationToken$Stub r0 = r14.appToken
            if (r19 != 0) goto L_0x0270
            r2 = 1
            goto L_0x0271
        L_0x0270:
            r2 = 0
        L_0x0271:
            r7 = r24
            r7.scheduleClearWillReplaceWindows(r0, r2)
            goto L_0x0279
        L_0x0277:
            r7 = r24
        L_0x0279:
            if (r6 != 0) goto L_0x028b
            if (r18 != 0) goto L_0x027f
            r0 = 1
            goto L_0x0280
        L_0x027f:
            r0 = 0
        L_0x0280:
            r2 = r21
            r8 = 0
            r13 = 0
            r2.ensureActivitiesVisible(r8, r13, r0)
            r2.resumeFocusedStacksTopActivities()
            goto L_0x028e
        L_0x028b:
            r2 = r21
            r13 = 0
        L_0x028e:
            int r0 = r28.getWindowingMode()
            r8 = r23
            r8.handleNonResizableTaskIfNeeded(r1, r0, r13, r15)
            r10 = r28
            if (r10 != r15) goto L_0x029c
            r13 = 1
        L_0x029c:
            return r13
        L_0x029d:
            r0 = move-exception
            r10 = r28
            goto L_0x02a6
        L_0x02a1:
            r0 = move-exception
            r10 = r28
            r9 = r34
        L_0x02a6:
            r2 = r21
            r8 = r23
            r7 = r24
            r11 = r3
            r3 = r29
            goto L_0x0306
        L_0x02b1:
            r0 = move-exception
            r10 = r28
            r17 = r9
            r7 = r12
            r9 = r13
            r8 = r14
            r2 = r21
            r14 = r24
            r3 = r29
            r11 = r23
            goto L_0x0306
        L_0x02c3:
            r0 = move-exception
            r10 = r28
            r29 = r3
            r17 = r9
            r7 = r12
            r9 = r13
            r8 = r14
            r2 = r21
            r14 = r24
            r11 = r23
            goto L_0x0306
        L_0x02d4:
            r0 = move-exception
            r10 = r28
            r29 = r3
            r17 = r9
            r7 = r12
            r9 = r13
            r2 = r21
            r26 = r14
            r14 = r8
            r8 = r26
            r11 = r23
            goto L_0x0306
        L_0x02e7:
            r0 = move-exception
            r10 = r28
            r17 = r9
            r7 = r12
            r9 = r13
            r2 = r21
            r11 = r23
            r26 = r14
            r14 = r8
            r8 = r26
            goto L_0x0306
        L_0x02f8:
            r0 = move-exception
            r10 = r2
            r17 = r9
            r9 = r13
            r2 = r15
            r15 = r11
            r11 = r7
            r7 = r12
            r26 = r14
            r14 = r8
            r8 = r26
        L_0x0306:
            r7.continueSurfaceLayout()
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.TaskRecord.reparent(com.android.server.wm.ActivityStack, int, int, boolean, boolean, boolean, java.lang.String):boolean");
    }

    private static boolean replaceWindowsOnTaskMove(int sourceWindowingMode, int targetWindowingMode) {
        return sourceWindowingMode == 5 || targetWindowingMode == 5;
    }

    /* access modifiers changed from: package-private */
    public void cancelWindowTransition() {
        Task task = this.mTask;
        if (task == null) {
            Slog.w(DisplayPolicy.TAG, "cancelWindowTransition: taskId " + this.taskId + " not found.");
            return;
        }
        task.cancelTaskWindowTransition();
    }

    /* access modifiers changed from: package-private */
    public ActivityManager.TaskSnapshot getSnapshot(boolean reducedResolution, boolean restoreFromDisk) {
        return this.mService.mWindowManager.getTaskSnapshot(this.taskId, this.userId, reducedResolution, restoreFromDisk);
    }

    /* access modifiers changed from: package-private */
    public void touchActiveTime() {
        this.lastActiveTime = SystemClock.elapsedRealtime();
    }

    /* access modifiers changed from: package-private */
    public long getInactiveDuration() {
        return SystemClock.elapsedRealtime() - this.lastActiveTime;
    }

    /* access modifiers changed from: package-private */
    public void setIntent(ActivityRecord r) {
        this.mCallingUid = r.launchedFromUid;
        this.mCallingPackage = r.launchedFromPackage;
        setIntent(r.intent, r.info);
        setLockTaskAuth(r);
    }

    private void setIntent(Intent _intent, ActivityInfo info) {
        ComponentName componentName;
        if (this.intent == null) {
            this.mNeverRelinquishIdentity = (info.flags & 4096) == 0;
        } else if (this.mNeverRelinquishIdentity) {
            return;
        }
        this.affinity = info.taskAffinity;
        if (this.intent == null) {
            this.rootAffinity = this.affinity;
        }
        this.effectiveUid = info.applicationInfo.uid;
        this.stringName = null;
        if (info.targetActivity == null) {
            if (!(_intent == null || (_intent.getSelector() == null && _intent.getSourceBounds() == null))) {
                _intent = new Intent(_intent);
                _intent.setSelector((Intent) null);
                _intent.setSourceBounds((Rect) null);
            }
            this.intent = _intent;
            this.realActivity = _intent != null ? _intent.getComponent() : null;
            this.origActivity = null;
        } else {
            ComponentName targetComponent = new ComponentName(info.packageName, info.targetActivity);
            if (_intent != null) {
                Intent targetIntent = new Intent(_intent);
                targetIntent.setSelector((Intent) null);
                targetIntent.setSourceBounds((Rect) null);
                this.intent = targetIntent;
                this.realActivity = targetComponent;
                this.origActivity = _intent.getComponent();
            } else {
                this.intent = null;
                this.realActivity = targetComponent;
                this.origActivity = new ComponentName(info.packageName, info.name);
            }
        }
        Intent intent2 = this.intent;
        int intentFlags = intent2 == null ? 0 : intent2.getFlags();
        if ((2097152 & intentFlags) != 0) {
            this.rootWasReset = true;
        }
        this.userId = UserHandle.getUserId(info.applicationInfo.uid);
        this.mUserSetupComplete = Settings.Secure.getIntForUser(this.mService.mContext.getContentResolver(), ATTR_USER_SETUP_COMPLETE, 0, this.userId) != 0;
        if ((info.flags & 8192) != 0) {
            this.autoRemoveRecents = true;
        } else if ((532480 & intentFlags) != 524288) {
            this.autoRemoveRecents = false;
        } else if (info.documentLaunchMode != 0) {
            this.autoRemoveRecents = false;
        } else {
            this.autoRemoveRecents = true;
        }
        this.mResizeMode = info.resizeMode;
        this.mSupportsPictureInPicture = info.supportsPictureInPicture();
        if (!supportsSplitScreenWindowingMode() && (componentName = this.realActivity) != null && ActivityManagerServiceInjector.inResizeWhiteList(componentName.getPackageName())) {
            this.mResizeMode = 4;
        }
    }

    private void setMinDimensions(ActivityInfo info) {
        if (info == null || info.windowLayout == null) {
            this.mMinWidth = -1;
            this.mMinHeight = -1;
            return;
        }
        this.mMinWidth = info.windowLayout.minWidth;
        this.mMinHeight = info.windowLayout.minHeight;
    }

    /* access modifiers changed from: package-private */
    public boolean isSameIntentFilter(ActivityRecord r) {
        Intent intent2;
        Intent intent3 = new Intent(r.intent);
        if (Objects.equals(this.realActivity, r.mActivityComponent) && (intent2 = this.intent) != null) {
            intent3.setComponent(intent2.getComponent());
        }
        return intent3.filterEquals(this.intent);
    }

    /* access modifiers changed from: package-private */
    public boolean returnsToHomeStack() {
        Intent intent2 = this.intent;
        return intent2 != null && (intent2.getFlags() & 268451840) == 268451840;
    }

    /* access modifiers changed from: package-private */
    public void setPrevAffiliate(TaskRecord prevAffiliate) {
        this.mPrevAffiliate = prevAffiliate;
        this.mPrevAffiliateTaskId = prevAffiliate == null ? -1 : prevAffiliate.taskId;
    }

    /* access modifiers changed from: package-private */
    public void setNextAffiliate(TaskRecord nextAffiliate) {
        this.mNextAffiliate = nextAffiliate;
        this.mNextAffiliateTaskId = nextAffiliate == null ? -1 : nextAffiliate.taskId;
    }

    /* access modifiers changed from: package-private */
    public <T extends ActivityStack> T getStack() {
        return this.mStack;
    }

    /* access modifiers changed from: package-private */
    public void setStack(ActivityStack stack) {
        if (stack == null || stack.isInStackLocked(this)) {
            ActivityStack oldStack = this.mStack;
            this.mStack = stack;
            if (oldStack != this.mStack) {
                for (int i = getChildCount() - 1; i >= 0; i--) {
                    ActivityRecord activity = getChildAt(i);
                    if (oldStack != null) {
                        oldStack.onActivityRemovedFromStack(activity);
                    }
                    if (this.mStack != null) {
                        stack.onActivityAddedToStack(activity);
                    }
                }
            }
            onParentChanged();
            return;
        }
        throw new IllegalStateException("Task must be added as a Stack child first.");
    }

    /* access modifiers changed from: package-private */
    public int getStackId() {
        ActivityStack activityStack = this.mStack;
        if (activityStack != null) {
            return activityStack.mStackId;
        }
        return -1;
    }

    /* access modifiers changed from: protected */
    public int getChildCount() {
        return this.mActivities.size();
    }

    /* access modifiers changed from: protected */
    public ActivityRecord getChildAt(int index) {
        return this.mActivities.get(index);
    }

    /* access modifiers changed from: protected */
    public ConfigurationContainer getParent() {
        return this.mStack;
    }

    /* access modifiers changed from: protected */
    public void onParentChanged() {
        super.onParentChanged();
        this.mService.mRootActivityContainer.updateUIDsPresentOnDisplay();
    }

    private void closeRecentsChain() {
        TaskRecord taskRecord = this.mPrevAffiliate;
        if (taskRecord != null) {
            taskRecord.setNextAffiliate(this.mNextAffiliate);
        }
        TaskRecord taskRecord2 = this.mNextAffiliate;
        if (taskRecord2 != null) {
            taskRecord2.setPrevAffiliate(this.mPrevAffiliate);
        }
        setPrevAffiliate((TaskRecord) null);
        setNextAffiliate((TaskRecord) null);
    }

    /* access modifiers changed from: package-private */
    public void removedFromRecents() {
        closeRecentsChain();
        if (this.inRecents) {
            this.inRecents = false;
            this.mService.notifyTaskPersisterLocked(this, false);
        }
        clearRootProcess();
        this.mService.mWindowManager.notifyTaskRemovedFromRecents(this.taskId, this.userId);
    }

    /* access modifiers changed from: package-private */
    public void setTaskToAffiliateWith(TaskRecord taskToAffiliateWith) {
        closeRecentsChain();
        this.mAffiliatedTaskId = taskToAffiliateWith.mAffiliatedTaskId;
        this.mAffiliatedTaskColor = taskToAffiliateWith.mAffiliatedTaskColor;
        while (true) {
            if (taskToAffiliateWith.mNextAffiliate == null) {
                break;
            }
            TaskRecord nextRecents = taskToAffiliateWith.mNextAffiliate;
            if (nextRecents.mAffiliatedTaskId != this.mAffiliatedTaskId) {
                Slog.e("ActivityTaskManager", "setTaskToAffiliateWith: nextRecents=" + nextRecents + " affilTaskId=" + nextRecents.mAffiliatedTaskId + " should be " + this.mAffiliatedTaskId);
                if (nextRecents.mPrevAffiliate == taskToAffiliateWith) {
                    nextRecents.setPrevAffiliate((TaskRecord) null);
                }
                taskToAffiliateWith.setNextAffiliate((TaskRecord) null);
            } else {
                taskToAffiliateWith = nextRecents;
            }
        }
        taskToAffiliateWith.setNextAffiliate(this);
        setPrevAffiliate(taskToAffiliateWith);
        setNextAffiliate((TaskRecord) null);
    }

    /* access modifiers changed from: package-private */
    public Intent getBaseIntent() {
        Intent intent2 = this.intent;
        return intent2 != null ? intent2 : this.affinityIntent;
    }

    /* access modifiers changed from: package-private */
    public ActivityRecord getRootActivity() {
        for (int i = 0; i < this.mActivities.size(); i++) {
            ActivityRecord r = this.mActivities.get(i);
            if (!r.finishing) {
                return r;
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public ActivityRecord getTopActivity() {
        return getTopActivity(true);
    }

    /* access modifiers changed from: package-private */
    public ActivityRecord getTopActivity(boolean includeOverlays) {
        for (int i = this.mActivities.size() - 1; i >= 0; i--) {
            ActivityRecord r = this.mActivities.get(i);
            if (!r.finishing && (includeOverlays || !r.mTaskOverlay)) {
                return r;
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public ActivityRecord topRunningActivityLocked() {
        if (this.mStack == null) {
            return null;
        }
        for (int activityNdx = this.mActivities.size() - 1; activityNdx >= 0; activityNdx--) {
            ActivityRecord r = this.mActivities.get(activityNdx);
            if (!r.finishing && r.okToShowLocked()) {
                return r;
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public boolean isVisible() {
        for (int i = this.mActivities.size() - 1; i >= 0; i--) {
            if (this.mActivities.get(i).visible) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean containsAppUid(int uid) {
        for (int i = this.mActivities.size() - 1; i >= 0; i--) {
            if (this.mActivities.get(i).getUid() == uid) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void getAllRunningVisibleActivitiesLocked(ArrayList<ActivityRecord> outActivities) {
        if (this.mStack != null) {
            for (int activityNdx = this.mActivities.size() - 1; activityNdx >= 0; activityNdx--) {
                ActivityRecord r = this.mActivities.get(activityNdx);
                if (!r.finishing && r.okToShowLocked() && r.visibleIgnoringKeyguard) {
                    outActivities.add(r);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public ActivityRecord topRunningActivityWithStartingWindowLocked() {
        if (this.mStack == null) {
            return null;
        }
        for (int activityNdx = this.mActivities.size() - 1; activityNdx >= 0; activityNdx--) {
            ActivityRecord r = this.mActivities.get(activityNdx);
            if (r.mStartingWindowState == 1 && !r.finishing && r.okToShowLocked()) {
                return r;
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public void getNumRunningActivities(TaskActivitiesReport reportOut) {
        reportOut.reset();
        for (int i = this.mActivities.size() - 1; i >= 0; i--) {
            ActivityRecord r = this.mActivities.get(i);
            if (!r.finishing) {
                reportOut.base = r;
                reportOut.numActivities++;
                if (reportOut.top == null || reportOut.top.isState(ActivityStack.ActivityState.INITIALIZING)) {
                    reportOut.top = r;
                    reportOut.numRunning = 0;
                }
                if (r.attachedToProcess()) {
                    reportOut.numRunning++;
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean okToShowLocked() {
        return this.mService.mStackSupervisor.isCurrentProfileLocked(this.userId) || topRunningActivityLocked() != null;
    }

    /* access modifiers changed from: package-private */
    public final void setFrontOfTask() {
        boolean foundFront = false;
        int numActivities = this.mActivities.size();
        for (int activityNdx = 0; activityNdx < numActivities; activityNdx++) {
            ActivityRecord r = this.mActivities.get(activityNdx);
            if (foundFront || r.finishing) {
                r.frontOfTask = false;
            } else {
                r.frontOfTask = true;
                foundFront = true;
            }
        }
        if (!foundFront && numActivities > 0) {
            this.mActivities.get(0).frontOfTask = true;
        }
    }

    /* access modifiers changed from: package-private */
    public final void moveActivityToFrontLocked(ActivityRecord newTop) {
        this.mActivities.remove(newTop);
        this.mActivities.add(newTop);
        this.mTask.positionChildAtTop(newTop.mAppWindowToken);
        updateEffectiveIntent();
        setFrontOfTask();
    }

    /* access modifiers changed from: package-private */
    public void addActivityToTop(ActivityRecord r) {
        addActivityAtIndex(this.mActivities.size(), r);
    }

    public int getActivityType() {
        int applicationType = super.getActivityType();
        if (applicationType != 0 || this.mActivities.isEmpty()) {
            return applicationType;
        }
        return this.mActivities.get(0).getActivityType();
    }

    /* access modifiers changed from: package-private */
    public void addActivityAtIndex(int index, ActivityRecord r) {
        TaskRecord task = r.getTaskRecord();
        if (task == null || task == this) {
            r.setTask(this);
            if (!this.mActivities.remove(r) && r.fullscreen) {
                this.numFullscreen++;
            }
            if (this.mActivities.isEmpty()) {
                if (r.getActivityType() == 0) {
                    r.setActivityType(1);
                }
                setActivityType(r.getActivityType());
                this.isPersistable = r.isPersistable();
                this.mCallingUid = r.launchedFromUid;
                this.mCallingPackage = r.launchedFromPackage;
                this.maxRecents = Math.min(Math.max(r.info.maxRecents, 1), ActivityTaskManager.getMaxAppRecentsLimitStatic());
            } else {
                r.setActivityType(getActivityType());
            }
            int size = this.mActivities.size();
            if (index == size && size > 0 && this.mActivities.get(size - 1).mTaskOverlay) {
                index--;
            }
            int index2 = Math.min(size, index);
            this.mActivities.add(index2, r);
            updateEffectiveIntent();
            if (r.isPersistable()) {
                this.mService.notifyTaskPersisterLocked(this, false);
            }
            if (r.mAppWindowToken != null) {
                this.mTask.positionChildAt(r.mAppWindowToken, index2);
            }
            this.mService.mRootActivityContainer.updateUIDsPresentOnDisplay();
            return;
        }
        throw new IllegalArgumentException("Can not add r= to task=" + this + " current parent=" + task);
    }

    /* access modifiers changed from: package-private */
    public boolean removeActivity(ActivityRecord r) {
        return removeActivity(r, false);
    }

    /* access modifiers changed from: package-private */
    public boolean removeActivity(ActivityRecord r, boolean reparenting) {
        if (r.getTaskRecord() == this) {
            r.setTask((TaskRecord) null, reparenting);
            if (this.mActivities.remove(r) && r.fullscreen) {
                this.numFullscreen--;
            }
            if (r.isPersistable()) {
                this.mService.notifyTaskPersisterLocked(this, false);
            }
            if (inPinnedWindowingMode()) {
                this.mService.getTaskChangeNotificationController().notifyTaskStackChanged();
            }
            if (this.mActivities.isEmpty()) {
                return !this.mReuseTask;
            }
            updateEffectiveIntent();
            return false;
        }
        throw new IllegalArgumentException("Activity=" + r + " does not belong to task=" + this);
    }

    /* access modifiers changed from: package-private */
    public boolean onlyHasTaskOverlayActivities(boolean excludeFinishing) {
        int count = 0;
        for (int i = this.mActivities.size() - 1; i >= 0; i--) {
            ActivityRecord r = this.mActivities.get(i);
            if (!excludeFinishing || !r.finishing) {
                if (!r.mTaskOverlay) {
                    return false;
                }
                count++;
            }
        }
        if (count > 0) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean autoRemoveFromRecents() {
        return this.autoRemoveRecents || (this.mActivities.isEmpty() && !this.hasBeenVisible);
    }

    /* access modifiers changed from: package-private */
    public final void performClearTaskAtIndexLocked(int activityNdx, boolean pauseImmediately, String reason) {
        int numActivities = this.mActivities.size();
        while (activityNdx < numActivities) {
            ActivityRecord r = this.mActivities.get(activityNdx);
            if (!r.finishing) {
                ActivityStack activityStack = this.mStack;
                if (activityStack == null) {
                    r.takeFromHistory();
                    this.mActivities.remove(activityNdx);
                    activityNdx--;
                    numActivities--;
                } else if (activityStack.finishActivityLocked(r, 0, (Intent) null, reason, false, pauseImmediately)) {
                    activityNdx--;
                    numActivities--;
                }
            }
            activityNdx++;
        }
    }

    /* access modifiers changed from: package-private */
    public void performClearTaskLocked() {
        this.mReuseTask = true;
        performClearTaskAtIndexLocked(0, false, "clear-task-all");
        this.mReuseTask = false;
    }

    /* access modifiers changed from: package-private */
    public ActivityRecord performClearTaskForReuseLocked(ActivityRecord newR, int launchFlags) {
        this.mReuseTask = true;
        ActivityRecord result = performClearTaskLocked(newR, launchFlags);
        this.mReuseTask = false;
        return result;
    }

    /* access modifiers changed from: package-private */
    public final ActivityRecord performClearTaskLocked(ActivityRecord newR, int launchFlags) {
        int activityNdx;
        int numActivities = this.mActivities.size();
        int activityNdx2 = numActivities - 1;
        while (activityNdx >= 0) {
            ActivityRecord r = this.mActivities.get(activityNdx);
            if (!r.finishing && r.mActivityComponent.equals(newR.mActivityComponent)) {
                ActivityRecord ret = r;
                while (true) {
                    activityNdx++;
                    if (activityNdx >= numActivities) {
                        break;
                    }
                    ActivityRecord r2 = this.mActivities.get(activityNdx);
                    if (!r2.finishing) {
                        ActivityOptions opts = r2.takeOptionsLocked(false);
                        if (opts != null) {
                            ret.updateOptionsLocked(opts);
                        }
                        ActivityStack activityStack = this.mStack;
                        if (activityStack != null && activityStack.finishActivityLocked(r2, 0, (Intent) null, "clear-task-stack", false)) {
                            activityNdx--;
                            numActivities--;
                        }
                    }
                }
                if (ret.launchMode != 0 || (536870912 & launchFlags) != 0 || ActivityStarter.isDocumentLaunchesIntoExisting(launchFlags) || ret.finishing) {
                    return ret;
                }
                ActivityStack activityStack2 = this.mStack;
                if (activityStack2 != null) {
                    activityStack2.finishActivityLocked(ret, 0, (Intent) null, "clear-task-top", false);
                }
                return null;
            }
            activityNdx2 = activityNdx - 1;
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public void removeTaskActivitiesLocked(boolean pauseImmediately, String reason) {
        performClearTaskAtIndexLocked(0, pauseImmediately, reason);
    }

    /* access modifiers changed from: package-private */
    public String lockTaskAuthToString() {
        int i = this.mLockTaskAuth;
        if (i == 0) {
            return "LOCK_TASK_AUTH_DONT_LOCK";
        }
        if (i == 1) {
            return "LOCK_TASK_AUTH_PINNABLE";
        }
        if (i == 2) {
            return "LOCK_TASK_AUTH_LAUNCHABLE";
        }
        if (i == 3) {
            return "LOCK_TASK_AUTH_WHITELISTED";
        }
        if (i == 4) {
            return "LOCK_TASK_AUTH_LAUNCHABLE_PRIV";
        }
        return "unknown=" + this.mLockTaskAuth;
    }

    /* access modifiers changed from: package-private */
    public void setLockTaskAuth() {
        setLockTaskAuth(getRootActivity());
    }

    private void setLockTaskAuth(ActivityRecord r) {
        int i = 1;
        if (r == null) {
            this.mLockTaskAuth = 1;
            return;
        }
        ComponentName componentName = this.realActivity;
        String pkg = componentName != null ? componentName.getPackageName() : null;
        LockTaskController lockTaskController = this.mService.getLockTaskController();
        int i2 = r.lockTaskLaunchMode;
        if (i2 == 0) {
            if (lockTaskController.isPackageWhitelisted(this.userId, pkg)) {
                i = 3;
            }
            this.mLockTaskAuth = i;
        } else if (i2 == 1) {
            this.mLockTaskAuth = 0;
        } else if (i2 == 2) {
            this.mLockTaskAuth = 4;
        } else if (i2 == 3) {
            if (lockTaskController.isPackageWhitelisted(this.userId, pkg)) {
                i = 2;
            }
            this.mLockTaskAuth = i;
        }
    }

    private boolean isResizeable(boolean checkSupportsPip) {
        return this.mService.mForceResizableActivities || ActivityInfo.isResizeableMode(this.mResizeMode) || (checkSupportsPip && this.mSupportsPictureInPicture) || ActivityStackSupervisorInjector.supportsFreeform();
    }

    /* access modifiers changed from: package-private */
    public boolean isResizeable() {
        return isResizeable(true);
    }

    public boolean supportsSplitScreenWindowingMode() {
        if (!super.supportsSplitScreenWindowingMode() || !this.mService.mSupportsSplitScreenMultiWindow) {
            return false;
        }
        if (!this.mService.mForceResizableActivities) {
            if (!isResizeable(false)) {
                return false;
            }
            ComponentName componentName = this.realActivity;
            return componentName == null || !ActivityManagerServiceInjector.inResizeBlackList(componentName.getPackageName());
        }
    }

    /* access modifiers changed from: package-private */
    public boolean canBeLaunchedOnDisplay(int displayId) {
        return this.mService.mStackSupervisor.canPlaceEntityOnDisplay(displayId, -1, -1, (ActivityInfo) null);
    }

    private boolean canResizeToBounds(Rect bounds) {
        if (bounds == null || !inFreeformWindowingMode()) {
            return true;
        }
        boolean landscape = bounds.width() > bounds.height();
        Rect configBounds = getRequestedOverrideBounds();
        int i = this.mResizeMode;
        if (i == 7) {
            if (configBounds.isEmpty()) {
                return true;
            }
            if (landscape == (configBounds.width() > configBounds.height())) {
                return true;
            }
            return false;
        } else if (((i != 6 || !landscape) && (this.mResizeMode != 5 || landscape)) || ActivityStackSupervisorInjector.supportsFreeform()) {
            return true;
        } else {
            return false;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isClearingToReuseTask() {
        return this.mReuseTask;
    }

    /* access modifiers changed from: package-private */
    public final ActivityRecord findActivityInHistoryLocked(ActivityRecord r) {
        ComponentName realActivity2 = r.mActivityComponent;
        for (int activityNdx = this.mActivities.size() - 1; activityNdx >= 0; activityNdx--) {
            ActivityRecord candidate = this.mActivities.get(activityNdx);
            if (!candidate.finishing && candidate.mActivityComponent.equals(realActivity2)) {
                return candidate;
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public void updateTaskDescription() {
        int numActivities = this.mActivities.size();
        boolean relinquish = false;
        if (!(numActivities == 0 || (this.mActivities.get(0).info.flags & 4096) == 0)) {
            relinquish = true;
        }
        int activityNdx = Math.min(numActivities, 1);
        while (true) {
            if (activityNdx >= numActivities) {
                break;
            }
            ActivityRecord r = this.mActivities.get(activityNdx);
            if (!relinquish || (r.info.flags & 4096) != 0) {
                if (r.intent != null && (r.intent.getFlags() & DumpState.DUMP_FROZEN) != 0) {
                    break;
                }
                activityNdx++;
            } else {
                activityNdx++;
                break;
            }
        }
        if (activityNdx > 0) {
            String label = null;
            String iconFilename = null;
            int iconResource = -1;
            int colorPrimary = 0;
            int colorBackground = 0;
            int statusBarColor = 0;
            int navigationBarColor = 0;
            boolean statusBarContrastWhenTransparent = false;
            boolean navigationBarContrastWhenTransparent = false;
            boolean topActivity = true;
            for (int activityNdx2 = activityNdx - 1; activityNdx2 >= 0; activityNdx2--) {
                ActivityRecord r2 = this.mActivities.get(activityNdx2);
                if (!r2.mTaskOverlay) {
                    if (r2.taskDescription != null) {
                        if (label == null) {
                            label = r2.taskDescription.getLabel();
                        }
                        if (iconResource == -1) {
                            iconResource = r2.taskDescription.getIconResource();
                        }
                        if (iconFilename == null) {
                            iconFilename = r2.taskDescription.getIconFilename();
                        }
                        if (colorPrimary == 0) {
                            colorPrimary = r2.taskDescription.getPrimaryColor();
                        }
                        if (topActivity) {
                            colorBackground = r2.taskDescription.getBackgroundColor();
                            statusBarColor = r2.taskDescription.getStatusBarColor();
                            navigationBarColor = r2.taskDescription.getNavigationBarColor();
                            statusBarContrastWhenTransparent = r2.taskDescription.getEnsureStatusBarContrastWhenTransparent();
                            navigationBarContrastWhenTransparent = r2.taskDescription.getEnsureNavigationBarContrastWhenTransparent();
                        }
                    }
                    topActivity = false;
                }
            }
            ActivityManager.TaskDescription taskDescription = r4;
            int i = iconResource;
            ActivityManager.TaskDescription taskDescription2 = new ActivityManager.TaskDescription(label, (Bitmap) null, iconResource, iconFilename, colorPrimary, colorBackground, statusBarColor, navigationBarColor, statusBarContrastWhenTransparent, navigationBarContrastWhenTransparent);
            this.lastTaskDescription = taskDescription;
            Task task = this.mTask;
            if (task != null) {
                task.setTaskDescription(this.lastTaskDescription);
            }
            if (this.taskId == this.mAffiliatedTaskId) {
                this.mAffiliatedTaskColor = this.lastTaskDescription.getPrimaryColor();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public int findEffectiveRootIndex() {
        int effectiveNdx = 0;
        int topActivityNdx = this.mActivities.size() - 1;
        for (int activityNdx = 0; activityNdx <= topActivityNdx; activityNdx++) {
            ActivityRecord r = this.mActivities.get(activityNdx);
            if (!r.finishing) {
                effectiveNdx = activityNdx;
                if ((r.info.flags & 4096) == 0) {
                    break;
                }
            }
        }
        return effectiveNdx;
    }

    /* access modifiers changed from: package-private */
    public void updateEffectiveIntent() {
        setIntent(this.mActivities.get(findEffectiveRootIndex()));
        updateTaskDescription();
    }

    /* access modifiers changed from: package-private */
    public void adjustForMinimalTaskDimensions(Rect bounds, Rect previousBounds) {
        if (bounds != null) {
            int minWidth = this.mMinWidth;
            int minHeight = this.mMinHeight;
            if (!inPinnedWindowingMode() && this.mStack != null) {
                int defaultMinSize = (int) (((float) this.mService.mRootActivityContainer.mDefaultMinSizeOfResizeableTaskDp) * (((float) this.mService.mRootActivityContainer.getActivityDisplay(this.mStack.mDisplayId).getConfiguration().densityDpi) / 160.0f));
                if (minWidth == -1) {
                    minWidth = defaultMinSize;
                }
                if (minHeight == -1) {
                    minHeight = defaultMinSize;
                }
            }
            boolean adjustHeight = true;
            boolean adjustWidth = minWidth > bounds.width();
            if (minHeight <= bounds.height()) {
                adjustHeight = false;
            }
            if (adjustWidth || adjustHeight) {
                if (adjustWidth) {
                    if (previousBounds.isEmpty() || bounds.right != previousBounds.right) {
                        bounds.right = bounds.left + minWidth;
                    } else {
                        bounds.left = bounds.right - minWidth;
                    }
                }
                if (!adjustHeight) {
                    return;
                }
                if (previousBounds.isEmpty() || bounds.bottom != previousBounds.bottom) {
                    bounds.bottom = bounds.top + minHeight;
                } else {
                    bounds.top = bounds.bottom - minHeight;
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean updateOverrideConfiguration(Rect bounds) {
        return updateOverrideConfiguration(bounds, (Rect) null);
    }

    /* access modifiers changed from: package-private */
    public void setLastNonFullscreenBounds(Rect bounds) {
        Rect rect = this.mLastNonFullscreenBounds;
        if (rect == null) {
            this.mLastNonFullscreenBounds = new Rect(bounds);
        } else {
            rect.set(bounds);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean updateOverrideConfiguration(Rect bounds, Rect insetBounds) {
        boolean hasSetDisplayedBounds = insetBounds != null && !insetBounds.isEmpty();
        if (hasSetDisplayedBounds) {
            setDisplayedBounds(bounds);
        } else {
            setDisplayedBounds((Rect) null);
        }
        Rect steadyBounds = hasSetDisplayedBounds ? insetBounds : bounds;
        if (equivalentRequestedOverrideBounds(steadyBounds)) {
            return false;
        }
        this.mTmpConfig.setTo(getResolvedOverrideConfiguration());
        setBounds(steadyBounds);
        return true ^ this.mTmpConfig.equals(getResolvedOverrideConfiguration());
    }

    /* access modifiers changed from: package-private */
    public void onActivityStateChanged(ActivityRecord record, ActivityStack.ActivityState state, String reason) {
        ActivityStack parent = getStack();
        if (parent != null) {
            parent.onActivityStateChanged(record, state, reason);
        }
    }

    public void onConfigurationChanged(Configuration newParentConfig) {
        Rect rect;
        boolean prevPersistTaskBounds = getWindowConfiguration().persistTaskBounds();
        boolean nextPersistTaskBounds = getRequestedOverrideConfiguration().windowConfiguration.persistTaskBounds() || newParentConfig.windowConfiguration.persistTaskBounds();
        if (!prevPersistTaskBounds && nextPersistTaskBounds && (rect = this.mLastNonFullscreenBounds) != null && !rect.isEmpty()) {
            if (MiuiMultiWindowUtils.mIsMiniFreeformMode) {
                this.mLastNonFullscreenBounds.set(getStack().getBounds());
            }
            getRequestedOverrideConfiguration().windowConfiguration.setBounds(this.mLastNonFullscreenBounds);
        }
        boolean wasInMultiWindowMode = inMultiWindowMode();
        super.onConfigurationChanged(newParentConfig);
        if (wasInMultiWindowMode != inMultiWindowMode()) {
            this.mService.mStackSupervisor.scheduleUpdateMultiWindowMode(this);
        }
        if (getWindowConfiguration().persistTaskBounds()) {
            Rect currentBounds = getRequestedOverrideBounds();
            if (!currentBounds.isEmpty()) {
                setLastNonFullscreenBounds(currentBounds);
            }
        }
        saveLaunchingStateIfNeeded();
    }

    /* access modifiers changed from: package-private */
    public void saveLaunchingStateIfNeeded() {
        if (this.hasBeenVisible && getWindowingMode() == 1) {
            this.mService.mStackSupervisor.mLaunchParamsPersister.saveTask(this);
        }
    }

    private static void fitWithinBounds(Rect bounds, Rect stackBounds, int overlapPxX, int overlapPxY) {
        if (stackBounds != null && !stackBounds.isEmpty() && !stackBounds.contains(bounds)) {
            int horizontalDiff = 0;
            int overlapLR = Math.min(overlapPxX, bounds.width());
            if (bounds.right < stackBounds.left + overlapLR) {
                horizontalDiff = overlapLR - (bounds.right - stackBounds.left);
            } else if (bounds.left > stackBounds.right - overlapLR) {
                horizontalDiff = -(overlapLR - (stackBounds.right - bounds.left));
            }
            int verticalDiff = 0;
            int overlapTB = Math.min(overlapPxY, bounds.width());
            if (bounds.bottom < stackBounds.top + overlapTB) {
                verticalDiff = overlapTB - (bounds.bottom - stackBounds.top);
            } else if (bounds.top > stackBounds.bottom - overlapTB) {
                verticalDiff = -(overlapTB - (stackBounds.bottom - bounds.top));
            }
            bounds.offset(horizontalDiff, verticalDiff);
        }
    }

    /* access modifiers changed from: package-private */
    public void setDisplayedBounds(Rect bounds) {
        if (bounds == null) {
            this.mDisplayedBounds.setEmpty();
        } else {
            this.mDisplayedBounds.set(bounds);
        }
        Task task = this.mTask;
        if (task != null) {
            task.setOverrideDisplayedBounds(this.mDisplayedBounds.isEmpty() ? null : this.mDisplayedBounds);
        }
    }

    /* access modifiers changed from: package-private */
    public Rect getDisplayedBounds() {
        return this.mDisplayedBounds;
    }

    /* access modifiers changed from: package-private */
    public boolean hasDisplayedBounds() {
        return !this.mDisplayedBounds.isEmpty();
    }

    static void intersectWithInsetsIfFits(Rect inOutBounds, Rect intersectBounds, Rect intersectInsets) {
        if (inOutBounds.right <= intersectBounds.right) {
            inOutBounds.right = Math.min(intersectBounds.right - intersectInsets.right, inOutBounds.right);
        }
        if (inOutBounds.bottom <= intersectBounds.bottom) {
            inOutBounds.bottom = Math.min(intersectBounds.bottom - intersectInsets.bottom, inOutBounds.bottom);
        }
        if (inOutBounds.left >= intersectBounds.left) {
            inOutBounds.left = Math.max(intersectBounds.left + intersectInsets.left, inOutBounds.left);
        }
        if (inOutBounds.top >= intersectBounds.top) {
            inOutBounds.top = Math.max(intersectBounds.top + intersectInsets.top, inOutBounds.top);
        }
    }

    private void calculateInsetFrames(Rect outNonDecorBounds, Rect outStableBounds, Rect bounds, DisplayInfo displayInfo) {
        DisplayPolicy policy;
        outNonDecorBounds.set(bounds);
        outStableBounds.set(bounds);
        if (getStack() != null && getStack().getDisplay() != null && (policy = getStack().getDisplay().mDisplayContent.getDisplayPolicy()) != null) {
            this.mTmpBounds.set(0, 0, displayInfo.logicalWidth, displayInfo.logicalHeight);
            policy.getNonDecorInsetsLw(displayInfo.rotation, displayInfo.logicalWidth, displayInfo.logicalHeight, displayInfo.displayCutout, this.mTmpInsets);
            intersectWithInsetsIfFits(outNonDecorBounds, this.mTmpBounds, this.mTmpInsets);
            policy.convertNonDecorInsetsToStableInsets(this.mTmpInsets, displayInfo.rotation);
            intersectWithInsetsIfFits(outStableBounds, this.mTmpBounds, this.mTmpInsets);
        }
    }

    private int getSmallestScreenWidthDpForDockedBounds(Rect bounds) {
        DisplayContent dc = this.mStack.getDisplay().mDisplayContent;
        if (dc != null) {
            return dc.getDockedDividerController().getSmallestWidthDpForBounds(bounds);
        }
        return 0;
    }

    /* access modifiers changed from: package-private */
    public void computeConfigResourceOverrides(Configuration inOutConfig, Configuration parentConfig) {
        computeConfigResourceOverrides(inOutConfig, parentConfig, (ActivityRecord.CompatDisplayInsets) null);
    }

    /* access modifiers changed from: package-private */
    public void computeConfigResourceOverrides(Configuration inOutConfig, Configuration parentConfig, ActivityRecord.CompatDisplayInsets compatInsets) {
        int i;
        int i2;
        Rect parentAppBounds;
        int windowingMode = inOutConfig.windowConfiguration.getWindowingMode();
        if (windowingMode == 0) {
            windowingMode = parentConfig.windowConfiguration.getWindowingMode();
        }
        float density = (float) inOutConfig.densityDpi;
        if (density == 0.0f) {
            density = (float) parentConfig.densityDpi;
        }
        float density2 = density * 0.00625f;
        Rect bounds = inOutConfig.windowConfiguration.getBounds();
        Rect outAppBounds = inOutConfig.windowConfiguration.getAppBounds();
        if (outAppBounds == null || outAppBounds.isEmpty()) {
            inOutConfig.windowConfiguration.setAppBounds(bounds);
            outAppBounds = inOutConfig.windowConfiguration.getAppBounds();
        }
        int i3 = 1;
        boolean insideParentBounds = compatInsets == null;
        if (insideParentBounds && windowingMode != 5 && (parentAppBounds = parentConfig.windowConfiguration.getAppBounds()) != null && !parentAppBounds.isEmpty()) {
            outAppBounds.intersect(parentAppBounds);
        }
        if (inOutConfig.screenWidthDp == 0 || inOutConfig.screenHeightDp == 0) {
            if (!insideParentBounds || this.mStack == null) {
                int rotation = parentConfig.windowConfiguration.getRotation();
                if (rotation == -1 || compatInsets == null) {
                    this.mTmpNonDecorBounds.set(outAppBounds);
                    this.mTmpStableBounds.set(outAppBounds);
                } else {
                    this.mTmpNonDecorBounds.set(bounds);
                    this.mTmpStableBounds.set(bounds);
                    compatInsets.getDisplayBoundsByRotation(this.mTmpBounds, rotation);
                    intersectWithInsetsIfFits(this.mTmpNonDecorBounds, this.mTmpBounds, compatInsets.mNonDecorInsets[rotation]);
                    intersectWithInsetsIfFits(this.mTmpStableBounds, this.mTmpBounds, compatInsets.mStableInsets[rotation]);
                    outAppBounds.set(this.mTmpNonDecorBounds);
                }
            } else {
                DisplayInfo di = new DisplayInfo();
                this.mStack.getDisplay().mDisplay.getDisplayInfo(di);
                calculateInsetFrames(this.mTmpNonDecorBounds, this.mTmpStableBounds, bounds, di);
            }
            if (inOutConfig.screenWidthDp == 0) {
                int overrideScreenWidthDp = (int) (((float) this.mTmpStableBounds.width()) / density2);
                if (insideParentBounds) {
                    i2 = Math.min(overrideScreenWidthDp, parentConfig.screenWidthDp);
                } else {
                    i2 = overrideScreenWidthDp;
                }
                inOutConfig.screenWidthDp = i2;
            }
            if (inOutConfig.screenHeightDp == 0) {
                int overrideScreenHeightDp = (int) (((float) this.mTmpStableBounds.height()) / density2);
                if (insideParentBounds) {
                    i = Math.min(overrideScreenHeightDp, parentConfig.screenHeightDp);
                } else {
                    i = overrideScreenHeightDp;
                }
                inOutConfig.screenHeightDp = i;
            }
            if (inOutConfig.smallestScreenWidthDp == 0) {
                if (WindowConfiguration.isFloating(windowingMode)) {
                    inOutConfig.smallestScreenWidthDp = (int) (((float) Math.min(bounds.width(), bounds.height())) / density2);
                } else if (WindowConfiguration.isSplitScreenWindowingMode(windowingMode)) {
                    inOutConfig.smallestScreenWidthDp = getSmallestScreenWidthDpForDockedBounds(bounds);
                }
            }
        }
        if (inOutConfig.orientation == 0) {
            if (!this.mTmpBounds.isEmpty() ? this.mTmpBounds.bottom < this.mTmpBounds.right : inOutConfig.screenWidthDp > inOutConfig.screenHeightDp) {
                i3 = 2;
            }
            inOutConfig.orientation = i3;
        }
        if (inOutConfig.screenLayout == 0) {
            int compatScreenWidthDp = (int) (((float) this.mTmpNonDecorBounds.width()) / density2);
            int compatScreenHeightDp = (int) (((float) this.mTmpNonDecorBounds.height()) / density2);
            inOutConfig.screenLayout = Configuration.reduceScreenLayout(parentConfig.screenLayout & 63, Math.max(compatScreenHeightDp, compatScreenWidthDp), Math.min(compatScreenHeightDp, compatScreenWidthDp));
        }
    }

    /* access modifiers changed from: package-private */
    public void resolveOverrideConfiguration(Configuration newParentConfig) {
        this.mTmpBounds.set(getResolvedOverrideConfiguration().windowConfiguration.getBounds());
        super.resolveOverrideConfiguration(newParentConfig);
        int windowingMode = getRequestedOverrideConfiguration().windowConfiguration.getWindowingMode();
        if (windowingMode == 0) {
            windowingMode = newParentConfig.windowConfiguration.getWindowingMode();
        }
        Rect outOverrideBounds = getResolvedOverrideConfiguration().windowConfiguration.getBounds();
        if (windowingMode == 1) {
            computeFullscreenBounds(outOverrideBounds, (ActivityRecord) null, newParentConfig.windowConfiguration.getBounds(), newParentConfig.orientation);
        }
        if (!outOverrideBounds.isEmpty()) {
            adjustForMinimalTaskDimensions(outOverrideBounds, this.mTmpBounds);
            if (windowingMode == 5) {
                float density = ((float) newParentConfig.densityDpi) / 160.0f;
                Rect parentBounds = new Rect(newParentConfig.windowConfiguration.getBounds());
                ActivityDisplay display = this.mStack.getDisplay();
                if (display != null) {
                    DisplayContent displayContent = display.mDisplayContent;
                }
                fitWithinBounds(outOverrideBounds, parentBounds, (int) (48.0f * density), (int) (32.0f * density));
                int offsetTop = parentBounds.top - outOverrideBounds.top;
                if (offsetTop > 0) {
                    outOverrideBounds.offset(0, offsetTop);
                }
            }
            computeConfigResourceOverrides(getResolvedOverrideConfiguration(), newParentConfig);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean handlesOrientationChangeFromDescendant() {
        Task task = this.mTask;
        return (task == null || task.getParent() == null || !this.mTask.getParent().handlesOrientationChangeFromDescendant()) ? false : true;
    }

    /* access modifiers changed from: package-private */
    public void computeFullscreenBounds(Rect outBounds, ActivityRecord refActivity, Rect parentBounds, int parentOrientation) {
        outBounds.setEmpty();
        if (!handlesOrientationChangeFromDescendant()) {
            if (refActivity == null) {
                refActivity = getTopActivity(false);
            }
            int overrideOrientation = getRequestedOverrideConfiguration().orientation;
            int forcedOrientation = (overrideOrientation != 0 || refActivity == null) ? overrideOrientation : refActivity.getRequestedConfigurationOrientation();
            if (forcedOrientation != 0 && forcedOrientation != parentOrientation) {
                int parentWidth = parentBounds.width();
                int parentHeight = parentBounds.height();
                float aspect = ((float) parentHeight) / ((float) parentWidth);
                if (forcedOrientation == 2) {
                    int height = (int) (((float) parentWidth) / aspect);
                    int top = parentBounds.centerY() - (height / 2);
                    outBounds.set(parentBounds.left, top, parentBounds.right, top + height);
                    return;
                }
                int width = (int) (((float) parentHeight) * aspect);
                int left = parentBounds.centerX() - (width / 2);
                outBounds.set(left, parentBounds.top, left + width, parentBounds.bottom);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public Rect updateOverrideConfigurationFromLaunchBounds() {
        Rect bounds = getLaunchBounds();
        updateOverrideConfiguration(bounds);
        if (bounds != null && !bounds.isEmpty()) {
            bounds.set(getRequestedOverrideBounds());
        }
        return bounds;
    }

    /* access modifiers changed from: package-private */
    public void updateOverrideConfigurationForStack(ActivityStack inStack) {
        ActivityStack activityStack = this.mStack;
        if (activityStack != null && activityStack == inStack) {
            return;
        }
        if (!inStack.inFreeformWindowingMode()) {
            updateOverrideConfiguration(inStack.getRequestedOverrideBounds());
        } else if (!isResizeable()) {
            throw new IllegalArgumentException("Can not position non-resizeable task=" + this + " in stack=" + inStack);
        } else if (matchParentBounds()) {
            Rect rect = this.mLastNonFullscreenBounds;
            if (rect != null) {
                updateOverrideConfiguration(rect);
            } else {
                this.mService.mStackSupervisor.getLaunchParamsController().layoutTask(this, (ActivityInfo.WindowLayout) null);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public Rect getLaunchBounds() {
        if (this.mStack == null) {
            return null;
        }
        int windowingMode = getWindowingMode();
        if (!isActivityTypeStandardOrUndefined() || windowingMode == 1 || (windowingMode == 3 && !isResizeable())) {
            if (isResizeable()) {
                return this.mStack.getRequestedOverrideBounds();
            }
            return null;
        } else if (!getWindowConfiguration().persistTaskBounds()) {
            return this.mStack.getRequestedOverrideBounds();
        } else {
            return this.mLastNonFullscreenBounds;
        }
    }

    /* access modifiers changed from: package-private */
    public void addStartingWindowsForVisibleActivities(boolean taskSwitch) {
        for (int activityNdx = this.mActivities.size() - 1; activityNdx >= 0; activityNdx--) {
            ActivityRecord r = this.mActivities.get(activityNdx);
            if (r.visible) {
                r.showStartingWindow((ActivityRecord) null, false, taskSwitch);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setRootProcess(WindowProcessController proc) {
        clearRootProcess();
        Intent intent2 = this.intent;
        if (intent2 != null && (intent2.getFlags() & DumpState.DUMP_VOLUMES) == 0) {
            this.mRootProcess = proc;
            this.mRootProcess.addRecentTask(this);
        }
    }

    /* access modifiers changed from: package-private */
    public void clearRootProcess() {
        WindowProcessController windowProcessController = this.mRootProcess;
        if (windowProcessController != null) {
            windowProcessController.removeRecentTask(this);
            this.mRootProcess = null;
        }
    }

    /* access modifiers changed from: package-private */
    public void clearAllPendingOptions() {
        for (int i = getChildCount() - 1; i >= 0; i--) {
            getChildAt(i).clearOptionsLocked(false);
        }
    }

    /* access modifiers changed from: package-private */
    public void fillTaskInfo(TaskInfo info) {
        ComponentName componentName;
        getNumRunningActivities(this.mReuseActivitiesReport);
        info.userId = this.userId;
        info.stackId = getStackId();
        info.taskId = this.taskId;
        ActivityStack activityStack = this.mStack;
        info.displayId = activityStack == null ? -1 : activityStack.mDisplayId;
        info.isRunning = getTopActivity() != null;
        info.baseIntent = new Intent(getBaseIntent());
        ComponentName componentName2 = null;
        if (this.mReuseActivitiesReport.base != null) {
            componentName = this.mReuseActivitiesReport.base.intent.getComponent();
        } else {
            componentName = null;
        }
        info.baseActivity = componentName;
        if (this.mReuseActivitiesReport.top != null) {
            componentName2 = this.mReuseActivitiesReport.top.mActivityComponent;
        }
        info.topActivity = componentName2;
        info.origActivity = this.origActivity;
        info.realActivity = this.realActivity;
        info.numActivities = this.mReuseActivitiesReport.numActivities;
        info.lastActiveTime = this.lastActiveTime;
        info.taskDescription = new ActivityManager.TaskDescription(this.lastTaskDescription);
        info.supportsSplitScreenMultiWindow = supportsSplitScreenWindowingMode();
        info.resizeMode = this.mResizeMode;
        info.configuration.setTo(getConfiguration());
    }

    /* access modifiers changed from: package-private */
    public ActivityManager.RunningTaskInfo getTaskInfo() {
        ActivityManager.RunningTaskInfo info = new ActivityManager.RunningTaskInfo();
        fillTaskInfo(info);
        return info;
    }

    /* access modifiers changed from: package-private */
    public void dump(PrintWriter pw, String prefix) {
        pw.print(prefix);
        pw.print("userId=");
        pw.print(this.userId);
        pw.print(" effectiveUid=");
        UserHandle.formatUid(pw, this.effectiveUid);
        pw.print(" mCallingUid=");
        UserHandle.formatUid(pw, this.mCallingUid);
        pw.print(" mUserSetupComplete=");
        pw.print(this.mUserSetupComplete);
        pw.print(" mCallingPackage=");
        pw.println(this.mCallingPackage);
        if (!(this.affinity == null && this.rootAffinity == null)) {
            pw.print(prefix);
            pw.print("affinity=");
            pw.print(this.affinity);
            String str = this.affinity;
            if (str == null || !str.equals(this.rootAffinity)) {
                pw.print(" root=");
                pw.println(this.rootAffinity);
            } else {
                pw.println();
            }
        }
        if (!(this.voiceSession == null && this.voiceInteractor == null)) {
            pw.print(prefix);
            pw.print("VOICE: session=0x");
            pw.print(Integer.toHexString(System.identityHashCode(this.voiceSession)));
            pw.print(" interactor=0x");
            pw.println(Integer.toHexString(System.identityHashCode(this.voiceInteractor)));
        }
        if (this.intent != null) {
            StringBuilder sb = new StringBuilder(128);
            sb.append(prefix);
            sb.append("intent={");
            this.intent.toShortString(sb, false, true, false, true);
            sb.append('}');
            pw.println(sb.toString());
        }
        if (this.affinityIntent != null) {
            StringBuilder sb2 = new StringBuilder(128);
            sb2.append(prefix);
            sb2.append("affinityIntent={");
            this.affinityIntent.toShortString(sb2, false, true, false, true);
            sb2.append('}');
            pw.println(sb2.toString());
        }
        if (this.origActivity != null) {
            pw.print(prefix);
            pw.print("origActivity=");
            pw.println(this.origActivity.flattenToShortString());
        }
        if (this.realActivity != null) {
            pw.print(prefix);
            pw.print("mActivityComponent=");
            pw.println(this.realActivity.flattenToShortString());
        }
        if (this.autoRemoveRecents || this.isPersistable || !isActivityTypeStandard() || this.numFullscreen != 0) {
            pw.print(prefix);
            pw.print("autoRemoveRecents=");
            pw.print(this.autoRemoveRecents);
            pw.print(" isPersistable=");
            pw.print(this.isPersistable);
            pw.print(" numFullscreen=");
            pw.print(this.numFullscreen);
            pw.print(" activityType=");
            pw.println(getActivityType());
        }
        if (this.rootWasReset || this.mNeverRelinquishIdentity || this.mReuseTask || this.mLockTaskAuth != 1) {
            pw.print(prefix);
            pw.print("rootWasReset=");
            pw.print(this.rootWasReset);
            pw.print(" mNeverRelinquishIdentity=");
            pw.print(this.mNeverRelinquishIdentity);
            pw.print(" mReuseTask=");
            pw.print(this.mReuseTask);
            pw.print(" mLockTaskAuth=");
            pw.println(lockTaskAuthToString());
        }
        if (!(this.mAffiliatedTaskId == this.taskId && this.mPrevAffiliateTaskId == -1 && this.mPrevAffiliate == null && this.mNextAffiliateTaskId == -1 && this.mNextAffiliate == null)) {
            pw.print(prefix);
            pw.print("affiliation=");
            pw.print(this.mAffiliatedTaskId);
            pw.print(" prevAffiliation=");
            pw.print(this.mPrevAffiliateTaskId);
            pw.print(" (");
            TaskRecord taskRecord = this.mPrevAffiliate;
            if (taskRecord == null) {
                pw.print("null");
            } else {
                pw.print(Integer.toHexString(System.identityHashCode(taskRecord)));
            }
            pw.print(") nextAffiliation=");
            pw.print(this.mNextAffiliateTaskId);
            pw.print(" (");
            TaskRecord taskRecord2 = this.mNextAffiliate;
            if (taskRecord2 == null) {
                pw.print("null");
            } else {
                pw.print(Integer.toHexString(System.identityHashCode(taskRecord2)));
            }
            pw.println(")");
        }
        pw.print(prefix);
        pw.print("Activities=");
        pw.println(this.mActivities);
        if (!this.askedCompatMode || !this.inRecents || !this.isAvailable) {
            pw.print(prefix);
            pw.print("askedCompatMode=");
            pw.print(this.askedCompatMode);
            pw.print(" inRecents=");
            pw.print(this.inRecents);
            pw.print(" isAvailable=");
            pw.println(this.isAvailable);
        }
        if (this.lastDescription != null) {
            pw.print(prefix);
            pw.print("lastDescription=");
            pw.println(this.lastDescription);
        }
        if (this.mRootProcess != null) {
            pw.print(prefix);
            pw.print("mRootProcess=");
            pw.println(this.mRootProcess);
        }
        pw.print(prefix);
        pw.print("stackId=");
        pw.println(getStackId());
        pw.print(prefix + "hasBeenVisible=" + this.hasBeenVisible);
        StringBuilder sb3 = new StringBuilder();
        sb3.append(" mResizeMode=");
        sb3.append(ActivityInfo.resizeModeToString(this.mResizeMode));
        pw.print(sb3.toString());
        pw.print(" mSupportsPictureInPicture=" + this.mSupportsPictureInPicture);
        pw.print(" isResizeable=" + isResizeable());
        pw.print(" supportsSplitScreenWindowingMode=" + supportsSplitScreenWindowingMode());
        pw.print(" lastActiveTime=" + this.lastActiveTime);
        pw.println(" (inactive for " + (getInactiveDuration() / 1000) + "s)");
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        String str = this.stringName;
        if (str != null) {
            sb.append(str);
            sb.append(" U=");
            sb.append(this.userId);
            sb.append(" StackId=");
            sb.append(getStackId());
            sb.append(" sz=");
            sb.append(this.mActivities.size());
            sb.append('}');
            return sb.toString();
        }
        sb.append("TaskRecord{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(" #");
        sb.append(this.taskId);
        if (this.affinity != null) {
            sb.append(" A=");
            sb.append(this.affinity);
        } else if (this.intent != null) {
            sb.append(" I=");
            sb.append(this.intent.getComponent().flattenToShortString());
        } else {
            Intent intent2 = this.affinityIntent;
            if (intent2 == null || intent2.getComponent() == null) {
                sb.append(" ??");
            } else {
                sb.append(" aI=");
                sb.append(this.affinityIntent.getComponent().flattenToShortString());
            }
        }
        this.stringName = sb.toString();
        return toString();
    }

    public void writeToProto(ProtoOutputStream proto, long fieldId, int logLevel) {
        if (logLevel != 2 || isVisible()) {
            long token = proto.start(fieldId);
            super.writeToProto(proto, 1146756268033L, logLevel);
            proto.write(1120986464258L, this.taskId);
            for (int i = this.mActivities.size() - 1; i >= 0; i--) {
                this.mActivities.get(i).writeToProto(proto, 2246267895811L);
            }
            proto.write(1120986464260L, this.mStack.mStackId);
            Rect rect = this.mLastNonFullscreenBounds;
            if (rect != null) {
                rect.writeToProto(proto, 1146756268037L);
            }
            ComponentName componentName = this.realActivity;
            if (componentName != null) {
                proto.write(1138166333446L, componentName.flattenToShortString());
            }
            ComponentName componentName2 = this.origActivity;
            if (componentName2 != null) {
                proto.write(1138166333447L, componentName2.flattenToShortString());
            }
            proto.write(1120986464264L, getActivityType());
            proto.write(1120986464265L, this.mResizeMode);
            proto.write(1133871366154L, matchParentBounds());
            if (!matchParentBounds()) {
                getRequestedOverrideBounds().writeToProto(proto, 1146756268043L);
            }
            proto.write(1120986464268L, this.mMinWidth);
            proto.write(1120986464269L, this.mMinHeight);
            proto.end(token);
        }
    }

    static class TaskActivitiesReport {
        ActivityRecord base;
        int numActivities;
        int numRunning;
        ActivityRecord top;

        TaskActivitiesReport() {
        }

        /* access modifiers changed from: package-private */
        public void reset() {
            this.numActivities = 0;
            this.numRunning = 0;
            this.base = null;
            this.top = null;
        }
    }

    /* access modifiers changed from: package-private */
    public void saveToXml(XmlSerializer out) throws IOException, XmlPullParserException {
        out.attribute((String) null, ATTR_TASKID, String.valueOf(this.taskId));
        ComponentName componentName = this.realActivity;
        if (componentName != null) {
            out.attribute((String) null, ATTR_REALACTIVITY, componentName.flattenToShortString());
        }
        out.attribute((String) null, ATTR_REALACTIVITY_SUSPENDED, String.valueOf(this.realActivitySuspended));
        ComponentName componentName2 = this.origActivity;
        if (componentName2 != null) {
            out.attribute((String) null, ATTR_ORIGACTIVITY, componentName2.flattenToShortString());
        }
        String str = this.affinity;
        if (str != null) {
            out.attribute((String) null, ATTR_AFFINITY, str);
            if (!this.affinity.equals(this.rootAffinity)) {
                String str2 = this.rootAffinity;
                if (str2 == null) {
                    str2 = "@";
                }
                out.attribute((String) null, ATTR_ROOT_AFFINITY, str2);
            }
        } else {
            String str3 = this.rootAffinity;
            if (str3 != null) {
                if (str3 == null) {
                    str3 = "@";
                }
                out.attribute((String) null, ATTR_ROOT_AFFINITY, str3);
            }
        }
        out.attribute((String) null, ATTR_ROOTHASRESET, String.valueOf(this.rootWasReset));
        out.attribute((String) null, ATTR_AUTOREMOVERECENTS, String.valueOf(this.autoRemoveRecents));
        out.attribute((String) null, ATTR_ASKEDCOMPATMODE, String.valueOf(this.askedCompatMode));
        out.attribute((String) null, ATTR_USERID, String.valueOf(this.userId));
        out.attribute((String) null, ATTR_USER_SETUP_COMPLETE, String.valueOf(this.mUserSetupComplete));
        out.attribute((String) null, ATTR_EFFECTIVE_UID, String.valueOf(this.effectiveUid));
        out.attribute((String) null, ATTR_LASTTIMEMOVED, String.valueOf(this.mLastTimeMoved));
        out.attribute((String) null, ATTR_NEVERRELINQUISH, String.valueOf(this.mNeverRelinquishIdentity));
        CharSequence charSequence = this.lastDescription;
        if (charSequence != null) {
            out.attribute((String) null, ATTR_LASTDESCRIPTION, charSequence.toString());
        }
        ActivityManager.TaskDescription taskDescription = this.lastTaskDescription;
        if (taskDescription != null) {
            taskDescription.saveToXml(out);
        }
        out.attribute((String) null, ATTR_TASK_AFFILIATION_COLOR, String.valueOf(this.mAffiliatedTaskColor));
        out.attribute((String) null, ATTR_TASK_AFFILIATION, String.valueOf(this.mAffiliatedTaskId));
        out.attribute((String) null, ATTR_PREV_AFFILIATION, String.valueOf(this.mPrevAffiliateTaskId));
        out.attribute((String) null, ATTR_NEXT_AFFILIATION, String.valueOf(this.mNextAffiliateTaskId));
        out.attribute((String) null, ATTR_CALLING_UID, String.valueOf(this.mCallingUid));
        String str4 = this.mCallingPackage;
        if (str4 == null) {
            str4 = "";
        }
        out.attribute((String) null, ATTR_CALLING_PACKAGE, str4);
        out.attribute((String) null, ATTR_RESIZE_MODE, String.valueOf(this.mResizeMode));
        out.attribute((String) null, ATTR_SUPPORTS_PICTURE_IN_PICTURE, String.valueOf(this.mSupportsPictureInPicture));
        Rect rect = this.mLastNonFullscreenBounds;
        if (rect != null) {
            out.attribute((String) null, ATTR_NON_FULLSCREEN_BOUNDS, rect.flattenToString());
        }
        out.attribute((String) null, ATTR_MIN_WIDTH, String.valueOf(this.mMinWidth));
        out.attribute((String) null, ATTR_MIN_HEIGHT, String.valueOf(this.mMinHeight));
        out.attribute((String) null, ATTR_PERSIST_TASK_VERSION, String.valueOf(1));
        out.attribute((String) null, ATTR_IS_AVAILABLE, String.valueOf(this.isAvailable));
        if (this.affinityIntent != null) {
            out.startTag((String) null, TAG_AFFINITYINTENT);
            this.affinityIntent.saveToXml(out);
            out.endTag((String) null, TAG_AFFINITYINTENT);
        }
        if (this.intent != null) {
            out.startTag((String) null, TAG_INTENT);
            this.intent.saveToXml(out);
            out.endTag((String) null, TAG_INTENT);
        }
        ArrayList<ActivityRecord> activities = this.mActivities;
        int numActivities = activities.size();
        int activityNdx = 0;
        while (activityNdx < numActivities) {
            ActivityRecord r = activities.get(activityNdx);
            if (r.info.persistableMode != 0 && r.isPersistable()) {
                if (((r.intent.getFlags() & DumpState.DUMP_FROZEN) | 8192) != 524288 || activityNdx <= 0) {
                    out.startTag((String) null, TAG_ACTIVITY);
                    r.saveToXml(out);
                    out.endTag((String) null, TAG_ACTIVITY);
                    activityNdx++;
                } else {
                    return;
                }
            } else {
                return;
            }
        }
    }

    @VisibleForTesting
    static TaskRecordFactory getTaskRecordFactory() {
        if (sTaskRecordFactory == null) {
            setTaskRecordFactory(new TaskRecordFactory());
        }
        return sTaskRecordFactory;
    }

    static void setTaskRecordFactory(TaskRecordFactory factory) {
        sTaskRecordFactory = factory;
    }

    static TaskRecord create(ActivityTaskManagerService service, int taskId2, ActivityInfo info, Intent intent2, IVoiceInteractionSession voiceSession2, IVoiceInteractor voiceInteractor2) {
        return getTaskRecordFactory().create(service, taskId2, info, intent2, voiceSession2, voiceInteractor2);
    }

    static TaskRecord create(ActivityTaskManagerService service, int taskId2, ActivityInfo info, Intent intent2, ActivityManager.TaskDescription taskDescription) {
        return getTaskRecordFactory().create(service, taskId2, info, intent2, taskDescription);
    }

    static TaskRecord restoreFromXml(XmlPullParser in, ActivityStackSupervisor stackSupervisor) throws IOException, XmlPullParserException {
        return getTaskRecordFactory().restoreFromXml(in, stackSupervisor);
    }

    static class TaskRecordFactory {
        TaskRecordFactory() {
        }

        /* access modifiers changed from: package-private */
        public TaskRecord create(ActivityTaskManagerService service, int taskId, ActivityInfo info, Intent intent, IVoiceInteractionSession voiceSession, IVoiceInteractor voiceInteractor) {
            return new TaskRecord(service, taskId, info, intent, voiceSession, voiceInteractor);
        }

        /* access modifiers changed from: package-private */
        public TaskRecord create(ActivityTaskManagerService service, int taskId, ActivityInfo info, Intent intent, ActivityManager.TaskDescription taskDescription) {
            return new TaskRecord(service, taskId, info, intent, taskDescription);
        }

        /* access modifiers changed from: package-private */
        public TaskRecord create(ActivityTaskManagerService service, int taskId, Intent intent, Intent affinityIntent, String affinity, String rootAffinity, ComponentName realActivity, ComponentName origActivity, boolean rootWasReset, boolean autoRemoveRecents, boolean askedCompatMode, int userId, int effectiveUid, String lastDescription, ArrayList<ActivityRecord> activities, long lastTimeMoved, boolean neverRelinquishIdentity, ActivityManager.TaskDescription lastTaskDescription, int taskAffiliation, int prevTaskId, int nextTaskId, int taskAffiliationColor, int callingUid, String callingPackage, int resizeMode, boolean supportsPictureInPicture, boolean realActivitySuspended, boolean userSetupComplete, int minWidth, int minHeight, boolean isAvailable) {
            return new TaskRecord(service, taskId, intent, affinityIntent, affinity, rootAffinity, realActivity, origActivity, rootWasReset, autoRemoveRecents, askedCompatMode, userId, effectiveUid, lastDescription, activities, lastTimeMoved, neverRelinquishIdentity, lastTaskDescription, taskAffiliation, prevTaskId, nextTaskId, taskAffiliationColor, callingUid, callingPackage, resizeMode, supportsPictureInPicture, realActivitySuspended, userSetupComplete, minWidth, minHeight, isAvailable);
        }

        /* access modifiers changed from: package-private */
        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* JADX WARNING: Removed duplicated region for block: B:195:0x048e A[LOOP:2: B:194:0x048c->B:195:0x048e, LOOP_END] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public com.android.server.wm.TaskRecord restoreFromXml(org.xmlpull.v1.XmlPullParser r72, com.android.server.wm.ActivityStackSupervisor r73) throws java.io.IOException, org.xmlpull.v1.XmlPullParserException {
            /*
                r71 = this;
                r1 = r72
                r0 = 0
                r2 = 0
                java.util.ArrayList r3 = new java.util.ArrayList
                r3.<init>()
                r4 = 0
                r5 = 0
                r6 = 0
                r7 = 0
                r8 = 0
                r9 = 0
                r10 = 0
                r11 = 0
                r12 = 0
                r13 = 0
                r14 = 0
                r15 = 1
                r16 = -1
                r17 = 0
                r18 = 0
                r20 = 1
                r21 = -1
                r22 = r2
                int r2 = r72.getDepth()
                android.app.ActivityManager$TaskDescription r23 = new android.app.ActivityManager$TaskDescription
                r23.<init>()
                r37 = r23
                r23 = -1
                r24 = 0
                r25 = -1
                r26 = -1
                r27 = -1
                java.lang.String r28 = ""
                r29 = 4
                r30 = 0
                r31 = 0
                r32 = -1
                r33 = -1
                r34 = 0
                r35 = 1
                int r36 = r72.getAttributeCount()
                r38 = r15
                r15 = 1
                int r36 = r36 + -1
                r39 = r4
                r54 = r5
                r42 = r6
                r44 = r7
                r55 = r9
                r56 = r10
                r57 = r11
                r58 = r12
                r11 = r13
                r10 = r14
                r50 = r17
                r52 = r18
                r14 = r21
                r40 = r23
                r41 = r24
                r43 = r25
                r45 = r26
                r46 = r27
                r51 = r28
                r5 = r29
                r12 = r31
                r47 = r32
                r48 = r33
                r13 = r34
                r49 = r35
                r4 = r36
                r59 = r38
                r38 = r20
            L_0x0085:
                java.lang.String r6 = "ActivityTaskManager"
                if (r4 < 0) goto L_0x0341
                java.lang.String r7 = r1.getAttributeName(r4)
                java.lang.String r9 = r1.getAttributeValue(r4)
                int r19 = r7.hashCode()
                switch(r19) {
                    case -2134816935: goto L_0x01da;
                    case -2045008396: goto L_0x01cf;
                    case -1556983798: goto L_0x01c4;
                    case -1537240555: goto L_0x01ba;
                    case -1494902876: goto L_0x01af;
                    case -1292777190: goto L_0x01a4;
                    case -1138503444: goto L_0x019a;
                    case -1124927690: goto L_0x018f;
                    case -974080081: goto L_0x0184;
                    case -929566280: goto L_0x0178;
                    case -865458610: goto L_0x016c;
                    case -826243148: goto L_0x0160;
                    case -707249465: goto L_0x0154;
                    case -705269939: goto L_0x0149;
                    case -502399667: goto L_0x013e;
                    case -360792224: goto L_0x0132;
                    case -162744347: goto L_0x0127;
                    case -147132913: goto L_0x011b;
                    case -132216235: goto L_0x010f;
                    case 180927924: goto L_0x0103;
                    case 331206372: goto L_0x00f7;
                    case 541503897: goto L_0x00eb;
                    case 605497640: goto L_0x00e0;
                    case 869221331: goto L_0x00d4;
                    case 1007873193: goto L_0x00c8;
                    case 1081438155: goto L_0x00bc;
                    case 1457608782: goto L_0x00b0;
                    case 1539554448: goto L_0x00a5;
                    case 2023391309: goto L_0x009a;
                    default: goto L_0x0098;
                }
            L_0x0098:
                goto L_0x01e5
            L_0x009a:
                java.lang.String r15 = "root_has_reset"
                boolean r15 = r7.equals(r15)
                if (r15 == 0) goto L_0x0098
                r15 = 6
                goto L_0x01e6
            L_0x00a5:
                java.lang.String r15 = "real_activity"
                boolean r15 = r7.equals(r15)
                if (r15 == 0) goto L_0x0098
                r15 = 1
                goto L_0x01e6
            L_0x00b0:
                java.lang.String r15 = "never_relinquish_identity"
                boolean r15 = r7.equals(r15)
                if (r15 == 0) goto L_0x0098
                r15 = 15
                goto L_0x01e6
            L_0x00bc:
                java.lang.String r15 = "calling_package"
                boolean r15 = r7.equals(r15)
                if (r15 == 0) goto L_0x0098
                r15 = 21
                goto L_0x01e6
            L_0x00c8:
                java.lang.String r15 = "persist_task_version"
                boolean r15 = r7.equals(r15)
                if (r15 == 0) goto L_0x0098
                r15 = 27
                goto L_0x01e6
            L_0x00d4:
                java.lang.String r15 = "last_description"
                boolean r15 = r7.equals(r15)
                if (r15 == 0) goto L_0x0098
                r15 = 13
                goto L_0x01e6
            L_0x00e0:
                java.lang.String r15 = "affinity"
                boolean r15 = r7.equals(r15)
                if (r15 == 0) goto L_0x0098
                r15 = 4
                goto L_0x01e6
            L_0x00eb:
                java.lang.String r15 = "min_width"
                boolean r15 = r7.equals(r15)
                if (r15 == 0) goto L_0x0098
                r15 = 25
                goto L_0x01e6
            L_0x00f7:
                java.lang.String r15 = "prev_affiliation"
                boolean r15 = r7.equals(r15)
                if (r15 == 0) goto L_0x0098
                r15 = 17
                goto L_0x01e6
            L_0x0103:
                java.lang.String r15 = "task_type"
                boolean r15 = r7.equals(r15)
                if (r15 == 0) goto L_0x0098
                r15 = 12
                goto L_0x01e6
            L_0x010f:
                java.lang.String r15 = "calling_uid"
                boolean r15 = r7.equals(r15)
                if (r15 == 0) goto L_0x0098
                r15 = 20
                goto L_0x01e6
            L_0x011b:
                java.lang.String r15 = "user_id"
                boolean r15 = r7.equals(r15)
                if (r15 == 0) goto L_0x0098
                r15 = 9
                goto L_0x01e6
            L_0x0127:
                java.lang.String r15 = "root_affinity"
                boolean r15 = r7.equals(r15)
                if (r15 == 0) goto L_0x0098
                r15 = 5
                goto L_0x01e6
            L_0x0132:
                java.lang.String r15 = "supports_picture_in_picture"
                boolean r15 = r7.equals(r15)
                if (r15 == 0) goto L_0x0098
                r15 = 23
                goto L_0x01e6
            L_0x013e:
                java.lang.String r15 = "auto_remove_recents"
                boolean r15 = r7.equals(r15)
                if (r15 == 0) goto L_0x0098
                r15 = 7
                goto L_0x01e6
            L_0x0149:
                java.lang.String r15 = "orig_activity"
                boolean r15 = r7.equals(r15)
                if (r15 == 0) goto L_0x0098
                r15 = 3
                goto L_0x01e6
            L_0x0154:
                java.lang.String r15 = "non_fullscreen_bounds"
                boolean r15 = r7.equals(r15)
                if (r15 == 0) goto L_0x0098
                r15 = 24
                goto L_0x01e6
            L_0x0160:
                java.lang.String r15 = "min_height"
                boolean r15 = r7.equals(r15)
                if (r15 == 0) goto L_0x0098
                r15 = 26
                goto L_0x01e6
            L_0x016c:
                java.lang.String r15 = "resize_mode"
                boolean r15 = r7.equals(r15)
                if (r15 == 0) goto L_0x0098
                r15 = 22
                goto L_0x01e6
            L_0x0178:
                java.lang.String r15 = "effective_uid"
                boolean r15 = r7.equals(r15)
                if (r15 == 0) goto L_0x0098
                r15 = 11
                goto L_0x01e6
            L_0x0184:
                java.lang.String r15 = "user_setup_complete"
                boolean r15 = r7.equals(r15)
                if (r15 == 0) goto L_0x0098
                r15 = 10
                goto L_0x01e6
            L_0x018f:
                java.lang.String r15 = "task_affiliation"
                boolean r15 = r7.equals(r15)
                if (r15 == 0) goto L_0x0098
                r15 = 16
                goto L_0x01e6
            L_0x019a:
                java.lang.String r15 = "real_activity_suspended"
                boolean r15 = r7.equals(r15)
                if (r15 == 0) goto L_0x0098
                r15 = 2
                goto L_0x01e6
            L_0x01a4:
                java.lang.String r15 = "task_affiliation_color"
                boolean r15 = r7.equals(r15)
                if (r15 == 0) goto L_0x0098
                r15 = 19
                goto L_0x01e6
            L_0x01af:
                java.lang.String r15 = "next_affiliation"
                boolean r15 = r7.equals(r15)
                if (r15 == 0) goto L_0x0098
                r15 = 18
                goto L_0x01e6
            L_0x01ba:
                java.lang.String r15 = "task_id"
                boolean r15 = r7.equals(r15)
                if (r15 == 0) goto L_0x0098
                r15 = 0
                goto L_0x01e6
            L_0x01c4:
                java.lang.String r15 = "last_time_moved"
                boolean r15 = r7.equals(r15)
                if (r15 == 0) goto L_0x0098
                r15 = 14
                goto L_0x01e6
            L_0x01cf:
                java.lang.String r15 = "is_available"
                boolean r15 = r7.equals(r15)
                if (r15 == 0) goto L_0x0098
                r15 = 28
                goto L_0x01e6
            L_0x01da:
                java.lang.String r15 = "asked_compat_mode"
                boolean r15 = r7.equals(r15)
                if (r15 == 0) goto L_0x0098
                r15 = 8
                goto L_0x01e6
            L_0x01e5:
                r15 = -1
            L_0x01e6:
                switch(r15) {
                    case 0: goto L_0x0312;
                    case 1: goto L_0x0309;
                    case 2: goto L_0x02fc;
                    case 3: goto L_0x02f3;
                    case 4: goto L_0x02ec;
                    case 5: goto L_0x02e2;
                    case 6: goto L_0x02d9;
                    case 7: goto L_0x02cf;
                    case 8: goto L_0x02c5;
                    case 9: goto L_0x02bb;
                    case 10: goto L_0x02b1;
                    case 11: goto L_0x02a5;
                    case 12: goto L_0x029a;
                    case 13: goto L_0x0292;
                    case 14: goto L_0x0288;
                    case 15: goto L_0x027e;
                    case 16: goto L_0x0274;
                    case 17: goto L_0x026a;
                    case 18: goto L_0x0260;
                    case 19: goto L_0x0256;
                    case 20: goto L_0x024c;
                    case 21: goto L_0x0244;
                    case 22: goto L_0x023a;
                    case 23: goto L_0x022e;
                    case 24: goto L_0x0223;
                    case 25: goto L_0x0219;
                    case 26: goto L_0x020f;
                    case 27: goto L_0x0204;
                    case 28: goto L_0x01fa;
                    default: goto L_0x01e9;
                }
            L_0x01e9:
                java.lang.String r15 = "task_description_"
                boolean r15 = r7.startsWith(r15)
                if (r15 == 0) goto L_0x031e
                r15 = r37
                r15.restoreFromXml(r7, r9)
                r19 = r0
                goto L_0x0338
            L_0x01fa:
                boolean r49 = java.lang.Boolean.parseBoolean(r9)
                r19 = r0
                r15 = r37
                goto L_0x0338
            L_0x0204:
                int r6 = java.lang.Integer.parseInt(r9)
                r19 = r0
                r13 = r6
                r15 = r37
                goto L_0x0338
            L_0x020f:
                int r48 = java.lang.Integer.parseInt(r9)
                r19 = r0
                r15 = r37
                goto L_0x0338
            L_0x0219:
                int r47 = java.lang.Integer.parseInt(r9)
                r19 = r0
                r15 = r37
                goto L_0x0338
            L_0x0223:
                android.graphics.Rect r6 = android.graphics.Rect.unflattenFromString(r9)
                r19 = r0
                r12 = r6
                r15 = r37
                goto L_0x0338
            L_0x022e:
                boolean r6 = java.lang.Boolean.parseBoolean(r9)
                r19 = r0
                r30 = r6
                r15 = r37
                goto L_0x0338
            L_0x023a:
                int r5 = java.lang.Integer.parseInt(r9)
                r19 = r0
                r15 = r37
                goto L_0x0338
            L_0x0244:
                r51 = r9
                r19 = r0
                r15 = r37
                goto L_0x0338
            L_0x024c:
                int r46 = java.lang.Integer.parseInt(r9)
                r19 = r0
                r15 = r37
                goto L_0x0338
            L_0x0256:
                int r41 = java.lang.Integer.parseInt(r9)
                r19 = r0
                r15 = r37
                goto L_0x0338
            L_0x0260:
                int r45 = java.lang.Integer.parseInt(r9)
                r19 = r0
                r15 = r37
                goto L_0x0338
            L_0x026a:
                int r43 = java.lang.Integer.parseInt(r9)
                r19 = r0
                r15 = r37
                goto L_0x0338
            L_0x0274:
                int r40 = java.lang.Integer.parseInt(r9)
                r19 = r0
                r15 = r37
                goto L_0x0338
            L_0x027e:
                boolean r38 = java.lang.Boolean.parseBoolean(r9)
                r19 = r0
                r15 = r37
                goto L_0x0338
            L_0x0288:
                long r52 = java.lang.Long.parseLong(r9)
                r19 = r0
                r15 = r37
                goto L_0x0338
            L_0x0292:
                r50 = r9
                r19 = r0
                r15 = r37
                goto L_0x0338
            L_0x029a:
                int r6 = java.lang.Integer.parseInt(r9)
                r19 = r0
                r11 = r6
                r15 = r37
                goto L_0x0338
            L_0x02a5:
                int r6 = java.lang.Integer.parseInt(r9)
                r19 = r0
                r16 = r6
                r15 = r37
                goto L_0x0338
            L_0x02b1:
                boolean r59 = java.lang.Boolean.parseBoolean(r9)
                r19 = r0
                r15 = r37
                goto L_0x0338
            L_0x02bb:
                int r10 = java.lang.Integer.parseInt(r9)
                r19 = r0
                r15 = r37
                goto L_0x0338
            L_0x02c5:
                boolean r58 = java.lang.Boolean.parseBoolean(r9)
                r19 = r0
                r15 = r37
                goto L_0x0338
            L_0x02cf:
                boolean r57 = java.lang.Boolean.parseBoolean(r9)
                r19 = r0
                r15 = r37
                goto L_0x0338
            L_0x02d9:
                boolean r56 = java.lang.Boolean.parseBoolean(r9)
                r19 = r0
                r15 = r37
                goto L_0x0338
            L_0x02e2:
                r6 = r9
                r8 = 1
                r19 = r0
                r55 = r8
                r15 = r37
                r8 = r6
                goto L_0x0338
            L_0x02ec:
                r44 = r9
                r19 = r0
                r15 = r37
                goto L_0x0338
            L_0x02f3:
                android.content.ComponentName r42 = android.content.ComponentName.unflattenFromString(r9)
                r19 = r0
                r15 = r37
                goto L_0x0338
            L_0x02fc:
                java.lang.Boolean r6 = java.lang.Boolean.valueOf(r9)
                boolean r54 = r6.booleanValue()
                r19 = r0
                r15 = r37
                goto L_0x0338
            L_0x0309:
                android.content.ComponentName r39 = android.content.ComponentName.unflattenFromString(r9)
                r19 = r0
                r15 = r37
                goto L_0x0338
            L_0x0312:
                r6 = -1
                if (r14 != r6) goto L_0x0319
                int r14 = java.lang.Integer.parseInt(r9)
            L_0x0319:
                r19 = r0
                r15 = r37
                goto L_0x0338
            L_0x031e:
                r15 = r37
                r19 = r0
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                r21 = r9
                java.lang.String r9 = "TaskRecord: Unknown attribute="
                r0.append(r9)
                r0.append(r7)
                java.lang.String r0 = r0.toString()
                android.util.Slog.w(r6, r0)
            L_0x0338:
                int r4 = r4 + -1
                r37 = r15
                r0 = r19
                r15 = 1
                goto L_0x0085
            L_0x0341:
                r19 = r0
                r15 = r37
                r37 = r19
                r60 = r22
            L_0x0349:
                int r0 = r72.next()
                r9 = r0
                r4 = 1
                if (r0 == r4) goto L_0x03a5
                r4 = 3
                if (r9 != r4) goto L_0x035e
                int r0 = r72.getDepth()
                if (r0 < r2) goto L_0x035b
                goto L_0x035e
            L_0x035b:
                r7 = r71
                goto L_0x03a7
            L_0x035e:
                r4 = 2
                if (r9 != r4) goto L_0x03a2
                java.lang.String r0 = r72.getName()
                java.lang.String r4 = "affinity_intent"
                boolean r4 = r4.equals(r0)
                if (r4 == 0) goto L_0x0376
                android.content.Intent r4 = android.content.Intent.restoreFromXml(r72)
                r7 = r71
                r60 = r4
                goto L_0x03a1
            L_0x0376:
                java.lang.String r4 = "intent"
                boolean r4 = r4.equals(r0)
                if (r4 == 0) goto L_0x0387
                android.content.Intent r4 = android.content.Intent.restoreFromXml(r72)
                r7 = r71
                r37 = r4
                goto L_0x03a1
            L_0x0387:
                java.lang.String r4 = "activity"
                boolean r4 = r4.equals(r0)
                if (r4 == 0) goto L_0x039c
                com.android.server.wm.ActivityRecord r4 = com.android.server.wm.ActivityRecord.restoreFromXml(r72, r73)
                if (r4 == 0) goto L_0x0399
                r3.add(r4)
            L_0x0399:
                r7 = r71
                goto L_0x03a1
            L_0x039c:
                r7 = r71
                r7.handleUnknownTag(r0, r1)
            L_0x03a1:
                goto L_0x0349
            L_0x03a2:
                r7 = r71
                goto L_0x0349
            L_0x03a5:
                r7 = r71
            L_0x03a7:
                if (r55 != 0) goto L_0x03ae
                r0 = r44
                r61 = r0
                goto L_0x03bc
            L_0x03ae:
                java.lang.String r0 = "@"
                boolean r0 = r0.equals(r8)
                if (r0 == 0) goto L_0x03ba
                r0 = 0
                r61 = r0
                goto L_0x03bc
            L_0x03ba:
                r61 = r8
            L_0x03bc:
                if (r16 > 0) goto L_0x0412
                if (r37 == 0) goto L_0x03c3
                r0 = r37
                goto L_0x03c5
            L_0x03c3:
                r0 = r60
            L_0x03c5:
                r4 = r0
                r8 = 0
                if (r4 == 0) goto L_0x03ea
                android.content.pm.IPackageManager r1 = android.app.AppGlobals.getPackageManager()
                android.content.ComponentName r0 = r4.getComponent()     // Catch:{ RemoteException -> 0x03e6 }
                java.lang.String r0 = r0.getPackageName()     // Catch:{ RemoteException -> 0x03e6 }
                r62 = r2
                r2 = 8704(0x2200, float:1.2197E-41)
                android.content.pm.ApplicationInfo r0 = r1.getApplicationInfo(r0, r2, r10)     // Catch:{ RemoteException -> 0x03e4 }
                if (r0 == 0) goto L_0x03e3
                int r2 = r0.uid     // Catch:{ RemoteException -> 0x03e4 }
                r8 = r2
            L_0x03e3:
                goto L_0x03ec
            L_0x03e4:
                r0 = move-exception
                goto L_0x03ec
            L_0x03e6:
                r0 = move-exception
                r62 = r2
                goto L_0x03ec
            L_0x03ea:
                r62 = r2
            L_0x03ec:
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r1 = "Updating task #"
                r0.append(r1)
                r0.append(r14)
                java.lang.String r1 = " for "
                r0.append(r1)
                r0.append(r4)
                java.lang.String r1 = ": effectiveUid="
                r0.append(r1)
                r0.append(r8)
                java.lang.String r0 = r0.toString()
                android.util.Slog.w(r6, r0)
                r0 = r8
                goto L_0x0416
            L_0x0412:
                r62 = r2
                r0 = r16
            L_0x0416:
                r1 = 1
                if (r13 >= r1) goto L_0x0422
                if (r11 != r1) goto L_0x042a
                r2 = 2
                if (r5 != r2) goto L_0x042a
                r2 = 1
                r63 = r30
                goto L_0x042d
            L_0x0422:
                r2 = 3
                if (r5 != r2) goto L_0x042a
                r2 = 2
                r4 = 1
                r63 = r4
                goto L_0x042d
            L_0x042a:
                r2 = r5
                r63 = r30
            L_0x042d:
                r8 = r73
                com.android.server.wm.ActivityTaskManagerService r5 = r8.mService
                r4 = r71
                r6 = r14
                r7 = r37
                r8 = r60
                r64 = r9
                r9 = r44
                r65 = r10
                r10 = r61
                r66 = r11
                r11 = r39
                r1 = r12
                r12 = r42
                r67 = r13
                r13 = r56
                r68 = r14
                r14 = r57
                r69 = r15
                r70 = 1
                r15 = r58
                r16 = r65
                r17 = r0
                r18 = r50
                r19 = r3
                r20 = r52
                r22 = r38
                r23 = r69
                r24 = r40
                r25 = r43
                r26 = r45
                r27 = r41
                r28 = r46
                r29 = r51
                r30 = r2
                r31 = r63
                r32 = r54
                r33 = r59
                r34 = r47
                r35 = r48
                r36 = r49
                com.android.server.wm.TaskRecord r4 = r4.create(r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20, r22, r23, r24, r25, r26, r27, r28, r29, r30, r31, r32, r33, r34, r35, r36)
                r4.mLastNonFullscreenBounds = r1
                r4.setBounds(r1)
                int r5 = r3.size()
                int r5 = r5 + -1
            L_0x048c:
                if (r5 < 0) goto L_0x049a
                java.lang.Object r6 = r3.get(r5)
                com.android.server.wm.ActivityRecord r6 = (com.android.server.wm.ActivityRecord) r6
                r6.setTask(r4)
                int r5 = r5 + -1
                goto L_0x048c
            L_0x049a:
                return r4
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.TaskRecord.TaskRecordFactory.restoreFromXml(org.xmlpull.v1.XmlPullParser, com.android.server.wm.ActivityStackSupervisor):com.android.server.wm.TaskRecord");
        }

        /* access modifiers changed from: package-private */
        public void handleUnknownTag(String name, XmlPullParser in) throws IOException, XmlPullParserException {
            Slog.e("ActivityTaskManager", "restoreTask: Unexpected name=" + name);
            XmlUtils.skipCurrentTag(in);
        }
    }
}
