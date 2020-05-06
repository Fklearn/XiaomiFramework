package com.miui.securityscan;

import android.os.SystemClock;
import com.miui.common.customview.AutoPasteListView;
import com.miui.common.customview.gif.GifImageView;
import com.miui.securityscan.a.G;
import com.miui.securityscan.cards.d;

class I implements AutoPasteListView.b {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ L f7554a;

    I(L l) {
        this.f7554a = l;
    }

    private void b(float f) {
        L l;
        boolean z;
        if (f > 0.5f) {
            if (!this.f7554a.w) {
                G.o("slide_down");
                if (this.f7554a.R > 0) {
                    G.j((SystemClock.elapsedRealtime() - this.f7554a.R) / 1000);
                }
                long unused = this.f7554a.R = SystemClock.elapsedRealtime();
                l = this.f7554a;
                z = true;
            } else {
                return;
            }
        } else if (f < 1.0E-6f && this.f7554a.w) {
            if (this.f7554a.R > 0) {
                G.i((SystemClock.elapsedRealtime() - this.f7554a.R) / 1000);
            }
            long unused2 = this.f7554a.R = SystemClock.elapsedRealtime();
            l = this.f7554a;
            z = false;
        } else {
            return;
        }
        boolean unused3 = l.w = z;
    }

    public void a(float f) {
        d dVar;
        L l = this.f7554a;
        boolean z = true;
        if (l.sa) {
            l.H.setClickable(f < 1.0E-6f);
        }
        if (f < 1.0E-6f) {
            L l2 = this.f7554a;
            if (!l2.ea && (dVar = l2.fa) != null) {
                l2.a(dVar.f());
            }
            this.f7554a.da = true;
        } else {
            this.f7554a.da = false;
        }
        b(f);
        float f2 = (-2.5f * f) + 1.0f;
        this.f7554a.E.setAlpha(f2);
        int i = (f2 > 0.0f ? 1 : (f2 == 0.0f ? 0 : -1));
        this.f7554a.E.setEnabled(i > 0);
        this.f7554a.D.setAlpha(f2);
        GifImageView g = this.f7554a.D;
        if (i <= 0) {
            z = false;
        }
        g.setEnabled(z);
        this.f7554a.B.setAlpha((f * 10.0f) - 4.0f);
        this.f7554a.ja.setAlpha(f2);
    }
}
