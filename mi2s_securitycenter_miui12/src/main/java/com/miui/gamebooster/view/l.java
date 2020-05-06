package com.miui.gamebooster.view;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import com.miui.gamebooster.globalgame.util.Utils;
import com.miui.gamebooster.j.a;
import com.miui.securitycenter.R;

class l implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int f5305a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ float[] f5306b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ int f5307c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ View f5308d;
    final /* synthetic */ ViewPager e;
    final /* synthetic */ View f;

    l(int i, float[] fArr, int i2, View view, ViewPager viewPager, View view2) {
        this.f5305a = i;
        this.f5306b = fArr;
        this.f5307c = i2;
        this.f5308d = view;
        this.e = viewPager;
        this.f = view2;
    }

    public void run() {
        int i = this.f5305a;
        float[] fArr = this.f5306b;
        int i2 = (int) (((float) i) / fArr[1]);
        int i3 = (int) (((float) i2) * fArr[0]);
        int a2 = (int) ((((float) this.f5307c) / fArr[5]) * n.d());
        int i4 = ((int) (((float) i) / fArr[4])) << 1;
        int i5 = i2 + i4;
        int i6 = i4 + i3;
        ViewGroup.LayoutParams layoutParams = this.f5308d.getLayoutParams();
        layoutParams.height = i6;
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
            marginLayoutParams.setMargins(marginLayoutParams.leftMargin, a2, marginLayoutParams.rightMargin, marginLayoutParams.bottomMargin);
        }
        Utils.a((View) this.e, -1, i3);
        this.e.setPageMargin((int) (-(((float) this.f5305a) * this.f5306b[7])));
        Utils.a(this.f, i5, i6);
        this.f.setBackgroundResource(a.a() ? R.drawable.gb_icon_outline_border : R.drawable.gb_big_post_outline_border);
        this.f5308d.requestLayout();
    }
}
