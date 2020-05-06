package androidx.appcompat.view.menu;

import a.a.g;
import android.content.Context;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import androidx.annotation.RestrictTo;
import androidx.appcompat.view.menu.s;
import androidx.appcompat.view.menu.t;
import java.util.ArrayList;

@RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
public class h implements s, AdapterView.OnItemClickListener {

    /* renamed from: a  reason: collision with root package name */
    Context f378a;

    /* renamed from: b  reason: collision with root package name */
    LayoutInflater f379b;

    /* renamed from: c  reason: collision with root package name */
    j f380c;

    /* renamed from: d  reason: collision with root package name */
    ExpandedMenuView f381d;
    int e;
    int f;
    int g;
    private s.a h;
    a i;

    private class a extends BaseAdapter {

        /* renamed from: a  reason: collision with root package name */
        private int f382a = -1;

        public a() {
            a();
        }

        /* access modifiers changed from: package-private */
        public void a() {
            n f = h.this.f380c.f();
            if (f != null) {
                ArrayList<n> j = h.this.f380c.j();
                int size = j.size();
                for (int i = 0; i < size; i++) {
                    if (j.get(i) == f) {
                        this.f382a = i;
                        return;
                    }
                }
            }
            this.f382a = -1;
        }

        public int getCount() {
            int size = h.this.f380c.j().size() - h.this.e;
            return this.f382a < 0 ? size : size - 1;
        }

        public n getItem(int i) {
            ArrayList<n> j = h.this.f380c.j();
            int i2 = i + h.this.e;
            int i3 = this.f382a;
            if (i3 >= 0 && i2 >= i3) {
                i2++;
            }
            return j.get(i2);
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                h hVar = h.this;
                view = hVar.f379b.inflate(hVar.g, viewGroup, false);
            }
            ((t.a) view).a(getItem(i), 0);
            return view;
        }

        public void notifyDataSetChanged() {
            a();
            super.notifyDataSetChanged();
        }
    }

    public h(int i2, int i3) {
        this.g = i2;
        this.f = i3;
    }

    public h(Context context, int i2) {
        this(i2, 0);
        this.f378a = context;
        this.f379b = LayoutInflater.from(this.f378a);
    }

    public t a(ViewGroup viewGroup) {
        if (this.f381d == null) {
            this.f381d = (ExpandedMenuView) this.f379b.inflate(g.abc_expanded_menu_layout, viewGroup, false);
            if (this.i == null) {
                this.i = new a();
            }
            this.f381d.setAdapter(this.i);
            this.f381d.setOnItemClickListener(this);
        }
        return this.f381d;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:7:0x001c, code lost:
        if (r2.f379b == null) goto L_0x000b;
     */
    /* JADX WARNING: Removed duplicated region for block: B:10:0x0025  */
    /* JADX WARNING: Removed duplicated region for block: B:12:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void a(android.content.Context r3, androidx.appcompat.view.menu.j r4) {
        /*
            r2 = this;
            int r0 = r2.f
            if (r0 == 0) goto L_0x0014
            android.view.ContextThemeWrapper r1 = new android.view.ContextThemeWrapper
            r1.<init>(r3, r0)
            r2.f378a = r1
        L_0x000b:
            android.content.Context r3 = r2.f378a
            android.view.LayoutInflater r3 = android.view.LayoutInflater.from(r3)
            r2.f379b = r3
            goto L_0x001f
        L_0x0014:
            android.content.Context r0 = r2.f378a
            if (r0 == 0) goto L_0x001f
            r2.f378a = r3
            android.view.LayoutInflater r3 = r2.f379b
            if (r3 != 0) goto L_0x001f
            goto L_0x000b
        L_0x001f:
            r2.f380c = r4
            androidx.appcompat.view.menu.h$a r3 = r2.i
            if (r3 == 0) goto L_0x0028
            r3.notifyDataSetChanged()
        L_0x0028:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.appcompat.view.menu.h.a(android.content.Context, androidx.appcompat.view.menu.j):void");
    }

    public void a(j jVar, boolean z) {
        s.a aVar = this.h;
        if (aVar != null) {
            aVar.a(jVar, z);
        }
    }

    public void a(s.a aVar) {
        this.h = aVar;
    }

    public void a(boolean z) {
        a aVar = this.i;
        if (aVar != null) {
            aVar.notifyDataSetChanged();
        }
    }

    public boolean a() {
        return false;
    }

    public boolean a(j jVar, n nVar) {
        return false;
    }

    public boolean a(z zVar) {
        if (!zVar.hasVisibleItems()) {
            return false;
        }
        new k(zVar).a((IBinder) null);
        s.a aVar = this.h;
        if (aVar == null) {
            return true;
        }
        aVar.a(zVar);
        return true;
    }

    public ListAdapter b() {
        if (this.i == null) {
            this.i = new a();
        }
        return this.i;
    }

    public boolean b(j jVar, n nVar) {
        return false;
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i2, long j) {
        this.f380c.a((MenuItem) this.i.getItem(i2), (s) this, 0);
    }
}
