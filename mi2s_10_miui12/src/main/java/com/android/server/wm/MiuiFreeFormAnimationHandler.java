package com.android.server.wm;

import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.ArrayMap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.MiuiMultiWindowUtils;
import android.view.Choreographer;
import android.view.WindowManager;
import java.util.ArrayList;

class MiuiFreeFormAnimationHandler {
    private static final long FRAME_DELAY_MS = 10;
    private static final String TAG = "MiuiFreeFormAnimationHandler";
    public static final ThreadLocal<MiuiFreeFormAnimationHandler> sAnimatorHandler = new ThreadLocal<>();
    final ArrayList<AnimationFrameCallback> mAnimationCallbacks = new ArrayList<>();
    private final AnimationCallbackDispatcher mCallbackDispatcher = new AnimationCallbackDispatcher();
    long mCurrentFrameTime = 0;
    private final ArrayMap<AnimationFrameCallback, Long> mDelayedCallbackStartTime = new ArrayMap<>();
    private boolean mListDirty = false;
    private AnimationFrameCallbackProvider mProvider;

    interface AnimationFrameCallback {
        boolean doAnimationFrame(long j);
    }

    MiuiFreeFormAnimationHandler() {
    }

    class AnimationCallbackDispatcher {
        AnimationCallbackDispatcher() {
        }

        /* access modifiers changed from: package-private */
        public void dispatchAnimationFrame() {
            MiuiFreeFormAnimationHandler.this.mCurrentFrameTime = SystemClock.uptimeMillis();
            MiuiFreeFormAnimationHandler miuiFreeFormAnimationHandler = MiuiFreeFormAnimationHandler.this;
            miuiFreeFormAnimationHandler.doAnimationFrame(miuiFreeFormAnimationHandler.mCurrentFrameTime);
            if (MiuiFreeFormAnimationHandler.this.mAnimationCallbacks.size() > 0) {
                MiuiFreeFormAnimationHandler.this.getProvider().postFrameCallback();
            }
        }
    }

    public static MiuiFreeFormAnimationHandler getInstance() {
        if (sAnimatorHandler.get() == null) {
            sAnimatorHandler.set(new MiuiFreeFormAnimationHandler());
        }
        return sAnimatorHandler.get();
    }

    public static long getFrameTime() {
        if (sAnimatorHandler.get() == null) {
            return 0;
        }
        return sAnimatorHandler.get().mCurrentFrameTime;
    }

    public void setProvider(AnimationFrameCallbackProvider provider) {
        this.mProvider = provider;
    }

    /* access modifiers changed from: package-private */
    public AnimationFrameCallbackProvider getProvider() {
        if (this.mProvider == null) {
            if (Build.VERSION.SDK_INT >= 16) {
                this.mProvider = new FrameCallbackProvider16(this.mCallbackDispatcher);
            } else {
                this.mProvider = new FrameCallbackProvider14(this.mCallbackDispatcher);
            }
        }
        return this.mProvider;
    }

    public void addAnimationFrameCallback(AnimationFrameCallback callback, long delay) {
        if (this.mAnimationCallbacks.size() == 0) {
            getProvider().postFrameCallback();
        }
        if (!this.mAnimationCallbacks.contains(callback)) {
            this.mAnimationCallbacks.add(callback);
        }
        if (delay > 0) {
            this.mDelayedCallbackStartTime.put(callback, Long.valueOf(SystemClock.uptimeMillis() + delay));
        }
    }

    public void removeCallback(AnimationFrameCallback callback) {
        this.mDelayedCallbackStartTime.remove(callback);
        int id = this.mAnimationCallbacks.indexOf(callback);
        if (id >= 0) {
            this.mAnimationCallbacks.set(id, (Object) null);
            this.mListDirty = true;
        }
    }

    /* access modifiers changed from: package-private */
    public void doAnimationFrame(long frameTime) {
        long currentTime = SystemClock.uptimeMillis();
        for (int i = 0; i < this.mAnimationCallbacks.size(); i++) {
            AnimationFrameCallback callback = this.mAnimationCallbacks.get(i);
            if (callback != null && isCallbackDue(callback, currentTime)) {
                callback.doAnimationFrame(frameTime);
            }
        }
        cleanUpList();
    }

    private boolean isCallbackDue(AnimationFrameCallback callback, long currentTime) {
        Long startTime = this.mDelayedCallbackStartTime.get(callback);
        if (startTime == null) {
            return true;
        }
        if (startTime.longValue() >= currentTime) {
            return false;
        }
        this.mDelayedCallbackStartTime.remove(callback);
        return true;
    }

