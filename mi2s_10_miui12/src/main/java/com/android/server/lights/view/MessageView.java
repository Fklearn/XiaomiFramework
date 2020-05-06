package com.android.server.lights.view;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.miui.R;
import android.util.AttributeSet;
import android.view.View;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import miui.animation.physics.DynamicAnimation;
import miui.animation.physics.SpringAnimation;
import miui.animation.physics.SpringForce;
import miui.animation.property.FloatValueHolder;

public class MessageView extends View {
    private static final float LEFT_PENDDING = 708.0f;
    private static final float RIGHT_PENDDING = 118.0f;
    public static final int STAR_MAX = 100;
    public static final int WAVE_LINE_NUM = 9;
    public static final int WAVE_LINE_RANGE = 8;
    public static final int WAVE_THICK = 2;
    /* access modifiers changed from: private */
    public float mAlphaTarX;
    /* access modifiers changed from: private */
    public float mCtrlTarX;
    private LineData mDataList;
    /* access modifiers changed from: private */
    public PictureData mLightBlue;
    /* access modifiers changed from: private */
    public PictureData mLightHigh;
    private LinearGradient mLinearGradient;
    private Paint mPaint;
    private SpringAnimation mSpringAnimation;
    /* access modifiers changed from: private */
    public Star[] mStarArr;

    public MessageView(Context context) {
        this(context, (AttributeSet) null);
    }

