package com.miui.applicationlock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.miui.applicationlock.a.h;
import com.miui.applicationlock.c.C0259c;
import com.miui.applicationlock.c.o;
import com.miui.maml.folme.AnimatedTarget;
import miui.security.SecurityManager;

public class TransitionHelper extends Activity {

    /* renamed from: a  reason: collision with root package name */
    private C0259c f3218a;

    /* renamed from: b  reason: collision with root package name */
    private String f3219b;

    /* renamed from: c  reason: collision with root package name */
    private String f3220c;

    /* renamed from: d  reason: collision with root package name */
    private String f3221d;
    private SecurityManager e;
    private boolean f;

    public static boolean a(Context context) {
        try {
            return b(context);
        } catch (Exception e2) {
            Log.e("TransitionHelper", "isScreenLockOpen error", e2);
            return false;
        }
    }

    private static boolean b(Context context) {
        Class<?> cls = Class.forName("android.provider.MiuiSettings$Secure");
        return ((Boolean) cls.getMethod("hasCommonPassword", new Class[]{Context.class}).invoke(cls, new Object[]{context})).booleanValue();
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x005f, code lost:
        startActivity(r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0062, code lost:
        finish();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0027, code lost:
        if (r9 != null) goto L_0x0029;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:7:0x0029, code lost:
        r9.putExtra("external_app_name", r6.f3221d);
        startActivity(r9);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onActivityResult(int r7, int r8, android.content.Intent r9) {
        /*
            r6 = this;
            super.onActivityResult(r7, r8, r9)
            r0 = 0
            java.lang.String r1 = "not_home_start"
            java.lang.String r2 = "extra_data"
            r3 = -1
            java.lang.String r4 = "external_app_name"
            r5 = 1
            switch(r7) {
                case 1022103: goto L_0x0066;
                case 1022119: goto L_0x0062;
                case 1022130: goto L_0x0032;
                case 1022132: goto L_0x0011;
                default: goto L_0x000f;
            }
        L_0x000f:
            goto L_0x009d
        L_0x0011:
            if (r8 != r3) goto L_0x0062
            java.lang.String r7 = r6.f3221d
            boolean r7 = android.text.TextUtils.isEmpty(r7)
            if (r7 != 0) goto L_0x0062
            miui.security.SecurityManager r7 = r6.e
            java.lang.String r8 = r6.f3221d
            r7.setApplicationAccessControlEnabled(r8, r5)
            com.miui.applicationlock.c.c r7 = r6.f3218a
            r7.e(r0)
            if (r9 == 0) goto L_0x0062
        L_0x0029:
            java.lang.String r7 = r6.f3221d
            r9.putExtra(r4, r7)
            r6.startActivity(r9)
            goto L_0x0062
        L_0x0032:
            if (r8 != r3) goto L_0x0062
            java.lang.String r7 = r6.f3221d
            boolean r7 = android.text.TextUtils.isEmpty(r7)
            if (r7 != 0) goto L_0x0062
            android.content.Intent r7 = new android.content.Intent
            java.lang.Class<com.miui.applicationlock.PrivacyAndAppLockManageActivity> r8 = com.miui.applicationlock.PrivacyAndAppLockManageActivity.class
            r7.<init>(r6, r8)
            r7.putExtra(r2, r1)
            java.lang.String r8 = r6.f3221d
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 != 0) goto L_0x005a
            java.lang.String r8 = r6.f3221d
            r7.putExtra(r4, r8)
            miui.security.SecurityManager r8 = r6.e
            java.lang.String r9 = r6.f3221d
            r8.setApplicationAccessControlEnabled(r9, r5)
        L_0x005a:
            com.miui.applicationlock.c.c r8 = r6.f3218a
            r8.e(r0)
        L_0x005f:
            r6.startActivity(r7)
        L_0x0062:
            r6.finish()
            goto L_0x009d
        L_0x0066:
            if (r8 != r3) goto L_0x0062
            boolean r7 = r6.f
            if (r7 != 0) goto L_0x007e
            java.lang.String r7 = r6.f3221d
            boolean r7 = android.text.TextUtils.isEmpty(r7)
            if (r7 != 0) goto L_0x0062
            if (r9 == 0) goto L_0x0062
            miui.security.SecurityManager r7 = r6.e
            java.lang.String r8 = r6.f3221d
            r7.setApplicationAccessControlEnabled(r8, r5)
            goto L_0x0029
        L_0x007e:
            android.content.Intent r7 = new android.content.Intent
            java.lang.Class<com.miui.applicationlock.PrivacyAndAppLockManageActivity> r8 = com.miui.applicationlock.PrivacyAndAppLockManageActivity.class
            r7.<init>(r6, r8)
            r7.putExtra(r2, r1)
            java.lang.String r8 = r6.f3221d
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 != 0) goto L_0x005f
            java.lang.String r8 = r6.f3221d
            r7.putExtra(r4, r8)
            miui.security.SecurityManager r8 = r6.e
            java.lang.String r9 = r6.f3221d
            r8.setApplicationAccessControlEnabled(r9, r5)
            goto L_0x005f
        L_0x009d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.applicationlock.TransitionHelper.onActivityResult(int, int, android.content.Intent):void");
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().addFlags(67108864);
        this.f3219b = getIntent().getStringExtra("enter_way");
        this.f3220c = getIntent().getStringExtra(AnimatedTarget.STATE_TAG_FROM);
        h.c(this.f3219b);
        this.f3218a = C0259c.b(getApplicationContext());
        this.e = (SecurityManager) getSystemService("security");
        this.f3221d = getIntent().getStringExtra("external_app_name");
        this.f = this.f3218a.d();
        try {
            if (!this.f) {
                o.a((Context) this);
                Intent intent = new Intent(this, FirstUseAppLockActivity.class);
                intent.putExtra("extra_enterway", this.f3219b);
                if (!TextUtils.isEmpty(this.f3220c)) {
                    intent.putExtra(AnimatedTarget.STATE_TAG_FROM, this.f3220c);
                }
                if (!TextUtils.isEmpty(this.f3221d)) {
                    intent.putExtra("external_app_name", this.f3221d);
                }
                startActivityForResult(intent, 1022119);
                return;
            }
            Intent intent2 = new Intent(this, ConfirmAccessControl.class);
            intent2.putExtra("extra_data", "HappyCodingMain");
            startActivityForResult(intent2, 1022103);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }
}
