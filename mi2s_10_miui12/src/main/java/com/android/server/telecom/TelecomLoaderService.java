package com.android.server.telecom;

import android.app.role.OnRoleHoldersChangedListener;
import android.app.role.RoleManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManagerInternal;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.util.IntArray;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.telephony.SmsApplication;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.pm.UserManagerService;
import com.android.server.pm.permission.DefaultPermissionGrantPolicy;
import com.android.server.pm.permission.PermissionManagerServiceInternal;

public class TelecomLoaderService extends SystemService {
    private static final String SERVICE_ACTION = "com.android.ITelecomService";
    private static final ComponentName SERVICE_COMPONENT = new ComponentName("com.android.server.telecom", "com.android.server.telecom.components.TelecomService");
    private static final String TAG = "TelecomLoaderService";
    /* access modifiers changed from: private */
    public final Context mContext;
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public IntArray mDefaultSimCallManagerRequests;
    /* access modifiers changed from: private */
    public final Object mLock = new Object();
    @GuardedBy({"mLock"})
    private TelecomServiceConnection mServiceConnection;

    private class TelecomServiceConnection implements ServiceConnection {
        private TelecomServiceConnection() {
        }

        /* Debug info: failed to restart local var, previous not found, register: 9 */
        public void onServiceConnected(ComponentName name, IBinder service) {
            PhoneAccountHandle phoneAccount;
            try {
                service.linkToDeath(new IBinder.DeathRecipient() {
                    public void binderDied() {
                        TelecomLoaderService.this.connectToTelecom();
                    }
                }, 0);
                SmsApplication.getDefaultMmsApplication(TelecomLoaderService.this.mContext, false);
                ServiceManager.addService("telecom", service);
                synchronized (TelecomLoaderService.this.mLock) {
                    if (TelecomLoaderService.this.mDefaultSimCallManagerRequests != null) {
                        DefaultPermissionGrantPolicy permissionPolicy = TelecomLoaderService.this.getDefaultPermissionGrantPolicy();
                        if (!(TelecomLoaderService.this.mDefaultSimCallManagerRequests == null || (phoneAccount = ((TelecomManager) TelecomLoaderService.this.mContext.getSystemService("telecom")).getSimCallManager()) == null)) {
                            int requestCount = TelecomLoaderService.this.mDefaultSimCallManagerRequests.size();
                            String packageName = phoneAccount.getComponentName().getPackageName();
                            for (int i = requestCount - 1; i >= 0; i--) {
                                int userId = TelecomLoaderService.this.mDefaultSimCallManagerRequests.get(i);
                                TelecomLoaderService.this.mDefaultSimCallManagerRequests.remove(i);
                                permissionPolicy.grantDefaultPermissionsToDefaultSimCallManager(packageName, userId);
                            }
                        }
                    }
                }
            } catch (RemoteException e) {
                Slog.w(TelecomLoaderService.TAG, "Failed linking to death.");
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            TelecomLoaderService.this.connectToTelecom();
        }
    }

    /* access modifiers changed from: private */
    public DefaultPermissionGrantPolicy getDefaultPermissionGrantPolicy() {
        return ((PermissionManagerServiceInternal) LocalServices.getService(PermissionManagerServiceInternal.class)).getDefaultPermissionGrantPolicy();
    }

    public TelecomLoaderService(Context context) {
        super(context);
        this.mContext = context;
        registerDefaultAppProviders();
    }

    public void onStart() {
    }

    public void onBootPhase(int phase) {
        if (phase == 550) {
            registerDefaultAppNotifier();
            registerCarrierConfigChangedReceiver();
        } else if (phase == 600) {
            connectToTelecom();
        }
    }

    /* access modifiers changed from: private */
    public void connectToTelecom() {
        synchronized (this.mLock) {
            if (this.mServiceConnection != null) {
                this.mContext.unbindService(this.mServiceConnection);
                this.mServiceConnection = null;
            }
            TelecomServiceConnection serviceConnection = new TelecomServiceConnection();
            Intent intent = new Intent(SERVICE_ACTION);
            intent.setComponent(SERVICE_COMPONENT);
            if (this.mContext.bindServiceAsUser(intent, serviceConnection, 67108929, UserHandle.SYSTEM)) {
                this.mServiceConnection = serviceConnection;
            }
        }
    }

