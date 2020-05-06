package androidx.appcompat.widget;

import a.a.j;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.core.content.res.g;
import androidx.core.widget.b;
import java.lang.ref.WeakReference;

class H {
    @NonNull

    /* renamed from: a  reason: collision with root package name */
    private final TextView f490a;

    /* renamed from: b  reason: collision with root package name */
    private ta f491b;

    /* renamed from: c  reason: collision with root package name */
    private ta f492c;

    /* renamed from: d  reason: collision with root package name */
    private ta f493d;
    private ta e;
    private ta f;
    private ta g;
    private ta h;
    @NonNull
    private final J i;
    private int j = 0;
    private int k = -1;
    private Typeface l;
    private boolean m;

    H(@NonNull TextView textView) {
        this.f490a = textView;
        this.i = new J(this.f490a);
    }

    private static ta a(Context context, C0112o oVar, int i2) {
        ColorStateList b2 = oVar.b(context, i2);
        if (b2 == null) {
            return null;
        }
        ta taVar = new ta();
        taVar.f662d = true;
        taVar.f659a = b2;
        return taVar;
    }

    private void a(Context context, va vaVar) {
        String d2;
        Typeface typeface;
        Typeface typeface2;
        this.j = vaVar.d(j.TextAppearance_android_textStyle, this.j);
        boolean z = false;
        if (Build.VERSION.SDK_INT >= 28) {
            this.k = vaVar.d(j.TextAppearance_android_textFontWeight, -1);
            if (this.k != -1) {
                this.j = (this.j & 2) | 0;
            }
        }
        if (vaVar.g(j.TextAppearance_android_fontFamily) || vaVar.g(j.TextAppearance_fontFamily)) {
            this.l = null;
            int i2 = vaVar.g(j.TextAppearance_fontFamily) ? j.TextAppearance_fontFamily : j.TextAppearance_android_fontFamily;
            int i3 = this.k;
            int i4 = this.j;
            if (!context.isRestricted()) {
                try {
                    Typeface a2 = vaVar.a(i2, this.j, (g.a) new G(this, i3, i4, new WeakReference(this.f490a)));
                    if (a2 != null) {
                        if (Build.VERSION.SDK_INT >= 28 && this.k != -1) {
                            a2 = Typeface.create(Typeface.create(a2, 0), this.k, (this.j & 2) != 0);
                        }
                        this.l = a2;
                    }
                    this.m = this.l == null;
                } catch (Resources.NotFoundException | UnsupportedOperationException unused) {
                }
            }
            if (this.l == null && (d2 = vaVar.d(i2)) != null) {
                if (Build.VERSION.SDK_INT < 28 || this.k == -1) {
                    typeface = Typeface.create(d2, this.j);
                } else {
                    Typeface create = Typeface.create(d2, 0);
                    int i5 = this.k;
                    if ((this.j & 2) != 0) {
                        z = true;
                    }
                    typeface = Typeface.create(create, i5, z);
                }
                this.l = typeface;
            }
        } else if (vaVar.g(j.TextAppearance_android_typeface)) {
            this.m = false;
            int d3 = vaVar.d(j.TextAppearance_android_typeface, 1);
            if (d3 == 1) {
                typeface2 = Typeface.SANS_SERIF;
            } else if (d3 == 2) {
                typeface2 = Typeface.SERIF;
            } else if (d3 == 3) {
                typeface2 = Typeface.MONOSPACE;
            } else {
                return;
            }
            this.l = typeface2;
        }
    }

