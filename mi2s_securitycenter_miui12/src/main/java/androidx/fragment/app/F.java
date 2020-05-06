package androidx.fragment.app;

import android.graphics.Rect;
import android.transition.Transition;

class F extends Transition.EpicenterCallback {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Rect f880a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ J f881b;

    F(J j, Rect rect) {
        this.f881b = j;
        this.f880a = rect;
    }

    public Rect onGetEpicenter(Transition transition) {
        return this.f880a;
    }
}
