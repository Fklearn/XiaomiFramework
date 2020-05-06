package com.android.server.contentsuggestions;

import android.app.contentsuggestions.ClassificationsRequest;
import android.app.contentsuggestions.IClassificationsCallback;
import android.app.contentsuggestions.ISelectionsCallback;
import android.app.contentsuggestions.SelectionsRequest;
import android.graphics.GraphicBuffer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.service.contentsuggestions.IContentSuggestionsService;
import com.android.internal.infra.AbstractMultiplePendingRequestsRemoteService;
import com.android.internal.infra.AbstractRemoteService;

public class RemoteContentSuggestionsService extends AbstractMultiplePendingRequestsRemoteService<RemoteContentSuggestionsService, IContentSuggestionsService> {
    private static final long TIMEOUT_REMOTE_REQUEST_MILLIS = 2000;

    interface Callbacks extends AbstractRemoteService.VultureCallback<RemoteContentSuggestionsService> {
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    RemoteContentSuggestionsService(android.content.Context r11, android.content.ComponentName r12, int r13, com.android.server.contentsuggestions.RemoteContentSuggestionsService.Callbacks r14, boolean r15, boolean r16) {
        /*
            r10 = this;
            android.os.Handler r6 = r11.getMainThreadHandler()
            if (r15 == 0) goto L_0x000a
            r0 = 4194304(0x400000, float:5.877472E-39)
            goto L_0x000b
        L_0x000a:
            r0 = 0
        L_0x000b:
            r7 = r0
            r9 = 1
            java.lang.String r2 = "android.service.contentsuggestions.ContentSuggestionsService"
            r0 = r10
            r1 = r11
            r3 = r12
            r4 = r13
            r5 = r14
            r8 = r16
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.contentsuggestions.RemoteContentSuggestionsService.<init>(android.content.Context, android.content.ComponentName, int, com.android.server.contentsuggestions.RemoteContentSuggestionsService$Callbacks, boolean, boolean):void");
    }

    /* access modifiers changed from: protected */
    public IContentSuggestionsService getServiceInterface(IBinder service) {
        return IContentSuggestionsService.Stub.asInterface(service);
    }

    /* access modifiers changed from: protected */
    public long getTimeoutIdleBindMillis() {
        return 0;
    }

    /* access modifiers changed from: protected */
    public long getRemoteRequestMillis() {
        return TIMEOUT_REMOTE_REQUEST_MILLIS;
    }

    /* access modifiers changed from: package-private */
    public void provideContextImage(int taskId, GraphicBuffer contextImage, int colorSpaceId, Bundle imageContextRequestExtras) {
        scheduleAsyncRequest(new AbstractRemoteService.AsyncRequest(taskId, contextImage, colorSpaceId, imageContextRequestExtras) {
            private final /* synthetic */ int f$0;
            private final /* synthetic */ GraphicBuffer f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ Bundle f$3;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run(IInterface iInterface) {
                ((IContentSuggestionsService) iInterface).provideContextImage(this.f$0, this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: package-private */
    public void suggestContentSelections(SelectionsRequest selectionsRequest, ISelectionsCallback selectionsCallback) {
        scheduleAsyncRequest(new AbstractRemoteService.AsyncRequest(selectionsRequest, selectionsCallback) {
            private final /* synthetic */ SelectionsRequest f$0;
            private final /* synthetic */ ISelectionsCallback f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void run(IInterface iInterface) {
                ((IContentSuggestionsService) iInterface).suggestContentSelections(this.f$0, this.f$1);
            }
        });
    }

    /* access modifiers changed from: package-private */
    public void classifyContentSelections(ClassificationsRequest classificationsRequest, IClassificationsCallback callback) {
        scheduleAsyncRequest(new AbstractRemoteService.AsyncRequest(classificationsRequest, callback) {
            private final /* synthetic */ ClassificationsRequest f$0;
            private final /* synthetic */ IClassificationsCallback f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void run(IInterface iInterface) {
                ((IContentSuggestionsService) iInterface).classifyContentSelections(this.f$0, this.f$1);
            }
        });
    }

    /* access modifiers changed from: package-private */
    public void notifyInteraction(String requestId, Bundle bundle) {
        scheduleAsyncRequest(new AbstractRemoteService.AsyncRequest(requestId, bundle) {
            private final /* synthetic */ String f$0;
            private final /* synthetic */ Bundle f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void run(IInterface iInterface) {
                ((IContentSuggestionsService) iInterface).notifyInteraction(this.f$0, this.f$1);
            }
        });
    }
}
