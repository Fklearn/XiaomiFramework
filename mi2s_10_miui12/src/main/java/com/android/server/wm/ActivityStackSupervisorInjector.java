package com.android.server.wm;

import android.app.ActivityOptions;
import android.app.AppGlobals;
import android.app.IApplicationThread;
import android.app.ProfilerInfo;
import android.content.Context;
import android.content.IIntentSender;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.server.am.SplitScreenReporter;
import android.util.Slog;
import com.android.server.am.ActivityManagerService;
import com.android.server.am.PendingIntentController;
import com.android.server.appop.AppOpsService;
import com.android.server.display.ScreenEffectService;
import com.android.server.pm.PackageDexOptimizerManager;
import com.miui.server.XSpaceManagerService;
import java.util.ArrayList;
import miui.os.SystemProperties;

public class ActivityStackSupervisorInjector {
    public static final String EXTRA_PACKAGE_NAME = "android.intent.extra.PACKAGE_NAME";
    private static final String INCALL_PACKAGE_NAME = "com.android.incallui";
    private static final String INCALL_UI_NAME = "com.android.incallui.InCallActivity";
    private static final int MAX_SWITCH_INTERVAL = 1000;
    public static final String MIUI_APP_LOCK_ACTION = "miui.intent.action.CHECK_ACCESS_CONTROL";
    public static final String MIUI_APP_LOCK_ACTIVITY_NAME = "com.miui.applicationlock.ConfirmAccessControl";
    public static final String MIUI_APP_LOCK_PACKAGE_NAME = "com.miui.securitycenter";
    public static final int MIUI_APP_LOCK_REQUEST_CODE = -1001;
    private static final String TAG = "ActivityStackSupervisor";
    private static long mLastIncallUiLaunchTime = -1;
    private static int sActivityRequestId;
    static final ArrayList<String> sSupportsMultiTaskInDockList = new ArrayList<>();

    public static class OpCheckData {
        ActivityInfo newAInfo;
        Intent newIntent;
        ResolveInfo newRInfo;
        Intent orginalintent;
        String resolvedType;
        ActivityRecord resultRecord;
        ActivityStackSupervisor stackSupervisor;
        int startFlags;
        int userId;
    }

    static {
        sSupportsMultiTaskInDockList.add("com.miui.hybrid");
    }

    static void updateScreenPaperMode(String packageName) {
        ScreenEffectService.updateLocalScreenEffect(packageName);
    }

    static boolean isXSpaceActive() {
        return XSpaceManagerService.sIsXSpaceActived;
    }

    static Intent checkXSpaceControl(Context context, ActivityInfo aInfo, Intent intent, boolean fromActivity, int requestCode, int userId, String callingPackage) {
        if (!XSpaceManagerService.isPublicIntent(intent, callingPackage)) {
            return intent;
        }
        return XSpaceManagerService.checkXSpaceControl(context, aInfo, intent, fromActivity, requestCode, userId, callingPackage);
    }

