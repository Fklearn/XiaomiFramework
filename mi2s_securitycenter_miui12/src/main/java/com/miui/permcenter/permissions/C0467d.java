package com.miui.permcenter.permissions;

import miui.app.ActionBar;

/* renamed from: com.miui.permcenter.permissions.d  reason: case insensitive filesystem */
class C0467d implements ActionBar.FragmentViewPagerChangeListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AppPermissionsTabActivity f6259a;

    C0467d(AppPermissionsTabActivity appPermissionsTabActivity) {
        this.f6259a = appPermissionsTabActivity;
    }

    public void onPageScrollStateChanged(int i) {
    }

    public void onPageScrolled(int i, float f, boolean z, boolean z2) {
    }

    public void onPageSelected(int i) {
        this.f6259a.a(i);
    }
}
