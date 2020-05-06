package com.miui.antispam.ui.activity;

import android.content.DialogInterface;
import android.widget.EditText;

class E implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ EditText f2528a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ KeywordListActivity f2529b;

    E(KeywordListActivity keywordListActivity, EditText editText) {
        this.f2529b = keywordListActivity;
        this.f2528a = editText;
    }

    /* JADX WARNING: type inference failed for: r4v8, types: [miui.app.Activity, com.miui.antispam.ui.activity.KeywordListActivity] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onClick(android.content.DialogInterface r4, int r5) {
        /*
            r3 = this;
            android.widget.EditText r4 = r3.f2528a
            android.text.Editable r4 = r4.getText()
            java.lang.String r4 = r4.toString()
            java.lang.String r4 = r4.trim()
            boolean r5 = android.text.TextUtils.isEmpty(r4)
            r0 = 0
            if (r5 != 0) goto L_0x0076
            java.lang.String r5 = ","
            boolean r5 = r4.contains(r5)
            if (r5 != 0) goto L_0x0066
            java.lang.String r5 = "，"
            boolean r5 = r4.contains(r5)
            if (r5 == 0) goto L_0x0026
            goto L_0x0066
        L_0x0026:
            com.miui.antispam.ui.activity.KeywordListActivity r5 = r3.f2529b
            java.util.HashSet r5 = r5.q
            boolean r5 = r5.contains(r4)
            if (r5 == 0) goto L_0x0045
            com.miui.antispam.ui.activity.KeywordListActivity r5 = r3.f2529b
            r1 = 2131758376(0x7f100d28, float:1.9147714E38)
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r0] = r4
            java.lang.String r4 = r5.getString(r1, r2)
            android.widget.Toast r4 = android.widget.Toast.makeText(r5, r4, r0)
            goto L_0x0083
        L_0x0045:
            android.content.ContentValues r5 = new android.content.ContentValues
            r5.<init>()
            java.lang.String r0 = "data"
            r5.put(r0, r4)
            com.miui.antispam.ui.activity.KeywordListActivity r4 = r3.f2529b
            android.content.ContentResolver r4 = r4.getContentResolver()
            android.net.Uri r0 = miui.provider.ExtraTelephony.Keyword.CONTENT_URI
            com.miui.antispam.ui.activity.KeywordListActivity r1 = r3.f2529b
            long r1 = r1.k
            android.net.Uri r0 = android.content.ContentUris.withAppendedId(r0, r1)
            r1 = 0
            r4.update(r0, r5, r1, r1)
            goto L_0x0086
        L_0x0066:
            com.miui.antispam.ui.activity.KeywordListActivity r4 = r3.f2529b
            android.content.Context r4 = r4.getApplicationContext()
            com.miui.antispam.ui.activity.KeywordListActivity r5 = r3.f2529b
            r1 = 2131758377(0x7f100d29, float:1.9147716E38)
            java.lang.String r5 = r5.getString(r1)
            goto L_0x007f
        L_0x0076:
            com.miui.antispam.ui.activity.KeywordListActivity r4 = r3.f2529b
            r5 = 2131758375(0x7f100d27, float:1.9147712E38)
            java.lang.String r5 = r4.getString(r5)
        L_0x007f:
            android.widget.Toast r4 = android.widget.Toast.makeText(r4, r5, r0)
        L_0x0083:
            r4.show()
        L_0x0086:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.antispam.ui.activity.E.onClick(android.content.DialogInterface, int):void");
    }
}
