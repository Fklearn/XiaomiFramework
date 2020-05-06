package androidx.appcompat.app;

import a.a.d.b;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.CallSuper;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.appcompat.widget.Ia;
import androidx.core.app.a;
import androidx.core.app.h;
import androidx.core.app.j;
import androidx.fragment.app.C0137g;

public class l extends C0137g implements m, j.a, C0079a {
    private AppCompatDelegate q;
    private Resources r;

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0026, code lost:
        r0 = getWindow();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean b(android.view.KeyEvent r3) {
        /*
            r2 = this;
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 26
            if (r0 >= r1) goto L_0x003e
            boolean r0 = r3.isCtrlPressed()
            if (r0 != 0) goto L_0x003e
            int r0 = r3.getMetaState()
            boolean r0 = android.view.KeyEvent.metaStateHasNoModifiers(r0)
            if (r0 != 0) goto L_0x003e
            int r0 = r3.getRepeatCount()
            if (r0 != 0) goto L_0x003e
            int r0 = r3.getKeyCode()
            boolean r0 = android.view.KeyEvent.isModifierKey(r0)
            if (r0 != 0) goto L_0x003e
            android.view.Window r0 = r2.getWindow()
            if (r0 == 0) goto L_0x003e
            android.view.View r1 = r0.getDecorView()
            if (r1 == 0) goto L_0x003e
            android.view.View r0 = r0.getDecorView()
            boolean r3 = r0.dispatchKeyShortcutEvent(r3)
            if (r3 == 0) goto L_0x003e
            r3 = 1
            return r3
        L_0x003e:
            r3 = 0
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.appcompat.app.l.b(android.view.KeyEvent):boolean");
    }

    @Nullable
    public b a(@NonNull b.a aVar) {
        return null;
    }

    @CallSuper
    public void a(@NonNull b bVar) {
    }

    public void a(@NonNull Intent intent) {
        h.a((Activity) this, intent);
    }

    public void a(@NonNull j jVar) {
        jVar.a((Activity) this);
    }

    public void addContentView(View view, ViewGroup.LayoutParams layoutParams) {
        i().a(view, layoutParams);
    }

    /* access modifiers changed from: protected */
    public void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        i().a(context);
    }

    /* access modifiers changed from: protected */
    public void b(int i) {
    }

    @CallSuper
    public void b(@NonNull b bVar) {
    }

    public void b(@NonNull j jVar) {
    }

    public boolean b(@NonNull Intent intent) {
        return h.b((Activity) this, intent);
    }

    public void closeOptionsMenu() {
        ActionBar j = j();
        if (!getWindow().hasFeature(0)) {
            return;
        }
        if (j == null || !j.e()) {
            super.closeOptionsMenu();
        }
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        int keyCode = keyEvent.getKeyCode();
        ActionBar j = j();
        if (keyCode != 82 || j == null || !j.a(keyEvent)) {
            return super.dispatchKeyEvent(keyEvent);
        }
        return true;
    }

    public <T extends View> T findViewById(@IdRes int i) {
        return i().a(i);
    }

    @NonNull
    public MenuInflater getMenuInflater() {
        return i().c();
    }

    public Resources getResources() {
        if (this.r == null && Ia.b()) {
            this.r = new Ia(this, super.getResources());
        }
        Resources resources = this.r;
        return resources == null ? super.getResources() : resources;
    }

    @Nullable
    public Intent getSupportParentActivityIntent() {
        return h.a(this);
    }

    public void h() {
        i().f();
    }

    @NonNull
    public AppCompatDelegate i() {
        if (this.q == null) {
            this.q = AppCompatDelegate.a((Activity) this, (m) this);
        }
        return this.q;
    }

    public void invalidateOptionsMenu() {
        i().f();
    }

    @Nullable
    public ActionBar j() {
        return i().d();
    }

    @Deprecated
    public void k() {
    }

    public boolean l() {
        Intent supportParentActivityIntent = getSupportParentActivityIntent();
        if (supportParentActivityIntent == null) {
            return false;
        }
        if (b(supportParentActivityIntent)) {
            j a2 = j.a((Context) this);
            a(a2);
            b(a2);
            a2.a();
            try {
                a.a(this);
                return true;
            } catch (IllegalStateException unused) {
                finish();
                return true;
            }
        } else {
            a(supportParentActivityIntent);
            return true;
        }
    }

    public void onConfigurationChanged(@NonNull Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (this.r != null) {
            this.r.updateConfiguration(configuration, super.getResources().getDisplayMetrics());
        }
        i().a(configuration);
    }

    public void onContentChanged() {
        k();
    }

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle bundle) {
        AppCompatDelegate i = i();
        i.e();
        i.a(bundle);
        super.onCreate(bundle);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        i().g();
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (b(keyEvent)) {
            return true;
        }
        return super.onKeyDown(i, keyEvent);
    }

    public final boolean onMenuItemSelected(int i, @NonNull MenuItem menuItem) {
        if (super.onMenuItemSelected(i, menuItem)) {
            return true;
        }
        ActionBar j = j();
        if (menuItem.getItemId() != 16908332 || j == null || (j.g() & 4) == 0) {
            return false;
        }
        return l();
    }

    public boolean onMenuOpened(int i, Menu menu) {
        return super.onMenuOpened(i, menu);
    }

    public void onPanelClosed(int i, @NonNull Menu menu) {
        super.onPanelClosed(i, menu);
    }

    /* access modifiers changed from: protected */
    public void onPostCreate(@Nullable Bundle bundle) {
        super.onPostCreate(bundle);
        i().b(bundle);
    }

    /* access modifiers changed from: protected */
    public void onPostResume() {
        super.onPostResume();
        i().h();
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        i().c(bundle);
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        i().i();
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        i().j();
    }

    /* access modifiers changed from: protected */
    public void onTitleChanged(CharSequence charSequence, int i) {
        super.onTitleChanged(charSequence, i);
        i().a(charSequence);
    }

    public void openOptionsMenu() {
        ActionBar j = j();
        if (!getWindow().hasFeature(0)) {
            return;
        }
        if (j == null || !j.k()) {
            super.openOptionsMenu();
        }
    }

    public void setContentView(@LayoutRes int i) {
        i().c(i);
    }

    public void setContentView(View view) {
        i().a(view);
    }

    public void setContentView(View view, ViewGroup.LayoutParams layoutParams) {
        i().b(view, layoutParams);
    }

    public void setTheme(@StyleRes int i) {
        super.setTheme(i);
        i().d(i);
    }
}
