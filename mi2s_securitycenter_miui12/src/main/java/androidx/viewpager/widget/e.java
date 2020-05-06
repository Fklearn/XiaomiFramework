package androidx.viewpager.widget;

import android.graphics.Rect;
import android.view.View;
import androidx.core.view.H;
import androidx.core.view.ViewCompat;
import androidx.core.view.q;

class e implements q {

    /* renamed from: a  reason: collision with root package name */
    private final Rect f1300a = new Rect();

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ ViewPager f1301b;

    e(ViewPager viewPager) {
        this.f1301b = viewPager;
    }

    public H a(View view, H h) {
        H b2 = ViewCompat.b(view, h);
        if (b2.e()) {
            return b2;
        }
        Rect rect = this.f1300a;
        rect.left = b2.b();
        rect.top = b2.d();
        rect.right = b2.c();
        rect.bottom = b2.a();
        int childCount = this.f1301b.getChildCount();
        for (int i = 0; i < childCount; i++) {
            H a2 = ViewCompat.a(this.f1301b.getChildAt(i), b2);
            rect.left = Math.min(a2.b(), rect.left);
            rect.top = Math.min(a2.d(), rect.top);
            rect.right = Math.min(a2.c(), rect.right);
            rect.bottom = Math.min(a2.a(), rect.bottom);
        }
        return b2.a(rect.left, rect.top, rect.right, rect.bottom);
    }
}
