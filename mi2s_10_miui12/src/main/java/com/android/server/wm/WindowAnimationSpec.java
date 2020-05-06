package com.android.server.wm;

import android.graphics.Point;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.proto.ProtoOutputStream;
import android.view.SurfaceControl;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.view.animation.MiuiTransformation;
import android.view.animation.RadiusAnimation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import com.android.server.wm.LocalAnimationAdapter;
import java.io.PrintWriter;
import java.util.List;

public class WindowAnimationSpec implements LocalAnimationAdapter.AnimationSpec {
    AppWindowAnimatorHelper mActivityThumbnailHelper;
    private Animation mAnimation;
    AnimationDimmer mAnimationDimmer;
    private final boolean mCanSkipFirstFrame;
    private final boolean mIsAppAnimation;
    MiuiTransformation mMiuiTransformation;
    private final Point mPosition;
    RadiusAnimation mRadiusAnimation;
    private final Rect mStackBounds;
    private int mStackClipMode;
    private final ThreadLocal<TmpValues> mThreadLocalTmps;
    AppWindowAnimatorHelper mThumbnailHelper;
    private final Rect mTmpRect;
    private final float mWindowCornerRadius;
    AppWindowToken mWindowToken;

    static /* synthetic */ TmpValues lambda$new$0() {
        return new TmpValues();
    }

    public WindowAnimationSpec(Animation animation, Point position, boolean canSkipFirstFrame, float windowCornerRadius) {
        this(animation, position, (Rect) null, canSkipFirstFrame, 2, false, windowCornerRadius);
    }

    public WindowAnimationSpec(Animation animation, Point position, Rect stackBounds, boolean canSkipFirstFrame, int stackClipMode, boolean isAppAnimation, float windowCornerRadius) {
        this.mPosition = new Point();
        this.mThreadLocalTmps = ThreadLocal.withInitial($$Lambda$WindowAnimationSpec$jKE7Phq2DESkeBondpaNPBLn6Cs.INSTANCE);
        this.mStackBounds = new Rect();
        this.mTmpRect = new Rect();
        this.mMiuiTransformation = new MiuiTransformation();
        this.mAnimation = animation;
        if (position != null) {
            this.mPosition.set(position.x, position.y);
        }
        this.mWindowCornerRadius = windowCornerRadius;
        this.mCanSkipFirstFrame = canSkipFirstFrame;
        this.mIsAppAnimation = isAppAnimation;
        this.mStackClipMode = stackClipMode;
        if (stackBounds != null) {
            this.mStackBounds.set(stackBounds);
        }
    }

    public boolean getShowWallpaper() {
        return this.mAnimation.getShowWallpaper();
    }

    public int getBackgroundColor() {
        return this.mAnimation.getBackgroundColor();
    }

    public long getDuration() {
        return this.mAnimation.computeDurationHint();
    }

