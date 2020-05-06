package androidx.appcompat.app;

import a.c.d;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.StyleRes;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.Iterator;

public abstract class AppCompatDelegate {

    /* renamed from: a  reason: collision with root package name */
    private static int f242a = -100;

    /* renamed from: b  reason: collision with root package name */
    private static final d<WeakReference<AppCompatDelegate>> f243b = new d<>();

    /* renamed from: c  reason: collision with root package name */
    private static final Object f244c = new Object();

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    @Retention(RetentionPolicy.SOURCE)
    public @interface NightMode {
    }

    AppCompatDelegate() {
    }

    public static int a() {
        return f242a;
    }

    @NonNull
    public static AppCompatDelegate a(@NonNull Activity activity, @Nullable m mVar) {
        return new AppCompatDelegateImpl(activity, mVar);
    }

    @NonNull
    public static AppCompatDelegate a(@NonNull Dialog dialog, @Nullable m mVar) {
        return new AppCompatDelegateImpl(dialog, mVar);
    }

    static void a(@NonNull AppCompatDelegate appCompatDelegate) {
        synchronized (f244c) {
            c(appCompatDelegate);
            f243b.add(new WeakReference(appCompatDelegate));
        }
    }

    static void b(@NonNull AppCompatDelegate appCompatDelegate) {
        synchronized (f244c) {
            c(appCompatDelegate);
        }
    }

    private static void c(@NonNull AppCompatDelegate appCompatDelegate) {
        synchronized (f244c) {
            Iterator<WeakReference<AppCompatDelegate>> it = f243b.iterator();
            while (it.hasNext()) {
                AppCompatDelegate appCompatDelegate2 = (AppCompatDelegate) it.next().get();
                if (appCompatDelegate2 == appCompatDelegate || appCompatDelegate2 == null) {
                    it.remove();
                }
            }
        }
    }

    @Nullable
    public abstract <T extends View> T a(@IdRes int i);

    public void a(Context context) {
    }

    public abstract void a(Configuration configuration);

    public abstract void a(Bundle bundle);

    public abstract void a(View view);

    public abstract void a(View view, ViewGroup.LayoutParams layoutParams);

    public abstract void a(@Nullable CharSequence charSequence);

    public int b() {
        return -100;
    }

    public abstract void b(Bundle bundle);

    public abstract void b(View view, ViewGroup.LayoutParams layoutParams);

    public abstract boolean b(int i);

    public abstract MenuInflater c();

    public abstract void c(@LayoutRes int i);

    public abstract void c(Bundle bundle);

    @Nullable
    public abstract ActionBar d();

    public void d(@StyleRes int i) {
    }

    public abstract void e();

    public abstract void f();

    public abstract void g();

    public abstract void h();

    public abstract void i();

    public abstract void j();
}
