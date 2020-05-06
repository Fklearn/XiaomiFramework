package com.miui.gamebooster.customview;

import com.miui.gamebooster.d;
import com.miui.securitycenter.R;

/* renamed from: com.miui.gamebooster.customview.k  reason: case insensitive filesystem */
class C0342k implements d {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ GameBoxFunctionItemView f4212a;

    C0342k(GameBoxFunctionItemView gameBoxFunctionItemView) {
        this.f4212a = gameBoxFunctionItemView;
    }

    public void a() {
        this.f4212a.f4126a.setImageResource(R.drawable.gamebox_milink_button);
        this.f4212a.f4127b.setTextColor(this.f4212a.getResources().getColor(R.color.gamebox_func_text));
    }

    public void b() {
        this.f4212a.f4126a.setImageResource(R.drawable.gamebox_milink_light_button);
        this.f4212a.f4127b.setTextColor(this.f4212a.getResources().getColor(R.color.gamebox_func_text_light));
    }
}
