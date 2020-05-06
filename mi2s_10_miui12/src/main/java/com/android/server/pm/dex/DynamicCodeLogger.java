package com.android.server.pm.dex;

import android.content.pm.IPackageManager;
import android.os.FileUtils;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.EventLog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.pm.Installer;
import com.android.server.pm.dex.PackageDynamicCodeLoading;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class DynamicCodeLogger {
    private static final String DCL_DEX_SUBTAG = "dcl";
    private static final String DCL_NATIVE_SUBTAG = "dcln";
    private static final int SNET_TAG = 1397638484;
    private static final String TAG = "DynamicCodeLogger";
    private final Installer mInstaller;
    private final PackageDynamicCodeLoading mPackageDynamicCodeLoading;
    private final IPackageManager mPackageManager;

    DynamicCodeLogger(IPackageManager pms, Installer installer) {
        this(pms, installer, new PackageDynamicCodeLoading());
    }

    @VisibleForTesting
    DynamicCodeLogger(IPackageManager pms, Installer installer, PackageDynamicCodeLoading packageDynamicCodeLoading) {
        this.mPackageManager = pms;
        this.mPackageDynamicCodeLoading = packageDynamicCodeLoading;
        this.mInstaller = installer;
    }

    public Set<String> getAllPackagesWithDynamicCodeLoading() {
        return this.mPackageDynamicCodeLoading.getAllPackagesWithDynamicCodeLoading();
    }

    /* JADX WARNING: Removed duplicated region for block: B:43:0x0101  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x0104  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x011f  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x0137  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x0162  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void logDynamicCodeLoading(java.lang.String r24) {
        /*
            r23 = this;
            r1 = r23
            r8 = r24
            com.android.server.pm.dex.PackageDynamicCodeLoading$PackageDynamicCode r9 = r23.getPackageDynamicCodeInfo(r24)
            if (r9 != 0) goto L_0x000b
            return
        L_0x000b:
            android.util.SparseArray r0 = new android.util.SparseArray
            r0.<init>()
            r10 = r0
            r0 = 0
            java.util.Map<java.lang.String, com.android.server.pm.dex.PackageDynamicCodeLoading$DynamicCodeFile> r2 = r9.mFileUsageMap
            java.util.Set r2 = r2.entrySet()
            java.util.Iterator r11 = r2.iterator()
            r2 = r0
        L_0x001d:
            boolean r0 = r11.hasNext()
            if (r0 == 0) goto L_0x01b7
            java.lang.Object r0 = r11.next()
            r12 = r0
            java.util.Map$Entry r12 = (java.util.Map.Entry) r12
            java.lang.Object r0 = r12.getKey()
            r13 = r0
            java.lang.String r13 = (java.lang.String) r13
            java.lang.Object r0 = r12.getValue()
            r14 = r0
            com.android.server.pm.dex.PackageDynamicCodeLoading$DynamicCodeFile r14 = (com.android.server.pm.dex.PackageDynamicCodeLoading.DynamicCodeFile) r14
            int r15 = r14.mUserId
            int r16 = r10.indexOfKey(r15)
            r7 = 0
            java.lang.String r6 = "DynamicCodeLogger"
            if (r16 < 0) goto L_0x004d
            java.lang.Object r0 = r10.get(r15)
            android.content.pm.ApplicationInfo r0 = (android.content.pm.ApplicationInfo) r0
            r5 = r0
            r17 = r2
            goto L_0x008d
        L_0x004d:
            r3 = 0
            android.content.pm.IPackageManager r0 = r1.mPackageManager     // Catch:{ RemoteException -> 0x005c }
            android.content.pm.PackageInfo r0 = r0.getPackageInfo(r8, r7, r15)     // Catch:{ RemoteException -> 0x005c }
            if (r0 != 0) goto L_0x0058
            r4 = 0
            goto L_0x005a
        L_0x0058:
            android.content.pm.ApplicationInfo r4 = r0.applicationInfo     // Catch:{ RemoteException -> 0x005c }
        L_0x005a:
            r0 = r4
            goto L_0x005e
        L_0x005c:
            r0 = move-exception
            r0 = r3
        L_0x005e:
            r10.put(r15, r0)
            if (r0 != 0) goto L_0x008a
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Could not find package "
            r3.append(r4)
            r3.append(r8)
            java.lang.String r4 = " for user "
            r3.append(r4)
            r3.append(r15)
            java.lang.String r3 = r3.toString()
            android.util.Slog.d(r6, r3)
            com.android.server.pm.dex.PackageDynamicCodeLoading r3 = r1.mPackageDynamicCodeLoading
            boolean r3 = r3.removeUserPackage(r8, r15)
            r2 = r2 | r3
            r5 = r0
            r17 = r2
            goto L_0x008d
        L_0x008a:
            r5 = r0
            r17 = r2
        L_0x008d:
            if (r5 != 0) goto L_0x0092
            r2 = r17
            goto L_0x001d
        L_0x0092:
            java.lang.String r0 = r5.credentialProtectedDataDir
            boolean r0 = r1.fileIsUnder(r13, r0)
            if (r0 == 0) goto L_0x009e
            r0 = 2
            r18 = r0
            goto L_0x00a9
        L_0x009e:
            java.lang.String r0 = r5.deviceProtectedDataDir
            boolean r0 = r1.fileIsUnder(r13, r0)
            if (r0 == 0) goto L_0x018d
            r0 = 1
            r18 = r0
        L_0x00a9:
            r19 = 0
            com.android.server.pm.Installer r2 = r1.mInstaller     // Catch:{ InstallerException -> 0x00cf }
            int r0 = r5.uid     // Catch:{ InstallerException -> 0x00cf }
            java.lang.String r4 = r5.volumeUuid     // Catch:{ InstallerException -> 0x00cf }
            r3 = r13
            r20 = r4
            r4 = r24
            r21 = r9
            r9 = r5
            r5 = r0
            r22 = r10
            r10 = r6
            r6 = r20
            r20 = r11
            r11 = r7
            r7 = r18
            byte[] r0 = r2.hashSecondaryDexFile(r3, r4, r5, r6, r7)     // Catch:{ InstallerException -> 0x00cd }
            r19 = r0
            r2 = r19
            goto L_0x00fb
        L_0x00cd:
            r0 = move-exception
            goto L_0x00d9
        L_0x00cf:
            r0 = move-exception
            r21 = r9
            r22 = r10
            r20 = r11
            r9 = r5
            r10 = r6
            r11 = r7
        L_0x00d9:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Got InstallerException when hashing file "
            r2.append(r3)
            r2.append(r13)
            java.lang.String r3 = ": "
            r2.append(r3)
            java.lang.String r3 = r0.getMessage()
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            android.util.Slog.e(r10, r2)
            r2 = r19
        L_0x00fb:
            char r0 = r14.mFileType
            r3 = 68
            if (r0 != r3) goto L_0x0104
            java.lang.String r0 = "dcl"
            goto L_0x0106
        L_0x0104:
            java.lang.String r0 = "dcln"
        L_0x0106:
            r3 = r0
            java.io.File r0 = new java.io.File
            r0.<init>(r13)
            java.lang.String r4 = r0.getName()
            byte[] r0 = r4.getBytes()
            java.lang.String r0 = android.util.PackageUtils.computeSha256Digest(r0)
            if (r2 == 0) goto L_0x0137
            int r5 = r2.length
            r6 = 32
            if (r5 != r6) goto L_0x0137
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r0)
            r5.append(r6)
            java.lang.String r6 = android.util.ByteStringUtils.toHexString(r2)
            r5.append(r6)
            java.lang.String r0 = r5.toString()
            r5 = r0
            goto L_0x0156
        L_0x0137:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "Got no hash for "
            r5.append(r6)
            r5.append(r13)
            java.lang.String r5 = r5.toString()
            android.util.Slog.d(r10, r5)
            com.android.server.pm.dex.PackageDynamicCodeLoading r5 = r1.mPackageDynamicCodeLoading
            boolean r5 = r5.removeFile(r8, r13, r15)
            r5 = r17 | r5
            r17 = r5
            r5 = r0
        L_0x0156:
            java.util.Set<java.lang.String> r0 = r14.mLoadingPackages
            java.util.Iterator r6 = r0.iterator()
        L_0x015c:
            boolean r0 = r6.hasNext()
            if (r0 == 0) goto L_0x0183
            java.lang.Object r0 = r6.next()
            r7 = r0
            java.lang.String r7 = (java.lang.String) r7
            r10 = -1
            boolean r0 = r7.equals(r8)
            if (r0 == 0) goto L_0x0173
            int r10 = r9.uid
            goto L_0x017c
        L_0x0173:
            android.content.pm.IPackageManager r0 = r1.mPackageManager     // Catch:{ RemoteException -> 0x017b }
            int r0 = r0.getPackageUid(r7, r11, r15)     // Catch:{ RemoteException -> 0x017b }
            r10 = r0
            goto L_0x017c
        L_0x017b:
            r0 = move-exception
        L_0x017c:
            r0 = -1
            if (r10 == r0) goto L_0x0182
            r1.writeDclEvent(r3, r10, r5)
        L_0x0182:
            goto L_0x015c
        L_0x0183:
            r2 = r17
            r11 = r20
            r9 = r21
            r10 = r22
            goto L_0x001d
        L_0x018d:
            r21 = r9
            r22 = r10
            r20 = r11
            r9 = r5
            r10 = r6
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "Could not infer CE/DE storage for path "
            r0.append(r2)
            r0.append(r13)
            java.lang.String r0 = r0.toString()
            android.util.Slog.e(r10, r0)
            com.android.server.pm.dex.PackageDynamicCodeLoading r0 = r1.mPackageDynamicCodeLoading
            boolean r0 = r0.removeFile(r8, r13, r15)
            r2 = r17 | r0
            r9 = r21
            r10 = r22
            goto L_0x001d
        L_0x01b7:
            r21 = r9
            r22 = r10
            if (r2 == 0) goto L_0x01c2
            com.android.server.pm.dex.PackageDynamicCodeLoading r0 = r1.mPackageDynamicCodeLoading
            r0.maybeWriteAsync()
        L_0x01c2:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.dex.DynamicCodeLogger.logDynamicCodeLoading(java.lang.String):void");
    }

    private boolean fileIsUnder(String filePath, String directoryPath) {
        if (directoryPath == null) {
            return false;
        }
        try {
            return FileUtils.contains(new File(directoryPath).getCanonicalPath(), new File(filePath).getCanonicalPath());
        } catch (IOException e) {
            return false;
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public PackageDynamicCodeLoading.PackageDynamicCode getPackageDynamicCodeInfo(String packageName) {
        return this.mPackageDynamicCodeLoading.getPackageDynamicCodeInfo(packageName);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void writeDclEvent(String subtag, int uid, String message) {
        EventLog.writeEvent(SNET_TAG, new Object[]{subtag, Integer.valueOf(uid), message});
    }

    /* access modifiers changed from: package-private */
    public void recordDex(int loaderUserId, String dexPath, String owningPackageName, String loadingPackageName) {
        if (this.mPackageDynamicCodeLoading.record(owningPackageName, dexPath, 68, loaderUserId, loadingPackageName)) {
            this.mPackageDynamicCodeLoading.maybeWriteAsync();
        }
    }

    public void recordNative(int loadingUid, String path) {
        try {
            String[] packages = this.mPackageManager.getPackagesForUid(loadingUid);
            if (packages != null && packages.length != 0) {
                String loadingPackageName = packages[0];
                if (this.mPackageDynamicCodeLoading.record(loadingPackageName, path, 78, UserHandle.getUserId(loadingUid), loadingPackageName)) {
                    this.mPackageDynamicCodeLoading.maybeWriteAsync();
                }
            }
        } catch (RemoteException e) {
        }
    }

    /* access modifiers changed from: package-private */
    public void clear() {
        this.mPackageDynamicCodeLoading.clear();
    }

    /* access modifiers changed from: package-private */
    public void removePackage(String packageName) {
        if (this.mPackageDynamicCodeLoading.removePackage(packageName)) {
            this.mPackageDynamicCodeLoading.maybeWriteAsync();
        }
    }

    /* access modifiers changed from: package-private */
    public void removeUserPackage(String packageName, int userId) {
        if (this.mPackageDynamicCodeLoading.removeUserPackage(packageName, userId)) {
            this.mPackageDynamicCodeLoading.maybeWriteAsync();
        }
    }

    /* access modifiers changed from: package-private */
    public void readAndSync(Map<String, Set<Integer>> packageToUsersMap) {
        this.mPackageDynamicCodeLoading.read();
        this.mPackageDynamicCodeLoading.syncData(packageToUsersMap);
    }

    /* access modifiers changed from: package-private */
    public void writeNow() {
        this.mPackageDynamicCodeLoading.writeNow();
    }
}
