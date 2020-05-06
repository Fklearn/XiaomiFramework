package d.a.g;

import android.view.View;

class t extends B {
    t(String str) {
        super(str);
    }

    /* renamed from: a */
    public void setValue(View view, float f) {
        view.setTranslationY(f);
    }

    /* renamed from: b */
    public float getValue(View view) {
        return view.getTranslationY();
    }
}
