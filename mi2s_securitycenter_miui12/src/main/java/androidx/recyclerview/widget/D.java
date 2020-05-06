package androidx.recyclerview.widget;

import androidx.recyclerview.widget.RecyclerView;

class D implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ RecyclerView f1083a;

    D(RecyclerView recyclerView) {
        this.f1083a = recyclerView;
    }

    public void run() {
        RecyclerView.ItemAnimator itemAnimator = this.f1083a.V;
        if (itemAnimator != null) {
            itemAnimator.i();
        }
        this.f1083a.wa = false;
    }
}
