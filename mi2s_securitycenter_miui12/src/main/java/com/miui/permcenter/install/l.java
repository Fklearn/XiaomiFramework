package com.miui.permcenter.install;

import android.content.Context;
import b.b.c.i.a;
import com.miui.securitycenter.R;
import java.util.Collections;
import java.util.List;

class l extends a<g> {

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ PackageManagerActivity f6161b;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    l(PackageManagerActivity packageManagerActivity, Context context) {
        super(context);
        this.f6161b = packageManagerActivity;
    }

    public g loadInBackground() {
        Context context = getContext();
        List<h> b2 = d.a(context).b();
        g gVar = new g();
        if (b2.size() > 0) {
            gVar.a(context.getString(R.string.reject_usb_install));
            gVar.a(b2);
            Collections.sort(b2, new k(this));
        }
        return gVar;
    }
}
