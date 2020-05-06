package com.miui.permcenter.settings.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import com.miui.securitycenter.R;

public class c extends FrameLayout implements ViewTreeObserver.OnGlobalLayoutListener {

    /* renamed from: a  reason: collision with root package name */
    private Context f6589a;

    /* renamed from: b  reason: collision with root package name */
    private View f6590b;

    /* renamed from: c  reason: collision with root package name */
    private Paint f6591c;

    /* renamed from: d  reason: collision with root package name */
    private boolean f6592d;
    private int[] e;
    private PorterDuffXfermode f;
    private Bitmap g;
    private int h;
    private Canvas i;
    private int[] j;
    /* access modifiers changed from: private */
    public b k;

    public static class a {

        /* renamed from: a  reason: collision with root package name */
        static c f6593a;

        /* renamed from: b  reason: collision with root package name */
        static a f6594b = new a();

        private a() {
        }

        public static a a(Context context) {
            f6593a = new c(context);
            return f6594b;
        }

        public a a(int i) {
            f6593a.setBgColor(i);
            return f6594b;
        }

        public a a(View view) {
            f6593a.setTargetView(view);
            return f6594b;
        }

        public a a(b bVar) {
            f6593a.setOnclickListener(bVar);
            return f6594b;
        }

        public c a() {
            f6593a.e();
            return f6593a;
        }
    }

    public interface b {
        void a();
    }

    public c(Context context) {
        super(context);
        this.f6589a = context;
    }

    private void a(Canvas canvas) {
        this.g = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
        this.i = new Canvas(this.g);
        Paint paint = new Paint();
        int i2 = this.h;
        if (i2 == 0) {
            i2 = getResources().getColor(R.color.gb_wonderful_video_shape_video_play);
        }
        paint.setColor(i2);
        Canvas canvas2 = this.i;
        canvas2.drawRect(0.0f, 0.0f, (float) canvas2.getWidth(), (float) this.i.getHeight(), paint);
        if (this.f6591c == null) {
            this.f6591c = new Paint();
        }
        this.f = new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT);
        this.f6591c.setColor(0);
        this.f6591c.setXfermode(this.f);
        this.f6591c.setAntiAlias(true);
        this.i.drawRoundRect(getRectF(), 48.0f, 48.0f, this.f6591c);
        canvas.drawBitmap(this.g, 0.0f, 0.0f, paint);
        this.g.recycle();
    }

    private boolean d() {
        return this.f6590b == null;
    }

    /* access modifiers changed from: private */
    public void e() {
        setOnClickListener(new b(this));
    }

    public void a() {
        this.f6590b.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        removeAllViews();
        ((FrameLayout) ((Activity) this.f6589a).getWindow().getDecorView()).removeView(this);
        b();
    }

    public void b() {
        this.f6591c = null;
        this.f6592d = false;
        this.e = null;
        this.f = null;
        this.g = null;
        this.i = null;
    }

    public void c() {
        if (!d()) {
            View view = this.f6590b;
            if (view != null) {
                view.getViewTreeObserver().addOnGlobalLayoutListener(this);
            }
            setBackgroundResource(R.color.transparent);
            ((FrameLayout) ((Activity) this.f6589a).getWindow().getDecorView()).addView(this);
        }
    }

    public int[] getCenter() {
        return this.e;
    }

    public int[] getLocation() {
        return this.j;
    }

    public RectF getRectF() {
        RectF rectF = new RectF();
        View view = this.f6590b;
        if (view != null) {
            int[] iArr = new int[2];
            view.getLocationOnScreen(iArr);
            rectF.left = (float) iArr[0];
            rectF.top = (float) iArr[1];
            rectF.right = (float) (iArr[0] + this.f6590b.getWidth());
            rectF.bottom = (float) (iArr[1] + this.f6590b.getHeight());
        }
        return rectF;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.f6592d && this.f6590b != null) {
            a(canvas);
        }
    }

    public void onGlobalLayout() {
        if (!this.f6592d) {
            if (this.f6590b.getHeight() > 0 && this.f6590b.getWidth() > 0) {
                this.f6592d = true;
            }
            if (this.e == null) {
                this.j = new int[2];
                this.f6590b.getLocationInWindow(this.j);
                this.e = new int[2];
                this.e[0] = this.j[0] + (this.f6590b.getWidth() / 2);
                this.e[1] = this.j[1] + (this.f6590b.getHeight() / 2);
            }
        }
    }

    public void setBgColor(int i2) {
        this.h = i2;
    }

    public void setCenter(int[] iArr) {
        this.e = iArr;
    }

    public void setLocation(int[] iArr) {
        this.j = iArr;
    }

    public void setOnclickListener(b bVar) {
        this.k = bVar;
    }

    public void setTargetView(View view) {
        this.f6590b = view;
    }
}
