package com.miui.gamebooster.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

public class RoundImageView extends C0355y {

    /* renamed from: c  reason: collision with root package name */
    private int f4152c;

    /* renamed from: d  reason: collision with root package name */
    private RectF f4153d;
    private int e;
    private Paint f = new Paint();
    private int g;
    private int h;

    public RoundImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.f.setAntiAlias(true);
    }

    private Bitmap a(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();
        Bitmap createBitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888);
        int i = this.g;
        if (i == 0 || this.h == 0) {
            drawable.setBounds(0, 0, intrinsicWidth, intrinsicHeight);
        } else {
            float width = ((float) i) / ((float) createBitmap.getWidth());
            float height = ((float) this.h) / ((float) createBitmap.getHeight());
            Matrix matrix = new Matrix();
            matrix.postScale(width, height);
            createBitmap = Bitmap.createBitmap(createBitmap, 0, 0, intrinsicWidth, intrinsicHeight, matrix, true);
            drawable.setBounds(0, 0, this.g, this.h);
        }
        drawable.draw(new Canvas(createBitmap));
        return createBitmap;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (getDrawable() != null) {
            Paint paint = this.f;
            Bitmap a2 = a(getDrawable());
            Shader.TileMode tileMode = Shader.TileMode.CLAMP;
            paint.setShader(new BitmapShader(a2, tileMode, tileMode));
            this.f4153d = new RectF(0.0f, 0.0f, (float) getWidth(), (float) getHeight());
            RectF rectF = this.f4153d;
            int i = this.f4152c;
            canvas.drawRoundRect(rectF, (float) i, (float) i, this.f);
            int i2 = this.e ^ 15;
            if ((i2 & 1) != 0) {
                int i3 = this.f4152c;
                canvas.drawRect(0.0f, 0.0f, (float) i3, (float) i3, this.f);
            }
            if ((i2 & 2) != 0) {
                float f2 = this.f4153d.right;
                int i4 = this.f4152c;
                canvas.drawRect(f2 - ((float) i4), 0.0f, f2, (float) i4, this.f);
            }
            if ((i2 & 4) != 0) {
                float f3 = this.f4153d.bottom;
                int i5 = this.f4152c;
                canvas.drawRect(0.0f, f3 - ((float) i5), (float) i5, f3, this.f);
            }
            if ((i2 & 8) != 0) {
                RectF rectF2 = this.f4153d;
                float f4 = rectF2.right;
                int i6 = this.f4152c;
                float f5 = rectF2.bottom;
                canvas.drawRect(f4 - ((float) i6), f5 - ((float) i6), f4, f5, this.f);
            }
        }
    }
}
