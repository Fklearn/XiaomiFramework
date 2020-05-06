package miuix.preference;

import android.view.MotionEvent;
import android.view.View;
import d.a.a.a;
import d.a.b;
import d.a.j;

class f implements View.OnTouchListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ DropDownPreference f8900a;

    f(DropDownPreference dropDownPreference) {
        this.f8900a = dropDownPreference;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 0) {
            j jVar = b.a(view).touch();
            jVar.a(1.0f, new j.a[0]);
            jVar.a(new a[0]);
        } else if (action == 1) {
            this.f8900a.g.performClick(motionEvent.getX(), motionEvent.getY());
        } else if (action == 3) {
            b.a(view).touch().c(new a[0]);
        }
        return true;
    }
}
