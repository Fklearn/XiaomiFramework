package com.miui.gamebooster.p;

import android.view.View;
import android.widget.AdapterView;
import com.miui.gamebooster.d.d;
import com.miui.gamebooster.m.D;
import com.miui.gamebooster.model.g;

class o implements AdapterView.OnItemClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ r f4733a;

    o(r rVar) {
        this.f4733a = rVar;
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        g gVar = (g) adapterView.getItemAtPosition(i);
        r rVar = this.f4733a;
        D.a(rVar, gVar, rVar.f4740d, view);
        if (gVar.c() != d.ANTIMSG) {
            this.f4733a.i();
        }
    }
}
