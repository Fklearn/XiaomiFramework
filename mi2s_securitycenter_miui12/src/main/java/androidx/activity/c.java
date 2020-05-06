package androidx.activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.CallSuper;
import androidx.annotation.LayoutRes;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.g;
import androidx.lifecycle.f;
import androidx.lifecycle.i;
import androidx.lifecycle.k;
import androidx.lifecycle.r;
import androidx.lifecycle.u;
import androidx.lifecycle.v;
import androidx.savedstate.b;

public class c extends g implements i, v, androidx.savedstate.c, e {

    /* renamed from: c  reason: collision with root package name */
    private final k f212c = new k(this);

    /* renamed from: d  reason: collision with root package name */
    private final b f213d = b.a((androidx.savedstate.c) this);
    private u e;
    private final OnBackPressedDispatcher f = new OnBackPressedDispatcher(new b(this));
    @LayoutRes
    private int g;

    static final class a {

        /* renamed from: a  reason: collision with root package name */
        Object f214a;

        /* renamed from: b  reason: collision with root package name */
        u f215b;

        a() {
        }
    }

    public c() {
        if (a() != null) {
            if (Build.VERSION.SDK_INT >= 19) {
                a().a(new ComponentActivity$2(this));
            }
            a().a(new ComponentActivity$3(this));
            int i = Build.VERSION.SDK_INT;
            if (19 <= i && i <= 23) {
                a().a(new ImmLeaksCleaner(this));
                return;
            }
            return;
        }
        throw new IllegalStateException("getLifecycle() returned null in ComponentActivity's constructor. Please make sure you are lazily constructing your Lifecycle in the first call to getLifecycle() rather than relying on field initialization.");
    }

    @NonNull
    public f a() {
        return this.f212c;
    }

    @NonNull
    public final OnBackPressedDispatcher b() {
        return this.f;
    }

    @NonNull
    public final androidx.savedstate.a c() {
        return this.f213d.a();
    }

    @NonNull
    public u d() {
        if (getApplication() != null) {
            if (this.e == null) {
                a aVar = (a) getLastNonConfigurationInstance();
                if (aVar != null) {
                    this.e = aVar.f215b;
                }
                if (this.e == null) {
                    this.e = new u();
                }
            }
            return this.e;
        }
        throw new IllegalStateException("Your activity is not yet attached to the Application instance. You can't request ViewModel before onCreate call.");
    }

    @Deprecated
    @Nullable
    public Object e() {
        return null;
    }

    @MainThread
    public void onBackPressed() {
        this.f.a();
    }

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        this.f213d.a(bundle);
        r.a((Activity) this);
        int i = this.g;
        if (i != 0) {
            setContentView(i);
        }
    }

    @Nullable
    public final Object onRetainNonConfigurationInstance() {
        a aVar;
        Object e2 = e();
        u uVar = this.e;
        if (uVar == null && (aVar = (a) getLastNonConfigurationInstance()) != null) {
            uVar = aVar.f215b;
        }
        if (uVar == null && e2 == null) {
            return null;
        }
        a aVar2 = new a();
        aVar2.f214a = e2;
        aVar2.f215b = uVar;
        return aVar2;
    }

    /* access modifiers changed from: protected */
    @CallSuper
    public void onSaveInstanceState(@NonNull Bundle bundle) {
        f a2 = a();
        if (a2 instanceof k) {
            ((k) a2).b(f.b.CREATED);
        }
        super.onSaveInstanceState(bundle);
        this.f213d.b(bundle);
    }
}
