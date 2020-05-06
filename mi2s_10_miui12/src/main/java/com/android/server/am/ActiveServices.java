package com.android.server.am;

import android.app.ActivityManager;
import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.app.IApplicationThread;
import android.app.IServiceConnection;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ServiceStartArgs;
import android.content.ComponentName;
import android.content.IIntentSender;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ApplicationInfo;
import android.content.pm.ParceledListSlice;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.TransactionTooLargeException;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.BoostFramework;
import android.util.PrintWriterPrinter;
import android.util.Slog;
import android.util.SparseArray;
import android.util.StatsLog;
import android.util.TimeUtils;
import android.util.proto.ProtoOutputStream;
import android.webkit.WebViewZygote;
import com.android.internal.app.procstats.ServiceState;
import com.android.internal.os.TransferPipe;
import com.android.internal.util.DumpUtils;
import com.android.server.AppStateTracker;
import com.android.server.LocalServices;
import com.android.server.am.ActivityManagerService;
import com.android.server.am.ServiceRecord;
import com.android.server.job.controllers.JobStatus;
import com.android.server.pm.DumpState;
import com.android.server.slice.SliceClientPermissions;
import com.android.server.uri.NeededUriGrants;
import com.android.server.wm.ActivityRecord;
import com.android.server.wm.ActivityServiceConnectionsHolder;
import com.miui.server.AccessController;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Predicate;
import vendor.qti.hardware.servicetracker.V1_0.ClientData;
import vendor.qti.hardware.servicetracker.V1_0.IServicetracker;
import vendor.qti.hardware.servicetracker.V1_0.ServiceData;

public final class ActiveServices {
    private static final boolean DEBUG_DELAYED_SERVICE = false;
    private static final boolean DEBUG_DELAYED_STARTS = false;
    static final int LAST_ANR_LIFETIME_DURATION_MSECS = 7200000;
    private static final boolean LOG_SERVICE_START_STOP = false;
    static final int SERVICE_BACKGROUND_TIMEOUT = 200000;
    private static boolean SERVICE_RESCHEDULE = false;
    static final int SERVICE_START_FOREGROUND_TIMEOUT = 10000;
    static final int SERVICE_TIMEOUT = 20000;
    private static final boolean SHOW_DUNGEON_NOTIFICATION = false;
    private static final String TAG = "ActivityManager";
    private static final String TAG_MU = "ActivityManager_MU";
    private static final String TAG_SERVICE = "ActivityManager";
    private static final String TAG_SERVICE_EXECUTING = "ActivityManager";
    public static BoostFramework mPerf = new BoostFramework();
    final ActivityManagerService mAm;
    final ArrayList<ServiceRecord> mDestroyingServices = new ArrayList<>();
    String mLastAnrDump;
    final Runnable mLastAnrDumpClearer;
    final int mMaxStartingBackground;
    final ArrayList<ServiceRecord> mPendingServices = new ArrayList<>();
    final ArrayList<ServiceRecord> mRestartingServices = new ArrayList<>();
    boolean mScreenOn;
    final ArrayMap<IBinder, ArrayList<ConnectionRecord>> mServiceConnections = new ArrayMap<>();
    final SparseArray<ServiceMap> mServiceMap = new SparseArray<>();
    private IServicetracker mServicetracker;
    private ArrayList<ServiceRecord> mTmpCollectionResults = null;

    class ForcedStandbyListener extends AppStateTracker.Listener {
        ForcedStandbyListener() {
        }

        public void stopForegroundServicesForUidPackage(int uid, String packageName) {
            synchronized (ActiveServices.this.mAm) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    ServiceMap smap = ActiveServices.this.getServiceMapLocked(UserHandle.getUserId(uid));
                    int N = smap.mServicesByInstanceName.size();
                    ArrayList<ServiceRecord> toStop = new ArrayList<>(N);
                    for (int i = 0; i < N; i++) {
                        ServiceRecord r = smap.mServicesByInstanceName.valueAt(i);
                        if ((uid == r.serviceInfo.applicationInfo.uid || packageName.equals(r.serviceInfo.packageName)) && r.isForeground) {
                            toStop.add(r);
                        }
                    }
                    int numToStop = toStop.size();
                    for (int i2 = 0; i2 < numToStop; i2++) {
                        ActiveServices.this.setServiceForegroundInnerLocked(toStop.get(i2), 0, (Notification) null, 0, 0);
                    }
                } catch (Throwable th) {
                    while (true) {
                        ActivityManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
        }
    }

    static final class ActiveForegroundApp {
        boolean mAppOnTop;
        long mEndTime;
        long mHideTime;
        CharSequence mLabel;
        int mNumActive;
        String mPackageName;
        boolean mShownWhileScreenOn;
        boolean mShownWhileTop;
        long mStartTime;
        long mStartVisibleTime;
        int mUid;

        ActiveForegroundApp() {
        }
    }

    final class ServiceMap extends Handler {
        static final int MSG_BG_START_TIMEOUT = 1;
        static final int MSG_UPDATE_FOREGROUND_APPS = 2;
        final ArrayMap<String, ActiveForegroundApp> mActiveForegroundApps = new ArrayMap<>();
        boolean mActiveForegroundAppsChanged;
        final ArrayList<ServiceRecord> mDelayedStartList = new ArrayList<>();
        final ArrayMap<ComponentName, ServiceRecord> mServicesByInstanceName = new ArrayMap<>();
        final ArrayMap<Intent.FilterComparison, ServiceRecord> mServicesByIntent = new ArrayMap<>();
        final ArrayList<ServiceRecord> mStartingBackground = new ArrayList<>();
        final int mUserId;

        ServiceMap(Looper looper, int userId) {
            super(looper);
            this.mUserId = userId;
        }

        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i == 1) {
                synchronized (ActiveServices.this.mAm) {
                    try {
                        ActivityManagerService.boostPriorityForLockedSection();
                        rescheduleDelayedStartsLocked();
                    } catch (Throwable th) {
                        while (true) {
                            ActivityManagerService.resetPriorityAfterLockedSection();
                            throw th;
                        }
                    }
                }
                ActivityManagerService.resetPriorityAfterLockedSection();
            } else if (i == 2) {
                ActiveServices.this.updateForegroundApps(this);
            }
        }

        /* access modifiers changed from: package-private */
        public void ensureNotStartingBackgroundLocked(ServiceRecord r) {
            if (this.mStartingBackground.remove(r)) {
                rescheduleDelayedStartsLocked();
            }
            this.mDelayedStartList.remove(r);
        }

