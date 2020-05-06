package androidx.fragment.app;

import android.view.View;
import java.util.ArrayList;

class A implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ArrayList f860a;

    A(ArrayList arrayList) {
        this.f860a = arrayList;
    }

    public void run() {
        E.a((ArrayList<View>) this.f860a, 4);
    }
}
