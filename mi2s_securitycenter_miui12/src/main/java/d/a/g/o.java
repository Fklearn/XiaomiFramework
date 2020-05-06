package d.a.g;

import android.view.View;

class o extends B {
    o(String str) {
        super(str);
    }

    /* renamed from: a */
    public void setValue(View view, float f) {
        view.setScrollX((int) f);
    }

    /* renamed from: b */
    public float getValue(View view) {
        return (float) view.getScrollX();
    }
}
