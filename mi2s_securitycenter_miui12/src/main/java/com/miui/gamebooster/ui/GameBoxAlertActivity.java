package com.miui.gamebooster.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.widget.Button;
import com.miui.common.persistence.b;
import com.miui.gamebooster.m.C0384o;
import com.miui.gamebooster.m.D;
import com.miui.gamebooster.videobox.settings.f;
import com.miui.gamebooster.videobox.utils.c;
import miui.app.AlertActivity;

public class GameBoxAlertActivity extends AlertActivity implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private String f4900a;

    /* renamed from: b  reason: collision with root package name */
    private String f4901b;

    /* renamed from: c  reason: collision with root package name */
    private String f4902c;

    /* renamed from: d  reason: collision with root package name */
    private String f4903d;
    /* access modifiers changed from: private */
    public int e = 5;
    /* access modifiers changed from: private */
    public Handler f;
    private Object g;
    /* access modifiers changed from: private */
    public Button h;
    /* access modifiers changed from: private */
    public Runnable i = new C0436la(this);

    static /* synthetic */ int b(GameBoxAlertActivity gameBoxAlertActivity) {
        int i2 = gameBoxAlertActivity.e;
        gameBoxAlertActivity.e = i2 - 1;
        return i2;
    }

    /* JADX WARNING: type inference failed for: r8v0, types: [miui.app.AlertActivity, android.content.Context, com.miui.gamebooster.ui.GameBoxAlertActivity] */
    public void onClick(DialogInterface dialogInterface, int i2) {
        if (i2 != -2) {
            char c2 = 65535;
            if (i2 == -1) {
                String str = this.f4900a;
                switch (str.hashCode()) {
                    case -2026025149:
                        if (str.equals("intent_videobox_func_type_hangup")) {
                            c2 = 2;
                            break;
                        }
                        break;
                    case -1589811933:
                        if (str.equals("intent_gamebox_func_type_milink_hangup")) {
                            c2 = 4;
                            break;
                        }
                        break;
                    case -1424643528:
                        if (str.equals("intent_gamebox_func_type_immersion")) {
                            c2 = 0;
                            break;
                        }
                        break;
                    case -553785888:
                        if (str.equals("intent_videobox_func_type_milink_hangup")) {
                            c2 = 5;
                            break;
                        }
                        break;
                    case 23274848:
                        if (str.equals("intent_gamebox_func_type_hangup")) {
                            c2 = 1;
                            break;
                        }
                        break;
                    case 1994176142:
                        if (str.equals("intent_gamebox_func_type_immersion_back")) {
                            c2 = 3;
                            break;
                        }
                        break;
                }
                if (c2 == 0) {
                    b.b("key_gamebooster_immersion_ok", true);
                    D.a((Context) this, true);
                } else if (c2 == 1) {
                    b.b("key_gamebooster_hangup_ok", true);
                    D.e(getApplicationContext());
                } else if (c2 == 2) {
                    f.b(true);
                    c.c(getApplicationContext());
                } else if (c2 != 3) {
                    if (c2 == 4) {
                        b.b("key_gamebooster_milink_hangup_ok", true);
                    } else if (c2 == 5) {
                        b.b("key_videobox_milink_hangup_ok", true);
                        f.c(true);
                    } else {
                        return;
                    }
                    C0384o.b(getContentResolver(), (String) C0384o.b("android.provider.MiuiSettings$Secure", "SCREEN_PROJECT_HANG_UP"), 1, -2);
                } else {
                    D.a((Context) this, false);
                }
            }
        } else {
            cancel();
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x0194 A[Catch:{ Exception -> 0x01c8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:50:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onCreate(android.os.Bundle r10) {
        /*
            r9 = this;
            com.miui.gamebooster.ui.GameBoxAlertActivity.super.onCreate(r10)
            android.content.Intent r10 = r9.getIntent()
            java.lang.String r0 = "intent_gamebox_function_type"
            java.lang.String r10 = r10.getStringExtra(r0)
            r9.f4900a = r10
            android.content.Intent r10 = r9.getIntent()
            java.lang.String r0 = "intent_gamebox_booster_pkg"
            java.lang.String r10 = r10.getStringExtra(r0)
            r9.f4901b = r10
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r0 = "onCreate: alertType="
            r10.append(r0)
            java.lang.String r0 = r9.f4900a
            r10.append(r0)
            java.lang.String r10 = r10.toString()
            java.lang.String r0 = "GameBoxAlertActivity"
            android.util.Log.i(r0, r10)
            java.lang.String r10 = r9.f4900a
            int r0 = r10.hashCode()
            java.lang.String r1 = "intent_gamebox_func_type_immersion_back"
            r2 = 5
            r3 = 4
            r4 = 3
            r5 = 2
            r6 = -1
            r7 = 0
            r8 = 1
            switch(r0) {
                case -2026025149: goto L_0x0076;
                case -1589811933: goto L_0x006c;
                case -1424643528: goto L_0x0062;
                case -553785888: goto L_0x0058;
                case 23274848: goto L_0x004e;
                case 1994176142: goto L_0x0046;
                default: goto L_0x0045;
            }
        L_0x0045:
            goto L_0x0080
        L_0x0046:
            boolean r10 = r10.equals(r1)
            if (r10 == 0) goto L_0x0080
            r10 = r4
            goto L_0x0081
        L_0x004e:
            java.lang.String r0 = "intent_gamebox_func_type_hangup"
            boolean r10 = r10.equals(r0)
            if (r10 == 0) goto L_0x0080
            r10 = r8
            goto L_0x0081
        L_0x0058:
            java.lang.String r0 = "intent_videobox_func_type_milink_hangup"
            boolean r10 = r10.equals(r0)
            if (r10 == 0) goto L_0x0080
            r10 = r2
            goto L_0x0081
        L_0x0062:
            java.lang.String r0 = "intent_gamebox_func_type_immersion"
            boolean r10 = r10.equals(r0)
            if (r10 == 0) goto L_0x0080
            r10 = r7
            goto L_0x0081
        L_0x006c:
            java.lang.String r0 = "intent_gamebox_func_type_milink_hangup"
            boolean r10 = r10.equals(r0)
            if (r10 == 0) goto L_0x0080
            r10 = r3
            goto L_0x0081
        L_0x0076:
            java.lang.String r0 = "intent_videobox_func_type_hangup"
            boolean r10 = r10.equals(r0)
            if (r10 == 0) goto L_0x0080
            r10 = r5
            goto L_0x0081
        L_0x0080:
            r10 = r6
        L_0x0081:
            if (r10 == 0) goto L_0x00f2
            r0 = 2131756367(0x7f10054f, float:1.914364E38)
            if (r10 == r8) goto L_0x00e0
            if (r10 == r5) goto L_0x00ce
            if (r10 == r4) goto L_0x00b9
            if (r10 == r3) goto L_0x00a7
            if (r10 == r2) goto L_0x0092
            goto L_0x010c
        L_0x0092:
            android.content.res.Resources r10 = r9.getResources()
            r0 = 2131758531(0x7f100dc3, float:1.9148029E38)
            java.lang.String r10 = r10.getString(r0)
            r9.f4902c = r10
            android.content.res.Resources r10 = r9.getResources()
            r0 = 2131755144(0x7f100088, float:1.9141159E38)
            goto L_0x0106
        L_0x00a7:
            android.content.res.Resources r10 = r9.getResources()
            java.lang.String r10 = r10.getString(r0)
            r9.f4902c = r10
            android.content.res.Resources r10 = r9.getResources()
            r0 = 2131755143(0x7f100087, float:1.9141157E38)
            goto L_0x0106
        L_0x00b9:
            android.content.res.Resources r10 = r9.getResources()
            r0 = 2131755142(0x7f100086, float:1.9141155E38)
            java.lang.String r10 = r10.getString(r0)
            r9.f4902c = r10
            android.content.res.Resources r10 = r9.getResources()
            r0 = 2131755141(0x7f100085, float:1.9141153E38)
            goto L_0x0106
        L_0x00ce:
            android.content.res.Resources r10 = r9.getResources()
            java.lang.String r10 = r10.getString(r0)
            r9.f4902c = r10
            android.content.res.Resources r10 = r9.getResources()
            r0 = 2131758530(0x7f100dc2, float:1.9148027E38)
            goto L_0x0106
        L_0x00e0:
            android.content.res.Resources r10 = r9.getResources()
            java.lang.String r10 = r10.getString(r0)
            r9.f4902c = r10
            android.content.res.Resources r10 = r9.getResources()
            r0 = 2131756366(0x7f10054e, float:1.9143637E38)
            goto L_0x0106
        L_0x00f2:
            android.content.res.Resources r10 = r9.getResources()
            r0 = 2131756370(0x7f100552, float:1.9143646E38)
            java.lang.String r10 = r10.getString(r0)
            r9.f4902c = r10
            android.content.res.Resources r10 = r9.getResources()
            r0 = 2131756369(0x7f100551, float:1.9143644E38)
        L_0x0106:
            java.lang.String r10 = r10.getString(r0)
            r9.f4903d = r10
        L_0x010c:
            java.lang.String r10 = r9.f4902c
            if (r10 == 0) goto L_0x0114
            java.lang.String r10 = r9.f4903d
            if (r10 != 0) goto L_0x0117
        L_0x0114:
            r9.finish()
        L_0x0117:
            java.lang.Class[] r10 = new java.lang.Class[r8]     // Catch:{ Exception -> 0x01c8 }
            java.lang.Class r0 = java.lang.Integer.TYPE     // Catch:{ Exception -> 0x01c8 }
            r10[r7] = r0     // Catch:{ Exception -> 0x01c8 }
            java.lang.Class<miui.app.AlertActivity> r0 = miui.app.AlertActivity.class
            java.lang.String r2 = "mAlertParams"
            java.lang.Object r0 = b.b.o.g.e.a((java.lang.Object) r9, (java.lang.Class<?>) r0, (java.lang.String) r2)     // Catch:{ Exception -> 0x01c8 }
            java.lang.Class<miui.app.AlertActivity> r2 = miui.app.AlertActivity.class
            java.lang.String r3 = "mAlert"
            java.lang.Object r2 = b.b.o.g.e.a((java.lang.Object) r9, (java.lang.Class<?>) r2, (java.lang.String) r3)     // Catch:{ Exception -> 0x01c8 }
            r9.g = r2     // Catch:{ Exception -> 0x01c8 }
            java.lang.Class r2 = r0.getClass()     // Catch:{ Exception -> 0x01c8 }
            java.lang.Class r2 = r2.getSuperclass()     // Catch:{ Exception -> 0x01c8 }
            java.lang.String r3 = "mTitle"
            java.lang.String r4 = r9.f4902c     // Catch:{ Exception -> 0x01c8 }
            b.b.o.g.e.a((java.lang.Object) r0, (java.lang.Class<?>) r2, (java.lang.String) r3, (java.lang.Object) r4)     // Catch:{ Exception -> 0x01c8 }
            java.lang.String r3 = "mMessage"
            java.lang.String r4 = r9.f4903d     // Catch:{ Exception -> 0x01c8 }
            b.b.o.g.e.a((java.lang.Object) r0, (java.lang.Class<?>) r2, (java.lang.String) r3, (java.lang.Object) r4)     // Catch:{ Exception -> 0x01c8 }
            java.lang.String r3 = "mNegativeButtonText"
            android.content.res.Resources r4 = r9.getResources()     // Catch:{ Exception -> 0x01c8 }
            r5 = 2131755753(0x7f1002e9, float:1.9142394E38)
            java.lang.String r4 = r4.getString(r5)     // Catch:{ Exception -> 0x01c8 }
            b.b.o.g.e.a((java.lang.Object) r0, (java.lang.Class<?>) r2, (java.lang.String) r3, (java.lang.Object) r4)     // Catch:{ Exception -> 0x01c8 }
            java.lang.String r3 = "mPositiveButtonText"
            android.content.res.Resources r4 = r9.getResources()     // Catch:{ Exception -> 0x01c8 }
            r5 = 2131757021(0x7f1007dd, float:1.9144966E38)
            java.lang.String r4 = r4.getString(r5)     // Catch:{ Exception -> 0x01c8 }
            b.b.o.g.e.a((java.lang.Object) r0, (java.lang.Class<?>) r2, (java.lang.String) r3, (java.lang.Object) r4)     // Catch:{ Exception -> 0x01c8 }
            java.lang.String r3 = "mPositiveButtonListener"
            b.b.o.g.e.a((java.lang.Object) r0, (java.lang.Class<?>) r2, (java.lang.String) r3, (java.lang.Object) r9)     // Catch:{ Exception -> 0x01c8 }
            java.lang.String r3 = "mNegativeButtonListener"
            b.b.o.g.e.a((java.lang.Object) r0, (java.lang.Class<?>) r2, (java.lang.String) r3, (java.lang.Object) r9)     // Catch:{ Exception -> 0x01c8 }
            r9.setupAlert()     // Catch:{ Exception -> 0x01c8 }
            java.lang.Object r0 = r9.g     // Catch:{ Exception -> 0x01c8 }
            java.lang.String r2 = "getButton"
            java.lang.String r3 = "com.android.internal.app.AlertController"
            java.lang.Class r3 = java.lang.Class.forName(r3)     // Catch:{ Exception -> 0x01c8 }
            java.lang.Object[] r4 = new java.lang.Object[r8]     // Catch:{ Exception -> 0x01c8 }
            java.lang.Integer r5 = java.lang.Integer.valueOf(r6)     // Catch:{ Exception -> 0x01c8 }
            r4[r7] = r5     // Catch:{ Exception -> 0x01c8 }
            java.lang.Object r10 = b.b.o.g.e.a((java.lang.Object) r0, (java.lang.String) r2, (java.lang.Class<?>) r3, (java.lang.Class<?>[]) r10, (java.lang.Object[]) r4)     // Catch:{ Exception -> 0x01c8 }
            android.widget.Button r10 = (android.widget.Button) r10     // Catch:{ Exception -> 0x01c8 }
            r9.h = r10     // Catch:{ Exception -> 0x01c8 }
            java.lang.String r10 = r9.f4900a     // Catch:{ Exception -> 0x01c8 }
            boolean r10 = r1.equals(r10)     // Catch:{ Exception -> 0x01c8 }
            if (r10 != 0) goto L_0x01d0
            android.widget.Button r10 = r9.h     // Catch:{ Exception -> 0x01c8 }
            r10.setEnabled(r7)     // Catch:{ Exception -> 0x01c8 }
            android.widget.Button r10 = r9.h     // Catch:{ Exception -> 0x01c8 }
            android.content.res.Resources r0 = r9.getResources()     // Catch:{ Exception -> 0x01c8 }
            r1 = 2131756374(0x7f100556, float:1.9143654E38)
            java.lang.Object[] r2 = new java.lang.Object[r8]     // Catch:{ Exception -> 0x01c8 }
            int r3 = r9.e     // Catch:{ Exception -> 0x01c8 }
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)     // Catch:{ Exception -> 0x01c8 }
            r2[r7] = r3     // Catch:{ Exception -> 0x01c8 }
            java.lang.String r0 = r0.getString(r1, r2)     // Catch:{ Exception -> 0x01c8 }
            r10.setText(r0)     // Catch:{ Exception -> 0x01c8 }
            android.os.Handler r10 = new android.os.Handler     // Catch:{ Exception -> 0x01c8 }
            android.os.Looper r0 = android.os.Looper.myLooper()     // Catch:{ Exception -> 0x01c8 }
            r10.<init>(r0)     // Catch:{ Exception -> 0x01c8 }
            r9.f = r10     // Catch:{ Exception -> 0x01c8 }
            android.os.Handler r10 = r9.f     // Catch:{ Exception -> 0x01c8 }
            java.lang.Runnable r0 = r9.i     // Catch:{ Exception -> 0x01c8 }
            r1 = 1000(0x3e8, double:4.94E-321)
            r10.postDelayed(r0, r1)     // Catch:{ Exception -> 0x01c8 }
            goto L_0x01d0
        L_0x01c8:
            r10 = move-exception
            java.lang.String r0 = "GameBoosterReflectUtils"
            java.lang.String r1 = "setAlertParams"
            android.util.Log.e(r0, r1, r10)
        L_0x01d0:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.ui.GameBoxAlertActivity.onCreate(android.os.Bundle):void");
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        GameBoxAlertActivity.super.onDestroy();
        if (this.f != null && !"intent_gamebox_func_type_immersion_back".equals(this.f4900a)) {
            this.f.removeCallbacks(this.i);
        }
    }
}
