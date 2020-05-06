package b.c.a.b.e;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import b.c.a.b.a.i;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

public abstract class d implements a {

    /* renamed from: a  reason: collision with root package name */
    protected Reference<View> f2026a;

    /* renamed from: b  reason: collision with root package name */
    protected boolean f2027b;

    public d(View view) {
        this(view, true);
    }

    public d(View view, boolean z) {
        if (view != null) {
            this.f2026a = new WeakReference(view);
            this.f2027b = z;
            return;
        }
        throw new IllegalArgumentException("view must not be null");
    }

    public int a() {
        View view = this.f2026a.get();
        int i = 0;
        if (view == null) {
            return 0;
        }
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (!(!this.f2027b || layoutParams == null || layoutParams.height == -2)) {
            i = view.getHeight();
        }
        return (i > 0 || layoutParams == null) ? i : layoutParams.height;
    }

    /* access modifiers changed from: protected */
    public abstract void a(Bitmap bitmap, View view);

    /* access modifiers changed from: protected */
    public abstract void a(Drawable drawable, View view);

    public boolean a(Bitmap bitmap) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            View view = this.f2026a.get();
            if (view != null) {
                a(bitmap, view);
                return true;
            }
        } else {
            b.c.a.c.d.d("Can't set a bitmap into view. You should call ImageLoader on UI thread for it.", new Object[0]);
        }
        return false;
    }

    public boolean a(Drawable drawable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            View view = this.f2026a.get();
            if (view != null) {
                a(drawable, view);
                return true;
            }
        } else {
            b.c.a.c.d.d("Can't set a drawable into view. You should call ImageLoader on UI thread for it.", new Object[0]);
        }
        return false;
    }

    public View b() {
        return this.f2026a.get();
    }

    public int c() {
        View view = this.f2026a.get();
        int i = 0;
        if (view == null) {
            return 0;
        }
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (!(!this.f2027b || layoutParams == null || layoutParams.width == -2)) {
            i = view.getWidth();
        }
        return (i > 0 || layoutParams == null) ? i : layoutParams.width;
    }

    public boolean d() {
        return this.f2026a.get() == null;
    }

    public i e() {
        return i.CROP;
    }

    public int getId() {
        View view = this.f2026a.get();
        return view == null ? super.hashCode() : view.hashCode();
    }
}
