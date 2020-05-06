package d.a.g;

import android.view.View;

class n extends B {
    n(String str) {
        super(str);
    }

    /* renamed from: a */
    public void setValue(View view, float f) {
        view.setAlpha(f);
        boolean z = Math.abs(f) <= 0.00390625f;
        if (view.getVisibility() != 0 && f > 0.0f && !z) {
            view.setVisibility(0);
        } else if (z) {
            view.setVisibility(8);
        }
    }

    /* renamed from: b */
    public float getValue(View view) {
        return view.getAlpha();
    }
}