        /* access modifiers changed from: package-private */
        public void rescheduleDelayedStartsLocked() {
            removeMessages(1);
            long now = SystemClock.uptimeMillis();
            int i = 0;
            int N = this.mStartingBackground.size();
            while (i < N) {
                ServiceRecord r = this.mStartingBackground.get(i);
                if (r.startingBgTimeout <= now) {
                    Slog.i("ActivityManager", "Waited long enough for: " + r);
                    this.mStartingBackground.remove(i);
                    N += -1;
                    i += -1;
                }
                i++;
            }
            while (this.mDelayedStartList.size() > 0 && this.mStartingBackground.size() < ActiveServices.this.mMaxStartingBackground) {
                ServiceRecord r2 = this.mDelayedStartList.remove(0);
                r2.delayed = false;
                if (r2.pendingStarts.size() <= 0) {
                    Slog.wtf("ActivityManager", "**** NO PENDING STARTS! " + r2 + " startReq=" + r2.startRequested + " delayedStop=" + r2.delayedStop);
                } else {
                    try {
                        ActiveServices.this.startServiceInnerLocked(this, r2.pendingStarts.get(0).intent, r2, false, true);
                    } catch (TransactionTooLargeException e) {
                    }
                }
            }
            if (this.mStartingBackground.size() > 0) {
                ServiceRecord next = this.mStartingBackground.get(0);
                sendMessageAtTime(obtainMessage(1), next.startingBgTimeout > now ? next.startingBgTimeout : now);
            }
            if (this.mStartingBackground.size() < ActiveServices.this.mMaxStartingBackground) {
                ActiveServices.this.mAm.backgroundServicesFinishedLocked(this.mUserId);
            }
        }
    }

    public ActiveServices(ActivityManagerService service) {
        int i = 1;
        this.mScreenOn = true;
        this.mLastAnrDumpClearer = new Runnable() {
            public void run() {
                synchronized (ActiveServices.this.mAm) {
                    try {
                        ActivityManagerService.boostPriorityForLockedSection();
                        ActiveServices.this.mLastAnrDump = null;
                    } catch (Throwable th) {
                        while (true) {
                            ActivityManagerService.resetPriorityAfterLockedSection();
                            throw th;
                        }
                    }
                }
                ActivityManagerService.resetPriorityAfterLockedSection();
            }
        };
        this.mAm = service;
        int maxBg = 0;
        try {
            maxBg = Integer.parseInt(SystemProperties.get("ro.config.max_starting_bg", "0"));
        } catch (RuntimeException e) {
        }
        if (maxBg > 0) {
            i = maxBg;
        } else if (!ActivityManager.isLowRamDeviceStatic()) {
            i = 8;
        }
        this.mMaxStartingBackground = i;
        BoostFramework boostFramework = mPerf;
        if (boostFramework != null) {
            SERVICE_RESCHEDULE = Boolean.parseBoolean(boostFramework.perfGetProp("ro.vendor.qti.am.reschedule_service", "false"));
        }
    }

    /* access modifiers changed from: package-private */
    public void systemServicesReady() {
        ((AppStateTracker) LocalServices.getService(AppStateTracker.class)).addListener(new ForcedStandbyListener());
    }

    private boolean getServicetrackerInstance() {
        if (this.mServicetracker != null) {
            return true;
        }
        try {
            this.mServicetracker = IServicetracker.getService(false);
        } catch (NoSuchElementException e) {
        } catch (RemoteException e2) {
            return false;
        }
        if (this.mServicetracker == null) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public ServiceRecord getServiceByNameLocked(ComponentName name, int callingUser) {
        return getServiceMapLocked(callingUser).mServicesByInstanceName.get(name);
    }

    /* access modifiers changed from: package-private */
    public boolean hasBackgroundServicesLocked(int callingUser) {
        ServiceMap smap = this.mServiceMap.get(callingUser);
        return smap != null && smap.mStartingBackground.size() >= this.mMaxStartingBackground;
    }

    /* access modifiers changed from: private */
    public ServiceMap getServiceMapLocked(int callingUser) {
        ServiceMap smap = this.mServiceMap.get(callingUser);
        if (smap != null) {
            return smap;
        }
        ServiceMap smap2 = new ServiceMap(this.mAm.mHandler.getLooper(), callingUser);
        this.mServiceMap.put(callingUser, smap2);
        return smap2;
    }

    /* access modifiers changed from: package-private */
    public ArrayMap<ComponentName, ServiceRecord> getServicesLocked(int callingUser) {
        return getServiceMapLocked(callingUser).mServicesByInstanceName;
    }

    private boolean appRestrictedAnyInBackground(int uid, String packageName) {
        return this.mAm.mAppOpsService.checkOperation(70, uid, packageName) != 0;
    }

    /* access modifiers changed from: package-private */
    public ComponentName startServiceLocked(IApplicationThread caller, Intent service, String resolvedType, int callingPid, int callingUid, boolean fgRequired, String callingPackage, int userId) throws TransactionTooLargeException {
        return startServiceLocked(caller, service, resolvedType, callingPid, callingUid, fgRequired, callingPackage, userId, false);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x0309  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0186  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x01fd  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.content.ComponentName startServiceLocked(android.app.IApplicationThread r31, android.content.Intent r32, java.lang.String r33, int r34, int r35, boolean r36, java.lang.String r37, int r38, boolean r39) throws android.os.TransactionTooLargeException {
        /*
            r30 = this;
            r12 = r30
            r13 = r31
            r14 = r32
            r15 = r34
            r11 = r35
            r10 = r37
            r9 = 0
            r8 = 1
            if (r13 == 0) goto L_0x004a
            com.android.server.am.ActivityManagerService r0 = r12.mAm
            com.android.server.am.ProcessRecord r0 = r0.getRecordForAppLocked(r13)
            if (r0 == 0) goto L_0x0023
            int r1 = r0.setSchedGroup
            if (r1 == 0) goto L_0x001e
            r1 = r8
            goto L_0x001f
        L_0x001e:
            r1 = r9
        L_0x001f:
            r0 = r1
            r16 = r0
            goto L_0x004d
        L_0x0023:
            java.lang.SecurityException r1 = new java.lang.SecurityException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Unable to find app for caller "
            r2.append(r3)
            r2.append(r13)
            java.lang.String r3 = " (pid="
            r2.append(r3)
            r2.append(r15)
            java.lang.String r3 = ") when starting service "
            r2.append(r3)
            r2.append(r14)
            java.lang.String r2 = r2.toString()
            r1.<init>(r2)
            throw r1
        L_0x004a:
            r0 = 1
            r16 = r0
        L_0x004d:
            r2 = 0
            r17 = 1
            r18 = 0
            r19 = 0
            r0 = r30
            r1 = r32
            r3 = r33
            r4 = r37
            r5 = r34
            r6 = r35
            r7 = r38
            r8 = r17
            r9 = r16
            r10 = r18
            r13 = r11
            r11 = r19
            com.android.server.am.ActiveServices$ServiceLookupResult r9 = r0.retrieveServiceLocked(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            r10 = 0
            if (r9 != 0) goto L_0x0073
            return r10
        L_0x0073:
            com.android.server.am.ServiceRecord r0 = r9.record
            if (r0 != 0) goto L_0x0089
            android.content.ComponentName r0 = new android.content.ComponentName
            java.lang.String r1 = r9.permission
            if (r1 == 0) goto L_0x0080
            java.lang.String r1 = r9.permission
            goto L_0x0083
        L_0x0080:
            java.lang.String r1 = "private to package"
        L_0x0083:
            java.lang.String r2 = "!"
            r0.<init>(r2, r1)
            return r0
        L_0x0089:
            com.android.server.am.ServiceRecord r11 = r9.record
            com.android.server.am.ActivityManagerService r0 = r12.mAm
            com.android.server.am.UserController r0 = r0.mUserController
            int r1 = r11.userId
            boolean r0 = r0.exists(r1)
            java.lang.String r8 = "ActivityManager"
            if (r0 != 0) goto L_0x00b0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "Trying to start service with non-existent user! "
            r0.append(r1)
            int r1 = r11.userId
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            android.util.Slog.w(r8, r0)
            return r10
        L_0x00b0:
            com.android.server.am.ActivityManagerService r0 = r12.mAm
            android.content.pm.ApplicationInfo r1 = r11.appInfo
            int r1 = r1.uid
            boolean r0 = r0.isUidActiveLocked(r1)
            r7 = 1
            r0 = r0 ^ r7
            r17 = r0
            r0 = 0
            if (r17 == 0) goto L_0x00d1
            android.content.pm.ApplicationInfo r1 = r11.appInfo
            int r1 = r1.uid
            java.lang.String r2 = r11.packageName
            boolean r1 = r12.appRestrictedAnyInBackground(r1, r2)
            if (r1 == 0) goto L_0x00d1
            r0 = 1
            r18 = r0
            goto L_0x00d3
        L_0x00d1:
            r18 = r0
        L_0x00d3:
            r0 = 0
            java.lang.String r6 = " pkg="
            java.lang.String r5 = " uid="
            java.lang.String r4 = " from pid="
            java.lang.String r3 = " to "
            if (r36 == 0) goto L_0x013d
            com.android.server.am.ActivityManagerService r1 = r12.mAm
            com.android.server.appop.AppOpsService r1 = r1.mAppOpsService
            r2 = 76
            android.content.pm.ApplicationInfo r10 = r11.appInfo
            int r10 = r10.uid
            java.lang.String r7 = r11.packageName
            int r1 = r1.checkOperation(r2, r10, r7)
            if (r1 == 0) goto L_0x0139
            r7 = 1
            if (r1 == r7) goto L_0x0103
            r2 = 3
            if (r1 == r2) goto L_0x0100
            android.content.ComponentName r2 = new android.content.ComponentName
            java.lang.String r3 = "!!"
            java.lang.String r4 = "foreground not allowed as per app op"
            r2.<init>(r3, r4)
            return r2
        L_0x0100:
            r10 = r37
            goto L_0x013c
        L_0x0103:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r10 = "startForegroundService not allowed due to app op: service "
            r2.append(r10)
            r2.append(r14)
            r2.append(r3)
            java.lang.String r10 = r11.shortInstanceName
            r2.append(r10)
            r2.append(r4)
            r2.append(r15)
            r2.append(r5)
            r2.append(r13)
            r2.append(r6)
            r10 = r37
            r2.append(r10)
            java.lang.String r2 = r2.toString()
            android.util.Slog.w(r8, r2)
            r2 = 0
            r0 = 1
            r20 = r0
            goto L_0x0143
        L_0x0139:
            r10 = r37
            r7 = 1
        L_0x013c:
            goto L_0x013f
        L_0x013d:
            r10 = r37
        L_0x013f:
            r2 = r36
            r20 = r0
        L_0x0143:
            if (r18 != 0) goto L_0x0152
            boolean r0 = r11.startRequested
            if (r0 != 0) goto L_0x014c
            if (r2 != 0) goto L_0x014c
            goto L_0x0152
        L_0x014c:
            r24 = r9
            r9 = r8
            r8 = r12
            goto L_0x0203
        L_0x0152:
            com.android.server.am.ActivityManagerService r0 = r12.mAm
            android.content.pm.ApplicationInfo r1 = r11.appInfo
            int r1 = r1.uid
            java.lang.String r7 = r11.packageName
            r36 = r2
            android.content.pm.ApplicationInfo r2 = r11.appInfo
            int r2 = r2.targetSdkVersion
            r22 = 0
            r23 = 0
            r25 = r36
            r24 = r2
            r2 = r7
            r7 = r3
            r3 = r24
            r26 = r4
            r4 = r34
            r24 = r9
            r9 = r5
            r5 = r22
            r27 = r6
            r6 = r23
            r28 = r7
            r7 = r18
            r12 = r8
            r8 = r37
            int r0 = r0.getAppStartModeLocked(r1, r2, r3, r4, r5, r6, r7, r8)
            if (r0 == 0) goto L_0x01fd
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Background start not allowed: service "
            r1.append(r2)
            r1.append(r14)
            r2 = r28
            r1.append(r2)
            java.lang.String r2 = r11.shortInstanceName
            r1.append(r2)
            r2 = r26
            r1.append(r2)
            r1.append(r15)
            r1.append(r9)
            r1.append(r13)
            r2 = r27
            r1.append(r2)
            r1.append(r10)
            java.lang.String r2 = " startFg?="
            r1.append(r2)
            r2 = r25
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            android.util.Slog.w(r12, r1)
            r7 = 1
            if (r0 == r7) goto L_0x01f9
            if (r20 == 0) goto L_0x01cc
            r8 = r30
            goto L_0x01fb
        L_0x01cc:
            if (r18 == 0) goto L_0x01d2
            if (r2 == 0) goto L_0x01d2
            r1 = 0
            return r1
        L_0x01d2:
            r8 = r30
            com.android.server.am.ActivityManagerService r1 = r8.mAm
            com.android.server.am.ProcessList r1 = r1.mProcessList
            android.content.pm.ApplicationInfo r3 = r11.appInfo
            int r3 = r3.uid
            com.android.server.am.UidRecord r1 = r1.getUidRecordLocked(r3)
            android.content.ComponentName r3 = new android.content.ComponentName
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "app is in background uid "
            r4.append(r5)
            r4.append(r1)
            java.lang.String r4 = r4.toString()
            java.lang.String r5 = "?"
            r3.<init>(r5, r4)
            return r3
        L_0x01f9:
            r8 = r30
        L_0x01fb:
            r1 = 0
            return r1
        L_0x01fd:
            r8 = r30
            r9 = r12
            r2 = r25
            r7 = 1
        L_0x0203:
            android.content.pm.ApplicationInfo r0 = r11.appInfo
            int r0 = r0.targetSdkVersion
            r1 = 26
            if (r0 >= r1) goto L_0x0210
            if (r2 == 0) goto L_0x0210
            r2 = 0
            r12 = r2
            goto L_0x0211
        L_0x0210:
            r12 = r2
        L_0x0211:
            com.android.server.am.ActivityManagerService r0 = r8.mAm
            com.android.server.uri.UriGrantsManagerInternal r0 = r0.mUgmInternal
            java.lang.String r2 = r11.packageName
            int r4 = r32.getFlags()
            r5 = 0
            int r6 = r11.userId
            r1 = r35
            r3 = r32
            com.android.server.uri.NeededUriGrants r21 = r0.checkGrantUriPermissionFromIntent(r1, r2, r3, r4, r5, r6)
            r0 = r30
            r1 = r11
            r2 = r37
            r3 = r35
            r4 = r32
            r5 = r16
            r6 = r38
            boolean r0 = r0.requestStartTargetPermissionsReviewIfNeededLocked(r1, r2, r3, r4, r5, r6)
            if (r0 != 0) goto L_0x023b
            r0 = 0
            return r0
        L_0x023b:
            r6 = 0
            r8.unscheduleServiceRestartLocked(r11, r13, r6)
            long r0 = android.os.SystemClock.uptimeMillis()
            r11.lastActivity = r0
            r11.startRequested = r7
            r11.delayedStop = r6
            r11.fgRequired = r12
            java.util.ArrayList<com.android.server.am.ServiceRecord$StartItem> r5 = r11.pendingStarts
            com.android.server.am.ServiceRecord$StartItem r4 = new com.android.server.am.ServiceRecord$StartItem
            r2 = 0
            int r3 = r11.makeNextStartId()
            r0 = r4
            r1 = r11
            r7 = r4
            r4 = r32
            r29 = r5
            r5 = r21
            r10 = r6
            r6 = r35
            r0.<init>(r1, r2, r3, r4, r5, r6)
            r0 = r29
            r0.add(r7)
            if (r12 == 0) goto L_0x0296
            com.android.internal.app.procstats.ServiceState r0 = r11.getTracker()
            if (r0 == 0) goto L_0x027e
            com.android.server.am.ActivityManagerService r1 = r8.mAm
            com.android.server.am.ProcessStatsService r1 = r1.mProcessStats
            int r1 = r1.getMemFactorLocked()
            long r2 = r11.lastActivity
            r4 = 1
            r0.setForeground(r4, r1, r2)
        L_0x027e:
            com.android.server.am.ActivityManagerService r1 = r8.mAm
            com.android.server.appop.AppOpsService r2 = r1.mAppOpsService
            com.android.server.am.ActivityManagerService r1 = r8.mAm
            com.android.server.appop.AppOpsService r1 = r1.mAppOpsService
            android.os.IBinder r3 = android.app.AppOpsManager.getToken(r1)
            r4 = 76
            android.content.pm.ApplicationInfo r1 = r11.appInfo
            int r5 = r1.uid
            java.lang.String r6 = r11.packageName
            r7 = 1
            r2.startOperation(r3, r4, r5, r6, r7)
        L_0x0296:
            int r0 = r11.userId
            com.android.server.am.ActiveServices$ServiceMap r6 = r8.getServiceMapLocked(r0)
            r0 = 0
            if (r16 != 0) goto L_0x0306
            if (r12 != 0) goto L_0x0306
            com.android.server.am.ProcessRecord r1 = r11.app
            if (r1 != 0) goto L_0x0306
            com.android.server.am.ActivityManagerService r1 = r8.mAm
            com.android.server.am.UserController r1 = r1.mUserController
            int r2 = r11.userId
            boolean r1 = r1.hasStartedUserState(r2)
            if (r1 == 0) goto L_0x0306
            com.android.server.am.ActivityManagerService r1 = r8.mAm
            java.lang.String r2 = r11.processName
            android.content.pm.ApplicationInfo r3 = r11.appInfo
            int r3 = r3.uid
            com.android.server.am.ProcessRecord r1 = r1.getProcessRecordLocked(r2, r3, r10)
            if (r1 == 0) goto L_0x02d3
            int r2 = r1.getCurProcState()
            r3 = 12
            if (r2 <= r3) goto L_0x02c8
            goto L_0x02d3
        L_0x02c8:
            int r2 = r1.getCurProcState()
            r3 = 11
            if (r2 < r3) goto L_0x0306
            r0 = 1
            r7 = r0
            goto L_0x0307
        L_0x02d3:
            boolean r2 = r11.delayed
            if (r2 == 0) goto L_0x02da
            android.content.ComponentName r2 = r11.name
            return r2
        L_0x02da:
            java.util.ArrayList<com.android.server.am.ServiceRecord> r2 = r6.mStartingBackground
            int r2 = r2.size()
            int r3 = r8.mMaxStartingBackground
            if (r2 < r3) goto L_0x0303
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Delaying start of: "
            r2.append(r3)
            r2.append(r11)
            java.lang.String r2 = r2.toString()
            android.util.Slog.i(r9, r2)
            java.util.ArrayList<com.android.server.am.ServiceRecord> r2 = r6.mDelayedStartList
            r2.add(r11)
            r2 = 1
            r11.delayed = r2
            android.content.ComponentName r2 = r11.name
            return r2
        L_0x0303:
            r0 = 1
            r7 = r0
            goto L_0x0307
        L_0x0306:
            r7 = r0
        L_0x0307:
            if (r39 == 0) goto L_0x030c
            r11.whitelistBgActivityStartsOnServiceStart()
        L_0x030c:
            r0 = r30
            r1 = r6
            r2 = r32
            r3 = r11
            r4 = r16
            r5 = r7
            android.content.ComponentName r0 = r0.startServiceInnerLocked(r1, r2, r3, r4, r5)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ActiveServices.startServiceLocked(android.app.IApplicationThread, android.content.Intent, java.lang.String, int, int, boolean, java.lang.String, int, boolean):android.content.ComponentName");
    }

    private boolean requestStartTargetPermissionsReviewIfNeededLocked(ServiceRecord r, String callingPackage, int callingUid, Intent service, boolean callerFg, int userId) {
        ServiceRecord serviceRecord = r;
        Intent intent = service;
        if (!this.mAm.getPackageManagerInternalLocked().isPermissionsReviewRequired(serviceRecord.packageName, serviceRecord.userId)) {
            int i = userId;
            return true;
        } else if (!callerFg) {
            Slog.w("ActivityManager", "u" + serviceRecord.userId + " Starting a service in package" + serviceRecord.packageName + " requires a permissions review");
            return false;
        } else {
            IIntentSender target = this.mAm.mPendingIntentController.getIntentSender(4, callingPackage, callingUid, userId, (IBinder) null, (String) null, 0, new Intent[]{intent}, new String[]{intent.resolveType(this.mAm.mContext.getContentResolver())}, 1409286144, (Bundle) null);
            final Intent intent2 = new Intent("android.intent.action.REVIEW_PERMISSIONS");
            intent2.addFlags(411041792);
            intent2.putExtra("android.intent.extra.PACKAGE_NAME", serviceRecord.packageName);
            intent2.putExtra("android.intent.extra.INTENT", new IntentSender(target));
            final int i2 = userId;
            this.mAm.mHandler.post(new Runnable() {
                public void run() {
                    ActiveServices.this.mAm.mContext.startActivityAsUser(intent2, new UserHandle(i2));
                }
            });
            return false;
        }
    }

    /* access modifiers changed from: package-private */
    public ComponentName startServiceInnerLocked(ServiceMap smap, Intent service, ServiceRecord r, boolean callerFg, boolean addToStarting) throws TransactionTooLargeException {
        ServiceMap serviceMap = smap;
        ServiceRecord serviceRecord = r;
        ServiceState stracker = r.getTracker();
        if (stracker != null) {
            stracker.setStarted(true, this.mAm.mProcessStats.getMemFactorLocked(), serviceRecord.lastActivity);
        }
        boolean z = false;
        serviceRecord.callStart = false;
        StatsLog.write(99, serviceRecord.appInfo.uid, serviceRecord.name.getPackageName(), serviceRecord.name.getClassName(), 1);
        synchronized (serviceRecord.stats.getBatteryStats()) {
            serviceRecord.stats.startRunningLocked();
        }
        String error = bringUpServiceLocked(r, service.getFlags(), callerFg, false, false);
        if (error != null) {
            return new ComponentName("!!", error);
        }
        if (serviceRecord.startRequested && addToStarting) {
            if (serviceMap.mStartingBackground.size() == 0) {
                z = true;
            }
            boolean first = z;
            serviceMap.mStartingBackground.add(r);
            serviceRecord.startingBgTimeout = SystemClock.uptimeMillis() + this.mAm.mConstants.BG_START_TIMEOUT;
            if (first) {
                smap.rescheduleDelayedStartsLocked();
            }
        } else if (callerFg || serviceRecord.fgRequired) {
            smap.ensureNotStartingBackgroundLocked(r);
        }
        return serviceRecord.name;
    }

    private void stopServiceLocked(ServiceRecord service) {
        if (service.delayed) {
            service.delayedStop = true;
            return;
        }
        StatsLog.write(99, service.appInfo.uid, service.name.getPackageName(), service.name.getClassName(), 2);
        synchronized (service.stats.getBatteryStats()) {
            service.stats.stopRunningLocked();
        }
        service.startRequested = false;
        if (service.tracker != null) {
            service.tracker.setStarted(false, this.mAm.mProcessStats.getMemFactorLocked(), SystemClock.uptimeMillis());
        }
        service.callStart = false;
        if (this.mAm.mAppOpsService.noteOperation(63, service.appInfo.uid, service.packageName) != 0 && !service.hasOtherAppAutoCreateConnections()) {
            Slog.w("ActivityManager", "Background stop Service: " + service + " don't refer to it's conn.");
        }
        bringDownServiceIfNeededLocked(service, false, false);
    }

    /* JADX INFO: finally extract failed */
    /* access modifiers changed from: package-private */
    public int stopServiceLocked(IApplicationThread caller, Intent service, String resolvedType, int userId) {
        IApplicationThread iApplicationThread = caller;
        ProcessRecord callerApp = this.mAm.getRecordForAppLocked(iApplicationThread);
        if (iApplicationThread == null) {
            Intent intent = service;
        } else if (callerApp != null) {
            Intent intent2 = service;
        } else {
            throw new SecurityException("Unable to find app for caller " + iApplicationThread + " (pid=" + Binder.getCallingPid() + ") when stopping service " + service);
        }
        ServiceLookupResult r = retrieveServiceLocked(service, (String) null, resolvedType, (String) null, Binder.getCallingPid(), Binder.getCallingUid(), userId, false, false, false, false);
        if (r == null) {
            return 0;
        }
        if (r.record == null) {
            return -1;
        }
        long origId = Binder.clearCallingIdentity();
        try {
            stopServiceLocked(r.record);
            Binder.restoreCallingIdentity(origId);
            return 1;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(origId);
            throw th;
        }
    }

    /* access modifiers changed from: package-private */
    public void stopInBackgroundLocked(int uid) {
        ServiceMap services2 = this.mServiceMap.get(UserHandle.getUserId(uid));
        ArrayList<ServiceRecord> stopping = null;
        if (services2 != null) {
            for (int i = services2.mServicesByInstanceName.size() - 1; i >= 0; i--) {
                ServiceRecord service = services2.mServicesByInstanceName.valueAt(i);
                if (service.appInfo.uid == uid && service.startRequested && this.mAm.getAppStartModeLocked(service.appInfo.uid, service.packageName, service.appInfo.targetSdkVersion, -1, false, false, false, service.callerPackage) != 0) {
                    if (stopping == null) {
                        stopping = new ArrayList<>();
                    }
                    String compName = service.shortInstanceName;
                    EventLogTags.writeAmStopIdleService(service.appInfo.uid, compName);
                    StringBuilder sb = new StringBuilder(64);
                    sb.append("Stopping service due to app idle: ");
                    UserHandle.formatUid(sb, service.appInfo.uid);
                    sb.append(" ");
                    TimeUtils.formatDuration(service.createRealTime - SystemClock.elapsedRealtime(), sb);
                    sb.append(" ");
                    sb.append(compName);
                    Slog.w("ActivityManager", sb.toString());
                    stopping.add(service);
                    if (appRestrictedAnyInBackground(service.appInfo.uid, service.packageName)) {
                        cancelForegroundNotificationLocked(service);
                    }
                }
            }
            if (stopping != null) {
                for (int i2 = stopping.size() - 1; i2 >= 0; i2--) {
                    ServiceRecord service2 = stopping.get(i2);
                    service2.delayed = false;
                    services2.ensureNotStartingBackgroundLocked(service2);
                    stopServiceLocked(service2);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public IBinder peekServiceLocked(Intent service, String resolvedType, String callingPackage) {
        ServiceLookupResult r = retrieveServiceLocked(service, (String) null, resolvedType, callingPackage, Binder.getCallingPid(), Binder.getCallingUid(), UserHandle.getCallingUserId(), false, false, false, false);
        if (r == null) {
            return null;
        }
        if (r.record != null) {
            IntentBindRecord ib = r.record.bindings.get(r.record.intent);
            if (ib != null) {
                return ib.binder;
            }
            return null;
        }
        throw new SecurityException("Permission Denial: Accessing service from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires " + r.permission);
    }

    /* access modifiers changed from: package-private */
    public boolean stopServiceTokenLocked(ComponentName className, IBinder token, int startId) {
        ServiceRecord r = findServiceLocked(className, token, UserHandle.getCallingUserId());
        if (r == null) {
            return false;
        }
        if (startId >= 0) {
            ServiceRecord.StartItem si = r.findDeliveredStart(startId, false, false);
            if (si != null) {
                while (r.deliveredStarts.size() > 0) {
                    ServiceRecord.StartItem cur = r.deliveredStarts.remove(0);
                    cur.removeUriPermissionsLocked();
                    if (cur == si) {
                        break;
                    }
                }
            }
            if (r.getLastStartId() != startId) {
                return false;
            }
            if (r.deliveredStarts.size() > 0) {
                Slog.w("ActivityManager", "stopServiceToken startId " + startId + " is last, but have " + r.deliveredStarts.size() + " remaining args");
            }
        }
        StatsLog.write(99, r.appInfo.uid, r.name.getPackageName(), r.name.getClassName(), 2);
        synchronized (r.stats.getBatteryStats()) {
            r.stats.stopRunningLocked();
        }
        r.startRequested = false;
        if (r.tracker != null) {
            r.tracker.setStarted(false, this.mAm.mProcessStats.getMemFactorLocked(), SystemClock.uptimeMillis());
        }
        r.callStart = false;
        long origId = Binder.clearCallingIdentity();
        bringDownServiceIfNeededLocked(r, false, false);
        Binder.restoreCallingIdentity(origId);
        return true;
    }

    public void setServiceForegroundLocked(ComponentName className, IBinder token, int id, Notification notification, int flags, int foregroundServiceType) {
        int userId = UserHandle.getCallingUserId();
        long origId = Binder.clearCallingIdentity();
        ComponentName componentName = className;
        IBinder iBinder = token;
        try {
            ServiceRecord r = findServiceLocked(className, token, userId);
            if (r != null) {
                setServiceForegroundInnerLocked(r, id, notification, flags, foregroundServiceType);
            }
        } finally {
            Binder.restoreCallingIdentity(origId);
        }
    }

    public int getForegroundServiceTypeLocked(ComponentName className, IBinder token) {
        int userId = UserHandle.getCallingUserId();
        long origId = Binder.clearCallingIdentity();
        int ret = 0;
        try {
            ServiceRecord r = findServiceLocked(className, token, userId);
            if (r != null) {
                ret = r.foregroundServiceType;
            }
            return ret;
        } finally {
            Binder.restoreCallingIdentity(origId);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean foregroundAppShownEnoughLocked(ActiveForegroundApp aa, long nowElapsed) {
        long j;
        aa.mHideTime = JobStatus.NO_LATEST_RUNTIME;
        if (aa.mShownWhileTop) {
            return true;
        }
        if (this.mScreenOn || aa.mShownWhileScreenOn) {
            long minTime = aa.mStartVisibleTime;
            if (aa.mStartTime != aa.mStartVisibleTime) {
                j = this.mAm.mConstants.FGSERVICE_SCREEN_ON_AFTER_TIME;
            } else {
                j = this.mAm.mConstants.FGSERVICE_MIN_SHOWN_TIME;
            }
            long minTime2 = minTime + j;
            if (nowElapsed >= minTime2) {
                return true;
            }
            long reportTime = this.mAm.mConstants.FGSERVICE_MIN_REPORT_TIME + nowElapsed;
            aa.mHideTime = reportTime > minTime2 ? reportTime : minTime2;
            return false;
        }
        long minTime3 = aa.mEndTime + this.mAm.mConstants.FGSERVICE_SCREEN_ON_BEFORE_TIME;
        if (nowElapsed >= minTime3) {
            return true;
        }
        aa.mHideTime = minTime3;
        return false;
    }

    /* access modifiers changed from: package-private */
    public void updateForegroundApps(ServiceMap smap) {
        ArrayList<ActiveForegroundApp> active = null;
        synchronized (this.mAm) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                long now = SystemClock.elapsedRealtime();
                long nextUpdateTime = JobStatus.NO_LATEST_RUNTIME;
                if (smap != null) {
                    for (int i = smap.mActiveForegroundApps.size() - 1; i >= 0; i--) {
                        ActiveForegroundApp aa = smap.mActiveForegroundApps.valueAt(i);
                        if (aa.mEndTime != 0) {
                            if (foregroundAppShownEnoughLocked(aa, now)) {
                                smap.mActiveForegroundApps.removeAt(i);
                                smap.mActiveForegroundAppsChanged = true;
                            } else if (aa.mHideTime < nextUpdateTime) {
                                nextUpdateTime = aa.mHideTime;
                            }
                        }
                        if (!aa.mAppOnTop) {
                            if (active == null) {
                                active = new ArrayList<>();
                            }
                            active.add(aa);
                        }
                    }
                    smap.removeMessages(2);
                    if (nextUpdateTime < JobStatus.NO_LATEST_RUNTIME) {
                        smap.sendMessageAtTime(smap.obtainMessage(2), (SystemClock.uptimeMillis() + nextUpdateTime) - SystemClock.elapsedRealtime());
                    }
                }
                if (!smap.mActiveForegroundAppsChanged) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                smap.mActiveForegroundAppsChanged = false;
                ActivityManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    private void requestUpdateActiveForegroundAppsLocked(ServiceMap smap, long timeElapsed) {
        Message msg = smap.obtainMessage(2);
        if (timeElapsed != 0) {
            smap.sendMessageAtTime(msg, (SystemClock.uptimeMillis() + timeElapsed) - SystemClock.elapsedRealtime());
            return;
        }
        smap.mActiveForegroundAppsChanged = true;
        smap.sendMessage(msg);
    }

    private void decActiveForegroundAppLocked(ServiceMap smap, ServiceRecord r) {
        ActiveForegroundApp active = smap.mActiveForegroundApps.get(r.packageName);
        if (active != null) {
            active.mNumActive--;
            if (active.mNumActive <= 0) {
                active.mEndTime = SystemClock.elapsedRealtime();
                if (foregroundAppShownEnoughLocked(active, active.mEndTime)) {
                    smap.mActiveForegroundApps.remove(r.packageName);
                    smap.mActiveForegroundAppsChanged = true;
                    requestUpdateActiveForegroundAppsLocked(smap, 0);
                } else if (active.mHideTime < JobStatus.NO_LATEST_RUNTIME) {
                    requestUpdateActiveForegroundAppsLocked(smap, active.mHideTime);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void updateScreenStateLocked(boolean screenOn) {
        if (this.mScreenOn != screenOn) {
            this.mScreenOn = screenOn;
            if (screenOn) {
                long nowElapsed = SystemClock.elapsedRealtime();
                for (int i = this.mServiceMap.size() - 1; i >= 0; i--) {
                    ServiceMap smap = this.mServiceMap.valueAt(i);
                    long nextUpdateTime = JobStatus.NO_LATEST_RUNTIME;
                    boolean changed = false;
                    for (int j = smap.mActiveForegroundApps.size() - 1; j >= 0; j--) {
                        ActiveForegroundApp active = smap.mActiveForegroundApps.valueAt(j);
                        if (active.mEndTime != 0) {
                            if (!active.mShownWhileScreenOn && active.mStartVisibleTime == active.mStartTime) {
                                active.mStartVisibleTime = nowElapsed;
                                active.mEndTime = nowElapsed;
                            }
                            if (foregroundAppShownEnoughLocked(active, nowElapsed)) {
                                smap.mActiveForegroundApps.remove(active.mPackageName);
                                smap.mActiveForegroundAppsChanged = true;
                                changed = true;
                            } else if (active.mHideTime < nextUpdateTime) {
                                nextUpdateTime = active.mHideTime;
                            }
                        } else if (!active.mShownWhileScreenOn) {
                            active.mShownWhileScreenOn = true;
                            active.mStartVisibleTime = nowElapsed;
                        }
                    }
                    if (changed) {
                        requestUpdateActiveForegroundAppsLocked(smap, 0);
                    } else if (nextUpdateTime < JobStatus.NO_LATEST_RUNTIME) {
                        requestUpdateActiveForegroundAppsLocked(smap, nextUpdateTime);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void foregroundServiceProcStateChangedLocked(UidRecord uidRec) {
        ServiceMap smap = this.mServiceMap.get(UserHandle.getUserId(uidRec.uid));
        if (smap != null) {
            boolean changed = false;
            for (int j = smap.mActiveForegroundApps.size() - 1; j >= 0; j--) {
                ActiveForegroundApp active = smap.mActiveForegroundApps.valueAt(j);
                if (active.mUid == uidRec.uid) {
                    if (uidRec.getCurProcState() <= 2) {
                        if (!active.mAppOnTop) {
                            active.mAppOnTop = true;
                            changed = true;
                        }
                        active.mShownWhileTop = true;
                    } else if (active.mAppOnTop) {
                        active.mAppOnTop = false;
                        changed = true;
                    }
                }
            }
            if (changed) {
                requestUpdateActiveForegroundAppsLocked(smap, 0);
            }
        }
    }

    private boolean appIsTopLocked(int uid) {
        return this.mAm.getUidState(uid) <= 2;
    }

    /* Debug info: failed to restart local var, previous not found, register: 22 */
    /* access modifiers changed from: private */
    public void setServiceForegroundInnerLocked(ServiceRecord r, int id, Notification notification, int flags, int foregroundServiceType) {
        int foregroundServiceType2;
        ServiceState stracker;
        ServiceRecord serviceRecord = r;
        int i = id;
        Notification notification2 = notification;
        if (i == 0) {
            int i2 = foregroundServiceType;
            if (serviceRecord.isForeground) {
                ServiceMap smap = getServiceMapLocked(serviceRecord.userId);
                if (smap != null) {
                    decActiveForegroundAppLocked(smap, serviceRecord);
                }
                serviceRecord.isForeground = false;
                ServiceState stracker2 = r.getTracker();
                if (stracker2 != null) {
                    stracker2.setForeground(false, this.mAm.mProcessStats.getMemFactorLocked(), serviceRecord.lastActivity);
                }
                this.mAm.mAppOpsService.finishOperation(AppOpsManager.getToken(this.mAm.mAppOpsService), 76, serviceRecord.appInfo.uid, serviceRecord.packageName);
                StatsLog.write(60, serviceRecord.appInfo.uid, serviceRecord.shortInstanceName, 2);
                this.mAm.updateForegroundServiceUsageStats(serviceRecord.name, serviceRecord.userId, false);
                if (serviceRecord.app != null) {
                    this.mAm.updateLruProcessLocked(serviceRecord.app, false, (ProcessRecord) null);
                    updateServiceForegroundLocked(serviceRecord.app, true);
                }
            }
            if ((flags & 1) != 0) {
                cancelForegroundNotificationLocked(r);
                serviceRecord.foregroundId = 0;
                serviceRecord.foregroundNoti = null;
            } else if (serviceRecord.appInfo.targetSdkVersion >= 21) {
                r.stripForegroundServiceFlagFromNotification();
                if ((flags & 2) != 0) {
                    serviceRecord.foregroundId = 0;
                    serviceRecord.foregroundNoti = null;
                }
            }
            int i3 = i2;
        } else if (notification2 != null) {
            if (serviceRecord.appInfo.isInstantApp()) {
                int mode = this.mAm.mAppOpsService.checkOperation(68, serviceRecord.appInfo.uid, serviceRecord.appInfo.packageName);
                if (mode != 0) {
                    if (mode == 1) {
                        Slog.w("ActivityManager", "Instant app " + serviceRecord.appInfo.packageName + " does not have permission to create foreground services, ignoring.");
                        return;
                    } else if (mode != 2) {
                        this.mAm.enforcePermission("android.permission.INSTANT_APP_FOREGROUND_SERVICE", serviceRecord.app.pid, serviceRecord.appInfo.uid, "startForeground");
                    } else {
                        throw new SecurityException("Instant app " + serviceRecord.appInfo.packageName + " does not have permission to create foreground services");
                    }
                }
                foregroundServiceType2 = foregroundServiceType;
            } else {
                if (serviceRecord.appInfo.targetSdkVersion >= 28) {
                    this.mAm.enforcePermission("android.permission.FOREGROUND_SERVICE", serviceRecord.app.pid, serviceRecord.appInfo.uid, "startForeground");
                }
                int manifestType = serviceRecord.serviceInfo.getForegroundServiceType();
                int i4 = foregroundServiceType;
                if (i4 == -1) {
                    foregroundServiceType2 = manifestType;
                } else {
                    foregroundServiceType2 = i4;
                }
                if ((foregroundServiceType2 & manifestType) != foregroundServiceType2) {
                    throw new IllegalArgumentException("foregroundServiceType " + String.format("0x%08X", new Object[]{Integer.valueOf(foregroundServiceType2)}) + " is not a subset of foregroundServiceType attribute " + String.format("0x%08X", new Object[]{Integer.valueOf(manifestType)}) + " in service element of manifest file");
                }
            }
            boolean alreadyStartedOp = false;
            boolean stopProcStatsOp = false;
            if (serviceRecord.fgRequired) {
                serviceRecord.fgRequired = false;
                serviceRecord.fgWaiting = false;
                stopProcStatsOp = true;
                alreadyStartedOp = true;
                this.mAm.mHandler.removeMessages(66, serviceRecord);
            }
            boolean ignoreForeground = false;
            try {
                int mode2 = this.mAm.mAppOpsService.checkOperation(76, serviceRecord.appInfo.uid, serviceRecord.packageName);
                if (mode2 != 0) {
                    if (mode2 == 1) {
                        Slog.w("ActivityManager", "Service.startForeground() not allowed due to app op: service " + serviceRecord.shortInstanceName);
                        ignoreForeground = true;
                    } else if (mode2 != 3) {
                        throw new SecurityException("Foreground not allowed as per app op");
                    }
                }
                if (!ignoreForeground && !appIsTopLocked(serviceRecord.appInfo.uid) && appRestrictedAnyInBackground(serviceRecord.appInfo.uid, serviceRecord.packageName)) {
                    Slog.w("ActivityManager", "Service.startForeground() not allowed due to bg restriction: service " + serviceRecord.shortInstanceName);
                    updateServiceForegroundLocked(serviceRecord.app, false);
                    ignoreForeground = true;
                }
                if (!ignoreForeground) {
                    if (serviceRecord.foregroundId != i) {
                        cancelForegroundNotificationLocked(r);
                        serviceRecord.foregroundId = i;
                    }
                    notification2.flags |= 64;
                    serviceRecord.foregroundNoti = notification2;
                    serviceRecord.foregroundServiceType = foregroundServiceType2;
                    if (!serviceRecord.isForeground) {
                        ServiceMap smap2 = getServiceMapLocked(serviceRecord.userId);
                        if (smap2 != null) {
                            ActiveForegroundApp active = smap2.mActiveForegroundApps.get(serviceRecord.packageName);
                            if (active == null) {
                                active = new ActiveForegroundApp();
                                active.mPackageName = serviceRecord.packageName;
                                active.mUid = serviceRecord.appInfo.uid;
                                active.mShownWhileScreenOn = this.mScreenOn;
                                if (serviceRecord.app != null) {
                                    boolean z = serviceRecord.app.uidRecord.getCurProcState() <= 2;
                                    active.mShownWhileTop = z;
                                    active.mAppOnTop = z;
                                }
                                long elapsedRealtime = SystemClock.elapsedRealtime();
                                active.mStartVisibleTime = elapsedRealtime;
                                active.mStartTime = elapsedRealtime;
                                smap2.mActiveForegroundApps.put(serviceRecord.packageName, active);
                                requestUpdateActiveForegroundAppsLocked(smap2, 0);
                            }
                            active.mNumActive++;
                        }
                        serviceRecord.isForeground = true;
                        if (!stopProcStatsOp) {
                            ServiceState stracker3 = r.getTracker();
                            if (stracker3 != null) {
                                stracker3.setForeground(true, this.mAm.mProcessStats.getMemFactorLocked(), serviceRecord.lastActivity);
                            }
                        } else {
                            stopProcStatsOp = false;
                        }
                        this.mAm.mAppOpsService.startOperation(AppOpsManager.getToken(this.mAm.mAppOpsService), 76, serviceRecord.appInfo.uid, serviceRecord.packageName, true);
                        StatsLog.write(60, serviceRecord.appInfo.uid, serviceRecord.shortInstanceName, 1);
                        this.mAm.updateForegroundServiceUsageStats(serviceRecord.name, serviceRecord.userId, true);
                    }
                    r.postNotification();
                    if (serviceRecord.app != null) {
                        updateServiceForegroundLocked(serviceRecord.app, true);
                    }
                    getServiceMapLocked(serviceRecord.userId).ensureNotStartingBackgroundLocked(serviceRecord);
                    this.mAm.notifyPackageUse(serviceRecord.serviceInfo.packageName, 2);
                }
            } finally {
                if (stopProcStatsOp && (stracker = r.getTracker()) != null) {
                    stracker.setForeground(false, this.mAm.mProcessStats.getMemFactorLocked(), serviceRecord.lastActivity);
                }
                if (alreadyStartedOp) {
                    this.mAm.mAppOpsService.finishOperation(AppOpsManager.getToken(this.mAm.mAppOpsService), 76, serviceRecord.appInfo.uid, serviceRecord.packageName);
                }
            }
        } else {
            int i5 = foregroundServiceType;
            throw new IllegalArgumentException("null notification");
        }
    }

    private void cancelForegroundNotificationLocked(ServiceRecord r) {
        if (r.foregroundId != 0) {
            ServiceMap sm = getServiceMapLocked(r.userId);
            if (sm != null) {
                int i = sm.mServicesByInstanceName.size() - 1;
                while (i >= 0) {
                    ServiceRecord other = sm.mServicesByInstanceName.valueAt(i);
                    if (other == r || other.foregroundId != r.foregroundId || !other.packageName.equals(r.packageName)) {
                        i--;
                    } else {
                        return;
                    }
                }
            }
            r.cancelNotification();
        }
    }

    private void updateServiceForegroundLocked(ProcessRecord proc, boolean oomAdj) {
        boolean anyForeground = false;
        int fgServiceTypes = 0;
        for (int i = proc.f3services.size() - 1; i >= 0; i--) {
            ServiceRecord sr = proc.f3services.valueAt(i);
            if (sr.isForeground || sr.fgRequired) {
                anyForeground = true;
                fgServiceTypes |= sr.foregroundServiceType;
            }
        }
        this.mAm.updateProcessForegroundLocked(proc, anyForeground, fgServiceTypes, oomAdj);
    }

    private void updateWhitelistManagerLocked(ProcessRecord proc) {
        proc.whitelistManager = false;
        for (int i = proc.f3services.size() - 1; i >= 0; i--) {
            if (proc.f3services.valueAt(i).whitelistManager) {
                proc.whitelistManager = true;
                return;
            }
        }
    }

    public void updateServiceConnectionActivitiesLocked(ProcessRecord clientProc) {
        ArraySet<ProcessRecord> updatedProcesses = null;
        for (int i = 0; i < clientProc.connections.size(); i++) {
            ProcessRecord proc = clientProc.connections.valueAt(i).binding.service.app;
            if (!(proc == null || proc == clientProc)) {
                if (updatedProcesses == null) {
                    updatedProcesses = new ArraySet<>();
                } else if (updatedProcesses.contains(proc)) {
                }
                updatedProcesses.add(proc);
                updateServiceClientActivitiesLocked(proc, (ConnectionRecord) null, false);
            }
        }
    }

    private boolean updateServiceClientActivitiesLocked(ProcessRecord proc, ConnectionRecord modCr, boolean updateLru) {
        if (modCr != null && modCr.binding.client != null && !modCr.binding.client.hasActivities()) {
            return false;
        }
        boolean anyClientActivities = false;
        for (int i = proc.f3services.size() - 1; i >= 0 && !anyClientActivities; i--) {
            ArrayMap<IBinder, ArrayList<ConnectionRecord>> connections = proc.f3services.valueAt(i).getConnections();
            for (int conni = connections.size() - 1; conni >= 0 && !anyClientActivities; conni--) {
                ArrayList<ConnectionRecord> clist = connections.valueAt(conni);
                int cri = clist.size() - 1;
                while (true) {
                    if (cri < 0) {
                        break;
                    }
                    ConnectionRecord cr = clist.get(cri);
                    if (cr.binding.client != null && cr.binding.client != proc && cr.binding.client.hasActivities()) {
                        anyClientActivities = true;
                        break;
                    }
                    cri--;
                }
            }
        }
        if (anyClientActivities == proc.hasClientActivities()) {
            return false;
        }
        proc.setHasClientActivities(anyClientActivities);
        if (updateLru) {
            this.mAm.updateLruProcessLocked(proc, anyClientActivities, (ProcessRecord) null);
        }
        return true;
    }

    /* JADX WARNING: type inference failed for: r4v14, types: [android.os.Parcelable] */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int bindServiceLocked(android.app.IApplicationThread r41, android.os.IBinder r42, android.content.Intent r43, java.lang.String r44, android.app.IServiceConnection r45, int r46, java.lang.String r47, java.lang.String r48, int r49) throws android.os.TransactionTooLargeException {
        /*
            r40 = this;
            r13 = r40
            r14 = r41
            r15 = r42
            r0 = r43
            com.android.server.am.ActivityManagerService r1 = r13.mAm
            com.android.server.am.ProcessRecord r10 = r1.getRecordForAppLocked(r14)
            java.lang.String r1 = " (pid="
            if (r10 == 0) goto L_0x04ae
            r2 = 0
            java.lang.String r9 = "ActivityManager"
            r8 = 0
            if (r15 == 0) goto L_0x0039
            com.android.server.am.ActivityManagerService r3 = r13.mAm
            com.android.server.wm.ActivityTaskManagerInternal r3 = r3.mAtmInternal
            com.android.server.wm.ActivityServiceConnectionsHolder r2 = r3.getServiceConnectionsHolder(r15)
            if (r2 != 0) goto L_0x0037
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = "Binding with unknown activity: "
            r1.append(r3)
            r1.append(r15)
            java.lang.String r1 = r1.toString()
            android.util.Slog.w(r9, r1)
            return r8
        L_0x0037:
            r7 = r2
            goto L_0x003a
        L_0x0039:
            r7 = r2
        L_0x003a:
            r2 = 0
            r3 = 0
            android.content.pm.ApplicationInfo r4 = r10.info
            int r4 = r4.uid
            r5 = 1000(0x3e8, float:1.401E-42)
            r6 = 1
            if (r4 != r5) goto L_0x0047
            r4 = r6
            goto L_0x0048
        L_0x0047:
            r4 = r8
        L_0x0048:
            r16 = r4
            if (r16 == 0) goto L_0x0078
            r0.setDefusable(r6)
            java.lang.String r4 = "android.intent.extra.client_intent"
            android.os.Parcelable r4 = r0.getParcelableExtra(r4)
            r3 = r4
            android.app.PendingIntent r3 = (android.app.PendingIntent) r3
            if (r3 == 0) goto L_0x0072
            java.lang.String r4 = "android.intent.extra.client_label"
            int r2 = r0.getIntExtra(r4, r8)
            if (r2 == 0) goto L_0x006c
            android.content.Intent r0 = r43.cloneFilter()
            r5 = r0
            r18 = r2
            r17 = r3
            goto L_0x007d
        L_0x006c:
            r5 = r0
            r18 = r2
            r17 = r3
            goto L_0x007d
        L_0x0072:
            r5 = r0
            r18 = r2
            r17 = r3
            goto L_0x007d
        L_0x0078:
            r5 = r0
            r18 = r2
            r17 = r3
        L_0x007d:
            r19 = 134217728(0x8000000, float:3.85186E-34)
            r0 = r46 & r19
            if (r0 == 0) goto L_0x008c
            com.android.server.am.ActivityManagerService r0 = r13.mAm
            java.lang.String r2 = "android.permission.MANAGE_ACTIVITY_STACKS"
            java.lang.String r3 = "BIND_TREAT_LIKE_ACTIVITY"
            r0.enforceCallingPermission(r2, r3)
        L_0x008c:
            r0 = 524288(0x80000, float:7.34684E-40)
            r0 = r46 & r0
            if (r0 == 0) goto L_0x00b8
            if (r16 == 0) goto L_0x0095
            goto L_0x00b8
        L_0x0095:
            java.lang.SecurityException r0 = new java.lang.SecurityException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Non-system caller (pid="
            r1.append(r2)
            int r2 = android.os.Binder.getCallingPid()
            r1.append(r2)
            java.lang.String r2 = ") set BIND_SCHEDULE_LIKE_TOP_APP when binding service "
            r1.append(r2)
            r1.append(r5)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            throw r0
        L_0x00b8:
            r20 = 16777216(0x1000000, float:2.3509887E-38)
            r0 = r46 & r20
            java.lang.String r2 = "Non-system caller "
            if (r0 == 0) goto L_0x00ea
            if (r16 == 0) goto L_0x00c3
            goto L_0x00ea
        L_0x00c3:
            java.lang.SecurityException r0 = new java.lang.SecurityException
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r2)
            r3.append(r14)
            r3.append(r1)
            int r1 = android.os.Binder.getCallingPid()
            r3.append(r1)
            java.lang.String r1 = ") set BIND_ALLOW_WHITELIST_MANAGEMENT when binding service "
            r3.append(r1)
            r3.append(r5)
            java.lang.String r1 = r3.toString()
            r0.<init>(r1)
            throw r0
        L_0x00ea:
            r0 = 4194304(0x400000, float:5.877472E-39)
            r3 = r46 & r0
            if (r3 == 0) goto L_0x011a
            if (r16 == 0) goto L_0x00f3
            goto L_0x011a
        L_0x00f3:
            java.lang.SecurityException r0 = new java.lang.SecurityException
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r2)
            r3.append(r14)
            r3.append(r1)
            int r1 = android.os.Binder.getCallingPid()
            r3.append(r1)
            java.lang.String r1 = ") set BIND_ALLOW_INSTANT when binding service "
            r3.append(r1)
            r3.append(r5)
            java.lang.String r1 = r3.toString()
            r0.<init>(r1)
            throw r0
        L_0x011a:
            r21 = 1048576(0x100000, float:1.469368E-39)
            r1 = r46 & r21
            if (r1 == 0) goto L_0x0129
            com.android.server.am.ActivityManagerService r1 = r13.mAm
            java.lang.String r2 = "android.permission.START_ACTIVITIES_FROM_BACKGROUND"
            java.lang.String r3 = "BIND_ALLOW_BACKGROUND_ACTIVITY_STARTS"
            r1.enforceCallingPermission(r2, r3)
        L_0x0129:
            int r1 = r10.setSchedGroup
            if (r1 == 0) goto L_0x012f
            r1 = r6
            goto L_0x0130
        L_0x012f:
            r1 = r8
        L_0x0130:
            r4 = r1
            r1 = -2147483648(0xffffffff80000000, float:-0.0)
            r1 = r46 & r1
            if (r1 == 0) goto L_0x0139
            r11 = r6
            goto L_0x013a
        L_0x0139:
            r11 = r8
        L_0x013a:
            r0 = r46 & r0
            if (r0 == 0) goto L_0x0140
            r12 = r6
            goto L_0x0141
        L_0x0140:
            r12 = r8
        L_0x0141:
            int r0 = android.os.Binder.getCallingPid()
            int r22 = android.os.Binder.getCallingUid()
            r23 = 1
            r1 = r40
            r2 = r5
            r3 = r47
            r43 = r4
            r4 = r44
            r24 = r5
            r5 = r48
            r6 = r0
            r26 = r7
            r7 = r22
            r15 = r8
            r8 = r49
            r27 = r9
            r9 = r23
            r28 = r10
            r10 = r43
            com.android.server.am.ActiveServices$ServiceLookupResult r10 = r1.retrieveServiceLocked(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12)
            if (r10 != 0) goto L_0x0170
            return r15
        L_0x0170:
            com.android.server.am.ServiceRecord r0 = r10.record
            r9 = -1
            if (r0 != 0) goto L_0x0176
            return r9
        L_0x0176:
            com.android.server.am.ServiceRecord r8 = r10.record
            r0 = 0
            com.android.server.am.ActivityManagerService r1 = r13.mAm
            android.content.pm.PackageManagerInternal r1 = r1.getPackageManagerInternalLocked()
            java.lang.String r2 = r8.packageName
            int r3 = r8.userId
            boolean r1 = r1.isPermissionsReviewRequired(r2, r3)
            if (r1 == 0) goto L_0x01fb
            r0 = 1
            r7 = r43
            if (r7 != 0) goto L_0x01b7
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "u"
            r1.append(r2)
            int r2 = r8.userId
            r1.append(r2)
            java.lang.String r2 = " Binding to a service in package"
            r1.append(r2)
            java.lang.String r2 = r8.packageName
            r1.append(r2)
            java.lang.String r2 = " requires a permissions review"
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r6 = r27
            android.util.Slog.w(r6, r1)
            return r15
        L_0x01b7:
            r6 = r27
            r3 = r8
            r4 = r24
            android.os.RemoteCallback r5 = new android.os.RemoteCallback
            com.android.server.am.ActiveServices$3 r2 = new com.android.server.am.ActiveServices$3
            r1 = r2
            r9 = r2
            r2 = r40
            r15 = r5
            r5 = r7
            r23 = r10
            r10 = r6
            r6 = r45
            r1.<init>(r3, r4, r5, r6)
            r15.<init>(r9)
            r1 = r15
            android.content.Intent r2 = new android.content.Intent
            java.lang.String r5 = "android.intent.action.REVIEW_PERMISSIONS"
            r2.<init>(r5)
            r5 = 411041792(0x18800000, float:3.3087225E-24)
            r2.addFlags(r5)
            java.lang.String r5 = r8.packageName
            java.lang.String r6 = "android.intent.extra.PACKAGE_NAME"
            r2.putExtra(r6, r5)
            java.lang.String r5 = "android.intent.extra.REMOTE_CALLBACK"
            r2.putExtra(r5, r1)
            com.android.server.am.ActivityManagerService r5 = r13.mAm
            com.android.server.am.ActivityManagerService$MainHandler r5 = r5.mHandler
            com.android.server.am.ActiveServices$4 r6 = new com.android.server.am.ActiveServices$4
            r15 = r49
            r6.<init>(r2, r15)
            r5.post(r6)
            r27 = r0
            goto L_0x0205
        L_0x01fb:
            r7 = r43
            r15 = r49
            r23 = r10
            r10 = r27
            r27 = r0
        L_0x0205:
            long r29 = android.os.Binder.clearCallingIdentity()
            r9 = r28
            android.content.pm.ApplicationInfo r0 = r9.info     // Catch:{ all -> 0x049e }
            int r0 = r0.uid     // Catch:{ all -> 0x049e }
            r1 = 0
            r13.unscheduleServiceRestartLocked(r8, r0, r1)     // Catch:{ all -> 0x049e }
            r0 = r46 & 1
            if (r0 == 0) goto L_0x024a
            long r0 = android.os.SystemClock.uptimeMillis()     // Catch:{ all -> 0x023c }
            r8.lastActivity = r0     // Catch:{ all -> 0x023c }
            boolean r0 = r8.hasAutoCreateConnections()     // Catch:{ all -> 0x023c }
            if (r0 != 0) goto L_0x023a
            com.android.internal.app.procstats.ServiceState r0 = r8.getTracker()     // Catch:{ all -> 0x023c }
            if (r0 == 0) goto L_0x0238
            com.android.server.am.ActivityManagerService r1 = r13.mAm     // Catch:{ all -> 0x023c }
            com.android.server.am.ProcessStatsService r1 = r1.mProcessStats     // Catch:{ all -> 0x023c }
            int r1 = r1.getMemFactorLocked()     // Catch:{ all -> 0x023c }
            long r2 = r8.lastActivity     // Catch:{ all -> 0x023c }
            r6 = 1
            r0.setBound(r6, r1, r2)     // Catch:{ all -> 0x023c }
            goto L_0x024b
        L_0x0238:
            r6 = 1
            goto L_0x024b
        L_0x023a:
            r6 = 1
            goto L_0x024b
        L_0x023c:
            r0 = move-exception
            r1 = r7
            r15 = r9
            r33 = r11
            r25 = r12
            r28 = r24
            r9 = r26
            r12 = r8
            goto L_0x04aa
        L_0x024a:
            r6 = 1
        L_0x024b:
            r0 = 2097152(0x200000, float:2.938736E-39)
            r0 = r46 & r0
            if (r0 == 0) goto L_0x025a
            com.android.server.am.ActivityManagerService r0 = r13.mAm     // Catch:{ all -> 0x023c }
            android.content.pm.ApplicationInfo r1 = r8.appInfo     // Catch:{ all -> 0x023c }
            java.lang.String r1 = r1.packageName     // Catch:{ all -> 0x023c }
            r0.requireAllowedAssociationsLocked(r1)     // Catch:{ all -> 0x023c }
        L_0x025a:
            com.android.server.am.ActivityManagerService r0 = r13.mAm     // Catch:{ all -> 0x049e }
            int r1 = r9.uid     // Catch:{ all -> 0x049e }
            java.lang.String r2 = r9.processName     // Catch:{ all -> 0x049e }
            int r34 = r9.getCurProcState()     // Catch:{ all -> 0x049e }
            android.content.pm.ApplicationInfo r3 = r8.appInfo     // Catch:{ all -> 0x049e }
            int r3 = r3.uid     // Catch:{ all -> 0x049e }
            android.content.pm.ApplicationInfo r4 = r8.appInfo     // Catch:{ all -> 0x049e }
            long r4 = r4.longVersionCode     // Catch:{ all -> 0x049e }
            android.content.ComponentName r6 = r8.instanceName     // Catch:{ all -> 0x049e }
            r28 = r7
            java.lang.String r7 = r8.processName     // Catch:{ all -> 0x0490 }
            r31 = r0
            r32 = r1
            r33 = r2
            r35 = r3
            r36 = r4
            r38 = r6
            r39 = r7
            r31.startAssociationLocked(r32, r33, r34, r35, r36, r38, r39)     // Catch:{ all -> 0x0490 }
            com.android.server.am.ActivityManagerService r0 = r13.mAm     // Catch:{ all -> 0x0490 }
            int r1 = r9.userId     // Catch:{ all -> 0x0490 }
            android.content.pm.ApplicationInfo r2 = r8.appInfo     // Catch:{ all -> 0x0490 }
            int r2 = r2.uid     // Catch:{ all -> 0x0490 }
            int r2 = android.os.UserHandle.getAppId(r2)     // Catch:{ all -> 0x0490 }
            int r3 = r9.uid     // Catch:{ all -> 0x0490 }
            int r3 = android.os.UserHandle.getAppId(r3)     // Catch:{ all -> 0x0490 }
            r7 = r24
            r0.grantEphemeralAccessLocked(r1, r7, r2, r3)     // Catch:{ all -> 0x0482 }
            com.android.server.am.AppBindRecord r0 = r8.retrieveAppBindingLocked(r7, r9)     // Catch:{ all -> 0x0482 }
            r6 = r0
            com.android.server.am.ConnectionRecord r24 = new com.android.server.am.ConnectionRecord     // Catch:{ all -> 0x0482 }
            int r5 = r9.uid     // Catch:{ all -> 0x0482 }
            java.lang.String r4 = r9.processName     // Catch:{ all -> 0x0482 }
            r0 = r24
            r1 = r6
            r2 = r26
            r3 = r45
            r31 = r4
            r4 = r46
            r32 = r5
            r5 = r18
            r33 = r11
            r25 = r12
            r12 = 1
            r11 = r6
            r6 = r17
            r34 = r28
            r28 = r7
            r7 = r32
            r12 = r8
            r8 = r31
            r15 = r9
            r14 = -1
            r9 = r48
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9)     // Catch:{ all -> 0x047c }
            r7 = r24
            android.os.IBinder r0 = r45.asBinder()     // Catch:{ all -> 0x047c }
            r8 = r0
            r12.addConnection(r8, r7)     // Catch:{ all -> 0x047c }
            android.util.ArraySet<com.android.server.am.ConnectionRecord> r0 = r11.connections     // Catch:{ all -> 0x047c }
            r0.add(r7)     // Catch:{ all -> 0x047c }
            r9 = r26
            if (r9 == 0) goto L_0x02e8
            r9.addConnection(r7)     // Catch:{ all -> 0x02e3 }
            goto L_0x02e8
        L_0x02e3:
            r0 = move-exception
            r1 = r34
            goto L_0x04aa
        L_0x02e8:
            com.android.server.am.ProcessRecord r0 = r11.client     // Catch:{ all -> 0x0478 }
            android.util.ArraySet<com.android.server.am.ConnectionRecord> r0 = r0.connections     // Catch:{ all -> 0x0478 }
            r0.add(r7)     // Catch:{ all -> 0x0478 }
            r7.startAssociationIfNeeded()     // Catch:{ all -> 0x0478 }
            int r0 = r7.flags     // Catch:{ all -> 0x0478 }
            r0 = r0 & 8
            if (r0 == 0) goto L_0x02fd
            com.android.server.am.ProcessRecord r0 = r11.client     // Catch:{ all -> 0x02e3 }
            r1 = 1
            r0.hasAboveClient = r1     // Catch:{ all -> 0x02e3 }
        L_0x02fd:
            int r0 = r7.flags     // Catch:{ all -> 0x0478 }
            r0 = r0 & r20
            if (r0 == 0) goto L_0x0306
            r1 = 1
            r12.whitelistManager = r1     // Catch:{ all -> 0x02e3 }
        L_0x0306:
            r0 = r46 & r21
            if (r0 == 0) goto L_0x030e
            r1 = 1
            r12.setHasBindingWhitelistingBgActivityStarts(r1)     // Catch:{ all -> 0x02e3 }
        L_0x030e:
            com.android.server.am.ProcessRecord r0 = r12.app     // Catch:{ all -> 0x0478 }
            if (r0 == 0) goto L_0x0318
            com.android.server.am.ProcessRecord r0 = r12.app     // Catch:{ all -> 0x02e3 }
            r1 = 1
            r13.updateServiceClientActivitiesLocked(r0, r7, r1)     // Catch:{ all -> 0x02e3 }
        L_0x0318:
            android.util.ArrayMap<android.os.IBinder, java.util.ArrayList<com.android.server.am.ConnectionRecord>> r0 = r13.mServiceConnections     // Catch:{ all -> 0x0478 }
            java.lang.Object r0 = r0.get(r8)     // Catch:{ all -> 0x0478 }
            java.util.ArrayList r0 = (java.util.ArrayList) r0     // Catch:{ all -> 0x0478 }
            if (r0 != 0) goto L_0x032f
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ all -> 0x02e3 }
            r1.<init>()     // Catch:{ all -> 0x02e3 }
            r0 = r1
            android.util.ArrayMap<android.os.IBinder, java.util.ArrayList<com.android.server.am.ConnectionRecord>> r1 = r13.mServiceConnections     // Catch:{ all -> 0x02e3 }
            r1.put(r8, r0)     // Catch:{ all -> 0x02e3 }
            r6 = r0
            goto L_0x0330
        L_0x032f:
            r6 = r0
        L_0x0330:
            r6.add(r7)     // Catch:{ all -> 0x0478 }
            vendor.qti.hardware.servicetracker.V1_0.ServiceData r0 = new vendor.qti.hardware.servicetracker.V1_0.ServiceData     // Catch:{ all -> 0x0478 }
            r0.<init>()     // Catch:{ all -> 0x0478 }
            r5 = r0
            java.lang.String r0 = r12.packageName     // Catch:{ all -> 0x0478 }
            r5.packageName = r0     // Catch:{ all -> 0x0478 }
            java.lang.String r0 = r12.shortInstanceName     // Catch:{ all -> 0x0478 }
            r5.processName = r0     // Catch:{ all -> 0x0478 }
            long r0 = r12.lastActivity     // Catch:{ all -> 0x0478 }
            double r0 = (double) r0     // Catch:{ all -> 0x0478 }
            r5.lastActivity = r0     // Catch:{ all -> 0x0478 }
            com.android.server.am.ProcessRecord r0 = r12.app     // Catch:{ all -> 0x0478 }
            if (r0 == 0) goto L_0x0357
            com.android.server.am.ProcessRecord r0 = r12.app     // Catch:{ all -> 0x02e3 }
            int r0 = r0.pid     // Catch:{ all -> 0x02e3 }
            r5.pid = r0     // Catch:{ all -> 0x02e3 }
            com.android.server.am.ProcessRecord r0 = r12.app     // Catch:{ all -> 0x02e3 }
            boolean r0 = r0.serviceb     // Catch:{ all -> 0x02e3 }
            r5.serviceB = r0     // Catch:{ all -> 0x02e3 }
            goto L_0x035c
        L_0x0357:
            r5.pid = r14     // Catch:{ all -> 0x0478 }
            r1 = 0
            r5.serviceB = r1     // Catch:{ all -> 0x0478 }
        L_0x035c:
            vendor.qti.hardware.servicetracker.V1_0.ClientData r0 = new vendor.qti.hardware.servicetracker.V1_0.ClientData     // Catch:{ all -> 0x0478 }
            r0.<init>()     // Catch:{ all -> 0x0478 }
            r14 = r0
            java.lang.String r0 = r15.processName     // Catch:{ all -> 0x0478 }
            r14.processName = r0     // Catch:{ all -> 0x0478 }
            int r0 = r15.pid     // Catch:{ all -> 0x0478 }
            r14.pid = r0     // Catch:{ all -> 0x0478 }
            boolean r0 = r40.getServicetrackerInstance()     // Catch:{ RemoteException -> 0x0376 }
            if (r0 == 0) goto L_0x0375
            vendor.qti.hardware.servicetracker.V1_0.IServicetracker r0 = r13.mServicetracker     // Catch:{ RemoteException -> 0x0376 }
            r0.bindService(r5, r14)     // Catch:{ RemoteException -> 0x0376 }
        L_0x0375:
            goto L_0x037f
        L_0x0376:
            r0 = move-exception
            java.lang.String r1 = "Failed to send bind details to servicetracker HAL"
            android.util.Slog.e(r10, r1, r0)     // Catch:{ all -> 0x0478 }
            r1 = 0
            r13.mServicetracker = r1     // Catch:{ all -> 0x0478 }
        L_0x037f:
            r0 = r46 & 1
            if (r0 == 0) goto L_0x03a6
            long r0 = android.os.SystemClock.uptimeMillis()     // Catch:{ all -> 0x02e3 }
            r12.lastActivity = r0     // Catch:{ all -> 0x02e3 }
            int r3 = r28.getFlags()     // Catch:{ all -> 0x02e3 }
            r0 = 0
            r1 = r40
            r2 = r12
            r4 = r34
            r20 = r5
            r5 = r0
            r21 = r6
            r6 = r27
            java.lang.String r0 = r1.bringUpServiceLocked(r2, r3, r4, r5, r6)     // Catch:{ all -> 0x02e3 }
            if (r0 == 0) goto L_0x03aa
            android.os.Binder.restoreCallingIdentity(r29)
            r1 = 0
            return r1
        L_0x03a6:
            r20 = r5
            r21 = r6
        L_0x03aa:
            com.android.server.am.ProcessRecord r0 = r12.app     // Catch:{ all -> 0x0478 }
            if (r0 == 0) goto L_0x03ed
            r0 = r46 & r19
            if (r0 == 0) goto L_0x03b7
            com.android.server.am.ProcessRecord r0 = r12.app     // Catch:{ all -> 0x02e3 }
            r1 = 1
            r0.treatLikeActivity = r1     // Catch:{ all -> 0x02e3 }
        L_0x03b7:
            boolean r0 = r12.whitelistManager     // Catch:{ all -> 0x02e3 }
            if (r0 == 0) goto L_0x03c0
            com.android.server.am.ProcessRecord r0 = r12.app     // Catch:{ all -> 0x02e3 }
            r1 = 1
            r0.whitelistManager = r1     // Catch:{ all -> 0x02e3 }
        L_0x03c0:
            com.android.server.am.ActivityManagerService r0 = r13.mAm     // Catch:{ all -> 0x02e3 }
            com.android.server.am.ProcessRecord r1 = r12.app     // Catch:{ all -> 0x02e3 }
            boolean r2 = r15.hasActivitiesOrRecentTasks()     // Catch:{ all -> 0x02e3 }
            if (r2 == 0) goto L_0x03d2
            com.android.server.am.ProcessRecord r2 = r12.app     // Catch:{ all -> 0x02e3 }
            boolean r2 = r2.hasClientActivities()     // Catch:{ all -> 0x02e3 }
            if (r2 != 0) goto L_0x03dd
        L_0x03d2:
            int r2 = r15.getCurProcState()     // Catch:{ all -> 0x02e3 }
            r3 = 2
            if (r2 > r3) goto L_0x03df
            r2 = r46 & r19
            if (r2 == 0) goto L_0x03df
        L_0x03dd:
            r2 = 1
            goto L_0x03e0
        L_0x03df:
            r2 = 0
        L_0x03e0:
            com.android.server.am.ProcessRecord r3 = r11.client     // Catch:{ all -> 0x02e3 }
            r0.updateLruProcessLocked(r1, r2, r3)     // Catch:{ all -> 0x02e3 }
            com.android.server.am.ActivityManagerService r0 = r13.mAm     // Catch:{ all -> 0x02e3 }
            java.lang.String r1 = "updateOomAdj_bindService"
            r0.updateOomAdjLocked(r1)     // Catch:{ all -> 0x02e3 }
        L_0x03ed:
            com.android.server.am.ProcessRecord r0 = r12.app     // Catch:{ all -> 0x0478 }
            if (r0 == 0) goto L_0x0459
            com.android.server.am.IntentBindRecord r0 = r11.intent     // Catch:{ all -> 0x0478 }
            boolean r0 = r0.received     // Catch:{ all -> 0x0478 }
            if (r0 == 0) goto L_0x0459
            android.app.IServiceConnection r0 = r7.conn     // Catch:{ Exception -> 0x0404 }
            android.content.ComponentName r1 = r12.name     // Catch:{ Exception -> 0x0404 }
            com.android.server.am.IntentBindRecord r2 = r11.intent     // Catch:{ Exception -> 0x0404 }
            android.os.IBinder r2 = r2.binder     // Catch:{ Exception -> 0x0404 }
            r3 = 0
            r0.connected(r1, r2, r3)     // Catch:{ Exception -> 0x0404 }
            goto L_0x043c
        L_0x0404:
            r0 = move-exception
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0478 }
            r1.<init>()     // Catch:{ all -> 0x0478 }
            java.lang.String r2 = "Failure sending service "
            r1.append(r2)     // Catch:{ all -> 0x0478 }
            java.lang.String r2 = r12.shortInstanceName     // Catch:{ all -> 0x0478 }
            r1.append(r2)     // Catch:{ all -> 0x0478 }
            java.lang.String r2 = " to connection "
            r1.append(r2)     // Catch:{ all -> 0x0478 }
            android.app.IServiceConnection r2 = r7.conn     // Catch:{ all -> 0x0478 }
            android.os.IBinder r2 = r2.asBinder()     // Catch:{ all -> 0x0478 }
            r1.append(r2)     // Catch:{ all -> 0x0478 }
            java.lang.String r2 = " (in "
            r1.append(r2)     // Catch:{ all -> 0x0478 }
            com.android.server.am.AppBindRecord r2 = r7.binding     // Catch:{ all -> 0x0478 }
            com.android.server.am.ProcessRecord r2 = r2.client     // Catch:{ all -> 0x0478 }
            java.lang.String r2 = r2.processName     // Catch:{ all -> 0x0478 }
            r1.append(r2)     // Catch:{ all -> 0x0478 }
            java.lang.String r2 = ")"
            r1.append(r2)     // Catch:{ all -> 0x0478 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0478 }
            android.util.Slog.w(r10, r1, r0)     // Catch:{ all -> 0x0478 }
        L_0x043c:
            com.android.server.am.IntentBindRecord r0 = r11.intent     // Catch:{ all -> 0x0478 }
            android.util.ArrayMap<com.android.server.am.ProcessRecord, com.android.server.am.AppBindRecord> r0 = r0.apps     // Catch:{ all -> 0x0478 }
            int r0 = r0.size()     // Catch:{ all -> 0x0478 }
            r1 = 1
            if (r0 != r1) goto L_0x0456
            com.android.server.am.IntentBindRecord r0 = r11.intent     // Catch:{ all -> 0x0478 }
            boolean r0 = r0.doRebind     // Catch:{ all -> 0x0478 }
            if (r0 == 0) goto L_0x0456
            com.android.server.am.IntentBindRecord r0 = r11.intent     // Catch:{ all -> 0x0478 }
            r1 = r34
            r2 = 1
            r13.requestServiceBindingLocked(r12, r0, r1, r2)     // Catch:{ all -> 0x0476 }
            goto L_0x0467
        L_0x0456:
            r1 = r34
            goto L_0x0467
        L_0x0459:
            r1 = r34
            com.android.server.am.IntentBindRecord r0 = r11.intent     // Catch:{ all -> 0x0476 }
            boolean r0 = r0.requested     // Catch:{ all -> 0x0476 }
            if (r0 != 0) goto L_0x0467
            com.android.server.am.IntentBindRecord r0 = r11.intent     // Catch:{ all -> 0x0476 }
            r2 = 0
            r13.requestServiceBindingLocked(r12, r0, r1, r2)     // Catch:{ all -> 0x0476 }
        L_0x0467:
            int r0 = r12.userId     // Catch:{ all -> 0x0476 }
            com.android.server.am.ActiveServices$ServiceMap r0 = r13.getServiceMapLocked(r0)     // Catch:{ all -> 0x0476 }
            r0.ensureNotStartingBackgroundLocked(r12)     // Catch:{ all -> 0x0476 }
            android.os.Binder.restoreCallingIdentity(r29)
            r2 = 1
            return r2
        L_0x0476:
            r0 = move-exception
            goto L_0x04aa
        L_0x0478:
            r0 = move-exception
            r1 = r34
            goto L_0x04aa
        L_0x047c:
            r0 = move-exception
            r9 = r26
            r1 = r34
            goto L_0x04aa
        L_0x0482:
            r0 = move-exception
            r15 = r9
            r33 = r11
            r25 = r12
            r9 = r26
            r1 = r28
            r28 = r7
            r12 = r8
            goto L_0x04aa
        L_0x0490:
            r0 = move-exception
            r15 = r9
            r33 = r11
            r25 = r12
            r9 = r26
            r1 = r28
            r12 = r8
            r28 = r24
            goto L_0x04aa
        L_0x049e:
            r0 = move-exception
            r1 = r7
            r15 = r9
            r33 = r11
            r25 = r12
            r28 = r24
            r9 = r26
            r12 = r8
        L_0x04aa:
            android.os.Binder.restoreCallingIdentity(r29)
            throw r0
        L_0x04ae:
            java.lang.SecurityException r2 = new java.lang.SecurityException
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Unable to find app for caller "
            r3.append(r4)
            r4 = r41
            r3.append(r4)
            r3.append(r1)
            int r1 = android.os.Binder.getCallingPid()
            r3.append(r1)
            java.lang.String r1 = ") when binding service "
            r3.append(r1)
            r3.append(r0)
            java.lang.String r1 = r3.toString()
            r2.<init>(r1)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ActiveServices.bindServiceLocked(android.app.IApplicationThread, android.os.IBinder, android.content.Intent, java.lang.String, android.app.IServiceConnection, int, java.lang.String, java.lang.String, int):int");
    }

    /* access modifiers changed from: package-private */
    public void publishServiceLocked(ServiceRecord r, Intent intent, IBinder service) {
        ConnectionRecord c;
        ServiceRecord serviceRecord = r;
        IBinder iBinder = service;
        long origId = Binder.clearCallingIdentity();
        if (serviceRecord != null) {
            try {
                try {
                    Intent.FilterComparison filter = new Intent.FilterComparison(intent);
                    IntentBindRecord b = serviceRecord.bindings.get(filter);
                    boolean z = false;
                    if (b != null && !b.received) {
                        b.binder = iBinder;
                        b.requested = true;
                        b.received = true;
                        ArrayMap<IBinder, ArrayList<ConnectionRecord>> connections = r.getConnections();
                        int conni = connections.size() - 1;
                        while (conni >= 0) {
                            ArrayList valueAt = connections.valueAt(conni);
                            int i = z;
                            while (i < valueAt.size()) {
                                c = (ConnectionRecord) valueAt.get(i);
                                if (filter.equals(c.binding.intent.intent)) {
                                    c.conn.connected(serviceRecord.name, iBinder, z);
                                }
                                i++;
                                iBinder = service;
                                z = false;
                            }
                            conni--;
                            iBinder = service;
                            z = false;
                        }
                    }
                    serviceDoneExecutingLocked(serviceRecord, this.mDestroyingServices.contains(serviceRecord), false);
                } catch (Exception e) {
                    Slog.w("ActivityManager", "Failure sending service " + serviceRecord.shortInstanceName + " to connection " + c.conn.asBinder() + " (in " + c.binding.client.processName + ")", e);
                } catch (Throwable th) {
                    th = th;
                    Binder.restoreCallingIdentity(origId);
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                Intent intent2 = intent;
                Binder.restoreCallingIdentity(origId);
                throw th;
            }
        } else {
            Intent intent3 = intent;
        }
        Binder.restoreCallingIdentity(origId);
    }

    /* access modifiers changed from: package-private */
    public void updateServiceGroupLocked(IServiceConnection connection, int group, int importance) {
        ArrayList<ConnectionRecord> clist = this.mServiceConnections.get(connection.asBinder());
        if (clist != null) {
            for (int i = clist.size() - 1; i >= 0; i--) {
                ServiceRecord srec = clist.get(i).binding.service;
                if (!(srec == null || (srec.serviceInfo.flags & 2) == 0)) {
                    if (srec.app != null) {
                        if (group > 0) {
                            srec.app.connectionService = srec;
                            srec.app.connectionGroup = group;
                            srec.app.connectionImportance = importance;
                        } else {
                            srec.app.connectionService = null;
                            srec.app.connectionGroup = 0;
                            srec.app.connectionImportance = 0;
                        }
                    } else if (group > 0) {
                        srec.pendingConnectionGroup = group;
                        srec.pendingConnectionImportance = importance;
                    } else {
                        srec.pendingConnectionGroup = 0;
                        srec.pendingConnectionImportance = 0;
                    }
                }
            }
            return;
        }
        throw new IllegalArgumentException("Could not find connection for " + connection.asBinder());
    }

    /* access modifiers changed from: package-private */
    public boolean unbindServiceLocked(IServiceConnection connection) {
        IBinder binder = connection.asBinder();
        ArrayList<ConnectionRecord> clist = this.mServiceConnections.get(binder);
        if (clist == null) {
            Slog.w("ActivityManager", "Unbind failed: could not find connection for " + connection.asBinder());
            return false;
        }
        long origId = Binder.clearCallingIdentity();
        while (true) {
            try {
                boolean z = true;
                if (clist.size() > 0) {
                    ConnectionRecord r = clist.get(0);
                    ServiceData sData = new ServiceData();
                    sData.packageName = r.binding.service.packageName;
                    sData.processName = r.binding.service.shortInstanceName;
                    sData.lastActivity = (double) r.binding.service.lastActivity;
                    if (r.binding.service.app != null) {
                        sData.pid = r.binding.service.app.pid;
                        sData.serviceB = r.binding.service.app.serviceb;
                    } else {
                        sData.pid = -1;
                        sData.serviceB = false;
                    }
                    ClientData cData = new ClientData();
                    cData.processName = r.binding.client.processName;
                    cData.pid = r.binding.client.pid;
                    if (getServicetrackerInstance()) {
                        this.mServicetracker.unbindService(sData, cData);
                    }
                    removeConnectionLocked(r, (ProcessRecord) null, (ActivityServiceConnectionsHolder) null);
                    if (clist.size() > 0 && clist.get(0) == r) {
                        Slog.wtf("ActivityManager", "Connection " + r + " not removed for binder " + binder);
                        clist.remove(0);
                    }
                    if (r.binding.service.app != null) {
                        if (r.binding.service.app.whitelistManager) {
                            updateWhitelistManagerLocked(r.binding.service.app);
                        }
                        if ((r.flags & 134217728) != 0) {
                            r.binding.service.app.treatLikeActivity = true;
                            ActivityManagerService activityManagerService = this.mAm;
                            ProcessRecord processRecord = r.binding.service.app;
                            if (!r.binding.service.app.hasClientActivities()) {
                                if (!r.binding.service.app.treatLikeActivity) {
                                    z = false;
                                }
                            }
                            activityManagerService.updateLruProcessLocked(processRecord, z, (ProcessRecord) null);
                        }
                    }
                } else {
                    this.mAm.updateOomAdjLocked("updateOomAdj_unbindService");
                    Binder.restoreCallingIdentity(origId);
                    return true;
                }
            } catch (RemoteException e) {
                Slog.e("ActivityManager", "Failed to send unbind details to servicetracker HAL", e);
                this.mServicetracker = null;
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(origId);
                throw th;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void unbindFinishedLocked(ServiceRecord r, Intent intent, boolean doRebind) {
        long origId = Binder.clearCallingIdentity();
        if (r != null) {
            try {
                IntentBindRecord b = r.bindings.get(new Intent.FilterComparison(intent));
                boolean inDestroying = this.mDestroyingServices.contains(r);
                if (b != null) {
                    if (b.apps.size() <= 0 || inDestroying) {
                        b.doRebind = true;
                    } else {
                        boolean inFg = false;
                        int i = b.apps.size() - 1;
                        while (true) {
                            if (i >= 0) {
                                ProcessRecord client = b.apps.valueAt(i).client;
                                if (client != null && client.setSchedGroup != 0) {
                                    inFg = true;
                                    break;
                                }
                                i--;
                            }
                        }
                        try {
                            requestServiceBindingLocked(r, b, inFg, true);
                            break;
                        } catch (TransactionTooLargeException e) {
                        }
                    }
                }
                serviceDoneExecutingLocked(r, inDestroying, false);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(origId);
                throw th;
            }
        }
        Binder.restoreCallingIdentity(origId);
    }

    private final ServiceRecord findServiceLocked(ComponentName name, IBinder token, int userId) {
        ServiceRecord r = getServiceByNameLocked(name, userId);
        if (r == token) {
            return r;
        }
        return null;
    }

    private final class ServiceLookupResult {
        final String permission;
        final ServiceRecord record;

        ServiceLookupResult(ServiceRecord _record, String _permission) {
            this.record = _record;
            this.permission = _permission;
        }
    }

    private class ServiceRestarter implements Runnable {
        private ServiceRecord mService;

        private ServiceRestarter() {
        }

        /* access modifiers changed from: package-private */
        public void setService(ServiceRecord service) {
            this.mService = service;
        }

        public void run() {
            synchronized (ActiveServices.this.mAm) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    ActiveServices.this.performServiceRestartLocked(this.mService);
                } catch (Throwable th) {
                    while (true) {
                        ActivityManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 30 */
    /* JADX WARNING: Code restructure failed: missing block: B:100:0x02f8, code lost:
        r9 = r1.mPendingServices.get(r0);
        r18 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:101:0x030c, code lost:
        if (r9.serviceInfo.applicationInfo.uid != r2.applicationInfo.uid) goto L_0x031b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:103:0x0314, code lost:
        if (r9.instanceName.equals(r4) == false) goto L_0x031b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:104:0x0316, code lost:
        r1.mPendingServices.remove(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:105:0x031b, code lost:
        r0 = r0 - 1;
        r3 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:106:0x0320, code lost:
        r18 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:116:0x032f, code lost:
        r11 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:98:?, code lost:
        r14 = new com.android.server.am.ServiceRecord(r1.mAm, r20, r3, r4, r23, r24, r5, r2, r39, r6);
        r6.setService(r14);
        r12.mServicesByInstanceName.put(r4, r14);
        r12.mServicesByIntent.put(r5, r14);
        r0 = r1.mPendingServices.size() - 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:99:0x02f6, code lost:
        if (r0 < 0) goto L_0x0320;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.android.server.am.ActiveServices.ServiceLookupResult retrieveServiceLocked(android.content.Intent r31, java.lang.String r32, java.lang.String r33, java.lang.String r34, int r35, int r36, int r37, boolean r38, boolean r39, boolean r40, boolean r41) {
        /*
            r30 = this;
            r1 = r30
            r9 = r31
            r10 = r32
            r8 = r34
            r7 = r35
            r6 = r36
            r0 = 0
            com.android.server.am.ActivityManagerService r2 = r1.mAm
            com.android.server.am.UserController r11 = r2.mUserController
            r15 = 0
            r16 = 1
            java.lang.String r17 = "service"
            r12 = r35
            r13 = r36
            r14 = r37
            r18 = r34
            int r11 = r11.handleIncomingUser(r12, r13, r14, r15, r16, r17, r18)
            com.android.server.am.ActiveServices$ServiceMap r12 = r1.getServiceMapLocked(r11)
            if (r10 != 0) goto L_0x002f
            android.content.ComponentName r2 = r31.getComponent()
            r13 = r2
            goto L_0x0058
        L_0x002f:
            android.content.ComponentName r2 = r31.getComponent()
            if (r2 == 0) goto L_0x04cf
            android.content.ComponentName r3 = new android.content.ComponentName
            java.lang.String r4 = r2.getPackageName()
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r13 = r2.getClassName()
            r5.append(r13)
            java.lang.String r13 = ":"
            r5.append(r13)
            r5.append(r10)
            java.lang.String r5 = r5.toString()
            r3.<init>(r4, r5)
            r2 = r3
            r13 = r2
        L_0x0058:
            if (r13 == 0) goto L_0x0063
            android.util.ArrayMap<android.content.ComponentName, com.android.server.am.ServiceRecord> r2 = r12.mServicesByInstanceName
            java.lang.Object r2 = r2.get(r13)
            r0 = r2
            com.android.server.am.ServiceRecord r0 = (com.android.server.am.ServiceRecord) r0
        L_0x0063:
            if (r0 != 0) goto L_0x0077
            if (r40 != 0) goto L_0x0077
            if (r10 != 0) goto L_0x0077
            android.content.Intent$FilterComparison r2 = new android.content.Intent$FilterComparison
            r2.<init>(r9)
            android.util.ArrayMap<android.content.Intent$FilterComparison, com.android.server.am.ServiceRecord> r3 = r12.mServicesByIntent
            java.lang.Object r3 = r3.get(r2)
            r0 = r3
            com.android.server.am.ServiceRecord r0 = (com.android.server.am.ServiceRecord) r0
        L_0x0077:
            if (r0 == 0) goto L_0x008c
            android.content.pm.ServiceInfo r2 = r0.serviceInfo
            int r2 = r2.flags
            r2 = r2 & 4
            if (r2 == 0) goto L_0x008c
            java.lang.String r2 = r0.packageName
            boolean r2 = r8.equals(r2)
            if (r2 != 0) goto L_0x008c
            r0 = 0
            r14 = r0
            goto L_0x008d
        L_0x008c:
            r14 = r0
        L_0x008d:
            r15 = 0
            if (r14 != 0) goto L_0x0364
            r0 = 268436480(0x10000400, float:2.524663E-29)
            if (r41 == 0) goto L_0x009b
            r2 = 8388608(0x800000, float:1.17549435E-38)
            r0 = r0 | r2
            r16 = r0
            goto L_0x009d
        L_0x009b:
            r16 = r0
        L_0x009d:
            com.android.server.am.ActivityManagerService r0 = r1.mAm     // Catch:{ RemoteException -> 0x0361 }
            android.content.pm.PackageManagerInternal r2 = r0.getPackageManagerInternalLocked()     // Catch:{ RemoteException -> 0x0361 }
            r3 = r31
            r4 = r33
            r5 = r16
            r6 = r11
            r7 = r36
            android.content.pm.ResolveInfo r0 = r2.resolveService(r3, r4, r5, r6, r7)     // Catch:{ RemoteException -> 0x035d }
            r2 = r0
            if (r2 == 0) goto L_0x00b6
            android.content.pm.ServiceInfo r0 = r2.serviceInfo     // Catch:{ RemoteException -> 0x035d }
            goto L_0x00b7
        L_0x00b6:
            r0 = r15
        L_0x00b7:
            if (r0 != 0) goto L_0x00dd
            java.lang.String r3 = "ActivityManager"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x035d }
            r4.<init>()     // Catch:{ RemoteException -> 0x035d }
            java.lang.String r5 = "Unable to start service "
            r4.append(r5)     // Catch:{ RemoteException -> 0x035d }
            r4.append(r9)     // Catch:{ RemoteException -> 0x035d }
            java.lang.String r5 = " U="
            r4.append(r5)     // Catch:{ RemoteException -> 0x035d }
            r4.append(r11)     // Catch:{ RemoteException -> 0x035d }
            java.lang.String r5 = ": not found"
            r4.append(r5)     // Catch:{ RemoteException -> 0x035d }
            java.lang.String r4 = r4.toString()     // Catch:{ RemoteException -> 0x035d }
            android.util.Slog.w(r3, r4)     // Catch:{ RemoteException -> 0x035d }
            return r15
        L_0x00dd:
            if (r10 == 0) goto L_0x010c
            int r3 = r0.flags     // Catch:{ RemoteException -> 0x035d }
            r3 = r3 & 2
            if (r3 == 0) goto L_0x00e6
            goto L_0x010c
        L_0x00e6:
            java.lang.IllegalArgumentException r3 = new java.lang.IllegalArgumentException     // Catch:{ RemoteException -> 0x035d }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x035d }
            r4.<init>()     // Catch:{ RemoteException -> 0x035d }
            java.lang.String r5 = "Can't use instance name '"
            r4.append(r5)     // Catch:{ RemoteException -> 0x035d }
            r4.append(r10)     // Catch:{ RemoteException -> 0x035d }
            java.lang.String r5 = "' with non-isolated service '"
            r4.append(r5)     // Catch:{ RemoteException -> 0x035d }
            java.lang.String r5 = r0.name     // Catch:{ RemoteException -> 0x035d }
            r4.append(r5)     // Catch:{ RemoteException -> 0x035d }
            java.lang.String r5 = "'"
            r4.append(r5)     // Catch:{ RemoteException -> 0x035d }
            java.lang.String r4 = r4.toString()     // Catch:{ RemoteException -> 0x035d }
            r3.<init>(r4)     // Catch:{ RemoteException -> 0x035d }
            throw r3     // Catch:{ RemoteException -> 0x035d }
        L_0x010c:
            android.content.ComponentName r3 = new android.content.ComponentName     // Catch:{ RemoteException -> 0x035d }
            android.content.pm.ApplicationInfo r4 = r0.applicationInfo     // Catch:{ RemoteException -> 0x035d }
            java.lang.String r4 = r4.packageName     // Catch:{ RemoteException -> 0x035d }
            java.lang.String r5 = r0.name     // Catch:{ RemoteException -> 0x035d }
            r3.<init>(r4, r5)     // Catch:{ RemoteException -> 0x035d }
            if (r13 == 0) goto L_0x011b
            r4 = r13
            goto L_0x011c
        L_0x011b:
            r4 = r3
        L_0x011c:
            com.android.server.am.ActivityManagerService r5 = r1.mAm     // Catch:{ RemoteException -> 0x035d }
            java.lang.String r6 = r4.getPackageName()     // Catch:{ RemoteException -> 0x035d }
            android.content.pm.ApplicationInfo r7 = r0.applicationInfo     // Catch:{ RemoteException -> 0x035d }
            int r7 = r7.uid     // Catch:{ RemoteException -> 0x035d }
            r15 = r36
            boolean r5 = r5.validateAssociationAllowedLocked(r8, r15, r6, r7)     // Catch:{ RemoteException -> 0x035b }
            if (r5 != 0) goto L_0x016a
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x035b }
            r5.<init>()     // Catch:{ RemoteException -> 0x035b }
            java.lang.String r6 = "association not allowed between packages "
            r5.append(r6)     // Catch:{ RemoteException -> 0x035b }
            r5.append(r8)     // Catch:{ RemoteException -> 0x035b }
            java.lang.String r6 = " and "
            r5.append(r6)     // Catch:{ RemoteException -> 0x035b }
            java.lang.String r6 = r4.getPackageName()     // Catch:{ RemoteException -> 0x035b }
            r5.append(r6)     // Catch:{ RemoteException -> 0x035b }
            java.lang.String r5 = r5.toString()     // Catch:{ RemoteException -> 0x035b }
            java.lang.String r6 = "ActivityManager"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x035b }
            r7.<init>()     // Catch:{ RemoteException -> 0x035b }
            r17 = r2
            java.lang.String r2 = "Service lookup failed: "
            r7.append(r2)     // Catch:{ RemoteException -> 0x035b }
            r7.append(r5)     // Catch:{ RemoteException -> 0x035b }
            java.lang.String r2 = r7.toString()     // Catch:{ RemoteException -> 0x035b }
            android.util.Slog.w(r6, r2)     // Catch:{ RemoteException -> 0x035b }
            com.android.server.am.ActiveServices$ServiceLookupResult r2 = new com.android.server.am.ActiveServices$ServiceLookupResult     // Catch:{ RemoteException -> 0x035b }
            r6 = 0
            r2.<init>(r6, r5)     // Catch:{ RemoteException -> 0x035b }
            return r2
        L_0x016a:
            r17 = r2
            android.content.pm.ApplicationInfo r2 = r0.applicationInfo     // Catch:{ RemoteException -> 0x035b }
            java.lang.String r2 = r2.packageName     // Catch:{ RemoteException -> 0x035b }
            r23 = r2
            android.content.pm.ApplicationInfo r2 = r0.applicationInfo     // Catch:{ RemoteException -> 0x035b }
            int r2 = r2.uid     // Catch:{ RemoteException -> 0x035b }
            r24 = r2
            int r2 = r0.flags     // Catch:{ RemoteException -> 0x035b }
            r2 = r2 & 4
            if (r2 == 0) goto L_0x0254
            if (r40 == 0) goto L_0x023d
            boolean r2 = r0.exported     // Catch:{ RemoteException -> 0x035b }
            if (r2 == 0) goto L_0x0221
            int r2 = r0.flags     // Catch:{ RemoteException -> 0x035b }
            r2 = r2 & 2
            if (r2 == 0) goto L_0x0205
            android.content.pm.IPackageManager r2 = android.app.AppGlobals.getPackageManager()     // Catch:{ RemoteException -> 0x035b }
            r5 = 1024(0x400, float:1.435E-42)
            android.content.pm.ApplicationInfo r2 = r2.getApplicationInfo(r8, r5, r11)     // Catch:{ RemoteException -> 0x035b }
            if (r2 == 0) goto L_0x01ee
            android.content.pm.ServiceInfo r5 = new android.content.pm.ServiceInfo     // Catch:{ RemoteException -> 0x035b }
            r5.<init>(r0)     // Catch:{ RemoteException -> 0x035b }
            r0 = r5
            android.content.pm.ApplicationInfo r5 = new android.content.pm.ApplicationInfo     // Catch:{ RemoteException -> 0x035b }
            android.content.pm.ApplicationInfo r6 = r0.applicationInfo     // Catch:{ RemoteException -> 0x035b }
            r5.<init>(r6)     // Catch:{ RemoteException -> 0x035b }
            r0.applicationInfo = r5     // Catch:{ RemoteException -> 0x035b }
            android.content.pm.ApplicationInfo r5 = r0.applicationInfo     // Catch:{ RemoteException -> 0x035b }
            java.lang.String r6 = r2.packageName     // Catch:{ RemoteException -> 0x035b }
            r5.packageName = r6     // Catch:{ RemoteException -> 0x035b }
            android.content.pm.ApplicationInfo r5 = r0.applicationInfo     // Catch:{ RemoteException -> 0x035b }
            int r6 = r2.uid     // Catch:{ RemoteException -> 0x035b }
            r5.uid = r6     // Catch:{ RemoteException -> 0x035b }
            android.content.ComponentName r5 = new android.content.ComponentName     // Catch:{ RemoteException -> 0x035b }
            java.lang.String r6 = r2.packageName     // Catch:{ RemoteException -> 0x035b }
            java.lang.String r7 = r4.getClassName()     // Catch:{ RemoteException -> 0x035b }
            r5.<init>(r6, r7)     // Catch:{ RemoteException -> 0x035b }
            r4 = r5
            android.content.ComponentName r5 = new android.content.ComponentName     // Catch:{ RemoteException -> 0x035b }
            java.lang.String r6 = r2.packageName     // Catch:{ RemoteException -> 0x035b }
            if (r10 != 0) goto L_0x01ca
            java.lang.String r7 = r3.getClassName()     // Catch:{ RemoteException -> 0x035b }
            r18 = r0
            goto L_0x01e4
        L_0x01ca:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x035b }
            r7.<init>()     // Catch:{ RemoteException -> 0x035b }
            r18 = r0
            java.lang.String r0 = r3.getClassName()     // Catch:{ RemoteException -> 0x035b }
            r7.append(r0)     // Catch:{ RemoteException -> 0x035b }
            java.lang.String r0 = ":"
            r7.append(r0)     // Catch:{ RemoteException -> 0x035b }
            r7.append(r10)     // Catch:{ RemoteException -> 0x035b }
            java.lang.String r7 = r7.toString()     // Catch:{ RemoteException -> 0x035b }
        L_0x01e4:
            r5.<init>(r6, r7)     // Catch:{ RemoteException -> 0x035b }
            r3 = r5
            r9.setComponent(r4)     // Catch:{ RemoteException -> 0x035b }
            r0 = r18
            goto L_0x0256
        L_0x01ee:
            java.lang.SecurityException r5 = new java.lang.SecurityException     // Catch:{ RemoteException -> 0x035b }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x035b }
            r6.<init>()     // Catch:{ RemoteException -> 0x035b }
            java.lang.String r7 = "BIND_EXTERNAL_SERVICE failed, could not resolve client package "
            r6.append(r7)     // Catch:{ RemoteException -> 0x035b }
            r6.append(r8)     // Catch:{ RemoteException -> 0x035b }
            java.lang.String r6 = r6.toString()     // Catch:{ RemoteException -> 0x035b }
            r5.<init>(r6)     // Catch:{ RemoteException -> 0x035b }
            throw r5     // Catch:{ RemoteException -> 0x035b }
        L_0x0205:
            java.lang.SecurityException r2 = new java.lang.SecurityException     // Catch:{ RemoteException -> 0x035b }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x035b }
            r5.<init>()     // Catch:{ RemoteException -> 0x035b }
            java.lang.String r6 = "BIND_EXTERNAL_SERVICE failed, "
            r5.append(r6)     // Catch:{ RemoteException -> 0x035b }
            r5.append(r3)     // Catch:{ RemoteException -> 0x035b }
            java.lang.String r6 = " is not an isolatedProcess"
            r5.append(r6)     // Catch:{ RemoteException -> 0x035b }
            java.lang.String r5 = r5.toString()     // Catch:{ RemoteException -> 0x035b }
            r2.<init>(r5)     // Catch:{ RemoteException -> 0x035b }
            throw r2     // Catch:{ RemoteException -> 0x035b }
        L_0x0221:
            java.lang.SecurityException r2 = new java.lang.SecurityException     // Catch:{ RemoteException -> 0x035b }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x035b }
            r5.<init>()     // Catch:{ RemoteException -> 0x035b }
            java.lang.String r6 = "BIND_EXTERNAL_SERVICE failed, "
            r5.append(r6)     // Catch:{ RemoteException -> 0x035b }
            r5.append(r3)     // Catch:{ RemoteException -> 0x035b }
            java.lang.String r6 = " is not exported"
            r5.append(r6)     // Catch:{ RemoteException -> 0x035b }
            java.lang.String r5 = r5.toString()     // Catch:{ RemoteException -> 0x035b }
            r2.<init>(r5)     // Catch:{ RemoteException -> 0x035b }
            throw r2     // Catch:{ RemoteException -> 0x035b }
        L_0x023d:
            java.lang.SecurityException r2 = new java.lang.SecurityException     // Catch:{ RemoteException -> 0x035b }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x035b }
            r5.<init>()     // Catch:{ RemoteException -> 0x035b }
            java.lang.String r6 = "BIND_EXTERNAL_SERVICE required for "
            r5.append(r6)     // Catch:{ RemoteException -> 0x035b }
            r5.append(r4)     // Catch:{ RemoteException -> 0x035b }
            java.lang.String r5 = r5.toString()     // Catch:{ RemoteException -> 0x035b }
            r2.<init>(r5)     // Catch:{ RemoteException -> 0x035b }
            throw r2     // Catch:{ RemoteException -> 0x035b }
        L_0x0254:
            if (r40 != 0) goto L_0x033f
        L_0x0256:
            if (r11 <= 0) goto L_0x028d
            com.android.server.am.ActivityManagerService r2 = r1.mAm     // Catch:{ RemoteException -> 0x035b }
            java.lang.String r5 = r0.processName     // Catch:{ RemoteException -> 0x035b }
            android.content.pm.ApplicationInfo r6 = r0.applicationInfo     // Catch:{ RemoteException -> 0x035b }
            java.lang.String r7 = r0.name     // Catch:{ RemoteException -> 0x035b }
            int r9 = r0.flags     // Catch:{ RemoteException -> 0x035b }
            boolean r2 = r2.isSingleton(r5, r6, r7, r9)     // Catch:{ RemoteException -> 0x035b }
            if (r2 == 0) goto L_0x027b
            com.android.server.am.ActivityManagerService r2 = r1.mAm     // Catch:{ RemoteException -> 0x035b }
            android.content.pm.ApplicationInfo r5 = r0.applicationInfo     // Catch:{ RemoteException -> 0x035b }
            int r5 = r5.uid     // Catch:{ RemoteException -> 0x035b }
            boolean r2 = r2.isValidSingletonCall(r15, r5)     // Catch:{ RemoteException -> 0x035b }
            if (r2 == 0) goto L_0x027b
            r11 = 0
            r2 = 0
            com.android.server.am.ActiveServices$ServiceMap r2 = r1.getServiceMapLocked(r2)     // Catch:{ RemoteException -> 0x035b }
            r12 = r2
        L_0x027b:
            android.content.pm.ServiceInfo r2 = new android.content.pm.ServiceInfo     // Catch:{ RemoteException -> 0x035b }
            r2.<init>(r0)     // Catch:{ RemoteException -> 0x035b }
            r0 = r2
            com.android.server.am.ActivityManagerService r2 = r1.mAm     // Catch:{ RemoteException -> 0x035b }
            android.content.pm.ApplicationInfo r5 = r0.applicationInfo     // Catch:{ RemoteException -> 0x035b }
            android.content.pm.ApplicationInfo r2 = r2.getAppInfoForUser(r5, r11)     // Catch:{ RemoteException -> 0x035b }
            r0.applicationInfo = r2     // Catch:{ RemoteException -> 0x035b }
            r2 = r0
            goto L_0x028e
        L_0x028d:
            r2 = r0
        L_0x028e:
            android.util.ArrayMap<android.content.ComponentName, com.android.server.am.ServiceRecord> r0 = r12.mServicesByInstanceName     // Catch:{ RemoteException -> 0x033b }
            java.lang.Object r0 = r0.get(r4)     // Catch:{ RemoteException -> 0x033b }
            com.android.server.am.ServiceRecord r0 = (com.android.server.am.ServiceRecord) r0     // Catch:{ RemoteException -> 0x033b }
            r14 = r0
            if (r14 != 0) goto L_0x0334
            if (r38 == 0) goto L_0x0334
            android.content.Intent$FilterComparison r0 = new android.content.Intent$FilterComparison     // Catch:{ RemoteException -> 0x033b }
            android.content.Intent r5 = r31.cloneFilter()     // Catch:{ RemoteException -> 0x033b }
            r0.<init>(r5)     // Catch:{ RemoteException -> 0x033b }
            r5 = r0
            com.android.server.am.ActiveServices$ServiceRestarter r0 = new com.android.server.am.ActiveServices$ServiceRestarter     // Catch:{ RemoteException -> 0x033b }
            r6 = 0
            r0.<init>()     // Catch:{ RemoteException -> 0x033b }
            r6 = r0
            com.android.server.am.ActivityManagerService r0 = r1.mAm     // Catch:{ RemoteException -> 0x033b }
            com.android.server.am.BatteryStatsService r0 = r0.mBatteryStatsService     // Catch:{ RemoteException -> 0x033b }
            com.android.internal.os.BatteryStatsImpl r0 = r0.getActiveStatistics()     // Catch:{ RemoteException -> 0x033b }
            r7 = r0
            monitor-enter(r7)     // Catch:{ RemoteException -> 0x033b }
            android.content.pm.ApplicationInfo r0 = r2.applicationInfo     // Catch:{ all -> 0x0327 }
            int r0 = r0.uid     // Catch:{ all -> 0x0327 }
            java.lang.String r9 = r4.getPackageName()     // Catch:{ all -> 0x0327 }
            r29 = r11
            java.lang.String r11 = r4.getClassName()     // Catch:{ all -> 0x0323 }
            com.android.internal.os.BatteryStatsImpl$Uid$Pkg$Serv r20 = r7.getServiceStatsLocked(r0, r9, r11)     // Catch:{ all -> 0x0323 }
            monitor-exit(r7)     // Catch:{ all -> 0x0323 }
            com.android.server.am.ServiceRecord r0 = new com.android.server.am.ServiceRecord     // Catch:{ RemoteException -> 0x032e }
            com.android.server.am.ActivityManagerService r9 = r1.mAm     // Catch:{ RemoteException -> 0x032e }
            r18 = r0
            r19 = r9
            r21 = r3
            r22 = r4
            r25 = r5
            r26 = r2
            r27 = r39
            r28 = r6
            r18.<init>(r19, r20, r21, r22, r23, r24, r25, r26, r27, r28)     // Catch:{ RemoteException -> 0x032e }
            r14 = r0
            r6.setService(r14)     // Catch:{ RemoteException -> 0x032e }
            android.util.ArrayMap<android.content.ComponentName, com.android.server.am.ServiceRecord> r0 = r12.mServicesByInstanceName     // Catch:{ RemoteException -> 0x032e }
            r0.put(r4, r14)     // Catch:{ RemoteException -> 0x032e }
            android.util.ArrayMap<android.content.Intent$FilterComparison, com.android.server.am.ServiceRecord> r0 = r12.mServicesByIntent     // Catch:{ RemoteException -> 0x032e }
            r0.put(r5, r14)     // Catch:{ RemoteException -> 0x032e }
            java.util.ArrayList<com.android.server.am.ServiceRecord> r0 = r1.mPendingServices     // Catch:{ RemoteException -> 0x032e }
            int r0 = r0.size()     // Catch:{ RemoteException -> 0x032e }
            int r0 = r0 + -1
        L_0x02f6:
            if (r0 < 0) goto L_0x0320
            java.util.ArrayList<com.android.server.am.ServiceRecord> r9 = r1.mPendingServices     // Catch:{ RemoteException -> 0x032e }
            java.lang.Object r9 = r9.get(r0)     // Catch:{ RemoteException -> 0x032e }
            com.android.server.am.ServiceRecord r9 = (com.android.server.am.ServiceRecord) r9     // Catch:{ RemoteException -> 0x032e }
            android.content.pm.ServiceInfo r11 = r9.serviceInfo     // Catch:{ RemoteException -> 0x032e }
            android.content.pm.ApplicationInfo r11 = r11.applicationInfo     // Catch:{ RemoteException -> 0x032e }
            int r11 = r11.uid     // Catch:{ RemoteException -> 0x032e }
            r18 = r3
            android.content.pm.ApplicationInfo r3 = r2.applicationInfo     // Catch:{ RemoteException -> 0x032e }
            int r3 = r3.uid     // Catch:{ RemoteException -> 0x032e }
            if (r11 != r3) goto L_0x031b
            android.content.ComponentName r3 = r9.instanceName     // Catch:{ RemoteException -> 0x032e }
            boolean r3 = r3.equals(r4)     // Catch:{ RemoteException -> 0x032e }
            if (r3 == 0) goto L_0x031b
            java.util.ArrayList<com.android.server.am.ServiceRecord> r3 = r1.mPendingServices     // Catch:{ RemoteException -> 0x032e }
            r3.remove(r0)     // Catch:{ RemoteException -> 0x032e }
        L_0x031b:
            int r0 = r0 + -1
            r3 = r18
            goto L_0x02f6
        L_0x0320:
            r18 = r3
            goto L_0x0338
        L_0x0323:
            r0 = move-exception
            r18 = r3
            goto L_0x032c
        L_0x0327:
            r0 = move-exception
            r18 = r3
            r29 = r11
        L_0x032c:
            monitor-exit(r7)     // Catch:{ all -> 0x0332 }
            throw r0     // Catch:{ RemoteException -> 0x032e }
        L_0x032e:
            r0 = move-exception
            r11 = r29
            goto L_0x0365
        L_0x0332:
            r0 = move-exception
            goto L_0x032c
        L_0x0334:
            r18 = r3
            r29 = r11
        L_0x0338:
            r11 = r29
            goto L_0x0365
        L_0x033b:
            r0 = move-exception
            r29 = r11
            goto L_0x0365
        L_0x033f:
            java.lang.SecurityException r2 = new java.lang.SecurityException     // Catch:{ RemoteException -> 0x035b }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x035b }
            r5.<init>()     // Catch:{ RemoteException -> 0x035b }
            java.lang.String r6 = "BIND_EXTERNAL_SERVICE failed, "
            r5.append(r6)     // Catch:{ RemoteException -> 0x035b }
            r5.append(r4)     // Catch:{ RemoteException -> 0x035b }
            java.lang.String r6 = " is not an externalService"
            r5.append(r6)     // Catch:{ RemoteException -> 0x035b }
            java.lang.String r5 = r5.toString()     // Catch:{ RemoteException -> 0x035b }
            r2.<init>(r5)     // Catch:{ RemoteException -> 0x035b }
            throw r2     // Catch:{ RemoteException -> 0x035b }
        L_0x035b:
            r0 = move-exception
            goto L_0x0365
        L_0x035d:
            r0 = move-exception
            r15 = r36
            goto L_0x0365
        L_0x0361:
            r0 = move-exception
            r15 = r6
            goto L_0x0365
        L_0x0364:
            r15 = r6
        L_0x0365:
            if (r14 == 0) goto L_0x04ca
            r14.callerPackage = r8
            com.android.server.am.ActivityManagerService r0 = r1.mAm
            java.lang.String r2 = r14.packageName
            android.content.pm.ApplicationInfo r3 = r14.appInfo
            int r3 = r3.uid
            boolean r0 = r0.validateAssociationAllowedLocked(r8, r15, r2, r3)
            if (r0 != 0) goto L_0x03af
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "association not allowed between packages "
            r0.append(r2)
            r0.append(r8)
            java.lang.String r2 = " and "
            r0.append(r2)
            java.lang.String r2 = r14.packageName
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Service lookup failed: "
            r2.append(r3)
            r2.append(r0)
            java.lang.String r2 = r2.toString()
            java.lang.String r3 = "ActivityManager"
            android.util.Slog.w(r3, r2)
            com.android.server.am.ActiveServices$ServiceLookupResult r2 = new com.android.server.am.ActiveServices$ServiceLookupResult
            r3 = 0
            r2.<init>(r3, r0)
            return r2
        L_0x03af:
            com.android.server.am.ActivityManagerService r0 = r1.mAm
            com.android.server.firewall.IntentFirewall r2 = r0.mIntentFirewall
            android.content.ComponentName r3 = r14.name
            android.content.pm.ApplicationInfo r0 = r14.appInfo
            r4 = r31
            r5 = r36
            r6 = r35
            r7 = r33
            r9 = r8
            r8 = r0
            boolean r0 = r2.checkService(r3, r4, r5, r6, r7, r8)
            if (r0 != 0) goto L_0x03d0
            com.android.server.am.ActiveServices$ServiceLookupResult r0 = new com.android.server.am.ActiveServices$ServiceLookupResult
            java.lang.String r2 = "blocked by firewall"
            r3 = 0
            r0.<init>(r3, r2)
            return r0
        L_0x03d0:
            com.android.server.am.ActivityManagerService r0 = r1.mAm
            java.lang.String r0 = r14.permission
            android.content.pm.ApplicationInfo r2 = r14.appInfo
            int r2 = r2.uid
            boolean r3 = r14.exported
            r4 = r35
            int r0 = com.android.server.am.ActivityManagerService.checkComponentPermission(r0, r4, r15, r2, r3)
            if (r0 == 0) goto L_0x0472
            boolean r0 = r14.exported
            if (r0 != 0) goto L_0x0437
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "Permission Denial: Accessing service "
            r0.append(r2)
            java.lang.String r2 = r14.shortInstanceName
            r0.append(r2)
            java.lang.String r2 = " from pid="
            r0.append(r2)
            r0.append(r4)
            java.lang.String r2 = ", uid="
            r0.append(r2)
            r0.append(r15)
            java.lang.String r2 = " that is not exported from uid "
            r0.append(r2)
            android.content.pm.ApplicationInfo r2 = r14.appInfo
            int r2 = r2.uid
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            java.lang.String r2 = "ActivityManager"
            android.util.Slog.w(r2, r0)
            com.android.server.am.ActiveServices$ServiceLookupResult r0 = new com.android.server.am.ActiveServices$ServiceLookupResult
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "not exported from uid "
            r2.append(r3)
            android.content.pm.ApplicationInfo r3 = r14.appInfo
            int r3 = r3.uid
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r3 = 0
            r0.<init>(r3, r2)
            return r0
        L_0x0437:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "Permission Denial: Accessing service "
            r0.append(r2)
            java.lang.String r2 = r14.shortInstanceName
            r0.append(r2)
            java.lang.String r2 = " from pid="
            r0.append(r2)
            r0.append(r4)
            java.lang.String r2 = ", uid="
            r0.append(r2)
            r0.append(r15)
            java.lang.String r2 = " requires "
            r0.append(r2)
            java.lang.String r2 = r14.permission
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            java.lang.String r2 = "ActivityManager"
            android.util.Slog.w(r2, r0)
            com.android.server.am.ActiveServices$ServiceLookupResult r0 = new com.android.server.am.ActiveServices$ServiceLookupResult
            java.lang.String r2 = r14.permission
            r3 = 0
            r0.<init>(r3, r2)
            return r0
        L_0x0472:
            java.lang.String r0 = r14.permission
            if (r0 == 0) goto L_0x04c3
            if (r9 == 0) goto L_0x04c3
            java.lang.String r0 = r14.permission
            int r0 = android.app.AppOpsManager.permissionToOpCode(r0)
            r2 = -1
            if (r0 == r2) goto L_0x04c1
            com.android.server.am.ActivityManagerService r2 = r1.mAm
            com.android.server.appop.AppOpsService r2 = r2.mAppOpsService
            int r2 = r2.checkOperation(r0, r15, r9)
            if (r2 == 0) goto L_0x04c1
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Appop Denial: Accessing service "
            r2.append(r3)
            java.lang.String r3 = r14.shortInstanceName
            r2.append(r3)
            java.lang.String r3 = " from pid="
            r2.append(r3)
            r2.append(r4)
            java.lang.String r3 = ", uid="
            r2.append(r3)
            r2.append(r15)
            java.lang.String r3 = " requires appop "
            r2.append(r3)
            java.lang.String r3 = android.app.AppOpsManager.opToName(r0)
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            java.lang.String r3 = "ActivityManager"
            android.util.Slog.w(r3, r2)
            r2 = 0
            return r2
        L_0x04c1:
            r2 = 0
            goto L_0x04c4
        L_0x04c3:
            r2 = 0
        L_0x04c4:
            com.android.server.am.ActiveServices$ServiceLookupResult r0 = new com.android.server.am.ActiveServices$ServiceLookupResult
            r0.<init>(r14, r2)
            return r0
        L_0x04ca:
            r4 = r35
            r9 = r8
            r2 = 0
            return r2
        L_0x04cf:
            r15 = r6
            r4 = r7
            r9 = r8
            java.lang.IllegalArgumentException r3 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "Can't use custom instance name '"
            r5.append(r6)
            r5.append(r10)
            java.lang.String r6 = "' without expicit component in Intent"
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            r3.<init>(r5)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ActiveServices.retrieveServiceLocked(android.content.Intent, java.lang.String, java.lang.String, java.lang.String, int, int, int, boolean, boolean, boolean, boolean):com.android.server.am.ActiveServices$ServiceLookupResult");
    }

    private final void bumpServiceExecutingLocked(ServiceRecord r, boolean fg, String why) {
        boolean timeoutNeeded = true;
        if (this.mAm.mBootPhase < 600 && r.app != null && r.app.pid == Process.myPid()) {
            Slog.w("ActivityManager", "Too early to start/bind service in system_server: Phase=" + this.mAm.mBootPhase + " " + r.getComponentName());
            timeoutNeeded = false;
        }
        long now = SystemClock.uptimeMillis();
        if (r.executeNesting == 0) {
            r.executeFg = fg;
            ServiceState stracker = r.getTracker();
            if (stracker != null) {
                stracker.setExecuting(true, this.mAm.mProcessStats.getMemFactorLocked(), now);
            }
            if (r.app != null) {
                r.app.executingServices.add(r);
                r.app.execServicesFg |= fg;
                if (timeoutNeeded && r.app.executingServices.size() == 1) {
                    scheduleServiceTimeoutLocked(r.app);
                }
            }
        } else if (r.app != null && fg && !r.app.execServicesFg) {
            r.app.execServicesFg = true;
            if (timeoutNeeded) {
                scheduleServiceTimeoutLocked(r.app);
            }
        }
        r.executeFg |= fg;
        r.executeNesting++;
        r.executingStart = now;
    }

    private final boolean requestServiceBindingLocked(ServiceRecord r, IntentBindRecord i, boolean execInFg, boolean rebind) throws TransactionTooLargeException {
        if (r.app == null || r.app.thread == null) {
            return false;
        }
        if ((!i.requested || rebind) && i.apps.size() > 0) {
            try {
                bumpServiceExecutingLocked(r, execInFg, "bind");
                r.app.forceProcessStateUpTo(11);
                r.app.thread.scheduleBindService(r, i.intent.getIntent(), rebind, r.app.getReportedProcState());
                if (!rebind) {
                    i.requested = true;
                }
                i.hasBound = true;
                i.doRebind = false;
            } catch (TransactionTooLargeException e) {
                boolean inDestroying = this.mDestroyingServices.contains(r);
                serviceDoneExecutingLocked(r, inDestroying, inDestroying);
                throw e;
            } catch (RemoteException e2) {
                boolean inDestroying2 = this.mDestroyingServices.contains(r);
                serviceDoneExecutingLocked(r, inDestroying2, inDestroying2);
                return false;
            }
        }
        return true;
    }

    /* JADX WARNING: Removed duplicated region for block: B:72:0x01ea  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final boolean scheduleServiceRestartLocked(com.android.server.am.ServiceRecord r26, boolean r27) {
        /*
            r25 = this;
            r0 = r25
            r1 = r26
            r2 = 1
            if (r27 == 0) goto L_0x0010
            com.android.server.am.ActivityManagerService r3 = r0.mAm
            boolean r3 = com.android.server.am.ActiveServicesInjector.canRestartServiceLocked(r1, r3)
            if (r3 != 0) goto L_0x0010
            return r2
        L_0x0010:
            r3 = 0
            com.android.server.am.ActivityManagerService r4 = r0.mAm
            com.android.server.wm.ActivityTaskManagerInternal r4 = r4.mAtmInternal
            boolean r4 = r4.isShuttingDown()
            java.lang.String r5 = "ActivityManager"
            r6 = 0
            if (r4 == 0) goto L_0x003a
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r4 = "Not scheduling restart of crashed service "
            r2.append(r4)
            java.lang.String r4 = r1.shortInstanceName
            r2.append(r4)
            java.lang.String r4 = " - system is shutting down"
            r2.append(r4)
            java.lang.String r2 = r2.toString()
            android.util.Slog.w(r5, r2)
            return r6
        L_0x003a:
            int r4 = r1.userId
            com.android.server.am.ActiveServices$ServiceMap r4 = r0.getServiceMapLocked(r4)
            android.util.ArrayMap<android.content.ComponentName, com.android.server.am.ServiceRecord> r7 = r4.mServicesByInstanceName
            android.content.ComponentName r8 = r1.instanceName
            java.lang.Object r7 = r7.get(r8)
            if (r7 == r1) goto L_0x0071
            android.util.ArrayMap<android.content.ComponentName, com.android.server.am.ServiceRecord> r2 = r4.mServicesByInstanceName
            android.content.ComponentName r7 = r1.instanceName
            java.lang.Object r2 = r2.get(r7)
            com.android.server.am.ServiceRecord r2 = (com.android.server.am.ServiceRecord) r2
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "Attempting to schedule restart of "
            r7.append(r8)
            r7.append(r1)
            java.lang.String r8 = " when found in map: "
            r7.append(r8)
            r7.append(r2)
            java.lang.String r7 = r7.toString()
            android.util.Slog.wtf(r5, r7)
            return r6
        L_0x0071:
            long r7 = android.os.SystemClock.uptimeMillis()
            android.content.pm.ServiceInfo r9 = r1.serviceInfo
            android.content.pm.ApplicationInfo r9 = r9.applicationInfo
            int r9 = r9.flags
            r9 = r9 & 8
            r10 = 3
            if (r9 != 0) goto L_0x01cd
            boolean r9 = com.android.server.am.ActiveServicesInjector.willRestartNow((com.android.server.am.ServiceRecord) r26)
            if (r9 != 0) goto L_0x01ca
            com.android.server.am.ActivityManagerService r9 = r0.mAm
            com.android.server.am.ActivityManagerConstants r9 = r9.mConstants
            long r13 = r9.SERVICE_RESTART_DURATION
            com.android.server.am.ActivityManagerService r9 = r0.mAm
            com.android.server.am.ActivityManagerConstants r9 = r9.mConstants
            long r11 = r9.SERVICE_RESET_RUN_DURATION
            java.util.ArrayList<com.android.server.am.ServiceRecord$StartItem> r9 = r1.deliveredStarts
            int r9 = r9.size()
            if (r9 <= 0) goto L_0x011c
            int r17 = r9 + -1
            r24 = r17
            r17 = r3
            r3 = r24
        L_0x00a2:
            if (r3 < 0) goto L_0x0114
            java.util.ArrayList<com.android.server.am.ServiceRecord$StartItem> r15 = r1.deliveredStarts
            java.lang.Object r15 = r15.get(r3)
            com.android.server.am.ServiceRecord$StartItem r15 = (com.android.server.am.ServiceRecord.StartItem) r15
            r15.removeUriPermissionsLocked()
            android.content.Intent r2 = r15.intent
            if (r2 != 0) goto L_0x00b6
            r22 = r7
            goto L_0x010c
        L_0x00b6:
            if (r27 == 0) goto L_0x00e8
            int r2 = r15.deliveryCount
            if (r2 >= r10) goto L_0x00c2
            int r2 = r15.doneExecutingCount
            r10 = 6
            if (r2 >= r10) goto L_0x00c2
            goto L_0x00e8
        L_0x00c2:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r10 = "Canceling start item "
            r2.append(r10)
            android.content.Intent r10 = r15.intent
            r2.append(r10)
            java.lang.String r10 = " in service "
            r2.append(r10)
            java.lang.String r10 = r1.shortInstanceName
            r2.append(r10)
            java.lang.String r2 = r2.toString()
            android.util.Slog.w(r5, r2)
            r2 = 1
            r17 = r2
            r22 = r7
            goto L_0x010c
        L_0x00e8:
            java.util.ArrayList<com.android.server.am.ServiceRecord$StartItem> r2 = r1.pendingStarts
            r2.add(r6, r15)
            long r20 = android.os.SystemClock.uptimeMillis()
            r22 = r7
            long r6 = r15.deliveredTime
            long r20 = r20 - r6
            r6 = 2
            long r20 = r20 * r6
            boolean r6 = SERVICE_RESCHEDULE
            int r6 = (r13 > r20 ? 1 : (r13 == r20 ? 0 : -1))
            if (r6 >= 0) goto L_0x0104
            r6 = r20
            r13 = r6
        L_0x0104:
            int r6 = (r11 > r20 ? 1 : (r11 == r20 ? 0 : -1))
            if (r6 >= 0) goto L_0x010b
            r6 = r20
            r11 = r6
        L_0x010b:
        L_0x010c:
            int r3 = r3 + -1
            r7 = r22
            r2 = 1
            r6 = 0
            r10 = 3
            goto L_0x00a2
        L_0x0114:
            r22 = r7
            java.util.ArrayList<com.android.server.am.ServiceRecord$StartItem> r3 = r1.deliveredStarts
            r3.clear()
            goto L_0x0120
        L_0x011c:
            r22 = r7
            r17 = r3
        L_0x0120:
            int r3 = r1.totalRestartCount
            r6 = 1
            int r3 = r3 + r6
            r1.totalRestartCount = r3
            boolean r3 = SERVICE_RESCHEDULE
            long r7 = r1.restartDelay
            r15 = 0
            int r3 = (r7 > r15 ? 1 : (r7 == r15 ? 0 : -1))
            if (r3 != 0) goto L_0x0138
            int r3 = r1.restartCount
            int r3 = r3 + r6
            r1.restartCount = r3
            r1.restartDelay = r13
            goto L_0x016a
        L_0x0138:
            int r3 = r1.crashCount
            if (r3 <= r6) goto L_0x014a
            com.android.server.am.ActivityManagerService r3 = r0.mAm
            com.android.server.am.ActivityManagerConstants r3 = r3.mConstants
            long r7 = r3.BOUND_SERVICE_CRASH_RESTART_DURATION
            int r3 = r1.crashCount
            int r3 = r3 - r6
            long r2 = (long) r3
            long r7 = r7 * r2
            r1.restartDelay = r7
            goto L_0x016a
        L_0x014a:
            long r2 = r1.restartTime
            long r2 = r2 + r11
            int r2 = (r22 > r2 ? 1 : (r22 == r2 ? 0 : -1))
            if (r2 <= 0) goto L_0x0156
            r1.restartCount = r6
            r1.restartDelay = r13
            goto L_0x016a
        L_0x0156:
            long r2 = r1.restartDelay
            com.android.server.am.ActivityManagerService r6 = r0.mAm
            com.android.server.am.ActivityManagerConstants r6 = r6.mConstants
            int r6 = r6.SERVICE_RESTART_DURATION_FACTOR
            long r6 = (long) r6
            long r2 = r2 * r6
            r1.restartDelay = r2
            long r2 = r1.restartDelay
            int r2 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1))
            if (r2 >= 0) goto L_0x016a
            r1.restartDelay = r13
        L_0x016a:
            long r2 = r1.restartDelay
            long r7 = r22 + r2
            r1.nextRestartTime = r7
            boolean r2 = SERVICE_RESCHEDULE
        L_0x0172:
            r2 = 0
            com.android.server.am.ActivityManagerService r3 = r0.mAm
            com.android.server.am.ActivityManagerConstants r3 = r3.mConstants
            long r6 = r3.SERVICE_MIN_RESTART_TIME_BETWEEN
            java.util.ArrayList<com.android.server.am.ServiceRecord> r3 = r0.mRestartingServices
            int r3 = r3.size()
            r8 = 1
            int r3 = r3 - r8
        L_0x0181:
            if (r3 < 0) goto L_0x01bb
            java.util.ArrayList<com.android.server.am.ServiceRecord> r8 = r0.mRestartingServices
            java.lang.Object r8 = r8.get(r3)
            com.android.server.am.ServiceRecord r8 = (com.android.server.am.ServiceRecord) r8
            if (r8 == r1) goto L_0x01b0
            r18 = r11
            long r10 = r1.nextRestartTime
            r20 = r13
            long r12 = r8.nextRestartTime
            long r12 = r12 - r6
            int r10 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r10 < 0) goto L_0x01b4
            long r10 = r1.nextRestartTime
            long r12 = r8.nextRestartTime
            long r12 = r12 + r6
            int r10 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r10 >= 0) goto L_0x01b4
            long r10 = r8.nextRestartTime
            long r10 = r10 + r6
            r1.nextRestartTime = r10
            long r10 = r1.nextRestartTime
            long r10 = r10 - r22
            r1.restartDelay = r10
            r2 = 1
            goto L_0x01bf
        L_0x01b0:
            r18 = r11
            r20 = r13
        L_0x01b4:
            int r3 = r3 + -1
            r11 = r18
            r13 = r20
            goto L_0x0181
        L_0x01bb:
            r18 = r11
            r20 = r13
        L_0x01bf:
            if (r2 != 0) goto L_0x01c5
            r6 = r22
            r2 = 0
            goto L_0x01e2
        L_0x01c5:
            r11 = r18
            r13 = r20
            goto L_0x0172
        L_0x01ca:
            r22 = r7
            goto L_0x01cf
        L_0x01cd:
            r22 = r7
        L_0x01cf:
            int r2 = r1.totalRestartCount
            r6 = 1
            int r2 = r2 + r6
            r1.totalRestartCount = r2
            r2 = 0
            r1.restartCount = r2
            r6 = 0
            r1.restartDelay = r6
            r6 = r22
            r1.nextRestartTime = r6
            r17 = r3
        L_0x01e2:
            java.util.ArrayList<com.android.server.am.ServiceRecord> r3 = r0.mRestartingServices
            boolean r3 = r3.contains(r1)
            if (r3 != 0) goto L_0x01fc
            r1.createdFromFg = r2
            java.util.ArrayList<com.android.server.am.ServiceRecord> r3 = r0.mRestartingServices
            r3.add(r1)
            com.android.server.am.ActivityManagerService r3 = r0.mAm
            com.android.server.am.ProcessStatsService r3 = r3.mProcessStats
            int r3 = r3.getMemFactorLocked()
            r1.makeRestarting(r3, r6)
        L_0x01fc:
            r25.cancelForegroundNotificationLocked(r26)
            com.android.server.am.ActivityManagerService r3 = r0.mAm
            com.android.server.am.ActivityManagerService$MainHandler r3 = r3.mHandler
            java.lang.Runnable r8 = r1.restarter
            r3.removeCallbacks(r8)
            com.android.server.am.ActivityManagerService r3 = r0.mAm
            com.android.server.am.ActivityManagerService$MainHandler r3 = r3.mHandler
            java.lang.Runnable r8 = r1.restarter
            long r9 = r1.nextRestartTime
            r3.postAtTime(r8, r9)
            long r8 = android.os.SystemClock.uptimeMillis()
            long r10 = r1.restartDelay
            long r8 = r8 + r10
            r1.nextRestartTime = r8
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r8 = "Scheduling restart of crashed service "
            r3.append(r8)
            java.lang.String r8 = r1.shortInstanceName
            r3.append(r8)
            java.lang.String r8 = " in "
            r3.append(r8)
            long r8 = r1.restartDelay
            r3.append(r8)
            java.lang.String r8 = "ms"
            r3.append(r8)
            java.lang.String r3 = r3.toString()
            android.util.Slog.w(r5, r3)
            boolean r3 = SERVICE_RESCHEDULE
            r3 = 30035(0x7553, float:4.2088E-41)
            r5 = 3
            java.lang.Object[] r5 = new java.lang.Object[r5]
            int r8 = r1.userId
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r2 = 0
            r5[r2] = r8
            java.lang.String r2 = r1.shortInstanceName
            r8 = 1
            r5[r8] = r2
            r2 = 2
            long r8 = r1.restartDelay
            java.lang.Long r8 = java.lang.Long.valueOf(r8)
            r5[r2] = r8
            android.util.EventLog.writeEvent(r3, r5)
            return r17
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ActiveServices.scheduleServiceRestartLocked(com.android.server.am.ServiceRecord, boolean):boolean");
    }

    /* access modifiers changed from: package-private */
    public final void performServiceRestartLocked(ServiceRecord r) {
        if (this.mRestartingServices.contains(r)) {
            boolean isPersistent = false;
            if (!isServiceNeededLocked(r, false, false)) {
                Slog.wtf("ActivityManager", "Restarting service that is not needed: " + r);
                return;
            }
            try {
                if (SERVICE_RESCHEDULE) {
                    boolean shouldDelay = false;
                    ActivityRecord top_rc = this.mAm.mAtmInternal.getFocusedStackTopRunningActivity();
                    if ((r.serviceInfo.applicationInfo.flags & 8) != 0) {
                        isPersistent = true;
                    }
                    if (top_rc != null && top_rc.launching && !r.shortInstanceName.contains(top_rc.packageName) && !isPersistent) {
                        shouldDelay = true;
                    }
                    if (!shouldDelay) {
                        bringUpServiceLocked(r, r.intent.getIntent().getFlags(), r.createdFromFg, true, false);
                    } else {
                        r.resetRestartCounter();
                        scheduleServiceRestartLocked(r, true);
                    }
                    return;
                }
                bringUpServiceLocked(r, r.intent.getIntent().getFlags(), r.createdFromFg, true, false);
            } catch (TransactionTooLargeException e) {
            }
        }
    }

    private final boolean unscheduleServiceRestartLocked(ServiceRecord r, int callingUid, boolean force) {
        if (!force && r.restartDelay == 0) {
            return false;
        }
        boolean removed = this.mRestartingServices.remove(r);
        if (removed || callingUid != r.appInfo.uid) {
            r.resetRestartCounter();
        }
        if (removed) {
            clearRestartingIfNeededLocked(r);
        }
        this.mAm.mHandler.removeCallbacks(r.restarter);
        return true;
    }

    private void clearRestartingIfNeededLocked(ServiceRecord r) {
        if (r.restartTracker != null) {
            boolean stillTracking = false;
            int i = this.mRestartingServices.size() - 1;
            while (true) {
                if (i < 0) {
                    break;
                } else if (this.mRestartingServices.get(i).restartTracker == r.restartTracker) {
                    stillTracking = true;
                    break;
                } else {
                    i--;
                }
            }
            if (!stillTracking) {
                r.restartTracker.setRestarting(false, this.mAm.mProcessStats.getMemFactorLocked(), SystemClock.uptimeMillis());
                r.restartTracker = null;
            }
        }
    }

    /* access modifiers changed from: private */
    public String bringUpServiceLocked(ServiceRecord r, int intentFlags, boolean execInFg, boolean whileRestarting, boolean permissionsReviewRequired) throws TransactionTooLargeException {
        HostingRecord hostingRecord;
        ProcessRecord app;
        ServiceRecord serviceRecord = r;
        boolean z = execInFg;
        if (serviceRecord.app != null && serviceRecord.app.thread != null) {
            sendServiceArgsLocked(serviceRecord, z, false);
            return null;
        } else if (!whileRestarting && this.mRestartingServices.contains(serviceRecord)) {
            return null;
        } else {
            if (this.mRestartingServices.remove(serviceRecord)) {
                clearRestartingIfNeededLocked(r);
            }
            if (serviceRecord.delayed) {
                getServiceMapLocked(serviceRecord.userId).mDelayedStartList.remove(serviceRecord);
                serviceRecord.delayed = false;
            }
            if (!this.mAm.mUserController.hasStartedUserState(serviceRecord.userId)) {
                String msg = "Unable to launch app " + serviceRecord.appInfo.packageName + SliceClientPermissions.SliceAuthority.DELIMITER + serviceRecord.appInfo.uid + " for service " + serviceRecord.intent.getIntent() + ": user " + serviceRecord.userId + " is stopped";
                Slog.w("ActivityManager", msg);
                bringDownServiceLocked(r);
                return msg;
            }
            try {
                AppGlobals.getPackageManager().setPackageStoppedState(serviceRecord.packageName, false, serviceRecord.userId);
            } catch (RemoteException e) {
            } catch (IllegalArgumentException e2) {
                Slog.w("ActivityManager", "Failed trying to unstop package " + serviceRecord.packageName + ": " + e2);
            }
            boolean isolated = (serviceRecord.serviceInfo.flags & 2) != 0;
            String procName = serviceRecord.processName;
            HostingRecord hostingRecord2 = new HostingRecord("service", serviceRecord.instanceName);
            if (!isolated) {
                ProcessRecord app2 = this.mAm.getProcessRecordLocked(procName, serviceRecord.appInfo.uid, false);
                if (!(app2 == null || app2.thread == null)) {
                    try {
                        app2.addPackage(serviceRecord.appInfo.packageName, serviceRecord.appInfo.longVersionCode, this.mAm.mProcessStats);
                        realStartServiceLocked(serviceRecord, app2, z);
                        return null;
                    } catch (TransactionTooLargeException e3) {
                        throw e3;
                    } catch (RemoteException e4) {
                        Slog.w("ActivityManager", "Exception when starting service " + serviceRecord.shortInstanceName, e4);
                    }
                }
                hostingRecord = hostingRecord2;
                app = app2;
            } else {
                ProcessRecord app3 = serviceRecord.isolatedProc;
                if (WebViewZygote.isMultiprocessEnabled() && serviceRecord.serviceInfo.packageName.equals(WebViewZygote.getPackageName())) {
                    hostingRecord2 = HostingRecord.byWebviewZygote(serviceRecord.instanceName);
                }
                if ((serviceRecord.serviceInfo.flags & 8) != 0) {
                    hostingRecord = HostingRecord.byAppZygote(serviceRecord.instanceName, serviceRecord.definingPackageName, serviceRecord.definingUid);
                    app = app3;
                } else {
                    hostingRecord = hostingRecord2;
                    app = app3;
                }
            }
            if (app != null || permissionsReviewRequired) {
            } else {
                String str = procName;
                ProcessRecord startProcessLocked = this.mAm.startProcessLocked(procName, serviceRecord.appInfo, true, intentFlags, hostingRecord, false, isolated, false, serviceRecord.callerPackage);
                ProcessRecord app4 = startProcessLocked;
                if (startProcessLocked == null) {
                    String msg2 = "Unable to launch app " + serviceRecord.appInfo.packageName + SliceClientPermissions.SliceAuthority.DELIMITER + serviceRecord.appInfo.uid + " for service " + serviceRecord.intent.getIntent() + ": process is bad";
                    Slog.w("ActivityManager", msg2);
                    bringDownServiceLocked(r);
                    return msg2;
                } else if (isolated) {
                    serviceRecord.isolatedProc = app4;
                }
            }
            if (serviceRecord.fgRequired) {
                this.mAm.tempWhitelistUidLocked(serviceRecord.appInfo.uid, JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY, "fg-service-launch");
            }
            if (!this.mPendingServices.contains(serviceRecord)) {
                this.mPendingServices.add(serviceRecord);
            }
            if (serviceRecord.delayedStop) {
                serviceRecord.delayedStop = false;
                if (serviceRecord.startRequested) {
                    stopServiceLocked(r);
                }
            }
            return null;
        }
    }

    private final void requestServiceBindingsLocked(ServiceRecord r, boolean execInFg) throws TransactionTooLargeException {
        int i = r.bindings.size() - 1;
        while (i >= 0 && requestServiceBindingLocked(r, r.bindings.valueAt(i), execInFg, false)) {
            i--;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 17 */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x016e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final void realStartServiceLocked(com.android.server.am.ServiceRecord r18, com.android.server.am.ProcessRecord r19, boolean r20) throws android.os.RemoteException {
        /*
            r17 = this;
            r1 = r17
            r9 = r18
            r10 = r19
            r11 = r20
            android.app.IApplicationThread r0 = r10.thread
            if (r0 == 0) goto L_0x0189
            r18.setProcess(r19)
            long r2 = android.os.SystemClock.uptimeMillis()
            r9.lastActivity = r2
            r9.restartTime = r2
            android.util.ArraySet<com.android.server.am.ServiceRecord> r0 = r10.f3services
            boolean r12 = r0.add(r9)
            java.lang.String r0 = "create"
            r1.bumpServiceExecutingLocked(r9, r11, r0)
            com.android.server.am.ActivityManagerService r0 = r1.mAm
            r2 = 0
            r13 = 0
            r0.updateLruProcessLocked(r10, r13, r2)
            com.android.server.am.ProcessRecord r0 = r9.app
            r1.updateServiceForegroundLocked(r0, r13)
            com.android.server.am.ActivityManagerService r0 = r1.mAm
            java.lang.String r3 = "updateOomAdj_startService"
            r0.updateOomAdjLocked(r3)
            r3 = 0
            r0 = 100
            android.content.pm.ApplicationInfo r4 = r9.appInfo     // Catch:{ DeadObjectException -> 0x013d }
            int r4 = r4.uid     // Catch:{ DeadObjectException -> 0x013d }
            android.content.ComponentName r5 = r9.name     // Catch:{ DeadObjectException -> 0x013d }
            java.lang.String r5 = r5.getPackageName()     // Catch:{ DeadObjectException -> 0x013d }
            android.content.ComponentName r6 = r9.name     // Catch:{ DeadObjectException -> 0x013d }
            java.lang.String r6 = r6.getClassName()     // Catch:{ DeadObjectException -> 0x013d }
            android.util.StatsLog.write(r0, r4, r5, r6)     // Catch:{ DeadObjectException -> 0x013d }
            com.android.internal.os.BatteryStatsImpl$Uid$Pkg$Serv r0 = r9.stats     // Catch:{ DeadObjectException -> 0x013d }
            com.android.internal.os.BatteryStatsImpl r4 = r0.getBatteryStats()     // Catch:{ DeadObjectException -> 0x013d }
            monitor-enter(r4)     // Catch:{ DeadObjectException -> 0x013d }
            com.android.internal.os.BatteryStatsImpl$Uid$Pkg$Serv r0 = r9.stats     // Catch:{ all -> 0x0138 }
            r0.startLaunchedLocked()     // Catch:{ all -> 0x0138 }
            monitor-exit(r4)     // Catch:{ all -> 0x0138 }
            com.android.server.am.ActivityManagerService r0 = r1.mAm     // Catch:{ DeadObjectException -> 0x013d }
            android.content.pm.ServiceInfo r4 = r9.serviceInfo     // Catch:{ DeadObjectException -> 0x013d }
            java.lang.String r4 = r4.packageName     // Catch:{ DeadObjectException -> 0x013d }
            r14 = 1
            r0.notifyPackageUse(r4, r14)     // Catch:{ DeadObjectException -> 0x013d }
            r0 = 11
            r10.forceProcessStateUpTo(r0)     // Catch:{ DeadObjectException -> 0x013d }
            android.app.IApplicationThread r0 = r10.thread     // Catch:{ DeadObjectException -> 0x013d }
            android.content.pm.ServiceInfo r4 = r9.serviceInfo     // Catch:{ DeadObjectException -> 0x013d }
            com.android.server.am.ActivityManagerService r5 = r1.mAm     // Catch:{ DeadObjectException -> 0x013d }
            android.content.pm.ServiceInfo r6 = r9.serviceInfo     // Catch:{ DeadObjectException -> 0x013d }
            android.content.pm.ApplicationInfo r6 = r6.applicationInfo     // Catch:{ DeadObjectException -> 0x013d }
            android.content.res.CompatibilityInfo r5 = r5.compatibilityInfoForPackage(r6)     // Catch:{ DeadObjectException -> 0x013d }
            int r6 = r19.getReportedProcState()     // Catch:{ DeadObjectException -> 0x013d }
            r0.scheduleCreateService(r9, r4, r5, r6)     // Catch:{ DeadObjectException -> 0x013d }
            r18.postNotification()     // Catch:{ DeadObjectException -> 0x013d }
            r15 = 1
            vendor.qti.hardware.servicetracker.V1_0.ServiceData r0 = new vendor.qti.hardware.servicetracker.V1_0.ServiceData     // Catch:{ DeadObjectException -> 0x0135, all -> 0x0132 }
            r0.<init>()     // Catch:{ DeadObjectException -> 0x0135, all -> 0x0132 }
            r3 = r0
            java.lang.String r0 = r9.packageName     // Catch:{ DeadObjectException -> 0x0135, all -> 0x0132 }
            r3.packageName = r0     // Catch:{ DeadObjectException -> 0x0135, all -> 0x0132 }
            java.lang.String r0 = r9.shortInstanceName     // Catch:{ DeadObjectException -> 0x0135, all -> 0x0132 }
            r3.processName = r0     // Catch:{ DeadObjectException -> 0x0135, all -> 0x0132 }
            com.android.server.am.ProcessRecord r0 = r9.app     // Catch:{ DeadObjectException -> 0x0135, all -> 0x0132 }
            int r0 = r0.pid     // Catch:{ DeadObjectException -> 0x0135, all -> 0x0132 }
            r3.pid = r0     // Catch:{ DeadObjectException -> 0x0135, all -> 0x0132 }
            long r4 = r9.lastActivity     // Catch:{ DeadObjectException -> 0x0135, all -> 0x0132 }
            double r4 = (double) r4     // Catch:{ DeadObjectException -> 0x0135, all -> 0x0132 }
            r3.lastActivity = r4     // Catch:{ DeadObjectException -> 0x0135, all -> 0x0132 }
            com.android.server.am.ProcessRecord r0 = r9.app     // Catch:{ DeadObjectException -> 0x0135, all -> 0x0132 }
            boolean r0 = r0.serviceb     // Catch:{ DeadObjectException -> 0x0135, all -> 0x0132 }
            r3.serviceB = r0     // Catch:{ DeadObjectException -> 0x0135, all -> 0x0132 }
            boolean r0 = r17.getServicetrackerInstance()     // Catch:{ RemoteException -> 0x00ac }
            if (r0 == 0) goto L_0x00ab
            vendor.qti.hardware.servicetracker.V1_0.IServicetracker r0 = r1.mServicetracker     // Catch:{ RemoteException -> 0x00ac }
            r0.startService(r3)     // Catch:{ RemoteException -> 0x00ac }
        L_0x00ab:
            goto L_0x00b6
        L_0x00ac:
            r0 = move-exception
            java.lang.String r4 = "ActivityManager"
            java.lang.String r5 = "Failed to send start details to servicetracker HAL"
            android.util.Slog.e(r4, r5, r0)     // Catch:{ DeadObjectException -> 0x0135, all -> 0x0132 }
            r1.mServicetracker = r2     // Catch:{ DeadObjectException -> 0x0135, all -> 0x0132 }
        L_0x00b6:
            if (r15 != 0) goto L_0x00d1
            java.util.ArrayList<com.android.server.am.ServiceRecord> r0 = r1.mDestroyingServices
            boolean r0 = r0.contains(r9)
            r1.serviceDoneExecutingLocked(r9, r0, r0)
            if (r12 == 0) goto L_0x00cc
            android.util.ArraySet<com.android.server.am.ServiceRecord> r3 = r10.f3services
            r3.remove(r9)
            r9.app = r2
            boolean r3 = SERVICE_RESCHEDULE
        L_0x00cc:
            if (r0 != 0) goto L_0x00d1
            r1.scheduleServiceRestartLocked(r9, r13)
        L_0x00d1:
            boolean r0 = r9.whitelistManager
            if (r0 == 0) goto L_0x00d7
            r10.whitelistManager = r14
        L_0x00d7:
            r1.requestServiceBindingsLocked(r9, r11)
            r1.updateServiceClientActivitiesLocked(r10, r2, r14)
            if (r12 == 0) goto L_0x00e4
            if (r15 == 0) goto L_0x00e4
            r10.addBoundClientUidsOfNewService(r9)
        L_0x00e4:
            boolean r0 = r9.startRequested
            if (r0 == 0) goto L_0x010d
            boolean r0 = r9.callStart
            if (r0 == 0) goto L_0x010d
            java.util.ArrayList<com.android.server.am.ServiceRecord$StartItem> r0 = r9.pendingStarts
            int r0 = r0.size()
            if (r0 != 0) goto L_0x010d
            java.util.ArrayList<com.android.server.am.ServiceRecord$StartItem> r0 = r9.pendingStarts
            com.android.server.am.ServiceRecord$StartItem r8 = new com.android.server.am.ServiceRecord$StartItem
            r4 = 0
            int r5 = r18.makeNextStartId()
            r6 = 0
            r7 = 0
            r16 = 0
            r2 = r8
            r3 = r18
            r13 = r8
            r8 = r16
            r2.<init>(r3, r4, r5, r6, r7, r8)
            r0.add(r13)
        L_0x010d:
            r1.sendServiceArgsLocked(r9, r11, r14)
            boolean r0 = r9.delayed
            if (r0 == 0) goto L_0x0123
            int r0 = r9.userId
            com.android.server.am.ActiveServices$ServiceMap r0 = r1.getServiceMapLocked(r0)
            java.util.ArrayList<com.android.server.am.ServiceRecord> r0 = r0.mDelayedStartList
            r0.remove(r9)
            r2 = 0
            r9.delayed = r2
            goto L_0x0124
        L_0x0123:
            r2 = 0
        L_0x0124:
            boolean r0 = r9.delayedStop
            if (r0 == 0) goto L_0x0131
            r9.delayedStop = r2
            boolean r0 = r9.startRequested
            if (r0 == 0) goto L_0x0131
            r17.stopServiceLocked(r18)
        L_0x0131:
            return
        L_0x0132:
            r0 = move-exception
            r3 = r15
            goto L_0x016c
        L_0x0135:
            r0 = move-exception
            r3 = r15
            goto L_0x013e
        L_0x0138:
            r0 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x0138 }
            throw r0     // Catch:{ DeadObjectException -> 0x013d }
        L_0x013b:
            r0 = move-exception
            goto L_0x016c
        L_0x013d:
            r0 = move-exception
        L_0x013e:
            java.lang.String r4 = "ActivityManager"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x013b }
            r5.<init>()     // Catch:{ all -> 0x013b }
            java.lang.String r6 = "Application dead when creating service "
            r5.append(r6)     // Catch:{ all -> 0x013b }
            r5.append(r9)     // Catch:{ all -> 0x013b }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x013b }
            android.util.Slog.w(r4, r5)     // Catch:{ all -> 0x013b }
            com.android.server.am.ActivityManagerService r4 = r1.mAm     // Catch:{ all -> 0x013b }
            java.util.ArrayList<com.android.server.am.ProcessRecord> r4 = r4.mPersistentStartingProcesses     // Catch:{ all -> 0x013b }
            boolean r4 = r4.contains(r10)     // Catch:{ all -> 0x013b }
            if (r4 == 0) goto L_0x0165
            com.android.server.am.ActivityManagerService r4 = r1.mAm     // Catch:{ all -> 0x013b }
            java.util.ArrayList<com.android.server.am.ProcessRecord> r4 = r4.mPersistentStartingProcesses     // Catch:{ all -> 0x013b }
            r4.remove(r10)     // Catch:{ all -> 0x013b }
        L_0x0165:
            com.android.server.am.ActivityManagerService r4 = r1.mAm     // Catch:{ all -> 0x013b }
            r4.appDiedLocked(r10)     // Catch:{ all -> 0x013b }
            throw r0     // Catch:{ all -> 0x013b }
        L_0x016c:
            if (r3 != 0) goto L_0x0188
            java.util.ArrayList<com.android.server.am.ServiceRecord> r4 = r1.mDestroyingServices
            boolean r4 = r4.contains(r9)
            r1.serviceDoneExecutingLocked(r9, r4, r4)
            if (r12 == 0) goto L_0x0182
            android.util.ArraySet<com.android.server.am.ServiceRecord> r5 = r10.f3services
            r5.remove(r9)
            r9.app = r2
            boolean r2 = SERVICE_RESCHEDULE
        L_0x0182:
            if (r4 != 0) goto L_0x0188
            r2 = 0
            r1.scheduleServiceRestartLocked(r9, r2)
        L_0x0188:
            throw r0
        L_0x0189:
            android.os.RemoteException r0 = new android.os.RemoteException
            r0.<init>()
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ActiveServices.realStartServiceLocked(com.android.server.am.ServiceRecord, com.android.server.am.ProcessRecord, boolean):void");
    }

    private final void sendServiceArgsLocked(ServiceRecord r, boolean execInFg, boolean oomAdjusted) throws TransactionTooLargeException {
        int N = r.pendingStarts.size();
        if (N != 0) {
            ArrayList<ServiceStartArgs> args = new ArrayList<>();
            while (r.pendingStarts.size() > 0) {
                ServiceRecord.StartItem si = r.pendingStarts.remove(0);
                if (si.intent != null || N <= 1) {
                    si.deliveredTime = SystemClock.uptimeMillis();
                    r.deliveredStarts.add(si);
                    si.deliveryCount++;
                    if (si.neededGrants != null) {
                        this.mAm.mUgmInternal.grantUriPermissionUncheckedFromIntent(si.neededGrants, si.getUriPermissionsLocked());
                    }
                    this.mAm.grantEphemeralAccessLocked(r.userId, si.intent, UserHandle.getAppId(r.appInfo.uid), UserHandle.getAppId(si.callingId));
                    bumpServiceExecutingLocked(r, execInFg, "start");
                    if (!oomAdjusted) {
                        oomAdjusted = true;
                        this.mAm.updateOomAdjLocked(r.app, true, "updateOomAdj_startService");
                    }
                    if (r.fgRequired && !r.fgWaiting) {
                        if (!r.isForeground) {
                            scheduleServiceForegroundTransitionTimeoutLocked(r);
                        } else {
                            r.fgRequired = false;
                        }
                    }
                    int flags = 0;
                    if (si.deliveryCount > 1) {
                        flags = 0 | 2;
                    }
                    if (si.doneExecutingCount > 0) {
                        flags |= 1;
                    }
                    args.add(new ServiceStartArgs(si.taskRemoved, si.id, flags, si.intent));
                }
            }
            ParceledListSlice<ServiceStartArgs> slice = new ParceledListSlice<>(args);
            slice.setInlineCountLimit(4);
            Exception caughtException = null;
            try {
                r.app.thread.scheduleServiceArgs(r, slice);
            } catch (TransactionTooLargeException e) {
                Slog.w("ActivityManager", "Failed delivering service starts", e);
                caughtException = e;
            } catch (RemoteException e2) {
                Slog.w("ActivityManager", "Failed delivering service starts", e2);
                caughtException = e2;
            } catch (Exception e3) {
                Slog.w("ActivityManager", "Unexpected exception", e3);
                caughtException = e3;
            }
            if (caughtException != null) {
                boolean inDestroying = this.mDestroyingServices.contains(r);
                for (int i = 0; i < args.size(); i++) {
                    serviceDoneExecutingLocked(r, inDestroying, inDestroying);
                }
                if ((caughtException instanceof TransactionTooLargeException) != 0) {
                    throw ((TransactionTooLargeException) caughtException);
                }
            }
        }
    }

    private final boolean isServiceNeededLocked(ServiceRecord r, boolean knowConn, boolean hasConn) {
        if (r.startRequested) {
            return true;
        }
        if (!knowConn) {
            hasConn = r.hasAutoCreateConnections();
        }
        if (hasConn) {
            return true;
        }
        return false;
    }

    private final void bringDownServiceIfNeededLocked(ServiceRecord r, boolean knowConn, boolean hasConn) {
        if (!isServiceNeededLocked(r, knowConn, hasConn) && !this.mPendingServices.contains(r)) {
            bringDownServiceLocked(r);
        }
    }

    private final void bringDownServiceLocked(ServiceRecord r) {
        ServiceData sData = new ServiceData();
        sData.packageName = r.packageName;
        sData.processName = r.shortInstanceName;
        sData.lastActivity = (double) r.lastActivity;
        if (r.app != null) {
            sData.pid = r.app.pid;
        } else {
            sData.pid = -1;
            sData.serviceB = false;
        }
        try {
            if (getServicetrackerInstance()) {
                this.mServicetracker.destroyService(sData);
            }
        } catch (RemoteException e) {
            Slog.e("ActivityManager", "Failed to send destroy details to servicetracker HAL", e);
            this.mServicetracker = null;
        }
        ArrayMap<IBinder, ArrayList<ConnectionRecord>> connections = r.getConnections();
        for (int conni = connections.size() - 1; conni >= 0; conni--) {
            ArrayList<ConnectionRecord> c = connections.valueAt(conni);
            for (int i = 0; i < c.size(); i++) {
                ConnectionRecord cr = c.get(i);
                cr.serviceDead = true;
                cr.stopAssociation();
                try {
                    cr.conn.connected(r.name, (IBinder) null, true);
                } catch (Exception e2) {
                    Slog.w("ActivityManager", "Failure disconnecting service " + r.shortInstanceName + " to connection " + c.get(i).conn.asBinder() + " (in " + c.get(i).binding.client.processName + ")", e2);
                }
            }
        }
        if (!(r.app == null || r.app.thread == null)) {
            boolean needOomAdj = false;
            try {
                for (int i2 = r.bindings.size() - 1; i2 >= 0; i2--) {
                    IntentBindRecord ibr = r.bindings.valueAt(i2);
                    if (ibr.hasBound) {
                        bumpServiceExecutingLocked(r, false, "bring down unbind");
                        needOomAdj = true;
                        ibr.hasBound = false;
                        ibr.requested = false;
                        r.app.thread.scheduleUnbindService(r, ibr.intent.getIntent());
                    }
                }
            } catch (Exception e3) {
                Slog.w("ActivityManager", "Exception when unbinding service " + r.shortInstanceName, e3);
                serviceProcessGoneLocked(r);
            }
            if (needOomAdj && r.app != null) {
                this.mAm.updateOomAdjLocked(r.app, true, "updateOomAdj_unbindService");
            }
        }
        if (r.fgRequired) {
            Slog.w("ActivityManager", "Bringing down service while still waiting for start foreground: " + r);
            r.fgRequired = false;
            r.fgWaiting = false;
            ServiceState stracker = r.getTracker();
            if (stracker != null) {
                stracker.setForeground(false, this.mAm.mProcessStats.getMemFactorLocked(), r.lastActivity);
            }
            this.mAm.mAppOpsService.finishOperation(AppOpsManager.getToken(this.mAm.mAppOpsService), 76, r.appInfo.uid, r.packageName);
            this.mAm.mHandler.removeMessages(66, r);
            if (r.app != null) {
                Message msg = this.mAm.mHandler.obtainMessage(69);
                msg.obj = r.app;
                msg.getData().putCharSequence("servicerecord", r.toString());
                this.mAm.mHandler.sendMessage(msg);
            }
        }
        r.destroyTime = SystemClock.uptimeMillis();
        ServiceMap smap = getServiceMapLocked(r.userId);
        ServiceRecord found = smap.mServicesByInstanceName.remove(r.instanceName);
        if (found == null || found == r) {
            smap.mServicesByIntent.remove(r.intent);
            r.totalRestartCount = 0;
            unscheduleServiceRestartLocked(r, 0, true);
            for (int i3 = this.mPendingServices.size() - 1; i3 >= 0; i3--) {
                if (this.mPendingServices.get(i3) == r) {
                    this.mPendingServices.remove(i3);
                }
            }
            cancelForegroundNotificationLocked(r);
            if (r.isForeground) {
                decActiveForegroundAppLocked(smap, r);
                ServiceState stracker2 = r.getTracker();
                if (stracker2 != null) {
                    stracker2.setForeground(false, this.mAm.mProcessStats.getMemFactorLocked(), r.lastActivity);
                }
                this.mAm.mAppOpsService.finishOperation(AppOpsManager.getToken(this.mAm.mAppOpsService), 76, r.appInfo.uid, r.packageName);
                StatsLog.write(60, r.appInfo.uid, r.shortInstanceName, 2);
                this.mAm.updateForegroundServiceUsageStats(r.name, r.userId, false);
            }
            r.isForeground = false;
            r.foregroundId = 0;
            r.foregroundNoti = null;
            r.clearDeliveredStartsLocked();
            r.pendingStarts.clear();
            smap.mDelayedStartList.remove(r);
            if (r.app != null) {
                synchronized (r.stats.getBatteryStats()) {
                    r.stats.stopLaunchedLocked();
                }
                r.app.f3services.remove(r);
                r.app.updateBoundClientUids();
                if (r.whitelistManager) {
                    updateWhitelistManagerLocked(r.app);
                }
                if (r.app.thread != null) {
                    updateServiceForegroundLocked(r.app, false);
                    try {
                        bumpServiceExecutingLocked(r, false, "destroy");
                        this.mDestroyingServices.add(r);
                        r.destroying = true;
                        this.mAm.updateOomAdjLocked(r.app, true, "updateOomAdj_unbindService");
                        r.app.thread.scheduleStopService(r);
                    } catch (Exception e4) {
                        Slog.w("ActivityManager", "Exception when destroying service " + r.shortInstanceName, e4);
                        serviceProcessGoneLocked(r);
                    }
                }
            }
            if (r.bindings.size() > 0) {
                r.bindings.clear();
            }
            if (r.restarter instanceof ServiceRestarter) {
                ((ServiceRestarter) r.restarter).setService((ServiceRecord) null);
            }
            int memFactor = this.mAm.mProcessStats.getMemFactorLocked();
            long now = SystemClock.uptimeMillis();
            if (r.tracker != null) {
                r.tracker.setStarted(false, memFactor, now);
                r.tracker.setBound(false, memFactor, now);
                if (r.executeNesting == 0) {
                    r.tracker.clearCurrentOwner(r, false);
                    r.tracker = null;
                }
            }
            smap.ensureNotStartingBackgroundLocked(r);
            return;
        }
        smap.mServicesByInstanceName.put(r.instanceName, found);
        throw new IllegalStateException("Bringing down " + r + " but actually running " + found);
    }

    /* access modifiers changed from: package-private */
    public void removeConnectionLocked(ConnectionRecord c, ProcessRecord skipApp, ActivityServiceConnectionsHolder skipAct) {
        ConnectionRecord connectionRecord = c;
        ActivityServiceConnectionsHolder activityServiceConnectionsHolder = skipAct;
        IBinder binder = connectionRecord.conn.asBinder();
        AppBindRecord b = connectionRecord.binding;
        ServiceRecord s = b.service;
        if (activityServiceConnectionsHolder == null || (!s.destroying && s.destroyTime == 0)) {
            ArrayList<ConnectionRecord> clist = s.getConnections().get(binder);
            if (clist != null) {
                clist.remove(connectionRecord);
                if (clist.size() == 0) {
                    s.removeConnection(binder);
                }
            }
            b.connections.remove(connectionRecord);
            c.stopAssociation();
            if (!(connectionRecord.activity == null || connectionRecord.activity == activityServiceConnectionsHolder)) {
                connectionRecord.activity.removeConnection(connectionRecord);
            }
            if (b.client != skipApp) {
                b.client.connections.remove(connectionRecord);
                if ((connectionRecord.flags & 8) != 0) {
                    b.client.updateHasAboveClientLocked();
                }
                if ((connectionRecord.flags & DumpState.DUMP_SERVICE_PERMISSIONS) != 0) {
                    s.updateWhitelistManager();
                    if (!s.whitelistManager && s.app != null) {
                        updateWhitelistManagerLocked(s.app);
                    }
                }
                if ((connectionRecord.flags & DumpState.DUMP_DEXOPT) != 0) {
                    s.updateHasBindingWhitelistingBgActivityStarts();
                }
                if (s.app != null) {
                    updateServiceClientActivitiesLocked(s.app, connectionRecord, true);
                }
            }
            ArrayList<ConnectionRecord> clist2 = this.mServiceConnections.get(binder);
            if (clist2 != null) {
                clist2.remove(connectionRecord);
                if (clist2.size() == 0) {
                    this.mServiceConnections.remove(binder);
                }
            }
            this.mAm.stopAssociationLocked(b.client.uid, b.client.processName, s.appInfo.uid, s.appInfo.longVersionCode, s.instanceName, s.processName);
            if (b.connections.size() == 0) {
                b.intent.apps.remove(b.client);
            }
            if (!connectionRecord.serviceDead) {
                if (s.app != null && s.app.thread != null && b.intent.apps.size() == 0 && b.intent.hasBound) {
                    try {
                        bumpServiceExecutingLocked(s, false, "unbind");
                        if (b.client != s.app && (connectionRecord.flags & 32) == 0 && s.app.setProcState <= 14) {
                            this.mAm.updateLruProcessLocked(s.app, false, (ProcessRecord) null);
                        }
                        this.mAm.updateOomAdjLocked(s.app, true, "updateOomAdj_unbindService");
                        b.intent.hasBound = false;
                        b.intent.doRebind = false;
                        s.app.thread.scheduleUnbindService(s, b.intent.intent.getIntent());
                    } catch (Exception e) {
                        Slog.w("ActivityManager", "Exception when unbinding service " + s.shortInstanceName, e);
                        serviceProcessGoneLocked(s);
                    }
                }
                this.mPendingServices.remove(s);
                if ((connectionRecord.flags & 1) != 0) {
                    boolean hasAutoCreate = s.hasAutoCreateConnections();
                    if (!hasAutoCreate && s.tracker != null) {
                        s.tracker.setBound(false, this.mAm.mProcessStats.getMemFactorLocked(), SystemClock.uptimeMillis());
                    }
                    bringDownServiceIfNeededLocked(s, true, hasAutoCreate);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void serviceDoneExecutingLocked(ServiceRecord r, int type, int startId, int res) {
        boolean inDestroying = this.mDestroyingServices.contains(r);
        if (r != null) {
            if (type == 1) {
                r.callStart = true;
                if (res == 0 || res == 1) {
                    r.findDeliveredStart(startId, false, true);
                    r.stopIfKilled = false;
                } else if (res == 2) {
                    r.findDeliveredStart(startId, false, true);
                    if (r.getLastStartId() == startId) {
                        r.stopIfKilled = true;
                    }
                } else if (res == 3) {
                    ServiceRecord.StartItem si = r.findDeliveredStart(startId, false, false);
                    if (si != null) {
                        si.deliveryCount = 0;
                        si.doneExecutingCount++;
                        r.stopIfKilled = true;
                    }
                } else if (res == 1000) {
                    r.findDeliveredStart(startId, true, true);
                } else {
                    throw new IllegalArgumentException("Unknown service start result: " + res);
                }
                if (res == 0) {
                    r.callStart = false;
                }
            } else if (type == 2) {
                if (!inDestroying) {
                    if (r.app != null) {
                        Slog.w("ActivityManager", "Service done with onDestroy, but not inDestroying: " + r + ", app=" + r.app);
                    }
                } else if (r.executeNesting != 1) {
                    Slog.w("ActivityManager", "Service done with onDestroy, but executeNesting=" + r.executeNesting + ": " + r);
                    r.executeNesting = 1;
                }
            }
            long origId = Binder.clearCallingIdentity();
            serviceDoneExecutingLocked(r, inDestroying, inDestroying);
            Binder.restoreCallingIdentity(origId);
            return;
        }
        Slog.w("ActivityManager", "Done executing unknown service from pid " + Binder.getCallingPid());
    }

    private void serviceProcessGoneLocked(ServiceRecord r) {
        if (r.tracker != null) {
            int memFactor = this.mAm.mProcessStats.getMemFactorLocked();
            long now = SystemClock.uptimeMillis();
            r.tracker.setExecuting(false, memFactor, now);
            r.tracker.setForeground(false, memFactor, now);
            r.tracker.setBound(false, memFactor, now);
            r.tracker.setStarted(false, memFactor, now);
        }
        serviceDoneExecutingLocked(r, true, true);
    }

    private void serviceDoneExecutingLocked(ServiceRecord r, boolean inDestroying, boolean finishing) {
        r.executeNesting--;
        if (r.executeNesting <= 0) {
            if (r.app != null) {
                r.app.execServicesFg = false;
                r.app.executingServices.remove(r);
                if (r.app.executingServices.size() == 0) {
                    this.mAm.mHandler.removeMessages(12, r.app);
                } else if (r.executeFg) {
                    int i = r.app.executingServices.size() - 1;
                    while (true) {
                        if (i < 0) {
                            break;
                        } else if (r.app.executingServices.valueAt(i).executeFg) {
                            r.app.execServicesFg = true;
                            break;
                        } else {
                            i--;
                        }
                    }
                }
                if (inDestroying) {
                    this.mDestroyingServices.remove(r);
                    r.bindings.clear();
                }
                this.mAm.updateOomAdjLocked(r.app, true, "updateOomAdj_unbindService");
            }
            r.executeFg = false;
            if (r.tracker != null) {
                int memFactor = this.mAm.mProcessStats.getMemFactorLocked();
                long now = SystemClock.uptimeMillis();
                r.tracker.setExecuting(false, memFactor, now);
                r.tracker.setForeground(false, memFactor, now);
                if (finishing) {
                    r.tracker.clearCurrentOwner(r, false);
                    r.tracker = null;
                }
            }
            if (finishing) {
                if (r.app != null && !r.app.isPersistent()) {
                    r.app.f3services.remove(r);
                    r.app.updateBoundClientUids();
                    if (r.whitelistManager) {
                        updateWhitelistManagerLocked(r.app);
                    }
                }
                r.setProcess((ProcessRecord) null);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean attachApplicationLocked(ProcessRecord proc, String processName) throws RemoteException {
        boolean didSomething = false;
        if (this.mPendingServices.size() > 0) {
            ServiceRecord sr = null;
            int i = 0;
            while (i < this.mPendingServices.size()) {
                try {
                    sr = this.mPendingServices.get(i);
                    if (proc != sr.isolatedProc) {
                        if (proc.uid == sr.appInfo.uid) {
                            if (!processName.equals(sr.processName)) {
                            }
                        }
                        i++;
                    }
                    this.mPendingServices.remove(i);
                    i--;
                    proc.addPackage(sr.appInfo.packageName, sr.appInfo.longVersionCode, this.mAm.mProcessStats);
                    realStartServiceLocked(sr, proc, sr.createdFromFg);
                    didSomething = true;
                    if (!isServiceNeededLocked(sr, false, false)) {
                        bringDownServiceLocked(sr);
                    }
                    i++;
                } catch (RemoteException e) {
                    Slog.w("ActivityManager", "Exception in new application when starting service " + sr.shortInstanceName, e);
                    throw e;
                }
            }
        }
        if (this.mRestartingServices.size() > 0) {
            for (int i2 = 0; i2 < this.mRestartingServices.size(); i2++) {
                ServiceRecord sr2 = this.mRestartingServices.get(i2);
                if (proc == sr2.isolatedProc || (proc.uid == sr2.appInfo.uid && processName.equals(sr2.processName))) {
                    this.mAm.mHandler.removeCallbacks(sr2.restarter);
                    this.mAm.mHandler.post(sr2.restarter);
                }
            }
        }
        return didSomething;
    }

    /* access modifiers changed from: package-private */
    public void processStartTimedOutLocked(ProcessRecord proc) {
        int i = 0;
        while (i < this.mPendingServices.size()) {
            ServiceRecord sr = this.mPendingServices.get(i);
            if ((proc.uid == sr.appInfo.uid && proc.processName.equals(sr.processName)) || sr.isolatedProc == proc) {
                Slog.w("ActivityManager", "Forcing bringing down service: " + sr);
                sr.isolatedProc = null;
                this.mPendingServices.remove(i);
                i += -1;
                if (AccessController.PACKAGE_SYSTEMUI.equals(proc.processName)) {
                    this.mRestartingServices.add(sr);
                } else {
                    bringDownServiceLocked(sr);
                }
            }
            i++;
        }
    }

    private boolean collectPackageServicesLocked(String packageName, Set<String> filterByClasses, boolean evenPersistent, boolean doit, ArrayMap<ComponentName, ServiceRecord> services2) {
        boolean didSomething = false;
        for (int i = services2.size() - 1; i >= 0; i--) {
            ServiceRecord service = services2.valueAt(i);
            if ((packageName == null || (service.packageName.equals(packageName) && (filterByClasses == null || filterByClasses.contains(service.name.getClassName())))) && (service.app == null || evenPersistent || !service.app.isPersistent())) {
                if (!doit) {
                    return true;
                }
                didSomething = true;
                Slog.i("ActivityManager", "  Force stopping service " + service);
                if (service.app != null && !service.app.isPersistent()) {
                    service.app.f3services.remove(service);
                    service.app.updateBoundClientUids();
                    if (service.whitelistManager) {
                        updateWhitelistManagerLocked(service.app);
                    }
                }
                service.setProcess((ProcessRecord) null);
                service.isolatedProc = null;
                if (this.mTmpCollectionResults == null) {
                    this.mTmpCollectionResults = new ArrayList<>();
                }
                this.mTmpCollectionResults.add(service);
            }
        }
        return didSomething;
    }

    /* access modifiers changed from: package-private */
    public boolean bringDownDisabledPackageServicesLocked(String packageName, Set<String> filterByClasses, int userId, boolean evenPersistent, boolean doit) {
        boolean didSomething = false;
        ArrayList<ServiceRecord> arrayList = this.mTmpCollectionResults;
        if (arrayList != null) {
            arrayList.clear();
        }
        if (userId == -1) {
            for (int i = this.mServiceMap.size() - 1; i >= 0; i--) {
                didSomething |= collectPackageServicesLocked(packageName, filterByClasses, evenPersistent, doit, this.mServiceMap.valueAt(i).mServicesByInstanceName);
                if (!doit && didSomething) {
                    return true;
                }
                if (doit && filterByClasses == null) {
                    forceStopPackageLocked(packageName, this.mServiceMap.valueAt(i).mUserId);
                }
            }
        } else {
            ServiceMap smap = this.mServiceMap.get(userId);
            if (smap != null) {
                didSomething = collectPackageServicesLocked(packageName, filterByClasses, evenPersistent, doit, smap.mServicesByInstanceName);
            }
            if (doit && filterByClasses == null) {
                forceStopPackageLocked(packageName, userId);
            }
        }
        ActiveServicesInjector.removeServiceLocked(userId, this.mServiceMap, this.mTmpCollectionResults);
        ArrayList<ServiceRecord> arrayList2 = this.mTmpCollectionResults;
        if (arrayList2 != null) {
            for (int i2 = arrayList2.size() - 1; i2 >= 0; i2--) {
                bringDownServiceLocked(this.mTmpCollectionResults.get(i2));
            }
            this.mTmpCollectionResults.clear();
        }
        return didSomething;
    }

    /* access modifiers changed from: package-private */
    public void forceStopPackageLocked(String packageName, int userId) {
        ServiceMap smap = this.mServiceMap.get(userId);
        if (smap != null && smap.mActiveForegroundApps.size() > 0) {
            for (int i = smap.mActiveForegroundApps.size() - 1; i >= 0; i--) {
                if (smap.mActiveForegroundApps.valueAt(i).mPackageName.equals(packageName)) {
                    smap.mActiveForegroundApps.removeAt(i);
                    smap.mActiveForegroundAppsChanged = true;
                }
            }
            if (smap.mActiveForegroundAppsChanged != 0) {
                requestUpdateActiveForegroundAppsLocked(smap, 0);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void cleanUpServices(int userId, ComponentName component, Intent baseIntent) {
        ArrayList arrayList = new ArrayList();
        ArrayMap<ComponentName, ServiceRecord> alls = getServicesLocked(userId);
        for (int i = alls.size() - 1; i >= 0; i--) {
            ServiceRecord sr = alls.valueAt(i);
            if (sr.packageName.equals(component.getPackageName())) {
                arrayList.add(sr);
            }
        }
        for (int i2 = arrayList.size() - 1; i2 >= 0; i2--) {
            ServiceRecord sr2 = (ServiceRecord) arrayList.get(i2);
            if (sr2.startRequested) {
                if ((sr2.serviceInfo.flags & 1) != 0) {
                    Slog.i("ActivityManager", "Stopping service " + sr2.shortInstanceName + ": remove task");
                    stopServiceLocked(sr2);
                } else {
                    sr2.pendingStarts.add(new ServiceRecord.StartItem(sr2, true, sr2.getLastStartId(), baseIntent, (NeededUriGrants) null, 0));
                    if (!(sr2.app == null || sr2.app.thread == null)) {
                        try {
                            sendServiceArgsLocked(sr2, true, false);
                        } catch (TransactionTooLargeException e) {
                        }
                    }
                }
            }
        }
    }

    /* JADX WARNING: type inference failed for: r3v4, types: [android.os.IBinder] */
    /* JADX WARNING: type inference failed for: r3v6 */
    /* JADX WARNING: type inference failed for: r3v10 */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void killServicesLocked(com.android.server.am.ProcessRecord r17, boolean r18) {
        /*
            r16 = this;
            r1 = r16
            r2 = r17
            r3 = 0
            boolean r0 = r16.getServicetrackerInstance()     // Catch:{ RemoteException -> 0x0013 }
            if (r0 == 0) goto L_0x0012
            vendor.qti.hardware.servicetracker.V1_0.IServicetracker r0 = r1.mServicetracker     // Catch:{ RemoteException -> 0x0013 }
            int r4 = r2.pid     // Catch:{ RemoteException -> 0x0013 }
            r0.killProcess(r4)     // Catch:{ RemoteException -> 0x0013 }
        L_0x0012:
            goto L_0x001d
        L_0x0013:
            r0 = move-exception
            java.lang.String r4 = "ActivityManager"
            java.lang.String r5 = "Failed to send kill process details to servicetracker HAL"
            android.util.Slog.e(r4, r5, r0)
            r1.mServicetracker = r3
        L_0x001d:
            android.util.ArraySet<com.android.server.am.ConnectionRecord> r0 = r2.connections
            int r0 = r0.size()
            r4 = 1
            int r0 = r0 - r4
        L_0x0025:
            if (r0 < 0) goto L_0x0035
            android.util.ArraySet<com.android.server.am.ConnectionRecord> r5 = r2.connections
            java.lang.Object r5 = r5.valueAt(r0)
            com.android.server.am.ConnectionRecord r5 = (com.android.server.am.ConnectionRecord) r5
            r1.removeConnectionLocked(r5, r2, r3)
            int r0 = r0 + -1
            goto L_0x0025
        L_0x0035:
            r16.updateServiceConnectionActivitiesLocked(r17)
            android.util.ArraySet<com.android.server.am.ConnectionRecord> r0 = r2.connections
            r0.clear()
            r0 = 0
            r2.whitelistManager = r0
            android.util.ArraySet<com.android.server.am.ServiceRecord> r5 = r2.f3services
            int r5 = r5.size()
            int r5 = r5 - r4
        L_0x0047:
            if (r5 < 0) goto L_0x00f5
            android.util.ArraySet<com.android.server.am.ServiceRecord> r6 = r2.f3services
            java.lang.Object r6 = r6.valueAt(r5)
            com.android.server.am.ServiceRecord r6 = (com.android.server.am.ServiceRecord) r6
            com.android.internal.os.BatteryStatsImpl$Uid$Pkg$Serv r7 = r6.stats
            com.android.internal.os.BatteryStatsImpl r7 = r7.getBatteryStats()
            monitor-enter(r7)
            com.android.internal.os.BatteryStatsImpl$Uid$Pkg$Serv r8 = r6.stats     // Catch:{ all -> 0x00f2 }
            r8.stopLaunchedLocked()     // Catch:{ all -> 0x00f2 }
            monitor-exit(r7)     // Catch:{ all -> 0x00f2 }
            com.android.server.am.ProcessRecord r7 = r6.app
            if (r7 == r2) goto L_0x007a
            com.android.server.am.ProcessRecord r7 = r6.app
            if (r7 == 0) goto L_0x007a
            com.android.server.am.ProcessRecord r7 = r6.app
            boolean r7 = r7.isPersistent()
            if (r7 != 0) goto L_0x007a
            com.android.server.am.ProcessRecord r7 = r6.app
            android.util.ArraySet<com.android.server.am.ServiceRecord> r7 = r7.f3services
            r7.remove(r6)
            com.android.server.am.ProcessRecord r7 = r6.app
            r7.updateBoundClientUids()
        L_0x007a:
            r6.setProcess(r3)
            r6.isolatedProc = r3
            r6.executeNesting = r0
            r6.forceClearTracker()
            java.util.ArrayList<com.android.server.am.ServiceRecord> r7 = r1.mDestroyingServices
            r7.remove(r6)
            android.util.ArrayMap<android.content.Intent$FilterComparison, com.android.server.am.IntentBindRecord> r7 = r6.bindings
            int r7 = r7.size()
            int r8 = r7 + -1
        L_0x0091:
            if (r8 < 0) goto L_0x00ed
            android.util.ArrayMap<android.content.Intent$FilterComparison, com.android.server.am.IntentBindRecord> r9 = r6.bindings
            java.lang.Object r9 = r9.valueAt(r8)
            com.android.server.am.IntentBindRecord r9 = (com.android.server.am.IntentBindRecord) r9
            r9.binder = r3
            r9.hasBound = r0
            r9.received = r0
            r9.requested = r0
            android.util.ArrayMap<com.android.server.am.ProcessRecord, com.android.server.am.AppBindRecord> r10 = r9.apps
            int r10 = r10.size()
            int r10 = r10 - r4
        L_0x00aa:
            if (r10 < 0) goto L_0x00e9
            android.util.ArrayMap<com.android.server.am.ProcessRecord, com.android.server.am.AppBindRecord> r11 = r9.apps
            java.lang.Object r11 = r11.keyAt(r10)
            com.android.server.am.ProcessRecord r11 = (com.android.server.am.ProcessRecord) r11
            boolean r12 = r11.killedByAm
            if (r12 != 0) goto L_0x00e5
            android.app.IApplicationThread r12 = r11.thread
            if (r12 != 0) goto L_0x00bd
            goto L_0x00e5
        L_0x00bd:
            android.util.ArrayMap<com.android.server.am.ProcessRecord, com.android.server.am.AppBindRecord> r12 = r9.apps
            java.lang.Object r12 = r12.valueAt(r10)
            com.android.server.am.AppBindRecord r12 = (com.android.server.am.AppBindRecord) r12
            r13 = 0
            android.util.ArraySet<com.android.server.am.ConnectionRecord> r14 = r12.connections
            int r14 = r14.size()
            int r14 = r14 - r4
        L_0x00cd:
            if (r14 < 0) goto L_0x00e3
            android.util.ArraySet<com.android.server.am.ConnectionRecord> r15 = r12.connections
            java.lang.Object r15 = r15.valueAt(r14)
            com.android.server.am.ConnectionRecord r15 = (com.android.server.am.ConnectionRecord) r15
            int r3 = r15.flags
            r3 = r3 & 49
            if (r3 != r4) goto L_0x00df
            r13 = 1
            goto L_0x00e3
        L_0x00df:
            int r14 = r14 + -1
            r3 = 0
            goto L_0x00cd
        L_0x00e3:
            if (r13 != 0) goto L_0x00e5
        L_0x00e5:
            int r10 = r10 + -1
            r3 = 0
            goto L_0x00aa
        L_0x00e9:
            int r8 = r8 + -1
            r3 = 0
            goto L_0x0091
        L_0x00ed:
            int r5 = r5 + -1
            r3 = 0
            goto L_0x0047
        L_0x00f2:
            r0 = move-exception
            monitor-exit(r7)     // Catch:{ all -> 0x00f2 }
            throw r0
        L_0x00f5:
            int r3 = r2.userId
            com.android.server.am.ActiveServices$ServiceMap r3 = r1.getServiceMapLocked(r3)
            android.util.ArraySet<com.android.server.am.ServiceRecord> r5 = r2.f3services
            int r5 = r5.size()
            int r5 = r5 - r4
        L_0x0102:
            if (r5 < 0) goto L_0x0200
            android.util.ArraySet<com.android.server.am.ServiceRecord> r6 = r2.f3services
            java.lang.Object r6 = r6.valueAt(r5)
            com.android.server.am.ServiceRecord r6 = (com.android.server.am.ServiceRecord) r6
            boolean r7 = r17.isPersistent()
            if (r7 != 0) goto L_0x011a
            android.util.ArraySet<com.android.server.am.ServiceRecord> r7 = r2.f3services
            r7.removeAt(r5)
            r17.updateBoundClientUids()
        L_0x011a:
            android.util.ArrayMap<android.content.ComponentName, com.android.server.am.ServiceRecord> r7 = r3.mServicesByInstanceName
            android.content.ComponentName r8 = r6.instanceName
            java.lang.Object r7 = r7.get(r8)
            com.android.server.am.ServiceRecord r7 = (com.android.server.am.ServiceRecord) r7
            if (r7 == r6) goto L_0x0150
            if (r7 == 0) goto L_0x01fc
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "Service "
            r8.append(r9)
            r8.append(r6)
            java.lang.String r9 = " in process "
            r8.append(r9)
            r8.append(r2)
            java.lang.String r9 = " not same as in map: "
            r8.append(r9)
            r8.append(r7)
            java.lang.String r8 = r8.toString()
            java.lang.String r9 = "ActivityManager"
            android.util.Slog.wtf(r9, r8)
            goto L_0x01fc
        L_0x0150:
            if (r18 == 0) goto L_0x01b3
            int r8 = r6.crashCount
            long r8 = (long) r8
            com.android.server.am.ActivityManagerService r10 = r1.mAm
            com.android.server.am.ActivityManagerConstants r10 = r10.mConstants
            long r10 = r10.BOUND_SERVICE_MAX_CRASH_RETRY
            int r8 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r8 < 0) goto L_0x01b3
            android.content.pm.ServiceInfo r8 = r6.serviceInfo
            android.content.pm.ApplicationInfo r8 = r8.applicationInfo
            int r8 = r8.flags
            r8 = r8 & 8
            if (r8 != 0) goto L_0x01b3
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "Service crashed "
            r8.append(r9)
            int r9 = r6.crashCount
            r8.append(r9)
            java.lang.String r9 = " times, stopping: "
            r8.append(r9)
            r8.append(r6)
            java.lang.String r8 = r8.toString()
            java.lang.String r9 = "ActivityManager"
            android.util.Slog.w(r9, r8)
            r8 = 30034(0x7552, float:4.2087E-41)
            r9 = 4
            java.lang.Object[] r9 = new java.lang.Object[r9]
            int r10 = r6.userId
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            r9[r0] = r10
            int r10 = r6.crashCount
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            r9[r4] = r10
            r10 = 2
            java.lang.String r11 = r6.shortInstanceName
            r9[r10] = r11
            r10 = 3
            int r11 = r2.pid
            java.lang.Integer r11 = java.lang.Integer.valueOf(r11)
            r9[r10] = r11
            android.util.EventLog.writeEvent(r8, r9)
            r1.bringDownServiceLocked(r6)
            goto L_0x01fc
        L_0x01b3:
            if (r18 == 0) goto L_0x01f9
            com.android.server.am.ActivityManagerService r8 = r1.mAm
            com.android.server.am.UserController r8 = r8.mUserController
            int r9 = r6.userId
            boolean r8 = r8.isUserRunning(r9, r0)
            if (r8 != 0) goto L_0x01c2
            goto L_0x01f9
        L_0x01c2:
            boolean r8 = r1.scheduleServiceRestartLocked(r6, r4)
            boolean r9 = r6.startRequested
            if (r9 == 0) goto L_0x01fc
            boolean r9 = r6.stopIfKilled
            if (r9 != 0) goto L_0x01d0
            if (r8 == 0) goto L_0x01fc
        L_0x01d0:
            java.util.ArrayList<com.android.server.am.ServiceRecord$StartItem> r9 = r6.pendingStarts
            int r9 = r9.size()
            if (r9 != 0) goto L_0x01fc
            r6.startRequested = r0
            com.android.internal.app.procstats.ServiceState r9 = r6.tracker
            if (r9 == 0) goto L_0x01ef
            com.android.internal.app.procstats.ServiceState r9 = r6.tracker
            com.android.server.am.ActivityManagerService r10 = r1.mAm
            com.android.server.am.ProcessStatsService r10 = r10.mProcessStats
            int r10 = r10.getMemFactorLocked()
            long r11 = android.os.SystemClock.uptimeMillis()
            r9.setStarted(r0, r10, r11)
        L_0x01ef:
            boolean r9 = r6.hasAutoCreateConnections()
            if (r9 != 0) goto L_0x01fc
            r1.bringDownServiceLocked(r6)
            goto L_0x01fc
        L_0x01f9:
            r1.bringDownServiceLocked(r6)
        L_0x01fc:
            int r5 = r5 + -1
            goto L_0x0102
        L_0x0200:
            if (r18 != 0) goto L_0x026e
            android.util.ArraySet<com.android.server.am.ServiceRecord> r0 = r2.f3services
            r0.clear()
            r17.clearBoundClientUids()
            java.util.ArrayList<com.android.server.am.ServiceRecord> r0 = r1.mRestartingServices
            int r0 = r0.size()
            int r0 = r0 - r4
        L_0x0211:
            if (r0 < 0) goto L_0x023f
            java.util.ArrayList<com.android.server.am.ServiceRecord> r5 = r1.mRestartingServices
            java.lang.Object r5 = r5.get(r0)
            com.android.server.am.ServiceRecord r5 = (com.android.server.am.ServiceRecord) r5
            java.lang.String r6 = r5.processName
            java.lang.String r7 = r2.processName
            boolean r6 = r6.equals(r7)
            if (r6 == 0) goto L_0x023c
            android.content.pm.ServiceInfo r6 = r5.serviceInfo
            android.content.pm.ApplicationInfo r6 = r6.applicationInfo
            int r6 = r6.uid
            android.content.pm.ApplicationInfo r7 = r2.info
            int r7 = r7.uid
            if (r6 != r7) goto L_0x023c
            java.util.ArrayList<com.android.server.am.ServiceRecord> r6 = r1.mRestartingServices
            r6.remove(r0)
            r5.resetRestartCounter()
            r1.clearRestartingIfNeededLocked(r5)
        L_0x023c:
            int r0 = r0 + -1
            goto L_0x0211
        L_0x023f:
            java.util.ArrayList<com.android.server.am.ServiceRecord> r0 = r1.mPendingServices
            int r0 = r0.size()
            int r0 = r0 - r4
        L_0x0246:
            if (r0 < 0) goto L_0x026e
            java.util.ArrayList<com.android.server.am.ServiceRecord> r4 = r1.mPendingServices
            java.lang.Object r4 = r4.get(r0)
            com.android.server.am.ServiceRecord r4 = (com.android.server.am.ServiceRecord) r4
            java.lang.String r5 = r4.processName
            java.lang.String r6 = r2.processName
            boolean r5 = r5.equals(r6)
            if (r5 == 0) goto L_0x026b
            android.content.pm.ServiceInfo r5 = r4.serviceInfo
            android.content.pm.ApplicationInfo r5 = r5.applicationInfo
            int r5 = r5.uid
            android.content.pm.ApplicationInfo r6 = r2.info
            int r6 = r6.uid
            if (r5 != r6) goto L_0x026b
            java.util.ArrayList<com.android.server.am.ServiceRecord> r5 = r1.mPendingServices
            r5.remove(r0)
        L_0x026b:
            int r0 = r0 + -1
            goto L_0x0246
        L_0x026e:
            java.util.ArrayList<com.android.server.am.ServiceRecord> r0 = r1.mDestroyingServices
            int r0 = r0.size()
        L_0x0274:
            if (r0 <= 0) goto L_0x028d
            int r0 = r0 + -1
            java.util.ArrayList<com.android.server.am.ServiceRecord> r4 = r1.mDestroyingServices
            java.lang.Object r4 = r4.get(r0)
            com.android.server.am.ServiceRecord r4 = (com.android.server.am.ServiceRecord) r4
            com.android.server.am.ProcessRecord r5 = r4.app
            if (r5 != r2) goto L_0x028c
            r4.forceClearTracker()
            java.util.ArrayList<com.android.server.am.ServiceRecord> r5 = r1.mDestroyingServices
            r5.remove(r0)
        L_0x028c:
            goto L_0x0274
        L_0x028d:
            android.util.ArraySet<com.android.server.am.ServiceRecord> r4 = r2.executingServices
            r4.clear()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ActiveServices.killServicesLocked(com.android.server.am.ProcessRecord, boolean):void");
    }

    /* access modifiers changed from: package-private */
    public ActivityManager.RunningServiceInfo makeRunningServiceInfoLocked(ServiceRecord r) {
        ActivityManager.RunningServiceInfo info = new ActivityManager.RunningServiceInfo();
        info.service = r.name;
        if (r.app != null) {
            info.pid = r.app.pid;
        }
        info.uid = r.appInfo.uid;
        info.process = r.processName;
        info.foreground = r.isForeground;
        info.activeSince = r.createRealTime;
        info.started = r.startRequested;
        info.clientCount = r.getConnections().size();
        info.crashCount = r.crashCount;
        info.lastActivityTime = r.lastActivity;
        if (r.isForeground) {
            info.flags |= 2;
        }
        if (r.startRequested) {
            info.flags |= 1;
        }
        if (r.app != null && r.app.pid == ActivityManagerService.MY_PID) {
            info.flags |= 4;
        }
        if (r.app != null && r.app.isPersistent()) {
            info.flags |= 8;
        }
        ArrayMap<IBinder, ArrayList<ConnectionRecord>> connections = r.getConnections();
        for (int conni = connections.size() - 1; conni >= 0; conni--) {
            ArrayList<ConnectionRecord> connl = connections.valueAt(conni);
            for (int i = 0; i < connl.size(); i++) {
                ConnectionRecord conn = connl.get(i);
                if (conn.clientLabel != 0) {
                    info.clientPackage = conn.binding.client.info.packageName;
                    info.clientLabel = conn.clientLabel;
                    return info;
                }
            }
        }
        return info;
    }

    /* access modifiers changed from: package-private */
    public List<ActivityManager.RunningServiceInfo> getRunningServiceInfoLocked(int maxNum, int flags, int callingUid, boolean allowed, boolean canInteractAcrossUsers) {
        ArrayList<ActivityManager.RunningServiceInfo> res = new ArrayList<>();
        long ident = Binder.clearCallingIdentity();
        int i = 0;
        if (canInteractAcrossUsers) {
            try {
                int[] users = this.mAm.mUserController.getUsers();
                for (int ui = 0; ui < users.length && res.size() < maxNum; ui++) {
                    ArrayMap<ComponentName, ServiceRecord> alls = getServicesLocked(users[ui]);
                    for (int i2 = 0; i2 < alls.size() && res.size() < maxNum; i2++) {
                        res.add(makeRunningServiceInfoLocked(alls.valueAt(i2)));
                    }
                }
                while (i < this.mRestartingServices.size() && res.size() < maxNum) {
                    ServiceRecord r = this.mRestartingServices.get(i);
                    ActivityManager.RunningServiceInfo info = makeRunningServiceInfoLocked(r);
                    info.restarting = r.nextRestartTime;
                    res.add(info);
                    i++;
                }
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(ident);
                throw th;
            }
        } else {
            int userId = UserHandle.getUserId(callingUid);
            ArrayMap<ComponentName, ServiceRecord> alls2 = getServicesLocked(userId);
            for (int i3 = 0; i3 < alls2.size() && res.size() < maxNum; i3++) {
                ServiceRecord sr = alls2.valueAt(i3);
                if (allowed || (sr.app != null && sr.app.uid == callingUid)) {
                    res.add(makeRunningServiceInfoLocked(sr));
                }
            }
            while (i < this.mRestartingServices.size() && res.size() < maxNum) {
                ServiceRecord r2 = this.mRestartingServices.get(i);
                if (r2.userId == userId && (allowed || (r2.app != null && r2.app.uid == callingUid))) {
                    ActivityManager.RunningServiceInfo info2 = makeRunningServiceInfoLocked(r2);
                    info2.restarting = r2.nextRestartTime;
                    res.add(info2);
                }
                i++;
            }
        }
        Binder.restoreCallingIdentity(ident);
        return res;
    }

    public PendingIntent getRunningServiceControlPanelLocked(ComponentName name) {
        ServiceRecord r = getServiceByNameLocked(name, UserHandle.getUserId(Binder.getCallingUid()));
        if (r == null) {
            return null;
        }
        ArrayMap<IBinder, ArrayList<ConnectionRecord>> connections = r.getConnections();
        for (int conni = connections.size() - 1; conni >= 0; conni--) {
            ArrayList<ConnectionRecord> conn = connections.valueAt(conni);
            for (int i = 0; i < conn.size(); i++) {
                if (conn.get(i).clientIntent != null) {
                    return conn.get(i).clientIntent;
                }
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00ef, code lost:
        com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00f2, code lost:
        if (r10 == null) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00f4, code lost:
        r18.appNotResponding((java.lang.String) null, (android.content.pm.ApplicationInfo) null, (java.lang.String) null, (com.android.server.wm.WindowProcessController) null, false, r10);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void serviceTimeout(com.android.server.am.ProcessRecord r18) {
        /*
            r17 = this;
            r1 = r17
            r9 = r18
            r2 = 0
            com.android.server.am.ActivityManagerService r3 = r1.mAm
            monitor-enter(r3)
            com.android.server.am.ActivityManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x0108 }
            boolean r0 = r18.isDebugging()     // Catch:{ all -> 0x0108 }
            if (r0 == 0) goto L_0x0016
            monitor-exit(r3)     // Catch:{ all -> 0x0108 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            return
        L_0x0016:
            android.util.ArraySet<com.android.server.am.ServiceRecord> r0 = r9.executingServices     // Catch:{ all -> 0x0108 }
            int r0 = r0.size()     // Catch:{ all -> 0x0108 }
            if (r0 == 0) goto L_0x0103
            android.app.IApplicationThread r0 = r9.thread     // Catch:{ all -> 0x0108 }
            if (r0 != 0) goto L_0x0024
            goto L_0x0103
        L_0x0024:
            long r4 = android.os.SystemClock.uptimeMillis()     // Catch:{ all -> 0x0108 }
            boolean r0 = r9.execServicesFg     // Catch:{ all -> 0x0108 }
            if (r0 == 0) goto L_0x0030
            r0 = 20000(0x4e20, float:2.8026E-41)
            goto L_0x0033
        L_0x0030:
            r0 = 200000(0x30d40, float:2.8026E-40)
        L_0x0033:
            long r6 = (long) r0     // Catch:{ all -> 0x0108 }
            long r6 = r4 - r6
            r0 = 0
            r10 = 0
            android.util.ArraySet<com.android.server.am.ServiceRecord> r8 = r9.executingServices     // Catch:{ all -> 0x0108 }
            int r8 = r8.size()     // Catch:{ all -> 0x0108 }
            int r8 = r8 + -1
        L_0x0041:
            if (r8 < 0) goto L_0x005f
            android.util.ArraySet<com.android.server.am.ServiceRecord> r12 = r9.executingServices     // Catch:{ all -> 0x0108 }
            java.lang.Object r12 = r12.valueAt(r8)     // Catch:{ all -> 0x0108 }
            com.android.server.am.ServiceRecord r12 = (com.android.server.am.ServiceRecord) r12     // Catch:{ all -> 0x0108 }
            long r13 = r12.executingStart     // Catch:{ all -> 0x0108 }
            int r13 = (r13 > r6 ? 1 : (r13 == r6 ? 0 : -1))
            if (r13 >= 0) goto L_0x0053
            r0 = r12
            goto L_0x005f
        L_0x0053:
            long r13 = r12.executingStart     // Catch:{ all -> 0x0108 }
            int r13 = (r13 > r10 ? 1 : (r13 == r10 ? 0 : -1))
            if (r13 <= 0) goto L_0x005c
            long r13 = r12.executingStart     // Catch:{ all -> 0x0108 }
            r10 = r13
        L_0x005c:
            int r8 = r8 + -1
            goto L_0x0041
        L_0x005f:
            if (r0 == 0) goto L_0x00cd
            com.android.server.am.ActivityManagerService r8 = r1.mAm     // Catch:{ all -> 0x0108 }
            com.android.server.am.ProcessList r8 = r8.mProcessList     // Catch:{ all -> 0x0108 }
            java.util.ArrayList<com.android.server.am.ProcessRecord> r8 = r8.mLruProcesses     // Catch:{ all -> 0x0108 }
            boolean r8 = r8.contains(r9)     // Catch:{ all -> 0x0108 }
            if (r8 == 0) goto L_0x00cd
            java.lang.String r8 = "ActivityManager"
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ all -> 0x0108 }
            r12.<init>()     // Catch:{ all -> 0x0108 }
            java.lang.String r13 = "Timeout executing service: "
            r12.append(r13)     // Catch:{ all -> 0x0108 }
            r12.append(r0)     // Catch:{ all -> 0x0108 }
            java.lang.String r12 = r12.toString()     // Catch:{ all -> 0x0108 }
            android.util.Slog.w(r8, r12)     // Catch:{ all -> 0x0108 }
            java.io.StringWriter r8 = new java.io.StringWriter     // Catch:{ all -> 0x0108 }
            r8.<init>()     // Catch:{ all -> 0x0108 }
            com.android.internal.util.FastPrintWriter r12 = new com.android.internal.util.FastPrintWriter     // Catch:{ all -> 0x0108 }
            r13 = 0
            r14 = 1024(0x400, float:1.435E-42)
            r12.<init>(r8, r13, r14)     // Catch:{ all -> 0x0108 }
            r12.println(r0)     // Catch:{ all -> 0x0108 }
            java.lang.String r13 = "    "
            r0.dump(r12, r13)     // Catch:{ all -> 0x0108 }
            r12.close()     // Catch:{ all -> 0x0108 }
            java.lang.String r13 = r8.toString()     // Catch:{ all -> 0x0108 }
            r1.mLastAnrDump = r13     // Catch:{ all -> 0x0108 }
            com.android.server.am.ActivityManagerService r13 = r1.mAm     // Catch:{ all -> 0x0108 }
            com.android.server.am.ActivityManagerService$MainHandler r13 = r13.mHandler     // Catch:{ all -> 0x0108 }
            java.lang.Runnable r14 = r1.mLastAnrDumpClearer     // Catch:{ all -> 0x0108 }
            r13.removeCallbacks(r14)     // Catch:{ all -> 0x0108 }
            com.android.server.am.ActivityManagerService r13 = r1.mAm     // Catch:{ all -> 0x0108 }
            com.android.server.am.ActivityManagerService$MainHandler r13 = r13.mHandler     // Catch:{ all -> 0x0108 }
            java.lang.Runnable r14 = r1.mLastAnrDumpClearer     // Catch:{ all -> 0x0108 }
            r15 = r4
            r4 = 7200000(0x6ddd00, double:3.5572727E-317)
            r13.postDelayed(r14, r4)     // Catch:{ all -> 0x0108 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0108 }
            r4.<init>()     // Catch:{ all -> 0x0108 }
            java.lang.String r5 = "executing service "
            r4.append(r5)     // Catch:{ all -> 0x0108 }
            java.lang.String r5 = r0.shortInstanceName     // Catch:{ all -> 0x0108 }
            r4.append(r5)     // Catch:{ all -> 0x0108 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0108 }
            r2 = r4
            r10 = r2
            goto L_0x00ee
        L_0x00cd:
            r15 = r4
            com.android.server.am.ActivityManagerService r4 = r1.mAm     // Catch:{ all -> 0x0108 }
            com.android.server.am.ActivityManagerService$MainHandler r4 = r4.mHandler     // Catch:{ all -> 0x0108 }
            r5 = 12
            android.os.Message r4 = r4.obtainMessage(r5)     // Catch:{ all -> 0x0108 }
            r4.obj = r9     // Catch:{ all -> 0x0108 }
            com.android.server.am.ActivityManagerService r5 = r1.mAm     // Catch:{ all -> 0x0108 }
            com.android.server.am.ActivityManagerService$MainHandler r5 = r5.mHandler     // Catch:{ all -> 0x0108 }
            boolean r8 = r9.execServicesFg     // Catch:{ all -> 0x0108 }
            if (r8 == 0) goto L_0x00e6
            r12 = 20000(0x4e20, double:9.8813E-320)
            long r12 = r12 + r10
            goto L_0x00ea
        L_0x00e6:
            r12 = 200000(0x30d40, double:9.8813E-319)
            long r12 = r12 + r10
        L_0x00ea:
            r5.sendMessageAtTime(r4, r12)     // Catch:{ all -> 0x0108 }
            r10 = r2
        L_0x00ee:
            monitor-exit(r3)     // Catch:{ all -> 0x0100 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            if (r10 == 0) goto L_0x00ff
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r2 = r18
            r8 = r10
            r2.appNotResponding(r3, r4, r5, r6, r7, r8)
        L_0x00ff:
            return
        L_0x0100:
            r0 = move-exception
            r2 = r10
            goto L_0x0109
        L_0x0103:
            monitor-exit(r3)     // Catch:{ all -> 0x0108 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            return
        L_0x0108:
            r0 = move-exception
        L_0x0109:
            monitor-exit(r3)     // Catch:{ all -> 0x0108 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ActiveServices.serviceTimeout(com.android.server.am.ProcessRecord):void");
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0025, code lost:
        com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0028, code lost:
        if (r1 == null) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x002a, code lost:
        r1.appNotResponding((java.lang.String) null, (android.content.pm.ApplicationInfo) null, (java.lang.String) null, (com.android.server.wm.WindowProcessController) null, false, "Context.startForegroundService() did not then call Service.startForeground(): " + r10);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void serviceForegroundTimeout(com.android.server.am.ServiceRecord r10) {
        /*
            r9 = this;
            com.android.server.am.ActivityManagerService r0 = r9.mAm
            monitor-enter(r0)
            com.android.server.am.ActivityManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x004a }
            boolean r1 = r10.fgRequired     // Catch:{ all -> 0x004a }
            if (r1 == 0) goto L_0x0045
            boolean r1 = r10.destroying     // Catch:{ all -> 0x004a }
            if (r1 == 0) goto L_0x000f
            goto L_0x0045
        L_0x000f:
            com.android.server.am.ProcessRecord r1 = r10.app     // Catch:{ all -> 0x004a }
            if (r1 == 0) goto L_0x001e
            boolean r2 = r1.isDebugging()     // Catch:{ all -> 0x004a }
            if (r2 == 0) goto L_0x001e
            monitor-exit(r0)     // Catch:{ all -> 0x004a }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            return
        L_0x001e:
            r2 = 0
            r10.fgWaiting = r2     // Catch:{ all -> 0x004a }
            r9.stopServiceLocked(r10)     // Catch:{ all -> 0x004a }
            monitor-exit(r0)     // Catch:{ all -> 0x004a }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            if (r1 == 0) goto L_0x0044
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "Context.startForegroundService() did not then call Service.startForeground(): "
            r0.append(r2)
            r0.append(r10)
            java.lang.String r8 = r0.toString()
            r2 = r1
            r2.appNotResponding(r3, r4, r5, r6, r7, r8)
        L_0x0044:
            return
        L_0x0045:
            monitor-exit(r0)     // Catch:{ all -> 0x004a }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            return
        L_0x004a:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x004a }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ActiveServices.serviceForegroundTimeout(com.android.server.am.ServiceRecord):void");
    }

    public void updateServiceApplicationInfoLocked(ApplicationInfo applicationInfo) {
        ServiceMap serviceMap = this.mServiceMap.get(UserHandle.getUserId(applicationInfo.uid));
        if (serviceMap != null) {
            ArrayMap<ComponentName, ServiceRecord> servicesByName = serviceMap.mServicesByInstanceName;
            for (int j = servicesByName.size() - 1; j >= 0; j--) {
                ServiceRecord serviceRecord = servicesByName.valueAt(j);
                if (applicationInfo.packageName.equals(serviceRecord.appInfo.packageName)) {
                    serviceRecord.appInfo = applicationInfo;
                    serviceRecord.serviceInfo.applicationInfo = applicationInfo;
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void serviceForegroundCrash(ProcessRecord app, CharSequence serviceRecord) {
        ActivityManagerService activityManagerService = this.mAm;
        int i = app.uid;
        int i2 = app.pid;
        String str = app.info.packageName;
        int i3 = app.userId;
        activityManagerService.crashApplication(i, i2, str, i3, "Context.startForegroundService() did not then call Service.startForeground(): " + serviceRecord);
    }

    /* access modifiers changed from: package-private */
    public void scheduleServiceTimeoutLocked(ProcessRecord proc) {
        if (proc.executingServices.size() != 0 && proc.thread != null) {
            Message msg = this.mAm.mHandler.obtainMessage(12);
            msg.obj = proc;
            if (!this.mAm.mSystemMainLooperReady && "system".equals(proc.processName)) {
                msg.arg1 = 1;
            }
            this.mAm.mHandler.sendMessageDelayed(msg, proc.execServicesFg ? ActivityManagerServiceInjector.KEEP_FOREGROUND_DURATION : 200000);
        }
    }

    /* access modifiers changed from: package-private */
    public void scheduleServiceForegroundTransitionTimeoutLocked(ServiceRecord r) {
        if (r.app.executingServices.size() != 0 && r.app.thread != null) {
            Message msg = this.mAm.mHandler.obtainMessage(66);
            msg.obj = r;
            r.fgWaiting = true;
            this.mAm.mHandler.sendMessageDelayed(msg, JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY);
        }
    }

    final class ServiceDumper {
        private final String[] args;
        private final boolean dumpAll;
        private final String dumpPackage;
        private final FileDescriptor fd;
        private final ActivityManagerService.ItemMatcher matcher;
        private boolean needSep;
        private final long nowReal = SystemClock.elapsedRealtime();
        private boolean printed;
        private boolean printedAnything;
        private final PrintWriter pw;

        /* renamed from: services  reason: collision with root package name */
        private final ArrayList<ServiceRecord> f2services = new ArrayList<>();
        final /* synthetic */ ActiveServices this$0;

        ServiceDumper(ActiveServices this$02, FileDescriptor fd2, PrintWriter pw2, String[] args2, int opti, boolean dumpAll2, String dumpPackage2) {
            ActiveServices activeServices = this$02;
            String[] strArr = args2;
            String str = dumpPackage2;
            this.this$0 = activeServices;
            int i = 0;
            this.needSep = false;
            this.printedAnything = false;
            this.printed = false;
            this.fd = fd2;
            this.pw = pw2;
            this.args = strArr;
            this.dumpAll = dumpAll2;
            this.dumpPackage = str;
            this.matcher = new ActivityManagerService.ItemMatcher();
            this.matcher.build(strArr, opti);
            int[] users = activeServices.mAm.mUserController.getUsers();
            int length = users.length;
            while (i < length) {
                ServiceMap smap = activeServices.getServiceMapLocked(users[i]);
                if (smap.mServicesByInstanceName.size() > 0) {
                    int si = 0;
                    while (si < smap.mServicesByInstanceName.size()) {
                        ServiceRecord r = smap.mServicesByInstanceName.valueAt(si);
                        if (this.matcher.match(r, r.name) && (str == null || str.equals(r.appInfo.packageName))) {
                            this.f2services.add(r);
                        }
                        si++;
                        ActiveServices activeServices2 = this$02;
                    }
                }
                i++;
                activeServices = this$02;
            }
        }

        private void dumpHeaderLocked() {
            this.pw.println("ACTIVITY MANAGER SERVICES (dumpsys activity services)");
            if (this.this$0.mLastAnrDump != null) {
                this.pw.println("  Last ANR service:");
                this.pw.print(this.this$0.mLastAnrDump);
                this.pw.println();
            }
        }

        /* access modifiers changed from: package-private */
        public void dumpLocked() {
            dumpHeaderLocked();
            try {
                int[] users = this.this$0.mAm.mUserController.getUsers();
                int length = users.length;
                for (int i = 0; i < length; i++) {
                    int user = users[i];
                    int serviceIdx = 0;
                    while (serviceIdx < this.f2services.size() && this.f2services.get(serviceIdx).userId != user) {
                        serviceIdx++;
                    }
                    this.printed = false;
                    if (serviceIdx < this.f2services.size()) {
                        this.needSep = false;
                        while (true) {
                            if (serviceIdx >= this.f2services.size()) {
                                break;
                            }
                            ServiceRecord r = this.f2services.get(serviceIdx);
                            serviceIdx++;
                            if (r.userId != user) {
                                break;
                            }
                            dumpServiceLocalLocked(r);
                        }
                        this.needSep |= this.printed;
                    }
                    dumpUserRemainsLocked(user);
                }
            } catch (Exception e) {
                Slog.w("ActivityManager", "Exception in dumpServicesLocked", e);
            }
            dumpRemainsLocked();
        }

        /* Debug info: failed to restart local var, previous not found, register: 8 */
        /* access modifiers changed from: package-private */
        public void dumpWithClient() {
            synchronized (this.this$0.mAm) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    dumpHeaderLocked();
                } finally {
                    while (true) {
                        ActivityManagerService.resetPriorityAfterLockedSection();
                    }
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
            try {
                int[] users = this.this$0.mAm.mUserController.getUsers();
                int length = users.length;
                for (int i = 0; i < length; i++) {
                    int user = users[i];
                    int serviceIdx = 0;
                    while (serviceIdx < this.f2services.size() && this.f2services.get(serviceIdx).userId != user) {
                        serviceIdx++;
                    }
                    this.printed = false;
                    if (serviceIdx < this.f2services.size()) {
                        this.needSep = false;
                        while (true) {
                            if (serviceIdx >= this.f2services.size()) {
                                break;
                            }
                            ServiceRecord r = this.f2services.get(serviceIdx);
                            serviceIdx++;
                            if (r.userId != user) {
                                break;
                            }
                            synchronized (this.this$0.mAm) {
                                ActivityManagerService.boostPriorityForLockedSection();
                                dumpServiceLocalLocked(r);
                            }
                            ActivityManagerService.resetPriorityAfterLockedSection();
                            dumpServiceClient(r);
                        }
                        this.needSep |= this.printed;
                    }
                    synchronized (this.this$0.mAm) {
                        ActivityManagerService.boostPriorityForLockedSection();
                        dumpUserRemainsLocked(user);
                    }
                    ActivityManagerService.resetPriorityAfterLockedSection();
                }
            } catch (Exception e) {
                Slog.w("ActivityManager", "Exception in dumpServicesLocked", e);
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            synchronized (this.this$0.mAm) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    dumpRemainsLocked();
                } catch (Throwable th2) {
                    while (true) {
                        throw th2;
                    }
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
        }

        private void dumpUserHeaderLocked(int user) {
            if (!this.printed) {
                if (this.printedAnything) {
                    this.pw.println();
                }
                PrintWriter printWriter = this.pw;
                printWriter.println("  User " + user + " active services:");
                this.printed = true;
            }
            this.printedAnything = true;
            if (this.needSep) {
                this.pw.println();
            }
        }

        private void dumpServiceLocalLocked(ServiceRecord r) {
            dumpUserHeaderLocked(r.userId);
            this.pw.print("  * ");
            this.pw.println(r);
            if (this.dumpAll) {
                r.dump(this.pw, "    ");
                this.needSep = true;
                return;
            }
            this.pw.print("    app=");
            this.pw.println(r.app);
            this.pw.print("    created=");
            TimeUtils.formatDuration(r.createRealTime, this.nowReal, this.pw);
            this.pw.print(" started=");
            this.pw.print(r.startRequested);
            this.pw.print(" connections=");
            ArrayMap<IBinder, ArrayList<ConnectionRecord>> connections = r.getConnections();
            this.pw.println(connections.size());
            if (connections.size() > 0) {
                this.pw.println("    Connections:");
                for (int conni = 0; conni < connections.size(); conni++) {
                    ArrayList<ConnectionRecord> clist = connections.valueAt(conni);
                    for (int i = 0; i < clist.size(); i++) {
                        ConnectionRecord conn = clist.get(i);
                        this.pw.print("      ");
                        this.pw.print(conn.binding.intent.intent.getIntent().toShortString(false, false, false, false));
                        this.pw.print(" -> ");
                        ProcessRecord proc = conn.binding.client;
                        this.pw.println(proc != null ? proc.toShortString() : "null");
                    }
                }
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 6 */
        private void dumpServiceClient(ServiceRecord r) {
            IApplicationThread thread;
            TransferPipe tp;
            ProcessRecord proc = r.app;
            if (proc != null && (thread = proc.thread) != null) {
                this.pw.println("    Client:");
                this.pw.flush();
                try {
                    tp = new TransferPipe();
                    thread.dumpService(tp.getWriteFd(), r, this.args);
                    tp.setBufferPrefix("      ");
                    tp.go(this.fd, 2000);
                    tp.kill();
                } catch (IOException e) {
                    PrintWriter printWriter = this.pw;
                    printWriter.println("      Failure while dumping the service: " + e);
                } catch (RemoteException e2) {
                    this.pw.println("      Got a RemoteException while dumping the service");
                } catch (Throwable th) {
                    tp.kill();
                    throw th;
                }
                this.needSep = true;
            }
        }

        private void dumpUserRemainsLocked(int user) {
            String str;
            String str2;
            ServiceMap smap = this.this$0.getServiceMapLocked(user);
            this.printed = false;
            int SN = smap.mDelayedStartList.size();
            for (int si = 0; si < SN; si++) {
                ServiceRecord r = smap.mDelayedStartList.get(si);
                if (this.matcher.match(r, r.name) && ((str2 = this.dumpPackage) == null || str2.equals(r.appInfo.packageName))) {
                    if (!this.printed) {
                        if (this.printedAnything) {
                            this.pw.println();
                        }
                        PrintWriter printWriter = this.pw;
                        printWriter.println("  User " + user + " delayed start services:");
                        this.printed = true;
                    }
                    this.printedAnything = true;
                    this.pw.print("  * Delayed start ");
                    this.pw.println(r);
                }
            }
            this.printed = false;
            int SN2 = smap.mStartingBackground.size();
            for (int si2 = 0; si2 < SN2; si2++) {
                ServiceRecord r2 = smap.mStartingBackground.get(si2);
                if (this.matcher.match(r2, r2.name) && ((str = this.dumpPackage) == null || str.equals(r2.appInfo.packageName))) {
                    if (!this.printed) {
                        if (this.printedAnything) {
                            this.pw.println();
                        }
                        PrintWriter printWriter2 = this.pw;
                        printWriter2.println("  User " + user + " starting in background:");
                        this.printed = true;
                    }
                    this.printedAnything = true;
                    this.pw.print("  * Starting bg ");
                    this.pw.println(r2);
                }
            }
        }

        private void dumpRemainsLocked() {
            String str;
            String str2;
            String str3;
            if (this.this$0.mPendingServices.size() > 0) {
                this.printed = false;
                for (int i = 0; i < this.this$0.mPendingServices.size(); i++) {
                    ServiceRecord r = this.this$0.mPendingServices.get(i);
                    if (this.matcher.match(r, r.name) && ((str3 = this.dumpPackage) == null || str3.equals(r.appInfo.packageName))) {
                        this.printedAnything = true;
                        if (!this.printed) {
                            if (this.needSep) {
                                this.pw.println();
                            }
                            this.needSep = true;
                            this.pw.println("  Pending services:");
                            this.printed = true;
                        }
                        this.pw.print("  * Pending ");
                        this.pw.println(r);
                        r.dump(this.pw, "    ");
                    }
                }
                this.needSep = true;
            }
            if (this.this$0.mRestartingServices.size() > 0) {
                this.printed = false;
                for (int i2 = 0; i2 < this.this$0.mRestartingServices.size(); i2++) {
                    ServiceRecord r2 = this.this$0.mRestartingServices.get(i2);
                    if (this.matcher.match(r2, r2.name) && ((str2 = this.dumpPackage) == null || str2.equals(r2.appInfo.packageName))) {
                        this.printedAnything = true;
                        if (!this.printed) {
                            if (this.needSep) {
                                this.pw.println();
                            }
                            this.needSep = true;
                            this.pw.println("  Restarting services:");
                            this.printed = true;
                        }
                        this.pw.print("  * Restarting ");
                        this.pw.println(r2);
                        r2.dump(this.pw, "    ");
                    }
                }
                this.needSep = true;
            }
            if (this.this$0.mDestroyingServices.size() > 0) {
                this.printed = false;
                for (int i3 = 0; i3 < this.this$0.mDestroyingServices.size(); i3++) {
                    ServiceRecord r3 = this.this$0.mDestroyingServices.get(i3);
                    if (this.matcher.match(r3, r3.name) && ((str = this.dumpPackage) == null || str.equals(r3.appInfo.packageName))) {
                        this.printedAnything = true;
                        if (!this.printed) {
                            if (this.needSep) {
                                this.pw.println();
                            }
                            this.needSep = true;
                            this.pw.println("  Destroying services:");
                            this.printed = true;
                        }
                        this.pw.print("  * Destroy ");
                        this.pw.println(r3);
                        r3.dump(this.pw, "    ");
                    }
                }
                this.needSep = true;
            }
            if (this.dumpAll) {
                this.printed = false;
                for (int ic = 0; ic < this.this$0.mServiceConnections.size(); ic++) {
                    ArrayList<ConnectionRecord> r4 = this.this$0.mServiceConnections.valueAt(ic);
                    for (int i4 = 0; i4 < r4.size(); i4++) {
                        ConnectionRecord cr = r4.get(i4);
                        if (this.matcher.match(cr.binding.service, cr.binding.service.name) && (this.dumpPackage == null || (cr.binding.client != null && this.dumpPackage.equals(cr.binding.client.info.packageName)))) {
                            this.printedAnything = true;
                            if (!this.printed) {
                                if (this.needSep) {
                                    this.pw.println();
                                }
                                this.needSep = true;
                                this.pw.println("  Connection bindings to services:");
                                this.printed = true;
                            }
                            this.pw.print("  * ");
                            this.pw.println(cr);
                            cr.dump(this.pw, "    ");
                        }
                    }
                }
            }
            if (this.matcher.all) {
                long nowElapsed = SystemClock.elapsedRealtime();
                for (int user : this.this$0.mAm.mUserController.getUsers()) {
                    boolean printedUser = false;
                    ServiceMap smap = this.this$0.mServiceMap.get(user);
                    if (smap != null) {
                        for (int i5 = smap.mActiveForegroundApps.size() - 1; i5 >= 0; i5--) {
                            ActiveForegroundApp aa = smap.mActiveForegroundApps.valueAt(i5);
                            String str4 = this.dumpPackage;
                            if (str4 == null || str4.equals(aa.mPackageName)) {
                                if (!printedUser) {
                                    printedUser = true;
                                    this.printedAnything = true;
                                    if (this.needSep) {
                                        this.pw.println();
                                    }
                                    this.needSep = true;
                                    this.pw.print("Active foreground apps - user ");
                                    this.pw.print(user);
                                    this.pw.println(":");
                                }
                                this.pw.print("  #");
                                this.pw.print(i5);
                                this.pw.print(": ");
                                this.pw.println(aa.mPackageName);
                                if (aa.mLabel != null) {
                                    this.pw.print("    mLabel=");
                                    this.pw.println(aa.mLabel);
                                }
                                this.pw.print("    mNumActive=");
                                this.pw.print(aa.mNumActive);
                                this.pw.print(" mAppOnTop=");
                                this.pw.print(aa.mAppOnTop);
                                this.pw.print(" mShownWhileTop=");
                                this.pw.print(aa.mShownWhileTop);
                                this.pw.print(" mShownWhileScreenOn=");
                                this.pw.println(aa.mShownWhileScreenOn);
                                this.pw.print("    mStartTime=");
                                TimeUtils.formatDuration(aa.mStartTime - nowElapsed, this.pw);
                                this.pw.print(" mStartVisibleTime=");
                                TimeUtils.formatDuration(aa.mStartVisibleTime - nowElapsed, this.pw);
                                this.pw.println();
                                if (aa.mEndTime != 0) {
                                    this.pw.print("    mEndTime=");
                                    TimeUtils.formatDuration(aa.mEndTime - nowElapsed, this.pw);
                                    this.pw.println();
                                }
                            }
                        }
                        if (smap.hasMessagesOrCallbacks() != 0) {
                            if (this.needSep) {
                                this.pw.println();
                            }
                            this.printedAnything = true;
                            this.needSep = true;
                            this.pw.print("  Handler - user ");
                            this.pw.print(user);
                            this.pw.println(":");
                            smap.dumpMine(new PrintWriterPrinter(this.pw), "    ");
                        }
                    }
                }
            }
            if (!this.printedAnything) {
                this.pw.println("  (nothing)");
            }
        }
    }

    /* access modifiers changed from: package-private */
    public ServiceDumper newServiceDumperLocked(FileDescriptor fd, PrintWriter pw, String[] args, int opti, boolean dumpAll, String dumpPackage) {
        return new ServiceDumper(this, fd, pw, args, opti, dumpAll, dumpPackage);
    }

    /* access modifiers changed from: protected */
    public void writeToProto(ProtoOutputStream proto, long fieldId) {
        int i;
        ProtoOutputStream protoOutputStream = proto;
        synchronized (this.mAm) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                long outterToken = proto.start(fieldId);
                int[] users = this.mAm.mUserController.getUsers();
                int length = users.length;
                int i2 = 0;
                int i3 = 0;
                while (i3 < length) {
                    int user = users[i3];
                    ServiceMap smap = this.mServiceMap.get(user);
                    if (smap == null) {
                        i = i3;
                    } else {
                        long token = protoOutputStream.start(2246267895809L);
                        protoOutputStream.write(1120986464257L, user);
                        ArrayMap<ComponentName, ServiceRecord> alls = smap.mServicesByInstanceName;
                        int i4 = i2;
                        while (i4 < alls.size()) {
                            alls.valueAt(i4).writeToProto(protoOutputStream, 2246267895810L);
                            i4++;
                            i3 = i3;
                        }
                        i = i3;
                        protoOutputStream.end(token);
                    }
                    i3 = i + 1;
                    i2 = 0;
                }
                protoOutputStream.end(outterToken);
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
    }

    /* access modifiers changed from: protected */
    public boolean dumpService(FileDescriptor fd, PrintWriter pw, String name, String[] args, int opti, boolean dumpAll) {
        ArrayList arrayList = new ArrayList();
        Predicate<ServiceRecord> filter = DumpUtils.filterRecord(name);
        synchronized (this.mAm) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                for (int user : this.mAm.mUserController.getUsers()) {
                    ServiceMap smap = this.mServiceMap.get(user);
                    if (smap != null) {
                        ArrayMap<ComponentName, ServiceRecord> alls = smap.mServicesByInstanceName;
                        for (int i = 0; i < alls.size(); i++) {
                            ServiceRecord r1 = alls.valueAt(i);
                            if (filter.test(r1)) {
                                arrayList.add(r1);
                            }
                        }
                    }
                }
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
        if (arrayList.size() <= 0) {
            return false;
        }
        arrayList.sort(Comparator.comparing($$Lambda$Y_KRxxoOXfyYceuDG7WHd46Y_I.INSTANCE));
        boolean needSep = false;
        int i2 = 0;
        while (i2 < arrayList.size()) {
            if (needSep) {
                pw.println();
            }
            dumpService("", fd, pw, (ServiceRecord) arrayList.get(i2), args, dumpAll);
            i2++;
            needSep = true;
        }
        return true;
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    private void dumpService(String prefix, FileDescriptor fd, PrintWriter pw, ServiceRecord r, String[] args, boolean dumpAll) {
        TransferPipe tp;
        String innerPrefix = prefix + "  ";
        synchronized (this.mAm) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                pw.print(prefix);
                pw.print("SERVICE ");
                pw.print(r.shortInstanceName);
                pw.print(" ");
                pw.print(Integer.toHexString(System.identityHashCode(r)));
                pw.print(" pid=");
                if (r.app != null) {
                    pw.println(r.app.pid);
                } else {
                    pw.println("(not running)");
                }
                if (dumpAll) {
                    r.dump(pw, innerPrefix);
                }
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
        if (r.app != null && r.app.thread != null) {
            pw.print(prefix);
            pw.println("  Client:");
            pw.flush();
            try {
                tp = new TransferPipe();
                r.app.thread.dumpService(tp.getWriteFd(), r, args);
                tp.setBufferPrefix(prefix + "    ");
                tp.go(fd);
                tp.kill();
            } catch (IOException e) {
                pw.println(prefix + "    Failure while dumping the service: " + e);
            } catch (RemoteException e2) {
                pw.println(prefix + "    Got a RemoteException while dumping the service");
            } catch (Throwable th2) {
                tp.kill();
                throw th2;
            }
        }
    }
}
