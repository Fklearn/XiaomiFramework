package com.android.server.autofill;

import android.app.AppGlobals;
import android.content.ComponentName;
import android.content.pm.ServiceInfo;
import android.os.IBinder;
import android.os.ICancellationSignal;
import android.os.RemoteException;
import android.os.SystemClock;
import android.service.autofill.augmented.Helper;
import android.service.autofill.augmented.IAugmentedAutofillService;
import android.service.autofill.augmented.IFillCallback;
import android.util.Pair;
import android.util.Slog;
import android.view.autofill.AutofillId;
import android.view.autofill.AutofillValue;
import android.view.autofill.IAutoFillManagerClient;
import com.android.internal.infra.AbstractRemoteService;
import com.android.internal.infra.AbstractSinglePendingRequestRemoteService;
import com.android.server.pm.DumpState;

final class RemoteAugmentedAutofillService extends AbstractSinglePendingRequestRemoteService<RemoteAugmentedAutofillService, IAugmentedAutofillService> {
    /* access modifiers changed from: private */
    public static final String TAG = RemoteAugmentedAutofillService.class.getSimpleName();
    private final int mIdleUnbindTimeoutMs;
    /* access modifiers changed from: private */
    public final int mRequestTimeoutMs;

    public interface RemoteAugmentedAutofillServiceCallbacks extends AbstractRemoteService.VultureCallback<RemoteAugmentedAutofillService> {
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    RemoteAugmentedAutofillService(android.content.Context r11, android.content.ComponentName r12, int r13, com.android.server.autofill.RemoteAugmentedAutofillService.RemoteAugmentedAutofillServiceCallbacks r14, boolean r15, boolean r16, int r17, int r18) {
        /*
            r10 = this;
            r9 = r10
            android.os.Handler r6 = r11.getMainThreadHandler()
            if (r15 == 0) goto L_0x000a
            r0 = 4194304(0x400000, float:5.877472E-39)
            goto L_0x000b
        L_0x000a:
            r0 = 0
        L_0x000b:
            r7 = r0
            java.lang.String r2 = "android.service.autofill.augmented.AugmentedAutofillService"
            r0 = r10
            r1 = r11
            r3 = r12
            r4 = r13
            r5 = r14
            r8 = r16
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8)
            r0 = r17
            r9.mIdleUnbindTimeoutMs = r0
            r1 = r18
            r9.mRequestTimeoutMs = r1
            r10.scheduleBind()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.autofill.RemoteAugmentedAutofillService.<init>(android.content.Context, android.content.ComponentName, int, com.android.server.autofill.RemoteAugmentedAutofillService$RemoteAugmentedAutofillServiceCallbacks, boolean, boolean, int, int):void");
    }

    static Pair<ServiceInfo, ComponentName> getComponentName(String componentName, int userId, boolean isTemporary) {
        int flags = 128;
        if (!isTemporary) {
            flags = 128 | DumpState.DUMP_DEXOPT;
        }
        try {
            ComponentName serviceComponent = ComponentName.unflattenFromString(componentName);
            ServiceInfo serviceInfo = AppGlobals.getPackageManager().getServiceInfo(serviceComponent, flags, userId);
            if (serviceInfo != null) {
                return new Pair<>(serviceInfo, serviceComponent);
            }
            String str = TAG;
            Slog.e(str, "Bad service name for flags " + flags + ": " + componentName);
            return null;
        } catch (Exception e) {
            String str2 = TAG;
            Slog.e(str2, "Error getting service info for '" + componentName + "': " + e);
            return null;
        }
    }

    /* access modifiers changed from: protected */
    public void handleOnConnectedStateChanged(boolean state) {
        if (state && getTimeoutIdleBindMillis() != 0) {
            scheduleUnbind();
        }
        if (state) {
            try {
                this.mService.onConnected(Helper.sDebug, Helper.sVerbose);
            } catch (Exception e) {
                String str = this.mTag;
                Slog.w(str, "Exception calling onConnectedStateChanged(" + state + "): " + e);
            }
        } else {
            this.mService.onDisconnected();
        }
    }

    /* access modifiers changed from: protected */
    public IAugmentedAutofillService getServiceInterface(IBinder service) {
        return IAugmentedAutofillService.Stub.asInterface(service);
    }

    /* access modifiers changed from: protected */
    public long getTimeoutIdleBindMillis() {
        return (long) this.mIdleUnbindTimeoutMs;
    }

    /* access modifiers changed from: protected */
    public long getRemoteRequestMillis() {
        return (long) this.mRequestTimeoutMs;
    }

    public void onRequestAutofillLocked(int sessionId, IAutoFillManagerClient client, int taskId, ComponentName activityComponent, AutofillId focusedId, AutofillValue focusedValue) {
        scheduleRequest(new PendingAutofillRequest(this, sessionId, client, taskId, activityComponent, focusedId, focusedValue));
    }

    public String toString() {
        return "RemoteAugmentedAutofillService[" + ComponentName.flattenToShortString(getComponentName()) + "]";
    }

    public void onDestroyAutofillWindowsRequest() {
        scheduleAsyncRequest($$Lambda$RemoteAugmentedAutofillService$e7zSmzv77rBdYV5oClY8EJj9dY.INSTANCE);
    }

    /* access modifiers changed from: private */
    public void dispatchOnFillTimeout(ICancellationSignal cancellation) {
        this.mHandler.post(new Runnable(cancellation) {
            private final /* synthetic */ ICancellationSignal f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                RemoteAugmentedAutofillService.this.lambda$dispatchOnFillTimeout$1$RemoteAugmentedAutofillService(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$dispatchOnFillTimeout$1$RemoteAugmentedAutofillService(ICancellationSignal cancellation) {
        try {
            cancellation.cancel();
        } catch (RemoteException e) {
            String str = this.mTag;
            Slog.w(str, "Error calling cancellation signal: " + e);
        }
    }

    private static abstract class MyPendingRequest extends AbstractRemoteService.PendingRequest<RemoteAugmentedAutofillService, IAugmentedAutofillService> {
        protected final int mSessionId;

        private MyPendingRequest(RemoteAugmentedAutofillService service, int sessionId) {
            super(service);
            this.mSessionId = sessionId;
        }
    }

    private static final class PendingAutofillRequest extends MyPendingRequest {
        /* access modifiers changed from: private */
        public final ComponentName mActivityComponent;
        /* access modifiers changed from: private */
        public final IFillCallback mCallback;
        /* access modifiers changed from: private */
        public ICancellationSignal mCancellation;
        private final IAutoFillManagerClient mClient;
        /* access modifiers changed from: private */
        public final AutofillId mFocusedId;
        /* access modifiers changed from: private */
        public final AutofillValue mFocusedValue;
        /* access modifiers changed from: private */
        public final long mRequestTime = SystemClock.elapsedRealtime();
        /* access modifiers changed from: private */
        public final int mSessionId;
        /* access modifiers changed from: private */
        public final int mTaskId;

        protected PendingAutofillRequest(RemoteAugmentedAutofillService service, int sessionId, IAutoFillManagerClient client, int taskId, ComponentName activityComponent, AutofillId focusedId, AutofillValue focusedValue) {
            super(sessionId);
            this.mClient = client;
            this.mSessionId = sessionId;
            this.mTaskId = taskId;
            this.mActivityComponent = activityComponent;
            this.mFocusedId = focusedId;
            this.mFocusedValue = focusedValue;
            this.mCallback = new IFillCallback.Stub() {
                public void onSuccess() {
                    if (PendingAutofillRequest.this.finish()) {
                    }
                }

                /* Debug info: failed to restart local var, previous not found, register: 5 */
                public void onCancellable(ICancellationSignal cancellation) {
                    boolean cancelled;
                    synchronized (PendingAutofillRequest.this.mLock) {
                        synchronized (PendingAutofillRequest.this.mLock) {
                            ICancellationSignal unused = PendingAutofillRequest.this.mCancellation = cancellation;
                            cancelled = PendingAutofillRequest.this.isCancelledLocked();
                        }
                        if (cancelled) {
                            try {
                                cancellation.cancel();
                            } catch (RemoteException e) {
                                Slog.e(PendingAutofillRequest.this.mTag, "Error requesting a cancellation", e);
                            }
                        }
                    }
                }

                public boolean isCompleted() {
                    return PendingAutofillRequest.this.isRequestCompleted();
                }

                public void cancel() {
                    PendingAutofillRequest.this.cancel();
                }
            };
        }

        /* JADX WARNING: Code restructure failed: missing block: B:11:0x0018, code lost:
            r0 = getService();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:12:0x001e, code lost:
            if (r0 != null) goto L_0x0021;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:13:0x0020, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:16:?, code lost:
            r6.mClient.getAugmentedAutofillClient(new com.android.server.autofill.RemoteAugmentedAutofillService.PendingAutofillRequest.AnonymousClass2(r6));
         */
        /* JADX WARNING: Code restructure failed: missing block: B:17:0x002c, code lost:
            r2 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:18:0x002d, code lost:
            r3 = com.android.server.autofill.RemoteAugmentedAutofillService.access$1600();
            android.util.Slog.e(r3, "exception handling getAugmentedAutofillClient() for " + r6.mSessionId + ": " + r2);
            finish();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:26:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:27:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:9:0x0016, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
                r6 = this;
                java.lang.Object r0 = r6.mLock
                monitor-enter(r0)
                boolean r1 = r6.isCancelledLocked()     // Catch:{ all -> 0x0053 }
                if (r1 == 0) goto L_0x0017
                boolean r1 = com.android.server.autofill.Helper.sDebug     // Catch:{ all -> 0x0053 }
                if (r1 == 0) goto L_0x0015
                java.lang.String r1 = r6.mTag     // Catch:{ all -> 0x0053 }
                java.lang.String r2 = "run() called after canceled"
                android.util.Slog.d(r1, r2)     // Catch:{ all -> 0x0053 }
            L_0x0015:
                monitor-exit(r0)     // Catch:{ all -> 0x0053 }
                return
            L_0x0017:
                monitor-exit(r0)     // Catch:{ all -> 0x0053 }
                com.android.internal.infra.AbstractRemoteService r0 = r6.getService()
                com.android.server.autofill.RemoteAugmentedAutofillService r0 = (com.android.server.autofill.RemoteAugmentedAutofillService) r0
                if (r0 != 0) goto L_0x0021
                return
            L_0x0021:
                com.android.server.autofill.RemoteAugmentedAutofillService$PendingAutofillRequest$2 r1 = new com.android.server.autofill.RemoteAugmentedAutofillService$PendingAutofillRequest$2
                r1.<init>(r0)
                android.view.autofill.IAutoFillManagerClient r2 = r6.mClient     // Catch:{ RemoteException -> 0x002c }
                r2.getAugmentedAutofillClient(r1)     // Catch:{ RemoteException -> 0x002c }
                goto L_0x0052
            L_0x002c:
                r2 = move-exception
                java.lang.String r3 = com.android.server.autofill.RemoteAugmentedAutofillService.TAG
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                java.lang.String r5 = "exception handling getAugmentedAutofillClient() for "
                r4.append(r5)
                int r5 = r6.mSessionId
                r4.append(r5)
                java.lang.String r5 = ": "
                r4.append(r5)
                r4.append(r2)
                java.lang.String r4 = r4.toString()
                android.util.Slog.e(r3, r4)
                r6.finish()
            L_0x0052:
                return
            L_0x0053:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0053 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.autofill.RemoteAugmentedAutofillService.PendingAutofillRequest.run():void");
        }

        /* access modifiers changed from: protected */
        public void onTimeout(RemoteAugmentedAutofillService remoteService) {
            ICancellationSignal cancellation;
            String access$1600 = RemoteAugmentedAutofillService.TAG;
            Slog.w(access$1600, "PendingAutofillRequest timed out (" + remoteService.mRequestTimeoutMs + "ms) for " + remoteService);
            synchronized (this.mLock) {
                cancellation = this.mCancellation;
            }
            if (cancellation != null) {
                remoteService.dispatchOnFillTimeout(cancellation);
            }
            finish();
            Helper.logResponse(15, remoteService.getComponentName().getPackageName(), this.mActivityComponent, this.mSessionId, (long) remoteService.mRequestTimeoutMs);
        }

        public boolean cancel() {
            ICancellationSignal cancellation;
            if (!super.cancel()) {
                return false;
            }
            synchronized (this.mLock) {
                cancellation = this.mCancellation;
            }
            if (cancellation == null) {
                return true;
            }
            try {
                cancellation.cancel();
                return true;
            } catch (RemoteException e) {
                Slog.e(this.mTag, "Error cancelling an augmented fill request", e);
                return true;
            }
        }
    }
}
