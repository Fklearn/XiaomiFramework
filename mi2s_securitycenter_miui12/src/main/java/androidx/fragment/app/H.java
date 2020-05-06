package androidx.fragment.app;

import android.transition.Transition;
import android.view.View;
import java.util.ArrayList;

class H implements Transition.TransitionListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Object f894a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ ArrayList f895b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ Object f896c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ ArrayList f897d;
    final /* synthetic */ Object e;
    final /* synthetic */ ArrayList f;
    final /* synthetic */ J g;

    H(J j, Object obj, ArrayList arrayList, Object obj2, ArrayList arrayList2, Object obj3, ArrayList arrayList3) {
        this.g = j;
        this.f894a = obj;
        this.f895b = arrayList;
        this.f896c = obj2;
        this.f897d = arrayList2;
        this.e = obj3;
        this.f = arrayList3;
    }

    public void onTransitionCancel(Transition transition) {
    }

    public void onTransitionEnd(Transition transition) {
        transition.removeListener(this);
    }

    public void onTransitionPause(Transition transition) {
    }

    public void onTransitionResume(Transition transition) {
    }

    public void onTransitionStart(Transition transition) {
        Object obj = this.f894a;
        if (obj != null) {
            this.g.a(obj, (ArrayList<View>) this.f895b, (ArrayList<View>) null);
        }
        Object obj2 = this.f896c;
        if (obj2 != null) {
            this.g.a(obj2, (ArrayList<View>) this.f897d, (ArrayList<View>) null);
        }
        Object obj3 = this.e;
        if (obj3 != null) {
            this.g.a(obj3, (ArrayList<View>) this.f, (ArrayList<View>) null);
        }
    }
}
