package androidx.appcompat.app;

import a.a.d.b;
import a.a.d.g;
import a.a.d.i;
import a.a.f;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import androidx.annotation.RestrictTo;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.view.menu.j;
import androidx.appcompat.widget.ActionBarContainer;
import androidx.appcompat.widget.ActionBarContextView;
import androidx.appcompat.widget.ActionBarOverlayLayout;
import androidx.appcompat.widget.C0090ba;
import androidx.appcompat.widget.M;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.D;
import androidx.core.view.E;
import androidx.core.view.ViewCompat;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

@RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
public class G extends ActionBar implements ActionBarOverlayLayout.a {

    /* renamed from: a  reason: collision with root package name */
    private static final Interpolator f284a = new AccelerateInterpolator();

    /* renamed from: b  reason: collision with root package name */
    private static final Interpolator f285b = new DecelerateInterpolator();
    i A;
    private boolean B;
    boolean C;
    final E D = new D(this);
    final E E = new E(this);
    final androidx.core.view.G F = new F(this);

    /* renamed from: c  reason: collision with root package name */
    Context f286c;

    /* renamed from: d  reason: collision with root package name */
    private Context f287d;
    private Activity e;
    ActionBarOverlayLayout f;
    ActionBarContainer g;
    M h;
    ActionBarContextView i;
    View j;
    C0090ba k;
    private ArrayList<Object> l = new ArrayList<>();
    private int m = -1;
    private boolean n;
    a o;
    b p;
    b.a q;
    private boolean r;
    private ArrayList<ActionBar.b> s = new ArrayList<>();
    private boolean t;
    private int u = 0;
    boolean v = true;
    boolean w;
    boolean x;
    private boolean y;
    private boolean z = true;

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public class a extends b implements j.a {

        /* renamed from: c  reason: collision with root package name */
        private final Context f288c;

        /* renamed from: d  reason: collision with root package name */
        private final j f289d;
        private b.a e;
        private WeakReference<View> f;

        public a(Context context, b.a aVar) {
            this.f288c = context;
            this.e = aVar;
            j jVar = new j(context);
            jVar.c(1);
            this.f289d = jVar;
            this.f289d.a((j.a) this);
        }

        public void a() {
            G g2 = G.this;
            if (g2.o == this) {
                if (!G.a(g2.w, g2.x, false)) {
                    G g3 = G.this;
                    g3.p = this;
                    g3.q = this.e;
                } else {
                    this.e.a(this);
                }
                this.e = null;
                G.this.e(false);
                G.this.i.a();
                G.this.h.i().sendAccessibilityEvent(32);
                G g4 = G.this;
                g4.f.setHideOnContentScrollEnabled(g4.C);
                G.this.o = null;
            }
        }

        public void a(int i) {
            a((CharSequence) G.this.f286c.getResources().getString(i));
        }

        public void a(View view) {
            G.this.i.setCustomView(view);
            this.f = new WeakReference<>(view);
        }

        public void a(j jVar) {
            if (this.e != null) {
                i();
                G.this.i.d();
            }
        }

        public void a(CharSequence charSequence) {
            G.this.i.setSubtitle(charSequence);
        }

        public void a(boolean z) {
            super.a(z);
            G.this.i.setTitleOptional(z);
        }

        public boolean a(j jVar, MenuItem menuItem) {
            b.a aVar = this.e;
            if (aVar != null) {
                return aVar.a((b) this, menuItem);
            }
            return false;
        }

        public View b() {
            WeakReference<View> weakReference = this.f;
            if (weakReference != null) {
                return (View) weakReference.get();
            }
            return null;
        }

        public void b(int i) {
            b((CharSequence) G.this.f286c.getResources().getString(i));
        }

        public void b(CharSequence charSequence) {
            G.this.i.setTitle(charSequence);
        }

        public Menu c() {
            return this.f289d;
        }

        public MenuInflater d() {
            return new g(this.f288c);
        }

        public CharSequence e() {
            return G.this.i.getSubtitle();
        }

        public CharSequence g() {
            return G.this.i.getTitle();
        }

        public void i() {
            if (G.this.o == this) {
                this.f289d.s();
                try {
                    this.e.b(this, this.f289d);
                } finally {
                    this.f289d.r();
                }
            }
        }

        public boolean j() {
            return G.this.i.b();
        }

        public boolean k() {
            this.f289d.s();
            try {
                return this.e.a((b) this, (Menu) this.f289d);
            } finally {
                this.f289d.r();
            }
        }
    }

