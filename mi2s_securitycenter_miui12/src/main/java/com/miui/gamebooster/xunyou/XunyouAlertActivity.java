package com.miui.gamebooster.xunyou;

import android.content.DialogInterface;
import miui.app.AlertActivity;

public class XunyouAlertActivity extends AlertActivity implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private String f5395a;

    /* renamed from: b  reason: collision with root package name */
    private String f5396b;

    /* renamed from: c  reason: collision with root package name */
    private String f5397c;

    /* renamed from: d  reason: collision with root package name */
    private String f5398d;
    private String e;

    /* JADX WARNING: type inference failed for: r12v0, types: [android.content.Context, com.miui.gamebooster.xunyou.XunyouAlertActivity] */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0037, code lost:
        if (r13.equals("xunyou_alert_dialog_first") != false) goto L_0x004b;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onClick(android.content.DialogInterface r13, int r14) {
        /*
            r12 = this;
            r13 = -2
            java.lang.String r0 = "xunyou_alert_dialog_overdue"
            java.lang.String r1 = "xunyou_alert_dialog_expired"
            java.lang.String r2 = "xunyou_alert_dialog_first"
            java.lang.String r3 = "xunyou_alert_dialog_overdue_gift"
            r4 = 0
            java.lang.String r5 = "voice_changer_permission_dialog"
            r6 = -1
            r7 = 4
            r8 = 3
            r9 = 2
            r10 = 1
            java.lang.String r11 = "show"
            if (r14 == r13) goto L_0x007f
            if (r14 == r6) goto L_0x0019
            goto L_0x00d2
        L_0x0019:
            java.lang.String r13 = r12.e
            int r14 = r13.hashCode()
            switch(r14) {
                case -1679992814: goto L_0x0042;
                case -952206162: goto L_0x003a;
                case -892847763: goto L_0x0033;
                case 506548898: goto L_0x002b;
                case 724489245: goto L_0x0023;
                default: goto L_0x0022;
            }
        L_0x0022:
            goto L_0x004a
        L_0x0023:
            boolean r13 = r13.equals(r0)
            if (r13 == 0) goto L_0x004a
            r4 = r10
            goto L_0x004b
        L_0x002b:
            boolean r13 = r13.equals(r1)
            if (r13 == 0) goto L_0x004a
            r4 = r8
            goto L_0x004b
        L_0x0033:
            boolean r13 = r13.equals(r2)
            if (r13 == 0) goto L_0x004a
            goto L_0x004b
        L_0x003a:
            boolean r13 = r13.equals(r5)
            if (r13 == 0) goto L_0x004a
            r4 = r7
            goto L_0x004b
        L_0x0042:
            boolean r13 = r13.equals(r3)
            if (r13 == 0) goto L_0x004a
            r4 = r9
            goto L_0x004b
        L_0x004a:
            r4 = r6
        L_0x004b:
            java.lang.String r13 = "cancle"
            if (r4 == 0) goto L_0x0068
            if (r4 == r10) goto L_0x0064
            if (r4 == r9) goto L_0x0060
            if (r4 == r8) goto L_0x005c
            if (r4 == r7) goto L_0x0058
            goto L_0x006b
        L_0x0058:
            com.miui.gamebooster.m.ma.b((boolean) r10)
            goto L_0x006b
        L_0x005c:
            com.miui.gamebooster.m.C0373d.k(r11, r13)
            goto L_0x006b
        L_0x0060:
            com.miui.gamebooster.m.C0373d.i(r11, r13)
            goto L_0x006b
        L_0x0064:
            com.miui.gamebooster.m.C0373d.p(r11, r13)
            goto L_0x006b
        L_0x0068:
            com.miui.gamebooster.m.C0373d.m(r11, r13)
        L_0x006b:
            java.lang.String r13 = r12.e
            boolean r13 = r5.equals(r13)
            if (r13 != 0) goto L_0x00d2
            android.content.Intent r13 = new android.content.Intent
            java.lang.String r14 = "com.miui.gamebooster.action.MI_PUSH_GAMEBOOSTER_HOT"
            r13.<init>(r14)
            r14 = 0
            com.miui.gamebooster.m.C0393y.a((android.content.Context) r12, (android.content.Intent) r13, (java.lang.String) r14, (boolean) r10)
            goto L_0x00d2
        L_0x007f:
            java.lang.String r13 = r12.e
            int r14 = r13.hashCode()
            switch(r14) {
                case -1679992814: goto L_0x00a9;
                case -952206162: goto L_0x00a1;
                case -892847763: goto L_0x0099;
                case 506548898: goto L_0x0091;
                case 724489245: goto L_0x0089;
                default: goto L_0x0088;
            }
        L_0x0088:
            goto L_0x00b0
        L_0x0089:
            boolean r13 = r13.equals(r0)
            if (r13 == 0) goto L_0x00b0
            r6 = r10
            goto L_0x00b0
        L_0x0091:
            boolean r13 = r13.equals(r1)
            if (r13 == 0) goto L_0x00b0
            r6 = r8
            goto L_0x00b0
        L_0x0099:
            boolean r13 = r13.equals(r2)
            if (r13 == 0) goto L_0x00b0
            r6 = r4
            goto L_0x00b0
        L_0x00a1:
            boolean r13 = r13.equals(r5)
            if (r13 == 0) goto L_0x00b0
            r6 = r7
            goto L_0x00b0
        L_0x00a9:
            boolean r13 = r13.equals(r3)
            if (r13 == 0) goto L_0x00b0
            r6 = r9
        L_0x00b0:
            java.lang.String r13 = "open_now"
            if (r6 == 0) goto L_0x00cf
            java.lang.String r14 = "renew_now"
            if (r6 == r10) goto L_0x00cb
            if (r6 == r9) goto L_0x00c7
            if (r6 == r8) goto L_0x00c3
            if (r6 == r7) goto L_0x00bf
            goto L_0x00d2
        L_0x00bf:
            com.miui.gamebooster.m.ma.b((boolean) r4)
            goto L_0x00d2
        L_0x00c3:
            com.miui.gamebooster.m.C0373d.k(r11, r14)
            goto L_0x00d2
        L_0x00c7:
            com.miui.gamebooster.m.C0373d.i(r11, r13)
            goto L_0x00d2
        L_0x00cb:
            com.miui.gamebooster.m.C0373d.p(r11, r14)
            goto L_0x00d2
        L_0x00cf:
            com.miui.gamebooster.m.C0373d.m(r11, r13)
        L_0x00d2:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.xunyou.XunyouAlertActivity.onClick(android.content.DialogInterface, int):void");
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onCreate(android.os.Bundle r11) {
        /*
            r10 = this;
            com.miui.gamebooster.xunyou.XunyouAlertActivity.super.onCreate(r11)
            android.content.Intent r11 = r10.getIntent()
            java.lang.String r0 = "alertType"
            java.lang.String r11 = r11.getStringExtra(r0)
            r10.e = r11
            java.lang.String r11 = r10.e
            int r0 = r11.hashCode()
            r1 = 0
            r2 = 4
            r3 = 3
            r4 = 2
            r5 = 1
            switch(r0) {
                case -1679992814: goto L_0x0046;
                case -952206162: goto L_0x003c;
                case -892847763: goto L_0x0032;
                case 506548898: goto L_0x0028;
                case 724489245: goto L_0x001e;
                default: goto L_0x001d;
            }
        L_0x001d:
            goto L_0x0050
        L_0x001e:
            java.lang.String r0 = "xunyou_alert_dialog_overdue"
            boolean r11 = r11.equals(r0)
            if (r11 == 0) goto L_0x0050
            r11 = r5
            goto L_0x0051
        L_0x0028:
            java.lang.String r0 = "xunyou_alert_dialog_expired"
            boolean r11 = r11.equals(r0)
            if (r11 == 0) goto L_0x0050
            r11 = r3
            goto L_0x0051
        L_0x0032:
            java.lang.String r0 = "xunyou_alert_dialog_first"
            boolean r11 = r11.equals(r0)
            if (r11 == 0) goto L_0x0050
            r11 = r1
            goto L_0x0051
        L_0x003c:
            java.lang.String r0 = "voice_changer_permission_dialog"
            boolean r11 = r11.equals(r0)
            if (r11 == 0) goto L_0x0050
            r11 = r2
            goto L_0x0051
        L_0x0046:
            java.lang.String r0 = "xunyou_alert_dialog_overdue_gift"
            boolean r11 = r11.equals(r0)
            if (r11 == 0) goto L_0x0050
            r11 = r4
            goto L_0x0051
        L_0x0050:
            r11 = -1
        L_0x0051:
            r0 = 2131757021(0x7f1007dd, float:1.9144966E38)
            java.lang.String r6 = "time"
            java.lang.String r7 = "show"
            r8 = 2131755753(0x7f1002e9, float:1.9142394E38)
            if (r11 == 0) goto L_0x0149
            r9 = 2131757701(0x7f100a85, float:1.9146345E38)
            if (r11 == r5) goto L_0x0117
            if (r11 == r4) goto L_0x00e5
            if (r11 == r3) goto L_0x00a0
            if (r11 == r2) goto L_0x006a
            goto L_0x017a
        L_0x006a:
            android.content.res.Resources r11 = r10.getResources()
            r0 = 2131756484(0x7f1005c4, float:1.9143877E38)
            java.lang.String r11 = r11.getString(r0)
            r10.f5395a = r11
            android.content.res.Resources r11 = r10.getResources()
            r0 = 2131756483(0x7f1005c3, float:1.9143875E38)
            java.lang.String r11 = r11.getString(r0)
            r10.f5396b = r11
            android.content.res.Resources r11 = r10.getResources()
            r0 = 2131756477(0x7f1005bd, float:1.9143863E38)
            java.lang.String r11 = r11.getString(r0)
            r10.f5397c = r11
            android.content.res.Resources r11 = r10.getResources()
            r0 = 2131756476(0x7f1005bc, float:1.914386E38)
            java.lang.String r11 = r11.getString(r0)
            r10.f5398d = r11
            goto L_0x017a
        L_0x00a0:
            android.content.Intent r11 = r10.getIntent()
            java.lang.String r0 = "expired"
            int r11 = r11.getIntExtra(r0, r5)
            android.content.res.Resources r0 = r10.getResources()
            r2 = 2131756930(0x7f100782, float:1.9144781E38)
            java.lang.String r0 = r0.getString(r2)
            r10.f5395a = r0
            android.content.res.Resources r0 = r10.getResources()
            r2 = 2131624004(0x7f0e0044, float:1.8875175E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            java.lang.Integer r4 = java.lang.Integer.valueOf(r11)
            r3[r1] = r4
            java.lang.String r11 = r0.getQuantityString(r2, r11, r3)
            r10.f5396b = r11
            android.content.res.Resources r11 = r10.getResources()
            java.lang.String r11 = r11.getString(r8)
            r10.f5397c = r11
            android.content.res.Resources r11 = r10.getResources()
            java.lang.String r11 = r11.getString(r9)
            r10.f5398d = r11
            com.miui.gamebooster.m.C0373d.k(r7, r6)
            goto L_0x017a
        L_0x00e5:
            android.content.res.Resources r11 = r10.getResources()
            r1 = 2131756301(0x7f10050d, float:1.9143506E38)
            java.lang.String r11 = r11.getString(r1)
            r10.f5395a = r11
            android.content.res.Resources r11 = r10.getResources()
            r1 = 2131756302(0x7f10050e, float:1.9143508E38)
            java.lang.String r11 = r11.getString(r1)
            r10.f5396b = r11
            android.content.res.Resources r11 = r10.getResources()
            java.lang.String r11 = r11.getString(r8)
            r10.f5397c = r11
            android.content.res.Resources r11 = r10.getResources()
            java.lang.String r11 = r11.getString(r0)
            r10.f5398d = r11
            com.miui.gamebooster.m.C0373d.i(r7, r6)
            goto L_0x017a
        L_0x0117:
            android.content.res.Resources r11 = r10.getResources()
            r0 = 2131756931(0x7f100783, float:1.9144783E38)
            java.lang.String r11 = r11.getString(r0)
            r10.f5395a = r11
            android.content.res.Resources r11 = r10.getResources()
            r0 = 2131756932(0x7f100784, float:1.9144785E38)
            java.lang.String r11 = r11.getString(r0)
            r10.f5396b = r11
            android.content.res.Resources r11 = r10.getResources()
            java.lang.String r11 = r11.getString(r8)
            r10.f5397c = r11
            android.content.res.Resources r11 = r10.getResources()
            java.lang.String r11 = r11.getString(r9)
            r10.f5398d = r11
            com.miui.gamebooster.m.C0373d.p(r7, r6)
            goto L_0x017a
        L_0x0149:
            android.content.res.Resources r11 = r10.getResources()
            r1 = 2131756300(0x7f10050c, float:1.9143504E38)
            java.lang.String r11 = r11.getString(r1)
            r10.f5395a = r11
            android.content.res.Resources r11 = r10.getResources()
            r1 = 2131756303(0x7f10050f, float:1.914351E38)
            java.lang.String r11 = r11.getString(r1)
            r10.f5396b = r11
            android.content.res.Resources r11 = r10.getResources()
            java.lang.String r11 = r11.getString(r8)
            r10.f5397c = r11
            android.content.res.Resources r11 = r10.getResources()
            java.lang.String r11 = r11.getString(r0)
            r10.f5398d = r11
            com.miui.gamebooster.m.C0373d.m(r7, r6)
        L_0x017a:
            java.lang.String r11 = r10.f5395a
            if (r11 == 0) goto L_0x0182
            java.lang.String r11 = r10.f5396b
            if (r11 != 0) goto L_0x0185
        L_0x0182:
            r10.finish()
        L_0x0185:
            java.lang.Class<miui.app.AlertActivity> r11 = miui.app.AlertActivity.class
            java.lang.String r0 = "mAlertParams"
            java.lang.Object r11 = b.b.o.g.e.a((java.lang.Object) r10, (java.lang.Class<?>) r11, (java.lang.String) r0)     // Catch:{ Exception -> 0x01bf }
            java.lang.Class r0 = r11.getClass()     // Catch:{ Exception -> 0x01bf }
            java.lang.Class r0 = r0.getSuperclass()     // Catch:{ Exception -> 0x01bf }
            java.lang.String r1 = "mTitle"
            java.lang.String r2 = r10.f5395a     // Catch:{ Exception -> 0x01bf }
            b.b.o.g.e.a((java.lang.Object) r11, (java.lang.Class<?>) r0, (java.lang.String) r1, (java.lang.Object) r2)     // Catch:{ Exception -> 0x01bf }
            java.lang.String r1 = "mMessage"
            java.lang.String r2 = r10.f5396b     // Catch:{ Exception -> 0x01bf }
            b.b.o.g.e.a((java.lang.Object) r11, (java.lang.Class<?>) r0, (java.lang.String) r1, (java.lang.Object) r2)     // Catch:{ Exception -> 0x01bf }
            java.lang.String r1 = "mNegativeButtonText"
            java.lang.String r2 = r10.f5397c     // Catch:{ Exception -> 0x01bf }
            b.b.o.g.e.a((java.lang.Object) r11, (java.lang.Class<?>) r0, (java.lang.String) r1, (java.lang.Object) r2)     // Catch:{ Exception -> 0x01bf }
            java.lang.String r1 = "mPositiveButtonText"
            java.lang.String r2 = r10.f5398d     // Catch:{ Exception -> 0x01bf }
            b.b.o.g.e.a((java.lang.Object) r11, (java.lang.Class<?>) r0, (java.lang.String) r1, (java.lang.Object) r2)     // Catch:{ Exception -> 0x01bf }
            java.lang.String r1 = "mPositiveButtonListener"
            b.b.o.g.e.a((java.lang.Object) r11, (java.lang.Class<?>) r0, (java.lang.String) r1, (java.lang.Object) r10)     // Catch:{ Exception -> 0x01bf }
            java.lang.String r1 = "mNegativeButtonListener"
            b.b.o.g.e.a((java.lang.Object) r11, (java.lang.Class<?>) r0, (java.lang.String) r1, (java.lang.Object) r10)     // Catch:{ Exception -> 0x01bf }
            r10.setupAlert()     // Catch:{ Exception -> 0x01bf }
            goto L_0x01c7
        L_0x01bf:
            r11 = move-exception
            java.lang.String r0 = "GameBoosterReflectUtils"
            java.lang.String r1 = "setAlertParams"
            android.util.Log.e(r0, r1, r11)
        L_0x01c7:
            r10.setupAlert()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.xunyou.XunyouAlertActivity.onCreate(android.os.Bundle):void");
    }
}
