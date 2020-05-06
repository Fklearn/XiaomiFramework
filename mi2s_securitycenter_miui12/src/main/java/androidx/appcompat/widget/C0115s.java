package androidx.appcompat.widget;

import a.a.a.a.a;
import a.a.j;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.core.widget.e;

@RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
/* renamed from: androidx.appcompat.widget.s  reason: case insensitive filesystem */
public class C0115s {
    @NonNull

    /* renamed from: a  reason: collision with root package name */
    private final ImageView f648a;

    /* renamed from: b  reason: collision with root package name */
    private ta f649b;

    /* renamed from: c  reason: collision with root package name */
    private ta f650c;

    /* renamed from: d  reason: collision with root package name */
    private ta f651d;

    public C0115s(@NonNull ImageView imageView) {
        this.f648a = imageView;
    }

    private boolean a(@NonNull Drawable drawable) {
        if (this.f651d == null) {
            this.f651d = new ta();
        }
        ta taVar = this.f651d;
        taVar.a();
        ColorStateList a2 = e.a(this.f648a);
        if (a2 != null) {
            taVar.f662d = true;
            taVar.f659a = a2;
        }
        PorterDuff.Mode b2 = e.b(this.f648a);
        if (b2 != null) {
            taVar.f661c = true;
            taVar.f660b = b2;
        }
        if (!taVar.f662d && !taVar.f661c) {
            return false;
        }
        C0112o.a(drawable, taVar, this.f648a.getDrawableState());
        return true;
    }

    private boolean e() {
        int i = Build.VERSION.SDK_INT;
        return i > 21 ? this.f649b != null : i == 21;
    }

    /* access modifiers changed from: package-private */
    public void a() {
        Drawable drawable = this.f648a.getDrawable();
        if (drawable != null) {
            N.b(drawable);
        }
        if (drawable == null) {
            return;
        }
        if (!e() || !a(drawable)) {
            ta taVar = this.f650c;
            if (taVar != null || (taVar = this.f649b) != null) {
                C0112o.a(drawable, taVar, this.f648a.getDrawableState());
            }
        }
    }

    public void a(int i) {
        if (i != 0) {
            Drawable b2 = a.b(this.f648a.getContext(), i);
            if (b2 != null) {
                N.b(b2);
            }
            this.f648a.setImageDrawable(b2);
        } else {
            this.f648a.setImageDrawable((Drawable) null);
        }
        a();
    }

    /* access modifiers changed from: package-private */
    public void a(ColorStateList colorStateList) {
        if (this.f650c == null) {
            this.f650c = new ta();
        }
        ta taVar = this.f650c;
        taVar.f659a = colorStateList;
        taVar.f662d = true;
        a();
    }

    /* access modifiers changed from: package-private */
    public void a(PorterDuff.Mode mode) {
        if (this.f650c == null) {
            this.f650c = new ta();
        }
        ta taVar = this.f650c;
        taVar.f660b = mode;
        taVar.f661c = true;
        a();
    }

    public void a(AttributeSet attributeSet, int i) {
        int g;
        va a2 = va.a(this.f648a.getContext(), attributeSet, j.AppCompatImageView, i, 0);
        if (Build.VERSION.SDK_INT >= 29) {
            ImageView imageView = this.f648a;
            imageView.saveAttributeDataForStyleable(imageView.getContext(), j.AppCompatImageView, attributeSet, a2.a(), i, 0);
        }
        try {
            Drawable drawable = this.f648a.getDrawable();
            if (!(drawable != null || (g = a2.g(j.AppCompatImageView_srcCompat, -1)) == -1 || (drawable = a.b(this.f648a.getContext(), g)) == null)) {
                this.f648a.setImageDrawable(drawable);
            }
            if (drawable != null) {
                N.b(drawable);
            }
            if (a2.g(j.AppCompatImageView_tint)) {
                e.a(this.f648a, a2.a(j.AppCompatImageView_tint));
            }
            if (a2.g(j.AppCompatImageView_tintMode)) {
                e.a(this.f648a, N.a(a2.d(j.AppCompatImageView_tintMode, -1), (PorterDuff.Mode) null));
            }
        } finally {
            a2.b();
        }
    }

    /* access modifiers changed from: package-private */
    public ColorStateList b() {
        ta taVar = this.f650c;
        if (taVar != null) {
            return taVar.f659a;
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public PorterDuff.Mode c() {
        ta taVar = this.f650c;
        if (taVar != null) {
            return taVar.f660b;
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public boolean d() {
        return Build.VERSION.SDK_INT < 21 || !(this.f648a.getBackground() instanceof RippleDrawable);
    }
}
