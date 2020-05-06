package androidx.appcompat.widget;

import a.a.g;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.appcompat.view.menu.j;
import androidx.appcompat.view.menu.n;
import androidx.appcompat.view.menu.r;
import androidx.appcompat.view.menu.s;
import androidx.appcompat.view.menu.t;
import androidx.appcompat.view.menu.v;
import androidx.appcompat.view.menu.z;
import androidx.appcompat.widget.ActionMenuView;
import androidx.core.view.C0124b;
import java.util.ArrayList;

class ActionMenuPresenter extends androidx.appcompat.view.menu.b implements C0124b.a {
    c A;
    private b B;
    final f C = new f();
    int D;
    d k;
    private Drawable l;
    private boolean m;
    private boolean n;
    private boolean o;
    private int p;
    private int q;
    private int r;
    private boolean s;
    private boolean t;
    private boolean u;
    private boolean v;
    private int w;
    private final SparseBooleanArray x = new SparseBooleanArray();
    e y;
    a z;

    @SuppressLint({"BanParcelableUsage"})
    private static class SavedState implements Parcelable {
        public static final Parcelable.Creator<SavedState> CREATOR = new C0101h();
        public int openSubMenuId;

        SavedState() {
        }

        SavedState(Parcel parcel) {
            this.openSubMenuId = parcel.readInt();
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.openSubMenuId);
        }
    }

    private class a extends r {
        public a(Context context, z zVar, View view) {
            super(context, zVar, view, false, a.a.a.actionOverflowMenuStyle);
            if (!((n) zVar.getItem()).g()) {
                View view2 = ActionMenuPresenter.this.k;
                a(view2 == null ? (View) ActionMenuPresenter.this.i : view2);
            }
            a((s.a) ActionMenuPresenter.this.C);
        }

        /* access modifiers changed from: protected */
        public void d() {
            ActionMenuPresenter actionMenuPresenter = ActionMenuPresenter.this;
            actionMenuPresenter.z = null;
            actionMenuPresenter.D = 0;
            super.d();
        }
    }

    private class b extends ActionMenuItemView.b {
        b() {
        }

        public v a() {
            a aVar = ActionMenuPresenter.this.z;
            if (aVar != null) {
                return aVar.b();
            }
            return null;
        }
    }

    private class c implements Runnable {

        /* renamed from: a  reason: collision with root package name */
        private e f432a;

        public c(e eVar) {
            this.f432a = eVar;
        }

        public void run() {
            if (ActionMenuPresenter.this.f366c != null) {
                ActionMenuPresenter.this.f366c.a();
            }
            View view = (View) ActionMenuPresenter.this.i;
            if (!(view == null || view.getWindowToken() == null || !this.f432a.f())) {
                ActionMenuPresenter.this.y = this.f432a;
            }
            ActionMenuPresenter.this.A = null;
        }
    }

    private class d extends AppCompatImageView implements ActionMenuView.a {
        public d(Context context) {
            super(context, (AttributeSet) null, a.a.a.actionOverflowButtonStyle);
            setClickable(true);
            setFocusable(true);
            setVisibility(0);
            setEnabled(true);
            Da.a(this, getContentDescription());
            setOnTouchListener(new C0099g(this, this, ActionMenuPresenter.this));
        }

        public boolean a() {
            return false;
        }

        public boolean b() {
            return false;
        }

        public boolean performClick() {
            if (super.performClick()) {
                return true;
            }
            playSoundEffect(0);
            ActionMenuPresenter.this.i();
            return true;
        }

        /* access modifiers changed from: protected */
        public boolean setFrame(int i, int i2, int i3, int i4) {
            boolean frame = super.setFrame(i, i2, i3, i4);
            Drawable drawable = getDrawable();
            Drawable background = getBackground();
            if (!(drawable == null || background == null)) {
                int width = getWidth();
                int height = getHeight();
                int max = Math.max(width, height) / 2;
                int paddingLeft = (width + (getPaddingLeft() - getPaddingRight())) / 2;
                int paddingTop = (height + (getPaddingTop() - getPaddingBottom())) / 2;
                androidx.core.graphics.drawable.a.a(background, paddingLeft - max, paddingTop - max, paddingLeft + max, paddingTop + max);
            }
            return frame;
        }
    }

    private class e extends r {
        public e(Context context, j jVar, View view, boolean z) {
            super(context, jVar, view, z, a.a.a.actionOverflowMenuStyle);
            a(8388613);
            a((s.a) ActionMenuPresenter.this.C);
        }

        /* access modifiers changed from: protected */
        public void d() {
            if (ActionMenuPresenter.this.f366c != null) {
                ActionMenuPresenter.this.f366c.close();
            }
            ActionMenuPresenter.this.y = null;
            super.d();
        }
    }

    private class f implements s.a {
        f() {
        }

        public void a(j jVar, boolean z) {
            if (jVar instanceof z) {
                jVar.m().a(false);
            }
            s.a b2 = ActionMenuPresenter.this.b();
            if (b2 != null) {
                b2.a(jVar, z);
            }
        }

        public boolean a(j jVar) {
            if (jVar == null) {
                return false;
            }
            ActionMenuPresenter.this.D = ((z) jVar).getItem().getItemId();
            s.a b2 = ActionMenuPresenter.this.b();
            if (b2 != null) {
                return b2.a(jVar);
            }
            return false;
        }
    }

    public ActionMenuPresenter(Context context) {
        super(context, g.abc_action_menu_layout, g.abc_action_menu_item_layout);
    }

    private View a(MenuItem menuItem) {
        ViewGroup viewGroup = (ViewGroup) this.i;
        if (viewGroup == null) {
            return null;
        }
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = viewGroup.getChildAt(i);
            if ((childAt instanceof t.a) && ((t.a) childAt).getItemData() == menuItem) {
                return childAt;
            }
        }
        return null;
    }

    public View a(n nVar, View view, ViewGroup viewGroup) {
        View actionView = nVar.getActionView();
        if (actionView == null || nVar.e()) {
            actionView = super.a(nVar, view, viewGroup);
        }
        actionView.setVisibility(nVar.isActionViewExpanded() ? 8 : 0);
        ActionMenuView actionMenuView = (ActionMenuView) viewGroup;
        ViewGroup.LayoutParams layoutParams = actionView.getLayoutParams();
        if (!actionMenuView.checkLayoutParams(layoutParams)) {
            actionView.setLayoutParams(actionMenuView.generateLayoutParams(layoutParams));
        }
        return actionView;
    }

    public void a(@NonNull Context context, @Nullable j jVar) {
        super.a(context, jVar);
        Resources resources = context.getResources();
        a.a.d.a a2 = a.a.d.a.a(context);
        if (!this.o) {
            this.n = a2.g();
        }
        if (!this.u) {
            this.p = a2.b();
        }
        if (!this.s) {
            this.r = a2.c();
        }
        int i = this.p;
        if (this.n) {
            if (this.k == null) {
                this.k = new d(this.f364a);
                if (this.m) {
                    this.k.setImageDrawable(this.l);
                    this.l = null;
                    this.m = false;
                }
                int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
                this.k.measure(makeMeasureSpec, makeMeasureSpec);
            }
            i -= this.k.getMeasuredWidth();
        } else {
            this.k = null;
        }
        this.q = i;
        this.w = (int) (resources.getDisplayMetrics().density * 56.0f);
    }

    public void a(Configuration configuration) {
        if (!this.s) {
            this.r = a.a.d.a.a(this.f365b).c();
        }
        j jVar = this.f366c;
        if (jVar != null) {
            jVar.b(true);
        }
    }

    public void a(Drawable drawable) {
        d dVar = this.k;
        if (dVar != null) {
            dVar.setImageDrawable(drawable);
            return;
        }
        this.m = true;
        this.l = drawable;
    }

    public void a(j jVar, boolean z2) {
        c();
        super.a(jVar, z2);
    }

    public void a(n nVar, t.a aVar) {
        aVar.a(nVar, 0);
        ActionMenuItemView actionMenuItemView = (ActionMenuItemView) aVar;
        actionMenuItemView.setItemInvoker((ActionMenuView) this.i);
        if (this.B == null) {
            this.B = new b();
        }
        actionMenuItemView.setPopupCallback(this.B);
    }

    public void a(ActionMenuView actionMenuView) {
        this.i = actionMenuView;
        actionMenuView.a(this.f366c);
    }

    public void a(boolean z2) {
        t tVar;
        super.a(z2);
        ((View) this.i).requestLayout();
        j jVar = this.f366c;
        boolean z3 = false;
        if (jVar != null) {
            ArrayList<n> c2 = jVar.c();
            int size = c2.size();
            for (int i = 0; i < size; i++) {
                C0124b supportActionProvider = c2.get(i).getSupportActionProvider();
                if (supportActionProvider != null) {
                    supportActionProvider.a((C0124b.a) this);
                }
            }
        }
        j jVar2 = this.f366c;
        ArrayList<n> j = jVar2 != null ? jVar2.j() : null;
        if (this.n && j != null) {
            int size2 = j.size();
            if (size2 == 1) {
                z3 = !j.get(0).isActionViewExpanded();
            } else if (size2 > 0) {
                z3 = true;
            }
        }
        if (z3) {
            if (this.k == null) {
                this.k = new d(this.f364a);
            }
            ViewGroup viewGroup = (ViewGroup) this.k.getParent();
            if (viewGroup != this.i) {
                if (viewGroup != null) {
                    viewGroup.removeView(this.k);
                }
                ActionMenuView actionMenuView = (ActionMenuView) this.i;
                actionMenuView.addView(this.k, actionMenuView.b());
            }
        } else {
            d dVar = this.k;
            if (dVar != null && dVar.getParent() == (tVar = this.i)) {
                ((ViewGroup) tVar).removeView(this.k);
            }
        }
        ((ActionMenuView) this.i).setOverflowReserved(this.n);
    }

    public boolean a() {
        int i;
        ArrayList<n> arrayList;
        int i2;
        int i3;
        int i4;
        boolean z2;
        boolean z3;
        ActionMenuPresenter actionMenuPresenter = this;
        j jVar = actionMenuPresenter.f366c;
        View view = null;
        boolean z4 = false;
        if (jVar != null) {
            arrayList = jVar.n();
            i = arrayList.size();
        } else {
            arrayList = null;
            i = 0;
        }
        int i5 = actionMenuPresenter.r;
        int i6 = actionMenuPresenter.q;
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
        ViewGroup viewGroup = (ViewGroup) actionMenuPresenter.i;
        boolean z5 = false;
        int i7 = 0;
        int i8 = 0;
        int i9 = i5;
        for (int i10 = 0; i10 < i; i10++) {
            n nVar = arrayList.get(i10);
            if (nVar.j()) {
                i7++;
            } else if (nVar.i()) {
                i8++;
            } else {
                z5 = true;
            }
            if (actionMenuPresenter.v && nVar.isActionViewExpanded()) {
                i9 = 0;
            }
        }
        if (actionMenuPresenter.n && (z5 || i8 + i7 > i9)) {
            i9--;
        }
        int i11 = i9 - i7;
        SparseBooleanArray sparseBooleanArray = actionMenuPresenter.x;
        sparseBooleanArray.clear();
        if (actionMenuPresenter.t) {
            int i12 = actionMenuPresenter.w;
            i2 = i6 / i12;
            i3 = i12 + ((i6 % i12) / i2);
        } else {
            i3 = 0;
            i2 = 0;
        }
        int i13 = 0;
        int i14 = i6;
        int i15 = 0;
        while (i15 < i) {
            n nVar2 = arrayList.get(i15);
            if (nVar2.j()) {
                View a2 = actionMenuPresenter.a(nVar2, view, viewGroup);
                if (actionMenuPresenter.t) {
                    i2 -= ActionMenuView.a(a2, i3, i2, makeMeasureSpec, z4 ? 1 : 0);
                } else {
                    a2.measure(makeMeasureSpec, makeMeasureSpec);
                }
                int measuredWidth = a2.getMeasuredWidth();
                i14 -= measuredWidth;
                if (i13 != 0) {
                    measuredWidth = i13;
                }
                int groupId = nVar2.getGroupId();
                if (groupId != 0) {
                    z3 = true;
                    sparseBooleanArray.put(groupId, true);
                } else {
                    z3 = true;
                }
                nVar2.d(z3);
                i13 = measuredWidth;
                z2 = z4;
                i4 = i;
            } else if (nVar2.i()) {
                int groupId2 = nVar2.getGroupId();
                boolean z6 = sparseBooleanArray.get(groupId2);
                boolean z7 = (i11 > 0 || z6) && i14 > 0 && (!actionMenuPresenter.t || i2 > 0);
                if (z7) {
                    boolean z8 = z7;
                    i4 = i;
                    View a3 = actionMenuPresenter.a(nVar2, (View) null, viewGroup);
                    if (actionMenuPresenter.t) {
                        int a4 = ActionMenuView.a(a3, i3, i2, makeMeasureSpec, 0);
                        i2 -= a4;
                        z8 = a4 == 0 ? false : z8;
                    } else {
                        a3.measure(makeMeasureSpec, makeMeasureSpec);
                    }
                    int measuredWidth2 = a3.getMeasuredWidth();
                    i14 -= measuredWidth2;
                    if (i13 == 0) {
                        i13 = measuredWidth2;
                    }
                    z7 = z8 & (!actionMenuPresenter.t ? i14 + i13 > 0 : i14 >= 0);
                } else {
                    boolean z9 = z7;
                    i4 = i;
                }
                if (z7 && groupId2 != 0) {
                    sparseBooleanArray.put(groupId2, true);
                } else if (z6) {
                    sparseBooleanArray.put(groupId2, false);
                    int i16 = 0;
                    while (i16 < i15) {
                        n nVar3 = arrayList.get(i16);
                        if (nVar3.getGroupId() == groupId2) {
                            if (nVar3.g()) {
                                i11++;
                            }
                            nVar3.d(false);
                        }
                        i16++;
                    }
                }
                if (z7) {
                    i11--;
                }
                nVar2.d(z7);
                z2 = false;
            } else {
                z2 = z4;
                i4 = i;
                nVar2.d(z2);
            }
            i15++;
            view = null;
            z4 = z2;
            i = i4;
            actionMenuPresenter = this;
        }
        return true;
    }

    public boolean a(int i, n nVar) {
        return nVar.g();
    }

    public boolean a(ViewGroup viewGroup, int i) {
        if (viewGroup.getChildAt(i) == this.k) {
            return false;
        }
        return super.a(viewGroup, i);
    }

    public boolean a(z zVar) {
        boolean z2 = false;
        if (!zVar.hasVisibleItems()) {
            return false;
        }
        z zVar2 = zVar;
        while (zVar2.t() != this.f366c) {
            zVar2 = (z) zVar2.t();
        }
        View a2 = a(zVar2.getItem());
        if (a2 == null) {
            return false;
        }
        this.D = zVar.getItem().getItemId();
        int size = zVar.size();
        int i = 0;
        while (true) {
            if (i >= size) {
                break;
            }
            MenuItem item = zVar.getItem(i);
            if (item.isVisible() && item.getIcon() != null) {
                z2 = true;
                break;
            }
            i++;
        }
        this.z = new a(this.f365b, zVar, a2);
        this.z.a(z2);
        this.z.e();
        super.a(zVar);
        return true;
    }

    public t b(ViewGroup viewGroup) {
        t tVar = this.i;
        t b2 = super.b(viewGroup);
        if (tVar != b2) {
            ((ActionMenuView) b2).setPresenter(this);
        }
        return b2;
    }

    public void b(boolean z2) {
        this.v = z2;
    }

    public void c(boolean z2) {
        this.n = z2;
        this.o = true;
    }

    public boolean c() {
        return e() | f();
    }

    public Drawable d() {
        d dVar = this.k;
        if (dVar != null) {
            return dVar.getDrawable();
        }
        if (this.m) {
            return this.l;
        }
        return null;
    }

    public boolean e() {
        t tVar;
        c cVar = this.A;
        if (cVar == null || (tVar = this.i) == null) {
            e eVar = this.y;
            if (eVar == null) {
                return false;
            }
            eVar.a();
            return true;
        }
        ((View) tVar).removeCallbacks(cVar);
        this.A = null;
        return true;
    }

    public boolean f() {
        a aVar = this.z;
        if (aVar == null) {
            return false;
        }
        aVar.a();
        return true;
    }

    public boolean g() {
        return this.A != null || h();
    }

    public boolean h() {
        e eVar = this.y;
        return eVar != null && eVar.c();
    }

    public boolean i() {
        j jVar;
        if (!this.n || h() || (jVar = this.f366c) == null || this.i == null || this.A != null || jVar.j().isEmpty()) {
            return false;
        }
        this.A = new c(new e(this.f365b, this.f366c, this.k, true));
        ((View) this.i).post(this.A);
        super.a((z) null);
        return true;
    }
}
