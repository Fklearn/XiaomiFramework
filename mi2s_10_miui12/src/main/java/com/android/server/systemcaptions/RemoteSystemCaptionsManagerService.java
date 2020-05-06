package com.android.server.systemcaptions;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;

final class RemoteSystemCaptionsManagerService {
    private static final String SERVICE_INTERFACE = "android.service.systemcaptions.SystemCaptionsManagerService";
    /* access modifiers changed from: private */
    public static final String TAG = RemoteSystemCaptionsManagerService.class.getSimpleName();
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public boolean mBinding = false;
    private final ComponentName mComponentName;
    private final Context mContext;
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public boolean mDestroyed = false;
    private final Handler mHandler;
    private final Intent mIntent;
    /* access modifiers changed from: private */
    public final Object mLock = new Object();
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public IBinder mService;
    private final RemoteServiceConnection mServiceConnection = new RemoteServiceConnection();
    private final int mUserId;
    /* access modifiers changed from: private */
    public final boolean mVerbose;

    RemoteSystemCaptionsManagerService(Context context, ComponentName componentName, int userId, boolean verbose) {
        this.mContext = context;
        this.mComponentName = componentName;
        this.mUserId = userId;
        this.mVerbose = verbose;
        this.mIntent = new Intent(SERVICE_INTERFACE).setComponent(componentName);
        this.mHandler = new Handler(Looper.getMainLooper());
    }

