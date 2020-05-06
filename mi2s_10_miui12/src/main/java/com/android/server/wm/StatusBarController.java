package com.android.server.wm;

import android.os.IBinder;
import com.android.server.am.BroadcastQueueInjector;
import com.android.server.statusbar.StatusBarManagerInternal;
import com.android.server.wm.StatusBarController;
import com.android.server.wm.WindowManagerInternal;

public class StatusBarController extends BarController {
    private final WindowManagerInternal.AppTransitionListener mAppTransitionListener = new WindowManagerInternal.AppTransitionListener() {
        private Runnable mAppTransitionCancelled = new Runnable() {
            public final void run() {
                StatusBarController.AnonymousClass1.this.lambda$$1$StatusBarController$1();
            }
        };
        private Runnable mAppTransitionFinished = new Runnable() {
            public final void run() {
                StatusBarController.AnonymousClass1.this.lambda$$2$StatusBarController$1();
            }
        };
        private Runnable mAppTransitionPending = new Runnable() {
            public final void run() {
                StatusBarController.AnonymousClass1.this.lambda$$0$StatusBarController$1();
            }
        };

        public /* synthetic */ void lambda$$0$StatusBarController$1() {
            StatusBarManagerInternal statusBar = StatusBarController.this.getStatusBarInternal();
            if (statusBar != null) {
                statusBar.appTransitionPending(StatusBarController.this.mDisplayId);
            }
        }

        public /* synthetic */ void lambda$$1$StatusBarController$1() {
            StatusBarManagerInternal statusBar = StatusBarController.this.getStatusBarInternal();
            if (statusBar != null) {
                statusBar.appTransitionCancelled(StatusBarController.this.mDisplayId);
            }
        }

        public /* synthetic */ void lambda$$2$StatusBarController$1() {
            StatusBarManagerInternal statusBar = StatusBarController.this.getStatusBarInternal();
            if (statusBar != null) {
                statusBar.appTransitionFinished(StatusBarController.this.mDisplayId);
            }
        }

        public void onAppTransitionPendingLocked() {
            StatusBarController.this.mHandler.post(this.mAppTransitionPending);
        }

        public int onAppTransitionStartingLocked(int transit, long duration, long statusBarAnimationStartTime, long statusBarAnimationDuration) {
            StatusBarController.this.mHandler.post(new Runnable(statusBarAnimationStartTime, statusBarAnimationDuration) {
                private final /* synthetic */ long f$1;
                private final /* synthetic */ long f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r4;
                }

                public final void run() {
                    StatusBarController.AnonymousClass1.this.lambda$onAppTransitionStartingLocked$3$StatusBarController$1(this.f$1, this.f$2);
                }
            });
            return 0;
        }

        public /* synthetic */ void lambda$onAppTransitionStartingLocked$3$StatusBarController$1(long statusBarAnimationStartTime, long statusBarAnimationDuration) {
            StatusBarManagerInternal statusBar = StatusBarController.this.getStatusBarInternal();
            if (statusBar != null) {
                statusBar.appTransitionStarting(StatusBarController.this.mDisplayId, statusBarAnimationStartTime, statusBarAnimationDuration);
            }
        }

        public void onAppTransitionCancelledLocked(int transit) {
            StatusBarController.this.mHandler.post(this.mAppTransitionCancelled);
        }

        public void onAppTransitionFinishedLocked(IBinder token) {
            StatusBarController.this.mHandler.post(this.mAppTransitionFinished);
        }
    };

    StatusBarController(int displayId) {
        super("StatusBar", displayId, BroadcastQueueInjector.FLAG_IMMUTABLE, 268435456, 1073741824, 1, BroadcastQueueInjector.FLAG_IMMUTABLE, 8);
    }

    /* access modifiers changed from: package-private */
    public void setTopAppHidesStatusBar(boolean hidesStatusBar) {
        StatusBarManagerInternal statusBar = getStatusBarInternal();
        if (statusBar != null) {
            statusBar.setTopAppHidesStatusBar(hidesStatusBar);
        }
    }

    /* access modifiers changed from: protected */
    public boolean skipAnimation() {
        return this.mWin.getAttrs().height == -1;
    }

    /* access modifiers changed from: package-private */
    public WindowManagerInternal.AppTransitionListener getAppTransitionListener() {
        return this.mAppTransitionListener;
    }
}
