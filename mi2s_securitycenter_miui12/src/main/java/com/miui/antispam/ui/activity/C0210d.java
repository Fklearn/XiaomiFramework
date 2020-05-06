package com.miui.antispam.ui.activity;

import android.view.View;

/* renamed from: com.miui.antispam.ui.activity.d  reason: case insensitive filesystem */
class C0210d implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ boolean f2589a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ AddPhoneListActivity f2590b;

    C0210d(AddPhoneListActivity addPhoneListActivity, boolean z) {
        this.f2590b = addPhoneListActivity;
        this.f2589a = z;
    }

    /* JADX WARNING: type inference failed for: r0v5, types: [android.content.Context, com.miui.antispam.ui.activity.AddPhoneListActivity] */
    /* JADX WARNING: type inference failed for: r0v6, types: [android.content.Context, com.miui.antispam.ui.activity.AddPhoneListActivity] */
    /* JADX WARNING: type inference failed for: r4v8, types: [android.content.Context, com.miui.antispam.ui.activity.AddPhoneListActivity] */
    /* JADX WARNING: type inference failed for: r0v7, types: [android.content.Context, com.miui.antispam.ui.activity.AddPhoneListActivity] */
    /* JADX WARNING: type inference failed for: r3v24, types: [android.content.Context, com.miui.antispam.ui.activity.AddPhoneListActivity] */
    /* JADX WARNING: type inference failed for: r3v26, types: [android.content.Context, com.miui.antispam.ui.activity.AddPhoneListActivity] */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x003f  */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x0041  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0060  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0063  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0070  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x007b  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0094  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00b4  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onClick(android.view.View r11) {
        /*
            r10 = this;
            com.miui.antispam.ui.activity.AddPhoneListActivity r11 = r10.f2590b
            android.widget.CheckBox r11 = r11.e
            boolean r11 = r11.isChecked()
            r0 = 0
            r1 = 2
            r2 = 1
            if (r11 == 0) goto L_0x001c
            com.miui.antispam.ui.activity.AddPhoneListActivity r11 = r10.f2590b
            android.widget.CheckBox r11 = r11.f
            boolean r11 = r11.isChecked()
            if (r11 == 0) goto L_0x001c
            goto L_0x0038
        L_0x001c:
            com.miui.antispam.ui.activity.AddPhoneListActivity r11 = r10.f2590b
            android.widget.CheckBox r11 = r11.f
            boolean r11 = r11.isChecked()
            if (r11 != 0) goto L_0x002a
            r11 = r2
            goto L_0x0039
        L_0x002a:
            com.miui.antispam.ui.activity.AddPhoneListActivity r11 = r10.f2590b
            android.widget.CheckBox r11 = r11.e
            boolean r11 = r11.isChecked()
            if (r11 != 0) goto L_0x0038
            r11 = r1
            goto L_0x0039
        L_0x0038:
            r11 = r0
        L_0x0039:
            com.miui.antispam.ui.activity.AddPhoneListActivity r3 = r10.f2590b
            boolean r3 = r3.f2612c
            if (r3 == 0) goto L_0x0041
            r6 = r1
            goto L_0x0042
        L_0x0041:
            r6 = r11
        L_0x0042:
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            com.miui.antispam.ui.activity.AddPhoneListActivity r3 = r10.f2590b
            android.widget.EditText r3 = r3.f2510d
            android.text.Editable r3 = r3.getText()
            java.lang.String r3 = r3.toString()
            r11.append(r3)
            com.miui.antispam.ui.activity.AddPhoneListActivity r3 = r10.f2590b
            boolean r3 = r3.n
            if (r3 == 0) goto L_0x0063
            java.lang.String r3 = "*"
            goto L_0x0065
        L_0x0063:
            java.lang.String r3 = ""
        L_0x0065:
            r11.append(r3)
            java.lang.String r11 = r11.toString()
            boolean r3 = r10.f2589a
            if (r3 == 0) goto L_0x007b
            com.miui.antispam.ui.activity.AddPhoneListActivity r3 = r10.f2590b
            int r4 = r3.m
            boolean r3 = miui.provider.ExtraTelephony.isInBlacklist(r3, r11, r6, r4)
            goto L_0x0085
        L_0x007b:
            com.miui.antispam.ui.activity.AddPhoneListActivity r3 = r10.f2590b
            int r4 = r3.m
            boolean r3 = miui.provider.ExtraTelephony.isInWhiteList(r3, r11, r6, r4)
        L_0x0085:
            com.miui.antispam.ui.activity.AddPhoneListActivity r4 = r10.f2590b
            long r4 = r4.h
            r7 = -1
            int r4 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
            r5 = 2131757971(0x7f100b93, float:1.9146893E38)
            if (r4 != 0) goto L_0x00b4
            if (r3 == 0) goto L_0x00a0
            b.b.a.b.b r11 = b.b.a.b.b.b()
            com.miui.antispam.ui.activity.AddPhoneListActivity r0 = r10.f2590b
            r11.a(r0, r5)
            return
        L_0x00a0:
            com.miui.antispam.ui.activity.AddPhoneListActivity r4 = r10.f2590b
            java.lang.String[] r5 = new java.lang.String[r2]
            r5[r0] = r11
            r7 = 0
            int r8 = r4.m
            boolean r11 = r10.f2589a
            r9 = r11 ^ 1
            b.b.a.e.n.a(r4, r5, r6, r7, r8, r9)
            goto L_0x0126
        L_0x00b4:
            com.miui.antispam.ui.activity.AddPhoneListActivity r0 = r10.f2590b
            java.lang.String r0 = r0.k
            boolean r0 = r11.equals(r0)
            if (r0 != 0) goto L_0x00cd
            if (r3 != 0) goto L_0x00c3
            goto L_0x00cd
        L_0x00c3:
            b.b.a.b.b r11 = b.b.a.b.b.b()
            com.miui.antispam.ui.activity.AddPhoneListActivity r0 = r10.f2590b
            r11.a(r0, r5)
            return
        L_0x00cd:
            android.content.ContentValues r0 = new android.content.ContentValues
            r0.<init>()
            java.lang.String r3 = "number"
            r0.put(r3, r11)
            java.lang.String r3 = "display_number"
            r0.put(r3, r11)
            java.lang.Integer r3 = java.lang.Integer.valueOf(r6)
            java.lang.String r4 = "state"
            r0.put(r4, r3)
            com.miui.antispam.ui.activity.AddPhoneListActivity r3 = r10.f2590b
            int r3 = r3.j
            r4 = 3
            if (r3 != r4) goto L_0x00f0
            r3 = r1
            goto L_0x00f6
        L_0x00f0:
            com.miui.antispam.ui.activity.AddPhoneListActivity r3 = r10.f2590b
            int r3 = r3.j
        L_0x00f6:
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            java.lang.String r4 = "sync_dirty"
            r0.put(r4, r3)
            com.miui.antispam.ui.activity.AddPhoneListActivity r3 = r10.f2590b
            android.content.ContentResolver r3 = r3.getContentResolver()
            android.net.Uri r4 = miui.provider.ExtraTelephony.Phonelist.CONTENT_URI
            com.miui.antispam.ui.activity.AddPhoneListActivity r5 = r10.f2590b
            long r7 = r5.h
            android.net.Uri r4 = android.content.ContentUris.withAppendedId(r4, r7)
            r5 = 0
            r3.update(r4, r0, r5, r5)
            com.miui.antispam.ui.activity.AddPhoneListActivity r0 = r10.f2590b
            boolean r3 = r10.f2589a
            if (r3 == 0) goto L_0x011c
            goto L_0x011d
        L_0x011c:
            r1 = r2
        L_0x011d:
            com.miui.antispam.ui.activity.AddPhoneListActivity r2 = r10.f2590b
            int r2 = r2.m
            b.b.a.e.n.b(r0, r11, r6, r1, r2)
        L_0x0126:
            com.miui.antispam.ui.activity.AddPhoneListActivity r11 = r10.f2590b
            r11.finish()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.antispam.ui.activity.C0210d.onClick(android.view.View):void");
    }
}
