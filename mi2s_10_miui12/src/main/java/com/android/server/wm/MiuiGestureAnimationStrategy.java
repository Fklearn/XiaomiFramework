package com.android.server.wm;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.Slog;
import android.view.animation.Transformation;
import com.android.server.wm.MiuiGestureStrategy;
import java.util.Iterator;

abstract class MiuiGestureAnimationStrategy extends MiuiGestureStrategy {
    private final String TAG = "MiuiGesture";
    private final Transformation mDefaultTransformation = new Transformation();

    MiuiGestureAnimationStrategy(MiuiGestureAnimator surfaceController, WindowManagerService service, MiuiGesturePointerEventListener pointerEventListener, MiuiGestureController gestureAnimator, MiuiGestureStrategy.GestureStrategyCallback callback) {
        super(surfaceController, service, pointerEventListener, gestureAnimator, callback);
    }

    /* access modifiers changed from: package-private */
    public Transformation getDefaultTransformation() {
        return this.mDefaultTransformation;
    }

    /* access modifiers changed from: package-private */
    public boolean onAnimationUpdate(long currentTimeMs) {
        if (getDefaultAnimation() == null) {
            return false;
        }
        boolean more = getDefaultAnimation().getTransformation(currentTimeMs, this.mDefaultTransformation);
        stepAnimation(this.mDefaultTransformation);
        return more;
    }

    /* access modifiers changed from: protected */
    public void stepAnimation(Transformation t, WindowState w) {
        WindowState windowState = w;
        float[] tmp = new float[9];
        Matrix tmpMatrix = new Matrix();
        tmpMatrix.reset();
        tmpMatrix.postConcat(t.getMatrix());
        Rect appFrame = w.getContainingFrame();
        tmpMatrix.postTranslate((float) appFrame.left, (float) appFrame.top);
        tmpMatrix.getValues(tmp);
        float alpha = t.getAlpha();
        float scaleX = tmp[0];
        float scaleY = tmp[4];
        float tx = tmp[2];
        float ty = tmp[5];
        setAppTokenTransformation(windowState.mAppToken, alpha, scaleX, scaleY, tx, ty);
        if (MiuiGestureController.DEBUG_STEP) {
            Slog.d("MiuiGesture", "During " + getAnimationString() + ", set " + windowState.mAppToken + ": alpha=" + alpha + " ,scaleX=" + scaleX + " ,scaleY=" + scaleY + " ,tx=" + tx + " ,ty=" + ty);
        }
    }

    /* access modifiers changed from: protected */
    public void stepAnimation(Transformation t) {
        float[] tmp = new float[9];
        Matrix tmpMatrix = new Matrix();
        tmpMatrix.reset();
        tmpMatrix.postConcat(t.getMatrix());
        Rect appFrame = getTopWindow().getContainingFrame();
        tmpMatrix.postTranslate((float) appFrame.left, (float) appFrame.top);
        tmpMatrix.getValues(tmp);
        float alpha = t.getAlpha();
        float scaleX = tmp[0];
        float scaleY = tmp[4];
        float tx = tmp[2];
        float ty = tmp[5];
        Iterator it = this.mClosingAppTokens.iterator();
        while (it.hasNext()) {
            AppWindowToken aToken = (AppWindowToken) it.next();
            if (MiuiGestureController.DEBUG_STEP) {
                Slog.d("MiuiGesture", "During " + getAnimationString() + ", set " + aToken + ": alpha=" + alpha + " ,scaleX=" + scaleX + " ,scaleY=" + scaleY + " ,tx=" + tx + " ,ty=" + ty);
            }
            AppWindowToken appWindowToken = aToken;
            setAppTokenTransformation(aToken, alpha, scaleX, scaleY, tx, ty);
        }
    }
}
