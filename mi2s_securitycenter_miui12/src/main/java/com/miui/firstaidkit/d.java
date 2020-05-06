package com.miui.firstaidkit;

import android.content.Intent;
import com.miui.common.customview.ActionBarContainer;
import miui.app.Activity;

class d implements ActionBarContainer.a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ FirstAidKitActivity f3932a;

    d(FirstAidKitActivity firstAidKitActivity) {
        this.f3932a = firstAidKitActivity;
    }

    public void a() {
        this.f3932a.onBackPressed();
    }

    public void b() {
        Activity activity = this.f3932a;
        activity.startActivity(new Intent(activity, FirstAidKitWhiteListActivity.class));
    }
}
