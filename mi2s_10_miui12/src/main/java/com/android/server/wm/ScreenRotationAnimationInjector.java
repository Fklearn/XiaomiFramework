package com.android.server.wm;

import android.app.ActivityTaskManager;
import android.app.IUiModeManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.util.LruCache;
import android.util.Slog;
import android.view.Surface;
import android.view.SurfaceControl;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import com.android.internal.R;
import com.android.internal.annotations.GuardedBy;
import com.android.server.wm.ActivityStack;

class ScreenRotationAnimationInjector {
    public static final int BLACK_SURFACE_INVALID_POSITION = -10000;
    public static final int BLACK_SURFACE_SIZE_EGE = 500;
    private static final int CACHE_SIZE = 4;
    public static final int COVER_EGE = 800;
    public static final int COVER_OFFSET = 250;
    private static final boolean DEBUG_WINDOW_BACKGROUND = false;
    private static final int DEFAULT_HALF_ROTATION_ANIMATION_DURATION = 250;
    private static final int DEFAULT_PHASE1_SCALE_ANIMATION_DURATION = 167;
    private static final int DEFAULT_PHASE2_SCALE_ANIMATION_DURATION = 333;
    private static final int DEFAULT_ROTATION_ANIMATION_DURATION = 500;
    private static final int GESTURE_LINE_ALPHA_DURATION = 500;
    private static final Interpolator QUART_EASE_OUT_INTERPOLATOR = new EaseQuartOutInterpolator();
    private static final float SCALE_DELAY_OFFSET = 0.33333334f;
    private static final float SCALE_FACTOR = 0.94f;
    static final int SCREEN_FREEZE_LAYER_BASE = 2010000;
    static final int SCREEN_FREEZE_LAYER_SCREENSHOT = 2010001;
    private static final Interpolator SIN_EASE_INOUT_INTERPOLATOR = new EaseSineInOutInterpolator();
    private static final String TAG = "ScreenRotationAnimationInjector";
    public static final int TYPE_APP = 2;
    public static final int TYPE_BACKGROUND = 3;
    public static final int TYPE_SCREEN_SHOT = 1;
    public static final boolean USE_SEAMLESS_ROTATION = SystemProperties.getBoolean("persist.miui.useseamless", true);
    @GuardedBy({"class"})
    private static final LruCache<String, Drawable> mDrawables = new LruCache<>(4);
    public static float[] sTmpFloats = new float[9];

    ScreenRotationAnimationInjector() {
    }

    static Animation createRotationExit(int width, int height, int orignRotation, int curRotation) {
        AnimationSet set = new AnimationSet(true);
        set.setInterpolator(QUART_EASE_OUT_INTERPOLATOR);
        ScreenRotationRotateAnimation screenRotationRotateAnimation = new ScreenRotationRotateAnimation(1, width, height, orignRotation, curRotation);
        screenRotationRotateAnimation.setDuration(500);
        screenRotationRotateAnimation.setFillAfter(true);
        screenRotationRotateAnimation.setFillBefore(true);
        screenRotationRotateAnimation.setFillEnabled(true);
        set.addAnimation(screenRotationRotateAnimation);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(250);
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setFillBefore(true);
        alphaAnimation.setFillEnabled(true);
        set.addAnimation(alphaAnimation);
        return set;
    }

    static Animation createRotationEnter(int width, int height, int orignRotation, int curRotation) {
        AnimationSet set = new AnimationSet(true);
        set.setInterpolator(QUART_EASE_OUT_INTERPOLATOR);
        ScreenRotationRotateAnimation screenRotationRotateAnimation = new ScreenRotationRotateAnimation(2, width, height, orignRotation, curRotation);
        screenRotationRotateAnimation.setDuration(500);
        screenRotationRotateAnimation.setFillAfter(true);
        screenRotationRotateAnimation.setFillBefore(true);
        screenRotationRotateAnimation.setFillEnabled(true);
        set.addAnimation(screenRotationRotateAnimation);
        return set;
    }

    public static boolean ignoreAspectPackages(String name) {
        return name.contains("com.xiaomi.account");
    }

    static boolean isTransitActivityClose(int transtion) {
        return transtion == 7;
    }

