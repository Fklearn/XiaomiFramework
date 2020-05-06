package com.miui.gamebooster.n.d;

import android.view.View;
import com.miui.gamebooster.n.d.b;

class k implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ b.a f4703a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ l f4704b;

    k(l lVar, b.a aVar) {
        this.f4704b = lVar;
        this.f4703a = aVar;
    }

    public void onClick(View view) {
        b.a aVar = this.f4703a;
        if (aVar != null) {
            aVar.a(this.f4704b, view);
        }
    }
}
