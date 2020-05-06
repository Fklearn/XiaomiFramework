package com.miui.antispam.policy.a;

import android.util.Log;

class a implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ boolean f2357a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ b f2358b;

    a(b bVar, boolean z) {
        this.f2358b = bVar;
        this.f2357a = z;
    }

    public void run() {
        try {
            if (this.f2357a) {
                b.d.a.a.a.a();
            }
            if (this.f2358b.a(this.f2358b.f2359a) || this.f2358b.a(this.f2358b.f2359a, this.f2358b.f2362d)) {
                b.d.a.a.a.a(this.f2358b.f2362d);
                Log.d("SmsEngineHandler", "initNewSmsEngine success.");
            }
        } catch (Throwable th) {
            Log.w("SmsEngineHandler", "initNewSmsEngine failed. " + th);
        }
    }
}
