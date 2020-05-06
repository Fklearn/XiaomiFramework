package com.android.server.autofill;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.RemoteCallback;
import android.os.RemoteException;
import android.service.autofill.IAutofillFieldClassificationService;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Slog;
import android.view.autofill.AutofillValue;
import com.android.internal.annotations.GuardedBy;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class FieldClassificationStrategy {
    private static final String TAG = "FieldClassificationStrategy";
    private final Context mContext;
    /* access modifiers changed from: private */
    public final Object mLock = new Object();
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public ArrayList<Command> mQueuedCommands;
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public IAutofillFieldClassificationService mRemoteService;
    @GuardedBy({"mLock"})
    private ServiceConnection mServiceConnection;
    private final int mUserId;

    private interface Command {
        void run(IAutofillFieldClassificationService iAutofillFieldClassificationService) throws RemoteException;
    }

    private interface MetadataParser<T> {
        T get(Resources resources, int i);
    }

    public FieldClassificationStrategy(Context context, int userId) {
        this.mContext = context;
        this.mUserId = userId;
    }

    /* access modifiers changed from: package-private */
    public ServiceInfo getServiceInfo() {
        String packageName = this.mContext.getPackageManager().getServicesSystemSharedLibraryPackageName();
        if (packageName == null) {
            Slog.w(TAG, "no external services package!");
            return null;
        }
        Intent intent = new Intent("android.service.autofill.AutofillFieldClassificationService");
        intent.setPackage(packageName);
        ResolveInfo resolveInfo = this.mContext.getPackageManager().resolveService(intent, 132);
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
        if (!"android.permission.BIND_AUTOFILL_FIELD_CLASSIFICATION_SERVICE".equals(serviceInfo.permission)) {
            Slog.w(TAG, name.flattenToShortString() + " does not require permission " + "android.permission.BIND_AUTOFILL_FIELD_CLASSIFICATION_SERVICE");
            return null;
        }
        if (Helper.sVerbose) {
            Slog.v(TAG, "getServiceComponentName(): " + name);
        }
        return name;
    }

    /* access modifiers changed from: package-private */
    public void reset() {
        synchronized (this.mLock) {
            if (this.mServiceConnection != null) {
                if (Helper.sDebug) {
                    Slog.d(TAG, "reset(): unbinding service.");
                }
                this.mContext.unbindService(this.mServiceConnection);
                this.mServiceConnection = null;
            } else if (Helper.sDebug) {
                Slog.d(TAG, "reset(): service is not bound. Do nothing.");
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 9 */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0019, code lost:
        r1 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:?, code lost:
        android.util.Slog.w(TAG, "exception calling service: " + r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x00b6, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x00b7, code lost:
        android.os.Binder.restoreCallingIdentity(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x00ba, code lost:
        throw r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x00bc, code lost:
        return;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:5:0x0007, B:34:0x0091] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void connectAndRun(com.android.server.autofill.FieldClassificationStrategy.Command r10) {
        /*
            r9 = this;
            java.lang.Object r0 = r9.mLock
            monitor-enter(r0)
            android.service.autofill.IAutofillFieldClassificationService r1 = r9.mRemoteService     // Catch:{ all -> 0x00bd }
            if (r1 == 0) goto L_0x0032
            boolean r1 = com.android.server.autofill.Helper.sVerbose     // Catch:{ RemoteException -> 0x0019 }
            if (r1 == 0) goto L_0x0013
            java.lang.String r1 = "FieldClassificationStrategy"
            java.lang.String r2 = "running command right away"
            android.util.Slog.v(r1, r2)     // Catch:{ RemoteException -> 0x0019 }
        L_0x0013:
            android.service.autofill.IAutofillFieldClassificationService r1 = r9.mRemoteService     // Catch:{ RemoteException -> 0x0019 }
            r10.run(r1)     // Catch:{ RemoteException -> 0x0019 }
            goto L_0x0030
        L_0x0019:
            r1 = move-exception
            java.lang.String r2 = "FieldClassificationStrategy"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x00bd }
            r3.<init>()     // Catch:{ all -> 0x00bd }
            java.lang.String r4 = "exception calling service: "
            r3.append(r4)     // Catch:{ all -> 0x00bd }
            r3.append(r1)     // Catch:{ all -> 0x00bd }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x00bd }
            android.util.Slog.w(r2, r3)     // Catch:{ all -> 0x00bd }
        L_0x0030:
            monitor-exit(r0)     // Catch:{ all -> 0x00bd }
            return
        L_0x0032:
            boolean r1 = com.android.server.autofill.Helper.sDebug     // Catch:{ all -> 0x00bd }
            if (r1 == 0) goto L_0x003e
            java.lang.String r1 = "FieldClassificationStrategy"
            java.lang.String r2 = "service is null; queuing command"
            android.util.Slog.d(r1, r2)     // Catch:{ all -> 0x00bd }
        L_0x003e:
            java.util.ArrayList<com.android.server.autofill.FieldClassificationStrategy$Command> r1 = r9.mQueuedCommands     // Catch:{ all -> 0x00bd }
            r2 = 1
            if (r1 != 0) goto L_0x004a
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ all -> 0x00bd }
            r1.<init>(r2)     // Catch:{ all -> 0x00bd }
            r9.mQueuedCommands = r1     // Catch:{ all -> 0x00bd }
        L_0x004a:
            java.util.ArrayList<com.android.server.autofill.FieldClassificationStrategy$Command> r1 = r9.mQueuedCommands     // Catch:{ all -> 0x00bd }
            r1.add(r10)     // Catch:{ all -> 0x00bd }
            android.content.ServiceConnection r1 = r9.mServiceConnection     // Catch:{ all -> 0x00bd }
            if (r1 == 0) goto L_0x0055
            monitor-exit(r0)     // Catch:{ all -> 0x00bd }
            return
        L_0x0055:
            boolean r1 = com.android.server.autofill.Helper.sVerbose     // Catch:{ all -> 0x00bd }
            if (r1 == 0) goto L_0x0060
            java.lang.String r1 = "FieldClassificationStrategy"
            java.lang.String r3 = "creating connection"
            android.util.Slog.v(r1, r3)     // Catch:{ all -> 0x00bd }
        L_0x0060:
            com.android.server.autofill.FieldClassificationStrategy$1 r1 = new com.android.server.autofill.FieldClassificationStrategy$1     // Catch:{ all -> 0x00bd }
            r1.<init>()     // Catch:{ all -> 0x00bd }
            android.content.ComponentName r3 = r9.getServiceComponentName()     // Catch:{ all -> 0x00bd }
            boolean r4 = com.android.server.autofill.Helper.sVerbose     // Catch:{ all -> 0x00bd }
            if (r4 == 0) goto L_0x0083
            java.lang.String r4 = "FieldClassificationStrategy"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x00bd }
            r5.<init>()     // Catch:{ all -> 0x00bd }
            java.lang.String r6 = "binding to: "
            r5.append(r6)     // Catch:{ all -> 0x00bd }
            r5.append(r3)     // Catch:{ all -> 0x00bd }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x00bd }
            android.util.Slog.v(r4, r5)     // Catch:{ all -> 0x00bd }
        L_0x0083:
            if (r3 == 0) goto L_0x00bb
            android.content.Intent r4 = new android.content.Intent     // Catch:{ all -> 0x00bd }
            r4.<init>()     // Catch:{ all -> 0x00bd }
            r4.setComponent(r3)     // Catch:{ all -> 0x00bd }
            long r5 = android.os.Binder.clearCallingIdentity()     // Catch:{ all -> 0x00bd }
            android.content.Context r7 = r9.mContext     // Catch:{ all -> 0x00b6 }
            int r8 = r9.mUserId     // Catch:{ all -> 0x00b6 }
            android.os.UserHandle r8 = android.os.UserHandle.of(r8)     // Catch:{ all -> 0x00b6 }
            boolean r2 = r7.bindServiceAsUser(r4, r1, r2, r8)     // Catch:{ all -> 0x00b6 }
            if (r2 == 0) goto L_0x00a1
            r9.mServiceConnection = r1     // Catch:{ all -> 0x00b6 }
        L_0x00a1:
            boolean r7 = com.android.server.autofill.Helper.sVerbose     // Catch:{ all -> 0x00b6 }
            if (r7 == 0) goto L_0x00b2
            java.lang.String r7 = "FieldClassificationStrategy"
            if (r2 == 0) goto L_0x00ac
            java.lang.String r8 = "bound"
            goto L_0x00af
        L_0x00ac:
            java.lang.String r8 = "unbound"
        L_0x00af:
            android.util.Slog.v(r7, r8)     // Catch:{ all -> 0x00b6 }
        L_0x00b2:
            android.os.Binder.restoreCallingIdentity(r5)     // Catch:{ all -> 0x00bd }
            goto L_0x00bb
        L_0x00b6:
            r2 = move-exception
            android.os.Binder.restoreCallingIdentity(r5)     // Catch:{ all -> 0x00bd }
            throw r2     // Catch:{ all -> 0x00bd }
        L_0x00bb:
            monitor-exit(r0)     // Catch:{ all -> 0x00bd }
            return
        L_0x00bd:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x00bd }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.autofill.FieldClassificationStrategy.connectAndRun(com.android.server.autofill.FieldClassificationStrategy$Command):void");
    }

    /* access modifiers changed from: package-private */
    public String[] getAvailableAlgorithms() {
        return (String[]) getMetadataValue("android.autofill.field_classification.available_algorithms", $$Lambda$FieldClassificationStrategy$NQQgQ63vxhPkiwOWrnwRyuYSHTM.INSTANCE);
    }

    /* access modifiers changed from: package-private */
    public String getDefaultAlgorithm() {
        return (String) getMetadataValue("android.autofill.field_classification.default_algorithm", $$Lambda$FieldClassificationStrategy$vGIL1YGX_9ksoSV74T7gO4fkEBE.INSTANCE);
    }

    private <T> T getMetadataValue(String field, MetadataParser<T> parser) {
        ServiceInfo serviceInfo = getServiceInfo();
        if (serviceInfo == null) {
            return null;
        }
        try {
            return parser.get(this.mContext.getPackageManager().getResourcesForApplication(serviceInfo.applicationInfo), serviceInfo.metaData.getInt(field));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Error getting application resources for " + serviceInfo, e);
            return null;
        }
    }

    /* access modifiers changed from: package-private */
    public void calculateScores(RemoteCallback callback, List<AutofillValue> actualValues, String[] userDataValues, String[] categoryIds, String defaultAlgorithm, Bundle defaultArgs, ArrayMap<String, String> algorithms, ArrayMap<String, Bundle> args) {
        connectAndRun(new Command(callback, actualValues, userDataValues, categoryIds, defaultAlgorithm, defaultArgs, algorithms, args) {
            private final /* synthetic */ RemoteCallback f$0;
            private final /* synthetic */ List f$1;
            private final /* synthetic */ String[] f$2;
            private final /* synthetic */ String[] f$3;
            private final /* synthetic */ String f$4;
            private final /* synthetic */ Bundle f$5;
            private final /* synthetic */ ArrayMap f$6;
            private final /* synthetic */ ArrayMap f$7;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
                this.f$7 = r8;
            }

            public final void run(IAutofillFieldClassificationService iAutofillFieldClassificationService) {
                iAutofillFieldClassificationService.calculateScores(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
            }
        });
    }

    /* access modifiers changed from: package-private */
    public void dump(String prefix, PrintWriter pw) {
        ComponentName impl = getServiceComponentName();
        pw.print(prefix);
        pw.print("User ID: ");
        pw.println(this.mUserId);
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
            return;
        }
        pw.println(impl.flattenToShortString());
        try {
            pw.print(prefix);
            pw.print("Available algorithms: ");
            pw.println(Arrays.toString(getAvailableAlgorithms()));
            pw.print(prefix);
            pw.print("Default algorithm: ");
            pw.println(getDefaultAlgorithm());
        } catch (Exception e) {
            pw.print("ERROR CALLING SERVICE: ");
            pw.println(e);
        }
    }
}