    /* access modifiers changed from: package-private */
    public void initialize() {
        if (this.mVerbose) {
            Slog.v(TAG, "initialize()");
        }
        ensureBound();
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x001e, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void destroy() {
        /*
            r3 = this;
            boolean r0 = r3.mVerbose
            if (r0 == 0) goto L_0x000b
            java.lang.String r0 = TAG
            java.lang.String r1 = "destroy()"
            android.util.Slog.v(r0, r1)
        L_0x000b:
            java.lang.Object r0 = r3.mLock
            monitor-enter(r0)
            boolean r1 = r3.mDestroyed     // Catch:{ all -> 0x0027 }
            if (r1 == 0) goto L_0x001f
            boolean r1 = r3.mVerbose     // Catch:{ all -> 0x0027 }
            if (r1 == 0) goto L_0x001d
            java.lang.String r1 = TAG     // Catch:{ all -> 0x0027 }
            java.lang.String r2 = "destroy(): Already destroyed"
            android.util.Slog.v(r1, r2)     // Catch:{ all -> 0x0027 }
        L_0x001d:
            monitor-exit(r0)     // Catch:{ all -> 0x0027 }
            return
        L_0x001f:
            r1 = 1
            r3.mDestroyed = r1     // Catch:{ all -> 0x0027 }
            r3.ensureUnboundLocked()     // Catch:{ all -> 0x0027 }
            monitor-exit(r0)     // Catch:{ all -> 0x0027 }
            return
        L_0x0027:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0027 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.systemcaptions.RemoteSystemCaptionsManagerService.destroy():void");
    }

    /* access modifiers changed from: package-private */
    public boolean isDestroyed() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mDestroyed;
        }
        return z;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x005a, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x005c, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void ensureBound() {
        /*
            r8 = this;
            java.lang.Object r0 = r8.mLock
            monitor-enter(r0)
            android.os.IBinder r1 = r8.mService     // Catch:{ all -> 0x005d }
            if (r1 != 0) goto L_0x005b
            boolean r1 = r8.mBinding     // Catch:{ all -> 0x005d }
            if (r1 == 0) goto L_0x000c
            goto L_0x005b
        L_0x000c:
            boolean r1 = r8.mVerbose     // Catch:{ all -> 0x005d }
            if (r1 == 0) goto L_0x0017
            java.lang.String r1 = TAG     // Catch:{ all -> 0x005d }
            java.lang.String r2 = "ensureBound(): binding"
            android.util.Slog.v(r1, r2)     // Catch:{ all -> 0x005d }
        L_0x0017:
            r1 = 1
            r8.mBinding = r1     // Catch:{ all -> 0x005d }
            r1 = 67108865(0x4000001, float:1.504633E-36)
            android.content.Context r2 = r8.mContext     // Catch:{ all -> 0x005d }
            android.content.Intent r3 = r8.mIntent     // Catch:{ all -> 0x005d }
            com.android.server.systemcaptions.RemoteSystemCaptionsManagerService$RemoteServiceConnection r4 = r8.mServiceConnection     // Catch:{ all -> 0x005d }
            android.os.Handler r6 = r8.mHandler     // Catch:{ all -> 0x005d }
            android.os.UserHandle r7 = new android.os.UserHandle     // Catch:{ all -> 0x005d }
            int r5 = r8.mUserId     // Catch:{ all -> 0x005d }
            r7.<init>(r5)     // Catch:{ all -> 0x005d }
            r5 = r1
            boolean r2 = r2.bindServiceAsUser(r3, r4, r5, r6, r7)     // Catch:{ all -> 0x005d }
            if (r2 != 0) goto L_0x0059
            java.lang.String r3 = TAG     // Catch:{ all -> 0x005d }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x005d }
            r4.<init>()     // Catch:{ all -> 0x005d }
            java.lang.String r5 = "Could not bind to "
            r4.append(r5)     // Catch:{ all -> 0x005d }
            android.content.Intent r5 = r8.mIntent     // Catch:{ all -> 0x005d }
            r4.append(r5)     // Catch:{ all -> 0x005d }
            java.lang.String r5 = " with flags "
            r4.append(r5)     // Catch:{ all -> 0x005d }
            r4.append(r1)     // Catch:{ all -> 0x005d }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x005d }
            android.util.Slog.w(r3, r4)     // Catch:{ all -> 0x005d }
            r3 = 0
            r8.mBinding = r3     // Catch:{ all -> 0x005d }
            r3 = 0
            r8.mService = r3     // Catch:{ all -> 0x005d }
        L_0x0059:
            monitor-exit(r0)     // Catch:{ all -> 0x005d }
            return
        L_0x005b:
            monitor-exit(r0)     // Catch:{ all -> 0x005d }
            return
        L_0x005d:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x005d }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.systemcaptions.RemoteSystemCaptionsManagerService.ensureBound():void");
    }

    @GuardedBy({"mLock"})
    private void ensureUnboundLocked() {
        if (this.mService != null || this.mBinding) {
            this.mBinding = false;
            this.mService = null;
            if (this.mVerbose) {
                Slog.v(TAG, "ensureUnbound(): unbinding");
            }
            this.mContext.unbindService(this.mServiceConnection);
        }
    }

    private class RemoteServiceConnection implements ServiceConnection {
        private RemoteServiceConnection() {
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            synchronized (RemoteSystemCaptionsManagerService.this.mLock) {
                if (RemoteSystemCaptionsManagerService.this.mVerbose) {
                    Slog.v(RemoteSystemCaptionsManagerService.TAG, "onServiceConnected()");
                }
                if (!RemoteSystemCaptionsManagerService.this.mDestroyed) {
                    if (RemoteSystemCaptionsManagerService.this.mBinding) {
                        boolean unused = RemoteSystemCaptionsManagerService.this.mBinding = false;
                        IBinder unused2 = RemoteSystemCaptionsManagerService.this.mService = service;
                        return;
                    }
                }
                Slog.wtf(RemoteSystemCaptionsManagerService.TAG, "onServiceConnected() dispatched after unbindService");
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            synchronized (RemoteSystemCaptionsManagerService.this.mLock) {
                if (RemoteSystemCaptionsManagerService.this.mVerbose) {
                    Slog.v(RemoteSystemCaptionsManagerService.TAG, "onServiceDisconnected()");
                }
                boolean unused = RemoteSystemCaptionsManagerService.this.mBinding = true;
                IBinder unused2 = RemoteSystemCaptionsManagerService.this.mService = null;
            }
        }
    }
}
