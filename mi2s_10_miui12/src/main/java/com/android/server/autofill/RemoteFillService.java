package com.android.server.autofill;

import android.content.ComponentName;
import android.content.Context;
import android.content.IntentSender;
import android.os.Handler;
import android.os.IBinder;
import android.os.ICancellationSignal;
import android.os.RemoteException;
import android.os.SystemClock;
import android.service.autofill.FillRequest;
import android.service.autofill.FillResponse;
import android.service.autofill.IAutoFillService;
import android.service.autofill.IFillCallback;
import android.service.autofill.ISaveCallback;
import android.service.autofill.SaveRequest;
import android.util.Slog;
import com.android.internal.infra.AbstractRemoteService;
import com.android.internal.infra.AbstractSinglePendingRequestRemoteService;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.server.autofill.RemoteFillService;
import com.android.server.pm.DumpState;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

final class RemoteFillService extends AbstractSinglePendingRequestRemoteService<RemoteFillService, IAutoFillService> {
    private static final long TIMEOUT_IDLE_BIND_MILLIS = 5000;
    private static final long TIMEOUT_REMOTE_REQUEST_MILLIS = 5000;
    private final FillServiceCallbacks mCallbacks;

    public interface FillServiceCallbacks extends AbstractRemoteService.VultureCallback<RemoteFillService> {
        void onFillRequestFailure(int i, CharSequence charSequence);

        void onFillRequestSuccess(int i, FillResponse fillResponse, String str, int i2);

        void onFillRequestTimeout(int i);

        void onSaveRequestFailure(CharSequence charSequence, String str);

        void onSaveRequestSuccess(String str, IntentSender intentSender);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    RemoteFillService(Context context, ComponentName componentName, int userId, FillServiceCallbacks callbacks, boolean bindInstantServiceAllowed) {
        super(context, "android.service.autofill.AutofillService", componentName, userId, callbacks, context.getMainThreadHandler(), (bindInstantServiceAllowed ? DumpState.DUMP_CHANGES : 0) | DumpState.DUMP_DEXOPT, Helper.sVerbose);
        this.mCallbacks = callbacks;
    }

    /* access modifiers changed from: protected */
    public void handleOnConnectedStateChanged(boolean state) {
        if (this.mService == null) {
            Slog.w(this.mTag, "onConnectedStateChanged(): null service");
            return;
        }
        try {
            this.mService.onConnectedStateChanged(state);
        } catch (Exception e) {
            String str = this.mTag;
            Slog.w(str, "Exception calling onConnectedStateChanged(" + state + "): " + e);
        }
    }

    /* access modifiers changed from: protected */
    public IAutoFillService getServiceInterface(IBinder service) {
        return IAutoFillService.Stub.asInterface(service);
    }

    /* access modifiers changed from: protected */
    public long getTimeoutIdleBindMillis() {
        return 5000;
    }

    /* access modifiers changed from: protected */
    public long getRemoteRequestMillis() {
        return 5000;
    }

    /* access modifiers changed from: protected */
    public void handleUnbind() {
        RemoteFillService.super.handleUnbind();
    }

    public CompletableFuture<Integer> cancelCurrentRequest() {
        $$Lambda$RemoteFillService$_BUUnv78CuBw5KA9LSgPsdJ9MjM r0 = new Supplier() {
            public final Object get() {
                return RemoteFillService.this.lambda$cancelCurrentRequest$0$RemoteFillService();
            }
        };
        Handler handler = this.mHandler;
        Objects.requireNonNull(handler);
        return CompletableFuture.supplyAsync(r0, new Executor(handler) {
            private final /* synthetic */ Handler f$0;

            {
                this.f$0 = r1;
            }

            public final void execute(Runnable runnable) {
                this.f$0.post(runnable);
            }
        });
    }

    public /* synthetic */ Integer lambda$cancelCurrentRequest$0$RemoteFillService() {
        int i = Integer.MIN_VALUE;
        if (isDestroyed()) {
            return Integer.MIN_VALUE;
        }
        PendingFillRequest handleCancelPendingRequest = handleCancelPendingRequest();
        if (handleCancelPendingRequest instanceof PendingFillRequest) {
            i = handleCancelPendingRequest.mRequest.getId();
        }
        return Integer.valueOf(i);
    }

    public void onFillRequest(FillRequest request) {
        scheduleRequest(new PendingFillRequest(request, this));
    }

    public void onSaveRequest(SaveRequest request) {
        scheduleRequest(new PendingSaveRequest(request, this));
    }

