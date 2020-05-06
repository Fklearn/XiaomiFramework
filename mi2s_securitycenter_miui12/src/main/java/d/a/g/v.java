package d.a.g;

import android.view.View;

class v extends B {
    v(String str) {
        super(str);
    }

    /* renamed from: a */
    public void setValue(View view, float f) {
        view.setScaleX(f);
    }

    /* renamed from: b */
    public float getValue(View view) {
        return view.getScaleX();
    }
}
