package androidx.appcompat.view.menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.RestrictTo;
import androidx.appcompat.view.menu.s;
import androidx.appcompat.view.menu.t;
import java.util.ArrayList;

@RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
public abstract class b implements s {

    /* renamed from: a  reason: collision with root package name */
    protected Context f364a;

    /* renamed from: b  reason: collision with root package name */
    protected Context f365b;

    /* renamed from: c  reason: collision with root package name */
    protected j f366c;

    /* renamed from: d  reason: collision with root package name */
    protected LayoutInflater f367d;
    protected LayoutInflater e;
    private s.a f;
    private int g;
    private int h;
    protected t i;
    private int j;

    public b(Context context, int i2, int i3) {
        this.f364a = context;
        this.f367d = LayoutInflater.from(context);
        this.g = i2;
        this.h = i3;
    }

    public View a(n nVar, View view, ViewGroup viewGroup) {
        t.a a2 = view instanceof t.a ? (t.a) view : a(viewGroup);
        a(nVar, a2);
        return (View) a2;
    }

    public t.a a(ViewGroup viewGroup) {
        return (t.a) this.f367d.inflate(this.h, viewGroup, false);
    }

    public void a(int i2) {
        this.j = i2;
    }

    public void a(Context context, j jVar) {
        this.f365b = context;
        this.e = LayoutInflater.from(this.f365b);
        this.f366c = jVar;
    }

    /* access modifiers changed from: protected */
    public void a(View view, int i2) {
        ViewGroup viewGroup = (ViewGroup) view.getParent();
        if (viewGroup != null) {
            viewGroup.removeView(view);
        }
        ((ViewGroup) this.i).addView(view, i2);
    }

    public void a(j jVar, boolean z) {
        s.a aVar = this.f;
        if (aVar != null) {
            aVar.a(jVar, z);
        }
    }

    public abstract void a(n nVar, t.a aVar);

    public void a(s.a aVar) {
        this.f = aVar;
    }

    public void a(boolean z) {
        ViewGroup viewGroup = (ViewGroup) this.i;
        if (viewGroup != null) {
            j jVar = this.f366c;
            int i2 = 0;
            if (jVar != null) {
                jVar.b();
                ArrayList<n> n = this.f366c.n();
                int size = n.size();
                int i3 = 0;
                for (int i4 = 0; i4 < size; i4++) {
                    n nVar = n.get(i4);
                    if (a(i3, nVar)) {
                        View childAt = viewGroup.getChildAt(i3);
                        n itemData = childAt instanceof t.a ? ((t.a) childAt).getItemData() : null;
                        View a2 = a(nVar, childAt, viewGroup);
                        if (nVar != itemData) {
                            a2.setPressed(false);
                            a2.jumpDrawablesToCurrentState();
                        }
                        if (a2 != childAt) {
                            a(a2, i3);
                        }
                        i3++;
                    }
                }
                i2 = i3;
            }
            while (i2 < viewGroup.getChildCount()) {
                if (!a(viewGroup, i2)) {
                    i2++;
                }
            }
        }
    }

    public abstract boolean a(int i2, n nVar);

    /* access modifiers changed from: protected */
    public boolean a(ViewGroup viewGroup, int i2) {
        viewGroup.removeViewAt(i2);
        return true;
    }

    public boolean a(j jVar, n nVar) {
        return false;
    }

    public boolean a(z zVar) {
        s.a aVar = this.f;
        if (aVar != null) {
            return aVar.a(zVar);
        }
        return false;
    }

    public s.a b() {
        return this.f;
    }

    public t b(ViewGroup viewGroup) {
        if (this.i == null) {
            this.i = (t) this.f367d.inflate(this.g, viewGroup, false);
            this.i.a(this.f366c);
            a(true);
        }
        return this.i;
    }

    public boolean b(j jVar, n nVar) {
        return false;
    }
}
