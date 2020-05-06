package androidx.appcompat.view.menu;

import a.a.d;
import a.a.g;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.appcompat.view.menu.s;
import androidx.appcompat.widget.V;
import androidx.appcompat.widget.W;
import androidx.core.view.C0125c;
import androidx.core.view.ViewCompat;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

final class CascadingMenuPopup extends p implements s, View.OnKeyListener, PopupWindow.OnDismissListener {

    /* renamed from: b  reason: collision with root package name */
    private static final int f347b = g.abc_cascading_menu_item_layout;
    private PopupWindow.OnDismissListener A;
    boolean B;

    /* renamed from: c  reason: collision with root package name */
    private final Context f348c;

    /* renamed from: d  reason: collision with root package name */
    private final int f349d;
    private final int e;
    private final int f;
    private final boolean g;
    final Handler h;
    private final List<j> i = new ArrayList();
    final List<a> j = new ArrayList();
    final ViewTreeObserver.OnGlobalLayoutListener k = new d(this);
    private final View.OnAttachStateChangeListener l = new e(this);
    private final V m = new g(this);
    private int n = 0;
    private int o = 0;
    private View p;
    View q;
    private int r;
    private boolean s;
    private boolean t;
    private int u;
    private int v;
    private boolean w;
    private boolean x;
    private s.a y;
    ViewTreeObserver z;

    @Retention(RetentionPolicy.SOURCE)
    public @interface HorizPosition {
    }

    private static class a {

        /* renamed from: a  reason: collision with root package name */
        public final W f350a;

        /* renamed from: b  reason: collision with root package name */
        public final j f351b;

        /* renamed from: c  reason: collision with root package name */
        public final int f352c;

        public a(@NonNull W w, @NonNull j jVar, int i) {
            this.f350a = w;
            this.f351b = jVar;
            this.f352c = i;
        }

        public ListView a() {
            return this.f350a.c();
        }
    }

    public CascadingMenuPopup(@NonNull Context context, @NonNull View view, @AttrRes int i2, @StyleRes int i3, boolean z2) {
        this.f348c = context;
        this.p = view;
        this.e = i2;
        this.f = i3;
        this.g = z2;
        this.w = false;
        this.r = g();
        Resources resources = context.getResources();
        this.f349d = Math.max(resources.getDisplayMetrics().widthPixels / 2, resources.getDimensionPixelSize(d.abc_config_prefDialogWidth));
        this.h = new Handler();
    }

    private MenuItem a(@NonNull j jVar, @NonNull j jVar2) {
        int size = jVar.size();
        for (int i2 = 0; i2 < size; i2++) {
            MenuItem item = jVar.getItem(i2);
            if (item.hasSubMenu() && jVar2 == item.getSubMenu()) {
                return item;
            }
        }
        return null;
    }

