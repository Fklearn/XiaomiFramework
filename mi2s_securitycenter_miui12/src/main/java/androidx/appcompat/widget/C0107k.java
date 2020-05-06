package androidx.appcompat.widget;

import a.a.a;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.core.view.t;
import androidx.core.widget.TextViewCompat;
import androidx.core.widget.b;
import androidx.core.widget.k;

/* renamed from: androidx.appcompat.widget.k  reason: case insensitive filesystem */
public class C0107k extends Button implements t, b, k {

    /* renamed from: a  reason: collision with root package name */
    private final C0105j f616a;

    /* renamed from: b  reason: collision with root package name */
    private final H f617b;

    public C0107k(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, a.buttonStyle);
    }

    public C0107k(@NonNull Context context, @Nullable AttributeSet attributeSet, int i) {
        super(sa.a(context), attributeSet, i);
        qa.a((View) this, getContext());
        this.f616a = new C0105j(this);
        this.f616a.a(attributeSet, i);
        this.f617b = new H(this);
        this.f617b.a(attributeSet, i);
        this.f617b.a();
    }

    /* access modifiers changed from: protected */
    public void drawableStateChanged() {
        super.drawableStateChanged();
        C0105j jVar = this.f616a;
        if (jVar != null) {
            jVar.a();
        }
        H h = this.f617b;
        if (h != null) {
            h.a();
        }
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public int getAutoSizeMaxTextSize() {
        if (b.f853a) {
            return super.getAutoSizeMaxTextSize();
        }
        H h = this.f617b;
        if (h != null) {
            return h.c();
        }
        return -1;
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public int getAutoSizeMinTextSize() {
        if (b.f853a) {
            return super.getAutoSizeMinTextSize();
        }
        H h = this.f617b;
        if (h != null) {
            return h.d();
        }
        return -1;
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public int getAutoSizeStepGranularity() {
        if (b.f853a) {
            return super.getAutoSizeStepGranularity();
        }
        H h = this.f617b;
        if (h != null) {
            return h.e();
        }
        return -1;
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public int[] getAutoSizeTextAvailableSizes() {
        if (b.f853a) {
            return super.getAutoSizeTextAvailableSizes();
        }
        H h = this.f617b;
        return h != null ? h.f() : new int[0];
    }

    @SuppressLint({"WrongConstant"})
    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public int getAutoSizeTextType() {
        if (b.f853a) {
            return super.getAutoSizeTextType() == 1 ? 1 : 0;
        }
        H h = this.f617b;
        if (h != null) {
            return h.g();
        }
        return 0;
    }

    @Nullable
    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public ColorStateList getSupportBackgroundTintList() {
        C0105j jVar = this.f616a;
        if (jVar != null) {
            return jVar.b();
        }
        return null;
    }

    @Nullable
    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public PorterDuff.Mode getSupportBackgroundTintMode() {
        C0105j jVar = this.f616a;
        if (jVar != null) {
            return jVar.c();
        }
        return null;
    }

    @Nullable
    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public ColorStateList getSupportCompoundDrawablesTintList() {
        return this.f617b.h();
    }

    @Nullable
    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public PorterDuff.Mode getSupportCompoundDrawablesTintMode() {
        return this.f617b.i();
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        accessibilityEvent.setClassName(Button.class.getName());
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName(Button.class.getName());
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        H h = this.f617b;
        if (h != null) {
            h.a(z, i, i2, i3, i4);
        }
    }

    /* access modifiers changed from: protected */
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        super.onTextChanged(charSequence, i, i2, i3);
        H h = this.f617b;
        if (h != null && !b.f853a && h.j()) {
            this.f617b.b();
        }
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public void setAutoSizeTextTypeUniformWithConfiguration(int i, int i2, int i3, int i4) {
        if (b.f853a) {
            super.setAutoSizeTextTypeUniformWithConfiguration(i, i2, i3, i4);
            return;
        }
        H h = this.f617b;
        if (h != null) {
            h.a(i, i2, i3, i4);
        }
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public void setAutoSizeTextTypeUniformWithPresetSizes(@NonNull int[] iArr, int i) {
        if (b.f853a) {
            super.setAutoSizeTextTypeUniformWithPresetSizes(iArr, i);
            return;
        }
        H h = this.f617b;
        if (h != null) {
            h.a(iArr, i);
        }
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public void setAutoSizeTextTypeWithDefaults(int i) {
        if (b.f853a) {
            super.setAutoSizeTextTypeWithDefaults(i);
            return;
        }
        H h = this.f617b;
        if (h != null) {
            h.a(i);
        }
    }

    public void setBackgroundDrawable(Drawable drawable) {
        super.setBackgroundDrawable(drawable);
        C0105j jVar = this.f616a;
        if (jVar != null) {
            jVar.a(drawable);
        }
    }

    public void setBackgroundResource(@DrawableRes int i) {
        super.setBackgroundResource(i);
        C0105j jVar = this.f616a;
        if (jVar != null) {
            jVar.a(i);
        }
    }

    public void setCustomSelectionActionModeCallback(ActionMode.Callback callback) {
        super.setCustomSelectionActionModeCallback(TextViewCompat.a((TextView) this, callback));
    }

    public void setSupportAllCaps(boolean z) {
        H h = this.f617b;
        if (h != null) {
            h.a(z);
        }
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public void setSupportBackgroundTintList(@Nullable ColorStateList colorStateList) {
        C0105j jVar = this.f616a;
        if (jVar != null) {
            jVar.b(colorStateList);
        }
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public void setSupportBackgroundTintMode(@Nullable PorterDuff.Mode mode) {
        C0105j jVar = this.f616a;
        if (jVar != null) {
            jVar.a(mode);
        }
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public void setSupportCompoundDrawablesTintList(@Nullable ColorStateList colorStateList) {
        this.f617b.a(colorStateList);
        this.f617b.a();
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public void setSupportCompoundDrawablesTintMode(@Nullable PorterDuff.Mode mode) {
        this.f617b.a(mode);
        this.f617b.a();
    }

    public void setTextAppearance(Context context, int i) {
        super.setTextAppearance(context, i);
        H h = this.f617b;
        if (h != null) {
            h.a(context, i);
        }
    }

    public void setTextSize(int i, float f) {
        if (b.f853a) {
            super.setTextSize(i, f);
            return;
        }
        H h = this.f617b;
        if (h != null) {
            h.a(i, f);
        }
    }
}
