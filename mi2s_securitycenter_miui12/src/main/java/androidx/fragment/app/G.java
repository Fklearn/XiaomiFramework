package androidx.fragment.app;

import android.transition.Transition;
import android.view.View;
import java.util.ArrayList;

class G implements Transition.TransitionListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ View f891a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ ArrayList f892b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ J f893c;

    G(J j, View view, ArrayList arrayList) {
        this.f893c = j;
        this.f891a = view;
        this.f892b = arrayList;
    }

    public void onTransitionCancel(Transition transition) {
    }

    public void onTransitionEnd(Transition transition) {
        transition.removeListener(this);
        this.f891a.setVisibility(8);
        int size = this.f892b.size();
        for (int i = 0; i < size; i++) {
            ((View) this.f892b.get(i)).setVisibility(0);
        }
    }

    public void onTransitionPause(Transition transition) {
    }

    public void onTransitionResume(Transition transition) {
    }

    public void onTransitionStart(Transition transition) {
    }
}
