package com.android.server.pm;

import android.content.Context;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;

public class ProtectedPackages {
    private final Context mContext;
    @GuardedBy({"this"})
    private String mDeviceOwnerPackage;
    @GuardedBy({"this"})
    private int mDeviceOwnerUserId;
    @GuardedBy({"this"})
    private final String mDeviceProvisioningPackage = this.mContext.getResources().getString(17039741);
    @GuardedBy({"this"})
    private SparseArray<String> mProfileOwnerPackages;

    public ProtectedPackages(Context context) {
        this.mContext = context;
    }

    public synchronized void setDeviceAndProfileOwnerPackages(int deviceOwnerUserId, String deviceOwnerPackage, SparseArray<String> profileOwnerPackages) {
        this.mDeviceOwnerUserId = deviceOwnerUserId;
        SparseArray<String> sparseArray = null;
        this.mDeviceOwnerPackage = deviceOwnerUserId == -10000 ? null : deviceOwnerPackage;
        if (profileOwnerPackages != null) {
            sparseArray = profileOwnerPackages.clone();
        }
        this.mProfileOwnerPackages = sparseArray;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:22:0x002c, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private synchronized boolean hasDeviceOwnerOrProfileOwner(int r4, java.lang.String r5) {
        /*
            r3 = this;
            monitor-enter(r3)
            r0 = 0
            if (r5 != 0) goto L_0x0006
            monitor-exit(r3)
            return r0
        L_0x0006:
            java.lang.String r1 = r3.mDeviceOwnerPackage     // Catch:{ all -> 0x002d }
            r2 = 1
            if (r1 == 0) goto L_0x0019
            int r1 = r3.mDeviceOwnerUserId     // Catch:{ all -> 0x002d }
            if (r1 != r4) goto L_0x0019
            java.lang.String r1 = r3.mDeviceOwnerPackage     // Catch:{ all -> 0x002d }
            boolean r1 = r5.equals(r1)     // Catch:{ all -> 0x002d }
            if (r1 == 0) goto L_0x0019
            monitor-exit(r3)
            return r2
        L_0x0019:
            android.util.SparseArray<java.lang.String> r1 = r3.mProfileOwnerPackages     // Catch:{ all -> 0x002d }
            if (r1 == 0) goto L_0x002b
            android.util.SparseArray<java.lang.String> r1 = r3.mProfileOwnerPackages     // Catch:{ all -> 0x002d }
            java.lang.Object r1 = r1.get(r4)     // Catch:{ all -> 0x002d }
            boolean r1 = r5.equals(r1)     // Catch:{ all -> 0x002d }
            if (r1 == 0) goto L_0x002b
            monitor-exit(r3)
            return r2
        L_0x002b:
            monitor-exit(r3)
            return r0
        L_0x002d:
            r4 = move-exception
            monitor-exit(r3)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.ProtectedPackages.hasDeviceOwnerOrProfileOwner(int, java.lang.String):boolean");
    }

    public synchronized String getDeviceOwnerOrProfileOwnerPackage(int userId) {
        if (this.mDeviceOwnerUserId == userId) {
            return this.mDeviceOwnerPackage;
        }
        return this.mProfileOwnerPackages.get(userId);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x002f, code lost:
        if (r5.equals(r4.mDeviceProvisioningPackage) != false) goto L_0x0033;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0034, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private synchronized boolean isProtectedPackage(java.lang.String r5) {
        /*
            r4 = this;
            monitor-enter(r4)
            android.content.Context r0 = r4.mContext     // Catch:{ all -> 0x0035 }
            boolean r0 = com.miui.enterprise.ApplicationHelper.shouldKeeAlive(r0, r5)     // Catch:{ all -> 0x0035 }
            r1 = 1
            if (r0 == 0) goto L_0x0027
            java.lang.String r0 = "Enterprise"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0035 }
            r2.<init>()     // Catch:{ all -> 0x0035 }
            java.lang.String r3 = "Package "
            r2.append(r3)     // Catch:{ all -> 0x0035 }
            r2.append(r5)     // Catch:{ all -> 0x0035 }
            java.lang.String r3 = " is protected"
            r2.append(r3)     // Catch:{ all -> 0x0035 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0035 }
            android.util.Slog.d(r0, r2)     // Catch:{ all -> 0x0035 }
            monitor-exit(r4)
            return r1
        L_0x0027:
            if (r5 == 0) goto L_0x0032
            java.lang.String r0 = r4.mDeviceProvisioningPackage     // Catch:{ all -> 0x0035 }
            boolean r0 = r5.equals(r0)     // Catch:{ all -> 0x0035 }
            if (r0 == 0) goto L_0x0032
            goto L_0x0033
        L_0x0032:
            r1 = 0
        L_0x0033:
            monitor-exit(r4)
            return r1
        L_0x0035:
            r5 = move-exception
            monitor-exit(r4)
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.ProtectedPackages.isProtectedPackage(java.lang.String):boolean");
    }

    public boolean isPackageStateProtected(int userId, String packageName) {
        return hasDeviceOwnerOrProfileOwner(userId, packageName) || isProtectedPackage(packageName);
    }

    public boolean isPackageDataProtected(int userId, String packageName) {
        return hasDeviceOwnerOrProfileOwner(userId, packageName) || isProtectedPackage(packageName);
    }
}
