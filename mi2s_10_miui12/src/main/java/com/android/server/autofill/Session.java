package com.android.server.autofill;

import android.app.ActivityTaskManager;
import android.app.IAssistDataReceiver;
import android.app.assist.AssistStructure;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.metrics.LogMaker;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.SystemClock;
import android.service.autofill.AutofillFieldClassificationService;
import android.service.autofill.CompositeUserData;
import android.service.autofill.Dataset;
import android.service.autofill.FieldClassification;
import android.service.autofill.FieldClassificationUserData;
import android.service.autofill.FillContext;
import android.service.autofill.FillRequest;
import android.service.autofill.FillResponse;
import android.service.autofill.InternalSanitizer;
import android.service.autofill.SaveInfo;
import android.service.autofill.SaveRequest;
import android.service.autofill.UserData;
import android.service.autofill.ValueFinder;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.LocalLog;
import android.util.Slog;
import android.util.SparseArray;
import android.util.TimeUtils;
import android.view.KeyEvent;
import android.view.autofill.AutofillId;
import android.view.autofill.AutofillManager;
import android.view.autofill.AutofillValue;
import android.view.autofill.IAutoFillManagerClient;
import android.view.autofill.IAutofillWindowPresenter;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.server.autofill.RemoteFillService;
import com.android.server.autofill.ViewState;
import com.android.server.autofill.ui.AutoFillUI;
import com.android.server.autofill.ui.PendingUi;
import com.android.server.slice.SliceClientPermissions;
import com.android.server.wm.ActivityTaskManagerInternal;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

final class Session implements RemoteFillService.FillServiceCallbacks, ViewState.Listener, AutoFillUI.AutoFillUiCallback, ValueFinder {
    private static final String EXTRA_REQUEST_ID = "android.service.autofill.extra.REQUEST_ID";
    private static final String TAG = "AutofillSession";
    private static AtomicInteger sIdCounter = new AtomicInteger();
    public final int id;
    @GuardedBy({"mLock"})
    private IBinder mActivityToken;
    private final IAssistDataReceiver mAssistReceiver = new IAssistDataReceiver.Stub() {
        public void onHandleAssistData(Bundle resultData) throws RemoteException {
            FillRequest request;
            if (Session.this.mRemoteFillService == null) {
                Session session = Session.this;
                session.wtf((Exception) null, "onHandleAssistData() called without a remote service. mForAugmentedAutofillOnly: %s", Boolean.valueOf(session.mForAugmentedAutofillOnly));
                return;
            }
            AssistStructure structure = (AssistStructure) resultData.getParcelable(ActivityTaskManagerInternal.ASSIST_KEY_STRUCTURE);
            if (structure == null) {
                Slog.e(Session.TAG, "No assist structure - app might have crashed providing it");
                return;
            }
            Bundle receiverExtras = resultData.getBundle(ActivityTaskManagerInternal.ASSIST_KEY_RECEIVER_EXTRAS);
            if (receiverExtras == null) {
                Slog.e(Session.TAG, "No receiver extras - app might have crashed providing it");
                return;
            }
            int requestId = receiverExtras.getInt(Session.EXTRA_REQUEST_ID);
            if (Helper.sVerbose) {
                Slog.v(Session.TAG, "New structure for requestId " + requestId + ": " + structure);
            }
            synchronized (Session.this.mLock) {
                try {
                    structure.ensureDataForAutofill();
                    ArrayList<AutofillId> ids = Helper.getAutofillIds(structure, false);
                    for (int i = 0; i < ids.size(); i++) {
                        ids.get(i).setSessionId(Session.this.id);
                    }
                    int flags = structure.getFlags();
                    if (Session.this.mCompatMode) {
                        String[] urlBarIds = Session.this.mService.getUrlBarResourceIdsForCompatMode(Session.this.mComponentName.getPackageName());
                        if (Helper.sDebug) {
                            Slog.d(Session.TAG, "url_bars in compat mode: " + Arrays.toString(urlBarIds));
                        }
                        if (urlBarIds != null) {
                            AssistStructure.ViewNode unused = Session.this.mUrlBar = Helper.sanitizeUrlBar(structure, urlBarIds);
                            if (Session.this.mUrlBar != null) {
                                AutofillId urlBarId = Session.this.mUrlBar.getAutofillId();
                                if (Helper.sDebug) {
                                    Slog.d(Session.TAG, "Setting urlBar as id=" + urlBarId + " and domain " + Session.this.mUrlBar.getWebDomain());
                                }
                                Session.this.mViewStates.put(urlBarId, new ViewState(urlBarId, Session.this, 512));
                            }
                        }
                        flags |= 2;
                    }
                    structure.sanitizeForParceling(true);
                    if (Session.this.mContexts == null) {
                        ArrayList unused2 = Session.this.mContexts = new ArrayList(1);
                    }
                    Session.this.mContexts.add(new FillContext(requestId, structure, Session.this.mCurrentViewId));
                    Session.this.cancelCurrentRequestLocked();
                    int numContexts = Session.this.mContexts.size();
                    for (int i2 = 0; i2 < numContexts; i2++) {
                        Session.this.fillContextWithAllowedValuesLocked((FillContext) Session.this.mContexts.get(i2), flags);
                    }
                    request = new FillRequest(requestId, Session.this.mergePreviousSessionLocked(false), Session.this.mClientState, flags);
                } catch (RuntimeException e) {
                    Session.this.wtf(e, "Exception lazy loading assist structure for %s: %s", structure.getActivityComponent(), e);
                    return;
                }
            }
            Session.this.mRemoteFillService.onFillRequest(request);
        }

        public void onHandleAssistScreenshot(Bitmap screenshot) {
        }
    };
    @GuardedBy({"mLock"})
    private Runnable mAugmentedAutofillDestroyer;
    @GuardedBy({"mLock"})
    private ArrayList<AutofillId> mAugmentedAutofillableIds;
    @GuardedBy({"mLock"})
    private ArrayList<LogMaker> mAugmentedRequestsLogs;
    @GuardedBy({"mLock"})
    private IAutoFillManagerClient mClient;
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public Bundle mClientState;
    @GuardedBy({"mLock"})
    private IBinder.DeathRecipient mClientVulture;
    /* access modifiers changed from: private */
    public final boolean mCompatMode;
    /* access modifiers changed from: private */
    public final ComponentName mComponentName;
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public ArrayList<FillContext> mContexts;
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public AutofillId mCurrentViewId;
    @GuardedBy({"mLock"})
    private boolean mDestroyed;
    public final int mFlags;
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public boolean mForAugmentedAutofillOnly;
    private final Handler mHandler;
    private boolean mHasCallback;
    @GuardedBy({"mLock"})
    private boolean mIsSaving;
    /* access modifiers changed from: private */
    public final Object mLock;
    private final MetricsLogger mMetricsLogger = new MetricsLogger();
    @GuardedBy({"mLock"})
    private PendingUi mPendingSaveUi;
    /* access modifiers changed from: private */
    public final RemoteFillService mRemoteFillService;
    @GuardedBy({"mLock"})
    private final SparseArray<LogMaker> mRequestLogs = new SparseArray<>(1);
    @GuardedBy({"mLock"})
    private SparseArray<FillResponse> mResponses;
    @GuardedBy({"mLock"})
    private boolean mSaveOnAllViewsInvisible;
    @GuardedBy({"mLock"})
    private ArrayList<String> mSelectedDatasetIds;
    /* access modifiers changed from: private */
    public final AutofillManagerServiceImpl mService;
    private final long mStartTime;
    private final AutoFillUI mUi;
    @GuardedBy({"mLock"})
    private final LocalLog mUiLatencyHistory;
    @GuardedBy({"mLock"})
    private long mUiShownTime;
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public AssistStructure.ViewNode mUrlBar;
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public final ArrayMap<AutofillId, ViewState> mViewStates = new ArrayMap<>();
    @GuardedBy({"mLock"})
    private final LocalLog mWtfHistory;
    public final int taskId;
    public final int uid;

