package androidx.appcompat.view.menu;

import a.c.i;
import a.d.b.a.b;
import android.content.Context;
import android.view.MenuItem;
import android.view.SubMenu;

abstract class c {

    /* renamed from: a  reason: collision with root package name */
    final Context f368a;

    /* renamed from: b  reason: collision with root package name */
    private i<b, MenuItem> f369b;

    /* renamed from: c  reason: collision with root package name */
    private i<a.d.b.a.c, SubMenu> f370c;

    c(Context context) {
        this.f368a = context;
    }

    /* access modifiers changed from: package-private */
    public final MenuItem a(MenuItem menuItem) {
        if (!(menuItem instanceof b)) {
            return menuItem;
        }
        b bVar = (b) menuItem;
        if (this.f369b == null) {
            this.f369b = new i<>();
        }
        MenuItem menuItem2 = this.f369b.get(menuItem);
        if (menuItem2 != null) {
            return menuItem2;
        }
        o oVar = new o(this.f368a, bVar);
        this.f369b.put(bVar, oVar);
        return oVar;
    }

    /* access modifiers changed from: package-private */
    public final SubMenu a(SubMenu subMenu) {
        if (!(subMenu instanceof a.d.b.a.c)) {
            return subMenu;
        }
        a.d.b.a.c cVar = (a.d.b.a.c) subMenu;
        if (this.f370c == null) {
            this.f370c = new i<>();
        }
        SubMenu subMenu2 = this.f370c.get(cVar);
        if (subMenu2 != null) {
            return subMenu2;
        }
        A a2 = new A(this.f368a, cVar);
        this.f370c.put(cVar, a2);
        return a2;
    }

    /* access modifiers changed from: package-private */
    public final void a() {
        i<b, MenuItem> iVar = this.f369b;
        if (iVar != null) {
            iVar.clear();
        }
        i<a.d.b.a.c, SubMenu> iVar2 = this.f370c;
        if (iVar2 != null) {
            iVar2.clear();
        }
    }

    /* access modifiers changed from: package-private */
    public final void a(int i) {
        if (this.f369b != null) {
            int i2 = 0;
            while (i2 < this.f369b.size()) {
                if (this.f369b.b(i2).getGroupId() == i) {
                    this.f369b.c(i2);
                    i2--;
                }
                i2++;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public final void b(int i) {
        if (this.f369b != null) {
            for (int i2 = 0; i2 < this.f369b.size(); i2++) {
                if (this.f369b.b(i2).getItemId() == i) {
                    this.f369b.c(i2);
                    return;
                }
            }
        }
    }
}
