package com.android.server.wm;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Slog;
import android.view.View;

public class MiuiFreeFormRoundRectView extends View {
    private static final String TAG = "MiuiFreeFormRoundRectView";
    private float mBottomRadius;
    private RectF mBounds;
    private float mCurrentAlpha;
    private float mCurrentBgAlpha;
    private float mCurrentBgColor;
    private float mCurrentColor;
    private float mCurrentRaduis;
    private Paint mPaint;
    private float mTopRadius;

    public MiuiFreeFormRoundRectView(Context context) {
        this(context, (AttributeSet) null);
        this.mTopRadius = (float) MiuiFreeFormGestureDetector.getScreenRoundCornerRadiusTop(context);
        this.mBottomRadius = (float) MiuiFreeFormGestureDetector.getScreenRoundCornerRadiusBottom(context);
        if (this.mTopRadius == -1.0f) {
            this.mTopRadius = 66.0f;
        }
        if (this.mBottomRadius == -1.0f) {
            this.mBottomRadius = 66.0f;
        }
    }

    public MiuiFreeFormRoundRectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mBounds = new RectF();
        this.mTopRadius = 66.0f;
        this.mBottomRadius = 66.0f;
        this.mTopRadius = (float) MiuiFreeFormGestureDetector.getScreenRoundCornerRadiusTop(context);
        this.mBottomRadius = (float) MiuiFreeFormGestureDetector.getScreenRoundCornerRadiusBottom(context);
        if (this.mTopRadius == -1.0f) {
            this.mTopRadius = 66.0f;
        }
        if (this.mBottomRadius == -1.0f) {
            this.mBottomRadius = 66.0f;
        }
        this.mPaint = new Paint();
        this.mCurrentColor = 196.0f;
        this.mCurrentAlpha = 0.8f;
        this.mCurrentBgColor = 196.0f;
        this.mCurrentBgAlpha = 0.3f;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (MiuiFreeFormGestureController.DEBUG) {
            Slog.d(TAG, "onDraw:" + this.mBounds + " mTopRadius:" + this.mTopRadius + " mBottomRadius:" + this.mBottomRadius);
        }
        super.onDraw(canvas);
        this.mPaint.reset();
        this.mPaint.setAntiAlias(true);
        this.mPaint.setStyle(Paint.Style.FILL);
        Paint paint = this.mPaint;
        float f = this.mCurrentBgColor;
        paint.setColor(Color.valueOf(f / 255.0f, f / 255.0f, f / 255.0f, this.mCurrentBgAlpha).toArgb());
        canvas.drawRoundRect(this.mBounds, this.mTopRadius, this.mBottomRadius, this.mPaint);
        this.mPaint.reset();
        this.mPaint.setAntiAlias(true);
        this.mPaint.setStrokeWidth(8.0f);
        this.mPaint.setStyle(Paint.Style.STROKE);
        Paint paint2 = this.mPaint;
        float f2 = this.mCurrentColor;
        paint2.setColor(Color.valueOf(f2 / 255.0f, f2 / 255.0f, f2 / 255.0f, this.mCurrentAlpha).toArgb());
        canvas.drawRoundRect(this.mBounds, this.mTopRadius, this.mBottomRadius, this.mPaint);
    }

    public void setRectBounds(Rect rect) {
        this.mBounds.set(rect);
        invalidate();
    }
}
