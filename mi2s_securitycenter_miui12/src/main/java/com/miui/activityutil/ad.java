package com.miui.activityutil;

final class ad extends ai {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ aa f2263a;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    ad(aa aaVar) {
        super(aaVar);
        this.f2263a = aaVar;
    }

    /* access modifiers changed from: package-private */
    public final void a(int i, int i2, boolean z) {
        if (i == 2 || i == 5) {
            new w().a(this.f2263a.r, aa.f2257d, new h(this.f2263a.r).b(), (x) new ae(this));
        } else if (i == 3) {
            aj.a((Runnable) new ab(this.f2263a, i2));
        } else {
            if (z) {
                int unused = this.f2263a.s = 4;
            }
            this.f2263a.t = false;
        }
    }
}
