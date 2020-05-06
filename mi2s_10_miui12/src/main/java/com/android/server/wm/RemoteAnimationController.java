package com.android.server.wm;

import android.app.WindowConfiguration;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import android.view.IRemoteAnimationFinishedCallback;
import android.view.RemoteAnimationAdapter;
import android.view.RemoteAnimationTarget;
import android.view.SurfaceControl;
import com.android.internal.util.FastPrintWriter;
import com.android.server.wm.SurfaceAnimator;
import com.android.server.wm.utils.InsetUtils;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

class RemoteAnimationController implements IBinder.DeathRecipient {
    private static final String TAG = "WindowManager";
    private static final long TIMEOUT_MS = 2000;
    private boolean mCanceled;
    private FinishedCallback mFinishedCallback;
    /* access modifiers changed from: private */
    public final Handler mHandler;
    private boolean mLinkedToDeathOfRunner;
    /* access modifiers changed from: private */
    public final ArrayList<RemoteAnimationRecord> mPendingAnimations = new ArrayList<>();
    /* access modifiers changed from: private */
    public final RemoteAnimationAdapter mRemoteAnimationAdapter;
    private final WindowManagerService mService;
    /* access modifiers changed from: private */
    public final Runnable mTimeoutRunnable = new Runnable() {
        public final void run() {
            RemoteAnimationController.this.lambda$new$0$RemoteAnimationController();
        }
    };
    /* access modifiers changed from: private */
    public final Rect mTmpRect = new Rect();

    public /* synthetic */ void lambda$new$0$RemoteAnimationController() {
        cancelAnimation("timeoutRunnable");
    }

    RemoteAnimationController(WindowManagerService service, RemoteAnimationAdapter remoteAnimationAdapter, Handler handler) {
        this.mService = service;
        this.mRemoteAnimationAdapter = remoteAnimationAdapter;
        this.mHandler = handler;
    }

    /* access modifiers changed from: package-private */
    public RemoteAnimationRecord createRemoteAnimationRecord(AppWindowToken appWindowToken, Point position, Rect stackBounds, Rect startBounds) {
        RemoteAnimationRecord adapters = new RemoteAnimationRecord(appWindowToken, position, stackBounds, startBounds);
        this.mPendingAnimations.add(adapters);
        return adapters;
    }

