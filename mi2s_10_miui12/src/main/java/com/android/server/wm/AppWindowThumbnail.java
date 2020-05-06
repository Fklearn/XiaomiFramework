package com.android.server.wm;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorSpace;
import android.graphics.GraphicBuffer;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Binder;
import android.util.proto.ProtoOutputStream;
import android.view.Surface;
import android.view.SurfaceControl;
import android.view.animation.Animation;
import com.android.server.job.controllers.JobStatus;
import com.android.server.wm.SurfaceAnimator;

class AppWindowThumbnail implements SurfaceAnimator.Animatable {
    private static final String TAG = "WindowManager";
    private Rect mAppRect;
    private final AppWindowToken mAppToken;
    private final int mHeight;
    private final boolean mRelative;
    private final SurfaceAnimator mSurfaceAnimator;
    private SurfaceControl mSurfaceControl;
    AppWindowAnimatorHelper mThumbnailHelper;
    private SurfaceControl mThumbnailLeash;
    private final int mWidth;

    AppWindowThumbnail(SurfaceControl.Transaction t, AppWindowToken appToken, GraphicBuffer thumbnailHeader) {
        this(t, appToken, thumbnailHeader, false);
    }

    AppWindowThumbnail(SurfaceControl.Transaction t, AppWindowToken appToken, GraphicBuffer thumbnailHeader, boolean relative) {
        this(t, appToken, thumbnailHeader, relative, (AppWindowAnimatorHelper) null);
    }

    AppWindowThumbnail(SurfaceControl.Transaction t, AppWindowToken appToken, GraphicBuffer thumbnailHeader, boolean relative, AppWindowAnimatorHelper helper) {
        this(t, appToken, thumbnailHeader, relative, helper, 0);
    }

    AppWindowThumbnail(SurfaceControl.Transaction t, AppWindowToken appToken, GraphicBuffer thumbnailHeader, boolean relative, AppWindowAnimatorHelper helper, int foreGroundColor) {
        this(t, appToken, thumbnailHeader, relative, new Surface(), (SurfaceAnimator) null, helper, foreGroundColor);
    }

    AppWindowThumbnail(SurfaceControl.Transaction t, AppWindowToken appToken, GraphicBuffer thumbnailHeader, boolean relative, Surface drawSurface, SurfaceAnimator animator, AppWindowAnimatorHelper helper, int foreGroundColor) {
        SurfaceControl.Transaction transaction = t;
        AppWindowToken appWindowToken = appToken;
        GraphicBuffer graphicBuffer = thumbnailHeader;
        boolean z = relative;
        Surface surface = drawSurface;
        SurfaceAnimator surfaceAnimator = animator;
        AppWindowAnimatorHelper appWindowAnimatorHelper = helper;
        this.mAppToken = appWindowToken;
        this.mRelative = z;
        if (surfaceAnimator != null) {
            this.mSurfaceAnimator = surfaceAnimator;
        } else {
            this.mSurfaceAnimator = new SurfaceAnimator(this, new Runnable() {
                public final void run() {
                    AppWindowThumbnail.this.onAnimationFinished();
                }
            }, appWindowToken.mWmService);
        }
        this.mWidth = thumbnailHeader.getWidth();
        this.mHeight = thumbnailHeader.getHeight();
        WindowState window = appToken.findMainWindow();
        if (!this.mAppToken.mIsMiuiActivityThumbnail || appWindowAnimatorHelper == null) {
            SurfaceControl.Builder makeSurface = appToken.makeSurface();
            this.mSurfaceControl = makeSurface.setName("thumbnail anim: " + appToken.toString()).setBufferSize(this.mWidth, this.mHeight).setFormat(-3).setMetadata(2, appWindowToken.windowType).setMetadata(1, window != null ? window.mOwnerUid : Binder.getCallingUid()).build();
        } else {
            this.mAppRect = helper.getAppRect();
            SurfaceControl.Builder makeSurface2 = appToken.makeSurface();
            this.mSurfaceControl = makeSurface2.setName("activity thumbnail anim: " + appToken.toString()).setBufferSize(this.mAppRect.width(), this.mAppRect.height()).setFormat(-3).setMetadata(2, appWindowToken.windowType).setMetadata(1, window != null ? window.mOwnerUid : Binder.getCallingUid()).build();
        }
        surface.copyFrom(this.mSurfaceControl);
        if (this.mAppToken.mIsMiuiActivityThumbnail) {
            Canvas canvas = drawSurface.lockHardwareCanvas();
            canvas.drawColor(foreGroundColor);
            canvas.drawBitmap(Bitmap.wrapHardwareBuffer(graphicBuffer, (ColorSpace) null), (Rect) null, new RectF(0.0f, 0.0f, (float) this.mWidth, (float) this.mHeight), new Paint());
            surface.unlockCanvasAndPost(canvas);
        } else {
            int i = foreGroundColor;
            surface.attachAndQueueBuffer(graphicBuffer);
        }
        drawSurface.release();
        transaction.show(this.mSurfaceControl);
        transaction.setLayer(this.mSurfaceControl, Integer.MAX_VALUE);
        if (z) {
            transaction.reparent(this.mSurfaceControl, appToken.getSurfaceControl());
        }
        if ((this.mAppToken.mIsMiuiThumbnail || this.mAppToken.mIsMiuiActivityThumbnail) && appWindowAnimatorHelper != null) {
            this.mThumbnailHelper = appWindowAnimatorHelper;
            this.mThumbnailHelper.setMiuiThumbnailRect(new Rect(0, 0, this.mWidth, this.mHeight));
        }
    }

