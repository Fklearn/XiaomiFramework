package com.android.server.appprediction;

import android.app.AppGlobals;
import android.app.prediction.AppPredictionContext;
import android.app.prediction.AppPredictionSessionId;
import android.app.prediction.AppTargetEvent;
import android.app.prediction.IPredictionCallback;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ParceledListSlice;
import android.content.pm.ServiceInfo;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.ArrayMap;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.server.appprediction.AppPredictionPerUserService;
import com.android.server.appprediction.RemoteAppPredictionService;
import com.android.server.infra.AbstractPerUserSystemService;
import java.util.function.Consumer;

public class AppPredictionPerUserService extends AbstractPerUserSystemService<AppPredictionPerUserService, AppPredictionManagerService> implements RemoteAppPredictionService.RemoteAppPredictionServiceCallbacks {
    private static final String TAG = AppPredictionPerUserService.class.getSimpleName();
    @GuardedBy({"mLock"})
    private RemoteAppPredictionService mRemoteService;
    @GuardedBy({"mLock"})
    private final ArrayMap<AppPredictionSessionId, AppPredictionSessionInfo> mSessionInfos = new ArrayMap<>();
    @GuardedBy({"mLock"})
    private boolean mZombie;

    protected AppPredictionPerUserService(AppPredictionManagerService master, Object lock, int userId) {
        super(master, lock, userId);
    }

    /* access modifiers changed from: protected */
    public ServiceInfo newServiceInfoLocked(ComponentName serviceComponent) throws PackageManager.NameNotFoundException {
        try {
            return AppGlobals.getPackageManager().getServiceInfo(serviceComponent, 128, this.mUserId);
        } catch (RemoteException e) {
            throw new PackageManager.NameNotFoundException("Could not get service for " + serviceComponent);
        }
    }

    /* access modifiers changed from: protected */
    @GuardedBy({"mLock"})
    public boolean updateLocked(boolean disabled) {
        boolean enabledChanged = super.updateLocked(disabled);
        if (enabledChanged && !isEnabledLocked()) {
            this.mRemoteService = null;
        }
        return enabledChanged;
    }

    @GuardedBy({"mLock"})
    public void onCreatePredictionSessionLocked(AppPredictionContext context, AppPredictionSessionId sessionId) {
        RemoteAppPredictionService service = getRemoteServiceLocked();
        if (service != null) {
            service.onCreatePredictionSession(context, sessionId);
            if (!this.mSessionInfos.containsKey(sessionId)) {
                this.mSessionInfos.put(sessionId, new AppPredictionSessionInfo(sessionId, context, new Consumer() {
                    public final void accept(Object obj) {
                        AppPredictionPerUserService.this.removeAppPredictionSessionInfo((AppPredictionSessionId) obj);
                    }
                }));
            }
        }
    }

    @GuardedBy({"mLock"})
    public void notifyAppTargetEventLocked(AppPredictionSessionId sessionId, AppTargetEvent event) {
        RemoteAppPredictionService service = getRemoteServiceLocked();
        if (service != null) {
            service.notifyAppTargetEvent(sessionId, event);
        }
    }

    @GuardedBy({"mLock"})
    public void notifyLaunchLocationShownLocked(AppPredictionSessionId sessionId, String launchLocation, ParceledListSlice targetIds) {
        RemoteAppPredictionService service = getRemoteServiceLocked();
        if (service != null) {
            service.notifyLaunchLocationShown(sessionId, launchLocation, targetIds);
        }
    }

    @GuardedBy({"mLock"})
    public void sortAppTargetsLocked(AppPredictionSessionId sessionId, ParceledListSlice targets, IPredictionCallback callback) {
        RemoteAppPredictionService service = getRemoteServiceLocked();
        if (service != null) {
            service.sortAppTargets(sessionId, targets, callback);
        }
    }

