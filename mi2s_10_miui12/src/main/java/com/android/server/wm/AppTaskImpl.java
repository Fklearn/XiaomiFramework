package com.android.server.wm;

import android.app.ActivityManager;
import android.app.IAppTask;
import android.app.IApplicationThread;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.UserHandle;
import android.util.Slog;
import com.android.server.am.PendingIntentRecord;
import com.android.server.pm.DumpState;

class AppTaskImpl extends IAppTask.Stub {
    private static final String TAG = "AppTaskImpl";
    private int mCallingUid;
    private ActivityTaskManagerService mService;
    private int mTaskId;

    public AppTaskImpl(ActivityTaskManagerService service, int taskId, int callingUid) {
        this.mService = service;
        this.mTaskId = taskId;
        this.mCallingUid = callingUid;
    }

    private void checkCaller() {
        if (this.mCallingUid != Binder.getCallingUid()) {
            throw new SecurityException("Caller " + this.mCallingUid + " does not match caller of getAppTasks(): " + Binder.getCallingUid());
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    public void finishAndRemoveTask() {
        long origId;
        checkCaller();
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                origId = Binder.clearCallingIdentity();
                if (this.mService.mStackSupervisor.removeTaskByIdLocked(this.mTaskId, false, true, "finish-and-remove-task")) {
                    Binder.restoreCallingIdentity(origId);
                } else {
                    throw new IllegalArgumentException("Unable to find task ID " + this.mTaskId);
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    public ActivityManager.RecentTaskInfo getTaskInfo() {
        long origId;
        ActivityManager.RecentTaskInfo createRecentTaskInfo;
        checkCaller();
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                origId = Binder.clearCallingIdentity();
                TaskRecord tr = this.mService.mRootActivityContainer.anyTaskForId(this.mTaskId, 1);
                if (tr != null) {
                    createRecentTaskInfo = this.mService.getRecentTasks().createRecentTaskInfo(tr);
                    Binder.restoreCallingIdentity(origId);
                } else {
                    throw new IllegalArgumentException("Unable to find task ID " + this.mTaskId);
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return createRecentTaskInfo;
    }

    /* Debug info: failed to restart local var, previous not found, register: 20 */
    public void moveToFront(IApplicationThread appThread, String callingPackage) {
        WindowManagerGlobalLock windowManagerGlobalLock;
        IApplicationThread iApplicationThread = appThread;
        String str = callingPackage;
        checkCaller();
        int callingPid = Binder.getCallingPid();
        int callingUid = Binder.getCallingUid();
        if (this.mService.isSameApp(callingUid, str)) {
            long origId = Binder.clearCallingIdentity();
            try {
                WindowManagerGlobalLock windowManagerGlobalLock2 = this.mService.mGlobalLock;
                synchronized (windowManagerGlobalLock2) {
                    try {
                        WindowManagerService.boostPriorityForLockedSection();
                        if (this.mService.checkAppSwitchAllowedLocked(callingPid, callingUid, -1, -1, "Move to front")) {
                            WindowProcessController callerApp = null;
                            if (iApplicationThread != null) {
                                callerApp = this.mService.getProcessController(iApplicationThread);
                            }
                            windowManagerGlobalLock = windowManagerGlobalLock2;
                            if (!this.mService.getActivityStartController().obtainStarter((Intent) null, "moveToFront").shouldAbortBackgroundActivityStart(callingUid, callingPid, callingPackage, -1, -1, callerApp, (PendingIntentRecord) null, false, (Intent) null) || this.mService.isBackgroundActivityStartsEnabled()) {
                                try {
                                    this.mService.mStackSupervisor.startActivityFromRecents(callingPid, callingUid, this.mTaskId, (SafeActivityOptions) null);
                                    WindowManagerService.resetPriorityAfterLockedSection();
                                    Binder.restoreCallingIdentity(origId);
                                    return;
                                } catch (Throwable th) {
                                    th = th;
                                    WindowManagerService.resetPriorityAfterLockedSection();
                                    throw th;
                                }
                            } else {
                                WindowManagerService.resetPriorityAfterLockedSection();
                                Binder.restoreCallingIdentity(origId);
                                return;
                            }
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        windowManagerGlobalLock = windowManagerGlobalLock2;
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            } finally {
                Binder.restoreCallingIdentity(origId);
            }
        } else {
            String msg = "Permission Denial: moveToFront() from pid=" + Binder.getCallingPid() + " as package " + str;
            Slog.w(TAG, msg);
            throw new SecurityException(msg);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    /* JADX INFO: finally extract failed */
    public int startActivity(IBinder whoThread, String callingPackage, Intent intent, String resolvedType, Bundle bOptions) {
        TaskRecord tr;
        IApplicationThread appThread;
        checkCaller();
        int callingUser = UserHandle.getCallingUserId();
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                tr = this.mService.mRootActivityContainer.anyTaskForId(this.mTaskId, 1);
                if (tr != null) {
                    appThread = IApplicationThread.Stub.asInterface(whoThread);
                    if (appThread == null) {
                        throw new IllegalArgumentException("Bad app thread " + appThread);
                    }
                } else {
                    throw new IllegalArgumentException("Unable to find task ID " + this.mTaskId);
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return this.mService.getActivityStartController().obtainStarter(intent, TAG).setCaller(appThread).setCallingPackage(callingPackage).setResolvedType(resolvedType).setActivityOptions(bOptions).setMayWait(callingUser).setInTask(tr).execute();
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    public void setExcludeFromRecents(boolean exclude) {
        long origId;
        checkCaller();
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                origId = Binder.clearCallingIdentity();
                TaskRecord tr = this.mService.mRootActivityContainer.anyTaskForId(this.mTaskId, 1);
                if (tr != null) {
                    Intent intent = tr.getBaseIntent();
                    if (exclude) {
                        intent.addFlags(DumpState.DUMP_VOLUMES);
                    } else {
                        intent.setFlags(intent.getFlags() & -8388609);
                    }
                    Binder.restoreCallingIdentity(origId);
                } else {
                    throw new IllegalArgumentException("Unable to find task ID " + this.mTaskId);
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }
}
