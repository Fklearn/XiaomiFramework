package com.android.server.wm;

import android.content.Intent;
import android.graphics.Rect;
import android.os.UserHandle;
import android.util.Slog;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import com.android.server.MiuiFgThread;
import com.android.server.wm.MiuiGestureController;
import com.android.server.wm.MiuiGestureStrategy;
import com.miui.internal.transition.MiuiAppTransitionAnimationSpec;
import java.util.Iterator;

public class MiuiGestureHomeStrategy extends MiuiGestureAnimationStrategy {
    private static final int DELAYED_NOTIFY_TO_HOME = 350;
    private static String TAG = "MiuiGesture";
    private AppWindowToken mHomeAppToken;
    private WindowState mHomeWindow = null;
    private boolean mIsMiuiHome;
    private Animation mOpenAnimation;
    private final Transformation mOpenTransformation = new Transformation();
    private volatile MiuiAppTransitionAnimationSpec mSpec;

    public /* bridge */ /* synthetic */ boolean isAnimating() {
        return super.isAnimating();
    }

    public /* bridge */ /* synthetic */ void onAnimationStart() {
        super.onAnimationStart();
    }

    public /* bridge */ /* synthetic */ void setAnimating(boolean z) {
        super.setAnimating(z);
    }

    public MiuiGestureHomeStrategy(MiuiGestureAnimator mSurfaceController, WindowManagerService mService, MiuiGesturePointerEventListener pointerEventListener, MiuiGestureController gestureAnimator, MiuiGestureStrategy.GestureStrategyCallback callback) {
        super(mSurfaceController, mService, pointerEventListener, gestureAnimator, callback);
    }

    public void setHomeAppToken(AppWindowToken appToken) {
        this.mHomeAppToken = appToken;
        this.mHomeWindow = appToken.findMainWindow(false);
        this.mIsMiuiHome = this.mGestureController.isMiuiHomeAppToken(appToken);
    }

    public boolean onCreateAnimation(Rect appFrame, Rect curRect) {
        if (this.mHomeAppToken == null || this.mHomeWindow == null) {
            Slog.w(TAG, "mHomeWindow is null");
        }
        if (MiuiGestureController.DEBUG_HOME) {
            String str = TAG;
            Slog.w(str, getAnimationString() + ":onCreateAnimation, curRect=" + curRect);
        }
        this.mGestureAnimator.createLeash(this.mHomeAppToken);
        Rect positionRect = new Rect();
        int displayWidth = MiuiGestureDetector.sScreenWidth;
        int displayHeight = MiuiGestureDetector.sScreenHeight;
        if (MiuiGestureDetector.isPortrait()) {
            if (this.mSpec != null && this.mSpec.mBitmap != null) {
                positionRect.set(this.mSpec.mRect);
                if (!positionRect.isEmpty() && this.mGestureAnimator.createThumbnail(getTopWindow().mAppToken, getTopWindow(), this.mSpec.mBitmap)) {
                    AppTransitionInjector.notifyMiuiAnimationStart(this.mGestureController.mMiuiAppTransitionAnimationHelper);
                }
            } else if (this.mIsMiuiHome) {
                Slog.e(TAG, "fail to get mSpec!");
            }
        }
        if (MiuiGestureController.DEBUG_HOME) {
            String str2 = TAG;
            Slog.d(str2, "create close animation: appFrame=" + appFrame + ",positionRect=" + positionRect + ",closingAppStartRect" + curRect);
        }
        Animation closeAnimation = AppTransitionInjector.createTransitionAnimation(false, appFrame, positionRect, curRect, 1, MiuiGestureDetector.calculateInertia());
        if (closeAnimation != null) {
            if (MiuiGestureController.DEBUG_HOME) {
                Slog.d(TAG, "create go home animation successfully");
            }
            int containingWidth = appFrame.width();
            int containingHeight = appFrame.height();
            if (!closeAnimation.isInitialized()) {
                closeAnimation.initialize(containingWidth, containingHeight, displayWidth, displayHeight);
            }
            closeAnimation.scaleCurrentDuration(this.mService.getTransitionAnimationScaleLocked());
        }
        setDefaultAnimation(closeAnimation);
        WindowState windowState = this.mHomeWindow;
        if (windowState != null) {
            appFrame.set(windowState.getContainingFrame());
            this.mGestureController.startGestureAnimation(this.mHomeWindow.mWinAnimator);
        }
        launchHome();
        if (this.mHomeAppToken != null && this.mGestureController.isMiuiHomeAppToken(this.mHomeAppToken)) {
            this.mOpenAnimation = AppTransitionInjector.createWallPaperOpenAnimation(true, appFrame, positionRect, curRect);
            if (this.mOpenAnimation != null) {
                int containingWidth2 = appFrame.width();
                int containingHeight2 = appFrame.height();
                if (!this.mOpenAnimation.isInitialized()) {
                    this.mOpenAnimation.initialize(containingWidth2, containingHeight2, displayWidth, displayHeight);
                }
                this.mOpenAnimation.scaleCurrentDuration(this.mService.getTransitionAnimationScaleLocked());
            }
        }
        this.mGestureController.mHandler.postDelayed(new Runnable() {
            public final void run() {
                MiuiGestureHomeStrategy.this.lambda$onCreateAnimation$0$MiuiGestureHomeStrategy();
            }
        }, (long) (this.mService.getTransitionAnimationScaleLocked() * 350.0f));
        return true;
    }

