package com.android.server.autofill;

import android.app.ActivityManagerInternal;
import android.app.ActivityThread;
import android.content.AutofillOptions;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.UserInfo;
import android.database.ContentObserver;
import android.graphics.Rect;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.RemoteCallback;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.service.autofill.FillEventHistory;
import android.service.autofill.UserData;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.LocalLog;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.autofill.AutofillId;
import android.view.autofill.AutofillManager;
import android.view.autofill.AutofillManagerInternal;
import android.view.autofill.AutofillValue;
import android.view.autofill.Helper;
import android.view.autofill.IAutoFillManager;
import android.view.autofill.IAutoFillManagerClient;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.infra.GlobalWhitelistState;
import com.android.internal.os.IResultReceiver;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.Preconditions;
import com.android.internal.util.SyncResultReceiver;
import com.android.server.FgThread;
import com.android.server.LocalServices;
import com.android.server.autofill.ui.AutoFillUI;
import com.android.server.infra.AbstractMasterSystemService;
import com.android.server.infra.FrameworkResourcesServiceNameResolver;
import com.android.server.infra.SecureSettingsServiceNameResolver;
import com.android.server.infra.ServiceNameResolver;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class AutofillManagerService extends AbstractMasterSystemService<AutofillManagerService, AutofillManagerServiceImpl> {
    private static final char COMPAT_PACKAGE_DELIMITER = ':';
    private static final char COMPAT_PACKAGE_URL_IDS_BLOCK_BEGIN = '[';
    private static final char COMPAT_PACKAGE_URL_IDS_BLOCK_END = ']';
    private static final char COMPAT_PACKAGE_URL_IDS_DELIMITER = ',';
    private static final int DEFAULT_AUGMENTED_AUTOFILL_REQUEST_TIMEOUT_MILLIS = 5000;
    static final String RECEIVER_BUNDLE_EXTRA_SESSIONS = "sessions";
    private static final String TAG = "AutofillManagerService";
    private static final Object sLock = AutofillManagerService.class;
    /* access modifiers changed from: private */
    @GuardedBy({"sLock"})
    public static int sPartitionMaxCount = 10;
    /* access modifiers changed from: private */
    @GuardedBy({"sLock"})
    public static int sVisibleDatasetsMaxCount = 0;
    /* access modifiers changed from: private */
    public final ActivityManagerInternal mAm = ((ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class));
    final FrameworkResourcesServiceNameResolver mAugmentedAutofillResolver;
    final AugmentedAutofillState mAugmentedAutofillState = new AugmentedAutofillState();
    @GuardedBy({"mLock"})
    int mAugmentedServiceIdleUnbindTimeoutMs;
    @GuardedBy({"mLock"})
    int mAugmentedServiceRequestTimeoutMs;
    /* access modifiers changed from: private */
    public final AutofillCompatState mAutofillCompatState = new AutofillCompatState();
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.CLOSE_SYSTEM_DIALOGS".equals(intent.getAction())) {
                if (Helper.sDebug) {
                    Slog.d(AutofillManagerService.TAG, "Close system dialogs");
                }
                synchronized (AutofillManagerService.this.mLock) {
                    AutofillManagerService.this.visitServicesLocked($$Lambda$AutofillManagerService$1$1WNu3tTkxodB_LsZ7dGIlvrPN0.INSTANCE);
                }
                AutofillManagerService.this.mUi.hideAll((AutoFillUI.AutoFillUiCallback) null);
            }
        }
    };
    private final LocalService mLocalService = new LocalService();
    /* access modifiers changed from: private */
    public final LocalLog mRequestsHistory = new LocalLog(20);
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public int mSupportedSmartSuggestionModes;
    /* access modifiers changed from: private */
    public final AutoFillUI mUi = new AutoFillUI(ActivityThread.currentActivityThread().getSystemUiContext());
    /* access modifiers changed from: private */
    public final LocalLog mUiLatencyHistory = new LocalLog(20);
    /* access modifiers changed from: private */
    public final LocalLog mWtfHistory = new LocalLog(50);

    public AutofillManagerService(Context context) {
        super(context, new SecureSettingsServiceNameResolver(context, "autofill_service"), "no_autofill");
        DeviceConfig.addOnPropertiesChangedListener("autofill", ActivityThread.currentApplication().getMainExecutor(), new DeviceConfig.OnPropertiesChangedListener() {
            public final void onPropertiesChanged(DeviceConfig.Properties properties) {
                AutofillManagerService.this.lambda$new$0$AutofillManagerService(properties);
            }
        });
        setLogLevelFromSettings();
        setMaxPartitionsFromSettings();
        setMaxVisibleDatasetsFromSettings();
        setDeviceConfigProperties();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
        context.registerReceiver(this.mBroadcastReceiver, filter, (String) null, FgThread.getHandler());
        this.mAugmentedAutofillResolver = new FrameworkResourcesServiceNameResolver(getContext(), 17039723);
        this.mAugmentedAutofillResolver.setOnTemporaryServiceNameChangedCallback(new ServiceNameResolver.NameResolverListener() {
            public final void onNameResolved(int i, String str, boolean z) {
                AutofillManagerService.this.lambda$new$1$AutofillManagerService(i, str, z);
            }
        });
        if (this.mSupportedSmartSuggestionModes != 0) {
            List<UserInfo> users = ((UserManager) getContext().getSystemService(UserManager.class)).getUsers();
            for (int i = 0; i < users.size(); i++) {
                int userId = users.get(i).id;
                getServiceForUserLocked(userId);
                this.mAugmentedAutofillState.setServiceInfo(userId, this.mAugmentedAutofillResolver.getServiceName(userId), this.mAugmentedAutofillResolver.isTemporary(userId));
            }
        }
    }

    public /* synthetic */ void lambda$new$0$AutofillManagerService(DeviceConfig.Properties properties) {
        onDeviceConfigChange(properties.getKeyset());
    }

    /* access modifiers changed from: protected */
    public String getServiceSettingsProperty() {
        return "autofill_service";
    }

    /* access modifiers changed from: protected */
    public void registerForExtraSettingsChanges(ContentResolver resolver, ContentObserver observer) {
        resolver.registerContentObserver(Settings.Global.getUriFor("autofill_compat_mode_allowed_packages"), false, observer, -1);
        resolver.registerContentObserver(Settings.Global.getUriFor("autofill_logging_level"), false, observer, -1);
        resolver.registerContentObserver(Settings.Global.getUriFor("autofill_max_partitions_size"), false, observer, -1);
        resolver.registerContentObserver(Settings.Global.getUriFor("autofill_max_visible_datasets"), false, observer, -1);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onSettingsChanged(int r5, java.lang.String r6) {
        /*
            r4 = this;
            int r0 = r6.hashCode()
            r1 = 4
            r2 = 2
            r3 = 1
            switch(r0) {
                case -1848997872: goto L_0x0029;
                case -1299292969: goto L_0x001f;
                case -1048937777: goto L_0x0015;
                case 1670367536: goto L_0x000b;
                default: goto L_0x000a;
            }
        L_0x000a:
            goto L_0x0033
        L_0x000b:
            java.lang.String r0 = "autofill_compat_mode_allowed_packages"
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L_0x000a
            r0 = r1
            goto L_0x0034
        L_0x0015:
            java.lang.String r0 = "autofill_max_partitions_size"
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L_0x000a
            r0 = r3
            goto L_0x0034
        L_0x001f:
            java.lang.String r0 = "autofill_logging_level"
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L_0x000a
            r0 = 0
            goto L_0x0034
        L_0x0029:
            java.lang.String r0 = "autofill_max_visible_datasets"
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L_0x000a
            r0 = r2
            goto L_0x0034
        L_0x0033:
            r0 = -1
        L_0x0034:
            if (r0 == 0) goto L_0x006a
            if (r0 == r3) goto L_0x0066
            if (r0 == r2) goto L_0x0062
            if (r0 == r1) goto L_0x0057
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "Unexpected property ("
            r0.append(r1)
            r0.append(r6)
            java.lang.String r1 = "); updating cache instead"
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "AutofillManagerService"
            android.util.Slog.w(r1, r0)
        L_0x0057:
            java.lang.Object r0 = r4.mLock
            monitor-enter(r0)
            r4.updateCachedServiceLocked(r5)     // Catch:{ all -> 0x005f }
            monitor-exit(r0)     // Catch:{ all -> 0x005f }
            goto L_0x006e
        L_0x005f:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x005f }
            throw r1
        L_0x0062:
            r4.setMaxVisibleDatasetsFromSettings()
            goto L_0x006e
        L_0x0066:
            r4.setMaxPartitionsFromSettings()
            goto L_0x006e
        L_0x006a:
            r4.setLogLevelFromSettings()
        L_0x006e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.autofill.AutofillManagerService.onSettingsChanged(int, java.lang.String):void");
    }

    private void onDeviceConfigChange(Set<String> keys) {
        for (String key : keys) {
            char c = 65535;
            int hashCode = key.hashCode();
            if (hashCode != -1546842390) {
                if (hashCode != -987506216) {
                    if (hashCode == 1709136986 && key.equals("smart_suggestion_supported_modes")) {
                        c = 0;
                    }
                } else if (key.equals("augmented_service_request_timeout")) {
                    c = 2;
                }
            } else if (key.equals("augmented_service_idle_unbind_timeout")) {
                c = 1;
            }
            if (c == 0 || c == 1 || c == 2) {
                setDeviceConfigProperties();
            } else {
                Slog.i(this.mTag, "Ignoring change on " + key);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: onAugmentedServiceNameChanged */
    public void lambda$new$1$AutofillManagerService(int userId, String serviceName, boolean isTemporary) {
        this.mAugmentedAutofillState.setServiceInfo(userId, serviceName, isTemporary);
        synchronized (this.mLock) {
            ((AutofillManagerServiceImpl) getServiceForUserLocked(userId)).updateRemoteAugmentedAutofillService();
        }
    }

    /* access modifiers changed from: protected */
    public AutofillManagerServiceImpl newServiceLocked(int resolvedUserId, boolean disabled) {
        return new AutofillManagerServiceImpl(this, this.mLock, this.mUiLatencyHistory, this.mWtfHistory, resolvedUserId, this.mUi, this.mAutofillCompatState, disabled);
    }

    /* access modifiers changed from: protected */
    public void onServiceRemoved(AutofillManagerServiceImpl service, int userId) {
        service.destroyLocked();
        this.mAutofillCompatState.removeCompatibilityModeRequests(userId);
    }

    /* access modifiers changed from: protected */
    public void onServiceEnabledLocked(AutofillManagerServiceImpl service, int userId) {
        addCompatibilityModeRequestsLocked(service, userId);
    }

    /* access modifiers changed from: protected */
    public void enforceCallingPermissionForManagement() {
        getContext().enforceCallingPermission("android.permission.MANAGE_AUTO_FILL", TAG);
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [android.os.IBinder, com.android.server.autofill.AutofillManagerService$AutoFillManagerServiceStub] */
    public void onStart() {
        publishBinderService("autofill", new AutoFillManagerServiceStub());
        publishLocalService(AutofillManagerInternal.class, this.mLocalService);
    }

    public void onSwitchUser(int userHandle) {
        if (Helper.sDebug) {
            Slog.d(TAG, "Hiding UI when user switched");
        }
        this.mUi.hideAll((AutoFillUI.AutoFillUiCallback) null);
    }

    /* access modifiers changed from: package-private */
    public int getSupportedSmartSuggestionModesLocked() {
        return this.mSupportedSmartSuggestionModes;
    }

    /* access modifiers changed from: package-private */
    public void logRequestLocked(String historyItem) {
        this.mRequestsHistory.log(historyItem);
    }

    /* access modifiers changed from: package-private */
    public boolean isInstantServiceAllowed() {
        return this.mAllowInstantService;
    }

    /* access modifiers changed from: package-private */
    public void destroySessions(int userId, IResultReceiver receiver) {
        Slog.i(TAG, "destroySessions() for userId " + userId);
        enforceCallingPermissionForManagement();
        synchronized (this.mLock) {
            if (userId != -1) {
                AutofillManagerServiceImpl service = (AutofillManagerServiceImpl) peekServiceForUserLocked(userId);
                if (service != null) {
                    service.destroySessionsLocked();
                }
            } else {
                visitServicesLocked($$Lambda$AutofillManagerService$J4rMQC_cWRd6Td3UdzyhcfhT9xc.INSTANCE);
            }
        }
        try {
            receiver.send(0, new Bundle());
        } catch (RemoteException e) {
        }
    }

    /* access modifiers changed from: package-private */
    public void listSessions(int userId, IResultReceiver receiver) {
        Slog.i(TAG, "listSessions() for userId " + userId);
        enforceCallingPermissionForManagement();
        Bundle resultData = new Bundle();
        ArrayList<String> sessions = new ArrayList<>();
        synchronized (this.mLock) {
            if (userId != -1) {
                try {
                    AutofillManagerServiceImpl service = (AutofillManagerServiceImpl) peekServiceForUserLocked(userId);
                    if (service != null) {
                        service.listSessionsLocked(sessions);
                    }
                } catch (Throwable th) {
                    while (true) {
                        throw th;
                    }
                }
            } else {
                visitServicesLocked(new AbstractMasterSystemService.Visitor(sessions) {
                    private final /* synthetic */ ArrayList f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void visit(Object obj) {
                        ((AutofillManagerServiceImpl) obj).listSessionsLocked(this.f$0);
                    }
                });
            }
        }
        resultData.putStringArrayList(RECEIVER_BUNDLE_EXTRA_SESSIONS, sessions);
        try {
            receiver.send(0, resultData);
        } catch (RemoteException e) {
        }
    }

    /* access modifiers changed from: package-private */
    public void reset() {
        Slog.i(TAG, "reset()");
        enforceCallingPermissionForManagement();
        synchronized (this.mLock) {
            visitServicesLocked($$Lambda$AutofillManagerService$PR6iUwKxXatnzjgBDLARdxaGV3A.INSTANCE);
            clearCacheLocked();
        }
    }

    /* access modifiers changed from: package-private */
    public void setLogLevel(int level) {
        Slog.i(TAG, "setLogLevel(): " + level);
        enforceCallingPermissionForManagement();
        long token = Binder.clearCallingIdentity();
        try {
            Settings.Global.putInt(getContext().getContentResolver(), "autofill_logging_level", level);
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    private void setLogLevelFromSettings() {
        int level = Settings.Global.getInt(getContext().getContentResolver(), "autofill_logging_level", AutofillManager.DEFAULT_LOGGING_LEVEL);
        boolean debug = false;
        boolean verbose = false;
        if (level != 0) {
            if (level == 4) {
                verbose = true;
                debug = true;
            } else if (level == 2) {
                debug = true;
            } else {
                Slog.w(TAG, "setLogLevelFromSettings(): invalid level: " + level);
            }
        }
        if (debug || Helper.sDebug) {
            Slog.d(TAG, "setLogLevelFromSettings(): level=" + level + ", debug=" + debug + ", verbose=" + verbose);
        }
        synchronized (this.mLock) {
            setLoggingLevelsLocked(debug, verbose);
        }
    }

    /* access modifiers changed from: package-private */
    public int getLogLevel() {
        enforceCallingPermissionForManagement();
        synchronized (this.mLock) {
            if (Helper.sVerbose) {
                return 4;
            }
            if (Helper.sDebug) {
                return 2;
            }
            return 0;
        }
    }

    /* access modifiers changed from: package-private */
    public int getMaxPartitions() {
        int i;
        enforceCallingPermissionForManagement();
        synchronized (this.mLock) {
            i = sPartitionMaxCount;
        }
        return i;
    }

    /* access modifiers changed from: package-private */
    public void setMaxPartitions(int max) {
        Slog.i(TAG, "setMaxPartitions(): " + max);
        enforceCallingPermissionForManagement();
        long token = Binder.clearCallingIdentity();
        try {
            Settings.Global.putInt(getContext().getContentResolver(), "autofill_max_partitions_size", max);
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    private void setMaxPartitionsFromSettings() {
        int max = Settings.Global.getInt(getContext().getContentResolver(), "autofill_max_partitions_size", 10);
        if (Helper.sDebug) {
            Slog.d(TAG, "setMaxPartitionsFromSettings(): " + max);
        }
        synchronized (sLock) {
            sPartitionMaxCount = max;
        }
    }

    /* access modifiers changed from: package-private */
    public int getMaxVisibleDatasets() {
        int i;
        enforceCallingPermissionForManagement();
        synchronized (sLock) {
            i = sVisibleDatasetsMaxCount;
        }
        return i;
    }

    /* access modifiers changed from: package-private */
    public void setMaxVisibleDatasets(int max) {
        Slog.i(TAG, "setMaxVisibleDatasets(): " + max);
        enforceCallingPermissionForManagement();
        long token = Binder.clearCallingIdentity();
        try {
            Settings.Global.putInt(getContext().getContentResolver(), "autofill_max_visible_datasets", max);
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    private void setMaxVisibleDatasetsFromSettings() {
        int max = Settings.Global.getInt(getContext().getContentResolver(), "autofill_max_visible_datasets", 0);
        if (Helper.sDebug) {
            Slog.d(TAG, "setMaxVisibleDatasetsFromSettings(): " + max);
        }
        synchronized (sLock) {
            sVisibleDatasetsMaxCount = max;
        }
    }

    private void setDeviceConfigProperties() {
        synchronized (this.mLock) {
            this.mAugmentedServiceIdleUnbindTimeoutMs = DeviceConfig.getInt("autofill", "augmented_service_idle_unbind_timeout", 0);
            this.mAugmentedServiceRequestTimeoutMs = DeviceConfig.getInt("autofill", "augmented_service_request_timeout", DEFAULT_AUGMENTED_AUTOFILL_REQUEST_TIMEOUT_MILLIS);
            this.mSupportedSmartSuggestionModes = DeviceConfig.getInt("autofill", "smart_suggestion_supported_modes", 1);
            if (this.verbose) {
                String str = this.mTag;
                Slog.v(str, "setDeviceConfigProperties(): augmentedIdleTimeout=" + this.mAugmentedServiceIdleUnbindTimeoutMs + ", augmentedRequestTimeout=" + this.mAugmentedServiceRequestTimeoutMs + ", smartSuggestionMode=" + AutofillManager.getSmartSuggestionModeToString(this.mSupportedSmartSuggestionModes));
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void calculateScore(String algorithmName, String value1, String value2, RemoteCallback callback) {
        enforceCallingPermissionForManagement();
        RemoteCallback remoteCallback = callback;
        new FieldClassificationStrategy(getContext(), -2).calculateScores(remoteCallback, Arrays.asList(new AutofillValue[]{AutofillValue.forText(value1)}), new String[]{value2}, new String[]{null}, algorithmName, (Bundle) null, (ArrayMap<String, String>) null, (ArrayMap<String, Bundle>) null);
    }

    /* access modifiers changed from: package-private */
    public Boolean getFullScreenMode() {
        enforceCallingPermissionForManagement();
        return Helper.sFullScreenMode;
    }

    /* access modifiers changed from: package-private */
    public void setFullScreenMode(Boolean mode) {
        enforceCallingPermissionForManagement();
        Helper.sFullScreenMode = mode;
    }

    /* access modifiers changed from: package-private */
    public void setTemporaryAugmentedAutofillService(int userId, String serviceName, int durationMs) {
        String str = this.mTag;
        Slog.i(str, "setTemporaryAugmentedAutofillService(" + userId + ") to " + serviceName + " for " + durationMs + "ms");
        enforceCallingPermissionForManagement();
        Preconditions.checkNotNull(serviceName);
        if (durationMs <= 120000) {
            this.mAugmentedAutofillResolver.setTemporaryService(userId, serviceName, durationMs);
            return;
        }
        throw new IllegalArgumentException("Max duration is 120000 (called with " + durationMs + ")");
    }

    /* access modifiers changed from: package-private */
    public void resetTemporaryAugmentedAutofillService(int userId) {
        enforceCallingPermissionForManagement();
        this.mAugmentedAutofillResolver.resetTemporaryService(userId);
    }

    /* access modifiers changed from: package-private */
    public boolean isDefaultAugmentedServiceEnabled(int userId) {
        enforceCallingPermissionForManagement();
        return this.mAugmentedAutofillResolver.isDefaultServiceEnabled(userId);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0057, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean setDefaultAugmentedServiceEnabled(int r7, boolean r8) {
        /*
            r6 = this;
            java.lang.String r0 = r6.mTag
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "setDefaultAugmentedServiceEnabled() for userId "
            r1.append(r2)
            r1.append(r7)
            java.lang.String r2 = ": "
            r1.append(r2)
            r1.append(r8)
            java.lang.String r1 = r1.toString()
            android.util.Slog.i(r0, r1)
            r6.enforceCallingPermissionForManagement()
            java.lang.Object r0 = r6.mLock
            monitor-enter(r0)
            com.android.server.infra.AbstractPerUserSystemService r1 = r6.getServiceForUserLocked(r7)     // Catch:{ all -> 0x0059 }
            com.android.server.autofill.AutofillManagerServiceImpl r1 = (com.android.server.autofill.AutofillManagerServiceImpl) r1     // Catch:{ all -> 0x0059 }
            if (r1 == 0) goto L_0x0056
            com.android.server.infra.FrameworkResourcesServiceNameResolver r2 = r6.mAugmentedAutofillResolver     // Catch:{ all -> 0x0059 }
            boolean r2 = r2.setDefaultServiceEnabled(r7, r8)     // Catch:{ all -> 0x0059 }
            if (r2 == 0) goto L_0x003b
            r1.updateRemoteAugmentedAutofillService()     // Catch:{ all -> 0x0059 }
            r3 = 1
            monitor-exit(r0)     // Catch:{ all -> 0x0059 }
            return r3
        L_0x003b:
            boolean r3 = r6.debug     // Catch:{ all -> 0x0059 }
            if (r3 == 0) goto L_0x0056
            java.lang.String r3 = "AutofillManagerService"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0059 }
            r4.<init>()     // Catch:{ all -> 0x0059 }
            java.lang.String r5 = "setDefaultAugmentedServiceEnabled(): already "
            r4.append(r5)     // Catch:{ all -> 0x0059 }
            r4.append(r8)     // Catch:{ all -> 0x0059 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0059 }
            android.util.Slog.d(r3, r4)     // Catch:{ all -> 0x0059 }
        L_0x0056:
            monitor-exit(r0)     // Catch:{ all -> 0x0059 }
            r0 = 0
            return r0
        L_0x0059:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0059 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.autofill.AutofillManagerService.setDefaultAugmentedServiceEnabled(int, boolean):boolean");
    }

    private void setLoggingLevelsLocked(boolean debug, boolean verbose) {
        Helper.sDebug = debug;
        Helper.sDebug = debug;
        this.debug = debug;
        Helper.sVerbose = verbose;
        Helper.sVerbose = verbose;
        this.verbose = verbose;
    }

    private void addCompatibilityModeRequestsLocked(AutofillManagerServiceImpl service, int userId) {
        this.mAutofillCompatState.reset(userId);
        ArrayMap<String, Long> compatPackages = service.getCompatibilityPackagesLocked();
        if (compatPackages != null && !compatPackages.isEmpty()) {
            Map<String, String[]> whiteListedPackages = getWhitelistedCompatModePackages();
            int compatPackageCount = compatPackages.size();
            for (int i = 0; i < compatPackageCount; i++) {
                String packageName = compatPackages.keyAt(i);
                if (whiteListedPackages == null || !whiteListedPackages.containsKey(packageName)) {
                    Slog.w(TAG, "Ignoring not whitelisted compat package " + packageName);
                } else {
                    Long maxVersionCode = compatPackages.valueAt(i);
                    if (maxVersionCode != null) {
                        this.mAutofillCompatState.addCompatibilityModeRequest(packageName, maxVersionCode.longValue(), whiteListedPackages.get(packageName), userId);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public String getWhitelistedCompatModePackagesFromSettings() {
        return Settings.Global.getString(getContext().getContentResolver(), "autofill_compat_mode_allowed_packages");
    }

    private Map<String, String[]> getWhitelistedCompatModePackages() {
        return getWhitelistedCompatModePackages(getWhitelistedCompatModePackagesFromSettings());
    }

    /* access modifiers changed from: private */
    public void send(IResultReceiver receiver, int value) {
        try {
            receiver.send(value, (Bundle) null);
        } catch (RemoteException e) {
            Slog.w(TAG, "Error async reporting result to client: " + e);
        }
    }

    private void send(IResultReceiver receiver, Bundle value) {
        try {
            receiver.send(0, value);
        } catch (RemoteException e) {
            Slog.w(TAG, "Error async reporting result to client: " + e);
        }
    }

    /* access modifiers changed from: private */
    public void send(IResultReceiver receiver, String value) {
        send(receiver, SyncResultReceiver.bundleFor(value));
    }

    /* access modifiers changed from: private */
    public void send(IResultReceiver receiver, String[] value) {
        send(receiver, SyncResultReceiver.bundleFor(value));
    }

    /* access modifiers changed from: private */
    public void send(IResultReceiver receiver, Parcelable value) {
        send(receiver, SyncResultReceiver.bundleFor(value));
    }

    /* access modifiers changed from: private */
    public void send(IResultReceiver receiver, boolean value) {
        send(receiver, (int) value);
    }

    /* access modifiers changed from: private */
    public void send(IResultReceiver receiver, int value1, int value2) {
        try {
            receiver.send(value1, SyncResultReceiver.bundleFor(value2));
        } catch (RemoteException e) {
            Slog.w(TAG, "Error async reporting result to client: " + e);
        }
    }

    @VisibleForTesting
    static Map<String, String[]> getWhitelistedCompatModePackages(String setting) {
        List<String> urlBarIds;
        String packageName;
        if (TextUtils.isEmpty(setting)) {
            return null;
        }
        ArrayMap<String, String[]> compatPackages = new ArrayMap<>();
        TextUtils.SimpleStringSplitter splitter = new TextUtils.SimpleStringSplitter(COMPAT_PACKAGE_DELIMITER);
        splitter.setString(setting);
        while (splitter.hasNext()) {
            String packageBlock = splitter.next();
            int urlBlockIndex = packageBlock.indexOf(91);
            if (urlBlockIndex == -1) {
                packageName = packageBlock;
                urlBarIds = null;
            } else if (packageBlock.charAt(packageBlock.length() - 1) != ']') {
                Slog.w(TAG, "Ignoring entry '" + packageBlock + "' on '" + setting + "'because it does not end on '" + COMPAT_PACKAGE_URL_IDS_BLOCK_END + "'");
            } else {
                packageName = packageBlock.substring(0, urlBlockIndex);
                List<String> urlBarIds2 = new ArrayList<>();
                String urlBarIdsBlock = packageBlock.substring(urlBlockIndex + 1, packageBlock.length() - 1);
                if (Helper.sVerbose) {
                    Slog.v(TAG, "pkg:" + packageName + ": block:" + packageBlock + ": urls:" + urlBarIds2 + ": block:" + urlBarIdsBlock + ":");
                }
                TextUtils.SimpleStringSplitter splitter2 = new TextUtils.SimpleStringSplitter(COMPAT_PACKAGE_URL_IDS_DELIMITER);
                splitter2.setString(urlBarIdsBlock);
                while (splitter2.hasNext()) {
                    urlBarIds2.add(splitter2.next());
                }
                urlBarIds = urlBarIds2;
            }
            if (urlBarIds == null) {
                compatPackages.put(packageName, (Object) null);
            } else {
                String[] urlBarIdsArray = new String[urlBarIds.size()];
                urlBarIds.toArray(urlBarIdsArray);
                compatPackages.put(packageName, urlBarIdsArray);
            }
        }
        return compatPackages;
    }

    public static int getPartitionMaxCount() {
        int i;
        synchronized (sLock) {
            i = sPartitionMaxCount;
        }
        return i;
    }

    public static int getVisibleDatasetsMaxCount() {
        int i;
        synchronized (sLock) {
            i = sVisibleDatasetsMaxCount;
        }
        return i;
    }

    private final class LocalService extends AutofillManagerInternal {
        private LocalService() {
        }

        public void onBackKeyPressed() {
            if (Helper.sDebug) {
                Slog.d(AutofillManagerService.TAG, "onBackKeyPressed()");
            }
            AutofillManagerService.this.mUi.hideAll((AutoFillUI.AutoFillUiCallback) null);
            synchronized (AutofillManagerService.this.mLock) {
                ((AutofillManagerServiceImpl) AutofillManagerService.this.getServiceForUserLocked(UserHandle.getCallingUserId())).onBackKeyPressed();
            }
        }

        public AutofillOptions getAutofillOptions(String packageName, long versionCode, int userId) {
            int loggingLevel;
            if (AutofillManagerService.this.verbose) {
                loggingLevel = 6;
            } else if (AutofillManagerService.this.debug) {
                loggingLevel = 2;
            } else {
                loggingLevel = 0;
            }
            AutofillOptions options = new AutofillOptions(loggingLevel, AutofillManagerService.this.mAutofillCompatState.isCompatibilityModeRequested(packageName, versionCode, userId));
            AutofillManagerService.this.mAugmentedAutofillState.injectAugmentedAutofillInfo(options, userId, packageName);
            return options;
        }

        public boolean isAugmentedAutofillServiceForUser(int callingUid, int userId) {
            synchronized (AutofillManagerService.this.mLock) {
                AutofillManagerServiceImpl service = (AutofillManagerServiceImpl) AutofillManagerService.this.peekServiceForUserLocked(userId);
                if (service == null) {
                    return false;
                }
                boolean isAugmentedAutofillServiceForUserLocked = service.isAugmentedAutofillServiceForUserLocked(callingUid);
                return isAugmentedAutofillServiceForUserLocked;
            }
        }
    }

    static final class PackageCompatState {
        /* access modifiers changed from: private */
        public final long maxVersionCode;
        /* access modifiers changed from: private */
        public final String[] urlBarResourceIds;

        PackageCompatState(long maxVersionCode2, String[] urlBarResourceIds2) {
            this.maxVersionCode = maxVersionCode2;
            this.urlBarResourceIds = urlBarResourceIds2;
        }

        public String toString() {
            return "maxVersionCode=" + this.maxVersionCode + ", urlBarResourceIds=" + Arrays.toString(this.urlBarResourceIds);
        }
    }

    static final class AutofillCompatState {
        private final Object mLock = new Object();
        @GuardedBy({"mLock"})
        private SparseArray<ArrayMap<String, PackageCompatState>> mUserSpecs;

        AutofillCompatState() {
        }

        /* access modifiers changed from: package-private */
        /* JADX WARNING: Code restructure failed: missing block: B:19:0x002a, code lost:
            return r2;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean isCompatibilityModeRequested(java.lang.String r7, long r8, int r10) {
            /*
                r6 = this;
                java.lang.Object r0 = r6.mLock
                monitor-enter(r0)
                android.util.SparseArray<android.util.ArrayMap<java.lang.String, com.android.server.autofill.AutofillManagerService$PackageCompatState>> r1 = r6.mUserSpecs     // Catch:{ all -> 0x002b }
                r2 = 0
                if (r1 != 0) goto L_0x000a
                monitor-exit(r0)     // Catch:{ all -> 0x002b }
                return r2
            L_0x000a:
                android.util.SparseArray<android.util.ArrayMap<java.lang.String, com.android.server.autofill.AutofillManagerService$PackageCompatState>> r1 = r6.mUserSpecs     // Catch:{ all -> 0x002b }
                java.lang.Object r1 = r1.get(r10)     // Catch:{ all -> 0x002b }
                android.util.ArrayMap r1 = (android.util.ArrayMap) r1     // Catch:{ all -> 0x002b }
                if (r1 != 0) goto L_0x0016
                monitor-exit(r0)     // Catch:{ all -> 0x002b }
                return r2
            L_0x0016:
                java.lang.Object r3 = r1.get(r7)     // Catch:{ all -> 0x002b }
                com.android.server.autofill.AutofillManagerService$PackageCompatState r3 = (com.android.server.autofill.AutofillManagerService.PackageCompatState) r3     // Catch:{ all -> 0x002b }
                if (r3 != 0) goto L_0x0020
                monitor-exit(r0)     // Catch:{ all -> 0x002b }
                return r2
            L_0x0020:
                long r4 = r3.maxVersionCode     // Catch:{ all -> 0x002b }
                int r4 = (r8 > r4 ? 1 : (r8 == r4 ? 0 : -1))
                if (r4 > 0) goto L_0x0029
                r2 = 1
            L_0x0029:
                monitor-exit(r0)     // Catch:{ all -> 0x002b }
                return r2
            L_0x002b:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x002b }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.autofill.AutofillManagerService.AutofillCompatState.isCompatibilityModeRequested(java.lang.String, long, int):boolean");
        }

        /* access modifiers changed from: package-private */
        public String[] getUrlBarResourceIds(String packageName, int userId) {
            synchronized (this.mLock) {
                if (this.mUserSpecs == null) {
                    return null;
                }
                ArrayMap<String, PackageCompatState> userSpec = this.mUserSpecs.get(userId);
                if (userSpec == null) {
                    return null;
                }
                PackageCompatState metadata = userSpec.get(packageName);
                if (metadata == null) {
                    return null;
                }
                String[] access$1100 = metadata.urlBarResourceIds;
                return access$1100;
            }
        }

        /* access modifiers changed from: package-private */
        public void addCompatibilityModeRequest(String packageName, long versionCode, String[] urlBarResourceIds, int userId) {
            synchronized (this.mLock) {
                if (this.mUserSpecs == null) {
                    this.mUserSpecs = new SparseArray<>();
                }
                ArrayMap<String, PackageCompatState> userSpec = this.mUserSpecs.get(userId);
                if (userSpec == null) {
                    userSpec = new ArrayMap<>();
                    this.mUserSpecs.put(userId, userSpec);
                }
                userSpec.put(packageName, new PackageCompatState(versionCode, urlBarResourceIds));
            }
        }

        /* access modifiers changed from: package-private */
        public void removeCompatibilityModeRequests(int userId) {
            synchronized (this.mLock) {
                if (this.mUserSpecs != null) {
                    this.mUserSpecs.remove(userId);
                    if (this.mUserSpecs.size() <= 0) {
                        this.mUserSpecs = null;
                    }
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void reset(int userId) {
            synchronized (this.mLock) {
                if (this.mUserSpecs != null) {
                    this.mUserSpecs.delete(userId);
                    int newSize = this.mUserSpecs.size();
                    if (newSize == 0) {
                        if (Helper.sVerbose) {
                            Slog.v(AutofillManagerService.TAG, "reseting mUserSpecs");
                        }
                        this.mUserSpecs = null;
                    } else if (Helper.sVerbose) {
                        Slog.v(AutofillManagerService.TAG, "mUserSpecs down to " + newSize);
                    }
                }
            }
        }

        /* access modifiers changed from: private */
        public void dump(String prefix, PrintWriter pw) {
            synchronized (this.mLock) {
                if (this.mUserSpecs == null) {
                    pw.println("N/A");
                    return;
                }
                pw.println();
                String prefix2 = prefix + "  ";
                for (int i = 0; i < this.mUserSpecs.size(); i++) {
                    int user = this.mUserSpecs.keyAt(i);
                    pw.print(prefix);
                    pw.print("User: ");
                    pw.println(user);
                    ArrayMap<String, PackageCompatState> perUser = this.mUserSpecs.valueAt(i);
                    for (int j = 0; j < perUser.size(); j++) {
                        pw.print(prefix2);
                        pw.print(perUser.keyAt(j));
                        pw.print(": ");
                        pw.println(perUser.valueAt(j));
                    }
                }
            }
        }
    }

    static final class AugmentedAutofillState extends GlobalWhitelistState {
        @GuardedBy({"mGlobalWhitelistStateLock"})
        private final SparseArray<String> mServicePackages = new SparseArray<>();
        @GuardedBy({"mGlobalWhitelistStateLock"})
        private final SparseBooleanArray mTemporaryServices = new SparseBooleanArray();

        AugmentedAutofillState() {
        }

        /* access modifiers changed from: private */
        public void setServiceInfo(int userId, String serviceName, boolean isTemporary) {
            synchronized (this.mGlobalWhitelistStateLock) {
                if (isTemporary) {
                    this.mTemporaryServices.put(userId, true);
                } else {
                    this.mTemporaryServices.delete(userId);
                }
                if (serviceName != null) {
                    ComponentName componentName = ComponentName.unflattenFromString(serviceName);
                    if (componentName == null) {
                        Slog.w(AutofillManagerService.TAG, "setServiceInfo(): invalid name: " + serviceName);
                        this.mServicePackages.remove(userId);
                    } else {
                        this.mServicePackages.put(userId, componentName.getPackageName());
                    }
                } else {
                    this.mServicePackages.remove(userId);
                }
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:11:0x0021, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void injectAugmentedAutofillInfo(android.content.AutofillOptions r4, int r5, java.lang.String r6) {
            /*
                r3 = this;
                java.lang.Object r0 = r3.mGlobalWhitelistStateLock
                monitor-enter(r0)
                android.util.SparseArray r1 = r3.mWhitelisterHelpers     // Catch:{ all -> 0x0022 }
                if (r1 != 0) goto L_0x0009
                monitor-exit(r0)     // Catch:{ all -> 0x0022 }
                return
            L_0x0009:
                android.util.SparseArray r1 = r3.mWhitelisterHelpers     // Catch:{ all -> 0x0022 }
                java.lang.Object r1 = r1.get(r5)     // Catch:{ all -> 0x0022 }
                com.android.internal.infra.WhitelistHelper r1 = (com.android.internal.infra.WhitelistHelper) r1     // Catch:{ all -> 0x0022 }
                if (r1 == 0) goto L_0x0020
                boolean r2 = r1.isWhitelisted(r6)     // Catch:{ all -> 0x0022 }
                r4.augmentedAutofillEnabled = r2     // Catch:{ all -> 0x0022 }
                android.util.ArraySet r2 = r1.getWhitelistedComponents(r6)     // Catch:{ all -> 0x0022 }
                r4.whitelistedActivitiesForAugmentedAutofill = r2     // Catch:{ all -> 0x0022 }
            L_0x0020:
                monitor-exit(r0)     // Catch:{ all -> 0x0022 }
                return
            L_0x0022:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0022 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.autofill.AutofillManagerService.AugmentedAutofillState.injectAugmentedAutofillInfo(android.content.AutofillOptions, int, java.lang.String):void");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:17:0x0051, code lost:
            return true;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean isWhitelisted(int r7, android.content.ComponentName r8) {
            /*
                r6 = this;
                java.lang.Object r0 = r6.mGlobalWhitelistStateLock
                monitor-enter(r0)
                boolean r1 = com.android.server.autofill.AutofillManagerService.super.isWhitelisted(r7, r8)     // Catch:{ all -> 0x0053 }
                r2 = 0
                if (r1 != 0) goto L_0x000c
                monitor-exit(r0)     // Catch:{ all -> 0x0053 }
                return r2
            L_0x000c:
                boolean r1 = android.os.Build.IS_USER     // Catch:{ all -> 0x0053 }
                if (r1 == 0) goto L_0x0050
                android.util.SparseBooleanArray r1 = r6.mTemporaryServices     // Catch:{ all -> 0x0053 }
                boolean r1 = r1.get(r7)     // Catch:{ all -> 0x0053 }
                if (r1 == 0) goto L_0x0050
                java.lang.String r1 = r8.getPackageName()     // Catch:{ all -> 0x0053 }
                android.util.SparseArray<java.lang.String> r3 = r6.mServicePackages     // Catch:{ all -> 0x0053 }
                java.lang.Object r3 = r3.get(r7)     // Catch:{ all -> 0x0053 }
                boolean r3 = r1.equals(r3)     // Catch:{ all -> 0x0053 }
                if (r3 != 0) goto L_0x0050
                java.lang.String r3 = "AutofillManagerService"
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0053 }
                r4.<init>()     // Catch:{ all -> 0x0053 }
                java.lang.String r5 = "Ignoring package "
                r4.append(r5)     // Catch:{ all -> 0x0053 }
                r4.append(r1)     // Catch:{ all -> 0x0053 }
                java.lang.String r5 = " for augmented autofill while using temporary service "
                r4.append(r5)     // Catch:{ all -> 0x0053 }
                android.util.SparseArray<java.lang.String> r5 = r6.mServicePackages     // Catch:{ all -> 0x0053 }
                java.lang.Object r5 = r5.get(r7)     // Catch:{ all -> 0x0053 }
                java.lang.String r5 = (java.lang.String) r5     // Catch:{ all -> 0x0053 }
                r4.append(r5)     // Catch:{ all -> 0x0053 }
                java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0053 }
                android.util.Slog.w(r3, r4)     // Catch:{ all -> 0x0053 }
                monitor-exit(r0)     // Catch:{ all -> 0x0053 }
                return r2
            L_0x0050:
                monitor-exit(r0)     // Catch:{ all -> 0x0053 }
                r0 = 1
                return r0
            L_0x0053:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0053 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.autofill.AutofillManagerService.AugmentedAutofillState.isWhitelisted(int, android.content.ComponentName):boolean");
        }

        public void dump(String prefix, PrintWriter pw) {
            AutofillManagerService.super.dump(prefix, pw);
            synchronized (this.mGlobalWhitelistStateLock) {
                if (this.mServicePackages.size() > 0) {
                    pw.print(prefix);
                    pw.print("Service packages: ");
                    pw.println(this.mServicePackages);
                }
                if (this.mTemporaryServices.size() > 0) {
                    pw.print(prefix);
                    pw.print("Temp services: ");
                    pw.println(this.mTemporaryServices);
                }
            }
        }
    }

    final class AutoFillManagerServiceStub extends IAutoFillManager.Stub {
        AutoFillManagerServiceStub() {
        }

        public void addClient(IAutoFillManagerClient client, ComponentName componentName, int userId, IResultReceiver receiver) {
            int flags = 0;
            synchronized (AutofillManagerService.this.mLock) {
                int enabledFlags = ((AutofillManagerServiceImpl) AutofillManagerService.this.getServiceForUserLocked(userId)).addClientLocked(client, componentName);
                if (enabledFlags != 0) {
                    flags = 0 | enabledFlags;
                }
                if (Helper.sDebug) {
                    flags |= 2;
                }
                if (Helper.sVerbose) {
                    flags |= 4;
                }
            }
            AutofillManagerService.this.send(receiver, flags);
        }

        public void removeClient(IAutoFillManagerClient client, int userId) {
            synchronized (AutofillManagerService.this.mLock) {
                AutofillManagerServiceImpl service = (AutofillManagerServiceImpl) AutofillManagerService.this.peekServiceForUserLocked(userId);
                if (service != null) {
                    service.removeClientLocked(client);
                } else if (Helper.sVerbose) {
                    Slog.v(AutofillManagerService.TAG, "removeClient(): no service for " + userId);
                }
            }
        }

        public void setAuthenticationResult(Bundle data, int sessionId, int authenticationId, int userId) {
            synchronized (AutofillManagerService.this.mLock) {
                ((AutofillManagerServiceImpl) AutofillManagerService.this.getServiceForUserLocked(userId)).setAuthenticationResultLocked(data, sessionId, authenticationId, getCallingUid());
            }
        }

        public void setHasCallback(int sessionId, int userId, boolean hasIt) {
            synchronized (AutofillManagerService.this.mLock) {
                ((AutofillManagerServiceImpl) AutofillManagerService.this.getServiceForUserLocked(userId)).setHasCallback(sessionId, getCallingUid(), hasIt);
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:15:0x00a3, code lost:
            r0 = (int) r4;
            r6 = (int) (r4 >> 32);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:16:0x00a9, code lost:
            if (r6 == 0) goto L_0x00b1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:17:0x00ab, code lost:
            com.android.server.autofill.AutofillManagerService.access$2500(r1.this$0, r3, r0, r6);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:18:0x00b1, code lost:
            com.android.server.autofill.AutofillManagerService.access$1400(r1.this$0, r3, r0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:28:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:29:?, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void startSession(android.os.IBinder r24, android.os.IBinder r25, android.view.autofill.AutofillId r26, android.graphics.Rect r27, android.view.autofill.AutofillValue r28, int r29, boolean r30, int r31, android.content.ComponentName r32, boolean r33, com.android.internal.os.IResultReceiver r34) {
            /*
                r23 = this;
                r1 = r23
                r2 = r29
                r3 = r34
                java.lang.String r0 = "activityToken"
                r4 = r24
                java.lang.Object r0 = com.android.internal.util.Preconditions.checkNotNull(r4, r0)
                r15 = r0
                android.os.IBinder r15 = (android.os.IBinder) r15
                java.lang.String r0 = "appCallback"
                r4 = r25
                java.lang.Object r0 = com.android.internal.util.Preconditions.checkNotNull(r4, r0)
                r17 = r0
                android.os.IBinder r17 = (android.os.IBinder) r17
                java.lang.String r0 = "autoFillId"
                r4 = r26
                java.lang.Object r0 = com.android.internal.util.Preconditions.checkNotNull(r4, r0)
                r18 = r0
                android.view.autofill.AutofillId r18 = (android.view.autofill.AutofillId) r18
                java.lang.String r0 = "componentName"
                r4 = r32
                java.lang.Object r0 = com.android.internal.util.Preconditions.checkNotNull(r4, r0)
                r19 = r0
                android.content.ComponentName r19 = (android.content.ComponentName) r19
                java.lang.String r0 = r19.getPackageName()
                java.lang.Object r0 = com.android.internal.util.Preconditions.checkNotNull(r0)
                r14 = r0
                java.lang.String r14 = (java.lang.String) r14
                int r0 = getCallingUid()
                int r0 = android.os.UserHandle.getUserId(r0)
                r4 = 0
                if (r2 != r0) goto L_0x004d
                r0 = 1
                goto L_0x004e
            L_0x004d:
                r0 = r4
            L_0x004e:
                java.lang.String r5 = "userId"
                com.android.internal.util.Preconditions.checkArgument(r0, r5)
                com.android.server.autofill.AutofillManagerService r0 = com.android.server.autofill.AutofillManagerService.this     // Catch:{ NameNotFoundException -> 0x00bf }
                android.content.Context r0 = r0.getContext()     // Catch:{ NameNotFoundException -> 0x00bf }
                android.content.pm.PackageManager r0 = r0.getPackageManager()     // Catch:{ NameNotFoundException -> 0x00bf }
                r0.getPackageInfoAsUser(r14, r4, r2)     // Catch:{ NameNotFoundException -> 0x00bf }
                com.android.server.autofill.AutofillManagerService r0 = com.android.server.autofill.AutofillManagerService.this
                android.app.ActivityManagerInternal r0 = r0.mAm
                int r20 = r0.getTaskIdForActivity(r15, r4)
                com.android.server.autofill.AutofillManagerService r0 = com.android.server.autofill.AutofillManagerService.this
                java.lang.Object r21 = r0.mLock
                monitor-enter(r21)
                com.android.server.autofill.AutofillManagerService r0 = com.android.server.autofill.AutofillManagerService.this     // Catch:{ all -> 0x00b7 }
                com.android.server.infra.AbstractPerUserSystemService r0 = r0.getServiceForUserLocked(r2)     // Catch:{ all -> 0x00b7 }
                r4 = r0
                com.android.server.autofill.AutofillManagerServiceImpl r4 = (com.android.server.autofill.AutofillManagerServiceImpl) r4     // Catch:{ all -> 0x00b7 }
                int r7 = getCallingUid()     // Catch:{ all -> 0x00b7 }
                com.android.server.autofill.AutofillManagerService r0 = com.android.server.autofill.AutofillManagerService.this     // Catch:{ all -> 0x00b7 }
                boolean r0 = r0.mAllowInstantService     // Catch:{ all -> 0x00b7 }
                r5 = r15
                r6 = r20
                r8 = r17
                r9 = r18
                r10 = r27
                r11 = r28
                r12 = r30
                r13 = r19
                r2 = r14
                r14 = r33
                r22 = r15
                r15 = r0
                r16 = r31
                long r5 = r4.startSessionLocked(r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16)     // Catch:{ all -> 0x00bd }
                r4 = r5
                monitor-exit(r21)     // Catch:{ all -> 0x00bd }
                int r0 = (int) r4
                r6 = 32
                long r6 = r4 >> r6
                int r6 = (int) r6
                if (r6 == 0) goto L_0x00b1
                com.android.server.autofill.AutofillManagerService r7 = com.android.server.autofill.AutofillManagerService.this
                r7.send(r3, r0, r6)
                goto L_0x00b6
            L_0x00b1:
                com.android.server.autofill.AutofillManagerService r7 = com.android.server.autofill.AutofillManagerService.this
                r7.send((com.android.internal.os.IResultReceiver) r3, (int) r0)
            L_0x00b6:
                return
            L_0x00b7:
                r0 = move-exception
                r2 = r14
                r22 = r15
            L_0x00bb:
                monitor-exit(r21)     // Catch:{ all -> 0x00bd }
                throw r0
            L_0x00bd:
                r0 = move-exception
                goto L_0x00bb
            L_0x00bf:
                r0 = move-exception
                r2 = r14
                r22 = r15
                java.lang.IllegalArgumentException r4 = new java.lang.IllegalArgumentException
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                r5.append(r2)
                java.lang.String r6 = " is not a valid package"
                r5.append(r6)
                java.lang.String r5 = r5.toString()
                r4.<init>(r5, r0)
                throw r4
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.autofill.AutofillManagerService.AutoFillManagerServiceStub.startSession(android.os.IBinder, android.os.IBinder, android.view.autofill.AutofillId, android.graphics.Rect, android.view.autofill.AutofillValue, int, boolean, int, android.content.ComponentName, boolean, com.android.internal.os.IResultReceiver):void");
        }

        public void getFillEventHistory(IResultReceiver receiver) throws RemoteException {
            int userId = UserHandle.getCallingUserId();
            FillEventHistory fillEventHistory = null;
            synchronized (AutofillManagerService.this.mLock) {
                AutofillManagerServiceImpl service = (AutofillManagerServiceImpl) AutofillManagerService.this.peekServiceForUserLocked(userId);
                if (service != null) {
                    fillEventHistory = service.getFillEventHistory(getCallingUid());
                } else if (Helper.sVerbose) {
                    Slog.v(AutofillManagerService.TAG, "getFillEventHistory(): no service for " + userId);
                }
            }
            AutofillManagerService.this.send(receiver, (Parcelable) fillEventHistory);
        }

        public void getUserData(IResultReceiver receiver) throws RemoteException {
            int userId = UserHandle.getCallingUserId();
            UserData userData = null;
            synchronized (AutofillManagerService.this.mLock) {
                AutofillManagerServiceImpl service = (AutofillManagerServiceImpl) AutofillManagerService.this.peekServiceForUserLocked(userId);
                if (service != null) {
                    userData = service.getUserData(getCallingUid());
                } else if (Helper.sVerbose) {
                    Slog.v(AutofillManagerService.TAG, "getUserData(): no service for " + userId);
                }
            }
            AutofillManagerService.this.send(receiver, (Parcelable) userData);
        }

        public void getUserDataId(IResultReceiver receiver) throws RemoteException {
            int userId = UserHandle.getCallingUserId();
            UserData userData = null;
            synchronized (AutofillManagerService.this.mLock) {
                AutofillManagerServiceImpl service = (AutofillManagerServiceImpl) AutofillManagerService.this.peekServiceForUserLocked(userId);
                if (service != null) {
                    userData = service.getUserData(getCallingUid());
                } else if (Helper.sVerbose) {
                    Slog.v(AutofillManagerService.TAG, "getUserDataId(): no service for " + userId);
                }
            }
            AutofillManagerService.this.send(receiver, userData == null ? null : userData.getId());
        }

        public void setUserData(UserData userData) throws RemoteException {
            int userId = UserHandle.getCallingUserId();
            synchronized (AutofillManagerService.this.mLock) {
                AutofillManagerServiceImpl service = (AutofillManagerServiceImpl) AutofillManagerService.this.peekServiceForUserLocked(userId);
                if (service != null) {
                    service.setUserData(getCallingUid(), userData);
                } else if (Helper.sVerbose) {
                    Slog.v(AutofillManagerService.TAG, "setUserData(): no service for " + userId);
                }
            }
        }

        public void isFieldClassificationEnabled(IResultReceiver receiver) throws RemoteException {
            int userId = UserHandle.getCallingUserId();
            boolean enabled = false;
            synchronized (AutofillManagerService.this.mLock) {
                AutofillManagerServiceImpl service = (AutofillManagerServiceImpl) AutofillManagerService.this.peekServiceForUserLocked(userId);
                if (service != null) {
                    enabled = service.isFieldClassificationEnabled(getCallingUid());
                } else if (Helper.sVerbose) {
                    Slog.v(AutofillManagerService.TAG, "isFieldClassificationEnabled(): no service for " + userId);
                }
            }
            AutofillManagerService.this.send(receiver, enabled);
        }

        public void getDefaultFieldClassificationAlgorithm(IResultReceiver receiver) throws RemoteException {
            int userId = UserHandle.getCallingUserId();
            String algorithm = null;
            synchronized (AutofillManagerService.this.mLock) {
                AutofillManagerServiceImpl service = (AutofillManagerServiceImpl) AutofillManagerService.this.peekServiceForUserLocked(userId);
                if (service != null) {
                    algorithm = service.getDefaultFieldClassificationAlgorithm(getCallingUid());
                } else if (Helper.sVerbose) {
                    Slog.v(AutofillManagerService.TAG, "getDefaultFcAlgorithm(): no service for " + userId);
                }
            }
            AutofillManagerService.this.send(receiver, algorithm);
        }

        public void setAugmentedAutofillWhitelist(List<String> packages, List<ComponentName> activities, IResultReceiver receiver) throws RemoteException {
            int i;
            boolean ok;
            int userId = UserHandle.getCallingUserId();
            synchronized (AutofillManagerService.this.mLock) {
                AutofillManagerServiceImpl service = (AutofillManagerServiceImpl) AutofillManagerService.this.peekServiceForUserLocked(userId);
                i = 0;
                if (service != null) {
                    ok = service.setAugmentedAutofillWhitelistLocked(packages, activities, getCallingUid());
                } else {
                    if (Helper.sVerbose) {
                        Slog.v(AutofillManagerService.TAG, "setAugmentedAutofillWhitelist(): no service for " + userId);
                    }
                    ok = false;
                }
            }
            AutofillManagerService autofillManagerService = AutofillManagerService.this;
            if (!ok) {
                i = -1;
            }
            autofillManagerService.send(receiver, i);
        }

        public void getAvailableFieldClassificationAlgorithms(IResultReceiver receiver) throws RemoteException {
            int userId = UserHandle.getCallingUserId();
            String[] algorithms = null;
            synchronized (AutofillManagerService.this.mLock) {
                AutofillManagerServiceImpl service = (AutofillManagerServiceImpl) AutofillManagerService.this.peekServiceForUserLocked(userId);
                if (service != null) {
                    algorithms = service.getAvailableFieldClassificationAlgorithms(getCallingUid());
                } else if (Helper.sVerbose) {
                    Slog.v(AutofillManagerService.TAG, "getAvailableFcAlgorithms(): no service for " + userId);
                }
            }
            AutofillManagerService.this.send(receiver, algorithms);
        }

        public void getAutofillServiceComponentName(IResultReceiver receiver) throws RemoteException {
            int userId = UserHandle.getCallingUserId();
            ComponentName componentName = null;
            synchronized (AutofillManagerService.this.mLock) {
                AutofillManagerServiceImpl service = (AutofillManagerServiceImpl) AutofillManagerService.this.peekServiceForUserLocked(userId);
                if (service != null) {
                    componentName = service.getServiceComponentName();
                } else if (Helper.sVerbose) {
                    Slog.v(AutofillManagerService.TAG, "getAutofillServiceComponentName(): no service for " + userId);
                }
            }
            AutofillManagerService.this.send(receiver, (Parcelable) componentName);
        }

        public void restoreSession(int sessionId, IBinder activityToken, IBinder appCallback, IResultReceiver receiver) throws RemoteException {
            int userId = UserHandle.getCallingUserId();
            IBinder activityToken2 = (IBinder) Preconditions.checkNotNull(activityToken, "activityToken");
            IBinder appCallback2 = (IBinder) Preconditions.checkNotNull(appCallback, "appCallback");
            boolean restored = false;
            synchronized (AutofillManagerService.this.mLock) {
                AutofillManagerServiceImpl service = (AutofillManagerServiceImpl) AutofillManagerService.this.peekServiceForUserLocked(userId);
                if (service != null) {
                    restored = service.restoreSession(sessionId, getCallingUid(), activityToken2, appCallback2);
                } else if (Helper.sVerbose) {
                    Slog.v(AutofillManagerService.TAG, "restoreSession(): no service for " + userId);
                }
            }
            AutofillManagerService.this.send(receiver, restored);
        }

        public void updateSession(int sessionId, AutofillId autoFillId, Rect bounds, AutofillValue value, int action, int flags, int userId) {
            int i = userId;
            synchronized (AutofillManagerService.this.mLock) {
                AutofillManagerServiceImpl service = (AutofillManagerServiceImpl) AutofillManagerService.this.peekServiceForUserLocked(i);
                if (service != null) {
                    service.updateSessionLocked(sessionId, getCallingUid(), autoFillId, bounds, value, action, flags);
                } else if (Helper.sVerbose) {
                    Slog.v(AutofillManagerService.TAG, "updateSession(): no service for " + i);
                }
            }
        }

        public void setAutofillFailure(int sessionId, List<AutofillId> ids, int userId) {
            synchronized (AutofillManagerService.this.mLock) {
                AutofillManagerServiceImpl service = (AutofillManagerServiceImpl) AutofillManagerService.this.peekServiceForUserLocked(userId);
                if (service != null) {
                    service.setAutofillFailureLocked(sessionId, getCallingUid(), ids);
                } else if (Helper.sVerbose) {
                    Slog.v(AutofillManagerService.TAG, "setAutofillFailure(): no service for " + userId);
                }
            }
        }

        public void finishSession(int sessionId, int userId) {
            synchronized (AutofillManagerService.this.mLock) {
                AutofillManagerServiceImpl service = (AutofillManagerServiceImpl) AutofillManagerService.this.peekServiceForUserLocked(userId);
                if (service != null) {
                    service.finishSessionLocked(sessionId, getCallingUid());
                } else if (Helper.sVerbose) {
                    Slog.v(AutofillManagerService.TAG, "finishSession(): no service for " + userId);
                }
            }
        }

        public void cancelSession(int sessionId, int userId) {
            synchronized (AutofillManagerService.this.mLock) {
                AutofillManagerServiceImpl service = (AutofillManagerServiceImpl) AutofillManagerService.this.peekServiceForUserLocked(userId);
                if (service != null) {
                    service.cancelSessionLocked(sessionId, getCallingUid());
                } else if (Helper.sVerbose) {
                    Slog.v(AutofillManagerService.TAG, "cancelSession(): no service for " + userId);
                }
            }
        }

        public void disableOwnedAutofillServices(int userId) {
            synchronized (AutofillManagerService.this.mLock) {
                AutofillManagerServiceImpl service = (AutofillManagerServiceImpl) AutofillManagerService.this.peekServiceForUserLocked(userId);
                if (service != null) {
                    service.disableOwnedAutofillServicesLocked(Binder.getCallingUid());
                } else if (Helper.sVerbose) {
                    Slog.v(AutofillManagerService.TAG, "cancelSession(): no service for " + userId);
                }
            }
        }

        public void isServiceSupported(int userId, IResultReceiver receiver) {
            boolean supported;
            synchronized (AutofillManagerService.this.mLock) {
                supported = !AutofillManagerService.this.isDisabledLocked(userId);
            }
            AutofillManagerService.this.send(receiver, supported);
        }

        public void isServiceEnabled(int userId, String packageName, IResultReceiver receiver) {
            boolean enabled = false;
            synchronized (AutofillManagerService.this.mLock) {
                AutofillManagerServiceImpl service = (AutofillManagerServiceImpl) AutofillManagerService.this.peekServiceForUserLocked(userId);
                if (service != null) {
                    enabled = Objects.equals(packageName, service.getServicePackageName());
                } else if (Helper.sVerbose) {
                    Slog.v(AutofillManagerService.TAG, "isServiceEnabled(): no service for " + userId);
                }
            }
            AutofillManagerService.this.send(receiver, enabled);
        }

        public void onPendingSaveUi(int operation, IBinder token) {
            Preconditions.checkNotNull(token, "token");
            Preconditions.checkArgument(operation == 1 || operation == 2, "invalid operation: %d", new Object[]{Integer.valueOf(operation)});
            synchronized (AutofillManagerService.this.mLock) {
                AutofillManagerServiceImpl service = (AutofillManagerServiceImpl) AutofillManagerService.this.peekServiceForUserLocked(UserHandle.getCallingUserId());
                if (service != null) {
                    service.onPendingSaveUi(operation, token);
                }
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 11 */
        public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
            if (DumpUtils.checkDumpPermission(AutofillManagerService.this.getContext(), AutofillManagerService.TAG, pw)) {
                boolean showHistory = true;
                boolean showHistory2 = false;
                if (args != null) {
                    boolean uiOnly = false;
                    boolean showHistory3 = true;
                    for (String arg : args) {
                        char c = 65535;
                        int hashCode = arg.hashCode();
                        if (hashCode != 900765093) {
                            if (hashCode != 1098711592) {
                                if (hashCode == 1333069025 && arg.equals("--help")) {
                                    c = 2;
                                }
                            } else if (arg.equals("--no-history")) {
                                c = 0;
                            }
                        } else if (arg.equals("--ui-only")) {
                            c = 1;
                        }
                        if (c == 0) {
                            showHistory3 = false;
                        } else if (c == 1) {
                            uiOnly = true;
                        } else if (c != 2) {
                            Slog.w(AutofillManagerService.TAG, "Ignoring invalid dump arg: " + arg);
                        } else {
                            pw.println("Usage: dumpsys autofill [--ui-only|--no-history]");
                            return;
                        }
                    }
                    showHistory = showHistory3;
                    showHistory2 = uiOnly;
                }
                if (showHistory2) {
                    AutofillManagerService.this.mUi.dump(pw);
                    return;
                }
                boolean realDebug = Helper.sDebug;
                boolean realVerbose = Helper.sVerbose;
                try {
                    Helper.sVerbose = true;
                    Helper.sDebug = true;
                    synchronized (AutofillManagerService.this.mLock) {
                        pw.print("sDebug: ");
                        pw.print(realDebug);
                        pw.print(" sVerbose: ");
                        pw.println(realVerbose);
                        AutofillManagerService.this.dumpLocked("", pw);
                        AutofillManagerService.this.mAugmentedAutofillResolver.dumpShort(pw);
                        pw.println();
                        pw.print("Max partitions per session: ");
                        pw.println(AutofillManagerService.sPartitionMaxCount);
                        pw.print("Max visible datasets: ");
                        pw.println(AutofillManagerService.sVisibleDatasetsMaxCount);
                        if (Helper.sFullScreenMode != null) {
                            pw.print("Overridden full-screen mode: ");
                            pw.println(Helper.sFullScreenMode);
                        }
                        pw.println("User data constraints: ");
                        UserData.dumpConstraints("  ", pw);
                        AutofillManagerService.this.mUi.dump(pw);
                        pw.print("Autofill Compat State: ");
                        AutofillManagerService.this.mAutofillCompatState.dump("  ", pw);
                        pw.print("from settings: ");
                        pw.println(AutofillManagerService.this.getWhitelistedCompatModePackagesFromSettings());
                        if (AutofillManagerService.this.mSupportedSmartSuggestionModes != 0) {
                            pw.print("Smart Suggestion modes: ");
                            pw.println(AutofillManager.getSmartSuggestionModeToString(AutofillManagerService.this.mSupportedSmartSuggestionModes));
                        }
                        pw.print("Augmented Service Idle Unbind Timeout: ");
                        pw.println(AutofillManagerService.this.mAugmentedServiceIdleUnbindTimeoutMs);
                        pw.print("Augmented Service Request Timeout: ");
                        pw.println(AutofillManagerService.this.mAugmentedServiceRequestTimeoutMs);
                        if (showHistory) {
                            pw.println();
                            pw.println("Requests history:");
                            pw.println();
                            AutofillManagerService.this.mRequestsHistory.reverseDump(fd, pw, args);
                            pw.println();
                            pw.println("UI latency history:");
                            pw.println();
                            AutofillManagerService.this.mUiLatencyHistory.reverseDump(fd, pw, args);
                            pw.println();
                            pw.println("WTF history:");
                            pw.println();
                            AutofillManagerService.this.mWtfHistory.reverseDump(fd, pw, args);
                        }
                        pw.println("Augmented Autofill State: ");
                        AutofillManagerService.this.mAugmentedAutofillState.dump("  ", pw);
                    }
                    Helper.sDebug = realDebug;
                    Helper.sVerbose = realVerbose;
                } catch (Throwable th) {
                    Helper.sDebug = realDebug;
                    Helper.sVerbose = realVerbose;
                    throw th;
                }
            }
        }

        /* JADX WARNING: type inference failed for: r1v1, types: [android.os.Binder] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onShellCommand(java.io.FileDescriptor r9, java.io.FileDescriptor r10, java.io.FileDescriptor r11, java.lang.String[] r12, android.os.ShellCallback r13, android.os.ResultReceiver r14) {
            /*
                r8 = this;
                com.android.server.autofill.AutofillManagerServiceShellCommand r0 = new com.android.server.autofill.AutofillManagerServiceShellCommand
                com.android.server.autofill.AutofillManagerService r1 = com.android.server.autofill.AutofillManagerService.this
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.autofill.AutofillManagerService.AutoFillManagerServiceStub.onShellCommand(java.io.FileDescriptor, java.io.FileDescriptor, java.io.FileDescriptor, java.lang.String[], android.os.ShellCallback, android.os.ResultReceiver):void");
        }
    }
}
