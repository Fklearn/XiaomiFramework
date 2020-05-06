package com.miui.powercenter.batteryhistory;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Shader;
import android.os.Build;
import android.os.Message;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;
import com.miui.powercenter.utils.i;
import com.miui.powercenter.utils.o;
import com.miui.powercenter.utils.t;
import com.miui.powercenter.utils.u;
import com.miui.powercenter.view.ShadowTextView;
import com.miui.securitycenter.R;
import com.miui.superpower.b.k;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import miui.view.animation.QuarticEaseOutInterpolator;

public class BatteryLevelChart extends RelativeLayout {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public Context f6798a;

    /* renamed from: b  reason: collision with root package name */
    private a f6799b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public ShadowTextView f6800c;

    /* renamed from: d  reason: collision with root package name */
    private float f6801d;
    /* access modifiers changed from: private */
    public int e;
    /* access modifiers changed from: private */
    public int f;
    /* access modifiers changed from: private */
    public int g;

    private class a extends View {
        private final Paint A;
        private final Paint B;
        private final Paint C;
        private final Path D;
        private final int E;
        private int F;
        private int G;
        private int H;
        private int I;
        /* access modifiers changed from: private */
        public float[] J;
        /* access modifiers changed from: private */
        public boolean K;
        /* access modifiers changed from: private */
        public boolean L;
        private boolean M;
        List<aa> N;
        List<BatteryHistogramItem> O;
        private List<Float> P;
        private List<Float> Q;
        private List<Boolean> R;
        private List<Float> S;
        private List<Float> T;
        private List<Boolean> U;
        private ArrayList<b> V;
        private ArrayList<b> W;

        /* renamed from: a  reason: collision with root package name */
        final TextPaint f6802a;
        /* access modifiers changed from: private */
        public float aa;

        /* renamed from: b  reason: collision with root package name */
        final Path f6803b;
        private ValueAnimator ba;

        /* renamed from: c  reason: collision with root package name */
        int f6804c;
        private boolean ca;

        /* renamed from: d  reason: collision with root package name */
        int f6805d;
        private boolean da;
        int e;
        private int ea;
        int f;
        private int fa;
        int g;
        private int ga;
        int h;
        private int ha;
        int i;
        private boolean ia;
        int j;
        int k;
        int l;
        float m;
        float n;
        float o;
        int p;
        long q;
        long r;
        long s;
        long t;
        private final int u;
        private final Paint v;
        private final Paint w;
        private final Paint x;
        private final Paint y;
        private final Paint z;

        /* renamed from: com.miui.powercenter.batteryhistory.BatteryLevelChart$a$a  reason: collision with other inner class name */
        private class C0060a {
            /* access modifiers changed from: private */

            /* renamed from: a  reason: collision with root package name */
            public Float f6806a;
            /* access modifiers changed from: private */

            /* renamed from: b  reason: collision with root package name */
            public Float f6807b;

            private C0060a(Float f, Float f2) {
                this.f6806a = f;
                this.f6807b = f2;
            }
        }

        private class b {
            /* access modifiers changed from: private */

            /* renamed from: a  reason: collision with root package name */
            public C0060a f6809a;
            /* access modifiers changed from: private */

            /* renamed from: b  reason: collision with root package name */
            public C0060a f6810b;

            private b() {
            }

            /* access modifiers changed from: private */
            public boolean a() {
                return this.f6809a != null;
            }
        }

        public a(BatteryLevelChart batteryLevelChart, Context context) {
            this(context, (AttributeSet) null);
        }

        public a(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            int i2;
            Resources resources;
            int i3;
            this.f6802a = new TextPaint(1);
            this.f6803b = new Path();
            this.D = new Path();
            this.J = new float[2];
            this.K = true;
            this.L = false;
            this.N = new ArrayList();
            this.O = new ArrayList();
            this.P = new ArrayList();
            this.Q = new ArrayList();
            this.R = new ArrayList();
            this.S = new ArrayList();
            this.T = new ArrayList();
            this.U = new ArrayList();
            this.V = new ArrayList<>();
            this.W = new ArrayList<>();
            this.ca = false;
            this.da = false;
            this.ea = 0;
            this.fa = 48;
            setLayerType(1, (Paint) null);
            this.f6802a.setTextSize((float) getResources().getDimensionPixelSize(R.dimen.pc_power_history_chart_text_size));
            this.f6802a.setTypeface(t.a());
            this.f6802a.setColor(getResources().getColor(R.color.pc_battery_statics_chart_text_color));
            this.F = getResources().getColor(R.color.pc_battery_statics_chart_color_normal);
            this.G = getResources().getColor(R.color.pc_battery_statics_chart_color_charge);
            this.H = getResources().getColor(R.color.pc_battery_statics_chart_color_normal_outside);
            this.I = getResources().getColor(R.color.pc_battery_statics_chart_color_charge_outside);
            int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.pc_power_history_chart_line_width);
            this.j = getResources().getDimensionPixelSize(R.dimen.pc_power_history_chart_line_shadow_y);
            this.k = getResources().getDimensionPixelSize(R.dimen.pc_power_history_chart_ball_radius_outer);
            this.l = getResources().getDimensionPixelSize(R.dimen.pc_power_history_chart_ball_radius_inner);
            this.v = new Paint(5);
            this.v.setStyle(Paint.Style.STROKE);
            this.v.setStrokeCap(Paint.Cap.BUTT);
            this.v.setStrokeJoin(Paint.Join.ROUND);
            this.v.setColor(context.getResources().getColor(R.color.pc_battery_statics_chart_color_default));
            float f2 = (float) dimensionPixelSize;
            this.v.setStrokeWidth(f2);
            if (Build.VERSION.SDK_INT < 28) {
                i3 = Color.parseColor("#10979797");
                resources = getResources();
                i2 = R.dimen.pc_power_history_chart_line_shadow_radius_small;
            } else {
                i3 = Color.parseColor("#1A979797");
                resources = getResources();
                i2 = R.dimen.pc_power_history_chart_line_shadow_radius;
            }
            this.i = resources.getDimensionPixelSize(i2);
            this.v.setShadowLayer((float) this.i, 0.0f, (float) this.j, i3);
            this.y = new Paint(5);
            this.y.setStyle(Paint.Style.STROKE);
            this.y.setStrokeCap(Paint.Cap.BUTT);
            this.y.setStrokeJoin(Paint.Join.ROUND);
            this.y.setStrokeWidth(f2);
            this.z = new Paint(5);
            this.z.setStyle(Paint.Style.STROKE);
            this.z.setColor(this.F);
            this.z.setStrokeWidth(f2);
            this.A = new Paint(5);
            this.A.setStyle(Paint.Style.FILL);
            this.A.setColor(-1);
            this.B = new Paint(5);
            this.B.setStyle(Paint.Style.FILL);
            this.v.setAntiAlias(true);
            this.u = getResources().getDimensionPixelSize(R.dimen.usage_graph_line_corner_radius);
            this.w = new Paint();
            this.w.setAntiAlias(true);
            this.w.setStyle(Paint.Style.FILL);
            this.x = new Paint();
            this.x.setAntiAlias(true);
            this.x.setStyle(Paint.Style.FILL);
            this.C = new Paint();
            this.C.setAntiAlias(true);
            this.C.setStyle(Paint.Style.FILL);
            this.E = ViewConfiguration.get(context).getScaledTouchSlop();
        }

