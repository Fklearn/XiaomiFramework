package com.miui.firstaidkit.b;

import android.view.View;
import com.miui.common.card.models.BaseCardModel;
import com.miui.firstaidkit.b.h;

class g implements View.OnLongClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ h f3921a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ BaseCardModel f3922b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ h.a f3923c;

    g(h.a aVar, h hVar, BaseCardModel baseCardModel) {
        this.f3923c = aVar;
        this.f3921a = hVar;
        this.f3922b = baseCardModel;
    }

    public boolean onLongClick(View view) {
        if (this.f3921a.f3924a == null) {
            return true;
        }
        this.f3923c.showFirstAidItemLongClickDialog(this.f3922b, this.f3921a.f3924a, this.f3923c.f3926a);
        return true;
    }
}
