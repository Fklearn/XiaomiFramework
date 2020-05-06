package com.android.server.timezone;

final class CheckToken {
    final int mOptimisticLockId;
    final PackageVersions mPackageVersions;

    CheckToken(int optimisticLockId, PackageVersions packageVersions) {
        this.mOptimisticLockId = optimisticLockId;
        if (packageVersions != null) {
            this.mPackageVersions = packageVersions;
            return;
        }
        throw new NullPointerException("packageVersions == null");
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x002b, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:?, code lost:
        $closeResource(r2, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x002f, code lost:
        throw r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public byte[] toByteArray() {
        /*
            r5 = this;
            java.io.ByteArrayOutputStream r0 = new java.io.ByteArrayOutputStream
            r1 = 12
            r0.<init>(r1)
            java.io.DataOutputStream r1 = new java.io.DataOutputStream     // Catch:{ IOException -> 0x0030 }
            r1.<init>(r0)     // Catch:{ IOException -> 0x0030 }
            r2 = 0
            int r3 = r5.mOptimisticLockId     // Catch:{ all -> 0x0029 }
            r1.writeInt(r3)     // Catch:{ all -> 0x0029 }
            com.android.server.timezone.PackageVersions r3 = r5.mPackageVersions     // Catch:{ all -> 0x0029 }
            long r3 = r3.mUpdateAppVersion     // Catch:{ all -> 0x0029 }
            r1.writeLong(r3)     // Catch:{ all -> 0x0029 }
            com.android.server.timezone.PackageVersions r3 = r5.mPackageVersions     // Catch:{ all -> 0x0029 }
            long r3 = r3.mDataAppVersion     // Catch:{ all -> 0x0029 }
            r1.writeLong(r3)     // Catch:{ all -> 0x0029 }
            $closeResource(r2, r1)     // Catch:{ IOException -> 0x0030 }
            byte[] r1 = r0.toByteArray()
            return r1
        L_0x0029:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x002b }
        L_0x002b:
            r3 = move-exception
            $closeResource(r2, r1)     // Catch:{ IOException -> 0x0030 }
            throw r3     // Catch:{ IOException -> 0x0030 }
        L_0x0030:
            r1 = move-exception
            java.lang.RuntimeException r2 = new java.lang.RuntimeException
            java.lang.String r3 = "Unable to write into a ByteArrayOutputStream"
            r2.<init>(r3, r1)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.timezone.CheckToken.toByteArray():byte[]");
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

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x002b, code lost:
        throw r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0027, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0028, code lost:
        $closeResource(r2, r1);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static com.android.server.timezone.CheckToken fromByteArray(byte[] r9) throws java.io.IOException {
        /*
            java.io.ByteArrayInputStream r0 = new java.io.ByteArrayInputStream
            r0.<init>(r9)
            java.io.DataInputStream r1 = new java.io.DataInputStream
            r1.<init>(r0)
            int r2 = r1.readInt()     // Catch:{ all -> 0x0025 }
            long r3 = r1.readLong()     // Catch:{ all -> 0x0025 }
            long r5 = r1.readLong()     // Catch:{ all -> 0x0025 }
            com.android.server.timezone.CheckToken r7 = new com.android.server.timezone.CheckToken     // Catch:{ all -> 0x0025 }
            com.android.server.timezone.PackageVersions r8 = new com.android.server.timezone.PackageVersions     // Catch:{ all -> 0x0025 }
            r8.<init>(r3, r5)     // Catch:{ all -> 0x0025 }
            r7.<init>(r2, r8)     // Catch:{ all -> 0x0025 }
            r8 = 0
            $closeResource(r8, r1)
            return r7
        L_0x0025:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x0027 }
        L_0x0027:
            r3 = move-exception
            $closeResource(r2, r1)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.timezone.CheckToken.fromByteArray(byte[]):com.android.server.timezone.CheckToken");
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CheckToken checkToken = (CheckToken) o;
        if (this.mOptimisticLockId != checkToken.mOptimisticLockId) {
            return false;
        }
        return this.mPackageVersions.equals(checkToken.mPackageVersions);
    }

    public int hashCode() {
        return (this.mOptimisticLockId * 31) + this.mPackageVersions.hashCode();
    }

    public String toString() {
        return "Token{mOptimisticLockId=" + this.mOptimisticLockId + ", mPackageVersions=" + this.mPackageVersions + '}';
    }
}
