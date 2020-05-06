package com.miui.optimizemanage.memoryclean;

import android.view.View;
import com.miui.optimizemanage.memoryclean.e;

class d implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ e.c f5955a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ c f5956b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ int f5957c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ e f5958d;

    d(e eVar, e.c cVar, c cVar2, int i) {
        this.f5958d = eVar;
        this.f5955a = cVar;
        this.f5956b = cVar2;
        this.f5957c = i;
    }

    public void onClick(View view) {
        this.f5955a.f5967d.setChecked(!this.f5956b.f5954c);
        if (this.f5958d.f5961c != null) {
            this.f5958d.f5961c.a(this.f5957c, this.f5956b);
        }
    }
}
