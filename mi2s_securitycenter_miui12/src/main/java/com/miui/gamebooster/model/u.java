package com.miui.gamebooster.model;

import android.view.View;
import com.miui.gamebooster.a.I;
import com.miui.gamebooster.model.C;

class u implements View.OnLongClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ boolean f4595a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ t f4596b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ I.a f4597c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ int f4598d;
    final /* synthetic */ C.a e;

    u(C.a aVar, boolean z, t tVar, I.a aVar2, int i) {
        this.e = aVar;
        this.f4595a = z;
        this.f4596b = tVar;
        this.f4597c = aVar2;
        this.f4598d = i;
    }

    public boolean onLongClick(View view) {
        if (!this.f4595a) {
            this.f4596b.a(true);
            I.a aVar = this.f4597c;
            if (aVar != null) {
                aVar.a(this.f4598d, true);
            }
        }
        return true;
    }
}
