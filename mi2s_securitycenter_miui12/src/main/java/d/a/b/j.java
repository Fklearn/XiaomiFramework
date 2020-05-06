package d.a.b;

import android.app.UiModeManager;
import android.graphics.Color;
import android.util.ArrayMap;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.AbsListView;
import d.a.d;
import d.a.g.C0575b;
import d.a.j;
import d.a.r;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

public class j extends b implements d.a.j {

    /* renamed from: b  reason: collision with root package name */
    private static WeakHashMap<View, b> f8667b = new WeakHashMap<>();
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public float f8668c;

    /* renamed from: d  reason: collision with root package name */
    private c f8669d;
    private int e;
    private int f;
    private boolean g;
    private int[] h = new int[2];
    private Map<j.a, Boolean> i = new ArrayMap();
    private WeakReference<View> j;
    private WeakReference<View> k;
    private d.a.a.a l = new d.a.a.a();
    private d.a.a.a m = new d.a.a.a();
    private d.a.a.a n;
    private boolean o;

    private static class a implements View.OnTouchListener {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<j> f8670a;

        /* renamed from: b  reason: collision with root package name */
        private d.a.a.a[] f8671b;

        a(j jVar, d.a.a.a... aVarArr) {
            this.f8670a = new WeakReference<>(jVar);
            this.f8671b = aVarArr;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            WeakReference<j> weakReference = this.f8670a;
            j jVar = weakReference == null ? null : (j) weakReference.get();
            if (jVar == null) {
                return false;
            }
            if (motionEvent == null) {
                jVar.g(this.f8671b);
                return false;
            }
            jVar.a(view, motionEvent, this.f8671b);
            return false;
        }
    }

    private static class b implements View.OnTouchListener {

        /* renamed from: a  reason: collision with root package name */
        private WeakHashMap<j, d.a.a.a[]> f8672a;

        private b() {
            this.f8672a = new WeakHashMap<>();
        }

        /* synthetic */ b(g gVar) {
            this();
        }

        /* access modifiers changed from: package-private */
        public void a(j jVar, d.a.a.a... aVarArr) {
            this.f8672a.put(jVar, aVarArr);
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            for (Map.Entry next : this.f8672a.entrySet()) {
                ((j) next.getKey()).a(view, motionEvent, (d.a.a.a[]) next.getValue());
            }
            return false;
        }
    }

    private static class c {

        /* renamed from: a  reason: collision with root package name */
        AbsListView f8673a;

        /* renamed from: b  reason: collision with root package name */
        View f8674b;

        private c() {
        }

        /* synthetic */ c(g gVar) {
            this();
        }
    }

    public j(d... dVarArr) {
        super(dVarArr);
        a(dVarArr.length > 0 ? dVarArr[0] : null);
        C0575b a2 = a(2);
        C0575b a3 = a(3);
        a state = this.f8645a.getState(j.a.UP);
        state.a(a2, 1.0f, new long[0]);
        state.a(a3, 1.0f, new long[0]);
        a state2 = this.f8645a.getState(j.a.DOWN);
        state2.a(a2, 0.9f, new long[0]);
        state2.a(a3, 0.9f, new long[0]);
        b();
        this.l.f8626c = d.a.i.b.a(-2, 0.99f, 0.15f);
        this.l.a(new g(this));
        this.m.f8626c = d.a.i.b.a(-2, 0.99f, 0.3f);
        d.a.a.a aVar = new d.a.a.a(a(4));
        aVar.a(-2, 0.9f, 0.2f);
        this.n = aVar;
    }

    private View a(WeakReference<View> weakReference) {
        View view = (View) weakReference.get();
        if (view != null) {
            view.setOnTouchListener((View.OnTouchListener) null);
        }
        return view;
    }

