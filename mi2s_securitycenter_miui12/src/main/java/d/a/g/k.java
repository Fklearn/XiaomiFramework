package d.a.g;

import android.view.View;
import d.a.m;

class k extends B {
    k(String str) {
        super(str);
    }

    /* renamed from: a */
    public void setValue(View view, float f) {
        view.getLayoutParams().height = (int) f;
        view.setTag(m.miuix_animation_tag_set_height, Float.valueOf(f));
        view.requestLayout();
    }

    /* renamed from: b */
    public float getValue(View view) {
        int height = view.getHeight();
        Float f = (Float) view.getTag(m.miuix_animation_tag_set_height);
        if (f != null) {
            return f.floatValue();
        }
        if (height == 0 && B.b(view)) {
            height = view.getMeasuredHeight();
        }
        return (float) height;
    }
}
