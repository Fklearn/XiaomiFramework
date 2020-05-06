package com.android.server.wm;

import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import android.view.SurfaceControl;
import com.android.internal.annotations.VisibleForTesting;
import java.io.PrintWriter;

class SurfaceAnimator {
    private static final String TAG = "WindowManager";
    @VisibleForTesting
    final Animatable mAnimatable;
    private AnimationAdapter mAnimation;
    @VisibleForTesting
    final Runnable mAnimationFinishedCallback;
    private boolean mAnimationStartDelayed;
    private final OnAnimationFinishedCallback mInnerAnimationFinishedCallback;
    @VisibleForTesting
    SurfaceControl mLeash;
    private final WindowManagerService mService;

    interface OnAnimationFinishedCallback {
        void onAnimationFinished(AnimationAdapter animationAdapter);
    }

    SurfaceAnimator(Animatable animatable, Runnable animationFinishedCallback, WindowManagerService service) {
        this.mAnimatable = animatable;
        this.mService = service;
        this.mAnimationFinishedCallback = animationFinishedCallback;
        this.mInnerAnimationFinishedCallback = getFinishedCallback(animationFinishedCallback);
    }

    private OnAnimationFinishedCallback getFinishedCallback(Runnable animationFinishedCallback) {
        return new OnAnimationFinishedCallback(animationFinishedCallback) {
            private final /* synthetic */ Runnable f$1;

            {
                this.f$1 = r2;
            }

            public final void onAnimationFinished(AnimationAdapter animationAdapter) {
                SurfaceAnimator.this.lambda$getFinishedCallback$1$SurfaceAnimator(this.f$1, animationAdapter);
            }
        };
    }

    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0038, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x003b, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$getFinishedCallback$1$SurfaceAnimator(java.lang.Runnable r5, com.android.server.wm.AnimationAdapter r6) {
        /*
            r4 = this;
            com.android.server.wm.WindowManagerService r0 = r4.mService
            com.android.server.wm.WindowManagerGlobalLock r0 = r0.mGlobalLock
            monitor-enter(r0)
            com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x003c }
            com.android.server.wm.WindowManagerService r1 = r4.mService     // Catch:{ all -> 0x003c }
            android.util.ArrayMap<com.android.server.wm.AnimationAdapter, com.android.server.wm.SurfaceAnimator> r1 = r1.mAnimationTransferMap     // Catch:{ all -> 0x003c }
            java.lang.Object r1 = r1.remove(r6)     // Catch:{ all -> 0x003c }
            com.android.server.wm.SurfaceAnimator r1 = (com.android.server.wm.SurfaceAnimator) r1     // Catch:{ all -> 0x003c }
            if (r1 == 0) goto L_0x001e
            com.android.server.wm.SurfaceAnimator$OnAnimationFinishedCallback r2 = r1.mInnerAnimationFinishedCallback     // Catch:{ all -> 0x003c }
            r2.onAnimationFinished(r6)     // Catch:{ all -> 0x003c }
            monitor-exit(r0)     // Catch:{ all -> 0x003c }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return
        L_0x001e:
            com.android.server.wm.AnimationAdapter r2 = r4.mAnimation     // Catch:{ all -> 0x003c }
            if (r6 == r2) goto L_0x0027
            monitor-exit(r0)     // Catch:{ all -> 0x003c }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return
        L_0x0027:
            com.android.server.wm.-$$Lambda$SurfaceAnimator$M9kRDTUpVS03LTqe-QLQz3DnMhk r2 = new com.android.server.wm.-$$Lambda$SurfaceAnimator$M9kRDTUpVS03LTqe-QLQz3DnMhk     // Catch:{ all -> 0x003c }
            r2.<init>(r6, r5)     // Catch:{ all -> 0x003c }
            com.android.server.wm.SurfaceAnimator$Animatable r3 = r4.mAnimatable     // Catch:{ all -> 0x003c }
            boolean r3 = r3.shouldDeferAnimationFinish(r2)     // Catch:{ all -> 0x003c }
            if (r3 != 0) goto L_0x0037
            r2.run()     // Catch:{ all -> 0x003c }
        L_0x0037:
            monitor-exit(r0)     // Catch:{ all -> 0x003c }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return
        L_0x003c:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x003c }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.SurfaceAnimator.lambda$getFinishedCallback$1$SurfaceAnimator(java.lang.Runnable, com.android.server.wm.AnimationAdapter):void");
    }

    public /* synthetic */ void lambda$getFinishedCallback$0$SurfaceAnimator(AnimationAdapter anim, Runnable animationFinishedCallback) {
        if (anim == this.mAnimation) {
            reset(this.mAnimatable.getPendingTransaction(), true);
            if (animationFinishedCallback != null) {
                animationFinishedCallback.run();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void startAnimation(SurfaceControl.Transaction t, AnimationAdapter anim, boolean hidden) {
        cancelAnimation(t, true, true);
        this.mAnimation = anim;
        SurfaceControl surface = this.mAnimatable.getSurfaceControl();
        if (surface == null) {
            Slog.w("WindowManager", "Unable to start animation, surface is null or no children.");
            cancelAnimation();
            return;
        }
        this.mLeash = createAnimationLeash(surface, t, this.mAnimatable.getSurfaceWidth(), this.mAnimatable.getSurfaceHeight(), hidden);
        this.mAnimatable.onAnimationLeashCreated(t, this.mLeash);
        if (!this.mAnimationStartDelayed) {
            this.mAnimation.startAnimation(this.mLeash, t, this.mInnerAnimationFinishedCallback);
        }
    }

    /* access modifiers changed from: package-private */
    public void startDelayingAnimationStart() {
        if (!isAnimating()) {
            this.mAnimationStartDelayed = true;
        }
    }

    /* access modifiers changed from: package-private */
    public void endDelayingAnimationStart() {
        AnimationAdapter animationAdapter;
        boolean delayed = this.mAnimationStartDelayed;
        this.mAnimationStartDelayed = false;
        if (delayed && (animationAdapter = this.mAnimation) != null) {
            animationAdapter.startAnimation(this.mLeash, this.mAnimatable.getPendingTransaction(), this.mInnerAnimationFinishedCallback);
            this.mAnimatable.commitPendingTransaction();
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isAnimating() {
        return this.mAnimation != null;
    }

    /* access modifiers changed from: package-private */
    public AnimationAdapter getAnimation() {
        return this.mAnimation;
    }

    /* access modifiers changed from: package-private */
    public void cancelAnimation() {
        cancelAnimation(this.mAnimatable.getPendingTransaction(), false, true);
        this.mAnimatable.commitPendingTransaction();
    }

    /* access modifiers changed from: package-private */
    public void setLayer(SurfaceControl.Transaction t, int layer) {
        SurfaceControl surfaceControl = this.mLeash;
        if (surfaceControl == null) {
            surfaceControl = this.mAnimatable.getSurfaceControl();
        }
        t.setLayer(surfaceControl, layer);
    }

    /* access modifiers changed from: package-private */
    public void setRelativeLayer(SurfaceControl.Transaction t, SurfaceControl relativeTo, int layer) {
        SurfaceControl surfaceControl = this.mLeash;
        if (surfaceControl == null) {
            surfaceControl = this.mAnimatable.getSurfaceControl();
        }
        t.setRelativeLayer(surfaceControl, relativeTo, layer);
    }

    /* access modifiers changed from: package-private */
    public void reparent(SurfaceControl.Transaction t, SurfaceControl newParent) {
        SurfaceControl surfaceControl = this.mLeash;
        if (surfaceControl == null) {
            surfaceControl = this.mAnimatable.getSurfaceControl();
        }
        t.reparent(surfaceControl, newParent);
    }

    /* access modifiers changed from: package-private */
    public boolean hasLeash() {
        return this.mLeash != null;
    }

    /* access modifiers changed from: package-private */
    public void transferAnimation(SurfaceAnimator from) {
        if (from.mLeash != null) {
            SurfaceControl surface = this.mAnimatable.getSurfaceControl();
            SurfaceControl parent = this.mAnimatable.getAnimationLeashParent();
            if (surface == null || parent == null) {
                Slog.w("WindowManager", "Unable to transfer animation, surface or parent is null");
                cancelAnimation();
                return;
            }
            endDelayingAnimationStart();
            SurfaceControl.Transaction t = this.mAnimatable.getPendingTransaction();
            cancelAnimation(t, true, true);
            this.mLeash = from.mLeash;
            this.mAnimation = from.mAnimation;
            from.cancelAnimation(t, false, false);
            t.reparent(surface, this.mLeash);
            t.reparent(this.mLeash, parent);
            this.mAnimatable.onAnimationLeashCreated(t, this.mLeash);
            this.mService.mAnimationTransferMap.put(this.mAnimation, this);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isAnimationStartDelayed() {
        return this.mAnimationStartDelayed;
    }

    private void cancelAnimation(SurfaceControl.Transaction t, boolean restarting, boolean forwardCancel) {
        SurfaceControl leash = this.mLeash;
        AnimationAdapter animation = this.mAnimation;
        reset(t, false);
        Animatable animatable = this.mAnimatable;
        if ((animatable instanceof AppWindowToken) && ((AppWindowToken) animatable).mHandleByGesture) {
            Slog.d("MiuiGesture", "apply reparent immediately");
            t.apply();
        }
        if (animation != null) {
            if (!this.mAnimationStartDelayed && forwardCancel) {
                animation.onAnimationCancelled(leash);
            }
            if (!restarting) {
                this.mAnimationFinishedCallback.run();
            }
        }
        if (forwardCancel && leash != null) {
            t.remove(leash);
            this.mService.scheduleAnimationLocked();
        }
        if (!restarting) {
            this.mAnimationStartDelayed = false;
        }
    }

    private void reset(SurfaceControl.Transaction t, boolean destroyLeash) {
        SurfaceControl surface = this.mAnimatable.getSurfaceControl();
        SurfaceControl parent = this.mAnimatable.getParentSurfaceControl();
        boolean scheduleAnim = false;
        boolean reparent = (this.mLeash == null || surface == null) ? false : true;
        if (reparent && surface.isValid() && parent != null && parent.isValid()) {
            t.reparent(surface, parent);
            scheduleAnim = true;
        }
        this.mService.mAnimationTransferMap.remove(this.mAnimation);
        SurfaceControl surfaceControl = this.mLeash;
        if (surfaceControl != null && destroyLeash) {
            t.remove(surfaceControl);
            scheduleAnim = true;
        }
        this.mLeash = null;
        this.mAnimation = null;
        if (reparent) {
            this.mAnimatable.onAnimationLeashLost(t);
            scheduleAnim = true;
        }
        if (scheduleAnim) {
            this.mService.scheduleAnimationLocked();
        }
    }

    /* access modifiers changed from: package-private */
    public SurfaceControl createAnimationLeash(SurfaceControl surface, SurfaceControl.Transaction t, int width, int height, boolean hidden) {
        SurfaceControl.Builder parent = this.mAnimatable.makeAnimationLeash().setParent(this.mAnimatable.getAnimationLeashParent());
        SurfaceControl leash = parent.setName(surface + " - animation-leash").build();
        t.setWindowCrop(leash, width, height);
        if (!hidden) {
            t.show(leash);
        }
        t.reparent(surface, leash);
        return leash;
    }

    /* access modifiers changed from: package-private */
    public void writeToProto(ProtoOutputStream proto, long fieldId) {
        long token = proto.start(fieldId);
        AnimationAdapter animationAdapter = this.mAnimation;
        if (animationAdapter != null) {
            animationAdapter.writeToProto(proto, 1146756268035L);
        }
        SurfaceControl surfaceControl = this.mLeash;
        if (surfaceControl != null) {
            surfaceControl.writeToProto(proto, 1146756268033L);
        }
        proto.write(1133871366146L, this.mAnimationStartDelayed);
        proto.end(token);
    }

    /* access modifiers changed from: package-private */
    public void dump(PrintWriter pw, String prefix) {
        pw.print(prefix);
        pw.print("mLeash=");
        pw.print(this.mLeash);
        if (this.mAnimationStartDelayed) {
            pw.print(" mAnimationStartDelayed=");
            pw.println(this.mAnimationStartDelayed);
        } else {
            pw.println();
        }
        pw.print(prefix);
        pw.println("Animation:");
        AnimationAdapter animationAdapter = this.mAnimation;
        if (animationAdapter != null) {
            animationAdapter.dump(pw, prefix + "  ");
            return;
        }
        pw.print(prefix);
        pw.println("null");
    }

    interface Animatable {
        void commitPendingTransaction();

        SurfaceControl getAnimationLeashParent();

        SurfaceControl getParentSurfaceControl();

        SurfaceControl.Transaction getPendingTransaction();

        SurfaceControl getSurfaceControl();

        int getSurfaceHeight();

        int getSurfaceWidth();

        SurfaceControl.Builder makeAnimationLeash();

        void onAnimationLeashCreated(SurfaceControl.Transaction transaction, SurfaceControl surfaceControl);

        void onAnimationLeashLost(SurfaceControl.Transaction transaction);

        boolean shouldDeferAnimationFinish(Runnable endDeferFinishCallback) {
            return false;
        }
    }
}
