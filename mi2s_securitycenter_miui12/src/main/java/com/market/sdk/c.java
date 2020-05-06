package com.market.sdk;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

public class c implements Application.ActivityLifecycleCallbacks {

    /* renamed from: a  reason: collision with root package name */
    private i f2225a = p.b().a();

    public void onActivityCreated(Activity activity, Bundle bundle) {
        this.f2225a.a(activity, 0);
    }

    public void onActivityDestroyed(Activity activity) {
        this.f2225a.a(activity, 5);
    }

    public void onActivityPaused(Activity activity) {
        this.f2225a.a(activity, 3);
    }

    public void onActivityResumed(Activity activity) {
        this.f2225a.a(activity, 2);
    }

    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        this.f2225a.a(activity, 6);
    }

    public void onActivityStarted(Activity activity) {
        this.f2225a.a(activity, 1);
    }

    public void onActivityStopped(Activity activity) {
        this.f2225a.a(activity, 4);
    }
}