    public G(Activity activity, boolean z2) {
        this.e = activity;
        View decorView = activity.getWindow().getDecorView();
        b(decorView);
        if (!z2) {
            this.j = decorView.findViewById(16908290);
        }
    }

    public G(Dialog dialog) {
        b(dialog.getWindow().getDecorView());
    }

    private M a(View view) {
        if (view instanceof M) {
            return (M) view;
        }
        if (view instanceof Toolbar) {
            return ((Toolbar) view).getWrapper();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Can't make a decor toolbar out of ");
        sb.append(view != null ? view.getClass().getSimpleName() : "null");
        throw new IllegalStateException(sb.toString());
    }

    static boolean a(boolean z2, boolean z3, boolean z4) {
        if (z4) {
            return true;
        }
        return !z2 && !z3;
    }

    private void b(View view) {
        this.f = (ActionBarOverlayLayout) view.findViewById(f.decor_content_parent);
        ActionBarOverlayLayout actionBarOverlayLayout = this.f;
        if (actionBarOverlayLayout != null) {
            actionBarOverlayLayout.setActionBarVisibilityCallback(this);
        }
        this.h = a(view.findViewById(f.action_bar));
        this.i = (ActionBarContextView) view.findViewById(f.action_context_bar);
        this.g = (ActionBarContainer) view.findViewById(f.action_bar_container);
        M m2 = this.h;
        if (m2 == null || this.i == null || this.g == null) {
            throw new IllegalStateException(G.class.getSimpleName() + " can only be used with a compatible window decor layout");
        }
        this.f286c = m2.j();
        boolean z2 = (this.h.m() & 4) != 0;
        if (z2) {
            this.n = true;
        }
        a.a.d.a a2 = a.a.d.a.a(this.f286c);
        j(a2.a() || z2);
        k(a2.f());
        TypedArray obtainStyledAttributes = this.f286c.obtainStyledAttributes((AttributeSet) null, a.a.j.ActionBar, a.a.a.actionBarStyle, 0);
        if (obtainStyledAttributes.getBoolean(a.a.j.ActionBar_hideOnContentScroll, false)) {
            i(true);
        }
        int dimensionPixelSize = obtainStyledAttributes.getDimensionPixelSize(a.a.j.ActionBar_elevation, 0);
        if (dimensionPixelSize != 0) {
            a((float) dimensionPixelSize);
        }
        obtainStyledAttributes.recycle();
    }

    private void k(boolean z2) {
        this.t = z2;
        if (!this.t) {
            this.h.a((C0090ba) null);
            this.g.setTabContainer(this.k);
        } else {
            this.g.setTabContainer((C0090ba) null);
            this.h.a(this.k);
        }
        boolean z3 = true;
        boolean z4 = m() == 2;
        C0090ba baVar = this.k;
        if (baVar != null) {
            if (z4) {
                baVar.setVisibility(0);
                ActionBarOverlayLayout actionBarOverlayLayout = this.f;
                if (actionBarOverlayLayout != null) {
                    ViewCompat.v(actionBarOverlayLayout);
                }
            } else {
                baVar.setVisibility(8);
            }
        }
        this.h.b(!this.t && z4);
        ActionBarOverlayLayout actionBarOverlayLayout2 = this.f;
        if (this.t || !z4) {
            z3 = false;
        }
        actionBarOverlayLayout2.setHasNonEmbeddedTabs(z3);
    }

    private void l(boolean z2) {
        if (a(this.w, this.x, this.y)) {
            if (!this.z) {
                this.z = true;
                g(z2);
            }
        } else if (this.z) {
            this.z = false;
            f(z2);
        }
    }

    private void n() {
        if (this.y) {
            this.y = false;
            ActionBarOverlayLayout actionBarOverlayLayout = this.f;
            if (actionBarOverlayLayout != null) {
                actionBarOverlayLayout.setShowingForActionMode(false);
            }
            l(false);
        }
    }

    private boolean o() {
        return ViewCompat.s(this.g);
    }

    private void p() {
        if (!this.y) {
            this.y = true;
            ActionBarOverlayLayout actionBarOverlayLayout = this.f;
            if (actionBarOverlayLayout != null) {
                actionBarOverlayLayout.setShowingForActionMode(true);
            }
            l(false);
        }
    }

    public b a(b.a aVar) {
        a aVar2 = this.o;
        if (aVar2 != null) {
            aVar2.a();
        }
        this.f.setHideOnContentScrollEnabled(false);
        this.i.c();
        a aVar3 = new a(this.i.getContext(), aVar);
        if (!aVar3.k()) {
            return null;
        }
        this.o = aVar3;
        aVar3.i();
        this.i.a(aVar3);
        e(true);
        this.i.sendAccessibilityEvent(32);
        return aVar3;
    }

    public void a() {
        if (this.x) {
            this.x = false;
            l(true);
        }
    }

    public void a(float f2) {
        ViewCompat.a((View) this.g, f2);
    }

    public void a(int i2) {
        this.u = i2;
    }

    public void a(int i2, int i3) {
        int m2 = this.h.m();
        if ((i3 & 4) != 0) {
            this.n = true;
        }
        this.h.a((i2 & i3) | ((~i3) & m2));
    }

    public void a(Configuration configuration) {
        k(a.a.d.a.a(this.f286c).f());
    }

    public void a(CharSequence charSequence) {
        this.h.setWindowTitle(charSequence);
    }

    public void a(boolean z2) {
        this.v = z2;
    }

    public boolean a(int i2, KeyEvent keyEvent) {
        Menu c2;
        a aVar = this.o;
        if (aVar == null || (c2 = aVar.c()) == null) {
            return false;
        }
        boolean z2 = true;
        if (KeyCharacterMap.load(keyEvent != null ? keyEvent.getDeviceId() : -1).getKeyboardType() == 1) {
            z2 = false;
        }
        c2.setQwertyMode(z2);
        return c2.performShortcut(i2, keyEvent, 0);
    }

    public void b() {
    }

    public void b(boolean z2) {
        if (z2 != this.r) {
            this.r = z2;
            int size = this.s.size();
            for (int i2 = 0; i2 < size; i2++) {
                this.s.get(i2).onMenuVisibilityChanged(z2);
            }
        }
    }

    public void c() {
        if (!this.x) {
            this.x = true;
            l(true);
        }
    }

    public void c(boolean z2) {
        if (!this.n) {
            h(z2);
        }
    }

    public void d() {
        i iVar = this.A;
        if (iVar != null) {
            iVar.a();
            this.A = null;
        }
    }

    public void d(boolean z2) {
        i iVar;
        this.B = z2;
        if (!z2 && (iVar = this.A) != null) {
            iVar.a();
        }
    }

    public void e(boolean z2) {
        D d2;
        D d3;
        if (z2) {
            p();
        } else {
            n();
        }
        if (o()) {
            if (z2) {
                d2 = this.h.a(4, 100);
                d3 = this.i.a(0, 200);
            } else {
                d3 = this.h.a(0, 200);
                d2 = this.i.a(8, 100);
            }
            i iVar = new i();
            iVar.a(d2, d3);
            iVar.c();
        } else if (z2) {
            this.h.c(4);
            this.i.setVisibility(0);
        } else {
            this.h.c(0);
            this.i.setVisibility(8);
        }
    }

    public void f(boolean z2) {
        View view;
        i iVar = this.A;
        if (iVar != null) {
            iVar.a();
        }
        if (this.u != 0 || (!this.B && !z2)) {
            this.D.onAnimationEnd((View) null);
            return;
        }
        this.g.setAlpha(1.0f);
        this.g.setTransitioning(true);
        i iVar2 = new i();
        float f2 = (float) (-this.g.getHeight());
        if (z2) {
            int[] iArr = {0, 0};
            this.g.getLocationInWindow(iArr);
            f2 -= (float) iArr[1];
        }
        D a2 = ViewCompat.a(this.g);
        a2.b(f2);
        a2.a(this.F);
        iVar2.a(a2);
        if (this.v && (view = this.j) != null) {
            D a3 = ViewCompat.a(view);
            a3.b(f2);
            iVar2.a(a3);
        }
        iVar2.a(f284a);
        iVar2.a(250);
        iVar2.a(this.D);
        this.A = iVar2;
        iVar2.c();
    }

    public boolean f() {
        M m2 = this.h;
        if (m2 == null || !m2.g()) {
            return false;
        }
        this.h.collapseActionView();
        return true;
    }

    public int g() {
        return this.h.m();
    }

    public void g(boolean z2) {
        View view;
        View view2;
        i iVar = this.A;
        if (iVar != null) {
            iVar.a();
        }
        this.g.setVisibility(0);
        if (this.u != 0 || (!this.B && !z2)) {
            this.g.setAlpha(1.0f);
            this.g.setTranslationY(0.0f);
            if (this.v && (view = this.j) != null) {
                view.setTranslationY(0.0f);
            }
            this.E.onAnimationEnd((View) null);
        } else {
            this.g.setTranslationY(0.0f);
            float f2 = (float) (-this.g.getHeight());
            if (z2) {
                int[] iArr = {0, 0};
                this.g.getLocationInWindow(iArr);
                f2 -= (float) iArr[1];
            }
            this.g.setTranslationY(f2);
            i iVar2 = new i();
            D a2 = ViewCompat.a(this.g);
            a2.b(0.0f);
            a2.a(this.F);
            iVar2.a(a2);
            if (this.v && (view2 = this.j) != null) {
                view2.setTranslationY(f2);
                D a3 = ViewCompat.a(this.j);
                a3.b(0.0f);
                iVar2.a(a3);
            }
            iVar2.a(f285b);
            iVar2.a(250);
            iVar2.a(this.E);
            this.A = iVar2;
            iVar2.c();
        }
        ActionBarOverlayLayout actionBarOverlayLayout = this.f;
        if (actionBarOverlayLayout != null) {
            ViewCompat.v(actionBarOverlayLayout);
        }
    }

    public Context h() {
        if (this.f287d == null) {
            TypedValue typedValue = new TypedValue();
            this.f286c.getTheme().resolveAttribute(a.a.a.actionBarWidgetTheme, typedValue, true);
            int i2 = typedValue.resourceId;
            if (i2 != 0) {
                this.f287d = new ContextThemeWrapper(this.f286c, i2);
            } else {
                this.f287d = this.f286c;
            }
        }
        return this.f287d;
    }

    public void h(boolean z2) {
        a(z2 ? 4 : 0, 4);
    }

    public void i(boolean z2) {
        if (!z2 || this.f.i()) {
            this.C = z2;
            this.f.setHideOnContentScrollEnabled(z2);
            return;
        }
        throw new IllegalStateException("Action bar must be in overlay mode (Window.FEATURE_OVERLAY_ACTION_BAR) to enable hide on content scroll");
    }

    public void j(boolean z2) {
        this.h.a(z2);
    }

    /* access modifiers changed from: package-private */
    public void l() {
        b.a aVar = this.q;
        if (aVar != null) {
            aVar.a(this.p);
            this.p = null;
            this.q = null;
        }
    }

    public int m() {
        return this.h.h();
    }
}