    @GuardedBy({"mLock"})
    public void registerPredictionUpdatesLocked(AppPredictionSessionId sessionId, IPredictionCallback callback) {
        RemoteAppPredictionService service = getRemoteServiceLocked();
        if (service != null) {
            service.registerPredictionUpdates(sessionId, callback);
            AppPredictionSessionInfo sessionInfo = this.mSessionInfos.get(sessionId);
            if (sessionInfo != null) {
                sessionInfo.addCallbackLocked(callback);
            }
        }
    }

    @GuardedBy({"mLock"})
    public void unregisterPredictionUpdatesLocked(AppPredictionSessionId sessionId, IPredictionCallback callback) {
        RemoteAppPredictionService service = getRemoteServiceLocked();
        if (service != null) {
            service.unregisterPredictionUpdates(sessionId, callback);
            AppPredictionSessionInfo sessionInfo = this.mSessionInfos.get(sessionId);
            if (sessionInfo != null) {
                sessionInfo.removeCallbackLocked(callback);
            }
        }
    }

    @GuardedBy({"mLock"})
    public void requestPredictionUpdateLocked(AppPredictionSessionId sessionId) {
        RemoteAppPredictionService service = getRemoteServiceLocked();
        if (service != null) {
            service.requestPredictionUpdate(sessionId);
        }
    }

    @GuardedBy({"mLock"})
    public void onDestroyPredictionSessionLocked(AppPredictionSessionId sessionId) {
        RemoteAppPredictionService service = getRemoteServiceLocked();
        if (service != null) {
            service.onDestroyPredictionSession(sessionId);
            AppPredictionSessionInfo sessionInfo = this.mSessionInfos.get(sessionId);
            if (sessionInfo != null) {
                sessionInfo.destroy();
            }
        }
    }

