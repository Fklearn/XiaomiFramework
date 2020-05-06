package a.d.d;

import a.d.e.c;
import android.annotation.SuppressLint;
import android.os.Build;
import android.text.PrecomputedText;
import android.text.Spannable;
import android.text.TextDirectionHeuristic;
import android.text.TextDirectionHeuristics;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.MetricAffectingSpan;
import androidx.annotation.GuardedBy;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.RestrictTo;
import java.util.concurrent.Executor;

public class a implements Spannable {

    /* renamed from: a  reason: collision with root package name */
    private static final Object f121a = new Object();
    @GuardedBy("sLock")
    @NonNull

    /* renamed from: b  reason: collision with root package name */
    private static Executor f122b = null;
    @NonNull

    /* renamed from: c  reason: collision with root package name */
    private final Spannable f123c;
    @NonNull

    /* renamed from: d  reason: collision with root package name */
    private final C0003a f124d;
    @Nullable
    private final PrecomputedText e;

    /* renamed from: a.d.d.a$a  reason: collision with other inner class name */
    public static final class C0003a {
        @NonNull

        /* renamed from: a  reason: collision with root package name */
        private final TextPaint f125a;
        @Nullable

        /* renamed from: b  reason: collision with root package name */
        private final TextDirectionHeuristic f126b;

        /* renamed from: c  reason: collision with root package name */
        private final int f127c;

        /* renamed from: d  reason: collision with root package name */
        private final int f128d;
        final PrecomputedText.Params e;

        /* renamed from: a.d.d.a$a$a  reason: collision with other inner class name */
        public static class C0004a {
            @NonNull

            /* renamed from: a  reason: collision with root package name */
            private final TextPaint f129a;

            /* renamed from: b  reason: collision with root package name */
            private TextDirectionHeuristic f130b;

            /* renamed from: c  reason: collision with root package name */
            private int f131c;

            /* renamed from: d  reason: collision with root package name */
            private int f132d;

            public C0004a(@NonNull TextPaint textPaint) {
                this.f129a = textPaint;
                if (Build.VERSION.SDK_INT >= 23) {
                    this.f131c = 1;
                    this.f132d = 1;
                } else {
                    this.f132d = 0;
                    this.f131c = 0;
                }
                this.f130b = Build.VERSION.SDK_INT >= 18 ? TextDirectionHeuristics.FIRSTSTRONG_LTR : null;
            }

            @RequiresApi(23)
            public C0004a a(int i) {
                this.f131c = i;
                return this;
            }

            @RequiresApi(18)
            public C0004a a(@NonNull TextDirectionHeuristic textDirectionHeuristic) {
                this.f130b = textDirectionHeuristic;
                return this;
            }

            @NonNull
            public C0003a a() {
                return new C0003a(this.f129a, this.f130b, this.f131c, this.f132d);
            }

            @RequiresApi(23)
            public C0004a b(int i) {
                this.f132d = i;
                return this;
            }
        }

        @RequiresApi(28)
        public C0003a(@NonNull PrecomputedText.Params params) {
            this.f125a = params.getTextPaint();
            this.f126b = params.getTextDirection();
            this.f127c = params.getBreakStrategy();
            this.f128d = params.getHyphenationFrequency();
            this.e = Build.VERSION.SDK_INT < 29 ? null : params;
        }

        @SuppressLint({"NewApi"})
        C0003a(@NonNull TextPaint textPaint, @NonNull TextDirectionHeuristic textDirectionHeuristic, int i, int i2) {
            this.e = Build.VERSION.SDK_INT >= 29 ? new PrecomputedText.Params.Builder(textPaint).setBreakStrategy(i).setHyphenationFrequency(i2).setTextDirection(textDirectionHeuristic).build() : null;
            this.f125a = textPaint;
            this.f126b = textDirectionHeuristic;
            this.f127c = i;
            this.f128d = i2;
        }

        @RequiresApi(23)
        public int a() {
            return this.f127c;
        }

        @RestrictTo({RestrictTo.a.f224c})
        public boolean a(@NonNull C0003a aVar) {
            if ((Build.VERSION.SDK_INT >= 23 && (this.f127c != aVar.a() || this.f128d != aVar.b())) || this.f125a.getTextSize() != aVar.d().getTextSize() || this.f125a.getTextScaleX() != aVar.d().getTextScaleX() || this.f125a.getTextSkewX() != aVar.d().getTextSkewX()) {
                return false;
            }
            if ((Build.VERSION.SDK_INT >= 21 && (this.f125a.getLetterSpacing() != aVar.d().getLetterSpacing() || !TextUtils.equals(this.f125a.getFontFeatureSettings(), aVar.d().getFontFeatureSettings()))) || this.f125a.getFlags() != aVar.d().getFlags()) {
                return false;
            }
            int i = Build.VERSION.SDK_INT;
            if (i >= 24) {
                if (!this.f125a.getTextLocales().equals(aVar.d().getTextLocales())) {
                    return false;
                }
            } else if (i >= 17 && !this.f125a.getTextLocale().equals(aVar.d().getTextLocale())) {
                return false;
            }
            return this.f125a.getTypeface() == null ? aVar.d().getTypeface() == null : this.f125a.getTypeface().equals(aVar.d().getTypeface());
        }

