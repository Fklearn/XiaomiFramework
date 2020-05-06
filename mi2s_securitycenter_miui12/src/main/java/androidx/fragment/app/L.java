package androidx.fragment.app;

import android.view.View;
import androidx.core.view.ViewCompat;
import java.util.ArrayList;
import java.util.Map;

class L implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ArrayList f904a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Map f905b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ N f906c;

    L(N n, ArrayList arrayList, Map map) {
        this.f906c = n;
        this.f904a = arrayList;
        this.f905b = map;
    }

    public void run() {
        int size = this.f904a.size();
        for (int i = 0; i < size; i++) {
            View view = (View) this.f904a.get(i);
            String m = ViewCompat.m(view);
            if (m != null) {
                ViewCompat.a(view, N.a((Map<String, String>) this.f905b, m));
            }
        }
    }
}
