package com.miui.gamebooster.p;

import android.os.CountDownTimer;
import android.view.View;
import com.miui.gamebooster.m.C0373d;
import com.miui.gamebooster.widget.ProgressCircle;

class q implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ProgressCircle f4735a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ r f4736b;

    q(r rVar, ProgressCircle progressCircle) {
        this.f4736b = rVar;
        this.f4735a = progressCircle;
    }

    public void onClick(View view) {
        r rVar;
        CountDownTimer start;
        if (this.f4736b.B != null) {
            this.f4736b.B.cancel();
            this.f4736b.B.onFinish();
            rVar = this.f4736b;
            start = null;
        } else {
            if (this.f4736b.f4739c != null) {
                C0373d.g(this.f4736b.l());
                this.f4736b.f4739c.h();
            }
            rVar = this.f4736b;
            start = new p(this, 3000, 32).start();
        }
        CountDownTimer unused = rVar.B = start;
    }
}
