package com.miui.powercenter.batteryhistory;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import com.miui.powercenter.batteryhistory.a.a;
import com.miui.powercenter.utils.i;
import com.miui.powercenter.utils.t;
import com.miui.powercenter.utils.u;
import com.miui.powercenter.view.ShadowTextView;
import com.miui.securitycenter.R;
import com.miui.superpower.b.k;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import miui.view.animation.CubicEaseOutInterpolator;

public class BatteryLevelHistogram extends RelativeLayout {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public Context f6812a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public b f6813b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public a f6814c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public ShadowTextView f6815d;
    /* access modifiers changed from: private */
    public int e;
    /* access modifiers changed from: private */
    public int f;
    /* access modifiers changed from: private */
    public int g;
    /* access modifiers changed from: private */
    public int h;
    private List<BatteryHistogramItem> i;
    private ValueAnimator j;
    /* access modifiers changed from: private */
    public float k;

    private class a extends View {

        /* renamed from: a  reason: collision with root package name */
        int f6816a;

        /* renamed from: b  reason: collision with root package name */
        int f6817b;

        /* renamed from: c  reason: collision with root package name */
        int f6818c;

        /* renamed from: d  reason: collision with root package name */
        int f6819d;
        int e;
        private int f;
        private final TextPaint g;

        public a(BatteryLevelHistogram batteryLevelHistogram, Context context) {
            this(batteryLevelHistogram, context, (AttributeSet) null);
        }

        public a(BatteryLevelHistogram batteryLevelHistogram, @Nullable Context context, AttributeSet attributeSet) {
            this(context, attributeSet, 0);
        }

        public a(Context context, @Nullable AttributeSet attributeSet, int i) {
            super(context, attributeSet, i);
            this.g = new TextPaint(1);
            this.g.setTextSize((float) getResources().getDimensionPixelSize(R.dimen.pc_power_history_chart_text_size));
            this.g.setColor(getResources().getColor(R.color.pc_battery_statics_chart_text_color));
            this.g.setTypeface(t.a());
            this.g.setTextAlign(Paint.Align.LEFT);
        }

        /* access modifiers changed from: private */
        public void a(int i) {
            this.f = i;
            invalidate();
        }

        /* access modifiers changed from: package-private */
        public void a(Canvas canvas) {
            int i = this.f6817b;
            int i2 = this.f6819d;
            int width = getWidth();
            Path path = new Path();
            float f2 = (float) i2;
            float f3 = (float) i;
            path.moveTo(f2, f3);
            float f4 = (float) width;
            path.lineTo(f4, f3);
            path.moveTo(f2, (float) this.f6818c);
            path.lineTo(f4, (float) this.f6818c);
            int i3 = (this.f6818c - i) / 5;
            canvas.drawText("mAh", 0.0f, (float) ((i - this.e) - 9), this.g);
            for (int i4 = 0; i4 < 6; i4++) {
                int i5 = (i3 * i4) + i;
                int i6 = this.f;
                canvas.drawText(u.a(i6 - ((i6 / 5) * i4)), 0.0f, (float) ((this.f6816a / 2) + i5), this.g);
                if (!(i4 == 0 || i4 == 5)) {
                    float f5 = (float) i5;
                    path.moveTo(f2, f5);
                    path.lineTo(f4, f5);
                }
            }
            Paint paint = new Paint();
            paint.setColor(getResources().getColor(R.color.pc_power_center_text_true));
            paint.setStyle(Paint.Style.STROKE);
            paint.setPathEffect(new DashPathEffect(new float[]{3.0f, 3.0f}, 0.0f));
            canvas.drawPath(path, paint);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            a(canvas);
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            int height = getHeight();
            this.e = ((int) this.g.descent()) - ((int) this.g.ascent());
            this.f6816a = this.e / 2;
            if (this.f6816a <= 0) {
                this.f6816a = 1;
            }
            this.f6817b = BatteryLevelHistogram.this.f6812a.getResources().getDimensionPixelSize(R.dimen.pc_power_history_chart_top);
            this.f6819d = BatteryLevelHistogram.this.e;
            int i5 = this.f6817b;
            this.f6818c = (i5 + (height - i5)) - this.f6819d;
        }
    }

