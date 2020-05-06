package com.miui.networkassistant.firewall;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.j.B;
import com.miui.analytics.AnalyticsUtil;
import com.miui.net.MiuiNetworkPolicyManager;
import com.miui.networkassistant.firewall.UserConfigure;
import com.miui.networkassistant.utils.DeviceUtil;

public class BackgroundPolicyService {
    private static final String TAG = "BackgroundPolicyService";
    private static BackgroundPolicyService sInstance;
    private Context mContext;
    private MiuiNetworkPolicyManager mPolicyService;

    private BackgroundPolicyService(Context context) {
        this.mContext = context;
        this.mPolicyService = new MiuiNetworkPolicyManager(context);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0039, code lost:
        if (r0 != null) goto L_0x0048;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0046, code lost:
        if (r0 != null) goto L_0x0048;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0048, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x004b, code lost:
        return null;
     */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x004f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String getAppRestrictBackground(java.lang.String r8) {
        /*
            r7 = this;
            java.lang.String r3 = "pkgName = ? AND userId = ?"
            r0 = 2
            java.lang.String[] r4 = new java.lang.String[r0]
            r0 = 0
            r4[r0] = r8
            int r8 = b.b.c.j.B.j()
            java.lang.String r8 = java.lang.Integer.toString(r8)
            r0 = 1
            r4[r0] = r8
            r8 = 0
            android.content.Context r0 = r7.mContext     // Catch:{ Exception -> 0x0041, all -> 0x003c }
            android.content.ContentResolver r0 = r0.getContentResolver()     // Catch:{ Exception -> 0x0041, all -> 0x003c }
            android.net.Uri r1 = com.miui.networkassistant.firewall.UserConfigure.CONTENT_URI     // Catch:{ Exception -> 0x0041, all -> 0x003c }
            r2 = 0
            r5 = 0
            android.database.Cursor r0 = r0.query(r1, r2, r3, r4, r5)     // Catch:{ Exception -> 0x0041, all -> 0x003c }
            if (r0 == 0) goto L_0x0039
            r0.moveToFirst()     // Catch:{ Exception -> 0x0037 }
            java.lang.String r1 = "bgControl"
            int r1 = r0.getColumnIndex(r1)     // Catch:{ Exception -> 0x0037 }
            java.lang.String r8 = r0.getString(r1)     // Catch:{ Exception -> 0x0037 }
            if (r0 == 0) goto L_0x0036
            r0.close()
        L_0x0036:
            return r8
        L_0x0037:
            r1 = move-exception
            goto L_0x0043
        L_0x0039:
            if (r0 == 0) goto L_0x004b
            goto L_0x0048
        L_0x003c:
            r0 = move-exception
            r6 = r0
            r0 = r8
            r8 = r6
            goto L_0x004d
        L_0x0041:
            r1 = move-exception
            r0 = r8
        L_0x0043:
            r1.printStackTrace()     // Catch:{ all -> 0x004c }
            if (r0 == 0) goto L_0x004b
        L_0x0048:
            r0.close()
        L_0x004b:
            return r8
        L_0x004c:
            r8 = move-exception
        L_0x004d:
            if (r0 == 0) goto L_0x0052
            r0.close()
        L_0x0052:
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.firewall.BackgroundPolicyService.getAppRestrictBackground(java.lang.String):java.lang.String");
    }

    public static synchronized BackgroundPolicyService getInstance(Context context) {
        BackgroundPolicyService backgroundPolicyService;
        synchronized (BackgroundPolicyService.class) {
            if (sInstance == null) {
                sInstance = new BackgroundPolicyService(context);
            }
            backgroundPolicyService = sInstance;
        }
        return backgroundPolicyService;
    }

    private void setAppRestrictBackground(String str, String str2) {
        try {
            Bundle bundle = new Bundle();
            bundle.putInt(UserConfigure.Columns.USER_ID, B.j());
            bundle.putString("pkgName", str);
            bundle.putString(UserConfigure.Columns.BG_CONTROL, str2);
            this.mContext.getContentResolver().call(UserConfigure.CONTENT_URI, UserConfigure.METHOD_UPDATE, (String) null, bundle);
        } catch (IllegalArgumentException e) {
            Log.i(TAG, "setAppRestrictBackground IllegalArgumentException", e);
        } catch (Exception e2) {
            Log.i(TAG, "setAppRestrictBackground exception", e2);
            AnalyticsUtil.trackException(e2);
        }
    }

    public boolean isAppRestrictBackground(String str, int i) {
        if (DeviceUtil.IS_M_OR_LATER) {
            return this.mPolicyService.isAppRestrictBackground(i);
        }
        String appRestrictBackground = getAppRestrictBackground(str);
        if (!TextUtils.isEmpty(appRestrictBackground)) {
            return TextUtils.equals(appRestrictBackground, UserConfigure.BG_CONTROL_RESTRICT_BG) || TextUtils.equals(appRestrictBackground, UserConfigure.BG_CONTROL_NO_BG);
        }
        return false;
    }

    public boolean isRestrictBackground() {
        return this.mPolicyService.getRestrictBackground();
    }

    public void setAppRestrictBackground(int i, boolean z) {
        if (DeviceUtil.IS_M_OR_LATER) {
            this.mPolicyService.setAppRestrictBackground(i, z);
            return;
        }
        String str = z ? UserConfigure.BG_CONTROL_RESTRICT_BG : "miuiAuto";
        String[] packagesForUid = this.mContext.getPackageManager().getPackagesForUid(i);
        if (packagesForUid != null && packagesForUid.length > 0) {
            for (String appRestrictBackground : packagesForUid) {
                setAppRestrictBackground(appRestrictBackground, str);
            }
        }
    }

    public void setRestrictBackground(boolean z) {
        this.mPolicyService.setRestrictBackground(z);
    }
}
