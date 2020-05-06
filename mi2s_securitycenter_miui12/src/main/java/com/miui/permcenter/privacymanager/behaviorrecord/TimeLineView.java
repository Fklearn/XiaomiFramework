package com.miui.permcenter.privacymanager.behaviorrecord;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import com.miui.securitycenter.R;

public class TimeLineView extends View {

    /* renamed from: a  reason: collision with root package name */
    private Resources f6426a;

    /* renamed from: b  reason: collision with root package name */
    private Paint f6427b;

    /* renamed from: c  reason: collision with root package name */
    private Paint f6428c;

    /* renamed from: d  reason: collision with root package name */
    private int f6429d;
    private int e;
    private int f;
    private boolean g;
    private Bitmap h;

    public TimeLineView(Context context) {
        super(context);
        a(context);
    }

    public TimeLineView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        a(context);
    }

    public TimeLineView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        a(context);
    }

    private void a(Context context) {
        this.f6426a = context.getResources();
        this.f6427b = new Paint();
        this.f6427b.setColor(this.f6426a.getColor(R.color.app_behavior_timeline));
        this.f6428c = new Paint();
        this.f6428c.setColor(this.f6426a.getColor(R.color.app_behavior_timeline));
        this.g = false;
        this.f6429d = 0;
    }

    public void a(boolean z, boolean z2) {
        Paint paint = this.f6427b;
        Resources resources = this.f6426a;
        int i = R.color.tx_runtime_behavior;
        paint.setColor(resources.getColor(z ? R.color.tx_runtime_behavior : R.color.app_behavior_timeline));
        Paint paint2 = this.f6428c;
        Resources resources2 = this.f6426a;
        if (!z) {
            i = R.color.app_behavior_timeline;
        }
        paint2.setColor(resources2.getColor(i));
        setImportant(z2);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int i;
        super.onDraw(canvas);
        int i2 = this.e / 2;
        int i3 = this.f / 2;
        this.f6427b.setStrokeWidth(0.0f);
        this.f6428c.setStrokeWidth((float) (i2 / 8));
        if (!this.g || this.h == null) {
            i = this.f6426a.getDimensionPixelSize(R.dimen.app_behavior_timeline_radius) / 2;
            canvas.drawCircle((float) i2, (float) i3, (float) i, this.f6428c);
        } else {
            i = this.f6426a.getDimensionPixelSize(R.dimen.app_behavior_item_line_width) / 2;
            canvas.drawBitmap(this.h, (float) (i2 - i), (float) (i3 - i), (Paint) null);
        }
        if ((this.f6429d & 16) != 0) {
            float f2 = (float) i2;
            canvas.drawLine(f2, 0.0f, f2, (float) (i3 - i), this.f6427b);
        }
        if ((this.f6429d & 1) != 0) {
            float f3 = (float) i2;
            canvas.drawLine(f3, (float) (i3 + i), f3, (float) this.f, this.f6427b);
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        this.e = getMeasuredWidth();
        this.f = getMeasuredHeight();
    }

    public void setImportant(boolean z) {
        this.g = z;
        if (this.g && this.h == null) {
            Drawable drawable = this.f6426a.getDrawable(R.drawable.pm_app_behavior_warn_icon);
            this.h = Bitmap.createBitmap(this.f6426a.getDimensionPixelSize(R.dimen.app_behavior_item_line_width), this.f6426a.getDimensionPixelSize(R.dimen.app_behavior_item_line_width), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(this.h);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }
        invalidate();
    }

    public void setSpecialLine(int i) {
        if (this.f6429d != i) {
            this.f6429d = i;
            invalidate();
        }
    }
}
