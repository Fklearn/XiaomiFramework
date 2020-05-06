package com.miui.securityscan;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import com.miui.cleanmaster.g;
import com.miui.securityscan.a.G;
import java.net.URISyntaxException;

/* renamed from: com.miui.securityscan.g  reason: case insensitive filesystem */
class C0547g implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Activity f7702a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ L f7703b;

    C0547g(L l, Activity activity) {
        this.f7703b = l;
        this.f7702a = activity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        try {
            boolean unused = this.f7703b.Ga = true;
            G.a(1);
            Intent parseUri = Intent.parseUri("#Intent;action=miui.intent.action.GARBAGE_CLEANUP;end", 0);
            parseUri.putExtra("enter_homepage_way", "security_scan_diversion");
            g.b(this.f7702a, parseUri);
        } catch (URISyntaxException e) {
            Log.e("com.miui.securityscan.MainActivity", "URISyntaxException", e);
        }
    }
}