    private void registerDefaultAppProviders() {
        DefaultPermissionGrantPolicy permissionPolicy = getDefaultPermissionGrantPolicy();
        permissionPolicy.setSmsAppPackagesProvider(new PackageManagerInternal.PackagesProvider() {
            public final String[] getPackages(int i) {
                return TelecomLoaderService.this.lambda$registerDefaultAppProviders$0$TelecomLoaderService(i);
            }
        });
        permissionPolicy.setDialerAppPackagesProvider(new PackageManagerInternal.PackagesProvider() {
            public final String[] getPackages(int i) {
                return TelecomLoaderService.this.lambda$registerDefaultAppProviders$1$TelecomLoaderService(i);
            }
        });
        permissionPolicy.setSimCallManagerPackagesProvider(new PackageManagerInternal.PackagesProvider() {
            public final String[] getPackages(int i) {
                return TelecomLoaderService.this.lambda$registerDefaultAppProviders$2$TelecomLoaderService(i);
            }
        });
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x001d, code lost:
        return new java.lang.String[]{r0.getPackageName()};
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x001e, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x000b, code lost:
        r0 = com.android.internal.telephony.SmsApplication.getDefaultSmsApplication(r4.mContext, true);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0012, code lost:
        if (r0 == null) goto L_0x001e;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ java.lang.String[] lambda$registerDefaultAppProviders$0$TelecomLoaderService(int r5) {
        /*
            r4 = this;
            java.lang.Object r0 = r4.mLock
            monitor-enter(r0)
            com.android.server.telecom.TelecomLoaderService$TelecomServiceConnection r1 = r4.mServiceConnection     // Catch:{ all -> 0x001f }
            r2 = 0
            if (r1 != 0) goto L_0x000a
            monitor-exit(r0)     // Catch:{ all -> 0x001f }
            return r2
        L_0x000a:
            monitor-exit(r0)     // Catch:{ all -> 0x001f }
            android.content.Context r0 = r4.mContext
            r1 = 1
            android.content.ComponentName r0 = com.android.internal.telephony.SmsApplication.getDefaultSmsApplication(r0, r1)
            if (r0 == 0) goto L_0x001e
            java.lang.String[] r1 = new java.lang.String[r1]
            r2 = 0
            java.lang.String r3 = r0.getPackageName()
            r1[r2] = r3
            return r1
        L_0x001e:
            return r2
        L_0x001f:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x001f }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.telecom.TelecomLoaderService.lambda$registerDefaultAppProviders$0$TelecomLoaderService(int):java.lang.String[]");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0019, code lost:
        return new java.lang.String[]{r0};
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x001a, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x000b, code lost:
        r0 = android.telecom.DefaultDialerManager.getDefaultDialerApplication(r3.mContext);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0011, code lost:
        if (r0 == null) goto L_0x001a;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ java.lang.String[] lambda$registerDefaultAppProviders$1$TelecomLoaderService(int r4) {
        /*
            r3 = this;
            java.lang.Object r0 = r3.mLock
            monitor-enter(r0)
            com.android.server.telecom.TelecomLoaderService$TelecomServiceConnection r1 = r3.mServiceConnection     // Catch:{ all -> 0x001b }
            r2 = 0
            if (r1 != 0) goto L_0x000a
            monitor-exit(r0)     // Catch:{ all -> 0x001b }
            return r2
        L_0x000a:
            monitor-exit(r0)     // Catch:{ all -> 0x001b }
            android.content.Context r0 = r3.mContext
            java.lang.String r0 = android.telecom.DefaultDialerManager.getDefaultDialerApplication(r0)
            if (r0 == 0) goto L_0x001a
            r1 = 1
            java.lang.String[] r1 = new java.lang.String[r1]
            r2 = 0
            r1[r2] = r0
            return r1
        L_0x001a:
            return r2
        L_0x001b:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x001b }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.telecom.TelecomLoaderService.lambda$registerDefaultAppProviders$1$TelecomLoaderService(int):java.lang.String[]");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x001b, code lost:
        r1 = ((android.telecom.TelecomManager) r5.mContext.getSystemService("telecom")).getSimCallManager(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x002a, code lost:
        if (r1 == null) goto L_0x003b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x003a, code lost:
        return new java.lang.String[]{r1.getComponentName().getPackageName()};
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x003b, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ java.lang.String[] lambda$registerDefaultAppProviders$2$TelecomLoaderService(int r6) {
        /*
            r5 = this;
            java.lang.Object r0 = r5.mLock
            monitor-enter(r0)
            com.android.server.telecom.TelecomLoaderService$TelecomServiceConnection r1 = r5.mServiceConnection     // Catch:{ all -> 0x003c }
            r2 = 0
            if (r1 != 0) goto L_0x001a
            android.util.IntArray r1 = r5.mDefaultSimCallManagerRequests     // Catch:{ all -> 0x003c }
            if (r1 != 0) goto L_0x0013
            android.util.IntArray r1 = new android.util.IntArray     // Catch:{ all -> 0x003c }
            r1.<init>()     // Catch:{ all -> 0x003c }
            r5.mDefaultSimCallManagerRequests = r1     // Catch:{ all -> 0x003c }
        L_0x0013:
            android.util.IntArray r1 = r5.mDefaultSimCallManagerRequests     // Catch:{ all -> 0x003c }
            r1.add(r6)     // Catch:{ all -> 0x003c }
            monitor-exit(r0)     // Catch:{ all -> 0x003c }
            return r2
        L_0x001a:
            monitor-exit(r0)     // Catch:{ all -> 0x003c }
            android.content.Context r0 = r5.mContext
            java.lang.String r1 = "telecom"
            java.lang.Object r0 = r0.getSystemService(r1)
            android.telecom.TelecomManager r0 = (android.telecom.TelecomManager) r0
            android.telecom.PhoneAccountHandle r1 = r0.getSimCallManager(r6)
            if (r1 == 0) goto L_0x003b
            r2 = 1
            java.lang.String[] r2 = new java.lang.String[r2]
            r3 = 0
            android.content.ComponentName r4 = r1.getComponentName()
            java.lang.String r4 = r4.getPackageName()
            r2[r3] = r4
            return r2
        L_0x003b:
            return r2
        L_0x003c:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x003c }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.telecom.TelecomLoaderService.lambda$registerDefaultAppProviders$2$TelecomLoaderService(int):java.lang.String[]");
    }

    private void registerDefaultAppNotifier() {
        ((RoleManager) this.mContext.getSystemService(RoleManager.class)).addOnRoleHoldersChangedListenerAsUser(this.mContext.getMainExecutor(), new OnRoleHoldersChangedListener(getDefaultPermissionGrantPolicy()) {
            private final /* synthetic */ DefaultPermissionGrantPolicy f$1;

            {
                this.f$1 = r2;
            }

            public final void onRoleHoldersChanged(String str, UserHandle userHandle) {
                TelecomLoaderService.this.lambda$registerDefaultAppNotifier$3$TelecomLoaderService(this.f$1, str, userHandle);
            }
        }, UserHandle.ALL);
    }

