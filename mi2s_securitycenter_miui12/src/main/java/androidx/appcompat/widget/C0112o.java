package androidx.appcompat.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.appcompat.widget.X;

@RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
/* renamed from: androidx.appcompat.widget.o  reason: case insensitive filesystem */
public final class C0112o {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public static final PorterDuff.Mode f632a = PorterDuff.Mode.SRC_IN;

    /* renamed from: b  reason: collision with root package name */
    private static C0112o f633b;

    /* renamed from: c  reason: collision with root package name */
    private X f634c;

    public static synchronized PorterDuffColorFilter a(int i, PorterDuff.Mode mode) {
        PorterDuffColorFilter a2;
        synchronized (C0112o.class) {
            a2 = X.a(i, mode);
        }
        return a2;
    }

    static void a(Drawable drawable, ta taVar, int[] iArr) {
        X.a(drawable, taVar, iArr);
    }

    public static synchronized C0112o b() {
        C0112o oVar;
        synchronized (C0112o.class) {
            if (f633b == null) {
                c();
            }
            oVar = f633b;
        }
        return oVar;
    }

    public static synchronized void c() {
        synchronized (C0112o.class) {
            if (f633b == null) {
                f633b = new C0112o();
                f633b.f634c = X.a();
                f633b.f634c.a((X.e) new C0111n());
            }
        }
    }

    public synchronized Drawable a(@NonNull Context context, @DrawableRes int i) {
        return this.f634c.a(context, i);
    }

    /* access modifiers changed from: package-private */
    public synchronized Drawable a(@NonNull Context context, @DrawableRes int i, boolean z) {
        return this.f634c.a(context, i, z);
    }

    public synchronized void a(@NonNull Context context) {
        this.f634c.a(context);
    }

    /* access modifiers changed from: package-private */
    public synchronized ColorStateList b(@NonNull Context context, @DrawableRes int i) {
        return this.f634c.b(context, i);
    }
}
