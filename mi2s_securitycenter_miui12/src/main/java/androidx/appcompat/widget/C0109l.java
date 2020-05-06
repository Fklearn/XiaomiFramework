package androidx.appcompat.widget;

import a.a.a;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.core.view.t;
import androidx.core.widget.j;

/* renamed from: androidx.appcompat.widget.l  reason: case insensitive filesystem */
public class C0109l extends CheckBox implements j, t {

    /* renamed from: a  reason: collision with root package name */
    private final C0110m f619a;

    /* renamed from: b  reason: collision with root package name */
    private final C0105j f620b;

    /* renamed from: c  reason: collision with root package name */
    private final H f621c;

    public C0109l(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, a.checkboxStyle);
    }

    public C0109l(@NonNull Context context, @Nullable AttributeSet attributeSet, int i) {
        super(sa.a(context), attributeSet, i);
        qa.a((View) this, getContext());
        this.f619a = new C0110m(this);
        this.f619a.a(attributeSet, i);
        this.f620b = new C0105j(this);
        this.f620b.a(attributeSet, i);
        this.f621c = new H(this);
        this.f621c.a(attributeSet, i);
    }

    /* access modifiers changed from: protected */
    public void drawableStateChanged() {
        super.drawableStateChanged();
        C0105j jVar = this.f620b;
        if (jVar != null) {
            jVar.a();
        }
        H h = this.f621c;
        if (h != null) {
            h.a();
        }
    }

    public int getCompoundPaddingLeft() {
        int compoundPaddingLeft = super.getCompoundPaddingLeft();
        C0110m mVar = this.f619a;
        return mVar != null ? mVar.a(compoundPaddingLeft) : compoundPaddingLeft;
    }

    @Nullable
    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public ColorStateList getSupportBackgroundTintList() {
        C0105j jVar = this.f620b;
        if (jVar != null) {
            return jVar.b();
        }
        return null;
    }

    @Nullable
    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public PorterDuff.Mode getSupportBackgroundTintMode() {
        C0105j jVar = this.f620b;
        if (jVar != null) {
            return jVar.c();
        }
        return null;
    }

    @Nullable
    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public ColorStateList getSupportButtonTintList() {
        C0110m mVar = this.f619a;
        if (mVar != null) {
            return mVar.b();
        }
        return null;
    }

    @Nullable
    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public PorterDuff.Mode getSupportButtonTintMode() {
        C0110m mVar = this.f619a;
        if (mVar != null) {
            return mVar.c();
        }
        return null;
    }

    public void setBackgroundDrawable(Drawable drawable) {
        super.setBackgroundDrawable(drawable);
        C0105j jVar = this.f620b;
        if (jVar != null) {
            jVar.a(drawable);
        }
    }

    public void setBackgroundResource(@DrawableRes int i) {
        super.setBackgroundResource(i);
        C0105j jVar = this.f620b;
        if (jVar != null) {
            jVar.a(i);
        }
    }

    public void setButtonDrawable(@DrawableRes int i) {
        setButtonDrawable(a.a.a.a.a.b(getContext(), i));
    }

    public void setButtonDrawable(Drawable drawable) {
        super.setButtonDrawable(drawable);
        C0110m mVar = this.f619a;
        if (mVar != null) {
            mVar.d();
        }
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public void setSupportBackgroundTintList(@Nullable ColorStateList colorStateList) {
        C0105j jVar = this.f620b;
        if (jVar != null) {
            jVar.b(colorStateList);
        }
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public void setSupportBackgroundTintMode(@Nullable PorterDuff.Mode mode) {
        C0105j jVar = this.f620b;
        if (jVar != null) {
            jVar.a(mode);
        }
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public void setSupportButtonTintList(@Nullable ColorStateList colorStateList) {
        C0110m mVar = this.f619a;
        if (mVar != null) {
            mVar.a(colorStateList);
        }
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public void setSupportButtonTintMode(@Nullable PorterDuff.Mode mode) {
        C0110m mVar = this.f619a;
        if (mVar != null) {
            mVar.a(mode);
        }
    }
}
