package miuix.preference;

/* renamed from: miuix.preference.b  reason: case insensitive filesystem */
class C0579b implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ String f8894a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ c f8895b;

    C0579b(c cVar, String str) {
        this.f8895b = cVar;
        this.f8894a = str;
    }

    public void run() {
        if (!this.f8894a.equals(this.f8895b.f8896a.c()) && this.f8895b.f8896a.callChangeListener(this.f8894a)) {
            this.f8895b.f8896a.b(this.f8894a);
        }
    }
}
