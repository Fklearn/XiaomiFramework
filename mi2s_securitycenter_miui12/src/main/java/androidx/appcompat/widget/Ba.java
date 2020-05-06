package androidx.appcompat.widget;

import android.view.View;
import androidx.core.view.F;

class Ba extends F {

    /* renamed from: a  reason: collision with root package name */
    private boolean f459a = false;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ int f460b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ Ca f461c;

    Ba(Ca ca, int i) {
        this.f461c = ca;
        this.f460b = i;
    }

    public void onAnimationCancel(View view) {
        this.f459a = true;
    }

    public void onAnimationEnd(View view) {
        if (!this.f459a) {
            this.f461c.f466a.setVisibility(this.f460b);
        }
    }

    public void onAnimationStart(View view) {
        this.f461c.f466a.setVisibility(0);
    }
}
