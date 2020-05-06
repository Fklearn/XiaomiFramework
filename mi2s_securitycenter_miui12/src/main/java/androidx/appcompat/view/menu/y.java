package androidx.appcompat.view.menu;

import a.a.d;
import a.a.g;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.appcompat.view.menu.s;
import androidx.appcompat.widget.W;
import androidx.core.view.ViewCompat;

final class y extends p implements PopupWindow.OnDismissListener, AdapterView.OnItemClickListener, s, View.OnKeyListener {

    /* renamed from: b  reason: collision with root package name */
    private static final int f417b = g.abc_popup_menu_item_layout;

    /* renamed from: c  reason: collision with root package name */
    private final Context f418c;

    /* renamed from: d  reason: collision with root package name */
    private final j f419d;
    private final i e;
    private final boolean f;
    private final int g;
    private final int h;
    private final int i;
    final W j;
    final ViewTreeObserver.OnGlobalLayoutListener k = new w(this);
    private final View.OnAttachStateChangeListener l = new x(this);
    private PopupWindow.OnDismissListener m;
    private View n;
    View o;
    private s.a p;
    ViewTreeObserver q;
    private boolean r;
    private boolean s;
    private int t;
    private int u = 0;
    private boolean v;

    public y(Context context, j jVar, View view, int i2, int i3, boolean z) {
        this.f418c = context;
        this.f419d = jVar;
        this.f = z;
        this.e = new i(jVar, LayoutInflater.from(context), this.f, f417b);
        this.h = i2;
        this.i = i3;
        Resources resources = context.getResources();
        this.g = Math.max(resources.getDisplayMetrics().widthPixels / 2, resources.getDimensionPixelSize(d.abc_config_prefDialogWidth));
        this.n = view;
        this.j = new W(this.f418c, (AttributeSet) null, this.h, this.i);
        jVar.a((s) this, context);
    }

    private boolean f() {
        View view;
        if (isShowing()) {
            return true;
        }
        if (this.r || (view = this.n) == null) {
            return false;
        }
        this.o = view;
        this.j.a((PopupWindow.OnDismissListener) this);
        this.j.a((AdapterView.OnItemClickListener) this);
        this.j.a(true);
        View view2 = this.o;
        boolean z = this.q == null;
        this.q = view2.getViewTreeObserver();
        if (z) {
            this.q.addOnGlobalLayoutListener(this.k);
        }
        view2.addOnAttachStateChangeListener(this.l);
        this.j.a(view2);
        this.j.c(this.u);
        if (!this.s) {
            this.t = p.a(this.e, (ViewGroup) null, this.f418c, this.g);
            this.s = true;
        }
        this.j.b(this.t);
        this.j.d(2);
        this.j.a(e());
        this.j.b();
        ListView c2 = this.j.c();
        c2.setOnKeyListener(this);
        if (this.v && this.f419d.h() != null) {
            FrameLayout frameLayout = (FrameLayout) LayoutInflater.from(this.f418c).inflate(g.abc_popup_menu_header_item_layout, c2, false);
            TextView textView = (TextView) frameLayout.findViewById(16908310);
            if (textView != null) {
                textView.setText(this.f419d.h());
            }
            frameLayout.setEnabled(false);
            c2.addHeaderView(frameLayout, (Object) null, false);
        }
        this.j.setAdapter(this.e);
        this.j.b();
        return true;
    }

    public void a(int i2) {
        this.u = i2;
    }

    public void a(View view) {
        this.n = view;
    }

    public void a(PopupWindow.OnDismissListener onDismissListener) {
        this.m = onDismissListener;
    }

    public void a(j jVar) {
    }

    public void a(j jVar, boolean z) {
        if (jVar == this.f419d) {
            dismiss();
            s.a aVar = this.p;
            if (aVar != null) {
                aVar.a(jVar, z);
            }
        }
    }

    public void a(s.a aVar) {
        this.p = aVar;
    }

    public void a(boolean z) {
        this.s = false;
        i iVar = this.e;
        if (iVar != null) {
            iVar.notifyDataSetChanged();
        }
    }

    public boolean a() {
        return false;
    }

    public boolean a(z zVar) {
        if (zVar.hasVisibleItems()) {
            r rVar = new r(this.f418c, zVar, this.o, this.f, this.h, this.i);
            rVar.a(this.p);
            rVar.a(p.b((j) zVar));
            rVar.a(this.m);
            this.m = null;
            this.f419d.a(false);
            int horizontalOffset = this.j.getHorizontalOffset();
            int verticalOffset = this.j.getVerticalOffset();
            if ((Gravity.getAbsoluteGravity(this.u, ViewCompat.j(this.n)) & 7) == 5) {
                horizontalOffset += this.n.getWidth();
            }
            if (rVar.a(horizontalOffset, verticalOffset)) {
                s.a aVar = this.p;
                if (aVar == null) {
                    return true;
                }
                aVar.a(zVar);
                return true;
            }
        }
        return false;
    }

    public void b() {
        if (!f()) {
            throw new IllegalStateException("StandardMenuPopup cannot be used without an anchor");
        }
    }

    public void b(int i2) {
        this.j.setHorizontalOffset(i2);
    }

    public void b(boolean z) {
        this.e.a(z);
    }

    public ListView c() {
        return this.j.c();
    }

    public void c(int i2) {
        this.j.setVerticalOffset(i2);
    }

    public void c(boolean z) {
        this.v = z;
    }

    public void dismiss() {
        if (isShowing()) {
            this.j.dismiss();
        }
    }

    public boolean isShowing() {
        return !this.r && this.j.isShowing();
    }

    public void onDismiss() {
        this.r = true;
        this.f419d.close();
        ViewTreeObserver viewTreeObserver = this.q;
        if (viewTreeObserver != null) {
            if (!viewTreeObserver.isAlive()) {
                this.q = this.o.getViewTreeObserver();
            }
            this.q.removeGlobalOnLayoutListener(this.k);
            this.q = null;
        }
        this.o.removeOnAttachStateChangeListener(this.l);
        PopupWindow.OnDismissListener onDismissListener = this.m;
        if (onDismissListener != null) {
            onDismissListener.onDismiss();
        }
    }

    public boolean onKey(View view, int i2, KeyEvent keyEvent) {
        if (keyEvent.getAction() != 1 || i2 != 82) {
            return false;
        }
        dismiss();
        return true;
    }
}
