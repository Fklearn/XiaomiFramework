package d.g.a.a.a;

import android.view.View;
import d.g.a.a.a.s;

class h extends s.d {
    h(String str) {
        super(str, (j) null);
    }

    public float a(View view) {
        return (float) view.getScrollY();
    }

    public void a(View view, float f) {
        view.setScrollY((int) f);
    }
}