    private void a(Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4, Drawable drawable5, Drawable drawable6) {
        if (Build.VERSION.SDK_INT >= 17 && (drawable5 != null || drawable6 != null)) {
            Drawable[] compoundDrawablesRelative = this.f490a.getCompoundDrawablesRelative();
            TextView textView = this.f490a;
            if (drawable5 == null) {
                drawable5 = compoundDrawablesRelative[0];
            }
            if (drawable2 == null) {
                drawable2 = compoundDrawablesRelative[1];
            }
            if (drawable6 == null) {
                drawable6 = compoundDrawablesRelative[2];
            }
            if (drawable4 == null) {
                drawable4 = compoundDrawablesRelative[3];
            }
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable5, drawable2, drawable6, drawable4);
        } else if (drawable != null || drawable2 != null || drawable3 != null || drawable4 != null) {
            if (Build.VERSION.SDK_INT >= 17) {
                Drawable[] compoundDrawablesRelative2 = this.f490a.getCompoundDrawablesRelative();
                if (!(compoundDrawablesRelative2[0] == null && compoundDrawablesRelative2[2] == null)) {
                    TextView textView2 = this.f490a;
                    Drawable drawable7 = compoundDrawablesRelative2[0];
                    if (drawable2 == null) {
                        drawable2 = compoundDrawablesRelative2[1];
                    }
                    Drawable drawable8 = compoundDrawablesRelative2[2];
                    if (drawable4 == null) {
                        drawable4 = compoundDrawablesRelative2[3];
                    }
                    textView2.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable7, drawable2, drawable8, drawable4);
                    return;
                }
            }
            Drawable[] compoundDrawables = this.f490a.getCompoundDrawables();
            TextView textView3 = this.f490a;
            if (drawable == null) {
                drawable = compoundDrawables[0];
            }
            if (drawable2 == null) {
                drawable2 = compoundDrawables[1];
            }
            if (drawable3 == null) {
                drawable3 = compoundDrawables[2];
            }
            if (drawable4 == null) {
                drawable4 = compoundDrawables[3];
            }
            textView3.setCompoundDrawablesWithIntrinsicBounds(drawable, drawable2, drawable3, drawable4);
        }
    }

    private void a(Drawable drawable, ta taVar) {
        if (drawable != null && taVar != null) {
            C0112o.a(drawable, taVar, this.f490a.getDrawableState());
        }
    }

    private void b(int i2, float f2) {
        this.i.a(i2, f2);
    }

    private void l() {
        ta taVar = this.h;
        this.f491b = taVar;
        this.f492c = taVar;
        this.f493d = taVar;
        this.e = taVar;
        this.f = taVar;
        this.g = taVar;
    }

    /* access modifiers changed from: package-private */
    public void a() {
        if (!(this.f491b == null && this.f492c == null && this.f493d == null && this.e == null)) {
            Drawable[] compoundDrawables = this.f490a.getCompoundDrawables();
            a(compoundDrawables[0], this.f491b);
            a(compoundDrawables[1], this.f492c);
            a(compoundDrawables[2], this.f493d);
            a(compoundDrawables[3], this.e);
        }
        if (Build.VERSION.SDK_INT < 17) {
            return;
        }
        if (this.f != null || this.g != null) {
            Drawable[] compoundDrawablesRelative = this.f490a.getCompoundDrawablesRelative();
            a(compoundDrawablesRelative[0], this.f);
            a(compoundDrawablesRelative[2], this.g);
        }
    }

    /* access modifiers changed from: package-private */
    public void a(int i2) {
        this.i.b(i2);
    }

    /* access modifiers changed from: package-private */
    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public void a(int i2, float f2) {
        if (!b.f853a && !j()) {
            b(i2, f2);
        }
    }

    /* access modifiers changed from: package-private */
    public void a(int i2, int i3, int i4, int i5) {
        this.i.a(i2, i3, i4, i5);
    }

    /* access modifiers changed from: package-private */
    public void a(Context context, int i2) {
        String d2;
        ColorStateList a2;
        va a3 = va.a(context, i2, j.TextAppearance);
        if (a3.g(j.TextAppearance_textAllCaps)) {
            a(a3.a(j.TextAppearance_textAllCaps, false));
        }
        if (Build.VERSION.SDK_INT < 23 && a3.g(j.TextAppearance_android_textColor) && (a2 = a3.a(j.TextAppearance_android_textColor)) != null) {
            this.f490a.setTextColor(a2);
        }
        if (a3.g(j.TextAppearance_android_textSize) && a3.c(j.TextAppearance_android_textSize, -1) == 0) {
            this.f490a.setTextSize(0, 0.0f);
        }
        a(context, a3);
        if (Build.VERSION.SDK_INT >= 26 && a3.g(j.TextAppearance_fontVariationSettings) && (d2 = a3.d(j.TextAppearance_fontVariationSettings)) != null) {
            this.f490a.setFontVariationSettings(d2);
        }
        a3.b();
        Typeface typeface = this.l;
        if (typeface != null) {
            this.f490a.setTypeface(typeface, this.j);
        }
    }

    /* access modifiers changed from: package-private */
    public void a(@Nullable ColorStateList colorStateList) {
        if (this.h == null) {
            this.h = new ta();
        }
        ta taVar = this.h;
        taVar.f659a = colorStateList;
        taVar.f662d = colorStateList != null;
        l();
    }

    /* access modifiers changed from: package-private */
    public void a(@Nullable PorterDuff.Mode mode) {
        if (this.h == null) {
            this.h = new ta();
        }
        ta taVar = this.h;
        taVar.f660b = mode;
        taVar.f661c = mode != null;
        l();
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x012a  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x0131  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x013e  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0145  */
    @android.annotation.SuppressLint({"NewApi"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void a(@androidx.annotation.Nullable android.util.AttributeSet r19, int r20) {
        /*
            r18 = this;
            r7 = r18
            r8 = r19
            r9 = r20
            android.widget.TextView r0 = r7.f490a
            android.content.Context r10 = r0.getContext()
            androidx.appcompat.widget.o r11 = androidx.appcompat.widget.C0112o.b()
            int[] r0 = a.a.j.AppCompatTextHelper
            r12 = 0
            androidx.appcompat.widget.va r13 = androidx.appcompat.widget.va.a(r10, r8, r0, r9, r12)
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 29
            if (r0 < r1) goto L_0x0031
            android.widget.TextView r0 = r7.f490a
            android.content.Context r1 = r0.getContext()
            int[] r2 = a.a.j.AppCompatTextHelper
            android.content.res.TypedArray r4 = r13.a()
            r6 = 0
            r3 = r19
            r5 = r20
            r0.saveAttributeDataForStyleable(r1, r2, r3, r4, r5, r6)
        L_0x0031:
            int r0 = a.a.j.AppCompatTextHelper_android_textAppearance
            r14 = -1
            int r0 = r13.g(r0, r14)
            int r1 = a.a.j.AppCompatTextHelper_android_drawableLeft
            boolean r1 = r13.g(r1)
            if (r1 == 0) goto L_0x004c
            int r1 = a.a.j.AppCompatTextHelper_android_drawableLeft
            int r1 = r13.g(r1, r12)
            androidx.appcompat.widget.ta r1 = a(r10, r11, r1)
            r7.f491b = r1
        L_0x004c:
            int r1 = a.a.j.AppCompatTextHelper_android_drawableTop
            boolean r1 = r13.g(r1)
            if (r1 == 0) goto L_0x0060
            int r1 = a.a.j.AppCompatTextHelper_android_drawableTop
            int r1 = r13.g(r1, r12)
            androidx.appcompat.widget.ta r1 = a(r10, r11, r1)
            r7.f492c = r1
        L_0x0060:
            int r1 = a.a.j.AppCompatTextHelper_android_drawableRight
            boolean r1 = r13.g(r1)
            if (r1 == 0) goto L_0x0074
            int r1 = a.a.j.AppCompatTextHelper_android_drawableRight
            int r1 = r13.g(r1, r12)
            androidx.appcompat.widget.ta r1 = a(r10, r11, r1)
            r7.f493d = r1
        L_0x0074:
            int r1 = a.a.j.AppCompatTextHelper_android_drawableBottom
            boolean r1 = r13.g(r1)
            if (r1 == 0) goto L_0x0088
            int r1 = a.a.j.AppCompatTextHelper_android_drawableBottom
            int r1 = r13.g(r1, r12)
            androidx.appcompat.widget.ta r1 = a(r10, r11, r1)
            r7.e = r1
        L_0x0088:
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 17
            if (r1 < r2) goto L_0x00b6
            int r1 = a.a.j.AppCompatTextHelper_android_drawableStart
            boolean r1 = r13.g(r1)
            if (r1 == 0) goto L_0x00a2
            int r1 = a.a.j.AppCompatTextHelper_android_drawableStart
            int r1 = r13.g(r1, r12)
            androidx.appcompat.widget.ta r1 = a(r10, r11, r1)
            r7.f = r1
        L_0x00a2:
            int r1 = a.a.j.AppCompatTextHelper_android_drawableEnd
            boolean r1 = r13.g(r1)
            if (r1 == 0) goto L_0x00b6
            int r1 = a.a.j.AppCompatTextHelper_android_drawableEnd
            int r1 = r13.g(r1, r12)
            androidx.appcompat.widget.ta r1 = a(r10, r11, r1)
            r7.g = r1
        L_0x00b6:
            r13.b()
            android.widget.TextView r1 = r7.f490a
            android.text.method.TransformationMethod r1 = r1.getTransformationMethod()
            boolean r1 = r1 instanceof android.text.method.PasswordTransformationMethod
            r2 = 26
            r4 = 23
            if (r0 == r14) goto L_0x014a
            int[] r5 = a.a.j.TextAppearance
            androidx.appcompat.widget.va r0 = androidx.appcompat.widget.va.a((android.content.Context) r10, (int) r0, (int[]) r5)
            if (r1 != 0) goto L_0x00e0
            int r5 = a.a.j.TextAppearance_textAllCaps
            boolean r5 = r0.g(r5)
            if (r5 == 0) goto L_0x00e0
            int r5 = a.a.j.TextAppearance_textAllCaps
            boolean r5 = r0.a((int) r5, (boolean) r12)
            r6 = r5
            r5 = 1
            goto L_0x00e2
        L_0x00e0:
            r5 = r12
            r6 = r5
        L_0x00e2:
            r7.a((android.content.Context) r10, (androidx.appcompat.widget.va) r0)
            int r15 = android.os.Build.VERSION.SDK_INT
            if (r15 >= r4) goto L_0x011f
            int r15 = a.a.j.TextAppearance_android_textColor
            boolean r15 = r0.g(r15)
            if (r15 == 0) goto L_0x00f8
            int r15 = a.a.j.TextAppearance_android_textColor
            android.content.res.ColorStateList r15 = r0.a(r15)
            goto L_0x00f9
        L_0x00f8:
            r15 = 0
        L_0x00f9:
            int r3 = a.a.j.TextAppearance_android_textColorHint
            boolean r3 = r0.g(r3)
            if (r3 == 0) goto L_0x0108
            int r3 = a.a.j.TextAppearance_android_textColorHint
            android.content.res.ColorStateList r3 = r0.a(r3)
            goto L_0x0109
        L_0x0108:
            r3 = 0
        L_0x0109:
            int r13 = a.a.j.TextAppearance_android_textColorLink
            boolean r13 = r0.g(r13)
            if (r13 == 0) goto L_0x011d
            int r13 = a.a.j.TextAppearance_android_textColorLink
            android.content.res.ColorStateList r13 = r0.a(r13)
            r17 = r15
            r15 = r13
            r13 = r17
            goto L_0x0122
        L_0x011d:
            r13 = r15
            goto L_0x0121
        L_0x011f:
            r3 = 0
            r13 = 0
        L_0x0121:
            r15 = 0
        L_0x0122:
            int r14 = a.a.j.TextAppearance_textLocale
            boolean r14 = r0.g(r14)
            if (r14 == 0) goto L_0x0131
            int r14 = a.a.j.TextAppearance_textLocale
            java.lang.String r14 = r0.d(r14)
            goto L_0x0132
        L_0x0131:
            r14 = 0
        L_0x0132:
            int r4 = android.os.Build.VERSION.SDK_INT
            if (r4 < r2) goto L_0x0145
            int r4 = a.a.j.TextAppearance_fontVariationSettings
            boolean r4 = r0.g(r4)
            if (r4 == 0) goto L_0x0145
            int r4 = a.a.j.TextAppearance_fontVariationSettings
            java.lang.String r4 = r0.d(r4)
            goto L_0x0146
        L_0x0145:
            r4 = 0
        L_0x0146:
            r0.b()
            goto L_0x0151
        L_0x014a:
            r5 = r12
            r6 = r5
            r3 = 0
            r4 = 0
            r13 = 0
            r14 = 0
            r15 = 0
        L_0x0151:
            int[] r0 = a.a.j.TextAppearance
            androidx.appcompat.widget.va r0 = androidx.appcompat.widget.va.a(r10, r8, r0, r9, r12)
            if (r1 != 0) goto L_0x016a
            int r2 = a.a.j.TextAppearance_textAllCaps
            boolean r2 = r0.g(r2)
            if (r2 == 0) goto L_0x016a
            int r2 = a.a.j.TextAppearance_textAllCaps
            boolean r6 = r0.a((int) r2, (boolean) r12)
            r16 = 1
            goto L_0x016c
        L_0x016a:
            r16 = r5
        L_0x016c:
            int r2 = android.os.Build.VERSION.SDK_INT
            r5 = 23
            if (r2 >= r5) goto L_0x019c
            int r2 = a.a.j.TextAppearance_android_textColor
            boolean r2 = r0.g(r2)
            if (r2 == 0) goto L_0x0180
            int r2 = a.a.j.TextAppearance_android_textColor
            android.content.res.ColorStateList r13 = r0.a(r2)
        L_0x0180:
            int r2 = a.a.j.TextAppearance_android_textColorHint
            boolean r2 = r0.g(r2)
            if (r2 == 0) goto L_0x018e
            int r2 = a.a.j.TextAppearance_android_textColorHint
            android.content.res.ColorStateList r3 = r0.a(r2)
        L_0x018e:
            int r2 = a.a.j.TextAppearance_android_textColorLink
            boolean r2 = r0.g(r2)
            if (r2 == 0) goto L_0x019c
            int r2 = a.a.j.TextAppearance_android_textColorLink
            android.content.res.ColorStateList r15 = r0.a(r2)
        L_0x019c:
            int r2 = a.a.j.TextAppearance_textLocale
            boolean r2 = r0.g(r2)
            if (r2 == 0) goto L_0x01aa
            int r2 = a.a.j.TextAppearance_textLocale
            java.lang.String r14 = r0.d(r2)
        L_0x01aa:
            int r2 = android.os.Build.VERSION.SDK_INT
            r5 = 26
            if (r2 < r5) goto L_0x01be
            int r2 = a.a.j.TextAppearance_fontVariationSettings
            boolean r2 = r0.g(r2)
            if (r2 == 0) goto L_0x01be
            int r2 = a.a.j.TextAppearance_fontVariationSettings
            java.lang.String r4 = r0.d(r2)
        L_0x01be:
            int r2 = android.os.Build.VERSION.SDK_INT
            r5 = 28
            if (r2 < r5) goto L_0x01db
            int r2 = a.a.j.TextAppearance_android_textSize
            boolean r2 = r0.g(r2)
            if (r2 == 0) goto L_0x01db
            int r2 = a.a.j.TextAppearance_android_textSize
            r5 = -1
            int r2 = r0.c(r2, r5)
            if (r2 != 0) goto L_0x01db
            android.widget.TextView r2 = r7.f490a
            r5 = 0
            r2.setTextSize(r12, r5)
        L_0x01db:
            r7.a((android.content.Context) r10, (androidx.appcompat.widget.va) r0)
            r0.b()
            if (r13 == 0) goto L_0x01e8
            android.widget.TextView r0 = r7.f490a
            r0.setTextColor(r13)
        L_0x01e8:
            if (r3 == 0) goto L_0x01ef
            android.widget.TextView r0 = r7.f490a
            r0.setHintTextColor(r3)
        L_0x01ef:
            if (r15 == 0) goto L_0x01f6
            android.widget.TextView r0 = r7.f490a
            r0.setLinkTextColor(r15)
        L_0x01f6:
            if (r1 != 0) goto L_0x01fd
            if (r16 == 0) goto L_0x01fd
            r7.a((boolean) r6)
        L_0x01fd:
            android.graphics.Typeface r0 = r7.l
            if (r0 == 0) goto L_0x0213
            int r1 = r7.k
            r2 = -1
            if (r1 != r2) goto L_0x020e
            android.widget.TextView r1 = r7.f490a
            int r2 = r7.j
            r1.setTypeface(r0, r2)
            goto L_0x0213
        L_0x020e:
            android.widget.TextView r1 = r7.f490a
            r1.setTypeface(r0)
        L_0x0213:
            if (r4 == 0) goto L_0x021a
            android.widget.TextView r0 = r7.f490a
            r0.setFontVariationSettings(r4)
        L_0x021a:
            if (r14 == 0) goto L_0x0243
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 24
            if (r0 < r1) goto L_0x022c
            android.widget.TextView r0 = r7.f490a
            android.os.LocaleList r1 = android.os.LocaleList.forLanguageTags(r14)
            r0.setTextLocales(r1)
            goto L_0x0243
        L_0x022c:
            r1 = 21
            if (r0 < r1) goto L_0x0243
            r0 = 44
            int r0 = r14.indexOf(r0)
            java.lang.String r0 = r14.substring(r12, r0)
            android.widget.TextView r1 = r7.f490a
            java.util.Locale r0 = java.util.Locale.forLanguageTag(r0)
            r1.setTextLocale(r0)
        L_0x0243:
            androidx.appcompat.widget.J r0 = r7.i
            r0.a((android.util.AttributeSet) r8, (int) r9)
            boolean r0 = androidx.core.widget.b.f853a
            if (r0 == 0) goto L_0x0287
            androidx.appcompat.widget.J r0 = r7.i
            int r0 = r0.f()
            if (r0 == 0) goto L_0x0287
            androidx.appcompat.widget.J r0 = r7.i
            int[] r0 = r0.e()
            int r1 = r0.length
            if (r1 <= 0) goto L_0x0287
            android.widget.TextView r1 = r7.f490a
            int r1 = r1.getAutoSizeStepGranularity()
            float r1 = (float) r1
            r2 = -1082130432(0xffffffffbf800000, float:-1.0)
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 == 0) goto L_0x0282
            android.widget.TextView r0 = r7.f490a
            androidx.appcompat.widget.J r1 = r7.i
            int r1 = r1.c()
            androidx.appcompat.widget.J r2 = r7.i
            int r2 = r2.b()
            androidx.appcompat.widget.J r3 = r7.i
            int r3 = r3.d()
            r0.setAutoSizeTextTypeUniformWithConfiguration(r1, r2, r3, r12)
            goto L_0x0287
        L_0x0282:
            android.widget.TextView r1 = r7.f490a
            r1.setAutoSizeTextTypeUniformWithPresetSizes(r0, r12)
        L_0x0287:
            int[] r0 = a.a.j.AppCompatTextView
            androidx.appcompat.widget.va r8 = androidx.appcompat.widget.va.a((android.content.Context) r10, (android.util.AttributeSet) r8, (int[]) r0)
            int r0 = a.a.j.AppCompatTextView_drawableLeftCompat
            r1 = -1
            int r0 = r8.g(r0, r1)
            if (r0 == r1) goto L_0x029c
            android.graphics.drawable.Drawable r0 = r11.a((android.content.Context) r10, (int) r0)
            r2 = r0
            goto L_0x029d
        L_0x029c:
            r2 = 0
        L_0x029d:
            int r0 = a.a.j.AppCompatTextView_drawableTopCompat
            int r0 = r8.g(r0, r1)
            if (r0 == r1) goto L_0x02ab
            android.graphics.drawable.Drawable r0 = r11.a((android.content.Context) r10, (int) r0)
            r3 = r0
            goto L_0x02ac
        L_0x02ab:
            r3 = 0
        L_0x02ac:
            int r0 = a.a.j.AppCompatTextView_drawableRightCompat
            int r0 = r8.g(r0, r1)
            if (r0 == r1) goto L_0x02ba
            android.graphics.drawable.Drawable r0 = r11.a((android.content.Context) r10, (int) r0)
            r4 = r0
            goto L_0x02bb
        L_0x02ba:
            r4 = 0
        L_0x02bb:
            int r0 = a.a.j.AppCompatTextView_drawableBottomCompat
            int r0 = r8.g(r0, r1)
            if (r0 == r1) goto L_0x02c9
            android.graphics.drawable.Drawable r0 = r11.a((android.content.Context) r10, (int) r0)
            r5 = r0
            goto L_0x02ca
        L_0x02c9:
            r5 = 0
        L_0x02ca:
            int r0 = a.a.j.AppCompatTextView_drawableStartCompat
            int r0 = r8.g(r0, r1)
            if (r0 == r1) goto L_0x02d8
            android.graphics.drawable.Drawable r0 = r11.a((android.content.Context) r10, (int) r0)
            r6 = r0
            goto L_0x02d9
        L_0x02d8:
            r6 = 0
        L_0x02d9:
            int r0 = a.a.j.AppCompatTextView_drawableEndCompat
            int r0 = r8.g(r0, r1)
            if (r0 == r1) goto L_0x02e7
            android.graphics.drawable.Drawable r0 = r11.a((android.content.Context) r10, (int) r0)
            r9 = r0
            goto L_0x02e8
        L_0x02e7:
            r9 = 0
        L_0x02e8:
            r0 = r18
            r1 = r2
            r2 = r3
            r3 = r4
            r4 = r5
            r5 = r6
            r6 = r9
            r0.a(r1, r2, r3, r4, r5, r6)
            int r0 = a.a.j.AppCompatTextView_drawableTint
            boolean r0 = r8.g(r0)
            if (r0 == 0) goto L_0x0306
            int r0 = a.a.j.AppCompatTextView_drawableTint
            android.content.res.ColorStateList r0 = r8.a(r0)
            android.widget.TextView r1 = r7.f490a
            androidx.core.widget.TextViewCompat.a((android.widget.TextView) r1, (android.content.res.ColorStateList) r0)
        L_0x0306:
            int r0 = a.a.j.AppCompatTextView_drawableTintMode
            boolean r0 = r8.g(r0)
            if (r0 == 0) goto L_0x0320
            int r0 = a.a.j.AppCompatTextView_drawableTintMode
            r1 = -1
            int r0 = r8.d(r0, r1)
            r2 = 0
            android.graphics.PorterDuff$Mode r0 = androidx.appcompat.widget.N.a(r0, r2)
            android.widget.TextView r2 = r7.f490a
            androidx.core.widget.TextViewCompat.a((android.widget.TextView) r2, (android.graphics.PorterDuff.Mode) r0)
            goto L_0x0321
        L_0x0320:
            r1 = -1
        L_0x0321:
            int r0 = a.a.j.AppCompatTextView_firstBaselineToTopHeight
            int r0 = r8.c(r0, r1)
            int r2 = a.a.j.AppCompatTextView_lastBaselineToBottomHeight
            int r2 = r8.c(r2, r1)
            int r3 = a.a.j.AppCompatTextView_lineHeight
            int r3 = r8.c(r3, r1)
            r8.b()
            if (r0 == r1) goto L_0x033d
            android.widget.TextView r4 = r7.f490a
            androidx.core.widget.TextViewCompat.a((android.widget.TextView) r4, (int) r0)
        L_0x033d:
            if (r2 == r1) goto L_0x0344
            android.widget.TextView r0 = r7.f490a
            androidx.core.widget.TextViewCompat.b(r0, r2)
        L_0x0344:
            if (r3 == r1) goto L_0x034b
            android.widget.TextView r0 = r7.f490a
            androidx.core.widget.TextViewCompat.c(r0, r3)
        L_0x034b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.appcompat.widget.H.a(android.util.AttributeSet, int):void");
    }

    /* access modifiers changed from: package-private */
    public void a(WeakReference<TextView> weakReference, Typeface typeface) {
        if (this.m) {
            this.l = typeface;
            TextView textView = (TextView) weakReference.get();
            if (textView != null) {
                textView.setTypeface(typeface, this.j);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void a(boolean z) {
        this.f490a.setAllCaps(z);
    }

    /* access modifiers changed from: package-private */
    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public void a(boolean z, int i2, int i3, int i4, int i5) {
        if (!b.f853a) {
            b();
        }
    }

    /* access modifiers changed from: package-private */
    public void a(@NonNull int[] iArr, int i2) {
        this.i.a(iArr, i2);
    }

    /* access modifiers changed from: package-private */
    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public void b() {
        this.i.a();
    }

    /* access modifiers changed from: package-private */
    public int c() {
        return this.i.b();
    }

    /* access modifiers changed from: package-private */
    public int d() {
        return this.i.c();
    }

    /* access modifiers changed from: package-private */
    public int e() {
        return this.i.d();
    }

    /* access modifiers changed from: package-private */
    public int[] f() {
        return this.i.e();
    }

    /* access modifiers changed from: package-private */
    public int g() {
        return this.i.f();
    }

    /* access modifiers changed from: package-private */
    @Nullable
    public ColorStateList h() {
        ta taVar = this.h;
        if (taVar != null) {
            return taVar.f659a;
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    @Nullable
    public PorterDuff.Mode i() {
        ta taVar = this.h;
        if (taVar != null) {
            return taVar.f660b;
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public boolean j() {
        return this.i.g();
    }

    /* access modifiers changed from: package-private */
    public void k() {
        a();
    }
}
