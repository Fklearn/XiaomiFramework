package com.miui.applicationlock.c;

import android.content.Context;
import android.graphics.BitmapFactory;
import com.miui.applicationlock.a.h;
import com.miui.securitycenter.R;

/* renamed from: com.miui.applicationlock.c.g  reason: case insensitive filesystem */
class C0263g implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f3305a;

    C0263g(Context context) {
        this.f3305a = context;
    }

    public void run() {
        if (!C0267k.h(this.f3305a) || o.m() >= 2) {
            C0267k.b(this.f3305a, 4, "recommend_app_installed");
            return;
        }
        int m = o.m();
        Context context = this.f3305a;
        o.a(context, R.string.ac_pre_notification_contentTitle, R.string.ac_pre_notification_contentText, C0267k.c(context, "000011"), 103, 4, BitmapFactory.decodeResource(this.f3305a.getResources(), R.drawable.icon_card_app_lock));
        h.h("pre_guide_notification");
        o.g(m + 1);
    }
}
