package com.miui.antivirus.activity;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import b.b.b.a.b;
import com.miui.cleanmaster.g;

/* renamed from: com.miui.antivirus.activity.c  reason: case insensitive filesystem */
class C0231c implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MainActivity f2714a;

    C0231c(MainActivity mainActivity) {
        this.f2714a = mainActivity;
    }

    /* JADX WARNING: type inference failed for: r4v5, types: [android.content.Context, com.miui.antivirus.activity.MainActivity] */
    public void onClick(DialogInterface dialogInterface, int i) {
        if (this.f2714a.D) {
            Intent intent = new Intent("miui.intent.action.GARBAGE_CLEANUP");
            intent.addFlags(67108864);
            g.b(this.f2714a, intent);
        } else {
            Intent intent2 = new Intent();
            intent2.addFlags(67108864);
            intent2.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.securityscan.MainActivity"));
            intent2.putExtra("extra_auto_optimize", true);
            this.f2714a.startActivity(intent2);
        }
        b.C0023b.a(this.f2714a.D ? "clean_master" : "home_page_optimise");
    }
}
