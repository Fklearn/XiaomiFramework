package com.miui.gamebooster.p;

import android.view.View;
import com.miui.gamebooster.p.f;

class e implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ f.a f4716a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ f f4717b;

    e(f fVar, f.a aVar) {
        this.f4717b = fVar;
        this.f4716a = aVar;
    }

    public void onClick(View view) {
        f.a aVar = this.f4716a;
        if (aVar != null) {
            aVar.a();
        }
    }
}
