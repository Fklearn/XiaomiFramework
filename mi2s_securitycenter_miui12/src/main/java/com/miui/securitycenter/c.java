package com.miui.securitycenter;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

class c implements Application.ActivityLifecycleCallbacks {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Application f7470a;

    c(Application application) {
        this.f7470a = application;
    }

    public void onActivityCreated(Activity activity, Bundle bundle) {
    }

    public void onActivityDestroyed(Activity activity) {
    }

    public void onActivityPaused(Activity activity) {
    }

    public void onActivityResumed(Activity activity) {
    }

    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    public void onActivityStarted(Activity activity) {
        this.f7470a.a(false);
        Application.a();
    }

    public void onActivityStopped(Activity activity) {
        Application.b();
        this.f7470a.a(true);
    }
}