    /* access modifiers changed from: package-private */
    public void startAnimation(SurfaceControl.Transaction t, Animation anim) {
        startAnimation(t, anim, (Point) null);
    }

    /* access modifiers changed from: package-private */
    public void startAnimation(SurfaceControl.Transaction t, Animation anim, Point position) {
        Rect rect;
        if (this.mAppToken.mIsMiuiThumbnail) {
            SurfaceControl surfaceControl = this.mSurfaceControl;
            if (surfaceControl != null) {
                this.mThumbnailLeash = this.mSurfaceAnimator.createAnimationLeash(surfaceControl, t, this.mHeight, this.mWidth, false);
                t.setLayer(this.mThumbnailLeash, Integer.MIN_VALUE);
                this.mThumbnailHelper.setLeash(this.mThumbnailLeash);
                if (this.mAppToken.mWindowAnimationSpec != null) {
                    this.mAppToken.mWindowAnimationSpec.mThumbnailHelper = this.mThumbnailHelper;
                }
            }
        } else if (!this.mAppToken.mIsMiuiActivityThumbnail || (rect = this.mAppRect) == null) {
            anim.restrictDuration(JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY);
            anim.scaleCurrentDuration(this.mAppToken.mWmService.getTransitionAnimationScaleLocked());
            this.mSurfaceAnimator.startAnimation(t, new LocalAnimationAdapter(new WindowAnimationSpec(anim, position, this.mAppToken.getDisplayContent().mAppTransition.canSkipFirstFrame(), this.mAppToken.getDisplayContent().getWindowCornerRadius()), this.mAppToken.mWmService.mSurfaceAnimationRunner), false);
        } else {
            SurfaceControl surfaceControl2 = this.mSurfaceControl;
            if (surfaceControl2 != null) {
                this.mThumbnailLeash = this.mSurfaceAnimator.createAnimationLeash(surfaceControl2, t, rect.width(), this.mAppRect.height(), false);
                t.setLayer(this.mThumbnailLeash, Integer.MIN_VALUE);
                t.setAlpha(this.mThumbnailLeash, 0.0f);
                this.mThumbnailHelper.setLeash(this.mThumbnailLeash);
                if (this.mAppToken.mWindowAnimationSpec != null) {
                    this.mAppToken.mWindowAnimationSpec.mActivityThumbnailHelper = this.mThumbnailHelper;
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void startAnimation(SurfaceControl.Transaction t, AnimationAdapter anim, boolean hidden) {
        this.mSurfaceAnimator.startAnimation(t, anim, hidden);
    }

    /* access modifiers changed from: private */
    public void onAnimationFinished() {
    }

    /* access modifiers changed from: package-private */
    public void setShowing(SurfaceControl.Transaction pendingTransaction, boolean show) {
        if (show) {
            pendingTransaction.show(this.mSurfaceControl);
        } else {
            pendingTransaction.hide(this.mSurfaceControl);
        }
    }

    /* access modifiers changed from: package-private */
    public void destroy() {
        AppWindowToken appWindowToken = this.mAppToken;
        if (appWindowToken == null || !appWindowToken.mIsMiuiThumbnail) {
            this.mSurfaceAnimator.cancelAnimation();
        }
        AppWindowAnimatorHelper appWindowAnimatorHelper = this.mThumbnailHelper;
        if (appWindowAnimatorHelper != null) {
            appWindowAnimatorHelper.clearMiuiThumbnail();
        }
        SurfaceControl surfaceControl = this.mThumbnailLeash;
        if (surfaceControl != null) {
            surfaceControl.remove();
            this.mThumbnailLeash = null;
        }
        getPendingTransaction().remove(this.mSurfaceControl);
        this.mSurfaceControl = null;
    }

    /* access modifiers changed from: package-private */
    public void writeToProto(ProtoOutputStream proto, long fieldId) {
        long token = proto.start(fieldId);
        proto.write(1120986464257L, this.mWidth);
        proto.write(1120986464258L, this.mHeight);
        if (this.mSurfaceAnimator.isAnimating()) {
            this.mSurfaceAnimator.writeToProto(proto, 1146756268035L);
        }
        proto.end(token);
    }

    public SurfaceControl.Transaction getPendingTransaction() {
        return this.mAppToken.getPendingTransaction();
    }

    public void commitPendingTransaction() {
        this.mAppToken.commitPendingTransaction();
    }

    public void onAnimationLeashCreated(SurfaceControl.Transaction t, SurfaceControl leash) {
        t.setLayer(leash, Integer.MAX_VALUE);
        if (this.mRelative) {
            t.reparent(leash, this.mAppToken.getSurfaceControl());
        }
    }

    public void onAnimationLeashLost(SurfaceControl.Transaction t) {
        t.hide(this.mSurfaceControl);
    }

    public SurfaceControl.Builder makeAnimationLeash() {
        return this.mAppToken.makeSurface();
    }

    public SurfaceControl getSurfaceControl() {
        return this.mSurfaceControl;
    }

    public SurfaceControl getAnimationLeashParent() {
        return this.mAppToken.getAppAnimationLayer();
    }

    public SurfaceControl getParentSurfaceControl() {
        return this.mAppToken.getParentSurfaceControl();
    }

    public int getSurfaceWidth() {
        return this.mWidth;
    }

    public int getSurfaceHeight() {
        return this.mHeight;
    }
}
