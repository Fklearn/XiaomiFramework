package miuix.recyclerview.widget;

import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

class b implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ List f8919a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ c f8920b;

    b(c cVar, List list) {
        this.f8920b = cVar;
        this.f8919a = list;
    }

    public void run() {
        for (RecyclerView.u t : this.f8919a) {
            this.f8920b.t(t);
        }
    }
}
