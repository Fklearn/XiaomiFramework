package com.miui.securityscan;

import android.support.v4.view.ViewPager;
import b.b.j.h;
import com.miui.securityscan.a.G;

/* renamed from: com.miui.securityscan.c  reason: case insensitive filesystem */
class C0543c implements ViewPager.OnPageChangeListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MainActivity f7624a;

    C0543c(MainActivity mainActivity) {
        this.f7624a = mainActivity;
    }

    public void onPageScrollStateChanged(int i) {
    }

    public void onPageScrolled(int i, float f, int i2) {
    }

    public void onPageSelected(int i) {
        String str;
        this.f7624a.f7562b.a(i);
        if (i == 0) {
            ((L) this.f7624a.f7563c[0]).a(true, false);
            ((h) this.f7624a.f7563c[1]).a(false, true);
            ((h) this.f7624a.f7563c[1]).b(false);
            str = "page_securityscan";
        } else {
            ((L) this.f7624a.f7563c[0]).a(false, false);
            ((L) this.f7624a.f7563c[0]).b(false);
            ((h) this.f7624a.f7563c[1]).a(true, true);
            ((h) this.f7624a.f7563c[1]).a(true);
            if (!this.f7624a.f7564d) {
                ((h) this.f7624a.f7563c[1]).b(true);
                boolean unused = this.f7624a.f7564d = true;
            }
            str = "page_phonemanage";
        }
        G.y(str);
        if (!this.f7624a.e) {
            this.f7624a.a(true, true);
        }
    }
}
