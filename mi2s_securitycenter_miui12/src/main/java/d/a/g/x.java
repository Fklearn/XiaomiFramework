package d.a.g;

import android.view.View;

class x extends B {
    x(String str) {
        super(str);
    }

    /* renamed from: a */
    public void setValue(View view, float f) {
        view.setRotation(f);
    }

    /* renamed from: b */
    public float getValue(View view) {
        return view.getRotation();
    }
}
