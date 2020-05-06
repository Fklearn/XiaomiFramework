package androidx.fragment.app;

import a.d.e.f;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.v;

/* renamed from: androidx.fragment.app.i  reason: case insensitive filesystem */
public class C0139i {

    /* renamed from: a  reason: collision with root package name */
    private final C0141k<?> f914a;

    private C0139i(C0141k<?> kVar) {
        this.f914a = kVar;
    }

    @NonNull
    public static C0139i a(@NonNull C0141k<?> kVar) {
        f.a(kVar, "callbacks == null");
        return new C0139i(kVar);
    }

    @Nullable
    public View a(@Nullable View view, @NonNull String str, @NonNull Context context, @NonNull AttributeSet attributeSet) {
        return this.f914a.e.onCreateView(view, str, context, attributeSet);
    }

    @Nullable
    public Fragment a(@NonNull String str) {
        return this.f914a.e.b(str);
    }

    public void a() {
        this.f914a.e.e();
    }

    public void a(@NonNull Configuration configuration) {
        this.f914a.e.a(configuration);
    }

    public void a(@Nullable Parcelable parcelable) {
        C0141k<?> kVar = this.f914a;
        if (kVar instanceof v) {
            kVar.e.a(parcelable);
            return;
        }
        throw new IllegalStateException("Your FragmentHostCallback must implement ViewModelStoreOwner to call restoreSaveState(). Call restoreAllState()  if you're still using retainNestedNonConfig().");
    }

    public void a(@NonNull Menu menu) {
        this.f914a.e.a(menu);
    }

    public void a(@Nullable Fragment fragment) {
        C0141k<?> kVar = this.f914a;
        kVar.e.a((C0141k) kVar, (C0138h) kVar, fragment);
    }

    public void a(boolean z) {
        this.f914a.e.a(z);
    }

    public boolean a(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        return this.f914a.e.a(menu, menuInflater);
    }

    public boolean a(@NonNull MenuItem menuItem) {
        return this.f914a.e.a(menuItem);
    }

    public void b() {
        this.f914a.e.f();
    }

    public void b(boolean z) {
        this.f914a.e.b(z);
    }

    public boolean b(@NonNull Menu menu) {
        return this.f914a.e.b(menu);
    }

    public boolean b(@NonNull MenuItem menuItem) {
        return this.f914a.e.b(menuItem);
    }

    public void c() {
        this.f914a.e.g();
    }

    public void d() {
        this.f914a.e.i();
    }

    public void e() {
        this.f914a.e.j();
    }

    public void f() {
        this.f914a.e.l();
    }

    public void g() {
        this.f914a.e.m();
    }

    public void h() {
        this.f914a.e.n();
    }

    public boolean i() {
        return this.f914a.e.p();
    }

    @NonNull
    public C0142l j() {
        return this.f914a.e;
    }

    public void k() {
        this.f914a.e.w();
    }

    @Nullable
    public Parcelable l() {
        return this.f914a.e.y();
    }
}
