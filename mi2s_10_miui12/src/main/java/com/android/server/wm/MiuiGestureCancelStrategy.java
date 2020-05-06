package com.android.server.wm;

import android.graphics.Rect;
import com.android.server.wm.MiuiGestureStrategy;

public class MiuiGestureCancelStrategy extends MiuiGestureSimpleStrategy {
    public /* bridge */ /* synthetic */ void finishAnimation() {
        super.finishAnimation();
    }

    public /* bridge */ /* synthetic */ boolean isAnimating() {
        return super.isAnimating();
    }

    public /* bridge */ /* synthetic */ void onAnimationStart() {
        super.onAnimationStart();
    }

    public /* bridge */ /* synthetic */ void setAnimating(boolean z) {
        super.setAnimating(z);
    }

    public MiuiGestureCancelStrategy(MiuiGestureAnimator mSurfaceController, WindowManagerService mService, MiuiGesturePointerEventListener pointerEventListener, MiuiGestureController gestureController, MiuiGestureStrategy.GestureStrategyCallback callback) {
        super(mSurfaceController, mService, pointerEventListener, gestureController, callback);
    }

    /* access modifiers changed from: package-private */
    public boolean onCreateAnimation(Rect appFrame, Rect curRect) {
        if (appFrame == null) {
            return false;
        }
        setTargetRect(new Rect(0, 0, appFrame.width(), appFrame.height()));
        return super.onCreateAnimation(appFrame, curRect);
    }

    public int getAnimationType() {
        return 2;
    }
}
