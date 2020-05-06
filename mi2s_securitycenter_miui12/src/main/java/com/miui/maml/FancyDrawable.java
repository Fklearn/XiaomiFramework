package com.miui.maml;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.miui.maml.MamlDrawable;
import com.miui.maml.RendererController;
import com.miui.maml.ScreenElementRootFactory;
import com.miui.maml.util.Utils;

public class FancyDrawable extends MamlDrawable implements RendererController.IRenderable {
    private static final boolean DBG = true;
    private static final String LOG_TAG = "FancyDrawable";
    private static final String QUIET_IMAGE_NAME = "quietImage.png";
    private static final int RENDER_TIMEOUT = 100;
    private static final String START_IMAGE_NAME = "startImage.png";
    private static final String USE_QUIET_IMAGE_TAG = "useQuietImage";
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Object mPauseLock = new Object();
    private boolean mPaused;
    private Drawable mQuietDrawable;
    private Runnable mRenderTimeout = new Runnable() {
        public void run() {
            boolean unused = FancyDrawable.this.mTimeOut = true;
            FancyDrawable.this.doPause();
        }
    };
    private RendererCore mRendererCore;
    private Drawable mStartDrawable;
    /* access modifiers changed from: private */
    public boolean mTimeOut;

    static final class FancyDrawableState extends MamlDrawable.MamlDrawableState {
        RendererCore mRendererCore;

        public FancyDrawableState(RendererCore rendererCore) {
            this.mRendererCore = rendererCore;
        }

        /* access modifiers changed from: protected */
        public MamlDrawable createDrawable() {
            return new FancyDrawable(this.mRendererCore);
        }
    }

    public FancyDrawable(RendererCore rendererCore) {
        init(rendererCore);
    }

    public FancyDrawable(ScreenElementRoot screenElementRoot, RenderThread renderThread) {
        init(screenElementRoot, renderThread);
    }

    /* access modifiers changed from: private */
    public void doPause() {
        synchronized (this.mPauseLock) {
            if (!this.mPaused) {
                logd("doPause: ");
                this.mPaused = true;
                this.mRendererCore.pauseRenderable(this);
            }
        }
    }

    private void doResume() {
        synchronized (this.mPauseLock) {
            if (this.mPaused) {
                logd("doResume: ");
                this.mPaused = false;
                this.mRendererCore.resumeRenderable(this);
            }
        }
    }

    public static FancyDrawable fromZipFile(Context context, String str) {
        return fromZipFile(context, str, RenderThread.globalThread(true));
    }

    public static FancyDrawable fromZipFile(Context context, String str, RenderThread renderThread) {
        ScreenElementRoot create = ScreenElementRootFactory.create(new ScreenElementRootFactory.Parameter(context, str));
        if (create == null) {
            return null;
        }
        create.setDefaultFramerate(0.0f);
        RendererCore rendererCore = create.load() ? new RendererCore(create, renderThread) : null;
        if (rendererCore == null) {
            return null;
        }
        return new FancyDrawable(rendererCore);
    }

    private void init(RendererCore rendererCore) {
        if (rendererCore != null) {
            this.mState = new FancyDrawableState(rendererCore);
            this.mRendererCore = rendererCore;
            this.mRendererCore.addRenderable(this);
            setIntrinsicSize((int) this.mRendererCore.getRoot().getWidth(), (int) this.mRendererCore.getRoot().getHeight());
            ScreenContext context = this.mRendererCore.getRoot().getContext();
            this.mQuietDrawable = context.mResourceManager.getDrawable(context.mContext.getResources(), QUIET_IMAGE_NAME);
            Drawable drawable = this.mQuietDrawable;
            if (drawable != null) {
                this.mQuietDrawable = drawable.mutate();
                Drawable drawable2 = this.mQuietDrawable;
                drawable2.setBounds(0, 0, drawable2.getIntrinsicWidth(), this.mQuietDrawable.getIntrinsicHeight());
            }
            this.mStartDrawable = context.mResourceManager.getDrawable(context.mContext.getResources(), START_IMAGE_NAME);
            Drawable drawable3 = this.mStartDrawable;
            if (drawable3 != null) {
                this.mStartDrawable = drawable3.mutate();
                Drawable drawable4 = this.mStartDrawable;
                drawable4.setBounds(0, 0, drawable4.getIntrinsicWidth(), this.mStartDrawable.getIntrinsicHeight());
                return;
            }
            return;
        }
        throw new NullPointerException();
    }

