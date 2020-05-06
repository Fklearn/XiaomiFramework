package com.miui.maml;

import android.os.Handler;
import android.os.SystemClock;
import com.miui.maml.RendererController;

@Deprecated
public abstract class RenderUpdater implements RendererController.ISelfUpdateRenderable {
    private boolean mAutoCleanup;
    private long mCreateTime;
    private long mDelay;
    /* access modifiers changed from: private */
    public Handler mHandler;
    protected long mLastUpdateTime;
    protected long mNextUpdateInterval;
    /* access modifiers changed from: private */
    public boolean mPaused;
    protected boolean mPendingRender;
    /* access modifiers changed from: private */
    public ScreenElementRoot mRoot;
    private Runnable mRunUpdater;
    /* access modifiers changed from: private */
    public boolean mSignaled;
    /* access modifiers changed from: private */
    public boolean mStarted;
    /* access modifiers changed from: private */
    public Runnable mUpdater;

    public RenderUpdater(ScreenElementRoot screenElementRoot, Handler handler) {
        this(screenElementRoot, handler, false);
    }

    public RenderUpdater(ScreenElementRoot screenElementRoot, Handler handler, boolean z) {
        this.mUpdater = new Runnable() {
            public void run() {
                boolean unused = RenderUpdater.this.mSignaled = false;
                long elapsedRealtime = SystemClock.elapsedRealtime();
                RenderUpdater renderUpdater = RenderUpdater.this;
                renderUpdater.mNextUpdateInterval = renderUpdater.mRoot.updateIfNeeded(elapsedRealtime);
                RenderUpdater renderUpdater2 = RenderUpdater.this;
                renderUpdater2.mLastUpdateTime = elapsedRealtime;
                if (!renderUpdater2.mPendingRender && !renderUpdater2.mPaused && !RenderUpdater.this.mSignaled) {
                    RenderUpdater renderUpdater3 = RenderUpdater.this;
                    if (renderUpdater3.mNextUpdateInterval < Long.MAX_VALUE) {
                        renderUpdater3.mHandler.postDelayed(RenderUpdater.this.mUpdater, RenderUpdater.this.mNextUpdateInterval);
                    }
                }
            }
        };
        this.mRunUpdater = new Runnable() {
            public void run() {
                RenderUpdater.this.doRunUpdater();
                boolean unused = RenderUpdater.this.mStarted = true;
            }
        };
        this.mRoot = screenElementRoot;
        this.mHandler = handler;
        this.mAutoCleanup = z;
    }

    private long checkDelay() {
        if (this.mDelay <= 0) {
            return 0;
        }
        long elapsedRealtime = SystemClock.elapsedRealtime() - this.mCreateTime;
        long j = this.mDelay;
        if (elapsedRealtime < j) {
            return j - elapsedRealtime;
        }
        return 0;
    }

    /* access modifiers changed from: private */
    public void doRunUpdater() {
        if (!this.mSignaled) {
            this.mSignaled = true;
            this.mHandler.removeCallbacks(this.mUpdater);
            this.mHandler.post(this.mUpdater);
        }
    }

    public void cleanUp() {
        this.mHandler.removeCallbacks(this.mUpdater);
        this.mPaused = true;
        this.mRoot.selfFinish();
        this.mSignaled = false;
    }

    public final void doRender() {
        this.mPendingRender = true;
        doRenderImp();
    }

    /* access modifiers changed from: protected */
    public abstract void doRenderImp();

    public void doneRender() {
        this.mPendingRender = false;
        if (!this.mPaused && !this.mSignaled) {
            long j = this.mNextUpdateInterval;
            if (j < Long.MAX_VALUE) {
                this.mHandler.postDelayed(this.mUpdater, j - (SystemClock.elapsedRealtime() - this.mLastUpdateTime));
            }
        }
    }

    /* access modifiers changed from: protected */
    public void finalize() {
        if (this.mAutoCleanup) {
            cleanUp();
        }
        super.finalize();
    }

    public void forceUpdate() {
        runUpdater();
    }

    public void init() {
        this.mPaused = false;
        this.mRoot.setRenderControllerRenderable(this);
        this.mRoot.selfInit();
    }

    public boolean isStarted() {
        return this.mStarted;
    }

    public void onPause() {
        this.mRoot.selfPause();
        this.mSignaled = false;
        this.mPaused = true;
    }

    public void onResume() {
        this.mPaused = false;
        this.mRoot.selfResume();
        runUpdater();
    }

    public void runUpdater() {
        long checkDelay = this.mStarted ? 0 : checkDelay();
        if (checkDelay <= 0) {
            doRunUpdater();
            this.mStarted = true;
        } else if (!this.mHandler.hasCallbacks(this.mRunUpdater)) {
            this.mHandler.postDelayed(this.mRunUpdater, checkDelay);
        }
    }

    public void setAutoCleanup(boolean z) {
        this.mAutoCleanup = z;
    }

    public void setStartDelay(long j, long j2) {
        this.mCreateTime = j;
        this.mDelay = j2;
        if (this.mDelay <= 0) {
            this.mStarted = true;
        }
    }

    public void triggerUpdate() {
        runUpdater();
    }
}
