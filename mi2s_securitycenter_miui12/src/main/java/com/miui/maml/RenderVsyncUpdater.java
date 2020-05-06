package com.miui.maml;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import com.miui.maml.RendererController;

public abstract class RenderVsyncUpdater implements RendererController.ISelfUpdateRenderable {
    private boolean mAutoCleanup;
    private long mCreateTime;
    private long mDelay;
    private FrameDisplayEventReceiver mDisplayEventReceiver;
    /* access modifiers changed from: private */
    public Handler mHandler;
    protected long mLastUpdateTime;
    protected long mNextUpdateInterval;
    /* access modifiers changed from: private */
    public boolean mPaused;
    protected boolean mPendingRender;
    private ScreenElementRoot mRoot;
    private Runnable mRunUpdater;
    private Runnable mScheduleFrame;
    /* access modifiers changed from: private */
    public boolean mStarted;
    /* access modifiers changed from: private */
    public boolean mStopRefresh;
    private int mSyncInterval;
    /* access modifiers changed from: private */
    public long mVsyncLeft;

    private final class FrameDisplayEventReceiver extends MamlDisplayEventReceiver implements Runnable {
        public FrameDisplayEventReceiver(Looper looper) {
            super(looper);
        }

        public void onVsync(long j) {
            RenderVsyncUpdater renderVsyncUpdater = RenderVsyncUpdater.this;
            long unused = renderVsyncUpdater.mVsyncLeft = renderVsyncUpdater.mVsyncLeft - 1;
            RenderVsyncUpdater.this.mHandler.sendMessageAtTime(Message.obtain(RenderVsyncUpdater.this.mHandler, this), j / 1000000);
        }

        public void run() {
            if (RenderVsyncUpdater.this.mVsyncLeft <= 0) {
                RenderVsyncUpdater.this.scheduleFrame();
            } else if (!RenderVsyncUpdater.this.mPaused && !RenderVsyncUpdater.this.mStopRefresh) {
                scheduleVsync();
            }
        }
    }

    public RenderVsyncUpdater(ScreenElementRoot screenElementRoot, Handler handler) {
        this(screenElementRoot, handler, false);
    }

    public RenderVsyncUpdater(ScreenElementRoot screenElementRoot, Handler handler, boolean z) {
        this.mSyncInterval = 16;
        this.mRunUpdater = new Runnable() {
            public void run() {
                RenderVsyncUpdater.this.doRunUpdater();
                boolean unused = RenderVsyncUpdater.this.mStarted = true;
            }
        };
        this.mScheduleFrame = new Runnable() {
            public void run() {
                RenderVsyncUpdater.this.scheduleFrame();
            }
        };
        this.mRoot = screenElementRoot;
        this.mHandler = handler;
        this.mAutoCleanup = z;
        this.mDisplayEventReceiver = new FrameDisplayEventReceiver(handler.getLooper());
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
        if (this.mVsyncLeft > 0) {
            this.mDisplayEventReceiver.scheduleVsync();
        } else if (!this.mHandler.hasCallbacks(this.mScheduleFrame)) {
            this.mHandler.post(this.mScheduleFrame);
        }
    }

    /* access modifiers changed from: private */
    public void scheduleFrame() {
        this.mNextUpdateInterval = this.mRoot.update(SystemClock.elapsedRealtime());
        this.mStopRefresh = this.mNextUpdateInterval == Long.MAX_VALUE;
        this.mVsyncLeft = this.mNextUpdateInterval / ((long) this.mSyncInterval);
        long j = this.mVsyncLeft;
        if (j > 0) {
            this.mVsyncLeft = j - 1;
        }
    }

    public void cleanUp() {
        this.mPaused = true;
        this.mRoot.selfFinish();
    }

    public final void doRender() {
        this.mPendingRender = true;
        doRenderImp();
    }

    /* access modifiers changed from: protected */
    public abstract void doRenderImp();

    public void doneRender() {
        this.mPendingRender = false;
    }

    /* access modifiers changed from: protected */
    public void finalize() {
        if (this.mAutoCleanup) {
            cleanUp();
        }
        super.finalize();
    }

    public void forceUpdate() {
        this.mVsyncLeft = 0;
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
        this.mPaused = true;
    }

    public void onResume() {
        this.mPaused = false;
        this.mRoot.selfResume();
        int systemFrameRate = (int) this.mRoot.getSystemFrameRate();
        if (systemFrameRate != 0) {
            this.mSyncInterval = 1000 / systemFrameRate;
        }
        if (this.mSyncInterval < 1) {
            this.mSyncInterval = 1;
        }
        runUpdater();
    }

    public void runUpdater() {
        long checkDelay = checkDelay();
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
