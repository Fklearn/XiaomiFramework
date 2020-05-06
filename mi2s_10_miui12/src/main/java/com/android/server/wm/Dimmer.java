package com.android.server.wm;

import android.graphics.Rect;
import android.util.Log;
import android.util.MiuiMultiWindowUtils;
import android.util.proto.ProtoOutputStream;
import android.view.Surface;
import android.view.SurfaceControl;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.pm.DumpState;
import com.android.server.wm.Dimmer;
import com.android.server.wm.LocalAnimationAdapter;
import com.android.server.wm.SurfaceAnimator;
import java.io.PrintWriter;

class Dimmer {
    private static final int DEFAULT_DIM_ANIM_DURATION = 200;
    private static final String TAG = "WindowManager";
    @VisibleForTesting
    DimState mDimState;
    /* access modifiers changed from: private */
    public WindowContainer mHost;
    private WindowContainer mLastRequestedDimContainer;
    private final SurfaceAnimatorStarter mSurfaceAnimatorStarter;

    @VisibleForTesting
    interface SurfaceAnimatorStarter {
        void startAnimation(SurfaceAnimator surfaceAnimator, SurfaceControl.Transaction transaction, AnimationAdapter animationAdapter, boolean z);
    }

    private class DimAnimatable implements SurfaceAnimator.Animatable {
        private SurfaceControl mDimLayer;

        private DimAnimatable(SurfaceControl dimLayer) {
            this.mDimLayer = dimLayer;
        }

        public SurfaceControl.Transaction getPendingTransaction() {
            return Dimmer.this.mHost.getPendingTransaction();
        }

        public void commitPendingTransaction() {
            Dimmer.this.mHost.commitPendingTransaction();
        }

        public void onAnimationLeashCreated(SurfaceControl.Transaction t, SurfaceControl leash) {
        }

        public void onAnimationLeashLost(SurfaceControl.Transaction t) {
        }

        public SurfaceControl.Builder makeAnimationLeash() {
            return Dimmer.this.mHost.makeAnimationLeash();
        }

        public SurfaceControl getAnimationLeashParent() {
            return Dimmer.this.mHost.getSurfaceControl();
        }

        public SurfaceControl getSurfaceControl() {
            return this.mDimLayer;
        }

        public SurfaceControl getParentSurfaceControl() {
            return Dimmer.this.mHost.getSurfaceControl();
        }

        public int getSurfaceWidth() {
            return Dimmer.this.mHost.getSurfaceWidth();
        }

        public int getSurfaceHeight() {
            return Dimmer.this.mHost.getSurfaceHeight();
        }

        /* access modifiers changed from: package-private */
        public void removeSurface() {
            SurfaceControl surfaceControl = this.mDimLayer;
            if (surfaceControl != null && surfaceControl.isValid()) {
                getPendingTransaction().remove(this.mDimLayer);
            }
            this.mDimLayer = null;
        }
    }

    @VisibleForTesting
    class DimState {
        boolean isVisible;
        boolean mAnimateExit = true;
        SurfaceControl mDimLayer;
        boolean mDimming;
        boolean mDontReset;
        SurfaceAnimator mSurfaceAnimator;

        DimState(SurfaceControl dimLayer) {
            this.mDimLayer = dimLayer;
            this.mDimming = true;
            DimAnimatable dimAnimatable = new DimAnimatable(dimLayer);
            this.mSurfaceAnimator = new SurfaceAnimator(dimAnimatable, new Runnable(dimAnimatable) {
                private final /* synthetic */ Dimmer.DimAnimatable f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    Dimmer.DimState.this.lambda$new$0$Dimmer$DimState(this.f$1);
                }
            }, Dimmer.this.mHost.mWmService);
        }

