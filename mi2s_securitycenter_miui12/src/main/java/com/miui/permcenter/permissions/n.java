package com.miui.permcenter.permissions;

import android.view.View;
import com.miui.permcenter.a;

class n implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int f6276a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ a f6277b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ o f6278c;

    n(o oVar, int i, a aVar) {
        this.f6278c = oVar;
        this.f6276a = i;
        this.f6277b = aVar;
    }

    public void onClick(View view) {
        this.f6278c.f6281c.a(this.f6276a, view, this.f6277b);
    }
}
