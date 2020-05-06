package d.a.g;

import android.view.View;

class i extends B {
    i(String str) {
        super(str);
    }

    /* renamed from: a */
    public void setValue(View view, float f) {
        view.setY(f);
    }

    /* renamed from: b */
    public float getValue(View view) {
        return view.getY();
    }
}
