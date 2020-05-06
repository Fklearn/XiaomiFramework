package androidx.preference;

import androidx.recyclerview.widget.RecyclerView;

class p implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ r f1044a;

    p(r rVar) {
        this.f1044a = rVar;
    }

    public void run() {
        RecyclerView recyclerView = this.f1044a.mList;
        recyclerView.focusableViewAvailable(recyclerView);
    }
}