        private int a(float f2) {
            int size = this.O.size();
            int i2 = this.f;
            float f3 = ((f2 - ((float) i2)) * ((float) size)) / ((float) (this.g - i2));
            int i3 = (int) f3;
            return f3 - ((float) i3) > 0.5f ? i3 + 1 : i3;
        }

        private int a(int i2, float f2) {
            return i2 & ((((int) (f2 * 255.0f)) << 24) | 16777215);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:61:0x02b5, code lost:
            if (r6 == (r0.S.size() - 1)) goto L_0x029e;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void b(android.graphics.Canvas r19) {
            /*
                r18 = this;
                r0 = r18
                r1 = r19
                java.util.List<java.lang.Float> r2 = r0.S
                int r2 = r2.size()
                r3 = 1
                if (r2 <= 0) goto L_0x001f
                java.util.List<java.lang.Float> r2 = r0.S
                int r4 = r2.size()
                int r4 = r4 - r3
                java.lang.Object r2 = r2.get(r4)
                java.lang.Float r2 = (java.lang.Float) r2
                float r2 = r2.floatValue()
                goto L_0x0022
            L_0x001f:
                int r2 = r0.g
                float r2 = (float) r2
            L_0x0022:
                int r4 = r18.getHeight()
                android.graphics.Path r5 = new android.graphics.Path
                r5.<init>()
                android.graphics.Path r6 = new android.graphics.Path
                r6.<init>()
                android.graphics.Path r7 = new android.graphics.Path
                r7.<init>()
                android.graphics.Path r8 = new android.graphics.Path
                r8.<init>()
                android.graphics.Path r9 = new android.graphics.Path
                r9.<init>()
                android.graphics.RectF r10 = new android.graphics.RectF
                r10.<init>()
                r11 = 0
                r10.top = r11
                float r4 = (float) r4
                r10.bottom = r4
                java.util.List<java.lang.Float> r12 = r0.S
                int r12 = r12.size()
                r13 = 0
                if (r12 <= 0) goto L_0x0078
                java.util.List<java.lang.Float> r12 = r0.S
                java.lang.Object r12 = r12.get(r13)
                java.lang.Float r12 = (java.lang.Float) r12
                float r12 = r12.floatValue()
                java.util.List<java.lang.Float> r14 = r0.T
                java.lang.Object r14 = r14.get(r13)
                java.lang.Float r14 = (java.lang.Float) r14
                float r14 = r14.floatValue()
                java.util.List<java.lang.Boolean> r15 = r0.U
                java.lang.Object r15 = r15.get(r13)
                java.lang.Boolean r15 = (java.lang.Boolean) r15
                boolean r15 = r15.booleanValue()
                goto L_0x007b
            L_0x0078:
                r12 = r11
                r14 = r12
                r15 = r13
            L_0x007b:
                if (r15 == 0) goto L_0x008a
                int r11 = r0.e
                float r11 = (float) r11
                r6.moveTo(r12, r11)
                r6.lineTo(r12, r14)
                r9.moveTo(r12, r14)
                goto L_0x0096
            L_0x008a:
                int r11 = r0.e
                float r11 = (float) r11
                r5.moveTo(r12, r11)
                r5.lineTo(r12, r14)
                r8.moveTo(r12, r14)
            L_0x0096:
                r7.moveTo(r12, r14)
                r11 = r3
            L_0x009a:
                java.util.List<java.lang.Float> r12 = r0.S
                int r12 = r12.size()
                if (r11 >= r12) goto L_0x0106
                java.util.List<java.lang.Float> r12 = r0.S
                java.lang.Object r12 = r12.get(r11)
                java.lang.Float r12 = (java.lang.Float) r12
                float r12 = r12.floatValue()
                java.util.List<java.lang.Float> r14 = r0.T
                java.lang.Object r14 = r14.get(r11)
                java.lang.Float r14 = (java.lang.Float) r14
                float r14 = r14.floatValue()
                java.util.List<java.lang.Boolean> r13 = r0.U
                java.lang.Object r13 = r13.get(r11)
                java.lang.Boolean r13 = (java.lang.Boolean) r13
                boolean r13 = r13.booleanValue()
                if (r15 == 0) goto L_0x00cf
                r6.lineTo(r12, r14)
                r9.lineTo(r12, r14)
                goto L_0x00d5
            L_0x00cf:
                r5.lineTo(r12, r14)
                r8.lineTo(r12, r14)
            L_0x00d5:
                if (r13 == r15) goto L_0x00ff
                if (r15 == 0) goto L_0x00ec
                int r15 = r0.e
                float r15 = (float) r15
                r6.lineTo(r12, r15)
                int r15 = r0.e
                float r15 = (float) r15
                r5.moveTo(r12, r15)
                r5.lineTo(r12, r14)
                r8.moveTo(r12, r14)
                goto L_0x00fe
            L_0x00ec:
                int r15 = r0.e
                float r15 = (float) r15
                r5.lineTo(r12, r15)
                int r15 = r0.e
                float r15 = (float) r15
                r6.moveTo(r12, r15)
                r6.lineTo(r12, r14)
                r9.moveTo(r12, r14)
            L_0x00fe:
                r15 = r13
            L_0x00ff:
                r7.lineTo(r12, r14)
                int r11 = r11 + 1
                r13 = 0
                goto L_0x009a
            L_0x0106:
                java.util.List<java.lang.Float> r11 = r0.S
                int r11 = r11.size()
                if (r11 <= 0) goto L_0x013f
                if (r15 == 0) goto L_0x0128
                java.util.List<java.lang.Float> r11 = r0.S
                int r12 = r11.size()
                int r12 = r12 - r3
                java.lang.Object r11 = r11.get(r12)
                java.lang.Float r11 = (java.lang.Float) r11
                float r11 = r11.floatValue()
                int r12 = r0.e
                float r12 = (float) r12
                r6.lineTo(r11, r12)
                goto L_0x013f
            L_0x0128:
                java.util.List<java.lang.Float> r11 = r0.S
                int r12 = r11.size()
                int r12 = r12 - r3
                java.lang.Object r11 = r11.get(r12)
                java.lang.Float r11 = (java.lang.Float) r11
                float r11 = r11.floatValue()
                int r12 = r0.e
                float r12 = (float) r12
                r5.lineTo(r11, r12)
            L_0x013f:
                r19.save()
                int r11 = r0.g
                int r12 = r0.f
                int r11 = r11 - r12
                float r11 = (float) r11
                float r13 = r0.aa
                float r11 = r11 * r13
                float r13 = (float) r12
                float r11 = r11 + r13
                float r12 = (float) r12
                r10.left = r12
                r10.right = r11
                r1.clipRect(r10)
                android.graphics.Paint r12 = r0.v
                r1.drawPath(r7, r12)
                android.graphics.Paint r7 = r0.C
                com.miui.powercenter.batteryhistory.BatteryLevelChart r12 = com.miui.powercenter.batteryhistory.BatteryLevelChart.this
                android.content.Context r12 = r12.f6798a
                android.content.res.Resources r12 = r12.getResources()
                r13 = 2131100374(0x7f0602d6, float:1.7813128E38)
                int r12 = r12.getColor(r13)
                r7.setColor(r12)
                android.graphics.Path r7 = r0.D
                android.graphics.Paint r12 = r0.C
                r1.drawPath(r7, r12)
                r19.restore()
                r19.save()
                float[] r7 = r0.J
                r12 = 0
                r13 = r7[r12]
                r12 = r7[r3]
                int r12 = (r12 > r11 ? 1 : (r12 == r11 ? 0 : -1))
                if (r12 <= 0) goto L_0x0189
                goto L_0x018b
            L_0x0189:
                r11 = r7[r3]
            L_0x018b:
                r7 = 0
                r10.top = r7
                r10.bottom = r4
                r10.left = r13
                r10.right = r11
                r1.clipRect(r10)
                android.graphics.Paint r4 = r0.w
                r1.drawPath(r5, r4)
                android.graphics.Paint r4 = r0.x
                r1.drawPath(r6, r4)
                android.graphics.Paint r4 = r0.y
                int r5 = r0.F
                r4.setColor(r5)
                int r4 = android.os.Build.VERSION.SDK_INT
                r5 = 28
                if (r4 >= r5) goto L_0x01be
                android.graphics.Paint r4 = r0.y
                int r6 = r0.i
                float r6 = (float) r6
                int r7 = r0.j
                float r7 = (float) r7
                java.lang.String r10 = "#0A10AEFF"
                int r10 = android.graphics.Color.parseColor(r10)
                r12 = 0
                goto L_0x01cd
            L_0x01be:
                r12 = 0
                android.graphics.Paint r4 = r0.y
                int r6 = r0.i
                float r6 = (float) r6
                int r7 = r0.j
                float r7 = (float) r7
                java.lang.String r10 = "#2610AEFF"
                int r10 = android.graphics.Color.parseColor(r10)
            L_0x01cd:
                r4.setShadowLayer(r6, r12, r7, r10)
                android.graphics.Paint r4 = r0.y
                r1.drawPath(r8, r4)
                android.graphics.Paint r4 = r0.y
                int r6 = r0.G
                r4.setColor(r6)
                int r4 = android.os.Build.VERSION.SDK_INT
                if (r4 >= r5) goto L_0x01f0
                android.graphics.Paint r4 = r0.y
                int r5 = r0.i
                float r5 = (float) r5
                int r6 = r0.j
                float r6 = (float) r6
                java.lang.String r7 = "#0A39DA5F"
                int r7 = android.graphics.Color.parseColor(r7)
                r8 = 0
                goto L_0x01ff
            L_0x01f0:
                r8 = 0
                android.graphics.Paint r4 = r0.y
                int r5 = r0.i
                float r5 = (float) r5
                int r6 = r0.j
                float r6 = (float) r6
                java.lang.String r7 = "#2639DA5F"
                int r7 = android.graphics.Color.parseColor(r7)
            L_0x01ff:
                r4.setShadowLayer(r5, r8, r6, r7)
                android.graphics.Paint r4 = r0.y
                r1.drawPath(r9, r4)
                android.graphics.Paint r4 = r0.C
                com.miui.powercenter.batteryhistory.BatteryLevelChart r5 = com.miui.powercenter.batteryhistory.BatteryLevelChart.this
                android.content.Context r5 = r5.f6798a
                android.content.res.Resources r5 = r5.getResources()
                r6 = 2131100373(0x7f0602d5, float:1.7813126E38)
                int r5 = r5.getColor(r6)
                r4.setColor(r5)
                android.graphics.Path r4 = r0.D
                android.graphics.Paint r5 = r0.C
                r1.drawPath(r4, r5)
                r19.restore()
                r4 = 2
                float[] r5 = new float[r4]
                float[] r6 = r0.J
                r7 = r6[r3]
                r9 = 0
                r10 = r6[r9]
                float r7 = r7 - r10
                r10 = 1073741824(0x40000000, float:2.0)
                float r7 = r7 / r10
                r6 = r6[r9]
                float r7 = r7 + r6
                r6 = r3
                r9 = r8
                r8 = 0
            L_0x023b:
                java.util.List<java.lang.Float> r12 = r0.S
                int r12 = r12.size()
                if (r6 >= r12) goto L_0x02e2
                java.util.List<java.lang.Float> r12 = r0.S
                java.lang.Object r12 = r12.get(r6)
                java.lang.Float r12 = (java.lang.Float) r12
                float r12 = r12.floatValue()
                java.util.List<java.lang.Float> r14 = r0.T
                java.lang.Object r14 = r14.get(r6)
                java.lang.Float r14 = (java.lang.Float) r14
                float r14 = r14.floatValue()
                java.util.List<java.lang.Float> r15 = r0.S
                int r10 = r6 + -1
                java.lang.Object r15 = r15.get(r10)
                java.lang.Float r15 = (java.lang.Float) r15
                float r15 = r15.floatValue()
                java.util.List<java.lang.Float> r4 = r0.T
                java.lang.Object r4 = r4.get(r10)
                java.lang.Float r4 = (java.lang.Float) r4
                float r4 = r4.floatValue()
                if (r8 != 0) goto L_0x0294
                int r10 = (r12 > r13 ? 1 : (r12 == r13 ? 0 : -1))
                if (r10 != 0) goto L_0x0282
                r16 = 0
                r5[r16] = r14
            L_0x027f:
                int r8 = r8 + 1
                goto L_0x0294
            L_0x0282:
                r16 = 0
                if (r10 <= 0) goto L_0x0294
                float r10 = r14 - r4
                float r17 = r13 - r15
                float r10 = r10 * r17
                float r17 = r12 - r15
                float r10 = r10 / r17
                float r10 = r10 + r4
                r5[r16] = r10
                goto L_0x027f
            L_0x0294:
                if (r8 != r3) goto L_0x02ba
                boolean r10 = r0.K
                if (r10 != 0) goto L_0x02b8
                int r10 = (r12 > r7 ? 1 : (r12 == r7 ? 0 : -1))
                if (r10 != 0) goto L_0x02a2
            L_0x029e:
                int r8 = r8 + 1
                r9 = r14
                goto L_0x02ba
            L_0x02a2:
                if (r10 <= 0) goto L_0x02ae
                float r9 = r14 - r4
                float r10 = r7 - r15
                float r9 = r9 * r10
                float r10 = r12 - r15
                float r9 = r9 / r10
                float r9 = r9 + r4
                goto L_0x02b8
            L_0x02ae:
                java.util.List<java.lang.Float> r10 = r0.S
                int r10 = r10.size()
                int r10 = r10 - r3
                if (r6 != r10) goto L_0x02ba
                goto L_0x029e
            L_0x02b8:
                int r8 = r8 + 1
            L_0x02ba:
                r10 = 2
                if (r8 != r10) goto L_0x02db
                int r10 = (r12 > r11 ? 1 : (r12 == r11 ? 0 : -1))
                if (r10 != 0) goto L_0x02c4
                r5[r3] = r14
                goto L_0x02e2
            L_0x02c4:
                if (r10 <= 0) goto L_0x02d0
                float r14 = r14 - r4
                float r6 = r11 - r15
                float r14 = r14 * r6
                float r12 = r12 - r15
                float r14 = r14 / r12
                float r14 = r14 + r4
                r5[r3] = r14
                goto L_0x02e2
            L_0x02d0:
                java.util.List<java.lang.Float> r4 = r0.S
                int r4 = r4.size()
                int r4 = r4 - r3
                if (r6 != r4) goto L_0x02db
                r5[r3] = r14
            L_0x02db:
                int r6 = r6 + 1
                r4 = 2
                r10 = 1073741824(0x40000000, float:2.0)
                goto L_0x023b
            L_0x02e2:
                boolean r4 = r0.L
                if (r4 == 0) goto L_0x0358
                java.util.ArrayList<com.miui.powercenter.batteryhistory.BatteryLevelChart$a$b> r4 = r0.V
                java.util.Iterator r4 = r4.iterator()
                r6 = 0
            L_0x02ed:
                boolean r8 = r4.hasNext()
                if (r8 == 0) goto L_0x031b
                java.lang.Object r8 = r4.next()
                com.miui.powercenter.batteryhistory.BatteryLevelChart$a$b r8 = (com.miui.powercenter.batteryhistory.BatteryLevelChart.a.b) r8
                com.miui.powercenter.batteryhistory.BatteryLevelChart$a$a r10 = r8.f6809a
                java.lang.Float r10 = r10.f6806a
                float r10 = r10.floatValue()
                int r10 = (r13 > r10 ? 1 : (r13 == r10 ? 0 : -1))
                if (r10 < 0) goto L_0x02ed
                com.miui.powercenter.batteryhistory.BatteryLevelChart$a$a r8 = r8.f6810b
                java.lang.Float r8 = r8.f6806a
                float r8 = r8.floatValue()
                int r8 = (r13 > r8 ? 1 : (r13 == r8 ? 0 : -1))
                if (r8 > 0) goto L_0x02ed
                r6 = r3
                goto L_0x02ed
            L_0x031b:
                android.graphics.Paint r4 = r0.z
                if (r6 == 0) goto L_0x0322
                int r8 = r0.G
                goto L_0x0324
            L_0x0322:
                int r8 = r0.F
            L_0x0324:
                r4.setColor(r8)
                boolean r4 = r0.M
                if (r4 == 0) goto L_0x0343
                android.graphics.Paint r4 = r0.B
                if (r6 == 0) goto L_0x0332
                int r6 = r0.I
                goto L_0x0334
            L_0x0332:
                int r6 = r0.H
            L_0x0334:
                r4.setColor(r6)
                r4 = 0
                r6 = r5[r4]
                int r8 = r0.k
                float r8 = (float) r8
                android.graphics.Paint r10 = r0.B
                r1.drawCircle(r13, r6, r8, r10)
                goto L_0x0344
            L_0x0343:
                r4 = 0
            L_0x0344:
                r6 = r5[r4]
                int r8 = r0.l
                float r8 = (float) r8
                android.graphics.Paint r10 = r0.A
                r1.drawCircle(r13, r6, r8, r10)
                r6 = r5[r4]
                int r4 = r0.l
                float r4 = (float) r4
                android.graphics.Paint r8 = r0.z
                r1.drawCircle(r13, r6, r4, r8)
            L_0x0358:
                java.util.ArrayList<com.miui.powercenter.batteryhistory.BatteryLevelChart$a$b> r4 = r0.V
                java.util.Iterator r4 = r4.iterator()
                r6 = 0
            L_0x035f:
                boolean r8 = r4.hasNext()
                if (r8 == 0) goto L_0x038d
                java.lang.Object r8 = r4.next()
                com.miui.powercenter.batteryhistory.BatteryLevelChart$a$b r8 = (com.miui.powercenter.batteryhistory.BatteryLevelChart.a.b) r8
                com.miui.powercenter.batteryhistory.BatteryLevelChart$a$a r10 = r8.f6809a
                java.lang.Float r10 = r10.f6806a
                float r10 = r10.floatValue()
                int r10 = (r11 > r10 ? 1 : (r11 == r10 ? 0 : -1))
                if (r10 < 0) goto L_0x035f
                com.miui.powercenter.batteryhistory.BatteryLevelChart$a$a r8 = r8.f6810b
                java.lang.Float r8 = r8.f6806a
                float r8 = r8.floatValue()
                int r8 = (r11 > r8 ? 1 : (r11 == r8 ? 0 : -1))
                if (r8 > 0) goto L_0x035f
                r6 = r3
                goto L_0x035f
            L_0x038d:
                android.graphics.Paint r4 = r0.z
                if (r6 == 0) goto L_0x0394
                int r8 = r0.G
                goto L_0x0396
            L_0x0394:
                int r8 = r0.F
            L_0x0396:
                r4.setColor(r8)
                boolean r4 = r0.L
                if (r4 == 0) goto L_0x03b7
                boolean r4 = r0.M
                if (r4 == 0) goto L_0x03b7
                android.graphics.Paint r4 = r0.B
                if (r6 == 0) goto L_0x03a8
                int r6 = r0.I
                goto L_0x03aa
            L_0x03a8:
                int r6 = r0.H
            L_0x03aa:
                r4.setColor(r6)
                r4 = r5[r3]
                int r6 = r0.k
                float r6 = (float) r6
                android.graphics.Paint r8 = r0.B
                r1.drawCircle(r11, r4, r6, r8)
            L_0x03b7:
                r4 = r5[r3]
                int r6 = r0.l
                float r6 = (float) r6
                android.graphics.Paint r8 = r0.A
                r1.drawCircle(r11, r4, r6, r8)
                r4 = r5[r3]
                int r5 = r0.l
                float r5 = (float) r5
                android.graphics.Paint r6 = r0.z
                r1.drawCircle(r11, r4, r5, r6)
                com.miui.powercenter.batteryhistory.BatteryLevelChart r1 = com.miui.powercenter.batteryhistory.BatteryLevelChart.this
                int r1 = r1.e
                float r1 = (float) r1
                r4 = 1073741824(0x40000000, float:2.0)
                float r1 = r1 / r4
                float r1 = r7 - r1
                com.miui.powercenter.batteryhistory.BatteryLevelChart r5 = com.miui.powercenter.batteryhistory.BatteryLevelChart.this
                int r5 = r5.e
                float r5 = (float) r5
                float r5 = r5 / r4
                float r7 = r7 + r5
                com.miui.powercenter.batteryhistory.BatteryLevelChart r4 = com.miui.powercenter.batteryhistory.BatteryLevelChart.this
                int r4 = r4.f
                float r4 = (float) r4
                float r9 = r9 - r4
                com.miui.powercenter.batteryhistory.BatteryLevelChart r4 = com.miui.powercenter.batteryhistory.BatteryLevelChart.this
                int r4 = r4.g
                float r4 = (float) r4
                float r9 = r9 - r4
                int r4 = r0.f
                float r5 = (float) r4
                int r5 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
                if (r5 >= 0) goto L_0x03f9
                float r1 = (float) r4
                goto L_0x0406
            L_0x03f9:
                int r4 = (r7 > r2 ? 1 : (r7 == r2 ? 0 : -1))
                if (r4 <= 0) goto L_0x0406
                com.miui.powercenter.batteryhistory.BatteryLevelChart r1 = com.miui.powercenter.batteryhistory.BatteryLevelChart.this
                int r1 = r1.e
                float r1 = (float) r1
                float r1 = r2 - r1
            L_0x0406:
                r2 = 1101004800(0x41a00000, float:20.0)
                int r4 = (r9 > r2 ? 1 : (r9 == r2 ? 0 : -1))
                if (r4 >= 0) goto L_0x040d
                goto L_0x040e
            L_0x040d:
                r2 = r9
            L_0x040e:
                boolean r4 = r0.L
                if (r4 == 0) goto L_0x04e9
                boolean r4 = r0.M
                if (r4 == 0) goto L_0x04e9
                com.miui.powercenter.batteryhistory.BatteryLevelChart r4 = com.miui.powercenter.batteryhistory.BatteryLevelChart.this
                com.miui.powercenter.view.ShadowTextView r4 = r4.f6800c
                int r4 = r4.getVisibility()
                if (r4 == 0) goto L_0x042c
                com.miui.powercenter.batteryhistory.BatteryLevelChart r4 = com.miui.powercenter.batteryhistory.BatteryLevelChart.this
                com.miui.powercenter.view.ShadowTextView r4 = r4.f6800c
                r5 = 0
                r4.setVisibility(r5)
            L_0x042c:
                com.miui.powercenter.batteryhistory.BatteryLevelChart r4 = com.miui.powercenter.batteryhistory.BatteryLevelChart.this
                com.miui.powercenter.view.ShadowTextView r4 = r4.f6800c
                float r4 = r4.getAlpha()
                r5 = 1065353216(0x3f800000, float:1.0)
                int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
                if (r4 == 0) goto L_0x045b
                com.miui.powercenter.batteryhistory.BatteryLevelChart r4 = com.miui.powercenter.batteryhistory.BatteryLevelChart.this
                com.miui.powercenter.view.ShadowTextView r4 = r4.f6800c
                android.view.ViewPropertyAnimator r4 = r4.animate()
                android.view.ViewPropertyAnimator r4 = r4.alpha(r5)
                android.view.ViewPropertyAnimator r4 = r4.scaleX(r5)
                android.view.ViewPropertyAnimator r4 = r4.scaleY(r5)
                r5 = 200(0xc8, double:9.9E-322)
                android.view.ViewPropertyAnimator r4 = r4.setDuration(r5)
                r4.start()
            L_0x045b:
                com.miui.powercenter.batteryhistory.BatteryLevelChart r4 = com.miui.powercenter.batteryhistory.BatteryLevelChart.this
                com.miui.powercenter.view.ShadowTextView r4 = r4.f6800c
                r4.setX(r1)
                com.miui.powercenter.batteryhistory.BatteryLevelChart r1 = com.miui.powercenter.batteryhistory.BatteryLevelChart.this
                com.miui.powercenter.view.ShadowTextView r1 = r1.f6800c
                r1.setY(r2)
                float[] r1 = r0.J
                r2 = 0
                r1 = r1[r2]
                int r1 = (int) r1
                float r1 = (float) r1
                int r1 = r0.a((float) r1)
                float[] r4 = r0.J
                r5 = r4[r3]
                r4 = r4[r2]
                float r5 = r5 - r4
                float r2 = r0.n
                float r5 = r5 / r2
                int r2 = java.lang.Math.round(r5)
                int r2 = java.lang.Math.max(r3, r2)
                java.util.List<com.miui.powercenter.batteryhistory.BatteryHistogramItem> r4 = r0.O
                java.lang.Object r1 = r4.get(r1)
                com.miui.powercenter.batteryhistory.BatteryHistogramItem r1 = (com.miui.powercenter.batteryhistory.BatteryHistogramItem) r1
                long r4 = r1.startUTCTime
                long r1 = (long) r2
                r6 = 3600000(0x36ee80, double:1.7786363E-317)
                long r1 = r1 * r6
                long r1 = r1 + r4
                float[] r8 = r0.J
                r8 = r8[r3]
                float r9 = r0.o
                int r8 = (r8 > r9 ? 1 : (r8 == r9 ? 0 : -1))
                if (r8 < 0) goto L_0x04b4
                java.util.List<com.miui.powercenter.batteryhistory.BatteryHistogramItem> r1 = r0.O
                int r2 = r1.size()
                int r2 = r2 - r3
                java.lang.Object r1 = r1.get(r2)
                com.miui.powercenter.batteryhistory.BatteryHistogramItem r1 = (com.miui.powercenter.batteryhistory.BatteryHistogramItem) r1
                long r1 = r1.startUTCTime
                long r1 = r1 + r6
            L_0x04b4:
                java.util.Calendar r6 = java.util.Calendar.getInstance()
                r6.setTimeInMillis(r4)
                r4 = 11
                int r5 = r6.get(r4)
                r6.setTimeInMillis(r1)
                int r1 = r6.get(r4)
                com.miui.powercenter.batteryhistory.BatteryLevelChart r2 = com.miui.powercenter.batteryhistory.BatteryLevelChart.this
                com.miui.powercenter.view.ShadowTextView r2 = r2.f6800c
                java.util.Locale r4 = java.util.Locale.ENGLISH
                r6 = 2
                java.lang.Object[] r6 = new java.lang.Object[r6]
                java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
                r7 = 0
                r6[r7] = r5
                java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
                r6[r3] = r1
                java.lang.String r1 = "%1$02d:00-%2$02d:00"
                java.lang.String r1 = java.lang.String.format(r4, r1, r6)
                r2.setText(r1)
            L_0x04e9:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.powercenter.batteryhistory.BatteryLevelChart.a.b(android.graphics.Canvas):void");
        }

        private C0060a c(float f2, float f3) {
            float f4;
            float f5 = f2 + ((f3 - f2) / 2.0f);
            int i2 = 1;
            while (true) {
                if (i2 >= this.S.size()) {
                    f4 = 0.0f;
                    break;
                }
                float floatValue = this.S.get(i2).floatValue();
                f4 = this.T.get(i2).floatValue();
                int i3 = i2 - 1;
                float floatValue2 = this.S.get(i3).floatValue();
                float floatValue3 = this.T.get(i3).floatValue();
                int i4 = (floatValue > f5 ? 1 : (floatValue == f5 ? 0 : -1));
                if (i4 == 0) {
                    break;
                } else if (i4 > 0) {
                    f4 = (((f4 - floatValue3) * (f5 - floatValue2)) / (floatValue - floatValue2)) + floatValue3;
                    break;
                } else {
                    i2++;
                }
            }
            return new C0060a(Float.valueOf(f5), Float.valueOf(f4));
        }

        private void d() {
            if (getWidth() != 0) {
                this.S.clear();
                this.T.clear();
                this.U.clear();
                this.V.clear();
                this.W.clear();
                float f2 = 0.0f;
                float f3 = 0.0f;
                b bVar = new b();
                Boolean bool = null;
                for (int i2 = 0; i2 < this.P.size(); i2++) {
                    float floatValue = this.P.get(i2).floatValue();
                    float floatValue2 = this.Q.get(i2).floatValue();
                    if (floatValue2 - -1.0f > 1.0E-4f) {
                        if (this.S.size() > 0) {
                            List<Float> list = this.S;
                            float floatValue3 = list.get(list.size() - 1).floatValue();
                            List<Float> list2 = this.T;
                            float floatValue4 = list2.get(list2.size() - 1).floatValue();
                            if (floatValue4 - -1.0f > 1.0E-4f && !d(floatValue3, floatValue) && !d(floatValue4, floatValue2)) {
                            }
                        }
                        this.S.add(Float.valueOf(floatValue));
                        this.T.add(Float.valueOf(floatValue2));
                        boolean booleanValue = this.R.get(i2).booleanValue();
                        this.U.add(Boolean.valueOf(booleanValue));
                        if (bool == null) {
                            bool = Boolean.valueOf(booleanValue);
                        }
                        if (!bVar.a()) {
                            C0060a unused = bVar.f6809a = new C0060a(Float.valueOf(floatValue), Float.valueOf(floatValue2));
                        } else if (bool.booleanValue() != booleanValue) {
                            C0060a unused2 = bVar.f6810b = new C0060a(Float.valueOf(floatValue), Float.valueOf(floatValue2));
                            (bool.booleanValue() ? this.V : this.W).add(bVar);
                            b bVar2 = new b();
                            C0060a unused3 = bVar2.f6809a = new C0060a(Float.valueOf(floatValue), Float.valueOf(floatValue2));
                            Boolean valueOf = Boolean.valueOf(booleanValue);
                            f2 = floatValue;
                            f3 = floatValue2;
                            Boolean bool2 = valueOf;
                            bVar = bVar2;
                            bool = bool2;
                        }
                        f2 = floatValue;
                        f3 = floatValue2;
                    }
                }
                if (bool != null && bVar.a()) {
                    C0060a unused4 = bVar.f6810b = new C0060a(Float.valueOf(f2), Float.valueOf(f3));
                    (bool.booleanValue() ? this.V : this.W).add(bVar);
                }
                this.D.reset();
                Iterator<b> it = this.V.iterator();
                while (it.hasNext()) {
                    b next = it.next();
                    C0060a c2 = c(next.f6809a.f6806a.floatValue(), next.f6810b.f6806a.floatValue());
                    float floatValue5 = c2.f6806a.floatValue();
                    float floatValue6 = c2.f6807b.floatValue() + 30.0f + 22.5f;
                    if (((float) this.e) - floatValue6 >= 22.5f && ((float) this.g) - floatValue5 >= 28.0f && floatValue5 - ((float) this.f) >= 28.0f) {
                        float f4 = floatValue5 - 14.0f;
                        float f5 = floatValue6 + 3.8606f;
                        this.D.moveTo(f4, f5);
                        float f6 = f4 + 12.7335f;
                        this.D.lineTo(f6, f5);
                        this.D.lineTo(f6, f5 + 18.6394f);
                        float f7 = floatValue5 + 14.0f;
                        float f8 = floatValue6 - 3.8606f;
                        this.D.lineTo(f7, f8);
                        float f9 = f7 - 12.7335f;
                        this.D.lineTo(f9, f8);
                        this.D.lineTo(f9, floatValue6 - 22.5f);
                        this.D.close();
                    }
                }
            }
        }

        private boolean d(float f2, float f3) {
            return Math.abs(((int) f3) - ((int) f2)) >= this.u;
        }

        private Long e() {
            Calendar instance = Calendar.getInstance();
            instance.set(11, 0);
            instance.set(12, 0);
            instance.set(13, 0);
            return Long.valueOf(instance.getTimeInMillis());
        }

        /* access modifiers changed from: private */
        public void f() {
            this.K = true;
            this.L = false;
            BatteryLevelChart.this.f6800c.setVisibility(8);
            BatteryLevelChart.this.f6800c.setAlpha(0.0f);
            a(0.0f, (float) (this.g - this.f));
        }

        /* access modifiers changed from: private */
        public void g() {
            Message message = new Message();
            message.what = 10003;
            message.arg1 = -1;
            message.arg2 = -1;
            i.a().a(message);
        }

        /* access modifiers changed from: private */
        public void h() {
            if (this.K) {
                g();
                return;
            }
            int a2 = a((float) ((int) this.J[0]));
            float[] fArr = this.J;
            int max = Math.max(1, Math.round((fArr[1] - fArr[0]) / this.n)) + a2;
            if (this.J[1] >= this.o) {
                max = this.O.size();
            }
            if (max > this.O.size() - 1) {
                max = this.O.size();
            }
            if (a2 >= 0 && max <= this.O.size() && a2 <= max) {
                Message message = new Message();
                message.what = 10003;
                message.arg1 = a2;
                message.arg2 = max;
                i.a().a(message);
            }
        }

        /* access modifiers changed from: package-private */
        public void a() {
            long j2;
            float f2;
            Path path;
            int i2 = this.g;
            int height = getHeight();
            this.f6803b.reset();
            this.P.clear();
            this.Q.clear();
            this.R.clear();
            long j3 = this.q;
            long j4 = this.r - j3;
            int i3 = height - this.f6805d;
            float f3 = 0.0f;
            float f4 = -1.0f;
            float f5 = -1.0f;
            Path path2 = null;
            for (aa next : this.N) {
                byte b2 = next.f6867d;
                byte b3 = next.f;
                Log.d("BatteryLevelChart", "raw data: " + next.b());
                if (next.b()) {
                    byte b4 = next.f6866c;
                    long a2 = next.a() - j3;
                    int i4 = this.f;
                    j2 = j3;
                    float f6 = ((((float) (((long) (i2 - i4)) * a2)) * 1.0f) / ((float) j4)) + ((float) i4);
                    float f7 = ((float) this.e) - ((((float) ((b4 + 0) * ((i3 - i4) - 1))) * 1.0f) / 100.0f);
                    if (f5 == f6 || f4 == f7) {
                        f2 = f5;
                        path = path2;
                    } else {
                        Path path3 = this.f6803b;
                        if (path3 != path2) {
                            if (path2 != null) {
                                path2.lineTo(f6, f7);
                            }
                            path3.moveTo(f6, f7);
                        } else {
                            path3.lineTo(f6, f7);
                            path3 = path2;
                        }
                        this.P.add(Float.valueOf(f6));
                        this.Q.add(Float.valueOf(f7));
                        this.R.add(Boolean.valueOf(a((int) b2, (int) b3)));
                        path = path3;
                        f2 = f6;
                        f4 = f7;
                    }
                    f3 = f6;
                    path2 = path;
                    f5 = f2;
                } else {
                    j2 = j3;
                    if (!next.c()) {
                        a(f3 + 1.0f, f4, f5, path2, a((int) b2, (int) b3));
                        Log.d("BatteryLevelChart", "is over flow: " + next.c());
                        f4 = -1.0f;
                        f5 = -1.0f;
                        path2 = null;
                    }
                }
                j3 = j2;
            }
            a((float) i2, f4, f5, path2, o.k(getContext()));
        }

        public void a(float f2, float f3) {
            float f4;
            if (this.S.size() > 0) {
                List<Float> list = this.S;
                f4 = list.get(list.size() - 1).floatValue();
            } else {
                f4 = (float) this.g;
            }
            int i2 = this.f;
            if (f2 < ((float) i2)) {
                f3 = (f3 + ((float) i2)) - f2;
                f2 = (float) i2;
            }
            if (f3 > f4) {
                f2 = (f2 - f3) + f4;
                f3 = f4;
            }
            float[] fArr = this.J;
            fArr[0] = f2;
            fArr[1] = f3;
        }

        /* access modifiers changed from: package-private */
        public void a(float f2, float f3, float f4, Path path, boolean z2) {
            if (path != null && f4 >= 0.0f && f4 < f2) {
                path.lineTo(f2, f3);
                this.P.add(Float.valueOf(f2));
                this.Q.add(Float.valueOf(f3));
                this.R.add(Boolean.valueOf(z2));
            }
        }

        @SuppressLint({"NewApi"})
        public void a(float f2, float f3, boolean z2) {
            ValueAnimator valueAnimator = this.ba;
            if (valueAnimator == null || !valueAnimator.isRunning()) {
                float f4 = f3 / 2.0f;
                this.ba = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f}).setDuration(800);
                this.ba.setInterpolator(new QuarticEaseOutInterpolator());
                this.ba.addUpdateListener(new E(this, (float) this.f, f2 - f4, (float) this.g, f2 + f4));
                this.ba.addListener(new F(this));
                this.ba.start();
                this.L = true;
            }
        }

