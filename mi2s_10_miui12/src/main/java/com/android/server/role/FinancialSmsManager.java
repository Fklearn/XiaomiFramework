package com.android.server.role;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.os.RemoteCallback;
import android.os.RemoteException;
import android.os.UserHandle;
import android.service.sms.IFinancialSmsService;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import java.io.PrintWriter;
import java.util.ArrayList;

final class FinancialSmsManager {
    private static final String TAG = "FinancialSmsManager";
    private final Context mContext;
    /* access modifiers changed from: private */
    public final Object mLock = new Object();
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public ArrayList<Command> mQueuedCommands;
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public IFinancialSmsService mRemoteService;
    @GuardedBy({"mLock"})
    private ServiceConnection mServiceConnection;

    private interface Command {
        void run(IFinancialSmsService iFinancialSmsService) throws RemoteException;
    }

    FinancialSmsManager(Context context) {
        this.mContext = context;
    }

    /* access modifiers changed from: package-private */
    public ServiceInfo getServiceInfo() {
        String packageName = this.mContext.getPackageManager().getServicesSystemSharedLibraryPackageName();
        if (packageName == null) {
            Slog.w(TAG, "no external services package!");
            return null;
        }
        Intent intent = new Intent("android.service.sms.action.FINANCIAL_SERVICE_INTENT");
        intent.setPackage(packageName);
        ResolveInfo resolveInfo = this.mContext.getPackageManager().resolveService(intent, 4);
        if (resolveInfo != null && resolveInfo.serviceInfo != null) {
            return resolveInfo.serviceInfo;
        }
        Slog.w(TAG, "No valid components found.");
        return null;
    }

    private ComponentName getServiceComponentName() {
        ServiceInfo serviceInfo = getServiceInfo();
        if (serviceInfo == null) {
            return null;
        }
        ComponentName name = new ComponentName(serviceInfo.packageName, serviceInfo.name);
        if ("android.permission.BIND_FINANCIAL_SMS_SERVICE".equals(serviceInfo.permission)) {
            return name;
        }
        Slog.w(TAG, name.flattenToShortString() + " does not require permission " + "android.permission.BIND_FINANCIAL_SMS_SERVICE");
        return null;
    }