    @Nullable
    private View a(@NonNull a aVar, @NonNull j jVar) {
        int i2;
        i iVar;
        int firstVisiblePosition;
        MenuItem a2 = a(aVar.f351b, jVar);
        if (a2 == null) {
            return null;
        }
        ListView a3 = aVar.a();
        ListAdapter adapter = a3.getAdapter();
        int i3 = 0;
        if (adapter instanceof HeaderViewListAdapter) {
            HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) adapter;
            i2 = headerViewListAdapter.getHeadersCount();
            iVar = (i) headerViewListAdapter.getWrappedAdapter();
        } else {
            iVar = (i) adapter;
            i2 = 0;
        }
        int count = iVar.getCount();
        while (true) {
            if (i3 >= count) {
                i3 = -1;
                break;
            } else if (a2 == iVar.getItem(i3)) {
                break;
            } else {
                i3++;
            }
        }
        if (i3 != -1 && (firstVisiblePosition = (i3 + i2) - a3.getFirstVisiblePosition()) >= 0 && firstVisiblePosition < a3.getChildCount()) {
            return a3.getChildAt(firstVisiblePosition);
        }
        return null;
    }

    private int c(@NonNull j jVar) {
        int size = this.j.size();
        for (int i2 = 0; i2 < size; i2++) {
            if (jVar == this.j.get(i2).f351b) {
                return i2;
            }
        }
        return -1;
    }

    private int d(int i2) {
        List<a> list = this.j;
        ListView a2 = list.get(list.size() - 1).a();
        int[] iArr = new int[2];
        a2.getLocationOnScreen(iArr);
        Rect rect = new Rect();
        this.q.getWindowVisibleDisplayFrame(rect);
        return this.r == 1 ? (iArr[0] + a2.getWidth()) + i2 > rect.right ? 0 : 1 : iArr[0] - i2 < 0 ? 1 : 0;
    }

    private void d(@NonNull j jVar) {
        View view;
        a aVar;
        int i2;
        int i3;
        int i4;
        LayoutInflater from = LayoutInflater.from(this.f348c);
        i iVar = new i(jVar, from, this.g, f347b);
        if (!isShowing() && this.w) {
            iVar.a(true);
        } else if (isShowing()) {
            iVar.a(p.b(jVar));
        }
        int a2 = p.a(iVar, (ViewGroup) null, this.f348c, this.f349d);
        W f2 = f();
        f2.setAdapter(iVar);
        f2.b(a2);
        f2.c(this.o);
        if (this.j.size() > 0) {
            List<a> list = this.j;
            aVar = list.get(list.size() - 1);
            view = a(aVar, jVar);
        } else {
            aVar = null;
            view = null;
        }
        if (view != null) {
            f2.c(false);
            f2.a((Object) null);
            int d2 = d(a2);
            boolean z2 = d2 == 1;
            this.r = d2;
            if (Build.VERSION.SDK_INT >= 26) {
                f2.a(view);
                i3 = 0;
                i2 = 0;
            } else {
                int[] iArr = new int[2];
                this.p.getLocationOnScreen(iArr);
                int[] iArr2 = new int[2];
                view.getLocationOnScreen(iArr2);
                if ((this.o & 7) == 5) {
                    iArr[0] = iArr[0] + this.p.getWidth();
                    iArr2[0] = iArr2[0] + view.getWidth();
                }
                i2 = iArr2[0] - iArr[0];
                i3 = iArr2[1] - iArr[1];
            }
            if ((this.o & 5) != 5) {
                if (z2) {
                    a2 = view.getWidth();
                }
                i4 = i2 - a2;
                f2.setHorizontalOffset(i4);
                f2.b(true);
                f2.setVerticalOffset(i3);
            } else if (!z2) {
                a2 = view.getWidth();
                i4 = i2 - a2;
                f2.setHorizontalOffset(i4);
                f2.b(true);
                f2.setVerticalOffset(i3);
            }
            i4 = i2 + a2;
            f2.setHorizontalOffset(i4);
            f2.b(true);
            f2.setVerticalOffset(i3);
        } else {
            if (this.s) {
                f2.setHorizontalOffset(this.u);
            }
            if (this.t) {
                f2.setVerticalOffset(this.v);
            }
            f2.a(e());
        }
        this.j.add(new a(f2, jVar, this.r));
        f2.b();
        ListView c2 = f2.c();
        c2.setOnKeyListener(this);
        if (aVar == null && this.x && jVar.h() != null) {
            FrameLayout frameLayout = (FrameLayout) from.inflate(g.abc_popup_menu_header_item_layout, c2, false);
            frameLayout.setEnabled(false);
            ((TextView) frameLayout.findViewById(16908310)).setText(jVar.h());
            c2.addHeaderView(frameLayout, (Object) null, false);
            f2.b();
        }
    }

    private W f() {
        W w2 = new W(this.f348c, (AttributeSet) null, this.e, this.f);
        w2.a(this.m);
        w2.a((AdapterView.OnItemClickListener) this);
        w2.a((PopupWindow.OnDismissListener) this);
        w2.a(this.p);
        w2.c(this.o);
        w2.a(true);
        w2.d(2);
        return w2;
    }

    private int g() {
        return ViewCompat.j(this.p) == 1 ? 0 : 1;
    }

    public void a(int i2) {
        if (this.n != i2) {
            this.n = i2;
            this.o = C0125c.a(i2, ViewCompat.j(this.p));
        }
    }

    public void a(@NonNull View view) {
        if (this.p != view) {
            this.p = view;
            this.o = C0125c.a(this.n, ViewCompat.j(this.p));
        }
    }

    public void a(PopupWindow.OnDismissListener onDismissListener) {
        this.A = onDismissListener;
    }

    public void a(j jVar) {
        jVar.a((s) this, this.f348c);
        if (isShowing()) {
            d(jVar);
        } else {
            this.i.add(jVar);
        }
    }

    public void a(j jVar, boolean z2) {
        int c2 = c(jVar);
        if (c2 >= 0) {
            int i2 = c2 + 1;
            if (i2 < this.j.size()) {
                this.j.get(i2).f351b.a(false);
            }
            a remove = this.j.remove(c2);
            remove.f351b.b((s) this);
            if (this.B) {
                remove.f350a.b((Object) null);
                remove.f350a.a(0);
            }
            remove.f350a.dismiss();
            int size = this.j.size();
            this.r = size > 0 ? this.j.get(size - 1).f352c : g();
            if (size == 0) {
                dismiss();
                s.a aVar = this.y;
                if (aVar != null) {
                    aVar.a(jVar, true);
                }
                ViewTreeObserver viewTreeObserver = this.z;
                if (viewTreeObserver != null) {
                    if (viewTreeObserver.isAlive()) {
                        this.z.removeGlobalOnLayoutListener(this.k);
                    }
                    this.z = null;
                }
                this.q.removeOnAttachStateChangeListener(this.l);
                this.A.onDismiss();
            } else if (z2) {
                this.j.get(0).f351b.a(false);
            }
        }
    }

    public void a(s.a aVar) {
        this.y = aVar;
    }

    public void a(boolean z2) {
        for (a a2 : this.j) {
            p.a(a2.a().getAdapter()).notifyDataSetChanged();
        }
    }

    public boolean a() {
        return false;
    }

    public boolean a(z zVar) {
        for (a next : this.j) {
            if (zVar == next.f351b) {
                next.a().requestFocus();
                return true;
            }
        }
        if (!zVar.hasVisibleItems()) {
            return false;
        }
        a((j) zVar);
        s.a aVar = this.y;
        if (aVar != null) {
            aVar.a(zVar);
        }
        return true;
    }

    public void b() {
        if (!isShowing()) {
            for (j d2 : this.i) {
                d(d2);
            }
            this.i.clear();
            this.q = this.p;
            if (this.q != null) {
                boolean z2 = this.z == null;
                this.z = this.q.getViewTreeObserver();
                if (z2) {
                    this.z.addOnGlobalLayoutListener(this.k);
                }
                this.q.addOnAttachStateChangeListener(this.l);
            }
        }
    }

    public void b(int i2) {
        this.s = true;
        this.u = i2;
    }

    public void b(boolean z2) {
        this.w = z2;
    }

    public ListView c() {
        if (this.j.isEmpty()) {
            return null;
        }
        List<a> list = this.j;
        return list.get(list.size() - 1).a();
    }

    public void c(int i2) {
        this.t = true;
        this.v = i2;
    }

    public void c(boolean z2) {
        this.x = z2;
    }

    /* access modifiers changed from: protected */
    public boolean d() {
        return false;
    }

    public void dismiss() {
        int size = this.j.size();
        if (size > 0) {
            a[] aVarArr = (a[]) this.j.toArray(new a[size]);
            for (int i2 = size - 1; i2 >= 0; i2--) {
                a aVar = aVarArr[i2];
                if (aVar.f350a.isShowing()) {
                    aVar.f350a.dismiss();
                }
            }
        }
    }

    public boolean isShowing() {
        return this.j.size() > 0 && this.j.get(0).f350a.isShowing();
    }

    public void onDismiss() {
        a aVar;
        int size = this.j.size();
        int i2 = 0;
        while (true) {
            if (i2 >= size) {
                aVar = null;
                break;
            }
            aVar = this.j.get(i2);
            if (!aVar.f350a.isShowing()) {
                break;
            }
            i2++;
        }
        if (aVar != null) {
            aVar.f351b.a(false);
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
