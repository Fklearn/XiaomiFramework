package d.a.g;

import android.view.View;

class z extends B {
    z(String str) {
        super(str);
    }

    /* renamed from: a */
    public void setValue(View view, float f) {
        view.setRotationY(f);
    }

    /* renamed from: b */
    public float getValue(View view) {
        return view.getRotationY();
    }
}
