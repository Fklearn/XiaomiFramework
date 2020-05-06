package com.android.server.wm;

import android.app.IApplicationThread;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import com.android.server.am.ProcessManagerService;
import com.android.server.am.ProcessPolicy;
import com.android.server.pm.DumpState;
import com.miui.server.AccessController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WindowProcessUtils {
    private static final String TAG = WindowProcessUtils.class.getSimpleName();

    public static boolean isProcessHasActivityInOtherTaskLocked(WindowProcessController app, int curTaskId) {
        for (int i = 0; i < app.getAllActivities().size(); i++) {
            TaskRecord otherTask = app.getAllActivities().get(i).getTaskRecord();
            if (otherTask != null && curTaskId != otherTask.taskId && otherTask.inRecents && isTaskVisibleInRecents(otherTask)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isTaskVisibleInRecents(TaskRecord task) {
        if (task == null) {
            return false;
        }
        if (task.intent == null) {
            return true;
        }
        if (!task.isAvailable || (task.intent.getFlags() & DumpState.DUMP_VOLUMES) != 0) {
            return false;
        }
        return true;
    }

    public static Map<Integer, String> getPerceptibleRecentAppList(ActivityTaskManagerService atms) {
        ActivityRecord r;
        Map<Integer, String> taskPackageMap = new HashMap<>();
        synchronized (atms.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityStack dockedStack = getMultiWindowStackLocked(atms);
                if (!(dockedStack == null || (r = dockedStack.topRunningActivityLocked()) == null || r.getTaskRecord() == null)) {
                    taskPackageMap.put(Integer.valueOf(r.getTaskRecord().taskId), r.packageName);
                }
                Iterator<TaskRecord> it = atms.getRecentTasks().getRawTasks().iterator();
                while (true) {
                    if (it.hasNext()) {
                        TaskRecord task = it.next();
                        if (task != null) {
                            if (task.getStack() != null) {
                                String taskPackageName = getTaskPackageNameLocked(task);
                                if (!isTaskInMultiWindowStackLocked(task) && !TextUtils.isEmpty(taskPackageName)) {
                                    if (!taskPackageName.equals(AccessController.PACKAGE_SYSTEMUI)) {
                                        taskPackageMap.put(Integer.valueOf(task.taskId), taskPackageName);
                                        break;
                                    }
                                }
                            }
                        }
                    } else {
                        break;
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
        String str = TAG;
        Log.d(str, "getPerceptibleRecentAppList: " + Arrays.toString(taskPackageMap.values().toArray()));
        return taskPackageMap;
    }

    private static ActivityStack getMultiWindowStackLocked(ActivityTaskManagerService atms) {
        int numDisplays = atms.mStackSupervisor.mRootActivityContainer.getChildCount();
        for (int displayNdx = 0; displayNdx < numDisplays; displayNdx++) {
            ActivityDisplay display = atms.mStackSupervisor.mRootActivityContainer.getChildAt(displayNdx);
            for (int stackNdx = display.getChildCount() - 1; stackNdx >= 0; stackNdx--) {
                ActivityStack stack = display.getChildAt(stackNdx);
                if (isMultiWindowStackLocked(stack)) {
                    return stack;
                }
            }
        }
        return null;
    }

    private static boolean isTaskInMultiWindowStackLocked(TaskRecord task) {
        ActivityRecord topActivity;
        if (task == null || (topActivity = task.topRunningActivityLocked()) == null || topActivity.getWindowingMode() != 3) {
            return false;
        }
        return true;
    }

    private static boolean isMultiWindowStackLocked(ActivityStack stack) {
        return stack != null && stack.getWindowingMode() == 3;
    }

    private static String getTaskPackageNameLocked(TaskRecord task) {
        if (task == null || task.getBaseIntent() == null || task.getBaseIntent().getComponent() == null) {
            return null;
        }
        return task.getBaseIntent().getComponent().getPackageName();
    }

    public static WindowProcessController getTaskTopApp(int taskId, ActivityTaskManagerService atms) {
        WindowProcessController taskTopAppLocked;
        synchronized (atms.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                taskTopAppLocked = getTaskTopAppLocked(atms.getRecentTasks().getTask(taskId));
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return taskTopAppLocked;
    }

    private static WindowProcessController getTaskRootOrTopAppLocked(TaskRecord task) {
        if (task == null) {
            return null;
        }
        ActivityRecord record = task.getRootActivity();
        if (record == null) {
            record = task.topRunningActivityLocked();
        }
        if (record == null) {
            return null;
        }
        return record.app;
    }

    private static WindowProcessController getTaskTopAppLocked(TaskRecord task) {
        ActivityRecord topActivity;
        if (task == null || (topActivity = task.topRunningActivityLocked()) == null) {
            return null;
        }
        return topActivity.app;
    }

    private static int getTaskTopAppUidLocked(TaskRecord task) {
        ActivityRecord topActivity;
        if (task == null || (topActivity = task.topRunningActivityLocked()) == null || topActivity.appInfo == null) {
            return -1;
        }
        return topActivity.appInfo.uid;
    }

    private static String getTaskTopAppProcessNameLocked(TaskRecord task) {
        ActivityRecord topActivity;
        if (task == null || (topActivity = task.topRunningActivityLocked()) == null || topActivity.appInfo == null) {
            return null;
        }
        return topActivity.processName;
    }

    public static boolean isRemoveTaskDisabled(int taskId, String packageName, ActivityTaskManagerService atms) {
        synchronized (atms.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                TaskRecord task = atms.getRecentTasks().getTask(taskId);
                if (task != null) {
                    boolean equals = TextUtils.equals(packageName, getTaskTopAppProcessNameLocked(task));
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return equals;
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

    public static void removeAllTasks(ProcessManagerService pms, int userId, ActivityTaskManagerService atms) {
        synchronized (atms.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                List<TaskRecord> removedTasks = new ArrayList<>();
                Iterator<TaskRecord> it = atms.getRecentTasks().getRawTasks().iterator();
                while (it.hasNext()) {
                    TaskRecord task = it.next();
                    if (task.userId == userId) {
                        WindowProcessController app = getTaskRootOrTopAppLocked(task);
                        if (app == null) {
                            removedTasks.add(task);
                        } else if (pms.isTrimMemoryEnable(app.mInfo.packageName)) {
                            removedTasks.add(task);
                        }
                    }
                }
                for (TaskRecord task2 : removedTasks) {
                    removeTaskLocked(task2, atms);
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

    public static void removeTasks(List<Integer> taskIdList, Set<Integer> whiteTaskSet, ProcessPolicy processPolicy, ActivityTaskManagerService atms, List<String> whiteList) {
        if (taskIdList != null && !taskIdList.isEmpty()) {
            synchronized (atms.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    List<TaskRecord> removedTasks = new ArrayList<>();
                    Iterator<TaskRecord> it = atms.getRecentTasks().getRawTasks().iterator();
                    while (it.hasNext()) {
                        TaskRecord task = it.next();
                        if (task != null) {
                            String taskPackageName = getTaskPackageNameLocked(task);
                            if (!processPolicy.isLockedApplication(taskPackageName, task.userId) && (whiteTaskSet == null || !whiteTaskSet.contains(Integer.valueOf(task.taskId)))) {
                                if (whiteList == null || !whiteList.contains(taskPackageName)) {
                                    if (taskIdList.contains(Integer.valueOf(task.taskId)) && !TextUtils.isEmpty(taskPackageName)) {
                                        removedTasks.add(task);
                                    }
                                }
                            }
                        }
                    }
                    for (TaskRecord task2 : removedTasks) {
                        removeTaskLocked(task2, atms);
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

    public static void removeTasksInPackages(List<String> packages, int userId, ProcessPolicy processPolicy, ActivityTaskManagerService atms) {
        if (packages != null && !packages.isEmpty()) {
            synchronized (atms.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    List<TaskRecord> removedTasks = new ArrayList<>();
                    Iterator<TaskRecord> it = atms.getRecentTasks().getRawTasks().iterator();
                    while (it.hasNext()) {
                        TaskRecord task = it.next();
                        String taskPackageName = getTaskPackageNameLocked(task);
                        if (!processPolicy.isLockedApplication(taskPackageName, userId)) {
                            if (task.userId == userId && !TextUtils.isEmpty(taskPackageName) && packages.contains(taskPackageName)) {
                                removedTasks.add(task);
                            }
                        }
                    }
                    for (TaskRecord task2 : removedTasks) {
                        removeTaskLocked(task2, atms);
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

    public static void removeTask(int taskId, ActivityTaskManagerService atms) {
        synchronized (atms.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                removeTaskLocked(atms.getRecentTasks().getTask(taskId), atms);
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    private static void removeTaskLocked(TaskRecord task, ActivityTaskManagerService atms) {
        if (task != null) {
            task.removeTaskActivitiesLocked(false, "remove-task");
            atms.getRecentTasks().remove(task);
            task.removedFromRecents();
            if (!(task.getBaseIntent() == null || task.getBaseIntent().getComponent() == null)) {
                atms.mStackSupervisor.cleanUpRemovedTaskLocked(task, false, true);
            }
            atms.getLockTaskController().clearLockedTask(task);
            if (task.isPersistable) {
                atms.notifyTaskPersisterLocked((TaskRecord) null, true);
            }
            String str = TAG;
            Log.d(str, "remove task: " + task.toString());
        }
    }

    public static ApplicationInfo getMultiWindowForegroundAppInfoLocked(ActivityTaskManagerService atms) {
        ActivityRecord multiWindowActivity;
        ApplicationInfo applicationInfo = null;
        synchronized (atms.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityStack multiWindowStack = getMultiWindowStackLocked(atms);
                if (!(multiWindowStack == null || (multiWindowActivity = multiWindowStack.topRunningActivityLocked()) == null)) {
                    applicationInfo = multiWindowActivity.appInfo;
                }
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return applicationInfo;
    }

    public static int getTopRunningPidLocked(ActivityTaskManagerService atms) {
        ActivityRecord record;
        synchronized (atms.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityStack topStack = atms.mStackSupervisor.mRootActivityContainer.getTopDisplayFocusedStack();
                if (topStack == null || (record = topStack.topRunningActivityLocked()) == null || record.app == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return 0;
                }
                int pid = record.app.getPid();
                WindowManagerService.resetPriorityAfterLockedSection();
                return pid;
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    public static WindowProcessController getTopRunningProcessController(ActivityTaskManagerService atms) {
        ActivityRecord record;
        synchronized (atms.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityStack topStack = atms.mStackSupervisor.mRootActivityContainer.getTopDisplayFocusedStack();
                if (topStack == null || (record = topStack.topRunningActivityLocked()) == null || record.app == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return null;
                }
                WindowProcessController windowProcessController = record.app;
                WindowManagerService.resetPriorityAfterLockedSection();
                return windowProcessController;
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    public static ArrayList<Intent> getTaskIntentForToken(IBinder token, ActivityTaskManagerService atms) {
        synchronized (atms.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord activityRecord = ActivityRecord.isInStackLocked(token);
                if (activityRecord != null) {
                    ArrayList<Intent> arrayList = new ArrayList<>();
                    Iterator<ActivityRecord> it = activityRecord.getTaskRecord().mActivities.iterator();
                    while (it.hasNext()) {
                        arrayList.add(new Intent(it.next().intent));
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return arrayList;
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

    public static HashMap<String, Object> getTopRunningActivityInfo(ActivityTaskManagerService atms) {
        ActivityRecord r;
        synchronized (atms.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityStack activityStack = atms.mStackSupervisor.mRootActivityContainer.getTopDisplayFocusedStack();
                if (activityStack == null || (r = activityStack.topRunningActivityLocked()) == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return null;
                }
                HashMap<String, Object> map = new HashMap<>();
                map.put("packageName", r.packageName);
                map.put("token", r.appToken);
                map.put("userId", Integer.valueOf(r.mUserId));
                map.put("intent", r.intent);
                WindowManagerService.resetPriorityAfterLockedSection();
                return map;
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    public static String getActivityComponentName(ActivityRecord activity) {
        return activity.shortComponentName;
    }

    public static String getCallerPackageName(ActivityTaskManagerService atms, IApplicationThread caller) {
        synchronized (atms.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                WindowProcessController wpc = atms.getProcessController(caller);
                if (wpc != null) {
                    if (wpc.mInfo != null) {
                        String str = wpc.mInfo.packageName;
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return str;
                    }
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

    public static int getCallerUid(ActivityTaskManagerService atms, IApplicationThread caller) {
        synchronized (atms.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                WindowProcessController wpc = atms.getProcessController(caller);
                if (wpc != null) {
                    if (wpc.mInfo != null) {
                        int i = wpc.mInfo.uid;
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return i;
                    }
                }
                WindowManagerService.resetPriorityAfterLockedSection();
                return -1;
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    public static boolean isPackageRunning(ActivityTaskManagerService atms, String packageName, String processName, int uid) {
        if (TextUtils.isEmpty(packageName) || TextUtils.isEmpty(processName) || uid == 0) {
            return false;
        }
        synchronized (atms.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                if (atms.getProcessController(processName, uid) != null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return true;
                }
                SparseArray<WindowProcessController> pidMap = atms.mProcessMap.getPidMap();
                for (int i = pidMap.size() - 1; i >= 0; i--) {
                    WindowProcessController wpc = pidMap.get(pidMap.keyAt(i));
                    if (wpc != null) {
                        if (wpc.mUid == uid) {
                            if (wpc.getThread() != null && !wpc.isCrashing() && !wpc.isNotResponding() && wpc.mPkgList.contains(packageName)) {
                                WindowManagerService.resetPriorityAfterLockedSection();
                                return true;
                            }
                        }
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
}
