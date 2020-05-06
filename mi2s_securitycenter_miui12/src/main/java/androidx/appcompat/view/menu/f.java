package androidx.appcompat.view.menu;

import android.view.MenuItem;
import androidx.appcompat.view.menu.CascadingMenuPopup;

class f implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ CascadingMenuPopup.a f373a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ MenuItem f374b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ j f375c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ g f376d;

    f(g gVar, CascadingMenuPopup.a aVar, MenuItem menuItem, j jVar) {
        this.f376d = gVar;
        this.f373a = aVar;
        this.f374b = menuItem;
        this.f375c = jVar;
    }

    public void run() {
        CascadingMenuPopup.a aVar = this.f373a;
        if (aVar != null) {
            this.f376d.f377a.B = true;
            aVar.f351b.a(false);
            this.f376d.f377a.B = false;
        }
        if (this.f374b.isEnabled() && this.f374b.hasSubMenu()) {
            this.f375c.a(this.f374b, 4);
        }
    }
}
