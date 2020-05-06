package com.miui.gamebooster.p;

import com.miui.gamebooster.customview.C0354x;
import com.miui.gamebooster.m.ja;
import com.miui.gamebooster.view.k;

class g implements C0354x.a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ r f4722a;

    g(r rVar) {
        this.f4722a = rVar;
    }

    public void a() {
        if (ja.a("key_gb_record_manual", this.f4722a.e().c())) {
            this.f4722a.a();
        } else {
            k.b();
        }
    }

    public void onFinish() {
        this.f4722a.n();
    }
}
