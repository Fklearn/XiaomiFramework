package com.android.server.wm;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.miui.R;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Slog;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceControl;

class TalkbackWatermark {
    private static final boolean DEBUG = false;
    private static final String TAG = "TalkbackWatermark";
    private final float mDetDp = 20.37f;
    private final int mDetPx;
    private final Display mDisplay;
    private boolean mDrawNeeded = false;
    private boolean mHasDrawn = false;
    private int mLastDH = 0;
    private int mLastDW = 0;
    private final float mPaddingDp = 12.36f;
    private final int mPaddingPx;
    private final String mString1;
    private final String mString2;
    private Surface mSurface = new Surface();
    private SurfaceControl mSurfaceControl = null;
    private final float mTextSizeDp = 20.0f;
    private final int mTextSizePx;
    private final float mTitleSizeDp = 25.45f;
    private final float mTitleSizePx;
    private final float mXProportion = 0.5f;
    private final float mYProportionTop = 0.4f;

    TalkbackWatermark(DisplayContent dc, DisplayMetrics dm, Context context) {
        this.mString1 = context.getResources().getString(R.string.talk_back_water_mark_string1);
        this.mString2 = context.getResources().getString(R.string.talk_back_water_mark_string2);
        this.mDisplay = dc.getDisplay();
        float constNum = ((float) dm.densityDpi) / 160.0f;
        this.mTextSizePx = (int) (20.0f * constNum);
        this.mDetPx = (int) (20.37f * constNum);
        this.mPaddingPx = (int) (12.36f * constNum);
        this.mTitleSizePx = (float) ((int) (25.45f * constNum));
        SurfaceControl ctrl = null;
        try {
            ctrl = dc.makeOverlay().setName("TalkbackWatermarkSurface").setBufferSize(1, 1).setFormat(-3).build();
            ctrl.setLayerStack(this.mDisplay.getLayerStack());
            ctrl.setLayer(1000000);
            ctrl.setPosition(0.0f, 0.0f);
            ctrl.show();
            this.mSurface.copyFrom(ctrl);
        } catch (Surface.OutOfResourcesException e) {
            Slog.w(TAG, "createrSurface e" + e);
        }
        this.mSurfaceControl = ctrl;
    }

    /* access modifiers changed from: package-private */
    public void positionSurface(int dw, int dh) {
        if (this.mLastDW != dw || this.mLastDH != dh) {
            this.mLastDW = dw;
            this.mLastDH = dh;
            this.mSurfaceControl.setBufferSize(dw, dh);
            this.mDrawNeeded = true;
        }
    }

    private void drawIfNeeded() {
        if (this.mDrawNeeded) {
            int dw = this.mLastDW;
            int dh = this.mLastDH;
            Canvas c = null;
            try {
                c = this.mSurface.lockCanvas(new Rect(0, 0, dw, dh));
            } catch (Surface.OutOfResourcesException | IllegalArgumentException e) {
            }
            if (c != null && c.getWidth() == dw && c.getHeight() == dh) {
                this.mDrawNeeded = false;
                c.drawColor(0, PorterDuff.Mode.CLEAR);
                int x = (int) (((float) dw) * 0.5f);
                int y = ((int) (((float) dh) * 0.4f)) + 60;
                Paint paint = new Paint(1);
                paint.setTextSize(this.mTitleSizePx);
                paint.setTypeface(Typeface.create(Typeface.SANS_SERIF, 0));
                paint.setColor(-5000269);
                paint.setTextAlign(Paint.Align.CENTER);
                c.drawText(this.mString1, (float) x, (float) y, paint);
                paint.setTextSize((float) this.mTextSizePx);
                StaticLayout staticLayout = new StaticLayout(this.mString2, new TextPaint(paint), c.getWidth() - this.mPaddingPx, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                c.save();
                c.translate((float) x, (float) (this.mDetPx + y));
                staticLayout.draw(c);
                c.restore();
                this.mSurface.unlockCanvasAndPost(c);
                this.mHasDrawn = true;
                return;
            }
            this.mSurface.unlockCanvasAndPost(c);
        }
    }

    /* access modifiers changed from: package-private */
    public void setVisibility(boolean show) {
        setVisibility(show, true);
    }

    /* access modifiers changed from: package-private */
    public void setVisibility(boolean show, boolean destroy) {
        if (show) {
            drawIfNeeded();
            if (this.mHasDrawn) {
                this.mSurfaceControl.show();
                return;
            }
            return;
        }
        SurfaceControl surfaceControl = this.mSurfaceControl;
        if (surfaceControl != null) {
            surfaceControl.hide();
            if (destroy) {
                this.mSurface.destroy();
                this.mSurfaceControl.remove();
                this.mSurfaceControl = null;
                this.mSurface = null;
                this.mHasDrawn = false;
            }
        }
    }
}
