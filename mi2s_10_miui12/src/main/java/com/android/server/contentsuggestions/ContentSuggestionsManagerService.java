package com.android.server.contentsuggestions;

import android.app.contentsuggestions.ClassificationsRequest;
import android.app.contentsuggestions.IClassificationsCallback;
import android.app.contentsuggestions.IContentSuggestionsManager;
import android.app.contentsuggestions.ISelectionsCallback;
import android.app.contentsuggestions.SelectionsRequest;
import android.content.Context;
import android.os.Binder;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Slog;
import com.android.internal.os.IResultReceiver;
import com.android.server.LocalServices;
import com.android.server.infra.AbstractMasterSystemService;
import com.android.server.infra.FrameworkResourcesServiceNameResolver;
import com.android.server.wm.ActivityTaskManagerInternal;

public class ContentSuggestionsManagerService extends AbstractMasterSystemService<ContentSuggestionsManagerService, ContentSuggestionsPerUserService> {
    private static final int MAX_TEMP_SERVICE_DURATION_MS = 120000;
    /* access modifiers changed from: private */
    public static final String TAG = ContentSuggestionsManagerService.class.getSimpleName();
    private static final boolean VERBOSE = false;
    private ActivityTaskManagerInternal mActivityTaskManagerInternal = ((ActivityTaskManagerInternal) LocalServices.getService(ActivityTaskManagerInternal.class));

    public ContentSuggestionsManagerService(Context context) {
        super(context, new FrameworkResourcesServiceNameResolver(context, 17039726), "no_content_suggestions");
    }

