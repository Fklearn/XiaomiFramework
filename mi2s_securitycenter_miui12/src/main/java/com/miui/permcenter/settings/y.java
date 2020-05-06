package com.miui.permcenter.settings;

import com.miui.permcenter.a.a;
import miui.app.ActionBar;

class y implements ActionBar.FragmentViewPagerChangeListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ PrivacySettingsActivity f6602a;

    y(PrivacySettingsActivity privacySettingsActivity) {
        this.f6602a = privacySettingsActivity;
    }

    public void onPageScrollStateChanged(int i) {
    }

    public void onPageScrolled(int i, float f, boolean z, boolean z2) {
    }

    public void onPageSelected(int i) {
        String str;
        if (i == 0) {
            str = "privacy_setting_informed";
        } else if (i == 1) {
            str = "privacy_setting_manage";
        } else {
            return;
        }
        a.d(str);
    }
}
