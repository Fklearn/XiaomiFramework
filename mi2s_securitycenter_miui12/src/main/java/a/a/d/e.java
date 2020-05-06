package a.a.d;

import a.a.d.b;
import android.content.Context;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.RestrictTo;
import androidx.appcompat.view.menu.j;
import androidx.appcompat.widget.ActionBarContextView;
import java.lang.ref.WeakReference;

@RestrictTo({RestrictTo.a.f224c})
public class e extends b implements j.a {

    /* renamed from: c  reason: collision with root package name */
    private Context f13c;

    /* renamed from: d  reason: collision with root package name */
    private ActionBarContextView f14d;
    private b.a e;
    private WeakReference<View> f;
    private boolean g;
    private boolean h;
    private j i;

    public e(Context context, ActionBarContextView actionBarContextView, b.a aVar, boolean z) {
        this.f13c = context;
        this.f14d = actionBarContextView;
        this.e = aVar;
        j jVar = new j(actionBarContextView.getContext());
        jVar.c(1);
        this.i = jVar;
        this.i.a((j.a) this);
        this.h = z;
    }

    public void a() {
        if (!this.g) {
            this.g = true;
            this.f14d.sendAccessibilityEvent(32);
            this.e.a(this);
        }
    }

    public void a(int i2) {
        a((CharSequence) this.f13c.getString(i2));
    }

    public void a(View view) {
        this.f14d.setCustomView(view);
        this.f = view != null ? new WeakReference<>(view) : null;
    }

    public void a(j jVar) {
        i();
        this.f14d.d();
    }

    public void a(CharSequence charSequence) {
        this.f14d.setSubtitle(charSequence);
    }

    public void a(boolean z) {
        super.a(z);
        this.f14d.setTitleOptional(z);
    }

    public boolean a(j jVar, MenuItem menuItem) {
        return this.e.a((b) this, menuItem);
    }

    public View b() {
        WeakReference<View> weakReference = this.f;
        if (weakReference != null) {
            return (View) weakReference.get();
        }
        return null;
    }

    public void b(int i2) {
        b((CharSequence) this.f13c.getString(i2));
    }

    public void b(CharSequence charSequence) {
        this.f14d.setTitle(charSequence);
    }

    public Menu c() {
        return this.i;
    }

    public MenuInflater d() {
        return new g(this.f14d.getContext());
    }

    public CharSequence e() {
        return this.f14d.getSubtitle();
    }

    public CharSequence g() {
        return this.f14d.getTitle();
    }

    public void i() {
        this.e.b(this, this.i);
    }

    public boolean j() {
        return this.f14d.b();
    }
}
