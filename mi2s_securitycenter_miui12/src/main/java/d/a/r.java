package d.a;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import d.a.g.B;
import d.a.g.C0575b;
import d.a.g.D;
import java.lang.ref.WeakReference;

public class r extends d<View> {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public static a f8806a = new a((p) null);

    /* renamed from: b  reason: collision with root package name */
    public static final i<View> f8807b = new p();

    /* renamed from: c  reason: collision with root package name */
    private WeakReference<View> f8808c;

    /* renamed from: d  reason: collision with root package name */
    private boolean f8809d;

    private static final class a implements View.OnAttachStateChangeListener {
        private a() {
        }

        /* synthetic */ a(p pVar) {
            this();
        }

        public void onViewAttachedToWindow(View view) {
        }

        public void onViewDetachedFromWindow(View view) {
            b.b((T[]) new View[]{view});
        }
    }

    private r(View view) {
        this.f8808c = new WeakReference<>(view);
    }

    /* synthetic */ r(View view, p pVar) {
        this(view);
    }

    /* access modifiers changed from: private */
    public void a(View view, Runnable runnable) {
        ViewParent parent = view.getParent();
        if (parent instanceof ViewGroup) {
            view.setTag(m.miuix_animation_tag_init_layout, true);
            ViewGroup viewGroup = (ViewGroup) parent;
            view.measure(viewGroup.getMeasuredWidthAndState(), viewGroup.getMeasuredHeightAndState());
            runnable.run();
            view.setTag(m.miuix_animation_tag_init_layout, (Object) null);
        }
    }

    private void b(View view, Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            if (e.getClass().getName().contains("CalledFromWrongThreadException")) {
                this.f8809d = true;
                view.post(runnable);
                return;
            }
            throw e;
        }
    }

    public boolean allowAnimRun() {
        return getTargetObject() != null && !b.a(getTargetObject());
    }

    public void executeOnInitialized(Runnable runnable) {
        View view = (View) this.f8808c.get();
        if (view == null) {
            return;
        }
        if (view.getVisibility() == 8 && !view.isLaidOut() && (view.getWidth() == 0 || view.getHeight() == 0)) {
            post(new q(this, view, runnable));
        } else {
            post(runnable);
        }
    }

    public void getLocationOnScreen(int[] iArr) {
        View view = (View) this.f8808c.get();
        if (view == null) {
            iArr[1] = Integer.MAX_VALUE;
            iArr[0] = Integer.MAX_VALUE;
            return;
        }
        view.getLocationOnScreen(iArr);
    }

    public C0575b getProperty(int i) {
        switch (i) {
            case 0:
                return B.i;
            case 1:
                return B.j;
            case 2:
                return B.f8757d;
            case 3:
                return B.e;
            case 4:
                return B.n;
            case 5:
                return B.l;
            case 6:
                return B.m;
            case 7:
                return D.f8758a;
            case 8:
                return D.f8759b;
            case 9:
                return B.f;
            case 10:
                return B.g;
            case 11:
                return B.h;
            case 12:
                return B.p;
            case 13:
                return B.q;
            case 14:
                return B.o;
            case 15:
                return B.f8754a;
            case 16:
                return B.f8755b;
            case 17:
                return B.k;
            case 18:
                return B.f8756c;
            default:
                return null;
        }
    }

    public View getTargetObject() {
        return (View) this.f8808c.get();
    }

    public int getType(C0575b bVar) {
        if (bVar.equals(B.i)) {
            return 0;
        }
        if (bVar.equals(B.j)) {
            return 1;
        }
        if (bVar.equals(B.f8754a)) {
            return 15;
        }
        if (bVar.equals(B.f8755b)) {
            return 16;
        }
        if (bVar.equals(B.f8757d)) {
            return 2;
        }
        if (bVar.equals(B.e)) {
            return 3;
        }
        if (bVar.equals(B.n)) {
            return 4;
        }
        if (bVar.equals(B.l)) {
            return 5;
        }
        if (bVar.equals(B.m)) {
            return 6;
        }
        if (bVar.equals(D.f8758a)) {
            return 7;
        }
        if (bVar.equals(D.f8759b)) {
            return 8;
        }
        if (bVar.equals(B.o)) {
            return 14;
        }
        if (bVar.equals(B.f)) {
            return 9;
        }
        if (bVar.equals(B.g)) {
            return 10;
        }
        if (bVar.equals(B.h)) {
            return 11;
        }
        if (bVar.equals(B.p)) {
            return 12;
        }
        if (bVar.equals(B.q)) {
            return 13;
        }
        if (bVar.equals(B.f8756c)) {
            return 18;
        }
        return bVar.equals(B.k) ? 17 : -1;
    }

    public boolean isValid() {
        return ((View) this.f8808c.get()) != null;
    }

    public void onFrameEnd(boolean z) {
        View view = (View) this.f8808c.get();
        if (z && view != null) {
            view.setTag(m.miuix_animation_tag_set_height, (Object) null);
            view.setTag(m.miuix_animation_tag_set_width, (Object) null);
        }
    }

    public void post(Runnable runnable) {
        View targetObject = getTargetObject();
        if (targetObject != null) {
            if (this.f8809d) {
                targetObject.post(runnable);
            } else {
                b(targetObject, runnable);
            }
        }
    }

    public boolean shouldUseIntValue(C0575b bVar) {
        if (bVar == B.m || bVar == B.l || bVar == B.p || bVar == B.q) {
            return true;
        }
        return super.shouldUseIntValue(bVar);
    }
}
