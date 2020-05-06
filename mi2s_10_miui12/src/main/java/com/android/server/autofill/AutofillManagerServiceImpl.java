package com.android.server.autofill;

import android.app.ActivityManagerInternal;
import android.app.ActivityTaskManager;
import android.app.IActivityTaskManager;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.graphics.Rect;
import android.metrics.LogMaker;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings;
import android.service.autofill.AutofillServiceInfo;
import android.service.autofill.FieldClassification;
import android.service.autofill.FillEventHistory;
import android.service.autofill.FillResponse;
import android.service.autofill.UserData;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.LocalLog;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.util.TimeUtils;
import android.view.autofill.AutofillId;
import android.view.autofill.AutofillValue;
import android.view.autofill.IAutoFillManagerClient;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.logging.MetricsLogger;
import com.android.server.LocalServices;
import com.android.server.autofill.AutofillManagerService;
import com.android.server.autofill.RemoteAugmentedAutofillService;
import com.android.server.autofill.ui.AutoFillUI;
import com.android.server.autofill.ui.PendingUi;
import com.android.server.infra.AbstractPerUserSystemService;
import com.android.server.job.controllers.JobStatus;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

final class AutofillManagerServiceImpl extends AbstractPerUserSystemService<AutofillManagerServiceImpl, AutofillManagerService> {
    private static final int MAX_ABANDONED_SESSION_MILLIS = 30000;
    private static final int MAX_SESSION_ID_CREATE_TRIES = 2048;
    private static final String TAG = "AutofillManagerServiceImpl";
    private static final Random sRandom = new Random();
    private final AutofillManagerService.AutofillCompatState mAutofillCompatState;
    @GuardedBy({"mLock"})
    private RemoteCallbackList<IAutoFillManagerClient> mClients;
    @GuardedBy({"mLock"})
    private ArrayMap<ComponentName, Long> mDisabledActivities;
    @GuardedBy({"mLock"})
    private ArrayMap<String, Long> mDisabledApps;
    @GuardedBy({"mLock"})
    private FillEventHistory mEventHistory;
    private final FieldClassificationStrategy mFieldClassificationStrategy;
    private final Handler mHandler = new Handler(Looper.getMainLooper(), (Handler.Callback) null, true);
    @GuardedBy({"mLock"})
    private AutofillServiceInfo mInfo;
    private long mLastPrune = 0;
    private final MetricsLogger mMetricsLogger = new MetricsLogger();
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public RemoteAugmentedAutofillService mRemoteAugmentedAutofillService;
    @GuardedBy({"mLock"})
    private ServiceInfo mRemoteAugmentedAutofillServiceInfo;
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public final SparseArray<Session> mSessions = new SparseArray<>();
    private final AutoFillUI mUi;
    private final LocalLog mUiLatencyHistory;
    @GuardedBy({"mLock"})
    private UserData mUserData;
    private final LocalLog mWtfHistory;

