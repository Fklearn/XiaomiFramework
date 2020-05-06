package com.miui.wakepath.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.UserHandle;
import android.view.View;
import b.b.c.j.B;
import b.b.c.j.g;
import b.b.o.g.e;
import com.miui.permcenter.a.a;
import com.miui.permcenter.privacymanager.behaviorrecord.o;
import com.miui.permission.PermissionContract;
import com.miui.securitycenter.R;
import miui.app.AlertActivity;
import miui.util.Log;

public class ConfirmStartActivity extends AlertActivity implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private static final String f8234a = "ConfirmStartActivity";

    /* renamed from: b  reason: collision with root package name */
    private Intent f8235b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public String f8236c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public String f8237d;
    private int e;
    private int f;

    private void a(Context context) {
        new a(this, context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    private void a(View view) {
        try {
            Object a2 = e.a((Object) this, (Class<?>) AlertActivity.class, "mAlertParams");
            Class<? super Object> superclass = a2.getClass().getSuperclass();
            e.a(a2, (Class<?>) superclass, "mView", (Object) view);
            e.a(a2, (Class<?>) superclass, "mNegativeButtonText", (Object) getString(R.string.button_text_deny));
            e.a(a2, (Class<?>) superclass, "mPositiveButtonText", (Object) getString(R.string.button_text_accept));
            e.a(a2, (Class<?>) superclass, "mPositiveButtonListener", (Object) this);
            e.a(a2, (Class<?>) superclass, "mNegativeButtonListener", (Object) this);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [android.content.Context, miui.app.AlertActivity, com.miui.wakepath.ui.ConfirmStartActivity] */
    private void a(boolean z) {
        if (o.b((Context) this)) {
            Bundle bundle = new Bundle();
            bundle.putString("pkgName", this.f8236c);
            bundle.putString("calleePkg", this.f8237d);
            bundle.putInt("type", 1);
            bundle.putBoolean("mode", z);
            bundle.putInt("user", this.e);
            bundle.putInt(PermissionContract.Method.SendPermissionRecord.EXTRA_CALLER_UID, this.f);
            getContentResolver().call(PermissionContract.CONTENT_URI, String.valueOf(14), (String) null, bundle);
        }
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [miui.app.AlertActivity, android.content.Context, com.miui.wakepath.ui.ConfirmStartActivity] */
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -2) {
            a(false);
            a.b(this.f8236c, this.f8237d);
        } else if (i == -1) {
            Intent intent = this.f8235b;
            if (intent != null) {
                int i2 = this.e;
                if (i2 != -1) {
                    UserHandle e2 = B.e(i2);
                    try {
                        com.miui.applicationlock.c.o.a();
                        if (this.f8235b.getClipData() != null) {
                            this.f8235b.addFlags(Integer.MIN_VALUE);
                        }
                        g.b((Context) this, this.f8235b, e2);
                    } finally {
                        com.miui.applicationlock.c.o.b();
                    }
                } else {
                    startActivity(intent);
                }
            } else {
                Log.w(f8234a, "intent == null");
            }
            a(getApplicationContext());
            a(true);
            a.a(this.f8236c, this.f8237d);
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x009f  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00b0  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onCreate(android.os.Bundle r6) {
        /*
            r5 = this;
            java.lang.String r0 = f8234a
            java.lang.String r1 = "onCreate"
            miui.util.Log.i(r0, r1)
            com.miui.wakepath.ui.ConfirmStartActivity.super.onCreate(r6)
            r6 = 0
            android.content.Intent r0 = r5.getIntent()     // Catch:{ Exception -> 0x0069 }
            if (r0 == 0) goto L_0x0066
            android.os.Bundle r1 = r0.getExtras()     // Catch:{ Exception -> 0x0069 }
            if (r1 == 0) goto L_0x0057
            java.lang.String r2 = "CallerPkgName"
            java.lang.String r2 = r1.getString(r2)     // Catch:{ Exception -> 0x0069 }
            r5.f8236c = r2     // Catch:{ Exception -> 0x0069 }
            java.lang.String r2 = "CalleePkgName"
            java.lang.String r2 = r1.getString(r2)     // Catch:{ Exception -> 0x0069 }
            r5.f8237d = r2     // Catch:{ Exception -> 0x0069 }
            java.lang.String r2 = "UserId"
            int r2 = r1.getInt(r2)     // Catch:{ Exception -> 0x0069 }
            r5.e = r2     // Catch:{ Exception -> 0x0069 }
            java.lang.String r2 = "callerUserId"
            int r1 = r1.getInt(r2)     // Catch:{ Exception -> 0x0069 }
            r5.f = r1     // Catch:{ Exception -> 0x0069 }
            android.content.Context r1 = r5.getApplicationContext()     // Catch:{ Exception -> 0x0069 }
            java.lang.String r2 = r5.f8236c     // Catch:{ Exception -> 0x0069 }
            java.lang.CharSequence r1 = b.b.c.j.x.j(r1, r2)     // Catch:{ Exception -> 0x0069 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0069 }
            android.content.Context r2 = r5.getApplicationContext()     // Catch:{ Exception -> 0x0054 }
            java.lang.String r3 = r5.f8237d     // Catch:{ Exception -> 0x0054 }
            java.lang.CharSequence r2 = b.b.c.j.x.j(r2, r3)     // Catch:{ Exception -> 0x0054 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0054 }
            goto L_0x0059
        L_0x0054:
            r0 = move-exception
            r2 = r6
            goto L_0x006c
        L_0x0057:
            r1 = r6
            r2 = r1
        L_0x0059:
            java.lang.String r3 = "android.intent.extra.INTENT"
            android.os.Parcelable r0 = r0.getParcelableExtra(r3)     // Catch:{ Exception -> 0x0064 }
            android.content.Intent r0 = (android.content.Intent) r0     // Catch:{ Exception -> 0x0064 }
            r5.f8235b = r0     // Catch:{ Exception -> 0x0064 }
            goto L_0x006f
        L_0x0064:
            r0 = move-exception
            goto L_0x006c
        L_0x0066:
            r1 = r6
            r2 = r1
            goto L_0x006f
        L_0x0069:
            r0 = move-exception
            r1 = r6
            r2 = r1
        L_0x006c:
            r0.printStackTrace()
        L_0x006f:
            java.lang.String r0 = r5.f8236c
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x007f
            java.lang.String r0 = r5.f8237d
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x0082
        L_0x007f:
            r5.finish()
        L_0x0082:
            java.lang.String r0 = "layout_inflater"
            java.lang.Object r0 = r5.getSystemService(r0)
            android.view.LayoutInflater r0 = (android.view.LayoutInflater) r0
            r3 = 2131493438(0x7f0c023e, float:1.8610356E38)
            android.view.View r6 = r0.inflate(r3, r6)
            r5.a((android.view.View) r6)
            r0 = 2131296975(0x7f0902cf, float:1.8211882E38)
            android.view.View r0 = r6.findViewById(r0)
            android.widget.ImageView r0 = (android.widget.ImageView) r0
            if (r0 == 0) goto L_0x00a5
            r3 = 2131231656(0x7f0803a8, float:1.80794E38)
            r0.setImageResource(r3)
        L_0x00a5:
            r0 = 2131296760(0x7f0901f8, float:1.8211446E38)
            android.view.View r6 = r6.findViewById(r0)
            android.widget.TextView r6 = (android.widget.TextView) r6
            if (r6 == 0) goto L_0x00cb
            android.content.res.Resources r0 = r5.getResources()
            r3 = 2131758559(0x7f100ddf, float:1.9148085E38)
            java.lang.String r0 = r0.getString(r3)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r4 = 0
            r3[r4] = r1
            r1 = 1
            r3[r1] = r2
            java.lang.String r0 = java.lang.String.format(r0, r3)
            r6.setText(r0)
        L_0x00cb:
            r5.setupAlert()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.wakepath.ui.ConfirmStartActivity.onCreate(android.os.Bundle):void");
    }
}
