package androidx.appcompat.widget;

import a.a.j;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.RectF;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextDirectionHeuristic;
import android.text.TextDirectionHeuristics;
import android.text.TextPaint;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

class J {

    /* renamed from: a  reason: collision with root package name */
    private static final RectF f504a = new RectF();

    /* renamed from: b  reason: collision with root package name */
    private static ConcurrentHashMap<String, Method> f505b = new ConcurrentHashMap<>();

    /* renamed from: c  reason: collision with root package name */
    private static ConcurrentHashMap<String, Field> f506c = new ConcurrentHashMap<>();

    /* renamed from: d  reason: collision with root package name */
    private int f507d = 0;
    private boolean e = false;
    private float f = -1.0f;
    private float g = -1.0f;
    private float h = -1.0f;
    private int[] i = new int[0];
    private boolean j = false;
    private TextPaint k;
    @NonNull
    private final TextView l;
    private final Context m;

    J(@NonNull TextView textView) {
        this.l = textView;
        this.m = this.l.getContext();
    }

    private int a(RectF rectF) {
        int length = this.i.length;
        if (length != 0) {
            int i2 = 0;
            int i3 = 1;
            int i4 = length - 1;
            while (true) {
                int i5 = i3;
                int i6 = i2;
                i2 = i5;
                while (i2 <= i4) {
                    int i7 = (i2 + i4) / 2;
                    if (a(this.i[i7], rectF)) {
                        i3 = i7 + 1;
                    } else {
                        i6 = i7 - 1;
                        i4 = i6;
                    }
                }
                return this.i[i6];
            }
        }
        throw new IllegalStateException("No available text sizes to choose from.");
    }

    private StaticLayout a(CharSequence charSequence, Layout.Alignment alignment, int i2) {
        return new StaticLayout(charSequence, this.k, i2, alignment, ((Float) a((Object) this.l, "mSpacingMult", Float.valueOf(1.0f))).floatValue(), ((Float) a((Object) this.l, "mSpacingAdd", Float.valueOf(0.0f))).floatValue(), ((Boolean) a((Object) this.l, "mIncludePad", true)).booleanValue());
    }

    private static <T> T a(@NonNull Object obj, @NonNull String str, @NonNull T t) {
        try {
            Field a2 = a(str);
            return a2 == null ? t : a2.get(obj);
        } catch (IllegalAccessException e2) {
            Log.w("ACTVAutoSizeHelper", "Failed to access TextView#" + str + " member", e2);
            return t;
        }
    }

    @Nullable
    private static Field a(@NonNull String str) {
        try {
            Field field = f506c.get(str);
            if (field == null && (field = TextView.class.getDeclaredField(str)) != null) {
                field.setAccessible(true);
                f506c.put(str, field);
            }
            return field;
        } catch (NoSuchFieldException e2) {
            Log.w("ACTVAutoSizeHelper", "Failed to access TextView#" + str + " member", e2);
            return null;
        }
    }

    private void a(float f2) {
        if (f2 != this.l.getPaint().getTextSize()) {
            this.l.getPaint().setTextSize(f2);
            boolean isInLayout = Build.VERSION.SDK_INT >= 18 ? this.l.isInLayout() : false;
            if (this.l.getLayout() != null) {
                this.e = false;
                try {
                    Method b2 = b("nullLayouts");
                    if (b2 != null) {
                        b2.invoke(this.l, new Object[0]);
                    }
                } catch (Exception e2) {
                    Log.w("ACTVAutoSizeHelper", "Failed to invoke TextView#nullLayouts() method", e2);
                }
                if (!isInLayout) {
                    this.l.requestLayout();
                } else {
                    this.l.forceLayout();
                }
                this.l.invalidate();
            }
        }
    }

    private void a(float f2, float f3, float f4) {
        if (f2 <= 0.0f) {
            throw new IllegalArgumentException("Minimum auto-size text size (" + f2 + "px) is less or equal to (0px)");
        } else if (f3 <= f2) {
            throw new IllegalArgumentException("Maximum auto-size text size (" + f3 + "px) is less or equal to minimum auto-size text size (" + f2 + "px)");
        } else if (f4 > 0.0f) {
            this.f507d = 1;
            this.g = f2;
            this.h = f3;
            this.f = f4;
            this.j = false;
        } else {
            throw new IllegalArgumentException("The auto-size step granularity (" + f4 + "px) is less or equal to (0px)");
        }
    }