    private class b extends View {
        private final int A;
        private final int B;
        private final int C;
        private final int D;
        private final int E;
        private final TextPaint F;
        /* access modifiers changed from: private */
        public int G;
        /* access modifiers changed from: private */
        public float H;
        private boolean I;
        private ValueAnimator J;
        RectF K;
        Path L;
        Path M;
        Path N;
        Path O;
        float[] P;
        Interpolator Q;
        private GestureDetector.SimpleOnGestureListener R;

        /* renamed from: a  reason: collision with root package name */
        private boolean f6820a;

        /* renamed from: b  reason: collision with root package name */
        private List<BatteryHistogramItem> f6821b;

        /* renamed from: c  reason: collision with root package name */
        private GestureDetector f6822c;
        /* access modifiers changed from: private */

        /* renamed from: d  reason: collision with root package name */
        public Scroller f6823d;
        /* access modifiers changed from: private */
        public boolean e;
        /* access modifiers changed from: private */
        public boolean f;
        /* access modifiers changed from: private */
        public float g;
        int h;
        int i;
        int j;
        int k;
        int l;
        int m;
        /* access modifiers changed from: private */
        public ArrayList<Float> n;
        /* access modifiers changed from: private */
        public int o;
        /* access modifiers changed from: private */
        public int p;
        /* access modifiers changed from: private */
        public int q;
        /* access modifiers changed from: private */
        public int r;
        /* access modifiers changed from: private */
        public int s;
        private Paint t;
        private Paint u;
        private final int v;
        private final int w;
        private final int x;
        private final int y;
        private final int z;

        public b(BatteryLevelHistogram batteryLevelHistogram, Context context) {
            this(batteryLevelHistogram, context, (AttributeSet) null);
        }

        public b(BatteryLevelHistogram batteryLevelHistogram, @Nullable Context context, AttributeSet attributeSet) {
            this(context, attributeSet, 0);
        }

        public b(Context context, @Nullable AttributeSet attributeSet, int i2) {
            super(context, attributeSet, i2);
            this.f6820a = false;
            this.f6821b = new ArrayList();
            this.f = false;
            this.n = new ArrayList<>();
            this.r = -1;
            this.s = -1;
            this.F = new TextPaint(1);
            this.G = -1;
            this.H = -1.0f;
            this.K = new RectF();
            this.L = new Path();
            this.M = new Path();
            this.N = new Path();
            this.O = new Path();
            this.Q = new CubicEaseOutInterpolator();
            this.R = new T(this);
            setLayerType(1, (Paint) null);
            this.o = context.getResources().getDimensionPixelSize(R.dimen.pc_power_history_histogram_width);
            this.p = context.getResources().getDimensionPixelSize(R.dimen.pc_power_history_histogram_space);
            this.t = new Paint(1);
            Paint paint = this.t;
            int color = context.getResources().getColor(R.color.pc_battery_statics_chart_color_normal_alpha);
            this.w = color;
            paint.setColor(color);
            this.u = new Paint(1);
            Paint paint2 = this.u;
            int color2 = context.getResources().getColor(R.color.pc_battery_statics_histogram_current);
            this.v = color2;
            paint2.setColor(color2);
            int i3 = this.w;
            this.x = (i3 >> 24) & 255;
            this.y = (i3 >> 16) & 255;
            this.z = (i3 >> 8) & 255;
            this.A = i3 & 255;
            this.B = 255;
            int i4 = this.v;
            this.C = (i4 >> 16) & 255;
            this.D = (i4 >> 8) & 255;
            this.E = i4 & 255;
            this.F.setTextSize((float) getResources().getDimensionPixelSize(R.dimen.pc_power_history_chart_text_size));
            this.F.setColor(getResources().getColor(R.color.pc_battery_statics_chart_text_color));
            this.F.setTypeface(t.a());
            float dimensionPixelSize = (float) getResources().getDimensionPixelSize(R.dimen.pc_power_history_histogram_item_radius);
            this.P = new float[]{dimensionPixelSize, dimensionPixelSize, dimensionPixelSize, dimensionPixelSize, 0.0f, 0.0f, 0.0f, 0.0f};
            this.f6822c = new GestureDetector(getContext(), this.R);
            this.f6822c.setIsLongpressEnabled(false);
            this.f6823d = new Scroller(getContext());
        }

