package androidx.appcompat.widget;

import a.a.j;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

/* renamed from: androidx.appcompat.widget.j  reason: case insensitive filesystem */
class C0105j {
    @NonNull

    /* renamed from: a  reason: collision with root package name */
    private final View f611a;

    /* renamed from: b  reason: collision with root package name */
    private final C0112o f612b;

    /* renamed from: c  reason: collision with root package name */
    private int f613c = -1;

    /* renamed from: d  reason: collision with root package name */
    private ta f614d;
    private ta e;
    private ta f;

    C0105j(@NonNull View view) {
        this.f611a = view;
        this.f612b = C0112o.b();
    }

    private boolean b(@NonNull Drawable drawable) {
        if (this.f == null) {
            this.f = new ta();
        }
        ta taVar = this.f;
        taVar.a();
        ColorStateList e2 = ViewCompat.e(this.f611a);
        if (e2 != null) {
            taVar.f662d = true;
            taVar.f659a = e2;
        }
        PorterDuff.Mode f2 = ViewCompat.f(this.f611a);
        if (f2 != null) {
            taVar.f661c = true;
            taVar.f660b = f2;
        }
        if (!taVar.f662d && !taVar.f661c) {
            return false;
        }
        C0112o.a(drawable, taVar, this.f611a.getDrawableState());
        return true;
    }

    private boolean d() {
        int i = Build.VERSION.SDK_INT;
        return i > 21 ? this.f614d != null : i == 21;
    }

    /* access modifiers changed from: package-private */
    public void a() {
        Drawable background = this.f611a.getBackground();
        if (background == null) {
            return;
        }
        if (!d() || !b(background)) {
            ta taVar = this.e;
            if (taVar != null || (taVar = this.f614d) != null) {
                C0112o.a(background, taVar, this.f611a.getDrawableState());
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void a(int i) {
        this.f613c = i;
        C0112o oVar = this.f612b;
        a(oVar != null ? oVar.b(this.f611a.getContext(), i) : null);
        a();
    }

    /* access modifiers changed from: package-private */
    public void a(ColorStateList colorStateList) {
        if (colorStateList != null) {
            if (this.f614d == null) {
                this.f614d = new ta();
            }
            ta taVar = this.f614d;
            taVar.f659a = colorStateList;
            taVar.f662d = true;
        } else {
            this.f614d = null;
        }
        a();
    }

    /* access modifiers changed from: package-private */
    public void a(PorterDuff.Mode mode) {
        if (this.e == null) {
            this.e = new ta();
        }
        ta taVar = this.e;
        taVar.f660b = mode;
        taVar.f661c = true;
        a();
    }

    /* access modifiers changed from: package-private */
    public void a(Drawable drawable) {
        this.f613c = -1;
        a((ColorStateList) null);
        a();
    }

    /* access modifiers changed from: package-private */
    public void a(@Nullable AttributeSet attributeSet, int i) {
        va a2 = va.a(this.f611a.getContext(), attributeSet, j.ViewBackgroundHelper, i, 0);
        if (Build.VERSION.SDK_INT >= 29) {
            View view = this.f611a;
            view.saveAttributeDataForStyleable(view.getContext(), j.ViewBackgroundHelper, attributeSet, a2.a(), i, 0);
        }
        try {
            if (a2.g(j.ViewBackgroundHelper_android_background)) {
                this.f613c = a2.g(j.ViewBackgroundHelper_android_background, -1);
                ColorStateList b2 = this.f612b.b(this.f611a.getContext(), this.f613c);
                if (b2 != null) {
                    a(b2);
                }
            }
            if (a2.g(j.ViewBackgroundHelper_backgroundTint)) {
                ViewCompat.a(this.f611a, a2.a(j.ViewBackgroundHelper_backgroundTint));
            }
            if (a2.g(j.ViewBackgroundHelper_backgroundTintMode)) {
                ViewCompat.a(this.f611a, N.a(a2.d(j.ViewBackgroundHelper_backgroundTintMode, -1), (PorterDuff.Mode) null));
            }
        } finally {
            a2.b();
        }
    }

    /* access modifiers changed from: package-private */
    public ColorStateList b() {
        ta taVar = this.e;
        if (taVar != null) {
            return taVar.f659a;
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public void b(ColorStateList colorStateList) {
        if (this.e == null) {
            this.e = new ta();
        }
        ta taVar = this.e;
        taVar.f659a = colorStateList;
        taVar.f662d = true;
        a();
    }

    /* access modifiers changed from: package-private */
    public PorterDuff.Mode c() {
        ta taVar = this.e;
        if (taVar != null) {
            return taVar.f660b;
        }
        return null;
    }
}