    public /* synthetic */ void lambda$onCreateAnimation$0$MiuiGestureHomeStrategy() {
        AppTransitionInjector.notifyMiuiAnimationEnd(this.mService.mMiuiAppTransitionAnimationHelper);
    }

    public boolean onAnimationUpdate(long currentTime) {
        try {
            boolean moreAnimation = super.onAnimationUpdate(currentTime);
            this.mGestureAnimator.stepThumbnailAnimationIfNeeded(getDefaultTransformation());
            if (this.mOpenAnimation == null) {
                return moreAnimation;
            }
            this.mOpenTransformation.clear();
            boolean hasMoreFrames = this.mOpenAnimation.getTransformation(currentTime, this.mOpenTransformation);
            if (this.mHomeWindow != null) {
                stepAnimation(this.mOpenTransformation, this.mHomeWindow);
                WindowSurfaceController surfaceController = this.mHomeWindow.mWinAnimator.mSurfaceController;
                if (surfaceController != null && !surfaceController.getShown() && MiuiGestureDetector.isPortrait()) {
                    if (surfaceController.showRobustlyInTransaction()) {
                        this.mHomeWindow.mWinAnimator.markPreservedSurfaceForDestroy();
                        this.mHomeWindow.mWinAnimator.mAnimator.requestRemovalOfReplacedWindows(this.mHomeWindow);
                        this.mHomeWindow.mWinAnimator.mLastHidden = false;
                        if (!this.mHomeWindow.getDisplayContent().getLastHasContent()) {
                            this.mHomeWindow.mWinAnimator.mAnimator.setPendingLayoutChanges(this.mHomeWindow.getDisplayId(), 8);
                        }
                    } else {
                        this.mHomeWindow.setOrientationChanging(false);
                    }
                }
            }
            return moreAnimation | hasMoreFrames;
        } catch (Exception e) {
            Slog.e(TAG, "Go home animating exception");
            e.printStackTrace();
            return false;
        }
    }

