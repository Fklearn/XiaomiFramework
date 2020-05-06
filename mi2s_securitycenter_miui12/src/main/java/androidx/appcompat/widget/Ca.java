package androidx.appcompat.widget;

import a.a.a;
import a.a.e;
import a.a.f;
import a.a.h;
import a.a.j;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import androidx.annotation.RestrictTo;
import androidx.appcompat.view.menu.s;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.D;
import androidx.core.view.E;
import androidx.core.view.ViewCompat;

@RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
public class Ca implements M {

    /* renamed from: a  reason: collision with root package name */
    Toolbar f466a;

    /* renamed from: b  reason: collision with root package name */
    private int f467b;

    /* renamed from: c  reason: collision with root package name */
    private View f468c;

    /* renamed from: d  reason: collision with root package name */
    private View f469d;
    private Drawable e;
    private Drawable f;
    private Drawable g;
    private boolean h;
    CharSequence i;
    private CharSequence j;
    private CharSequence k;
    Window.Callback l;
    boolean m;
    private ActionMenuPresenter n;
    private int o;
    private int p;
    private Drawable q;

    public Ca(Toolbar toolbar, boolean z) {
        this(toolbar, z, h.abc_action_bar_up_description, e.abc_ic_ab_back_material);
    }

    public Ca(Toolbar toolbar, boolean z, int i2, int i3) {
        Drawable drawable;
        this.o = 0;
        this.p = 0;
        this.f466a = toolbar;
        this.i = toolbar.getTitle();
        this.j = toolbar.getSubtitle();
        this.h = this.i != null;
        this.g = toolbar.getNavigationIcon();
        va a2 = va.a(toolbar.getContext(), (AttributeSet) null, j.ActionBar, a.actionBarStyle, 0);
        this.q = a2.b(j.ActionBar_homeAsUpIndicator);
        if (z) {
            CharSequence e2 = a2.e(j.ActionBar_title);
            if (!TextUtils.isEmpty(e2)) {
                c(e2);
            }
            CharSequence e3 = a2.e(j.ActionBar_subtitle);
            if (!TextUtils.isEmpty(e3)) {
                b(e3);
            }
            Drawable b2 = a2.b(j.ActionBar_logo);
            if (b2 != null) {
                a(b2);
            }
            Drawable b3 = a2.b(j.ActionBar_icon);
            if (b3 != null) {
                setIcon(b3);
            }
            if (this.g == null && (drawable = this.q) != null) {
                b(drawable);
            }
            a(a2.d(j.ActionBar_displayOptions, 0));
            int g2 = a2.g(j.ActionBar_customNavigationLayout, 0);
            if (g2 != 0) {
                a(LayoutInflater.from(this.f466a.getContext()).inflate(g2, this.f466a, false));
                a(this.f467b | 16);
            }
            int f2 = a2.f(j.ActionBar_height, 0);
            if (f2 > 0) {
                ViewGroup.LayoutParams layoutParams = this.f466a.getLayoutParams();
                layoutParams.height = f2;
                this.f466a.setLayoutParams(layoutParams);
            }
            int b4 = a2.b(j.ActionBar_contentInsetStart, -1);
            int b5 = a2.b(j.ActionBar_contentInsetEnd, -1);
            if (b4 >= 0 || b5 >= 0) {
                this.f466a.a(Math.max(b4, 0), Math.max(b5, 0));
            }
            int g3 = a2.g(j.ActionBar_titleTextStyle, 0);
            if (g3 != 0) {
                Toolbar toolbar2 = this.f466a;
                toolbar2.b(toolbar2.getContext(), g3);
            }
            int g4 = a2.g(j.ActionBar_subtitleTextStyle, 0);
            if (g4 != 0) {
                Toolbar toolbar3 = this.f466a;
                toolbar3.a(toolbar3.getContext(), g4);
            }
            int g5 = a2.g(j.ActionBar_popupTheme, 0);
            if (g5 != 0) {
                this.f466a.setPopupTheme(g5);
            }
        } else {
            this.f467b = o();
        }
        a2.b();
        d(i2);
        this.k = this.f466a.getNavigationContentDescription();
        this.f466a.setNavigationOnClickListener(new Aa(this));
    }

    private void d(CharSequence charSequence) {
        this.i = charSequence;
        if ((this.f467b & 8) != 0) {
            this.f466a.setTitle(charSequence);
        }
    }

    private int o() {
        if (this.f466a.getNavigationIcon() == null) {
            return 11;
        }
        this.q = this.f466a.getNavigationIcon();
        return 15;
    }

    private void p() {
        if ((this.f467b & 4) == 0) {
            return;
        }
        if (TextUtils.isEmpty(this.k)) {
            this.f466a.setNavigationContentDescription(this.p);
        } else {
            this.f466a.setNavigationContentDescription(this.k);
        }
    }

    private void q() {
        Drawable drawable;
        Toolbar toolbar;
        if ((this.f467b & 4) != 0) {
            toolbar = this.f466a;
            drawable = this.g;
            if (drawable == null) {
                drawable = this.q;
            }
        } else {
            toolbar = this.f466a;
            drawable = null;
        }
        toolbar.setNavigationIcon(drawable);
    }

