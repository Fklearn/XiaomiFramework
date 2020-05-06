package com.android.server.wm;

import android.graphics.Rect;
import android.util.Slog;
import android.view.animation.Animation;
import com.android.server.wm.IGestureStrategy;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

abstract class MiuiGestureStrategy implements IGestureStrategy {
    private static final String TAG = "MiuiGesture";
    protected volatile boolean mAnimating;
    private final GestureStrategyCallback mCallback;
    final TreeSet<AppWindowToken> mClosingAppTokens = new TreeSet<>();
    private Animation mDefaultAnimation;
    protected final MiuiGestureAnimator mGestureAnimator;
    final MiuiGestureController mGestureController;
    public final MiuiGesturePointerEventListener mPointerEventListener;
    final ConcurrentHashMap<WindowState, IGestureStrategy.WindowStateInfo> mScalingWindows = new ConcurrentHashMap<>();
    protected final WindowManagerService mService;
    private WindowState mTopWindow;

    interface GestureStrategyCallback {
        void onStrategyFinish();
    }

    /* access modifiers changed from: package-private */
    public abstract boolean onAnimationUpdate(long j);

    /* access modifiers changed from: package-private */
    public abstract boolean onCreateAnimation(Rect rect, Rect rect2);

    MiuiGestureStrategy(MiuiGestureAnimator gestureAnimator, WindowManagerService mService2, MiuiGesturePointerEventListener pointerEventListener, MiuiGestureController gestureController, GestureStrategyCallback callback) {
        this.mGestureAnimator = gestureAnimator;
        this.mService = mService2;
        this.mPointerEventListener = pointerEventListener;
        this.mGestureController = gestureController;
        this.mCallback = callback;
    }

    public boolean isAnimating() {
        return this.mAnimating;
    }

    public void onAnimationStart() {
        setAnimating(true);
        FullScreenEventReporter.resetAnimationFrameIntervalParams();
    }

    public void setAnimating(boolean animating) {
        this.mAnimating = animating;
    }

    /* access modifiers changed from: package-private */
    public void setDefaultAnimation(Animation animation) {
        this.mDefaultAnimation = animation;
    }

    /* access modifiers changed from: package-private */
    public Animation getDefaultAnimation() {
        return this.mDefaultAnimation;
    }

    /* access modifiers changed from: package-private */
    public WindowState getTopWindow() {
        return this.mTopWindow;
    }

    /* access modifiers changed from: package-private */
    public void setAppTokenTransformation(AppWindowToken aToken, float alpha, float sx, float sy, float tx, float ty) {
        synchronized (MiuiGestureController.mGestureLock) {
            this.mGestureAnimator.setAlphaInTransaction(aToken, alpha);
            this.mGestureAnimator.setPositionInTransaction(aToken, tx, ty);
            this.mGestureAnimator.setMatrixInTransaction(aToken, sx, 0.0f, 0.0f, sy);
        }
    }

    public final boolean createAnimation(TreeSet<AppWindowToken> closingApps, ConcurrentHashMap<WindowState, IGestureStrategy.WindowStateInfo> scalingWindows, Rect curRect) {
        if (closingApps == null || closingApps.size() == 0) {
            Slog.e("MiuiGesture", "no closing apps");
            return false;
        } else if (curRect == null || curRect.isEmpty()) {
            Slog.e("MiuiGesture", "curRect is null");
            return false;
        } else if (scalingWindows == null || scalingWindows.isEmpty()) {
            Slog.e("MiuiGesture", "scalingWindows is null");
            return false;
        } else {
            this.mTopWindow = (WindowState) closingApps.first().mChildren.peekLast();
            if (this.mTopWindow == null) {
                Slog.e("MiuiGesture", "mTopWindow is null, skip");
                return false;
            }
            this.mClosingAppTokens.clear();
            this.mClosingAppTokens.addAll(closingApps);
            this.mScalingWindows.clear();
            this.mScalingWindows.putAll(scalingWindows);
            DisplayContent displayContent = this.mService.getDefaultDisplayContentLocked();
            int width = displayContent.mBaseDisplayWidth;
            int height = displayContent.mBaseDisplayHeight;
            Rect appFrame = new Rect();
            appFrame.set(this.mTopWindow.getContainingFrame());
            if (MiuiGestureController.DEBUG_PROGRESS) {
                Slog.d("MiuiGesture", "appFrame=" + appFrame + ",curRect=" + curRect + ",width=" + width + ",height=" + height);
            }
            if (onCreateAnimation(appFrame, curRect)) {
                return true;
            }
            finishAnimation();
            return false;
        }
    }

    public final boolean updateAnimation(long frameTimeNs) {
        FullScreenEventReporter.caculateAnimationFrameInterval(getAnimationString());
        boolean more = onAnimationUpdate(frameTimeNs / 1000000);
        this.mGestureAnimator.applyTransaction();
        return more;
    }

    public void finishAnimation() {
        setAnimating(false);
        this.mClosingAppTokens.clear();
        this.mScalingWindows.clear();
        this.mTopWindow = null;
        this.mAnimating = false;
        this.mDefaultAnimation = null;
        GestureStrategyCallback gestureStrategyCallback = this.mCallback;
        if (gestureStrategyCallback != null) {
            gestureStrategyCallback.onStrategyFinish();
        }
    }
}
