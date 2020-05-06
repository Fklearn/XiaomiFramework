package com.miui.antispam.ui.activity;

import android.content.DialogInterface;
import b.b.a.e.n;

/* renamed from: com.miui.antispam.ui.activity.j  reason: case insensitive filesystem */
class C0216j implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0217k f2596a;

    C0216j(C0217k kVar) {
        this.f2596a = kVar;
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.antispam.ui.activity.AntiSpamAddressActivity] */
    public void onClick(DialogInterface dialogInterface, int i) {
        int i2 = (!this.f2596a.f2599c.g.isChecked() || !this.f2596a.f2599c.h.isChecked()) ? this.f2596a.f2599c.g.isChecked() ? 1 : this.f2596a.f2599c.h.isChecked() ? 2 : -1 : 0;
        if (i2 > -1) {
            ? r2 = this.f2596a.f2599c;
            C0217k kVar = this.f2596a;
            n.a(r2, (String[]) r2.k.toArray(new String[0]), i2, (Integer[]) this.f2596a.f2599c.l.toArray(new Integer[0]), kVar.f2598b, kVar.f2597a ^ true ? 1 : 0);
        }
        this.f2596a.f2599c.finish();
    }
}
