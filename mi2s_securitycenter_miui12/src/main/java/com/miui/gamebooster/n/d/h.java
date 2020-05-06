package com.miui.gamebooster.n.d;

import android.support.v4.view.ViewPager;
import com.miui.gamebooster.videobox.adapter.h;

class h implements ViewPager.OnPageChangeListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ h.a f4698a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ j f4699b;

    h(j jVar, h.a aVar) {
        this.f4699b = jVar;
        this.f4698a = aVar;
    }

    public void onPageScrollStateChanged(int i) {
    }

    public void onPageScrolled(int i, float f, int i2) {
        this.f4698a.f5172d.a(i, f);
    }

    public void onPageSelected(int i) {
    }
}
