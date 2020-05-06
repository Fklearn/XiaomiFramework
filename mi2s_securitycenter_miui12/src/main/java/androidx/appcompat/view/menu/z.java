package androidx.appcompat.view.menu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import androidx.annotation.RestrictTo;
import androidx.appcompat.view.menu.j;

@RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
public class z extends j implements SubMenu {
    private j B;
    private n C;

    public z(Context context, j jVar, n nVar) {
        super(context);
        this.B = jVar;
        this.C = nVar;
    }

    public void a(j.a aVar) {
        this.B.a(aVar);
    }

    /* access modifiers changed from: package-private */
    public boolean a(j jVar, MenuItem menuItem) {
        return super.a(jVar, menuItem) || this.B.a(jVar, menuItem);
    }

    public boolean a(n nVar) {
        return this.B.a(nVar);
    }

    public boolean b(n nVar) {
        return this.B.b(nVar);
    }

    public String d() {
        n nVar = this.C;
        int itemId = nVar != null ? nVar.getItemId() : 0;
        if (itemId == 0) {
            return null;
        }
        return super.d() + ":" + itemId;
    }

    public MenuItem getItem() {
        return this.C;
    }

    public j m() {
        return this.B.m();
    }

    public boolean o() {
        return this.B.o();
    }

    public boolean p() {
        return this.B.p();
    }

    public boolean q() {
        return this.B.q();
    }

    public void setGroupDividerEnabled(boolean z) {
        this.B.setGroupDividerEnabled(z);
    }

    public SubMenu setHeaderIcon(int i) {
        super.d(i);
        return this;
    }

    public SubMenu setHeaderIcon(Drawable drawable) {
        super.a(drawable);
        return this;
    }

    public SubMenu setHeaderTitle(int i) {
        super.e(i);
        return this;
    }

    public SubMenu setHeaderTitle(CharSequence charSequence) {
        super.a(charSequence);
        return this;
    }

    public SubMenu setHeaderView(View view) {
        super.a(view);
        return this;
    }

    public SubMenu setIcon(int i) {
        this.C.setIcon(i);
        return this;
    }

    public SubMenu setIcon(Drawable drawable) {
        this.C.setIcon(drawable);
        return this;
    }

    public void setQwertyMode(boolean z) {
        this.B.setQwertyMode(z);
    }

    public Menu t() {
        return this.B;
    }
}
