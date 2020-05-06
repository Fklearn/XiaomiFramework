package com.miui.applicationlock.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import b.b.o.g.c;
import com.miui.gamebooster.m.na;
import com.miui.securitycenter.R;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class LockPatternView extends View {
    private int A;
    private final Matrix B;
    private final Matrix C;
    private boolean D;
    private boolean E;
    private int F;
    private int G;
    private Bitmap[] H;
    private Bitmap[] I;

    /* renamed from: a  reason: collision with root package name */
    private boolean f3398a;

    /* renamed from: b  reason: collision with root package name */
    private Paint f3399b;

    /* renamed from: c  reason: collision with root package name */
    private Paint f3400c;

    /* renamed from: d  reason: collision with root package name */
    private Paint f3401d;
    private c e;
    private ArrayList<a> f;
    private boolean[][] g;
    private float h;
    private float i;
    private long j;
    private b k;
    private boolean l;
    private boolean m;
    private boolean n;
    private boolean o;
    private float p;
    private int q;
    private float r;
    private float s;
    private float t;
    private Bitmap u;
    private Bitmap v;
    private final Path w;
    private final Rect x;
    private int y;
    private int z;

    private static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new d();
        private final int mDisplayMode;
        private final boolean mInStealthMode;
        private final boolean mInputEnabled;
        private final String mSerializedPattern;
        private final boolean mTactileFeedbackEnabled;

        private SavedState(Parcel parcel) {
            super(parcel);
            this.mSerializedPattern = parcel.readString();
            this.mDisplayMode = parcel.readInt();
            this.mInputEnabled = ((Boolean) parcel.readValue((ClassLoader) null)).booleanValue();
            this.mInStealthMode = ((Boolean) parcel.readValue((ClassLoader) null)).booleanValue();
            this.mTactileFeedbackEnabled = ((Boolean) parcel.readValue((ClassLoader) null)).booleanValue();
        }

        private SavedState(Parcelable parcelable, String str, int i, boolean z, boolean z2, boolean z3) {
            super(parcelable);
            this.mSerializedPattern = str;
            this.mDisplayMode = i;
            this.mInputEnabled = z;
            this.mInStealthMode = z2;
            this.mTactileFeedbackEnabled = z3;
        }

        public int getDisplayMode() {
            return this.mDisplayMode;
        }

        public String getSerializedPattern() {
            return this.mSerializedPattern;
        }

        public boolean isInStealthMode() {
            return this.mInStealthMode;
        }

        public boolean isInputEnabled() {
            return this.mInputEnabled;
        }

        public boolean isTactileFeedbackEnabled() {
            return this.mTactileFeedbackEnabled;
        }

        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeString(this.mSerializedPattern);
            parcel.writeInt(this.mDisplayMode);
            parcel.writeValue(Boolean.valueOf(this.mInputEnabled));
            parcel.writeValue(Boolean.valueOf(this.mInStealthMode));
            parcel.writeValue(Boolean.valueOf(this.mTactileFeedbackEnabled));
        }
    }

    public static final class a {

        /* renamed from: a  reason: collision with root package name */
        private static final a[][] f3402a = c();

        /* renamed from: b  reason: collision with root package name */
        final int f3403b;

        /* renamed from: c  reason: collision with root package name */
        final int f3404c;

        private a(int i, int i2) {
            b(i, i2);
            this.f3403b = i;
            this.f3404c = i2;
        }

        public static a a(int i, int i2) {
            b(i, i2);
            return f3402a[i][i2];
        }

        private static void b(int i, int i2) {
            if (i < 0 || i > 2) {
                throw new IllegalArgumentException("row must be in range 0-2");
            } else if (i2 < 0 || i2 > 2) {
                throw new IllegalArgumentException("column must be in range 0-2");
            }
        }

        private static a[][] c() {
            a[][] aVarArr = (a[][]) Array.newInstance(a.class, new int[]{3, 3});
            for (int i = 0; i < 3; i++) {
                for (int i2 = 0; i2 < 3; i2++) {
                    aVarArr[i][i2] = new a(i, i2);
                }
            }
            return aVarArr;
        }

        public int a() {
            return this.f3404c;
        }

        public int b() {
            return this.f3403b;
        }

        public String toString() {
            return "(row=" + this.f3403b + ",clmn=" + this.f3404c + ")";
        }
    }

    public enum b {
        Correct,
        Animate,
        Wrong
    }

    public interface c {
        void a();

        void a(List<a> list);

        void b();

        void b(List<a> list);
    }

    public LockPatternView(Context context) {
        this(context, (AttributeSet) null);
    }

    public LockPatternView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.f3398a = false;
        this.f3399b = new Paint();
        this.f3400c = new Paint();
        this.f3401d = new Paint();
        this.f = new ArrayList<>(9);
        this.g = (boolean[][]) Array.newInstance(boolean.class, new int[]{3, 3});
        this.h = -1.0f;
        this.i = -1.0f;
        this.k = b.Correct;
        this.l = true;
        this.m = false;
        this.n = true;
        this.o = false;
        this.p = 0.05f;
        this.q = 64;
        this.r = 0.6f;
        this.w = new Path();
        this.x = new Rect();
        this.B = new Matrix();
        this.C = new Matrix();
        a(context, attributeSet);
        setClickable(true);
        this.f3400c.setAntiAlias(true);
        this.f3400c.setDither(true);
        this.f3400c.setAlpha(this.q);
        this.f3400c.setStyle(Paint.Style.STROKE);
        this.f3400c.setStrokeJoin(Paint.Join.ROUND);
        this.f3400c.setStrokeCap(Paint.Cap.ROUND);
        this.f3401d.setAntiAlias(true);
        this.f3401d.setDither(true);
        this.f3401d.setAlpha(this.q);
        this.f3401d.setStyle(Paint.Style.STROKE);
        this.f3401d.setStrokeJoin(Paint.Join.ROUND);
        this.f3401d.setStrokeCap(Paint.Cap.ROUND);
    }

    private int a(float f2) {
        float f3 = this.s;
        float f4 = this.r * f3;
        float paddingLeft = ((float) getPaddingLeft()) + ((f3 - f4) / 2.0f);
        for (int i2 = 0; i2 < 3; i2++) {
            float f5 = (((float) i2) * f3) + paddingLeft;
            if (f2 >= f5 && f2 <= f5 + f4) {
                return i2;
            }
        }
        return -1;
    }

    private int a(int i2, int i3) {
        int size = View.MeasureSpec.getSize(i2);
        int mode = View.MeasureSpec.getMode(i2);
        return mode != Integer.MIN_VALUE ? mode != 0 ? size : i3 : Math.max(size, i3);
    }

    private Bitmap a(int i2) {
        if (-1 == i2) {
            return null;
        }
        return BitmapFactory.decodeResource(getContext().getResources(), i2);
    }

    private a a(float f2, float f3) {
        int a2;
        int b2 = b(f3);
        if (b2 >= 0 && (a2 = a(f2)) >= 0 && !this.g[b2][a2]) {
            return a.a(b2, a2);
        }
        return null;
    }

    /* JADX WARNING: Removed duplicated region for block: B:14:0x0077  */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x007a  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x008d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void a(android.content.Context r6, android.util.AttributeSet r7) {
        /*
            r5 = this;
            int[] r0 = com.miui.securitycenter.i.LockPatternView
            android.content.res.TypedArray r6 = r6.obtainStyledAttributes(r7, r0)
            r7 = 2
            java.lang.String r0 = r6.getString(r7)
            java.lang.String r1 = "square"
            boolean r1 = r1.equals(r0)
            r2 = 1
            r3 = 0
            if (r1 == 0) goto L_0x0018
        L_0x0015:
            r5.A = r3
            goto L_0x0039
        L_0x0018:
            java.lang.String r1 = "lock_width"
            boolean r1 = r1.equals(r0)
            if (r1 == 0) goto L_0x0023
            r5.A = r2
            goto L_0x0039
        L_0x0023:
            java.lang.String r1 = "lock_height"
            boolean r1 = r1.equals(r0)
            if (r1 == 0) goto L_0x002e
            r5.A = r7
            goto L_0x0039
        L_0x002e:
            java.lang.String r1 = "fixed"
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x0015
            r0 = 3
            r5.A = r0
        L_0x0039:
            android.graphics.Paint r0 = r5.f3400c
            r1 = 11
            r4 = -1
            int r1 = r6.getColor(r1, r4)
            r0.setColor(r1)
            android.graphics.Paint r0 = r5.f3401d
            r1 = 13
            int r1 = r6.getColor(r1, r4)
            r0.setColor(r1)
            r0 = 10
            r1 = 1036831949(0x3dcccccd, float:0.1)
            float r0 = r6.getFloat(r0, r1)
            r5.p = r0
            r0 = 12
            r1 = 128(0x80, float:1.794E-43)
            int r0 = r6.getInteger(r0, r1)
            r5.q = r0
            r0 = 6
            int r0 = r6.getResourceId(r0, r4)
            android.graphics.Bitmap r0 = r5.a((int) r0)
            r5.u = r0
            r0 = 5
            int r0 = r6.getResourceId(r0, r4)
            if (r4 != r0) goto L_0x007a
            android.graphics.Bitmap r0 = r5.u
            goto L_0x007e
        L_0x007a:
            android.graphics.Bitmap r0 = r5.a((int) r0)
        L_0x007e:
            r5.v = r0
            android.graphics.Bitmap[] r7 = new android.graphics.Bitmap[r7]
            android.graphics.Bitmap r0 = r5.v
            r7[r3] = r0
            android.graphics.Bitmap r0 = r5.u
            r7[r2] = r0
            int r0 = r7.length
        L_0x008b:
            if (r3 >= r0) goto L_0x00ac
            r1 = r7[r3]
            if (r1 == 0) goto L_0x00a9
            int r2 = r5.y
            int r4 = r1.getWidth()
            int r2 = java.lang.Math.max(r2, r4)
            r5.y = r2
            int r2 = r5.z
            int r1 = r1.getHeight()
            int r1 = java.lang.Math.max(r2, r1)
            r5.z = r1
        L_0x00a9:
            int r3 = r3 + 1
            goto L_0x008b
        L_0x00ac:
            r6.recycle()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.applicationlock.widget.LockPatternView.a(android.content.Context, android.util.AttributeSet):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:36:0x0074  */
    /* JADX WARNING: Removed duplicated region for block: B:38:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void a(android.graphics.Canvas r6, int r7, int r8, boolean r9, int r10, int r11) {
        /*
            r5 = this;
            r0 = 1
            r1 = 0
            if (r9 == 0) goto L_0x004f
            boolean r9 = r5.m
            if (r9 == 0) goto L_0x000f
            com.miui.applicationlock.widget.LockPatternView$b r9 = r5.k
            com.miui.applicationlock.widget.LockPatternView$b r2 = com.miui.applicationlock.widget.LockPatternView.b.Wrong
            if (r9 == r2) goto L_0x000f
            goto L_0x004f
        L_0x000f:
            boolean r9 = r5.o
            if (r9 == 0) goto L_0x0024
            android.graphics.Bitmap[] r9 = r5.H
            if (r9 == 0) goto L_0x005d
            boolean r2 = r5.E
            if (r2 == 0) goto L_0x005d
            int r10 = r10 * 3
            int r10 = r10 + r11
            r9 = r9[r10]
            r4 = r1
            r1 = r0
            r0 = r4
            goto L_0x0060
        L_0x0024:
            com.miui.applicationlock.widget.LockPatternView$b r9 = r5.k
            com.miui.applicationlock.widget.LockPatternView$b r10 = com.miui.applicationlock.widget.LockPatternView.b.Wrong
            if (r9 != r10) goto L_0x002d
            android.graphics.Bitmap r9 = r5.v
            goto L_0x005f
        L_0x002d:
            com.miui.applicationlock.widget.LockPatternView$b r10 = com.miui.applicationlock.widget.LockPatternView.b.Correct
            if (r9 == r10) goto L_0x005d
            com.miui.applicationlock.widget.LockPatternView$b r10 = com.miui.applicationlock.widget.LockPatternView.b.Animate
            if (r9 != r10) goto L_0x0036
            goto L_0x005d
        L_0x0036:
            java.lang.IllegalStateException r6 = new java.lang.IllegalStateException
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "unknown display mode "
            r7.append(r8)
            com.miui.applicationlock.widget.LockPatternView$b r8 = r5.k
            r7.append(r8)
            java.lang.String r7 = r7.toString()
            r6.<init>(r7)
            throw r6
        L_0x004f:
            android.graphics.Bitmap[] r9 = r5.I
            if (r9 == 0) goto L_0x005d
            boolean r2 = r5.E
            if (r2 == 0) goto L_0x005d
            int r10 = r10 * 3
            int r10 = r10 + r11
            r9 = r9[r10]
            goto L_0x0060
        L_0x005d:
            android.graphics.Bitmap r9 = r5.u
        L_0x005f:
            r0 = r1
        L_0x0060:
            boolean r10 = r5.D
            if (r10 == 0) goto L_0x006a
            if (r1 != 0) goto L_0x006a
            if (r0 != 0) goto L_0x006a
            android.graphics.Bitmap r9 = r5.v
        L_0x006a:
            int r10 = r5.y
            int r10 = r5.z
            float r10 = r5.s
            float r11 = r5.t
            if (r9 == 0) goto L_0x00d1
            int r0 = r9.getWidth()
            float r0 = (float) r0
            float r10 = r10 - r0
            r0 = 1073741824(0x40000000, float:2.0)
            float r10 = r10 / r0
            int r10 = (int) r10
            int r1 = r9.getHeight()
            float r1 = (float) r1
            float r11 = r11 - r1
            float r11 = r11 / r0
            int r11 = (int) r11
            float r0 = r5.s
            int r1 = r5.y
            float r1 = (float) r1
            float r0 = r0 / r1
            r1 = 1065353216(0x3f800000, float:1.0)
            float r0 = java.lang.Math.min(r0, r1)
            float r2 = r5.t
            int r3 = r5.z
            float r3 = (float) r3
            float r2 = r2 / r3
            float r1 = java.lang.Math.min(r2, r1)
            android.graphics.Matrix r2 = r5.C
            int r7 = r7 + r10
            float r7 = (float) r7
            int r8 = r8 + r11
            float r8 = (float) r8
            r2.setTranslate(r7, r8)
            android.graphics.Matrix r7 = r5.C
            int r8 = r5.y
            int r8 = r8 / 2
            float r8 = (float) r8
            int r10 = r5.z
            int r10 = r10 / 2
            float r10 = (float) r10
            r7.preTranslate(r8, r10)
            android.graphics.Matrix r7 = r5.C
            r7.preScale(r0, r1)
            android.graphics.Matrix r7 = r5.C
            int r8 = r5.y
            int r8 = -r8
            int r8 = r8 / 2
            float r8 = (float) r8
            int r10 = r5.z
            int r10 = -r10
            int r10 = r10 / 2
            float r10 = (float) r10
            r7.preTranslate(r8, r10)
            android.graphics.Matrix r7 = r5.C
            android.graphics.Paint r8 = r5.f3399b
            r6.drawBitmap(r9, r7, r8)
        L_0x00d1:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.applicationlock.widget.LockPatternView.a(android.graphics.Canvas, int, int, boolean, int, int):void");
    }

    private void a(MotionEvent motionEvent) {
        i();
        float x2 = motionEvent.getX();
        float y2 = motionEvent.getY();
        if (b(x2, y2) != null) {
            this.o = true;
            this.k = b.Correct;
            h();
            invalidate();
        } else {
            this.o = false;
            f();
        }
        this.h = x2;
        this.i = y2;
    }

    private void a(a aVar) {
        this.g[aVar.b()][aVar.a()] = true;
        this.f.add(aVar);
        d((aVar.b() * 3) + aVar.a() + 1);
    }

    private boolean a(Context context) {
        try {
            c.a a2 = c.a.a("android.view.accessibility.AccessibilityManager");
            a2.b("getInstance", new Class[]{Context.class}, context);
            a2.e();
            a2.a("isTouchExplorationEnabled", (Class<?>[]) null, new Object[0]);
            return a2.a();
        } catch (Exception e2) {
            e2.printStackTrace();
            return false;
        }
    }

    private float b(int i2) {
        float f2 = this.s;
        return ((float) getPaddingLeft()) + (((float) i2) * f2) + (f2 / 2.0f);
    }

    private int b(float f2) {
        float f3 = this.t;
        float f4 = this.r * f3;
        float paddingTop = ((float) getPaddingTop()) + ((f3 - f4) / 2.0f);
        for (int i2 = 0; i2 < 3; i2++) {
            float f5 = (((float) i2) * f3) + paddingTop;
            if (f2 >= f5 && f2 <= f5 + f4) {
                return i2;
            }
        }
        return -1;
    }

    private a b(float f2, float f3) {
        a a2 = a(f2, f3);
        a aVar = null;
        if (a2 == null) {
            return null;
        }
        ArrayList<a> arrayList = this.f;
        if (!arrayList.isEmpty()) {
            a aVar2 = arrayList.get(arrayList.size() - 1);
            int b2 = a2.b() - aVar2.b();
            int a3 = a2.a() - aVar2.a();
            int b3 = aVar2.b();
            int a4 = aVar2.a();
            int i2 = -1;
            if (Math.abs(b2) == 2 && Math.abs(a3) != 1) {
                b3 = aVar2.b() + (b2 > 0 ? 1 : -1);
            }
            if (Math.abs(a3) == 2 && Math.abs(b2) != 1) {
                int a5 = aVar2.a();
                if (a3 > 0) {
                    i2 = 1;
                }
                a4 = a5 + i2;
            }
            aVar = a.a(b3, a4);
        }
        if (aVar != null && !this.g[aVar.b()][aVar.a()]) {
            a(aVar);
        }
        a(a2);
        if (this.n) {
            performHapticFeedback(1, 3);
        }
        return a2;
    }

    private void b(int i2, int i3) {
        setContentDescription(getContext().getString(i2) + String.valueOf(i3));
        sendAccessibilityEvent(4);
        setContentDescription((CharSequence) null);
    }

    private void b(MotionEvent motionEvent) {
        int historySize = motionEvent.getHistorySize();
        int i2 = 0;
        while (i2 < historySize + 1) {
            float historicalX = i2 < historySize ? motionEvent.getHistoricalX(i2) : motionEvent.getX();
            float historicalY = i2 < historySize ? motionEvent.getHistoricalY(i2) : motionEvent.getY();
            a b2 = b(historicalX, historicalY);
            int size = this.f.size();
            if (b2 != null && size == 1) {
                this.o = true;
                h();
            }
            if (Math.abs(historicalX - this.h) + Math.abs(historicalY - this.i) > this.s * 0.01f) {
                this.h = historicalX;
                this.i = historicalY;
                invalidate();
            }
            i2++;
        }
    }

    private float c(int i2) {
        float f2 = this.t;
        return ((float) getPaddingTop()) + (((float) i2) * f2) + (f2 / 2.0f);
    }

    private void c(MotionEvent motionEvent) {
        if (!this.f.isEmpty()) {
            this.o = false;
            g();
            invalidate();
        }
    }

    private void d(int i2) {
        c cVar = this.e;
        if (cVar != null) {
            cVar.b(this.f);
        }
        b((int) R.string.lockscreen_access_pattern_cell_added, i2);
    }

    private void e() {
        for (int i2 = 0; i2 < 3; i2++) {
            for (int i3 = 0; i3 < 3; i3++) {
                this.g[i2][i3] = false;
            }
        }
    }

    private void e(int i2) {
        setContentDescription(getContext().getString(i2));
        sendAccessibilityEvent(4);
        setContentDescription((CharSequence) null);
    }

    private void f() {
        c cVar = this.e;
        if (cVar != null) {
            cVar.a();
        }
        e(R.string.lockscreen_access_pattern_cleared);
    }

    private void g() {
        c cVar = this.e;
        if (cVar != null) {
            cVar.a(this.f);
        }
    }

    private void h() {
        c cVar = this.e;
        if (cVar != null) {
            cVar.b();
        }
        e(R.string.lockscreen_access_pattern_start);
    }

    private void i() {
        this.f.clear();
        e();
        this.k = b.Correct;
        invalidate();
    }

    public void a() {
        i();
    }

    public void a(Context context, com.miui.applicationlock.b.b bVar) {
        this.u = BitmapFactory.decodeResource(getResources(), R.drawable.applock_ad_dot);
        this.f3400c.setColor(context.getResources().getColor(R.color.lock_pattern_ad_dot_display));
        this.H = com.miui.applicationlock.b.a.a(context, bVar, 0);
        this.I = com.miui.applicationlock.b.a.a(context, bVar, 20);
    }

    public void b() {
        this.l = false;
    }

    public void c() {
        this.l = true;
    }

    public boolean d() {
        return this.l;
    }

    /* access modifiers changed from: protected */
    public int getSuggestedMinimumHeight() {
        return this.y * 3;
    }

    /* access modifiers changed from: protected */
    public int getSuggestedMinimumWidth() {
        return this.y * 3;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Path path;
        Canvas canvas2 = canvas;
        ArrayList<a> arrayList = this.f;
        int size = arrayList.size();
        boolean[][] zArr = this.g;
        if (this.k == b.Animate) {
            int elapsedRealtime = ((int) (SystemClock.elapsedRealtime() - this.j)) % ((size + 1) * 700);
            int i2 = elapsedRealtime / 700;
            e();
            for (int i3 = 0; i3 < i2; i3++) {
                a aVar = arrayList.get(i3);
                zArr[aVar.b()][aVar.a()] = true;
            }
            if (i2 > 0 && i2 < size) {
                float f2 = ((float) (elapsedRealtime % 700)) / 700.0f;
                a aVar2 = arrayList.get(i2 - 1);
                float b2 = b(aVar2.a());
                float c2 = c(aVar2.b());
                a aVar3 = arrayList.get(i2);
                this.h = b2 + ((b(aVar3.a()) - b2) * f2);
                this.i = c2 + (f2 * (c(aVar3.b()) - c2));
            }
            invalidate();
        }
        float f3 = this.s;
        float f4 = this.t;
        float f5 = this.p * f3;
        this.f3400c.setStrokeWidth(f5);
        this.f3401d.setStrokeWidth(f5);
        Path path2 = this.w;
        path2.rewind();
        int paddingTop = getPaddingTop();
        int paddingLeft = getPaddingLeft();
        int i4 = 0;
        while (true) {
            int i5 = 3;
            if (i4 >= 3) {
                break;
            }
            float f6 = (((float) i4) * f4) + ((float) paddingTop);
            int i6 = 0;
            while (i6 < i5) {
                int i7 = i6;
                int i8 = i4;
                a(canvas, (int) (((float) paddingLeft) + (((float) i6) * f3)), (int) f6, zArr[i4][i6], i8, i7);
                i6 = i7 + 1;
                paddingLeft = paddingLeft;
                i5 = 3;
                paddingTop = paddingTop;
                f6 = f6;
                i4 = i8;
                path2 = path2;
            }
            int i9 = paddingLeft;
            int i10 = paddingTop;
            Path path3 = path2;
            i4++;
        }
        Path path4 = path2;
        boolean z2 = !this.m || this.k == b.Wrong;
        boolean z3 = (this.f3399b.getFlags() & 2) != 0;
        this.f3399b.setFilterBitmap(true);
        if (z2) {
            int i11 = 0;
            boolean z4 = false;
            while (i11 < size) {
                a aVar4 = arrayList.get(i11);
                if (!zArr[aVar4.b()][aVar4.a()]) {
                    break;
                }
                float b3 = b(aVar4.a());
                float c3 = c(aVar4.b());
                if (i11 == 0) {
                    path = path4;
                    path.moveTo(b3, c3);
                } else {
                    path = path4;
                    path.lineTo(b3, c3);
                }
                i11++;
                path4 = path;
                z4 = true;
            }
            Path path5 = path4;
            if ((this.o || this.k == b.Animate) && z4) {
                path5.lineTo(this.h, this.i);
            }
            canvas2.drawPath(path5, this.k != b.Wrong ? this.f3400c : this.f3401d);
        }
        this.f3399b.setFilterBitmap(z3);
    }

    public boolean onHoverEvent(MotionEvent motionEvent) {
        int i2;
        if (a(getContext())) {
            int action = motionEvent.getAction();
            if (action == 7) {
                i2 = 2;
            } else if (action != 9) {
                if (action == 10) {
                    i2 = 1;
                }
                onTouchEvent(motionEvent);
                motionEvent.setAction(action);
            } else {
                i2 = 0;
            }
            motionEvent.setAction(i2);
            onTouchEvent(motionEvent);
            motionEvent.setAction(action);
        }
        return super.onHoverEvent(motionEvent);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i2, int i3) {
        int suggestedMinimumWidth = getSuggestedMinimumWidth();
        int suggestedMinimumHeight = getSuggestedMinimumHeight();
        int a2 = a(i2, suggestedMinimumWidth);
        int a3 = a(i3, suggestedMinimumHeight);
        int i4 = this.A;
        if (i4 != 0) {
            boolean z2 = true;
            if (i4 == 1) {
                a3 = Math.min(a2, a3);
            } else if (i4 == 2) {
                a2 = Math.min(a2, a3);
            } else if (i4 == 3) {
                if (this.G != 0) {
                    z2 = false;
                }
                if (z2) {
                    a2 = (int) (((float) na.b()) * 0.68f);
                    a3 = (int) (((float) na.b()) * 0.68f);
                } else {
                    Resources resources = getResources();
                    int i5 = R.dimen.pattern_settings_lock_pattern_view_size;
                    a2 = resources.getDimensionPixelSize(z2 ? R.dimen.pattern_settings_lock_pattern_view_size : this.G);
                    Resources resources2 = getResources();
                    if (!z2) {
                        i5 = this.F;
                    }
                    a3 = resources2.getDimensionPixelSize(i5);
                }
            }
        } else {
            a2 = Math.min(a2, a3);
            a3 = a2;
        }
        setMeasuredDimension(a2, a3);
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i2, int i3, int i4, int i5) {
        this.s = ((float) ((i2 - getPaddingLeft()) - getPaddingRight())) / 3.0f;
        this.t = ((float) ((i3 - getPaddingTop()) - getPaddingBottom())) / 3.0f;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!this.l || !isEnabled()) {
            return false;
        }
        int action = motionEvent.getAction();
        if (action == 0) {
            a(motionEvent);
            return true;
        } else if (action == 1) {
            c(motionEvent);
            return true;
        } else if (action == 2) {
            b(motionEvent);
            return true;
        } else if (action != 3) {
            return false;
        } else {
            i();
            this.o = false;
            f();
            return true;
        }
    }

    public void setAppPage(boolean z2) {
        this.E = z2;
    }

    public void setDisplayMode(b bVar) {
        this.k = bVar;
        if (bVar == b.Animate) {
            if (this.f.size() != 0) {
                this.j = SystemClock.elapsedRealtime();
                a aVar = this.f.get(0);
                this.h = b(aVar.a());
                this.i = c(aVar.b());
                e();
            } else {
                throw new IllegalStateException("you must have a pattern to animate if you want to set the display mode to animate");
            }
        }
        invalidate();
    }

    public void setInStealthMode(boolean z2) {
        this.m = z2;
    }

    public void setLightMode(boolean z2) {
        if (z2) {
            this.D = z2;
            invalidate();
        }
    }

    public void setOnPatternListener(c cVar) {
        this.e = cVar;
    }

    public void setTactileFeedbackEnabled(boolean z2) {
        this.n = z2;
    }
}
