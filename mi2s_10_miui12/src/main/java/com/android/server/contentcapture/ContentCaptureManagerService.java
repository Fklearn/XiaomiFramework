package com.android.server.contentcapture;

import android.app.ActivityManagerInternal;
import android.app.ActivityThread;
import android.content.ComponentName;
import android.content.ContentCaptureOptions;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ActivityPresentationInfo;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.database.ContentObserver;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.util.LocalLog;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.contentcapture.ContentCaptureCondition;
import android.view.contentcapture.ContentCaptureHelper;
import android.view.contentcapture.DataRemovalRequest;
import android.view.contentcapture.IContentCaptureManager;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.infra.GlobalWhitelistState;
import com.android.internal.os.IResultReceiver;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.Preconditions;
import com.android.internal.util.SyncResultReceiver;
import com.android.server.LocalServices;
import com.android.server.contentcapture.ContentCaptureManagerService;
import com.android.server.infra.AbstractMasterSystemService;
import com.android.server.infra.FrameworkResourcesServiceNameResolver;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public final class ContentCaptureManagerService extends AbstractMasterSystemService<ContentCaptureManagerService, ContentCapturePerUserService> {
    private static final int MAX_TEMP_SERVICE_DURATION_MS = 120000;
    static final String RECEIVER_BUNDLE_EXTRA_SESSIONS = "sessions";
    @GuardedBy({"mLock"})
    private ActivityManagerInternal mAm;
    @GuardedBy({"mLock"})
    int mDevCfgIdleFlushingFrequencyMs;
    @GuardedBy({"mLock"})
    int mDevCfgIdleUnbindTimeoutMs;
    @GuardedBy({"mLock"})
    int mDevCfgLogHistorySize;
    @GuardedBy({"mLock"})
    int mDevCfgLoggingLevel;
    @GuardedBy({"mLock"})
    int mDevCfgMaxBufferSize;
    @GuardedBy({"mLock"})
    int mDevCfgTextChangeFlushingFrequencyMs;
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public boolean mDisabledByDeviceConfig;
    @GuardedBy({"mLock"})
    private SparseBooleanArray mDisabledBySettings;
    final GlobalContentCaptureOptions mGlobalContentCaptureOptions = new GlobalContentCaptureOptions();
    private final LocalService mLocalService = new LocalService();
    final LocalLog mRequestsHistory;

    public ContentCaptureManagerService(Context context) {
        super(context, new FrameworkResourcesServiceNameResolver(context, 17039725), "no_content_capture", 0);
        DeviceConfig.addOnPropertiesChangedListener("content_capture", ActivityThread.currentApplication().getMainExecutor(), new DeviceConfig.OnPropertiesChangedListener() {
            public final void onPropertiesChanged(DeviceConfig.Properties properties) {
                ContentCaptureManagerService.this.lambda$new$0$ContentCaptureManagerService(properties);
            }
        });
        setDeviceConfigProperties();
        if (this.mDevCfgLogHistorySize > 0) {
            if (this.debug) {
                String str = this.mTag;
                Slog.d(str, "log history size: " + this.mDevCfgLogHistorySize);
            }
            this.mRequestsHistory = new LocalLog(this.mDevCfgLogHistorySize);
        } else {
            if (this.debug) {
                String str2 = this.mTag;
                Slog.d(str2, "disabled log history because size is " + this.mDevCfgLogHistorySize);
            }
            this.mRequestsHistory = null;
        }
        List<UserInfo> users = ((UserManager) getContext().getSystemService(UserManager.class)).getUsers();
        for (int i = 0; i < users.size(); i++) {
            int userId = users.get(i).id;
            if (!isEnabledBySettings(userId)) {
                String str3 = this.mTag;
                Slog.i(str3, "user " + userId + " disabled by settings");
                if (this.mDisabledBySettings == null) {
                    this.mDisabledBySettings = new SparseBooleanArray(1);
                }
                this.mDisabledBySettings.put(userId, true);
            }
            this.mGlobalContentCaptureOptions.setServiceInfo(userId, this.mServiceNameResolver.getServiceName(userId), this.mServiceNameResolver.isTemporary(userId));
        }
    }

    /* access modifiers changed from: protected */
    public ContentCapturePerUserService newServiceLocked(int resolvedUserId, boolean disabled) {
        return new ContentCapturePerUserService(this, this.mLock, disabled, resolvedUserId);
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [com.android.server.contentcapture.ContentCaptureManagerService$ContentCaptureManagerServiceStub, android.os.IBinder] */
    public void onStart() {
        publishBinderService("content_capture", new ContentCaptureManagerServiceStub());
        publishLocalService(ContentCaptureManagerInternal.class, this.mLocalService);
    }

    /* access modifiers changed from: protected */
    public void onServiceRemoved(ContentCapturePerUserService service, int userId) {
        service.destroyLocked();
    }

    /* access modifiers changed from: protected */
    public void onServicePackageUpdatingLocked(int userId) {
        ContentCapturePerUserService service = (ContentCapturePerUserService) getServiceForUserLocked(userId);
        if (service != null) {
            service.onPackageUpdatingLocked();
        }
    }

    /* access modifiers changed from: protected */
    public void onServicePackageUpdatedLocked(int userId) {
        ContentCapturePerUserService service = (ContentCapturePerUserService) getServiceForUserLocked(userId);
        if (service != null) {
            service.onPackageUpdatedLocked();
        }
    }

    /* access modifiers changed from: protected */
    public void onServiceNameChanged(int userId, String serviceName, boolean isTemporary) {
        this.mGlobalContentCaptureOptions.setServiceInfo(userId, serviceName, isTemporary);
        super.lambda$new$0$AbstractMasterSystemService(userId, serviceName, isTemporary);
    }

    /* access modifiers changed from: protected */
    public void enforceCallingPermissionForManagement() {
        getContext().enforceCallingPermission("android.permission.MANAGE_CONTENT_CAPTURE", this.mTag);
    }

    /* access modifiers changed from: protected */
    public int getMaximumTemporaryServiceDurationMs() {
        return MAX_TEMP_SERVICE_DURATION_MS;
    }

    /* access modifiers changed from: protected */
    public void registerForExtraSettingsChanges(ContentResolver resolver, ContentObserver observer) {
        resolver.registerContentObserver(Settings.Secure.getUriFor("content_capture_enabled"), false, observer, -1);
    }

    /* access modifiers changed from: protected */
    public void onSettingsChanged(int userId, String property) {
        if (((property.hashCode() == -322385022 && property.equals("content_capture_enabled")) ? (char) 0 : 65535) != 0) {
            String str = this.mTag;
            Slog.w(str, "Unexpected property (" + property + "); updating cache instead");
            return;
        }
        setContentCaptureFeatureEnabledBySettingsForUser(userId, isEnabledBySettings(userId));
    }

    /* access modifiers changed from: protected */
    public boolean isDisabledLocked(int userId) {
        return this.mDisabledByDeviceConfig || isDisabledBySettingsLocked(userId) || super.isDisabledLocked(userId);
    }

    /* access modifiers changed from: private */
    public boolean isDisabledBySettingsLocked(int userId) {
        SparseBooleanArray sparseBooleanArray = this.mDisabledBySettings;
        return sparseBooleanArray != null && sparseBooleanArray.get(userId);
    }

    private boolean isEnabledBySettings(int userId) {
        boolean enabled = true;
        if (Settings.Secure.getIntForUser(getContext().getContentResolver(), "content_capture_enabled", 1, userId) != 1) {
            enabled = false;
        }
        return enabled;
    }

    /* access modifiers changed from: private */
    /* renamed from: onDeviceConfigChange */
    public void lambda$new$0$ContentCaptureManagerService(DeviceConfig.Properties properties) {
        for (String key : properties.getKeyset()) {
            char c = 65535;
            switch (key.hashCode()) {
                case -1970239836:
                    if (key.equals("logging_level")) {
                        c = 1;
                        break;
                    }
                    break;
                case -302650995:
                    if (key.equals("service_explicitly_enabled")) {
                        c = 0;
                        break;
                    }
                    break;
                case -148969820:
                    if (key.equals("text_change_flush_frequency")) {
                        c = 5;
                        break;
                    }
                    break;
                case 227845607:
                    if (key.equals("log_history_size")) {
                        c = 4;
                        break;
                    }
                    break;
                case 1119140421:
                    if (key.equals("max_buffer_size")) {
                        c = 2;
                        break;
                    }
                    break;
                case 1568835651:
                    if (key.equals("idle_unbind_timeout")) {
                        c = 6;
                        break;
                    }
                    break;
                case 2068460406:
                    if (key.equals("idle_flush_frequency")) {
                        c = 3;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    setDisabledByDeviceConfig(properties.getString(key, (String) null));
                    return;
                case 1:
                    setLoggingLevelFromDeviceConfig();
                    return;
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                    setFineTuneParamsFromDeviceConfig();
                    return;
                default:
                    String str = this.mTag;
                    Slog.i(str, "Ignoring change on " + key);
            }
        }
    }

    private void setFineTuneParamsFromDeviceConfig() {
        synchronized (this.mLock) {
            this.mDevCfgMaxBufferSize = DeviceConfig.getInt("content_capture", "max_buffer_size", 100);
            this.mDevCfgIdleFlushingFrequencyMs = DeviceConfig.getInt("content_capture", "idle_flush_frequency", 5000);
            this.mDevCfgTextChangeFlushingFrequencyMs = DeviceConfig.getInt("content_capture", "text_change_flush_frequency", 1000);
            this.mDevCfgLogHistorySize = DeviceConfig.getInt("content_capture", "log_history_size", 20);
            this.mDevCfgIdleUnbindTimeoutMs = DeviceConfig.getInt("content_capture", "idle_unbind_timeout", 0);
            if (this.verbose) {
                String str = this.mTag;
                Slog.v(str, "setFineTuneParamsFromDeviceConfig(): bufferSize=" + this.mDevCfgMaxBufferSize + ", idleFlush=" + this.mDevCfgIdleFlushingFrequencyMs + ", textFluxh=" + this.mDevCfgTextChangeFlushingFrequencyMs + ", logHistory=" + this.mDevCfgLogHistorySize + ", idleUnbindTimeoutMs=" + this.mDevCfgIdleUnbindTimeoutMs);
            }
        }
    }

    private void setLoggingLevelFromDeviceConfig() {
        this.mDevCfgLoggingLevel = DeviceConfig.getInt("content_capture", "logging_level", ContentCaptureHelper.getDefaultLoggingLevel());
        ContentCaptureHelper.setLoggingLevel(this.mDevCfgLoggingLevel);
        this.verbose = ContentCaptureHelper.sVerbose;
        this.debug = ContentCaptureHelper.sDebug;
        if (this.verbose) {
            String str = this.mTag;
            Slog.v(str, "setLoggingLevelFromDeviceConfig(): level=" + this.mDevCfgLoggingLevel + ", debug=" + this.debug + ", verbose=" + this.verbose);
        }
    }

    private void setDeviceConfigProperties() {
        setLoggingLevelFromDeviceConfig();
        setFineTuneParamsFromDeviceConfig();
        setDisabledByDeviceConfig(DeviceConfig.getProperty("content_capture", "service_explicitly_enabled"));
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x005b, code lost:
        return;
     */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00ac  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00af  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setDisabledByDeviceConfig(java.lang.String r12) {
        /*
            r11 = this;
            boolean r0 = r11.verbose
            if (r0 == 0) goto L_0x001b
            java.lang.String r0 = r11.mTag
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "setDisabledByDeviceConfig(): explicitlyEnabled="
            r1.append(r2)
            r1.append(r12)
            java.lang.String r1 = r1.toString()
            android.util.Slog.v(r0, r1)
        L_0x001b:
            android.content.Context r0 = r11.getContext()
            java.lang.Class<android.os.UserManager> r1 = android.os.UserManager.class
            java.lang.Object r0 = r0.getSystemService(r1)
            android.os.UserManager r0 = (android.os.UserManager) r0
            java.util.List r1 = r0.getUsers()
            if (r12 == 0) goto L_0x0037
            java.lang.String r2 = "false"
            boolean r2 = r12.equalsIgnoreCase(r2)
            if (r2 == 0) goto L_0x0037
            r2 = 1
            goto L_0x0038
        L_0x0037:
            r2 = 0
        L_0x0038:
            java.lang.Object r3 = r11.mLock
            monitor-enter(r3)
            boolean r4 = r11.mDisabledByDeviceConfig     // Catch:{ all -> 0x00c4 }
            if (r4 != r2) goto L_0x005c
            boolean r4 = r11.verbose     // Catch:{ all -> 0x00c4 }
            if (r4 == 0) goto L_0x005a
            java.lang.String r4 = r11.mTag     // Catch:{ all -> 0x00c4 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x00c4 }
            r5.<init>()     // Catch:{ all -> 0x00c4 }
            java.lang.String r6 = "setDisabledByDeviceConfig(): already "
            r5.append(r6)     // Catch:{ all -> 0x00c4 }
            r5.append(r2)     // Catch:{ all -> 0x00c4 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x00c4 }
            android.util.Slog.v(r4, r5)     // Catch:{ all -> 0x00c4 }
        L_0x005a:
            monitor-exit(r3)     // Catch:{ all -> 0x00c4 }
            return
        L_0x005c:
            r11.mDisabledByDeviceConfig = r2     // Catch:{ all -> 0x00c4 }
            java.lang.String r4 = r11.mTag     // Catch:{ all -> 0x00c4 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x00c4 }
            r5.<init>()     // Catch:{ all -> 0x00c4 }
            java.lang.String r6 = "setDisabledByDeviceConfig(): set to "
            r5.append(r6)     // Catch:{ all -> 0x00c4 }
            boolean r6 = r11.mDisabledByDeviceConfig     // Catch:{ all -> 0x00c4 }
            r5.append(r6)     // Catch:{ all -> 0x00c4 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x00c4 }
            android.util.Slog.i(r4, r5)     // Catch:{ all -> 0x00c4 }
            r4 = 0
            r5 = r4
        L_0x0079:
            int r6 = r1.size()     // Catch:{ all -> 0x00c4 }
            if (r5 >= r6) goto L_0x00c2
            java.lang.Object r6 = r1.get(r5)     // Catch:{ all -> 0x00c4 }
            android.content.pm.UserInfo r6 = (android.content.pm.UserInfo) r6     // Catch:{ all -> 0x00c4 }
            int r6 = r6.id     // Catch:{ all -> 0x00c4 }
            boolean r7 = r11.mDisabledByDeviceConfig     // Catch:{ all -> 0x00c4 }
            if (r7 != 0) goto L_0x0094
            boolean r7 = r11.isDisabledBySettingsLocked(r6)     // Catch:{ all -> 0x00c4 }
            if (r7 == 0) goto L_0x0092
            goto L_0x0094
        L_0x0092:
            r7 = r4
            goto L_0x0095
        L_0x0094:
            r7 = 1
        L_0x0095:
            java.lang.String r8 = r11.mTag     // Catch:{ all -> 0x00c4 }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x00c4 }
            r9.<init>()     // Catch:{ all -> 0x00c4 }
            java.lang.String r10 = "setDisabledByDeviceConfig(): updating service for user "
            r9.append(r10)     // Catch:{ all -> 0x00c4 }
            r9.append(r6)     // Catch:{ all -> 0x00c4 }
            java.lang.String r10 = " to "
            r9.append(r10)     // Catch:{ all -> 0x00c4 }
            if (r7 == 0) goto L_0x00af
            java.lang.String r10 = "'disabled'"
            goto L_0x00b1
        L_0x00af:
            java.lang.String r10 = "'enabled'"
        L_0x00b1:
            r9.append(r10)     // Catch:{ all -> 0x00c4 }
            java.lang.String r9 = r9.toString()     // Catch:{ all -> 0x00c4 }
            android.util.Slog.i(r8, r9)     // Catch:{ all -> 0x00c4 }
            r11.updateCachedServiceLocked(r6, r7)     // Catch:{ all -> 0x00c4 }
            int r5 = r5 + 1
            goto L_0x0079
        L_0x00c2:
            monitor-exit(r3)     // Catch:{ all -> 0x00c4 }
            return
        L_0x00c4:
            r4 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x00c4 }
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.contentcapture.ContentCaptureManagerService.setDisabledByDeviceConfig(java.lang.String):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0036, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setContentCaptureFeatureEnabledBySettingsForUser(int r7, boolean r8) {
        /*
            r6 = this;
            java.lang.Object r0 = r6.mLock
            monitor-enter(r0)
            android.util.SparseBooleanArray r1 = r6.mDisabledBySettings     // Catch:{ all -> 0x0080 }
            if (r1 != 0) goto L_0x000e
            android.util.SparseBooleanArray r1 = new android.util.SparseBooleanArray     // Catch:{ all -> 0x0080 }
            r1.<init>()     // Catch:{ all -> 0x0080 }
            r6.mDisabledBySettings = r1     // Catch:{ all -> 0x0080 }
        L_0x000e:
            android.util.SparseBooleanArray r1 = r6.mDisabledBySettings     // Catch:{ all -> 0x0080 }
            boolean r1 = r1.get(r7)     // Catch:{ all -> 0x0080 }
            r2 = 1
            r1 = r1 ^ r2
            r3 = r8 ^ r1
            if (r3 != 0) goto L_0x0037
            boolean r2 = r6.debug     // Catch:{ all -> 0x0080 }
            if (r2 == 0) goto L_0x0035
            java.lang.String r2 = r6.mTag     // Catch:{ all -> 0x0080 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0080 }
            r3.<init>()     // Catch:{ all -> 0x0080 }
            java.lang.String r4 = "setContentCaptureFeatureEnabledForUser(): already "
            r3.append(r4)     // Catch:{ all -> 0x0080 }
            r3.append(r8)     // Catch:{ all -> 0x0080 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0080 }
            android.util.Slog.d(r2, r3)     // Catch:{ all -> 0x0080 }
        L_0x0035:
            monitor-exit(r0)     // Catch:{ all -> 0x0080 }
            return
        L_0x0037:
            if (r8 == 0) goto L_0x0056
            java.lang.String r3 = r6.mTag     // Catch:{ all -> 0x0080 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0080 }
            r4.<init>()     // Catch:{ all -> 0x0080 }
            java.lang.String r5 = "setContentCaptureFeatureEnabled(): enabling service for user "
            r4.append(r5)     // Catch:{ all -> 0x0080 }
            r4.append(r7)     // Catch:{ all -> 0x0080 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0080 }
            android.util.Slog.i(r3, r4)     // Catch:{ all -> 0x0080 }
            android.util.SparseBooleanArray r3 = r6.mDisabledBySettings     // Catch:{ all -> 0x0080 }
            r3.delete(r7)     // Catch:{ all -> 0x0080 }
            goto L_0x0072
        L_0x0056:
            java.lang.String r3 = r6.mTag     // Catch:{ all -> 0x0080 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0080 }
            r4.<init>()     // Catch:{ all -> 0x0080 }
            java.lang.String r5 = "setContentCaptureFeatureEnabled(): disabling service for user "
            r4.append(r5)     // Catch:{ all -> 0x0080 }
            r4.append(r7)     // Catch:{ all -> 0x0080 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0080 }
            android.util.Slog.i(r3, r4)     // Catch:{ all -> 0x0080 }
            android.util.SparseBooleanArray r3 = r6.mDisabledBySettings     // Catch:{ all -> 0x0080 }
            r3.put(r7, r2)     // Catch:{ all -> 0x0080 }
        L_0x0072:
            if (r8 == 0) goto L_0x007a
            boolean r3 = r6.mDisabledByDeviceConfig     // Catch:{ all -> 0x0080 }
            if (r3 == 0) goto L_0x0079
            goto L_0x007a
        L_0x0079:
            r2 = 0
        L_0x007a:
            r6.updateCachedServiceLocked(r7, r2)     // Catch:{ all -> 0x0080 }
            monitor-exit(r0)     // Catch:{ all -> 0x0080 }
            return
        L_0x0080:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0080 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.contentcapture.ContentCaptureManagerService.setContentCaptureFeatureEnabledBySettingsForUser(int, boolean):void");
    }

    /* access modifiers changed from: package-private */
    public void destroySessions(int userId, IResultReceiver receiver) {
        String str = this.mTag;
        Slog.i(str, "destroySessions() for userId " + userId);
        enforceCallingPermissionForManagement();
        synchronized (this.mLock) {
            if (userId != -1) {
                ContentCapturePerUserService service = (ContentCapturePerUserService) peekServiceForUserLocked(userId);
                if (service != null) {
                    service.destroySessionsLocked();
                }
            } else {
                visitServicesLocked($$Lambda$ContentCaptureManagerService$jCIcV2sgwD7QUkNc6yfPd58T_U.INSTANCE);
            }
        }
        try {
            receiver.send(0, new Bundle());
        } catch (RemoteException e) {
        }
    }

    /* access modifiers changed from: package-private */
    public void listSessions(int userId, IResultReceiver receiver) {
        String str = this.mTag;
        Slog.i(str, "listSessions() for userId " + userId);
        enforceCallingPermissionForManagement();
        Bundle resultData = new Bundle();
        ArrayList<String> sessions = new ArrayList<>();
        synchronized (this.mLock) {
            if (userId != -1) {
                try {
                    ContentCapturePerUserService service = (ContentCapturePerUserService) peekServiceForUserLocked(userId);
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
                        ((ContentCapturePerUserService) obj).listSessionsLocked(this.f$0);
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

    /* access modifiers changed from: private */
    public ActivityManagerInternal getAmInternal() {
        synchronized (this.mLock) {
            if (this.mAm == null) {
                this.mAm = (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
            }
        }
        return this.mAm;
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public void assertCalledByServiceLocked(String methodName) {
        if (!isCalledByServiceLocked(methodName)) {
            throw new SecurityException("caller is not user's ContentCapture service");
        }
    }

    @GuardedBy({"mLock"})
    private boolean isCalledByServiceLocked(String methodName) {
        int userId = UserHandle.getCallingUserId();
        int callingUid = Binder.getCallingUid();
        String serviceName = this.mServiceNameResolver.getServiceName(userId);
        if (serviceName == null) {
            String str = this.mTag;
            Slog.e(str, methodName + ": called by UID " + callingUid + ", but there's no service set for user " + userId);
            return false;
        }
        ComponentName serviceComponent = ComponentName.unflattenFromString(serviceName);
        if (serviceComponent == null) {
            String str2 = this.mTag;
            Slog.w(str2, methodName + ": invalid service name: " + serviceName);
            return false;
        }
        String servicePackageName = serviceComponent.getPackageName();
        try {
            int serviceUid = getContext().getPackageManager().getPackageUidAsUser(servicePackageName, UserHandle.getCallingUserId());
            if (callingUid == serviceUid) {
                return true;
            }
            String str3 = this.mTag;
            Slog.e(str3, methodName + ": called by UID " + callingUid + ", but service UID is " + serviceUid);
            return false;
        } catch (PackageManager.NameNotFoundException e) {
            String str4 = this.mTag;
            Slog.w(str4, methodName + ": could not verify UID for " + serviceName);
            return false;
        }
    }

    /* access modifiers changed from: private */
    public boolean throwsSecurityException(IResultReceiver result, Runnable runable) {
        try {
            runable.run();
            return false;
        } catch (SecurityException e) {
            try {
                result.send(-1, SyncResultReceiver.bundleFor(e.getMessage()));
                return true;
            } catch (RemoteException e2) {
                String str = this.mTag;
                Slog.w(str, "Unable to send security exception (" + e + "): ", e2);
                return true;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void dumpLocked(String prefix, PrintWriter pw) {
        super.dumpLocked(prefix, pw);
        String prefix2 = prefix + "  ";
        pw.print(prefix);
        pw.print("Users disabled by Settings: ");
        pw.println(this.mDisabledBySettings);
        pw.print(prefix);
        pw.println("DeviceConfig Settings: ");
        pw.print(prefix2);
        pw.print("disabled: ");
        pw.println(this.mDisabledByDeviceConfig);
        pw.print(prefix2);
        pw.print("loggingLevel: ");
        pw.println(this.mDevCfgLoggingLevel);
        pw.print(prefix2);
        pw.print("maxBufferSize: ");
        pw.println(this.mDevCfgMaxBufferSize);
        pw.print(prefix2);
        pw.print("idleFlushingFrequencyMs: ");
        pw.println(this.mDevCfgIdleFlushingFrequencyMs);
        pw.print(prefix2);
        pw.print("textChangeFlushingFrequencyMs: ");
        pw.println(this.mDevCfgTextChangeFlushingFrequencyMs);
        pw.print(prefix2);
        pw.print("logHistorySize: ");
        pw.println(this.mDevCfgLogHistorySize);
        pw.print(prefix2);
        pw.print("idleUnbindTimeoutMs: ");
        pw.println(this.mDevCfgIdleUnbindTimeoutMs);
        pw.print(prefix);
        pw.println("Global Options:");
        this.mGlobalContentCaptureOptions.dump(prefix2, pw);
    }

    final class ContentCaptureManagerServiceStub extends IContentCaptureManager.Stub {
        ContentCaptureManagerServiceStub() {
        }

        public void startSession(IBinder activityToken, ComponentName componentName, int sessionId, int flags, IResultReceiver result) {
            Preconditions.checkNotNull(activityToken);
            Preconditions.checkNotNull(Integer.valueOf(sessionId));
            int userId = UserHandle.getCallingUserId();
            ActivityPresentationInfo activityPresentationInfo = ContentCaptureManagerService.this.getAmInternal().getActivityPresentationInfo(activityToken);
            synchronized (ContentCaptureManagerService.this.mLock) {
                ((ContentCapturePerUserService) ContentCaptureManagerService.this.getServiceForUserLocked(userId)).startSessionLocked(activityToken, activityPresentationInfo, sessionId, Binder.getCallingUid(), flags, result);
            }
        }

        public void finishSession(int sessionId) {
            Preconditions.checkNotNull(Integer.valueOf(sessionId));
            int userId = UserHandle.getCallingUserId();
            synchronized (ContentCaptureManagerService.this.mLock) {
                ((ContentCapturePerUserService) ContentCaptureManagerService.this.getServiceForUserLocked(userId)).finishSessionLocked(sessionId);
            }
        }

        public void getServiceComponentName(IResultReceiver result) {
            ComponentName connectedServiceComponentName;
            int userId = UserHandle.getCallingUserId();
            synchronized (ContentCaptureManagerService.this.mLock) {
                connectedServiceComponentName = ((ContentCapturePerUserService) ContentCaptureManagerService.this.getServiceForUserLocked(userId)).getServiceComponentName();
            }
            try {
                result.send(0, SyncResultReceiver.bundleFor(connectedServiceComponentName));
            } catch (RemoteException e) {
                String access$900 = ContentCaptureManagerService.this.mTag;
                Slog.w(access$900, "Unable to send service component name: " + e);
            }
        }

        public void removeData(DataRemovalRequest request) {
            Preconditions.checkNotNull(request);
            ContentCaptureManagerService.this.assertCalledByPackageOwner(request.getPackageName());
            int userId = UserHandle.getCallingUserId();
            synchronized (ContentCaptureManagerService.this.mLock) {
                ((ContentCapturePerUserService) ContentCaptureManagerService.this.getServiceForUserLocked(userId)).removeDataLocked(request);
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:15:0x0030, code lost:
            if (r1 == false) goto L_0x0033;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0033, code lost:
            r3 = 2;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
            r6.send(r3, (android.os.Bundle) null);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:20:0x0039, code lost:
            r0 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:21:0x003a, code lost:
            r2 = com.android.server.contentcapture.ContentCaptureManagerService.access$1700(r5.this$0);
            android.util.Slog.w(r2, "Unable to send isContentCaptureFeatureEnabled(): " + r0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:29:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:30:?, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void isContentCaptureFeatureEnabled(com.android.internal.os.IResultReceiver r6) {
            /*
                r5 = this;
                com.android.server.contentcapture.ContentCaptureManagerService r0 = com.android.server.contentcapture.ContentCaptureManagerService.this
                java.lang.Object r0 = r0.mLock
                monitor-enter(r0)
                com.android.server.contentcapture.ContentCaptureManagerService r1 = com.android.server.contentcapture.ContentCaptureManagerService.this     // Catch:{ all -> 0x0055 }
                com.android.server.contentcapture.-$$Lambda$ContentCaptureManagerService$ContentCaptureManagerServiceStub$vyDTyUUAt356my5WVtp7QPYv5gY r2 = new com.android.server.contentcapture.-$$Lambda$ContentCaptureManagerService$ContentCaptureManagerServiceStub$vyDTyUUAt356my5WVtp7QPYv5gY     // Catch:{ all -> 0x0055 }
                r2.<init>()     // Catch:{ all -> 0x0055 }
                boolean r1 = r1.throwsSecurityException(r6, r2)     // Catch:{ all -> 0x0055 }
                if (r1 == 0) goto L_0x0016
                monitor-exit(r0)     // Catch:{ all -> 0x0055 }
                return
            L_0x0016:
                int r1 = android.os.UserHandle.getCallingUserId()     // Catch:{ all -> 0x0055 }
                com.android.server.contentcapture.ContentCaptureManagerService r2 = com.android.server.contentcapture.ContentCaptureManagerService.this     // Catch:{ all -> 0x0055 }
                boolean r2 = r2.mDisabledByDeviceConfig     // Catch:{ all -> 0x0055 }
                r3 = 1
                if (r2 != 0) goto L_0x002d
                com.android.server.contentcapture.ContentCaptureManagerService r2 = com.android.server.contentcapture.ContentCaptureManagerService.this     // Catch:{ all -> 0x0055 }
                boolean r2 = r2.isDisabledBySettingsLocked(r1)     // Catch:{ all -> 0x0055 }
                if (r2 != 0) goto L_0x002d
                r2 = r3
                goto L_0x002e
            L_0x002d:
                r2 = 0
            L_0x002e:
                r1 = r2
                monitor-exit(r0)     // Catch:{ all -> 0x0055 }
                if (r1 == 0) goto L_0x0033
                goto L_0x0034
            L_0x0033:
                r3 = 2
            L_0x0034:
                r0 = 0
                r6.send(r3, r0)     // Catch:{ RemoteException -> 0x0039 }
                goto L_0x0054
            L_0x0039:
                r0 = move-exception
                com.android.server.contentcapture.ContentCaptureManagerService r2 = com.android.server.contentcapture.ContentCaptureManagerService.this
                java.lang.String r2 = r2.mTag
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                java.lang.String r4 = "Unable to send isContentCaptureFeatureEnabled(): "
                r3.append(r4)
                r3.append(r0)
                java.lang.String r3 = r3.toString()
                android.util.Slog.w(r2, r3)
            L_0x0054:
                return
            L_0x0055:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0055 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.contentcapture.ContentCaptureManagerService.ContentCaptureManagerServiceStub.isContentCaptureFeatureEnabled(com.android.internal.os.IResultReceiver):void");
        }

        public /* synthetic */ void lambda$isContentCaptureFeatureEnabled$0$ContentCaptureManagerService$ContentCaptureManagerServiceStub() {
            ContentCaptureManagerService.this.assertCalledByServiceLocked("isContentCaptureFeatureEnabled()");
        }

        public void getServiceSettingsActivity(IResultReceiver result) {
            if (!ContentCaptureManagerService.this.throwsSecurityException(result, new Runnable() {
                public final void run() {
                    ContentCaptureManagerService.ContentCaptureManagerServiceStub.this.lambda$getServiceSettingsActivity$1$ContentCaptureManagerService$ContentCaptureManagerServiceStub();
                }
            })) {
                int userId = UserHandle.getCallingUserId();
                synchronized (ContentCaptureManagerService.this.mLock) {
                    ContentCapturePerUserService service = (ContentCapturePerUserService) ContentCaptureManagerService.this.getServiceForUserLocked(userId);
                    if (service != null) {
                        ComponentName componentName = service.getServiceSettingsActivityLocked();
                        try {
                            result.send(0, SyncResultReceiver.bundleFor(componentName));
                        } catch (RemoteException e) {
                            String access$2000 = ContentCaptureManagerService.this.mTag;
                            Slog.w(access$2000, "Unable to send getServiceSettingsIntent(): " + e);
                        }
                    }
                }
            }
        }

        public /* synthetic */ void lambda$getServiceSettingsActivity$1$ContentCaptureManagerService$ContentCaptureManagerServiceStub() {
            ContentCaptureManagerService.this.enforceCallingPermissionForManagement();
        }

        public void getContentCaptureConditions(String packageName, IResultReceiver result) {
            ArrayList<ContentCaptureCondition> arrayList;
            ArrayList<ContentCaptureCondition> conditions;
            if (!ContentCaptureManagerService.this.throwsSecurityException(result, new Runnable(packageName) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ContentCaptureManagerService.ContentCaptureManagerServiceStub.this.lambda$getContentCaptureConditions$2$ContentCaptureManagerService$ContentCaptureManagerServiceStub(this.f$1);
                }
            })) {
                int userId = UserHandle.getCallingUserId();
                synchronized (ContentCaptureManagerService.this.mLock) {
                    ContentCapturePerUserService service = (ContentCapturePerUserService) ContentCaptureManagerService.this.getServiceForUserLocked(userId);
                    if (service == null) {
                        arrayList = null;
                    } else {
                        arrayList = ContentCaptureHelper.toList(service.getContentCaptureConditionsLocked(packageName));
                    }
                    conditions = arrayList;
                }
                try {
                    result.send(0, SyncResultReceiver.bundleFor(conditions));
                } catch (RemoteException e) {
                    String access$2300 = ContentCaptureManagerService.this.mTag;
                    Slog.w(access$2300, "Unable to send getServiceComponentName(): " + e);
                }
            }
        }

        public /* synthetic */ void lambda$getContentCaptureConditions$2$ContentCaptureManagerService$ContentCaptureManagerServiceStub(String packageName) {
            ContentCaptureManagerService.this.assertCalledByPackageOwner(packageName);
        }

        public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
            if (DumpUtils.checkDumpPermission(ContentCaptureManagerService.this.getContext(), ContentCaptureManagerService.this.mTag, pw)) {
                boolean showHistory = true;
                if (args != null) {
                    boolean showHistory2 = true;
                    for (String arg : args) {
                        char c = 65535;
                        int hashCode = arg.hashCode();
                        if (hashCode != 1098711592) {
                            if (hashCode == 1333069025 && arg.equals("--help")) {
                                c = 1;
                            }
                        } else if (arg.equals("--no-history")) {
                            c = 0;
                        }
                        if (c == 0) {
                            showHistory2 = false;
                        } else if (c != 1) {
                            Slog.w(ContentCaptureManagerService.this.mTag, "Ignoring invalid dump arg: " + arg);
                        } else {
                            pw.println("Usage: dumpsys content_capture [--no-history]");
                            return;
                        }
                    }
                    showHistory = showHistory2;
                }
                synchronized (ContentCaptureManagerService.this.mLock) {
                    ContentCaptureManagerService.this.dumpLocked("", pw);
                }
                pw.print("Requests history: ");
                if (ContentCaptureManagerService.this.mRequestsHistory == null) {
                    pw.println("disabled by device config");
                } else if (showHistory) {
                    pw.println();
                    ContentCaptureManagerService.this.mRequestsHistory.reverseDump(fd, pw, args);
                    pw.println();
                } else {
                    pw.println();
                }
            }
        }

        /* JADX WARNING: type inference failed for: r1v1, types: [android.os.Binder] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onShellCommand(java.io.FileDescriptor r9, java.io.FileDescriptor r10, java.io.FileDescriptor r11, java.lang.String[] r12, android.os.ShellCallback r13, android.os.ResultReceiver r14) throws android.os.RemoteException {
            /*
                r8 = this;
                com.android.server.contentcapture.ContentCaptureManagerServiceShellCommand r0 = new com.android.server.contentcapture.ContentCaptureManagerServiceShellCommand
                com.android.server.contentcapture.ContentCaptureManagerService r1 = com.android.server.contentcapture.ContentCaptureManagerService.this
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.contentcapture.ContentCaptureManagerService.ContentCaptureManagerServiceStub.onShellCommand(java.io.FileDescriptor, java.io.FileDescriptor, java.io.FileDescriptor, java.lang.String[], android.os.ShellCallback, android.os.ResultReceiver):void");
        }
    }

    private final class LocalService extends ContentCaptureManagerInternal {
        private LocalService() {
        }

        public boolean isContentCaptureServiceForUser(int uid, int userId) {
            synchronized (ContentCaptureManagerService.this.mLock) {
                ContentCapturePerUserService service = (ContentCapturePerUserService) ContentCaptureManagerService.this.peekServiceForUserLocked(userId);
                if (service == null) {
                    return false;
                }
                boolean isContentCaptureServiceForUserLocked = service.isContentCaptureServiceForUserLocked(uid);
                return isContentCaptureServiceForUserLocked;
            }
        }

        public boolean sendActivityAssistData(int userId, IBinder activityToken, Bundle data) {
            synchronized (ContentCaptureManagerService.this.mLock) {
                ContentCapturePerUserService service = (ContentCapturePerUserService) ContentCaptureManagerService.this.peekServiceForUserLocked(userId);
                if (service == null) {
                    return false;
                }
                boolean sendActivityAssistDataLocked = service.sendActivityAssistDataLocked(activityToken, data);
                return sendActivityAssistDataLocked;
            }
        }

        public ContentCaptureOptions getOptionsForPackage(int userId, String packageName) {
            return ContentCaptureManagerService.this.mGlobalContentCaptureOptions.getOptions(userId, packageName);
        }

        public void notifyActivityEvent(int userId, ComponentName activityComponent, int eventType) {
            synchronized (ContentCaptureManagerService.this.mLock) {
                ContentCapturePerUserService service = (ContentCapturePerUserService) ContentCaptureManagerService.this.peekServiceForUserLocked(userId);
                if (service != null) {
                    service.onActivityEventLocked(activityComponent, eventType);
                }
            }
        }
    }

    final class GlobalContentCaptureOptions extends GlobalWhitelistState {
        @GuardedBy({"mGlobalWhitelistStateLock"})
        private final SparseArray<String> mServicePackages = new SparseArray<>();
        @GuardedBy({"mGlobalWhitelistStateLock"})
        private final SparseBooleanArray mTemporaryServices = new SparseBooleanArray();

        GlobalContentCaptureOptions() {
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
                        String access$3500 = ContentCaptureManagerService.this.mTag;
                        Slog.w(access$3500, "setServiceInfo(): invalid name: " + serviceName);
                        this.mServicePackages.remove(userId);
                    } else {
                        this.mServicePackages.put(userId, componentName.getPackageName());
                    }
                } else {
                    this.mServicePackages.remove(userId);
                }
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:17:0x004c, code lost:
            if (android.os.Build.IS_USER == false) goto L_0x0091;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:19:0x0058, code lost:
            if (com.android.server.contentcapture.ContentCaptureManagerService.access$3700(r10.this$0).isTemporary(r11) == false) goto L_0x0091;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:21:0x0064, code lost:
            if (r12.equals(r10.mServicePackages.get(r11)) != false) goto L_0x0091;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:22:0x0066, code lost:
            r1 = com.android.server.contentcapture.ContentCaptureManagerService.access$3800(r10.this$0);
            android.util.Slog.w(r1, "Ignoring package " + r12 + " while using temporary service " + r10.mServicePackages.get(r11));
         */
        /* JADX WARNING: Code restructure failed: missing block: B:23:0x0090, code lost:
            return null;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:24:0x0091, code lost:
            if (r2 != false) goto L_0x00bb;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:25:0x0093, code lost:
            if (r0 != null) goto L_0x00bb;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:27:0x0099, code lost:
            if (r10.this$0.verbose == false) goto L_0x00ba;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:28:0x009b, code lost:
            r1 = com.android.server.contentcapture.ContentCaptureManagerService.access$3900(r10.this$0);
            android.util.Slog.v(r1, "getOptionsForPackage(" + r12 + "): not whitelisted");
         */
        /* JADX WARNING: Code restructure failed: missing block: B:29:0x00ba, code lost:
            return null;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:30:0x00bb, code lost:
            r3 = new android.content.ContentCaptureOptions(r10.this$0.mDevCfgLoggingLevel, r10.this$0.mDevCfgMaxBufferSize, r10.this$0.mDevCfgIdleFlushingFrequencyMs, r10.this$0.mDevCfgTextChangeFlushingFrequencyMs, r10.this$0.mDevCfgLogHistorySize, r0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:31:0x00da, code lost:
            if (r10.this$0.verbose == false) goto L_0x00fe;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:32:0x00dc, code lost:
            r3 = com.android.server.contentcapture.ContentCaptureManagerService.access$4000(r10.this$0);
            android.util.Slog.v(r3, "getOptionsForPackage(" + r12 + "): " + r3);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:33:0x00fe, code lost:
            return r3;
         */
        @com.android.internal.annotations.GuardedBy({"mGlobalWhitelistStateLock"})
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public android.content.ContentCaptureOptions getOptions(int r11, java.lang.String r12) {
            /*
                r10 = this;
                r0 = 0
                java.lang.Object r1 = r10.mGlobalWhitelistStateLock
                monitor-enter(r1)
                boolean r2 = r10.isWhitelisted(r11, r12)     // Catch:{ all -> 0x00ff }
                if (r2 != 0) goto L_0x0048
                android.util.ArraySet r3 = r10.getWhitelistedComponents(r11, r12)     // Catch:{ all -> 0x00ff }
                r0 = r3
                if (r0 != 0) goto L_0x0048
                android.util.SparseArray<java.lang.String> r3 = r10.mServicePackages     // Catch:{ all -> 0x00ff }
                java.lang.Object r3 = r3.get(r11)     // Catch:{ all -> 0x00ff }
                boolean r3 = r12.equals(r3)     // Catch:{ all -> 0x00ff }
                if (r3 == 0) goto L_0x0048
                com.android.server.contentcapture.ContentCaptureManagerService r3 = com.android.server.contentcapture.ContentCaptureManagerService.this     // Catch:{ all -> 0x00ff }
                boolean r3 = r3.verbose     // Catch:{ all -> 0x00ff }
                if (r3 == 0) goto L_0x003d
                com.android.server.contentcapture.ContentCaptureManagerService r3 = com.android.server.contentcapture.ContentCaptureManagerService.this     // Catch:{ all -> 0x00ff }
                java.lang.String r3 = r3.mTag     // Catch:{ all -> 0x00ff }
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ff }
                r4.<init>()     // Catch:{ all -> 0x00ff }
                java.lang.String r5 = "getOptionsForPackage() lite for "
                r4.append(r5)     // Catch:{ all -> 0x00ff }
                r4.append(r12)     // Catch:{ all -> 0x00ff }
                java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x00ff }
                android.util.Slog.v(r3, r4)     // Catch:{ all -> 0x00ff }
            L_0x003d:
                android.content.ContentCaptureOptions r3 = new android.content.ContentCaptureOptions     // Catch:{ all -> 0x00ff }
                com.android.server.contentcapture.ContentCaptureManagerService r4 = com.android.server.contentcapture.ContentCaptureManagerService.this     // Catch:{ all -> 0x00ff }
                int r4 = r4.mDevCfgLoggingLevel     // Catch:{ all -> 0x00ff }
                r3.<init>(r4)     // Catch:{ all -> 0x00ff }
                monitor-exit(r1)     // Catch:{ all -> 0x00ff }
                return r3
            L_0x0048:
                monitor-exit(r1)     // Catch:{ all -> 0x00ff }
                boolean r1 = android.os.Build.IS_USER
                r3 = 0
                if (r1 == 0) goto L_0x0091
                com.android.server.contentcapture.ContentCaptureManagerService r1 = com.android.server.contentcapture.ContentCaptureManagerService.this
                com.android.server.infra.ServiceNameResolver r1 = r1.mServiceNameResolver
                boolean r1 = r1.isTemporary(r11)
                if (r1 == 0) goto L_0x0091
                android.util.SparseArray<java.lang.String> r1 = r10.mServicePackages
                java.lang.Object r1 = r1.get(r11)
                boolean r1 = r12.equals(r1)
                if (r1 != 0) goto L_0x0091
                com.android.server.contentcapture.ContentCaptureManagerService r1 = com.android.server.contentcapture.ContentCaptureManagerService.this
                java.lang.String r1 = r1.mTag
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                java.lang.String r5 = "Ignoring package "
                r4.append(r5)
                r4.append(r12)
                java.lang.String r5 = " while using temporary service "
                r4.append(r5)
                android.util.SparseArray<java.lang.String> r5 = r10.mServicePackages
                java.lang.Object r5 = r5.get(r11)
                java.lang.String r5 = (java.lang.String) r5
                r4.append(r5)
                java.lang.String r4 = r4.toString()
                android.util.Slog.w(r1, r4)
                return r3
            L_0x0091:
                if (r2 != 0) goto L_0x00bb
                if (r0 != 0) goto L_0x00bb
                com.android.server.contentcapture.ContentCaptureManagerService r1 = com.android.server.contentcapture.ContentCaptureManagerService.this
                boolean r1 = r1.verbose
                if (r1 == 0) goto L_0x00ba
                com.android.server.contentcapture.ContentCaptureManagerService r1 = com.android.server.contentcapture.ContentCaptureManagerService.this
                java.lang.String r1 = r1.mTag
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                java.lang.String r5 = "getOptionsForPackage("
                r4.append(r5)
                r4.append(r12)
                java.lang.String r5 = "): not whitelisted"
                r4.append(r5)
                java.lang.String r4 = r4.toString()
                android.util.Slog.v(r1, r4)
            L_0x00ba:
                return r3
            L_0x00bb:
                android.content.ContentCaptureOptions r1 = new android.content.ContentCaptureOptions
                com.android.server.contentcapture.ContentCaptureManagerService r3 = com.android.server.contentcapture.ContentCaptureManagerService.this
                int r4 = r3.mDevCfgLoggingLevel
                com.android.server.contentcapture.ContentCaptureManagerService r3 = com.android.server.contentcapture.ContentCaptureManagerService.this
                int r5 = r3.mDevCfgMaxBufferSize
                com.android.server.contentcapture.ContentCaptureManagerService r3 = com.android.server.contentcapture.ContentCaptureManagerService.this
                int r6 = r3.mDevCfgIdleFlushingFrequencyMs
                com.android.server.contentcapture.ContentCaptureManagerService r3 = com.android.server.contentcapture.ContentCaptureManagerService.this
                int r7 = r3.mDevCfgTextChangeFlushingFrequencyMs
                com.android.server.contentcapture.ContentCaptureManagerService r3 = com.android.server.contentcapture.ContentCaptureManagerService.this
                int r8 = r3.mDevCfgLogHistorySize
                r3 = r1
                r9 = r0
                r3.<init>(r4, r5, r6, r7, r8, r9)
                com.android.server.contentcapture.ContentCaptureManagerService r3 = com.android.server.contentcapture.ContentCaptureManagerService.this
                boolean r3 = r3.verbose
                if (r3 == 0) goto L_0x00fe
                com.android.server.contentcapture.ContentCaptureManagerService r3 = com.android.server.contentcapture.ContentCaptureManagerService.this
                java.lang.String r3 = r3.mTag
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                java.lang.String r5 = "getOptionsForPackage("
                r4.append(r5)
                r4.append(r12)
                java.lang.String r5 = "): "
                r4.append(r5)
                r4.append(r1)
                java.lang.String r4 = r4.toString()
                android.util.Slog.v(r3, r4)
            L_0x00fe:
                return r1
            L_0x00ff:
                r2 = move-exception
                monitor-exit(r1)     // Catch:{ all -> 0x00ff }
                throw r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.contentcapture.ContentCaptureManagerService.GlobalContentCaptureOptions.getOptions(int, java.lang.String):android.content.ContentCaptureOptions");
        }

        public void dump(String prefix, PrintWriter pw) {
            ContentCaptureManagerService.super.dump(prefix, pw);
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
}
