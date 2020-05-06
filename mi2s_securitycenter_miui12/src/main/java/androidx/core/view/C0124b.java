package androidx.core.view;

import android.content.Context;
import android.util.Log;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import androidx.annotation.RestrictTo;

/* renamed from: androidx.core.view.b  reason: case insensitive filesystem */
public abstract class C0124b {

    /* renamed from: a  reason: collision with root package name */
    private final Context f816a;

    /* renamed from: b  reason: collision with root package name */
    private a f817b;

    /* renamed from: c  reason: collision with root package name */
    private C0015b f818c;

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    /* renamed from: androidx.core.view.b$a */
    public interface a {
    }

    /* renamed from: androidx.core.view.b$b  reason: collision with other inner class name */
    public interface C0015b {
        void onActionProviderVisibilityChanged(boolean z);
    }

    public C0124b(Context context) {
        this.f816a = context;
    }

    public View a(MenuItem menuItem) {
        return c();
    }

    public void a(SubMenu subMenu) {
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public void a(a aVar) {
        this.f817b = aVar;
    }

    public void a(C0015b bVar) {
        if (!(this.f818c == null || bVar == null)) {
            Log.w("ActionProvider(support)", "setVisibilityListener: Setting a new ActionProvider.VisibilityListener when one is already set. Are you reusing this " + getClass().getSimpleName() + " instance while it is still in use somewhere else?");
        }
        this.f818c = bVar;
    }

    public boolean a() {
        return false;
    }

    public boolean b() {
        return true;
    }

    public abstract View c();

    public boolean d() {
        return false;
    }

    public boolean e() {
        return false;
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public void f() {
        this.f818c = null;
        this.f817b = null;
    }
}
