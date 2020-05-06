package com.android.server.wm;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.app.IApplicationThread;
import android.app.IMiuiActivityObserver;
import android.app.ProfilerInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ParceledListSlice;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.miui.AppOpsUtils;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.server.am.SplitScreenReporter;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.EventLog;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.app.IPerfShielder;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.ArrayUtils;
import com.android.server.LocalServices;
import com.android.server.MiuiFgThread;
import com.android.server.am.ActivityManagerService;
import com.android.server.am.EventLogTags;
import com.android.server.am.ExtraActivityManagerService;
import com.android.server.am.PendingIntentRecordInjector;
import com.android.server.appop.AppOpsService;
import com.android.server.wm.ActivityStack;
import com.miui.hybrid.hook.HookClient;
import com.miui.server.AppRunningControlService;
import com.miui.server.PerfShielderService;
import com.miui.server.SplashScreenServiceDelegate;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miui.mqsas.sdk.MQSEventManagerDelegate;
import miui.mqsas.sdk.event.PackageForegroundEvent;
import miui.process.ProcessManagerInternal;

public class ActivityTaskManagerServiceInjector {
    private static final ArrayMap<String, String> ACTION_TO_RUNTIME_PERMISSION = new ArrayMap<>();
    private static final int ACTIVITY_RESTRICTION_APPOP = 2;
    private static final int ACTIVITY_RESTRICTION_NONE = 0;
    private static final int ACTIVITY_RESTRICTION_PERMISSION = 1;
    private static final int PACKAGE_FORE_BUFFER_SIZE = SystemProperties.getInt("sys.proc.fore_pkg_buffer", 15);
    private static final String TAG = "ActivityTaskManagerServiceInjector";
    private static String lastForegroundPkg = null;
    private static ApplicationInfo lastMultiWindowAppInfo = null;
    private static List<String> mDefaultHomePkgNames;
    private static int mLastStartActivityUid;
    private static SplashScreenServiceDelegate mSplashScreenServiceDelegate;
    private static boolean mSystemReady;
    private static final List<PackageForegroundEvent> sCachedForegroundPackageList = new ArrayList();
    static String sPackageHoldOn;
    private static IPerfShielder sPerfService;
    private static boolean sPerfServiceObtained;
    private static boolean sSystemBootCompleted;

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x003a, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static android.os.IBinder finishActivity(com.android.server.wm.ActivityTaskManagerService r5, android.os.IBinder r6, int r7, android.content.Intent r8) {
        /*
            if (r6 != 0) goto L_0x0044
            com.android.server.wm.WindowManagerGlobalLock r0 = r5.mGlobalLock
            monitor-enter(r0)
            com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x003e }
            android.content.Context r1 = r5.mContext     // Catch:{ all -> 0x003e }
            java.lang.String r2 = "security"
            java.lang.Object r1 = r1.getSystemService(r2)     // Catch:{ all -> 0x003e }
            miui.security.SecurityManager r1 = (miui.security.SecurityManager) r1     // Catch:{ all -> 0x003e }
            com.android.server.wm.ActivityStack r2 = r5.getTopDisplayFocusedStack()     // Catch:{ all -> 0x003e }
            com.android.server.wm.ActivityRecord r2 = r2.topRunningActivityLocked()     // Catch:{ all -> 0x003e }
            if (r2 == 0) goto L_0x0039
            java.lang.String r3 = r2.packageName     // Catch:{ all -> 0x003e }
            if (r3 == 0) goto L_0x0039
            java.lang.String r3 = r2.packageName     // Catch:{ all -> 0x003e }
            boolean r3 = r1.getApplicationAccessControlEnabled(r3)     // Catch:{ all -> 0x003e }
            if (r3 == 0) goto L_0x0039
            java.lang.String r3 = r2.packageName     // Catch:{ all -> 0x003e }
            r4 = 0
            boolean r3 = r1.checkAccessControlPass(r3, r4)     // Catch:{ all -> 0x003e }
            if (r3 != 0) goto L_0x0039
            android.view.IApplicationToken$Stub r3 = r2.appToken     // Catch:{ all -> 0x003e }
            r6 = r3
            monitor-exit(r0)     // Catch:{ all -> 0x003e }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return r3
        L_0x0039:
            monitor-exit(r0)     // Catch:{ all -> 0x003e }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            goto L_0x0044
        L_0x003e:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x003e }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            throw r1
        L_0x0044:
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityTaskManagerServiceInjector.finishActivity(com.android.server.wm.ActivityTaskManagerService, android.os.IBinder, int, android.content.Intent):android.os.IBinder");
    }

    public static String hookGetCallingPkg(ActivityTaskManagerService atms, IBinder token, String originCallingPkg) {
        return HookClient.hookGetCallingPkg(atms.getPackageForToken(token), originCallingPkg);
    }

    public static Intent hookStartActivity(Intent intent, String callingPackage) {
        return HookClient.redirectStartActivity(intent, callingPackage);
    }

    static boolean isGetTasksOpAllowed(ActivityTaskManagerService atms, String caller, int pid, int uid) {
        AppOpsService opsService = atms.getAppOpsService();
        if (AppOpsUtils.isXOptMode() || !"getRunningAppProcesses".equals(caller)) {
            return false;
        }
        String packageName = null;
        synchronized (atms.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                WindowProcessController wpc = atms.mProcessMap.getProcess(pid);
                if (!(wpc == null || wpc.mInfo == null)) {
                    packageName = wpc.mInfo.packageName;
                }
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        if (packageName != null && opsService.checkOperation(10019, uid, packageName) == 0) {
            return true;
        }
        return false;
    }

    static void onSystemReady(Context context) {
        mSystemReady = true;
        mSplashScreenServiceDelegate = new SplashScreenServiceDelegate(context);
    }

    static void activityIdle(ActivityInfo aInfo) {
        if (!mSystemReady) {
            Slog.w(TAG, "System was not ready,activityIdle failed.");
        } else if (aInfo == null) {
            Slog.w(TAG, "aInfo is null!");
        } else {
            SplashScreenServiceDelegate splashScreenServiceDelegate = mSplashScreenServiceDelegate;
            if (splashScreenServiceDelegate != null) {
                splashScreenServiceDelegate.activityIdle(aInfo);
            }
        }
    }

    static void destroyActivity(ActivityInfo aInfo) {
        if (!mSystemReady) {
            Slog.w(TAG, "System was not ready,destroyActivity failed.");
        } else if (aInfo == null) {
            Slog.w(TAG, "aInfo is null!");
        } else {
            SplashScreenServiceDelegate splashScreenServiceDelegate = mSplashScreenServiceDelegate;
            if (splashScreenServiceDelegate != null) {
                splashScreenServiceDelegate.destroyActivity(aInfo);
            }
        }
    }

    static Intent requestSplashScreen(Intent intent, ActivityInfo aInfo) {
        if (!mSystemReady) {
            Slog.w(TAG, "System was not ready,request splash failed.");
            return intent;
        } else if (intent == null || aInfo == null) {
            Slog.w(TAG, "Intent or aInfo is null!");
            return intent;
        } else {
            SplashScreenServiceDelegate splashScreenServiceDelegate = mSplashScreenServiceDelegate;
            if (splashScreenServiceDelegate != null) {
                return splashScreenServiceDelegate.requestSplashScreen(intent, aInfo);
            }
            Slog.e(TAG, "mSplashScreenServiceDelegate is null!");
            return intent;
        }
    }

    static Intent requestSplashScreen(Intent intent, ActivityInfo aInfo, SafeActivityOptions options, IApplicationThread caller, ActivityStackSupervisor supervisor, ActivityTaskManagerService service) {
        ActivityOptions activityOptions;
        ActivityOptions checkedOptions;
        if (!mSystemReady) {
            Slog.w(TAG, "System was not ready,request splash failed.");
            return intent;
        } else if (intent == null || aInfo == null) {
            Slog.w(TAG, "Intent or aInfo is null!");
            return intent;
        } else {
            synchronized (service.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    WindowProcessController callerApp = service.getProcessController(caller);
                    if (options != null) {
                        activityOptions = options.getOptions(intent, aInfo, callerApp, supervisor);
                    } else {
                        activityOptions = null;
                    }
                    checkedOptions = activityOptions;
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            if (checkedOptions == null || checkedOptions.getLaunchWindowingMode() != 5) {
                SplashScreenServiceDelegate splashScreenServiceDelegate = mSplashScreenServiceDelegate;
                if (splashScreenServiceDelegate != null) {
                    return splashScreenServiceDelegate.requestSplashScreen(intent, aInfo);
                }
                Slog.e(TAG, "mSplashScreenServiceDelegate is null!");
                return intent;
            }
            Slog.w(TAG, "The Activity is in freeForm windowing mode !");
            return intent;
        }
    }

    static ActivityInfo resolveSplashIntent(ActivityInfo aInfo, Intent intent, ActivityStackSupervisor stack, ProfilerInfo profilerInfo, int userId) {
        ComponentName component;
        if (intent == null || (component = intent.getComponent()) == null || !SplashScreenServiceDelegate.SPLASHSCREEN_PACKAGE.equals(component.getPackageName()) || !SplashScreenServiceDelegate.SPLASHSCREEN_ACTIVITY.equals(component.getClassName())) {
            return aInfo;
        }
        return stack.resolveActivity(intent, (String) null, 0, profilerInfo, userId, Binder.getCallingUid());
    }

    static ActivityInfo resolveCheckIntent(ActivityInfo aInfo, Intent intent, ActivityStackSupervisor stack, ProfilerInfo profilerInfo, int userId) {
        if (intent == null || intent.getComponent() != null) {
            return aInfo;
        }
        boolean transform = false;
        if (ActivityStackSupervisorInjector.MIUI_APP_LOCK_ACTION.equals(intent.getAction()) || "android.app.action.CHECK_ACCESS_CONTROL_PAD".equals(intent.getAction()) || "android.app.action.CHECK_ALLOW_START_ACTIVITY".equals(intent.getAction()) || "android.app.action.CHECK_ALLOW_START_ACTIVITY_PAD".equals(intent.getAction()) || "com.miui.gamebooster.action.ACCESS_WINDOWCALLACTIVITY".equals(intent.getAction()) || AppRunningControlService.isBlockActivity(intent)) {
            if (userId == 999) {
                userId = 0;
            }
            transform = true;
        }
        if (!transform) {
            return aInfo;
        }
        return stack.resolveActivity(intent, (String) null, 0, profilerInfo, userId, Binder.getCallingUid());
    }

    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0071, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0074, code lost:
        if (r1 == false) goto L_0x00a7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0076, code lost:
        r4 = ((miui.security.SecurityManager) r26.getSystemService("security")).getCheckIntent(r26, r25, r37, r11.packageName, r30, r32, r33, r2, android.os.UserHandle.getUserId(r11.applicationInfo.uid), r36, r38);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00a3, code lost:
        if (r4 == null) goto L_0x00c8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00a7, code lost:
        r3 = r26;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:?, code lost:
        return r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static android.content.Intent checkStartActivityPermission(android.content.Context r26, com.android.server.wm.ActivityTaskManagerService r27, android.app.IApplicationThread r28, android.content.pm.ActivityInfo r29, android.content.Intent r30, java.lang.String r31, boolean r32, int r33, boolean r34, int r35, int r36, java.lang.String r37, android.os.Bundle r38) {
        /*
            r10 = r27
            r11 = r29
            r15 = r30
            if (r11 == 0) goto L_0x00c6
            r1 = 0
            r2 = 0
            r12 = 0
            r13 = 0
            com.android.server.wm.WindowManagerGlobalLock r14 = r10.mGlobalLock
            monitor-enter(r14)
            com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x00bc }
            if (r28 == 0) goto L_0x0038
            com.android.server.wm.WindowProcessController r0 = r27.getProcessController(r28)     // Catch:{ all -> 0x00bc }
            r1 = r0
            if (r1 != 0) goto L_0x0020
            monitor-exit(r14)     // Catch:{ all -> 0x00bc }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return r15
        L_0x0020:
            android.content.pm.ApplicationInfo r0 = r1.mInfo     // Catch:{ all -> 0x00bc }
            r2 = r0
            int r0 = r1.mUid     // Catch:{ all -> 0x00bc }
            boolean r0 = com.miui.server.ConfirmStartHelper.hasOpenConfirmStartPermission(r0, r15)     // Catch:{ all -> 0x00bc }
            if (r0 == 0) goto L_0x0030
            r24 = r1
            r25 = r2
            goto L_0x003c
        L_0x0030:
            java.lang.SecurityException r0 = new java.lang.SecurityException     // Catch:{ all -> 0x00bc }
            java.lang.String r3 = "Permission denied: No permission to open CHECK_ALLOW_START_ACTIVITY"
            r0.<init>(r3)     // Catch:{ all -> 0x00bc }
            throw r0     // Catch:{ all -> 0x00bc }
        L_0x0038:
            r24 = r1
            r25 = r2
        L_0x003c:
            r1 = r27
            r2 = r24
            r3 = r36
            r4 = r37
            r5 = r29
            r6 = r30
            r7 = r31
            r8 = r34
            r9 = r38
            boolean r0 = checkStartActivityLocked(r1, r2, r3, r4, r5, r6, r7, r8, r9)     // Catch:{ all -> 0x00b4 }
            r1 = r0
            if (r1 == 0) goto L_0x006f
            java.lang.String r0 = r11.packageName     // Catch:{ all -> 0x0066 }
            java.lang.String r2 = r11.processName     // Catch:{ all -> 0x0066 }
            android.content.pm.ApplicationInfo r3 = r11.applicationInfo     // Catch:{ all -> 0x0066 }
            int r3 = r3.uid     // Catch:{ all -> 0x0066 }
            boolean r0 = packageIsRunningLocked(r10, r0, r2, r3)     // Catch:{ all -> 0x0066 }
            if (r0 == 0) goto L_0x006f
            r0 = 1
            r2 = r0
            goto L_0x0070
        L_0x0066:
            r0 = move-exception
            r3 = r26
            r13 = r1
            r1 = r24
            r2 = r25
            goto L_0x00bf
        L_0x006f:
            r2 = r12
        L_0x0070:
            monitor-exit(r14)     // Catch:{ all -> 0x00aa }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            if (r1 == 0) goto L_0x00a7
            java.lang.String r0 = "security"
            r3 = r26
            java.lang.Object r0 = r3.getSystemService(r0)
            miui.security.SecurityManager r0 = (miui.security.SecurityManager) r0
            java.lang.String r4 = r11.packageName
            android.content.pm.ApplicationInfo r5 = r11.applicationInfo
            int r5 = r5.uid
            int r21 = android.os.UserHandle.getUserId(r5)
            r12 = r0
            r13 = r26
            r14 = r25
            r15 = r37
            r16 = r4
            r17 = r30
            r18 = r32
            r19 = r33
            r20 = r2
            r22 = r36
            r23 = r38
            android.content.Intent r4 = r12.getCheckIntent(r13, r14, r15, r16, r17, r18, r19, r20, r21, r22, r23)
            if (r4 == 0) goto L_0x00c8
            r5 = r4
            goto L_0x00ca
        L_0x00a7:
            r3 = r26
            goto L_0x00c8
        L_0x00aa:
            r0 = move-exception
            r3 = r26
            r13 = r1
            r12 = r2
            r1 = r24
            r2 = r25
            goto L_0x00bf
        L_0x00b4:
            r0 = move-exception
            r3 = r26
            r1 = r24
            r2 = r25
            goto L_0x00bf
        L_0x00bc:
            r0 = move-exception
            r3 = r26
        L_0x00bf:
            monitor-exit(r14)     // Catch:{ all -> 0x00c4 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            throw r0
        L_0x00c4:
            r0 = move-exception
            goto L_0x00bf
        L_0x00c6:
            r3 = r26
        L_0x00c8:
            r5 = r30
        L_0x00ca:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityTaskManagerServiceInjector.checkStartActivityPermission(android.content.Context, com.android.server.wm.ActivityTaskManagerService, android.app.IApplicationThread, android.content.pm.ActivityInfo, android.content.Intent, java.lang.String, boolean, int, boolean, int, int, java.lang.String, android.os.Bundle):android.content.Intent");
    }

    private static boolean packageIsRunningLocked(ActivityTaskManagerService atms, String packageName, String processName, int uid) {
        if (packageName == null || packageName.isEmpty() || processName == null || processName.isEmpty() || uid == 0) {
            return false;
        }
        if (atms.getProcessController(processName, uid) != null) {
            return true;
        }
        SparseArray<WindowProcessController> pidMap = atms.mProcessMap.getPidMap();
        for (int i = pidMap.size() - 1; i >= 0; i--) {
            WindowProcessController app = pidMap.get(pidMap.keyAt(i));
            if (app != null && app.mUid == uid && app.getThread() != null && !app.isCrashing() && !app.isNotResponding() && app.mPkgList.contains(packageName)) {
                return true;
            }
        }
        return false;
    }

    private static boolean checkStartActivityLocked(ActivityTaskManagerService atms, WindowProcessController callerApp, int callingUid, String callingPackage, ActivityInfo aInfo, Intent intent, String resolvedType, boolean ignoreTargetSecurity, Bundle bOptions) {
        int callingPid;
        int callingUid2;
        ActivityTaskManagerService activityTaskManagerService = atms;
        WindowProcessController windowProcessController = callerApp;
        ActivityInfo activityInfo = aInfo;
        long startTime = SystemClock.elapsedRealtime();
        if (windowProcessController != null) {
            callingPid = callerApp.getPid();
            callingUid2 = windowProcessController.mUid;
        } else if (callingUid >= 0) {
            callingUid2 = callingUid;
            callingPid = -1;
        } else {
            callingPid = Binder.getCallingPid();
            callingUid2 = Binder.getCallingUid();
        }
        long origId = Binder.clearCallingIdentity();
        try {
            if (ActivityTaskManagerService.checkPermission("android.permission.START_ANY_ACTIVITY", callingPid, callingUid2) == 0) {
                Binder.restoreCallingIdentity(origId);
                return true;
            }
            int componentRestriction = getComponentRestrictionForCallingPackage(atms, aInfo, callingPackage, callingPid, callingUid2, ignoreTargetSecurity);
            try {
                int actionRestriction = getActionRestrictionForCallingPackage(activityTaskManagerService, intent.getAction(), callingPackage, callingPid, callingUid2);
                if (componentRestriction == 1) {
                    int i = callingPid;
                } else if (actionRestriction == 1) {
                    int i2 = callingUid2;
                    int i3 = callingPid;
                } else if (actionRestriction == 2) {
                    Binder.restoreCallingIdentity(origId);
                    return false;
                } else if (componentRestriction == 2) {
                    Binder.restoreCallingIdentity(origId);
                    return false;
                } else {
                    ActivityOptions options = ActivityOptions.fromBundle(bOptions);
                    if (options != null) {
                        try {
                            if (!(options.getLaunchTaskId() == -1 || ActivityTaskManagerService.checkPermission("android.permission.START_TASKS_FROM_RECENTS", callingPid, callingUid2) == 0)) {
                                Binder.restoreCallingIdentity(origId);
                                return false;
                            }
                        } catch (Exception e) {
                            e = e;
                            int i4 = callingUid2;
                            int i5 = callingPid;
                            try {
                                Slog.w(TAG, "checkStartActivityLocked: An exception occured. ", e);
                                Binder.restoreCallingIdentity(origId);
                                return false;
                            } catch (Throwable th) {
                                e = th;
                                Binder.restoreCallingIdentity(origId);
                                throw e;
                            }
                        } catch (Throwable th2) {
                            e = th2;
                            int i6 = callingUid2;
                            int i7 = callingPid;
                            Binder.restoreCallingIdentity(origId);
                            throw e;
                        }
                    }
                    int i8 = callingUid2;
                    int i9 = callingPid;
                    try {
                        if (!activityTaskManagerService.mIntentFirewall.checkStartActivity(intent, callingUid2, callingPid, resolvedType, activityInfo.applicationInfo)) {
                            Binder.restoreCallingIdentity(origId);
                            return false;
                        }
                        if (activityTaskManagerService.mController != null) {
                            try {
                                if (!activityTaskManagerService.mController.activityStarting(intent.cloneFilter(), activityInfo.applicationInfo.packageName)) {
                                    Binder.restoreCallingIdentity(origId);
                                    return false;
                                }
                            } catch (RemoteException e2) {
                            }
                        }
                        Binder.restoreCallingIdentity(origId);
                        checkTime(startTime, "checkStartActivityLocked");
                        return true;
                    } catch (Exception e3) {
                        e = e3;
                        Slog.w(TAG, "checkStartActivityLocked: An exception occured. ", e);
                        Binder.restoreCallingIdentity(origId);
                        return false;
                    }
                }
                Binder.restoreCallingIdentity(origId);
                return false;
            } catch (Exception e4) {
                e = e4;
                int i10 = callingUid2;
                int i11 = callingPid;
                Slog.w(TAG, "checkStartActivityLocked: An exception occured. ", e);
                Binder.restoreCallingIdentity(origId);
                return false;
            } catch (Throwable th3) {
                e = th3;
                int i12 = callingUid2;
                int i13 = callingPid;
                Binder.restoreCallingIdentity(origId);
                throw e;
            }
        } catch (Exception e5) {
            e = e5;
            String str = callingPackage;
            int i102 = callingUid2;
            int i112 = callingPid;
            Slog.w(TAG, "checkStartActivityLocked: An exception occured. ", e);
            Binder.restoreCallingIdentity(origId);
            return false;
        } catch (Throwable th4) {
            e = th4;
            String str2 = callingPackage;
            int i122 = callingUid2;
            int i132 = callingPid;
            Binder.restoreCallingIdentity(origId);
            throw e;
        }
    }

    private static void checkTime(long startTime, String where) {
        long now = SystemClock.elapsedRealtime();
        if (now - startTime > 1000) {
            Slog.w(TAG, "MIUILOG-checkTime:Slow operation: " + (now - startTime) + "ms so far, now at " + where);
        }
    }

    private static int getComponentRestrictionForCallingPackage(ActivityTaskManagerService atms, ActivityInfo activityInfo, String callingPackage, int callingPid, int callingUid, boolean ignoreTargetSecurity) {
        int opCode;
        if (!ignoreTargetSecurity && ActivityTaskManagerService.checkComponentPermission(activityInfo.permission, callingPid, callingUid, activityInfo.applicationInfo.uid, activityInfo.exported) == -1) {
            return 1;
        }
        if (activityInfo.permission == null || (opCode = AppOpsManager.permissionToOpCode(activityInfo.permission)) == -1 || atms.getAppOpsService().checkOperation(opCode, callingUid, callingPackage) == 0 || ignoreTargetSecurity) {
            return 0;
        }
        return 2;
    }

    static {
        ACTION_TO_RUNTIME_PERMISSION.put("android.media.action.IMAGE_CAPTURE", "android.permission.CAMERA");
        ACTION_TO_RUNTIME_PERMISSION.put("android.media.action.VIDEO_CAPTURE", "android.permission.CAMERA");
        ACTION_TO_RUNTIME_PERMISSION.put("android.intent.action.CALL", "android.permission.CALL_PHONE");
    }

    private static int getActionRestrictionForCallingPackage(ActivityTaskManagerService atms, String action, String callingPackage, int callingPid, int callingUid) {
        String permission;
        if (action == null || (permission = ACTION_TO_RUNTIME_PERMISSION.get(action)) == null) {
            return 0;
        }
        try {
            if (!ArrayUtils.contains(atms.mContext.getPackageManager().getPackageInfo(callingPackage, 4096).requestedPermissions, permission)) {
                return 0;
            }
            if (ActivityTaskManagerService.checkPermission(permission, callingPid, callingUid) == -1) {
                return 1;
            }
            int opCode = AppOpsManager.permissionToOpCode(permission);
            if (opCode == -1 || atms.getAppOpsService().checkOperation(opCode, callingUid, callingPackage) == 0) {
                return 0;
            }
            return 2;
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }

    static boolean isAllowedStartActivity(ActivityTaskManagerService atms, ActivityStackSupervisor supervisor, Intent intent, String callingPackage, int callingUid, ActivityInfo aInfo) {
        ActivityTaskManagerService activityTaskManagerService = atms;
        Intent intent2 = intent;
        String str = callingPackage;
        int i = callingUid;
        ActivityInfo activityInfo = aInfo;
        if (UserHandle.getAppId(callingUid) == 1000) {
            ActivityStackSupervisor activityStackSupervisor = supervisor;
        } else if ((intent.getMiuiFlags() & 2) != 0) {
            ActivityStackSupervisor activityStackSupervisor2 = supervisor;
        } else if (PendingIntentRecordInjector.containsPendingIntent(callingPackage)) {
            ActivityStackSupervisor activityStackSupervisor3 = supervisor;
        } else if (PendingIntentRecordInjector.containsPendingIntent(activityInfo.applicationInfo.packageName)) {
            ActivityStackSupervisor activityStackSupervisor4 = supervisor;
        } else if (i == mLastStartActivityUid) {
            ActivityStackSupervisor activityStackSupervisor5 = supervisor;
        } else {
            ActivityStack stack = supervisor.mRootActivityContainer.getDefaultDisplay().getFocusedStack();
            if (stack == null) {
                return true;
            }
            if (!activityTaskManagerService.mWindowManager.isKeyguardLocked() || atms.getAppOpsService().noteOperation(10020, i, str) == 0) {
                ActivityRecord r = stack.topRunningActivityLocked();
                if (r == null) {
                    return true;
                }
                if (i == r.info.applicationInfo.uid) {
                    mLastStartActivityUid = activityInfo.applicationInfo.uid;
                    return true;
                } else if (atms.getAppOpsService().checkOperation(10021, i, str) == 0) {
                    return true;
                } else {
                    SparseArray<WindowProcessController> pidMap = activityTaskManagerService.mProcessMap.getPidMap();
                    int i2 = pidMap.size() - 1;
                    while (i2 >= 0) {
                        int pid = pidMap.keyAt(i2);
                        WindowProcessController app = pidMap.get(pid);
                        if (app != null && app.mUid == i && (app.hasForegroundActivities() || (ExtraActivityManagerService.isProcessRecordVisible(pid, i) && app.hasActivities() && i == r.launchedFromUid))) {
                            mLastStartActivityUid = activityInfo.applicationInfo.uid;
                            return true;
                        }
                        i2--;
                        ActivityTaskManagerService activityTaskManagerService2 = atms;
                    }
                    atms.getAppOpsService().noteOperation(10021, i, str);
                    Slog.d(TAG, "MIUILOG- Permission Denied Activity : " + intent2 + " pkg : " + str + " uid : " + i + " tuid : " + r.info.applicationInfo.uid);
                    return false;
                }
            } else {
                Slog.d(TAG, "MIUILOG- Permission Denied Activity KeyguardLocked: " + intent2 + " pkg : " + str + " uid : " + i);
                return false;
            }
        }
        mLastStartActivityUid = activityInfo.applicationInfo.uid;
        return true;
    }

    public static boolean isAllowedBackgroundStart(ActivityTaskManagerService atms, Intent intent, String callingPackage, int callingUid) {
        return false;
    }

    public static void updateLastStartActivityUid(String foregroundPackageName, int lastUid) {
        if (foregroundPackageName != null) {
            if (mDefaultHomePkgNames == null) {
                ArrayList<ResolveInfo> homeActivities = new ArrayList<>();
                try {
                    ComponentName homeActivities2 = AppGlobals.getPackageManager().getHomeActivities(homeActivities);
                    if (homeActivities.size() > 0) {
                        Iterator<ResolveInfo> it = homeActivities.iterator();
                        while (it.hasNext()) {
                            ResolveInfo info = it.next();
                            if (mDefaultHomePkgNames == null) {
                                mDefaultHomePkgNames = new ArrayList();
                            }
                            mDefaultHomePkgNames.add(info.activityInfo.packageName);
                        }
                    }
                } catch (Exception e) {
                }
            }
            List<String> list = mDefaultHomePkgNames;
            if (list != null && list.contains(foregroundPackageName)) {
                mLastStartActivityUid = lastUid;
            }
        }
    }

    public static boolean checkRunningCompatibility(ActivityTaskManagerService atms, IApplicationThread caller, ActivityInfo info, Intent intent, int userId, String callingPackage) {
        return ExtraActivityManagerService.checkRunningCompatibility(atms.mContext, (ActivityManagerService) null, caller, info, intent, userId, callingPackage);
    }

    public static void onFreeFormToFullScreen(final ActivityRecord r) {
        if (r != null && r.app != null) {
            final ActivityStack.ActivityState state = r.getState();
            final int pid = r.app.getPid();
            MiuiFgThread.getHandler().post(new Runnable() {
                public void run() {
                    ActivityTaskManagerServiceInjector.onForegroundActivityChanged(ActivityRecord.this, state, pid, (ApplicationInfo) null);
                }
            });
        }
    }

    public static void onForegroundActivityChangedLocked(final ActivityRecord r) {
        if (r.app != null && r.getActivityStack() != null) {
            if (r.getActivityStack().getWindowingMode() == 5) {
                Slog.i(TAG, "do not report freeform event");
                return;
            }
            final ActivityStack.ActivityState state = r.getState();
            final int pid = r.app.getPid();
            final ApplicationInfo multiWindowAppInfo = ((ProcessManagerInternal) LocalServices.getService(ProcessManagerInternal.class)).getMultiWindowForegroundAppInfoLocked();
            MiuiFgThread.getHandler().post(new Runnable() {
                public void run() {
                    ActivityTaskManagerServiceInjector.onForegroundActivityChanged(ActivityRecord.this, state, pid, multiWindowAppInfo);
                }
            });
        }
    }

    static void onForegroundActivityChanged(ActivityRecord record, ActivityStack.ActivityState state, int pid, ApplicationInfo multiWindowAppInfo) {
        if (record == null || record.app == null || TextUtils.isEmpty(record.packageName)) {
            Slog.w(TAG, "next or next process is null, skip report!");
            return;
        }
        if (!TextUtils.equals(record.packageName, lastForegroundPkg) || lastMultiWindowAppInfo != multiWindowAppInfo) {
            ((ProcessManagerInternal) LocalServices.getService(ProcessManagerInternal.class)).notifyForegroundInfoChanged(record, state, pid, multiWindowAppInfo);
            reportPackageForeground(record, pid, lastForegroundPkg);
            lastForegroundPkg = record.packageName;
            lastMultiWindowAppInfo = multiWindowAppInfo;
        }
        ((ProcessManagerInternal) LocalServices.getService(ProcessManagerInternal.class)).notifyActivityChanged(record.mActivityComponent);
    }

    private static void reportPackageForeground(ActivityRecord record, int pid, String lastPkgName) {
        PackageForegroundEvent event = new PackageForegroundEvent();
        event.setPackageName(record.packageName);
        event.setComponentName(record.shortComponentName);
        event.setIdentity(System.identityHashCode(record));
        event.setPid(pid);
        event.setForegroundTime(SystemClock.uptimeMillis());
        event.setColdStart(record.isColdStart);
        event.setLastPackageName(lastPkgName);
        sCachedForegroundPackageList.add(event);
        if (sCachedForegroundPackageList.size() >= PACKAGE_FORE_BUFFER_SIZE && isSystemBootCompleted()) {
            Slog.d(TAG, "Begin to report package foreground events...");
            List<PackageForegroundEvent> events = new ArrayList<>();
            events.addAll(sCachedForegroundPackageList);
            sCachedForegroundPackageList.clear();
            reportPackageForegroundEvents(events);
        }
    }

    private static void reportPackageForegroundEvents(List<PackageForegroundEvent> events) {
        final ParceledListSlice<PackageForegroundEvent> reportEvents = new ParceledListSlice<>(events);
        BackgroundThread.getHandler().post(new Runnable() {
            public void run() {
                MQSEventManagerDelegate.getInstance().reportPackageForegroundEvents(reportEvents);
            }
        });
    }

    private static boolean isSystemBootCompleted() {
        if (!sSystemBootCompleted) {
            sSystemBootCompleted = SplitScreenReporter.ACTION_ENTER_SPLIT.equals(SystemProperties.get("sys.boot_completed"));
        }
        return sSystemBootCompleted;
    }

    public static void setPackageHoldOn(ActivityTaskManagerService atms, String packageName) {
        ActivityStack stack;
        if (!TextUtils.isEmpty(packageName)) {
            for (ActivityManager.StackInfo info : atms.getAllStackInfos()) {
                if (info.topActivity.getPackageName().equals(packageName) && (stack = atms.mRootActivityContainer.getStack(info.stackId)) != null && stack.getTopActivity() != null) {
                    sPackageHoldOn = packageName;
                    ((MiuiWindowManagerInternal) LocalServices.getService(WindowManagerInternal.class)).setHoldOn(stack.getTopActivity().appToken, true);
                    ((PowerManager) atms.mContext.getSystemService("power")).goToSleep(SystemClock.uptimeMillis());
                    Slog.i(TAG, "Go to sleep and hold on - " + sPackageHoldOn);
                    return;
                }
            }
            return;
        }
        sPackageHoldOn = null;
    }

    public static MiuiActivityController getMiuiActivityController() {
        return MiuiActivityController.getInstance();
    }

    public static class MiuiActivityController {
        /* access modifiers changed from: private */
        public static final boolean DEBUG_MESSAGES = SystemProperties.getBoolean("debug.miui.activity.log", false);
        private static final String PREFIX_TAG = "MiuiLog-ActivityObserver:";
        private static final String TAG = "MiuiActivityController";
        private static MiuiActivityController sInstance = new MiuiActivityController();
        /* access modifiers changed from: private */
        public final RemoteCallbackList<IMiuiActivityObserver> mActivityObservers = new RemoteCallbackList<>();
        private final H mH;
        /* access modifiers changed from: private */
        public final Intent mSendIntent = new Intent();

        final class H extends Handler {
            static final int ACTIVITY_DESTROYED = 5;
            static final int ACTIVITY_IDLE = 1;
            static final int ACTIVITY_PAUSED = 3;
            static final int ACTIVITY_RESUMED = 2;
            static final int ACTIVITY_STOPPED = 4;

            public H(Looper looper) {
                super(looper, (Handler.Callback) null, true);
            }

            /* access modifiers changed from: package-private */
            public String codeToString(int code) {
                if (MiuiActivityController.DEBUG_MESSAGES) {
                    if (code == 1) {
                        return "ACTIVITY_IDLE";
                    }
                    if (code == 2) {
                        return "ACTIVITY_RESUMED";
                    }
                    if (code == 3) {
                        return "ACTIVITY_PAUSED";
                    }
                    if (code == 4) {
                        return "ACTIVITY_STOPPED";
                    }
                    if (code == 5) {
                        return "ACTIVITY_DESTROYED";
                    }
                }
                return Integer.toString(code);
            }

            public void handleMessage(Message msg) {
                int what = msg.what;
                if ((what == 1 || what == 2 || what == 3 || what == 4 || what == 5) && msg.obj != null && (msg.obj instanceof ActivityRecord)) {
                    synchronized (MiuiActivityController.this) {
                        int i = MiuiActivityController.this.mActivityObservers.beginBroadcast();
                        while (i > 0) {
                            i--;
                            IMiuiActivityObserver observer = MiuiActivityController.this.mActivityObservers.getBroadcastItem(i);
                            if (observer != null) {
                                try {
                                    ActivityRecord record = (ActivityRecord) msg.obj;
                                    Object cookie = MiuiActivityController.this.mActivityObservers.getBroadcastCookie(i);
                                    if (cookie == null || !(cookie instanceof Intent)) {
                                        dispatchEvent(what, observer, record);
                                    } else if (canDispatchNow(record, (Intent) cookie)) {
                                        dispatchEvent(what, observer, record);
                                    } else {
                                        MiuiActivityController.logMessage(MiuiActivityController.PREFIX_TAG, " No need to dispatch the event, ignore it!");
                                    }
                                } catch (RemoteException e) {
                                    Slog.e(MiuiActivityController.TAG, "MiuiLog-ActivityObserver: There was something wrong : " + e.getMessage());
                                } catch (Exception e2) {
                                    Slog.e(MiuiActivityController.TAG, "MiuiLog-ActivityObserver: There was something wrong : " + e2.getMessage());
                                }
                            }
                        }
                        MiuiActivityController.this.mActivityObservers.finishBroadcast();
                    }
                }
            }

            private boolean canDispatchNow(ActivityRecord record, Intent intent) {
                if (record == null || intent == null) {
                    MiuiActivityController.logMessage(MiuiActivityController.PREFIX_TAG, "Record or intent is null");
                    return false;
                }
                ArrayList<String> packages = intent.getStringArrayListExtra("packages");
                ArrayList<ComponentName> activities = intent.getParcelableArrayListExtra(ActivityTaskManagerService.DUMP_ACTIVITIES_CMD);
                boolean needFilterPackage = packages != null && !packages.isEmpty();
                boolean needFilterActivity = activities != null && !activities.isEmpty();
                if (!needFilterPackage && !needFilterActivity) {
                    return true;
                }
                if (!needFilterPackage) {
                    MiuiActivityController.logMessage(MiuiActivityController.PREFIX_TAG, "Don't need to check package");
                } else if (packages.contains(record.packageName)) {
                    return true;
                } else {
                    MiuiActivityController.logMessage(MiuiActivityController.PREFIX_TAG, "The package " + record.packageName + " is not matched");
                }
                if (needFilterActivity) {
                    ComponentName realActivity = record.mActivityComponent;
                    if (realActivity != null) {
                        Iterator<ComponentName> it = activities.iterator();
                        while (it.hasNext()) {
                            if (realActivity.equals(it.next())) {
                                return true;
                            }
                        }
                        MiuiActivityController.logMessage(MiuiActivityController.PREFIX_TAG, "The activity " + realActivity + " is not matched");
                    } else {
                        MiuiActivityController.logMessage(MiuiActivityController.PREFIX_TAG, "The realActivity is null");
                    }
                } else {
                    MiuiActivityController.logMessage(MiuiActivityController.PREFIX_TAG, "Don't need to check activity");
                }
                return false;
            }

            private void dispatchEvent(int event, IMiuiActivityObserver observer, ActivityRecord record) throws RemoteException {
                Intent intent = MiuiActivityController.this.mSendIntent;
                intent.setComponent(record.mActivityComponent);
                if (event == 1) {
                    observer.activityIdle(intent);
                } else if (event == 2) {
                    intent.putExtra("appBounds", record.getConfiguration().windowConfiguration.getAppBounds());
                    observer.activityResumed(intent);
                } else if (event == 3) {
                    observer.activityPaused(intent);
                } else if (event == 4) {
                    observer.activityStopped(intent);
                } else if (event == 5) {
                    observer.activityDestroyed(intent);
                }
            }
        }

        public static MiuiActivityController getInstance() {
            return sInstance;
        }

        private MiuiActivityController() {
            HandlerThread handlerThread = new HandlerThread(TAG, -2);
            handlerThread.start();
            this.mH = new H(handlerThread.getLooper());
        }

        public void registerActivityObserver(IMiuiActivityObserver observer, Intent intent) {
            this.mActivityObservers.register(observer, intent);
        }

        public void unregisterActivityObserver(IMiuiActivityObserver observer) {
            this.mActivityObservers.unregister(observer);
        }

        public void activityIdle(ActivityRecord record) {
            sendMessage(1, record);
        }

        public void activityResumed(ActivityRecord record) {
            sendMessage(2, record);
        }

        public void activityPaused(ActivityRecord record) {
            sendMessage(3, record);
        }

        public void activityStopped(ActivityRecord record) {
            sendMessage(4, record);
        }

        public void activityDestroyed(ActivityRecord record) {
            sendMessage(5, record);
        }

        private void sendMessage(int what, Object obj) {
            sendMessage(what, obj, 0, 0, false);
        }

        private void sendMessage(int what, Object obj, int arg1) {
            sendMessage(what, obj, arg1, 0, false);
        }

        private void sendMessage(int what, Object obj, int arg1, int arg2) {
            sendMessage(what, obj, arg1, arg2, false);
        }

        private void sendMessage(int what, Object obj, int arg1, int arg2, boolean async) {
            int size = this.mActivityObservers.getRegisteredCallbackCount();
            logMessage(PREFIX_TAG, "SendMessage " + what + " " + this.mH.codeToString(what) + ": " + arg1 + " / " + obj + " observer size: " + size);
            if (size > 0) {
                Message msg = Message.obtain();
                msg.what = what;
                msg.obj = obj;
                msg.arg1 = arg1;
                msg.arg2 = arg2;
                if (async) {
                    msg.setAsynchronous(true);
                }
                this.mH.sendMessage(msg);
            }
        }

        public static void logMessage(String prefix, String msg) {
            if (DEBUG_MESSAGES) {
                Slog.d(TAG, prefix + msg);
            }
        }
    }

