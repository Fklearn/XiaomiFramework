package d.a.h;

import android.view.View;

class f implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ View f8770a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ g f8771b;

    f(View view, g gVar) {
        this.f8770a = view;
        this.f8771b = gVar;
    }

    public void run() {
        this.f8770a.setForeground(this.f8771b);
    }
}
