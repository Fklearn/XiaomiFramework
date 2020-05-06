package d.a.g;

import android.view.View;

class y extends B {
    y(String str) {
        super(str);
    }

    /* renamed from: a */
    public void setValue(View view, float f) {
        view.setRotationX(f);
    }

    /* renamed from: b */
    public float getValue(View view) {
        return view.getRotationX();
    }
}