    public void onFailureOrTimeout(boolean timedOut) {
        if (isDebug()) {
            String str = TAG;
            Slog.d(str, "onFailureOrTimeout(): timed out=" + timedOut);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:21:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onConnectedStateChanged(boolean r4) {
        /*
            r3 = this;
            boolean r0 = r3.isDebug()
            if (r0 == 0) goto L_0x001d
            java.lang.String r0 = TAG
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "onConnectedStateChanged(): connected="
            r1.append(r2)
            r1.append(r4)
            java.lang.String r1 = r1.toString()
            android.util.Slog.d(r0, r1)
        L_0x001d:
            if (r4 == 0) goto L_0x003e
            java.lang.Object r0 = r3.mLock
            monitor-enter(r0)
            boolean r1 = r3.mZombie     // Catch:{ all -> 0x003b }
            if (r1 == 0) goto L_0x0039
            com.android.server.appprediction.RemoteAppPredictionService r1 = r3.mRemoteService     // Catch:{ all -> 0x003b }
            if (r1 != 0) goto L_0x0033
            java.lang.String r1 = TAG     // Catch:{ all -> 0x003b }
            java.lang.String r2 = "Cannot resurrect sessions because remote service is null"
            android.util.Slog.w(r1, r2)     // Catch:{ all -> 0x003b }
            monitor-exit(r0)     // Catch:{ all -> 0x003b }
            return
        L_0x0033:
            r1 = 0
            r3.mZombie = r1     // Catch:{ all -> 0x003b }
            r3.resurrectSessionsLocked()     // Catch:{ all -> 0x003b }
        L_0x0039:
            monitor-exit(r0)     // Catch:{ all -> 0x003b }
            goto L_0x003e
        L_0x003b:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x003b }
            throw r1
        L_0x003e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.appprediction.AppPredictionPerUserService.onConnectedStateChanged(boolean):void");
    }

    public void onServiceDied(RemoteAppPredictionService service) {
        if (isDebug()) {
            String str = TAG;
            Slog.w(str, "onServiceDied(): service=" + service);
        }
        synchronized (this.mLock) {
            this.mZombie = true;
        }
    }

    private void resurrectSessionsLocked() {
        int numSessions = this.mSessionInfos.size();
        if (isDebug()) {
            String str = TAG;
            Slog.d(str, "Resurrecting remote service (" + this.mRemoteService + ") on " + numSessions + " sessions.");
        }
        for (AppPredictionSessionInfo sessionInfo : this.mSessionInfos.values()) {
            sessionInfo.resurrectSessionLocked(this);
        }
    }

    /* access modifiers changed from: private */
    public void removeAppPredictionSessionInfo(AppPredictionSessionId sessionId) {
        if (isDebug()) {
            String str = TAG;
            Slog.d(str, "removeAppPredictionSessionInfo(): sessionId=" + sessionId);
        }
        synchronized (this.mLock) {
            this.mSessionInfos.remove(sessionId);
        }
    }

    @GuardedBy({"mLock"})
    private RemoteAppPredictionService getRemoteServiceLocked() {
        if (this.mRemoteService == null) {
            String serviceName = getComponentNameLocked();
            if (serviceName != null) {
                this.mRemoteService = new RemoteAppPredictionService(getContext(), "android.service.appprediction.AppPredictionService", ComponentName.unflattenFromString(serviceName), this.mUserId, this, ((AppPredictionManagerService) this.mMaster).isBindInstantServiceAllowed(), ((AppPredictionManagerService) this.mMaster).verbose);
            } else if (!((AppPredictionManagerService) this.mMaster).verbose) {
                return null;
            } else {
                Slog.v(TAG, "getRemoteServiceLocked(): not set");
                return null;
            }
        }
        return this.mRemoteService;
    }

    private static final class AppPredictionSessionInfo {
        private static final boolean DEBUG = false;
        /* access modifiers changed from: private */
        public final RemoteCallbackList<IPredictionCallback> mCallbacks = new RemoteCallbackList<IPredictionCallback>() {
            public void onCallbackDied(IPredictionCallback callback) {
                if (AppPredictionSessionInfo.this.mCallbacks.getRegisteredCallbackCount() == 0) {
                    AppPredictionSessionInfo.this.destroy();
                }
            }
        };
        private final AppPredictionContext mPredictionContext;
        private final Consumer<AppPredictionSessionId> mRemoveSessionInfoAction;
        private final AppPredictionSessionId mSessionId;

        AppPredictionSessionInfo(AppPredictionSessionId id, AppPredictionContext predictionContext, Consumer<AppPredictionSessionId> removeSessionInfoAction) {
            this.mSessionId = id;
            this.mPredictionContext = predictionContext;
            this.mRemoveSessionInfoAction = removeSessionInfoAction;
        }

        /* access modifiers changed from: package-private */
        public void addCallbackLocked(IPredictionCallback callback) {
            this.mCallbacks.register(callback);
        }

        /* access modifiers changed from: package-private */
        public void removeCallbackLocked(IPredictionCallback callback) {
            this.mCallbacks.unregister(callback);
        }

        /* access modifiers changed from: package-private */
        public void destroy() {
            this.mCallbacks.kill();
            this.mRemoveSessionInfoAction.accept(this.mSessionId);
        }

        /* access modifiers changed from: package-private */
        public void resurrectSessionLocked(AppPredictionPerUserService service) {
            int registeredCallbackCount = this.mCallbacks.getRegisteredCallbackCount();
            service.onCreatePredictionSessionLocked(this.mPredictionContext, this.mSessionId);
            this.mCallbacks.broadcast(new Consumer(service) {
                private final /* synthetic */ AppPredictionPerUserService f$1;

                {
                    this.f$1 = r2;
                }

                public final void accept(Object obj) {
                    AppPredictionPerUserService.AppPredictionSessionInfo.this.lambda$resurrectSessionLocked$0$AppPredictionPerUserService$AppPredictionSessionInfo(this.f$1, (IPredictionCallback) obj);
                }
            });
        }

        public /* synthetic */ void lambda$resurrectSessionLocked$0$AppPredictionPerUserService$AppPredictionSessionInfo(AppPredictionPerUserService service, IPredictionCallback callback) {
            service.registerPredictionUpdatesLocked(this.mSessionId, callback);
        }
    }
}
