package com.miui.gamebooster.p;

import android.content.Intent;
import android.view.View;
import com.miui.gamebooster.m.C0373d;
import com.miui.gamebooster.m.C0393y;
import com.xiaomi.stat.MiStat;

class b implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ c f4710a;

    b(c cVar) {
        this.f4710a = cVar;
    }

    public void onClick(View view) {
        C0373d.f(MiStat.Event.CLICK, "settings");
        this.f4710a.c();
        C0393y.a(this.f4710a.f4712b, new Intent("com.miui.gamebooster.action.ACCESS_MAINACTIVITY"), "00007", true);
    }
}
