package com.miui.gamebooster.model;

import android.content.Context;
import com.miui.gamebooster.a.F;
import com.miui.gamebooster.d;
import com.miui.securitycenter.R;

class h implements d {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ F.a f4563a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Context f4564b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ j f4565c;

    h(j jVar, F.a aVar, Context context) {
        this.f4565c = jVar;
        this.f4563a = aVar;
        this.f4564b = context;
    }

    public void a() {
        this.f4563a.f4013a.setImageResource(R.drawable.gamebox_milink_button);
        this.f4563a.f4014b.setTextColor(this.f4564b.getResources().getColor(R.color.gamebox_func_text));
    }

    public void b() {
        this.f4563a.f4013a.setImageResource(R.drawable.gamebox_milink_light_button);
        this.f4563a.f4014b.setTextColor(this.f4564b.getResources().getColor(R.color.gamebox_func_text_light));
    }
}