        /* access modifiers changed from: package-private */
        public void a(Canvas canvas) {
            Canvas canvas2 = canvas;
            int i2 = this.f6805d;
            int i3 = this.f;
            int i4 = this.g;
            Path path = new Path();
            float f2 = (float) i3;
            float f3 = (float) i2;
            path.moveTo(f2, f3);
            float f4 = (float) i4;
            path.lineTo(f4, f3);
            path.moveTo(f2, (float) this.e);
            path.lineTo(f4, (float) this.e);
            int i5 = (this.e - i2) / 5;
            this.f6802a.setTextAlign(Paint.Align.LEFT);
            for (int i6 = 0; i6 < 6; i6++) {
                int i7 = (i5 * i6) + i2;
                canvas2.drawText(u.a(getContext(), 100 - (i6 * 20)), 0.0f, (float) ((this.f6804c / 2) + i7), this.f6802a);
                if (i6 != 0) {
                    if (i6 != 5) {
                        float f5 = (float) i7;
                        path.moveTo(f2, f5);
                        path.lineTo(f4, f5);
                    }
                }
            }
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.parseColor("#33000000"));
            this.f6802a.setTextAlign(Paint.Align.CENTER);
            int size = this.O.size();
            int i8 = i4 - i3;
            float abs = ((float) Math.abs(i8)) / ((float) size);
            Calendar instance = Calendar.getInstance();
            instance.setTimeInMillis(System.currentTimeMillis());
            int i9 = instance.get(11);
            long j2 = this.t - this.s;
            long currentTimeMillis = System.currentTimeMillis() - j2;
            long longValue = e().longValue();
            Path path2 = path;
            String string = getResources().getString(R.string.pc_battery_detail_curve_reference_now);
            float measureText = this.f6802a.measureText(string) * 1.5f;
            String str = string;
            float f6 = f4;
            if (longValue > currentTimeMillis) {
                float f7 = (float) ((int) ((((((float) (longValue - currentTimeMillis)) * 1.0f) / ((float) j2)) * ((float) i8)) + f2));
                canvas.drawLine(f7, f3, f7, (float) this.e, paint);
                float f8 = f7;
                int i10 = 0;
                while (f8 >= f2) {
                    canvas2.drawText(String.format(Locale.getDefault(), "%d", new Object[]{Integer.valueOf(i10)}), f8, (float) this.h, this.f6802a);
                    f8 -= abs * 3.0f;
                    i10 -= 3;
                    if (i10 < 0) {
                        i10 += 24;
                    }
                }
                float f9 = abs * 3.0f;
                float f10 = f7 + f9;
                int i11 = 3;
                while (f10 <= f6 - measureText) {
                    canvas2.drawText(String.format(Locale.getDefault(), "%d", new Object[]{Integer.valueOf(i11)}), f10, (float) this.h, this.f6802a);
                    f10 += f9;
                    i11 += 3;
                    if (i11 >= 24) {
                        i11 -= 24;
                    }
                }
            } else if (size > 0) {
                int i12 = (i9 - size) + 1;
                if (i12 < 0) {
                    i12 += 24;
                }
                while (f2 <= f6 - measureText) {
                    canvas2.drawText(String.format(Locale.getDefault(), "%d", new Object[]{Integer.valueOf(r2)}), f2, (float) this.h, this.f6802a);
                    f2 += abs * 3.0f;
                    int i13 = i13 + 3;
                    if (i13 >= 24) {
                        i13 -= 24;
                    }
                }
            }
            this.f6802a.setTextAlign(Paint.Align.RIGHT);
            canvas2.drawText(str, f6, (float) this.h, this.f6802a);
            paint.setColor(getResources().getColor(R.color.pc_power_center_text_true));
            paint.setPathEffect(new DashPathEffect(new float[]{3.0f, 3.0f}, 0.0f));
            canvas2.drawPath(path2, paint);
        }

        public boolean a(int i2, int i3) {
            return (i2 == 2 || i2 == 5) && i3 != 0;
        }

        public boolean a(List<aa> list, List<BatteryHistogramItem> list2) {
            boolean z2 = false;
            if (list == null || list.isEmpty() || list2 == null || list2.isEmpty()) {
                BatteryLevelChart.this.f6800c.setVisibility(8);
                BatteryLevelChart.this.b();
                return false;
            }
            this.N.clear();
            this.N.addAll(list);
            this.O.clear();
            this.O.addAll(list2);
            this.p = list.size();
            this.q = list.get(0).a();
            this.r = list.get(list.size() - 1).a();
            long j2 = this.r;
            long j3 = this.q;
            if (j2 <= j3) {
                this.r = j3 + 1;
            }
            this.s = list2.get(0).startTime;
            this.t = list2.get(list2.size() - 1).startTime + 3600000;
            long j4 = this.t;
            long j5 = this.s;
            if (j4 <= j5) {
                this.t = j5 + 1;
            }
            if (this.O.size() > 1) {
                float f2 = ((((float) (this.O.get(0).endTime - this.O.get(0).startTime)) / 1000.0f) / 60.0f) / 60.0f;
                List<BatteryHistogramItem> list3 = this.O;
                float f3 = ((float) list3.get(list3.size() - 1).minLastItemHold) / 60.0f;
                int size = ((this.O.size() - 1) / 8) + 1;
                if (size > 3) {
                    size = 3;
                }
                float f4 = f2 + f3;
                float f5 = (float) size;
                if (((float) (this.O.size() - 2)) + f4 > f5) {
                    z2 = true;
                }
                this.M = z2;
                if (this.M) {
                    this.m = (((float) (this.g - this.f)) / (((float) (this.O.size() - 2)) + f4)) * f5;
                    this.n = ((float) (this.g - this.f)) / (f4 + ((float) (this.O.size() - 2)));
                    int i2 = this.g;
                    this.o = ((float) i2) - Math.min((float) i2, f3 * this.n);
                    a();
                    d();
                    a(0.0f, (float) (this.g - this.f));
                    b();
                    return true;
                }
            } else {
                this.M = false;
            }
            this.m = (float) (this.g - this.f);
            this.n = this.m;
            a();
            d();
            a(0.0f, (float) (this.g - this.f));
            b();
            return true;
        }

        @SuppressLint({"NewApi"})
        public void b() {
            ValueAnimator valueAnimator = this.ba;
            if (valueAnimator == null || !valueAnimator.isRunning()) {
                this.ba = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f}).setDuration(800);
                this.ba.setInterpolator(new QuarticEaseOutInterpolator());
                this.ba.addUpdateListener(new A(this));
                this.ba.addListener(new B(this));
                this.ba.start();
            }
        }

        @SuppressLint({"NewApi"})
        public void b(float f2, float f3) {
            ValueAnimator valueAnimator = this.ba;
            if (valueAnimator == null || !valueAnimator.isRunning()) {
                this.ba = ValueAnimator.ofFloat(new float[]{f2, f3}).setDuration(400);
                this.ba.setInterpolator(new AccelerateDecelerateInterpolator());
                this.ba.addUpdateListener(new C(this));
                this.ba.addListener(new D(this));
                this.ba.start();
            }
        }

        @SuppressLint({"NewApi"})
        public void c() {
            ValueAnimator valueAnimator = this.ba;
            if (valueAnimator == null || !valueAnimator.isRunning()) {
                float[] fArr = this.J;
                float f2 = fArr[0];
                float f3 = fArr[1];
                float f4 = (float) this.f;
                float f5 = (float) this.g;
                this.ba = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f}).setDuration(800);
                this.ba.setInterpolator(new QuarticEaseOutInterpolator());
                this.ba.addUpdateListener(new G(this, f2, f4, f3, f5));
                this.ba.addListener(new H(this));
                this.ba.start();
                BatteryLevelChart.this.f6800c.animate().alpha(0.0f).scaleX(0.8f).scaleY(0.8f).setDuration(200).start();
                this.L = false;
            }
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            a(canvas);
            if (!this.S.isEmpty()) {
                b(canvas);
            }
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z2, int i2, int i3, int i4, int i5) {
            super.onLayout(z2, i2, i3, i4, i5);
            int height = getHeight();
            int descent = (((int) this.f6802a.descent()) - ((int) this.f6802a.ascent())) / 2;
            this.f6804c = descent;
            if (this.f6804c <= 0) {
                this.f6804c = 1;
            }
            this.g = i4 - BatteryLevelChart.this.f6798a.getResources().getDimensionPixelSize(R.dimen.pc_power_history_layout_padding_horizon_chart_offset);
            this.f = BatteryLevelChart.this.f6798a.getResources().getDimensionPixelSize(R.dimen.pc_power_history_chart_left);
            this.f6805d = BatteryLevelChart.this.f6798a.getResources().getDimensionPixelSize(R.dimen.pc_power_history_chart_top);
            int i6 = this.f6805d;
            this.e = (i6 + (height - i6)) - this.f;
            this.h = this.e + getResources().getDimensionPixelSize(R.dimen.pc_power_history_chart_current_text_offset) + descent;
            this.w.setShader(new LinearGradient(0.0f, 0.0f, 0.0f, (float) this.e, a(this.F, 0.36f), getResources().getColor(R.color.pc_battery_statics_chart_color_to), Shader.TileMode.REPEAT));
            this.x.setShader(new LinearGradient(0.0f, 0.0f, 0.0f, (float) this.e, a(this.G, 0.5f), getResources().getColor(R.color.pc_battery_statics_chart_color_to), Shader.TileMode.REPEAT));
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (!(!this.M || this.N.size() == 0 || this.O.size() == 0)) {
                int action = motionEvent.getAction();
                int x2 = (int) motionEvent.getX();
                if (action == 0) {
                    this.ia = false;
                    this.ha = x2;
                    this.ga = x2;
                    int i2 = this.f;
                    int i3 = this.fa;
                    int i4 = i2 - i3;
                    int i5 = this.ga;
                    if (i4 <= i5 && i5 <= this.g + i3) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                        if (!this.K) {
                            int i6 = this.ga;
                            float[] fArr = this.J;
                            if (((float) i6) <= fArr[0] && ((float) i6) >= fArr[0] - ((float) this.fa)) {
                                this.ea = 1;
                                return true;
                            }
                        }
                        if (!this.K) {
                            int i7 = this.ga;
                            float[] fArr2 = this.J;
                            if (((float) i7) <= fArr2[1] + ((float) this.fa) && ((float) i7) >= fArr2[1]) {
                                this.ea = 2;
                                return true;
                            }
                        }
                        if (!this.K) {
                            int i8 = this.ga;
                            float[] fArr3 = this.J;
                            if (((float) i8) > fArr3[0] && ((float) i8) < fArr3[1]) {
                                this.da = true;
                                return true;
                            }
                        }
                        this.ca = !this.K;
                        return true;
                    }
                } else if (action == 1) {
                    if (this.ca) {
                        this.ca = false;
                        b(this.J[0], ((float) this.ha) - (this.m / 2.0f));
                    } else if (this.K && this.M) {
                        a((float) this.ha, this.m, false);
                    } else if (!this.ia && !this.K) {
                        this.da = false;
                        c();
                    }
                    if (this.da || this.ea > 0) {
                        this.da = false;
                        this.ea = 0;
                        h();
                    }
                } else if (action == 2) {
                    if (Math.abs(x2 - this.ga) > this.E) {
                        this.ia = true;
                    }
                    int i9 = this.ea;
                    if (i9 <= 0) {
                        if (this.da && !this.K) {
                            float[] fArr4 = this.J;
                            float f2 = (float) x2;
                            int i10 = this.ha;
                            a((fArr4[0] + f2) - ((float) i10), (fArr4[1] + f2) - ((float) i10));
                        }
                        this.ha = x2;
                    } else if ((i9 != 2 || this.J[1] < ((float) this.g) || x2 - this.ha <= 0) && (this.ea != 1 || this.J[0] > ((float) this.f) || x2 - this.ha >= 0)) {
                        if (this.ea == 1) {
                            float[] fArr5 = this.J;
                            fArr5[0] = (fArr5[0] + ((float) x2)) - ((float) this.ha);
                            float f3 = fArr5[0];
                            int i11 = this.f;
                            if (f3 < ((float) i11)) {
                                fArr5[0] = (float) i11;
                            } else {
                                float f4 = fArr5[0];
                                float f5 = fArr5[1];
                                float f6 = this.n;
                                if (f4 > f5 - f6) {
                                    fArr5[0] = fArr5[1] - f6;
                                }
                            }
                        } else {
                            float[] fArr6 = this.J;
                            fArr6[1] = (fArr6[1] + ((float) x2)) - ((float) this.ha);
                            float f7 = fArr6[1];
                            int i12 = this.g;
                            if (f7 > ((float) i12)) {
                                fArr6[1] = (float) i12;
                            } else {
                                float f8 = fArr6[1];
                                float f9 = fArr6[0];
                                float f10 = this.n;
                                if (f8 < f9 + f10) {
                                    fArr6[1] = fArr6[0] + f10;
                                }
                            }
                        }
                        float[] fArr7 = this.J;
                        this.m = fArr7[1] - fArr7[0];
                        float f11 = this.m;
                        float f12 = this.n;
                        if (f11 < f12) {
                            this.m = f12;
                        } else {
                            int i13 = this.g;
                            int i14 = this.f;
                            if (f11 > ((float) (i13 - i14))) {
                                this.m = (float) (i13 - i14);
                            }
                        }
                    }
                    invalidate();
                    this.ha = x2;
                }
                getParent().requestDisallowInterceptTouchEvent(false);
            }
            return false;
        }
    }

    public BatteryLevelChart(Context context) {
        this(context, (AttributeSet) null);
    }

    public BatteryLevelChart(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public BatteryLevelChart(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.f6798a = context;
        this.e = context.getResources().getDimensionPixelSize(R.dimen.pc_power_history_float_text_width);
        this.f = context.getResources().getDimensionPixelSize(R.dimen.pc_power_history_float_text_height);
        this.g = this.f;
        this.f6799b = new a(this, context);
        addView(this.f6799b, new RelativeLayout.LayoutParams(-1, -1));
        LayoutInflater.from(context).inflate(R.layout.pc_battery_statics_chart_time, this);
        this.f6800c = (ShadowTextView) findViewById(R.id.float_text);
        if (k.a() > 8) {
            this.f6800c.a(t.a(), 1);
        }
    }

    /* access modifiers changed from: private */
    public void b() {
        Message message = new Message();
        message.what = 10005;
        i.a().a(message);
    }

    public void a() {
        this.f6799b.f();
    }

    public void a(List<aa> list, List<BatteryHistogramItem> list2) {
        this.f6799b.a(list, list2);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        this.e = this.f6800c.getWidth();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.f6798a instanceof Activity) {
            Point point = new Point();
            ((Activity) this.f6798a).getWindowManager().getDefaultDisplay().getRealSize(point);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) this.f6798a).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            this.f6801d = displayMetrics.density;
            if (point.y <= 1920) {
                setMeasuredDimension(getMeasuredWidth(), this.f6798a.getResources().getDimensionPixelSize(R.dimen.pc_battery_statics_chart_height_mini));
            }
        }
    }
}
