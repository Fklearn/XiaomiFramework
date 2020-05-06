package com.android.server.content;

import android.accounts.Account;
import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.app.job.JobInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.IContentService;
import android.content.ISyncStatusObserver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.PeriodicSync;
import android.content.SyncAdapterType;
import android.content.SyncInfo;
import android.content.SyncRequest;
import android.content.SyncStatusInfo;
import android.content.pm.PackageManagerInternal;
import android.content.pm.ProviderInfo;
import android.database.IContentObserver;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.FactoryTest;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.os.BinderDeathDispatcher;
import com.android.internal.util.ArrayUtils;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.content.SyncStorageEngine;
import com.android.server.pm.Settings;
import com.android.server.slice.SliceClientPermissions;
import com.android.server.wm.ActivityTaskManagerInternal;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public final class ContentService extends IContentService.Stub {
    static final boolean DEBUG = false;
    static final String TAG = "ContentService";
    private static final int TOO_MANY_OBSERVERS_THRESHOLD = 1000;
    /* access modifiers changed from: private */
    public static final BinderDeathDispatcher<IContentObserver> sObserverDeathDispatcher = new BinderDeathDispatcher<>();
    /* access modifiers changed from: private */
    @GuardedBy({"sObserverLeakDetectedUid"})
    public static final ArraySet<Integer> sObserverLeakDetectedUid = new ArraySet<>(0);
    /* access modifiers changed from: private */
    @GuardedBy({"mCache"})
    public final SparseArray<ArrayMap<String, ArrayMap<Pair<String, Uri>, Bundle>>> mCache = new SparseArray<>();
    private BroadcastReceiver mCacheReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            synchronized (ContentService.this.mCache) {
                if ("android.intent.action.LOCALE_CHANGED".equals(intent.getAction())) {
                    ContentService.this.mCache.clear();
                } else {
                    Uri data = intent.getData();
                    if (data != null) {
                        ContentService.this.invalidateCacheLocked(intent.getIntExtra("android.intent.extra.user_handle", ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION), data.getSchemeSpecificPart(), (Uri) null);
                    }
                }
            }
        }
    };
    private Context mContext;
    private boolean mFactoryTest;
    private final ObserverNode mRootNode = new ObserverNode("");
    private SyncManager mSyncManager = null;
    private final Object mSyncManagerLock = new Object();

    public static class Lifecycle extends SystemService {
        private ContentService mService;

        public Lifecycle(Context context) {
            super(context);
        }

        /* JADX WARNING: type inference failed for: r1v3, types: [com.android.server.content.ContentService, android.os.IBinder] */
        public void onStart() {
            boolean factoryTest = true;
            if (FactoryTest.getMode() != 1) {
                factoryTest = false;
            }
            this.mService = new ContentService(getContext(), factoryTest);
            publishBinderService(ActivityTaskManagerInternal.ASSIST_KEY_CONTENT, this.mService);
        }

        public void onBootPhase(int phase) {
            this.mService.onBootPhase(phase);
        }

        public void onStartUser(int userHandle) {
            this.mService.onStartUser(userHandle);
        }

        public void onUnlockUser(int userHandle) {
            this.mService.onUnlockUser(userHandle);
        }

        public void onStopUser(int userHandle) {
            this.mService.onStopUser(userHandle);
        }

        public void onCleanupUser(int userHandle) {
            synchronized (this.mService.mCache) {
                this.mService.mCache.remove(userHandle);
            }
        }
    }

    private SyncManager getSyncManager() {
        SyncManager syncManager;
        synchronized (this.mSyncManagerLock) {
            try {
                if (this.mSyncManager == null) {
                    this.mSyncManager = new SyncManager(this.mContext, this.mFactoryTest);
                }
            } catch (SQLiteException e) {
                Log.e(TAG, "Can't create SyncManager", e);
            }
            syncManager = this.mSyncManager;
        }
        return syncManager;
    }

    /* access modifiers changed from: package-private */
    public void onStartUser(int userHandle) {
        SyncManager syncManager = this.mSyncManager;
        if (syncManager != null) {
            syncManager.onStartUser(userHandle);
        }
    }

    /* access modifiers changed from: package-private */
    public void onUnlockUser(int userHandle) {
        SyncManager syncManager = this.mSyncManager;
        if (syncManager != null) {
            syncManager.onUnlockUser(userHandle);
        }
    }

    /* access modifiers changed from: package-private */
    public void onStopUser(int userHandle) {
        SyncManager syncManager = this.mSyncManager;
        if (syncManager != null) {
            syncManager.onStopUser(userHandle);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 18 */
    /*  JADX ERROR: IndexOutOfBoundsException in pass: RegionMakerVisitor
        java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        	at java.util.ArrayList.rangeCheck(ArrayList.java:657)
        	at java.util.ArrayList.get(ArrayList.java:433)
        	at jadx.core.dex.nodes.InsnNode.getArg(InsnNode.java:101)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:611)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:698)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:123)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:598)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:49)
        */
    protected synchronized void dump(java.io.FileDescriptor r19, java.io.PrintWriter r20, java.lang.String[] r21) {
        /*
            r18 = this;
            r1 = r18
            r2 = r20
            monitor-enter(r18)
            android.content.Context r0 = r1.mContext     // Catch:{ all -> 0x0159 }
            java.lang.String r3 = "ContentService"
            boolean r0 = com.android.internal.util.DumpUtils.checkDumpAndUsageStatsPermission(r0, r3, r2)     // Catch:{ all -> 0x0159 }
            if (r0 != 0) goto L_0x0011
            monitor-exit(r18)
            return
        L_0x0011:
            com.android.internal.util.IndentingPrintWriter r0 = new com.android.internal.util.IndentingPrintWriter     // Catch:{ all -> 0x0159 }
            java.lang.String r3 = "  "
            r0.<init>(r2, r3)     // Catch:{ all -> 0x0159 }
            r3 = r0
            java.lang.String r0 = "-a"
            r12 = r21
            boolean r0 = com.android.internal.util.ArrayUtils.contains(r12, r0)     // Catch:{ all -> 0x0159 }
            r13 = r0
            long r4 = clearCallingIdentity()     // Catch:{ all -> 0x0159 }
            r14 = r4
            com.android.server.content.SyncManager r0 = r1.mSyncManager     // Catch:{ all -> 0x0154 }
            if (r0 != 0) goto L_0x0033
            java.lang.String r0 = "SyncManager not available yet"
            r3.println(r0)     // Catch:{ all -> 0x0154 }
            r11 = r19
            goto L_0x003a
        L_0x0033:
            com.android.server.content.SyncManager r0 = r1.mSyncManager     // Catch:{ all -> 0x0154 }
            r11 = r19
            r0.dump(r11, r3, r13)     // Catch:{ all -> 0x0154 }
        L_0x003a:
            r3.println()     // Catch:{ all -> 0x0154 }
            java.lang.String r0 = "Observer tree:"
            r3.println(r0)     // Catch:{ all -> 0x0154 }
            com.android.server.content.ContentService$ObserverNode r10 = r1.mRootNode     // Catch:{ all -> 0x0154 }
            monitor-enter(r10)     // Catch:{ all -> 0x0154 }
            r0 = 2
            int[] r0 = new int[r0]     // Catch:{ all -> 0x014d }
            android.util.SparseIntArray r4 = new android.util.SparseIntArray     // Catch:{ all -> 0x014d }
            r4.<init>()     // Catch:{ all -> 0x014d }
            r9 = r4
            com.android.server.content.ContentService$ObserverNode r4 = r1.mRootNode     // Catch:{ all -> 0x014d }
            java.lang.String r8 = ""
            java.lang.String r16 = "  "
            r5 = r19
            r6 = r3
            r7 = r21
            r17 = r9
            r9 = r16
            r16 = r10
            r10 = r0
            r11 = r17
            r4.dumpLocked(r5, r6, r7, r8, r9, r10, r11)     // Catch:{ all -> 0x0152 }
            r3.println()     // Catch:{ all -> 0x0152 }
            java.util.ArrayList r4 = new java.util.ArrayList     // Catch:{ all -> 0x0152 }
            r4.<init>()     // Catch:{ all -> 0x0152 }
            r5 = 0
            r6 = r5
        L_0x006f:
            int r7 = r17.size()     // Catch:{ all -> 0x0152 }
            if (r6 >= r7) goto L_0x0087
            r7 = r17
            int r8 = r7.keyAt(r6)     // Catch:{ all -> 0x0152 }
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)     // Catch:{ all -> 0x0152 }
            r4.add(r8)     // Catch:{ all -> 0x0152 }
            int r6 = r6 + 1
            r17 = r7
            goto L_0x006f
        L_0x0087:
            r7 = r17
            com.android.server.content.ContentService$2 r6 = new com.android.server.content.ContentService$2     // Catch:{ all -> 0x0152 }
            r6.<init>(r7)     // Catch:{ all -> 0x0152 }
            java.util.Collections.sort(r4, r6)     // Catch:{ all -> 0x0152 }
            r6 = r5
        L_0x0092:
            int r8 = r4.size()     // Catch:{ all -> 0x0152 }
            if (r6 >= r8) goto L_0x00be
            java.lang.Object r8 = r4.get(r6)     // Catch:{ all -> 0x0152 }
            java.lang.Integer r8 = (java.lang.Integer) r8     // Catch:{ all -> 0x0152 }
            int r8 = r8.intValue()     // Catch:{ all -> 0x0152 }
            java.lang.String r9 = "  pid "
            r3.print(r9)     // Catch:{ all -> 0x0152 }
            r3.print(r8)     // Catch:{ all -> 0x0152 }
            java.lang.String r9 = ": "
            r3.print(r9)     // Catch:{ all -> 0x0152 }
            int r9 = r7.get(r8)     // Catch:{ all -> 0x0152 }
            r3.print(r9)     // Catch:{ all -> 0x0152 }
            java.lang.String r9 = " observers"
            r3.println(r9)     // Catch:{ all -> 0x0152 }
            int r6 = r6 + 1
            goto L_0x0092
        L_0x00be:
            r3.println()     // Catch:{ all -> 0x0152 }
            java.lang.String r6 = " Total number of nodes: "
            r3.print(r6)     // Catch:{ all -> 0x0152 }
            r6 = r0[r5]     // Catch:{ all -> 0x0152 }
            r3.println(r6)     // Catch:{ all -> 0x0152 }
            java.lang.String r6 = " Total number of observers: "
            r3.print(r6)     // Catch:{ all -> 0x0152 }
            r6 = 1
            r6 = r0[r6]     // Catch:{ all -> 0x0152 }
            r3.println(r6)     // Catch:{ all -> 0x0152 }
            com.android.internal.os.BinderDeathDispatcher<android.database.IContentObserver> r6 = sObserverDeathDispatcher     // Catch:{ all -> 0x0152 }
            java.lang.String r8 = " "
            r6.dump(r3, r8)     // Catch:{ all -> 0x0152 }
            monitor-exit(r16)     // Catch:{ all -> 0x0152 }
            android.util.ArraySet<java.lang.Integer> r4 = sObserverLeakDetectedUid     // Catch:{ all -> 0x0154 }
            monitor-enter(r4)     // Catch:{ all -> 0x0154 }
            r3.println()     // Catch:{ all -> 0x014a }
            java.lang.String r0 = "Observer leaking UIDs: "
            r3.print(r0)     // Catch:{ all -> 0x014a }
            android.util.ArraySet<java.lang.Integer> r0 = sObserverLeakDetectedUid     // Catch:{ all -> 0x014a }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x014a }
            r3.println(r0)     // Catch:{ all -> 0x014a }
            monitor-exit(r4)     // Catch:{ all -> 0x014a }
            android.util.SparseArray<android.util.ArrayMap<java.lang.String, android.util.ArrayMap<android.util.Pair<java.lang.String, android.net.Uri>, android.os.Bundle>>> r4 = r1.mCache     // Catch:{ all -> 0x0154 }
            monitor-enter(r4)     // Catch:{ all -> 0x0154 }
            r3.println()     // Catch:{ all -> 0x0145 }
            java.lang.String r0 = "Cached content:"
            r3.println(r0)     // Catch:{ all -> 0x0145 }
            r3.increaseIndent()     // Catch:{ all -> 0x0145 }
            r0 = r5
        L_0x0102:
            android.util.SparseArray<android.util.ArrayMap<java.lang.String, android.util.ArrayMap<android.util.Pair<java.lang.String, android.net.Uri>, android.os.Bundle>>> r5 = r1.mCache     // Catch:{ all -> 0x0145 }
            int r5 = r5.size()     // Catch:{ all -> 0x0145 }
            if (r0 >= r5) goto L_0x013b
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0148 }
            r5.<init>()     // Catch:{ all -> 0x0148 }
            java.lang.String r6 = "User "
            r5.append(r6)     // Catch:{ all -> 0x0148 }
            android.util.SparseArray<android.util.ArrayMap<java.lang.String, android.util.ArrayMap<android.util.Pair<java.lang.String, android.net.Uri>, android.os.Bundle>>> r6 = r1.mCache     // Catch:{ all -> 0x0148 }
            int r6 = r6.keyAt(r0)     // Catch:{ all -> 0x0148 }
            r5.append(r6)     // Catch:{ all -> 0x0148 }
            java.lang.String r6 = ":"
            r5.append(r6)     // Catch:{ all -> 0x0148 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0148 }
            r3.println(r5)     // Catch:{ all -> 0x0148 }
            r3.increaseIndent()     // Catch:{ all -> 0x0148 }
            android.util.SparseArray<android.util.ArrayMap<java.lang.String, android.util.ArrayMap<android.util.Pair<java.lang.String, android.net.Uri>, android.os.Bundle>>> r5 = r1.mCache     // Catch:{ all -> 0x0148 }
            java.lang.Object r5 = r5.valueAt(r0)     // Catch:{ all -> 0x0148 }
            r3.println(r5)     // Catch:{ all -> 0x0148 }
            r3.decreaseIndent()     // Catch:{ all -> 0x0148 }
            int r0 = r0 + 1
            goto L_0x0102
        L_0x013b:
            r3.decreaseIndent()     // Catch:{ all -> 0x0145 }
            monitor-exit(r4)     // Catch:{ all -> 0x0145 }
            restoreCallingIdentity(r14)     // Catch:{ all -> 0x0159 }
            monitor-exit(r18)
            return
        L_0x0145:
            r0 = move-exception
        L_0x0146:
            monitor-exit(r4)     // Catch:{ all -> 0x0148 }
            throw r0     // Catch:{ all -> 0x0154 }
        L_0x0148:
            r0 = move-exception
            goto L_0x0146
        L_0x014a:
            r0 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x014a }
            throw r0     // Catch:{ all -> 0x0154 }
        L_0x014d:
            r0 = move-exception
            r16 = r10
        L_0x0150:
            monitor-exit(r16)     // Catch:{ all -> 0x0152 }
            throw r0     // Catch:{ all -> 0x0154 }
        L_0x0152:
            r0 = move-exception
            goto L_0x0150
        L_0x0154:
            r0 = move-exception
            restoreCallingIdentity(r14)     // Catch:{ all -> 0x0159 }
            throw r0     // Catch:{ all -> 0x0159 }
        L_0x0159:
            r0 = move-exception
            monitor-exit(r18)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.ContentService.dump(java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[]):void");
    }

    ContentService(Context context, boolean factoryTest) {
        this.mContext = context;
        this.mFactoryTest = factoryTest;
        ((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class)).setSyncAdapterPackagesprovider(new PackageManagerInternal.SyncAdapterPackagesProvider() {
            public String[] getPackages(String authority, int userId) {
                return ContentService.this.getSyncAdapterPackagesForAuthorityAsUser(authority, userId);
            }
        });
        IntentFilter packageFilter = new IntentFilter();
        packageFilter.addAction("android.intent.action.PACKAGE_ADDED");
        packageFilter.addAction("android.intent.action.PACKAGE_CHANGED");
        packageFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        packageFilter.addAction("android.intent.action.PACKAGE_DATA_CLEARED");
        packageFilter.addDataScheme(Settings.ATTR_PACKAGE);
        this.mContext.registerReceiverAsUser(this.mCacheReceiver, UserHandle.ALL, packageFilter, (String) null, (Handler) null);
        IntentFilter localeFilter = new IntentFilter();
        localeFilter.addAction("android.intent.action.LOCALE_CHANGED");
        this.mContext.registerReceiverAsUser(this.mCacheReceiver, UserHandle.ALL, localeFilter, (String) null, (Handler) null);
    }

    /* access modifiers changed from: package-private */
    public void onBootPhase(int phase) {
        if (phase == 550) {
            getSyncManager();
        }
        SyncManager syncManager = this.mSyncManager;
        if (syncManager != null) {
            syncManager.onBootPhase(phase);
        }
    }

    public void registerContentObserver(Uri uri, boolean notifyForDescendants, IContentObserver observer, int userHandle, int targetSdkVersion) {
        Uri uri2 = uri;
        if (observer == null || uri2 == null) {
            int i = targetSdkVersion;
            throw new IllegalArgumentException("You must pass a valid uri and observer");
        }
        int uid = Binder.getCallingUid();
        int pid = Binder.getCallingPid();
        int userHandle2 = handleIncomingUser(uri, pid, uid, 1, true, userHandle);
        String msg = ((ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class)).checkContentProviderAccess(uri.getAuthority(), userHandle2);
        if (msg == null) {
            int i2 = targetSdkVersion;
        } else if (targetSdkVersion >= 26) {
            throw new SecurityException(msg);
        } else if (!msg.startsWith("Failed to find provider")) {
            Log.w(TAG, "Ignoring content changes for " + uri2 + " from " + uid + ": " + msg);
            return;
        }
        synchronized (this.mRootNode) {
            try {
                int i3 = uid;
                this.mRootNode.addObserverLocked(uri, observer, notifyForDescendants, this.mRootNode, uid, pid, userHandle2);
            } catch (Throwable th) {
                th = th;
                throw th;
            }
        }
    }

    public void registerContentObserver(Uri uri, boolean notifyForDescendants, IContentObserver observer) {
        registerContentObserver(uri, notifyForDescendants, observer, UserHandle.getCallingUserId(), 10000);
    }

    public void unregisterContentObserver(IContentObserver observer) {
        if (observer != null) {
            synchronized (this.mRootNode) {
                this.mRootNode.removeObserverLocked(observer);
            }
            return;
        }
        throw new IllegalArgumentException("You must pass a valid observer");
    }

    /* Debug info: failed to restart local var, previous not found, register: 22 */
    /*  JADX ERROR: IndexOutOfBoundsException in pass: RegionMakerVisitor
        java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        	at java.util.ArrayList.rangeCheck(ArrayList.java:657)
        	at java.util.ArrayList.get(ArrayList.java:433)
        	at jadx.core.dex.nodes.InsnNode.getArg(InsnNode.java:101)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:611)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processExcHandler(RegionMaker.java:1043)
        	at jadx.core.dex.visitors.regions.RegionMaker.processTryCatchBlocks(RegionMaker.java:975)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:52)
        */
    public void notifyChange(android.net.Uri r23, android.database.IContentObserver r24, boolean r25, int r26, int r27, int r28, java.lang.String r29) {
        /*
            r22 = this;
            r8 = r22
            r15 = r23
            if (r15 == 0) goto L_0x01ae
            int r14 = android.os.Binder.getCallingUid()
            int r18 = android.os.Binder.getCallingPid()
            int r19 = android.os.UserHandle.getCallingUserId()
            r5 = 2
            r6 = 1
            r1 = r22
            r2 = r23
            r3 = r18
            r4 = r14
            r7 = r27
            int r1 = r1.handleIncomingUser(r2, r3, r4, r5, r6, r7)
            java.lang.Class<android.app.ActivityManagerInternal> r0 = android.app.ActivityManagerInternal.class
            java.lang.Object r0 = com.android.server.LocalServices.getService(r0)
            android.app.ActivityManagerInternal r0 = (android.app.ActivityManagerInternal) r0
            java.lang.String r2 = r23.getAuthority()
            java.lang.String r2 = r0.checkContentProviderAccess(r2, r1)
            if (r2 == 0) goto L_0x006f
            r0 = 26
            r3 = r28
            if (r3 >= r0) goto L_0x0069
            java.lang.String r0 = "Failed to find provider"
            boolean r0 = r2.startsWith(r0)
            if (r0 == 0) goto L_0x0042
            goto L_0x0071
        L_0x0042:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r4 = "Ignoring notify for "
            r0.append(r4)
            r0.append(r15)
            java.lang.String r4 = " from "
            r0.append(r4)
            r0.append(r14)
            java.lang.String r4 = ": "
            r0.append(r4)
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            java.lang.String r4 = "ContentService"
            android.util.Log.w(r4, r0)
            return
        L_0x0069:
            java.lang.SecurityException r0 = new java.lang.SecurityException
            r0.<init>(r2)
            throw r0
        L_0x006f:
            r3 = r28
        L_0x0071:
            long r4 = clearCallingIdentity()
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ all -> 0x01a5 }
            r0.<init>()     // Catch:{ all -> 0x01a5 }
            r6 = r0
            com.android.server.content.ContentService$ObserverNode r7 = r8.mRootNode     // Catch:{ all -> 0x01a5 }
            monitor-enter(r7)     // Catch:{ all -> 0x01a5 }
            com.android.server.content.ContentService$ObserverNode r9 = r8.mRootNode     // Catch:{ all -> 0x0198 }
            r11 = 0
            r10 = r23
            r12 = r24
            r13 = r25
            r20 = r14
            r14 = r26
            r15 = r1
            r16 = r6
            r9.collectObserversLocked(r10, r11, r12, r13, r14, r15, r16)     // Catch:{ all -> 0x018e }
            monitor-exit(r7)     // Catch:{ all -> 0x018e }
            int r0 = r6.size()     // Catch:{ all -> 0x0186 }
            r7 = r0
            r0 = 0
            r9 = r0
        L_0x0099:
            if (r9 >= r7) goto L_0x013b
            java.lang.Object r0 = r6.get(r9)     // Catch:{ all -> 0x0132 }
            com.android.server.content.ContentService$ObserverCall r0 = (com.android.server.content.ContentService.ObserverCall) r0     // Catch:{ all -> 0x0132 }
            r10 = r0
            android.database.IContentObserver r0 = r10.mObserver     // Catch:{ RemoteException -> 0x00c4, all -> 0x00bb }
            boolean r11 = r10.mSelfChange     // Catch:{ RemoteException -> 0x00c4, all -> 0x00bb }
            r15 = r23
            r0.onChange(r11, r15, r1)     // Catch:{ RemoteException -> 0x00b9, all -> 0x00b1 }
            r27 = r2
            r21 = r6
            goto L_0x0114
        L_0x00b1:
            r0 = move-exception
            r27 = r2
            r3 = r15
            r2 = r20
            goto L_0x01aa
        L_0x00b9:
            r0 = move-exception
            goto L_0x00c7
        L_0x00bb:
            r0 = move-exception
            r3 = r23
            r27 = r2
            r2 = r20
            goto L_0x01aa
        L_0x00c4:
            r0 = move-exception
            r15 = r23
        L_0x00c7:
            r11 = r0
            com.android.server.content.ContentService$ObserverNode r12 = r8.mRootNode     // Catch:{ all -> 0x012d }
            monitor-enter(r12)     // Catch:{ all -> 0x012d }
            java.lang.String r0 = "ContentService"
            java.lang.String r13 = "Found dead observer, removing"
            android.util.Log.w(r0, r13)     // Catch:{ all -> 0x011e }
            android.database.IContentObserver r0 = r10.mObserver     // Catch:{ all -> 0x011e }
            android.os.IBinder r0 = r0.asBinder()     // Catch:{ all -> 0x011e }
            com.android.server.content.ContentService$ObserverNode r13 = r10.mNode     // Catch:{ all -> 0x011e }
            java.util.ArrayList r13 = r13.mObservers     // Catch:{ all -> 0x011e }
            int r14 = r13.size()     // Catch:{ all -> 0x011e }
            r16 = 0
            r27 = r2
            r2 = r14
            r14 = r16
        L_0x00e9:
            if (r14 >= r2) goto L_0x0111
            java.lang.Object r16 = r13.get(r14)     // Catch:{ all -> 0x010d }
            com.android.server.content.ContentService$ObserverNode$ObserverEntry r16 = (com.android.server.content.ContentService.ObserverNode.ObserverEntry) r16     // Catch:{ all -> 0x010d }
            r17 = r16
            r21 = r6
            r3 = r17
            android.database.IContentObserver r6 = r3.observer     // Catch:{ all -> 0x012b }
            android.os.IBinder r6 = r6.asBinder()     // Catch:{ all -> 0x012b }
            if (r6 != r0) goto L_0x0106
            r13.remove(r14)     // Catch:{ all -> 0x012b }
            int r14 = r14 + -1
            int r2 = r2 + -1
        L_0x0106:
            int r14 = r14 + 1
            r3 = r28
            r6 = r21
            goto L_0x00e9
        L_0x010d:
            r0 = move-exception
            r21 = r6
            goto L_0x0123
        L_0x0111:
            r21 = r6
            monitor-exit(r12)     // Catch:{ all -> 0x012b }
        L_0x0114:
            int r9 = r9 + 1
            r2 = r27
            r3 = r28
            r6 = r21
            goto L_0x0099
        L_0x011e:
            r0 = move-exception
            r27 = r2
            r21 = r6
        L_0x0123:
            monitor-exit(r12)     // Catch:{ all -> 0x012b }
            throw r0     // Catch:{ all -> 0x0125 }
        L_0x0125:
            r0 = move-exception
            r3 = r15
            r2 = r20
            goto L_0x01aa
        L_0x012b:
            r0 = move-exception
            goto L_0x0123
        L_0x012d:
            r0 = move-exception
            r27 = r2
            r3 = r15
            goto L_0x0137
        L_0x0132:
            r0 = move-exception
            r27 = r2
            r3 = r23
        L_0x0137:
            r2 = r20
            goto L_0x01aa
        L_0x013b:
            r15 = r23
            r27 = r2
            r21 = r6
            r0 = r26 & 1
            if (r0 == 0) goto L_0x0170
            com.android.server.content.SyncManager r0 = r22.getSyncManager()     // Catch:{ all -> 0x016b }
            if (r0 == 0) goto L_0x0167
            r10 = 0
            java.lang.String r13 = r23.getAuthority()     // Catch:{ all -> 0x016b }
            r2 = r20
            int r14 = r8.getSyncExemptionForCaller(r2)     // Catch:{ all -> 0x0164 }
            r9 = r0
            r11 = r19
            r12 = r2
            r3 = r15
            r15 = r2
            r16 = r18
            r17 = r29
            r9.scheduleLocalSync(r10, r11, r12, r13, r14, r15, r16, r17)     // Catch:{ all -> 0x01a1 }
            goto L_0x0173
        L_0x0164:
            r0 = move-exception
            r3 = r15
            goto L_0x01aa
        L_0x0167:
            r3 = r15
            r2 = r20
            goto L_0x0173
        L_0x016b:
            r0 = move-exception
            r3 = r15
            r2 = r20
            goto L_0x01aa
        L_0x0170:
            r3 = r15
            r2 = r20
        L_0x0173:
            android.util.SparseArray<android.util.ArrayMap<java.lang.String, android.util.ArrayMap<android.util.Pair<java.lang.String, android.net.Uri>, android.os.Bundle>>> r6 = r8.mCache     // Catch:{ all -> 0x01a1 }
            monitor-enter(r6)     // Catch:{ all -> 0x01a1 }
            java.lang.String r0 = r22.getProviderPackageName(r23)     // Catch:{ all -> 0x0183 }
            r8.invalidateCacheLocked(r1, r0, r3)     // Catch:{ all -> 0x0183 }
            monitor-exit(r6)     // Catch:{ all -> 0x0183 }
            restoreCallingIdentity(r4)
            return
        L_0x0183:
            r0 = move-exception
            monitor-exit(r6)     // Catch:{ all -> 0x0183 }
            throw r0     // Catch:{ all -> 0x01a1 }
        L_0x0186:
            r0 = move-exception
            r3 = r23
            r27 = r2
            r2 = r20
            goto L_0x01aa
        L_0x018e:
            r0 = move-exception
            r3 = r23
            r27 = r2
            r21 = r6
            r2 = r20
            goto L_0x019f
        L_0x0198:
            r0 = move-exception
            r27 = r2
            r21 = r6
            r2 = r14
            r3 = r15
        L_0x019f:
            monitor-exit(r7)     // Catch:{ all -> 0x01a3 }
            throw r0     // Catch:{ all -> 0x01a1 }
        L_0x01a1:
            r0 = move-exception
            goto L_0x01aa
        L_0x01a3:
            r0 = move-exception
            goto L_0x019f
        L_0x01a5:
            r0 = move-exception
            r27 = r2
            r2 = r14
            r3 = r15
        L_0x01aa:
            restoreCallingIdentity(r4)
            throw r0
        L_0x01ae:
            r3 = r15
            java.lang.NullPointerException r0 = new java.lang.NullPointerException
            java.lang.String r1 = "Uri must not be null"
            r0.<init>(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.ContentService.notifyChange(android.net.Uri, android.database.IContentObserver, boolean, int, int, int, java.lang.String):void");
    }

    private int checkUriPermission(Uri uri, int pid, int uid, int modeFlags, int userHandle) {
        try {
            return ActivityManager.getService().checkUriPermission(uri, pid, uid, modeFlags, userHandle, (IBinder) null);
        } catch (RemoteException e) {
            return -1;
        }
    }

    public void notifyChange(Uri uri, IContentObserver observer, boolean observerWantsSelfNotifications, boolean syncToNetwork, String callingPackage) {
        notifyChange(uri, observer, observerWantsSelfNotifications, syncToNetwork ? 1 : 0, UserHandle.getCallingUserId(), 10000, callingPackage);
    }

    public static final class ObserverCall {
        final ObserverNode mNode;
        final IContentObserver mObserver;
        final int mObserverUserId;
        final boolean mSelfChange;

        ObserverCall(ObserverNode node, IContentObserver observer, boolean selfChange, int observerUserId) {
            this.mNode = node;
            this.mObserver = observer;
            this.mSelfChange = selfChange;
            this.mObserverUserId = observerUserId;
        }
    }

    public void requestSync(Account account, String authority, Bundle extras, String callingPackage) {
        Bundle bundle = extras;
        if (SyncSecurityInjector.permitControlSyncForAccount(this.mContext, account)) {
            Bundle.setDefusable(bundle, true);
            ContentResolver.validateSyncExtrasBundle(extras);
            int userId = UserHandle.getCallingUserId();
            int callingUid = Binder.getCallingUid();
            int callingPid = Binder.getCallingPid();
            validateExtras(callingUid, bundle);
            int syncExemption = getSyncExemptionAndCleanUpExtrasForCaller(callingUid, bundle);
            long identityToken = clearCallingIdentity();
            try {
                SyncManager syncManager = getSyncManager();
                if (syncManager != null) {
                    int i = callingUid;
                    try {
                        syncManager.scheduleSync(account, userId, callingUid, authority, extras, -2, syncExemption, callingUid, callingPid, callingPackage);
                    } catch (Throwable th) {
                        th = th;
                    }
                }
                restoreCallingIdentity(identityToken);
            } catch (Throwable th2) {
                th = th2;
                int i2 = callingUid;
                restoreCallingIdentity(identityToken);
                throw th;
            }
        }
    }

    public void sync(SyncRequest request, String callingPackage) {
        syncAsUser(request, UserHandle.getCallingUserId(), callingPackage);
    }

    private long clampPeriod(long period) {
        long minPeriod = JobInfo.getMinPeriodMillis() / 1000;
        if (period >= minPeriod) {
            return period;
        }
        Slog.w(TAG, "Requested poll frequency of " + period + " seconds being rounded up to " + minPeriod + "s.");
        return minPeriod;
    }

    public void syncAsUser(SyncRequest request, int userId, String callingPackage) {
        int i = userId;
        if (SyncSecurityInjector.permitControlSyncForAccount(this.mContext, request.getAccount())) {
            enforceCrossUserPermission(i, "no permission to request sync as user: " + i);
            int callingUid = Binder.getCallingUid();
            int callingPid = Binder.getCallingPid();
            Bundle extras = request.getBundle();
            validateExtras(callingUid, extras);
            int syncExemption = getSyncExemptionAndCleanUpExtrasForCaller(callingUid, extras);
            long identityToken = clearCallingIdentity();
            try {
                SyncManager syncManager = getSyncManager();
                if (syncManager == null) {
                    restoreCallingIdentity(identityToken);
                    return;
                }
                long flextime = request.getSyncFlexTime();
                long runAtTime = request.getSyncRunTime();
                if (request.isPeriodic()) {
                    try {
                        this.mContext.enforceCallingOrSelfPermission("android.permission.WRITE_SYNC_SETTINGS", "no permission to write the sync settings");
                        long runAtTime2 = clampPeriod(runAtTime);
                        getSyncManager().updateOrAddPeriodicSync(new SyncStorageEngine.EndPoint(request.getAccount(), request.getProvider(), i), runAtTime2, flextime, extras);
                        Bundle bundle = extras;
                    } catch (Throwable th) {
                        th = th;
                        Bundle bundle2 = extras;
                        restoreCallingIdentity(identityToken);
                        throw th;
                    }
                } else {
                    long j = runAtTime;
                    Bundle bundle3 = extras;
                    try {
                        syncManager.scheduleSync(request.getAccount(), userId, callingUid, request.getProvider(), extras, -2, syncExemption, callingUid, callingPid, callingPackage);
                    } catch (Throwable th2) {
                        th = th2;
                        restoreCallingIdentity(identityToken);
                        throw th;
                    }
                }
                restoreCallingIdentity(identityToken);
            } catch (Throwable th3) {
                th = th3;
                Bundle bundle4 = extras;
                restoreCallingIdentity(identityToken);
                throw th;
            }
        }
    }

    public void cancelSync(Account account, String authority, ComponentName cname) {
        cancelSyncAsUser(account, authority, cname, UserHandle.getCallingUserId());
    }

    public void cancelSyncAsUser(Account account, String authority, ComponentName cname, int userId) {
        if (cname == null && !SyncSecurityInjector.permitControlSyncForAccount(this.mContext, account)) {
            return;
        }
        if (authority == null || authority.length() != 0) {
            enforceCrossUserPermission(userId, "no permission to modify the sync settings for user " + userId);
            long identityToken = clearCallingIdentity();
            if (cname != null) {
                Slog.e(TAG, "cname not null.");
                return;
            }
            try {
                SyncManager syncManager = getSyncManager();
                if (syncManager != null) {
                    SyncStorageEngine.EndPoint info = new SyncStorageEngine.EndPoint(account, authority, userId);
                    syncManager.clearScheduledSyncOperations(info);
                    syncManager.cancelActiveSync(info, (Bundle) null, "API");
                }
            } finally {
                restoreCallingIdentity(identityToken);
            }
        } else {
            throw new IllegalArgumentException("Authority must be non-empty");
        }
    }

    public void cancelRequest(SyncRequest request) {
        SyncManager syncManager;
        if (SyncSecurityInjector.permitControlSyncForAccount(this.mContext, request.getAccount()) && (syncManager = getSyncManager()) != null) {
            int userId = UserHandle.getCallingUserId();
            int callingUid = Binder.getCallingUid();
            if (request.isPeriodic()) {
                this.mContext.enforceCallingOrSelfPermission("android.permission.WRITE_SYNC_SETTINGS", "no permission to write the sync settings");
            }
            Bundle extras = new Bundle(request.getBundle());
            validateExtras(callingUid, extras);
            long identityToken = clearCallingIdentity();
            try {
                SyncStorageEngine.EndPoint info = new SyncStorageEngine.EndPoint(request.getAccount(), request.getProvider(), userId);
                if (request.isPeriodic()) {
                    SyncManager syncManager2 = getSyncManager();
                    syncManager2.removePeriodicSync(info, extras, "cancelRequest() by uid=" + callingUid);
                }
                syncManager.cancelScheduledSyncOperation(info, extras);
                syncManager.cancelActiveSync(info, extras, "API");
            } finally {
                restoreCallingIdentity(identityToken);
            }
        }
    }

    public SyncAdapterType[] getSyncAdapterTypes() {
        return getSyncAdapterTypesAsUser(UserHandle.getCallingUserId());
    }

    public SyncAdapterType[] getSyncAdapterTypesAsUser(int userId) {
        enforceCrossUserPermission(userId, "no permission to read sync settings for user " + userId);
        long identityToken = clearCallingIdentity();
        try {
            return getSyncManager().getSyncAdapterTypes(userId);
        } finally {
            restoreCallingIdentity(identityToken);
        }
    }

    public String[] getSyncAdapterPackagesForAuthorityAsUser(String authority, int userId) {
        enforceCrossUserPermission(userId, "no permission to read sync settings for user " + userId);
        long identityToken = clearCallingIdentity();
        try {
            return getSyncManager().getSyncAdapterPackagesForAuthorityAsUser(authority, userId);
        } finally {
            restoreCallingIdentity(identityToken);
        }
    }

    public boolean getSyncAutomatically(Account account, String providerName) {
        return getSyncAutomaticallyAsUser(account, providerName, UserHandle.getCallingUserId());
    }

    public boolean getSyncAutomaticallyAsUser(Account account, String providerName, int userId) {
        enforceCrossUserPermission(userId, "no permission to read the sync settings for user " + userId);
        this.mContext.enforceCallingOrSelfPermission("android.permission.READ_SYNC_SETTINGS", "no permission to read the sync settings");
        long identityToken = clearCallingIdentity();
        try {
            SyncManager syncManager = getSyncManager();
            if (syncManager != null) {
                return syncManager.getSyncStorageEngine().getSyncAutomatically(account, userId, providerName);
            }
            restoreCallingIdentity(identityToken);
            return false;
        } finally {
            restoreCallingIdentity(identityToken);
        }
    }

    public void setSyncAutomatically(Account account, String providerName, boolean sync) {
        setSyncAutomaticallyAsUser(account, providerName, sync, UserHandle.getCallingUserId());
    }

    public void setSyncAutomaticallyAsUser(Account account, String providerName, boolean sync, int userId) {
        int i = userId;
        if (SyncSecurityInjector.permitControlSyncForAccount(this.mContext, account)) {
            if (!TextUtils.isEmpty(providerName)) {
                this.mContext.enforceCallingOrSelfPermission("android.permission.WRITE_SYNC_SETTINGS", "no permission to write the sync settings");
                enforceCrossUserPermission(i, "no permission to modify the sync settings for user " + i);
                int callingUid = Binder.getCallingUid();
                int callingPid = Binder.getCallingPid();
                int syncExemptionFlag = getSyncExemptionForCaller(callingUid);
                long identityToken = clearCallingIdentity();
                try {
                    SyncManager syncManager = getSyncManager();
                    if (syncManager != null) {
                        syncManager.getSyncStorageEngine().setSyncAutomatically(account, userId, providerName, sync, syncExemptionFlag, callingUid, callingPid);
                    }
                } finally {
                    restoreCallingIdentity(identityToken);
                }
            } else {
                throw new IllegalArgumentException("Authority must be non-empty");
            }
        }
    }

    public void addPeriodicSync(Account account, String authority, Bundle extras, long pollFrequency) {
        Account account2 = account;
        Bundle bundle = extras;
        if (SyncSecurityInjector.permitControlSyncForAccount(this.mContext, account2)) {
            Bundle.setDefusable(bundle, true);
            if (account2 == null) {
                long j = pollFrequency;
                throw new IllegalArgumentException("Account must not be null");
            } else if (!TextUtils.isEmpty(authority)) {
                this.mContext.enforceCallingOrSelfPermission("android.permission.WRITE_SYNC_SETTINGS", "no permission to write the sync settings");
                validateExtras(Binder.getCallingUid(), bundle);
                int userId = UserHandle.getCallingUserId();
                long pollFrequency2 = clampPeriod(pollFrequency);
                long defaultFlex = SyncStorageEngine.calculateDefaultFlexTime(pollFrequency2);
                long identityToken = clearCallingIdentity();
                try {
                    getSyncManager().updateOrAddPeriodicSync(new SyncStorageEngine.EndPoint(account2, authority, userId), pollFrequency2, defaultFlex, extras);
                } finally {
                    restoreCallingIdentity(identityToken);
                }
            } else {
                long j2 = pollFrequency;
                throw new IllegalArgumentException("Authority must not be empty.");
            }
        }
    }

    public void removePeriodicSync(Account account, String authority, Bundle extras) {
        if (SyncSecurityInjector.permitControlSyncForAccount(this.mContext, account)) {
            Bundle.setDefusable(extras, true);
            if (account == null) {
                throw new IllegalArgumentException("Account must not be null");
            } else if (!TextUtils.isEmpty(authority)) {
                this.mContext.enforceCallingOrSelfPermission("android.permission.WRITE_SYNC_SETTINGS", "no permission to write the sync settings");
                validateExtras(Binder.getCallingUid(), extras);
                int callingUid = Binder.getCallingUid();
                int userId = UserHandle.getCallingUserId();
                long identityToken = clearCallingIdentity();
                try {
                    SyncManager syncManager = getSyncManager();
                    SyncStorageEngine.EndPoint endPoint = new SyncStorageEngine.EndPoint(account, authority, userId);
                    syncManager.removePeriodicSync(endPoint, extras, "removePeriodicSync() by uid=" + callingUid);
                } finally {
                    restoreCallingIdentity(identityToken);
                }
            } else {
                throw new IllegalArgumentException("Authority must not be empty");
            }
        }
    }

    public List<PeriodicSync> getPeriodicSyncs(Account account, String providerName, ComponentName cname) {
        if (account == null) {
            throw new IllegalArgumentException("Account must not be null");
        } else if (!TextUtils.isEmpty(providerName)) {
            this.mContext.enforceCallingOrSelfPermission("android.permission.READ_SYNC_SETTINGS", "no permission to read the sync settings");
            int userId = UserHandle.getCallingUserId();
            long identityToken = clearCallingIdentity();
            try {
                return getSyncManager().getPeriodicSyncs(new SyncStorageEngine.EndPoint(account, providerName, userId));
            } finally {
                restoreCallingIdentity(identityToken);
            }
        } else {
            throw new IllegalArgumentException("Authority must not be empty");
        }
    }

    public int getIsSyncable(Account account, String providerName) {
        return getIsSyncableAsUser(account, providerName, UserHandle.getCallingUserId());
    }

    public int getIsSyncableAsUser(Account account, String providerName, int userId) {
        enforceCrossUserPermission(userId, "no permission to read the sync settings for user " + userId);
        this.mContext.enforceCallingOrSelfPermission("android.permission.READ_SYNC_SETTINGS", "no permission to read the sync settings");
        long identityToken = clearCallingIdentity();
        try {
            SyncManager syncManager = getSyncManager();
            if (syncManager != null) {
                return syncManager.computeSyncable(account, userId, providerName, false);
            }
            restoreCallingIdentity(identityToken);
            return -1;
        } finally {
            restoreCallingIdentity(identityToken);
        }
    }

    public void setIsSyncable(Account account, String providerName, int syncable) {
        setIsSyncableAsUser(account, providerName, syncable, UserHandle.getCallingUserId());
    }

    public void setIsSyncableAsUser(Account account, String providerName, int syncable, int userId) {
        int i = userId;
        if (SyncSecurityInjector.permitControlSyncForAccount(this.mContext, account)) {
            if (!TextUtils.isEmpty(providerName)) {
                enforceCrossUserPermission(i, "no permission to set the sync settings for user " + i);
                this.mContext.enforceCallingOrSelfPermission("android.permission.WRITE_SYNC_SETTINGS", "no permission to write the sync settings");
                int syncable2 = normalizeSyncable(syncable);
                int callingUid = Binder.getCallingUid();
                int callingPid = Binder.getCallingPid();
                long identityToken = clearCallingIdentity();
                try {
                    SyncManager syncManager = getSyncManager();
                    if (syncManager != null) {
                        syncManager.getSyncStorageEngine().setIsSyncable(account, userId, providerName, syncable2, callingUid, callingPid);
                    }
                } finally {
                    restoreCallingIdentity(identityToken);
                }
            } else {
                throw new IllegalArgumentException("Authority must not be empty");
            }
        }
    }

    public boolean getMasterSyncAutomatically() {
        return getMasterSyncAutomaticallyAsUser(UserHandle.getCallingUserId());
    }

    public boolean getMasterSyncAutomaticallyAsUser(int userId) {
        enforceCrossUserPermission(userId, "no permission to read the sync settings for user " + userId);
        this.mContext.enforceCallingOrSelfPermission("android.permission.READ_SYNC_SETTINGS", "no permission to read the sync settings");
        long identityToken = clearCallingIdentity();
        try {
            SyncManager syncManager = getSyncManager();
            if (syncManager != null) {
                return syncManager.getSyncStorageEngine().getMasterSyncAutomatically(userId);
            }
            restoreCallingIdentity(identityToken);
            return false;
        } finally {
            restoreCallingIdentity(identityToken);
        }
    }

    public void setMasterSyncAutomatically(boolean flag) {
        setMasterSyncAutomaticallyAsUser(flag, UserHandle.getCallingUserId());
    }

    public void setMasterSyncAutomaticallyAsUser(boolean flag, int userId) {
        if (SyncSecurityInjector.permitControlSyncForAccount(this.mContext, (Account) null)) {
            enforceCrossUserPermission(userId, "no permission to set the sync status for user " + userId);
            this.mContext.enforceCallingOrSelfPermission("android.permission.WRITE_SYNC_SETTINGS", "no permission to write the sync settings");
            int callingUid = Binder.getCallingUid();
            int callingPid = Binder.getCallingPid();
            long identityToken = clearCallingIdentity();
            try {
                SyncManager syncManager = getSyncManager();
                if (syncManager != null) {
                    syncManager.getSyncStorageEngine().setMasterSyncAutomatically(flag, userId, getSyncExemptionForCaller(callingUid), callingUid, callingPid);
                }
            } finally {
                restoreCallingIdentity(identityToken);
            }
        }
    }

    public boolean isSyncActive(Account account, String authority, ComponentName cname) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.READ_SYNC_STATS", "no permission to read the sync stats");
        int userId = UserHandle.getCallingUserId();
        long identityToken = clearCallingIdentity();
        try {
            SyncManager syncManager = getSyncManager();
            if (syncManager == null) {
                return false;
            }
            boolean isSyncActive = syncManager.getSyncStorageEngine().isSyncActive(new SyncStorageEngine.EndPoint(account, authority, userId));
            restoreCallingIdentity(identityToken);
            return isSyncActive;
        } finally {
            restoreCallingIdentity(identityToken);
        }
    }

    public List<SyncInfo> getCurrentSyncs() {
        return getCurrentSyncsAsUser(UserHandle.getCallingUserId());
    }

    public List<SyncInfo> getCurrentSyncsAsUser(int userId) {
        enforceCrossUserPermission(userId, "no permission to read the sync settings for user " + userId);
        this.mContext.enforceCallingOrSelfPermission("android.permission.READ_SYNC_STATS", "no permission to read the sync stats");
        boolean canAccessAccounts = this.mContext.checkCallingOrSelfPermission("android.permission.GET_ACCOUNTS") == 0;
        long identityToken = clearCallingIdentity();
        try {
            return getSyncManager().getSyncStorageEngine().getCurrentSyncsCopy(userId, canAccessAccounts);
        } finally {
            restoreCallingIdentity(identityToken);
        }
    }

    public SyncStatusInfo getSyncStatus(Account account, String authority, ComponentName cname) {
        return getSyncStatusAsUser(account, authority, cname, UserHandle.getCallingUserId());
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    public SyncStatusInfo getSyncStatusAsUser(Account account, String authority, ComponentName cname, int userId) {
        if (!TextUtils.isEmpty(authority)) {
            enforceCrossUserPermission(userId, "no permission to read the sync stats for user " + userId);
            this.mContext.enforceCallingOrSelfPermission("android.permission.READ_SYNC_STATS", "no permission to read the sync stats");
            long identityToken = clearCallingIdentity();
            try {
                SyncManager syncManager = getSyncManager();
                if (syncManager == null) {
                    return null;
                } else if (account == null || authority == null) {
                    throw new IllegalArgumentException("Must call sync status with valid authority");
                } else {
                    SyncStatusInfo statusByAuthority = syncManager.getSyncStorageEngine().getStatusByAuthority(new SyncStorageEngine.EndPoint(account, authority, userId));
                    restoreCallingIdentity(identityToken);
                    return statusByAuthority;
                }
            } finally {
                restoreCallingIdentity(identityToken);
            }
        } else {
            throw new IllegalArgumentException("Authority must not be empty");
        }
    }

    public boolean isSyncPending(Account account, String authority, ComponentName cname) {
        return isSyncPendingAsUser(account, authority, cname, UserHandle.getCallingUserId());
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    public boolean isSyncPendingAsUser(Account account, String authority, ComponentName cname, int userId) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.READ_SYNC_STATS", "no permission to read the sync stats");
        enforceCrossUserPermission(userId, "no permission to retrieve the sync settings for user " + userId);
        long identityToken = clearCallingIdentity();
        SyncManager syncManager = getSyncManager();
        if (syncManager == null) {
            return false;
        }
        if (account == null || authority == null) {
            throw new IllegalArgumentException("Invalid authority specified");
        }
        try {
            return syncManager.getSyncStorageEngine().isSyncPending(new SyncStorageEngine.EndPoint(account, authority, userId));
        } finally {
            restoreCallingIdentity(identityToken);
        }
    }

    public void addStatusChangeListener(int mask, ISyncStatusObserver callback) {
        int callingUid = Binder.getCallingUid();
        long identityToken = clearCallingIdentity();
        try {
            SyncManager syncManager = getSyncManager();
            if (!(syncManager == null || callback == null)) {
                syncManager.getSyncStorageEngine().addStatusChangeListener(mask, UserHandle.getUserId(callingUid), callback);
            }
        } finally {
            restoreCallingIdentity(identityToken);
        }
    }

    public void addStatusChangeListenerAsUser(int mask, ISyncStatusObserver callback, int userId) {
        if (Binder.getCallingUid() == Process.myUid()) {
            long identityToken = clearCallingIdentity();
            try {
                SyncManager syncManager = getSyncManager();
                if (!(syncManager == null || callback == null)) {
                    syncManager.getSyncStorageEngine().addStatusChangeListener(mask, userId, callback);
                }
            } finally {
                restoreCallingIdentity(identityToken);
            }
        } else {
            throw new SecurityException();
        }
    }

    public void removeStatusChangeListener(ISyncStatusObserver callback) {
        long identityToken = clearCallingIdentity();
        try {
            SyncManager syncManager = getSyncManager();
            if (!(syncManager == null || callback == null)) {
                syncManager.getSyncStorageEngine().removeStatusChangeListener(callback);
            }
        } finally {
            restoreCallingIdentity(identityToken);
        }
    }

    private String getProviderPackageName(Uri uri) {
        ProviderInfo pi = this.mContext.getPackageManager().resolveContentProvider(uri.getAuthority(), 0);
        if (pi != null) {
            return pi.packageName;
        }
        return null;
    }

    @GuardedBy({"mCache"})
    private ArrayMap<Pair<String, Uri>, Bundle> findOrCreateCacheLocked(int userId, String providerPackageName) {
        ArrayMap<String, ArrayMap<Pair<String, Uri>, Bundle>> userCache = this.mCache.get(userId);
        if (userCache == null) {
            userCache = new ArrayMap<>();
            this.mCache.put(userId, userCache);
        }
        ArrayMap<Pair<String, Uri>, Bundle> packageCache = userCache.get(providerPackageName);
        if (packageCache != null) {
            return packageCache;
        }
        ArrayMap<Pair<String, Uri>, Bundle> packageCache2 = new ArrayMap<>();
        userCache.put(providerPackageName, packageCache2);
        return packageCache2;
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mCache"})
    public void invalidateCacheLocked(int userId, String providerPackageName, Uri uri) {
        ArrayMap<Pair<String, Uri>, Bundle> packageCache;
        ArrayMap<String, ArrayMap<Pair<String, Uri>, Bundle>> userCache = this.mCache.get(userId);
        if (userCache != null && (packageCache = userCache.get(providerPackageName)) != null) {
            if (uri != null) {
                int i = 0;
                while (i < packageCache.size()) {
                    Pair<String, Uri> key = packageCache.keyAt(i);
                    if (key.second == null || !((Uri) key.second).toString().startsWith(uri.toString())) {
                        i++;
                    } else {
                        packageCache.removeAt(i);
                    }
                }
                return;
            }
            packageCache.clear();
        }
    }

    public void putCache(String packageName, Uri key, Bundle value, int userId) {
        Bundle.setDefusable(value, true);
        enforceCrossUserPermission(userId, TAG);
        this.mContext.enforceCallingOrSelfPermission("android.permission.CACHE_CONTENT", TAG);
        ((AppOpsManager) this.mContext.getSystemService(AppOpsManager.class)).checkPackage(Binder.getCallingUid(), packageName);
        String providerPackageName = getProviderPackageName(key);
        Pair<String, Uri> fullKey = Pair.create(packageName, key);
        synchronized (this.mCache) {
            ArrayMap<Pair<String, Uri>, Bundle> cache = findOrCreateCacheLocked(userId, providerPackageName);
            if (value != null) {
                cache.put(fullKey, value);
            } else {
                cache.remove(fullKey);
            }
        }
    }

    public Bundle getCache(String packageName, Uri key, int userId) {
        Bundle bundle;
        enforceCrossUserPermission(userId, TAG);
        this.mContext.enforceCallingOrSelfPermission("android.permission.CACHE_CONTENT", TAG);
        ((AppOpsManager) this.mContext.getSystemService(AppOpsManager.class)).checkPackage(Binder.getCallingUid(), packageName);
        String providerPackageName = getProviderPackageName(key);
        Pair<String, Uri> fullKey = Pair.create(packageName, key);
        synchronized (this.mCache) {
            bundle = findOrCreateCacheLocked(userId, providerPackageName).get(fullKey);
        }
        return bundle;
    }

    private int handleIncomingUser(Uri uri, int pid, int uid, int modeFlags, boolean allowNonFull, int userId) {
        if (userId == -2) {
            userId = ActivityManager.getCurrentUser();
        }
        String permissions = "android.permission.INTERACT_ACROSS_USERS_FULL";
        if (userId == -1) {
            Context context = this.mContext;
            context.enforceCallingOrSelfPermission(permissions, "No access to " + uri);
        } else if (userId < 0) {
            throw new IllegalArgumentException("Invalid user: " + userId);
        } else if (!(userId == UserHandle.getCallingUserId() || checkUriPermission(uri, pid, uid, modeFlags, userId) == 0)) {
            boolean allow = false;
            if (this.mContext.checkCallingOrSelfPermission(permissions) == 0) {
                allow = true;
            } else if (allowNonFull && this.mContext.checkCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS") == 0) {
                allow = true;
            }
            if (!allow) {
                if (allowNonFull) {
                    permissions = "android.permission.INTERACT_ACROSS_USERS_FULL or android.permission.INTERACT_ACROSS_USERS";
                }
                throw new SecurityException("No access to " + uri + ": neither user " + uid + " nor current process has " + permissions);
            }
        }
        return userId;
    }

    private void enforceCrossUserPermission(int userHandle, String message) {
        if (UserHandle.getCallingUserId() != userHandle) {
            this.mContext.enforceCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS_FULL", message);
        }
    }

    private static int normalizeSyncable(int syncable) {
        if (syncable > 0) {
            return 1;
        }
        if (syncable == 0) {
            return 0;
        }
        return -2;
    }

    private void validateExtras(int callingUid, Bundle extras) {
        if (extras.containsKey("v_exemption") && callingUid != 0 && callingUid != 1000 && callingUid != 2000) {
            Log.w(TAG, "Invalid extras specified. requestsync -f/-F needs to run on 'adb shell'");
            throw new SecurityException("Invalid extras specified.");
        }
    }

    private int getSyncExemptionForCaller(int callingUid) {
        return getSyncExemptionAndCleanUpExtrasForCaller(callingUid, (Bundle) null);
    }

    private int getSyncExemptionAndCleanUpExtrasForCaller(int callingUid, Bundle extras) {
        if (extras != null) {
            int exemption = extras.getInt("v_exemption", -1);
            extras.remove("v_exemption");
            if (exemption != -1) {
                return exemption;
            }
        }
        ActivityManagerInternal ami = (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
        if (ami == null) {
            return 0;
        }
        int procState = ami.getUidProcessState(callingUid);
        boolean isUidActive = ami.isUidActive(callingUid);
        if (procState <= 2 || procState == 4) {
            return 2;
        }
        if (procState <= 7 || isUidActive) {
            return 1;
        }
        return 0;
    }

    public static final class ObserverNode {
        public static final int DELETE_TYPE = 2;
        public static final int INSERT_TYPE = 0;
        public static final int UPDATE_TYPE = 1;
        private ArrayList<ObserverNode> mChildren = new ArrayList<>();
        private String mName;
        /* access modifiers changed from: private */
        public ArrayList<ObserverEntry> mObservers = new ArrayList<>();

        private class ObserverEntry implements IBinder.DeathRecipient {
            public final boolean notifyForDescendants;
            public final IContentObserver observer;
            private final Object observersLock;
            public final int pid;
            public final int uid;
            /* access modifiers changed from: private */
            public final int userHandle;

            public ObserverEntry(IContentObserver o, boolean n, Object observersLock2, int _uid, int _pid, int _userHandle, Uri uri) {
                boolean alreadyDetected;
                this.observersLock = observersLock2;
                this.observer = o;
                this.uid = _uid;
                this.pid = _pid;
                this.userHandle = _userHandle;
                this.notifyForDescendants = n;
                int entries = ContentService.sObserverDeathDispatcher.linkToDeath(this.observer, this);
                if (entries == -1) {
                    binderDied();
                } else if (entries == 1000) {
                    synchronized (ContentService.sObserverLeakDetectedUid) {
                        alreadyDetected = ContentService.sObserverLeakDetectedUid.contains(Integer.valueOf(this.uid));
                        if (!alreadyDetected) {
                            ContentService.sObserverLeakDetectedUid.add(Integer.valueOf(this.uid));
                        }
                    }
                    if (!alreadyDetected) {
                        String caller = null;
                        try {
                            caller = (String) ArrayUtils.firstOrNull(AppGlobals.getPackageManager().getPackagesForUid(this.uid));
                        } catch (RemoteException e) {
                        }
                        Slog.wtf(ContentService.TAG, "Observer registered too many times. Leak? cpid=" + this.pid + " cuid=" + this.uid + " cpkg=" + caller + " url=" + uri);
                    }
                }
            }

            public void binderDied() {
                synchronized (this.observersLock) {
                    ObserverNode.this.removeObserverLocked(this.observer);
                }
            }

            public void dumpLocked(FileDescriptor fd, PrintWriter pw, String[] args, String name, String prefix, SparseIntArray pidCounts) {
                int i = this.pid;
                pidCounts.put(i, pidCounts.get(i) + 1);
                pw.print(prefix);
                pw.print(name);
                pw.print(": pid=");
                pw.print(this.pid);
                pw.print(" uid=");
                pw.print(this.uid);
                pw.print(" user=");
                pw.print(this.userHandle);
                pw.print(" target=");
                IContentObserver iContentObserver = this.observer;
                pw.println(Integer.toHexString(System.identityHashCode(iContentObserver != null ? iContentObserver.asBinder() : null)));
            }
        }

        public ObserverNode(String name) {
            this.mName = name;
        }

        public void dumpLocked(FileDescriptor fd, PrintWriter pw, String[] args, String name, String prefix, int[] counts, SparseIntArray pidCounts) {
            String innerName;
            String str = name;
            String innerName2 = null;
            if (this.mObservers.size() > 0) {
                if ("".equals(str)) {
                    innerName2 = this.mName;
                } else {
                    innerName2 = str + SliceClientPermissions.SliceAuthority.DELIMITER + this.mName;
                }
                for (int i = 0; i < this.mObservers.size(); i++) {
                    counts[1] = counts[1] + 1;
                    this.mObservers.get(i).dumpLocked(fd, pw, args, innerName2, prefix, pidCounts);
                }
            }
            if (this.mChildren.size() > 0) {
                if (innerName2 != null) {
                    innerName = innerName2;
                } else if ("".equals(str)) {
                    innerName = this.mName;
                } else {
                    innerName = str + SliceClientPermissions.SliceAuthority.DELIMITER + this.mName;
                }
                for (int i2 = 0; i2 < this.mChildren.size(); i2++) {
                    counts[0] = counts[0] + 1;
                    this.mChildren.get(i2).dumpLocked(fd, pw, args, innerName, prefix, counts, pidCounts);
                }
                return;
            }
        }

        private String getUriSegment(Uri uri, int index) {
            if (uri == null) {
                return null;
            }
            if (index == 0) {
                return uri.getAuthority();
            }
            return uri.getPathSegments().get(index - 1);
        }

        private int countUriSegments(Uri uri) {
            if (uri == null) {
                return 0;
            }
            return uri.getPathSegments().size() + 1;
        }

        public void addObserverLocked(Uri uri, IContentObserver observer, boolean notifyForDescendants, Object observersLock, int uid, int pid, int userHandle) {
            addObserverLocked(uri, 0, observer, notifyForDescendants, observersLock, uid, pid, userHandle);
        }

        private void addObserverLocked(Uri uri, int index, IContentObserver observer, boolean notifyForDescendants, Object observersLock, int uid, int pid, int userHandle) {
            int i = index;
            if (i == countUriSegments(uri)) {
                this.mObservers.add(new ObserverEntry(observer, notifyForDescendants, observersLock, uid, pid, userHandle, uri));
                return;
            }
            String segment = getUriSegment(uri, index);
            if (segment != null) {
                int N = this.mChildren.size();
                for (int i2 = 0; i2 < N; i2++) {
                    ObserverNode node = this.mChildren.get(i2);
                    if (node.mName.equals(segment)) {
                        node.addObserverLocked(uri, i + 1, observer, notifyForDescendants, observersLock, uid, pid, userHandle);
                        return;
                    }
                }
                ObserverNode node2 = new ObserverNode(segment);
                this.mChildren.add(node2);
                node2.addObserverLocked(uri, i + 1, observer, notifyForDescendants, observersLock, uid, pid, userHandle);
                return;
            }
            throw new IllegalArgumentException("Invalid Uri (" + uri + ") used for observer");
        }

        public boolean removeObserverLocked(IContentObserver observer) {
            int size = this.mChildren.size();
            int i = 0;
            while (i < size) {
                if (this.mChildren.get(i).removeObserverLocked(observer)) {
                    this.mChildren.remove(i);
                    i--;
                    size--;
                }
                i++;
            }
            IBinder observerBinder = observer.asBinder();
            int size2 = this.mObservers.size();
            int i2 = 0;
            while (true) {
                if (i2 >= size2) {
                    break;
                }
                ObserverEntry entry = this.mObservers.get(i2);
                if (entry.observer.asBinder() == observerBinder) {
                    this.mObservers.remove(i2);
                    ContentService.sObserverDeathDispatcher.unlinkToDeath(observer, entry);
                    break;
                }
                i2++;
            }
            if (this.mChildren.size() == 0 && this.mObservers.size() == 0) {
                return true;
            }
            return false;
        }

        private void collectMyObserversLocked(boolean leaf, IContentObserver observer, boolean observerWantsSelfNotifications, int flags, int targetUserHandle, ArrayList<ObserverCall> calls) {
            int N = this.mObservers.size();
            IBinder observerBinder = observer == null ? null : observer.asBinder();
            for (int i = 0; i < N; i++) {
                ObserverEntry entry = this.mObservers.get(i);
                boolean selfChange = entry.observer.asBinder() == observerBinder;
                if ((!selfChange || observerWantsSelfNotifications) && (targetUserHandle == -1 || entry.userHandle == -1 || targetUserHandle == entry.userHandle)) {
                    if (leaf) {
                        if ((flags & 2) != 0 && entry.notifyForDescendants) {
                        }
                    } else if (!entry.notifyForDescendants) {
                    }
                    calls.add(new ObserverCall(this, entry.observer, selfChange, UserHandle.getUserId(entry.uid)));
                }
            }
        }

        public void collectObserversLocked(Uri uri, int index, IContentObserver observer, boolean observerWantsSelfNotifications, int flags, int targetUserHandle, ArrayList<ObserverCall> calls) {
            int i = index;
            String segment = null;
            int segmentCount = countUriSegments(uri);
            if (i >= segmentCount) {
                collectMyObserversLocked(true, observer, observerWantsSelfNotifications, flags, targetUserHandle, calls);
            } else if (i < segmentCount) {
                segment = getUriSegment(uri, index);
                collectMyObserversLocked(false, observer, observerWantsSelfNotifications, flags, targetUserHandle, calls);
            }
            int N = this.mChildren.size();
            for (int i2 = 0; i2 < N; i2++) {
                ObserverNode node = this.mChildren.get(i2);
                if (segment == null || node.mName.equals(segment)) {
                    node.collectObserversLocked(uri, i + 1, observer, observerWantsSelfNotifications, flags, targetUserHandle, calls);
                    if (segment != null) {
                        return;
                    }
                }
            }
        }
    }

    private void enforceShell(String method) {
        int callingUid = Binder.getCallingUid();
        if (callingUid != 2000 && callingUid != 0) {
            throw new SecurityException("Non-shell user attempted to call " + method);
        }
    }

    public void resetTodayStats() {
        enforceShell("resetTodayStats");
        if (this.mSyncManager != null) {
            long token = Binder.clearCallingIdentity();
            try {
                this.mSyncManager.resetTodayStats();
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }
    }

    public void onDbCorruption(String tag, String message, String stacktrace) {
        Slog.e(tag, message);
        Slog.e(tag, "at " + stacktrace);
        Slog.wtf(tag, message);
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [android.os.Binder] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onShellCommand(java.io.FileDescriptor r9, java.io.FileDescriptor r10, java.io.FileDescriptor r11, java.lang.String[] r12, android.os.ShellCallback r13, android.os.ResultReceiver r14) {
        /*
            r8 = this;
            com.android.server.content.ContentShellCommand r0 = new com.android.server.content.ContentShellCommand
            r0.<init>(r8)
            r1 = r8
            r2 = r9
            r3 = r10
            r4 = r11
            r5 = r12
            r6 = r13
            r7 = r14
            r0.exec(r1, r2, r3, r4, r5, r6, r7)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.ContentService.onShellCommand(java.io.FileDescriptor, java.io.FileDescriptor, java.io.FileDescriptor, java.lang.String[], android.os.ShellCallback, android.os.ResultReceiver):void");
    }

    public void setMiSyncPauseToTime(Account account, long pauseTimeMillis, int uid) {
        ContentServiceInjector.setMiSyncPauseToTime(this.mContext, this, account, pauseTimeMillis, uid);
    }

    public long getMiSyncPauseToTime(Account account, int uid) {
        return ContentServiceInjector.getMiSyncPauseToTime(this.mContext, this, account, uid);
    }

    public void setMiSyncStrategy(Account account, int strategy, int uid) {
        ContentServiceInjector.setMiSyncStrategy(this.mContext, this, account, strategy, uid);
    }

    public int getMiSyncStrategy(Account account, int uid) {
        return ContentServiceInjector.getMiSyncStrategy(this.mContext, this, account, uid);
    }

    /* access modifiers changed from: package-private */
    public void enforceCrossUserPermissionForInjector(int userHandle, String message) {
        enforceCrossUserPermission(userHandle, message);
    }

    /* access modifiers changed from: package-private */
    public SyncManager getSyncManagerForInjector() {
        return getSyncManager();
    }
}
