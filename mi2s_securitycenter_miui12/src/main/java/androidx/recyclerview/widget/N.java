package androidx.recyclerview.widget;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

class N {
    static int a(RecyclerView.r rVar, B b2, View view, View view2, RecyclerView.g gVar, boolean z) {
        if (gVar.e() == 0 || rVar.a() == 0 || view == null || view2 == null) {
            return 0;
        }
        if (!z) {
            return Math.abs(gVar.l(view) - gVar.l(view2)) + 1;
        }
        return Math.min(b2.g(), b2.a(view2) - b2.d(view));
    }

    static int a(RecyclerView.r rVar, B b2, View view, View view2, RecyclerView.g gVar, boolean z, boolean z2) {
        if (gVar.e() == 0 || rVar.a() == 0 || view == null || view2 == null) {
            return 0;
        }
        int max = z2 ? Math.max(0, (rVar.a() - Math.max(gVar.l(view), gVar.l(view2))) - 1) : Math.max(0, Math.min(gVar.l(view), gVar.l(view2)));
        if (!z) {
            return max;
        }
        return Math.round((((float) max) * (((float) Math.abs(b2.a(view2) - b2.d(view))) / ((float) (Math.abs(gVar.l(view) - gVar.l(view2)) + 1)))) + ((float) (b2.f() - b2.d(view))));
    }

    static int b(RecyclerView.r rVar, B b2, View view, View view2, RecyclerView.g gVar, boolean z) {
        if (gVar.e() == 0 || rVar.a() == 0 || view == null || view2 == null) {
            return 0;
        }
        if (!z) {
            return rVar.a();
        }
        return (int) ((((float) (b2.a(view2) - b2.d(view))) / ((float) (Math.abs(gVar.l(view) - gVar.l(view2)) + 1))) * ((float) rVar.a()));
    }
}
