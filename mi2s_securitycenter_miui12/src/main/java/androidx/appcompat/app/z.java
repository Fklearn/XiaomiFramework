package androidx.appcompat.app;

import a.a.a;
import a.a.d.b;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.core.view.C0126d;

public class z extends Dialog implements m {

    /* renamed from: a  reason: collision with root package name */
    private AppCompatDelegate f328a;

    /* renamed from: b  reason: collision with root package name */
    private final C0126d.a f329b = new y(this);

    public z(Context context, int i) {
        super(context, a(context, i));
        AppCompatDelegate a2 = a();
        a2.d(a(context, i));
        a2.a((Bundle) null);
    }

    private static int a(Context context, int i) {
        if (i != 0) {
            return i;
        }
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(a.dialogTheme, typedValue, true);
        return typedValue.resourceId;
    }

    @Nullable
    public b a(b.a aVar) {
        return null;
    }

    public AppCompatDelegate a() {
        if (this.f328a == null) {
            this.f328a = AppCompatDelegate.a((Dialog) this, (m) this);
        }
        return this.f328a;
    }

    public void a(b bVar) {
    }

    public boolean a(int i) {
        return a().b(i);
    }

    /* access modifiers changed from: package-private */
    public boolean a(KeyEvent keyEvent) {
        return super.dispatchKeyEvent(keyEvent);
    }

    public void addContentView(View view, ViewGroup.LayoutParams layoutParams) {
        a().a(view, layoutParams);
    }

    public void b(b bVar) {
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        return C0126d.a(this.f329b, getWindow().getDecorView(), this, keyEvent);
    }

    @Nullable
    public <T extends View> T findViewById(@IdRes int i) {
        return a().a(i);
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public void invalidateOptionsMenu() {
        a().f();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        a().e();
        super.onCreate(bundle);
        a().a(bundle);
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        a().j();
    }

    public void setContentView(@LayoutRes int i) {
        a().c(i);
    }

    public void setContentView(View view) {
        a().a(view);
    }

    public void setContentView(View view, ViewGroup.LayoutParams layoutParams) {
        a().b(view, layoutParams);
    }

    public void setTitle(int i) {
        super.setTitle(i);
        a().a((CharSequence) getContext().getString(i));
    }

    public void setTitle(CharSequence charSequence) {
        super.setTitle(charSequence);
        a().a(charSequence);
    }
}
