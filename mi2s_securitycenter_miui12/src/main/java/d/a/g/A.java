package d.a.g;

import android.view.View;

class A extends B {
    A(String str) {
        super(str);
    }

    /* renamed from: a */
    public void setValue(View view, float f) {
        view.setX(f);
    }

    /* renamed from: b */
    public float getValue(View view) {
        return view.getX();
    }
}
