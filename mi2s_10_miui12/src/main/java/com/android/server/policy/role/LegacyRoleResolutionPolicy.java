package com.android.server.policy.role;

import android.content.Context;
import com.android.server.role.RoleManagerService;

public class LegacyRoleResolutionPolicy implements RoleManagerService.RoleHoldersResolver {
    private static final boolean DEBUG = false;
    private static final String LOG_TAG = "LegacyRoleResolutionPol";
    private final Context mContext;

    public LegacyRoleResolutionPolicy(Context context) {
        this.mContext = context;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<java.lang.String> getRoleHolders(java.lang.String r8, int r9) {
        /*
            r7 = this;
            int r0 = r8.hashCode()
            r1 = 5
            r2 = 4
            r3 = 3
            r4 = 2
            r5 = 1
            switch(r0) {
                case 443215373: goto L_0x003f;
                case 666116809: goto L_0x0035;
                case 854448779: goto L_0x002b;
                case 1634943122: goto L_0x0021;
                case 1834128197: goto L_0x0017;
                case 1965677020: goto L_0x000d;
                default: goto L_0x000c;
            }
        L_0x000c:
            goto L_0x0049
        L_0x000d:
            java.lang.String r0 = "android.app.role.BROWSER"
            boolean r0 = r8.equals(r0)
            if (r0 == 0) goto L_0x000c
            r0 = r5
            goto L_0x004a
        L_0x0017:
            java.lang.String r0 = "android.app.role.EMERGENCY"
            boolean r0 = r8.equals(r0)
            if (r0 == 0) goto L_0x000c
            r0 = r1
            goto L_0x004a
        L_0x0021:
            java.lang.String r0 = "android.app.role.ASSISTANT"
            boolean r0 = r8.equals(r0)
            if (r0 == 0) goto L_0x000c
            r0 = 0
            goto L_0x004a
        L_0x002b:
            java.lang.String r0 = "android.app.role.HOME"
            boolean r0 = r8.equals(r0)
            if (r0 == 0) goto L_0x000c
            r0 = r2
            goto L_0x004a
        L_0x0035:
            java.lang.String r0 = "android.app.role.DIALER"
            boolean r0 = r8.equals(r0)
            if (r0 == 0) goto L_0x000c
            r0 = r4
            goto L_0x004a
        L_0x003f:
            java.lang.String r0 = "android.app.role.SMS"
            boolean r0 = r8.equals(r0)
            if (r0 == 0) goto L_0x000c
            r0 = r3
            goto L_0x004a
        L_0x0049:
            r0 = -1
        L_0x004a:
            r6 = 0
            if (r0 == 0) goto L_0x0110
            if (r0 == r5) goto L_0x00ff
            if (r0 == r4) goto L_0x00cf
            if (r0 == r3) goto L_0x009e
            if (r0 == r2) goto L_0x0083
            if (r0 == r1) goto L_0x0072
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "Don't know how to find legacy role holders for "
            r0.append(r1)
            r0.append(r8)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "LegacyRoleResolutionPol"
            android.util.Slog.e(r1, r0)
            java.util.List r0 = java.util.Collections.emptyList()
            return r0
        L_0x0072:
            android.content.Context r0 = r7.mContext
            android.content.ContentResolver r0 = r0.getContentResolver()
            java.lang.String r1 = "emergency_assistance_application"
            java.lang.String r0 = android.provider.Settings.Secure.getStringForUser(r0, r1, r9)
            java.util.List r1 = com.android.internal.util.CollectionUtils.singletonOrEmpty(r0)
            return r1
        L_0x0083:
            android.content.Context r0 = r7.mContext
            android.content.pm.PackageManager r0 = r0.getPackageManager()
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            android.content.ComponentName r2 = r0.getHomeActivities(r1)
            if (r2 == 0) goto L_0x0098
            java.lang.String r6 = r2.getPackageName()
        L_0x0098:
            r3 = r6
            java.util.List r4 = com.android.internal.util.CollectionUtils.singletonOrEmpty(r3)
            return r4
        L_0x009e:
            android.content.Context r0 = r7.mContext
            android.content.ContentResolver r0 = r0.getContentResolver()
            java.lang.String r1 = "sms_default_application"
            java.lang.String r0 = android.provider.Settings.Secure.getStringForUser(r0, r1, r9)
            boolean r1 = android.text.TextUtils.isEmpty(r0)
            if (r1 != 0) goto L_0x00b3
            r1 = r0
            goto L_0x00ca
        L_0x00b3:
            android.content.Context r1 = r7.mContext
            android.content.pm.PackageManager r1 = r1.getPackageManager()
            boolean r1 = r1.isDeviceUpgrading()
            if (r1 == 0) goto L_0x00c9
            android.content.Context r1 = r7.mContext
            r2 = 17039396(0x1040024, float:2.4244672E-38)
            java.lang.String r1 = r1.getString(r2)
            goto L_0x00ca
        L_0x00c9:
            r1 = 0
        L_0x00ca:
            java.util.List r2 = com.android.internal.util.CollectionUtils.singletonOrEmpty(r1)
            return r2
        L_0x00cf:
            android.content.Context r0 = r7.mContext
            android.content.ContentResolver r0 = r0.getContentResolver()
            java.lang.String r1 = "dialer_default_application"
            java.lang.String r0 = android.provider.Settings.Secure.getStringForUser(r0, r1, r9)
            boolean r1 = android.text.TextUtils.isEmpty(r0)
            if (r1 != 0) goto L_0x00e3
            r1 = r0
            goto L_0x00fa
        L_0x00e3:
            android.content.Context r1 = r7.mContext
            android.content.pm.PackageManager r1 = r1.getPackageManager()
            boolean r1 = r1.isDeviceUpgrading()
            if (r1 == 0) goto L_0x00f9
            android.content.Context r1 = r7.mContext
            r2 = 17039395(0x1040023, float:2.424467E-38)
            java.lang.String r1 = r1.getString(r2)
            goto L_0x00fa
        L_0x00f9:
            r1 = 0
        L_0x00fa:
            java.util.List r2 = com.android.internal.util.CollectionUtils.singletonOrEmpty(r1)
            return r2
        L_0x00ff:
            java.lang.Class<android.content.pm.PackageManagerInternal> r0 = android.content.pm.PackageManagerInternal.class
            java.lang.Object r0 = com.android.server.LocalServices.getService(r0)
            android.content.pm.PackageManagerInternal r0 = (android.content.pm.PackageManagerInternal) r0
            java.lang.String r1 = r0.removeLegacyDefaultBrowserPackageName(r9)
            java.util.List r2 = com.android.internal.util.CollectionUtils.singletonOrEmpty(r1)
            return r2
        L_0x0110:
            android.content.Context r0 = r7.mContext
            android.content.ContentResolver r0 = r0.getContentResolver()
            java.lang.String r1 = "assistant"
            java.lang.String r0 = android.provider.Settings.Secure.getStringForUser(r0, r1, r9)
            if (r0 == 0) goto L_0x0132
            boolean r1 = r0.isEmpty()
            if (r1 != 0) goto L_0x0130
            android.content.ComponentName r1 = android.content.ComponentName.unflattenFromString(r0)
            if (r1 == 0) goto L_0x012e
            java.lang.String r6 = r1.getPackageName()
        L_0x012e:
            r1 = r6
            goto L_0x0151
        L_0x0130:
            r1 = 0
            goto L_0x0151
        L_0x0132:
            android.content.Context r1 = r7.mContext
            android.content.pm.PackageManager r1 = r1.getPackageManager()
            boolean r1 = r1.isDeviceUpgrading()
            if (r1 == 0) goto L_0x0150
            android.content.Context r1 = r7.mContext
            r2 = 17039393(0x1040021, float:2.4244663E-38)
            java.lang.String r1 = r1.getString(r2)
            boolean r2 = android.text.TextUtils.isEmpty(r1)
            if (r2 != 0) goto L_0x014e
            r6 = r1
        L_0x014e:
            r1 = r6
            goto L_0x0151
        L_0x0150:
            r1 = 0
        L_0x0151:
            java.util.List r2 = com.android.internal.util.CollectionUtils.singletonOrEmpty(r1)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.policy.role.LegacyRoleResolutionPolicy.getRoleHolders(java.lang.String, int):java.util.List");
    }
}
