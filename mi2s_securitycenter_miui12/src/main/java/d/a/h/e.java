package d.a.h;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

class e implements View.OnAttachStateChangeListener {
    e() {
    }

    public void onViewAttachedToWindow(View view) {
    }

    public void onViewDetachedFromWindow(View view) {
        g a2 = g.a(view);
        if (a2 != null && Build.VERSION.SDK_INT >= 23) {
            Drawable a3 = a2.g;
            if (a3 != null) {
                view.setForeground(a3);
            }
            a2.a();
            view.removeOnAttachStateChangeListener(this);
        }
    }
}
