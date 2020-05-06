package com.android.server.lights.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import com.android.server.SystemService;
import com.android.server.lights.evaluator.ArgbArrayEvaluator;
import com.android.server.lights.interpolater.LinearInterpolater;
import com.android.server.lights.interpolater.SineEaseInOutInterpolater;
import java.util.ArrayList;
import java.util.List;

public class MusicView extends View {
    private static final float LEFT_LINE_WIDTH = 708.0f;
    private static final float RIGHT_LINE_WIDTH = 118.0f;
    public static final int WAVE_LINE_NUM = 28;
    public static final int WAVE_LINE_RANGE = 12;
    public static final int WAVE_THICK = 4;
    /* access modifiers changed from: private */
    public int beatTar;
    /* access modifiers changed from: private */
    public int[][] colrArray;
    /* access modifiers changed from: private */
    public Point ctrlBall;
    private AnimatorSet mAnimatorSet;
    /* access modifiers changed from: private */
    public ValueAnimator mColorAnimator;
    /* access modifiers changed from: private */
    public int mCurrentColor;
    /* access modifiers changed from: private */
    public int[] mCurrentColorArr;
    /* access modifiers changed from: private */
    public LineData mDataList;
    private Paint mLinePaint;
    private LinearGradient mLinearGradient;
    /* access modifiers changed from: private */
    public int moveTar;
    /* access modifiers changed from: private */
    public ValueAnimator xAnimator;
    /* access modifiers changed from: private */
    public ValueAnimator yAnimator;

    public MusicView(Context context) {
        this(context, (AttributeSet) null);
    }

