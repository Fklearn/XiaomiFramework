package com.android.server.wm;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.view.DisplayInfo;
import android.view.Surface;
import android.view.SurfaceControl;
import android.view.SurfaceSession;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;

public class MultiWindowAnimation {
    static final int ANIMATION_TIMEOUT = 2000;
    public static final int TRANSIT_ENTER = 2;
    public static final int TRANSIT_EXIT = 3;
    public static final int TRANSIT_NONE = 0;
    public static final int TRANSIT_RESIZE = 4;
    public static final int TRANSIT_SWAP = 1;
    private boolean mAnimRunning = false;
    private MultiWindowAnimSurface mAnimSurface = null;
    private Animation mAnimation;
    private final Runnable mAnimationTimeoutRunnable = new Runnable() {
        public void run() {
            MultiWindowAnimation.this.stopAnimation();
        }
    };
    private final Context mContext;
    private final DisplayContent mDisplayContent;
    private final WindowManagerService mService;
    private final SurfaceSession mSession;
    private Transformation mTransformation = new Transformation();
    private int mTransition;

    MultiWindowAnimation(Context context, DisplayContent displayContent, SurfaceSession session, WindowManagerService service) {
        this.mContext = context;
        this.mSession = session;
        this.mDisplayContent = displayContent;
        this.mService = service;
    }

    /* access modifiers changed from: package-private */
    public void setTransition(int transit) {
        if ((transit == 1 || transit == 2 || transit == 3 || transit == 4) && this.mAnimSurface == null) {
            this.mTransition = transit;
            createSurface();
        }
    }

    public void stopAnimation() {
        MultiWindowAnimSurface multiWindowAnimSurface = this.mAnimSurface;
        if (multiWindowAnimSurface != null) {
            multiWindowAnimSurface.kill();
            this.mAnimSurface = null;
        }
        this.mAnimRunning = false;
        this.mTransition = 0;
    }

    private MultiWindowAnimSurface createSurface() {
        DisplayInfo displayInfo = this.mDisplayContent.getDisplayInfo();
        boolean z = true;
        if (!(displayInfo.rotation == 1 || displayInfo.rotation == 3)) {
            z = false;
        }
        boolean isLandscape = z;
        try {
            this.mAnimSurface = new MultiWindowAnimSurface(this.mSession, 0, 0, isLandscape ? displayInfo.logicalHeight : displayInfo.logicalWidth, isLandscape ? displayInfo.logicalWidth : displayInfo.logicalHeight, displayInfo.layerStack, displayInfo.rotation);
        } catch (Surface.OutOfResourcesException e) {
            e.printStackTrace();
        }
        MultiWindowAnimSurface multiWindowAnimSurface = this.mAnimSurface;
        if (multiWindowAnimSurface != null) {
            multiWindowAnimSurface.show();
        }
        this.mService.mH.postDelayed(this.mAnimationTimeoutRunnable, 2000);
        return this.mAnimSurface;
    }

    public boolean isAnimating() {
        return (this.mAnimSurface == null || this.mTransition == 0) ? false : true;
    }

    /* access modifiers changed from: package-private */
    public boolean stepAnimationLocked(long now) {
        return stepAnimation(now);
    }

    private boolean stepAnimation(long now) {
        int i = this.mTransition;
        if (i == 1 || i == 2 || i == 3 || i == 4) {
            return animate(now, 17432577);
        }
        return false;
    }

    private boolean animate(long now, int animResId) {
        if (!this.mAnimRunning && this.mAnimSurface != null) {
            this.mAnimRunning = true;
            this.mAnimation = AnimationUtils.loadAnimation(this.mContext, animResId);
            this.mAnimation.scaleCurrentDuration(this.mService.getTransitionAnimationScaleLocked());
            this.mAnimation.initialize(this.mAnimSurface.mRect.centerX(), this.mAnimSurface.mRect.centerY(), this.mAnimSurface.mRect.centerX(), this.mAnimSurface.mRect.centerY());
            return true;
        } else if (this.mAnimation == null) {
            return false;
        } else {
            this.mTransformation.clear();
            boolean moreAnim = this.mAnimation.getTransformation(now, this.mTransformation);
            MultiWindowAnimSurface multiWindowAnimSurface = this.mAnimSurface;
            if (multiWindowAnimSurface == null) {
                return false;
            }
            multiWindowAnimSurface.setAnimationTransform(this.mTransformation);
            if (moreAnim) {
                return true;
            }
            this.mAnimSurface.kill();
            this.mAnimSurface = null;
            this.mAnimRunning = false;
            this.mTransition = 0;
            this.mAnimation = null;
            return false;
        }
    }

