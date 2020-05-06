package com.xiaomi.stat;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.SystemClock;

class r implements Application.ActivityLifecycleCallbacks {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ e f8600a;

    /* renamed from: b  reason: collision with root package name */
    private int f8601b;

    r(e eVar) {
        this.f8600a = eVar;
    }

    public void onActivityCreated(Activity activity, Bundle bundle) {
    }

    public void onActivityDestroyed(Activity activity) {
    }

    public void onActivityPaused(Activity activity) {
        e.j(this.f8600a);
        if (this.f8601b == System.identityHashCode(activity)) {
            long elapsedRealtime = SystemClock.elapsedRealtime() - this.f8600a.f;
            long l = this.f8600a.d();
            this.f8600a.a(activity.getClass().getName(), l - elapsedRealtime, l);
            this.f8600a.h();
        }
    }

    public void onActivityResumed(Activity activity) {
        e.h(this.f8600a);
        this.f8601b = System.identityHashCode(activity);
        long unused = this.f8600a.f = SystemClock.elapsedRealtime();
        this.f8600a.h();
    }

    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    public void onActivityStarted(Activity activity) {
        if (this.f8600a.i == 0) {
            long unused = this.f8600a.l = SystemClock.elapsedRealtime();
            int unused2 = this.f8600a.j = 0;
            int unused3 = this.f8600a.k = 0;
            this.f8600a.e.execute(new s(this));
        }
        e.g(this.f8600a);
    }

    public void onActivityStopped(Activity activity) {
        e.m(this.f8600a);
        if (this.f8600a.i == 0) {
            long elapsedRealtime = SystemClock.elapsedRealtime() - this.f8600a.l;
            long b2 = com.xiaomi.stat.d.r.b();
            e eVar = this.f8600a;
            eVar.a(eVar.j, this.f8600a.k, b2 - elapsedRealtime, b2);
            this.f8600a.e.execute(new t(this));
        }
    }
}
