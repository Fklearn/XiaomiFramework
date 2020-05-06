package com.android.server.contentcapture;

import android.content.ComponentName;
import android.os.IBinder;
import android.os.IInterface;
import android.service.contentcapture.ActivityEvent;
import android.service.contentcapture.IContentCaptureService;
import android.service.contentcapture.SnapshotData;
import android.util.Slog;
import android.view.contentcapture.ContentCaptureContext;
import android.view.contentcapture.ContentCaptureHelper;
import android.view.contentcapture.DataRemovalRequest;
import com.android.internal.infra.AbstractMultiplePendingRequestsRemoteService;
import com.android.internal.infra.AbstractRemoteService;
import com.android.internal.os.IResultReceiver;

final class RemoteContentCaptureService extends AbstractMultiplePendingRequestsRemoteService<RemoteContentCaptureService, IContentCaptureService> {
    private final int mIdleUnbindTimeoutMs;
    private final ContentCapturePerUserService mPerUserService;
    private final IBinder mServerCallback;

    public interface ContentCaptureServiceCallbacks extends AbstractRemoteService.VultureCallback<RemoteContentCaptureService> {
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    RemoteContentCaptureService(android.content.Context r12, java.lang.String r13, android.content.ComponentName r14, android.service.contentcapture.IContentCaptureServiceCallback r15, int r16, com.android.server.contentcapture.ContentCapturePerUserService r17, boolean r18, boolean r19, int r20) {
        /*
            r11 = this;
            r10 = r11
            android.os.Handler r6 = r12.getMainThreadHandler()
            if (r18 == 0) goto L_0x000a
            r0 = 4194304(0x400000, float:5.877472E-39)
            goto L_0x000b
        L_0x000a:
            r0 = 0
        L_0x000b:
            r7 = r0
            r9 = 2
            r0 = r11
            r1 = r12
            r2 = r13
            r3 = r14
            r4 = r16
            r5 = r17
            r8 = r19
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9)
            r0 = r17
            r10.mPerUserService = r0
            android.os.IBinder r1 = r15.asBinder()
            r10.mServerCallback = r1
            r1 = r20
            r10.mIdleUnbindTimeoutMs = r1
            r11.ensureBoundLocked()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.contentcapture.RemoteContentCaptureService.<init>(android.content.Context, java.lang.String, android.content.ComponentName, android.service.contentcapture.IContentCaptureServiceCallback, int, com.android.server.contentcapture.ContentCapturePerUserService, boolean, boolean, int):void");
    }

    /* access modifiers changed from: protected */
    public IContentCaptureService getServiceInterface(IBinder service) {
        return IContentCaptureService.Stub.asInterface(service);
    }

    /* access modifiers changed from: protected */
    public long getTimeoutIdleBindMillis() {
        return (long) this.mIdleUnbindTimeoutMs;
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* access modifiers changed from: protected */
    public void handleOnConnectedStateChanged(boolean connected) {
        if (connected && getTimeoutIdleBindMillis() != 0) {
            scheduleUnbind();
        }
        if (connected) {
            try {
                this.mService.onConnected(this.mServerCallback, ContentCaptureHelper.sVerbose, ContentCaptureHelper.sDebug);
                ContentCaptureMetricsLogger.writeServiceEvent(1, this.mComponentName);
                this.mPerUserService.onConnected();
            } catch (Exception e) {
                String str = this.mTag;
                Slog.w(str, "Exception calling onConnectedStateChanged(" + connected + "): " + e);
            } catch (Throwable th) {
                this.mPerUserService.onConnected();
                throw th;
            }
        } else {
            this.mService.onDisconnected();
            ContentCaptureMetricsLogger.writeServiceEvent(2, this.mComponentName);
        }
    }

    public void ensureBoundLocked() {
        scheduleBind();
    }

    public void onSessionStarted(ContentCaptureContext context, int sessionId, int uid, IResultReceiver clientReceiver, int initialState) {
        scheduleAsyncRequest(new AbstractRemoteService.AsyncRequest(context, sessionId, uid, clientReceiver, initialState) {
            private final /* synthetic */ ContentCaptureContext f$0;
            private final /* synthetic */ int f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ IResultReceiver f$3;
            private final /* synthetic */ int f$4;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run(IInterface iInterface) {
                ((IContentCaptureService) iInterface).onSessionStarted(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
        ContentCaptureMetricsLogger.writeSessionEvent(sessionId, 1, initialState, getComponentName(), context.getActivityComponent(), false);
    }

    public void onSessionFinished(int sessionId) {
        scheduleAsyncRequest(new AbstractRemoteService.AsyncRequest(sessionId) {
            private final /* synthetic */ int f$0;

            {
                this.f$0 = r1;
            }

            public final void run(IInterface iInterface) {
                ((IContentCaptureService) iInterface).onSessionFinished(this.f$0);
            }
        });
        ContentCaptureMetricsLogger.writeSessionEvent(sessionId, 2, 0, getComponentName(), (ComponentName) null, false);
    }

    public void onActivitySnapshotRequest(int sessionId, SnapshotData snapshotData) {
        scheduleAsyncRequest(new AbstractRemoteService.AsyncRequest(sessionId, snapshotData) {
            private final /* synthetic */ int f$0;
            private final /* synthetic */ SnapshotData f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void run(IInterface iInterface) {
                ((IContentCaptureService) iInterface).onActivitySnapshot(this.f$0, this.f$1);
            }
        });
    }

    public void onDataRemovalRequest(DataRemovalRequest request) {
        scheduleAsyncRequest(new AbstractRemoteService.AsyncRequest(request) {
            private final /* synthetic */ DataRemovalRequest f$0;

            {
                this.f$0 = r1;
            }

            public final void run(IInterface iInterface) {
                ((IContentCaptureService) iInterface).onDataRemovalRequest(this.f$0);
            }
        });
        ContentCaptureMetricsLogger.writeServiceEvent(5, this.mComponentName);
    }

    public void onActivityLifecycleEvent(ActivityEvent event) {
        scheduleAsyncRequest(new AbstractRemoteService.AsyncRequest(event) {
            private final /* synthetic */ ActivityEvent f$0;

            {
                this.f$0 = r1;
            }

            public final void run(IInterface iInterface) {
                ((IContentCaptureService) iInterface).onActivityEvent(this.f$0);
            }
        });
    }
}
