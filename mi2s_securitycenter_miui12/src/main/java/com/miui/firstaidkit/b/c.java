package com.miui.firstaidkit.b;

import android.view.View;
import com.miui.firstaidkit.FirstAidKitActivity;
import com.miui.firstaidkit.b.d;
import com.miui.securityscan.model.AbsModel;

class c implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AbsModel f3907a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ d.a f3908b;

    c(d.a aVar, AbsModel absModel) {
        this.f3908b = aVar;
        this.f3907a = absModel;
    }

    /* JADX WARNING: type inference failed for: r2v6, types: [com.miui.firstaidkit.FirstAidKitActivity, android.content.Context] */
    public void onClick(View view) {
        ? r2;
        if (this.f3908b.f3912a != null && (r2 = (FirstAidKitActivity) this.f3908b.f3912a.get()) != 0) {
            this.f3907a.optimize(r2);
        }
    }
}
