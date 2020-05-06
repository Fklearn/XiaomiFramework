package androidx.appcompat.graphics.drawable;

class c implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ d f337a;

    c(d dVar) {
        this.f337a = dVar;
    }

    public void run() {
        this.f337a.a(true);
        this.f337a.invalidateSelf();
    }
}
