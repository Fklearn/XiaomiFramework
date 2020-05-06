package com.miui.gamebooster.p;

import android.view.View;
import com.miui.gamebooster.m.C0373d;
import com.xiaomi.stat.MiStat;

class a implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ c f4709a;

    a(c cVar) {
        this.f4709a = cVar;
    }

    public void onClick(View view) {
        C0373d.f(MiStat.Event.CLICK, TtmlNode.START);
        this.f4709a.c();
    }
}
