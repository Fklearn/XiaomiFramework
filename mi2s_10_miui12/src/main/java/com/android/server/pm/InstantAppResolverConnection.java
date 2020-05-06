package com.android.server.pm;

import android.app.IInstantAppResolver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.InstantAppResolveInfo;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IRemoteCallback;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Slog;
import android.util.TimedRemoteCaller;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.os.BackgroundThread;
import com.android.server.pm.InstantAppResolverConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeoutException;

final class InstantAppResolverConnection implements IBinder.DeathRecipient {
    private static final long BIND_SERVICE_TIMEOUT_MS = (Build.IS_ENG ? 500 : 300);
    /* access modifiers changed from: private */
    public static final long CALL_SERVICE_TIMEOUT_MS = (Build.IS_ENG ? 200 : 100);
    /* access modifiers changed from: private */
    public static final boolean DEBUG_INSTANT = Build.IS_DEBUGGABLE;
    private static final int STATE_BINDING = 1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PENDING = 2;
    private static final String TAG = "PackageManager";
    private final Handler mBgHandler;
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public int mBindState = 0;
    private final Context mContext;
    private final GetInstantAppResolveInfoCaller mGetInstantAppResolveInfoCaller = new GetInstantAppResolveInfoCaller();
    private final Intent mIntent;
    /* access modifiers changed from: private */
    public final Object mLock = new Object();
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public IInstantAppResolver mRemoteInstance;
    private final ServiceConnection mServiceConnection = new MyServiceConnection();

    public static abstract class PhaseTwoCallback {
        /* access modifiers changed from: package-private */
        public abstract void onPhaseTwoResolved(List<InstantAppResolveInfo> list, long j);
    }

