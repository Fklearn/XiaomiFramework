package androidx.fragment.app;

import android.graphics.Rect;
import android.transition.Transition;

class I extends Transition.EpicenterCallback {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Rect f898a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ J f899b;

    I(J j, Rect rect) {
        this.f899b = j;
        this.f898a = rect;
    }

    public Rect onGetEpicenter(Transition transition) {
        Rect rect = this.f898a;
        if (rect == null || rect.isEmpty()) {
            return null;
        }
        return this.f898a;
    }
}
