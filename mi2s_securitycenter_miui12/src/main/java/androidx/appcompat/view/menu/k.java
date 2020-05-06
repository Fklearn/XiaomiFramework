package androidx.appcompat.view.menu;

import a.a.g;
import android.content.DialogInterface;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.k;
import androidx.appcompat.view.menu.s;

class k implements DialogInterface.OnKeyListener, DialogInterface.OnClickListener, DialogInterface.OnDismissListener, s.a {

    /* renamed from: a  reason: collision with root package name */
    private j f392a;

    /* renamed from: b  reason: collision with root package name */
    private androidx.appcompat.app.k f393b;

    /* renamed from: c  reason: collision with root package name */
    h f394c;

    /* renamed from: d  reason: collision with root package name */
    private s.a f395d;

    public k(j jVar) {
        this.f392a = jVar;
    }

    public void a() {
        androidx.appcompat.app.k kVar = this.f393b;
        if (kVar != null) {
            kVar.dismiss();
        }
    }

    public void a(IBinder iBinder) {
        j jVar = this.f392a;
        k.a aVar = new k.a(jVar.e());
        this.f394c = new h(aVar.b(), g.abc_list_menu_item_layout);
        this.f394c.a((s.a) this);
        this.f392a.a((s) this.f394c);
        aVar.a(this.f394c.b(), this);
        View i = jVar.i();
        if (i != null) {
            aVar.a(i);
        } else {
            aVar.a(jVar.g());
            aVar.a(jVar.h());
        }
        aVar.a((DialogInterface.OnKeyListener) this);
        this.f393b = aVar.a();
        this.f393b.setOnDismissListener(this);
        WindowManager.LayoutParams attributes = this.f393b.getWindow().getAttributes();
        attributes.type = 1003;
        if (iBinder != null) {
            attributes.token = iBinder;
        }
        attributes.flags |= 131072;
        this.f393b.show();
    }

    public void a(j jVar, boolean z) {
        if (z || jVar == this.f392a) {
            a();
        }
        s.a aVar = this.f395d;
        if (aVar != null) {
            aVar.a(jVar, z);
        }
    }

    public boolean a(j jVar) {
        s.a aVar = this.f395d;
        if (aVar != null) {
            return aVar.a(jVar);
        }
        return false;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.f392a.a((MenuItem) (n) this.f394c.b().getItem(i), 0);
    }

    public void onDismiss(DialogInterface dialogInterface) {
        this.f394c.a(this.f392a, true);
    }

    public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
        Window window;
        View decorView;
        KeyEvent.DispatcherState keyDispatcherState;
        View decorView2;
        KeyEvent.DispatcherState keyDispatcherState2;
        if (i == 82 || i == 4) {
            if (keyEvent.getAction() == 0 && keyEvent.getRepeatCount() == 0) {
                Window window2 = this.f393b.getWindow();
                if (!(window2 == null || (decorView2 = window2.getDecorView()) == null || (keyDispatcherState2 = decorView2.getKeyDispatcherState()) == null)) {
                    keyDispatcherState2.startTracking(keyEvent, this);
                    return true;
                }
            } else if (keyEvent.getAction() == 1 && !keyEvent.isCanceled() && (window = this.f393b.getWindow()) != null && (decorView = window.getDecorView()) != null && (keyDispatcherState = decorView.getKeyDispatcherState()) != null && keyDispatcherState.isTracking(keyEvent)) {
                this.f392a.a(true);
                dialogInterface.dismiss();
                return true;
            }
        }
        return this.f392a.performShortcut(i, keyEvent, 0);
    }
}
