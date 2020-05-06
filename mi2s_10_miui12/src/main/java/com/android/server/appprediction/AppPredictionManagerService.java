package com.android.server.appprediction;

import android.app.prediction.AppPredictionContext;
import android.app.prediction.AppPredictionSessionId;
import android.app.prediction.AppTargetEvent;
import android.app.prediction.IPredictionCallback;
import android.app.prediction.IPredictionManager;
import android.content.Context;
import android.content.pm.ParceledListSlice;
import android.os.Binder;
import android.os.UserHandle;
import android.util.Slog;
import com.android.server.LocalServices;
import com.android.server.infra.AbstractMasterSystemService;
import com.android.server.infra.FrameworkResourcesServiceNameResolver;
import com.android.server.wm.ActivityTaskManagerInternal;
import java.util.function.Consumer;

public class AppPredictionManagerService extends AbstractMasterSystemService<AppPredictionManagerService, AppPredictionPerUserService> {
    private static final int MAX_TEMP_SERVICE_DURATION_MS = 120000;
    /* access modifiers changed from: private */
    public static final String TAG = AppPredictionManagerService.class.getSimpleName();
    /* access modifiers changed from: private */
    public ActivityTaskManagerInternal mActivityTaskManagerInternal = ((ActivityTaskManagerInternal) LocalServices.getService(ActivityTaskManagerInternal.class));

    public AppPredictionManagerService(Context context) {
        super(context, new FrameworkResourcesServiceNameResolver(context, 17039720), (String) null);
    }