    private void r() {
        Drawable drawable;
        int i2 = this.f467b;
        if ((i2 & 2) == 0) {
            drawable = null;
        } else if ((i2 & 1) == 0 || (drawable = this.f) == null) {
            drawable = this.e;
        }
        this.f466a.setLogo(drawable);
    }

    public D a(int i2, long j2) {
        D a2 = ViewCompat.a(this.f466a);
        a2.a(i2 == 0 ? 1.0f : 0.0f);
        a2.a(j2);
        a2.a((E) new Ba(this, i2));
        return a2;
    }

    public void a(int i2) {
        View view;
        CharSequence charSequence;
        Toolbar toolbar;
        int i3 = this.f467b ^ i2;
        this.f467b = i2;
        if (i3 != 0) {
            if ((i3 & 4) != 0) {
                if ((i2 & 4) != 0) {
                    p();
                }
                q();
            }
            if ((i3 & 3) != 0) {
                r();
            }
            if ((i3 & 8) != 0) {
                if ((i2 & 8) != 0) {
                    this.f466a.setTitle(this.i);
                    toolbar = this.f466a;
                    charSequence = this.j;
                } else {
                    charSequence = null;
                    this.f466a.setTitle((CharSequence) null);
                    toolbar = this.f466a;
                }
                toolbar.setSubtitle(charSequence);
            }
            if ((i3 & 16) != 0 && (view = this.f469d) != null) {
                if ((i2 & 16) != 0) {
                    this.f466a.addView(view);
                } else {
                    this.f466a.removeView(view);
                }
            }
        }
    }

    public void a(Drawable drawable) {
        this.f = drawable;
        r();
    }

    public void a(Menu menu, s.a aVar) {
        if (this.n == null) {
            this.n = new ActionMenuPresenter(this.f466a.getContext());
            this.n.a(f.action_menu_presenter);
        }
        this.n.a(aVar);
        this.f466a.a((androidx.appcompat.view.menu.j) menu, this.n);
    }

    public void a(View view) {
        View view2 = this.f469d;
        if (!(view2 == null || (this.f467b & 16) == 0)) {
            this.f466a.removeView(view2);
        }
        this.f469d = view;
        if (view != null && (this.f467b & 16) != 0) {
            this.f466a.addView(this.f469d);
        }
    }

    public void a(C0090ba baVar) {
        Toolbar toolbar;
        View view = this.f468c;
        if (view != null && view.getParent() == (toolbar = this.f466a)) {
            toolbar.removeView(this.f468c);
        }
        this.f468c = baVar;
        if (baVar != null && this.o == 2) {
            this.f466a.addView(this.f468c, 0);
            Toolbar.b bVar = (Toolbar.b) this.f468c.getLayoutParams();
            bVar.width = -2;
            bVar.height = -2;
            bVar.f230a = 8388691;
            baVar.setAllowCollapse(true);
        }
    }

    public void a(CharSequence charSequence) {
        this.k = charSequence;
        p();
    }

    public void a(boolean z) {
    }

    public boolean a() {
        return this.f466a.b();
    }

    public void b(int i2) {
        a(i2 != 0 ? a.a.a.a.a.b(j(), i2) : null);
    }

    public void b(Drawable drawable) {
        this.g = drawable;
        q();
    }

    public void b(CharSequence charSequence) {
        this.j = charSequence;
        if ((this.f467b & 8) != 0) {
            this.f466a.setSubtitle(charSequence);
        }
    }

    public void b(boolean z) {
        this.f466a.setCollapsible(z);
    }

    public boolean b() {
        return this.f466a.g();
    }

    public void c(int i2) {
        this.f466a.setVisibility(i2);
    }

    public void c(CharSequence charSequence) {
        this.h = true;
        d(charSequence);
    }

    public boolean c() {
        return this.f466a.k();
    }

    public void collapseActionView() {
        this.f466a.c();
    }

    public void d(int i2) {
        if (i2 != this.p) {
            this.p = i2;
            if (TextUtils.isEmpty(this.f466a.getNavigationContentDescription())) {
                e(this.p);
            }
        }
    }

    public boolean d() {
        return this.f466a.i();
    }

    public void e() {
        this.m = true;
    }

    public void e(int i2) {
        a((CharSequence) i2 == 0 ? null : j().getString(i2));
    }

    public boolean f() {
        return this.f466a.h();
    }

    public boolean g() {
        return this.f466a.f();
    }

    public CharSequence getTitle() {
        return this.f466a.getTitle();
    }

    public int h() {
        return this.o;
    }

    public ViewGroup i() {
        return this.f466a;
    }

    public Context j() {
        return this.f466a.getContext();
    }

    public void k() {
        Log.i("ToolbarWidgetWrapper", "Progress display unsupported");
    }

    public void l() {
        this.f466a.d();
    }

    public int m() {
        return this.f467b;
    }

    public void n() {
        Log.i("ToolbarWidgetWrapper", "Progress display unsupported");
    }

    public void setIcon(int i2) {
        setIcon(i2 != 0 ? a.a.a.a.a.b(j(), i2) : null);
    }

    public void setIcon(Drawable drawable) {
        this.e = drawable;
        r();
    }

    public void setWindowCallback(Window.Callback callback) {
        this.l = callback;
    }

    public void setWindowTitle(CharSequence charSequence) {
        if (!this.h) {
            d(charSequence);
        }
    }
}
