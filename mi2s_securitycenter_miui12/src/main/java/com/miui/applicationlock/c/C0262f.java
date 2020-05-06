package com.miui.applicationlock.c;

import android.content.Context;
import android.graphics.BitmapFactory;
import com.miui.applicationlock.a.h;
import com.miui.securitycenter.R;

/* renamed from: com.miui.applicationlock.c.f  reason: case insensitive filesystem */
class C0262f implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f3304a;

    C0262f(Context context) {
        this.f3304a = context;
    }

    public void run() {
        if (!C0267k.h(this.f3304a) || o.l() >= 1) {
            C0267k.b(this.f3304a, 3, "competitive_app_installed");
            return;
        }
        Context context = this.f3304a;
        o.a(context, R.string.ac_post_notification_contentTitle, R.string.ac_post_notification_contentText, C0267k.c(context, "000012"), 104, 3, BitmapFactory.decodeResource(this.f3304a.getResources(), R.drawable.icon_card_app_lock));
        h.h("post_guide_notification");
        o.f(1);
    }
}
