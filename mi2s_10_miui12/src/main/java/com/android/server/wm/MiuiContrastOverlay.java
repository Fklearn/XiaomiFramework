package com.android.server.wm;

import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.DisplayInfo;
import android.view.Surface;
import android.view.SurfaceControl;

public class MiuiContrastOverlay {
    private String TAG = "MiuiContrastOverlay";
    private int mDh = 0;
    private final Display mDisplay;
    private int mDw = 0;
    private SurfaceControl mSurfaceControl;

    MiuiContrastOverlay(DisplayContent dc, DisplayMetrics dm, Context mcontext) {
        this.mDisplay = dc.getDisplay();
        DisplayInfo defaultInfo = dc.getDisplayInfo();
        this.mDw = defaultInfo.logicalWidth;
        this.mDh = defaultInfo.logicalHeight;
        SurfaceControl control = null;
        float f = ((float) dm.densityDpi) / 160.0f;
        try {
            control = dc.makeOverlay().setName("MiuiContrastOverlay").setOpaque(true).setColorLayer().build();
            control.setLayerStack(this.mDisplay.getLayerStack());
            control.setLayer(999999);
            control.setPosition(0.0f, 0.0f);
            control.show();
        } catch (Surface.OutOfResourcesException e) {
            String str = this.TAG;
            Log.d(str, "createSurface e " + e);
        }
        this.mSurfaceControl = control;
    }

    /* access modifiers changed from: package-private */
    public void positionSurface(int dw, int dh) {
        if (dw != this.mDw || dh != this.mDh) {
            this.mDw = dw;
            this.mDh = dh;
            this.mSurfaceControl.setWindowCrop(new Rect(0, 0, this.mDw, this.mDh));
        }
    }

    /* access modifiers changed from: package-private */
    public void showContrastOverlay(float alpha) {
        this.mSurfaceControl.setColor(new float[]{0.0f, 0.0f, 0.0f});
        setAlpha(alpha);
        this.mSurfaceControl.setWindowCrop(new Rect(0, 0, this.mDw, this.mDh));
        this.mSurfaceControl.show();
    }

    public void setAlpha(float alpha) {
        this.mSurfaceControl.setAlpha(alpha);
    }

    /* access modifiers changed from: package-private */
    public void hideContrastOverlay() {
        SurfaceControl surfaceControl = this.mSurfaceControl;
        if (surfaceControl != null) {
            surfaceControl.hide();
            this.mSurfaceControl.remove();
            this.mSurfaceControl = null;
        }
    }
}