    private void init(ScreenElementRoot screenElementRoot, RenderThread renderThread) {
        logd("init  root:" + screenElementRoot.toString());
        init(new RendererCore(screenElementRoot, renderThread));
    }

    private void logd(CharSequence charSequence) {
        Log.d(LOG_TAG, charSequence + "  [" + toString() + "]");
    }

    public void cleanUp() {
        logd("cleanUp: ");
        this.mRendererCore.removeRenderable(this);
    }

    public void doRender() {
        this.mHandler.removeCallbacks(this.mRenderTimeout);
        this.mHandler.postDelayed(this.mRenderTimeout, 100);
        this.mHandler.post(this.mInvalidateSelf);
    }

    /* access modifiers changed from: protected */
    public void drawIcon(Canvas canvas) {
        this.mHandler.removeCallbacks(this.mRenderTimeout);
        if (this.mTimeOut) {
            doResume();
            this.mTimeOut = false;
        }
        try {
            int save = canvas.save();
            canvas.translate((float) getBounds().left, (float) getBounds().top);
            canvas.scale(((float) this.mWidth) / ((float) this.mIntrinsicWidth), ((float) this.mHeight) / ((float) this.mIntrinsicHeight), 0.0f, 0.0f);
            if (Utils.getVariableNumber(USE_QUIET_IMAGE_TAG, this.mRendererCore.getRoot().getVariables()) <= 0.0d || this.mQuietDrawable == null) {
                this.mRendererCore.render(canvas);
            } else {
                this.mQuietDrawable.draw(canvas);
            }
            canvas.restoreToCount(save);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: protected */
    public void finalize() {
        cleanUp();
        super.finalize();
    }

    public int getIntrinsicHeight() {
        return this.mIntrinsicHeight;
    }

    public int getIntrinsicWidth() {
        return this.mIntrinsicWidth;
    }

    public Drawable getQuietDrawable() {
        return this.mQuietDrawable;
    }

    public ScreenElementRoot getRoot() {
        return this.mRendererCore.getRoot();
    }

    public Drawable getStartDrawable() {
        return this.mStartDrawable;
    }

    public void onPause() {
        getRoot().onCommand("pause");
        doPause();
        this.mHandler.removeCallbacks(this.mRenderTimeout);
    }

    public void onResume() {
        getRoot().onCommand("resume");
        doResume();
    }

    public void setAlpha(int i) {
        Drawable drawable = this.mQuietDrawable;
        if (drawable != null) {
            drawable.setAlpha(i);
        }
        Drawable drawable2 = this.mStartDrawable;
        if (drawable2 != null) {
            drawable2.setAlpha(i);
        }
    }

    public void setBadgeInfo(Drawable drawable, Rect rect) {
        if (rect == null || (rect.left >= 0 && rect.top >= 0 && rect.width() <= this.mIntrinsicWidth && rect.height() <= this.mIntrinsicHeight)) {
            this.mBadgeDrawable = drawable;
            this.mBadgeLocation = rect;
            MamlDrawable.MamlDrawableState mamlDrawableState = this.mState;
            mamlDrawableState.mStateBadgeDrawable = drawable;
            mamlDrawableState.mStateBadgeLocation = rect;
            return;
        }
        throw new IllegalArgumentException("Badge location " + rect + " not in badged drawable bounds " + new Rect(0, 0, this.mIntrinsicWidth, this.mIntrinsicHeight));
    }

    public void setColorFilter(ColorFilter colorFilter) {
        super.setColorFilter(colorFilter);
        Log.d(LOG_TAG, "setColorFilter");
        Drawable drawable = this.mQuietDrawable;
        if (drawable != null) {
            drawable.setColorFilter(colorFilter);
        }
        Drawable drawable2 = this.mStartDrawable;
        if (drawable2 != null) {
            drawable2.setColorFilter(colorFilter);
        }
        Drawable drawable3 = this.mBadgeDrawable;
        if (drawable3 != null) {
            drawable3.setColorFilter(colorFilter);
        }
        RendererCore rendererCore = this.mRendererCore;
        if (rendererCore != null) {
            rendererCore.setColorFilter(colorFilter);
        }
    }
}
