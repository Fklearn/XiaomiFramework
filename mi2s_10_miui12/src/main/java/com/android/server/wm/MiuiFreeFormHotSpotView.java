package com.android.server.wm;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.MiuiMultiWindowUtils;
import android.util.Slog;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;

public class MiuiFreeFormHotSpotView extends View {
    private static boolean DEBUG = MiuiFreeFormGestureController.DEBUG;
    public static final int FREEFORM_ANIMATION_IN = 0;
    public static final int FREEFORM_ANIMATION_OUT = 1;
    private static final float FREEFORM_HOTSPOT_FREEFROM_COLOR = 206.0f;
    private static final int FREEFORM_HOTSPOT_MAX_RADIUS = ((int) TypedValue.applyDimension(1, 94.55f, Resources.getSystem().getDisplayMetrics()));
    private static final int FREEFORM_HOTSPOT_MIN_RADIUS = ((int) TypedValue.applyDimension(1, 72.73f, Resources.getSystem().getDisplayMetrics()));
    private static final float FREEFORM_HOTSPOT_NIGHT_FREEFROM_COLOR = 171.0f;
    private static final float FREEFORM_HOTSPOT_NIGHT_SMALL_FREEFROM_COLOR = 79.0f;
    private static final float FREEFORM_HOTSPOT_SMALL_FREEFROM_COLOR = 92.0f;
    public static final int SMALL_FREEFORM_ANIMATION_IN = 2;
    public static final int SMALL_FREEFORM_ANIMATION_OUT = 3;
    private static final String TAG = "HotSpotView";
    private Context mContext;
    float mCurrentAlpha;
    float mCurrentColor;
    int mCurrentHotSpot;
    float mCurrentRaduis;
    /* access modifiers changed from: private */
    public ValueAnimator mInAnimator;
    float mMaxColor;
    float mMinColor;
    /* access modifiers changed from: private */
    public ValueAnimator mOutAnimator;
    private Paint mPaint;
    /* access modifiers changed from: private */
    public ValueAnimator mSmallInAnimator;
    /* access modifiers changed from: private */
    public ValueAnimator mSmallOutAnimator;

    public MiuiFreeFormHotSpotView(Context context) {
        this(context, (AttributeSet) null);
    }

