package com.miui.applicationlock;

import android.view.View;
import com.miui.applicationlock.a.h;
import com.xiaomi.stat.MiStat;

/* renamed from: com.miui.applicationlock.k  reason: case insensitive filesystem */
class C0283k implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0312y f3357a;

    C0283k(C0312y yVar) {
        this.f3357a = yVar;
    }

    public void onClick(View view) {
        if (view == this.f3357a.e) {
            C0312y yVar = this.f3357a;
            yVar.a(yVar.L);
            h.e(MiStat.Event.SEARCH);
        }
    }
}
