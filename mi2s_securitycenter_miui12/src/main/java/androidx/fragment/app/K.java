package androidx.fragment.app;

import android.view.View;
import androidx.core.view.ViewCompat;
import java.util.ArrayList;

class K implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int f900a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ ArrayList f901b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ ArrayList f902c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ ArrayList f903d;
    final /* synthetic */ ArrayList e;
    final /* synthetic */ N f;

    K(N n, int i, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4) {
        this.f = n;
        this.f900a = i;
        this.f901b = arrayList;
        this.f902c = arrayList2;
        this.f903d = arrayList3;
        this.e = arrayList4;
    }

    public void run() {
        for (int i = 0; i < this.f900a; i++) {
            ViewCompat.a((View) this.f901b.get(i), (String) this.f902c.get(i));
            ViewCompat.a((View) this.f903d.get(i), (String) this.e.get(i));
        }
    }
}
