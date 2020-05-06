package com.miui.gamebooster.a;

import android.view.View;
import com.miui.securitycenter.R;

class j implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ v f4048a;

    j(v vVar) {
        this.f4048a = vVar;
    }

    public void run() {
        this.f4048a.e.setVisibility(0);
        int height = ((View) this.f4048a.e.getParent()).getHeight();
        v vVar = this.f4048a;
        float f = (float) height;
        float unused = vVar.m = (f - (((float) ((int) this.f4048a.k.c())) * vVar.r)) - ((float) this.f4048a.s.getResources().getDimensionPixelOffset(R.dimen.spacing_60));
        this.f4048a.e.animate().translationYBy(f).withEndAction(new C0331i(this, height)).setDuration(0).start();
    }
}
