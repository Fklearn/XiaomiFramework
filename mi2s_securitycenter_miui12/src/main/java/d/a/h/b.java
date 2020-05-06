package d.a.h;

import android.os.Build;
import android.view.View;
import d.a.a.g;
import d.a.d;
import d.a.g.D;
import d.a.r;

public class b extends a {
    private int q;

    b(Object obj) {
        super(obj, D.f8758a);
    }

    private boolean a(View view) {
        return view == null || Build.VERSION.SDK_INT < 23;
    }

    private View q() {
        d h = h();
        if (h instanceof r) {
            return ((r) h).getTargetObject();
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public void a(g gVar) {
        super.a(gVar);
        this.q = gVar.e(this.h, this.i);
    }

    /* access modifiers changed from: protected */
    public void m() {
        super.m();
        View q2 = q();
        if (!a(q2)) {
            g.b(q2).a((c) this, this.q);
        }
    }
}
