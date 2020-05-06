package androidx.appcompat.widget;

import a.a.a;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.core.view.t;
import androidx.core.widget.l;

public class r extends ImageButton implements t, l {

    /* renamed from: a  reason: collision with root package name */
    private final C0105j f646a;

    /* renamed from: b  reason: collision with root package name */
    private final C0115s f647b;

    public r(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, a.imageButtonStyle);
    }

    public r(@NonNull Context context, @Nullable AttributeSet attributeSet, int i) {
        super(sa.a(context), attributeSet, i);
        qa.a((View) this, getContext());
        this.f646a = new C0105j(this);
        this.f646a.a(attributeSet, i);
        this.f647b = new C0115s(this);
        this.f647b.a(attributeSet, i);
    }

    /* access modifiers changed from: protected */
    public void drawableStateChanged() {
        super.drawableStateChanged();
        C0105j jVar = this.f646a;
        if (jVar != null) {
            jVar.a();
        }
        C0115s sVar = this.f647b;
        if (sVar != null) {
            sVar.a();
        }
    }

    @Nullable
    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public ColorStateList getSupportBackgroundTintList() {
        C0105j jVar = this.f646a;
        if (jVar != null) {
            return jVar.b();
        }
        return null;
    }

    @Nullable
    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public PorterDuff.Mode getSupportBackgroundTintMode() {
        C0105j jVar = this.f646a;
        if (jVar != null) {
            return jVar.c();
        }
        return null;
    }

    @Nullable
    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public ColorStateList getSupportImageTintList() {
        C0115s sVar = this.f647b;
        if (sVar != null) {
            return sVar.b();
        }
        return null;
    }

    @Nullable
    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public PorterDuff.Mode getSupportImageTintMode() {
        C0115s sVar = this.f647b;
        if (sVar != null) {
            return sVar.c();
        }
        return null;
    }

    public boolean hasOverlappingRendering() {
        return this.f647b.d() && super.hasOverlappingRendering();
    }

    public void setBackgroundDrawable(Drawable drawable) {
        super.setBackgroundDrawable(drawable);
        C0105j jVar = this.f646a;
        if (jVar != null) {
            jVar.a(drawable);
        }
    }

    public void setBackgroundResource(@DrawableRes int i) {
        super.setBackgroundResource(i);
        C0105j jVar = this.f646a;
        if (jVar != null) {
            jVar.a(i);
        }
    }

    public void setImageBitmap(Bitmap bitmap) {
        super.setImageBitmap(bitmap);
        C0115s sVar = this.f647b;
        if (sVar != null) {
            sVar.a();
        }
    }

    public void setImageDrawable(@Nullable Drawable drawable) {
        super.setImageDrawable(drawable);
        C0115s sVar = this.f647b;
        if (sVar != null) {
            sVar.a();
        }
    }

    public void setImageResource(@DrawableRes int i) {
        this.f647b.a(i);
    }

    public void setImageURI(@Nullable Uri uri) {
        super.setImageURI(uri);
        C0115s sVar = this.f647b;
        if (sVar != null) {
            sVar.a();
        }
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public void setSupportBackgroundTintList(@Nullable ColorStateList colorStateList) {
        C0105j jVar = this.f646a;
        if (jVar != null) {
            jVar.b(colorStateList);
        }
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public void setSupportBackgroundTintMode(@Nullable PorterDuff.Mode mode) {
        C0105j jVar = this.f646a;
        if (jVar != null) {
            jVar.a(mode);
        }
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public void setSupportImageTintList(@Nullable ColorStateList colorStateList) {
        C0115s sVar = this.f647b;
        if (sVar != null) {
            sVar.a(colorStateList);
        }
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public void setSupportImageTintMode(@Nullable PorterDuff.Mode mode) {
        C0115s sVar = this.f647b;
        if (sVar != null) {
            sVar.a(mode);
        }
    }
}
