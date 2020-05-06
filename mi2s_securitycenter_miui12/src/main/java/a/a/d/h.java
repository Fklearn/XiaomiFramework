package a.a.d;

import android.view.View;
import androidx.core.view.E;
import androidx.core.view.F;

class h extends F {

    /* renamed from: a  reason: collision with root package name */
    private boolean f32a = false;

    /* renamed from: b  reason: collision with root package name */
    private int f33b = 0;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ i f34c;

    h(i iVar) {
        this.f34c = iVar;
    }

    /* access modifiers changed from: package-private */
    public void a() {
        this.f33b = 0;
        this.f32a = false;
        this.f34c.b();
    }

    public void onAnimationEnd(View view) {
        int i = this.f33b + 1;
        this.f33b = i;
        if (i == this.f34c.f35a.size()) {
            E e = this.f34c.f38d;
            if (e != null) {
                e.onAnimationEnd((View) null);
            }
            a();
        }
    }

    public void onAnimationStart(View view) {
        if (!this.f32a) {
            this.f32a = true;
            E e = this.f34c.f38d;
            if (e != null) {
                e.onAnimationStart((View) null);
            }
        }
    }
}
