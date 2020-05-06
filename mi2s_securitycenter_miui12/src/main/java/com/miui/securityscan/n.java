package com.miui.securityscan;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import b.b.c.j.A;
import b.b.c.j.x;
import com.miui.cleanmaster.f;
import com.miui.cleanmaster.g;
import com.miui.securitycenter.R;
import com.miui.securityscan.a.G;

class n implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Activity f7806a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ L f7807b;

    n(L l, Activity activity) {
        this.f7807b = l;
        this.f7806a = activity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        G.a(10);
        Intent intent = new Intent("miui.intent.action.GARBAGE_CLEANUP");
        intent.putExtra("enter_homepage_way", "security_scan_diversion");
        if (!f.a(this.f7806a)) {
            g.b(this.f7806a, intent, 103, (Bundle) null);
        } else if (!x.a((Context) this.f7806a, intent, 103)) {
            A.a((Context) this.f7806a, (int) R.string.app_not_installed_toast);
        }
    }
}