    public void finishAnimation() {
        FullScreenEventReporter.recordJankyFrames(getAnimationString(), getTopWindow().mAttrs.packageName);
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                Iterator it = this.mClosingAppTokens.iterator();
                while (it.hasNext()) {
                    AppWindowToken token = (AppWindowToken) it.next();
                    DisplayContent dc = token.getDisplayContent();
                    if (!token.inPinnedWindowingMode()) {
                        if (dc.mOpeningApps.contains(token)) {
                            String str = TAG;
                            Slog.d(str, "finishAnimation mOpeningApps don't hide = " + token);
                        } else {
                            if (this.mPointerEventListener.cancelGoHomeAppWindowToken.contains(token)) {
                                String str2 = TAG;
                                Slog.d(str2, "finishAnimation cancelGoHomeAppWindowToken don't hide = " + token);
                            }
                            String str3 = TAG;
                            Slog.d(str3, "finishAnimation hideWindow = " + token);
                            this.mGestureAnimator.hideWindow(token);
                        }
                    }
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (Throwable th) {
                while (true) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        AppWindowToken appWindowToken = this.mHomeAppToken;
        if (appWindowToken != null) {
            setAppTokenTransformation(appWindowToken, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f);
            this.mGestureAnimator.applyTransaction();
        }
        this.mGestureController.notifyGestureFinish(true);
        reset();
        super.finishAnimation();
    }

    public int getAnimationType() {
        return 0;
    }

    private void reset() {
        synchronized (MiuiGestureController.mGestureLock) {
            if (this.mHomeWindow != null) {
                this.mGestureController.stopGestureAnimation(this.mHomeWindow.mWinAnimator);
            }
            this.mHomeWindow = null;
            this.mHomeAppToken = null;
            this.mOpenAnimation = null;
            this.mSpec = null;
            this.mIsMiuiHome = false;
        }
    }

    /* access modifiers changed from: package-private */
    public void launchHome() {
        if (this.mHomeAppToken != null) {
            this.mGestureController.setSkipAppTransition();
            try {
                this.mService.mH.postAtFrontOfQueue(new Runnable() {
                    public final void run() {
                        MiuiGestureHomeStrategy.this.lambda$launchHome$1$MiuiGestureHomeStrategy();
                    }
                });
            } catch (Exception e) {
                Slog.d(TAG, e.toString());
            }
        } else {
            Intent homeIntent = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.HOME");
            homeIntent.addFlags(268500992);
            try {
                this.mService.mH.postAtFrontOfQueue(new Runnable(homeIntent) {
                    private final /* synthetic */ Intent f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        MiuiGestureHomeStrategy.this.lambda$launchHome$2$MiuiGestureHomeStrategy(this.f$1);
                    }
                });
            } catch (Exception e2) {
                Slog.d(TAG, e2.toString());
            }
        }
    }

    public /* synthetic */ void lambda$launchHome$1$MiuiGestureHomeStrategy() {
        this.mGestureController.launchHome(this.mHomeAppToken);
    }

    public /* synthetic */ void lambda$launchHome$2$MiuiGestureHomeStrategy(Intent homeIntent) {
        this.mService.mAtmService.stopAppSwitches();
        this.mService.mContext.startActivityAsUser(homeIntent, UserHandle.CURRENT);
    }

    /* access modifiers changed from: package-private */
    public void getSpec(AppWindowToken topToken) {
        if (topToken != null && this.mHomeAppToken != null && this.mIsMiuiHome) {
            MiuiGestureController.MiuiLaunchIconInfo iconInfo = new MiuiGestureController.MiuiLaunchIconInfo();
            iconInfo.launchIconName = topToken.mActivityRecord.realComponentName;
            iconInfo.userId = topToken.mActivityRecord.mUserId;
            if (MiuiGestureController.DEBUG_HOME) {
                String str = TAG;
                Slog.d(str, "try to get iconInfo of " + iconInfo.launchIconName);
            }
            MiuiFgThread.getHandler().postAtFrontOfQueue(new Runnable(iconInfo) {
                private final /* synthetic */ MiuiGestureController.MiuiLaunchIconInfo f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MiuiGestureHomeStrategy.this.lambda$getSpec$3$MiuiGestureHomeStrategy(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$getSpec$3$MiuiGestureHomeStrategy(MiuiGestureController.MiuiLaunchIconInfo iconInfo) {
        this.mSpec = this.mGestureController.getLaunchIconInfo(iconInfo);
    }
}