    static boolean isAppLockActivity(Intent intent, ActivityInfo aInfo) {
        return intent != null && aInfo != null && MIUI_APP_LOCK_PACKAGE_NAME.equals(intent.getPackage()) && MIUI_APP_LOCK_ACTION.equals(intent.getAction()) && MIUI_APP_LOCK_ACTIVITY_NAME.equals(aInfo.name);
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x003e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static android.content.pm.ActivityInfo resolveXSpaceIntent(android.content.pm.ActivityInfo r16, android.content.Intent r17, com.android.server.wm.ActivityStackSupervisor r18, android.app.ProfilerInfo r19, java.lang.String r20, int r21, int r22, java.lang.String r23) {
        /*
            r8 = r17
            r9 = r22
            r10 = r23
            if (r9 == 0) goto L_0x000c
            r0 = 999(0x3e7, float:1.4E-42)
            if (r9 != r0) goto L_0x0012
        L_0x000c:
            boolean r0 = com.miui.server.XSpaceManagerService.isPublicIntent(r8, r10)
            if (r0 != 0) goto L_0x0013
        L_0x0012:
            return r16
        L_0x0013:
            boolean r0 = com.miui.server.XSpaceManagerService.shouldResolveAgain(r8, r10)
            if (r0 == 0) goto L_0x0032
            android.content.pm.IPackageManager r0 = android.app.AppGlobals.getPackageManager()     // Catch:{ RemoteException -> 0x002b }
            r1 = 66560(0x10400, float:9.327E-41)
            r11 = r20
            android.content.pm.ResolveInfo r0 = r0.resolveIntent(r8, r11, r1, r9)     // Catch:{ RemoteException -> 0x0029 }
            android.content.pm.ActivityInfo r0 = r0.activityInfo     // Catch:{ RemoteException -> 0x0029 }
            goto L_0x0036
        L_0x0029:
            r0 = move-exception
            goto L_0x002e
        L_0x002b:
            r0 = move-exception
            r11 = r20
        L_0x002e:
            r0.printStackTrace()
            goto L_0x0034
        L_0x0032:
            r11 = r20
        L_0x0034:
            r0 = r16
        L_0x0036:
            int r12 = com.miui.server.XSpaceManagerService.getCachedUserId(r8, r10)
            r1 = -10000(0xffffffffffffd8f0, float:NaN)
            if (r12 == r1) goto L_0x0067
            int r13 = android.os.Binder.getCallingUid()
            long r14 = android.os.Binder.clearCallingIdentity()
            r1 = r18
            r2 = r17
            r3 = r20
            r4 = r21
            r5 = r19
            r6 = r12
            r7 = r13
            android.content.pm.ActivityInfo r0 = r1.resolveActivity(r2, r3, r4, r5, r6, r7)
            android.os.Binder.restoreCallingIdentity(r14)
            r1 = 0
            java.lang.String r2 = "calling_relation"
            boolean r1 = r8.getBooleanExtra(r2, r1)
            if (r1 == 0) goto L_0x0067
            java.lang.String r1 = r0.packageName
            com.miui.server.XSpaceManagerService.putCachedCallingRelation(r1, r10)
        L_0x0067:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityStackSupervisorInjector.resolveXSpaceIntent(android.content.pm.ActivityInfo, android.content.Intent, com.android.server.wm.ActivityStackSupervisor, android.app.ProfilerInfo, java.lang.String, int, int, java.lang.String):android.content.pm.ActivityInfo");
    }

    static ActivityInfo resolveXSpaceIntent(ActivityInfo aInfo, Intent intent, ActivityStackSupervisor stack, ProfilerInfo profilerInfo, String resolvedType, int startFlags, int userId, String callingPackage, int filterCallingUid) {
        ActivityInfo aInfo2;
        Intent intent2 = intent;
        int i = userId;
        String str = callingPackage;
        if ((i != 0 && i != 999) || !XSpaceManagerService.isPublicIntent(intent2, str)) {
            return aInfo;
        }
        if (XSpaceManagerService.shouldResolveAgain(intent2, str)) {
            aInfo2 = stack.resolveIntent(intent, resolvedType, userId, 66560, filterCallingUid).activityInfo;
        } else {
            aInfo2 = aInfo;
        }
        int cachedUserId = XSpaceManagerService.getCachedUserId(intent2, str);
        if (cachedUserId == -10000) {
            return aInfo2;
        }
        int callingUid = Binder.getCallingUid();
        long token = Binder.clearCallingIdentity();
        ActivityInfo aInfo3 = stack.resolveActivity(intent, resolvedType, startFlags, profilerInfo, cachedUserId, callingUid);
        Binder.restoreCallingIdentity(token);
        return aInfo3;
    }

    static void updateInfoBeforeRealStartActivity(ActivityStack stack, IApplicationThread caller, int callingUid, String callingPackage, Intent intent, ActivityInfo aInfo, IBinder resultTo, int requestCode, int userId) {
        MiuiMultiTaskManager.updateMultiTaskInfoIfNeed(stack, aInfo, intent);
    }

    static boolean ensurePackageDexOpt(String packageName) {
        return PackageDexOptimizerManager.getInstance().ensurePackageDexOpt(packageName);
    }

    static boolean isAllowedAppSwitch(ActivityStack stack, String callingPackageName, ActivityInfo aInfo, long lastTime) {
        return isAllowedAppSwitch(stack, callingPackageName, aInfo);
    }

    static boolean isAllowedAppSwitch(ActivityStack stack, String callingPackageName, ActivityInfo aInfo) {
        if (stack == null) {
            return false;
        }
        ActivityRecord topr = stack.topRunningNonDelayedActivityLocked((ActivityRecord) null);
        if (topr != null && topr.info != null && INCALL_UI_NAME.equals(topr.info.name) && !INCALL_PACKAGE_NAME.equals(callingPackageName) && aInfo != null && !INCALL_UI_NAME.equals(aInfo.name) && mLastIncallUiLaunchTime + 1000 > System.currentTimeMillis()) {
            Slog.w("ActivityManager", "app switch:" + aInfo.name + " stopped for " + INCALL_UI_NAME + "in " + 1000 + " ms.Try later.");
            return false;
        } else if (aInfo == null || !INCALL_UI_NAME.equals(aInfo.name)) {
            return true;
        } else {
            mLastIncallUiLaunchTime = System.currentTimeMillis();
            return true;
        }
    }

    public static int noteOperationLocked(int appOp, int callingUid, String callingPackage, Handler handler, OpCheckData checker) {
        int i = appOp;
        int i2 = callingUid;
        String str = callingPackage;
        OpCheckData opCheckData = checker;
        ActivityManagerService service = (ActivityManagerService) ServiceManager.getService("activity");
        AppOpsService mAppOps = (AppOpsService) service.getAppOpsService().asBinder();
        int mode = mAppOps.checkOperation(i, i2, str);
        if (mode != 5) {
            mAppOps.noteOperation(i, i2, str);
            return mode;
        }
        int userId = opCheckData.userId;
        int requestCode = getNextRequestIdLocked();
        PendingIntentController pendingIntentController = service.mPendingIntentController;
        Intent[] intentArr = {opCheckData.orginalintent};
        int userId2 = userId;
        Intent[] intentArr2 = intentArr;
        int mode2 = mode;
        String[] strArr = {opCheckData.orginalintent.resolveType(service.mActivityTaskManager.mContext.getContentResolver())};
        AppOpsService appOpsService = mAppOps;
        ActivityManagerService activityManagerService = service;
        IIntentSender target = pendingIntentController.getIntentSender(2, callingPackage, callingUid, userId, (IBinder) null, (String) null, requestCode, intentArr2, strArr, 1342177280, (Bundle) null);
        Intent intent = new Intent("com.miui.intent.action.REQUEST_PERMISSIONS");
        intent.setPackage("com.lbe.security.miui");
        intent.putExtra("android.intent.extra.PACKAGE_NAME", str);
        intent.putExtra("android.intent.extra.UID", i2);
        intent.putExtra("android.intent.extra.INTENT", new IntentSender(target));
        if (opCheckData.resultRecord != null) {
            intent.putExtra("EXTRA_RESULT_NEEDED", true);
        }
        intent.putExtra("op", i);
        ActivityInfo activityInfo = opCheckData.stackSupervisor.resolveActivity(intent, opCheckData.resolvedType, opCheckData.startFlags, (ProfilerInfo) null, userId2, callingUid);
        if (activityInfo == null) {
            return mode2;
        }
        opCheckData.newAInfo = activityInfo;
        opCheckData.newIntent = intent;
        if (Build.VERSION.SDK_INT >= 24) {
            opCheckData.newRInfo = resolveIntent(intent, opCheckData.resolvedType, userId2);
        }
        Slog.i(TAG, "MIUILOG - Launching Request permission [Activity] uid : " + i2 + "  pkg : " + str + " op : " + i);
        return 0;
    }

    public static boolean supportsMultiTaskInDock(String packageName) {
        return sSupportsMultiTaskInDockList.contains(packageName);
    }

    private static ResolveInfo resolveIntent(Intent intent, String resolvedType, int userId) {
        try {
            return AppGlobals.getPackageManager().resolveIntent(intent, resolvedType, 66560, userId);
        } catch (RemoteException e) {
            return null;
        }
    }

    private static int getNextRequestIdLocked() {
        if (sActivityRequestId >= Integer.MAX_VALUE) {
            sActivityRequestId = 0;
        }
        sActivityRequestId++;
        return sActivityRequestId;
    }

    public static boolean notPauseAtFreeformMode(ActivityStack focusStack, ActivityStack curStack) {
        if (!supportsFreeform()) {
            return false;
        }
        if ((focusStack.getWindowingMode() == 1 && curStack.getWindowingMode() == 5) || focusStack.getWindowingMode() == 5) {
            return true;
        }
        return false;
    }

    public static boolean supportsFreeform() {
        return SystemProperties.getBoolean("persist.sys.miui_optimization", !SplitScreenReporter.ACTION_ENTER_SPLIT.equals(SystemProperties.get("ro.miui.cts")));
    }

    public static TaskRecord exitfreeformIfNeeded(TaskRecord task, int taskId, int windowMode, ActivityStackSupervisor supervisor) {
        TaskRecord tTask = task;
        if (tTask == null || tTask.getWindowingMode() != 5 || windowMode == 5) {
            return tTask;
        }
        ActivityOptions op = ActivityOptions.makeBasic();
        op.setLaunchWindowingMode(1);
        return supervisor.mRootActivityContainer.anyTaskForId(taskId, 2, op, true);
    }

    public static void updateApplicationConfiguration(ActivityStackSupervisor stackSupervisor, Configuration globalConfiguration, String packageName) {
        ActivityRecord topActivity;
        ActivityStack topStack = stackSupervisor.mRootActivityContainer.getTopDisplayFocusedStack();
        if (topStack != null && (topActivity = topStack.topRunningActivityLocked()) != null && topActivity.getWindowingMode() == 5 && packageName.equals(topActivity.packageName)) {
            Rect rect = topActivity.getConfiguration().windowConfiguration.getBounds();
            globalConfiguration.orientation = rect.height() > rect.width() ? 1 : 2;
            globalConfiguration.windowConfiguration.setWindowingMode(5);
            globalConfiguration.windowConfiguration.setBounds(topActivity.getConfiguration().windowConfiguration.getBounds());
        }
    }
}
