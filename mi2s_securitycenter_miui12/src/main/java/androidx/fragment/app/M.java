package androidx.fragment.app;

import android.view.View;
import androidx.core.view.ViewCompat;
import java.util.ArrayList;
import java.util.Map;

class M implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ArrayList f907a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Map f908b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ N f909c;

    M(N n, ArrayList arrayList, Map map) {
        this.f909c = n;
        this.f907a = arrayList;
        this.f908b = map;
    }

    public void run() {
        int size = this.f907a.size();
        for (int i = 0; i < size; i++) {
            View view = (View) this.f907a.get(i);
            ViewCompat.a(view, (String) this.f908b.get(ViewCompat.m(view)));
        }
    }
}
