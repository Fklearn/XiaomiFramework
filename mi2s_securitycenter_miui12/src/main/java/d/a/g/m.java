package d.a.g;

import android.view.View;

class m extends B {
    m(String str) {
        super(str);
    }

    /* renamed from: a */
    public void setValue(View view, float f) {
        view.setAlpha(f);
    }

    /* renamed from: b */
    public float getValue(View view) {
        return view.getAlpha();
    }
}