    static void reportActivityLaunchTime(Object... list) {
        EventLog.writeEvent(EventLogTags.AM_ACTIVITY_LAUNCH_INFO, list);
    }

    static void handleExtraConfigurationChangesForSystem(int changes, Configuration newConfig) {
        ExtraActivityManagerService.handleExtraConfigurationChangesForSystem(changes, newConfig);
    }

    public static boolean needSetWindowMode(ActivityTaskManagerService service, TaskRecord task, boolean toTop, int windowingMode) {
        ActivityStack stack = task.getStack();
        if (stack.getWindowingMode() == 5 && windowingMode == 1) {
            if (!toTop) {
                service.mStackSupervisor.removeTaskByIdLocked(task.taskId, false, false, "setTaskWindowingMode");
                return true;
            } else if (stack.getTaskStack() != null) {
                setMiuiConfigFlag(stack.getTaskStack(), 1, true);
            }
        }
        return false;
    }

    private static boolean setMiuiConfigFlag(TaskStack object, int miuiConfigFlag, boolean isSetToStack) {
        Class clazz = TaskStack.class;
        try {
            Method method = clazz.getDeclaredMethod("setMiuiConfigFlag", new Class[]{Integer.TYPE, Boolean.TYPE});
            method.setAccessible(true);
            method.invoke(object, new Object[]{Integer.valueOf(miuiConfigFlag), Boolean.valueOf(isSetToStack)});
            return true;
        } catch (Exception e) {
            Log.d(TAG, "setMiuiConfigFlag:" + e.toString());
            return false;
        }
    }

