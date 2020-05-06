package com.miui.gamebooster.videobox.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.miui.activityutil.o;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SrsLevelSeekBarPro extends View {

    /* renamed from: a  reason: collision with root package name */
    private static final String[] f5227a = {o.f2309a, o.f2310b, "2", o.f2312d};

    /* renamed from: b  reason: collision with root package name */
    private int f5228b = 4;

    /* renamed from: c  reason: collision with root package name */
    private int f5229c = 0;

    /* renamed from: d  reason: collision with root package name */
    private List<Float> f5230d = new ArrayList();
    private float e;
    private int f;
    private int g;
    private int h;
    private int i;
    private int j;
    private int k;
    private int l;
    private int m;
    private RectF n = new RectF();
    private RectF o = new RectF();
    private Paint p;
    private a q;

    public interface a {
        void a(SrsLevelSeekBarPro srsLevelSeekBarPro, int i);
    }

    public SrsLevelSeekBarPro(Context context) {
        super(context);
        a(context, (AttributeSet) null, 0);
    }

    public SrsLevelSeekBarPro(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        a(context, attributeSet, 0);
    }

    public SrsLevelSeekBarPro(Context context, AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        a(context, attributeSet, i2);
    }

    public static float a(Paint paint) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float f2 = fontMetrics.descent;
        return ((f2 - fontMetrics.ascent) / 2.0f) - f2;
    }

    private void a(Context context, AttributeSet attributeSet, int i2) {
        setBackgroundResource(R.drawable.gb_videobox_seekbar_bg);
        Resources resources = context.getResources();
        this.f = resources.getDimensionPixelSize(R.dimen.vtb_srs_seekbar_bg_radius);
        this.g = resources.getDimensionPixelSize(R.dimen.vtb_srs_seekbar_select_radius);
        this.h = resources.getDimensionPixelSize(R.dimen.vtb_srs_seekbar_padding);
        this.j = resources.getColor(R.color.color_vtb_srs_seekbar_select_bg);
        this.k = resources.getColor(R.color.color_vtb_srs_seekbar_bg);
        this.m = resources.getColor(R.color.color_vtb_srs_seekbar_select_txt);
        this.l = resources.getColor(R.color.color_vtb_srs_seekbar_normal_txt);
        this.p = new Paint();
        this.p.setAntiAlias(true);
        this.p.setStyle(Paint.Style.FILL);
        this.p.setStrokeWidth(0.0f);
        this.p.setTextSize(resources.getDimension(R.dimen.vtb_srs_seekbar_txt_size));
        this.f5228b = f5227a.length;
    }

    private boolean a() {
        return TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == 1;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Typeface typeface;
        Paint paint;
        Typeface typeface2;
        Paint paint2;
        super.onDraw(canvas);
        this.p.setColor(this.k);
        RectF rectF = this.n;
        int i2 = this.f;
        canvas.drawRoundRect(rectF, (float) i2, (float) i2, this.p);
        int i3 = 0;
        if (!a()) {
            RectF rectF2 = this.o;
            int i4 = this.h;
            rectF2.set((float) i4, (float) i4, this.f5230d.get(this.f5229c).floatValue() + ((float) (this.i / 2)), (float) (getHeight() - this.h));
            this.p.setColor(this.j);
            RectF rectF3 = this.o;
            int i5 = this.g;
            canvas.drawRoundRect(rectF3, (float) i5, (float) i5, this.p);
            while (i3 < this.f5228b) {
                if (i3 == this.f5229c) {
                    this.p.setColor(this.m);
                    paint2 = this.p;
                    typeface2 = Typeface.DEFAULT_BOLD;
                } else {
                    this.p.setColor(this.l);
                    paint2 = this.p;
                    typeface2 = Typeface.DEFAULT;
                }
                paint2.setTypeface(typeface2);
                canvas.drawText(f5227a[i3], (float) ((int) (this.f5230d.get(i3).floatValue() - (this.p.measureText(f5227a[i3]) / 2.0f))), a(this.p) + this.e, this.p);
                i3++;
            }
            return;
        }
        int width = getWidth();
        RectF rectF4 = this.o;
        float floatValue = (((float) width) - this.f5230d.get(this.f5229c).floatValue()) - ((float) (this.i / 2));
        int i6 = this.h;
        rectF4.set(floatValue, (float) i6, (float) (width - i6), (float) (getHeight() - this.h));
        this.p.setColor(this.j);
        RectF rectF5 = this.o;
        int i7 = this.g;
        canvas.drawRoundRect(rectF5, (float) i7, (float) i7, this.p);
        int i8 = (this.f5228b - 1) - this.f5229c;
        while (i3 < this.f5228b) {
            if (i3 == i8) {
                this.p.setColor(this.m);
                paint = this.p;
                typeface = Typeface.DEFAULT_BOLD;
            } else {
                this.p.setColor(this.l);
                paint = this.p;
                typeface = Typeface.DEFAULT;
            }
            paint.setTypeface(typeface);
            String str = f5227a[(this.f5228b - 1) - i3];
            canvas.drawText(str, (float) ((int) (this.f5230d.get(i3).floatValue() - (this.p.measureText(str) / 2.0f))), a(this.p) + this.e, this.p);
            i3++;
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i2, int i3, int i4, int i5) {
        super.onLayout(z, i2, i3, i4, i5);
        this.e = (float) (getHeight() / 2);
        float width = (float) ((getWidth() - getHeight()) / (this.f5228b - 1));
        this.f5230d.clear();
        for (int i6 = 0; i6 < this.f5228b; i6++) {
            this.f5230d.add(Float.valueOf(((float) (getHeight() / 2)) + (((float) i6) * width)));
        }
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i2, int i3, int i4, int i5) {
        super.onSizeChanged(i2, i3, i4, i5);
        this.n.set(0.0f, 0.0f, (float) i2, (float) i3);
        this.i = i3 - (this.h * 2);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action != 0 && action != 1 && action != 2) {
            return false;
        }
        float f2 = 2.14748365E9f;
        int i2 = 0;
        for (int i3 = 0; i3 < this.f5228b; i3++) {
            float abs = Math.abs(motionEvent.getX() - this.f5230d.get(i3).floatValue());
            if (abs < f2) {
                i2 = i3;
                f2 = abs;
            }
        }
        if (a()) {
            i2 = (this.f5228b - 1) - i2;
        }
        if (i2 != this.f5229c) {
            this.f5229c = i2;
            a aVar = this.q;
            if (aVar != null) {
                aVar.a(this, this.f5229c);
            }
            invalidate();
        }
        return true;
    }

    public void setCurrentLevel(int i2) {
        this.f5229c = i2;
        invalidate();
    }

    public void setLevelChangeListener(a aVar) {
        this.q = aVar;
    }
}
