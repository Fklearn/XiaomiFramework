package com.miui.optimizemanage;

import android.content.Intent;
import com.miui.common.customview.ActionBarContainer;
import com.miui.optimizemanage.a.a;
import com.miui.optimizemanage.settings.SettingsActivity;

class o implements ActionBarContainer.a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ OptimizemanageMainActivity f5983a;

    o(OptimizemanageMainActivity optimizemanageMainActivity) {
        this.f5983a = optimizemanageMainActivity;
    }

    public void a() {
        this.f5983a.onBackPressed();
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [android.content.Context, com.miui.optimizemanage.OptimizemanageMainActivity] */
    public void b() {
        this.f5983a.startActivity(new Intent(this.f5983a, SettingsActivity.class));
        a.d("speedboost_settings");
    }
}
