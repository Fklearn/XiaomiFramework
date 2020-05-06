package androidx.recyclerview.widget;

class C implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ RecyclerView f1082a;

    C(RecyclerView recyclerView) {
        this.f1082a = recyclerView;
    }

    public void run() {
        RecyclerView recyclerView = this.f1082a;
        if (recyclerView.D && !recyclerView.isLayoutRequested()) {
            RecyclerView recyclerView2 = this.f1082a;
            if (!recyclerView2.A) {
                recyclerView2.requestLayout();
            } else if (recyclerView2.G) {
                recyclerView2.F = true;
            } else {
                recyclerView2.b();
            }
        }
    }
}
