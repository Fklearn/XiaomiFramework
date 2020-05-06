package com.miui.optimizemanage;

class e implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ f f5933a;

    e(f fVar) {
        this.f5933a = fVar;
    }

    public void run() {
        OptimizemanageMainActivity optimizemanageMainActivity = (OptimizemanageMainActivity) this.f5933a.f5934a.getActivity();
        if (optimizemanageMainActivity != null && !optimizemanageMainActivity.isFinishing() && !optimizemanageMainActivity.f5862b) {
            this.f5933a.f5934a.a();
            this.f5933a.f5934a.a(true);
        }
    }
}
