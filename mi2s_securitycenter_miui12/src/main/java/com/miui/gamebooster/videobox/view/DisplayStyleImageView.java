package com.miui.gamebooster.videobox.view;

import android.content.Context;
import android.content.res.Resources;
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
import com.miui.securitycenter.R;

public class DisplayStyleImageView extends ImageView {

    /* renamed from: a  reason: collision with root package name */
    private Xfermode f5215a;

    /* renamed from: b  reason: collision with root package name */
    private int f5216b;

    /* renamed from: c  reason: collision with root package name */
    private int f5217c;

    /* renamed from: d  reason: collision with root package name */
    private int f5218d;
    private int e;
    private RectF f;
    private RectF g;
    private Paint h;
    private Path i;
    private Bitmap j;
    private int k;
    private int l;
    private boolean m;

    public DisplayStyleImageView(Context context) {
        this(context, (AttributeSet) null);
    }

    public DisplayStyleImageView(Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public DisplayStyleImageView(Context context, @Nullable AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        this.f5217c = -1;
        this.m = false;
        Resources resources = context.getResources();
        this.f5217c = resources.getColor(R.color.color_vb_display_style_img_border);
        this.f5218d = resources.getDimensionPixelSize(R.dimen.vb_display_style_img_border_radius);
        this.f5216b = resources.getDimensionPixelSize(R.dimen.vb_display_style_img_border_w);
        b();
    }

    private void a() {
        this.f.set(0.0f, 0.0f, (float) this.k, (float) this.l);
        int i2 = this.f5216b;
        int i3 = i2 / 3;
        int i4 = i2 / 3;
        this.g.set((float) i3, (float) i4, (float) (this.k - i3), (float) (this.l - i4));
    }

    private void a(Canvas canvas) {
        this.i.reset();
        this.h.setStrokeWidth((float) this.f5216b);
        this.h.setColor(this.f5217c);
        this.h.setStyle(Paint.Style.STROKE);
        Path path = this.i;
        RectF rectF = this.g;
        int i2 = this.e;
        path.addRoundRect(rectF, (float) i2, (float) i2, Path.Direction.CCW);
        canvas.drawPath(this.i, this.h);
    }

    private void b() {
        this.h = new Paint(1);
        this.h.setStyle(Paint.Style.FILL);
        this.i = new Path();
        this.f = new RectF();
        this.f5215a = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        this.g = new RectF();
        this.e = this.f5218d - (this.f5216b / 2);
    }

    private void b(Canvas canvas) {
        this.h.setStyle(Paint.Style.FILL);
        this.h.setXfermode(this.f5215a);
        if (this.j == null) {
            this.j = Bitmap.createBitmap((int) this.f.width(), (int) this.f.height(), Bitmap.Config.ARGB_8888);
            Canvas canvas2 = new Canvas(this.j);
            Paint paint = new Paint(1);
            paint.setColor(RoundedDrawable.DEFAULT_BORDER_COLOR);
            RectF rectF = this.f;
            int i2 = this.f5218d;
            canvas2.drawRoundRect(rectF, (float) i2, (float) i2, paint);
        }
        canvas.drawBitmap(this.j, 0.0f, 0.0f, this.h);
        this.h.setXfermode((Xfermode) null);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int saveLayer = canvas.saveLayer(this.f, (Paint) null, 31);
        super.onDraw(canvas);
        this.h.reset();
        this.i.reset();
        b(canvas);
        canvas.restoreToCount(saveLayer);
        if (this.m) {
            a(canvas);
        }
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i2, int i3, int i4, int i5) {
        super.onSizeChanged(i2, i3, i4, i5);
        this.k = i2;
        this.l = i3;
        a();
    }

    public void setDrawBorder(boolean z) {
        this.m = z;
        invalidate();
    }
}