    private void a(TypedArray typedArray) {
        int length = typedArray.length();
        int[] iArr = new int[length];
        if (length > 0) {
            for (int i2 = 0; i2 < length; i2++) {
                iArr[i2] = typedArray.getDimensionPixelSize(i2, -1);
            }
            this.i = a(iArr);
            j();
        }
    }

    private boolean a(int i2, RectF rectF) {
        CharSequence transformation;
        CharSequence text = this.l.getText();
        TransformationMethod transformationMethod = this.l.getTransformationMethod();
        if (!(transformationMethod == null || (transformation = transformationMethod.getTransformation(text, this.l)) == null)) {
            text = transformation;
        }
        int maxLines = Build.VERSION.SDK_INT >= 16 ? this.l.getMaxLines() : -1;
        a(i2);
        StaticLayout a2 = a(text, (Layout.Alignment) b((Object) this.l, "getLayoutAlignment", Layout.Alignment.ALIGN_NORMAL), Math.round(rectF.right), maxLines);
        return (maxLines == -1 || (a2.getLineCount() <= maxLines && a2.getLineEnd(a2.getLineCount() - 1) == text.length())) && ((float) a2.getHeight()) <= rectF.bottom;
    }

    private int[] a(int[] iArr) {
        if (r0 == 0) {
            return iArr;
        }
        Arrays.sort(iArr);
        ArrayList arrayList = new ArrayList();
        for (int i2 : iArr) {
            if (i2 > 0 && Collections.binarySearch(arrayList, Integer.valueOf(i2)) < 0) {
                arrayList.add(Integer.valueOf(i2));
            }
        }
        if (r0 == arrayList.size()) {
            return iArr;
        }
        int size = arrayList.size();
        int[] iArr2 = new int[size];
        for (int i3 = 0; i3 < size; i3++) {
            iArr2[i3] = ((Integer) arrayList.get(i3)).intValue();
        }
        return iArr2;
    }

    @RequiresApi(16)
    private StaticLayout b(CharSequence charSequence, Layout.Alignment alignment, int i2) {
        return new StaticLayout(charSequence, this.k, i2, alignment, this.l.getLineSpacingMultiplier(), this.l.getLineSpacingExtra(), this.l.getIncludeFontPadding());
    }

    @RequiresApi(23)
    private StaticLayout b(CharSequence charSequence, Layout.Alignment alignment, int i2, int i3) {
        StaticLayout.Builder obtain = StaticLayout.Builder.obtain(charSequence, 0, charSequence.length(), this.k, i2);
        StaticLayout.Builder hyphenationFrequency = obtain.setAlignment(alignment).setLineSpacing(this.l.getLineSpacingExtra(), this.l.getLineSpacingMultiplier()).setIncludePad(this.l.getIncludeFontPadding()).setBreakStrategy(this.l.getBreakStrategy()).setHyphenationFrequency(this.l.getHyphenationFrequency());
        if (i3 == -1) {
            i3 = Integer.MAX_VALUE;
        }
        hyphenationFrequency.setMaxLines(i3);
        try {
            obtain.setTextDirection(Build.VERSION.SDK_INT >= 29 ? this.l.getTextDirectionHeuristic() : (TextDirectionHeuristic) b((Object) this.l, "getTextDirectionHeuristic", TextDirectionHeuristics.FIRSTSTRONG_LTR));
        } catch (ClassCastException unused) {
            Log.w("ACTVAutoSizeHelper", "Failed to obtain TextDirectionHeuristic, auto size may be incorrect");
        }
        return obtain.build();
    }

    private static <T> T b(@NonNull Object obj, @NonNull String str, @NonNull T t) {
        try {
            return b(str).invoke(obj, new Object[0]);
        } catch (Exception e2) {
            Log.w("ACTVAutoSizeHelper", "Failed to invoke TextView#" + str + "() method", e2);
            return t;
        }
    }

    @Nullable
    private static Method b(@NonNull String str) {
        try {
            Method method = f505b.get(str);
            if (method == null && (method = TextView.class.getDeclaredMethod(str, new Class[0])) != null) {
                method.setAccessible(true);
                f505b.put(str, method);
            }
            return method;
        } catch (Exception e2) {
            Log.w("ACTVAutoSizeHelper", "Failed to retrieve TextView#" + str + "() method", e2);
            return null;
        }
    }