    /* access modifiers changed from: protected */
    public ContentSuggestionsPerUserService newServiceLocked(int resolvedUserId, boolean disabled) {
        return new ContentSuggestionsPerUserService(this, this.mLock, resolvedUserId);
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [com.android.server.contentsuggestions.ContentSuggestionsManagerService$ContentSuggestionsManagerStub, android.os.IBinder] */
    public void onStart() {
        publishBinderService("content_suggestions", new ContentSuggestionsManagerStub());
    }

    /* access modifiers changed from: protected */
    public void enforceCallingPermissionForManagement() {
        getContext().enforceCallingPermission("android.permission.MANAGE_CONTENT_SUGGESTIONS", TAG);
    }

    /* access modifiers changed from: protected */
    public int getMaximumTemporaryServiceDurationMs() {
        return MAX_TEMP_SERVICE_DURATION_MS;
    }

    /* access modifiers changed from: private */
    public void enforceCaller(int userId, String func) {
        if (getContext().checkCallingPermission("android.permission.BIND_CONTENT_SUGGESTIONS_SERVICE") != 0 && !this.mServiceNameResolver.isTemporary(userId) && !this.mActivityTaskManagerInternal.isCallerRecents(Binder.getCallingUid())) {
            String msg = "Permission Denial: " + func + " from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " expected caller is recents";
            Slog.w(TAG, msg);
            throw new SecurityException(msg);
        }
    }

    private class ContentSuggestionsManagerStub extends IContentSuggestionsManager.Stub {
        private ContentSuggestionsManagerStub() {
        }

        public void provideContextImage(int userId, int taskId, Bundle imageContextRequestExtras) {
            if (imageContextRequestExtras != null) {
                ContentSuggestionsManagerService.this.enforceCaller(UserHandle.getCallingUserId(), "provideContextImage");
                synchronized (ContentSuggestionsManagerService.this.mLock) {
                    ContentSuggestionsPerUserService service = (ContentSuggestionsPerUserService) ContentSuggestionsManagerService.this.getServiceForUserLocked(userId);
                    if (service != null) {
                        service.provideContextImageLocked(taskId, imageContextRequestExtras);
                    }
                }
                return;
            }
            throw new IllegalArgumentException("Expected non-null imageContextRequestExtras");
        }

        public void suggestContentSelections(int userId, SelectionsRequest selectionsRequest, ISelectionsCallback selectionsCallback) {
            ContentSuggestionsManagerService.this.enforceCaller(UserHandle.getCallingUserId(), "suggestContentSelections");
            synchronized (ContentSuggestionsManagerService.this.mLock) {
                ContentSuggestionsPerUserService service = (ContentSuggestionsPerUserService) ContentSuggestionsManagerService.this.getServiceForUserLocked(userId);
                if (service != null) {
                    service.suggestContentSelectionsLocked(selectionsRequest, selectionsCallback);
                }
            }
        }

        public void classifyContentSelections(int userId, ClassificationsRequest classificationsRequest, IClassificationsCallback callback) {
            ContentSuggestionsManagerService.this.enforceCaller(UserHandle.getCallingUserId(), "classifyContentSelections");
            synchronized (ContentSuggestionsManagerService.this.mLock) {
                ContentSuggestionsPerUserService service = (ContentSuggestionsPerUserService) ContentSuggestionsManagerService.this.getServiceForUserLocked(userId);
                if (service != null) {
                    service.classifyContentSelectionsLocked(classificationsRequest, callback);
                }
            }
        }

        public void notifyInteraction(int userId, String requestId, Bundle bundle) {
            ContentSuggestionsManagerService.this.enforceCaller(UserHandle.getCallingUserId(), "notifyInteraction");
            synchronized (ContentSuggestionsManagerService.this.mLock) {
                ContentSuggestionsPerUserService service = (ContentSuggestionsPerUserService) ContentSuggestionsManagerService.this.getServiceForUserLocked(userId);
                if (service != null) {
                    service.notifyInteractionLocked(requestId, bundle);
                }
            }
        }

        public void isEnabled(int userId, IResultReceiver receiver) throws RemoteException {
            boolean isDisabled;
            ContentSuggestionsManagerService.this.enforceCaller(UserHandle.getCallingUserId(), "isEnabled");
            synchronized (ContentSuggestionsManagerService.this.mLock) {
                isDisabled = ContentSuggestionsManagerService.this.isDisabledLocked(userId);
            }
            receiver.send(isDisabled ? 0 : 1, (Bundle) null);
        }

        /* JADX WARNING: type inference failed for: r4v0, types: [android.os.Binder] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onShellCommand(java.io.FileDescriptor r12, java.io.FileDescriptor r13, java.io.FileDescriptor r14, java.lang.String[] r15, android.os.ShellCallback r16, android.os.ResultReceiver r17) throws android.os.RemoteException {
            /*
                r11 = this;
                int r0 = android.os.Binder.getCallingUid()
                r1 = 2000(0x7d0, float:2.803E-42)
                if (r0 == r1) goto L_0x0014
                if (r0 == 0) goto L_0x0014
                java.lang.String r1 = com.android.server.contentsuggestions.ContentSuggestionsManagerService.TAG
                java.lang.String r2 = "Expected shell caller"
                android.util.Slog.e(r1, r2)
                return
            L_0x0014:
                com.android.server.contentsuggestions.ContentSuggestionsManagerServiceShellCommand r3 = new com.android.server.contentsuggestions.ContentSuggestionsManagerServiceShellCommand
                r1 = r11
                com.android.server.contentsuggestions.ContentSuggestionsManagerService r2 = com.android.server.contentsuggestions.ContentSuggestionsManagerService.this
                r3.<init>(r2)
                r4 = r11
                r5 = r12
                r6 = r13
                r7 = r14
                r8 = r15
                r9 = r16
                r10 = r17
                r3.exec(r4, r5, r6, r7, r8, r9, r10)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.contentsuggestions.ContentSuggestionsManagerService.ContentSuggestionsManagerStub.onShellCommand(java.io.FileDescriptor, java.io.FileDescriptor, java.io.FileDescriptor, java.lang.String[], android.os.ShellCallback, android.os.ResultReceiver):void");
        }
    }
}
