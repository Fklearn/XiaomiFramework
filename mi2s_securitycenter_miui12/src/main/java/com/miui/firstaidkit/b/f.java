package com.miui.firstaidkit.b;

import android.view.View;
import com.miui.common.card.models.BaseCardModel;
import com.miui.firstaidkit.b.h;

class f implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ h f3918a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ BaseCardModel f3919b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ h.a f3920c;

    f(h.a aVar, h hVar, BaseCardModel baseCardModel) {
        this.f3920c = aVar;
        this.f3918a = hVar;
        this.f3919b = baseCardModel;
    }

    public void onClick(View view) {
        if (this.f3918a.f3924a != null) {
            this.f3920c.showFirstAidItemLongClickDialog(this.f3919b, this.f3918a.f3924a, this.f3920c.f3926a);
        }
    }
}