    private void h() {
        this.f507d = 0;
        this.g = -1.0f;
        this.h = -1.0f;
        this.f = -1.0f;
        this.i = new int[0];
        this.e = false;
    }

    private boolean i() {
        if (!k() || this.f507d != 1) {
            this.e = false;
        } else {
            if (!this.j || this.i.length == 0) {
                int floor = ((int) Math.floor((double) ((this.h - this.g) / this.f))) + 1;
                int[] iArr = new int[floor];
                for (int i2 = 0; i2 < floor; i2++) {
                    iArr[i2] = Math.round(this.g + (((float) i2) * this.f));
                }
                this.i = a(iArr);
            }
            this.e = true;
        }
        return this.e;
    }

    private boolean j() {
        int length = this.i.length;
        this.j = length > 0;
        if (this.j) {
            this.f507d = 1;
            int[] iArr = this.i;
            this.g = (float) iArr[0];
            this.h = (float) iArr[length - 1];
            this.f = -1.0f;
        }
        return this.j;
    }

    private boolean k() {
        return !(this.l instanceof C0113p);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public StaticLayout a(CharSequence charSequence, Layout.Alignment alignment, int i2, int i3) {
        int i4 = Build.VERSION.SDK_INT;
        return i4 >= 23 ? b(charSequence, alignment, i2, i3) : i4 >= 16 ? b(charSequence, alignment, i2) : a(charSequence, alignment, i2);
    }

    /* access modifiers changed from: package-private */
    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public void a() {
        if (g()) {
            if (this.e) {
                if (this.l.getMeasuredHeight() > 0 && this.l.getMeasuredWidth() > 0) {
                    int measuredWidth = Build.VERSION.SDK_INT >= 29 ? this.l.isHorizontallyScrollable() : ((Boolean) b((Object) this.l, "getHorizontallyScrolling", false)).booleanValue() ? ExtractorMediaSource.DEFAULT_LOADING_CHECK_INTERVAL_BYTES : (this.l.getMeasuredWidth() - this.l.getTotalPaddingLeft()) - this.l.getTotalPaddingRight();
                    int height = (this.l.getHeight() - this.l.getCompoundPaddingBottom()) - this.l.getCompoundPaddingTop();
                    if (measuredWidth > 0 && height > 0) {
                        synchronized (f504a) {
                            f504a.setEmpty();
                            f504a.right = (float) measuredWidth;
                            f504a.bottom = (float) height;
                            float a2 = (float) a(f504a);
                            if (a2 != this.l.getTextSize()) {
                                a(0, a2);
                            }
                        }
                    } else {
                        return;
                    }
                } else {
                    return;
                }
            }
            this.e = true;
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void a(int i2) {
        TextPaint textPaint = this.k;
        if (textPaint == null) {
            this.k = new TextPaint();
        } else {
            textPaint.reset();
        }
        this.k.set(this.l.getPaint());
        this.k.setTextSize((float) i2);
    }

    /* access modifiers changed from: package-private */
    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public void a(int i2, float f2) {
        Context context = this.m;
        a(TypedValue.applyDimension(i2, f2, (context == null ? Resources.getSystem() : context.getResources()).getDisplayMetrics()));
    }

    /* access modifiers changed from: package-private */
    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public void a(int i2, int i3, int i4, int i5) {
        if (k()) {
            DisplayMetrics displayMetrics = this.m.getResources().getDisplayMetrics();
            a(TypedValue.applyDimension(i5, (float) i2, displayMetrics), TypedValue.applyDimension(i5, (float) i3, displayMetrics), TypedValue.applyDimension(i5, (float) i4, displayMetrics));
            if (i()) {
                a();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void a(@Nullable AttributeSet attributeSet, int i2) {
        int resourceId;
        TypedArray obtainStyledAttributes = this.m.obtainStyledAttributes(attributeSet, j.AppCompatTextView, i2, 0);
        if (Build.VERSION.SDK_INT >= 29) {
            TextView textView = this.l;
            textView.saveAttributeDataForStyleable(textView.getContext(), j.AppCompatTextView, attributeSet, obtainStyledAttributes, i2, 0);
        }
        if (obtainStyledAttributes.hasValue(j.AppCompatTextView_autoSizeTextType)) {
            this.f507d = obtainStyledAttributes.getInt(j.AppCompatTextView_autoSizeTextType, 0);
        }
        float dimension = obtainStyledAttributes.hasValue(j.AppCompatTextView_autoSizeStepGranularity) ? obtainStyledAttributes.getDimension(j.AppCompatTextView_autoSizeStepGranularity, -1.0f) : -1.0f;
        float dimension2 = obtainStyledAttributes.hasValue(j.AppCompatTextView_autoSizeMinTextSize) ? obtainStyledAttributes.getDimension(j.AppCompatTextView_autoSizeMinTextSize, -1.0f) : -1.0f;
        float dimension3 = obtainStyledAttributes.hasValue(j.AppCompatTextView_autoSizeMaxTextSize) ? obtainStyledAttributes.getDimension(j.AppCompatTextView_autoSizeMaxTextSize, -1.0f) : -1.0f;
        if (obtainStyledAttributes.hasValue(j.AppCompatTextView_autoSizePresetSizes) && (resourceId = obtainStyledAttributes.getResourceId(j.AppCompatTextView_autoSizePresetSizes, 0)) > 0) {
            TypedArray obtainTypedArray = obtainStyledAttributes.getResources().obtainTypedArray(resourceId);
            a(obtainTypedArray);
            obtainTypedArray.recycle();
        }
        obtainStyledAttributes.recycle();
        if (!k()) {
            this.f507d = 0;
        } else if (this.f507d == 1) {
            if (!this.j) {
                DisplayMetrics displayMetrics = this.m.getResources().getDisplayMetrics();
                if (dimension2 == -1.0f) {
                    dimension2 = TypedValue.applyDimension(2, 12.0f, displayMetrics);
                }
                if (dimension3 == -1.0f) {
                    dimension3 = TypedValue.applyDimension(2, 112.0f, displayMetrics);
                }
                if (dimension == -1.0f) {
                    dimension = 1.0f;
                }
                a(dimension2, dimension3, dimension);
            }
            i();
        }
    }

    /* access modifiers changed from: package-private */
    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public void a(@NonNull int[] iArr, int i2) {
        if (k()) {
            int length = iArr.length;
            if (length > 0) {
                int[] iArr2 = new int[length];
                if (i2 == 0) {
                    iArr2 = Arrays.copyOf(iArr, length);
                } else {
                    DisplayMetrics displayMetrics = this.m.getResources().getDisplayMetrics();
                    for (int i3 = 0; i3 < length; i3++) {
                        iArr2[i3] = Math.round(TypedValue.applyDimension(i2, (float) iArr[i3], displayMetrics));
                    }
                }
                this.i = a(iArr2);
                if (!j()) {
                    throw new IllegalArgumentException("None of the preset sizes is valid: " + Arrays.toString(iArr));
                }
            } else {
                this.j = false;
            }
            if (i()) {
                a();
            }
        }
    }

    /* access modifiers changed from: package-private */
    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public int b() {
        return Math.round(this.h);
    }

    /* access modifiers changed from: package-private */
    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public void b(int i2) {
        if (!k()) {
            return;
        }
        if (i2 == 0) {
            h();
        } else if (i2 == 1) {
            DisplayMetrics displayMetrics = this.m.getResources().getDisplayMetrics();
            a(TypedValue.applyDimension(2, 12.0f, displayMetrics), TypedValue.applyDimension(2, 112.0f, displayMetrics), 1.0f);
            if (i()) {
                a();
            }
        } else {
            throw new IllegalArgumentException("Unknown auto-size text type: " + i2);
        }
    }

    /* access modifiers changed from: package-private */
    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public int c() {
        return Math.round(this.g);
    }

    /* access modifiers changed from: package-private */
    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public int d() {
        return Math.round(this.f);
    }

    /* access modifiers changed from: package-private */
    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public int[] e() {
        return this.i;
    }

    /* access modifiers changed from: package-private */
    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public int f() {
        return this.f507d;
    }

    /* access modifiers changed from: package-private */
    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public boolean g() {
        return k() && this.f507d != 0;
    }
}