    public InstantAppResolverConnection(Context context, ComponentName componentName, String action) {
        this.mContext = context;
        this.mIntent = new Intent(action).setComponent(componentName);
        this.mBgHandler = BackgroundThread.getHandler();
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    /*  JADX ERROR: IndexOutOfBoundsException in pass: RegionMakerVisitor
        java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        	at java.util.ArrayList.rangeCheck(ArrayList.java:657)
        	at java.util.ArrayList.get(ArrayList.java:433)
        	at jadx.core.dex.nodes.InsnNode.getArg(InsnNode.java:101)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:611)
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
    public java.util.List<android.content.pm.InstantAppResolveInfo> getInstantAppResolveInfoList(android.content.Intent r8, int[] r9, int r10, java.lang.String r11) throws com.android.server.pm.InstantAppResolverConnection.ConnectionException {
        /*
            r7 = this;
            r7.throwIfCalledOnMainThread()
            r0 = 0
            android.app.IInstantAppResolver r2 = r7.getRemoteInstanceLazy(r11)     // Catch:{ TimeoutException -> 0x0046, InterruptedException -> 0x003e }
            com.android.server.pm.InstantAppResolverConnection$GetInstantAppResolveInfoCaller r1 = r7.mGetInstantAppResolveInfoCaller     // Catch:{ TimeoutException -> 0x0034, RemoteException -> 0x0024 }
            r3 = r8
            r4 = r9
            r5 = r10
            r6 = r11
            java.util.List r0 = r1.getInstantAppResolveInfoList(r2, r3, r4, r5, r6)     // Catch:{ TimeoutException -> 0x0034, RemoteException -> 0x0024 }
            java.lang.Object r1 = r7.mLock
            monitor-enter(r1)
            java.lang.Object r3 = r7.mLock     // Catch:{ all -> 0x001d }
            r3.notifyAll()     // Catch:{ all -> 0x001d }
            monitor-exit(r1)     // Catch:{ all -> 0x001d }
            return r0
        L_0x001d:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x001d }
            throw r0
        L_0x0020:
            r0 = move-exception
            r1 = r0
            r0 = r2
            goto L_0x004e
        L_0x0024:
            r0 = move-exception
            java.lang.Object r0 = r7.mLock
            monitor-enter(r0)
            java.lang.Object r1 = r7.mLock     // Catch:{ all -> 0x0031 }
            r1.notifyAll()     // Catch:{ all -> 0x0031 }
            monitor-exit(r0)     // Catch:{ all -> 0x0031 }
            r0 = 0
            return r0
        L_0x0031:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0031 }
            throw r1
        L_0x0034:
            r0 = move-exception
            com.android.server.pm.InstantAppResolverConnection$ConnectionException r1 = new com.android.server.pm.InstantAppResolverConnection$ConnectionException     // Catch:{ all -> 0x0020 }
            r3 = 2
            r1.<init>(r3)     // Catch:{ all -> 0x0020 }
            throw r1     // Catch:{ all -> 0x0020 }
        L_0x003c:
            r1 = move-exception
            goto L_0x004e
        L_0x003e:
            r1 = move-exception
            com.android.server.pm.InstantAppResolverConnection$ConnectionException r2 = new com.android.server.pm.InstantAppResolverConnection$ConnectionException     // Catch:{ all -> 0x003c }
            r3 = 3
            r2.<init>(r3)     // Catch:{ all -> 0x003c }
            throw r2     // Catch:{ all -> 0x003c }
        L_0x0046:
            r1 = move-exception
            com.android.server.pm.InstantAppResolverConnection$ConnectionException r2 = new com.android.server.pm.InstantAppResolverConnection$ConnectionException     // Catch:{ all -> 0x003c }
            r3 = 1
            r2.<init>(r3)     // Catch:{ all -> 0x003c }
            throw r2     // Catch:{ all -> 0x003c }
        L_0x004e:
            java.lang.Object r2 = r7.mLock
            monitor-enter(r2)
            java.lang.Object r3 = r7.mLock     // Catch:{ all -> 0x0058 }
            r3.notifyAll()     // Catch:{ all -> 0x0058 }
            monitor-exit(r2)     // Catch:{ all -> 0x0058 }
            throw r1
        L_0x0058:
            r1 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0058 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.InstantAppResolverConnection.getInstantAppResolveInfoList(android.content.Intent, int[], int, java.lang.String):java.util.List");
    }

    public void getInstantAppIntentFilterList(Intent sanitizedIntent, int[] hashPrefix, int userId, String token, PhaseTwoCallback callback, Handler callbackHandler, long startTime) throws ConnectionException {
        final Handler handler = callbackHandler;
        final PhaseTwoCallback phaseTwoCallback = callback;
        final long j = startTime;
        try {
            getRemoteInstanceLazy(token).getInstantAppIntentFilterList(sanitizedIntent, hashPrefix, userId, token, new IRemoteCallback.Stub() {
                public void sendResult(Bundle data) throws RemoteException {
                    handler.post(new Runnable(data.getParcelableArrayList("android.app.extra.RESOLVE_INFO"), j) {
                        private final /* synthetic */ ArrayList f$1;
                        private final /* synthetic */ long f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run() {
                            InstantAppResolverConnection.PhaseTwoCallback.this.onPhaseTwoResolved(this.f$1, this.f$2);
                        }
                    });
                }
            });
        } catch (TimeoutException e) {
            throw new ConnectionException(1);
        } catch (InterruptedException e2) {
            throw new ConnectionException(3);
        } catch (RemoteException e3) {
        }
    }

    private IInstantAppResolver getRemoteInstanceLazy(String token) throws ConnectionException, TimeoutException, InterruptedException {
        long binderToken = Binder.clearCallingIdentity();
        try {
            return bind(token);
        } finally {
            Binder.restoreCallingIdentity(binderToken);
        }
    }

    @GuardedBy({"mLock"})
    private void waitForBindLocked(String token) throws TimeoutException, InterruptedException {
        long startMillis = SystemClock.uptimeMillis();
        while (this.mBindState != 0 && this.mRemoteInstance == null) {
            long remainingMillis = BIND_SERVICE_TIMEOUT_MS - (SystemClock.uptimeMillis() - startMillis);
            if (remainingMillis > 0) {
                this.mLock.wait(remainingMillis);
            } else {
                throw new TimeoutException("[" + token + "] Didn't bind to resolver in time!");
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 12 */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0076, code lost:
        r1 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x0079, code lost:
        if (r0 == false) goto L_0x00a1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x007d, code lost:
        if (DEBUG_INSTANT == false) goto L_0x009a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x007f, code lost:
        android.util.Slog.i(TAG, "[" + r13 + "] Previous connection never established; rebinding");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x009a, code lost:
        r12.mContext.unbindService(r12.mServiceConnection);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x00a3, code lost:
        if (DEBUG_INSTANT == false) goto L_0x00c0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x00a5, code lost:
        android.util.Slog.v(TAG, "[" + r13 + "] Binding to instant app resolver");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x00c0, code lost:
        r1 = r12.mContext.bindServiceAsUser(r12.mIntent, r12.mServiceConnection, 67108865, android.os.UserHandle.SYSTEM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x00d3, code lost:
        if (r1 == false) goto L_0x00f8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x00d5, code lost:
        r4 = r12.mLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x00d7, code lost:
        monitor-enter(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:?, code lost:
        waitForBindLocked(r13);
        r2 = r12.mRemoteInstance;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x00de, code lost:
        monitor-exit(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x00df, code lost:
        r7 = r12.mLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x00e1, code lost:
        monitor-enter(r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x00e2, code lost:
        if (r1 == false) goto L_0x00e9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x00e4, code lost:
        if (r2 != null) goto L_0x00e9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:?, code lost:
        r12.mBindState = 2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x00e9, code lost:
        r12.mBindState = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x00eb, code lost:
        r12.mLock.notifyAll();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x00f0, code lost:
        monitor-exit(r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x00f1, code lost:
        return r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x00f8, code lost:
        android.util.Slog.w(TAG, "[" + r13 + "] Failed to bind to: " + r12.mIntent);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:0x011d, code lost:
        throw new com.android.server.pm.InstantAppResolverConnection.ConnectionException(1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x011e, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:75:0x011f, code lost:
        r2 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:76:0x0123, code lost:
        monitor-enter(r12.mLock);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:77:0x0124, code lost:
        if (r2 == false) goto L_0x012b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:80:?, code lost:
        r12.mBindState = 2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:81:0x012b, code lost:
        r12.mBindState = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:82:0x012d, code lost:
        r12.mLock.notifyAll();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:0x0133, code lost:
        throw r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.app.IInstantAppResolver bind(java.lang.String r13) throws com.android.server.pm.InstantAppResolverConnection.ConnectionException, java.util.concurrent.TimeoutException, java.lang.InterruptedException {
        /*
            r12 = this;
            r0 = 0
            java.lang.Object r1 = r12.mLock
            monitor-enter(r1)
            android.app.IInstantAppResolver r2 = r12.mRemoteInstance     // Catch:{ all -> 0x0137 }
            if (r2 == 0) goto L_0x000c
            android.app.IInstantAppResolver r2 = r12.mRemoteInstance     // Catch:{ all -> 0x0137 }
            monitor-exit(r1)     // Catch:{ all -> 0x0137 }
            return r2
        L_0x000c:
            int r2 = r12.mBindState     // Catch:{ all -> 0x0137 }
            r3 = 2
            if (r2 != r3) goto L_0x003e
            boolean r2 = DEBUG_INSTANT     // Catch:{ all -> 0x0137 }
            if (r2 == 0) goto L_0x0030
            java.lang.String r2 = "PackageManager"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0137 }
            r4.<init>()     // Catch:{ all -> 0x0137 }
            java.lang.String r5 = "["
            r4.append(r5)     // Catch:{ all -> 0x0137 }
            r4.append(r13)     // Catch:{ all -> 0x0137 }
            java.lang.String r5 = "] Previous bind timed out; waiting for connection"
            r4.append(r5)     // Catch:{ all -> 0x0137 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0137 }
            android.util.Slog.i(r2, r4)     // Catch:{ all -> 0x0137 }
        L_0x0030:
            r12.waitForBindLocked(r13)     // Catch:{ TimeoutException -> 0x003c }
            android.app.IInstantAppResolver r2 = r12.mRemoteInstance     // Catch:{ TimeoutException -> 0x003c }
            if (r2 == 0) goto L_0x003b
            android.app.IInstantAppResolver r2 = r12.mRemoteInstance     // Catch:{ TimeoutException -> 0x003c }
            monitor-exit(r1)     // Catch:{ all -> 0x0137 }
            return r2
        L_0x003b:
            goto L_0x003e
        L_0x003c:
            r2 = move-exception
            r0 = 1
        L_0x003e:
            int r2 = r12.mBindState     // Catch:{ all -> 0x0137 }
            r4 = 1
            if (r2 != r4) goto L_0x0073
            boolean r2 = DEBUG_INSTANT     // Catch:{ all -> 0x0137 }
            if (r2 == 0) goto L_0x0062
            java.lang.String r2 = "PackageManager"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0137 }
            r3.<init>()     // Catch:{ all -> 0x0137 }
            java.lang.String r5 = "["
            r3.append(r5)     // Catch:{ all -> 0x0137 }
            r3.append(r13)     // Catch:{ all -> 0x0137 }
            java.lang.String r5 = "] Another thread is binding; waiting for connection"
            r3.append(r5)     // Catch:{ all -> 0x0137 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0137 }
            android.util.Slog.i(r2, r3)     // Catch:{ all -> 0x0137 }
        L_0x0062:
            r12.waitForBindLocked(r13)     // Catch:{ all -> 0x0137 }
            android.app.IInstantAppResolver r2 = r12.mRemoteInstance     // Catch:{ all -> 0x0137 }
            if (r2 == 0) goto L_0x006d
            android.app.IInstantAppResolver r2 = r12.mRemoteInstance     // Catch:{ all -> 0x0137 }
            monitor-exit(r1)     // Catch:{ all -> 0x0137 }
            return r2
        L_0x006d:
            com.android.server.pm.InstantAppResolverConnection$ConnectionException r2 = new com.android.server.pm.InstantAppResolverConnection$ConnectionException     // Catch:{ all -> 0x0137 }
            r2.<init>(r4)     // Catch:{ all -> 0x0137 }
            throw r2     // Catch:{ all -> 0x0137 }
        L_0x0073:
            r12.mBindState = r4     // Catch:{ all -> 0x0137 }
            monitor-exit(r1)     // Catch:{ all -> 0x0137 }
            r1 = 0
            r2 = 0
            r5 = 0
            if (r0 == 0) goto L_0x00a1
            boolean r6 = DEBUG_INSTANT     // Catch:{ all -> 0x011e }
            if (r6 == 0) goto L_0x009a
            java.lang.String r6 = "PackageManager"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x011e }
            r7.<init>()     // Catch:{ all -> 0x011e }
            java.lang.String r8 = "["
            r7.append(r8)     // Catch:{ all -> 0x011e }
            r7.append(r13)     // Catch:{ all -> 0x011e }
            java.lang.String r8 = "] Previous connection never established; rebinding"
            r7.append(r8)     // Catch:{ all -> 0x011e }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x011e }
            android.util.Slog.i(r6, r7)     // Catch:{ all -> 0x011e }
        L_0x009a:
            android.content.Context r6 = r12.mContext     // Catch:{ all -> 0x011e }
            android.content.ServiceConnection r7 = r12.mServiceConnection     // Catch:{ all -> 0x011e }
            r6.unbindService(r7)     // Catch:{ all -> 0x011e }
        L_0x00a1:
            boolean r6 = DEBUG_INSTANT     // Catch:{ all -> 0x011e }
            if (r6 == 0) goto L_0x00c0
            java.lang.String r6 = "PackageManager"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x011e }
            r7.<init>()     // Catch:{ all -> 0x011e }
            java.lang.String r8 = "["
            r7.append(r8)     // Catch:{ all -> 0x011e }
            r7.append(r13)     // Catch:{ all -> 0x011e }
            java.lang.String r8 = "] Binding to instant app resolver"
            r7.append(r8)     // Catch:{ all -> 0x011e }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x011e }
            android.util.Slog.v(r6, r7)     // Catch:{ all -> 0x011e }
        L_0x00c0:
            r6 = 67108865(0x4000001, float:1.504633E-36)
            android.content.Context r7 = r12.mContext     // Catch:{ all -> 0x011e }
            android.content.Intent r8 = r12.mIntent     // Catch:{ all -> 0x011e }
            android.content.ServiceConnection r9 = r12.mServiceConnection     // Catch:{ all -> 0x011e }
            r10 = 67108865(0x4000001, float:1.504633E-36)
            android.os.UserHandle r11 = android.os.UserHandle.SYSTEM     // Catch:{ all -> 0x011e }
            boolean r7 = r7.bindServiceAsUser(r8, r9, r10, r11)     // Catch:{ all -> 0x011e }
            r1 = r7
            if (r1 == 0) goto L_0x00f8
            java.lang.Object r4 = r12.mLock     // Catch:{ all -> 0x011e }
            monitor-enter(r4)     // Catch:{ all -> 0x011e }
            r12.waitForBindLocked(r13)     // Catch:{ all -> 0x00f5 }
            android.app.IInstantAppResolver r7 = r12.mRemoteInstance     // Catch:{ all -> 0x00f5 }
            r2 = r7
            monitor-exit(r4)     // Catch:{ all -> 0x00f5 }
            java.lang.Object r7 = r12.mLock
            monitor-enter(r7)
            if (r1 == 0) goto L_0x00e9
            if (r2 != 0) goto L_0x00e9
            r12.mBindState = r3     // Catch:{ all -> 0x00f2 }
            goto L_0x00eb
        L_0x00e9:
            r12.mBindState = r5     // Catch:{ all -> 0x00f2 }
        L_0x00eb:
            java.lang.Object r3 = r12.mLock     // Catch:{ all -> 0x00f2 }
            r3.notifyAll()     // Catch:{ all -> 0x00f2 }
            monitor-exit(r7)     // Catch:{ all -> 0x00f2 }
            return r2
        L_0x00f2:
            r3 = move-exception
            monitor-exit(r7)     // Catch:{ all -> 0x00f2 }
            throw r3
        L_0x00f5:
            r7 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x00f5 }
            throw r7     // Catch:{ all -> 0x011e }
        L_0x00f8:
            java.lang.String r7 = "PackageManager"
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x011e }
            r8.<init>()     // Catch:{ all -> 0x011e }
            java.lang.String r9 = "["
            r8.append(r9)     // Catch:{ all -> 0x011e }
            r8.append(r13)     // Catch:{ all -> 0x011e }
            java.lang.String r9 = "] Failed to bind to: "
            r8.append(r9)     // Catch:{ all -> 0x011e }
            android.content.Intent r9 = r12.mIntent     // Catch:{ all -> 0x011e }
            r8.append(r9)     // Catch:{ all -> 0x011e }
            java.lang.String r8 = r8.toString()     // Catch:{ all -> 0x011e }
            android.util.Slog.w(r7, r8)     // Catch:{ all -> 0x011e }
            com.android.server.pm.InstantAppResolverConnection$ConnectionException r7 = new com.android.server.pm.InstantAppResolverConnection$ConnectionException     // Catch:{ all -> 0x011e }
            r7.<init>(r4)     // Catch:{ all -> 0x011e }
            throw r7     // Catch:{ all -> 0x011e }
        L_0x011e:
            r4 = move-exception
            r6 = r2
            r2 = r1
            java.lang.Object r7 = r12.mLock
            monitor-enter(r7)
            if (r2 == 0) goto L_0x012b
            if (r6 != 0) goto L_0x012b
            r12.mBindState = r3     // Catch:{ all -> 0x0134 }
            goto L_0x012d
        L_0x012b:
            r12.mBindState = r5     // Catch:{ all -> 0x0134 }
        L_0x012d:
            java.lang.Object r1 = r12.mLock     // Catch:{ all -> 0x0134 }
            r1.notifyAll()     // Catch:{ all -> 0x0134 }
            monitor-exit(r7)     // Catch:{ all -> 0x0134 }
            throw r4
        L_0x0134:
            r1 = move-exception
            monitor-exit(r7)     // Catch:{ all -> 0x0134 }
            throw r1
        L_0x0137:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0137 }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.InstantAppResolverConnection.bind(java.lang.String):android.app.IInstantAppResolver");
    }

    private void throwIfCalledOnMainThread() {
        if (Thread.currentThread() == this.mContext.getMainLooper().getThread()) {
            throw new RuntimeException("Cannot invoke on the main thread");
        }
    }

    /* access modifiers changed from: package-private */
    public void optimisticBind() {
        this.mBgHandler.post(new Runnable() {
            public final void run() {
                InstantAppResolverConnection.this.lambda$optimisticBind$0$InstantAppResolverConnection();
            }
        });
    }

    public /* synthetic */ void lambda$optimisticBind$0$InstantAppResolverConnection() {
        try {
            if (bind("Optimistic Bind") != null && DEBUG_INSTANT) {
                Slog.i(TAG, "Optimistic bind succeeded.");
            }
        } catch (ConnectionException | InterruptedException | TimeoutException e) {
            Slog.e(TAG, "Optimistic bind failed.", e);
        }
    }

    public void binderDied() {
        if (DEBUG_INSTANT) {
            Slog.d(TAG, "Binder to instant app resolver died");
        }
        synchronized (this.mLock) {
            handleBinderDiedLocked();
        }
        optimisticBind();
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public void handleBinderDiedLocked() {
        IInstantAppResolver iInstantAppResolver = this.mRemoteInstance;
        if (iInstantAppResolver != null) {
            try {
                iInstantAppResolver.asBinder().unlinkToDeath(this, 0);
            } catch (NoSuchElementException e) {
            }
        }
        this.mRemoteInstance = null;
    }

    public static class ConnectionException extends Exception {
        public static final int FAILURE_BIND = 1;
        public static final int FAILURE_CALL = 2;
        public static final int FAILURE_INTERRUPTED = 3;
        public final int failure;

        public ConnectionException(int _failure) {
            this.failure = _failure;
        }
    }

    private final class MyServiceConnection implements ServiceConnection {
        private MyServiceConnection() {
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            if (InstantAppResolverConnection.DEBUG_INSTANT) {
                Slog.d(InstantAppResolverConnection.TAG, "Connected to instant app resolver");
            }
            synchronized (InstantAppResolverConnection.this.mLock) {
                IInstantAppResolver unused = InstantAppResolverConnection.this.mRemoteInstance = IInstantAppResolver.Stub.asInterface(service);
                if (InstantAppResolverConnection.this.mBindState == 2) {
                    int unused2 = InstantAppResolverConnection.this.mBindState = 0;
                }
                try {
                    service.linkToDeath(InstantAppResolverConnection.this, 0);
                } catch (RemoteException e) {
                    InstantAppResolverConnection.this.handleBinderDiedLocked();
                }
                InstantAppResolverConnection.this.mLock.notifyAll();
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            if (InstantAppResolverConnection.DEBUG_INSTANT) {
                Slog.d(InstantAppResolverConnection.TAG, "Disconnected from instant app resolver");
            }
            synchronized (InstantAppResolverConnection.this.mLock) {
                InstantAppResolverConnection.this.handleBinderDiedLocked();
            }
        }
    }

    private static final class GetInstantAppResolveInfoCaller extends TimedRemoteCaller<List<InstantAppResolveInfo>> {
        private final IRemoteCallback mCallback = new IRemoteCallback.Stub() {
            public void sendResult(Bundle data) throws RemoteException {
                GetInstantAppResolveInfoCaller.this.onRemoteMethodResult(data.getParcelableArrayList("android.app.extra.RESOLVE_INFO"), data.getInt("android.app.extra.SEQUENCE", -1));
            }
        };

        public GetInstantAppResolveInfoCaller() {
            super(InstantAppResolverConnection.CALL_SERVICE_TIMEOUT_MS);
        }

        public List<InstantAppResolveInfo> getInstantAppResolveInfoList(IInstantAppResolver target, Intent sanitizedIntent, int[] hashPrefix, int userId, String token) throws RemoteException, TimeoutException {
            int sequence = onBeforeRemoteCall();
            target.getInstantAppResolveInfoList(sanitizedIntent, hashPrefix, userId, token, sequence, this.mCallback);
            return (List) getResultTimed(sequence);
        }
    }
}