    @GuardedBy({"mLock"})
    private AutofillId[] getIdsOfAllViewStatesLocked() {
        int numViewState = this.mViewStates.size();
        AutofillId[] ids = new AutofillId[numViewState];
        for (int i = 0; i < numViewState; i++) {
            ids[i] = this.mViewStates.valueAt(i).id;
        }
        return ids;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0033, code lost:
        return r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x004b, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String findByAutofillId(android.view.autofill.AutofillId r8) {
        /*
            r7 = this;
            java.lang.Object r0 = r7.mLock
            monitor-enter(r0)
            android.view.autofill.AutofillValue r1 = r7.findValueLocked(r8)     // Catch:{ all -> 0x004c }
            r2 = 0
            if (r1 == 0) goto L_0x004a
            boolean r3 = r1.isText()     // Catch:{ all -> 0x004c }
            if (r3 == 0) goto L_0x001a
            java.lang.CharSequence r2 = r1.getTextValue()     // Catch:{ all -> 0x004c }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x004c }
            monitor-exit(r0)     // Catch:{ all -> 0x004c }
            return r2
        L_0x001a:
            boolean r3 = r1.isList()     // Catch:{ all -> 0x004c }
            if (r3 == 0) goto L_0x004a
            java.lang.CharSequence[] r3 = r7.getAutofillOptionsFromContextsLocked(r8)     // Catch:{ all -> 0x004c }
            if (r3 == 0) goto L_0x0034
            int r4 = r1.getListValue()     // Catch:{ all -> 0x004c }
            r5 = r3[r4]     // Catch:{ all -> 0x004c }
            if (r5 == 0) goto L_0x0032
            java.lang.String r2 = r5.toString()     // Catch:{ all -> 0x004c }
        L_0x0032:
            monitor-exit(r0)     // Catch:{ all -> 0x004c }
            return r2
        L_0x0034:
            java.lang.String r4 = "AutofillSession"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x004c }
            r5.<init>()     // Catch:{ all -> 0x004c }
            java.lang.String r6 = "findByAutofillId(): no autofill options for id "
            r5.append(r6)     // Catch:{ all -> 0x004c }
            r5.append(r8)     // Catch:{ all -> 0x004c }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x004c }
            android.util.Slog.w(r4, r5)     // Catch:{ all -> 0x004c }
        L_0x004a:
            monitor-exit(r0)     // Catch:{ all -> 0x004c }
            return r2
        L_0x004c:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x004c }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.autofill.Session.findByAutofillId(android.view.autofill.AutofillId):java.lang.String");
    }

    public AutofillValue findRawValueByAutofillId(AutofillId id2) {
        AutofillValue findValueLocked;
        synchronized (this.mLock) {
            findValueLocked = findValueLocked(id2);
        }
        return findValueLocked;
    }

    @GuardedBy({"mLock"})
    private AutofillValue findValueLocked(AutofillId autofillId) {
        AutofillValue value = findValueFromThisSessionOnlyLocked(autofillId);
        if (value != null) {
            return getSanitizedValue(createSanitizers(getSaveInfoLocked()), autofillId, value);
        }
        ArrayList<Session> previousSessions = this.mService.getPreviousSessionsLocked(this);
        if (previousSessions == null) {
            return null;
        }
        if (Helper.sDebug) {
            Slog.d(TAG, "findValueLocked(): looking on " + previousSessions.size() + " previous sessions for autofillId " + autofillId);
        }
        for (int i = 0; i < previousSessions.size(); i++) {
            Session previousSession = previousSessions.get(i);
            AutofillValue previousValue = previousSession.findValueFromThisSessionOnlyLocked(autofillId);
            if (previousValue != null) {
                return getSanitizedValue(createSanitizers(previousSession.getSaveInfoLocked()), autofillId, previousValue);
            }
        }
        return null;
    }

    private AutofillValue findValueFromThisSessionOnlyLocked(AutofillId autofillId) {
        ViewState state = this.mViewStates.get(autofillId);
        if (state != null) {
            AutofillValue value = state.getCurrentValue();
            if (value != null) {
                return value;
            }
            if (Helper.sDebug) {
                Slog.d(TAG, "findValueLocked(): no current value for " + autofillId);
            }
            return getValueFromContextsLocked(autofillId);
        } else if (!Helper.sDebug) {
            return null;
        } else {
            Slog.d(TAG, "findValueLocked(): no view state for " + autofillId);
            return null;
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public void fillContextWithAllowedValuesLocked(FillContext fillContext, int flags) {
        AssistStructure.ViewNode[] nodes = fillContext.findViewNodesByAutofillIds(getIdsOfAllViewStatesLocked());
        int numViewState = this.mViewStates.size();
        for (int i = 0; i < numViewState; i++) {
            ViewState viewState = this.mViewStates.valueAt(i);
            AssistStructure.ViewNode node = nodes[i];
            if (node != null) {
                AutofillValue currentValue = viewState.getCurrentValue();
                AutofillValue filledValue = viewState.getAutofilledValue();
                AssistStructure.AutofillOverlay overlay = new AssistStructure.AutofillOverlay();
                if (filledValue != null && filledValue.equals(currentValue)) {
                    overlay.value = currentValue;
                }
                AutofillId autofillId = this.mCurrentViewId;
                if (autofillId != null) {
                    overlay.focused = autofillId.equals(viewState.id);
                    if (overlay.focused && (flags & 1) != 0) {
                        overlay.value = currentValue;
                    }
                }
                node.setAutofillOverlay(overlay);
            } else if (Helper.sVerbose) {
                Slog.v(TAG, "fillContextWithAllowedValuesLocked(): no node for " + viewState.id);
            }
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public void cancelCurrentRequestLocked() {
        RemoteFillService remoteFillService = this.mRemoteFillService;
        if (remoteFillService == null) {
            wtf((Exception) null, "cancelCurrentRequestLocked() called without a remote service. mForAugmentedAutofillOnly: %s", Boolean.valueOf(this.mForAugmentedAutofillOnly));
            return;
        }
        remoteFillService.cancelCurrentRequest().whenComplete(new BiConsumer() {
            public final void accept(Object obj, Object obj2) {
                Session.this.lambda$cancelCurrentRequestLocked$0$Session((Integer) obj, (Throwable) obj2);
            }
        });
    }

    public /* synthetic */ void lambda$cancelCurrentRequestLocked$0$Session(Integer canceledRequest, Throwable err) {
        ArrayList<FillContext> arrayList;
        if (err != null) {
            Slog.e(TAG, "cancelCurrentRequest(): unexpected exception", err);
        } else if (canceledRequest.intValue() != Integer.MIN_VALUE && (arrayList = this.mContexts) != null) {
            for (int i = arrayList.size() - 1; i >= 0; i--) {
                if (this.mContexts.get(i).getRequestId() == canceledRequest.intValue()) {
                    if (Helper.sDebug) {
                        Slog.d(TAG, "cancelCurrentRequest(): id = " + canceledRequest);
                    }
                    this.mContexts.remove(i);
                    return;
                }
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 10 */
    @GuardedBy({"mLock"})
    private void requestNewFillResponseLocked(ViewState viewState, int newState, int flags) {
        int requestId;
        long identity;
        if (this.mForAugmentedAutofillOnly || this.mRemoteFillService == null) {
            if (Helper.sVerbose != 0) {
                Slog.v(TAG, "requestNewFillResponse(): triggering augmented autofill instead (mForAugmentedAutofillOnly=" + this.mForAugmentedAutofillOnly + ", flags=" + flags + ")");
            }
            this.mForAugmentedAutofillOnly = true;
            triggerAugmentedAutofillLocked(flags);
            return;
        }
        viewState.setState(newState);
        do {
            requestId = sIdCounter.getAndIncrement();
        } while (requestId == Integer.MIN_VALUE);
        int ordinal = this.mRequestLogs.size() + 1;
        LogMaker log = newLogMaker(907).addTaggedData(1454, Integer.valueOf(ordinal));
        if (flags != 0) {
            log.addTaggedData(1452, Integer.valueOf(flags));
        }
        this.mRequestLogs.put(requestId, log);
        if (Helper.sVerbose) {
            Slog.v(TAG, "Requesting structure for request #" + ordinal + " ,requestId=" + requestId + ", flags=" + flags);
        }
        cancelCurrentRequestLocked();
        try {
            Bundle receiverExtras = new Bundle();
            receiverExtras.putInt(EXTRA_REQUEST_ID, requestId);
            identity = Binder.clearCallingIdentity();
            if (!ActivityTaskManager.getService().requestAutofillData(this.mAssistReceiver, receiverExtras, this.mActivityToken, flags)) {
                Slog.w(TAG, "failed to request autofill data for " + this.mActivityToken);
            }
            Binder.restoreCallingIdentity(identity);
        } catch (RemoteException e) {
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(identity);
            throw th;
        }
    }

    Session(AutofillManagerServiceImpl service, AutoFillUI ui, Context context, Handler handler, int userId, Object lock, int sessionId, int taskId2, int uid2, IBinder activityToken, IBinder client, boolean hasCallback, LocalLog uiLatencyHistory, LocalLog wtfHistory, ComponentName serviceComponentName, ComponentName componentName, boolean compatMode, boolean bindInstantServiceAllowed, boolean forAugmentedAutofillOnly, int flags) {
        RemoteFillService remoteFillService;
        int i = sessionId;
        if (i < 0) {
            wtf((Exception) null, "Non-positive sessionId: %s", Integer.valueOf(sessionId));
        }
        this.id = i;
        this.mFlags = flags;
        this.taskId = taskId2;
        this.uid = uid2;
        this.mStartTime = SystemClock.elapsedRealtime();
        this.mService = service;
        this.mLock = lock;
        this.mUi = ui;
        this.mHandler = handler;
        if (serviceComponentName == null) {
            remoteFillService = null;
        } else {
            remoteFillService = new RemoteFillService(context, serviceComponentName, userId, this, bindInstantServiceAllowed);
        }
        this.mRemoteFillService = remoteFillService;
        this.mActivityToken = activityToken;
        this.mHasCallback = hasCallback;
        this.mUiLatencyHistory = uiLatencyHistory;
        this.mWtfHistory = wtfHistory;
        this.mComponentName = componentName;
        this.mCompatMode = compatMode;
        this.mForAugmentedAutofillOnly = forAugmentedAutofillOnly;
        setClientLocked(client);
        this.mMetricsLogger.write(newLogMaker(906).addTaggedData(1452, Integer.valueOf(flags)));
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public IBinder getActivityTokenLocked() {
        return this.mActivityToken;
    }

    /* access modifiers changed from: package-private */
    public void switchActivity(IBinder newActivity, IBinder newClient) {
        synchronized (this.mLock) {
            if (this.mDestroyed) {
                Slog.w(TAG, "Call to Session#switchActivity() rejected - session: " + this.id + " destroyed");
                return;
            }
            this.mActivityToken = newActivity;
            setClientLocked(newClient);
            updateTrackedIdsLocked();
        }
    }

    @GuardedBy({"mLock"})
    private void setClientLocked(IBinder client) {
        unlinkClientVultureLocked();
        this.mClient = IAutoFillManagerClient.Stub.asInterface(client);
        this.mClientVulture = new IBinder.DeathRecipient() {
            public final void binderDied() {
                Session.this.lambda$setClientLocked$1$Session();
            }
        };
        try {
            this.mClient.asBinder().linkToDeath(this.mClientVulture, 0);
        } catch (RemoteException e) {
            Slog.w(TAG, "could not set binder death listener on autofill client: " + e);
            this.mClientVulture = null;
        }
    }

    public /* synthetic */ void lambda$setClientLocked$1$Session() {
        Slog.d(TAG, "handling death of " + this.mActivityToken + " when saving=" + this.mIsSaving);
        synchronized (this.mLock) {
            if (this.mIsSaving) {
                this.mUi.hideFillUi(this);
            } else {
                this.mUi.destroyAll(this.mPendingSaveUi, this, false);
            }
        }
    }

    @GuardedBy({"mLock"})
    private void unlinkClientVultureLocked() {
        IAutoFillManagerClient iAutoFillManagerClient = this.mClient;
        if (iAutoFillManagerClient != null && this.mClientVulture != null) {
            if (!iAutoFillManagerClient.asBinder().unlinkToDeath(this.mClientVulture, 0)) {
                Slog.w(TAG, "unlinking vulture from death failed for " + this.mActivityToken);
            }
            this.mClientVulture = null;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0098, code lost:
        r1.mService.setLastResponse(r1.id, r3);
        r14 = r21.getDisableDuration();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x00a8, code lost:
        if (r14 <= 0) goto L_0x012d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x00aa, code lost:
        r13 = r21.getFlags();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x00b0, code lost:
        if ((r13 & 2) == 0) goto L_0x00c4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x00b2, code lost:
        r0 = r13;
        r1.mService.disableAutofillForActivity(r1.mComponentName, r14, r1.id, r1.mCompatMode);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x00c4, code lost:
        r0 = r13;
        r1.mService.disableAutofillForApp(r1.mComponentName.getPackageName(), r14, r1.id, r1.mCompatMode);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00d9, code lost:
        if (triggerAugmentedAutofillLocked(r4) == null) goto L_0x0100;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00db, code lost:
        r1.mForAugmentedAutofillOnly = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00e0, code lost:
        if (com.android.server.autofill.Helper.sDebug == false) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00e2, code lost:
        android.util.Slog.d(TAG, "Service disabled autofill for " + r1.mComponentName + ", but session is kept for augmented autofill only");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0102, code lost:
        if (com.android.server.autofill.Helper.sDebug == false) goto L_0x012a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x0104, code lost:
        r8 = new java.lang.StringBuilder("Service disabled autofill for ");
        r8.append(r1.mComponentName);
        r8.append(": flags=");
        r8.append(r0);
        r8 = r8.append(", duration=");
        android.util.TimeUtils.formatDuration(r14, r8);
        android.util.Slog.d(TAG, r8.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x012a, code lost:
        r8 = 4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x012d, code lost:
        r8 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x0133, code lost:
        if (r21.getDatasets() == null) goto L_0x013f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x013d, code lost:
        if (r21.getDatasets().isEmpty() == false) goto L_0x0145;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x0143, code lost:
        if (r21.getAuthentication() == null) goto L_0x0149;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x0147, code lost:
        if (r14 <= 0) goto L_0x014c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x0149, code lost:
        notifyUnavailableToClient(r8, (java.util.ArrayList<android.view.autofill.AutofillId>) null);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x014c, code lost:
        if (r6 == null) goto L_0x0174;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x0153, code lost:
        if (r21.getDatasets() != null) goto L_0x0157;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x0155, code lost:
        r0 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x0157, code lost:
        r0 = r21.getDatasets().size();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x015f, code lost:
        r6.addTaggedData(909, java.lang.Integer.valueOf(r0));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x0168, code lost:
        if (r7 == null) goto L_0x0174;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x016a, code lost:
        r6.addTaggedData(1271, java.lang.Integer.valueOf(r7.length));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x0174, code lost:
        r9 = r1.mLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x0176, code lost:
        monitor-enter(r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:?, code lost:
        processResponseLocked(r3, (android.os.Bundle) null, r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x017a, code lost:
        monitor-exit(r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x017b, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:76:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:77:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onFillRequestSuccess(int r20, android.service.autofill.FillResponse r21, java.lang.String r22, int r23) {
        /*
            r19 = this;
            r1 = r19
            r2 = r20
            r3 = r21
            r4 = r23
            java.lang.Object r5 = r1.mLock
            monitor-enter(r5)
            boolean r0 = r1.mDestroyed     // Catch:{ all -> 0x017f }
            if (r0 == 0) goto L_0x002e
            java.lang.String r0 = "AutofillSession"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x017f }
            r6.<init>()     // Catch:{ all -> 0x017f }
            java.lang.String r7 = "Call to Session#onFillRequestSuccess() rejected - session: "
            r6.append(r7)     // Catch:{ all -> 0x017f }
            int r7 = r1.id     // Catch:{ all -> 0x017f }
            r6.append(r7)     // Catch:{ all -> 0x017f }
            java.lang.String r7 = " destroyed"
            r6.append(r7)     // Catch:{ all -> 0x017f }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x017f }
            android.util.Slog.w(r0, r6)     // Catch:{ all -> 0x017f }
            monitor-exit(r5)     // Catch:{ all -> 0x017f }
            return
        L_0x002e:
            android.util.SparseArray<android.metrics.LogMaker> r0 = r1.mRequestLogs     // Catch:{ all -> 0x017f }
            java.lang.Object r0 = r0.get(r2)     // Catch:{ all -> 0x017f }
            android.metrics.LogMaker r0 = (android.metrics.LogMaker) r0     // Catch:{ all -> 0x017f }
            r6 = r0
            if (r6 == 0) goto L_0x003f
            r0 = 10
            r6.setType(r0)     // Catch:{ all -> 0x017f }
            goto L_0x0056
        L_0x003f:
            java.lang.String r0 = "AutofillSession"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x017f }
            r7.<init>()     // Catch:{ all -> 0x017f }
            java.lang.String r8 = "onFillRequestSuccess(): no request log for id "
            r7.append(r8)     // Catch:{ all -> 0x017f }
            r7.append(r2)     // Catch:{ all -> 0x017f }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x017f }
            android.util.Slog.w(r0, r7)     // Catch:{ all -> 0x017f }
        L_0x0056:
            r0 = 909(0x38d, float:1.274E-42)
            if (r3 != 0) goto L_0x0069
            if (r6 == 0) goto L_0x0064
            r7 = -1
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)     // Catch:{ all -> 0x017f }
            r6.addTaggedData(r0, r7)     // Catch:{ all -> 0x017f }
        L_0x0064:
            r1.processNullResponseLocked(r2, r4)     // Catch:{ all -> 0x017f }
            monitor-exit(r5)     // Catch:{ all -> 0x017f }
            return
        L_0x0069:
            android.view.autofill.AutofillId[] r7 = r21.getFieldClassificationIds()     // Catch:{ all -> 0x017f }
            if (r7 == 0) goto L_0x0097
            com.android.server.autofill.AutofillManagerServiceImpl r8 = r1.mService     // Catch:{ all -> 0x017f }
            boolean r8 = r8.isFieldClassificationEnabledLocked()     // Catch:{ all -> 0x017f }
            if (r8 != 0) goto L_0x0097
            java.lang.String r0 = "AutofillSession"
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x017f }
            r8.<init>()     // Catch:{ all -> 0x017f }
            java.lang.String r9 = "Ignoring "
            r8.append(r9)     // Catch:{ all -> 0x017f }
            r8.append(r3)     // Catch:{ all -> 0x017f }
            java.lang.String r9 = " because field detection is disabled"
            r8.append(r9)     // Catch:{ all -> 0x017f }
            java.lang.String r8 = r8.toString()     // Catch:{ all -> 0x017f }
            android.util.Slog.w(r0, r8)     // Catch:{ all -> 0x017f }
            r1.processNullResponseLocked(r2, r4)     // Catch:{ all -> 0x017f }
            monitor-exit(r5)     // Catch:{ all -> 0x017f }
            return
        L_0x0097:
            monitor-exit(r5)     // Catch:{ all -> 0x017f }
            com.android.server.autofill.AutofillManagerServiceImpl r5 = r1.mService
            int r8 = r1.id
            r5.setLastResponse(r8, r3)
            r5 = 0
            long r14 = r21.getDisableDuration()
            r16 = 0
            int r8 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1))
            if (r8 <= 0) goto L_0x012d
            int r13 = r21.getFlags()
            r8 = r13 & 2
            if (r8 == 0) goto L_0x00c4
            com.android.server.autofill.AutofillManagerServiceImpl r8 = r1.mService
            android.content.ComponentName r9 = r1.mComponentName
            int r12 = r1.id
            boolean r10 = r1.mCompatMode
            r18 = r10
            r10 = r14
            r0 = r13
            r13 = r18
            r8.disableAutofillForActivity(r9, r10, r12, r13)
            goto L_0x00d5
        L_0x00c4:
            r0 = r13
            com.android.server.autofill.AutofillManagerServiceImpl r8 = r1.mService
            android.content.ComponentName r9 = r1.mComponentName
            java.lang.String r9 = r9.getPackageName()
            int r12 = r1.id
            boolean r13 = r1.mCompatMode
            r10 = r14
            r8.disableAutofillForApp(r9, r10, r12, r13)
        L_0x00d5:
            java.lang.Runnable r8 = r1.triggerAugmentedAutofillLocked(r4)
            if (r8 == 0) goto L_0x0100
            r8 = 1
            r1.mForAugmentedAutofillOnly = r8
            boolean r8 = com.android.server.autofill.Helper.sDebug
            if (r8 == 0) goto L_0x00ff
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "Service disabled autofill for "
            r8.append(r9)
            android.content.ComponentName r9 = r1.mComponentName
            r8.append(r9)
            java.lang.String r9 = ", but session is kept for augmented autofill only"
            r8.append(r9)
            java.lang.String r8 = r8.toString()
            java.lang.String r9 = "AutofillSession"
            android.util.Slog.d(r9, r8)
        L_0x00ff:
            return
        L_0x0100:
            boolean r8 = com.android.server.autofill.Helper.sDebug
            if (r8 == 0) goto L_0x012a
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            java.lang.String r9 = "Service disabled autofill for "
            r8.<init>(r9)
            android.content.ComponentName r9 = r1.mComponentName
            r8.append(r9)
            java.lang.String r9 = ": flags="
            r8.append(r9)
            r8.append(r0)
            java.lang.String r9 = ", duration="
            java.lang.StringBuilder r8 = r8.append(r9)
            android.util.TimeUtils.formatDuration(r14, r8)
            java.lang.String r9 = r8.toString()
            java.lang.String r10 = "AutofillSession"
            android.util.Slog.d(r10, r9)
        L_0x012a:
            r5 = 4
            r8 = r5
            goto L_0x012e
        L_0x012d:
            r8 = r5
        L_0x012e:
            java.util.List r0 = r21.getDatasets()
            r5 = 0
            if (r0 == 0) goto L_0x013f
            java.util.List r0 = r21.getDatasets()
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0145
        L_0x013f:
            android.content.IntentSender r0 = r21.getAuthentication()
            if (r0 == 0) goto L_0x0149
        L_0x0145:
            int r0 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1))
            if (r0 <= 0) goto L_0x014c
        L_0x0149:
            r1.notifyUnavailableToClient(r8, r5)
        L_0x014c:
            if (r6 == 0) goto L_0x0174
            java.util.List r0 = r21.getDatasets()
            if (r0 != 0) goto L_0x0157
            r0 = 0
            goto L_0x015f
        L_0x0157:
            java.util.List r0 = r21.getDatasets()
            int r0 = r0.size()
        L_0x015f:
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r9 = 909(0x38d, float:1.274E-42)
            r6.addTaggedData(r9, r0)
            if (r7 == 0) goto L_0x0174
            r0 = 1271(0x4f7, float:1.781E-42)
            int r9 = r7.length
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            r6.addTaggedData(r0, r9)
        L_0x0174:
            java.lang.Object r9 = r1.mLock
            monitor-enter(r9)
            r1.processResponseLocked(r3, r5, r4)     // Catch:{ all -> 0x017c }
            monitor-exit(r9)     // Catch:{ all -> 0x017c }
            return
        L_0x017c:
            r0 = move-exception
            monitor-exit(r9)     // Catch:{ all -> 0x017c }
            throw r0
        L_0x017f:
            r0 = move-exception
            monitor-exit(r5)     // Catch:{ all -> 0x017f }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.autofill.Session.onFillRequestSuccess(int, android.service.autofill.FillResponse, java.lang.String, int):void");
    }

    public void onFillRequestFailure(int requestId, CharSequence message) {
        onFillRequestFailureOrTimeout(requestId, false, message);
    }

    public void onFillRequestTimeout(int requestId) {
        onFillRequestFailureOrTimeout(requestId, true, (CharSequence) null);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:29:0x00c2, code lost:
        notifyUnavailableToClient(6, (java.util.ArrayList<android.view.autofill.AutofillId>) null);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x00c7, code lost:
        if (r0 == false) goto L_0x00d0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x00c9, code lost:
        getUiForShowing().showError(r10, (com.android.server.autofill.ui.AutoFillUI.AutoFillUiCallback) r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x00d0, code lost:
        removeSelf();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00d3, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void onFillRequestFailureOrTimeout(int r8, boolean r9, java.lang.CharSequence r10) {
        /*
            r7 = this;
            boolean r0 = android.text.TextUtils.isEmpty(r10)
            r0 = r0 ^ 1
            java.lang.Object r1 = r7.mLock
            monitor-enter(r1)
            boolean r2 = r7.mDestroyed     // Catch:{ all -> 0x00d4 }
            if (r2 == 0) goto L_0x0034
            java.lang.String r2 = "AutofillSession"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x00d4 }
            r3.<init>()     // Catch:{ all -> 0x00d4 }
            java.lang.String r4 = "Call to Session#onFillRequestFailureOrTimeout(req="
            r3.append(r4)     // Catch:{ all -> 0x00d4 }
            r3.append(r8)     // Catch:{ all -> 0x00d4 }
            java.lang.String r4 = ") rejected - session: "
            r3.append(r4)     // Catch:{ all -> 0x00d4 }
            int r4 = r7.id     // Catch:{ all -> 0x00d4 }
            r3.append(r4)     // Catch:{ all -> 0x00d4 }
            java.lang.String r4 = " destroyed"
            r3.append(r4)     // Catch:{ all -> 0x00d4 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x00d4 }
            android.util.Slog.w(r2, r3)     // Catch:{ all -> 0x00d4 }
            monitor-exit(r1)     // Catch:{ all -> 0x00d4 }
            return
        L_0x0034:
            boolean r2 = com.android.server.autofill.Helper.sDebug     // Catch:{ all -> 0x00d4 }
            if (r2 == 0) goto L_0x0056
            java.lang.String r2 = "AutofillSession"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x00d4 }
            r3.<init>()     // Catch:{ all -> 0x00d4 }
            java.lang.String r4 = "finishing session due to service "
            r3.append(r4)     // Catch:{ all -> 0x00d4 }
            if (r9 == 0) goto L_0x004a
            java.lang.String r4 = "timeout"
            goto L_0x004c
        L_0x004a:
            java.lang.String r4 = "failure"
        L_0x004c:
            r3.append(r4)     // Catch:{ all -> 0x00d4 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x00d4 }
            android.util.Slog.d(r2, r3)     // Catch:{ all -> 0x00d4 }
        L_0x0056:
            com.android.server.autofill.AutofillManagerServiceImpl r2 = r7.mService     // Catch:{ all -> 0x00d4 }
            r2.resetLastResponse()     // Catch:{ all -> 0x00d4 }
            android.util.SparseArray<android.metrics.LogMaker> r2 = r7.mRequestLogs     // Catch:{ all -> 0x00d4 }
            java.lang.Object r2 = r2.get(r8)     // Catch:{ all -> 0x00d4 }
            android.metrics.LogMaker r2 = (android.metrics.LogMaker) r2     // Catch:{ all -> 0x00d4 }
            if (r2 != 0) goto L_0x007d
            java.lang.String r3 = "AutofillSession"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x00d4 }
            r4.<init>()     // Catch:{ all -> 0x00d4 }
            java.lang.String r5 = "onFillRequestFailureOrTimeout(): no log for id "
            r4.append(r5)     // Catch:{ all -> 0x00d4 }
            r4.append(r8)     // Catch:{ all -> 0x00d4 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x00d4 }
            android.util.Slog.w(r3, r4)     // Catch:{ all -> 0x00d4 }
            goto L_0x0086
        L_0x007d:
            if (r9 == 0) goto L_0x0081
            r3 = 2
            goto L_0x0083
        L_0x0081:
            r3 = 11
        L_0x0083:
            r2.setType(r3)     // Catch:{ all -> 0x00d4 }
        L_0x0086:
            if (r0 == 0) goto L_0x00c1
            com.android.server.autofill.AutofillManagerServiceImpl r3 = r7.mService     // Catch:{ all -> 0x00d4 }
            int r3 = r3.getTargedSdkLocked()     // Catch:{ all -> 0x00d4 }
            r4 = 29
            if (r3 < r4) goto L_0x00b2
            r0 = 0
            java.lang.String r4 = "AutofillSession"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x00d4 }
            r5.<init>()     // Catch:{ all -> 0x00d4 }
            java.lang.String r6 = "onFillRequestFailureOrTimeout(): not showing '"
            r5.append(r6)     // Catch:{ all -> 0x00d4 }
            r5.append(r10)     // Catch:{ all -> 0x00d4 }
            java.lang.String r6 = "' because service's targetting API "
            r5.append(r6)     // Catch:{ all -> 0x00d4 }
            r5.append(r3)     // Catch:{ all -> 0x00d4 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x00d4 }
            android.util.Slog.w(r4, r5)     // Catch:{ all -> 0x00d4 }
        L_0x00b2:
            if (r10 == 0) goto L_0x00c1
            r4 = 1572(0x624, float:2.203E-42)
            int r5 = r10.length()     // Catch:{ all -> 0x00d4 }
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ all -> 0x00d4 }
            r2.addTaggedData(r4, r5)     // Catch:{ all -> 0x00d4 }
        L_0x00c1:
            monitor-exit(r1)     // Catch:{ all -> 0x00d4 }
            r1 = 6
            r2 = 0
            r7.notifyUnavailableToClient(r1, r2)
            if (r0 == 0) goto L_0x00d0
            com.android.server.autofill.ui.AutoFillUI r1 = r7.getUiForShowing()
            r1.showError((java.lang.CharSequence) r10, (com.android.server.autofill.ui.AutoFillUI.AutoFillUiCallback) r7)
        L_0x00d0:
            r7.removeSelf()
            return
        L_0x00d4:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x00d4 }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.autofill.Session.onFillRequestFailureOrTimeout(int, boolean, java.lang.CharSequence):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x002a, code lost:
        r0 = newLogMaker(918, r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0030, code lost:
        if (r6 != null) goto L_0x0035;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0032, code lost:
        r1 = 10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0035, code lost:
        r1 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0036, code lost:
        r4.mMetricsLogger.write(r0.setType(r1));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x003f, code lost:
        if (r6 == null) goto L_0x004f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0043, code lost:
        if (com.android.server.autofill.Helper.sDebug == false) goto L_0x004c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0045, code lost:
        android.util.Slog.d(TAG, "Starting intent sender on save()");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x004c, code lost:
        startIntentSender(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x004f, code lost:
        removeSelf();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0052, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onSaveRequestSuccess(java.lang.String r5, android.content.IntentSender r6) {
        /*
            r4 = this;
            java.lang.Object r0 = r4.mLock
            monitor-enter(r0)
            r1 = 0
            r4.mIsSaving = r1     // Catch:{ all -> 0x0053 }
            boolean r1 = r4.mDestroyed     // Catch:{ all -> 0x0053 }
            if (r1 == 0) goto L_0x0029
            java.lang.String r1 = "AutofillSession"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0053 }
            r2.<init>()     // Catch:{ all -> 0x0053 }
            java.lang.String r3 = "Call to Session#onSaveRequestSuccess() rejected - session: "
            r2.append(r3)     // Catch:{ all -> 0x0053 }
            int r3 = r4.id     // Catch:{ all -> 0x0053 }
            r2.append(r3)     // Catch:{ all -> 0x0053 }
            java.lang.String r3 = " destroyed"
            r2.append(r3)     // Catch:{ all -> 0x0053 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0053 }
            android.util.Slog.w(r1, r2)     // Catch:{ all -> 0x0053 }
            monitor-exit(r0)     // Catch:{ all -> 0x0053 }
            return
        L_0x0029:
            monitor-exit(r0)     // Catch:{ all -> 0x0053 }
            r0 = 918(0x396, float:1.286E-42)
            android.metrics.LogMaker r0 = r4.newLogMaker(r0, r5)
            if (r6 != 0) goto L_0x0035
            r1 = 10
            goto L_0x0036
        L_0x0035:
            r1 = 1
        L_0x0036:
            android.metrics.LogMaker r0 = r0.setType(r1)
            com.android.internal.logging.MetricsLogger r1 = r4.mMetricsLogger
            r1.write(r0)
            if (r6 == 0) goto L_0x004f
            boolean r1 = com.android.server.autofill.Helper.sDebug
            if (r1 == 0) goto L_0x004c
            java.lang.String r1 = "AutofillSession"
            java.lang.String r2 = "Starting intent sender on save()"
            android.util.Slog.d(r1, r2)
        L_0x004c:
            r4.startIntentSender(r6)
        L_0x004f:
            r4.removeSelf()
            return
        L_0x0053:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0053 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.autofill.Session.onSaveRequestSuccess(java.lang.String, android.content.IntentSender):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x005c, code lost:
        r1 = newLogMaker(918, r8).setType(11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0068, code lost:
        if (r7 == null) goto L_0x0077;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x006a, code lost:
        r1.addTaggedData(1572, java.lang.Integer.valueOf(r7.length()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0077, code lost:
        r6.mMetricsLogger.write(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x007c, code lost:
        if (r0 == false) goto L_0x0085;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x007e, code lost:
        getUiForShowing().showError(r7, (com.android.server.autofill.ui.AutoFillUI.AutoFillUiCallback) r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0085, code lost:
        removeSelf();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0088, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onSaveRequestFailure(java.lang.CharSequence r7, java.lang.String r8) {
        /*
            r6 = this;
            boolean r0 = android.text.TextUtils.isEmpty(r7)
            r0 = r0 ^ 1
            java.lang.Object r1 = r6.mLock
            monitor-enter(r1)
            r2 = 0
            r6.mIsSaving = r2     // Catch:{ all -> 0x0089 }
            boolean r2 = r6.mDestroyed     // Catch:{ all -> 0x0089 }
            if (r2 == 0) goto L_0x002f
            java.lang.String r2 = "AutofillSession"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0089 }
            r3.<init>()     // Catch:{ all -> 0x0089 }
            java.lang.String r4 = "Call to Session#onSaveRequestFailure() rejected - session: "
            r3.append(r4)     // Catch:{ all -> 0x0089 }
            int r4 = r6.id     // Catch:{ all -> 0x0089 }
            r3.append(r4)     // Catch:{ all -> 0x0089 }
            java.lang.String r4 = " destroyed"
            r3.append(r4)     // Catch:{ all -> 0x0089 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0089 }
            android.util.Slog.w(r2, r3)     // Catch:{ all -> 0x0089 }
            monitor-exit(r1)     // Catch:{ all -> 0x0089 }
            return
        L_0x002f:
            if (r0 == 0) goto L_0x005b
            com.android.server.autofill.AutofillManagerServiceImpl r2 = r6.mService     // Catch:{ all -> 0x0089 }
            int r2 = r2.getTargedSdkLocked()     // Catch:{ all -> 0x0089 }
            r3 = 29
            if (r2 < r3) goto L_0x005b
            r0 = 0
            java.lang.String r3 = "AutofillSession"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0089 }
            r4.<init>()     // Catch:{ all -> 0x0089 }
            java.lang.String r5 = "onSaveRequestFailure(): not showing '"
            r4.append(r5)     // Catch:{ all -> 0x0089 }
            r4.append(r7)     // Catch:{ all -> 0x0089 }
            java.lang.String r5 = "' because service's targetting API "
            r4.append(r5)     // Catch:{ all -> 0x0089 }
            r4.append(r2)     // Catch:{ all -> 0x0089 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0089 }
            android.util.Slog.w(r3, r4)     // Catch:{ all -> 0x0089 }
        L_0x005b:
            monitor-exit(r1)     // Catch:{ all -> 0x0089 }
            r1 = 918(0x396, float:1.286E-42)
            android.metrics.LogMaker r1 = r6.newLogMaker(r1, r8)
            r2 = 11
            android.metrics.LogMaker r1 = r1.setType(r2)
            if (r7 == 0) goto L_0x0077
            r2 = 1572(0x624, float:2.203E-42)
            int r3 = r7.length()
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r1.addTaggedData(r2, r3)
        L_0x0077:
            com.android.internal.logging.MetricsLogger r2 = r6.mMetricsLogger
            r2.write(r1)
            if (r0 == 0) goto L_0x0085
            com.android.server.autofill.ui.AutoFillUI r2 = r6.getUiForShowing()
            r2.showError((java.lang.CharSequence) r7, (com.android.server.autofill.ui.AutoFillUI.AutoFillUiCallback) r6)
        L_0x0085:
            r6.removeSelf()
            return
        L_0x0089:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0089 }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.autofill.Session.onSaveRequestFailure(java.lang.CharSequence, java.lang.String):void");
    }

    @GuardedBy({"mLock"})
    private FillContext getFillContextByRequestIdLocked(int requestId) {
        ArrayList<FillContext> arrayList = this.mContexts;
        if (arrayList == null) {
            return null;
        }
        int numContexts = arrayList.size();
        for (int i = 0; i < numContexts; i++) {
            FillContext context = this.mContexts.get(i);
            if (context.getRequestId() == requestId) {
                return context;
            }
        }
        return null;
    }

    public void authenticate(int requestId, int datasetIndex, IntentSender intent, Bundle extras) {
        if (Helper.sDebug) {
            Slog.d(TAG, "authenticate(): requestId=" + requestId + "; datasetIdx=" + datasetIndex + "; intentSender=" + intent);
        }
        synchronized (this.mLock) {
            if (this.mDestroyed) {
                Slog.w(TAG, "Call to Session#authenticate() rejected - session: " + this.id + " destroyed");
                return;
            }
            Intent fillInIntent = createAuthFillInIntentLocked(requestId, extras);
            if (fillInIntent == null) {
                forceRemoveSelfLocked();
                return;
            }
            this.mService.setAuthenticationSelected(this.id, this.mClientState);
            this.mHandler.sendMessage(PooledLambda.obtainMessage($$Lambda$Session$LM4xf4dbxH_NTutQzBkaQNxKbV0.INSTANCE, this, Integer.valueOf(AutofillManager.makeAuthenticationId(requestId, datasetIndex)), intent, fillInIntent));
        }
    }

    public void onServiceDied(RemoteFillService service) {
        Slog.w(TAG, "removing session because service died");
        forceRemoveSelfLocked();
    }

    public void fill(int requestId, int datasetIndex, Dataset dataset) {
        synchronized (this.mLock) {
            if (this.mDestroyed) {
                Slog.w(TAG, "Call to Session#fill() rejected - session: " + this.id + " destroyed");
                return;
            }
            this.mHandler.sendMessage(PooledLambda.obtainMessage($$Lambda$knR7oLyPSG_CoFAxBA_nqSw3JBo.INSTANCE, this, Integer.valueOf(requestId), Integer.valueOf(datasetIndex), dataset, true));
        }
    }

    public void save() {
        synchronized (this.mLock) {
            if (this.mDestroyed) {
                Slog.w(TAG, "Call to Session#save() rejected - session: " + this.id + " destroyed");
                return;
            }
            this.mHandler.sendMessage(PooledLambda.obtainMessage($$Lambda$Z6KVL097A8ARGd4URYlOvvM48.INSTANCE, this.mService, this));
        }
    }

    public void cancelSave() {
        synchronized (this.mLock) {
            this.mIsSaving = false;
            if (this.mDestroyed) {
                Slog.w(TAG, "Call to Session#cancelSave() rejected - session: " + this.id + " destroyed");
                return;
            }
            this.mHandler.sendMessage(PooledLambda.obtainMessage($$Lambda$Session$cYu1t6lYVopApYWvct827slZk.INSTANCE, this));
        }
    }

    public void requestShowFillUi(AutofillId id2, int width, int height, IAutofillWindowPresenter presenter) {
        synchronized (this.mLock) {
            if (this.mDestroyed) {
                Slog.w(TAG, "Call to Session#requestShowFillUi() rejected - session: " + id2 + " destroyed");
            } else if (id2.equals(this.mCurrentViewId)) {
                try {
                    this.mClient.requestShowFillUi(this.id, id2, width, height, this.mViewStates.get(id2).getVirtualBounds(), presenter);
                } catch (RemoteException e) {
                    Slog.e(TAG, "Error requesting to show fill UI", e);
                }
            } else if (Helper.sDebug) {
                Slog.d(TAG, "Do not show full UI on " + id2 + " as it is not the current view (" + this.mCurrentViewId + ") anymore");
            }
        }
    }

    public void dispatchUnhandledKey(AutofillId id2, KeyEvent keyEvent) {
        synchronized (this.mLock) {
            if (this.mDestroyed) {
                Slog.w(TAG, "Call to Session#dispatchUnhandledKey() rejected - session: " + id2 + " destroyed");
            } else if (id2.equals(this.mCurrentViewId)) {
                try {
                    this.mClient.dispatchUnhandledKey(this.id, id2, keyEvent);
                } catch (RemoteException e) {
                    Slog.e(TAG, "Error requesting to dispatch unhandled key", e);
                }
            } else {
                Slog.w(TAG, "Do not dispatch unhandled key on " + id2 + " as it is not the current view (" + this.mCurrentViewId + ") anymore");
            }
        }
    }

    public void requestHideFillUi(AutofillId id2) {
        synchronized (this.mLock) {
            try {
                this.mClient.requestHideFillUi(this.id, id2);
            } catch (RemoteException e) {
                Slog.e(TAG, "Error requesting to hide fill UI", e);
            }
        }
    }

    public void startIntentSender(IntentSender intentSender) {
        synchronized (this.mLock) {
            if (this.mDestroyed) {
                Slog.w(TAG, "Call to Session#startIntentSender() rejected - session: " + this.id + " destroyed");
                return;
            }
            removeSelfLocked();
            this.mHandler.sendMessage(PooledLambda.obtainMessage($$Lambda$Session$dldcS_opIdRI25w0DM6rSIaHIoc.INSTANCE, this, intentSender));
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 3 */
    /* access modifiers changed from: private */
    public void doStartIntentSender(IntentSender intentSender) {
        try {
            synchronized (this.mLock) {
                this.mClient.startIntentSender(intentSender, (Intent) null);
            }
        } catch (RemoteException e) {
            Slog.e(TAG, "Error launching auth intent", e);
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public void setAuthenticationResultLocked(Bundle data, int authenticationId) {
        if (this.mDestroyed) {
            Slog.w(TAG, "Call to Session#setAuthenticationResultLocked() rejected - session: " + this.id + " destroyed");
        } else if (this.mResponses == null) {
            Slog.w(TAG, "setAuthenticationResultLocked(" + authenticationId + "): no responses");
            removeSelf();
        } else {
            int requestId = AutofillManager.getRequestIdFromAuthenticationId(authenticationId);
            FillResponse authenticatedResponse = this.mResponses.get(requestId);
            if (authenticatedResponse == null || data == null) {
                Slog.w(TAG, "no authenticated response");
                removeSelf();
                return;
            }
            int datasetIdx = AutofillManager.getDatasetIdFromAuthenticationId(authenticationId);
            if (datasetIdx == 65535 || ((Dataset) authenticatedResponse.getDatasets().get(datasetIdx)) != null) {
                Parcelable result = data.getParcelable("android.view.autofill.extra.AUTHENTICATION_RESULT");
                Bundle newClientState = data.getBundle("android.view.autofill.extra.CLIENT_STATE");
                if (Helper.sDebug) {
                    Slog.d(TAG, "setAuthenticationResultLocked(): result=" + result + ", clientState=" + newClientState + ", authenticationId=" + authenticationId);
                }
                if (result instanceof FillResponse) {
                    logAuthenticationStatusLocked(requestId, 912);
                    replaceResponseLocked(authenticatedResponse, (FillResponse) result, newClientState);
                } else if (!(result instanceof Dataset)) {
                    if (result != null) {
                        Slog.w(TAG, "service returned invalid auth type: " + result);
                    }
                    logAuthenticationStatusLocked(requestId, 1128);
                    processNullResponseLocked(requestId, 0);
                } else if (datasetIdx != 65535) {
                    logAuthenticationStatusLocked(requestId, 1126);
                    if (newClientState != null) {
                        if (Helper.sDebug) {
                            Slog.d(TAG, "Updating client state from auth dataset");
                        }
                        this.mClientState = newClientState;
                    }
                    Dataset dataset = (Dataset) result;
                    authenticatedResponse.getDatasets().set(datasetIdx, dataset);
                    autoFill(requestId, datasetIdx, dataset, false);
                } else {
                    Slog.w(TAG, "invalid index (" + datasetIdx + ") for authentication id " + authenticationId);
                    logAuthenticationStatusLocked(requestId, 1127);
                }
            } else {
                Slog.w(TAG, "no dataset with index " + datasetIdx + " on fill response");
                removeSelf();
            }
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public void setHasCallbackLocked(boolean hasIt) {
        if (this.mDestroyed) {
            Slog.w(TAG, "Call to Session#setHasCallbackLocked() rejected - session: " + this.id + " destroyed");
            return;
        }
        this.mHasCallback = hasIt;
    }

    @GuardedBy({"mLock"})
    private FillResponse getLastResponseLocked(String logPrefixFmt) {
        String logPrefix;
        if (!Helper.sDebug || logPrefixFmt == null) {
            logPrefix = null;
        } else {
            logPrefix = String.format(logPrefixFmt, new Object[]{Integer.valueOf(this.id)});
        }
        if (this.mContexts == null) {
            if (logPrefix != null) {
                Slog.d(TAG, logPrefix + ": no contexts");
            }
            return null;
        } else if (this.mResponses == null) {
            if (Helper.sVerbose && logPrefix != null) {
                Slog.v(TAG, logPrefix + ": no responses on session");
            }
            return null;
        } else {
            int lastResponseIdx = getLastResponseIndexLocked();
            if (lastResponseIdx < 0) {
                if (logPrefix != null) {
                    Slog.w(TAG, logPrefix + ": did not get last response. mResponses=" + this.mResponses + ", mViewStates=" + this.mViewStates);
                }
                return null;
            }
            FillResponse response = this.mResponses.valueAt(lastResponseIdx);
            if (Helper.sVerbose && logPrefix != null) {
                Slog.v(TAG, logPrefix + ": mResponses=" + this.mResponses + ", mContexts=" + this.mContexts + ", mViewStates=" + this.mViewStates);
            }
            return response;
        }
    }

    @GuardedBy({"mLock"})
    private SaveInfo getSaveInfoLocked() {
        FillResponse response = getLastResponseLocked((String) null);
        if (response == null) {
            return null;
        }
        return response.getSaveInfo();
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public int getSaveInfoFlagsLocked() {
        SaveInfo saveInfo = getSaveInfoLocked();
        if (saveInfo == null) {
            return 0;
        }
        return saveInfo.getFlags();
    }

    public void logContextCommitted() {
        this.mHandler.sendMessage(PooledLambda.obtainMessage($$Lambda$Session$v6ZVyksJuHdWgJ1F8aoa_1LJWPo.INSTANCE, this));
    }

    /* access modifiers changed from: private */
    public void handleLogContextCommitted() {
        FillResponse lastResponse;
        FieldClassificationUserData userData;
        synchronized (this.mLock) {
            lastResponse = getLastResponseLocked("logContextCommited(%s)");
        }
        if (lastResponse == null) {
            Slog.w(TAG, "handleLogContextCommitted(): last response is null");
            return;
        }
        UserData genericUserData = this.mService.getUserData();
        FieldClassificationUserData packageUserData = lastResponse.getUserData();
        if (packageUserData == null && genericUserData == null) {
            userData = null;
        } else if (packageUserData != null && genericUserData != null) {
            userData = new CompositeUserData(genericUserData, packageUserData);
        } else if (packageUserData != null) {
            userData = packageUserData;
        } else {
            userData = this.mService.getUserData();
        }
        FieldClassificationStrategy fcStrategy = this.mService.getFieldClassificationStrategy();
        if (userData == null || fcStrategy == null) {
            logContextCommitted((ArrayList<AutofillId>) null, (ArrayList<FieldClassification>) null);
        } else {
            logFieldClassificationScore(fcStrategy, userData);
        }
    }

    private void logContextCommitted(ArrayList<AutofillId> detectedFieldIds, ArrayList<FieldClassification> detectedFieldClassifications) {
        synchronized (this.mLock) {
            logContextCommittedLocked(detectedFieldIds, detectedFieldClassifications);
        }
    }

    @GuardedBy({"mLock"})
    private void logContextCommittedLocked(ArrayList<AutofillId> detectedFieldIds, ArrayList<FieldClassification> detectedFieldClassifications) {
        String str;
        int responseCount;
        boolean hasAtLeastOneDataset;
        AutofillId[] fieldClassificationIds;
        FillResponse lastResponse;
        String str2;
        int responseCount2;
        boolean hasAtLeastOneDataset2;
        AutofillValue currentValue;
        String str3;
        int responseCount3;
        AutofillValue currentValue2;
        String str4;
        ArraySet<String> ignoredDatasets;
        ArrayList<AutofillValue> values;
        AutofillValue currentValue3;
        ArrayMap<AutofillId, ArraySet<String>> manuallyFilledIds;
        ArrayList<String> changedDatasetIds;
        ArrayList<AutofillId> changedFieldIds;
        int flags;
        ArrayList<String> changedDatasetIds2;
        FillResponse lastResponse2 = getLastResponseLocked("logContextCommited(%s)");
        if (lastResponse2 != null) {
            int flags2 = lastResponse2.getFlags();
            if ((flags2 & 1) != 0) {
                ArraySet<String> ignoredDatasets2 = null;
                ArrayList<AutofillId> changedFieldIds2 = null;
                ArrayList<String> changedDatasetIds3 = null;
                ArrayMap<AutofillId, ArraySet<String>> manuallyFilledIds2 = null;
                boolean hasAtLeastOneDataset3 = false;
                int responseCount4 = this.mResponses.size();
                int i = 0;
                while (true) {
                    str = "logContextCommitted() skipping idless dataset ";
                    if (i >= responseCount4) {
                        break;
                    }
                    List<Dataset> datasets = this.mResponses.valueAt(i).getDatasets();
                    if (datasets == null) {
                        flags = flags2;
                        changedFieldIds = changedFieldIds2;
                        changedDatasetIds = changedDatasetIds3;
                    } else if (datasets.isEmpty()) {
                        flags = flags2;
                        changedFieldIds = changedFieldIds2;
                        changedDatasetIds = changedDatasetIds3;
                    } else {
                        int j = 0;
                        while (true) {
                            flags = flags2;
                            if (j >= datasets.size()) {
                                break;
                            }
                            Dataset dataset = datasets.get(j);
                            ArrayList<AutofillId> changedFieldIds3 = changedFieldIds2;
                            String datasetId = dataset.getId();
                            if (datasetId != null) {
                                changedDatasetIds2 = changedDatasetIds3;
                                ArrayList<String> arrayList = this.mSelectedDatasetIds;
                                if (arrayList == null || !arrayList.contains(datasetId)) {
                                    if (Helper.sVerbose) {
                                        Slog.v(TAG, "adding ignored dataset " + datasetId);
                                    }
                                    if (ignoredDatasets2 == null) {
                                        ignoredDatasets2 = new ArraySet<>();
                                    }
                                    ignoredDatasets2.add(datasetId);
                                    hasAtLeastOneDataset3 = true;
                                } else {
                                    hasAtLeastOneDataset3 = true;
                                }
                            } else if (Helper.sVerbose) {
                                changedDatasetIds2 = changedDatasetIds3;
                                Slog.v(TAG, str + dataset);
                            } else {
                                changedDatasetIds2 = changedDatasetIds3;
                            }
                            j++;
                            flags2 = flags;
                            changedFieldIds2 = changedFieldIds3;
                            changedDatasetIds3 = changedDatasetIds2;
                        }
                        changedFieldIds = changedFieldIds2;
                        changedDatasetIds = changedDatasetIds3;
                        i++;
                        flags2 = flags;
                        changedFieldIds2 = changedFieldIds;
                        changedDatasetIds3 = changedDatasetIds;
                    }
                    if (Helper.sVerbose != 0) {
                        Slog.v(TAG, "logContextCommitted() no datasets at " + i);
                    }
                    i++;
                    flags2 = flags;
                    changedFieldIds2 = changedFieldIds;
                    changedDatasetIds3 = changedDatasetIds;
                }
                ArrayList<AutofillId> changedFieldIds4 = changedFieldIds2;
                ArrayList<String> changedDatasetIds4 = changedDatasetIds3;
                AutofillId[] fieldClassificationIds2 = lastResponse2.getFieldClassificationIds();
                if (hasAtLeastOneDataset3 || fieldClassificationIds2 != null) {
                    int i2 = 0;
                    ArrayList<AutofillId> changedFieldIds5 = changedFieldIds4;
                    ArrayList<String> changedDatasetIds5 = changedDatasetIds4;
                    while (i2 < this.mViewStates.size()) {
                        ViewState viewState = this.mViewStates.valueAt(i2);
                        int state = viewState.getState();
                        if ((state & 8) != 0) {
                            lastResponse = lastResponse2;
                            if ((state & 2048) != 0) {
                                String datasetId2 = viewState.getDatasetId();
                                if (datasetId2 == null) {
                                    fieldClassificationIds = fieldClassificationIds2;
                                    StringBuilder sb = new StringBuilder();
                                    int i3 = state;
                                    sb.append("logContextCommitted(): no dataset id on ");
                                    sb.append(viewState);
                                    Slog.w(TAG, sb.toString());
                                    hasAtLeastOneDataset = hasAtLeastOneDataset3;
                                    responseCount = responseCount4;
                                    str2 = str;
                                } else {
                                    fieldClassificationIds = fieldClassificationIds2;
                                    int i4 = state;
                                    AutofillValue autofilledValue = viewState.getAutofilledValue();
                                    AutofillValue currentValue4 = viewState.getCurrentValue();
                                    if (autofilledValue == null || !autofilledValue.equals(currentValue4)) {
                                        AutofillValue autofillValue = currentValue4;
                                        if (Helper.sDebug) {
                                            Slog.d(TAG, "logContextCommitted() found changed state: " + viewState);
                                        }
                                        if (changedFieldIds5 == null) {
                                            changedFieldIds5 = new ArrayList<>();
                                            changedDatasetIds5 = new ArrayList<>();
                                        }
                                        changedFieldIds5.add(viewState.id);
                                        changedDatasetIds5.add(datasetId2);
                                        hasAtLeastOneDataset = hasAtLeastOneDataset3;
                                        responseCount = responseCount4;
                                        str2 = str;
                                    } else if (Helper.sDebug) {
                                        AutofillValue autofillValue2 = autofilledValue;
                                        StringBuilder sb2 = new StringBuilder();
                                        AutofillValue autofillValue3 = currentValue4;
                                        sb2.append("logContextCommitted(): ignoring changed ");
                                        sb2.append(viewState);
                                        sb2.append(" because it has same value that was autofilled");
                                        Slog.d(TAG, sb2.toString());
                                        hasAtLeastOneDataset = hasAtLeastOneDataset3;
                                        responseCount = responseCount4;
                                        str2 = str;
                                    } else {
                                        AutofillValue autofillValue4 = currentValue4;
                                        hasAtLeastOneDataset = hasAtLeastOneDataset3;
                                        responseCount = responseCount4;
                                        str2 = str;
                                    }
                                }
                            } else {
                                fieldClassificationIds = fieldClassificationIds2;
                                int i5 = state;
                                AutofillValue currentValue5 = viewState.getCurrentValue();
                                if (currentValue5 == null) {
                                    if (Helper.sDebug) {
                                        Slog.d(TAG, "logContextCommitted(): skipping view without current value ( " + viewState + ")");
                                        hasAtLeastOneDataset = hasAtLeastOneDataset3;
                                        responseCount = responseCount4;
                                        str2 = str;
                                    } else {
                                        hasAtLeastOneDataset = hasAtLeastOneDataset3;
                                        responseCount = responseCount4;
                                        str2 = str;
                                    }
                                } else if (hasAtLeastOneDataset3) {
                                    int j2 = 0;
                                    while (j2 < responseCount4) {
                                        ArraySet<String> ignoredDatasets3 = ignoredDatasets2;
                                        List<Dataset> datasets2 = this.mResponses.valueAt(j2).getDatasets();
                                        if (datasets2 == null) {
                                            currentValue = currentValue5;
                                            List<Dataset> list = datasets2;
                                            hasAtLeastOneDataset2 = hasAtLeastOneDataset3;
                                            responseCount2 = responseCount4;
                                            str3 = str;
                                        } else if (datasets2.isEmpty()) {
                                            currentValue = currentValue5;
                                            List<Dataset> list2 = datasets2;
                                            hasAtLeastOneDataset2 = hasAtLeastOneDataset3;
                                            responseCount2 = responseCount4;
                                            str3 = str;
                                        } else {
                                            ArrayMap<AutofillId, ArraySet<String>> manuallyFilledIds3 = manuallyFilledIds2;
                                            int k = 0;
                                            while (true) {
                                                hasAtLeastOneDataset2 = hasAtLeastOneDataset3;
                                                if (k >= datasets2.size()) {
                                                    break;
                                                }
                                                Dataset dataset2 = datasets2.get(k);
                                                List<Dataset> datasets3 = datasets2;
                                                String datasetId3 = dataset2.getId();
                                                if (datasetId3 == null) {
                                                    if (Helper.sVerbose) {
                                                        responseCount3 = responseCount4;
                                                        Slog.v(TAG, str + dataset2);
                                                    } else {
                                                        responseCount3 = responseCount4;
                                                    }
                                                    currentValue2 = currentValue5;
                                                    str4 = str;
                                                } else {
                                                    responseCount3 = responseCount4;
                                                    ArrayList<AutofillValue> values2 = dataset2.getFieldValues();
                                                    Dataset dataset3 = dataset2;
                                                    int l = 0;
                                                    while (true) {
                                                        str4 = str;
                                                        if (l >= values2.size()) {
                                                            break;
                                                        }
                                                        AutofillValue candidate = values2.get(l);
                                                        if (currentValue5.equals(candidate)) {
                                                            if (Helper.sDebug) {
                                                                currentValue3 = currentValue5;
                                                                StringBuilder sb3 = new StringBuilder();
                                                                values = values2;
                                                                sb3.append("field ");
                                                                sb3.append(viewState.id);
                                                                sb3.append(" was manually filled with value set by dataset ");
                                                                sb3.append(datasetId3);
                                                                Slog.d(TAG, sb3.toString());
                                                            } else {
                                                                currentValue3 = currentValue5;
                                                                values = values2;
                                                            }
                                                            if (manuallyFilledIds3 == null) {
                                                                manuallyFilledIds = new ArrayMap<>();
                                                            } else {
                                                                manuallyFilledIds = manuallyFilledIds3;
                                                            }
                                                            ArraySet<String> datasetIds = manuallyFilledIds.get(viewState.id);
                                                            if (datasetIds == null) {
                                                                ArraySet<String> arraySet = datasetIds;
                                                                AutofillValue autofillValue5 = candidate;
                                                                datasetIds = new ArraySet<>(1);
                                                                manuallyFilledIds.put(viewState.id, datasetIds);
                                                            } else {
                                                                ArraySet<String> arraySet2 = datasetIds;
                                                                AutofillValue autofillValue6 = candidate;
                                                            }
                                                            datasetIds.add(datasetId3);
                                                            manuallyFilledIds3 = manuallyFilledIds;
                                                        } else {
                                                            currentValue3 = currentValue5;
                                                            values = values2;
                                                            AutofillValue autofillValue7 = candidate;
                                                        }
                                                        l++;
                                                        str = str4;
                                                        currentValue5 = currentValue3;
                                                        values2 = values;
                                                    }
                                                    currentValue2 = currentValue5;
                                                    ArrayList<AutofillValue> arrayList2 = values2;
                                                    ArrayList<String> arrayList3 = this.mSelectedDatasetIds;
                                                    if (arrayList3 == null || !arrayList3.contains(datasetId3)) {
                                                        if (Helper.sVerbose) {
                                                            Slog.v(TAG, "adding ignored dataset " + datasetId3);
                                                        }
                                                        if (ignoredDatasets3 == null) {
                                                            ignoredDatasets = new ArraySet<>();
                                                        } else {
                                                            ignoredDatasets = ignoredDatasets3;
                                                        }
                                                        ignoredDatasets.add(datasetId3);
                                                        ignoredDatasets3 = ignoredDatasets;
                                                    }
                                                }
                                                k++;
                                                datasets2 = datasets3;
                                                str = str4;
                                                currentValue5 = currentValue2;
                                                hasAtLeastOneDataset3 = hasAtLeastOneDataset2;
                                                responseCount4 = responseCount3;
                                            }
                                            currentValue = currentValue5;
                                            List<Dataset> list3 = datasets2;
                                            responseCount2 = responseCount4;
                                            str3 = str;
                                            ignoredDatasets2 = ignoredDatasets3;
                                            manuallyFilledIds2 = manuallyFilledIds3;
                                            j2++;
                                            str = str3;
                                            currentValue5 = currentValue;
                                            hasAtLeastOneDataset3 = hasAtLeastOneDataset2;
                                            responseCount4 = responseCount2;
                                        }
                                        if (Helper.sVerbose) {
                                            Slog.v(TAG, "logContextCommitted() no datasets at " + j2);
                                        }
                                        ignoredDatasets2 = ignoredDatasets3;
                                        j2++;
                                        str = str3;
                                        currentValue5 = currentValue;
                                        hasAtLeastOneDataset3 = hasAtLeastOneDataset2;
                                        responseCount4 = responseCount2;
                                    }
                                    ArraySet<String> arraySet3 = ignoredDatasets2;
                                    hasAtLeastOneDataset = hasAtLeastOneDataset3;
                                    responseCount = responseCount4;
                                    str2 = str;
                                } else {
                                    hasAtLeastOneDataset = hasAtLeastOneDataset3;
                                    responseCount = responseCount4;
                                    str2 = str;
                                }
                            }
                        } else {
                            lastResponse = lastResponse2;
                            fieldClassificationIds = fieldClassificationIds2;
                            hasAtLeastOneDataset = hasAtLeastOneDataset3;
                            responseCount = responseCount4;
                            str2 = str;
                            int i6 = state;
                        }
                        i2++;
                        str = str2;
                        lastResponse2 = lastResponse;
                        fieldClassificationIds2 = fieldClassificationIds;
                        hasAtLeastOneDataset3 = hasAtLeastOneDataset;
                        responseCount4 = responseCount;
                    }
                    AutofillId[] autofillIdArr = fieldClassificationIds2;
                    boolean z = hasAtLeastOneDataset3;
                    int i7 = responseCount4;
                    ArrayList<AutofillId> manuallyFilledFieldIds = null;
                    ArrayList<ArrayList<String>> manuallyFilledDatasetIds = null;
                    if (manuallyFilledIds2 != null) {
                        int size = manuallyFilledIds2.size();
                        manuallyFilledFieldIds = new ArrayList<>(size);
                        manuallyFilledDatasetIds = new ArrayList<>(size);
                        for (int i8 = 0; i8 < size; i8++) {
                            manuallyFilledFieldIds.add(manuallyFilledIds2.keyAt(i8));
                            manuallyFilledDatasetIds.add(new ArrayList(manuallyFilledIds2.valueAt(i8)));
                        }
                    }
                    this.mService.logContextCommittedLocked(this.id, this.mClientState, this.mSelectedDatasetIds, ignoredDatasets2, changedFieldIds5, changedDatasetIds5, manuallyFilledFieldIds, manuallyFilledDatasetIds, detectedFieldIds, detectedFieldClassifications, this.mComponentName, this.mCompatMode);
                } else if (Helper.sVerbose) {
                    Slog.v(TAG, "logContextCommittedLocked(): skipped (no datasets nor fields classification ids)");
                }
            } else if (Helper.sVerbose) {
                Slog.v(TAG, "logContextCommittedLocked(): ignored by flags " + flags2);
            }
        }
    }

    /* JADX INFO: finally extract failed */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x009e, code lost:
        r0 = th;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void logFieldClassificationScore(com.android.server.autofill.FieldClassificationStrategy r26, android.service.autofill.FieldClassificationUserData r27) {
        /*
            r25 = this;
            r9 = r25
            java.lang.String[] r15 = r27.getValues()
            java.lang.String[] r14 = r27.getCategoryIds()
            java.lang.String r19 = r27.getFieldClassificationAlgorithm()
            android.os.Bundle r20 = r27.getDefaultFieldClassificationArgs()
            android.util.ArrayMap r21 = r27.getFieldClassificationAlgorithms()
            android.util.ArrayMap r22 = r27.getFieldClassificationArgs()
            if (r15 == 0) goto L_0x00a0
            if (r14 == 0) goto L_0x00a0
            int r0 = r15.length
            int r1 = r14.length
            if (r0 == r1) goto L_0x0026
            r4 = r14
            r5 = r15
            goto L_0x00a2
        L_0x0026:
            int r13 = android.service.autofill.UserData.getMaxFieldClassificationIdsSize()
            java.util.ArrayList r7 = new java.util.ArrayList
            r7.<init>(r13)
            java.util.ArrayList r8 = new java.util.ArrayList
            r8.<init>(r13)
            java.lang.Object r1 = r9.mLock
            monitor-enter(r1)
            android.util.ArrayMap<android.view.autofill.AutofillId, com.android.server.autofill.ViewState> r0 = r9.mViewStates     // Catch:{ all -> 0x0098 }
            java.util.Collection r0 = r0.values()     // Catch:{ all -> 0x0098 }
            monitor-exit(r1)     // Catch:{ all -> 0x0098 }
            int r12 = r0.size()
            android.view.autofill.AutofillId[] r10 = new android.view.autofill.AutofillId[r12]
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>(r12)
            r11 = r1
            r1 = 0
            java.util.Iterator r2 = r0.iterator()
            r23 = r1
        L_0x0051:
            boolean r1 = r2.hasNext()
            if (r1 == 0) goto L_0x006d
            java.lang.Object r1 = r2.next()
            com.android.server.autofill.ViewState r1 = (com.android.server.autofill.ViewState) r1
            android.view.autofill.AutofillValue r3 = r1.getCurrentValue()
            r11.add(r3)
            int r3 = r23 + 1
            android.view.autofill.AutofillId r4 = r1.id
            r10[r23] = r4
            r23 = r3
            goto L_0x0051
        L_0x006d:
            android.os.RemoteCallback r6 = new android.os.RemoteCallback
            com.android.server.autofill.-$$Lambda$Session$PBwPPZBgjCZzQ_ztfoUbwBZupu8 r5 = new com.android.server.autofill.-$$Lambda$Session$PBwPPZBgjCZzQ_ztfoUbwBZupu8
            r1 = r5
            r2 = r25
            r3 = r12
            r4 = r10
            r24 = r0
            r0 = r5
            r5 = r15
            r9 = r6
            r6 = r14
            r1.<init>(r3, r4, r5, r6, r7, r8)
            r9.<init>(r0)
            r0 = r11
            r11 = r9
            r1 = r10
            r10 = r26
            r2 = r12
            r12 = r0
            r3 = r13
            r13 = r15
            r4 = r14
            r15 = r19
            r16 = r20
            r17 = r21
            r18 = r22
            r10.calculateScores(r11, r12, r13, r14, r15, r16, r17, r18)
            return
        L_0x0098:
            r0 = move-exception
            r3 = r13
            r4 = r14
            r5 = r15
        L_0x009c:
            monitor-exit(r1)     // Catch:{ all -> 0x009e }
            throw r0
        L_0x009e:
            r0 = move-exception
            goto L_0x009c
        L_0x00a0:
            r4 = r14
            r5 = r15
        L_0x00a2:
            r0 = -1
            if (r5 != 0) goto L_0x00a7
            r1 = r0
            goto L_0x00a8
        L_0x00a7:
            int r1 = r5.length
        L_0x00a8:
            if (r4 != 0) goto L_0x00ab
            goto L_0x00ac
        L_0x00ab:
            int r0 = r4.length
        L_0x00ac:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "setScores(): user data mismatch: values.length = "
            r2.append(r3)
            r2.append(r1)
            java.lang.String r3 = ", ids.length = "
            r2.append(r3)
            r2.append(r0)
            java.lang.String r2 = r2.toString()
            java.lang.String r3 = "AutofillSession"
            android.util.Slog.w(r3, r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.autofill.Session.logFieldClassificationScore(com.android.server.autofill.FieldClassificationStrategy, android.service.autofill.FieldClassificationUserData):void");
    }

    public /* synthetic */ void lambda$logFieldClassificationScore$2$Session(int viewsSize, AutofillId[] autofillIds, String[] userValues, String[] categoryIds, ArrayList detectedFieldIds, ArrayList detectedFieldClassifications, Bundle result) {
        String[] strArr = userValues;
        ArrayList arrayList = detectedFieldIds;
        ArrayList arrayList2 = detectedFieldClassifications;
        Bundle bundle = result;
        if (bundle == null) {
            if (Helper.sDebug) {
                Slog.d(TAG, "setFieldClassificationScore(): no results");
            }
            logContextCommitted((ArrayList<AutofillId>) null, (ArrayList<FieldClassification>) null);
            return;
        }
        AutofillFieldClassificationService.Scores scores = bundle.getParcelable("scores");
        if (scores == null) {
            Slog.w(TAG, "No field classification score on " + bundle);
            return;
        }
        int j = 0;
        int i = 0;
        while (i < viewsSize) {
            try {
                AutofillId autofillId = autofillIds[i];
                ArrayMap<String, Float> scoresByField = null;
                int j2 = 0;
                while (j < strArr.length) {
                    String categoryId = categoryIds[j];
                    float score = scores.scores[i][j];
                    if (score > 0.0f) {
                        if (scoresByField == null) {
                            scoresByField = new ArrayMap<>(strArr.length);
                        }
                        Float currentScore = scoresByField.get(categoryId);
                        if (currentScore == null || currentScore.floatValue() <= score) {
                            if (Helper.sVerbose) {
                                Slog.v(TAG, "adding score " + score + " at index " + j + " and id " + autofillId);
                            }
                            scoresByField.put(categoryId, Float.valueOf(score));
                        } else if (Helper.sVerbose) {
                            Slog.v(TAG, "skipping score " + score + " because it's less than " + currentScore);
                        }
                    } else if (Helper.sVerbose) {
                        Slog.v(TAG, "skipping score 0 at index " + j + " and id " + autofillId);
                    }
                    j2 = j + 1;
                    strArr = userValues;
                    Bundle bundle2 = result;
                }
                if (scoresByField != null) {
                    ArrayList<FieldClassification.Match> matches = new ArrayList<>(scoresByField.size());
                    j = 0;
                    while (j < scoresByField.size()) {
                        matches.add(new FieldClassification.Match(scoresByField.keyAt(j), scoresByField.valueAt(j).floatValue()));
                        j++;
                    }
                    arrayList.add(autofillId);
                    arrayList2.add(new FieldClassification(matches));
                } else if (Helper.sVerbose) {
                    Slog.v(TAG, "no score for autofillId=" + autofillId);
                }
                i++;
                strArr = userValues;
                Bundle bundle3 = result;
            } catch (ArrayIndexOutOfBoundsException e) {
                wtf(e, "Error accessing FC score at [%d, %d] (%s): %s", Integer.valueOf(i), Integer.valueOf(j), scores, e);
                return;
            }
        }
        logContextCommitted(arrayList, arrayList2);
    }

    /* JADX INFO: finally extract failed */
    /* JADX WARNING: Code restructure failed: missing block: B:207:0x05e0, code lost:
        r0 = th;
     */
    @com.android.internal.annotations.GuardedBy({"mLock"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean showSaveLocked() {
        /*
            r30 = this;
            r12 = r30
            boolean r0 = r12.mDestroyed
            r13 = 0
            if (r0 == 0) goto L_0x0025
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "Call to Session#showSaveLocked() rejected - session: "
            r0.append(r1)
            int r1 = r12.id
            r0.append(r1)
            java.lang.String r1 = " destroyed"
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "AutofillSession"
            android.util.Slog.w(r1, r0)
            return r13
        L_0x0025:
            java.lang.String r0 = "showSaveLocked(%s)"
            android.service.autofill.FillResponse r14 = r12.getLastResponseLocked(r0)
            if (r14 != 0) goto L_0x0030
            r1 = 0
            goto L_0x0034
        L_0x0030:
            android.service.autofill.SaveInfo r1 = r14.getSaveInfo()
        L_0x0034:
            r15 = r1
            r11 = 1
            if (r15 != 0) goto L_0x005b
            boolean r0 = com.android.server.autofill.Helper.sVerbose
            if (r0 == 0) goto L_0x005a
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "showSaveLocked("
            r0.append(r1)
            int r1 = r12.id
            r0.append(r1)
            java.lang.String r1 = "): no saveInfo from service"
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "AutofillSession"
            android.util.Slog.v(r1, r0)
        L_0x005a:
            return r11
        L_0x005b:
            int r1 = r15.getFlags()
            r1 = r1 & 4
            if (r1 == 0) goto L_0x0086
            boolean r0 = com.android.server.autofill.Helper.sDebug
            if (r0 == 0) goto L_0x0085
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "showSaveLocked("
            r0.append(r1)
            int r1 = r12.id
            r0.append(r1)
            java.lang.String r1 = "): service asked to delay save"
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "AutofillSession"
            android.util.Slog.v(r1, r0)
        L_0x0085:
            return r13
        L_0x0086:
            android.util.ArrayMap r10 = r12.createSanitizers(r15)
            android.util.ArrayMap r1 = new android.util.ArrayMap
            r1.<init>()
            r9 = r1
            android.util.ArraySet r1 = new android.util.ArraySet
            r1.<init>()
            r8 = r1
            android.view.autofill.AutofillId[] r7 = r15.getRequiredIds()
            r1 = 1
            r2 = 0
            r3 = 0
            if (r7 == 0) goto L_0x0221
            r4 = 0
        L_0x00a0:
            int r5 = r7.length
            if (r4 >= r5) goto L_0x021a
            r5 = r7[r4]
            if (r5 != 0) goto L_0x00c8
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r0 = "null autofill id on "
            r6.append(r0)
            java.lang.String r0 = java.util.Arrays.toString(r7)
            r6.append(r0)
            java.lang.String r0 = r6.toString()
            java.lang.String r6 = "AutofillSession"
            android.util.Slog.w(r6, r0)
            r18 = r1
            r20 = r2
            goto L_0x01eb
        L_0x00c8:
            r8.add(r5)
            android.util.ArrayMap<android.view.autofill.AutofillId, com.android.server.autofill.ViewState> r0 = r12.mViewStates
            java.lang.Object r0 = r0.get(r5)
            com.android.server.autofill.ViewState r0 = (com.android.server.autofill.ViewState) r0
            if (r0 != 0) goto L_0x00f0
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r13 = "showSaveLocked(): no ViewState for required "
            r6.append(r13)
            r6.append(r5)
            java.lang.String r6 = r6.toString()
            java.lang.String r13 = "AutofillSession"
            android.util.Slog.w(r13, r6)
            r1 = 0
            r13 = r1
            goto L_0x0225
        L_0x00f0:
            android.view.autofill.AutofillValue r6 = r0.getCurrentValue()
            if (r6 == 0) goto L_0x0100
            boolean r13 = r6.isEmpty()
            if (r13 == 0) goto L_0x00fd
            goto L_0x0100
        L_0x00fd:
            r18 = r1
            goto L_0x0133
        L_0x0100:
            android.view.autofill.AutofillValue r13 = r12.getValueFromContextsLocked(r5)
            if (r13 == 0) goto L_0x01f5
            boolean r17 = com.android.server.autofill.Helper.sDebug
            if (r17 == 0) goto L_0x0130
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r18 = r1
            java.lang.String r1 = "Value of required field "
            r11.append(r1)
            r11.append(r5)
            java.lang.String r1 = " didn't change; using initial value ("
            r11.append(r1)
            r11.append(r13)
            java.lang.String r1 = ") instead"
            r11.append(r1)
            java.lang.String r1 = r11.toString()
            java.lang.String r11 = "AutofillSession"
            android.util.Slog.d(r11, r1)
            goto L_0x0132
        L_0x0130:
            r18 = r1
        L_0x0132:
            r6 = r13
        L_0x0133:
            android.view.autofill.AutofillValue r1 = r12.getSanitizedValue(r10, r5, r6)
            if (r1 != 0) goto L_0x015d
            boolean r6 = com.android.server.autofill.Helper.sDebug
            if (r6 == 0) goto L_0x0159
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r11 = "value of required field "
            r6.append(r11)
            r6.append(r5)
            java.lang.String r11 = " failed sanitization"
            r6.append(r11)
            java.lang.String r6 = r6.toString()
            java.lang.String r11 = "AutofillSession"
            android.util.Slog.d(r11, r6)
        L_0x0159:
            r6 = 0
            r13 = r6
            goto L_0x0225
        L_0x015d:
            r0.setSanitizedValue(r1)
            r9.put(r5, r1)
            android.view.autofill.AutofillValue r6 = r0.getAutofilledValue()
            boolean r11 = r1.equals(r6)
            if (r11 != 0) goto L_0x01e7
            r11 = 1
            if (r6 != 0) goto L_0x01b0
            android.view.autofill.AutofillValue r13 = r12.getValueFromContextsLocked(r5)
            if (r13 == 0) goto L_0x01ab
            boolean r19 = r13.equals(r1)
            if (r19 == 0) goto L_0x01ab
            boolean r19 = com.android.server.autofill.Helper.sDebug
            if (r19 == 0) goto L_0x01a4
            r19 = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r20 = r2
            java.lang.String r2 = "id "
            r0.append(r2)
            r0.append(r5)
            java.lang.String r2 = " is part of dataset but initial value didn't change: "
            r0.append(r2)
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.String r2 = "AutofillSession"
            android.util.Slog.d(r2, r0)
            goto L_0x01a8
        L_0x01a4:
            r19 = r0
            r20 = r2
        L_0x01a8:
            r0 = 0
            r11 = r0
            goto L_0x01af
        L_0x01ab:
            r19 = r0
            r20 = r2
        L_0x01af:
            goto L_0x01b5
        L_0x01b0:
            r19 = r0
            r20 = r2
            r3 = 1
        L_0x01b5:
            if (r11 == 0) goto L_0x01e4
            boolean r0 = com.android.server.autofill.Helper.sDebug
            if (r0 == 0) goto L_0x01e1
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "found a change on required "
            r0.append(r2)
            r0.append(r5)
            java.lang.String r2 = ": "
            r0.append(r2)
            r0.append(r6)
            java.lang.String r2 = " => "
            r0.append(r2)
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.String r2 = "AutofillSession"
            android.util.Slog.d(r2, r0)
        L_0x01e1:
            r0 = 1
            r2 = r0
            goto L_0x01ed
        L_0x01e4:
            r2 = r20
            goto L_0x01ed
        L_0x01e7:
            r19 = r0
            r20 = r2
        L_0x01eb:
            r2 = r20
        L_0x01ed:
            int r4 = r4 + 1
            r1 = r18
            r11 = 1
            r13 = 0
            goto L_0x00a0
        L_0x01f5:
            r19 = r0
            r18 = r1
            r20 = r2
            boolean r0 = com.android.server.autofill.Helper.sDebug
            if (r0 == 0) goto L_0x0215
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "empty value for required "
            r0.append(r1)
            r0.append(r5)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "AutofillSession"
            android.util.Slog.d(r1, r0)
        L_0x0215:
            r1 = 0
            r13 = r1
            r2 = r20
            goto L_0x0225
        L_0x021a:
            r18 = r1
            r20 = r2
            r13 = r18
            goto L_0x0225
        L_0x0221:
            r18 = r1
            r13 = r18
        L_0x0225:
            android.view.autofill.AutofillId[] r11 = r15.getOptionalIds()
            boolean r0 = com.android.server.autofill.Helper.sVerbose
            if (r0 == 0) goto L_0x0250
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "allRequiredAreNotEmpty: "
            r0.append(r1)
            r0.append(r13)
            java.lang.String r1 = " hasOptional: "
            r0.append(r1)
            if (r11 == 0) goto L_0x0243
            r1 = 1
            goto L_0x0244
        L_0x0243:
            r1 = 0
        L_0x0244:
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "AutofillSession"
            android.util.Slog.v(r1, r0)
        L_0x0250:
            if (r13 == 0) goto L_0x05f6
            if (r11 == 0) goto L_0x0356
            if (r2 == 0) goto L_0x0258
            if (r3 != 0) goto L_0x0356
        L_0x0258:
            r0 = 0
        L_0x0259:
            int r1 = r11.length
            if (r0 >= r1) goto L_0x0351
            r1 = r11[r0]
            r8.add(r1)
            android.util.ArrayMap<android.view.autofill.AutofillId, com.android.server.autofill.ViewState> r4 = r12.mViewStates
            java.lang.Object r4 = r4.get(r1)
            com.android.server.autofill.ViewState r4 = (com.android.server.autofill.ViewState) r4
            if (r4 != 0) goto L_0x0288
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "no ViewState for optional "
            r5.append(r6)
            r5.append(r1)
            java.lang.String r5 = r5.toString()
            java.lang.String r6 = "AutofillSession"
            android.util.Slog.w(r6, r5)
            r18 = r2
            r19 = r3
            goto L_0x0349
        L_0x0288:
            int r5 = r4.getState()
            r5 = r5 & 8
            if (r5 == 0) goto L_0x0317
            android.view.autofill.AutofillValue r5 = r4.getCurrentValue()
            android.view.autofill.AutofillValue r6 = r12.getSanitizedValue(r10, r1, r5)
            if (r6 != 0) goto L_0x02c6
            boolean r18 = com.android.server.autofill.Helper.sDebug
            if (r18 == 0) goto L_0x02c0
            r18 = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r19 = r3
            java.lang.String r3 = "value of opt. field "
            r2.append(r3)
            r2.append(r1)
            java.lang.String r3 = " failed sanitization"
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            java.lang.String r3 = "AutofillSession"
            android.util.Slog.d(r3, r2)
            goto L_0x0349
        L_0x02c0:
            r18 = r2
            r19 = r3
            goto L_0x0349
        L_0x02c6:
            r18 = r2
            r19 = r3
            r9.put(r1, r6)
            android.view.autofill.AutofillValue r2 = r4.getAutofilledValue()
            boolean r3 = r6.equals(r2)
            if (r3 != 0) goto L_0x0310
            boolean r3 = com.android.server.autofill.Helper.sDebug
            if (r3 == 0) goto L_0x0304
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r20 = r4
            java.lang.String r4 = "found a change on optional "
            r3.append(r4)
            r3.append(r1)
            java.lang.String r4 = ": "
            r3.append(r4)
            r3.append(r2)
            java.lang.String r4 = " => "
            r3.append(r4)
            r3.append(r6)
            java.lang.String r3 = r3.toString()
            java.lang.String r4 = "AutofillSession"
            android.util.Slog.d(r4, r3)
            goto L_0x0306
        L_0x0304:
            r20 = r4
        L_0x0306:
            if (r2 == 0) goto L_0x030a
            r3 = 1
            goto L_0x030c
        L_0x030a:
            r3 = r19
        L_0x030c:
            r4 = 1
            r18 = r4
            goto L_0x0314
        L_0x0310:
            r20 = r4
            r3 = r19
        L_0x0314:
            r2 = r18
            goto L_0x034d
        L_0x0317:
            r18 = r2
            r19 = r3
            r20 = r4
            android.view.autofill.AutofillValue r2 = r12.getValueFromContextsLocked(r1)
            boolean r3 = com.android.server.autofill.Helper.sDebug
            if (r3 == 0) goto L_0x0344
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "no current value for "
            r3.append(r4)
            r3.append(r1)
            java.lang.String r4 = "; initial value is "
            r3.append(r4)
            r3.append(r2)
            java.lang.String r3 = r3.toString()
            java.lang.String r4 = "AutofillSession"
            android.util.Slog.d(r4, r3)
        L_0x0344:
            if (r2 == 0) goto L_0x0349
            r9.put(r1, r2)
        L_0x0349:
            r2 = r18
            r3 = r19
        L_0x034d:
            int r0 = r0 + 1
            goto L_0x0259
        L_0x0351:
            r18 = r2
            r19 = r3
            goto L_0x035a
        L_0x0356:
            r18 = r2
            r19 = r3
        L_0x035a:
            if (r18 == 0) goto L_0x05e2
            boolean r0 = com.android.server.autofill.Helper.sDebug
            if (r0 == 0) goto L_0x0367
            java.lang.String r0 = "AutofillSession"
            java.lang.String r1 = "at least one field changed, validate fields for save UI"
            android.util.Slog.d(r0, r1)
        L_0x0367:
            android.service.autofill.InternalValidator r6 = r15.getValidator()
            if (r6 == 0) goto L_0x03c3
            r0 = 1133(0x46d, float:1.588E-42)
            android.metrics.LogMaker r1 = r12.newLogMaker(r0)
            boolean r0 = r6.isValid(r12)     // Catch:{ Exception -> 0x03af }
            boolean r2 = com.android.server.autofill.Helper.sDebug     // Catch:{ Exception -> 0x03af }
            if (r2 == 0) goto L_0x0394
            java.lang.String r2 = "AutofillSession"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x03af }
            r3.<init>()     // Catch:{ Exception -> 0x03af }
            r3.append(r6)     // Catch:{ Exception -> 0x03af }
            java.lang.String r4 = " returned "
            r3.append(r4)     // Catch:{ Exception -> 0x03af }
            r3.append(r0)     // Catch:{ Exception -> 0x03af }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x03af }
            android.util.Slog.d(r2, r3)     // Catch:{ Exception -> 0x03af }
        L_0x0394:
            if (r0 == 0) goto L_0x0399
            r2 = 10
            goto L_0x039a
        L_0x0399:
            r2 = 5
        L_0x039a:
            r1.setType(r2)     // Catch:{ Exception -> 0x03af }
            com.android.internal.logging.MetricsLogger r2 = r12.mMetricsLogger
            r2.write(r1)
            if (r0 != 0) goto L_0x03c3
            java.lang.String r2 = "AutofillSession"
            java.lang.String r3 = "not showing save UI because fields failed validation"
            android.util.Slog.i(r2, r3)
            r2 = 1
            return r2
        L_0x03af:
            r0 = move-exception
            java.lang.String r2 = "AutofillSession"
            java.lang.String r3 = "Not showing save UI because validation failed:"
            android.util.Slog.e(r2, r3, r0)
            r2 = 11
            r1.setType(r2)
            com.android.internal.logging.MetricsLogger r2 = r12.mMetricsLogger
            r2.write(r1)
            r2 = 1
            return r2
        L_0x03c3:
            java.util.List r5 = r14.getDatasets()
            if (r5 == 0) goto L_0x04f0
            r0 = 0
        L_0x03ca:
            int r1 = r5.size()
            if (r0 >= r1) goto L_0x04e7
            java.lang.Object r1 = r5.get(r0)
            android.service.autofill.Dataset r1 = (android.service.autofill.Dataset) r1
            android.util.ArrayMap r2 = com.android.server.autofill.Helper.getFields(r1)
            boolean r3 = com.android.server.autofill.Helper.sVerbose
            if (r3 == 0) goto L_0x0405
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Checking if saved fields match contents of dataset #"
            r3.append(r4)
            r3.append(r0)
            java.lang.String r4 = ": "
            r3.append(r4)
            r3.append(r1)
            java.lang.String r4 = "; savableIds="
            r3.append(r4)
            r3.append(r8)
            java.lang.String r3 = r3.toString()
            java.lang.String r4 = "AutofillSession"
            android.util.Slog.v(r4, r3)
        L_0x0405:
            r3 = 0
        L_0x0406:
            int r4 = r8.size()
            if (r3 >= r4) goto L_0x04b9
            java.lang.Object r4 = r8.valueAt(r3)
            android.view.autofill.AutofillId r4 = (android.view.autofill.AutofillId) r4
            java.lang.Object r20 = r9.get(r4)
            r21 = r5
            r5 = r20
            android.view.autofill.AutofillValue r5 = (android.view.autofill.AutofillValue) r5
            if (r5 != 0) goto L_0x0447
            boolean r20 = com.android.server.autofill.Helper.sDebug
            if (r20 == 0) goto L_0x0440
            r20 = r6
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r22 = r7
            java.lang.String r7 = "dataset has value for field that is null: "
            r6.append(r7)
            r6.append(r4)
            java.lang.String r6 = r6.toString()
            java.lang.String r7 = "AutofillSession"
            android.util.Slog.d(r7, r6)
            r23 = r2
            goto L_0x04ad
        L_0x0440:
            r20 = r6
            r22 = r7
            r23 = r2
            goto L_0x04ad
        L_0x0447:
            r20 = r6
            r22 = r7
            java.lang.Object r6 = r2.get(r4)
            android.view.autofill.AutofillValue r6 = (android.view.autofill.AutofillValue) r6
            boolean r7 = r5.equals(r6)
            if (r7 != 0) goto L_0x0490
            boolean r7 = com.android.server.autofill.Helper.sDebug
            if (r7 == 0) goto L_0x0484
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r23 = r2
            java.lang.String r2 = "found a dataset change on id "
            r7.append(r2)
            r7.append(r4)
            java.lang.String r2 = ": from "
            r7.append(r2)
            r7.append(r6)
            java.lang.String r2 = " to "
            r7.append(r2)
            r7.append(r5)
            java.lang.String r2 = r7.toString()
            java.lang.String r7 = "AutofillSession"
            android.util.Slog.d(r7, r2)
            goto L_0x0486
        L_0x0484:
            r23 = r2
        L_0x0486:
            int r0 = r0 + 1
            r6 = r20
            r5 = r21
            r7 = r22
            goto L_0x03ca
        L_0x0490:
            r23 = r2
            boolean r2 = com.android.server.autofill.Helper.sVerbose
            if (r2 == 0) goto L_0x04ad
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r7 = "no dataset changes for id "
            r2.append(r7)
            r2.append(r4)
            java.lang.String r2 = r2.toString()
            java.lang.String r7 = "AutofillSession"
            android.util.Slog.v(r7, r2)
        L_0x04ad:
            int r3 = r3 + 1
            r6 = r20
            r5 = r21
            r7 = r22
            r2 = r23
            goto L_0x0406
        L_0x04b9:
            r23 = r2
            r21 = r5
            r20 = r6
            r22 = r7
            boolean r2 = com.android.server.autofill.Helper.sDebug
            if (r2 == 0) goto L_0x04e4
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "ignoring Save UI because all fields match contents of dataset #"
            r2.append(r3)
            r2.append(r0)
            java.lang.String r3 = ": "
            r2.append(r3)
            r2.append(r1)
            java.lang.String r2 = r2.toString()
            java.lang.String r3 = "AutofillSession"
            android.util.Slog.d(r3, r2)
        L_0x04e4:
            r17 = 1
            return r17
        L_0x04e7:
            r21 = r5
            r20 = r6
            r22 = r7
            r17 = 1
            goto L_0x04f8
        L_0x04f0:
            r21 = r5
            r20 = r6
            r22 = r7
            r17 = 1
        L_0x04f8:
            boolean r0 = com.android.server.autofill.Helper.sDebug
            if (r0 == 0) goto L_0x0519
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "Good news, everyone! All checks passed, show save UI for "
            r0.append(r1)
            int r1 = r12.id
            r0.append(r1)
            java.lang.String r1 = "!"
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "AutofillSession"
            android.util.Slog.d(r1, r0)
        L_0x0519:
            android.os.Handler r0 = r12.mHandler
            com.android.server.autofill.-$$Lambda$Session$NtvZwhlT1c4eLjg2qI6EER2oCtY r1 = com.android.server.autofill.$$Lambda$Session$NtvZwhlT1c4eLjg2qI6EER2oCtY.INSTANCE
            android.os.Message r1 = com.android.internal.util.function.pooled.PooledLambda.obtainMessage(r1, r12)
            r0.sendMessage(r1)
            android.view.autofill.IAutoFillManagerClient r7 = r30.getClient()
            com.android.server.autofill.ui.PendingUi r0 = new com.android.server.autofill.ui.PendingUi
            android.os.IBinder r1 = r12.mActivityToken
            int r2 = r12.id
            r0.<init>(r1, r2, r7)
            r12.mPendingSaveUi = r0
            java.lang.Object r1 = r12.mLock
            monitor-enter(r1)
            com.android.server.autofill.AutofillManagerServiceImpl r0 = r12.mService     // Catch:{ all -> 0x05d0 }
            java.lang.CharSequence r0 = r0.getServiceLabelLocked()     // Catch:{ all -> 0x05d0 }
            r23 = r0
            com.android.server.autofill.AutofillManagerServiceImpl r0 = r12.mService     // Catch:{ all -> 0x05d0 }
            android.graphics.drawable.Drawable r0 = r0.getServiceIconLocked()     // Catch:{ all -> 0x05d0 }
            r24 = r0
            monitor-exit(r1)     // Catch:{ all -> 0x05d0 }
            if (r23 == 0) goto L_0x05b6
            if (r24 != 0) goto L_0x055d
            r27 = r8
            r28 = r9
            r25 = r10
            r26 = r14
            r29 = r15
            r15 = r17
            r1 = 0
            r14 = r7
            r17 = r11
            goto L_0x05c6
        L_0x055d:
            com.android.server.autofill.ui.AutoFillUI r1 = r30.getUiForShowing()
            com.android.server.autofill.AutofillManagerServiceImpl r0 = r12.mService
            java.lang.String r4 = r0.getServicePackageName()
            android.content.ComponentName r0 = r12.mComponentName
            com.android.server.autofill.ui.PendingUi r6 = r12.mPendingSaveUi
            boolean r5 = r12.mCompatMode
            r2 = r23
            r3 = r24
            r16 = r5
            r5 = r15
            r25 = r6
            r6 = r30
            r26 = r14
            r14 = r7
            r7 = r0
            r27 = r8
            r8 = r30
            r28 = r9
            r9 = r25
            r25 = r10
            r10 = r19
            r29 = r15
            r15 = r17
            r17 = r11
            r11 = r16
            r1.showSaveUi(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            if (r14 == 0) goto L_0x05b2
            int r0 = r12.id     // Catch:{ RemoteException -> 0x059b }
            r14.setSaveUiState(r0, r15)     // Catch:{ RemoteException -> 0x059b }
            goto L_0x05b2
        L_0x059b:
            r0 = move-exception
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Error notifying client to set save UI state to shown: "
            r1.append(r2)
            r1.append(r0)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "AutofillSession"
            android.util.Slog.e(r2, r1)
        L_0x05b2:
            r12.mIsSaving = r15
            r1 = 0
            return r1
        L_0x05b6:
            r27 = r8
            r28 = r9
            r25 = r10
            r26 = r14
            r29 = r15
            r15 = r17
            r1 = 0
            r14 = r7
            r17 = r11
        L_0x05c6:
            java.lang.Object[] r0 = new java.lang.Object[r1]
            java.lang.String r1 = "showSaveLocked(): no service label or icon"
            r2 = 0
            r12.wtf(r2, r1, r0)
            return r15
        L_0x05d0:
            r0 = move-exception
            r27 = r8
            r28 = r9
            r25 = r10
            r17 = r11
            r26 = r14
            r29 = r15
            r14 = r7
        L_0x05de:
            monitor-exit(r1)     // Catch:{ all -> 0x05e0 }
            throw r0
        L_0x05e0:
            r0 = move-exception
            goto L_0x05de
        L_0x05e2:
            r22 = r7
            r27 = r8
            r28 = r9
            r25 = r10
            r17 = r11
            r26 = r14
            r29 = r15
            r15 = 1
            r2 = r18
            r3 = r19
            goto L_0x0605
        L_0x05f6:
            r22 = r7
            r27 = r8
            r28 = r9
            r25 = r10
            r17 = r11
            r26 = r14
            r29 = r15
            r15 = 1
        L_0x0605:
            boolean r0 = com.android.server.autofill.Helper.sDebug
            if (r0 == 0) goto L_0x0632
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "showSaveLocked("
            r0.append(r1)
            int r1 = r12.id
            r0.append(r1)
            java.lang.String r1 = "): with no changes, comes no responsibilities.allRequiredAreNotNull="
            r0.append(r1)
            r0.append(r13)
            java.lang.String r1 = ", atLeastOneChanged="
            r0.append(r1)
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "AutofillSession"
            android.util.Slog.d(r1, r0)
        L_0x0632:
            return r15
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.autofill.Session.showSaveLocked():boolean");
    }

    /* access modifiers changed from: private */
    public void logSaveShown() {
        this.mService.logSaveShown(this.id, this.mClientState);
    }

    private ArrayMap<AutofillId, InternalSanitizer> createSanitizers(SaveInfo saveInfo) {
        InternalSanitizer[] sanitizerKeys;
        if (saveInfo == null || (sanitizerKeys = saveInfo.getSanitizerKeys()) == null) {
            return null;
        }
        int size = sanitizerKeys.length;
        ArrayMap<AutofillId, InternalSanitizer> sanitizers = new ArrayMap<>(size);
        if (Helper.sDebug) {
            Slog.d(TAG, "Service provided " + size + " sanitizers");
        }
        AutofillId[][] sanitizerValues = saveInfo.getSanitizerValues();
        for (int i = 0; i < size; i++) {
            InternalSanitizer sanitizer = sanitizerKeys[i];
            AutofillId[] ids = sanitizerValues[i];
            if (Helper.sDebug) {
                Slog.d(TAG, "sanitizer #" + i + " (" + sanitizer + ") for ids " + Arrays.toString(ids));
            }
            for (AutofillId id2 : ids) {
                sanitizers.put(id2, sanitizer);
            }
        }
        return sanitizers;
    }

    private AutofillValue getSanitizedValue(ArrayMap<AutofillId, InternalSanitizer> sanitizers, AutofillId id2, AutofillValue value) {
        if (sanitizers == null || value == null) {
            return value;
        }
        ViewState state = this.mViewStates.get(id2);
        AutofillValue sanitized = state == null ? null : state.getSanitizedValue();
        if (sanitized == null) {
            InternalSanitizer sanitizer = sanitizers.get(id2);
            if (sanitizer == null) {
                return value;
            }
            sanitized = sanitizer.sanitize(value);
            if (Helper.sDebug) {
                Slog.d(TAG, "Value for " + id2 + "(" + value + ") sanitized to " + sanitized);
            }
            if (state != null) {
                state.setSanitizedValue(sanitized);
            }
        }
        return sanitized;
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public boolean isSavingLocked() {
        return this.mIsSaving;
    }

    @GuardedBy({"mLock"})
    private AutofillValue getValueFromContextsLocked(AutofillId autofillId) {
        for (int i = this.mContexts.size() - 1; i >= 0; i--) {
            AssistStructure.ViewNode node = Helper.findViewNodeByAutofillId(this.mContexts.get(i).getStructure(), autofillId);
            if (node != null) {
                AutofillValue value = node.getAutofillValue();
                if (Helper.sDebug) {
                    Slog.d(TAG, "getValueFromContexts(" + this.id + SliceClientPermissions.SliceAuthority.DELIMITER + autofillId + ") at " + i + ": " + value);
                }
                if (value != null && !value.isEmpty()) {
                    return value;
                }
            }
        }
        return null;
    }

    @GuardedBy({"mLock"})
    private CharSequence[] getAutofillOptionsFromContextsLocked(AutofillId id2) {
        for (int i = this.mContexts.size() - 1; i >= 0; i--) {
            AssistStructure.ViewNode node = Helper.findViewNodeByAutofillId(this.mContexts.get(i).getStructure(), id2);
            if (node != null && node.getAutofillOptions() != null) {
                return node.getAutofillOptions();
            }
        }
        return null;
    }

    private void updateValuesForSaveLocked() {
        ArrayMap<AutofillId, InternalSanitizer> sanitizers = createSanitizers(getSaveInfoLocked());
        int numContexts = this.mContexts.size();
        for (int contextNum = 0; contextNum < numContexts; contextNum++) {
            FillContext context = this.mContexts.get(contextNum);
            AssistStructure.ViewNode[] nodes = context.findViewNodesByAutofillIds(getIdsOfAllViewStatesLocked());
            if (Helper.sVerbose) {
                Slog.v(TAG, "updateValuesForSaveLocked(): updating " + context);
            }
            for (int viewStateNum = 0; viewStateNum < this.mViewStates.size(); viewStateNum++) {
                ViewState viewState = this.mViewStates.valueAt(viewStateNum);
                AutofillId id2 = viewState.id;
                AutofillValue value = viewState.getCurrentValue();
                if (value != null) {
                    AssistStructure.ViewNode node = nodes[viewStateNum];
                    if (node == null) {
                        Slog.w(TAG, "callSaveLocked(): did not find node with id " + id2);
                    } else {
                        if (Helper.sVerbose) {
                            Slog.v(TAG, "updateValuesForSaveLocked(): updating " + id2 + " to " + value);
                        }
                        AutofillValue sanitizedValue = viewState.getSanitizedValue();
                        if (sanitizedValue == null) {
                            sanitizedValue = getSanitizedValue(sanitizers, id2, value);
                        }
                        if (sanitizedValue != null) {
                            node.updateAutofillValue(sanitizedValue);
                        } else if (Helper.sDebug) {
                            Slog.d(TAG, "updateValuesForSaveLocked(): not updating field " + id2 + " because it failed sanitization");
                        }
                    }
                } else if (Helper.sVerbose) {
                    Slog.v(TAG, "updateValuesForSaveLocked(): skipping " + id2);
                }
            }
            context.getStructure().sanitizeForParceling(false);
            if (Helper.sVerbose) {
                Slog.v(TAG, "updateValuesForSaveLocked(): dumping structure of " + context + " before calling service.save()");
                context.getStructure().dump(false);
            }
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public void callSaveLocked() {
        if (this.mDestroyed) {
            Slog.w(TAG, "Call to Session#callSaveLocked() rejected - session: " + this.id + " destroyed");
        } else if (this.mRemoteFillService == null) {
            wtf((Exception) null, "callSaveLocked() called without a remote service. mForAugmentedAutofillOnly: %s", Boolean.valueOf(this.mForAugmentedAutofillOnly));
        } else {
            if (Helper.sVerbose) {
                Slog.v(TAG, "callSaveLocked(" + this.id + "): mViewStates=" + this.mViewStates);
            }
            if (this.mContexts == null) {
                Slog.w(TAG, "callSaveLocked(): no contexts");
                return;
            }
            updateValuesForSaveLocked();
            cancelCurrentRequestLocked();
            this.mRemoteFillService.onSaveRequest(new SaveRequest(mergePreviousSessionLocked(true), this.mClientState, this.mSelectedDatasetIds));
        }
    }

    /* access modifiers changed from: private */
    public ArrayList<FillContext> mergePreviousSessionLocked(boolean forSave) {
        ArrayList<Session> previousSessions = this.mService.getPreviousSessionsLocked(this);
        if (previousSessions == null) {
            return new ArrayList<>(this.mContexts);
        }
        if (Helper.sDebug) {
            Slog.d(TAG, "mergeSessions(" + this.id + "): Merging the content of " + previousSessions.size() + " sessions for task " + this.taskId);
        }
        ArrayList<FillContext> contexts = new ArrayList<>();
        for (int i = 0; i < previousSessions.size(); i++) {
            Session previousSession = previousSessions.get(i);
            ArrayList<FillContext> previousContexts = previousSession.mContexts;
            if (previousContexts == null) {
                Slog.w(TAG, "mergeSessions(" + this.id + "): Not merging null contexts from " + previousSession.id);
            } else {
                if (forSave) {
                    previousSession.updateValuesForSaveLocked();
                }
                if (Helper.sDebug) {
                    Slog.d(TAG, "mergeSessions(" + this.id + "): adding " + previousContexts.size() + " context from previous session #" + previousSession.id);
                }
                contexts.addAll(previousContexts);
                if (this.mClientState == null && previousSession.mClientState != null) {
                    if (Helper.sDebug) {
                        Slog.d(TAG, "mergeSessions(" + this.id + "): setting client state from previous session" + previousSession.id);
                    }
                    this.mClientState = previousSession.mClientState;
                }
            }
        }
        contexts.addAll(this.mContexts);
        return contexts;
    }

    @GuardedBy({"mLock"})
    private void requestNewFillResponseOnViewEnteredIfNecessaryLocked(AutofillId id2, ViewState viewState, int flags) {
        if ((flags & 1) != 0) {
            this.mForAugmentedAutofillOnly = false;
            if (Helper.sDebug) {
                Slog.d(TAG, "Re-starting session on view " + id2 + " and flags " + flags);
            }
            requestNewFillResponseLocked(viewState, 256, flags);
        } else if (shouldStartNewPartitionLocked(id2)) {
            if (Helper.sDebug) {
                Slog.d(TAG, "Starting partition or augmented request for view id " + id2 + ": " + viewState.getStateAsString());
            }
            requestNewFillResponseLocked(viewState, 32, flags);
        } else if (Helper.sVerbose) {
            Slog.v(TAG, "Not starting new partition for view " + id2 + ": " + viewState.getStateAsString());
        }
    }

    @GuardedBy({"mLock"})
    private boolean shouldStartNewPartitionLocked(AutofillId id2) {
        SparseArray<FillResponse> sparseArray = this.mResponses;
        if (sparseArray == null) {
            return true;
        }
        int numResponses = sparseArray.size();
        if (numResponses >= AutofillManagerService.getPartitionMaxCount()) {
            Slog.e(TAG, "Not starting a new partition on " + id2 + " because session " + this.id + " reached maximum of " + AutofillManagerService.getPartitionMaxCount());
            return false;
        }
        for (int responseNum = 0; responseNum < numResponses; responseNum++) {
            FillResponse response = this.mResponses.valueAt(responseNum);
            if (ArrayUtils.contains(response.getIgnoredIds(), id2)) {
                return false;
            }
            SaveInfo saveInfo = response.getSaveInfo();
            if (saveInfo != null && (ArrayUtils.contains(saveInfo.getOptionalIds(), id2) || ArrayUtils.contains(saveInfo.getRequiredIds(), id2))) {
                return false;
            }
            List<Dataset> datasets = response.getDatasets();
            if (datasets != null) {
                int numDatasets = datasets.size();
                for (int dataSetNum = 0; dataSetNum < numDatasets; dataSetNum++) {
                    ArrayList<AutofillId> fields = datasets.get(dataSetNum).getFieldIds();
                    if (fields != null && fields.contains(id2)) {
                        return false;
                    }
                }
            }
            if (ArrayUtils.contains(response.getAuthenticationIds(), id2)) {
                return false;
            }
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public void updateLocked(AutofillId id2, Rect virtualBounds, AutofillValue value, int action, int flags) {
        ArrayList<AutofillId> arrayList;
        String filterText;
        String currentUrl;
        if (this.mDestroyed) {
            Slog.w(TAG, "Call to Session#updateLocked() rejected - session: " + id2 + " destroyed");
            return;
        }
        id2.setSessionId(this.id);
        if (Helper.sVerbose) {
            Slog.v(TAG, "updateLocked(" + this.id + "): id=" + id2 + ", action=" + actionAsString(action) + ", flags=" + flags);
        }
        ViewState viewState = this.mViewStates.get(id2);
        if (viewState == null) {
            if (action == 1 || action == 4 || action == 2) {
                if (Helper.sVerbose) {
                    Slog.v(TAG, "Creating viewState for " + id2);
                }
                boolean isIgnored = isIgnoredLocked(id2);
                viewState = new ViewState(id2, this, isIgnored ? 128 : 1);
                this.mViewStates.put(id2, viewState);
                if (isIgnored) {
                    if (Helper.sDebug) {
                        Slog.d(TAG, "updateLocked(): ignoring view " + viewState);
                        return;
                    }
                    return;
                }
            } else if (Helper.sVerbose) {
                Slog.v(TAG, "Ignoring specific action when viewState=null");
                return;
            } else {
                return;
            }
        }
        if (action == 1) {
            this.mCurrentViewId = viewState.id;
            viewState.update(value, virtualBounds, flags);
            requestNewFillResponseLocked(viewState, 16, flags);
        } else if (action != 2) {
            String str = null;
            if (action != 3) {
                if (action != 4) {
                    Slog.w(TAG, "updateLocked(): unknown action: " + action);
                } else if (this.mCompatMode && (viewState.getState() & 512) != 0) {
                    AssistStructure.ViewNode viewNode = this.mUrlBar;
                    if (viewNode == null) {
                        currentUrl = null;
                    } else {
                        currentUrl = viewNode.getText().toString().trim();
                    }
                    if (currentUrl == null) {
                        wtf((Exception) null, "URL bar value changed, but current value is null", new Object[0]);
                    } else if (value == null || !value.isText()) {
                        wtf((Exception) null, "URL bar value changed to null or non-text: %s", value);
                    } else if (value.getTextValue().toString().equals(currentUrl)) {
                        if (Helper.sDebug) {
                            Slog.d(TAG, "Ignoring change on URL bar as it's the same");
                        }
                    } else if (!this.mSaveOnAllViewsInvisible) {
                        if (Helper.sDebug) {
                            Slog.d(TAG, "Finishing session because URL bar changed");
                        }
                        forceRemoveSelfLocked(5);
                    } else if (Helper.sDebug) {
                        Slog.d(TAG, "Ignoring change on URL because session will finish when views are gone");
                    }
                } else if (!Objects.equals(value, viewState.getCurrentValue())) {
                    if (!((value != null && !value.isEmpty()) || viewState.getCurrentValue() == null || !viewState.getCurrentValue().isText() || viewState.getCurrentValue().getTextValue() == null || getSaveInfoLocked() == null)) {
                        int length = viewState.getCurrentValue().getTextValue().length();
                        if (Helper.sDebug) {
                            Slog.d(TAG, "updateLocked(" + id2 + "): resetting value that was " + length + " chars long");
                        }
                        this.mMetricsLogger.write(newLogMaker(1124).addTaggedData(1125, Integer.valueOf(length)));
                    }
                    viewState.setCurrentValue(value);
                    AutofillValue filledValue = viewState.getAutofilledValue();
                    if (filledValue != null) {
                        if (filledValue.equals(value)) {
                            if (Helper.sVerbose) {
                                Slog.v(TAG, "ignoring autofilled change on id " + id2);
                            }
                            viewState.resetState(8);
                            return;
                        } else if (viewState.id.equals(this.mCurrentViewId) && (viewState.getState() & 4) != 0) {
                            if (Helper.sVerbose) {
                                Slog.v(TAG, "field changed after autofill on id " + id2);
                            }
                            viewState.resetState(4);
                            this.mViewStates.get(this.mCurrentViewId).maybeCallOnFillReady(flags);
                        }
                    }
                    viewState.setState(8);
                    if (value == null || !value.isText()) {
                        filterText = null;
                    } else {
                        CharSequence text = value.getTextValue();
                        if (text != null) {
                            str = text.toString();
                        }
                        filterText = str;
                    }
                    getUiForShowing().filterFillUi(filterText, this);
                }
            } else if (Objects.equals(this.mCurrentViewId, viewState.id)) {
                if (Helper.sVerbose) {
                    Slog.v(TAG, "Exiting view " + id2);
                }
                this.mUi.hideFillUi(this);
                hideAugmentedAutofillLocked(viewState);
                this.mCurrentViewId = null;
            }
        } else {
            if (Helper.sVerbose && virtualBounds != null) {
                Slog.v(TAG, "entered on virtual child " + id2 + ": " + virtualBounds);
            }
            this.mCurrentViewId = viewState.id;
            viewState.setCurrentValue(value);
            if (!this.mCompatMode || (viewState.getState() & 512) == 0) {
                if ((flags & 1) != 0 || (arrayList = this.mAugmentedAutofillableIds) == null || !arrayList.contains(id2)) {
                    requestNewFillResponseOnViewEnteredIfNecessaryLocked(id2, viewState, flags);
                    if (!Objects.equals(this.mCurrentViewId, viewState.id)) {
                        this.mUi.hideFillUi(this);
                        this.mCurrentViewId = viewState.id;
                        hideAugmentedAutofillLocked(viewState);
                    }
                    viewState.update(value, virtualBounds, flags);
                    return;
                }
                if (Helper.sDebug) {
                    Slog.d(TAG, "updateLocked(" + id2 + "): augmented-autofillable");
                }
                triggerAugmentedAutofillLocked(flags);
            } else if (Helper.sDebug) {
                Slog.d(TAG, "Ignoring VIEW_ENTERED on URL BAR (id=" + id2 + ")");
            }
        }
    }

    @GuardedBy({"mLock"})
    private void hideAugmentedAutofillLocked(ViewState viewState) {
        if ((viewState.getState() & 4096) != 0) {
            viewState.resetState(4096);
            cancelAugmentedAutofillLocked();
        }
    }

    @GuardedBy({"mLock"})
    private boolean isIgnoredLocked(AutofillId id2) {
        FillResponse response = getLastResponseLocked((String) null);
        if (response == null) {
            return false;
        }
        return ArrayUtils.contains(response.getIgnoredIds(), id2);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x002a, code lost:
        if (r19 == null) goto L_0x003c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0030, code lost:
        if (r19.isText() == false) goto L_0x003c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0032, code lost:
        r13 = r19.getTextValue().toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x003c, code lost:
        r13 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x003d, code lost:
        r2 = r12.mLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x003f, code lost:
        monitor-enter(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:?, code lost:
        r14 = r12.mService.getServiceLabelLocked();
        r15 = r12.mService.getServiceIconLocked();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x004e, code lost:
        monitor-exit(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x004f, code lost:
        if (r14 == null) goto L_0x00f0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0051, code lost:
        if (r15 != null) goto L_0x0055;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0055, code lost:
        getUiForShowing().showFillUi(r18, r17, r13, r12.mService.getServicePackageName(), r12.mComponentName, r14, r15, r16, r12.id, r12.mCompatMode);
        r1 = r12.mLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0073, code lost:
        monitor-enter(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x007a, code lost:
        if (r12.mUiShownTime != 0) goto L_0x00eb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x007c, code lost:
        r12.mUiShownTime = android.os.SystemClock.elapsedRealtime();
        r2 = r12.mUiShownTime - r12.mStartTime;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0089, code lost:
        if (com.android.server.autofill.Helper.sDebug == false) goto L_0x00a8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x008b, code lost:
        r0 = new java.lang.StringBuilder("1st UI for ");
        r0.append(r12.mActivityToken);
        r0.append(" shown in ");
        android.util.TimeUtils.formatDuration(r2, r0);
        android.util.Slog.d(TAG, r0.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x00a8, code lost:
        r0 = new java.lang.StringBuilder("id=");
        r0.append(r12.id);
        r0.append(" app=");
        r0.append(r12.mActivityToken);
        r0.append(" svc=");
        r0.append(r12.mService.getServicePackageName());
        r0.append(" latency=");
        android.util.TimeUtils.formatDuration(r2, r0);
        r12.mUiLatencyHistory.log(r0.toString());
        addTaggedDataToRequestLogLocked(r17.getRequestId(), 1145, java.lang.Long.valueOf(r2));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x00eb, code lost:
        monitor-exit(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x00ec, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00f0, code lost:
        wtf((java.lang.Exception) null, "onFillReady(): no service label or icon", new java.lang.Object[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00fa, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onFillReady(android.service.autofill.FillResponse r17, android.view.autofill.AutofillId r18, android.view.autofill.AutofillValue r19) {
        /*
            r16 = this;
            r12 = r16
            java.lang.Object r1 = r12.mLock
            monitor-enter(r1)
            boolean r0 = r12.mDestroyed     // Catch:{ all -> 0x00fe }
            if (r0 == 0) goto L_0x0028
            java.lang.String r0 = "AutofillSession"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x00fe }
            r2.<init>()     // Catch:{ all -> 0x00fe }
            java.lang.String r3 = "Call to Session#onFillReady() rejected - session: "
            r2.append(r3)     // Catch:{ all -> 0x00fe }
            int r3 = r12.id     // Catch:{ all -> 0x00fe }
            r2.append(r3)     // Catch:{ all -> 0x00fe }
            java.lang.String r3 = " destroyed"
            r2.append(r3)     // Catch:{ all -> 0x00fe }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x00fe }
            android.util.Slog.w(r0, r2)     // Catch:{ all -> 0x00fe }
            monitor-exit(r1)     // Catch:{ all -> 0x00fe }
            return
        L_0x0028:
            monitor-exit(r1)     // Catch:{ all -> 0x00fe }
            r0 = 0
            if (r19 == 0) goto L_0x003c
            boolean r1 = r19.isText()
            if (r1 == 0) goto L_0x003c
            java.lang.CharSequence r1 = r19.getTextValue()
            java.lang.String r0 = r1.toString()
            r13 = r0
            goto L_0x003d
        L_0x003c:
            r13 = r0
        L_0x003d:
            java.lang.Object r2 = r12.mLock
            monitor-enter(r2)
            com.android.server.autofill.AutofillManagerServiceImpl r0 = r12.mService     // Catch:{ all -> 0x00fb }
            java.lang.CharSequence r0 = r0.getServiceLabelLocked()     // Catch:{ all -> 0x00fb }
            r14 = r0
            com.android.server.autofill.AutofillManagerServiceImpl r0 = r12.mService     // Catch:{ all -> 0x00fb }
            android.graphics.drawable.Drawable r0 = r0.getServiceIconLocked()     // Catch:{ all -> 0x00fb }
            r15 = r0
            monitor-exit(r2)     // Catch:{ all -> 0x00fb }
            if (r14 == 0) goto L_0x00f0
            if (r15 != 0) goto L_0x0055
            goto L_0x00f0
        L_0x0055:
            com.android.server.autofill.ui.AutoFillUI r1 = r16.getUiForShowing()
            com.android.server.autofill.AutofillManagerServiceImpl r0 = r12.mService
            java.lang.String r5 = r0.getServicePackageName()
            android.content.ComponentName r6 = r12.mComponentName
            int r10 = r12.id
            boolean r11 = r12.mCompatMode
            r2 = r18
            r3 = r17
            r4 = r13
            r7 = r14
            r8 = r15
            r9 = r16
            r1.showFillUi(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            java.lang.Object r1 = r12.mLock
            monitor-enter(r1)
            long r2 = r12.mUiShownTime     // Catch:{ all -> 0x00ed }
            r4 = 0
            int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r0 != 0) goto L_0x00eb
            long r2 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x00ed }
            r12.mUiShownTime = r2     // Catch:{ all -> 0x00ed }
            long r2 = r12.mUiShownTime     // Catch:{ all -> 0x00ed }
            long r4 = r12.mStartTime     // Catch:{ all -> 0x00ed }
            long r2 = r2 - r4
            boolean r0 = com.android.server.autofill.Helper.sDebug     // Catch:{ all -> 0x00ed }
            if (r0 == 0) goto L_0x00a8
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ed }
            java.lang.String r4 = "1st UI for "
            r0.<init>(r4)     // Catch:{ all -> 0x00ed }
            android.os.IBinder r4 = r12.mActivityToken     // Catch:{ all -> 0x00ed }
            r0.append(r4)     // Catch:{ all -> 0x00ed }
            java.lang.String r4 = " shown in "
            r0.append(r4)     // Catch:{ all -> 0x00ed }
            android.util.TimeUtils.formatDuration(r2, r0)     // Catch:{ all -> 0x00ed }
            java.lang.String r4 = "AutofillSession"
            java.lang.String r5 = r0.toString()     // Catch:{ all -> 0x00ed }
            android.util.Slog.d(r4, r5)     // Catch:{ all -> 0x00ed }
        L_0x00a8:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ed }
            java.lang.String r4 = "id="
            r0.<init>(r4)     // Catch:{ all -> 0x00ed }
            int r4 = r12.id     // Catch:{ all -> 0x00ed }
            r0.append(r4)     // Catch:{ all -> 0x00ed }
            java.lang.String r4 = " app="
            r0.append(r4)     // Catch:{ all -> 0x00ed }
            android.os.IBinder r4 = r12.mActivityToken     // Catch:{ all -> 0x00ed }
            r0.append(r4)     // Catch:{ all -> 0x00ed }
            java.lang.String r4 = " svc="
            r0.append(r4)     // Catch:{ all -> 0x00ed }
            com.android.server.autofill.AutofillManagerServiceImpl r4 = r12.mService     // Catch:{ all -> 0x00ed }
            java.lang.String r4 = r4.getServicePackageName()     // Catch:{ all -> 0x00ed }
            r0.append(r4)     // Catch:{ all -> 0x00ed }
            java.lang.String r4 = " latency="
            r0.append(r4)     // Catch:{ all -> 0x00ed }
            android.util.TimeUtils.formatDuration(r2, r0)     // Catch:{ all -> 0x00ed }
            android.util.LocalLog r4 = r12.mUiLatencyHistory     // Catch:{ all -> 0x00ed }
            java.lang.String r5 = r0.toString()     // Catch:{ all -> 0x00ed }
            r4.log(r5)     // Catch:{ all -> 0x00ed }
            int r4 = r17.getRequestId()     // Catch:{ all -> 0x00ed }
            r5 = 1145(0x479, float:1.604E-42)
            java.lang.Long r6 = java.lang.Long.valueOf(r2)     // Catch:{ all -> 0x00ed }
            r12.addTaggedDataToRequestLogLocked(r4, r5, r6)     // Catch:{ all -> 0x00ed }
        L_0x00eb:
            monitor-exit(r1)     // Catch:{ all -> 0x00ed }
            return
        L_0x00ed:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x00ed }
            throw r0
        L_0x00f0:
            r0 = 0
            r1 = 0
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = "onFillReady(): no service label or icon"
            r12.wtf(r0, r2, r1)
            return
        L_0x00fb:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x00fb }
            throw r0
        L_0x00fe:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x00fe }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.autofill.Session.onFillReady(android.service.autofill.FillResponse, android.view.autofill.AutofillId, android.view.autofill.AutofillValue):void");
    }

    /* access modifiers changed from: package-private */
    public boolean isDestroyed() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mDestroyed;
        }
        return z;
    }

    /* access modifiers changed from: package-private */
    public IAutoFillManagerClient getClient() {
        IAutoFillManagerClient iAutoFillManagerClient;
        synchronized (this.mLock) {
            iAutoFillManagerClient = this.mClient;
        }
        return iAutoFillManagerClient;
    }

    private void notifyUnavailableToClient(int sessionFinishedState, ArrayList<AutofillId> autofillableIds) {
        synchronized (this.mLock) {
            if (this.mCurrentViewId != null) {
                try {
                    if (this.mHasCallback) {
                        this.mClient.notifyNoFillUi(this.id, this.mCurrentViewId, sessionFinishedState);
                    } else if (sessionFinishedState != 0) {
                        this.mClient.setSessionFinished(sessionFinishedState, autofillableIds);
                    }
                } catch (RemoteException e) {
                    Slog.e(TAG, "Error notifying client no fill UI: id=" + this.mCurrentViewId, e);
                }
            }
        }
    }

    @GuardedBy({"mLock"})
    private void updateTrackedIdsLocked() {
        AutofillId saveTriggerId;
        boolean saveOnFinish;
        int flags;
        ArraySet<AutofillId> trackedViews;
        ArraySet<AutofillId> fillableIds;
        FillResponse response = getLastResponseLocked((String) null);
        if (response != null) {
            ArraySet<AutofillId> trackedViews2 = null;
            this.mSaveOnAllViewsInvisible = false;
            SaveInfo saveInfo = response.getSaveInfo();
            boolean z = true;
            if (saveInfo != null) {
                AutofillId saveTriggerId2 = saveInfo.getTriggerId();
                if (saveTriggerId2 != null) {
                    writeLog(1228);
                }
                int flags2 = saveInfo.getFlags();
                this.mSaveOnAllViewsInvisible = (flags2 & 1) != 0;
                if (this.mSaveOnAllViewsInvisible) {
                    if (0 == 0) {
                        trackedViews2 = new ArraySet<>();
                    }
                    if (saveInfo.getRequiredIds() != null) {
                        Collections.addAll(trackedViews2, saveInfo.getRequiredIds());
                    }
                    if (saveInfo.getOptionalIds() != null) {
                        Collections.addAll(trackedViews2, saveInfo.getOptionalIds());
                    }
                }
                if ((flags2 & 2) != 0) {
                    saveOnFinish = false;
                    saveTriggerId = saveTriggerId2;
                    flags = flags2;
                    trackedViews = trackedViews2;
                } else {
                    saveOnFinish = true;
                    saveTriggerId = saveTriggerId2;
                    flags = flags2;
                    trackedViews = trackedViews2;
                }
            } else {
                saveOnFinish = true;
                saveTriggerId = null;
                flags = 0;
                trackedViews = null;
            }
            List<Dataset> datasets = response.getDatasets();
            ArraySet<AutofillId> fillableIds2 = null;
            if (datasets != null) {
                for (int i = 0; i < datasets.size(); i++) {
                    ArrayList<AutofillId> fieldIds = datasets.get(i).getFieldIds();
                    if (fieldIds != null) {
                        for (int j = 0; j < fieldIds.size(); j++) {
                            AutofillId id2 = fieldIds.get(j);
                            if (trackedViews == null || !trackedViews.contains(id2)) {
                                fillableIds2 = ArrayUtils.add(fillableIds2, id2);
                            }
                        }
                    }
                }
                fillableIds = fillableIds2;
            } else {
                fillableIds = null;
            }
            try {
                if (Helper.sVerbose) {
                    try {
                        StringBuilder sb = new StringBuilder();
                        sb.append("updateTrackedIdsLocked(): ");
                        sb.append(trackedViews);
                        sb.append(" => ");
                        sb.append(fillableIds);
                        sb.append(" triggerId: ");
                        sb.append(saveTriggerId);
                        sb.append(" saveOnFinish:");
                        sb.append(saveOnFinish);
                        sb.append(" flags: ");
                        sb.append(flags);
                        sb.append(" hasSaveInfo: ");
                        if (saveInfo == null) {
                            z = false;
                        }
                        sb.append(z);
                        Slog.v(TAG, sb.toString());
                    } catch (RemoteException e) {
                        e = e;
                        List<Dataset> list = datasets;
                        int i2 = flags;
                        boolean z2 = saveOnFinish;
                        Slog.w(TAG, "Cannot set tracked ids", e);
                    }
                }
                List<Dataset> list2 = datasets;
                int i3 = flags;
                boolean z3 = saveOnFinish;
                try {
                    this.mClient.setTrackedViews(this.id, Helper.toArray(trackedViews), this.mSaveOnAllViewsInvisible, saveOnFinish, Helper.toArray(fillableIds), saveTriggerId);
                } catch (RemoteException e2) {
                    e = e2;
                }
            } catch (RemoteException e3) {
                e = e3;
                List<Dataset> list3 = datasets;
                int i4 = flags;
                boolean z4 = saveOnFinish;
                Slog.w(TAG, "Cannot set tracked ids", e);
            }
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public void setAutofillFailureLocked(List<AutofillId> ids) {
        for (int i = 0; i < ids.size(); i++) {
            AutofillId id2 = ids.get(i);
            ViewState viewState = this.mViewStates.get(id2);
            if (viewState == null) {
                Slog.w(TAG, "setAutofillFailure(): no view for id " + id2);
            } else {
                viewState.resetState(4);
                viewState.setState(viewState.getState() | 1024);
                if (Helper.sVerbose) {
                    Slog.v(TAG, "Changed state of " + id2 + " to " + viewState.getStateAsString());
                }
            }
        }
    }

    @GuardedBy({"mLock"})
    private void replaceResponseLocked(FillResponse oldResponse, FillResponse newResponse, Bundle newClientState) {
        setViewStatesLocked(oldResponse, 1, true);
        newResponse.setRequestId(oldResponse.getRequestId());
        this.mResponses.put(newResponse.getRequestId(), newResponse);
        processResponseLocked(newResponse, newClientState, 0);
    }

    @GuardedBy({"mLock"})
    private void processNullResponseLocked(int requestId, int flags) {
        ArrayList<AutofillId> autofillableIds;
        if ((flags & 1) != 0) {
            getUiForShowing().showError(17039563, (AutoFillUI.AutoFillUiCallback) this);
        }
        FillContext context = getFillContextByRequestIdLocked(requestId);
        if (context != null) {
            autofillableIds = Helper.getAutofillIds(context.getStructure(), true);
        } else {
            Slog.w(TAG, "processNullResponseLocked(): no context for req " + requestId);
            autofillableIds = null;
        }
        this.mService.resetLastResponse();
        this.mAugmentedAutofillDestroyer = triggerAugmentedAutofillLocked(flags);
        if (this.mAugmentedAutofillDestroyer == null && (flags & 4) == 0) {
            if (Helper.sVerbose) {
                Slog.v(TAG, "canceling session " + this.id + " when service returned null and it cannot be augmented. AutofillableIds: " + autofillableIds);
            }
            notifyUnavailableToClient(2, autofillableIds);
            removeSelf();
            return;
        }
        if (Helper.sVerbose) {
            if ((flags & 4) != 0) {
                Slog.v(TAG, "keeping session " + this.id + " when service returned null and augmented service is disabled for password fields. AutofillableIds: " + autofillableIds);
            } else {
                Slog.v(TAG, "keeping session " + this.id + " when service returned null but it can be augmented. AutofillableIds: " + autofillableIds);
            }
        }
        this.mAugmentedAutofillableIds = autofillableIds;
        try {
            this.mClient.setState(32);
        } catch (RemoteException e) {
            Slog.e(TAG, "Error setting client to autofill-only", e);
        }
    }

    @GuardedBy({"mLock"})
    private Runnable triggerAugmentedAutofillLocked(int flags) {
        if ((flags & 4) != 0) {
            return null;
        }
        int supportedModes = this.mService.getSupportedSmartSuggestionModesLocked();
        if (supportedModes == 0) {
            if (Helper.sVerbose) {
                Slog.v(TAG, "triggerAugmentedAutofillLocked(): no supported modes");
            }
            return null;
        }
        RemoteAugmentedAutofillService remoteService = this.mService.getRemoteAugmentedAutofillServiceLocked();
        if (remoteService == null) {
            if (Helper.sVerbose) {
                Slog.v(TAG, "triggerAugmentedAutofillLocked(): no service for user");
            }
            return null;
        } else if ((supportedModes & 1) == 0) {
            Slog.w(TAG, "Unsupported Smart Suggestion mode: " + supportedModes);
            return null;
        } else if (this.mCurrentViewId == null) {
            Slog.w(TAG, "triggerAugmentedAutofillLocked(): no view currently focused");
            return null;
        } else {
            boolean isWhitelisted = this.mService.isWhitelistedForAugmentedAutofillLocked(this.mComponentName);
            ((AutofillManagerService) this.mService.getMaster()).logRequestLocked("aug:id=" + this.id + " u=" + this.uid + " m=" + 1 + " a=" + ComponentName.flattenToShortString(this.mComponentName) + " f=" + this.mCurrentViewId + " s=" + remoteService.getComponentName() + " w=" + isWhitelisted);
            if (!isWhitelisted) {
                if (Helper.sVerbose) {
                    Slog.v(TAG, "triggerAugmentedAutofillLocked(): " + ComponentName.flattenToShortString(this.mComponentName) + " not whitelisted ");
                }
                return null;
            }
            if (Helper.sVerbose) {
                Slog.v(TAG, "calling Augmented Autofill Service (" + ComponentName.flattenToShortString(remoteService.getComponentName()) + ") on view " + this.mCurrentViewId + " using suggestion mode " + AutofillManager.getSmartSuggestionModeToString(1) + " when server returned null for session " + this.id);
            }
            ViewState viewState = this.mViewStates.get(this.mCurrentViewId);
            viewState.setState(4096);
            AutofillValue currentValue = viewState.getCurrentValue();
            if (this.mAugmentedRequestsLogs == null) {
                this.mAugmentedRequestsLogs = new ArrayList<>();
            }
            this.mAugmentedRequestsLogs.add(newLogMaker(1630, remoteService.getComponentName().getPackageName()));
            RemoteAugmentedAutofillService remoteAugmentedAutofillService = remoteService;
            remoteAugmentedAutofillService.onRequestAutofillLocked(this.id, this.mClient, this.taskId, this.mComponentName, AutofillId.withoutSession(this.mCurrentViewId), currentValue);
            if (this.mAugmentedAutofillDestroyer == null) {
                this.mAugmentedAutofillDestroyer = new Runnable() {
                    public final void run() {
                        RemoteAugmentedAutofillService.this.onDestroyAutofillWindowsRequest();
                    }
                };
            }
            return this.mAugmentedAutofillDestroyer;
        }
    }

    @GuardedBy({"mLock"})
    private void cancelAugmentedAutofillLocked() {
        RemoteAugmentedAutofillService remoteService = this.mService.getRemoteAugmentedAutofillServiceLocked();
        if (remoteService == null) {
            Slog.w(TAG, "cancelAugmentedAutofillLocked(): no service for user");
            return;
        }
        if (Helper.sVerbose) {
            Slog.v(TAG, "cancelAugmentedAutofillLocked() on " + this.mCurrentViewId);
        }
        remoteService.onDestroyAutofillWindowsRequest();
    }

    @GuardedBy({"mLock"})
    private void processResponseLocked(FillResponse newResponse, Bundle newClientState, int flags) {
        this.mUi.hideAll(this);
        int requestId = newResponse.getRequestId();
        if (Helper.sVerbose) {
            Slog.v(TAG, "processResponseLocked(): mCurrentViewId=" + this.mCurrentViewId + ",flags=" + flags + ", reqId=" + requestId + ", resp=" + newResponse + ",newClientState=" + newClientState);
        }
        if (this.mResponses == null) {
            this.mResponses = new SparseArray<>(2);
        }
        this.mResponses.put(requestId, newResponse);
        this.mClientState = newClientState != null ? newClientState : newResponse.getClientState();
        setViewStatesLocked(newResponse, 2, false);
        updateTrackedIdsLocked();
        AutofillId autofillId = this.mCurrentViewId;
        if (autofillId != null) {
            this.mViewStates.get(autofillId).maybeCallOnFillReady(flags);
        }
    }

    @GuardedBy({"mLock"})
    private void setViewStatesLocked(FillResponse response, int state, boolean clearResponse) {
        List<Dataset> datasets = response.getDatasets();
        if (datasets != null) {
            for (int i = 0; i < datasets.size(); i++) {
                Dataset dataset = datasets.get(i);
                if (dataset == null) {
                    Slog.w(TAG, "Ignoring null dataset on " + datasets);
                } else {
                    setViewStatesLocked(response, dataset, state, clearResponse);
                }
            }
        } else if (response.getAuthentication() != null) {
            for (AutofillId autofillId : response.getAuthenticationIds()) {
                ViewState viewState = createOrUpdateViewStateLocked(autofillId, state, (AutofillValue) null);
                if (!clearResponse) {
                    viewState.setResponse(response);
                } else {
                    viewState.setResponse((FillResponse) null);
                }
            }
        }
        SaveInfo saveInfo = response.getSaveInfo();
        if (saveInfo != null) {
            AutofillId[] requiredIds = saveInfo.getRequiredIds();
            if (requiredIds != null) {
                for (AutofillId id2 : requiredIds) {
                    createOrUpdateViewStateLocked(id2, state, (AutofillValue) null);
                }
            }
            AutofillId[] optionalIds = saveInfo.getOptionalIds();
            if (optionalIds != null) {
                for (AutofillId id3 : optionalIds) {
                    createOrUpdateViewStateLocked(id3, state, (AutofillValue) null);
                }
            }
        }
        AutofillId[] requiredIds2 = response.getAuthenticationIds();
        if (requiredIds2 != null) {
            for (AutofillId id4 : requiredIds2) {
                createOrUpdateViewStateLocked(id4, state, (AutofillValue) null);
            }
        }
    }

    @GuardedBy({"mLock"})
    private void setViewStatesLocked(FillResponse response, Dataset dataset, int state, boolean clearResponse) {
        ArrayList<AutofillId> ids = dataset.getFieldIds();
        ArrayList<AutofillValue> values = dataset.getFieldValues();
        for (int j = 0; j < ids.size(); j++) {
            ViewState viewState = createOrUpdateViewStateLocked(ids.get(j), state, values.get(j));
            String datasetId = dataset.getId();
            if (datasetId != null) {
                viewState.setDatasetId(datasetId);
            }
            if (response != null) {
                viewState.setResponse(response);
            } else if (clearResponse) {
                viewState.setResponse((FillResponse) null);
            }
        }
    }

    @GuardedBy({"mLock"})
    private ViewState createOrUpdateViewStateLocked(AutofillId id2, int state, AutofillValue value) {
        ViewState viewState = this.mViewStates.get(id2);
        if (viewState != null) {
            viewState.setState(state);
        } else {
            viewState = new ViewState(id2, this, state);
            if (Helper.sVerbose) {
                Slog.v(TAG, "Adding autofillable view with id " + id2 + " and state " + state);
            }
            viewState.setCurrentValue(findValueLocked(id2));
            this.mViewStates.put(id2, viewState);
        }
        if ((state & 4) != 0) {
            viewState.setAutofilledValue(value);
        }
        return viewState;
    }

    /* access modifiers changed from: package-private */
    public void autoFill(int requestId, int datasetIndex, Dataset dataset, boolean generateEvent) {
        if (Helper.sDebug) {
            Slog.d(TAG, "autoFill(): requestId=" + requestId + "; datasetIdx=" + datasetIndex + "; dataset=" + dataset);
        }
        synchronized (this.mLock) {
            if (this.mDestroyed) {
                Slog.w(TAG, "Call to Session#autoFill() rejected - session: " + this.id + " destroyed");
            } else if (dataset.getAuthentication() == null) {
                if (generateEvent) {
                    this.mService.logDatasetSelected(dataset.getId(), this.id, this.mClientState);
                }
                autoFillApp(dataset);
            } else {
                this.mService.logDatasetAuthenticationSelected(dataset.getId(), this.id, this.mClientState);
                setViewStatesLocked((FillResponse) null, dataset, 64, false);
                Intent fillInIntent = createAuthFillInIntentLocked(requestId, this.mClientState);
                if (fillInIntent == null) {
                    forceRemoveSelfLocked();
                } else {
                    startAuthentication(AutofillManager.makeAuthenticationId(requestId, datasetIndex), dataset.getAuthentication(), fillInIntent);
                }
            }
        }
    }

    @GuardedBy({"mLock"})
    private Intent createAuthFillInIntentLocked(int requestId, Bundle extras) {
        Intent fillInIntent = new Intent();
        FillContext context = getFillContextByRequestIdLocked(requestId);
        if (context == null) {
            wtf((Exception) null, "createAuthFillInIntentLocked(): no FillContext. requestId=%d; mContexts=%s", Integer.valueOf(requestId), this.mContexts);
            return null;
        }
        fillInIntent.putExtra("android.view.autofill.extra.ASSIST_STRUCTURE", context.getStructure());
        fillInIntent.putExtra("android.view.autofill.extra.CLIENT_STATE", extras);
        return fillInIntent;
    }

    /* Debug info: failed to restart local var, previous not found, register: 3 */
    /* access modifiers changed from: private */
    public void startAuthentication(int authenticationId, IntentSender intent, Intent fillInIntent) {
        try {
            synchronized (this.mLock) {
                this.mClient.authenticate(this.id, authenticationId, intent, fillInIntent);
            }
        } catch (RemoteException e) {
            Slog.e(TAG, "Error launching auth intent", e);
        }
    }

    public String toString() {
        return "Session: [id=" + this.id + ", component=" + this.mComponentName + "]";
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public void dumpLocked(String prefix, PrintWriter pw) {
        String prefix2 = prefix + "  ";
        pw.print(prefix);
        pw.print("id: ");
        pw.println(this.id);
        pw.print(prefix);
        pw.print("uid: ");
        pw.println(this.uid);
        pw.print(prefix);
        pw.print("taskId: ");
        pw.println(this.taskId);
        pw.print(prefix);
        pw.print("flags: ");
        pw.println(this.mFlags);
        pw.print(prefix);
        pw.print("mComponentName: ");
        pw.println(this.mComponentName);
        pw.print(prefix);
        pw.print("mActivityToken: ");
        pw.println(this.mActivityToken);
        pw.print(prefix);
        pw.print("mStartTime: ");
        pw.println(this.mStartTime);
        pw.print(prefix);
        pw.print("Time to show UI: ");
        long j = this.mUiShownTime;
        if (j == 0) {
            pw.println("N/A");
        } else {
            TimeUtils.formatDuration(j - this.mStartTime, pw);
            pw.println();
        }
        int requestLogsSizes = this.mRequestLogs.size();
        pw.print(prefix);
        pw.print("mSessionLogs: ");
        pw.println(requestLogsSizes);
        for (int i = 0; i < requestLogsSizes; i++) {
            int requestId = this.mRequestLogs.keyAt(i);
            pw.print(prefix2);
            pw.print('#');
            pw.print(i);
            pw.print(": req=");
            pw.print(requestId);
            pw.print(", log=");
            dumpRequestLog(pw, this.mRequestLogs.valueAt(i));
            pw.println();
        }
        pw.print(prefix);
        pw.print("mResponses: ");
        SparseArray<FillResponse> sparseArray = this.mResponses;
        if (sparseArray == null) {
            pw.println("null");
        } else {
            pw.println(sparseArray.size());
            for (int i2 = 0; i2 < this.mResponses.size(); i2++) {
                pw.print(prefix2);
                pw.print('#');
                pw.print(i2);
                pw.print(' ');
                pw.println(this.mResponses.valueAt(i2));
            }
        }
        pw.print(prefix);
        pw.print("mCurrentViewId: ");
        pw.println(this.mCurrentViewId);
        pw.print(prefix);
        pw.print("mDestroyed: ");
        pw.println(this.mDestroyed);
        pw.print(prefix);
        pw.print("mIsSaving: ");
        pw.println(this.mIsSaving);
        pw.print(prefix);
        pw.print("mPendingSaveUi: ");
        pw.println(this.mPendingSaveUi);
        int numberViews = this.mViewStates.size();
        pw.print(prefix);
        pw.print("mViewStates size: ");
        pw.println(this.mViewStates.size());
        for (int i3 = 0; i3 < numberViews; i3++) {
            pw.print(prefix);
            pw.print("ViewState at #");
            pw.println(i3);
            this.mViewStates.valueAt(i3).dump(prefix2, pw);
        }
        pw.print(prefix);
        pw.print("mContexts: ");
        ArrayList<FillContext> arrayList = this.mContexts;
        if (arrayList != null) {
            int numContexts = arrayList.size();
            for (int i4 = 0; i4 < numContexts; i4++) {
                FillContext context = this.mContexts.get(i4);
                pw.print(prefix2);
                pw.print(context);
                if (Helper.sVerbose) {
                    pw.println("AssistStructure dumped at logcat)");
                    context.getStructure().dump(false);
                }
            }
        } else {
            pw.println("null");
        }
        pw.print(prefix);
        pw.print("mHasCallback: ");
        pw.println(this.mHasCallback);
        if (this.mClientState != null) {
            pw.print(prefix);
            pw.print("mClientState: ");
            pw.print(this.mClientState.getSize());
            pw.println(" bytes");
        }
        pw.print(prefix);
        pw.print("mCompatMode: ");
        pw.println(this.mCompatMode);
        pw.print(prefix);
        pw.print("mUrlBar: ");
        if (this.mUrlBar == null) {
            pw.println("N/A");
        } else {
            pw.print("id=");
            pw.print(this.mUrlBar.getAutofillId());
            pw.print(" domain=");
            pw.print(this.mUrlBar.getWebDomain());
            pw.print(" text=");
            Helper.printlnRedactedText(pw, this.mUrlBar.getText());
        }
        pw.print(prefix);
        pw.print("mSaveOnAllViewsInvisible: ");
        pw.println(this.mSaveOnAllViewsInvisible);
        pw.print(prefix);
        pw.print("mSelectedDatasetIds: ");
        pw.println(this.mSelectedDatasetIds);
        if (this.mForAugmentedAutofillOnly) {
            pw.print(prefix);
            pw.println("For Augmented Autofill Only");
        }
        if (this.mAugmentedAutofillDestroyer != null) {
            pw.print(prefix);
            pw.println("has mAugmentedAutofillDestroyer");
        }
        if (this.mAugmentedRequestsLogs != null) {
            pw.print(prefix);
            pw.print("number augmented requests: ");
            pw.println(this.mAugmentedRequestsLogs.size());
        }
        if (this.mAugmentedAutofillableIds != null) {
            pw.print(prefix);
            pw.print("mAugmentedAutofillableIds: ");
            pw.println(this.mAugmentedAutofillableIds);
        }
        RemoteFillService remoteFillService = this.mRemoteFillService;
        if (remoteFillService != null) {
            remoteFillService.dump(prefix, pw);
        }
    }

    private static void dumpRequestLog(PrintWriter pw, LogMaker log) {
        pw.print("CAT=");
        pw.print(log.getCategory());
        pw.print(", TYPE=");
        int type = log.getType();
        if (type == 2) {
            pw.print("CLOSE");
        } else if (type == 10) {
            pw.print("SUCCESS");
        } else if (type != 11) {
            pw.print("UNSUPPORTED");
        } else {
            pw.print("FAILURE");
        }
        pw.print('(');
        pw.print(type);
        pw.print(')');
        pw.print(", PKG=");
        pw.print(log.getPackageName());
        pw.print(", SERVICE=");
        pw.print(log.getTaggedData(908));
        pw.print(", ORDINAL=");
        pw.print(log.getTaggedData(1454));
        dumpNumericValue(pw, log, "FLAGS", 1452);
        dumpNumericValue(pw, log, "NUM_DATASETS", 909);
        dumpNumericValue(pw, log, "UI_LATENCY", 1145);
        int authStatus = Helper.getNumericValue(log, 1453);
        if (authStatus != 0) {
            pw.print(", AUTH_STATUS=");
            if (authStatus != 912) {
                switch (authStatus) {
                    case 1126:
                        pw.print("DATASET_AUTHENTICATED");
                        break;
                    case 1127:
                        pw.print("INVALID_DATASET_AUTHENTICATION");
                        break;
                    case 1128:
                        pw.print("INVALID_AUTHENTICATION");
                        break;
                    default:
                        pw.print("UNSUPPORTED");
                        break;
                }
            } else {
                pw.print("AUTHENTICATED");
            }
            pw.print('(');
            pw.print(authStatus);
            pw.print(')');
        }
        dumpNumericValue(pw, log, "FC_IDS", 1271);
        dumpNumericValue(pw, log, "COMPAT_MODE", 1414);
    }

    private static void dumpNumericValue(PrintWriter pw, LogMaker log, String field, int tag) {
        int value = Helper.getNumericValue(log, tag);
        if (value != 0) {
            pw.print(", ");
            pw.print(field);
            pw.print('=');
            pw.print(value);
        }
    }

    /* access modifiers changed from: package-private */
    public void autoFillApp(Dataset dataset) {
        synchronized (this.mLock) {
            if (this.mDestroyed) {
                Slog.w(TAG, "Call to Session#autoFillApp() rejected - session: " + this.id + " destroyed");
                return;
            }
            try {
                int entryCount = dataset.getFieldIds().size();
                List<AutofillId> ids = new ArrayList<>(entryCount);
                List<AutofillValue> values = new ArrayList<>(entryCount);
                boolean waitingDatasetAuth = false;
                for (int i = 0; i < entryCount; i++) {
                    if (dataset.getFieldValues().get(i) != null) {
                        AutofillId viewId = (AutofillId) dataset.getFieldIds().get(i);
                        ids.add(viewId);
                        values.add((AutofillValue) dataset.getFieldValues().get(i));
                        ViewState viewState = this.mViewStates.get(viewId);
                        if (!(viewState == null || (viewState.getState() & 64) == 0)) {
                            if (Helper.sVerbose) {
                                Slog.v(TAG, "autofillApp(): view " + viewId + " waiting auth");
                            }
                            waitingDatasetAuth = true;
                            viewState.resetState(64);
                        }
                    }
                }
                if (ids.isEmpty() == 0) {
                    if (waitingDatasetAuth) {
                        this.mUi.hideFillUi(this);
                    }
                    if (Helper.sDebug) {
                        Slog.d(TAG, "autoFillApp(): the buck is on the app: " + dataset);
                    }
                    this.mClient.autofill(this.id, ids, values);
                    if (dataset.getId() != null) {
                        if (this.mSelectedDatasetIds == null) {
                            this.mSelectedDatasetIds = new ArrayList<>();
                        }
                        this.mSelectedDatasetIds.add(dataset.getId());
                    }
                    setViewStatesLocked((FillResponse) null, dataset, 4, false);
                }
            } catch (RemoteException e) {
                Slog.w(TAG, "Error autofilling activity: " + e);
            }
        }
    }

    private AutoFillUI getUiForShowing() {
        AutoFillUI autoFillUI;
        synchronized (this.mLock) {
            this.mUi.setCallback(this);
            autoFillUI = this.mUi;
        }
        return autoFillUI;
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public RemoteFillService destroyLocked() {
        int totalAugmentedRequests;
        if (this.mDestroyed) {
            return null;
        }
        unlinkClientVultureLocked();
        this.mUi.destroyAll(this.mPendingSaveUi, this, true);
        this.mUi.clearCallback(this);
        this.mDestroyed = true;
        int totalRequests = this.mRequestLogs.size();
        if (totalRequests > 0) {
            if (Helper.sVerbose) {
                Slog.v(TAG, "destroyLocked(): logging " + totalRequests + " requests");
            }
            for (int i = 0; i < totalRequests; i++) {
                this.mMetricsLogger.write(this.mRequestLogs.valueAt(i));
            }
        }
        ArrayList<LogMaker> arrayList = this.mAugmentedRequestsLogs;
        if (arrayList == null) {
            totalAugmentedRequests = 0;
        } else {
            totalAugmentedRequests = arrayList.size();
        }
        if (totalAugmentedRequests > 0) {
            if (Helper.sVerbose) {
                Slog.v(TAG, "destroyLocked(): logging " + totalRequests + " augmented requests");
            }
            for (int i2 = 0; i2 < totalAugmentedRequests; i2++) {
                this.mMetricsLogger.write(this.mAugmentedRequestsLogs.get(i2));
            }
        }
        LogMaker log = newLogMaker(919).addTaggedData(1455, Integer.valueOf(totalRequests));
        if (totalAugmentedRequests > 0) {
            log.addTaggedData(1631, Integer.valueOf(totalAugmentedRequests));
        }
        if (this.mForAugmentedAutofillOnly) {
            log.addTaggedData(1720, 1);
        }
        this.mMetricsLogger.write(log);
        return this.mRemoteFillService;
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public void forceRemoveSelfLocked() {
        forceRemoveSelfLocked(0);
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public void forceRemoveSelfIfForAugmentedAutofillOnlyLocked() {
        if (Helper.sVerbose) {
            Slog.v(TAG, "forceRemoveSelfIfForAugmentedAutofillOnly(" + this.id + "): " + this.mForAugmentedAutofillOnly);
        }
        if (this.mForAugmentedAutofillOnly) {
            forceRemoveSelfLocked();
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public void forceRemoveSelfLocked(int clientState) {
        if (Helper.sVerbose) {
            Slog.v(TAG, "forceRemoveSelfLocked(): " + this.mPendingSaveUi);
        }
        boolean isPendingSaveUi = isSaveUiPendingLocked();
        this.mPendingSaveUi = null;
        removeSelfLocked();
        this.mUi.destroyAll(this.mPendingSaveUi, this, false);
        if (!isPendingSaveUi) {
            try {
                this.mClient.setSessionFinished(clientState, (List) null);
            } catch (RemoteException e) {
                Slog.e(TAG, "Error notifying client to finish session", e);
            }
        }
        destroyAugmentedAutofillWindowsLocked();
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public void destroyAugmentedAutofillWindowsLocked() {
        Runnable runnable = this.mAugmentedAutofillDestroyer;
        if (runnable != null) {
            runnable.run();
            this.mAugmentedAutofillDestroyer = null;
        }
    }

    /* access modifiers changed from: private */
    public void removeSelf() {
        synchronized (this.mLock) {
            removeSelfLocked();
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public void removeSelfLocked() {
        if (Helper.sVerbose) {
            Slog.v(TAG, "removeSelfLocked(" + this.id + "): " + this.mPendingSaveUi);
        }
        if (this.mDestroyed) {
            Slog.w(TAG, "Call to Session#removeSelfLocked() rejected - session: " + this.id + " destroyed");
        } else if (isSaveUiPendingLocked()) {
            Slog.i(TAG, "removeSelfLocked() ignored, waiting for pending save ui");
        } else {
            RemoteFillService remoteFillService = destroyLocked();
            this.mService.removeSessionLocked(this.id);
            if (remoteFillService != null) {
                remoteFillService.destroy();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void onPendingSaveUi(int operation, IBinder token) {
        getUiForShowing().onPendingSaveUi(operation, token);
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mLock"})
    public boolean isSaveUiPendingForTokenLocked(IBinder token) {
        return isSaveUiPendingLocked() && token.equals(this.mPendingSaveUi.getToken());
    }

    @GuardedBy({"mLock"})
    private boolean isSaveUiPendingLocked() {
        PendingUi pendingUi = this.mPendingSaveUi;
        return pendingUi != null && pendingUi.getState() == 2;
    }

    @GuardedBy({"mLock"})
    private int getLastResponseIndexLocked() {
        int lastResponseIdx = -1;
        SparseArray<FillResponse> sparseArray = this.mResponses;
        if (sparseArray != null) {
            int responseCount = sparseArray.size();
            for (int i = 0; i < responseCount; i++) {
                if (this.mResponses.keyAt(i) > -1) {
                    lastResponseIdx = i;
                }
            }
        }
        return lastResponseIdx;
    }

    private LogMaker newLogMaker(int category) {
        return newLogMaker(category, this.mService.getServicePackageName());
    }

    private LogMaker newLogMaker(int category, String servicePackageName) {
        return Helper.newLogMaker(category, this.mComponentName, servicePackageName, this.id, this.mCompatMode);
    }

    private void writeLog(int category) {
        this.mMetricsLogger.write(newLogMaker(category));
    }

    @GuardedBy({"mLock"})
    private void logAuthenticationStatusLocked(int requestId, int status) {
        addTaggedDataToRequestLogLocked(requestId, 1453, Integer.valueOf(status));
    }

    @GuardedBy({"mLock"})
    private void addTaggedDataToRequestLogLocked(int requestId, int tag, Object value) {
        LogMaker requestLog = this.mRequestLogs.get(requestId);
        if (requestLog == null) {
            Slog.w(TAG, "addTaggedDataToRequestLogLocked(tag=" + tag + "): no log for id " + requestId);
            return;
        }
        requestLog.addTaggedData(tag, value);
    }

    /* access modifiers changed from: private */
    public void wtf(Exception e, String fmt, Object... args) {
        String message = String.format(fmt, args);
        synchronized (this.mLock) {
            this.mWtfHistory.log(message);
        }
        if (e != null) {
            Slog.wtf(TAG, message, e);
        } else {
            Slog.wtf(TAG, message);
        }
    }

    private static String actionAsString(int action) {
        if (action == 1) {
            return "START_SESSION";
        }
        if (action == 2) {
            return "VIEW_ENTERED";
        }
        if (action == 3) {
            return "VIEW_EXITED";
        }
        if (action == 4) {
            return "VALUE_CHANGED";
        }
        return "UNKNOWN_" + action;
    }
}
