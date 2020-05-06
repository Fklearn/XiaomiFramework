package androidx.recyclerview.widget;

class S implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ StaggeredGridLayoutManager f1154a;

    S(StaggeredGridLayoutManager staggeredGridLayoutManager) {
        this.f1154a = staggeredGridLayoutManager;
    }

    public void run() {
        this.f1154a.G();
    }
}
