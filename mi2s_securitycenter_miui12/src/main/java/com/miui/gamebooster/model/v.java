package com.miui.gamebooster.model;

import android.view.View;
import com.miui.gamebooster.a.I;
import com.miui.gamebooster.model.C;

class v implements View.OnLongClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ boolean f4599a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ t f4600b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ I.a f4601c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ int f4602d;
    final /* synthetic */ C.a e;

    v(C.a aVar, boolean z, t tVar, I.a aVar2, int i) {
        this.e = aVar;
        this.f4599a = z;
        this.f4600b = tVar;
        this.f4601c = aVar2;
        this.f4602d = i;
    }

    public boolean onLongClick(View view) {
        if (!this.f4599a) {
            this.f4600b.a(true);
            I.a aVar = this.f4601c;
            if (aVar != null) {
                aVar.a(this.f4602d, true);
            }
        }
        return true;
    }
}
