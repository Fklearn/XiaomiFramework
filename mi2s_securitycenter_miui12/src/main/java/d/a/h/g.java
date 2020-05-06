package d.a.h;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import d.a.b;

public class g extends Drawable {

    /* renamed from: a  reason: collision with root package name */
    private static final View.OnAttachStateChangeListener f8772a = new e();

    /* renamed from: b  reason: collision with root package name */
    private View f8773b;

    /* renamed from: c  reason: collision with root package name */
    private Bitmap f8774c;

    /* renamed from: d  reason: collision with root package name */
    private Paint f8775d = new Paint();
    private RectF e = new RectF();
    private Rect f = new Rect();
    /* access modifiers changed from: private */
    public Drawable g;
    private c h;
    private boolean i;

    public static g a(View view) {
        if (Build.VERSION.SDK_INT < 23) {
            return null;
        }
        Drawable foreground = view.getForeground();
        if (foreground instanceof g) {
            return (g) foreground;
        }
        return null;
    }

    /* access modifiers changed from: private */
    public void a() {
        this.h = null;
        this.i = false;
        d();
    }

    private void a(int i2) {
        if (Build.VERSION.SDK_INT >= 23) {
            Bitmap bitmap = this.f8774c;
            if (bitmap == null || bitmap.isRecycled()) {
                this.f8773b.setForeground(this.g);
                return;
            }
            try {
                this.f8774c.eraseColor(0);
                Canvas canvas = new Canvas(this.f8774c);
                canvas.translate((float) (-this.f8773b.getScrollX()), (float) (-this.f8773b.getScrollY()));
                this.f8773b.setForeground(this.g);
                this.f8773b.draw(canvas);
                this.f8773b.setForeground(this);
                if (i2 == 0) {
                    b();
                }
            } catch (OutOfMemoryError unused) {
                Log.w("miuix_anim", "TintDrawable.initBitmap failed, out of memory");
            }
        }
    }

    private void a(int i2, int i3) {
        if (Build.VERSION.SDK_INT >= 23) {
            Bitmap bitmap = this.f8774c;
            if (bitmap == null || bitmap.getWidth() != i2 || this.f8774c.getHeight() != this.f8773b.getHeight()) {
                d();
                this.f8775d.setAntiAlias(true);
                try {
                    this.f8774c = Bitmap.createBitmap(i2, i3, Bitmap.Config.ARGB_8888);
                } catch (OutOfMemoryError unused) {
                    Log.w("miuix_anim", "TintDrawable.createBitmap failed, out of memory");
                }
            }
        }
    }

    private void a(Drawable drawable) {
        this.g = drawable;
    }

    static g b(View view) {
        g a2 = a(view);
        if (a2 != null || Build.VERSION.SDK_INT < 23) {
            return a2;
        }
        g gVar = new g();
        gVar.f8773b = view;
        gVar.a(view.getForeground());
        view.addOnAttachStateChangeListener(f8772a);
        b.a(view, (Runnable) new f(view, gVar));
        return gVar;
    }

    private void b() {
        int width = this.f8774c.getWidth();
        int height = this.f8774c.getHeight();
        int[] iArr = new int[(width * height)];
        this.f8774c.getPixels(iArr, 0, width, 0, 0, width, height);
        for (int i2 = 0; i2 < iArr.length; i2++) {
            if (Color.alpha(iArr[i2]) > 1) {
                iArr[i2] = -16777216;
            }
        }
        this.f8774c.setPixels(iArr, 0, width, 0, 0, width, height);
    }

    private boolean c() {
        return this.i && Color.alpha(this.h.d()) == 0;
    }

    private void d() {
        Bitmap bitmap = this.f8774c;
        if (bitmap != null) {
            bitmap.recycle();
            this.f8774c = null;
        }
    }

    /* access modifiers changed from: package-private */
    public void a(c cVar, int i2) {
        View view = this.f8773b;
        if (view != null) {
            int width = view.getWidth();
            int height = this.f8773b.getHeight();
            if (width == 0 || height == 0) {
                d();
                return;
            }
            this.h = cVar;
            this.i = false;
            a(width, height);
            a(i2);
        }
    }

    public void draw(@NonNull Canvas canvas) {
        Bitmap bitmap;
        Drawable drawable = this.g;
        if (drawable != null) {
            drawable.draw(canvas);
        }
        if (this.h != null && !c() && (bitmap = this.f8774c) != null && !bitmap.isRecycled()) {
            this.i = true;
            int scrollX = this.f8773b.getScrollX();
            int scrollY = this.f8773b.getScrollY();
            int width = this.f8773b.getWidth();
            int height = this.f8773b.getHeight();
            this.e.set((float) scrollX, (float) scrollY, (float) (scrollX + width), (float) (scrollY + height));
            this.f.set(0, 0, width, height);
            canvas.save();
            canvas.clipRect(this.e);
            canvas.drawColor(0);
            this.f8775d.setColorFilter(new PorterDuffColorFilter(this.h.d(), PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(this.f8774c, this.f, this.e, this.f8775d);
            canvas.restore();
        } else if (Build.VERSION.SDK_INT >= 23) {
            this.f8773b.setForeground(this.g);
        }
    }

    public int getOpacity() {
        return -2;
    }

    public void setAlpha(int i2) {
    }

    public void setColorFilter(@Nullable ColorFilter colorFilter) {
    }
}
