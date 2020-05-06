package androidx.appcompat.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.StyleableRes;
import androidx.core.content.res.g;

@RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
public class va {

    /* renamed from: a  reason: collision with root package name */
    private final Context f669a;

    /* renamed from: b  reason: collision with root package name */
    private final TypedArray f670b;

    /* renamed from: c  reason: collision with root package name */
    private TypedValue f671c;

    private va(Context context, TypedArray typedArray) {
        this.f669a = context;
        this.f670b = typedArray;
    }

    public static va a(Context context, int i, int[] iArr) {
        return new va(context, context.obtainStyledAttributes(i, iArr));
    }

    public static va a(Context context, AttributeSet attributeSet, int[] iArr) {
        return new va(context, context.obtainStyledAttributes(attributeSet, iArr));
    }

    public static va a(Context context, AttributeSet attributeSet, int[] iArr, int i, int i2) {
        return new va(context, context.obtainStyledAttributes(attributeSet, iArr, i, i2));
    }

    public float a(int i, float f) {
        return this.f670b.getFloat(i, f);
    }

    public int a(int i, int i2) {
        return this.f670b.getColor(i, i2);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x0011, code lost:
        r0 = a.a.a.a.a.a(r2.f669a, (r0 = r2.f670b.getResourceId(r3, 0)));
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.content.res.ColorStateList a(int r3) {
        /*
            r2 = this;
            android.content.res.TypedArray r0 = r2.f670b
            boolean r0 = r0.hasValue(r3)
            if (r0 == 0) goto L_0x001a
            android.content.res.TypedArray r0 = r2.f670b
            r1 = 0
            int r0 = r0.getResourceId(r3, r1)
            if (r0 == 0) goto L_0x001a
            android.content.Context r1 = r2.f669a
            android.content.res.ColorStateList r0 = a.a.a.a.a.a(r1, r0)
            if (r0 == 0) goto L_0x001a
            return r0
        L_0x001a:
            android.content.res.TypedArray r0 = r2.f670b
            android.content.res.ColorStateList r3 = r0.getColorStateList(r3)
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.appcompat.widget.va.a(int):android.content.res.ColorStateList");
    }

    public TypedArray a() {
        return this.f670b;
    }

    @Nullable
    public Typeface a(@StyleableRes int i, int i2, @Nullable g.a aVar) {
        int resourceId = this.f670b.getResourceId(i, 0);
        if (resourceId == 0) {
            return null;
        }
        if (this.f671c == null) {
            this.f671c = new TypedValue();
        }
        return g.a(this.f669a, resourceId, this.f671c, i2, aVar);
    }

    public boolean a(int i, boolean z) {
        return this.f670b.getBoolean(i, z);
    }

    public int b(int i, int i2) {
        return this.f670b.getDimensionPixelOffset(i, i2);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0008, code lost:
        r0 = r2.f670b.getResourceId(r3, 0);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.graphics.drawable.Drawable b(int r3) {
        /*
            r2 = this;
            android.content.res.TypedArray r0 = r2.f670b
            boolean r0 = r0.hasValue(r3)
            if (r0 == 0) goto L_0x0018
            android.content.res.TypedArray r0 = r2.f670b
            r1 = 0
            int r0 = r0.getResourceId(r3, r1)
            if (r0 == 0) goto L_0x0018
            android.content.Context r3 = r2.f669a
            android.graphics.drawable.Drawable r3 = a.a.a.a.a.b(r3, r0)
            return r3
        L_0x0018:
            android.content.res.TypedArray r0 = r2.f670b
            android.graphics.drawable.Drawable r3 = r0.getDrawable(r3)
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.appcompat.widget.va.b(int):android.graphics.drawable.Drawable");
    }

    public void b() {
        this.f670b.recycle();
    }

    public int c(int i, int i2) {
        return this.f670b.getDimensionPixelSize(i, i2);
    }

    public Drawable c(int i) {
        int resourceId;
        if (!this.f670b.hasValue(i) || (resourceId = this.f670b.getResourceId(i, 0)) == 0) {
            return null;
        }
        return C0112o.b().a(this.f669a, resourceId, true);
    }

    public int d(int i, int i2) {
        return this.f670b.getInt(i, i2);
    }

    public String d(int i) {
        return this.f670b.getString(i);
    }

    public int e(int i, int i2) {
        return this.f670b.getInteger(i, i2);
    }

    public CharSequence e(int i) {
        return this.f670b.getText(i);
    }

    public int f(int i, int i2) {
        return this.f670b.getLayoutDimension(i, i2);
    }

    public CharSequence[] f(int i) {
        return this.f670b.getTextArray(i);
    }

    public int g(int i, int i2) {
        return this.f670b.getResourceId(i, i2);
    }

    public boolean g(int i) {
        return this.f670b.hasValue(i);
    }
}
