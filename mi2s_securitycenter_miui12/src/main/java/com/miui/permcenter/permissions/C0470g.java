package com.miui.permcenter.permissions;

import android.widget.Button;
import androidx.viewpager.widget.ViewPager;
import com.miui.permcenter.permissions.AppPermissionsTabActivity;
import com.miui.privacyapps.view.ViewPagerIndicator;
import com.miui.securitycenter.R;

/* renamed from: com.miui.permcenter.permissions.g  reason: case insensitive filesystem */
class C0470g implements ViewPager.e {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ViewPagerIndicator f6265a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ AppPermissionsTabActivity.a[] f6266b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ Button f6267c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ AppPermissionsTabActivity f6268d;

    C0470g(AppPermissionsTabActivity appPermissionsTabActivity, ViewPagerIndicator viewPagerIndicator, AppPermissionsTabActivity.a[] aVarArr, Button button) {
        this.f6268d = appPermissionsTabActivity;
        this.f6265a = viewPagerIndicator;
        this.f6266b = aVarArr;
        this.f6267c = button;
    }

    public void onPageScrollStateChanged(int i) {
    }

    public void onPageScrolled(int i, float f, int i2) {
    }

    public void onPageSelected(int i) {
        Button button;
        int i2;
        int unused = this.f6268d.f6196a = i;
        this.f6265a.setSelected(this.f6268d.f6196a);
        if (this.f6268d.f6196a == this.f6266b.length - 1) {
            button = this.f6267c;
            i2 = R.string.ok;
        } else {
            button = this.f6267c;
            i2 = R.string.button_text_next_step;
        }
        button.setText(i2);
    }
}