    public /* synthetic */ void lambda$registerDefaultAppNotifier$3$TelecomLoaderService(DefaultPermissionGrantPolicy permissionPolicy, String roleName, UserHandle user) {
        updateSimCallManagerPermissions(permissionPolicy, user.getIdentifier());
    }

    private void registerCarrierConfigChangedReceiver() {
        this.mContext.registerReceiverAsUser(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("android.telephony.action.CARRIER_CONFIG_CHANGED")) {
                    for (int userId : UserManagerService.getInstance().getUserIds()) {
                        TelecomLoaderService telecomLoaderService = TelecomLoaderService.this;
                        telecomLoaderService.updateSimCallManagerPermissions(telecomLoaderService.getDefaultPermissionGrantPolicy(), userId);
                    }
                }
            }
        }, UserHandle.ALL, new IntentFilter("android.telephony.action.CARRIER_CONFIG_CHANGED"), (String) null, (Handler) null);
    }

    /* access modifiers changed from: private */
    public void updateSimCallManagerPermissions(DefaultPermissionGrantPolicy permissionGrantPolicy, int userId) {
        PhoneAccountHandle phoneAccount = ((TelecomManager) this.mContext.getSystemService("telecom")).getSimCallManager(userId);
        if (phoneAccount != null) {
            Slog.i(TAG, "updating sim call manager permissions for userId:" + userId);
            permissionGrantPolicy.grantDefaultPermissionsToDefaultSimCallManager(phoneAccount.getComponentName().getPackageName(), userId);
        }
    }
}
