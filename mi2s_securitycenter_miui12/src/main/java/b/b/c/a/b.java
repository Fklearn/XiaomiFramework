package b.b.c.a;

import android.view.View;
import android.view.animation.Animation;
import b.b.c.a.c;

class b implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ c f1596a;

    b(c cVar) {
        this.f1596a = cVar;
    }

    public void run() {
        c.a aVar = (c.a) this.f1596a.f1597a.get(this.f1596a.f1598b);
        View a2 = aVar.f1601a;
        Animation b2 = aVar.f1602b;
        b2.setAnimationListener(this.f1596a);
        a2.setVisibility(0);
        a2.startAnimation(b2);
    }
}
