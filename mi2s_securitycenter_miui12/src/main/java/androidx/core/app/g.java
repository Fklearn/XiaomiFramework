package androidx.core.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.core.view.C0126d;
import androidx.lifecycle.f;
import androidx.lifecycle.i;
import androidx.lifecycle.k;
import androidx.lifecycle.r;

@RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
public class g extends Activity implements i, C0126d.a {

    /* renamed from: a  reason: collision with root package name */
    private a.c.i<Class<? extends Object>, Object> f697a = new a.c.i<>();

    /* renamed from: b  reason: collision with root package name */
    private k f698b = new k(this);

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public boolean a(KeyEvent keyEvent) {
        return super.dispatchKeyEvent(keyEvent);
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        View decorView = getWindow().getDecorView();
        if (decorView == null || !C0126d.a(decorView, keyEvent)) {
            return C0126d.a(this, decorView, this, keyEvent);
        }
        return true;
    }

    public boolean dispatchKeyShortcutEvent(KeyEvent keyEvent) {
        View decorView = getWindow().getDecorView();
        if (decorView == null || !C0126d.a(decorView, keyEvent)) {
            return super.dispatchKeyShortcutEvent(keyEvent);
        }
        return true;
    }

    /* access modifiers changed from: protected */
    @SuppressLint({"RestrictedApi"})
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        r.a((Activity) this);
    }

    /* access modifiers changed from: protected */
    @CallSuper
    public void onSaveInstanceState(@NonNull Bundle bundle) {
        this.f698b.a(f.b.CREATED);
        super.onSaveInstanceState(bundle);
    }
}
