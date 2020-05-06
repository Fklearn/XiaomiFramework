package com.miui.permcenter.privacymanager.behaviorrecord;

import com.miui.common.stickydecoration.b.b;
import com.miui.securitycenter.R;
import miui.util.Log;

class d implements b {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AppBehaviorRecordActivity f6441a;

    d(AppBehaviorRecordActivity appBehaviorRecordActivity) {
        this.f6441a = appBehaviorRecordActivity;
    }

    public void a(int i, int i2) {
        if (i2 == R.id.header_classify_menu) {
            Log.i("BehaviorRecord-ALL", "onClick header menu");
            this.f6441a.n();
        }
    }
}