    /* access modifiers changed from: protected */
    public AppPredictionPerUserService newServiceLocked(int resolvedUserId, boolean disabled) {
        return new AppPredictionPerUserService(this, this.mLock, resolvedUserId);
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [com.android.server.appprediction.AppPredictionManagerService$PredictionManagerServiceStub, android.os.IBinder] */
    public void onStart() {
        publishBinderService("app_prediction", new PredictionManagerServiceStub());
    }

    /* access modifiers changed from: protected */
    public void enforceCallingPermissionForManagement() {
        getContext().enforceCallingPermission("android.permission.MANAGE_APP_PREDICTIONS", TAG);
    }

    /* access modifiers changed from: protected */
    public int getMaximumTemporaryServiceDurationMs() {
        return MAX_TEMP_SERVICE_DURATION_MS;
    }

    private class PredictionManagerServiceStub extends IPredictionManager.Stub {
        private PredictionManagerServiceStub() {
        }

        public void createPredictionSession(AppPredictionContext context, AppPredictionSessionId sessionId) {
            runForUserLocked("createPredictionSession", new Consumer(context, sessionId) {
                private final /* synthetic */ AppPredictionContext f$0;
                private final /* synthetic */ AppPredictionSessionId f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void accept(Object obj) {
                    ((AppPredictionPerUserService) obj).onCreatePredictionSessionLocked(this.f$0, this.f$1);
                }
            });
        }

        public void notifyAppTargetEvent(AppPredictionSessionId sessionId, AppTargetEvent event) {
            runForUserLocked("notifyAppTargetEvent", new Consumer(sessionId, event) {
                private final /* synthetic */ AppPredictionSessionId f$0;
                private final /* synthetic */ AppTargetEvent f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void accept(Object obj) {
                    ((AppPredictionPerUserService) obj).notifyAppTargetEventLocked(this.f$0, this.f$1);
                }
            });
        }

        public void notifyLaunchLocationShown(AppPredictionSessionId sessionId, String launchLocation, ParceledListSlice targetIds) {
            runForUserLocked("notifyLaunchLocationShown", new Consumer(sessionId, launchLocation, targetIds) {
                private final /* synthetic */ AppPredictionSessionId f$0;
                private final /* synthetic */ String f$1;
                private final /* synthetic */ ParceledListSlice f$2;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void accept(Object obj) {
                    ((AppPredictionPerUserService) obj).notifyLaunchLocationShownLocked(this.f$0, this.f$1, this.f$2);
                }
            });
        }

        public void sortAppTargets(AppPredictionSessionId sessionId, ParceledListSlice targets, IPredictionCallback callback) {
            runForUserLocked("sortAppTargets", new Consumer(sessionId, targets, callback) {
                private final /* synthetic */ AppPredictionSessionId f$0;
                private final /* synthetic */ ParceledListSlice f$1;
                private final /* synthetic */ IPredictionCallback f$2;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void accept(Object obj) {
                    ((AppPredictionPerUserService) obj).sortAppTargetsLocked(this.f$0, this.f$1, this.f$2);
                }
            });
        }

        public void registerPredictionUpdates(AppPredictionSessionId sessionId, IPredictionCallback callback) {
            runForUserLocked("registerPredictionUpdates", new Consumer(sessionId, callback) {
                private final /* synthetic */ AppPredictionSessionId f$0;
                private final /* synthetic */ IPredictionCallback f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void accept(Object obj) {
                    ((AppPredictionPerUserService) obj).registerPredictionUpdatesLocked(this.f$0, this.f$1);
                }
            });
        }

        public void unregisterPredictionUpdates(AppPredictionSessionId sessionId, IPredictionCallback callback) {
            runForUserLocked("unregisterPredictionUpdates", new Consumer(sessionId, callback) {
                private final /* synthetic */ AppPredictionSessionId f$0;
                private final /* synthetic */ IPredictionCallback f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void accept(Object obj) {
                    ((AppPredictionPerUserService) obj).unregisterPredictionUpdatesLocked(this.f$0, this.f$1);
                }
            });
        }

        public void requestPredictionUpdate(AppPredictionSessionId sessionId) {
            runForUserLocked("requestPredictionUpdate", new Consumer(sessionId) {
                private final /* synthetic */ AppPredictionSessionId f$0;

                {
                    this.f$0 = r1;
                }

                public final void accept(Object obj) {
                    ((AppPredictionPerUserService) obj).requestPredictionUpdateLocked(this.f$0);
                }
            });
        }

        public void onDestroyPredictionSession(AppPredictionSessionId sessionId) {
            runForUserLocked("onDestroyPredictionSession", new Consumer(sessionId) {
                private final /* synthetic */ AppPredictionSessionId f$0;

                {
                    this.f$0 = r1;
                }

                public final void accept(Object obj) {
                    ((AppPredictionPerUserService) obj).onDestroyPredictionSessionLocked(this.f$0);
                }
            });
        }

        /* JADX WARNING: type inference failed for: r1v1, types: [android.os.Binder] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onShellCommand(java.io.FileDescriptor r9, java.io.FileDescriptor r10, java.io.FileDescriptor r11, java.lang.String[] r12, android.os.ShellCallback r13, android.os.ResultReceiver r14) {
            /*
                r8 = this;
                com.android.server.appprediction.AppPredictionManagerServiceShellCommand r0 = new com.android.server.appprediction.AppPredictionManagerServiceShellCommand
                com.android.server.appprediction.AppPredictionManagerService r1 = com.android.server.appprediction.AppPredictionManagerService.this
                r0.<init>(r1)
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.appprediction.AppPredictionManagerService.PredictionManagerServiceStub.onShellCommand(java.io.FileDescriptor, java.io.FileDescriptor, java.io.FileDescriptor, java.lang.String[], android.os.ShellCallback, android.os.ResultReceiver):void");
        }

        /* Debug info: failed to restart local var, previous not found, register: 6 */
        private void runForUserLocked(String func, Consumer<AppPredictionPerUserService> c) {
            int userId = UserHandle.getCallingUserId();
            if (AppPredictionManagerService.this.getContext().checkCallingPermission("android.permission.PACKAGE_USAGE_STATS") == 0 || AppPredictionManagerService.this.mServiceNameResolver.isTemporary(userId) || AppPredictionManagerService.this.mActivityTaskManagerInternal.isCallerRecents(Binder.getCallingUid())) {
                long origId = Binder.clearCallingIdentity();
                try {
                    synchronized (AppPredictionManagerService.this.mLock) {
                        c.accept((AppPredictionPerUserService) AppPredictionManagerService.this.getServiceForUserLocked(userId));
                    }
                    Binder.restoreCallingIdentity(origId);
                } catch (Throwable th) {
                    Binder.restoreCallingIdentity(origId);
                    throw th;
                }
            } else {
                String msg = "Permission Denial: " + func + " from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " expected caller to hold PACKAGE_USAGE_STATS permission";
                Slog.w(AppPredictionManagerService.TAG, msg);
                throw new SecurityException(msg);
            }
        }
    }
}
