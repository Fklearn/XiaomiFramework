package b.c.a.b.c;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import b.c.a.b.a.f;

public class b implements a {

    /* renamed from: a  reason: collision with root package name */
    protected final int f2000a;

    /* renamed from: b  reason: collision with root package name */
    protected final int f2001b;

    public static class a extends Drawable {

        /* renamed from: a  reason: collision with root package name */
        protected final float f2002a;

        /* renamed from: b  reason: collision with root package name */
        protected final int f2003b;

        /* renamed from: c  reason: collision with root package name */
        protected final RectF f2004c = new RectF();

        /* renamed from: d  reason: collision with root package name */
        protected final RectF f2005d;
        protected final BitmapShader e;
        protected final Paint f;

        public a(Bitmap bitmap, int i, int i2) {
            this.f2002a = (float) i;
            this.f2003b = i2;
            Shader.TileMode tileMode = Shader.TileMode.CLAMP;
            this.e = new BitmapShader(bitmap, tileMode, tileMode);
            float f2 = (float) i2;
            this.f2005d = new RectF(f2, f2, (float) (bitmap.getWidth() - i2), (float) (bitmap.getHeight() - i2));
            this.f = new Paint();
            this.f.setAntiAlias(true);
            this.f.setShader(this.e);
        }

        public void draw(Canvas canvas) {
            RectF rectF = this.f2004c;
            float f2 = this.f2002a;
            canvas.drawRoundRect(rectF, f2, f2, this.f);
        }

        public int getOpacity() {
            return -3;
        }

        /* access modifiers changed from: protected */
        public void onBoundsChange(Rect rect) {
            super.onBoundsChange(rect);
            RectF rectF = this.f2004c;
            int i = this.f2003b;
            rectF.set((float) i, (float) i, (float) (rect.width() - this.f2003b), (float) (rect.height() - this.f2003b));
            Matrix matrix = new Matrix();
            matrix.setRectToRect(this.f2005d, this.f2004c, Matrix.ScaleToFit.FILL);
            this.e.setLocalMatrix(matrix);
        }

        public void setAlpha(int i) {
            this.f.setAlpha(i);
        }

        public void setColorFilter(ColorFilter colorFilter) {
            this.f.setColorFilter(colorFilter);
        }
    }

    public b(int i) {
        this(i, 0);
    }

    public b(int i, int i2) {
        this.f2000a = i;
        this.f2001b = i2;
    }

    public void a(Bitmap bitmap, b.c.a.b.e.a aVar, f fVar) {
        if (aVar instanceof b.c.a.b.e.b) {
            aVar.a((Drawable) new a(bitmap, this.f2000a, this.f2001b));
            return;
        }
        throw new IllegalArgumentException("ImageAware should wrap ImageView. ImageViewAware is expected.");
    }
}