        public /* synthetic */ void lambda$new$0$Dimmer$DimState(DimAnimatable dimAnimatable) {
            if (!this.mDimming) {
                dimAnimatable.removeSurface();
            }
        }
    }

    Dimmer(WindowContainer host) {
        this(host, $$Lambda$yACUZqn1AkGL14Nu3kHUSaLX0.INSTANCE);
    }

    Dimmer(WindowContainer host, SurfaceAnimatorStarter surfaceAnimatorStarter) {
        this.mHost = host;
        this.mSurfaceAnimatorStarter = surfaceAnimatorStarter;
    }

    private SurfaceControl makeDimLayer() {
        SurfaceControl.Builder colorLayer = this.mHost.makeChildSurface((WindowContainer) null).setParent(this.mHost.getSurfaceControl()).setColorLayer();
        return colorLayer.setName("Dim Layer for - " + this.mHost.getName()).build();
    }

    private DimState getDimState(WindowContainer container) {
        if (this.mDimState == null) {
            try {
                SurfaceControl ctl = makeDimLayer();
                if (!(ctl == null || container == null || !container.inFreeformWindowingMode())) {
                    ctl.setMatrix(MiuiMultiWindowUtils.sScale, 0.0f, 0.0f, MiuiMultiWindowUtils.sScale);
                }
                if (!(ctl == null || container == null || (!container.inFreeformWindowingMode() && !container.mIsPrivacy))) {
                    SurfaceControl.Transaction dimmerTransaction = new SurfaceControl.Transaction();
                    if (!DisplayContent.mFreeFormWindowShowed) {
                        if (!container.mIsPrivacy) {
                            dimmerTransaction.setScreenProjection(ctl, 0);
                            dimmerTransaction.apply();
                        }
                    }
                    dimmerTransaction.setScreenProjection(ctl, DumpState.DUMP_SERVICE_PERMISSIONS);
                    dimmerTransaction.apply();
                }
                this.mDimState = new DimState(ctl);
                if (container == null) {
                    this.mDimState.mDontReset = true;
                }
            } catch (Surface.OutOfResourcesException e) {
                Log.w("WindowManager", "OutOfResourcesException creating dim surface");
            }
        }
        this.mLastRequestedDimContainer = container;
        return this.mDimState;
    }

    private void dim(SurfaceControl.Transaction t, WindowContainer container, int relativeLayer, float alpha) {
        DimState d;
        if ((container == null || !container.inFreeformWindowingMode()) && (d = getDimState(container)) != null) {
            if (container != null) {
                t.setRelativeLayer(d.mDimLayer, container.getSurfaceControl(), relativeLayer);
            } else {
                t.setLayer(d.mDimLayer, Integer.MAX_VALUE);
            }
            t.setAlpha(d.mDimLayer, alpha);
            d.mDimming = true;
        }
    }

    /* access modifiers changed from: package-private */
    public void stopDim(SurfaceControl.Transaction t) {
        DimState dimState = this.mDimState;
        if (dimState != null) {
            t.hide(dimState.mDimLayer);
            DimState dimState2 = this.mDimState;
            dimState2.isVisible = false;
            dimState2.mDontReset = false;
        }
    }

    /* access modifiers changed from: package-private */
    public void dimAbove(SurfaceControl.Transaction t, float alpha) {
        dim(t, (WindowContainer) null, 1, alpha);
    }

    /* access modifiers changed from: package-private */
    public void dimAbove(SurfaceControl.Transaction t, WindowContainer container, float alpha) {
        dim(t, container, 1, alpha);
    }

    /* access modifiers changed from: package-private */
    public void dimBelow(SurfaceControl.Transaction t, WindowContainer container, float alpha) {
        dim(t, container, -1, alpha);
    }

    /* access modifiers changed from: package-private */
    public void resetDimStates() {
        DimState dimState = this.mDimState;
        if (dimState != null && !dimState.mDontReset) {
            this.mDimState.mDimming = false;
        }
    }

    /* access modifiers changed from: package-private */
    public void dontAnimateExit() {
        DimState dimState = this.mDimState;
        if (dimState != null) {
            dimState.mAnimateExit = false;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean updateDims(SurfaceControl.Transaction t, Rect bounds) {
        DimState dimState = this.mDimState;
        if (dimState == null) {
            return false;
        }
        if (!dimState.mDimming) {
            if (this.mDimState.mAnimateExit) {
                startDimExit(this.mLastRequestedDimContainer, this.mDimState.mSurfaceAnimator, t);
            } else if (this.mDimState.mDimLayer.isValid()) {
                t.remove(this.mDimState.mDimLayer);
            }
            this.mDimState = null;
            return false;
        }
        t.setPosition(this.mDimState.mDimLayer, (float) bounds.left, (float) bounds.top);
        t.setWindowCrop(this.mDimState.mDimLayer, bounds.width(), bounds.height());
        if (!this.mDimState.isVisible) {
            DimState dimState2 = this.mDimState;
            dimState2.isVisible = true;
            t.show(dimState2.mDimLayer);
            startDimEnter(this.mLastRequestedDimContainer, this.mDimState.mSurfaceAnimator, t);
        }
        return true;
    }

    private void startDimEnter(WindowContainer container, SurfaceAnimator animator, SurfaceControl.Transaction t) {
        startAnim(container, animator, t, 0.0f, 1.0f);
    }

    private void startDimExit(WindowContainer container, SurfaceAnimator animator, SurfaceControl.Transaction t) {
        startAnim(container, animator, t, 1.0f, 0.0f);
    }

    private void startAnim(WindowContainer container, SurfaceAnimator animator, SurfaceControl.Transaction t, float startAlpha, float endAlpha) {
        this.mSurfaceAnimatorStarter.startAnimation(animator, t, new LocalAnimationAdapter(new AlphaAnimationSpec(startAlpha, endAlpha, getDimDuration(container)), this.mHost.mWmService.mSurfaceAnimationRunner), false);
    }

    private long getDimDuration(WindowContainer container) {
        if (container == null) {
            return 0;
        }
        AnimationAdapter animationAdapter = container.mSurfaceAnimator.getAnimation();
        if (animationAdapter == null) {
            return 200;
        }
        return animationAdapter.getDurationHint();
    }

    private static class AlphaAnimationSpec implements LocalAnimationAdapter.AnimationSpec {
        private final long mDuration;
        private final float mFromAlpha;
        private final float mToAlpha;

        AlphaAnimationSpec(float fromAlpha, float toAlpha, long duration) {
            this.mFromAlpha = fromAlpha;
            this.mToAlpha = toAlpha;
            this.mDuration = duration;
        }

        public long getDuration() {
            return this.mDuration;
        }

        public void apply(SurfaceControl.Transaction t, SurfaceControl sc, long currentPlayTime) {
            float duration = ((float) currentPlayTime) / ((float) getDuration());
            float f = this.mToAlpha;
            float f2 = this.mFromAlpha;
            t.setAlpha(sc, (duration * (f - f2)) + f2);
        }

        public void dump(PrintWriter pw, String prefix) {
            pw.print(prefix);
            pw.print("from=");
            pw.print(this.mFromAlpha);
            pw.print(" to=");
            pw.print(this.mToAlpha);
            pw.print(" duration=");
            pw.println(this.mDuration);
        }

        public void writeToProtoInner(ProtoOutputStream proto) {
            long token = proto.start(1146756268035L);
            proto.write(1108101562369L, this.mFromAlpha);
            proto.write(1108101562370L, this.mToAlpha);
            proto.write(1112396529667L, this.mDuration);
            proto.end(token);
        }
    }

    public void setCastFlags(WindowContainer container, boolean enter) {
        DimState d = getDimState(container);
        if (d != null) {
            SurfaceControl.Transaction t = new SurfaceControl.Transaction();
            t.setFlagsFromSV(d.mDimLayer, enter);
            t.apply();
        }
    }

    public void setPrivacyFlags(WindowContainer container, boolean isPrivacy) {
        DimState d = getDimState(container);
        if (d != null) {
            SurfaceControl.Transaction t = new SurfaceControl.Transaction();
            if (isPrivacy) {
                t.setScreenProjection(d.mDimLayer, DumpState.DUMP_SERVICE_PERMISSIONS);
            } else {
                t.setScreenProjection(d.mDimLayer, 0);
            }
            t.apply();
        }
    }
}
