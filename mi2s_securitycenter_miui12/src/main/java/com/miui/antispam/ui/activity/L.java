package com.miui.antispam.ui.activity;

import android.content.Intent;
import android.view.View;
import b.b.a.a.a;
import miui.app.Activity;

class L implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MainActivity f2553a;

    L(MainActivity mainActivity) {
        this.f2553a = mainActivity;
    }

    public void onClick(View view) {
        Activity activity = this.f2553a;
        activity.startActivity(new Intent(activity, AntiSpamNewSettingsActivity.class));
        a.a("settings");
    }
}
