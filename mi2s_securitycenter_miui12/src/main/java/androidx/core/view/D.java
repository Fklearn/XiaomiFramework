package androidx.core.view;

import android.animation.Animator;
import android.os.Build;
import android.view.View;
import android.view.animation.Interpolator;
import java.lang.ref.WeakReference;

public final class D {

    /* renamed from: a  reason: collision with root package name */
    private WeakReference<View> f780a;

    /* renamed from: b  reason: collision with root package name */
    Runnable f781b = null;

    /* renamed from: c  reason: collision with root package name */
    Runnable f782c = null;

    /* renamed from: d  reason: collision with root package name */
    int f783d = -1;

    static class a implements E {

        /* renamed from: a  reason: collision with root package name */
        D f784a;

        /* renamed from: b  reason: collision with root package name */
        boolean f785b;

        a(D d2) {
            this.f784a = d2;
        }

        public void onAnimationCancel(View view) {
            Object tag = view.getTag(2113929216);
            E e = tag instanceof E ? (E) tag : null;
            if (e != null) {
                e.onAnimationCancel(view);
            }
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v5, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v2, resolved type: androidx.core.view.E} */
        /* JADX WARNING: Multi-variable type inference failed */
        @android.annotation.SuppressLint({"WrongConstant"})
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onAnimationEnd(android.view.View r4) {
            /*
                r3 = this;
                androidx.core.view.D r0 = r3.f784a
                int r0 = r0.f783d
                r1 = -1
                r2 = 0
                if (r0 <= r1) goto L_0x000f
                r4.setLayerType(r0, r2)
                androidx.core.view.D r0 = r3.f784a
                r0.f783d = r1
            L_0x000f:
                int r0 = android.os.Build.VERSION.SDK_INT
                r1 = 16
                if (r0 >= r1) goto L_0x0019
                boolean r0 = r3.f785b
                if (r0 != 0) goto L_0x0039
            L_0x0019:
                androidx.core.view.D r0 = r3.f784a
                java.lang.Runnable r1 = r0.f782c
                if (r1 == 0) goto L_0x0024
                r0.f782c = r2
                r1.run()
            L_0x0024:
                r0 = 2113929216(0x7e000000, float:4.2535296E37)
                java.lang.Object r0 = r4.getTag(r0)
                boolean r1 = r0 instanceof androidx.core.view.E
                if (r1 == 0) goto L_0x0031
                r2 = r0
                androidx.core.view.E r2 = (androidx.core.view.E) r2
            L_0x0031:
                if (r2 == 0) goto L_0x0036
                r2.onAnimationEnd(r4)
            L_0x0036:
                r4 = 1
                r3.f785b = r4
            L_0x0039:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: androidx.core.view.D.a.onAnimationEnd(android.view.View):void");
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v5, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v2, resolved type: androidx.core.view.E} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onAnimationStart(android.view.View r4) {
            /*
                r3 = this;
                r0 = 0
                r3.f785b = r0
                androidx.core.view.D r0 = r3.f784a
                int r0 = r0.f783d
                r1 = 0
                r2 = -1
                if (r0 <= r2) goto L_0x000f
                r0 = 2
                r4.setLayerType(r0, r1)
            L_0x000f:
                androidx.core.view.D r0 = r3.f784a
                java.lang.Runnable r2 = r0.f781b
                if (r2 == 0) goto L_0x001a
                r0.f781b = r1
                r2.run()
            L_0x001a:
                r0 = 2113929216(0x7e000000, float:4.2535296E37)
                java.lang.Object r0 = r4.getTag(r0)
                boolean r2 = r0 instanceof androidx.core.view.E
                if (r2 == 0) goto L_0x0027
                r1 = r0
                androidx.core.view.E r1 = (androidx.core.view.E) r1
            L_0x0027:
                if (r1 == 0) goto L_0x002c
                r1.onAnimationStart(r4)
            L_0x002c:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: androidx.core.view.D.a.onAnimationStart(android.view.View):void");
        }
    }

    D(View view) {
        this.f780a = new WeakReference<>(view);
    }

    private void a(View view, E e) {
        if (e != null) {
            view.animate().setListener(new B(this, e, view));
        } else {
            view.animate().setListener((Animator.AnimatorListener) null);
        }
    }

    public D a(float f) {
        View view = (View) this.f780a.get();
        if (view != null) {
            view.animate().alpha(f);
        }
        return this;
    }

    public D a(long j) {
        View view = (View) this.f780a.get();
        if (view != null) {
            view.animate().setDuration(j);
        }
        return this;
    }

    public D a(Interpolator interpolator) {
        View view = (View) this.f780a.get();
        if (view != null) {
            view.animate().setInterpolator(interpolator);
        }
        return this;
    }

    public D a(E e) {
        View view = (View) this.f780a.get();
        if (view != null) {
            if (Build.VERSION.SDK_INT < 16) {
                view.setTag(2113929216, e);
                e = new a(this);
            }
            a(view, e);
        }
        return this;
    }

    public D a(G g) {
        View view = (View) this.f780a.get();
        if (view != null && Build.VERSION.SDK_INT >= 19) {
            C c2 = null;
            if (g != null) {
                c2 = new C(this, g, view);
            }
            view.animate().setUpdateListener(c2);
        }
        return this;
    }

    public void a() {
        View view = (View) this.f780a.get();
        if (view != null) {
            view.animate().cancel();
        }
    }

    public long b() {
        View view = (View) this.f780a.get();
        if (view != null) {
            return view.animate().getDuration();
        }
        return 0;
    }

    public D b(float f) {
        View view = (View) this.f780a.get();
        if (view != null) {
            view.animate().translationY(f);
        }
        return this;
    }

    public D b(long j) {
        View view = (View) this.f780a.get();
        if (view != null) {
            view.animate().setStartDelay(j);
        }
        return this;
    }

    public void c() {
        View view = (View) this.f780a.get();
        if (view != null) {
            view.animate().start();
        }
    }
}