    public static int handleFreeformModeRequst(IBinder token, int cmd, Context mContext) {
        int result = -1;
        long ident = Binder.clearCallingIdentity();
        try {
            ActivityRecord r = ActivityRecord.forTokenLocked(token);
            int i = 0;
            if (cmd == 0) {
                if (r != null) {
                    i = r.getWindowingMode();
                }
                result = i;
            } else if (cmd == 1) {
                Settings.Secure.putString(mContext.getContentResolver(), "gamebox_stick", r.getTaskRecord().getBaseIntent().getComponent().flattenToShortString());
            } else if (cmd == 2) {
                Settings.Secure.putString(mContext.getContentResolver(), "gamebox_stick", "");
            } else if (cmd == 3) {
                String component = Settings.Secure.getString(mContext.getContentResolver(), "gamebox_stick");
                if (r != null && r.getTaskRecord().getBaseIntent().getComponent().flattenToShortString().equals(component)) {
                    i = 1;
                }
                result = i;
            }
            return result;
        } finally {
            Binder.restoreCallingIdentity(ident);
        }
    }

    private static IPerfShielder getPerfService() {
        if (!sPerfServiceObtained) {
            sPerfService = IPerfShielder.Stub.asInterface(ServiceManager.getService(PerfShielderService.SERVICE_NAME));
            sPerfServiceObtained = true;
        }
        return sPerfService;
    }

    static void setSchedFgPid(int pid) {
        IPerfShielder perfService = getPerfService();
        if (perfService != null) {
            try {
                perfService.setSchedFgPid(pid);
            } catch (RemoteException e) {
            }
        }
    }
}
