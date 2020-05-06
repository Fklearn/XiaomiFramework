package com.android.server.policy;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.UserHandle;

public abstract class SoftRestrictedPermissionPolicy {
    private static final SoftRestrictedPermissionPolicy DUMMY_POLICY = new SoftRestrictedPermissionPolicy() {
        public int resolveAppOp() {
            return -1;
        }

        public int getDesiredOpMode() {
            return 3;
        }

        public boolean shouldSetAppOpIfNotDefault() {
            return false;
        }

        public boolean canBeGranted() {
            return true;
        }
    };
    private static final int FLAGS_PERMISSION_RESTRICTION_ANY_EXEMPT = 14336;

    public abstract boolean canBeGranted();

    public abstract int getDesiredOpMode();

    public abstract int resolveAppOp();

    public abstract boolean shouldSetAppOpIfNotDefault();

    private static int getMinimumTargetSDK(Context context, ApplicationInfo appInfo, UserHandle user) {
        PackageManager pm = context.getPackageManager();
        int minimumTargetSDK = appInfo.targetSdkVersion;
        String[] uidPkgs = pm.getPackagesForUid(appInfo.uid);
        if (uidPkgs == null) {
            return minimumTargetSDK;
        }
        int minimumTargetSDK2 = minimumTargetSDK;
        for (String uidPkg : uidPkgs) {
            if (!uidPkg.equals(appInfo.packageName)) {
                try {
                    minimumTargetSDK2 = Integer.min(minimumTargetSDK2, pm.getApplicationInfoAsUser(uidPkg, 0, user).targetSdkVersion);
                } catch (PackageManager.NameNotFoundException e) {
                }
            }
        }
        return minimumTargetSDK2;
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x002e  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0052  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static com.android.server.policy.SoftRestrictedPermissionPolicy forPermission(android.content.Context r16, android.content.pm.ApplicationInfo r17, android.os.UserHandle r18, java.lang.String r19) {
        /*
            r1 = r17
            r2 = r18
            r3 = r19
            int r0 = r19.hashCode()
            r4 = -406040016(0xffffffffe7cc5230, float:-1.9297577E24)
            r5 = 0
            r6 = 1
            if (r0 == r4) goto L_0x0021
            r4 = 1365911975(0x516a29a7, float:6.2857572E10)
            if (r0 == r4) goto L_0x0017
        L_0x0016:
            goto L_0x002b
        L_0x0017:
            java.lang.String r0 = "android.permission.WRITE_EXTERNAL_STORAGE"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0016
            r0 = r6
            goto L_0x002c
        L_0x0021:
            java.lang.String r0 = "android.permission.READ_EXTERNAL_STORAGE"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0016
            r0 = r5
            goto L_0x002c
        L_0x002b:
            r0 = -1
        L_0x002c:
            if (r0 == 0) goto L_0x0052
            if (r0 == r6) goto L_0x0033
            com.android.server.policy.SoftRestrictedPermissionPolicy r0 = DUMMY_POLICY
            return r0
        L_0x0033:
            if (r1 == 0) goto L_0x004a
            android.content.pm.PackageManager r0 = r16.getPackageManager()
            java.lang.String r4 = r1.packageName
            int r0 = r0.getPermissionFlags(r3, r4, r2)
            r4 = r0 & 14336(0x3800, float:2.0089E-41)
            if (r4 == 0) goto L_0x0044
            r5 = r6
        L_0x0044:
            r4 = r5
            int r0 = getMinimumTargetSDK(r16, r17, r18)
            goto L_0x004c
        L_0x004a:
            r4 = 0
            r0 = 0
        L_0x004c:
            com.android.server.policy.SoftRestrictedPermissionPolicy$3 r5 = new com.android.server.policy.SoftRestrictedPermissionPolicy$3
            r5.<init>(r4, r0)
            return r5
        L_0x0052:
            if (r1 == 0) goto L_0x00a1
            android.content.pm.PackageManager r4 = r16.getPackageManager()
            java.lang.String r0 = r1.packageName
            int r7 = r4.getPermissionFlags(r3, r0, r2)
            r0 = r7 & 16384(0x4000, float:2.2959E-41)
            if (r0 == 0) goto L_0x0064
            r0 = r6
            goto L_0x0065
        L_0x0064:
            r0 = r5
        L_0x0065:
            r8 = r0
            r0 = r7 & 14336(0x3800, float:2.0089E-41)
            if (r0 == 0) goto L_0x006b
            goto L_0x006c
        L_0x006b:
            r6 = r5
        L_0x006c:
            int r9 = getMinimumTargetSDK(r16, r17, r18)
            boolean r0 = r17.hasRequestedLegacyExternalStorage()
            int r10 = r1.uid
            java.lang.String[] r10 = r4.getPackagesForUid(r10)
            if (r10 == 0) goto L_0x009e
            int r11 = r10.length
            r13 = r0
            r12 = r5
        L_0x0080:
            if (r12 >= r11) goto L_0x009f
            r14 = r10[r12]
            java.lang.String r0 = r1.packageName
            boolean r0 = r14.equals(r0)
            if (r0 != 0) goto L_0x009b
            android.content.pm.ApplicationInfo r0 = r4.getApplicationInfoAsUser(r14, r5, r2)     // Catch:{ NameNotFoundException -> 0x0098 }
            boolean r15 = r0.hasRequestedLegacyExternalStorage()
            r13 = r13 | r15
            goto L_0x009b
        L_0x0098:
            r0 = move-exception
            r15 = r0
            r0 = r15
        L_0x009b:
            int r12 = r12 + 1
            goto L_0x0080
        L_0x009e:
            r13 = r0
        L_0x009f:
            r0 = r13
            goto L_0x00a6
        L_0x00a1:
            r7 = 0
            r8 = 0
            r6 = 0
            r0 = 0
            r9 = 0
        L_0x00a6:
            com.android.server.policy.SoftRestrictedPermissionPolicy$2 r4 = new com.android.server.policy.SoftRestrictedPermissionPolicy$2
            r4.<init>(r8, r0, r6, r9)
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.policy.SoftRestrictedPermissionPolicy.forPermission(android.content.Context, android.content.pm.ApplicationInfo, android.os.UserHandle, java.lang.String):com.android.server.policy.SoftRestrictedPermissionPolicy");
    }
}