    private void cleanUpList() {
        if (this.mListDirty) {
            for (int i = this.mAnimationCallbacks.size() - 1; i >= 0; i--) {
                if (this.mAnimationCallbacks.get(i) == null) {
                    this.mAnimationCallbacks.remove(i);
                }
            }
            this.mListDirty = false;
        }
    }

    private static class FrameCallbackProvider16 extends AnimationFrameCallbackProvider {
        private final Choreographer mChoreographer = Choreographer.getInstance();
        private final Choreographer.FrameCallback mChoreographerCallback = new Choreographer.FrameCallback() {
            public void doFrame(long frameTimeNanos) {
                FrameCallbackProvider16.this.mDispatcher.dispatchAnimationFrame();
            }
        };

        FrameCallbackProvider16(AnimationCallbackDispatcher dispatcher) {
            super(dispatcher);
        }

        /* access modifiers changed from: package-private */
        public void postFrameCallback() {
            this.mChoreographer.postFrameCallback(this.mChoreographerCallback);
        }
    }

    private static class FrameCallbackProvider14 extends AnimationFrameCallbackProvider {
        private final Handler mHandler = new Handler(Looper.myLooper());
        long mLastFrameTime = -1;
        private final Runnable mRunnable = new Runnable() {
            public void run() {
                FrameCallbackProvider14.this.mLastFrameTime = SystemClock.uptimeMillis();
                FrameCallbackProvider14.this.mDispatcher.dispatchAnimationFrame();
            }
        };

        FrameCallbackProvider14(AnimationCallbackDispatcher dispatcher) {
            super(dispatcher);
        }

        /* access modifiers changed from: package-private */
        public void postFrameCallback() {
            this.mHandler.postDelayed(this.mRunnable, Math.max(MiuiFreeFormAnimationHandler.FRAME_DELAY_MS - (SystemClock.uptimeMillis() - this.mLastFrameTime), 0));
        }
    }

    static abstract class AnimationFrameCallbackProvider {
        final AnimationCallbackDispatcher mDispatcher;

        /* access modifiers changed from: package-private */
        public abstract void postFrameCallback();

        AnimationFrameCallbackProvider(AnimationCallbackDispatcher dispatcher) {
            this.mDispatcher = dispatcher;
        }
    }

