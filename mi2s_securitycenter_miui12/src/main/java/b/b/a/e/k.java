package b.b.a.e;

import android.content.Context;

class k implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f1448a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ String f1449b;

    k(Context context, String str) {
        this.f1448a = context;
        this.f1449b = str;
    }

    public void run() {
        n.f(this.f1448a, this.f1449b);
    }
}
