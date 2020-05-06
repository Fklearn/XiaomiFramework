package com.miui.optimizemanage;

import com.miui.common.customview.AutoPasteListView;
import com.miui.optimizemanage.a.a;

class p implements AutoPasteListView.b {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ v f5984a;

    p(v vVar) {
        this.f5984a = vVar;
    }

    public void a(float f) {
        this.f5984a.h.setAlpha((-1.2f * f) + 1.0f);
        if (!this.f5984a.k && f > 0.5f) {
            a.a();
            boolean unused = this.f5984a.k = true;
        }
    }
}
