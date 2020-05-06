package com.miui.antispam.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;

/* renamed from: com.miui.antispam.ui.activity.w  reason: case insensitive filesystem */
class C0228w implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ z f2619a;

    C0228w(z zVar) {
        this.f2619a = zVar;
    }

    /* JADX WARNING: type inference failed for: r0v3, types: [com.miui.antispam.ui.activity.z, android.content.Context] */
    /* JADX WARNING: type inference failed for: r1v1, types: [com.miui.antispam.ui.activity.z, android.content.Context] */
    /* JADX WARNING: type inference failed for: r0v9, types: [com.miui.antispam.ui.activity.z, android.content.Context] */
    public void onClick(DialogInterface dialogInterface, int i) {
        boolean z;
        Intent intent;
        dialogInterface.dismiss();
        String str = "is_black";
        if (i == 0) {
            intent = new Intent(this.f2619a, AddPhoneListActivity.class);
            intent.putExtra(str, this.f2619a.q);
            str = AddAntiSpamActivity.h;
            z = false;
        } else {
            z = true;
            if (i == 1) {
                intent = new Intent(this.f2619a, AddPhoneListActivity.class);
                intent.putExtra(str, this.f2619a.q);
                str = AddAntiSpamActivity.h;
            } else if (i == 2) {
                this.f2619a.f();
                return;
            } else if (i == 3) {
                intent = new Intent(this.f2619a, AntiSpamAddressActivity.class);
                z = this.f2619a.q;
            } else {
                return;
            }
        }
        intent.putExtra(str, z);
        intent.putExtra(AddAntiSpamActivity.g, this.f2619a.p);
        this.f2619a.startActivity(intent);
    }
}
