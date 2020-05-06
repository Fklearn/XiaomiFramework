package com.miui.powercenter.mainui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainBatteryView extends RelativeLayout {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public int f7113a;

    /* renamed from: b  reason: collision with root package name */
    private float f7114b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public float f7115c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public float f7116d;
    private float e;
    /* access modifiers changed from: private */
    public Paint f;
    /* access modifiers changed from: private */
    public Paint g;
    /* access modifiers changed from: private */
    public Paint h;
    private boolean i;
    private boolean j;
    /* access modifiers changed from: private */
    public LinearGradient k;
    /* access modifiers changed from: private */
    public Button l;
    /* access modifiers changed from: private */
    public int m;
    /* access modifiers changed from: private */
    public int n;
    private a o;
    private ValueAnimator p;
    private boolean q;
    private boolean r;
    /* access modifiers changed from: private */
    public boolean s;
    /* access modifiers changed from: private */
    public int t;
    /* access modifiers changed from: private */
    public float u;
    private List<a> v;
    private Random w;

    public class a {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public float f7117a;
        /* access modifiers changed from: private */

        /* renamed from: b  reason: collision with root package name */
        public float f7118b;
        /* access modifiers changed from: private */

        /* renamed from: c  reason: collision with root package name */
        public float f7119c;

        /* renamed from: d  reason: collision with root package name */
        private Paint f7120d;
        private ValueAnimator e;

        public a() {
        }

        public ValueAnimator a() {
            return this.e;
        }

        public void a(float f2) {
            this.f7119c = f2;
        }

        public void a(ValueAnimator valueAnimator) {
            this.e = valueAnimator;
        }

        public void a(Paint paint) {
            this.f7120d = paint;
        }

        public Paint b() {
            return this.f7120d;
        }

        public void b(float f2) {
            this.f7117a = f2;
        }

        public float c() {
            return this.f7119c;
        }

        public void c(float f2) {
            this.f7118b = f2;
        }
    }

    public MainBatteryView(Context context) {
        this(context, (AttributeSet) null);
    }

    public MainBatteryView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.f7113a = -1;
        this.t = 100;
        this.v = new ArrayList();
        this.w = new Random();
        this.f = new Paint();
        this.f.setStyle(Paint.Style.FILL);
        this.f.setAntiAlias(true);
        this.g = new Paint();
        this.g.setStyle(Paint.Style.FILL);
        this.g.setAntiAlias(true);
        this.g.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        this.f7115c = context.getResources().getDimension(R.dimen.pc_battery_view_width);
        this.f7114b = context.getResources().getDimension(R.dimen.pc_battery_view_height);
        this.f7116d = context.getResources().getDimension(R.dimen.applock_fod_finger_switch);
        this.e = context.getResources().getDimension(R.dimen.view_dimen_48);
        this.u = this.f7115c;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:23:0x013f, code lost:
        if (r14.q != false) goto L_0x00d3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void a() {
        /*
            r14 = this;
            int r0 = r14.f7113a
            r1 = 20
            r2 = 1
            r3 = 0
            if (r0 < r1) goto L_0x0144
            boolean r0 = r14.j
            if (r0 == 0) goto L_0x0144
            boolean r0 = r14.i
            if (r0 != 0) goto L_0x0144
            java.util.List<com.miui.powercenter.mainui.MainBatteryView$a> r0 = r14.v
            int r0 = r0.size()
            r4 = -1
            r5 = 2
            r6 = 10
            if (r0 >= r6) goto L_0x00d9
            r0 = r3
        L_0x001d:
            if (r0 >= r6) goto L_0x008e
            com.miui.powercenter.mainui.MainBatteryView$a r7 = new com.miui.powercenter.mainui.MainBatteryView$a
            r7.<init>()
            java.util.Random r8 = r14.w
            float r8 = r8.nextFloat()
            float r9 = r14.f7114b
            r10 = 1101004800(0x41a00000, float:20.0)
            float r9 = r9 - r10
            float r8 = r8 * r9
            r9 = 1073741824(0x40000000, float:2.0)
            float r8 = r8 + r9
            java.util.Random r9 = r14.w
            float r9 = r9.nextFloat()
            r10 = 1092616192(0x41200000, float:10.0)
            float r9 = r9 * r10
            r10 = 1088421888(0x40e00000, float:7.0)
            float r9 = r9 + r10
            java.util.Random r10 = r14.w
            r11 = 4
            int r10 = r10.nextInt(r11)
            int r10 = r10 + r11
            java.util.Random r11 = r14.w
            int r11 = r11.nextInt(r6)
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r13 = "#"
            r12.append(r13)
            r12.append(r10)
            r12.append(r11)
            java.lang.String r10 = "FFFFFF"
            r12.append(r10)
            java.lang.String r10 = r12.toString()
            android.graphics.Paint r11 = new android.graphics.Paint
            r11.<init>()
            int r10 = android.graphics.Color.parseColor(r10)
            r11.setColor(r10)
            android.graphics.Paint$Style r10 = android.graphics.Paint.Style.FILL
            r11.setStyle(r10)
            r11.setAntiAlias(r2)
            r7.c((float) r8)
            r7.a((android.graphics.Paint) r11)
            r7.a((float) r9)
            r14.a((com.miui.powercenter.mainui.MainBatteryView.a) r7)
            java.util.List<com.miui.powercenter.mainui.MainBatteryView$a> r8 = r14.v
            r8.add(r7)
            int r0 = r0 + 1
            goto L_0x001d
        L_0x008e:
            r0 = 3
            int[] r0 = new int[r0]
            java.lang.String r6 = "#3FD268"
            int r7 = android.graphics.Color.parseColor(r6)
            r0[r3] = r7
            java.lang.String r7 = "#4CF076"
            int r7 = android.graphics.Color.parseColor(r7)
            r0[r2] = r7
            int r6 = android.graphics.Color.parseColor(r6)
            r0[r5] = r6
            android.animation.ValueAnimator r0 = android.animation.ValueAnimator.ofInt(r0)
            r5 = 3500(0xdac, double:1.729E-320)
            android.animation.ValueAnimator r0 = r0.setDuration(r5)
            r14.p = r0
            android.animation.ValueAnimator r0 = r14.p
            r0.setRepeatCount(r4)
            android.animation.ValueAnimator r0 = r14.p
            android.animation.ArgbEvaluator r4 = new android.animation.ArgbEvaluator
            r4.<init>()
            r0.setEvaluator(r4)
            android.animation.ValueAnimator r0 = r14.p
            r4 = 2000(0x7d0, double:9.88E-321)
            r0.setStartDelay(r4)
            android.animation.ValueAnimator r0 = r14.p
            com.miui.powercenter.mainui.f r4 = new com.miui.powercenter.mainui.f
            r4.<init>(r14)
            r0.addUpdateListener(r4)
        L_0x00d3:
            android.animation.ValueAnimator r0 = r14.p
            r0.start()
            goto L_0x0142
        L_0x00d9:
            java.util.List<com.miui.powercenter.mainui.MainBatteryView$a> r0 = r14.v
            int r0 = r0.size()
            if (r0 != r6) goto L_0x0142
            r0 = r3
        L_0x00e2:
            java.util.List<com.miui.powercenter.mainui.MainBatteryView$a> r6 = r14.v
            int r6 = r6.size()
            if (r0 >= r6) goto L_0x013d
            java.util.Random r6 = r14.w
            r7 = 2000(0x7d0, float:2.803E-42)
            int r6 = r6.nextInt(r7)
            java.util.List<com.miui.powercenter.mainui.MainBatteryView$a> r7 = r14.v
            java.lang.Object r7 = r7.get(r0)
            com.miui.powercenter.mainui.MainBatteryView$a r7 = (com.miui.powercenter.mainui.MainBatteryView.a) r7
            r14.o = r7
            com.miui.powercenter.mainui.MainBatteryView$a r7 = r14.o
            android.animation.ValueAnimator r7 = r7.a()
            float[] r8 = new float[r5]
            com.miui.powercenter.mainui.MainBatteryView$a r9 = r14.o
            float r9 = r9.c()
            float r9 = -r9
            r8[r3] = r9
            int r9 = r14.f7113a
            float r9 = (float) r9
            float r10 = r14.f7115c
            float r9 = r9 * r10
            r10 = 1120403456(0x42c80000, float:100.0)
            float r9 = r9 / r10
            com.miui.powercenter.mainui.MainBatteryView$a r10 = r14.o
            float r10 = r10.c()
            float r9 = r9 - r10
            r8[r2] = r9
            r7.setFloatValues(r8)
            boolean r8 = r14.q
            if (r8 == 0) goto L_0x013a
            com.miui.powercenter.mainui.MainBatteryView$a r8 = r14.o
            float r9 = r8.c()
            float r9 = -r9
            r8.b((float) r9)
            r7.setRepeatCount(r4)
            long r8 = (long) r6
            r7.setStartDelay(r8)
            r7.start()
        L_0x013a:
            int r0 = r0 + 1
            goto L_0x00e2
        L_0x013d:
            boolean r0 = r14.q
            if (r0 == 0) goto L_0x0142
            goto L_0x00d3
        L_0x0142:
            r14.q = r3
        L_0x0144:
            int r0 = r14.f7113a
            if (r0 < r1) goto L_0x0150
            boolean r0 = r14.j
            if (r0 == 0) goto L_0x0150
            boolean r0 = r14.i
            if (r0 == 0) goto L_0x0179
        L_0x0150:
            r0 = r3
        L_0x0151:
            java.util.List<com.miui.powercenter.mainui.MainBatteryView$a> r1 = r14.v
            int r1 = r1.size()
            if (r0 >= r1) goto L_0x0177
            java.util.List<com.miui.powercenter.mainui.MainBatteryView$a> r1 = r14.v
            java.lang.Object r1 = r1.get(r0)
            com.miui.powercenter.mainui.MainBatteryView$a r1 = (com.miui.powercenter.mainui.MainBatteryView.a) r1
            android.animation.ValueAnimator r1 = r1.a()
            if (r1 == 0) goto L_0x016d
            r1.setRepeatCount(r3)
            r1.cancel()
        L_0x016d:
            android.animation.ValueAnimator r1 = r14.p
            if (r1 == 0) goto L_0x0174
            r1.cancel()
        L_0x0174:
            int r0 = r0 + 1
            goto L_0x0151
        L_0x0177:
            r14.q = r2
        L_0x0179:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.powercenter.mainui.MainBatteryView.a():void");
    }

    public void a(a aVar) {
        ValueAnimator duration = ValueAnimator.ofFloat(new float[]{-aVar.c(), ((((float) this.f7113a) * this.f7115c) / 100.0f) - aVar.c()}).setDuration((long) (this.w.nextInt(5000) + 1000));
        duration.setRepeatCount(-1);
        duration.setInterpolator(new LinearInterpolator());
        duration.setStartDelay((long) (this.w.nextInt(1500) + 1000));
        duration.addUpdateListener(new e(this, aVar));
        duration.start();
        aVar.a(duration);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        for (int i2 = 0; i2 < this.v.size(); i2++) {
            this.o = this.v.get(i2);
            ValueAnimator a2 = this.o.a();
            if (a2 != null) {
                a2.cancel();
            }
        }
        ValueAnimator valueAnimator = this.p;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.f7113a >= 0) {
            Canvas canvas2 = canvas;
            int saveLayer = canvas2.saveLayer(0.0f, 0.0f, (float) getWidth(), (float) getHeight(), (Paint) null, 31);
            RectF rectF = new RectF(0.0f, 0.0f, this.f7115c, this.f7114b);
            float f2 = this.e;
            canvas.drawRoundRect(rectF, f2, f2, this.f);
            canvas.drawRect(0.0f, 0.0f, this.f7115c - this.u, this.f7114b, this.g);
            int i2 = this.f7113a;
            float f3 = this.f7115c;
            float f4 = this.f7116d;
            if (((((float) i2) * f3) / 100.0f) - f4 >= 0.0f && i2 >= 20 && this.j && !this.i && this.s) {
                canvas.drawRect(((((float) i2) * f3) / 100.0f) - f4, 0.0f, (((float) i2) * f3) / 100.0f, this.f7114b, this.h);
            }
            List<a> list = this.v;
            if (list != null && !list.isEmpty() && !this.q) {
                for (int i3 = 0; i3 < this.v.size(); i3++) {
                    if (!(this.v.get(i3).f7119c == 0.0f || this.v.get(i3).b() == null || this.v.get(i3).f7117a == 0.0f)) {
                        canvas.drawRoundRect(new RectF(this.v.get(i3).f7117a, this.v.get(i3).f7118b, this.v.get(i3).f7117a + this.v.get(i3).f7119c, this.v.get(i3).f7118b + this.v.get(i3).f7119c), this.v.get(i3).f7119c / 2.0f, this.v.get(i3).f7119c / 2.0f, this.v.get(i3).b());
                    }
                }
            }
            canvas.restoreToCount(saveLayer);
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i2, int i3) {
        super.onMeasure(i2, i3);
        this.f7114b = (float) getMeasuredHeight();
        this.f7115c = (float) getMeasuredWidth();
    }

    public void setButtonStatus(Button button) {
        this.l = button;
    }

    public void setChargingStatus(boolean z) {
        this.j = z;
        setCurrentValue(this.f7113a);
    }

    /* JADX WARNING: Removed duplicated region for block: B:30:0x01fb  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x023a  */
    /* JADX WARNING: Removed duplicated region for block: B:38:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setCurrentValue(int r9) {
        /*
            r8 = this;
            if (r9 >= 0) goto L_0x0003
            return
        L_0x0003:
            r8.f7113a = r9
            android.widget.Button r0 = r8.l
            if (r0 == 0) goto L_0x000f
            r1 = 2131757288(0x7f1008e8, float:1.9145508E38)
            r0.setText(r1)
        L_0x000f:
            android.graphics.Paint r0 = r8.f
            r1 = 1
            if (r0 != 0) goto L_0x002e
            android.graphics.Paint r0 = new android.graphics.Paint
            r0.<init>()
            r8.f = r0
            android.graphics.Paint r0 = r8.f
            android.graphics.Paint$Style r2 = android.graphics.Paint.Style.FILL
            r0.setStyle(r2)
            android.graphics.Paint r0 = r8.f
            r0.setAntiAlias(r1)
            android.graphics.Paint r0 = r8.f
            int r2 = r8.m
            r0.setColor(r2)
        L_0x002e:
            android.graphics.Paint r0 = r8.g
            if (r0 != 0) goto L_0x0058
            android.graphics.Paint r0 = new android.graphics.Paint
            r0.<init>()
            r8.g = r0
            android.graphics.Paint r0 = r8.g
            android.graphics.Paint$Style r2 = android.graphics.Paint.Style.FILL
            r0.setStyle(r2)
            android.graphics.Paint r0 = r8.g
            r0.setAntiAlias(r1)
            android.graphics.Paint r0 = r8.g
            int r2 = r8.n
            r0.setColor(r2)
            android.graphics.Paint r0 = r8.g
            android.graphics.PorterDuffXfermode r2 = new android.graphics.PorterDuffXfermode
            android.graphics.PorterDuff$Mode r3 = android.graphics.PorterDuff.Mode.SRC_IN
            r2.<init>(r3)
            r0.setXfermode(r2)
        L_0x0058:
            android.graphics.Paint r0 = r8.h
            if (r0 != 0) goto L_0x0082
            android.graphics.Paint r0 = new android.graphics.Paint
            r0.<init>()
            r8.h = r0
            android.graphics.Paint r0 = r8.h
            android.graphics.Paint$Style r2 = android.graphics.Paint.Style.FILL
            r0.setStyle(r2)
            android.graphics.Paint r0 = r8.h
            r0.setAntiAlias(r1)
            android.graphics.Paint r0 = r8.h
            android.graphics.PorterDuffXfermode r2 = new android.graphics.PorterDuffXfermode
            android.graphics.PorterDuff$Mode r3 = android.graphics.PorterDuff.Mode.SRC_IN
            r2.<init>(r3)
            r0.setXfermode(r2)
            android.graphics.Paint r0 = r8.h
            int r2 = r8.n
            r0.setColor(r2)
        L_0x0082:
            boolean r0 = r8.i
            r2 = 300(0x12c, double:1.48E-321)
            r4 = 2
            r5 = 0
            if (r0 == 0) goto L_0x0105
            int r0 = r8.n
            java.lang.String r6 = "#ffaf14"
            int r7 = android.graphics.Color.parseColor(r6)
            if (r0 == r7) goto L_0x01f2
            int[] r0 = new int[r4]
            int r7 = r8.n
            r0[r5] = r7
            int r6 = android.graphics.Color.parseColor(r6)
            r0[r1] = r6
            android.animation.ValueAnimator r0 = android.animation.ValueAnimator.ofInt(r0)
            android.animation.ValueAnimator r0 = r0.setDuration(r2)
            r0.setRepeatCount(r5)
            android.animation.ArgbEvaluator r6 = new android.animation.ArgbEvaluator
            r6.<init>()
            r0.setEvaluator(r6)
            miui.view.animation.CubicEaseOutInterpolator r6 = new miui.view.animation.CubicEaseOutInterpolator
            r6.<init>()
            r0.setInterpolator(r6)
            com.miui.powercenter.mainui.g r6 = new com.miui.powercenter.mainui.g
            r6.<init>(r8)
            r0.addUpdateListener(r6)
            r0.start()
            int[] r0 = new int[r4]
            int r6 = r8.m
            r0[r5] = r6
            android.content.Context r6 = r8.getContext()
            android.content.res.Resources r6 = r6.getResources()
            r7 = 2131100408(0x7f0602f8, float:1.7813197E38)
            int r6 = r6.getColor(r7)
            r0[r1] = r6
            android.animation.ValueAnimator r0 = android.animation.ValueAnimator.ofInt(r0)
            android.animation.ValueAnimator r0 = r0.setDuration(r2)
            r0.setRepeatCount(r5)
            android.animation.ArgbEvaluator r2 = new android.animation.ArgbEvaluator
            r2.<init>()
            r0.setEvaluator(r2)
            miui.view.animation.CubicEaseOutInterpolator r2 = new miui.view.animation.CubicEaseOutInterpolator
            r2.<init>()
            r0.setInterpolator(r2)
            com.miui.powercenter.mainui.h r2 = new com.miui.powercenter.mainui.h
            r2.<init>(r8)
        L_0x00fd:
            r0.addUpdateListener(r2)
            r0.start()
            goto L_0x01f2
        L_0x0105:
            r0 = 20
            if (r9 >= r0) goto L_0x017d
            int r0 = r8.n
            java.lang.String r6 = "#FF5E56"
            int r7 = android.graphics.Color.parseColor(r6)
            if (r0 == r7) goto L_0x01f2
            int[] r0 = new int[r4]
            int r7 = r8.n
            r0[r5] = r7
            int r6 = android.graphics.Color.parseColor(r6)
            r0[r1] = r6
            android.animation.ValueAnimator r0 = android.animation.ValueAnimator.ofInt(r0)
            android.animation.ValueAnimator r0 = r0.setDuration(r2)
            r0.setRepeatCount(r5)
            miui.view.animation.CubicEaseOutInterpolator r6 = new miui.view.animation.CubicEaseOutInterpolator
            r6.<init>()
            r0.setInterpolator(r6)
            android.animation.ArgbEvaluator r6 = new android.animation.ArgbEvaluator
            r6.<init>()
            r0.setEvaluator(r6)
            com.miui.powercenter.mainui.i r6 = new com.miui.powercenter.mainui.i
            r6.<init>(r8)
            r0.addUpdateListener(r6)
            r0.start()
            int[] r0 = new int[r4]
            int r6 = r8.m
            r0[r5] = r6
            android.content.Context r6 = r8.getContext()
            android.content.res.Resources r6 = r6.getResources()
            r7 = 2131100407(0x7f0602f7, float:1.7813195E38)
            int r6 = r6.getColor(r7)
            r0[r1] = r6
            android.animation.ValueAnimator r0 = android.animation.ValueAnimator.ofInt(r0)
            android.animation.ValueAnimator r0 = r0.setDuration(r2)
            r0.setRepeatCount(r5)
            miui.view.animation.CubicEaseOutInterpolator r2 = new miui.view.animation.CubicEaseOutInterpolator
            r2.<init>()
            r0.setInterpolator(r2)
            android.animation.ArgbEvaluator r2 = new android.animation.ArgbEvaluator
            r2.<init>()
            r0.setEvaluator(r2)
            com.miui.powercenter.mainui.j r2 = new com.miui.powercenter.mainui.j
            r2.<init>(r8)
            goto L_0x00fd
        L_0x017d:
            int r0 = r8.n
            java.lang.String r6 = "#3FD268"
            int r7 = android.graphics.Color.parseColor(r6)
            if (r0 == r7) goto L_0x01f2
            int[] r0 = new int[r4]
            int r7 = r8.n
            r0[r5] = r7
            int r6 = android.graphics.Color.parseColor(r6)
            r0[r1] = r6
            android.animation.ValueAnimator r0 = android.animation.ValueAnimator.ofInt(r0)
            android.animation.ValueAnimator r0 = r0.setDuration(r2)
            r0.setRepeatCount(r5)
            miui.view.animation.CubicEaseOutInterpolator r6 = new miui.view.animation.CubicEaseOutInterpolator
            r6.<init>()
            r0.setInterpolator(r6)
            android.animation.ArgbEvaluator r6 = new android.animation.ArgbEvaluator
            r6.<init>()
            r0.setEvaluator(r6)
            com.miui.powercenter.mainui.k r6 = new com.miui.powercenter.mainui.k
            r6.<init>(r8)
            r0.addUpdateListener(r6)
            r0.start()
            int[] r0 = new int[r4]
            int r6 = r8.m
            r0[r5] = r6
            android.content.Context r6 = r8.getContext()
            android.content.res.Resources r6 = r6.getResources()
            r7 = 2131100406(0x7f0602f6, float:1.7813193E38)
            int r6 = r6.getColor(r7)
            r0[r1] = r6
            android.animation.ValueAnimator r0 = android.animation.ValueAnimator.ofInt(r0)
            android.animation.ValueAnimator r0 = r0.setDuration(r2)
            r0.setRepeatCount(r5)
            android.animation.ArgbEvaluator r2 = new android.animation.ArgbEvaluator
            r2.<init>()
            r0.setEvaluator(r2)
            miui.view.animation.CubicEaseOutInterpolator r2 = new miui.view.animation.CubicEaseOutInterpolator
            r2.<init>()
            r0.setInterpolator(r2)
            com.miui.powercenter.mainui.l r2 = new com.miui.powercenter.mainui.l
            r2.<init>(r8)
            goto L_0x00fd
        L_0x01f2:
            r8.a()
            boolean r0 = r8.r
            r2 = 1120403456(0x42c80000, float:100.0)
            if (r0 != 0) goto L_0x0232
            float[] r0 = new float[r4]
            float r3 = r8.f7115c
            r0[r5] = r3
            int r6 = 100 - r9
            float r6 = (float) r6
            float r6 = r6 * r3
            float r6 = r6 / r2
            r0[r1] = r6
            android.animation.ValueAnimator r0 = android.animation.ValueAnimator.ofFloat(r0)
            r6 = 1000(0x3e8, double:4.94E-321)
            android.animation.ValueAnimator r0 = r0.setDuration(r6)
            android.view.animation.DecelerateInterpolator r3 = new android.view.animation.DecelerateInterpolator
            r3.<init>()
            r0.setInterpolator(r3)
            r0.setRepeatCount(r5)
            com.miui.powercenter.mainui.m r3 = new com.miui.powercenter.mainui.m
            r3.<init>(r8)
            r0.addUpdateListener(r3)
            com.miui.powercenter.mainui.n r3 = new com.miui.powercenter.mainui.n
            r3.<init>(r8, r9)
            r0.addListener(r3)
            r0.start()
            r8.r = r1
        L_0x0232:
            int r0 = r8.t
            if (r0 == r9) goto L_0x0277
            boolean r3 = r8.s
            if (r3 == 0) goto L_0x0277
            float[] r3 = new float[r4]
            int r0 = 100 - r0
            float r0 = (float) r0
            float r4 = r8.f7115c
            float r0 = r0 * r4
            float r0 = r0 / r2
            r3[r5] = r0
            int r0 = 100 - r9
            float r0 = (float) r0
            float r0 = r0 * r4
            float r0 = r0 / r2
            r2 = 1065353216(0x3f800000, float:1.0)
            float r0 = r0 + r2
            r3[r1] = r0
            android.animation.ValueAnimator r0 = android.animation.ValueAnimator.ofFloat(r3)
            r1 = 200(0xc8, double:9.9E-322)
            android.animation.ValueAnimator r0 = r0.setDuration(r1)
            android.view.animation.DecelerateInterpolator r1 = new android.view.animation.DecelerateInterpolator
            r1.<init>()
            r0.setInterpolator(r1)
            r0.setRepeatCount(r5)
            com.miui.powercenter.mainui.o r1 = new com.miui.powercenter.mainui.o
            r1.<init>(r8)
            r0.addUpdateListener(r1)
            com.miui.powercenter.mainui.d r1 = new com.miui.powercenter.mainui.d
            r1.<init>(r8, r9)
            r0.addListener(r1)
            r0.start()
        L_0x0277:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.powercenter.mainui.MainBatteryView.setCurrentValue(int):void");
    }

    public void setSaveModeStatus(boolean z) {
        if (z != this.i) {
            this.i = z;
            setCurrentValue(this.f7113a);
        }
    }
}
