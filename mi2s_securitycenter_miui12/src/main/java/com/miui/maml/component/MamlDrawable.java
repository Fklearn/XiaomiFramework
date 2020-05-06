package com.miui.maml.component;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.miui.maml.RenderUpdater;
import com.miui.maml.ScreenElementRoot;

@Deprecated
public class MamlDrawable extends Drawable {
    private static final boolean DBG = true;
    private static final String LOG_TAG = "MamlDrawable";
    private static final int RENDER_TIMEOUT = 100;
    /* access modifiers changed from: private */
    public Handler mHandler;
    private int mHeight;
    private int mIntrinsicHeight;
    private int mIntrinsicWidth;
    /* access modifiers changed from: private */
    public Runnable mInvalidateSelf;
    private boolean mPaused;
    /* access modifiers changed from: private */
    public Runnable mRenderTimeout;
    private ScreenElementRoot mRoot;
    private RenderUpdater mUpdater;
    private int mWidth;

    public MamlDrawable(ScreenElementRoot screenElementRoot) {
        this(screenElementRoot, false);
    }

    public MamlDrawable(ScreenElementRoot screenElementRoot, boolean z) {
        this.mPaused = true;
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mRenderTimeout = new Runnable() {
            public void run() {
                MamlDrawable.this.doPause();
            }
        };
        this.mInvalidateSelf = new Runnable() {
            public void run() {
                MamlDrawable.this.invalidateSelf();
            }
        };
        this.mRoot = screenElementRoot;
        setIntrinsicSize((int) this.mRoot.getWidth(), (int) this.mRoot.getHeight());
        this.mUpdater = new RenderUpdater(this.mRoot, new Handler(), z) {
            public void doRenderImp() {
                MamlDrawable.this.mHandler.removeCallbacks(MamlDrawable.this.mRenderTimeout);
                MamlDrawable.this.mHandler.postDelayed(MamlDrawable.this.mRenderTimeout, 100);
                MamlDrawable.this.mHandler.post(MamlDrawable.this.mInvalidateSelf);
            }
        };
        this.mUpdater.init();
        this.mUpdater.runUpdater();
    }

    /* access modifiers changed from: private */
    public void doPause() {
        if (!this.mPaused) {
            logd("doPause: ");
            this.mPaused = true;
            this.mUpdater.onPause();
        }
    }

    private void doResume() {
        if (this.mPaused) {
            logd("doResume: ");
            this.mPaused = false;
            this.mUpdater.onResume();
        }
    }

    private void logd(CharSequence charSequence) {
        Log.d(LOG_TAG, charSequence + "  [" + toString() + "]");
    }

    public void cleanUp() {
        logd("cleanUp: ");
        this.mUpdater.cleanUp();
    }

    public void draw(Canvas canvas) {
        String str;
        this.mHandler.removeCallbacks(this.mRenderTimeout);
        doResume();
        try {
            int save = canvas.save();
            canvas.translate((float) getBounds().left, (float) getBounds().top);
            canvas.scale(((float) this.mWidth) / ((float) this.mIntrinsicWidth), ((float) this.mHeight) / ((float) this.mIntrinsicHeight), 0.0f, 0.0f);
            this.mRoot.render(canvas);
            canvas.restoreToCount(save);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            str = e.toString();
        } catch (OutOfMemoryError e2) {
            e2.printStackTrace();
            str = e2.toString();
        }
        Log.e(LOG_TAG, str);
    }

    public int getIntrinsicHeight() {
        return this.mIntrinsicHeight;
    }

    public int getIntrinsicWidth() {
        return this.mIntrinsicWidth;
    }

    public int getOpacity() {
        return -3;
    }

    public ScreenElementRoot getRoot() {
        return this.mRoot;
    }

    public void setAlpha(int i) {
    }

    public MamlDrawable setAutoCleanup(boolean z) {
        this.mUpdater.setAutoCleanup(z);
        return this;
    }

    public void setBounds(int i, int i2, int i3, int i4) {
        super.setBounds(i, i2, i3, i4);
        this.mWidth = i3 - i;
        this.mHeight = i4 - i2;
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public void setIntrinsicSize(int i, int i2) {
        this.mIntrinsicWidth = i;
        this.mIntrinsicHeight = i2;
    }
}