    public void apply(SurfaceControl.Transaction t, SurfaceControl leash, long currentPlayTime) {
        TmpValues tmp = this.mThreadLocalTmps.get();
        tmp.transformation.clear();
        this.mAnimation.getTransformation(currentPlayTime, tmp.transformation);
        tmp.transformation.getMatrix().postTranslate((float) this.mPosition.x, (float) this.mPosition.y);
        t.setMatrix(leash, tmp.transformation.getMatrix(), tmp.floats);
        t.setAlpha(leash, tmp.transformation.getAlpha());
        boolean cropSet = false;
        if (this.mStackClipMode != 2) {
            this.mTmpRect.set(this.mStackBounds);
            if (tmp.transformation.hasClipRect()) {
                this.mTmpRect.intersect(tmp.transformation.getClipRect());
            }
            t.setWindowCrop(leash, this.mTmpRect);
            cropSet = true;
        } else if (tmp.transformation.hasClipRect()) {
            t.setWindowCrop(leash, tmp.transformation.getClipRect());
            cropSet = true;
        }
        this.mMiuiTransformation.clear();
        Animation animation = this.mAnimation;
        if (animation instanceof AnimationSet) {
            List<Animation> animations = ((AnimationSet) animation).getAnimations();
            for (int i = animations.size() - 1; i >= 0; i--) {
                RadiusAnimation radiusAnimation = (Animation) animations.get(i);
                if (radiusAnimation instanceof RadiusAnimation) {
                    this.mRadiusAnimation = radiusAnimation;
                    this.mRadiusAnimation.getTransformation(currentPlayTime, this.mMiuiTransformation);
                }
            }
        }
        AppWindowAnimatorHelper appWindowAnimatorHelper = this.mActivityThumbnailHelper;
        if (appWindowAnimatorHelper != null) {
            appWindowAnimatorHelper.stepMiuiActivityThumbnailAnimation(t, tmp.transformation, this.mMiuiTransformation.getRadius());
            if (this.mActivityThumbnailHelper.getLeash() != null) {
                t.setCornerRadius(this.mActivityThumbnailHelper.getLeash(), this.mMiuiTransformation.getRadius());
            }
        }
        AppWindowToken appWindowToken = this.mWindowToken;
        if (appWindowToken != null) {
            if (appWindowToken.mShouldActivityTransitionRoundCorner) {
                t.setCornerRadius(leash, this.mMiuiTransformation.getRadius());
            } else if ((cropSet && this.mAnimation.hasRoundedCorners() && this.mWindowCornerRadius > 0.0f) || this.mWindowToken.mShouldAppTransitionRoundCorner) {
                t.setCornerRadius(leash, this.mWindowCornerRadius);
            }
        }
        AppWindowAnimatorHelper appWindowAnimatorHelper2 = this.mThumbnailHelper;
        if (appWindowAnimatorHelper2 != null) {
            appWindowAnimatorHelper2.stepMiuiThumbnailAnimation(t, tmp.transformation);
        }
        AnimationDimmer animationDimmer = this.mAnimationDimmer;
        if (animationDimmer != null) {
            animationDimmer.stepTransitionDim(t, tmp.transformation);
        }
    }

    public long calculateStatusBarTransitionStartTime() {
        TranslateAnimation openTranslateAnimation = findTranslateAnimation(this.mAnimation);
        if (openTranslateAnimation == null) {
            return SystemClock.uptimeMillis();
        }
        return ((SystemClock.uptimeMillis() + openTranslateAnimation.getStartOffset()) + ((long) (((float) openTranslateAnimation.getDuration()) * findAlmostThereFraction(openTranslateAnimation.getInterpolator())))) - 120;
    }

    public boolean canSkipFirstFrame() {
        return this.mCanSkipFirstFrame;
    }

    public boolean needsEarlyWakeup() {
        return this.mIsAppAnimation;
    }

    public void dump(PrintWriter pw, String prefix) {
        pw.print(prefix);
        pw.println(this.mAnimation);
    }

    public void writeToProtoInner(ProtoOutputStream proto) {
        long token = proto.start(1146756268033L);
        proto.write(1138166333441L, this.mAnimation.toString());
        proto.end(token);
    }

    private static TranslateAnimation findTranslateAnimation(Animation animation) {
        if (animation instanceof TranslateAnimation) {
            return (TranslateAnimation) animation;
        }
        if (!(animation instanceof AnimationSet)) {
            return null;
        }
        AnimationSet set = (AnimationSet) animation;
        for (int i = 0; i < set.getAnimations().size(); i++) {
            Animation a = set.getAnimations().get(i);
            if (a instanceof TranslateAnimation) {
                return (TranslateAnimation) a;
            }
        }
        return null;
    }

    private static float findAlmostThereFraction(Interpolator interpolator) {
        float val = 0.5f;
        for (float adj = 0.25f; adj >= 0.01f; adj /= 2.0f) {
            if (interpolator.getInterpolation(val) < 0.99f) {
                val += adj;
            } else {
                val -= adj;
            }
        }
        return val;
    }

    private static class TmpValues {
        final float[] floats;
        final Transformation transformation;

        private TmpValues() {
            this.transformation = new Transformation();
            this.floats = new float[9];
        }
    }
}
