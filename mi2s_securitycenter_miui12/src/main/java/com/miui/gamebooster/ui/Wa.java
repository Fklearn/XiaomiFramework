package com.miui.gamebooster.ui;

import android.app.Activity;
import com.miui.gamebooster.m.C0393y;
import com.miui.gamebooster.ui.WelcomActivity;
import com.miui.securitycenter.R;

class Wa implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ WelcomActivity f5016a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ WelcomActivity.b f5017b;

    Wa(WelcomActivity.b bVar, WelcomActivity welcomActivity) {
        this.f5017b = bVar;
        this.f5016a = welcomActivity;
    }

    /* JADX WARNING: type inference failed for: r0v2, types: [com.miui.gamebooster.ui.WelcomActivity, android.app.Activity] */
    /* JADX WARNING: type inference failed for: r0v3, types: [com.miui.gamebooster.ui.WelcomActivity, android.app.Activity] */
    public void run() {
        if (this.f5016a.e) {
            ? r0 = this.f5016a;
            C0393y.a((Activity) r0, r0.f5020c, this.f5016a.getResources().getString(R.string.xunyou_pay_webview), 100);
            return;
        }
        ? r02 = this.f5016a;
        C0393y.b(r02, r02.f5020c, this.f5016a.getResources().getString(R.string.xunyou_pay_webview), 100);
    }
}
