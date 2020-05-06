package androidx.appcompat.widget;

import a.d.a.c;
import a.d.d.a;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.textclassifier.TextClassifier;
import android.widget.TextView;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.annotation.RequiresApi;
import androidx.annotation.RestrictTo;
import androidx.core.view.t;
import androidx.core.widget.TextViewCompat;
import androidx.core.widget.b;
import androidx.core.widget.k;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class I extends TextView implements t, k, b {

    /* renamed from: a  reason: collision with root package name */
    private final C0105j f498a;

    /* renamed from: b  reason: collision with root package name */
    private final H f499b;

    /* renamed from: c  reason: collision with root package name */
    private final F f500c;
    @Nullable

    /* renamed from: d  reason: collision with root package name */
    private Future<a> f501d;

    public I(@NonNull Context context) {
        this(context, (AttributeSet) null);
    }

    public I(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, 16842884);
    }

    public I(@NonNull Context context, @Nullable AttributeSet attributeSet, int i) {
        super(sa.a(context), attributeSet, i);
        qa.a((View) this, getContext());
        this.f498a = new C0105j(this);
        this.f498a.a(attributeSet, i);
        this.f499b = new H(this);
        this.f499b.a(attributeSet, i);
        this.f499b.a();
        this.f500c = new F(this);
    }

    private void d() {
        Future<a> future = this.f501d;
        if (future != null) {
            try {
                this.f501d = null;
                TextViewCompat.a((TextView) this, future.get());
            } catch (InterruptedException | ExecutionException unused) {
            }
        }
    }

    /* access modifiers changed from: protected */
    public void drawableStateChanged() {
        super.drawableStateChanged();
        C0105j jVar = this.f498a;
        if (jVar != null) {
            jVar.a();
        }
        H h = this.f499b;
        if (h != null) {
            h.a();
        }
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public int getAutoSizeMaxTextSize() {
        if (b.f853a) {
            return super.getAutoSizeMaxTextSize();
        }
        H h = this.f499b;
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
        H h = this.f499b;
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
        H h = this.f499b;
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
        H h = this.f499b;
        return h != null ? h.f() : new int[0];
    }

    @SuppressLint({"WrongConstant"})
    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public int getAutoSizeTextType() {
        if (b.f853a) {
            return super.getAutoSizeTextType() == 1 ? 1 : 0;
        }
        H h = this.f499b;
        if (h != null) {
            return h.g();
        }
        return 0;
    }

    public int getFirstBaselineToTopHeight() {
        return TextViewCompat.a((TextView) this);
    }

    public int getLastBaselineToBottomHeight() {
        return TextViewCompat.b(this);
    }

    @Nullable
    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public ColorStateList getSupportBackgroundTintList() {
        C0105j jVar = this.f498a;
        if (jVar != null) {
            return jVar.b();
        }
        return null;
    }

    @Nullable
    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public PorterDuff.Mode getSupportBackgroundTintMode() {
        C0105j jVar = this.f498a;
        if (jVar != null) {
            return jVar.c();
        }
        return null;
    }

    @Nullable
    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public ColorStateList getSupportCompoundDrawablesTintList() {
        return this.f499b.h();
    }

    @Nullable
    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public PorterDuff.Mode getSupportCompoundDrawablesTintMode() {
        return this.f499b.i();
    }

    public CharSequence getText() {
        d();
        return super.getText();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0006, code lost:
        r0 = r2.f500c;
     */
    @androidx.annotation.RequiresApi(api = 26)
    @androidx.annotation.NonNull
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.textclassifier.TextClassifier getTextClassifier() {
        /*
            r2 = this;
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 28
            if (r0 >= r1) goto L_0x0010
            androidx.appcompat.widget.F r0 = r2.f500c
            if (r0 != 0) goto L_0x000b
            goto L_0x0010
        L_0x000b:
            android.view.textclassifier.TextClassifier r0 = r0.a()
            return r0
        L_0x0010:
            android.view.textclassifier.TextClassifier r0 = super.getTextClassifier()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.appcompat.widget.I.getTextClassifier():android.view.textclassifier.TextClassifier");
    }

    @NonNull
    public a.C0003a getTextMetricsParamsCompat() {
        return TextViewCompat.c(this);
    }

    public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
        InputConnection onCreateInputConnection = super.onCreateInputConnection(editorInfo);
        C0114q.a(onCreateInputConnection, editorInfo, this);
        return onCreateInputConnection;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        H h = this.f499b;
        if (h != null) {
            h.a(z, i, i2, i3, i4);
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        d();
        super.onMeasure(i, i2);
    }

    /* access modifiers changed from: protected */
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        super.onTextChanged(charSequence, i, i2, i3);
        H h = this.f499b;
        if (h != null && !b.f853a && h.j()) {
            this.f499b.b();
        }
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public void setAutoSizeTextTypeUniformWithConfiguration(int i, int i2, int i3, int i4) {
        if (b.f853a) {
            super.setAutoSizeTextTypeUniformWithConfiguration(i, i2, i3, i4);
            return;
        }
        H h = this.f499b;
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
        H h = this.f499b;
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
        H h = this.f499b;
        if (h != null) {
            h.a(i);
        }
    }

    public void setBackgroundDrawable(Drawable drawable) {
        super.setBackgroundDrawable(drawable);
        C0105j jVar = this.f498a;
        if (jVar != null) {
            jVar.a(drawable);
        }
    }

    public void setBackgroundResource(@DrawableRes int i) {
        super.setBackgroundResource(i);
        C0105j jVar = this.f498a;
        if (jVar != null) {
            jVar.a(i);
        }
    }

    public void setCompoundDrawables(@Nullable Drawable drawable, @Nullable Drawable drawable2, @Nullable Drawable drawable3, @Nullable Drawable drawable4) {
        super.setCompoundDrawables(drawable, drawable2, drawable3, drawable4);
        H h = this.f499b;
        if (h != null) {
            h.k();
        }
    }

    @RequiresApi(17)
    public void setCompoundDrawablesRelative(@Nullable Drawable drawable, @Nullable Drawable drawable2, @Nullable Drawable drawable3, @Nullable Drawable drawable4) {
        super.setCompoundDrawablesRelative(drawable, drawable2, drawable3, drawable4);
        H h = this.f499b;
        if (h != null) {
            h.k();
        }
    }

    @RequiresApi(17)
    public void setCompoundDrawablesRelativeWithIntrinsicBounds(int i, int i2, int i3, int i4) {
        Context context = getContext();
        Drawable drawable = null;
        Drawable b2 = i != 0 ? a.a.a.a.a.b(context, i) : null;
        Drawable b3 = i2 != 0 ? a.a.a.a.a.b(context, i2) : null;
        Drawable b4 = i3 != 0 ? a.a.a.a.a.b(context, i3) : null;
        if (i4 != 0) {
            drawable = a.a.a.a.a.b(context, i4);
        }
        setCompoundDrawablesRelativeWithIntrinsicBounds(b2, b3, b4, drawable);
        H h = this.f499b;
        if (h != null) {
            h.k();
        }
    }

    @RequiresApi(17)
    public void setCompoundDrawablesRelativeWithIntrinsicBounds(@Nullable Drawable drawable, @Nullable Drawable drawable2, @Nullable Drawable drawable3, @Nullable Drawable drawable4) {
        super.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, drawable2, drawable3, drawable4);
        H h = this.f499b;
        if (h != null) {
            h.k();
        }
    }

    public void setCompoundDrawablesWithIntrinsicBounds(int i, int i2, int i3, int i4) {
        Context context = getContext();
        Drawable drawable = null;
        Drawable b2 = i != 0 ? a.a.a.a.a.b(context, i) : null;
        Drawable b3 = i2 != 0 ? a.a.a.a.a.b(context, i2) : null;
        Drawable b4 = i3 != 0 ? a.a.a.a.a.b(context, i3) : null;
        if (i4 != 0) {
            drawable = a.a.a.a.a.b(context, i4);
        }
        setCompoundDrawablesWithIntrinsicBounds(b2, b3, b4, drawable);
        H h = this.f499b;
        if (h != null) {
            h.k();
        }
    }

    public void setCompoundDrawablesWithIntrinsicBounds(@Nullable Drawable drawable, @Nullable Drawable drawable2, @Nullable Drawable drawable3, @Nullable Drawable drawable4) {
        super.setCompoundDrawablesWithIntrinsicBounds(drawable, drawable2, drawable3, drawable4);
        H h = this.f499b;
        if (h != null) {
            h.k();
        }
    }

    public void setCustomSelectionActionModeCallback(ActionMode.Callback callback) {
        super.setCustomSelectionActionModeCallback(TextViewCompat.a((TextView) this, callback));
    }

    public void setFirstBaselineToTopHeight(@Px @IntRange(from = 0) int i) {
        if (Build.VERSION.SDK_INT >= 28) {
            super.setFirstBaselineToTopHeight(i);
        } else {
            TextViewCompat.a((TextView) this, i);
        }
    }

    public void setLastBaselineToBottomHeight(@Px @IntRange(from = 0) int i) {
        if (Build.VERSION.SDK_INT >= 28) {
            super.setLastBaselineToBottomHeight(i);
        } else {
            TextViewCompat.b(this, i);
        }
    }

    public void setLineHeight(@Px @IntRange(from = 0) int i) {
        TextViewCompat.c(this, i);
    }

    public void setPrecomputedText(@NonNull a aVar) {
        TextViewCompat.a((TextView) this, aVar);
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public void setSupportBackgroundTintList(@Nullable ColorStateList colorStateList) {
        C0105j jVar = this.f498a;
        if (jVar != null) {
            jVar.b(colorStateList);
        }
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public void setSupportBackgroundTintMode(@Nullable PorterDuff.Mode mode) {
        C0105j jVar = this.f498a;
        if (jVar != null) {
            jVar.a(mode);
        }
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public void setSupportCompoundDrawablesTintList(@Nullable ColorStateList colorStateList) {
        this.f499b.a(colorStateList);
        this.f499b.a();
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public void setSupportCompoundDrawablesTintMode(@Nullable PorterDuff.Mode mode) {
        this.f499b.a(mode);
        this.f499b.a();
    }

    public void setTextAppearance(Context context, int i) {
        super.setTextAppearance(context, i);
        H h = this.f499b;
        if (h != null) {
            h.a(context, i);
        }
    }

    @RequiresApi(api = 26)
    public void setTextClassifier(@Nullable TextClassifier textClassifier) {
        F f;
        if (Build.VERSION.SDK_INT >= 28 || (f = this.f500c) == null) {
            super.setTextClassifier(textClassifier);
        } else {
            f.a(textClassifier);
        }
    }

    public void setTextFuture(@Nullable Future<a> future) {
        this.f501d = future;
        if (future != null) {
            requestLayout();
        }
    }

    public void setTextMetricsParamsCompat(@NonNull a.C0003a aVar) {
        TextViewCompat.a((TextView) this, aVar);
    }

    public void setTextSize(int i, float f) {
        if (b.f853a) {
            super.setTextSize(i, f);
            return;
        }
        H h = this.f499b;
        if (h != null) {
            h.a(i, f);
        }
    }

    public void setTypeface(@Nullable Typeface typeface, int i) {
        Typeface a2 = (typeface == null || i <= 0) ? null : c.a(getContext(), typeface, i);
        if (a2 != null) {
            typeface = a2;
        }
        super.setTypeface(typeface, i);
    }
}
