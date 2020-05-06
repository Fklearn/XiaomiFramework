package d.a.g;

import android.os.Build;
import android.view.View;

class u extends B {
    u(String str) {
        super(str);
    }

    /* renamed from: a */
    public void setValue(View view, float f) {
        if (Build.VERSION.SDK_INT >= 21) {
            view.setTranslationZ(f);
        }
    }

    /* renamed from: b */
    public float getValue(View view) {
        if (Build.VERSION.SDK_INT >= 21) {
            return view.getTranslationZ();
        }
        return 0.0f;
    }
}
