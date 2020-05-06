package com.miui.permcenter.privacymanager.behaviorrecord;

import android.view.View;

class j implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AppBehaviorRecordActivity f6448a;

    j(AppBehaviorRecordActivity appBehaviorRecordActivity) {
        this.f6448a = appBehaviorRecordActivity;
    }

    public void onClick(View view) {
        if (view == this.f6448a.v) {
            this.f6448a.q();
        }
    }
}