    /* access modifiers changed from: package-private */
    public void reset() {
        synchronized (this.mLock) {
            if (this.mServiceConnection != null) {
                this.mContext.unbindService(this.mServiceConnection);
                this.mServiceConnection = null;
            } else {
                Slog.d(TAG, "reset(): service is not bound. Do nothing.");
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 9 */
    /* JADX WARNING: Code restructure failed: missing block: B:10:?, code lost:
        android.util.Slog.w(TAG, "exception calling service: " + r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0069, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x006a, code lost:
        android.os.Binder.restoreCallingIdentity(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x006d, code lost:
        throw r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x006f, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:7:0x000d, code lost:
        r1 = move-exception;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:5:0x0007, B:23:0x0056] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void connectAndRun(com.android.server.role.FinancialSmsManager.Command r10) {
        /*
            r9 = this;
            java.lang.Object r0 = r9.mLock
            monitor-enter(r0)
            android.service.sms.IFinancialSmsService r1 = r9.mRemoteService     // Catch:{ all -> 0x0070 }
            if (r1 == 0) goto L_0x0026
            android.service.sms.IFinancialSmsService r1 = r9.mRemoteService     // Catch:{ RemoteException -> 0x000d }
            r10.run(r1)     // Catch:{ RemoteException -> 0x000d }
            goto L_0x0024
        L_0x000d:
            r1 = move-exception
            java.lang.String r2 = "FinancialSmsManager"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0070 }
            r3.<init>()     // Catch:{ all -> 0x0070 }
            java.lang.String r4 = "exception calling service: "
            r3.append(r4)     // Catch:{ all -> 0x0070 }
            r3.append(r1)     // Catch:{ all -> 0x0070 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0070 }
            android.util.Slog.w(r2, r3)     // Catch:{ all -> 0x0070 }
        L_0x0024:
            monitor-exit(r0)     // Catch:{ all -> 0x0070 }
            return
        L_0x0026:
            java.util.ArrayList<com.android.server.role.FinancialSmsManager$Command> r1 = r9.mQueuedCommands     // Catch:{ all -> 0x0070 }
            r2 = 1
            if (r1 != 0) goto L_0x0032
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ all -> 0x0070 }
            r1.<init>(r2)     // Catch:{ all -> 0x0070 }
            r9.mQueuedCommands = r1     // Catch:{ all -> 0x0070 }
        L_0x0032:
            java.util.ArrayList<com.android.server.role.FinancialSmsManager$Command> r1 = r9.mQueuedCommands     // Catch:{ all -> 0x0070 }
            r1.add(r10)     // Catch:{ all -> 0x0070 }
            android.content.ServiceConnection r1 = r9.mServiceConnection     // Catch:{ all -> 0x0070 }
            if (r1 == 0) goto L_0x003d
            monitor-exit(r0)     // Catch:{ all -> 0x0070 }
            return
        L_0x003d:
            com.android.server.role.FinancialSmsManager$1 r1 = new com.android.server.role.FinancialSmsManager$1     // Catch:{ all -> 0x0070 }
            r1.<init>()     // Catch:{ all -> 0x0070 }
            r9.mServiceConnection = r1     // Catch:{ all -> 0x0070 }
            android.content.ComponentName r1 = r9.getServiceComponentName()     // Catch:{ all -> 0x0070 }
            if (r1 == 0) goto L_0x006e
            android.content.Intent r3 = new android.content.Intent     // Catch:{ all -> 0x0070 }
            r3.<init>()     // Catch:{ all -> 0x0070 }
            r3.setComponent(r1)     // Catch:{ all -> 0x0070 }
            long r4 = android.os.Binder.clearCallingIdentity()     // Catch:{ all -> 0x0070 }
            android.content.Context r6 = r9.mContext     // Catch:{ all -> 0x0069 }
            android.content.ServiceConnection r7 = r9.mServiceConnection     // Catch:{ all -> 0x0069 }
            int r8 = android.os.UserHandle.getCallingUserId()     // Catch:{ all -> 0x0069 }
            android.os.UserHandle r8 = android.os.UserHandle.getUserHandleForUid(r8)     // Catch:{ all -> 0x0069 }
            r6.bindServiceAsUser(r3, r7, r2, r8)     // Catch:{ all -> 0x0069 }
            android.os.Binder.restoreCallingIdentity(r4)     // Catch:{ all -> 0x0070 }
            goto L_0x006e
        L_0x0069:
            r2 = move-exception
            android.os.Binder.restoreCallingIdentity(r4)     // Catch:{ all -> 0x0070 }
            throw r2     // Catch:{ all -> 0x0070 }
        L_0x006e:
            monitor-exit(r0)     // Catch:{ all -> 0x0070 }
            return
        L_0x0070:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0070 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.role.FinancialSmsManager.connectAndRun(com.android.server.role.FinancialSmsManager$Command):void");
    }

    /* access modifiers changed from: package-private */
    public void getSmsMessages(RemoteCallback callback, Bundle params) {
        connectAndRun(new Command(callback, params) {
            private final /* synthetic */ RemoteCallback f$0;
            private final /* synthetic */ Bundle f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void run(IFinancialSmsService iFinancialSmsService) {
                iFinancialSmsService.getSmsMessages(this.f$0, this.f$1);
            }
        });
    }

    /* access modifiers changed from: package-private */
    public void dump(String prefix, PrintWriter pw) {
        ComponentName impl = getServiceComponentName();
        pw.print(prefix);
        pw.print("User ID: ");
        pw.println(UserHandle.getCallingUserId());
        pw.print(prefix);
        pw.print("Queued commands: ");
        ArrayList<Command> arrayList = this.mQueuedCommands;
        if (arrayList == null) {
            pw.println("N/A");
        } else {
            pw.println(arrayList.size());
        }
        pw.print(prefix);
        pw.print("Implementation: ");
        if (impl == null) {
            pw.println("N/A");
        } else {
            pw.println(impl.flattenToShortString());
        }
    }
}
