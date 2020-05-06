package com.miui.superpower;

class g implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ h f8104a;

    g(h hVar) {
        this.f8104a = hVar;
    }

    public void run() {
        boolean unused = this.f8104a.f8105a.n = false;
        this.f8104a.f8105a.q.edit().putBoolean("pref_key_superpower_user_entersuperpower", false).commit();
    }
}
