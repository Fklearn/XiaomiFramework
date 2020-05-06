package com.miui.securityscan;

import com.miui.common.customview.AutoPasteListView;
import com.miui.securityscan.a.G;

/* renamed from: com.miui.securityscan.e  reason: case insensitive filesystem */
class C0545e implements AutoPasteListView.b {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0546f f7693a;

    C0545e(C0546f fVar) {
        this.f7693a = fVar;
    }

    public void a(float f) {
        L l = this.f7693a.f7694a;
        if (l.O == 1) {
            if (f > 0.1f) {
                if (!l.x) {
                    G.g();
                }
                boolean unused = this.f7693a.f7694a.x = true;
            } else {
                boolean unused2 = l.x = false;
            }
            this.f7693a.f7694a.ja.setAlpha((f * -1.2f) + 1.0f);
        }
    }
}
