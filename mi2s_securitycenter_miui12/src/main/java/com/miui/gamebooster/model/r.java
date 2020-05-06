package com.miui.gamebooster.model;

import android.view.View;
import com.miui.gamebooster.a.I;
import com.miui.gamebooster.model.s;

class r implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ s f4582a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ I.a f4583b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ int f4584c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ s.a f4585d;

    r(s.a aVar, s sVar, I.a aVar2, int i) {
        this.f4585d = aVar;
        this.f4582a = sVar;
        this.f4583b = aVar2;
        this.f4584c = i;
    }

    public void onClick(View view) {
        s sVar = this.f4582a;
        sVar.b(!sVar.f());
        I.a aVar = this.f4583b;
        if (aVar != null) {
            aVar.b(this.f4584c, this.f4582a.f());
        }
    }
}
