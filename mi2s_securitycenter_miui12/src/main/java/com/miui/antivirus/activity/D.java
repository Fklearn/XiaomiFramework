package com.miui.antivirus.activity;

import android.content.DialogInterface;
import b.b.c.j.f;
import com.miui.antivirus.activity.SettingsActivity;
import com.miui.securitycenter.h;

class D implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ SettingsActivity.c f2655a;

    D(SettingsActivity.c cVar) {
        this.f2655a = cVar;
    }

    /* JADX WARNING: type inference failed for: r1v3, types: [android.content.Context, com.miui.antivirus.activity.SettingsActivity] */
    public void onClick(DialogInterface dialogInterface, int i) {
        h.b(true);
        if (f.a(this.f2655a.f2689a)) {
            this.f2655a.b();
        } else {
            this.f2655a.f();
        }
    }
}
