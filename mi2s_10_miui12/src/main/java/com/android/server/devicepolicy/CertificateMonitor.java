package com.android.server.devicepolicy;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.storage.StorageManager;
import android.util.Log;
import com.android.internal.notification.SystemNotificationChannels;
import com.android.server.devicepolicy.DevicePolicyManagerService;
import com.android.server.pm.DumpState;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;

public class CertificateMonitor {
    protected static final String LOG_TAG = "DevicePolicyManager";
    protected static final int MONITORING_CERT_NOTIFICATION_ID = 33;
    private final Handler mHandler;
    private final DevicePolicyManagerService.Injector mInjector;
    private final BroadcastReceiver mRootCaReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (!StorageManager.inCryptKeeperBounce()) {
                CertificateMonitor.this.updateInstalledCertificates(UserHandle.of(intent.getIntExtra("android.intent.extra.user_handle", getSendingUserId())));
            }
        }
    };
    private final DevicePolicyManagerService mService;

    public CertificateMonitor(DevicePolicyManagerService service, DevicePolicyManagerService.Injector injector, Handler handler) {
        this.mService = service;
        this.mInjector = injector;
        this.mHandler = handler;
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.USER_STARTED");
        filter.addAction("android.intent.action.USER_UNLOCKED");
        filter.addAction("android.security.action.TRUST_STORE_CHANGED");
        filter.setPriority(1000);
        this.mInjector.mContext.registerReceiverAsUser(this.mRootCaReceiver, UserHandle.ALL, filter, (String) null, this.mHandler);
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x002a, code lost:
        r6 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x002b, code lost:
        if (r4 != null) goto L_0x002d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:?, code lost:
        $closeResource(r5, r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0030, code lost:
        throw r6;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String installCaCert(android.os.UserHandle r8, byte[] r9) {
        /*
            r7 = this;
            java.lang.String r0 = "installCaCertsToKeyChain(): "
            java.lang.String r1 = "DevicePolicyManager"
            r2 = 0
            java.security.cert.X509Certificate r3 = parseCert(r9)     // Catch:{ IOException | CertificateException -> 0x0043 }
            r4 = 1
            java.security.cert.Certificate[] r4 = new java.security.cert.Certificate[r4]     // Catch:{ IOException | CertificateException -> 0x0043 }
            r5 = 0
            r4[r5] = r3     // Catch:{ IOException | CertificateException -> 0x0043 }
            byte[] r4 = android.security.Credentials.convertToPem(r4)     // Catch:{ IOException | CertificateException -> 0x0043 }
            r3 = r4
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r4 = r7.mInjector     // Catch:{ RemoteException -> 0x003d, InterruptedException -> 0x0031 }
            android.security.KeyChain$KeyChainConnection r4 = r4.keyChainBindAsUser(r8)     // Catch:{ RemoteException -> 0x003d, InterruptedException -> 0x0031 }
            android.security.IKeyChainService r5 = r4.getService()     // Catch:{ all -> 0x0028 }
            java.lang.String r5 = r5.installCaCertificate(r3)     // Catch:{ all -> 0x0028 }
            $closeResource(r2, r4)     // Catch:{ RemoteException -> 0x003d, InterruptedException -> 0x0031 }
            return r5
        L_0x0028:
            r5 = move-exception
            throw r5     // Catch:{ all -> 0x002a }
        L_0x002a:
            r6 = move-exception
            if (r4 == 0) goto L_0x0030
            $closeResource(r5, r4)     // Catch:{ RemoteException -> 0x003d, InterruptedException -> 0x0031 }
        L_0x0030:
            throw r6     // Catch:{ RemoteException -> 0x003d, InterruptedException -> 0x0031 }
        L_0x0031:
            r4 = move-exception
            android.util.Log.w(r1, r0, r4)
            java.lang.Thread r0 = java.lang.Thread.currentThread()
            r0.interrupt()
            goto L_0x0042
        L_0x003d:
            r4 = move-exception
            android.util.Log.e(r1, r0, r4)
        L_0x0042:
            return r2
        L_0x0043:
            r0 = move-exception
            java.lang.String r3 = "Problem converting cert"
            android.util.Log.e(r1, r3, r0)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.CertificateMonitor.installCaCert(android.os.UserHandle, byte[]):java.lang.String");
    }

    private static /* synthetic */ void $closeResource(Throwable x0, AutoCloseable x1) {
        if (x0 != null) {
            try {
                x1.close();
            } catch (Throwable th) {
                x0.addSuppressed(th);
            }
        } else {
            x1.close();
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0021, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0022, code lost:
        if (r1 != null) goto L_0x0024;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:?, code lost:
        $closeResource(r2, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0027, code lost:
        throw r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void uninstallCaCerts(android.os.UserHandle r7, java.lang.String[] r8) {
        /*
            r6 = this;
            java.lang.String r0 = "DevicePolicyManager"
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r1 = r6.mInjector     // Catch:{ RemoteException -> 0x0036, InterruptedException -> 0x0028 }
            android.security.KeyChain$KeyChainConnection r1 = r1.keyChainBindAsUser(r7)     // Catch:{ RemoteException -> 0x0036, InterruptedException -> 0x0028 }
            r2 = 0
            r3 = 0
        L_0x000a:
            int r4 = r8.length     // Catch:{ all -> 0x001f }
            if (r3 >= r4) goto L_0x0019
            android.security.IKeyChainService r4 = r1.getService()     // Catch:{ all -> 0x001f }
            r5 = r8[r3]     // Catch:{ all -> 0x001f }
            r4.deleteCaCertificate(r5)     // Catch:{ all -> 0x001f }
            int r3 = r3 + 1
            goto L_0x000a
        L_0x0019:
            if (r1 == 0) goto L_0x003c
            $closeResource(r2, r1)     // Catch:{ RemoteException -> 0x0036, InterruptedException -> 0x0028 }
            goto L_0x003c
        L_0x001f:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x0021 }
        L_0x0021:
            r3 = move-exception
            if (r1 == 0) goto L_0x0027
            $closeResource(r2, r1)     // Catch:{ RemoteException -> 0x0036, InterruptedException -> 0x0028 }
        L_0x0027:
            throw r3     // Catch:{ RemoteException -> 0x0036, InterruptedException -> 0x0028 }
        L_0x0028:
            r1 = move-exception
            java.lang.String r2 = "CaCertUninstaller: "
            android.util.Log.w(r0, r2, r1)
            java.lang.Thread r0 = java.lang.Thread.currentThread()
            r0.interrupt()
            goto L_0x003d
        L_0x0036:
            r1 = move-exception
            java.lang.String r2 = "from CaCertUninstaller: "
            android.util.Log.e(r0, r2, r1)
        L_0x003c:
        L_0x003d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.CertificateMonitor.uninstallCaCerts(android.os.UserHandle, java.lang.String[]):void");
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0019, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x001a, code lost:
        if (r1 != null) goto L_0x001c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:?, code lost:
        $closeResource(r2, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x001f, code lost:
        throw r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<java.lang.String> getInstalledCaCertificates(android.os.UserHandle r5) throws android.os.RemoteException, java.lang.RuntimeException {
        /*
            r4 = this;
            r0 = 0
            com.android.server.devicepolicy.DevicePolicyManagerService$Injector r1 = r4.mInjector     // Catch:{ InterruptedException -> 0x0027, AssertionError -> 0x0020 }
            android.security.KeyChain$KeyChainConnection r1 = r1.keyChainBindAsUser(r5)     // Catch:{ InterruptedException -> 0x0027, AssertionError -> 0x0020 }
            android.security.IKeyChainService r2 = r1.getService()     // Catch:{ all -> 0x0017 }
            android.content.pm.StringParceledListSlice r2 = r2.getUserCaAliases()     // Catch:{ all -> 0x0017 }
            java.util.List r2 = r2.getList()     // Catch:{ all -> 0x0017 }
            $closeResource(r0, r1)     // Catch:{ InterruptedException -> 0x0027, AssertionError -> 0x0020 }
            return r2
        L_0x0017:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x0019 }
        L_0x0019:
            r3 = move-exception
            if (r1 == 0) goto L_0x001f
            $closeResource(r2, r1)     // Catch:{ InterruptedException -> 0x0027, AssertionError -> 0x0020 }
        L_0x001f:
            throw r3     // Catch:{ InterruptedException -> 0x0027, AssertionError -> 0x0020 }
        L_0x0020:
            r0 = move-exception
            java.lang.RuntimeException r1 = new java.lang.RuntimeException
            r1.<init>(r0)
            throw r1
        L_0x0027:
            r1 = move-exception
            java.lang.Thread r2 = java.lang.Thread.currentThread()
            r2.interrupt()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.devicepolicy.CertificateMonitor.getInstalledCaCertificates(android.os.UserHandle):java.util.List");
    }

    public /* synthetic */ void lambda$onCertificateApprovalsChanged$0$CertificateMonitor(int userId) {
        updateInstalledCertificates(UserHandle.of(userId));
    }

    public void onCertificateApprovalsChanged(int userId) {
        this.mHandler.post(new Runnable(userId) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                CertificateMonitor.this.lambda$onCertificateApprovalsChanged$0$CertificateMonitor(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    public void updateInstalledCertificates(UserHandle userHandle) {
        if (this.mInjector.getUserManager().isUserUnlocked(userHandle.getIdentifier())) {
            try {
                List<String> installedCerts = getInstalledCaCertificates(userHandle);
                this.mService.onInstalledCertificatesChanged(userHandle, installedCerts);
                int pendingCertificateCount = installedCerts.size() - this.mService.getAcceptedCaCertificates(userHandle).size();
                if (pendingCertificateCount != 0) {
                    this.mInjector.getNotificationManager().notifyAsUser(LOG_TAG, 33, buildNotification(userHandle, pendingCertificateCount), userHandle);
                    return;
                }
                this.mInjector.getNotificationManager().cancelAsUser(LOG_TAG, 33, userHandle);
            } catch (RemoteException | RuntimeException e) {
                Log.e(LOG_TAG, "Could not retrieve certificates from KeyChain service", e);
            }
        }
    }

    private Notification buildNotification(UserHandle userHandle, int pendingCertificateCount) {
        String contentText;
        int parentUserId;
        int smallIconId;
        UserHandle userHandle2 = userHandle;
        int i = pendingCertificateCount;
        try {
            Context userContext = this.mInjector.createContextAsUser(userHandle2);
            Resources resources = this.mInjector.getResources();
            int parentUserId2 = userHandle.getIdentifier();
            if (this.mService.getProfileOwner(userHandle.getIdentifier()) != null) {
                String contentText2 = resources.getString(17041156, new Object[]{this.mService.getProfileOwnerName(userHandle.getIdentifier())});
                parentUserId = this.mService.getProfileParentId(userHandle.getIdentifier());
                contentText = contentText2;
                smallIconId = 17303600;
            } else if (this.mService.getDeviceOwnerUserId() == userHandle.getIdentifier()) {
                String deviceOwnerName = this.mService.getDeviceOwnerName();
                parentUserId = parentUserId2;
                smallIconId = 17303600;
                contentText = resources.getString(17041156, new Object[]{this.mService.getDeviceOwnerName()});
            } else {
                parentUserId = parentUserId2;
                contentText = resources.getString(17041155);
                smallIconId = 17301642;
            }
            Intent dialogIntent = new Intent("com.android.settings.MONITORING_CERT_INFO");
            dialogIntent.setFlags(268468224);
            dialogIntent.putExtra("android.settings.extra.number_of_certificates", i);
            dialogIntent.putExtra("android.intent.extra.USER_ID", userHandle.getIdentifier());
            ActivityInfo targetInfo = dialogIntent.resolveActivityInfo(this.mInjector.getPackageManager(), DumpState.DUMP_DEXOPT);
            if (targetInfo != null) {
                dialogIntent.setComponent(targetInfo.getComponentName());
            }
            Intent intent = dialogIntent;
            return new Notification.Builder(userContext, SystemNotificationChannels.SECURITY).setSmallIcon(smallIconId).setContentTitle(resources.getQuantityText(18153500, i)).setContentText(contentText).setContentIntent(this.mInjector.pendingIntentGetActivityAsUser(userContext, 0, dialogIntent, 134217728, (Bundle) null, UserHandle.of(parentUserId))).setShowWhen(false).setColor(17170460).build();
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(LOG_TAG, "Create context as " + userHandle2 + " failed", e);
            return null;
        }
    }

    private static X509Certificate parseCert(byte[] certBuffer) throws CertificateException {
        return (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(certBuffer));
    }
}
