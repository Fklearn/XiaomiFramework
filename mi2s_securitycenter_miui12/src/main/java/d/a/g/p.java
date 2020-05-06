package d.a.g;

import android.view.View;

class p extends B {
    p(String str) {
        super(str);
    }

    /* renamed from: a */
    public void setValue(View view, float f) {
        view.setScrollY((int) f);
    }

    /* renamed from: b */
    public float getValue(View view) {
        return (float) view.getScrollY();
    }
}
