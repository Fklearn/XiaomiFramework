package androidx.appcompat.widget;

import a.a.j;
import a.d.a.a;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

@RestrictTo({RestrictTo.a.LIBRARY})
public class qa {

    /* renamed from: a  reason: collision with root package name */
    private static final ThreadLocal<TypedValue> f642a = new ThreadLocal<>();

    /* renamed from: b  reason: collision with root package name */
    static final int[] f643b = {-16842910};

    /* renamed from: c  reason: collision with root package name */
    static final int[] f644c = {16842908};

    /* renamed from: d  reason: collision with root package name */
    static final int[] f645d = {16843518};
    static final int[] e = {16842919};
    static final int[] f = {16842912};
    static final int[] g = {16842913};
    static final int[] h = {-16842919, -16842908};
    static final int[] i = new int[0];
    private static final int[] j = new int[1];

    public static int a(@NonNull Context context, int i2) {
        ColorStateList c2 = c(context, i2);
        if (c2 != null && c2.isStateful()) {
            return c2.getColorForState(f643b, c2.getDefaultColor());
        }
        TypedValue a2 = a();
        context.getTheme().resolveAttribute(16842803, a2, true);
        return a(context, i2, a2.getFloat());
    }

    static int a(@NonNull Context context, int i2, float f2) {
        int b2 = b(context, i2);
        return a.b(b2, Math.round(((float) Color.alpha(b2)) * f2));
    }

    private static TypedValue a() {
        TypedValue typedValue = f642a.get();
        if (typedValue != null) {
            return typedValue;
        }
        TypedValue typedValue2 = new TypedValue();
        f642a.set(typedValue2);
        return typedValue2;
    }

    public static void a(@NonNull View view, @NonNull Context context) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(j.AppCompatTheme);
        try {
            if (!obtainStyledAttributes.hasValue(j.AppCompatTheme_windowActionBar)) {
                Log.e("ThemeUtils", "View " + view.getClass() + " is an AppCompat widget that can only be used with a Theme.AppCompat theme (or descendant).");
            }
        } finally {
            obtainStyledAttributes.recycle();
        }
    }

    public static int b(@NonNull Context context, int i2) {
        int[] iArr = j;
        iArr[0] = i2;
        va a2 = va.a(context, (AttributeSet) null, iArr);
        try {
            return a2.a(0, 0);
        } finally {
            a2.b();
        }
    }

    @Nullable
    public static ColorStateList c(@NonNull Context context, int i2) {
        int[] iArr = j;
        iArr[0] = i2;
        va a2 = va.a(context, (AttributeSet) null, iArr);
        try {
            return a2.a(0);
        } finally {
            a2.b();
        }
    }
}
