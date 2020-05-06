package com.miui.permcenter.privacymanager.b;

import android.graphics.RectF;
import android.view.ViewTreeObserver;

class h implements ViewTreeObserver.OnGlobalLayoutListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ m f6362a;

    h(m mVar) {
        this.f6362a = mVar;
    }

    public void onGlobalLayout() {
        this.f6362a.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        m mVar = this.f6362a;
        mVar.setMessageLocation(mVar.e());
        int[] iArr = new int[2];
        this.f6362a.j.getLocationOnScreen(iArr);
        m mVar2 = this.f6362a;
        RectF unused = mVar2.k = new RectF((float) iArr[0], (float) iArr[1], (float) (iArr[0] + mVar2.j.getWidth()), (float) (iArr[1] + this.f6362a.j.getHeight()));
        this.f6362a.i.set(this.f6362a.getPaddingLeft(), this.f6362a.getPaddingTop(), this.f6362a.getWidth() - this.f6362a.getPaddingRight(), this.f6362a.getHeight() - this.f6362a.getPaddingBottom());
        m mVar3 = this.f6362a;
        float unused2 = mVar3.s = (float) ((int) (mVar3.n ? this.f6362a.s : -this.f6362a.s));
        m mVar4 = this.f6362a;
        float unused3 = mVar4.q = (mVar4.n ? this.f6362a.k.bottom : this.f6362a.k.top) + this.f6362a.s;
        m mVar5 = this.f6362a;
        float unused4 = mVar5.m = ((float) mVar5.p) + this.f6362a.t;
        this.f6362a.f();
        this.f6362a.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }
}