    /* access modifiers changed from: package-private */
    public void goodToGo() {
        if (this.mPendingAnimations.isEmpty() || this.mCanceled) {
            onAnimationFinished();
            return;
        }
        this.mHandler.postDelayed(this.mTimeoutRunnable, (long) (this.mService.getCurrentAnimatorScale() * 2000.0f));
        this.mFinishedCallback = new FinishedCallback(this);
        RemoteAnimationTarget[] animations = createAnimations();
        if (animations.length == 0) {
            onAnimationFinished();
            return;
        }
        this.mService.mAnimator.addAfterPrepareSurfacesRunnable(new Runnable(animations) {
            private final /* synthetic */ RemoteAnimationTarget[] f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                RemoteAnimationController.this.lambda$goodToGo$1$RemoteAnimationController(this.f$1);
            }
        });
        sendRunningRemoteAnimation(true);
    }

    public /* synthetic */ void lambda$goodToGo$1$RemoteAnimationController(RemoteAnimationTarget[] animations) {
        try {
            linkToDeathOfRunner();
            this.mRemoteAnimationAdapter.getRunner().onAnimationStart(animations, this.mFinishedCallback);
        } catch (RemoteException e) {
            Slog.e("WindowManager", "Failed to start remote animation", e);
            onAnimationFinished();
        }
    }

    /* access modifiers changed from: package-private */
    public void cancelAnimation(String reason) {
        synchronized (this.mService.getWindowManagerLock()) {
            if (!this.mCanceled) {
                this.mCanceled = true;
                onAnimationFinished();
                invokeAnimationCancelled();
            }
        }
    }

    private void writeStartDebugStatement() {
        Slog.i("WindowManager", "Starting remote animation");
        StringWriter sw = new StringWriter();
        FastPrintWriter pw = new FastPrintWriter(sw);
        for (int i = this.mPendingAnimations.size() - 1; i >= 0; i--) {
            this.mPendingAnimations.get(i).mAdapter.dump(pw, "");
        }
        pw.close();
        Slog.i("WindowManager", sw.toString());
    }

    private RemoteAnimationTarget[] createAnimations() {
        ArrayList<RemoteAnimationTarget> targets = new ArrayList<>();
        for (int i = this.mPendingAnimations.size() - 1; i >= 0; i--) {
            RemoteAnimationRecord wrappers = this.mPendingAnimations.get(i);
            RemoteAnimationTarget target = wrappers.createRemoteAnimationTarget();
            if (target != null) {
                targets.add(target);
            } else {
                if (!(wrappers.mAdapter == null || wrappers.mAdapter.mCapturedFinishCallback == null)) {
                    wrappers.mAdapter.mCapturedFinishCallback.onAnimationFinished(wrappers.mAdapter);
                }
                if (!(wrappers.mThumbnailAdapter == null || wrappers.mThumbnailAdapter.mCapturedFinishCallback == null)) {
                    wrappers.mThumbnailAdapter.mCapturedFinishCallback.onAnimationFinished(wrappers.mThumbnailAdapter);
                }
                this.mPendingAnimations.remove(i);
            }
        }
        return (RemoteAnimationTarget[]) targets.toArray(new RemoteAnimationTarget[targets.size()]);
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    /* access modifiers changed from: private */
    public void onAnimationFinished() {
        this.mHandler.removeCallbacks(this.mTimeoutRunnable);
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                unlinkToDeathOfRunner();
                releaseFinishedCallback();
                this.mService.openSurfaceTransaction();
                for (int i = this.mPendingAnimations.size() - 1; i >= 0; i--) {
                    RemoteAnimationRecord adapters = this.mPendingAnimations.get(i);
                    if (adapters.mAdapter != null) {
                        adapters.mAdapter.mCapturedFinishCallback.onAnimationFinished(adapters.mAdapter);
                    }
                    if (adapters.mThumbnailAdapter != null) {
                        adapters.mThumbnailAdapter.mCapturedFinishCallback.onAnimationFinished(adapters.mThumbnailAdapter);
                    }
                }
                this.mService.closeSurfaceTransaction("RemoteAnimationController#finished");
            } catch (Exception e) {
                Slog.e("WindowManager", "Failed to finish remote animation", e);
                throw e;
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        sendRunningRemoteAnimation(false);
    }

    /* access modifiers changed from: private */
    public void invokeAnimationCancelled() {
        try {
            this.mRemoteAnimationAdapter.getRunner().onAnimationCancelled();
        } catch (RemoteException e) {
            Slog.e("WindowManager", "Failed to notify cancel", e);
        }
    }

    /* access modifiers changed from: private */
    public void releaseFinishedCallback() {
        FinishedCallback finishedCallback = this.mFinishedCallback;
        if (finishedCallback != null) {
            finishedCallback.release();
            this.mFinishedCallback = null;
        }
    }

    /* access modifiers changed from: private */
    public void sendRunningRemoteAnimation(boolean running) {
        int pid = this.mRemoteAnimationAdapter.getCallingPid();
        if (pid != 0) {
            this.mService.sendSetRunningRemoteAnimation(pid, running);
            return;
        }
        throw new RuntimeException("Calling pid of remote animation was null");
    }

    private void linkToDeathOfRunner() throws RemoteException {
        if (!this.mLinkedToDeathOfRunner) {
            this.mRemoteAnimationAdapter.getRunner().asBinder().linkToDeath(this, 0);
            this.mLinkedToDeathOfRunner = true;
        }
    }

    private void unlinkToDeathOfRunner() {
        if (this.mLinkedToDeathOfRunner) {
            this.mRemoteAnimationAdapter.getRunner().asBinder().unlinkToDeath(this, 0);
            this.mLinkedToDeathOfRunner = false;
        }
    }

    public void binderDied() {
        cancelAnimation("binderDied");
    }

    private static final class FinishedCallback extends IRemoteAnimationFinishedCallback.Stub {
        RemoteAnimationController mOuter;

        FinishedCallback(RemoteAnimationController outer) {
            this.mOuter = outer;
        }

        public void onAnimationFinished() throws RemoteException {
            long token = Binder.clearCallingIdentity();
            try {
                if (this.mOuter != null) {
                    this.mOuter.onAnimationFinished();
                    this.mOuter = null;
                }
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        /* access modifiers changed from: package-private */
        public void release() {
            this.mOuter = null;
        }
    }

    public class RemoteAnimationRecord {
        RemoteAnimationAdapterWrapper mAdapter;
        final AppWindowToken mAppWindowToken;
        final Rect mStartBounds;
        RemoteAnimationTarget mTarget;
        RemoteAnimationAdapterWrapper mThumbnailAdapter = null;

        RemoteAnimationRecord(AppWindowToken appWindowToken, Point endPos, Rect endBounds, Rect startBounds) {
            this.mAppWindowToken = appWindowToken;
            this.mAdapter = new RemoteAnimationAdapterWrapper(this, endPos, endBounds);
            if (startBounds != null) {
                this.mStartBounds = new Rect(startBounds);
                RemoteAnimationController.this.mTmpRect.set(startBounds);
                RemoteAnimationController.this.mTmpRect.offsetTo(0, 0);
                if (RemoteAnimationController.this.mRemoteAnimationAdapter.getChangeNeedsSnapshot()) {
                    this.mThumbnailAdapter = new RemoteAnimationAdapterWrapper(this, new Point(0, 0), RemoteAnimationController.this.mTmpRect);
                    return;
                }
                return;
            }
            this.mStartBounds = null;
        }

        /* access modifiers changed from: package-private */
        public RemoteAnimationTarget createRemoteAnimationTarget() {
            RemoteAnimationAdapterWrapper remoteAnimationAdapterWrapper;
            Task task = this.mAppWindowToken.getTask();
            WindowState mainWindow = this.mAppWindowToken.findMainWindow();
            SurfaceControl surfaceControl = null;
            if (task == null || mainWindow == null || (remoteAnimationAdapterWrapper = this.mAdapter) == null) {
            } else if (remoteAnimationAdapterWrapper.mCapturedFinishCallback == null) {
            } else if (this.mAdapter.mCapturedLeash == null) {
                Task task2 = task;
            } else {
                Rect insets = new Rect();
                mainWindow.getContentInsets(insets);
                InsetUtils.addInsets(insets, this.mAppWindowToken.getLetterboxInsets());
                int i = task.mTaskId;
                int mode = getMode();
                SurfaceControl surfaceControl2 = this.mAdapter.mCapturedLeash;
                boolean z = !this.mAppWindowToken.fillsParent();
                Rect rect = mainWindow.mWinAnimator.mLastClipRect;
                int prefixOrderIndex = this.mAppWindowToken.getPrefixOrderIndex();
                Point access$400 = this.mAdapter.mPosition;
                Rect access$500 = this.mAdapter.mStackBounds;
                WindowConfiguration windowConfiguration = task.getWindowConfiguration();
                RemoteAnimationAdapterWrapper remoteAnimationAdapterWrapper2 = this.mThumbnailAdapter;
                if (remoteAnimationAdapterWrapper2 != null) {
                    surfaceControl = remoteAnimationAdapterWrapper2.mCapturedLeash;
                }
                Task task3 = task;
                Task task4 = r5;
                Task remoteAnimationTarget = new RemoteAnimationTarget(i, mode, surfaceControl2, z, rect, insets, prefixOrderIndex, access$400, access$500, windowConfiguration, false, surfaceControl, this.mStartBounds);
                this.mTarget = task4;
                return this.mTarget;
            }
            return null;
        }

        private int getMode() {
            DisplayContent dc = this.mAppWindowToken.getDisplayContent();
            if (dc.mOpeningApps.contains(this.mAppWindowToken)) {
                return 0;
            }
            if (dc.mChangingApps.contains(this.mAppWindowToken)) {
                return 2;
            }
            return 1;
        }
    }

    private class RemoteAnimationAdapterWrapper implements AnimationAdapter {
        /* access modifiers changed from: private */
        public SurfaceAnimator.OnAnimationFinishedCallback mCapturedFinishCallback;
        SurfaceControl mCapturedLeash;
        /* access modifiers changed from: private */
        public final Point mPosition = new Point();
        private final RemoteAnimationRecord mRecord;
        /* access modifiers changed from: private */
        public final Rect mStackBounds = new Rect();

        RemoteAnimationAdapterWrapper(RemoteAnimationRecord record, Point position, Rect stackBounds) {
            this.mRecord = record;
            this.mPosition.set(position.x, position.y);
            this.mStackBounds.set(stackBounds);
        }

        public boolean getShowWallpaper() {
            return false;
        }

        public int getBackgroundColor() {
            return 0;
        }

        public void startAnimation(SurfaceControl animationLeash, SurfaceControl.Transaction t, SurfaceAnimator.OnAnimationFinishedCallback finishCallback) {
            t.setLayer(animationLeash, this.mRecord.mAppWindowToken.getPrefixOrderIndex());
            t.setPosition(animationLeash, (float) this.mPosition.x, (float) this.mPosition.y);
            RemoteAnimationController.this.mTmpRect.set(this.mStackBounds);
            RemoteAnimationController.this.mTmpRect.offsetTo(0, 0);
            t.setWindowCrop(animationLeash, RemoteAnimationController.this.mTmpRect);
            this.mCapturedLeash = animationLeash;
            this.mCapturedFinishCallback = finishCallback;
        }

        public void onAnimationCancelled(SurfaceControl animationLeash) {
            if (this.mRecord.mAdapter == this) {
                this.mRecord.mAdapter = null;
            } else {
                this.mRecord.mThumbnailAdapter = null;
            }
            if (this.mRecord.mAdapter == null && this.mRecord.mThumbnailAdapter == null) {
                RemoteAnimationController.this.mPendingAnimations.remove(this.mRecord);
            }
            if (RemoteAnimationController.this.mPendingAnimations.isEmpty()) {
                RemoteAnimationController.this.mHandler.removeCallbacks(RemoteAnimationController.this.mTimeoutRunnable);
                RemoteAnimationController.this.releaseFinishedCallback();
                RemoteAnimationController.this.invokeAnimationCancelled();
                RemoteAnimationController.this.sendRunningRemoteAnimation(false);
            }
        }

        public long getDurationHint() {
            return RemoteAnimationController.this.mRemoteAnimationAdapter.getDuration();
        }

        public long getStatusBarTransitionsStartTime() {
            return SystemClock.uptimeMillis() + RemoteAnimationController.this.mRemoteAnimationAdapter.getStatusBarTransitionDelay();
        }

        public void dump(PrintWriter pw, String prefix) {
            pw.print(prefix);
            pw.print("token=");
            pw.println(this.mRecord.mAppWindowToken);
            if (this.mRecord.mTarget != null) {
                pw.print(prefix);
                pw.println("Target:");
                RemoteAnimationTarget remoteAnimationTarget = this.mRecord.mTarget;
                remoteAnimationTarget.dump(pw, prefix + "  ");
                return;
            }
            pw.print(prefix);
            pw.println("Target: null");
        }

        public void writeToProto(ProtoOutputStream proto) {
            long token = proto.start(1146756268034L);
            if (this.mRecord.mTarget != null) {
                this.mRecord.mTarget.writeToProto(proto, 1146756268033L);
            }
            proto.end(token);
        }
    }
}
