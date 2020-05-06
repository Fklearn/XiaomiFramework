package com.miui.networkassistant.netdiagnose;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import com.miui.networkassistant.utils.BitmapUtil;
import com.miui.securitycenter.R;

public class NetworkDiagnosticsDialView extends View {
    private static final double CONVERSION_ANGLE_CONST = 0.017453292519943295d;
    private static final float MAX_ANGLE = 270.0f;
    private static final float START_ANGLE = 135.2f;
    private RectF fullRectF;
    private ValueAnimator mAnimator;
    private Paint mArcPaint;
    private Bitmap mDashboardArcBitmap;
    private Bitmap mDashboardBgBitmap;
    private Bitmap mDashboardPointerBgBitmap;
    private Bitmap mDashboardPointerProgressBitmap;
    private Paint mLinePaint;
    private float mLineSize;
    /* access modifiers changed from: private */
    public float mProgress = 0.01f;
    private int mWidth;
    private Paint xfermodePaint;

    public NetworkDiagnosticsDialView(Context context) {
        super(context);
        init();
    }

    public NetworkDiagnosticsDialView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public NetworkDiagnosticsDialView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    public NetworkDiagnosticsDialView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        init();
    }

    private float getRotatePointX(float f, float f2, float f3) {
        double d2 = ((double) f) * CONVERSION_ANGLE_CONST;
        return ((float) ((((double) (f2 - ((float) (this.mWidth / 2)))) * Math.cos(d2)) + (((double) (f3 - ((float) (this.mWidth / 2)))) * Math.sin(d2)))) + ((float) (this.mWidth / 2));
    }

    private float getRotatePointY(float f, float f2, float f3) {
        double d2 = ((double) f) * CONVERSION_ANGLE_CONST;
        return ((float) ((((double) (f3 - ((float) (this.mWidth / 2)))) * Math.cos(d2)) - (((double) (f2 - ((float) (this.mWidth / 2)))) * Math.sin(d2)))) + ((float) (this.mWidth / 2));
    }

    private void init() {
        this.mArcPaint = new Paint();
        this.mArcPaint.setColor(-7829368);
        this.mArcPaint.setAntiAlias(true);
        this.mLinePaint = new Paint();
        this.mLinePaint.setColor(-65536);
        this.mLinePaint.setAntiAlias(true);
        this.mLinePaint.setColor(Color.parseColor("#0099FF"));
        this.mLinePaint.setStrokeWidth(6.0f);
        this.xfermodePaint = new Paint();
        this.xfermodePaint.setAntiAlias(true);
    }

    private void makeArc() {
        Bitmap bitmap = this.mDashboardArcBitmap;
        if (bitmap != null) {
            bitmap.recycle();
        }
        this.mDashboardArcBitmap = Bitmap.createBitmap(this.mDashboardPointerBgBitmap.getWidth(), this.mDashboardPointerBgBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        new Canvas(this.mDashboardArcBitmap).drawArc(this.fullRectF, START_ANGLE, this.mProgress * MAX_ANGLE, true, this.mArcPaint);
    }

    private void makeProgressBitmap() {
        Bitmap bitmap = this.mDashboardPointerProgressBitmap;
        if (bitmap != null) {
            bitmap.recycle();
        }
        this.mDashboardPointerProgressBitmap = Bitmap.createBitmap(this.mDashboardPointerBgBitmap.getWidth(), this.mDashboardPointerBgBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(this.mDashboardPointerProgressBitmap);
        canvas.drawBitmap(this.mDashboardPointerBgBitmap, 0.0f, 0.0f, (Paint) null);
        this.xfermodePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        makeArc();
        canvas.drawBitmap(this.mDashboardArcBitmap, 0.0f, 0.0f, this.xfermodePaint);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(this.mDashboardBgBitmap, 0.0f, 0.0f, (Paint) null);
        makeProgressBitmap();
        canvas.drawBitmap(this.mDashboardPointerProgressBitmap, 0.0f, 0.0f, (Paint) null);
        float f = START_ANGLE - (this.mProgress * MAX_ANGLE);
        canvas.drawLine(getRotatePointX(f, (float) (this.mWidth / 2), 0.0f), getRotatePointY(f, (float) (this.mWidth / 2), 0.0f), getRotatePointX(f, (float) (this.mWidth / 2), this.mLineSize), getRotatePointY(f, (float) (this.mWidth / 2), this.mLineSize), this.mLinePaint);
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        this.mWidth = getMeasuredWidth();
        this.mDashboardBgBitmap = BitmapUtil.getSvgBitmap(getContext(), R.drawable.na_nd_dashboard_bg);
        this.mDashboardPointerBgBitmap = BitmapUtil.getSvgBitmap(getContext(), R.drawable.na_nd_dashboard_pointer);
        Matrix matrix = new Matrix();
        float width = ((float) this.mWidth) / ((float) this.mDashboardBgBitmap.getWidth());
        matrix.postScale(width, width);
        Bitmap bitmap = this.mDashboardBgBitmap;
        Matrix matrix2 = matrix;
        this.mDashboardBgBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), this.mDashboardBgBitmap.getHeight(), matrix2, true);
        Bitmap bitmap2 = this.mDashboardPointerBgBitmap;
        this.mDashboardPointerBgBitmap = Bitmap.createBitmap(bitmap2, 0, 0, bitmap2.getWidth(), this.mDashboardPointerBgBitmap.getHeight(), matrix2, true);
        this.fullRectF = new RectF(0.0f, 0.0f, (float) this.mDashboardBgBitmap.getWidth(), (float) this.mDashboardBgBitmap.getWidth());
        this.mLineSize = ((float) this.mDashboardBgBitmap.getWidth()) * 0.11f;
    }

    public void setProgress(float f) {
        if (f <= 0.01f) {
            f = 0.01f;
        } else if (f >= 0.99f) {
            f = 0.99f;
        }
        ValueAnimator valueAnimator = this.mAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.mAnimator = ValueAnimator.ofFloat(new float[]{this.mProgress, f});
        this.mAnimator.setInterpolator(new DecelerateInterpolator());
        this.mAnimator.setDuration(1200);
        this.mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                if (Math.abs(NetworkDiagnosticsDialView.this.mProgress - floatValue) > 0.001f) {
                    float unused = NetworkDiagnosticsDialView.this.mProgress = floatValue;
                    NetworkDiagnosticsDialView.this.invalidate();
                }
            }
        });
        this.mAnimator.start();
    }
}
