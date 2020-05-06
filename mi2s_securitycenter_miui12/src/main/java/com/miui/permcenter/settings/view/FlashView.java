package com.miui.permcenter.settings.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import com.miui.securitycenter.R;

public class FlashView extends View {

    /* renamed from: a  reason: collision with root package name */
    private Paint f6578a;

    /* renamed from: b  reason: collision with root package name */
    private Paint f6579b;

    /* renamed from: c  reason: collision with root package name */
    private Bitmap f6580c;

    /* renamed from: d  reason: collision with root package name */
    private Bitmap f6581d;
    private Bitmap e;
    private Canvas f;
    private int g;
    private int h;
    private float i;
    private int[] j;
    private float[] k;
    private int l;
    private ObjectAnimator m;
    private boolean n;
    private int o;

    public FlashView(@NonNull Context context) {
        this(context, (AttributeSet) null);
    }

    public FlashView(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public FlashView(@NonNull Context context, @Nullable AttributeSet attributeSet, @AttrRes int i2) {
        super(context, attributeSet, i2);
        this.j = new int[]{getResources().getColor(R.color.pm_setting_empty_load_start), getResources().getColor(R.color.pm_setting_empty_load_end), getResources().getColor(R.color.pm_setting_empty_load_start)};
        this.k = new float[]{0.45f, 0.6f, 0.75f};
        this.l = TsExtractor.TS_STREAM_TYPE_HDMV_DTS;
        this.n = false;
        f();
    }

    private void a(Canvas canvas, Paint paint) {
        canvas.drawBitmap(this.f6580c, 0.0f, 0.0f, paint);
    }

    private void c() {
        if (this.f6581d == null) {
            int i2 = (int) (((float) this.g) * 2.5f);
            int i3 = this.h;
            float f2 = (float) i2;
            float f3 = (float) i3;
            LinearGradient linearGradient = new LinearGradient(f2 * 0.35f, f3 * 1.0f, f2 * 0.75f, f3 * 0.0f, this.j, this.k, Shader.TileMode.CLAMP);
            this.f6581d = Bitmap.createBitmap(i2, i3, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(this.f6581d);
            Paint paint = new Paint();
            paint.setShader(linearGradient);
            canvas.drawRect(0.0f, 0.0f, f2, f3, paint);
        }
    }

    private void d() {
        if (this.e == null) {
            this.e = Bitmap.createBitmap(this.g, this.h, Bitmap.Config.ARGB_8888);
            this.f = new Canvas(this.e);
        }
    }

    private void e() {
        if (this.f6580c == null) {
            throw new RuntimeException("You need call setImage(int resId) to set a src image!");
        }
    }

    private void f() {
        this.o = 1800;
        this.f6579b = new Paint();
        this.f6579b.setAlpha(this.l);
        PorterDuffXfermode porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        this.f6578a = new Paint();
        this.f6578a.setAntiAlias(true);
        this.f6578a.setDither(true);
        this.f6578a.setFilterBitmap(true);
        this.f6578a.setXfermode(porterDuffXfermode);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        this.g = (int) TypedValue.applyDimension(1, 140.0f, displayMetrics);
        this.h = (int) TypedValue.applyDimension(1, 148.0f, displayMetrics);
    }

    private void g() {
        if (this.m == null) {
            this.m = ObjectAnimator.ofFloat(this, "percent", new float[]{0.0f, 1.5f});
            this.m.setInterpolator(new DecelerateInterpolator());
            this.m.setDuration((long) this.o);
            this.m.setRepeatCount(-1);
        }
        this.m.start();
    }

    private void setPercent(float f2) {
        this.i = ((float) this.g) * (f2 - 1.5f);
        invalidate();
    }

    public void a() {
        if (!this.n) {
            this.n = true;
            g();
        }
    }

    public void b() {
        ObjectAnimator objectAnimator = this.m;
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
        this.n = false;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        e();
        a(canvas, this.f6579b);
        d();
        a(this.f, (Paint) null);
        c();
        this.f.drawBitmap(this.f6581d, this.i, 0.0f, this.f6578a);
        canvas.drawBitmap(this.e, 0.0f, 0.0f, (Paint) null);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i2, int i3) {
        int mode = View.MeasureSpec.getMode(i2);
        int size = View.MeasureSpec.getSize(i2);
        int mode2 = View.MeasureSpec.getMode(i3);
        int size2 = View.MeasureSpec.getSize(i3);
        if (mode == Integer.MIN_VALUE) {
            size = this.f6580c.getWidth();
        }
        if (mode2 == Integer.MIN_VALUE) {
            size2 = this.f6580c.getHeight();
        }
        setMeasuredDimension(size, size2);
    }

    public void setDuration(int i2) {
        this.o = i2;
    }

    public void setImage(int i2) {
        this.f6580c = BitmapFactory.decodeResource(getResources(), i2);
    }

    public void setSrcAlpha(int i2) {
        this.l = i2;
        Paint paint = this.f6579b;
        if (paint != null) {
            paint.setAlpha(i2);
        }
    }
}
