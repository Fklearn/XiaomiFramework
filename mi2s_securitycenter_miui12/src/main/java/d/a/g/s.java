package d.a.g;

import android.view.View;

class s extends B {
    s(String str) {
        super(str);
    }

    /* renamed from: a */
    public void setValue(View view, float f) {
        view.setTranslationX(f);
    }

    /* renamed from: b */
    public float getValue(View view) {
        return view.getTranslationX();
    }
}
