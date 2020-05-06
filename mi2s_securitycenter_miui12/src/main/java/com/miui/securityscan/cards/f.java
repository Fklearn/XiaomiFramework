package com.miui.securityscan.cards;

import android.content.Intent;
import android.util.Log;

class f implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ String f7649a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ g f7650b;

    f(g gVar, String str) {
        this.f7650b = gVar;
        this.f7649a = str;
    }

    public void run() {
        try {
            Intent launchIntentForPackage = this.f7650b.f.getPackageManager().getLaunchIntentForPackage(this.f7649a);
            if (launchIntentForPackage != null) {
                this.f7650b.f.startActivity(launchIntentForPackage);
            }
        } catch (Exception e) {
            Log.e("InstallCacheManager", " startActivity error ", e);
        }
    }
}