    public MessageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MessageView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MessageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mLightHigh = new PictureData();
        this.mLightBlue = new PictureData();
        this.mDataList = new LineData();
        this.mStarArr = new Star[100];
        init();
    }

    private void init() {
        setBackgroundColor(0);
        if (this.mLinearGradient == null) {
            this.mLinearGradient = new LinearGradient(0.0f, 0.0f, 0.0f, 2250.0f, new int[]{Color.parseColor("#ff6e02"), Color.parseColor("#ffff00"), Color.parseColor("#ff6e02")}, (float[]) null, Shader.TileMode.REPEAT);
        }
        for (int i = 0; i < 9; i++) {
            this.mDataList.mLeftPath.add(new Path());
            this.mDataList.mRightPath.add(new Path());
            Paint topPaint = new Paint(1);
            topPaint.setStyle(Paint.Style.STROKE);
            topPaint.setStrokeWidth(2.0f);
            topPaint.setColor(-1);
            this.mDataList.mPaint.add(topPaint);
        }
        this.mPaint = new Paint(1);
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeWidth(2.0f);
        this.mPaint.setColor(-1);
        InputStream inLightHigh = null;
        InputStream inLightBlue = null;
        InputStream inStar = null;
        try {
            inLightHigh = getResources().openRawResource(R.drawable.light_high);
            inLightBlue = getResources().openRawResource(R.drawable.light_blue);
            inStar = getResources().openRawResource(R.drawable.star);
            Bitmap unused = this.mLightHigh.mBitmap = BitmapFactory.decodeStream(inLightHigh);
            Bitmap unused2 = this.mLightBlue.mBitmap = BitmapFactory.decodeStream(inLightBlue);
            Matrix unused3 = this.mLightHigh.mMatrix = new Matrix();
            Matrix unused4 = this.mLightBlue.mMatrix = new Matrix();
            this.mLightHigh.mMatrix.setScale(1.0f, 1.0f);
            this.mLightBlue.mMatrix.setScale(1.0f, 1.0f);
            if (inLightHigh != null) {
                try {
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            for (int i2 = 0; i2 < 100; i2++) {
                this.mStarArr[i2] = new Star(getContext());
            }
            setVisibility(8);
        } finally {
            if (inLightHigh != null) {
                try {
                    inLightHigh.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
            if (inLightBlue != null) {
                inLightBlue.close();
            }
            if (inStar != null) {
                inStar.close();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Canvas canvas2 = canvas;
        int width = getWidth();
        for (int i = 0; i < 9; i++) {
            this.mDataList.mLeftPath.get(i).reset();
            this.mDataList.mLeftPath.get(i).moveTo(LEFT_PENDDING, (float) this.mDataList.mConfig.y);
            this.mDataList.mRightPath.get(i).reset();
            this.mDataList.mRightPath.get(i).moveTo(((float) width) - RIGHT_PENDDING, (float) this.mDataList.mConfig.y);
        }
        for (int index = 0; index <= this.mDataList.mConfig.waveWidth; index++) {
            for (int i2 = 0; i2 < 9; i2++) {
                float waveRate = ((float) (this.mDataList.mConfig.waveHeight - (i2 * 8))) / ((float) this.mDataList.mConfig.waveHeight);
                float waveAlpha = (float) (((double) (((this.mAlphaTarX / 100.0f) * 1.5f) - Math.abs(waveRate))) - 0.3d);
                float endX = (((((float) Math.sin((((double) (((((float) index) - ((float) this.mDataList.mConfig.waveCenter)) / (((float) this.mDataList.mConfig.waveWidth) / 360.0f)) + 90.0f)) * 3.141592653589793d) / 180.0d)) + 1.0f) * ((float) this.mDataList.mConfig.waveHeight)) * waveRate) / 2.0f;
                this.mDataList.mLeftPath.get(i2).lineTo(endX + LEFT_PENDDING, (float) (this.mDataList.mConfig.y + index));
                this.mDataList.mRightPath.get(i2).lineTo((((float) width) - endX) - RIGHT_PENDDING, (float) (this.mDataList.mConfig.y + index));
                if (waveAlpha < 0.0f) {
                    waveAlpha = 0.0f;
                } else if (waveAlpha > 1.0f) {
                    waveAlpha = 1.0f;
                }
                this.mDataList.mPaint.get(i2).setAlpha((int) (255.0f * waveAlpha));
            }
        }
        for (int i3 = 0; i3 < 9; i3++) {
            canvas2.drawPath(this.mDataList.mLeftPath.get(i3), this.mDataList.mPaint.get(i3));
            canvas2.drawPath(this.mDataList.mRightPath.get(i3), this.mDataList.mPaint.get(i3));
        }
        this.mLightHigh.mMatrix.setScale((float) this.mLightHigh.mScaleX, (float) this.mLightHigh.mScaleY);
        Bitmap bitmapHigh = Bitmap.createBitmap(this.mLightHigh.mBitmap, 0, 0, this.mLightHigh.mBitmap.getWidth(), this.mLightHigh.mBitmap.getHeight(), this.mLightHigh.mMatrix, true);
        this.mLightBlue.mMatrix.setScale((float) this.mLightBlue.mScaleX, (float) this.mLightBlue.mScaleY);
        Bitmap bitmapBlue = Bitmap.createBitmap(this.mLightBlue.mBitmap, 0, 0, this.mLightBlue.mBitmap.getWidth(), this.mLightBlue.mBitmap.getHeight(), this.mLightBlue.mMatrix, true);
        if (this.mLightHigh.mAlpha > 1.0d) {
            this.mLightHigh.mAlpha = 1.0d;
        } else if (this.mLightHigh.mAlpha < 0.0d) {
            this.mLightHigh.mAlpha = 0.0d;
        }
        this.mPaint.setAlpha((int) (this.mLightHigh.mAlpha * 255.0d));
        int top = (int) ((((double) getHeight()) - (((double) this.mLightHigh.mBitmap.getHeight()) * this.mLightHigh.mScaleY)) / 2.0d);
        canvas2.drawBitmap(bitmapHigh, (float) ((int) (708.0d - ((((double) this.mLightHigh.mBitmap.getWidth()) * this.mLightHigh.mScaleX) / 2.0d))), (float) top, this.mPaint);
        canvas2.drawBitmap(bitmapHigh, (float) ((int) (((double) (((float) getWidth()) - RIGHT_PENDDING)) - ((((double) this.mLightHigh.mBitmap.getWidth()) * this.mLightHigh.mScaleX) / 2.0d))), (float) top, this.mPaint);
        if (this.mLightBlue.mAlpha > 1.0d) {
            this.mLightBlue.mAlpha = 1.0d;
        } else if (this.mLightBlue.mAlpha < 0.0d) {
            this.mLightBlue.mAlpha = 0.0d;
        }
        this.mPaint.setAlpha((int) (this.mLightBlue.mAlpha * 255.0d));
        int top2 = (int) ((((double) getHeight()) - (((double) this.mLightBlue.mBitmap.getHeight()) * this.mLightBlue.mScaleY)) / 2.0d);
        int leftLeft = (int) (((double) (((float) getWidth()) - RIGHT_PENDDING)) - ((((double) this.mLightBlue.mBitmap.getWidth()) * this.mLightBlue.mScaleX) / 2.0d));
        canvas2.drawBitmap(bitmapBlue, (float) ((int) (708.0d - ((((double) this.mLightBlue.mBitmap.getWidth()) * this.mLightBlue.mScaleX) / 2.0d))), (float) top2, this.mPaint);
        canvas2.drawBitmap(bitmapBlue, (float) leftLeft, (float) top2, this.mPaint);
        Star[] starArr = this.mStarArr;
        int length = starArr.length;
        char c = 0;
        int i4 = 0;
        while (i4 < length) {
            Star star = starArr[i4];
            this.mPaint.setAlpha((int) star.prop[2]);
            canvas2.drawBitmap(star.mStarBitmap, star.prop[c] + LEFT_PENDDING, star.prop[1] + ((float) (getHeight() / 2)), this.mPaint);
            canvas2.drawBitmap(star.mStarBitmap, (((float) getWidth()) - RIGHT_PENDDING) - star.prop[0], star.prop[1] + ((float) (getHeight() / 2)), this.mPaint);
            i4++;
            width = width;
            c = 0;
        }
    }

    public void startAnimation() {
        setVisibility(0);
        SpringForce springForce = new SpringForce(0.0f);
        springForce.setDampingRatio(0.98f);
        springForce.setStiffness(12.18f);
        this.mSpringAnimation = new SpringAnimation(new FloatValueHolder()).setStartVelocity(2000.0f).setStartValue(0.0f).setSpring(springForce);
        this.mSpringAnimation.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
            public void onAnimationUpdate(DynamicAnimation dynamicAnimation, float v, float v1) {
                float unused = MessageView.this.mCtrlTarX = v;
                MessageView messageView = MessageView.this;
                float unused2 = messageView.mAlphaTarX = (float) Math.max(Math.min(((double) messageView.mCtrlTarX) / 2.0d, 100.0d), 0.0d);
                float per = MessageView.this.mAlphaTarX / 100.0f;
                MessageView.this.mLightHigh.mScaleX = (((double) per) * 0.2d) + 0.8d;
                MessageView.this.mLightHigh.mScaleY = (((double) per) * 0.4d) + 0.6d;
                MessageView.this.mLightHigh.mAlpha = Math.min(((1.0d - Math.pow((double) (1.0f - per), 3.0d)) * 1.5d) - 0.2d, 2.0d);
                MessageView.this.mLightBlue.mScaleX = (((double) per) * 0.8d) + 0.2d;
                MessageView.this.mLightBlue.mScaleY = (((double) per) * 0.4d) + 0.6d;
                MessageView.this.mLightBlue.mAlpha = (1.0d - (Math.pow((double) (1.0f - per), 2.0d) * 1.1d)) - 0.1d;
                MessageView.this.invalidate();
            }
        });
        this.mSpringAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
            public void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean b, float v, float v1) {
                for (Star star : MessageView.this.mStarArr) {
                    star.stop();
                }
                MessageView.this.setVisibility(8);
            }
        });
        for (Star star : this.mStarArr) {
            star.boom();
        }
        this.mSpringAnimation.start();
    }

    public boolean isAnimationRunning() {
        SpringAnimation springAnimation = this.mSpringAnimation;
        if (springAnimation != null) {
            return springAnimation.isRunning();
        }
        return false;
    }

    public void stopAnimation() {
        for (Star star : this.mStarArr) {
            if (star != null) {
                star.stop();
            }
        }
        SpringAnimation springAnimation = this.mSpringAnimation;
        if (springAnimation != null) {
            springAnimation.skipToEnd();
        }
        setVisibility(8);
    }

    class WaveConfig {
        public static final int EDGE_X = 700;
        public int acc = 120;
        public int waveCenter = 425;
        public int waveHeight = 32;
        public int waveWidth = 850;
        public int width = 2250;
        public int x = 0;
        public int y = 700;

        WaveConfig() {
        }
    }

    class LineData {
        public WaveConfig mConfig = new WaveConfig();
        public List<Path> mLeftPath = new ArrayList();
        public List<Paint> mPaint = new ArrayList();
        public List<Path> mRightPath = new ArrayList();

        public LineData() {
        }
    }

    class PictureData {
        public double mAlpha;
        /* access modifiers changed from: private */
        public Bitmap mBitmap;
        /* access modifiers changed from: private */
        public Matrix mMatrix;
        public double mScaleX = 1.0d;
        public double mScaleY = 1.0d;

        PictureData() {
        }
    }

    class Star {
        private Context mContext;
        /* access modifiers changed from: private */
        public Matrix mMatrix;
        /* access modifiers changed from: private */
        public Bitmap mStarBitmap;
        /* access modifiers changed from: private */
        public Bitmap mStarBitmapOri;
        private ValueAnimator mValueAnimator;
        /* access modifiers changed from: private */
        public float[] prop = new float[5];

        @SuppressLint({"ResourceType"})
        public Star(Context context) {
            this.mContext = context;
            InputStream inStar = null;
            InputStream inStarOri = null;
            try {
                inStar = this.mContext.getResources().openRawResource(R.drawable.star);
                this.mStarBitmap = BitmapFactory.decodeStream(inStar);
                inStarOri = this.mContext.getResources().openRawResource(R.drawable.star);
                this.mStarBitmapOri = BitmapFactory.decodeStream(inStarOri);
                this.mMatrix = new Matrix();
                this.mMatrix.setScale(1.0f, 1.0f);
                if (inStar != null) {
                    try {
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            } finally {
                if (inStar != null) {
                    try {
                        inStar.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
                if (inStarOri != null) {
                    inStarOri.close();
                }
            }
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v3, resolved type: java.lang.Object[]} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void boom() {
            /*
                r22 = this;
                r0 = r22
                double r1 = java.lang.Math.random()
                r3 = 4602678819172646912(0x3fe0000000000000, double:0.5)
                double r1 = r1 - r3
                r5 = 4649544402794971136(0x4086800000000000, double:720.0)
                double r1 = r1 * r5
                float r1 = (float) r1
                r2 = 1092616192(0x41200000, float:10.0)
                float r2 = r1 / r2
                double r5 = (double) r2
                double r7 = java.lang.Math.random()
                double r7 = r7 - r3
                r9 = 4633641066610819072(0x404e000000000000, double:60.0)
                double r7 = r7 * r9
                double r5 = r5 + r7
                float r2 = (float) r5
                double r5 = java.lang.Math.random()
                r7 = 4639481672377565184(0x4062c00000000000, double:150.0)
                double r5 = r5 * r7
                r7 = 4632233691727265792(0x4049000000000000, double:50.0)
                double r5 = r5 + r7
                float r5 = (float) r5
                double r6 = java.lang.Math.random()
                double r6 = r6 * r3
                double r6 = r6 + r3
                float r6 = (float) r6
                r7 = 1128792064(0x43480000, float:200.0)
                float r7 = r5 / r7
                double r7 = (double) r7
                double r9 = java.lang.Math.random()
                r11 = 4617315517961601024(0x4014000000000000, double:5.0)
                double r9 = r9 * r11
                double r7 = r7 + r9
                r9 = 4652007308841189376(0x408f400000000000, double:1000.0)
                double r7 = r7 * r9
                int r7 = (int) r7
                r8 = 5
                float[] r9 = new float[r8]
                r10 = 0
                r11 = 0
                r9[r11] = r10
                r12 = 1
                r9[r12] = r1
                r13 = 2
                r14 = 1132396544(0x437f0000, float:255.0)
                r9[r13] = r14
                r14 = 3
                r9[r14] = r6
                r15 = 4
                r9[r15] = r6
                double r16 = java.lang.Math.random()
                int r3 = (r16 > r3 ? 1 : (r16 == r3 ? 0 : -1))
                if (r3 <= 0) goto L_0x0067
                r3 = r12
                goto L_0x0068
            L_0x0067:
                r3 = -1
            L_0x0068:
                double r14 = (double) r3
                double r12 = (double) r2
                r18 = 4614256656552045848(0x400921fb54442d18, double:3.141592653589793)
                double r12 = r12 * r18
                r20 = 4640537203540230144(0x4066800000000000, double:180.0)
                double r12 = r12 / r20
                double r12 = java.lang.Math.cos(r12)
                double r14 = r14 * r12
                double r12 = (double) r5
                double r14 = r14 * r12
                float r12 = (float) r14
                double r13 = (double) r1
                double r3 = (double) r2
                double r3 = r3 * r18
                double r3 = r3 / r20
                double r3 = java.lang.Math.sin(r3)
                double r10 = (double) r5
                double r3 = r3 * r10
                double r13 = r13 + r3
                float r4 = (float) r13
                float[] r8 = new float[r8]
                r3 = 0
                r8[r3] = r12
                r3 = 1
                r8[r3] = r4
                r10 = 0
                r11 = 2
                r8[r11] = r10
                r10 = 1073741824(0x40000000, float:2.0)
                float r13 = r6 / r10
                r14 = 3
                r8[r14] = r13
                float r10 = r6 / r10
                r13 = 4
                r8[r13] = r10
                android.animation.FloatArrayEvaluator r10 = new android.animation.FloatArrayEvaluator
                r10.<init>()
                java.lang.Object[] r11 = new java.lang.Object[r11]
                r13 = 0
                r11[r13] = r9
                r3 = 1
                r11[r3] = r8
                android.animation.ValueAnimator r10 = android.animation.ValueAnimator.ofObject(r10, r11)
                r0.mValueAnimator = r10
                android.animation.ValueAnimator r10 = r0.mValueAnimator
                long r13 = (long) r7
                r10.setDuration(r13)
                android.animation.ValueAnimator r10 = r0.mValueAnimator
                com.android.server.lights.interpolater.ExpoEaseOutInterpolater r11 = new com.android.server.lights.interpolater.ExpoEaseOutInterpolater
                r11.<init>()
                r10.setInterpolator(r11)
                android.animation.ValueAnimator r10 = r0.mValueAnimator
                r11 = -1
                r10.setRepeatCount(r11)
                android.animation.ValueAnimator r10 = r0.mValueAnimator
                r3 = 1
                r10.setRepeatCount(r3)
                android.animation.ValueAnimator r3 = r0.mValueAnimator
                com.android.server.lights.view.MessageView$Star$1 r10 = new com.android.server.lights.view.MessageView$Star$1
                r10.<init>()
                r3.addUpdateListener(r10)
                android.animation.ValueAnimator r3 = r0.mValueAnimator
                boolean r3 = r3.isRunning()
                if (r3 == 0) goto L_0x00ec
                android.animation.ValueAnimator r3 = r0.mValueAnimator
                r3.cancel()
            L_0x00ec:
                android.animation.ValueAnimator r3 = r0.mValueAnimator
                r3.start()
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.lights.view.MessageView.Star.boom():void");
        }

        public void stop() {
            this.mValueAnimator.cancel();
        }
    }
}
