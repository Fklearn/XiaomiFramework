package com.android.server.wm;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.UiModeManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Slog;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import java.util.Collection;
import java.util.HashSet;

public class MiuiFreeFormOverlayView extends FrameLayout {
    private static final String ALPHA = "ALPHA";
    public static final int ANIMATION_ALPHA = 0;
    public static final int ANIMATION_BORDER_HIDE = 3;
    public static final int ANIMATION_BORDER_OPEN = 2;
    public static final int ANIMATION_CONTENT_OPEN = 1;
    private static final String SCALE_HEIGTH = "HEIGHT_SCALE";
    private static final String SCALE_WIDTH = "WIDTH_SCALE";
    private static final String TAG = "OverlayView";
    private static final String TRANSLATE_X = "TRANSLATE_X";
    private static final String TRANSLATE_Y = "TRANSLATE_Y";
    /* access modifiers changed from: private */
    public int ACTION_ALPHA_HIDE = 1;
    public int ACTION_ALPHA_SHOW = 0;
    /* access modifiers changed from: private */
    public int ACTION_UNDEFINED = -1;
    /* access modifiers changed from: private */
    public MiuiFreeFormRoundRectView mBorderView;
    /* access modifiers changed from: private */
    public View mContentView;
    /* access modifiers changed from: private */
    public MiuiFreeFormWindowController mController;
    /* access modifiers changed from: private */
    public Drawable mDraw;
    /* access modifiers changed from: private */
    public ImageView mIcoImageView;
    /* access modifiers changed from: private */
    public Bitmap mIconBmp = null;
    /* access modifiers changed from: private */
    public PackageManager mPackageManager;
    /* access modifiers changed from: private */
    public String mPackageName;
    private Rect mStartBounds = new Rect();

    public MiuiFreeFormOverlayView(Context context) {
        super(context);
        this.mPackageManager = context.getPackageManager();
    }