    private static class MultiWindowAnimSurface {
        static final int FREEZE_LAYER = 2000001;
        Rect mRect = new Rect();
        int mRotation;
        SurfaceControl mSurface = null;
        float[] mTmpFloats = new float[9];
        Matrix mTmpMatrix = new Matrix();

        MultiWindowAnimSurface(SurfaceSession session, int left, int top, int right, int bottom, int layerStack, int rotation) throws Surface.OutOfResourcesException {
            int i = left;
            int i2 = top;
            int i3 = right;
            int i4 = bottom;
            int w = i3 - i;
            int h = i4 - i2;
            this.mRect.set(i, i2, i3, i4);
            this.mRotation = rotation;
            this.mSurface = new SurfaceControl.Builder(session).setName("MultiWindowAnimSurface").setBufferSize(w, h).setFormat(4).setFlags(4).build();
            Surface sur = new Surface();
            sur.copyFrom(this.mSurface);
            SurfaceControl.screenshot(SurfaceControl.getInternalDisplayToken(), sur);
            sur.destroy();
            this.mSurface.setLayerStack(layerStack);
            this.mSurface.setLayer(FREEZE_LAYER);
            int i5 = this.mRotation;
            if (i5 == 0) {
                this.mSurface.setPosition(0.0f, 0.0f);
                this.mSurface.setMatrix(1.0f, 0.0f, 0.0f, 1.0f);
            } else if (i5 == 1) {
                this.mSurface.setPosition(0.0f, (float) w);
                this.mSurface.setMatrix(0.0f, -1.0f, 1.0f, 0.0f);
            } else if (i5 == 2) {
                this.mSurface.setPosition((float) w, (float) h);
                this.mSurface.setMatrix(-1.0f, 0.0f, 0.0f, -1.0f);
            } else if (i5 == 3) {
                this.mSurface.setPosition((float) h, 0.0f);
                this.mSurface.setMatrix(0.0f, 1.0f, -1.0f, 0.0f);
            }
        }

        /* access modifiers changed from: package-private */
        public void setAnimationTransform(Transformation transformation) {
            this.mTmpMatrix.set(transformation.getMatrix());
            float left = (float) this.mRect.left;
            float top = (float) this.mRect.top;
            float width = (float) this.mRect.width();
            float height = (float) this.mRect.height();
            int i = this.mRotation;
            if (i == 0) {
                this.mTmpMatrix.postTranslate(left, top);
            } else if (i == 1) {
                this.mTmpMatrix.postRotate(270.0f);
                this.mTmpMatrix.postTranslate(left, width - top);
            } else if (i == 2) {
                this.mTmpMatrix.postRotate(180.0f);
                this.mTmpMatrix.postTranslate(width - left, height - top);
            } else if (i == 3) {
                this.mTmpMatrix.postRotate(90.0f);
                this.mTmpMatrix.postTranslate(height - left, top);
            }
            this.mTmpMatrix.getValues(this.mTmpFloats);
            SurfaceControl surfaceControl = this.mSurface;
            float[] fArr = this.mTmpFloats;
            surfaceControl.setPosition(fArr[2], fArr[5]);
            SurfaceControl surfaceControl2 = this.mSurface;
            float[] fArr2 = this.mTmpFloats;
            surfaceControl2.setMatrix(fArr2[0], fArr2[3], fArr2[1], fArr2[4]);
            this.mSurface.setAlpha(transformation.getAlpha());
        }

        /* access modifiers changed from: package-private */
        public void show() {
            SurfaceControl surfaceControl = this.mSurface;
            if (surfaceControl != null) {
                surfaceControl.show();
            }
        }

        /* access modifiers changed from: package-private */
        public void kill() {
            SurfaceControl surfaceControl = this.mSurface;
            if (surfaceControl != null) {
                surfaceControl.remove();
                this.mSurface = null;
            }
        }
    }
}