    public MusicView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MusicView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mDataList = new LineData();
        this.colrArray = new int[][]{new int[]{Color.parseColor("#ff6e02"), Color.parseColor("#ffff00"), Color.parseColor("#ff6e02")}, new int[]{Color.parseColor("#6b25c3"), Color.parseColor("#00ffd8"), Color.parseColor("#6b25c3")}, new int[]{Color.parseColor("#088b4c"), Color.parseColor("#deff00"), Color.parseColor("#088b4c")}, new int[]{Color.parseColor("#004eff"), Color.parseColor("#00f6ff"), Color.parseColor("#004eff")}, new int[]{Color.parseColor("#d37e05"), Color.parseColor("#ff00ba"), Color.parseColor("#d37e05")}, new int[]{Color.parseColor("#4306cf"), Color.parseColor("#d800ff"), Color.parseColor("#4306cf")}};
        this.mCurrentColor = 0;
        this.mCurrentColorArr = new int[3];
        this.ctrlBall = new Point(0, 0);
        init();
    }

    public void init() {
        setBackgroundColor(0);
        if (this.mLinearGradient == null) {
            this.mLinearGradient = new LinearGradient(0.0f, 0.0f, 0.0f, 2250.0f, new int[]{Color.parseColor("#ff6e02"), Color.parseColor("#ffff00"), Color.parseColor("#ff6e02")}, (float[]) null, Shader.TileMode.REPEAT);
        }
        for (int i = 0; i < 28; i++) {
            this.mDataList.mLeftTopPath.add(new Path());
            this.mDataList.mLeftBottomPath.add(new Path());
            this.mDataList.mRightTopPath.add(new Path());
            this.mDataList.mRightBottomPath.add(new Path());
            Paint topPaint = new Paint(1);
            topPaint.setStyle(Paint.Style.STROKE);
            topPaint.setStrokeWidth(4.0f);
            topPaint.setShader(this.mLinearGradient);
            this.mDataList.mTopPaint.add(topPaint);
            Paint bottomPaint = new Paint(1);
            bottomPaint.setStyle(Paint.Style.STROKE);
            bottomPaint.setStrokeWidth(4.0f);
            bottomPaint.setShader(this.mLinearGradient);
            this.mDataList.mBottomPaint.add(bottomPaint);
        }
        this.mLinePaint = new Paint(1);
        this.mLinePaint.setStyle(Paint.Style.STROKE);
        this.mLinePaint.setStrokeWidth(4.0f);
        this.mLinePaint.setShader(this.mLinearGradient);
        setVisibility(8);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int i;
        float waveAlpha;
        Canvas canvas2 = canvas;
        int width = getWidth();
        this.mLinearGradient = new LinearGradient(0.0f, 0.0f, 0.0f, 2250.0f, this.mCurrentColorArr, (float[]) null, Shader.TileMode.REPEAT);
        int index = 0;
        int i2 = 0;
        while (true) {
            i = 28;
            if (i2 >= 28) {
                break;
            }
            this.mDataList.mLeftTopPath.get(i2).reset();
            this.mDataList.mLeftTopPath.get(i2).moveTo(((float) this.mDataList.mTopConfig.x) + LEFT_LINE_WIDTH, (float) this.mDataList.mTopConfig.y);
            this.mDataList.mLeftBottomPath.get(i2).reset();
            this.mDataList.mLeftBottomPath.get(i2).moveTo(((float) this.mDataList.mBottomConfig.x) + LEFT_LINE_WIDTH, (float) this.mDataList.mBottomConfig.y);
            this.mDataList.mRightTopPath.get(i2).reset();
            this.mDataList.mRightTopPath.get(i2).moveTo(((float) width) - RIGHT_LINE_WIDTH, (float) this.mDataList.mBottomConfig.y);
            this.mDataList.mRightBottomPath.get(i2).reset();
            this.mDataList.mRightBottomPath.get(i2).moveTo(((float) width) - RIGHT_LINE_WIDTH, (float) this.mDataList.mBottomConfig.y);
            i2++;
        }
        while (true) {
            float f = 2.0f;
            if (index > this.mDataList.mTopConfig.waveWidth) {
                break;
            }
            int i3 = 0;
            while (i3 < i) {
                float waveRate = ((float) (this.mDataList.mTopConfig.waveHeight - (i3 * 12))) / ((float) this.mDataList.mTopConfig.waveHeight);
                float waveAlpha2 = 1.0f - (((((float) this.mDataList.mTopConfig.waveHeight) * waveRate) * 8.0f) / ((float) this.mDataList.mTopConfig.waveWidth));
                float endX = (((((float) Math.sin((((double) (((((float) index) - ((float) this.mDataList.mTopConfig.waveCenter)) / (((float) this.mDataList.mTopConfig.waveWidth) / 360.0f)) + 90.0f)) * 3.141592653589793d) / 180.0d)) + 1.0f) * ((float) this.mDataList.mTopConfig.waveHeight)) * waveRate) / f;
                this.mDataList.mLeftTopPath.get(i3).lineTo(endX + LEFT_LINE_WIDTH, (float) (this.mDataList.mTopConfig.y + index));
                this.mDataList.mRightTopPath.get(i3).lineTo((((float) width) - RIGHT_LINE_WIDTH) - endX, (float) (this.mDataList.mTopConfig.y + index));
                if (waveAlpha2 < 0.0f) {
                    waveAlpha = 0.0f;
                } else if (waveAlpha2 > 1.0f) {
                    waveAlpha = 1.0f;
                } else {
                    waveAlpha = waveAlpha2;
                }
                this.mDataList.mTopPaint.get(i3).setAlpha((int) (waveAlpha * 255.0f));
                this.mDataList.mTopPaint.get(i3).setShader(this.mLinearGradient);
                i3++;
                i = 28;
                f = 2.0f;
            }
            index++;
            i = 28;
        }
        for (int index2 = 0; index2 <= this.mDataList.mBottomConfig.waveWidth; index2++) {
            for (int i4 = 0; i4 < 28; i4++) {
                float waveRate1 = ((float) (this.mDataList.mBottomConfig.waveHeight - (i4 * 12))) / ((float) this.mDataList.mBottomConfig.waveHeight);
                float waveAlpha3 = 1.0f - (((((float) this.mDataList.mBottomConfig.waveHeight) * waveRate1) * 8.0f) / ((float) this.mDataList.mBottomConfig.waveWidth));
                float endX1 = (((((float) Math.sin((((double) (((((float) index2) - ((float) this.mDataList.mBottomConfig.waveCenter)) / (((float) this.mDataList.mBottomConfig.waveWidth) / 360.0f)) + 90.0f)) * 3.141592653589793d) / 180.0d)) + 1.0f) * ((float) this.mDataList.mBottomConfig.waveHeight)) * waveRate1) / 2.0f;
                this.mDataList.mLeftBottomPath.get(i4).lineTo(endX1 + LEFT_LINE_WIDTH, (float) (this.mDataList.mBottomConfig.y + index2));
                this.mDataList.mRightBottomPath.get(i4).lineTo((((float) width) - RIGHT_LINE_WIDTH) - endX1, (float) (this.mDataList.mBottomConfig.y + index2));
                if (waveAlpha3 < 0.0f) {
                    waveAlpha3 = 0.0f;
                } else if (waveAlpha3 > 1.0f) {
                    waveAlpha3 = 1.0f;
                }
                this.mDataList.mBottomPaint.get(i4).setAlpha((int) (waveAlpha3 * 255.0f));
                this.mDataList.mBottomPaint.get(i4).setShader(this.mLinearGradient);
            }
        }
        for (int i5 = 0; i5 < 28; i5++) {
            canvas2.drawPath(this.mDataList.mLeftTopPath.get(i5), this.mDataList.mTopPaint.get(i5));
            canvas2.drawPath(this.mDataList.mRightTopPath.get(i5), this.mDataList.mTopPaint.get(i5));
            canvas2.drawPath(this.mDataList.mLeftBottomPath.get(i5), this.mDataList.mBottomPaint.get(i5));
            canvas2.drawPath(this.mDataList.mRightBottomPath.get(i5), this.mDataList.mBottomPaint.get(i5));
        }
        this.mLinePaint.setStrokeWidth(LEFT_LINE_WIDTH);
        this.mLinePaint.setShader(this.mLinearGradient);
        canvas.drawLine(354.0f, 0.0f, 354.0f, (float) getHeight(), this.mLinePaint);
        this.mLinePaint.setStrokeWidth(RIGHT_LINE_WIDTH);
        canvas.drawLine(((float) getWidth()) - 59.0f, 0.0f, ((float) getWidth()) - 59.0f, (float) getHeight(), this.mLinePaint);
    }

    public void startAnimation() {
        setVisibility(0);
        this.mAnimatorSet = new AnimatorSet();
        this.yAnimator = ValueAnimator.ofInt(new int[]{1200, ((int) (Math.random() * 1000.0d)) + 1200});
        this.yAnimator.setDuration(3000);
        this.yAnimator.setInterpolator(new SineEaseInOutInterpolater());
        this.yAnimator.setRepeatCount(-1);
        this.yAnimator.setRepeatMode(1);
        this.yAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                int unused = MusicView.this.moveTar = ((Integer) animation.getAnimatedValue()).intValue();
            }
        });
        this.yAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationRepeat(Animator animation) {
                MusicView.this.yAnimator.setIntValues(new int[]{1200, ((int) (Math.random() * 1000.0d)) + 1200});
                super.onAnimationRepeat(animation);
            }
        });
        this.xAnimator = ValueAnimator.ofInt(new int[]{2004 - ((int) ((Math.random() * 240.0d) + 80.0d)), 2004});
        this.xAnimator.setDuration(500);
        this.xAnimator.setInterpolator(new LinearInterpolater());
        this.xAnimator.setRepeatCount(-1);
        this.xAnimator.setRepeatMode(1);
        this.xAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                int unused = MusicView.this.beatTar = ((Integer) animation.getAnimatedValue()).intValue();
                MusicView.this.ctrlBall.x += (int) (((double) (MusicView.this.beatTar - MusicView.this.ctrlBall.x)) * (MusicView.this.ctrlBall.x > MusicView.this.beatTar ? 0.5d : 0.2d));
                MusicView.this.ctrlBall.y = MusicView.this.moveTar;
                MusicView.this.mDataList.mTopConfig.waveCenter = MusicView.this.ctrlBall.y - 1200;
                MusicView.this.mDataList.mTopConfig.waveWidth = (MusicView.this.ctrlBall.y - 1200) * 2;
                MusicView.this.mDataList.mTopConfig.waveHeight = (int) (((double) (2004 - MusicView.this.ctrlBall.x)) * 1.2d);
                MusicView.this.mDataList.mBottomConfig.y = (MusicView.this.ctrlBall.y - 1200) * 2;
                MusicView.this.mDataList.mBottomConfig.waveCenter = (2250 - MusicView.this.mDataList.mBottomConfig.y) / 2;
                MusicView.this.mDataList.mBottomConfig.waveWidth = 2250 - MusicView.this.mDataList.mBottomConfig.y;
                MusicView.this.mDataList.mBottomConfig.waveHeight = (int) (((double) (2004 - MusicView.this.ctrlBall.x)) * 1.2d);
                MusicView.this.invalidate();
            }
        });
        this.xAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationRepeat(Animator animation) {
                MusicView.this.xAnimator.setIntValues(new int[]{2004 - ((int) ((Math.random() * 240.0d) + 80.0d)), 2004});
                super.onAnimationRepeat(animation);
            }

            public void onAnimationEnd(Animator animation) {
                MusicView.this.setVisibility(8);
                MusicView.this.mColorAnimator.cancel();
                MusicView.this.yAnimator.cancel();
                super.onAnimationEnd(animation);
            }
        });
        ArgbArrayEvaluator argbArrayEvaluator = new ArgbArrayEvaluator();
        int[][] iArr = this.colrArray;
        int i = this.mCurrentColor;
        this.mColorAnimator = ValueAnimator.ofObject(argbArrayEvaluator, new Object[]{iArr[i], iArr[(i + 1) % 6]});
        this.mColorAnimator.setDuration(1000);
        this.mColorAnimator.setInterpolator(new SineEaseInOutInterpolater());
        this.mColorAnimator.setRepeatCount(-1);
        this.mColorAnimator.setRepeatMode(1);
        this.mColorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                int[] unused = MusicView.this.mCurrentColorArr = (int[]) animation.getAnimatedValue();
            }
        });
        this.mColorAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationRepeat(Animator animation) {
                MusicView musicView = MusicView.this;
                int unused = musicView.mCurrentColor = (musicView.mCurrentColor + 1) % 6;
                MusicView.this.mColorAnimator.setObjectValues(new Object[]{MusicView.this.colrArray[MusicView.this.mCurrentColor], MusicView.this.colrArray[(MusicView.this.mCurrentColor + 1) % 6]});
                super.onAnimationRepeat(animation);
            }
        });
        this.mAnimatorSet.playTogether(new Animator[]{this.xAnimator, this.yAnimator, this.mColorAnimator});
        this.mAnimatorSet.start();
    }

    public void stopAnimation() {
        ValueAnimator valueAnimator = this.xAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
    }

    public boolean isAnimationRunning() {
        ValueAnimator valueAnimator = this.xAnimator;
        if (valueAnimator != null) {
            return valueAnimator.isRunning();
        }
        return false;
    }

    class WaveConfig {
        public int acc = 120;
        public int waveCenter = SystemService.PHASE_THIRD_PARTY_APPS_CAN_START;
        public int waveHeight = 100;
        public int waveWidth = 1600;
        public int width = 2250;
        public int x = 0;
        public int y = 0;

        WaveConfig() {
        }
    }

    class LineData {
        public WaveConfig mBottomConfig = new WaveConfig();
        public List<Paint> mBottomPaint = new ArrayList();
        public List<Path> mLeftBottomPath = new ArrayList();
        public List<Path> mLeftTopPath = new ArrayList();
        public List<Path> mRightBottomPath = new ArrayList();
        public List<Path> mRightTopPath = new ArrayList();
        public WaveConfig mTopConfig = new WaveConfig();
        public List<Paint> mTopPaint = new ArrayList();

        public LineData() {
        }
    }
}
