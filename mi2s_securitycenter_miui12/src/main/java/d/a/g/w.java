package d.a.g;

import android.view.View;

class w extends B {
    w(String str) {
        super(str);
    }

    /* renamed from: a */
    public void setValue(View view, float f) {
        view.setScaleY(f);
    }

    /* renamed from: b */
    public float getValue(View view) {
        return view.getScaleY();
    }
}
