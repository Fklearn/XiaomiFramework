package com.miui.networkassistant.ui.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import com.miui.networkassistant.ui.view.PhysicBasedInterpolator;

public class BackgroundView extends View {
    private int H = 30;
    /* access modifiers changed from: private */
    public int W = 0;
    private int lastColor = -1;
    private float lastProgress = -1.0f;
    private int mHeight;
    /* access modifiers changed from: private */
    public int mOffsetX;
    /* access modifiers changed from: private */
    public int mOffsetY;
    private Paint mWavePaint;
    private Path mWavePath;
    private int mWidth;

    public BackgroundView(Context context) {
        super(context);
        init();
    }

    public BackgroundView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public BackgroundView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    public BackgroundView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        init();
    }

    private void drawWave(Canvas canvas, float f, float f2, float f3, float f4) {
        this.mWavePath.reset();
        float f5 = f2 + f4;
        this.mWavePath.moveTo((-3.0f * f) + f3, f5);
        int i = -3;
        while (true) {
            float f6 = (float) i;
            int i2 = this.mWidth;
            if (f6 < (((float) i2) / f) + 5.0f) {
                float f7 = f6 * f;
                this.mWavePath.quadTo((f / 4.0f) + f7 + f3, f4, (f / 2.0f) + f7 + f3, f5);
                this.mWavePath.quadTo(((3.0f * f) / 4.0f) + f7 + f3, (2.0f * f2) + f4, f7 + f + f3, f5);
                i++;
            } else {
                this.mWavePath.lineTo((float) i2, f5);
                this.mWavePath.lineTo((float) this.mWidth, (float) this.mHeight);
                this.mWavePath.lineTo(0.0f, (float) this.mHeight);
                canvas.drawPath(this.mWavePath, this.mWavePaint);
                return;
            }
        }
    }

    private void init() {
        this.mWavePath = new Path();
        this.mWavePaint = new Paint();
        this.mWavePaint.setStyle(Paint.Style.FILL);
        this.mWavePaint.setStrokeWidth(4.0f);
        this.mWavePaint.setAntiAlias(true);
    }

    private void startAnimation(float f) {
        if (f > 1.0f) {
            f = 1.0f;
        }
        final int random = (int) (((double) this.W) * Math.random());
        final float f2 = (((float) (this.mHeight - (this.H * 2))) * (1.0f - f)) + 50.0f;
        final float f3 = (float) this.mOffsetY;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        ofFloat.setDuration(4500);
        ofFloat.setInterpolator(new DecelerateInterpolator());
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                BackgroundView backgroundView = BackgroundView.this;
                int unused = backgroundView.mOffsetX = ((int) (((float) (backgroundView.W * 2)) * floatValue)) + random;
                BackgroundView.this.invalidate();
            }
        });
        ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        ofFloat2.setDuration(1500);
        ofFloat2.setInterpolator(new PhysicBasedInterpolator.Builder().setDamping(0.6f).setResponse(0.625f).build());
        ofFloat2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                BackgroundView backgroundView = BackgroundView.this;
                float f = f3;
                int unused = backgroundView.mOffsetY = (int) (f + ((f2 - f) * floatValue));
                BackgroundView.this.invalidate();
            }
        });
        ofFloat2.start();
        ofFloat.start();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawWave(canvas, (float) this.W, (float) this.H, (float) this.mOffsetX, (float) this.mOffsetY);
        Canvas canvas2 = canvas;
        drawWave(canvas2, ((float) this.W) * 1.3f, (float) this.H, ((float) this.mOffsetX) * -1.3f, (float) this.mOffsetY);
        drawWave(canvas2, ((float) this.W) * 1.1f, (float) this.H, ((float) this.mOffsetX) * -1.1f, (float) this.mOffsetY);
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        this.mWidth = getMeasuredWidth();
        this.mHeight = getMeasuredHeight();
        this.W = (int) (((float) this.mWidth) * 0.75f);
        this.mWavePaint.setShader(new LinearGradient(0.0f, (float) this.H, 0.0f, (float) this.mHeight, new int[]{Color.parseColor("#28F0F0F0"), Color.parseColor("#28F0F0F0")}, (float[]) null, Shader.TileMode.CLAMP));
        float f = this.lastProgress;
        if (f != -1.0f) {
            setParam(this.lastColor, f, true);
        }
    }

    public void setParam(int i, float f, boolean z) {
        if (z || this.lastProgress != f) {
            this.lastProgress = f;
            this.lastColor = i;
            int i2 = this.mHeight;
            if (i2 != 0) {
                this.mWavePaint.setShader(new LinearGradient(0.0f, (float) this.H, 0.0f, (float) i2, new int[]{i, 0}, (float[]) null, Shader.TileMode.CLAMP));
                invalidate();
                startAnimation(f);
            }
        }
    }
}