    private boolean handleResponseCallbackCommon(AbstractRemoteService.PendingRequest<RemoteFillService, IAutoFillService> pendingRequest) {
        if (isDestroyed()) {
            return false;
        }
        if (this.mPendingRequest != pendingRequest) {
            return true;
        }
        this.mPendingRequest = null;
        return true;
    }

    /* access modifiers changed from: private */
    public void dispatchOnFillRequestSuccess(PendingFillRequest pendingRequest, FillResponse response, int requestFlags) {
        this.mHandler.post(new Runnable(pendingRequest, response, requestFlags) {
            private final /* synthetic */ RemoteFillService.PendingFillRequest f$1;
            private final /* synthetic */ FillResponse f$2;
            private final /* synthetic */ int f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                RemoteFillService.this.lambda$dispatchOnFillRequestSuccess$1$RemoteFillService(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$dispatchOnFillRequestSuccess$1$RemoteFillService(PendingFillRequest pendingRequest, FillResponse response, int requestFlags) {
        if (handleResponseCallbackCommon(pendingRequest)) {
            this.mCallbacks.onFillRequestSuccess(pendingRequest.mRequest.getId(), response, this.mComponentName.getPackageName(), requestFlags);
        }
    }

    /* access modifiers changed from: private */
    public void dispatchOnFillRequestFailure(PendingFillRequest pendingRequest, CharSequence message) {
        this.mHandler.post(new Runnable(pendingRequest, message) {
            private final /* synthetic */ RemoteFillService.PendingFillRequest f$1;
            private final /* synthetic */ CharSequence f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                RemoteFillService.this.lambda$dispatchOnFillRequestFailure$2$RemoteFillService(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$dispatchOnFillRequestFailure$2$RemoteFillService(PendingFillRequest pendingRequest, CharSequence message) {
        if (handleResponseCallbackCommon(pendingRequest)) {
            this.mCallbacks.onFillRequestFailure(pendingRequest.mRequest.getId(), message);
        }
    }

    /* access modifiers changed from: private */
    public void dispatchOnFillRequestTimeout(PendingFillRequest pendingRequest) {
        this.mHandler.post(new Runnable(pendingRequest) {
            private final /* synthetic */ RemoteFillService.PendingFillRequest f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                RemoteFillService.this.lambda$dispatchOnFillRequestTimeout$3$RemoteFillService(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$dispatchOnFillRequestTimeout$3$RemoteFillService(PendingFillRequest pendingRequest) {
        if (handleResponseCallbackCommon(pendingRequest)) {
            this.mCallbacks.onFillRequestTimeout(pendingRequest.mRequest.getId());
        }
    }

    /* access modifiers changed from: private */
    public void dispatchOnFillTimeout(ICancellationSignal cancellationSignal) {
        this.mHandler.post(new Runnable(cancellationSignal) {
            private final /* synthetic */ ICancellationSignal f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                RemoteFillService.this.lambda$dispatchOnFillTimeout$4$RemoteFillService(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$dispatchOnFillTimeout$4$RemoteFillService(ICancellationSignal cancellationSignal) {
        try {
            cancellationSignal.cancel();
        } catch (RemoteException e) {
            String str = this.mTag;
            Slog.w(str, "Error calling cancellation signal: " + e);
        }
    }

    /* access modifiers changed from: private */
    public void dispatchOnSaveRequestSuccess(PendingSaveRequest pendingRequest, IntentSender intentSender) {
        this.mHandler.post(new Runnable(pendingRequest, intentSender) {
            private final /* synthetic */ RemoteFillService.PendingSaveRequest f$1;
            private final /* synthetic */ IntentSender f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                RemoteFillService.this.lambda$dispatchOnSaveRequestSuccess$5$RemoteFillService(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$dispatchOnSaveRequestSuccess$5$RemoteFillService(PendingSaveRequest pendingRequest, IntentSender intentSender) {
        if (handleResponseCallbackCommon(pendingRequest)) {
            this.mCallbacks.onSaveRequestSuccess(this.mComponentName.getPackageName(), intentSender);
        }
    }

    /* access modifiers changed from: private */
    public void dispatchOnSaveRequestFailure(PendingSaveRequest pendingRequest, CharSequence message) {
        this.mHandler.post(new Runnable(pendingRequest, message) {
            private final /* synthetic */ RemoteFillService.PendingSaveRequest f$1;
            private final /* synthetic */ CharSequence f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                RemoteFillService.this.lambda$dispatchOnSaveRequestFailure$6$RemoteFillService(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$dispatchOnSaveRequestFailure$6$RemoteFillService(PendingSaveRequest pendingRequest, CharSequence message) {
        if (handleResponseCallbackCommon(pendingRequest)) {
            this.mCallbacks.onSaveRequestFailure(message, this.mComponentName.getPackageName());
        }
    }

    private static final class PendingFillRequest extends AbstractRemoteService.PendingRequest<RemoteFillService, IAutoFillService> {
        private final IFillCallback mCallback;
        /* access modifiers changed from: private */
        public ICancellationSignal mCancellation;
        /* access modifiers changed from: private */
        public final FillRequest mRequest;

        public PendingFillRequest(final FillRequest request, RemoteFillService service) {
            super(service);
            this.mRequest = request;
            this.mCallback = new IFillCallback.Stub() {
                /* Debug info: failed to restart local var, previous not found, register: 5 */
                public void onCancellable(ICancellationSignal cancellation) {
                    boolean cancelled;
                    synchronized (PendingFillRequest.this.mLock) {
                        synchronized (PendingFillRequest.this.mLock) {
                            ICancellationSignal unused = PendingFillRequest.this.mCancellation = cancellation;
                            cancelled = PendingFillRequest.this.isCancelledLocked();
                        }
                        if (cancelled) {
                            try {
                                cancellation.cancel();
                            } catch (RemoteException e) {
                                Slog.e(PendingFillRequest.this.mTag, "Error requesting a cancellation", e);
                            }
                        }
                    }
                }

                public void onSuccess(FillResponse response) {
                    RemoteFillService remoteService;
                    if (((response != null && response.isContainCaptcha() && response.getDatasets() == null) || PendingFillRequest.this.finish()) && (remoteService = PendingFillRequest.this.getService()) != null) {
                        if (response != null && response.isContainCaptcha()) {
                            remoteService.mHandler.removeCallbacks(PendingFillRequest.this.mTimeoutTrigger);
                            remoteService.mHandler.postAtTime(PendingFillRequest.this.mTimeoutTrigger, SystemClock.uptimeMillis() + 50000);
                            remoteService.mHandler.removeMessages(2);
                            remoteService.mHandler.sendMessageDelayed(PooledLambda.obtainMessage($$Lambda$azBot91HkyT4s82OXKnd8RQNKBo.INSTANCE, remoteService).setWhat(2), 50000);
                        }
                        remoteService.dispatchOnFillRequestSuccess(PendingFillRequest.this, response, request.getFlags());
                    }
                }

                public void onFailure(int requestId, CharSequence message) {
                    RemoteFillService remoteService;
                    if (PendingFillRequest.this.finish() && (remoteService = PendingFillRequest.this.getService()) != null) {
                        remoteService.dispatchOnFillRequestFailure(PendingFillRequest.this, message);
                    }
                }
            };
        }

        /* access modifiers changed from: protected */
        public void onTimeout(RemoteFillService remoteService) {
            ICancellationSignal cancellation;
            synchronized (this.mLock) {
                cancellation = this.mCancellation;
            }
            if (cancellation != null) {
                remoteService.dispatchOnFillTimeout(cancellation);
            }
            remoteService.dispatchOnFillRequestTimeout(this);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:11:0x0029, code lost:
            r0 = getService();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:12:0x002f, code lost:
            if (r0 == null) goto L_?;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0033, code lost:
            if (com.android.server.autofill.Helper.sVerbose == false) goto L_0x0051;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:15:0x0035, code lost:
            r1 = r4.mTag;
            android.util.Slog.v(r1, "calling onFillRequest() for id=" + r4.mRequest.getId());
         */
        /* JADX WARNING: Code restructure failed: missing block: B:17:?, code lost:
            com.android.server.autofill.RemoteFillService.access$1900(r0).onFillRequest(r4.mRequest, r4.mCallback);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:18:0x005f, code lost:
            r1 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:19:0x0060, code lost:
            android.util.Slog.e(r4.mTag, "Error calling on fill request", r1);
            com.android.server.autofill.RemoteFillService.access$1600(r0, r4, (java.lang.CharSequence) null);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:27:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:28:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:29:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:9:0x0027, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
                r4 = this;
                java.lang.Object r0 = r4.mLock
                monitor-enter(r0)
                boolean r1 = r4.isCancelledLocked()     // Catch:{ all -> 0x006c }
                if (r1 == 0) goto L_0x0028
                boolean r1 = com.android.server.autofill.Helper.sDebug     // Catch:{ all -> 0x006c }
                if (r1 == 0) goto L_0x0026
                java.lang.String r1 = r4.mTag     // Catch:{ all -> 0x006c }
                java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x006c }
                r2.<init>()     // Catch:{ all -> 0x006c }
                java.lang.String r3 = "run() called after canceled: "
                r2.append(r3)     // Catch:{ all -> 0x006c }
                android.service.autofill.FillRequest r3 = r4.mRequest     // Catch:{ all -> 0x006c }
                r2.append(r3)     // Catch:{ all -> 0x006c }
                java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x006c }
                android.util.Slog.d(r1, r2)     // Catch:{ all -> 0x006c }
            L_0x0026:
                monitor-exit(r0)     // Catch:{ all -> 0x006c }
                return
            L_0x0028:
                monitor-exit(r0)     // Catch:{ all -> 0x006c }
                com.android.internal.infra.AbstractRemoteService r0 = r4.getService()
                com.android.server.autofill.RemoteFillService r0 = (com.android.server.autofill.RemoteFillService) r0
                if (r0 == 0) goto L_0x006b
                boolean r1 = com.android.server.autofill.Helper.sVerbose
                if (r1 == 0) goto L_0x0051
                java.lang.String r1 = r4.mTag
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                r2.<init>()
                java.lang.String r3 = "calling onFillRequest() for id="
                r2.append(r3)
                android.service.autofill.FillRequest r3 = r4.mRequest
                int r3 = r3.getId()
                r2.append(r3)
                java.lang.String r2 = r2.toString()
                android.util.Slog.v(r1, r2)
            L_0x0051:
                android.os.IInterface r1 = r0.mService     // Catch:{ RemoteException -> 0x005f }
                android.service.autofill.IAutoFillService r1 = (android.service.autofill.IAutoFillService) r1     // Catch:{ RemoteException -> 0x005f }
                android.service.autofill.FillRequest r2 = r4.mRequest     // Catch:{ RemoteException -> 0x005f }
                android.service.autofill.IFillCallback r3 = r4.mCallback     // Catch:{ RemoteException -> 0x005f }
                r1.onFillRequest(r2, r3)     // Catch:{ RemoteException -> 0x005f }
                goto L_0x006b
            L_0x005f:
                r1 = move-exception
                java.lang.String r2 = r4.mTag
                java.lang.String r3 = "Error calling on fill request"
                android.util.Slog.e(r2, r3, r1)
                r2 = 0
                r0.dispatchOnFillRequestFailure(r4, r2)
            L_0x006b:
                return
            L_0x006c:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x006c }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.autofill.RemoteFillService.PendingFillRequest.run():void");
        }

        public boolean cancel() {
            ICancellationSignal cancellation;
            if (!RemoteFillService.super.cancel()) {
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
                Slog.e(this.mTag, "Error cancelling a fill request", e);
                return true;
            }
        }
    }

    private static final class PendingSaveRequest extends AbstractRemoteService.PendingRequest<RemoteFillService, IAutoFillService> {
        private final ISaveCallback mCallback = new ISaveCallback.Stub() {
            public void onSuccess(IntentSender intentSender) {
                RemoteFillService remoteService;
                if (PendingSaveRequest.this.finish() && (remoteService = PendingSaveRequest.this.getService()) != null) {
                    remoteService.dispatchOnSaveRequestSuccess(PendingSaveRequest.this, intentSender);
                }
            }

            public void onFailure(CharSequence message) {
                RemoteFillService remoteService;
                if (PendingSaveRequest.this.finish() && (remoteService = PendingSaveRequest.this.getService()) != null) {
                    remoteService.dispatchOnSaveRequestFailure(PendingSaveRequest.this, message);
                }
            }
        };
        private final SaveRequest mRequest;

        public PendingSaveRequest(SaveRequest request, RemoteFillService service) {
            super(service);
            this.mRequest = request;
        }

        /* access modifiers changed from: protected */
        public void onTimeout(RemoteFillService remoteService) {
            remoteService.dispatchOnSaveRequestFailure(this, (CharSequence) null);
        }

        public void run() {
            RemoteFillService remoteService = getService();
            if (remoteService != null) {
                if (Helper.sVerbose) {
                    Slog.v(this.mTag, "calling onSaveRequest()");
                }
                try {
                    remoteService.mService.onSaveRequest(this.mRequest, this.mCallback);
                } catch (RemoteException e) {
                    Slog.e(this.mTag, "Error calling on save request", e);
                    remoteService.dispatchOnSaveRequestFailure(this, (CharSequence) null);
                }
            }
        }

        public boolean isFinal() {
            return true;
        }
    }
}