        @RequiresApi(23)
        public int b() {
            return this.f128d;
        }

        @RequiresApi(18)
        @Nullable
        public TextDirectionHeuristic c() {
            return this.f126b;
        }

        @NonNull
        public TextPaint d() {
            return this.f125a;
        }

        public boolean equals(@Nullable Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof C0003a)) {
                return false;
            }
            C0003a aVar = (C0003a) obj;
            if (!a(aVar)) {
                return false;
            }
            return Build.VERSION.SDK_INT < 18 || this.f126b == aVar.c();
        }

        public int hashCode() {
            int i = Build.VERSION.SDK_INT;
            return c.a(i >= 24 ? new Object[]{Float.valueOf(this.f125a.getTextSize()), Float.valueOf(this.f125a.getTextScaleX()), Float.valueOf(this.f125a.getTextSkewX()), Float.valueOf(this.f125a.getLetterSpacing()), Integer.valueOf(this.f125a.getFlags()), this.f125a.getTextLocales(), this.f125a.getTypeface(), Boolean.valueOf(this.f125a.isElegantTextHeight()), this.f126b, Integer.valueOf(this.f127c), Integer.valueOf(this.f128d)} : i >= 21 ? new Object[]{Float.valueOf(this.f125a.getTextSize()), Float.valueOf(this.f125a.getTextScaleX()), Float.valueOf(this.f125a.getTextSkewX()), Float.valueOf(this.f125a.getLetterSpacing()), Integer.valueOf(this.f125a.getFlags()), this.f125a.getTextLocale(), this.f125a.getTypeface(), Boolean.valueOf(this.f125a.isElegantTextHeight()), this.f126b, Integer.valueOf(this.f127c), Integer.valueOf(this.f128d)} : i >= 18 ? new Object[]{Float.valueOf(this.f125a.getTextSize()), Float.valueOf(this.f125a.getTextScaleX()), Float.valueOf(this.f125a.getTextSkewX()), Integer.valueOf(this.f125a.getFlags()), this.f125a.getTextLocale(), this.f125a.getTypeface(), this.f126b, Integer.valueOf(this.f127c), Integer.valueOf(this.f128d)} : i >= 17 ? new Object[]{Float.valueOf(this.f125a.getTextSize()), Float.valueOf(this.f125a.getTextScaleX()), Float.valueOf(this.f125a.getTextSkewX()), Integer.valueOf(this.f125a.getFlags()), this.f125a.getTextLocale(), this.f125a.getTypeface(), this.f126b, Integer.valueOf(this.f127c), Integer.valueOf(this.f128d)} : new Object[]{Float.valueOf(this.f125a.getTextSize()), Float.valueOf(this.f125a.getTextScaleX()), Float.valueOf(this.f125a.getTextSkewX()), Integer.valueOf(this.f125a.getFlags()), this.f125a.getTypeface(), this.f126b, Integer.valueOf(this.f127c), Integer.valueOf(this.f128d)});
        }

        /* JADX WARNING: Removed duplicated region for block: B:12:0x00e3  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public java.lang.String toString() {
            /*
                r4 = this;
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                java.lang.String r1 = "{"
                r0.<init>(r1)
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = "textSize="
                r1.append(r2)
                android.text.TextPaint r2 = r4.f125a
                float r2 = r2.getTextSize()
                r1.append(r2)
                java.lang.String r1 = r1.toString()
                r0.append(r1)
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = ", textScaleX="
                r1.append(r2)
                android.text.TextPaint r2 = r4.f125a
                float r2 = r2.getTextScaleX()
                r1.append(r2)
                java.lang.String r1 = r1.toString()
                r0.append(r1)
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = ", textSkewX="
                r1.append(r2)
                android.text.TextPaint r2 = r4.f125a
                float r2 = r2.getTextSkewX()
                r1.append(r2)
                java.lang.String r1 = r1.toString()
                r0.append(r1)
                int r1 = android.os.Build.VERSION.SDK_INT
                r2 = 21
                if (r1 < r2) goto L_0x008f
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = ", letterSpacing="
                r1.append(r2)
                android.text.TextPaint r2 = r4.f125a
                float r2 = r2.getLetterSpacing()
                r1.append(r2)
                java.lang.String r1 = r1.toString()
                r0.append(r1)
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = ", elegantTextHeight="
                r1.append(r2)
                android.text.TextPaint r2 = r4.f125a
                boolean r2 = r2.isElegantTextHeight()
                r1.append(r2)
                java.lang.String r1 = r1.toString()
                r0.append(r1)
            L_0x008f:
                int r1 = android.os.Build.VERSION.SDK_INT
                r2 = 24
                java.lang.String r3 = ", textLocale="
                if (r1 < r2) goto L_0x00b0
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                r1.append(r3)
                android.text.TextPaint r2 = r4.f125a
                android.os.LocaleList r2 = r2.getTextLocales()
            L_0x00a5:
                r1.append(r2)
                java.lang.String r1 = r1.toString()
                r0.append(r1)
                goto L_0x00c3
            L_0x00b0:
                r2 = 17
                if (r1 < r2) goto L_0x00c3
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                r1.append(r3)
                android.text.TextPaint r2 = r4.f125a
                java.util.Locale r2 = r2.getTextLocale()
                goto L_0x00a5
            L_0x00c3:
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = ", typeface="
                r1.append(r2)
                android.text.TextPaint r2 = r4.f125a
                android.graphics.Typeface r2 = r2.getTypeface()
                r1.append(r2)
                java.lang.String r1 = r1.toString()
                r0.append(r1)
                int r1 = android.os.Build.VERSION.SDK_INT
                r2 = 26
                if (r1 < r2) goto L_0x00fd
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = ", variationSettings="
                r1.append(r2)
                android.text.TextPaint r2 = r4.f125a
                java.lang.String r2 = r2.getFontVariationSettings()
                r1.append(r2)
                java.lang.String r1 = r1.toString()
                r0.append(r1)
            L_0x00fd:
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = ", textDir="
                r1.append(r2)
                android.text.TextDirectionHeuristic r2 = r4.f126b
                r1.append(r2)
                java.lang.String r1 = r1.toString()
                r0.append(r1)
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = ", breakStrategy="
                r1.append(r2)
                int r2 = r4.f127c
                r1.append(r2)
                java.lang.String r1 = r1.toString()
                r0.append(r1)
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = ", hyphenationFrequency="
                r1.append(r2)
                int r2 = r4.f128d
                r1.append(r2)
                java.lang.String r1 = r1.toString()
                r0.append(r1)
                java.lang.String r1 = "}"
                r0.append(r1)
                java.lang.String r0 = r0.toString()
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: a.d.d.a.C0003a.toString():java.lang.String");
        }
    }

    @NonNull
    public C0003a a() {
        return this.f124d;
    }

    @RequiresApi(28)
    @Nullable
    @RestrictTo({RestrictTo.a.f224c})
    public PrecomputedText b() {
        Spannable spannable = this.f123c;
        if (spannable instanceof PrecomputedText) {
            return (PrecomputedText) spannable;
        }
        return null;
    }

    public char charAt(int i) {
        return this.f123c.charAt(i);
    }

    public int getSpanEnd(Object obj) {
        return this.f123c.getSpanEnd(obj);
    }

    public int getSpanFlags(Object obj) {
        return this.f123c.getSpanFlags(obj);
    }

    public int getSpanStart(Object obj) {
        return this.f123c.getSpanStart(obj);
    }

    @SuppressLint({"NewApi"})
    public <T> T[] getSpans(int i, int i2, Class<T> cls) {
        return Build.VERSION.SDK_INT >= 29 ? this.e.getSpans(i, i2, cls) : this.f123c.getSpans(i, i2, cls);
    }

    public int length() {
        return this.f123c.length();
    }

    public int nextSpanTransition(int i, int i2, Class cls) {
        return this.f123c.nextSpanTransition(i, i2, cls);
    }

    @SuppressLint({"NewApi"})
    public void removeSpan(Object obj) {
        if (obj instanceof MetricAffectingSpan) {
            throw new IllegalArgumentException("MetricAffectingSpan can not be removed from PrecomputedText.");
        } else if (Build.VERSION.SDK_INT >= 29) {
            this.e.removeSpan(obj);
        } else {
            this.f123c.removeSpan(obj);
        }
    }

    @SuppressLint({"NewApi"})
    public void setSpan(Object obj, int i, int i2, int i3) {
        if (obj instanceof MetricAffectingSpan) {
            throw new IllegalArgumentException("MetricAffectingSpan can not be set to PrecomputedText.");
        } else if (Build.VERSION.SDK_INT >= 29) {
            this.e.setSpan(obj, i, i2, i3);
        } else {
            this.f123c.setSpan(obj, i, i2, i3);
        }
    }

    public CharSequence subSequence(int i, int i2) {
        return this.f123c.subSequence(i, i2);
    }

    @NonNull
    public String toString() {
        return this.f123c.toString();
    }
}
