package com.android.server.wm;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.AppGlobals;
import android.app.WindowConfiguration;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManagerInternal;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.Trace;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.service.voice.IVoiceInteractionSession;
import android.util.ArraySet;
import android.util.IntArray;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.TimeUtils;
import android.util.proto.ProtoOutputStream;
import android.view.Display;
import android.view.IApplicationToken;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.IVoiceInteractor;
import com.android.internal.app.ResolverActivity;
import com.android.server.LocalServices;
import com.android.server.UiModeManagerService;
import com.android.server.am.AppTimeTracker;
import com.android.server.am.UserState;
import com.android.server.wm.ActivityStack;
import com.android.server.wm.ActivityTaskManagerInternal;
import com.android.server.wm.LaunchParamsController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class RootActivityContainer extends ConfigurationContainer implements DisplayManager.DisplayListener {
    static final int MATCH_TASK_IN_STACKS_ONLY = 0;
    static final int MATCH_TASK_IN_STACKS_OR_RECENT_TASKS = 1;
    static final int MATCH_TASK_IN_STACKS_OR_RECENT_TASKS_AND_RESTORE = 2;
    private static final String TAG = "ActivityTaskManager";
    private static final String TAG_RECENTS = "ActivityTaskManager";
    private static final String TAG_RELEASE = "ActivityTaskManager";
    static final String TAG_STATES = "ActivityTaskManager";
    static final String TAG_TASKS = "ActivityTaskManager";
    private final ArrayList<ActivityDisplay> mActivityDisplays = new ArrayList<>();
    int mCurrentUser;
    private ActivityDisplay mDefaultDisplay;
    int mDefaultMinSizeOfResizeableTaskDp = -1;
    private final SparseArray<IntArray> mDisplayAccessUIDs = new SparseArray<>();
    DisplayManager mDisplayManager;
    private DisplayManagerInternal mDisplayManagerInternal;
    boolean mIsDockMinimized;
    private boolean mPowerHintSent;
    private RootWindowContainer mRootWindowContainer;
    ActivityTaskManagerService mService;
    final ArrayList<ActivityTaskManagerInternal.SleepToken> mSleepTokens = new ArrayList<>();
    ActivityStackSupervisor mStackSupervisor;
    private boolean mTaskLayersChanged = true;
    private final ArrayList<ActivityRecord> mTmpActivityList = new ArrayList<>();
    private final FindTaskResult mTmpFindTaskResult = new FindTaskResult();
    SparseIntArray mUserStackInFront = new SparseIntArray(2);
    WindowManagerService mWindowManager;

    @Retention(RetentionPolicy.SOURCE)
    public @interface AnyTaskForIdMatchTaskMode {
    }

    static class FindTaskResult {
        boolean mIdealMatch;
        ActivityRecord mRecord;

        FindTaskResult() {
        }

        /* access modifiers changed from: package-private */
        public void clear() {
            this.mRecord = null;
            this.mIdealMatch = false;
        }

        /* access modifiers changed from: package-private */
        public void setTo(FindTaskResult result) {
            this.mRecord = result.mRecord;
            this.mIdealMatch = result.mIdealMatch;
        }
    }

    RootActivityContainer(ActivityTaskManagerService service) {
        this.mService = service;
        this.mStackSupervisor = service.mStackSupervisor;
        this.mStackSupervisor.mRootActivityContainer = this;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setWindowContainer(RootWindowContainer container) {
        this.mRootWindowContainer = container;
        this.mRootWindowContainer.setRootActivityContainer(this);
    }

    /* access modifiers changed from: package-private */
    public void setWindowManager(WindowManagerService wm) {
        this.mWindowManager = wm;
        setWindowContainer(this.mWindowManager.mRoot);
        this.mDisplayManager = (DisplayManager) this.mService.mContext.getSystemService(DisplayManager.class);
        this.mDisplayManager.registerDisplayListener(this, this.mService.mUiHandler);
        this.mDisplayManagerInternal = (DisplayManagerInternal) LocalServices.getService(DisplayManagerInternal.class);
        Display[] displays = this.mDisplayManager.getDisplays();
        for (Display display : displays) {
            ActivityDisplay activityDisplay = new ActivityDisplay(this, display);
            if (activityDisplay.mDisplayId == 0) {
                this.mDefaultDisplay = activityDisplay;
            }
            addChild(activityDisplay, Integer.MAX_VALUE);
        }
        calculateDefaultMinimalSizeOfResizeableTasks();
        ActivityDisplay defaultDisplay = getDefaultDisplay();
        defaultDisplay.getOrCreateStack(1, 2, true);
        positionChildAt(defaultDisplay, Integer.MAX_VALUE);
    }

    /* access modifiers changed from: package-private */
    public ActivityDisplay getDefaultDisplay() {
        return this.mDefaultDisplay;
    }

    /* access modifiers changed from: package-private */
    public ActivityDisplay getActivityDisplay(String uniqueId) {
        for (int i = this.mActivityDisplays.size() - 1; i >= 0; i--) {
            ActivityDisplay display = this.mActivityDisplays.get(i);
            if (display.mDisplay.isValid() && display.mDisplay.getUniqueId().equals(uniqueId)) {
                return display;
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public ActivityDisplay getActivityDisplay(int displayId) {
        for (int i = this.mActivityDisplays.size() - 1; i >= 0; i--) {
            ActivityDisplay activityDisplay = this.mActivityDisplays.get(i);
            if (activityDisplay.mDisplayId == displayId) {
                return activityDisplay;
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public ActivityDisplay getActivityDisplayOrCreate(int displayId) {
        Display display;
        ActivityDisplay activityDisplay = getActivityDisplay(displayId);
        if (activityDisplay != null) {
            return activityDisplay;
        }
        DisplayManager displayManager = this.mDisplayManager;
        if (displayManager == null || (display = displayManager.getDisplay(displayId)) == null) {
            return null;
        }
        ActivityDisplay activityDisplay2 = new ActivityDisplay(this, display);
        addChild(activityDisplay2, Integer.MIN_VALUE);
        return activityDisplay2;
    }

    /* access modifiers changed from: package-private */
    public boolean isDisplayAdded(int displayId) {
        return getActivityDisplayOrCreate(displayId) != null;
    }

    /* access modifiers changed from: package-private */
    public ActivityRecord getDefaultDisplayHomeActivity() {
        return getDefaultDisplayHomeActivityForUser(this.mCurrentUser);
    }

    /* access modifiers changed from: package-private */
    public ActivityRecord getDefaultDisplayHomeActivityForUser(int userId) {
        return getActivityDisplay(0).getHomeActivityForUser(userId);
    }

    /* access modifiers changed from: package-private */
    public boolean startHomeOnAllDisplays(int userId, String reason) {
        boolean homeStarted = false;
        for (int i = this.mActivityDisplays.size() - 1; i >= 0; i--) {
            homeStarted |= startHomeOnDisplay(userId, reason, this.mActivityDisplays.get(i).mDisplayId);
        }
        return homeStarted;
    }

    /* access modifiers changed from: package-private */
    public void startHomeOnEmptyDisplays(String reason) {
        for (int i = this.mActivityDisplays.size() - 1; i >= 0; i--) {
            ActivityDisplay display = this.mActivityDisplays.get(i);
            if (display.topRunningActivity() == null) {
                startHomeOnDisplay(this.mCurrentUser, reason, display.mDisplayId);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean startHomeOnDisplay(int userId, String reason, int displayId) {
        return startHomeOnDisplay(userId, reason, displayId, false, false);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v7, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v2, resolved type: android.content.pm.ActivityInfo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v8, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: android.content.Intent} */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean startHomeOnDisplay(int r7, java.lang.String r8, int r9, boolean r10, boolean r11) {
        /*
            r6 = this;
            r0 = -1
            if (r9 != r0) goto L_0x0009
            com.android.server.wm.ActivityStack r0 = r6.getTopDisplayFocusedStack()
            int r9 = r0.mDisplayId
        L_0x0009:
            r0 = 0
            r1 = 0
            if (r9 != 0) goto L_0x0018
            com.android.server.wm.ActivityTaskManagerService r2 = r6.mService
            android.content.Intent r0 = r2.getHomeIntent()
            android.content.pm.ActivityInfo r1 = r6.resolveHomeActivity(r7, r0)
            goto L_0x002c
        L_0x0018:
            boolean r2 = r6.shouldPlaceSecondaryHomeOnDisplay(r9)
            if (r2 == 0) goto L_0x002c
            android.util.Pair r2 = r6.resolveSecondaryHomeActivity(r7, r9)
            java.lang.Object r3 = r2.first
            r1 = r3
            android.content.pm.ActivityInfo r1 = (android.content.pm.ActivityInfo) r1
            java.lang.Object r3 = r2.second
            r0 = r3
            android.content.Intent r0 = (android.content.Intent) r0
        L_0x002c:
            r2 = 0
            if (r1 == 0) goto L_0x008b
            if (r0 != 0) goto L_0x0032
            goto L_0x008b
        L_0x0032:
            boolean r3 = r6.canStartHomeOnDisplay(r1, r9, r10)
            if (r3 != 0) goto L_0x0039
            return r2
        L_0x0039:
            android.content.ComponentName r2 = new android.content.ComponentName
            android.content.pm.ApplicationInfo r3 = r1.applicationInfo
            java.lang.String r3 = r3.packageName
            java.lang.String r4 = r1.name
            r2.<init>(r3, r4)
            r0.setComponent(r2)
            int r2 = r0.getFlags()
            r3 = 268435456(0x10000000, float:2.5243549E-29)
            r2 = r2 | r3
            r0.setFlags(r2)
            r2 = 1
            if (r11 == 0) goto L_0x0059
            java.lang.String r3 = "android.intent.extra.FROM_HOME_KEY"
            r0.putExtra(r3, r2)
        L_0x0059:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r8)
            java.lang.String r4 = ":"
            r3.append(r4)
            r3.append(r7)
            r3.append(r4)
            android.content.pm.ApplicationInfo r5 = r1.applicationInfo
            int r5 = r5.uid
            int r5 = android.os.UserHandle.getUserId(r5)
            r3.append(r5)
            r3.append(r4)
            r3.append(r9)
            java.lang.String r3 = r3.toString()
            com.android.server.wm.ActivityTaskManagerService r4 = r6.mService
            com.android.server.wm.ActivityStartController r4 = r4.getActivityStartController()
            r4.startHomeActivity(r0, r1, r3, r9)
            return r2
        L_0x008b:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.RootActivityContainer.startHomeOnDisplay(int, java.lang.String, int, boolean, boolean):boolean");
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public ActivityInfo resolveHomeActivity(int userId, Intent homeIntent) {
        ComponentName comp = homeIntent.getComponent();
        ActivityInfo aInfo = null;
        if (comp != null) {
            try {
                aInfo = AppGlobals.getPackageManager().getActivityInfo(comp, 1024, userId);
            } catch (RemoteException e) {
            }
        } else {
            ResolveInfo info = AppGlobals.getPackageManager().resolveIntent(homeIntent, homeIntent.resolveTypeIfNeeded(this.mService.mContext.getContentResolver()), 1024, userId);
            if (info != null) {
                aInfo = info.activityInfo;
            }
        }
        if (aInfo == null) {
            Slog.wtf("ActivityTaskManager", "No home screen found for " + homeIntent, new Throwable());
            return null;
        }
        ActivityInfo aInfo2 = new ActivityInfo(aInfo);
        aInfo2.applicationInfo = this.mService.getAppInfoForUser(aInfo2.applicationInfo, userId);
        return aInfo2;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public Pair<ActivityInfo, Intent> resolveSecondaryHomeActivity(int userId, int displayId) {
        if (displayId != 0) {
            Intent homeIntent = this.mService.getHomeIntent();
            ActivityInfo aInfo = resolveHomeActivity(userId, homeIntent);
            if (aInfo != null) {
                if (ResolverActivity.class.getName().equals(aInfo.name)) {
                    aInfo = null;
                } else {
                    homeIntent = this.mService.getSecondaryHomeIntent(aInfo.applicationInfo.packageName);
                    List<ResolveInfo> resolutions = resolveActivities(userId, homeIntent);
                    int size = resolutions.size();
                    String targetName = aInfo.name;
                    aInfo = null;
                    int i = 0;
                    while (true) {
                        if (i >= size) {
                            break;
                        }
                        ResolveInfo resolveInfo = resolutions.get(i);
                        if (resolveInfo.activityInfo.name.equals(targetName)) {
                            aInfo = resolveInfo.activityInfo;
                            break;
                        }
                        i++;
                    }
                    if (aInfo == null && size > 0) {
                        aInfo = resolutions.get(0).activityInfo;
                    }
                }
            }
            if (aInfo != null && !canStartHomeOnDisplay(aInfo, displayId, false)) {
                aInfo = null;
            }
            if (aInfo == null) {
                homeIntent = this.mService.getSecondaryHomeIntent((String) null);
                aInfo = resolveHomeActivity(userId, homeIntent);
            }
            return Pair.create(aInfo, homeIntent);
        }
        throw new IllegalArgumentException("resolveSecondaryHomeActivity: Should not be DEFAULT_DISPLAY");
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public List<ResolveInfo> resolveActivities(int userId, Intent homeIntent) {
        try {
            return AppGlobals.getPackageManager().queryIntentActivities(homeIntent, homeIntent.resolveTypeIfNeeded(this.mService.mContext.getContentResolver()), 1024, userId).getList();
        } catch (RemoteException e) {
            return new ArrayList<>();
        }
    }

    /* access modifiers changed from: package-private */
    public boolean resumeHomeActivity(ActivityRecord prev, String reason, int displayId) {
        if (!this.mService.isBooting() && !this.mService.isBooted()) {
            return false;
        }
        if (displayId == -1) {
            displayId = 0;
        }
        ActivityRecord r = getActivityDisplay(displayId).getHomeActivity();
        String myReason = reason + " resumeHomeActivity";
        if (r == null || r.finishing || r.mIsCastMode) {
            return startHomeOnDisplay(this.mCurrentUser, myReason, displayId);
        }
        r.moveFocusableActivityToTop(myReason);
        return resumeFocusedStacksTopActivities(r.getActivityStack(), prev, (ActivityOptions) null);
    }

    /* access modifiers changed from: package-private */
    public boolean shouldPlaceSecondaryHomeOnDisplay(int displayId) {
        ActivityDisplay display;
        if (displayId == 0) {
            throw new IllegalArgumentException("shouldPlaceSecondaryHomeOnDisplay: Should not be DEFAULT_DISPLAY");
        } else if (displayId == -1 || !this.mService.mSupportsMultiDisplay) {
            return false;
        } else {
            if ((Settings.Global.getInt(this.mService.mContext.getContentResolver(), "device_provisioned", 0) != 0) && StorageManager.isUserKeyUnlocked(this.mCurrentUser) && (display = getActivityDisplay(displayId)) != null && !display.isRemoved() && display.supportsSystemDecorations()) {
                return true;
            }
            return false;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean canStartHomeOnDisplay(ActivityInfo homeInfo, int displayId, boolean allowInstrumenting) {
        if (this.mService.mFactoryTest == 1 && this.mService.mTopAction == null) {
            return false;
        }
        WindowProcessController app = this.mService.getProcessController(homeInfo.processName, homeInfo.applicationInfo.uid);
        if (!allowInstrumenting && app != null && app.isInstrumenting()) {
            return false;
        }
        if (displayId == 0 || (displayId != -1 && displayId == this.mService.mVr2dDisplayId)) {
            return true;
        }
        if (!shouldPlaceSecondaryHomeOnDisplay(displayId)) {
            return false;
        }
        return homeInfo.launchMode != 2 && homeInfo.launchMode != 3;
    }

    /* access modifiers changed from: package-private */
    public boolean ensureVisibilityAndConfig(ActivityRecord starting, int displayId, boolean markFrozenIfConfigChanged, boolean deferResume) {
        IApplicationToken.Stub stub = null;
        ensureActivitiesVisible((ActivityRecord) null, 0, false, false);
        if (displayId == -1) {
            return true;
        }
        DisplayContent displayContent = this.mRootWindowContainer.getDisplayContent(displayId);
        Configuration config = null;
        if (displayContent != null) {
            Configuration displayOverrideConfiguration = getDisplayOverrideConfiguration(displayId);
            if (starting != null && starting.mayFreezeScreenLocked(starting.app)) {
                stub = starting.appToken;
            }
            config = displayContent.updateOrientationFromAppTokens(displayOverrideConfiguration, stub, true);
        }
        if (!(starting == null || !markFrozenIfConfigChanged || config == null)) {
            starting.frozenBeforeDestroy = true;
        }
        return this.mService.updateDisplayOverrideConfigurationLocked(config, starting, deferResume, displayId);
    }

    /* access modifiers changed from: package-private */
    public List<IBinder> getTopVisibleActivities() {
        ActivityRecord top;
        ArrayList<IBinder> topActivityTokens = new ArrayList<>();
        ActivityStack topFocusedStack = getTopDisplayFocusedStack();
        for (int i = this.mActivityDisplays.size() - 1; i >= 0; i--) {
            ActivityDisplay display = this.mActivityDisplays.get(i);
            for (int j = display.getChildCount() - 1; j >= 0; j--) {
                ActivityStack stack = display.getChildAt(j);
                if (stack.shouldBeVisible((ActivityRecord) null) && (top = stack.getTopActivity()) != null) {
                    if (stack == topFocusedStack) {
                        topActivityTokens.add(0, top.appToken);
                    } else {
                        topActivityTokens.add(top.appToken);
                    }
                }
            }
        }
        return topActivityTokens;
    }

    public ActivityStack getTopDisplayFocusedStack() {
        for (int i = this.mActivityDisplays.size() - 1; i >= 0; i--) {
            ActivityStack focusedStack = this.mActivityDisplays.get(i).getFocusedStack();
            if (focusedStack != null) {
                return focusedStack;
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public ActivityRecord getTopResumedActivity() {
        ActivityStack focusedStack = getTopDisplayFocusedStack();
        if (focusedStack == null) {
            return null;
        }
        ActivityRecord resumedActivity = focusedStack.getResumedActivity();
        if (resumedActivity != null && resumedActivity.app != null) {
            return resumedActivity;
        }
        for (int i = this.mActivityDisplays.size() - 1; i >= 0; i--) {
            ActivityRecord resumedActivityOnDisplay = this.mActivityDisplays.get(i).getResumedActivity();
            if (resumedActivityOnDisplay != null) {
                return resumedActivityOnDisplay;
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public boolean isFocusable(ConfigurationContainer container, boolean alwaysFocusable) {
        if (container.inSplitScreenPrimaryWindowingMode() && this.mIsDockMinimized) {
            return false;
        }
        if (container.getWindowConfiguration().canReceiveKeys() || alwaysFocusable) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean isTopDisplayFocusedStack(ActivityStack stack) {
        return stack != null && stack == getTopDisplayFocusedStack();
    }

    /* access modifiers changed from: package-private */
    public void updatePreviousProcess(ActivityRecord r) {
        WindowProcessController fgApp = null;
        for (int displayNdx = this.mActivityDisplays.size() - 1; displayNdx >= 0; displayNdx--) {
            ActivityDisplay display = this.mActivityDisplays.get(displayNdx);
            int stackNdx = display.getChildCount() - 1;
            while (true) {
                if (stackNdx < 0) {
                    break;
                }
                ActivityStack stack = display.getChildAt(stackNdx);
                if (isTopDisplayFocusedStack(stack)) {
                    ActivityRecord resumedActivity = stack.getResumedActivity();
                    if (resumedActivity != null) {
                        fgApp = resumedActivity.app;
                    } else if (stack.mPausingActivity != null) {
                        fgApp = stack.mPausingActivity.app;
                    }
                } else {
                    stackNdx--;
                }
            }
        }
        if (r.hasProcess() != 0 && fgApp != null && r.app != fgApp && r.lastVisibleTime > this.mService.mPreviousProcessVisibleTime && r.app != this.mService.mHomeProcess) {
            this.mService.mPreviousProcess = r.app;
            this.mService.mPreviousProcessVisibleTime = r.lastVisibleTime;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean attachApplication(WindowProcessController app) throws RemoteException {
        String processName = app.mName;
        boolean didSomething = false;
        for (int displayNdx = this.mActivityDisplays.size() - 1; displayNdx >= 0; displayNdx--) {
            ActivityStack stack = this.mActivityDisplays.get(displayNdx).getFocusedStack();
            if (stack != null) {
                stack.getAllRunningVisibleActivitiesLocked(this.mTmpActivityList);
                ActivityRecord top = stack.topRunningActivityLocked();
                int size = this.mTmpActivityList.size();
                for (int i = 0; i < size; i++) {
                    ActivityRecord activity = this.mTmpActivityList.get(i);
                    if (activity.app == null && app.mUid == activity.info.applicationInfo.uid && processName.equals(activity.processName)) {
                        try {
                            if (this.mStackSupervisor.realStartActivityLocked(activity, app, top == activity, true)) {
                                didSomething = true;
                            }
                        } catch (RemoteException e) {
                            Slog.w("ActivityTaskManager", "Exception in new application when starting activity " + top.intent.getComponent().flattenToShortString(), e);
                            throw e;
                        }
                    }
                }
            }
        }
        if (!didSomething) {
            ensureActivitiesVisible((ActivityRecord) null, 0, false);
        }
        return didSomething;
    }

    /* access modifiers changed from: package-private */
    public void ensureActivitiesVisible(ActivityRecord starting, int configChanges, boolean preserveWindows) {
        ensureActivitiesVisible(starting, configChanges, preserveWindows, true);
    }

    /* access modifiers changed from: package-private */
    public void ensureActivitiesVisible(ActivityRecord starting, int configChanges, boolean preserveWindows, boolean notifyClients) {
        this.mStackSupervisor.getKeyguardController().beginActivityVisibilityUpdate();
        try {
            for (int displayNdx = this.mActivityDisplays.size() - 1; displayNdx >= 0; displayNdx--) {
                this.mActivityDisplays.get(displayNdx).ensureActivitiesVisible(starting, configChanges, preserveWindows, notifyClients);
            }
        } finally {
            this.mStackSupervisor.getKeyguardController().endActivityVisibilityUpdate();
        }
    }

    /* access modifiers changed from: package-private */
    public boolean switchUser(int userId, UserState uss) {
        int focusStackId = getTopDisplayFocusedStack().getStackId();
        ActivityStack dockedStack = getDefaultDisplay().getSplitScreenPrimaryStack();
        if (dockedStack != null) {
            this.mStackSupervisor.moveTasksToFullscreenStackLocked(dockedStack, dockedStack.isFocusedStackOnDisplay());
        }
        removeStacksInWindowingModes(2);
        this.mUserStackInFront.put(this.mCurrentUser, focusStackId);
        int restoreStackId = this.mUserStackInFront.get(userId, getDefaultDisplay().getHomeStack().mStackId);
        this.mCurrentUser = userId;
        this.mStackSupervisor.mStartingUsers.add(uss);
        for (int displayNdx = this.mActivityDisplays.size() - 1; displayNdx >= 0; displayNdx--) {
            ActivityDisplay display = this.mActivityDisplays.get(displayNdx);
            for (int stackNdx = display.getChildCount() - 1; stackNdx >= 0; stackNdx--) {
                ActivityStack stack = display.getChildAt(stackNdx);
                stack.switchUserLocked(userId);
                TaskRecord task = stack.topTask();
                if (task != null) {
                    stack.positionChildWindowContainerAtTop(task);
                }
            }
        }
        ActivityStack stack2 = getStack(restoreStackId);
        if (stack2 == null) {
            stack2 = getDefaultDisplay().getHomeStack();
        }
        boolean homeInFront = stack2.isActivityTypeHome();
        if (stack2.isOnHomeDisplay()) {
            stack2.moveToFront("switchUserOnHomeDisplay");
        } else {
            resumeHomeActivity((ActivityRecord) null, "switchUserOnOtherDisplay", 0);
        }
        return homeInFront;
    }

    /* access modifiers changed from: package-private */
    public void removeUser(int userId) {
        this.mUserStackInFront.delete(userId);
    }

    /* access modifiers changed from: package-private */
    public void updateUserStack(int userId, ActivityStack stack) {
        int i;
        if (userId != this.mCurrentUser) {
            SparseIntArray sparseIntArray = this.mUserStackInFront;
            if (stack != null) {
                i = stack.getStackId();
            } else {
                i = getDefaultDisplay().getHomeStack().mStackId;
            }
            sparseIntArray.put(userId, i);
        }
    }

    /* access modifiers changed from: package-private */
    public void resizeStack(ActivityStack stack, Rect bounds, Rect tempTaskBounds, Rect tempTaskInsetBounds, boolean preserveWindows, boolean allowResizeInDockedMode, boolean deferResume) {
        ActivityStack activityStack = stack;
        if (stack.inSplitScreenPrimaryWindowingMode()) {
            this.mStackSupervisor.resizeDockedStackLocked(bounds, tempTaskBounds, tempTaskInsetBounds, (Rect) null, (Rect) null, preserveWindows, deferResume);
            return;
        }
        boolean splitScreenActive = getDefaultDisplay().hasSplitScreenPrimaryStack();
        if (allowResizeInDockedMode || stack.getWindowConfiguration().tasksAreFloating() || !splitScreenActive) {
            Trace.traceBegin(64, "am.resizeStack_" + activityStack.mStackId);
            this.mWindowManager.deferSurfaceLayout();
            try {
                if (stack.affectedBySplitScreenResize()) {
                    if (bounds == null && stack.inSplitScreenWindowingMode()) {
                        stack.setWindowingMode(1);
                    } else if (splitScreenActive) {
                        stack.setWindowingMode(4);
                    }
                }
                stack.resize(bounds, tempTaskBounds, tempTaskInsetBounds);
                if (!deferResume) {
                    try {
                        stack.ensureVisibleActivitiesConfigurationLocked(stack.topRunningActivityLocked(), preserveWindows);
                    } catch (Throwable th) {
                        th = th;
                    }
                } else {
                    boolean z = preserveWindows;
                }
                this.mWindowManager.continueSurfaceLayout();
                Trace.traceEnd(64);
            } catch (Throwable th2) {
                th = th2;
                boolean z2 = preserveWindows;
                this.mWindowManager.continueSurfaceLayout();
                Trace.traceEnd(64);
                throw th;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void moveStackToDisplay(int stackId, int displayId, boolean onTop) {
        ActivityDisplay activityDisplay = getActivityDisplayOrCreate(displayId);
        if (activityDisplay != null) {
            ActivityStack stack = getStack(stackId);
            if (stack != null) {
                ActivityDisplay currentDisplay = stack.getDisplay();
                if (currentDisplay == null) {
                    throw new IllegalStateException("moveStackToDisplay: Stack with stack=" + stack + " is not attached to any display.");
                } else if (currentDisplay.mDisplayId == displayId) {
                    throw new IllegalArgumentException("Trying to move stack=" + stack + " to its current displayId=" + displayId);
                } else if (!activityDisplay.isSingleTaskInstance() || activityDisplay.getChildCount() <= 0) {
                    stack.reparent(activityDisplay, onTop, false);
                } else {
                    Slog.e("ActivityTaskManager", "Can not move stack=" + stack + " to single task instance display=" + activityDisplay);
                }
            } else {
                throw new IllegalArgumentException("moveStackToDisplay: Unknown stackId=" + stackId);
            }
        } else {
            throw new IllegalArgumentException("moveStackToDisplay: Unknown displayId=" + displayId);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean moveTopStackActivityToPinnedStack(int stackId) {
        ActivityStack stack = getStack(stackId);
        if (stack != null) {
            ActivityRecord r = stack.topRunningActivityLocked();
            if (r == null) {
                Slog.w("ActivityTaskManager", "moveTopStackActivityToPinnedStack: No top running activity in stack=" + stack);
                return false;
            } else if (this.mService.mForceResizableActivities || r.supportsPictureInPicture()) {
                moveActivityToPinnedStack(r, (Rect) null, 0.0f, "moveTopActivityToPinnedStack");
                return true;
            } else {
                Slog.w("ActivityTaskManager", "moveTopStackActivityToPinnedStack: Picture-In-Picture not supported for  r=" + r);
                return false;
            }
        } else {
            throw new IllegalArgumentException("moveTopStackActivityToPinnedStack: Unknown stackId=" + stackId);
        }
    }

    /* access modifiers changed from: package-private */
    public void moveActivityToPinnedStack(ActivityRecord r, Rect sourceHintBounds, float aspectRatio, String reason) {
        ActivityStack stack;
        ActivityRecord activityRecord = r;
        this.mWindowManager.deferSurfaceLayout();
        ActivityDisplay display = r.getActivityStack().getDisplay();
        ActivityStack stack2 = display.getPinnedStack();
        if (stack2 != null) {
            this.mStackSupervisor.moveTasksToFullscreenStackLocked(stack2, false);
        }
        ActivityStack stack3 = display.getOrCreateStack(2, r.getActivityType(), true);
        Rect destBounds = stack3.getDefaultPictureInPictureBounds(aspectRatio);
        try {
            TaskRecord task = r.getTaskRecord();
            Rect destBounds2 = destBounds;
            try {
                resizeStack(stack3, task.getRequestedOverrideBounds(), (Rect) null, (Rect) null, false, true, false);
                if (task.mActivities.size() == 1) {
                    stack = stack3;
                    try {
                        task.reparent(stack3, true, 0, false, true, false, reason);
                    } catch (Throwable th) {
                        th = th;
                        Rect rect = sourceHintBounds;
                        Rect rect2 = destBounds2;
                    }
                } else {
                    stack = stack3;
                    try {
                        TaskRecord newTask = task.getStack().createTaskRecord(this.mStackSupervisor.getNextTaskIdForUserLocked(activityRecord.mUserId), activityRecord.info, activityRecord.intent, (IVoiceInteractionSession) null, (IVoiceInteractor) null, true);
                        activityRecord.reparent(newTask, Integer.MAX_VALUE, "moveActivityToStack");
                        newTask.reparent(stack, true, 0, false, true, false, reason);
                    } catch (Throwable th2) {
                        th = th2;
                        Rect rect3 = sourceHintBounds;
                        Rect rect4 = destBounds2;
                        this.mWindowManager.continueSurfaceLayout();
                        throw th;
                    }
                }
                activityRecord.supportsEnterPipOnTaskSwitch = false;
                this.mWindowManager.continueSurfaceLayout();
                stack.animateResizePinnedStack(sourceHintBounds, destBounds2, -1, true);
                ensureActivitiesVisible((ActivityRecord) null, 0, false);
                resumeFocusedStacksTopActivities();
                this.mService.getTaskChangeNotificationController().notifyActivityPinned(activityRecord);
            } catch (Throwable th3) {
                th = th3;
                Rect rect5 = sourceHintBounds;
                ActivityStack activityStack = stack3;
                Rect rect6 = destBounds2;
                this.mWindowManager.continueSurfaceLayout();
                throw th;
            }
        } catch (Throwable th4) {
            th = th4;
            Rect rect7 = sourceHintBounds;
            Rect rect8 = destBounds;
            ActivityStack activityStack2 = stack3;
            this.mWindowManager.continueSurfaceLayout();
            throw th;
        }
    }

    /* access modifiers changed from: package-private */
    public void executeAppTransitionForAllDisplay() {
        for (int displayNdx = this.mActivityDisplays.size() - 1; displayNdx >= 0; displayNdx--) {
            this.mActivityDisplays.get(displayNdx).mDisplayContent.executeAppTransition();
        }
    }

    /* access modifiers changed from: package-private */
    public void setDockedStackMinimized(boolean minimized) {
        ActivityStack current = getTopDisplayFocusedStack();
        this.mIsDockMinimized = minimized;
        if (this.mIsDockMinimized && current.inSplitScreenPrimaryWindowingMode()) {
            current.adjustFocusToNextFocusableStack("setDockedStackMinimized");
        }
    }

    /* access modifiers changed from: package-private */
    public ActivityRecord findTask(ActivityRecord r, int preferredDisplayId) {
        this.mTmpFindTaskResult.clear();
        ActivityDisplay preferredDisplay = getActivityDisplay(preferredDisplayId);
        if (preferredDisplay != null) {
            preferredDisplay.findTaskLocked(r, true, this.mTmpFindTaskResult);
            if (this.mTmpFindTaskResult.mIdealMatch) {
                return this.mTmpFindTaskResult.mRecord;
            }
        }
        for (int displayNdx = this.mActivityDisplays.size() - 1; displayNdx >= 0; displayNdx--) {
            ActivityDisplay display = this.mActivityDisplays.get(displayNdx);
            if (display.mDisplayId != preferredDisplayId) {
                display.findTaskLocked(r, false, this.mTmpFindTaskResult);
                if (this.mTmpFindTaskResult.mIdealMatch) {
                    return this.mTmpFindTaskResult.mRecord;
                }
            }
        }
        return this.mTmpFindTaskResult.mRecord;
    }

    /* access modifiers changed from: package-private */
    public int finishTopCrashedActivities(WindowProcessController app, String reason) {
        TaskRecord finishedTask = null;
        ActivityStack focusedStack = getTopDisplayFocusedStack();
        for (int displayNdx = this.mActivityDisplays.size() - 1; displayNdx >= 0; displayNdx--) {
            ActivityDisplay display = this.mActivityDisplays.get(displayNdx);
            for (int stackNdx = 0; stackNdx < display.getChildCount(); stackNdx++) {
                ActivityStack stack = display.getChildAt(stackNdx);
                TaskRecord t = stack.finishTopCrashedActivityLocked(app, reason);
                if (stack == focusedStack || finishedTask == null) {
                    finishedTask = t;
                }
            }
        }
        if (finishedTask != null) {
            return finishedTask.taskId;
        }
        return -1;
    }

    /* access modifiers changed from: package-private */
    public boolean resumeFocusedStacksTopActivities() {
        return resumeFocusedStacksTopActivities((ActivityStack) null, (ActivityRecord) null, (ActivityOptions) null);
    }

    /* access modifiers changed from: package-private */
    public boolean resumeFocusedStacksTopActivities(ActivityStack targetStack, ActivityRecord target, ActivityOptions targetOptions) {
        ActivityStack focusedStack;
        if (!this.mStackSupervisor.readyToResume()) {
            return false;
        }
        ActivityStack recentsStack = getStack(1, 3);
        if (this.mService.mGestureController.mLaunchRecentsFromGesture && recentsStack != null && !this.mService.mGestureController.mHasResumeRecentsBehind && !this.mService.mGestureController.mStopLaunchRecentsBehind) {
            return recentsStack.resumeTopActivityUncheckedLocked((ActivityRecord) null, (ActivityOptions) null);
        }
        boolean result = false;
        if (targetStack != null && (targetStack.isTopStackOnDisplay() || getTopDisplayFocusedStack() == targetStack)) {
            result = targetStack.resumeTopActivityUncheckedLocked(target, targetOptions);
        }
        for (int displayNdx = this.mActivityDisplays.size() - 1; displayNdx >= 0; displayNdx--) {
            boolean resumedOnDisplay = false;
            ActivityDisplay display = this.mActivityDisplays.get(displayNdx);
            for (int stackNdx = display.getChildCount() - 1; stackNdx >= 0; stackNdx--) {
                ActivityStack stack = display.getChildAt(stackNdx);
                ActivityRecord topRunningActivity = stack.topRunningActivityLocked();
                if (stack.isFocusableAndVisible() && topRunningActivity != null) {
                    if (stack == targetStack) {
                        resumedOnDisplay |= result;
                    } else if (!display.isTopStack(stack) || !topRunningActivity.isState(ActivityStack.ActivityState.RESUMED)) {
                        resumedOnDisplay |= topRunningActivity.makeActiveIfNeeded(target);
                    } else {
                        stack.executeAppTransition(targetOptions);
                    }
                }
            }
            if (!resumedOnDisplay && (focusedStack = display.getFocusedStack()) != null) {
                focusedStack.resumeTopActivityUncheckedLocked(target, targetOptions);
            }
        }
        return result;
    }

    /* access modifiers changed from: package-private */
    public void applySleepTokens(boolean applyToStacks) {
        for (int displayNdx = this.mActivityDisplays.size() - 1; displayNdx >= 0; displayNdx--) {
            ActivityDisplay display = this.mActivityDisplays.get(displayNdx);
            boolean displayShouldSleep = display.shouldSleep();
            if (displayShouldSleep != display.isSleeping()) {
                display.setIsSleeping(displayShouldSleep);
                if (applyToStacks) {
                    for (int stackNdx = display.getChildCount() - 1; stackNdx >= 0; stackNdx--) {
                        ActivityStack stack = display.getChildAt(stackNdx);
                        if (displayShouldSleep) {
                            stack.goToSleepIfPossible(false);
                        } else {
                            stack.awakeFromSleepingLocked();
                            if (stack.isFocusedStackOnDisplay() && !this.mStackSupervisor.getKeyguardController().isKeyguardOrAodShowing(display.mDisplayId)) {
                                resumeFocusedStacksTopActivities();
                            }
                        }
                    }
                    if (!displayShouldSleep && !this.mStackSupervisor.mGoingToSleepActivities.isEmpty()) {
                        Iterator<ActivityRecord> it = this.mStackSupervisor.mGoingToSleepActivities.iterator();
                        while (it.hasNext()) {
                            if (it.next().getDisplayId() == display.mDisplayId) {
                                it.remove();
                            }
                        }
                    }
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public <T extends ActivityStack> T getStack(int stackId) {
        for (int i = this.mActivityDisplays.size() - 1; i >= 0; i--) {
            T stack = this.mActivityDisplays.get(i).getStack(stackId);
            if (stack != null) {
                return stack;
            }
        }
        return null;
    }

    private <T extends ActivityStack> T getStack(int windowingMode, int activityType) {
        for (int i = this.mActivityDisplays.size() - 1; i >= 0; i--) {
            T stack = this.mActivityDisplays.get(i).getStack(windowingMode, activityType);
            if (stack != null) {
                return stack;
            }
        }
        return null;
    }

    private ActivityManager.StackInfo getStackInfo(ActivityStack stack) {
        String str;
        int displayId = stack.mDisplayId;
        ActivityDisplay display = getActivityDisplay(displayId);
        ActivityManager.StackInfo info = new ActivityManager.StackInfo();
        stack.getWindowContainerBounds(info.bounds);
        info.displayId = displayId;
        info.stackId = stack.mStackId;
        info.userId = stack.mCurrentUser;
        ComponentName componentName = null;
        info.visible = stack.shouldBeVisible((ActivityRecord) null);
        info.position = display != null ? display.getIndexOf(stack) : 0;
        info.configuration.setTo(stack.getConfiguration());
        ArrayList<TaskRecord> tasks = stack.getAllTasks();
        int numTasks = tasks.size();
        int[] taskIds = new int[numTasks];
        String[] taskNames = new String[numTasks];
        Rect[] taskBounds = new Rect[numTasks];
        int[] taskUserIds = new int[numTasks];
        for (int i = 0; i < numTasks; i++) {
            TaskRecord task = tasks.get(i);
            taskIds[i] = task.taskId;
            if (task.origActivity != null) {
                str = task.origActivity.flattenToString();
            } else if (task.realActivity != null) {
                str = task.realActivity.flattenToString();
            } else if (task.getTopActivity() != null) {
                str = task.getTopActivity().packageName;
            } else {
                str = UiModeManagerService.Shell.NIGHT_MODE_STR_UNKNOWN;
            }
            taskNames[i] = str;
            taskBounds[i] = new Rect();
            task.getWindowContainerBounds(taskBounds[i]);
            taskUserIds[i] = task.userId;
        }
        info.taskIds = taskIds;
        info.taskNames = taskNames;
        info.taskBounds = taskBounds;
        info.taskUserIds = taskUserIds;
        ActivityRecord top = stack.topRunningActivityLocked();
        if (top != null) {
            componentName = top.intent.getComponent();
        }
        info.topActivity = componentName;
        return info;
    }

    /* access modifiers changed from: package-private */
    public ActivityManager.StackInfo getStackInfo(int stackId) {
        ActivityStack stack = getStack(stackId);
        if (stack != null) {
            return getStackInfo(stack);
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public ActivityManager.StackInfo getStackInfo(int windowingMode, int activityType) {
        ActivityStack stack = getStack(windowingMode, activityType);
        if (stack != null) {
            return getStackInfo(stack);
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public ArrayList<ActivityManager.StackInfo> getAllStackInfos() {
        ArrayList<ActivityManager.StackInfo> list = new ArrayList<>();
        for (int displayNdx = 0; displayNdx < this.mActivityDisplays.size(); displayNdx++) {
            ActivityDisplay display = this.mActivityDisplays.get(displayNdx);
            for (int stackNdx = display.getChildCount() - 1; stackNdx >= 0; stackNdx--) {
                list.add(getStackInfo(display.getChildAt(stackNdx)));
            }
        }
        return list;
    }

    /* access modifiers changed from: package-private */
    public void deferUpdateBounds(int activityType) {
        ActivityStack stack = getStack(0, activityType);
        if (stack != null) {
            stack.deferUpdateBounds();
        }
    }

    /* access modifiers changed from: package-private */
    public void continueUpdateBounds(int activityType) {
        ActivityStack stack = getStack(0, activityType);
        if (stack != null) {
            stack.continueUpdateBounds();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0029, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x002c, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDisplayAdded(int r4) {
        /*
            r3 = this;
            com.android.server.wm.ActivityTaskManagerService r0 = r3.mService
            com.android.server.wm.WindowManagerGlobalLock r0 = r0.mGlobalLock
            monitor-enter(r0)
            com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x002d }
            com.android.server.wm.ActivityDisplay r1 = r3.getActivityDisplayOrCreate(r4)     // Catch:{ all -> 0x002d }
            if (r1 != 0) goto L_0x0013
            monitor-exit(r0)     // Catch:{ all -> 0x002d }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return
        L_0x0013:
            com.android.server.wm.ActivityTaskManagerService r2 = r3.mService     // Catch:{ all -> 0x002d }
            boolean r2 = r2.isBooted()     // Catch:{ all -> 0x002d }
            if (r2 != 0) goto L_0x0023
            com.android.server.wm.ActivityTaskManagerService r2 = r3.mService     // Catch:{ all -> 0x002d }
            boolean r2 = r2.isBooting()     // Catch:{ all -> 0x002d }
            if (r2 == 0) goto L_0x0028
        L_0x0023:
            com.android.server.wm.DisplayContent r2 = r1.mDisplayContent     // Catch:{ all -> 0x002d }
            r3.startSystemDecorations(r2)     // Catch:{ all -> 0x002d }
        L_0x0028:
            monitor-exit(r0)     // Catch:{ all -> 0x002d }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return
        L_0x002d:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x002d }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.RootActivityContainer.onDisplayAdded(int):void");
    }

    private void startSystemDecorations(DisplayContent displayContent) {
        startHomeOnDisplay(this.mCurrentUser, "displayAdded", displayContent.getDisplayId());
        displayContent.getDisplayPolicy().notifyDisplayReady();
    }

    public void onDisplayRemoved(int displayId) {
        if (displayId != 0) {
            synchronized (this.mService.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    ActivityDisplay activityDisplay = getActivityDisplay(displayId);
                    if (activityDisplay == null) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return;
                    }
                    activityDisplay.remove();
                    WindowManagerService.resetPriorityAfterLockedSection();
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("Can't remove the primary display.");
        }
    }

    public void onDisplayChanged(int displayId) {
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityDisplay activityDisplay = getActivityDisplay(displayId);
                if (activityDisplay != null) {
                    activityDisplay.onDisplayChanged();
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
    public void updateUIDsPresentOnDisplay() {
        this.mDisplayAccessUIDs.clear();
        for (int displayNdx = this.mActivityDisplays.size() - 1; displayNdx >= 0; displayNdx--) {
            ActivityDisplay activityDisplay = this.mActivityDisplays.get(displayNdx);
            if (activityDisplay.isPrivate()) {
                this.mDisplayAccessUIDs.append(activityDisplay.mDisplayId, activityDisplay.getPresentUIDs());
            }
        }
        this.mDisplayManagerInternal.setDisplayAccessUIDs(this.mDisplayAccessUIDs);
    }

    /* access modifiers changed from: package-private */
    public ActivityStack findStackBehind(ActivityStack stack) {
        ActivityDisplay display = getActivityDisplay(stack.mDisplayId);
        if (display != null) {
            for (int i = display.getChildCount() - 1; i >= 0; i--) {
                if (display.getChildAt(i) == stack && i > 0) {
                    return display.getChildAt(i - 1);
                }
            }
        }
        throw new IllegalStateException("Failed to find a stack behind stack=" + stack + " in=" + display);
    }

    /* access modifiers changed from: protected */
    public int getChildCount() {
        return this.mActivityDisplays.size();
    }

    /* access modifiers changed from: protected */
    public ActivityDisplay getChildAt(int index) {
        return this.mActivityDisplays.get(index);
    }

    /* access modifiers changed from: protected */
    public ConfigurationContainer getParent() {
        return null;
    }

    /* access modifiers changed from: package-private */
    public void onChildPositionChanged(ActivityDisplay display, int position) {
        if (display != null) {
            positionChildAt(display, position);
        }
    }

    private void positionChildAt(ActivityDisplay display, int position) {
        if (position >= this.mActivityDisplays.size()) {
            position = this.mActivityDisplays.size() - 1;
        } else if (position < 0) {
            position = 0;
        }
        if (this.mActivityDisplays.isEmpty()) {
            this.mActivityDisplays.add(display);
        } else if (this.mActivityDisplays.get(position) != display) {
            this.mActivityDisplays.remove(display);
            this.mActivityDisplays.add(position, display);
        }
        this.mStackSupervisor.updateTopResumedActivityIfNeeded();
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void addChild(ActivityDisplay activityDisplay, int position) {
        positionChildAt(activityDisplay, position);
        this.mRootWindowContainer.positionChildAt(position, activityDisplay.mDisplayContent);
    }

    /* access modifiers changed from: package-private */
    public void removeChild(ActivityDisplay activityDisplay) {
        this.mActivityDisplays.remove(activityDisplay);
    }

    /* access modifiers changed from: package-private */
    public Configuration getDisplayOverrideConfiguration(int displayId) {
        ActivityDisplay activityDisplay = getActivityDisplayOrCreate(displayId);
        if (activityDisplay != null) {
            return activityDisplay.getRequestedOverrideConfiguration();
        }
        throw new IllegalArgumentException("No display found with id: " + displayId);
    }

    /* access modifiers changed from: package-private */
    public void setDisplayOverrideConfiguration(Configuration overrideConfiguration, int displayId) {
        ActivityDisplay activityDisplay = getActivityDisplayOrCreate(displayId);
        if (activityDisplay != null) {
            activityDisplay.onRequestedOverrideConfigurationChanged(overrideConfiguration);
            return;
        }
        throw new IllegalArgumentException("No display found with id: " + displayId);
    }

    /* access modifiers changed from: package-private */
    public void prepareForShutdown() {
        for (int i = 0; i < this.mActivityDisplays.size(); i++) {
            createSleepToken("shutdown", this.mActivityDisplays.get(i).mDisplayId);
        }
    }

    /* access modifiers changed from: package-private */
    public ActivityTaskManagerInternal.SleepToken createSleepToken(String tag, int displayId) {
        ActivityDisplay display = getActivityDisplay(displayId);
        if (display != null) {
            SleepTokenImpl token = new SleepTokenImpl(tag, displayId);
            this.mSleepTokens.add(token);
            display.mAllSleepTokens.add(token);
            return token;
        }
        throw new IllegalArgumentException("Invalid display: " + displayId);
    }

    /* access modifiers changed from: private */
    public void removeSleepToken(SleepTokenImpl token) {
        this.mSleepTokens.remove(token);
        ActivityDisplay display = getActivityDisplay(token.mDisplayId);
        if (display != null) {
            display.mAllSleepTokens.remove(token);
            if (display.mAllSleepTokens.isEmpty()) {
                this.mService.updateSleepIfNeededLocked();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void addStartingWindowsForVisibleActivities(boolean taskSwitch) {
        for (int displayNdx = this.mActivityDisplays.size() - 1; displayNdx >= 0; displayNdx--) {
            ActivityDisplay display = this.mActivityDisplays.get(displayNdx);
            for (int stackNdx = display.getChildCount() - 1; stackNdx >= 0; stackNdx--) {
                display.getChildAt(stackNdx).addStartingWindowsForVisibleActivities(taskSwitch);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void invalidateTaskLayers() {
        this.mTaskLayersChanged = true;
    }

    /* access modifiers changed from: package-private */
    public void rankTaskLayersIfNeeded() {
        if (this.mTaskLayersChanged) {
            this.mTaskLayersChanged = false;
            for (int displayNdx = 0; displayNdx < this.mActivityDisplays.size(); displayNdx++) {
                ActivityDisplay display = this.mActivityDisplays.get(displayNdx);
                int baseLayer = 0;
                for (int stackNdx = display.getChildCount() - 1; stackNdx >= 0; stackNdx--) {
                    baseLayer += display.getChildAt(stackNdx).rankTaskLayers(baseLayer);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void clearOtherAppTimeTrackers(AppTimeTracker except) {
        for (int displayNdx = this.mActivityDisplays.size() - 1; displayNdx >= 0; displayNdx--) {
            ActivityDisplay display = this.mActivityDisplays.get(displayNdx);
            for (int stackNdx = display.getChildCount() - 1; stackNdx >= 0; stackNdx--) {
                display.getChildAt(stackNdx).clearOtherAppTimeTrackers(except);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void scheduleDestroyAllActivities(WindowProcessController app, String reason) {
        for (int displayNdx = this.mActivityDisplays.size() - 1; displayNdx >= 0; displayNdx--) {
            ActivityDisplay display = this.mActivityDisplays.get(displayNdx);
            for (int stackNdx = display.getChildCount() - 1; stackNdx >= 0; stackNdx--) {
                display.getChildAt(stackNdx).scheduleDestroyActivities(app, reason);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void releaseSomeActivitiesLocked(WindowProcessController app, String reason) {
        ArraySet<TaskRecord> tasks = app.getReleaseSomeActivitiesTasks();
        if (tasks != null) {
            int numDisplays = this.mActivityDisplays.size();
            for (int displayNdx = 0; displayNdx < numDisplays; displayNdx++) {
                ActivityDisplay display = this.mActivityDisplays.get(displayNdx);
                int stackCount = display.getChildCount();
                int stackNdx = 0;
                while (stackNdx < stackCount) {
                    if (display.getChildAt(stackNdx).releaseSomeActivitiesLocked(app, tasks, reason) <= 0) {
                        stackNdx++;
                    } else {
                        return;
                    }
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean putStacksToSleep(boolean allowDelay, boolean shuttingDown) {
        boolean allSleep = true;
        for (int displayNdx = this.mActivityDisplays.size() - 1; displayNdx >= 0; displayNdx--) {
            ActivityDisplay display = this.mActivityDisplays.get(displayNdx);
            for (int stackNdx = display.getChildCount() - 1; stackNdx >= 0; stackNdx--) {
                if (stackNdx < display.getChildCount()) {
                    ActivityStack stack = display.getChildAt(stackNdx);
                    if (allowDelay) {
                        allSleep &= stack.goToSleepIfPossible(shuttingDown);
                    } else {
                        stack.goToSleep();
                    }
                }
            }
        }
        return allSleep;
    }

    /* access modifiers changed from: package-private */
    public void handleAppCrash(WindowProcessController app) {
        for (int displayNdx = this.mActivityDisplays.size() - 1; displayNdx >= 0; displayNdx--) {
            ActivityDisplay display = this.mActivityDisplays.get(displayNdx);
            for (int stackNdx = display.getChildCount() - 1; stackNdx >= 0; stackNdx--) {
                display.getChildAt(stackNdx).handleAppCrash(app);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public ActivityRecord findActivity(Intent intent, ActivityInfo info, boolean compareIntentFilters) {
        for (int displayNdx = this.mActivityDisplays.size() - 1; displayNdx >= 0; displayNdx--) {
            ActivityDisplay display = this.mActivityDisplays.get(displayNdx);
            for (int stackNdx = display.getChildCount() - 1; stackNdx >= 0; stackNdx--) {
                ActivityRecord ar = display.getChildAt(stackNdx).findActivityLocked(intent, info, compareIntentFilters);
                if (ar != null) {
                    return ar;
                }
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public boolean hasAwakeDisplay() {
        for (int displayNdx = this.mActivityDisplays.size() - 1; displayNdx >= 0; displayNdx--) {
            if (!this.mActivityDisplays.get(displayNdx).shouldSleep()) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public <T extends ActivityStack> T getLaunchStack(ActivityRecord r, ActivityOptions options, TaskRecord candidateTask, boolean onTop) {
        return getLaunchStack(r, options, candidateTask, onTop, (LaunchParamsController.LaunchParams) null);
    }

    /* access modifiers changed from: package-private */
    public <T extends ActivityStack> T getLaunchStack(ActivityRecord r, ActivityOptions options, TaskRecord candidateTask, boolean onTop, LaunchParamsController.LaunchParams launchParams) {
        int taskId;
        int displayId;
        T stack;
        ActivityDisplay display;
        int windowingMode;
        T stack2;
        T stack3;
        ActivityRecord activityRecord = r;
        ActivityOptions activityOptions = options;
        TaskRecord taskRecord = candidateTask;
        LaunchParamsController.LaunchParams launchParams2 = launchParams;
        int displayId2 = -1;
        if (activityOptions != null) {
            int taskId2 = options.getLaunchTaskId();
            displayId2 = options.getLaunchDisplayId();
            taskId = taskId2;
        } else {
            taskId = -1;
        }
        if (taskId != -1) {
            activityOptions.setLaunchTaskId(-1);
            TaskRecord task = anyTaskForId(taskId, 2, activityOptions, onTop);
            activityOptions.setLaunchTaskId(taskId);
            if (task != null) {
                return task.getStack();
            }
        } else {
            boolean z = onTop;
        }
        int activityType = resolveActivityType(r, options, candidateTask);
        if (launchParams2 == null || launchParams2.mPreferredDisplayId == -1) {
            displayId = displayId2;
        } else {
            displayId = launchParams2.mPreferredDisplayId;
        }
        if (displayId != -1 && canLaunchOnDisplay(activityRecord, displayId)) {
            if (activityRecord != null && (stack3 = getValidLaunchStackOnDisplay(displayId, r, candidateTask, options, launchParams)) != null) {
                return stack3;
            }
            ActivityDisplay display2 = getActivityDisplayOrCreate(displayId);
            if (!(display2 == null || (stack2 = display2.getOrCreateStack(r, options, candidateTask, activityType, onTop)) == null)) {
                return stack2;
            }
        }
        T stack4 = null;
        ActivityDisplay display3 = null;
        if (taskRecord != null) {
            stack4 = candidateTask.getStack();
        }
        if (stack4 != null || activityRecord == null) {
            stack = stack4;
        } else {
            stack = r.getActivityStack();
        }
        if (!(stack == null || (display3 = stack.getDisplay()) == null || !canLaunchOnDisplay(activityRecord, display3.mDisplayId))) {
            if (launchParams2 != null) {
                windowingMode = launchParams2.mWindowingMode;
            } else {
                windowingMode = 0;
            }
            if (windowingMode == 0) {
                windowingMode = display3.resolveWindowingMode(activityRecord, activityOptions, taskRecord, activityType);
            }
            if (stack.isCompatible(windowingMode, activityType)) {
                return stack;
            }
            if (windowingMode == 4 && display3.getSplitScreenPrimaryStack() == stack && taskRecord == stack.topTask()) {
                return stack;
            }
        }
        if (display3 == null || !canLaunchOnDisplay(activityRecord, display3.mDisplayId)) {
            display = getDefaultDisplay();
        } else {
            display = display3;
        }
        return display.getOrCreateStack(r, options, candidateTask, activityType, onTop);
    }

    private boolean canLaunchOnDisplay(ActivityRecord r, int displayId) {
        if (r == null) {
            return true;
        }
        return r.canBeLaunchedOnDisplay(displayId);
    }

    private ActivityStack getValidLaunchStackOnDisplay(int displayId, ActivityRecord r, TaskRecord candidateTask, ActivityOptions options, LaunchParamsController.LaunchParams launchParams) {
        int windowingMode;
        ActivityDisplay activityDisplay = getActivityDisplayOrCreate(displayId);
        if (activityDisplay == null) {
            throw new IllegalArgumentException("Display with displayId=" + displayId + " not found.");
        } else if (!r.canBeLaunchedOnDisplay(displayId)) {
            return null;
        } else {
            if (r.getDisplayId() == displayId && r.getTaskRecord() == candidateTask && (options == null || options.getLaunchWindowingMode() != 5 || (candidateTask.getStack() != null && candidateTask.getStack().getWindowingMode() == 5))) {
                return candidateTask.getStack();
            }
            if (launchParams != null) {
                windowingMode = launchParams.mWindowingMode;
            } else if (options != null) {
                windowingMode = options.getLaunchWindowingMode();
            } else {
                windowingMode = r.getWindowingMode();
            }
            int windowingMode2 = activityDisplay.validateWindowingMode(windowingMode, r, candidateTask, r.getActivityType());
            for (int i = activityDisplay.getChildCount() - 1; i >= 0; i--) {
                ActivityStack stack = activityDisplay.getChildAt(i);
                if (isValidLaunchStack(stack, r, windowingMode2)) {
                    return stack;
                }
            }
            if (displayId == 0) {
                return null;
            }
            return activityDisplay.createStack(windowingMode2, (options == null || options.getLaunchActivityType() == 0) ? r.getActivityType() : options.getLaunchActivityType(), true);
        }
    }

    /* access modifiers changed from: package-private */
    public ActivityStack getValidLaunchStackOnDisplay(int displayId, ActivityRecord r, ActivityOptions options, LaunchParamsController.LaunchParams launchParams) {
        return getValidLaunchStackOnDisplay(displayId, r, (TaskRecord) null, options, launchParams);
    }

    private boolean isValidLaunchStack(ActivityStack stack, ActivityRecord r, int windowingMode) {
        int activityType = stack.getActivityType();
        if (activityType == 2) {
            return r.isActivityTypeHome();
        }
        if (activityType == 3) {
            return r.isActivityTypeRecents();
        }
        if (activityType == 4) {
            return r.isActivityTypeAssistant();
        }
        if (stack.getWindowingMode() != 3 || !r.supportsSplitScreenWindowingMode()) {
            return false;
        }
        if (windowingMode == 3 || windowingMode == 0) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public int resolveActivityType(ActivityRecord r, ActivityOptions options, TaskRecord task) {
        int activityType = r != null ? r.getActivityType() : 0;
        if (activityType == 0 && task != null) {
            activityType = task.getActivityType();
        }
        if (activityType != 0) {
            return activityType;
        }
        if (options != null) {
            activityType = options.getLaunchActivityType();
        }
        if (activityType != 0) {
            return activityType;
        }
        return 1;
    }

    /* access modifiers changed from: package-private */
    public ActivityStack getNextFocusableStack(ActivityStack currentFocus, boolean ignoreCurrent) {
        ActivityStack nextFocusableStack;
        ActivityDisplay preferredDisplay = currentFocus.getDisplay();
        ActivityStack preferredFocusableStack = preferredDisplay.getNextFocusableStack(currentFocus, ignoreCurrent);
        if (preferredFocusableStack != null) {
            return preferredFocusableStack;
        }
        if (preferredDisplay.supportsSystemDecorations()) {
            return null;
        }
        for (int i = this.mActivityDisplays.size() - 1; i >= 0; i--) {
            ActivityDisplay display = this.mActivityDisplays.get(i);
            if (display != preferredDisplay && (nextFocusableStack = display.getNextFocusableStack(currentFocus, ignoreCurrent)) != null) {
                return nextFocusableStack;
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public ActivityStack getNextValidLaunchStack(ActivityRecord r, int currentFocus) {
        ActivityStack stack;
        int i = this.mActivityDisplays.size();
        while (true) {
            i--;
            if (i < 0) {
                return null;
            }
            ActivityDisplay display = this.mActivityDisplays.get(i);
            if (display.mDisplayId != currentFocus && (stack = getValidLaunchStackOnDisplay(display.mDisplayId, r, (ActivityOptions) null, (LaunchParamsController.LaunchParams) null)) != null) {
                return stack;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean handleAppDied(WindowProcessController app) {
        boolean hasVisibleActivities = false;
        for (int displayNdx = this.mActivityDisplays.size() - 1; displayNdx >= 0; displayNdx--) {
            ActivityDisplay display = this.mActivityDisplays.get(displayNdx);
            for (int stackNdx = display.getChildCount() - 1; stackNdx >= 0; stackNdx--) {
                hasVisibleActivities |= display.getChildAt(stackNdx).handleAppDiedLocked(app);
            }
        }
        return hasVisibleActivities;
    }

    /* access modifiers changed from: package-private */
    public void closeSystemDialogs() {
        for (int displayNdx = this.mActivityDisplays.size() - 1; displayNdx >= 0; displayNdx--) {
            ActivityDisplay display = this.mActivityDisplays.get(displayNdx);
            for (int stackNdx = display.getChildCount() - 1; stackNdx >= 0; stackNdx--) {
                display.getChildAt(stackNdx).closeSystemDialogsLocked();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean finishDisabledPackageActivities(String packageName, Set<String> filterByClasses, boolean doit, boolean evenPersistent, int userId) {
        boolean didSomething = false;
        for (int displayNdx = this.mActivityDisplays.size() - 1; displayNdx >= 0; displayNdx--) {
            ActivityDisplay display = this.mActivityDisplays.get(displayNdx);
            for (int stackNdx = display.getChildCount() - 1; stackNdx >= 0; stackNdx--) {
                if (display.getChildAt(stackNdx).finishDisabledPackageActivitiesLocked(packageName, filterByClasses, doit, evenPersistent, userId)) {
                    didSomething = true;
                }
            }
        }
        return didSomething;
    }

    /* access modifiers changed from: package-private */
    public void updateActivityApplicationInfo(ApplicationInfo aInfo) {
        for (int displayNdx = this.mActivityDisplays.size() - 1; displayNdx >= 0; displayNdx--) {
            ActivityDisplay display = this.mActivityDisplays.get(displayNdx);
            for (int stackNdx = display.getChildCount() - 1; stackNdx >= 0; stackNdx--) {
                display.getChildAt(stackNdx).updateActivityApplicationInfoLocked(aInfo);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void finishVoiceTask(IVoiceInteractionSession session) {
        for (int displayNdx = this.mActivityDisplays.size() - 1; displayNdx >= 0; displayNdx--) {
            ActivityDisplay display = this.mActivityDisplays.get(displayNdx);
            int numStacks = display.getChildCount();
            for (int stackNdx = 0; stackNdx < numStacks; stackNdx++) {
                display.getChildAt(stackNdx).finishVoiceTask(session);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void removeStacksInWindowingModes(int... windowingModes) {
        for (int i = this.mActivityDisplays.size() - 1; i >= 0; i--) {
            this.mActivityDisplays.get(i).removeStacksInWindowingModes(windowingModes);
        }
    }

    /* access modifiers changed from: package-private */
    public void removeStacksWithActivityTypes(int... activityTypes) {
        for (int i = this.mActivityDisplays.size() - 1; i >= 0; i--) {
            this.mActivityDisplays.get(i).removeStacksWithActivityTypes(activityTypes);
        }
    }

    /* access modifiers changed from: package-private */
    public ActivityRecord topRunningActivity() {
        for (int i = this.mActivityDisplays.size() - 1; i >= 0; i--) {
            ActivityRecord topActivity = this.mActivityDisplays.get(i).topRunningActivity();
            if (topActivity != null) {
                return topActivity;
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public boolean allResumedActivitiesIdle() {
        ActivityStack stack;
        ActivityRecord resumedActivity;
        for (int displayNdx = this.mActivityDisplays.size() - 1; displayNdx >= 0; displayNdx--) {
            ActivityDisplay display = this.mActivityDisplays.get(displayNdx);
            if (!display.isSleeping() && (stack = display.getFocusedStack()) != null && stack.numActivities() != 0 && ((resumedActivity = stack.getResumedActivity()) == null || !resumedActivity.idle)) {
                return false;
            }
        }
        sendPowerHintForLaunchEndIfNeeded();
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean allResumedActivitiesVisible() {
        boolean foundResumed = false;
        for (int displayNdx = this.mActivityDisplays.size() - 1; displayNdx >= 0; displayNdx--) {
            ActivityDisplay display = this.mActivityDisplays.get(displayNdx);
            for (int stackNdx = display.getChildCount() - 1; stackNdx >= 0; stackNdx--) {
                ActivityRecord r = display.getChildAt(stackNdx).getResumedActivity();
                if (r != null) {
                    if (!r.nowVisible) {
                        return false;
                    }
                    foundResumed = true;
                }
            }
        }
        return foundResumed;
    }

    /* access modifiers changed from: package-private */
    public boolean allPausedActivitiesComplete() {
        for (int displayNdx = this.mActivityDisplays.size() - 1; displayNdx >= 0; displayNdx--) {
            ActivityDisplay display = this.mActivityDisplays.get(displayNdx);
            for (int stackNdx = display.getChildCount() - 1; stackNdx >= 0; stackNdx--) {
                ActivityRecord r = display.getChildAt(stackNdx).mPausingActivity;
                if (r != null && !r.isState(ActivityStack.ActivityState.PAUSED, ActivityStack.ActivityState.STOPPED, ActivityStack.ActivityState.STOPPING)) {
                    return false;
                }
            }
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public void lockAllProfileTasks(int userId) {
        this.mWindowManager.deferSurfaceLayout();
        try {
            for (int displayNdx = this.mActivityDisplays.size() - 1; displayNdx >= 0; displayNdx--) {
                ActivityDisplay display = this.mActivityDisplays.get(displayNdx);
                for (int stackNdx = display.getChildCount() - 1; stackNdx >= 0; stackNdx--) {
                    List<TaskRecord> tasks = display.getChildAt(stackNdx).getAllTasks();
                    for (int taskNdx = tasks.size() - 1; taskNdx >= 0; taskNdx--) {
                        TaskRecord task = tasks.get(taskNdx);
                        if (taskTopActivityIsUser(task, userId)) {
                            this.mService.getTaskChangeNotificationController().notifyTaskProfileLocked(task.taskId, userId);
                        }
                    }
                }
            }
        } finally {
            this.mWindowManager.continueSurfaceLayout();
        }
    }

    private boolean taskTopActivityIsUser(TaskRecord task, int userId) {
        ActivityRecord activityRecord = task.getTopActivity();
        ActivityRecord resultTo = activityRecord != null ? activityRecord.resultTo : null;
        return (activityRecord != null && activityRecord.mUserId == userId) || (resultTo != null && resultTo.mUserId == userId);
    }

    /* access modifiers changed from: package-private */
    public void cancelInitializingActivities() {
        for (int displayNdx = this.mActivityDisplays.size() - 1; displayNdx >= 0; displayNdx--) {
            ActivityDisplay display = this.mActivityDisplays.get(displayNdx);
            for (int stackNdx = display.getChildCount() - 1; stackNdx >= 0; stackNdx--) {
                display.getChildAt(stackNdx).cancelInitializingActivities();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public TaskRecord anyTaskForId(int id) {
        return anyTaskForId(id, 2);
    }

    /* access modifiers changed from: package-private */
    public TaskRecord anyTaskForId(int id, int matchMode) {
        return anyTaskForId(id, matchMode, (ActivityOptions) null, false);
    }

    /* access modifiers changed from: package-private */
    public TaskRecord anyTaskForId(int id, int matchMode, ActivityOptions aOptions, boolean onTop) {
        TaskRecord task;
        int i = id;
        int i2 = matchMode;
        ActivityOptions activityOptions = aOptions;
        boolean z = onTop;
        int reparentMode = 2;
        if (i2 == 2 || activityOptions == null) {
            int numDisplays = this.mActivityDisplays.size();
            for (int displayNdx = 0; displayNdx < numDisplays; displayNdx++) {
                ActivityDisplay display = this.mActivityDisplays.get(displayNdx);
                int stackNdx = display.getChildCount() - 1;
                while (stackNdx >= 0) {
                    ActivityStack stack = display.getChildAt(stackNdx);
                    TaskRecord task2 = stack.taskForIdLocked(i);
                    if (task2 == null) {
                        stackNdx--;
                    } else if (activityOptions != null) {
                        ActivityStack launchStack = getLaunchStack((ActivityRecord) null, activityOptions, task2, z);
                        if (launchStack == null || stack == launchStack) {
                            ActivityStack activityStack = stack;
                            return task2;
                        }
                        if (z) {
                            reparentMode = 0;
                        }
                        ActivityStack activityStack2 = launchStack;
                        TaskRecord task3 = task2;
                        ActivityStack activityStack3 = stack;
                        task2.reparent(launchStack, onTop, reparentMode, true, true, "anyTaskForId");
                        return task3;
                    } else {
                        ActivityStack activityStack4 = stack;
                        return task2;
                    }
                }
            }
            if (i2 == 0 || (task = this.mStackSupervisor.mRecentTasks.getTask(i)) == null) {
                return null;
            }
            if (i2 != 1 && !this.mStackSupervisor.restoreRecentTaskLocked(task, activityOptions, z)) {
                return null;
            }
            return task;
        }
        throw new IllegalArgumentException("Should not specify activity options for non-restore lookup");
    }

    /* access modifiers changed from: package-private */
    public ActivityRecord isInAnyStack(IBinder token) {
        int numDisplays = this.mActivityDisplays.size();
        for (int displayNdx = 0; displayNdx < numDisplays; displayNdx++) {
            ActivityDisplay display = this.mActivityDisplays.get(displayNdx);
            for (int stackNdx = display.getChildCount() - 1; stackNdx >= 0; stackNdx--) {
                ActivityRecord r = display.getChildAt(stackNdx).isInStackLocked(token);
                if (r != null) {
                    return r;
                }
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void getRunningTasks(int maxNum, List<ActivityManager.RunningTaskInfo> list, @WindowConfiguration.ActivityType int ignoreActivityType, @WindowConfiguration.WindowingMode int ignoreWindowingMode, int callingUid, boolean allowed) {
        this.mStackSupervisor.mRunningTasks.getTasks(maxNum, list, ignoreActivityType, ignoreWindowingMode, this.mActivityDisplays, callingUid, allowed);
    }

    /* access modifiers changed from: package-private */
    public void sendPowerHintForLaunchStartIfNeeded(boolean forceSend, ActivityRecord targetActivity) {
        boolean sendHint = forceSend;
        boolean z = false;
        if (!sendHint) {
            sendHint = targetActivity == null || targetActivity.app == null;
        }
        if (!sendHint) {
            boolean noResumedActivities = true;
            boolean allFocusedProcessesDiffer = true;
            for (int displayNdx = 0; displayNdx < this.mActivityDisplays.size(); displayNdx++) {
                ActivityRecord resumedActivity = this.mActivityDisplays.get(displayNdx).getResumedActivity();
                WindowProcessController resumedActivityProcess = resumedActivity == null ? null : resumedActivity.app;
                noResumedActivities &= resumedActivityProcess == null;
                if (resumedActivityProcess != null) {
                    allFocusedProcessesDiffer &= !resumedActivityProcess.equals(targetActivity.app);
                }
            }
            if (noResumedActivities || allFocusedProcessesDiffer) {
                z = true;
            }
            sendHint = z;
        }
        if (sendHint && this.mService.mPowerManagerInternal != null) {
            this.mService.mPowerManagerInternal.powerHint(8, 1);
            this.mPowerHintSent = true;
        }
    }

    /* access modifiers changed from: package-private */
    public void sendPowerHintForLaunchEndIfNeeded() {
        if (this.mPowerHintSent && this.mService.mPowerManagerInternal != null) {
            this.mService.mPowerManagerInternal.powerHint(8, 0);
            this.mPowerHintSent = false;
        }
    }

    private void calculateDefaultMinimalSizeOfResizeableTasks() {
        Resources res = this.mService.mContext.getResources();
        this.mDefaultMinSizeOfResizeableTaskDp = (int) (res.getDimension(17105124) / res.getDisplayMetrics().density);
    }

    /* access modifiers changed from: package-private */
    public ArrayList<ActivityRecord> getDumpActivities(String name, boolean dumpVisibleStacksOnly, boolean dumpFocusedStackOnly) {
        if (dumpFocusedStackOnly) {
            return getTopDisplayFocusedStack().getDumpActivitiesLocked(name);
        }
        ArrayList<ActivityRecord> activities = new ArrayList<>();
        int numDisplays = this.mActivityDisplays.size();
        for (int displayNdx = 0; displayNdx < numDisplays; displayNdx++) {
            ActivityDisplay display = this.mActivityDisplays.get(displayNdx);
            for (int stackNdx = display.getChildCount() - 1; stackNdx >= 0; stackNdx--) {
                ActivityStack stack = display.getChildAt(stackNdx);
                if (!dumpVisibleStacksOnly || stack.shouldBeVisible((ActivityRecord) null)) {
                    activities.addAll(stack.getDumpActivitiesLocked(name));
                }
            }
        }
        return activities;
    }

    public void dump(PrintWriter pw, String prefix) {
        pw.print(prefix);
        pw.println("topDisplayFocusedStack=" + getTopDisplayFocusedStack());
        for (int i = this.mActivityDisplays.size() + -1; i >= 0; i--) {
            this.mActivityDisplays.get(i).dump(pw, prefix);
        }
    }

    /* access modifiers changed from: package-private */
    public void dumpDisplayConfigs(PrintWriter pw, String prefix) {
        pw.print(prefix);
        pw.println("Display override configurations:");
        int displayCount = this.mActivityDisplays.size();
        for (int i = 0; i < displayCount; i++) {
            ActivityDisplay activityDisplay = this.mActivityDisplays.get(i);
            pw.print(prefix);
            pw.print("  ");
            pw.print(activityDisplay.mDisplayId);
            pw.print(": ");
            pw.println(activityDisplay.getRequestedOverrideConfiguration());
        }
    }

    public void dumpDisplays(PrintWriter pw) {
        for (int i = this.mActivityDisplays.size() - 1; i >= 0; i += -1) {
            ActivityDisplay display = this.mActivityDisplays.get(i);
            pw.print("[id:" + display.mDisplayId + " stacks:");
            display.dumpStacks(pw);
            pw.print("]");
        }
    }

    /* access modifiers changed from: package-private */
    public boolean dumpActivities(FileDescriptor fd, PrintWriter pw, boolean dumpAll, boolean dumpClient, String dumpPackage) {
        PrintWriter printWriter = pw;
        boolean printed = false;
        boolean needSep = false;
        for (int displayNdx = this.mActivityDisplays.size() - 1; displayNdx >= 0; displayNdx--) {
            ActivityDisplay activityDisplay = this.mActivityDisplays.get(displayNdx);
            printWriter.print("Display #");
            printWriter.print(activityDisplay.mDisplayId);
            printWriter.println(" (activities from top to bottom):");
            ActivityDisplay display = this.mActivityDisplays.get(displayNdx);
            for (int stackNdx = display.getChildCount() - 1; stackNdx >= 0; stackNdx--) {
                ActivityStack stack = display.getChildAt(stackNdx);
                pw.println();
                printed = stack.dump(fd, pw, dumpAll, dumpClient, dumpPackage, needSep);
                needSep = printed;
            }
            ActivityStackSupervisor.printThisActivity(printWriter, activityDisplay.getResumedActivity(), dumpPackage, needSep, " ResumedActivity:");
        }
        String str = dumpPackage;
        return ActivityStackSupervisor.dumpHistoryList(fd, pw, this.mStackSupervisor.mGoingToSleepActivities, "  ", "Sleep", false, !dumpAll, false, dumpPackage, true, "  Activities waiting to sleep:", (TaskRecord) null) | printed | ActivityStackSupervisor.dumpHistoryList(fd, pw, this.mStackSupervisor.mFinishingActivities, "  ", "Fin", false, !dumpAll, false, dumpPackage, true, "  Activities waiting to finish:", (TaskRecord) null) | ActivityStackSupervisor.dumpHistoryList(fd, pw, this.mStackSupervisor.mStoppingActivities, "  ", "Stop", false, !dumpAll, false, dumpPackage, true, "  Activities waiting to stop:", (TaskRecord) null);
    }

    /* access modifiers changed from: protected */
    public void writeToProto(ProtoOutputStream proto, long fieldId, int logLevel) {
        long token = proto.start(fieldId);
        super.writeToProto(proto, 1146756268033L, logLevel);
        for (int displayNdx = 0; displayNdx < this.mActivityDisplays.size(); displayNdx++) {
            this.mActivityDisplays.get(displayNdx).writeToProto(proto, 2246267895810L, logLevel);
        }
        this.mStackSupervisor.getKeyguardController().writeToProto(proto, 1146756268035L);
        ActivityStack focusedStack = getTopDisplayFocusedStack();
        if (focusedStack != null) {
            proto.write(1120986464260L, focusedStack.mStackId);
            ActivityRecord focusedActivity = focusedStack.getDisplay().getResumedActivity();
            if (focusedActivity != null) {
                focusedActivity.writeIdentifierToProto(proto, 1146756268037L);
            }
        } else {
            proto.write(1120986464260L, -1);
        }
        proto.write(1133871366150L, this.mStackSupervisor.mRecentTasks.isRecentsComponentHomeActivity(this.mCurrentUser));
        this.mService.getActivityStartController().writeToProto(proto, 2246267895815L);
        proto.end(token);
    }

    private final class SleepTokenImpl extends ActivityTaskManagerInternal.SleepToken {
        private final long mAcquireTime = SystemClock.uptimeMillis();
        /* access modifiers changed from: private */
        public final int mDisplayId;
        private final String mTag;

        public SleepTokenImpl(String tag, int displayId) {
            this.mTag = tag;
            this.mDisplayId = displayId;
        }

        public void release() {
            synchronized (RootActivityContainer.this.mService.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    RootActivityContainer.this.removeSleepToken(this);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public String toString() {
            return "{\"" + this.mTag + "\", display " + this.mDisplayId + ", acquire at " + TimeUtils.formatUptime(this.mAcquireTime) + "}";
        }
    }
}
