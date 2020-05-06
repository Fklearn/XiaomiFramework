package com.miui.superpower;

class e implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ f f8102a;

    e(f fVar) {
        this.f8102a = fVar;
    }

    public void run() {
        boolean unused = this.f8102a.f8103a.o = false;
        this.f8102a.f8103a.q.edit().putBoolean("pref_key_superpower_user_leavesuperpower", false).commit();
        if (this.f8102a.f8103a.i < 5 && !this.f8102a.f8103a.j.get()) {
            this.f8102a.f8103a.a(true, false);
        }
    }
}
