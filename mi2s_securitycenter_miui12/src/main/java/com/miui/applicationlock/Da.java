package com.miui.applicationlock;

import b.b.k.a.a;
import com.miui.privacyapps.ui.n;
import miui.app.ActionBar;

class Da implements ActionBar.FragmentViewPagerChangeListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ActionBar f3154a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ PrivacyAndAppLockManageActivity f3155b;

    Da(PrivacyAndAppLockManageActivity privacyAndAppLockManageActivity, ActionBar actionBar) {
        this.f3155b = privacyAndAppLockManageActivity;
        this.f3154a = actionBar;
    }

    public void onPageScrollStateChanged(int i) {
    }

    public void onPageScrolled(int i, float f, boolean z, boolean z2) {
    }

    public void onPageSelected(int i) {
        if (i == 1) {
            ((n) this.f3154a.getFragmentAt(i)).c();
            if (!this.f3155b.f3207d) {
                a.a();
                boolean unused = this.f3155b.f3207d = true;
            }
        }
    }
}