    public MiuiFreeFormHotSpotView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        setBackgroundColor(0);
        this.mPaint = new Paint();
        this.mCurrentHotSpot = -1;
        this.mCurrentAlpha = 0.0f;
        this.mCurrentRaduis = (float) FREEFORM_HOTSPOT_MIN_RADIUS;
        if (MiuiMultiWindowUtils.isNightMode(this.mContext)) {
            this.mMaxColor = FREEFORM_HOTSPOT_NIGHT_FREEFROM_COLOR;
            this.mMinColor = FREEFORM_HOTSPOT_NIGHT_SMALL_FREEFROM_COLOR;
        } else {
            this.mMaxColor = FREEFORM_HOTSPOT_FREEFROM_COLOR;
            this.mMinColor = FREEFORM_HOTSPOT_SMALL_FREEFROM_COLOR;
        }
        this.mCurrentColor = this.mMaxColor;
        Log.i(TAG, "MiuiFreeFormHotSpotView() Context:" + context);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = this.mPaint;
        float f = this.mCurrentColor;
        paint.setColor(Color.valueOf(f / 255.0f, f / 255.0f, f / 255.0f, this.mCurrentAlpha).toArgb());
        int i = this.mCurrentHotSpot;
        if (i == 1) {
            canvas.drawCircle(0.0f, 0.0f, this.mCurrentRaduis, this.mPaint);
        } else if (i == 2) {
            canvas.drawCircle((float) getDisplayWidth(), 0.0f, this.mCurrentRaduis, this.mPaint);
        } else if (i == 3) {
            canvas.drawCircle(0.0f, (float) getDisplayHeight(), this.mCurrentRaduis, this.mPaint);
        } else if (i == 4) {
            canvas.drawCircle((float) getDisplayWidth(), (float) getDisplayHeight(), this.mCurrentRaduis, this.mPaint);
        }
        Slog.d(TAG, " mCurrentHotSpot: " + this.mCurrentHotSpot + "mCurrentRaduis" + this.mCurrentRaduis + " mCurrentColor:" + this.mCurrentColor + " mCurrentAlpha:" + this.mCurrentAlpha + "getDisplayWidth(): " + getDisplayWidth() + " getDisplayHeight():" + getDisplayHeight());
    }

    public void startAnimating(final int type) {
        if (type == 0) {
            this.mInAnimator = ValueAnimator.ofFloat(new float[]{this.mCurrentAlpha, 0.4f});
            this.mInAnimator.setDuration(300);
            this.mInAnimator.setInterpolator(new MiuiMultiWindowUtils.QuadraticEaseOutInterpolator());
            this.mInAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    try {
                        MiuiFreeFormHotSpotView.this.mCurrentAlpha = ((Float) MiuiFreeFormHotSpotView.this.mInAnimator.getAnimatedValue()).floatValue();
                        MiuiFreeFormHotSpotView.this.invalidate();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            this.mInAnimator.addListener(new Animator.AnimatorListener() {
                public void onAnimationStart(Animator animation) {
                    if (type == 0) {
                        Log.d(MiuiFreeFormHotSpotView.TAG, "setVisibility(VISIBLE)");
                        MiuiFreeFormHotSpotView.this.setVisibility(0);
                    }
                }

                public void onAnimationEnd(Animator animation) {
                }

                public void onAnimationCancel(Animator animation) {
                }

                public void onAnimationRepeat(Animator animation) {
                }
            });
            ValueAnimator valueAnimator = this.mOutAnimator;
            if (valueAnimator != null && valueAnimator.isStarted()) {
                this.mOutAnimator.cancel();
            }
            this.mInAnimator.start();
        } else if (type == 1) {
            this.mOutAnimator = ValueAnimator.ofFloat(new float[]{this.mCurrentAlpha, 0.0f});
            this.mOutAnimator.setDuration(300);
            this.mOutAnimator.setInterpolator(new MiuiMultiWindowUtils.QuadraticEaseOutInterpolator());
            this.mOutAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    try {
                        MiuiFreeFormHotSpotView.this.mCurrentAlpha = ((Float) MiuiFreeFormHotSpotView.this.mOutAnimator.getAnimatedValue()).floatValue();
                        MiuiFreeFormHotSpotView.this.invalidate();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            this.mOutAnimator.addListener(new Animator.AnimatorListener() {
                public void onAnimationStart(Animator animation) {
                }

                public void onAnimationEnd(Animator animation) {
                    if (type == 1) {
                        Log.d(MiuiFreeFormHotSpotView.TAG, "setVisibility(GONE)");
                        MiuiFreeFormHotSpotView.this.setVisibility(8);
                    }
                }

                public void onAnimationCancel(Animator animation) {
                }

                public void onAnimationRepeat(Animator animation) {
                }
            });
            ValueAnimator valueAnimator2 = this.mInAnimator;
            if (valueAnimator2 != null && valueAnimator2.isStarted()) {
                this.mInAnimator.cancel();
            }
            this.mOutAnimator.start();
        } else if (type == 2) {
            this.mSmallInAnimator = ValueAnimator.ofFloat(new float[]{this.mCurrentColor, this.mMinColor});
            this.mSmallInAnimator.setDuration(300);
            this.mSmallInAnimator.setInterpolator(new MiuiMultiWindowUtils.QuadraticEaseOutInterpolator());
            this.mSmallInAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    try {
                        MiuiFreeFormHotSpotView.this.mCurrentColor = ((Float) MiuiFreeFormHotSpotView.this.mSmallInAnimator.getAnimatedValue()).floatValue();
                        Log.d(MiuiFreeFormHotSpotView.TAG, "SMALL_FREEFORM_ANIMATION_IN mCurrentColor: " + MiuiFreeFormHotSpotView.this.mCurrentColor);
                        MiuiFreeFormHotSpotView.this.invalidate();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            ValueAnimator valueAnimator3 = this.mSmallOutAnimator;
            if (valueAnimator3 != null && valueAnimator3.isStarted()) {
                this.mSmallOutAnimator.cancel();
            }
            this.mSmallInAnimator.start();
        } else if (type == 3) {
            this.mSmallOutAnimator = ValueAnimator.ofFloat(new float[]{this.mCurrentColor, this.mMaxColor});
            this.mSmallOutAnimator.setDuration(300);
            this.mSmallOutAnimator.setInterpolator(new MiuiMultiWindowUtils.QuadraticEaseOutInterpolator());
            this.mSmallOutAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    try {
                        MiuiFreeFormHotSpotView.this.mCurrentColor = ((Float) MiuiFreeFormHotSpotView.this.mSmallOutAnimator.getAnimatedValue()).floatValue();
                        Log.d(MiuiFreeFormHotSpotView.TAG, "SMALL_FREEFORM_ANIMATION_OUT mCurrentColor: " + MiuiFreeFormHotSpotView.this.mCurrentColor);
                        MiuiFreeFormHotSpotView.this.invalidate();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            ValueAnimator valueAnimator4 = this.mSmallInAnimator;
            if (valueAnimator4 != null && valueAnimator4.isStarted()) {
                this.mSmallInAnimator.cancel();
            }
            this.mSmallOutAnimator.start();
        }
    }

    public void inHotSpotArea(int hotSpotNum, float x, float y) {
        this.mCurrentHotSpot = hotSpotNum;
        culcalateRadiusAndColor(hotSpotNum, x, y);
    }

    private void culcalateRadiusAndColor(int hotSpotNum, float x, float y) {
        float f;
        double d;
        float f2;
        double d2;
        float f3;
        double d3;
        float f4;
        double d4;
        float maxDistance = MiuiMultiWindowUtils.FREEFORM_HOTSPOT_REMINDER_PORTRAIT_RADIUS + MiuiMultiWindowUtils.FREEFORM_HOTSPOT_REMINDER_PORTRAIT_VERTICAL_TOP_MARGIN;
        double currentDistance = (double) (MiuiMultiWindowUtils.FREEFORM_HOTSPOT_REMINDER_PORTRAIT_RADIUS + MiuiMultiWindowUtils.FREEFORM_HOTSPOT_REMINDER_PORTRAIT_VERTICAL_TOP_MARGIN);
        int i = this.mCurrentHotSpot;
        if (i == 1) {
            boolean isPortrait = isPortrait();
            if (isPortrait) {
                f = (MiuiMultiWindowUtils.FREEFORM_HOTSPOT_REMINDER_PORTRAIT_RADIUS + MiuiMultiWindowUtils.FREEFORM_HOTSPOT_REMINDER_PORTRAIT_VERTICAL_TOP_MARGIN) - MiuiMultiWindowUtils.FREEFORM_HOTSPOT_TRIGGER_PORTRAIT_RADIUS;
            } else {
                f = (MiuiMultiWindowUtils.FREEFORM_HOTSPOT_REMINDER_LANDCAPE_RADIUS + MiuiMultiWindowUtils.FREEFORM_HOTSPOT_REMINDER_LANDCAPE_HORIZONTAL_TOP_MARGIN) - MiuiMultiWindowUtils.FREEFORM_HOTSPOT_TRIGGER_LANDCAPE_RADIUS;
            }
            maxDistance = f;
            if (isPortrait) {
                d = MiuiMultiWindowUtils.getDistance(x, y, 0.0f, 0.0f) - ((double) MiuiMultiWindowUtils.FREEFORM_HOTSPOT_TRIGGER_PORTRAIT_RADIUS);
            } else {
                d = MiuiMultiWindowUtils.getDistance(x, y, 0.0f, 0.0f) - ((double) MiuiMultiWindowUtils.FREEFORM_HOTSPOT_TRIGGER_LANDCAPE_RADIUS);
            }
            currentDistance = d;
        } else if (i == 2) {
            boolean isPortrait2 = isPortrait();
            if (isPortrait2) {
                f2 = (MiuiMultiWindowUtils.FREEFORM_HOTSPOT_REMINDER_PORTRAIT_RADIUS + MiuiMultiWindowUtils.FREEFORM_HOTSPOT_REMINDER_PORTRAIT_VERTICAL_TOP_MARGIN) - MiuiMultiWindowUtils.FREEFORM_HOTSPOT_TRIGGER_PORTRAIT_RADIUS;
            } else {
                f2 = (MiuiMultiWindowUtils.FREEFORM_HOTSPOT_REMINDER_LANDCAPE_RADIUS + MiuiMultiWindowUtils.FREEFORM_HOTSPOT_REMINDER_LANDCAPE_HORIZONTAL_TOP_MARGIN) - MiuiMultiWindowUtils.FREEFORM_HOTSPOT_TRIGGER_LANDCAPE_RADIUS;
            }
            maxDistance = f2;
            if (isPortrait2) {
                d2 = MiuiMultiWindowUtils.getDistance(x, y, (float) getDisplayWidth(), 0.0f) - ((double) MiuiMultiWindowUtils.FREEFORM_HOTSPOT_TRIGGER_PORTRAIT_RADIUS);
            } else {
                d2 = MiuiMultiWindowUtils.getDistance(x, y, (float) getDisplayWidth(), 0.0f) - ((double) MiuiMultiWindowUtils.FREEFORM_HOTSPOT_TRIGGER_LANDCAPE_RADIUS);
            }
            currentDistance = d2;
        } else if (i == 3) {
            boolean isPortrait3 = isPortrait();
            if (isPortrait3) {
                f3 = (MiuiMultiWindowUtils.FREEFORM_HOTSPOT_REMINDER_PORTRAIT_RADIUS + MiuiMultiWindowUtils.FREEFORM_HOTSPOT_REMINDER_PORTRAIT_VERTICAL_BOTTOM_MARGIN) - MiuiMultiWindowUtils.FREEFORM_HOTSPOT_TRIGGER_PORTRAIT_RADIUS;
            } else {
                f3 = (MiuiMultiWindowUtils.FREEFORM_HOTSPOT_REMINDER_LANDCAPE_RADIUS + MiuiMultiWindowUtils.FREEFORM_HOTSPOT_REMINDER_LANDCAPE_HORIZONTAL_BOTTOM_MARGIN) - MiuiMultiWindowUtils.FREEFORM_HOTSPOT_TRIGGER_LANDCAPE_RADIUS;
            }
            maxDistance = f3;
            if (isPortrait3) {
                d3 = MiuiMultiWindowUtils.getDistance(x, y, 0.0f, (float) getDisplayHeight()) - ((double) MiuiMultiWindowUtils.FREEFORM_HOTSPOT_TRIGGER_PORTRAIT_RADIUS);
            } else {
                d3 = MiuiMultiWindowUtils.getDistance(x, y, 0.0f, (float) getDisplayHeight()) - ((double) MiuiMultiWindowUtils.FREEFORM_HOTSPOT_TRIGGER_LANDCAPE_RADIUS);
            }
            currentDistance = d3;
        } else if (i == 4) {
            boolean isPortrait4 = isPortrait();
            if (isPortrait4) {
                f4 = (MiuiMultiWindowUtils.FREEFORM_HOTSPOT_REMINDER_PORTRAIT_RADIUS + MiuiMultiWindowUtils.FREEFORM_HOTSPOT_REMINDER_PORTRAIT_VERTICAL_BOTTOM_MARGIN) - MiuiMultiWindowUtils.FREEFORM_HOTSPOT_TRIGGER_PORTRAIT_RADIUS;
            } else {
                f4 = (MiuiMultiWindowUtils.FREEFORM_HOTSPOT_REMINDER_LANDCAPE_RADIUS + MiuiMultiWindowUtils.FREEFORM_HOTSPOT_REMINDER_LANDCAPE_HORIZONTAL_BOTTOM_MARGIN) - MiuiMultiWindowUtils.FREEFORM_HOTSPOT_TRIGGER_LANDCAPE_RADIUS;
            }
            maxDistance = f4;
            if (isPortrait4) {
                d4 = MiuiMultiWindowUtils.getDistance(x, y, (float) getDisplayWidth(), (float) getDisplayHeight()) - ((double) MiuiMultiWindowUtils.FREEFORM_HOTSPOT_TRIGGER_PORTRAIT_RADIUS);
            } else {
                d4 = MiuiMultiWindowUtils.getDistance(x, y, (float) getDisplayWidth(), (float) getDisplayHeight()) - ((double) MiuiMultiWindowUtils.FREEFORM_HOTSPOT_TRIGGER_LANDCAPE_RADIUS);
            }
            currentDistance = d4;
        }
        this.mCurrentRaduis = ((float) FREEFORM_HOTSPOT_MAX_RADIUS) - ((((float) currentDistance) / maxDistance) * (MiuiMultiWindowUtils.FREEFORM_HOTSPOT_REMINDER_PORTRAIT_RADIUS - MiuiMultiWindowUtils.FREEFORM_HOTSPOT_REMINDER_LANDCAPE_RADIUS));
        invalidate();
        Log.d(TAG, " hotSpotNum: " + hotSpotNum + " x: " + x + " y: " + y + "mCurrentRaduis" + this.mCurrentRaduis + " mCurrentColor:" + this.mCurrentColor);
    }

    public void enterSmallWindow() {
        Log.d(TAG, "enterSmallWindow");
        startAnimating(2);
    }

    public void outSmallWindow() {
        Log.d(TAG, "outSmallWindow");
        startAnimating(3);
    }

    public int getDisplayHeight() {
        DisplayMetrics outMetrics = new DisplayMetrics();
        ((WindowManager) this.mContext.getSystemService("window")).getDefaultDisplay().getRealMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    public int getDisplayWidth() {
        DisplayMetrics outMetrics = new DisplayMetrics();
        ((WindowManager) this.mContext.getSystemService("window")).getDefaultDisplay().getRealMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    public boolean isPortrait() {
        return getResources().getConfiguration().orientation == 1;
    }

    public void show() {
        Log.d(TAG, "show");
        if (MiuiMultiWindowUtils.isNightMode(this.mContext)) {
            this.mMaxColor = FREEFORM_HOTSPOT_NIGHT_FREEFROM_COLOR;
            this.mMinColor = FREEFORM_HOTSPOT_NIGHT_SMALL_FREEFROM_COLOR;
        } else {
            this.mMaxColor = FREEFORM_HOTSPOT_FREEFROM_COLOR;
            this.mMinColor = FREEFORM_HOTSPOT_SMALL_FREEFROM_COLOR;
        }
        this.mCurrentColor = this.mMaxColor;
        startAnimating(0);
    }

    public void hide() {
        Log.d(TAG, "hide");
        startAnimating(1);
    }
}