    private c a(View view) {
        AbsListView absListView = null;
        c cVar = new c((g) null);
        ViewParent parent = view.getParent();
        while (true) {
            if (parent == null) {
                break;
            } else if (parent instanceof AbsListView) {
                absListView = (AbsListView) parent;
                break;
            } else {
                if (parent instanceof View) {
                    view = (View) parent;
                }
                parent = parent.getParent();
            }
        }
        if (absListView != null) {
            this.k = new WeakReference<>(cVar.f8673a);
            cVar.f8673a = absListView;
            cVar.f8674b = view;
        }
        return cVar;
    }

    public static m a(AbsListView absListView) {
        return (m) absListView.getTag(d.d.b.miuix_animation_tag_touch_listener);
    }

    private j.a a(j.a... aVarArr) {
        return aVarArr.length > 0 ? aVarArr[0] : j.a.DOWN;
    }

    private void a() {
        this.g = false;
    }

    private void a(MotionEvent motionEvent, View view, d.a.a.a... aVarArr) {
        if (this.g && !a(view, this.h, motionEvent)) {
            c(aVarArr);
            a();
        }
    }

    /* access modifiers changed from: private */
    public void a(View view, MotionEvent motionEvent, d.a.a.a... aVarArr) {
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            f(aVarArr);
        } else if (actionMasked != 2) {
            g(aVarArr);
        } else {
            a(motionEvent, view, aVarArr);
        }
    }

    /* access modifiers changed from: private */
    public void a(View view, boolean z) {
        view.setClickable(z);
        view.setOnTouchListener((View.OnTouchListener) null);
    }

    private void a(AbsListView absListView, View view, boolean z, d.a.a.a... aVarArr) {
        m a2 = a(absListView);
        if (a2 == null) {
            a2 = new m(absListView);
            absListView.setTag(d.d.b.miuix_animation_tag_touch_listener, a2);
        }
        if (z) {
            absListView.setOnTouchListener(a2);
        }
        a2.a(view, (View.OnTouchListener) new a(this, aVarArr));
    }

    private void a(d dVar) {
        View targetObject = dVar instanceof r ? ((r) dVar).getTargetObject() : null;
        if (targetObject != null) {
            this.f8668c = TypedValue.applyDimension(1, 10.0f, targetObject.getResources().getDisplayMetrics());
        }
    }

    static boolean a(View view, int[] iArr, MotionEvent motionEvent) {
        if (view == null) {
            return true;
        }
        view.getLocationOnScreen(iArr);
        int rawX = (int) motionEvent.getRawX();
        int rawY = (int) motionEvent.getRawY();
        return rawX >= iArr[0] && rawX <= iArr[0] + view.getWidth() && rawY >= iArr[1] && rawY <= iArr[1] + view.getHeight();
    }

    /* access modifiers changed from: private */
    public boolean a(j.a aVar) {
        return Boolean.TRUE.equals(this.i.get(aVar));
    }

    private void b() {
        if (!this.o) {
            int argb = Color.argb(20, 0, 0, 0);
            Object targetObject = this.f8645a.getTarget().getTargetObject();
            if (targetObject instanceof View) {
                View view = (View) targetObject;
                int i2 = d.d.a.miuix_folme_color_touch_tint;
                UiModeManager uiModeManager = (UiModeManager) view.getContext().getSystemService("uimode");
                if (uiModeManager != null && uiModeManager.getNightMode() == 2) {
                    i2 = d.d.a.miuix_folme_color_touch_tint_dark;
                }
                argb = view.getResources().getColor(i2);
            }
            this.f8645a.getState(j.a.DOWN).a(a(7), argb, new long[0]);
        }
    }

    private boolean b(View view) {
        WeakReference<View> weakReference = this.j;
        if ((weakReference != null ? (View) weakReference.get() : null) == view) {
            return false;
        }
        this.j = new WeakReference<>(view);
        return true;
    }

    /* access modifiers changed from: private */
    public boolean b(View view, boolean z, d.a.a.a... aVarArr) {
        c a2;
        if (this.f8645a.getTarget() == null || (a2 = a(view)) == null || a2.f8673a == null) {
            return false;
        }
        Log.d("miuix_anim", "handleListViewTouch for " + view);
        a(a2.f8673a, view, z, aVarArr);
        return true;
    }

    private void c(View view, d.a.a.a... aVarArr) {
        b bVar = f8667b.get(view);
        if (bVar == null) {
            bVar = new b((g) null);
            f8667b.put(view, bVar);
        }
        view.setOnTouchListener(bVar);
        bVar.a(this, aVarArr);
    }

    private d.a.a.a[] d(d.a.a.a... aVarArr) {
        return (d.a.a.a[]) d.a.i.a.a((T[]) aVarArr, (T[]) new d.a.a.a[]{this.l});
    }

    private d.a.a.a[] e(d.a.a.a... aVarArr) {
        return (d.a.a.a[]) d.a.i.a.a((T[]) aVarArr, (T[]) new d.a.a.a[]{this.m, this.n});
    }

    private void f(d.a.a.a... aVarArr) {
        Log.d("miuix_anim", "onEventDown, touchDown");
        this.g = true;
        a(aVarArr);
    }

    /* access modifiers changed from: private */
    public void g(d.a.a.a... aVarArr) {
        if (this.g) {
            Log.d("miuix_anim", "onEventUp, touchUp");
            c(aVarArr);
            a();
        }
    }

    public d.a.j a(float f2, j.a... aVarArr) {
        j.a a2 = a(aVarArr);
        this.i.put(a2, true);
        a state = this.f8645a.getState(a2);
        state.a(a(2), f2, new long[0]);
        state.a(a(3), f2, new long[0]);
        return this;
    }

    public void a(View view, boolean z, d.a.a.a... aVarArr) {
        c(view, aVarArr);
        if (b(view)) {
            Log.d("miuix_anim", "handleViewTouch for " + view);
            boolean isClickable = view.isClickable();
            view.setClickable(true);
            d.a.i.a.a(view, (Runnable) new i(this, z, view, aVarArr, isClickable));
        }
    }

    public void a(View view, d.a.a.a... aVarArr) {
        if (b(view)) {
            d.a.i.a.a(view, (Runnable) new h(this, view, aVarArr));
        }
    }

    public void a(c cVar) {
        this.f8669d = cVar;
    }

    public void a(d.a.a.a... aVarArr) {
        b();
        a(j.a.UP, j.a.DOWN);
        d.a.a.a[] d2 = d(aVarArr);
        c cVar = this.f8669d;
        if (cVar != null) {
            cVar.a(this.f, d2);
        }
        l lVar = this.f8645a;
        lVar.a(lVar.getState(j.a.DOWN), d2);
    }

    public d.a.j b(int i2) {
        this.o = true;
        this.f8645a.getState(j.a.DOWN).a(a(7), i2, new long[0]);
        return this;
    }

    public void b(View view, d.a.a.a... aVarArr) {
        a(view, false, aVarArr);
    }

    public void c(d.a.a.a... aVarArr) {
        a(j.a.DOWN, j.a.UP);
        d.a.a.a[] e2 = e(aVarArr);
        c cVar = this.f8669d;
        if (cVar != null) {
            cVar.a(this.e, e2);
        }
        l lVar = this.f8645a;
        lVar.a(lVar.getState(j.a.UP), e2);
    }

    public void clean() {
        super.clean();
        c cVar = this.f8669d;
        if (cVar != null) {
            cVar.clean();
        }
        this.i.clear();
        WeakReference<View> weakReference = this.j;
        if (weakReference != null) {
            a(weakReference);
            this.j = null;
        }
        WeakReference<View> weakReference2 = this.k;
        if (weakReference2 != null) {
            View a2 = a(weakReference2);
            if (a2 != null) {
                a2.setTag(d.d.b.miuix_animation_tag_touch_listener, (Object) null);
            }
            this.k = null;
        }
        a();
    }

    public d.a.j setTint(float f2, float f3, float f4, float f5) {
        b(Color.argb((int) (f2 * 255.0f), (int) (f3 * 255.0f), (int) (f4 * 255.0f), (int) (f5 * 255.0f)));
        return this;
    }
}
