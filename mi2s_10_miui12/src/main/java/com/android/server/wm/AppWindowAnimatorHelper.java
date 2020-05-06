package com.android.server.wm;

import android.graphics.Rect;
import android.view.SurfaceControl;
import android.view.animation.Transformation;
import com.miui.internal.transition.IMiuiAppTransitionAnimationHelper;

public class AppWindowAnimatorHelper {
    private final Rect mAppRect = new Rect();
    private boolean mHasNotifyMiuiThumbnailAnimEnd;
    private boolean mHasNotifyMiuiThumbnailAnimStart;
    private IMiuiAppTransitionAnimationHelper mMiuiAppTransitionAnimationHelper;
    private final Rect mMiuiThumbnailRect = new Rect();
    private SurfaceControl mThumbnailLeash;

    AppWindowAnimatorHelper(Rect appRect, IMiuiAppTransitionAnimationHelper helper) {
        this.mAppRect.set(appRect);
        this.mMiuiAppTransitionAnimationHelper = helper;
    }

    /* access modifiers changed from: package-private */
    public void setMiuiThumbnailRect(Rect thumbnailRect) {
        synchronized (this) {
            this.mMiuiThumbnailRect.set(thumbnailRect);
        }
    }

    public Rect getAppRect() {
        return this.mAppRect;
    }

    /* access modifiers changed from: package-private */
    public void setLeash(SurfaceControl leash) {
        synchronized (this) {
            this.mThumbnailLeash = leash;
        }
    }

    /* access modifiers changed from: package-private */
    public SurfaceControl getLeash() {
        SurfaceControl surfaceControl;
        synchronized (this) {
            surfaceControl = this.mThumbnailLeash;
        }
        return surfaceControl;
    }

    /* access modifiers changed from: package-private */
    public void clearMiuiThumbnail() {
        synchronized (this) {
            if (!this.mHasNotifyMiuiThumbnailAnimEnd) {
                AppTransitionInjector.notifyMiuiAnimationEnd(this.mMiuiAppTransitionAnimationHelper);
            } else {
                this.mHasNotifyMiuiThumbnailAnimEnd = false;
            }
            this.mHasNotifyMiuiThumbnailAnimStart = false;
            this.mAppRect.setEmpty();
            this.mMiuiThumbnailRect.setEmpty();
            this.mThumbnailLeash = null;
        }
    }

    /* access modifiers changed from: package-private */
    public void stepGestureThumbnailAnimation(SurfaceControl.Transaction t, Transformation appTransformation) {
        synchronized (this) {
            AppTransitionInjector.calculateGestureThumbnailSpec(this.mAppRect, this.mMiuiThumbnailRect, appTransformation.getMatrix(), 1.0f - appTransformation.getAlpha(), t, this.mThumbnailLeash);
        }
    }

    /* access modifiers changed from: package-private */
    public void stepMiuiThumbnailAnimation(SurfaceControl.Transaction t, Transformation appTransformation) {
        synchronized (this) {
            if (appTransformation.getAlpha() > 0.0f && !this.mHasNotifyMiuiThumbnailAnimStart) {
                this.mHasNotifyMiuiThumbnailAnimStart = true;
                AppTransitionInjector.notifyMiuiAnimationStart(this.mMiuiAppTransitionAnimationHelper);
            }
            float alpha = 1.0f - appTransformation.getAlpha();
            AppTransitionInjector.calculateMiuiThumbnailSpec(this.mAppRect, this.mMiuiThumbnailRect, appTransformation.getMatrix(), alpha, t, this.mThumbnailLeash);
            if (alpha == 1.0f && !this.mHasNotifyMiuiThumbnailAnimEnd) {
                this.mHasNotifyMiuiThumbnailAnimEnd = true;
                AppTransitionInjector.notifyMiuiAnimationEnd(this.mMiuiAppTransitionAnimationHelper);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void stepMiuiActivityThumbnailAnimation(SurfaceControl.Transaction t, Transformation appTransformation, float radius) {
        synchronized (this) {
            AppTransitionInjector.calculateMiuiActivityThumbnailSpec(this.mAppRect, this.mMiuiThumbnailRect, appTransformation.getMatrix(), 1.0f, radius, t, this.mThumbnailLeash);
        }
    }

    /* access modifiers changed from: package-private */
    public void destoryMiuiActivityThumbnailLeash() {
        SurfaceControl surfaceControl = this.mThumbnailLeash;
        if (surfaceControl != null) {
            surfaceControl.remove();
            this.mThumbnailLeash = null;
        }
    }
}
