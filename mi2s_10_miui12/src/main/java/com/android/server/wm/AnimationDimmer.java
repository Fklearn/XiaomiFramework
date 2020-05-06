package com.android.server.wm;

import android.view.SurfaceControl;
import android.view.animation.Transformation;

class AnimationDimmer {
    private static final String TAG = "AnimationDimmer";
    float alpha = 0.1f;
    boolean isVisible;
    SurfaceControl mDimLayer;
    private WindowContainer mHost;

    public AnimationDimmer(WindowContainer host) {
        this.mHost = host;
    }

    private SurfaceControl makeDimLayer() {
        SurfaceControl.Builder colorLayer = this.mHost.makeChildSurface((WindowContainer) null).setParent(this.mHost.getSurfaceControl()).setColorLayer();
        return colorLayer.setName("Transition Dim Layer for - " + this.mHost.getName()).build();
    }

    private void dim(SurfaceControl.Transaction t, WindowContainer container, int relativeLayer, float alpha2) {
        if (this.mDimLayer == null) {
            this.mDimLayer = makeDimLayer();
        }
        SurfaceControl surfaceControl = this.mDimLayer;
        if (surfaceControl != null) {
            if (container != null) {
                t.setRelativeLayer(surfaceControl, container.getSurfaceControl(), relativeLayer);
            } else {
                t.setLayer(surfaceControl, Integer.MAX_VALUE);
            }
            t.setAlpha(this.mDimLayer, alpha2);
            this.alpha = alpha2;
            t.show(this.mDimLayer);
            this.isVisible = true;
        }
    }

    /* access modifiers changed from: package-private */
    public void stopDim(SurfaceControl.Transaction t) {
        SurfaceControl surfaceControl = this.mDimLayer;
        if (surfaceControl != null) {
            t.hide(surfaceControl);
            this.isVisible = false;
        }
    }

    /* access modifiers changed from: package-private */
    public void dimAbove(SurfaceControl.Transaction t, float alpha2) {
        dim(t, (WindowContainer) null, 1, alpha2);
    }

    /* access modifiers changed from: package-private */
    public void dimAbove(SurfaceControl.Transaction t, WindowContainer container, float alpha2) {
        dim(t, container, 1, alpha2);
    }

    /* access modifiers changed from: package-private */
    public void dimBelow(SurfaceControl.Transaction t, WindowContainer container, float alpha2) {
        dim(t, container, -1, alpha2);
    }

    /* access modifiers changed from: package-private */
    public void setAlpha(SurfaceControl.Transaction t, float alpha2) {
        SurfaceControl surfaceControl = this.mDimLayer;
        if (surfaceControl != null && this.isVisible) {
            t.setAlpha(surfaceControl, alpha2);
        }
    }

    /* access modifiers changed from: package-private */
    public void stepTransitionDim(SurfaceControl.Transaction t, Transformation appTransformation) {
        float f = this.alpha;
        if (f <= 1.0f) {
            this.alpha = f + 0.05f;
            setAlpha(t, this.alpha);
        }
    }
}
