package androidx.appcompat.view.menu;

import a.d.b.a.a;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import androidx.annotation.RestrictTo;

@RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
public class u extends c implements Menu {

    /* renamed from: d  reason: collision with root package name */
    private final a f414d;

    public u(Context context, a aVar) {
        super(context);
        if (aVar != null) {
            this.f414d = aVar;
            return;
        }
        throw new IllegalArgumentException("Wrapped Object can not be null.");
    }

    public MenuItem add(int i) {
        return a(this.f414d.add(i));
    }

    public MenuItem add(int i, int i2, int i3, int i4) {
        return a(this.f414d.add(i, i2, i3, i4));
    }

    public MenuItem add(int i, int i2, int i3, CharSequence charSequence) {
        return a(this.f414d.add(i, i2, i3, charSequence));
    }

    public MenuItem add(CharSequence charSequence) {
        return a(this.f414d.add(charSequence));
    }

    public int addIntentOptions(int i, int i2, int i3, ComponentName componentName, Intent[] intentArr, Intent intent, int i4, MenuItem[] menuItemArr) {
        MenuItem[] menuItemArr2 = menuItemArr;
        MenuItem[] menuItemArr3 = menuItemArr2 != null ? new MenuItem[menuItemArr2.length] : null;
        int addIntentOptions = this.f414d.addIntentOptions(i, i2, i3, componentName, intentArr, intent, i4, menuItemArr3);
        if (menuItemArr3 != null) {
            int length = menuItemArr3.length;
            for (int i5 = 0; i5 < length; i5++) {
                menuItemArr2[i5] = a(menuItemArr3[i5]);
            }
        }
        return addIntentOptions;
    }

    public SubMenu addSubMenu(int i) {
        return a(this.f414d.addSubMenu(i));
    }

    public SubMenu addSubMenu(int i, int i2, int i3, int i4) {
        return a(this.f414d.addSubMenu(i, i2, i3, i4));
    }

    public SubMenu addSubMenu(int i, int i2, int i3, CharSequence charSequence) {
        return a(this.f414d.addSubMenu(i, i2, i3, charSequence));
    }

    public SubMenu addSubMenu(CharSequence charSequence) {
        return a(this.f414d.addSubMenu(charSequence));
    }

    public void clear() {
        a();
        this.f414d.clear();
    }

    public void close() {
        this.f414d.close();
    }

    public MenuItem findItem(int i) {
        return a(this.f414d.findItem(i));
    }

    public MenuItem getItem(int i) {
        return a(this.f414d.getItem(i));
    }

    public boolean hasVisibleItems() {
        return this.f414d.hasVisibleItems();
    }

    public boolean isShortcutKey(int i, KeyEvent keyEvent) {
        return this.f414d.isShortcutKey(i, keyEvent);
    }

    public boolean performIdentifierAction(int i, int i2) {
        return this.f414d.performIdentifierAction(i, i2);
    }

    public boolean performShortcut(int i, KeyEvent keyEvent, int i2) {
        return this.f414d.performShortcut(i, keyEvent, i2);
    }

    public void removeGroup(int i) {
        a(i);
        this.f414d.removeGroup(i);
    }

    public void removeItem(int i) {
        b(i);
        this.f414d.removeItem(i);
    }

    public void setGroupCheckable(int i, boolean z, boolean z2) {
        this.f414d.setGroupCheckable(i, z, z2);
    }

    public void setGroupEnabled(int i, boolean z) {
        this.f414d.setGroupEnabled(i, z);
    }

    public void setGroupVisible(int i, boolean z) {
        this.f414d.setGroupVisible(i, z);
    }

    public void setQwertyMode(boolean z) {
        this.f414d.setQwertyMode(z);
    }

    public int size() {
        return this.f414d.size();
    }
}
