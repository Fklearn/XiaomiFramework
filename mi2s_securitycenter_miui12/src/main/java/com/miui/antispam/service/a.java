package com.miui.antispam.service;

import android.util.Log;
import com.miui.antispam.service.b;

class a implements b.a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AntiSpamService f2394a;

    a(AntiSpamService antiSpamService) {
        this.f2394a = antiSpamService;
    }

    public void a(b bVar) {
        AntiSpamService.c(this.f2394a);
    }

    public void b(b bVar) {
        synchronized (this.f2394a.f2391c) {
            AntiSpamService.d(this.f2394a);
            this.f2394a.f2391c.remove(bVar.a());
            if (this.f2394a.f2392d == 0) {
                this.f2394a.stopSelf();
                Log.i(AntiSpamService.f2389a, "stopSelf");
            }
        }
    }
}