    public static void setRequestedOrientation(int requestedOrientation, TaskRecord taskRecord) {
        int top;
        int left;
        int widthAfterScale;
        int bottom;
        int top2;
        DisplayMetrics outMetrics = new DisplayMetrics();
        ((WindowManager) taskRecord.mService.mContext.getSystemService("window")).getDefaultDisplay().getRealMetrics(outMetrics);
        Rect rect = taskRecord.getBounds();
        int heightCenter = rect.top + ((int) (((((float) rect.height()) * MiuiMultiWindowUtils.sScale) / 2.0f) + 0.5f));
        int widthCenter = rect.left + ((int) (((((float) rect.width()) * MiuiMultiWindowUtils.sScale) / 2.0f) + 0.5f));
        boolean isPortrait = outMetrics.heightPixels > outMetrics.widthPixels;
        if (MiuiMultiWindowUtils.isOrientationLandscape(requestedOrientation)) {
            if (isPortrait) {
                int widthAfterScale2 = MiuiMultiWindowUtils.LANDCAPE_FREEFORM_WIDTH;
                int heightAfterScale = MiuiMultiWindowUtils.LANDCAPE_FREEFORM_HEIGHT;
                MiuiMultiWindowUtils.sScale = (((float) MiuiMultiWindowUtils.LANDCAPE_FREEFORM_WIDTH) * 1.0f) / ((float) outMetrics.widthPixels);
                int widthOri = (int) ((((float) widthAfterScale2) / MiuiMultiWindowUtils.sScale) + 0.5f);
                int heightOri = (int) ((((float) heightAfterScale) / MiuiMultiWindowUtils.sScale) + 0.5f);
                left = widthCenter - (widthAfterScale2 / 2);
                if (left + widthAfterScale2 > outMetrics.widthPixels) {
                    left = outMetrics.widthPixels - widthAfterScale2;
                }
                if (left < 0) {
                    left = 0;
                }
                Log.d(TAG, "MiuiFreeFormAnimationHandler::setRequestedOrientation::heightAfterScale = " + heightAfterScale + "setRequestedOrientation::FREEFORM_PORTRAIT_LANDCAPE_HEIGHT = " + MiuiMultiWindowUtils.LANDCAPE_FREEFORM_HEIGHT);
                top = heightCenter - (heightAfterScale / 2);
                widthAfterScale = heightOri + top;
                bottom = left + widthOri;
            } else {
                int widthAfterScale3 = MiuiMultiWindowUtils.LANDCAPE_FREEFORM_WIDTH;
                int heightAfterScale2 = MiuiMultiWindowUtils.LANDCAPE_FREEFORM_HEIGHT;
                MiuiMultiWindowUtils.sScale = (((float) MiuiMultiWindowUtils.LANDCAPE_FREEFORM_WIDTH) * 1.0f) / ((float) outMetrics.widthPixels);
                int widthOri2 = (int) ((((float) widthAfterScale3) / MiuiMultiWindowUtils.sScale) + 0.5f);
                int heightOri2 = (int) ((((float) heightAfterScale2) / MiuiMultiWindowUtils.sScale) + 0.5f);
                int left2 = widthCenter - (widthAfterScale3 / 2);
                if (left2 + widthAfterScale3 > outMetrics.widthPixels) {
                    left2 = outMetrics.widthPixels - widthAfterScale3;
                }
                if (left2 < MiuiMultiWindowUtils.FREEFORM_TO_LEFT) {
                    left2 = MiuiMultiWindowUtils.FREEFORM_TO_LEFT;
                }
                top = heightCenter - (heightAfterScale2 / 2);
                widthAfterScale = heightOri2 + top;
                bottom = left + widthOri2;
            }
        } else if (isPortrait) {
            int widthAfterScale4 = MiuiMultiWindowUtils.FREEFORM_PORTRAIT_WIDTH;
            int heightAfterScale3 = MiuiMultiWindowUtils.FREEFORM_PORTRAIT_HEIGHT;
            MiuiMultiWindowUtils.sScale = (((float) MiuiMultiWindowUtils.FREEFORM_PORTRAIT_WIDTH) * 1.0f) / ((float) outMetrics.widthPixels);
            int widthOri3 = (int) ((((float) widthAfterScale4) / MiuiMultiWindowUtils.sScale) + 0.5f);
            int heightOri3 = (int) ((((float) heightAfterScale3) / MiuiMultiWindowUtils.sScale) + 0.5f);
            int top3 = heightCenter - (heightAfterScale3 / 2);
            if (top3 + heightAfterScale3 > outMetrics.heightPixels) {
                top3 = outMetrics.heightPixels - heightAfterScale3;
            }
            if (top3 < MiuiMultiWindowUtils.TOP_DECOR_CAPTIONVIEW_HEIGHT) {
                top = MiuiMultiWindowUtils.TOP_DECOR_CAPTIONVIEW_HEIGHT;
            } else {
                top = top3;
            }
            int top4 = top + heightOri3;
            int left3 = widthCenter - (widthAfterScale4 / 2);
            bottom = left3 + widthOri3;
            widthAfterScale = top4;
            left = left3;
        } else {
            int widthAfterScale5 = MiuiMultiWindowUtils.FREEFORM_LANDCAPE_WIDTH;
            int heightAfterScale4 = MiuiMultiWindowUtils.FREEFORM_LANDCAPE_HEIGHT;
            MiuiMultiWindowUtils.sScale = (((float) MiuiMultiWindowUtils.FREEFORM_LANDCAPE_WIDTH) * 1.0f) / ((float) outMetrics.widthPixels);
            int widthOri4 = (int) ((((float) widthAfterScale5) / MiuiMultiWindowUtils.sScale) + 0.5f);
            int heightOri4 = (int) ((((float) heightAfterScale4) / MiuiMultiWindowUtils.sScale) + 0.5f);
            int top5 = heightCenter - (heightAfterScale4 / 2);
            if (top5 < 0) {
                top2 = 0;
            } else {
                top2 = top5;
            }
            int top6 = top + heightOri4;
            int left4 = widthCenter - (widthAfterScale5 / 2);
            bottom = left4 + widthOri4;
            widthAfterScale = top6;
            left = left4;
        }
        Log.d(TAG, "MiuiFreeFormAnimationHandler::setRequestedOrientation::rect = " + new Rect(left, top, bottom, widthAfterScale));
        taskRecord.requestResize(new Rect(left, top, bottom, widthAfterScale), 2);
    }
}
