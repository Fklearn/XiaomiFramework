package b.b.n;

class c implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ String f1850a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ e f1851b;

    c(e eVar, String str) {
        this.f1851b = eVar;
        this.f1850a = str;
    }

    public void run() {
        if (this.f1851b.f1854b == null) {
            this.f1851b.a();
        }
        this.f1851b.f1854b.b(this.f1850a);
    }
}