    static boolean isTransitActivityOpen(int transtion) {
        return transtion == 6;
    }

    static boolean isTransitClose(int transtion) {
        return transtion == 7 || transtion == 9 || transtion == 11;
    }

    static boolean isTransitOpen(int transtion) {
        return transtion == 6 || transtion == 8 || transtion == 10;
    }

    static boolean isTransitTaskClose(int transtion) {
        return transtion == 9 || transtion == 11;
    }

    static boolean isTransitTaskOpen(int transtion) {
        return transtion == 8 || transtion == 10;
    }

    static Animation createRotation180Exit() {
        AnimationSet set = new AnimationSet(false);
        RotateAnimation rotateAnimation = new RotateAnimation(0.0f, -180.0f, 1, 0.5f, 1, 0.5f);
        rotateAnimation.setInterpolator(QUART_EASE_OUT_INTERPOLATOR);
        rotateAnimation.setDuration(500);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setFillBefore(true);
        rotateAnimation.setFillEnabled(true);
        set.addAnimation(rotateAnimation);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setInterpolator(QUART_EASE_OUT_INTERPOLATOR);
        alphaAnimation.setDuration(250);
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setFillBefore(true);
        alphaAnimation.setFillEnabled(true);
        set.addAnimation(alphaAnimation);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, SCALE_FACTOR, 1.0f, SCALE_FACTOR, 1, 0.5f, 1, 0.5f);
        scaleAnimation.setInterpolator(QUART_EASE_OUT_INTERPOLATOR);
        scaleAnimation.setDuration(167);
        scaleAnimation.setFillAfter(false);
        scaleAnimation.setFillBefore(false);
        scaleAnimation.setFillEnabled(true);
        set.addAnimation(scaleAnimation);
        ScaleAnimation scalephase2 = new ScaleAnimation(SCALE_FACTOR, 1.0f, SCALE_FACTOR, 1.0f, 1, 0.5f, 1, 0.5f);
        scalephase2.setInterpolator(SIN_EASE_INOUT_INTERPOLATOR);
        scalephase2.setStartOffset(167);
        scalephase2.setDuration(333);
        scalephase2.setFillAfter(true);
        scalephase2.setFillBefore(false);
        scalephase2.setFillEnabled(true);
        set.addAnimation(scalephase2);
        return set;
    }

    static Animation createRotation180Enter() {
        AnimationSet set = new AnimationSet(false);
        RotateAnimation rotateAnimation = new RotateAnimation(180.0f, 0.0f, 1, 0.5f, 1, 0.5f);
        rotateAnimation.setInterpolator(QUART_EASE_OUT_INTERPOLATOR);
        rotateAnimation.setDuration(500);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setFillBefore(true);
        rotateAnimation.setFillEnabled(true);
        set.addAnimation(rotateAnimation);
        ScaleAnimation scalephase1 = new ScaleAnimation(1.0f, SCALE_FACTOR, 1.0f, SCALE_FACTOR, 1, 0.5f, 1, 0.5f);
        scalephase1.setInterpolator(QUART_EASE_OUT_INTERPOLATOR);
        scalephase1.setDuration(167);
        scalephase1.setFillAfter(false);
        scalephase1.setFillBefore(false);
        scalephase1.setFillEnabled(true);
        set.addAnimation(scalephase1);
        ScaleAnimation scalephase2 = new ScaleAnimation(SCALE_FACTOR, 1.0f, SCALE_FACTOR, 1.0f, 1, 0.5f, 1, 0.5f);
        scalephase2.setInterpolator(SIN_EASE_INOUT_INTERPOLATOR);
        scalephase2.setStartOffset(167);
        scalephase2.setDuration(333);
        scalephase2.setFillAfter(true);
        scalephase2.setFillBefore(false);
        scalephase2.setFillEnabled(true);
        set.addAnimation(scalephase2);
        return set;
    }

    static Animation createLauncherExit() {
        AnimationSet set = new AnimationSet(false);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setInterpolator(QUART_EASE_OUT_INTERPOLATOR);
        alphaAnimation.setDuration(250);
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setFillBefore(true);
        alphaAnimation.setFillEnabled(true);
        set.addAnimation(alphaAnimation);
        return set;
    }

    static Animation createLauncherEnter() {
        AnimationSet set = new AnimationSet(false);
        ScaleAnimation scalephase2 = new ScaleAnimation(SCALE_FACTOR, 1.0f, SCALE_FACTOR, 1.0f, 1, 0.5f, 1, 0.5f);
        scalephase2.setInterpolator(SIN_EASE_INOUT_INTERPOLATOR);
        scalephase2.setDuration(333);
        scalephase2.setFillAfter(true);
        scalephase2.setFillBefore(false);
        scalephase2.setFillEnabled(true);
        set.addAnimation(scalephase2);
        return set;
    }

    static SurfaceControl initializeBackgroundTop(DisplayContent displayContent, SurfaceControl.Transaction t, int size) {
        SurfaceControl sfBgCoverBgTop = displayContent.makeOverlay().setName("BlackSurface-top").setBufferSize(size, COVER_EGE).setColorLayer().setParent((SurfaceControl) null).build();
        t.setPosition(sfBgCoverBgTop, -10000.0f, -10000.0f);
        t.setAlpha(sfBgCoverBgTop, 1.0f);
        t.setLayer(sfBgCoverBgTop, SCREEN_FREEZE_LAYER_SCREENSHOT);
        t.setWindowCrop(sfBgCoverBgTop, size, COVER_EGE);
        t.setLayerStack(sfBgCoverBgTop, displayContent.getDisplayId());
        return sfBgCoverBgTop;
    }

    static SurfaceControl initializeBackgroundLt(DisplayContent displayContent, SurfaceControl.Transaction t, int size) {
        SurfaceControl sfBgCoverBgLeft = displayContent.makeOverlay().setName("BlackSurface-left").setBufferSize(COVER_EGE, size).setColorLayer().setParent((SurfaceControl) null).build();
        t.setPosition(sfBgCoverBgLeft, -10000.0f, -10000.0f);
        t.setAlpha(sfBgCoverBgLeft, 1.0f);
        t.setLayer(sfBgCoverBgLeft, SCREEN_FREEZE_LAYER_SCREENSHOT);
        t.setWindowCrop(sfBgCoverBgLeft, COVER_EGE, size);
        t.setLayerStack(sfBgCoverBgLeft, displayContent.getDisplayId());
        return sfBgCoverBgLeft;
    }

    static SurfaceControl initializeBackgroundRt(DisplayContent displayContent, SurfaceControl.Transaction t, int size) {
        SurfaceControl sfBgCoverBgRight = displayContent.makeOverlay().setName("BlackSurface-right").setBufferSize(COVER_EGE, size).setColorLayer().setParent((SurfaceControl) null).build();
        t.setPosition(sfBgCoverBgRight, -10000.0f, -10000.0f);
        t.setAlpha(sfBgCoverBgRight, 1.0f);
        t.setLayer(sfBgCoverBgRight, SCREEN_FREEZE_LAYER_SCREENSHOT);
        t.setWindowCrop(sfBgCoverBgRight, COVER_EGE, size);
        t.setLayerStack(sfBgCoverBgRight, displayContent.getDisplayId());
        return sfBgCoverBgRight;
    }

    static SurfaceControl initializeBackgroundBt(DisplayContent displayContent, SurfaceControl.Transaction t, int size) {
        SurfaceControl sfBgCoverBgBotom = displayContent.makeOverlay().setName("BlackSurface-bottom").setBufferSize(size, COVER_EGE).setColorLayer().setParent((SurfaceControl) null).build();
        t.setPosition(sfBgCoverBgBotom, -10000.0f, -10000.0f);
        t.setAlpha(sfBgCoverBgBotom, 1.0f);
        t.setLayer(sfBgCoverBgBotom, SCREEN_FREEZE_LAYER_SCREENSHOT);
        t.setWindowCrop(sfBgCoverBgBotom, size, COVER_EGE);
        t.setLayerStack(sfBgCoverBgBotom, displayContent.getDisplayId());
        return sfBgCoverBgBotom;
    }

    static void setBackgroundTransform(SurfaceControl.Transaction t, Transformation transformation, SurfaceControl sfBg, SurfaceControl sfBgCoverBottom, SurfaceControl sfBgCoverRight, SurfaceControl sfBgCoverLeft, SurfaceControl sfBgCoverTop) {
        SurfaceControl.Transaction transaction = t;
        SurfaceControl surfaceControl = sfBg;
        Matrix matrix = transformation.getMatrix();
        matrix.getValues(sTmpFloats);
        float[] fArr = sTmpFloats;
        transaction.setPosition(surfaceControl, fArr[2], fArr[5]);
        float[] fArr2 = sTmpFloats;
        t.setMatrix(sfBg, fArr2[0], fArr2[3], fArr2[1], fArr2[4]);
        transaction.setAlpha(surfaceControl, 1.0f);
        if (transformation.hasClipRect()) {
            Matrix tmpMatrix = new Matrix();
            tmpMatrix.setTranslate(0.0f, (float) transformation.getClipRect().height());
            tmpMatrix.postConcat(matrix);
            tmpMatrix.getValues(sTmpFloats);
            float[] fArr3 = sTmpFloats;
            transaction.setPosition(sfBgCoverBottom, fArr3[2], fArr3[5]);
            float[] fArr4 = sTmpFloats;
            float f = fArr4[0];
            float f2 = fArr4[3];
            float f3 = fArr4[1];
            float f4 = f3;
            Matrix tmpMatrix2 = tmpMatrix;
            t.setMatrix(sfBgCoverBottom, f, f2, f4, fArr4[4]);
            tmpMatrix2.reset();
            Matrix tmpMatrix3 = tmpMatrix2;
            tmpMatrix3.setTranslate((float) transformation.getClipRect().width(), 0.0f);
            tmpMatrix3.postConcat(matrix);
            tmpMatrix3.getValues(sTmpFloats);
            float[] fArr5 = sTmpFloats;
            transaction.setPosition(sfBgCoverRight, fArr5[2], fArr5[5]);
            float[] fArr6 = sTmpFloats;
            float f5 = fArr6[0];
            float f6 = fArr6[3];
            float f7 = fArr6[1];
            float f8 = f7;
            Matrix tmpMatrix4 = tmpMatrix3;
            t.setMatrix(sfBgCoverRight, f5, f6, f8, fArr6[4]);
            tmpMatrix4.reset();
            Matrix tmpMatrix5 = tmpMatrix4;
            tmpMatrix5.setTranslate(-800.0f, 0.0f);
            tmpMatrix5.postConcat(matrix);
            tmpMatrix5.getValues(sTmpFloats);
            float[] fArr7 = sTmpFloats;
            transaction.setPosition(sfBgCoverLeft, fArr7[2], fArr7[5]);
            float[] fArr8 = sTmpFloats;
            Matrix tmpMatrix6 = tmpMatrix5;
            t.setMatrix(sfBgCoverLeft, fArr8[0], fArr8[3], fArr8[1], fArr8[4]);
            tmpMatrix6.reset();
            Matrix tmpMatrix7 = tmpMatrix6;
            tmpMatrix7.setTranslate(-250.0f, -800.0f);
            tmpMatrix7.postConcat(matrix);
            tmpMatrix7.getValues(sTmpFloats);
            float[] fArr9 = sTmpFloats;
            transaction.setPosition(sfBgCoverTop, fArr9[2], fArr9[5]);
            float[] fArr10 = sTmpFloats;
            Matrix matrix2 = tmpMatrix7;
            t.setMatrix(sfBgCoverTop, fArr10[0], fArr10[3], fArr10[1], fArr10[4]);
            return;
        }
        SurfaceControl surfaceControl2 = sfBgCoverLeft;
        SurfaceControl surfaceControl3 = sfBgCoverTop;
        sfBgCoverLeft.hide();
        sfBgCoverBottom.hide();
        sfBgCoverRight.hide();
        sfBgCoverTop.hide();
    }

    static void drawBackgroud(Surface surface, int size) {
        Drawable backgroundDrawable;
        Canvas c = null;
        try {
            c = surface.lockCanvas(new Rect(0, 0, size, size));
        } catch (IllegalArgumentException e) {
            Slog.d(TAG, "drawBackgroud lockCanvas exception");
        }
        if (c != null) {
            IBinder b = ServiceManager.getService("uimode");
            boolean isDarkMode = false;
            if (b != null) {
                try {
                    isDarkMode = IUiModeManager.Stub.asInterface(b).getNightMode() == 2;
                } catch (Exception e2) {
                }
            }
            Drawable backgroundDrawable2 = isDarkMode ? new ColorDrawable(-16777216) : getTopVisibleActivityBackground(getActivityTaskManagerService());
            if (backgroundDrawable2 == null) {
                backgroundDrawable = new ColorDrawable(-1);
            } else {
                backgroundDrawable = backgroundDrawable2;
            }
            backgroundDrawable.setBounds(0, 0, size, size);
            backgroundDrawable.draw(c);
            Paint myBundPaint = new Paint();
            myBundPaint.setColor(-16777216);
            myBundPaint.setStrokeWidth(6.0f);
            Canvas canvas = c;
            Paint paint = myBundPaint;
            canvas.drawLine(0.0f, 0.0f, 0.0f, (float) size, paint);
            canvas.drawLine(0.0f, (float) size, (float) size, (float) size, paint);
            canvas.drawLine((float) size, (float) size, (float) size, 0.0f, paint);
            canvas.drawLine((float) size, 0.0f, 0.0f, 0.0f, paint);
            surface.unlockCanvasAndPost(c);
        }
    }

    private static ActivityTaskManagerService getActivityTaskManagerService() {
        return ActivityTaskManager.getService();
    }

    public static Drawable getTopVisibleActivityBackground(ActivityTaskManagerService atm) {
        ActivityRecord r;
        ActivityStack mainStack = atm.getTopDisplayFocusedStack();
        if (mainStack == null || (r = mainStack.topRunningActivityLocked()) == null || !r.isState(ActivityStack.ActivityState.RESUMED)) {
            return null;
        }
        return getCachedDrawable(atm, r);
    }

    static Drawable getCachedDrawable(ActivityTaskManagerService atm, ActivityRecord r) {
        Drawable backgroundDrawable;
        Drawable drawable;
        synchronized (ScreenRotationAnimationInjector.class) {
            backgroundDrawable = mDrawables.get(r.shortComponentName);
            if (backgroundDrawable == null) {
                try {
                    Context context = atm.mContext.createPackageContextAsUser(r.packageName, 0, new UserHandle(r.mUserId));
                    if (context != null) {
                        context.setTheme(r.getRealTheme());
                        TypedArray typedArray = context.obtainStyledAttributes(R.styleable.Window);
                        int resId = typedArray.getResourceId(1, 0);
                        if (!(resId == 0 || (drawable = context.getDrawable(resId)) == null)) {
                            backgroundDrawable = drawable;
                            mDrawables.put(r.shortComponentName, drawable);
                        }
                        typedArray.recycle();
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    Slog.e(TAG, "getCachedDrawable can not find drawable!");
                }
            }
        }
        return backgroundDrawable;
    }

    private static class ScreenRotationRotateAnimation extends Animation {
        MovieClip finalred = new MovieClip();
        private int mCurrentRotation;
        private int mOriginRotation;
        private float mScale = 1.0f;
        final Matrix mScreenShotInitialMatrix = new Matrix();
        private int mStageH;
        private int mStageW;
        private Transformation mTempTransform = new Transformation();
        private int mType;
        MovieClip red = new MovieClip();
        MovieClip redPoint = new MovieClip();
        MovieClip redPos = new MovieClip();

        public ScreenRotationRotateAnimation(int type, int stageW, int stageH, int orignRotation, int curRotation) {
            this.mType = type;
            this.mStageW = stageW;
            this.mStageH = stageH;
            this.mOriginRotation = orignRotation;
            this.mCurrentRotation = curRotation;
        }

        public void createScreenShotInitMatrix(Matrix outMatrix) {
            int rotation = DisplayContent.deltaRotation(this.mOriginRotation, 0);
            if (rotation == 0) {
                outMatrix.reset();
            } else if (rotation == 1) {
                outMatrix.setRotate(90.0f, 0.0f, 0.0f);
                outMatrix.preTranslate(0.0f, (float) (-this.mStageH));
            } else if (rotation == 2) {
                outMatrix.setRotate(180.0f, 0.0f, 0.0f);
                outMatrix.preTranslate((float) (-this.mStageW), (float) (-this.mStageH));
            } else if (rotation != 3) {
                outMatrix.reset();
            } else {
                outMatrix.setRotate(270.0f, 0.0f, 0.0f);
                outMatrix.preTranslate((float) (-this.mStageW), 0.0f);
            }
        }

        /* access modifiers changed from: protected */
        public void applyTransformation(float interpolatedTime, Transformation t) {
            updateTransformation(interpolatedTime, this.mScale);
            t.getMatrix().setRotate(this.finalred.rotation);
            t.getMatrix().postTranslate(this.finalred.x, this.finalred.y);
            Matrix matrix = t.getMatrix();
            float f = this.mScale;
            matrix.postScale(f, f);
            t.setClipRect(0, 0, (int) this.finalred.width, (int) this.finalred.height);
        }

        public boolean getTransformation(long currentTime, Transformation outTransformation) {
            float normalizedTime;
            float f;
            long j = currentTime;
            Transformation temp = this.mTempTransform;
            outTransformation.clear();
            long startOffset = getStartOffset();
            long duration = getDuration();
            long startTime = getStartTime();
            if (duration != 0) {
                normalizedTime = ((float) (j - (startTime + startOffset))) / ((float) duration);
            } else {
                normalizedTime = j < startTime ? 0.0f : 1.0f;
            }
            boolean fillAfter = getFillAfter();
            boolean fillBefore = getFillBefore();
            boolean fillEnabled = isFillEnabled();
            if (!fillEnabled) {
                f = 0.0f;
                normalizedTime = Math.max(Math.min(normalizedTime, 1.0f), 0.0f);
            } else {
                f = 0.0f;
            }
            if ((normalizedTime >= f || fillBefore) && (normalizedTime <= 1.0f || fillAfter)) {
                if (fillEnabled) {
                    normalizedTime = Math.max(Math.min(normalizedTime, 1.0f), 0.0f);
                }
                if (normalizedTime <= ScreenRotationAnimationInjector.SCALE_DELAY_OFFSET) {
                    this.mScale = (-0.060000002f * getQuartOutInterpolation(3.0f * normalizedTime)) + 1.0f;
                } else {
                    this.mScale = (0.060000002f * getSineOutInterpolation(((3.0f * normalizedTime) / 2.0f) - 0.5f)) + ScreenRotationAnimationInjector.SCALE_FACTOR;
                }
            }
            if (this.mType == 1) {
                outTransformation.getMatrix().set(this.mScreenShotInitialMatrix);
            }
            temp.clear();
            boolean more = super.getTransformation(j, temp);
            outTransformation.postCompose(temp);
            return more;
        }

        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
            if (this.mType == 1) {
                createScreenShotInitMatrix(this.mScreenShotInitialMatrix);
            }
        }

        public float getQuartOutInterpolation(float input) {
            return 1.0f - ((((input - 1.0f) * (input - 1.0f)) * (input - 1.0f)) * (input - 1.0f));
        }

        public float getSineOutInterpolation(float input) {
            return (float) Math.sin((((double) input) * 3.141592653589793d) / 2.0d);
        }

        public void updateTransformation(float per, float scale) {
            float per2;
            int toAng;
            Point r;
            if ((this.mOriginRotation == 3 && this.mCurrentRotation == 0) || (this.mOriginRotation == 1 && this.mCurrentRotation == 2)) {
                per -= 1.0f;
            } else if ((this.mOriginRotation == 0 && this.mCurrentRotation == 3) || (this.mOriginRotation == 2 && this.mCurrentRotation == 1)) {
                per = -per;
            } else if ((this.mOriginRotation == 1 && this.mCurrentRotation == 0) || (this.mOriginRotation == 3 && this.mCurrentRotation == 2)) {
                per = 1.0f - per;
            }
            float per3 = Math.min(1.0f, Math.max(-1.0f, per));
            float maxLen = ((float) Math.sqrt(2.0d)) * ((float) ((this.mStageH / 2) - (this.mStageW / 2))) * scale;
            if (per3 >= 0.0f) {
                toAng = 90;
                per2 = Math.abs(per3);
                r = calPoint(per2, 0.0f, maxLen, 225.0f, 135.0f);
            } else {
                toAng = -90;
                per2 = Math.abs(per3);
                r = calPoint(per2, 0.0f, maxLen, -135.0f, -45.0f);
            }
            this.redPoint.x = r.x;
            this.redPoint.y = r.y;
            Point xy = rot(((float) ((-this.mStageW) / 2)) * scale, ((float) ((-this.mStageH) / 2)) * scale, ((float) toAng) * per2);
            this.redPos.x = this.redPoint.x + xy.x + ((((float) this.mStageW) * (1.0f - this.mScale)) / 2.0f);
            this.redPos.y = this.redPoint.y + xy.y + ((((float) this.mStageH) * (1.0f - this.mScale)) / 2.0f);
            this.red.x = this.redPos.x;
            this.red.y = this.redPos.y;
            MovieClip movieClip = this.red;
            movieClip.rotation = ((float) toAng) * per2;
            MovieClip movieClip2 = this.finalred;
            float valFromPer = valFromPer(per2, this.mStageW, this.mStageH) * scale;
            movieClip.width = valFromPer;
            movieClip2.width = valFromPer;
            MovieClip movieClip3 = this.finalred;
            MovieClip movieClip4 = this.red;
            float valFromPer2 = valFromPer(per2, this.mStageH, this.mStageW) * scale;
            movieClip4.height = valFromPer2;
            movieClip3.height = valFromPer2;
            if ((this.mOriginRotation == 3 && this.mCurrentRotation == 0) || ((this.mOriginRotation == 1 && this.mCurrentRotation == 2) || ((this.mOriginRotation == 1 && this.mCurrentRotation == 0) || (this.mOriginRotation == 3 && this.mCurrentRotation == 2)))) {
                this.finalred.x = this.red.x;
                this.finalred.y = this.red.y;
                this.finalred.rotation = this.red.rotation;
            } else if ((this.mOriginRotation == 0 && this.mCurrentRotation == 3) || (this.mOriginRotation == 2 && this.mCurrentRotation == 1)) {
                this.finalred.x = ((float) this.mStageH) - this.red.y;
                this.finalred.y = this.red.x;
                this.finalred.rotation = this.red.rotation + 90.0f;
            } else if ((this.mOriginRotation == 0 && this.mCurrentRotation == 1) || (this.mOriginRotation == 2 && this.mCurrentRotation == 3)) {
                this.finalred.x = this.red.y;
                this.finalred.y = ((float) this.mStageW) - this.red.x;
                this.finalred.rotation = this.red.rotation - 90.0f;
            }
        }

        /* access modifiers changed from: package-private */
        public Point calPoint(float per, float len1, float len2, float ang1, float ang2) {
            float len = valFromPer(per, len1, len2);
            float ang = valFromPer(per, ang1, ang2);
            Point tempPoint = new Point();
            tempPoint.x = (float) ((((double) this.mStageW) * 0.5d) + (((double) len) * cos((double) ang)));
            tempPoint.y = (float) ((((double) this.mStageH) * 0.5d) - (((double) len) * sin((double) ang)));
            return tempPoint;
        }

        /* access modifiers changed from: package-private */
        public Point rot(float x, float y, float ang) {
            Point tempPoint = new Point();
            tempPoint.x = (float) ((((double) x) * cos((double) ang)) - (((double) y) * sin((double) ang)));
            tempPoint.y = (float) ((((double) x) * sin((double) ang)) + (((double) y) * cos((double) ang)));
            return tempPoint;
        }

        /* access modifiers changed from: package-private */
        public float valFromPer(float per, int from, int to) {
            return ((float) from) + (((float) (to - from)) * per);
        }

        /* access modifiers changed from: package-private */
        public float valFromPer(float per, float from, float to) {
            return ((to - from) * per) + from;
        }

        public double cos(double ang) {
            return Math.cos(0.017453292519943295d * ang);
        }

        public double sin(double ang) {
            return Math.sin(0.017453292519943295d * ang);
        }
    }

    static boolean hideGestureLineIfNeed(Context context, WindowManagerService wmservice, DisplayContent displyContent, MiuiSurfaceControllerHelper helper) {
        boolean isFsgMode = MiuiSettings.Global.getBoolean(context.getContentResolver(), "force_fsg_nav_bar");
        boolean hidegesutreline = Settings.Global.getInt(context.getContentResolver(), "hide_gesture_line", 0) != 0;
        WindowState navgationBar = displyContent.getDisplayPolicy().mNavigationBar;
        if (!isFsgMode || hidegesutreline || navgationBar == null || !navgationBar.isVisibleLw() || navgationBar.mWinAnimator == null || navgationBar.mWinAnimator.mSurfaceController == null || navgationBar.mWinAnimator.mSurfaceController.mSurfaceControl == null) {
            return false;
        }
        helper.setHandleByRotation(true);
        SurfaceControl.Transaction t0 = wmservice.mTransactionFactory.make();
        t0.setAlpha(navgationBar.mWinAnimator.mSurfaceController.mSurfaceControl, 0.0f);
        t0.apply();
        return true;
    }

    static void showGestureLineIfNeed(DisplayContent displyContent, final WindowManagerService wmservice, final MiuiSurfaceControllerHelper helper) {
        final WindowState navgationBar = displyContent.getDisplayPolicy().mNavigationBar;
        if (navgationBar != null && navgationBar.isVisibleLw() && navgationBar.mWinAnimator != null && navgationBar.mWinAnimator.mSurfaceController != null && navgationBar.mWinAnimator.mSurfaceController.mSurfaceControl != null) {
            applyGestureLineAnimationLocked(navgationBar, new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {
                    SurfaceControl.Transaction t0 = WindowManagerService.this.mTransactionFactory.make();
                    t0.setAlpha(navgationBar.mWinAnimator.mSurfaceController.mSurfaceControl, 1.0f);
                    t0.apply();
                }

                public void onAnimationEnd(Animation animation) {
                    helper.setHandleByRotation(false);
                }

                public void onAnimationRepeat(Animation animation) {
                }
            });
        } else if (navgationBar != null && navgationBar.mWinAnimator != null && navgationBar.mWinAnimator.mSurfaceController != null && navgationBar.mWinAnimator.mSurfaceController.mSurfaceControl != null) {
            SurfaceControl.Transaction t0 = wmservice.mTransactionFactory.make();
            t0.setAlpha(navgationBar.mWinAnimator.mSurfaceController.mSurfaceControl, 1.0f);
            t0.apply();
            helper.setHandleByRotation(false);
        }
    }

    static boolean applyGestureLineAnimationLocked(WindowState windowState, Animation.AnimationListener animationListener) {
        WindowState windowState2 = windowState;
        if (windowState2.mToken.okToAnimate()) {
            AnimationSet animationSet = new AnimationSet(true);
            Animation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
            alphaAnimation.setInterpolator(new EaseQuartOutInterpolator());
            alphaAnimation.setDuration(500);
            alphaAnimation.setFillAfter(true);
            alphaAnimation.setFillBefore(false);
            alphaAnimation.setFillEnabled(true);
            animationSet.addAnimation(alphaAnimation);
            TranslateAnimation translateAnimation = new TranslateAnimation(1, 0.0f, 1, 0.0f, 1, 1.0f, 1, 0.0f);
            translateAnimation.setInterpolator(new EaseQuartOutInterpolator());
            translateAnimation.setDuration(500);
            translateAnimation.setFillAfter(true);
            translateAnimation.setFillBefore(true);
            translateAnimation.setFillEnabled(true);
            animationSet.addAnimation(translateAnimation);
            animationSet.setAnimationListener(animationListener);
            windowState2.startAnimation(animationSet);
        } else {
            Animation.AnimationListener animationListener2 = animationListener;
        }
        return windowState.isAnimating();
    }

    public static class SineEaseOutInterpolator implements Interpolator {
        public float getInterpolation(float t) {
            return (float) Math.sin(((double) t) * 1.5707963267948966d);
        }
    }

    public static class SineEaseInInterpolater implements Interpolator {
        public float getInterpolation(float t) {
            return 1.0f - ((float) Math.cos(((double) t) * 1.5707963267948966d));
        }
    }
}
