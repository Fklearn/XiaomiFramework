package com.android.server.wm;

import android.view.IGestureStubListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class GestureStubController {
    private static final int GESTURE_RESET_DELAY = 2000;
    private DisplayContent mDisplayContent;
    private Runnable mGestureFinishRunnable = new Runnable() {
        public void run() {
            GestureStubController.this.reset();
        }
    };
    private IGestureStubListener mGestureStubListener;
    private Set<AppWindowToken> mInvisibleAppTokens = new HashSet();
    private boolean mIsAppTransitionSkipped = false;
    /* access modifiers changed from: private */
    public boolean mKeepWallpaperShowing;
    private final Object mLock = new Object();
    /* access modifiers changed from: private */
    public WindowManagerService mService;
    /* access modifiers changed from: private */
    public GestureThumbnailCallback mThumbnailCallback;
    private Map<WindowState, Float> mWindowAlphaMap = new HashMap();

    public interface GestureThumbnailCallback {
        void onGestureFinished(AppWindowToken appWindowToken);

        void onGestureReady(AppWindowToken appWindowToken);
    }

    GestureStubController(WindowManagerService service, DisplayContent displayContent) {
        this.mService = service;
        this.mDisplayContent = displayContent;
    }

    /* access modifiers changed from: package-private */
    public boolean isAppTransitionSkipped() {
        return this.mIsAppTransitionSkipped;
    }

    /* access modifiers changed from: package-private */
    public boolean keepWallpaperShowing() {
        return this.mKeepWallpaperShowing;
    }

    /* access modifiers changed from: package-private */
    public void disabledAppTransitionSkipped() {
        synchronized (this.mLock) {
            this.mIsAppTransitionSkipped = false;
        }
    }

    /* access modifiers changed from: package-private */
    public void resetSurfaceStatusIfNeeded(AppWindowToken wtoken) {
        if (this.mInvisibleAppTokens.contains(wtoken)) {
            this.mService.mH.removeCallbacks(this.mGestureFinishRunnable);
            reset();
        }
    }

    /* access modifiers changed from: package-private */
    public void registerThumbnailCallback(GestureThumbnailCallback callback) {
        synchronized (this.mLock) {
            this.mThumbnailCallback = callback;
        }
    }

    /* access modifiers changed from: package-private */
    public IGestureStubListener getGestureStubListener() {
        if (this.mGestureStubListener == null) {
            synchronized (this.mLock) {
                if (this.mGestureStubListener == null) {
                    this.mGestureStubListener = new IGestureStubListener.Stub() {
                        public void onGestureReady() {
                            GestureStubController.this.notifyGestureReady();
                        }

                        public void onGestureStart() {
                            GestureStubController.this.notifyGestureStart();
                        }

                        public void onGestureFinish(boolean immediate) {
                            GestureStubController.this.notifyGestureFinish(immediate);
                        }

                        public void skipAppTransition() {
                            GestureStubController.this.notifySkipAppTransition();
                        }
                    };
                }
            }
        }
        return this.mGestureStubListener;
    }

    /* access modifiers changed from: private */
    public void notifyGestureReady() {
        this.mService.mH.removeCallbacks(this.mGestureFinishRunnable);
        reset();
        this.mDisplayContent.forAllWindows((Consumer<WindowState>) new Consumer(this.mDisplayContent.getSplitScreenPrimaryStackIgnoringVisibility(), this.mDisplayContent.getHomeStack()) {
            private final /* synthetic */ TaskStack f$1;
            private final /* synthetic */ TaskStack f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void accept(Object obj) {
                GestureStubController.this.lambda$notifyGestureReady$0$GestureStubController(this.f$1, this.f$2, (WindowState) obj);
            }
        }, true);
        if (this.mThumbnailCallback != null) {
            for (AppWindowToken token : this.mInvisibleAppTokens) {
                this.mThumbnailCallback.onGestureReady(token);
            }
        }
    }

    public /* synthetic */ void lambda$notifyGestureReady$0$GestureStubController(TaskStack dockedStack, TaskStack homeStack, WindowState w) {
        TaskStack stack = w.getStack();
        if (stack != dockedStack && stack != homeStack && w.mWinAnimator != null && w.mWinAnimator.hasSurface() && w.mAppToken != null) {
            synchronized (this.mLock) {
                this.mInvisibleAppTokens.add(w.mAppToken);
                this.mWindowAlphaMap.put(w, Float.valueOf(w.mWinAnimator.mAlpha));
            }
        }
    }

    /* access modifiers changed from: private */
    public void notifyGestureStart() {
        this.mDisplayContent.forAllWindows((Consumer<WindowState>) new Consumer() {
            public final void accept(Object obj) {
                GestureStubController.this.lambda$notifyGestureStart$1$GestureStubController((WindowState) obj);
            }
        }, false);
        synchronized (this.mLock) {
            for (WindowState win : this.mWindowAlphaMap.keySet()) {
                if (!(win == null || win.mWinAnimator == null)) {
                    win.mWinAnimator.mAlpha = 0.0f;
                    this.mService.mH.post(new Runnable() {
                        public void run() {
                            GestureStubController.this.mService.requestTraversal();
                        }
                    });
                }
            }
        }
    }

    public /* synthetic */ void lambda$notifyGestureStart$1$GestureStubController(WindowState w) {
        if (w.mWinAnimator != null && w.mWinAnimator.hasSurface() && w.mIsWallpaper) {
            this.mKeepWallpaperShowing = true;
            this.mService.mH.postDelayed(new Runnable() {
                public void run() {
                    boolean unused = GestureStubController.this.mKeepWallpaperShowing = false;
                }
            }, 500);
            w.mWinAnimator.mSurfaceController.showRobustlyInTransaction();
        }
    }

    /* access modifiers changed from: private */
    public void notifyGestureFinish(boolean immediate) {
        this.mService.mH.postDelayed(this.mGestureFinishRunnable, immediate ? 0 : 2000);
    }

    /* access modifiers changed from: private */
    public void notifySkipAppTransition() {
        synchronized (this.mLock) {
            this.mIsAppTransitionSkipped = true;
        }
    }

    /* access modifiers changed from: private */
    public void reset() {
        synchronized (this.mLock) {
            this.mKeepWallpaperShowing = false;
            for (final AppWindowToken token : this.mInvisibleAppTokens) {
                this.mService.mH.post(new Runnable() {
                    public void run() {
                        if (GestureStubController.this.mThumbnailCallback != null) {
                            GestureStubController.this.mThumbnailCallback.onGestureFinished(token);
                        }
                    }
                });
            }
            for (WindowState win : this.mWindowAlphaMap.keySet()) {
                if (!(win == null || win.mWinAnimator == null)) {
                    win.mWinAnimator.mAlpha = this.mWindowAlphaMap.get(win).floatValue();
                }
            }
            this.mService.mH.post(new Runnable() {
                public void run() {
                    GestureStubController.this.mService.requestTraversal();
                }
            });
            this.mInvisibleAppTokens.clear();
            this.mWindowAlphaMap.clear();
        }
    }
}