    public MiuiFreeFormOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mPackageManager = context.getPackageManager();
    }

    public MiuiFreeFormOverlayView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mPackageManager = context.getPackageManager();
    }

    public void setController(MiuiFreeFormWindowController controller) {
        this.mController = controller;
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == 1) {
            this.mController.updateOvleryView(1);
            this.mContentView.setX(0.0f);
            this.mContentView.setY(0.0f);
            this.mContentView.getLayoutParams().width = -1;
            this.mContentView.getLayoutParams().height = -1;
            this.mContentView.requestLayout();
            this.mIcoImageView.setRotation(0.0f);
            invalidate();
            MiuiFreeFormWindowController.DropWindowType = -1;
        } else if (newConfig.orientation == 2 && MiuiFreeFormWindowController.DropWindowType == 0) {
            this.mController.updateOvleryView(2);
            this.mContentView.setX(0.0f);
            this.mContentView.setY(0.0f);
            this.mContentView.getLayoutParams().width = -1;
            this.mContentView.getLayoutParams().height = -1;
            this.mContentView.requestLayout();
            this.mIcoImageView.setRotation(0.0f);
            invalidate();
            MiuiFreeFormWindowController.DropWindowType = -1;
        }
    }

    public void startRemoveOverLayViewAnimation() {
        postDelayed(new Runnable() {
            public void run() {
                ValueAnimator animator = ValueAnimator.ofPropertyValuesHolder(new PropertyValuesHolder[]{PropertyValuesHolder.ofFloat("ALPHA", new float[]{1.0f, 0.0f})});
                animator.setInterpolator(new LinearInterpolator());
                animator.setDuration(200);
                MiuiFreeFormOverlayView miuiFreeFormOverlayView = MiuiFreeFormOverlayView.this;
                animator.addUpdateListener(new AnimatorUpdateListener(animator, 0, miuiFreeFormOverlayView));
                MiuiFreeFormOverlayView miuiFreeFormOverlayView2 = MiuiFreeFormOverlayView.this;
                animator.addListener(new AnimatorListener(0, miuiFreeFormOverlayView2, miuiFreeFormOverlayView2.ACTION_ALPHA_HIDE));
                animator.start();
            }
        }, 600);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mIcoImageView = (ImageView) getChildAt(2);
        boolean isNightMode = true;
        this.mContentView = getChildAt(1);
        UiModeManager uiManager = (UiModeManager) getContext().getSystemService("uimode");
        if (uiManager == null || uiManager.getNightMode() != 2) {
            isNightMode = false;
        }
        if (isNightMode) {
            this.mContentView.setBackgroundColor(Color.parseColor("#FA1F1F1F"));
        } else {
            this.mContentView.setBackgroundColor(Color.parseColor("#FAFAFAFA"));
        }
        this.mBorderView = (MiuiFreeFormRoundRectView) getChildAt(0);
        this.mIcoImageView.setVisibility(4);
        this.mContentView.setVisibility(4);
        this.mBorderView.setVisibility(4);
    }

    public void setStartBounds(Rect contentBounds) {
        Slog.d(TAG, "setStartBounds:" + contentBounds);
        this.mContentView.setX((float) contentBounds.left);
        this.mContentView.setY((float) contentBounds.top);
        this.mContentView.getLayoutParams().width = contentBounds.width();
        this.mContentView.getLayoutParams().height = contentBounds.height();
        this.mContentView.requestLayout();
        this.mStartBounds.set(contentBounds);
    }

    public void startContentAnimation(int animationType, String packageName) {
        startOpenAnimation(animationType, packageName);
    }

    public void startBorderAnimation(boolean appear) {
        float centerY;
        float centerX;
        float nowHeightScale;
        float nowWidthScale;
        setVisibility(0);
        this.mIcoImageView.setVisibility(4);
        this.mContentView.setVisibility(4);
        this.mBorderView.setVisibility(0);
        Rect contentBounds = this.mStartBounds;
        if (this.mController.mGestureController.mGestureListener.mIsPortrait) {
            nowWidthScale = (((float) contentBounds.width()) * 1.0f) / ((float) this.mController.mScreenShortSide);
            nowHeightScale = (((float) contentBounds.height()) * 1.0f) / ((float) this.mController.mScreenLongSide);
            centerX = (float) (this.mController.mScreenShortSide / 2);
            centerY = (float) (this.mController.mScreenLongSide / 2);
        } else {
            nowWidthScale = (((float) contentBounds.width()) * 1.0f) / ((float) this.mController.mScreenLongSide);
            nowHeightScale = (((float) contentBounds.height()) * 1.0f) / ((float) this.mController.mScreenShortSide);
            centerX = (float) (this.mController.mScreenLongSide / 2);
            centerY = (float) (this.mController.mScreenShortSide / 2);
        }
        Collection<Animator> animatorItems = new HashSet<>();
        AnimatorSet animatorSet = new AnimatorSet();
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(SCALE_WIDTH, new float[]{nowWidthScale, 1.0f});
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(SCALE_HEIGTH, new float[]{nowHeightScale, 1.0f});
        PropertyValuesHolder translateX = PropertyValuesHolder.ofFloat("TRANSLATE_X", new float[]{(float) contentBounds.centerX(), centerX});
        PropertyValuesHolder translateY = PropertyValuesHolder.ofFloat("TRANSLATE_Y", new float[]{(float) contentBounds.centerY(), centerY});
        ValueAnimator animator = ValueAnimator.ofPropertyValuesHolder(new PropertyValuesHolder[]{translateX, translateY, scaleX, scaleY, PropertyValuesHolder.ofFloat("ALPHA", new float[]{0.5f, 1.0f})});
        animator.setInterpolator(new DecelerateInterpolator());
        Rect rect = contentBounds;
        animator.setDuration(300);
        animator.addUpdateListener(new AnimatorUpdateListener(animator, 2, this));
        animator.setInterpolator(new LinearInterpolator());
        animatorItems.add(animator);
        animatorSet.playTogether(animatorItems);
        if (appear) {
            PropertyValuesHolder propertyValuesHolder = translateY;
            animatorSet.addListener(new AnimatorListener(2, this, this.ACTION_UNDEFINED));
        } else {
            animatorSet.addListener(new AnimatorListener(3, this, this.ACTION_UNDEFINED));
        }
        if (appear) {
            animatorSet.start();
        } else {
            animatorSet.reverse();
        }
    }

    public void startOpenAnimation(int actionType, String packageName) {
        this.mPackageName = packageName;
        if (actionType == 1) {
            setVisibility(0);
            this.mContentView.setVisibility(0);
            if (this.mController.mGestureController.mGestureListener.mIsPortrait) {
                this.mIcoImageView.setRotation(90.0f);
            } else {
                this.mIcoImageView.setRotation(-90.0f);
            }
            this.mIcoImageView.setVisibility(4);
            ValueAnimator animator = ValueAnimator.ofPropertyValuesHolder(new PropertyValuesHolder[]{PropertyValuesHolder.ofFloat("ALPHA", new float[]{0.0f, 1.0f})});
            animator.setInterpolator(MiuiFreeFormGestureAnimator.QUINT_EASE_OUT_INTERPOLATOR);
            animator.setDuration(200);
            animator.addUpdateListener(new AnimatorUpdateListener(animator, 0, this));
            animator.addListener(new AnimatorListener(0, this, this.ACTION_ALPHA_SHOW));
            animator.start();
        }
    }

    /* access modifiers changed from: private */
    public void startOpenAnimation() {
        float centerY;
        float centerX;
        float nowHeightScale;
        float nowWidthScale;
        Rect contentBounds = this.mStartBounds;
        if (this.mController.mGestureController.mGestureListener.mIsPortrait) {
            nowWidthScale = (((float) contentBounds.width()) * 1.0f) / ((float) this.mController.mScreenShortSide);
            nowHeightScale = (((float) contentBounds.height()) * 1.0f) / ((float) this.mController.mScreenLongSide);
            centerX = (float) (this.mController.mScreenShortSide / 2);
            centerY = (float) (this.mController.mScreenLongSide / 2);
        } else {
            nowWidthScale = (((float) contentBounds.width()) * 1.0f) / ((float) this.mController.mScreenLongSide);
            nowHeightScale = (((float) contentBounds.height()) * 1.0f) / ((float) this.mController.mScreenShortSide);
            centerX = (float) (this.mController.mScreenLongSide / 2);
            centerY = (float) (this.mController.mScreenShortSide / 2);
        }
        Collection<Animator> animatorItems = new HashSet<>();
        AnimatorSet animatorSet = new AnimatorSet();
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(SCALE_WIDTH, new float[]{nowWidthScale, 1.0f});
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(SCALE_HEIGTH, new float[]{nowHeightScale, 1.0f});
        PropertyValuesHolder translateX = PropertyValuesHolder.ofFloat("TRANSLATE_X", new float[]{(float) contentBounds.centerX(), centerX});
        ValueAnimator animator = ValueAnimator.ofPropertyValuesHolder(new PropertyValuesHolder[]{translateX, PropertyValuesHolder.ofFloat("TRANSLATE_Y", new float[]{(float) contentBounds.centerY(), centerY}), scaleX, scaleY});
        animator.setInterpolator(new DecelerateInterpolator());
        PropertyValuesHolder propertyValuesHolder = translateX;
        animator.setDuration(350);
        animator.addUpdateListener(new AnimatorUpdateListener(animator, 1, this.mContentView));
        animator.setInterpolator(MiuiFreeFormGestureAnimator.QUINT_EASE_OUT_INTERPOLATOR);
        animatorItems.add(animator);
        animatorSet.playTogether(animatorItems);
        Rect rect = contentBounds;
        animatorSet.addListener(new AnimatorListener(1, this.mContentView, this.ACTION_UNDEFINED));
        animatorSet.start();
        postDelayed(new Runnable() {
            public void run() {
                ValueAnimator animator = ValueAnimator.ofPropertyValuesHolder(new PropertyValuesHolder[]{PropertyValuesHolder.ofFloat("ALPHA", new float[]{0.0f, 1.0f})});
                animator.setInterpolator(MiuiFreeFormGestureAnimator.QUINT_EASE_OUT_INTERPOLATOR);
                animator.setDuration(175);
                MiuiFreeFormOverlayView miuiFreeFormOverlayView = MiuiFreeFormOverlayView.this;
                animator.addUpdateListener(new AnimatorUpdateListener(animator, 0, miuiFreeFormOverlayView.mIcoImageView));
                MiuiFreeFormOverlayView miuiFreeFormOverlayView2 = MiuiFreeFormOverlayView.this;
                animator.addListener(new AnimatorListener(0, miuiFreeFormOverlayView2.mIcoImageView, MiuiFreeFormOverlayView.this.ACTION_UNDEFINED));
                animator.start();
            }
        }, 175);
    }

    public void hide() {
        setVisibility(8);
    }

    public void show() {
        setVisibility(0);
    }

    private class AnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener {
        private int mAnimationType;
        private ValueAnimator mAnimator;
        private View mView;

        AnimatorUpdateListener(ValueAnimator animator, int animationType, View view) {
            this.mAnimator = animator;
            this.mAnimationType = animationType;
            this.mView = view;
        }

        public void onAnimationUpdate(ValueAnimator animation) {
            float alpha;
            String str;
            int right;
            int top;
            int left;
            int bottom;
            int right2;
            int top2;
            int left2;
            int bottom2;
            int right3;
            int top3;
            int left3;
            ValueAnimator valueAnimator = animation;
            int i = this.mAnimationType;
            if (i == 0) {
                float value = ((Float) valueAnimator.getAnimatedValue("ALPHA")).floatValue();
                if (this.mView instanceof ImageView) {
                    MiuiFreeFormOverlayView.this.mIcoImageView.setAlpha(value);
                } else {
                    MiuiFreeFormOverlayView.this.setAlpha(value);
                }
            } else if (i == 1) {
                float animationTransX = ((Float) valueAnimator.getAnimatedValue("TRANSLATE_X")).floatValue();
                float animationTransY = ((Float) valueAnimator.getAnimatedValue("TRANSLATE_Y")).floatValue();
                float scaleX = ((Float) valueAnimator.getAnimatedValue(MiuiFreeFormOverlayView.SCALE_WIDTH)).floatValue();
                float scaleY = ((Float) valueAnimator.getAnimatedValue(MiuiFreeFormOverlayView.SCALE_HEIGTH)).floatValue();
                if (MiuiFreeFormOverlayView.this.mController.mGestureController.mGestureListener.mIsPortrait) {
                    left3 = (int) (animationTransX - ((((float) MiuiFreeFormOverlayView.this.mController.mScreenShortSide) * scaleX) / 2.0f));
                    top3 = (int) (animationTransY - ((((float) MiuiFreeFormOverlayView.this.mController.mScreenLongSide) * scaleY) / 2.0f));
                    right3 = (int) (((((float) MiuiFreeFormOverlayView.this.mController.mScreenShortSide) * scaleX) / 2.0f) + animationTransX);
                    bottom2 = (int) (((((float) MiuiFreeFormOverlayView.this.mController.mScreenLongSide) * scaleY) / 2.0f) + animationTransY);
                } else {
                    left3 = (int) (animationTransX - ((((float) MiuiFreeFormOverlayView.this.mController.mScreenLongSide) * scaleX) / 2.0f));
                    top3 = (int) (animationTransY - ((((float) MiuiFreeFormOverlayView.this.mController.mScreenShortSide) * scaleY) / 2.0f));
                    right3 = (int) (((((float) MiuiFreeFormOverlayView.this.mController.mScreenLongSide) * scaleX) / 2.0f) + animationTransX);
                    bottom2 = (int) (((((float) MiuiFreeFormOverlayView.this.mController.mScreenShortSide) * scaleY) / 2.0f) + animationTransY);
                }
                Rect bounds = new Rect(left3, top3, right3, bottom2);
                int i2 = left3;
                Slog.d(MiuiFreeFormOverlayView.TAG, "onAnimationUpdate: bounds:" + bounds + "animationTransX:" + animationTransX + " animationTransY:" + animationTransY + " scaleX:" + scaleX + " scaleY:" + scaleY);
                MiuiFreeFormOverlayView.this.mContentView.setX((float) bounds.left);
                MiuiFreeFormOverlayView.this.mContentView.setY((float) bounds.top);
                MiuiFreeFormOverlayView.this.mContentView.getLayoutParams().width = bounds.width();
                MiuiFreeFormOverlayView.this.mContentView.getLayoutParams().height = bounds.height();
                MiuiFreeFormOverlayView.this.mContentView.requestLayout();
            } else if (i == 2) {
                ValueAnimator valueAnimator2 = animation;
                float animationTransX2 = ((Float) valueAnimator2.getAnimatedValue("TRANSLATE_X")).floatValue();
                float animationTransY2 = ((Float) valueAnimator2.getAnimatedValue("TRANSLATE_Y")).floatValue();
                float scaleX2 = ((Float) valueAnimator2.getAnimatedValue(MiuiFreeFormOverlayView.SCALE_WIDTH)).floatValue();
                float scaleY2 = ((Float) valueAnimator2.getAnimatedValue(MiuiFreeFormOverlayView.SCALE_HEIGTH)).floatValue();
                float alpha2 = ((Float) valueAnimator2.getAnimatedValue("ALPHA")).floatValue();
                if (MiuiFreeFormOverlayView.this.mController.mGestureController.mGestureListener.mIsPortrait) {
                    left = (int) (animationTransX2 - ((((float) MiuiFreeFormOverlayView.this.mController.mScreenShortSide) * scaleX2) / 2.0f));
                    top = (int) (animationTransY2 - ((((float) MiuiFreeFormOverlayView.this.mController.mScreenLongSide) * scaleY2) / 2.0f));
                    right = (int) (((((float) MiuiFreeFormOverlayView.this.mController.mScreenShortSide) * scaleX2) / 2.0f) + animationTransX2);
                    bottom = (int) (((((float) MiuiFreeFormOverlayView.this.mController.mScreenLongSide) * scaleY2) / 2.0f) + animationTransY2);
                    str = MiuiFreeFormOverlayView.TAG;
                    alpha = alpha2;
                    if (right > MiuiFreeFormOverlayView.this.mController.mScreenShortSide - 4) {
                        right = MiuiFreeFormOverlayView.this.mController.mScreenShortSide - 4;
                    }
                    if (left < 4) {
                        left = 4;
                    }
                    if (top < 4) {
                        top = 4;
                    }
                    if (bottom > MiuiFreeFormOverlayView.this.mController.mScreenLongSide - 4) {
                        bottom = MiuiFreeFormOverlayView.this.mController.mScreenLongSide - 4;
                    }
                } else {
                    alpha = alpha2;
                    str = MiuiFreeFormOverlayView.TAG;
                    int left4 = (int) (animationTransX2 - ((((float) MiuiFreeFormOverlayView.this.mController.mScreenLongSide) * scaleX2) / 2.0f));
                    int top4 = (int) (animationTransY2 - ((((float) MiuiFreeFormOverlayView.this.mController.mScreenShortSide) * scaleY2) / 2.0f));
                    int right4 = (int) (((((float) MiuiFreeFormOverlayView.this.mController.mScreenLongSide) * scaleX2) / 2.0f) + animationTransX2);
                    int bottom3 = (int) (((((float) MiuiFreeFormOverlayView.this.mController.mScreenShortSide) * scaleY2) / 2.0f) + animationTransY2);
                    if (right4 > MiuiFreeFormOverlayView.this.mController.mScreenLongSide - 4) {
                        right2 = MiuiFreeFormOverlayView.this.mController.mScreenLongSide - 4;
                    } else {
                        right2 = right4;
                    }
                    if (left4 < 4) {
                        left2 = 4;
                    } else {
                        left2 = left4;
                    }
                    if (top4 < 4) {
                        top2 = 4;
                    } else {
                        top2 = top4;
                    }
                    if (bottom3 > MiuiFreeFormOverlayView.this.mController.mScreenShortSide - 4) {
                        right = right2;
                        top = top2;
                        bottom = MiuiFreeFormOverlayView.this.mController.mScreenShortSide - 4;
                    } else {
                        right = right2;
                        top = top2;
                        bottom = bottom3;
                    }
                }
                Rect bounds2 = new Rect(left, top, right, bottom);
                Slog.d(str, "onAnimationUpdate: bounds:" + bounds2 + "animationTransX:" + animationTransX2 + " animationTransY:" + animationTransY2 + " scaleX:" + scaleX2 + " scaleY:" + scaleY2 + " alpha:" + alpha);
                MiuiFreeFormOverlayView.this.mBorderView.setRectBounds(bounds2);
            }
        }
    }

    private class AnimatorListener implements Animator.AnimatorListener {
        private int mAction;
        private int mAnimationType;
        private View mView;

        AnimatorListener(int animationType, View view, int action) {
            this.mAnimationType = animationType;
            this.mView = view;
            this.mAction = action;
        }

        public void onAnimationStart(Animator animator) {
            if (this.mAnimationType == 0) {
                try {
                    Drawable unused = MiuiFreeFormOverlayView.this.mDraw = MiuiFreeFormOverlayView.this.mPackageManager.getApplicationIcon(MiuiFreeFormOverlayView.this.mPackageName);
                    if (MiuiFreeFormOverlayView.this.mDraw instanceof BitmapDrawable) {
                        Bitmap unused2 = MiuiFreeFormOverlayView.this.mIconBmp = ((BitmapDrawable) MiuiFreeFormOverlayView.this.mDraw).getBitmap();
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    Log.w(MiuiFreeFormOverlayView.TAG, "No such package for this name!");
                }
                if (this.mView instanceof ImageView) {
                    MiuiFreeFormOverlayView.this.mIcoImageView.setImageDrawable(MiuiFreeFormOverlayView.this.mDraw);
                    MiuiFreeFormOverlayView.this.mIcoImageView.setVisibility(0);
                }
            }
        }

        public void onAnimationEnd(Animator animator) {
            MiuiFreeFormOverlayView miuiFreeFormOverlayView;
            Slog.d(MiuiFreeFormOverlayView.TAG, "onAnimationEnd mAnimationType:" + this.mAnimationType + " mAction:" + this.mAction + " mView:" + this.mView);
            int i = this.mAnimationType;
            if (i == 0) {
                View view = this.mView;
                if ((view instanceof ImageView) || view != (miuiFreeFormOverlayView = MiuiFreeFormOverlayView.this)) {
                    return;
                }
                if (this.mAction == miuiFreeFormOverlayView.ACTION_ALPHA_SHOW) {
                    MiuiFreeFormOverlayView.this.startOpenAnimation();
                } else if (this.mAction == MiuiFreeFormOverlayView.this.ACTION_ALPHA_HIDE) {
                    MiuiFreeFormOverlayView.this.mController.removeOverlayView();
                    MiuiFreeFormOverlayView.this.mController.setDisableScreenRotation(false);
                }
            } else if (i == 1) {
                MiuiFreeFormOverlayView.this.postDelayed(new Runnable() {
                    public void run() {
                        try {
                            MiuiFreeFormOverlayView.this.mController.mGestureController.mService.mActivityManager.resizeTask(MiuiFreeFormOverlayView.this.mController.mGestureController.mGestureListener.mTaskId, new Rect(0, 0, MiuiFreeFormOverlayView.this.mController.mScreenShortSide, MiuiFreeFormOverlayView.this.mController.mScreenLongSide), 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        MiuiFreeFormOverlayView.this.mController.setDisableScreenRotation(true);
                        MiuiFreeFormOverlayView.this.mController.startShowFullScreenWindow();
                    }
                }, 200);
            } else if (i == 3) {
                MiuiFreeFormOverlayView.this.mBorderView.setVisibility(4);
            }
        }

        public void onAnimationCancel(Animator animator) {
        }

        public void onAnimationRepeat(Animator animator) {
        }
    }

    private static class ScaleXAnimation extends Animation {
        private float mFromX;
        private float mPivotX;
        private float mToX;

        public ScaleXAnimation(float fromX, float toX) {
            this.mFromX = fromX;
            this.mToX = toX;
            this.mPivotX = 0.0f;
        }

        public ScaleXAnimation(float fromX, float toX, float pivotX) {
            this.mFromX = fromX;
            this.mToX = toX;
            this.mPivotX = pivotX;
        }

        /* access modifiers changed from: protected */
        public void applyTransformation(float interpolatedTime, Transformation t) {
            float sx = 1.0f;
            float scale = getScaleFactor();
            if (!(this.mFromX == 1.0f && this.mToX == 1.0f)) {
                float f = this.mFromX;
                sx = f + ((this.mToX - f) * interpolatedTime);
            }
            if (this.mPivotX == 0.0f) {
                t.getMatrix().setScale(sx, 1.0f);
            } else {
                t.getMatrix().setScale(sx, 1.0f, this.mPivotX * scale, 0.0f);
            }
        }
    }

    private static class ScaleYAnimation extends Animation {
        private float mFromY;
        private float mPivotY;
        private float mToY;

        public ScaleYAnimation(float fromY, float toY) {
            this.mFromY = fromY;
            this.mToY = toY;
            this.mPivotY = 0.0f;
        }

        public ScaleYAnimation(float fromY, float toY, float pivotY) {
            this.mFromY = fromY;
            this.mToY = toY;
            this.mPivotY = pivotY;
        }

        /* access modifiers changed from: protected */
        public void applyTransformation(float interpolatedTime, Transformation t) {
            float sy = 1.0f;
            float scale = getScaleFactor();
            if (!(this.mFromY == 1.0f && this.mToY == 1.0f)) {
                float f = this.mFromY;
                sy = f + ((this.mToY - f) * interpolatedTime);
            }
            if (this.mPivotY == 0.0f) {
                t.getMatrix().setScale(1.0f, sy);
            } else {
                t.getMatrix().setScale(1.0f, sy, 0.0f, this.mPivotY * scale);
            }
        }
    }
}