        private int a(float f2) {
            int i2 = this.x;
            int i3 = (int) (((float) i2) + (((float) (this.B - i2)) * f2));
            int i4 = this.y;
            int i5 = (int) (((float) i4) + (((float) (this.C - i4)) * f2));
            int i6 = this.z;
            int i7 = (int) (((float) i6) + (((float) (this.D - i6)) * f2));
            int i8 = this.A;
            return Color.argb(i3, i5, i7, (int) (((float) i8) + (((float) (this.E - i8)) * f2)));
        }

        /* access modifiers changed from: private */
        public int a(float f2, float f3) {
            if (this.n.size() == 0) {
                return -1;
            }
            int i2 = (int) (f2 - this.g);
            int i3 = this.p;
            int i4 = this.o;
            int i5 = i2 / (i3 + i4);
            if (i2 % (i3 + i4) > i4) {
                i5++;
            }
            if (i5 < 0 || i5 >= this.n.size()) {
                return -1;
            }
            float floatValue = this.n.get(i5).floatValue();
            int i6 = this.o;
            if (floatValue < ((float) i6)) {
                int i7 = this.j;
                if (f3 > ((float) i7) || f3 < ((float) (i7 - i6))) {
                    return -1;
                }
                return i5;
            }
            int i8 = this.j;
            if (f3 > ((float) i8) || f3 < ((float) i8) - this.n.get(i5).floatValue()) {
                return -1;
            }
            return i5;
        }

        private void a(List<BatteryHistogramItem> list) {
            this.n.clear();
            boolean z2 = false;
            float f2 = 0.0f;
            for (int i2 = 0; i2 < list.size(); i2++) {
                float f3 = (float) list.get(i2).totalConsume;
                if (f3 > f2) {
                    f2 = f3;
                }
                this.n.add(Float.valueOf(f3));
            }
            int i3 = ((((int) f2) + 50) / 50) * 50;
            for (int i4 = 0; i4 < this.n.size(); i4++) {
                this.n.set(i4, Float.valueOf((this.n.get(i4).floatValue() / ((float) i3)) * ((float) (this.j - this.i))));
            }
            BatteryLevelHistogram.this.f6814c.a(i3);
            this.q = (this.n.size() * this.o) + ((this.n.size() - 1) * this.p);
            if (this.q > getWidth()) {
                z2 = true;
            }
            this.f = z2;
            this.g = 0.0f;
            if (this.f) {
                this.f6823d.startScroll(0, 0, this.l - this.q, 0, 500);
            } else {
                this.g = (float) (this.l - this.q);
            }
            a();
        }

        private int b(float f2) {
            int i2 = this.B;
            int i3 = (int) (((float) i2) - (((float) (i2 - this.x)) * f2));
            int i4 = this.C;
            int i5 = (int) (((float) i4) - (((float) (i4 - this.y)) * f2));
            int i6 = this.D;
            int i7 = (int) (((float) i6) - (((float) (i6 - this.z)) * f2));
            int i8 = this.E;
            return Color.argb(i3, i5, i7, (int) (((float) i8) - (((float) (i8 - this.A)) * f2)));
        }

