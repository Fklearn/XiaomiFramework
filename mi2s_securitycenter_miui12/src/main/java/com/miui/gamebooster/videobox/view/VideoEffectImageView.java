package com.miui.gamebooster.videobox.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.miui.gamebooster.globalgame.view.RoundedDrawable;
import com.miui.gamebooster.m.na;
import com.miui.securitycenter.R;

public class VideoEffectImageView extends ImageView {

    /* renamed from: a  reason: collision with root package name */
    private static final int[] f5235a = {R.attr.vtbIsLeftImg};

    /* renamed from: b  reason: collision with root package name */
    private Xfermode f5236b;

    /* renamed from: c  reason: collision with root package name */
    private int f5237c;

    /* renamed from: d  reason: collision with root package name */
    private RectF f5238d;
    private Paint e;
    private Path f;
    private Bitmap g;
    private int h;
    private int i;
    private boolean j;

    public VideoEffectImageView(Context context) {
        this(context, (AttributeSet) null);
    }

    public VideoEffectImageView(Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public VideoEffectImageView(Context context, @Nullable AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        this.f5237c = context.getResources().getDimensionPixelSize(R.dimen.vb_advance_settings_img_corner);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, f5235a, i2, 0);
        this.j = obtainStyledAttributes.getBoolean(0, true);
        obtainStyledAttributes.recycle();
        b();
    }

    private void a() {
        if (!na.c() ? !this.j : this.j) {
            this.f5238d.set((float) (-this.f5237c), 0.0f, (float) this.h, (float) this.i);
        } else {
            this.f5238d.set(0.0f, 0.0f, (float) (this.h + this.f5237c), (float) this.i);
        }
    }

    private void a(Canvas canvas) {
        this.e.setStyle(Paint.Style.FILL);
        this.e.setXfermode(this.f5236b);
        if (this.g == null) {
            this.g = Bitmap.createBitmap((int) this.f5238d.width(), (int) this.f5238d.height(), Bitmap.Config.ARGB_8888);
            Canvas canvas2 = new Canvas(this.g);
            Paint paint = new Paint(1);
            paint.setColor(RoundedDrawable.DEFAULT_BORDER_COLOR);
            RectF rectF = this.f5238d;
            int i2 = this.f5237c;
            canvas2.drawRoundRect(rectF, (float) i2, (float) i2, paint);
        }
        canvas.drawBitmap(this.g, 0.0f, 0.0f, this.e);
        this.e.setXfermode((Xfermode) null);
    }

    private void b() {
        this.e = new Paint(1);
        this.e.setStyle(Paint.Style.FILL);
        this.f = new Path();
        this.f5238d = new RectF();
        this.f5236b = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int saveLayer = canvas.saveLayer(this.f5238d, (Paint) null, 31);
        super.onDraw(canvas);
        this.e.reset();
        this.f.reset();
        a(canvas);
        canvas.restoreToCount(saveLayer);
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i2, int i3, int i4, int i5) {
        super.onSizeChanged(i2, i3, i4, i5);
        this.h = i2;
        this.i = i3;
        a();
    }
}
