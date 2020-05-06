package com.miui.gamebooster.a;

import android.view.View;
import com.miui.gamebooster.i.a.b;

class m implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ v f4051a;

    m(v vVar) {
        this.f4051a = vVar;
    }

    public void onClick(View view) {
        if (this.f4051a.i == null || this.f4051a.i.getChildAt(0) == null || this.f4051a.i.getChildAt(0).getTop() == 0) {
            this.f4051a.e();
            return;
        }
        this.f4051a.i.smoothScrollToPosition(0);
        b.d(this.f4051a.s);
    }
}
