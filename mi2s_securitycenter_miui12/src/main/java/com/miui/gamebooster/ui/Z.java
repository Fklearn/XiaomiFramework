package com.miui.gamebooster.ui;

import android.app.Activity;
import com.miui.gamebooster.l.a;
import com.miui.gamebooster.model.C0395a;
import com.miui.gamebooster.model.C0396b;
import java.util.List;

class Z implements a.C0047a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Activity f5038a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ GameBoosterRealMainActivity f5039b;

    Z(GameBoosterRealMainActivity gameBoosterRealMainActivity, Activity activity) {
        this.f5039b = gameBoosterRealMainActivity;
        this.f5038a = activity;
    }

    public void a(C0395a aVar) {
        List<C0396b> a2 = aVar.a();
        if (a2 != null && a2.size() > 0) {
            this.f5039b.a(a2.get(0), this.f5038a);
        }
    }
}
