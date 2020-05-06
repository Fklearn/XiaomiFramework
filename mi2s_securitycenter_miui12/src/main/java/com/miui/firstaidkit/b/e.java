package com.miui.firstaidkit.b;

import android.view.View;
import com.miui.firstaidkit.FirstAidKitActivity;
import com.miui.firstaidkit.b.h;
import com.miui.securityscan.a.G;
import miui.app.Activity;

class e implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ h f3916a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ h.a f3917b;

    e(h.a aVar, h hVar) {
        this.f3917b = aVar;
        this.f3916a = hVar;
    }

    public void onClick(View view) {
        Activity activity;
        if (this.f3917b.f3927b != null && (activity = (FirstAidKitActivity) this.f3917b.f3927b.get()) != null && !activity.isFinishing() && !activity.isDestroyed()) {
            this.f3916a.f3924a.optimize(activity);
            G.k(this.f3916a.f3924a.getTrackStr());
        }
    }
}
