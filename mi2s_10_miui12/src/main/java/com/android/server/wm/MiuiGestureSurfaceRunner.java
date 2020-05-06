package com.android.server.wm;

import android.os.Handler;
import android.os.Trace;
import android.util.Slog;
import android.view.Choreographer;

public class MiuiGestureSurfaceRunner implements ISurfaceRunner {
    private static String TAG = "MiuiGesture";
    private volatile boolean mAnimationCancelled;
    private final Choreographer.FrameCallback mAnimationFrameCallback;
    private boolean mAnimationFrameCallbackScheduled;
    private Choreographer mChoreographer;
    private IGestureStrategy mGestureStrategy;

    public MiuiGestureSurfaceRunner(Handler handler) {
        if (handler != null) {
            handler.runWithScissors(new Runnable() {
                public final void run() {
                    MiuiGestureSurfaceRunner.this.lambda$new$0$MiuiGestureSurfaceRunner();
                }
            }, 0);
        }
        this.mAnimationFrameCallback = new Choreographer.FrameCallback() {
            public final void doFrame(long j) {
                MiuiGestureSurfaceRunner.this.lambda$new$1$MiuiGestureSurfaceRunner(j);
            }
        };
    }

    public /* synthetic */ void lambda$new$0$MiuiGestureSurfaceRunner() {
        this.mChoreographer = Choreographer.getSfInstance();
    }

    public /* synthetic */ void lambda$new$1$MiuiGestureSurfaceRunner(long frameTimeNs) {
        this.mAnimationFrameCallbackScheduled = false;
        animate(frameTimeNs);
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    public void startAnimation(IGestureStrategy strategy) {
        synchronized (MiuiGestureController.mGestureLock) {
            if (strategy != null) {
                try {
                    this.mGestureStrategy = strategy;
                    this.mAnimationCancelled = false;
                    String str = TAG;
                    Slog.i(str, " start " + this.mGestureStrategy.getAnimationString() + " animation");
                    scheduleAnimation();
                    this.mGestureStrategy.onAnimationStart();
                } catch (Throwable th) {
                    throw th;
                }
            } else {
                throw new RuntimeException("animtor can't be null");
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x001b, code lost:
        r3.mGestureStrategy.finishAnimation();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0020, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void cancelAnimation() {
        /*
            r3 = this;
            java.lang.Object r0 = com.android.server.wm.MiuiGestureController.mGestureLock
            monitor-enter(r0)
            boolean r1 = r3.mAnimationCancelled     // Catch:{ all -> 0x0021 }
            if (r1 == 0) goto L_0x0009
            monitor-exit(r0)     // Catch:{ all -> 0x0021 }
            return
        L_0x0009:
            r1 = 1
            r3.mAnimationCancelled = r1     // Catch:{ all -> 0x0021 }
            boolean r1 = r3.mAnimationFrameCallbackScheduled     // Catch:{ all -> 0x0021 }
            if (r1 == 0) goto L_0x001a
            r1 = 0
            r3.mAnimationFrameCallbackScheduled = r1     // Catch:{ all -> 0x0021 }
            android.view.Choreographer r1 = r3.mChoreographer     // Catch:{ all -> 0x0021 }
            android.view.Choreographer$FrameCallback r2 = r3.mAnimationFrameCallback     // Catch:{ all -> 0x0021 }
            r1.removeFrameCallback(r2)     // Catch:{ all -> 0x0021 }
        L_0x001a:
            monitor-exit(r0)     // Catch:{ all -> 0x0021 }
            com.android.server.wm.IGestureStrategy r0 = r3.mGestureStrategy
            r0.finishAnimation()
            return
        L_0x0021:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0021 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.MiuiGestureSurfaceRunner.cancelAnimation():void");
    }

    private void scheduleAnimation() {
        if (!this.mAnimationFrameCallbackScheduled) {
            this.mAnimationFrameCallbackScheduled = true;
            this.mChoreographer.postFrameCallback(this.mAnimationFrameCallback);
        }
    }

    private void animate(long frameTimeNs) {
        if (!this.mAnimationCancelled) {
            Trace.traceBegin(32, "gesture" + this.mGestureStrategy.getAnimationString() + " animation");
            if (this.mGestureStrategy.updateAnimation(frameTimeNs)) {
                scheduleAnimation();
            } else {
                cancelAnimation();
            }
            Trace.traceEnd(32);
        }
    }
}