        /* access modifiers changed from: private */
        @SuppressLint({"NewApi"})
        public void b() {
            ValueAnimator valueAnimator = this.J;
            if (valueAnimator == null || !valueAnimator.isRunning()) {
                this.J = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f}).setDuration(300);
                this.J.setInterpolator(new LinearInterpolator());
                this.J.addUpdateListener(new Q(this));
                this.J.addListener(new S(this));
                this.J.start();
            }
        }

        @SuppressLint({"NewApi"})
        public void a() {
            ValueAnimator valueAnimator = this.J;
            if (valueAnimator == null || !valueAnimator.isRunning()) {
                this.I = true;
                this.J = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f}).setDuration((long) ((this.f6821b.size() * 20) + 300));
                this.J.setInterpolator(new LinearInterpolator());
                this.J.addUpdateListener(new M(this));
                this.J.addListener(new N(this));
                this.J.start();
            }
        }

        @SuppressLint({"NewApi"})
        public void a(a.C0061a aVar) {
            ValueAnimator valueAnimator = this.J;
            if (valueAnimator == null || !valueAnimator.isRunning()) {
                this.I = false;
                this.J = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f}).setDuration((long) ((this.f6821b.size() * 10) + 250));
                this.J.setInterpolator(new LinearInterpolator());
                this.J.addUpdateListener(new O(this));
                this.J.addListener(new P(this, aVar));
                this.J.start();
            }
        }

        public boolean a(List<aa> list, List<BatteryHistogramItem> list2) {
            if (list2.size() == 0) {
                invalidate();
                BatteryLevelHistogram.this.c();
                return false;
            }
            this.f6821b.clear();
            this.f6821b.addAll(list2);
            if (!this.f6820a) {
                return true;
            }
            a(this.f6821b);
            return true;
        }

        public void computeScroll() {
            if (this.f) {
                if (this.f6823d.computeScrollOffset()) {
                    this.g = (float) this.f6823d.getCurrX();
                    invalidate();
                }
                if (this.e) {
                    if (this.f6823d.isFinished()) {
                        this.e = false;
                    }
                    if (this.s >= 0 && BatteryLevelHistogram.this.f6815d.getVisibility() == 0) {
                        BatteryLevelHistogram batteryLevelHistogram = BatteryLevelHistogram.this;
                        int i2 = this.s;
                        batteryLevelHistogram.a((((((((float) i2) + 0.5f) * ((float) this.o)) + ((float) (i2 * this.p))) + ((float) this.k)) - (((float) batteryLevelHistogram.f) / 2.0f)) + this.g, ((((float) this.j) - this.n.get(this.s).floatValue()) - ((float) BatteryLevelHistogram.this.g)) - ((float) BatteryLevelHistogram.this.h));
                    }
                }
            }
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Code restructure failed: missing block: B:50:0x012d, code lost:
            if (r7 == r6) goto L_0x0121;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:53:0x0134, code lost:
            if (r12.r == -1) goto L_0x0121;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:54:0x0137, code lost:
            r7 = r12.N;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onDraw(android.graphics.Canvas r13) {
            /*
                r12 = this;
                super.onDraw(r13)
                java.util.ArrayList<java.lang.Float> r0 = r12.n
                int r0 = r0.size()
                if (r0 != 0) goto L_0x000c
                return
            L_0x000c:
                float r1 = r12.g
                r2 = 0
                r13.translate(r1, r2)
                android.graphics.Path r1 = r12.L
                r1.reset()
                android.graphics.Path r1 = r12.M
                r1.reset()
                android.graphics.Path r1 = r12.O
                r1.reset()
                android.graphics.Path r1 = r12.N
                r1.reset()
                boolean r1 = r12.I
                if (r1 == 0) goto L_0x002d
                r1 = 20
                goto L_0x002f
            L_0x002d:
                r1 = 10
            L_0x002f:
                boolean r3 = r12.I
                if (r3 == 0) goto L_0x0036
                r3 = 300(0x12c, float:4.2E-43)
                goto L_0x0038
            L_0x0036:
                r3 = 250(0xfa, float:3.5E-43)
            L_0x0038:
                java.util.Calendar r4 = java.util.Calendar.getInstance()
                long r5 = java.lang.System.currentTimeMillis()
                r4.setTimeInMillis(r5)
                r5 = 11
                int r4 = r4.get(r5)
                r5 = 0
                r6 = r5
            L_0x004b:
                if (r6 >= r0) goto L_0x014b
                java.util.ArrayList<java.lang.Float> r7 = r12.n
                java.lang.Object r7 = r7.get(r6)
                java.lang.Float r7 = (java.lang.Float) r7
                float r7 = r7.floatValue()
                int r8 = r12.G
                r9 = -1
                if (r8 == r9) goto L_0x008b
                int r10 = r1 * r6
                int r8 = r8 - r10
                float r8 = (float) r8
                int r10 = (r8 > r2 ? 1 : (r8 == r2 ? 0 : -1))
                if (r10 >= 0) goto L_0x0068
                r8 = r2
                goto L_0x006e
            L_0x0068:
                float r10 = (float) r3
                int r11 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
                if (r11 <= 0) goto L_0x006e
                r8 = r10
            L_0x006e:
                android.view.animation.Interpolator r10 = r12.Q
                float r11 = (float) r3
                float r8 = r8 / r11
                float r8 = r10.getInterpolation(r8)
                boolean r10 = r12.I
                if (r10 == 0) goto L_0x007c
                float r8 = r8 * r7
                goto L_0x007f
            L_0x007c:
                float r8 = r8 * r7
                float r8 = r7 - r8
            L_0x007f:
                int r10 = (r8 > r7 ? 1 : (r8 == r7 ? 0 : -1))
                if (r10 <= 0) goto L_0x0084
                goto L_0x008b
            L_0x0084:
                int r7 = (r8 > r2 ? 1 : (r8 == r2 ? 0 : -1))
                if (r7 >= 0) goto L_0x008a
                r7 = r2
                goto L_0x008b
            L_0x008a:
                r7 = r8
            L_0x008b:
                android.graphics.RectF r8 = r12.K
                int r10 = r6 * 2
                int r11 = r12.o
                int r10 = r10 * r11
                float r10 = (float) r10
                r8.left = r10
                int r10 = r12.p
                int r10 = r10 + r11
                int r10 = r10 * r6
                float r10 = (float) r10
                r8.left = r10
                float r10 = r8.left
                float r11 = (float) r11
                float r10 = r10 + r11
                r8.right = r10
                int r10 = r12.j
                float r11 = (float) r10
                float r11 = r11 - r7
                r8.top = r11
                float r7 = (float) r10
                r8.bottom = r7
                int r7 = r0 + -1
                if (r6 == r7) goto L_0x00b5
                int r8 = r7 - r6
                int r8 = r8 % 3
                if (r8 != 0) goto L_0x0119
            L_0x00b5:
                if (r6 != r7) goto L_0x00d6
                android.text.TextPaint r7 = r12.F
                android.graphics.Paint$Align r8 = android.graphics.Paint.Align.RIGHT
                r7.setTextAlign(r8)
                android.content.res.Resources r7 = r12.getResources()
                r8 = 2131757144(0x7f100858, float:1.9145215E38)
                java.lang.String r7 = r7.getString(r8)
                android.graphics.RectF r8 = r12.K
                float r8 = r8.right
                int r10 = r12.m
                float r10 = (float) r10
                android.text.TextPaint r11 = r12.F
                r13.drawText(r7, r8, r10, r11)
                goto L_0x0119
            L_0x00d6:
                if (r6 != 0) goto L_0x00e4
                android.text.TextPaint r7 = r12.F
                android.graphics.Paint$Align r8 = android.graphics.Paint.Align.LEFT
                r7.setTextAlign(r8)
                android.graphics.RectF r7 = r12.K
                float r7 = r7.left
                goto L_0x00f6
            L_0x00e4:
                android.text.TextPaint r7 = r12.F
                android.graphics.Paint$Align r8 = android.graphics.Paint.Align.CENTER
                r7.setTextAlign(r8)
                android.graphics.RectF r7 = r12.K
                float r8 = r7.left
                float r7 = r7.right
                float r7 = r7 - r8
                r10 = 1073741824(0x40000000, float:2.0)
                float r7 = r7 / r10
                float r7 = r7 + r8
            L_0x00f6:
                int r8 = r4 - r0
                r10 = 1
                int r8 = r8 + r10
                int r8 = r8 + r6
                if (r8 >= 0) goto L_0x00ff
                int r8 = r8 + 24
            L_0x00ff:
                java.util.Locale r11 = java.util.Locale.getDefault()
                java.lang.Object[] r10 = new java.lang.Object[r10]
                java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
                r10[r5] = r8
                java.lang.String r8 = "%02d:00"
                java.lang.String r8 = java.lang.String.format(r11, r8, r10)
                int r10 = r12.m
                float r10 = (float) r10
                android.text.TextPaint r11 = r12.F
                r13.drawText(r8, r7, r10, r11)
            L_0x0119:
                int r7 = r12.s
                if (r7 != r9) goto L_0x0130
                int r7 = r12.r
                if (r7 != r9) goto L_0x012d
            L_0x0121:
                android.graphics.Path r7 = r12.M
            L_0x0123:
                android.graphics.RectF r8 = r12.K
                float[] r9 = r12.P
                android.graphics.Path$Direction r10 = android.graphics.Path.Direction.CW
                r7.addRoundRect(r8, r9, r10)
                goto L_0x0147
            L_0x012d:
                if (r7 != r6) goto L_0x0137
                goto L_0x0121
            L_0x0130:
                if (r7 != r6) goto L_0x013a
                int r7 = r12.r
                if (r7 != r9) goto L_0x0137
                goto L_0x0121
            L_0x0137:
                android.graphics.Path r7 = r12.N
                goto L_0x0123
            L_0x013a:
                int r7 = r12.r
                if (r7 != r9) goto L_0x0141
            L_0x013e:
                android.graphics.Path r7 = r12.O
                goto L_0x0123
            L_0x0141:
                if (r7 != r6) goto L_0x0144
                goto L_0x013e
            L_0x0144:
                android.graphics.Path r7 = r12.L
                goto L_0x0123
            L_0x0147:
                int r6 = r6 + 1
                goto L_0x004b
            L_0x014b:
                float r0 = r12.H
                r1 = -1082130432(0xffffffffbf800000, float:-1.0)
                int r1 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r1 == 0) goto L_0x0165
                android.graphics.Paint r1 = r12.u
                int r0 = r12.a((float) r0)
                r1.setColor(r0)
                android.graphics.Paint r0 = r12.t
                float r1 = r12.H
                int r1 = r12.b((float) r1)
                goto L_0x0170
            L_0x0165:
                android.graphics.Paint r0 = r12.u
                int r1 = r12.v
                r0.setColor(r1)
                android.graphics.Paint r0 = r12.t
                int r1 = r12.w
            L_0x0170:
                r0.setColor(r1)
                android.graphics.Path r0 = r12.N
                android.graphics.Paint r1 = r12.u
                r13.drawPath(r0, r1)
                android.graphics.Path r0 = r12.O
                android.graphics.Paint r1 = r12.t
                r13.drawPath(r0, r1)
                android.graphics.Paint r0 = r12.t
                int r1 = r12.v
                r0.setColor(r1)
                android.graphics.Path r0 = r12.M
                android.graphics.Paint r1 = r12.t
                r13.drawPath(r0, r1)
                android.graphics.Paint r0 = r12.t
                int r1 = r12.w
                r0.setColor(r1)
                android.graphics.Path r0 = r12.L
                android.graphics.Paint r1 = r12.t
                r13.drawPath(r0, r1)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.powercenter.batteryhistory.BatteryLevelHistogram.b.onDraw(android.graphics.Canvas):void");
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z2, int i2, int i3, int i4, int i5) {
            super.onLayout(z2, i2, i3, i4, i5);
            int height = getHeight();
            int descent = ((int) this.F.descent()) - ((int) this.F.ascent());
            this.f = this.q > getWidth();
            int i6 = descent / 2;
            this.h = i6;
            if (this.h <= 0) {
                this.h = 1;
            }
            this.i = BatteryLevelHistogram.this.f6812a.getResources().getDimensionPixelSize(R.dimen.pc_power_history_chart_top);
            this.l = getWidth();
            this.k = BatteryLevelHistogram.this.e;
            int i7 = this.i;
            this.j = (i7 + (height - i7)) - this.k;
            this.m = this.j + getResources().getDimensionPixelSize(R.dimen.pc_power_history_chart_current_text_offset) + i6;
            if (!this.f6820a && !this.f6821b.isEmpty()) {
                this.f6820a = true;
                a(this.f6821b);
            }
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            ViewParent viewParent;
            int action = motionEvent.getAction();
            motionEvent.getX();
            boolean z2 = true;
            if (action != 0) {
                if (action == 1) {
                    if (this.f) {
                        viewParent = getParent();
                        z2 = false;
                    }
                }
                return this.f6822c.onTouchEvent(motionEvent);
            }
            if (this.f) {
                viewParent = getParent();
            }
            return this.f6822c.onTouchEvent(motionEvent);
            viewParent.requestDisallowInterceptTouchEvent(z2);
            return this.f6822c.onTouchEvent(motionEvent);
        }
    }

    public BatteryLevelHistogram(Context context) {
        this(context, (AttributeSet) null);
    }

    public BatteryLevelHistogram(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public BatteryLevelHistogram(Context context, AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        this.i = new ArrayList();
        this.k = -1.0f;
        this.f6812a = context;
        this.f = context.getResources().getDimensionPixelSize(R.dimen.pc_power_history_float_text_width);
        this.g = context.getResources().getDimensionPixelSize(R.dimen.pc_power_history_float_text_height);
        this.h = this.g;
        this.f6814c = new a(this, context);
        addView(this.f6814c, new RelativeLayout.LayoutParams(-1, -1));
        this.f6813b = new b(this, context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-1, -1);
        TextPaint textPaint = new TextPaint(1);
        textPaint.setTextSize(context.getResources().getDimension(R.dimen.text_font_size_34));
        textPaint.setTypeface(t.a());
        this.e = this.f6812a.getResources().getDimensionPixelSize(R.dimen.pc_power_history_chart_left);
        layoutParams.setMargins(this.e, 0, 0, 0);
        addView(this.f6813b, layoutParams);
        LayoutInflater.from(context).inflate(R.layout.pc_battery_statics_chart_time, this);
        this.f6815d = (ShadowTextView) findViewById(R.id.float_text);
        if (k.a() > 8) {
            this.f6815d.a(t.a(), 1);
        }
    }

    /* access modifiers changed from: private */
    public void a(float f2, float f3) {
        b bVar = this.f6813b;
        int i2 = bVar.k;
        if (f2 < ((float) i2)) {
            f2 = (float) i2;
        } else {
            int i3 = bVar.l;
            int i4 = this.e;
            int i5 = this.f;
            if (f2 > ((float) ((i3 + i4) - i5))) {
                f2 = (float) ((i3 + i4) - i5);
            }
        }
        if (f3 < 20.0f) {
            f3 = 20.0f;
        }
        this.f6815d.setX(f2);
        this.f6815d.setY(f3);
    }

    @SuppressLint({"NewApi"})
    private void a(float f2, float f3, float f4, float f5) {
        b();
        this.j = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f}).setDuration(300);
        this.j.setInterpolator(new CubicEaseOutInterpolator());
        this.j.addUpdateListener(new K(this, f4, f2, f5, f3));
        this.j.addListener(new L(this));
        this.j.start();
    }

    /* access modifiers changed from: private */
    public void a(boolean z, float f2, float f3) {
        if (!z) {
            this.f6815d.animate().alpha(0.0f).scaleX(0.8f).scaleY(0.8f).setDuration(200).setListener(new J(this)).start();
        } else if (this.f6815d.getVisibility() == 0) {
            a(this.f6815d.getX(), this.f6815d.getY(), f2, f3);
        } else {
            ValueAnimator valueAnimator = this.j;
            if (valueAnimator == null || !valueAnimator.isRunning()) {
                if (this.f6815d.getVisibility() != 0) {
                    this.f6815d.setVisibility(0);
                    this.f6815d.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(200).setListener(new I(this)).start();
                }
                a(f2, f3);
            }
        }
    }

    private void b() {
        ValueAnimator valueAnimator = this.j;
        if (valueAnimator != null && valueAnimator.isRunning()) {
            this.j.cancel();
        }
    }

    /* access modifiers changed from: private */
    public void c() {
        Message message = new Message();
        message.what = 10005;
        i.a().a(message);
    }

    /* access modifiers changed from: private */
    public void d() {
        if (this.i.size() > 0 && this.f6813b.s < this.i.size()) {
            Calendar instance = Calendar.getInstance();
            instance.setTimeInMillis(this.i.get(this.f6813b.s).startUTCTime);
            int i2 = instance.get(11);
            this.f6815d.setText(String.format(Locale.getDefault(), "%1$02d:00-%2$02d:00", new Object[]{Integer.valueOf(i2), Integer.valueOf(i2 + 1)}));
        }
    }

    public void a() {
        int unused = this.f6813b.r = -1;
        int unused2 = this.f6813b.s = -1;
        a(false, 0.0f, 0.0f);
    }

    public void a(a.C0061a aVar) {
        if (this.i.size() == 0 || (this.i.size() == 1 && this.i.get(0).totalConsume == 0.0d)) {
            aVar.a();
            return;
        }
        this.f6815d.animate().alpha(0.0f).scaleX(0.8f).scaleY(0.8f).setDuration(200).start();
        b();
        this.f6813b.a(aVar);
    }

    public void a(List<aa> list, List<BatteryHistogramItem> list2) {
        this.i.clear();
        this.i.addAll(list2);
        this.f6813b.a(list, list2);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i2, int i3, int i4, int i5) {
        super.onLayout(z, i2, i3, i4, i5);
        this.f = this.f6815d.getWidth();
    }
}