    AutofillManagerServiceImpl(AutofillManagerService master, Object lock, LocalLog uiLatencyHistory, LocalLog wtfHistory, int userId, AutoFillUI ui, AutofillManagerService.AutofillCompatState autofillCompatState, boolean disabled) {
        super(master, lock, userId);
        this.mUiLatencyHistory = uiLatencyHistory;
        this.mWtfHistory = wtfHistory;
        this.mUi = ui;
        this.mFieldClassificationStrategy = new FieldClassificationStrategy(getContext(), userId);
        this.mAutofillCompatState = autofillCompatState;
        updateLocked(disabled);
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public void onBackKeyPressed() {
        RemoteAugmentedAutofillService remoteService = getRemoteAugmentedAutofillServiceLocked();
        if (remoteService != null) {
            remoteService.onDestroyAutofillWindowsRequest();
        }
    }

    /* access modifiers changed from: protected */
    @GuardedBy({"mLock"})
    public boolean updateLocked(boolean disabled) {
        destroySessionsLocked();
        boolean enabledChanged = super.updateLocked(disabled);
        if (enabledChanged) {
            if (!isEnabledLocked()) {
                for (int i = this.mSessions.size() - 1; i >= 0; i--) {
                    this.mSessions.valueAt(i).removeSelfLocked();
                }
            }
            sendStateToClients(false);
        }
        updateRemoteAugmentedAutofillService();
        return enabledChanged;
    }

    /* access modifiers changed from: protected */
    public ServiceInfo newServiceInfoLocked(ComponentName serviceComponent) throws PackageManager.NameNotFoundException {
        this.mInfo = new AutofillServiceInfo(getContext(), serviceComponent, this.mUserId);
        return this.mInfo.getServiceInfo();
    }

    /* access modifiers changed from: package-private */
    public String[] getUrlBarResourceIdsForCompatMode(String packageName) {
        return this.mAutofillCompatState.getUrlBarResourceIds(packageName, this.mUserId);
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public int addClientLocked(IAutoFillManagerClient client, ComponentName componentName) {
        if (this.mClients == null) {
            this.mClients = new RemoteCallbackList<>();
        }
        this.mClients.register(client);
        if (isEnabledLocked()) {
            return 1;
        }
        if (!isAugmentedAutofillServiceAvailableLocked() || !isWhitelistedForAugmentedAutofillLocked(componentName)) {
            return 0;
        }
        return 8;
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public void removeClientLocked(IAutoFillManagerClient client) {
        RemoteCallbackList<IAutoFillManagerClient> remoteCallbackList = this.mClients;
        if (remoteCallbackList != null) {
            remoteCallbackList.unregister(client);
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public void setAuthenticationResultLocked(Bundle data, int sessionId, int authenticationId, int uid) {
        Session session;
        if (isEnabledLocked() && (session = this.mSessions.get(sessionId)) != null && uid == session.uid) {
            session.setAuthenticationResultLocked(data, authenticationId);
        }
    }

    /* access modifiers changed from: package-private */
    public void setHasCallback(int sessionId, int uid, boolean hasIt) {
        Session session;
        if (isEnabledLocked() && (session = this.mSessions.get(sessionId)) != null && uid == session.uid) {
            synchronized (this.mLock) {
                session.setHasCallbackLocked(hasIt);
            }
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public long startSessionLocked(IBinder activityToken, int taskId, int uid, IBinder appCallbackToken, AutofillId autofillId, Rect virtualBounds, AutofillValue value, boolean hasCallback, ComponentName componentName, boolean compatMode, boolean bindInstantServiceAllowed, int flags) {
        boolean forAugmentedAutofillOnly;
        ComponentName componentName2 = componentName;
        int i = flags;
        boolean forAugmentedAutofillOnly2 = (i & 8) != 0;
        if (!isEnabledLocked() && !forAugmentedAutofillOnly2) {
            return 0;
        }
        if (forAugmentedAutofillOnly2 || !isAutofillDisabledLocked(componentName2)) {
            forAugmentedAutofillOnly = forAugmentedAutofillOnly2;
        } else if (isWhitelistedForAugmentedAutofillLocked(componentName2)) {
            if (Helper.sDebug) {
                Slog.d(TAG, "startSession(" + componentName2 + "): disabled by service but whitelisted for augmented autofill");
            }
            forAugmentedAutofillOnly = true;
        } else {
            if (Helper.sDebug) {
                Slog.d(TAG, "startSession(" + componentName2 + "): ignored because disabled by service and not whitelisted for augmented autofill");
            }
            try {
                IAutoFillManagerClient.Stub.asInterface(appCallbackToken).setSessionFinished(4, (List) null);
            } catch (RemoteException e) {
                Slog.w(TAG, "Could not notify " + componentName2 + " that it's disabled: " + e);
            }
            return 2147483647L;
        }
        if (Helper.sVerbose) {
            Slog.v(TAG, "startSession(): token=" + activityToken + ", flags=" + i + ", forAugmentedAutofillOnly=" + forAugmentedAutofillOnly);
        } else {
            IBinder iBinder = activityToken;
        }
        pruneAbandonedSessionsLocked();
        boolean forAugmentedAutofillOnly3 = forAugmentedAutofillOnly;
        Session newSession = createSessionByTokenLocked(activityToken, taskId, uid, appCallbackToken, hasCallback, componentName, compatMode, bindInstantServiceAllowed, forAugmentedAutofillOnly, flags);
        if (newSession == null) {
            return 2147483647L;
        }
        AutofillServiceInfo autofillServiceInfo = this.mInfo;
        String historyItem = "id=" + newSession.id + " uid=" + uid + " a=" + componentName.toShortString() + " s=" + (autofillServiceInfo == null ? null : autofillServiceInfo.getServiceInfo().packageName) + " u=" + this.mUserId + " i=" + autofillId + " b=" + virtualBounds + " hc=" + hasCallback + " f=" + i + " aa=" + forAugmentedAutofillOnly3;
        ((AutofillManagerService) this.mMaster).logRequestLocked(historyItem);
        String str = historyItem;
        newSession.updateLocked(autofillId, virtualBounds, value, 1, flags);
        if (forAugmentedAutofillOnly3) {
            return 4294967296L | ((long) newSession.id);
        }
        return (long) newSession.id;
    }

    @GuardedBy({"mLock"})
    private void pruneAbandonedSessionsLocked() {
        long now = System.currentTimeMillis();
        if (this.mLastPrune < now - 30000) {
            this.mLastPrune = now;
            if (this.mSessions.size() > 0) {
                new PruneTask().execute(new Void[0]);
            }
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public void setAutofillFailureLocked(int sessionId, int uid, List<AutofillId> ids) {
        if (isEnabledLocked()) {
            Session session = this.mSessions.get(sessionId);
            if (session == null || uid != session.uid) {
                Slog.v(TAG, "setAutofillFailure(): no session for " + sessionId + "(" + uid + ")");
                return;
            }
            session.setAutofillFailureLocked(ids);
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public void finishSessionLocked(int sessionId, int uid) {
        if (isEnabledLocked()) {
            Session session = this.mSessions.get(sessionId);
            if (session != null && uid == session.uid) {
                session.logContextCommitted();
                boolean finished = session.showSaveLocked();
                if (Helper.sVerbose) {
                    Slog.v(TAG, "finishSessionLocked(): session finished on save? " + finished);
                }
                if (finished) {
                    session.removeSelfLocked();
                }
            } else if (Helper.sVerbose) {
                Slog.v(TAG, "finishSessionLocked(): no session for " + sessionId + "(" + uid + ")");
            }
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public void cancelSessionLocked(int sessionId, int uid) {
        if (isEnabledLocked()) {
            Session session = this.mSessions.get(sessionId);
            if (session == null || uid != session.uid) {
                Slog.w(TAG, "cancelSessionLocked(): no session for " + sessionId + "(" + uid + ")");
                return;
            }
            session.removeSelfLocked();
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public void disableOwnedAutofillServicesLocked(int uid) {
        Slog.i(TAG, "disableOwnedServices(" + uid + "): " + this.mInfo);
        AutofillServiceInfo autofillServiceInfo = this.mInfo;
        if (autofillServiceInfo != null) {
            ServiceInfo serviceInfo = autofillServiceInfo.getServiceInfo();
            if (serviceInfo.applicationInfo.uid != uid) {
                Slog.w(TAG, "disableOwnedServices(): ignored when called by UID " + uid + " instead of " + serviceInfo.applicationInfo.uid + " for service " + this.mInfo);
                return;
            }
            long identity = Binder.clearCallingIdentity();
            try {
                String autoFillService = getComponentNameLocked();
                ComponentName componentName = serviceInfo.getComponentName();
                if (componentName.equals(ComponentName.unflattenFromString(autoFillService))) {
                    this.mMetricsLogger.action(1135, componentName.getPackageName());
                    Settings.Secure.putStringForUser(getContext().getContentResolver(), "autofill_service", (String) null, this.mUserId);
                    destroySessionsLocked();
                } else {
                    Slog.w(TAG, "disableOwnedServices(): ignored because current service (" + serviceInfo + ") does not match Settings (" + autoFillService + ")");
                }
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }
    }

    @GuardedBy({"mLock"})
    private Session createSessionByTokenLocked(IBinder activityToken, int taskId, int uid, IBinder appCallbackToken, boolean hasCallback, ComponentName componentName, boolean compatMode, boolean bindInstantServiceAllowed, boolean forAugmentedAutofillOnly, int flags) {
        ComponentName serviceComponentName;
        AutofillManagerServiceImpl autofillManagerServiceImpl = this;
        int tries = 0;
        while (true) {
            int tries2 = tries + 1;
            if (tries2 > 2048) {
                Slog.w(TAG, "Cannot create session in 2048 tries");
                return null;
            }
            int sessionId = Math.abs(sRandom.nextInt());
            if (sessionId == 0 || sessionId == Integer.MAX_VALUE || autofillManagerServiceImpl.mSessions.indexOfKey(sessionId) >= 0) {
                autofillManagerServiceImpl = autofillManagerServiceImpl;
                tries = tries2;
            } else {
                autofillManagerServiceImpl.assertCallerLocked(componentName, compatMode);
                AutofillServiceInfo autofillServiceInfo = autofillManagerServiceImpl.mInfo;
                if (autofillServiceInfo == null) {
                    serviceComponentName = null;
                } else {
                    serviceComponentName = autofillServiceInfo.getServiceInfo().getComponentName();
                }
                int i = sessionId;
                int i2 = tries2;
                Session newSession = new Session(this, autofillManagerServiceImpl.mUi, getContext(), autofillManagerServiceImpl.mHandler, autofillManagerServiceImpl.mUserId, autofillManagerServiceImpl.mLock, sessionId, taskId, uid, activityToken, appCallbackToken, hasCallback, autofillManagerServiceImpl.mUiLatencyHistory, autofillManagerServiceImpl.mWtfHistory, serviceComponentName, componentName, compatMode, bindInstantServiceAllowed, forAugmentedAutofillOnly, flags);
                this.mSessions.put(newSession.id, newSession);
                return newSession;
            }
        }
    }

    private void assertCallerLocked(ComponentName componentName, boolean compatMode) {
        String callingPackage;
        String packageName = componentName.getPackageName();
        PackageManager pm = getContext().getPackageManager();
        int callingUid = Binder.getCallingUid();
        try {
            int packageUid = pm.getPackageUidAsUser(packageName, UserHandle.getCallingUserId());
            if (callingUid != packageUid && !((ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class)).hasRunningActivity(callingUid, packageName)) {
                String[] packages = pm.getPackagesForUid(callingUid);
                if (packages != null) {
                    callingPackage = packages[0];
                } else {
                    callingPackage = "uid-" + callingUid;
                }
                Slog.w(TAG, "App (package=" + callingPackage + ", UID=" + callingUid + ") passed component (" + componentName + ") owned by UID " + packageUid);
                LogMaker log = new LogMaker(948).setPackageName(callingPackage).addTaggedData(908, getServicePackageName()).addTaggedData(949, componentName.flattenToShortString());
                if (compatMode) {
                    log.addTaggedData(1414, 1);
                }
                this.mMetricsLogger.write(log);
                throw new SecurityException("Invalid component: " + componentName);
            }
        } catch (PackageManager.NameNotFoundException e) {
            throw new SecurityException("Could not verify UID for " + componentName);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean restoreSession(int sessionId, int uid, IBinder activityToken, IBinder appCallback) {
        Session session = this.mSessions.get(sessionId);
        if (session == null || uid != session.uid) {
            return false;
        }
        session.switchActivity(activityToken, appCallback);
        return true;
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public boolean updateSessionLocked(int sessionId, int uid, AutofillId autofillId, Rect virtualBounds, AutofillValue value, int action, int flags) {
        Session session = this.mSessions.get(sessionId);
        if (session != null && session.uid == uid) {
            session.updateLocked(autofillId, virtualBounds, value, action, flags);
            return false;
        } else if ((flags & 1) == 0) {
            if (Helper.sVerbose) {
                Slog.v(TAG, "updateSessionLocked(): session gone for " + sessionId + "(" + uid + ")");
            }
            return false;
        } else if (!Helper.sDebug) {
            return true;
        } else {
            Slog.d(TAG, "restarting session " + sessionId + " due to manual request on " + autofillId);
            return true;
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public void removeSessionLocked(int sessionId) {
        this.mSessions.remove(sessionId);
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public ArrayList<Session> getPreviousSessionsLocked(Session session) {
        int size = this.mSessions.size();
        ArrayList<Session> previousSessions = null;
        for (int i = 0; i < size; i++) {
            Session previousSession = this.mSessions.valueAt(i);
            if (!(previousSession.taskId != session.taskId || previousSession.id == session.id || (previousSession.getSaveInfoFlagsLocked() & 4) == 0)) {
                if (previousSessions == null) {
                    previousSessions = new ArrayList<>(size);
                }
                previousSessions.add(previousSession);
            }
        }
        return previousSessions;
    }

    /* access modifiers changed from: package-private */
    public void handleSessionSave(Session session) {
        synchronized (this.mLock) {
            if (this.mSessions.get(session.id) == null) {
                Slog.w(TAG, "handleSessionSave(): already gone: " + session.id);
                return;
            }
            session.callSaveLocked();
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0049, code lost:
        if (com.android.server.autofill.Helper.sDebug == false) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x004b, code lost:
        android.util.Slog.d(TAG, "No pending Save UI for token " + r7 + " and operation " + android.util.DebugUtils.flagsToString(android.view.autofill.AutofillManager.class, "PENDING_UI_OPERATION_", r6));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onPendingSaveUi(int r6, android.os.IBinder r7) {
        /*
            r5 = this;
            boolean r0 = com.android.server.autofill.Helper.sVerbose
            if (r0 == 0) goto L_0x0023
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "onPendingSaveUi("
            r0.append(r1)
            r0.append(r6)
            java.lang.String r1 = "): "
            r0.append(r1)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "AutofillManagerServiceImpl"
            android.util.Slog.v(r1, r0)
        L_0x0023:
            java.lang.Object r0 = r5.mLock
            monitor-enter(r0)
            android.util.SparseArray<com.android.server.autofill.Session> r1 = r5.mSessions     // Catch:{ all -> 0x0072 }
            int r1 = r1.size()     // Catch:{ all -> 0x0072 }
            int r2 = r1 + -1
        L_0x002e:
            if (r2 < 0) goto L_0x0046
            android.util.SparseArray<com.android.server.autofill.Session> r3 = r5.mSessions     // Catch:{ all -> 0x0072 }
            java.lang.Object r3 = r3.valueAt(r2)     // Catch:{ all -> 0x0072 }
            com.android.server.autofill.Session r3 = (com.android.server.autofill.Session) r3     // Catch:{ all -> 0x0072 }
            boolean r4 = r3.isSaveUiPendingForTokenLocked(r7)     // Catch:{ all -> 0x0072 }
            if (r4 == 0) goto L_0x0043
            r3.onPendingSaveUi(r6, r7)     // Catch:{ all -> 0x0072 }
            monitor-exit(r0)     // Catch:{ all -> 0x0072 }
            return
        L_0x0043:
            int r2 = r2 + -1
            goto L_0x002e
        L_0x0046:
            monitor-exit(r0)     // Catch:{ all -> 0x0072 }
            boolean r0 = com.android.server.autofill.Helper.sDebug
            if (r0 == 0) goto L_0x0071
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "No pending Save UI for token "
            r0.append(r1)
            r0.append(r7)
            java.lang.String r1 = " and operation "
            r0.append(r1)
            java.lang.Class<android.view.autofill.AutofillManager> r1 = android.view.autofill.AutofillManager.class
            java.lang.String r2 = "PENDING_UI_OPERATION_"
            java.lang.String r1 = android.util.DebugUtils.flagsToString(r1, r2, r6)
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "AutofillManagerServiceImpl"
            android.util.Slog.d(r1, r0)
        L_0x0071:
            return
        L_0x0072:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0072 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.autofill.AutofillManagerServiceImpl.onPendingSaveUi(int, android.os.IBinder):void");
    }

    /* access modifiers changed from: protected */
    @GuardedBy({"mLock"})
    public void handlePackageUpdateLocked(String packageName) {
        ServiceInfo serviceInfo = this.mFieldClassificationStrategy.getServiceInfo();
        if (serviceInfo != null && serviceInfo.packageName.equals(packageName)) {
            resetExtServiceLocked();
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public void resetExtServiceLocked() {
        if (Helper.sVerbose) {
            Slog.v(TAG, "reset autofill service.");
        }
        this.mFieldClassificationStrategy.reset();
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public void destroyLocked() {
        if (Helper.sVerbose) {
            Slog.v(TAG, "destroyLocked()");
        }
        resetExtServiceLocked();
        int numSessions = this.mSessions.size();
        ArraySet<RemoteFillService> remoteFillServices = new ArraySet<>(numSessions);
        for (int i = 0; i < numSessions; i++) {
            RemoteFillService remoteFillService = this.mSessions.valueAt(i).destroyLocked();
            if (remoteFillService != null) {
                remoteFillServices.add(remoteFillService);
            }
        }
        this.mSessions.clear();
        for (int i2 = 0; i2 < remoteFillServices.size(); i2++) {
            remoteFillServices.valueAt(i2).destroy();
        }
        sendStateToClients(true);
        RemoteCallbackList<IAutoFillManagerClient> remoteCallbackList = this.mClients;
        if (remoteCallbackList != null) {
            remoteCallbackList.kill();
            this.mClients = null;
        }
    }

    /* access modifiers changed from: package-private */
    public void setLastResponse(int sessionId, FillResponse response) {
        synchronized (this.mLock) {
            this.mEventHistory = new FillEventHistory(sessionId, response.getClientState());
        }
    }

    /* access modifiers changed from: package-private */
    public void resetLastResponse() {
        synchronized (this.mLock) {
            this.mEventHistory = null;
        }
    }

    @GuardedBy({"mLock"})
    private boolean isValidEventLocked(String method, int sessionId) {
        FillEventHistory fillEventHistory = this.mEventHistory;
        if (fillEventHistory == null) {
            Slog.w(TAG, method + ": not logging event because history is null");
            return false;
        } else if (sessionId == fillEventHistory.getSessionId()) {
            return true;
        } else {
            if (Helper.sDebug) {
                Slog.d(TAG, method + ": not logging event for session " + sessionId + " because tracked session is " + this.mEventHistory.getSessionId());
            }
            return false;
        }
    }

    /* access modifiers changed from: package-private */
    public void setAuthenticationSelected(int sessionId, Bundle clientState) {
        synchronized (this.mLock) {
            try {
                if (isValidEventLocked("setAuthenticationSelected()", sessionId)) {
                    FillEventHistory fillEventHistory = this.mEventHistory;
                    FillEventHistory.Event event = r4;
                    FillEventHistory.Event event2 = new FillEventHistory.Event(2, (String) null, clientState, (List) null, (ArraySet) null, (ArrayList) null, (ArrayList) null, (ArrayList) null, (ArrayList) null, (AutofillId[]) null, (FieldClassification[]) null);
                    fillEventHistory.addEvent(event);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void logDatasetAuthenticationSelected(String selectedDataset, int sessionId, Bundle clientState) {
        synchronized (this.mLock) {
            try {
                if (isValidEventLocked("logDatasetAuthenticationSelected()", sessionId)) {
                    FillEventHistory fillEventHistory = this.mEventHistory;
                    FillEventHistory.Event event = r4;
                    FillEventHistory.Event event2 = new FillEventHistory.Event(1, selectedDataset, clientState, (List) null, (ArraySet) null, (ArrayList) null, (ArrayList) null, (ArrayList) null, (ArrayList) null, (AutofillId[]) null, (FieldClassification[]) null);
                    fillEventHistory.addEvent(event);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void logSaveShown(int sessionId, Bundle clientState) {
        synchronized (this.mLock) {
            try {
                if (isValidEventLocked("logSaveShown()", sessionId)) {
                    FillEventHistory fillEventHistory = this.mEventHistory;
                    FillEventHistory.Event event = r4;
                    FillEventHistory.Event event2 = new FillEventHistory.Event(3, (String) null, clientState, (List) null, (ArraySet) null, (ArrayList) null, (ArrayList) null, (ArrayList) null, (ArrayList) null, (AutofillId[]) null, (FieldClassification[]) null);
                    fillEventHistory.addEvent(event);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void logDatasetSelected(String selectedDataset, int sessionId, Bundle clientState) {
        synchronized (this.mLock) {
            try {
                if (isValidEventLocked("logDatasetSelected()", sessionId)) {
                    FillEventHistory fillEventHistory = this.mEventHistory;
                    FillEventHistory.Event event = r4;
                    FillEventHistory.Event event2 = new FillEventHistory.Event(0, selectedDataset, clientState, (List) null, (ArraySet) null, (ArrayList) null, (ArrayList) null, (ArrayList) null, (ArrayList) null, (AutofillId[]) null, (FieldClassification[]) null);
                    fillEventHistory.addEvent(event);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public void logContextCommittedLocked(int sessionId, Bundle clientState, ArrayList<String> selectedDatasets, ArraySet<String> ignoredDatasets, ArrayList<AutofillId> changedFieldIds, ArrayList<String> changedDatasetIds, ArrayList<AutofillId> manuallyFilledFieldIds, ArrayList<ArrayList<String>> manuallyFilledDatasetIds, ComponentName appComponentName, boolean compatMode) {
        logContextCommittedLocked(sessionId, clientState, selectedDatasets, ignoredDatasets, changedFieldIds, changedDatasetIds, manuallyFilledFieldIds, manuallyFilledDatasetIds, (ArrayList<AutofillId>) null, (ArrayList<FieldClassification>) null, appComponentName, compatMode);
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public void logContextCommittedLocked(int sessionId, Bundle clientState, ArrayList<String> selectedDatasets, ArraySet<String> ignoredDatasets, ArrayList<AutofillId> changedFieldIds, ArrayList<String> changedDatasetIds, ArrayList<AutofillId> manuallyFilledFieldIds, ArrayList<ArrayList<String>> manuallyFilledDatasetIds, ArrayList<AutofillId> detectedFieldIdsList, ArrayList<FieldClassification> detectedFieldClassificationsList, ComponentName appComponentName, boolean compatMode) {
        FieldClassification[] detectedFieldClassifications;
        int i = sessionId;
        ArrayList<AutofillId> arrayList = detectedFieldIdsList;
        ArrayList<FieldClassification> arrayList2 = detectedFieldClassificationsList;
        boolean z = compatMode;
        if (isValidEventLocked("logDatasetNotSelected()", i)) {
            if (Helper.sVerbose) {
                Slog.v(TAG, "logContextCommitted() with FieldClassification: id=" + i + ", selectedDatasets=" + selectedDatasets + ", ignoredDatasetIds=" + ignoredDatasets + ", changedAutofillIds=" + changedFieldIds + ", changedDatasetIds=" + changedDatasetIds + ", manuallyFilledFieldIds=" + manuallyFilledFieldIds + ", detectedFieldIds=" + arrayList + ", detectedFieldClassifications=" + arrayList2 + ", appComponentName=" + appComponentName.toShortString() + ", compatMode=" + z);
            } else {
                ArrayList<String> arrayList3 = selectedDatasets;
                ArraySet<String> arraySet = ignoredDatasets;
                ArrayList<AutofillId> arrayList4 = changedFieldIds;
                ArrayList<String> arrayList5 = changedDatasetIds;
                ArrayList<AutofillId> arrayList6 = manuallyFilledFieldIds;
            }
            AutofillId[] detectedFieldsIds = null;
            if (arrayList != null) {
                AutofillId[] detectedFieldsIds2 = new AutofillId[detectedFieldIdsList.size()];
                arrayList.toArray(detectedFieldsIds2);
                FieldClassification[] detectedFieldClassifications2 = new FieldClassification[detectedFieldClassificationsList.size()];
                arrayList2.toArray(detectedFieldClassifications2);
                int numberFields = detectedFieldsIds2.length;
                int totalSize = 0;
                float totalScore = 0.0f;
                int i2 = 0;
                while (i2 < numberFields) {
                    List<FieldClassification.Match> matches = detectedFieldClassifications2[i2].getMatches();
                    int size = matches.size();
                    totalSize += size;
                    float totalScore2 = totalScore;
                    for (int j = 0; j < size; j++) {
                        totalScore2 += matches.get(j).getScore();
                    }
                    i2++;
                    ArrayList<AutofillId> arrayList7 = detectedFieldIdsList;
                    ArrayList<FieldClassification> arrayList8 = detectedFieldClassificationsList;
                    totalScore = totalScore2;
                }
                int i3 = totalSize;
                this.mMetricsLogger.write(Helper.newLogMaker(1273, appComponentName, getServicePackageName(), i, z).setCounterValue(numberFields).addTaggedData(1274, Integer.valueOf((int) ((100.0f * totalScore) / ((float) totalSize)))));
                detectedFieldClassifications = detectedFieldClassifications2;
                detectedFieldsIds = detectedFieldsIds2;
            } else {
                ComponentName componentName = appComponentName;
                detectedFieldClassifications = null;
            }
            this.mEventHistory.addEvent(new FillEventHistory.Event(4, (String) null, clientState, selectedDatasets, ignoredDatasets, changedFieldIds, changedDatasetIds, manuallyFilledFieldIds, manuallyFilledDatasetIds, detectedFieldsIds, detectedFieldClassifications));
            return;
        }
        ArrayList<String> arrayList9 = selectedDatasets;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0014, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.service.autofill.FillEventHistory getFillEventHistory(int r3) {
        /*
            r2 = this;
            java.lang.Object r0 = r2.mLock
            monitor-enter(r0)
            android.service.autofill.FillEventHistory r1 = r2.mEventHistory     // Catch:{ all -> 0x0016 }
            if (r1 == 0) goto L_0x0013
            java.lang.String r1 = "getFillEventHistory"
            boolean r1 = r2.isCalledByServiceLocked(r1, r3)     // Catch:{ all -> 0x0016 }
            if (r1 == 0) goto L_0x0013
            android.service.autofill.FillEventHistory r1 = r2.mEventHistory     // Catch:{ all -> 0x0016 }
            monitor-exit(r0)     // Catch:{ all -> 0x0016 }
            return r1
        L_0x0013:
            monitor-exit(r0)     // Catch:{ all -> 0x0016 }
            r0 = 0
            return r0
        L_0x0016:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0016 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.autofill.AutofillManagerServiceImpl.getFillEventHistory(int):android.service.autofill.FillEventHistory");
    }

    /* access modifiers changed from: package-private */
    public UserData getUserData() {
        UserData userData;
        synchronized (this.mLock) {
            userData = this.mUserData;
        }
        return userData;
    }

    /* access modifiers changed from: package-private */
    public UserData getUserData(int callingUid) {
        synchronized (this.mLock) {
            if (!isCalledByServiceLocked("getUserData", callingUid)) {
                return null;
            }
            UserData userData = this.mUserData;
            return userData;
        }
    }

    /* access modifiers changed from: package-private */
    public void setUserData(int callingUid, UserData userData) {
        synchronized (this.mLock) {
            if (isCalledByServiceLocked("setUserData", callingUid)) {
                this.mUserData = userData;
                this.mMetricsLogger.write(new LogMaker(1272).setPackageName(getServicePackageName()).addTaggedData(914, Integer.valueOf(this.mUserData == null ? 0 : this.mUserData.getCategoryIds().length)));
            }
        }
    }

    @GuardedBy({"mLock"})
    private boolean isCalledByServiceLocked(String methodName, int callingUid) {
        int serviceUid = getServiceUidLocked();
        if (serviceUid == callingUid) {
            return true;
        }
        Slog.w(TAG, methodName + "() called by UID " + callingUid + ", but service UID is " + serviceUid);
        return false;
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public int getSupportedSmartSuggestionModesLocked() {
        return ((AutofillManagerService) this.mMaster).getSupportedSmartSuggestionModesLocked();
    }

    /* access modifiers changed from: protected */
    @GuardedBy({"mLock"})
    public void dumpLocked(String prefix, PrintWriter pw) {
        String str = prefix;
        PrintWriter printWriter = pw;
        super.dumpLocked(prefix, pw);
        String prefix2 = str + "  ";
        printWriter.print(str);
        printWriter.print("UID: ");
        printWriter.println(getServiceUidLocked());
        printWriter.print(str);
        printWriter.print("Autofill Service Info: ");
        if (this.mInfo == null) {
            printWriter.println("N/A");
        } else {
            pw.println();
            this.mInfo.dump(prefix2, printWriter);
        }
        printWriter.print(str);
        printWriter.print("Default component: ");
        printWriter.println(getContext().getString(17039724));
        printWriter.print(str);
        printWriter.println("mAugmentedAutofillNamer: ");
        printWriter.print(prefix2);
        ((AutofillManagerService) this.mMaster).mAugmentedAutofillResolver.dumpShort(printWriter, this.mUserId);
        pw.println();
        if (this.mRemoteAugmentedAutofillService != null) {
            printWriter.print(str);
            printWriter.println("RemoteAugmentedAutofillService: ");
            this.mRemoteAugmentedAutofillService.dump(prefix2, printWriter);
        }
        if (this.mRemoteAugmentedAutofillServiceInfo != null) {
            printWriter.print(str);
            printWriter.print("RemoteAugmentedAutofillServiceInfo: ");
            printWriter.println(this.mRemoteAugmentedAutofillServiceInfo);
        }
        printWriter.print(str);
        printWriter.print("Field classification enabled: ");
        printWriter.println(isFieldClassificationEnabledLocked());
        printWriter.print(str);
        printWriter.print("Compat pkgs: ");
        ArrayMap<String, Long> compatPkgs = getCompatibilityPackagesLocked();
        if (compatPkgs == null) {
            printWriter.println("N/A");
        } else {
            printWriter.println(compatPkgs);
        }
        printWriter.print(str);
        printWriter.print("Last prune: ");
        printWriter.println(this.mLastPrune);
        printWriter.print(str);
        printWriter.print("Disabled apps: ");
        ArrayMap<String, Long> arrayMap = this.mDisabledApps;
        String str2 = ": ";
        String str3 = ". ";
        if (arrayMap == null) {
            printWriter.println("N/A");
        } else {
            int size = arrayMap.size();
            printWriter.println(size);
            StringBuilder builder = new StringBuilder();
            long now = SystemClock.elapsedRealtime();
            int i = 0;
            while (i < size) {
                long expiration = this.mDisabledApps.valueAt(i).longValue();
                builder.append(str);
                builder.append(str);
                builder.append(i);
                builder.append(str3);
                builder.append(this.mDisabledApps.keyAt(i));
                builder.append(str2);
                TimeUtils.formatDuration(expiration - now, builder);
                builder.append(10);
                i++;
                size = size;
            }
            printWriter.println(builder);
        }
        printWriter.print(str);
        printWriter.print("Disabled activities: ");
        ArrayMap<ComponentName, Long> arrayMap2 = this.mDisabledActivities;
        if (arrayMap2 == null) {
            printWriter.println("N/A");
        } else {
            int size2 = arrayMap2.size();
            printWriter.println(size2);
            StringBuilder builder2 = new StringBuilder();
            long now2 = SystemClock.elapsedRealtime();
            int i2 = 0;
            while (i2 < size2) {
                long expiration2 = this.mDisabledActivities.valueAt(i2).longValue();
                builder2.append(str);
                builder2.append(str);
                builder2.append(i2);
                builder2.append(str3);
                builder2.append(this.mDisabledActivities.keyAt(i2));
                builder2.append(str2);
                TimeUtils.formatDuration(expiration2 - now2, builder2);
                builder2.append(10);
                i2++;
                str2 = str2;
                str3 = str3;
            }
            printWriter.println(builder2);
        }
        int size3 = this.mSessions.size();
        if (size3 == 0) {
            printWriter.print(str);
            printWriter.println("No sessions");
        } else {
            printWriter.print(str);
            printWriter.print(size3);
            printWriter.println(" sessions:");
            for (int i3 = 0; i3 < size3; i3++) {
                printWriter.print(str);
                printWriter.print("#");
                printWriter.println(i3 + 1);
                this.mSessions.valueAt(i3).dumpLocked(prefix2, printWriter);
            }
        }
        printWriter.print(str);
        printWriter.print("Clients: ");
        if (this.mClients == null) {
            printWriter.println("N/A");
        } else {
            pw.println();
            this.mClients.dump(printWriter, prefix2);
        }
        FillEventHistory fillEventHistory = this.mEventHistory;
        if (fillEventHistory == null || fillEventHistory.getEvents() == null || this.mEventHistory.getEvents().size() == 0) {
            printWriter.print(str);
            printWriter.println("No event on last fill response");
        } else {
            printWriter.print(str);
            printWriter.println("Events of last fill response:");
            printWriter.print(str);
            int numEvents = this.mEventHistory.getEvents().size();
            for (int i4 = 0; i4 < numEvents; i4++) {
                FillEventHistory.Event event = this.mEventHistory.getEvents().get(i4);
                printWriter.println("  " + i4 + ": eventType=" + event.getType() + " datasetId=" + event.getDatasetId());
            }
        }
        printWriter.print(str);
        printWriter.print("User data: ");
        if (this.mUserData == null) {
            printWriter.println("N/A");
        } else {
            pw.println();
            this.mUserData.dump(prefix2, printWriter);
        }
        printWriter.print(str);
        printWriter.println("Field Classification strategy: ");
        this.mFieldClassificationStrategy.dump(prefix2, printWriter);
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public void destroySessionsLocked() {
        if (this.mSessions.size() == 0) {
            this.mUi.destroyAll((PendingUi) null, (AutoFillUI.AutoFillUiCallback) null, false);
            return;
        }
        while (this.mSessions.size() > 0) {
            this.mSessions.valueAt(0).forceRemoveSelfLocked();
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public void destroySessionsForAugmentedAutofillOnlyLocked() {
        for (int i = this.mSessions.size() - 1; i >= 0; i--) {
            this.mSessions.valueAt(i).forceRemoveSelfIfForAugmentedAutofillOnlyLocked();
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public void destroyFinishedSessionsLocked() {
        for (int i = this.mSessions.size() - 1; i >= 0; i--) {
            Session session = this.mSessions.valueAt(i);
            if (session.isSavingLocked()) {
                if (Helper.sDebug) {
                    Slog.d(TAG, "destroyFinishedSessionsLocked(): " + session.id);
                }
                session.forceRemoveSelfLocked();
            } else {
                session.destroyAugmentedAutofillWindowsLocked();
            }
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public void listSessionsLocked(ArrayList<String> output) {
        String service;
        String augmentedService;
        int numSessions = this.mSessions.size();
        if (numSessions > 0) {
            for (int i = 0; i < numSessions; i++) {
                int id = this.mSessions.keyAt(i);
                AutofillServiceInfo autofillServiceInfo = this.mInfo;
                if (autofillServiceInfo == null) {
                    service = "no_svc";
                } else {
                    service = autofillServiceInfo.getServiceInfo().getComponentName().flattenToShortString();
                }
                ServiceInfo serviceInfo = this.mRemoteAugmentedAutofillServiceInfo;
                if (serviceInfo == null) {
                    augmentedService = "no_aug";
                } else {
                    augmentedService = serviceInfo.getComponentName().flattenToShortString();
                }
                output.add(String.format("%d:%s:%s", new Object[]{Integer.valueOf(id), service, augmentedService}));
            }
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public ArrayMap<String, Long> getCompatibilityPackagesLocked() {
        AutofillServiceInfo autofillServiceInfo = this.mInfo;
        if (autofillServiceInfo != null) {
            return autofillServiceInfo.getCompatibilityPackages();
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public RemoteAugmentedAutofillService getRemoteAugmentedAutofillServiceLocked() {
        if (this.mRemoteAugmentedAutofillService == null) {
            String serviceName = ((AutofillManagerService) this.mMaster).mAugmentedAutofillResolver.getServiceName(this.mUserId);
            if (serviceName == null) {
                if (((AutofillManagerService) this.mMaster).verbose) {
                    Slog.v(TAG, "getRemoteAugmentedAutofillServiceLocked(): not set");
                }
                return null;
            }
            Pair<ServiceInfo, ComponentName> pair = RemoteAugmentedAutofillService.getComponentName(serviceName, this.mUserId, ((AutofillManagerService) this.mMaster).mAugmentedAutofillResolver.isTemporary(this.mUserId));
            if (pair == null) {
                return null;
            }
            this.mRemoteAugmentedAutofillServiceInfo = (ServiceInfo) pair.first;
            ComponentName componentName = (ComponentName) pair.second;
            if (Helper.sVerbose) {
                Slog.v(TAG, "getRemoteAugmentedAutofillServiceLocked(): " + componentName);
            }
            this.mRemoteAugmentedAutofillService = new RemoteAugmentedAutofillService(getContext(), componentName, this.mUserId, new RemoteAugmentedAutofillService.RemoteAugmentedAutofillServiceCallbacks() {
                public void onServiceDied(RemoteAugmentedAutofillService service) {
                    Slog.w(AutofillManagerServiceImpl.TAG, "remote augmented autofill service died");
                    RemoteAugmentedAutofillService remoteService = AutofillManagerServiceImpl.this.mRemoteAugmentedAutofillService;
                    if (remoteService != null) {
                        remoteService.destroy();
                    }
                    RemoteAugmentedAutofillService unused = AutofillManagerServiceImpl.this.mRemoteAugmentedAutofillService = null;
                }
            }, ((AutofillManagerService) this.mMaster).isInstantServiceAllowed(), ((AutofillManagerService) this.mMaster).verbose, ((AutofillManagerService) this.mMaster).mAugmentedServiceIdleUnbindTimeoutMs, ((AutofillManagerService) this.mMaster).mAugmentedServiceRequestTimeoutMs);
        }
        return this.mRemoteAugmentedAutofillService;
    }

    /* access modifiers changed from: package-private */
    public void updateRemoteAugmentedAutofillService() {
        synchronized (this.mLock) {
            if (this.mRemoteAugmentedAutofillService != null) {
                if (Helper.sVerbose) {
                    Slog.v(TAG, "updateRemoteAugmentedAutofillService(): destroying old remote service");
                }
                destroySessionsForAugmentedAutofillOnlyLocked();
                this.mRemoteAugmentedAutofillService.destroy();
                this.mRemoteAugmentedAutofillService = null;
                this.mRemoteAugmentedAutofillServiceInfo = null;
                resetAugmentedAutofillWhitelistLocked();
            }
            boolean available = isAugmentedAutofillServiceAvailableLocked();
            if (Helper.sVerbose) {
                Slog.v(TAG, "updateRemoteAugmentedAutofillService(): " + available);
            }
            if (available) {
                this.mRemoteAugmentedAutofillService = getRemoteAugmentedAutofillServiceLocked();
            }
        }
    }

    private boolean isAugmentedAutofillServiceAvailableLocked() {
        if (((AutofillManagerService) this.mMaster).verbose) {
            Slog.v(TAG, "isAugmentedAutofillService(): setupCompleted=" + isSetupCompletedLocked() + ", disabled=" + isDisabledByUserRestrictionsLocked() + ", augmentedService=" + ((AutofillManagerService) this.mMaster).mAugmentedAutofillResolver.getServiceName(this.mUserId));
        }
        if (!isSetupCompletedLocked() || isDisabledByUserRestrictionsLocked() || ((AutofillManagerService) this.mMaster).mAugmentedAutofillResolver.getServiceName(this.mUserId) == null) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean isAugmentedAutofillServiceForUserLocked(int callingUid) {
        ServiceInfo serviceInfo = this.mRemoteAugmentedAutofillServiceInfo;
        return serviceInfo != null && serviceInfo.applicationInfo.uid == callingUid;
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public boolean setAugmentedAutofillWhitelistLocked(List<String> packages, List<ComponentName> activities, int callingUid) {
        String serviceName;
        if (!isCalledByAugmentedAutofillServiceLocked("setAugmentedAutofillWhitelistLocked", callingUid)) {
            return false;
        }
        if (((AutofillManagerService) this.mMaster).verbose) {
            Slog.v(TAG, "setAugmentedAutofillWhitelistLocked(packages=" + packages + ", activities=" + activities + ")");
        }
        whitelistForAugmentedAutofillPackages(packages, activities);
        ServiceInfo serviceInfo = this.mRemoteAugmentedAutofillServiceInfo;
        if (serviceInfo != null) {
            serviceName = serviceInfo.getComponentName().flattenToShortString();
        } else {
            Slog.e(TAG, "setAugmentedAutofillWhitelistLocked(): no service");
            serviceName = "N/A";
        }
        LogMaker log = new LogMaker(1721).addTaggedData(908, serviceName);
        if (packages != null) {
            log.addTaggedData(1722, Integer.valueOf(packages.size()));
        }
        if (activities != null) {
            log.addTaggedData(1723, Integer.valueOf(activities.size()));
        }
        this.mMetricsLogger.write(log);
        return true;
    }

    @GuardedBy({"mLock"})
    private boolean isCalledByAugmentedAutofillServiceLocked(String methodName, int callingUid) {
        if (getRemoteAugmentedAutofillServiceLocked() == null) {
            Slog.w(TAG, methodName + "() called by UID " + callingUid + ", but there is no augmented autofill service defined for user " + getUserId());
            return false;
        } else if (getAugmentedAutofillServiceUidLocked() == callingUid) {
            return true;
        } else {
            Slog.w(TAG, methodName + "() called by UID " + callingUid + ", but service UID is " + getAugmentedAutofillServiceUidLocked() + " for user " + getUserId());
            return false;
        }
    }

    @GuardedBy({"mLock"})
    private int getAugmentedAutofillServiceUidLocked() {
        ServiceInfo serviceInfo = this.mRemoteAugmentedAutofillServiceInfo;
        if (serviceInfo != null) {
            return serviceInfo.applicationInfo.uid;
        }
        if (!((AutofillManagerService) this.mMaster).verbose) {
            return -1;
        }
        Slog.v(TAG, "getAugmentedAutofillServiceUid(): no mRemoteAugmentedAutofillServiceInfo");
        return -1;
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public boolean isWhitelistedForAugmentedAutofillLocked(ComponentName componentName) {
        return ((AutofillManagerService) this.mMaster).mAugmentedAutofillState.isWhitelisted(this.mUserId, componentName);
    }

    private void whitelistForAugmentedAutofillPackages(List<String> packages, List<ComponentName> components) {
        synchronized (this.mLock) {
            if (((AutofillManagerService) this.mMaster).verbose) {
                Slog.v(TAG, "whitelisting packages: " + packages + "and activities: " + components);
            }
            ((AutofillManagerService) this.mMaster).mAugmentedAutofillState.setWhitelist(this.mUserId, packages, components);
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public void resetAugmentedAutofillWhitelistLocked() {
        if (((AutofillManagerService) this.mMaster).verbose) {
            Slog.v(TAG, "resetting augmented autofill whitelist");
        }
        ((AutofillManagerService) this.mMaster).mAugmentedAutofillState.resetWhitelist(this.mUserId);
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0011, code lost:
        if (r0 >= r2) goto L_0x0057;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:?, code lost:
        r3 = r1.getBroadcastItem(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:?, code lost:
        r4 = r8.mLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x001b, code lost:
        monitor-enter(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x001c, code lost:
        if (r9 != false) goto L_0x0029;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0022, code lost:
        if (isClientSessionDestroyedLocked(r3) == false) goto L_0x0025;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0025, code lost:
        r5 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0027, code lost:
        r5 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0029, code lost:
        r5 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x002a, code lost:
        r6 = isEnabledLocked();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x002e, code lost:
        monitor-exit(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x002f, code lost:
        r4 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0030, code lost:
        if (r6 == false) goto L_0x0034;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0032, code lost:
        r4 = 0 | 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0034, code lost:
        if (r5 == false) goto L_0x0038;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0036, code lost:
        r4 = r4 | 2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0038, code lost:
        if (r9 == false) goto L_0x003c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x003a, code lost:
        r4 = r4 | 4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x003e, code lost:
        if (com.android.server.autofill.Helper.sDebug == false) goto L_0x0042;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0040, code lost:
        r4 = r4 | 8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0044, code lost:
        if (com.android.server.autofill.Helper.sVerbose == false) goto L_0x0048;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x0046, code lost:
        r4 = r4 | 16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x0048, code lost:
        r3.setState(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:?, code lost:
        throw r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x0052, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x0053, code lost:
        r1.finishBroadcast();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x0056, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x0057, code lost:
        r1.finishBroadcast();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x005b, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0010, code lost:
        r0 = 0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void sendStateToClients(boolean r9) {
        /*
            r8 = this;
            java.lang.Object r0 = r8.mLock
            monitor-enter(r0)
            android.os.RemoteCallbackList<android.view.autofill.IAutoFillManagerClient> r1 = r8.mClients     // Catch:{ all -> 0x005c }
            if (r1 != 0) goto L_0x0009
            monitor-exit(r0)     // Catch:{ all -> 0x005c }
            return
        L_0x0009:
            android.os.RemoteCallbackList<android.view.autofill.IAutoFillManagerClient> r1 = r8.mClients     // Catch:{ all -> 0x005c }
            int r2 = r1.beginBroadcast()     // Catch:{ all -> 0x005c }
            monitor-exit(r0)     // Catch:{ all -> 0x005c }
            r0 = 0
        L_0x0011:
            if (r0 >= r2) goto L_0x0057
            android.os.IInterface r3 = r1.getBroadcastItem(r0)     // Catch:{ all -> 0x0052 }
            android.view.autofill.IAutoFillManagerClient r3 = (android.view.autofill.IAutoFillManagerClient) r3     // Catch:{ all -> 0x0052 }
            java.lang.Object r4 = r8.mLock     // Catch:{ RemoteException -> 0x004e }
            monitor-enter(r4)     // Catch:{ RemoteException -> 0x004e }
            if (r9 != 0) goto L_0x0029
            boolean r5 = r8.isClientSessionDestroyedLocked(r3)     // Catch:{ all -> 0x0027 }
            if (r5 == 0) goto L_0x0025
            goto L_0x0029
        L_0x0025:
            r5 = 0
            goto L_0x002a
        L_0x0027:
            r5 = move-exception
            goto L_0x004c
        L_0x0029:
            r5 = 1
        L_0x002a:
            boolean r6 = r8.isEnabledLocked()     // Catch:{ all -> 0x0027 }
            monitor-exit(r4)     // Catch:{ all -> 0x0027 }
            r4 = 0
            if (r6 == 0) goto L_0x0034
            r4 = r4 | 1
        L_0x0034:
            if (r5 == 0) goto L_0x0038
            r4 = r4 | 2
        L_0x0038:
            if (r9 == 0) goto L_0x003c
            r4 = r4 | 4
        L_0x003c:
            boolean r7 = com.android.server.autofill.Helper.sDebug     // Catch:{ RemoteException -> 0x004e }
            if (r7 == 0) goto L_0x0042
            r4 = r4 | 8
        L_0x0042:
            boolean r7 = com.android.server.autofill.Helper.sVerbose     // Catch:{ RemoteException -> 0x004e }
            if (r7 == 0) goto L_0x0048
            r4 = r4 | 16
        L_0x0048:
            r3.setState(r4)     // Catch:{ RemoteException -> 0x004e }
            goto L_0x004f
        L_0x004c:
            monitor-exit(r4)     // Catch:{ all -> 0x0027 }
            throw r5     // Catch:{ RemoteException -> 0x004e }
        L_0x004e:
            r4 = move-exception
        L_0x004f:
            int r0 = r0 + 1
            goto L_0x0011
        L_0x0052:
            r0 = move-exception
            r1.finishBroadcast()
            throw r0
        L_0x0057:
            r1.finishBroadcast()
            return
        L_0x005c:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x005c }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.autofill.AutofillManagerServiceImpl.sendStateToClients(boolean):void");
    }

    @GuardedBy({"mLock"})
    private boolean isClientSessionDestroyedLocked(IAutoFillManagerClient client) {
        int sessionCount = this.mSessions.size();
        for (int i = 0; i < sessionCount; i++) {
            Session session = this.mSessions.valueAt(i);
            if (session.getClient().equals(client)) {
                return session.isDestroyed();
            }
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public void disableAutofillForApp(String packageName, long duration, int sessionId, boolean compatMode) {
        synchronized (this.mLock) {
            if (this.mDisabledApps == null) {
                this.mDisabledApps = new ArrayMap<>(1);
            }
            long expiration = SystemClock.elapsedRealtime() + duration;
            if (expiration < 0) {
                expiration = JobStatus.NO_LATEST_RUNTIME;
            }
            this.mDisabledApps.put(packageName, Long.valueOf(expiration));
            this.mMetricsLogger.write(Helper.newLogMaker(1231, packageName, getServicePackageName(), sessionId, compatMode).addTaggedData(1145, Integer.valueOf(duration > 2147483647L ? Integer.MAX_VALUE : (int) duration)));
        }
    }

    /* access modifiers changed from: package-private */
    public void disableAutofillForActivity(ComponentName componentName, long duration, int sessionId, boolean compatMode) {
        int intDuration;
        synchronized (this.mLock) {
            if (this.mDisabledActivities == null) {
                this.mDisabledActivities = new ArrayMap<>(1);
            }
            long expiration = SystemClock.elapsedRealtime() + duration;
            if (expiration < 0) {
                expiration = JobStatus.NO_LATEST_RUNTIME;
            }
            this.mDisabledActivities.put(componentName, Long.valueOf(expiration));
            if (duration > 2147483647L) {
                intDuration = Integer.MAX_VALUE;
            } else {
                intDuration = (int) duration;
            }
            LogMaker log = new LogMaker(1232).setComponentName(componentName).addTaggedData(908, getServicePackageName()).addTaggedData(1145, Integer.valueOf(intDuration)).addTaggedData(1456, Integer.valueOf(sessionId));
            if (compatMode) {
                log.addTaggedData(1414, 1);
            }
            this.mMetricsLogger.write(log);
        }
    }

    @GuardedBy({"mLock"})
    private boolean isAutofillDisabledLocked(ComponentName componentName) {
        Long expiration;
        long elapsedTime = 0;
        if (this.mDisabledActivities != null) {
            elapsedTime = SystemClock.elapsedRealtime();
            Long expiration2 = this.mDisabledActivities.get(componentName);
            if (expiration2 != null) {
                if (expiration2.longValue() >= elapsedTime) {
                    return true;
                }
                if (Helper.sVerbose) {
                    Slog.v(TAG, "Removing " + componentName.toShortString() + " from disabled list");
                }
                this.mDisabledActivities.remove(componentName);
            }
        }
        String packageName = componentName.getPackageName();
        ArrayMap<String, Long> arrayMap = this.mDisabledApps;
        if (arrayMap == null || (expiration = arrayMap.get(packageName)) == null) {
            return false;
        }
        if (elapsedTime == 0) {
            elapsedTime = SystemClock.elapsedRealtime();
        }
        if (expiration.longValue() >= elapsedTime) {
            return true;
        }
        if (Helper.sVerbose) {
            Slog.v(TAG, "Removing " + packageName + " from disabled list");
        }
        this.mDisabledApps.remove(packageName);
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean isFieldClassificationEnabled(int callingUid) {
        synchronized (this.mLock) {
            if (!isCalledByServiceLocked("isFieldClassificationEnabled", callingUid)) {
                return false;
            }
            boolean isFieldClassificationEnabledLocked = isFieldClassificationEnabledLocked();
            return isFieldClassificationEnabledLocked;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isFieldClassificationEnabledLocked() {
        return Settings.Secure.getIntForUser(getContext().getContentResolver(), "autofill_field_classification", 1, this.mUserId) == 1;
    }

    /* access modifiers changed from: package-private */
    public FieldClassificationStrategy getFieldClassificationStrategy() {
        return this.mFieldClassificationStrategy;
    }

    /* access modifiers changed from: package-private */
    public String[] getAvailableFieldClassificationAlgorithms(int callingUid) {
        synchronized (this.mLock) {
            if (!isCalledByServiceLocked("getFCAlgorithms()", callingUid)) {
                return null;
            }
            return this.mFieldClassificationStrategy.getAvailableAlgorithms();
        }
    }

    /* access modifiers changed from: package-private */
    public String getDefaultFieldClassificationAlgorithm(int callingUid) {
        synchronized (this.mLock) {
            if (!isCalledByServiceLocked("getDefaultFCAlgorithm()", callingUid)) {
                return null;
            }
            return this.mFieldClassificationStrategy.getDefaultAlgorithm();
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AutofillManagerServiceImpl: [userId=");
        sb.append(this.mUserId);
        sb.append(", component=");
        AutofillServiceInfo autofillServiceInfo = this.mInfo;
        sb.append(autofillServiceInfo != null ? autofillServiceInfo.getServiceInfo().getComponentName() : null);
        sb.append("]");
        return sb.toString();
    }

    private class PruneTask extends AsyncTask<Void, Void, Void> {
        private PruneTask() {
        }

        /* access modifiers changed from: protected */
        public Void doInBackground(Void... ignored) {
            int numSessionsToRemove;
            SparseArray<IBinder> sessionsToRemove;
            synchronized (AutofillManagerServiceImpl.this.mLock) {
                numSessionsToRemove = AutofillManagerServiceImpl.this.mSessions.size();
                sessionsToRemove = new SparseArray<>(numSessionsToRemove);
                for (int i = 0; i < numSessionsToRemove; i++) {
                    Session session = (Session) AutofillManagerServiceImpl.this.mSessions.valueAt(i);
                    sessionsToRemove.put(session.id, session.getActivityTokenLocked());
                }
            }
            IActivityTaskManager atm = ActivityTaskManager.getService();
            int i2 = 0;
            while (i2 < numSessionsToRemove) {
                try {
                    if (atm.getActivityClassForToken(sessionsToRemove.valueAt(i2)) != null) {
                        sessionsToRemove.removeAt(i2);
                        i2--;
                        numSessionsToRemove--;
                    }
                } catch (RemoteException e) {
                    Slog.w(AutofillManagerServiceImpl.TAG, "Cannot figure out if activity is finished", e);
                }
                i2++;
            }
            synchronized (AutofillManagerServiceImpl.this.mLock) {
                for (int i3 = 0; i3 < numSessionsToRemove; i3++) {
                    Session sessionToRemove = (Session) AutofillManagerServiceImpl.this.mSessions.get(sessionsToRemove.keyAt(i3));
                    if (sessionToRemove != null && sessionsToRemove.valueAt(i3) == sessionToRemove.getActivityTokenLocked()) {
                        if (!sessionToRemove.isSavingLocked()) {
                            if (Helper.sDebug) {
                                Slog.i(AutofillManagerServiceImpl.TAG, "Prune session " + sessionToRemove.id + " (" + sessionToRemove.getActivityTokenLocked() + ")");
                            }
                            sessionToRemove.removeSelfLocked();
                        } else if (Helper.sVerbose) {
                            Slog.v(AutofillManagerServiceImpl.TAG, "Session " + sessionToRemove.id + " is saving");
                        }
                    }
                }
            }
            return null;
        }
    }
}
