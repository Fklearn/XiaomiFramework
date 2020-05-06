package com.miui.securityscan;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import b.b.c.j.A;
import b.b.c.j.x;
import com.miui.securitycenter.R;
import com.miui.securityscan.a.G;
import miui.os.Build;

/* renamed from: com.miui.securityscan.k  reason: case insensitive filesystem */
class C0551k implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Activity f7755a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ L f7756b;

    C0551k(L l, Activity activity) {
        this.f7756b = l;
        this.f7755a = activity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        Intent intent;
        boolean unused = this.f7756b.Ia = true;
        G.a(7);
        if (Build.IS_INTERNATIONAL_BUILD) {
            intent = new Intent("miui.intent.action.GARBAGE_UNINSTALL_APPS");
        } else {
            intent = new Intent();
            intent.setComponent(new ComponentName("com.xiaomi.market", "com.xiaomi.market.ui.LocalAppsActivity"));
            intent.putExtra("back", true);
        }
        if (!x.c((Context) this.f7755a, intent)) {
            A.a((Context) this.f7755a, (int) R.string.app_not_installed_toast);
        }
    }
}
