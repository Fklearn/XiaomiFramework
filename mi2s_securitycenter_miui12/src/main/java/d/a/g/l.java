package d.a.g;

import android.view.View;
import d.a.m;

class l extends B {
    l(String str) {
        super(str);
    }

    /* renamed from: a */
    public void setValue(View view, float f) {
        view.getLayoutParams().width = (int) f;
        view.setTag(m.miuix_animation_tag_set_width, Float.valueOf(f));
        view.requestLayout();
    }

    /* renamed from: b */
    public float getValue(View view) {
        int width = view.getWidth();
        Float f = (Float) view.getTag(m.miuix_animation_tag_set_width);
        if (f != null) {
            return f.floatValue();
        }
        if (width == 0 && B.b(view)) {
            width = view.getMeasuredWidth();
        }
        return (float) width;
    }
}
